package com.volvoxmobile.volvoxhub

import android.app.Application
import android.content.Context
import android.os.Build
import androidx.room.Room
import com.appsflyer.AppsFlyerLib
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitString
import com.github.michaelbull.result.get
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.onesignal.OneSignal
import com.scottyab.rootbeer.RootBeer
import com.volvoxmobile.volvoxhub.common.amplitude.AmplitudeManager
import com.volvoxmobile.volvoxhub.common.extensions.deviceType
import com.volvoxmobile.volvoxhub.common.extensions.getAdvertisingId
import com.volvoxmobile.volvoxhub.common.extensions.getScreenDpi
import com.volvoxmobile.volvoxhub.common.extensions.getScreenResolution
import com.volvoxmobile.volvoxhub.common.extensions.getUserRegion
import com.volvoxmobile.volvoxhub.common.util.DeviceUuidFactory
import com.volvoxmobile.volvoxhub.common.util.Localizations
import com.volvoxmobile.volvoxhub.common.util.VolvoxHubLogLevel
import com.volvoxmobile.volvoxhub.common.util.tryOrLog
import com.volvoxmobile.volvoxhub.data.local.model.VolvoxHubResponse
import com.volvoxmobile.volvoxhub.data.local.model.db.LocalizationEntity
import com.volvoxmobile.volvoxhub.data.remote.api.hub.HubApiHeaderInterceptor
import com.volvoxmobile.volvoxhub.data.remote.api.hub.HubApiService
import com.volvoxmobile.volvoxhub.data.remote.model.hub.request.RegisterRequest
import com.volvoxmobile.volvoxhub.data.remote.model.hub.response.RegisterBaseResponse
import com.volvoxmobile.volvoxhub.data.remote.model.hub.response.RegisterConfigResponse
import com.volvoxmobile.volvoxhub.db.AppDatabase
import com.volvoxmobile.volvoxhub.domain.local.localization.LocalizationRepositoryImpl
import com.volvoxmobile.volvoxhub.domain.local.preferences.PreferencesRepositoryImpl
import com.volvoxmobile.volvoxhub.domain.remote.hub.HubApiRepository
import com.volvoxmobile.volvoxhub.domain.remote.hub.HubApiRepositoryImpl
import com.volvoxmobile.volvoxhub.strings.ConfigureStrings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Locale
import java.util.concurrent.TimeUnit

internal class VolvoxHubService {
    /**
     * Singleton instance of the service
     */
    companion object {
        val instance: VolvoxHubService by lazy { VolvoxHubService() }
    }

    /**
     * Coroutine scope for the service requests
     */
    private val scope = CoroutineScope(Dispatchers.IO + Job())

    /**
     * Sdk configuration
     */
    private lateinit var configuration: Configuration

    /**
     * Shared preferences for the sdk
     */
    private val sharedPreferences by lazy {
        configuration.context.getSharedPreferences("volvox_hub_prefs", Context.MODE_PRIVATE)
    }

    /**
     * Preferences repository for the sdk
     */
    private val preferencesRepository by lazy {
        PreferencesRepositoryImpl(sharedPreferences)
    }

    /**
     * Room database for the sdk
     */
    private val roomDb by lazy {
        Room
            .databaseBuilder(
                configuration.context,
                AppDatabase::class.java,
                "hub.db",
            ).build()
    }

    /**
     * Localization entity dao for the sdk
     */
    private val localizationEntityDao by lazy {
        roomDb.localizationEntityDao()
    }

    /**
     * Localization repository for the sdk
     */
    private val localizationRepository by lazy {
        LocalizationRepositoryImpl(localizationEntityDao)
    }

    /**
     * Interceptor to add headers (like auth tokens) to API requests
     */
    private val hubApiHeaderInterceptor: HubApiHeaderInterceptor by lazy {
        HubApiHeaderInterceptor(context = configuration.context, appId = configuration.appId)
    }

    /**
     * OkHttp client for the API requests
     */
    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient
            .Builder()
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(hubApiHeaderInterceptor)
            .build()
    }

    /**
     * Retrofit instance for the API requests
     */
    private val retrofit: Retrofit by lazy {
        Retrofit
            .Builder()
            .baseUrl(BaseUrlDecider.getApiBaseUrl())
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(Gson()))
            .build()
    }

    /**
     * Hub API service
     */
    private val hubApiService: HubApiService by lazy {
        retrofit.create(HubApiService::class.java)
    }

    /**
     * Hub API repository
     */
    private val hubApiRepository: HubApiRepository by lazy {
        HubApiRepositoryImpl(hubApiService)
    }

    /**
     * Initialize the service with the configuration and Synchronized for thread safety
     * @param configuration sdk configuration includes context and other necessary data
     */
    @Synchronized
    fun initialize(configuration: Configuration) {
        this.configuration = configuration
        initializeFirebase()
    }

    /**
     * Create a register request for the hub
     */
    private fun createRegisterRequest(): RegisterRequest {
        val context = configuration.context
        val locale = Locale.getDefault()

        return RegisterRequest(
            deviceType = context.deviceType(),
            deviceBrand = Build.BRAND,
            deviceModel = Build.MODEL,
            countryCode = locale.country,
            languageCode = locale.language,
            idfa = context.getAdvertisingId().orEmpty(),
            appsflyerId = preferencesRepository.getAppsFlyerUserId(),
            firebaseId = preferencesRepository.getFirebaseId(),
            opRegion = context.getUserRegion().orEmpty(),
            carrierRegion = context.getUserRegion().orEmpty(),
            appName = configuration.appName,
            os = Build.VERSION.RELEASE,
            resolution = context.getScreenResolution(),
            dpi = context.getScreenDpi(),
            oneSignalToken = preferencesRepository.getPushToken(),
            oneSignalPlayerId = preferencesRepository.getOneSignalPlayerId(),
            filePath = configuration.context.filesDir.absolutePath,
            appVersion = context.packageManager.getPackageInfo(context.packageName, 0).versionName,
        )
    }

    /**
     * Start the service with the hub init listener, It makes a register request to the hub
     * and calls the listener on success or failure
     * @param hubInitListener listener for the hub init
     */
    fun start(hubInitListener: VolvoxHubInitListener) {
        scope.launch {
            val registerRequest = createRegisterRequest()
            hubApiRepository.register(registerRequest).get()?.let {
                handleInitializationSuccess(it, hubInitListener)
                handleInitializeSdksError(it)
                updateRegisterRequest()
            } ?: run {
                hubInitListener.onInitFailed(0)
            }
        }
    }

    /**
     * Resends the registration request after initializing SDKs with the response from the first request,
     * and includes the SDK IDs in the updated request.
     */
    private fun updateRegisterRequest() {
        scope.launch {
            val isInitialized = preferencesRepository.initializeSucceeded()
            if (isInitialized.not()) {
                preferencesRepository.setInitializeSucceeded(true)
                val registerRequest = createRegisterRequest()
                hubApiRepository.register(registerRequest)
            }
        }
    }

    /**
     * Handle the initialization success response from the hub
     * @param response register response from the hub
     * @param hubInitListener listener for the hub init
     */
    private suspend fun handleInitializationSuccess(
        response: RegisterBaseResponse,
        hubInitListener: VolvoxHubInitListener,
    ) {
        val volvoxHubResponse =
            VolvoxHubResponse(
                banned = response.device.banStatus,
                premiumStatus = response.device.premiumStatus,
                forceUpdate = response.config.forceUpdate,
                isRooted = rootCheck(),
                remoteConfig = response.remoteConfig
            )

        initializeFirebase()
        initializeFacebook(response.thirdParty.facebookAppId.orEmpty(), response.thirdParty.facebookClientToken.orEmpty(), configuration.appName)
        initAppsflyerSdk(response.thirdParty.appsflyerDevKey.orEmpty())
        handleLocalizations(response.config.localizationUrl)
        initOneSignalSDK(response.thirdParty.oneSignalAppId.orEmpty())
        initAmplitudeSdk(response.thirdParty.amplitudeApiKey.orEmpty())
        initializeRcBillingHelper(response.thirdParty.revenuecatId.orEmpty())
        saveConfigUrls(response.config)

        hubInitListener.onInitCompleted(volvoxHubResponse)
    }

    /**
     * Handle the initialization error response from the hub
     * @param response register response from the hub
     */
    private fun handleInitializeSdksError(response: RegisterBaseResponse) {
        val thirdPartyResponse = response.thirdParty

        fun logIfEmpty(
            value: String,
            logMessage: String,
        ) {
            if (value.isEmpty()) {
                VolvoxHubLogManager.log(logMessage, VolvoxHubLogLevel.ERROR)
            }
        }

        logIfEmpty(thirdPartyResponse.oneSignalAppId.orEmpty(), ConfigureStrings.ONE_SIGNAL_APP_ID_EMPTY)
        logIfEmpty(thirdPartyResponse.revenuecatId.orEmpty(), ConfigureStrings.REVENUECAT_ID_EMPTY)
        logIfEmpty(thirdPartyResponse.appsflyerDevKey.orEmpty(), ConfigureStrings.APPSFLYER_DEV_KEY_EMPTY)
        logIfEmpty(thirdPartyResponse.amplitudeApiKey.orEmpty(), ConfigureStrings.AMPLITUDE_API_KEY_EMPTY)
        logIfEmpty(thirdPartyResponse.appsflyerAppId.orEmpty(), ConfigureStrings.APPSFLYER_APP_ID_EMPTY)
    }

    /**
     * Initialize the Facebook SDK
     */
    private fun initializeFacebook(appId: String, clientToken: String, applicationName: String) {
        if (appId.isEmpty()) return
        FacebookSdk.setApplicationId(appId)
        FacebookSdk.setClientToken(clientToken)
        FacebookSdk.setApplicationName(applicationName)
        FacebookSdk.sdkInitialize(configuration.context.applicationContext)
        AppEventsLogger.activateApp(configuration.context.applicationContext as Application)
    }

    /**
     * Initialize the RevenueCat billing helper
     */
    private fun initializeRcBillingHelper(rcKey: String) {
        if (rcKey.isEmpty()) return
        VolvoxHub.getInstance().rcBillingHelper.init(
            context = configuration.context,
            rcKey = rcKey,
            uuid = DeviceUuidFactory.create(configuration.context),
        )
    }

    /**
     * Initialize the OneSignal SDK
     */
    private fun initOneSignalSDK(oneSignalAppId: String) {
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.NONE, OneSignal.LOG_LEVEL.NONE)
        OneSignal.initWithContext(configuration.context)
        OneSignal.setAppId(oneSignalAppId)
        val pushToken = OneSignal.getDeviceState()?.pushToken ?: ""
        val playerId = OneSignal.getDeviceState()?.userId ?: ""
        checkRequestChanges()
        preferencesRepository.savePushToken(pushToken)
        preferencesRepository.saveOneSignalPlayerId(playerId)
    }

    /**
     * Initialize the AppsFlyer SDK
     */
    private fun initAppsflyerSdk(appsflyerDevKey: String) {
        AppsFlyerLib.getInstance().init(appsflyerDevKey, null, configuration.context)
        AppsFlyerLib.getInstance().setAppId(configuration.packageName)
        AppsFlyerLib.getInstance().setCustomerUserId(DeviceUuidFactory.create(configuration.context))
        AppsFlyerLib.getInstance().waitForCustomerUserId(true)
        AppsFlyerLib.getInstance().start(configuration.context)
        checkRequestChanges()
        preferencesRepository.setAppsFlyerUserId(AppsFlyerLib.getInstance().getAppsFlyerUID(configuration.context).orEmpty())
    }

    /**
     * Handle the localization response from the hub and save it to the room database
     * @param localizationUrl localization url from the hub
     */
    private suspend fun handleLocalizations(localizationUrl: String) {
        tryOrLog {
            val savedLocalization = localizationRepository.get()
            val needLocalizationFetch = savedLocalization == null || savedLocalization.localizationUrl != localizationUrl
            if (needLocalizationFetch) {
                val localizationResponse = Fuel.get(localizationUrl).awaitString()
                val localizations: Map<String, String> =
                    Gson().fromJson(
                        localizationResponse,
                        object : TypeToken<Map<String, String>>() {}.type,
                    )
                val localizationEntity =
                    LocalizationEntity(
                        id = LocalizationEntity.DEFAULT_LOCALIZATION_ID,
                        localizationUrl = localizationUrl,
                        localizations = localizations,
                    )
                localizationRepository.save(localizationEntity = localizationEntity)
                Localizations.set(localizationsMap = localizations)
            } else {
                Localizations.set(localizationsMap = savedLocalization!!.localizations)
            }
        }
    }

    /**
     * Save the config URLs to the shared preferences
     */
    private fun saveConfigUrls(config: RegisterConfigResponse) {
        preferencesRepository.setPrivacyPolicyUrl(config.privacyPolicyUrl)
        preferencesRepository.setTermsOfServiceUrl(config.eula)
    }

    /**
     * Initialize the Amplitude SDK
     */
    private fun initAmplitudeSdk(apiKey: String) {
        tryOrLog {
            AmplitudeManager.initialize(configuration.context, apiKey = apiKey)
        }
    }

    /**
     * Initialize the Firebase SDK
     * Save the Firebase ID to the shared preferences
     */
    private fun initializeFirebase() {
        FirebaseApp.initializeApp(configuration.context)
        FirebaseAnalytics.getInstance(configuration.context).setAnalyticsCollectionEnabled(true)
        FirebaseAnalytics.getInstance(configuration.context).appInstanceId.addOnSuccessListener {
            tryOrLog {
                preferencesRepository.saveFirebaseId(it.orEmpty())
            }
        }
    }

    /**
     * Check if the device is rooted and return the result
     */
    private fun rootCheck(): Boolean {
        val rootCheck = RootBeer(configuration.context)
        return rootCheck.isRooted
    }

    /**
     * Check if the request values have changed and update the register request
     * if there are changes in the values like push token, OneSignal player id, or advertising id (AppsFlyer) values have changed since the last request to the hub
     * Send a new register request to the hub with the updated values
     */
    private fun checkRequestChanges() {
        val currentValues =
            mapOf(
                "PushToken" to preferencesRepository.getPushToken(),
                "OneSignalPlayerId" to preferencesRepository.getOneSignalPlayerId(),
                "AdvertisingId" to preferencesRepository.getAppsFlyerUserId(),
            )

        val newValues =
            mapOf(
                "PushToken" to (OneSignal.getDeviceState()?.pushToken.orEmpty()),
                "OneSignalPlayerId" to (OneSignal.getDeviceState()?.userId.orEmpty()),
                "AdvertisingId" to AppsFlyerLib.getInstance().getAppsFlyerUID(configuration.context).orEmpty(),
            )

        val hasChanges =
            currentValues.any { (key, currentValue) ->
                newValues[key] != currentValue
            }

        if (hasChanges) {
            preferencesRepository.setInitializeSucceeded(false)
            updateRegisterRequest()
        }
    }

    /**
     * Get the privacy policy url
     */
    fun getPrivacyPolicyUrl(): String = preferencesRepository.getPrivacyPolicyUrl()

    /**
     * Get the terms of service url
     */
    fun getTermsOfServiceUrl(): String = preferencesRepository.getTermsOfServiceUrl()
}

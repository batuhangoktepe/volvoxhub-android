package com.volvoxmobile.volvoxhub

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.room.Room
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitString
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.get
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.Gson
import com.google.gson.JsonObject
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
import com.volvoxmobile.volvoxhub.common.util.StringUtils
import com.volvoxmobile.volvoxhub.common.util.VolvoxHubLogLevel
import com.volvoxmobile.volvoxhub.common.util.tryOrLog
import com.volvoxmobile.volvoxhub.data.local.model.VolvoxHubResponse
import com.volvoxmobile.volvoxhub.data.local.model.db.LocalizationEntity
import com.volvoxmobile.volvoxhub.data.remote.api.hub.HubApiHeaderInterceptor
import com.volvoxmobile.volvoxhub.data.remote.api.hub.HubApiService
import com.volvoxmobile.volvoxhub.data.remote.model.hub.request.InstalledAppsRequest
import com.volvoxmobile.volvoxhub.data.remote.model.hub.request.MessageTicketRequest
import com.volvoxmobile.volvoxhub.data.remote.model.hub.request.NewTicketRequest
import com.volvoxmobile.volvoxhub.data.remote.model.hub.request.PromoCodeRequest
import com.volvoxmobile.volvoxhub.data.remote.model.hub.request.RegisterRequest
import com.volvoxmobile.volvoxhub.data.remote.model.hub.response.ClaimRewardResponse
import com.volvoxmobile.volvoxhub.data.remote.model.hub.response.CreateNewTicketResponse
import com.volvoxmobile.volvoxhub.data.remote.model.hub.response.PromoCodeResponse
import com.volvoxmobile.volvoxhub.data.remote.model.hub.response.RegisterBaseResponse
import com.volvoxmobile.volvoxhub.data.remote.model.hub.response.RegisterConfigResponse
import com.volvoxmobile.volvoxhub.data.remote.model.hub.response.RewardStatusResponse
import com.volvoxmobile.volvoxhub.data.remote.model.hub.response.SupportTicketResponse
import com.volvoxmobile.volvoxhub.data.remote.model.hub.response.SupportTicketsResponse
import com.volvoxmobile.volvoxhub.data.remote.model.hub.response.UnseenStatusResponse
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
        HubApiHeaderInterceptor(
            context = configuration.context,
            appId = configuration.appId,
            appName = configuration.appName,
            vIdProvider = { preferencesRepository.getVID() }
        )
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
            .baseUrl(BaseUrlDecider.getApiBaseUrl(configuration.environment))
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
            languageCode = configuration.languageCode,
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
            installedAppsRequest = getInstalledApps(context)
        )
    }

    /**
     * Create Installed Apps Response
     */
    private fun getInstalledApps(context: Context): InstalledAppsRequest {
        val packageManager = context.packageManager

        fun isAppInstalled(packageName: String): Boolean {
            return try {
                packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
                true
            } catch (e: PackageManager.NameNotFoundException) {
                false
            }
        }

        return InstalledAppsRequest(
            instagram = isAppInstalled("com.instagram.android"),
            facebook = isAppInstalled("com.facebook.katana"),
            tiktok = isAppInstalled("com.zhiliaoapp.musically"),
            snapchat = isAppInstalled("com.snapchat.android")
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
     * Updates the application's localization by setting the language code and making a registration request.
     *
     * @param languageCode The new language code to set.
     * @param onComplete Callback to be invoked when the operation is completed successfully.
     */
    fun updateLocalizations(languageCode: String, onComplete: () -> Unit) {
        scope.launch {
            configuration.languageCode = languageCode
            val registerRequest = createRegisterRequest()
            val result = hubApiRepository.register(registerRequest).get()
            if (result != null) {
                handleLocalizations(result.config.localizationUrl)
                onComplete()
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
                remoteConfig = response.remoteConfig,
                vId = response.vid
            )

        saveVID(response.vid)
        saveSupportedLanguages(response.config.supportedLanguages)
        saveSupportEmail(response.config.supportEmail.orEmpty())
        initializeFirebase()
        initializeFacebook(
            response.thirdParty.facebookAppId.orEmpty(),
            response.thirdParty.facebookClientToken.orEmpty(),
            configuration.appName
        )
        initAppsflyerSdk(response.thirdParty.appsflyerDevKey.orEmpty())
        handleLocalizations(response.config.localizationUrl)
        initOneSignalSDK(response.thirdParty.oneSignalAppId.orEmpty())
        initAmplitudeSdk(
            apiKey = response.thirdParty.amplitudeApiKey.orEmpty(),
            experimentKey = response.thirdParty.amplitudeExperimentKey.orEmpty()
        )
        initializeRcBillingHelper(response.thirdParty.revenuecatId.orEmpty(), response.vid)
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

        logIfEmpty(
            thirdPartyResponse.oneSignalAppId.orEmpty(),
            ConfigureStrings.ONE_SIGNAL_APP_ID_EMPTY
        )
        logIfEmpty(thirdPartyResponse.revenuecatId.orEmpty(), ConfigureStrings.REVENUECAT_ID_EMPTY)
        logIfEmpty(
            thirdPartyResponse.appsflyerDevKey.orEmpty(),
            ConfigureStrings.APPSFLYER_DEV_KEY_EMPTY
        )
        logIfEmpty(
            thirdPartyResponse.amplitudeApiKey.orEmpty(),
            ConfigureStrings.AMPLITUDE_API_KEY_EMPTY
        )
        logIfEmpty(
            thirdPartyResponse.appsflyerAppId.orEmpty(),
            ConfigureStrings.APPSFLYER_APP_ID_EMPTY
        )
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
    private fun initializeRcBillingHelper(rcKey: String, vId: String) {
        if (rcKey.isEmpty()) return
        VolvoxHub.getInstance().rcBillingHelper.init(
            context = configuration.context,
            rcKey = rcKey,
            uuid = vId,
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
        AppsFlyerLib.getInstance().init(appsflyerDevKey, object : AppsFlyerConversionListener {
            override fun onConversionDataSuccess(conversionData: MutableMap<String, Any>?) {
                scope.launch {
                    conversionData?.let { data ->
                        val jsonObject = JsonObject()
                        // Iterate through all conversion data and add to JsonObject
                        data.forEach { (key, value) ->
                            when (value) {
                                is String -> jsonObject.addProperty(key, value)
                                is Number -> jsonObject.addProperty(key, value)
                                is Boolean -> jsonObject.addProperty(key, value)
                                else -> jsonObject.addProperty(key, value.toString())
                            }
                        }
                        // Send conversion data to backend
                        hubApiRepository.updateConversion(jsonObject)
                    }
                }
            }

            override fun onConversionDataFail(error: String?) {
                VolvoxHubLogManager.log(
                    message = "AppsFlyer Conversion Data Failed: $error",
                    level = VolvoxHubLogLevel.ERROR
                )
            }

            override fun onAppOpenAttribution(attributionData: MutableMap<String, String>?) {
                // Not needed for this implementation
            }

            override fun onAttributionFailure(error: String?) {
                // Not needed for this implementation
            }
        }, configuration.context)
        AppsFlyerLib.getInstance().setAppId(configuration.packageName)
        AppsFlyerLib.getInstance().setCustomerUserId(
            DeviceUuidFactory.create(
                configuration.context,
                configuration.appName
            )
        )
        AppsFlyerLib.getInstance().waitForCustomerUserId(true)
        AppsFlyerLib.getInstance().start(configuration.context)
        triggerForegroundManually(configuration.context)

        checkRequestChanges()
        preferencesRepository.setAppsFlyerUserId(
            AppsFlyerLib.getInstance().getAppsFlyerUID(configuration.context).orEmpty()
        )
    }

    // TODO refactor
    private fun triggerForegroundManually(context: Context) {
        val intent = Intent(context, DummyActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    /**
     * Handle the localization response from the hub and save it to the room database
     * @param localizationUrl localization url from the hub
     */
    private suspend fun handleLocalizations(localizationUrl: String) {
        tryOrLog {
            val savedLocalization = localizationRepository.get()
            val needLocalizationFetch =
                savedLocalization == null || savedLocalization.localizationUrl != localizationUrl
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
     * Save Volvox Hub Id to the shared preferences
     */
    private fun saveVID(vId: String) {
        preferencesRepository.saveVID(vId)
    }

    /**
     * Save Supported Languages to the shared preferences
     */
    private fun saveSupportedLanguages(languages: List<String>?) {
        preferencesRepository.saveSupportedLanguages(languages)
    }

    private fun saveSupportEmail(supportEmail: String) {
        preferencesRepository.saveSupportEmail(supportEmail)
    }

    /**
     * Initialize the Amplitude SDK
     */
    private fun initAmplitudeSdk(apiKey: String, experimentKey: String = StringUtils.EMPTY) {
        tryOrLog {
            AmplitudeManager.initialize(
                configuration.context,
                apiKey = apiKey,
                experimentKey = experimentKey,
                appName = configuration.appName
            )
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
                "AdvertisingId" to AppsFlyerLib.getInstance().getAppsFlyerUID(configuration.context)
                    .orEmpty(),
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

    /**
     * Get the contact us email
     */
    fun getSupportEmail(): String? = preferencesRepository.getSupportEmail()

    /**
     * Get the supported languages list
     */
    fun getSupportedLanguages(): List<String> = preferencesRepository.getSupportedLanguages()


    /**
     * Send Claim Reward Request
     * @param onComplete Callback to be invoked when the operation is completed successfully.
     */
    fun claimReward(onComplete: (ClaimRewardResponse) -> Unit, onError: (String) -> Unit) {
        scope.launch {
            when (val result = hubApiRepository.claimReward()) {
                is Ok -> onComplete(result.value)
                is Err -> onError(result.error.message.orEmpty())
            }
        }
    }

    /**
     * Asynchronously fetches the reward status from the repository and provides the result
     * via a callback function.
     *
     * This function uses a coroutine to perform a network request or database operation
     * in a non-blocking manner. Once the operation is complete, it calls the provided
     * callback with the result if it's not null.
     *
     * @param onComplete A lambda function that receives the result of the reward status
     *                   operation as a `RewardStatusResponse`.
     */
    fun rewardStatus(onComplete: (RewardStatusResponse) -> Unit) {
        scope.launch {
            hubApiRepository.rewardStatus().get()?.let {
                onComplete(it)
            }
        }
    }

    /**
     * Attempts to use a promo code.
     * Calls `successCallback` on success with `PromoCodeResponse`,
     * otherwise calls `errorCallback`.
     */
    fun usePromoCode(
        code: String,
        errorCallback: (String?) -> Unit,
        successCallback: (PromoCodeResponse) -> Unit
    ) {
        scope.launch {
            val promoCodeRequest = PromoCodeRequest(code)
            when (val result = hubApiRepository.usePromoCode(promoCodeRequest)) {
                is Ok -> successCallback.invoke(result.value)
                is Err -> errorCallback.invoke(result.error.message)
            }
        }
    }

    fun getTickets(
        errorCallback: (String?) -> Unit,
        successCallback: (SupportTicketsResponse) -> Unit
    ) {
        scope.launch {
            when (val result = hubApiRepository.getTickets()) {
                is Ok -> successCallback(result.value)
                is Err -> {
                    errorCallback.invoke(result.error.message)
                    Log.d("error message", result.error.message.toString())
                }
            }
        }
    }

    fun getTicket(
        ticketId: String,
        errorCallback: (String?) -> Unit,
        successCallback: (SupportTicketResponse) -> Unit
    ) {
        scope.launch {
            when (val result = hubApiRepository.getTicket(ticketId)) {
                is Ok -> successCallback(result.value)
                is Err -> errorCallback(result.error.message)
            }
        }
    }

    fun createNewMessage(
        ticketId: String,
        message: String,
        errorCallback: (String?) -> Unit,
        successCallback: () -> Unit
    ) {
        scope.launch {
            val messageNewTicketRequest = MessageTicketRequest(message)
            when (val result =
                hubApiRepository.createNewMessage(ticketId, messageNewTicketRequest)) {
                is Ok -> successCallback()
                is Err -> errorCallback(result.error.message)
            }
        }
    }

    fun createNewTicket(
        category: String,
        message: String,
        errorCallback: (String?) -> Unit,
        successCallback: (CreateNewTicketResponse) -> Unit
    ) {
        scope.launch {
            val newTicketRequest = NewTicketRequest(category, message)
            when (val result = hubApiRepository.createNewTicket(newTicketRequest)) {
                is Ok -> successCallback(result.value)
                is Err -> errorCallback(result.error.message)
            }
        }
    }

    fun getUnseenStatus(
        errorCallback: (String?) -> Unit,
        successCallback: (UnseenStatusResponse) -> Unit
    ) {
        scope.launch {
            when (val result = hubApiRepository.getUnseenStatus()) {
                is Ok -> successCallback(result.value)
                is Err -> errorCallback(result.error.message)
            }
        }
    }
}

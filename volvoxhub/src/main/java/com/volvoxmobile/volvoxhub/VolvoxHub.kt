package com.volvoxmobile.volvoxhub

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.Manifest
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.revenuecat.purchases.CustomerInfo
import com.revenuecat.purchases.PurchasesError
import com.revenuecat.purchases.models.StoreProduct
import com.revenuecat.purchases.models.StoreTransaction
import com.revenuecat.purchases.models.googleProduct
import com.revenuecat.purchases.ui.revenuecatui.ExperimentalPreviewRevenueCatUIPurchasesAPI
import com.revenuecat.purchases.ui.revenuecatui.PaywallDialog
import com.revenuecat.purchases.ui.revenuecatui.PaywallDialogOptions
import com.revenuecat.purchases.ui.revenuecatui.PaywallListener
import com.volvoxmobile.volvoxhub.billing.RcBillingHelper
import com.volvoxmobile.volvoxhub.common.sign_in.GoogleSignIn
import com.volvoxmobile.volvoxhub.common.sign_in.GoogleSignInCallback
import com.volvoxmobile.volvoxhub.common.sign_in.GoogleSignInConfig
import com.volvoxmobile.volvoxhub.common.util.Localizations
import com.volvoxmobile.volvoxhub.common.util.NotificationPermissionStatus
import com.volvoxmobile.volvoxhub.common.util.VolvoxHubLogLevel
import com.volvoxmobile.volvoxhub.data.remote.model.hub.request.SocialLoginRequest
import com.volvoxmobile.volvoxhub.data.remote.model.hub.response.ClaimRewardResponse
import com.volvoxmobile.volvoxhub.data.remote.model.hub.response.DeleteAccountResponse
import com.volvoxmobile.volvoxhub.data.remote.model.hub.response.GetProductsResponse
import com.volvoxmobile.volvoxhub.data.remote.model.hub.response.PromoCodeResponse
import com.volvoxmobile.volvoxhub.data.remote.model.hub.response.RegisterBaseResponse
import com.volvoxmobile.volvoxhub.data.remote.model.hub.response.RewardStatusResponse
import com.volvoxmobile.volvoxhub.strings.ConfigureStrings
import com.volvoxmobile.volvoxhub.ui.ban.BannedPopup
import com.volvoxmobile.volvoxhub.ui.ban.BannedPopupConfig
import com.volvoxmobile.volvoxhub.ui.login.GoogleSignInButton
import com.volvoxmobile.volvoxhub.ui.web.WebScreen


class VolvoxHub private constructor(
    configuration: Configuration,
) {
    /**
     * VolvoxHubService instance to handle the hub operations
     */
    private var volvoxHubService: VolvoxHubService = VolvoxHubService.instance

    /**
     * Revenuecat Billing Helper
     */
    internal val rcBillingHelper by lazy {
        RcBillingHelper()
    }

    init {
        volvoxHubService.initialize(configuration)
    }


    companion object {
        @Volatile
        private var instance: VolvoxHub? = null

        // Map to store notification permission callbacks
        private val notificationPermissionCallbacks = mutableMapOf<Int, (Boolean) -> Unit>()

        // Default request code for notification permission
        private const val DEFAULT_NOTIFICATION_PERMISSION_REQUEST_CODE = 1001

        /**
         * Initialize the VolvoxHub
         * @param configuration Configuration class for the hub
         */
        fun initialize(configuration: Configuration) {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) {
                        instance = VolvoxHub(configuration)
                    }
                }
            }
        }

        fun getInstance(): VolvoxHub = instance
            ?: throw IllegalStateException("VolvoxHub is not initialized. Call initialize() first.")

        @Composable
        fun ShowBannedPopup(
            isVisible: Boolean,
            config: BannedPopupConfig,
        ) {
            BannedPopup(isVisible = isVisible, config = config)
        }

        @Composable
        fun ShowCustomWebScreen(
            url: String,
            title: String,
            onClose: () -> Unit,
            backgroundColor: Color = MaterialTheme.colorScheme.primary,
            contentColor: Color = Color.White,
        ) {
            WebScreen(
                url = url,
                title = title,
                onClose = onClose,
                backgroundColor = backgroundColor,
                contentColor = contentColor
            )
        }

        @Composable
        fun ShowPrivacyPolicyScreen(
            context: Context,
            backgroundColor: Color = MaterialTheme.colorScheme.primary,
            contentColor: Color = Color.White,
            onClose: () -> Unit
        ) {
            val privacyPolicyUrl = VolvoxHubService.instance.getPrivacyPolicyUrl()
            WebScreen(
                url = privacyPolicyUrl,
                title = Localizations.get(context, "Privacy Policy"),
                onClose = onClose,
                backgroundColor = backgroundColor,
                contentColor = contentColor
            )
        }

        @Composable
        fun ShowTermsOfServiceScreen(
            context: Context,
            backgroundColor: Color = MaterialTheme.colorScheme.primary,
            contentColor: Color = Color.White,
            onClose: () -> Unit
        ) {
            val termsOfServiceUrl = VolvoxHubService.instance.getTermsOfServiceUrl()
            WebScreen(
                url = termsOfServiceUrl,
                title = Localizations.get(context, "Terms of Service"),
                onClose = onClose,
                backgroundColor = backgroundColor,
                contentColor = contentColor
            )
        }

        @OptIn(ExperimentalPreviewRevenueCatUIPurchasesAPI::class)
        @Composable
        fun PaywallScreen(entitlementId: String) {
            PaywallDialog(
                PaywallDialogOptions.Builder()
                    .setRequiredEntitlementIdentifier(entitlementId)
                    .setListener(
                        object : PaywallListener {
                            override fun onPurchaseCompleted(
                                customerInfo: CustomerInfo,
                                storeTransaction: StoreTransaction
                            ) {
                            }

                            override fun onRestoreCompleted(customerInfo: CustomerInfo) {}
                        }
                    )
                    .build()
            )

        }

        @Composable
        fun ShowLogInWithGoogle(
            modifier: Modifier,
            context: Context,
            successCallback: (RegisterBaseResponse) -> Unit,
            errorCallback: (String?) -> Unit,
            firebaseWebClientId: String
        ) {
            GoogleSignIn.initialize(
                config = GoogleSignInConfig(
                    context = context,
                    serverClientId = firebaseWebClientId
                )
            )
            GoogleSignIn.getInstance().setCallback(
                callback = object : GoogleSignInCallback {
                    override fun onSignInSuccess(socialLoginRequest: SocialLoginRequest) {
                        VolvoxHubService.instance.socialLogin(
                            socialLoginRequest = socialLoginRequest,
                            errorCallback = errorCallback,
                            successCallback = successCallback
                        )
                    }

                    override fun onSignInError(exception: Exception) {
                        errorCallback(exception.message)
                    }
                }
            )
            GoogleSignInButton(
                modifier = modifier
            ) {
                GoogleSignIn.getInstance().signIn()
            }
        }

    }

    /**
     * Start the authorization process
     * @param hubInitListener listener for the hub init process
     * @see VolvoxHubInitListener
     */
    fun startAuthorization(hubInitListener: VolvoxHubInitListener) {
        VolvoxHubLogManager.log(ConfigureStrings.LISTENER_SET, VolvoxHubLogLevel.INFO)
        volvoxHubService.start(hubInitListener)
    }

    /**
     * Fetch consumable products from the RevenueCat
     */
    fun fetchConsumableProducts(productsCallBack: (List<StoreProduct>) -> Unit) {
        rcBillingHelper.getConsumableSkuDetails(productsCallBack)
    }

    /**
     * Fetch subscription products from the RevenueCat
     */
    fun fetchSubscriptionProducts(productsCallBack: (List<StoreProduct>) -> Unit) {
        rcBillingHelper.getSubscriptionSkuDetails(productsCallBack)
    }

    /**
     * Launch the purchase flow for the given product
     * @param activity activity to launch the purchase flowz
     * @param sku product to purchase
     * @param errorCallback error callback for the purchase flow
     * @param successCallback success callback for the purchase flow
     */
    fun launchPurchaseFlow(
        activity: Activity,
        sku: StoreProduct,
        errorCallback: (PurchasesError) -> Unit,
        successCallback: (StoreTransaction?) -> Unit,
    ) {
        rcBillingHelper.launchPurchaseFlow(activity, sku, errorCallback, successCallback)
    }

    /**
     * Restore the purchase for the user
     */
    fun restorePurchase(
        errorCallback: (PurchasesError) -> Unit,
        successCallback: () -> Unit,
    ) {
        rcBillingHelper.restorePurchase(errorCallback, successCallback)
    }

    /**
     * Updates the application's localization by setting the language code and making a registration request.
     * This function serves as an intermediary to trigger the localization update in the service layer.
     *
     * @param languageCode The new language code to set.
     * @param onComplete Callback to be invoked when the operation is completed successfully.
     */
    fun updateLocalizations(languageCode: String, onComplete: () -> Unit) {
        volvoxHubService.updateLocalizations(languageCode) {
            onComplete()
        }
    }

    /**
     * Claims a reward using the volvoxHubService and triggers a callback with the response.
     *
     * @param onComplete A callback function that is invoked with the response of the reward claim.
     *                   The response is provided as a `ClaimRewardResponse` object.
     */
    fun claimReward(onComplete: (ClaimRewardResponse) -> Unit, onError: (String) -> Unit) {
        volvoxHubService.claimReward(onComplete = onComplete, onError = onError)
    }

    /**
     * Fetches the reward status and invokes the callback with the result.
     *
     * @param onComplete A callback to handle the `RewardStatusResponse`.
     */
    fun rewardStatus(onComplete: (RewardStatusResponse) -> Unit) {
        volvoxHubService.rewardStatus(onComplete)
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
        volvoxHubService.usePromoCode(code, errorCallback, successCallback)
    }

    /**
     * Launches the default email client to send an email.
     *
     * This function uses an implicit intent with the `mailto:` URI scheme to open the default email app installed
     * on the device. It takes the recipient's email address, subject, and body content as parameters and
     * ensures that the recipient's email address is valid.
     *
     * @param context The context required to start the activity.
     * @param subject The subject of the email. Optional, can be empty.
     * @param body The body content of the email. Optional, can be empty.
     *
     * Note: The recipient email is retrieved using `volvoxHubService.getSupportEmail()`. Ensure the service
     * provides a valid email address. If the address is invalid, the user will be notified via a `Toast` message.
     *
     * Usage Example:
     * ```kotlin
     * sendEmail(context, "Support Request", "I need help with the app.")
     * ```
     */
    fun sendEmail(context: Context, subject: String = "", body: String = "") {
        val recipientEmail = volvoxHubService.getSupportEmail().orEmpty()

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(recipientEmail).matches()) {
            return
        }

        val emailUri = Uri.parse("mailto:").buildUpon().apply {
            appendQueryParameter("to", recipientEmail)
            appendQueryParameter("subject", subject)
            appendQueryParameter("body", body)
        }.build()

        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = emailUri
        }

        try {
            context.startActivity(emailIntent)
        } catch (_: ActivityNotFoundException) {
        }
    }

    /**
     * Get supported languages list
     */
    fun getSupportedLanguages(): List<String> = volvoxHubService.getSupportedLanguages()

    /**
     * Revenuecat Trial Check
     */
    fun checkIfUserUsedTrialForPackage(product: StoreProduct): Boolean {
        val trialCheck =
            product.googleProduct?.productDetails?.subscriptionOfferDetails?.any { offer ->
                offer.pricingPhases.pricingPhaseList.any() { phase ->
                    phase.priceAmountMicros == 0L && phase.billingCycleCount > 0
                }
            } ?: false
        return trialCheck.not()
    }

    fun deleteAccount(
        errorCallback: (String?) -> Unit,
        successCallback: (DeleteAccountResponse) -> Unit
    ) {
        volvoxHubService.deleteAccount(
            successCallback = successCallback,
            errorCallback = errorCallback
        )
    }

    /**
     * Checks the notification permission status.
     *
     * This function determines the current state of notification permissions based on Android version:
     * - For Android 13 (API 33) and above, runtime permission for POST_NOTIFICATIONS is required
     * - For Android 12L (API 32) and below, no explicit permission is required
     *
     * @param context The context required to check permissions
     * @param activity Optional activity parameter needed to check if permission was requested before
     * @return One of the following [NotificationPermissionStatus] values:
     *   - NOT_REQUIRED: No permission needed (API 32 and below)
     *   - GRANTED: Permission already granted
     *   - NEVER_REQUESTED: Permission never requested
     *   - DENIED: Permission denied
     */
    fun checkNotificationPermissionStatus(
        context: Context
    ): NotificationPermissionStatus {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissionState = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            )

            val hasRequestedBefore = volvoxHubService.getNotificationPermissionState()

            return when {
                permissionState == PackageManager.PERMISSION_GRANTED -> {
                    NotificationPermissionStatus.GRANTED
                }

                hasRequestedBefore -> {
                    NotificationPermissionStatus.DENIED
                }

                else -> {
                    NotificationPermissionStatus.NEVER_REQUESTED
                }
            }
        }
        return NotificationPermissionStatus.NOT_REQUIRED
    }

    /**
     * Requests the POST_NOTIFICATIONS permission for Android 13 (API 33) and above.
     *
     * This function handles the notification permission request process:
     * - For Android 13 (API 33) and above, it requests the POST_NOTIFICATIONS permission
     * - For Android 12L (API 32) and below, the callback is immediately invoked with true as no permission is needed
     *
     * The result of the permission request will be delivered through the provided callback.
     * Note: The host Activity must override onRequestPermissionsResult and call VolvoxHub.onRequestPermissionsResult
     * to properly handle the permission result.
     *
     * @param activity The activity required to request the permission
     * @param callback A callback function that will be invoked with the result of the permission request (true if granted, false if denied)
     * @param requestCode Optional request code for the permission request (default: DEFAULT_NOTIFICATION_PERMISSION_REQUEST_CODE)
     */
    fun requestNotificationPermission(
        activity: Activity,
        callback: (Boolean) -> Unit,
        requestCode: Int = DEFAULT_NOTIFICATION_PERMISSION_REQUEST_CODE
    ) {
        notificationPermissionCallbacks[requestCode] = callback
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissionState = checkNotificationPermissionStatus(activity)

            when (permissionState) {
                NotificationPermissionStatus.GRANTED -> {
                    callback(true)
                    notificationPermissionCallbacks.remove(requestCode)
                }

                NotificationPermissionStatus.NOT_REQUIRED -> {
                    callback(true)
                    notificationPermissionCallbacks.remove(requestCode)
                }

                else -> {
                    volvoxHubService.saveNotificationPermissionState(true)

                    ActivityCompat.requestPermissions(
                        activity,
                        arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                        requestCode
                    )
                }
            }
        } else {
            callback(true)
            notificationPermissionCallbacks.remove(requestCode)
        }
    }


    fun getProducts(
        successCallback: (GetProductsResponse) -> Unit,
        errorCallback: (String?) -> Unit
    ) {
        volvoxHubService.getProducts(
            errorCallback = errorCallback,
            successCallback = successCallback
        )
    }

    fun socialLoginRemote(
        socialLoginRequest: SocialLoginRequest,
        errorCallback: (String?) -> Unit,
        successCallback: (RegisterBaseResponse) -> Unit
    ) {
        volvoxHubService.socialLogin(
            socialLoginRequest = socialLoginRequest,
            errorCallback = errorCallback,
            successCallback = successCallback
        )
    }
}
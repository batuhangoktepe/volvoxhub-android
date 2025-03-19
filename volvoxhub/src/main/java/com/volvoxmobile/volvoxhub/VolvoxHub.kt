package com.volvoxmobile.volvoxhub

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
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
import com.volvoxmobile.volvoxhub.common.util.Localizations
import com.volvoxmobile.volvoxhub.common.util.VolvoxHubLogLevel
import com.volvoxmobile.volvoxhub.data.remote.model.hub.response.ClaimRewardResponse
import com.volvoxmobile.volvoxhub.data.remote.model.hub.response.PromoCodeResponse
import com.volvoxmobile.volvoxhub.data.remote.model.hub.response.RewardStatusResponse
import com.volvoxmobile.volvoxhub.data.remote.model.hub.response.UnseenStatusResponse
import com.volvoxmobile.volvoxhub.strings.ConfigureStrings
import com.volvoxmobile.volvoxhub.ui.ban.BannedPopup
import com.volvoxmobile.volvoxhub.ui.ban.BannedPopupConfig
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

    fun getUnseenStatus(
        errorCallback: (String?) -> Unit,
        successCallback: (UnseenStatusResponse) -> Unit
    ) {
        volvoxHubService.getUnseenStatus(
            errorCallback = errorCallback,
            successCallback = successCallback
        )
    }
}
package com.volvoxmobile.volvoxhub

import android.app.Activity
import androidx.compose.runtime.Composable
import com.revenuecat.purchases.PurchasesError
import com.revenuecat.purchases.models.StoreProduct
import com.revenuecat.purchases.models.StoreTransaction
import com.volvoxmobile.volvoxhub.billing.RcBillingHelper
import com.volvoxmobile.volvoxhub.common.util.VolvoxHubLogLevel
import com.volvoxmobile.volvoxhub.strings.ConfigureStrings
import com.volvoxmobile.volvoxhub.ui.ban.BannedPopup
import com.volvoxmobile.volvoxhub.ui.ban.BannedPopupConfig

class VolvoxHub private constructor(configuration: Configuration) {

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

        fun getInstance(): VolvoxHub {
            return instance ?: throw IllegalStateException("VolvoxHub is not initialized. Call initialize() first.")
        }

        @Composable
        fun ShowBannedPopup(isVisible: Boolean, config: BannedPopupConfig) {
            BannedPopup(isVisible = isVisible, config = config)
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
     * @param activity activity to launch the purchase flow
     * @param sku product to purchase
     * @param errorCallback error callback for the purchase flow
     * @param successCallback success callback for the purchase flow
     */
    fun launchPurchaseFlow(
        activity: Activity,
        sku: StoreProduct,
        errorCallback: (PurchasesError) -> Unit,
        successCallback: (StoreTransaction?) -> Unit
    ) {
        rcBillingHelper.launchPurchaseFlow(activity, sku, errorCallback, successCallback)
    }

    /**
     * Restore the purchase for the user
     */
    fun restorePurchase(errorCallback: (PurchasesError) -> Unit, successCallback: () -> Unit) {
        rcBillingHelper.restorePurchase(errorCallback, successCallback)
    }
}

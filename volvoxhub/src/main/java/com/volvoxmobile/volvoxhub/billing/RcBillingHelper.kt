package com.volvoxmobile.volvoxhub.billing

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import com.appsflyer.AppsFlyerLib
import com.facebook.appevents.AppEventsLogger
import com.onesignal.OneSignal
import com.revenuecat.purchases.*
import com.revenuecat.purchases.interfaces.*
import com.revenuecat.purchases.models.StoreProduct
import com.revenuecat.purchases.models.StoreTransaction
import com.volvoxmobile.volvoxhub.VolvoxHubLogManager
import com.volvoxmobile.volvoxhub.common.util.Localizations
import com.volvoxmobile.volvoxhub.common.util.VolvoxHubLogLevel

internal class RcBillingHelper : UpdatedCustomerInfoListener {
    private companion object {
        const val MAX_RETRY_COUNT = 10
    }

    private var anonymousAppDeviceGUID: String = ""
    private var retryCount = 0
    private var skuDetails: List<StoreProduct>? = null

    private fun isProductsFetched() = skuDetails != null

    /**
     * Configure the RevenueCat SDK and fetch available products
     * Active log level is set to DEBUG if the SDK is in debug mode
     * Fetch available products and store them in the skuDetails variable
     */
    fun init(
        context: Context,
        uuid: String,
        rcKey: String,
        userEmail: String,
        userName: String
    ) {
        configurePurchases(context, uuid, rcKey)
        setLoggingLevelIfNeeded()
        anonymousAppDeviceGUID = AppEventsLogger.getAnonymousAppDeviceGUID(context)
        setUserDeviceId(uuid, userEmail, userName)
        updateAppsflyerUid(AppsFlyerLib.getInstance().getAppsFlyerUID(context).orEmpty())
        fetchAvailableProducts()
    }

    private fun configurePurchases(
        context: Context,
        uuid: String,
        rcKey: String,
    ) {
        Purchases.configure(
            PurchasesConfiguration
                .Builder(context, rcKey)
                .appUserID(uuid)
                .build(),
        )
        Purchases.sharedInstance.updatedCustomerInfoListener = this
        Purchases.sharedInstance.collectDeviceIdentifiers()
    }

    private fun setLoggingLevelIfNeeded() {
        if (VolvoxHubLogManager.isDebug()) {
            Purchases.logLevel = LogLevel.DEBUG
        }
    }

    private fun setUserDeviceId(uuid: String, userEmail: String, userName: String) {
        Purchases.sharedInstance.logIn(uuid)
        Purchases.sharedInstance.setOnesignalID(OneSignal.getDeviceState()?.userId)
        Purchases.sharedInstance.setFBAnonymousID(anonymousAppDeviceGUID)
        Purchases.sharedInstance.setEmail(userEmail)
        Purchases.sharedInstance.setDisplayName(userName)
    }

    private fun updateAppsflyerUid(uid: String) {
        Purchases.sharedInstance.setAppsflyerID(uid)
    }

    fun launchPurchaseFlow(
        activity: Activity,
        sku: StoreProduct,
        errorCallback: (PurchasesError) -> Unit,
        successCallback: (StoreTransaction?) -> Unit,
    ) {
        Purchases.sharedInstance.purchase(
            PurchaseParams.Builder(activity, sku).build(),
            object : PurchaseCallback {
                override fun onCompleted(
                    storeTransaction: StoreTransaction,
                    customerInfo: CustomerInfo,
                ) {
                    successCallback.invoke(storeTransaction)
                }

                override fun onError(
                    error: PurchasesError,
                    userCancelled: Boolean,
                ) {
                    handleError(error)
                    errorCallback.invoke(error)
                }
            },
        )
    }

    fun restorePurchase(
        context: Context,
        errorCallback: (PurchasesError) -> Unit,
        successCallback: () -> Unit,
    ) {
        Purchases.sharedInstance.restorePurchases(
            object : ReceiveCustomerInfoCallback {
                override fun onError(error: PurchasesError) {
                    handleError(error)
                }

                override fun onReceived(customerInfo: CustomerInfo) {
                    if (customerInfo.activeSubscriptions.isNotEmpty()) {
                        successCallback.invoke()
                    } else {
                        errorCallback.invoke(
                            PurchasesError(
                                PurchasesErrorCode.UnknownError,
                                Localizations.getHub(context,"no_active_subscription_found"),
                            ),
                        )
                    }
                }
            },
        )
    }

    fun getSubscriptionSkuDetails(skuDetailsCallback: (List<StoreProduct>) -> Unit) {
        if (isProductsFetched()) {
            skuDetails?.filter { it.type == ProductType.SUBS }
                .let { skuDetailsCallback.invoke(it.orEmpty()) }
        } else {
            Handler(Looper.getMainLooper()).postDelayed({
                fetchAvailableProducts()
                getSubscriptionSkuDetails(skuDetailsCallback)
            }, 3000L)
        }
    }

    fun getConsumableSkuDetails(skuDetailsCallback: (List<StoreProduct>) -> Unit) {
        if (isProductsFetched()) {
            skuDetails?.filter { it.type == ProductType.INAPP }
                .let { skuDetailsCallback.invoke(it.orEmpty()) }
        } else {
            Handler(Looper.getMainLooper()).postDelayed({
                fetchAvailableProducts()
                getConsumableSkuDetails(skuDetailsCallback)
            }, 3000L)
        }
    }

    private fun fetchAvailableProducts() {
        Purchases.sharedInstance.getOfferings(
            object : ReceiveOfferingsCallback {
                override fun onError(error: PurchasesError) {
                    Handler(Looper.getMainLooper()).postDelayed({
                        if (retryCount < MAX_RETRY_COUNT) {
                            fetchAvailableProducts()
                            retryCount++
                        } else {
                            handleError(error, "Failed to fetch available products")
                        }
                    }, 3000L)
                    handleError(error)
                }

                override fun onReceived(offerings: Offerings) {
                    val subscriptions =
                        offerings.all["Subscriptions"]
                            ?.availablePackages
                            ?.map { it.product }
                            .orEmpty()
                    val consumables =
                        offerings.all["Consumables"]
                            ?.availablePackages
                            ?.map { it.product }
                            .orEmpty()
                    skuDetails = subscriptions + consumables
                }
            },
        )
    }

    private fun handleError(
        error: PurchasesError,
        defaultMessage: String = "An error occurred",
    ) {
        VolvoxHubLogManager.log(error.message ?: defaultMessage, VolvoxHubLogLevel.ERROR)
    }

    override fun onReceived(customerInfo: CustomerInfo) {
        VolvoxHubLogManager.log("Customer info updated: $customerInfo", VolvoxHubLogLevel.INFO)
    }
}

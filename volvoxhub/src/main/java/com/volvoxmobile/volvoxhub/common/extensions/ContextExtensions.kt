package com.volvoxmobile.volvoxhub.common.extensions

import android.app.UiModeManager
import android.content.Context
import android.content.res.Configuration
import android.telephony.TelephonyManager
import android.util.DisplayMetrics
import android.view.WindowManager
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import java.io.IOException

fun Context.deviceType(): String {
    val screenSize = this.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK
    val uiModeManager = this.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
    return when {
        uiModeManager.currentModeType == Configuration.UI_MODE_TYPE_TELEVISION -> "TV"
        screenSize >= Configuration.SCREENLAYOUT_SIZE_LARGE -> "Tablet"
        else -> "Phone"
    }
}

fun Context.getUserRegion(): String? {
    val telephonyManager = this.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    return telephonyManager.networkCountryIso
}

fun Context.getScreenResolution(): String {
    val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val displayMetrics = DisplayMetrics()
    windowManager.defaultDisplay.getMetrics(displayMetrics)

    val width = displayMetrics.widthPixels
    val height = displayMetrics.heightPixels

    return "$width x $height"
}

fun Context.getScreenDpi(): String {
    val displayMetrics = resources.displayMetrics
    return displayMetrics.densityDpi.toString()
}

fun Context.getAdvertisingId(): String? =
    try {
        val advertisingIdInfo = AdvertisingIdClient.getAdvertisingIdInfo(this)
        if (advertisingIdInfo.isLimitAdTrackingEnabled) {
            null
        } else {
            advertisingIdInfo.id
        }
    } catch (e: IOException) {
        e.printStackTrace()
        null
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }

fun Context.resIdByName(
    resIdName: String,
    resType: String,
): Int = resources.getIdentifier(resIdName, resType, packageName)

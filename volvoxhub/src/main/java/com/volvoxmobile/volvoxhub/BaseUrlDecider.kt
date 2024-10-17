package com.volvoxmobile.volvoxhub

import com.volvoxmobile.volvoxhub.common.util.Constants

object BaseUrlDecider {

    fun getApiBaseUrl() = if (BuildConfig.DEBUG) {
        Constants.BASE_URL
    } else {
        Constants.BASE_URL
    }
}
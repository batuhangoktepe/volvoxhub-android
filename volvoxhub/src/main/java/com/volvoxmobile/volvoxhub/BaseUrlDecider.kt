package com.volvoxmobile.volvoxhub

import com.volvoxmobile.volvoxhub.common.util.ApiEnvironment
import com.volvoxmobile.volvoxhub.common.util.Constants

object BaseUrlDecider {
    fun getApiBaseUrl(environment: ApiEnvironment) =
        if (environment == ApiEnvironment.PROD) {
            Constants.PROD_BASE_URL
        } else {
            Constants.STAGE_BASE_URL
        }
}

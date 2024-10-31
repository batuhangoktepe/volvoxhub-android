package com.volvoxmobile.volvoxhub.data.remote.model.hub.response

import com.google.gson.annotations.SerializedName

data class RegisterThirdPartyResponse(
    @SerializedName("revenue_cat_id") val revenuecatId: String?,
    @SerializedName("appsflyer_dev_key") val appsflyerDevKey: String?,
    @SerializedName("appsflyer_app_id") val appsflyerAppId: String?,
    @SerializedName("onesignal_app_id") val oneSignalAppId: String?,
    @SerializedName("amplitude_api_key") val amplitudeApiKey: String?,
)

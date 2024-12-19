package com.volvoxmobile.volvoxhub.data.remote.model.hub.response

import com.google.gson.annotations.SerializedName

data class RegisterThirdPartyResponse(
    @SerializedName("revenue_cat_api_key") val revenuecatId: String?,
    @SerializedName("appsflyer_dev_key") val appsflyerDevKey: String?,
    @SerializedName("appsflyer_app_id") val appsflyerAppId: String?,
    @SerializedName("one_signal_api_key") val oneSignalAppId: String?,
    @SerializedName("amplitude_api_key") val amplitudeApiKey: String?,
    @SerializedName("amplitude_experiment_key") val amplitudeExperimentKey: String?,
    @SerializedName("facebook_app_id") val facebookAppId: String?,
    @SerializedName("facebook_client_token") val facebookClientToken: String?
)

package com.volvoxmobile.volvoxhub.data.remote.model.hub.response

import com.google.gson.annotations.SerializedName

data class RegisterConfigResponse(
    @SerializedName("store_version") val storeVersion: String,
    @SerializedName("force_update") val forceUpdate: Boolean,
    @SerializedName("localization_url") val localizationUrl: String,
    @SerializedName("privacy_policy_url") val privacyPolicyUrl: String,
    @SerializedName("tos_url") val eula: String,
)

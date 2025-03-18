package com.volvoxmobile.volvoxhub.data.remote.model.hub.response

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

data class RegisterBaseResponse(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String,
    @SerializedName("vid") val vid: String,
    @SerializedName("social") val social: RegisterSocialResponse,
    @SerializedName("device") val device: RegisterDeviceResponse,
    @SerializedName("config") val config: RegisterConfigResponse,
    @SerializedName("third_party") val thirdParty: RegisterThirdPartyResponse,
    @SerializedName("remote_config") val remoteConfig: JsonObject?,
    @SerializedName("support") val support: RegisterSupportResponse
)

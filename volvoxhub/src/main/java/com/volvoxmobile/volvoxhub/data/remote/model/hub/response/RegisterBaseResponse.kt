package com.volvoxmobile.volvoxhub.data.remote.model.hub.response

import com.google.gson.annotations.SerializedName

data class RegisterBaseResponse(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String,
    @SerializedName("vid") val vid: String,
    @SerializedName("device") val device: RegisterDeviceResponse,
    @SerializedName("config") val config: RegisterConfigResponse,
    @SerializedName("third_party") val thirdParty: RegisterThirdPartyResponse
)

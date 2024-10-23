package com.volvoxmobile.volvoxhub.data.remote.model.hub.response

import com.google.gson.annotations.SerializedName

data class RegisterDeviceResponse(
    @SerializedName("premium_status") val premiumStatus: Boolean,
    @SerializedName("ban_status") val banStatus: Boolean,
    @SerializedName("user_type") val userType: String,
    @SerializedName("onesignal_status") val oneSignalStatus: Boolean,
)

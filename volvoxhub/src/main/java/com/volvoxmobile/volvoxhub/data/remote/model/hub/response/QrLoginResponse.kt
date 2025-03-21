package com.volvoxmobile.volvoxhub.data.remote.model.hub.response


import com.google.gson.annotations.SerializedName

data class QrLoginResponse(
    @SerializedName("success")
    val success: Boolean
)
package com.volvoxmobile.volvoxhub.data.remote.model.hub.request


import com.google.gson.annotations.SerializedName

data class QrLoginRequest(
    @SerializedName("token")
    val token: String
)
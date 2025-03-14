package com.volvoxmobile.volvoxhub.data.remote.model.hub.request


import com.google.gson.annotations.SerializedName

data class SocialLoginRequest(
    @SerializedName("account_id")
    val accountId: String,
    @SerializedName("provider")
    val provider: String,
    @SerializedName("token")
    val token: String
)
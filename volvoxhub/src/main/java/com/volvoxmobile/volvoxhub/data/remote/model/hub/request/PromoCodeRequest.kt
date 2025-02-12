package com.volvoxmobile.volvoxhub.data.remote.model.hub.request

import com.google.gson.annotations.SerializedName

data class PromoCodeRequest(
    @SerializedName("code") val code: String,
)
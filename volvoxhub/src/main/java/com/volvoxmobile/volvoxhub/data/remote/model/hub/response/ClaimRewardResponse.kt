package com.volvoxmobile.volvoxhub.data.remote.model.hub.response

import com.google.gson.annotations.SerializedName

data class ClaimRewardResponse(
    @SerializedName("status") val status: String,
    @SerializedName("gift_amount") val giftAmount: Int,
    @SerializedName("balance") val balance: Int
)
package com.volvoxmobile.volvoxhub.data.remote.model.hub.response

import com.google.gson.annotations.SerializedName

data class RewardStatusResponse(
    @SerializedName("is_eligible") val isEligible: Boolean,
    @SerializedName("next_reward_date") val nextRewardDate: String
)
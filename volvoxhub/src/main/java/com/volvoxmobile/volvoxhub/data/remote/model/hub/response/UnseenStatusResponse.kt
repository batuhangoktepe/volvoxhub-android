package com.volvoxmobile.volvoxhub.data.remote.model.hub.response


import com.google.gson.annotations.SerializedName

data class UnseenStatusResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("unseen_response")
    val unseenResponse: Boolean
)
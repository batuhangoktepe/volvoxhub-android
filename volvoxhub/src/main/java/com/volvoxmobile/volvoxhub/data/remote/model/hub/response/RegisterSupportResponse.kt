package com.volvoxmobile.volvoxhub.data.remote.model.hub.response


import com.google.gson.annotations.SerializedName

data class RegisterSupportResponse(
    @SerializedName("categories")
    val categories: List<String>,
    @SerializedName("unseen_response")
    val unseenResponse: Boolean
)
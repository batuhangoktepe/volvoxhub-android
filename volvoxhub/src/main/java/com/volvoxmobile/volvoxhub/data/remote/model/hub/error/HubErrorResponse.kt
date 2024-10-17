package com.volvoxmobile.volvoxhub.data.remote.model.hub.error

import com.google.gson.annotations.SerializedName

data class HubErrorResponse(
    @SerializedName("statusCode") val statusCode: Int,
    @SerializedName("message") val message: String?
)
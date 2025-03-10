package com.volvoxmobile.volvoxhub.data.remote.model.hub.request

import com.google.gson.annotations.SerializedName

data class NewTicketRequest(
    @SerializedName("category") val category: String,
    @SerializedName("message") val message: String,
)

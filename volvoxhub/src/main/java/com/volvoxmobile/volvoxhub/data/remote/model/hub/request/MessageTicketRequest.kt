package com.volvoxmobile.volvoxhub.data.remote.model.hub.request

import com.google.gson.annotations.SerializedName

data class MessageTicketRequest(
    @SerializedName("message") val message: String
)

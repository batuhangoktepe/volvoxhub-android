package com.volvoxmobile.volvoxhub.data.remote.model.hub.response


import com.google.gson.annotations.SerializedName

data class CreateNewTicketResponse(
    @SerializedName("category")
    val category: String?,
    @SerializedName("createdAt")
    val createdAt: String?,
    @SerializedName("id")
    val id: String?,
    @SerializedName("state")
    val state: String?,
    @SerializedName("status")
    val status: String?
)
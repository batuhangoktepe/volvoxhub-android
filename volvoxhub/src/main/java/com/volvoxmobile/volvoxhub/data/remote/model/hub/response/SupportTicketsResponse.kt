package com.volvoxmobile.volvoxhub.data.remote.model.hub.response


import com.google.gson.annotations.SerializedName

typealias SupportTicketsResponse = List<SupportTicketsResponseItem>

data class SupportTicketsResponseItem(
    @SerializedName("category")
    val category: String?,
    @SerializedName("createdAt")
    val createdAt: String?,
    @SerializedName("id")
    val id: String?,
    @SerializedName("isSeen")
    val isSeen: Boolean?,
    @SerializedName("lastMessage")
    val lastMessage: String?,
    @SerializedName("lastMessageCreatedAt")
    val lastMessageCreatedAt: String?,
    @SerializedName("state")
    val state: String?,
    @SerializedName("status")
    val status: String?
)
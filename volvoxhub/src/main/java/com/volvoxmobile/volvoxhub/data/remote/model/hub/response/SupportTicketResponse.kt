package com.volvoxmobile.volvoxhub.data.remote.model.hub.response


import com.google.gson.annotations.SerializedName

data class SupportTicketResponse(
    @SerializedName("category")
    val category: String?,
    @SerializedName("createdAt")
    val createdAt: String?,
    @SerializedName("deviceId")
    val deviceId: String?,
    @SerializedName("email")
    val email: Any?,
    @SerializedName("id")
    val id: String?,
    @SerializedName("messages")
    val messages: List<Message?>?,
    @SerializedName("name")
    val name: Any?,
    @SerializedName("projectId")
    val projectId: Int?,
    @SerializedName("source")
    val source: String?,
    @SerializedName("state")
    val state: String?,
    @SerializedName("status")
    val status: String?,
    @SerializedName("updatedAt")
    val updatedAt: String?,
    @SerializedName("vid")
    val vid: String?
)

data class Message(
    @SerializedName("createdAt")
    val createdAt: String?,
    @SerializedName("deviceId")
    val deviceId: String?,
    @SerializedName("id")
    val id: String?,
    @SerializedName("isFromDevice")
    val isFromDevice: Boolean?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("readBy")
    val readBy: List<ReadBy?>?,
    @SerializedName("ticketId")
    val ticketId: String?,
    @SerializedName("userId")
    val userId: String?
)

data class ReadBy(
    @SerializedName("deviceId")
    val deviceId: String?,
    @SerializedName("readAt")
    val readAt: String?,
    @SerializedName("userId")
    val userId: String?
)
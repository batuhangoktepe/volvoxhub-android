package com.volvoxmobile.volvoxhub.data.remote.model.hub.response


import com.google.gson.annotations.SerializedName

data class CreateNewMessageResponse(
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
    val readBy: List<Any?>?,
    @SerializedName("ticketId")
    val ticketId: String?,
    @SerializedName("userId")
    val userId: Any?
)
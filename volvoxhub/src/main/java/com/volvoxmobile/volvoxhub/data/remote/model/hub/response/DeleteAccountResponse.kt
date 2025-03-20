package com.volvoxmobile.volvoxhub.data.remote.model.hub.response


import com.google.gson.annotations.SerializedName

data class DeleteAccountResponse(
    @SerializedName("success")
    val success: Boolean
)
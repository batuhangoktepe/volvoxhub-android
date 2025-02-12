package com.volvoxmobile.volvoxhub.data.remote.model.hub.response

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

data class PromoCodeResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("action_type") val actionType: String,
    @SerializedName("action_meta") val actionMeta: String,
    @SerializedName("extra_data") val extraData: JsonObject,
)
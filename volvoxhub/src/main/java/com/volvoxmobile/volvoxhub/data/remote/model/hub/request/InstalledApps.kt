package com.volvoxmobile.volvoxhub.data.remote.model.hub.request

import com.google.gson.annotations.SerializedName

data class InstalledApps(
    @SerializedName("instagram") val instagram: Boolean,
    @SerializedName("facebook") val facebook: Boolean,
    @SerializedName("tiktok") val tiktok: Boolean,
    @SerializedName("snapchat") val snapchat: Boolean
)
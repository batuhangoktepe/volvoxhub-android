package com.volvoxmobile.volvoxhub.data.local.model

import com.google.gson.JsonObject

data class VolvoxHubResponse(
    val banned: Boolean,
    val premiumStatus: Boolean,
    val forceUpdate: Boolean,
    val isRooted: Boolean,
    val remoteConfig: JsonObject?,
    val vId: String
)

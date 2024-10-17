package com.volvoxmobile.volvoxhub.data.local.model

data class VolvoxHubResponse(
    val banned: Boolean,
    val premiumStatus: Boolean,
    val forceUpdate: Boolean,
    val isRooted: Boolean
)
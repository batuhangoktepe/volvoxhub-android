package com.volvoxmobile.volvoxhub.data.remote.model.hub.request

import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    @SerializedName("user_type") val userType: String = "regular",
    @SerializedName("device_platform") val devicePlatform: String = "android",
    @SerializedName("device_type") val deviceType: String,
    @SerializedName("device_brand") val deviceBrand: String,
    @SerializedName("device_model") val deviceModel: String,
    @SerializedName("country_code") val countryCode: String,
    @SerializedName("language_code") val languageCode: String,
    @SerializedName("idfa") val idfa: String,
    @SerializedName("appsflyer_id") val appsflyerId: String,
    @SerializedName("firebase_id") val firebaseId: String,
    @SerializedName("op_region") val opRegion: String,
    @SerializedName("carrier_region") val carrierRegion: String,
    @SerializedName("app_name") val appName: String,
    @SerializedName("os") val os: String,
    @SerializedName("resolution") val resolution: String,
    @SerializedName("dpi") val dpi: String,
    @SerializedName("one_signal_token") val oneSignalToken: String,
    @SerializedName("one_signal_player_id") val oneSignalPlayerId: String,
    @SerializedName("file_path") val filePath: String,
    @SerializedName("app_version") val appVersion: String
)

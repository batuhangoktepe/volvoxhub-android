package com.volvoxmobile.volvoxhub.domain.local.preferences

interface PreferencesRepository {
    fun savePushToken(token: String)
    fun getPushToken(): String
    fun saveOneSignalPlayerId(playerId: String)
    fun getOneSignalPlayerId(): String
    fun saveAdvertisingId(advertisingId: String)
    fun getAdvertisingId(): String
    fun setAppsFlyerUserId(userId: String)
    fun getAppsFlyerUserId(): String
    fun getFirebaseId(): String
    fun saveFirebaseId(firebaseId: String)
    fun initializeSucceeded(): Boolean
    fun setInitializeSucceeded(succeeded: Boolean)
}
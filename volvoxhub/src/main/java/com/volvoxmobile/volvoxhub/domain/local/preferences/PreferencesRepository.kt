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

    fun setPrivacyPolicyUrl(url: String?)

    fun getPrivacyPolicyUrl(): String

    fun setTermsOfServiceUrl(url: String?)

    fun getTermsOfServiceUrl(): String

    fun saveVID(vId: String)

    fun getVID(): String

    fun saveSupportEmail(supportEmail: String?)

    fun getSupportEmail(): String?

    fun saveSupportedLanguages(languages: List<String>?)

    fun getSupportedLanguages(): List<String>?

    fun saveGoogleClientId(googleClientId: String)

    fun getGoogleClientId(): String
}

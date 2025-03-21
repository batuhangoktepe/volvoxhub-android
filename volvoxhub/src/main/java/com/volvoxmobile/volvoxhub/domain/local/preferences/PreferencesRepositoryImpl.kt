package com.volvoxmobile.volvoxhub.domain.local.preferences

import android.content.SharedPreferences
import com.volvoxmobile.volvoxhub.common.extensions.getStringOrEmpty
import com.volvoxmobile.volvoxhub.common.extensions.getStringOrNull

class PreferencesRepositoryImpl(
    private val sharedPreferences: SharedPreferences,
) : PreferencesRepository {
    private val sharedPreferencesEditor: SharedPreferences.Editor by lazy {
        sharedPreferences.edit()
    }

    override fun savePushToken(token: String) {
        with(sharedPreferencesEditor) {
            putString(PUSH_TOKEN, token)
            commit()
        }
    }

    override fun getPushToken(): String = sharedPreferences.getStringOrEmpty(PUSH_TOKEN)

    override fun saveOneSignalPlayerId(playerId: String) {
        with(sharedPreferencesEditor) {
            putString(PLAYER_ID, playerId)
            commit()
        }
    }

    override fun getOneSignalPlayerId(): String = sharedPreferences.getStringOrEmpty(PLAYER_ID)

    override fun saveAdvertisingId(advertisingId: String) {
        with(sharedPreferencesEditor) {
            putString(ADVERTISING_ID, advertisingId)
            commit()
        }
    }

    override fun getAdvertisingId(): String = sharedPreferences.getStringOrEmpty(ADVERTISING_ID)

    override fun setAppsFlyerUserId(userId: String) {
        with(sharedPreferencesEditor) {
            putString(APPS_FLYER_USER_ID, userId)
            commit()
        }
    }

    override fun getAppsFlyerUserId(): String = sharedPreferences.getStringOrEmpty(APPS_FLYER_USER_ID)

    override fun getFirebaseId(): String = sharedPreferences.getStringOrEmpty(FIREBASE_ID)

    override fun saveFirebaseId(firebaseId: String) {
        with(sharedPreferencesEditor) {
            putString(FIREBASE_ID, firebaseId)
            commit()
        }
    }

    override fun initializeSucceeded(): Boolean = sharedPreferences.getBoolean(INITIALIZE_SUCCEEDED, false)

    override fun setInitializeSucceeded(succeeded: Boolean) {
        with(sharedPreferencesEditor) {
            putBoolean(INITIALIZE_SUCCEEDED, succeeded)
            commit()
        }
    }

    override fun setPrivacyPolicyUrl(url: String?) {
        with(sharedPreferencesEditor) {
            putString(PRIVACY_POLICY_URL, url)
            commit()
        }
    }

    override fun getPrivacyPolicyUrl(): String = sharedPreferences.getStringOrEmpty(PRIVACY_POLICY_URL)

    override fun setTermsOfServiceUrl(url: String?) {
        with(sharedPreferencesEditor) {
            putString(TERMS_OF_SERVICE_URL, url)
            commit()
        }
    }

    override fun getTermsOfServiceUrl(): String = sharedPreferences.getStringOrEmpty(TERMS_OF_SERVICE_URL)

    override fun saveVID(vId: String) {
        with(sharedPreferencesEditor) {
            putString(V_ID, vId)
            commit()
        }
    }

    override fun getVID(): String = sharedPreferences.getStringOrEmpty(V_ID)

    override fun saveSupportEmail(supportEmail: String?) {
        with(sharedPreferencesEditor) {
            putString(SUPPORT_EMAIL, supportEmail)
            commit()
        }
    }

    override fun getSupportedLanguages(): List<String> =
        sharedPreferences.getStringSet(SUPPORTED_LANGUAGES, emptySet())?.toList() ?: emptyList()

    override fun saveSupportedLanguages(languages: List<String>?) {
        with(sharedPreferencesEditor) {
            putStringSet(SUPPORTED_LANGUAGES, languages?.toSet() ?: emptySet())
            apply()
        }
    }

    override fun getSupportEmail(): String? = sharedPreferences.getStringOrNull(SUPPORT_EMAIL)

    override fun saveNotificationPermissionState(permissionState: Boolean) {
        with(sharedPreferencesEditor) {
            putBoolean(NOTIFICATION_REQUESTED, permissionState)
            apply()
        }
    }

    override fun getNotificationPermissionState(): Boolean =
        sharedPreferences.getBoolean(NOTIFICATION_REQUESTED, false)


    override fun saveGoogleClientId(googleClientId: String) {
        with(sharedPreferencesEditor){
            putString(GOOGLE_CLIENT_ID,googleClientId)
            commit()
        }
    }

    override fun getGoogleClientId(): String = sharedPreferences.getStringOrEmpty(GOOGLE_CLIENT_ID)

    companion object {
        private const val ADVERTISING_ID = "advertising_id"
        private const val PUSH_TOKEN = "push_token"
        private const val PLAYER_ID = "player_id"
        private const val APPS_FLYER_USER_ID = "apps_flyer_user_id"
        private const val FIREBASE_ID = "firebase_id"
        private const val INITIALIZE_SUCCEEDED = "initialize_succeeded"
        private const val PRIVACY_POLICY_URL = "privacy_policy_url"
        private const val TERMS_OF_SERVICE_URL = "terms_of_service_url"
        private const val V_ID = "v_id"
        private const val SUPPORT_EMAIL = "support_email"
        private const val SUPPORTED_LANGUAGES = "supported_languages"
        private const val NOTIFICATION_REQUESTED = "notification_permission_requested"
        private const val GOOGLE_CLIENT_ID = "google_client_id"
    }
}

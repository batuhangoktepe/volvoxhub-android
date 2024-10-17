package com.volvoxmobile.volvoxhub.domain.local.preferences

import android.content.SharedPreferences
import com.volvoxmobile.volvoxhub.common.extensions.getStringOrEmpty

class PreferencesRepositoryImpl(private val sharedPreferences: SharedPreferences) :
    PreferencesRepository {

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

    override fun getAppsFlyerUserId(): String {
        return sharedPreferences.getStringOrEmpty(APPS_FLYER_USER_ID)
    }

    override fun getFirebaseId(): String = sharedPreferences.getStringOrEmpty(FIREBASE_ID)

    override fun saveFirebaseId(firebaseId: String) {
        with(sharedPreferencesEditor) {
            putString(FIREBASE_ID, firebaseId)
            commit()
        }
    }

    override fun initializeSucceeded(): Boolean {
        return sharedPreferences.getBoolean(INITIALIZE_SUCCEEDED, false)
    }

    override fun setInitializeSucceeded(succeeded: Boolean) {
        with(sharedPreferencesEditor) {
            putBoolean(INITIALIZE_SUCCEEDED, succeeded)
            commit()
        }
    }

    companion object {
        private const val ADVERTISING_ID = "advertising_id"
        private const val PUSH_TOKEN = "push_token"
        private const val PLAYER_ID = "player_id"
        private const val APPS_FLYER_USER_ID = "apps_flyer_user_id"
        private const val FIREBASE_ID = "firebase_id"
        private const val INITIALIZE_SUCCEEDED = "initialize_succeeded"
    }
}
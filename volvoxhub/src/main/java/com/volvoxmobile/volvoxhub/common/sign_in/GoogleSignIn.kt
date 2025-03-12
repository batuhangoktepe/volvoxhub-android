package com.volvoxmobile.volvoxhub.common.sign_in

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * Configuration class for Google Sign-In
 */
data class GoogleSignInConfig(
    val context: Context,
    val serverClientId: String?,
    val filterByAuthorizedAccounts: Boolean = true
)

/**
 * Callback interface for Google Sign-In events
 */
interface GoogleSignInCallback {
    fun onSignInSuccess(user: FirebaseUser)
    fun onSignInError(exception: Exception)
}

class GoogleSignIn private constructor(private val config: GoogleSignInConfig) {
    private lateinit var auth: FirebaseAuth
    private lateinit var credentialManager: CredentialManager
    private val scope = CoroutineScope(Dispatchers.IO + Job())
    private var callback: GoogleSignInCallback? = null

    companion object {
        private const val TAG = "GoogleSignIn"

        @Volatile
        private var instance: GoogleSignIn? = null

        fun initialize(config: GoogleSignInConfig): GoogleSignIn {
            return instance ?: synchronized(this) {
                instance ?: GoogleSignIn(config).also {
                    instance = it
                    it.init()
                }
            }
        }

        fun getInstance(): GoogleSignIn {
            return instance ?: throw IllegalStateException("GoogleSignIn must be initialized first")
        }

        /**
         * Helper method to get the server client ID from google-services.json
         * @param context Application context
         * @return Server client ID or null if not found
         */
        fun getServerClientIdFromResources(context: Context): String? {
            return try {
                val resources = context.resources
                val resourceId = resources.getIdentifier("google_services", "raw", context.packageName)
                if (resourceId == 0) {
                    Log.e(TAG, "google-services.json not found in raw resources")
                    return null
                }

                val inputStream = resources.openRawResource(resourceId)
                val reader = BufferedReader(InputStreamReader(inputStream))
                val jsonString = reader.readText()
                reader.close()

                val jsonObject = JSONObject(jsonString)
                val client = jsonObject.getJSONObject("client")
                val clientInfo = client.getJSONArray("oauth_client")

                for (i in 0 until clientInfo.length()) {
                    val item = clientInfo.getJSONObject(i)
                    if (item.has("client_type") && item.getInt("client_type") == 3) {
                        return item.getString("client_id")
                    }
                }

                Log.e(TAG, "Web client ID not found in google-services.json")
                null
            } catch (e: Exception) {
                Log.e(TAG, "Error reading google-services.json: ${e.message}")
                null
            }
        }
    }

    private fun init() {
        auth = FirebaseAuth.getInstance()
        credentialManager = CredentialManager.create(config.context)
    }

    fun setCallback(callback: GoogleSignInCallback) {
        this.callback = callback
    }

    fun signIn() {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setServerClientId(getServerClientIdFromResources(config.context) ?: "")
            .setFilterByAuthorizedAccounts(config.filterByAuthorizedAccounts)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        scope.launch {
            try {
                val result = credentialManager.getCredential(
                    context = config.context,
                    request = request
                )
                handleSignIn(result.credential)
            } catch (e: Exception) {
                Log.e(TAG, "Couldn't retrieve user's credentials: ${e.localizedMessage}")
                callback?.onSignInError(e)
            }
        }
    }

    fun signOut() {
        auth.signOut()
    }

    fun getCurrentUser(): FirebaseUser? = auth.currentUser

    private fun handleSignIn(credential: Credential) {
        if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            firebaseAuthWithGoogle(googleIdTokenCredential.idToken)
        } else {
            val error = IllegalStateException("Credential is not of type Google ID!")
            Log.w(TAG, error.message ?: "")
            callback?.onSignInError(error)
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "signInWithCredential:success")
                val user = auth.currentUser
                user?.let { callback?.onSignInSuccess(it) }
            } else {
                Log.w(TAG, "signInWithCredential:failure", task.exception)
                task.exception?.let { callback?.onSignInError(it) }
            }
        }
    }
}

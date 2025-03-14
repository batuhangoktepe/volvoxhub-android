package com.volvoxmobile.volvoxhub.common.sign_in

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.volvoxmobile.volvoxhub.data.remote.model.hub.request.SocialLoginRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


data class GoogleSignInConfig(
    val context: Context,
    val serverClientId: String?
)

interface GoogleSignInCallback {
    fun onSignInSuccess(socialLoginRequest: SocialLoginRequest)
    fun onSignInError(exception: Exception)
}

class GoogleSignIn private constructor(private val config: GoogleSignInConfig) {
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
                }
            }
        }

        fun getInstance(): GoogleSignIn {
            return instance ?: throw IllegalStateException("GoogleSignIn must be initialized first")
        }
    }

    fun setCallback(callback: GoogleSignInCallback) {
        this.callback = callback
    }

    fun signIn() {


        val serverClientId = config.serverClientId
            ?: throw IllegalStateException("Server Client ID must be provided for Google Sign In")

        val signInWithGoogleOption: GetSignInWithGoogleOption =
            GetSignInWithGoogleOption.Builder(
                serverClientId
            ).build()

        val request: GetCredentialRequest =
            GetCredentialRequest.Builder()
                .addCredentialOption(signInWithGoogleOption)
                .build()

        scope.launch {
            try {
                val credentialManager =
                    CredentialManager.create(context = config.context)
                val result = credentialManager.getCredential(
                    request = request,
                    context = config.context,
                )
                handleSignIn(result) {
                    callback?.onSignInSuccess(it)
                }
            } catch (e: Exception) {
                callback?.onSignInError(e)
            }
        }
    }

    private fun handleSignIn(
        result: GetCredentialResponse,
        onSignInSuccess: (SocialLoginRequest) -> Unit
    ) {
        val verifier = GoogleIdTokenVerifier.initialize(config.serverClientId ?: "")

        when (val credential = result.credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        scope.launch {
                            Log.d("TAG2", credential.data.toString())
                            val googleIdTokenCredential =
                                GoogleIdTokenCredential.createFrom(credential.data)
                            val verify = verifier.verify(googleIdTokenCredential.idToken)
                            val socialLoginRequest = SocialLoginRequest(
                                accountId = verify?.getPayload()?.getSubject() ?: "",
                                provider = "google",
                                token = googleIdTokenCredential.idToken
                            )
                            onSignInSuccess(socialLoginRequest)
                        }

                    } catch (e: GoogleIdTokenParsingException) {
                        Log.e("TAG2", "Received an invalid google id token response", e)
                    }
                }
            }

            else -> {
                Log.e("TAG2", "Unexpected type of credential")
            }
        }
    }
}
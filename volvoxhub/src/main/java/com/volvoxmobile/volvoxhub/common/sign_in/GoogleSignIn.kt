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
import com.volvoxmobile.volvoxhub.common.util.Constants
import com.volvoxmobile.volvoxhub.data.remote.model.hub.request.SocialLoginRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


data class GoogleSignInConfig(
    val context: Context,
    val serverClientId: String = Constants.WEB_CLIENT_ID
)

interface GoogleSignInCallback {
    fun onSignInSuccess(socialLoginRequest: SocialLoginRequest)
    fun onSignInError(exception: Exception)
}

class GoogleSignIn private constructor(private val config: GoogleSignInConfig) {
    private val scope = CoroutineScope(Dispatchers.IO + Job())
    private var callback: GoogleSignInCallback? = null

    companion object {
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
                handleSignIn(
                    result,
                    onSignInError = { exception ->
                        callback?.onSignInError(exception)
                    },
                    onSignInSuccess = { socialLoginRequest ->
                        callback?.onSignInSuccess(socialLoginRequest)
                    }
                )
            } catch (e: Exception) {
                callback?.onSignInError(e)
            }
        }
    }

    private fun handleSignIn(
        result: GetCredentialResponse,
        onSignInError: (Exception) -> Unit?,
        onSignInSuccess: (SocialLoginRequest) -> Unit
    ) {
        val verifier = GoogleIdTokenVerifier.initialize(config.serverClientId)

        when (val credential = result.credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        scope.launch {
                            val googleIdTokenCredential =
                                GoogleIdTokenCredential.createFrom(credential.data)
                            val payload = verifier.verify(googleIdTokenCredential.idToken)
                            val socialLoginRequest = SocialLoginRequest(
                                accountId = payload?.getPayload()?.getSubject() ?: "",
                                provider = "google",
                                token = googleIdTokenCredential.idToken,
                                name = googleIdTokenCredential.displayName.orEmpty(),
                                email = googleIdTokenCredential.id,
                                photoUrl = googleIdTokenCredential.profilePictureUri?.toString() ?: ""
                            )
                            onSignInSuccess(socialLoginRequest)
                        }
                    } catch (e: GoogleIdTokenParsingException) {
                        onSignInError(e)
                    }
                }
            }
            else -> {
                Log.e("GoogleSignIn", "Unexpected type of credential")
            }
        }
    }
}
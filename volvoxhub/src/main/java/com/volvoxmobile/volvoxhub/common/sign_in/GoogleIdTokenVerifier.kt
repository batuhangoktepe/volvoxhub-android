package com.volvoxmobile.volvoxhub.common.sign_in

import android.util.Base64
import android.util.Log
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import java.security.KeyFactory
import java.security.PublicKey
import java.security.Signature
import java.security.spec.X509EncodedKeySpec
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray

/**
 * A utility class to verify Google ID tokens and extract claims from them.
 */
class GoogleIdTokenVerifier private constructor(
    private val clientId: String
) {
    companion object {
        private const val TAG = "GoogleIdTokenVerifier"
        private const val GOOGLE_CERTS_URL = "https://www.googleapis.com/oauth2/v3/certs"
        private const val ISSUER = "https://accounts.google.com"

        @Volatile
        private var instance: GoogleIdTokenVerifier? = null

        fun initialize(clientId: String): GoogleIdTokenVerifier {
            return instance ?: synchronized(this) {
                instance ?: GoogleIdTokenVerifier(clientId).also { instance = it }
            }
        }

        fun getInstance(): GoogleIdTokenVerifier {
            return instance ?: throw IllegalStateException("GoogleIdTokenVerifier must be initialized first")
        }
    }

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    /**
     * Verifies the given ID token and returns a GoogleIdToken object if valid.
     *
     * @param idTokenString The ID token string to verify
     * @return A GoogleIdToken object if the token is valid, null otherwise
     */
    suspend fun verify(idTokenString: String): GoogleIdToken? {
        return try {
            // Split the JWT token into its parts
            val tokenParts = idTokenString.split(".")
            if (tokenParts.size != 3) {
                Log.e(TAG, "Invalid token format")
                return null
            }

            val header = tokenParts[0]
            val payload = tokenParts[1]
            val signature = tokenParts[2]

            // Parse the header to get the key ID and algorithm
            val headerJson = JSONObject(decodeBase64(header))
            val keyId = headerJson.getString("kid")
            val algorithm = headerJson.getString("alg")

            if (algorithm != "RS256") {
                Log.e(TAG, "Unsupported algorithm: $algorithm")
                return null
            }

            // Parse the payload
            val payloadJson = JSONObject(decodeBase64(payload))
            
            // Verify token claims
            val issuer = payloadJson.getString("iss")
            val audience = payloadJson.getString("aud")
            val expiration = payloadJson.getLong("exp")
            
            if (issuer != ISSUER) {
                Log.e(TAG, "Invalid issuer: $issuer")
                return null
            }
            
            if (audience != clientId) {
                Log.e(TAG, "Invalid audience: $audience")
                return null
            }
            
            val currentTimeSeconds = System.currentTimeMillis() / 1000
            if (expiration < currentTimeSeconds) {
                Log.e(TAG, "Token expired")
                return null
            }

            // Fetch Google's public keys
            val publicKey = fetchPublicKey(keyId) ?: return null

            // Verify signature
            val signatureBytes = Base64.decode(signature.replace('-', '+').replace('_', '/'), Base64.DEFAULT)
            val signedData = "${header}.${payload}".toByteArray(StandardCharsets.UTF_8)
            
            val signatureVerifier = Signature.getInstance("SHA256withRSA")
            signatureVerifier.initVerify(publicKey)
            signatureVerifier.update(signedData)
            
            if (!signatureVerifier.verify(signatureBytes)) {
                Log.e(TAG, "Signature verification failed")
                return null
            }

            // Create and return the GoogleIdToken
            GoogleIdToken(payloadJson)
        } catch (e: Exception) {
            Log.e(TAG, "Error verifying token", e)
            null
        }
    }

    private suspend fun fetchPublicKey(keyId: String): PublicKey? {
        return withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url(GOOGLE_CERTS_URL)
                    .build()

                val response = httpClient.newCall(request).execute()
                val responseBody = response.body?.string() ?: ""
                
                val certsJson = JSONObject(responseBody)
                val keysArray = certsJson.getJSONArray("keys")
                
                for (i in 0 until keysArray.length()) {
                    val keyJson = keysArray.getJSONObject(i)
                    if (keyJson.getString("kid") == keyId) {
                        // This is the key we want
                        val n = keyJson.getString("n")
                        val e = keyJson.getString("e")
                        
                        // Convert JWK components to RSA public key
                        try {
                            // Decode the base64url encoded modulus and exponent
                            val modulus = urlBase64Decode(n)
                            val exponent = urlBase64Decode(e)
                            
                            // Create RSA public key specification
                            val rsaKeyFactory = KeyFactory.getInstance("RSA")
                            
                            // Create ASN.1 formatted key from the modulus and exponent
                            // This is a simplified implementation - a real implementation
                            // would properly construct the ASN.1 DER encoding
                            
                            // For decoding JWT in a production app, consider using a JWT library
                            // like jjwt, auth0-jwt, or nimbus-jose-jwt
                            // For this example, we'll implement a simple JWT decoder
                            
                            // Extract the BigInteger values for modulus and exponent
                            val modulusBigInt = java.math.BigInteger(1, modulus)
                            val exponentBigInt = java.math.BigInteger(1, exponent)
                            
                            // Create RSA public key spec
                            val keySpec = java.security.spec.RSAPublicKeySpec(modulusBigInt, exponentBigInt)
                            
                            // Generate the public key
                            return@withContext rsaKeyFactory.generatePublic(keySpec)
                        } catch (e: Exception) {
                            Log.e(TAG, "Error creating public key from JWK", e)
                            return@withContext null
                        }
                    }
                }
                
                Log.e(TAG, "Public key with ID $keyId not found")
                null
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching public key", e)
                null
            }
        }
    }

    private fun decodeBase64(input: String): String {
        val decodedBytes = Base64.decode(
            input.replace('-', '+').replace('_', '/'), 
            Base64.DEFAULT
        )
        return String(decodedBytes, StandardCharsets.UTF_8)
    }

    private fun urlBase64Decode(input: String): ByteArray {
        return Base64.decode(
            input.replace('-', '+').replace('_', '/'),
            Base64.DEFAULT
        )
    }
}

/**
 * Represents a verified Google ID token and provides access to its payload.
 */
class GoogleIdToken(private val payloadJson: JSONObject) {
    /**
     * Gets the payload of the token.
     */
    fun getPayload(): GoogleIdTokenPayload {
        return GoogleIdTokenPayload(payloadJson)
    }
}

/**
 * Represents the payload of a Google ID token.
 */
class GoogleIdTokenPayload(private val payloadJson: JSONObject) {
    /**
     * Gets the subject (user ID) from the payload.
     */
    fun getSubject(): String {
        return payloadJson.getString("sub")
    }
    
    /**
     * Gets the email from the payload.
     */
    fun getEmail(): String? {
        return if (payloadJson.has("email")) payloadJson.getString("email") else null
    }
    
    /**
     * Gets whether the email is verified.
     */
    fun getEmailVerified(): Boolean {
        return if (payloadJson.has("email_verified")) payloadJson.getBoolean("email_verified") else false
    }
    
    /**
     * Gets the user's name from the payload.
     */
    fun getName(): String? {
        return if (payloadJson.has("name")) payloadJson.getString("name") else null
    }
    
    /**
     * Gets the user's picture URL from the payload.
     */
    fun getPicture(): String? {
        return if (payloadJson.has("picture")) payloadJson.getString("picture") else null
    }
    
    /**
     * Gets the hosted domain from the payload.
     */
    fun getHostedDomain(): String? {
        return if (payloadJson.has("hd")) payloadJson.getString("hd") else null
    }
}
package com.patsy.app.auth.debug

import android.content.Context
import android.util.Base64
import com.patsy.app.auth.PublicSession
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Arrays
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object DebugTestAccessFactory {
    fun create(context: Context): DebugTestAccess = DebugTestAccessImpl(context.applicationContext)
}

private class DebugTestAccessImpl(context: Context) : DebugTestAccess {
    private val prefs = context.getSharedPreferences("patsy_debug_test_access", Context.MODE_PRIVATE)

    override suspend fun attemptLogin(
        username: String,
        password: CharArray,
        keepSignedIn: Boolean,
    ): DebugLoginResult {
        val normalized = username.trim()
        if (normalized != DEBUG_USERNAME) {
            Arrays.fill(password, '\u0000')
            return DebugLoginResult.NotHandled
        }
        return try {
            if (!prefs.getBoolean(KEY_HAS_CUSTOM, false)) {
                if (MessageDigest.isEqual(String(password).toByteArray(), TEMP_PASSWORD.toByteArray())) {
                    DebugLoginResult.NeedsPasswordSetup
                } else {
                    DebugLoginResult.Rejected("Invalid username or password")
                }
            } else if (verifyCustom(password)) {
                prefs.edit()
                    .putBoolean(KEY_SESSION_ACTIVE, true)
                    .putBoolean(KEY_KEEP_SIGNED_IN, keepSignedIn)
                    .apply()
                DebugLoginResult.Authenticated(createSession())
            } else {
                DebugLoginResult.Rejected("Invalid username or password")
            }
        } finally {
            Arrays.fill(password, '\u0000')
        }
    }

    override suspend fun completePasswordSetup(
        newPassword: CharArray,
        confirmPassword: CharArray,
        keepSignedIn: Boolean,
    ): PublicSession? {
        return try {
            if (newPassword.size < 8 || !newPassword.contentEquals(confirmPassword)) {
                null
            } else {
                val salt = ByteArray(16).also { SecureRandom().nextBytes(it) }
                val verifier = derive(newPassword, salt)
                prefs.edit()
                    .putString(KEY_SALT, Base64.encodeToString(salt, Base64.NO_WRAP))
                    .putString(KEY_VERIFIER, Base64.encodeToString(verifier, Base64.NO_WRAP))
                    .putBoolean(KEY_HAS_CUSTOM, true)
                    .putBoolean(KEY_SESSION_ACTIVE, true)
                    .putBoolean(KEY_KEEP_SIGNED_IN, keepSignedIn)
                    .apply()
                createSession()
            }
        } finally {
            Arrays.fill(newPassword, '\u0000')
            Arrays.fill(confirmPassword, '\u0000')
        }
    }

    override suspend fun restoreSessionIfRemembered(): PublicSession? {
        if (!prefs.getBoolean(KEY_HAS_CUSTOM, false)) return null
        if (!prefs.getBoolean(KEY_KEEP_SIGNED_IN, false)) return null
        if (!prefs.getBoolean(KEY_SESSION_ACTIVE, false)) return null
        return createSession()
    }

    override suspend fun logout() {
        prefs.edit()
            .putBoolean(KEY_SESSION_ACTIVE, false)
            .putBoolean(KEY_KEEP_SIGNED_IN, false)
            .apply()
    }

    private fun verifyCustom(password: CharArray): Boolean {
        val saltText = prefs.getString(KEY_SALT, null) ?: return false
        val expectedText = prefs.getString(KEY_VERIFIER, null) ?: return false
        return runCatching {
            val salt = Base64.decode(saltText, Base64.NO_WRAP)
            val expected = Base64.decode(expectedText, Base64.NO_WRAP)
            val actual = derive(password, salt)
            MessageDigest.isEqual(actual, expected)
        }.getOrDefault(false)
    }

    private fun derive(password: CharArray, salt: ByteArray): ByteArray {
        val spec = PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH_BITS)
        return try {
            SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).encoded
        } finally {
            spec.clearPassword()
        }
    }

    private fun createSession(): PublicSession = PublicSession(
        sessionId = "debug-patsy-owner-session",
        userId = "debug-patsy-owner",
        username = DEBUG_USERNAME,
        maskedEmail = "te**@local.invalid",
        emailVerified = true,
        expiresAtEpochMillis = System.currentTimeMillis() + SESSION_WINDOW_MILLIS,
    )

    private companion object {
        const val DEBUG_USERNAME = "patsytest"
        const val TEMP_PASSWORD = "PatsyTest!2026"
        const val ITERATIONS = 120_000
        const val KEY_LENGTH_BITS = 256
        const val SESSION_WINDOW_MILLIS = 30L * 24 * 60 * 60 * 1000
        const val KEY_SALT = "salt"
        const val KEY_VERIFIER = "verifier"
        const val KEY_HAS_CUSTOM = "has_custom"
        const val KEY_KEEP_SIGNED_IN = "keep_signed_in"
        const val KEY_SESSION_ACTIVE = "session_active"
    }
}

package com.patsy.app.auth

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/** Stores only this device's Supabase session tokens in Android encrypted preferences. */
class EncryptedAuthSessionStore(context: Context) : AuthSessionStore {
    private val prefs = EncryptedSharedPreferences.create(
        context,
        FILE_NAME,
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
    )

    override fun read(): StoredAuthSession? {
        val access = prefs.getString(KEY_ACCESS, null) ?: return null
        val refresh = prefs.getString(KEY_REFRESH, null) ?: return null
        val sessionId = prefs.getString(KEY_SESSION_ID, null) ?: return null
        val userId = prefs.getString(KEY_USER_ID, null) ?: return null
        val username = prefs.getString(KEY_USERNAME, null) ?: return null
        val maskedEmail = prefs.getString(KEY_MASKED_EMAIL, null) ?: "hidden"
        val emailVerified = prefs.getBoolean(KEY_EMAIL_VERIFIED, false)
        val expiresAt = prefs.getLong(KEY_EXPIRES_AT, 0L)
        if (expiresAt <= 0L) return null
        return StoredAuthSession(
            accessToken = access,
            refreshToken = refresh,
            publicSession = PublicSession(
                sessionId = sessionId,
                userId = userId,
                username = username,
                maskedEmail = maskedEmail,
                emailVerified = emailVerified,
                expiresAtEpochMillis = expiresAt,
            ),
        )
    }

    override fun write(session: StoredAuthSession) {
        prefs.edit()
            .putString(KEY_ACCESS, session.accessToken)
            .putString(KEY_REFRESH, session.refreshToken)
            .putString(KEY_SESSION_ID, session.publicSession.sessionId)
            .putString(KEY_USER_ID, session.publicSession.userId)
            .putString(KEY_USERNAME, session.publicSession.username)
            .putString(KEY_MASKED_EMAIL, session.publicSession.maskedEmail)
            .putBoolean(KEY_EMAIL_VERIFIED, session.publicSession.emailVerified)
            .putLong(KEY_EXPIRES_AT, session.publicSession.expiresAtEpochMillis)
            .apply()
    }

    override fun clear() {
        prefs.edit().clear().apply()
    }

    private companion object {
        const val FILE_NAME = "patsy_auth_session"
        const val KEY_ACCESS = "access"
        const val KEY_REFRESH = "refresh"
        const val KEY_SESSION_ID = "session_id"
        const val KEY_USER_ID = "user_id"
        const val KEY_USERNAME = "username"
        const val KEY_MASKED_EMAIL = "masked_email"
        const val KEY_EMAIL_VERIFIED = "email_verified"
        const val KEY_EXPIRES_AT = "expires_at"
    }
}

package com.patsy.app.auth.ui

import com.patsy.app.auth.AuthGateway
import com.patsy.app.auth.LoginSessionRetention
import com.patsy.app.auth.SessionState
import com.patsy.app.auth.SignOutResult

/**
 * Login-screen "Remember Me" is session restoration only.
 *
 * It stores only this Boolean opt-in. Passwords and provider tokens are outside this boundary.
 * The separate Patsy Remember Me paw remains reserved for user-approved saved content.
 */
interface RememberMePreferenceStore {
    suspend fun isSessionRestoreEnabled(): Boolean
    suspend fun setSessionRestoreEnabled(enabled: Boolean)
}

class RememberMeCoordinator(
    private val preferenceStore: RememberMePreferenceStore,
) {
    fun retentionFor(rememberMeEnabled: Boolean): LoginSessionRetention =
        if (rememberMeEnabled) {
            LoginSessionRetention.RESTORE_ON_NEXT_LAUNCH
        } else {
            LoginSessionRetention.CURRENT_PROCESS_ONLY
        }

    suspend fun restoreSession(gateway: AuthGateway): SessionState {
        if (!preferenceStore.isSessionRestoreEnabled()) return SessionState.Anonymous

        val result = gateway.restoreSession()
        when (result) {
            is SessionState.Authenticated,
            is SessionState.Unavailable -> Unit
            SessionState.Anonymous,
            is SessionState.Expired -> preferenceStore.setSessionRestoreEnabled(false)
        }
        return result
    }

    suspend fun recordSuccessfulLogin(rememberMeEnabled: Boolean) {
        preferenceStore.setSessionRestoreEnabled(rememberMeEnabled)
    }

    suspend fun signOut(gateway: AuthGateway): SignOutResult {
        try {
            return gateway.signOut()
        } finally {
            preferenceStore.setSessionRestoreEnabled(false)
        }
    }
}

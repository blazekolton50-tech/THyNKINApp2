package com.patsy.app.auth.ui

import com.patsy.app.auth.AuthFailure
import com.patsy.app.auth.AuthGateway
import com.patsy.app.auth.CompleteRegistrationRequest
import com.patsy.app.auth.ConfirmEmailRequest
import com.patsy.app.auth.EmailConfirmationResult
import com.patsy.app.auth.LoginIdentifier
import com.patsy.app.auth.LoginRequest
import com.patsy.app.auth.LoginResult
import com.patsy.app.auth.LoginSessionRetention
import com.patsy.app.auth.PasswordResetRequest
import com.patsy.app.auth.PasswordResetResult
import com.patsy.app.auth.PublicSession
import com.patsy.app.auth.RegistrationResult
import com.patsy.app.auth.RegistrationStartResult
import com.patsy.app.auth.SecretChars
import com.patsy.app.auth.ServiceFailure
import com.patsy.app.auth.SessionEndReason
import com.patsy.app.auth.SessionState
import com.patsy.app.auth.SignOutResult
import com.patsy.app.auth.StartRegistrationRequest
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class FinalAuthSessionPolicyTest {
    @Test
    fun uncheckedRememberMeSkipsGatewayRestore() = runTest {
        val store = FakePreferenceStore(false)
        val gateway = CountingGateway(SessionState.Authenticated(session()))
        val coordinator = RememberMeCoordinator(store)

        assertIs<SessionState.Anonymous>(coordinator.restoreSession(gateway))
        assertEquals(0, gateway.restoreCalls)
    }

    @Test
    fun checkedRememberMeRestoresExactlyOnce() = runTest {
        val store = FakePreferenceStore(true)
        val gateway = CountingGateway(SessionState.Authenticated(session()))
        val coordinator = RememberMeCoordinator(store)

        assertIs<SessionState.Authenticated>(coordinator.restoreSession(gateway))
        assertEquals(1, gateway.restoreCalls)
        assertTrue(store.enabled)
    }

    @Test
    fun expiredRestoreClearsOptInButOfflineKeepsIt() = runTest {
        val expiredStore = FakePreferenceStore(true)
        RememberMeCoordinator(expiredStore).restoreSession(
            CountingGateway(SessionState.Expired(SessionEndReason.EXPIRED)),
        )
        assertFalse(expiredStore.enabled)

        val offlineStore = FakePreferenceStore(true)
        RememberMeCoordinator(offlineStore).restoreSession(
            CountingGateway(SessionState.Unavailable(ServiceFailure.Offline)),
        )
        assertTrue(offlineStore.enabled)
    }

    @Test
    fun signOutAlwaysClearsOptIn() = runTest {
        val store = FakePreferenceStore(true)
        val result = RememberMeCoordinator(store).signOut(
            CountingGateway(
                restoreResult = SessionState.Anonymous,
                signOutResult = SignOutResult.Unavailable(ServiceFailure.Offline),
            ),
        )

        assertIs<SignOutResult.Unavailable>(result)
        assertFalse(store.enabled)
    }

    @Test
    fun rememberMeChoiceControlsProviderSessionRetention() {
        val coordinator = RememberMeCoordinator(FakePreferenceStore(false))

        assertEquals(
            LoginSessionRetention.CURRENT_PROCESS_ONLY,
            coordinator.retentionFor(false),
        )
        assertEquals(
            LoginSessionRetention.RESTORE_ON_NEXT_LAUNCH,
            coordinator.retentionFor(true),
        )
    }

    @Test
    fun loginRequestNeverPrintsPassword() {
        val password = SecretChars.copyOf("NeverPrintThis!9".toCharArray())
        try {
            val request = LoginRequest(
                identifier = LoginIdentifier.Username("blaze"),
                password = password,
                sessionRetention = LoginSessionRetention.RESTORE_ON_NEXT_LAUNCH,
            )

            assertFalse(request.toString().contains("NeverPrintThis!9"))
            assertTrue(request.toString().contains("[REDACTED]"))
        } finally {
            password.close()
        }
    }

    private fun session() = PublicSession(
        sessionId = "session-1",
        userId = "user-1",
        username = "blaze",
        maskedEmail = "b***@example.com",
        emailVerified = true,
        expiresAtEpochMillis = Long.MAX_VALUE,
    )
}

private class FakePreferenceStore(initiallyEnabled: Boolean) : RememberMePreferenceStore {
    var enabled: Boolean = initiallyEnabled
        private set

    override suspend fun isSessionRestoreEnabled(): Boolean = enabled

    override suspend fun setSessionRestoreEnabled(enabled: Boolean) {
        this.enabled = enabled
    }
}

private class CountingGateway(
    private val restoreResult: SessionState,
    private val signOutResult: SignOutResult = SignOutResult.SignedOut,
) : AuthGateway {
    var restoreCalls = 0
        private set

    override suspend fun startRegistration(request: StartRegistrationRequest) =
        RegistrationStartResult.Unavailable(ServiceFailure.NotConfigured)

    override suspend fun completeRegistration(request: CompleteRegistrationRequest) =
        RegistrationResult.Unavailable(ServiceFailure.NotConfigured)

    override suspend fun confirmEmail(request: ConfirmEmailRequest) =
        EmailConfirmationResult.Unavailable(ServiceFailure.NotConfigured)

    override suspend fun login(request: LoginRequest) =
        LoginResult.Rejected(AuthFailure.InvalidCredentials)

    override suspend fun requestPasswordReset(request: PasswordResetRequest) =
        PasswordResetResult.Unavailable(ServiceFailure.NotConfigured)

    override suspend fun restoreSession(): SessionState {
        restoreCalls += 1
        return restoreResult
    }

    override suspend fun signOut(): SignOutResult = signOutResult
}

package com.patsy.app.auth

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

class SupabaseAuthGatewayIntegrationTest {
    @Test
    fun `successful login persists remote session and returns public session`() = kotlinx.coroutines.test.runTest {
        val remote = remoteSession("first")
        val store = FakeSessionStore()
        val gateway = SupabaseAuthGateway(FakeTransport(loginResult = RemoteAuthResult.Authenticated(remote)), store)

        val result = gateway.login(
            LoginRequest(
                identifier = LoginIdentifier.Username("patsytest"),
                password = SecretChars.copyOf("Example!Pass123".toCharArray()),
                sessionRetention = LoginSessionRetention.RESTORE_ON_NEXT_LAUNCH,
            )
        )

        val authenticated = assertIs<LoginResult.Authenticated>(result)
        assertEquals(remote.userId, authenticated.session.userId)
        assertEquals(remote.refreshToken, store.value?.refreshToken)
    }

    @Test
    fun `restore refreshes stored token and replaces stored session`() = kotlinx.coroutines.test.runTest {
        val store = FakeSessionStore(remoteSession("old").toStored())
        val refreshed = remoteSession("new")
        val gateway = SupabaseAuthGateway(FakeTransport(refreshResult = RemoteAuthResult.Authenticated(refreshed)), store)

        val result = gateway.restoreSession()

        val authenticated = assertIs<SessionState.Authenticated>(result)
        assertEquals("new-session", authenticated.session.sessionId)
        assertEquals("new-refresh", store.value?.refreshToken)
    }

    @Test
    fun `invalid refreshed session clears stored tokens`() = kotlinx.coroutines.test.runTest {
        val store = FakeSessionStore(remoteSession("old").toStored())
        val gateway = SupabaseAuthGateway(
            FakeTransport(refreshResult = RemoteAuthResult.Failure(RemoteAuthFailure.InvalidSession)),
            store,
        )

        val result = gateway.restoreSession()

        assertIs<SessionState.Expired>(result)
        assertNull(store.value)
    }

    @Test
    fun `sign out clears local session even when server is unavailable`() = kotlinx.coroutines.test.runTest {
        val store = FakeSessionStore(remoteSession("old").toStored())
        val gateway = SupabaseAuthGateway(
            FakeTransport(signOutResult = RemoteSignOutResult.Unavailable(ServiceFailure.Offline)),
            store,
        )

        val result = gateway.signOut()

        assertIs<SignOutResult.Unavailable>(result)
        assertNull(store.value)
    }

    private fun remoteSession(prefix: String) = RemoteAuthSession(
        accessToken = "$prefix-access",
        refreshToken = "$prefix-refresh",
        sessionId = "$prefix-session",
        userId = "user-1",
        username = "patsytest",
        maskedEmail = "pa***@example.com",
        emailVerified = true,
        expiresAtEpochMillis = 9_999_999_999L,
    )

    private class FakeSessionStore(initial: StoredAuthSession? = null) : AuthSessionStore {
        var value: StoredAuthSession? = initial
        override fun read(): StoredAuthSession? = value
        override fun write(session: StoredAuthSession) { value = session }
        override fun clear() { value = null }
    }

    private class FakeTransport(
        private val loginResult: RemoteAuthResult = RemoteAuthResult.Failure(RemoteAuthFailure.Unknown),
        private val refreshResult: RemoteAuthResult = RemoteAuthResult.Failure(RemoteAuthFailure.Unknown),
        private val signOutResult: RemoteSignOutResult = RemoteSignOutResult.SignedOut,
    ) : SupabaseAuthTransport {
        override suspend fun login(identifier: LoginIdentifier, password: CharArray): RemoteAuthResult = loginResult
        override suspend fun refresh(refreshToken: String): RemoteAuthResult = refreshResult
        override suspend fun signOut(accessToken: String): RemoteSignOutResult = signOutResult
    }
}

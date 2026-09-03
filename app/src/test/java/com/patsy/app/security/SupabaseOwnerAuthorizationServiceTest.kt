package com.patsy.app.security

import com.patsy.app.auth.AuthSessionStore
import com.patsy.app.auth.PublicSession
import com.patsy.app.auth.ServiceFailure
import com.patsy.app.auth.StoredAuthSession
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class SupabaseOwnerAuthorizationServiceTest {
    private val now = 1_000_000L

    private fun publicSession(
        sessionId: String = "session-1",
        userId: String = "user-1",
        expiresAt: Long = now + 60_000L,
    ) = PublicSession(
        sessionId = sessionId,
        userId = userId,
        username = "owner",
        maskedEmail = "ow***@example.com",
        emailVerified = true,
        expiresAtEpochMillis = expiresAt,
    )

    private class FakeStore(var stored: StoredAuthSession?) : AuthSessionStore {
        override fun read(): StoredAuthSession? = stored
        override fun write(session: StoredAuthSession) { stored = session }
        override fun clear() { stored = null }
    }

    private class FakeTransport(
        var result: RemoteOwnerAuthorizationResult,
    ) : OwnerAuthorizationTransport {
        var calls = 0
        var token: String? = null
        var capability: OwnerCapability? = null

        override suspend fun verify(
            accessToken: String,
            capability: OwnerCapability,
        ): RemoteOwnerAuthorizationResult {
            calls += 1
            token = accessToken
            this.capability = capability
            return result
        }
    }

    @Test
    fun missingStoredSessionFailsClosedWithoutTransportCall() = kotlinx.coroutines.test.runTest {
        val transport = FakeTransport(RemoteOwnerAuthorizationResult.Denied)
        val service = SupabaseOwnerAuthorizationService(
            sessionStore = FakeStore(null),
            transport = transport,
            nowEpochMillis = { now },
        )

        val decision = service.verify(publicSession(), OwnerCapability.VIEW_OWNER_TOOLS)

        assertEquals(0, transport.calls)
        assertEquals(
            OwnerAuthorizationDecision.Denied(OwnerDenialReason.NOT_AUTHENTICATED),
            decision,
        )
    }

    @Test
    fun mismatchedStoredSessionIsTreatedAsRevoked() = kotlinx.coroutines.test.runTest {
        val supplied = publicSession()
        val stored = StoredAuthSession(
            accessToken = "secret-access-token",
            refreshToken = "refresh",
            publicSession = publicSession(sessionId = "other-session"),
        )
        val transport = FakeTransport(RemoteOwnerAuthorizationResult.Denied)
        val service = SupabaseOwnerAuthorizationService(
            sessionStore = FakeStore(stored),
            transport = transport,
            nowEpochMillis = { now },
        )

        val decision = service.verify(supplied, OwnerCapability.VIEW_OWNER_TOOLS)

        assertEquals(0, transport.calls)
        assertEquals(
            OwnerAuthorizationDecision.Denied(OwnerDenialReason.SESSION_REVOKED),
            decision,
        )
    }

    @Test
    fun expiredSessionFailsClosedWithoutTransportCall() = kotlinx.coroutines.test.runTest {
        val supplied = publicSession(expiresAt = now - 1)
        val stored = StoredAuthSession("secret-access-token", "refresh", supplied)
        val transport = FakeTransport(RemoteOwnerAuthorizationResult.Denied)
        val service = SupabaseOwnerAuthorizationService(
            sessionStore = FakeStore(stored),
            transport = transport,
            nowEpochMillis = { now },
        )

        val decision = service.verify(supplied, OwnerCapability.VIEW_OWNER_TOOLS)

        assertEquals(0, transport.calls)
        assertEquals(
            OwnerAuthorizationDecision.Denied(OwnerDenialReason.SESSION_EXPIRED),
            decision,
        )
    }

    @Test
    fun matchingEncryptedSessionForwardsOnlyTokenAndRequestedCapability() = kotlinx.coroutines.test.runTest {
        val supplied = publicSession()
        val stored = StoredAuthSession("secret-access-token", "refresh", supplied)
        val transport = FakeTransport(
            RemoteOwnerAuthorizationResult.Allowed(
                authorizationId = "authz-1",
                capability = OwnerCapability.VIEW_ANALYTICS,
                expiresAtEpochMillis = now + 30_000L,
                auditCorrelationId = "audit-1",
            ),
        )
        val service = SupabaseOwnerAuthorizationService(
            sessionStore = FakeStore(stored),
            transport = transport,
            nowEpochMillis = { now },
        )

        val decision = service.verify(supplied, OwnerCapability.VIEW_ANALYTICS)

        assertEquals(1, transport.calls)
        assertEquals("secret-access-token", transport.token)
        assertEquals(OwnerCapability.VIEW_ANALYTICS, transport.capability)
        val allowed = assertIs<OwnerAuthorizationDecision.Allowed>(decision)
        assertEquals("authz-1", allowed.authorizationId)
        assertEquals(OwnerCapability.VIEW_ANALYTICS, allowed.capability)
        assertEquals("audit-1", allowed.auditCorrelationId)
    }

    @Test
    fun serverDenialAndUnavailableStayFailClosed() = kotlinx.coroutines.test.runTest {
        val supplied = publicSession()
        val store = FakeStore(StoredAuthSession("token", "refresh", supplied))
        val deniedTransport = FakeTransport(RemoteOwnerAuthorizationResult.Denied)
        val denied = SupabaseOwnerAuthorizationService(store, deniedTransport) { now }
            .verify(supplied, OwnerCapability.MANAGE_PRIVACY)
        assertEquals(OwnerAuthorizationDecision.Denied(OwnerDenialReason.NOT_OWNER), denied)

        val unavailableTransport = FakeTransport(
            RemoteOwnerAuthorizationResult.Unavailable(ServiceFailure.Timeout),
        )
        val unavailable = SupabaseOwnerAuthorizationService(store, unavailableTransport) { now }
            .verify(supplied, OwnerCapability.MANAGE_PRIVACY)
        assertEquals(OwnerAuthorizationDecision.Unavailable(ServiceFailure.Timeout), unavailable)
    }
}

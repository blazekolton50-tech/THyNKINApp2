package com.patsy.app.studio

import com.patsy.app.auth.AuthSessionStore
import com.patsy.app.auth.PublicSession
import com.patsy.app.auth.StoredAuthSession
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlinx.coroutines.test.runTest

class StudioProjectServiceTest {
    private val session = PublicSession(
        sessionId = "studio-project-session",
        userId = "00000000-0000-0000-0000-000000000001",
        username = "patsy_creator",
        maskedEmail = "p***@example.com",
        emailVerified = true,
        expiresAtEpochMillis = Long.MAX_VALUE,
    )

    @Test
    fun createReturnsProjectOnlyAfterServerConfirmsUuid() = runTest {
        val transport = FakeStudioProjectTransport(
            RemoteStudioProjectCreateResult.Created(
                projectId = "00000000-0000-0000-0000-000000000010",
                name = "Untitled design",
            ),
        )
        val service = ServerStudioProjectService(
            sessionStore = FakeStudioProjectSessionStore(session),
            transport = transport,
        )

        val result = assertIs<StudioProjectCreateResult.Created>(
            service.create(session, "Untitled design"),
        )

        assertEquals("00000000-0000-0000-0000-000000000010", result.projectId)
        assertEquals("Untitled design", result.name)
        assertEquals("access", transport.lastAccessToken)
        assertEquals(session.userId, transport.lastUserId)
        assertEquals("Untitled design", transport.lastName)
    }

    @Test
    fun createRejectsBlankOrOverlongNamesWithoutRemoteWrite() = runTest {
        val transport = FakeStudioProjectTransport(RemoteStudioProjectCreateResult.Unavailable)
        val service = ServerStudioProjectService(
            sessionStore = FakeStudioProjectSessionStore(session),
            transport = transport,
        )

        assertIs<StudioProjectCreateResult.InvalidName>(service.create(session, "   "))
        assertIs<StudioProjectCreateResult.InvalidName>(service.create(session, "x".repeat(121)))
        assertEquals(null, transport.lastName)
    }

    @Test
    fun createSessionMismatchFailsClosedWithoutRemoteWrite() = runTest {
        val transport = FakeStudioProjectTransport(RemoteStudioProjectCreateResult.Unavailable)
        val service = ServerStudioProjectService(
            sessionStore = FakeStudioProjectSessionStore(session.copy(sessionId = "wrong")),
            transport = transport,
        )

        assertIs<StudioProjectCreateResult.Unauthorized>(service.create(session, "Untitled design"))
        assertEquals(null, transport.lastName)
    }
}

private class FakeStudioProjectSessionStore(session: PublicSession) : AuthSessionStore {
    private var stored: StoredAuthSession? = StoredAuthSession("access", "refresh", session)
    override fun read(): StoredAuthSession? = stored
    override fun write(session: StoredAuthSession) { stored = session }
    override fun clear() { stored = null }
}

private class FakeStudioProjectTransport(
    private val result: RemoteStudioProjectCreateResult,
) : StudioProjectTransport {
    var lastAccessToken: String? = null
    var lastUserId: String? = null
    var lastName: String? = null

    override suspend fun create(accessToken: String, userId: String, name: String): RemoteStudioProjectCreateResult {
        lastAccessToken = accessToken
        lastUserId = userId
        lastName = name
        return result
    }
}

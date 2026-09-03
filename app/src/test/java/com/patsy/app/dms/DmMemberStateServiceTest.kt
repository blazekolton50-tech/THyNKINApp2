package com.patsy.app.dms

import com.patsy.app.auth.AuthSessionStore
import com.patsy.app.auth.PublicSession
import com.patsy.app.auth.StoredAuthSession
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class DmMemberStateServiceTest {
    private val session = PublicSession(
        sessionId = "session-dm-state",
        userId = "00000000-0000-0000-0000-000000000001",
        username = "patsy_creator",
        maskedEmail = "p***@example.com",
        emailVerified = true,
        expiresAtEpochMillis = Long.MAX_VALUE,
    )

    @Test
    fun markReadUsesAuthenticatedStoredSessionAndServerTime() = runTest {
        val transport = FakeDmMemberStateTransport()
        val service = ServerDmMemberStateService(
            sessionStore = FakeDmMemberStateSessionStore(session),
            transport = transport,
        )

        val result = service.markRead(session, "thread-1")

        assertIs<DmMemberStateResult.Updated>(result)
        assertEquals("thread-1", transport.lastReadThreadId)
        assertEquals("access-token", transport.lastAccessToken)
    }

    @Test
    fun archiveUsesAuthenticatedStoredSessionAndRequestedState() = runTest {
        val transport = FakeDmMemberStateTransport()
        val service = ServerDmMemberStateService(
            sessionStore = FakeDmMemberStateSessionStore(session),
            transport = transport,
        )

        val result = service.setArchived(session, "thread-2", archived = true)

        assertIs<DmMemberStateResult.Updated>(result)
        assertEquals("thread-2", transport.lastArchiveThreadId)
        assertEquals(true, transport.lastArchived)
    }

    @Test
    fun mismatchedStoredSessionFailsClosedWithoutMutation() = runTest {
        val transport = FakeDmMemberStateTransport()
        val service = ServerDmMemberStateService(
            sessionStore = FakeDmMemberStateSessionStore(session.copy(sessionId = "other")),
            transport = transport,
        )

        assertIs<DmMemberStateResult.Unauthorized>(service.markRead(session, "thread-1"))
        assertEquals(null, transport.lastReadThreadId)
        assertEquals(null, transport.lastArchiveThreadId)
    }

    @Test
    fun httpStatusMappingNeverTreatsFailureAsUpdated() {
        assertIs<DmMemberStateResult.Updated>(dmMemberStateResultForStatus(200))
        assertIs<DmMemberStateResult.Updated>(dmMemberStateResultForStatus(204))
        assertIs<DmMemberStateResult.Unauthorized>(dmMemberStateResultForStatus(401))
        assertIs<DmMemberStateResult.Unauthorized>(dmMemberStateResultForStatus(403))
        assertIs<DmMemberStateResult.Unavailable>(dmMemberStateResultForStatus(400))
        assertIs<DmMemberStateResult.Unavailable>(dmMemberStateResultForStatus(500))
    }

    @Test
    fun onlyServerAcceptedMemberStateMayRefreshInboxTruth() {
        assertTrue(shouldRefreshInboxAfterMemberState(DmMemberStateResult.Updated))
        assertFalse(shouldRefreshInboxAfterMemberState(DmMemberStateResult.Unauthorized))
        assertFalse(shouldRefreshInboxAfterMemberState(DmMemberStateResult.Unavailable))
    }

    @Test
    fun markReadPayloadContainsOnlyThreadAndAction() {
        assertEquals(
            "{\"thread_id\":\"00000000-0000-0000-0000-000000000010\",\"action\":\"mark_read\"}",
            dmMemberStatePayload("00000000-0000-0000-0000-000000000010", archived = null),
        )
    }

    @Test
    fun archivePayloadCarriesExplicitRequestedBoolean() {
        assertEquals(
            "{\"thread_id\":\"00000000-0000-0000-0000-000000000020\",\"action\":\"set_archived\",\"archived\":false}",
            dmMemberStatePayload("00000000-0000-0000-0000-000000000020", archived = false),
        )
    }
}

private class FakeDmMemberStateSessionStore(session: PublicSession) : AuthSessionStore {
    private var stored: StoredAuthSession? = StoredAuthSession("access-token", "refresh-token", session)
    override fun read(): StoredAuthSession? = stored
    override fun write(session: StoredAuthSession) { stored = session }
    override fun clear() { stored = null }
}

private class FakeDmMemberStateTransport : DmMemberStateTransport {
    var lastAccessToken: String? = null
    var lastReadThreadId: String? = null
    var lastArchiveThreadId: String? = null
    var lastArchived: Boolean? = null

    override suspend fun markRead(accessToken: String, threadId: String): DmMemberStateResult {
        lastAccessToken = accessToken
        lastReadThreadId = threadId
        return DmMemberStateResult.Updated
    }

    override suspend fun setArchived(accessToken: String, threadId: String, archived: Boolean): DmMemberStateResult {
        lastAccessToken = accessToken
        lastArchiveThreadId = threadId
        lastArchived = archived
        return DmMemberStateResult.Updated
    }
}

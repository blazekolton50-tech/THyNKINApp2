package com.patsy.app.dms

import com.patsy.app.auth.AuthSessionStore
import com.patsy.app.auth.PublicSession
import com.patsy.app.auth.StoredAuthSession
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlinx.coroutines.test.runTest

class DmConversationServiceTest {
    private val session = PublicSession(
        sessionId = "dm-conversation-session",
        userId = "00000000-0000-0000-0000-000000000001",
        username = "patsy_creator",
        maskedEmail = "p***@example.com",
        emailVerified = true,
        expiresAtEpochMillis = Long.MAX_VALUE,
    )

    @Test
    fun loadKeepsOnlyNonExpiredServerMessages() = runTest {
        val now = 1_000L
        val service = ServerDmConversationService(
            sessionStore = FakeConversationSessionStore(session),
            transport = FakeConversationTransport(
                loadResult = RemoteConversationResult.Loaded(
                    listOf(
                        RemoteDmMessage("m1", "t1", session.userId, "keep", 100L, 2_000L),
                        RemoteDmMessage("m2", "t1", session.userId, "expired", 200L, 900L),
                    ),
                ),
            ),
            nowEpochMillis = { now },
        )

        val loaded = assertIs<DmConversationResult.Loaded>(service.load(session, "t1"))
        assertEquals(listOf("m1"), loaded.messages.map { it.id })
    }

    @Test
    fun sendAcceptsOnlyServerReturnedMessageForSameCallerAndThread() = runTest {
        val transport = FakeConversationTransport(
            sendResult = RemoteSendMessageResult.Accepted(
                RemoteDmMessage("m3", "t1", session.userId, "Hello", 300L, 10_000L),
            ),
        )
        val service = ServerDmConversationService(
            sessionStore = FakeConversationSessionStore(session),
            transport = transport,
        )

        val result = assertIs<DmSendResult.Accepted>(service.send(session, "t1", "  Hello  "))
        assertEquals("Hello", result.message.body)
        assertEquals("Hello", transport.lastBody)
    }

    @Test
    fun blankMessageIsRejectedBeforeTransport() = runTest {
        val transport = FakeConversationTransport()
        val service = ServerDmConversationService(FakeConversationSessionStore(session), transport)

        assertIs<DmSendResult.InvalidInput>(service.send(session, "t1", "   "))
        assertEquals(null, transport.lastBody)
    }
}

private class FakeConversationSessionStore(session: PublicSession) : AuthSessionStore {
    private var stored: StoredAuthSession? = StoredAuthSession("access", "refresh", session)
    override fun read(): StoredAuthSession? = stored
    override fun write(session: StoredAuthSession) { stored = session }
    override fun clear() { stored = null }
}

private class FakeConversationTransport(
    private val loadResult: RemoteConversationResult = RemoteConversationResult.Loaded(emptyList()),
    private val sendResult: RemoteSendMessageResult = RemoteSendMessageResult.Unavailable,
) : DmConversationTransport {
    var lastBody: String? = null
    override suspend fun load(accessToken: String, threadId: String): RemoteConversationResult = loadResult
    override suspend fun send(accessToken: String, userId: String, threadId: String, body: String): RemoteSendMessageResult {
        lastBody = body
        return sendResult
    }
}

package com.patsy.app.dms

import com.patsy.app.auth.AuthSessionStore
import com.patsy.app.auth.PublicSession
import com.patsy.app.auth.StoredAuthSession
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlinx.coroutines.test.runTest

class DmDataServiceTest {
    private val session = PublicSession(
        sessionId = "session-dm",
        userId = "00000000-0000-0000-0000-000000000001",
        username = "patsy_creator",
        maskedEmail = "p***@example.com",
        emailVerified = true,
        expiresAtEpochMillis = Long.MAX_VALUE,
    )

    @Test
    fun loadPreservesOnlyServerBackedDmFacts() = runTest {
        val service = ServerDmDataService(
            sessionStore = FakeDmSessionStore(session),
            transport = FakeDmTransport(
                RemoteDmDataResult.Loaded(
                    threads = listOf(
                        RemoteDmThread(
                            id = "thread-1",
                            updatedAtEpochMillis = 200L,
                            title = "Darcy",
                            avatarPath = "avatars/darcy.png",
                            isGroup = false,
                            isFriend = true,
                            participantCount = 2,
                            unreadCount = null,
                            archived = null,
                            lastMessage = RemoteDmMessage(
                                id = "message-1",
                                threadId = "thread-1",
                                senderId = session.userId,
                                body = "Hello",
                                createdAtEpochMillis = 150L,
                                expiresAtEpochMillis = 500L,
                            ),
                        ),
                    ),
                ),
            ),
        )

        val loaded = assertIs<DmDataResult.Loaded>(service.load(session))
        assertEquals("thread-1", loaded.threads.single().id)
        assertEquals("Hello", loaded.threads.single().lastMessage?.body)
        assertEquals("Darcy", loaded.threads.single().title)
        assertEquals(false, loaded.threads.single().isGroup)
        assertEquals(true, loaded.threads.single().isFriend)
        assertEquals(null, loaded.threads.single().unreadCount)
        assertEquals(null, loaded.threads.single().archived)
    }

    @Test
    fun mismatchedStoredSessionFailsClosedBeforeTransport() = runTest {
        val other = session.copy(sessionId = "other")
        val service = ServerDmDataService(
            sessionStore = FakeDmSessionStore(other),
            transport = FakeDmTransport(RemoteDmDataResult.Unavailable),
        )

        assertIs<DmDataResult.Unauthorized>(service.load(session))
    }
}

private class FakeDmSessionStore(session: PublicSession) : AuthSessionStore {
    private var stored: StoredAuthSession? = StoredAuthSession("access-token", "refresh-token", session)
    override fun read(): StoredAuthSession? = stored
    override fun write(session: StoredAuthSession) { stored = session }
    override fun clear() { stored = null }
}

private class FakeDmTransport(
    private val result: RemoteDmDataResult,
) : DmDataTransport {
    override suspend fun fetch(accessToken: String, userId: String): RemoteDmDataResult = result
}

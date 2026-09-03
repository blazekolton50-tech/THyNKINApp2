package com.patsy.app.profile

import com.patsy.app.auth.AuthSessionStore
import com.patsy.app.auth.PublicSession
import com.patsy.app.auth.StoredAuthSession
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlinx.coroutines.test.runTest

class ProfileDataServiceTest {
    private val session = PublicSession(
        sessionId = "session-1",
        userId = "00000000-0000-0000-0000-000000000001",
        username = "patsy_creator",
        maskedEmail = "p***@example.com",
        emailVerified = true,
        expiresAtEpochMillis = Long.MAX_VALUE,
    )

    @Test
    fun loadedProfileUsesServerProfileAndRealProjectCountWithoutInventingSocialMetrics() = runTest {
        val service = ServerProfileDataService(
            sessionStore = FakeSessionStore(session),
            transport = FakeProfileTransport(
                RemoteProfileDataResult.Loaded(
                    profile = RemoteProfileRow(
                        userId = session.userId,
                        username = "patsy_creator",
                        displayName = "Patsy",
                        avatarPath = "avatars/patsy.png",
                    ),
                    projects = listOf(
                        RemoteProfileProjectRow("p1", "Poster One", false, 300L),
                        RemoteProfileProjectRow("p2", "Music Idea", true, 200L),
                    ),
                ),
            ),
        )

        val result = service.load(session)
        val loaded = assertIs<ProfileDataResult.Loaded>(result)

        assertEquals("Patsy", loaded.presentation.displayName)
        assertEquals("@patsy_creator", loaded.presentation.username)
        assertEquals("avatars/patsy.png", loaded.presentation.avatarUri)
        assertEquals("2", loaded.presentation.metrics.first { it.label == "Projects" }.formattedValue)
        assertEquals(null, loaded.presentation.metrics.first { it.label == "Followers" }.count)
        assertEquals(listOf("Poster One", "Music Idea"), loaded.presentation.recentProjects.map { it.title })
        assertEquals(emptyList(), loaded.presentation.savedProjects)
    }

    @Test
    fun staleOrDifferentStoredSessionFailsClosed() = runTest {
        val other = session.copy(sessionId = "other-session")
        val service = ServerProfileDataService(
            sessionStore = FakeSessionStore(other),
            transport = FakeProfileTransport(RemoteProfileDataResult.Unavailable),
        )

        assertIs<ProfileDataResult.Unauthorized>(service.load(session))
    }
}

private class FakeSessionStore(session: PublicSession) : AuthSessionStore {
    private var stored: StoredAuthSession? = StoredAuthSession("access-token", "refresh-token", session)
    override fun read(): StoredAuthSession? = stored
    override fun write(session: StoredAuthSession) { stored = session }
    override fun clear() { stored = null }
}

private class FakeProfileTransport(
    private val result: RemoteProfileDataResult,
) : ProfileDataTransport {
    override suspend fun fetch(accessToken: String, userId: String): RemoteProfileDataResult = result
}

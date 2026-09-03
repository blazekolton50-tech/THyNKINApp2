package com.patsy.app.studio

import com.patsy.app.auth.AuthSessionStore
import com.patsy.app.auth.PublicSession
import com.patsy.app.auth.StoredAuthSession
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlinx.coroutines.test.runTest

class StudioPersistenceServiceTest {
    private val session = PublicSession(
        sessionId = "studio-session",
        userId = "00000000-0000-0000-0000-000000000001",
        username = "patsy_creator",
        maskedEmail = "p***@example.com",
        emailVerified = true,
        expiresAtEpochMillis = Long.MAX_VALUE,
    )

    @Test
    fun loadReturnsOnlyServerBackedProjectStateLayersAndRevision() = runTest {
        val service = ServerStudioPersistenceService(
            sessionStore = FakeStudioSessionStore(session),
            transport = FakeStudioTransport(
                loadResult = RemoteStudioPersistenceResult.Loaded(
                    state = StudioProjectPersistenceState(
                        projectId = "p1",
                        editorMode = "image",
                        canvasWidthPx = 1080,
                        canvasHeightPx = 1080,
                        durationMs = 0,
                        fps = 30.0,
                        autosaveRevision = 4,
                        lastAutosavedAtEpochMillis = 500L,
                    ),
                    layers = listOf(
                        StudioLayerPersistenceRecord(
                            id = "l1",
                            projectId = "p1",
                            parentLayerId = null,
                            layerType = "text",
                            name = "Title",
                            zIndex = 2,
                            startMs = 0,
                            endMs = null,
                            isLocked = false,
                            isHidden = false,
                            opacity = 1.0,
                            transformJson = "{}",
                            cropJson = null,
                            styleJson = "{}",
                            effectsJson = "[]",
                            contentJson = "{\"text\":\"Hello\"}",
                        ),
                    ),
                    latestRevision = StudioRevisionRecord("r4", "p1", 4, "autosave", "{}", 500L),
                ),
            ),
        )

        val loaded = assertIs<StudioPersistenceResult.Loaded>(service.load(session, "p1"))
        assertEquals(4, loaded.state.autosaveRevision)
        assertEquals("Title", loaded.layers.single().name)
        assertEquals(4, loaded.latestRevision?.revisionNo)
    }

    @Test
    fun autosaveReturnsSavedOnlyWhenRemoteWriterConfirmsIt() = runTest {
        val transport = FakeStudioTransport(
            loadResult = RemoteStudioPersistenceResult.Unavailable,
            saveResult = RemoteStudioSaveResult.Saved(revisionNo = 5, savedAtEpochMillis = 900L),
        )
        val service = ServerStudioPersistenceService(
            sessionStore = FakeStudioSessionStore(session),
            transport = transport,
        )
        val request = sampleAutosaveRequest()

        val saved = assertIs<StudioSaveResult.Saved>(service.autosave(session, request))

        assertEquals(5, saved.revisionNo)
        assertEquals(900L, saved.savedAtEpochMillis)
        assertEquals("access", transport.lastSaveAccessToken)
        assertEquals(session.userId, transport.lastSaveUserId)
        assertEquals(request, transport.lastSaveRequest)
    }

    @Test
    fun autosaveSessionMismatchFailsClosedWithoutRemoteWrite() = runTest {
        val transport = FakeStudioTransport(RemoteStudioPersistenceResult.Unavailable)
        val service = ServerStudioPersistenceService(
            sessionStore = FakeStudioSessionStore(session.copy(sessionId = "wrong")),
            transport = transport,
        )

        assertIs<StudioSaveResult.Unauthorized>(service.autosave(session, sampleAutosaveRequest()))
        assertEquals(null, transport.lastSaveRequest)
    }

    @Test
    fun sessionMismatchFailsClosed() = runTest {
        val service = ServerStudioPersistenceService(
            sessionStore = FakeStudioSessionStore(session.copy(sessionId = "wrong")),
            transport = FakeStudioTransport(RemoteStudioPersistenceResult.Unavailable),
        )
        assertIs<StudioPersistenceResult.Unauthorized>(service.load(session, "p1"))
    }

    private fun sampleAutosaveRequest() = StudioAutosaveRequest(
        projectId = "00000000-0000-0000-0000-000000000010",
        editorMode = "image",
        canvasWidthPx = 1080,
        canvasHeightPx = 1080,
        durationMs = 0,
        fps = 30.0,
        layers = emptyList(),
        snapshotJson = "{}",
    )
}

private class FakeStudioSessionStore(session: PublicSession) : AuthSessionStore {
    private var stored: StoredAuthSession? = StoredAuthSession("access", "refresh", session)
    override fun read(): StoredAuthSession? = stored
    override fun write(session: StoredAuthSession) { stored = session }
    override fun clear() { stored = null }
}

private class FakeStudioTransport(
    private val loadResult: RemoteStudioPersistenceResult,
    private val saveResult: RemoteStudioSaveResult = RemoteStudioSaveResult.Unavailable,
) : StudioPersistenceTransport {
    var lastSaveAccessToken: String? = null
    var lastSaveUserId: String? = null
    var lastSaveRequest: StudioAutosaveRequest? = null

    override suspend fun load(accessToken: String, userId: String, projectId: String): RemoteStudioPersistenceResult = loadResult

    override suspend fun autosave(
        accessToken: String,
        userId: String,
        request: StudioAutosaveRequest,
    ): RemoteStudioSaveResult {
        lastSaveAccessToken = accessToken
        lastSaveUserId = userId
        lastSaveRequest = request
        return saveResult
    }
}

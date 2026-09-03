package com.patsy.app.storage

import com.patsy.app.auth.AuthSessionStore
import com.patsy.app.auth.PublicSession
import com.patsy.app.auth.StoredAuthSession
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlinx.coroutines.test.runTest

class StorageDataServiceTest {
    private val session = PublicSession(
        sessionId = "storage-session",
        userId = "00000000-0000-0000-0000-000000000001",
        username = "patsy_creator",
        maskedEmail = "p***@example.com",
        emailVerified = true,
        expiresAtEpochMillis = Long.MAX_VALUE,
    )

    @Test
    fun storageLoadPreservesServerLockExpiryAndSavedFacts() = runTest {
        val service = ServerStorageDataService(
            sessionStore = FakeStorageSessionStore(session),
            transport = FakeStorageTransport(
                RemoteStorageDataResult.Loaded(
                    albums = listOf(RemoteAlbumRow("a1", "Ideas", null, 20L)),
                    media = listOf(
                        RemoteMediaAssetRow(
                            id = "m1",
                            projectId = null,
                            albumId = "a1",
                            mediaType = "image",
                            storageProvider = "device",
                            bucket = null,
                            objectPath = null,
                            deviceUri = "content://example/1",
                            mimeType = "image/png",
                            byteSize = 1234L,
                            isLocked = true,
                            expiresAtEpochMillis = null,
                            updatedAtEpochMillis = 30L,
                        ),
                    ),
                    savedMediaIds = setOf("m1"),
                ),
            ),
        )

        val loaded = assertIs<StorageDataResult.Loaded>(service.load(session))
        assertEquals("Ideas", loaded.albums.single().name)
        assertEquals(true, loaded.media.single().isLocked)
        assertEquals(true, loaded.media.single().savedToProfile)
        assertEquals(null, loaded.media.single().expiresAtEpochMillis)
        assertEquals("content://example/1", loaded.media.single().deviceUri)
    }

    @Test
    fun wrongStoredSessionFailsClosed() = runTest {
        val service = ServerStorageDataService(
            sessionStore = FakeStorageSessionStore(session.copy(userId = "00000000-0000-0000-0000-000000000002")),
            transport = FakeStorageTransport(RemoteStorageDataResult.Unavailable),
        )
        assertIs<StorageDataResult.Unauthorized>(service.load(session))
    }
}

private class FakeStorageSessionStore(session: PublicSession) : AuthSessionStore {
    private var stored: StoredAuthSession? = StoredAuthSession("access", "refresh", session)
    override fun read(): StoredAuthSession? = stored
    override fun write(session: StoredAuthSession) { stored = session }
    override fun clear() { stored = null }
}

private class FakeStorageTransport(
    private val result: RemoteStorageDataResult,
) : StorageDataTransport {
    override suspend fun fetch(accessToken: String, userId: String): RemoteStorageDataResult = result
}

package com.patsy.app.storage

import com.patsy.app.auth.AuthSessionStore
import com.patsy.app.auth.PublicSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URLEncoder
import java.net.URL

sealed interface StorageDataResult {
    data class Loaded(val albums: List<AlbumRecord>, val media: List<MediaAssetRecord>) : StorageDataResult
    data object Unauthorized : StorageDataResult
    data object Unavailable : StorageDataResult
}

data class AlbumRecord(val id: String, val name: String, val projectId: String?, val updatedAtEpochMillis: Long)

data class MediaAssetRecord(
    val id: String,
    val projectId: String?,
    val albumId: String?,
    val mediaType: String,
    val storageProvider: String,
    val bucket: String?,
    val objectPath: String?,
    val deviceUri: String?,
    val mimeType: String?,
    val byteSize: Long?,
    val isLocked: Boolean,
    val expiresAtEpochMillis: Long?,
    val updatedAtEpochMillis: Long,
    val savedToProfile: Boolean,
)

data class RemoteAlbumRow(val id: String, val name: String, val projectId: String?, val updatedAtEpochMillis: Long)

data class RemoteMediaAssetRow(
    val id: String,
    val projectId: String?,
    val albumId: String?,
    val mediaType: String,
    val storageProvider: String,
    val bucket: String?,
    val objectPath: String?,
    val deviceUri: String?,
    val mimeType: String?,
    val byteSize: Long?,
    val isLocked: Boolean,
    val expiresAtEpochMillis: Long?,
    val updatedAtEpochMillis: Long,
)

sealed interface RemoteStorageDataResult {
    data class Loaded(
        val albums: List<RemoteAlbumRow>,
        val media: List<RemoteMediaAssetRow>,
        val savedMediaIds: Set<String>,
    ) : RemoteStorageDataResult
    data object Unauthorized : RemoteStorageDataResult
    data object Unavailable : RemoteStorageDataResult
}

interface StorageDataTransport {
    suspend fun fetch(accessToken: String, userId: String): RemoteStorageDataResult
}

interface StorageDataService {
    suspend fun load(session: PublicSession): StorageDataResult
}

class ServerStorageDataService(
    private val sessionStore: AuthSessionStore,
    private val transport: StorageDataTransport,
) : StorageDataService {
    override suspend fun load(session: PublicSession): StorageDataResult {
        val stored = sessionStore.read() ?: return StorageDataResult.Unauthorized
        if (stored.publicSession.sessionId != session.sessionId || stored.publicSession.userId != session.userId || stored.accessToken.isBlank()) {
            return StorageDataResult.Unauthorized
        }
        return when (val remote = transport.fetch(stored.accessToken, session.userId)) {
            is RemoteStorageDataResult.Loaded -> StorageDataResult.Loaded(
                albums = remote.albums.sortedByDescending { it.updatedAtEpochMillis }.map {
                    AlbumRecord(it.id, it.name, it.projectId, it.updatedAtEpochMillis)
                },
                media = remote.media.sortedByDescending { it.updatedAtEpochMillis }.map {
                    MediaAssetRecord(
                        id = it.id,
                        projectId = it.projectId,
                        albumId = it.albumId,
                        mediaType = it.mediaType,
                        storageProvider = it.storageProvider,
                        bucket = it.bucket,
                        objectPath = it.objectPath,
                        deviceUri = it.deviceUri,
                        mimeType = it.mimeType,
                        byteSize = it.byteSize,
                        isLocked = it.isLocked,
                        expiresAtEpochMillis = it.expiresAtEpochMillis,
                        updatedAtEpochMillis = it.updatedAtEpochMillis,
                        savedToProfile = it.id in remote.savedMediaIds,
                    )
                },
            )
            RemoteStorageDataResult.Unauthorized -> StorageDataResult.Unauthorized
            RemoteStorageDataResult.Unavailable -> StorageDataResult.Unavailable
        }
    }
}

class SupabaseStorageDataTransport(
    baseUrl: String,
    private val publishableKey: String,
) : StorageDataTransport {
    private val restBase = "${baseUrl.trimEnd('/')}/rest/v1"

    override suspend fun fetch(accessToken: String, userId: String): RemoteStorageDataResult = withContext(Dispatchers.IO) {
        try {
            val uid = URLEncoder.encode(userId, Charsets.UTF_8.name())
            val albumsResponse = get("$restBase/albums?select=id,project_id,name,updated_at&user_id=eq.$uid&order=updated_at.desc", accessToken)
            val mediaResponse = get("$restBase/media_assets?select=id,project_id,album_id,media_type,storage_provider,bucket,object_path,device_uri,mime_type,byte_size,is_locked,expires_at,updated_at&user_id=eq.$uid&order=updated_at.desc", accessToken)
            val savedResponse = get("$restBase/profile_saved_media?select=media_id&user_id=eq.$uid", accessToken)
            val responses = listOf(albumsResponse, mediaResponse, savedResponse)
            if (responses.any { it.status == 401 || it.status == 403 }) return@withContext RemoteStorageDataResult.Unauthorized
            if (responses.any { it.status !in 200..299 }) return@withContext RemoteStorageDataResult.Unavailable

            val albumsJson = JSONArray(albumsResponse.body)
            val albums = buildList {
                repeat(albumsJson.length()) {
                    val item = albumsJson.getJSONObject(it)
                    add(RemoteAlbumRow(item.getString("id"), item.optString("name").ifBlank { "Untitled album" }, item.optNullableString("project_id"), parseIsoMillis(item.optString("updated_at"))))
                }
            }
            val mediaJson = JSONArray(mediaResponse.body)
            val media = buildList {
                repeat(mediaJson.length()) {
                    val item = mediaJson.getJSONObject(it)
                    add(
                        RemoteMediaAssetRow(
                            id = item.getString("id"),
                            projectId = item.optNullableString("project_id"),
                            albumId = item.optNullableString("album_id"),
                            mediaType = item.optString("media_type"),
                            storageProvider = item.optString("storage_provider"),
                            bucket = item.optNullableString("bucket"),
                            objectPath = item.optNullableString("object_path"),
                            deviceUri = item.optNullableString("device_uri"),
                            mimeType = item.optNullableString("mime_type"),
                            byteSize = if (item.has("byte_size") && !item.isNull("byte_size")) item.getLong("byte_size") else null,
                            isLocked = item.optBoolean("is_locked", false),
                            expiresAtEpochMillis = item.optNullableString("expires_at")?.let(::parseIsoMillis),
                            updatedAtEpochMillis = parseIsoMillis(item.optString("updated_at")),
                        ),
                    )
                }
            }
            val savedJson = JSONArray(savedResponse.body)
            val savedIds = buildSet { repeat(savedJson.length()) { add(savedJson.getJSONObject(it).getString("media_id")) } }
            RemoteStorageDataResult.Loaded(albums, media, savedIds)
        } catch (_: Exception) {
            RemoteStorageDataResult.Unavailable
        }
    }

    private data class Response(val status: Int, val body: String)

    private fun get(url: String, accessToken: String): Response {
        val connection = (URL(url).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 15_000
            readTimeout = 20_000
            useCaches = false
            setRequestProperty("Accept", "application/json")
            setRequestProperty("apikey", publishableKey)
            setRequestProperty("Authorization", "Bearer $accessToken")
        }
        return try {
            val status = connection.responseCode
            val stream = if (status in 200..299) connection.inputStream else connection.errorStream
            Response(status, stream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }.orEmpty())
        } finally {
            connection.disconnect()
        }
    }

    private fun JSONObject.optNullableString(name: String): String? =
        if (!has(name) || isNull(name)) null else optString(name).takeIf { it.isNotBlank() }

    private fun parseIsoMillis(value: String): Long = try {
        java.time.Instant.parse(value).toEpochMilli()
    } catch (_: Exception) {
        0L
    }
}

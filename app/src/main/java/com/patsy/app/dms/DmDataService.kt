package com.patsy.app.dms

import com.patsy.app.auth.AuthSessionStore
import com.patsy.app.auth.PublicSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

sealed interface DmDataResult {
    data class Loaded(val threads: List<DmThreadRecord>) : DmDataResult
    data object Unauthorized : DmDataResult
    data object Unavailable : DmDataResult
}

data class DmThreadRecord(
    val id: String,
    val updatedAtEpochMillis: Long,
    val lastMessage: RemoteDmMessage?,
    val title: String? = null,
    val avatarPath: String? = null,
    val participantCount: Int? = null,
    val unreadCount: Int? = null,
    val isGroup: Boolean? = null,
    val isFriend: Boolean? = null,
    val archived: Boolean? = null,
)

data class RemoteDmThread(
    val id: String,
    val updatedAtEpochMillis: Long,
    val title: String?,
    val avatarPath: String?,
    val isGroup: Boolean?,
    val isFriend: Boolean?,
    val participantCount: Int?,
    val unreadCount: Int?,
    val archived: Boolean?,
    val lastMessage: RemoteDmMessage?,
)

data class RemoteDmMessage(
    val id: String,
    val threadId: String,
    val senderId: String,
    val body: String,
    val createdAtEpochMillis: Long,
    val expiresAtEpochMillis: Long?,
)

sealed interface RemoteDmDataResult {
    data class Loaded(val threads: List<RemoteDmThread>) : RemoteDmDataResult
    data object Unauthorized : RemoteDmDataResult
    data object Unavailable : RemoteDmDataResult
}

interface DmDataTransport {
    suspend fun fetch(accessToken: String, userId: String): RemoteDmDataResult
}

interface DmDataService {
    suspend fun load(session: PublicSession): DmDataResult
}

class ServerDmDataService(
    private val sessionStore: AuthSessionStore,
    private val transport: DmDataTransport,
) : DmDataService {
    override suspend fun load(session: PublicSession): DmDataResult {
        val stored = sessionStore.read() ?: return DmDataResult.Unauthorized
        if (
            stored.publicSession.sessionId != session.sessionId ||
            stored.publicSession.userId != session.userId ||
            stored.accessToken.isBlank()
        ) return DmDataResult.Unauthorized

        return when (val result = transport.fetch(stored.accessToken, session.userId)) {
            is RemoteDmDataResult.Loaded -> DmDataResult.Loaded(
                result.threads
                    .sortedByDescending { it.lastMessage?.createdAtEpochMillis ?: it.updatedAtEpochMillis }
                    .map {
                        DmThreadRecord(
                            id = it.id,
                            updatedAtEpochMillis = it.updatedAtEpochMillis,
                            lastMessage = it.lastMessage,
                            title = it.title,
                            avatarPath = it.avatarPath,
                            participantCount = it.participantCount,
                            unreadCount = it.unreadCount,
                            isGroup = it.isGroup,
                            isFriend = it.isFriend,
                            archived = it.archived,
                        )
                    },
            )
            RemoteDmDataResult.Unauthorized -> DmDataResult.Unauthorized
            RemoteDmDataResult.Unavailable -> DmDataResult.Unavailable
        }
    }
}

class SupabaseDmDataTransport(
    baseUrl: String,
    private val publishableKey: String,
) : DmDataTransport {
    private val endpoint = "${baseUrl.trimEnd('/')}/functions/v1/pdm-inbox"

    override suspend fun fetch(accessToken: String, userId: String): RemoteDmDataResult =
        withContext(Dispatchers.IO) {
            try {
                val response = get(accessToken)
                when {
                    response.status == 401 || response.status == 403 -> RemoteDmDataResult.Unauthorized
                    response.status !in 200..299 -> RemoteDmDataResult.Unavailable
                    else -> parse(response.body)
                }
            } catch (_: Exception) {
                RemoteDmDataResult.Unavailable
            }
        }

    private fun parse(body: String): RemoteDmDataResult = try {
        val root = JSONObject(body)
        val rows = root.getJSONArray("threads")
        val threads = buildList {
            repeat(rows.length()) { index ->
                val row = rows.getJSONObject(index)
                val threadId = row.getString("thread_id")
                val lastMessageId = row.optNullableString("last_message_id")
                val lastMessage = lastMessageId?.let {
                    val senderId = row.optNullableString("last_message_sender_id")
                        ?: return RemoteDmDataResult.Unavailable
                    RemoteDmMessage(
                        id = it,
                        threadId = threadId,
                        senderId = senderId,
                        body = row.optNullableString("last_message_body").orEmpty(),
                        createdAtEpochMillis = parseIsoMillis(row.optNullableString("last_message_created_at")),
                        expiresAtEpochMillis = row.optNullableString("last_message_expires_at")?.let(::parseIsoMillis),
                    )
                }
                add(
                    RemoteDmThread(
                        id = threadId,
                        updatedAtEpochMillis = parseIsoMillis(row.optNullableString("updated_at")),
                        title = row.optNullableString("title"),
                        avatarPath = row.optNullableString("avatar_path"),
                        isGroup = row.optNullableBoolean("is_group"),
                        isFriend = row.optNullableBoolean("is_friend"),
                        participantCount = row.optNullableInt("participant_count"),
                        unreadCount = row.optNullableInt("unread_count"),
                        archived = row.optNullableBoolean("archived"),
                        lastMessage = lastMessage,
                    ),
                )
            }
        }
        RemoteDmDataResult.Loaded(threads)
    } catch (_: Exception) {
        RemoteDmDataResult.Unavailable
    }

    private data class Response(val status: Int, val body: String)

    private fun get(accessToken: String): Response {
        val connection = (URL(endpoint).openConnection() as HttpURLConnection).apply {
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

    private fun JSONObject.optNullableInt(name: String): Int? =
        if (!has(name) || isNull(name)) null else getInt(name)

    private fun JSONObject.optNullableBoolean(name: String): Boolean? =
        if (!has(name) || isNull(name)) null else getBoolean(name)

    private fun parseIsoMillis(value: String?): Long = try {
        value?.let { java.time.Instant.parse(it).toEpochMilli() } ?: 0L
    } catch (_: Exception) {
        0L
    }
}

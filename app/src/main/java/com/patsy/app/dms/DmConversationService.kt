package com.patsy.app.dms

import com.patsy.app.auth.AuthSessionStore
import com.patsy.app.auth.PublicSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URLEncoder
import java.net.URL

sealed interface DmConversationResult {
    data class Loaded(val messages: List<RemoteDmMessage>) : DmConversationResult
    data object Unauthorized : DmConversationResult
    data object Unavailable : DmConversationResult
}

sealed interface DmSendResult {
    data class Accepted(val message: RemoteDmMessage) : DmSendResult
    data class InvalidInput(val reason: String) : DmSendResult
    data object Unauthorized : DmSendResult
    data object Unavailable : DmSendResult
}

sealed interface RemoteConversationResult {
    data class Loaded(val messages: List<RemoteDmMessage>) : RemoteConversationResult
    data object Unauthorized : RemoteConversationResult
    data object Unavailable : RemoteConversationResult
}

sealed interface RemoteSendMessageResult {
    data class Accepted(val message: RemoteDmMessage) : RemoteSendMessageResult
    data object Unauthorized : RemoteSendMessageResult
    data object Unavailable : RemoteSendMessageResult
}

interface DmConversationTransport {
    suspend fun load(accessToken: String, threadId: String): RemoteConversationResult
    suspend fun send(accessToken: String, userId: String, threadId: String, body: String): RemoteSendMessageResult
}

interface DmConversationService {
    suspend fun load(session: PublicSession, threadId: String): DmConversationResult
    suspend fun send(session: PublicSession, threadId: String, body: String): DmSendResult
}

class ServerDmConversationService(
    private val sessionStore: AuthSessionStore,
    private val transport: DmConversationTransport,
    private val nowEpochMillis: () -> Long = System::currentTimeMillis,
) : DmConversationService {
    override suspend fun load(session: PublicSession, threadId: String): DmConversationResult {
        if (threadId.isBlank()) return DmConversationResult.Unavailable
        val stored = validStoredSession(session) ?: return DmConversationResult.Unauthorized
        return when (val remote = transport.load(stored.accessToken, threadId)) {
            is RemoteConversationResult.Loaded -> {
                val now = nowEpochMillis()
                DmConversationResult.Loaded(
                    remote.messages
                        .filter { it.threadId == threadId }
                        .filter { it.expiresAtEpochMillis == null || it.expiresAtEpochMillis > now }
                        .sortedBy { it.createdAtEpochMillis },
                )
            }
            RemoteConversationResult.Unauthorized -> DmConversationResult.Unauthorized
            RemoteConversationResult.Unavailable -> DmConversationResult.Unavailable
        }
    }

    override suspend fun send(session: PublicSession, threadId: String, body: String): DmSendResult {
        val trimmed = body.trim()
        if (threadId.isBlank()) return DmSendResult.InvalidInput("thread")
        if (trimmed.isBlank()) return DmSendResult.InvalidInput("body")
        if (trimmed.length > 4_000) return DmSendResult.InvalidInput("body_too_long")
        val stored = validStoredSession(session) ?: return DmSendResult.Unauthorized
        return when (val remote = transport.send(stored.accessToken, session.userId, threadId, trimmed)) {
            is RemoteSendMessageResult.Accepted -> {
                val message = remote.message
                if (message.threadId != threadId || message.senderId != session.userId || message.body != trimmed) {
                    DmSendResult.Unavailable
                } else {
                    DmSendResult.Accepted(message)
                }
            }
            RemoteSendMessageResult.Unauthorized -> DmSendResult.Unauthorized
            RemoteSendMessageResult.Unavailable -> DmSendResult.Unavailable
        }
    }

    private fun validStoredSession(session: PublicSession) = sessionStore.read()?.takeIf {
        it.publicSession.sessionId == session.sessionId &&
            it.publicSession.userId == session.userId &&
            it.accessToken.isNotBlank()
    }
}

class SupabaseDmConversationTransport(
    baseUrl: String,
    private val publishableKey: String,
) : DmConversationTransport {
    private val restBase = "${baseUrl.trimEnd('/')}/rest/v1"

    override suspend fun load(accessToken: String, threadId: String): RemoteConversationResult = withContext(Dispatchers.IO) {
        try {
            val encodedThreadId = URLEncoder.encode(threadId, Charsets.UTF_8.name())
            val response = request(
                method = "GET",
                url = "$restBase/dm_messages?select=id,thread_id,sender_id,body,created_at,expires_at&thread_id=eq.$encodedThreadId&order=created_at.asc&limit=200",
                accessToken = accessToken,
            )
            when {
                response.status == 401 || response.status == 403 -> RemoteConversationResult.Unauthorized
                response.status !in 200..299 -> RemoteConversationResult.Unavailable
                else -> {
                    val rows = JSONArray(response.body)
                    val messages = buildList {
                        repeat(rows.length()) { add(parseMessage(rows.getJSONObject(it))) }
                    }
                    RemoteConversationResult.Loaded(messages)
                }
            }
        } catch (_: Exception) {
            RemoteConversationResult.Unavailable
        }
    }

    override suspend fun send(
        accessToken: String,
        userId: String,
        threadId: String,
        body: String,
    ): RemoteSendMessageResult = withContext(Dispatchers.IO) {
        try {
            val payload = JSONObject()
                .put("thread_id", threadId)
                .put("sender_id", userId)
                .put("body", body)
                .toString()
            val response = request(
                method = "POST",
                url = "$restBase/dm_messages?select=id,thread_id,sender_id,body,created_at,expires_at",
                accessToken = accessToken,
                body = payload,
                returnRepresentation = true,
            )
            when {
                response.status == 401 || response.status == 403 -> RemoteSendMessageResult.Unauthorized
                response.status !in 200..299 -> RemoteSendMessageResult.Unavailable
                else -> {
                    val rows = JSONArray(response.body)
                    if (rows.length() != 1) RemoteSendMessageResult.Unavailable
                    else RemoteSendMessageResult.Accepted(parseMessage(rows.getJSONObject(0)))
                }
            }
        } catch (_: Exception) {
            RemoteSendMessageResult.Unavailable
        }
    }

    private data class Response(val status: Int, val body: String)

    private fun request(
        method: String,
        url: String,
        accessToken: String,
        body: String? = null,
        returnRepresentation: Boolean = false,
    ): Response {
        val connection = (URL(url).openConnection() as HttpURLConnection).apply {
            requestMethod = method
            connectTimeout = 15_000
            readTimeout = 20_000
            useCaches = false
            setRequestProperty("Accept", "application/json")
            setRequestProperty("apikey", publishableKey)
            setRequestProperty("Authorization", "Bearer $accessToken")
            if (body != null) {
                doOutput = true
                setRequestProperty("Content-Type", "application/json")
            }
            if (returnRepresentation) setRequestProperty("Prefer", "return=representation")
        }
        return try {
            if (body != null) connection.outputStream.bufferedWriter(Charsets.UTF_8).use { it.write(body) }
            val status = connection.responseCode
            val stream = if (status in 200..299) connection.inputStream else connection.errorStream
            Response(status, stream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }.orEmpty())
        } finally {
            connection.disconnect()
        }
    }

    private fun parseMessage(row: JSONObject): RemoteDmMessage = RemoteDmMessage(
        id = row.getString("id"),
        threadId = row.getString("thread_id"),
        senderId = row.getString("sender_id"),
        body = row.optString("body"),
        createdAtEpochMillis = parseIsoMillis(row.optString("created_at")),
        expiresAtEpochMillis = row.optNullableString("expires_at")?.let(::parseIsoMillis),
    )

    private fun JSONObject.optNullableString(name: String): String? =
        if (!has(name) || isNull(name)) null else optString(name).takeIf { it.isNotBlank() }

    private fun parseIsoMillis(value: String): Long = try {
        java.time.Instant.parse(value).toEpochMilli()
    } catch (_: Exception) {
        0L
    }
}

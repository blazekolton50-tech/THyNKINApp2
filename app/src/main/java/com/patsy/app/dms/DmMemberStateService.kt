package com.patsy.app.dms

import com.patsy.app.auth.AuthSessionStore
import com.patsy.app.auth.PublicSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

sealed interface DmMemberStateResult {
    data object Updated : DmMemberStateResult
    data object Unauthorized : DmMemberStateResult
    data object Unavailable : DmMemberStateResult
}

fun dmMemberStateResultForStatus(status: Int): DmMemberStateResult = when {
    status in 200..299 -> DmMemberStateResult.Updated
    status == 401 || status == 403 -> DmMemberStateResult.Unauthorized
    else -> DmMemberStateResult.Unavailable
}

fun shouldRefreshInboxAfterMemberState(result: DmMemberStateResult): Boolean =
    result is DmMemberStateResult.Updated

fun dmMemberStatePayload(threadId: String, archived: Boolean?): String = if (archived == null) {
    "{\"thread_id\":\"$threadId\",\"action\":\"mark_read\"}"
} else {
    "{\"thread_id\":\"$threadId\",\"action\":\"set_archived\",\"archived\":$archived}"
}

interface DmMemberStateTransport {
    suspend fun markRead(accessToken: String, threadId: String): DmMemberStateResult
    suspend fun setArchived(accessToken: String, threadId: String, archived: Boolean): DmMemberStateResult
}

interface DmMemberStateService {
    suspend fun markRead(session: PublicSession, threadId: String): DmMemberStateResult
    suspend fun setArchived(session: PublicSession, threadId: String, archived: Boolean): DmMemberStateResult
}

class ServerDmMemberStateService(
    private val sessionStore: AuthSessionStore,
    private val transport: DmMemberStateTransport,
) : DmMemberStateService {
    override suspend fun markRead(session: PublicSession, threadId: String): DmMemberStateResult {
        if (threadId.isBlank()) return DmMemberStateResult.Unavailable
        val stored = validStoredSession(session) ?: return DmMemberStateResult.Unauthorized
        return transport.markRead(stored.accessToken, threadId)
    }

    override suspend fun setArchived(
        session: PublicSession,
        threadId: String,
        archived: Boolean,
    ): DmMemberStateResult {
        if (threadId.isBlank()) return DmMemberStateResult.Unavailable
        val stored = validStoredSession(session) ?: return DmMemberStateResult.Unauthorized
        return transport.setArchived(stored.accessToken, threadId, archived)
    }

    private fun validStoredSession(session: PublicSession) = sessionStore.read()?.takeIf {
        it.publicSession.sessionId == session.sessionId &&
            it.publicSession.userId == session.userId &&
            it.accessToken.isNotBlank()
    }
}

class SupabaseDmMemberStateTransport(
    baseUrl: String,
    private val publishableKey: String,
) : DmMemberStateTransport {
    private val endpoint = "${baseUrl.trimEnd('/')}/functions/v1/pdm-member-state"

    override suspend fun markRead(accessToken: String, threadId: String): DmMemberStateResult =
        post(accessToken, dmMemberStatePayload(threadId, archived = null))

    override suspend fun setArchived(
        accessToken: String,
        threadId: String,
        archived: Boolean,
    ): DmMemberStateResult = post(accessToken, dmMemberStatePayload(threadId, archived))

    private suspend fun post(accessToken: String, payload: String): DmMemberStateResult =
        withContext(Dispatchers.IO) {
            try {
                val connection = (URL(endpoint).openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    connectTimeout = 15_000
                    readTimeout = 20_000
                    useCaches = false
                    doOutput = true
                    setRequestProperty("Accept", "application/json")
                    setRequestProperty("Content-Type", "application/json")
                    setRequestProperty("apikey", publishableKey)
                    setRequestProperty("Authorization", "Bearer $accessToken")
                }
                try {
                    connection.outputStream.bufferedWriter(Charsets.UTF_8).use { it.write(payload) }
                    dmMemberStateResultForStatus(connection.responseCode)
                } finally {
                    connection.disconnect()
                }
            } catch (_: Exception) {
                DmMemberStateResult.Unavailable
            }
        }
}

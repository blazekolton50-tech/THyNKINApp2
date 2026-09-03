package com.patsy.app.studio

import com.patsy.app.auth.AuthSessionStore
import com.patsy.app.auth.PublicSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

sealed interface StudioProjectCreateResult {
    data class Created(
        val projectId: String,
        val name: String,
    ) : StudioProjectCreateResult
    data object InvalidName : StudioProjectCreateResult
    data object Unauthorized : StudioProjectCreateResult
    data object Unavailable : StudioProjectCreateResult
}

sealed interface RemoteStudioProjectCreateResult {
    data class Created(
        val projectId: String,
        val name: String,
    ) : RemoteStudioProjectCreateResult
    data object Unauthorized : RemoteStudioProjectCreateResult
    data object Unavailable : RemoteStudioProjectCreateResult
}

interface StudioProjectTransport {
    suspend fun create(accessToken: String, userId: String, name: String): RemoteStudioProjectCreateResult
}

interface StudioProjectService {
    suspend fun create(session: PublicSession, name: String): StudioProjectCreateResult
}

class ServerStudioProjectService(
    private val sessionStore: AuthSessionStore,
    private val transport: StudioProjectTransport,
) : StudioProjectService {
    override suspend fun create(session: PublicSession, name: String): StudioProjectCreateResult {
        val normalized = name.trim()
        if (normalized.isEmpty() || normalized.length > 120) return StudioProjectCreateResult.InvalidName

        val stored = sessionStore.read()?.takeIf {
            it.publicSession.sessionId == session.sessionId &&
                it.publicSession.userId == session.userId &&
                it.accessToken.isNotBlank()
        } ?: return StudioProjectCreateResult.Unauthorized

        return when (val result = transport.create(stored.accessToken, session.userId, normalized)) {
            is RemoteStudioProjectCreateResult.Created -> {
                if (result.projectId.isBlank() || result.name.isBlank()) StudioProjectCreateResult.Unavailable
                else StudioProjectCreateResult.Created(result.projectId, result.name)
            }
            RemoteStudioProjectCreateResult.Unauthorized -> StudioProjectCreateResult.Unauthorized
            RemoteStudioProjectCreateResult.Unavailable -> StudioProjectCreateResult.Unavailable
        }
    }
}

class SupabaseStudioProjectTransport(
    baseUrl: String,
    private val publishableKey: String,
) : StudioProjectTransport {
    private val endpoint = "${baseUrl.trimEnd('/')}/rest/v1/rpc/studio_create_project"

    override suspend fun create(
        accessToken: String,
        userId: String,
        name: String,
    ): RemoteStudioProjectCreateResult = withContext(Dispatchers.IO) {
        try {
            val response = post(
                accessToken = accessToken,
                body = JSONObject().put("p_name", name).toString(),
            )
            if (response.status == 401 || response.status == 403) return@withContext RemoteStudioProjectCreateResult.Unauthorized
            if (response.status !in 200..299) return@withContext RemoteStudioProjectCreateResult.Unavailable

            val rows = JSONArray(response.body)
            if (rows.length() != 1) return@withContext RemoteStudioProjectCreateResult.Unavailable
            val row = rows.getJSONObject(0)
            val projectId = row.optString("project_id")
            val returnedName = row.optString("name")
            if (projectId.isBlank() || returnedName.isBlank()) return@withContext RemoteStudioProjectCreateResult.Unavailable
            RemoteStudioProjectCreateResult.Created(projectId, returnedName)
        } catch (_: Exception) {
            RemoteStudioProjectCreateResult.Unavailable
        }
    }

    private data class Response(val status: Int, val body: String)

    private fun post(accessToken: String, body: String): Response {
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
        return try {
            connection.outputStream.use { it.write(body.toByteArray(Charsets.UTF_8)) }
            val status = connection.responseCode
            val stream = if (status in 200..299) connection.inputStream else connection.errorStream
            Response(status, stream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }.orEmpty())
        } finally {
            connection.disconnect()
        }
    }
}

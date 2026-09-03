package com.patsy.app.profile

import com.patsy.app.auth.AuthSessionStore
import com.patsy.app.auth.PublicSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URLEncoder
import java.net.URL

sealed interface ProfileDataResult {
    data class Loaded(val presentation: ProfilePresentationState) : ProfileDataResult
    data object Unauthorized : ProfileDataResult
    data object Unavailable : ProfileDataResult
}

data class RemoteProfileRow(
    val userId: String,
    val username: String,
    val displayName: String?,
    val avatarPath: String?,
)

data class RemoteProfileProjectRow(
    val id: String,
    val name: String,
    val isLocked: Boolean,
    val updatedAtEpochMillis: Long,
)

sealed interface RemoteProfileDataResult {
    data class Loaded(
        val profile: RemoteProfileRow,
        val projects: List<RemoteProfileProjectRow>,
    ) : RemoteProfileDataResult
    data object Unauthorized : RemoteProfileDataResult
    data object Unavailable : RemoteProfileDataResult
}

interface ProfileDataTransport {
    suspend fun fetch(accessToken: String, userId: String): RemoteProfileDataResult
}

interface ProfileDataService {
    suspend fun load(session: PublicSession): ProfileDataResult
}

class ServerProfileDataService(
    private val sessionStore: AuthSessionStore,
    private val transport: ProfileDataTransport,
) : ProfileDataService {
    override suspend fun load(session: PublicSession): ProfileDataResult {
        val stored = sessionStore.read() ?: return ProfileDataResult.Unauthorized
        if (
            stored.publicSession.sessionId != session.sessionId ||
            stored.publicSession.userId != session.userId ||
            stored.accessToken.isBlank()
        ) return ProfileDataResult.Unauthorized

        return when (val remote = transport.fetch(stored.accessToken, session.userId)) {
            is RemoteProfileDataResult.Loaded -> {
                if (remote.profile.userId != session.userId) return ProfileDataResult.Unauthorized
                val projects = remote.projects.sortedByDescending { it.updatedAtEpochMillis }
                ProfileDataResult.Loaded(
                    ProfilePresentationState(
                        displayName = remote.profile.displayName?.takeIf { it.isNotBlank() }
                            ?: remote.profile.username.ifBlank { session.username },
                        username = remote.profile.username.trim().removePrefix("@").let {
                            if (it.isBlank()) "" else "@$it"
                        },
                        avatarUri = remote.profile.avatarPath,
                        metrics = listOf(
                            ProfileMetric("Projects", projects.size.toLong()),
                            ProfileMetric("Followers", null),
                            ProfileMetric("Following", null),
                            ProfileMetric("Likes", null),
                        ),
                        recentProjects = projects.take(12).map {
                            ProfileProjectRef(id = it.id, title = it.name)
                        },
                        // No saved-project relation is currently authoritative in the live schema.
                        savedProjects = emptyList(),
                    ),
                )
            }
            RemoteProfileDataResult.Unauthorized -> ProfileDataResult.Unauthorized
            RemoteProfileDataResult.Unavailable -> ProfileDataResult.Unavailable
        }
    }
}

class SupabaseProfileDataTransport(
    baseUrl: String,
    private val publishableKey: String,
) : ProfileDataTransport {
    private val restBase = "${baseUrl.trimEnd('/')}/rest/v1"

    override suspend fun fetch(accessToken: String, userId: String): RemoteProfileDataResult =
        withContext(Dispatchers.IO) {
            try {
                val encodedUserId = URLEncoder.encode(userId, Charsets.UTF_8.name())
                val profileResponse = get(
                    "$restBase/profiles?select=user_id,username,display_name,avatar_path&user_id=eq.$encodedUserId&limit=1",
                    accessToken,
                )
                if (profileResponse.status == 401 || profileResponse.status == 403) {
                    return@withContext RemoteProfileDataResult.Unauthorized
                }
                if (profileResponse.status !in 200..299) return@withContext RemoteProfileDataResult.Unavailable
                val profiles = JSONArray(profileResponse.body)
                if (profiles.length() != 1) return@withContext RemoteProfileDataResult.Unavailable
                val profileJson = profiles.getJSONObject(0)
                val profile = RemoteProfileRow(
                    userId = profileJson.getString("user_id"),
                    username = profileJson.optString("username"),
                    displayName = profileJson.optString("display_name").takeIf { it.isNotBlank() && it != "null" },
                    avatarPath = profileJson.optString("avatar_path").takeIf { it.isNotBlank() && it != "null" },
                )

                val projectsResponse = get(
                    "$restBase/projects?select=id,name,is_locked,updated_at&user_id=eq.$encodedUserId&order=updated_at.desc&limit=100",
                    accessToken,
                )
                if (projectsResponse.status == 401 || projectsResponse.status == 403) {
                    return@withContext RemoteProfileDataResult.Unauthorized
                }
                if (projectsResponse.status !in 200..299) return@withContext RemoteProfileDataResult.Unavailable
                val projectsJson = JSONArray(projectsResponse.body)
                val projects = buildList {
                    repeat(projectsJson.length()) {
                        val item = projectsJson.getJSONObject(it)
                        add(
                            RemoteProfileProjectRow(
                                id = item.getString("id"),
                                name = item.optString("name").ifBlank { "Untitled project" },
                                isLocked = item.optBoolean("is_locked", false),
                                updatedAtEpochMillis = parseIsoMillis(item.optString("updated_at")),
                            ),
                        )
                    }
                }
                RemoteProfileDataResult.Loaded(profile, projects)
            } catch (_: Exception) {
                RemoteProfileDataResult.Unavailable
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

    private fun parseIsoMillis(value: String): Long = try {
        java.time.Instant.parse(value).toEpochMilli()
    } catch (_: Exception) {
        0L
    }
}

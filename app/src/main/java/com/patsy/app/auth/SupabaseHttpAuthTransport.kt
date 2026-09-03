package com.patsy.app.auth

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.io.Writer
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL
import java.util.Base64

class SupabaseHttpAuthTransport(
    baseUrl: String,
    private val publishableKey: String,
) : SupabaseAuthTransport {
    private val baseUrl = baseUrl.trimEnd('/')

    override suspend fun login(identifier: LoginIdentifier, password: CharArray): RemoteAuthResult =
        withContext(Dispatchers.IO) { requestAuthLogin(identifier, password) }

    override suspend fun refresh(refreshToken: String): RemoteAuthResult = withContext(Dispatchers.IO) {
        requestRefresh(refreshToken)
    }

    override suspend fun signOut(accessToken: String): RemoteSignOutResult = withContext(Dispatchers.IO) {
        try {
            val connection = open("$baseUrl/auth/v1/logout", "POST").apply {
                setRequestProperty("Authorization", "Bearer $accessToken")
                doOutput = true
            }
            connection.outputStream.use { }
            val code = connection.responseCode
            connection.disconnect()
            if (code in 200..299 || code == 401) RemoteSignOutResult.SignedOut
            else if (code == 408 || code == 504) RemoteSignOutResult.Unavailable(ServiceFailure.Timeout)
            else RemoteSignOutResult.Unavailable(ServiceFailure.ServerError)
        } catch (_: SocketTimeoutException) {
            RemoteSignOutResult.Unavailable(ServiceFailure.Timeout)
        } catch (_: IOException) {
            RemoteSignOutResult.Unavailable(ServiceFailure.Offline)
        } catch (_: Exception) {
            RemoteSignOutResult.Unavailable(ServiceFailure.Unknown)
        }
    }

    private fun requestAuthLogin(identifier: LoginIdentifier, password: CharArray): RemoteAuthResult = try {
        val connection = open("$baseUrl/functions/v1/auth-login", "POST").apply { doOutput = true }
        connection.outputStream.bufferedWriter(Charsets.UTF_8).use { writer ->
            writer.write('{'.code)
            writer.write("\"identifier\":")
            writeJsonString(writer, identifier.value)
            writer.write(",\"password\":")
            writeJsonString(writer, password)
            writer.write('}'.code)
        }
        parseCustomLoginResponse(connection)
    } catch (_: SocketTimeoutException) {
        RemoteAuthResult.Failure(RemoteAuthFailure.Timeout)
    } catch (_: IOException) {
        RemoteAuthResult.Failure(RemoteAuthFailure.Offline)
    } catch (_: Exception) {
        RemoteAuthResult.Failure(RemoteAuthFailure.Unknown)
    }

    private fun requestRefresh(refreshToken: String): RemoteAuthResult = try {
        val connection = open("$baseUrl/auth/v1/token?grant_type=refresh_token", "POST").apply { doOutput = true }
        connection.outputStream.bufferedWriter(Charsets.UTF_8).use { writer ->
            writer.write("{\"refresh_token\":")
            writeJsonString(writer, refreshToken)
            writer.write('}'.code)
        }
        val code = connection.responseCode
        val body = readResponse(connection, code)
        connection.disconnect()
        when {
            code in 200..299 -> parseSupabaseSession(JSONObject(body))
            code == 400 || code == 401 || code == 403 -> RemoteAuthResult.Failure(RemoteAuthFailure.InvalidSession)
            code == 429 -> RemoteAuthResult.Failure(RemoteAuthFailure.RateLimited)
            code == 408 || code == 504 -> RemoteAuthResult.Failure(RemoteAuthFailure.Timeout)
            code >= 500 -> RemoteAuthResult.Failure(RemoteAuthFailure.ServiceUnavailable)
            else -> RemoteAuthResult.Failure(RemoteAuthFailure.Unknown)
        }
    } catch (_: SocketTimeoutException) {
        RemoteAuthResult.Failure(RemoteAuthFailure.Timeout)
    } catch (_: IOException) {
        RemoteAuthResult.Failure(RemoteAuthFailure.Offline)
    } catch (_: Exception) {
        RemoteAuthResult.Failure(RemoteAuthFailure.Unknown)
    }

    private fun parseCustomLoginResponse(connection: HttpURLConnection): RemoteAuthResult {
        val code = connection.responseCode
        val body = readResponse(connection, code)
        connection.disconnect()
        return when {
            code in 200..299 -> {
                val root = JSONObject(body)
                val user = root.getJSONObject("user")
                val access = root.getString("access_token")
                val refresh = root.getString("refresh_token")
                val expiresAt = root.optLong("expires_at", 0L).takeIf { it > 0L }?.times(1000L)
                    ?: (System.currentTimeMillis() + root.optLong("expires_in", 3600L) * 1000L)
                RemoteAuthResult.Authenticated(
                    RemoteAuthSession(
                        accessToken = access,
                        refreshToken = refresh,
                        sessionId = jwtSessionId(access) ?: "supabase-${user.getString("id")}-$expiresAt",
                        userId = user.getString("id"),
                        username = user.optString("username", "User"),
                        maskedEmail = user.optString("masked_email", "hidden"),
                        emailVerified = user.optBoolean("email_verified", false),
                        expiresAtEpochMillis = expiresAt,
                    )
                )
            }
            code == 401 -> RemoteAuthResult.Failure(RemoteAuthFailure.InvalidCredentials)
            code == 429 -> RemoteAuthResult.Failure(RemoteAuthFailure.RateLimited)
            code == 408 || code == 504 -> RemoteAuthResult.Failure(RemoteAuthFailure.Timeout)
            code >= 500 -> RemoteAuthResult.Failure(RemoteAuthFailure.ServiceUnavailable)
            else -> RemoteAuthResult.Failure(RemoteAuthFailure.Unknown)
        }
    }

    private fun parseSupabaseSession(root: JSONObject): RemoteAuthResult {
        val access = root.optString("access_token")
        val refresh = root.optString("refresh_token")
        val user = root.optJSONObject("user") ?: return RemoteAuthResult.Failure(RemoteAuthFailure.Unknown)
        val userId = user.optString("id")
        val email = user.optString("email")
        if (access.isBlank() || refresh.isBlank() || userId.isBlank()) return RemoteAuthResult.Failure(RemoteAuthFailure.Unknown)
        val expiresAt = root.optLong("expires_at", 0L).takeIf { it > 0L }?.times(1000L)
            ?: (System.currentTimeMillis() + root.optLong("expires_in", 3600L) * 1000L)
        val username = fetchOwnUsername(userId, access) ?: "User"
        return RemoteAuthResult.Authenticated(
            RemoteAuthSession(
                accessToken = access,
                refreshToken = refresh,
                sessionId = jwtSessionId(access) ?: "supabase-$userId-$expiresAt",
                userId = userId,
                username = username,
                maskedEmail = maskEmail(email),
                emailVerified = user.optString("email_confirmed_at").isNotBlank(),
                expiresAtEpochMillis = expiresAt,
            )
        )
    }

    private fun fetchOwnUsername(userId: String, accessToken: String): String? = try {
        val query = "select=username&user_id=eq.${urlEncode(userId)}&limit=1"
        val connection = open("$baseUrl/rest/v1/profiles?$query", "GET").apply {
            setRequestProperty("Authorization", "Bearer $accessToken")
        }
        val code = connection.responseCode
        val body = readResponse(connection, code)
        connection.disconnect()
        if (code !in 200..299) null else JSONArray(body).optJSONObject(0)?.optString("username")?.takeIf { it.isNotBlank() }
    } catch (_: Exception) {
        null
    }

    private fun open(url: String, method: String): HttpURLConnection =
        (URL(url).openConnection() as HttpURLConnection).apply {
            requestMethod = method
            connectTimeout = 15_000
            readTimeout = 20_000
            useCaches = false
            setRequestProperty("Accept", "application/json")
            setRequestProperty("Content-Type", "application/json; charset=utf-8")
            setRequestProperty("apikey", publishableKey)
        }

    private fun readResponse(connection: HttpURLConnection, code: Int): String {
        val stream = if (code in 200..299) connection.inputStream else connection.errorStream
        return stream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }.orEmpty()
    }

    private fun jwtSessionId(accessToken: String): String? = try {
        val parts = accessToken.split('.')
        if (parts.size != 3) null else {
            val json = String(Base64.getUrlDecoder().decode(parts[1]), Charsets.UTF_8)
            JSONObject(json).optString("session_id").takeIf { it.isNotBlank() }
        }
    } catch (_: Exception) {
        null
    }

    private fun maskEmail(email: String): String {
        val at = email.indexOf('@')
        if (at <= 0) return "hidden"
        val local = email.substring(0, at)
        val domain = email.substring(at + 1)
        val shown = local.take(2.coerceAtMost(local.length))
        return "$shown${"*".repeat((local.length - shown.length).coerceAtLeast(2))}@$domain"
    }

    private fun urlEncode(value: String): String = java.net.URLEncoder.encode(value, Charsets.UTF_8.name())
}

private fun writeJsonString(writer: Writer, value: CharSequence) {
    writer.write('"'.code)
    value.forEach { writeEscapedJsonChar(writer, it) }
    writer.write('"'.code)
}

private fun writeJsonString(writer: Writer, value: CharArray) {
    writer.write('"'.code)
    value.forEach { writeEscapedJsonChar(writer, it) }
    writer.write('"'.code)
}

private fun writeEscapedJsonChar(writer: Writer, char: Char) {
    when (char) {
        '"' -> writer.write("\\\"")
        '\\' -> writer.write("\\\\")
        '\b' -> writer.write("\\b")
        '\u000C' -> writer.write("\\f")
        '\n' -> writer.write("\\n")
        '\r' -> writer.write("\\r")
        '\t' -> writer.write("\\t")
        else -> if (char.code < 0x20) writer.write("\\u%04x".format(char.code)) else writer.write(char.code)
    }
}

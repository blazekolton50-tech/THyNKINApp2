package com.patsy.app.security

import com.patsy.app.auth.ServiceFailure
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL

/** Client-safe transport. The Android app sends only its user access token and requested capability. */
class SupabaseOwnerAuthorizationTransport(
    baseUrl: String,
    private val publishableKey: String,
) : OwnerAuthorizationTransport {
    private val endpoint = "${baseUrl.trimEnd('/')}/functions/v1/owner-authorize"

    override suspend fun verify(
        accessToken: String,
        capability: OwnerCapability,
    ): RemoteOwnerAuthorizationResult = withContext(Dispatchers.IO) {
        var connection: HttpURLConnection? = null
        try {
            connection = (URL(endpoint).openConnection() as HttpURLConnection).apply {
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

            val payload = JSONObject()
                .put("capability", capability.name)
                .toString()
                .toByteArray(Charsets.UTF_8)
            connection.outputStream.use { it.write(payload) }

            val status = connection.responseCode
            val stream = if (status in 200..299) connection.inputStream else connection.errorStream
            val body = stream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }.orEmpty()

            when {
                status in 200..299 -> parseAllowed(body)
                status == 401 -> RemoteOwnerAuthorizationResult.Unauthorized
                status == 403 -> RemoteOwnerAuthorizationResult.Denied
                status == 408 -> RemoteOwnerAuthorizationResult.Unavailable(ServiceFailure.Timeout)
                status >= 500 -> RemoteOwnerAuthorizationResult.Unavailable(ServiceFailure.ServerError)
                else -> RemoteOwnerAuthorizationResult.Unavailable(ServiceFailure.Unknown)
            }
        } catch (_: SocketTimeoutException) {
            RemoteOwnerAuthorizationResult.Unavailable(ServiceFailure.Timeout)
        } catch (_: IOException) {
            RemoteOwnerAuthorizationResult.Unavailable(ServiceFailure.Offline)
        } catch (_: Exception) {
            RemoteOwnerAuthorizationResult.Unavailable(ServiceFailure.Unknown)
        } finally {
            connection?.disconnect()
        }
    }

    private fun parseAllowed(body: String): RemoteOwnerAuthorizationResult = try {
        val root = JSONObject(body)
        if (!root.optBoolean("allowed", false)) {
            RemoteOwnerAuthorizationResult.Denied
        } else {
            val capability = OwnerCapability.entries.firstOrNull {
                it.name == root.getString("capability")
            } ?: return RemoteOwnerAuthorizationResult.Unavailable(ServiceFailure.Unknown)

            val authorizationId = root.getString("authorization_id")
            val auditCorrelationId = root.getString("audit_correlation_id")
            val expiresAt = root.getLong("expires_at_epoch_millis")
            if (authorizationId.isBlank() || auditCorrelationId.isBlank() || expiresAt <= 0L) {
                RemoteOwnerAuthorizationResult.Unavailable(ServiceFailure.Unknown)
            } else {
                RemoteOwnerAuthorizationResult.Allowed(
                    authorizationId = authorizationId,
                    capability = capability,
                    expiresAtEpochMillis = expiresAt,
                    auditCorrelationId = auditCorrelationId,
                )
            }
        }
    } catch (_: Exception) {
        RemoteOwnerAuthorizationResult.Unavailable(ServiceFailure.Unknown)
    }
}

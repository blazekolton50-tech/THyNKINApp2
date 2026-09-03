package com.patsy.app.auth

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL

sealed interface RemotePasswordResetResult {
    data class Accepted(val genericMessage: String) : RemotePasswordResetResult
    data class Unavailable(val failure: ServiceFailure) : RemotePasswordResetResult
}

interface SupabaseRecoveryTransport {
    suspend fun request(identifier: String): RemotePasswordResetResult
}

object UnconfiguredRecoveryTransport : SupabaseRecoveryTransport {
    override suspend fun request(identifier: String) =
        RemotePasswordResetResult.Unavailable(ServiceFailure.NotConfigured)
}

class SupabaseHttpRecoveryTransport(
    baseUrl: String,
    private val publishableKey: String,
) : SupabaseRecoveryTransport {
    private val baseUrl = baseUrl.trimEnd('/')

    override suspend fun request(identifier: String): RemotePasswordResetResult = withContext(Dispatchers.IO) {
        try {
            val connection = (URL("$baseUrl/functions/v1/auth-reset-request").openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                connectTimeout = 15_000
                readTimeout = 20_000
                useCaches = false
                doOutput = true
                setRequestProperty("Accept", "application/json")
                setRequestProperty("Content-Type", "application/json; charset=utf-8")
                setRequestProperty("apikey", publishableKey)
            }
            val payload = JSONObject().put("identifier", identifier.trim()).toString()
            connection.outputStream.bufferedWriter(Charsets.UTF_8).use { it.write(payload) }
            val status = connection.responseCode
            val stream = if (status in 200..299) connection.inputStream else connection.errorStream
            val body = stream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }.orEmpty()
            connection.disconnect()
            when {
                status in 200..299 -> {
                    val message = try {
                        JSONObject(body).optString(
                            "message",
                            "If an account matches and email delivery is available, you'll receive password reset instructions.",
                        )
                    } catch (_: Exception) {
                        "If an account matches and email delivery is available, you'll receive password reset instructions."
                    }
                    RemotePasswordResetResult.Accepted(message)
                }
                status == 408 || status == 504 -> RemotePasswordResetResult.Unavailable(ServiceFailure.Timeout)
                status >= 500 -> RemotePasswordResetResult.Unavailable(ServiceFailure.ServerError)
                else -> RemotePasswordResetResult.Unavailable(ServiceFailure.Unknown)
            }
        } catch (_: SocketTimeoutException) {
            RemotePasswordResetResult.Unavailable(ServiceFailure.Timeout)
        } catch (_: IOException) {
            RemotePasswordResetResult.Unavailable(ServiceFailure.Offline)
        } catch (_: Exception) {
            RemotePasswordResetResult.Unavailable(ServiceFailure.Unknown)
        }
    }
}

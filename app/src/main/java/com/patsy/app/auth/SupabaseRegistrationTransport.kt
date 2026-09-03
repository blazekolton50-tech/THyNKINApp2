package com.patsy.app.auth

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.IOException
import java.io.Writer
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL

sealed interface RemoteRegistrationStartResult {
    data class Ready(
        val attemptId: String,
        val normalizedUsername: String,
        val maskedEmail: String,
    ) : RemoteRegistrationStartResult
    data class Failure(val failure: RemoteRegistrationFailure) : RemoteRegistrationStartResult
}

sealed interface RemoteRegistrationResult {
    data class AccountCreated(
        val userId: String,
        val username: String,
        val maskedEmail: String,
        val deliveryState: ConfirmedEmailDeliveryState,
    ) : RemoteRegistrationResult
    data class Failure(val failure: RemoteRegistrationFailure) : RemoteRegistrationResult
}

enum class RemoteRegistrationFailure {
    DuplicateUsername,
    DuplicateEmail,
    InvalidUsername,
    InvalidEmail,
    InvalidExperienceMode,
    InvalidAttempt,
    InvalidPassword,
    RateLimited,
    Offline,
    Timeout,
    ServiceUnavailable,
    Unknown,
}

interface SupabaseRegistrationTransport {
    suspend fun start(request: StartRegistrationRequest): RemoteRegistrationStartResult
    suspend fun complete(attemptId: String, password: CharArray): RemoteRegistrationResult
}

object UnconfiguredRegistrationTransport : SupabaseRegistrationTransport {
    override suspend fun start(request: StartRegistrationRequest) =
        RemoteRegistrationStartResult.Failure(RemoteRegistrationFailure.ServiceUnavailable)
    override suspend fun complete(attemptId: String, password: CharArray) =
        RemoteRegistrationResult.Failure(RemoteRegistrationFailure.ServiceUnavailable)
}

class SupabaseHttpRegistrationTransport(
    baseUrl: String,
    private val publishableKey: String,
) : SupabaseRegistrationTransport {
    private val baseUrl = baseUrl.trimEnd('/')

    override suspend fun start(request: StartRegistrationRequest): RemoteRegistrationStartResult =
        withContext(Dispatchers.IO) {
            try {
                val connection = open("$baseUrl/functions/v1/auth-register-start").apply { doOutput = true }
                connection.outputStream.bufferedWriter(Charsets.UTF_8).use { writer ->
                    writer.write('{'.code)
                    writer.write("\"username\":")
                    writeString(writer, request.username)
                    writer.write(",\"email\":")
                    writeString(writer, request.email)
                    writer.write(",\"experience_mode\":")
                    writeString(writer, request.experienceMode)
                    writer.write('}'.code)
                }
                val code = connection.responseCode
                val body = readResponse(connection, code)
                connection.disconnect()
                if (code in 200..299) {
                    val root = JSONObject(body)
                    RemoteRegistrationStartResult.Ready(
                        attemptId = root.getString("registration_attempt_id"),
                        normalizedUsername = root.getString("normalized_username"),
                        maskedEmail = root.getString("masked_email"),
                    )
                } else {
                    RemoteRegistrationStartResult.Failure(mapFailure(code, body))
                }
            } catch (_: SocketTimeoutException) {
                RemoteRegistrationStartResult.Failure(RemoteRegistrationFailure.Timeout)
            } catch (_: IOException) {
                RemoteRegistrationStartResult.Failure(RemoteRegistrationFailure.Offline)
            } catch (_: Exception) {
                RemoteRegistrationStartResult.Failure(RemoteRegistrationFailure.Unknown)
            }
        }

    override suspend fun complete(attemptId: String, password: CharArray): RemoteRegistrationResult =
        withContext(Dispatchers.IO) {
            try {
                val connection = open("$baseUrl/functions/v1/auth-register-complete").apply { doOutput = true }
                connection.outputStream.bufferedWriter(Charsets.UTF_8).use { writer ->
                    writer.write("{\"registration_attempt_id\":")
                    writeString(writer, attemptId)
                    writer.write(",\"password\":")
                    writeChars(writer, password)
                    writer.write('}'.code)
                }
                val code = connection.responseCode
                val body = readResponse(connection, code)
                connection.disconnect()
                if (code in 200..299) {
                    val root = JSONObject(body)
                    val confirmation = root.getJSONObject("confirmation_email")
                    val deliveryState = when (confirmation.optString("state")) {
                        "QUEUED" -> ConfirmedEmailDeliveryState.QUEUED
                        "SENT" -> ConfirmedEmailDeliveryState.SENT
                        else -> return@withContext RemoteRegistrationResult.Failure(RemoteRegistrationFailure.Unknown)
                    }
                    RemoteRegistrationResult.AccountCreated(
                        userId = root.getString("user_id"),
                        username = root.getString("username"),
                        maskedEmail = confirmation.getString("masked_email"),
                        deliveryState = deliveryState,
                    )
                } else {
                    RemoteRegistrationResult.Failure(mapFailure(code, body))
                }
            } catch (_: SocketTimeoutException) {
                RemoteRegistrationResult.Failure(RemoteRegistrationFailure.Timeout)
            } catch (_: IOException) {
                RemoteRegistrationResult.Failure(RemoteRegistrationFailure.Offline)
            } catch (_: Exception) {
                RemoteRegistrationResult.Failure(RemoteRegistrationFailure.Unknown)
            }
        }

    private fun open(url: String): HttpURLConnection =
        (URL(url).openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = 15_000
            readTimeout = 25_000
            useCaches = false
            setRequestProperty("Accept", "application/json")
            setRequestProperty("Content-Type", "application/json; charset=utf-8")
            setRequestProperty("apikey", publishableKey)
        }

    private fun readResponse(connection: HttpURLConnection, code: Int): String {
        val stream = if (code in 200..299) connection.inputStream else connection.errorStream
        return stream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }.orEmpty()
    }

    private fun mapFailure(status: Int, body: String): RemoteRegistrationFailure {
        val code = try { JSONObject(body).optString("code") } catch (_: Exception) { "" }
        return when (code) {
            "DUPLICATE_USERNAME" -> RemoteRegistrationFailure.DuplicateUsername
            "DUPLICATE_EMAIL" -> RemoteRegistrationFailure.DuplicateEmail
            "INVALID_USERNAME" -> RemoteRegistrationFailure.InvalidUsername
            "INVALID_EMAIL" -> RemoteRegistrationFailure.InvalidEmail
            "INVALID_EXPERIENCE_MODE" -> RemoteRegistrationFailure.InvalidExperienceMode
            "INVALID_ATTEMPT", "INVALID_OR_EXPIRED_ATTEMPT" -> RemoteRegistrationFailure.InvalidAttempt
            "INVALID_PASSWORD" -> RemoteRegistrationFailure.InvalidPassword
            "RATE_LIMITED" -> RemoteRegistrationFailure.RateLimited
            "SERVICE_UNAVAILABLE" -> RemoteRegistrationFailure.ServiceUnavailable
            else -> when {
                status == 429 -> RemoteRegistrationFailure.RateLimited
                status == 408 || status == 504 -> RemoteRegistrationFailure.Timeout
                status >= 500 -> RemoteRegistrationFailure.ServiceUnavailable
                else -> RemoteRegistrationFailure.Unknown
            }
        }
    }
}

private fun writeString(writer: Writer, value: CharSequence) {
    writer.write('"'.code)
    value.forEach { writeEscaped(writer, it) }
    writer.write('"'.code)
}

private fun writeChars(writer: Writer, value: CharArray) {
    writer.write('"'.code)
    value.forEach { writeEscaped(writer, it) }
    writer.write('"'.code)
}

private fun writeEscaped(writer: Writer, char: Char) {
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

package com.patsy.app.account

import com.patsy.app.security.OwnerCapability
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/** Authenticated, client-safe adapter. Authority is returned by the backend, never created here. */
class SupabaseAccountBootstrapTransport(
    baseUrl: String,
    private val publishableKey: String,
) : AccountBootstrapTransport {
    private val endpoint = "${baseUrl.trimEnd('/')}/functions/v1/account-bootstrap"

    override suspend fun fetch(accessToken: String): RemoteAccountBootstrapResult = withContext(Dispatchers.IO) {
        try {
            val connection = (URL(endpoint).openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 15_000
                readTimeout = 20_000
                useCaches = false
                setRequestProperty("Accept", "application/json")
                setRequestProperty("apikey", publishableKey)
                setRequestProperty("Authorization", "Bearer $accessToken")
            }
            val status = connection.responseCode
            val stream = if (status in 200..299) connection.inputStream else connection.errorStream
            val body = stream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }.orEmpty()
            connection.disconnect()
            when {
                status in 200..299 -> parse(body)
                status == 401 -> RemoteAccountBootstrapResult.Unauthorized
                status == 403 -> RemoteAccountBootstrapResult.Forbidden
                else -> RemoteAccountBootstrapResult.Unavailable
            }
        } catch (_: Exception) {
            RemoteAccountBootstrapResult.Unavailable
        }
    }

    private fun parse(body: String): RemoteAccountBootstrapResult = try {
        val root = JSONObject(body)
        fun <T : Enum<T>> enum(name: String, value: String, entries: Array<T>): T =
            entries.firstOrNull { it.name == value } ?: throw IllegalArgumentException("unknown $name")
        val capabilities = root.getJSONArray("capabilities").let { values ->
            buildSet {
                repeat(values.length()) {
                    add(enum("capability", values.getString(it), OwnerCapability.entries.toTypedArray()))
                }
            }
        }
        val restrictions = root.getJSONArray("restrictions").let { values ->
            buildSet {
                repeat(values.length()) {
                    add(enum("restriction", values.getString(it), ShellRestriction.entries.toTypedArray()))
                }
            }
        }
        RemoteAccountBootstrapResult.Available(
            AccountBootstrap(
                canonicalUserId = root.getString("canonical_user_id"),
                profileReadiness = enum(
                    "profile_readiness",
                    root.getString("profile_readiness"),
                    ProfileReadiness.entries.toTypedArray(),
                ),
                onboardingState = enum(
                    "onboarding_state",
                    root.getString("onboarding_state"),
                    OnboardingState.entries.toTypedArray(),
                ),
                ageState = enum(
                    "age_state",
                    root.getString("age_state"),
                    TrustedAgeState.entries.toTypedArray(),
                ),
                capabilities = capabilities,
                accountStatus = enum(
                    "account_status",
                    root.getString("account_status"),
                    AccountStatus.entries.toTypedArray(),
                ),
                settingsAvailability = enum(
                    "settings_availability",
                    root.getString("settings_availability"),
                    SettingsAvailability.entries.toTypedArray(),
                ),
                restrictions = restrictions,
                validUntilEpochMillis = root.getLong("valid_until_epoch_millis"),
            ),
        )
    } catch (_: Exception) {
        RemoteAccountBootstrapResult.Malformed
    }
}

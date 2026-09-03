package com.patsy.app.auth

import java.util.Arrays

/**
 * Boundary implemented by the future HTTPS backend adapter.
 *
 * No implementation in the APK may grant OWNER, confirm delivery, or accept credentials locally.
 */
interface AuthGateway {
    suspend fun startRegistration(request: StartRegistrationRequest): RegistrationStartResult
    suspend fun completeRegistration(request: CompleteRegistrationRequest): RegistrationResult
    suspend fun confirmEmail(request: ConfirmEmailRequest): EmailConfirmationResult
    suspend fun login(request: LoginRequest): LoginResult
    suspend fun requestPasswordReset(request: PasswordResetRequest): PasswordResetResult
    suspend fun restoreSession(): SessionState
    suspend fun signOut(): SignOutResult
}

data class StartRegistrationRequest(
    val username: String,
    val email: String,
    val experienceMode: String,
)

/** Registration is deliberately split so username/email always precede password setup. */
sealed interface RegistrationStartResult {
    data class ReadyForPassword(
        val registrationAttemptId: String,
        val normalizedUsername: String,
        val maskedEmail: String,
    ) : RegistrationStartResult

    data class Rejected(val failure: AuthFailure) : RegistrationStartResult
    data class Unavailable(val failure: ServiceFailure) : RegistrationStartResult
}

class CompleteRegistrationRequest(
    val registrationAttemptId: String,
    val password: SecretChars,
) {
    override fun toString(): String =
        "CompleteRegistrationRequest(registrationAttemptId=$registrationAttemptId, password=[REDACTED])"
}

sealed interface RegistrationResult {
    data class AccountCreated(
        val userId: String,
        val username: String,
        val confirmationEmail: ConfirmationEmailAcknowledgement,
    ) : RegistrationResult

    data class Rejected(val failure: AuthFailure) : RegistrationResult
    data class Unavailable(val failure: ServiceFailure) : RegistrationResult
}

/**
 * The UI may say an email was queued/sent only for [Confirmed]. All other states must remain honest.
 */
sealed interface ConfirmationEmailAcknowledgement {
    data class Confirmed(
        val deliveryState: ConfirmedEmailDeliveryState,
        val maskedEmail: String,
        val providerReceiptId: String? = null,
    ) : ConfirmationEmailAcknowledgement

    data class NotConfirmed(
        val reason: EmailDeliveryFailure,
        val retryAllowed: Boolean,
    ) : ConfirmationEmailAcknowledgement
}

enum class ConfirmedEmailDeliveryState { QUEUED, SENT }

enum class EmailDeliveryFailure {
    PROVIDER_NOT_CONFIGURED,
    PROVIDER_REJECTED,
    NETWORK_UNAVAILABLE,
    SERVER_ERROR,
    UNKNOWN,
}

data class ConfirmEmailRequest(val token: SecretChars)

sealed interface EmailConfirmationResult {
    data class Verified(val userId: String) : EmailConfirmationResult
    data class Rejected(val failure: AuthFailure) : EmailConfirmationResult
    data class Unavailable(val failure: ServiceFailure) : EmailConfirmationResult
}

enum class LoginSessionRetention {
    CURRENT_PROCESS_ONLY,
    RESTORE_ON_NEXT_LAUNCH,
}

class LoginRequest(
    val identifier: LoginIdentifier,
    val password: SecretChars,
    val sessionRetention: LoginSessionRetention = LoginSessionRetention.CURRENT_PROCESS_ONLY,
) {
    override fun toString(): String =
        "LoginRequest(identifier=$identifier, password=[REDACTED], sessionRetention=$sessionRetention)"
}

sealed interface LoginResult {
    data class Authenticated(val session: PublicSession) : LoginResult
    data class Rejected(val failure: AuthFailure) : LoginResult
    data class Unavailable(val failure: ServiceFailure) : LoginResult
}

data class PasswordResetRequest(val emailOrUsername: String)

sealed interface PasswordResetResult {
    /** Deliberately does not reveal whether the account exists. */
    data class RequestAccepted(val genericMessage: String) : PasswordResetResult
    data class Unavailable(val failure: ServiceFailure) : PasswordResetResult
}

data class ConfirmEmailRequestResult(val confirmation: EmailConfirmationResult)

sealed interface SignOutResult {
    data object SignedOut : SignOutResult
    data class Unavailable(val failure: ServiceFailure) : SignOutResult
}

sealed interface SessionState {
    data object Anonymous : SessionState
    data class Authenticated(val session: PublicSession) : SessionState
    data class Expired(val reason: SessionEndReason) : SessionState
    data class Unavailable(val failure: ServiceFailure) : SessionState
}

/** Safe-to-display session metadata. Privileged credentials stay inside the gateway/session adapter. */
data class PublicSession(
    val sessionId: String,
    val userId: String,
    val username: String,
    val maskedEmail: String,
    val emailVerified: Boolean,
    val expiresAtEpochMillis: Long,
)

enum class SessionEndReason { EXPIRED, REVOKED, SIGNED_OUT, UNKNOWN }

sealed interface AuthFailure {
    data object DuplicateUsername : AuthFailure
    data object DuplicateEmail : AuthFailure
    data object InvalidCredentials : AuthFailure
    data object InvalidOrExpiredToken : AuthFailure
    data class InvalidInput(val field: String, val code: String) : AuthFailure
    data object RateLimited : AuthFailure
}

sealed interface ServiceFailure {
    data object Offline : ServiceFailure
    data object NotConfigured : ServiceFailure
    data object Timeout : ServiceFailure
    data object ServerError : ServiceFailure
    data object Unknown : ServiceFailure
}

/** Mutable secret container whose value is redacted from logs and can be erased after transport. */
class SecretChars private constructor(private val chars: CharArray) : AutoCloseable {
    val length: Int get() = chars.size

    fun <T> useCopy(block: (CharArray) -> T): T {
        val copy = chars.copyOf()
        return try {
            block(copy)
        } finally {
            Arrays.fill(copy, '\u0000')
        }
    }

    suspend fun <T> useCopySuspending(block: suspend (CharArray) -> T): T {
        val copy = chars.copyOf()
        return try {
            block(copy)
        } finally {
            Arrays.fill(copy, '\u0000')
        }
    }

    override fun close() {
        Arrays.fill(chars, '\u0000')
    }

    override fun toString(): String = "[REDACTED]"

    companion object {
        fun copyOf(value: CharArray): SecretChars = SecretChars(value.copyOf())
    }
}

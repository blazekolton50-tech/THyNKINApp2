package com.patsy.app.services

import com.patsy.app.security.PatsyRole

/** Modes from the locked Patsy specification. Unknown ages stay protected. */
enum class AgeMode { SIXTEEN_PLUS, UNDER_SIXTEEN, PROTECTED }

enum class Capability {
    DIRECT_MESSAGES,
    CREATION_STUDIO,
    SCHEDULING,
    PATSY_AI,
    WEB_SEARCH
}

enum class Permission { ALLOWED, RESTRICTED, DISABLED }

/**
 * A server-issued policy snapshot. Every service request carries this snapshot so
 * an adapter cannot accidentally drop age-mode and safety decisions.
 */
data class SafetyContext(
    val ageMode: AgeMode,
    val policyRevision: String,
    val directMessages: Permission,
    val generatedContent: Permission,
    val publicScheduling: Permission,
    val externalSearch: Permission
)

/**
 * Authenticated service identity. Production implementations must obtain this
 * through [AuthenticatedAccessBoundary]; values created locally are not proof of
 * identity or OWNER authority.
 */
data class AuthenticatedContext(
    val userId: String,
    val sessionId: String,
    val roles: Set<PatsyRole>,
    val safety: SafetyContext
)

/** The only intended entry point from an opaque session credential to services. */
interface AuthenticatedAccessBoundary {
    suspend fun authenticate(sessionCredential: String): ServiceResult<AuthenticatedContext>
}

enum class UnavailableReason {
    NOT_CONFIGURED,
    OFFLINE,
    PROVIDER_OUTAGE,
    CONNECTION_REQUIRED,
    RATE_LIMITED
}

enum class DenialReason {
    NOT_AUTHENTICATED,
    SESSION_EXPIRED,
    NOT_AUTHORISED,
    AGE_MODE_RESTRICTION,
    SAFETY_POLICY,
    BLOCKED_RELATIONSHIP
}

/** Honest states shared by provider adapters; unavailable is never reported as success. */
sealed interface ServiceResult<out T> {
    data class Success<T>(val value: T) : ServiceResult<T>

    data class Unavailable(
        val capability: Capability,
        val reason: UnavailableReason,
        val retryable: Boolean,
        val publicMessage: String
    ) : ServiceResult<Nothing>

    data class Denied(
        val reason: DenialReason,
        val publicMessage: String
    ) : ServiceResult<Nothing>

    data class Invalid(
        val fieldErrors: Map<String, String>
    ) : ServiceResult<Nothing>

    data class Failure(
        val safeCode: String,
        val retryable: Boolean,
        val publicMessage: String
    ) : ServiceResult<Nothing>
}

data class Page<T>(
    val items: List<T>,
    val nextCursor: String? = null
)

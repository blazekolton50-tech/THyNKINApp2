package com.patsy.app.security

import com.patsy.app.auth.PublicSession
import com.patsy.app.auth.ServiceFailure
import java.util.concurrent.CancellationException

/**
 * Server-backed Owner authorisation. A username, local role, profile switch, hidden route, or
 * modified APK state is never proof of Owner access.
 */
interface OwnerAuthorizationService {
    suspend fun verify(
        session: PublicSession,
        capability: OwnerCapability,
    ): OwnerAuthorizationDecision
}

enum class OwnerCapability {
    VIEW_OWNER_PROFILE,
    VIEW_OWNER_TOOLS,
    MANAGE_CONTENT,
    MANAGE_SCHEDULE,
    VIEW_ANALYTICS,
    VIEW_SECURITY_AUDIT,
    MANAGE_PRIVACY,
    MANAGE_BACKUPS,
}

sealed interface OwnerAuthorizationDecision {
    /** A short-lived server decision. The server must still enforce every privileged API call. */
    data class Allowed(
        val authorizationId: String,
        val capability: OwnerCapability,
        val expiresAtEpochMillis: Long,
        val auditCorrelationId: String,
    ) : OwnerAuthorizationDecision

    data class Denied(val reason: OwnerDenialReason) : OwnerAuthorizationDecision
    data class Unavailable(val failure: ServiceFailure) : OwnerAuthorizationDecision
}

enum class OwnerDenialReason {
    NOT_AUTHENTICATED,
    NOT_OWNER,
    SESSION_EXPIRED,
    SESSION_REVOKED,
    RECENT_AUTHENTICATION_REQUIRED,
    CAPABILITY_NOT_GRANTED,
}

/** Catches transport/provider failures and converts them to a fail-closed unavailable decision. */
class FailClosedOwnerAuthorizationGate(
    private val service: OwnerAuthorizationService,
) {
    suspend fun verify(
        session: PublicSession?,
        capability: OwnerCapability,
    ): OwnerAuthorizationDecision {
        if (session == null) {
            return OwnerAuthorizationDecision.Denied(OwnerDenialReason.NOT_AUTHENTICATED)
        }

        return try {
            service.verify(session, capability)
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (_: Exception) {
            OwnerAuthorizationDecision.Unavailable(ServiceFailure.Unknown)
        }
    }
}

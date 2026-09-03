package com.patsy.app.security

import com.patsy.app.auth.AuthSessionStore
import com.patsy.app.auth.PublicSession
import com.patsy.app.auth.ServiceFailure

sealed interface RemoteOwnerAuthorizationResult {
    data class Allowed(
        val authorizationId: String,
        val capability: OwnerCapability,
        val expiresAtEpochMillis: Long,
        val auditCorrelationId: String,
    ) : RemoteOwnerAuthorizationResult

    data object Denied : RemoteOwnerAuthorizationResult
    data object Unauthorized : RemoteOwnerAuthorizationResult
    data class Unavailable(val failure: ServiceFailure) : RemoteOwnerAuthorizationResult
}

interface OwnerAuthorizationTransport {
    suspend fun verify(
        accessToken: String,
        capability: OwnerCapability,
    ): RemoteOwnerAuthorizationResult
}

class SupabaseOwnerAuthorizationService(
    private val sessionStore: AuthSessionStore,
    private val transport: OwnerAuthorizationTransport,
    private val nowEpochMillis: () -> Long = System::currentTimeMillis,
) : OwnerAuthorizationService {
    override suspend fun verify(
        session: PublicSession,
        capability: OwnerCapability,
    ): OwnerAuthorizationDecision {
        val stored = sessionStore.read()
            ?: return OwnerAuthorizationDecision.Denied(OwnerDenialReason.NOT_AUTHENTICATED)

        val now = nowEpochMillis()
        if (session.expiresAtEpochMillis <= now || stored.publicSession.expiresAtEpochMillis <= now) {
            return OwnerAuthorizationDecision.Denied(OwnerDenialReason.SESSION_EXPIRED)
        }

        if (
            stored.publicSession.sessionId != session.sessionId ||
            stored.publicSession.userId != session.userId
        ) {
            return OwnerAuthorizationDecision.Denied(OwnerDenialReason.SESSION_REVOKED)
        }

        return when (val remote = transport.verify(stored.accessToken, capability)) {
            is RemoteOwnerAuthorizationResult.Allowed -> {
                if (
                    remote.capability != capability ||
                    remote.authorizationId.isBlank() ||
                    remote.auditCorrelationId.isBlank() ||
                    remote.expiresAtEpochMillis <= now
                ) {
                    OwnerAuthorizationDecision.Unavailable(ServiceFailure.Unknown)
                } else {
                    OwnerAuthorizationDecision.Allowed(
                        authorizationId = remote.authorizationId,
                        capability = remote.capability,
                        expiresAtEpochMillis = remote.expiresAtEpochMillis,
                        auditCorrelationId = remote.auditCorrelationId,
                    )
                }
            }

            RemoteOwnerAuthorizationResult.Denied ->
                OwnerAuthorizationDecision.Denied(OwnerDenialReason.NOT_OWNER)

            RemoteOwnerAuthorizationResult.Unauthorized ->
                OwnerAuthorizationDecision.Denied(OwnerDenialReason.SESSION_REVOKED)

            is RemoteOwnerAuthorizationResult.Unavailable ->
                OwnerAuthorizationDecision.Unavailable(remote.failure)
        }
    }
}

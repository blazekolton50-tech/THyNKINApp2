package com.patsy.app.security

/** Secondary Owner routes require a current, capability-specific server grant. */
object FinalOwnerAccessPolicy {
    fun canOpen(
        grant: OwnerAuthorizationDecision.Allowed?,
        capability: OwnerCapability,
        nowEpochMillis: Long = System.currentTimeMillis(),
    ): Boolean =
        grant != null &&
            grant.capability == capability &&
            grant.authorizationId.isNotBlank() &&
            grant.auditCorrelationId.isNotBlank() &&
            grant.expiresAtEpochMillis > nowEpochMillis
}

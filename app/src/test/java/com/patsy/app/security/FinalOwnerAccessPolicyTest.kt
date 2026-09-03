package com.patsy.app.security

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FinalOwnerAccessPolicyTest {
    private val now = 1_000_000L

    private fun grant(
        capability: OwnerCapability,
        expiresAt: Long = now + 60_000L,
    ) = OwnerAuthorizationDecision.Allowed(
        authorizationId = "authz-${capability.name}",
        capability = capability,
        expiresAtEpochMillis = expiresAt,
        auditCorrelationId = "audit-${capability.name}",
    )

    @Test
    fun exactCurrentProfileGrantAllowsOnlyOwnerProfile() {
        val profile = grant(OwnerCapability.VIEW_OWNER_PROFILE)

        assertTrue(FinalOwnerAccessPolicy.canOpen(profile, OwnerCapability.VIEW_OWNER_PROFILE, now))
        assertFalse(FinalOwnerAccessPolicy.canOpen(profile, OwnerCapability.VIEW_OWNER_TOOLS, now))
    }

    @Test
    fun exactCurrentToolsGrantAllowsOnlyOwnerTools() {
        val tools = grant(OwnerCapability.VIEW_OWNER_TOOLS)

        assertTrue(FinalOwnerAccessPolicy.canOpen(tools, OwnerCapability.VIEW_OWNER_TOOLS, now))
        assertFalse(FinalOwnerAccessPolicy.canOpen(tools, OwnerCapability.VIEW_OWNER_PROFILE, now))
    }

    @Test
    fun expiredOrMissingGrantFailsClosed() {
        assertFalse(FinalOwnerAccessPolicy.canOpen(null, OwnerCapability.VIEW_OWNER_PROFILE, now))
        assertFalse(
            FinalOwnerAccessPolicy.canOpen(
                grant(OwnerCapability.VIEW_OWNER_PROFILE, expiresAt = now),
                OwnerCapability.VIEW_OWNER_PROFILE,
                now,
            ),
        )
    }
}

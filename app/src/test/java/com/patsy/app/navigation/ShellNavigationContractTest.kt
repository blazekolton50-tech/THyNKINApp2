package com.patsy.app.navigation

import com.patsy.app.account.AccountBootstrap
import com.patsy.app.account.AccountStatus
import com.patsy.app.account.OnboardingState
import com.patsy.app.account.ProfileReadiness
import com.patsy.app.account.SettingsAvailability
import com.patsy.app.account.ShellRestriction
import com.patsy.app.account.TrustedAgeState
import com.patsy.app.account.isProtectedMode
import com.patsy.app.security.OwnerCapability
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class ShellNavigationContractTest {
    private fun account(
        age: TrustedAgeState = TrustedAgeState.STANDARD_16_PLUS,
        onboarding: OnboardingState = OnboardingState.READY,
        restrictions: Set<ShellRestriction> = emptySet(),
        capabilities: Set<OwnerCapability> = emptySet(),
    ) = AccountBootstrap(
        canonicalUserId = "user-1",
        profileReadiness = ProfileReadiness.READY,
        onboardingState = onboarding,
        ageState = age,
        capabilities = capabilities,
        accountStatus = AccountStatus.ACTIVE,
        settingsAvailability = SettingsAvailability.AVAILABLE,
        restrictions = restrictions,
        validUntilEpochMillis = 9_000L,
    )

    @Test fun primaryRoutesHaveExactStableOrderLabelsAndMeaning() {
        assertEquals(
            listOf("home", "thynk", "create", "patsy_dms", "profile"),
            ShellNavigationContract.primaryRoutes.map { it.id },
        )
        assertEquals(
            listOf("Home", "THyNK", "Create", "Patsy DMs", "Profile"),
            ShellNavigationContract.primaryRoutes.map { it.label },
        )
        assertEquals(ShellDestination.HOME_FEED, ShellNavigationContract.route("home")?.destination)
        assertFalse(ShellNavigationContract.primaryRoutes.any { it.id == "schedule" || it.id == "more" })
    }

    @Test fun unknownAgeIsProtectedAndDmDeepLinkCannotBypassGate() {
        val protected = account(age = TrustedAgeState.UNKNOWN_UNVERIFIED)
        assertTrue(protected.isProtectedMode)
        val result = ShellNavigationGate.resolveDeepLink("patsy://patsy_dms/thread/guessed", protected)
        val denied = assertIs<NavigationDecision.Denied>(result)
        assertEquals(ShellDestination.SAFE_PROTECTED, denied.fallback)
    }

    @Test fun localAgeInputCannotChangeTrustedDomain() {
        val server = account(age = TrustedAgeState.UNDER_16_PROTECTED)
        val result = ShellNavigationGate.resolve("home", server, localAgeChoice = "16+ Patsy")
        val allowed = assertIs<NavigationDecision.Allowed>(result)
        assertEquals(AccountDomain.PROTECTED, allowed.domain)
    }

    @Test fun ordinaryAdultDoesNotGainOwnerAndOneCapabilityDoesNotGrantAnother() {
        assertIs<NavigationDecision.Denied>(ShellNavigationGate.resolve("owner_tools", account()))
        val analyticsOnly = account(capabilities = setOf(OwnerCapability.VIEW_ANALYTICS))
        assertIs<NavigationDecision.Denied>(ShellNavigationGate.resolve("owner_tools", analyticsOnly))
        assertIs<NavigationDecision.Allowed>(ShellNavigationGate.resolve("owner_analytics", analyticsOnly))
    }

    @Test fun onboardingResumesBeforeFeatureContent() {
        val result = ShellNavigationGate.resolve("home", account(onboarding = OnboardingState.NEEDS_AGE_STEP))
        val denied = assertIs<NavigationDecision.Denied>(result)
        assertEquals(ShellDestination.ONBOARDING_AGE, denied.fallback)
    }
}

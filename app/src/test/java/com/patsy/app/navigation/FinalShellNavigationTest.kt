package com.patsy.app.navigation

import com.patsy.app.account.AccountBootstrap
import com.patsy.app.account.AccountStatus
import com.patsy.app.account.OnboardingState
import com.patsy.app.account.ProfileReadiness
import com.patsy.app.account.SettingsAvailability
import com.patsy.app.account.TrustedAgeState
import com.patsy.app.security.OwnerCapability
import com.patsy.app.ui.finaldesign.FinalHomeDestination
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class FinalShellNavigationTest {
    private fun account(age: TrustedAgeState) = AccountBootstrap(
        canonicalUserId = "user-1",
        profileReadiness = ProfileReadiness.READY,
        onboardingState = OnboardingState.READY,
        ageState = age,
        capabilities = emptySet<OwnerCapability>(),
        accountStatus = AccountStatus.ACTIVE,
        settingsAvailability = SettingsAvailability.AVAILABLE,
        restrictions = emptySet(),
        validUntilEpochMillis = Long.MAX_VALUE,
    )

    @Test fun finalDestinationsMapToLockedSemanticRoutes() {
        assertEquals("home", FinalShellNavigation.routeId(FinalHomeDestination.HOME))
        assertEquals("thynk", FinalShellNavigation.routeId(FinalHomeDestination.THYNK))
        assertEquals("create", FinalShellNavigation.routeId(FinalHomeDestination.CREATE))
        assertEquals("patsy_dms", FinalShellNavigation.routeId(FinalHomeDestination.PATSY_DMS))
        assertEquals("profile", FinalShellNavigation.routeId(FinalHomeDestination.PROFILE))
    }

    @Test fun protectedAccountCannotReachProviderOrDmDestinations() {
        val protected = account(TrustedAgeState.UNDER_16_PROTECTED)
        assertIs<NavigationDecision.Denied>(FinalShellNavigation.authorize(FinalHomeDestination.THYNK, protected))
        assertIs<NavigationDecision.Denied>(FinalShellNavigation.authorize(FinalHomeDestination.CREATE, protected))
        assertIs<NavigationDecision.Denied>(FinalShellNavigation.authorize(FinalHomeDestination.PATSY_DMS, protected))
        assertIs<NavigationDecision.Allowed>(FinalShellNavigation.authorize(FinalHomeDestination.PROFILE, protected))
    }

    @Test fun trustedStandardAccountCanReachAllFivePrimaryDestinations() {
        val standard = account(TrustedAgeState.STANDARD_16_PLUS)
        FinalHomeDestination.entries.forEach { destination ->
            assertIs<NavigationDecision.Allowed>(FinalShellNavigation.authorize(destination, standard))
        }
    }
}

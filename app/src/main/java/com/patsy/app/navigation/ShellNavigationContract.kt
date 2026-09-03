package com.patsy.app.navigation

import com.patsy.app.account.AccountBootstrap
import com.patsy.app.account.AccountStatus
import com.patsy.app.account.OnboardingState
import com.patsy.app.account.ShellRestriction
import com.patsy.app.account.TrustedAgeState
import com.patsy.app.account.isProtectedMode
import com.patsy.app.security.OwnerCapability

enum class ShellDestination {
    HOME_FEED, THYNK, CREATE, DMS, PROFILE, CHAT, SCHEDULE,
    THYNK_TEMPLATES, THYNK_EDITOR, THYNK_AI_IMAGE, THYNK_AI_VIDEO,
    THYNK_PROJECTS, THYNK_BRAND_KIT, THYNK_INSPIRATION,
    OWNER_PROFILE, OWNER_TOOLS, OWNER_ANALYTICS,
    ONBOARDING_PROFILE, ONBOARDING_AGE, ONBOARDING_EMAIL, ONBOARDING_PROTECTED,
    SAFE_PROTECTED,
}

enum class AccountDomain { PROTECTED, STANDARD, SAFEGUARDING }

data class ShellRoute(
    val id: String,
    val label: String,
    val destination: ShellDestination,
    val primary: Boolean,
    val blockedBy: ShellRestriction? = null,
    val requiredCapability: OwnerCapability? = null,
    val fallback: ShellDestination = ShellDestination.SAFE_PROTECTED,
)

object ShellNavigationContract {
    private val routes = listOf(
        ShellRoute("home", "Home", ShellDestination.HOME_FEED, true, ShellRestriction.SOCIAL_RESTRICTED),
        ShellRoute("thynk", "THyNK", ShellDestination.THYNK, true, ShellRestriction.PROVIDERS_RESTRICTED),
        ShellRoute("create", "Create", ShellDestination.CREATE, true, ShellRestriction.PROVIDERS_RESTRICTED),
        ShellRoute("patsy_dms", "Patsy DMs", ShellDestination.DMS, true, ShellRestriction.DMS_RESTRICTED),
        ShellRoute("profile", "Profile", ShellDestination.PROFILE, true),
        ShellRoute("chat", "Ask Patsy", ShellDestination.CHAT, false, ShellRestriction.PROVIDERS_RESTRICTED),
        ShellRoute("schedule", "Schedule", ShellDestination.SCHEDULE, false),
        ShellRoute("thynk_templates", "Templates", ShellDestination.THYNK_TEMPLATES, false, ShellRestriction.PROVIDERS_RESTRICTED),
        ShellRoute("thynk_editor", "Editor", ShellDestination.THYNK_EDITOR, false, ShellRestriction.PROVIDERS_RESTRICTED),
        ShellRoute("thynk_ai_image", "AI Image", ShellDestination.THYNK_AI_IMAGE, false, ShellRestriction.PROVIDERS_RESTRICTED),
        ShellRoute("thynk_ai_video", "AI Video", ShellDestination.THYNK_AI_VIDEO, false, ShellRestriction.PROVIDERS_RESTRICTED),
        ShellRoute("thynk_projects", "Projects", ShellDestination.THYNK_PROJECTS, false),
        ShellRoute("thynk_brand_kit", "Brand Kit", ShellDestination.THYNK_BRAND_KIT, false),
        ShellRoute("thynk_inspiration", "Inspiration", ShellDestination.THYNK_INSPIRATION, false, ShellRestriction.PROVIDERS_RESTRICTED),
        ShellRoute("owner_profile", "Owner Profile", ShellDestination.OWNER_PROFILE, false, ShellRestriction.OWNER_RESTRICTED, OwnerCapability.VIEW_OWNER_PROFILE),
        ShellRoute("owner_tools", "Owner Tools", ShellDestination.OWNER_TOOLS, false, ShellRestriction.OWNER_RESTRICTED, OwnerCapability.VIEW_OWNER_TOOLS),
        ShellRoute("owner_analytics", "Owner Analytics", ShellDestination.OWNER_ANALYTICS, false, ShellRestriction.OWNER_RESTRICTED, OwnerCapability.VIEW_ANALYTICS),
    )

    val primaryRoutes: List<ShellRoute> = routes.filter { it.primary }

    fun route(id: String): ShellRoute? = routes.firstOrNull { it.id == id }
}

sealed interface NavigationDecision {
    data class Allowed(val route: ShellRoute, val domain: AccountDomain) : NavigationDecision
    data class Denied(
        val requestedRouteId: String,
        val fallback: ShellDestination,
        val reason: NavigationDenial,
    ) : NavigationDecision
}

enum class NavigationDenial {
    UNKNOWN_ROUTE,
    ONBOARDING_INCOMPLETE,
    ACCOUNT_UNAVAILABLE,
    RESTRICTED,
    CAPABILITY_MISSING,
}

object ShellNavigationGate {
    fun resolveDeepLink(uri: String, account: AccountBootstrap): NavigationDecision {
        val withoutScheme = uri.substringAfter("://", "")
        val routeId = withoutScheme.substringBefore('/').substringBefore('?')
        return resolve(routeId, account)
    }

    @Suppress("UNUSED_PARAMETER")
    fun resolve(
        routeId: String,
        account: AccountBootstrap,
        localAgeChoice: String? = null,
    ): NavigationDecision {
        val route = ShellNavigationContract.route(routeId)
            ?: return NavigationDecision.Denied(
                routeId,
                ShellDestination.SAFE_PROTECTED,
                NavigationDenial.UNKNOWN_ROUTE,
            )

        onboardingFallback(account.onboardingState)?.let {
            return NavigationDecision.Denied(routeId, it, NavigationDenial.ONBOARDING_INCOMPLETE)
        }

        if (account.accountStatus != AccountStatus.ACTIVE) {
            return NavigationDecision.Denied(
                routeId,
                ShellDestination.SAFE_PROTECTED,
                NavigationDenial.ACCOUNT_UNAVAILABLE,
            )
        }

        if (route.blockedBy != null && route.blockedBy in account.restrictions) {
            return NavigationDecision.Denied(routeId, route.fallback, NavigationDenial.RESTRICTED)
        }

        if (
            account.isProtectedMode &&
            route.blockedBy in setOf(ShellRestriction.DMS_RESTRICTED, ShellRestriction.PROVIDERS_RESTRICTED)
        ) {
            return NavigationDecision.Denied(routeId, route.fallback, NavigationDenial.RESTRICTED)
        }

        if (route.requiredCapability != null && route.requiredCapability !in account.capabilities) {
            return NavigationDecision.Denied(routeId, route.fallback, NavigationDenial.CAPABILITY_MISSING)
        }

        return NavigationDecision.Allowed(route, account.domain())
    }

    private fun onboardingFallback(state: OnboardingState): ShellDestination? = when (state) {
        OnboardingState.NEEDS_PROFILE -> ShellDestination.ONBOARDING_PROFILE
        OnboardingState.NEEDS_AGE_STEP -> ShellDestination.ONBOARDING_AGE
        OnboardingState.NEEDS_EMAIL_CONFIRMATION -> ShellDestination.ONBOARDING_EMAIL
        OnboardingState.NEEDS_PROTECTED_SETUP -> ShellDestination.ONBOARDING_PROTECTED
        OnboardingState.READY -> null
    }
}

private fun AccountBootstrap.domain(): AccountDomain = when {
    isProtectedMode -> AccountDomain.PROTECTED
    ageState == TrustedAgeState.SAFEGUARDING_AUTHORIZED -> AccountDomain.SAFEGUARDING
    else -> AccountDomain.STANDARD
}

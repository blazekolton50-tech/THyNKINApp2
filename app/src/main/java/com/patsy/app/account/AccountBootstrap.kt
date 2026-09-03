package com.patsy.app.account

import com.patsy.app.security.OwnerCapability

enum class ProfileReadiness { MISSING, INCOMPLETE, READY }
enum class TrustedAgeState { UNKNOWN_UNVERIFIED, UNDER_16_PROTECTED, STANDARD_16_PLUS, SAFEGUARDING_AUTHORIZED }
enum class OnboardingState { NEEDS_PROFILE, NEEDS_AGE_STEP, NEEDS_EMAIL_CONFIRMATION, NEEDS_PROTECTED_SETUP, READY }
enum class AccountStatus { ACTIVE, RESTRICTED, DISABLED }
enum class SettingsAvailability { AVAILABLE, UNAVAILABLE }
enum class ShellRestriction { PROTECTED_CONTENT_ONLY, DMS_RESTRICTED, SOCIAL_RESTRICTED, PROVIDERS_RESTRICTED, OWNER_RESTRICTED }

data class AccountBootstrap(
    val canonicalUserId: String,
    val profileReadiness: ProfileReadiness,
    val onboardingState: OnboardingState,
    val ageState: TrustedAgeState,
    val capabilities: Set<OwnerCapability>,
    val accountStatus: AccountStatus,
    val settingsAvailability: SettingsAvailability,
    val restrictions: Set<ShellRestriction>,
    val validUntilEpochMillis: Long,
)

sealed interface AccountBootstrapResult {
    data class Available(val account: AccountBootstrap) : AccountBootstrapResult
    data class FailedClosed(val reason: BootstrapFailure, val protectedState: AccountBootstrap) : AccountBootstrapResult
}

enum class BootstrapFailure {
    NOT_AUTHENTICATED,
    SESSION_EXPIRED,
    SESSION_MISMATCH,
    UNAUTHORIZED,
    FORBIDDEN,
    BACKEND_UNAVAILABLE,
    MALFORMED,
    INCONSISTENT,
    EXPIRED,
}

val AccountBootstrap.isProtectedMode: Boolean
    get() = ageState == TrustedAgeState.UNKNOWN_UNVERIFIED ||
        ageState == TrustedAgeState.UNDER_16_PROTECTED ||
        ShellRestriction.PROTECTED_CONTENT_ONLY in restrictions

internal fun AccountBootstrap.validationFailure(nowEpochMillis: Long): BootstrapFailure? = when {
    canonicalUserId.isBlank() -> BootstrapFailure.MALFORMED
    validUntilEpochMillis <= nowEpochMillis -> BootstrapFailure.EXPIRED
    onboardingState == OnboardingState.READY && profileReadiness != ProfileReadiness.READY -> BootstrapFailure.INCONSISTENT
    onboardingState == OnboardingState.READY && ageState == TrustedAgeState.UNKNOWN_UNVERIFIED -> BootstrapFailure.INCONSISTENT
    ageState == TrustedAgeState.UNKNOWN_UNVERIFIED && capabilities.isNotEmpty() -> BootstrapFailure.INCONSISTENT
    accountStatus != AccountStatus.ACTIVE && capabilities.isNotEmpty() -> BootstrapFailure.INCONSISTENT
    else -> null
}

fun protectedBootstrap(userId: String): AccountBootstrap = AccountBootstrap(
    canonicalUserId = userId,
    profileReadiness = ProfileReadiness.MISSING,
    onboardingState = OnboardingState.NEEDS_PROTECTED_SETUP,
    ageState = TrustedAgeState.UNKNOWN_UNVERIFIED,
    capabilities = emptySet(),
    accountStatus = AccountStatus.RESTRICTED,
    settingsAvailability = SettingsAvailability.UNAVAILABLE,
    restrictions = ShellRestriction.entries.toSet(),
    validUntilEpochMillis = 0L,
)

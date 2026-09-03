package com.patsy.app.services.state

import com.patsy.app.services.AgeMode
import com.patsy.app.services.AuthenticatedContext
import com.patsy.app.services.Capability
import com.patsy.app.services.DenialReason
import com.patsy.app.services.Permission
import com.patsy.app.services.ServiceResult
import com.patsy.app.services.UnavailableReason
import java.util.concurrent.CancellationException

/**
 * Non-sensitive context that lets a screen explain which server policy was applied.
 * Session credentials and role claims are deliberately not copied into UI state.
 */
data class FeatureRequestScope(
    val userId: String,
    val ageMode: AgeMode,
    val policyRevision: String,
)

/** Honest screen states shared by the provider-neutral feature coordinators. */
sealed interface FeatureUiState<out T> {
    data object Idle : FeatureUiState<Nothing>

    data class Loading(
        val scope: FeatureRequestScope,
    ) : FeatureUiState<Nothing>

    data class Success<T>(
        val scope: FeatureRequestScope,
        val value: T,
    ) : FeatureUiState<T>

    data class Unavailable(
        val scope: FeatureRequestScope,
        val capability: Capability,
        val reason: UnavailableReason,
        val retryable: Boolean,
        val message: String,
    ) : FeatureUiState<Nothing>

    data class Denied(
        val scope: FeatureRequestScope,
        val reason: DenialReason,
        val message: String,
    ) : FeatureUiState<Nothing>

    data class Error(
        val scope: FeatureRequestScope,
        val safeCode: String,
        val retryable: Boolean,
        val message: String,
        val fieldErrors: Map<String, String> = emptyMap(),
    ) : FeatureUiState<Nothing>
}

internal object FeatureRequestPolicy {
    fun scope(context: AuthenticatedContext): FeatureRequestScope = FeatureRequestScope(
        userId = context.userId,
        ageMode = context.safety.ageMode,
        policyRevision = context.safety.policyRevision,
    )

    /**
     * This is a fail-closed client preflight, not an authorisation decision. A trusted service
     * must still revalidate the session and policy on every operation.
     */
    fun denial(
        context: AuthenticatedContext,
        capability: Capability,
    ): FeatureUiState.Denied? {
        val scope = scope(context)
        if (context.userId.isBlank() || context.sessionId.isBlank()) {
            return FeatureUiState.Denied(
                scope = scope,
                reason = DenialReason.NOT_AUTHENTICATED,
                message = "Sign in again to use this feature.",
            )
        }

        if (context.safety.ageMode == AgeMode.PROTECTED) {
            return FeatureUiState.Denied(
                scope = scope,
                reason = DenialReason.AGE_MODE_RESTRICTION,
                message = "This feature is unavailable while the account age mode is protected.",
            )
        }

        val permission = when (capability) {
            Capability.DIRECT_MESSAGES -> context.safety.directMessages
            Capability.CREATION_STUDIO -> context.safety.generatedContent
            Capability.SCHEDULING -> context.safety.publicScheduling
            Capability.PATSY_AI -> Permission.RESTRICTED
            Capability.WEB_SEARCH -> context.safety.externalSearch
        }

        if (permission == Permission.DISABLED) {
            val ageRestricted = context.safety.ageMode == AgeMode.UNDER_SIXTEEN
            return FeatureUiState.Denied(
                scope = scope,
                reason = if (ageRestricted) {
                    DenialReason.AGE_MODE_RESTRICTION
                } else {
                    DenialReason.SAFETY_POLICY
                },
                message = if (ageRestricted) {
                    "This feature is not available in the current age mode."
                } else {
                    "This feature is disabled by the current safety policy."
                },
            )
        }

        // RESTRICTED is intentionally passed to the trusted service. Only it has enough
        // information to decide whether this particular operation is permitted.
        return null
    }
}

internal fun <T> ServiceResult<T>.toFeatureUiState(
    scope: FeatureRequestScope,
): FeatureUiState<T> = when (this) {
    is ServiceResult.Success -> FeatureUiState.Success(scope, value)
    is ServiceResult.Unavailable -> FeatureUiState.Unavailable(
        scope = scope,
        capability = capability,
        reason = reason,
        retryable = retryable,
        message = publicMessage,
    )
    is ServiceResult.Denied -> FeatureUiState.Denied(
        scope = scope,
        reason = reason,
        message = publicMessage,
    )
    is ServiceResult.Invalid -> FeatureUiState.Error(
        scope = scope,
        safeCode = "INVALID_REQUEST",
        retryable = false,
        message = "Check the highlighted details and try again.",
        fieldErrors = fieldErrors,
    )
    is ServiceResult.Failure -> FeatureUiState.Error(
        scope = scope,
        safeCode = safeCode,
        retryable = retryable,
        message = publicMessage,
    )
}

internal suspend fun <T> callFeatureService(
    scope: FeatureRequestScope,
    request: suspend () -> ServiceResult<T>,
): FeatureUiState<T> = try {
    request().toFeatureUiState(scope)
} catch (cancelled: CancellationException) {
    throw cancelled
} catch (_: Exception) {
    FeatureUiState.Error(
        scope = scope,
        safeCode = "SERVICE_CALL_FAILED",
        retryable = true,
        message = "The service could not be reached. Please try again.",
    )
}

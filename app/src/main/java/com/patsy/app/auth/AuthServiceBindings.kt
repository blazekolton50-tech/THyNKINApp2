package com.patsy.app.auth

import com.patsy.app.account.AccountBootstrapResult
import com.patsy.app.account.AccountBootstrapService
import com.patsy.app.account.BootstrapFailure
import com.patsy.app.account.protectedBootstrap
import com.patsy.app.security.OwnerAuthorizationDecision
import com.patsy.app.security.OwnerAuthorizationService
import com.patsy.app.security.OwnerCapability

/**
 * App-side service boundary. The source defaults to unavailable services until production HTTPS
 * adapters are configured; locally entered credentials or profile state never become authority.
 */
object PatsyServiceBindings {
    var authGateway: AuthGateway = UnconfiguredAuthGateway
    var ownerAuthorizationService: OwnerAuthorizationService = UnconfiguredOwnerAuthorizationService
    var accountBootstrapService: AccountBootstrapService = UnconfiguredAccountBootstrapService
}

private object UnconfiguredAuthGateway : AuthGateway {
    override suspend fun startRegistration(request: StartRegistrationRequest) =
        RegistrationStartResult.Unavailable(ServiceFailure.NotConfigured)

    override suspend fun completeRegistration(request: CompleteRegistrationRequest) =
        RegistrationResult.Unavailable(ServiceFailure.NotConfigured)

    override suspend fun confirmEmail(request: ConfirmEmailRequest) =
        EmailConfirmationResult.Unavailable(ServiceFailure.NotConfigured)

    override suspend fun login(request: LoginRequest) =
        LoginResult.Unavailable(ServiceFailure.NotConfigured)

    override suspend fun requestPasswordReset(request: PasswordResetRequest) =
        PasswordResetResult.Unavailable(ServiceFailure.NotConfigured)

    override suspend fun restoreSession() = SessionState.Unavailable(ServiceFailure.NotConfigured)
    override suspend fun signOut() = SignOutResult.Unavailable(ServiceFailure.NotConfigured)
}

private object UnconfiguredOwnerAuthorizationService : OwnerAuthorizationService {
    override suspend fun verify(
        session: PublicSession,
        capability: OwnerCapability,
    ): OwnerAuthorizationDecision =
        OwnerAuthorizationDecision.Unavailable(ServiceFailure.NotConfigured)
}

private object UnconfiguredAccountBootstrapService : AccountBootstrapService {
    override suspend fun fetch(session: PublicSession): AccountBootstrapResult =
        AccountBootstrapResult.FailedClosed(
            reason = BootstrapFailure.BACKEND_UNAVAILABLE,
            protectedState = protectedBootstrap(session.userId),
        )
}

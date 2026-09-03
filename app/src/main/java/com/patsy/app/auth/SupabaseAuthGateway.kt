package com.patsy.app.auth

/** Tokens never leave this storage/transport boundary as [PublicSession]. */
data class StoredAuthSession(
    val accessToken: String,
    val refreshToken: String,
    val publicSession: PublicSession,
)

interface AuthSessionStore {
    fun read(): StoredAuthSession?
    fun write(session: StoredAuthSession)
    fun clear()
}

data class RemoteAuthSession(
    val accessToken: String,
    val refreshToken: String,
    val sessionId: String,
    val userId: String,
    val username: String,
    val maskedEmail: String,
    val emailVerified: Boolean,
    val expiresAtEpochMillis: Long,
) {
    fun toStored(): StoredAuthSession = StoredAuthSession(
        accessToken = accessToken,
        refreshToken = refreshToken,
        publicSession = PublicSession(
            sessionId = sessionId,
            userId = userId,
            username = username,
            maskedEmail = maskedEmail,
            emailVerified = emailVerified,
            expiresAtEpochMillis = expiresAtEpochMillis,
        ),
    )
}

sealed interface RemoteAuthResult {
    data class Authenticated(val session: RemoteAuthSession) : RemoteAuthResult
    data class Failure(val failure: RemoteAuthFailure) : RemoteAuthResult
}

enum class RemoteAuthFailure {
    InvalidCredentials,
    InvalidSession,
    RateLimited,
    Offline,
    Timeout,
    ServiceUnavailable,
    Unknown,
}

sealed interface RemoteSignOutResult {
    data object SignedOut : RemoteSignOutResult
    data class Unavailable(val failure: ServiceFailure) : RemoteSignOutResult
}

interface SupabaseAuthTransport {
    suspend fun login(identifier: LoginIdentifier, password: CharArray): RemoteAuthResult
    suspend fun refresh(refreshToken: String): RemoteAuthResult
    suspend fun signOut(accessToken: String): RemoteSignOutResult
}

class SupabaseAuthGateway(
    private val transport: SupabaseAuthTransport,
    private val sessionStore: AuthSessionStore,
    private val registrationTransport: SupabaseRegistrationTransport = UnconfiguredRegistrationTransport,
    private val recoveryTransport: SupabaseRecoveryTransport = UnconfiguredRecoveryTransport,
) : AuthGateway {
    override suspend fun startRegistration(request: StartRegistrationRequest): RegistrationStartResult =
        when (val result = registrationTransport.start(request)) {
            is RemoteRegistrationStartResult.Ready -> RegistrationStartResult.ReadyForPassword(
                registrationAttemptId = result.attemptId,
                normalizedUsername = result.normalizedUsername,
                maskedEmail = result.maskedEmail,
            )
            is RemoteRegistrationStartResult.Failure -> result.failure.toRegistrationStartResult()
        }

    override suspend fun completeRegistration(request: CompleteRegistrationRequest): RegistrationResult {
        val result = request.password.useCopySuspending { password ->
            registrationTransport.complete(request.registrationAttemptId, password)
        }
        return when (result) {
            is RemoteRegistrationResult.AccountCreated -> RegistrationResult.AccountCreated(
                userId = result.userId,
                username = result.username,
                confirmationEmail = ConfirmationEmailAcknowledgement.Confirmed(
                    deliveryState = result.deliveryState,
                    maskedEmail = result.maskedEmail,
                ),
            )
            is RemoteRegistrationResult.Failure -> result.failure.toRegistrationResult()
        }
    }

    override suspend fun confirmEmail(request: ConfirmEmailRequest): EmailConfirmationResult =
        EmailConfirmationResult.Unavailable(ServiceFailure.NotConfigured)

    override suspend fun login(request: LoginRequest): LoginResult {
        val remote = request.password.useCopySuspending { password ->
            transport.login(request.identifier, password)
        }
        return when (remote) {
            is RemoteAuthResult.Authenticated -> {
                val stored = remote.session.toStored()
                sessionStore.write(stored)
                LoginResult.Authenticated(stored.publicSession)
            }
            is RemoteAuthResult.Failure -> remote.toLoginResult()
        }
    }

    override suspend fun requestPasswordReset(request: PasswordResetRequest): PasswordResetResult =
        when (val result = recoveryTransport.request(request.emailOrUsername.trim())) {
            is RemotePasswordResetResult.Accepted -> PasswordResetResult.RequestAccepted(result.genericMessage)
            is RemotePasswordResetResult.Unavailable -> PasswordResetResult.Unavailable(result.failure)
        }

    override suspend fun restoreSession(): SessionState {
        val existing = sessionStore.read() ?: return SessionState.Anonymous
        return when (val refreshed = transport.refresh(existing.refreshToken)) {
            is RemoteAuthResult.Authenticated -> {
                val stored = refreshed.session.toStored()
                sessionStore.write(stored)
                SessionState.Authenticated(stored.publicSession)
            }
            is RemoteAuthResult.Failure -> when (refreshed.failure) {
                RemoteAuthFailure.InvalidCredentials, RemoteAuthFailure.InvalidSession -> {
                    sessionStore.clear()
                    SessionState.Expired(SessionEndReason.REVOKED)
                }
                RemoteAuthFailure.RateLimited -> SessionState.Unavailable(ServiceFailure.ServerError)
                RemoteAuthFailure.Offline -> SessionState.Unavailable(ServiceFailure.Offline)
                RemoteAuthFailure.Timeout -> SessionState.Unavailable(ServiceFailure.Timeout)
                RemoteAuthFailure.ServiceUnavailable -> SessionState.Unavailable(ServiceFailure.ServerError)
                RemoteAuthFailure.Unknown -> SessionState.Unavailable(ServiceFailure.Unknown)
            }
        }
    }

    override suspend fun signOut(): SignOutResult {
        val existing = sessionStore.read()
        val result = if (existing == null) RemoteSignOutResult.SignedOut else transport.signOut(existing.accessToken)
        sessionStore.clear()
        return when (result) {
            RemoteSignOutResult.SignedOut -> SignOutResult.SignedOut
            is RemoteSignOutResult.Unavailable -> SignOutResult.Unavailable(result.failure)
        }
    }
}

private fun RemoteAuthResult.Failure.toLoginResult(): LoginResult = when (failure) {
    RemoteAuthFailure.InvalidCredentials, RemoteAuthFailure.InvalidSession -> LoginResult.Rejected(AuthFailure.InvalidCredentials)
    RemoteAuthFailure.RateLimited -> LoginResult.Rejected(AuthFailure.RateLimited)
    RemoteAuthFailure.Offline -> LoginResult.Unavailable(ServiceFailure.Offline)
    RemoteAuthFailure.Timeout -> LoginResult.Unavailable(ServiceFailure.Timeout)
    RemoteAuthFailure.ServiceUnavailable -> LoginResult.Unavailable(ServiceFailure.ServerError)
    RemoteAuthFailure.Unknown -> LoginResult.Unavailable(ServiceFailure.Unknown)
}

private fun RemoteRegistrationFailure.toRegistrationStartResult(): RegistrationStartResult = when (this) {
    RemoteRegistrationFailure.DuplicateUsername -> RegistrationStartResult.Rejected(AuthFailure.DuplicateUsername)
    RemoteRegistrationFailure.InvalidUsername -> RegistrationStartResult.Rejected(AuthFailure.InvalidInput("username", "invalid"))
    RemoteRegistrationFailure.InvalidEmail -> RegistrationStartResult.Rejected(AuthFailure.InvalidInput("email", "invalid"))
    RemoteRegistrationFailure.InvalidExperienceMode -> RegistrationStartResult.Rejected(AuthFailure.InvalidInput("experience", "invalid"))
    RemoteRegistrationFailure.RateLimited -> RegistrationStartResult.Rejected(AuthFailure.RateLimited)
    RemoteRegistrationFailure.Offline -> RegistrationStartResult.Unavailable(ServiceFailure.Offline)
    RemoteRegistrationFailure.Timeout -> RegistrationStartResult.Unavailable(ServiceFailure.Timeout)
    RemoteRegistrationFailure.ServiceUnavailable -> RegistrationStartResult.Unavailable(ServiceFailure.ServerError)
    RemoteRegistrationFailure.DuplicateEmail,
    RemoteRegistrationFailure.InvalidAttempt,
    RemoteRegistrationFailure.InvalidPassword,
    RemoteRegistrationFailure.Unknown -> RegistrationStartResult.Unavailable(ServiceFailure.Unknown)
}

private fun RemoteRegistrationFailure.toRegistrationResult(): RegistrationResult = when (this) {
    RemoteRegistrationFailure.DuplicateUsername -> RegistrationResult.Rejected(AuthFailure.DuplicateUsername)
    RemoteRegistrationFailure.DuplicateEmail -> RegistrationResult.Rejected(AuthFailure.DuplicateEmail)
    RemoteRegistrationFailure.InvalidAttempt -> RegistrationResult.Rejected(AuthFailure.InvalidOrExpiredToken)
    RemoteRegistrationFailure.InvalidPassword -> RegistrationResult.Rejected(AuthFailure.InvalidInput("password", "invalid"))
    RemoteRegistrationFailure.InvalidUsername -> RegistrationResult.Rejected(AuthFailure.InvalidInput("username", "invalid"))
    RemoteRegistrationFailure.InvalidEmail -> RegistrationResult.Rejected(AuthFailure.InvalidInput("email", "invalid"))
    RemoteRegistrationFailure.InvalidExperienceMode -> RegistrationResult.Rejected(AuthFailure.InvalidInput("experience", "invalid"))
    RemoteRegistrationFailure.RateLimited -> RegistrationResult.Rejected(AuthFailure.RateLimited)
    RemoteRegistrationFailure.Offline -> RegistrationResult.Unavailable(ServiceFailure.Offline)
    RemoteRegistrationFailure.Timeout -> RegistrationResult.Unavailable(ServiceFailure.Timeout)
    RemoteRegistrationFailure.ServiceUnavailable -> RegistrationResult.Unavailable(ServiceFailure.ServerError)
    RemoteRegistrationFailure.Unknown -> RegistrationResult.Unavailable(ServiceFailure.Unknown)
}

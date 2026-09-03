package com.patsy.app.security

/**
 * Role model for Patsy accounts.
 *
 * IMPORTANT: OWNER must only be returned by a trusted authentication/authorisation
 * service in production. A local boolean, hidden route, username or profile switch
 * must never grant OWNER privileges.
 */
enum class PatsyRole { USER, OWNER }

data class AuthorisedSession(
    val userId: String,
    val role: PatsyRole,
    val experienceMode: String,
    val token: String? = null
)

interface AuthorisationService {
    suspend fun currentSession(): AuthorisedSession?
    suspend fun canAccessOwnerTools(session: AuthorisedSession): Boolean =
        session.role == PatsyRole.OWNER
}

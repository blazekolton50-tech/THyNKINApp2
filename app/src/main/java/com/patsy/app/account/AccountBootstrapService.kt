package com.patsy.app.account

import com.patsy.app.auth.AuthSessionStore
import com.patsy.app.auth.PublicSession
import java.util.concurrent.CancellationException

sealed interface RemoteAccountBootstrapResult {
    data class Available(val account: AccountBootstrap) : RemoteAccountBootstrapResult
    data object Unauthorized : RemoteAccountBootstrapResult
    data object Forbidden : RemoteAccountBootstrapResult
    data object Malformed : RemoteAccountBootstrapResult
    data object Unavailable : RemoteAccountBootstrapResult
}

interface AccountBootstrapTransport {
    suspend fun fetch(accessToken: String): RemoteAccountBootstrapResult
}

interface AccountBootstrapService {
    suspend fun fetch(session: PublicSession): AccountBootstrapResult
}

class ServerAccountBootstrapService(
    private val sessionStore: AuthSessionStore,
    private val transport: AccountBootstrapTransport,
    private val nowEpochMillis: () -> Long = System::currentTimeMillis,
) : AccountBootstrapService {
    override suspend fun fetch(session: PublicSession): AccountBootstrapResult {
        fun failed(reason: BootstrapFailure) =
            AccountBootstrapResult.FailedClosed(reason, protectedBootstrap(session.userId))

        val now = nowEpochMillis()
        if (session.expiresAtEpochMillis <= now) return failed(BootstrapFailure.SESSION_EXPIRED)

        val stored = sessionStore.read() ?: return failed(BootstrapFailure.NOT_AUTHENTICATED)
        if (stored.publicSession.userId != session.userId || stored.publicSession.sessionId != session.sessionId) {
            return failed(BootstrapFailure.SESSION_MISMATCH)
        }

        return when (val remote = try {
            transport.fetch(stored.accessToken)
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (_: Exception) {
            return failed(BootstrapFailure.BACKEND_UNAVAILABLE)
        }) {
            RemoteAccountBootstrapResult.Unavailable -> failed(BootstrapFailure.BACKEND_UNAVAILABLE)
            RemoteAccountBootstrapResult.Unauthorized -> failed(BootstrapFailure.UNAUTHORIZED)
            RemoteAccountBootstrapResult.Forbidden -> failed(BootstrapFailure.FORBIDDEN)
            RemoteAccountBootstrapResult.Malformed -> failed(BootstrapFailure.MALFORMED)
            is RemoteAccountBootstrapResult.Available -> {
                val account = remote.account
                val validationFailure = account.validationFailure(now)
                when {
                    account.canonicalUserId != session.userId -> failed(BootstrapFailure.MALFORMED)
                    validationFailure != null -> failed(validationFailure)
                    else -> AccountBootstrapResult.Available(account)
                }
            }
        }
    }
}

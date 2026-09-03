package com.patsy.app.account

import com.patsy.app.auth.AuthSessionStore
import com.patsy.app.auth.PublicSession
import com.patsy.app.auth.StoredAuthSession
import com.patsy.app.security.OwnerCapability
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class AccountBootstrapServiceTest {
    private val session = PublicSession("session-1", "uuid-1", "User", "hidden", true, 2_000L)
    private class Store(var value: StoredAuthSession?) : AuthSessionStore {
        override fun read() = value
        override fun write(session: StoredAuthSession) { value = session }
        override fun clear() { value = null }
    }
    private class Transport(var result: RemoteAccountBootstrapResult) : AccountBootstrapTransport {
        override suspend fun fetch(accessToken: String) = result
    }
    private fun stored() = StoredAuthSession("access", "refresh", session)

    @Test fun validBootstrapUsesCanonicalServerIdentityAndSpecificCapabilities(): Unit = runBlocking {
        val account = AccountBootstrap(
            canonicalUserId = "uuid-1", profileReadiness = ProfileReadiness.READY,
            onboardingState = OnboardingState.READY, ageState = TrustedAgeState.STANDARD_16_PLUS,
            capabilities = setOf(OwnerCapability.VIEW_ANALYTICS), accountStatus = AccountStatus.ACTIVE,
            settingsAvailability = SettingsAvailability.AVAILABLE, restrictions = emptySet(),
            validUntilEpochMillis = 1_900L,
        )
        val service = ServerAccountBootstrapService(Store(stored()), Transport(RemoteAccountBootstrapResult.Available(account)), { 1_000L })
        val available = assertIs<AccountBootstrapResult.Available>(service.fetch(session))
        assertEquals("uuid-1", available.account.canonicalUserId)
        assertEquals(setOf(OwnerCapability.VIEW_ANALYTICS), available.account.capabilities)
    }

    @Test fun malformedIdentityOrUnavailableBackendFailsClosedProtected(): Unit = runBlocking {
        val malformed = AccountBootstrap(
            "different-user", ProfileReadiness.READY, OnboardingState.READY,
            TrustedAgeState.STANDARD_16_PLUS, setOf(OwnerCapability.VIEW_OWNER_TOOLS),
            AccountStatus.ACTIVE, SettingsAvailability.AVAILABLE, emptySet(), 1_900L,
        )
        val mismatch = ServerAccountBootstrapService(Store(stored()), Transport(RemoteAccountBootstrapResult.Available(malformed)), { 1_000L })
        val mismatchResult = assertIs<AccountBootstrapResult.FailedClosed>(mismatch.fetch(session))
        assertEquals(TrustedAgeState.UNKNOWN_UNVERIFIED, mismatchResult.protectedState.ageState)
        assertTrue(mismatchResult.protectedState.capabilities.isEmpty())

        val unavailable = ServerAccountBootstrapService(Store(stored()), Transport(RemoteAccountBootstrapResult.Unavailable), { 1_000L })
        assertIs<AccountBootstrapResult.FailedClosed>(unavailable.fetch(session))
    }

    @Test fun expiredSessionOrLogoutFailsClosed(): Unit = runBlocking {
        val service = ServerAccountBootstrapService(Store(null), Transport(RemoteAccountBootstrapResult.Unavailable), { 2_001L })
        assertIs<AccountBootstrapResult.FailedClosed>(service.fetch(session))
    }

    @Test fun inconsistentReadyBootstrapFailsClosed(): Unit = runBlocking {
        val inconsistent = AccountBootstrap(
            "uuid-1", ProfileReadiness.MISSING, OnboardingState.READY,
            TrustedAgeState.STANDARD_16_PLUS, emptySet(), AccountStatus.ACTIVE,
            SettingsAvailability.AVAILABLE, emptySet(), 1_900L,
        )
        val service = ServerAccountBootstrapService(Store(stored()), Transport(RemoteAccountBootstrapResult.Available(inconsistent)), { 1_000L })
        val result = assertIs<AccountBootstrapResult.FailedClosed>(service.fetch(session))
        assertEquals(BootstrapFailure.INCONSISTENT, result.reason)
        assertEquals(TrustedAgeState.UNKNOWN_UNVERIFIED, result.protectedState.ageState)
    }

    @Test fun unknownAgeCannotCarryPrivilegedOrAdultAuthority(): Unit = runBlocking {
        val malformed = AccountBootstrap(
            "uuid-1", ProfileReadiness.READY, OnboardingState.READY,
            TrustedAgeState.UNKNOWN_UNVERIFIED, setOf(OwnerCapability.VIEW_OWNER_TOOLS),
            AccountStatus.ACTIVE, SettingsAvailability.AVAILABLE, emptySet(), 1_900L,
        )
        val service = ServerAccountBootstrapService(Store(stored()), Transport(RemoteAccountBootstrapResult.Available(malformed)), { 1_000L })
        val result = assertIs<AccountBootstrapResult.FailedClosed>(service.fetch(session))
        assertTrue(result.protectedState.capabilities.isEmpty())
        assertTrue(result.protectedState.restrictions.contains(ShellRestriction.PROTECTED_CONTENT_ONLY))
    }
}

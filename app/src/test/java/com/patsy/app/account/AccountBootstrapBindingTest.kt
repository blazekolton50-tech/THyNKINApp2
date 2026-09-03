package com.patsy.app.account

import com.patsy.app.auth.PatsyServiceBindings
import com.patsy.app.auth.PublicSession
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class AccountBootstrapBindingTest {
    @Test fun unconfiguredBootstrapBindingFailsClosedWithoutCapabilities(): Unit = runBlocking {
        val session = PublicSession(
            sessionId = "session-unconfigured",
            userId = "user-unconfigured",
            username = "User",
            maskedEmail = "hidden",
            emailVerified = true,
            expiresAtEpochMillis = Long.MAX_VALUE,
        )

        val result = assertIs<AccountBootstrapResult.FailedClosed>(
            PatsyServiceBindings.accountBootstrapService.fetch(session),
        )

        assertEquals(BootstrapFailure.BACKEND_UNAVAILABLE, result.reason)
        assertEquals(TrustedAgeState.UNKNOWN_UNVERIFIED, result.protectedState.ageState)
        assertTrue(result.protectedState.capabilities.isEmpty())
    }
}

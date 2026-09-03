package com.patsy.app.account

import com.patsy.app.auth.PublicSession
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AuthenticatedShellStateTest {
    private val sessionA = PublicSession("a", "user-a", "A", "a***", true, 5_000L)
    private val sessionB = PublicSession("b", "user-b", "B", "b***", true, 5_000L)

    @Test fun logoutClearsBootstrapCapabilitiesAndNavigationState() {
        val state = AuthenticatedShellState()
        state.begin(sessionA)
        state.accept(protectedBootstrap("user-a").copy(validUntilEpochMillis = 4_000L))
        state.selectRoute("profile")
        state.clear()
        assertNull(state.session)
        assertNull(state.bootstrap)
        assertNull(state.selectedRouteId)
    }

    @Test fun accountSwitchInvalidatesPriorBootstrap() {
        val state = AuthenticatedShellState()
        state.begin(sessionA)
        state.accept(protectedBootstrap("user-a").copy(validUntilEpochMillis = 4_000L))
        state.begin(sessionB)
        assertNull(state.bootstrap)
        assertEquals("user-b", state.session?.userId)
        assertTrue(state.isLoadingBootstrap)
    }
}

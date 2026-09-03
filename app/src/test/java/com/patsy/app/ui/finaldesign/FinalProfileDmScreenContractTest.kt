package com.patsy.app.ui.finaldesign

import com.patsy.app.dms.DmProviderCapability
import com.patsy.app.profile.ProfileMenuDestination
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FinalProfileDmScreenContractTest {
    @Test
    fun profileScreenNeverInventsUnavailableMetricsOrProjects() {
        val state = finalProfileScreenState(
            displayName = "Patsy User",
            username = "patsyuser",
        )

        assertTrue(state.presentation.metrics.all { it.count == null })
        assertTrue(state.presentation.recentProjects.isEmpty())
        assertTrue(state.presentation.savedProjects.isEmpty())
    }

    @Test
    fun expandedProfileMenuKeepsLogoutAndOwnerEntrySeparate() {
        val contract = finalProfileScreenState("Patsy User", "patsyuser")

        assertTrue(
            contract.menu.flatMap { it.items }
                .any { it.destination == ProfileMenuDestination.LOG_OUT && it.destructive },
        )
        assertFalse(contract.menuGrantsOwnerAuthority)
    }

    @Test
    fun dmScreenStartsTruthfullyEmptyAndCallingUnavailable() {
        val state = finalDmScreenState()

        assertTrue(state.threads.isEmpty())
        assertTrue(state.messages.isEmpty())
        assertEquals(DmProviderCapability.NOT_CONFIGURED, state.capabilities.audioCall)
        assertEquals(DmProviderCapability.NOT_CONFIGURED, state.capabilities.videoCall)
        assertFalse(state.fabricatesDeliveryState)
    }
}

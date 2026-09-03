package com.patsy.app.profile

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ProfilePresentationStateTest {
    @Test
    fun missingMetricsStayUnknownInsteadOfInventingCounts() {
        val state = ProfilePresentationState(
            displayName = "Patsy User",
            username = "@patsyuser",
            bio = null,
            avatarUri = null,
            metrics = listOf(
                ProfileMetric("Projects", null),
                ProfileMetric("Followers", null),
                ProfileMetric("Following", null),
                ProfileMetric("Likes", null),
            ),
        )

        assertTrue(state.metrics.all { it.count == null })
        assertTrue(state.metrics.all { it.formattedValue == null })
    }

    @Test
    fun knownMetricFormatsWithoutChangingStoredTruth() {
        val metric = ProfileMetric("Projects", 128L)

        assertEquals(128L, metric.count)
        assertEquals("128", metric.formattedValue)
    }

    @Test
    fun quickPostActionsMapToSharedCreatorDestinations() {
        assertEquals(
            listOf(
                ProfileQuickAction.IMAGE,
                ProfileQuickAction.VIDEO,
                ProfileQuickAction.DOCUMENT,
                ProfileQuickAction.TEMPLATE,
                ProfileQuickAction.MORE,
            ),
            defaultProfileQuickActions(),
        )
    }

    @Test
    fun expandedMenuKeepsLockedGroupingAndDestructiveLogout() {
        val menu = expandedProfileMenu()

        assertEquals(
            listOf(
                ProfileMenuSection.ACCOUNT,
                ProfileMenuSection.PREFERENCES,
                ProfileMenuSection.SUPPORT,
            ),
            menu.map { it.section },
        )
        val logout = menu.flatMap { it.items }.single { it.destination == ProfileMenuDestination.LOG_OUT }
        assertTrue(logout.destructive)
        assertEquals("Log Out", logout.label)
    }

    @Test
    fun ownerCapabilityIsNotGrantedByRenderingProfileMenu() {
        val menu = expandedProfileMenu()

        assertFalse(menu.flatMap { it.items }.any { it.grantsOwnerAuthority })
    }

    @Test
    fun emptyRecentAndSavedProjectsRemainTruthfulEmptyStates() {
        val state = ProfilePresentationState(
            displayName = "Patsy User",
            username = "@patsyuser",
        )

        assertTrue(state.recentProjects.isEmpty())
        assertTrue(state.savedProjects.isEmpty())
        assertNull(state.bio)
    }
}

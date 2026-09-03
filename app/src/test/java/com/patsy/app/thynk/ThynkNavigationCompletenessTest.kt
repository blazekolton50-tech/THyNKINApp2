package com.patsy.app.thynk

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ThynkNavigationCompletenessTest {
    @Test
    fun `every THyNK IT tool card resolves to a real route`() {
        ThynkStudioCatalog.categories.forEach { category ->
            category.items.forEach { item ->
                val route = ThynkWorkspaceNavigation.openItItem(category.id, item)
                assertNotNull(route, "${category.label} / $item must resolve")
                when (route) {
                    is ThynkWorkspaceRoute.Editor -> assertEquals(category.id, route.categoryId)
                    is ThynkWorkspaceRoute.Tool -> {
                        assertEquals(category.id, route.categoryId)
                        assertEquals(item, route.item)
                    }
                    else -> error("IT tool $item resolved outside its product")
                }
            }
        }
    }

    @Test
    fun `every THyNK Music catalog page has a render kind`() {
        ThynkMusicCatalog.pages.forEach { page ->
            assertNotNull(
                ThynkMusicPageRenderer.kind(page.id),
                "${page.id} must have visible page content",
            )
        }
    }

    @Test
    fun `media editor and audio boards retain THyNK Music logo ownership`() {
        listOf("recording", "mixer", "dj-studio", "auto-tuner", "video-editor").forEach { pageId ->
            assertEquals(
                com.patsy.app.ui.finaldesign.ThynkPanelDestination.MUSIC,
                ThynkWorkspaceNavigation.topLogoDestination(ThynkWorkspaceRoute.Music(pageId)),
            )
            assertTrue(ThynkWorkspaceNavigation.isEditingBoard(ThynkWorkspaceRoute.Music(pageId)))
        }
    }
}

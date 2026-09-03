package com.patsy.app.thynk

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ThynkWorkspaceNavigationTest {
    @Test
    fun `THyNK IT and Music panel entries open distinct roots`() {
        assertEquals(ThynkWorkspaceRoute.Hub, ThynkWorkspaceNavigation.initialRoute(ThynkStudioEntry.IT))
        assertEquals(
            ThynkWorkspaceRoute.Music("music-home"),
            ThynkWorkspaceNavigation.initialRoute(ThynkStudioEntry.MUSIC),
        )
    }

    @Test
    fun `design and legacy video editor route objects are editing boards`() {
        assertTrue(
            ThynkWorkspaceNavigation.isEditingBoard(
                ThynkWorkspaceRoute.Editor(pageId = "design-editor", categoryId = "design"),
            ),
        )
        assertTrue(
            ThynkWorkspaceNavigation.isEditingBoard(
                ThynkWorkspaceRoute.Editor(pageId = "video-editor", categoryId = "video"),
            ),
        )
    }

    @Test
    fun `fashion specialist tools are editing boards and hide the THyNK Panel`() {
        FashionPipelineContract.stages.forEach { stage ->
            val route = ThynkWorkspaceRoute.Tool(
                categoryId = "fashion",
                item = stage.label.uppercase(),
            )
            assertTrue(ThynkWorkspaceNavigation.isEditingBoard(route))
            val chrome = ThynkWorkspaceNavigation.chrome(route)
            assertTrue(chrome.showBack)
            assertTrue(chrome.showOverflowHome)
            assertFalse(chrome.showGlobalHomebar)
        }
    }

    @Test
    fun `specialist music and media workspaces hide the global homebar`() {
        listOf(
            "recording",
            "track-editor",
            "mixer",
            "equalizer",
            "effects",
            "lyrics-vocals",
            "dj-studio",
            "auto-tuner",
            "mastering",
            "video-editor",
            "export",
        ).forEach { pageId ->
            assertTrue(
                ThynkWorkspaceNavigation.isEditingBoard(ThynkWorkspaceRoute.Music(pageId)),
                "$pageId must be treated as an editing board",
            )
        }
    }

    @Test
    fun `hubs category screens and ordinary setup tools retain the global homebar`() {
        assertFalse(ThynkWorkspaceNavigation.isEditingBoard(ThynkWorkspaceRoute.Hub))
        assertFalse(
            ThynkWorkspaceNavigation.isEditingBoard(
                ThynkWorkspaceRoute.Category("design"),
            ),
        )
        assertFalse(
            ThynkWorkspaceNavigation.isEditingBoard(
                ThynkWorkspaceRoute.Tool(categoryId = "study", item = "NOTES"),
            ),
        )
        listOf("music-home", "create-music", "ai-music-generator", "video-home", "video-player").forEach { pageId ->
            assertFalse(
                ThynkWorkspaceNavigation.isEditingBoard(ThynkWorkspaceRoute.Music(pageId)),
                "$pageId must keep the global homebar",
            )
        }
    }

    @Test
    fun `editing board chrome is back plus overflow home with global homebar hidden`() {
        val chrome = ThynkWorkspaceNavigation.chrome(
            ThynkWorkspaceRoute.Music("mixer"),
        )

        assertTrue(chrome.showBack)
        assertTrue(chrome.showOverflowHome)
        assertFalse(chrome.showGlobalHomebar)
    }

    @Test
    fun `category chrome keeps back and global homebar without board overflow home`() {
        val chrome = ThynkWorkspaceNavigation.chrome(
            ThynkWorkspaceRoute.Category("fashion"),
        )

        assertTrue(chrome.showBack)
        assertFalse(chrome.showOverflowHome)
        assertTrue(chrome.showGlobalHomebar)
    }

    @Test
    fun `THyNK IT and Music roots have no back and keep global homebar`() {
        listOf(
            ThynkWorkspaceRoute.Hub,
            ThynkWorkspaceRoute.Music("music-home"),
        ).forEach { route ->
            val chrome = ThynkWorkspaceNavigation.chrome(route)
            assertFalse(chrome.showBack)
            assertFalse(chrome.showOverflowHome)
            assertTrue(chrome.showGlobalHomebar)
        }
    }

    @Test
    fun `back from non music editor returns to exact originating category`() {
        assertEquals(
            ThynkWorkspaceRoute.Category("fashion"),
            ThynkWorkspaceNavigation.back(
                ThynkWorkspaceRoute.Editor(pageId = "design-editor", categoryId = "fashion"),
            ),
        )
    }

    @Test
    fun `back from specialist audio board returns to music home`() {
        assertEquals(
            ThynkWorkspaceRoute.Music("music-home"),
            ThynkWorkspaceNavigation.back(ThynkWorkspaceRoute.Music("mixer")),
        )
        assertEquals(
            ThynkWorkspaceRoute.Music("music-home"),
            ThynkWorkspaceNavigation.back(ThynkWorkspaceRoute.Music("mastering")),
        )
    }

    @Test
    fun `back from video player or editor returns to video home`() {
        assertEquals(
            ThynkWorkspaceRoute.Music("video-home"),
            ThynkWorkspaceNavigation.back(ThynkWorkspaceRoute.Music("video-player")),
        )
        assertEquals(
            ThynkWorkspaceRoute.Music("video-home"),
            ThynkWorkspaceNavigation.back(ThynkWorkspaceRoute.Music("video-editor")),
        )
    }

    @Test
    fun `back from music or video setup returns one level toward product root`() {
        assertEquals(
            ThynkWorkspaceRoute.Music("music-home"),
            ThynkWorkspaceNavigation.back(ThynkWorkspaceRoute.Music("create-music")),
        )
        assertEquals(
            ThynkWorkspaceRoute.Music("music-home"),
            ThynkWorkspaceNavigation.back(ThynkWorkspaceRoute.Music("ai-music-generator")),
        )
        assertEquals(
            ThynkWorkspaceRoute.Music("music-home"),
            ThynkWorkspaceNavigation.back(ThynkWorkspaceRoute.Music("video-home")),
        )
        assertEquals(
            ThynkWorkspaceRoute.Music("music-home"),
            ThynkWorkspaceNavigation.back(ThynkWorkspaceRoute.Music("music-home")),
        )
    }
}

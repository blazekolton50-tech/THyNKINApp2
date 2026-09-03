package com.patsy.app.thynk

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ThynkStudioContractTest {
    @Test
    fun `THyNK IT excludes music and video specialist roots`() {
        assertEquals(
            listOf(
                "DESIGN & TEMPLATES",
                "SOCIAL & CONTENT",
                "PHOTO & IMAGE",
                "DOCUMENTS & BUSINESS",
                "HOMEWORK & STUDY",
                "PRESENTATIONS & PLANNING",
                "FASHION & TEXTILES",
                "COLLAGE & CREATIVE",
                "AI & MY STUDIO",
            ),
            ThynkStudioCatalog.categories.map { it.label },
        )
        assertFalse(ThynkStudioCatalog.categories.any { it.id == "music" || it.id == "video" })
    }

    @Test
    fun `THyNK Music owns the complete music and video media roots`() {
        val pages = ThynkMusicCatalog.pages.map { it.id }
        assertEquals(
            listOf(
                "music-home",
                "create-music",
                "ai-music-generator",
                "recording",
                "track-editor",
                "mixer",
                "effects",
                "equalizer",
                "lyrics-vocals",
                "dj-studio",
                "beats-sampler",
                "piano-roll",
                "ai-tools",
                "mastering",
                "auto-tuner",
                "video-home",
                "video-player",
                "video-editor",
                "export",
            ),
            pages,
        )
        assertTrue(ThynkMusicCatalog.pages.all { it.providerState != "FAKE_COMPLETE" })
        assertEquals(9, ThynkMusicLockedWorkspaces.ids.size)
        assertTrue(ThynkMusicLockedWorkspaces.ids.all(pages::contains))
    }

    @Test
    fun `video editor is owned by THyNK Music`() {
        assertEquals("video-editor", editorPageForThynkItem("VIDEO EDITOR"))
        assertEquals("video-editor", editorPageForThynkItem("TRIM & CUT"))
        assertEquals(ThynkStudioEntry.MUSIC, studioEntryForEditorPage("video-editor"))
    }

    @Test
    fun `design items remain owned by THyNK IT`() {
        assertEquals("design-editor", editorPageForThynkItem("POSTERS"))
        assertEquals("design-editor", editorPageForThynkItem("CUSTOM SIZE"))
        assertEquals("design-editor", editorPageForThynkItem("TEMPLATES"))
        assertEquals(ThynkStudioEntry.IT, studioEntryForEditorPage("design-editor"))
    }

    @Test
    fun `editor pages retain their editor kind`() {
        assertEquals(
            ThynkEditorDestination("design", ThynkEditorKind.DESIGN),
            editorDestinationForPage("design-editor"),
        )
        assertEquals(
            ThynkEditorDestination("video", ThynkEditorKind.VIDEO),
            editorDestinationForPage("video-editor"),
        )
        assertEquals(null, editorDestinationForPage("unknown-editor"))
    }
}

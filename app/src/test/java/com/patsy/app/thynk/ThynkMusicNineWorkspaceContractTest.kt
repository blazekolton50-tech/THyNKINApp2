package com.patsy.app.thynk

import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ThynkMusicNineWorkspaceContractTest {
    @Test
    fun lockedMusicWorkspaceSetIsExactlyTheNineOwnerApprovedStudios() {
        assertEquals(
            listOf(
                "mixer",
                "effects",
                "equalizer",
                "lyrics-vocals",
                "dj-studio",
                "beats-sampler",
                "piano-roll",
                "ai-tools",
                "mastering",
            ),
            ThynkMusicLockedWorkspaces.ids,
        )
        assertFalse("arrangement" in ThynkMusicLockedWorkspaces.ids)
        assertEquals(9, ThynkMusicLockedWorkspaces.ids.size)
    }

    @Test
    fun everyLockedWorkspaceHasANativeRendererAndVisibleBody() {
        ThynkMusicLockedWorkspaces.ids.forEach { id ->
            assertTrue(ThynkMusicPageRenderer.kind(id) != null, "Missing renderer for $id")
            assertTrue(ThynkMusicCatalog.pages.any { it.id == id }, "Missing catalog page for $id")
        }

        val screen = source("app/src/main/java/com/patsy/app/thynk/ThynkStudioScreen.kt")
        assertTrue(screen.contains("beatsSamplerItems()"))
        assertTrue(screen.contains("pianoRollItems()"))
        assertTrue(screen.contains("aiToolsItems()"))
        assertTrue(screen.contains("MIXER / MASTER CONSOLE"))
        assertTrue(screen.contains("BEATS SAMPLER"))
        assertTrue(screen.contains("PIANO ROLL / MIDI"))
        assertTrue(screen.contains("AI TOOLS SUITE"))
        assertTrue(screen.contains("MASTERING & EXPORT"))
    }

    private fun source(path: String): String {
        val candidates = sequenceOf(File(path), File("../$path"))
        return candidates.firstOrNull(File::isFile)?.readText()
            ?: error("Could not locate $path")
    }
}

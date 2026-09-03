package com.patsy.app.studio

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class StudioVideoPlayerLogicTest {
    @Test
    fun `time formatting is stable and non-negative`() {
        assertEquals("0:00", formatStudioTime(-100))
        assertEquals("0:05", formatStudioTime(5_000))
        assertEquals("1:05", formatStudioTime(65_000))
    }

    @Test
    fun `player only seeks when playable and meaningfully out of sync`() {
        assertFalse(shouldSeekPlayer(2_000, 2_200, canPlay = true))
        assertTrue(shouldSeekPlayer(2_000, 2_500, canPlay = true))
        assertFalse(shouldSeekPlayer(2_000, 2_500, canPlay = false))
    }
}

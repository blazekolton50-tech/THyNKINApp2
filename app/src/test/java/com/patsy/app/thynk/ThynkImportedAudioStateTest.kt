package com.patsy.app.thynk

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ThynkImportedAudioStateTest {
    @Test
    fun `selected real content uri becomes the current imported source`() {
        val state = reduceImportedAudioState(
            ThynkImportedAudioState.initial(),
            ThynkImportedAudioAction.Selected("content://media/audio/42"),
        )
        assertEquals(ThynkImportedAudioPhase.READY, state.phase)
        assertEquals("content://media/audio/42", state.uri)
    }

    @Test
    fun `blank selection fails closed instead of inventing media`() {
        val state = reduceImportedAudioState(
            ThynkImportedAudioState.initial(),
            ThynkImportedAudioAction.Selected("   "),
        )
        assertEquals(ThynkImportedAudioPhase.EMPTY, state.phase)
        assertNull(state.uri)
    }

    @Test
    fun `player callbacks drive playing stopped and error states`() {
        var state = reduceImportedAudioState(
            ThynkImportedAudioState.initial(),
            ThynkImportedAudioAction.Selected("content://media/audio/9"),
        )
        state = reduceImportedAudioState(state, ThynkImportedAudioAction.Playing)
        assertEquals(ThynkImportedAudioPhase.PLAYING, state.phase)

        state = reduceImportedAudioState(state, ThynkImportedAudioAction.Stopped)
        assertEquals(ThynkImportedAudioPhase.READY, state.phase)

        state = reduceImportedAudioState(state, ThynkImportedAudioAction.Failed)
        assertEquals(ThynkImportedAudioPhase.ERROR, state.phase)
        assertEquals("content://media/audio/9", state.uri)
    }
}

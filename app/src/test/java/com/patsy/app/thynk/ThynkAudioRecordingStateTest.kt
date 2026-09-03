package com.patsy.app.thynk

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ThynkAudioRecordingStateTest {
    @Test
    fun `recording state never invents an output before a real stop result`() {
        var state = ThynkAudioRecordingState.initial()
        assertEquals(ThynkAudioRecordingPhase.IDLE, state.phase)
        assertNull(state.outputPath)

        state = reduceThynkAudioRecordingState(
            state,
            ThynkAudioRecordingAction.Started("/cache/take.m4a"),
        )
        assertEquals(ThynkAudioRecordingPhase.RECORDING, state.phase)
        assertNull(state.outputPath)

        state = reduceThynkAudioRecordingState(
            state,
            ThynkAudioRecordingAction.Stopped("/cache/take.m4a"),
        )
        assertEquals(ThynkAudioRecordingPhase.RECORDED, state.phase)
        assertEquals("/cache/take.m4a", state.outputPath)
    }

    @Test
    fun `permission denial and recorder failures are explicit`() {
        var state = reduceThynkAudioRecordingState(
            ThynkAudioRecordingState.initial(),
            ThynkAudioRecordingAction.PermissionDenied,
        )
        assertEquals(ThynkAudioRecordingPhase.PERMISSION_REQUIRED, state.phase)

        state = reduceThynkAudioRecordingState(
            state,
            ThynkAudioRecordingAction.Failed("Recorder failed"),
        )
        assertEquals(ThynkAudioRecordingPhase.ERROR, state.phase)
        assertEquals("Recorder failed", state.message)
        assertNull(state.outputPath)
    }

    @Test
    fun `reset clears prior output and errors`() {
        val recorded = ThynkAudioRecordingState(
            phase = ThynkAudioRecordingPhase.RECORDED,
            outputPath = "/cache/voice.m4a",
            message = "Saved",
        )

        assertEquals(
            ThynkAudioRecordingState.initial(),
            reduceThynkAudioRecordingState(recorded, ThynkAudioRecordingAction.Reset),
        )
    }
}

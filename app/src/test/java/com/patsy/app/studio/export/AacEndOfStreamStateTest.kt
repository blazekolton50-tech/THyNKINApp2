package com.patsy.app.studio.export

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AacEndOfStreamStateTest {
    @Test
    fun `final audio pts is derived from final sample cursor`() {
        val state = AacEndOfStreamState.fromFinalCursor(
            finalSampleCursor = 96_000,
            sampleRate = 48_000,
        )

        assertEquals(96_000, state.finalSampleCursor)
        assertEquals(2_000_000, state.finalAudioPtsUs)
        assertFalse(state.inputEosQueued)
        assertFalse(state.outputEosSeen)
        assertFalse(state.complete)
    }

    @Test
    fun `queueing input eos does not mean encoder is complete`() {
        val state = AacEndOfStreamState.fromFinalCursor(48_000, 48_000)
            .queueInputEos()

        assertTrue(state.inputEosQueued)
        assertFalse(state.outputEosSeen)
        assertFalse(state.complete)
    }

    @Test
    fun `unrelated output flags do not mark encoder complete`() {
        val state = AacEndOfStreamState.fromFinalCursor(48_000, 48_000)
            .queueInputEos()
            .observeOutputFlags(flags = 2, endOfStreamFlag = 4)

        assertFalse(state.outputEosSeen)
        assertFalse(state.complete)
    }

    @Test
    fun `aac output eos after input eos marks encoding complete`() {
        val state = AacEndOfStreamState.fromFinalCursor(48_000, 48_000)
            .queueInputEos()
            .observeOutputFlags(flags = 4, endOfStreamFlag = 4)

        assertTrue(state.inputEosQueued)
        assertTrue(state.outputEosSeen)
        assertTrue(state.complete)
    }

    @Test
    fun `output eos observed before input eos is not complete`() {
        val state = AacEndOfStreamState.fromFinalCursor(48_000, 48_000)
            .observeOutputFlags(flags = 4, endOfStreamFlag = 4)

        assertFalse(state.inputEosQueued)
        assertTrue(state.outputEosSeen)
        assertFalse(state.complete)
    }

    @Test
    fun `input eos can only be queued once`() {
        val state = AacEndOfStreamState.fromFinalCursor(48_000, 48_000)
            .queueInputEos()

        assertFailsWith<IllegalStateException> {
            state.queueInputEos()
        }
    }

    @Test
    fun `invalid final cursor or sample rate fails fast`() {
        assertFailsWith<IllegalArgumentException> {
            AacEndOfStreamState.fromFinalCursor(-1, 48_000)
        }
        assertFailsWith<IllegalArgumentException> {
            AacEndOfStreamState.fromFinalCursor(0, 0)
        }
    }
}

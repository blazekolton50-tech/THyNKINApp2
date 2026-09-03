package com.patsy.app.studio.export

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class AacPcmInputPlannerTest {
    @Test
    fun `large input buffer is capped to 1024 pcm frames`() {
        val chunk = AacPcmInputPlanner.plan(
            sampleCursor = 4_800,
            remainingFrames = 10_000,
            inputBufferBytes = 64 * 1024,
            sampleRate = 48_000,
            channels = 2,
        )!!

        assertEquals(4_800, chunk.startSampleCursor)
        assertEquals(1024, chunk.frameCount)
        assertEquals(4096, chunk.byteCount)
        assertEquals(100_000, chunk.ptsUs)
        assertEquals(5_824, chunk.nextSampleCursor)
    }

    @Test
    fun `small codec buffer limits queued frames to complete pcm capacity`() {
        val chunk = AacPcmInputPlanner.plan(
            sampleCursor = 0,
            remainingFrames = 10_000,
            inputBufferBytes = 1000,
            sampleRate = 48_000,
            channels = 2,
        )!!

        assertEquals(250, chunk.frameCount)
        assertEquals(1000, chunk.byteCount)
        assertEquals(250, chunk.nextSampleCursor)
    }

    @Test
    fun `remaining frames are never over read`() {
        val chunk = AacPcmInputPlanner.plan(
            sampleCursor = 10_000,
            remainingFrames = 17,
            inputBufferBytes = 16_384,
            sampleRate = 48_000,
            channels = 2,
        )!!

        assertEquals(17, chunk.frameCount)
        assertEquals(68, chunk.byteCount)
        assertEquals(10_017, chunk.nextSampleCursor)
    }

    @Test
    fun `pts always comes from starting sample cursor`() {
        assertEquals(0, AacPcmInputPlanner.ptsUs(0, 48_000))
        assertEquals(1_000_000, AacPcmInputPlanner.ptsUs(48_000, 48_000))
        assertEquals(2_500_000, AacPcmInputPlanner.ptsUs(120_000, 48_000))
    }

    @Test
    fun `no remaining audio produces no input chunk`() {
        assertNull(
            AacPcmInputPlanner.plan(
                sampleCursor = 48_000,
                remainingFrames = 0,
                inputBufferBytes = 4096,
                sampleRate = 48_000,
                channels = 2,
            ),
        )
    }

    @Test
    fun `positive buffer too small for one complete pcm frame produces no chunk`() {
        assertNull(
            AacPcmInputPlanner.plan(
                sampleCursor = 0,
                remainingFrames = 1,
                inputBufferBytes = 3,
                sampleRate = 48_000,
                channels = 2,
            ),
        )
    }

    @Test
    fun `invalid audio configuration fails fast`() {
        assertFailsWith<IllegalArgumentException> {
            AacPcmInputPlanner.plan(0, 1, 4096, sampleRate = 0, channels = 2)
        }
        assertFailsWith<IllegalArgumentException> {
            AacPcmInputPlanner.plan(0, 1, 4096, sampleRate = 48_000, channels = 0)
        }
        assertFailsWith<IllegalArgumentException> {
            AacPcmInputPlanner.plan(0, 1, 0, sampleRate = 48_000, channels = 2)
        }
        assertFailsWith<IllegalArgumentException> {
            AacPcmInputPlanner.plan(0, 1, 4096, sampleRate = 48_000, channels = 2, bytesPerSample = 0)
        }
        assertFailsWith<IllegalArgumentException> {
            AacPcmInputPlanner.plan(-1, 1, 4096, sampleRate = 48_000, channels = 2)
        }
        assertFailsWith<IllegalArgumentException> {
            AacPcmInputPlanner.plan(0, -1, 4096, sampleRate = 48_000, channels = 2)
        }
    }
}

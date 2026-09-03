package com.patsy.app.studio.export

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MuxerCoordinatorCoreTest {
    @Test
    fun `audio plus video starts muxer exactly once after both formats register`() {
        val backend = FakeMuxerBackend(mapOf("video" to 4, "audio" to 9))
        val coordinator = MuxerCoordinatorCore(backend)

        coordinator.registerFormat(StudioEncoderKind.VIDEO, "video")
        assertFalse(coordinator.started)
        assertEquals(0, backend.startCalls)

        coordinator.registerFormat(StudioEncoderKind.AUDIO, "audio")
        assertTrue(coordinator.started)
        assertEquals(1, backend.startCalls)
        assertEquals(4, coordinator.trackIndex(StudioEncoderKind.VIDEO))
        assertEquals(9, coordinator.trackIndex(StudioEncoderKind.AUDIO))
    }

    @Test
    fun `duplicate video format is rejected`() {
        val coordinator = MuxerCoordinatorCore(FakeMuxerBackend())
        coordinator.registerFormat(StudioEncoderKind.VIDEO, "video-1")

        assertFailsWith<IllegalStateException> {
            coordinator.registerFormat(StudioEncoderKind.VIDEO, "video-2")
        }
    }

    @Test
    fun `duplicate audio format is rejected`() {
        val coordinator = MuxerCoordinatorCore(FakeMuxerBackend())
        coordinator.registerFormat(StudioEncoderKind.AUDIO, "audio-1")

        assertFailsWith<IllegalStateException> {
            coordinator.registerFormat(StudioEncoderKind.AUDIO, "audio-2")
        }
    }

    @Test
    fun `registration after muxer start is rejected`() {
        val coordinator = MuxerCoordinatorCore(FakeMuxerBackend())
        coordinator.registerFormat(StudioEncoderKind.VIDEO, "video")
        coordinator.registerFormat(StudioEncoderKind.AUDIO, "audio")

        assertFailsWith<IllegalStateException> {
            coordinator.registerFormat(StudioEncoderKind.VIDEO, "late")
        }
    }

    @Test
    fun `video only starts after video format without audio`() {
        val backend = FakeMuxerBackend(mapOf("video" to 7))
        val coordinator = MuxerCoordinatorCore(backend, videoOnly = true)

        coordinator.registerFormat(StudioEncoderKind.VIDEO, "video")

        assertTrue(coordinator.started)
        assertEquals(1, backend.startCalls)
        assertEquals(7, coordinator.trackIndex(StudioEncoderKind.VIDEO))
        assertFailsWith<IllegalStateException> {
            coordinator.trackIndex(StudioEncoderKind.AUDIO)
        }
    }

    private class FakeMuxerBackend(
        private val trackIndices: Map<String, Int> = emptyMap(),
    ) : MuxerBackend<String> {
        var startCalls = 0
            private set
        private var nextTrack = 0

        override fun addTrack(format: String): Int = trackIndices[format] ?: nextTrack++

        override fun start() {
            startCalls += 1
        }
    }
}

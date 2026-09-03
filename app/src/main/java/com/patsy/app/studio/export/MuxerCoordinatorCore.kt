package com.patsy.app.studio.export

enum class StudioEncoderKind {
    VIDEO,
    AUDIO,
}

interface MuxerBackend<F> {
    fun addTrack(format: F): Int
    fun start()
}

class MuxerCoordinatorCore<F>(
    private val backend: MuxerBackend<F>,
    private val videoOnly: Boolean = false,
) {
    private var videoTrackIndex = -1
    private var audioTrackIndex = -1

    var started: Boolean = false
        private set

    fun registerFormat(kind: StudioEncoderKind, format: F) {
        check(!started) { "Cannot register tracks after muxer start." }

        when (kind) {
            StudioEncoderKind.VIDEO -> {
                check(videoTrackIndex == -1) { "Video format changed more than once." }
                videoTrackIndex = backend.addTrack(format)
            }

            StudioEncoderKind.AUDIO -> {
                check(audioTrackIndex == -1) { "Audio format changed more than once." }
                audioTrackIndex = backend.addTrack(format)
            }
        }

        val ready = videoTrackIndex >= 0 && (videoOnly || audioTrackIndex >= 0)
        if (ready) {
            backend.start()
            started = true
        }
    }

    fun trackIndex(kind: StudioEncoderKind): Int = when (kind) {
        StudioEncoderKind.VIDEO -> videoTrackIndex.also {
            check(it >= 0) { "Video track has not been registered." }
        }

        StudioEncoderKind.AUDIO -> audioTrackIndex.also {
            check(it >= 0) { "Audio track has not been registered." }
        }
    }
}

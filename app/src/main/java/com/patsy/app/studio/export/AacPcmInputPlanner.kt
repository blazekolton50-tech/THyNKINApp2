package com.patsy.app.studio.export

data class AacInputChunk(
    val startSampleCursor: Long,
    val frameCount: Int,
    val byteCount: Int,
    val ptsUs: Long,
    val nextSampleCursor: Long,
)

object AacPcmInputPlanner {
    const val MAX_FRAMES_PER_INPUT = 1024

    fun plan(
        sampleCursor: Long,
        remainingFrames: Long,
        inputBufferBytes: Int,
        sampleRate: Int,
        channels: Int,
        bytesPerSample: Int = 2,
    ): AacInputChunk? {
        require(sampleCursor >= 0) { "sampleCursor must be >= 0" }
        require(remainingFrames >= 0) { "remainingFrames must be >= 0" }
        require(inputBufferBytes > 0) { "inputBufferBytes must be > 0" }
        require(sampleRate > 0) { "sampleRate must be > 0" }
        require(channels > 0) { "channels must be > 0" }
        require(bytesPerSample > 0) { "bytesPerSample must be > 0" }

        if (remainingFrames == 0L) return null

        val bytesPerFrame = channels.toLong() * bytesPerSample.toLong()
        val frameCapacity = inputBufferBytes.toLong() / bytesPerFrame
        if (frameCapacity <= 0L) return null

        val frameCount = minOf(
            MAX_FRAMES_PER_INPUT.toLong(),
            remainingFrames,
            frameCapacity,
        ).toInt()
        if (frameCount <= 0) return null

        val byteCount = (frameCount.toLong() * bytesPerFrame).toInt()
        return AacInputChunk(
            startSampleCursor = sampleCursor,
            frameCount = frameCount,
            byteCount = byteCount,
            ptsUs = ptsUs(sampleCursor, sampleRate),
            nextSampleCursor = sampleCursor + frameCount,
        )
    }

    fun ptsUs(sampleCursor: Long, sampleRate: Int): Long {
        require(sampleCursor >= 0) { "sampleCursor must be >= 0" }
        require(sampleRate > 0) { "sampleRate must be > 0" }
        return sampleCursor * 1_000_000L / sampleRate
    }
}

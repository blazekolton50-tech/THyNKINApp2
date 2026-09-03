package com.patsy.app.studio.export

data class AacEndOfStreamState(
    val finalSampleCursor: Long,
    val finalAudioPtsUs: Long,
    val inputEosQueued: Boolean = false,
    val outputEosSeen: Boolean = false,
) {
    val complete: Boolean
        get() = inputEosQueued && outputEosSeen

    fun queueInputEos(): AacEndOfStreamState {
        check(!inputEosQueued) { "AAC input EOS has already been queued." }
        return copy(inputEosQueued = true)
    }

    fun observeOutputFlags(flags: Int, endOfStreamFlag: Int): AacEndOfStreamState {
        require(endOfStreamFlag != 0) { "endOfStreamFlag must be non-zero" }
        return if (flags and endOfStreamFlag != 0) {
            copy(outputEosSeen = true)
        } else {
            this
        }
    }

    companion object {
        fun fromFinalCursor(
            finalSampleCursor: Long,
            sampleRate: Int,
        ): AacEndOfStreamState {
            require(finalSampleCursor >= 0) { "finalSampleCursor must be >= 0" }
            require(sampleRate > 0) { "sampleRate must be > 0" }
            return AacEndOfStreamState(
                finalSampleCursor = finalSampleCursor,
                finalAudioPtsUs = AacPcmInputPlanner.ptsUs(finalSampleCursor, sampleRate),
            )
        }
    }
}

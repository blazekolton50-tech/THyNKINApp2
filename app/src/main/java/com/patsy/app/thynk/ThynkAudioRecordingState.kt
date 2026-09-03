package com.patsy.app.thynk

enum class ThynkAudioRecordingPhase {
    IDLE,
    PERMISSION_REQUIRED,
    RECORDING,
    RECORDED,
    ERROR,
}

data class ThynkAudioRecordingState(
    val phase: ThynkAudioRecordingPhase,
    val outputPath: String?,
    val message: String?,
) {
    companion object {
        fun initial() = ThynkAudioRecordingState(
            phase = ThynkAudioRecordingPhase.IDLE,
            outputPath = null,
            message = null,
        )
    }
}

sealed interface ThynkAudioRecordingAction {
    data object PermissionDenied : ThynkAudioRecordingAction
    data class Started(val pendingPath: String) : ThynkAudioRecordingAction
    data class Stopped(val outputPath: String) : ThynkAudioRecordingAction
    data class Failed(val message: String) : ThynkAudioRecordingAction
    data object Reset : ThynkAudioRecordingAction
}

fun reduceThynkAudioRecordingState(
    state: ThynkAudioRecordingState,
    action: ThynkAudioRecordingAction,
): ThynkAudioRecordingState = when (action) {
    ThynkAudioRecordingAction.PermissionDenied -> state.copy(
        phase = ThynkAudioRecordingPhase.PERMISSION_REQUIRED,
        outputPath = null,
        message = "Microphone permission is required to record audio.",
    )

    is ThynkAudioRecordingAction.Started -> state.copy(
        phase = ThynkAudioRecordingPhase.RECORDING,
        outputPath = null,
        message = "Recording",
    )

    is ThynkAudioRecordingAction.Stopped -> state.copy(
        phase = ThynkAudioRecordingPhase.RECORDED,
        outputPath = action.outputPath,
        message = "Recording saved locally",
    )

    is ThynkAudioRecordingAction.Failed -> state.copy(
        phase = ThynkAudioRecordingPhase.ERROR,
        outputPath = null,
        message = action.message,
    )

    ThynkAudioRecordingAction.Reset -> ThynkAudioRecordingState.initial()
}

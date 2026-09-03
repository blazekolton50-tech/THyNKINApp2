package com.patsy.app.thynk

enum class ThynkImportedAudioPhase {
    EMPTY,
    READY,
    PLAYING,
    ERROR,
}

data class ThynkImportedAudioState(
    val phase: ThynkImportedAudioPhase,
    val uri: String?,
) {
    companion object {
        fun initial() = ThynkImportedAudioState(
            phase = ThynkImportedAudioPhase.EMPTY,
            uri = null,
        )
    }
}

sealed interface ThynkImportedAudioAction {
    data class Selected(val uri: String) : ThynkImportedAudioAction
    data object Playing : ThynkImportedAudioAction
    data object Stopped : ThynkImportedAudioAction
    data object Failed : ThynkImportedAudioAction
    data object Clear : ThynkImportedAudioAction
}

fun reduceImportedAudioState(
    state: ThynkImportedAudioState,
    action: ThynkImportedAudioAction,
): ThynkImportedAudioState = when (action) {
    is ThynkImportedAudioAction.Selected -> {
        val uri = action.uri.trim()
        if (uri.isBlank()) ThynkImportedAudioState.initial()
        else ThynkImportedAudioState(ThynkImportedAudioPhase.READY, uri)
    }
    ThynkImportedAudioAction.Playing -> {
        if (state.uri == null) state else state.copy(phase = ThynkImportedAudioPhase.PLAYING)
    }
    ThynkImportedAudioAction.Stopped -> {
        if (state.uri == null) ThynkImportedAudioState.initial()
        else state.copy(phase = ThynkImportedAudioPhase.READY)
    }
    ThynkImportedAudioAction.Failed -> state.copy(phase = ThynkImportedAudioPhase.ERROR)
    ThynkImportedAudioAction.Clear -> ThynkImportedAudioState.initial()
}

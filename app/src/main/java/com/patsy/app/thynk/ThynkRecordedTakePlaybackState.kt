package com.patsy.app.thynk

enum class ThynkRecordedTakePlaybackState {
    IDLE,
    PLAYING,
    ERROR,
}

sealed interface ThynkRecordedTakePlaybackAction {
    data object Playing : ThynkRecordedTakePlaybackAction
    data object Completed : ThynkRecordedTakePlaybackAction
    data object Stopped : ThynkRecordedTakePlaybackAction
    data object Failed : ThynkRecordedTakePlaybackAction
}

fun reduceRecordedTakePlaybackState(
    state: ThynkRecordedTakePlaybackState,
    action: ThynkRecordedTakePlaybackAction,
): ThynkRecordedTakePlaybackState = when (action) {
    ThynkRecordedTakePlaybackAction.Playing -> ThynkRecordedTakePlaybackState.PLAYING
    ThynkRecordedTakePlaybackAction.Completed,
    ThynkRecordedTakePlaybackAction.Stopped -> ThynkRecordedTakePlaybackState.IDLE
    ThynkRecordedTakePlaybackAction.Failed -> ThynkRecordedTakePlaybackState.ERROR
}

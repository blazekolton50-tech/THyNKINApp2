package com.patsy.app.thynk

import kotlin.test.Test
import kotlin.test.assertEquals

class ThynkRecordedTakePlaybackStateTest {
    @Test
    fun `playback state follows real player callbacks`() {
        var state = ThynkRecordedTakePlaybackState.IDLE
        state = reduceRecordedTakePlaybackState(state, ThynkRecordedTakePlaybackAction.Playing)
        assertEquals(ThynkRecordedTakePlaybackState.PLAYING, state)

        state = reduceRecordedTakePlaybackState(state, ThynkRecordedTakePlaybackAction.Completed)
        assertEquals(ThynkRecordedTakePlaybackState.IDLE, state)
    }

    @Test
    fun `playback failure is explicit and can reset`() {
        var state = reduceRecordedTakePlaybackState(
            ThynkRecordedTakePlaybackState.IDLE,
            ThynkRecordedTakePlaybackAction.Failed,
        )
        assertEquals(ThynkRecordedTakePlaybackState.ERROR, state)

        state = reduceRecordedTakePlaybackState(state, ThynkRecordedTakePlaybackAction.Stopped)
        assertEquals(ThynkRecordedTakePlaybackState.IDLE, state)
    }
}

package com.patsy.app.studio

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class StudioEditorStateTest {
    private fun readyVideo(
        durationMs: Int = 10_000,
        playheadMs: Int = 0,
        isPlaying: Boolean = false,
    ): StudioEditorState {
        var state = reduceStudioState(
            StudioEditorState.video(durationMs = 0),
            StudioAction.LoadMedia("content://patsy-test/clip"),
        )
        state = reduceStudioState(state, StudioAction.MediaReady(durationMs))
        state = reduceStudioState(state, StudioAction.SeekTo(playheadMs))
        if (isPlaying) {
            state = reduceStudioState(state, StudioAction.SetPlaying(true))
        }
        return state
    }

    @Test
    fun seekClampsInsideProjectDuration() {
        val state = StudioEditorState.video(durationMs = 10_000)

        assertEquals(0, reduceStudioState(state, StudioAction.SeekTo(-500)).playheadMs)
        assertEquals(10_000, reduceStudioState(state, StudioAction.SeekTo(15_000)).playheadMs)
        assertEquals(4_250, reduceStudioState(state, StudioAction.SeekTo(4_250)).playheadMs)
    }

    @Test
    fun frameStepNeverEscapesTimeline() {
        val state = StudioEditorState.video(durationMs = 10_000, playheadMs = 9_990)

        assertEquals(10_000, reduceStudioState(state, StudioAction.StepBy(500)).playheadMs)
        assertEquals(0, reduceStudioState(state.copy(playheadMs = 10), StudioAction.StepBy(-500)).playheadMs)
    }

    @Test
    fun reducingDurationClampsTheExistingPlayhead() {
        val state = StudioEditorState.video(durationMs = 10_000, playheadMs = 8_000)
        val reduced = reduceStudioState(state, StudioAction.SetDuration(3_000))

        assertEquals(3_000, reduced.durationMs)
        assertEquals(3_000, reduced.playheadMs)
    }

    @Test
    fun playPauseLoopAndMuteAreIndependentControls() {
        val state = readyVideo()
        val playing = reduceStudioState(state, StudioAction.TogglePlayPause)
        val looping = reduceStudioState(playing, StudioAction.ToggleLoop)
        val muted = reduceStudioState(looping, StudioAction.ToggleMute)

        assertTrue(playing.isPlaying)
        assertTrue(looping.isLooping)
        assertFalse(looping.isMuted)
        assertTrue(muted.isMuted)
    }

    @Test
    fun playerCallbacksSetPlaybackStateInsteadOfBlindlyTogglingIt() {
        val paused = readyVideo()
        val playing = reduceStudioState(paused, StudioAction.SetPlaying(true))
        val stillPlaying = reduceStudioState(playing, StudioAction.SetPlaying(true))
        val pausedAgain = reduceStudioState(stillPlaying, StudioAction.SetPlaying(false))

        assertTrue(playing.isPlaying)
        assertTrue(stillPlaying.isPlaying)
        assertFalse(pausedAgain.isPlaying)
        assertFalse(reduceStudioState(StudioEditorState.image(1080, 1350), StudioAction.SetPlaying(true)).isPlaying)
    }

    @Test
    fun playbackSpeedIsRestrictedToSupportedStudioSpeeds() {
        val state = StudioEditorState.video(durationMs = 10_000)

        assertEquals(1.5f, reduceStudioState(state, StudioAction.SetPlaybackSpeed(1.5f)).playbackSpeed)
        assertEquals(1f, reduceStudioState(state, StudioAction.SetPlaybackSpeed(1.3f)).playbackSpeed)
    }

    @Test
    fun selectingAToolDoesNotMutatePlayback() {
        val state = readyVideo(playheadMs = 2_000, isPlaying = true)
        val edited = reduceStudioState(state, StudioAction.SelectTool(StudioTool.EFFECTS))

        assertEquals(StudioTool.EFFECTS, edited.selectedTool)
        assertTrue(edited.isPlaying)
        assertEquals(2_000, edited.playheadMs)
    }

    @Test
    fun timelineFractionsRoundTripWithinTheProject() {
        assertEquals(0f, timelineFraction(playheadMs = -100, durationMs = 10_000))
        assertEquals(0.5f, timelineFraction(playheadMs = 5_000, durationMs = 10_000))
        assertEquals(1f, timelineFraction(playheadMs = 11_000, durationMs = 10_000))
        assertEquals(2_500, timeFromTimelineFraction(fraction = 0.25f, durationMs = 10_000))
        assertEquals(10_000, timeFromTimelineFraction(fraction = 2f, durationMs = 10_000))
    }

    @Test
    fun timelineLayerNormalizesInvalidTiming() {
        val layer = StudioTimelineLayer(
            id = "clip-1",
            type = StudioLayerType.VIDEO,
            startMs = 9_000,
            endMs = 2_000,
        ).normalized(projectDurationMs = 10_000)

        assertEquals(9_000, layer.startMs)
        assertEquals(9_000, layer.endMs)
    }

    @Test
    fun imageProjectsRemainStillByDefault() {
        val state = StudioEditorState.image(widthPx = 1080, heightPx = 1350)

        assertEquals(StudioMode.IMAGE, state.mode)
        assertEquals(0, state.durationMs)
        assertFalse(state.isPlaying)
    }
}

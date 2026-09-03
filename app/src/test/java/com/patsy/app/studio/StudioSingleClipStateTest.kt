package com.patsy.app.studio

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class StudioSingleClipStateTest {
    @Test
    fun loadingANewSourceClearsStalePlaybackMetadata() {
        val old = StudioEditorState.video(durationMs = 12_000, playheadMs = 6_000, isPlaying = true)

        val loading = reduceStudioState(old, StudioAction.LoadMedia("content://clip/new"))

        assertEquals(StudioMediaStatus.LOADING, loading.mediaStatus)
        assertEquals("content://clip/new", loading.sourceUri)
        assertEquals(0, loading.durationMs)
        assertEquals(0, loading.playheadMs)
        assertFalse(loading.isPlaying)
        assertNull(loading.clip)
    }

    @Test
    fun readyMediaCreatesOneClipUsingReportedDuration() {
        val loading = reduceStudioState(
            StudioEditorState.video(durationMs = 0),
            StudioAction.LoadMedia("content://clip/one"),
        )

        val ready = reduceStudioState(loading, StudioAction.MediaReady(9_000))

        assertEquals(StudioMediaStatus.READY, ready.mediaStatus)
        assertEquals(9_000, ready.durationMs)
        assertEquals(StudioClip("content://clip/one", 0, 9_000), ready.clip)
        assertTrue(ready.canPlay)
    }

    @Test
    fun clearingMediaReturnsToTruthfulEmptyState() {
        val ready = reduceStudioState(
            reduceStudioState(
                StudioEditorState.video(durationMs = 0),
                StudioAction.LoadMedia("content://clip/one"),
            ),
            StudioAction.MediaReady(9_000),
        )

        val empty = reduceStudioState(ready, StudioAction.ClearMedia)

        assertEquals(StudioMediaStatus.EMPTY, empty.mediaStatus)
        assertNull(empty.sourceUri)
        assertNull(empty.clip)
        assertEquals(0, empty.durationMs)
        assertFalse(empty.canPlay)
    }

    @Test
    fun failedMediaCannotPlayAndRetainsAnHonestError() {
        val loading = reduceStudioState(
            StudioEditorState.video(durationMs = 0),
            StudioAction.LoadMedia("content://clip/broken"),
        )

        val failed = reduceStudioState(loading, StudioAction.MediaFailed("Unsupported video"))

        assertEquals(StudioMediaStatus.FAILED, failed.mediaStatus)
        assertEquals("Unsupported video", failed.mediaError)
        assertFalse(failed.canPlay)
        assertFalse(failed.isPlaying)
    }

    @Test
    fun trimBoundsStayOrderedInsideTheSingleClip() {
        val ready = reduceStudioState(
            reduceStudioState(
                StudioEditorState.video(durationMs = 0),
                StudioAction.LoadMedia("content://clip/one"),
            ),
            StudioAction.MediaReady(10_000),
        )

        val startTrimmed = reduceStudioState(ready, StudioAction.SetTrimStart(2_500))
        val endTrimmed = reduceStudioState(startTrimmed, StudioAction.SetTrimEnd(7_500))
        val crossedStart = reduceStudioState(endTrimmed, StudioAction.SetTrimStart(9_000))
        val crossedEnd = reduceStudioState(crossedStart, StudioAction.SetTrimEnd(1_000))

        assertEquals(2_500, startTrimmed.clip?.trimStartMs)
        assertEquals(7_500, endTrimmed.clip?.trimEndMs)
        assertEquals(7_500, crossedStart.clip?.trimStartMs)
        assertEquals(7_500, crossedEnd.clip?.trimEndMs)
    }

    @Test
    fun fabricatedDurationWithoutReadyMediaDoesNotEnablePlayback() {
        val state = StudioEditorState.video(durationMs = 10_000)

        assertEquals(StudioMediaStatus.EMPTY, state.mediaStatus)
        assertFalse(state.canPlay)
        assertFalse(reduceStudioState(state, StudioAction.TogglePlayPause).isPlaying)
    }
}

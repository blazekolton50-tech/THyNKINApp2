package com.patsy.app.studio

import kotlin.math.roundToInt

enum class StudioMode {
    IMAGE,
    VIDEO,
    DOCUMENT,
    MEME,
    COLLAGE,
    CAMERA,
}

enum class StudioTool {
    SELECT,
    TEMPLATES,
    TEXT,
    MEDIA,
    ELEMENTS,
    AI,
    DRAW,
    CROP,
    CUTOUT,
    LAYERS,
    FRAMES,
    GUIDES,
    FILTERS,
    ADJUST,
    EFFECTS,
    ANIMATE,
    AUDIO,
    EXPORT,
}

enum class StudioLayerType {
    IMAGE,
    VIDEO,
    AUDIO,
    TEXT,
    SHAPE,
    STICKER,
    FRAME,
    GROUP,
    DRAWING,
    BACKGROUND,
}

enum class StudioMediaStatus {
    EMPTY,
    LOADING,
    READY,
    FAILED,
}

data class StudioClip(
    val sourceUri: String,
    val trimStartMs: Int,
    val trimEndMs: Int,
)

data class StudioEditorState(
    val mode: StudioMode,
    val projectName: String,
    val canvasWidthPx: Int,
    val canvasHeightPx: Int,
    val durationMs: Int,
    val playheadMs: Int = 0,
    val isPlaying: Boolean = false,
    val isLooping: Boolean = false,
    val isMuted: Boolean = false,
    val playbackSpeed: Float = 1f,
    val selectedTool: StudioTool = StudioTool.SELECT,
    val zoom: Float = 1f,
    val undoDepth: Int = 0,
    val redoDepth: Int = 0,
    val sourceUri: String? = null,
    val mediaStatus: StudioMediaStatus = StudioMediaStatus.EMPTY,
    val mediaError: String? = null,
    val clip: StudioClip? = null,
) {
    val canPlay: Boolean
        get() = (mode == StudioMode.VIDEO || mode == StudioMode.CAMERA) &&
            mediaStatus == StudioMediaStatus.READY &&
            clip != null &&
            durationMs > 0

    companion object {
        fun image(
            widthPx: Int,
            heightPx: Int,
            projectName: String = "Untitled image",
        ) = StudioEditorState(
            mode = StudioMode.IMAGE,
            projectName = projectName,
            canvasWidthPx = widthPx.coerceAtLeast(1),
            canvasHeightPx = heightPx.coerceAtLeast(1),
            durationMs = 0,
        )

        fun video(
            durationMs: Int,
            playheadMs: Int = 0,
            isPlaying: Boolean = false,
            widthPx: Int = 1080,
            heightPx: Int = 1920,
            projectName: String = "Untitled video",
        ): StudioEditorState {
            val safeDuration = durationMs.coerceAtLeast(0)
            return StudioEditorState(
                mode = StudioMode.VIDEO,
                projectName = projectName,
                canvasWidthPx = widthPx.coerceAtLeast(1),
                canvasHeightPx = heightPx.coerceAtLeast(1),
                durationMs = safeDuration,
                playheadMs = playheadMs.coerceIn(0, safeDuration),
                isPlaying = false,
            )
        }
    }
}

sealed interface StudioAction {
    data object TogglePlayPause : StudioAction
    data object ToggleLoop : StudioAction
    data object ToggleMute : StudioAction
    data object ClearMedia : StudioAction
    data class SetPlaying(val playing: Boolean) : StudioAction
    data class SeekTo(val timeMs: Int) : StudioAction
    data class StepBy(val deltaMs: Int) : StudioAction
    data class SetDuration(val durationMs: Int) : StudioAction
    data class SetPlaybackSpeed(val speed: Float) : StudioAction
    data class SelectTool(val tool: StudioTool) : StudioAction
    data class LoadMedia(val sourceUri: String) : StudioAction
    data class MediaReady(val durationMs: Int) : StudioAction
    data class MediaFailed(val message: String) : StudioAction
    data class SetTrimStart(val timeMs: Int) : StudioAction
    data class SetTrimEnd(val timeMs: Int) : StudioAction
}

private val supportedPlaybackSpeeds = setOf(0.25f, 0.5f, 1f, 1.5f, 2f)

fun reduceStudioState(
    state: StudioEditorState,
    action: StudioAction,
): StudioEditorState = when (action) {
    StudioAction.TogglePlayPause -> {
        if (!state.canPlay) state.copy(isPlaying = false)
        else state.copy(isPlaying = !state.isPlaying)
    }

    StudioAction.ToggleLoop -> state.copy(isLooping = !state.isLooping)
    StudioAction.ToggleMute -> state.copy(isMuted = !state.isMuted)
    StudioAction.ClearMedia -> state.copy(
        durationMs = 0,
        playheadMs = 0,
        isPlaying = false,
        sourceUri = null,
        mediaStatus = StudioMediaStatus.EMPTY,
        mediaError = null,
        clip = null,
    )

    is StudioAction.SetPlaying -> state.copy(
        isPlaying = action.playing && state.canPlay,
    )

    is StudioAction.SeekTo -> state.copy(
        playheadMs = action.timeMs.coerceIn(0, state.durationMs.coerceAtLeast(0)),
    )

    is StudioAction.StepBy -> state.copy(
        playheadMs = (state.playheadMs + action.deltaMs)
            .coerceIn(0, state.durationMs.coerceAtLeast(0)),
    )

    is StudioAction.SetDuration -> {
        val safeDuration = action.durationMs.coerceAtLeast(0)
        state.copy(
            durationMs = safeDuration,
            playheadMs = state.playheadMs.coerceIn(0, safeDuration),
            isPlaying = state.isPlaying && state.canPlay && safeDuration > 0,
            clip = state.clip?.let { existing ->
                val safeStart = existing.trimStartMs.coerceIn(0, safeDuration)
                existing.copy(
                    trimStartMs = safeStart,
                    trimEndMs = existing.trimEndMs.coerceIn(safeStart, safeDuration),
                )
            },
        )
    }

    is StudioAction.SetPlaybackSpeed -> {
        if (action.speed in supportedPlaybackSpeeds) state.copy(playbackSpeed = action.speed)
        else state
    }

    is StudioAction.SelectTool -> state.copy(selectedTool = action.tool)

    is StudioAction.LoadMedia -> {
        val source = action.sourceUri.trim()
        if (source.isEmpty()) {
            reduceStudioState(state, StudioAction.ClearMedia)
        } else {
            state.copy(
                durationMs = 0,
                playheadMs = 0,
                isPlaying = false,
                sourceUri = source,
                mediaStatus = StudioMediaStatus.LOADING,
                mediaError = null,
                clip = null,
            )
        }
    }

    is StudioAction.MediaReady -> {
        val source = state.sourceUri
        val safeDuration = action.durationMs.coerceAtLeast(0)
        if (source.isNullOrBlank() || safeDuration <= 0) {
            state.copy(
                durationMs = 0,
                playheadMs = 0,
                isPlaying = false,
                mediaStatus = StudioMediaStatus.FAILED,
                mediaError = "Media duration unavailable",
                clip = null,
            )
        } else {
            state.copy(
                durationMs = safeDuration,
                playheadMs = state.playheadMs.coerceIn(0, safeDuration),
                isPlaying = false,
                mediaStatus = StudioMediaStatus.READY,
                mediaError = null,
                clip = StudioClip(source, 0, safeDuration),
            )
        }
    }

    is StudioAction.MediaFailed -> state.copy(
        durationMs = 0,
        playheadMs = 0,
        isPlaying = false,
        mediaStatus = StudioMediaStatus.FAILED,
        mediaError = action.message.ifBlank { "Unable to load media" },
        clip = null,
    )

    is StudioAction.SetTrimStart -> {
        val existing = state.clip
        if (existing == null) state
        else state.copy(
            clip = existing.copy(
                trimStartMs = action.timeMs.coerceIn(0, existing.trimEndMs),
            ),
        )
    }

    is StudioAction.SetTrimEnd -> {
        val existing = state.clip
        if (existing == null) state
        else state.copy(
            clip = existing.copy(
                trimEndMs = action.timeMs.coerceIn(existing.trimStartMs, state.durationMs.coerceAtLeast(existing.trimStartMs)),
            ),
        )
    }
}

fun timelineFraction(
    playheadMs: Int,
    durationMs: Int,
): Float {
    if (durationMs <= 0) return 0f
    return (playheadMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f)
}

fun timeFromTimelineFraction(
    fraction: Float,
    durationMs: Int,
): Int {
    if (durationMs <= 0) return 0
    return (durationMs * fraction.coerceIn(0f, 1f)).roundToInt()
}

data class StudioTimelineLayer(
    val id: String,
    val type: StudioLayerType,
    val startMs: Int,
    val endMs: Int,
    val name: String = type.name.lowercase().replaceFirstChar { it.uppercase() },
    val locked: Boolean = false,
    val hidden: Boolean = false,
) {
    fun normalized(projectDurationMs: Int): StudioTimelineLayer {
        val duration = projectDurationMs.coerceAtLeast(0)
        val safeStart = startMs.coerceIn(0, duration)
        val safeEnd = endMs.coerceIn(safeStart, duration)
        return copy(startMs = safeStart, endMs = safeEnd)
    }
}

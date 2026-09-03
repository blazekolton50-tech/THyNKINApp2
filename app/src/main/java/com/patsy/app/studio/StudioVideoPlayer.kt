package com.patsy.app.studio

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import kotlinx.coroutines.delay

private val StudioBlack = Color(0xFF090A0C)
private val StudioPanel = Color(0xFF202124)
private val StudioText = Color(0xFFF7F7F7)
private val StudioMuted = Color(0xFFAAAAB0)

@Composable
fun StudioVideoPlayer(
    sourceUri: String?,
    state: StudioEditorState,
    onAction: (StudioAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val currentOnAction by rememberUpdatedState(onAction)
    val player = remember {
        ExoPlayer.Builder(context).build().apply {
            playWhenReady = false
            repeatMode = Player.REPEAT_MODE_OFF
        }
    }

    DisposableEffect(player) {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_READY) {
                    val duration = player.duration
                    if (duration != C.TIME_UNSET && duration in 1..Int.MAX_VALUE.toLong()) {
                        currentOnAction(StudioAction.MediaReady(duration.toInt()))
                    }
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                currentOnAction(
                    StudioAction.MediaFailed(
                        error.message ?: "Unable to load video",
                    ),
                )
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                currentOnAction(StudioAction.SetPlaying(isPlaying))
            }
        }
        player.addListener(listener)
        onDispose {
            player.removeListener(listener)
            player.release()
        }
    }

    LaunchedEffect(sourceUri) {
        player.stop()
        player.clearMediaItems()
        if (sourceUri.isNullOrBlank()) {
            currentOnAction(StudioAction.ClearMedia)
        } else {
            currentOnAction(StudioAction.LoadMedia(sourceUri))
            player.setMediaItem(MediaItem.fromUri(sourceUri))
            player.prepare()
        }
    }

    LaunchedEffect(state.isPlaying, state.canPlay, sourceUri) {
        if (sourceUri.isNullOrBlank() || !state.canPlay) {
            player.pause()
        } else if (state.isPlaying) {
            player.play()
        } else {
            player.pause()
        }
    }

    LaunchedEffect(state.isMuted) {
        player.volume = if (state.isMuted) 0f else 1f
    }

    LaunchedEffect(state.isLooping) {
        player.repeatMode = if (state.isLooping) Player.REPEAT_MODE_ONE else Player.REPEAT_MODE_OFF
    }

    LaunchedEffect(state.playbackSpeed) {
        player.playbackParameters = PlaybackParameters(state.playbackSpeed)
    }

    LaunchedEffect(state.playheadMs, sourceUri) {
        if (!sourceUri.isNullOrBlank() &&
            shouldSeekPlayer(player.currentPosition, state.playheadMs, state.canPlay)
        ) {
            player.seekTo(state.playheadMs.toLong())
        }
    }

    LaunchedEffect(player, sourceUri) {
        while (true) {
            if (!sourceUri.isNullOrBlank() && player.isPlaying) {
                val position = player.currentPosition.coerceAtLeast(0L)
                if (position <= Int.MAX_VALUE) {
                    currentOnAction(StudioAction.SeekTo(position.toInt()))
                }
            }
            delay(100)
        }
    }

    Column(
        modifier
            .fillMaxWidth()
            .background(StudioPanel, RoundedCornerShape(22.dp))
            .padding(12.dp),
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(260.dp)
                .background(StudioBlack, RoundedCornerShape(18.dp)),
            contentAlignment = Alignment.Center,
        ) {
            AndroidView(
                modifier = Modifier.fillMaxWidth().height(260.dp),
                factory = { viewContext ->
                    PlayerView(viewContext).apply {
                        useController = false
                        this.player = player
                        setShutterBackgroundColor(android.graphics.Color.BLACK)
                    }
                },
                update = { it.player = player },
            )
            when {
                sourceUri.isNullOrBlank() -> Text("Add a video clip to preview", color = StudioMuted)
                state.mediaStatus == StudioMediaStatus.LOADING -> Text("Loading clip…", color = StudioMuted)
                state.mediaStatus == StudioMediaStatus.FAILED -> Text(
                    state.mediaError ?: "Unable to load clip",
                    color = StudioMuted,
                )
            }
        }

        Spacer(Modifier.height(10.dp))

        Slider(
            value = timelineFraction(state.playheadMs, state.durationMs),
            onValueChange = { fraction ->
                currentOnAction(
                    StudioAction.SeekTo(
                        timeFromTimelineFraction(fraction, state.durationMs),
                    ),
                )
            },
            enabled = state.canPlay,
            modifier = Modifier.fillMaxWidth(),
        )

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextButton(
                onClick = { currentOnAction(StudioAction.StepBy(-1_000)) },
                enabled = state.canPlay,
            ) {
                Text("−1s", color = StudioText)
            }
            Button(
                onClick = { currentOnAction(StudioAction.TogglePlayPause) },
                enabled = state.canPlay,
                colors = ButtonDefaults.buttonColors(
                    containerColor = StudioText,
                    contentColor = Color.Black,
                ),
            ) {
                Text(if (state.isPlaying) "Pause" else "Play")
            }
            TextButton(
                onClick = { currentOnAction(StudioAction.StepBy(1_000)) },
                enabled = state.canPlay,
            ) {
                Text("+1s", color = StudioText)
            }
            Spacer(Modifier.weight(1f))
            Text(
                "${formatStudioTime(state.playheadMs)} / ${formatStudioTime(state.durationMs)}",
                color = StudioMuted,
            )
        }

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextButton(onClick = { currentOnAction(StudioAction.ToggleMute) }) {
                Text(if (state.isMuted) "Unmute" else "Mute", color = StudioText)
            }
            TextButton(onClick = { currentOnAction(StudioAction.ToggleLoop) }) {
                Text(if (state.isLooping) "Loop on" else "Loop", color = StudioText)
            }
            Spacer(Modifier.weight(1f))
            listOf(0.5f, 1f, 1.5f, 2f).forEach { speed ->
                TextButton(onClick = { currentOnAction(StudioAction.SetPlaybackSpeed(speed)) }) {
                    Text(
                        if (speed == state.playbackSpeed) "${speed}×" else speedLabel(speed),
                        color = if (speed == state.playbackSpeed) StudioText else StudioMuted,
                    )
                }
            }
        }
    }
}

private fun speedLabel(speed: Float): String = when (speed) {
    1f -> "1×"
    else -> "${speed}×"
}

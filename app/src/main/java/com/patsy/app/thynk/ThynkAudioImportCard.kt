package com.patsy.app.thynk

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.patsy.app.ui.finaldesign.FinalCard
import com.patsy.app.ui.finaldesign.FinalMuted
import com.patsy.app.ui.finaldesign.FinalWhite

/** Real Android media-picker + Media3 playback surface for THyNK Music Track Editor. */
@Composable
fun ThynkAudioImportCard() {
    val context = LocalContext.current
    val player = remember(context) { ExoPlayer.Builder(context.applicationContext).build() }
    var state by remember { mutableStateOf(ThynkImportedAudioState.initial()) }

    val picker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { uri ->
        if (uri != null) {
            player.stop()
            player.clearMediaItems()
            state = reduceImportedAudioState(
                state,
                ThynkImportedAudioAction.Selected(uri.toString()),
            )
        }
    }

    DisposableEffect(player) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                state = if (isPlaying) {
                    reduceImportedAudioState(state, ThynkImportedAudioAction.Playing)
                } else if (state.phase == ThynkImportedAudioPhase.PLAYING) {
                    reduceImportedAudioState(state, ThynkImportedAudioAction.Stopped)
                } else {
                    state
                }
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_ENDED) {
                    state = reduceImportedAudioState(state, ThynkImportedAudioAction.Stopped)
                    player.seekTo(0)
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                state = reduceImportedAudioState(state, ThynkImportedAudioAction.Failed)
            }
        }
        player.addListener(listener)
        onDispose {
            player.removeListener(listener)
            player.release()
        }
    }

    Column(
        Modifier.fillMaxWidth()
            .border(1.dp, Color(0xFF3B3B45), RoundedCornerShape(20.dp))
            .background(FinalCard, RoundedCornerShape(20.dp))
            .padding(15.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text("REAL AUDIO IMPORT", color = FinalWhite, fontSize = 13.sp, fontWeight = FontWeight.Black)
        Text(
            when (state.phase) {
                ThynkImportedAudioPhase.EMPTY -> "No audio selected"
                ThynkImportedAudioPhase.READY -> "Audio ready"
                ThynkImportedAudioPhase.PLAYING -> "Playing selected audio"
                ThynkImportedAudioPhase.ERROR -> "Playback error"
            },
            color = if (state.phase == ThynkImportedAudioPhase.ERROR) Color(0xFFFFC46B) else FinalMuted,
            fontSize = 11.sp,
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { picker.launch("audio/*") },
                colors = ButtonDefaults.buttonColors(
                    containerColor = FinalWhite,
                    contentColor = Color.Black,
                ),
            ) {
                Text(if (state.uri == null) "IMPORT AUDIO" else "CHANGE AUDIO", fontWeight = FontWeight.Black)
            }

            if (state.uri != null) {
                OutlinedButton(
                    onClick = {
                        if (state.phase == ThynkImportedAudioPhase.PLAYING) {
                            player.pause()
                            state = reduceImportedAudioState(state, ThynkImportedAudioAction.Stopped)
                        } else {
                            val uri = state.uri
                            if (uri != null) {
                                player.setMediaItem(MediaItem.fromUri(Uri.parse(uri)))
                                player.prepare()
                                player.playWhenReady = true
                            }
                        }
                    },
                ) {
                    Text(
                        if (state.phase == ThynkImportedAudioPhase.PLAYING) "PAUSE" else "PLAY",
                        color = FinalWhite,
                        fontWeight = FontWeight.Bold,
                    )
                }

                OutlinedButton(
                    onClick = {
                        player.stop()
                        player.clearMediaItems()
                        state = reduceImportedAudioState(state, ThynkImportedAudioAction.Clear)
                    },
                ) {
                    Text("CLEAR", color = FinalWhite)
                }
            }
        }

        if (state.uri != null) {
            Text(
                "Selected through Android's media picker. Playback uses the actual chosen URI; no demo clip is substituted.",
                color = FinalMuted,
                fontSize = 10.sp,
                lineHeight = 15.sp,
            )
        } else {
            Text(
                "Choose a real audio file from this device or an Android document provider.",
                color = FinalMuted,
                fontSize = 10.sp,
            )
        }
    }
}

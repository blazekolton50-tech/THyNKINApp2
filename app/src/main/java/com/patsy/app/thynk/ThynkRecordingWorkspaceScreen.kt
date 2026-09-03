package com.patsy.app.thynk

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.Alignment
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
import java.io.File

@Composable
fun ThynkRecordingWorkspaceScreen() {
    val context = LocalContext.current
    val recorder = remember(context) { NativeThynkAudioRecorder(context.applicationContext) }
    val player = remember(context) { ExoPlayer.Builder(context.applicationContext).build() }
    var state by remember { mutableStateOf(ThynkAudioRecordingState.initial()) }
    var playbackState by remember { mutableStateOf(ThynkRecordedTakePlaybackState.IDLE) }

    fun applyStartResult(result: Result<File>) {
        state = result.fold(
            onSuccess = { file ->
                reduceThynkAudioRecordingState(
                    state,
                    ThynkAudioRecordingAction.Started(file.absolutePath),
                )
            },
            onFailure = { error ->
                reduceThynkAudioRecordingState(
                    state,
                    ThynkAudioRecordingAction.Failed(error.message ?: "Could not start recording"),
                )
            },
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) {
            applyStartResult(recorder.start())
        } else {
            state = reduceThynkAudioRecordingState(
                state,
                ThynkAudioRecordingAction.PermissionDenied,
            )
        }
    }

    DisposableEffect(recorder) {
        onDispose { recorder.release() }
    }

    DisposableEffect(player) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (isPlaying) {
                    playbackState = reduceRecordedTakePlaybackState(
                        playbackState,
                        ThynkRecordedTakePlaybackAction.Playing,
                    )
                } else if (playbackState == ThynkRecordedTakePlaybackState.PLAYING) {
                    playbackState = reduceRecordedTakePlaybackState(
                        playbackState,
                        ThynkRecordedTakePlaybackAction.Stopped,
                    )
                }
            }

            override fun onPlaybackStateChanged(playbackStateValue: Int) {
                if (playbackStateValue == Player.STATE_ENDED) {
                    playbackState = reduceRecordedTakePlaybackState(
                        playbackState,
                        ThynkRecordedTakePlaybackAction.Completed,
                    )
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                playbackState = reduceRecordedTakePlaybackState(
                    playbackState,
                    ThynkRecordedTakePlaybackAction.Failed,
                )
            }
        }
        player.addListener(listener)
        onDispose {
            player.removeListener(listener)
            player.release()
        }
    }

    Column(
        Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text("THyNK MUSIC", color = FinalWhite, fontSize = 12.sp, fontWeight = FontWeight.Black)
        Text("Recording", color = FinalWhite, fontSize = 30.sp, fontWeight = FontWeight.Black)
        Text(
            "Native microphone capture • AAC audio in a local .m4a file",
            color = FinalMuted,
            fontSize = 12.sp,
        )

        Column(
            Modifier.fillMaxWidth()
                .border(1.dp, Color(0xFF3B3B45), RoundedCornerShape(22.dp))
                .background(FinalCard, RoundedCornerShape(22.dp))
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                when (state.phase) {
                    ThynkAudioRecordingPhase.IDLE -> "READY"
                    ThynkAudioRecordingPhase.PERMISSION_REQUIRED -> "MICROPHONE PERMISSION REQUIRED"
                    ThynkAudioRecordingPhase.RECORDING -> "● RECORDING"
                    ThynkAudioRecordingPhase.RECORDED -> "TAKE SAVED"
                    ThynkAudioRecordingPhase.ERROR -> "RECORDING ERROR"
                },
                color = if (state.phase == ThynkAudioRecordingPhase.RECORDING) Color(0xFFFF6B6B) else FinalWhite,
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
            )

            Text(
                state.message ?: "Press record to create a real local microphone take.",
                color = FinalMuted,
                fontSize = 12.sp,
            )

            when (state.phase) {
                ThynkAudioRecordingPhase.RECORDING -> {
                    Button(
                        onClick = {
                            state = recorder.stop().fold(
                                onSuccess = { file ->
                                    reduceThynkAudioRecordingState(
                                        state,
                                        ThynkAudioRecordingAction.Stopped(file.absolutePath),
                                    )
                                },
                                onFailure = { error ->
                                    reduceThynkAudioRecordingState(
                                        state,
                                        ThynkAudioRecordingAction.Failed(error.message ?: "Could not stop recording"),
                                    )
                                },
                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFF6B6B),
                            contentColor = Color.Black,
                        ),
                    ) {
                        Text("STOP & SAVE", fontWeight = FontWeight.Black)
                    }
                }

                ThynkAudioRecordingPhase.RECORDED -> {
                    val recordedFile = state.outputPath?.let(::File)
                    val name = recordedFile?.name ?: "recording.m4a"
                    Text(name, color = FinalWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text("Real local file • AAC/M4A", color = FinalMuted, fontSize = 11.sp)

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            enabled = recordedFile?.exists() == true,
                            onClick = {
                                if (playbackState == ThynkRecordedTakePlaybackState.PLAYING) {
                                    player.pause()
                                    playbackState = reduceRecordedTakePlaybackState(
                                        playbackState,
                                        ThynkRecordedTakePlaybackAction.Stopped,
                                    )
                                } else if (recordedFile != null && recordedFile.exists()) {
                                    player.setMediaItem(MediaItem.fromUri(Uri.fromFile(recordedFile)))
                                    player.prepare()
                                    player.playWhenReady = true
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = FinalWhite,
                                contentColor = Color.Black,
                            ),
                        ) {
                            Text(
                                if (playbackState == ThynkRecordedTakePlaybackState.PLAYING) "PAUSE" else "PLAY TAKE",
                                fontWeight = FontWeight.Black,
                            )
                        }

                        OutlinedButton(
                            onClick = {
                                player.stop()
                                player.clearMediaItems()
                                playbackState = reduceRecordedTakePlaybackState(
                                    playbackState,
                                    ThynkRecordedTakePlaybackAction.Stopped,
                                )
                                state = reduceThynkAudioRecordingState(
                                    state,
                                    ThynkAudioRecordingAction.Reset,
                                )
                            },
                        ) {
                            Text("NEW TAKE", color = FinalWhite)
                        }
                    }

                    Text(
                        when (playbackState) {
                            ThynkRecordedTakePlaybackState.IDLE -> "Playback ready"
                            ThynkRecordedTakePlaybackState.PLAYING -> "Playing real recorded take"
                            ThynkRecordedTakePlaybackState.ERROR -> "Playback failed"
                        },
                        color = if (playbackState == ThynkRecordedTakePlaybackState.ERROR) Color(0xFFFFC46B) else FinalMuted,
                        fontSize = 10.sp,
                    )
                }

                else -> {
                    Button(
                        onClick = {
                            if (context.checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                                applyStartResult(recorder.start())
                            } else {
                                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = FinalWhite,
                            contentColor = Color.Black,
                        ),
                    ) {
                        Text("RECORD", fontWeight = FontWeight.Black)
                    }
                }
            }
        }

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            RecordingFeatureCard("INPUT", "Device microphone", Modifier.weight(1f))
            RecordingFeatureCard("FORMAT", "AAC / M4A", Modifier.weight(1f))
        }

        RecordingInfoPanel(
            "TRUTHFUL AUDIO BOUNDARY",
            "This screen records and plays back a real local microphone take. It does not claim live autotune, MP3 encoding, stem separation, mastering or a generated waveform until those engines are genuinely connected.",
        )
        Spacer(Modifier.height(12.dp))
    }
}

@Composable
private fun RecordingFeatureCard(title: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier
            .border(1.dp, Color(0xFF3B3B45), RoundedCornerShape(18.dp))
            .background(FinalCard, RoundedCornerShape(18.dp))
            .padding(14.dp),
    ) {
        Text(title, color = FinalMuted, fontSize = 9.sp, fontWeight = FontWeight.Black)
        Text(value, color = FinalWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 5.dp))
    }
}

@Composable
private fun RecordingInfoPanel(title: String, body: String) {
    Column(
        Modifier.fillMaxWidth()
            .border(1.dp, Color(0xFF3B3B45), RoundedCornerShape(18.dp))
            .background(FinalCard, RoundedCornerShape(18.dp))
            .padding(14.dp),
    ) {
        Text(title, color = FinalWhite, fontSize = 11.sp, fontWeight = FontWeight.Black)
        Text(body, color = FinalMuted, fontSize = 11.sp, lineHeight = 16.sp, modifier = Modifier.padding(top = 6.dp))
    }
}

package com.patsy.app.thynk

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import java.io.File

/**
 * Small native recording boundary for THyNK Music.
 *
 * Records a real microphone take to an AAC-in-MP4 (.m4a) file in app cache. It does not
 * fabricate a recording, waveform, MP3, stem or mastered output.
 */
class NativeThynkAudioRecorder(
    private val context: Context,
) {
    private var recorder: MediaRecorder? = null
    private var pendingFile: File? = null

    fun start(): Result<File> {
        if (context.checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            return Result.failure(SecurityException("Microphone permission is required"))
        }
        if (recorder != null) {
            return Result.failure(IllegalStateException("A recording is already active"))
        }

        val output = File(
            context.cacheDir,
            "thynk-recording-${System.currentTimeMillis()}.m4a",
        )
        val candidate = createMediaRecorder()

        return try {
            candidate.setAudioSource(MediaRecorder.AudioSource.MIC)
            candidate.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            candidate.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            candidate.setAudioSamplingRate(44_100)
            candidate.setAudioEncodingBitRate(128_000)
            candidate.setOutputFile(output.absolutePath)
            candidate.prepare()
            candidate.start()
            recorder = candidate
            pendingFile = output
            Result.success(output)
        } catch (error: Exception) {
            runCatching { candidate.reset() }
            runCatching { candidate.release() }
            output.delete()
            recorder = null
            pendingFile = null
            Result.failure(error)
        }
    }

    fun stop(): Result<File> {
        val active = recorder
            ?: return Result.failure(IllegalStateException("No recording is active"))
        val output = pendingFile
            ?: return Result.failure(IllegalStateException("Recording output is unavailable"))

        var completed = false
        return try {
            active.stop()
            completed = output.exists() && output.length() > 0L
            if (completed) {
                Result.success(output)
            } else {
                output.delete()
                Result.failure(IllegalStateException("Android did not produce a recording file"))
            }
        } catch (error: Exception) {
            output.delete()
            Result.failure(error)
        } finally {
            runCatching { active.reset() }
            runCatching { active.release() }
            recorder = null
            pendingFile = null
            if (!completed && output.exists()) output.delete()
        }
    }

    /** Cancels any active take and removes the incomplete cache file. */
    fun release() {
        val active = recorder
        recorder = null
        runCatching { active?.reset() }
        runCatching { active?.release() }
        pendingFile?.delete()
        pendingFile = null
    }

    @Suppress("DEPRECATION")
    private fun createMediaRecorder(): MediaRecorder =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            MediaRecorder()
        }
}

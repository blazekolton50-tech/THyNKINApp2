package com.patsy.app.studio

import kotlin.math.abs

internal fun formatStudioTime(timeMs: Int): String {
    val safe = timeMs.coerceAtLeast(0)
    val totalSeconds = safe / 1_000
    return "%d:%02d".format(totalSeconds / 60, totalSeconds % 60)
}

internal fun shouldSeekPlayer(
    playerPositionMs: Long,
    statePositionMs: Int,
    canPlay: Boolean,
): Boolean = canPlay && abs(playerPositionMs - statePositionMs) > 350L

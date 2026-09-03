package com.patsy.app.patsy.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.patsy.app.patsy.PatsyCompanionController
import com.patsy.app.patsy.PatsyCompanionState
import com.patsy.app.patsy.PatsyCompanionTarget
import com.patsy.app.patsy.rig.PatsyRigCoordinator
import com.patsy.app.patsy.rig.rive.PatsyRiveHost
import com.patsy.app.patsy.rig.rive.PatsyRiveRuntimeAdapter

/**
 * Drop-in Compose entry matching the owner-facing API:
 *
 * PatsyQuickShrink(onMissionStart = { ... })
 *
 * It shrinks Big Patsy (300 / 1.0) to Mini Patsy (150 / 0.5) in 0.8 s, runs for 0.4 s,
 * arrives beside [target], points, then invokes [onMissionStart].
 *
 * The shrink is deliberately simple: jump, shrink while airborne, land Mini. There is no
 * rainbow/glitter overlay and no second rendered Patsy.
 */
@Composable
fun PatsyQuickShrink(
    onMissionStart: () -> Unit,
    target: PatsyCompanionTarget = PatsyCompanionTarget(0.82f, 0.30f),
    reducedMotion: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val runtime = remember { PatsyRiveRuntimeAdapter() }
    val rig = remember(runtime) { PatsyRigCoordinator(runtime) }
    var companionState by remember { mutableStateOf(PatsyCompanionState()) }
    val controller = remember(rig) {
        PatsyCompanionController(
            rig = rig,
            onStateChanged = { companionState = it },
        )
    }

    LaunchedEffect(reducedMotion) {
        controller.setReducedMotion(reducedMotion)
    }

    LaunchedEffect(target) {
        controller.guideTo(target)
        onMissionStart()
    }

    DisposableEffect(runtime) {
        onDispose { runtime.close() }
    }

    PatsyRiveHost(
        runtime = runtime,
        modifier = modifier.fillMaxSize(),
        playing = !reducedMotion,
        fallback = {
            PatsyTravelFallback(
                state = companionState,
                modifier = Modifier.fillMaxSize(),
            )
        },
    )
}

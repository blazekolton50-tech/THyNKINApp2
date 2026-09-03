package com.patsy.app.patsy.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.patsy.app.R
import com.patsy.app.patsy.PatsyCompanionController
import com.patsy.app.patsy.PatsyCompanionState
import com.patsy.app.patsy.PatsyCompanionTarget
import com.patsy.app.patsy.rig.PatsyRigCoordinator
import com.patsy.app.patsy.rig.rive.PatsyRiveHost
import com.patsy.app.patsy.rig.rive.PatsyRiveRuntimeAdapter

sealed interface PatsyCompanionCommand {
    data class GuideTo(val target: PatsyCompanionTarget) : PatsyCompanionCommand
    data object ReturnHome : PatsyCompanionCommand
}

/** One transparent, full-screen Patsy layer for authenticated THyNK-IN! pages. */
@Composable
fun PatsyCompanionOverlay(
    command: PatsyCompanionCommand?,
    onCommandConsumed: () -> Unit,
    reducedMotion: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val runtime = remember { PatsyRiveRuntimeAdapter() }
    val rig = remember(runtime) { PatsyRigCoordinator(runtime) }
    var companionState by remember { mutableStateOf(PatsyCompanionState()) }
    val controller = remember(rig) {
        PatsyCompanionController(
            rig = rig,
            onStateChanged = { state -> companionState = state },
        )
    }

    LaunchedEffect(reducedMotion) {
        controller.setReducedMotion(reducedMotion)
    }

    LaunchedEffect(command) {
        when (command) {
            is PatsyCompanionCommand.GuideTo -> controller.guideTo(command.target)
            PatsyCompanionCommand.ReturnHome -> controller.returnHome()
            null -> return@LaunchedEffect
        }
        onCommandConsumed()
    }

    DisposableEffect(runtime) {
        onDispose { runtime.close() }
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        PatsyRiveHost(
            runtime = runtime,
            modifier = Modifier.fillMaxSize(),
            playing = !reducedMotion,
            fallback = {
                PatsyTravelFallback(
                    state = companionState,
                    modifier = Modifier.fillMaxSize(),
                )
            },
        )
    }
}

/** Same approved Patsy image is moved/scaled if Rive is unavailable; no second mini sprite is used. */
@Composable
internal fun PatsyTravelFallback(
    state: PatsyCompanionState,
    modifier: Modifier,
) {
    BoxWithConstraints(modifier = modifier) {
        val baseSize = 300.dp
        val scaledSize = baseSize * state.pose.stageScale
        val x = (maxWidth * state.pose.stageX) - (scaledSize / 2f)
        val y = (maxHeight * state.pose.stageY) - (scaledSize / 2f)

        Image(
            painter = painterResource(R.drawable.patsy_generated_main),
            contentDescription = "Patsy assistant",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .offset(x = x, y = y)
                .size(scaledSize),
        )
    }
}

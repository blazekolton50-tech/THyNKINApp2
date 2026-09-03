package com.patsy.app.patsy

import com.patsy.app.patsy.rig.PatsyRigContractV1
import com.patsy.app.patsy.rig.PatsyRigCoordinator
import com.patsy.app.patsy.rig.PatsyRigMotion
import com.patsy.app.patsy.rig.PatsyRigMutation
import com.patsy.app.patsy.rig.PatsyRigRuntimePort
import com.patsy.app.patsy.rig.PatsyRigStatus
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PatsyCanonicalShrinkTest {

    @Test
    fun canonicalSizeStatesAreBigThreeHundredAndMiniOneHundredFifty() {
        assertEquals(1.0f, PatsySizeState.Big.scale)
        assertEquals(300, PatsySizeState.Big.dp)
        assertEquals(0.5f, PatsySizeState.Mini.scale)
        assertEquals(150, PatsySizeState.Mini.dp)
    }

    @Test
    fun canonicalShrinkJumpsAndLandsAtMiniScaleInEightHundredMilliseconds() = runTest {
        val observed = mutableListOf<PatsyCompanionState>()
        var currentMode = PatsyCompanionMode.IDLE
        var shrinkWaitMillis = 0L

        val controller = PatsyCompanionController(
            rig = PatsyRigCoordinator(RecordingRuntime()),
            onStateChanged = { state ->
                observed += state
                currentMode = state.mode
            },
            frameWait = { millis ->
                if (currentMode == PatsyCompanionMode.SHRINKING) shrinkWaitMillis += millis
            },
        )

        controller.guideTo(PatsyCompanionTarget(normalizedX = 0.82f, normalizedY = 0.30f))

        val shrinking = observed.filter { it.mode == PatsyCompanionMode.SHRINKING }
        assertTrue(shrinking.any { it.pose.motion == PatsyRigMotion.JUMP })
        assertTrue(shrinking.any { it.pose.stageY < 0.75f })
        val finalShrinkState = shrinking.last()
        assertEquals(0.50f, finalShrinkState.pose.stageScale)
        assertEquals(800L, shrinkWaitMillis)
        assertEquals(0.50f, controller.state.pose.stageScale)
    }

    @Test
    fun canonicalShrinkRunsRightToMissionForFourHundredMilliseconds() = runTest {
        val observed = mutableListOf<PatsyCompanionState>()
        var currentMode = PatsyCompanionMode.IDLE
        var travelWaitMillis = 0L
        val mission = PatsyCompanionTarget(normalizedX = 0.82f, normalizedY = 0.30f)

        val controller = PatsyCompanionController(
            rig = PatsyRigCoordinator(RecordingRuntime()),
            onStateChanged = { state ->
                observed += state
                currentMode = state.mode
            },
            frameWait = { millis ->
                if (currentMode == PatsyCompanionMode.TRAVELLING) travelWaitMillis += millis
            },
        )

        controller.guideTo(mission)

        val firstTravelState = observed.first { it.mode == PatsyCompanionMode.TRAVELLING }
        assertEquals("run", firstTravelState.pose.motion.riveValue)
        assertEquals(1f, firstTravelState.pose.facing)
        assertEquals(400L, travelWaitMillis)
        assertEquals(0.50f, firstTravelState.pose.stageScale)
    }

    @Test
    fun canonicalShrinkDoesNotRequestRainbowOrGlitter() = runTest {
        var effectCount = 0

        val controller = PatsyCompanionController(
            rig = PatsyRigCoordinator(RecordingRuntime()),
            onEffectRequested = { effectCount += 1 },
            frameWait = {},
        )

        controller.guideTo(PatsyCompanionTarget(normalizedX = 0.82f, normalizedY = 0.30f))
        assertEquals(0, effectCount)
    }

    @Test
    fun canonicalRiveContractUsesPatsySevenTwentyArtboardAndBodyStates() {
        assertEquals("Patsy", PatsyRigContractV1.ARTBOARD)
        assertEquals(720, PatsyRigContractV1.ARTBOARD_WIDTH)
        assertEquals(720, PatsyRigContractV1.ARTBOARD_HEIGHT)
        assertEquals(true, PatsyRigContractV1.ARTBOARD_TRANSPARENT)
        assertEquals("jump", enumValues<PatsyRigMotion>().firstOrNull { it.name == "JUMP" }?.riveValue)
        assertEquals("run", enumValues<PatsyRigMotion>().firstOrNull { it.name == "RUN" }?.riveValue)
        assertEquals("stand", enumValues<PatsyRigMotion>().firstOrNull { it.name == "STAND" }?.riveValue)
    }

    private class RecordingRuntime : PatsyRigRuntimePort {
        override val status: PatsyRigStatus = PatsyRigStatus.Ready
        override fun apply(mutations: List<PatsyRigMutation>) = Unit
    }
}

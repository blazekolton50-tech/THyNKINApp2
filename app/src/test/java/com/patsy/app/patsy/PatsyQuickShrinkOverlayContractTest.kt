package com.patsy.app.patsy

import java.io.File
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PatsyQuickShrinkOverlayContractTest {
    @Test
    fun `authenticated shell exposes one native Patsy jump-shrink overlay`() {
        val activity = source("app/src/main/java/com/patsy/app/FinalMainActivity.kt")
        val overlay = source("app/src/main/java/com/patsy/app/patsy/ui/PatsyCompanionOverlay.kt")
        val quickShrink = source("app/src/main/java/com/patsy/app/patsy/ui/PatsyQuickShrink.kt")
        val controller = source("app/src/main/java/com/patsy/app/patsy/PatsyCompanionController.kt")

        assertTrue(activity.contains("PatsyCompanionOverlay("))
        assertTrue(activity.contains("var patsyCommand by remember"))
        assertTrue(activity.contains("patsyCommand = PatsyCompanionCommand.GuideTo("))
        assertTrue(activity.contains("patsyCommand = PatsyCompanionCommand.ReturnHome"))

        assertTrue(overlay.contains("Modifier.fillMaxSize()"))
        assertTrue(overlay.contains("PatsyRiveHost("))
        assertTrue(overlay.contains("PatsyCompanionController("))
        assertFalse(overlay.contains("PatsyShrinkRainbow("))
        assertTrue(overlay.contains("R.drawable.patsy_generated_main"))
        assertTrue(overlay.contains("baseSize = 300.dp"))

        assertTrue(controller.contains("PatsyRigMotion.JUMP"))
        assertTrue(controller.contains("JUMP_HEIGHT"))
        assertFalse(controller.contains("onEffectRequested(CANONICAL_SHRINK_EFFECT)"))

        assertTrue(quickShrink.contains("fun PatsyQuickShrink("))
        assertTrue(quickShrink.contains("onMissionStart: () -> Unit"))
        assertTrue(quickShrink.contains("controller.guideTo("))
        assertFalse(quickShrink.contains("PatsyShrinkRainbow("))
        assertFalse(quickShrink.contains("RISING_RAINBOW_GLITTER"))
    }

    private fun source(path: String): String {
        val candidates = sequenceOf(File(path), File("../$path"))
        return candidates.firstOrNull(File::isFile)?.readText()
            ?: error("Could not locate $path")
    }
}

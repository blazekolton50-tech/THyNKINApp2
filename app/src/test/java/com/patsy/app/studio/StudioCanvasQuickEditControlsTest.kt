package com.patsy.app.studio

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class StudioCanvasQuickEditControlsTest {
    private fun selected(locked: Boolean = false) = StudioCanvasObject(
        id = "shape-1",
        type = StudioLayerType.SHAPE,
        label = "Shape",
        xPx = 120f,
        yPx = 160f,
        widthPx = 240f,
        heightPx = 120f,
        rotationDegrees = 30f,
        opacity = 0.6f,
        locked = locked,
    )

    @Test
    fun positionControlsMapToRealMoveAndRotateActions() {
        val controls = studioCanvasQuickEditControls(selected(), StudioCanvasQuickEditGroup.POSITION)

        assertEquals(
            listOf("X -10", "X +10", "Y -10", "Y +10", "ROT -15", "ROT +15"),
            controls.map { it.label },
        )
        assertEquals(
            listOf(
                StudioCanvasAction.Move("shape-1", -10f, 0f),
                StudioCanvasAction.Move("shape-1", 10f, 0f),
                StudioCanvasAction.Move("shape-1", 0f, -10f),
                StudioCanvasAction.Move("shape-1", 0f, 10f),
                StudioCanvasAction.Rotate("shape-1", 15f),
                StudioCanvasAction.Rotate("shape-1", 45f),
            ),
            controls.map { it.action },
        )
    }

    @Test
    fun opacityControlsMapToRealOpacityActions() {
        val controls = studioCanvasQuickEditControls(selected(), StudioCanvasQuickEditGroup.OPACITY)

        assertEquals(listOf("-10%", "+10%"), controls.map { it.label })
        assertEquals(
            listOf(
                StudioCanvasAction.SetOpacity("shape-1", 0.5f),
                StudioCanvasAction.SetOpacity("shape-1", 0.7f),
            ),
            controls.map { it.action },
        )
    }

    @Test
    fun lockedOrMissingSelectionExposesNoQuickEditControls() {
        assertTrue(studioCanvasQuickEditControls(selected(locked = true), StudioCanvasQuickEditGroup.POSITION).isEmpty())
        assertTrue(studioCanvasQuickEditControls(null, StudioCanvasQuickEditGroup.OPACITY).isEmpty())
    }
}

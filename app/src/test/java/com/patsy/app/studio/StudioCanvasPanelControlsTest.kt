package com.patsy.app.studio

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class StudioCanvasPanelControlsTest {
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
    fun positionPanelExposesMoveRotateSizeAndCanvasAlignment() {
        val controls = studioCanvasPanelControls("position", selected())

        assertEquals(
            listOf("X -10", "X +10", "Y -10", "Y +10", "ROT -15", "ROT +15"),
            controls.quick.map { it.label },
        )
        assertEquals(
            listOf("W -10", "W +10", "H -10", "H +10"),
            controls.size.map { it.label },
        )
        assertEquals(
            listOf("LEFT", "H-CENTRE", "RIGHT", "TOP", "V-CENTRE", "BOTTOM"),
            controls.alignment.map { it.label },
        )
    }

    @Test
    fun opacityPanelExposesOnlyOpacityQuickEdits() {
        val controls = studioCanvasPanelControls("opacity", selected())

        assertEquals(listOf("-10%", "+10%"), controls.quick.map { it.label })
        assertTrue(controls.size.isEmpty())
        assertTrue(controls.alignment.isEmpty())
    }

    @Test
    fun lockedOrUnknownPanelsExposeNoEditableControls() {
        val locked = studioCanvasPanelControls("position", selected(locked = true))
        val unknown = studioCanvasPanelControls("templates", selected())

        assertTrue(locked.quick.isEmpty())
        assertTrue(locked.size.isEmpty())
        assertTrue(locked.alignment.isEmpty())
        assertTrue(unknown.quick.isEmpty())
        assertTrue(unknown.size.isEmpty())
        assertTrue(unknown.alignment.isEmpty())
    }
}

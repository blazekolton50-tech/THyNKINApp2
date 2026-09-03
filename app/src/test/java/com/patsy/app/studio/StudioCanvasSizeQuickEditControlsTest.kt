package com.patsy.app.studio

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class StudioCanvasSizeQuickEditControlsTest {
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
    fun sizeControlsMapToRealResizeActions() {
        val controls = studioCanvasSizeQuickEditControls(selected())

        assertEquals(
            listOf("W -10", "W +10", "H -10", "H +10"),
            controls.map { it.label },
        )
        assertEquals(
            listOf(
                StudioCanvasAction.Resize("shape-1", 230f, 120f),
                StudioCanvasAction.Resize("shape-1", 250f, 120f),
                StudioCanvasAction.Resize("shape-1", 240f, 110f),
                StudioCanvasAction.Resize("shape-1", 240f, 130f),
            ),
            controls.map { it.action },
        )
    }

    @Test
    fun lockedOrMissingSelectionExposesNoSizeControls() {
        assertTrue(studioCanvasSizeQuickEditControls(selected(locked = true)).isEmpty())
        assertTrue(studioCanvasSizeQuickEditControls(null).isEmpty())
    }
}

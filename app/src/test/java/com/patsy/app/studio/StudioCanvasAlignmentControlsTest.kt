package com.patsy.app.studio

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class StudioCanvasAlignmentControlsTest {
    private fun selected(locked: Boolean = false) = StudioCanvasObject(
        id = "shape-1",
        type = StudioLayerType.SHAPE,
        label = "Shape",
        xPx = 120f,
        yPx = 160f,
        widthPx = 240f,
        heightPx = 120f,
        locked = locked,
    )

    @Test
    fun editableSelectionExposesSixCanvasAlignmentControls() {
        val controls = studioCanvasAlignmentControls(selected())

        assertEquals(
            listOf("LEFT", "H-CENTRE", "RIGHT", "TOP", "V-CENTRE", "BOTTOM"),
            controls.map { it.label },
        )
        assertEquals(
            listOf(
                StudioCanvasAction.AlignHorizontal("shape-1", StudioCanvasHorizontalAlignment.LEFT),
                StudioCanvasAction.AlignHorizontal("shape-1", StudioCanvasHorizontalAlignment.CENTER),
                StudioCanvasAction.AlignHorizontal("shape-1", StudioCanvasHorizontalAlignment.RIGHT),
                StudioCanvasAction.AlignVertical("shape-1", StudioCanvasVerticalAlignment.TOP),
                StudioCanvasAction.AlignVertical("shape-1", StudioCanvasVerticalAlignment.CENTER),
                StudioCanvasAction.AlignVertical("shape-1", StudioCanvasVerticalAlignment.BOTTOM),
            ),
            controls.map { it.action },
        )
    }

    @Test
    fun lockedOrMissingSelectionExposesNoAlignmentControls() {
        assertTrue(studioCanvasAlignmentControls(selected(locked = true)).isEmpty())
        assertTrue(studioCanvasAlignmentControls(null).isEmpty())
    }
}

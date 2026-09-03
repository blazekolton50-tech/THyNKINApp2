package com.patsy.app.studio

import kotlin.test.Test
import kotlin.test.assertEquals

class StudioCanvasAlignmentTest {
    private fun obj(
        id: String,
        x: Float = 123f,
        y: Float = 234f,
        width: Float = 200f,
        height: Float = 100f,
        locked: Boolean = false,
    ) = StudioCanvasObject(
        id = id,
        type = StudioLayerType.SHAPE,
        label = id,
        xPx = x,
        yPx = y,
        widthPx = width,
        heightPx = height,
        locked = locked,
    )

    @Test
    fun horizontalAlignmentUsesCanvasBounds() {
        val state = StudioCanvasState(1000, 800, listOf(obj("shape")))

        val left = reduceStudioCanvasState(
            state,
            StudioCanvasAction.AlignHorizontal("shape", StudioCanvasHorizontalAlignment.LEFT),
        )
        val center = reduceStudioCanvasState(
            state,
            StudioCanvasAction.AlignHorizontal("shape", StudioCanvasHorizontalAlignment.CENTER),
        )
        val right = reduceStudioCanvasState(
            state,
            StudioCanvasAction.AlignHorizontal("shape", StudioCanvasHorizontalAlignment.RIGHT),
        )

        assertEquals(0f, left.objects.single().xPx)
        assertEquals(400f, center.objects.single().xPx)
        assertEquals(800f, right.objects.single().xPx)
    }

    @Test
    fun verticalAlignmentUsesCanvasBounds() {
        val state = StudioCanvasState(1000, 800, listOf(obj("shape")))

        val top = reduceStudioCanvasState(
            state,
            StudioCanvasAction.AlignVertical("shape", StudioCanvasVerticalAlignment.TOP),
        )
        val center = reduceStudioCanvasState(
            state,
            StudioCanvasAction.AlignVertical("shape", StudioCanvasVerticalAlignment.CENTER),
        )
        val bottom = reduceStudioCanvasState(
            state,
            StudioCanvasAction.AlignVertical("shape", StudioCanvasVerticalAlignment.BOTTOM),
        )

        assertEquals(0f, top.objects.single().yPx)
        assertEquals(350f, center.objects.single().yPx)
        assertEquals(700f, bottom.objects.single().yPx)
    }

    @Test
    fun lockedObjectRejectsAlignment() {
        val locked = obj("locked", locked = true)
        val state = StudioCanvasState(1000, 800, listOf(locked))

        val horizontal = reduceStudioCanvasState(
            state,
            StudioCanvasAction.AlignHorizontal("locked", StudioCanvasHorizontalAlignment.CENTER),
        )
        val vertical = reduceStudioCanvasState(
            horizontal,
            StudioCanvasAction.AlignVertical("locked", StudioCanvasVerticalAlignment.BOTTOM),
        )

        assertEquals(locked, vertical.objects.single())
    }
}

package com.patsy.app.studio

import kotlin.test.Test
import kotlin.test.assertEquals

class StudioCanvasStackingTest {
    private fun obj(id: String, type: StudioLayerType = StudioLayerType.SHAPE) = StudioCanvasObject(
        id = id,
        type = type,
        label = id,
        xPx = 0f,
        yPx = 0f,
        widthPx = 100f,
        heightPx = 100f,
        locked = type == StudioLayerType.BACKGROUND,
    )

    @Test
    fun backgroundRemainsBottomWhenSendingEditableLayerBackward() {
        val state = StudioCanvasState(
            1080,
            1350,
            listOf(obj("background", StudioLayerType.BACKGROUND), obj("text", StudioLayerType.TEXT), obj("shape")),
        )

        val moved = reduceStudioCanvasState(state, StudioCanvasAction.SendBackward("text"))

        assertEquals(listOf("background", "text", "shape"), moved.objects.map { it.id })
    }

    @Test
    fun bringToFrontAndSendToBackPreserveBackgroundFloor() {
        val state = StudioCanvasState(
            1080,
            1350,
            listOf(
                obj("background", StudioLayerType.BACKGROUND),
                obj("text", StudioLayerType.TEXT),
                obj("photo", StudioLayerType.IMAGE),
                obj("shape"),
            ),
        )

        val front = reduceStudioCanvasState(state, StudioCanvasAction.BringToFront("text"))
        assertEquals(listOf("background", "photo", "shape", "text"), front.objects.map { it.id })

        val back = reduceStudioCanvasState(front, StudioCanvasAction.SendToBack("shape"))
        assertEquals(listOf("background", "shape", "photo", "text"), back.objects.map { it.id })
    }
}

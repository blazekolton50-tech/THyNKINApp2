package com.patsy.app.studio

import kotlin.test.Test
import kotlin.test.assertEquals

class StudioCanvasDuplicateTest {
    @Test
    fun duplicateCreatesOffsetCopyWithNewIdAndSelectsIt() {
        val original = StudioCanvasObject(
            id = "text-1",
            type = StudioLayerType.TEXT,
            label = "Headline",
            xPx = 100f,
            yPx = 200f,
            widthPx = 300f,
            heightPx = 80f,
            rotationDegrees = 12f,
            opacity = 0.75f,
        )
        val state = StudioCanvasState(1080, 1350, listOf(original), selectedObjectId = original.id)

        val duplicated = reduceStudioCanvasState(
            state,
            StudioCanvasAction.Duplicate(
                objectId = original.id,
                newObjectId = "text-2",
                offsetXPx = 24f,
                offsetYPx = 24f,
            ),
        )

        assertEquals(listOf("text-1", "text-2"), duplicated.objects.map { it.id })
        val copy = duplicated.objects.last()
        assertEquals(124f, copy.xPx)
        assertEquals(224f, copy.yPx)
        assertEquals(original.widthPx, copy.widthPx)
        assertEquals(original.heightPx, copy.heightPx)
        assertEquals(original.rotationDegrees, copy.rotationDegrees)
        assertEquals(original.opacity, copy.opacity)
        assertEquals("text-2", duplicated.selectedObjectId)
    }

    @Test
    fun duplicateRejectsExistingDestinationId() {
        val first = StudioCanvasObject("one", StudioLayerType.SHAPE, "One", 0f, 0f, 50f, 50f)
        val second = StudioCanvasObject("two", StudioLayerType.SHAPE, "Two", 60f, 60f, 50f, 50f)
        val state = StudioCanvasState(1080, 1350, listOf(first, second), selectedObjectId = "one")

        val unchanged = reduceStudioCanvasState(
            state,
            StudioCanvasAction.Duplicate("one", "two", 20f, 20f),
        )

        assertEquals(state, unchanged)
    }
}

package com.patsy.app.studio

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class StudioCanvasStateTest {
    private fun canvasObject(
        id: String,
        x: Float = 10f,
        y: Float = 20f,
        width: Float = 100f,
        height: Float = 80f,
        locked: Boolean = false,
        visible: Boolean = true,
    ) = StudioCanvasObject(
        id = id,
        type = StudioLayerType.IMAGE,
        label = id,
        xPx = x,
        yPx = y,
        widthPx = width,
        heightPx = height,
        locked = locked,
        visible = visible,
    )

    @Test
    fun selectingExistingObjectPreservesItsEditableTransformProperties() {
        val textObject = StudioCanvasObject(
            id = "text-1",
            type = StudioLayerType.TEXT,
            label = "Text - CREATE",
            xPx = 120f,
            yPx = 80f,
            widthPx = 300f,
            heightPx = 90f,
            rotationDegrees = -3f,
            opacity = 0.8f,
        )
        val state = StudioCanvasState(
            widthPx = 1080,
            heightPx = 1350,
            objects = listOf(textObject),
        )

        val selected = reduceStudioCanvasState(
            state,
            StudioCanvasAction.Select("text-1"),
        )

        assertEquals("text-1", selected.selectedObjectId)
        assertEquals(textObject, selected.objects.single())
    }

    @Test
    fun movementAppliesToUnlockedObjectsAndIsRejectedByLockedObjects() {
        val free = canvasObject("free")
        val locked = canvasObject("locked", x = 30f, y = 40f, width = 120f, height = 90f, locked = true)
        val state = StudioCanvasState(1080, 1350, listOf(free, locked))

        val movedFree = reduceStudioCanvasState(
            state,
            StudioCanvasAction.Move("free", deltaXPx = 5f, deltaYPx = 7f),
        )
        val attemptedLockedMove = reduceStudioCanvasState(
            movedFree,
            StudioCanvasAction.Move("locked", deltaXPx = 5f, deltaYPx = 7f),
        )

        assertEquals(15f, attemptedLockedMove.objects.first { it.id == "free" }.xPx)
        assertEquals(27f, attemptedLockedMove.objects.first { it.id == "free" }.yPx)
        assertEquals(30f, attemptedLockedMove.objects.first { it.id == "locked" }.xPx)
        assertEquals(40f, attemptedLockedMove.objects.first { it.id == "locked" }.yPx)
    }

    @Test
    fun resizeRotateAndOpacityUseTheSharedCanvasObjectModel() {
        val state = StudioCanvasState(1080, 1350, listOf(canvasObject("dog")))

        val resized = reduceStudioCanvasState(state, StudioCanvasAction.Resize("dog", 320f, 240f))
        val rotated = reduceStudioCanvasState(resized, StudioCanvasAction.Rotate("dog", 25f))
        val faded = reduceStudioCanvasState(rotated, StudioCanvasAction.SetOpacity("dog", 0.55f))

        val dog = faded.objects.single()
        assertEquals(320f, dog.widthPx)
        assertEquals(240f, dog.heightPx)
        assertEquals(25f, dog.rotationDegrees)
        assertEquals(0.55f, dog.opacity)
    }

    @Test
    fun lockedObjectsRejectResizeRotateOpacityAndDelete() {
        val locked = canvasObject("locked", locked = true)
        val state = StudioCanvasState(1080, 1350, listOf(locked), selectedObjectId = "locked")

        val resized = reduceStudioCanvasState(state, StudioCanvasAction.Resize("locked", 500f, 500f))
        val rotated = reduceStudioCanvasState(resized, StudioCanvasAction.Rotate("locked", 90f))
        val faded = reduceStudioCanvasState(rotated, StudioCanvasAction.SetOpacity("locked", 0.1f))
        val deleted = reduceStudioCanvasState(faded, StudioCanvasAction.Delete("locked"))

        assertEquals(listOf(locked), deleted.objects)
        assertEquals("locked", deleted.selectedObjectId)
    }

    @Test
    fun visibilityLockAndOneStepReorderPreserveStableIdentity() {
        val state = StudioCanvasState(
            1080,
            1350,
            listOf(
                canvasObject("background"),
                canvasObject("text"),
                canvasObject("photo"),
            ),
        )

        val hidden = reduceStudioCanvasState(state, StudioCanvasAction.SetVisible("text", false))
        val locked = reduceStudioCanvasState(hidden, StudioCanvasAction.SetLocked("photo", true))
        val raised = reduceStudioCanvasState(locked, StudioCanvasAction.BringForward("background"))

        assertFalse(raised.objects.first { it.id == "text" }.visible)
        assertTrue(raised.objects.first { it.id == "photo" }.locked)
        assertEquals(listOf("text", "background", "photo"), raised.objects.map { it.id })
    }

    @Test
    fun opacityAndResizeInputsAreNormalizedInsteadOfCreatingInvalidObjects() {
        val state = StudioCanvasState(1080, 1350, listOf(canvasObject("dog")))
        val transparent = reduceStudioCanvasState(state, StudioCanvasAction.SetOpacity("dog", -4f))
        val opaque = reduceStudioCanvasState(transparent, StudioCanvasAction.SetOpacity("dog", 4f))
        val tiny = reduceStudioCanvasState(opaque, StudioCanvasAction.Resize("dog", 0f, -10f))

        assertEquals(1f, tiny.objects.single().opacity)
        assertEquals(1f, tiny.objects.single().widthPx)
        assertEquals(1f, tiny.objects.single().heightPx)
    }

    @Test
    fun addingObjectAppendsAndSelectsUniqueIdentity() {
        val background = canvasObject("background", locked = true)
        val state = StudioCanvasState(1080, 1350, listOf(background))
        val text = StudioCanvasObject(
            id = "text-1",
            type = StudioLayerType.TEXT,
            label = "Text",
            xPx = 120f,
            yPx = 160f,
            widthPx = 320f,
            heightPx = 96f,
        )

        val added = reduceStudioCanvasState(state, StudioCanvasAction.AddObject(text))
        val duplicate = reduceStudioCanvasState(added, StudioCanvasAction.AddObject(text.copy(label = "Duplicate")))

        assertEquals(listOf("background", "text-1"), added.objects.map { it.id })
        assertEquals("text-1", added.selectedObjectId)
        assertEquals(added, duplicate)
    }

    @Test
    fun deletingSelectedUnlockedObjectClearsSelection() {
        val state = StudioCanvasState(
            1080,
            1350,
            listOf(canvasObject("dog")),
            selectedObjectId = "dog",
        )

        val deleted = reduceStudioCanvasState(state, StudioCanvasAction.Delete("dog"))

        assertTrue(deleted.objects.isEmpty())
        assertEquals(null, deleted.selectedObjectId)
    }
}

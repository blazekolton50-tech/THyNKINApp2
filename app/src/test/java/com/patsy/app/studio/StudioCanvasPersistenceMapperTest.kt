package com.patsy.app.studio

import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class StudioCanvasPersistenceMapperTest {
    private val projectA = "00000000-0000-0000-0000-000000000010"
    private val projectB = "00000000-0000-0000-0000-000000000020"

    @Test
    fun persistenceIdIsStableUuidWithoutReplacingLogicalEditorId() {
        val first = studioLayerPersistenceId(projectA, "background")
        val second = studioLayerPersistenceId(projectA, "background")
        val otherObject = studioLayerPersistenceId(projectA, "copy-1")
        val otherProject = studioLayerPersistenceId(projectB, "background")

        UUID.fromString(first)
        assertEquals(first, second)
        assertNotEquals(first, otherObject)
        assertNotEquals(first, otherProject)
    }

    @Test
    fun designCanvasMapsToEditableServerLayerRecordsDeterministically() {
        val state = StudioCanvasState(
            widthPx = 1080,
            heightPx = 1350,
            selectedObjectId = "title",
            objects = listOf(
                StudioCanvasObject(
                    id = "background",
                    type = StudioLayerType.BACKGROUND,
                    label = "Background",
                    xPx = 0f,
                    yPx = 0f,
                    widthPx = 1080f,
                    heightPx = 1350f,
                    locked = true,
                ),
                StudioCanvasObject(
                    id = "title",
                    type = StudioLayerType.TEXT,
                    label = "Title",
                    xPx = 120f,
                    yPx = 160f,
                    widthPx = 640f,
                    heightPx = 160f,
                    rotationDegrees = 12f,
                    opacity = 0.75f,
                    visible = false,
                ),
            ),
        )

        val mapped = studioCanvasPersistenceLayers(projectA, state)

        assertEquals(2, mapped.size)
        assertEquals("background", mapped[0].layerType)
        assertEquals("text", mapped[1].layerType)
        assertEquals(0, mapped[0].zIndex)
        assertEquals(1, mapped[1].zIndex)
        assertTrue(mapped[0].isLocked)
        assertTrue(mapped[1].isHidden)
        assertEquals(0.75, mapped[1].opacity, 0.0001)
        assertTrue(mapped[1].transformJson.contains("\"x\":120.0"))
        assertTrue(mapped[1].transformJson.contains("\"rotation\":12.0"))
        assertTrue(mapped[1].contentJson.contains("\"logical_id\":\"title\""))
        assertTrue(mapped[1].contentJson.contains("\"width_px\":640.0"))
        assertEquals(studioLayerPersistenceId(projectA, "title"), mapped[1].id)
    }

    @Test
    fun snapshotPreservesLogicalIdsAndSelectionForEditorRestore() {
        val state = StudioCanvasState(
            widthPx = 800,
            heightPx = 600,
            selectedObjectId = "copy-1",
            objects = listOf(
                StudioCanvasObject(
                    id = "copy-1",
                    type = StudioLayerType.SHAPE,
                    label = "Shape",
                    xPx = 5f,
                    yPx = 8f,
                    widthPx = 100f,
                    heightPx = 80f,
                ),
            ),
        )

        val snapshot = studioCanvasSnapshotJson(state)

        assertTrue(snapshot.contains("\"selected_object_id\":\"copy-1\""))
        assertTrue(snapshot.contains("\"logical_id\":\"copy-1\""))
        assertTrue(snapshot.contains("\"canvas_width_px\":800"))
        assertTrue(snapshot.contains("\"canvas_height_px\":600"))
    }
}

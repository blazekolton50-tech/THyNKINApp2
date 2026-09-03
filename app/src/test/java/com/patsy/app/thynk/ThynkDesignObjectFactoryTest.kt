package com.patsy.app.thynk

import com.patsy.app.studio.StudioLayerType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ThynkDesignObjectFactoryTest {
    @Test
    fun textAndElementToolsCreateDistinctCenteredNativeLayers() {
        val text = designCanvasObjectForTool("text", sequence = 3, canvasWidthPx = 1080, canvasHeightPx = 1350)
        val shape = designCanvasObjectForTool("elements", sequence = 4, canvasWidthPx = 1080, canvasHeightPx = 1350)

        requireNotNull(text)
        requireNotNull(shape)
        assertEquals("text-3", text.id)
        assertEquals(StudioLayerType.TEXT, text.type)
        assertEquals("shape-4", shape.id)
        assertEquals(StudioLayerType.SHAPE, shape.type)
        assertEquals((1080f - text.widthPx) / 2f, text.xPx)
        assertEquals((1350f - text.heightPx) / 2f, text.yPx)
        assertEquals((1080f - shape.widthPx) / 2f, shape.xPx)
        assertEquals((1350f - shape.heightPx) / 2f, shape.yPx)
    }

    @Test
    fun providerAndUnsupportedToolsDoNotCreateFakeLayers() {
        assertNull(designCanvasObjectForTool("ai", 1, 1080, 1350))
        assertNull(designCanvasObjectForTool("templates", 1, 1080, 1350))
    }
}

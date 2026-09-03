package com.patsy.app.thynk

import com.patsy.app.studio.StudioCanvasObject
import com.patsy.app.studio.StudioLayerType

internal fun designCanvasObjectForTool(
    toolId: String,
    sequence: Int,
    canvasWidthPx: Int,
    canvasHeightPx: Int,
): StudioCanvasObject? {
    val canvasWidth = canvasWidthPx.coerceAtLeast(1).toFloat()
    val canvasHeight = canvasHeightPx.coerceAtLeast(1).toFloat()
    val objectNumber = sequence.coerceAtLeast(1)

    return when (toolId) {
        "text" -> {
            val width = canvasWidth * 0.50f
            val height = canvasHeight * 0.10f
            StudioCanvasObject(
                id = "text-$objectNumber",
                type = StudioLayerType.TEXT,
                label = "New text",
                xPx = (canvasWidth - width) / 2f,
                yPx = (canvasHeight - height) / 2f,
                widthPx = width,
                heightPx = height,
            )
        }
        "elements" -> {
            val width = canvasWidth * 0.35f
            val height = canvasHeight * 0.22f
            StudioCanvasObject(
                id = "shape-$objectNumber",
                type = StudioLayerType.SHAPE,
                label = "Shape",
                xPx = (canvasWidth - width) / 2f,
                yPx = (canvasHeight - height) / 2f,
                widthPx = width,
                heightPx = height,
            )
        }
        else -> null
    }
}

package com.patsy.app.studio

import java.nio.charset.StandardCharsets
import java.util.UUID

/**
 * Maps the in-memory THyNK canvas into the server persistence contract without
 * replacing the logical editor object ids that the UI uses for selection and
 * editing.
 */
fun studioLayerPersistenceId(
    projectId: String,
    logicalObjectId: String,
): String = UUID.nameUUIDFromBytes(
    "$projectId\u0000$logicalObjectId".toByteArray(StandardCharsets.UTF_8),
).toString()

fun studioCanvasPersistenceLayers(
    projectId: String,
    state: StudioCanvasState,
): List<StudioLayerPersistenceRecord> = state.objects.mapIndexed { index, canvasObject ->
    StudioLayerPersistenceRecord(
        id = studioLayerPersistenceId(projectId, canvasObject.id),
        projectId = projectId,
        parentLayerId = null,
        layerType = canvasObject.type.name.lowercase(),
        name = canvasObject.label,
        zIndex = index,
        startMs = null,
        endMs = null,
        isLocked = canvasObject.locked,
        isHidden = !canvasObject.visible,
        opacity = canvasObject.opacity.toDouble(),
        transformJson = buildString {
            append('{')
            append("\"x\":").append(canvasObject.xPx)
            append(",\"y\":").append(canvasObject.yPx)
            append(",\"rotation\":").append(canvasObject.rotationDegrees)
            append('}')
        },
        cropJson = null,
        styleJson = "{}",
        effectsJson = "[]",
        contentJson = buildString {
            append('{')
            append("\"logical_id\":").append(jsonString(canvasObject.id))
            append(",\"width_px\":").append(canvasObject.widthPx)
            append(",\"height_px\":").append(canvasObject.heightPx)
            append('}')
        },
    )
}

fun studioCanvasSnapshotJson(state: StudioCanvasState): String = buildString {
    append('{')
    append("\"schema_version\":1")
    append(",\"canvas_width_px\":").append(state.widthPx)
    append(",\"canvas_height_px\":").append(state.heightPx)
    append(",\"selected_object_id\":")
    append(state.selectedObjectId?.let(::jsonString) ?: "null")
    append(",\"objects\":[")
    state.objects.forEachIndexed { index, canvasObject ->
        if (index > 0) append(',')
        append('{')
        append("\"logical_id\":").append(jsonString(canvasObject.id))
        append(",\"type\":").append(jsonString(canvasObject.type.name.lowercase()))
        append(",\"label\":").append(jsonString(canvasObject.label))
        append(",\"x_px\":").append(canvasObject.xPx)
        append(",\"y_px\":").append(canvasObject.yPx)
        append(",\"width_px\":").append(canvasObject.widthPx)
        append(",\"height_px\":").append(canvasObject.heightPx)
        append(",\"rotation_degrees\":").append(canvasObject.rotationDegrees)
        append(",\"opacity\":").append(canvasObject.opacity)
        append(",\"visible\":").append(canvasObject.visible)
        append(",\"locked\":").append(canvasObject.locked)
        append('}')
    }
    append("]}")
}

private fun jsonString(value: String): String = buildString {
    append('"')
    value.forEach { character ->
        when (character) {
            '"' -> append("\\\"")
            '\\' -> append("\\\\")
            '\b' -> append("\\b")
            '\u000C' -> append("\\f")
            '\n' -> append("\\n")
            '\r' -> append("\\r")
            '\t' -> append("\\t")
            else -> {
                if (character.code < 0x20) {
                    append("\\u")
                    append(character.code.toString(16).padStart(4, '0'))
                } else {
                    append(character)
                }
            }
        }
    }
    append('"')
}

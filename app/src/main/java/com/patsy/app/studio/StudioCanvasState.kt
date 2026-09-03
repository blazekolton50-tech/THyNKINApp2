package com.patsy.app.studio

data class StudioCanvasObject(
    val id: String,
    val type: StudioLayerType,
    val label: String,
    val xPx: Float,
    val yPx: Float,
    val widthPx: Float,
    val heightPx: Float,
    val rotationDegrees: Float = 0f,
    val opacity: Float = 1f,
    val visible: Boolean = true,
    val locked: Boolean = false,
)

data class StudioCanvasState(
    val widthPx: Int,
    val heightPx: Int,
    val objects: List<StudioCanvasObject> = emptyList(),
    val selectedObjectId: String? = null,
)

enum class StudioCanvasHorizontalAlignment {
    LEFT,
    CENTER,
    RIGHT,
}

enum class StudioCanvasVerticalAlignment {
    TOP,
    CENTER,
    BOTTOM,
}

sealed interface StudioCanvasAction {
    data class AddObject(val canvasObject: StudioCanvasObject) : StudioCanvasAction
    data class Select(val objectId: String?) : StudioCanvasAction
    data class Move(
        val objectId: String,
        val deltaXPx: Float,
        val deltaYPx: Float,
    ) : StudioCanvasAction
    data class Resize(
        val objectId: String,
        val widthPx: Float,
        val heightPx: Float,
    ) : StudioCanvasAction
    data class Rotate(
        val objectId: String,
        val rotationDegrees: Float,
    ) : StudioCanvasAction
    data class SetOpacity(
        val objectId: String,
        val opacity: Float,
    ) : StudioCanvasAction
    data class SetVisible(
        val objectId: String,
        val visible: Boolean,
    ) : StudioCanvasAction
    data class SetLocked(
        val objectId: String,
        val locked: Boolean,
    ) : StudioCanvasAction
    data class Duplicate(
        val objectId: String,
        val newObjectId: String,
        val offsetXPx: Float = 24f,
        val offsetYPx: Float = 24f,
    ) : StudioCanvasAction
    data class AlignHorizontal(
        val objectId: String,
        val alignment: StudioCanvasHorizontalAlignment,
    ) : StudioCanvasAction
    data class AlignVertical(
        val objectId: String,
        val alignment: StudioCanvasVerticalAlignment,
    ) : StudioCanvasAction
    data class BringForward(val objectId: String) : StudioCanvasAction
    data class SendBackward(val objectId: String) : StudioCanvasAction
    data class BringToFront(val objectId: String) : StudioCanvasAction
    data class SendToBack(val objectId: String) : StudioCanvasAction
    data class Delete(val objectId: String) : StudioCanvasAction
}

fun reduceStudioCanvasState(
    state: StudioCanvasState,
    action: StudioCanvasAction,
): StudioCanvasState = when (action) {
    is StudioCanvasAction.AddObject -> {
        if (state.objects.any { it.id == action.canvasObject.id }) {
            state
        } else {
            state.copy(
                objects = state.objects + action.canvasObject,
                selectedObjectId = action.canvasObject.id,
            )
        }
    }

    is StudioCanvasAction.Select -> state.copy(
        selectedObjectId = action.objectId?.takeIf { candidate ->
            state.objects.any { it.id == candidate }
        },
    )

    is StudioCanvasAction.Move -> state.updateUnlockedObject(action.objectId) { canvasObject ->
        canvasObject.copy(
            xPx = canvasObject.xPx + action.deltaXPx,
            yPx = canvasObject.yPx + action.deltaYPx,
        )
    }

    is StudioCanvasAction.Resize -> state.updateUnlockedObject(action.objectId) { canvasObject ->
        canvasObject.copy(
            widthPx = action.widthPx.coerceAtLeast(1f),
            heightPx = action.heightPx.coerceAtLeast(1f),
        )
    }

    is StudioCanvasAction.Rotate -> state.updateUnlockedObject(action.objectId) { canvasObject ->
        canvasObject.copy(rotationDegrees = action.rotationDegrees)
    }

    is StudioCanvasAction.SetOpacity -> state.updateUnlockedObject(action.objectId) { canvasObject ->
        canvasObject.copy(opacity = action.opacity.coerceIn(0f, 1f))
    }

    is StudioCanvasAction.SetVisible -> state.copy(
        objects = state.objects.map { canvasObject ->
            if (canvasObject.id == action.objectId) canvasObject.copy(visible = action.visible) else canvasObject
        },
    )

    is StudioCanvasAction.SetLocked -> state.copy(
        objects = state.objects.map { canvasObject ->
            if (canvasObject.id == action.objectId) canvasObject.copy(locked = action.locked) else canvasObject
        },
    )

    is StudioCanvasAction.Duplicate -> {
        if (action.newObjectId.isBlank() || state.objects.any { it.id == action.newObjectId }) {
            state
        } else {
            val source = state.objects.firstOrNull { it.id == action.objectId }
            if (source == null) {
                state
            } else {
                val duplicate = source.copy(
                    id = action.newObjectId,
                    xPx = source.xPx + action.offsetXPx,
                    yPx = source.yPx + action.offsetYPx,
                )
                state.copy(
                    objects = state.objects + duplicate,
                    selectedObjectId = duplicate.id,
                )
            }
        }
    }

    is StudioCanvasAction.AlignHorizontal -> state.updateUnlockedObject(action.objectId) { canvasObject ->
        val availableWidth = (state.widthPx.toFloat() - canvasObject.widthPx).coerceAtLeast(0f)
        canvasObject.copy(
            xPx = when (action.alignment) {
                StudioCanvasHorizontalAlignment.LEFT -> 0f
                StudioCanvasHorizontalAlignment.CENTER -> availableWidth / 2f
                StudioCanvasHorizontalAlignment.RIGHT -> availableWidth
            },
        )
    }

    is StudioCanvasAction.AlignVertical -> state.updateUnlockedObject(action.objectId) { canvasObject ->
        val availableHeight = (state.heightPx.toFloat() - canvasObject.heightPx).coerceAtLeast(0f)
        canvasObject.copy(
            yPx = when (action.alignment) {
                StudioCanvasVerticalAlignment.TOP -> 0f
                StudioCanvasVerticalAlignment.CENTER -> availableHeight / 2f
                StudioCanvasVerticalAlignment.BOTTOM -> availableHeight
            },
        )
    }

    is StudioCanvasAction.BringForward -> state.reorderOneStep(action.objectId, +1)
    is StudioCanvasAction.SendBackward -> state.reorderOneStep(action.objectId, -1)
    is StudioCanvasAction.BringToFront -> state.reorderToEdge(action.objectId, toFront = true)
    is StudioCanvasAction.SendToBack -> state.reorderToEdge(action.objectId, toFront = false)

    is StudioCanvasAction.Delete -> {
        val target = state.objects.firstOrNull { it.id == action.objectId }
        if (target == null || target.locked) {
            state
        } else {
            state.copy(
                objects = state.objects.filterNot { it.id == action.objectId },
                selectedObjectId = state.selectedObjectId.takeUnless { it == action.objectId },
            )
        }
    }
}

private inline fun StudioCanvasState.updateUnlockedObject(
    objectId: String,
    update: (StudioCanvasObject) -> StudioCanvasObject,
): StudioCanvasState = copy(
    objects = objects.map { canvasObject ->
        if (canvasObject.id != objectId || canvasObject.locked) canvasObject else update(canvasObject)
    },
)

private fun StudioCanvasState.editableFloorIndex(): Int =
    if (objects.firstOrNull()?.type == StudioLayerType.BACKGROUND) 1 else 0

private fun StudioCanvasState.reorderOneStep(
    objectId: String,
    delta: Int,
): StudioCanvasState {
    val fromIndex = objects.indexOfFirst { it.id == objectId }
    if (fromIndex < 0 || objects[fromIndex].type == StudioLayerType.BACKGROUND) return this
    val targetIndex = (fromIndex + delta).coerceIn(editableFloorIndex(), objects.lastIndex)
    if (targetIndex == fromIndex) return this

    val reordered = objects.toMutableList()
    val item = reordered.removeAt(fromIndex)
    reordered.add(targetIndex, item)
    return copy(objects = reordered)
}

private fun StudioCanvasState.reorderToEdge(
    objectId: String,
    toFront: Boolean,
): StudioCanvasState {
    val fromIndex = objects.indexOfFirst { it.id == objectId }
    if (fromIndex < 0 || objects[fromIndex].type == StudioLayerType.BACKGROUND) return this
    val targetIndex = if (toFront) objects.lastIndex else editableFloorIndex()
    if (targetIndex == fromIndex) return this

    val reordered = objects.toMutableList()
    val item = reordered.removeAt(fromIndex)
    reordered.add(targetIndex, item)
    return copy(objects = reordered)
}

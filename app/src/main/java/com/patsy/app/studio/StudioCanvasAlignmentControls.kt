package com.patsy.app.studio

data class StudioCanvasAlignmentControl(
    val label: String,
    val action: StudioCanvasAction,
)

fun studioCanvasAlignmentControls(
    selected: StudioCanvasObject?,
): List<StudioCanvasAlignmentControl> {
    if (selected == null || selected.locked) return emptyList()

    return listOf(
        StudioCanvasAlignmentControl(
            "LEFT",
            StudioCanvasAction.AlignHorizontal(selected.id, StudioCanvasHorizontalAlignment.LEFT),
        ),
        StudioCanvasAlignmentControl(
            "H-CENTRE",
            StudioCanvasAction.AlignHorizontal(selected.id, StudioCanvasHorizontalAlignment.CENTER),
        ),
        StudioCanvasAlignmentControl(
            "RIGHT",
            StudioCanvasAction.AlignHorizontal(selected.id, StudioCanvasHorizontalAlignment.RIGHT),
        ),
        StudioCanvasAlignmentControl(
            "TOP",
            StudioCanvasAction.AlignVertical(selected.id, StudioCanvasVerticalAlignment.TOP),
        ),
        StudioCanvasAlignmentControl(
            "V-CENTRE",
            StudioCanvasAction.AlignVertical(selected.id, StudioCanvasVerticalAlignment.CENTER),
        ),
        StudioCanvasAlignmentControl(
            "BOTTOM",
            StudioCanvasAction.AlignVertical(selected.id, StudioCanvasVerticalAlignment.BOTTOM),
        ),
    )
}

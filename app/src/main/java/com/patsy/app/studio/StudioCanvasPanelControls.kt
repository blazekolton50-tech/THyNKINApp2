package com.patsy.app.studio

data class StudioCanvasPanelControls(
    val quick: List<StudioCanvasQuickEditControl> = emptyList(),
    val size: List<StudioCanvasQuickEditControl> = emptyList(),
    val alignment: List<StudioCanvasAlignmentControl> = emptyList(),
)

fun studioCanvasPanelControls(
    panelId: String,
    selected: StudioCanvasObject?,
): StudioCanvasPanelControls {
    if (selected == null || selected.locked) return StudioCanvasPanelControls()

    return when (panelId) {
        "position" -> StudioCanvasPanelControls(
            quick = studioCanvasQuickEditControls(selected, StudioCanvasQuickEditGroup.POSITION),
            size = studioCanvasSizeQuickEditControls(selected),
            alignment = studioCanvasAlignmentControls(selected),
        )

        "opacity" -> StudioCanvasPanelControls(
            quick = studioCanvasQuickEditControls(selected, StudioCanvasQuickEditGroup.OPACITY),
        )

        else -> StudioCanvasPanelControls()
    }
}

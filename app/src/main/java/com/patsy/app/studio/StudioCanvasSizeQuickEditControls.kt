package com.patsy.app.studio

fun studioCanvasSizeQuickEditControls(
    selected: StudioCanvasObject?,
): List<StudioCanvasQuickEditControl> {
    if (selected == null || selected.locked) return emptyList()

    return listOf(
        StudioCanvasQuickEditControl(
            "W -10",
            StudioCanvasAction.Resize(selected.id, selected.widthPx - 10f, selected.heightPx),
        ),
        StudioCanvasQuickEditControl(
            "W +10",
            StudioCanvasAction.Resize(selected.id, selected.widthPx + 10f, selected.heightPx),
        ),
        StudioCanvasQuickEditControl(
            "H -10",
            StudioCanvasAction.Resize(selected.id, selected.widthPx, selected.heightPx - 10f),
        ),
        StudioCanvasQuickEditControl(
            "H +10",
            StudioCanvasAction.Resize(selected.id, selected.widthPx, selected.heightPx + 10f),
        ),
    )
}

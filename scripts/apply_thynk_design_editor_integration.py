from pathlib import Path

HOST = Path("app/src/main/java/com/patsy/app/thynk/ThynkStudioScreen.kt")
DESIGN = Path("app/src/main/java/com/patsy/app/thynk/ThynkDesignEditorScreen.kt")

OLD_BACK = '''                    is ThynkRoute.Editor -> ThynkRoute.Category(ThynkStudioCatalog.categories.first { it.id == "video" })'''
NEW_BACK = '''                    is ThynkRoute.Editor -> {
                        val categoryId = editorDestinationForPage((route as ThynkRoute.Editor).pageId)?.categoryId
                        categoryId?.let { id ->
                            ThynkRoute.Category(ThynkStudioCatalog.categories.first { it.id == id })
                        } ?: ThynkRoute.Hub
                    }'''

OLD_RENDER = '''                is ThynkRoute.Editor -> ThynkVideoEditorScreen()'''
NEW_RENDER = '''                is ThynkRoute.Editor -> when (editorDestinationForPage(current.pageId)?.kind) {
                    ThynkEditorKind.DESIGN -> ThynkDesignEditorScreen()
                    ThynkEditorKind.VIDEO -> ThynkVideoEditorScreen()
                    null -> InfoPanel("EDITOR UNAVAILABLE", "This editor route is not configured.")
                }'''

OLD_WIDE_DUPLICATE_ANCHOR = '''                        },
                        modifier = Modifier.widthIn(min = 260.dp, max = 310.dp).fillMaxHeight(),'''
NEW_WIDE_DUPLICATE_ANCHOR = '''                        },
                        onDuplicateObject = { objectId ->
                            canvasState = reduceStudioCanvasState(
                                canvasState,
                                StudioCanvasAction.Duplicate(
                                    objectId = objectId,
                                    newObjectId = "copy-${nextObjectSequence}",
                                ),
                            )
                            nextObjectSequence += 1
                        },
                        modifier = Modifier.widthIn(min = 260.dp, max = 310.dp).fillMaxHeight(),'''

OLD_NARROW_DUPLICATE_ANCHOR = '''                        },
                        modifier = Modifier.fillMaxWidth().height(116.dp),'''
NEW_NARROW_DUPLICATE_ANCHOR = '''                        },
                        onDuplicateObject = { objectId ->
                            canvasState = reduceStudioCanvasState(
                                canvasState,
                                StudioCanvasAction.Duplicate(
                                    objectId = objectId,
                                    newObjectId = "copy-${nextObjectSequence}",
                                ),
                            )
                            nextObjectSequence += 1
                        },
                        modifier = Modifier.fillMaxWidth().height(116.dp),'''

OLD_CONTEXT_SIGNATURE = '''    onStateChange: (StudioCanvasState) -> Unit,
    onAddToolObject: (String) -> Unit,
    modifier: Modifier,'''
NEW_CONTEXT_SIGNATURE = '''    onStateChange: (StudioCanvasState) -> Unit,
    onAddToolObject: (String) -> Unit,
    onDuplicateObject: (String) -> Unit,
    modifier: Modifier,'''

OLD_LAYERS = '''                    state.objects.asReversed().take(5).forEach { layer ->
                        Row(
                            Modifier.fillMaxWidth().clickable {
                                onStateChange(reduceStudioCanvasState(state, StudioCanvasAction.Select(layer.id)))
                            }.padding(vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(layer.label, color = FinalWhite, fontSize = 10.sp, modifier = Modifier.weight(1f))
                            Text(if (layer.visible) "SHOW" else "HIDE", color = FinalMuted, fontSize = 8.sp)
                            Text(if (layer.locked) "  LOCK" else "  OPEN", color = FinalMuted, fontSize = 8.sp)
                        }
                    }'''
NEW_LAYERS = '''                    state.objects.asReversed().take(5).forEach { layer ->
                        Column(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                            Row(
                                Modifier.fillMaxWidth().clickable {
                                    onStateChange(reduceStudioCanvasState(state, StudioCanvasAction.Select(layer.id)))
                                },
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(layer.label, color = FinalWhite, fontSize = 10.sp, modifier = Modifier.weight(1f))
                                Text(if (layer.visible) "VISIBLE" else "HIDDEN", color = FinalMuted, fontSize = 8.sp)
                                Text(if (layer.locked) "  LOCKED" else "  EDIT", color = FinalMuted, fontSize = 8.sp)
                            }
                            if (layer.type != StudioLayerType.BACKGROUND) {
                                Row(
                                    Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(top = 4.dp),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                ) {
                                    DesignMiniAction(if (layer.visible) "HIDE" else "SHOW") {
                                        onStateChange(reduceStudioCanvasState(state, StudioCanvasAction.SetVisible(layer.id, !layer.visible)))
                                    }
                                    DesignMiniAction(if (layer.locked) "UNLOCK" else "LOCK") {
                                        onStateChange(reduceStudioCanvasState(state, StudioCanvasAction.SetLocked(layer.id, !layer.locked)))
                                    }
                                    DesignMiniAction("FORWARD") {
                                        onStateChange(reduceStudioCanvasState(state, StudioCanvasAction.BringForward(layer.id)))
                                    }
                                    DesignMiniAction("BACK") {
                                        onStateChange(reduceStudioCanvasState(state, StudioCanvasAction.SendBackward(layer.id)))
                                    }
                                    DesignMiniAction("FRONT") {
                                        onStateChange(reduceStudioCanvasState(state, StudioCanvasAction.BringToFront(layer.id)))
                                    }
                                    DesignMiniAction("BACKMOST") {
                                        onStateChange(reduceStudioCanvasState(state, StudioCanvasAction.SendToBack(layer.id)))
                                    }
                                    DesignMiniAction("DUPLICATE") { onDuplicateObject(layer.id) }
                                    DesignMiniAction("DELETE") {
                                        onStateChange(reduceStudioCanvasState(state, StudioCanvasAction.Delete(layer.id)))
                                    }
                                }
                            }
                        }
                    }'''

OLD_CONTROLS_IMPORT = '''import com.patsy.app.studio.reduceStudioCanvasState'''
INTERMEDIATE_CONTROLS_IMPORT = '''import com.patsy.app.studio.reduceStudioCanvasState
import com.patsy.app.studio.studioCanvasAlignmentControls'''
NEW_CONTROLS_IMPORT = '''import com.patsy.app.studio.reduceStudioCanvasState
import com.patsy.app.studio.studioCanvasPanelControls'''

OLD_POSITION_PANEL = '''            "position", "opacity" -> {
                if (selected == null) {
                    Text("Select an object to edit position, size, rotation or opacity.", color = FinalMuted, fontSize = 9.sp)
                } else {
                    Text("X ${selected.xPx.toInt()}   Y ${selected.yPx.toInt()}", color = FinalWhite, fontSize = 9.sp)
                    Text("W ${selected.widthPx.toInt()}   H ${selected.heightPx.toInt()}", color = FinalWhite, fontSize = 9.sp)
                    Text(
                        "Rotate ${selected.rotationDegrees.toInt()}°   Opacity ${(selected.opacity * 100).toInt()}%",
                        color = FinalWhite,
                        fontSize = 9.sp,
                    )
                }
            }'''
INTERMEDIATE_POSITION_PANEL = '''            "position", "opacity" -> {
                if (selected == null) {
                    Text("Select an object to edit position, size, rotation or opacity.", color = FinalMuted, fontSize = 9.sp)
                } else {
                    Text("X ${selected.xPx.toInt()}   Y ${selected.yPx.toInt()}", color = FinalWhite, fontSize = 9.sp)
                    Text("W ${selected.widthPx.toInt()}   H ${selected.heightPx.toInt()}", color = FinalWhite, fontSize = 9.sp)
                    Text(
                        "Rotate ${selected.rotationDegrees.toInt()}°   Opacity ${(selected.opacity * 100).toInt()}%",
                        color = FinalWhite,
                        fontSize = 9.sp,
                    )
                    if (selected.locked) {
                        Text("Unlock this layer to align it.", color = FinalMuted, fontSize = 8.sp, modifier = Modifier.padding(top = 6.dp))
                    } else {
                        Text("ALIGN TO CANVAS", color = FinalMuted, fontSize = 8.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
                        Row(
                            Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(top = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            studioCanvasAlignmentControls(selected).forEach { control ->
                                DesignMiniAction(control.label) {
                                    onStateChange(reduceStudioCanvasState(state, control.action))
                                }
                            }
                        }
                    }
                }
            }'''
PRE_SIZE_POSITION_PANEL = '''            "position", "opacity" -> {
                if (selected == null) {
                    Text("Select an object to edit position, size, rotation or opacity.", color = FinalMuted, fontSize = 9.sp)
                } else {
                    Text("X ${selected.xPx.toInt()}   Y ${selected.yPx.toInt()}", color = FinalWhite, fontSize = 9.sp)
                    Text("W ${selected.widthPx.toInt()}   H ${selected.heightPx.toInt()}", color = FinalWhite, fontSize = 9.sp)
                    Text(
                        "Rotate ${selected.rotationDegrees.toInt()}°   Opacity ${(selected.opacity * 100).toInt()}%",
                        color = FinalWhite,
                        fontSize = 9.sp,
                    )
                    val panelControls = studioCanvasPanelControls(panelId, selected)
                    if (selected.locked) {
                        Text("Unlock this layer to edit it.", color = FinalMuted, fontSize = 8.sp, modifier = Modifier.padding(top = 6.dp))
                    } else {
                        if (panelControls.quick.isNotEmpty()) {
                            Text(
                                if (panelId == "position") "POSITION & ROTATION" else "OPACITY",
                                color = FinalMuted,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 8.dp),
                            )
                            Row(
                                Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(top = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                            ) {
                                panelControls.quick.forEach { control ->
                                    DesignMiniAction(control.label) {
                                        onStateChange(reduceStudioCanvasState(state, control.action))
                                    }
                                }
                            }
                        }
                        if (panelControls.alignment.isNotEmpty()) {
                            Text("ALIGN TO CANVAS", color = FinalMuted, fontSize = 8.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
                            Row(
                                Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(top = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                            ) {
                                panelControls.alignment.forEach { control ->
                                    DesignMiniAction(control.label) {
                                        onStateChange(reduceStudioCanvasState(state, control.action))
                                    }
                                }
                            }
                        }
                    }
                }
            }'''
NEW_POSITION_PANEL = '''            "position", "opacity" -> {
                if (selected == null) {
                    Text("Select an object to edit position, size, rotation or opacity.", color = FinalMuted, fontSize = 9.sp)
                } else {
                    Text("X ${selected.xPx.toInt()}   Y ${selected.yPx.toInt()}", color = FinalWhite, fontSize = 9.sp)
                    Text("W ${selected.widthPx.toInt()}   H ${selected.heightPx.toInt()}", color = FinalWhite, fontSize = 9.sp)
                    Text(
                        "Rotate ${selected.rotationDegrees.toInt()}°   Opacity ${(selected.opacity * 100).toInt()}%",
                        color = FinalWhite,
                        fontSize = 9.sp,
                    )
                    val panelControls = studioCanvasPanelControls(panelId, selected)
                    if (selected.locked) {
                        Text("Unlock this layer to edit it.", color = FinalMuted, fontSize = 8.sp, modifier = Modifier.padding(top = 6.dp))
                    } else {
                        if (panelControls.quick.isNotEmpty()) {
                            Text(
                                if (panelId == "position") "POSITION & ROTATION" else "OPACITY",
                                color = FinalMuted,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 8.dp),
                            )
                            Row(
                                Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(top = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                            ) {
                                panelControls.quick.forEach { control ->
                                    DesignMiniAction(control.label) {
                                        onStateChange(reduceStudioCanvasState(state, control.action))
                                    }
                                }
                            }
                        }
                        if (panelControls.size.isNotEmpty()) {
                            Text("SIZE", color = FinalMuted, fontSize = 8.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
                            Row(
                                Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(top = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                            ) {
                                panelControls.size.forEach { control ->
                                    DesignMiniAction(control.label) {
                                        onStateChange(reduceStudioCanvasState(state, control.action))
                                    }
                                }
                            }
                        }
                        if (panelControls.alignment.isNotEmpty()) {
                            Text("ALIGN TO CANVAS", color = FinalMuted, fontSize = 8.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
                            Row(
                                Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(top = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                            ) {
                                panelControls.alignment.forEach { control ->
                                    DesignMiniAction(control.label) {
                                        onStateChange(reduceStudioCanvasState(state, control.action))
                                    }
                                }
                            }
                        }
                    }
                }
            }'''

OLD_PANEL_ACTION = '''@Composable
private fun DesignPanelAction(label: String, onClick: () -> Unit) {'''
NEW_PANEL_ACTION = '''@Composable
private fun DesignMiniAction(label: String, onClick: () -> Unit) {
    Box(
        Modifier.border(1.dp, DesignBorder, RoundedCornerShape(7.dp))
            .clip(RoundedCornerShape(7.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 7.dp, vertical = 5.dp),
    ) {
        Text(label, color = FinalMuted, fontSize = 7.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun DesignPanelAction(label: String, onClick: () -> Unit) {'''


def replace_once(source: str, old: str, new: str, label: str) -> str:
    if new in source:
        return source
    if old not in source:
        raise RuntimeError(f"Expected {label} anchor was not found; refusing to guess at host wiring")
    return source.replace(old, new, 1)


def replace_any(source: str, old_candidates: list[str], new: str, label: str) -> str:
    if new in source:
        return source
    for old in old_candidates:
        if old in source:
            return source.replace(old, new, 1)
    raise RuntimeError(f"Expected {label} anchor was not found; refusing to guess at host wiring")


def main() -> None:
    host_source = HOST.read_text(encoding="utf-8")
    host_updated = replace_once(host_source, OLD_BACK, NEW_BACK, "editor Back ownership")
    host_updated = replace_once(host_updated, OLD_RENDER, NEW_RENDER, "editor render dispatch")
    if host_updated != host_source:
        HOST.write_text(host_updated, encoding="utf-8")

    design_source = DESIGN.read_text(encoding="utf-8")
    design_updated = replace_once(design_source, OLD_WIDE_DUPLICATE_ANCHOR, NEW_WIDE_DUPLICATE_ANCHOR, "wide Design duplicate callback")
    design_updated = replace_once(design_updated, OLD_NARROW_DUPLICATE_ANCHOR, NEW_NARROW_DUPLICATE_ANCHOR, "narrow Design duplicate callback")
    design_updated = replace_once(design_updated, OLD_CONTEXT_SIGNATURE, NEW_CONTEXT_SIGNATURE, "Design context callback signature")
    design_updated = replace_once(design_updated, OLD_LAYERS, NEW_LAYERS, "Design Layers actions")
    design_updated = replace_any(
        design_updated,
        [INTERMEDIATE_CONTROLS_IMPORT, OLD_CONTROLS_IMPORT],
        NEW_CONTROLS_IMPORT,
        "Design panel controls import",
    )
    design_updated = replace_any(
        design_updated,
        [PRE_SIZE_POSITION_PANEL, INTERMEDIATE_POSITION_PANEL, OLD_POSITION_PANEL],
        NEW_POSITION_PANEL,
        "Design Position, Size and Opacity controls",
    )
    design_updated = replace_once(design_updated, OLD_PANEL_ACTION, NEW_PANEL_ACTION, "Design mini layer action")
    if design_updated != design_source:
        DESIGN.write_text(design_updated, encoding="utf-8")

    if host_updated != host_source or design_updated != design_source:
        print("THyNK Design host and native layer controls integration applied")
    else:
        print("THyNK Design host and native layer controls integration already applied")


if __name__ == "__main__":
    main()

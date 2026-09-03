package com.patsy.app.thynk

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patsy.app.studio.StudioCanvasAction
import com.patsy.app.studio.StudioCanvasObject
import com.patsy.app.studio.StudioCanvasState
import com.patsy.app.studio.StudioLayerType
import com.patsy.app.studio.StudioMode
import com.patsy.app.studio.StudioToolCatalog
import com.patsy.app.studio.reduceStudioCanvasState
import com.patsy.app.studio.studioCanvasPanelControls
import com.patsy.app.ui.finaldesign.FinalCard
import com.patsy.app.ui.finaldesign.FinalCharcoal
import com.patsy.app.ui.finaldesign.FinalMuted
import com.patsy.app.ui.finaldesign.FinalWhite

private val DesignWorkspace = Color(0xFFF1F1F3)
private val DesignCanvas = Color.White
private val DesignBorder = Color(0xFF34343A)
private val DesignAccent = Color(0xFFFF4C98)

private val designToolIds = setOf(
    "templates",
    "media",
    "ai",
    "text",
    "elements",
    "stickers",
    "draw",
    "frames",
    "layers",
    "guides",
    "position",
    "opacity",
)

@Composable
fun ThynkDesignEditorScreen() {
    var selectedPanel by remember { mutableStateOf("templates") }
    var nextObjectSequence by remember { mutableIntStateOf(1) }
    var canvasState by remember {
        mutableStateOf(
            StudioCanvasState(
                widthPx = 1080,
                heightPx = 1350,
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
                ),
            ),
        )
    }
    val tools = remember {
        StudioToolCatalog.forMode(StudioMode.IMAGE).filter { it.id in designToolIds }
    }

    Column(Modifier.fillMaxSize().background(FinalCharcoal)) {
        DesignProjectBar()
        BoxWithConstraints(Modifier.weight(1f).fillMaxWidth()) {
            val wide = maxWidth >= 820.dp
            if (wide) {
                Row(Modifier.fillMaxSize()) {
                    DesignToolRail(
                        tools = tools.map { it.id to it.label },
                        selectedPanel = selectedPanel,
                        vertical = true,
                        onSelect = { selectedPanel = it },
                    )
                    DesignCanvasViewport(
                        state = canvasState,
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        onSelect = { objectId ->
                            canvasState = reduceStudioCanvasState(canvasState, StudioCanvasAction.Select(objectId))
                        },
                    )
                    DesignContextPanel(
                        panelId = selectedPanel,
                        state = canvasState,
                        onStateChange = { canvasState = it },
                        onAddToolObject = { toolId ->
                            designCanvasObjectForTool(
                                toolId = toolId,
                                sequence = nextObjectSequence,
                                canvasWidthPx = canvasState.widthPx,
                                canvasHeightPx = canvasState.heightPx,
                            )?.let { canvasObject ->
                                canvasState = reduceStudioCanvasState(
                                    canvasState,
                                    StudioCanvasAction.AddObject(canvasObject),
                                )
                                nextObjectSequence += 1
                            }
                        },
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
                        modifier = Modifier.widthIn(min = 260.dp, max = 310.dp).fillMaxHeight(),
                    )
                }
            } else {
                Column(Modifier.fillMaxSize()) {
                    DesignCanvasViewport(
                        state = canvasState,
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        onSelect = { objectId ->
                            canvasState = reduceStudioCanvasState(canvasState, StudioCanvasAction.Select(objectId))
                        },
                    )
                    DesignContextPanel(
                        panelId = selectedPanel,
                        state = canvasState,
                        onStateChange = { canvasState = it },
                        onAddToolObject = { toolId ->
                            designCanvasObjectForTool(
                                toolId = toolId,
                                sequence = nextObjectSequence,
                                canvasWidthPx = canvasState.widthPx,
                                canvasHeightPx = canvasState.heightPx,
                            )?.let { canvasObject ->
                                canvasState = reduceStudioCanvasState(
                                    canvasState,
                                    StudioCanvasAction.AddObject(canvasObject),
                                )
                                nextObjectSequence += 1
                            }
                        },
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
                        modifier = Modifier.fillMaxWidth().height(116.dp),
                    )
                    DesignToolRail(
                        tools = tools.map { it.id to it.label },
                        selectedPanel = selectedPanel,
                        vertical = false,
                        onSelect = { selectedPanel = it },
                    )
                }
            }
        }
    }
}

@Composable
private fun DesignProjectBar() {
    Row(
        Modifier.fillMaxWidth().height(50.dp).background(Color(0xFF121214)).padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text("Untitled design", color = FinalWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text("Local draft · not synced", color = FinalMuted, fontSize = 9.sp)
        }
        Text("Undo", color = FinalMuted, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 8.dp))
        Text("Redo", color = FinalMuted, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 8.dp))
        Text("Export", color = FinalMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun DesignToolRail(
    tools: List<Pair<String, String>>,
    selectedPanel: String,
    vertical: Boolean,
    onSelect: (String) -> Unit,
) {
    if (vertical) {
        Column(
            Modifier.width(112.dp).fillMaxHeight().background(Color(0xFF121214)).padding(7.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            tools.forEach { (id, label) ->
                DesignToolButton(id, label, selectedPanel == id, onSelect)
            }
        }
    } else {
        Row(
            Modifier.fillMaxWidth().height(64.dp).background(Color(0xFF121214))
                .horizontalScroll(rememberScrollState()).padding(horizontal = 7.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            tools.forEach { (id, label) ->
                Box(Modifier.width(92.dp)) {
                    DesignToolButton(id, label, selectedPanel == id, onSelect)
                }
            }
        }
    }
}

@Composable
private fun DesignToolButton(
    id: String,
    label: String,
    selected: Boolean,
    onSelect: (String) -> Unit,
) {
    val border = if (selected) DesignAccent else DesignBorder
    Box(
        Modifier.fillMaxWidth().border(1.dp, border, RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp)).clickable { onSelect(id) }
            .background(if (selected) Color(0xFF242126) else Color.Transparent)
            .padding(horizontal = 8.dp, vertical = 9.dp),
    ) {
        Text(label, color = if (selected) FinalWhite else FinalMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun DesignCanvasViewport(
    state: StudioCanvasState,
    modifier: Modifier,
    onSelect: (String?) -> Unit,
) {
    Box(modifier.background(DesignWorkspace).padding(18.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "0     200     400     600     800     1080 px",
                color = Color(0xFF62626A),
                fontSize = 8.sp,
                modifier = Modifier.padding(bottom = 4.dp),
            )
            BoxWithConstraints(
                Modifier.fillMaxWidth(0.82f).aspectRatio(state.widthPx.toFloat() / state.heightPx.toFloat())
                    .background(DesignCanvas).border(1.dp, Color(0xFFC8C8CC))
                    .clickable { onSelect(null) },
            ) {
                val scale = maxWidth.value / state.widthPx.coerceAtLeast(1).toFloat()
                state.objects
                    .filter { it.visible && it.type != StudioLayerType.BACKGROUND }
                    .forEach { canvasObject ->
                        val selected = canvasObject.id == state.selectedObjectId
                        Box(
                            Modifier
                                .offset(
                                    x = (canvasObject.xPx * scale).dp,
                                    y = (canvasObject.yPx * scale).dp,
                                )
                                .width((canvasObject.widthPx * scale).dp)
                                .height((canvasObject.heightPx * scale).dp)
                                .graphicsLayer {
                                    rotationZ = canvasObject.rotationDegrees
                                    alpha = canvasObject.opacity
                                }
                                .border(
                                    width = if (selected) 2.dp else 1.dp,
                                    color = if (selected) DesignAccent else Color(0xFFB8B8C0),
                                    shape = RoundedCornerShape(6.dp),
                                )
                                .background(
                                    color = if (canvasObject.type == StudioLayerType.SHAPE) Color(0xFFE8D7FF) else Color.Transparent,
                                    shape = RoundedCornerShape(6.dp),
                                )
                                .clickable { onSelect(canvasObject.id) }
                                .padding(6.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                canvasObject.label,
                                color = Color(0xFF25252A),
                                fontSize = 10.sp,
                                fontWeight = if (canvasObject.type == StudioLayerType.TEXT) FontWeight.Bold else FontWeight.Medium,
                            )
                        }
                    }
                Text(
                    "${state.widthPx} × ${state.heightPx}",
                    color = Color(0xFF9A9AA0),
                    fontSize = 9.sp,
                    modifier = Modifier.align(Alignment.BottomEnd).padding(8.dp),
                )
                if (state.objects.none { it.type != StudioLayerType.BACKGROUND }) {
                    Column(Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Blank design", color = Color(0xFF333338), fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text("Choose Text or Elements to add a real layer", color = Color(0xFF77777E), fontSize = 10.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun DesignContextPanel(
    panelId: String,
    state: StudioCanvasState,
    onStateChange: (StudioCanvasState) -> Unit,
    onAddToolObject: (String) -> Unit,
    onDuplicateObject: (String) -> Unit,
    modifier: Modifier,
) {
    val selected = state.objects.firstOrNull { it.id == state.selectedObjectId }
    Column(modifier.background(FinalCard).border(1.dp, DesignBorder).padding(12.dp)) {
        Text(panelTitle(panelId), color = FinalWhite, fontSize = 11.sp, fontWeight = FontWeight.Black)
        Spacer(Modifier.height(6.dp))
        when (panelId) {
            "layers" -> {
                if (state.objects.isEmpty()) {
                    Text("No layers yet", color = FinalMuted, fontSize = 10.sp)
                } else {
                    state.objects.asReversed().take(5).forEach { layer ->
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
                    }
                }
            }
            "text" -> {
                Text("Create a native text layer on the editable canvas.", color = FinalMuted, fontSize = 9.sp)
                DesignPanelAction("ADD TEXT") { onAddToolObject("text") }
            }
            "elements" -> {
                Text("Create a native shape layer on the editable canvas.", color = FinalMuted, fontSize = 9.sp)
                DesignPanelAction("ADD SHAPE") { onAddToolObject("elements") }
            }
            "ai" -> {
                Text("AI Generate", color = FinalWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Text("NOT_CONFIGURED — no provider success is simulated.", color = Color(0xFFFFC46B), fontSize = 9.sp)
            }
            "guides" -> Text(
                "Rulers, guides, grid and snapping share this native canvas. Guide interaction is not enabled in this slice yet.",
                color = FinalMuted,
                fontSize = 9.sp,
            )
            "position", "opacity" -> {
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
            }
            else -> Text(panelCopy(panelId), color = FinalMuted, fontSize = 9.sp)
        }
    }
}

@Composable
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
private fun DesignPanelAction(label: String, onClick: () -> Unit) {
    Box(
        Modifier.fillMaxWidth().padding(top = 8.dp)
            .border(1.dp, DesignAccent, RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 9.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, color = FinalWhite, fontSize = 9.sp, fontWeight = FontWeight.Black)
    }
}

private fun panelTitle(panelId: String): String = when (panelId) {
    "media" -> "UPLOAD / PHOTOS"
    "ai" -> "AI GENERATE"
    "guides" -> "RULERS & GUIDES"
    else -> panelId.replaceFirstChar { it.uppercase() }
}

private fun panelCopy(panelId: String): String = when (panelId) {
    "templates" -> "Template browser will use verified local template assets; no placeholder template is applied here."
    "media" -> "Use verified device media import when this panel is wired to the shared Android picker."
    "text" -> "Text layers will be created through the shared native canvas reducer."
    "elements" -> "Shapes and elements will be real editable canvas layers."
    "stickers" -> "Only verified sticker/PawMoji assets will be surfaced here."
    "draw" -> "Drawing stays disabled until stroke persistence and undo/redo are implemented."
    "frames" -> "Frames will use native canvas layer/mask state rather than donor HTML."
    else -> "This panel is connected to the shared native editor shell; unfinished actions remain unavailable."
}

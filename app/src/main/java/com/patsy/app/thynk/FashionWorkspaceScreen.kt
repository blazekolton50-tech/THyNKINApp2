package com.patsy.app.thynk

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patsy.app.ui.finaldesign.FinalCard
import com.patsy.app.ui.finaldesign.FinalMuted
import com.patsy.app.ui.finaldesign.FinalWhite

@Composable
fun FashionWorkspaceScreen(initialItem: String) {
    var state by remember(initialItem) {
        mutableStateOf(
            FashionWorkspaceState.initial().copy(
                activeStage = FashionWorkspaceRouting.stageForItem(initialItem),
            ),
        )
    }
    val dispatch: (FashionWorkspaceAction) -> Unit = { action ->
        state = reduceFashionWorkspace(state, action)
    }
    val activeStage = FashionPipelineContract.stages.first { it.kind == state.activeStage }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Column(Modifier.padding(top = 16.dp)) {
                Text("THyNK-IT FASHION", color = FinalWhite, fontSize = 12.sp, fontWeight = FontWeight.Black)
                Text(activeStage.label, color = FinalWhite, fontSize = 28.sp, fontWeight = FontWeight.Black)
                Text(
                    "Native fashion workspace • state stays inside Kotlin/Compose",
                    color = FinalMuted,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }

        item {
            Row(
                Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                FashionPipelineContract.stages.forEach { stage ->
                    if (stage.kind == state.activeStage) {
                        Button(
                            onClick = { dispatch(FashionWorkspaceAction.OpenStage(stage.kind)) },
                            colors = ButtonDefaults.buttonColors(containerColor = FinalWhite, contentColor = Color.Black),
                        ) {
                            Text(stage.label, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        OutlinedButton(onClick = { dispatch(FashionWorkspaceAction.OpenStage(stage.kind)) }) {
                            Text(stage.label, color = FinalWhite, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        item {
            FashionPanel("CAPABILITIES") {
                Text(
                    activeStage.capabilityIds.joinToString(" • ") { it.replace('-', ' ') },
                    color = FinalMuted,
                    fontSize = 11.sp,
                    lineHeight = 16.sp,
                )
            }
        }

        when (state.activeStage) {
            FashionPipelineStageKind.PATTERN_CAD -> patternCadItems(state, dispatch)
            FashionPipelineStageKind.SKETCH_3D -> sketchItems(state, dispatch)
            FashionPipelineStageKind.LAYER_MIXER -> layerMixerItems(state, dispatch)
            FashionPipelineStageKind.MATERIALS_LAB -> materialsItems(state, dispatch)
            FashionPipelineStageKind.PRODUCTION -> productionItems(state, dispatch)
        }

        item { Spacer(Modifier.height(24.dp)) }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.patternCadItems(
    state: FashionWorkspaceState,
    dispatch: (FashionWorkspaceAction) -> Unit,
) {
    item {
        FashionPanel("SEAM ALLOWANCE") {
            Text("${formatOneDecimal(state.patternCad.seamAllowanceCm)} cm", color = FinalWhite, fontWeight = FontWeight.Bold)
            Slider(
                value = state.patternCad.seamAllowanceCm,
                onValueChange = { dispatch(FashionWorkspaceAction.SetSeamAllowanceCm(it)) },
                valueRange = 0f..5f,
            )
        }
    }
    item {
        FashionPanel("GRAIN / NOTCHES") {
            Text("Grain ${state.patternCad.grainDegrees.toInt()}°", color = FinalWhite)
            Slider(
                value = state.patternCad.grainDegrees,
                onValueChange = { dispatch(FashionWorkspaceAction.SetGrainDegrees(it)) },
                valueRange = 0f..359f,
            )
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(onClick = { dispatch(FashionWorkspaceAction.AddNotch) }) { Text("ADD NOTCH") }
                Text("${state.patternCad.notchCount} notches", color = FinalMuted, fontSize = 12.sp)
            }
        }
    }
    item {
        FashionSwitchRow(
            title = "Stitch links",
            checked = state.patternCad.stitchLinksEnabled,
            onCheckedChange = { dispatch(FashionWorkspaceAction.SetStitchLinksEnabled(it)) },
        )
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.sketchItems(
    state: FashionWorkspaceState,
    dispatch: (FashionWorkspaceAction) -> Unit,
) {
    item {
        FashionPanel("BRUSH OPACITY") {
            Text("${(state.sketch.brushOpacity * 100).toInt()}%", color = FinalWhite, fontWeight = FontWeight.Bold)
            Slider(
                value = state.sketch.brushOpacity,
                onValueChange = { dispatch(FashionWorkspaceAction.SetBrushOpacity(it)) },
                valueRange = 0f..1f,
            )
        }
    }
    item {
        FashionPanel("SYMMETRY") {
            Row(
                Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                FashionSymmetryMode.entries.forEach { mode ->
                    OutlinedButton(onClick = { dispatch(FashionWorkspaceAction.SetSymmetry(mode)) }) {
                        Text(
                            if (mode == state.sketch.symmetry) "✓ ${mode.name}" else mode.name,
                            color = FinalWhite,
                            fontSize = 11.sp,
                        )
                    }
                }
            }
        }
    }
    item {
        FashionPanel("VECTOR / PATTERN HANDOFF") {
            Text(
                "Vector nodes and convert-to-pattern-block stay on this native stage. No browser canvas is embedded.",
                color = FinalMuted,
                fontSize = 12.sp,
                lineHeight = 17.sp,
            )
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.layerMixerItems(
    state: FashionWorkspaceState,
    dispatch: (FashionWorkspaceAction) -> Unit,
) {
    item {
        FashionPanel("MASTER OPACITY") {
            Text("${(state.layerMixer.masterOpacity * 100).toInt()}%", color = FinalWhite, fontWeight = FontWeight.Bold)
            Slider(
                value = state.layerMixer.masterOpacity,
                onValueChange = { dispatch(FashionWorkspaceAction.SetMasterLayerOpacity(it)) },
                valueRange = 0f..1f,
            )
        }
    }
    items(state.layerMixer.layers, key = { it.id }) { layer ->
        FashionPanel(layer.label.uppercase()) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedButton(onClick = { dispatch(FashionWorkspaceAction.ToggleLayerVisibility(layer.id)) }) {
                    Text(if (layer.visible) "VISIBLE" else "HIDDEN", color = FinalWhite, fontSize = 10.sp)
                }
                OutlinedButton(onClick = { dispatch(FashionWorkspaceAction.ToggleLayerLock(layer.id)) }) {
                    Text(if (layer.locked) "LOCKED" else "UNLOCKED", color = FinalWhite, fontSize = 10.sp)
                }
            }
            Text("Opacity ${(layer.opacity * 100).toInt()}%", color = FinalMuted, fontSize = 11.sp)
            Slider(
                value = layer.opacity,
                onValueChange = { dispatch(FashionWorkspaceAction.SetLayerOpacity(layer.id, it)) },
                valueRange = 0f..1f,
                enabled = !layer.locked,
            )
        }
    }
    item {
        FashionSwitchRow(
            title = "Tension diagnostics",
            checked = state.layerMixer.tensionDiagnosticsEnabled,
            onCheckedChange = { dispatch(FashionWorkspaceAction.SetTensionDiagnosticsEnabled(it)) },
        )
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.materialsItems(
    state: FashionWorkspaceState,
    dispatch: (FashionWorkspaceAction) -> Unit,
) {
    item {
        FashionPanel("FABRIC WEIGHT") {
            Text("${state.materials.weightGsm} gsm", color = FinalWhite, fontWeight = FontWeight.Bold)
            Slider(
                value = state.materials.weightGsm.toFloat(),
                onValueChange = { dispatch(FashionWorkspaceAction.SetMaterialWeightGsm(it.toInt())) },
                valueRange = 20f..1000f,
            )
        }
    }
    item {
        FashionPanel("DRAPE") {
            Text("${(state.materials.drape * 100).toInt()}%", color = FinalWhite)
            Slider(
                value = state.materials.drape,
                onValueChange = { dispatch(FashionWorkspaceAction.SetMaterialDrape(it)) },
                valueRange = 0f..1f,
            )
        }
    }
    item {
        FashionPanel("TEXTURE SCALE") {
            Text("${formatOneDecimal(state.materials.scale)}×", color = FinalWhite)
            Slider(
                value = state.materials.scale,
                onValueChange = { dispatch(FashionWorkspaceAction.SetMaterialScale(it)) },
                valueRange = 0.1f..4f,
            )
        }
    }
    item {
        FashionPanel("CUSTOM COLOUR") {
            OutlinedTextField(
                value = state.materials.customHex,
                onValueChange = { dispatch(FashionWorkspaceAction.SetMaterialHex(it)) },
                label = { Text("Hex colour") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.productionItems(
    state: FashionWorkspaceState,
    dispatch: (FashionWorkspaceAction) -> Unit,
) {
    item {
        FashionPanel("SIZE GRADING") {
            Row(
                Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                FashionPipelineContract.gradingRows.forEach { grading ->
                    OutlinedButton(onClick = { dispatch(FashionWorkspaceAction.SelectProductionSize(grading.size)) }) {
                        Text(
                            if (grading.size == state.production.selectedSize) "✓ ${grading.size}" else grading.size,
                            color = FinalWhite,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
            val grading = state.production.selectedGrading
            Text(
                "Bust ${grading.bustCm} cm • Waist ${grading.waistCm} cm • Hip ${grading.hipCm} cm",
                color = FinalWhite,
                fontSize = 12.sp,
            )
            Text(
                "Ease ${grading.easeMm} mm • Seam allowance ${formatOneDecimal(grading.seamAllowanceCm)} cm",
                color = FinalMuted,
                fontSize = 11.sp,
            )
        }
    }
    item {
        FashionSwitchRow(
            title = "Include hidden layers in export",
            checked = state.production.includeHiddenLayers,
            onCheckedChange = { dispatch(FashionWorkspaceAction.SetIncludeHiddenLayers(it)) },
        )
    }
    item {
        FashionPanel("PRODUCTION PROVIDERS") {
            ProviderLine("Patsy material search", state.production.materialSearch)
            ProviderLine("Multi-page PDF export", state.production.multiPagePdfExport)
            ProviderLine("Tech-pack export", state.production.techPackExport)
            Text(
                "These stay disabled until a verified provider/export pipeline is connected. No fake files or supplier results are generated.",
                color = FinalMuted,
                fontSize = 11.sp,
                lineHeight = 16.sp,
                modifier = Modifier.padding(top = 6.dp),
            )
        }
    }
}

@Composable
private fun FashionSwitchRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    FashionPanel(title.uppercase()) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(if (checked) "ON" else "OFF", color = FinalWhite, modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
            Switch(checked = checked, onCheckedChange = onCheckedChange)
        }
    }
}

@Composable
private fun FashionPanel(
    title: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        Modifier.fillMaxWidth()
            .border(1.dp, Color(0xFF3B3B45), RoundedCornerShape(18.dp))
            .background(FinalCard, RoundedCornerShape(18.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(title, color = FinalWhite, fontSize = 11.sp, fontWeight = FontWeight.Black)
        content()
    }
}

@Composable
private fun ProviderLine(label: String, state: FashionProviderState) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(label, color = FinalWhite, fontSize = 11.sp, modifier = Modifier.weight(1f))
        Text(state.name, color = Color(0xFFFFC46B), fontSize = 9.sp, fontWeight = FontWeight.Bold)
    }
}

private fun formatOneDecimal(value: Float): String = "%.1f".format(value)

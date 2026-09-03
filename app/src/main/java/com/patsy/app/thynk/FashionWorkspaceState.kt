package com.patsy.app.thynk

enum class FashionSymmetryMode {
    NONE,
    X,
    Y,
    RADIAL,
}

enum class FashionProviderState {
    NOT_CONFIGURED,
}

data class FashionPatternCadState(
    val seamAllowanceCm: Float = 1.0f,
    val notchCount: Int = 0,
    val grainDegrees: Float = 0f,
    val stitchLinksEnabled: Boolean = false,
)

data class FashionSketchState(
    val brushOpacity: Float = 1f,
    val symmetry: FashionSymmetryMode = FashionSymmetryMode.NONE,
)

data class FashionLayerState(
    val id: String,
    val label: String,
    val visible: Boolean = true,
    val locked: Boolean = false,
    val opacity: Float = 1f,
)

data class FashionLayerMixerState(
    val layers: List<FashionLayerState> = listOf(
        FashionLayerState("shell", "Garment shell"),
        FashionLayerState("details", "Construction details"),
        FashionLayerState("annotations", "Annotations"),
    ),
    val masterOpacity: Float = 1f,
    val tensionDiagnosticsEnabled: Boolean = false,
)

data class FashionMaterialsState(
    val weightGsm: Int = 180,
    val drape: Float = 0.5f,
    val scale: Float = 1f,
    val customHex: String = "#FFFFFF",
)

data class FashionProductionState(
    val selectedSize: String,
    val selectedGrading: FashionGradingRow,
    val includeHiddenLayers: Boolean = false,
    val materialSearch: FashionProviderState = FashionProviderState.NOT_CONFIGURED,
    val multiPagePdfExport: FashionProviderState = FashionProviderState.NOT_CONFIGURED,
    val techPackExport: FashionProviderState = FashionProviderState.NOT_CONFIGURED,
)

data class FashionWorkspaceState(
    val activeStage: FashionPipelineStageKind,
    val patternCad: FashionPatternCadState,
    val sketch: FashionSketchState,
    val layerMixer: FashionLayerMixerState,
    val materials: FashionMaterialsState,
    val production: FashionProductionState,
) {
    companion object {
        fun initial(): FashionWorkspaceState {
            val defaultGrading = FashionPipelineContract.gradingRows.first { it.size == "M" }
            return FashionWorkspaceState(
                activeStage = FashionPipelineStageKind.PATTERN_CAD,
                patternCad = FashionPatternCadState(),
                sketch = FashionSketchState(),
                layerMixer = FashionLayerMixerState(),
                materials = FashionMaterialsState(),
                production = FashionProductionState(
                    selectedSize = defaultGrading.size,
                    selectedGrading = defaultGrading,
                ),
            )
        }
    }
}

sealed interface FashionWorkspaceAction {
    data class OpenStage(val stage: FashionPipelineStageKind) : FashionWorkspaceAction
    data class SetSeamAllowanceCm(val value: Float) : FashionWorkspaceAction
    data object AddNotch : FashionWorkspaceAction
    data class SetGrainDegrees(val value: Float) : FashionWorkspaceAction
    data class SetStitchLinksEnabled(val enabled: Boolean) : FashionWorkspaceAction
    data class SetBrushOpacity(val value: Float) : FashionWorkspaceAction
    data class SetSymmetry(val mode: FashionSymmetryMode) : FashionWorkspaceAction
    data class ToggleLayerVisibility(val layerId: String) : FashionWorkspaceAction
    data class ToggleLayerLock(val layerId: String) : FashionWorkspaceAction
    data class SetLayerOpacity(val layerId: String, val value: Float) : FashionWorkspaceAction
    data class SetMasterLayerOpacity(val value: Float) : FashionWorkspaceAction
    data class SetTensionDiagnosticsEnabled(val enabled: Boolean) : FashionWorkspaceAction
    data class SetMaterialWeightGsm(val value: Int) : FashionWorkspaceAction
    data class SetMaterialDrape(val value: Float) : FashionWorkspaceAction
    data class SetMaterialScale(val value: Float) : FashionWorkspaceAction
    data class SetMaterialHex(val value: String) : FashionWorkspaceAction
    data class SelectProductionSize(val size: String) : FashionWorkspaceAction
    data class SetIncludeHiddenLayers(val enabled: Boolean) : FashionWorkspaceAction
}

fun reduceFashionWorkspace(
    state: FashionWorkspaceState,
    action: FashionWorkspaceAction,
): FashionWorkspaceState = when (action) {
    is FashionWorkspaceAction.OpenStage -> state.copy(activeStage = action.stage)

    is FashionWorkspaceAction.SetSeamAllowanceCm -> state.copy(
        patternCad = state.patternCad.copy(seamAllowanceCm = action.value.coerceIn(0f, 5f)),
    )

    FashionWorkspaceAction.AddNotch -> state.copy(
        patternCad = state.patternCad.copy(notchCount = (state.patternCad.notchCount + 1).coerceAtMost(64)),
    )

    is FashionWorkspaceAction.SetGrainDegrees -> state.copy(
        patternCad = state.patternCad.copy(grainDegrees = normalizeDegrees(action.value)),
    )

    is FashionWorkspaceAction.SetStitchLinksEnabled -> state.copy(
        patternCad = state.patternCad.copy(stitchLinksEnabled = action.enabled),
    )

    is FashionWorkspaceAction.SetBrushOpacity -> state.copy(
        sketch = state.sketch.copy(brushOpacity = action.value.coerceIn(0f, 1f)),
    )

    is FashionWorkspaceAction.SetSymmetry -> state.copy(
        sketch = state.sketch.copy(symmetry = action.mode),
    )

    is FashionWorkspaceAction.ToggleLayerVisibility -> state.copy(
        layerMixer = state.layerMixer.copy(
            layers = state.layerMixer.layers.map { layer ->
                if (layer.id == action.layerId) layer.copy(visible = !layer.visible) else layer
            },
        ),
    )

    is FashionWorkspaceAction.ToggleLayerLock -> state.copy(
        layerMixer = state.layerMixer.copy(
            layers = state.layerMixer.layers.map { layer ->
                if (layer.id == action.layerId) layer.copy(locked = !layer.locked) else layer
            },
        ),
    )

    is FashionWorkspaceAction.SetLayerOpacity -> state.copy(
        layerMixer = state.layerMixer.copy(
            layers = state.layerMixer.layers.map { layer ->
                if (layer.id == action.layerId) layer.copy(opacity = action.value.coerceIn(0f, 1f)) else layer
            },
        ),
    )

    is FashionWorkspaceAction.SetMasterLayerOpacity -> state.copy(
        layerMixer = state.layerMixer.copy(masterOpacity = action.value.coerceIn(0f, 1f)),
    )

    is FashionWorkspaceAction.SetTensionDiagnosticsEnabled -> state.copy(
        layerMixer = state.layerMixer.copy(tensionDiagnosticsEnabled = action.enabled),
    )

    is FashionWorkspaceAction.SetMaterialWeightGsm -> state.copy(
        materials = state.materials.copy(weightGsm = action.value.coerceIn(20, 1000)),
    )

    is FashionWorkspaceAction.SetMaterialDrape -> state.copy(
        materials = state.materials.copy(drape = action.value.coerceIn(0f, 1f)),
    )

    is FashionWorkspaceAction.SetMaterialScale -> state.copy(
        materials = state.materials.copy(scale = action.value.coerceIn(0.1f, 4f)),
    )

    is FashionWorkspaceAction.SetMaterialHex -> state.copy(
        materials = state.materials.copy(customHex = normalizeHex(action.value, state.materials.customHex)),
    )

    is FashionWorkspaceAction.SelectProductionSize -> {
        val grading = FashionPipelineContract.gradingRows.firstOrNull { it.size == action.size }
        if (grading == null) state else state.copy(
            production = state.production.copy(
                selectedSize = grading.size,
                selectedGrading = grading,
            ),
        )
    }

    is FashionWorkspaceAction.SetIncludeHiddenLayers -> state.copy(
        production = state.production.copy(includeHiddenLayers = action.enabled),
    )
}

private fun normalizeDegrees(value: Float): Float {
    val normalized = value % 360f
    return if (normalized < 0f) normalized + 360f else normalized
}

private fun normalizeHex(value: String, fallback: String): String {
    val candidate = value.trim().uppercase()
    val normalized = if (candidate.startsWith("#")) candidate else "#$candidate"
    return if (Regex("^#[0-9A-F]{6}$").matches(normalized)) normalized else fallback
}

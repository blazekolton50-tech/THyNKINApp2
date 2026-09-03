package com.patsy.app.thynk

enum class FashionPipelineStageKind {
    PATTERN_CAD,
    SKETCH_3D,
    LAYER_MIXER,
    MATERIALS_LAB,
    PRODUCTION,
}

data class FashionPipelineStage(
    val id: String,
    val label: String,
    val kind: FashionPipelineStageKind,
    val capabilityIds: List<String>,
)

data class FashionGradingRow(
    val size: String,
    val bustCm: Int,
    val waistCm: Int,
    val hipCm: Int,
    val easeMm: Int,
    val seamAllowanceCm: Float,
)

/**
 * Native contract distilled from the 2 September THyNK-IT Drive donor.
 *
 * The HTML donor remains reference material only. These values are safe, deterministic
 * product/model contracts for the Kotlin + Compose application; they do not imply that an
 * external material crawler, PDF compiler, or other provider is configured.
 */
object FashionPipelineContract {
    const val sourceDriveFileId = "14O4ekWjf9KlOHPPUrkvDpemJOMG2m1_6"

    val stages = listOf(
        FashionPipelineStage(
            id = "fashion-pattern-cad",
            label = "Pattern CAD",
            kind = FashionPipelineStageKind.PATTERN_CAD,
            capabilityIds = listOf(
                "pattern-slices",
                "strict-cad-grid",
                "seam-allowance",
                "notches",
                "grain",
                "scissors-cut",
                "stitch-link",
                "dart",
                "pleat",
            ),
        ),
        FashionPipelineStage(
            id = "fashion-sketch-3d",
            label = "3D Sketch",
            kind = FashionPipelineStageKind.SKETCH_3D,
            capabilityIds = listOf(
                "brush-opacity",
                "symmetry-x",
                "symmetry-y",
                "symmetry-radial",
                "vector-nodes",
                "convert-to-pattern-block",
            ),
        ),
        FashionPipelineStage(
            id = "fashion-layer-mixer",
            label = "Layer Mixer",
            kind = FashionPipelineStageKind.LAYER_MIXER,
            capabilityIds = listOf(
                "layer-visibility",
                "layer-lock",
                "layer-opacity",
                "tension-diagnostics",
                "live-hex",
                "master-opacity",
            ),
        ),
        FashionPipelineStage(
            id = "fashion-materials-lab",
            label = "Materials Lab",
            kind = FashionPipelineStageKind.MATERIALS_LAB,
            capabilityIds = listOf(
                "fabric-presets",
                "weight-gsm",
                "drape",
                "scale",
                "custom-hex",
                "simulation-preview",
            ),
        ),
        FashionPipelineStage(
            id = "fashion-production",
            label = "Production",
            kind = FashionPipelineStageKind.PRODUCTION,
            capabilityIds = listOf(
                "grading-xs-xl",
                "validation",
                "hidden-layer-export",
                "multi-page-pdf",
                "tech-pack",
                "patsy-material-search",
            ),
        ),
    )

    val gradingRows = listOf(
        FashionGradingRow("XS", bustCm = 78, waistCm = 60, hipCm = 86, easeMm = 12, seamAllowanceCm = 1.0f),
        FashionGradingRow("S", bustCm = 82, waistCm = 64, hipCm = 90, easeMm = 14, seamAllowanceCm = 1.0f),
        FashionGradingRow("M", bustCm = 86, waistCm = 68, hipCm = 94, easeMm = 16, seamAllowanceCm = 1.2f),
        FashionGradingRow("L", bustCm = 92, waistCm = 74, hipCm = 100, easeMm = 18, seamAllowanceCm = 1.2f),
        FashionGradingRow("XL", bustCm = 98, waistCm = 80, hipCm = 106, easeMm = 20, seamAllowanceCm = 1.5f),
    )

    fun stage(id: String): FashionPipelineStage? = stages.firstOrNull { it.id == id }
}

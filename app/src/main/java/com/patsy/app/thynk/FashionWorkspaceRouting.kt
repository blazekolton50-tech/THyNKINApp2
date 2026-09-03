package com.patsy.app.thynk

object FashionWorkspaceRouting {
    fun stageForItem(item: String): FashionPipelineStageKind = when (item.trim().uppercase()) {
        "PATTERN CAD" -> FashionPipelineStageKind.PATTERN_CAD
        "3D SKETCH" -> FashionPipelineStageKind.SKETCH_3D
        "LAYER MIXER" -> FashionPipelineStageKind.LAYER_MIXER
        "MATERIALS LAB" -> FashionPipelineStageKind.MATERIALS_LAB
        "PRODUCTION" -> FashionPipelineStageKind.PRODUCTION
        else -> FashionPipelineStageKind.PATTERN_CAD
    }
}

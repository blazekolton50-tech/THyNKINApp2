package com.patsy.app.thynk

import kotlin.test.Test
import kotlin.test.assertEquals

class FashionWorkspaceRoutingTest {
    @Test
    fun `fashion hub labels resolve to the exact native stage`() {
        assertEquals(FashionPipelineStageKind.PATTERN_CAD, FashionWorkspaceRouting.stageForItem("PATTERN CAD"))
        assertEquals(FashionPipelineStageKind.SKETCH_3D, FashionWorkspaceRouting.stageForItem("3D SKETCH"))
        assertEquals(FashionPipelineStageKind.LAYER_MIXER, FashionWorkspaceRouting.stageForItem("LAYER MIXER"))
        assertEquals(FashionPipelineStageKind.MATERIALS_LAB, FashionWorkspaceRouting.stageForItem("MATERIALS LAB"))
        assertEquals(FashionPipelineStageKind.PRODUCTION, FashionWorkspaceRouting.stageForItem("PRODUCTION"))
    }

    @Test
    fun `unknown fashion route fails closed to Pattern CAD`() {
        assertEquals(FashionPipelineStageKind.PATTERN_CAD, FashionWorkspaceRouting.stageForItem("UNKNOWN"))
    }
}

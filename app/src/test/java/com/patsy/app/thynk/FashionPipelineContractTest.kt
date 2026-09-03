package com.patsy.app.thynk

import kotlin.test.Test
import kotlin.test.assertEquals

class FashionPipelineContractTest {
    @Test
    fun `Drive fashion pipeline keeps exact five native stages`() {
        assertEquals("14O4ekWjf9KlOHPPUrkvDpemJOMG2m1_6", FashionPipelineContract.sourceDriveFileId)
        assertEquals(
            listOf(
                "fashion-pattern-cad",
                "fashion-sketch-3d",
                "fashion-layer-mixer",
                "fashion-materials-lab",
                "fashion-production",
            ),
            FashionPipelineContract.stages.map { it.id },
        )
    }

    @Test
    fun `Drive fashion grading table is preserved exactly`() {
        assertEquals(
            listOf(
                FashionGradingRow("XS", 78, 60, 86, 12, 1.0f),
                FashionGradingRow("S", 82, 64, 90, 14, 1.0f),
                FashionGradingRow("M", 86, 68, 94, 16, 1.2f),
                FashionGradingRow("L", 92, 74, 100, 18, 1.2f),
                FashionGradingRow("XL", 98, 80, 106, 20, 1.5f),
            ),
            FashionPipelineContract.gradingRows,
        )
    }

    @Test
    fun `production keeps truthful provider dependent capabilities`() {
        val production = FashionPipelineContract.stage("fashion-production")!!
        assertEquals(
            listOf(
                "grading-xs-xl",
                "validation",
                "hidden-layer-export",
                "multi-page-pdf",
                "tech-pack",
                "patsy-material-search",
            ),
            production.capabilityIds,
        )
    }
}

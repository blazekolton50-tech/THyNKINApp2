package com.patsy.app.thynk

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ThynkFashionDriveVisibilityContractTest {
    @Test
    fun `THyNK IT exposes Drive fashion pipeline as visible category`() {
        val fashion = assertNotNull(ThynkStudioCatalog.categories.firstOrNull { it.id == "fashion" })

        assertEquals("FASHION & TEXTILES", fashion.label)
        assertEquals(
            listOf("PATTERN CAD", "3D SKETCH", "LAYER MIXER", "MATERIALS LAB", "PRODUCTION"),
            fashion.items,
        )
    }

    @Test
    fun `visible fashion items correspond to native donor stages`() {
        assertEquals(
            listOf("Pattern CAD", "3D Sketch", "Layer Mixer", "Materials Lab", "Production"),
            FashionPipelineContract.stages.map { it.label },
        )
    }
}

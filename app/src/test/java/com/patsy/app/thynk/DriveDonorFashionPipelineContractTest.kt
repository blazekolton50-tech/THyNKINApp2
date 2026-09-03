package com.patsy.app.thynk

import org.junit.Assert.assertTrue
import org.junit.Test

class DriveDonorFashionPipelineContractTest {
    @Test
    fun fashionCollectionExposesAllFiveDrivePipelineStages() {
        val fashion = CreatorWorkspaceCatalog.workspaceConfig("fashion-collection")!!

        assertTrue("fashion-pattern-cad" in fashion.beginnerShortcutIds)
        assertTrue("fashion-sketch-3d" in fashion.beginnerShortcutIds)
        assertTrue("fashion-layer-mixer" in fashion.beginnerShortcutIds)
        assertTrue("fashion-materials-lab" in fashion.beginnerShortcutIds)
        assertTrue("fashion-production" in fashion.beginnerShortcutIds)
    }
}

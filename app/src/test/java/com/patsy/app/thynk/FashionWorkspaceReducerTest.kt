package com.patsy.app.thynk

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FashionWorkspaceReducerTest {
    @Test
    fun `workspace opens on Pattern CAD and follows the locked five stage order`() {
        var state = FashionWorkspaceState.initial()
        assertEquals(FashionPipelineStageKind.PATTERN_CAD, state.activeStage)

        FashionPipelineContract.stages.drop(1).forEach { stage ->
            state = reduceFashionWorkspace(state, FashionWorkspaceAction.OpenStage(stage.kind))
            assertEquals(stage.kind, state.activeStage)
        }
    }

    @Test
    fun `CAD sketch layer and material controls stay inside safe native ranges`() {
        var state = FashionWorkspaceState.initial()

        state = reduceFashionWorkspace(state, FashionWorkspaceAction.SetSeamAllowanceCm(8f))
        assertEquals(5f, state.patternCad.seamAllowanceCm)

        state = reduceFashionWorkspace(state, FashionWorkspaceAction.SetBrushOpacity(-2f))
        assertEquals(0f, state.sketch.brushOpacity)

        state = reduceFashionWorkspace(state, FashionWorkspaceAction.SetMasterLayerOpacity(4f))
        assertEquals(1f, state.layerMixer.masterOpacity)

        state = reduceFashionWorkspace(state, FashionWorkspaceAction.SetMaterialWeightGsm(5000))
        assertEquals(1000, state.materials.weightGsm)

        state = reduceFashionWorkspace(state, FashionWorkspaceAction.SetMaterialDrape(-1f))
        assertEquals(0f, state.materials.drape)
    }

    @Test
    fun `layer visibility lock and symmetry are real deterministic state`() {
        var state = FashionWorkspaceState.initial()
        state = reduceFashionWorkspace(state, FashionWorkspaceAction.ToggleLayerVisibility("shell"))
        state = reduceFashionWorkspace(state, FashionWorkspaceAction.ToggleLayerLock("shell"))
        state = reduceFashionWorkspace(state, FashionWorkspaceAction.SetSymmetry(FashionSymmetryMode.RADIAL))

        val shell = state.layerMixer.layers.first { it.id == "shell" }
        assertFalse(shell.visible)
        assertTrue(shell.locked)
        assertEquals(FashionSymmetryMode.RADIAL, state.sketch.symmetry)
    }

    @Test
    fun `production sizing resolves only from the locked grading table`() {
        var state = FashionWorkspaceState.initial()
        state = reduceFashionWorkspace(state, FashionWorkspaceAction.SelectProductionSize("XL"))

        assertEquals("XL", state.production.selectedSize)
        assertEquals(FashionPipelineContract.gradingRows.last(), state.production.selectedGrading)

        state = reduceFashionWorkspace(state, FashionWorkspaceAction.SelectProductionSize("XXL"))
        assertEquals("XL", state.production.selectedSize)
        assertEquals(FashionPipelineContract.gradingRows.last(), state.production.selectedGrading)
    }

    @Test
    fun `provider dependent production features remain not configured`() {
        val state = FashionWorkspaceState.initial()
        assertEquals(FashionProviderState.NOT_CONFIGURED, state.production.materialSearch)
        assertEquals(FashionProviderState.NOT_CONFIGURED, state.production.multiPagePdfExport)
        assertEquals(FashionProviderState.NOT_CONFIGURED, state.production.techPackExport)
    }
}

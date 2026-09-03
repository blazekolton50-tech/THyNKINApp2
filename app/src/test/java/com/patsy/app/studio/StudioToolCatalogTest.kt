package com.patsy.app.studio

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class StudioToolCatalogTest {
    @Test
    fun imageEditorIncludesTheSharedCreativeToolset() {
        val ids = StudioToolCatalog.forMode(StudioMode.IMAGE).map { it.id }.toSet()

        assertTrue("crop" in ids)
        assertTrue("cutout" in ids)
        assertTrue("resize" in ids)
        assertTrue("rotate" in ids)
        assertTrue("text" in ids)
        assertTrue("elements" in ids)
        assertTrue("layers" in ids)
        assertTrue("frames" in ids)
        assertTrue("guides" in ids)
        assertTrue("draw" in ids)
        assertTrue("filters" in ids)
        assertTrue("adjust" in ids)
        assertTrue("effects" in ids)
        assertTrue("export" in ids)
    }

    @Test
    fun videoEditorAddsTimelineSpecificTools() {
        val ids = StudioToolCatalog.forMode(StudioMode.VIDEO).map { it.id }.toSet()

        assertTrue("trim" in ids)
        assertTrue("split" in ids)
        assertTrue("speed" in ids)
        assertTrue("transitions" in ids)
        assertTrue("audio" in ids)
        assertTrue("captions" in ids)
        assertTrue("animate" in ids)
        assertTrue("export" in ids)
    }

    @Test
    fun stillImageModeDoesNotExposeVideoOnlyTimelineTools() {
        val ids = StudioToolCatalog.forMode(StudioMode.IMAGE).map { it.id }.toSet()

        assertFalse("trim" in ids)
        assertFalse("split" in ids)
        assertFalse("speed" in ids)
        assertFalse("transitions" in ids)
        assertFalse("captions" in ids)
    }

    @Test
    fun everyToolHasAUniqueStableId() {
        val all = StudioToolCatalog.all
        assertTrue(all.map { it.id }.distinct().size == all.size)
    }

    @Test
    fun destructiveOrProviderBackedActionsAreExplicitlyFlagged() {
        val byId = StudioToolCatalog.all.associateBy { it.id }

        assertTrue(byId.getValue("cutout").requiresProcessing)
        assertTrue(byId.getValue("ai").requiresProcessing)
        assertTrue(byId.getValue("export").requiresProcessing)
        assertFalse(byId.getValue("crop").requiresProcessing)
    }
}

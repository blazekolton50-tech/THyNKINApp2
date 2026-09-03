package com.patsy.app.thynk

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CreatorWorkspaceConfigTest {
    @Test
    fun fashionCollectionUsesAFreeformCreativeWorkspace() {
        val fashion = CreatorWorkspaceCatalog.workspaceConfig("fashion-collection")!!
        assertEquals(CreatorCanvasMode.FREEFORM, fashion.canvasMode)
        assertTrue("draw" in fashion.toolIds)
        assertTrue("elements" in fashion.toolIds)
        assertTrue("fashion-swatches" in fashion.beginnerShortcutIds)
        assertTrue("fashion-measurements" in fashion.beginnerShortcutIds)
        assertFalse("captions" in fashion.toolIds)
    }

    @Test
    fun magazineUsesSpreadAndEditorialControls() {
        val magazine = CreatorWorkspaceCatalog.workspaceConfig("publishing-magazine")!!
        assertEquals(CreatorCanvasMode.SPREAD, magazine.canvasMode)
        assertTrue("text" in magazine.toolIds)
        assertTrue("guides" in magazine.toolIds)
        assertTrue("publication-pages" in magazine.beginnerShortcutIds)
        assertTrue("publication-columns" in magazine.beginnerShortcutIds)
    }

    @Test
    fun officeCvUsesDocumentModeWithoutAnimationNoise() {
        val office = CreatorWorkspaceCatalog.workspaceConfig("office-cv")!!
        assertEquals(CreatorCanvasMode.DOCUMENT, office.canvasMode)
        assertTrue("text" in office.toolIds)
        assertTrue("office-sections" in office.beginnerShortcutIds)
        assertFalse("animate" in office.toolIds)
    }

    @Test
    fun artIllustrationPrioritizesDrawingAndLayers() {
        val art = CreatorWorkspaceCatalog.workspaceConfig("art-illustration")!!
        assertEquals(CreatorCanvasMode.FREEFORM, art.canvasMode)
        assertTrue("draw" in art.toolIds)
        assertTrue("layers" in art.toolIds)
        assertTrue("art-brushes" in art.beginnerShortcutIds)
    }

    @Test
    fun socialPostUsesTheExpectedSquareCanvasAndSimpleToolset() {
        val social = CreatorWorkspaceCatalog.workspaceConfig("social-instagram-post")!!
        assertEquals(CreatorCanvasMode.SOCIAL, social.canvasMode)
        assertEquals(1080, social.defaultWidthPx)
        assertEquals(1080, social.defaultHeightPx)
        assertTrue("text" in social.toolIds)
        assertTrue("stickers" in social.toolIds)
        assertFalse("trim" in social.toolIds)
    }
}

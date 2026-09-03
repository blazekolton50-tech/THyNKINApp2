package com.patsy.app.thynk

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ThynkDesignRoutingTest {
    @Test
    fun blankAndCustomHaveDedicatedEntryPoints() {
        assertEquals(DesignEntryPoint.Blank, designEntryForThynkItem("BLANK DESIGNS"))
        assertEquals(DesignEntryPoint.CustomSize, designEntryForThynkItem("CUSTOM SIZE"))
    }

    @Test
    fun designFormatsOpenTemplateBrowserWithStableKind() {
        assertEquals(DesignEntryPoint.Templates("posters"), designEntryForThynkItem("POSTERS"))
        assertEquals(DesignEntryPoint.Templates("flyers"), designEntryForThynkItem("FLYERS"))
        assertEquals(DesignEntryPoint.Templates("templates"), designEntryForThynkItem("TEMPLATES"))
    }

    @Test
    fun allLiveDesignCatalogueEntriesHaveAStableEntryPoint() {
        val designItems = ThynkStudioCatalog.categories.first { it.id == "design" }.items
        assertEquals(designItems.size, designItems.mapNotNull(::designEntryForThynkItem).size)
    }

    @Test
    fun nonDesignItemDoesNotCreateDesignRoute() {
        assertNull(designEntryForThynkItem("VIDEO EDITOR"))
    }
}

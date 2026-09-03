package com.patsy.app.thynk

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CreatorWorkspaceCatalogTest {
    @Test
    fun seedCatalogueCoversAdaptiveMusicAndVideoCreatorPaths() {
        assertNotNull(CreatorWorkspaceCatalog.projectType("fashion-collection"))
        assertNotNull(CreatorWorkspaceCatalog.projectType("publishing-magazine"))
        assertNotNull(CreatorWorkspaceCatalog.projectType("office-cv"))
        assertNotNull(CreatorWorkspaceCatalog.projectType("art-illustration"))
        assertNotNull(CreatorWorkspaceCatalog.projectType("social-instagram-post"))
        assertEquals(
            CreatorStudioDestination.VIDEO,
            CreatorWorkspaceCatalog.projectType("social-reel")!!.destination,
        )
        assertEquals(
            CreatorStudioDestination.MUSIC,
            CreatorWorkspaceCatalog.projectType("music-dj-set")!!.destination,
        )
    }

    @Test
    fun everyProjectReferencesADeclaredWorldAndAllIdsAreUnique() {
        val worldIds = CreatorWorkspaceCatalog.worlds.map { it.id }.toSet()
        val projectIds = CreatorWorkspaceCatalog.projectTypes.map { it.id }

        assertEquals(CreatorWorkspaceCatalog.worlds.size, worldIds.size)
        assertEquals(CreatorWorkspaceCatalog.projectTypes.size, projectIds.toSet().size)
        assertTrue(CreatorWorkspaceCatalog.projectTypes.all { it.worldId in worldIds })
    }

    @Test
    fun projectsForWorldReturnsOnlyThatWorld() {
        val fashion = CreatorWorkspaceCatalog.projectsForWorld("fashion")
        assertTrue(fashion.isNotEmpty())
        assertTrue(fashion.all { it.worldId == "fashion" })
    }
}

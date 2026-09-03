package com.patsy.app.thynk

import org.junit.Assert.assertEquals
import org.junit.Test

class CreatorWorkspaceModelsTest {
    @Test
    fun projectTypeKeepsStableRoutingMetadata() {
        val type = CreatorProjectType(
            id = "fashion-collection",
            worldId = "fashion",
            label = "Design a Collection",
            destination = CreatorStudioDestination.ADAPTIVE,
            workspaceConfigId = "fashion-collection",
        )

        assertEquals("fashion", type.worldId)
        assertEquals(CreatorStudioDestination.ADAPTIVE, type.destination)
        assertEquals("fashion-collection", type.workspaceConfigId)
    }
}

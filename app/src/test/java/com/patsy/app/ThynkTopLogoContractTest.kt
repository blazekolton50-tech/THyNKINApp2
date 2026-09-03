package com.patsy.app

import java.io.File
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ThynkTopLogoContractTest {
    @Test
    fun `THyNK IN home crops its official panel logo instead of Patsy wordmark`() {
        val home = source("app/src/main/java/com/patsy/app/ui/finaldesign/FinalHomeScreen.kt")

        assertTrue(home.contains("ThynkDestinationLogo("))
        assertTrue(home.contains("destination = ThynkPanelDestination.IN"))
        assertFalse(
            home.substringAfter("private fun FinalHomeTopBar()").substringBefore("private fun ContinueDesignsSection")
                .contains("R.drawable.patsy_logo_official_white"),
        )
        assertOfficialCropRenderer()
    }

    @Test
    fun `THyNK studio header selects official Music and IT panel logos`() {
        val studio = source("app/src/main/java/com/patsy/app/thynk/ThynkStudioScreen.kt")

        assertTrue(studio.contains("ThynkDestinationLogo("))
        assertTrue(studio.contains("ThynkWorkspaceNavigation.topLogoDestination(route)"))
        assertFalse(
            studio.substringAfter("private fun ThynkHeader(").substringBefore("private fun ThynkHub")
                .contains("R.drawable.patsy_logo_official_white"),
        )
        assertOfficialCropRenderer()
    }

    private fun assertOfficialCropRenderer() {
        val path = "app/src/main/java/com/patsy/app/ui/finaldesign/ThynkDestinationLogo.kt"
        val file = locate(path)
        assertNotNull(file, "Missing shared THyNK destination-logo renderer")
        val renderer = file.readText()
        assertTrue(renderer.contains("R.drawable.thynk_panel_official"))
        assertTrue(renderer.contains("destination.ordinal"))
        assertTrue(renderer.contains("srcOffset"))
        assertTrue(renderer.contains("srcSize"))
    }

    private fun source(path: String): String = locate(path)?.readText()
        ?: error("Could not locate $path")

    private fun locate(path: String): File? = sequenceOf(File(path), File("../$path"))
        .firstOrNull(File::isFile)
}

package com.patsy.app

import java.io.File
import kotlin.test.Test
import kotlin.test.assertTrue

class VisibleThynkLaunchContractTest {
    @Test
    fun `device build is unmistakably THyNK IN and exposes both creator studios from home`() {
        val manifest = source("app/src/main/AndroidManifest.xml")
        val gradle = source("app/build.gradle.kts")
        val home = source("app/src/main/java/com/patsy/app/ui/finaldesign/FinalHomeScreen.kt")
        val activity = source("app/src/main/java/com/patsy/app/FinalMainActivity.kt")

        assertTrue(manifest.contains("android:label=\"THyNK-IN!\""))
        assertTrue(gradle.contains("versionCode = 339"))
        assertTrue(gradle.contains("versionName = \"3.3.9-thynkin-visible1\""))

        assertTrue(home.contains("onOpenThynkMusic: () -> Unit"))
        assertTrue(home.contains("onOpenThynkIt: () -> Unit"))
        assertTrue(home.contains("VisibleThynkLaunchSection("))

        assertTrue(activity.contains("onOpenThynkMusic = { navigateThynk(ThynkStudioEntry.MUSIC) }"))
        assertTrue(activity.contains("onOpenThynkIt = { navigateThynk(ThynkStudioEntry.IT) }"))
    }

    private fun source(path: String): String {
        val candidates = sequenceOf(File(path), File("../$path"))
        return candidates.firstOrNull(File::isFile)?.readText()
            ?: error("Could not locate $path")
    }
}

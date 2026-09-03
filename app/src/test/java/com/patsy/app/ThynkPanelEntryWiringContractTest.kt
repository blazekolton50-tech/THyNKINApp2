package com.patsy.app

import java.io.File
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ThynkPanelEntryWiringContractTest {
    @Test
    fun `secure shell preserves distinct THyNK Music and IT panel entries`() {
        val activity = source("app/src/main/java/com/patsy/app/FinalMainActivity.kt")
        val studio = source("app/src/main/java/com/patsy/app/thynk/ThynkStudioScreen.kt")

        assertTrue(activity.contains("ThynkPrimaryNavigationBar("))
        assertTrue(activity.contains("var thynkEntry by remember"))
        assertTrue(activity.contains("fun navigateThynk(entry: ThynkStudioEntry)"))
        assertTrue(activity.contains("thynkEntry = entry"))
        assertTrue(activity.contains("ThynkPanelDestination.MUSIC -> navigateThynk(ThynkStudioEntry.MUSIC)"))
        assertTrue(activity.contains("ThynkPanelDestination.IT -> navigateThynk(ThynkStudioEntry.IT)"))
        assertTrue(activity.contains("ThynkStudioScreen(\n                                        entry = thynkEntry,"))

        assertTrue(studio.contains("entry: ThynkStudioEntry"))
        assertTrue(studio.contains("ThynkWorkspaceNavigation.initialRoute(entry)"))
        assertTrue(studio.contains("LaunchedEffect(entry)"))
    }

    @Test
    fun `generic creator entry resets to THyNK IT instead of inheriting Music`() {
        val activity = source("app/src/main/java/com/patsy/app/FinalMainActivity.kt")

        assertTrue(activity.contains("fun navigateThynk(entry: ThynkStudioEntry)"))
        assertTrue(
            activity.contains(
                "if (destination == FinalHomeDestination.THYNK) {\n            navigateThynk(ThynkStudioEntry.IT)\n            return\n        }",
            ),
        )
        assertTrue(activity.contains("ThynkPanelDestination.MUSIC -> navigateThynk(ThynkStudioEntry.MUSIC)"))
        assertTrue(activity.contains("ThynkPanelDestination.IT -> navigateThynk(ThynkStudioEntry.IT)"))
    }

    @Test
    fun `generic THyNK deep link resets to IT instead of inheriting Music`() {
        val activity = source("app/src/main/java/com/patsy/app/FinalMainActivity.kt")

        assertTrue(activity.contains("if (deepLinkedPage == FinalAppPage.THYNK)"))
        assertTrue(activity.contains("navigateThynk(ThynkStudioEntry.IT)"))
        assertFalse(
            activity.contains(
                "is NavigationDecision.Allowed -> decision.route.destination.toFinalPage() ?: FinalAppPage.PROTECTED",
            ),
        )
    }

    private fun source(path: String): String {
        val candidates = sequenceOf(File(path), File("../$path"))
        return candidates.firstOrNull(File::isFile)?.readText()
            ?: error("Could not locate $path")
    }
}

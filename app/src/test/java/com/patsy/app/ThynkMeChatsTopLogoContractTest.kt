package com.patsy.app

import java.io.File
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ThynkMeChatsTopLogoContractTest {
    @Test
    fun `profile header uses exact THyNK ME logo crop`() {
        val source = source("app/src/main/java/com/patsy/app/ui/finaldesign/FinalProfileDmScreens.kt")
        val profile = source.substringAfter("fun FinalProfileScreen(").substringBefore("fun FinalPatsyDmScreen(")

        assertTrue(profile.contains("ThynkDestinationLogo("))
        assertTrue(profile.contains("destination = ThynkPanelDestination.ME"))
        assertFalse(profile.contains("Text(\"PROFILE\""))
    }

    @Test
    fun `DM inbox header uses exact THyNK Chats logo crop`() {
        val source = source("app/src/main/java/com/patsy/app/ui/finaldesign/FinalProfileDmScreens.kt")
        val inbox = source.substringAfter("private fun DmInboxPane(").substringBefore("private fun DmConversationPane(")

        assertTrue(inbox.contains("ThynkDestinationLogo("))
        assertTrue(inbox.contains("destination = ThynkPanelDestination.CHATS"))
        assertFalse(inbox.contains("Text(\"PATSY DMs\""))
    }

    private fun source(path: String): String {
        val candidates = sequenceOf(File(path), File("../$path"))
        return candidates.firstOrNull(File::isFile)?.readText()
            ?: error("Could not locate $path")
    }
}

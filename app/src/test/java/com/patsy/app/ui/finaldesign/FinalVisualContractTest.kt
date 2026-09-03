package com.patsy.app.ui.finaldesign

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FinalVisualContractTest {
    @Test
    fun `final screens keep locked Patsy header and footer copy`() {
        assertEquals(132, FinalVisualContract.logoSquareDp)
        assertEquals(0xFF000000, FinalVisualContract.logoSquareArgb)
        assertEquals("Hi, I'm Patsy, Your AI Pet Pal.", FinalVisualContract.introCopy)
        assertEquals("YOUR AI. YOUR WORKSPACE. YOUR CONTROL.", FinalVisualContract.footerCopy)
    }

    @Test
    fun `login shows Remember Me while persistence remains keepSignedIn`() {
        assertEquals("Remember Me", FinalVisualContract.loginPersistenceVisibleLabel)
        assertEquals("keepSignedIn", FinalVisualContract.loginPersistenceSemanticName)
        assertFalse(FinalVisualContract.loginPersistenceIsSavedContentMemory)
    }

    @Test
    fun `primary navigation semantics remain security stable`() {
        assertEquals(
            listOf("HOME", "THyNK", "CAMERA", "PATSY DMS", "PROFILE"),
            FinalVisualContract.primaryNavigation,
        )
        assertEquals(
            listOf("Home", "", "", "PDMs", "Profile"),
            FinalVisualContract.primaryNavigationDisplayLabels,
        )
    }

    @Test
    fun `THyNK panel uses the five locked branded destinations`() {
        assertEquals(
            listOf("THyNK-ME", "THyNK Chats", "THyNK-IN", "THyNK Music", "THyNK-IT"),
            FinalVisualContract.thynkPanelDisplayLabels,
        )
    }

    @Test
    fun `global navigation visual remains available outside auth entry`() {
        assertTrue(FinalVisualContract.navigationVisibleOnAllPages)
        assertFalse(FinalVisualContract.showThynkSecondaryLabel)
        assertFalse(FinalVisualContract.showCenterActionSecondaryLabel)
    }

    @Test
    fun `debug owner entry credentials remain locked to debug use`() {
        assertEquals("patsytest", FinalVisualContract.debugUsername)
        assertEquals("PatsyTest!2026", FinalVisualContract.debugTemporaryPassword)
    }
}

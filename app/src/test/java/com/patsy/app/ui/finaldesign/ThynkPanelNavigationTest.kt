package com.patsy.app.ui.finaldesign

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ThynkPanelNavigationTest {
    @Test
    fun `five THyNK panel buttons keep the locked left to right order`() {
        assertEquals(
            listOf(
                ThynkPanelDestination.ME,
                ThynkPanelDestination.CHATS,
                ThynkPanelDestination.IN,
                ThynkPanelDestination.MUSIC,
                ThynkPanelDestination.IT,
            ),
            ThynkPanelDestination.entries,
        )
        assertEquals(
            listOf("THyNK-ME", "THyNK Chats", "THyNK-IN!", "THyNK Music", "THyNK-IT"),
            ThynkPanelDestination.entries.map { it.visibleLabel },
        )
        assertEquals(
            ThynkPanelDestination.IN,
            ThynkPanelDestination.entries[2],
            "THyNK-IN! must remain the centre destination.",
        )
    }

    @Test
    fun `THyNK panel buttons preserve existing secure route destinations`() {
        assertEquals(FinalHomeDestination.PROFILE, ThynkPanelDestination.ME.secureDestination())
        assertEquals(FinalHomeDestination.PATSY_DMS, ThynkPanelDestination.CHATS.secureDestination())
        assertEquals(FinalHomeDestination.HOME, ThynkPanelDestination.IN.secureDestination())
        assertEquals(FinalHomeDestination.THYNK, ThynkPanelDestination.MUSIC.secureDestination())
        assertEquals(FinalHomeDestination.THYNK, ThynkPanelDestination.IT.secureDestination())
    }

    @Test
    fun `panel stays visible while browsing and choosing categories but hides on editing boards`() {
        assertTrue(thynkPanelVisibleFor(ThynkPanelSurface.BROWSING))
        assertTrue(thynkPanelVisibleFor(ThynkPanelSurface.CATEGORY))
        assertFalse(thynkPanelVisibleFor(ThynkPanelSurface.EDITING_BOARD))
    }

    @Test
    fun `only selected button uses rainbow when rainbow setting is enabled`() {
        assertTrue(shouldLightThynkPanelItem(ThynkPanelDestination.IN, ThynkPanelDestination.IN, rainbowEnabled = true))
        assertFalse(shouldLightThynkPanelItem(ThynkPanelDestination.ME, ThynkPanelDestination.IN, rainbowEnabled = true))
        assertFalse(shouldLightThynkPanelItem(ThynkPanelDestination.IN, ThynkPanelDestination.IN, rainbowEnabled = false))
    }
}

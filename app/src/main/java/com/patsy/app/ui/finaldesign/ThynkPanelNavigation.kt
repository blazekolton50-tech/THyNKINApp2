package com.patsy.app.ui.finaldesign

/**
 * Visible THyNK Panel destinations. The enum declaration order is the locked left-to-right order.
 *
 * These visible destinations intentionally map onto the existing secure shell destinations rather
 * than replacing auth/navigation policy. MUSIC and IT both enter the authenticated THyNK area and
 * choose their specialist entry point inside that authorized route.
 */
enum class ThynkPanelDestination(val visibleLabel: String) {
    ME("THyNK-ME"),
    CHATS("THyNK Chats"),
    IN("THyNK-IN!"),
    MUSIC("THyNK Music"),
    IT("THyNK-IT"),
}

enum class ThynkPanelSurface {
    BROWSING,
    CATEGORY,
    EDITING_BOARD,
}

fun ThynkPanelDestination.secureDestination(): FinalHomeDestination = when (this) {
    ThynkPanelDestination.ME -> FinalHomeDestination.PROFILE
    ThynkPanelDestination.CHATS -> FinalHomeDestination.PATSY_DMS
    ThynkPanelDestination.IN -> FinalHomeDestination.HOME
    ThynkPanelDestination.MUSIC,
    ThynkPanelDestination.IT -> FinalHomeDestination.THYNK
}

fun thynkPanelVisibleFor(surface: ThynkPanelSurface): Boolean = when (surface) {
    ThynkPanelSurface.BROWSING,
    ThynkPanelSurface.CATEGORY -> true
    ThynkPanelSurface.EDITING_BOARD -> false
}

fun shouldLightThynkPanelItem(
    item: ThynkPanelDestination,
    selected: ThynkPanelDestination?,
    rainbowEnabled: Boolean,
): Boolean = rainbowEnabled && item == selected

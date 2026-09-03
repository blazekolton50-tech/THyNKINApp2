package com.patsy.app.ui.finaldesign

/**
 * SAVE MAIN APP / LOCK IN SAVE
 *
 * Source-of-truth constants for the FINAL Login, Set Password and authenticated chrome.
 * The visible Login/Set Password label remains "Remember Me", while the underlying authentication
 * meaning is keepSignedIn/session restoration only. The separate Patsy Remember Me paw remains
 * reserved for user-approved saved content.
 *
 * IMPORTANT: global app navigation and the THyNK-area Panel are separate UI contracts.
 * Global navigation keeps HOME / THyNK / CAMERA / PATSY DMS / PROFILE semantics and the existing
 * Home / THyNK mark / + / PDMs / Profile visual. THyNK-area pages additionally use the locked
 * THyNK-ME / THyNK Chats / THyNK-IN / THyNK Music / THyNK-IT panel.
 */
object FinalVisualContract {
    const val logoSquareDp = 132
    const val logoSquareArgb = 0xFF000000
    const val charcoalArgb = 0xFF0E0E10
    const val cardArgb = 0xFF151518

    const val introCopy = "Hi, I'm Patsy, Your AI Pet Pal."
    const val footerCopy = "YOUR AI. YOUR WORKSPACE. YOUR CONTROL."

    const val loginPersistenceVisibleLabel = "Remember Me"
    const val loginPersistenceSemanticName = "keepSignedIn"
    const val loginPersistenceIsSavedContentMemory = false

    // Global semantic destinations remain stable for route authorization/security.
    val primaryNavigation = listOf("HOME", "THyNK", "CAMERA", "PATSY DMS", "PROFILE")

    // App-wide bottom navigation remains visible on authenticated pages.
    const val navigationVisibleOnAllPages = true
    val primaryNavigationDisplayLabels = listOf("Home", "", "", "PDMs", "Profile")
    const val showThynkSecondaryLabel = false
    const val showCenterActionSecondaryLabel = false

    // THyNK-area panel: distinct from the global app navigation and shown on relevant THyNK pages.
    const val thynkPanelVisibleOnThynkPages = true
    val thynkPanelDisplayLabels = listOf(
        "THyNK-ME",
        "THyNK Chats",
        "THyNK-IN",
        "THyNK Music",
        "THyNK-IT",
    )

    const val debugUsername = "patsytest"
    const val debugTemporaryPassword = "PatsyTest!2026"
}

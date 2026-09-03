package com.patsy.app.thynk

import com.patsy.app.studio.StudioMode

enum class CreatorStudioDestination {
    ADAPTIVE,
    MUSIC,
    VIDEO,
}

enum class CreatorCanvasMode {
    FREEFORM,
    PAGE,
    SPREAD,
    DOCUMENT,
    SOCIAL,
}

data class CreatorWorld(
    val id: String,
    val label: String,
)

data class CreatorProjectType(
    val id: String,
    val worldId: String,
    val label: String,
    val destination: CreatorStudioDestination,
    val workspaceConfigId: String? = null,
)

data class WorkspaceConfig(
    val id: String,
    val title: String,
    val studioMode: StudioMode,
    val canvasMode: CreatorCanvasMode,
    val defaultWidthPx: Int,
    val defaultHeightPx: Int,
    val toolIds: List<String>,
    val beginnerShortcutIds: List<String> = emptyList(),
    val templateFamilyIds: List<String> = emptyList(),
    val paletteFamilyIds: List<String> = emptyList(),
    val typographyFamilyIds: List<String> = emptyList(),
)

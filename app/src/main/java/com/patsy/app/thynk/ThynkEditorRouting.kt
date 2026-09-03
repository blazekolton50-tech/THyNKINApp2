package com.patsy.app.thynk

private val designEditorItems = setOf(
    "POSTERS",
    "FLYERS",
    "INVITATIONS",
    "CARDS",
    "MENUS",
    "PRICE LISTS",
    "SIGNS",
    "CERTIFICATES",
    "BROCHURES",
    "LABELS",
    "BLANK DESIGNS",
    "CUSTOM SIZE",
    "TEMPLATES",
)

private val videoEditorItems = setOf(
    "VIDEO EDITOR",
    "TRIM & CUT",
    "SPLIT",
    "TRANSITIONS",
    "SUBTITLES",
    "OVERLAYS",
    "GREEN SCREEN",
    "VIDEO FILTERS",
    "SLOW MOTION",
    "SLIDESHOW",
    "LOOP",
    "ASPECT RESIZER",
)

enum class ThynkEditorKind {
    DESIGN,
    VIDEO,
}

data class ThynkEditorDestination(
    val categoryId: String,
    val kind: ThynkEditorKind,
)

internal fun editorPageForThynkItem(item: String): String? = when {
    item in designEditorItems -> "design-editor"
    item in videoEditorItems -> "video-editor"
    else -> null
}

internal fun studioEntryForEditorPage(pageId: String): ThynkStudioEntry? = when (pageId) {
    "design-editor" -> ThynkStudioEntry.IT
    "video-editor" -> ThynkStudioEntry.MUSIC
    else -> null
}

internal fun editorDestinationForPage(pageId: String): ThynkEditorDestination? = when (pageId) {
    "design-editor" -> ThynkEditorDestination("design", ThynkEditorKind.DESIGN)
    "video-editor" -> ThynkEditorDestination("video", ThynkEditorKind.VIDEO)
    else -> null
}

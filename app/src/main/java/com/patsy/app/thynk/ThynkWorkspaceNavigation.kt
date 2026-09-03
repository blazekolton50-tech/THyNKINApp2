package com.patsy.app.thynk

import com.patsy.app.ui.finaldesign.ThynkPanelDestination

/** Which specialist THyNK hub the five-logo app panel opened. */
enum class ThynkStudioEntry {
    MUSIC,
    IT,
}

/** Internal THyNK workspace history; the authenticated shell still owns global destinations. */
internal sealed interface ThynkWorkspaceRoute {
    data object Hub : ThynkWorkspaceRoute
    data class Category(val categoryId: String) : ThynkWorkspaceRoute
    data class Tool(
        val categoryId: String,
        val item: String,
    ) : ThynkWorkspaceRoute
    data class Music(val pageId: String) : ThynkWorkspaceRoute
    data class Editor(
        val pageId: String,
        val categoryId: String,
    ) : ThynkWorkspaceRoute
}

internal data class ThynkWorkspaceChrome(
    val showBack: Boolean,
    val showOverflowHome: Boolean,
    val showGlobalHomebar: Boolean,
)

internal object ThynkWorkspaceNavigation {
    private val musicEditingBoardPageIds = setOf(
        "recording",
        "track-editor",
        "mixer",
        "equalizer",
        "effects",
        "lyrics-vocals",
        "dj-studio",
        "beats-sampler",
        "piano-roll",
        "ai-tools",
        "auto-tuner",
        "mastering",
        "video-editor",
        "export",
    )

    fun initialRoute(entry: ThynkStudioEntry): ThynkWorkspaceRoute = when (entry) {
        ThynkStudioEntry.IT -> ThynkWorkspaceRoute.Hub
        ThynkStudioEntry.MUSIC -> ThynkWorkspaceRoute.Music("music-home")
    }

    fun openItItem(categoryId: String, item: String): ThynkWorkspaceRoute {
        val editorPage = editorPageForThynkItem(item)
        return if (editorPage != null && studioEntryForEditorPage(editorPage) == ThynkStudioEntry.IT) {
            ThynkWorkspaceRoute.Editor(pageId = editorPage, categoryId = categoryId)
        } else {
            ThynkWorkspaceRoute.Tool(categoryId = categoryId, item = item)
        }
    }

    fun topLogoDestination(route: ThynkWorkspaceRoute): ThynkPanelDestination = when (route) {
        is ThynkWorkspaceRoute.Music -> ThynkPanelDestination.MUSIC
        is ThynkWorkspaceRoute.Category,
        is ThynkWorkspaceRoute.Tool -> ThynkPanelDestination.IT
        is ThynkWorkspaceRoute.Editor -> when (studioEntryForEditorPage(route.pageId)) {
            ThynkStudioEntry.MUSIC -> ThynkPanelDestination.MUSIC
            ThynkStudioEntry.IT,
            null -> ThynkPanelDestination.IT
        }
        ThynkWorkspaceRoute.Hub -> ThynkPanelDestination.IT
    }

    fun isEditingBoard(route: ThynkWorkspaceRoute): Boolean = when (route) {
        is ThynkWorkspaceRoute.Editor -> true
        is ThynkWorkspaceRoute.Music -> route.pageId in musicEditingBoardPageIds
        is ThynkWorkspaceRoute.Tool -> route.categoryId == "fashion"
        ThynkWorkspaceRoute.Hub,
        is ThynkWorkspaceRoute.Category -> false
    }

    fun chrome(route: ThynkWorkspaceRoute): ThynkWorkspaceChrome {
        val editingBoard = isEditingBoard(route)
        val root = route is ThynkWorkspaceRoute.Hub ||
            (route is ThynkWorkspaceRoute.Music && route.pageId == "music-home")
        return ThynkWorkspaceChrome(
            showBack = !root,
            showOverflowHome = editingBoard,
            showGlobalHomebar = !editingBoard,
        )
    }

    fun back(route: ThynkWorkspaceRoute): ThynkWorkspaceRoute = when (route) {
        is ThynkWorkspaceRoute.Editor -> ThynkWorkspaceRoute.Category(route.categoryId)
        is ThynkWorkspaceRoute.Tool -> ThynkWorkspaceRoute.Category(route.categoryId)
        is ThynkWorkspaceRoute.Music -> when (route.pageId) {
            "music-home" -> route
            "video-player", "video-editor" -> ThynkWorkspaceRoute.Music("video-home")
            else -> ThynkWorkspaceRoute.Music("music-home")
        }
        is ThynkWorkspaceRoute.Category -> ThynkWorkspaceRoute.Hub
        ThynkWorkspaceRoute.Hub -> ThynkWorkspaceRoute.Hub
    }
}

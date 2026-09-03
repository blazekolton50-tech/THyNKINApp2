package com.patsy.app.thynk

enum class CameraActionAvailability {
    NATIVE,
    INTERNAL,
    NOT_CONFIGURED,
    FAKE_COMPLETE,
}

data class CameraHubAction(
    val id: String,
    val label: String,
    val availability: CameraActionAvailability,
)

enum class CameraMediaKind {
    PHOTO,
    VIDEO,
}

data class CameraMediaSelection(
    val uri: String,
    val kind: CameraMediaKind,
)

/**
 * SAVE MAIN APP / LOCK IN: the centre + remains Camera.
 * Native capture/import actions are real Android actions. Provider-backed AI actions remain
 * truthfully NOT_CONFIGURED until an approved provider is connected and verified.
 */
object CameraHubContract {
    val actions = listOf(
        CameraHubAction("take-photo", "PHOTO", CameraActionAvailability.NATIVE),
        CameraHubAction("record-video", "VIDEO", CameraActionAvailability.NATIVE),
        CameraHubAction("import-photo", "IMPORT PHOTO", CameraActionAvailability.NATIVE),
        CameraHubAction("import-video", "IMPORT VIDEO", CameraActionAvailability.NATIVE),
        CameraHubAction("open-editor", "OPEN THyNK EDITOR", CameraActionAvailability.INTERNAL),
        CameraHubAction("templates", "TEMPLATES", CameraActionAvailability.INTERNAL),
        CameraHubAction("ai-image", "AI IMAGE", CameraActionAvailability.NOT_CONFIGURED),
        CameraHubAction("ai-video", "AI VIDEO", CameraActionAvailability.NOT_CONFIGURED),
        CameraHubAction("projects", "PROJECTS", CameraActionAvailability.INTERNAL),
    )

    fun editorRouteFor(actionId: String): String? = when (actionId) {
        "record-video", "import-video", "open-editor" -> "video-editor"
        else -> null
    }
}

/**
 * Process-local handoff for a just-captured/imported URI. This is deliberately not presented as
 * durable project storage; project persistence remains the responsibility of the Studio repository.
 */
object CameraMediaHandoff {
    @Volatile
    private var selection: CameraMediaSelection? = null

    @Synchronized
    fun offer(uri: String, kind: CameraMediaKind): Boolean {
        val normalized = uri.trim()
        if (normalized.isEmpty()) return false
        selection = CameraMediaSelection(normalized, kind)
        return true
    }

    @Synchronized
    fun peek(): CameraMediaSelection? = selection

    @Synchronized
    fun clear() {
        selection = null
    }
}

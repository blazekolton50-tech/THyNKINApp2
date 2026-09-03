package com.patsy.app.studio

enum class StudioToolGroup {
    CREATE,
    CANVAS,
    EDIT,
    LOOK,
    LAYERS,
    MOTION,
    AUDIO,
    EXPORT,
}

data class StudioToolDescriptor(
    val id: String,
    val label: String,
    val group: StudioToolGroup,
    val modes: Set<StudioMode>,
    val requiresProcessing: Boolean = false,
)

object StudioToolCatalog {
    private val allModes = StudioMode.entries.toSet()
    private val stillModes = setOf(
        StudioMode.IMAGE,
        StudioMode.DOCUMENT,
        StudioMode.MEME,
        StudioMode.COLLAGE,
    )
    private val visualModes = stillModes + StudioMode.VIDEO + StudioMode.CAMERA
    private val timelineModes = setOf(StudioMode.VIDEO, StudioMode.CAMERA)

    val all: List<StudioToolDescriptor> = listOf(
        StudioToolDescriptor("templates", "Templates", StudioToolGroup.CREATE, allModes),
        StudioToolDescriptor("media", "Media", StudioToolGroup.CREATE, allModes),
        StudioToolDescriptor("ai", "AI Tools", StudioToolGroup.CREATE, visualModes, requiresProcessing = true),
        StudioToolDescriptor("text", "Text", StudioToolGroup.CREATE, visualModes),
        StudioToolDescriptor("elements", "Elements", StudioToolGroup.CREATE, visualModes),
        StudioToolDescriptor("stickers", "Stickers", StudioToolGroup.CREATE, visualModes),
        StudioToolDescriptor("draw", "Draw", StudioToolGroup.CREATE, visualModes),

        StudioToolDescriptor("crop", "Crop", StudioToolGroup.CANVAS, visualModes),
        StudioToolDescriptor("cutout", "Cutout", StudioToolGroup.CANVAS, visualModes, requiresProcessing = true),
        StudioToolDescriptor("resize", "Resize", StudioToolGroup.CANVAS, visualModes),
        StudioToolDescriptor("rotate", "Rotate", StudioToolGroup.CANVAS, visualModes),
        StudioToolDescriptor("mirror", "Mirror / Flip", StudioToolGroup.CANVAS, visualModes),
        StudioToolDescriptor("frames", "Frames", StudioToolGroup.CANVAS, visualModes),
        StudioToolDescriptor("guides", "Rulers & Guides", StudioToolGroup.CANVAS, visualModes),
        StudioToolDescriptor("eraser", "Eraser", StudioToolGroup.CANVAS, visualModes),

        StudioToolDescriptor("layers", "Layers", StudioToolGroup.LAYERS, visualModes),
        StudioToolDescriptor("position", "Position", StudioToolGroup.LAYERS, visualModes),
        StudioToolDescriptor("opacity", "Opacity", StudioToolGroup.LAYERS, visualModes),
        StudioToolDescriptor("blend", "Blend", StudioToolGroup.LAYERS, visualModes),

        StudioToolDescriptor("filters", "Filters", StudioToolGroup.LOOK, visualModes),
        StudioToolDescriptor("adjust", "Adjust", StudioToolGroup.LOOK, visualModes),
        StudioToolDescriptor("effects", "Effects", StudioToolGroup.LOOK, visualModes),

        StudioToolDescriptor("animate", "Animate", StudioToolGroup.MOTION, visualModes),
        StudioToolDescriptor("trim", "Trim", StudioToolGroup.MOTION, timelineModes),
        StudioToolDescriptor("split", "Split", StudioToolGroup.MOTION, timelineModes),
        StudioToolDescriptor("speed", "Speed", StudioToolGroup.MOTION, timelineModes),
        StudioToolDescriptor("transitions", "Transitions", StudioToolGroup.MOTION, timelineModes),
        StudioToolDescriptor("captions", "Captions", StudioToolGroup.MOTION, timelineModes),

        StudioToolDescriptor("audio", "Audio", StudioToolGroup.AUDIO, timelineModes),
        StudioToolDescriptor("music", "Original Music", StudioToolGroup.AUDIO, timelineModes, requiresProcessing = true),
        StudioToolDescriptor("volume", "Volume", StudioToolGroup.AUDIO, timelineModes),
        StudioToolDescriptor("fade", "Fade", StudioToolGroup.AUDIO, timelineModes),

        StudioToolDescriptor("undo", "Undo", StudioToolGroup.EDIT, allModes),
        StudioToolDescriptor("redo", "Redo", StudioToolGroup.EDIT, allModes),
        StudioToolDescriptor("reset", "Reset", StudioToolGroup.EDIT, allModes),
        StudioToolDescriptor("export", "Export", StudioToolGroup.EXPORT, allModes, requiresProcessing = true),
    )

    fun forMode(mode: StudioMode): List<StudioToolDescriptor> = all.filter { mode in it.modes }
}

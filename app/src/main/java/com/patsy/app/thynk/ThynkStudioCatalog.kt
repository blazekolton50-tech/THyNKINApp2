package com.patsy.app.thynk

data class ThynkCategory(
    val id: String,
    val label: String,
    val description: String,
    val items: List<String>,
)

data class ThynkMusicPage(
    val id: String,
    val title: String,
    val subtitle: String,
    val providerState: String = "LOCAL_UI",
)

/** Owner-locked production workspace set. Arrangement/Timeline is shared and is not page ten. */
object ThynkMusicLockedWorkspaces {
    val ids = listOf(
        "mixer",
        "effects",
        "equalizer",
        "lyrics-vocals",
        "dj-studio",
        "beats-sampler",
        "piano-roll",
        "ai-tools",
        "mastering",
    )
}

object ThynkStudioCatalog {
    val categories = listOf(
        ThynkCategory(
            "design",
            "DESIGN & TEMPLATES",
            "Create graphics, layouts and editable designs.",
            listOf("POSTERS", "FLYERS", "INVITATIONS", "CARDS", "MENUS", "PRICE LISTS", "SIGNS", "CERTIFICATES", "BROCHURES", "LABELS", "BLANK DESIGNS", "CUSTOM SIZE", "TEMPLATES"),
        ),
        ThynkCategory(
            "social",
            "SOCIAL & CONTENT",
            "Build posts, stories and creator content.",
            listOf("SQUARE POST", "PORTRAIT / STORY", "CAROUSEL", "QUOTE", "MEME", "ANNOUNCEMENT", "BEFORE / AFTER", "CREATOR TIP", "STAT CARD", "INFOGRAPHIC", "THUMBNAIL", "BANNER", "PROMO GRAPHIC"),
        ),
        ThynkCategory(
            "photo",
            "PHOTO & IMAGE",
            "Edit, enhance and transform images.",
            listOf("EDIT PHOTO", "CROP", "RESIZE", "ROTATE", "FLIP", "CUTOUT", "REMOVE BACKGROUND", "REMOVE OBJECT", "FILTERS", "RETOUCH", "COLOR CORRECTION", "FRAMES"),
        ),
        ThynkCategory(
            "documents",
            "DOCUMENTS & BUSINESS",
            "Build useful documents and business materials.",
            listOf("DOCUMENT", "CV / RESUME", "INVOICE", "PROPOSAL", "LETTER", "REPORT", "AGENDA", "CHECKLIST", "BRAND GUIDE", "TIMELINE", "PRICE LIST", "BUSINESS PLAN"),
        ),
        ThynkCategory(
            "study",
            "HOMEWORK & STUDY",
            "Study, revise, organise and learn.",
            listOf("ESSAY HELP", "FLASHCARDS", "TIMETABLE", "RESEARCH PLAN", "CITATIONS", "LAB REPORT", "MIND MAP", "NOTES", "QUIZ MAKER", "REVISION CARDS", "PROJECT PLAN", "WORKSHEET"),
        ),
        ThynkCategory(
            "presentations",
            "PRESENTATIONS & PLANNING",
            "Present ideas and organise projects.",
            listOf("PRESENTATION", "PITCH DECK", "LESSON PLAN", "MOOD BOARD", "ROADMAP", "MEETING DECK", "PORTFOLIO", "SWOT ANALYSIS", "CALENDAR", "GOAL TRACKER", "KANBAN BOARD", "WEEKLY PLANNER"),
        ),
        ThynkCategory(
            "fashion",
            "FASHION & TEXTILES",
            "Pattern, sketch, layer, material and production tools from the native THyNK-IT fashion pipeline.",
            listOf("PATTERN CAD", "3D SKETCH", "LAYER MIXER", "MATERIALS LAB", "PRODUCTION"),
        ),
        ThynkCategory(
            "collage",
            "COLLAGE & CREATIVE",
            "Play, experiment and build creative layouts.",
            listOf("PHOTO COLLAGE", "SCRAPBOOK", "MOOD COLLAGE", "POLAROID WALL", "MEME BUILDER", "STICKER COLLAGE", "MAGAZINE CUTOUT", "GRID COLLAGE", "FREESTYLE BOARD", "SHAPE COLLAGE", "LAYERED ART", "MEMORY WALL"),
        ),
        ThynkCategory(
            "ai",
            "AI & MY STUDIO",
            "AI tools, projects, uploads and your creative space.",
            listOf("AI IMAGE", "AI WRITER", "AI BACKGROUND", "AI UPSCALE", "AI IDEAS", "MY PROJECTS", "MY FAVORITES", "MY UPLOADS", "MY FONTS", "MY PALETTES", "RECENT EDITS", "ARCHIVE"),
        ),
    )
}

object ThynkMusicCatalog {
    val pages = listOf(
        // Entry and compatibility routes remain available, but are not additional production workspaces.
        ThynkMusicPage("music-home", "THyNK Music", "Music, video, recording and media production."),
        ThynkMusicPage("create-music", "Create Music", "Start a new original track.", "NOT_CONFIGURED"),
        ThynkMusicPage("ai-music-generator", "AI Music Generator", "Generate original music through a configured provider.", "NOT_CONFIGURED"),
        ThynkMusicPage("recording", "Recording", "Record a real local microphone take as AAC audio in an .m4a file.", "LOCAL_NATIVE"),
        ThynkMusicPage("track-editor", "Track Editor", "Arrange, trim and shape tracks."),

        // Exactly nine owner-approved production workspaces.
        ThynkMusicPage("mixer", "Mixer / Master Console", "Channel strips, master controls and shared arrangement."),
        ThynkMusicPage("effects", "Effects Rack", "Hardware-style effect routing and performance controls."),
        ThynkMusicPage("equalizer", "Equaliser", "Multi-band EQ and truthful analysis controls."),
        ThynkMusicPage("lyrics-vocals", "Vocal Studio", "Recording, pitch, harmony, takes and vocal controls.", "NOT_CONFIGURED"),
        ThynkMusicPage("dj-studio", "DJ Studio", "Dual-deck performance, cue, mix and project controls.", "NOT_CONFIGURED"),
        ThynkMusicPage("beats-sampler", "Beats Sampler", "16-pad sampler, sequence and shaping workspace.", "NOT_CONFIGURED"),
        ThynkMusicPage("piano-roll", "Piano Roll / MIDI", "MIDI note grid, velocity and instrument workspace."),
        ThynkMusicPage("ai-tools", "AI Tools Suite", "Six production AI tool areas with truthful provider states.", "NOT_CONFIGURED"),
        ThynkMusicPage("mastering", "Mastering & Export", "Master bus, loudness, format and export controls.", "NOT_CONFIGURED"),

        // Existing compatibility/media routes remain reachable but are not counted in the nine.
        ThynkMusicPage("auto-tuner", "Auto-Tuner", "Pitch-correction controls require a verified audio processing engine.", "NOT_CONFIGURED"),
        ThynkMusicPage("video-home", "Video", "Play, import and edit video inside the THyNK Music media family."),
        ThynkMusicPage("video-player", "Video Player", "Play a real selected video with the native Media3 foundation."),
        ThynkMusicPage("video-editor", "Video Editor", "Edit a real selected video with the shared native Media3 editor."),
        ThynkMusicPage("export", "Export", "Compatibility export route; production mastering/export lives in Mastering & Export.", "NOT_CONFIGURED"),
    )
}

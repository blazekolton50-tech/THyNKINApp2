package com.patsy.app.thynk

import com.patsy.app.studio.StudioMode

private val adaptiveWorkspaceConfigs = listOf(
    WorkspaceConfig(
        id = "fashion-collection",
        title = "Fashion Collection",
        studioMode = StudioMode.IMAGE,
        canvasMode = CreatorCanvasMode.FREEFORM,
        defaultWidthPx = 1600,
        defaultHeightPx = 1200,
        toolIds = listOf(
            "templates", "media", "text", "elements", "draw", "guides",
            "layers", "position", "opacity", "adjust", "effects", "undo", "redo", "export",
        ),
        beginnerShortcutIds = listOf(
            "fashion-mood-board", "fashion-garments", "fashion-swatches",
            "fashion-fabrics", "fashion-measurements", "fashion-lookbook",
            "fashion-pattern-cad", "fashion-sketch-3d", "fashion-layer-mixer",
            "fashion-materials-lab", "fashion-production",
        ),
        templateFamilyIds = listOf("fashion-collection", "fashion-lookbook", "fashion-mood-board"),
        paletteFamilyIds = listOf("fashion-seasonal", "fashion-luxury", "fashion-streetwear"),
        typographyFamilyIds = listOf("fashion-editorial", "fashion-luxury"),
    ),
    WorkspaceConfig(
        id = "publishing-magazine",
        title = "Magazine",
        studioMode = StudioMode.DOCUMENT,
        canvasMode = CreatorCanvasMode.SPREAD,
        defaultWidthPx = 2480,
        defaultHeightPx = 3508,
        toolIds = listOf(
            "templates", "media", "text", "elements", "guides", "layers",
            "position", "opacity", "undo", "redo", "export",
        ),
        beginnerShortcutIds = listOf(
            "publication-pages", "publication-master-pages", "publication-columns",
            "publication-margins", "publication-bleed", "publication-page-numbers",
        ),
        templateFamilyIds = listOf(
            "magazine-editorial", "magazine-fashion", "magazine-culture", "zine",
        ),
        paletteFamilyIds = listOf("editorial", "luxury", "culture"),
        typographyFamilyIds = listOf("editorial", "newspaper", "display-body"),
    ),
    WorkspaceConfig(
        id = "office-cv",
        title = "CV / Resume",
        studioMode = StudioMode.DOCUMENT,
        canvasMode = CreatorCanvasMode.DOCUMENT,
        defaultWidthPx = 2480,
        defaultHeightPx = 3508,
        toolIds = listOf(
            "templates", "media", "text", "elements", "guides", "layers",
            "position", "undo", "redo", "export",
        ),
        beginnerShortcutIds = listOf(
            "office-sections", "office-headings", "office-contact",
            "office-experience", "office-education", "office-skills",
        ),
        templateFamilyIds = listOf("office-cv-modern", "office-cv-minimal", "office-cv-professional"),
        paletteFamilyIds = listOf("office-neutral", "corporate"),
        typographyFamilyIds = listOf("office-readable", "office-professional"),
    ),
    WorkspaceConfig(
        id = "art-illustration",
        title = "Illustration",
        studioMode = StudioMode.IMAGE,
        canvasMode = CreatorCanvasMode.FREEFORM,
        defaultWidthPx = 1600,
        defaultHeightPx = 1600,
        toolIds = listOf(
            "media", "text", "elements", "draw", "resize", "rotate", "guides",
            "layers", "position", "opacity", "blend", "filters", "adjust", "effects",
            "undo", "redo", "export",
        ),
        beginnerShortcutIds = listOf("art-brushes", "art-palette", "art-reference", "art-layers"),
        paletteFamilyIds = listOf("art-painterly", "art-vivid", "art-earth"),
        typographyFamilyIds = listOf("art-display"),
    ),
    WorkspaceConfig(
        id = "social-instagram-post",
        title = "Instagram Post",
        studioMode = StudioMode.IMAGE,
        canvasMode = CreatorCanvasMode.SOCIAL,
        defaultWidthPx = 1080,
        defaultHeightPx = 1080,
        toolIds = listOf(
            "templates", "media", "text", "elements", "stickers", "draw", "resize", "frames",
            "layers", "position", "opacity", "filters", "adjust", "effects", "undo", "redo", "export",
        ),
        beginnerShortcutIds = listOf(
            "social-layouts", "social-brand-kit", "social-background", "social-resize",
        ),
        templateFamilyIds = listOf(
            "social-instagram-post", "social-announcement", "social-promo", "social-quote",
        ),
        paletteFamilyIds = listOf("social-trending", "social-brand"),
        typographyFamilyIds = listOf("social-display", "social-clean"),
    ),
)

fun CreatorWorkspaceCatalog.workspaceConfig(id: String): WorkspaceConfig? =
    adaptiveWorkspaceConfigs.firstOrNull { it.id == id }

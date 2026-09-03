package com.patsy.app.thynk

object CreatorWorkspaceCatalog {
    val worlds: List<CreatorWorld> = listOf(
        CreatorWorld("design", "Design & Graphics"),
        CreatorWorld("art", "Art & Illustration"),
        CreatorWorld("office", "Office Studio"),
        CreatorWorld("publishing", "Publishing"),
        CreatorWorld("journalism", "Journalism & Newsroom"),
        CreatorWorld("fashion", "Fashion & Textiles"),
        CreatorWorld("photo", "Photography"),
        CreatorWorld("social", "Social Content"),
        CreatorWorld("branding", "Branding & Advertising"),
        CreatorWorld("writing", "Books & Writing"),
        CreatorWorld("comics", "Comics & Storytelling"),
        CreatorWorld("interiors", "Interior Design"),
        CreatorWorld("architecture", "Architecture Concepts"),
        CreatorWorld("product", "Product Design"),
        CreatorWorld("ceramics", "Pottery & Ceramics"),
        CreatorWorld("jewellery", "Jewellery"),
        CreatorWorld("crafts", "Crafts & Making"),
        CreatorWorld("education", "Education & Study"),
        CreatorWorld("business", "Business & Entrepreneurship"),
        CreatorWorld("events", "Events"),
        CreatorWorld("portfolio", "Portfolio & Showcase"),
        CreatorWorld("music", "THyNK Music"),
        CreatorWorld("video", "THyNK Video"),
    )

    val projectTypes: List<CreatorProjectType> = listOf(
        adaptive("design-poster", "design", "Poster"),
        adaptive("design-flyer", "design", "Flyer"),
        adaptive("design-invitation", "design", "Invitation"),
        adaptive("design-brochure", "design", "Brochure"),

        adaptive("art-drawing", "art", "Drawing"),
        adaptive("art-illustration", "art", "Illustration"),
        adaptive("art-concept", "art", "Concept Art"),
        adaptive("art-collage", "art", "Collage"),
        adaptive("art-comic-page", "art", "Comic Page"),
        adaptive("art-character-sheet", "art", "Character Sheet"),
        adaptive("art-exhibition-board", "art", "Exhibition Board"),
        adaptive("art-portfolio", "art", "Art Portfolio"),

        adaptive("office-document", "office", "Document"),
        adaptive("office-letter", "office", "Letter"),
        adaptive("office-cv", "office", "CV / Resume"),
        adaptive("office-report", "office", "Report"),
        adaptive("office-proposal", "office", "Proposal"),
        adaptive("office-invoice", "office", "Invoice"),
        adaptive("office-presentation", "office", "Presentation"),
        adaptive("office-worksheet", "office", "Worksheet"),
        adaptive("office-planner", "office", "Planner"),

        adaptive("publishing-magazine", "publishing", "Magazine"),
        adaptive("publishing-newspaper", "publishing", "Newspaper"),
        adaptive("publishing-zine", "publishing", "Zine"),
        adaptive("publishing-brochure", "publishing", "Brochure"),
        adaptive("publishing-catalogue", "publishing", "Catalogue"),
        adaptive("publishing-book-layout", "publishing", "Book Layout"),

        adaptive("journalism-front-page", "journalism", "Front Page"),
        adaptive("journalism-article", "journalism", "Article"),
        adaptive("journalism-interview", "journalism", "Interview"),
        adaptive("journalism-investigation", "journalism", "Investigation Board"),
        adaptive("journalism-newsletter", "journalism", "Newsletter"),

        adaptive("fashion-collection", "fashion", "Design a Collection"),
        adaptive("fashion-mood-board", "fashion", "Mood Board"),
        adaptive("fashion-garment-sketch", "fashion", "Garment Sketch"),
        adaptive("fashion-technical-flat", "fashion", "Technical Flat"),
        adaptive("fashion-fabric-board", "fashion", "Fabric Board"),
        adaptive("fashion-lookbook", "fashion", "Lookbook"),
        adaptive("fashion-line-sheet", "fashion", "Line Sheet"),
        adaptive("fashion-sewing-project", "fashion", "Sewing Project"),

        adaptive("photo-edit", "photo", "Edit Photo"),
        adaptive("photo-contact-sheet", "photo", "Contact Sheet"),
        adaptive("photo-portfolio", "photo", "Photography Portfolio"),

        adaptive("social-instagram-post", "social", "Instagram Post"),
        adaptive("social-story", "social", "Story"),
        adaptive("social-carousel", "social", "Carousel"),
        adaptive("social-youtube-thumbnail", "social", "YouTube Thumbnail"),
        adaptive("social-linkedin-post", "social", "LinkedIn Post"),
        specialist("social-reel", "social", "Reel / Short Video", CreatorStudioDestination.VIDEO),

        adaptive("branding-brand-kit", "branding", "Brand Kit"),
        adaptive("branding-campaign", "branding", "Campaign"),
        adaptive("branding-packaging", "branding", "Packaging Concept"),

        adaptive("writing-book", "writing", "Book"),
        adaptive("writing-story", "writing", "Story"),
        adaptive("writing-script", "writing", "Script"),

        adaptive("comics-comic-page", "comics", "Comic Page"),
        adaptive("comics-storyboard", "comics", "Storyboard"),

        adaptive("interiors-mood-board", "interiors", "Interior Mood Board"),
        adaptive("interiors-room-concept", "interiors", "Room Concept"),
        adaptive("architecture-concept-board", "architecture", "Architecture Concept Board"),
        adaptive("product-concept", "product", "Product Concept"),
        adaptive("product-spec-sheet", "product", "Product Sheet"),

        adaptive("ceramics-collection", "ceramics", "Ceramic Collection"),
        adaptive("ceramics-glaze-board", "ceramics", "Glaze Board"),
        adaptive("ceramics-firing-log", "ceramics", "Firing Log"),
        adaptive("jewellery-collection", "jewellery", "Jewellery Collection"),
        adaptive("crafts-project-board", "crafts", "Making Project"),

        adaptive("education-assignment", "education", "Assignment"),
        adaptive("education-revision", "education", "Revision Pack"),
        adaptive("education-presentation", "education", "Study Presentation"),
        adaptive("business-plan", "business", "Business Plan"),
        adaptive("business-pitch", "business", "Pitch Deck"),
        adaptive("events-plan", "events", "Event Plan"),
        adaptive("events-invitation", "events", "Event Invitation"),
        adaptive("portfolio-creator", "portfolio", "Creator Portfolio"),

        specialist("music-track", "music", "Music Track", CreatorStudioDestination.MUSIC),
        specialist("music-recording", "music", "Recording", CreatorStudioDestination.MUSIC),
        specialist("music-mix", "music", "Mix", CreatorStudioDestination.MUSIC),
        specialist("music-dj-set", "music", "DJ Set", CreatorStudioDestination.MUSIC),

        specialist("video-edit", "video", "Video Edit", CreatorStudioDestination.VIDEO),
        specialist("video-film", "video", "Film", CreatorStudioDestination.VIDEO),
        specialist("video-reel-short", "video", "Reel / Short", CreatorStudioDestination.VIDEO),
        specialist("video-documentary", "video", "Documentary", CreatorStudioDestination.VIDEO),
        specialist("video-music-video", "video", "Music Video", CreatorStudioDestination.VIDEO),
    )

    fun projectType(id: String): CreatorProjectType? =
        projectTypes.firstOrNull { it.id == id }

    fun projectsForWorld(worldId: String): List<CreatorProjectType> =
        projectTypes.filter { it.worldId == worldId }

    private fun adaptive(
        id: String,
        worldId: String,
        label: String,
    ) = CreatorProjectType(
        id = id,
        worldId = worldId,
        label = label,
        destination = CreatorStudioDestination.ADAPTIVE,
        workspaceConfigId = id,
    )

    private fun specialist(
        id: String,
        worldId: String,
        label: String,
        destination: CreatorStudioDestination,
    ) = CreatorProjectType(
        id = id,
        worldId = worldId,
        label = label,
        destination = destination,
        workspaceConfigId = null,
    )
}

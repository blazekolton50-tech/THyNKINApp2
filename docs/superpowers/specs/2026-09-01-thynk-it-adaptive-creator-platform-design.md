# THyNK-IT Adaptive Creator Platform — Design Specification

Date: 2026-09-01
Status: Approved direction, implementation not yet started
Branch: `chatgpt/codex-ready-2026-09-01`
PR: #41 remains Draft and unmerged

## 1. Product idea

THyNK becomes one creative/productivity ecosystem with a shared project engine underneath and adaptive specialist workspaces on top.

The user should not have to learn a different app for every creative discipline. They choose what they want to make, and THyNK presents the tools, panels, layouts, templates, sizes, colours, fonts and workflows relevant to that job.

Core flow:

`THyNK -> What do you want to make? -> Choose project type -> Adaptive workspace -> Create -> Autosave -> Share / Showcase / Collaborate`

The shared app architecture remains native Kotlin/Jetpack Compose with the existing Supabase, auth/security, Camera, Media3, navigation and persistence boundaries. React/Tailwind donor implementations are reference/content sources only and must not become a second production runtime.

## 2. Top-level studio structure

### THyNK-IT

THyNK-IT is the umbrella adaptive creator/productivity environment for work that can share the common canvas/page/project engine.

Primary Creator Worlds:

- Design & Graphics
- Art & Illustration
- Office Studio
- Publishing
- Journalism & Newsroom
- Fashion & Textiles
- Photography
- Social Content
- Branding & Advertising
- Books & Writing
- Comics & Storytelling
- Interior Design
- Architecture Concepts
- Product Design
- Pottery & Ceramics
- Jewellery
- Crafts & Making
- Education & Study
- Business & Entrepreneurship
- Events
- Portfolio / Showcase creation

The list is intentionally extensible. New subjects should normally be added as workspace configurations rather than separate editor engines.

### THyNK Music

THyNK Music is a separate specialist environment because playback, recording, multitrack editing and audio processing require a dedicated timeline/audio engine.

THyNK Music owns both music playback and music creation/editing.

Planned areas include:

- Music player / library
- Create Music
- Recording
- Multitrack editor / DAW
- DJ Studio
- Mixer
- EQ
- Effects
- Loops & Samples
- Stems
- Sound Design / SFX
- Lyrics & Vocals
- Mastering
- Album / single artwork handoff to THyNK-IT
- Release preparation

DJ Studio lives inside THyNK Music, not as a separate top-level product.

Any control that depends on an unavailable provider or unfinished native audio capability must stay disabled or `NOT_CONFIGURED`; demo behavior must never be presented as production functionality.

### THyNK Video

THyNK Video is a separate specialist environment because video playback and editing require a dedicated Media3/timeline workflow.

THyNK Video owns both video playback and video creation/editing.

Planned areas include:

- Video player / library
- Camera/import handoff
- Timeline editor
- Film
- Reels / Shorts
- Music video
- Documentary
- Animation / motion layouts
- Clips and trimming
- Multi-track timeline
- Titles
- Captions / subtitles
- Transitions
- Effects
- Speed
- Filters
- Colour tools
- Audio tracks
- Export

A Reel or other video project selected from Social Content should hand off to THyNK Video automatically while preserving the same project/account/autosave experience.

## 3. Shared adaptive engine

All THyNK-IT Creator Worlds share the same underlying project/canvas system where possible.

Shared capabilities include:

- project identity and ownership
- canvas/page model
- layers
- object transforms
- text
- images
- shapes/elements
- colours
- fonts
- grids/guides
- templates
- undo/redo
- version history when available
- autosave/restore
- uploads/assets
- project library
- export/share boundaries
- collaboration metadata
- Patsy assistance
- Showcase metadata

The engine is stable; the workspace configuration changes.

A workspace configuration defines:

- visible tools
- tool order
- left/right panel composition
- canvas/page mode
- default dimensions
- template families
- colour libraries
- typography libraries
- subject-specific object types
- beginner shortcuts
- advanced controls
- project metadata
- export targets
- Patsy helper prompts

## 4. Adaptive workspace examples

### Fashion Design

Entry examples:

- Design a Collection
- Mood Board
- Garment Sketch
- Technical Flat
- Fabric Board
- Colour Story
- Lookbook
- Line Sheet
- Portfolio

Workspace tools can expose:

- Sketch
- Garments
- Silhouettes
- Fabric / Texture
- Swatches
- Colour Story
- Text / Labels
- Measurements
- Stitch / Detail notes
- Reference images
- Uploads

Selecting a garment can reveal relevant properties such as fabric, colour, pattern, sleeve, collar, fastening, measurements and construction notes.

### Publishing / Magazine

Entry examples:

- Fashion Magazine
- Music Magazine
- School Magazine
- Culture
- Art
- Gaming
- Sports
- Travel
- Food
- Community Magazine
- Independent Zine
- Portfolio Magazine

Workspace changes to publication mode with:

- pages and spreads
- page thumbnails
- master pages
- columns
- grids
- margins
- bleed
- guides
- page numbering
- headers/footers
- mastheads
- headlines
- pull quotes
- image captions
- editorial typography

Typical structure:

`Cover -> Contents -> Editor's Letter -> Features -> Interviews -> Photography -> Reviews -> Back Page`

### Journalism / Newspaper

Workspace can expose:

- front-page grids
- article drafting
- headline/deck/byline fields
- image captions
- interview notes
- source/reference notes
- local news layouts
- opinion layouts
- sports/culture sections
- investigative story boards
- print/newsletter handoff

### Art Studio

Entry examples:

- Drawing
- Painting plan
- Illustration
- Digital art
- Concept art
- Collage
- Comic page
- Character sheet
- Exhibition board
- Portfolio
- Pottery / ceramic collection board
- Jewellery / craft concept board

Tool visibility should favour brushes/drawing, references, palettes, layers, textures, frames and presentation tools rather than office/publication controls.

### Office Studio

Entry examples:

- Document
- Letter
- CV / Resume
- Report
- Proposal
- Invoice
- Form
- Presentation
- Meeting notes
- Business plan
- School assignment
- Worksheet
- Planner
- Calendar

The workspace should feel productivity-first: pages, headings, tables, structured blocks, print/export sizes and document formatting rather than art-first controls.

### Social Content

Entry examples:

- Instagram Post
- Story
- Facebook Post
- TikTok Graphic
- YouTube Thumbnail
- LinkedIn Post
- Carousel
- Reel / Short Video

Static/social graphics use the adaptive THyNK-IT design editor with simplified tools:

- Templates
- Photo
- Text
- Stickers
- Brand Kit
- Background
- Filters
- Effects
- Resize

Carousel mode uses multiple ordered pages with duplicate/reorder/apply-style-to-all behavior.

Reel/Short Video routes into THyNK Video rather than duplicating video editing inside the canvas editor.

## 5. Progressive complexity

The same workspace should work for a first-time creator and an experienced professional.

Default behavior:

- beginner view exposes the most useful controls for the chosen project
- advanced controls remain accessible underneath
- avoid presenting every tool at once
- do not remove professional capability merely to make the UI simple

The goal is simple on the surface, powerful underneath.

## 6. Template system

Templates must be organised by Creator World, project type and format rather than one giant flat catalogue.

Template families may include:

- social
- posters/flyers
- branding
- portfolios
- magazines
- newspapers
- books/zines
- presentations
- office documents
- CVs
- fashion collections
- lookbooks
- mood boards
- school work
- art portfolios
- product sheets
- event materials
- business materials

Production templates must fail closed until actual asset bytes, provenance/licensing, format, checksum and supersession state are verified.

React/Tailwind template libraries can be authored rapidly as donor/reference implementations, but must be explicitly marked donor/reference until ported and asset-verified.

## 7. Colour system

Provide both universal and subject-specific colour resources.

Universal:

- colour wheel / picker
- HEX/RGB/HSL entry where appropriate
- saved colours
- recent colours
- project palette
- gradients
- accessible contrast guidance

Curated palette libraries can include:

- editorial
- fashion seasons
- luxury
- streetwear
- natural/earth
- ceramic/glaze-inspired
- corporate
- education
- youth
- retro
- minimal
- neon
- monochrome
- cinematic
- social trends

Subject packs are suggestions, not restrictions.

## 8. Typography system

Typography should be treated as a serious creator resource rather than a short font dropdown.

Libraries can organise fonts and pairings by use:

- editorial
- newspaper
- fashion
- luxury
- corporate
- playful
- handwritten
- display
- body copy
- accessibility/readability
- posters
- presentations
- social content
- portfolios

Provide pairing presets and role-based typography such as:

- Masthead
- Headline
- Subheading
- Body
- Caption
- Pull Quote
- Label

Any bundled font must have verified licensing before production distribution.

## 9. Patsy inside creation

Patsy should assist in context rather than behave as a generic chatbot overlay.

Examples:

- Fashion: identify missing collection elements or help organise a lookbook.
- Magazine: suggest a contents page from existing article titles.
- Journalism: help structure interview questions or article sections while preserving factual/source boundaries.
- Office: help format a CV/report/proposal.
- Art: suggest composition or presentation options without overwriting the creator's authorship.
- Social: help adapt a finished design to another format.
- Music/Video: guide users toward the correct specialist tools.

Patsy suggestions must never fabricate provider success, factual sourcing, export completion or production media processing.

## 10. Creator Showcase and collaboration

THyNK should make it possible for emerging creators to be discovered without already having a large following.

Project metadata should be able to carry:

- Creator World / discipline
- skills used
- project type
- tools used
- collaboration status
- feedback permission
- remix permission
- visibility
- age/privacy rules

Discovery should support discipline/skill/project relevance, not only raw follower count.

Future capabilities can include:

- portfolios
- project pages
- constructive feedback
- collaboration requests
- challenges
- featured emerging creators
- mentoring/apprenticeship structures where safe and appropriate

Under-16 privacy/safety rules remain authoritative and must be designed before public collaboration features are enabled for minors.

## 11. Routing rule

The project type determines the correct editor automatically.

Examples:

- Fashion collection -> THyNK-IT adaptive Creator Editor / Fashion configuration
- Magazine -> THyNK-IT adaptive Creator Editor / Publishing configuration
- CV -> THyNK-IT adaptive Creator Editor / Office configuration
- Instagram Post -> THyNK-IT adaptive Creator Editor / Social configuration
- Reel -> THyNK Video
- Music track -> THyNK Music
- DJ Set -> THyNK Music / DJ Studio

The user should experience a seamless transition; internal engine boundaries should not make the app feel fragmented.

## 12. Production architecture rule

Native Android remains authoritative.

- Kotlin / Jetpack Compose production UI
- Supabase remains backend authority
- existing auth/security/Owner/age gates remain
- existing shared navigation remains
- Media3 remains video playback/editing foundation
- native Camera foundation remains
- native Studio state/core should be extended rather than replaced

React/Tailwind may be used extensively for:

- donor workspace prototypes
- template layouts
- colour libraries
- font pairings
- subject catalogues
- interaction references
- component references
- quick experimentation

It must not be embedded as a WebView or become a parallel production app runtime.

## 13. Non-goals / truth boundaries

Do not:

- create a second editor engine for each Creator World
- place hundreds of tools on one screen
- duplicate video editing inside Social
- duplicate audio editing outside THyNK Music
- fake AI generation
- fake export completion
- fake asset verification
- fake collaboration/realtime state
- weaken auth/age/privacy gates
- redesign locked global navigation

## 14. Success criteria

The architecture succeeds when:

1. Users can begin by describing/selecting what they want to make rather than choosing software concepts.
2. The editor visibly changes to match the chosen discipline while retaining a familiar underlying interaction model.
3. Music and Video have specialist editors without making the overall app feel fragmented.
4. New Creator Worlds can be added mostly by defining configurations, templates and subject tools rather than rebuilding the engine.
5. Beginners are not overwhelmed and advanced creators are not artificially limited.
6. Projects can move into portfolio/showcase/collaboration workflows without changing ownership, account or project identity.
7. React/Tailwind donor work can accelerate content design while native Android remains the production source of truth.

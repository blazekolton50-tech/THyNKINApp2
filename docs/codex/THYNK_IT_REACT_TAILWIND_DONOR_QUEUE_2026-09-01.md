# THyNK-IT React/Tailwind Donor Queue — 1 September 2026

Status: reference-content production queue; not native production runtime
Branch: `chatgpt/codex-ready-2026-09-01`
PR #41 remains Draft/unmerged.

## Rule

React/Tailwind work is for fast visual/content prototyping and donor layouts. It may define workspace compositions, template geometry, palette metadata, typography roles and interaction references. It must not be shipped as a WebView or second production editor. Every donor asset/layout starts `REFERENCE_ONLY` until native port and asset/license verification.

## Pack 1 — Fashion Studio

Build donor screens/components for:

1. Fashion hub
2. Design a Collection workspace
3. Mood Board workspace
4. Garment Sketch workspace
5. Technical Flat workspace
6. Fabric Board workspace
7. Colour Story workspace
8. Lookbook workspace
9. Line Sheet workspace
10. Fashion Portfolio workspace

### Template families

- 12 collection boards
- 12 mood boards
- 12 lookbook spreads
- 10 line sheets
- 10 garment presentation cards
- 10 fashion portfolio pages
- 8 fabric/swatches boards
- 8 colour-story boards

### Palette groups

- luxury neutral
- streetwear dark
- spring pastel
- summer bright
- autumn earth
- winter jewel
- monochrome
- denim/workwear
- sportswear energy
- sustainable/natural

### Typography roles

Every fashion template uses role metadata rather than hardcoded “random font” choices:

- Collection title
- Look name
- Section heading
- Body/notes
- Fabric label
- Measurement label
- Caption

## Pack 2 — Publishing / Magazine Studio

Build donor screens/components for:

1. Publication hub
2. Magazine page editor
3. Spread editor
4. Page overview / thumbnail strip
5. Master-page reference
6. Column/grid panel
7. Cover builder
8. Contents-page builder
9. Feature article layout
10. Interview layout

### Template families

- 12 fashion magazine covers
- 12 music magazine covers
- 10 culture magazine covers
- 10 art magazine covers
- 10 school/community magazine covers
- 12 editorial spreads
- 10 interview spreads
- 10 photo-story spreads
- 8 contents pages
- 8 editor-letter pages
- 8 review pages
- 6 back-page layouts
- 10 independent zine layouts

### Publication styles

- luxury editorial
- indie zine
- newspaper-modern
- youth culture
- music
- art gallery
- fashion
- clean minimal
- bold experimental
- community/local

## Pack 3 — Office Studio

Build donor screens/components for:

1. Office hub
2. Document editor
3. CV editor
4. Report editor
5. Proposal editor
6. Invoice editor
7. Presentation editor
8. Worksheet editor
9. Planner editor
10. Business-plan editor

### Template families

- 20 CV/resume layouts
- 12 cover letters
- 12 reports
- 10 proposals
- 10 invoices
- 12 business plans
- 20 presentation themes
- 12 worksheets
- 10 meeting-note layouts
- 10 planners/calendars

### Office style packs

- clean professional
- creative professional
- student/graduate
- executive
- modern minimal
- accessible/high-legibility
- education
- startup

## Pack 4 — Art Studio

Build donor screens/components for:

1. Art hub
2. Drawing workspace
3. Illustration workspace
4. Concept-art board
5. Collage workspace
6. Character sheet
7. Comic/storyboard page
8. Exhibition board
9. Artist portfolio
10. Reference/palette workspace

### Template families

- 12 character sheets
- 12 concept boards
- 12 exhibition boards
- 16 portfolio pages
- 10 comic page grids
- 10 storyboard sheets
- 10 collage starters
- 10 art-process/case-study pages

### Palette packs

- painterly earth
- high contrast
- muted editorial
- comic primary
- neon digital
- monochrome ink
- cinematic concept
- soft illustration

## Pack 5 — Social Content

Build donor screens/components for:

1. Social hub
2. Instagram Post workspace
3. Story workspace
4. Carousel workspace
5. YouTube thumbnail workspace
6. LinkedIn post workspace
7. Brand social pack workspace
8. Reel handoff card -> THyNK Video

### Template families

- 30 Instagram posts
- 20 Stories
- 20 carousel sets (5-8 pages each)
- 20 YouTube thumbnails
- 12 LinkedIn/business layouts
- 20 announcements
- 20 quotes
- 20 educational/tip cards
- 12 product promos
- 12 portfolio/social showcase layouts

### Social style packs

- minimal
- neon
- editorial
- bold type
- creator portfolio
- business
- youth
- retro
- monochrome
- colourful geometric

## Pack 6 — Journalism / Newspaper

Build donor screens/components for:

1. Newsroom hub
2. Newspaper front page
3. Article editor
4. Feature editor
5. Interview workspace
6. Opinion page
7. Sports page
8. Culture page
9. Investigative board
10. Newsletter layout

### Template families

- 12 front pages
- 12 news article pages
- 10 feature pages
- 10 interview pages
- 8 opinion pages
- 8 sports pages
- 8 culture pages
- 8 newsletters

Donor layouts may demonstrate source-note fields but must never invent factual sources or imply verification.

## Pack 7 — Pottery / Ceramics

Build donor screens/components for:

1. Ceramics hub
2. Vessel sketch board
3. Collection board
4. Glaze board
5. Clay/firing log
6. Product sheet
7. Maker portfolio
8. Market/catalogue page

### Template families

- 12 collection boards
- 12 glaze boards
- 10 firing logs
- 10 product sheets
- 12 maker portfolio pages
- 8 craft-market/catalogue pages

Palette metadata can use glaze-inspired colour naming as visual reference but must not imply real glaze chemistry/firing outcomes.

## Pack 8 — Sewing & Textiles

Build donor screens/components for:

1. Sewing hub
2. Sewing project board
3. Measurement sheet
4. Pattern-note board
5. Fabric plan
6. Alteration plan
7. Construction-step sheet
8. Sewing portfolio

### Template families

- 12 project boards
- 10 measurement sheets
- 10 pattern-note layouts
- 10 fabric boards
- 10 alteration plans
- 10 construction sheets
- 10 portfolio pages

## Shared colour donor library

Create metadata/examples for at least 20 palettes per family, initially reference-only:

- editorial
- fashion-seasonal
- luxury
- streetwear
- earth
- ceramic-glaze
- corporate
- education
- youth
- retro
- minimal
- neon
- monochrome
- cinematic
- social-trend

Each palette record should carry:

- stable ID
- name
- 4-8 colour values
- tags
- suitable worlds/project types
- contrast notes where relevant
- `REFERENCE_ONLY` verification state

## Shared typography donor library

Create pairing metadata/examples for at least 10 pairings per family:

- editorial
- newspaper
- fashion
- luxury
- corporate
- playful
- handwritten
- display
- body
- accessible
- poster
- presentation
- social
- portfolio

Each pairing record should carry:

- stable ID
- role mapping
- font-family references
- weights
- size-scale suggestion
- line-height suggestion
- suitable worlds
- license status
- production-ready boolean

No font is bundled until its redistribution license is verified.

## Shared template metadata contract for donor content

Every donor template should be describable by:

- `id`
- `worldId`
- `projectTypeId`
- `familyId`
- `title`
- `format`
- `width`
- `height`
- `pageCount`
- `tags`
- `paletteFamilyId`
- `typographyFamilyId`
- `previewStatus`
- `verification = REFERENCE_ONLY`
- `nativePortStatus`

## First implementation priority

When coding resumes, do not attempt every pack at once.

1. Implement adaptive workspace contracts/catalogue/routing.
2. Wire three proving workspaces: Fashion Collection, Publishing Magazine, Social Instagram Post.
3. Add Office CV and Art Illustration as second proving pair.
4. Generate corresponding React/Tailwind donor layouts/palettes/type metadata in isolated reference folders.
5. Port only the strongest verified interactions/layout structures into native Compose.
6. Expand world-by-world after the shared contract proves stable.

This queue is intentionally broad at the content level and narrow at the implementation level so THyNK-IT can eventually cover a very large creator universe without turning the first native slice into an unreviewable rewrite.

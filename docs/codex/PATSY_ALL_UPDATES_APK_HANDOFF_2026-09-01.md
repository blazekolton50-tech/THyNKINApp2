# THyNK-IN — ALL Updates / APK Handoff — 2026-09-01

## Authority

This file is the newest high-priority Codex handoff for PR #41.

**Canonical product name — binding:** the app/global product brand is **THyNK-IN**. Do not call the product “Patsy App.” **Patsy** remains the assistant/character name for Patsy-specific Rive, animation, personality, PawMoji, companion and reference-asset work. This file keeps its legacy filename only so existing references do not break.

Repository: `blazekolton50-tech/PatsyApp` (legacy repository slug)
Branch: `chatgpt/codex-ready-2026-09-01`
PR: #41 — KEEP DRAFT AND UNMERGED

Codex must always report the exact current HEAD before changing production code. The inspected code snapshot immediately before this handoff was `eead11a39b71635f9ab9b84a5eacc443db04cd5c`; do not assume that remains HEAD after documentation/CI persistence commits.

Production remains native Kotlin + Jetpack Compose. Supabase remains backend/security authority. React/Tailwind is donor/reference content only and must never become a production WebView or second app runtime.

## Read order

1. `docs/codex/PATSY_ALL_UPDATES_APK_HANDOFF_2026-09-01.md`
2. `docs/codex/CODEX_NEXT_INSTRUCTION.txt`
3. `docs/codex/PATSY_CURRENT_BUILD_STATUS_2026-09-01.md`
4. `docs/codex/PATSY_PAGE_VISUAL_LOCK_2026-09-01.md`
5. `docs/superpowers/specs/2026-09-01-thynk-it-adaptive-creator-platform-design.md`
6. `docs/superpowers/plans/2026-09-01-thynk-it-adaptive-creator-foundation.md`
7. `docs/codex/THYNK_IT_CREATOR_WORLD_MATRIX_2026-09-01.md`
8. `docs/codex/THYNK_IT_REACT_TAILWIND_DONOR_QUEUE_2026-09-01.md`
9. existing full-studio reconciliation/capability matrices and older navigation/security locks where they do not conflict with this file.

If an older document says to preserve a flat ten-category THyNK structure as the final product model, this newer THyNK-IT adaptive architecture supersedes that structural wording while preserving the working native code and stable routes needed during migration.

---

## 1. TWO NAVIGATION LAYERS — DO NOT CONFUSE THEM

### Global THyNK-IN app navigation

Semantic routes remain:

`HOME · THyNK · CAMERA · PATSY DMS · PROFILE`

Visible app-wide bar remains:

`Home · [coloured THyNK mark only] · [large + only] · PDMs · Profile`

Rules:
- centre `+` remains Camera semantically;
- no CAMERA caption;
- preserve the approved thin rainbow separator / raised centre arch;
- keep the authenticated global bar available through nested app pages unless a verified lock explicitly says otherwise;
- do not weaken route, age or Owner authorization.

### THyNK Panel — THyNK-area navigation

THyNK pages additionally use the locked five-button THyNK Panel in this exact left-to-right order:

1. `THyNK-ME`
2. `THyNK Chats`
3. `THyNK-IN`
4. `THyNK Music`
5. `THyNK-IT`

`THyNK-IN` is the centre THyNK Panel destination, opens the social/news-feed area, and is the app/global product brand.

The THyNK Panel is NOT permission to replace the global app navigation contract or its security semantics. Treat the two bars/contracts separately.

Current source contract separation is represented by `FinalVisualContract.primaryNavigation*` for global navigation and `FinalVisualContract.thynkPanelDisplayLabels` for the THyNK-area panel.

---

## 2. THyNK PAGE HEADER LOGOS — LOCKED

Each main THyNK destination page must show its matching approved logo at the top:

- THyNK-ME logo → profile / THyNK-ME destination
- THyNK Chats logo → messaging / chats destination
- THyNK-IN logo → social/news-feed destination
- THyNK Music logo → Music destination
- THyNK-IT logo → adaptive creator/productivity destination

Use approved supplied logo assets. Do not recreate the logos as plain text, generic SVG approximations or substituted fonts.

---

## 3. THyNK RAINBOW VISUAL SYSTEM + SETTINGS TOGGLE

THyNK-area pages use the same rainbow spectrum/language as the approved new THyNK Panel/logos, on the dark/black base with white primary controls/text.

Use the shared rainbow language consistently for restrained borders, active states, glows, section accents, dividers and progress where appropriate. Do not create unrelated rainbow palettes for different THyNK pages.

Settings must eventually expose a simple THyNK Panel rainbow active-state toggle:

- ON: currently selected THyNK Panel destination has the approved persistent rainbow-lit active state.
- OFF: THyNK Panel buttons remain plain white with no persistent rainbow-lit active state.
- one press toggles off; next press toggles back on.

Until the real setting persistence is wired, do not fake saved preference success. Implement this as its own tested preference slice.

---

## 4. THyNK PRODUCT STRUCTURE — NEW LOCK

### THyNK-IT

`THyNK-IT` is the umbrella adaptive creator/productivity environment.

One shared native project/canvas engine sits underneath. The screen, visible tools, panels, layout, defaults, templates, colours, typography and helper actions adapt to what the user chooses to make.

Core user flow:

`THyNK-IT → choose what to make → choose project type → adaptive workspace → create → autosave → share/showcase/collaborate`

Do NOT create a separate editor engine for every subject.

### THyNK Music

THyNK Music stays a specialist environment and owns BOTH music playback and music creation/editing.

Includes/targets:
- music player/library;
- recording;
- multitrack/DAW editor;
- mixer/EQ/effects;
- loops/samples/stems;
- lyrics/vocals;
- mastering only when real provider/engine exists;
- DJ Studio inside THyNK Music;
- album/single artwork can hand off to THyNK-IT.

DJ Studio is not a separate top-level app.

### THyNK Video

THyNK Video stays a specialist environment and owns BOTH video playback and video creation/editing.

Includes/targets:
- video player/library;
- Camera/import handoff;
- timeline editor;
- film/documentary/music-video workflows;
- Reels/Shorts;
- clips/trim/split;
- titles/captions;
- transitions/effects/speed/filters/colour;
- audio tracks;
- real export only when real Android bytes are produced.

A Social Reel selected from THyNK-IT must route into THyNK Video instead of duplicating a video engine inside Social.

---

## 5. THyNK-IT CREATOR WORLDS

Current native catalogue now seeds these Creator Worlds:

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
- Portfolio & Showcase
- THyNK Music
- THyNK Video

The catalogue also seeds real stable project IDs for Fashion collections/moodboards/garment sketches/technical flats/fabric boards/lookbooks/line sheets/sewing projects; Publishing magazines/newspapers/zines/catalogues/books; Office CVs/reports/proposals/invoices/presentations/worksheets/planners; Art drawing/illustration/concept/collage/comics/character sheets/portfolios; Journalism; Social posts/stories/carousels/thumbnails/LinkedIn/Reels; Photography; Branding; Writing; Interiors; Architecture; Product; Ceramics; Jewellery; Crafts; Education; Business; Events; Portfolios; Music; and Video.

Keep this extensible via configuration rather than giant conditional UI files.

---

## 6. ADAPTIVE WORKSPACE NATIVE PROGRESS

Already added natively on PR #41:

- `CreatorWorkspaceModels.kt`
  - `CreatorStudioDestination { ADAPTIVE, MUSIC, VIDEO }`
  - `CreatorCanvasMode { FREEFORM, PAGE, SPREAD, DOCUMENT, SOCIAL }`
  - `CreatorWorld`
  - `CreatorProjectType`
  - `WorkspaceConfig`

- `CreatorWorkspaceCatalog.kt`
  - Creator Worlds
  - stable project-type IDs
  - adaptive vs Music vs Video destinations

- `CreatorWorkspaceConfigs.kt`
  - first concrete adaptive configurations for:
    - Fashion Collection
    - Magazine
    - CV / Resume
    - Illustration
    - Instagram Post
  - each carries native `StudioMode`, canvas mode, default dimensions, ordered shared tool IDs, beginner shortcuts and template/palette/typography family IDs.

These configs deliberately reuse existing `StudioToolCatalog` IDs instead of creating a second editor engine.

NEXT adaptive-foundation slices:
1. `CreatorWorkspaceTools.kt` — resolve ordered config tools against `StudioToolCatalog`, fail closed on unknown IDs.
2. `CreatorWorkspaceRouting.kt` — project type → ADAPTIVE / MUSIC / VIDEO destination, preserve legacy route compatibility.
3. `CreatorResourceCatalog.kt` — template/palette/typography metadata with verification state; donor/reference is NOT production-ready.
4. `CreatorWorkspacePresentation.kt` — pure presentation model for headers/sections/shortcuts/tool groups.
5. carry `WorkspaceConfig` through native THyNK routing into the actual Design/Creator editor screen.
6. then add Creator World/project picker UI without replacing existing editor state.

---

## 7. THyNK DESIGN — CURRENT REAL NATIVE PROGRESS

Do not roll back the current Design work.

Already implemented/tested on this branch includes:
- shared canvas reducer/state;
- persistence mapper/snapshot support;
- layers selection/visibility/lock/reorder;
- duplicate/stacking/delete;
- X/Y quick movement;
- rotation quick edits;
- opacity quick edits;
- W/H resize quick edits using the real Resize reducer;
- single-object align LEFT / H-CENTRE / RIGHT / TOP / V-CENTRE / BOTTOM;
- Position panel presenter;
- Opacity panel presenter;
- actual Compose Design screen wiring for Position, Size, Alignment and Opacity;
- stable Design entry mapping for Blank, Custom Size and template kinds.

Still real work, not completed by labels alone:
- carry `DesignEntryPoint`/Creator workspace metadata fully through actual editor route;
- rulers/guides/grid/snap behavior;
- distribution requires an explicit multi-selection model before claiming it;
- deeper Text/Elements/Stickers/Draw/Frames/Background behavior where currently partial;
- verified template catalogue/assets;
- real native render/export path.

Production template assets remain fail-closed until bytes, provenance/licensing, checksum/identity and supersession are verified.

---

## 8. REACT / TAILWIND DONOR CONTENT

React/Tailwind may be created aggressively as DESIGN/CONTENT DONOR material for:
- Fashion workspaces;
- magazines/newspapers/zines;
- Art Studio;
- Office Studio;
- journalism;
- sewing/textiles;
- pottery/ceramics;
- social posts/carousels;
- typography systems;
- colour/palette libraries;
- templates;
- component/layout references.

It remains REFERENCE_ONLY until ported into native Android and any bundled assets/fonts are verified/licensed.

Never embed the donor React app as a WebView to shortcut the native implementation.

---

## 9. PROFILE / MENU / DMS

Profile and its dropdown/menu composition are owner-approved/locked. Do not redesign them simply because older docs call them partial. Only complete/bind missing real-data/backend behavior while preserving the approved composition.

Patsy DMs / THyNK Chats must use real Supabase-backed users/messages/membership/realtime/retention/age/privacy contracts. Call/video controls remain `NOT_CONFIGURED` until a real secure provider is present.

---

## 10. PATSY / RIVE

Native host/runtime contracts exist. Final production Patsy `.riv` is not yet verified as a complete authored/device-tested production rig.

Never substitute GIFs, sprites, static pose swaps, fake bobbing or a fake `.riv`.

---

## 11. APK READINESS GATE

An APK may be generated for testing when the exact final HEAD has:

- full unit test suite GREEN;
- debug APK build GREEN;
- release variant GREEN;
- integration scripts GREEN;
- debug APK artifact uploaded;
- Android Studio project ZIP generated/uploaded;
- no known compile/test regression at the final head.

That means `BUILDABLE TEST APK`, not `PRODUCTION COMPLETE`.

Physical-device gates remain necessary for production confidence, especially:
- login → restore → account bootstrap → Owner authorization with real Auth/user rows;
- external Camera app photo/video return URI behavior;
- Media3 real media playback/handoff;
- visual QA of global nav and THyNK Panel across target device sizes;
- THyNK Panel rainbow toggle once implemented;
- real storage/autosave restore;
- real DMs/realtime/retention;
- production Rive rig when available;
- real export pipelines.

Do not merge PR #41 automatically after a green APK build.

---

## 12. NEXT CODE ORDER FROM THE CURRENT STATE

Use TDD and stop on genuine failure.

1. Keep the global app nav and THyNK Panel separate; finish THyNK Panel UI/routing on relevant THyNK pages and matching header logos without weakening global routes.
2. Finish the THyNK-IT adaptive foundation: tool resolver → unified routing → resource verification metadata → presentation model → editor integration.
3. Make Fashion Collection, Magazine, CV, Illustration and Instagram Post visibly open with materially different native tool/panel configurations on the shared editor.
4. Route Social Reel to THyNK Video and DJ Set to THyNK Music.
5. Continue professional Design behavior: editor-entry carry-through, rulers/guides/grid/snap, then other genuine shared-canvas tools.
6. Continue Photo/Image shared-canvas extensions.
7. Continue THyNK Video on existing Camera + Media3; never fake export.
8. Continue THyNK Music player + editor + DJ Studio on genuine audio state/processing; never fake waveforms/provider success.
9. Implement verified template/palette/font content; donor/reference metadata must stay visibly non-production until verified.
10. Finish account-scoped autosave/project persistence, real DMs, Put-Away Portal, settings/preferences, under-16/provider slices and physical-device QA.
11. Integrate final production Patsy Rive only after the authored `.riv` is verified.

At every code boundary report starting SHA, ending SHA, changed files, RED evidence, GREEN targeted tests, full tests, debug build, release build, final-head Actions run, artifact IDs if produced, and remaining device/provider limitations.

# THyNK-IT Adaptive Creator Foundation Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build the native configuration layer that lets THyNK-IT reshape one shared editor around the chosen Creator World, while routing Music and Video into their existing specialist environments.

**Architecture:** Keep the existing Kotlin/Jetpack Compose Studio core, canvas reducer, Supabase authority, shared navigation, Camera and Media3 boundaries. Add a data-driven `CreatorWorld` / `CreatorProjectType` / `WorkspaceConfig` layer that resolves visible tools, document mode, starter size, template-family IDs, palette-family IDs and typography-family IDs. Music and Video remain specialist destinations; this plan only adds routing contracts into them and does not duplicate their editor engines.

**Tech Stack:** Kotlin, Jetpack Compose, existing `StudioEditorState`, `StudioToolCatalog`, `StudioCanvasState`, JUnit, existing Patsy Consolidation CI.

**Spec:** `docs/superpowers/specs/2026-09-01-thynk-it-adaptive-creator-platform-design.md`

## Global Constraints

- Production UI remains native Kotlin / Jetpack Compose.
- React/Tailwind is donor/reference content only; never embed it as a production WebView/runtime.
- Supabase/auth/security/Owner/age gates remain authoritative.
- Existing shared authenticated navigation remains unchanged.
- THyNK Music owns music playback/editing; DJ Studio lives inside THyNK Music.
- THyNK Video owns video playback/editing; Reels/Shorts route to THyNK Video.
- Never surface unverified production templates, fonts or media assets as ready/verified.
- Provider-dependent actions remain disabled or `NOT_CONFIGURED` until real native/provider support exists.
- Use RED/GREEN TDD, fresh debug/release builds, exact final-head CI evidence and keep PR #41 Draft/unmerged.

---

## File structure

Create focused configuration files rather than expanding `ThynkStudioScreen.kt` or `ThynkDesignEditorScreen.kt` into more monoliths:

- `app/src/main/java/com/patsy/app/thynk/CreatorWorkspaceModels.kt` — stable IDs and configuration data contracts.
- `app/src/main/java/com/patsy/app/thynk/CreatorWorkspaceCatalog.kt` — built-in Creator Worlds and starter project types.
- `app/src/main/java/com/patsy/app/thynk/CreatorWorkspaceRouting.kt` — project type -> adaptive/music/video destination resolution.
- `app/src/main/java/com/patsy/app/thynk/CreatorWorkspaceTools.kt` — resolves ordered visible tools against `StudioToolCatalog`.
- `app/src/main/java/com/patsy/app/thynk/CreatorResourceCatalog.kt` — palette/font/template family metadata and verification state only.
- `app/src/main/java/com/patsy/app/thynk/CreatorWorkspacePresentation.kt` — pure presentation model for headers, sections, beginner shortcuts and tool groups.
- Modify `app/src/main/java/com/patsy/app/thynk/ThynkEditorRouting.kt` only to delegate to the new destination resolver while preserving current Design/Video compatibility.
- Modify `app/src/main/java/com/patsy/app/thynk/ThynkStudioScreen.kt` only after the pure models are green.
- Modify `app/src/main/java/com/patsy/app/thynk/ThynkDesignEditorScreen.kt` only to consume a `WorkspaceConfig`; do not rebuild the canvas engine.

Tests live under `app/src/test/java/com/patsy/app/thynk/` with one focused test file per production unit.

---

### Task 1: Creator workspace contracts

**Files:**
- Create: `app/src/main/java/com/patsy/app/thynk/CreatorWorkspaceModels.kt`
- Test: `app/src/test/java/com/patsy/app/thynk/CreatorWorkspaceModelsTest.kt`

**Interfaces:**
- Produces:
  - `enum class CreatorStudioDestination { ADAPTIVE, MUSIC, VIDEO }`
  - `enum class CreatorCanvasMode { FREEFORM, PAGE, SPREAD, DOCUMENT, SOCIAL }`
  - `data class CreatorWorld(val id: String, val label: String)`
  - `data class CreatorProjectType(...)`
  - `data class WorkspaceConfig(...)`

- [ ] **Step 1: Write the failing contract test**

```kotlin
class CreatorWorkspaceModelsTest {
    @Test fun projectTypeKeepsStableRoutingMetadata() {
        val type = CreatorProjectType(
            id = "fashion-collection",
            worldId = "fashion",
            label = "Design a Collection",
            destination = CreatorStudioDestination.ADAPTIVE,
            workspaceConfigId = "fashion-collection",
        )
        assertEquals("fashion", type.worldId)
        assertEquals(CreatorStudioDestination.ADAPTIVE, type.destination)
        assertEquals("fashion-collection", type.workspaceConfigId)
    }
}
```

- [ ] **Step 2: Run the focused test and verify RED**

Run:

```bash
./gradlew testDebugUnitTest --tests com.patsy.app.thynk.CreatorWorkspaceModelsTest
```

Expected: FAIL because the new contracts do not exist.

- [ ] **Step 3: Implement the minimal contracts**

```kotlin
enum class CreatorStudioDestination { ADAPTIVE, MUSIC, VIDEO }
enum class CreatorCanvasMode { FREEFORM, PAGE, SPREAD, DOCUMENT, SOCIAL }

data class CreatorWorld(val id: String, val label: String)

data class CreatorProjectType(
    val id: String,
    val worldId: String,
    val label: String,
    val destination: CreatorStudioDestination,
    val workspaceConfigId: String? = null,
)

data class WorkspaceConfig(
    val id: String,
    val title: String,
    val studioMode: StudioMode,
    val canvasMode: CreatorCanvasMode,
    val defaultWidthPx: Int,
    val defaultHeightPx: Int,
    val toolIds: List<String>,
    val beginnerShortcutIds: List<String> = emptyList(),
    val templateFamilyIds: List<String> = emptyList(),
    val paletteFamilyIds: List<String> = emptyList(),
    val typographyFamilyIds: List<String> = emptyList(),
)
```

- [ ] **Step 4: Run the focused test and verify GREEN**
- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/patsy/app/thynk/CreatorWorkspaceModels.kt app/src/test/java/com/patsy/app/thynk/CreatorWorkspaceModelsTest.kt
git commit -m "feat: add THyNK creator workspace contracts"
```

---

### Task 2: Seed the Creator World and project-type catalogue

**Files:**
- Create: `app/src/main/java/com/patsy/app/thynk/CreatorWorkspaceCatalog.kt`
- Test: `app/src/test/java/com/patsy/app/thynk/CreatorWorkspaceCatalogTest.kt`

**Interfaces:**
- Produces:
  - `CreatorWorkspaceCatalog.worlds`
  - `CreatorWorkspaceCatalog.projectTypes`
  - `CreatorWorkspaceCatalog.projectType(id: String)`
  - `CreatorWorkspaceCatalog.projectsForWorld(worldId: String)`

- [ ] **Step 1: Write RED tests for the first complete seed set**

Tests must assert at minimum:

```kotlin
assertNotNull(CreatorWorkspaceCatalog.projectType("fashion-collection"))
assertNotNull(CreatorWorkspaceCatalog.projectType("publishing-magazine"))
assertNotNull(CreatorWorkspaceCatalog.projectType("office-cv"))
assertNotNull(CreatorWorkspaceCatalog.projectType("art-illustration"))
assertNotNull(CreatorWorkspaceCatalog.projectType("social-instagram-post"))
assertEquals(CreatorStudioDestination.VIDEO,
    CreatorWorkspaceCatalog.projectType("social-reel")!!.destination)
assertEquals(CreatorStudioDestination.MUSIC,
    CreatorWorkspaceCatalog.projectType("music-dj-set")!!.destination)
```

Also assert every project references a declared world and all IDs are unique.

- [ ] **Step 2: Run focused tests and verify RED**
- [ ] **Step 3: Implement the seed catalogue**

Seed these world IDs first because they cover the broadest editor modes:

`design`, `art`, `office`, `publishing`, `journalism`, `fashion`, `photo`, `social`, `branding`, `writing`, `comics`, `interiors`, `architecture`, `product`, `ceramics`, `jewellery`, `crafts`, `education`, `business`, `events`, `portfolio`, `music`, `video`.

Seed useful project types rather than hundreds of labels. Include at least:

- Fashion: collection, mood board, garment sketch, technical flat, fabric board, lookbook, line sheet.
- Publishing: magazine, newspaper, zine, brochure, catalogue, book layout.
- Office: document, letter, CV, report, proposal, invoice, presentation, worksheet, planner.
- Art: drawing, illustration, concept art, collage, comic page, character sheet, exhibition board, portfolio.
- Social: Instagram post, story, carousel, YouTube thumbnail, LinkedIn post, Reel.
- Music: track, recording, mix, DJ set.
- Video: video edit, film, Reel/Short, documentary, music video.

- [ ] **Step 4: Run focused tests and verify GREEN**
- [ ] **Step 5: Commit**

---

### Task 3: Workspace configurations for Fashion, Magazine, Office, Art and Social

**Files:**
- Modify: `app/src/main/java/com/patsy/app/thynk/CreatorWorkspaceCatalog.kt`
- Test: `app/src/test/java/com/patsy/app/thynk/CreatorWorkspaceConfigTest.kt`

**Interfaces:**
- Produces: `CreatorWorkspaceCatalog.workspaceConfig(id: String): WorkspaceConfig?`

- [ ] **Step 1: Write RED tests that prove the screen can materially change**

Required expectations:

```kotlin
val fashion = CreatorWorkspaceCatalog.workspaceConfig("fashion-collection")!!
assertEquals(CreatorCanvasMode.FREEFORM, fashion.canvasMode)
assertTrue("draw" in fashion.toolIds)
assertTrue("elements" in fashion.toolIds)
assertFalse("captions" in fashion.toolIds)

val magazine = CreatorWorkspaceCatalog.workspaceConfig("publishing-magazine")!!
assertEquals(CreatorCanvasMode.SPREAD, magazine.canvasMode)
assertTrue("text" in magazine.toolIds)
assertTrue("guides" in magazine.toolIds)

val office = CreatorWorkspaceCatalog.workspaceConfig("office-cv")!!
assertEquals(CreatorCanvasMode.DOCUMENT, office.canvasMode)
assertFalse("animate" in office.toolIds)

val social = CreatorWorkspaceCatalog.workspaceConfig("social-instagram-post")!!
assertEquals(CreatorCanvasMode.SOCIAL, social.canvasMode)
assertEquals(1080, social.defaultWidthPx)
assertEquals(1080, social.defaultHeightPx)
```

- [ ] **Step 2: Run and verify RED**
- [ ] **Step 3: Add concrete configs**

Use the existing `StudioToolCatalog` IDs. Do not invent a second generic editing engine. Subject-only concepts such as `garments`, `swatches`, `columns`, `masthead` and `measurements` should initially be represented as beginner shortcuts/subject panels, while universal edit actions continue to use the shared tool catalogue.

- [ ] **Step 4: Run and verify GREEN**
- [ ] **Step 5: Commit**

---

### Task 4: Resolve configured tools against the existing native tool catalogue

**Files:**
- Create: `app/src/main/java/com/patsy/app/thynk/CreatorWorkspaceTools.kt`
- Test: `app/src/test/java/com/patsy/app/thynk/CreatorWorkspaceToolsTest.kt`

**Interfaces:**
- Consumes: `WorkspaceConfig`, `StudioToolCatalog.all`
- Produces: `fun toolsForWorkspace(config: WorkspaceConfig): List<StudioToolDescriptor>`

- [ ] **Step 1: Write RED tests**

Verify ordering follows `config.toolIds`, unknown IDs are ignored/fail-closed, and no extra tools leak from another mode.

```kotlin
val tools = toolsForWorkspace(
    WorkspaceConfig(
        id = "test",
        title = "Test",
        studioMode = StudioMode.IMAGE,
        canvasMode = CreatorCanvasMode.FREEFORM,
        defaultWidthPx = 1000,
        defaultHeightPx = 1000,
        toolIds = listOf("draw", "text", "not-a-tool", "layers"),
    )
)
assertEquals(listOf("draw", "text", "layers"), tools.map { it.id })
```

- [ ] **Step 2: Run and verify RED**
- [ ] **Step 3: Implement with lookup against `StudioToolCatalog.all`**
- [ ] **Step 4: Run and verify GREEN**
- [ ] **Step 5: Commit**

---

### Task 5: Unified routing to Adaptive, Music or Video

**Files:**
- Create: `app/src/main/java/com/patsy/app/thynk/CreatorWorkspaceRouting.kt`
- Modify: `app/src/main/java/com/patsy/app/thynk/ThynkEditorRouting.kt`
- Test: `app/src/test/java/com/patsy/app/thynk/CreatorWorkspaceRoutingTest.kt`
- Test: existing `ThynkEditorRoutingTest.kt` if present

**Interfaces:**
- Produces:
  - `data class CreatorWorkspaceDestination(val destination: CreatorStudioDestination, val workspaceConfigId: String? = null)`
  - `fun creatorDestinationForProjectType(projectTypeId: String): CreatorWorkspaceDestination?`

- [ ] **Step 1: Write RED routing tests**

Required routing:

```kotlin
assertEquals(CreatorStudioDestination.ADAPTIVE,
    creatorDestinationForProjectType("fashion-collection")!!.destination)
assertEquals("publishing-magazine",
    creatorDestinationForProjectType("publishing-magazine")!!.workspaceConfigId)
assertEquals(CreatorStudioDestination.VIDEO,
    creatorDestinationForProjectType("social-reel")!!.destination)
assertEquals(CreatorStudioDestination.MUSIC,
    creatorDestinationForProjectType("music-dj-set")!!.destination)
```

- [ ] **Step 2: Run and verify RED**
- [ ] **Step 3: Implement the resolver and preserve current legacy item routes**

Do not delete the current `POSTERS`/`FLYERS`/`VIDEO EDITOR` compatibility mappings. Make them delegate into the new resolver only where a stable mapping exists.

- [ ] **Step 4: Run new + existing routing tests and verify GREEN**
- [ ] **Step 5: Commit**

---

### Task 6: Resource-family metadata for templates, colours and typography

**Files:**
- Create: `app/src/main/java/com/patsy/app/thynk/CreatorResourceCatalog.kt`
- Test: `app/src/test/java/com/patsy/app/thynk/CreatorResourceCatalogTest.kt`

**Interfaces:**
- Produces:
  - `enum class CreatorResourceVerification { REFERENCE_ONLY, VERIFIED_METADATA, VERIFIED_ASSET }`
  - `PaletteFamily`
  - `TypographyFamily`
  - `TemplateFamily`
  - lookup methods by stable family ID.

- [ ] **Step 1: Write RED tests for fail-closed verification**

Assert donor/reference families can exist in the catalogue without being reported as production assets.

```kotlin
val family = CreatorResourceCatalog.templateFamily("fashion-lookbook")!!
assertEquals(CreatorResourceVerification.REFERENCE_ONLY, family.verification)
assertFalse(family.productionReady)
```

- [ ] **Step 2: Run and verify RED**
- [ ] **Step 3: Seed metadata-only libraries**

Palette family IDs:

`editorial`, `fashion-seasonal`, `luxury`, `streetwear`, `earth`, `ceramic-glaze`, `corporate`, `education`, `youth`, `retro`, `minimal`, `neon`, `monochrome`, `cinematic`.

Typography family IDs:

`editorial`, `newspaper`, `fashion`, `luxury`, `corporate`, `playful`, `handwritten`, `display`, `body`, `accessible`, `poster`, `presentation`, `social`, `portfolio`.

Template family IDs should cover the seed project types but remain `REFERENCE_ONLY` until actual assets and licensing are verified.

- [ ] **Step 4: Run and verify GREEN**
- [ ] **Step 5: Commit**

---

### Task 7: Pure adaptive presentation model

**Files:**
- Create: `app/src/main/java/com/patsy/app/thynk/CreatorWorkspacePresentation.kt`
- Test: `app/src/test/java/com/patsy/app/thynk/CreatorWorkspacePresentationTest.kt`

**Interfaces:**
- Produces:
  - `data class CreatorWorkspacePresentation(...)`
  - `fun presentWorkspace(config: WorkspaceConfig): CreatorWorkspacePresentation`

- [ ] **Step 1: Write RED tests for three visibly different workspaces**

Fashion must expose Fashion wording/shortcuts, Magazine must expose page/spread wording, and Office must expose productivity wording. Tests should prove they are not just the same generic list with a different title.

- [ ] **Step 2: Run and verify RED**
- [ ] **Step 3: Implement the pure presentation mapper**
- [ ] **Step 4: Run and verify GREEN**
- [ ] **Step 5: Commit**

---

### Task 8: Carry `WorkspaceConfig` into the real Compose editor without replacing the canvas

**Files:**
- Modify: `app/src/main/java/com/patsy/app/thynk/ThynkDesignEditorScreen.kt`
- Modify: `scripts/apply_thynk_design_editor_integration.py` if CI still rewrites this screen
- Test: `app/src/test/java/com/patsy/app/thynk/ThynkAdaptiveEditorPresentationTest.kt`

**Interfaces:**
- Consumes: `WorkspaceConfig`, `CreatorWorkspacePresentation`, existing `StudioCanvasState` reducer and current Design UI.

- [ ] **Step 1: Add a failing test around a pure screen/presentation helper, not Compose screenshot text matching**
- [ ] **Step 2: Run and verify RED**
- [ ] **Step 3: Extend the editor signature with a backward-compatible default**

Target shape:

```kotlin
@Composable
fun ThynkDesignEditorScreen(
    workspaceConfig: WorkspaceConfig = CreatorWorkspaceCatalog.workspaceConfig("design-blank")!!,
    ...
)
```

Use `toolsForWorkspace(workspaceConfig)` for visible tool ordering and `presentWorkspace(workspaceConfig)` for subject-specific headings/shortcuts. Keep all existing canvas state, position/size/opacity/alignment behavior and locked-layer rules.

- [ ] **Step 4: Make the integration script idempotent with both pre-adaptive and final screen forms**
- [ ] **Step 5: Run unit tests, integration script and verify GREEN**
- [ ] **Step 6: Commit**

---

### Task 9: THyNK-IT hub selection and seamless specialist handoff

**Files:**
- Modify: `app/src/main/java/com/patsy/app/thynk/ThynkStudioScreen.kt`
- Test: `app/src/test/java/com/patsy/app/thynk/ThynkCreatorHubTest.kt`

**Interfaces:**
- Consumes: `CreatorWorkspaceCatalog`, `creatorDestinationForProjectType()`.

- [ ] **Step 1: Write RED tests for hub selection model**

Prove:
- Fashion -> adaptive editor config.
- Magazine -> adaptive editor config.
- CV -> adaptive editor config.
- Social Reel -> Video.
- Music Track -> Music.
- DJ Set -> Music/DJ route metadata.

- [ ] **Step 2: Run and verify RED**
- [ ] **Step 3: Wire the hub without changing the locked bottom navigation**

The UI should first answer “What do you want to make?” and then show relevant project types. Do not put every world/tool on one page.

- [ ] **Step 4: Run routing + hub + existing navigation tests**
- [ ] **Step 5: Commit**

---

### Task 10: Full verification and next-plan boundaries

**Files:**
- Modify docs only if test results expose an architecture change.

- [ ] **Step 1: Run complete unit suite**

```bash
./gradlew testDebugUnitTest
```

Expected: PASS.

- [ ] **Step 2: Run debug build**

```bash
./gradlew assembleDebug
```

Expected: PASS.

- [ ] **Step 3: Run release build**

```bash
./gradlew assembleRelease
```

Expected: PASS.

- [ ] **Step 4: Push and require exact final-head GitHub Actions success**

Keep PR #41 Draft and unmerged.

- [ ] **Step 5: Split subsequent work into separate plans**

After this foundation is green, create independent plans for:

1. THyNK-IT donor/template/font/palette content expansion.
2. THyNK Music playback + DAW + DJ Studio refinement.
3. THyNK Video player + timeline/editor refinement.
4. Creator Showcase/collaboration and discovery, with under-16 safety/privacy designed before enablement.
5. Production asset verification/import pipeline for template/font/media packs.

These must not be collapsed into one huge implementation branch.

---

## Self-review

- Spec coverage: shared adaptive engine, Music/Video specialist boundaries, routing, template/font/colour metadata, progressive workspace differences and truth boundaries are covered.
- Deliberately deferred: building full Music/Video engines, public Showcase collaboration and importing unverified assets. They require independent plans and tests.
- Placeholder scan: no implementation step relies on TBD/TODO behavior.
- Type consistency: `CreatorStudioDestination`, `CreatorCanvasMode`, `CreatorProjectType`, `WorkspaceConfig`, `CreatorWorkspaceDestination` and lookup function names are consistent across tasks.

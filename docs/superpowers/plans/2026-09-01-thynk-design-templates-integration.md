# THyNK Design & Templates Integration Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Route the locked Design & Templates category into the one generic THyNK image canvas, expose only verified donor templates, and persist editable design drafts without conflating project state with exported PNGs.

**Architecture:** Build on the donor manifest from Job 1 and the canvas/import/export model from Job 2. Add a pure Design routing contract, a verified-template adapter, an account-scoped editable-draft repository, and one native Design route inside the existing `ThynkStudioScreen`. Do not create a second editor or second THyNK home.

**Tech Stack:** Kotlin/JVM tests, Jetpack Compose, existing `androidx.datastore:datastore-preferences:1.1.1`, existing THyNK/canvas models, no new serialization dependency.

**Spec:** `docs/superpowers/specs/2026-08-31-thynk-donor-consolidation-design.md`

## Global Constraints

- Work on `chatgpt/codex-ready-2026-09-01`; keep work Draft and unmerged.
- Execute only after donor-asset intake and generic canvas/image-import/PNG-export jobs are GREEN.
- Preserve the locked ten-category THyNK hub and current category labels.
- Preserve semantic navigation `HOME · THyNK · CAMERA · PATSY DMS · PROFILE` and the shared outer primary bar.
- Design & Templates must use the generic image canvas; no competing editor architecture.
- Only donor records whose `isProductionReady` is true may become usable production templates.
- `REFERENCE_ONLY` and `REJECTED` donor records never appear as usable templates.
- Blank Design and Custom Size must work even when there are zero verified templates.
- Editable draft persistence and PNG export are separate state machines.
- Do not claim cloud sync; this plan adds account-scoped local draft persistence only.

---

### Task 1: Add stable Design entry routing contract

**Files:**
- Create: `app/src/main/java/com/patsy/app/thynk/ThynkDesignRouting.kt`
- Create: `app/src/test/java/com/patsy/app/thynk/ThynkDesignRoutingTest.kt`

**Interfaces:**
- Consumes: Design & Templates item labels from `ThynkStudioCatalog.kt`.
- Produces: `DesignEntryPoint`, `designEntryForThynkItem(item: String): DesignEntryPoint?`.

- [ ] **Step 1: Write the failing routing tests**

```kotlin
package com.patsy.app.thynk

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ThynkDesignRoutingTest {
    @Test fun blankAndCustomHaveDedicatedEntryPoints() {
        assertEquals(DesignEntryPoint.Blank, designEntryForThynkItem("BLANK DESIGNS"))
        assertEquals(DesignEntryPoint.CustomSize, designEntryForThynkItem("CUSTOM SIZE"))
    }

    @Test fun designFormatsOpenTemplateBrowserWithStableKind() {
        assertEquals(DesignEntryPoint.Templates("posters"), designEntryForThynkItem("POSTERS"))
        assertEquals(DesignEntryPoint.Templates("flyers"), designEntryForThynkItem("FLYERS"))
        assertEquals(DesignEntryPoint.Templates("templates"), designEntryForThynkItem("TEMPLATES"))
    }

    @Test fun nonDesignItemDoesNotCreateDesignRoute() {
        assertNull(designEntryForThynkItem("VIDEO EDITOR"))
    }
}
```

- [ ] **Step 2: Run the test and verify RED**

```bash
./gradlew testDebugUnitTest --tests 'com.patsy.app.thynk.ThynkDesignRoutingTest'
```
Expected: FAIL because `DesignEntryPoint` and `designEntryForThynkItem` do not exist.

- [ ] **Step 3: Implement the minimal mapping**

```kotlin
package com.patsy.app.thynk

sealed interface DesignEntryPoint {
    data object Blank : DesignEntryPoint
    data object CustomSize : DesignEntryPoint
    data class Templates(val kind: String) : DesignEntryPoint
}

private val designTemplateKinds = mapOf(
    "POSTERS" to "posters",
    "FLYERS" to "flyers",
    "INVITATIONS" to "invitations",
    "CARDS" to "cards",
    "MENUS" to "menus",
    "PRICE LISTS" to "price-lists",
    "SIGNS" to "signs",
    "CERTIFICATES" to "certificates",
    "BROCHURES" to "brochures",
    "LABELS" to "labels",
    "TEMPLATES" to "templates",
)

fun designEntryForThynkItem(item: String): DesignEntryPoint? = when (val label = item.trim().uppercase()) {
    "BLANK DESIGNS" -> DesignEntryPoint.Blank
    "CUSTOM SIZE" -> DesignEntryPoint.CustomSize
    else -> designTemplateKinds[label]?.let(DesignEntryPoint::Templates)
}
```

- [ ] **Step 4: Run targeted and THyNK tests**

```bash
./gradlew testDebugUnitTest --tests 'com.patsy.app.thynk.ThynkDesignRoutingTest'
./gradlew testDebugUnitTest --tests 'com.patsy.app.thynk.*'
```
Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/patsy/app/thynk/ThynkDesignRouting.kt app/src/test/java/com/patsy/app/thynk/ThynkDesignRoutingTest.kt
git commit -m "feat: add THyNK design entry routing"
```

---

### Task 2: Adapt verified donor templates without leaking reference-only assets

**Files:**
- Create: `app/src/main/java/com/patsy/app/studio/templates/VerifiedStudioTemplateCatalog.kt`
- Create: `app/src/test/java/com/patsy/app/studio/templates/VerifiedStudioTemplateCatalogTest.kt`

**Interfaces:**
- Consumes: `DonorAssetManifest`, `DonorAssetType.TEMPLATE`, `DonorAssetRecord.isProductionReady` from Job 1.
- Produces: `VerifiedStudioTemplateRef`, `verifiedStudioTemplates(manifest, kind)`.

- [ ] **Step 1: Write the failing verified-only test**

```kotlin
package com.patsy.app.studio.templates

import com.patsy.app.studio.assets.*
import kotlin.test.Test
import kotlin.test.assertEquals

class VerifiedStudioTemplateCatalogTest {
    private fun record(
        id: String,
        subcategory: String,
        availability: DonorAssetAvailability,
        license: DonorLicenseStatus,
    ) = DonorAssetRecord(
        id = id,
        origin = "Owner supplied",
        category = "design",
        subcategory = subcategory,
        type = DonorAssetType.TEMPLATE,
        location = "res/raw/$id.json",
        licenseStatus = license,
        duplicateStatus = DonorDuplicateStatus.UNIQUE,
        checksumSha256 = "abc123",
        availability = availability,
    )

    @Test fun onlyVerifiedMatchingTemplatesAreReturned() {
        val manifest = DonorAssetManifest(listOf(
            record("poster-ok", "posters", DonorAssetAvailability.VERIFIED, DonorLicenseStatus.VERIFIED_FOR_APP),
            record("poster-ref", "posters", DonorAssetAvailability.REFERENCE_ONLY, DonorLicenseStatus.UNKNOWN),
            record("flyer-ok", "flyers", DonorAssetAvailability.VERIFIED, DonorLicenseStatus.VERIFIED_FOR_APP),
        ))

        assertEquals(
            listOf("poster-ok"),
            verifiedStudioTemplates(manifest, "posters").map { it.id },
        )
    }
}
```

- [ ] **Step 2: Run the test and verify RED**

```bash
./gradlew testDebugUnitTest --tests 'com.patsy.app.studio.templates.VerifiedStudioTemplateCatalogTest'
```
Expected: FAIL because the verified-template adapter does not exist.

- [ ] **Step 3: Implement the adapter**

```kotlin
package com.patsy.app.studio.templates

import com.patsy.app.studio.assets.DonorAssetManifest
import com.patsy.app.studio.assets.DonorAssetType

data class VerifiedStudioTemplateRef(
    val id: String,
    val kind: String,
    val location: String,
)

fun verifiedStudioTemplates(
    manifest: DonorAssetManifest,
    kind: String,
): List<VerifiedStudioTemplateRef> = manifest.productionReady()
    .asSequence()
    .filter { it.type == DonorAssetType.TEMPLATE }
    .filter { it.category == "design" && it.subcategory == kind }
    .map { VerifiedStudioTemplateRef(it.id, it.subcategory, it.location) }
    .sortedBy { it.id }
    .toList()
```

- [ ] **Step 4: Run template and donor tests**

```bash
./gradlew testDebugUnitTest --tests 'com.patsy.app.studio.templates.*'
./gradlew testDebugUnitTest --tests 'com.patsy.app.studio.assets.*'
```
Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/patsy/app/studio/templates app/src/test/java/com/patsy/app/studio/templates
git commit -m "feat: expose only verified THyNK templates"
```

---

### Task 3: Add deterministic editable-draft codec and account-scoped DataStore repository

**Files:**
- Create: `app/src/main/java/com/patsy/app/studio/drafts/StudioDraftSnapshot.kt`
- Create: `app/src/main/java/com/patsy/app/studio/drafts/StudioDraftCodec.kt`
- Create: `app/src/main/java/com/patsy/app/studio/drafts/DataStoreStudioDraftRepository.kt`
- Create: `app/src/test/java/com/patsy/app/studio/drafts/StudioDraftCodecTest.kt`

**Interfaces:**
- Consumes: `StudioCanvasState`, `StudioCanvasSize`, `StudioCanvasElement`, `CanvasElementType`, `CanvasTransform` from Job 2.
- Produces: `StudioDraftSnapshot`, `StudioDraftCodec.encode/decode`, `StudioDraftRepository.load/save/clear`.

- [ ] **Step 1: Write the failing codec round-trip test**

```kotlin
package com.patsy.app.studio.drafts

import com.patsy.app.studio.canvas.*
import kotlin.test.Test
import kotlin.test.assertEquals

class StudioDraftCodecTest {
    @Test fun editableCanvasRoundTripsExactly() {
        val state = StudioCanvasState(
            canvasSize = StudioCanvasSize(1080, 1350),
            elements = listOf(
                StudioCanvasElement(
                    id = "image-1",
                    type = CanvasElementType.IMAGE,
                    content = "content://patsy/image/1",
                    transform = CanvasTransform(centerX = 0.4f, centerY = 0.6f, rotationDegrees = 15f),
                ),
            ),
            selectedElementId = "image-1",
        )
        val original = StudioDraftSnapshot.fromCanvas("draft-1", state)
        assertEquals(original, StudioDraftCodec.decode(StudioDraftCodec.encode(original)))
    }
}
```

- [ ] **Step 2: Run the test and verify RED**

```bash
./gradlew testDebugUnitTest --tests 'com.patsy.app.studio.drafts.StudioDraftCodecTest'
```
Expected: FAIL because the draft snapshot/codec do not exist.

- [ ] **Step 3: Implement the snapshot**

```kotlin
package com.patsy.app.studio.drafts

import com.patsy.app.studio.canvas.*

data class StudioDraftSnapshot(
    val version: Int = 1,
    val projectId: String,
    val widthPx: Int,
    val heightPx: Int,
    val selectedElementId: String?,
    val elements: List<StudioCanvasElement>,
) {
    fun toCanvas(): StudioCanvasState = StudioCanvasState(
        canvasSize = StudioCanvasSize(widthPx, heightPx),
        elements = elements,
        selectedElementId = selectedElementId?.takeIf { id -> elements.any { it.id == id } },
    )

    companion object {
        fun fromCanvas(projectId: String, state: StudioCanvasState) = StudioDraftSnapshot(
            projectId = projectId,
            widthPx = state.canvasSize.widthPx,
            heightPx = state.canvasSize.heightPx,
            selectedElementId = state.selectedElementId,
            elements = state.elements,
        )
    }
}
```

- [ ] **Step 4: Implement the deterministic version-1 codec**

Use one header line plus one Base64-URL-safe line per element. Encode `projectId`, selection, element IDs and content with `java.util.Base64.getUrlEncoder().withoutPadding()` and decode with `getUrlDecoder()`. Encode every `CanvasTransform` field as a separate delimiter-safe numeric/boolean field. `decode` returns `null` for malformed content, invalid dimensions or versions other than `1`; it never manufactures replacement content.

- [ ] **Step 5: Implement the account-scoped DataStore repository**

```kotlin
interface StudioDraftRepository {
    suspend fun load(projectId: String): StudioDraftSnapshot?
    suspend fun save(snapshot: StudioDraftSnapshot)
    suspend fun clear(projectId: String)
}
```

`DataStoreStudioDraftRepository` receives `Context` and `accountScopeKey`. Derive a stable key prefix by SHA-256 hashing `accountScopeKey` and hex-encoding the digest; never use raw email/username in the DataStore preference key. Store only the encoded draft string. `load` returns `null` when absent or malformed. Do not store auth tokens or passwords here.

- [ ] **Step 6: Run draft tests and full unit suite**

```bash
./gradlew testDebugUnitTest --tests 'com.patsy.app.studio.drafts.*'
./gradlew testDebugUnitTest --stacktrace
```
Expected: PASS.

- [ ] **Step 7: Commit**

```bash
git add app/src/main/java/com/patsy/app/studio/drafts app/src/test/java/com/patsy/app/studio/drafts
git commit -m "feat: persist editable THyNK design drafts"
```

---

### Task 4: Add one category-action resolver and one Design route to current THyNK

**Files:**
- Create: `app/src/main/java/com/patsy/app/thynk/ThynkCategoryAction.kt`
- Create: `app/src/test/java/com/patsy/app/thynk/ThynkCategoryActionTest.kt`
- Modify: `app/src/main/java/com/patsy/app/thynk/ThynkStudioScreen.kt`
- Modify: `app/src/main/java/com/patsy/app/FinalMainActivity.kt`
- Create: `app/src/main/java/com/patsy/app/studio/design/ThynkDesignScreen.kt`

**Interfaces:**
- Consumes: `designEntryForThynkItem`, existing `musicPageForItem`, existing `editorPageForThynkItem`, Job 2 canvas APIs.
- Produces: `ThynkCategoryAction`, `resolveThynkCategoryAction(categoryId, item)`, `ThynkRoute.Design`.

- [ ] **Step 1: Write the failing category-action test**

```kotlin
package com.patsy.app.thynk

import kotlin.test.Test
import kotlin.test.assertEquals

class ThynkCategoryActionTest {
    @Test fun designBlankResolvesToDesignAction() {
        assertEquals(
            ThynkCategoryAction.OpenDesign(DesignEntryPoint.Blank),
            resolveThynkCategoryAction("design", "BLANK DESIGNS"),
        )
    }

    @Test fun videoEditorKeepsExistingVideoAction() {
        assertEquals(
            ThynkCategoryAction.OpenEditor("video-editor"),
            resolveThynkCategoryAction("video", "VIDEO EDITOR"),
        )
    }
}
```

- [ ] **Step 2: Run the test and verify RED**

```bash
./gradlew testDebugUnitTest --tests 'com.patsy.app.thynk.ThynkCategoryActionTest'
```
Expected: FAIL because `ThynkCategoryAction` and `resolveThynkCategoryAction` do not exist.

- [ ] **Step 3: Implement the resolver**

```kotlin
package com.patsy.app.thynk

sealed interface ThynkCategoryAction {
    data class OpenDesign(val entry: DesignEntryPoint) : ThynkCategoryAction
    data class OpenMusic(val pageId: String) : ThynkCategoryAction
    data class OpenEditor(val pageId: String) : ThynkCategoryAction
    data object Stay : ThynkCategoryAction
}

fun resolveThynkCategoryAction(categoryId: String, item: String): ThynkCategoryAction = when (categoryId) {
    "design" -> designEntryForThynkItem(item)?.let(ThynkCategoryAction::OpenDesign) ?: ThynkCategoryAction.Stay
    "music" -> ThynkCategoryAction.OpenMusic(musicPageForItem(item))
    else -> editorPageForThynkItem(item)?.let(ThynkCategoryAction::OpenEditor) ?: ThynkCategoryAction.Stay
}
```

- [ ] **Step 4: Wire the resolver into the existing `ThynkStudioScreen`**

Add `data class Design(val entry: DesignEntryPoint) : ThynkRoute`.

Change category item handling to switch over `resolveThynkCategoryAction(current.category.id, item)` and route to the existing Music/Editor routes or the new Design route. `Stay` leaves the current category visible.

Back behavior:
- Design -> Design & Templates category
- Music -> THyNK Music category
- Editor -> Video & Camera category
- Category -> hub

Do not change the current hub or add a second bottom navigation.

- [ ] **Step 5: Pass account scope only for draft storage**

Change the signature to:

```kotlin
@Composable
fun ThynkStudioScreen(accountScopeKey: String)
```

From `FinalMainActivity`, call:

```kotlin
ThynkStudioScreen(accountScopeKey = session?.userId ?: "debug-preview")
```

This key scopes local drafts only and must never grant route or Owner authority.

- [ ] **Step 6: Implement native `ThynkDesignScreen` using Job 2 canvas**

Behavior:
- `Blank` starts `StudioCanvasPresets.SQUARE.size` with no elements.
- `CustomSize` validates via `validateCustomCanvasSize`; invalid dimensions do not create a canvas.
- `Templates(kind)` calls `verifiedStudioTemplates`. If the list is empty, show exactly `No verified templates are available yet.` and keep Blank/Custom Size actions available.
- A verified template creates editable canvas/layer state; it is never rendered as a fake flattened screenshot.
- Image import/export delegate to Job 2 implementations.
- The screen owns no primary bottom navigation.

- [ ] **Step 7: Run THyNK/design tests and builds**

```bash
./gradlew testDebugUnitTest --tests 'com.patsy.app.thynk.*'
./gradlew testDebugUnitTest --tests 'com.patsy.app.studio.*'
./gradlew assembleDebug --stacktrace
```
Expected: PASS.

- [ ] **Step 8: Commit**

```bash
git add app/src/main/java/com/patsy/app/thynk app/src/main/java/com/patsy/app/studio/design app/src/main/java/com/patsy/app/FinalMainActivity.kt app/src/test/java/com/patsy/app/thynk
git commit -m "feat: route THyNK Design into native canvas"
```

---

### Task 5: Persist each committed design edit and restore on re-entry

**Files:**
- Create: `app/src/main/java/com/patsy/app/studio/design/DesignDraftController.kt`
- Create: `app/src/test/java/com/patsy/app/studio/design/DesignDraftControllerTest.kt`
- Modify: `app/src/main/java/com/patsy/app/studio/design/ThynkDesignScreen.kt`

**Interfaces:**
- Consumes: `StudioDraftRepository`, `StudioCanvasHistory`, `StudioCanvasAction`, `applyCanvasAction` from Job 2.
- Produces: `DesignDraftController.restore()`, `DesignDraftController.applyAndSave(action)`.

- [ ] **Step 1: Write the failing controller test with an in-memory repository**

```kotlin
@Test fun committedCanvasActionIsSavedAsEditableDraft() = runTest {
    val repository = InMemoryStudioDraftRepository()
    val controller = DesignDraftController(
        projectId = "draft-1",
        repository = repository,
        initial = StudioCanvasHistory.initial(StudioCanvasState.empty(StudioCanvasPresets.SQUARE.size)),
    )

    controller.applyAndSave(StudioCanvasAction.AddElement(testImage))

    val saved = repository.load("draft-1")
    assertEquals(1, saved!!.elements.size)
}
```

The test file defines `InMemoryStudioDraftRepository` implementing the Task 3 interface. Use `kotlinx-coroutines-test`, already present in `app/build.gradle.kts`.

- [ ] **Step 2: Run the test and verify RED**

```bash
./gradlew testDebugUnitTest --tests 'com.patsy.app.studio.design.DesignDraftControllerTest'
```
Expected: FAIL because `DesignDraftController` does not exist.

- [ ] **Step 3: Implement the controller**

`restore()` loads `projectId`; if valid, replace the current history with `StudioCanvasHistory.initial(snapshot.toCanvas())`, otherwise preserve the supplied initial state.

`applyAndSave(action)` calls `applyCanvasAction`. If the action does not change `history.present`, return without writing. If it changes state, update history and synchronously await `repository.save(StudioDraftSnapshot.fromCanvas(projectId, history.present))` before reporting save success.

No export URI or `StudioExportState` belongs in `StudioDraftSnapshot`.

- [ ] **Step 4: Wire only committed semantic actions through the controller**

Pointer movement may update temporary gesture state in Compose, but dispatch one `MoveSelected`/`ResizeSelected`/`RotateSelected` action when the gesture commits. Each committed canvas action uses `applyAndSave`. Undo and redo must likewise persist the resulting editable state after the operation succeeds.

On Design route entry, call `restore()` once before presenting the editable project. A failed save keeps in-memory edits and shows a truthful retry/error state; do not show `Saved` unless the repository write completes.

- [ ] **Step 5: Run final verification**

```bash
./gradlew testDebugUnitTest --stacktrace
./gradlew assembleDebug --stacktrace
./gradlew assembleRelease --stacktrace
```
Expected: all exit 0.

- [ ] **Step 6: Push and require fresh Patsy Consolidation CI**

Record exact starting SHA, ending SHA, changed files, intentional RED evidence, GREEN tests/builds, final CI run, artifact IDs/digests and remaining physical-device limits. Keep Draft and unmerged.

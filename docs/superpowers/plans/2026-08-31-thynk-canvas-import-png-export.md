# THyNK Canvas, Image Import and PNG Export Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build the first real generic THyNK image-editor vertical slice: logical canvas presets/custom size, selectable/resizable layers, undo/redo, Android image import, and actual PNG export that is separate from editable project state.

**Architecture:** Extend the existing `StudioEditorState` ecosystem rather than replacing it. Add a focused canvas reducer/history model, a Compose canvas screen, a fail-closed import policy, and an Android PNG exporter. The visible canvas is scaled to fit the phone while export uses logical canvas dimensions. Video remains owned by the separate Media3 plan.

**Tech Stack:** Kotlin, Jetpack Compose, Android Activity Result APIs, `ContentResolver`, `Bitmap`/`Canvas`, MediaStore/app cache as appropriate, existing Gradle/Compose toolchain.

**Spec:** `docs/superpowers/specs/2026-08-31-thynk-donor-consolidation-design.md`

## Global Constraints

- Work only on PR #38 branch `chatgpt/thynk-music-apk-2026-08-31`; keep Draft.
- Execute after Media3, global navigation and donor-asset intake plans are GREEN.
- Preserve semantic primary nav `HOME · THyNK · CAMERA · PATSY DMS · PROFILE` and visible `Home · coloured THyNK · large + · PDMs · Profile`.
- Do not hide the global bar in the editor.
- Do not create a second THyNK home or parallel media-state architecture.
- Export must write real bytes before returning success.
- Project persistence/autosave and export are separate concepts.
- No fake sample import, fake export URI or provider result.
- First real format is PNG. JPEG/WebP/PDF/video/audio stay unavailable until separately implemented.

---

### Task 1: Add central canvas presets and custom-size validation

**Files:**
- Create: `app/src/main/java/com/patsy/app/studio/canvas/StudioCanvasSize.kt`
- Create: `app/src/test/java/com/patsy/app/studio/canvas/StudioCanvasSizeTest.kt`

**Interfaces:**
- Produces: `StudioCanvasSize`, `StudioCanvasPreset`, `StudioCanvasPresets`, `validateCustomCanvasSize(widthPx, heightPx)`.

- [ ] **Step 1: Write failing tests**

```kotlin
package com.patsy.app.studio.canvas

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class StudioCanvasSizeTest {
    @Test fun standardPresetsAreCentralAndStable() {
        assertEquals(StudioCanvasSize(1080, 1080), StudioCanvasPresets.SQUARE.size)
        assertEquals(StudioCanvasSize(1080, 1350), StudioCanvasPresets.PORTRAIT.size)
        assertEquals(StudioCanvasSize(1080, 1920), StudioCanvasPresets.STORY.size)
        assertEquals(StudioCanvasSize(1920, 1080), StudioCanvasPresets.LANDSCAPE.size)
    }

    @Test fun customSizeRejectsZeroAndUnreasonablyLargeValues() {
        assertFalse(validateCustomCanvasSize(0, 1080).isValid)
        assertFalse(validateCustomCanvasSize(20000, 20000).isValid)
        assertTrue(validateCustomCanvasSize(1200, 628).isValid)
    }
}
```

- [ ] **Step 2: Run RED**

```bash
./gradlew testDebugUnitTest --tests 'com.patsy.app.studio.canvas.StudioCanvasSizeTest'
```

- [ ] **Step 3: Implement central sizes**

```kotlin
package com.patsy.app.studio.canvas

data class StudioCanvasSize(val widthPx: Int, val heightPx: Int) {
    val aspectRatio: Float get() = widthPx.toFloat() / heightPx.toFloat()
}

data class StudioCanvasPreset(val id: String, val label: String, val size: StudioCanvasSize)

object StudioCanvasPresets {
    val SQUARE = StudioCanvasPreset("square", "Square", StudioCanvasSize(1080, 1080))
    val PORTRAIT = StudioCanvasPreset("portrait", "Portrait", StudioCanvasSize(1080, 1350))
    val STORY = StudioCanvasPreset("story", "Story", StudioCanvasSize(1080, 1920))
    val LANDSCAPE = StudioCanvasPreset("landscape", "Landscape", StudioCanvasSize(1920, 1080))
    val SOCIAL_WIDE = StudioCanvasPreset("social-wide", "Social Wide", StudioCanvasSize(1200, 628))
    val all = listOf(SQUARE, PORTRAIT, STORY, LANDSCAPE, SOCIAL_WIDE)
}

data class CanvasSizeValidation(val isValid: Boolean, val reason: String? = null)

fun validateCustomCanvasSize(widthPx: Int, heightPx: Int): CanvasSizeValidation = when {
    widthPx < 1 || heightPx < 1 -> CanvasSizeValidation(false, "Canvas dimensions must be positive")
    widthPx > 8192 || heightPx > 8192 -> CanvasSizeValidation(false, "Canvas dimensions exceed 8192px")
    widthPx.toLong() * heightPx.toLong() > 33_554_432L -> CanvasSizeValidation(false, "Canvas pixel area is too large")
    else -> CanvasSizeValidation(true)
}
```

- [ ] **Step 4: Run tests**

```bash
./gradlew testDebugUnitTest --tests 'com.patsy.app.studio.canvas.StudioCanvasSizeTest'
```
Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/patsy/app/studio/canvas app/src/test/java/com/patsy/app/studio/canvas
git commit -m "feat: centralize THyNK canvas sizes"
```

### Task 2: Add deterministic canvas layer reducer and transform model

**Files:**
- Create: `app/src/main/java/com/patsy/app/studio/canvas/StudioCanvasState.kt`
- Create: `app/src/test/java/com/patsy/app/studio/canvas/StudioCanvasStateTest.kt`

**Interfaces:**
- Produces: `CanvasElementType`, `CanvasTransform`, `StudioCanvasElement`, `StudioCanvasState`, `StudioCanvasAction`, `reduceCanvasState`.

- [ ] **Step 1: Write failing reducer tests**

```kotlin
@Test fun selectMoveResizeRotateFlipDuplicateDeleteAndReorderAreDeterministic() {
    var state = StudioCanvasState.empty(StudioCanvasSize(1080, 1080))
    state = reduceCanvasState(state, StudioCanvasAction.AddElement(testImage))
    state = reduceCanvasState(state, StudioCanvasAction.Select(testImage.id))
    state = reduceCanvasState(state, StudioCanvasAction.MoveSelected(40f, 30f))
    state = reduceCanvasState(state, StudioCanvasAction.ResizeSelected(0.5f, 0.5f))
    state = reduceCanvasState(state, StudioCanvasAction.RotateSelected(45f))
    state = reduceCanvasState(state, StudioCanvasAction.FlipSelected(horizontal = true, vertical = false))
    assertEquals(testImage.id, state.selectedElementId)
    assertEquals(45f, state.selectedElement!!.transform.rotationDegrees)
}

@Test fun deletingSelectionClearsSelection() {
    val state = reduceCanvasState(
        reduceCanvasState(StudioCanvasState.empty(size), StudioCanvasAction.AddElement(testImage)),
        StudioCanvasAction.Delete(testImage.id),
    )
    assertEquals(null, state.selectedElementId)
}
```

- [ ] **Step 2: Run RED**

```bash
./gradlew testDebugUnitTest --tests 'com.patsy.app.studio.canvas.StudioCanvasStateTest'
```

- [ ] **Step 3: Implement focused immutable model**

Use normalized logical coordinates relative to canvas size so the editor can render at any viewport scale without changing export geometry.

```kotlin
enum class CanvasElementType { IMAGE, TEXT, SHAPE, STICKER }

data class CanvasTransform(
    val centerX: Float = 0.5f,
    val centerY: Float = 0.5f,
    val widthFraction: Float = 0.5f,
    val heightFraction: Float = 0.5f,
    val rotationDegrees: Float = 0f,
    val flipHorizontal: Boolean = false,
    val flipVertical: Boolean = false,
    val opacity: Float = 1f,
)

data class StudioCanvasElement(
    val id: String,
    val type: CanvasElementType,
    val content: String,
    val transform: CanvasTransform = CanvasTransform(),
)

data class StudioCanvasState(
    val canvasSize: StudioCanvasSize,
    val elements: List<StudioCanvasElement>,
    val selectedElementId: String?,
) {
    val selectedElement: StudioCanvasElement? get() = elements.firstOrNull { it.id == selectedElementId }
    companion object { fun empty(size: StudioCanvasSize) = StudioCanvasState(size, emptyList(), null) }
}
```

Implement actions for add/select/move/resize/rotate/flip/opacity/duplicate/delete/bring-forward/send-backward, clamping fractions and opacity to safe ranges.

- [ ] **Step 4: Run targeted and full studio tests**

```bash
./gradlew testDebugUnitTest --tests 'com.patsy.app.studio.canvas.*'
./gradlew testDebugUnitTest --tests 'com.patsy.app.studio.*'
```

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/patsy/app/studio/canvas/StudioCanvasState.kt app/src/test/java/com/patsy/app/studio/canvas/StudioCanvasStateTest.kt
git commit -m "feat: add deterministic THyNK canvas reducer"
```

### Task 3: Add real undo/redo history around canvas reducer

**Files:**
- Create: `app/src/main/java/com/patsy/app/studio/canvas/StudioCanvasHistory.kt`
- Create: `app/src/test/java/com/patsy/app/studio/canvas/StudioCanvasHistoryTest.kt`

**Interfaces:**
- Produces: `StudioCanvasHistory`, `applyCanvasAction`, `undoCanvas`, `redoCanvas`.

- [ ] **Step 1: Write RED tests**

```kotlin
@Test fun actionCanUndoAndRedoWithoutLosingFutureState() {
    val initial = StudioCanvasHistory.initial(StudioCanvasState.empty(size))
    val edited = applyCanvasAction(initial, StudioCanvasAction.AddElement(testImage))
    val undone = undoCanvas(edited)
    val redone = redoCanvas(undone)
    assertTrue(undone.present.elements.isEmpty())
    assertEquals(1, redone.present.elements.size)
}

@Test fun newEditAfterUndoClearsRedoStack() {
    val edited = applyCanvasAction(initial, StudioCanvasAction.AddElement(testImage))
    val undone = undoCanvas(edited)
    val branched = applyCanvasAction(undone, StudioCanvasAction.AddElement(otherImage))
    assertTrue(branched.future.isEmpty())
}
```

- [ ] **Step 2: Run RED**

```bash
./gradlew testDebugUnitTest --tests 'com.patsy.app.studio.canvas.StudioCanvasHistoryTest'
```

- [ ] **Step 3: Implement bounded immutable history**

```kotlin
data class StudioCanvasHistory(
    val past: List<StudioCanvasState>,
    val present: StudioCanvasState,
    val future: List<StudioCanvasState>,
) {
    companion object { fun initial(state: StudioCanvasState) = StudioCanvasHistory(emptyList(), state, emptyList()) }
}

private const val MAX_CANVAS_HISTORY = 50

fun applyCanvasAction(history: StudioCanvasHistory, action: StudioCanvasAction): StudioCanvasHistory {
    val next = reduceCanvasState(history.present, action)
    if (next == history.present) return history
    return StudioCanvasHistory((history.past + history.present).takeLast(MAX_CANVAS_HISTORY), next, emptyList())
}
```

Implement symmetric `undoCanvas`/`redoCanvas`.

- [ ] **Step 4: Run tests and commit**

```bash
./gradlew testDebugUnitTest --tests 'com.patsy.app.studio.canvas.*'
git add app/src/main/java/com/patsy/app/studio/canvas app/src/test/java/com/patsy/app/studio/canvas
git commit -m "feat: add THyNK canvas undo redo"
```

### Task 4: Add fail-closed Android image import policy

**Files:**
- Create: `app/src/main/java/com/patsy/app/studio/importing/StudioImportPolicy.kt`
- Create: `app/src/test/java/com/patsy/app/studio/importing/StudioImportPolicyTest.kt`
- Create: `app/src/main/java/com/patsy/app/studio/importing/AndroidStudioImageImporter.kt`

**Interfaces:**
- Produces: `StudioImportCandidate`, `StudioImportDecision`, `validateStudioImport`, `AndroidStudioImageImporter.resolve(uri)`.

- [ ] **Step 1: Write pure RED policy tests**

```kotlin
@Test fun imageImportAcceptsSupportedReadableImage() {
    val decision = validateStudioImport(StudioImportCandidate("image/png", true, 1200, 1200, 2_000_000))
    assertTrue(decision is StudioImportDecision.Accept)
}

@Test fun importRejectsUnsupportedUnreadableOrOversizedMedia() {
    assertTrue(validateStudioImport(StudioImportCandidate("application/pdf", true, 1200, 1200, 10)) is StudioImportDecision.Reject)
    assertTrue(validateStudioImport(StudioImportCandidate("image/png", false, 1200, 1200, 10)) is StudioImportDecision.Reject)
    assertTrue(validateStudioImport(StudioImportCandidate("image/png", true, 1200, 1200, 100_000_000)) is StudioImportDecision.Reject)
}
```

- [ ] **Step 2: Run RED**

```bash
./gradlew testDebugUnitTest --tests 'com.patsy.app.studio.importing.StudioImportPolicyTest'
```

- [ ] **Step 3: Implement pure validation**

Support `image/png`, `image/jpeg`, and `image/webp` initially. Require readable URI, positive dimensions, dimensions no larger than 8192 each, and a central encoded-size cap of 32 MiB.

- [ ] **Step 4: Implement Android resolver**

`AndroidStudioImageImporter` must use `ContentResolver`, inspect MIME/type and bounds without silently copying untrusted files, request persistable URI permission when the picker grants it, and return a typed `Accept`/`Reject` result. Do not manufacture an example URI on error.

- [ ] **Step 5: Run tests/build and commit**

```bash
./gradlew testDebugUnitTest --tests 'com.patsy.app.studio.importing.*'
./gradlew assembleDebug --stacktrace
git add app/src/main/java/com/patsy/app/studio/importing app/src/test/java/com/patsy/app/studio/importing
git commit -m "feat: validate native THyNK image imports"
```

### Task 5: Implement Android PNG exporter with truthful state

**Files:**
- Create: `app/src/main/java/com/patsy/app/studio/export/StudioExportState.kt`
- Create: `app/src/test/java/com/patsy/app/studio/export/StudioExportStateTest.kt`
- Create: `app/src/main/java/com/patsy/app/studio/export/AndroidStudioPngExporter.kt`

**Interfaces:**
- Produces: `StudioExportFormat.PNG`, `StudioExportState`, `StudioExportResult`, `StudioPngExporter.export(project)`.

- [ ] **Step 1: Write RED state tests**

```kotlin
@Test fun successRequiresWrittenUri() {
    assertFailsWith<IllegalArgumentException> { StudioExportResult.Success("") }
}

@Test fun failedExportNeverBecomesSuccess() {
    val failed = reduceExportState(StudioExportState.Idle, StudioExportEvent.Failed("write failed"))
    assertTrue(failed is StudioExportState.Failed)
}
```

- [ ] **Step 2: Run RED**

```bash
./gradlew testDebugUnitTest --tests 'com.patsy.app.studio.export.StudioExportStateTest'
```

- [ ] **Step 3: Implement truthful export state contract**

```kotlin
enum class StudioExportFormat { PNG }
sealed interface StudioExportState {
    data object Idle : StudioExportState
    data object Exporting : StudioExportState
    data class Complete(val uri: String) : StudioExportState
    data class Failed(val message: String) : StudioExportState
}
sealed interface StudioExportResult {
    data class Success(val uri: String) : StudioExportResult { init { require(uri.isNotBlank()) } }
    data class Failure(val message: String) : StudioExportResult
}
```

- [ ] **Step 4: Implement real logical-size renderer**

`AndroidStudioPngExporter` must create a bitmap at `StudioCanvasState.canvasSize`, render the supported element stack in layer order with each normalized transform mapped to logical pixels, compress with `Bitmap.CompressFormat.PNG`, and only return `Success(uri)` after the output stream is flushed/closed and the destination is readable. Use an app-owned export destination or MediaStore appropriate to the current minSdk; do not screenshot the Compose viewport.

Initial renderer support must include background, imported bitmap image, simple text and shape/sticker placeholders only when those placeholders are actual project elements. Unsupported element types must return `Failure` rather than silently omitting production content.

- [ ] **Step 5: Compile and record device-verification gap**

```bash
./gradlew testDebugUnitTest --tests 'com.patsy.app.studio.export.*'
./gradlew assembleDebug --stacktrace
```
Expected: unit state tests and Android compilation pass. Do not claim real-device file visibility/readability until physical-device QA runs.

- [ ] **Step 6: Commit**

```bash
git add app/src/main/java/com/patsy/app/studio/export app/src/test/java/com/patsy/app/studio/export
git commit -m "feat: add real PNG export pipeline"
```

### Task 6: Build the Compose canvas editor and explicit resize handles

**Files:**
- Create: `app/src/main/java/com/patsy/app/studio/canvas/ThynkCanvasEditorScreen.kt`
- Modify: `app/src/main/java/com/patsy/app/thynk/ThynkStudioScreen.kt`
- Test: existing unit tests plus new pure route mapping test if routing logic is extracted.

**Interfaces:**
- Consumes: `StudioCanvasHistory`, import/export interfaces, `StudioCanvasPresets`.
- Produces: one nested image-editor route inside current THyNK; no new outer navigation.

- [ ] **Step 1: Extract/test route mapping before UI wiring**

Create a pure mapping from applicable THyNK items such as `BLANK DESIGNS`, `CUSTOM SIZE`, `SQUARE POST`, `EDIT PHOTO`, `PHOTO COLLAGE` to an editor launch config. Write a test proving Music items remain Music routes and Video items remain owned by the Media3 editor.

```kotlin
@Test fun customSizeLaunchesImageCanvasWithoutChangingPrimaryRoute() {
    val launch = thynkEditorLaunchFor(categoryId = "design", item = "CUSTOM SIZE")
    assertEquals(StudioMode.IMAGE, launch?.mode)
}
```

- [ ] **Step 2: Run RED, then implement minimal mapping**

```bash
./gradlew testDebugUnitTest --tests 'com.patsy.app.thynk.*'
```

- [ ] **Step 3: Implement canvas UI**

The screen must show the logical canvas scaled within available space, selection bounds, four corner resize handles inspired by the Gemini donor reference, move/rotate/flip/duplicate/delete/layer actions, undo/redo, preset/custom size control, Import and Export actions. Use Patsy charcoal/white/rainbow tokens rather than the donor Tailwind visual style.

Use Compose pointer input for touch transforms; do not paste React DOM drag code.

- [ ] **Step 4: Wire Android image picker**

Use `rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument())` with supported image MIME types. Pass the returned URI through `AndroidStudioImageImporter`; add a layer only on `Accept`.

- [ ] **Step 5: Wire PNG export**

Export button must transition `Idle -> Exporting -> Complete/Failed` from the real exporter. Display the returned URI only on real success.

- [ ] **Step 6: Build and commit**

```bash
./gradlew testDebugUnitTest --stacktrace
./gradlew assembleDebug --stacktrace
./gradlew assembleRelease --stacktrace
git add app/src/main/java/com/patsy/app/studio app/src/main/java/com/patsy/app/thynk app/src/test/java/com/patsy/app
git commit -m "feat: add native THyNK canvas image editor"
```

### Task 7: Final verification and device checklist

- [ ] **Step 1: Full CI-equivalent verification**

```bash
./gradlew testDebugUnitTest --stacktrace
./gradlew assembleDebug --stacktrace
./gradlew assembleRelease --stacktrace
```
Expected: all exit 0 before claiming GREEN.

- [ ] **Step 2: Physical Android QA required before merge**

Verify on device: open THyNK, launch image canvas, switch preset, custom size, import a real image, select/move/resize using all four handles, rotate/flip, duplicate/delete/reorder, undo/redo, export PNG, open/read exported PNG, confirm logical dimensions, navigate away through the permanent primary bar, return without an auth bypass.

- [ ] **Step 3: Report evidence**

Report exact start/end SHA, changed files, RED tests observed, GREEN tests/debug/release, final CI run and artifact IDs. Explicitly state whether device import/export was actually verified. Keep PR #38 Draft.

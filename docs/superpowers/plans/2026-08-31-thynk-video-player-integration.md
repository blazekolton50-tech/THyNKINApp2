# THyNK Video Player Integration Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add the repaired PR #23 Media3 video-player foundation to the current PR #38 THyNK Studio without restoring the obsolete PR #23 THyNK home/navigation design.

**Architecture:** Keep `com.patsy.app.studio` as the reusable editor-state/player engine and `com.patsy.app.thynk` as the current locked THyNK navigation/catalog UI. Add Media3 dependencies only, port `StudioVideoPlayer`, then expose it through a nested video-editor route inside the existing `ThynkStudioScreen`; the permanent Patsy bottom navigation remains outside this nested route in `FinalMainActivity`.

**Tech Stack:** Android/Kotlin, Jetpack Compose, AndroidX Media3 1.8.1, Kotlin unit tests, GitHub Actions.

**Spec:** `docs/PATSY_DESIGN_PRESERVATION_MASTER_2026-08-31.md`

## Global Constraints

- Work only in `blazekolton50-tech/PatsyApp`.
- Work from Draft PR #38 branch `chatgpt/thynk-music-apk-2026-08-31`.
- Do not merge to `main`.
- Preserve FINAL Login / Set Password / Home visuals.
- Preserve the current 10-category THyNK architecture.
- Never wholesale-port PR #23 `ThynkStudioScreen.kt`.
- Permanent primary navigation remains available while the editor is open.
- Provider-backed AI/export/cutout/music actions must remain truthful and must not fake success.
- Production Rive remains unverified until a real approved `.riv` is supplied and device-tested.
- Keep compileSdk 35 / targetSdk 35 / minSdk 26 and current Rive/auth/Supabase dependencies intact.

---

### Task 1: Lock the player formatting/synchronisation contract

**Files:**
- Create: `app/src/test/java/com/patsy/app/studio/StudioVideoPlayerLogicTest.kt`
- Create: `app/src/main/java/com/patsy/app/studio/StudioVideoPlayerLogic.kt`

**Interfaces:**
- Consumes: `StudioEditorState`, `StudioMediaStatus`, `timelineFraction`, `timeFromTimelineFraction`.
- Produces: `formatStudioTime(Int): String`, `shouldSeekPlayer(playerPositionMs: Long, statePositionMs: Int, canPlay: Boolean): Boolean`.

- [ ] **Step 1: Write the failing test**

```kotlin
package com.patsy.app.studio

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class StudioVideoPlayerLogicTest {
    @Test
    fun `time formatting is stable and non-negative`() {
        assertEquals("0:00", formatStudioTime(-100))
        assertEquals("0:05", formatStudioTime(5_000))
        assertEquals("1:05", formatStudioTime(65_000))
    }

    @Test
    fun `player only seeks when playable and meaningfully out of sync`() {
        assertFalse(shouldSeekPlayer(2_000, 2_200, canPlay = true))
        assertTrue(shouldSeekPlayer(2_000, 2_500, canPlay = true))
        assertFalse(shouldSeekPlayer(2_000, 2_500, canPlay = false))
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run:
```bash
./gradlew testDebugUnitTest --tests com.patsy.app.studio.StudioVideoPlayerLogicTest --stacktrace
```
Expected: FAIL because `formatStudioTime` and `shouldSeekPlayer` do not yet exist in production code.

- [ ] **Step 3: Write minimal implementation**

```kotlin
package com.patsy.app.studio

import kotlin.math.abs

internal fun formatStudioTime(timeMs: Int): String {
    val safe = timeMs.coerceAtLeast(0)
    val totalSeconds = safe / 1_000
    return "%d:%02d".format(totalSeconds / 60, totalSeconds % 60)
}

internal fun shouldSeekPlayer(
    playerPositionMs: Long,
    statePositionMs: Int,
    canPlay: Boolean,
): Boolean = canPlay && abs(playerPositionMs - statePositionMs) > 350L
```

- [ ] **Step 4: Run test to verify it passes**

Run the same targeted test. Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add app/src/test/java/com/patsy/app/studio/StudioVideoPlayerLogicTest.kt app/src/main/java/com/patsy/app/studio/StudioVideoPlayerLogic.kt
git commit -m "test: lock THyNK video player sync logic"
```

---

### Task 2: Add Media3 without disturbing the current dependency/security baseline

**Files:**
- Modify: `app/build.gradle.kts`

**Interfaces:**
- Consumes: existing Compose/Rive/Auth dependency block.
- Produces: Media3 ExoPlayer and UI classes usable by `StudioVideoPlayer`.

- [ ] **Step 1: Confirm the current dependency block has no Media3 entries**

Run:
```bash
grep -n "media3" app/build.gradle.kts || true
```
Expected before change: no Media3 implementation lines.

- [ ] **Step 2: Add only these dependencies**

```kotlin
implementation("androidx.media3:media3-exoplayer:1.8.1")
implementation("androidx.media3:media3-ui:1.8.1")
```

Do not alter existing Supabase `buildConfigField`s, Rive exclusions, Compose BOM, DataStore, Security Crypto or test dependencies.

- [ ] **Step 3: Compile**

Run:
```bash
./gradlew compileDebugKotlin --stacktrace
```
Expected: PASS.

- [ ] **Step 4: Commit**

```bash
git add app/build.gradle.kts
git commit -m "build: add Media3 for THyNK video preview"
```

---

### Task 3: Port the reusable Media3 player, not the obsolete PR #23 screen

**Files:**
- Create: `app/src/main/java/com/patsy/app/studio/StudioVideoPlayer.kt`

**Interfaces:**
- Consumes: `StudioEditorState`, `StudioAction`, `formatStudioTime`, `shouldSeekPlayer`.
- Produces: `@Composable fun StudioVideoPlayer(sourceUri: String?, state: StudioEditorState, onAction: (StudioAction) -> Unit, modifier: Modifier = Modifier)`.

- [ ] **Step 1: Read source PR #23 player fully**

Source branch: `chatgpt/thynk-studio-foundation`
Source path: `app/src/main/java/com/patsy/app/studio/StudioVideoPlayer.kt`.

Port the ExoPlayer lifecycle/listener behavior but use `shouldSeekPlayer(...)` from Task 1 instead of duplicating the 350ms drift rule.

- [ ] **Step 2: Preserve truthful media lifecycle**

Required behavior:
- null/blank source -> `StudioAction.ClearMedia`;
- new source -> `StudioAction.LoadMedia` then `player.prepare()`;
- ready duration -> `StudioAction.MediaReady(duration)` only when duration is finite and positive;
- playback error -> `StudioAction.MediaFailed(message)`;
- `state.canPlay == false` forces pause;
- listener writes actual playing state through `StudioAction.SetPlaying`;
- seek only through `shouldSeekPlayer`;
- release player in `onDispose`.

- [ ] **Step 3: Compile and run unit tests**

```bash
./gradlew testDebugUnitTest --stacktrace
./gradlew compileDebugKotlin --stacktrace
```
Expected: PASS.

- [ ] **Step 4: Commit**

```bash
git add app/src/main/java/com/patsy/app/studio/StudioVideoPlayer.kt
git commit -m "feat: add THyNK Media3 video preview"
```

---

### Task 4: Route the current VIDEO & CAMERA category into the editor

**Files:**
- Modify: `app/src/main/java/com/patsy/app/thynk/ThynkStudioScreen.kt`
- Test: `app/src/test/java/com/patsy/app/thynk/ThynkStudioContractTest.kt`

**Interfaces:**
- Consumes: `StudioMode.VIDEO`, `StudioEditorState.video`, `reduceStudioState`, `StudioVideoPlayer`.
- Produces: one nested current-THyNK video-editor route; no second THyNK home.

- [ ] **Step 1: Add a failing routing contract test**

Extend `ThynkStudioContractTest` with a pure mapping helper contract so UI routing is deterministic:

```kotlin
@Test
fun `video editor items map to the shared video editor route`() {
    assertEquals("video-editor", editorPageForThynkItem("VIDEO EDITOR"))
    assertEquals("video-editor", editorPageForThynkItem("TRIM & CUT"))
    assertEquals(null, editorPageForThynkItem("POSTERS"))
}
```

- [ ] **Step 2: Run test and verify RED**

```bash
./gradlew testDebugUnitTest --tests com.patsy.app.thynk.ThynkStudioContractTest --stacktrace
```
Expected: FAIL because `editorPageForThynkItem` does not exist.

- [ ] **Step 3: Add minimal mapping helper**

```kotlin
internal fun editorPageForThynkItem(item: String): String? = when (item) {
    "VIDEO EDITOR", "TRIM & CUT", "SPLIT", "TRANSITIONS", "SUBTITLES", "OVERLAYS",
    "GREEN SCREEN", "VIDEO FILTERS", "SLOW MOTION", "SLIDESHOW", "LOOP", "ASPECT RESIZER" -> "video-editor"
    else -> null
}
```

- [ ] **Step 4: Add nested editor route to the existing `ThynkRoute`**

Add:
```kotlin
data class Editor(val pageId: String) : ThynkRoute
```

On VIDEO & CAMERA category-item click, if `editorPageForThynkItem(item) != null`, route to `ThynkRoute.Editor("video-editor")`.

Do not change the 10-category hub.

- [ ] **Step 5: Render the editor inside the existing THyNK content box**

For `video-editor`, create remembered state:
```kotlin
var editorState by remember { mutableStateOf(StudioEditorState.video(durationMs = 0)) }
```

Render the current locked THyNK header/back behavior plus `StudioVideoPlayer`. Until a real Android picker/import flow is wired, `sourceUri` may remain null and must display the truthful empty state. Do not invent a sample video as production media.

- [ ] **Step 6: Verify nested route leaves the global bottom bar intact**

The bottom bar is owned by `FinalMainActivity`, outside `ThynkStudioScreen`; do not add or hide a second bar in the editor.

- [ ] **Step 7: Full verification**

```bash
./gradlew testDebugUnitTest --stacktrace
./gradlew assembleDebug --stacktrace
./gradlew assembleRelease --stacktrace
```
Expected: PASS.

- [ ] **Step 8: Commit**

```bash
git add app/src/main/java/com/patsy/app/thynk/ThynkStudioScreen.kt app/src/test/java/com/patsy/app/thynk/ThynkStudioContractTest.kt
git commit -m "feat: route THyNK video tools to shared editor"
```

---

## Self-review

- Spec coverage: Media3 preview, truthful ready/failed lifecycle, timeline state reuse, current THyNK architecture, permanent outer navigation all covered.
- No placeholders: every code-producing step contains exact behavior or code.
- Type consistency: tasks use existing `StudioEditorState`, `StudioAction`, `StudioMode` names already green on PR #38.
- Explicit exclusion: old PR #23 `ThynkStudioScreen.kt` is never ported wholesale.

## Completion report required

Report exact head SHA, changed files, RED test observed, GREEN tests, debug/release build results, CI run/result, and any device/media-picker limitations that remain.

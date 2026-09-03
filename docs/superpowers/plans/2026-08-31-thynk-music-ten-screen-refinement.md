# THyNK Music Ten-Screen Refinement Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Refine the existing native ten-page THyNK Music foundation toward the owner-supplied 10-screen visual reference while keeping provider/DSP/audio-file capabilities truthful and preserving the permanent Patsy navigation shell.

**Architecture:** Keep the stable Music route IDs in `ThynkMusicCatalog`, split Music UI/state out of the oversized `ThynkStudioScreen.kt`, and model local editor settings separately from real audio capability. Visual controls can be interactive when they change local state; generation, mastering, vocal processing and audio export stay unavailable until a real provider/engine exists.

**Tech Stack:** Kotlin, Jetpack Compose, existing Patsy design tokens, existing PR #38 shell. No new music provider or DSP dependency in this plan.

**Spec:** `docs/superpowers/specs/2026-08-31-thynk-donor-consolidation-design.md`

## Global Constraints

- Work only on PR #38 branch `chatgpt/thynk-music-apk-2026-08-31`; keep Draft.
- Execute after Media3, global navigation, donor intake, and canvas/image vertical slice are GREEN.
- Preserve ten IDs: `music-home`, `create-music`, `ai-music-generator`, `track-editor`, `mixer`, `equalizer`, `effects`, `lyrics-vocals`, `mastering`, `export`.
- Preserve outer bottom navigation on every Music page.
- Do not create a Music-only replacement bottom bar.
- Black/charcoal surfaces, white text, restrained purple/rainbow highlights, compact DAW-like hierarchy.
- Do not show fake generation percentages, fake audio waveforms presented as media, fake mastered output, fake stems or fake export success.
- Local controls may edit configuration/state without claiming DSP has been applied.
- Existing `NOT_CONFIGURED` provider truth is binding until an approved provider exists.

---

### Task 1: Replace string provider states with typed Music capability truth

**Files:**
- Modify: `app/src/main/java/com/patsy/app/thynk/ThynkStudioCatalog.kt`
- Create: `app/src/main/java/com/patsy/app/thynk/music/ThynkMusicCapabilities.kt`
- Create: `app/src/test/java/com/patsy/app/thynk/music/ThynkMusicCatalogTest.kt`

**Interfaces:**
- Produces: `MusicCapabilityState`, `MusicCapability`, `ThynkMusicCapabilities`, typed `ThynkMusicPage.capabilityState`.

- [ ] **Step 1: Write failing route/capability contract tests**

```kotlin
package com.patsy.app.thynk.music

import com.patsy.app.thynk.ThynkMusicCatalog
import kotlin.test.Test
import kotlin.test.assertEquals

class ThynkMusicCatalogTest {
    @Test fun tenStablePageIdsRemainInReferenceOrder() {
        assertEquals(
            listOf("music-home", "create-music", "ai-music-generator", "track-editor", "mixer", "equalizer", "effects", "lyrics-vocals", "mastering", "export"),
            ThynkMusicCatalog.pages.map { it.id },
        )
    }

    @Test fun providerBackedPagesRemainNotConfigured() {
        val states = ThynkMusicCatalog.pages.associate { it.id to it.capabilityState }
        assertEquals(MusicCapabilityState.NOT_CONFIGURED, states["create-music"])
        assertEquals(MusicCapabilityState.NOT_CONFIGURED, states["ai-music-generator"])
        assertEquals(MusicCapabilityState.NOT_CONFIGURED, states["mastering"])
        assertEquals(MusicCapabilityState.NOT_CONFIGURED, states["export"])
    }
}
```

- [ ] **Step 2: Run RED**

```bash
./gradlew testDebugUnitTest --tests 'com.patsy.app.thynk.music.ThynkMusicCatalogTest'
```
Expected: FAIL because `capabilityState` is not typed yet.

- [ ] **Step 3: Implement typed capability model**

```kotlin
package com.patsy.app.thynk.music

enum class MusicCapabilityState { LOCAL_UI, NOT_CONFIGURED, READY, FAILED }
enum class MusicCapability { GENERATION, AUDIO_IMPORT, RECORDING, DSP, VOCAL_PROCESSING, MASTERING, AUDIO_EXPORT }

data class MusicCapabilitySnapshot(
    val states: Map<MusicCapability, MusicCapabilityState>,
) {
    fun stateFor(capability: MusicCapability): MusicCapabilityState = states[capability] ?: MusicCapabilityState.NOT_CONFIGURED
}
```

Change `ThynkMusicPage.providerState: String` to `capabilityState: MusicCapabilityState`; do not change the ten route IDs.

- [ ] **Step 4: Run tests and commit**

```bash
./gradlew testDebugUnitTest --tests 'com.patsy.app.thynk.music.*'
./gradlew testDebugUnitTest
git add app/src/main/java/com/patsy/app/thynk app/src/test/java/com/patsy/app/thynk
git commit -m "refactor: type THyNK Music capability states"
```

### Task 2: Extract Music route mapping and split oversized THyNK screen

**Files:**
- Create: `app/src/main/java/com/patsy/app/thynk/music/ThynkMusicRoute.kt`
- Create: `app/src/main/java/com/patsy/app/thynk/music/ThynkMusicScreen.kt`
- Create: `app/src/main/java/com/patsy/app/thynk/music/ThynkMusicComponents.kt`
- Modify: `app/src/main/java/com/patsy/app/thynk/ThynkStudioScreen.kt`
- Create: `app/src/test/java/com/patsy/app/thynk/music/ThynkMusicRouteTest.kt`

**Interfaces:**
- Produces: `ThynkMusicRoute`, `musicPageForItem(item)`, `ThynkMusicScreen(pageId, onOpenPage)`.

- [ ] **Step 1: Write RED route tests before moving code**

```kotlin
@Test fun categoryItemsMapToStableMusicPages() {
    assertEquals("create-music", musicPageForItem("CREATE MUSIC"))
    assertEquals("ai-music-generator", musicPageForItem("AI MUSIC GENERATOR"))
    assertEquals("track-editor", musicPageForItem("RECORD"))
    assertEquals("track-editor", musicPageForItem("IMPORT AUDIO"))
    assertEquals("mixer", musicPageForItem("MIXER"))
    assertEquals("mastering", musicPageForItem("MASTERING"))
    assertEquals("lyrics-vocals", musicPageForItem("LYRICS & VOCALS"))
}
```

- [ ] **Step 2: Run RED then extract without behaviour change**

```bash
./gradlew testDebugUnitTest --tests 'com.patsy.app.thynk.music.ThynkMusicRouteTest'
```

Create:
```kotlin
sealed interface ThynkMusicRoute {
    data class Page(val pageId: String) : ThynkMusicRoute
}

fun musicPageForItem(item: String): String = when (item) {
    "CREATE MUSIC" -> "create-music"
    "AI MUSIC GENERATOR" -> "ai-music-generator"
    "TRACK EDITOR", "RECORD", "IMPORT AUDIO", "LOOPS & SAMPLES", "STEMS", "SOUND EFFECTS", "MY MUSIC" -> "track-editor"
    "MIXER" -> "mixer"
    "LYRICS & VOCALS" -> "lyrics-vocals"
    "MASTERING" -> "mastering"
    else -> "music-home"
}
```

Move Music-only composables/components out of `ThynkStudioScreen.kt`; keep THyNK hub/category routing there. Do not move `LockedCameraHub` into Music.

- [ ] **Step 3: Run full unit/debug build**

```bash
./gradlew testDebugUnitTest
./gradlew assembleDebug --stacktrace
```
Expected: PASS with no route change.

- [ ] **Step 4: Commit**

```bash
git add app/src/main/java/com/patsy/app/thynk app/src/test/java/com/patsy/app/thynk
git commit -m "refactor: isolate THyNK Music UI from studio shell"
```

### Task 3: Add local Music editor state without pretending to process audio

**Files:**
- Create: `app/src/main/java/com/patsy/app/thynk/music/ThynkMusicEditorState.kt`
- Create: `app/src/test/java/com/patsy/app/thynk/music/ThynkMusicEditorStateTest.kt`

**Interfaces:**
- Produces: `MusicTrackState`, `MixerChannelState`, `EqualizerBandState`, `MusicEffectSetting`, `ThynkMusicEditorState`, `MusicEditorAction`, `reduceMusicEditorState`.

- [ ] **Step 1: Write RED reducer tests**

```kotlin
@Test fun mixerAndEqChangesAreLocalConfigurationOnly() {
    var state = ThynkMusicEditorState.empty()
    state = reduceMusicEditorState(state, MusicEditorAction.SetMixerLevel("vocals", 0.7f))
    state = reduceMusicEditorState(state, MusicEditorAction.SetEqGain(250, 3f))
    assertEquals(0.7f, state.mixerChannels.getValue("vocals").level)
    assertEquals(3f, state.equalizerBands.first { it.frequencyHz == 250 }.gainDb)
    assertEquals(MusicCapabilityState.NOT_CONFIGURED, state.dspState)
}

@Test fun noAudioLoadedMeansNoFakeWaveformData() {
    val state = ThynkMusicEditorState.empty()
    assertTrue(state.tracks.all { it.waveformSamples.isEmpty() })
}
```

- [ ] **Step 2: Run RED**

```bash
./gradlew testDebugUnitTest --tests 'com.patsy.app.thynk.music.ThynkMusicEditorStateTest'
```

- [ ] **Step 3: Implement local configuration model**

```kotlin
data class MusicTrackState(
    val id: String,
    val name: String,
    val muted: Boolean = false,
    val solo: Boolean = false,
    val sourceUri: String? = null,
    val waveformSamples: List<Float> = emptyList(),
)

data class MixerChannelState(val level: Float = 0.75f, val pan: Float = 0f)
data class EqualizerBandState(val frequencyHz: Int, val gainDb: Float = 0f)

data class ThynkMusicEditorState(
    val tracks: List<MusicTrackState>,
    val mixerChannels: Map<String, MixerChannelState>,
    val equalizerBands: List<EqualizerBandState>,
    val lyrics: String,
    val dspState: MusicCapabilityState,
    val masteringState: MusicCapabilityState,
    val exportState: MusicCapabilityState,
)
```

Initialize named track slots only as UI/editor structure; do not populate `sourceUri` or waveform samples until real media exists. Clamp mixer level 0..1, pan -1..1 and EQ gain to the documented UI range.

- [ ] **Step 4: Run tests and commit**

```bash
./gradlew testDebugUnitTest --tests 'com.patsy.app.thynk.music.*'
git add app/src/main/java/com/patsy/app/thynk/music app/src/test/java/com/patsy/app/thynk/music
git commit -m "feat: add truthful THyNK Music editor state"
```

### Task 4: Refine Music Home, Create Music and AI Generator screens

**Files:**
- Create: `app/src/main/java/com/patsy/app/thynk/music/pages/MusicHomePage.kt`
- Create: `app/src/main/java/com/patsy/app/thynk/music/pages/CreateMusicPage.kt`
- Create: `app/src/main/java/com/patsy/app/thynk/music/pages/AiMusicGeneratorPage.kt`
- Modify: `app/src/main/java/com/patsy/app/thynk/music/ThynkMusicScreen.kt`

- [ ] **Step 1: Build Home toward reference layout**

Implement `Create. Record. Edit. Mix. Master.`, a decorative multicolour audio motif clearly marked as visual branding rather than loaded media, Quick Start cards for Create Music / AI Music / Record / Import, and truthful empty Recent Projects/My Music state.

- [ ] **Step 2: Build Create Music inputs**

Prompt field, style chips, mood chips and duration control may edit local request configuration. The primary generation action remains disabled when `GENERATION != READY` and must visibly say `NOT CONFIGURED` rather than simulating output.

- [ ] **Step 3: Build AI Generator state**

Match the circular visual target but replace the reference's fake `72%` composing state with one of: `Ready to generate` only when a provider is actually READY, `Provider not configured`, real loading state only after a real request starts, or real failure. Never use a timer to simulate progress.

- [ ] **Step 4: Compile and commit**

```bash
./gradlew testDebugUnitTest
./gradlew assembleDebug --stacktrace
git add app/src/main/java/com/patsy/app/thynk/music
git commit -m "feat: refine THyNK Music creation screens"
```

### Task 5: Refine Track Editor, Mixer, Equalizer and Effects screens

**Files:**
- Create: `app/src/main/java/com/patsy/app/thynk/music/pages/TrackEditorPage.kt`
- Create: `app/src/main/java/com/patsy/app/thynk/music/pages/MixerPage.kt`
- Create: `app/src/main/java/com/patsy/app/thynk/music/pages/EqualizerPage.kt`
- Create: `app/src/main/java/com/patsy/app/thynk/music/pages/EffectsPage.kt`
- Modify: `app/src/main/java/com/patsy/app/thynk/music/ThynkMusicComponents.kt`

- [ ] **Step 1: Track Editor**

Implement compact track rows with mute/solo controls, timeline ruler, playhead shell and Add Track/Split/Trim/Delete/More actions. Only render coloured waveform samples when a `MusicTrackState` contains real sample data; empty tracks use an empty lane/grid, not fabricated waveforms.

- [ ] **Step 2: Mixer**

Implement horizontal channel strip layout with pan/level controls and local dB display. Clearly label that settings are editor configuration until DSP/rendering capability is READY.

- [ ] **Step 3: Equalizer**

Implement a native Compose EQ curve/configuration with bands 32, 64, 125, 250, 500, 1k, 2k, 4k, 8k, 16k plus preset buttons. Changing gains updates local state only; do not claim processed audio.

- [ ] **Step 4: Effects**

Implement rows for Reverb, Delay, Chorus, Distortion, Compressor and Limiter with local enabled/settings states. When DSP is not configured, show `Settings saved — audio processing not configured` or equivalent truthful copy.

- [ ] **Step 5: Test/build/commit**

```bash
./gradlew testDebugUnitTest --tests 'com.patsy.app.thynk.music.*'
./gradlew assembleDebug --stacktrace
git add app/src/main/java/com/patsy/app/thynk/music
git commit -m "feat: refine THyNK Music editor and mixer screens"
```

### Task 6: Refine Lyrics & Vocals, Mastering and Export screens

**Files:**
- Create: `app/src/main/java/com/patsy/app/thynk/music/pages/LyricsVocalsPage.kt`
- Create: `app/src/main/java/com/patsy/app/thynk/music/pages/MasteringPage.kt`
- Create: `app/src/main/java/com/patsy/app/thynk/music/pages/MusicExportPage.kt`

- [ ] **Step 1: Lyrics/Vocals**

Provide editable lyrics and tabs matching the reference hierarchy. AI Write/Rhymes/Improve/Translate and vocal processing actions are disabled or `NOT_CONFIGURED` unless their actual capability becomes READY. Plain manual lyric editing is local and available.

- [ ] **Step 2: Mastering**

Show Auto Master, Equalizer, Stereo Widen, Loudness and Limiter settings. Local settings may be edited; `MASTER TRACK` remains unavailable unless mastering/rendering is genuinely READY. Do not create a fake mastered preview.

- [ ] **Step 3: Export**

Match MP3/WAV/AAC/FLAC and quality/options layout as a configuration UI, but do not enable `EXPORT TRACK` merely because a format is selected. Export is enabled only when `AUDIO_EXPORT == READY` and a real source project exists. Otherwise show `Audio export not configured`.

- [ ] **Step 4: Build and commit**

```bash
./gradlew testDebugUnitTest
./gradlew assembleDebug --stacktrace
./gradlew assembleRelease --stacktrace
git add app/src/main/java/com/patsy/app/thynk/music
git commit -m "feat: refine THyNK Music vocals mastering export UI"
```

### Task 7: Verify ten-screen navigation, permanent bar and no fake capability claims

**Files:**
- Add/modify pure tests under `app/src/test/java/com/patsy/app/thynk/music/` as needed.

- [ ] **Step 1: Add final truthfulness tests**

```kotlin
@Test fun everyCatalogPageHasAUiDestination() {
    assertEquals(ThynkMusicCatalog.pages.map { it.id }.toSet(), musicUiPageIds())
}

@Test fun unavailableAudioExportCannotBecomeCompleteFromLocalFormatChoice() {
    val state = ThynkMusicEditorState.empty()
    val changed = reduceMusicEditorState(state, MusicEditorAction.SelectExportFormat("WAV"))
    assertEquals(MusicCapabilityState.NOT_CONFIGURED, changed.exportState)
}
```

- [ ] **Step 2: Run all verification**

```bash
./gradlew testDebugUnitTest --stacktrace
./gradlew assembleDebug --stacktrace
./gradlew assembleRelease --stacktrace
```
Expected: all exit 0 before reporting GREEN.

- [ ] **Step 3: Physical-device visual QA**

Visit all ten Music screens. Verify visual hierarchy against the supplied 10-screen reference, permanent Patsy primary navigation, back navigation, no fake progress/audio/export result, small-screen scrolling, touch targets and no route escape around auth.

- [ ] **Step 4: Report evidence and keep Draft**

Report exact start/end SHA, changed files, RED and GREEN evidence, final CI run/artifacts, and clearly list provider/DSP/recording/audio-export items still `NOT_CONFIGURED`. Do not merge PR #38.

# THyNK Studio Export Core Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add the first production-safe THyNK Studio export-core primitives: strict muxer lifecycle, sample-cursor AAC input planning, and explicit output-EOS completion.

**Architecture:** Keep codec-independent rules in small pure-Kotlin classes under `com.patsy.app.studio.export` so JVM tests can prove timing/capacity/state invariants without Android codec mocks. Android `MediaCodec`/`MediaMuxer` adapters will consume these primitives in the next integration slice.

**Tech Stack:** Kotlin/JVM tests, Android app module, existing Gradle/Kotlin test stack.

**Spec:** `docs/superpowers/specs/2026-09-02-thynk-studio-ultra-mega-export-core-design.md`

## Global Constraints

- Preserve existing `com.patsy.app` namespace and Compose app shell.
- Do not change Camera, Supabase/auth/security, Owner tools, Patsy/Rive contracts, or current studio persistence.
- Keep current SDK and Media3 versions; do not downgrade to donor versions.
- `sampleCursor` is the only AAC PTS clock.
- Maximum PCM feed per encoder input is 1024 sample frames.
- PCM16 byte capacity is respected exactly.
- Input EOS is not export completion; completion requires AAC output EOS.

---

### Task 1: Strict muxer lifecycle core

**Files:**
- Create: `app/src/test/java/com/patsy/app/studio/export/MuxerCoordinatorCoreTest.kt`
- Create: `app/src/main/java/com/patsy/app/studio/export/MuxerCoordinatorCore.kt`

**Interfaces:**
- Produces: `enum class StudioEncoderKind { VIDEO, AUDIO }`
- Produces: `interface MuxerBackend<F> { fun addTrack(format: F): Int; fun start() }`
- Produces: `class MuxerCoordinatorCore<F>(backend: MuxerBackend<F>, videoOnly: Boolean = false)`
- Produces: `fun registerFormat(kind: StudioEncoderKind, format: F)` and `fun trackIndex(kind: StudioEncoderKind): Int`

- [ ] **Step 1: Write the failing test**

Test these real behaviors with a tiny fake backend:
- registering VIDEO then AUDIO starts exactly once after the second registration,
- duplicate VIDEO throws,
- duplicate AUDIO throws,
- any registration after start throws,
- returned track indices match backend results,
- video-only mode starts after VIDEO without requiring AUDIO.

- [ ] **Step 2: Run test to verify RED**

Run: `./gradlew testDebugUnitTest --tests 'com.patsy.app.studio.export.MuxerCoordinatorCoreTest'`
Expected: FAIL because `MuxerCoordinatorCore` does not exist.

- [ ] **Step 3: Write minimal implementation**

Implement only the state machine required by the tests. `start()` must be invoked once and only when required tracks are present.

- [ ] **Step 4: Run test to verify GREEN**

Run: `./gradlew testDebugUnitTest --tests 'com.patsy.app.studio.export.MuxerCoordinatorCoreTest'`
Expected: PASS.

- [ ] **Step 5: Commit**

Commit message: `feat: add strict studio muxer lifecycle core`

---

### Task 2: Sample-cursor AAC PCM input planner

**Files:**
- Create: `app/src/test/java/com/patsy/app/studio/export/AacPcmInputPlannerTest.kt`
- Create: `app/src/main/java/com/patsy/app/studio/export/AacPcmInputPlanner.kt`

**Interfaces:**
- Produces: `data class AacInputChunk(val startSampleCursor: Long, val frameCount: Int, val byteCount: Int, val ptsUs: Long, val nextSampleCursor: Long)`
- Produces: `object AacPcmInputPlanner`
- Produces: `fun plan(sampleCursor: Long, remainingFrames: Long, inputBufferBytes: Int, sampleRate: Int, channels: Int, bytesPerSample: Int = 2): AacInputChunk?`
- Produces: `fun ptsUs(sampleCursor: Long, sampleRate: Int): Long`

- [ ] **Step 1: Write the failing test**

Cover:
- a large input buffer queues exactly 1024 frames,
- a small buffer queues only `capacityBytes / (channels * bytesPerSample)` frames,
- remaining frames smaller than capacity are not over-read,
- byte count equals `frameCount * channels * bytesPerSample`,
- PTS is derived from the starting sample cursor,
- cursor advances by exactly queued frames,
- zero remaining frames returns null,
- invalid sampleRate/channels/capacity inputs fail fast.

- [ ] **Step 2: Run test to verify RED**

Run: `./gradlew testDebugUnitTest --tests 'com.patsy.app.studio.export.AacPcmInputPlannerTest'`
Expected: FAIL because `AacPcmInputPlanner` does not exist.

- [ ] **Step 3: Write minimal implementation**

Use:
`frameCapacity = inputBufferBytes / (channels * bytesPerSample)`
`frameCount = minOf(1024L, remainingFrames, frameCapacity.toLong()).toInt()`
`ptsUs = sampleCursor * 1_000_000L / sampleRate`

Return null if no complete PCM frame fits.

- [ ] **Step 4: Run test to verify GREEN**

Run: `./gradlew testDebugUnitTest --tests 'com.patsy.app.studio.export.AacPcmInputPlannerTest'`
Expected: PASS.

- [ ] **Step 5: Commit**

Commit message: `feat: add sample cursor AAC input planner`

---

### Task 3: AAC EOS/drain completion state

**Files:**
- Create: `app/src/test/java/com/patsy/app/studio/export/AacEndOfStreamStateTest.kt`
- Create: `app/src/main/java/com/patsy/app/studio/export/AacEndOfStreamState.kt`

**Interfaces:**
- Produces: `data class AacEndOfStreamState(val finalSampleCursor: Long, val finalAudioPtsUs: Long, val inputEosQueued: Boolean = false, val outputEosSeen: Boolean = false)`
- Produces: `fun queueInputEos(): AacEndOfStreamState`
- Produces: `fun observeOutputFlags(flags: Int, endOfStreamFlag: Int): AacEndOfStreamState`
- Produces: `val complete: Boolean`
- Produces: factory `fun fromFinalCursor(finalSampleCursor: Long, sampleRate: Int): AacEndOfStreamState`

- [ ] **Step 1: Write the failing test**

Cover:
- final PTS comes from final sample cursor,
- input EOS changes `inputEosQueued` but does not mark complete,
- unrelated output flags do not mark complete,
- output EOS marks complete only after input EOS was queued,
- duplicate input EOS attempts fail to prevent multiple EOS buffers.

- [ ] **Step 2: Run test to verify RED**

Run: `./gradlew testDebugUnitTest --tests 'com.patsy.app.studio.export.AacEndOfStreamStateTest'`
Expected: FAIL because `AacEndOfStreamState` does not exist.

- [ ] **Step 3: Write minimal implementation**

Keep state immutable. `complete` is exactly `inputEosQueued && outputEosSeen`.

- [ ] **Step 4: Run focused and full tests**

Run:
- `./gradlew testDebugUnitTest --tests 'com.patsy.app.studio.export.*'`
- `./gradlew testDebugUnitTest`

Expected: PASS.

- [ ] **Step 5: Build both variants**

Run:
- `./gradlew assembleDebug`
- `./gradlew assembleRelease`

Expected: both succeed.

- [ ] **Step 6: Commit**

Commit message: `feat: lock AAC EOS drain semantics`

---

## Self-review

This plan intentionally implements only export-core invariants. Android codec/muxer adapters, real PCM extraction, OES/GL/VFX and final master rendering are deferred to later slices so each boundary can be reviewed and verified independently.

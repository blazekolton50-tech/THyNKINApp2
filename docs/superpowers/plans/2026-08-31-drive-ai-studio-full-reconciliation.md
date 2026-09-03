# Drive + Google AI Studio Full Reconciliation Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Reconcile the real Google Drive / Google AI Studio Patsy source material with the secure native Android PR #38, restore truthful fallback movement, and converge THyNK Studio + THyNK Music on one native implementation without replacing auth/security/Owner/Rive boundaries.

**Architecture:** Keep PR #38 as the secure shell and navigation integration branch. Treat Google AI Studio, Drive Stage 1/2 exports, PR #23 and PR #24 as source inputs. Import only verified, non-duplicative native contracts into `com.patsy.app.studio`, then wire the user-facing THyNK surfaces in `com.patsy.app.thynk` to those contracts. Provider-backed AI/music/export capabilities remain `NOT_CONFIGURED` until real adapters are connected.

**Tech Stack:** Kotlin, Jetpack Compose, JUnit, Media3 where already present, Supabase through existing authenticated service boundaries, Rive through existing `PatsyRiveHost` / `PatsyRiveRuntimeAdapter`.

**Spec:** Drive source `Patsy Creation Studio Core Implementation`; Drive `PDF_Reader_SETUP_THYNK_STAGE2_REAL.py.txt`; GitHub PRs #23, #24, #35, #38; locked app navigation and visual contracts already in PR #38.

## Global Constraints

- Keep PR #38 Draft and unmerged until owner/device QA.
- Preserve HOME · THyNK · + CAMERA · PATSY DMS · PROFILE.
- Preserve FINAL Login / Set Password / Home and existing auth, age, Owner and Supabase security gates.
- Preserve exact approved Patsy logo assets and the existing Rive ABI/fallback boundary.
- Do not invent a production `.riv` asset.
- Do not simulate successful AI/music/mastering/export/backend work; use `NOT_CONFIGURED` when unavailable.
- Google AI Studio references to GitHub files that do not actually exist must remain untrusted until reconciled to real repository contracts.
- Do not restore historical Replit/web architecture over native Android.

---

### Task 1: Record Drive / AI Studio source-of-truth and conflicts

**Files:**
- Create: `docs/GOOGLE_DRIVE_AI_STUDIO_RECONCILIATION_2026-08-31.md`

**Interfaces:**
- Consumes: Drive AI Studio prompt, Drive Stage 2 setup, PR #23/#24 file manifests, PR #38 secure shell.
- Produces: audited import rules used by every later task.

- [ ] **Step 1: Write the reconciliation document** with exact source IDs/titles, confirmed native files, duplicate/superseded areas, and unverified references.
- [ ] **Step 2: Verify no secret values are copied** from Drive or provider configs.
- [ ] **Step 3: Commit** `docs: record Drive and AI Studio reconciliation`.

### Task 2: Restore truthful fallback Patsy motion

**Files:**
- Modify: `app/src/main/java/com/patsy/app/ui/finaldesign/FinalScreenChrome.kt`
- Test: `app/src/test/java/com/patsy/app/ui/finaldesign/FinalPatsyFallbackContractTest.kt`

**Interfaces:**
- Consumes: existing `breathe` and `look` animation values plus `PatsyRiveHost` fallback slot.
- Produces: visibly moving fallback image when no authored `.riv` is present, without claiming authored Rive motion.

- [ ] **Step 1: Write a failing source contract test** asserting fallback rendering applies `graphicsLayer` using the existing `breathe` value.
- [ ] **Step 2: Run `./gradlew testDebugUnitTest --tests '*FinalPatsyFallbackContractTest*'`** and confirm RED.
- [ ] **Step 3: Apply `graphicsLayer { scaleX = breathe; scaleY = breathe; rotationZ = look * 1.2f }` to the fallback image only.**
- [ ] **Step 4: Re-run the focused test** and confirm GREEN.
- [ ] **Step 5: Commit** `fix: animate Patsy fallback without faking Rive`.

### Task 3: Consolidate native Studio engine contracts from PR #23/#24

**Files:**
- Create/port only missing native files under: `app/src/main/java/com/patsy/app/studio/`
- Create/port matching tests under: `app/src/test/java/com/patsy/app/studio/`
- Do not duplicate types already present on PR #38.

**Interfaces:**
- Consumes: PR #23 editor/timeline/tool catalog and PR #24 catalogue/editor/camera/effects/export/media/project/sizing/timeline contracts.
- Produces: one native Studio engine package used by user-facing THyNK Compose screens.

- [ ] **Step 1: Add failing integration tests** proving editor history, layer transforms, canvas sizing and timeline constraints expected by the imported contracts.
- [ ] **Step 2: Run focused Studio tests** and confirm RED only for missing types/behaviour.
- [ ] **Step 3: Port the smallest non-duplicative set of production files** from PR #23/#24.
- [ ] **Step 4: Run all Studio tests** and confirm GREEN.
- [ ] **Step 5: Commit** `feat: consolidate native THyNK Studio engine contracts`.

### Task 4: Align THyNK Studio and THyNK Music with the actual Google Studio visual reference

**Files:**
- Modify: `app/src/main/java/com/patsy/app/thynk/ThynkStudioScreen.kt`
- Modify only if required: `app/src/main/java/com/patsy/app/thynk/ThynkStudioCatalog.kt`
- Test: `app/src/test/java/com/patsy/app/thynk/ThynkStudioContractTest.kt`

**Interfaces:**
- Consumes: verified 10-category catalog, native Studio engine, Drive/Library Google Studio screen references.
- Produces: THyNK hub and 10-screen THyNK Music flow matching the dark phone UI, gradient CTAs, waveform/timeline/mixer/EQ/effects/lyrics/mastering/export hierarchy.

- [ ] **Step 1: Extend contract tests** for page titles, tool routing, permanent Patsy homebar ownership and `NOT_CONFIGURED` provider states.
- [ ] **Step 2: Run focused tests** and confirm RED for newly required visual/route contracts.
- [ ] **Step 3: Refactor Compose surfaces** to match the Google Studio hierarchy without embedding fake data or replacing the native engine.
- [ ] **Step 4: Run focused tests** and confirm GREEN.
- [ ] **Step 5: Commit** `feat: align THyNK UI with Google Studio reference`.

### Task 5: Full Android verification

**Files:**
- No product changes unless verification exposes a concrete defect.

**Interfaces:**
- Consumes: Tasks 1-4.
- Produces: fresh CI evidence and artifacts.

- [ ] **Step 1: Run `./gradlew testDebugUnitTest --stacktrace`.**
- [ ] **Step 2: Run `./gradlew assembleDebug --stacktrace`.**
- [ ] **Step 3: Run `./gradlew assembleRelease --stacktrace`.**
- [ ] **Step 4: Inspect CI steps and artifact upload.**
- [ ] **Step 5: Keep PR #38 Draft.**

### Task 6: Drive archive handoff

**Files:**
- Generated: debug APK artifact and Android Studio source ZIP.

**Interfaces:**
- Consumes: successful Task 5 artifacts.
- Produces: durable Drive copies for Google Studio / Android Studio handoff.

- [ ] **Step 1: Download the verified GitHub Actions artifacts.**
- [ ] **Step 2: Upload the APK and Android Studio ZIP to the designated Patsy Drive folder using raw-file upload.**
- [ ] **Step 3: Record filenames, build commit and SHA-256 hashes in the reconciliation doc.**

# Realistic Patsy Rive and UI Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Preserve the 3.3.8 Android base, complete the realistic unboxed Patsy companion integration around the existing Rive runtime, and align the welcome/core shell with the locked Patsy visual system without redesigning approved screens.

**Architecture:** Keep the existing Rive ABI and fail-safe fallback path. Extract UI tokens and Patsy host behavior out of the monolithic `MainActivity.kt` incrementally, test the contract/coordinator first, then correct the welcome/header/companion presentation. The production `.riv` remains optional until authored and validated; missing external services/assets must remain honestly `NOT_CONFIGURED`/fallback rather than faked.

**Tech Stack:** Android, Kotlin 2.0.21, Jetpack Compose, Material 3, JVM 17, compileSdk 35, Rive Android 11.9.2.

**Spec:** `docs/PATSY_RIVE_RIG_CONTRACT_3.3.8.md`, `docs/RIVE_PROJECT_STATUS_3.3.8.md`, `CODEX_START_HERE.md`

## Global Constraints

- Main Patsy = realistic approved grey shaggy dog companion, never cartoon/chibi.
- Main Patsy is transparent/unboxed and free to move across UI; no permanent square/circle/halo.
- PawMojis remain cartoon/sticker/keyboard assets only.
- Principal pages use centred Patsy logo at top.
- Tagline is exactly `A LEGACY LED BY PAWS`, small/subtle only.
- Primary UI background is black/charcoal; primary text white/light grey.
- Primary buttons are white with dark text; rainbow/neon is restrained outline/glow/active treatment.
- Signup copy is exactly `I'm Patsy. Your personal AI PetPal. Log in and I'll show you what I can do!`
- Preserve the existing `PatsyAssistant` / `PatsyAssistantMachine` / `PatsyAssistantVM` / `Default` Rive ABI.
- Do not remove the generated transparent fallback until a production `.riv` passes the acceptance gate.
- Do not fake unavailable providers or delivery states.

---

### Task 1: Lock the Rive contract with unit tests

**Files:**
- Create: `app/src/test/java/com/patsy/app/patsy/rig/PatsyRigCoordinatorTest.kt`
- Modify: `app/build.gradle.kts`
- Verify: `app/src/main/java/com/patsy/app/patsy/rig/PatsyRigContractV1.kt`
- Verify: `app/src/main/java/com/patsy/app/patsy/rig/PatsyRigRuntimePort.kt`

**Interfaces:**
- Consumes: `PatsyRigCoordinator.render(PatsyRigPose)`, `retriggerAction(PatsyRigMotion)`, `blink()`.
- Produces: regression coverage for exact Rive property paths, clamping, reduced-motion behavior, and one-shot sequence retriggering.

- [ ] **Step 1: Add Kotlin/JUnit unit-test dependency**

Add to `dependencies` in `app/build.gradle.kts`:

```kotlin
testImplementation(kotlin("test"))
```

- [ ] **Step 2: Write a recording runtime and failing coordinator test**

Create `PatsyRigCoordinatorTest.kt` with a small `RecordingRuntime : PatsyRigRuntimePort` that stores applied mutations and tests that `render()` writes `motion/mode`, `stage/x`, `stage/y`, `stage/scale`, `face/expression`, and speech properties with normalized values.

- [ ] **Step 3: Add reduced-motion test**

Assert a `PatsyRigPose(motion = WALK, reducedMotion = true, tailEnergy = 1f, leftEarDrive = 1f, rightEarDrive = -1f)` is normalized to `IDLE`, zero motion speed, 20% ear/tail energy, and bounded scale.

- [ ] **Step 4: Add one-shot and blink sequence test**

Call `retriggerAction(PatsyRigMotion.POINT)` twice and assert `motion/action_sequence` increases; call `blink()` twice and assert `face/blink_sequence` increases.

- [ ] **Step 5: Run tests**

Run:

```bash
./gradlew testDebugUnitTest --tests com.patsy.app.patsy.rig.PatsyRigCoordinatorTest
```

Expected: PASS.

- [ ] **Step 6: Commit**

```bash
git add app/build.gradle.kts app/src/test/java/com/patsy/app/patsy/rig/PatsyRigCoordinatorTest.kt
git commit -m "test: lock Patsy Rive coordinator contract"
```

---

### Task 2: Add Rive runtime adapter tests and preserve fallback behavior

**Files:**
- Create: `app/src/test/java/com/patsy/app/patsy/rig/rive/PatsyRiveRuntimeAdapterTest.kt`
- Modify only if a test exposes a defect: `app/src/main/java/com/patsy/app/patsy/rig/rive/PatsyRiveRuntimeAdapter.kt`
- Verify: `app/src/main/java/com/patsy/app/patsy/rig/rive/PatsyRiveHost.kt`

**Interfaces:**
- Consumes: `PatsyRiveRuntimeAdapter.apply`, `markLoading`, `attach`, `markInvalid`, `markFailed`, `detach`.
- Produces: proof that latest-state buffering and failure states remain fail-safe while the `.riv` is missing or incompatible.

- [ ] **Step 1: Write buffering test**

Use an internal fake `PatsyRiveMutationWriter`. Apply two mutations to the same property before attach; attach the writer and assert only the latest value is flushed.

- [ ] **Step 2: Write ready/failure status tests**

Assert `markLoading()` -> `Loading`, valid `attach()` -> `Ready`, invalid -> `InvalidAsset`, write exception -> `Failed`, `detach()` -> `Detached`.

- [ ] **Step 3: Run tests**

```bash
./gradlew testDebugUnitTest --tests com.patsy.app.patsy.rig.rive.PatsyRiveRuntimeAdapterTest
```

Expected: PASS.

- [ ] **Step 4: Commit**

```bash
git add app/src/test/java/com/patsy/app/patsy/rig/rive/PatsyRiveRuntimeAdapterTest.kt app/src/main/java/com/patsy/app/patsy/rig/rive/PatsyRiveRuntimeAdapter.kt
git commit -m "test: protect Patsy Rive fallback runtime"
```

---

### Task 3: Extract locked Patsy visual tokens and header

**Files:**
- Create: `app/src/main/java/com/patsy/app/ui/PatsyVisualSystem.kt`
- Modify: `app/src/main/java/com/patsy/app/MainActivity.kt`

**Interfaces:**
- Produces: `PatsyColors`, `PatsyRainbowBrush`, `PatsyHeader()`, `PatsyPrimaryButton()`.
- Consumes: existing `patsy_logo_official_white` drawable.

- [ ] **Step 1: Create visual-system file**

Implement constants matching the current charcoal/white/rainbow system, but make the header copy locked:

```kotlin
@Composable
fun PatsyHeader(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(R.drawable.patsy_logo_official_white),
            contentDescription = "Patsy",
            modifier = Modifier.width(190.dp).height(82.dp),
            contentScale = ContentScale.Fit,
        )
        Text(
            text = "A LEGACY LED BY PAWS",
            color = PatsyColors.Muted,
            fontSize = 9.sp,
            letterSpacing = 1.sp,
        )
    }
}
```

- [ ] **Step 2: Move white primary button into the visual system**

Keep white container, black/dark label, rounded pill shape, and no dominant rainbow fill.

- [ ] **Step 3: Replace old header/tagline usage in `MainActivity.kt`**

Remove `YOUR AI. YOUR WORKSPACE. YOUR CONTROL.` from the principal header.

- [ ] **Step 4: Build**

```bash
./gradlew assembleDebug
```

Expected: BUILD SUCCESSFUL.

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/patsy/app/ui/PatsyVisualSystem.kt app/src/main/java/com/patsy/app/MainActivity.kt
git commit -m "ui: lock Patsy header and visual system"
```

---

### Task 4: Extract the realistic unboxed Patsy companion host

**Files:**
- Create: `app/src/main/java/com/patsy/app/patsy/ui/PatsyCompanion.kt`
- Modify: `app/src/main/java/com/patsy/app/MainActivity.kt`
- Verify: `app/src/main/java/com/patsy/app/patsy/rig/rive/PatsyRiveHost.kt`

**Interfaces:**
- Produces: `PatsyCompanion(action, message, target, modifier)` that owns the Rive adapter/coordinator and transparent fallback.
- Consumes: `PatsyRigCoordinator`, `PatsyRiveHost`, `patsy_generated_main`.

- [ ] **Step 1: Move `PatsyAction` and `PatsyMotion` behavior to `PatsyCompanion.kt`**

Keep the existing Rive behavior and fallback, but remove decorative fixed-frame semantics. The host may size a character drawing area for layout, but must not draw a visible square/circle/halo/background around Patsy.

- [ ] **Step 2: Remove emoji/arrow decorations that visually imply a cartoon mascot**

Do not render a floating `🐾` or `↗` beside the main companion as a substitute for real pointing. The real rig/fallback motion should carry the action.

- [ ] **Step 3: Preserve contextual message bubble as an independent overlay**

The speech bubble may remain, but it must not be the container around Patsy.

- [ ] **Step 4: Keep runtime fail-safe**

If `res/raw/patsy_assistant.riv` is absent/invalid, `patsy_generated_main` remains visible and animated by Compose host transforms. Do not claim production Rive is complete.

- [ ] **Step 5: Build and unit test**

```bash
./gradlew testDebugUnitTest assembleDebug
```

Expected: PASS / BUILD SUCCESSFUL.

- [ ] **Step 6: Commit**

```bash
git add app/src/main/java/com/patsy/app/patsy/ui/PatsyCompanion.kt app/src/main/java/com/patsy/app/MainActivity.kt
git commit -m "ui: extract realistic unboxed Patsy companion"
```

---

### Task 5: Correct the welcome/signup presentation without redesigning the flow

**Files:**
- Modify: `app/src/main/java/com/patsy/app/MainActivity.kt`
- Reuse: `app/src/main/java/com/patsy/app/ui/PatsyVisualSystem.kt`
- Reuse: `app/src/main/java/com/patsy/app/patsy/ui/PatsyCompanion.kt`

**Interfaces:**
- Consumes: current `WELCOME -> MODE -> PROFILE -> PASSWORD_SETUP -> EMAIL_LINKED -> LOGIN` navigation.
- Produces: approved signup visual/copy treatment while preserving auth boundaries.

- [ ] **Step 1: Replace welcome copy**

Use exactly:

```text
I'm Patsy. Your personal AI PetPal. Log in and I'll show you what I can do!
```

Do not add a larger alternate tagline.

- [ ] **Step 2: Use centred `PatsyHeader()` and realistic `PatsyCompanion()`**

No cartoon dog. No visible box/circle around Patsy.

- [ ] **Step 3: Keep lower account flow and white buttons**

Preserve the existing Get Started / existing-account pathways and age-aware flow. Primary button remains white; restrained rainbow/neon accents may be used on outline/focus/active states only.

- [ ] **Step 4: Add Compose UI smoke coverage if the repo supports instrumented tests; otherwise document manual acceptance**

Manual acceptance on emulator/device:
- logo centered at top,
- tagline small,
- Patsy realistic and unboxed,
- primary buttons white,
- no cartoon main Patsy,
- signup copy exact,
- Get Started and Login still route correctly.

- [ ] **Step 5: Build**

```bash
./gradlew assembleDebug
```

Expected: BUILD SUCCESSFUL.

- [ ] **Step 6: Commit**

```bash
git add app/src/main/java/com/patsy/app/MainActivity.kt
git commit -m "ui: align Patsy welcome screen with approved design"
```

---

### Task 6: Prepare the production `.riv` drop-in gate without faking the asset

**Files:**
- Verify: `docs/PATSY_RIVE_RIG_CONTRACT_3.3.8.md`
- Verify: `docs/RIVE_PROJECT_STATUS_3.3.8.md`
- Create: `docs/RIVE_ASSET_ACCEPTANCE_CHECKLIST.md`
- Asset when genuinely available: `app/src/main/res/raw/patsy_assistant.riv`

**Interfaces:**
- Consumes: exact Rive ABI already enforced by `PatsyRiveHost`.
- Produces: a deterministic release checklist for the final Rive export.

- [ ] **Step 1: Write the acceptance checklist**

Include exact names, every required View Model property, durable states (`idle`, `walk`, `sit`, `lie`), one-shots (`jump`, `wave`, `point`), independent ear drives, expressions, visemes, reduced motion, TalkBack, compact/large portrait/landscape travel, and three consecutive retriggers.

- [ ] **Step 2: Explicitly record current external-asset state**

If no validated `.riv` exists, keep status as `NOT_CONFIGURED`/fallback and do not add a fake or sprite-swap asset.

- [ ] **Step 3: When the real export is later supplied, place it at the exact raw-resource path and run**

```bash
./gradlew testDebugUnitTest assembleDebug
```

Then run physical-device smoke testing before changing fallback policy.

- [ ] **Step 4: Commit checklist only unless the real validated asset exists**

```bash
git add docs/RIVE_ASSET_ACCEPTANCE_CHECKLIST.md
git commit -m "docs: add Patsy Rive asset acceptance gate"
```

---

## Self-review

- Spec coverage: Rive ABI, fallback, unboxed realistic companion, centered logo, locked tagline, white buttons, signup copy, no cartoon substitution, reduced motion and honest external-asset state are all covered.
- Scope: this plan intentionally does **not** attempt to implement the full 90+ screen registry, backend providers, email delivery, DMs, scheduling, or AI media providers. Those are separate independently testable plans after this visual/Rive foundation is stable.
- Placeholder scan: no TODO/TBD implementation placeholders are used.
- Type consistency: all referenced Rive names and property paths come from `PatsyRigContractV1` / the 3.3.8 rig contract.

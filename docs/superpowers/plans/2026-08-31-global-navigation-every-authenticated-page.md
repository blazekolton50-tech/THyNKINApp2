# Global Navigation Every Authenticated Page Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Make the newly locked Patsy bottom navigation a single permanent authenticated-shell component on every authenticated/nested page, eliminating duplicate bars and the duplicate white THyNK subtitle.

**Architecture:** `FinalMainActivity` becomes the sole owner of the authenticated bottom navigation. Home, THyNK, Camera, PDMs, Profile, Owner Profile, Owner Tools and protected/denied authenticated states render their content inside one shared shell wrapper. `FinalHomeScreen` stops rendering its own private duplicate bar. Pre-auth Login and Set Password remain outside authenticated destination routing; they must not expose functional authenticated navigation before a session exists.

**Tech Stack:** Android/Kotlin, Jetpack Compose, Kotlin unit tests, GitHub Actions.

**Spec:** `docs/PATSY_DESIGN_PRESERVATION_MASTER_2026-08-31.md`

## Global Constraints

- Work only on Draft PR #38 branch `chatgpt/thynk-music-apk-2026-08-31`.
- Do not merge to main.
- Canonical visual order: **Home · coloured THyNK wordmark · large centre + · PDMs · Profile**.
- Do not render a second small/white `THyNK` caption below the coloured THyNK wordmark.
- Do not render `CAMERA` as visible text under the centre +; Camera remains the semantic route.
- Visible DM label is `PDMs`; semantic destination remains `PATSY DMS` / `FinalHomeDestination.PATSY_DMS`.
- Preserve black/charcoal base, thin rainbow wave/glow, rainbow Home/THyNK accents, white centre button with rainbow ring, PDM notification dot and Profile icon treatment.
- Do not redesign Login, Set Password or Home content.
- Do not weaken route/security/Owner authorization gates.
- Nested THyNK editors must never replace or hide this bar.

---

### Task 1: Lock visible versus semantic navigation labels

**Files:**
- Modify: `app/src/main/java/com/patsy/app/ui/finaldesign/FinalVisualContract.kt`
- Modify: `app/src/test/java/com/patsy/app/ui/finaldesign/FinalVisualContractTest.kt`

**Interfaces:**
- Consumes: existing semantic `primaryNavigation` list.
- Produces: `primaryNavigationVisibleLabels` for rendering only.

- [ ] **Step 1: Write the failing test**

```kotlin
@Test
fun `primary navigation visible labels match the locked image`() {
    assertEquals(
        listOf("Home", "", "", "PDMs", "Profile"),
        FinalVisualContract.primaryNavigationVisibleLabels,
    )
}
```

The empty strings are intentional: THyNK is its own coloured wordmark and the centre + has no caption.

- [ ] **Step 2: Run test and verify RED**

```bash
./gradlew testDebugUnitTest --tests com.patsy.app.ui.finaldesign.FinalVisualContractTest --stacktrace
```
Expected: FAIL because `primaryNavigationVisibleLabels` does not exist.

- [ ] **Step 3: Add minimal production contract**

```kotlin
val primaryNavigationVisibleLabels = listOf("Home", "", "", "PDMs", "Profile")
```

Do not alter:
```kotlin
val primaryNavigation = listOf("HOME", "THyNK", "CAMERA", "PATSY DMS", "PROFILE")
```

- [ ] **Step 4: Re-run targeted test**
Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/patsy/app/ui/finaldesign/FinalVisualContract.kt app/src/test/java/com/patsy/app/ui/finaldesign/FinalVisualContractTest.kt
git commit -m "test: lock visible Patsy navigation labels"
```

---

### Task 2: Make `FinalPrimaryNavigationBar` the one canonical bar

**Files:**
- Modify: `app/src/main/java/com/patsy/app/ui/finaldesign/FinalPrimaryNavigationBar.kt`

**Interfaces:**
- Consumes: `FinalHomeDestination`, `FinalVisualContract.primaryNavigationVisibleLabels`.
- Produces: one visual bar used everywhere in the authenticated shell.

- [ ] **Step 1: Verify current visual requirements in code**

Required rendering:
- Home: rainbow-outlined Home icon + visible `Home` label.
- THyNK: one coloured/rainbow `THyNK` wordmark only; no second subtitle.
- Centre: large white circular `+` with rainbow ring; no `CAMERA` caption.
- DMs: speech-bubble icon + visible `PDMs`; preserve pink notification dot when the component has one.
- Profile: profile outline + `Profile`.
- Top border/wave: current `FinalBottomWave(showFooterCopy = false)`.

- [ ] **Step 2: Remove any duplicate subtitle/caption rendering**

Do not call a generic item renderer with label `THyNK`. Render THyNK as a dedicated wordmark-only item or allow its visible label to be blank.

Do not render a centre caption.

- [ ] **Step 3: Keep semantic callbacks unchanged**

```kotlin
onNavigate(FinalHomeDestination.HOME)
onNavigate(FinalHomeDestination.THYNK)
onNavigate(FinalHomeDestination.CREATE)
onNavigate(FinalHomeDestination.PATSY_DMS)
onNavigate(FinalHomeDestination.PROFILE)
```

`CREATE` remains the internal enum value for the locked Camera route until the enum is deliberately renamed in a separate migration.

- [ ] **Step 4: Compile**

```bash
./gradlew compileDebugKotlin --stacktrace
```
Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/patsy/app/ui/finaldesign/FinalPrimaryNavigationBar.kt
git commit -m "fix: match locked Patsy navigation artwork"
```

---

### Task 3: Remove Home's duplicate private navigation implementation

**Files:**
- Modify: `app/src/main/java/com/patsy/app/ui/finaldesign/FinalHomeScreen.kt`

**Interfaces:**
- Consumes: Home content callbacks only.
- Produces: Home content with bottom padding suitable for the outer global nav; no private nav component.

- [ ] **Step 1: Remove the call to `FinalHomeBottomNavigation` from `FinalHomeScreen`**

Home must not own a second navigation bar.

- [ ] **Step 2: Delete private duplicate helpers only after they are unused**

Remove:
- `FinalHomeBottomNavigation`
- its private duplicate `NavItem` helper if no longer referenced.

Do not alter Home header, Ask Patsy, Continue Designs, Today, composer or feed layout as part of this task.

- [ ] **Step 3: Preserve safe bottom content padding**

Keep enough bottom padding (currently around 90dp) so Home content does not sit underneath the shared bar.

- [ ] **Step 4: Compile**

```bash
./gradlew compileDebugKotlin --stacktrace
```
Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/patsy/app/ui/finaldesign/FinalHomeScreen.kt
git commit -m "refactor: remove duplicate Home navigation bar"
```

---

### Task 4: Add one authenticated-shell wrapper in `FinalMainActivity`

**Files:**
- Modify: `app/src/main/java/com/patsy/app/FinalMainActivity.kt`

**Interfaces:**
- Consumes: `FinalHomeDestination`, current `navigate` function.
- Produces: `AuthenticatedPageWithNavigation(selected, onNavigate, content)`.

- [ ] **Step 1: Add the shared composable wrapper**

```kotlin
@Composable
private fun AuthenticatedPageWithNavigation(
    selected: FinalHomeDestination,
    onNavigate: (FinalHomeDestination) -> Unit,
    content: @Composable () -> Unit,
) {
    Column(Modifier.fillMaxSize().background(FinalCharcoal)) {
        Box(Modifier.weight(1f).fillMaxWidth()) {
            content()
        }
        FinalPrimaryNavigationBar(
            selected = selected,
            onNavigate = onNavigate,
        )
    }
}
```

- [ ] **Step 2: Use it for HOME**

When Home access is authorized, render:

```kotlin
AuthenticatedPageWithNavigation(
    selected = FinalHomeDestination.HOME,
    onNavigate = ::navigate,
) {
    FinalHomeScreen(onNavigate = ::navigate)
}
```

Debug preview Home must use the same wrapper.

- [ ] **Step 3: Use it for THyNK / Camera / DMs / Profile**

Replace the current repeated `Column + Box + FinalPrimaryNavigationBar` block with the shared wrapper. Keep existing authorization logic unchanged.

- [ ] **Step 4: Use it for OWNER_PROFILE and OWNER_TOOLS**

If authorized, wrap the existing Owner content with:

```kotlin
AuthenticatedPageWithNavigation(
    selected = FinalHomeDestination.PROFILE,
    onNavigate = ::navigate,
) { ... }
```

Do not change Owner authorization checks or grant lifetime logic.

- [ ] **Step 5: Use it for authenticated protected/denied states**

Protected route pages reached from an authenticated session must retain the same bar. Selecting another destination still passes through `navigate` and the existing `FinalShellNavigation.authorize` gate.

If `session == null`, do not expose functional authenticated routes on Login/Set Password.

- [ ] **Step 6: Compile and test**

```bash
./gradlew testDebugUnitTest --stacktrace
./gradlew compileDebugKotlin --stacktrace
```
Expected: PASS.

- [ ] **Step 7: Commit**

```bash
git add app/src/main/java/com/patsy/app/FinalMainActivity.kt
git commit -m "refactor: keep Patsy navigation on every authenticated page"
```

---

### Task 5: Verify no alternate navigation remains

**Files:**
- Search/audit only unless a duplicate is found.

- [ ] **Step 1: Search for bottom-nav implementations and labels**

```bash
grep -R "FinalHomeBottomNavigation\|FinalPrimaryNavigationBar\|CAMERA\|PATSY DMS\|PDMs\|THyNK" -n app/src/main/java/com/patsy/app
```

Expected:
- only `FinalPrimaryNavigationBar` renders the primary bottom bar;
- no small white THyNK subtitle under the coloured THyNK wordmark;
- no visible CAMERA label beneath the centre +;
- no duplicate Home-only bar.

- [ ] **Step 2: Verify nested THyNK editor behavior**

Open THyNK, enter category, enter Music/editor/video nested screens. The outer bar must remain visible because `ThynkStudioScreen` only controls content inside the shell.

- [ ] **Step 3: Full CI-equivalent verification**

```bash
./gradlew testDebugUnitTest --stacktrace
./gradlew assembleDebug --stacktrace
./gradlew assembleRelease --stacktrace
```
Expected: all PASS.

- [ ] **Step 4: Record exact final head and CI**

Report exact SHA, GitHub Actions run, APK artifact and Android Studio ZIP artifact. Keep PR #38 Draft.

---

## Self-review

- Spec coverage: canonical bar, no duplicate THyNK subtitle, no Camera caption, visible PDMs label, all authenticated/nested/owner/protected pages, one implementation, security gate preserved.
- No placeholder implementation steps.
- Semantic destination names remain stable while visible labels match the locked image.
- Pre-auth functional navigation is not exposed before authentication.

## Completion report required

Report exact changed files, RED/GREEN test evidence, debug/release results, CI result, and screenshots from Home + THyNK editor + Owner/Profile showing the same bar before requesting merge approval.

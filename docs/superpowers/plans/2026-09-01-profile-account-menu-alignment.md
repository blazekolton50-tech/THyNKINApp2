# Profile & Account Menu Alignment Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Replace the legacy `More`-style final-profile surface with the latest locked Profile layout and make the exact five-item top-right account menu consistently route to secondary account pages without changing the five primary destinations or weakening authorization.

**Architecture:** Add pure contracts for account-menu order and profile sections, create focused Compose files under `ui/finaldesign`, and hoist secondary page/menu routing into `FinalMainActivity`. Preserve current Owner authorization checks exactly. Account/About/Settings/Remember Me are secondary pages and never become bottom-navigation destinations.

**Tech Stack:** Kotlin/JVM unit tests, Jetpack Compose/Material3, current auth/bootstrap/Owner contracts, existing final design tokens.

**Spec:** `docs/PATSY_DESIGN_PRESERVATION_MASTER_2026-08-31.md`

## Global Constraints

- Work on `chatgpt/codex-ready-2026-09-01`; keep Draft and unmerged.
- Execute only after the main THyNK donor/canvas/design/music jobs remain GREEN.
- Primary semantic destinations remain exactly `HOME · THyNK · CAMERA · PATSY DMS · PROFILE`.
- Current display-label contract remains exactly `Home · [no text under THyNK] · [no text under +] · PDMs · Profile`.
- Schedule and Calendar remain secondary tools, never primary bottom tabs.
- Top-right account menu order is exactly `Account · About · Profile · Settings · Remember Me`.
- Owner Profile/Owner Tools stay server-authorized and fail closed.
- Do not invent bio/social/gallery/project data when it is absent; render truthful empty states.
- Do not turn Remember Me into plaintext password storage.
- Preserve the approved centered Patsy logo/chrome and realistic-main-Patsy boundary.

---

### Task 1: Lock account-menu and profile-section contracts under unit tests

**Files:**
- Create: `app/src/main/java/com/patsy/app/ui/finaldesign/FinalAccountMenuContract.kt`
- Create: `app/src/main/java/com/patsy/app/ui/finaldesign/FinalProfileContract.kt`
- Create: `app/src/test/java/com/patsy/app/ui/finaldesign/FinalAccountMenuContractTest.kt`
- Create: `app/src/test/java/com/patsy/app/ui/finaldesign/FinalProfileContractTest.kt`

**Interfaces:**
- Produces: `FinalAccountMenuDestination`, `FinalAccountMenuContract.items`, `FinalProfileSection`, `FinalProfileContract.sections`.

- [ ] **Step 1: Write failing contract tests**

```kotlin
package com.patsy.app.ui.finaldesign

import kotlin.test.Test
import kotlin.test.assertEquals

class FinalAccountMenuContractTest {
    @Test fun accountMenuOrderIsLocked() {
        assertEquals(
            listOf("Account", "About", "Profile", "Settings", "Remember Me"),
            FinalAccountMenuContract.items.map { it.label },
        )
    }
}
```

```kotlin
package com.patsy.app.ui.finaldesign

import kotlin.test.Test
import kotlin.test.assertEquals

class FinalProfileContractTest {
    @Test fun profileSectionsKeepSecondaryToolsOutOfPrimaryNavigation() {
        assertEquals(
            listOf("Identity", "Bio & Social", "Gallery", "Recent Projects", "Saved Projects", "Schedule & Calendar", "Remember Me"),
            FinalProfileContract.sections.map { it.label },
        )
    }
}
```

- [ ] **Step 2: Run tests and verify RED**

```bash
./gradlew testDebugUnitTest --tests 'com.patsy.app.ui.finaldesign.FinalAccountMenuContractTest'
./gradlew testDebugUnitTest --tests 'com.patsy.app.ui.finaldesign.FinalProfileContractTest'
```
Expected: FAIL because the new contract types do not exist.

- [ ] **Step 3: Implement the exact contracts**

```kotlin
package com.patsy.app.ui.finaldesign

enum class FinalAccountMenuDestination { ACCOUNT, ABOUT, PROFILE, SETTINGS, REMEMBER_ME }

data class FinalAccountMenuItem(
    val destination: FinalAccountMenuDestination,
    val label: String,
)

object FinalAccountMenuContract {
    val items = listOf(
        FinalAccountMenuItem(FinalAccountMenuDestination.ACCOUNT, "Account"),
        FinalAccountMenuItem(FinalAccountMenuDestination.ABOUT, "About"),
        FinalAccountMenuItem(FinalAccountMenuDestination.PROFILE, "Profile"),
        FinalAccountMenuItem(FinalAccountMenuDestination.SETTINGS, "Settings"),
        FinalAccountMenuItem(FinalAccountMenuDestination.REMEMBER_ME, "Remember Me"),
    )
}
```

```kotlin
package com.patsy.app.ui.finaldesign

enum class FinalProfileSection(val label: String) {
    IDENTITY("Identity"),
    BIO_SOCIAL("Bio & Social"),
    GALLERY("Gallery"),
    RECENT_PROJECTS("Recent Projects"),
    SAVED_PROJECTS("Saved Projects"),
    SCHEDULE_CALENDAR("Schedule & Calendar"),
    REMEMBER_ME("Remember Me"),
}

object FinalProfileContract {
    val sections = FinalProfileSection.entries.toList()
}
```

- [ ] **Step 4: Run tests and commit**

```bash
./gradlew testDebugUnitTest --tests 'com.patsy.app.ui.finaldesign.FinalAccountMenuContractTest'
./gradlew testDebugUnitTest --tests 'com.patsy.app.ui.finaldesign.FinalProfileContractTest'
git add app/src/main/java/com/patsy/app/ui/finaldesign/FinalAccountMenuContract.kt app/src/main/java/com/patsy/app/ui/finaldesign/FinalProfileContract.kt app/src/test/java/com/patsy/app/ui/finaldesign
git commit -m "test: lock profile and account menu contracts"
```

---

### Task 2: Build reusable top-right account menu UI and wire current top bars

**Files:**
- Create: `app/src/main/java/com/patsy/app/ui/finaldesign/FinalAccountMenu.kt`
- Modify: `app/src/main/java/com/patsy/app/ui/finaldesign/FinalHomeScreen.kt`
- Modify: `app/src/main/java/com/patsy/app/thynk/ThynkStudioScreen.kt`
- Modify: `app/src/main/java/com/patsy/app/thynk/NativeCameraHub.kt`
- Modify: `app/src/test/java/com/patsy/app/ui/finaldesign/FinalAccountMenuContractTest.kt`

**Interfaces:**
- Consumes: `FinalAccountMenuContract.items`.
- Produces: `accountMenuDestinationForLabel`, `FinalAccountMenuButton`, `FinalAccountMenuPopup`.

- [ ] **Step 1: Add failing selection-helper test**

```kotlin
@Test fun onlyLockedMenuLabelsResolve() {
    assertEquals(FinalAccountMenuDestination.PROFILE, accountMenuDestinationForLabel("Profile"))
    assertEquals(null, accountMenuDestinationForLabel("Schedule"))
}
```

- [ ] **Step 2: Run and verify RED**

```bash
./gradlew testDebugUnitTest --tests 'com.patsy.app.ui.finaldesign.FinalAccountMenuContractTest.onlyLockedMenuLabelsResolve'
```
Expected: FAIL because `accountMenuDestinationForLabel` does not exist.

- [ ] **Step 3: Implement helper and menu composables**

```kotlin
fun accountMenuDestinationForLabel(label: String): FinalAccountMenuDestination? =
    FinalAccountMenuContract.items.firstOrNull { it.label == label }?.destination
```

`FinalAccountMenuButton(onClick)` uses the existing top-right circular charcoal/rainbow treatment and `•••` semantics.

`FinalAccountMenuPopup(expanded, onDismiss, onSelect)` renders exactly `FinalAccountMenuContract.items` in their stored order. It must not insert Schedule, Calendar, Owner Tools, Sign out or another primary destination.

- [ ] **Step 4: Replace decorative ellipses with the reusable button**

Use these signatures when the Design job has already added `accountScopeKey` to THyNK:

```kotlin
fun FinalHomeScreen(
    onNavigate: (FinalHomeDestination) -> Unit,
    onOpenAccountMenu: () -> Unit,
    onAskPatsy: () -> Unit = {},
    onCreatePost: () -> Unit = {},
)
```

```kotlin
fun ThynkStudioScreen(
    accountScopeKey: String,
    onOpenAccountMenu: () -> Unit,
)
```

```kotlin
fun NativeCameraHub(
    onOpenThynk: () -> Unit = {},
    onOpenAccountMenu: () -> Unit = {},
)
```

Keep the centered logo and shared bottom navigation unchanged.

- [ ] **Step 5: Run tests/build and commit**

```bash
./gradlew testDebugUnitTest --tests 'com.patsy.app.ui.finaldesign.*'
./gradlew assembleDebug --stacktrace
git add app/src/main/java/com/patsy/app/ui/finaldesign app/src/main/java/com/patsy/app/thynk app/src/test/java/com/patsy/app/ui/finaldesign
git commit -m "feat: wire locked top-right account menu"
```

---

### Task 3: Create truthful final Profile screen and stop using legacy `More` as final Profile UI

**Files:**
- Create: `app/src/main/java/com/patsy/app/ui/finaldesign/FinalProfileScreen.kt`
- Modify: `app/src/main/java/com/patsy/app/FinalMainActivity.kt`
- Modify: `app/src/test/java/com/patsy/app/ui/finaldesign/FinalProfileContractTest.kt`

**Interfaces:**
- Consumes: current `Profile` session model, email verification flag, current Owner authorization booleans/callbacks.
- Produces: `FinalProfileViewState`, `FinalProfileScreen`.

- [ ] **Step 1: Add failing truthful-empty-state test**

```kotlin
@Test fun absentOptionalProfileContentStaysAbsent() {
    val state = FinalProfileViewState(
        displayName = "Blaze",
        username = "blaze",
        maskedEmail = "b***@example.com",
        emailVerified = true,
        bio = null,
        socialLinks = emptyList(),
        galleryCount = 0,
        recentProjectCount = 0,
        savedProjectCount = 0,
    )
    assertEquals(null, state.bio)
    assertEquals(emptyList(), state.socialLinks)
    assertEquals(0, state.galleryCount)
}
```

- [ ] **Step 2: Run and verify RED**

```bash
./gradlew testDebugUnitTest --tests 'com.patsy.app.ui.finaldesign.FinalProfileContractTest.absentOptionalProfileContentStaysAbsent'
```
Expected: FAIL because `FinalProfileViewState` does not exist.

- [ ] **Step 3: Implement truthful view state**

```kotlin
data class FinalProfileViewState(
    val displayName: String,
    val username: String,
    val maskedEmail: String,
    val emailVerified: Boolean,
    val bio: String?,
    val socialLinks: List<String>,
    val galleryCount: Int,
    val recentProjectCount: Int,
    val savedProjectCount: Int,
)
```

Do not seed fake bio, links, media or projects. Render exact empty-state copy:
- bio: `Not set`
- gallery: `No gallery items yet`
- recent projects: `No recent projects yet`
- saved projects: `No saved projects yet`

- [ ] **Step 4: Implement `FinalProfileScreen` with current final tokens**

Render:
- identity/avatar area from available profile/session data
- Bio & Social
- Gallery
- Recent Projects
- Saved Projects
- Schedule & Calendar secondary actions
- Remember Me secondary action
- Owner Profile and Owner Tools buttons only when current server-authorized booleans are true
- Sign out as an account action, never a primary destination

Use existing `FinalCharcoal`, `FinalCard`, `FinalRainbow`, `FinalWhite`, `FinalMuted` and the approved logo/chrome.

- [ ] **Step 5: Replace only the Final shell `More(...)` call**

In `FinalMainActivity`, replace `FinalAppPage.PROFILE -> More(...)` with `FinalProfileScreen(...)`. Preserve current Owner refresh/expiry checks and callbacks exactly. Do not delete legacy `MainActivity.kt` in this task; it remains non-launcher recovery code until a separate cleanup is reviewed.

- [ ] **Step 6: Run tests/build and commit**

```bash
./gradlew testDebugUnitTest --tests 'com.patsy.app.ui.finaldesign.*'
./gradlew testDebugUnitTest --stacktrace
./gradlew assembleDebug --stacktrace
git add app/src/main/java/com/patsy/app/ui/finaldesign/FinalProfileScreen.kt app/src/main/java/com/patsy/app/FinalMainActivity.kt app/src/test/java/com/patsy/app/ui/finaldesign
git commit -m "feat: align final Profile with locked layout"
```

---

### Task 4: Add secondary Account/About/Settings/Remember Me routes without altering the five primary destinations

**Files:**
- Modify: `app/src/main/java/com/patsy/app/FinalMainActivity.kt`
- Create: `app/src/main/java/com/patsy/app/ui/finaldesign/FinalSecondaryAccountScreens.kt`
- Modify: `app/src/test/java/com/patsy/app/ui/finaldesign/FinalVisualContractTest.kt`

**Interfaces:**
- Consumes: `FinalAccountMenuDestination`, existing `FinalVisualContract.primaryNavigation` and `FinalVisualContract.primaryNavigationDisplayLabels`.
- Produces: secondary `FinalAppPage` values `ACCOUNT`, `ABOUT`, `SETTINGS`, `REMEMBER_ME`.

- [ ] **Step 1: Add exact regression test for the existing primary contract**

```kotlin
@Test fun secondaryAccountPagesCannotExpandPrimaryNavigation() {
    assertEquals(
        listOf("HOME", "THyNK", "CAMERA", "PATSY DMS", "PROFILE"),
        FinalVisualContract.primaryNavigation,
    )
    assertEquals(
        listOf("Home", "", "", "PDMs", "Profile"),
        FinalVisualContract.primaryNavigationDisplayLabels,
    )
}
```

This test uses the exact constants that already exist in `FinalVisualContract.kt`; do not add duplicate navigation truth.

- [ ] **Step 2: Add secondary page enum values and menu mapping**

Map:
- Account -> `FinalAppPage.ACCOUNT`
- About -> `FinalAppPage.ABOUT`
- Profile -> `FinalAppPage.PROFILE`
- Settings -> `FinalAppPage.SETTINGS`
- Remember Me -> `FinalAppPage.REMEMBER_ME`

None of these additions changes `FinalVisualContract.primaryNavigation` or `primaryNavigationDisplayLabels`.

- [ ] **Step 3: Implement truthful secondary screens**

`Account`: show only current session/account facts already available to the client, such as display name/username, masked email and email verification status, plus existing account actions.

`About`: static Patsy app identity/version/about copy only; no fabricated provider/network state.

`Settings`: show only settings that are actually wired at implementation time. Unwired controls render disabled/unavailable and do not pretend to persist.

`Remember Me`: until the secure user-memory repository is genuinely wired, render exactly `Remember Me storage is not configured yet.` and do not expose fake saved memories. This page is separate from login keep-signed-in/session restoration.

- [ ] **Step 4: Hoist one menu state in `FinalMainActivity`**

Maintain one `accountMenuExpanded` state. Pass `onOpenAccountMenu` to Home, THyNK and Camera, and use the same reusable button on Profile/DMs/secondary screens. Selecting an item closes the popup and changes the secondary page route.

For the shared bottom bar:
- `PROFILE`, `ACCOUNT`, `ABOUT`, `SETTINGS`, `REMEMBER_ME`, `OWNER_PROFILE`, `OWNER_TOOLS` select `FinalHomeDestination.PROFILE`.
- no new bottom button is added.
- `PROTECTED` keeps the current nullable selection behavior.

- [ ] **Step 5: Run final verification**

```bash
./gradlew testDebugUnitTest --stacktrace
./gradlew assembleDebug --stacktrace
./gradlew assembleRelease --stacktrace
```
Expected: all exit 0.

- [ ] **Step 6: Push and require fresh Patsy Consolidation CI**

Report exact starting SHA, ending SHA, changed files, RED/GREEN evidence, CI run, artifact IDs/digests and remaining device/backend limitations. Keep Draft and unmerged.

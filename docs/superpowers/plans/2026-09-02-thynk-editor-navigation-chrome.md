# THyNK Editor Navigation Chrome Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Make THyNK editing boards full-screen with only a Back control on the left and a rainbow-circle overflow menu on the right containing Home, while restoring the normal five-logo homebar immediately after leaving an editing board.

**Architecture:** Move THyNK internal route semantics into a dependency-light navigation model that can be unit tested. `ThynkStudioScreen` owns category/editor history and tells the authenticated shell when an editing board is active; `FinalMainActivity` remains the authority for the global homebar and HOME route. Editor Home exits through the shell, while Back remains inside THyNK and returns to the originating category/tool hub.

**Tech Stack:** Kotlin, Jetpack Compose, Material 3, existing THyNK routing/catalog, existing authenticated shell, existing Studio persistence boundaries.

**Spec:** Current owner lock in conversation, 2026-09-02: editing board hides homebar; Back returns to the category/tool screen; top-right rainbow circle opens dropdown; Home inside dropdown exits to THyNK-IN! home/feed.

## Global Constraints

- Exact main panel order remains `THyNK-ME | THyNK Chats | THyNK-IN! | THyNK Music | THyNK-IT`.
- Do not bypass auth, Owner authorization, age/security gates, native Camera, Media3, or Supabase boundaries.
- Do not fake a successful autosave; only report persistence success when the existing persistence service returns success.
- Do not introduce React/WebView production UI.
- Do not fake a Patsy `.riv` asset.

---

### Task 1: Testable workspace-route contract

**Files:**
- Create: `app/src/test/java/com/patsy/app/thynk/ThynkWorkspaceNavigationTest.kt`
- Create: `app/src/main/java/com/patsy/app/thynk/ThynkWorkspaceNavigation.kt`

**Interfaces:**
- Produces: `ThynkWorkspaceRoute`, `ThynkWorkspaceNavigation.isEditingBoard(route)`, `ThynkWorkspaceNavigation.back(route)`.

- [ ] **Step 1:** Add failing tests proving design/video editor routes and specialist Music pages are editing boards, while hubs/category pages are not.
- [ ] **Step 2:** Run `./gradlew testDebugUnitTest --tests com.patsy.app.thynk.ThynkWorkspaceNavigationTest` and observe RED.
- [ ] **Step 3:** Add the minimum route model/navigation logic.
- [ ] **Step 4:** Run the focused test and observe GREEN.

### Task 2: Editor chrome + shell homebar visibility

**Files:**
- Modify: `app/src/main/java/com/patsy/app/thynk/ThynkStudioScreen.kt`
- Modify: `app/src/main/java/com/patsy/app/FinalMainActivity.kt`
- Test: `app/src/test/java/com/patsy/app/thynk/ThynkWorkspaceNavigationTest.kt`

**Interfaces:**
- `ThynkStudioScreen(onEditingBoardChanged: (Boolean) -> Unit, onHome: () -> Unit)`.

- [ ] **Step 1:** Add failing source/contract assertions for editing-board callback wiring and Home callback ownership if needed after route tests.
- [ ] **Step 2:** Make `ThynkStudioScreen` use `ThynkWorkspaceRoute` and emit editing-board state whenever route changes.
- [ ] **Step 3:** Render Back at top-left for non-hub routes. On editing boards, render a circular rainbow overflow control at top-right; its dropdown contains `Home` and calls `onHome`.
- [ ] **Step 4:** Track `editingBoardActive` in `FinalMainActivity`, hide `FinalPrimaryNavigationBar` only when true, and reset it when leaving THyNK.
- [ ] **Step 5:** Run focused + full unit tests.

### Task 3: Exit/persistence truth boundary

**Files:**
- Inspect/modify only where a real project/session is available: `ThynkDesignEditorScreen.kt`, Studio project/persistence bindings.

- [ ] **Step 1:** Keep Back/Home functional even if persistence is unavailable.
- [ ] **Step 2:** If a real project id/session is present, request autosave before exit and surface truthful save status; otherwise preserve the existing `Local draft · not synced` wording and do not claim a server save.
- [ ] **Step 3:** Add focused tests for any new persistence adapter logic.

### Task 4: Verification

- [ ] **Step 1:** Run `./gradlew testDebugUnitTest`.
- [ ] **Step 2:** Run `./gradlew assembleDebug`.
- [ ] **Step 3:** Run `./gradlew assembleRelease`.
- [ ] **Step 4:** Confirm PR head workflow status and keep the PR draft until physical-device QA verifies Back, overflow Home, and homebar hiding/restoration.

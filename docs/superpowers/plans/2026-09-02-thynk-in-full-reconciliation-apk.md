# THyNK-IN! Full Reconciliation and APK Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Reconcile the 2 September THyNK-IN! product rules, live Supabase deployment, native Android routes and approved assets into one reproducible branch and produce a verified device APK.

**Architecture:** Keep the existing Kotlin/Jetpack Compose secure shell, Supabase backend, Camera/FileProvider, Media3 and Patsy Rive fallback architecture. Normalize top-level product ownership so THyNK Music owns music+video while THyNK-IT owns non-media creator work, source-control all live backend functions, and make navigation state explicit rather than inheriting stale THyNK entry state.

**Tech Stack:** Kotlin, Jetpack Compose, Android, Supabase Edge Functions/Postgres/RLS, Media3, Rive Android runtime, GitHub Actions.

**Spec:** `docs/codex/THYNK_IN_AUTHORITATIVE_SOURCE_2026-09-02.md`

## Global Constraints

- Global product name is `THyNK-IN!`; Patsy remains the assistant/character.
- Exact homebar order is `THyNK-ME | THyNK Chats | THyNK-IN! | THyNK Music | THyNK-IT`.
- Editing boards hide the homebar; Back returns to the source category; top-right rainbow overflow contains Home.
- THyNK Music owns music and video/media; THyNK-IT owns non-music/non-video creator work.
- Preserve auth, Owner, age/security gates, RLS, native Camera/FileProvider, Media3 and tested editor state/persistence.
- React/Tailwind/HTML donor files are reference-only, never production WebView/runtime.
- No fake provider success, save/export, DMs, Owner authority, waveform data or production Rive.

---

### Task 1: Source-control live Supabase auth functions

**Files:**
- Create: `supabase/functions/auth-login/index.ts`
- Create: `supabase/functions/auth-register-start/index.ts`
- Create: `supabase/functions/auth-register-complete/index.ts`
- Create: `supabase/functions/auth-reset-request/index.ts`

**Interfaces:**
- Consumes: the currently deployed function source from Supabase project `tvtknwqcqbkecszvppub`.
- Produces: reproducible source matching Android callers in `SupabaseHttpAuthTransport`, `SupabaseHttpRegistrationTransport`, and `SupabaseHttpRecoveryTransport`.

- [ ] Add repository copies byte-for-byte in behavior from the current live functions.
- [ ] Verify Android endpoint names match the four function slugs.
- [ ] Do not redeploy or change authentication semantics in this task.
- [ ] Commit the source-control sync.

### Task 2: Lock Music vs THyNK-IT route ownership

**Files:**
- Modify: `app/src/main/java/com/patsy/app/thynk/ThynkStudioCatalog.kt`
- Modify: `app/src/main/java/com/patsy/app/thynk/ThynkEditorRouting.kt`
- Modify: `app/src/main/java/com/patsy/app/thynk/ThynkStudioScreen.kt`
- Test: `app/src/test/java/com/patsy/app/thynk/ThynkProductRouteOwnershipTest.kt`

**Interfaces:**
- Consumes: `ThynkStudioEntry`, `ThynkWorkspaceRoute`, existing Camera/Media3 video editor.
- Produces: IT catalog with no `music`/`video` root categories and Music catalog containing video/media destinations.

- [ ] Write a failing test asserting IT has no `music` or `video` category and Music exposes `video-home`, `video-editor`, `dj-studio`, `auto-tuner`, and `recording`.
- [ ] Run unit tests and confirm the new contract fails against the old catalog.
- [ ] Remove Music/Video from the IT root catalog and add truthful media pages to `ThynkMusicCatalog`.
- [ ] Route `video-editor` from the Music workspace while preserving the real Media3 editor implementation.
- [ ] Run the focused test and then the full debug unit suite.
- [ ] Commit.

### Task 3: Eliminate THyNK entry-state bleed

**Files:**
- Modify: `app/src/main/java/com/patsy/app/FinalMainActivity.kt`
- Test: `app/src/test/java/com/patsy/app/ThynkEntryNavigationContractTest.kt`

**Interfaces:**
- Consumes: `ThynkStudioEntry.MUSIC`, `ThynkStudioEntry.IT`, secure `FinalHomeDestination.THYNK` authorization.
- Produces: explicit `openThynkIt()` and `openThynkMusic()` intent at every shortcut that enters a studio product.

- [ ] Write a failing source/behavior contract proving Profile quick action, Home continue-design and generic creator entry select IT explicitly.
- [ ] Add explicit entry helpers without weakening the security gate.
- [ ] Route Camera/video handoff to Music media ownership where appropriate.
- [ ] Run tests and commit.

### Task 4: Connect DM thread creation

**Files:**
- Create/Modify: DM client service file under `app/src/main/java/com/patsy/app/dms/`
- Modify: `DmServiceBindings.kt`
- Modify: `FinalDmLiveRoute.kt` and DM presentation UI only as needed
- Test: focused DM route/service tests

**Interfaces:**
- Consumes: deployed JWT-protected Edge Function `create-dm-thread`.
- Produces: truthful client create-thread operation with unauthorized/unavailable/error states; no fabricated thread.

- [ ] Write failing service/route tests.
- [ ] Implement the minimal authenticated HTTP transport using the existing publishable key/session token pattern.
- [ ] Bind it in `PatsyApplication`.
- [ ] Add UI entry only when recipient input/resolution is already available; otherwise expose no fake success path.
- [ ] Run full tests and commit.

### Task 5: Reconcile declared routes and deep links

**Files:**
- Modify: navigation contract/shell files as needed
- Modify: `AndroidManifest.xml` only if an external URI scheme is already explicitly specified in project requirements
- Test: navigation/deep-link tests

**Interfaces:**
- Consumes: secure shell authorization and THyNK internal workspace routes.
- Produces: every authorized internal route maps to a renderable state; only registered external routes are advertised.

- [ ] Add failing tests for declared-but-unrenderable THyNK subroutes.
- [ ] Map supported subroutes into THyNK workspace state or classify them internal-only.
- [ ] Do not invent a public URI scheme.
- [ ] Run tests and commit.

### Task 6: Branding/document supersession cleanup

**Files:**
- Modify: `app/src/main/AndroidManifest.xml` or app-name resource
- Modify: `CODEX_START_HERE.md`
- Modify: `docs/NAVIGATION_LOCK_2026-08-31.md`
- Modify: `docs/PATSY_GLOBAL_NAVIGATION_LOCK_2026-08-31.md`
- Modify: `docs/THYNK_MASTER_UI_LOCK.md`

**Interfaces:**
- Produces: app-visible brand `THyNK-IN!` and explicit supersession pointers to the 2 September source.

- [ ] Add tests/source checks for app label and authoritative-doc pointer.
- [ ] Update the label without renaming risky package/application IDs.
- [ ] Add prominent superseded notices to conflicting historical docs; do not delete history.
- [ ] Run tests and commit.

### Task 7: Patsy/runtime and provider truth verification

**Files:**
- Test/inspect existing Patsy Rive, feature service bindings and provider-state contracts.

**Interfaces:**
- Produces: verified fallback behavior when `patsy_assistant` is absent and explicit NOT_CONFIGURED provider states.

- [ ] Confirm no unverified production `.riv` is packaged.
- [ ] Confirm Rive host still validates the ABI before replacing fallback.
- [ ] Confirm AI/web/scheduling/provider creation paths remain fail-closed unless configured.
- [ ] Run tests; change code only if a truth regression is found.

### Task 8: Final build and APK gate

**Files:**
- `.github/workflows/consolidation-ci.yml` only if required for build publication robustness.

**Interfaces:**
- Produces: fresh debug APK and release build from the exact reconciled head.

- [ ] Run complete debug unit tests.
- [ ] Build debug APK.
- [ ] Build release variant.
- [ ] Confirm launcher is `FinalMainActivity` and debug preview is not a launcher.
- [ ] Confirm five-logo panel, Music/IT ownership and editor-homebar behavior are in the packaged source head.
- [ ] Publish the debug APK through the prerelease path if Actions artifact quota remains exhausted.
- [ ] Keep PR Draft/unmerged pending physical-device QA.

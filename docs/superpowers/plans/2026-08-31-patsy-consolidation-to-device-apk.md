# Patsy Consolidation to Device APK Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Consolidate the strongest verified Patsy Android work into one Draft integration line that preserves the FINAL Login, Set Password and Home visuals, produces installable APKs, and prepares safe integration of auth, Rive, THyNK, DMs and protected-account foundations.

**Architecture:** Treat PR #33 as the visual baseline. Integrate only verified contracts from other Draft branches, preserving one canonical navigation contract, one auth boundary, one Patsy Rive boundary, and one Supabase backend. Do not redesign the three FINAL screens, fabricate a production .riv, or weaken release security.

**Tech Stack:** Android/Kotlin/Compose, Gradle, GitHub Actions, Rive Android runtime, Supabase, DataStore.

**Spec:** docs/design/PATSY_FINAL_LOGIN_SET_PASSWORD_HOME_LOCK.md

## Global Constraints
- FINAL visual source of truth: Login, Set Password, Home approved 2026-08-31.
- Navigation: HOME • THyNK • CREATE • PATSY DMS • PROFILE.
- Patsy is the animated AI entity; no GIF/sprite/static-pose fake completion.
- Production .riv remains required and must be ABI-validated before use.
- Login persistence must never store plaintext passwords.
- Separate saved-content Remember Me from auth/session semantics.
- Supabase remains the only production backend.
- Keep all integration work Draft and off main until device visual QA and security review pass.

---

### Task 1: Freeze and verify the FINAL-three baseline
- Confirm PR #33 head and changed files.
- Require unit tests, debug build, release build and APK upload to pass on the same SHA.
- Record APK artifact digest and extracted APK SHA-256.

### Task 2: Preserve exact visual contracts
- Keep `FinalVisualContract`, `FinalLoginScreen`, `FinalSetPasswordScreen`, `FinalHomeScreen`, `FinalScreenChrome` and the exact approved logo asset as canonical for these three pages.
- No older mockup may override these files without owner approval.
- Device screenshots are still required before claiming pixel-perfect match.

### Task 3: Reconcile auth/session contracts
- Import only the proven retention/password-redaction behavior from PR #31 that is not already present.
- Keep debug test access isolated from release source sets.
- Keep visible FINAL-screen copy stable while code semantics remain explicit and testable.

### Task 4: Wire production Supabase auth through one adapter
- Implement a production `AuthGateway` HTTPS adapter against the existing Supabase auth Edge Functions.
- Keep `PatsyServiceBindings` fail-closed until configuration is present.
- Add login, restore, sign-out, registration and reset error mapping tests.
- Never embed service-role secrets in Android.

### Task 5: Reconcile protected shell/navigation
- Pull the typed account bootstrap / protected route-gating concepts from PR #30.
- Preserve the exact five primary destinations.
- Keep Schedule secondary.

### Task 6: Reconcile Patsy Rive foundations
- Preserve `PatsyRigContractV1`, `PatsyRigRuntimePort`, `PatsyRiveRuntimeAdapter`, `PatsyRiveHost` and fallback behavior.
- Add/retain tests for ABI validation, missing-rig fallback, reduced motion and semantic actions.
- Do not mark full animation complete until `patsy_assistant.riv` is supplied and verified on device.

### Task 7: THyNK foundation repair
- Rebase the useful editor-state/tool-catalog/Media3 work from PR #23 onto this line.
- Fix the currently failing single-clip state contract before adding more Studio features.
- Keep the locked THyNK visual system unchanged.

### Task 8: Supabase security review before production merge
- Do not auto-apply schema changes.
- Resolve the current private-schema RLS warnings deliberately with explicit policies/service-role-only access decisions.
- Re-run Supabase security advisors after any database change.

### Task 9: DMs and Remember Me integration preparation
- Reuse the existing server-backed DM schema/function work; do not build a second messaging backend.
- Keep Remember Me saved-content/memory architecture separate from login persistence.

### Task 10: Device validation gate
- Install the debug APK on a physical Android device.
- Verify Login -> Set Password -> Home flow, close/reopen behavior, logout/relogin, locked navigation, visual comparison and accessibility.
- Capture screenshots and compare side-by-side against the three FINAL references before any merge recommendation.

## Verification Gate
Run on every integration checkpoint:
`./gradlew testDebugUnitTest assembleDebug assembleRelease --stacktrace`

Required evidence before merge recommendation:
- 0 test failures
- debug APK builds
- release build succeeds
- debug auth unavailable in release
- physical-device screenshots match approved references
- production Supabase adapter configured and tested
- Supabase security review completed
- no fabricated production Rive claim

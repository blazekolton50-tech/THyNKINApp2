# PatsyApp — Codex Handoff — 2026-08-31

## Active source of truth
- Repository: `blazekolton50-tech/PatsyApp`
- Active draft PR: **#38 — Draft: integrate THyNK Studio, THyNK Music and locked Camera shell**
- Active branch: `chatgpt/thynk-music-apk-2026-08-31`
- Keep PR #38 **Draft**. Do not merge to `main`.
- PR #35 is the secured consolidation ancestor, not the current working branch.
- Read first:
  - `docs/PATSY_DESIGN_PRESERVATION_MASTER_2026-08-31.md`
  - `docs/NAVIGATION_LOCK_2026-08-31.md`
  - `docs/codex/PATSY_CURRENT_BUILD_STATUS_2026-08-31.md`
  - `docs/superpowers/plans/2026-08-31-thynk-video-player-integration.md`
  - `docs/superpowers/plans/2026-08-31-global-navigation-every-authenticated-page.md`

## Latest fully verified code milestone
Head:
`46cf3a0665a56ad7dcd1e7ff8f8e1c225954c05e`

GitHub Actions:
- workflow: `Patsy Consolidation CI`
- run id: `33418288521`
- run number: `138`
- unit tests: PASS
- debug build: PASS
- release build: PASS
- APK upload: PASS
- Android Studio ZIP creation/upload: PASS
- conclusion: SUCCESS

Artifacts:
- `patsy-thynk-music-debug-apk`, id `9767979354`, sha256 `c79f72b9f9d4c2cea47d7862cd5347b82b974927d14d2234b84f578af9dcd492`
- `PatsyApp-AndroidStudio-THyNK-Music`, id `9767981130`, sha256 `6b3023696578620fbd16bc9c8d4300644b2d10e669f6113e489169c194b6f9f5`

Documentation-only commits may sit above that milestone; always inspect CI on the final head before claiming completion.

## Work already completed outside Codex
Do **not** redo this work:

### Locked navigation visual correction
`FinalPrimaryNavigationBar.kt` has been corrected toward the newly supplied navigation reference:
- Home visible label remains `Home`
- coloured THyNK wordmark has **no second small white THyNK subtitle**
- centre is the large `+` without a CAMERA caption
- DM visible label is `PDMs`
- Profile remains `Profile`
- semantic destinations still remain HOME / THyNK / CAMERA / PATSY DMS / PROFILE

Binding visual spec:
`docs/NAVIGATION_LOCK_2026-08-31.md`

The integration script was also repaired so CI no longer tries to reintroduce the old nav presentation.

### THyNK editor-state foundation ported with TDD
Source: useful engine/state work from draft PR #23.

Ported to PR #38:
- `app/src/main/java/com/patsy/app/studio/StudioEditorState.kt`
- `app/src/main/java/com/patsy/app/studio/StudioToolCatalog.kt`
- `app/src/test/java/com/patsy/app/studio/StudioEditorStateTest.kt`
- `app/src/test/java/com/patsy/app/studio/StudioSingleClipStateTest.kt`
- `app/src/test/java/com/patsy/app/studio/StudioToolCatalogTest.kt`

TDD evidence:
- RED head `7b1a3b42a0b15a219f78aa5d11ffb2d1ca90b9b1`
- RED run `33418034706` / #134
- integration script passed; tests failed on unresolved StudioEditorState/StudioAction/StudioToolCatalog symbols
- GREEN head `46cf3a0665a56ad7dcd1e7ff8f8e1c225954c05e`
- GREEN run `33418288521` / #138 full success

Do not port PR #23's obsolete `app/src/main/java/com/patsy/app/studio/ThynkStudioScreen.kt`.

## Locked visual constraints
- Premium black/charcoal base.
- White/light-grey text.
- White primary controls with dark text where applicable.
- Restrained rainbow/neon accents and thin glow.
- Exact approved Patsy wordmark asset only.
- Exact approved THyNK logo/brush-y treatment only.
- Realistic MASTER APP PATSY only for the main companion; cartoon Patsy stays PawMoji/sticker-only.
- One Patsy companion per page.
- Do not fake the production Rive asset.

## Permanent navigation — newest binding rule
Semantic destinations:
`HOME · THyNK · CAMERA · PATSY DMS · PROFILE`

Visible navigation must match `docs/NAVIGATION_LOCK_2026-08-31.md`:
`Home · [coloured THyNK wordmark only] · [large + only] · PDMs · Profile`

Rules:
- no duplicate white THyNK subtitle
- no CAMERA caption beneath centre +
- PDMs is the visible short label; semantic route remains PATSY DMS
- black base + rainbow wave/glow
- same bar on every authenticated page, including nested THyNK/editor pages, Owner Profile, Owner Tools and authenticated protected/denied states
- Home must not keep a second private implementation
- do not expose functional authenticated routes before a session exists on Login/Set Password

Exact implementation plan:
`docs/superpowers/plans/2026-08-31-global-navigation-every-authenticated-page.md`

## Current THyNK architecture — preserve
Main categories:
1. Design & Templates
2. Social & Content
3. Photo & Image
4. Video & Camera
5. Documents & Business
6. Homework & Study
7. Presentations & Planning
8. Collage & Creative
9. Music & Audio / THyNK Music
10. AI & My Studio

Current PR #38 already includes the category hub/screens, THyNK Music UI/workflow foundation and Camera shell. Do not create another THyNK home.

## NEXT CODEX JOB 1 — Media3 / VIDEO & CAMERA editor integration
Execute:
`docs/superpowers/plans/2026-08-31-thynk-video-player-integration.md`

Important boundaries:
- add Media3 `1.8.1` only after tests
- port reusable `StudioVideoPlayer`, not PR #23's old screen
- route current VIDEO & CAMERA items to a nested current-THyNK editor
- use the now-green `StudioEditorState` / `StudioToolCatalog`
- keep empty/failed media truthful
- do not insert fake sample media as production content
- outer global bottom bar must remain visible while editor route changes internally

## NEXT CODEX JOB 2 — One permanent global navigation shell
After Job 1 is green, execute:
`docs/superpowers/plans/2026-08-31-global-navigation-every-authenticated-page.md`

Known current gaps:
- Home still has `FinalHomeBottomNavigation`, a duplicate implementation
- Owner Profile and Owner Tools are not currently wrapped by `FinalPrimaryNavigationBar`
- authenticated protected/denied pages are not currently wrapped by the bar

Refactor those to one authenticated-shell-owned `FinalPrimaryNavigationBar`. Do not change the route authorization logic.

## Authentication / security
Keep current server-backed/fail-closed foundations:
- Supabase-backed auth/session only
- encrypted token/session storage
- publishable key only in Android
- no service-role key or provider secrets in client
- account-bootstrap + route authorization fail closed
- Owner capabilities are server-authorized, never granted from username/email/debug/local state
- protected/under-16 boundaries cannot be bypassed through deep links/navigation

Live connected Supabase verification during this pass:
- active/healthy project
- 37 public tables, RLS enabled on all 37
- 10 private tables
- 119 policies
- 4 private storage buckets
- 0 current Auth users
- Security Advisor: 0 security lints
- security gate issue #36 is resolved

Real-user/device authentication remains unverified; do not claim it complete from CI.

## Patsy DMs
After the two jobs above remain green:
- keep primary semantic destination `PATSY DMS`
- real Supabase-backed messaging only
- preserve protected/age capability gate
- no fake/local-only message content represented as production
- retain current `create-dm-thread` server boundary
- default DM retention target remains ~3 days where current schema/policy allows user adjustment

## Storage / Remember Me / Put-Away Portal
Later production slice:
- account scoped
- device-first media where practical
- feed/shared media retention target ~90 days
- profile target 100 pictures + 30 videos, profile picture excluded
- Put-Away Portal archive/restore/albums
- user memory review/update/delete controls
- age/privacy boundaries

## Rive/device boundary
Do not fake this:
- production `.riv` visual asset is still unavailable/unverified
- existing host/controller/adapter contracts stay intact
- no GIF, sprite or static-pose substitute may be called production Rive
- real device validation still required for final animation and final visual comparisons

## Verification discipline
Before reporting a Codex job complete:
1. show exact starting and ending SHA
2. show changed files
3. show RED test output first for behavior changes
4. show GREEN unit tests
5. show debug build
6. show release build
7. show exact GitHub Actions run/result on final head
8. show new artifact ids if produced
9. state device/provider/Rive/auth limitations truthfully
10. keep PR #38 Draft

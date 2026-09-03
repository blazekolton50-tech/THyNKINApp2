# Patsy PR #41 / PR #40 Controlled Consolidation Design

**Date:** 2026-09-01  
**Status:** Owner-approved design; Draft-only; no merge authorization  
**Repository:** `blazekolton50-tech/PatsyApp`  
**Working branch:** `chatgpt/codex-ready-2026-09-01`  
**Starting head:** `c5de22a57dfc8186f142d99c770b1b8425aeb296`

## Purpose

Continue the newest verified native Patsy line while preserving the approved branding work that currently exists only on sibling Draft PR #40. The consolidation must not merge PR branches wholesale, restore superseded handoff text, weaken security gates, or combine dependent product jobs into one unverifiable patch.

## Verified starting truth

- Draft PR #41 is the current Codex handoff and source-of-truth branch.
- PR #41 contains handoff documents only and is based directly on verified PR #39 head `78009aa63cdbdb0ae1d33a537ec14392f0b3380b`.
- PR #39 Patsy Consolidation CI run `33444861508` / #206 passed unit tests, debug build, release build, debug APK upload, and Android Studio ZIP upload.
- The downloaded PR #39 Android Studio artifact passed archive integrity checks and its outer SHA-256 matched `7b00238c168f1796037596c5ec14b7e17c67ff817dc2805b7f00b3bb8cabad32`.
- Draft PR #40 is a sibling branch from PR #39. Its final homebar geometry code and logo mapping are not present in PR #41.
- The two owner-supplied logo files are present and verified against PR #40:
  - `THYNK_badge_sticker_transparent (1)(3).png`
    - SHA-256: `be8310ca168ff43aa79a609f1a06f73165ac3cbb0ff694ce2fb8132adb2e362d`
    - target: `app/src/main/res/drawable-nodpi/thynk_logo_official.png`
  - `THYNK_Music_rainbow_matched_transparent(1).png`
    - SHA-256: `5f666b51c93e475be0511aaaec4d11e1744e9194b331f84a4bb4c2386dcf3423`
    - target: `app/src/main/res/drawable-nodpi/thynk_music_logo_official.png`
- Both files are valid 1600 x 1600 RGBA PNGs.
- The approved production Patsy `.riv` is still unavailable/unverified. Rive host and controller foundations are not production animation evidence.

## Source precedence

When sources disagree, use:

1. Current native Kotlin/Compose code on `chatgpt/codex-ready-2026-09-01`.
2. The 2026-09-01 Codex status, source-intake, and execution documents on PR #41.
3. Checked-in design, navigation, authentication, Owner, age, and security locks.
4. Live Supabase security truth.
5. Latest explicit owner-approved design requirements.
6. Verified owner-supplied files with known bytes, provenance, checksums, and supersession decisions.
7. Drive, AI Studio, Replit, Canva, archive, HTML, React, and WebView donor material as `REFERENCE_ONLY` unless promoted by the verifier.

PR #40's older `CODEX_START_HERE` and handoff wording must not replace PR #41's newer handoff. Only still-current branding code and verified owner assets are candidates for controlled porting.

## Locked product constraints

- Native Android/Kotlin/Jetpack Compose remains the production architecture.
- Keep all work Draft and unmerged.
- Preserve one authenticated primary homebar across authenticated and nested screens.
- Semantic destinations remain `HOME · THyNK · CAMERA · PATSY DMS · PROFILE`.
- Visible bar remains `Home · [official coloured THyNK only] · [large + only] · PDMs · Profile`.
- The centre `+` continues to route to the existing native Camera foundation. The legacy internal enum name `CREATE` is not user-facing.
- The homebar separator is straight on the outer sides and rises only in a smooth centre arch around the Camera control; it is not a full-width wave.
- No duplicate THyNK caption and no visible CAMERA caption.
- Patsy pages use the official Patsy logo. THyNK pages use the official THyNK logo. Music pages and, under the current owner lock, Video editing pages use the official THyNK Music logo.
- Do not weaken login, session, deep-link, age/protected, Owner, RLS, or provider boundaries.
- Provider-backed features remain `NOT_CONFIGURED` until genuinely connected.
- Do not use a GIF, sprite, video loop, static pose swap, or PNG bob as production Rive.
- Do not fabricate templates, audio, waveforms, calling, cloud save, export, user data, analytics, or success states.

## Chosen approach

Use two independently verified slices on PR #41 before continuing the existing dependent queue.

### Slice 1: Verified donor-asset intake foundation

Execute the existing plan `docs/superpowers/plans/2026-08-31-thynk-donor-asset-intake.md` with the PR #41 branch override and 2026-09-01 classifications.

Create a pure Kotlin boundary under `com.patsy.app.studio.assets`:

- donor asset type, availability, license, and duplicate status;
- immutable donor record and manifest;
- reference-only catalogue for audited sources;
- fail-closed verification evidence and decision types;
- production-ready filtering that never exposes `REFERENCE_ONLY` or `REJECTED` records.

Implementation correction to the older plan: a byte-backed checksum must be a real SHA-256 value, represented as exactly 64 hexadecimal characters. Tests must not treat placeholder values such as `abc123` as valid production verification evidence.

The source catalogue must encode at least:

- known Drive/AI Studio/archive wrappers as `REFERENCE_ONLY`;
- the older uploaded Android project artifact as `REFERENCE_ONLY`;
- Replit/React/HTML donor lanes as `REFERENCE_ONLY`;
- Gemini Java and XML WebView wrappers as `REJECTED`;
- no production-ready record by default.

No production UI is modified in this slice. No archive contents are copied into the APK.

### Gate after Slice 1

Stop until all of the following are evidenced on the exact branch head:

- intentional RED tests for missing donor contracts;
- targeted GREEN donor tests;
- complete unit suite GREEN;
- debug build GREEN;
- release build GREEN;
- fresh GitHub Actions result GREEN;
- exact ending SHA and changed-file list;
- confirmation the PR remains Draft and unmerged.

If the local workspace cannot download Gradle because of network policy, that limitation is reported separately and cannot be presented as a code failure or a GREEN result. GitHub CI remains required.

### Slice 2: Branding and homebar reconciliation

After Slice 1 is GREEN, port only the current, approved portions of PR #40 onto PR #41:

1. Promote the two owner-supplied logo records only after byte, SHA-256, origin/license, Android compatibility, and unique/supersession evidence pass the donor verifier.
2. Copy the verified PNG bytes to their exact `drawable-nodpi` targets without redrawing, recompressing, tracing, or recreating them.
3. Add the approved `FinalHomebarRainbowLine` geometry.
4. Replace the typed THyNK fallback with the official bitmap through Compose resource rendering.
5. Make header branding route-aware without creating a second THyNK shell or editor.
6. Preserve the existing native Camera route and the shared outer bar.
7. Add tests for resource presence/checksums or equivalent deterministic build-time verification, visible label rules, route-to-brand selection, and centre Camera mapping.

Do not copy PR #40's superseded handoff files over PR #41.

### Gate after Slice 2

Repeat the Slice 1 verification gate and additionally require physical-device visual QA later for:

- official logo scaling and transparency;
- homebar arch position around the large centre `+`;
- no overlap or clipped touch targets;
- all authenticated destinations and nested editors retaining the shared bar;
- correct Patsy/THyNK/THyNK Music header selection.

## Later dependent queue

Only after both consolidation slices are GREEN:

1. Generic THyNK canvas, Android image import, and real PNG export.
2. Design & Templates integration and account-scoped editable-draft restore.
3. THyNK Music ten-screen refinement.
4. Profile and exact account-menu alignment.
5. Separate DMs, Put-Away Portal/storage, Remember Me memory, under-16/protected, provider, and production-Rive slices.

Dependent jobs remain sequential. A failed job stops the queue.

## Component boundaries

| Boundary | Responsibility | Must not do |
| --- | --- | --- |
| Donor manifest | Store audited metadata and truth state | Fetch or execute donor code |
| Donor verifier | Promote only complete, valid evidence | Infer license, checksum, uniqueness, or bytes |
| Production-ready filter | Return verified usable records | Leak reference/rejected items into UI |
| Homebar | Render one locked global navigation system | Own auth or bypass route gates |
| Brand resolver | Select Patsy, THyNK, or THyNK Music asset for a route | Create new destinations or typed logo substitutes |
| Rive host/runtime | Preserve animation ABI and honest unavailable state | Claim an authored production rig exists |

## Error and truth model

- Invalid or incomplete donor evidence returns explicit rejection reasons.
- Invalid SHA-256 evidence fails closed.
- Duplicate or superseded records never become production-ready.
- Missing resource bytes fail the branding build rather than falling back silently to typed production branding.
- Route resolution defaults to the correct Patsy app identity outside THyNK workspaces and never changes authorization.
- A failed save, export, provider call, authentication request, or backend request never displays success.

## Verification strategy

For each slice:

1. Add focused failing tests first and record the expected failure.
2. Implement the smallest code needed to pass them.
3. Run targeted tests, the complete unit suite, debug build, and release build.
4. Push one coherent slice to PR #41.
5. Require fresh CI on the exact pushed SHA.
6. Record artifacts and remaining device/provider/Rive limitations.

The final merge decision is outside this design and requires explicit owner approval plus physical-device QA.

## Rejected alternatives

### Merge or cherry-pick PR #40 wholesale

Rejected because it would also carry superseded handoff wording and blur which source is authoritative.

### Combine donor intake, branding, canvas, Music, Profile, and DMs in one patch

Rejected because failures could not be isolated and dependent truth gates would be lost.

### Rebuild from Drive, older Android ZIPs, React, HTML, WebView, or Replit

Rejected because these sources are older or reference-only and would replace verified native architecture.

## Completion definition

This controlled consolidation is complete only when Slices 1 and 2 each have exact RED/GREEN evidence, full builds, fresh CI, exact SHAs, and Draft status. It does not mean the full Patsy app, providers, physical-device behavior, or production Rive is complete.

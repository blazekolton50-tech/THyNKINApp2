# THyNK Donor Consolidation Design

**Date:** 2026-08-31

**Status:** Owner-approved architecture consolidation for PR #38. This document does not authorize a merge to `main` and does not claim device/provider/Rive completion.

## Purpose

Consolidate the useful parts of the newest user-supplied THyNK Music visual reference, React/Tailwind component references, Gemini code snippets, Google AI Studio prompt, Google Drive archives, Replit prototype and current Android ZIP without creating another Patsy app or replacing the secured native Android architecture.

The production implementation remains the native Android/Kotlin/Jetpack Compose project in PR #38 on branch `chatgpt/thynk-music-apk-2026-08-31`, backed by the live Supabase project. Donor sources are input material only until a feature is ported to native code, covered by tests and verified by CI.

## Source hierarchy

Use this precedence when sources conflict:

1. Current PR #38 code and checked-in locked contracts.
2. `docs/PATSY_DESIGN_PRESERVATION_MASTER_2026-08-31.md` and `docs/NAVIGATION_LOCK_2026-08-31.md`.
3. Live Supabase security/auth/storage truth.
4. Owner-supplied current visual references in the 2026-08-31 conversation, especially the 10-screen THyNK Music sheet.
5. Google AI Studio / Gemini / Drive / Replit / uploaded ZIP donor material.
6. Older archive/prototype behaviour only where it does not conflict with items 1-5.

Never let a donor source weaken auth, age rules, RLS, Owner authorization, navigation locks or truthful `NOT_CONFIGURED` provider states.

## Source audit and allowed use

### Current native PR #38

Authoritative production lane. Existing useful foundations include:

- FINAL Login / Set Password / Home shell.
- Secured Supabase auth/account bootstrap/Owner route foundations.
- Locked 10-category THyNK hub.
- Existing ten-page THyNK Music route model and first-pass Compose UI.
- Locked Camera shell.
- `StudioEditorState.kt` and `StudioToolCatalog.kt` with existing tests.
- Shared `FinalPrimaryNavigationBar` visual contract.

Do not rebuild these from scratch.

### Owner-supplied THyNK Music 10-screen visual sheet

Treat this as the visual/interaction target for the dedicated Music area while preserving the app shell and security architecture.

The ten screens are:

1. Music Home
2. Create Music
3. AI Music Generator
4. Track Editor
5. Mixer
6. Equalizer
7. Effects
8. Lyrics & Vocals
9. Mastering
10. Export

Required visual character:

- black/charcoal surfaces;
- white primary text;
- restrained purple/rainbow accent treatment;
- multicolour audio waveforms where appropriate;
- compact DAW-style controls;
- clear track/channel hierarchy;
- persistent locked Patsy primary navigation outside the page content;
- no replacement editor-only bottom navigation.

The visual reference is not permission to fake music generation, mastering, export or rendered audio. Provider-backed actions remain `NOT_CONFIGURED` until a real approved service exists.

### Owner-supplied React/Tailwind 50-component sheet

Use only as a UX-pattern catalogue. Candidate patterns include:

- upload/drop zone;
- progress stepper;
- search/autosuggest;
- calendar/scheduler;
- timeline/roadmap;
- accordions;
- data tables;
- loading states;
- modal/overlay patterns;
- media-player controls;
- responsive grid/card behaviour.

Do not import its white SaaS/dashboard visual style into Patsy. Reimplement selected patterns as native Compose components using locked Patsy tokens.

### Owner-supplied Gemini snippets

Use behaviours, not React/Tailwind code.

Useful donor concepts:

- an editor drop zone that accepts image/vector assets and exposes explicit resize handles;
- card/grid composition examples as optional template inspiration;
- high-resolution export behaviour that renders a selected design at higher than viewport resolution before producing PNG output;
- explicit export action separated from editable project state.

The fashion-specific wording, colours and demo subject matter are not Patsy product requirements.

### Google AI Studio prompt: `Patsy Creation Studio Core Implementation`

Compatible donor requirements to retain:

- production Kotlin/Jetpack Compose architecture;
- typed project/canvas/preset/tool models;
- centrally defined canvas presets plus custom dimensions;
- square-first canvas entry point;
- responsive tool placement;
- Templates, Notes, Upload, Stickers/PawMojis, Text and AI Generate tool hooks;
- select/move/resize/crop/rotate/flip;
- layers/order, duplicate/delete, opacity;
- image adjustments including exposure/brightness, contrast, saturation and warmth, with blur/sharpen only where technically valid;
- proper undo/redo history;
- separate types for editable project, exported media, retained media, personal template and community-shared template;
- provider-neutral AI state returning `NOT_CONFIGURED` when unconfigured;
- future animated Patsy guide hooks without a fake static production Patsy.

Do not copy older v3.3.9 assumptions over newer PR #38 contracts.

### Google Drive donor archives

Drive currently exposes multiple recovery/reference archives, including:

- `patsy_full_1110_real.zip` wrappers;
- duplicated `patsy_thynk_stage1_real.zip` wrappers;
- `PDF_Reader_SETUP_THYNK_STAGE2_REAL.py.txt`;
- `Icons_100_Illustrations_100_Logos_50_Original.zip` wrapper;
- `Upscale_Studio_Canva_Snapchat_Video_Photo_Sound_Architecture.zip` wrapper;
- `Full_App_Backend_Frontend_Editor_1110_Bricks.zip` wrappers;
- `Fonts_50_Free_Pack_Codex_Tailwind.zip` wrapper;
- `Social_50_BusinessCards_25_Slides_50_Original.zip` wrapper;
- other template/catalogue packs.

These are donor/recovery sources only. Before any contained asset becomes production content, verify that the actual bytes are available, that licensing/origin is acceptable, that it is not a duplicate/outdated asset, and that it fits the locked native model. Never treat wrapper HTML or archive names as proof that the contained implementation is production-ready.

The Stage 2 `REAL` setup is specifically web-oriented in important areas (Node/Express, React, browser Canvas/WebAudio/MediaRecorder). Its concepts may be ported, but its architecture must not replace native Kotlin + Supabase.

### Replit

`Patsy Android Companion` remains Expo/React Native prototype/reference material. Current audit confirms no newer native Android, THyNK Music, Supabase, canvas import/export or asset work. Do not cherry-pick its old navigation, AsyncStorage model, static Patsy image or placeholder service architecture.

### Canva / Figma / Adobe / Meta / Vercel

- Canva currently exposes the personal Patsy Creator Assistant document, not a newer app screen set. It is not an app UI source of truth.
- No resolvable Patsy Figma file key is currently available, so no Figma screen can be promoted into production from this audit.
- Adobe connector access currently fails with HTTP 403; no Adobe asset may be claimed verified from this session.
- No distinct Meta/Facebook/Instagram/Llama implementation artifact or dedicated Meta plugin was found. Social-platform words inside template archives are not a Meta integration.
- Vercel currently has no Patsy project and is not part of the production runtime.

## Target architecture

### A. Preserve one authenticated Patsy shell

All authenticated destinations continue to use the locked semantic routes:

`HOME · THyNK · CAMERA · PATSY DMS · PROFILE`

Visible bar remains:

`Home · coloured THyNK · large + · PDMs · Profile`

No THyNK editor, Music screen, canvas, export flow or Owner route may replace or hide the canonical bar once the global navigation refactor is complete.

### B. Split the current oversized THyNK UI by responsibility

`ThynkStudioScreen.kt` currently mixes hub, category navigation, Music UI and Camera preview helpers. New work should preserve public behaviour while extracting focused units instead of adding another large monolith.

Recommended responsibilities:

- THyNK hub/category routing.
- THyNK Music route/model shell.
- individual Music page composables and reusable audio UI controls.
- generic canvas/editor shell.
- canvas model/history/transforms.
- import/media authorization.
- export renderer and export state.
- asset/template catalogue intake.

Do not create a second THyNK home or parallel editor state architecture.

### C. THyNK Music progression

The ten existing route IDs should remain stable where possible. Refine the current first-pass pages toward the supplied visual sheet in native Compose.

Local-only UI controls may work immediately when they represent editor state rather than pretending to process audio. Examples: selecting a track, changing a local mixer slider value, toggling a visual effect option, changing an EQ point, editing lyrics text, choosing an export format.

Actions that require real generation, DSP rendering, mastering, stems, codec/export, speech/vocal processing or provider calls must expose truthful availability state. UI may be visually complete while the action remains disabled or `NOT_CONFIGURED`.

### D. Generic THyNK canvas

The canvas must be a reusable editor foundation for relevant THyNK categories, not a fashion-specific or social-platform-specific screen.

Required core model:

- project ID and project mode;
- canvas width/height/aspect;
- centrally defined presets and custom size validation;
- ordered element/layer collection;
- selected element ID;
- element transform: position, scale/size, rotation, horizontal/vertical flip;
- opacity;
- element-specific content and adjustments;
- immutable history snapshots or commands supporting undo/redo;
- truthful media state.

Canvas interactions:

- select;
- move;
- resize with explicit handles/touch affordance;
- rotate;
- flip;
- duplicate;
- delete;
- reorder layers;
- crop for supported media;
- undo/redo;
- responsive canvas fitting without changing logical export dimensions.

### E. Import

Use Android-native media selection and persisted content URIs where appropriate. Do not invent local fake media.

Initial image/vector-compatible import should validate:

- MIME type;
- readable URI;
- dimensions where relevant;
- size limits defined centrally;
- supported project mode;
- authorization/persistence result.

The UI may show a polished drop-zone-style surface inspired by Gemini, but Android interaction should use tap/picker and supported drag/drop APIs rather than DOM behaviour.

Video import belongs to the separately checked-in Media3 plan and must integrate with, not duplicate, this media model.

### F. Export

Keep export separate from project persistence.

For the first generic image-editor vertical slice:

- PNG export is the required real format.
- Render at the logical canvas dimensions or an explicit export scale, never merely screenshot the visible Compose viewport.
- Use Android-native rendering/storage APIs.
- Return a typed success/failure result and URI only after real bytes are written.
- Preserve the editable project after export.
- Never mark an export successful because a preview was rendered.

JPEG/WebP/PDF/video/audio formats should only be enabled after their native encoding path and tests exist. THyNK Music export remains a separate audio capability and must not reuse a fake image-export success state.

### G. Asset/template intake

Create a manifest-driven intake path instead of copying whole archives into the app.

Each accepted donor asset/template record must include:

- stable ID;
- source/origin label;
- category/subcategory;
- media/template type;
- dimensions/aspect where relevant;
- local/repository path or approved remote reference;
- license/origin status;
- checksum when bytes are available;
- duplicate status;
- production availability state.

Only `VERIFIED`/equivalent records may appear as production-ready assets. Unknown/unavailable archive contents remain reference-only.

## Data flow

1. User enters THyNK through the authenticated Patsy shell.
2. Category selection routes within the existing THyNK navigation state.
3. Generic editor categories create/load a typed `StudioEditorState`-compatible project.
4. Import resolves an Android content URI and validates it before adding a media element.
5. User edits via deterministic state transitions recorded in history.
6. Autosave/persistence remains separate from rendered export.
7. Export renders from logical project state to actual bytes and returns a real URI or failure.
8. Music UI uses its own typed music/editor state and truthful provider/DSP capability states while preserving the same outer shell.

## Error and truthfulness rules

- Never fake provider completion percentages, generated audio, mastering, export files or imported media.
- `NOT_CONFIGURED`, `EMPTY`, `LOADING`, `READY` and `FAILED` states must reflect reality.
- Unsupported MIME types or invalid dimensions fail clearly without adding a fake layer.
- Export failure must not create an `ExportedMedia` success object.
- Missing donor archive bytes remain `REFERENCE_ONLY`, not production assets.
- Auth, age, Owner and RLS gates remain server-authoritative.

## Testing requirements

Use TDD for every implementation slice.

Pure/unit tests should cover:

- canvas-size validation and preset mapping;
- transform clamping/normalization where defined;
- layer order operations;
- duplicate/delete/select behaviour;
- undo/redo;
- import validation decisions independent of Android picker UI;
- export state transitions and media-model isolation;
- Music route mapping and truthful provider-state rules;
- locked visible navigation labels where touched.

Android/instrumented or device verification should cover:

- picker result handling;
- real PNG byte output and readable URI;
- visual resize handles and canvas interaction;
- Media3 video flow from its separate plan;
- persistent bottom navigation across THyNK and Music pages;
- no unauthenticated route bypass.

## Execution order relative to existing PR #38 plans

Do not jump ahead of the already checked-in safety-critical plans.

1. Complete `2026-08-31-thynk-video-player-integration.md` and get green verification.
2. Complete `2026-08-31-global-navigation-every-authenticated-page.md` and get green verification.
3. Implement donor asset/catalogue intake foundations.
4. Implement generic THyNK canvas + image import + real PNG export as a testable vertical slice.
5. Refine the existing ten THyNK Music screens toward the supplied visual reference without faking provider/DSP features.
6. Run unit tests, debug build, release build and final CI on the exact resulting head.
7. Perform physical-device QA and real-user Supabase auth before any merge decision.

## Non-goals for this consolidation

- No new parallel app.
- No migration to React Native, browser Canvas, Node/Express or Vercel.
- No replacement of Supabase.
- No copied Replit navigation.
- No unverified archive dump into production resources.
- No fake production `.riv` or static substitute.
- No automatic enabling of AI/music providers.
- No merge of PR #38 without explicit owner approval and device QA.

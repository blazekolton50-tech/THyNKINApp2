# THyNK Full Studio Reconciliation Design

**Status:** Approved design for Draft PR #41 (`chatgpt/codex-ready-2026-09-01`)

## Goal

Reconcile the newly uploaded THyNK Studio implementation with the current native Android/Kotlin/Jetpack Compose Patsy app without rebuilding functionality that already exists and without replacing the verified native app foundations.

The uploaded THyNK material is a donor implementation and behavioral reference. The production app remains native Android/Kotlin/Compose with the existing Supabase, authentication, protected-account, Camera, Media3, navigation, and provider-boundary rules.

## Source precedence for this reconciliation

1. Current code on `chatgpt/codex-ready-2026-09-01`.
2. Checked-in navigation, branding, authentication, age/security, storage and provider truth locks.
3. Live Supabase authority and existing native services.
4. Newly uploaded THyNK Studio source for editor behavior, tool coverage, workflow and local visual reference.
5. Older Drive/AI Studio/Replit/archive material only when it does not conflict with 1-4.

The uploaded Studio does **not** gain authority over locked app navigation, security, auth, Supabase, production provider truth, or the production Patsy/Rive contract.

## Verified native production foundations to preserve

The current PR #41 tree already contains native foundations including:

- `app/src/main/java/com/patsy/app/studio/StudioEditorState.kt`
- `app/src/main/java/com/patsy/app/studio/StudioToolCatalog.kt`
- `app/src/main/java/com/patsy/app/studio/StudioVideoPlayer.kt`
- `app/src/main/java/com/patsy/app/studio/StudioVideoPlayerLogic.kt`
- `app/src/main/java/com/patsy/app/thynk/ThynkStudioCatalog.kt`
- `app/src/main/java/com/patsy/app/thynk/ThynkStudioScreen.kt`
- `app/src/main/java/com/patsy/app/thynk/ThynkEditorRouting.kt`
- `app/src/main/java/com/patsy/app/thynk/NativeCameraHub.kt`
- `app/src/main/java/com/patsy/app/thynk/CameraHubContract.kt`
- authenticated shell/navigation contracts and tests
- Supabase auth, account bootstrap, Owner authorization and protected boundaries
- Media3 video playback foundation
- Rive host/runtime contracts, but no verified final production Patsy `.riv`

These are the integration targets. Do not replace them with a WebView, React runtime, localStorage auth/state, Express server, Java Spring donor backend, or browser-only media pipeline.

## Newly verified uploaded THyNK implementation

The uploaded Library contains substantial working/donor material, including:

- `Thynk-Full-Ecosystem-Studio.html`
- `Thynk-Full-Studio-Working.html`
- `Thynk-Studio-Pro-Complete` builds
- `FULL-INVENTORY.pdf`
- `THyNK-MUSIC-EVERYTHING.pdf`
- `Building-out-music-studio.tsx` variants
- `THYNK-Music-Lab.tsx`
- `Templates.tsx`
- Gemini/React source covering publication and video editor modes
- 1110-item donor asset/material manifests and setup descriptions

The uploaded implementation demonstrates or specifies:

- multi-world Studio navigation and editor modes
- Design canvas tools, object transforms, layers, rulers/guides, templates, uploads, text, elements, stickers, frames, backgrounds, alignment/distribution
- publication/magazine spread editing, page workflows and print-oriented layout concepts
- video player/timeline/filter/effect/transition/text-animation/overlay donor engines
- THyNK Music player, visualizer, EQ, drum machine, piano roll, sampler, tracks/layers, effects and export workflow
- templates across social, design, posters, documents and presentations
- local autosave/version-history patterns that can inform native draft restoration but must not introduce browser localStorage into production

## Reconciliation status model

Every donor capability must be classified into exactly one of these four statuses before implementation:

### 1. ALREADY_NATIVE

The capability already exists on the PR #41 native line and should be preserved/refined rather than rebuilt.

Examples include native Camera capture/import, FileProvider handling, Media3 video playback foundation, global authenticated primary navigation, THyNK category routing, `StudioEditorState`, `StudioToolCatalog`, security/auth boundaries and truthful provider state handling.

### 2. PORT_FROM_UPLOAD

The uploaded Studio contains behavior or workflow that is useful and should be reimplemented natively using the existing Kotlin/Compose architecture.

Examples include richer Design canvas tools, layers UI, rulers/guides, publication page/spread workflows, fuller Music tools, template surfacing, video timeline/effect affordances and project workflow details.

### 3. REPLACE_PLACEHOLDER

The uploaded Studio contains a UI or demo behavior whose implementation is not production-real and must be replaced by a native or Supabase-backed equivalent.

Examples include JavaScript `alert(...)` button actions, fake export URLs, placeholder generated media, external placeholder image fallback URLs, browser `localStorage`, Express/Prisma donor persistence, or WebAudio-only behavior that is not yet wired to the Android production audio path.

### 4. GENUINELY_MISSING

The capability is required by the locked product rules but neither the native line nor the uploaded donor contains a production-ready implementation.

This must remain explicitly incomplete. Provider-backed features remain `NOT_CONFIGURED` until genuinely configured. Production Rive remains unverified until the authored `.riv` is present and device-tested.

## Architecture

### Native shell remains authoritative

The authenticated Patsy shell remains the only app shell. The permanent primary navigation remains:

`HOME · THyNK · CAMERA · PATSY DMs · PROFILE`

All THyNK editor screens run inside this shell and preserve route gates, back behavior and deep-link security.

### Shared native editor core

The Studio should grow around the existing native editor state and tool catalogue rather than creating separate independent editors with unrelated state models.

Shared editor concepts should cover:

- project identity and editable draft state
- canvas/page dimensions
- deterministic layer/object ordering
- selection and transforms
- undo/redo
- import references
- export state distinct from editable project state
- autosave/draft restore hooks
- category/editor-specific extensions

### Category adapters

Design, Photo/Image, Video, Documents/Publication, Presentations, Collage and Music should use focused category-specific components that consume the shared native project/editor contracts.

Do not put all donor behavior into `ThynkStudioScreen.kt`. Split new code by responsibility when implementing each category.

### Media

Use native Android media and the existing Media3/Camera foundations. Browser `MediaRecorder`, canvas `captureStream`, WebAudio or WebView wrappers are donor behavior references only unless a specific capability has no native equivalent and is separately approved.

### Persistence

Editable projects and autosave must use native app storage/Supabase contracts appropriate to the production architecture. Do not port browser `localStorage` as production persistence.

### AI/provider features

All provider-backed generation remains behind existing service boundaries. Missing provider configuration must surface truthfully as `NOT_CONFIGURED`. Never substitute fake generated output to make an editor appear complete.

## Capability reconciliation by area

### Shared Studio / navigation

- **ALREADY_NATIVE:** authenticated shell, primary nav, THyNK entry, category catalogue/routing, Camera route, editor state/tool catalogue foundations.
- **PORT_FROM_UPLOAD:** donor project-title/workspace affordances and useful cross-editor tool organization where they fit the locked native design.
- **REPLACE_PLACEHOLDER:** React top-level mode switcher as app navigation; browser-only state.
- **GENUINELY_MISSING:** only capabilities confirmed absent after code-level comparison.

### Design & Templates

- **ALREADY_NATIVE:** `StudioEditorState`, `StudioToolCatalog`, native import/video foundations, THyNK category route.
- **PORT_FROM_UPLOAD:** templates, text/elements/stickers/draw/frames/background/layers/rulers/guides/align-distribute workflows; object x/y/w/h/rotation/opacity; drag/resize/rotate; layer hide/lock/reorder; template catalogue surfacing.
- **REPLACE_PLACEHOLDER:** placeholder images/URLs and browser persistence/export shortcuts.
- **GENUINELY_MISSING:** native features not represented by either side after detailed comparison.

### Photo & Image

- **PORT_FROM_UPLOAD:** relevant adjustment/filter/crop/transform concepts that are demonstrated in donor code and fit the locked Photo/Image scope.
- **REPLACE_PLACEHOLDER:** CSS-only/browser filter effects when they do not produce a real Android export.
- Keep Android import/export truthful and testable.

### Video & Camera

- **ALREADY_NATIVE:** Camera capture/import/FileProvider and Media3 single-clip player foundation.
- **PORT_FROM_UPLOAD:** timeline UX, filter/effect/transition/text-animation/overlay models and controls where donor behavior is useful.
- **REPLACE_PLACEHOLDER:** browser `captureStream`, timed demo export, fake backend export URL, alert-only controls.
- Physical Android Camera validation remains required.

### Documents / Publication / Presentations

- **PORT_FROM_UPLOAD:** multi-page publication model, add/duplicate/delete/reorder, spread view, page editor workflow, text/image-frame concepts, print/layout controls, document and presentation templates.
- **REPLACE_PLACEHOLDER:** buttons that only call `alert(...)`; visual-only mock page cards; any claimed PDF/export path that is not producing verified bytes on Android.
- Implement page-model and export slices incrementally rather than treating the entire 40-page concept as one monolith.

### THyNK Music

- **ALREADY_NATIVE:** stable ten-screen routing IDs and existing native Music screen foundation.
- **PORT_FROM_UPLOAD:** player controls, visualizer concepts, 10-band EQ UI/model, beats/drum sequencer, piano roll, sampler editing, track/layer controls, effects and sound-library workflow.
- **REPLACE_PLACEHOLDER:** WebAudio oscillators used as stand-ins, placeholder/random waveform data, backend endpoints not backed by production services, export formats that do not yet have a real native encoder/export pipeline.
- Preserve the stable ten Music route IDs already locked in the app.

### Library / project persistence

- **PORT_FROM_UPLOAD:** useful project/list/library UX and autosave/version-history behavior.
- **REPLACE_PLACEHOLDER:** localStorage and donor Express/Prisma project persistence.
- Integrate with existing native/Supabase storage policies rather than introducing a second backend.

## Asset policy

The 1110-item donor material and template/assets packs are not automatically production assets merely because a manifest names them.

Before copying any donor asset into Android resources, verify:

- the file actually exists
- it is owner-supplied/original/licensed as required
- it is not a placeholder manifest entry
- its dimensions/format are appropriate
- it does not conflict with locked logo/branding assets

Owner-supplied official THyNK and THyNK Music logo PNGs remain the required logo sources. Do not recreate them with text, SVG approximation or CSS.

## Rive boundary

Donor documents mention several `.riv` assets. Their names are not proof that production-ready binaries are present.

Do not claim any named `.riv` asset is integrated until the actual binary is verified and tested. The final production Patsy companion remains blocked on a verified authored production `.riv`; GIFs, sprites and static pose swaps are prohibited.

## Testing strategy

Each ported slice must follow the existing native test pattern and be independently reviewable:

- unit tests for state/model/tool behavior
- route/navigation contract tests where routes change
- Android build verification
- real export byte/content tests for export work
- Media3 tests for video state behavior
- physical-device validation for Camera and media-return flows
- no regression to auth/security/provider truth gates

## Delivery order

1. Commit this reconciliation and update source-intake/current-status/Codex handoff documentation.
2. Build a machine-readable/native-friendly donor capability matrix so later tasks cannot silently duplicate functionality.
3. Design & Templates: port the verified canvas/layer/template behavior onto the native editor core.
4. Photo/Image extensions that share the canvas/export core.
5. Video timeline/effects extensions on top of the existing Media3/Camera foundation.
6. Documents/Publication/Presentations page-model slices.
7. THyNK Music richer tools while preserving the ten stable screen IDs.
8. Library/autosave/project restoration against the native/Supabase storage architecture.
9. Consolidated Android CI and physical-device QA.
10. Production Patsy Rive only when the actual authored `.riv` is verified.

## Non-goals

- No WebView replacement of THyNK.
- No React/Next/Vite runtime embedded as the production Studio.
- No second backend replacing Supabase.
- No weakening auth, RLS, age/protected-account or Owner boundaries.
- No redesign of the locked Patsy/THyNK visual system.
- No fake AI/provider completion.
- No fake production Rive.
- No merge from Draft without explicit owner approval.

## Success criteria

The reconciliation is successful when:

- Codex can see exactly which uploaded Studio capabilities already exist natively, which must be ported, which demos/placeholders must be replaced and which are truly missing.
- New work extends the current native architecture instead of rebuilding or embedding the donor web app.
- Locked navigation, branding, auth/security, Supabase and truthful provider boundaries remain intact.
- Every ported capability has a testable native implementation and does not rely on demo-only browser behavior.
- PR #41 remains Draft until explicit owner approval to merge.

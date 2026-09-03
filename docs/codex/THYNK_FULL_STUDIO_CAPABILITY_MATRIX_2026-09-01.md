# THyNK Full Studio Capability Matrix — 2026-09-01

This is the human-readable companion to `THYNK_FULL_STUDIO_CAPABILITY_MATRIX_2026-09-01.json`.

## Status meanings

- **ALREADY_NATIVE** — preserve/refine the current Kotlin/Compose implementation; do not rebuild it.
- **PORT_FROM_UPLOAD** — the uploaded THyNK Studio contains useful behavior/workflow to reimplement natively.
- **REPLACE_PLACEHOLDER** — the donor has UI/demo behavior, but its implementation is not production-real and must be replaced.
- **GENUINELY_MISSING** — neither side currently has a production-ready implementation; keep it explicitly incomplete or `NOT_CONFIGURED` where appropriate.

## Uploaded donor set now recognized

The reconciliation is based on substantial uploaded Studio material rather than wrapper titles alone, including `Thynk-Full-Ecosystem-Studio(1).html`, `Thynk-Full-Studio-Working(1).html`, `Thynk-Studio-Pro-Complete` builds, `FULL-INVENTORY.pdf`, `THyNK-MUSIC-EVERYTHING.pdf`, `THYNK-Music-Lab.tsx`, `Templates.tsx`, Gemini/React publication/video editor source, and the 1110-item setup/material descriptions.

These files are implementation donors. They do not override the native app shell, security, Supabase, locked navigation, provider truth boundaries or production Rive contract.

## Shared Studio

| Capability | Status | Native target / action |
|---|---|---|
| Authenticated Patsy shell | **ALREADY_NATIVE** | Preserve `FinalMainActivity` and navigation contracts. |
| Primary HOME · THyNK · CAMERA · PATSY DMS · PROFILE navigation | **ALREADY_NATIVE** | Preserve `FinalPrimaryNavigationBar`. |
| Shared editor state/tool catalogue | **ALREADY_NATIVE** | Extend `StudioEditorState.kt` and `StudioToolCatalog.kt`. |
| Project-title/workspace affordances | **PORT_FROM_UPLOAD** | Port useful workspace behavior into Compose. |
| Browser localStorage autosave/version history | **REPLACE_PLACEHOLDER** | Replace with account-scoped native/Supabase project persistence. |

## Design & Templates

| Capability | Status | Native target / action |
|---|---|---|
| Canvas transforms x/y/w/h/rotation/opacity | **PORT_FROM_UPLOAD** | Add to deterministic native editor state and tests. |
| Layer hide/lock/reorder | **PORT_FROM_UPLOAD** | Port to shared layer model/UI. |
| Rulers & Guides | **PORT_FROM_UPLOAD** | Native editor tool. |
| Align & Distribute | **PORT_FROM_UPLOAD** | Native editor tool. |
| Text, Elements, Stickers, Draw, Frames, Background | **PORT_FROM_UPLOAD** | Expand `StudioToolCatalog` and focused Compose panels. |
| Template catalogue behavior | **PORT_FROM_UPLOAD** | Reuse donor catalogue structure; production assets still require verification. |
| Manifest-only/template-placeholder entries | **REPLACE_PLACEHOLDER** | Do not surface until actual bytes, provenance/license, checksum and supersession are verified. |

## Photo & Image

| Capability | Status | Native target / action |
|---|---|---|
| Android image import | **ALREADY_NATIVE** | Reuse current native import path. |
| Crop/adjust/filter/transform concepts | **PORT_FROM_UPLOAD** | Share the Design canvas/render/export core. |
| CSS/browser-only filters | **REPLACE_PLACEHOLDER** | Native export must actually contain the applied edit. |

## Video & Camera

| Capability | Status | Native target / action |
|---|---|---|
| Photo/video capture + image/video import + FileProvider | **ALREADY_NATIVE** | Preserve `NativeCameraHub`/`CameraHubContract`. |
| Media3 single-clip player | **ALREADY_NATIVE** | Extend `StudioVideoPlayer` rather than replacing it. |
| Timeline UX | **PORT_FROM_UPLOAD** | Add a focused native timeline state/model. |
| Filters/effects/transitions/text animations/overlays | **PORT_FROM_UPLOAD** | Port models/controls incrementally onto native media processing. |
| Browser `captureStream`/`MediaRecorder` timed export | **REPLACE_PLACEHOLDER** | Replace with a verified Android export path before claiming export success. |
| Physical external-camera return validation | **GENUINELY_MISSING** as verification | Requires a physical Android device; do not claim complete from CI alone. |

## Documents / Publication

| Capability | Status | Native target / action |
|---|---|---|
| Multi-page page model | **PORT_FROM_UPLOAD** | Focused native publication model. |
| Add/duplicate/delete/reorder pages | **PORT_FROM_UPLOAD** | Real state commands, not demo alerts. |
| Single/facing spread views | **PORT_FROM_UPLOAD** | Native page/spread UI. |
| Linked text/image-frame/layout concepts | **PORT_FROM_UPLOAD** | Incremental page-model slices. |
| Print-layout concepts, columns/gutters/guides | **PORT_FROM_UPLOAD** | Add only as independently testable native capabilities. |
| `alert(...)` actions and visual-only mock page cards | **REPLACE_PLACEHOLDER** | Replace with real native commands/state. |
| Verified Android PDF/document export | **GENUINELY_MISSING** | Do not claim until real bytes are written from the native editable model. |

## Presentations

| Capability | Status | Native target / action |
|---|---|---|
| Presentation templates | **PORT_FROM_UPLOAD** | Surface verified templates through shared page/canvas architecture. |
| Multi-page presentation workflow | **PORT_FROM_UPLOAD** | Reuse the shared native page model rather than a separate runtime. |

## THyNK Music

| Capability | Status | Native target / action |
|---|---|---|
| Ten stable route IDs | **ALREADY_NATIVE** | Keep all ten IDs exactly unchanged. |
| Existing native Music screen foundation | **ALREADY_NATIVE** | Refine rather than replace. |
| Player controls | **PORT_FROM_UPLOAD** | Native focused Music component/state. |
| Visualizer behavior | **PORT_FROM_UPLOAD** | Drive from real imported audio data only. |
| 10-band EQ + presets UI/model | **PORT_FROM_UPLOAD** | Port natively; real DSP remains truthful to actual implementation. |
| 8-channel / 16-step drum workflow | **PORT_FROM_UPLOAD** | Port sequencer state/UI in a focused slice. |
| Piano roll | **PORT_FROM_UPLOAD** | Port grid/note model and controls. |
| Sampler trim/fade/normalize/reverse workflow | **PORT_FROM_UPLOAD** | Port only operations backed by real audio buffers/data. |
| Track/layer vol-pan-mute-solo | **PORT_FROM_UPLOAD** | Native track state. |
| Reverb/Delay/Distortion/Compressor/Limiter/Filter/Chorus/Bitcrusher controls | **PORT_FROM_UPLOAD** | Port controls/model; do not claim DSP if the processing engine is absent. |
| Random/placeholder waveforms | **REPLACE_PLACEHOLDER** | Never fabricate waveform data. |
| WebAudio oscillator/demo audio stand-ins | **REPLACE_PLACEHOLDER** | Use real imported/generated audio only in production. |
| AI music generation | **GENUINELY_MISSING / NOT_CONFIGURED** | Remains provider-gated. |
| Lyrics/vocal provider processing | **GENUINELY_MISSING / NOT_CONFIGURED** | Remains provider-gated. |
| Mastering provider pipeline | **GENUINELY_MISSING / NOT_CONFIGURED** | Remains provider-gated. |
| Real WAV/MP3/OGG/FLAC/STEMS export | **GENUINELY_MISSING** until encoder exists | Format selection is not export success. |

## Library / Project Persistence

| Capability | Status | Native target / action |
|---|---|---|
| Project list/library UX | **PORT_FROM_UPLOAD** | Port onto `CreationStudioCoordinator`/native storage contracts. |
| Autosave/draft restore behavior | **PORT_FROM_UPLOAD** | Account-scoped native/Supabase persistence. |
| Version-history workflow | **PORT_FROM_UPLOAD** | Implement only when a durable native model exists. |
| Express/Prisma/Spring donor persistence | **REPLACE_PLACEHOLDER** | Do not create a second backend; Supabase remains authoritative. |

## AI / Provider Boundaries

| Capability | Status | Native target / action |
|---|---|---|
| Existing provider service boundaries | **ALREADY_NATIVE** | Preserve current services and truthful UI state. |
| AI image/video/music output without configured provider | **GENUINELY_MISSING / NOT_CONFIGURED** | Never fake success/progress/output. |

## Patsy / Rive

| Capability | Status | Native target / action |
|---|---|---|
| Rive host/runtime adapter contracts | **ALREADY_NATIVE** | Preserve `PatsyRiveHost.kt` and `PatsyRiveRuntimeAdapter.kt`. |
| Named donor `.riv` assets | **REPLACE_PLACEHOLDER as evidence** | A filename/spec is not proof the binary exists or is production-ready. |
| Final authored production Patsy `.riv` + device validation | **GENUINELY_MISSING** | Required before claiming final walking/pointing/lip-sync/ear-tail/gesture behavior. |

## New execution queue

The old assumption that Codex must first rediscover wrapper-only donor material is superseded. The correct order is now:

1. Freeze this reconciliation/source truth.
2. Design & Templates native port on the shared editor core.
3. Photo/Image shared-canvas extensions.
4. Video timeline/effects extensions on existing Media3/Camera.
5. Documents/Publication/Presentations page-model slices.
6. THyNK Music richer tools while preserving all ten route IDs.
7. Library/autosave/project persistence using native/Supabase contracts.
8. Profile/account and remaining production slices.
9. Consolidated Android CI and physical-device QA.
10. Production Patsy Rive only after the actual authored `.riv` is verified.

Asset verification remains a fail-closed gate for real template/media files, but it must not block porting non-asset editor behavior already demonstrated by the uploaded Studio.

# THyNK Drive donor sync — 2026-09-02

## Purpose

Record the 2 September Google Drive donor intake that is being carried into the native THyNK-IN! Android application without replacing the Kotlin + Jetpack Compose, Camera, Supabase/auth/security, persistence, Media3, or Patsy/Rive foundations.

## Drive sources reviewed

| Drive file | Drive file id | Native disposition |
| --- | --- | --- |
| `THYNK_EVERY_BUTTON_WORKS (1).html` | `1fZn4Covc2SFjLuVNQKlE_K9LaU-f-biu` | THyNK Music interaction donor: arrangement/mixer/EQ/beat controls. Browser runtime is not production Android runtime. |
| `THYNK_FULLSTACK_CODE.txt` | `1AQ7_vyjf0q2WFx8T5mXoATMe3x6GcBLo` | Backend donor/reference. Existing native project/autosave services remain authoritative. Stem separation and export routes in this donor are mock/TODO only. |
| `thynk_it_pipeline_neon.html` | `14O4ekWjf9KlOHPPUrkvDpemJOMG2m1_6` | Native THyNK-IT Fashion pipeline contract and visible Fashion & Textiles hub category. |
| `THYNK_MUSIC_FULL_APP.html` | `1-68My9MQmLuykAfClenu4r4pOrLPbhK3` | THyNK Music Studio Pro interaction/layout donor for arrangement, mixer, DJ, effects, EQ, vocals, beats, piano, AI and master workspaces. |

No newer Patsy/Rive rig file was found in the Drive scan. Existing Patsy/Rive source and ABI contracts remain unchanged by this sync.

## Native Fashion intake

The Drive Fashion donor is represented natively as five ordered stages:

1. `fashion-pattern-cad` — Pattern CAD
2. `fashion-sketch-3d` — 3D Sketch
3. `fashion-layer-mixer` — Layer Mixer
4. `fashion-materials-lab` — Materials Lab
5. `fashion-production` — Production

The stages are exposed in THyNK-IT through the visible `FASHION & TEXTILES` category and are also attached to the `fashion-collection` adaptive workspace configuration.

### Capability contract

Pattern CAD preserves pattern slices, strict CAD grid, seam allowance, notches, grain, scissors cut, stitch linking, darts and pleats.

3D Sketch preserves brush opacity, X/Y/radial symmetry, vector nodes and conversion to a pattern block.

Layer Mixer preserves visibility, lock, opacity, tension diagnostics, live hex and master opacity concepts.

Materials Lab preserves fabric presets, GSM weight, drape, scale, custom hex and simulation preview concepts.

Production preserves XS–XL grading, validation, hidden-layer export, multi-page PDF, tech-pack and Patsy material-search concepts. Provider-backed/search/export operations must remain truthful until a real implementation is connected.

### Exact grading table

| Size | Bust cm | Waist cm | Hip cm | Ease mm | Seam allowance cm |
| --- | ---: | ---: | ---: | ---: | ---: |
| XS | 78 | 60 | 86 | 12 | 1.0 |
| S | 82 | 64 | 90 | 14 | 1.0 |
| M | 86 | 68 | 94 | 16 | 1.2 |
| L | 92 | 74 | 100 | 18 | 1.2 |
| XL | 98 | 80 | 106 | 20 | 1.5 |

## Native THyNK Music reconciliation

The existing native THyNK Music pages/workspaces remain the production destination. The Drive donors contribute interaction and product ideas rather than a WebView or Next.js replacement.

Mapped donor capabilities include arrangement drag, mixer faders, three-band EQ, beat pads, DJ decks and auto-tuner controls. Existing `StudioProjectService` and `StudioPersistenceService` remain the project/persistence source of truth.

The donor `stem-separation` endpoint is explicitly classified `DONOR_MOCK_ONLY` until a verified provider is connected. The donor `mixdown-export` endpoint is explicitly classified `DONOR_MOCK_ONLY` until a verified renderer/export service is connected.

## Protected boundaries

This sync does not intentionally alter:

- native Camera capture/handoff
- Supabase auth/account bootstrap
- Owner/security policies
- under-16 protection boundaries
- Studio project/autosave persistence authority
- Media3 video foundation
- Patsy animation/controller/Rive ABI
- one-Patsy-on-screen rule

## Acceptance checks

- Five Fashion stages exist in the native contract in exact order.
- Exact XS–XL grading values are covered by tests.
- `FASHION & TEXTILES` is visible in the THyNK-IT hub and exposes the five stages.
- `fashion-collection` carries the five stage shortcut IDs.
- Music donor source ids and readiness states are covered by tests.
- Mock donor stem/export endpoints cannot be treated as native complete.
- Full unit/debug/release CI must pass before this sync is considered verified.

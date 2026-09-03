# Codex Rive Next Slice — CANONICAL PRE-RIV GUIDANCE

Codex must preserve the existing core Kotlin architecture without ABI redesign:
- `PatsyRigContractV1.kt`
- `PatsyRigRuntimePort.kt`
- `PatsyRiveHost.kt`
- `PatsyRiveRuntimeAdapter.kt`

## Permitted pre-RIV work

1. Add/strengthen unit tests for `PatsyRigCoordinator` covering complete pose-to-property mapping, V1 enum mapping, clamping, reduced-motion normalization, action-sequence increments, and blink-sequence increments.
2. Improve debug diagnostics for exact missing artboard/state-machine/View Model/instance/property contract items.
3. Verify pending-state behavior while the Rive asset is loading: retain only the newest value per property and flush after a validated View Model attaches.
4. Verify missing/invalid `.riv` keeps the transparent safe fallback active and never reports false Ready state.
5. Do not introduce numeric `motion/mode` IDs, action IDs in `motion/action_sequence`, unsupported V1 actions, HTML/JS/React/WebView animation, GIF/sprite/static pose swaps, or claims that production Rive exists before an actual asset is validated.
6. Use only the locked 13 motion-reference videos documented in `PATSY_RIVE_APPROVED_VIDEO_SOURCES_2026-09-01.md` and `PATSY_MOTION_REFERENCE_MAP_CANONICAL_2026-09-01.json`.

## Current real blocker

Author and deliver a genuine production asset at:
`app/src/main/res/raw/patsy_assistant.riv`

It must match the exact V1 ABI and pass the canonical acceptance gate before replacing the fallback.

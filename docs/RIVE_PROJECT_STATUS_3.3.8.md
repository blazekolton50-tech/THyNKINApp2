# Patsy Rive project status — lineage 3.3.8

Status date: 29 August 2026

## Cloud project

- Workspace: `Patsy1 Studio`
- File: `patsy1_3.3.8`
- Editor: <https://editor.rive.app/file/patsy1_338/2541372>
- Artboard: `PatsyAssistant` (1000 x 1000, transparent)
- Default state machine: `PatsyAssistantMachine`
- View Model: `PatsyAssistantVM`
- Initial durable timeline: `idle`

The editor project is saved in the signed-in Rive account. Access to the URL may require that account.

## Current authored-asset state

The stable project structure and Android-facing names exist, but the production vector character layers have not yet been generated or rigged. Rive Agent reached its account usage limit before it produced any character layers. No production `.riv` export is therefore present or claimed.

The Android app must continue to show its generated transparent fallback until a future Rive export passes the validation and motion acceptance gates in `PATSY_RIVE_RIG_CONTRACT_3.3.8.md`.

## Locked continuation rules

- Use the bundled real Patsy photos for identity reference only; do not upload, embed, export, or render them in the app.
- Use generated Patsy artwork as the authoring source.
- Produce one continuously animated rig, never a square image, photo, GIF, sprite sheet, or still-pose swap.
- Preserve Patsy's longer dark straight low-hanging ear hair, grey/white coat, white chest and muzzle, dark eyes, black nose, slim proportions, and curved dark fluffy tail.
- Rig left and right ears independently with separate deformable chains and secondary motion.
- Preserve the exact artboard, state-machine, View Model, instance, enum, and property-path ABI documented in `PATSY_RIVE_RIG_CONTRACT_3.3.8.md`.
- Do not remove the generated Android fallback until the exported rig validates and passes device smoke testing.


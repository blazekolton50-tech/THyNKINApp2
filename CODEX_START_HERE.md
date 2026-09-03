# CODEX — START HERE: THyNK-IN — 2026-09-01

Repository: `blazekolton50-tech/PatsyApp` (legacy repository slug; product name is THyNK-IN)

Working branch:
`chatgpt/codex-ready-2026-09-01`

Keep PR #41 **Draft and unmerged**. Do not merge to `main` without explicit owner approval and physical-device QA.

## Canonical naming — binding

- App/global product brand: **THyNK-IN**.
- Do not call the product **Patsy App** in current UI, docs, handoffs, PR wording or new code-facing copy.
- **Patsy** is the assistant/character name. Keep Patsy-specific animation, Rive, personality, PawMoji, companion and reference-asset names as Patsy.
- Older filenames containing `PATSY`, `PatsyApp` or `patsy1` are legacy filenames only; do not interpret them as the current product brand.
- Do not rename the existing `com.patsy.app` namespace/application identity as part of ordinary branding work. Treat that as a separate migration requiring install/auth/deep-link/backend verification.

## Read these first — binding order

1. `docs/superpowers/specs/2026-09-01-thynk-full-studio-reconciliation-design.md`
2. `docs/superpowers/plans/2026-09-01-thynk-full-studio-reconciliation.md`
3. `docs/codex/THYNK_FULL_STUDIO_CAPABILITY_MATRIX_2026-09-01.md`
4. `docs/codex/THYNK_FULL_STUDIO_CAPABILITY_MATRIX_2026-09-01.json`
5. `docs/codex/PATSY_CURRENT_BUILD_STATUS_2026-09-01.md`
6. `docs/codex/PATSY_SOURCE_INTAKE_2026-09-01.md`
7. `docs/codex/CODEX_NEXT_INSTRUCTION.txt`
8. `docs/PATSY_DESIGN_PRESERVATION_MASTER_2026-08-31.md`
9. `docs/NAVIGATION_LOCK_2026-08-31.md`

The older 2026-08-31 status/handoff files are historical checkpoints. The newly uploaded full THyNK Studio reconciliation above supersedes the older assumption that most Studio material is wrapper-only.

## Source truth

The uploaded Library contains substantial donor/editor implementations, including full THyNK Studio builds, Design/editor tools, Publication/Document workflows, Video editor donor behavior, THyNK Music Lab, templates and 1110-item setup/material descriptions.

This does **not** mean the React/Tailwind/WebAudio runtime becomes production architecture. Production THyNK-IN remains native Kotlin/Jetpack Compose with the existing Supabase, auth/security, Camera, Media3, navigation and provider truth boundaries.

Every donor capability must follow the four-status matrix:
- `ALREADY_NATIVE`
- `PORT_FROM_UPLOAD`
- `REPLACE_PLACEHOLDER`
- `GENUINELY_MISSING`

Do not rebuild `ALREADY_NATIVE` work. Do not copy `REPLACE_PLACEHOLDER` demo behavior as production. Do not claim `GENUINELY_MISSING` work is complete.

## Already native — do not redo

- Kotlin/Jetpack Compose production shell
- FINAL Login / Set Password / Home baseline
- Supabase auth/session/account-bootstrap/Owner foundations
- age/protected route gates
- one authenticated outer primary navigation bar
- ten THyNK categories
- ten stable THyNK Music page IDs
- `StudioEditorState.kt`
- `StudioToolCatalog.kt`
- Media3 1.8.1 single-clip video foundation
- native Camera photo/video capture
- native image/video import
- private FileProvider capture URIs
- video handoff to THyNK Media3 editor
- Rive host/runtime contracts, but **not** the final authored production Patsy `.riv`

Do not repeat Media3, global-navigation, Camera-foundation, category-routing or auth/security jobs.

## First coding slice after this documentation reconciliation

Start with **Design & Templates native port on the existing shared editor core**, using the capability matrix.

Port donor behavior such as:
- deterministic canvas transforms
- layers hide/lock/reorder
- rulers/guides
- align/distribute
- text/elements/stickers/draw/frames/background tool coverage
- template catalogue workflow

Do not create a second editor or embed the donor React runtime.

Asset/template files still fail closed until real bytes, origin/license, checksum, format and supersession status are verified. Asset verification is a gate inside the slice that uses the asset; it must not block porting non-asset editor behavior.

After Design, follow the queue in `docs/codex/CODEX_NEXT_INSTRUCTION.txt`.

## Locked visual rules

- black/charcoal primary UI
- white/light-grey main text
- white primary buttons/controls with restrained rainbow/neon treatment
- THyNK-IN is the app/global brand; use the approved current THyNK-IN/global brand asset where global app branding is required
- use matching approved THyNK destination logos on their destination pages
- one realistic main Patsy companion per page
- cartoon Patsy only for PawMojis/stickers/reactions
- never fake production `.riv`

## Locked main navigation

Semantic routes:
`HOME · THyNK · CAMERA · PATSY DMS · PROFILE`

Visible bar:
`Home · [coloured THyNK only] · [large + only] · PDMs · Profile`

No second white THyNK subtitle. No CAMERA caption under the centre +. Schedule/Calendar stay secondary.

## Top-right account menu

`Account · About · Profile · Settings · Remember Me`

## Production truth boundaries

Do not introduce:
- WebView/React/Vite/Next as the production THyNK runtime
- a second Express/Prisma/Spring backend replacing Supabase
- browser `localStorage` as production persistence
- fake export URLs or alert-only production actions
- random/placeholder waveforms presented as real audio
- simulated AI generation/mastering/provider success
- weakened auth/RLS/Owner/age gates
- fake production Rive using GIFs, sprites, PNG pose swaps or bobbing static images

Provider-backed work stays `NOT_CONFIGURED` until genuinely configured and verified.

## Verification

Every coding slice must show RED-before-GREEN evidence, targeted/full tests, debug/release build results, exact final GitHub Actions run/head SHA and remaining physical-device/provider/Rive limitations.

Stop on failure. Keep the PR Draft. Do not merge.

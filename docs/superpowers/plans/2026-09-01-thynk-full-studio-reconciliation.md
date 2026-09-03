# THyNK Full Studio Reconciliation Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Turn the newly uploaded THyNK Studio source into a binding capability map and execution queue so Codex extends the verified native Patsy Android app instead of rebuilding or embedding the donor web app.

**Architecture:** The production shell remains Kotlin/Jetpack Compose with the existing Supabase, auth/security, Camera, Media3, navigation and provider boundaries. Uploaded React/Tailwind/WebAudio/Canvas/editor material is a donor implementation: useful behaviors are ported into the existing native editor core, demo/browser placeholders are replaced, and genuinely unavailable capabilities remain explicit.

**Tech Stack:** Kotlin, Jetpack Compose, Android Media3 1.8.1, Android Camera/FileProvider foundations, Supabase, existing Studio editor contracts; donor references include React 18, Tailwind, Canvas API, Web Audio and TypeScript.

**Spec:** `docs/superpowers/specs/2026-09-01-thynk-full-studio-reconciliation-design.md`

## Global Constraints

- Work only on `chatgpt/codex-ready-2026-09-01`; keep PR #41 Draft and unmerged.
- Native Kotlin/Compose remains production authority. Do not embed React/Vite/Next/WebView as the production THyNK runtime.
- Preserve `HOME · THyNK · CAMERA · PATSY DMS · PROFILE` and the locked visible bottom bar.
- Preserve auth, RLS, Owner authorization and under-16/protected boundaries; never weaken them for editor work.
- Preserve current Camera/FileProvider and Media3 foundations; do not rebuild completed jobs.
- All donor capabilities use exactly one status: `ALREADY_NATIVE`, `PORT_FROM_UPLOAD`, `REPLACE_PLACEHOLDER`, or `GENUINELY_MISSING`.
- Provider-backed features remain `NOT_CONFIGURED` until a real provider pipeline is configured and tested.
- A named `.riv` in donor documentation is not proof of a production binary. Never fake production Patsy/Rive behavior.
- Do not surface donor assets as production merely because a manifest names them; verify bytes, origin/license, format, checksum and supersession first.

---

### Task 1: Freeze the Full Studio Capability Matrix

**Files:**
- Create: `docs/codex/THYNK_FULL_STUDIO_CAPABILITY_MATRIX_2026-09-01.json`
- Create: `docs/codex/THYNK_FULL_STUDIO_CAPABILITY_MATRIX_2026-09-01.md`

**Interfaces:**
- Consumes: uploaded donor evidence named in the approved spec plus the current PR #41 native tree.
- Produces: one stable capability ID per feature, one of the four reconciliation statuses, native target paths, donor evidence, truth constraints, and the next implementation slice.

- [ ] **Step 1: Add the matrix schema and records**

Required JSON top-level keys:

```json
{
  "version": "2026-09-01",
  "branch": "chatgpt/codex-ready-2026-09-01",
  "statuses": ["ALREADY_NATIVE", "PORT_FROM_UPLOAD", "REPLACE_PLACEHOLDER", "GENUINELY_MISSING"],
  "capabilities": []
}
```

Every capability record must include:

```json
{
  "id": "design.layers.reorder",
  "area": "Design & Templates",
  "status": "PORT_FROM_UPLOAD",
  "nativeTargets": ["app/src/main/java/com/patsy/app/studio/StudioEditorState.kt"],
  "donorEvidence": ["Thynk-Studio-Pro-Complete", "FULL-INVENTORY.pdf"],
  "truthBoundary": "Port behavior natively; do not embed donor runtime.",
  "nextSlice": "design-canvas"
}
```

- [ ] **Step 2: Cover every major area**

The matrix must contain records for:
- shared Studio/navigation/editor core
- Design & Templates
- Photo & Image
- Video & Camera
- Documents/Publication
- Presentations
- Collage/shared canvas behavior
- THyNK Music
- Library/project autosave/persistence
- AI/provider boundaries
- donor assets/templates
- Rive/Patsy animation boundary

- [ ] **Step 3: Validate the JSON**

Run:

```bash
python -m json.tool docs/codex/THYNK_FULL_STUDIO_CAPABILITY_MATRIX_2026-09-01.json >/dev/null
```

Expected: exit code `0`.

- [ ] **Step 4: Verify all four statuses are represented**

Run:

```bash
for s in ALREADY_NATIVE PORT_FROM_UPLOAD REPLACE_PLACEHOLDER GENUINELY_MISSING; do
  grep -q "\"status\": \"$s\"" docs/codex/THYNK_FULL_STUDIO_CAPABILITY_MATRIX_2026-09-01.json || exit 1
done
```

Expected: exit code `0`.

- [ ] **Step 5: Commit**

```bash
git add docs/codex/THYNK_FULL_STUDIO_CAPABILITY_MATRIX_2026-09-01.json docs/codex/THYNK_FULL_STUDIO_CAPABILITY_MATRIX_2026-09-01.md
git commit -m "docs: map uploaded THyNK Studio capabilities"
```

### Task 2: Correct the 2026-09-01 Source Intake

**Files:**
- Modify: `docs/codex/PATSY_SOURCE_INTAKE_2026-09-01.md`

**Interfaces:**
- Consumes: Task 1 capability matrix.
- Produces: an intake record that distinguishes the newly inspected full Studio source from older wrapper-only Drive/archive references.

- [ ] **Step 1: Add a new uploaded Full Studio section**

Record these verified donor/source names:
- `Thynk-Full-Ecosystem-Studio(1).html`
- `Thynk-Full-Studio-Working(1).html`
- `Thynk-Studio-Pro-Complete` builds
- `FULL-INVENTORY.pdf`
- `THyNK-MUSIC-EVERYTHING.pdf`
- `THYNK-Music-Lab.tsx`
- `Templates.tsx`
- Gemini/React TypeScript source containing publication and video editor modes
- 1110-item setup/material descriptions

State explicitly that these are **substantial donor implementations**, not wrapper titles, while still not overriding native Android architecture.

- [ ] **Step 2: Record demonstrated capabilities without promoting demo behavior**

Document Design canvas/layers/tools, publication pages/spreads, video timeline/effects concepts, Music Lab controls, templates and local autosave/version-history behavior. Explicitly flag browser `localStorage`, `alert(...)`, fake export URLs, random/placeholder waveform/media, WebAudio stand-ins and Express/Prisma persistence as `REPLACE_PLACEHOLDER` rather than production-ready.

- [ ] **Step 3: Link the matrix**

Add:

```text
docs/codex/THYNK_FULL_STUDIO_CAPABILITY_MATRIX_2026-09-01.md
docs/codex/THYNK_FULL_STUDIO_CAPABILITY_MATRIX_2026-09-01.json
```

- [ ] **Step 4: Verify stale wording is gone**

Search for language implying all Drive/AI Studio/React Studio material is only wrapper-level. Keep wrapper-only claims only for files that truly remain wrapper-only.

- [ ] **Step 5: Commit**

```bash
git add docs/codex/PATSY_SOURCE_INTAKE_2026-09-01.md
git commit -m "docs: recognize uploaded full THyNK Studio donor"
```

### Task 3: Correct the Current Build Status

**Files:**
- Modify: `docs/codex/PATSY_CURRENT_BUILD_STATUS_2026-09-01.md`

**Interfaces:**
- Consumes: capability matrix and corrected source intake.
- Produces: current truth for what is native, what is donor-portable, what is demo-only and what is genuinely missing.

- [ ] **Step 1: Add a Full Studio reconciliation section**

State that the uploaded source substantially covers Design, Publication/Documents, Video donor behavior, Music Lab, templates and shared editor workflows.

- [ ] **Step 2: Preserve native completed foundations**

Keep these marked complete and do not schedule them again:
- `StudioEditorState.kt`
- `StudioToolCatalog.kt`
- Media3 single-clip player foundation
- native Camera capture/import/FileProvider
- global authenticated navigation
- ten THyNK categories
- ten stable Music route IDs
- auth/security/Supabase authority

- [ ] **Step 3: Replace the old execution queue**

New high-level order:
1. capability matrix/source freeze
2. Design & Templates native port on shared editor core
3. Photo/Image shared-canvas extensions
4. Video timeline/effects extensions on Media3/Camera
5. Documents/Publication/Presentations page-model slices
6. THyNK Music richer native tools
7. Library/autosave/project persistence on native/Supabase contracts
8. Profile/account and remaining product slices
9. consolidated CI + physical-device QA
10. production Patsy Rive only with verified authored `.riv`

- [ ] **Step 4: Verify no claim upgrades demo features to production**

No text may claim real audio export, PDF export, AI generation, mastering, stems, final video export or Rive completion unless real native bytes/provider/device evidence exists.

- [ ] **Step 5: Commit**

```bash
git add docs/codex/PATSY_CURRENT_BUILD_STATUS_2026-09-01.md
git commit -m "docs: align build status with full THyNK Studio upload"
```

### Task 4: Make the Codex Entry Points Binding and Non-Contradictory

**Files:**
- Modify: `CODEX_START_HERE.md`
- Modify: `docs/codex/CODEX_NEXT_INSTRUCTION.txt`

**Interfaces:**
- Consumes: Tasks 1-3.
- Produces: one unambiguous execution start for Codex.

- [ ] **Step 1: Put the approved design, plan and matrix at the top of the read order**

Required order before old donor plans:

```text
docs/superpowers/specs/2026-09-01-thynk-full-studio-reconciliation-design.md
docs/superpowers/plans/2026-09-01-thynk-full-studio-reconciliation.md
docs/codex/THYNK_FULL_STUDIO_CAPABILITY_MATRIX_2026-09-01.md
docs/codex/PATSY_CURRENT_BUILD_STATUS_2026-09-01.md
docs/codex/PATSY_SOURCE_INTAKE_2026-09-01.md
```

- [ ] **Step 2: Remove the stale first-job instruction**

Do not tell Codex to start by rediscovering donor wrappers. The first coding slice after this documentation reconciliation is **Design & Templates native port using the matrix**, because the newly uploaded source already supplies the broader donor editor behavior.

- [ ] **Step 3: Keep asset verification as a gate, not the whole first project**

Asset/template records still require verification before becoming production resources. That verification happens inside the category slice that needs them and does not block porting non-asset editor behavior such as deterministic layer state, rulers/guides or page models.

- [ ] **Step 4: Verify locked rules remain verbatim in substance**

Confirm the handoff still prohibits:
- WebView/React replacement of THyNK
- fake provider success
- duplicate primary nav
- weakened auth/RLS/Owner/age gates
- fake production Rive
- automatic merge

- [ ] **Step 5: Commit**

```bash
git add CODEX_START_HERE.md docs/codex/CODEX_NEXT_INSTRUCTION.txt
git commit -m "docs: redirect Codex to full Studio native-port queue"
```

### Task 5: Split Implementation Into Independently Reviewable Native Plans

**Files:**
- Modify only when superseded: existing relevant plans under `docs/superpowers/plans/`
- Create later, one per subsystem, before touching production code.

**Interfaces:**
- Consumes: capability IDs and `nextSlice` values from Task 1.
- Produces: focused TDD plans with exact Kotlin/Compose files/tests for each subsystem.

- [ ] **Step 1: Reconcile Design plan first**

Update or supersede `docs/superpowers/plans/2026-09-01-thynk-design-templates-integration.md` so it ports the uploaded Design tools onto `StudioEditorState`/`StudioToolCatalog` rather than inventing another editor.

- [ ] **Step 2: Create independent plans in this order**

1. Photo/Image shared-canvas extensions
2. Video timeline/effects on Media3
3. Documents/Publication page model
4. Presentations adapters/templates
5. THyNK Music fuller tools
6. Library/autosave/project persistence

Each plan must use RED-before-GREEN tests and have a separately reviewable result.

- [ ] **Step 3: Do not combine independent subsystems into one giant implementation patch**

A reviewer must be able to approve Design while rejecting Publication or Music without entangling the code.

### Task 6: PR #41 Handoff Verification

**Files:**
- PR #41 description/comment only; no merge.

**Interfaces:**
- Consumes: all prior tasks.
- Produces: visible PR-level record of the changed source truth.

- [ ] **Step 1: Report exact final branch head**

Run:

```bash
git rev-parse HEAD
```

- [ ] **Step 2: Verify the reconciliation files exist**

Run:

```bash
test -f docs/superpowers/specs/2026-09-01-thynk-full-studio-reconciliation-design.md
test -f docs/superpowers/plans/2026-09-01-thynk-full-studio-reconciliation.md
test -f docs/codex/THYNK_FULL_STUDIO_CAPABILITY_MATRIX_2026-09-01.json
test -f docs/codex/THYNK_FULL_STUDIO_CAPABILITY_MATRIX_2026-09-01.md
```

Expected: exit code `0`.

- [ ] **Step 3: Confirm PR #41 remains Draft and unmerged**

Do not change Draft state. Do not merge.

- [ ] **Step 4: Add a concise PR conversation update**

The comment must say that the full uploaded THyNK Studio has now been recognized as a substantial donor implementation, the old wrapper-only execution assumption has been superseded, and native implementation must follow the four-status matrix.

## Self-review

Before code execution begins:
- every capability in the matrix has exactly one valid status
- every `ALREADY_NATIVE` record names a current native target/evidence
- every `PORT_FROM_UPLOAD` record names a native integration target or future focused plan
- every `REPLACE_PLACEHOLDER` record explains what production replacement is required
- every `GENUINELY_MISSING` record stays explicitly unavailable/`NOT_CONFIGURED` when provider-backed
- no plan introduces WebView/React runtime, a second backend, fake export/provider results or fake Rive
- the four Codex handoff documents agree on the same execution order

# THyNK-IN

Current consolidated THyNK-IN Android Studio master, based on the 3.3.8 lineage and latest owner-approved updates.

**Start here:** `CODEX_START_HERE.md`, then follow the current binding handoff/spec files on the active draft branch. Older files whose names contain `PATSY`, `PatsyApp`, or `patsy1` are legacy filenames and do not define the current product name.

## Canonical naming

- App/global product brand: **THyNK-IN**.
- Do not call the product **Patsy App**.
- **Patsy** remains the assistant/character name and stays valid for Patsy-specific animation, personality, PawMoji, reference-asset and companion features.
- Existing technical identifiers such as the GitHub repository slug and `com.patsy.app` package/namespace are legacy implementation identifiers until a deliberate migration is verified; they are not user-facing brand names.

Important: real Patsy photographs in `app/src/main/assets/references/latest/` are reference-only. Patsy assistant assets remain Patsy-specific and must not be renamed as THyNK-IN character assets.

Build configuration intentionally aligns Java and Kotlin to JVM 17 to address the observed Android Studio mismatch.

This is a development source project, not a claim that production backend/provider services are deployed.

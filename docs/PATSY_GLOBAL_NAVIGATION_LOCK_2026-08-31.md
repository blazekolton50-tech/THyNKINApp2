# PATSY APP — GLOBAL NAVIGATION LOCK

**Status:** MASTER / APPROVED / LATEST SAVE MAIN APP OVERRIDE  
**Date:** 31 August 2026  
**Visual source:** `THyNK_Bottom_Nav_EXACT_2x.png`

## Canonical navigation visual

This exact bottom navigation system must be present on **ALL PAGES** of the Patsy App.

Visual order:

1. Home
2. THyNK coloured mark
3. large centre +
4. PDMs
5. Profile

## Exact visual correction

- Remove the extra small white `THyNK` caption underneath the coloured THyNK mark.
- Do not render a second THyNK text line.
- Keep the coloured THyNK mark itself.
- Keep the centre control as the large `+` shown in the approved reference; do not show a secondary `CAMERA` caption beneath it.
- Display `PDMs` visually as shown in the approved reference.
- Preserve the black/charcoal base, rainbow wave/divider, rainbow accent/glow, icon positions and overall proportions of the approved reference.

## Semantic routing remains protected

The underlying route/security contract remains:

`HOME • THyNK • CAMERA • PATSY DMS • PROFILE`

Visual shortening or hidden secondary labels do not rename or weaken the semantic routes.

## Global rule

- One navigation system across the entire application.
- Do not replace it with editor-only navigation.
- Do not hide it on THyNK editors/subpages.
- Do not hide it on Profile, PDMs, Camera/Create, Owner, protected, account, settings, calendar, storage, or other nested app pages.
- Where a destination is unavailable because the user is logged out, under age restrictions, lacks a capability, or fails an authorization check, preserve the navigation visual while keeping the route fail-closed/disabled or returning the appropriate protected state.
- Never bypass authentication/RLS/Owner capability checks just to make a navigation tap work.

Older bottom-navigation variants are preserved as superseded recovery material and must not override this lock.

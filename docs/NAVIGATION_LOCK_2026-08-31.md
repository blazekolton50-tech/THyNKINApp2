# Patsy App — Permanent Navigation Lock — 2026-08-31

**Status: SAVE MAIN APP / LOCK IN / binding visual navigation reference**

## Semantic destinations

`HOME · THyNK · CAMERA · PATSY DMS · PROFILE`

## Visible bottom bar

`Home · [coloured THyNK wordmark only] · [large centre + only] · PDMs · Profile`

## Binding visual rules

- Use the newest user-supplied navigation strip as the visual reference.
- Keep the black/charcoal base.
- Keep the thin rainbow wave/glow along the top edge.
- Home keeps the rainbow-outlined Home icon and visible `Home` label.
- THyNK is shown once as the coloured/rainbow THyNK wordmark.
- **Remove the duplicate small white `THyNK` text beneath it.**
- The centre control is the large white circular `+` with rainbow ring.
- **Do not render a visible `CAMERA` caption beneath the +.** Camera remains the semantic route behind the control.
- The DM item uses the speech-bubble treatment and visible short label `PDMs`.
- The semantic product destination remains `PATSY DMS` / `FinalHomeDestination.PATSY_DMS`.
- Profile keeps the profile-outline icon and visible `Profile` label.
- Active states may use the approved restrained rainbow/pink treatment without redesigning the bar.

## Coverage rule

This is the single permanent primary navigation system throughout the authenticated app.

It must remain visible on:
- Home
- THyNK hub
- every THyNK category/subcategory
- every THyNK editor
- THyNK Music pages/editors
- Camera
- PDMs inbox/chat pages where the shell applies
- Profile
- Owner Profile
- Owner Tools
- authenticated protected/denied pages
- every other authenticated nested page unless a later explicit SAVE MAIN APP instruction overrides this rule

Editor-only navigation must never replace it.

Home must not maintain a separate duplicate primary-bar implementation.

Login / Set Password are pre-auth security entry screens. They must not expose functional authenticated destinations before a valid session exists. If a future visual requirement places the bar there, its destinations must remain non-bypassable through the auth/security gate.

## Implementation authority

Canonical shared Compose component:
`app/src/main/java/com/patsy/app/ui/finaldesign/FinalPrimaryNavigationBar.kt`

Global-shell refactor plan:
`docs/superpowers/plans/2026-08-31-global-navigation-every-authenticated-page.md`

Do not restore older primary-nav variants such as Home/Chat/Create/Social/More, Home/Studio/+/Projects/Profile, duplicate THyNK captions, or centre CAMERA captions.

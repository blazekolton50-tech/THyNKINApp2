# PATSY — FINAL LOGIN / SET PASSWORD / HOME VISUAL LOCK

**SAVE MAIN APP • LOCK IN SAVE • FINAL**

These three screens are an owner-approved visual contract. Engineering must fit around these designs; the designs must not be rebuilt around implementation convenience.

## Final reference images

1. `photo5289738092540000091.jpeg` — LOGIN
2. `photo893734336090202489.jpeg` — SET PASSWORD
3. `photo505578768821429139.jpeg` — HOME

The supplied final screenshots are the visual source of truth for composition, spacing, scale, typography hierarchy, rainbow treatment, charcoal/black surfaces, Patsy placement, and navigation placement.

## Shared lock

- App visual language: near-black charcoal workspace, white text, restrained rainbow neon outline/glow.
- Use the exact approved `patsy_logo_official_white.png`; never redraw or approximate it.
- Login/Set Password top logo presentation: black `#000000` square, 132dp, exact white wordmark centred.
- Directly below: `Hi, I'm Patsy, Your AI Pet Pal.`
- Footer copy only at the bottom: `YOUR AI. YOUR WORKSPACE. YOUR CONTROL.`
- Patsy is the AI entity. Use the existing Rive rig/runtime architecture. Static generated artwork is fallback only until the authored production `.riv` is validated.
- Do not replace Patsy with geometric shapes, generic mascot art, GIFs, sprites, or pose swaps.

## Login — final

Preserve the approved visual layout:

- Patsy wordmark centred above the page.
- Realistic/rigged Patsy at upper left in a waving/engaged state.
- Rainbow-outline speech bubble: `Hi, I'm Patsy, Your AI Pet Pal — let's get you logged in!`
- Large rainbow-outline Login panel.
- Visible fields: Username and Email.
- Owner Profile login setup row.
- Owner profile row with rainbow-framed avatar.
- Visible checkbox text remains `Remember Me` because the FINAL visual shows it.
- **Code meaning of that checkbox is `keepSignedIn` / session persistence only.** It must never be connected to the Patsy saved-content Remember Me feature.
- Large rainbow Login button.
- Forgot Password treatment stays within the final visual language.
- Curved rainbow wave at the bottom.

### Debug test access

Debug builds may support the owner test flow:

- username: `patsytest`
- initial temporary password: `PatsyTest!2026`
- first valid temporary login → FINAL Set Password page
- owner chooses a new password
- only a salted PBKDF2 verifier is stored; plaintext password is never persisted
- temporary credential stops authenticating once the custom verifier exists
- `keepSignedIn` may restore the debug test session
- logout clears session persistence but retains the verifier
- release builds contain no working debug bypass
- debug session must not grant Owner Tools or any server capability

## Set Password — final

Preserve the approved visual layout:

- same top Patsy/logo language as Login
- rainbow speech bubble: `Let's set your password to keep Blaze safe!`
- rainbow-outline main panel
- title: `SET PASSWORD`
- `Set password` field with visibility control
- `Confirm password` field with visibility control
- Owner Profile login setup row
- visible `Remember Me` label, with `keepSignedIn` semantics only
- large rainbow `Set Password & Login` action
- `Back to Login`
- `Forgot Password?`
- curved rainbow wave at bottom

## Home — final

Preserve the approved screen order and composition:

1. centred Patsy wordmark
2. notification bell and three-dot menu at top right
3. large rainbow-outline `Ask Patsy anything...` bar with microphone
4. greeting: `Hey! What can I help with today? 💜`
5. Continue Designs — four project cards plus New Design
6. Today — scheduled posts, reminder, Pet Awareness Day
7. Create Post — status input, Image, Video, Document, Template, More, Tag people, Add hashtag, rainbow POST
8. feed tabs — For You / Following / Questions / Latest
9. social feed preview
10. realistic/rigged Patsy at lower right, positioned so she does not block important controls/content
11. curved rainbow divider above bottom navigation
12. locked primary navigation

### Primary navigation

The semantic contract is exactly:

`HOME • THyNK • CREATE • PATSY DMS • PROFILE`

The centre Create destination uses the large rainbow-ring `+` control from the final Home design.

Schedule remains secondary and must not be added as a permanent primary destination.

## Remember Me separation

There are two different concepts and they must not be conflated:

1. **Login/session persistence** — code name `keepSignedIn`; visible Login/Set Password label currently remains `Remember Me` only to match the final approved visual.
2. **Patsy Remember Me** — separate in-app paw feature for user-approved saved pictures, images, videos, templates, creations, THyNK projects, saved designs and appropriate memories/preferences. Outline paw → approved rainbow/neon remembered state.

No password, credential, auth token meaning, or login state may be attached to the saved-content Remember Me feature.

## Visual QA gate

A screen is not `MATCH` just because it compiles.

For Login, Set Password and Home:

1. build/run on an Android device or emulator
2. capture the rendered screen
3. compare side-by-side to the final approved reference
4. fix visible differences in geometry, spacing, sizing, typography hierarchy, logo position, Patsy position, colour/outline treatment, and navigation before marking `MATCH`

Production `.riv` availability, live provider-backed content, and backend completeness must be reported truthfully and separately from visual completion.

**DO NOT MERGE this visual-lock work to main until the owner explicitly approves the device screenshots.**

# THyNK-IN! Authoritative Source — 2 September 2026

**Status:** OWNER APPROVED / CURRENT SOURCE OF TRUTH

This document supersedes older navigation, branding and studio wording wherever they conflict with it. Historical documents remain for recovery/context only.

## Product identity

- Global app/product name: **THyNK-IN!**
- Patsy is the assistant/character, not the product name.
- Legacy package/repository identifiers such as `PatsyApp`, `patsy1` and `com.patsy.app` may remain where renaming would create technical risk.

## Primary THyNK homebar

Exact left-to-right order:

`THyNK-ME | THyNK Chats | THyNK-IN! | THyNK Music | THyNK-IT`

Routes:
- THyNK-ME → Profile
- THyNK Chats → authenticated DMs
- THyNK-IN! → social/news feed
- THyNK Music → all music and video/media creation/playback/editing
- THyNK-IT → all non-music/non-video creator/editing work

Use the owner-supplied five-logo artwork and matching page logos. Do not recreate the marks as ordinary text when the approved raster is available.

Rainbow active-state lighting may be enabled/disabled by user preference. Disabled means plain white/non-persistent active styling; enabled means the selected destination receives the rainbow active treatment.

## Editing-board navigation

Normal pages, hubs and category/tool screens show the five-logo homebar.

A true editing board hides the homebar and uses:
- Back at top-left → return to the exact category/tool screen that opened the board.
- Rainbow overflow circle at top-right → menu containing Home.
- Home → THyNK-IN! feed.

Do not add another permanent Home control to editing boards. Persist/autosave truthfully before leaving where a connected persistence path exists; never claim a save that did not complete.

## Product ownership

### THyNK Music

Owns the complete music + video/media family, including:
- music player and video player
- music editor and video editor
- DJ Studio
- Auto-Tuner
- recording
- track arrangement
- mixer
- equalizer
- effects
- lyrics/vocals tooling
- mastering
- import/export when real pipelines exist
- Camera/video handoff where appropriate

Music/video provider-backed or export functionality remains `NOT_CONFIGURED` until a real provider/output pipeline is connected and verified.

### THyNK-IT

Owns non-music/non-video creator/productivity worlds such as:
- design/templates
- photo/image editing
- documents/office/business
- publishing/journalism
- fashion/textiles
- social graphics
- branding
- writing/comics
- interiors/architecture/product design
- pottery/ceramics/jewellery/crafts
- education/study
- events/portfolio/planning

The existing shared native creator engine remains the foundation. Do not replace production Kotlin/Compose with React, Tailwind or a WebView.

## Native architecture and safety boundaries

Preserve:
- Kotlin + Jetpack Compose production UI
- Supabase auth/account/Owner/age/security boundaries
- RLS and fail-closed authorization
- native Android Camera/FileProvider
- Media3 playback
- existing editor reducer/state/persistence work

Never fake AI/provider success, DMs/read state, storage success, mastering/vocal processing, waveform data, media/PDF/audio/video export, Owner authority, project counts, or production Rive.

## Patsy animation

Keep one realistic Patsy assistant. The Android Rive coordinator/runtime/host may be used, but the generated transparent fallback remains authoritative until a real authored `patsy_assistant.riv` passes the Rive ABI and device acceptance gates. Never fabricate a `.riv`, sprite swap, GIF or fake production animation.

## Connected backend routes — current target

Android must remain aligned with the deployed Supabase functions and REST/RPC paths used by the app:
- `auth-login`
- `auth-register-start`
- `auth-register-complete`
- `auth-reset-request`
- `account-bootstrap`
- `owner-authorize`
- `create-dm-thread`
- `pdm-inbox`
- `pdm-member-state`
- Studio REST/RPC persistence including `studio_autosave` and `studio_create_project`

Deployed function source must also exist in repository source control before it is treated as reproducible.

## Deep links

Only advertise external Android deep links that are explicitly registered in the manifest and map to a renderable secure shell destination. Internal route IDs may exist without being public deep links. Never authorize a route and then silently fall through to an unrelated screen.

## Source precedence

When sources conflict, use this order:
1. Explicit owner instruction dated 2 September 2026 or later.
2. This document.
3. Current tested native implementation and current Supabase deployment, where they do not conflict with 1–2.
4. 1 September handoff documents.
5. Older August locks/specifications.
6. Google Drive/HTML/React/Tailwind donor material as capability/reference input only.

Older files are not deleted; add superseded notices when they could mislead future workers.

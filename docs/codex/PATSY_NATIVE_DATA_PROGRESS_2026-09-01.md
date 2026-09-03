# Patsy Native Data Progress — 2026-09-01

## Scope

This addendum records production-native data work added on Draft PR #41 after the main current-build status was written. It does not replace physical-device, provider or final-Rive verification gates.

## Profile

Status: `PARTIAL` — authenticated data service implemented; active Compose screen binding still pending.

Native files:
- `app/src/main/java/com/patsy/app/profile/ProfileDataService.kt`
- `app/src/main/java/com/patsy/app/profile/ProfileServiceBindings.kt`
- `app/src/test/java/com/patsy/app/profile/ProfileDataServiceTest.kt`

Implemented:
- encrypted-session-bound access token use
- session/user mismatch fails closed before transport
- authenticated Supabase REST reads for the signed-in user's `profiles` row
- authenticated Supabase REST reads for that user's `projects`
- real project count from returned project rows
- real Recent Projects references from project rows
- display name / username / avatar-path mapping
- Followers / Following / Likes remain unknown rather than using screenshot placeholder values
- Saved Projects remains empty until a real authoritative saved-project relation is defined

Live backend checked:
- `profiles` has owner-only SELECT RLS
- `projects` has owner-only SELECT RLS

Not yet claimed:
- active screen loading from this service
- profile bio persistence (no verified current `profiles.bio` column)
- follower/following/like counts
- Edit Profile write path
- avatar signed-URL/render path
- Saved Projects relation

## Patsy DMs

Status: `PARTIAL` — authenticated read repository implemented; active Compose/realtime/send binding still pending.

Native files:
- `app/src/main/java/com/patsy/app/dms/DmDataService.kt`
- `app/src/main/java/com/patsy/app/dms/DmServiceBindings.kt`
- `app/src/test/java/com/patsy/app/dms/DmDataServiceTest.kt`

Implemented:
- encrypted-session-bound access token use
- session/user mismatch fails closed before transport
- membership-row lookup through RLS
- member-authorized thread reads
- member-authorized latest-message reads
- server `expires_at` preserved
- no fabricated read receipts, unread counts, group state, archive state or participant display names

Important truthful blocker:
The current raw DM tables do not by themselves provide a complete authoritative client-facing inbox projection for participant titles, unread counts, group/archive metadata and protected-domain-safe participant resolution. Do not invent those values in the UI. A safe server projection/function or verified existing source is needed before those filters/badges can be called production-real.

Not yet claimed:
- realtime subscription lifecycle
- send repository
- attachment upload/download
- unread reconciliation
- group/archive metadata
- participant display-name resolution
- notifications
- block/report UI binding
- call/video provider

## Put-Away Portal / Storage

Status: `PARTIAL` — authenticated read repository implemented; destructive/write flows and UI binding pending.

Native files:
- `app/src/main/java/com/patsy/app/storage/StorageDataService.kt`
- `app/src/main/java/com/patsy/app/storage/StorageServiceBindings.kt`
- `app/src/test/java/com/patsy/app/storage/StorageDataServiceTest.kt`

Implemented:
- encrypted-session-bound authenticated reads
- `albums` listing
- `media_assets` listing
- profile-saved-media relation reads
- real storage-provider/device-URI/object-path metadata preserved
- real `is_locked` preserved
- real `expires_at` preserved
- byte-size/mime/media type preserved
- session mismatch fails closed

Live backend checked:
- `albums`, `media_assets`, `profile_saved_media`, `projects`, `studio_project_state`, `studio_layers`, `studio_revisions` already exist
- owner-scoped RLS policies exist for those relevant data families
- no duplicate storage schema should be created

Not yet claimed:
- Put-Away Portal Compose screen
- upload/move/restore/delete operations
- signed object access
- retention cleanup worker verification
- profile 100-picture / 30-video quota enforcement
- device/cloud reconciliation

## THyNK persistence backend truth

Live Supabase already contains:
- `studio_project_state`
- `studio_layers`
- `studio_revisions`

These tables have owner-scoped RLS and fields for editor mode, canvas size, duration/fps, background/settings/playback JSON, autosave revision/time, full layer transform/style/effects/content JSON and revision snapshots.

Therefore the next persistence slice should adapt the existing native `StudioCanvasState` / `StudioEditorState` into these existing tables. Do not create a second persistence backend or port donor browser `localStorage`.

## CI note

The legacy THyNK integration script was made forward-compatible so newer already-integrated THyNK routing no longer fails merely because an old exact text anchor disappeared.

Fresh Android CI must still complete on the final exact head before any of the above code is promoted from `PARTIAL` to `VERIFIED`.

## Truth boundary

These repository layers prove native authenticated data-boundary implementation and tests. They do not prove physical-device network behavior, full UI binding, realtime delivery, retention cleanup, provider success or release readiness.

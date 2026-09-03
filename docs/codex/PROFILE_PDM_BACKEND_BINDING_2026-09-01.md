# Profile + Patsy DMs Backend Binding — 2026-09-01

## Purpose

Bind the newest Profile and Patsy DMs visual/state targets to the live Supabase project without inventing counts, messages, read states, calling support or Owner authority.

Project: `tvtknwqcqbkecszvppub`

This is a runtime truth record. It does not authorize broadening RLS or bypassing account/age/Owner gates.

## Profile binding

Live `public.profiles` columns currently verified:
- `user_id uuid`
- `username text`
- `display_name text`
- `avatar_path text`
- `age_tier text`
- `created_at timestamptz`
- `updated_at timestamptz`

Current profile RLS is own-user only:
- `profiles_select_own`
- `profiles_insert_own`
- `profiles_update_own`

Therefore the newest Profile screen may safely bind its identity/header fields to the authenticated user's real profile row, but it must not manufacture public-profile visibility, follower counts, likes, verification badges or other social facts that are not supplied by an authorized backend path.

Live `public.projects` columns currently verified:
- `id uuid`
- `user_id uuid`
- `name text`
- `project_type text`
- `is_locked boolean`
- `created_at timestamptz`
- `updated_at timestamptz`

Current project RLS is own-user CRUD. Recent Projects / Saved Projects may be derived from real authorized project rows. Thumbnail/media relationships require the existing media/project contracts; do not manufacture thumbnail URLs.

The pure Android contract in `app/src/main/java/com/patsy/app/profile/ProfilePresentationState.kt` intentionally represents unknown metrics as `null`; this prevents donor values such as 128 Projects / 2.4K Followers / 1.8K Likes from becoming production claims.

Expanded Profile/Owner-menu rendering never grants Owner authority. Owner routes continue to require the existing server-backed capability gate.

## Patsy DMs binding

Live DM tables currently verified:

`public.dm_threads`
- `id uuid`
- `created_by uuid`
- `created_at timestamptz`
- `updated_at timestamptz`
- `direct_pair_key text`

`public.dm_members`
- `thread_id uuid`
- `user_id uuid`
- `joined_at timestamptz`

`public.dm_messages`
- `id uuid`
- `thread_id uuid`
- `sender_id uuid`
- `body text`
- `created_at timestamptz`
- `expires_at timestamptz`

`public.dm_attachments`
- `message_id uuid`
- `media_id uuid`

`public.notifications`
- `id uuid`
- `user_id uuid`
- `notification_type text`
- `title text`
- `body text`
- `data jsonb`
- `read_at timestamptz`
- `created_at timestamptz`

## DM authorization truth

Current live RLS verifies `account_capabilities.can_use_dms = true` and membership/self authority as appropriate.

Verified policy behavior includes:
- threads SELECT only for authenticated members with DM capability
- messages SELECT only for authenticated thread members with DM capability
- message INSERT only when sender is `auth.uid()`, DM capability is allowed, and sender is a thread member
- message DELETE only by the sender with DM capability
- attachment SELECT only through authorized message/thread membership
- attachment INSERT only by the message sender and only for media owned by that user
- notifications SELECT/UPDATE/DELETE own-user only

This means the client must not treat a locally selected conversation, local unread flag or cached thread as authority. RLS/server capability remains authoritative on every real fetch/send/attachment action.

## Thread creation endpoint

Live Edge Function:
- slug: `create-dm-thread`
- version: `5`
- status: `ACTIVE`
- JWT verification: `true`

Use this production endpoint contract rather than recreating thread membership purely client-side. Inspect the function body before wiring final request/response models if those models are not already represented in native code.

## Responsive presentation contract

`app/src/main/java/com/patsy/app/dms/DmPresentationState.kt` now encodes only presentation behavior:
- filters: All / Unread / Friends / Groups / Archived
- stacked layout below 720dp
- split-view at 720dp and above
- selection without fabricating read receipts or message state
- audio/video calling defaults to `NOT_CONFIGURED`

The 720dp threshold is an Android presentation policy, not an authorization boundary. Security behavior must be identical in both layouts.

## Retention

`dm_messages.expires_at` exists in the live schema, which supports the locked default-expiry model. The UI may explain the three-day default only when the server path creating messages actually sets/enforces that expiry and cleanup is verified. Presence of the column alone is not proof that cleanup has run.

## Still not proven by this audit

- real follower/following/likes aggregation
- public profile discovery rules
- real PDM unread aggregation / read-receipt schema
- typing indicators
- realtime reconnect/revocation behavior on device
- DM retention cleanup job execution
- audio/video calling provider
- final notification payload routing
- physical-device Profile/PDM visual QA

Keep those PARTIAL / NOT_CONFIGURED until verified.

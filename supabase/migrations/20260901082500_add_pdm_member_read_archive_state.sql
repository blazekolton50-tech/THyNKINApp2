-- Per-user Patsy DM presentation state.
--
-- These columns live on the existing (thread_id, user_id) membership row so read/archive state
-- belongs to exactly one authenticated member. No client UPDATE grant or broader RLS policy is
-- added here: writes go through the JWT-authenticated pdm-member-state Edge Function, which uses
-- server time for last_read_at and updates only the caller's existing membership row.

alter table public.dm_members
  add column if not exists last_read_at timestamptz,
  add column if not exists archived_at timestamptz;

comment on column public.dm_members.last_read_at is
  'Server-authored timestamp through which this member has acknowledged the conversation.';

comment on column public.dm_members.archived_at is
  'Server-authored timestamp when this member archived the conversation; NULL means active.';

create index if not exists dm_members_user_archive_idx
  on public.dm_members(user_id, archived_at, thread_id);

-- Private operational tables are server-internal. Client roles must never gain direct access.
-- This migration mirrors the live hardening applied after security gate #36 review.

alter table private.user_access enable row level security;
alter table private.username_reservations enable row level security;
alter table private.storage_deletion_queue enable row level security;
alter table private.moderation_cases enable row level security;
alter table private.moderation_actions enable row level security;
alter table private.security_audit_log enable row level security;
alter table private.notification_delivery_queue enable row level security;
alter table private.account_processing_queue enable row level security;
alter table private.studio_asset_rotation_queue enable row level security;
alter table private.registration_attempts enable row level security;

drop policy if exists deny_direct_client_access on private.user_access;
create policy deny_direct_client_access on private.user_access
for all to anon, authenticated using (false) with check (false);

drop policy if exists deny_direct_client_access on private.username_reservations;
create policy deny_direct_client_access on private.username_reservations
for all to anon, authenticated using (false) with check (false);

drop policy if exists deny_direct_client_access on private.storage_deletion_queue;
create policy deny_direct_client_access on private.storage_deletion_queue
for all to anon, authenticated using (false) with check (false);

drop policy if exists deny_direct_client_access on private.moderation_cases;
create policy deny_direct_client_access on private.moderation_cases
for all to anon, authenticated using (false) with check (false);

drop policy if exists deny_direct_client_access on private.moderation_actions;
create policy deny_direct_client_access on private.moderation_actions
for all to anon, authenticated using (false) with check (false);

drop policy if exists deny_direct_client_access on private.security_audit_log;
create policy deny_direct_client_access on private.security_audit_log
for all to anon, authenticated using (false) with check (false);

drop policy if exists deny_direct_client_access on private.notification_delivery_queue;
create policy deny_direct_client_access on private.notification_delivery_queue
for all to anon, authenticated using (false) with check (false);

drop policy if exists deny_direct_client_access on private.account_processing_queue;
create policy deny_direct_client_access on private.account_processing_queue
for all to anon, authenticated using (false) with check (false);

drop policy if exists deny_direct_client_access on private.studio_asset_rotation_queue;
create policy deny_direct_client_access on private.studio_asset_rotation_queue
for all to anon, authenticated using (false) with check (false);

drop policy if exists deny_direct_client_access on private.registration_attempts;
create policy deny_direct_client_access on private.registration_attempts
for all to anon, authenticated using (false) with check (false);

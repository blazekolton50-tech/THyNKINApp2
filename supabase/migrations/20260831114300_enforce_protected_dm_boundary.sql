-- Under-16/protected accounts may not use DMs. Enforce this at capability sync and RLS,
-- not only in Android navigation. Safe Home/social access remains available for under-16 accounts.

create or replace function private.sync_account_capabilities()
returns trigger
language plpgsql
security definer
set search_path = ''
as $$
begin
  insert into public.account_capabilities(
    user_id, verified_age_tier,
    can_use_creator_studio, can_use_social, can_use_dms, can_link_social_accounts,
    can_use_school_tools, view_once_media_required, updated_at
  ) values (
    new.user_id, new.verified_age_tier,
    (new.verified_age_tier='16_plus' and new.account_state='active'),
    (new.verified_age_tier in ('under_16','16_plus') and new.account_state='active'),
    (new.verified_age_tier='16_plus' and new.account_state='active'),
    (new.verified_age_tier='16_plus' and new.account_state='active'),
    (new.verified_age_tier='under_16' and new.account_state='active'),
    (new.verified_age_tier='under_16'), now()
  )
  on conflict(user_id) do update set
    verified_age_tier=excluded.verified_age_tier,
    can_use_creator_studio=excluded.can_use_creator_studio,
    can_use_social=excluded.can_use_social,
    can_use_dms=excluded.can_use_dms,
    can_link_social_accounts=excluded.can_link_social_accounts,
    can_use_school_tools=excluded.can_use_school_tools,
    view_once_media_required=excluded.view_once_media_required,
    updated_at=now();

  update public.profiles set age_tier=new.verified_age_tier where user_id=new.user_id;
  return new;
end;
$$;

drop policy if exists dm_threads_insert_creator on public.dm_threads;
create policy dm_threads_insert_creator on public.dm_threads for insert to authenticated
with check (
  created_by = (select auth.uid())
  and exists (
    select 1 from public.account_capabilities c
    where c.user_id=(select auth.uid()) and c.can_use_dms=true
  )
);

drop policy if exists dm_threads_select_member on public.dm_threads;
create policy dm_threads_select_member on public.dm_threads for select to authenticated
using (
  exists (
    select 1 from public.account_capabilities c
    where c.user_id=(select auth.uid()) and c.can_use_dms=true
  )
  and exists (
    select 1 from public.dm_members m
    where m.thread_id=dm_threads.id and m.user_id=(select auth.uid())
  )
);

drop policy if exists dm_threads_delete_creator on public.dm_threads;
create policy dm_threads_delete_creator on public.dm_threads for delete to authenticated
using (
  created_by=(select auth.uid())
  and exists (
    select 1 from public.account_capabilities c
    where c.user_id=(select auth.uid()) and c.can_use_dms=true
  )
);

drop policy if exists dm_members_select_self on public.dm_members;
create policy dm_members_select_self on public.dm_members for select to authenticated
using (
  user_id=(select auth.uid())
  and exists (
    select 1 from public.account_capabilities c
    where c.user_id=(select auth.uid()) and c.can_use_dms=true
  )
);

drop policy if exists dm_members_insert_creator_self on public.dm_members;
create policy dm_members_insert_creator_self on public.dm_members for insert to authenticated
with check (
  user_id=(select auth.uid())
  and exists (
    select 1 from public.account_capabilities c
    where c.user_id=(select auth.uid()) and c.can_use_dms=true
  )
  and exists (
    select 1 from public.dm_threads t
    where t.id=dm_members.thread_id and t.created_by=(select auth.uid())
  )
);

drop policy if exists dm_members_delete_self on public.dm_members;
create policy dm_members_delete_self on public.dm_members for delete to authenticated
using (
  user_id=(select auth.uid())
  and exists (
    select 1 from public.account_capabilities c
    where c.user_id=(select auth.uid()) and c.can_use_dms=true
  )
);

drop policy if exists dm_messages_select_member on public.dm_messages;
create policy dm_messages_select_member on public.dm_messages for select to authenticated
using (
  exists (
    select 1 from public.account_capabilities c
    where c.user_id=(select auth.uid()) and c.can_use_dms=true
  )
  and exists (
    select 1 from public.dm_members m
    where m.thread_id=dm_messages.thread_id and m.user_id=(select auth.uid())
  )
);

drop policy if exists dm_messages_insert_member on public.dm_messages;
create policy dm_messages_insert_member on public.dm_messages for insert to authenticated
with check (
  sender_id=(select auth.uid())
  and exists (
    select 1 from public.account_capabilities c
    where c.user_id=(select auth.uid()) and c.can_use_dms=true
  )
  and exists (
    select 1 from public.dm_members m
    where m.thread_id=dm_messages.thread_id and m.user_id=(select auth.uid())
  )
);

drop policy if exists dm_messages_delete_sender on public.dm_messages;
create policy dm_messages_delete_sender on public.dm_messages for delete to authenticated
using (
  sender_id=(select auth.uid())
  and exists (
    select 1 from public.account_capabilities c
    where c.user_id=(select auth.uid()) and c.can_use_dms=true
  )
);

drop policy if exists dm_attach_select_member on public.dm_attachments;
create policy dm_attach_select_member on public.dm_attachments for select to authenticated
using (
  exists (
    select 1 from public.account_capabilities c
    where c.user_id=(select auth.uid()) and c.can_use_dms=true
  )
  and exists (
    select 1
    from public.dm_messages msg
    join public.dm_members m on m.thread_id=msg.thread_id
    where msg.id=dm_attachments.message_id and m.user_id=(select auth.uid())
  )
);

drop policy if exists dm_attach_insert_sender on public.dm_attachments;
create policy dm_attach_insert_sender on public.dm_attachments for insert to authenticated
with check (
  exists (
    select 1 from public.account_capabilities c
    where c.user_id=(select auth.uid()) and c.can_use_dms=true
  )
  and exists (
    select 1 from public.dm_messages msg
    where msg.id=dm_attachments.message_id and msg.sender_id=(select auth.uid())
  )
  and exists (
    select 1 from public.media_assets ma
    where ma.id=dm_attachments.media_id and ma.user_id=(select auth.uid())
  )
);

drop policy if exists dm_attach_delete_sender on public.dm_attachments;
create policy dm_attach_delete_sender on public.dm_attachments for delete to authenticated
using (
  exists (
    select 1 from public.account_capabilities c
    where c.user_id=(select auth.uid()) and c.can_use_dms=true
  )
  and exists (
    select 1 from public.dm_messages msg
    where msg.id=dm_attachments.message_id and msg.sender_id=(select auth.uid())
  )
);

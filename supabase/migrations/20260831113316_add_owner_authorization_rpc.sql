create or replace function public.internal_owner_authorize(
  p_user_id uuid,
  p_capability text
)
returns jsonb
language plpgsql
security definer
set search_path = ''
as $$
declare
  v_access private.user_access%rowtype;
  v_authorization_id uuid := gen_random_uuid();
  v_audit_id uuid := gen_random_uuid();
  v_expires_at timestamptz := clock_timestamp() + interval '60 seconds';
  v_allowed_capabilities constant text[] := array[
    'VIEW_OWNER_PROFILE',
    'VIEW_OWNER_TOOLS',
    'MANAGE_CONTENT',
    'MANAGE_SCHEDULE',
    'VIEW_ANALYTICS',
    'VIEW_SECURITY_AUDIT',
    'MANAGE_PRIVACY',
    'MANAGE_BACKUPS'
  ];
begin
  if p_user_id is null or p_capability is null or not (p_capability = any(v_allowed_capabilities)) then
    return jsonb_build_object('allowed', false);
  end if;

  select * into v_access
  from private.user_access ua
  where ua.user_id = p_user_id;

  if not found
     or v_access.app_role <> 'owner'
     or v_access.account_state <> 'active'
     or v_access.verified_age_tier <> '16_plus'
     or v_access.age_verified_at is null then
    return jsonb_build_object('allowed', false);
  end if;

  insert into private.security_audit_log(user_id,event_type,event_data)
  values(
    p_user_id,
    'owner_authorization_granted',
    jsonb_build_object(
      'capability', p_capability,
      'authorization_id', v_authorization_id,
      'audit_correlation_id', v_audit_id,
      'expires_at', v_expires_at
    )
  );

  return jsonb_build_object(
    'allowed', true,
    'authorization_id', v_authorization_id::text,
    'capability', p_capability,
    'expires_at_epoch_millis', floor(extract(epoch from v_expires_at) * 1000)::bigint,
    'audit_correlation_id', v_audit_id::text
  );
end;
$$;

revoke all on function public.internal_owner_authorize(uuid,text) from public;
revoke all on function public.internal_owner_authorize(uuid,text) from anon;
revoke all on function public.internal_owner_authorize(uuid,text) from authenticated;
grant execute on function public.internal_owner_authorize(uuid,text) to service_role;

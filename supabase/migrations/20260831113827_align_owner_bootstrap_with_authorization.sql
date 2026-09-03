create or replace function public.internal_account_bootstrap(p_user_id uuid)
returns jsonb
language plpgsql
security definer
set search_path = ''
as $function$
declare
  v_email_confirmed boolean := false;
  v_profile_exists boolean := false;
  v_username text := null;
  v_access_exists boolean := false;
  v_age_tier text := 'unknown';
  v_age_verified_at timestamptz := null;
  v_app_role text := 'user';
  v_account_state text := 'restricted';
  v_caps_exists boolean := false;
  v_cap_age_tier text := 'unknown';
  v_can_creator_studio boolean := false;
  v_can_social boolean := false;
  v_can_dms boolean := false;
  v_settings_exists boolean := false;
  v_profile_readiness text;
  v_onboarding_state text;
  v_age_state text;
  v_account_status text;
  v_owner_capabilities text[] := array[]::text[];
  v_restrictions text[] := array[]::text[];
  v_valid_until_epoch_millis bigint;
begin
  if p_user_id is null then
    raise exception 'account_bootstrap_user_required' using errcode = '22004';
  end if;

  select (u.email_confirmed_at is not null)
    into v_email_confirmed
  from auth.users u
  where u.id = p_user_id;

  if not found then
    v_valid_until_epoch_millis := floor(extract(epoch from (clock_timestamp() + interval '5 minutes')) * 1000)::bigint;
    return jsonb_build_object(
      'canonical_user_id', p_user_id::text,
      'profile_readiness', 'MISSING',
      'onboarding_state', 'NEEDS_PROTECTED_SETUP',
      'age_state', 'UNKNOWN_UNVERIFIED',
      'capabilities', '[]'::jsonb,
      'account_status', 'RESTRICTED',
      'settings_availability', 'UNAVAILABLE',
      'restrictions', to_jsonb(array[
        'PROTECTED_CONTENT_ONLY', 'DMS_RESTRICTED', 'SOCIAL_RESTRICTED',
        'PROVIDERS_RESTRICTED', 'OWNER_RESTRICTED'
      ]::text[]),
      'valid_until_epoch_millis', v_valid_until_epoch_millis
    );
  end if;

  select p.username
    into v_username
  from public.profiles p
  where p.user_id = p_user_id;
  v_profile_exists := found;

  select a.verified_age_tier, a.age_verified_at, a.app_role, a.account_state
    into v_age_tier, v_age_verified_at, v_app_role, v_account_state
  from private.user_access a
  where a.user_id = p_user_id;
  v_access_exists := found;

  if not v_access_exists then
    v_age_tier := 'unknown';
    v_age_verified_at := null;
    v_app_role := 'user';
    v_account_state := 'restricted';
  end if;

  select c.verified_age_tier, c.can_use_creator_studio, c.can_use_social, c.can_use_dms
    into v_cap_age_tier, v_can_creator_studio, v_can_social, v_can_dms
  from public.account_capabilities c
  where c.user_id = p_user_id;
  v_caps_exists := found;

  select exists(
    select 1 from public.user_settings s where s.user_id = p_user_id
  ) into v_settings_exists;

  v_profile_readiness := case
    when not v_profile_exists then 'MISSING'
    when v_username is null or btrim(v_username) = '' or v_username ~ '^user_[0-9a-f]{12}$' then 'INCOMPLETE'
    else 'READY'
  end;

  v_age_state := case v_age_tier
    when 'under_16' then 'UNDER_16_PROTECTED'
    when '16_plus' then 'STANDARD_16_PLUS'
    else 'UNKNOWN_UNVERIFIED'
  end;

  v_account_status := case v_account_state
    when 'active' then 'ACTIVE'
    when 'restricted' then 'RESTRICTED'
    else 'DISABLED'
  end;

  v_onboarding_state := case
    when v_profile_readiness <> 'READY' then 'NEEDS_PROFILE'
    when not v_email_confirmed then 'NEEDS_EMAIL_CONFIRMATION'
    when not v_access_exists then 'NEEDS_PROTECTED_SETUP'
    when v_age_tier = 'unknown' then 'NEEDS_AGE_STEP'
    else 'READY'
  end;

  if not v_access_exists or v_account_state <> 'active' or v_age_tier = 'unknown' then
    v_restrictions := array[
      'PROTECTED_CONTENT_ONLY', 'DMS_RESTRICTED', 'SOCIAL_RESTRICTED',
      'PROVIDERS_RESTRICTED', 'OWNER_RESTRICTED'
    ]::text[];
  elsif v_age_tier = 'under_16' then
    v_restrictions := array[
      'PROTECTED_CONTENT_ONLY', 'DMS_RESTRICTED', 'PROVIDERS_RESTRICTED', 'OWNER_RESTRICTED'
    ]::text[];
  else
    if not v_caps_exists or v_cap_age_tier <> v_age_tier then
      v_restrictions := array[
        'DMS_RESTRICTED', 'SOCIAL_RESTRICTED', 'PROVIDERS_RESTRICTED'
      ]::text[];
    else
      if not v_can_dms then
        v_restrictions := array_append(v_restrictions, 'DMS_RESTRICTED');
      end if;
      if not v_can_social then
        v_restrictions := array_append(v_restrictions, 'SOCIAL_RESTRICTED');
      end if;
      if not v_can_creator_studio then
        v_restrictions := array_append(v_restrictions, 'PROVIDERS_RESTRICTED');
      end if;
    end if;

    if v_app_role <> 'owner' then
      v_restrictions := array_append(v_restrictions, 'OWNER_RESTRICTED');
    end if;
  end if;

  if v_access_exists
     and v_account_state = 'active'
     and v_age_tier = '16_plus'
     and v_age_verified_at is not null
     and v_app_role = 'owner'
     and v_email_confirmed
     and v_profile_readiness = 'READY'
     and v_caps_exists
     and v_cap_age_tier = v_age_tier then
    v_owner_capabilities := array[
      'VIEW_OWNER_PROFILE', 'VIEW_OWNER_TOOLS', 'MANAGE_CONTENT', 'MANAGE_SCHEDULE',
      'VIEW_ANALYTICS', 'VIEW_SECURITY_AUDIT', 'MANAGE_PRIVACY', 'MANAGE_BACKUPS'
    ]::text[];
  end if;

  v_valid_until_epoch_millis := floor(extract(epoch from (clock_timestamp() + interval '5 minutes')) * 1000)::bigint;

  return jsonb_build_object(
    'canonical_user_id', p_user_id::text,
    'profile_readiness', v_profile_readiness,
    'onboarding_state', v_onboarding_state,
    'age_state', v_age_state,
    'capabilities', to_jsonb(v_owner_capabilities),
    'account_status', v_account_status,
    'settings_availability', case when v_settings_exists then 'AVAILABLE' else 'UNAVAILABLE' end,
    'restrictions', to_jsonb(v_restrictions),
    'valid_until_epoch_millis', v_valid_until_epoch_millis
  );
end;
$function$;

revoke all on function public.internal_account_bootstrap(uuid) from public;
revoke all on function public.internal_account_bootstrap(uuid) from anon;
revoke all on function public.internal_account_bootstrap(uuid) from authenticated;
grant execute on function public.internal_account_bootstrap(uuid) to service_role;

-- Authenticated THyNK project creation. Caller identity is server-derived only.

create or replace function public.studio_create_project(
    p_name text
)
returns table (
    project_id uuid,
    name text
)
language plpgsql
security invoker
set search_path = public, pg_temp
as $$
declare
    v_user_id uuid := auth.uid();
    v_name text := btrim(p_name);
    v_project_id uuid;
begin
    if v_user_id is null then
        raise exception 'AUTH_REQUIRED' using errcode = '42501';
    end if;

    if v_name is null or char_length(v_name) < 1 or char_length(v_name) > 120 then
        raise exception 'INVALID_PROJECT_NAME' using errcode = '22023';
    end if;

    insert into public.projects (user_id, name, project_type)
    values (v_user_id, v_name, 'creator')
    returning id into v_project_id;

    return query select v_project_id, v_name;
end;
$$;

revoke all on function public.studio_create_project(text) from public;
revoke all on function public.studio_create_project(text) from anon;
grant execute on function public.studio_create_project(text) to authenticated;

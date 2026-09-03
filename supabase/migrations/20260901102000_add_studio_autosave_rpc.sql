-- Atomic authenticated THyNK autosave writer.
-- Keeps existing RLS and the existing six native Studio editor modes authoritative.

create or replace function public.studio_autosave(
    p_project_id uuid,
    p_editor_mode text,
    p_canvas_width_px integer,
    p_canvas_height_px integer,
    p_duration_ms integer,
    p_fps numeric,
    p_layers jsonb,
    p_snapshot jsonb
)
returns table (
    revision_no bigint,
    saved_at timestamptz
)
language plpgsql
security invoker
set search_path = public, pg_temp
as $$
declare
    v_user_id uuid := auth.uid();
    v_saved_at timestamptz := now();
    v_next_revision bigint;
    v_layer jsonb;
    v_layer_ids uuid[] := array[]::uuid[];
begin
    if v_user_id is null then
        raise exception 'AUTH_REQUIRED' using errcode = '42501';
    end if;

    if p_editor_mode not in ('image', 'video', 'document', 'meme', 'collage', 'camera') then
        raise exception 'INVALID_EDITOR_MODE' using errcode = '22023';
    end if;

    if p_canvas_width_px <= 0 or p_canvas_height_px <= 0 then
        raise exception 'INVALID_CANVAS_SIZE' using errcode = '22023';
    end if;

    if p_duration_ms < 0 or p_fps < 1 or p_fps > 120 then
        raise exception 'INVALID_TIMING' using errcode = '22023';
    end if;

    if p_layers is null or jsonb_typeof(p_layers) <> 'array' then
        raise exception 'INVALID_LAYERS' using errcode = '22023';
    end if;

    if p_snapshot is null or jsonb_typeof(p_snapshot) <> 'object' then
        raise exception 'INVALID_SNAPSHOT' using errcode = '22023';
    end if;

    -- Lock the caller-owned project so simultaneous autosaves serialize revision numbers.
    perform 1
      from public.projects p
     where p.id = p_project_id
       and p.user_id = v_user_id
     for update;

    if not found then
        raise exception 'PROJECT_NOT_FOUND_OR_NOT_OWNED' using errcode = '42501';
    end if;

    select greatest(
        coalesce(s.autosave_revision, 0),
        coalesce((
            select max(r.revision_no)
              from public.studio_revisions r
             where r.project_id = p_project_id
               and r.user_id = v_user_id
        ), 0)
    ) + 1
      into v_next_revision
      from (select 1) seed
      left join public.studio_project_state s
        on s.project_id = p_project_id
       and s.user_id = v_user_id;

    insert into public.studio_project_state (
        project_id,
        user_id,
        editor_mode,
        canvas_width_px,
        canvas_height_px,
        duration_ms,
        fps,
        autosave_revision,
        last_autosaved_at,
        updated_at
    ) values (
        p_project_id,
        v_user_id,
        p_editor_mode,
        p_canvas_width_px,
        p_canvas_height_px,
        p_duration_ms,
        p_fps,
        v_next_revision,
        v_saved_at,
        v_saved_at
    )
    on conflict (project_id) do update set
        editor_mode = excluded.editor_mode,
        canvas_width_px = excluded.canvas_width_px,
        canvas_height_px = excluded.canvas_height_px,
        duration_ms = excluded.duration_ms,
        fps = excluded.fps,
        autosave_revision = excluded.autosave_revision,
        last_autosaved_at = excluded.last_autosaved_at,
        updated_at = excluded.updated_at
    where public.studio_project_state.user_id = v_user_id;

    -- Replace the caller-owned project's persisted layer set inside the same transaction.
    -- IDs remain stable so parent/group references can be restored exactly.
    for v_layer in select value from jsonb_array_elements(p_layers)
    loop
        if coalesce(v_layer->>'id', '') = '' then
            raise exception 'LAYER_ID_REQUIRED' using errcode = '22023';
        end if;
        v_layer_ids := array_append(v_layer_ids, (v_layer->>'id')::uuid);
    end loop;

    delete from public.studio_layers l
     where l.project_id = p_project_id
       and l.user_id = v_user_id
       and not (l.id = any(v_layer_ids));

    if jsonb_array_length(p_layers) > 0 then
        insert into public.studio_layers (
            id,
            project_id,
            user_id,
            parent_layer_id,
            layer_type,
            name,
            z_index,
            start_ms,
            end_ms,
            is_locked,
            is_hidden,
            opacity,
            transform,
            crop,
            style,
            effects,
            content,
            updated_at
        )
        select
            (x->>'id')::uuid,
            p_project_id,
            v_user_id,
            nullif(x->>'parent_layer_id', '')::uuid,
            x->>'layer_type',
            coalesce(nullif(x->>'name', ''), 'Layer'),
            coalesce((x->>'z_index')::integer, 0),
            coalesce((x->>'start_ms')::integer, 0),
            nullif(x->>'end_ms', '')::integer,
            coalesce((x->>'is_locked')::boolean, false),
            coalesce((x->>'is_hidden')::boolean, false),
            coalesce((x->>'opacity')::numeric, 1),
            coalesce(x->'transform', '{}'::jsonb),
            coalesce(x->'crop', '{}'::jsonb),
            coalesce(x->'style', '{}'::jsonb),
            coalesce(x->'effects', '[]'::jsonb),
            coalesce(x->'content', '{}'::jsonb),
            v_saved_at
        from jsonb_array_elements(p_layers) as layer(value)
        cross join lateral (select layer.value as x) parsed
        on conflict (id) do update set
            parent_layer_id = excluded.parent_layer_id,
            layer_type = excluded.layer_type,
            name = excluded.name,
            z_index = excluded.z_index,
            start_ms = excluded.start_ms,
            end_ms = excluded.end_ms,
            is_locked = excluded.is_locked,
            is_hidden = excluded.is_hidden,
            opacity = excluded.opacity,
            transform = excluded.transform,
            crop = excluded.crop,
            style = excluded.style,
            effects = excluded.effects,
            content = excluded.content,
            updated_at = excluded.updated_at
        where public.studio_layers.project_id = p_project_id
          and public.studio_layers.user_id = v_user_id;
    end if;

    insert into public.studio_revisions (
        project_id,
        user_id,
        revision_no,
        revision_type,
        snapshot,
        created_at
    ) values (
        p_project_id,
        v_user_id,
        v_next_revision,
        'autosave',
        p_snapshot,
        v_saved_at
    );

    update public.projects
       set updated_at = v_saved_at
     where id = p_project_id
       and user_id = v_user_id;

    return query select v_next_revision, v_saved_at;
end;
$$;

revoke all on function public.studio_autosave(uuid, text, integer, integer, integer, numeric, jsonb, jsonb) from public;
revoke all on function public.studio_autosave(uuid, text, integer, integer, integer, numeric, jsonb, jsonb) from anon;
grant execute on function public.studio_autosave(uuid, text, integer, integer, integer, numeric, jsonb, jsonb) to authenticated;

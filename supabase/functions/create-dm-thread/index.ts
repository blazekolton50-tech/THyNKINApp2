import { createClient } from 'npm:@supabase/supabase-js@2.112.4'

const json = (body: unknown, status = 200) => new Response(JSON.stringify(body), {
  status,
  headers: { 'Content-Type': 'application/json', 'Cache-Control': 'no-store' },
})

Deno.serve(async (req: Request) => {
  if (req.method !== 'POST') return json({ error: 'Method not allowed' }, 405)

  const authHeader = req.headers.get('Authorization')
  if (!authHeader?.startsWith('Bearer ')) return json({ error: 'Unauthorized' }, 401)

  const supabaseUrl = Deno.env.get('SUPABASE_URL')!
  const anonKey = Deno.env.get('SUPABASE_ANON_KEY')!
  const serviceKey = Deno.env.get('SUPABASE_SERVICE_ROLE_KEY')!
  if (!supabaseUrl || !anonKey || !serviceKey) return json({ error: 'Service unavailable' }, 503)

  const userClient = createClient(supabaseUrl, anonKey, {
    global: { headers: { Authorization: authHeader } },
    auth: { persistSession: false, autoRefreshToken: false },
  })
  const admin = createClient(supabaseUrl, serviceKey, {
    auth: { persistSession: false, autoRefreshToken: false },
  })

  const { data: userData, error: userError } = await userClient.auth.getUser()
  const user = userData.user
  if (userError || !user) return json({ error: 'Unauthorized' }, 401)

  let payload: { target_user_id?: string }
  try { payload = await req.json() } catch { return json({ error: 'Invalid JSON' }, 400) }
  const target = payload.target_user_id
  if (!target || !/^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$/i.test(target)) {
    return json({ error: 'Invalid target user' }, 400)
  }
  if (target === user.id) return json({ error: 'Cannot message yourself' }, 400)

  const { data: capabilities, error: capabilityError } = await admin
    .from('account_capabilities')
    .select('user_id,verified_age_tier,can_use_dms')
    .in('user_id', [user.id, target])
  if (capabilityError) return json({ error: 'Could not verify messaging access' }, 500)
  if (!capabilities || capabilities.length !== 2 || capabilities.some((c) => c.can_use_dms !== true || c.verified_age_tier !== '16_plus')) {
    return json({ error: 'Messaging is unavailable for this account' }, 403)
  }

  const { data: connections, error: connectionError } = await userClient
    .from('user_connections')
    .select('requester_id,addressee_id,status')
    .eq('status', 'accepted')
    .or(`and(requester_id.eq.${user.id},addressee_id.eq.${target}),and(requester_id.eq.${target},addressee_id.eq.${user.id})`)
    .limit(1)
  if (connectionError) return json({ error: 'Could not verify connection' }, 500)
  if (!connections?.length) return json({ error: 'An accepted safe connection is required' }, 403)

  const { data: blocks, error: blockError } = await userClient
    .from('user_blocks')
    .select('blocker_id,blocked_id')
    .or(`and(blocker_id.eq.${user.id},blocked_id.eq.${target}),and(blocker_id.eq.${target},blocked_id.eq.${user.id})`)
    .limit(1)
  if (blockError) return json({ error: 'Could not verify block status' }, 500)
  if (blocks?.length) return json({ error: 'Messaging is unavailable for this connection' }, 403)

  const { data: dmPrefs, error: prefsError } = await admin
    .from('user_settings')
    .select('user_id,allow_dm_from_connections')
    .in('user_id', [user.id, target])
  if (prefsError) return json({ error: 'Could not verify messaging preferences' }, 500)
  if (!dmPrefs || dmPrefs.length !== 2 || dmPrefs.some((p) => !p.allow_dm_from_connections)) {
    return json({ error: 'Messaging is disabled for this connection' }, 403)
  }

  const pair = [user.id, target].sort()
  const directPairKey = `direct:${pair[0]}:${pair[1]}`

  const { data: existing } = await admin
    .from('dm_threads')
    .select('id')
    .eq('direct_pair_key', directPairKey)
    .maybeSingle()

  let threadId = existing?.id as string | undefined
  if (!threadId) {
    const { data: created, error: createError } = await admin
      .from('dm_threads')
      .insert({ created_by: user.id, direct_pair_key: directPairKey })
      .select('id')
      .single()
    if (createError) {
      const { data: raced } = await admin
        .from('dm_threads')
        .select('id')
        .eq('direct_pair_key', directPairKey)
        .maybeSingle()
      if (!raced?.id) return json({ error: 'Could not create conversation' }, 500)
      threadId = raced.id
    } else {
      threadId = created.id
    }
  }

  const { error: memberError } = await admin
    .from('dm_members')
    .upsert([
      { thread_id: threadId, user_id: user.id },
      { thread_id: threadId, user_id: target },
    ], { onConflict: 'thread_id,user_id', ignoreDuplicates: true })
  if (memberError) return json({ error: 'Could not create conversation membership' }, 500)

  return json({ thread_id: threadId })
})

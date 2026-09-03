import { createClient } from 'npm:@supabase/supabase-js@2.112.4'

const json = (body: unknown, status = 200) => new Response(JSON.stringify(body), {
  status,
  headers: { 'Content-Type': 'application/json', 'Cache-Control': 'no-store' },
})

const uuid = /^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$/i

type Payload = {
  thread_id?: string
  action?: 'mark_read' | 'set_archived'
  archived?: boolean
}

Deno.serve(async (req: Request) => {
  if (req.method !== 'POST') return json({ error: 'Method not allowed' }, 405)

  const authHeader = req.headers.get('Authorization')
  if (!authHeader?.startsWith('Bearer ')) return json({ error: 'Unauthorized' }, 401)

  const supabaseUrl = Deno.env.get('SUPABASE_URL')
  const anonKey = Deno.env.get('SUPABASE_ANON_KEY')
  const serviceKey = Deno.env.get('SUPABASE_SERVICE_ROLE_KEY')
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

  let payload: Payload
  try {
    payload = await req.json()
  } catch {
    return json({ error: 'Invalid JSON' }, 400)
  }

  const threadId = payload.thread_id
  if (!threadId || !uuid.test(threadId)) return json({ error: 'Invalid conversation' }, 400)
  if (payload.action !== 'mark_read' && payload.action !== 'set_archived') {
    return json({ error: 'Invalid action' }, 400)
  }
  if (payload.action === 'set_archived' && typeof payload.archived !== 'boolean') {
    return json({ error: 'Invalid archive state' }, 400)
  }

  const { data: capability, error: capabilityError } = await admin
    .from('account_capabilities')
    .select('user_id,verified_age_tier,can_use_dms')
    .eq('user_id', user.id)
    .maybeSingle()
  if (capabilityError) return json({ error: 'Could not verify messaging access' }, 500)
  if (!capability || capability.verified_age_tier !== '16_plus' || capability.can_use_dms !== true) {
    return json({ error: 'Messaging is unavailable for this account' }, 403)
  }

  const { data: membership, error: membershipError } = await admin
    .from('dm_members')
    .select('thread_id,user_id')
    .eq('thread_id', threadId)
    .eq('user_id', user.id)
    .maybeSingle()
  if (membershipError) return json({ error: 'Could not verify conversation access' }, 500)
  if (!membership) return json({ error: 'Conversation unavailable' }, 403)

  const now = new Date().toISOString()
  const patch = payload.action === 'mark_read'
    ? { last_read_at: now }
    : { archived_at: payload.archived ? now : null }

  const { data: updated, error: updateError } = await admin
    .from('dm_members')
    .update(patch)
    .eq('thread_id', threadId)
    .eq('user_id', user.id)
    .select('thread_id,last_read_at,archived_at')
    .maybeSingle()

  if (updateError || !updated) return json({ error: 'Could not update conversation state' }, 500)

  return json({
    thread_id: updated.thread_id,
    last_read_at: updated.last_read_at ?? null,
    archived_at: updated.archived_at ?? null,
  })
})

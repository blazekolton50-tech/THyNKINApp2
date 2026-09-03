import { createClient } from 'npm:@supabase/supabase-js@2.112.4'

const jsonHeaders = {
  'Content-Type': 'application/json; charset=utf-8',
  'Cache-Control': 'no-store',
}

const reply = (status: number, body: Record<string, unknown>) =>
  new Response(JSON.stringify(body), { status, headers: jsonHeaders })

Deno.serve(async (req: Request) => {
  if (req.method !== 'GET') return reply(405, { code: 'METHOD_NOT_ALLOWED' })

  const authHeader = req.headers.get('Authorization')
  if (!authHeader?.startsWith('Bearer ')) return reply(401, { code: 'UNAUTHORIZED' })

  const supabaseUrl = Deno.env.get('SUPABASE_URL')
  const anonKey = Deno.env.get('SUPABASE_ANON_KEY')
  const serviceRole = Deno.env.get('SUPABASE_SERVICE_ROLE_KEY')
  if (!supabaseUrl || !anonKey || !serviceRole) {
    console.error('account-bootstrap missing required server configuration')
    return reply(503, { code: 'SERVICE_UNAVAILABLE' })
  }

  const userClient = createClient(supabaseUrl, anonKey, {
    global: { headers: { Authorization: authHeader } },
    auth: { persistSession: false, autoRefreshToken: false },
  })
  const admin = createClient(supabaseUrl, serviceRole, {
    auth: { persistSession: false, autoRefreshToken: false },
  })

  const { data: userData, error: userError } = await userClient.auth.getUser()
  const user = userData.user
  if (userError || !user) return reply(401, { code: 'UNAUTHORIZED' })

  const { data, error } = await admin.rpc('internal_account_bootstrap', {
    p_user_id: user.id,
  })
  if (error || !data || typeof data !== 'object') {
    console.error('account-bootstrap RPC failed', error?.code ?? 'MISSING_DATA')
    return reply(503, { code: 'SERVICE_UNAVAILABLE' })
  }

  const bootstrap = data as Record<string, unknown>
  if (bootstrap.canonical_user_id !== user.id) {
    console.error('account-bootstrap canonical user mismatch')
    return reply(503, { code: 'SERVICE_UNAVAILABLE' })
  }

  const required = [
    'profile_readiness', 'onboarding_state', 'age_state', 'capabilities',
    'account_status', 'settings_availability', 'restrictions', 'valid_until_epoch_millis',
  ]
  if (required.some((key) => !(key in bootstrap))) {
    console.error('account-bootstrap RPC returned incomplete contract')
    return reply(503, { code: 'SERVICE_UNAVAILABLE' })
  }

  return new Response(JSON.stringify(bootstrap), { status: 200, headers: jsonHeaders })
})

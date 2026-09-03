import { createClient } from 'npm:@supabase/supabase-js@2'

const json = (body: unknown, status = 200) => new Response(JSON.stringify(body), {
  status,
  headers: { 'Content-Type': 'application/json; charset=utf-8', 'Cache-Control': 'no-store' },
})

const uuidPattern = /^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$/i

function validPassword(password: string) {
  return password.length >= 12 && password.length <= 1024 &&
    /[a-z]/.test(password) && /[A-Z]/.test(password) && /[0-9]/.test(password) &&
    /[^A-Za-z0-9\s]/.test(password) && !/\s/.test(password)
}

async function rollbackUser(supabaseUrl: string, serviceKey: string, userId: string) {
  try {
    await fetch(`${supabaseUrl}/auth/v1/admin/users/${encodeURIComponent(userId)}`, {
      method: 'DELETE',
      headers: { apikey: serviceKey, Authorization: `Bearer ${serviceKey}` },
    })
  } catch { /* best effort; server logs below already identify the failure */ }
}

Deno.serve(async (req: Request) => {
  if (req.method !== 'POST') return json({ code: 'METHOD_NOT_ALLOWED' }, 405)

  let body: { registration_attempt_id?: unknown; password?: unknown }
  try { body = await req.json() } catch { return json({ code: 'INVALID_REQUEST' }, 400) }
  const attemptId = typeof body.registration_attempt_id === 'string' ? body.registration_attempt_id : ''
  const password = typeof body.password === 'string' ? body.password : ''
  if (!uuidPattern.test(attemptId)) return json({ code: 'INVALID_ATTEMPT' }, 400)
  if (!validPassword(password)) return json({ code: 'INVALID_PASSWORD' }, 400)

  const supabaseUrl = Deno.env.get('SUPABASE_URL')
  const serviceKey = Deno.env.get('SUPABASE_SERVICE_ROLE_KEY')
  const anonKey = Deno.env.get('SUPABASE_ANON_KEY')
  if (!supabaseUrl || !serviceKey || !anonKey) return json({ code: 'SERVICE_UNAVAILABLE' }, 503)

  const admin = createClient(supabaseUrl, serviceKey, {
    auth: { persistSession: false, autoRefreshToken: false },
  })

  const { data: attempts, error: claimError } = await admin.rpc('internal_claim_registration_attempt', {
    p_attempt_id: attemptId,
  })
  if (claimError) {
    console.error('auth-register-complete claim failed', claimError.code)
    return json({ code: 'SERVICE_UNAVAILABLE' }, 503)
  }
  const attempt = Array.isArray(attempts) ? attempts[0] : null
  if (!attempt?.username || !attempt?.email || !attempt?.experience_mode) {
    return json({ code: 'INVALID_OR_EXPIRED_ATTEMPT' }, 410)
  }

  const signupResponse = await fetch(`${supabaseUrl}/auth/v1/signup`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json', apikey: anonKey },
    body: JSON.stringify({ email: attempt.email, password }),
  })
  const signupText = await signupResponse.text()
  let signup: any = {}
  try { signup = signupText ? JSON.parse(signupText) : {} } catch { /* handled below */ }

  if (!signupResponse.ok) {
    if (signupResponse.status === 429) return json({ code: 'RATE_LIMITED' }, 429)
    const authCode = String(signup?.error_code || signup?.code || '').toLowerCase()
    const message = String(signup?.msg || signup?.message || '').toLowerCase()
    if (authCode.includes('user_already_exists') || message.includes('already registered') || message.includes('already been registered')) {
      return json({ code: 'DUPLICATE_EMAIL' }, 409)
    }
    console.error('auth-register-complete signup failed', signupResponse.status, authCode)
    return json({ code: signupResponse.status >= 500 ? 'SERVICE_UNAVAILABLE' : 'REGISTRATION_REJECTED' }, signupResponse.status >= 500 ? 503 : 400)
  }

  const user = signup?.user
  const identities = user?.identities
  if (!user?.id || (Array.isArray(identities) && identities.length === 0)) {
    return json({ code: 'DUPLICATE_EMAIL' }, 409)
  }

  const { error: finalizeError } = await admin.rpc('internal_finalize_registration', {
    p_user_id: user.id,
    p_username: attempt.username,
    p_experience_mode: attempt.experience_mode,
  })
  if (finalizeError) {
    console.error('auth-register-complete finalize failed', finalizeError.code)
    await rollbackUser(supabaseUrl, serviceKey, user.id)
    const message = (finalizeError.message || '').toLowerCase()
    if (message.includes('duplicate_username') || message.includes('reserved_username')) {
      return json({ code: 'DUPLICATE_USERNAME' }, 409)
    }
    return json({ code: 'SERVICE_UNAVAILABLE' }, 503)
  }

  return json({
    user_id: user.id,
    username: attempt.username,
    confirmation_email: {
      state: 'QUEUED',
      masked_email: (() => {
        const [local, domain] = String(attempt.email).split('@')
        const shown = local.slice(0, Math.min(2, local.length))
        return `${shown}${'*'.repeat(Math.max(2, local.length - shown.length))}@${domain}`
      })(),
    },
  })
})

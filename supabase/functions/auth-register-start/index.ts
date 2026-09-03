import { createClient } from 'npm:@supabase/supabase-js@2'

const json = (body: unknown, status = 200) => new Response(JSON.stringify(body), {
  status,
  headers: { 'Content-Type': 'application/json; charset=utf-8', 'Cache-Control': 'no-store' },
})

const usernamePattern = /^[A-Za-z0-9](?:[A-Za-z0-9._]{1,28}[A-Za-z0-9])?$/
const emailPattern = /^[^@\s]+@[^@\s]+\.[^@\s]+$/
const modes = new Set(['16+ Patsy', 'Under-16 Patsy', 'Protected Mode'])

function maskEmail(email: string) {
  const [local, domain] = email.split('@')
  if (!local || !domain) return 'hidden'
  const shown = local.slice(0, Math.min(2, local.length))
  return `${shown}${'*'.repeat(Math.max(2, local.length - shown.length))}@${domain}`
}

Deno.serve(async (req: Request) => {
  if (req.method !== 'POST') return json({ code: 'METHOD_NOT_ALLOWED' }, 405)

  let body: { username?: unknown; email?: unknown; experience_mode?: unknown }
  try { body = await req.json() } catch { return json({ code: 'INVALID_REQUEST' }, 400) }

  const username = typeof body.username === 'string' ? body.username.trim() : ''
  const email = typeof body.email === 'string' ? body.email.trim().toLowerCase() : ''
  const experienceMode = typeof body.experience_mode === 'string' ? body.experience_mode : ''
  if (!usernamePattern.test(username)) return json({ code: 'INVALID_USERNAME' }, 400)
  if (email.length > 254 || !emailPattern.test(email)) return json({ code: 'INVALID_EMAIL' }, 400)
  if (!modes.has(experienceMode)) return json({ code: 'INVALID_EXPERIENCE_MODE' }, 400)

  const supabaseUrl = Deno.env.get('SUPABASE_URL')
  const serviceKey = Deno.env.get('SUPABASE_SERVICE_ROLE_KEY')
  if (!supabaseUrl || !serviceKey) return json({ code: 'SERVICE_UNAVAILABLE' }, 503)

  const admin = createClient(supabaseUrl, serviceKey, {
    auth: { persistSession: false, autoRefreshToken: false },
  })

  const { data, error } = await admin.rpc('internal_issue_registration_attempt', {
    p_username: username,
    p_email: email,
    p_experience_mode: experienceMode,
  })
  if (error) {
    const message = (error.message || '').toLowerCase()
    if (message.includes('duplicate_username') || message.includes('reserved_username')) {
      return json({ code: 'DUPLICATE_USERNAME' }, 409)
    }
    if (message.includes('invalid_username')) return json({ code: 'INVALID_USERNAME' }, 400)
    if (message.includes('invalid_email')) return json({ code: 'INVALID_EMAIL' }, 400)
    console.error('auth-register-start rpc failed', error.code)
    return json({ code: 'SERVICE_UNAVAILABLE' }, 503)
  }

  return json({
    registration_attempt_id: data,
    normalized_username: username,
    masked_email: maskEmail(email),
  })
})

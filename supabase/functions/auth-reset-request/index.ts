import "jsr:@supabase/functions-js/edge-runtime.d.ts";

const headers = { "Content-Type": "application/json; charset=utf-8", "Cache-Control": "no-store" };
const usernamePattern = /^[A-Za-z0-9](?:[A-Za-z0-9._]{1,28}[A-Za-z0-9])?$/;
const emailPattern = /^[^@\s]+@[^@\s]+\.[^@\s]+$/;
const genericMessage = "If an account matches and email delivery is available, you'll receive password reset instructions.";

function reply(status: number, body: Record<string, unknown>) {
  return new Response(JSON.stringify(body), { status, headers });
}

async function serviceFetch(url: string, serviceRole: string, init: RequestInit = {}) {
  const requestHeaders = new Headers(init.headers);
  requestHeaders.set("apikey", serviceRole);
  requestHeaders.set("Authorization", `Bearer ${serviceRole}`);
  return fetch(url, { ...init, headers: requestHeaders });
}

Deno.serve(async (req: Request) => {
  if (req.method !== "POST") return reply(405, { code: "METHOD_NOT_ALLOWED" });

  let body: { identifier?: unknown };
  try { body = await req.json(); } catch { return reply(400, { code: "INVALID_REQUEST" }); }
  const identifier = typeof body.identifier === "string" ? body.identifier.trim() : "";
  if (!identifier || identifier.length > 254) return reply(400, { code: "INVALID_REQUEST" });

  const isEmail = identifier.includes("@");
  if (isEmail && !emailPattern.test(identifier)) return reply(400, { code: "INVALID_REQUEST" });
  if (!isEmail && !usernamePattern.test(identifier)) return reply(400, { code: "INVALID_REQUEST" });

  const supabaseUrl = Deno.env.get("SUPABASE_URL");
  const serviceRole = Deno.env.get("SUPABASE_SERVICE_ROLE_KEY");
  const anonKey = Deno.env.get("SUPABASE_ANON_KEY");
  if (!supabaseUrl || !serviceRole || !anonKey) {
    console.error("auth-reset-request missing server configuration");
    return reply(200, { accepted: true, message: genericMessage });
  }

  let email: string | null = isEmail ? identifier.toLowerCase() : null;
  if (!isEmail) {
    const params = new URLSearchParams({ select: "user_id,username", username: `ilike.${identifier}` });
    const profileResponse = await serviceFetch(`${supabaseUrl}/rest/v1/profiles?${params.toString()}`, serviceRole);
    if (profileResponse.ok) {
      const profiles = await profileResponse.json() as Array<{ user_id?: string; username?: string }>;
      const profile = profiles.find((item) => item.username?.toLowerCase() === identifier.toLowerCase());
      if (profile?.user_id) {
        const userResponse = await serviceFetch(`${supabaseUrl}/auth/v1/admin/users/${encodeURIComponent(profile.user_id)}`, serviceRole);
        if (userResponse.ok) {
          const user = await userResponse.json() as { email?: string };
          email = user.email ?? null;
        }
      }
    }
  }

  if (email) {
    try {
      const recover = await fetch(`${supabaseUrl}/auth/v1/recover`, {
        method: "POST",
        headers: { "Content-Type": "application/json", "apikey": anonKey },
        body: JSON.stringify({ email }),
      });
      if (!recover.ok) console.warn("auth-reset-request provider response", recover.status);
    } catch (error) {
      console.warn("auth-reset-request provider unavailable", String(error));
    }
  }

  // Deliberately identical for known and unknown accounts and for provider delivery uncertainty.
  return reply(200, { accepted: true, message: genericMessage });
});

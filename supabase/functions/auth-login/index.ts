import "jsr:@supabase/functions-js/edge-runtime.d.ts";

const jsonHeaders = { "Content-Type": "application/json; charset=utf-8", "Cache-Control": "no-store" };
const usernamePattern = /^[A-Za-z0-9](?:[A-Za-z0-9._]{1,28}[A-Za-z0-9])?$/;
const emailPattern = /^[^@\s]+@[^@\s]+\.[^@\s]+$/;

function reply(status: number, body: Record<string, unknown>) {
  return new Response(JSON.stringify(body), { status, headers: jsonHeaders });
}

function maskEmail(email: string): string {
  const [local, domain] = email.split("@");
  if (!local || !domain) return "hidden";
  const shown = local.length <= 2 ? local.slice(0, 1) : local.slice(0, 2);
  return `${shown}${"*".repeat(Math.max(2, local.length - shown.length))}@${domain}`;
}

async function serviceFetch(url: string, serviceRole: string, init: RequestInit = {}) {
  const headers = new Headers(init.headers);
  headers.set("apikey", serviceRole);
  headers.set("Authorization", `Bearer ${serviceRole}`);
  return fetch(url, { ...init, headers });
}

Deno.serve(async (req: Request) => {
  if (req.method !== "POST") return reply(405, { code: "METHOD_NOT_ALLOWED" });

  const supabaseUrl = Deno.env.get("SUPABASE_URL");
  const serviceRole = Deno.env.get("SUPABASE_SERVICE_ROLE_KEY");
  const anonKey = Deno.env.get("SUPABASE_ANON_KEY");
  if (!supabaseUrl || !serviceRole || !anonKey) {
    console.error("auth-login missing required server configuration");
    return reply(503, { code: "SERVICE_UNAVAILABLE" });
  }

  let body: { identifier?: unknown; password?: unknown };
  try {
    body = await req.json();
  } catch {
    return reply(400, { code: "INVALID_REQUEST" });
  }

  const identifier = typeof body.identifier === "string" ? body.identifier.trim() : "";
  const password = typeof body.password === "string" ? body.password : "";
  if (!identifier || !password || password.length > 1024) {
    return reply(400, { code: "INVALID_REQUEST" });
  }

  const isEmail = identifier.includes("@");
  if (isEmail && (!emailPattern.test(identifier) || identifier.length > 254)) {
    return reply(400, { code: "INVALID_REQUEST" });
  }
  if (!isEmail && !usernamePattern.test(identifier)) {
    return reply(400, { code: "INVALID_REQUEST" });
  }

  let email = identifier.toLowerCase();
  if (!isEmail) {
    const params = new URLSearchParams({ select: "user_id,username", username: `ilike.${identifier}` });
    const profileResponse = await serviceFetch(`${supabaseUrl}/rest/v1/profiles?${params.toString()}`, serviceRole);
    if (!profileResponse.ok) {
      console.error("auth-login profile lookup failed", profileResponse.status);
      return reply(503, { code: "SERVICE_UNAVAILABLE" });
    }
    const profiles = await profileResponse.json() as Array<{ user_id?: string; username?: string }>;
    const profile = profiles.find((item) => item.username?.toLowerCase() === identifier.toLowerCase());
    if (!profile?.user_id) return reply(401, { code: "INVALID_CREDENTIALS" });

    const userResponse = await serviceFetch(`${supabaseUrl}/auth/v1/admin/users/${encodeURIComponent(profile.user_id)}`, serviceRole);
    if (!userResponse.ok) {
      if (userResponse.status === 404) return reply(401, { code: "INVALID_CREDENTIALS" });
      console.error("auth-login admin user lookup failed", userResponse.status);
      return reply(503, { code: "SERVICE_UNAVAILABLE" });
    }
    const adminUser = await userResponse.json() as { email?: string };
    if (!adminUser.email) return reply(401, { code: "INVALID_CREDENTIALS" });
    email = adminUser.email;
  }

  const authResponse = await fetch(`${supabaseUrl}/auth/v1/token?grant_type=password`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      "apikey": anonKey,
    },
    body: JSON.stringify({ email, password }),
  });

  if (!authResponse.ok) {
    if (authResponse.status === 429) return reply(429, { code: "RATE_LIMITED" });
    if (authResponse.status >= 500) return reply(503, { code: "SERVICE_UNAVAILABLE" });
    return reply(401, { code: "INVALID_CREDENTIALS" });
  }

  const auth = await authResponse.json() as {
    access_token?: string;
    refresh_token?: string;
    expires_in?: number;
    expires_at?: number;
    user?: { id?: string; email?: string; email_confirmed_at?: string | null };
  };
  if (!auth.access_token || !auth.refresh_token || !auth.user?.id || !auth.user.email) {
    console.error("auth-login received incomplete auth session");
    return reply(503, { code: "SERVICE_UNAVAILABLE" });
  }

  const profileParams = new URLSearchParams({ select: "username", user_id: `eq.${auth.user.id}`, limit: "1" });
  const profileResponse = await serviceFetch(`${supabaseUrl}/rest/v1/profiles?${profileParams.toString()}`, serviceRole);
  let username = "User";
  if (profileResponse.ok) {
    const rows = await profileResponse.json() as Array<{ username?: string }>;
    if (rows[0]?.username) username = rows[0].username;
  }

  return reply(200, {
    access_token: auth.access_token,
    refresh_token: auth.refresh_token,
    expires_in: auth.expires_in ?? 3600,
    expires_at: auth.expires_at ?? null,
    user: {
      id: auth.user.id,
      username,
      masked_email: maskEmail(auth.user.email),
      email_verified: Boolean(auth.user.email_confirmed_at),
    },
  });
});

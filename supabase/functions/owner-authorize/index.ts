import "jsr:@supabase/functions-js/edge-runtime.d.ts";

const headers = {
  "Content-Type": "application/json; charset=utf-8",
  "Cache-Control": "no-store",
};
const allowedCapabilities = new Set([
  "VIEW_OWNER_PROFILE",
  "VIEW_OWNER_TOOLS",
  "MANAGE_CONTENT",
  "MANAGE_SCHEDULE",
  "VIEW_ANALYTICS",
  "VIEW_SECURITY_AUDIT",
  "MANAGE_PRIVACY",
  "MANAGE_BACKUPS",
]);

function reply(status: number, body: unknown) {
  return new Response(JSON.stringify(body), { status, headers });
}

Deno.serve(async (req: Request) => {
  if (req.method !== "POST") return reply(405, { code: "METHOD_NOT_ALLOWED" });

  const supabaseUrl = Deno.env.get("SUPABASE_URL");
  const serviceRole = Deno.env.get("SUPABASE_SERVICE_ROLE_KEY");
  if (!supabaseUrl || !serviceRole) return reply(503, { code: "SERVICE_UNAVAILABLE" });

  const authorization = req.headers.get("Authorization") ?? "";
  const match = authorization.match(/^Bearer\s+(.+)$/i);
  if (!match) return reply(401, { code: "UNAUTHORIZED" });
  const accessToken = match[1];

  let body: { capability?: unknown };
  try { body = await req.json(); } catch { return reply(400, { code: "INVALID_REQUEST" }); }
  const capability = typeof body.capability === "string" ? body.capability : "";
  if (!allowedCapabilities.has(capability)) return reply(400, { code: "INVALID_CAPABILITY" });

  const userResponse = await fetch(`${supabaseUrl}/auth/v1/user`, {
    headers: {
      apikey: serviceRole,
      Authorization: `Bearer ${accessToken}`,
      Accept: "application/json",
    },
  });
  if (!userResponse.ok) {
    if (userResponse.status === 401 || userResponse.status === 403) return reply(401, { code: "UNAUTHORIZED" });
    return reply(503, { code: "SERVICE_UNAVAILABLE" });
  }
  const user = await userResponse.json() as { id?: string };
  if (!user.id) return reply(401, { code: "UNAUTHORIZED" });

  const rpcResponse = await fetch(`${supabaseUrl}/rest/v1/rpc/internal_owner_authorize`, {
    method: "POST",
    headers: {
      apikey: serviceRole,
      Authorization: `Bearer ${serviceRole}`,
      "Content-Type": "application/json",
      Accept: "application/json",
    },
    body: JSON.stringify({ p_user_id: user.id, p_capability: capability }),
  });
  if (!rpcResponse.ok) {
    console.error("owner-authorize rpc failed", rpcResponse.status);
    return reply(503, { code: "SERVICE_UNAVAILABLE" });
  }

  const decision = await rpcResponse.json();
  if (!decision?.allowed) return reply(403, { allowed: false });
  return reply(200, decision);
});

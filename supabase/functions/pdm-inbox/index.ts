import { createClient } from 'npm:@supabase/supabase-js@2.112.4'

const json = (body: unknown, status = 200) => new Response(JSON.stringify(body), {
  status,
  headers: { 'Content-Type': 'application/json', 'Cache-Control': 'no-store' },
})

type CapabilityRow = { user_id: string; verified_age_tier: string; can_use_dms: boolean }
type MemberRow = { thread_id: string; user_id: string; joined_at: string; last_read_at?: string | null; archived_at?: string | null }
type ProfileRow = { user_id: string; username: string; display_name: string | null; avatar_path: string | null }
type ThreadRow = { id: string; updated_at: string }
type MessageRow = { id: string; thread_id: string; sender_id: string; body: string; created_at: string; expires_at: string | null }
type SettingsRow = { user_id: string; allow_dm_from_connections: boolean }
type ConnectionRow = { requester_id: string; addressee_id: string; status: string }

Deno.serve(async (req: Request) => {
  if (req.method !== 'GET') return json({ error: 'Method not allowed' }, 405)
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
  const admin = createClient(supabaseUrl, serviceKey, { auth: { persistSession: false, autoRefreshToken: false } })

  const { data: userData, error: userError } = await userClient.auth.getUser()
  const user = userData.user
  if (userError || !user) return json({ error: 'Unauthorized' }, 401)

  const { data: callerCapability, error: capabilityError } = await admin
    .from('account_capabilities')
    .select('user_id,verified_age_tier,can_use_dms')
    .eq('user_id', user.id)
    .maybeSingle()
  if (capabilityError) return json({ error: 'Could not verify messaging access' }, 500)
  if (!callerCapability || callerCapability.verified_age_tier !== '16_plus' || callerCapability.can_use_dms !== true) {
    return json({ error: 'Messaging is unavailable for this account' }, 403)
  }

  const { data: ownMemberships, error: ownMembershipError } = await admin
    .from('dm_members')
    .select('thread_id,user_id,joined_at,last_read_at,archived_at')
    .eq('user_id', user.id)
    .order('joined_at', { ascending: false })
    .limit(100)
  if (ownMembershipError) return json({ error: 'Could not load conversations' }, 500)

  const ownMembershipByThread = new Map<string, MemberRow>(
    ((ownMemberships ?? []) as MemberRow[]).map((row) => [row.thread_id, row]),
  )
  const threadIds = [...ownMembershipByThread.keys()]
  if (threadIds.length === 0) return json({ threads: [] })

  const [{ data: threads, error: threadError }, { data: members, error: memberError }] = await Promise.all([
    admin.from('dm_threads').select('id,updated_at').in('id', threadIds),
    admin.from('dm_members').select('thread_id,user_id,joined_at').in('thread_id', threadIds),
  ])
  if (threadError || memberError) return json({ error: 'Could not load conversations' }, 500)

  const memberRows = (members ?? []) as MemberRow[]
  const participantIds = [...new Set(memberRows.map((row) => row.user_id))]
  const [capabilityResult, blockResult, settingsResult, connectionsResult] = await Promise.all([
    admin.from('account_capabilities').select('user_id,verified_age_tier,can_use_dms').in('user_id', participantIds),
    admin.from('user_blocks').select('blocker_id,blocked_id').or(`blocker_id.eq.${user.id},blocked_id.eq.${user.id}`),
    admin.from('user_settings').select('user_id,allow_dm_from_connections').in('user_id', participantIds),
    admin.from('user_connections').select('requester_id,addressee_id,status').eq('status', 'accepted')
      .or(`requester_id.eq.${user.id},addressee_id.eq.${user.id}`),
  ])
  if (capabilityResult.error || blockResult.error || settingsResult.error || connectionsResult.error) {
    return json({ error: 'Could not verify conversation safety' }, 500)
  }

  const capabilityByUser = new Map<string, CapabilityRow>(((capabilityResult.data ?? []) as CapabilityRow[]).map((row) => [row.user_id, row]))
  const settingsByUser = new Map<string, SettingsRow>(((settingsResult.data ?? []) as SettingsRow[]).map((row) => [row.user_id, row]))
  const blockedPairs = new Set<string>()
  for (const block of blockResult.data ?? []) {
    const blocker = block.blocker_id as string
    const blocked = block.blocked_id as string
    if (blocker === user.id) blockedPairs.add(blocked)
    if (blocked === user.id) blockedPairs.add(blocker)
  }
  const acceptedConnections = new Set<string>()
  for (const connection of (connectionsResult.data ?? []) as ConnectionRow[]) {
    if (connection.status !== 'accepted') continue
    if (connection.requester_id === user.id) acceptedConnections.add(connection.addressee_id)
    if (connection.addressee_id === user.id) acceptedConnections.add(connection.requester_id)
  }

  const membersByThread = new Map<string, MemberRow[]>()
  for (const member of memberRows) {
    const current = membersByThread.get(member.thread_id) ?? []
    current.push(member)
    membersByThread.set(member.thread_id, current)
  }

  const safeThreadIds = threadIds.filter((threadId) => {
    const threadMembers = membersByThread.get(threadId) ?? []
    // Group conversation authorization has not yet been verified end-to-end; fail closed for now.
    if (threadMembers.length !== 2 || !threadMembers.some((member) => member.user_id === user.id)) return false
    const other = threadMembers.find((member) => member.user_id !== user.id)
    if (!other || !acceptedConnections.has(other.user_id)) return false
    return threadMembers.every((member) => {
      const capability = capabilityByUser.get(member.user_id)
      const settings = settingsByUser.get(member.user_id)
      return capability?.verified_age_tier === '16_plus' &&
        capability.can_use_dms === true &&
        settings?.allow_dm_from_connections === true &&
        !blockedPairs.has(member.user_id)
    })
  })
  if (safeThreadIds.length === 0) return json({ threads: [] })

  const safeParticipantIds = [...new Set(safeThreadIds.flatMap((id) => (membersByThread.get(id) ?? []).map((member) => member.user_id)))]
  const { data: profiles, error: profileError } = await admin
    .from('profiles')
    .select('user_id,username,display_name,avatar_path')
    .in('user_id', safeParticipantIds)
  if (profileError) return json({ error: 'Could not load conversation profiles' }, 500)
  const profileByUser = new Map<string, ProfileRow>(((profiles ?? []) as ProfileRow[]).map((profile) => [profile.user_id, profile]))

  const latestByThread = new Map<string, MessageRow | null>()
  const unreadByThread = new Map<string, number>()
  let messageLookupFailed = false
  await Promise.all(safeThreadIds.map(async (threadId) => {
    const now = new Date().toISOString()
    const ownMembership = ownMembershipByThread.get(threadId)
    if (!ownMembership) { messageLookupFailed = true; return }

    let latestQuery = admin
      .from('dm_messages')
      .select('id,thread_id,sender_id,body,created_at,expires_at')
      .eq('thread_id', threadId)
      .or(`expires_at.is.null,expires_at.gt.${now}`)
      .order('created_at', { ascending: false })
      .limit(1)
      .maybeSingle()

    let unreadQuery = admin
      .from('dm_messages')
      .select('id', { count: 'exact', head: true })
      .eq('thread_id', threadId)
      .neq('sender_id', user.id)
      .or(`expires_at.is.null,expires_at.gt.${now}`)
    if (ownMembership.last_read_at) unreadQuery = unreadQuery.gt('created_at', ownMembership.last_read_at)

    const [latestResult, unreadResult] = await Promise.all([latestQuery, unreadQuery])
    if (latestResult.error || unreadResult.error) { messageLookupFailed = true; return }
    latestByThread.set(threadId, (latestResult.data as MessageRow | null) ?? null)
    unreadByThread.set(threadId, unreadResult.count ?? 0)
  }))
  if (
    messageLookupFailed ||
    latestByThread.size !== safeThreadIds.length ||
    unreadByThread.size !== safeThreadIds.length
  ) return json({ error: 'Could not load recent messages' }, 500)

  const threadById = new Map<string, ThreadRow>(((threads ?? []) as ThreadRow[]).map((thread) => [thread.id, thread]))
  const result = safeThreadIds.flatMap((threadId) => {
    const thread = threadById.get(threadId)
    const ownMembership = ownMembershipByThread.get(threadId)
    if (!thread || !ownMembership) return []
    const threadMembers = membersByThread.get(threadId) ?? []
    const other = threadMembers.find((member) => member.user_id !== user.id)
    const otherProfile = other ? profileByUser.get(other.user_id) : null
    const latest = latestByThread.get(threadId) ?? null
    return [{
      thread_id: threadId,
      updated_at: thread.updated_at,
      is_group: false,
      is_friend: true,
      participant_count: threadMembers.length,
      title: otherProfile ? (otherProfile.display_name?.trim() || otherProfile.username || 'Conversation') : 'Conversation',
      avatar_path: otherProfile?.avatar_path ?? null,
      last_message_id: latest?.id ?? null,
      last_message_body: latest?.body ?? null,
      last_message_sender_id: latest?.sender_id ?? null,
      last_message_created_at: latest?.created_at ?? null,
      last_message_expires_at: latest?.expires_at ?? null,
      unread_count: unreadByThread.get(threadId) ?? 0,
      archived: ownMembership.archived_at != null,
    }]
  })
  result.sort((a, b) => Date.parse(b.last_message_created_at ?? b.updated_at) - Date.parse(a.last_message_created_at ?? a.updated_at))
  return json({ threads: result })
})
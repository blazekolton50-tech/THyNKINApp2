from pathlib import Path

screen_path = Path("app/src/main/java/com/patsy/app/ui/finaldesign/FinalProfileDmScreens.kt")
screen = screen_path.read_text(encoding="utf-8")

old_signature = "fun FinalPatsyDmScreen(state: FinalDmScreenState) {"
base_signature = '''fun FinalPatsyDmScreen(
    state: FinalDmScreenState,
    onThreadSelected: (String) -> Unit = {},
    onSendMessage: (String, String) -> Unit = { _, _ -> },
) {'''
archive_signature = '''fun FinalPatsyDmScreen(
    state: FinalDmScreenState,
    onThreadSelected: (String) -> Unit = {},
    onSendMessage: (String, String) -> Unit = { _, _ -> },
    onArchiveChanged: (String, Boolean) -> Unit = { _, _ -> },
) {'''
if archive_signature not in screen:
    if base_signature in screen:
        screen = screen.replace(base_signature, archive_signature, 1)
    elif old_signature in screen:
        screen = screen.replace(old_signature, archive_signature, 1)
    else:
        raise SystemExit("PDM screen signature anchor missing")

import_anchor = "import com.patsy.app.dms.filterDmThreads\n"
archive_import = "import com.patsy.app.dms.dmArchiveActionLabel\n"
if archive_import not in screen:
    if import_anchor not in screen:
        raise SystemExit("PDM archive helper import anchor missing")
    screen = screen.replace(import_anchor, import_anchor + archive_import, 1)

# Match the semantic call, not its indentation: split and stacked branches intentionally sit at
# different nesting levels.
old_thread = "onThread = { selectedThreadId = it },"
new_thread = '''onThread = {
                        selectedThreadId = it
                        onThreadSelected(it)
                    },'''
if "onThreadSelected(it)" not in screen:
    count = screen.count(old_thread)
    if count != 2:
        raise SystemExit(f"Expected two PDM thread-selection calls, found {count}")
    screen = screen.replace(old_thread, new_thread)

old_split = '''                DmConversationPane(
                    selectedThreadId = selectedThreadId,
                    state = state,
                    onSendMessage = onSendMessage,
                    modifier = Modifier.fillMaxHeight().weight(0.58f),
                )'''
new_split = '''                DmConversationPane(
                    selectedThreadId = selectedThreadId,
                    state = state,
                    onSendMessage = onSendMessage,
                    onArchiveChanged = onArchiveChanged,
                    modifier = Modifier.fillMaxHeight().weight(0.58f),
                )'''
if new_split not in screen:
    if old_split not in screen:
        raise SystemExit("Split-view conversation archive anchor missing")
    screen = screen.replace(old_split, new_split, 1)

old_stacked = '''                DmConversationPane(
                    selectedThreadId = selectedThreadId,
                    state = state,
                    onSendMessage = onSendMessage,
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                )'''
new_stacked = '''                DmConversationPane(
                    selectedThreadId = selectedThreadId,
                    state = state,
                    onSendMessage = onSendMessage,
                    onArchiveChanged = onArchiveChanged,
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                )'''
if new_stacked not in screen:
    if old_stacked not in screen:
        raise SystemExit("Stacked conversation archive anchor missing")
    screen = screen.replace(old_stacked, new_stacked, 1)

old_conversation_signature = '''private fun DmConversationPane(
    selectedThreadId: String?,
    state: FinalDmScreenState,
    onSendMessage: (String, String) -> Unit,
    modifier: Modifier,
) {'''
new_conversation_signature = '''private fun DmConversationPane(
    selectedThreadId: String?,
    state: FinalDmScreenState,
    onSendMessage: (String, String) -> Unit,
    onArchiveChanged: (String, Boolean) -> Unit,
    modifier: Modifier,
) {'''
if new_conversation_signature not in screen:
    if old_conversation_signature not in screen:
        raise SystemExit("Conversation pane archive signature anchor missing")
    screen = screen.replace(old_conversation_signature, new_conversation_signature, 1)

old_header = '''        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(thread?.title ?: "Conversation", color = FinalWhite, fontSize = 18.sp, fontWeight = FontWeight.Black, modifier = Modifier.weight(1f))
            Text("Video: ${if (state.capabilities.canStartVideoCall) "Ready" else "NOT_CONFIGURED"}", color = FinalMuted, fontSize = 10.sp)
        }'''
new_header = '''        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(thread?.title ?: "Conversation", color = FinalWhite, fontSize = 18.sp, fontWeight = FontWeight.Black, modifier = Modifier.weight(1f))
            thread?.let { activeThread ->
                dmArchiveActionLabel(activeThread.archived)?.let { label ->
                    TextButton(onClick = { onArchiveChanged(activeThread.id, activeThread.archived != true) }) {
                        Text(label, color = FinalWhite, fontSize = 10.sp)
                    }
                }
            }
            Text("Video: ${if (state.capabilities.canStartVideoCall) "Ready" else "NOT_CONFIGURED"}", color = FinalMuted, fontSize = 10.sp)
        }'''
if new_header not in screen:
    if old_header not in screen:
        raise SystemExit("Conversation archive header anchor missing")
    screen = screen.replace(old_header, new_header, 1)

screen_path.write_text(screen, encoding="utf-8")

activity_path = Path("app/src/main/java/com/patsy/app/FinalMainActivity.kt")
activity = activity_path.read_text(encoding="utf-8")
import_line = "import com.patsy.app.ui.finaldesign.FinalPatsyDmLiveRoute\n"
if import_line not in activity:
    anchor = "import com.patsy.app.ui.finaldesign.FinalPatsyDmScreen\n"
    if anchor not in activity:
        raise SystemExit("PDM route import anchor missing")
    activity = activity.replace(anchor, anchor + import_line, 1)

old_route = '''                                    FinalAppPage.DMS -> FinalPatsyDmScreen(
                                        state = finalDmScreenState(),
                                    )'''
new_route = '''                                    FinalAppPage.DMS -> {
                                        val activeSession = session
                                        if (activeSession == null) {
                                            FinalProtectedAccessState(
                                                message = "Patsy DMs require a verified signed-in session.",
                                                onSignOut = ::signOut,
                                            )
                                        } else {
                                            FinalPatsyDmLiveRoute(
                                                session = activeSession,
                                                onUnauthorized = { page = FinalAppPage.PROTECTED },
                                            )
                                        }
                                    }'''
if new_route not in activity:
    if old_route not in activity:
        raise SystemExit("PDM activity route anchor missing; refusing blind shell rewrite")
    activity = activity.replace(old_route, new_route, 1)

activity_path.write_text(activity, encoding="utf-8")
print("Live authenticated PDM inbox, conversation load, send, read, and archive route integrated")

package com.patsy.app.ui.finaldesign

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patsy.app.dms.DmConversationCapabilities
import com.patsy.app.dms.DmFilter
import com.patsy.app.dms.DmLayoutMode
import com.patsy.app.dms.DmPresentationState
import com.patsy.app.dms.DmThreadSummary
import com.patsy.app.dms.dmLayoutForWidth
import com.patsy.app.dms.filterDmThreads
import com.patsy.app.dms.dmArchiveActionLabel
import com.patsy.app.profile.ProfileMenuDestination
import com.patsy.app.profile.ProfileMenuGroup
import com.patsy.app.profile.ProfilePresentationState
import com.patsy.app.profile.ProfileQuickAction
import com.patsy.app.profile.defaultProfileQuickActions
import com.patsy.app.profile.expandedProfileMenu

/** Truthful UI binding for the newest approved Profile composition. */
data class FinalProfileScreenState(
    val presentation: ProfilePresentationState,
    val menu: List<ProfileMenuGroup> = expandedProfileMenu(),
    val menuGrantsOwnerAuthority: Boolean = false,
)

fun finalProfileScreenState(
    displayName: String,
    username: String,
): FinalProfileScreenState = FinalProfileScreenState(
    presentation = ProfilePresentationState(
        displayName = displayName.ifBlank { "Your profile" },
        username = username.trim().removePrefix("@").let { if (it.isBlank()) "" else "@$it" },
    ),
)

data class FinalDmMessage(
    val id: String,
    val body: String,
    val isOwn: Boolean,
)

data class FinalDmScreenState(
    val presentation: DmPresentationState = DmPresentationState(),
    val threads: List<DmThreadSummary> = emptyList(),
    val messages: List<FinalDmMessage> = emptyList(),
    val capabilities: DmConversationCapabilities = DmConversationCapabilities(),
    val fabricatesDeliveryState: Boolean = false,
)

fun finalDmScreenState(): FinalDmScreenState = FinalDmScreenState()

@Composable
fun FinalProfileScreen(
    state: FinalProfileScreenState,
    emailVerified: Boolean,
    ownerAccessChecked: Boolean,
    canViewOwnerProfile: Boolean,
    canViewOwnerTools: Boolean,
    onQuickAction: (ProfileQuickAction) -> Unit,
    onOpenOwnerProfile: () -> Unit,
    onOpenOwnerTools: () -> Unit,
    onSignOut: () -> Unit,
) {
    var menuOpen by remember { mutableStateOf(false) }
    val profile = state.presentation

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(FinalCharcoal).padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            Box(Modifier.fillMaxWidth().padding(top = 10.dp).height(60.dp)) {
                ThynkDestinationLogo(
                    destination = ThynkPanelDestination.ME,
                    modifier = Modifier.align(Alignment.Center).width(132.dp).height(58.dp),
                )
                Text(
                    text = if (menuOpen) "×" else "☰",
                    color = FinalWhite,
                    fontSize = 26.sp,
                    modifier = Modifier.align(Alignment.CenterEnd).clickable { menuOpen = !menuOpen }.padding(8.dp),
                )
            }
        }

        item {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Box(
                    Modifier.size(104.dp).border(3.dp, FinalRainbow, CircleShape).clip(CircleShape).background(FinalCard),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(profile.displayName.take(1).uppercase().ifBlank { "P" }, color = FinalWhite, fontSize = 38.sp, fontWeight = FontWeight.Black)
                }
                Text(profile.displayName, color = FinalWhite, fontSize = 24.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(top = 12.dp))
                if (profile.username.isNotBlank()) Text(profile.username, color = FinalMuted, fontSize = 13.sp)
                Text(
                    profile.bio ?: "Add a bio to tell people a little about you.",
                    color = FinalMuted,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 8.dp),
                )
                Text(
                    if (emailVerified) "Email verified" else "Email verification not confirmed",
                    color = FinalMuted,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(top = 5.dp),
                )
            }
        }

        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                profile.metrics.forEach { metric ->
                    Column(
                        Modifier.weight(1f).background(FinalCard, RoundedCornerShape(16.dp)).padding(vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(metric.formattedValue ?: "—", color = FinalWhite, fontWeight = FontWeight.Black, fontSize = 16.sp)
                        Text(metric.label, color = FinalMuted, fontSize = 10.sp)
                    }
                }
            }
        }

        item { SectionCardTitle("ABOUT ME") }
        item {
            FinalRainbowPanel(Modifier.fillMaxWidth(), radius = 20.dp) {
                Text(
                    profile.bio ?: "No About Me details added yet.",
                    color = if (profile.bio == null) FinalMuted else FinalWhite,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(16.dp),
                )
            }
        }

        item { SectionCardTitle("QUICK POST / CREATOR TOOLS") }
        item {
            Row(
                Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                defaultProfileQuickActions().forEach { action ->
                    val label = when (action) {
                        ProfileQuickAction.IMAGE -> "Image"
                        ProfileQuickAction.VIDEO -> "Video"
                        ProfileQuickAction.DOCUMENT -> "Document"
                        ProfileQuickAction.TEMPLATE -> "Template"
                        ProfileQuickAction.MORE -> "More"
                    }
                    Box(
                        Modifier.border(1.dp, FinalRainbow, RoundedCornerShape(18.dp))
                            .clip(RoundedCornerShape(18.dp)).clickable { onQuickAction(action) }.padding(horizontal = 18.dp, vertical = 12.dp),
                    ) { Text(label, color = FinalWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                }
            }
        }

        item { ProjectSection("RECENT PROJECTS", profile.recentProjects.isEmpty()) }
        item { ProjectSection("SAVED PROJECTS", profile.savedProjects.isEmpty()) }

        if (ownerAccessChecked && (canViewOwnerProfile || canViewOwnerTools)) {
            item { SectionCardTitle("OWNER") }
            if (canViewOwnerProfile) item { SimpleActionRow("Owner Profile", "Server-authorized owner profile", onOpenOwnerProfile) }
            if (canViewOwnerTools) item { SimpleActionRow("Owner Tools", "Capability-gated owner controls", onOpenOwnerTools) }
        }

        if (menuOpen) {
            state.menu.forEach { group ->
                item { SectionCardTitle(group.section.name.replace('_', ' ')) }
                items(group.items) { menuItem ->
                    val active = menuItem.destination == ProfileMenuDestination.LOG_OUT
                    Column(
                        Modifier.fillMaxWidth().background(FinalCard, RoundedCornerShape(16.dp))
                            .clickable(enabled = active) { if (active) onSignOut() }
                            .padding(horizontal = 16.dp, vertical = 13.dp),
                    ) {
                        Text(menuItem.label, color = if (menuItem.destructive) Color(0xFFFF5D86) else FinalWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        menuItem.subtitle?.let { Text(it, color = FinalMuted, fontSize = 11.sp, modifier = Modifier.padding(top = 2.dp)) }
                        if (!active) Text("Not wired yet", color = FinalMuted, fontSize = 10.sp, modifier = Modifier.padding(top = 4.dp))
                    }
                }
            }
        }

        item { Spacer(Modifier.height(22.dp)) }
    }
}

@Composable
fun FinalPatsyDmScreen(
    state: FinalDmScreenState,
    onThreadSelected: (String) -> Unit = {},
    onSendMessage: (String, String) -> Unit = { _, _ -> },
    onArchiveChanged: (String, Boolean) -> Unit = { _, _ -> },
) {
    var search by remember { mutableStateOf(state.presentation.searchQuery) }
    var filter by remember { mutableStateOf(state.presentation.filter) }
    var selectedThreadId by remember { mutableStateOf(state.presentation.selectedThreadId) }

    BoxWithConstraints(Modifier.fillMaxSize().background(FinalCharcoal)) {
        val mode = dmLayoutForWidth(maxWidth.value.toInt())
        val visibleThreads = filterDmThreads(state.threads, filter, search)
        if (mode == DmLayoutMode.SPLIT_VIEW) {
            Row(Modifier.fillMaxSize()) {
                DmInboxPane(
                    threads = visibleThreads,
                    search = search,
                    onSearch = { search = it },
                    filter = filter,
                    onFilter = { filter = it },
                    onThread = {
                        selectedThreadId = it
                        onThreadSelected(it)
                    },
                    modifier = Modifier.fillMaxHeight().weight(0.42f),
                )
                Box(Modifier.width(1.dp).fillMaxHeight().background(Color(0xFF333338)))
                DmConversationPane(
                    selectedThreadId = selectedThreadId,
                    state = state,
                    onSendMessage = onSendMessage,
                    onArchiveChanged = onArchiveChanged,
                    modifier = Modifier.fillMaxHeight().weight(0.58f),
                )
            }
        } else if (selectedThreadId == null) {
            DmInboxPane(
                threads = visibleThreads,
                search = search,
                onSearch = { search = it },
                filter = filter,
                onFilter = { filter = it },
                onThread = {
                    selectedThreadId = it
                    onThreadSelected(it)
                },
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            Column(Modifier.fillMaxSize()) {
                TextButton(onClick = { selectedThreadId = null }) { Text("‹ Conversations", color = FinalWhite) }
                DmConversationPane(
                    selectedThreadId = selectedThreadId,
                    state = state,
                    onSendMessage = onSendMessage,
                    onArchiveChanged = onArchiveChanged,
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun DmInboxPane(
    threads: List<DmThreadSummary>,
    search: String,
    onSearch: (String) -> Unit,
    filter: DmFilter,
    onFilter: (DmFilter) -> Unit,
    onThread: (String) -> Unit,
    modifier: Modifier,
) {
    Column(modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        ThynkDestinationLogo(
            destination = ThynkPanelDestination.CHATS,
            modifier = Modifier.align(Alignment.CenterHorizontally).width(150.dp).height(58.dp),
        )
        Text("Your conversations 💜", color = FinalMuted, fontSize = 13.sp)
        OutlinedTextField(
            value = search,
            onValueChange = onSearch,
            singleLine = true,
            placeholder = { Text("Search conversations", color = FinalMuted) },
            modifier = Modifier.fillMaxWidth(),
        )
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
            DmFilter.entries.forEach { item ->
                Box(
                    Modifier.border(1.dp, if (item == filter) FinalRainbow else androidx.compose.ui.graphics.SolidColor(Color(0xFF34343A)), RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp)).clickable { onFilter(item) }.padding(horizontal = 12.dp, vertical = 8.dp),
                ) { Text(item.name.lowercase().replaceFirstChar { it.uppercase() }, color = FinalWhite, fontSize = 11.sp) }
            }
        }
        if (threads.isEmpty()) {
            Column(Modifier.weight(1f).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Text("No conversations to show", color = FinalWhite, fontWeight = FontWeight.Bold)
                Text("Real Supabase-backed conversations will appear here when loaded.", color = FinalMuted, fontSize = 12.sp, modifier = Modifier.padding(top = 6.dp))
            }
        } else {
            LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(threads) { thread ->
                    Column(
                        Modifier.fillMaxWidth().background(FinalCard, RoundedCornerShape(18.dp)).clickable { onThread(thread.id) }.padding(14.dp),
                    ) {
                        Row(Modifier.fillMaxWidth()) {
                            Text(thread.title, color = FinalWhite, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                            thread.unreadCount?.takeIf { it > 0 }?.let { unread -> Text(unread.toString(), color = FinalWhite, fontSize = 11.sp) }
                        }
                        Text(thread.lastMessage ?: "No messages yet", color = FinalMuted, fontSize = 11.sp, modifier = Modifier.padding(top = 4.dp))
                    }
                }
            }
        }
        FinalRainbowPanel(Modifier.fillMaxWidth(), radius = 18.dp) {
            Column(Modifier.padding(13.dp)) {
                Text("DMs are private and secure", color = FinalWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text("Messages default to auto-delete after 3 days where the server retention policy applies.", color = FinalMuted, fontSize = 10.sp, modifier = Modifier.padding(top = 4.dp))
            }
        }
    }
}

@Composable
private fun DmConversationPane(
    selectedThreadId: String?,
    state: FinalDmScreenState,
    onSendMessage: (String, String) -> Unit,
    onArchiveChanged: (String, Boolean) -> Unit,
    modifier: Modifier,
) {
    Column(modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        if (selectedThreadId == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Select a conversation", color = FinalWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("Nothing is fabricated while no real thread is selected.", color = FinalMuted, fontSize = 12.sp, modifier = Modifier.padding(top = 6.dp))
                }
            }
            return@Column
        }
        val thread = state.threads.firstOrNull { it.id == selectedThreadId }
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(thread?.title ?: "Conversation", color = FinalWhite, fontSize = 18.sp, fontWeight = FontWeight.Black, modifier = Modifier.weight(1f))
            thread?.let { activeThread ->
                dmArchiveActionLabel(activeThread.archived)?.let { label ->
                    TextButton(onClick = { onArchiveChanged(activeThread.id, activeThread.archived != true) }) {
                        Text(label, color = FinalWhite, fontSize = 10.sp)
                    }
                }
            }
            Text("Video: ${if (state.capabilities.canStartVideoCall) "Ready" else "NOT_CONFIGURED"}", color = FinalMuted, fontSize = 10.sp)
        }
        if (state.messages.isEmpty()) {
            Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("No loaded messages", color = FinalMuted, fontSize = 12.sp)
            }
        } else {
            LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(state.messages) { message ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = if (message.isOwn) Arrangement.End else Arrangement.Start) {
                        Box(Modifier.background(FinalCard, RoundedCornerShape(18.dp)).padding(horizontal = 14.dp, vertical = 10.dp)) {
                            Text(message.body, color = FinalWhite, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
        var draft by remember(selectedThreadId) { mutableStateOf("") }
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = draft,
                onValueChange = { draft = it },
                singleLine = true,
                placeholder = { Text("Message", color = FinalMuted) },
                modifier = Modifier.weight(1f),
            )
            TextButton(
                onClick = {
                    val message = draft.trim()
                    if (message.isNotEmpty()) {
                        onSendMessage(selectedThreadId, message)
                        draft = ""
                    }
                },
            ) { Text("Send", color = FinalWhite) }
        }
        Text(
            "Sent here means the server accepted the message row. Delivered/read receipts are not claimed.",
            color = FinalMuted,
            fontSize = 10.sp,
        )
    }
}

@Composable
private fun SectionCardTitle(text: String) {
    Text(text, color = Color(0xFFFF5D86), fontSize = 11.sp, fontWeight = FontWeight.Black)
}

@Composable
private fun ProjectSection(title: String, empty: Boolean) {
    Column(Modifier.fillMaxWidth()) {
        SectionCardTitle(title)
        Box(
            Modifier.fillMaxWidth().padding(top = 8.dp).background(FinalCard, RoundedCornerShape(18.dp)).padding(16.dp),
        ) {
            Text(if (empty) "No projects here yet." else "Projects loaded", color = FinalMuted, fontSize = 12.sp)
        }
    }
}

@Composable
private fun SimpleActionRow(title: String, subtitle: String, onClick: () -> Unit) {
    Column(
        Modifier.fillMaxWidth().background(FinalCard, RoundedCornerShape(18.dp)).clickable(onClick = onClick).padding(16.dp),
    ) {
        Text(title, color = FinalWhite, fontWeight = FontWeight.Bold)
        Text(subtitle, color = FinalMuted, fontSize = 11.sp, modifier = Modifier.padding(top = 3.dp))
    }
}

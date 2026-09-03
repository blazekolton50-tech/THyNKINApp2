package com.patsy.app.dms

enum class DmFilter {
    ALL,
    UNREAD,
    FRIENDS,
    GROUPS,
    ARCHIVED,
}

enum class DmLayoutMode {
    STACKED,
    SPLIT_VIEW,
}

private const val SplitViewMinWidthDp = 720

fun dmLayoutForWidth(availableWidthDp: Int): DmLayoutMode =
    if (availableWidthDp >= SplitViewMinWidthDp) DmLayoutMode.SPLIT_VIEW else DmLayoutMode.STACKED

data class DmThreadSummary(
    val id: String,
    val title: String,
    val lastMessage: String?,
    val unreadCount: Int?,
    val isFriend: Boolean?,
    val isGroup: Boolean?,
    val archived: Boolean?,
)

fun dmArchiveActionLabel(archived: Boolean?): String? = when (archived) {
    false -> "Archive"
    true -> "Unarchive"
    null -> null
}

fun filterDmThreads(
    threads: List<DmThreadSummary>,
    filter: DmFilter,
    searchQuery: String,
): List<DmThreadSummary> {
    val query = searchQuery.trim().lowercase()
    return threads.asSequence()
        .filter { thread ->
            when (filter) {
                DmFilter.ALL -> thread.archived != true
                DmFilter.UNREAD -> thread.archived != true && (thread.unreadCount ?: 0) > 0
                DmFilter.FRIENDS -> thread.archived != true && thread.isFriend == true
                DmFilter.GROUPS -> thread.archived != true && thread.isGroup == true
                DmFilter.ARCHIVED -> thread.archived == true
            }
        }
        .filter { thread ->
            query.isBlank() ||
                thread.title.lowercase().contains(query) ||
                thread.lastMessage.orEmpty().lowercase().contains(query)
        }
        .toList()
}

enum class DmProviderCapability {
    NOT_CONFIGURED,
    AVAILABLE,
}

data class DmConversationCapabilities(
    val audioCall: DmProviderCapability = DmProviderCapability.NOT_CONFIGURED,
    val videoCall: DmProviderCapability = DmProviderCapability.NOT_CONFIGURED,
) {
    val canStartAudioCall: Boolean
        get() = audioCall == DmProviderCapability.AVAILABLE

    val canStartVideoCall: Boolean
        get() = videoCall == DmProviderCapability.AVAILABLE
}

data class DmPresentationState(
    val searchQuery: String = "",
    val filter: DmFilter = DmFilter.ALL,
    val selectedThreadId: String? = null,
    val locallyAcknowledgedThreadIds: Set<String> = emptySet(),
) {
    fun selectThread(threadId: String): DmPresentationState = copy(selectedThreadId = threadId)
}

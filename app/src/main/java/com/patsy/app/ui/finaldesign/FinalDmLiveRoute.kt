package com.patsy.app.ui.finaldesign

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.patsy.app.auth.PublicSession
import com.patsy.app.dms.DmConversationResult
import com.patsy.app.dms.DmDataResult
import com.patsy.app.dms.DmMemberStateResult
import com.patsy.app.dms.DmSendResult
import com.patsy.app.dms.DmServiceBindings
import com.patsy.app.dms.DmThreadSummary
import com.patsy.app.dms.shouldRefreshInboxAfterMemberState
import kotlinx.coroutines.launch

@Composable
fun FinalPatsyDmLiveRoute(
    session: PublicSession,
    onUnauthorized: () -> Unit,
) {
    var screenState by remember(session.sessionId) { mutableStateOf(finalDmScreenState()) }
    val scope = rememberCoroutineScope()

    suspend fun refreshInbox() {
        when (val result = DmServiceBindings.dmDataService.load(session)) {
            is DmDataResult.Loaded -> {
                screenState = screenState.copy(
                    threads = result.threads.map { thread ->
                        DmThreadSummary(
                            id = thread.id,
                            title = thread.title ?: "Conversation",
                            lastMessage = thread.lastMessage?.body,
                            unreadCount = thread.unreadCount,
                            isFriend = thread.isFriend,
                            isGroup = thread.isGroup,
                            archived = thread.archived,
                        )
                    },
                )
            }
            DmDataResult.Unauthorized -> onUnauthorized()
            DmDataResult.Unavailable -> Unit
        }
    }

    fun openThread(threadId: String) {
        scope.launch {
            when (val result = DmServiceBindings.conversationService.load(session, threadId)) {
                is DmConversationResult.Loaded -> {
                    screenState = screenState.copy(
                        presentation = screenState.presentation.selectThread(threadId),
                        messages = result.messages.map { message ->
                            FinalDmMessage(
                                id = message.id,
                                body = message.body,
                                isOwn = message.senderId == session.userId,
                            )
                        },
                    )
                    when (val memberState = DmServiceBindings.memberStateService.markRead(session, threadId)) {
                        DmMemberStateResult.Updated -> {
                            if (shouldRefreshInboxAfterMemberState(memberState)) refreshInbox()
                        }
                        DmMemberStateResult.Unauthorized -> onUnauthorized()
                        DmMemberStateResult.Unavailable -> Unit
                    }
                }
                DmConversationResult.Unauthorized -> onUnauthorized()
                DmConversationResult.Unavailable -> Unit
            }
        }
    }

    fun sendMessage(threadId: String, body: String) {
        scope.launch {
            when (val result = DmServiceBindings.conversationService.send(session, threadId, body)) {
                is DmSendResult.Accepted -> {
                    if (screenState.presentation.selectedThreadId == threadId) {
                        screenState = screenState.copy(
                            messages = screenState.messages + FinalDmMessage(
                                id = result.message.id,
                                body = result.message.body,
                                isOwn = true,
                            ),
                        )
                    }
                    refreshInbox()
                }
                is DmSendResult.InvalidInput -> Unit
                DmSendResult.Unauthorized -> onUnauthorized()
                DmSendResult.Unavailable -> Unit
            }
        }
    }

    fun setArchived(threadId: String, archived: Boolean) {
        scope.launch {
            when (val result = DmServiceBindings.memberStateService.setArchived(session, threadId, archived)) {
                DmMemberStateResult.Updated -> {
                    if (screenState.presentation.selectedThreadId == threadId) {
                        screenState = screenState.copy(
                            presentation = screenState.presentation.copy(selectedThreadId = null),
                            messages = emptyList(),
                        )
                    }
                    if (shouldRefreshInboxAfterMemberState(result)) refreshInbox()
                }
                DmMemberStateResult.Unauthorized -> onUnauthorized()
                DmMemberStateResult.Unavailable -> Unit
            }
        }
    }

    LaunchedEffect(session.sessionId) {
        refreshInbox()
    }

    FinalPatsyDmScreen(
        state = screenState,
        onThreadSelected = ::openThread,
        onSendMessage = ::sendMessage,
        onArchiveChanged = ::setArchived,
    )
}

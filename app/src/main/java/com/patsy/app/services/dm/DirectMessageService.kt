package com.patsy.app.services.dm

import com.patsy.app.services.AuthenticatedContext
import com.patsy.app.services.Page
import com.patsy.app.services.ServiceResult

data class DirectMessageThread(
    val id: String,
    val participantUserIds: Set<String>,
    val lastActivityEpochMillis: Long,
    val unreadCount: Int
)

data class DirectMessage(
    val id: String,
    val threadId: String,
    val senderUserId: String,
    val body: String,
    val sentAtEpochMillis: Long
)

enum class MessageReportReason { HARASSMENT, SPAM, UNSAFE_CONTENT, OTHER }

/**
 * Implementations must revalidate the session, relationship/block state and
 * context.safety.directMessages on the trusted backend for every operation.
 */
interface DirectMessageService {
    suspend fun listThreads(
        context: AuthenticatedContext,
        cursor: String? = null
    ): ServiceResult<Page<DirectMessageThread>>

    suspend fun openThread(
        context: AuthenticatedContext,
        otherUserId: String
    ): ServiceResult<DirectMessageThread>

    suspend fun listMessages(
        context: AuthenticatedContext,
        threadId: String,
        cursor: String? = null
    ): ServiceResult<Page<DirectMessage>>

    suspend fun sendMessage(
        context: AuthenticatedContext,
        threadId: String,
        body: String,
        clientRequestId: String
    ): ServiceResult<DirectMessage>

    suspend fun blockUser(
        context: AuthenticatedContext,
        userId: String
    ): ServiceResult<Unit>

    suspend fun reportMessage(
        context: AuthenticatedContext,
        messageId: String,
        reason: MessageReportReason,
        details: String? = null
    ): ServiceResult<Unit>
}

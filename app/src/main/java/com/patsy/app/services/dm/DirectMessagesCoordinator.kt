package com.patsy.app.services.dm

import com.patsy.app.services.AuthenticatedContext
import com.patsy.app.services.Capability
import com.patsy.app.services.Page
import com.patsy.app.services.state.FeatureRequestPolicy
import com.patsy.app.services.state.FeatureUiState
import com.patsy.app.services.state.callFeatureService

/** App-side state holder; all security and relationship decisions remain server-authoritative. */
class DirectMessagesCoordinator(
    private val service: DirectMessageService,
) {
    var inboxState: FeatureUiState<Page<DirectMessageThread>> = FeatureUiState.Idle
        private set

    var threadState: FeatureUiState<Page<DirectMessage>> = FeatureUiState.Idle
        private set

    var sendState: FeatureUiState<DirectMessage> = FeatureUiState.Idle
        private set

    var threadAccessState: FeatureUiState<DirectMessageThread> = FeatureUiState.Idle
        private set

    var moderationState: FeatureUiState<Unit> = FeatureUiState.Idle
        private set

    suspend fun loadInbox(context: AuthenticatedContext, cursor: String? = null) {
        val denial = FeatureRequestPolicy.denial(context, Capability.DIRECT_MESSAGES)
        if (denial != null) {
            inboxState = denial
            return
        }
        val scope = FeatureRequestPolicy.scope(context)
        inboxState = FeatureUiState.Loading(scope)
        inboxState = callFeatureService(scope) { service.listThreads(context, cursor) }
    }

    suspend fun openThread(context: AuthenticatedContext, otherUserId: String) {
        val denial = FeatureRequestPolicy.denial(context, Capability.DIRECT_MESSAGES)
        if (denial != null) {
            threadAccessState = denial
            return
        }
        val scope = FeatureRequestPolicy.scope(context)
        threadAccessState = FeatureUiState.Loading(scope)
        threadAccessState = callFeatureService(scope) { service.openThread(context, otherUserId) }
    }

    suspend fun loadMessages(
        context: AuthenticatedContext,
        threadId: String,
        cursor: String? = null,
    ) {
        val denial = FeatureRequestPolicy.denial(context, Capability.DIRECT_MESSAGES)
        if (denial != null) {
            threadState = denial
            return
        }
        val scope = FeatureRequestPolicy.scope(context)
        threadState = FeatureUiState.Loading(scope)
        threadState = callFeatureService(scope) { service.listMessages(context, threadId, cursor) }
    }

    suspend fun send(
        context: AuthenticatedContext,
        threadId: String,
        body: String,
        clientRequestId: String,
    ) {
        val denial = FeatureRequestPolicy.denial(context, Capability.DIRECT_MESSAGES)
        if (denial != null) {
            sendState = denial
            return
        }
        val scope = FeatureRequestPolicy.scope(context)
        sendState = FeatureUiState.Loading(scope)
        sendState = callFeatureService(scope) {
            service.sendMessage(context, threadId, body, clientRequestId)
        }
    }

    suspend fun block(context: AuthenticatedContext, userId: String) {
        runModeration(context) { service.blockUser(context, userId) }
    }

    suspend fun report(
        context: AuthenticatedContext,
        messageId: String,
        reason: MessageReportReason,
        details: String? = null,
    ) {
        runModeration(context) { service.reportMessage(context, messageId, reason, details) }
    }

    private suspend fun runModeration(
        context: AuthenticatedContext,
        request: suspend () -> com.patsy.app.services.ServiceResult<Unit>,
    ) {
        val denial = FeatureRequestPolicy.denial(context, Capability.DIRECT_MESSAGES)
        if (denial != null) {
            moderationState = denial
            return
        }
        val scope = FeatureRequestPolicy.scope(context)
        moderationState = FeatureUiState.Loading(scope)
        moderationState = callFeatureService(scope, request)
    }
}

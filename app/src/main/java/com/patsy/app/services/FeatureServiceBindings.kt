package com.patsy.app.services

import com.patsy.app.services.ai.PatsyAiService
import com.patsy.app.services.ai.PatsyReply
import com.patsy.app.services.ai.PatsyReplyRequest
import com.patsy.app.services.ai.SearchRequest
import com.patsy.app.services.ai.SearchResult
import com.patsy.app.services.ai.WebSearchService
import com.patsy.app.services.dm.DirectMessage
import com.patsy.app.services.dm.DirectMessageService
import com.patsy.app.services.dm.DirectMessageThread
import com.patsy.app.services.dm.MessageReportReason
import com.patsy.app.services.scheduling.ScheduleRequest
import com.patsy.app.services.scheduling.ScheduledPost
import com.patsy.app.services.scheduling.SchedulingService
import com.patsy.app.services.studio.CreationJob
import com.patsy.app.services.studio.CreationRequest
import com.patsy.app.services.studio.CreationStudioService

/**
 * Single provider-composition point for non-authenticated feature services.
 *
 * Production builds replace these bindings with HTTPS-backed implementations. Defaults always
 * fail closed; they never queue work, create local DMs, invent AI replies, or report publishing
 * success merely because the client UI accepted a request.
 */
object PatsyFeatureServiceBindings {
    var patsyAiService: PatsyAiService = UnconfiguredPatsyAiService
    var webSearchService: WebSearchService = UnconfiguredWebSearchService
    var directMessageService: DirectMessageService = UnconfiguredDirectMessageService
    var schedulingService: SchedulingService = UnconfiguredSchedulingService
    var creationStudioService: CreationStudioService = UnconfiguredCreationStudioService
}

private fun unavailable(capability: Capability, message: String) = ServiceResult.Unavailable(
    capability = capability,
    reason = UnavailableReason.NOT_CONFIGURED,
    retryable = false,
    publicMessage = message,
)

private object UnconfiguredPatsyAiService : PatsyAiService {
    override suspend fun reply(
        context: AuthenticatedContext,
        request: PatsyReplyRequest,
    ): ServiceResult<PatsyReply> = unavailable(
        Capability.PATSY_AI,
        "Patsy's secure AI service is not configured yet.",
    )
}

private object UnconfiguredWebSearchService : WebSearchService {
    override suspend fun search(
        context: AuthenticatedContext,
        request: SearchRequest,
    ): ServiceResult<SearchResult> = unavailable(
        Capability.WEB_SEARCH,
        "Secure web search is not configured yet.",
    )
}

private object UnconfiguredDirectMessageService : DirectMessageService {
    override suspend fun listThreads(context: AuthenticatedContext, cursor: String?) =
        unavailable(Capability.DIRECT_MESSAGES, "Direct messaging is not configured yet.")

    override suspend fun openThread(context: AuthenticatedContext, otherUserId: String) =
        unavailable(Capability.DIRECT_MESSAGES, "Direct messaging is not configured yet.")

    override suspend fun listMessages(
        context: AuthenticatedContext,
        threadId: String,
        cursor: String?,
    ): ServiceResult<Page<DirectMessage>> =
        unavailable(Capability.DIRECT_MESSAGES, "Direct messaging is not configured yet.")

    override suspend fun sendMessage(
        context: AuthenticatedContext,
        threadId: String,
        body: String,
        clientRequestId: String,
    ): ServiceResult<DirectMessage> =
        unavailable(Capability.DIRECT_MESSAGES, "Direct messaging is not configured yet.")

    override suspend fun blockUser(
        context: AuthenticatedContext,
        userId: String,
    ): ServiceResult<Unit> =
        unavailable(Capability.DIRECT_MESSAGES, "Direct messaging is not configured yet.")

    override suspend fun reportMessage(
        context: AuthenticatedContext,
        messageId: String,
        reason: MessageReportReason,
        details: String?,
    ): ServiceResult<Unit> =
        unavailable(Capability.DIRECT_MESSAGES, "Direct messaging is not configured yet.")
}

private object UnconfiguredSchedulingService : SchedulingService {
    override suspend fun schedule(
        context: AuthenticatedContext,
        request: ScheduleRequest,
    ): ServiceResult<ScheduledPost> =
        unavailable(Capability.SCHEDULING, "Publishing and scheduling are not configured yet.")

    override suspend fun listScheduled(context: AuthenticatedContext, cursor: String?) =
        unavailable(Capability.SCHEDULING, "Publishing and scheduling are not configured yet.")

    override suspend fun getScheduled(context: AuthenticatedContext, scheduledPostId: String) =
        unavailable(Capability.SCHEDULING, "Publishing and scheduling are not configured yet.")

    override suspend fun cancel(context: AuthenticatedContext, scheduledPostId: String) =
        unavailable(Capability.SCHEDULING, "Publishing and scheduling are not configured yet.")
}

private object UnconfiguredCreationStudioService : CreationStudioService {
    override suspend fun create(
        context: AuthenticatedContext,
        request: CreationRequest,
    ): ServiceResult<CreationJob> =
        unavailable(Capability.CREATION_STUDIO, "Creation providers are not configured yet.")

    override suspend fun getJob(context: AuthenticatedContext, jobId: String) =
        unavailable(Capability.CREATION_STUDIO, "Creation providers are not configured yet.")

    override suspend fun listJobs(context: AuthenticatedContext, cursor: String?) =
        unavailable(Capability.CREATION_STUDIO, "Creation providers are not configured yet.")

    override suspend fun cancel(context: AuthenticatedContext, jobId: String) =
        unavailable(Capability.CREATION_STUDIO, "Creation providers are not configured yet.")
}

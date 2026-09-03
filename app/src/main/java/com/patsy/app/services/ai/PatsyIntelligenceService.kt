package com.patsy.app.services.ai

import com.patsy.app.services.AuthenticatedContext
import com.patsy.app.services.ServiceResult

enum class ConversationRole { USER, PATSY }

data class ConversationTurn(
    val role: ConversationRole,
    val text: String
)

data class PatsyReplyRequest(
    val conversationId: String?,
    val turns: List<ConversationTurn>,
    val allowWebSearch: Boolean,
    val clientRequestId: String
)

data class SourceCitation(
    val title: String,
    val url: String,
    val snippet: String? = null
)

data class PatsyReply(
    val conversationId: String,
    val text: String,
    val citations: List<SourceCitation> = emptyList(),
    val usedWebSearch: Boolean,
    val safetyNotice: String? = null
)

data class SearchRequest(
    val query: String,
    val resultLimit: Int = 5
)

data class SearchResult(
    val query: String,
    val sources: List<SourceCitation>
)

/** Patsy is the assistant identity; the backing AI provider remains an implementation detail. */
interface PatsyAiService {
    suspend fun reply(
        context: AuthenticatedContext,
        request: PatsyReplyRequest
    ): ServiceResult<PatsyReply>
}

/** Search is separate so age/safety policy and provider availability cannot be bypassed by AI. */
interface WebSearchService {
    suspend fun search(
        context: AuthenticatedContext,
        request: SearchRequest
    ): ServiceResult<SearchResult>
}

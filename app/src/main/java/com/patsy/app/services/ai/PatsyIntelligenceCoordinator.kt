package com.patsy.app.services.ai

import com.patsy.app.services.AuthenticatedContext
import com.patsy.app.services.Capability
import com.patsy.app.services.ServiceResult
import com.patsy.app.services.state.FeatureRequestPolicy
import com.patsy.app.services.state.FeatureUiState
import com.patsy.app.services.state.callFeatureService

/**
 * Coordinates Patsy AI and explicit web search without letting either path bypass safety policy.
 * Patsy remains the user-facing assistant identity; provider names never enter the UI contract.
 */
class PatsyIntelligenceCoordinator(
    private val aiService: PatsyAiService,
    private val webSearchService: WebSearchService,
) {
    var replyState: FeatureUiState<PatsyReply> = FeatureUiState.Idle
        private set

    var searchState: FeatureUiState<SearchResult> = FeatureUiState.Idle
        private set

    suspend fun askPatsy(
        context: AuthenticatedContext,
        conversationId: String?,
        turns: List<ConversationTurn>,
        allowWebSearch: Boolean,
        clientRequestId: String,
    ) {
        val aiDenial = FeatureRequestPolicy.denial(context, Capability.PATSY_AI)
        if (aiDenial != null) {
            replyState = aiDenial
            return
        }
        if (allowWebSearch) {
            val searchDenial = FeatureRequestPolicy.denial(context, Capability.WEB_SEARCH)
            if (searchDenial != null) {
                replyState = searchDenial
                return
            }
        }

        val lastUserText = turns.lastOrNull { it.role == ConversationRole.USER }?.text?.trim()
        val scope = FeatureRequestPolicy.scope(context)
        if (lastUserText.isNullOrEmpty() || lastUserText.length > MAX_USER_MESSAGE_LENGTH) {
            replyState = FeatureUiState.Error(
                scope = scope,
                safeCode = "INVALID_AI_REQUEST",
                retryable = false,
                message = "Enter a message between 1 and $MAX_USER_MESSAGE_LENGTH characters.",
                fieldErrors = mapOf("message" to "INVALID_LENGTH"),
            )
            return
        }
        if (clientRequestId.isBlank()) {
            replyState = FeatureUiState.Error(
                scope = scope,
                safeCode = "MISSING_REQUEST_ID",
                retryable = false,
                message = "The request could not be prepared safely. Please try again.",
            )
            return
        }

        replyState = FeatureUiState.Loading(scope)
        replyState = callFeatureService(scope) {
            aiService.reply(
                context,
                PatsyReplyRequest(
                    conversationId = conversationId,
                    turns = turns,
                    allowWebSearch = allowWebSearch,
                    clientRequestId = clientRequestId,
                ),
            )
        }
    }

    suspend fun search(
        context: AuthenticatedContext,
        query: String,
        resultLimit: Int = 5,
    ) {
        val denial = FeatureRequestPolicy.denial(context, Capability.WEB_SEARCH)
        if (denial != null) {
            searchState = denial
            return
        }
        val normalizedQuery = query.trim()
        val scope = FeatureRequestPolicy.scope(context)
        if (normalizedQuery.isEmpty() || normalizedQuery.length > MAX_SEARCH_QUERY_LENGTH) {
            searchState = FeatureUiState.Error(
                scope = scope,
                safeCode = "INVALID_SEARCH_REQUEST",
                retryable = false,
                message = "Enter a search between 1 and $MAX_SEARCH_QUERY_LENGTH characters.",
                fieldErrors = mapOf("query" to "INVALID_LENGTH"),
            )
            return
        }

        searchState = FeatureUiState.Loading(scope)
        searchState = callFeatureService(scope) {
            webSearchService.search(
                context,
                SearchRequest(
                    query = normalizedQuery,
                    resultLimit = resultLimit.coerceIn(1, MAX_SEARCH_RESULTS),
                ),
            )
        }
    }

    private companion object {
        const val MAX_USER_MESSAGE_LENGTH = 4_000
        const val MAX_SEARCH_QUERY_LENGTH = 500
        const val MAX_SEARCH_RESULTS = 10
    }
}

package com.patsy.app.services.scheduling

import com.patsy.app.services.AuthenticatedContext
import com.patsy.app.services.Capability
import com.patsy.app.services.Page
import com.patsy.app.services.state.FeatureRequestPolicy
import com.patsy.app.services.state.FeatureUiState
import com.patsy.app.services.state.callFeatureService

/** App-side scheduling state. A provider is never considered connected until the service confirms it. */
class SchedulingCoordinator(
    private val service: SchedulingService,
) {
    var listState: FeatureUiState<Page<ScheduledPost>> = FeatureUiState.Idle
        private set

    var selectedState: FeatureUiState<ScheduledPost> = FeatureUiState.Idle
        private set

    var mutationState: FeatureUiState<ScheduledPost> = FeatureUiState.Idle
        private set

    suspend fun loadScheduled(context: AuthenticatedContext, cursor: String? = null) {
        val denial = FeatureRequestPolicy.denial(context, Capability.SCHEDULING)
        if (denial != null) {
            listState = denial
            return
        }
        val scope = FeatureRequestPolicy.scope(context)
        listState = FeatureUiState.Loading(scope)
        listState = callFeatureService(scope) { service.listScheduled(context, cursor) }
    }

    suspend fun loadScheduledPost(context: AuthenticatedContext, scheduledPostId: String) {
        val denial = FeatureRequestPolicy.denial(context, Capability.SCHEDULING)
        if (denial != null) {
            selectedState = denial
            return
        }
        val scope = FeatureRequestPolicy.scope(context)
        selectedState = FeatureUiState.Loading(scope)
        selectedState = callFeatureService(scope) { service.getScheduled(context, scheduledPostId) }
    }

    suspend fun schedule(context: AuthenticatedContext, request: ScheduleRequest) {
        val denial = FeatureRequestPolicy.denial(context, Capability.SCHEDULING)
        if (denial != null) {
            mutationState = denial
            return
        }
        val scope = FeatureRequestPolicy.scope(context)
        mutationState = FeatureUiState.Loading(scope)
        mutationState = callFeatureService(scope) { service.schedule(context, request) }
    }

    suspend fun cancel(context: AuthenticatedContext, scheduledPostId: String) {
        val denial = FeatureRequestPolicy.denial(context, Capability.SCHEDULING)
        if (denial != null) {
            mutationState = denial
            return
        }
        val scope = FeatureRequestPolicy.scope(context)
        mutationState = FeatureUiState.Loading(scope)
        mutationState = callFeatureService(scope) { service.cancel(context, scheduledPostId) }
    }
}

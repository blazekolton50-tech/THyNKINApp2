package com.patsy.app.services.studio

import com.patsy.app.services.AuthenticatedContext
import com.patsy.app.services.Capability
import com.patsy.app.services.Page
import com.patsy.app.services.state.FeatureRequestPolicy
import com.patsy.app.services.state.FeatureUiState
import com.patsy.app.services.state.callFeatureService

/** Provider-neutral Creation Studio state; queued and ready states only originate from the service. */
class CreationStudioCoordinator(
    private val service: CreationStudioService,
) {
    var jobsState: FeatureUiState<Page<CreationJob>> = FeatureUiState.Idle
        private set

    var selectedJobState: FeatureUiState<CreationJob> = FeatureUiState.Idle
        private set

    var mutationState: FeatureUiState<CreationJob> = FeatureUiState.Idle
        private set

    suspend fun loadJobs(context: AuthenticatedContext, cursor: String? = null) {
        val denial = FeatureRequestPolicy.denial(context, Capability.CREATION_STUDIO)
        if (denial != null) {
            jobsState = denial
            return
        }
        val scope = FeatureRequestPolicy.scope(context)
        jobsState = FeatureUiState.Loading(scope)
        jobsState = callFeatureService(scope) { service.listJobs(context, cursor) }
    }

    suspend fun loadJob(context: AuthenticatedContext, jobId: String) {
        val denial = FeatureRequestPolicy.denial(context, Capability.CREATION_STUDIO)
        if (denial != null) {
            selectedJobState = denial
            return
        }
        val scope = FeatureRequestPolicy.scope(context)
        selectedJobState = FeatureUiState.Loading(scope)
        selectedJobState = callFeatureService(scope) { service.getJob(context, jobId) }
    }

    suspend fun create(context: AuthenticatedContext, request: CreationRequest) {
        val denial = FeatureRequestPolicy.denial(context, Capability.CREATION_STUDIO)
        if (denial != null) {
            mutationState = denial
            return
        }
        val scope = FeatureRequestPolicy.scope(context)
        mutationState = FeatureUiState.Loading(scope)
        mutationState = callFeatureService(scope) { service.create(context, request) }
    }

    suspend fun cancel(context: AuthenticatedContext, jobId: String) {
        val denial = FeatureRequestPolicy.denial(context, Capability.CREATION_STUDIO)
        if (denial != null) {
            mutationState = denial
            return
        }
        val scope = FeatureRequestPolicy.scope(context)
        mutationState = FeatureUiState.Loading(scope)
        mutationState = callFeatureService(scope) { service.cancel(context, jobId) }
    }
}

package com.patsy.app.services.studio

import com.patsy.app.services.AuthenticatedContext
import com.patsy.app.services.Page
import com.patsy.app.services.ServiceResult

enum class CreationKind { IMAGE, SHORT_VIDEO_10_SECONDS, CAPTION, HASHTAGS, REPURPOSE }

data class MediaReference(
    val id: String,
    val mediaType: String
)

data class CreationRequest(
    val kind: CreationKind,
    val prompt: String,
    val sourceMedia: List<MediaReference> = emptyList(),
    val clientRequestId: String
)

enum class CreationState { QUEUED, RUNNING, READY, FAILED, CANCELLED }

data class CreationJob(
    val id: String,
    val ownerUserId: String,
    val kind: CreationKind,
    val state: CreationState,
    val output: List<MediaReference> = emptyList(),
    val safeFailureCode: String? = null
)

/** Provider-neutral contract; queued/ready may only be returned after provider confirmation. */
interface CreationStudioService {
    suspend fun create(
        context: AuthenticatedContext,
        request: CreationRequest
    ): ServiceResult<CreationJob>

    suspend fun getJob(
        context: AuthenticatedContext,
        jobId: String
    ): ServiceResult<CreationJob>

    suspend fun listJobs(
        context: AuthenticatedContext,
        cursor: String? = null
    ): ServiceResult<Page<CreationJob>>

    suspend fun cancel(
        context: AuthenticatedContext,
        jobId: String
    ): ServiceResult<CreationJob>
}

package com.patsy.app.services.scheduling

import com.patsy.app.services.AuthenticatedContext
import com.patsy.app.services.Page
import com.patsy.app.services.ServiceResult
import com.patsy.app.services.studio.MediaReference

data class PublishingDestination(
    val connectionId: String,
    val providerKey: String,
    val displayName: String
)

data class ScheduleRequest(
    val destination: PublishingDestination,
    val media: List<MediaReference>,
    val caption: String,
    val publishAtEpochMillis: Long,
    val clientRequestId: String
)

enum class ScheduledState { PENDING, PUBLISHING, PUBLISHED, FAILED, CANCELLED }

data class ScheduledPost(
    val id: String,
    val ownerUserId: String,
    val destination: PublishingDestination,
    val publishAtEpochMillis: Long,
    val state: ScheduledState,
    val providerPostId: String? = null,
    val safeFailureCode: String? = null
)

/** Adapters must enforce publicScheduling policy and verify provider connections server-side. */
interface SchedulingService {
    suspend fun schedule(
        context: AuthenticatedContext,
        request: ScheduleRequest
    ): ServiceResult<ScheduledPost>

    suspend fun listScheduled(
        context: AuthenticatedContext,
        cursor: String? = null
    ): ServiceResult<Page<ScheduledPost>>

    suspend fun getScheduled(
        context: AuthenticatedContext,
        scheduledPostId: String
    ): ServiceResult<ScheduledPost>

    suspend fun cancel(
        context: AuthenticatedContext,
        scheduledPostId: String
    ): ServiceResult<ScheduledPost>
}

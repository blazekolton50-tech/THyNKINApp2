package com.patsy.app.studio

import com.patsy.app.auth.PublicSession

object StudioPersistenceBindings {
    var service: StudioPersistenceService = UnconfiguredStudioPersistenceService
}

private object UnconfiguredStudioPersistenceService : StudioPersistenceService {
    override suspend fun load(session: PublicSession, projectId: String): StudioPersistenceResult =
        StudioPersistenceResult.Unavailable

    override suspend fun autosave(session: PublicSession, request: StudioAutosaveRequest): StudioSaveResult =
        StudioSaveResult.Unavailable
}

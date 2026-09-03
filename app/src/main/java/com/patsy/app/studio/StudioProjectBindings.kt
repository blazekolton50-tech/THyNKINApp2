package com.patsy.app.studio

import com.patsy.app.auth.PublicSession

object StudioProjectBindings {
    var service: StudioProjectService = UnconfiguredStudioProjectService
}

private object UnconfiguredStudioProjectService : StudioProjectService {
    override suspend fun create(session: PublicSession, name: String): StudioProjectCreateResult =
        StudioProjectCreateResult.Unavailable
}

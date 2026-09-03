package com.patsy.app.profile

import com.patsy.app.auth.PublicSession

object ProfileServiceBindings {
    var profileDataService: ProfileDataService = UnconfiguredProfileDataService
}

private object UnconfiguredProfileDataService : ProfileDataService {
    override suspend fun load(session: PublicSession): ProfileDataResult = ProfileDataResult.Unavailable
}

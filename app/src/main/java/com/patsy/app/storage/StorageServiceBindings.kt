package com.patsy.app.storage

import com.patsy.app.auth.PublicSession

object StorageServiceBindings {
    var storageDataService: StorageDataService = UnconfiguredStorageDataService
}

private object UnconfiguredStorageDataService : StorageDataService {
    override suspend fun load(session: PublicSession): StorageDataResult = StorageDataResult.Unavailable
}

package com.patsy.app.dms

import com.patsy.app.auth.PublicSession

object DmServiceBindings {
    var dmDataService: DmDataService = UnconfiguredDmDataService
    var conversationService: DmConversationService = UnconfiguredDmConversationService
    var memberStateService: DmMemberStateService = UnconfiguredDmMemberStateService
}

private object UnconfiguredDmDataService : DmDataService {
    override suspend fun load(session: PublicSession): DmDataResult = DmDataResult.Unavailable
}

private object UnconfiguredDmConversationService : DmConversationService {
    override suspend fun load(session: PublicSession, threadId: String): DmConversationResult =
        DmConversationResult.Unavailable

    override suspend fun send(session: PublicSession, threadId: String, body: String): DmSendResult =
        DmSendResult.Unavailable
}

private object UnconfiguredDmMemberStateService : DmMemberStateService {
    override suspend fun markRead(session: PublicSession, threadId: String): DmMemberStateResult =
        DmMemberStateResult.Unavailable

    override suspend fun setArchived(
        session: PublicSession,
        threadId: String,
        archived: Boolean,
    ): DmMemberStateResult = DmMemberStateResult.Unavailable
}

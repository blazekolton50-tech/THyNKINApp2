package com.patsy.app.account

import com.patsy.app.auth.PublicSession

/** Account-scoped shell memory. Starting another session invalidates all previous authority. */
class AuthenticatedShellState {
    var session: PublicSession? = null
        private set
    var bootstrap: AccountBootstrap? = null
        private set
    var selectedRouteId: String? = null
        private set
    var isLoadingBootstrap: Boolean = false
        private set

    fun begin(newSession: PublicSession) {
        clear()
        session = newSession
        isLoadingBootstrap = true
    }

    fun accept(account: AccountBootstrap): Boolean {
        if (session?.userId != account.canonicalUserId) return false
        bootstrap = account
        isLoadingBootstrap = false
        return true
    }

    fun selectRoute(routeId: String) {
        selectedRouteId = routeId
    }

    fun clear() {
        session = null
        bootstrap = null
        selectedRouteId = null
        isLoadingBootstrap = false
    }
}

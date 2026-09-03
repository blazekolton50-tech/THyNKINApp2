package com.patsy.app.navigation

import com.patsy.app.account.AccountBootstrap
import com.patsy.app.ui.finaldesign.FinalHomeDestination

/** Bridges the locked FINAL UI destinations to the trusted semantic shell contract. */
object FinalShellNavigation {
    fun routeId(destination: FinalHomeDestination): String = when (destination) {
        FinalHomeDestination.HOME -> "home"
        FinalHomeDestination.THYNK -> "thynk"
        FinalHomeDestination.CREATE -> "create"
        FinalHomeDestination.PATSY_DMS -> "patsy_dms"
        FinalHomeDestination.PROFILE -> "profile"
    }

    fun authorize(
        destination: FinalHomeDestination,
        account: AccountBootstrap,
    ): NavigationDecision = ShellNavigationGate.resolve(routeId(destination), account)
}

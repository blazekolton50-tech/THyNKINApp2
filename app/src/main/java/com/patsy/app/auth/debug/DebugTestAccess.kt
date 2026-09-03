package com.patsy.app.auth.debug

import com.patsy.app.auth.PublicSession

/**
 * Debug-only owner test access boundary.
 *
 * The implementation lives in src/debug. The release implementation is a no-op and contains
 * no test username/password. This boundary never grants owner capabilities; it only yields a
 * synthetic non-privileged PublicSession so the UI can be exercised on a device.
 */
interface DebugTestAccess {
    suspend fun attemptLogin(username: String, password: CharArray, keepSignedIn: Boolean): DebugLoginResult
    suspend fun completePasswordSetup(newPassword: CharArray, confirmPassword: CharArray, keepSignedIn: Boolean): PublicSession?
    suspend fun restoreSessionIfRemembered(): PublicSession?
    suspend fun logout()
}

sealed interface DebugLoginResult {
    data object NotHandled : DebugLoginResult
    data object NeedsPasswordSetup : DebugLoginResult
    data class Authenticated(val session: PublicSession) : DebugLoginResult
    data class Rejected(val message: String) : DebugLoginResult
}

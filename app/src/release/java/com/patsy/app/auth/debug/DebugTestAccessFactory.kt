package com.patsy.app.auth.debug

import android.content.Context
import com.patsy.app.auth.PublicSession
import java.util.Arrays

/** Release builds contain no test credential constants and never handle debug login. */
object DebugTestAccessFactory {
    fun create(context: Context): DebugTestAccess = ReleaseNoOpDebugTestAccess
}

private object ReleaseNoOpDebugTestAccess : DebugTestAccess {
    override suspend fun attemptLogin(
        username: String,
        password: CharArray,
        keepSignedIn: Boolean,
    ): DebugLoginResult {
        Arrays.fill(password, '\u0000')
        return DebugLoginResult.NotHandled
    }

    override suspend fun completePasswordSetup(
        newPassword: CharArray,
        confirmPassword: CharArray,
        keepSignedIn: Boolean,
    ): PublicSession? {
        Arrays.fill(newPassword, '\u0000')
        Arrays.fill(confirmPassword, '\u0000')
        return null
    }

    override suspend fun restoreSessionIfRemembered(): PublicSession? = null
    override suspend fun logout() = Unit
}

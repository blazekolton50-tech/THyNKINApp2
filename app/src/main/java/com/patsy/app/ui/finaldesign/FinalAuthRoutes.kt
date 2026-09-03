package com.patsy.app.ui.finaldesign

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.patsy.app.auth.AuthGateway
import com.patsy.app.auth.AuthValidation
import com.patsy.app.auth.LoginRequest
import com.patsy.app.auth.LoginResult
import com.patsy.app.auth.ui.RememberMeCoordinator
import com.patsy.app.auth.PasswordResetRequest
import com.patsy.app.auth.PasswordResetResult
import com.patsy.app.auth.PublicSession
import com.patsy.app.auth.SecretChars
import com.patsy.app.auth.debug.DebugLoginResult
import com.patsy.app.auth.debug.DebugTestAccess
import kotlinx.coroutines.launch

@Composable
fun FinalLoginRoute(
    authGateway: AuthGateway,
    debugTestAccess: DebugTestAccess,
    rememberMeCoordinator: RememberMeCoordinator,
    onAuthenticated: (PublicSession) -> Unit,
    onNeedDebugPasswordSetup: (Boolean) -> Unit,
) {
    var username by remember { mutableStateOf("patsyowner_blaze") }
    var email by remember { mutableStateOf("you@example.com") }
    var keepSignedIn by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf("") }
    var passwordPrompt by remember { mutableStateOf(false) }
    var password by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    FinalLoginScreen(
        username = username,
        email = email,
        keepSignedIn = keepSignedIn,
        errorMessage = error,
        onUsernameChange = { username = it },
        onEmailChange = { email = it },
        onKeepSignedInChange = { keepSignedIn = it },
        onLogin = {
            error = ""
            password = ""
            passwordPrompt = true
        },
        onForgotPassword = {
            scope.launch {
                val identifier = email.takeIf { it.isNotBlank() } ?: username
                when (val result = authGateway.requestPasswordReset(PasswordResetRequest(identifier.trim()))) {
                    is PasswordResetResult.RequestAccepted -> error = result.genericMessage
                    is PasswordResetResult.Unavailable -> error = "Secure password reset is currently unavailable."
                }
            }
        },
    )

    if (passwordPrompt) {
        FinalPasswordPromptDialog(
            password = password,
            onPasswordChange = { password = it },
            onDismiss = {
                password = ""
                passwordPrompt = false
            },
            onSubmit = {
                if (password.isBlank()) {
                    error = "Enter your password."
                    return@FinalPasswordPromptDialog
                }
                val entered = password
                password = ""
                passwordPrompt = false
                scope.launch {
                    when (val debug = debugTestAccess.attemptLogin(username, entered.toCharArray(), keepSignedIn)) {
                        DebugLoginResult.NeedsPasswordSetup -> onNeedDebugPasswordSetup(keepSignedIn)
                        is DebugLoginResult.Authenticated -> onAuthenticated(debug.session)
                        is DebugLoginResult.Rejected -> error = debug.message
                        DebugLoginResult.NotHandled -> {
                            val identifierValidation = AuthValidation.loginIdentifier(
                                username.takeIf { it.isNotBlank() } ?: email,
                            )
                            if (!identifierValidation.isValid || identifierValidation.identifier == null) {
                                error = "Enter a valid username or email address."
                            } else {
                                val secret = SecretChars.copyOf(entered.toCharArray())
                                val result = try {
                                    authGateway.login(
                                        LoginRequest(
                                            identifier = identifierValidation.identifier,
                                            password = secret,
                                            sessionRetention = rememberMeCoordinator.retentionFor(keepSignedIn),
                                        ),
                                    )
                                } finally {
                                    secret.close()
                                }
                                when (result) {
                                    is LoginResult.Authenticated -> {
                                        rememberMeCoordinator.recordSuccessfulLogin(keepSignedIn)
                                        onAuthenticated(result.session)
                                    }
                                    is LoginResult.Rejected -> error = "The username/email or password is incorrect."
                                    is LoginResult.Unavailable -> error = "Secure login is currently unavailable."
                                }
                            }
                        }
                    }
                }
            },
        )
    }
}

@Composable
fun FinalDebugSetPasswordRoute(
    keepSignedIn: Boolean,
    debugTestAccess: DebugTestAccess,
    onAuthenticated: (PublicSession) -> Unit,
    onBack: () -> Unit,
) {
    var password by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var revealPassword by remember { mutableStateOf(false) }
    var revealConfirm by remember { mutableStateOf(false) }
    var keepSession by remember(keepSignedIn) { mutableStateOf(keepSignedIn) }
    var error by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    FinalSetPasswordScreen(
        password = password,
        confirmPassword = confirm,
        revealPassword = revealPassword,
        revealConfirmPassword = revealConfirm,
        keepSignedIn = keepSession,
        errorMessage = error,
        onPasswordChange = { password = it },
        onConfirmPasswordChange = { confirm = it },
        onTogglePasswordReveal = { revealPassword = !revealPassword },
        onToggleConfirmReveal = { revealConfirm = !revealConfirm },
        onKeepSignedInChange = { keepSession = it },
        onSetPasswordAndLogin = {
            when {
                password.length < 8 -> error = "Minimum 8 characters."
                password != confirm -> error = "Passwords must match."
                else -> {
                    val first = password.toCharArray()
                    val second = confirm.toCharArray()
                    password = ""
                    confirm = ""
                    scope.launch {
                        val session = debugTestAccess.completePasswordSetup(first, second, keepSession)
                        if (session == null) error = "Password could not be saved."
                        else onAuthenticated(session)
                    }
                }
            }
        },
        onBackToLogin = onBack,
        onForgotPassword = onBack,
    )
}

@Composable
private fun FinalPasswordPromptDialog(
    password: String,
    onPasswordChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSubmit: () -> Unit,
) {
    var reveal by remember { mutableStateOf(false) }
    Dialog(onDismissRequest = onDismiss) {
        FinalRainbowPanel(Modifier.fillMaxWidth()) {
            Column(
                Modifier.fillMaxWidth().background(FinalCard).padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text("Enter password", color = FinalWhite, fontSize = 21.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(14.dp))
                FinalTextField(
                    value = password,
                    onValueChange = onPasswordChange,
                    secret = true,
                    revealSecret = reveal,
                    onToggleReveal = { reveal = !reveal },
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(16.dp))
                FinalGradientButton("Continue", onSubmit, Modifier.fillMaxWidth())
                TextButton(onClick = onDismiss) {
                    Text("Cancel", color = Color(0xFFCCCCD2))
                }
            }
        }
    }
}

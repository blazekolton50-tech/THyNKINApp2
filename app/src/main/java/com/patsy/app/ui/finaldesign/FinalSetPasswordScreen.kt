package com.patsy.app.ui.finaldesign

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FinalSetPasswordScreen(
    password: String,
    confirmPassword: String,
    revealPassword: Boolean,
    revealConfirmPassword: Boolean,
    keepSignedIn: Boolean,
    errorMessage: String,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onTogglePasswordReveal: () -> Unit,
    onToggleConfirmReveal: () -> Unit,
    onKeepSignedInChange: (Boolean) -> Unit,
    onSetPasswordAndLogin: () -> Unit,
    onBackToLogin: () -> Unit,
    onForgotPassword: () -> Unit,
) {
    Box(Modifier.fillMaxSize().background(FinalCharcoal)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(top = 8.dp, bottom = 52.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            FinalLogoBlock()
            FinalIntroCopy()
            Spacer(Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                FinalPatsyActor(
                    action = FinalPatsyAction.WAVE,
                    modifier = Modifier.size(142.dp),
                )
                FinalSpeechBubble(
                    text = "Let's set your password\nto keep Blaze safe!",
                    modifier = Modifier.weight(1f).padding(bottom = 8.dp),
                )
            }
            Spacer(Modifier.height(2.dp))
            FinalRainbowPanel(Modifier.fillMaxWidth()) {
                Column(Modifier.fillMaxWidth().padding(horizontal = 22.dp, vertical = 20.dp)) {
                    Text(
                        "SET PASSWORD",
                        color = FinalWhite,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                    )
                    Spacer(Modifier.height(18.dp))
                    Text("Set password", color = FinalWhite, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(6.dp))
                    FinalTextField(
                        value = password,
                        onValueChange = onPasswordChange,
                        secret = true,
                        revealSecret = revealPassword,
                        onToggleReveal = onTogglePasswordReveal,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(Modifier.height(14.dp))
                    Text("Confirm password", color = FinalWhite, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(6.dp))
                    FinalTextField(
                        value = confirmPassword,
                        onValueChange = onConfirmPasswordChange,
                        secret = true,
                        revealSecret = revealConfirmPassword,
                        onToggleReveal = onToggleConfirmReveal,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(Modifier.height(18.dp))
                    Text(
                        "Owner Profile login setup",
                        color = FinalMuted,
                        fontSize = 15.sp,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                    )
                    Spacer(Modifier.height(12.dp))
                    OwnerProfileLoginRow(
                        keepSignedIn = keepSignedIn,
                        onKeepSignedInChange = onKeepSignedInChange,
                    )
                    if (errorMessage.isNotBlank()) {
                        Spacer(Modifier.height(10.dp))
                        Text(errorMessage, color = Color(0xFFFF7878), fontSize = 12.sp)
                    }
                    Spacer(Modifier.height(20.dp))
                    FinalGradientButton("Set Password & Login", onSetPasswordAndLogin, Modifier.fillMaxWidth())
                    Spacer(Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(Modifier.weight(1f).height(1.dp).background(Color(0xFF37373D)))
                        TextButton(onClick = onBackToLogin) {
                            Text("Back to Login", color = Color(0xFFCCCCD2), fontSize = 15.sp)
                        }
                        Box(Modifier.weight(1f).height(1.dp).background(Color(0xFF37373D)))
                    }
                    TextButton(
                        onClick = onForgotPassword,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                    ) {
                        Text(
                            "Forgot Password?",
                            style = TextStyle(brush = FinalRainbow, fontSize = 16.sp, fontWeight = FontWeight.SemiBold),
                        )
                    }
                }
            }
            Spacer(Modifier.height(36.dp))
        }
        FinalBottomWave(
            modifier = Modifier.align(Alignment.BottomCenter),
            showFooterCopy = true,
        )
    }
}

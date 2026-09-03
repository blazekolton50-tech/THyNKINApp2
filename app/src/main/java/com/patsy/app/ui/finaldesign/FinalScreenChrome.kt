package com.patsy.app.ui.finaldesign

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patsy.app.R
import com.patsy.app.patsy.rig.PatsyRigCoordinator
import com.patsy.app.patsy.rig.PatsyRigExpression
import com.patsy.app.patsy.rig.PatsyRigMotion
import com.patsy.app.patsy.rig.PatsyRigPose
import com.patsy.app.patsy.rig.PatsyRigViseme
import com.patsy.app.patsy.rig.rive.PatsyRiveHost
import com.patsy.app.patsy.rig.rive.PatsyRiveRuntimeAdapter

val FinalCharcoal = Color(FinalVisualContract.charcoalArgb)
val FinalCard = Color(FinalVisualContract.cardArgb)
val FinalBlack = Color(FinalVisualContract.logoSquareArgb)
val FinalWhite = Color(0xFFF7F7F7)
val FinalMuted = Color(0xFFABABB2)

val FinalRainbow = Brush.horizontalGradient(
    listOf(
        Color(0xFF8E4DFF),
        Color(0xFFFF4C98),
        Color(0xFFFF884D),
        Color(0xFFFFDF57),
        Color(0xFF64E58D),
        Color(0xFF4FD7FF),
        Color(0xFFB55CFF),
    )
)

enum class FinalPatsyAction { IDLE, WAVE, TALKING }

@Composable
fun FinalPatsyActor(
    action: FinalPatsyAction,
    modifier: Modifier = Modifier,
) {
    val riveRuntime = remember { PatsyRiveRuntimeAdapter() }
    val rigCoordinator = remember(riveRuntime) { PatsyRigCoordinator(riveRuntime) }
    val transition = rememberInfiniteTransition(label = "final-patsy")
    val breathe by transition.animateFloat(
        initialValue = 0.99f,
        targetValue = 1.01f,
        animationSpec = infiniteRepeatable(tween(1300, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "breathe",
    )
    val look by transition.animateFloat(
        initialValue = -0.12f,
        targetValue = 0.12f,
        animationSpec = infiniteRepeatable(tween(2200, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "look",
    )
    val motion = when (action) {
        FinalPatsyAction.WAVE -> PatsyRigMotion.WAVE
        FinalPatsyAction.IDLE, FinalPatsyAction.TALKING -> PatsyRigMotion.IDLE
    }
    SideEffect {
        rigCoordinator.render(
            PatsyRigPose(
                motion = motion,
                motionSpeed = if (action == FinalPatsyAction.WAVE) 0.45f else 0.1f,
                lookX = look,
                headTilt = -look * 0.2f,
                leftEarDrive = look * 0.2f,
                rightEarDrive = -look * 0.16f,
                tailEnergy = if (action == FinalPatsyAction.WAVE) 0.75f else 0.35f,
                expression = if (action == FinalPatsyAction.WAVE) PatsyRigExpression.EXCITED else PatsyRigExpression.CHEEKY,
                expressionIntensity = 0.7f,
                talking = action == FinalPatsyAction.TALKING,
                viseme = if (action == FinalPatsyAction.TALKING) PatsyRigViseme.A else PatsyRigViseme.REST,
                visemeIntensity = if (action == FinalPatsyAction.TALKING) 0.45f else 0f,
                speechEnergy = if (action == FinalPatsyAction.TALKING) 0.35f else 0f,
            )
        )
    }
    LaunchedEffect(action) {
        if (action == FinalPatsyAction.WAVE) rigCoordinator.retriggerAction(PatsyRigMotion.WAVE)
    }
    DisposableEffect(riveRuntime) {
        onDispose { riveRuntime.close() }
    }
    Box(modifier = modifier) {
        PatsyRiveHost(
            runtime = riveRuntime,
            modifier = Modifier.fillMaxSize(),
            fallback = {
                Image(
                    painter = painterResource(R.drawable.patsy_generated_main),
                    contentDescription = "Patsy AI animated companion fallback",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize(),
                )
            },
        )
        // The authored Rive rig owns real motion; the fallback keeps only a very small breathing scale.
        Spacer(Modifier.fillMaxSize())
    }
}

@Composable
fun FinalLogoBlock(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(FinalVisualContract.logoSquareDp.dp)
            .background(FinalBlack),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(R.drawable.patsy_logo_official_white),
            contentDescription = "Patsy",
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize().padding(14.dp),
        )
    }
}

@Composable
fun FinalIntroCopy() {
    Text(
        text = FinalVisualContract.introCopy,
        color = FinalWhite,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
    )
}

@Composable
fun FinalSpeechBubble(
    text: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .border(2.dp, FinalRainbow, RoundedCornerShape(26.dp))
            .clip(RoundedCornerShape(26.dp))
            .background(FinalCard)
            .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        Text(text = text, color = FinalWhite, fontSize = 15.sp, lineHeight = 20.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun FinalRainbowPanel(
    modifier: Modifier = Modifier,
    radius: Dp = 30.dp,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .border(2.dp, FinalRainbow, RoundedCornerShape(radius))
            .clip(RoundedCornerShape(radius))
            .background(FinalCard),
    ) {
        content()
    }
}

@Composable
fun FinalTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    secret: Boolean = false,
    revealSecret: Boolean = false,
    onToggleReveal: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        visualTransformation = if (secret && !revealSecret) PasswordVisualTransformation() else VisualTransformation.None,
        textStyle = TextStyle(color = FinalWhite, fontSize = 16.sp),
        cursorBrush = SolidColor(FinalWhite),
        modifier = modifier,
        decorationBox = { inner ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .border(1.6.dp, FinalRainbow, RoundedCornerShape(29.dp))
                    .clip(RoundedCornerShape(29.dp))
                    .background(Color(0xFF101014))
                    .padding(horizontal = 18.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(Modifier.weight(1f)) {
                    if (value.isEmpty() && placeholder.isNotEmpty()) {
                        Text(placeholder, color = Color(0xFF8F8F98), fontSize = 16.sp)
                    }
                    inner()
                }
                if (secret && onToggleReveal != null) {
                    Text(
                        text = if (revealSecret) "◉" else "◎",
                        color = Color(0xFFA7A7AF),
                        fontSize = 26.sp,
                        modifier = Modifier.clickable(onClick = onToggleReveal).padding(start = 10.dp),
                    )
                }
            }
        },
    )
}

@Composable
fun OwnerProfileLoginRow(
    keepSignedIn: Boolean,
    onKeepSignedInChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFF303036), RoundedCornerShape(30.dp))
            .clip(RoundedCornerShape(30.dp))
            .background(Color(0xFF17171B))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .size(52.dp)
                .background(FinalRainbow, CircleShape)
                .padding(2.dp)
                .clip(CircleShape),
        ) {
            Image(
                painter = painterResource(R.drawable.owner_profile_avatar),
                contentDescription = "Owner profile avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize().clip(CircleShape),
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text("Blaze profile", color = FinalWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text("Owner • Blaze", color = FinalMuted, fontSize = 12.sp)
        }
        Box(
            modifier = Modifier
                .size(25.dp)
                .border(1.5.dp, FinalRainbow, RoundedCornerShape(5.dp))
                .clickable { onKeepSignedInChange(!keepSignedIn) },
            contentAlignment = Alignment.Center,
        ) {
            if (keepSignedIn) Text("✓", color = Color(0xFF64E58D), fontWeight = FontWeight.Black, fontSize = 17.sp)
        }
        Spacer(Modifier.width(8.dp))
        Text(
            text = FinalVisualContract.loginPersistenceVisibleLabel,
            color = FinalWhite,
            fontSize = 13.sp,
            modifier = Modifier.clickable { onKeepSignedInChange(!keepSignedIn) },
        )
    }
}

@Composable
fun FinalGradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .height(60.dp)
            .clip(RoundedCornerShape(30.dp))
            .background(FinalRainbow)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(text, color = Color.Black, fontSize = 20.sp, fontWeight = FontWeight.Black)
    }
}

@Composable
fun FinalBottomWave(
    modifier: Modifier = Modifier,
    showFooterCopy: Boolean = true,
) {
    Column(modifier = modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Canvas(Modifier.fillMaxWidth().height(26.dp)) {
            val path = Path().apply {
                moveTo(0f, size.height * 0.68f)
                cubicTo(
                    size.width * 0.28f,
                    size.height * 0.72f,
                    size.width * 0.42f,
                    size.height * 0.03f,
                    size.width * 0.54f,
                    size.height * 0.24f,
                )
                cubicTo(
                    size.width * 0.72f,
                    size.height * 0.58f,
                    size.width * 0.86f,
                    size.height * 0.62f,
                    size.width,
                    size.height * 0.72f,
                )
            }
            drawPath(path = path, brush = FinalRainbow, style = Stroke(width = 4f, cap = StrokeCap.Round))
        }
        if (showFooterCopy) {
            Text(
                FinalVisualContract.footerCopy,
                color = Color(0xFF77777F),
                fontSize = 8.sp,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 4.dp),
            )
        }
    }
}

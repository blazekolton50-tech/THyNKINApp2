package com.patsy.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.*
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patsy.app.auth.*
import com.patsy.app.security.FailClosedOwnerAuthorizationGate
import com.patsy.app.security.OwnerAuthorizationDecision
import com.patsy.app.security.OwnerCapability
import com.patsy.app.patsy.rig.PatsyRigCoordinator
import com.patsy.app.patsy.rig.PatsyRigExpression
import com.patsy.app.patsy.rig.PatsyRigMotion
import com.patsy.app.patsy.rig.PatsyRigPose
import com.patsy.app.patsy.rig.PatsyRigViseme
import com.patsy.app.patsy.rig.rive.PatsyRiveHost
import com.patsy.app.patsy.rig.rive.PatsyRiveRuntimeAdapter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val Charcoal = Color(0xFF202124)
private val Charcoal2 = Color(0xFF2A2B2E)
private val White = Color(0xFFF7F7F7)
private val Muted = Color(0xFFAAAAAF)
private val Rainbow = Brush.horizontalGradient(listOf(Color(0xFFFF6B35),Color(0xFFFFD447),Color(0xFF4CD964),Color(0xFF36A9FF),Color(0xFF9B59FF),Color(0xFFFF4FA3)))

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); setContent { PatsyApp() } }
}

data class Profile(
    val displayName:String,
    val username:String,
    val email:String,
    val mode:String = "16+ Patsy"
)

enum class Screen { WELCOME, MODE, PROFILE, PASSWORD_SETUP, EMAIL_LINKED, LOGIN, HOME, CHAT, CREATE, SCHEDULE, MORE, DMS, OWNER_PROFILE, OWNER_TOOLS }

@Composable fun PatsyApp(){
    val authGateway = remember { PatsyServiceBindings.authGateway }
    val ownerGate = remember { FailClosedOwnerAuthorizationGate(PatsyServiceBindings.ownerAuthorizationService) }
    var screen by remember { mutableStateOf(Screen.WELCOME) }
    var profile by remember { mutableStateOf<Profile?>(null) }
    var session by remember { mutableStateOf<PublicSession?>(null) }
    var pendingUsername by remember { mutableStateOf("") }
    var pendingEmail by remember { mutableStateOf("") }
    var pendingMode by remember { mutableStateOf("16+ Patsy") }
    var registrationAttemptId by remember { mutableStateOf<String?>(null) }
    var confirmationAcknowledgement by remember { mutableStateOf<ConfirmationEmailAcknowledgement?>(null) }
    LaunchedEffect(authGateway){
        when(val restored=authGateway.restoreSession()){
            is SessionState.Authenticated -> {
                session=restored.session
                profile=restored.session.toProfile(pendingMode)
                screen=Screen.HOME
            }
            SessionState.Anonymous,is SessionState.Expired,is SessionState.Unavailable -> Unit
        }
    }
    MaterialTheme(colorScheme = darkColorScheme(background=Color.Black,surface=Charcoal,primary=White)) {
        Surface(Modifier.fillMaxSize(),color=Color.Black){
            when(screen){
                Screen.WELCOME -> Welcome({screen=Screen.MODE},{screen=Screen.LOGIN})
                Screen.MODE -> ModeScreen({mode -> pendingMode=mode; screen=Screen.PROFILE},{screen=Screen.WELCOME})
                Screen.PROFILE -> ProfileScreen(
                    mode=pendingMode,
                    authGateway=authGateway,
                    onNext={user,email,attemptId ->
                        pendingUsername=user
                        pendingEmail=email
                        registrationAttemptId=attemptId
                        screen=Screen.PASSWORD_SETUP
                    },
                    back={screen=Screen.MODE}
                )
                Screen.PASSWORD_SETUP -> PasswordSetupScreen(
                    username=pendingUsername,
                    email=pendingEmail,
                    registrationAttemptId=registrationAttemptId,
                    authGateway=authGateway,
                    onCreated={acknowledgement ->
                        confirmationAcknowledgement=acknowledgement
                        screen=Screen.EMAIL_LINKED
                    },
                    back={screen=Screen.PROFILE}
                )
                Screen.EMAIL_LINKED -> EmailLinkedScreen(
                    email=pendingEmail,
                    acknowledgement=confirmationAcknowledgement,
                ){ screen=Screen.LOGIN }
                Screen.LOGIN -> LoginScreen(
                    authGateway=authGateway,
                    onDone={authenticatedSession ->
                        session=authenticatedSession
                        profile=authenticatedSession.toProfile(pendingMode)
                        screen=Screen.HOME
                    },
                    back={screen=Screen.WELCOME},
                )
                else -> Workspace(
                    profile=profile,
                    session=session,
                    authGateway=authGateway,
                    ownerGate=ownerGate,
                    onSignedOut={
                        session=null
                        profile=null
                        screen=Screen.WELCOME
                    },
                )
            }
        }
    }
}

@Composable fun Header(){ Column(horizontalAlignment=Alignment.CenterHorizontally,modifier=Modifier.fillMaxWidth().padding(top=18.dp)){ Image(painter=painterResource(R.drawable.patsy_logo_official_white),contentDescription="Patsy",modifier=Modifier.width(190.dp).height(88.dp),contentScale=ContentScale.Fit); Text("YOUR AI. YOUR WORKSPACE. YOUR CONTROL.",fontSize=10.sp,color=Muted,letterSpacing=1.sp) } }

/**
 * Animated Patsy actor architecture. The production actor must use generated Patsy character
 * assets based on the locked real-photo references. Real photographs are reference-only and must not appear in-app unless explicitly authorised. Motion states are intentionally separated from the UI so the real
 * asset can move, point, jump between controls, talk/lip-sync and react to app events.
 */
enum class PatsyAction { IDLE, THINKING, TALKING, POINTING, JUMPING, HAPPY, WARNING, SLEEPY, CELEBRATE }

@Composable fun PatsyMotion(label:String="Hi! 🐾", pointing:Boolean=false, action:PatsyAction = if(pointing) PatsyAction.POINTING else PatsyAction.IDLE, modifier:Modifier=Modifier){
    val riveRuntime = remember { PatsyRiveRuntimeAdapter() }
    val rigCoordinator = remember(riveRuntime) { PatsyRigCoordinator(riveRuntime) }
    val transition = rememberInfiniteTransition(label="patsy-motion")
    val bob by transition.animateFloat(0f, 1f, infiniteRepeatable(tween(900, easing=FastOutSlowInEasing), RepeatMode.Reverse), label="bob")
    val breathe by transition.animateFloat(0.985f, 1.015f, infiniteRepeatable(tween(1200, easing=FastOutSlowInEasing), RepeatMode.Reverse), label="breathe")
    val look by transition.animateFloat(-1f, 1f, infiniteRepeatable(tween(2200, easing=FastOutSlowInEasing), RepeatMode.Reverse), label="look")
    val jump by transition.animateFloat(0f, 1f, infiniteRepeatable(tween(1200, easing=FastOutSlowInEasing), RepeatMode.Reverse), label="jump")
    val rigMotion = when {
        action==PatsyAction.JUMPING -> PatsyRigMotion.JUMP
        action==PatsyAction.POINTING || pointing -> PatsyRigMotion.POINT
        action==PatsyAction.CELEBRATE -> PatsyRigMotion.WAVE
        action==PatsyAction.SLEEPY -> PatsyRigMotion.LIE
        else -> PatsyRigMotion.IDLE
    }
    val rigExpression = when(action){
        PatsyAction.THINKING -> PatsyRigExpression.CURIOUS
        PatsyAction.TALKING, PatsyAction.IDLE -> PatsyRigExpression.CHEEKY
        PatsyAction.POINTING -> PatsyRigExpression.PROUD
        PatsyAction.JUMPING, PatsyAction.HAPPY, PatsyAction.CELEBRATE -> PatsyRigExpression.EXCITED
        PatsyAction.WARNING -> PatsyRigExpression.CONCERNED
        PatsyAction.SLEEPY -> PatsyRigExpression.SLEEPY
    }
    val talking = action==PatsyAction.TALKING
    SideEffect {
        rigCoordinator.render(
            PatsyRigPose(
                motion=rigMotion,
                motionSpeed=when(rigMotion){
                    PatsyRigMotion.JUMP -> 0.85f
                    PatsyRigMotion.WAVE, PatsyRigMotion.POINT -> 0.45f
                    else -> 0.12f
                },
                pointX=if(pointing || action==PatsyAction.POINTING) 0.88f else 0.5f,
                pointY=if(pointing || action==PatsyAction.POINTING) 0.55f else 0.5f,
                lookX=look,
                lookY=(bob-0.5f)*0.12f,
                headTilt=-look*0.18f,
                leftEarDrive=(look*0.32f+(bob-0.5f)*0.08f).coerceIn(-1f,1f),
                rightEarDrive=(-look*0.22f+(bob-0.5f)*0.12f).coerceIn(-1f,1f),
                earPhysicsEnabled=true,
                tailDrive=look*0.2f,
                tailEnergy=when(action){
                    PatsyAction.HAPPY, PatsyAction.CELEBRATE, PatsyAction.JUMPING -> 0.85f
                    PatsyAction.SLEEPY, PatsyAction.WARNING -> 0.18f
                    else -> 0.38f
                },
                expression=rigExpression,
                expressionIntensity=when(action){
                    PatsyAction.IDLE -> 0.45f
                    PatsyAction.SLEEPY -> 0.65f
                    else -> 0.82f
                },
                talking=talking,
                viseme=if(talking) PatsyRigViseme.A else PatsyRigViseme.REST,
                visemeIntensity=if(talking) 0.25f+(bob*0.5f) else 0f,
                speechEnergy=if(talking) 0.2f+(bob*0.35f) else 0f,
            )
        )
    }
    LaunchedEffect(action,pointing){
        when(rigMotion){
            PatsyRigMotion.JUMP, PatsyRigMotion.WAVE, PatsyRigMotion.POINT -> rigCoordinator.retriggerAction(rigMotion)
            else -> Unit
        }
    }
    DisposableEffect(riveRuntime){ onDispose { riveRuntime.close() } }
    val x = when(action){
        PatsyAction.JUMPING -> ((jump * 210f) - 105f).dp
        PatsyAction.POINTING -> 18.dp
        else -> 0.dp
    }
    val y = when(action){
        PatsyAction.JUMPING -> (-32f * kotlin.math.sin(jump * Math.PI)).toFloat().dp
        else -> (12f * bob).dp
    }
    Box(modifier.fillMaxWidth().height(210.dp)){
        Text(label,color=White,modifier=Modifier.align(Alignment.TopEnd).background(Charcoal2,RoundedCornerShape(22.dp)).padding(12.dp))
        Box(
            modifier=Modifier
                .size(155.dp)
                .align(Alignment.Center)
                .offset(x=x,y=y)
                .graphicsLayer(
                    scaleX=breathe,
                    scaleY=breathe,
                    rotationY=look*7f,
                    rotationZ=look*1.8f,
                    translationX=look*3f
                )
        ){
            PatsyRiveHost(
                runtime=riveRuntime,
                modifier=Modifier.fillMaxSize(),
                fallback={
                    // Generated Patsy only in-app. Real photos remain reference-only.
                    Image(
                        painter=painterResource(R.drawable.patsy_generated_main),
                        contentDescription="Patsy AI — moving generated fallback; validated Rive rig activates automatically when present",
                        contentScale=ContentScale.Fit,
                        modifier=Modifier.fillMaxSize()
                    )
                }
            )
        }
        if(action==PatsyAction.POINTING || pointing) Text("🐾",fontSize=32.sp,modifier=Modifier.align(Alignment.CenterEnd).offset(x=(-52).dp,y=20.dp))
        if(action==PatsyAction.JUMPING) Text("↗",style=TextStyle(brush=Rainbow,fontSize=40.sp),modifier=Modifier.align(Alignment.CenterEnd).offset(x=(-34).dp,y=(-28).dp))
    }
}

@Composable fun Panel(content:@Composable ColumnScope.()->Unit){ Column(content=content,modifier=Modifier.fillMaxWidth().background(Charcoal,RoundedCornerShape(28.dp)).padding(20.dp)) }
@Composable fun Primary(text:String,onClick:()->Unit,enabled:Boolean=true){ Button(onClick=onClick,enabled=enabled,modifier=Modifier.fillMaxWidth().height(58.dp),colors=ButtonDefaults.buttonColors(containerColor=White,contentColor=Color.Black),shape=RoundedCornerShape(30.dp)){ Text(text,fontWeight=FontWeight.Bold,fontSize=16.sp) } }

@Composable fun Welcome(start:()->Unit,login:()->Unit){ Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),horizontalAlignment=Alignment.CenterHorizontally){ Header(); PatsyMotion("Hey there! 🐾",action=PatsyAction.HAPPY); Text("Let’s get you",fontSize=28.sp,color=White,fontWeight=FontWeight.Bold); Text("all set up…",style=TextStyle(brush=Rainbow,fontSize=30.sp,fontWeight=FontWeight.ExtraBold)); Spacer(Modifier.height(18.dp)); Primary("Get Started  →",start); Spacer(Modifier.height(10.dp)); OutlinedButton(login,Modifier.fillMaxWidth().height(54.dp),shape=RoundedCornerShape(28.dp),colors=ButtonDefaults.outlinedButtonColors(contentColor=White)){Text("I already have an account")}; Spacer(Modifier.height(18.dp)); Text("Patsy’s ready when you are! 🐾",color=Muted) } }

@Composable
fun ModeScreen(next:(String)->Unit,back:()->Unit){
    var mode by remember{mutableStateOf("16+ Patsy")}
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp)){
        Header()
        PatsyMotion("Hi, I'm Patsy. 💜",action=PatsyAction.TALKING)
        Text("CHOOSE YOUR EXPERIENCE",color=White,fontWeight=FontWeight.Bold,modifier=Modifier.padding(8.dp))
        listOf("16+ Patsy","Under-16 Patsy","Protected Mode").forEach{m->
            Button(
                onClick={mode=m},
                modifier=Modifier.fillMaxWidth().padding(vertical=5.dp),
                colors=ButtonDefaults.buttonColors(containerColor=if(mode==m) Color(0xFF34353A) else Color.White,contentColor=if(mode==m) White else Color.Black),
                shape=RoundedCornerShape(24.dp)
            ){Text(m,fontWeight=FontWeight.Bold)}
        }
        Spacer(Modifier.height(20.dp))
        Primary(text="Continue  →",onClick={next(mode)})
        TextButton(onClick=back,modifier=Modifier.fillMaxWidth()){Text("BACK",color=Muted)}
    }
}

@Composable
fun ProfileScreen(
    mode:String,
    authGateway:AuthGateway,
    onNext:(String,String,String)->Unit,
    back:()->Unit,
){
    var user by remember{mutableStateOf("")}
    var email by remember{mutableStateOf("")}
    var err by remember{mutableStateOf("")}
    var submitting by remember{mutableStateOf(false)}
    val scope = rememberCoroutineScope()
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp)){
        Header()
        PatsyMotion(
            label="First, let’s create your profile! 🐾",
            pointing=true,
            action=PatsyAction.POINTING
        )
        Panel{
            Text("Your Profile",fontSize=26.sp,fontWeight=FontWeight.Bold)
            Text("Choose your username and link your email.",color=Muted)
            Text("Experience: $mode",color=Muted,fontSize=12.sp)
            Spacer(Modifier.height(12.dp))
            Field(label="Username",value=user,onChange={user=it})
            Field(label="Email",value=email,onChange={email=it})
            Text("Password comes next — immediately after username/email setup.",color=Muted,fontSize=12.sp,modifier=Modifier.padding(top=6.dp))
            if(err.isNotEmpty()){Text(err,color=Color(0xFFFF6B6B))}
            Spacer(Modifier.height(12.dp))
            Primary(text=if(submitting) "Checking securely…" else "Continue to Password  →",enabled=!submitting,onClick={
                val usernameValidation=AuthValidation.username(user)
                val emailValidation=AuthValidation.email(email)
                if(!usernameValidation.isValid||!emailValidation.isValid){
                    err="Enter a valid username (3–30 letters, numbers, dots or underscores) and email address."
                }else scope.launch{
                    submitting=true
                    err=""
                    when(val result=authGateway.startRegistration(StartRegistrationRequest(usernameValidation.normalizedValue,emailValidation.normalizedValue,mode))){
                        is RegistrationStartResult.ReadyForPassword -> onNext(result.normalizedUsername,result.maskedEmail,result.registrationAttemptId)
                        is RegistrationStartResult.Rejected -> err=authFailureMessage(result.failure)
                        is RegistrationStartResult.Unavailable -> err=serviceFailureMessage(result.failure)
                    }
                    submitting=false
                }
            })
        }
        TextButton(onClick=back,modifier=Modifier.fillMaxWidth()){Text("BACK",color=Muted)}
    }
}

@Composable
fun PasswordSetupScreen(
    username:String,
    email:String,
    registrationAttemptId:String?,
    authGateway:AuthGateway,
    onCreated:(ConfirmationEmailAcknowledgement)->Unit,
    back:()->Unit,
){
    var pass by remember{mutableStateOf("")}
    var confirm by remember{mutableStateOf("")}
    var err by remember{mutableStateOf("")}
    var submitting by remember{mutableStateOf(false)}
    val scope=rememberCoroutineScope()
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp)){
        Header()
        PatsyMotion("Right — let’s lock it down! 🔐",action=PatsyAction.POINTING)
        Panel{
            Text("Secure Your Account",fontSize=26.sp,fontWeight=FontWeight.Bold)
            Text("@$username • $email",color=Muted)
            Spacer(Modifier.height(10.dp))
            Field("Password",pass,true){pass=it}
            Field("Confirm password",confirm,true){confirm=it}
            Text("12+ characters • upper and lower case • number • symbol • no spaces",color=Muted,fontSize=12.sp)
            if(err.isNotEmpty())Text(err,color=Color(0xFFFF6B6B))
            Spacer(Modifier.height(12.dp))
            Primary(if(submitting) "Creating securely…" else "Create Account & Send Confirmation →",enabled=!submitting,onClick={
                val validation=AuthValidation.password(pass)
                when{
                    registrationAttemptId==null -> err="Secure registration has expired. Go back and restart."
                    !validation.isValid -> err=passwordValidationMessage(validation)
                    pass!=confirm -> err="Passwords do not match."
                    else -> scope.launch{
                        submitting=true
                        err=""
                        val secret=SecretChars.copyOf(pass.toCharArray())
                        pass=""
                        confirm=""
                        val result=try{
                            authGateway.completeRegistration(CompleteRegistrationRequest(registrationAttemptId,secret))
                        }finally{
                            secret.close()
                        }
                        when(result){
                            is RegistrationResult.AccountCreated -> onCreated(result.confirmationEmail)
                            is RegistrationResult.Rejected -> err=authFailureMessage(result.failure)
                            is RegistrationResult.Unavailable -> err=serviceFailureMessage(result.failure)
                        }
                        submitting=false
                    }
                }
            })
        }
        TextButton(back,Modifier.fillMaxWidth()){Text("BACK",color=Muted)}
    }
}

@Composable
fun EmailLinkedScreen(email:String,acknowledgement:ConfirmationEmailAcknowledgement?,onContinue:()->Unit){
    val confirmed=acknowledgement as? ConfirmationEmailAcknowledgement.Confirmed
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),horizontalAlignment=Alignment.CenterHorizontally){
        Header()
        PatsyMotion(if(confirmed!=null) "Check your inbox! 🎉🐾" else "Your account was created. 🐾",action=if(confirmed!=null) PatsyAction.CELEBRATE else PatsyAction.WARNING)
        Panel{
            Text(if(confirmed!=null) "Confirmation email ${confirmed.deliveryState.name.lowercase()}" else "Confirmation email pending",fontSize=26.sp,fontWeight=FontWeight.Bold)
            when(val status=acknowledgement){
                is ConfirmationEmailAcknowledgement.Confirmed -> Text("The secure email service confirmed ${status.deliveryState.name.lowercase()} delivery to ${status.maskedEmail}.",color=White)
                is ConfirmationEmailAcknowledgement.NotConfirmed -> Text("Your account exists, but the email service did not confirm delivery. Try again later or contact support. (${status.reason.name.lowercase().replace('_',' ')})",color=Color(0xFFFFC46B))
                null -> Text("No verified delivery status is available. The app will not claim that an email was sent.",color=Color(0xFFFFC46B))
            }
            Text("Email: $email",color=Muted,modifier=Modifier.padding(top=8.dp))
            Spacer(Modifier.height(14.dp))
            Primary("Go to Login  →",onContinue)
        }
    }
}

@Composable
fun LoginScreen(authGateway:AuthGateway,onDone:(PublicSession)->Unit,back:()->Unit){
    var user by remember{mutableStateOf("")}
    var pass by remember{mutableStateOf("")}
    var err by remember{mutableStateOf("")}
    var resetStatus by remember{mutableStateOf("")}
    var submitting by remember{mutableStateOf(false)}
    val scope=rememberCoroutineScope()
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp)){
        Header()
        PatsyMotion("Welcome back! 🐾",action=PatsyAction.HAPPY)
        Panel{
            Text("Log In",fontSize=28.sp,fontWeight=FontWeight.Bold)
            Text("Use your username or linked email and password.",color=Muted)
            Field("Username or Email",user){user=it}
            Field("Password",pass,true){pass=it}
            if(err.isNotEmpty())Text(err,color=Color(0xFFFF6B6B))
            if(resetStatus.isNotEmpty())Text(resetStatus,color=Color(0xFF9BE7B2))
            Spacer(Modifier.height(12.dp))
            Primary(if(submitting) "Signing in securely…" else "Log In  →",enabled=!submitting,onClick={
                val identifier=AuthValidation.loginIdentifier(user)
                when{
                    !identifier.isValid -> err="Enter a valid username or email address."
                    pass.isBlank() -> err="Enter your password."
                    else -> scope.launch{
                        submitting=true
                        err=""
                        val secret=SecretChars.copyOf(pass.toCharArray())
                        pass=""
                        val result=try{
                            authGateway.login(LoginRequest(requireNotNull(identifier.identifier),secret))
                        }finally{
                            secret.close()
                        }
                        when(result){
                            is LoginResult.Authenticated -> onDone(result.session)
                            is LoginResult.Rejected -> err=authFailureMessage(result.failure)
                            is LoginResult.Unavailable -> err=serviceFailureMessage(result.failure)
                        }
                        submitting=false
                    }
                }
            })
            TextButton(onClick={
                val identifier=AuthValidation.loginIdentifier(user)
                if(!identifier.isValid){
                    err="Enter your username or email first."
                }else scope.launch{
                    when(val result=authGateway.requestPasswordReset(PasswordResetRequest(user.trim()))){
                        is PasswordResetResult.RequestAccepted -> resetStatus=result.genericMessage
                        is PasswordResetResult.Unavailable -> err=serviceFailureMessage(result.failure)
                    }
                }
            }){Text("Forgot password?",color=Color(0xFFB9A7FF))}
        }
        TextButton(back,Modifier.fillMaxWidth()){Text("BACK",color=Muted)}
    }
}

@Composable fun Field(label:String,value:String,secret:Boolean=false,onChange:(String)->Unit){ var show by remember{mutableStateOf(!secret)}; OutlinedTextField(value,onChange,label={Text(label)},modifier=Modifier.fillMaxWidth().padding(vertical=6.dp),singleLine=true,visualTransformation=if(secret&&!show) PasswordVisualTransformation() else VisualTransformation.None,trailingIcon={if(secret) TextButton({show=!show}){Text(if(show)"Hide" else "Show")}}) }

private fun authFailureMessage(failure:AuthFailure):String=when(failure){
    AuthFailure.DuplicateUsername -> "That username is already in use."
    AuthFailure.DuplicateEmail -> "That email is already linked to an account."
    AuthFailure.InvalidCredentials -> "The username/email or password is incorrect."
    AuthFailure.InvalidOrExpiredToken -> "That secure link is invalid or has expired."
    is AuthFailure.InvalidInput -> "Please check ${failure.field.replaceFirstChar{it.uppercase()}}."
    AuthFailure.RateLimited -> "Too many attempts. Please wait and try again."
}

private fun serviceFailureMessage(failure:ServiceFailure):String=when(failure){
    ServiceFailure.NotConfigured -> "The secure account service is not configured yet. No account, email or login has been claimed."
    ServiceFailure.Offline -> "You appear to be offline. Check your connection and try again."
    ServiceFailure.Timeout -> "The secure service took too long to respond. Please try again."
    ServiceFailure.ServerError -> "The secure service had a problem. Please try again later."
    ServiceFailure.Unknown -> "The secure service is unavailable. Please try again later."
}

private fun passwordValidationMessage(validation:PasswordValidation):String{
    val messages=validation.issues.map{issue->when(issue){
        ValidationIssue.Required -> "enter a password"
        is ValidationIssue.InvalidLength -> "use at least ${issue.minimum ?: 12} characters"
        ValidationIssue.PasswordNeedsLowercase -> "add a lowercase letter"
        ValidationIssue.PasswordNeedsUppercase -> "add an uppercase letter"
        ValidationIssue.PasswordNeedsDigit -> "add a number"
        ValidationIssue.PasswordNeedsSymbol -> "add a symbol"
        ValidationIssue.PasswordContainsWhitespace -> "remove spaces"
        ValidationIssue.InvalidUsernameCharacters,ValidationIssue.InvalidEmail -> "check the value"
    }}
    return "For a secure password, ${messages.distinct().joinToString()}."
}

private fun OwnerAuthorizationDecision.Allowed?.isCurrentGrant(capability:OwnerCapability):Boolean=
    this!=null&&this.capability==capability&&this.expiresAtEpochMillis>System.currentTimeMillis()

private fun PublicSession.toProfile(mode:String)=Profile(
    displayName=username,
    username=username,
    email=maskedEmail,
    mode=mode,
)

@Composable
fun Workspace(
    profile:Profile?,
    session:PublicSession?,
    authGateway:AuthGateway,
    ownerGate:FailClosedOwnerAuthorizationGate,
    onSignedOut:()->Unit,
){
    var selected by remember{mutableStateOf(Screen.HOME)}
    var ownerProfileGrant by remember{mutableStateOf<OwnerAuthorizationDecision.Allowed?>(null)}
    var ownerToolsGrant by remember{mutableStateOf<OwnerAuthorizationDecision.Allowed?>(null)}
    var ownerAccessChecked by remember{mutableStateOf(false)}
    val scope=rememberCoroutineScope()

    suspend fun refreshOwnerAccess(){
        ownerProfileGrant=(ownerGate.verify(session,OwnerCapability.VIEW_OWNER_PROFILE) as? OwnerAuthorizationDecision.Allowed)
            ?.takeIf{it.expiresAtEpochMillis>System.currentTimeMillis()&&it.capability==OwnerCapability.VIEW_OWNER_PROFILE}
        ownerToolsGrant=(ownerGate.verify(session,OwnerCapability.VIEW_OWNER_TOOLS) as? OwnerAuthorizationDecision.Allowed)
            ?.takeIf{it.expiresAtEpochMillis>System.currentTimeMillis()&&it.capability==OwnerCapability.VIEW_OWNER_TOOLS}
        ownerAccessChecked=true
    }

    LaunchedEffect(session?.sessionId){ refreshOwnerAccess() }
    Column(Modifier.fillMaxSize()){
        Header()
        Box(Modifier.weight(1f).fillMaxWidth()){
            when(selected){
                Screen.HOME->Home(profile){selected=it}
                Screen.CHAT->Chat()
                Screen.CREATE->CreateStudio()
                Screen.SCHEDULE->Schedule()
                Screen.MORE->More(
                    profile=profile,
                    emailVerified=session?.emailVerified==true,
                    ownerAccessChecked=ownerAccessChecked,
                    canViewOwnerProfile=ownerProfileGrant!=null,
                    canViewOwnerTools=ownerToolsGrant!=null,
                    openOwnerProfile={scope.launch{refreshOwnerAccess();if(ownerProfileGrant!=null)selected=Screen.OWNER_PROFILE}},
                    openOwnerTools={scope.launch{refreshOwnerAccess();if(ownerToolsGrant!=null)selected=Screen.OWNER_TOOLS}},
                    signOut={scope.launch{authGateway.signOut();onSignedOut()}},
                )
                Screen.DMS->Dms()
                Screen.OWNER_PROFILE->if(ownerProfileGrant.isCurrentGrant(OwnerCapability.VIEW_OWNER_PROFILE)) OwnerProfile(profile){selected=Screen.MORE} else OwnerAccessDenied{selected=Screen.MORE}
                Screen.OWNER_TOOLS->if(ownerToolsGrant.isCurrentGrant(OwnerCapability.VIEW_OWNER_TOOLS)) OwnerTools{selected=Screen.MORE} else OwnerAccessDenied{selected=Screen.MORE}
                else->Home(profile){selected=it}
            }
        }
        if(selected!=Screen.OWNER_PROFILE&&selected!=Screen.OWNER_TOOLS){
            AppNavigationBar(selected=selected,onNavigate={selected=it})
        }
    }
}

@Composable fun Home(profile:Profile?,nav:(Screen)->Unit){ LazyColumn(Modifier.fillMaxSize().padding(16.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){ item{PatsyMotion("Hi ${profile?.displayName ?: "there"}! 👋",true,PatsyAction.HAPPY)}; item{Text("What would you like to do?",fontSize=20.sp,fontWeight=FontWeight.Bold)}; item{Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(8.dp)){Tile("💡","Get Ideas") {nav(Screen.CHAT)};Tile("🎨","Create") {nav(Screen.CREATE)};Tile("📈","Grow") {}}}; item{Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(8.dp)){Tile("💬","DMs") {nav(Screen.DMS)};Tile("#️⃣","Hashtags") {nav(Screen.CHAT)};Tile("🛠","Tools") {nav(Screen.MORE)}}}; item{Panel{Text("Patsy says…",fontWeight=FontWeight.Bold,fontSize=18.sp);Text("Ask me like you ask your AI. I can help search, plan, create and guide you.",color=Muted);Text("Rainbow active states + charcoal workspace",color=White)}} } }
@Composable fun RowScope.Tile(icon:String,text:String,click:()->Unit){Button(click,Modifier.weight(1f).height(92.dp),shape=RoundedCornerShape(18.dp),colors=ButtonDefaults.buttonColors(containerColor=Charcoal2,contentColor=White)){Column(horizontalAlignment=Alignment.CenterHorizontally){Text(icon,fontSize=26.sp);Text(text,fontSize=12.sp)}}}

@Composable fun Chat(){ var q by remember{mutableStateOf("")}; var answer by remember{mutableStateOf("Ask Patsy anything. Web/AI calls are routed through the configured secure backend.")}; Column(Modifier.fillMaxSize().padding(16.dp)){PatsyMotion("I'm your AI search. Ask me anything. 🐾",true,PatsyAction.TALKING); Panel{Text(answer,color=White)}; Spacer(Modifier.height(10.dp)); OutlinedTextField(q,{q=it},modifier=Modifier.fillMaxWidth(),placeholder={Text("Ask Patsy…")}); Spacer(Modifier.height(8.dp)); Primary("ASK PATSY / SEARCH WEB",{answer=if(q.isBlank())"Tell me what you want to know." else "Patsy received: $q\n\nConnect the secure AI/search backend to return live results."})} }

@Composable fun CreateStudio(){ var brightness by remember{mutableStateOf(0f)}; var contrast by remember{mutableStateOf(0f)}; var saturation by remember{mutableStateOf(0f)}; Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)){PatsyMotion("Tell me what to create or change. 🎨",true,PatsyAction.POINTING); Panel{Text("CREATION STUDIO",fontSize=24.sp,fontWeight=FontWeight.Bold); Text("High-spec creative workspace",color=Muted); Spacer(Modifier.height(10.dp)); Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){Button({}){Text("Import")};Button({}){Text("AI Image")};Button({}){Text("10s Video")}}; Text("Editor canvas",Modifier.padding(top=14.dp),fontWeight=FontWeight.Bold); Box(Modifier.fillMaxWidth().height(150.dp).background(Color(0xFF111216),RoundedCornerShape(18.dp)),contentAlignment=Alignment.Center){Text("Preview / canvas")}; Adjust("Brightness",brightness){brightness=it};Adjust("Contrast",contrast){contrast=it};Adjust("Saturation",saturation){saturation=it};Text("Filters • Crop • Resize • Rotate • Text • Stickers • Shapes • Layers • Undo • Redo • Reset • Export",color=Muted); Spacer(Modifier.height(8.dp)); OutlinedTextField("",{},modifier=Modifier.fillMaxWidth(),placeholder={Text("Tell Patsy: make this brighter / turn into a Reel / make a 10 second video…")}); Primary("APPLY WITH PATSY",{})} } }
@Composable fun Adjust(label:String,value:Float,onChange:(Float)->Unit){Text(label);Slider(value,{onChange(it)},valueRange=-1f..1f)}
@Composable fun Schedule(){Panel{Text("SCHEDULE",fontSize=24.sp,fontWeight=FontWeight.Bold);Text("Plan and organise content here. Backend calendar/publishing integrations plug into this surface.",color=Muted)}}
@Composable fun Dms(){Panel{Text("DMS",fontSize=24.sp,fontWeight=FontWeight.Bold);Text("Private messaging workspace. Authenticated access only.",color=Muted);Text("Conversation list → conversation → send → delivery states",color=Muted)}}
@Composable fun More(
    profile:Profile?,
    emailVerified:Boolean,
    ownerAccessChecked:Boolean,
    canViewOwnerProfile:Boolean,
    canViewOwnerTools:Boolean,
    openOwnerProfile:()->Unit,
    openOwnerTools:()->Unit,
    signOut:()->Unit,
){
    LazyColumn(Modifier.fillMaxSize().padding(16.dp),verticalArrangement=Arrangement.spacedBy(10.dp)){
        item{Panel{Text("PROFILE",fontSize=24.sp,fontWeight=FontWeight.Bold);Text(profile?.displayName ?: "Guest");Text(profile?.username ?: "",color=Muted);Text("Experience: ${profile?.mode ?: "Protected Mode"}",color=Muted);Text(if(emailVerified)"Email verified" else "Email verification pending",color=if(emailVerified)Color(0xFF9BE7B2) else Color(0xFFFFC46B))}}
        item{Panel{Text("SAFETY & PRIVACY",fontSize=20.sp,fontWeight=FontWeight.Bold);Text("16+ / Under-16 / Protected Mode; privacy, secure sessions, HTTPS service layer and safe AI routing.",color=Muted)}}
        if(!ownerAccessChecked){item{Text("Checking secure access…",color=Muted)}}
        if(canViewOwnerProfile){item{Primary("Owner Profile",openOwnerProfile)}}
        if(canViewOwnerTools){item{Primary("Owner Tools",openOwnerTools)}}
        item{OutlinedButton(signOut,Modifier.fillMaxWidth()){Text("Sign out",color=White)}}
    }
}

@Composable fun OwnerProfile(profile:Profile?,back:()->Unit){
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp)){
        Text("OWNER PROFILE",style=TextStyle(brush=Rainbow,fontSize=28.sp,fontWeight=FontWeight.ExtraBold))
        Text("Server-authorized private area",color=Muted)
        Spacer(Modifier.height(14.dp))
        Panel{
            Text("Patsy Owner",fontSize=22.sp,fontWeight=FontWeight.Bold)
            Text(profile?.username ?: "",color=Muted)
            Text("This screen appears only while a current VIEW_OWNER_PROFILE capability grant is present.",color=White,modifier=Modifier.padding(top=8.dp))
        }
        Spacer(Modifier.height(12.dp))
        OutlinedButton(back,Modifier.fillMaxWidth()){Text("Back to profile",color=White)}
    }
}

@Composable fun OwnerTools(back:()->Unit){
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp)){
        Text("OWNER TOOLS",style=TextStyle(brush=Rainbow,fontSize=28.sp,fontWeight=FontWeight.ExtraBold))
        Text("Server-authorized private area",color=Muted)
        Spacer(Modifier.height(14.dp))
        Panel{
            Text("Secure controls",fontSize=22.sp,fontWeight=FontWeight.Bold)
            Text("Analytics • Safety audit • Privacy • Backups • Provider diagnostics",color=Muted)
            Text("Each future privileged action must request its own server capability and be enforced again by that API. No local OWNER switch exists.",color=White,modifier=Modifier.padding(top=8.dp))
        }
        Spacer(Modifier.height(12.dp))
        OutlinedButton(back,Modifier.fillMaxWidth()){Text("Back to profile",color=White)}
    }
}

@Composable fun OwnerAccessDenied(back:()->Unit){
    Column(Modifier.fillMaxSize().padding(24.dp),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.Center){
        Text("Owner access unavailable",fontSize=24.sp,fontWeight=FontWeight.Bold)
        Text("A current server-verified capability is required.",color=Muted)
        Spacer(Modifier.height(16.dp))
        Primary("Back",back)
    }
}

@Composable fun AppNavigationBar(selected:Screen,onNavigate:(Screen)->Unit){ Row(Modifier.fillMaxWidth().background(Color(0xFF0A0A0B)).padding(6.dp),horizontalArrangement=Arrangement.SpaceEvenly){listOf(Screen.HOME to "⌂\nHome",Screen.CHAT to "💬\nChat",Screen.CREATE to "✎\nCreate",Screen.SCHEDULE to "▣\nSchedule",Screen.MORE to "•••\nMore").forEach{(s,t)->TextButton(onClick={onNavigate(s)}){Text(t,color=if(selected==s)White else Muted,fontSize=11.sp)}}} }

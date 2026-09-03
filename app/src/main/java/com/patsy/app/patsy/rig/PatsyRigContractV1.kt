package com.patsy.app.patsy.rig

/**
 * Names and value ranges shared by the Android app and the authored Patsy Rive file.
 *
 * Canonical 2 September baseline: artboard `Patsy`, 720x720 transparent. The app/runtime
 * contract remains dependency-neutral and does not claim that a final production .riv exists.
 */
object PatsyRigContractV1 {
    const val VERSION = 1
    const val ARTBOARD = "Patsy"
    const val ARTBOARD_WIDTH = 720
    const val ARTBOARD_HEIGHT = 720
    const val ARTBOARD_TRANSPARENT = true
    const val STATE_MACHINE = "PatsyAssistantMachine"
    const val VIEW_MODEL = "PatsyAssistantVM"
    const val DEFAULT_VIEW_MODEL_INSTANCE = "Default"

    object Property {
        const val MOTION_MODE = "motion/mode"
        const val MOTION_SPEED = "motion/speed"
        const val MOTION_FACING = "motion/facing"
        const val MOTION_ACTION_SEQUENCE = "motion/action_sequence"
        const val MOTION_POINT_X = "motion/point_x"
        const val MOTION_POINT_Y = "motion/point_y"
        const val MOTION_REDUCED = "motion/reduced"

        const val STAGE_X = "stage/x"
        const val STAGE_Y = "stage/y"
        const val STAGE_SCALE = "stage/scale"

        const val HEAD_LOOK_X = "head/look_x"
        const val HEAD_LOOK_Y = "head/look_y"
        const val HEAD_TILT = "head/tilt"

        const val EAR_LEFT_DRIVE = "ears/left_drive"
        const val EAR_RIGHT_DRIVE = "ears/right_drive"
        const val EAR_PHYSICS = "ears/physics_enabled"

        const val TAIL_DRIVE = "tail/drive"
        const val TAIL_ENERGY = "tail/energy"

        const val FACE_EXPRESSION = "face/expression"
        const val FACE_EXPRESSION_INTENSITY = "face/expression_intensity"
        const val FACE_BLINK_SEQUENCE = "face/blink_sequence"

        const val SPEECH_TALKING = "speech/talking"
        const val SPEECH_VISEME = "speech/viseme"
        const val SPEECH_VISEME_INTENSITY = "speech/viseme_intensity"
        const val SPEECH_ENERGY = "speech/energy"
    }

    object MotionValue {
        const val IDLE = "idle"
        const val WALK = "walk"
        const val RUN = "run"
        const val SIT = "sit"
        const val LIE = "lie"
        const val STAND = "stand"
        const val JUMP = "jump"
        const val WAVE = "wave"
        const val POINT = "point"
    }

    object ExpressionValue {
        const val NEUTRAL = "neutral"
        const val CHEEKY = "cheeky"
        const val EXCITED = "excited"
        const val CURIOUS = "curious"
        const val CONFUSED = "confused"
        const val CONCERNED = "concerned"
        const val PROUD = "proud"
        const val SLEEPY = "sleepy"
    }

    object VisemeValue {
        const val REST = "rest"
        const val A = "a"
        const val E = "e"
        const val I = "i"
        const val O = "o"
        const val U = "u"
        const val MBP = "mbp"
        const val FV = "fv"
        const val L = "l"
        const val SZ = "sz"
    }

    val requiredProperties: Set<String> = setOf(
        Property.MOTION_MODE,
        Property.MOTION_SPEED,
        Property.MOTION_FACING,
        Property.MOTION_ACTION_SEQUENCE,
        Property.MOTION_POINT_X,
        Property.MOTION_POINT_Y,
        Property.MOTION_REDUCED,
        Property.STAGE_X,
        Property.STAGE_Y,
        Property.STAGE_SCALE,
        Property.HEAD_LOOK_X,
        Property.HEAD_LOOK_Y,
        Property.HEAD_TILT,
        Property.EAR_LEFT_DRIVE,
        Property.EAR_RIGHT_DRIVE,
        Property.EAR_PHYSICS,
        Property.TAIL_DRIVE,
        Property.TAIL_ENERGY,
        Property.FACE_EXPRESSION,
        Property.FACE_EXPRESSION_INTENSITY,
        Property.FACE_BLINK_SEQUENCE,
        Property.SPEECH_TALKING,
        Property.SPEECH_VISEME,
        Property.SPEECH_VISEME_INTENSITY,
        Property.SPEECH_ENERGY,
    )
}

enum class PatsyRigMotion(val riveValue: String) {
    IDLE(PatsyRigContractV1.MotionValue.IDLE),
    WALK(PatsyRigContractV1.MotionValue.WALK),
    RUN(PatsyRigContractV1.MotionValue.RUN),
    SIT(PatsyRigContractV1.MotionValue.SIT),
    LIE(PatsyRigContractV1.MotionValue.LIE),
    STAND(PatsyRigContractV1.MotionValue.STAND),
    JUMP(PatsyRigContractV1.MotionValue.JUMP),
    WAVE(PatsyRigContractV1.MotionValue.WAVE),
    POINT(PatsyRigContractV1.MotionValue.POINT),
}

enum class PatsyRigExpression(val riveValue: String) {
    NEUTRAL(PatsyRigContractV1.ExpressionValue.NEUTRAL),
    CHEEKY(PatsyRigContractV1.ExpressionValue.CHEEKY),
    EXCITED(PatsyRigContractV1.ExpressionValue.EXCITED),
    CURIOUS(PatsyRigContractV1.ExpressionValue.CURIOUS),
    CONFUSED(PatsyRigContractV1.ExpressionValue.CONFUSED),
    CONCERNED(PatsyRigContractV1.ExpressionValue.CONCERNED),
    PROUD(PatsyRigContractV1.ExpressionValue.PROUD),
    SLEEPY(PatsyRigContractV1.ExpressionValue.SLEEPY),
}

enum class PatsyRigViseme(val riveValue: String) {
    REST(PatsyRigContractV1.VisemeValue.REST),
    A(PatsyRigContractV1.VisemeValue.A),
    E(PatsyRigContractV1.VisemeValue.E),
    I(PatsyRigContractV1.VisemeValue.I),
    O(PatsyRigContractV1.VisemeValue.O),
    U(PatsyRigContractV1.VisemeValue.U),
    MBP(PatsyRigContractV1.VisemeValue.MBP),
    FV(PatsyRigContractV1.VisemeValue.FV),
    L(PatsyRigContractV1.VisemeValue.L),
    SZ(PatsyRigContractV1.VisemeValue.SZ),
}

data class PatsyRigPose(
    val motion: PatsyRigMotion = PatsyRigMotion.IDLE,
    val motionSpeed: Float = 0f,
    val facing: Float = 1f,
    val pointX: Float = 0.5f,
    val pointY: Float = 0.5f,
    val stageX: Float = 0.5f,
    val stageY: Float = 0.75f,
    val stageScale: Float = 1f,
    val lookX: Float = 0f,
    val lookY: Float = 0f,
    val headTilt: Float = 0f,
    val leftEarDrive: Float = 0f,
    val rightEarDrive: Float = 0f,
    val earPhysicsEnabled: Boolean = true,
    val tailDrive: Float = 0f,
    val tailEnergy: Float = 0.35f,
    val expression: PatsyRigExpression = PatsyRigExpression.CHEEKY,
    val expressionIntensity: Float = 0.5f,
    val talking: Boolean = false,
    val viseme: PatsyRigViseme = PatsyRigViseme.REST,
    val visemeIntensity: Float = 0f,
    val speechEnergy: Float = 0f,
    val reducedMotion: Boolean = false,
) {
    fun normalised(): PatsyRigPose = copy(
        motionSpeed = motionSpeed.coerceIn(0f, 1f),
        facing = if (facing < 0f) -1f else 1f,
        pointX = pointX.coerceIn(0f, 1f),
        pointY = pointY.coerceIn(0f, 1f),
        stageX = stageX.coerceIn(0f, 1f),
        stageY = stageY.coerceIn(0f, 1f),
        stageScale = stageScale.coerceIn(0.45f, 1.4f),
        lookX = lookX.coerceIn(-1f, 1f),
        lookY = lookY.coerceIn(-1f, 1f),
        headTilt = headTilt.coerceIn(-1f, 1f),
        leftEarDrive = leftEarDrive.coerceIn(-1f, 1f),
        rightEarDrive = rightEarDrive.coerceIn(-1f, 1f),
        tailDrive = tailDrive.coerceIn(-1f, 1f),
        tailEnergy = tailEnergy.coerceIn(0f, 1f),
        expressionIntensity = expressionIntensity.coerceIn(0f, 1f),
        visemeIntensity = visemeIntensity.coerceIn(0f, 1f),
        speechEnergy = speechEnergy.coerceIn(0f, 1f),
    ).let { pose ->
        if (!pose.reducedMotion) pose else pose.copy(
            motion = when (pose.motion) {
                PatsyRigMotion.SIT, PatsyRigMotion.LIE -> pose.motion
                else -> PatsyRigMotion.IDLE
            },
            motionSpeed = 0f,
            stageScale = pose.stageScale.coerceIn(0.8f, 1.1f),
            leftEarDrive = pose.leftEarDrive * 0.2f,
            rightEarDrive = pose.rightEarDrive * 0.2f,
            tailEnergy = pose.tailEnergy * 0.2f,
        )
    }
}

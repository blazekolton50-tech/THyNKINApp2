package com.patsy.app.patsy.rig

/** Values supported by the planned Rive View Model adapter. */
sealed interface PatsyRigValue {
    data class Number(val value: Float) : PatsyRigValue
    data class Boolean(val value: kotlin.Boolean) : PatsyRigValue
    data class Enum(val value: String) : PatsyRigValue
}

data class PatsyRigMutation(
    val propertyPath: String,
    val value: PatsyRigValue,
)

/**
 * Runtime boundary implemented later by a Rive Android/Compose adapter.
 *
 * The adapter must validate the artboard, state machine, view model and required property paths
 * before reporting [Ready]. Calls made before readiness must be retained as latest-state only.
 */
interface PatsyRigRuntimePort : AutoCloseable {
    val status: PatsyRigStatus

    fun apply(mutations: List<PatsyRigMutation>)

    override fun close() = Unit
}

sealed interface PatsyRigStatus {
    data object Detached : PatsyRigStatus
    data object Loading : PatsyRigStatus
    data object Ready : PatsyRigStatus
    data class InvalidAsset(val missingContractItems: Set<String>) : PatsyRigStatus
    data class Failed(val safeMessage: String) : PatsyRigStatus
}

/**
 * Converts app state into the exact data-binding property paths required by the rig contract.
 * One-shot actions are retriggered by monotonically increasing sequence values.
 */
class PatsyRigCoordinator(
    private val runtime: PatsyRigRuntimePort,
) {
    private var actionSequence = 0f
    private var blinkSequence = 0f

    fun render(pose: PatsyRigPose) {
        val p = pose.normalised()
        runtime.apply(
            listOf(
                enum(PatsyRigContractV1.Property.MOTION_MODE, p.motion.riveValue),
                number(PatsyRigContractV1.Property.MOTION_SPEED, p.motionSpeed),
                number(PatsyRigContractV1.Property.MOTION_FACING, p.facing),
                number(PatsyRigContractV1.Property.MOTION_POINT_X, p.pointX),
                number(PatsyRigContractV1.Property.MOTION_POINT_Y, p.pointY),
                bool(PatsyRigContractV1.Property.MOTION_REDUCED, p.reducedMotion),
                number(PatsyRigContractV1.Property.STAGE_X, p.stageX),
                number(PatsyRigContractV1.Property.STAGE_Y, p.stageY),
                number(PatsyRigContractV1.Property.STAGE_SCALE, p.stageScale),
                number(PatsyRigContractV1.Property.HEAD_LOOK_X, p.lookX),
                number(PatsyRigContractV1.Property.HEAD_LOOK_Y, p.lookY),
                number(PatsyRigContractV1.Property.HEAD_TILT, p.headTilt),
                number(PatsyRigContractV1.Property.EAR_LEFT_DRIVE, p.leftEarDrive),
                number(PatsyRigContractV1.Property.EAR_RIGHT_DRIVE, p.rightEarDrive),
                bool(PatsyRigContractV1.Property.EAR_PHYSICS, p.earPhysicsEnabled),
                number(PatsyRigContractV1.Property.TAIL_DRIVE, p.tailDrive),
                number(PatsyRigContractV1.Property.TAIL_ENERGY, p.tailEnergy),
                enum(PatsyRigContractV1.Property.FACE_EXPRESSION, p.expression.riveValue),
                number(
                    PatsyRigContractV1.Property.FACE_EXPRESSION_INTENSITY,
                    p.expressionIntensity,
                ),
                bool(PatsyRigContractV1.Property.SPEECH_TALKING, p.talking),
                enum(PatsyRigContractV1.Property.SPEECH_VISEME, p.viseme.riveValue),
                number(PatsyRigContractV1.Property.SPEECH_VISEME_INTENSITY, p.visemeIntensity),
                number(PatsyRigContractV1.Property.SPEECH_ENERGY, p.speechEnergy),
            )
        )
    }

    fun retriggerAction(action: PatsyRigMotion) {
        require(action in oneShotActions) { "$action is continuous and does not need retriggering" }
        actionSequence = nextSequence(actionSequence)
        runtime.apply(
            listOf(
                enum(PatsyRigContractV1.Property.MOTION_MODE, action.riveValue),
                number(PatsyRigContractV1.Property.MOTION_ACTION_SEQUENCE, actionSequence),
            )
        )
    }

    fun blink() {
        blinkSequence = nextSequence(blinkSequence)
        runtime.apply(
            listOf(number(PatsyRigContractV1.Property.FACE_BLINK_SEQUENCE, blinkSequence))
        )
    }

    private fun number(path: String, value: Float) =
        PatsyRigMutation(path, PatsyRigValue.Number(value))

    private fun bool(path: String, value: Boolean) =
        PatsyRigMutation(path, PatsyRigValue.Boolean(value))

    private fun enum(path: String, value: String) =
        PatsyRigMutation(path, PatsyRigValue.Enum(value))

    private fun nextSequence(current: Float): Float = if (current >= 16_000_000f) 1f else current + 1f

    private companion object {
        val oneShotActions = setOf(
            PatsyRigMotion.JUMP,
            PatsyRigMotion.WAVE,
            PatsyRigMotion.POINT,
        )
    }
}

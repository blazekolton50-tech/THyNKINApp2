package com.patsy.app.patsy

/**
 * Contract for the authored Patsy character rig.
 *
 * The production implementation must drive one continuously animated character (for example,
 * a Rive or Live2D skeletal/mesh rig). It must not simulate animation by visibly swapping
 * unrelated still pictures. The current Compose cutout is a transparent moving fallback only.
 */
interface PatsyAnimationController {
    fun look(horizontal: Float, vertical: Float)
    fun moveTo(normalizedX: Float, normalizedY: Float)
    fun setEarPose(left: PatsyEarPose, right: PatsyEarPose)
    fun setEarPhysics(airflowX: Float, verticalAcceleration: Float, damping: Float)
    fun setExpression(expression: PatsyExpression)
    fun setBodyAction(action: PatsyBodyAction)
    fun setSpeechViseme(viseme: PatsyViseme, intensity: Float)
    fun setRestingScale(scale: Float)
}

enum class PatsyEarPose {
    RELAXED, LISTENING, LIFTED, FLOPPING, BACK, ASYMMETRIC
}

enum class PatsyExpression {
    CHEEKY, EXCITED, CURIOUS, CONFUSED, CONCERNED, PROUD, SLEEPY
}

enum class PatsyBodyAction {
    IDLE, WALK, SIT, STAND, LIE_DOWN, JUMP, WAVE, POINT
}

enum class PatsyViseme {
    REST, A, E, I, O, U, M_B_P, F_V, L, S_Z
}

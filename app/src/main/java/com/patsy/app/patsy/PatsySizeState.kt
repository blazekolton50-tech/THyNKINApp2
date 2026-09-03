package com.patsy.app.patsy

/**
 * Canonical Patsy visual size states.
 *
 * Big = 300 dp / scale 1.0 = roughly two thumbnail widths.
 * Mini = 150 dp / scale 0.5 = roughly one thumbnail width.
 */
enum class PatsySizeState(
    val scale: Float,
    val dp: Int,
) {
    Big(scale = 1.0f, dp = 300),
    Mini(scale = 0.5f, dp = 150),
}

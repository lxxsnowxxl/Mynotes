package com.example.mynotes.ui.notes

/** Fixed, small angles avoid collisions with neighbouring notes. */
internal object IdleAngularMotionPolicy {
    fun maxAngleDegrees(level: Int): Float = when (level.coerceIn(1, 5)) {
        1 -> 0.55f
        2 -> 0.85f
        3 -> 1.20f
        4 -> 1.65f
        else -> 2.10f
    }

    fun amplitudeDegrees(level: Int, variation: Double): Float =
        maxAngleDegrees(level) * variation.coerceIn(0.85, 1.12).toFloat()
}

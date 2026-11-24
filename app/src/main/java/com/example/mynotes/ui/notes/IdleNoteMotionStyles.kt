package com.example.mynotes.ui.notes

/** Stable preference keys shared by existing installs and the current animation presets. */
internal object IdleNoteMotionStyles {
    const val ANGULAR = "angular"
    const val SWAY = "sway"
    const val BREATHE = "breathe"
    const val HEARTBEAT = "heartbeat"
    const val SPRING = "spring"
    const val RANDOM = "random"

    val all = listOf(ANGULAR, SWAY, BREATHE, HEARTBEAT, SPRING, RANDOM)
    val concrete = listOf(ANGULAR, SWAY, BREATHE, HEARTBEAT, SPRING)

    fun normalize(styleId: String?): String =
        if (styleId in all) styleId!! else ANGULAR

    /** Base stretch at each intensity level. Individual frames shape the horizontal and vertical stretch. */
    fun maxScaleDelta(level: Int): Float = when (level.coerceIn(1, 5)) {
        1 -> 0.006f
        2 -> 0.010f
        3 -> 0.014f
        4 -> 0.019f
        else -> 0.024f
    }

    fun swayAngle(level: Int): Float = IdleAngularMotionPolicy.maxAngleDegrees(level) * 0.75f
}

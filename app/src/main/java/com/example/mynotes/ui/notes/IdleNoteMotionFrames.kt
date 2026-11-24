package com.example.mynotes.ui.notes

import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing

/**
 * Travel, turn and stretch are relative to the intensity limits. Pitch and yaw are
 * in degrees. Easing applies from this frame to the next one.
 */
internal class IdleNoteMotionFrame(
    val millis: Int,
    val slide: Float = 0f,
    val lift: Float = 0f,
    val turn: Float = 0f,
    val pitch: Float = 0f,
    val yaw: Float = 0f,
    val stretchX: Float = 0f,
    val stretchY: Float = 0f,
    val easing: Easing = FastOutSlowInEasing
)

internal object IdleNoteMotionFrames {
    private val sideShuffle = listOf(
        IdleNoteMotionFrame(0),
        IdleNoteMotionFrame(105, slide = -0.22f, turn = -0.24f, stretchY = -0.3f, easing = LinearOutSlowInEasing),
        IdleNoteMotionFrame(265, slide = 1f, lift = 0.19f, turn = 0.80f, stretchX = 0.35f),
        IdleNoteMotionFrame(465, slide = -0.82f, lift = 0.11f, turn = -0.72f, stretchY = -0.3f),
        IdleNoteMotionFrame(650, slide = 0.38f, lift = 0.09f, turn = 0.30f),
        IdleNoteMotionFrame(830, slide = -0.10f, turn = -0.12f, easing = LinearOutSlowInEasing),
        IdleNoteMotionFrame(1010)
    )

    private val depthTilt = listOf(
        IdleNoteMotionFrame(0),
        IdleNoteMotionFrame(500, slide = -0.36f, lift = 0.26f, turn = -0.53f,
            pitch = 7f, yaw = -13f, stretchX = -0.25f, stretchY = 0.40f),
        IdleNoteMotionFrame(1260, slide = 0.44f, lift = 0.36f, turn = 0.58f,
            pitch = -5f, yaw = 14f, stretchX = -0.20f, stretchY = 0.23f),
        IdleNoteMotionFrame(1790, slide = -0.16f, lift = 0.12f, turn = -0.21f,
            pitch = 2f, yaw = -4f, easing = LinearOutSlowInEasing),
        IdleNoteMotionFrame(2160)
    )

    private val floatAndSettle = listOf(
        IdleNoteMotionFrame(0, easing = LinearOutSlowInEasing),
        IdleNoteMotionFrame(570, slide = -0.22f, lift = 0.72f, turn = -0.35f, stretchY = 0.55f),
        IdleNoteMotionFrame(1240, slide = 0.48f, lift = 1f, turn = 0.43f,
            stretchX = 0.52f, stretchY = 0.75f, easing = FastOutLinearInEasing),
        IdleNoteMotionFrame(1890, slide = 0.20f, lift = 0.05f, turn = 0.19f,
            stretchX = 0.28f, stretchY = -0.22f, easing = LinearOutSlowInEasing),
        IdleNoteMotionFrame(2310)
    )

    private val doublePulse = listOf(
        IdleNoteMotionFrame(0),
        IdleNoteMotionFrame(115, stretchX = -0.42f, stretchY = -0.60f, easing = LinearOutSlowInEasing),
        IdleNoteMotionFrame(245, lift = 0.36f, stretchX = 1.20f, stretchY = 1.45f),
        IdleNoteMotionFrame(425, lift = 0.04f, stretchX = -0.16f, stretchY = -0.27f, easing = LinearOutSlowInEasing),
        IdleNoteMotionFrame(560, lift = 0.19f, stretchX = 0.65f, stretchY = 0.86f),
        IdleNoteMotionFrame(790, stretchX = -0.10f, stretchY = -0.14f, easing = LinearOutSlowInEasing),
        IdleNoteMotionFrame(1010)
    )

    private val elasticBounce = listOf(
        IdleNoteMotionFrame(0),
        IdleNoteMotionFrame(205, lift = -0.12f, stretchX = 0.85f, stretchY = -1.30f, easing = LinearOutSlowInEasing),
        IdleNoteMotionFrame(470, lift = 0.91f, turn = 0.28f, stretchX = -0.68f,
            stretchY = 1.50f, easing = FastOutLinearInEasing),
        IdleNoteMotionFrame(735, lift = 0.04f, turn = -0.17f, stretchX = 0.51f,
            stretchY = -0.84f, easing = LinearOutSlowInEasing),
        IdleNoteMotionFrame(920, lift = 0.39f, turn = 0.16f, stretchX = -0.26f,
            stretchY = 0.53f, easing = FastOutLinearInEasing),
        IdleNoteMotionFrame(1095, stretchX = 0.16f, stretchY = -0.21f, easing = LinearOutSlowInEasing),
        IdleNoteMotionFrame(1380)
    )

    fun forStyle(style: String): List<IdleNoteMotionFrame> = when (style) {
        IdleNoteMotionStyles.ANGULAR -> sideShuffle
        IdleNoteMotionStyles.SWAY -> depthTilt
        IdleNoteMotionStyles.BREATHE -> floatAndSettle
        IdleNoteMotionStyles.HEARTBEAT -> doublePulse
        IdleNoteMotionStyles.SPRING -> elasticBounce
        else -> error("Expected a concrete card animation style")
    }
}

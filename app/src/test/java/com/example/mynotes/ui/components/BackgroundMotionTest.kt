package com.example.mynotes.ui.components

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs

class BackgroundMotionTest {
    @Test
    fun animationNeedsTheSelectedStyleEnabledAnimationsAndAConsumer() {
        for (style in listOf("solid", "gradient", "blur", "animated_blur", "unknown")) {
            for (enabled in listOf(false, true)) {
                for (consumers in listOf(-1, 0, 1, 3)) {
                    assertEquals((style == "animated_blur") && enabled && consumers > 0,
                        backgroundMotionEnabled(style, enabled, consumers))
                }
            }
        }
    }

    @Test
    fun performanceProfilesBoundTheUpdateRateAndHandleInvalidSpeed() {
        val performance = backgroundMotionProfile("performance", 1f)
        val balanced = backgroundMotionProfile("balanced", 1f)
        val quality = backgroundMotionProfile("quality", 1f)
        assertTrue(performance.frameDelayMillis > balanced.frameDelayMillis)
        assertTrue(balanced.frameDelayMillis > quality.frameDelayMillis)
        assertTrue(quality.frameDelayMillis >= 32L)
        assertEquals(balanced, backgroundMotionProfile("unknown", Float.NaN))
        for (mode in listOf("performance", "balanced", "quality")) {
            for (speed in listOf(-10f, 0.5f, 1f, 2f, 100f, Float.POSITIVE_INFINITY)) {
                assertTrue(backgroundMotionProfile(mode, speed).cycleMillis in 5_000L..28_000L)
            }
        }
    }

    @Test
    fun movementIsPeriodicAndTheCentersStayInsideTheReferenceBounds() {
        SettingsAnimatedBlurSpots.forEachIndexed { index, spot ->
            val start = settingsBlurMotionOffset(index, 0f)
            val end = settingsBlurMotionOffset(index, 1f)
            assertEquals(start.x, end.x, 0.00001f)
            assertEquals(start.y, end.y, 0.00001f)
            for (step in 0..360) {
                val offset = settingsBlurMotionOffset(index, step / 360f)
                assertTrue(abs(offset.x) <= 0.25001f && abs(offset.y) <= 0.20001f)
                assertTrue(spot.x + offset.x in -0.00001f..1.00001f)
                assertTrue(spot.y + offset.y in -0.00001f..1.00001f)
            }
        }
        assertFalse(backgroundMotionEnabled("animated_blur", false, 1))
        assertFalse(backgroundMotionEnabled("liquid_glass", true, 1))
    }

    @Test
    fun movementCoversAtLeastTwentyDpWithinOneSecondAtNormalSpeed() {
        for (mode in listOf("performance", "balanced", "quality")) {
            val phase = 1_000f / backgroundMotionProfile(mode, 1f).cycleMillis
            SettingsAnimatedBlurSpots.indices.forEach { index ->
                val start = settingsBlurMotionOffset(index, 0f)
                val next = settingsBlurMotionOffset(index, phase)
                val dx = (next.x - start.x) * 411f
                val dy = (next.y - start.y) * 891f
                assertTrue(dx * dx + dy * dy > 20f * 20f)
            }
        }
    }
}

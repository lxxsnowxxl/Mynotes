package com.example.mynotes.settings

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MotionStylePolicyTest {
    @Test
    fun savedLegacyStylesMigrateWithoutReturningRetiredKeys() {
        val groups = mapOf(
            "cross_dissolve" to listOf("fade", "subtle"),
            "lift_card" to listOf("zoom", "zoom_fade", "slide_up", "slide_down", "slide_zoom_up",
                "axis_y", "bounce", "elastic", "expressive_spring"),
            "lateral_stack" to listOf("slide_left", "slide_right", "slide_zoom_left", "axis_x", "predictive"),
            "depth_pull" to listOf("axis_z", "pop", "tonal_pop"),
            "corner_unfold" to listOf("expand", "expand_horizontal", "expand_vertical", "container_transform"),
            "focus_swap" to listOf("soft_reveal"),
            "diagonal_flow" to listOf("elastic_slide")
        )
        assertEquals(25, groups.values.sumOf { it.size })
        groups.forEach { (expected, oldKeys) ->
            oldKeys.forEach { old ->
                val migrated = MotionStylePolicy.normalize(old)
                assertEquals(old, expected, migrated)
                assertFalse(old, old in MotionStylePolicy.supportedStyles)
                assertEquals(migrated, MotionStylePolicy.normalize(migrated))
            }
        }
    }

    @Test
    fun newSelectionsSurviveSavingAndImportingAgain() {
        MotionStylePolicy.supportedStyles.forEach { saved ->
            assertEquals(saved, MotionStylePolicy.normalize(MotionStylePolicy.normalize(saved)))
        }
        assertEquals(MotionStylePolicy.DEFAULT_STYLE, AppSettings().animationStyle)
    }

    @Test
    fun invalidValuesHaveASafeDefaultAndRandomCannotSelectItself() {
        listOf("", "unknown", "ZOOM", "deleted_style").forEach { raw ->
            assertEquals(MotionStylePolicy.DEFAULT_STYLE, MotionStylePolicy.normalize(raw))
        }
        assertEquals("random", MotionStylePolicy.normalize("random"))
        assertTrue(MotionStylePolicy.concreteStyles.isNotEmpty())
        assertFalse("random" in MotionStylePolicy.concreteStyles)
        assertEquals(MotionStylePolicy.supportedStyles - "random", MotionStylePolicy.concreteStyles.toSet())
    }
}

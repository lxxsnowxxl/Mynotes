package com.example.mynotes.settings

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.mutablePreferencesOf
import androidx.datastore.preferences.core.stringPreferencesKey
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SettingsBackgroundSelectionTest {
    private val styleKey = stringPreferencesKey("settings_background_style")
    private val motionKey = booleanPreferencesKey("animations_enabled")

    @Test
    fun choosingAnimatedBlurStoresItsStyleAndEnablesMovementTogether() {
        val preferences = mutablePreferencesOf(styleKey to "solid", motionKey to false)
        assertTrue(SettingsRepository.writeSettingsBackgroundStyle(preferences, "animated_blur"))
        assertEquals("animated_blur", preferences[styleKey])
        assertEquals(true, preferences[motionKey])
        assertFalse(SettingsRepository.writeSettingsBackgroundStyle(preferences, "animated_blur"))
    }

    @Test
    fun choosingTheSameAnimatedTileResumesPreviouslyDisabledMovement() {
        val preferences = mutablePreferencesOf(styleKey to "animated_blur", motionKey to false)
        assertTrue(SettingsRepository.writeSettingsBackgroundStyle(preferences, "animated_blur"))
        assertEquals(true, preferences[motionKey])
        assertEquals("animated_blur", preferences[styleKey])
    }

    @Test
    fun removedGlassAndFluidPreferencesSafelyUseGradient() {
        for (removed in listOf("fluid_liquid", "liquid_glass")) {
            assertEquals("gradient", SettingsValuePolicy.settingsBackgroundStyle(removed))
            val preferences = mutablePreferencesOf(styleKey to "solid", motionKey to false)
            assertTrue(SettingsRepository.writeSettingsBackgroundStyle(preferences, removed))
            assertEquals("gradient", preferences[styleKey])
            assertEquals(false, preferences[motionKey])
        }
    }

    @Test
    fun staticStylesAndUnknownValuesPreserveTheAnimationPreference() {
        for (style in listOf("solid", "gradient", "blur", "unknown")) {
            for (enabled in listOf(false, true)) {
                val preferences = mutablePreferencesOf(styleKey to "animated_blur", motionKey to enabled)
                SettingsRepository.writeSettingsBackgroundStyle(preferences, style)
                assertEquals(if (style == "unknown") "solid" else style, preferences[styleKey])
                assertEquals(enabled, preferences[motionKey])
            }
        }
    }
}

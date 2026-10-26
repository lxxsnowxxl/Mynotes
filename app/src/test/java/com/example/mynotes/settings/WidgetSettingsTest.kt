package com.example.mynotes.settings

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.mutablePreferencesOf
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.mynotes.ui.theme.PaletteCatalog
import org.junit.Assert.assertEquals
import org.junit.Test

class WidgetSettingsTest {
    private val paletteKey = stringPreferencesKey("background_color")
    private val toneKey = intPreferencesKey("background_tone_index")
    private val languageKey = stringPreferencesKey("language")
    private val darkKey = booleanPreferencesKey("dark_mode")

    @Test
    fun preservesDefaults() {
        assertEquals(WidgetSettings(false, "neutral", 0, "system"), SettingsRepository.readWidgetSettings(emptyPreferences()))
    }

    @Test
    fun preservesEveryCurrentPaletteAndClampsToneWithoutLegacyInversion() {
        for (palette in PaletteCatalog.palettes) {
            for (tone in listOf(Int.MIN_VALUE, -1, 0, 1, 2, 3, 4, 5, Int.MAX_VALUE)) {
                val preferences = mutablePreferencesOf(paletteKey to palette.key, toneKey to tone)
                val actual = SettingsRepository.readWidgetSettings(preferences)
                assertEquals(palette.key, actual.backgroundColor)
                assertEquals(tone.coerceIn(0, palette.tones.lastIndex), actual.backgroundToneIndex)
            }
        }
    }

    @Test
    fun preservesLegacyPaletteMigrationIncludingMissingTone() {
        val aliases = mapOf("yellow" to "sun", "purple" to "lavender", "pink" to "soft_pink", "default" to "neutral", "gray" to "neutral")
        for ((oldKey, currentKey) in aliases) {
            assertEquals(WidgetSettings(false, currentKey, 0, "system"), SettingsRepository.readWidgetSettings(mutablePreferencesOf(paletteKey to oldKey)))
            for ((oldTone, expectedTone) in listOf(-1 to 3, 0 to 3, 1 to 2, 2 to 1, 3 to 0, 4 to 0)) {
                val actual = SettingsRepository.readWidgetSettings(mutablePreferencesOf(paletteKey to oldKey, toneKey to oldTone))
                assertEquals(currentKey, actual.backgroundColor)
                assertEquals(expectedTone, actual.backgroundToneIndex)
            }
        }
    }

    @Test
    fun keepsLanguageAndThemeFromTheSamePreferencesSnapshot() {
        val preferences = mutablePreferencesOf(paletteKey to "lavender", toneKey to 4, languageKey to "es-MX", darkKey to true)
        val first = SettingsRepository.readWidgetSettings(preferences)
        preferences[languageKey] = "system"
        preferences[darkKey] = false
        val second = SettingsRepository.readWidgetSettings(preferences)
        assertEquals(WidgetSettings(true, "lavender", 4, "es-MX"), first)
        assertEquals(WidgetSettings(false, "lavender", 4, "system"), second)
    }

    @Test
    fun unrelatedSettingsDoNotChangeWidgetAppearance() {
        val preferences = mutablePreferencesOf(paletteKey to "neutral", toneKey to 2)
        val before = SettingsRepository.readWidgetSettings(preferences)
        preferences[stringPreferencesKey("font")] = "serif"
        preferences[intPreferencesKey("grid_columns")] = 3
        preferences[stringPreferencesKey("sound_effects_theme")] = "classic"
        assertEquals(before, SettingsRepository.readWidgetSettings(preferences))
    }

    @Test
    fun unknownPaletteUsesNeutralWithoutApplyingLegacyToneRules() {
        val preferences = mutablePreferencesOf(paletteKey to "missing_palette", toneKey to 4)
        assertEquals(WidgetSettings(false, "neutral", 4, "system"), SettingsRepository.readWidgetSettings(preferences))
    }
}

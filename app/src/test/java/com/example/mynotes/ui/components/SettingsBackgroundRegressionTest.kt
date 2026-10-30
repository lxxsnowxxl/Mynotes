package com.example.mynotes.ui.components

import androidx.compose.ui.graphics.Color
import com.example.mynotes.settings.AppSettings
import com.example.mynotes.settings.SettingsValuePolicy
import com.example.mynotes.ui.theme.PaletteCatalog
import com.example.mynotes.ui.theme.uiContrastRatio
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SettingsBackgroundRegressionTest {
    @Test
    fun globalAppearanceKeepsTheSavedStyleInEveryConfigurationMode() {
        listOf("basic", "advanced", "unset").forEach { mode ->
            listOf("solid", "gradient", "blur").forEach { style ->
                val appearance = appBackgroundAppearance(AppSettings(
                    configurationMode = mode, settingsBackgroundStyle = style,
                    backgroundColor = "ocean", backgroundToneIndex = 4
                ))
                assertEquals(style, appearance.style)
                assertEquals("ocean", appearance.palette.key)
                assertEquals(4, appearance.toneIndex)
            }
        }
    }

    @Test
    fun globalAppearanceNormalizesInvalidValuesFromOlderSettings() {
        val appearance = appBackgroundAppearance(AppSettings(
            settingsBackgroundStyle = "unknown", backgroundColor = "unknown", backgroundToneIndex = Int.MAX_VALUE
        ))
        assertEquals("solid", appearance.style)
        assertEquals("neutral", appearance.palette.key)
        assertEquals(4, appearance.toneIndex)
    }

    @Test
    fun tintedBackgroundsPreserveReadableAutomaticTextForEverySavedTone() {
        PaletteCatalog.palettes.forEach { palette ->
            palette.tones.forEachIndexed { index, base ->
                val foreground = if (uiContrastRatio(Color.Black, base) >= uiContrastRatio(Color.White, base)) Color.Black else Color.White
                val colors = settingsBackgroundColors(palette, index, base, foreground)
                assertEquals(base, colors.base)
                listOf(colors.first, colors.second).forEach { color ->
                    assertTrue("${palette.key}:$index", uiContrastRatio(foreground, color) >= 4.5f)
                    assertEquals(1f, color.alpha, 0f)
                }
            }
        }
    }

    @Test
    fun existingSettingsAndInvalidStylesUseSolidBackground() {
        assertEquals("solid", AppSettings(configurationMode = "basic").settingsBackgroundStyle)
        assertEquals("solid", SettingsValuePolicy.settingsBackgroundStyle("unknown"))
        listOf("solid", "gradient", "blur").forEach { style ->
            assertEquals(style, SettingsValuePolicy.settingsBackgroundStyle(style))
        }
    }
}

package com.example.mynotes.ui.components

import androidx.compose.ui.graphics.Color
import com.example.mynotes.settings.AppSettings
import com.example.mynotes.settings.SettingsValuePolicy
import com.example.mynotes.ui.theme.PaletteCatalog
import com.example.mynotes.ui.theme.automaticUiTextColor
import com.example.mynotes.ui.theme.resolveAppColorScheme
import com.example.mynotes.ui.theme.uiContrastRatio
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SettingsBackgroundRegressionTest {
    @Test
    fun globalAppearanceKeepsTheSavedStyleInEveryConfigurationMode() {
        PaletteCatalog.settingsPalettes.forEach { palette ->
            palette.tones.indices.forEach { index ->
                listOf("basic", "advanced", "unset").forEach { mode ->
                    listOf("solid", "gradient", "blur", "animated_blur").forEach { style ->
                        val appearance = appBackgroundAppearance(AppSettings(
                            configurationMode = mode, settingsBackgroundStyle = style,
                            backgroundColor = palette.key, backgroundToneIndex = index
                        ))
                        assertEquals(style, appearance.style)
                        assertEquals(palette.key, appearance.palette.key)
                        assertEquals(index, appearance.toneIndex)
                    }
                }
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
                assertTrue("${palette.key}:$index must retain an effect", colors.first != base || colors.second != base)
                listOf(colors.first, colors.second).forEach { color ->
                    assertTrue("${palette.key}:$index", uiContrastRatio(foreground, color) >= 4.5f)
                    assertEquals(1f, color.alpha, 0f)
                }
            }
        }
    }

    @Test
    fun everyPaletteToneRetainsAnEffectForLightAndDarkThemesAndManualText() {
        PaletteCatalog.settingsPalettes.forEach { palette ->
            palette.tones.indices.forEach { index ->
                listOf(false, true).forEach { dark ->
                    listOf("auto", "black", "white").forEach { textMode ->
                        val scheme = resolveAppColorScheme(dark, palette, index, 72f, textMode, "palette")
                        val colors = settingsBackgroundColors(palette, index, scheme.background, scheme.onBackground)
                        val message = "${palette.key}:$index dark=$dark text=$textMode"
                        assertEquals(message, scheme.background, colors.base)
                        assertTrue(message, colors.first != colors.base || colors.second != colors.base)
                        val minimum = minOf(4.5f, uiContrastRatio(scheme.onBackground, scheme.background))
                        listOf(colors.first, colors.second).forEach { color ->
                            assertTrue(message, uiContrastRatio(scheme.onBackground, color) >= minimum)
                            assertEquals(message, 1f, color.alpha, 0f)
                        }
                    }
                }
            }
        }
    }

    @Test
    fun neonYellowFourthToneKeepsVisibleTintsAndReadableText() {
        val palette = PaletteCatalog.find("neon_yellow")
        val base = palette.toneAt(3)
        val foreground = automaticUiTextColor(base)
        val colors = settingsBackgroundColors(palette, 3, base, foreground)
        assertTrue(colors.first != base)
        assertTrue(colors.second != base)
        assertTrue(uiContrastRatio(foreground, colors.first) >= 4.5f)
        assertTrue(uiContrastRatio(foreground, colors.second) >= 4.5f)
    }

    @Test
    fun existingSettingsAndInvalidStylesUseSolidBackground() {
        assertEquals("solid", AppSettings(configurationMode = "basic").settingsBackgroundStyle)
        assertEquals("solid", SettingsValuePolicy.settingsBackgroundStyle("unknown"))
        listOf("solid", "gradient", "blur", "animated_blur").forEach { style ->
            assertEquals(style, SettingsValuePolicy.settingsBackgroundStyle(style))
        }
    }
}

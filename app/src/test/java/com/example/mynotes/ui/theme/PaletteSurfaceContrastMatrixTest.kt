package com.example.mynotes.ui.theme

import androidx.compose.ui.graphics.Color
import com.example.mynotes.ui.workspace.workspaceDrawerColors
import com.example.mynotes.ui.workspace.workspaceToolColorScheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Regresión de contraste para las 46 paletas × 5 tonos. No modifica preferencias,
 * contenido de notas, fotografías, lienzos ni los píxeles de archivos PDF.
 */
class PaletteSurfaceContrastMatrixTest {
    private fun assertContrast(label: String, ink: Color, surface: Color, minimum: Float) {
        val ratio = uiContrastRatio(ink, surface)
        assertTrue("$label -> $ratio: requiere $minimum", ratio >= minimum - 0.01f)
    }

    @Test fun selectedBackgroundToneIsUsedInBothModesAndAllManualTextSettings() {
        for (palette in PaletteCatalog.palettes) {
            for (toneIndex in palette.tones.indices) {
                for (dark in listOf(false, true)) {
                    for (mode in listOf("auto", "black", "white")) {
                        val scheme = resolveAppColorScheme(dark, palette, toneIndex, 72f, mode, "palette")
                        assertEquals("${palette.key}/$toneIndex/dark=$dark/$mode",
                            palette.toneAt(toneIndex), scheme.background)
                    }
                }
            }
        }
    }

    @Test fun uiTextIconsButtonsAndContainersRemainLegibleInEveryCombination() {
        val modes = listOf("auto", "black", "white")
        val intensities = listOf(0f, 36f, 72f, 100f)
        for (palette in PaletteCatalog.palettes) {
            for (toneIndex in palette.tones.indices) {
                for (dark in listOf(false, true)) {
                    for (mode in modes) {
                        for (intensity in intensities) {
                            val scheme = resolveAppColorScheme(dark, palette, toneIndex, intensity, mode, "palette")
                            val id = "${palette.key}/tone=$toneIndex/dark=$dark/mode=$mode/intensity=$intensity"
                            assertContrast("$id background", scheme.onBackground, scheme.background, 4.5f)
                            assertContrast("$id surface", scheme.onSurface, scheme.surface, 4.5f)
                            assertContrast("$id variant", scheme.onSurfaceVariant, scheme.surfaceVariant, 4.5f)
                            assertContrast("$id primary", scheme.onPrimary, scheme.primary, 4.5f)
                            assertContrast("$id primary container", scheme.onPrimaryContainer, scheme.primaryContainer, 4.5f)
                            assertContrast("$id secondary", scheme.onSecondary, scheme.secondary, 4.5f)
                            assertContrast("$id secondary container", scheme.onSecondaryContainer, scheme.secondaryContainer, 4.5f)
                            assertContrast("$id tertiary", scheme.onTertiary, scheme.tertiary, 4.5f)
                            assertContrast("$id tertiary container", scheme.onTertiaryContainer, scheme.tertiaryContainer, 4.5f)
                            assertContrast("$id inverse", scheme.inverseOnSurface, scheme.inverseSurface, 4.5f)
                            assertContrast("$id accent graphics", scheme.primary, scheme.background, 3f)
                            assertContrast("$id outlines", scheme.outline, scheme.background, 3f)
                            assertContrast("$id outline variant", scheme.outlineVariant, scheme.surfaceVariant, 3f)
                            for (surface in listOf(
                                scheme.surfaceContainerLowest,
                                scheme.surfaceContainerLow,
                                scheme.surfaceContainer,
                                scheme.surfaceContainerHigh,
                                scheme.surfaceContainerHighest,
                                scheme.surfaceBright,
                                scheme.surfaceDim
                            )) {
                                assertContrast("$id container", scheme.onSurface, surface, 4.5f)
                            }
                        }
                    }
                }
            }
        }
    }

    @Test fun oledIsIntentionallyBlackWithReadableManualOrAutomaticText() {
        for (palette in PaletteCatalog.palettes) {
            for (toneIndex in palette.tones.indices) {
                for (mode in listOf("auto", "black", "white")) {
                    val base = resolveAppColorScheme(true, palette, toneIndex, 72f, mode, "palette")
                    val oled = oledColorScheme(base, mode)
                    assertEquals(Color.Black, oled.background)
                    assertContrast("OLED $mode", oled.onBackground, oled.background, 7f)
                    assertContrast("OLED $mode cards", oled.onSurface, oled.surfaceContainerHighest, 7f)
                    assertContrast("OLED $mode variant", oled.onSurfaceVariant, oled.surfaceVariant, 4.5f)
                }
            }
        }
    }

    @Test fun manualAccentColorsAreVisibleOnAllPaletteBackgrounds() {
        val accents = listOf("palette", "red", "coral", "orange", "amber", "yellow",
            "lime", "green", "mint", "teal", "cyan", "sky", "blue", "indigo",
            "violet", "purple", "pink", "rose", "brown", "graphite")
        for (palette in PaletteCatalog.palettes) {
            for (toneIndex in palette.tones.indices) {
                for (dark in listOf(false, true)) {
                    for (accent in accents) {
                        val scheme = resolveAppColorScheme(dark, palette, toneIndex, 72f, "auto", accent)
                        val id = "${palette.key}/tone=$toneIndex/dark=$dark/accent=$accent"
                        assertContrast("$id accent", scheme.primary, scheme.background, 3f)
                        assertContrast("$id accent label", scheme.onPrimary, scheme.primary, 4.5f)
                        assertContrast("$id inverse accent", scheme.inversePrimary, scheme.inverseSurface, 3f)
                    }
                }
            }
        }
    }

    @Test fun paletteTintedToolDrawersKeepLightSectionsAndReadableButtons() {
        for (palette in PaletteCatalog.palettes) {
            for (toneIndex in palette.tones.indices) {
                for (intensity in listOf(0f, 36f, 72f, 100f)) {
                    val id = "${palette.key}/$toneIndex/intensity=$intensity"
                    val drawer = workspaceDrawerColors(palette, toneIndex, intensity)
                    val tools = drawer.controls
                    assertEquals("$id panel", tools.background, tools.surface)
                    assertContrast("$id panel text", Color.Black, tools.background, 7f)
                    assertEquals("$id panel title", Color.Black, tools.onBackground)
                    assertEquals("$id normal buttons", Color.Black, tools.onSurface)
                    assertEquals("$id selected buttons", Color.Black, tools.onPrimaryContainer)
                    assertEquals("$id selected chips", Color.Black, tools.onSecondaryContainer)
                    assertContrast("$id section labels", tools.onSurface, drawer.section, 7f)
                    assertContrast("$id section stands out", drawer.section, tools.background, 1.25f)
                    assertContrast("$id accent on section", tools.primary, drawer.section, 3f)
                    assertContrast("$id outline on section", tools.outlineVariant, drawer.section, 3f)
                    assertContrast("$id section secondary text", drawer.sectionMutedContent, drawer.section, 4.5f)
                    assertContrast("$id section error", drawer.sectionErrorContent, drawer.section, 4.5f)
                    assertContrast("$id disabled labels", drawer.disabledButtonContent, tools.surfaceContainerLow, 4.5f)
                    for (fill in listOf(tools.surfaceContainer, tools.surfaceContainerHigh, tools.primaryContainer)) {
                        assertContrast("$id button text and icon", Color.Black, fill, 7f)
                        assertContrast("$id button outline", tools.outlineVariant, fill, 3f)
                    }
                    assertContrast("$id selection outline", tools.outline, tools.primaryContainer, 3f)
                    assertContrast("$id selection fill", tools.primaryContainer, tools.surfaceContainerHigh, 1.15f)
                    assertContrast("$id switch unchecked", tools.onSurfaceVariant, tools.surfaceContainerHigh, 3f)
                }
            }
        }
    }

    @Test fun indigoToolsUseTheCurrentToneInLightSectionsAndButtons() {
        val palette = PaletteCatalog.find("indigo")
        val lightTone = workspaceDrawerColors(palette, 0, 72f)
        val darkTone = workspaceDrawerColors(palette, palette.tones.lastIndex, 72f)
        assertTrue(lightTone.section != darkTone.section)
        assertTrue(lightTone.controls.background != darkTone.controls.background)
        assertTrue(lightTone.controls.background.blue > lightTone.controls.background.red)
        assertTrue(lightTone.section.blue > lightTone.section.red)
        assertTrue(lightTone.controls.surfaceContainerHigh.blue > lightTone.controls.surfaceContainerHigh.red)
        assertTrue(lightTone.controls.primaryContainer.blue > lightTone.controls.primaryContainer.red)
    }

    @Test fun cardAnimationFillsStillUseThePaletteHueWithACustomAccent() {
        val palette = PaletteCatalog.find("indigo")
        val app = resolveAppColorScheme(false, palette, 0, 72f, "auto", "orange")
        val tools = workspaceToolColorScheme(app, palette, 72f)
        val backgroundTint = app.background.blue - app.background.red
        assertTrue(tools.surfaceContainer.blue - tools.surfaceContainer.red > backgroundTint)
        assertTrue(tools.primaryContainer.blue - tools.primaryContainer.red > backgroundTint)
    }
}

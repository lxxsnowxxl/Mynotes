package com.example.mynotes.ui.theme

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PaletteCatalogRegressionTest {
    @Test
    fun settingsShowsHalfThePaletteFamiliesAndKeepsExistingSelections() {
        assertEquals(23, PaletteCatalog.settingsPalettes.size)
        assertEquals(PaletteCatalog.palettes.size / 2, PaletteCatalog.settingsPalettes.size)
        PaletteCatalog.palettes.forEach { savedPalette ->
            val visible = PaletteCatalog.palettesForSettings(savedPalette.key)
            assertEquals(23, visible.size)
            assertEquals(visible.size, visible.map { it.key }.toSet().size)
            assertTrue(visible.any { it.key == "neutral" })
            assertEquals(savedPalette.tones, visible.single { it.key == savedPalette.key }.tones)
        }
    }

    @Test
    fun everyPaletteHasExactlyFiveSelectableTones() {
        assertTrue(PaletteCatalog.palettes.isNotEmpty())
        PaletteCatalog.palettes.forEach { palette ->
            assertEquals("Palette ${palette.key} must expose five tones", PaletteCatalog.SELECTABLE_TONE_COUNT, palette.tones.size)
            assertEquals("Palette ${palette.key} tones must be distinct", palette.tones.size, palette.tones.toSet().size)
        }
    }

    @Test
    fun toneIndexNormalizationUsesTheExpandedPaletteRange() {
        PaletteCatalog.palettes.forEach { palette ->
            assertEquals(0, palette.normalizeToneIndex(-1))
            assertEquals(PaletteCatalog.SELECTABLE_TONE_COUNT - 1, palette.normalizeToneIndex(Int.MAX_VALUE))
            assertEquals(palette.tones.first(), palette.toneAt(Int.MIN_VALUE))
            assertEquals(palette.tones.last(), palette.toneAt(Int.MAX_VALUE))
            assertEquals((PaletteCatalog.SELECTABLE_TONE_COUNT + 1) / 2, palette.darkToneStartIndex)
        }
    }

    @Test
    fun paletteKeysRemainUniqueForLazyRowItems() {
        val keys = PaletteCatalog.palettes.map { it.key }
        assertEquals(keys.size, keys.toSet().size)
    }
}

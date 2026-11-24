package com.example.mynotes.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PaletteRaisedSurfacesTest {
    @Test
    fun everyPaletteToneProducesDistinctReadableFabAndHarmonizedCard() {
        for (palette in PaletteCatalog.palettes) {
            for (index in palette.tones.indices) {
                val background = palette.toneAt(index)
                val surface = harmonizedHomeSurfaces(palette.key, background)
                assertNotEquals("Card should not reuse background: ${palette.key}/$index", background, surface.noteCard)
                assertNotEquals("FAB should not reuse background: ${palette.key}/$index", background, surface.fab)
                assertNotEquals("FAB should differ from card: ${palette.key}/$index", surface.noteCard, surface.fab)
                assertTrue("FAB contrast too low: ${palette.key}/$index",
                    uiContrastRatio(surface.fab, background) >= 3.24f)
                assertTrue("Cards must appear elevated: ${palette.key}/$index",
                    surface.noteCard.luminance() > background.luminance())
                assertTrue("Card outline must remain translucent: ${palette.key}/$index",
                    surface.noteOutline.alpha in 0.1f..0.4f)
            }
        }
    }

    @Test
    fun neutralLightBackgroundUsesNearWhiteCardsAndDarkAccentFab() {
        val palette = PaletteCatalog.find("neutral")
        val background = palette.toneAt(0)
        val surface = harmonizedHomeSurfaces(palette.key, background)
        assertTrue("Neutral cards should look nearly white rather than gray",
            surface.noteCard.luminance() > 0.90f)
        assertTrue("Neutral FAB should use a dark neutral accent", surface.fab.luminance() < 0.24f)
    }

    @Test
    fun neutralDarkBackgroundGetsElevatedCardAndVisibleFab() {
        val background = Color(0xFF2D2D2DL)
        val surface = harmonizedHomeSurfaces("neutral", background)
        assertTrue(surface.noteCard.luminance() > background.luminance())
        assertTrue(uiContrastRatio(surface.fab, background) >= 3.24f)
    }
}

package com.example.mynotes

import androidx.compose.ui.graphics.Color
import com.example.mynotes.ui.theme.PaletteCatalog
import com.example.mynotes.ui.theme.settingsSectionColors
import com.example.mynotes.ui.theme.uiContrastRatio
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SettingsSectionColorsTest {
    private val backgrounds = PaletteCatalog.palettes.flatMap { it.tones } + listOf(Color.Black, Color.White, Color(0xFF777777))
    @Test
    fun automaticContentRemainsReadableAcrossReferenceBackgrounds() {
        backgrounds.forEach { background -> val colors = settingsSectionColors(background, "auto")
            assertTrue(uiContrastRatio(colors.text, background) >= 4.499f)
            assertTrue(uiContrastRatio(colors.secondaryText, background) >= 4.499f)
            assertTrue(uiContrastRatio(colors.graphic, background) >= 2.999f)
        }
    }
    @Test
    fun manualBlackAndWhiteChoicesRemainUnchanged() {
        backgrounds.forEach { background -> listOf("black" to Color.Black, "white" to Color.White).forEach { (mode, ink) ->
                val colors = settingsSectionColors(background, mode)
                assertEquals(ink, colors.text)
                assertEquals(ink, colors.secondaryText)
                assertEquals(ink, colors.graphic)
            }
        }
    }
    @Test
    fun backgroundIsExactlyTheReferenceWithoutAnotherTranslucentLayer() {
        backgrounds.forEach { background -> assertEquals(background, settingsSectionColors(background, "auto").background)
        }
    }
    @Test
    fun blackReferenceRemainsBlackWithWhiteAutomaticText() {
        val colors = settingsSectionColors(Color.Black, "auto")
        assertEquals(Color.Black, colors.background)
        assertEquals(Color.White, colors.text)
        assertTrue(uiContrastRatio(colors.secondaryText, Color.Black) >= 4.499f)
    }
}

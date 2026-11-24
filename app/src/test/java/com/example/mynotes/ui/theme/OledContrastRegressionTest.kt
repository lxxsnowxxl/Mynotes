package com.example.mynotes.ui.theme

import androidx.compose.ui.graphics.Color
import org.junit.Assert.*
import org.junit.Test

class OledContrastRegressionTest {
    @Test fun automaticOledTextRemainsReadableForEverySavedPaletteTone() {
        for(palette in PaletteCatalog.palettes)for(tone in palette.tones.indices){
            val base=resolveAppColorScheme(true,palette,tone,72f,"auto","palette")
            val oled=oledColorScheme(base,"auto")
            assertEquals(Color.Black,oled.background)
            assertTrue(uiContrastRatio(oled.onBackground,oled.background)>=7f)
            assertTrue(uiContrastRatio(oled.onSurface,oled.surfaceContainerHighest)>=7f)
            assertTrue(uiContrastRatio(oled.onSurfaceVariant,oled.surfaceVariant)>=4.5f)
            assertTrue(uiContrastRatio(oled.outline,oled.background)>=3f-.001f)
        }
    }
    @Test fun manuallySelectedTextColorIsOnlyOverriddenWhenOledNeedsContrast() {
        for(mode in listOf("black","white")){
            val base=resolveAppColorScheme(true,PaletteCatalog.find("neutral"),0,72f,mode,"palette")
            val oled=oledColorScheme(base,mode)
            assertEquals(Color.Black,oled.background)
            assertTrue(uiContrastRatio(oled.onBackground, oled.background)>=7f)
            assertTrue(uiContrastRatio(oled.onSurface, oled.surfaceContainerHighest)>=7f)
            assertTrue(uiContrastRatio(oled.onSurfaceVariant, oled.surfaceVariant)>=4.5f)
        }
    }
}

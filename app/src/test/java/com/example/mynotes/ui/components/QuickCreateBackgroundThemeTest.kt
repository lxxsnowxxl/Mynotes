package com.example.mynotes.ui.components

import androidx.compose.ui.graphics.Color
import com.example.mynotes.ui.theme.PaletteCatalog
import com.example.mynotes.ui.theme.automaticUiTextColor
import com.example.mynotes.ui.theme.resolveAppColorScheme
import com.example.mynotes.ui.theme.uiContrastRatio
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class QuickCreateBackgroundThemeTest {
    @Test
    fun capsulesUseSolidPaletteSurfaceAndFabRemainsDarkAcrossThemes() {
        // El estilo de fondo no afecta a los accesos rápidos: solo a la pantalla.
        PaletteCatalog.settingsPalettes.forEach { palette ->
            palette.tones.indices.forEach { tone ->
                listOf(false, true).forEach { dark ->
                    val scheme = resolveAppColorScheme(dark, palette, tone, 72f, "auto", "palette")
                    val colors = quickCreateSolidColors(scheme.surfaceContainerHigh)
                    val label = "${palette.key}:$tone:$dark"
                    assertEquals(label, scheme.surfaceContainerHigh, colors.capsule)
                    assertEquals(label, Color(0xFF202124), colors.fab)
                    assertEquals(label, 1f, colors.capsule.alpha, 0f)
                    assertTrue(label, uiContrastRatio(automaticUiTextColor(colors.capsule), colors.capsule) >= 4.5f)
                    assertTrue(label, uiContrastRatio(Color.White, colors.fab) >= 4.5f)
                }
            }
        }
    }

    @Test
    fun switchingSolidPaletteSurfaceNeverTintsMainFab() {
        val darkBlue = quickCreateSolidColors(Color(0xFF283656))
        val paleLavender = quickCreateSolidColors(Color(0xFFE3DDF9))
        assertTrue(darkBlue.capsule != paleLavender.capsule)
        assertEquals(Color(0xFF202124), darkBlue.fab)
        assertEquals(darkBlue.fab, paleLavender.fab)
    }
}

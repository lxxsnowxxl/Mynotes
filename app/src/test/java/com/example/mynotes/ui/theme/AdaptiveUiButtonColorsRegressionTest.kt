package com.example.mynotes.ui.theme

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Test

class AdaptiveUiButtonColorsRegressionTest {
    @Test
    fun outputMatchesV220ForEveryPaletteToneAndTextMode() {
        val thresholds = listOf(4.5f to 1.55f, 7f to 1.30f, 4.5f to 1.85f)
        for (palette in PaletteCatalog.palettes) {
            for (background in palette.tones) {
                for (mode in listOf("auto", "black", "white")) {
                    for ((contentContrast, surfaceContrast) in thresholds) {
                        assertEquals("${palette.key}: $background / $mode / $contentContrast / $surfaceContrast",
                            LegacyButtonColors.resolveAdaptiveUiButtonColors(palette.accent, background, mode, contentContrast, surfaceContrast),
                            resolveAdaptiveUiButtonColors(palette.accent, background, mode, contentContrast, surfaceContrast))
                    }
                }
            }
        }
    }

    @Test
    fun translucentAndExtremeColorsKeepTheSameResult() {
        val colors = listOf(Color.Black, Color.White, Color(0xFF777777), Color(0x55BB2288), Color.Transparent)
        for (preferred in colors) for (background in colors) for (mode in listOf("auto", "black", "white")) {
            assertEquals(LegacyButtonColors.resolveAdaptiveUiButtonColors(preferred, background, mode),
                resolveAdaptiveUiButtonColors(preferred, background, mode))
        }
    }

    @Test
    fun alreadyValidPreferredColorRemainsExact() {
        assertEquals(Color.Black, adaptiveUiButtonContainer(Color.Black, Color.White, Color.White))
    }
}

/** Algoritmos congelados de v220 para detectar cualquier cambio de color en la optimización. */
private object LegacyButtonColors {
    private val AccessibleBlack = Color.Black
    private val AccessibleWhite = Color.White
    private fun mixOpaqueUiColor(foreground: Color, background: Color, amount: Float): Color {
        val t = amount.coerceIn(0f, 1f)
        return Color(red = foreground.red + (background.red - foreground.red) * t,
            green = foreground.green + (background.green - foreground.green) * t,
            blue = foreground.blue + (background.blue - foreground.blue) * t, alpha = 1f)
    }

    fun adaptiveUiButtonContainer(
        preferred: Color,
        background: Color,
        contentColor: Color,
        minimumContentContrast: Float = 4.5f,
        minimumSurfaceContrast: Float = 1.55f
    ): Color {
        val preferredOpaque = preferred.copy(alpha = 1f)
        val backgroundOpaque = background.copy(alpha = 1f)
        val contentOpaque = contentColor.copy(alpha = 1f)
    
        fun distanceSquared(first: Color, second: Color): Float {
            val dr = first.red - second.red
            val dg = first.green - second.green
            val db = first.blue - second.blue
            return dr * dr + dg * dg + db * db
        }
    
        var bestCandidate: Color? = null
        var bestDistance = Float.POSITIVE_INFINITY
    
        fun consider(candidate: Color) {
            val opaque = candidate.copy(alpha = 1f)
            if (uiContrastRatio(contentOpaque, opaque) < minimumContentContrast ||
                uiContrastRatio(opaque, backgroundOpaque) < minimumSurfaceContrast
            ) {
                return
            }
            val distance = distanceSquared(opaque, preferredOpaque)
            if (distance < bestDistance) {
                bestDistance = distance
                bestCandidate = opaque
            }
        }
    
        fun considerBlendSeries(from: Color, to: Color, steps: Int = 48) {
            for (index in 0..steps) {
                consider(mixOpaqueUiColor(from, to, index.toFloat() / steps.toFloat()))
            }
        }
    
        consider(preferredOpaque)
        considerBlendSeries(preferredOpaque, AccessibleBlack)
        considerBlendSeries(preferredOpaque, AccessibleWhite)
        considerBlendSeries(backgroundOpaque, AccessibleBlack)
        considerBlendSeries(backgroundOpaque, AccessibleWhite)
    
        // Serie neutra de respaldo. Evita que una combinación extrema de paleta,
        // acento y texto manual quede sin un candidato utilizable.
        for (index in 0..48) {
            val value = index.toFloat() / 48f
            consider(Color(value, value, value, 1f))
        }
    
        return bestCandidate ?: run {
            val fallbackTarget = if (uiContrastRatio(AccessibleBlack, contentOpaque) >
                uiContrastRatio(AccessibleWhite, contentOpaque)) AccessibleBlack else AccessibleWhite
            mixOpaqueUiColor(backgroundOpaque, fallbackTarget, 0.55f)
        }
    }

    fun resolveAdaptiveUiButtonColors(
        preferred: Color,
        background: Color,
        textColorMode: String,
        minimumContentContrast: Float = 4.5f,
        minimumSurfaceContrast: Float = 1.55f
    ): AdaptiveUiButtonColors {
        fun containerFor(content: Color) = adaptiveUiButtonContainer(
            preferred, background, content, minimumContentContrast, minimumSurfaceContrast)
    
        if (textColorMode == "black" || textColorMode == "white") {
            val content = resolveUiTextColor(textColorMode, background)
            val container = containerFor(content)
            return AdaptiveUiButtonColors(container = container, content = content)
        }
    
        // Primera pasada usando el color preferido (normalmente el accent actual).
        var content = automaticUiTextColor(preferred.copy(alpha = 1f))
        var container = containerFor(content)
    
        // Recalcula el texto contra el fondo REAL resultante y estabiliza una vez
        // más el contenedor. Esto evita combinaciones grises con poco contraste
        // cuando el usuario cambia Accent color o la paleta de fondo.
        content = automaticUiTextColor(container)
        container = containerFor(content)
        content = automaticUiTextColor(container)
    
        return AdaptiveUiButtonColors(container = container, content = content)
    }
}

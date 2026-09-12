package com.example.mynotes.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

private val AccessibleBlack = Color.Black
private val AccessibleWhite = Color.White

@Composable
fun noteBackgroundColor(color: String): Color {
    return when (color) {
        "purple" -> Color(0xFFF0E7FA)
        "yellow" -> Color(0xFFFFF4C7)
        "pink" -> Color(0xFFFFE5EC)
        "green" -> Color(0xFFE4F2E8)
        "blue" -> Color(0xFFE5F1FB)
        "orange" -> Color(0xFFFFE7D1)
        "red" -> Color(0xFFFFE0E0)
        "cyan" -> Color(0xFFE0F7FA)
        "teal" -> Color(0xFFDDF4F0)
        "mint" -> Color(0xFFDFF7EA)
        "lime" -> Color(0xFFF1F8D7)
        "brown" -> Color(0xFFEDE2D9)
        "gray" -> Color(0xFFE9ECEF)
        /*
         * "default" significa que la nota sigue el fondo global elegido
         * en Configuración → Paleta de colores. Antes se usaba
         * surfaceContainerLow, que en temas oscuros puede ser casi negro
         * aunque el usuario haya elegido una paleta clara o de otro color.
         */
        "default" -> MaterialTheme.colorScheme.background
        // Fallback seguro para colores antiguos o desconocidos.
        else -> MaterialTheme.colorScheme.background
    }
}

/**
 * Compone [foreground] sobre [background] y devuelve un color opaco.
 * Se usa para medir contraste real cuando una superficie o un icono tiene alpha.
 */
fun compositeUiColor(foreground: Color, background: Color): Color {
    val a = foreground.alpha.coerceIn(0f, 1f)
    val inverse = 1f - a
    return Color(red = foreground.red * a + background.red * inverse, green = foreground.green * a + background.green * inverse,
        blue = foreground.blue * a + background.blue * inverse, alpha = 1f)
}

/** Relación de contraste WCAG entre dos colores. */
fun uiContrastRatio(foreground: Color, background: Color): Float {
    val resolvedForeground = compositeUiColor(foreground, background)
    val l1 = resolvedForeground.luminance()
    val l2 = background.copy(alpha = 1f).luminance()
    val lighter = maxOf(l1, l2)
    val darker = minOf(l1, l2)
    return (lighter + 0.05f) / (darker + 0.05f)
}

/**
 * Elige negro o blanco usando el contraste WCAG contra el fondo REAL.
 * Para texto normal el resultado siempre será la alternativa de mayor contraste.
 */
fun automaticUiTextColor(background: Color): Color {
    val blackContrast = uiContrastRatio(AccessibleBlack, background)
    val whiteContrast = uiContrastRatio(AccessibleWhite, background)
    return if (blackContrast >= whiteContrast) {
        AccessibleBlack
    } else {
        AccessibleWhite
    }
}

/**
 * Resolución segura del color principal.
 *
 * auto  -> contraste calculado
 * black -> elección manual absoluta
 * white -> elección manual absoluta
 * valor desconocido -> auto (fallback seguro)
 */
fun resolveUiTextColor(value: String, background: Color): Color {
    return when (value) {
        "black" -> Color.Black
        "white" -> Color.White
        else -> automaticUiTextColor(background)
    }
}

private fun mixOpaqueUiColor(foreground: Color, background: Color, amount: Float): Color {
    val t = amount.coerceIn(0f, 1f)
    return Color(red = foreground.red + (background.red - foreground.red) * t,
        green = foreground.green + (background.green - foreground.green) * t,
        blue = foreground.blue + (background.blue - foreground.blue) * t, alpha = 1f)
}

/**
 * Atenúa un color hacia el fondo tanto como sea posible sin bajar del
 * contraste solicitado. Devuelve un color OPACO: evita que un alpha fijo se
 * vuelva ilegible al cambiar de paleta.
 */
fun softenUiColorToContrast(foreground: Color, background: Color, minimumContrast: Float, maximumSoftening: Float = 0.62f): Color {
    val base = foreground.copy(alpha = 1f)
    if (uiContrastRatio(base, background) < minimumContrast) {
        return automaticUiTextColor(background)
    }
    var low = 0f
    var high = maximumSoftening.coerceIn(0f, 0.92f)
    var best = base
    repeat(18) {
        val middle = (low + high) / 2f
        val candidate = mixOpaqueUiColor(base, background, middle)
        if (uiContrastRatio(candidate, background) >= minimumContrast) {
            best = candidate
            low = middle
        } else {
            high = middle
        }
    }
    return best
}

/**
 * Texto secundario automático: visualmente más suave pero >= 4.5:1.
 * En modo manual NO altera negro/blanco: la elección del usuario prevalece.
 */
fun resolveSecondaryUiTextColor(value: String, background: Color): Color {
    val primary = resolveUiTextColor(value, background)
    return if (value == "black" || value == "white") {
        primary
    } else {
        softenUiColorToContrast(foreground = primary, background = background, minimumContrast = 4.5f, maximumSoftening = 0.58f)
    }
}

/**
 * Iconos/contornos automáticos: mantiene al menos 3:1, criterio WCAG para
 * elementos gráficos. Los modos manuales siguen siendo absolutos.
 */
fun resolveUiGraphicColor(value: String, background: Color): Color {
    val primary = resolveUiTextColor(value, background)
    return if (value == "black" || value == "white") {
        primary
    } else {
        softenUiColorToContrast(foreground = primary, background = background, minimumContrast = 3f, maximumSoftening = 0.72f)
    }
}

/**
 * Conserva un color decorativo cuando ya tiene contraste suficiente; si no,
 * usa el negro/blanco automático. Útil para bordes y acentos sobre cualquier
 * tono de las 38 paletas.
 */
fun ensureUiContrast(preferred: Color, background: Color, minimumContrast: Float = 3f): Color {
    val opaque = preferred.copy(alpha = 1f)
    return if (uiContrastRatio(opaque, background) >= minimumContrast) {
        opaque
    } else {
        automaticUiTextColor(background)
    }
}

/** Compatibilidad con llamadas antiguas. */
fun manualUiTextColor(value: String): Color = resolveUiTextColor(value = value, background = Color.White)

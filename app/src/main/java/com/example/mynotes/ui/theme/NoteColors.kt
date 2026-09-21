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
 * auto  -> contraste calculado.
 * black / white -> respeta la elección mientras conserve al menos 4.5:1.
 * Si el color manual se vuelve ilegible sobre la superficie real, usa de
 * forma automática negro o blanco. Esto evita texto blanco sobre tarjetas
 * claras y texto negro sobre fondos oscuros.
 */
fun resolveUiTextColor(value: String, background: Color): Color {
    val preferred = when (value) {
        "black" -> Color.Black
        "white" -> Color.White
        else -> return automaticUiTextColor(background)
    }
    return if (uiContrastRatio(preferred, background) >= 4.5f) {
        preferred
    } else {
        automaticUiTextColor(background)
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


/**
 * Contorno adaptado a la paleta: conserva la familia cromática del fondo
 * y sólo lo desplaza hacia blanco o negro lo necesario para alcanzar
 * contraste visible. Si una combinación extrema no puede mantener el tono
 * con suficiente contraste, usa el monocromo automático como respaldo.
 */
fun paletteMatchedOutlineColor(background: Color, minimumContrast: Float = 3f): Color {
    val backgroundOpaque = background.copy(alpha = 1f)
    val target = if (backgroundOpaque.luminance() > 0.5f) AccessibleBlack else AccessibleWhite
    var low = 0f
    var high = 1f
    var best: Color? = null
    repeat(20) {
        val amount = (low + high) / 2f
        val candidate = mixOpaqueUiColor(backgroundOpaque, target, amount)
        if (uiContrastRatio(candidate, backgroundOpaque) >= minimumContrast) {
            best = candidate
            high = amount
        } else {
            low = amount
        }
    }
    return best ?: automaticUiTextColor(backgroundOpaque)
}


/**
 * Construye un color de botón que conserve el color de texto ya resuelto para
 * la interfaz (por ejemplo blanco en fondos oscuros o negro en fondos claros),
 * pero también se diferencie visualmente del panel que lo contiene.
 *
 * A diferencia de resolver el texto a partir del botón, esta función mantiene
 * coherencia con el modo de texto Automático/Negro/Blanco seleccionado por el
 * usuario y adapta el FONDO del botón hasta alcanzar los dos contrastes.
 */
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

    val candidates = ArrayList<Color>(220)
    fun add(candidate: Color) {
        candidates += candidate.copy(alpha = 1f)
    }
    fun addBlendSeries(from: Color, to: Color, steps: Int = 48) {
        for (index in 0..steps) {
            add(mixOpaqueUiColor(from, to, index.toFloat() / steps.toFloat()))
        }
    }

    add(preferredOpaque)
    addBlendSeries(preferredOpaque, AccessibleBlack)
    addBlendSeries(preferredOpaque, AccessibleWhite)
    addBlendSeries(backgroundOpaque, AccessibleBlack)
    addBlendSeries(backgroundOpaque, AccessibleWhite)

    // Serie neutra de respaldo. Evita que una combinación extrema de paleta,
    // acento y texto manual quede sin un candidato utilizable.
    for (index in 0..48) {
        val value = index.toFloat() / 48f
        add(Color(value, value, value, 1f))
    }

    val valid = candidates.filter { candidate ->
        uiContrastRatio(contentOpaque, candidate) >= minimumContentContrast &&
            uiContrastRatio(candidate, backgroundOpaque) >= minimumSurfaceContrast
    }

    return valid.minByOrNull { candidate ->
        distanceSquared(candidate, preferredOpaque)
    } ?: run {
        /*
         * En la práctica siempre existe un gris que satisface ambos límites.
         * Este fallback conserva una salida segura incluso ante parámetros de
         * contraste imposibles introducidos en futuras modificaciones.
         */
        val fallbackTarget = if (uiContrastRatio(AccessibleBlack, contentOpaque) >
            uiContrastRatio(AccessibleWhite, contentOpaque)) AccessibleBlack else AccessibleWhite
        mixOpaqueUiColor(backgroundOpaque, fallbackTarget, 0.55f)
    }
}



data class AdaptiveUiButtonColors(
    val container: Color,
    val content: Color
)

/**
 * Resuelve de forma conjunta el fondo y el texto de un botón.
 *
 * En modo Automático el texto se calcula CONTRA el fondo final del botón,
 * no contra el panel padre. Así un cambio de Accent color puede cambiar de
 * forma segura entre texto negro/blanco sin perder legibilidad.
 *
 * En modos manuales (black/white) el color solicitado por el usuario se
 * mantiene y se adapta únicamente el fondo del botón.
 */
fun resolveAdaptiveUiButtonColors(
    preferred: Color,
    background: Color,
    textColorMode: String,
    minimumContentContrast: Float = 4.5f,
    minimumSurfaceContrast: Float = 1.55f
): AdaptiveUiButtonColors {
    if (textColorMode == "black" || textColorMode == "white") {
        val content = resolveUiTextColor(textColorMode, background)
        val container = adaptiveUiButtonContainer(
            preferred = preferred,
            background = background,
            contentColor = content,
            minimumContentContrast = minimumContentContrast,
            minimumSurfaceContrast = minimumSurfaceContrast
        )
        return AdaptiveUiButtonColors(container = container, content = content)
    }

    // Primera pasada usando el color preferido (normalmente el accent actual).
    var content = automaticUiTextColor(preferred.copy(alpha = 1f))
    var container = adaptiveUiButtonContainer(
        preferred = preferred,
        background = background,
        contentColor = content,
        minimumContentContrast = minimumContentContrast,
        minimumSurfaceContrast = minimumSurfaceContrast
    )

    // Recalcula el texto contra el fondo REAL resultante y estabiliza una vez
    // más el contenedor. Esto evita combinaciones grises con poco contraste
    // cuando el usuario cambia Accent color o la paleta de fondo.
    content = automaticUiTextColor(container)
    container = adaptiveUiButtonContainer(
        preferred = preferred,
        background = background,
        contentColor = content,
        minimumContentContrast = minimumContentContrast,
        minimumSurfaceContrast = minimumSurfaceContrast
    )
    content = automaticUiTextColor(container)

    return AdaptiveUiButtonColors(container = container, content = content)
}

/** Compatibilidad con llamadas antiguas. */
fun manualUiTextColor(value: String): Color = resolveUiTextColor(value = value, background = Color.White)

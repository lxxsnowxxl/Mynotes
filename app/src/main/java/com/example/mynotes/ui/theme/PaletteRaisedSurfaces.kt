package com.example.mynotes.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

/**
 * Colores de las superficies de inicio, derivados del fondo REAL del tema.
 * La NoteCard no adopta una muestra arbitraria de la paleta: se aclara dentro
 * de la misma familia cromática. El FAB usa el acento de la familia y gana
 * el contraste suficiente para destacar sin crear colores discordantes.
 */
internal data class HarmonizedHomeSurfaces(
    val noteCard: Color,
    val fab: Color,
    val noteOutline: Color
)

internal fun harmonizedHomeSurfaces(paletteKey: String, background: Color): HarmonizedHomeSurfaces {
    val base = background.copy(alpha = 1f)
    val lightBackground = base.luminance() >= 0.43f

    // En temas claros, las tarjetas se aproximan al blanco del propio fondo
    // (no a un gris de otra muestra). En temas oscuros, se elevan suavemente.
    // El contorno y la elevación aportan la definición final de cada tarjeta.
    val cardColor = mixHomeColor(base, Color.White, if (lightBackground) 0.76f else 0.19f)

    // La variante de acento pertenece a la misma familia de color, pero el
    // contraste mínimo es estable incluso con tonos extremos o neón.
    val accent = PaletteCatalog.find(paletteKey).accent.copy(alpha = 1f)
    // Elegir el extremo con mayor contraste (no solo según el umbral de
    // apariencia), especialmente para rosas neón y colores medios saturados.
    val target = if (uiContrastRatio(Color.Black, base) >= uiContrastRatio(Color.White, base)) Color.Black else Color.White
    val fabColor = adjustHomeAccent(accent, base, target, minimumContrast = 3.25f)

    // No dibujar contornos negros fijos en tarjetas con fondos crema/colores.
    // El acento semitransparente acompaña el tema y deja ver la superficie.
    return HarmonizedHomeSurfaces(
        noteCard = cardColor,
        fab = fabColor,
        noteOutline = fabColor.copy(alpha = if (lightBackground) 0.23f else 0.30f)
    )
}

private fun adjustHomeAccent(accent: Color, background: Color, target: Color, minimumContrast: Float): Color {
    if (uiContrastRatio(accent, background) >= minimumContrast) return accent
    var low = 0f
    var high = 1f
    repeat(18) {
        val mid = (low + high) / 2f
        val trial = mixHomeColor(accent, target, mid)
        if (uiContrastRatio(trial, background) >= minimumContrast) high = mid else low = mid
    }
    return mixHomeColor(accent, target, high)
}

private fun mixHomeColor(from: Color, to: Color, amount: Float): Color {
    val value = amount.coerceIn(0f, 1f)
    return Color(
        red = from.red + (to.red - from.red) * value,
        green = from.green + (to.green - from.green) * value,
        blue = from.blue + (to.blue - from.blue) * value,
        alpha = 1f
    )
}

package com.example.mynotes.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
internal data class SettingsSectionColors(val background: Color, val text: Color, val secondaryText: Color, val graphic: Color)

/**
 * Contraste exclusivo de Configuración: el usuario conserva Negro/Blanco cuando
 * es legible. Si la paleta cambia y el texto se pierde, se usa el color de mayor
 * contraste sólo para controles, títulos, menús e iconos de Configuración.
 * No cambia la preferencia persistida ni el color del editor o las notas.
 * WCAG: >=4.5:1 texto normal y >=3:1 iconografía/controles gráficos.
 */
internal fun settingsSectionColors(background: Color, textColorMode: String): SettingsSectionColors {
    val preferred = resolveUiTextColor(textColorMode, background)
    val readable = ensureUiContrast(preferred, background, minimumContrast = 4.5f)
    return SettingsSectionColors(
        background = background,
        text = readable,
        secondaryText = softenUiColorToContrast(readable, background, minimumContrast = 4.5f, maximumSoftening = 0.50f),
        graphic = softenUiColorToContrast(readable, background, minimumContrast = 3f, maximumSoftening = 0.55f)
    )
}

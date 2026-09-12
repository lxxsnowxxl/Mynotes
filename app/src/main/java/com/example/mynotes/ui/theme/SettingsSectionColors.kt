package com.example.mynotes.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
internal data class SettingsSectionColors(val background: Color, val text: Color, val secondaryText: Color, val graphic: Color)

/**
 * Resuelve el texto contra el fondo exacto de la tarjeta de referencia.
 * El tema ya incorpora la paleta y la intensidad: no se agrega otro alpha
 * que aclararía el negro y haría diferentes Sonido, Vibración y los demás grupos.
 */
internal fun settingsSectionColors(background: Color, textColorMode: String): SettingsSectionColors {
    return SettingsSectionColors(background = background, text = resolveUiTextColor(textColorMode, background),
        secondaryText = resolveSecondaryUiTextColor(textColorMode, background), graphic = resolveUiGraphicColor(textColorMode, background))
}

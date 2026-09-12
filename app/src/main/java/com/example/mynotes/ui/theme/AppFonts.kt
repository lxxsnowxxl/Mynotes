package com.example.mynotes.ui.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.example.mynotes.R

/*
 * Familia completa: Compose elige automáticamente Regular / Medium / Bold
 * y sus variantes Italic según el FontWeight/FontStyle solicitado por cada Text.
 */
val GoogleSansFontFamily = FontFamily(Font(resId = R.font.google_sans_regular, weight = FontWeight.Normal, style = FontStyle.Normal), Font(
            resId = R.font.google_sans_medium, weight = FontWeight.Medium, style = FontStyle.Normal), Font(resId = R.font.google_sans_bold,
            weight = FontWeight.Bold, style = FontStyle.Normal), Font(resId = R.font.google_sans_italic, weight = FontWeight.Normal,
            style = FontStyle.Italic), Font(resId = R.font.google_sans_medium_italic, weight = FontWeight.Medium, style = FontStyle.Italic
        ), Font(resId = R.font.google_sans_bold_italic, weight = FontWeight.Bold, style = FontStyle.Italic))
/*
 * Variantes seleccionables individualmente desde Configuración.
 * Cada archivo se registra como una familia de una sola cara para que,
 * al elegirla, esa apariencia se aplique a toda la interfaz.
 */
val GoogleSansRegularFontFamily = FontFamily(Font(R.font.google_sans_regular))

val GoogleSansMediumFontFamily = FontFamily(Font(R.font.google_sans_medium))

val GoogleSansBoldFontFamily = FontFamily(Font(R.font.google_sans_bold))

val GoogleSansItalicFontFamily = FontFamily(Font(R.font.google_sans_italic))

val GoogleSansMediumItalicFontFamily = FontFamily(Font(R.font.google_sans_medium_italic))

val GoogleSansBoldItalicFontFamily = FontFamily(Font(R.font.google_sans_bold_italic))

val GoogleSansFlexFontFamily = FontFamily(Font(R.font.google_sans_flex))

fun appFontFamily(key: String): FontFamily {
    return when (key) {
        "google_sans" -> GoogleSansFontFamily
        "google_sans_regular" -> GoogleSansRegularFontFamily
        "google_sans_medium" -> GoogleSansMediumFontFamily
        "google_sans_bold" -> GoogleSansBoldFontFamily
        "google_sans_italic" -> GoogleSansItalicFontFamily
        "google_sans_medium_italic" -> GoogleSansMediumItalicFontFamily
        "google_sans_bold_italic" -> GoogleSansBoldItalicFontFamily
        "google_sans_flex" -> GoogleSansFlexFontFamily
        "serif" -> FontFamily.Serif
        "monospace" -> FontFamily.Monospace
        else -> FontFamily.SansSerif
    }
}

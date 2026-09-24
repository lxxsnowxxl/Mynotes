package com.example.mynotes.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography as MaterialTypography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily

private const val
    MAX_DARKEN_AMOUNT = 0.30f
private fun mixColor(first: Color, second: Color, amount: Float): Color {
    val value = amount.coerceIn(0f, 1f)
    return Color(red = first.red + (second.red - first.red) * value,
        green = first.green + (second.green - first.green) * value,
        blue = first.blue + (second.blue - first.blue) * value,
        alpha = 1f)
}

private fun intensityAmount(intensity: Float): Float {
    return (intensity.coerceIn(0f, 100f) / 100f) * MAX_DARKEN_AMOUNT
}

fun applySectionIntensity(color: Color, intensity: Float): Color {
    return mixColor(color, Color.Black, intensityAmount(intensity))
}

private fun readableContentColor(background: Color): Color = automaticUiTextColor(background)

private fun resolvedTextColor(textColor: String, background: Color): Color = resolveUiTextColor(value = textColor, background = background)

/**
 * Regla de color solicitada para el modo Automático de la paleta global:
 * tonos 1-2 -> negro, tonos 3-4 -> blanco.
 * Negro/Blanco manuales siguen teniendo prioridad absoluta.
 */
private fun resolvedPaletteTextColor(textColor: String, toneIndex: Int, background: Color): Color {
    if (textColor == "black" || textColor == "white") {
        return resolveUiTextColor(value = textColor, background = background)
    }
    val preferred = if (toneIndex.coerceIn(0, 3) <= 1) Color.Black else Color.White
    return if (uiContrastRatio(preferred, background) >= 4.5f) preferred else automaticUiTextColor(background)
}
private fun resolvedPaletteSecondaryTextColor(textColor: String, toneIndex: Int, background: Color): Color {
    val primary = resolvedPaletteTextColor(textColor, toneIndex, background)
    return if (textColor == "black" || textColor == "white") {
        primary
    } else {
        softenUiColorToContrast(foreground = primary, background = background, minimumContrast = 4.5f, maximumSoftening = 0.42f)
    }
}

private fun resolveAccentColor(value: String, palette: MyNotesPalette): Color {
    return when (value) {
        "red" -> Color(0xFFE55757)
        "coral" -> Color(0xFFF27663)
        "orange" -> Color(0xFFEF8A39)
        "amber" -> Color(0xFFE8B632)
        "yellow" -> Color(0xFFF2CF45)
        "lime" -> Color(0xFF9BCB4B)
        "green" -> Color(0xFF63A85C)
        "mint" -> Color(0xFF58B98E)
        "teal" -> Color(0xFF3FA4A0)
        "cyan" -> Color(0xFF45B9C8)
        "sky" -> Color(0xFF4FA9E2)
        "blue" -> Color(0xFF4B8EDB)
        "indigo" -> Color(0xFF6275CF)
        "violet" -> Color(0xFF8B6BC5)
        "purple" -> Color(0xFFA05BC1)
        "pink" -> Color(0xFFD96787)
        "rose" -> Color(0xFFE16F9A)
        "brown" -> Color(0xFF9A7157)
        "graphite" -> Color(0xFF59636A)
        else -> palette.accent
    }
}

private val BlackTextOutlineShadow = Shadow(color = Color.Black, offset = Offset.Zero, blurRadius = 1.6f)

private fun TextStyle.withBlackOutline(enabled: Boolean): TextStyle {
    return if (enabled) {
        copy(shadow = BlackTextOutlineShadow)
    } else {
        this
    }
}

private fun typographyWithFontFamily(base: MaterialTypography, fontFamily: FontFamily): MaterialTypography {
    return base.copy(
        displayLarge = base.displayLarge.copy(fontFamily = fontFamily),
        displayMedium = base.displayMedium.copy(fontFamily = fontFamily),
        displaySmall = base.displaySmall.copy(fontFamily = fontFamily),
        headlineLarge = base.headlineLarge.copy(fontFamily = fontFamily),
        headlineMedium = base.headlineMedium.copy(fontFamily = fontFamily),
        headlineSmall = base.headlineSmall.copy(fontFamily = fontFamily),
        titleLarge = base.titleLarge.copy(fontFamily = fontFamily),
        titleMedium = base.titleMedium.copy(fontFamily = fontFamily),
        titleSmall = base.titleSmall.copy(fontFamily = fontFamily),
        bodyLarge = base.bodyLarge.copy(fontFamily = fontFamily),
        bodyMedium = base.bodyMedium.copy(fontFamily = fontFamily),
        bodySmall = base.bodySmall.copy(fontFamily = fontFamily),
        labelLarge = base.labelLarge.copy(fontFamily = fontFamily),
        labelMedium = base.labelMedium.copy(fontFamily = fontFamily),
        labelSmall = base.labelSmall.copy(fontFamily = fontFamily)
    )
}

private fun typographyWithBlackOutline(base: MaterialTypography, enabled: Boolean): MaterialTypography {
    if (!enabled) {
        return base
    }
    return base.copy(displayLarge = base.displayLarge.withBlackOutline(true), displayMedium = base.displayMedium.withBlackOutline(true),
        displaySmall = base.displaySmall.withBlackOutline(true), headlineLarge = base.headlineLarge.withBlackOutline(true), headlineMedium =
            base.headlineMedium.withBlackOutline(true), headlineSmall = base.headlineSmall.withBlackOutline(true), titleLarge =
            base.titleLarge.withBlackOutline(true), titleMedium = base.titleMedium.withBlackOutline(true), titleSmall =
            base.titleSmall.withBlackOutline(true), bodyLarge = base.bodyLarge.withBlackOutline(true), bodyMedium =
            base.bodyMedium.withBlackOutline(true), bodySmall = base.bodySmall.withBlackOutline(true), labelLarge =
            base.labelLarge.withBlackOutline(true), labelMedium = base.labelMedium.withBlackOutline(true), labelSmall =
            base.labelSmall.withBlackOutline(true))
}

internal fun lightScheme(palette:
        MyNotesPalette, toneIndex: Int, backgroundIntensity: Float, surfacePanelIntensity: Float, headerIntensity: Float, textColor: String,
    accentColor: String): ColorScheme {
    val baseTone = palette.tones[toneIndex.coerceIn(0, 3)]
    val body = applySectionIntensity(baseTone, backgroundIntensity)
    val header = applySectionIntensity(baseTone, headerIntensity)
    /*
     * 72% reproduce exactamente las mezclas que tenía la app:
     * low 0.72, normal 0.58, high 0.42 y variant 0.45.
     */
    val panelMix = (surfacePanelIntensity.coerceIn(0f, 100f) / 100f)
    val accent = resolveAccentColor(accentColor, palette)
    val automaticDarkTone = textColor == "auto" && toneIndex.coerceIn(0, 3) >= 2
    /*
     * Los tonos 1-2 se mantienen en una familia clara para texto negro.
     * Los tonos 3-4 se mezclan hacia negro para que el texto blanco no
     * pierda contraste aunque el usuario aumente la intensidad del panel.
     */
    val panelTarget = if (automaticDarkTone) Color.Black else Color.White
    val surfaceVariantColor = mixColor(body, panelTarget, (panelMix * 0.625f).coerceIn(0f, 1f))
    val surfaceContainerColor = mixColor(body, panelTarget, (panelMix * 0.8055556f).coerceIn(0f, 1f))
    val bodyContent = resolvedPaletteTextColor(textColor = textColor, toneIndex = toneIndex, background = body)
    val surfaceContent = resolvedPaletteTextColor(textColor = textColor, toneIndex = toneIndex, background = header)
    val variantContent = resolvedPaletteSecondaryTextColor(textColor = textColor, toneIndex = toneIndex, background = surfaceVariantColor)
    return lightColorScheme(
        primary = accent,
        onPrimary = readableContentColor(accent),
        primaryContainer = mixColor(accent, Color.White, 0.80f),
        onPrimaryContainer = readableContentColor(mixColor(accent, Color.White, 0.80f)),
        secondary = accent,
        onSecondary = readableContentColor(accent),
        secondaryContainer = mixColor(accent, Color.White, 0.88f),
        onSecondaryContainer = readableContentColor(mixColor(accent, Color.White, 0.88f)),
        background = body,
        onBackground = bodyContent,
        surface = header,
        onSurface = surfaceContent,
        surfaceVariant = surfaceVariantColor,
        onSurfaceVariant = variantContent,
        surfaceContainerLow = mixColor(body, panelTarget, panelMix),
        surfaceContainer = surfaceContainerColor,
        surfaceContainerHigh = mixColor(body, panelTarget, (panelMix * 0.5833333f).coerceIn(0f, 1f)),
        outline = ensureUiContrast(preferred = mixColor(accent, Color.Black, 0.22f), background = body, minimumContrast = 3f))
}

internal fun darkScheme(palette:
        MyNotesPalette, toneIndex: Int, backgroundIntensity: Float, surfacePanelIntensity: Float, headerIntensity: Float, textColor: String,
    accentColor: String): ColorScheme {
    val selected = palette.tones[toneIndex.coerceIn(0, 3)]
    val automaticPaletteMode = textColor != "black" && textColor != "white"
    val automaticDarkTone = automaticPaletteMode && toneIndex.coerceIn(0, 3) >= 2
    /*
     * En Automático la tonalidad elegida manda sobre el modo del sistema:
     * 1-2 permanecen claras (texto negro) y 3-4 oscuras (texto blanco).
     * En Negro/Blanco manual se conserva el comportamiento oscuro anterior.
     */
    val base = if (automaticPaletteMode) {
            selected
        } else {
            mixColor(selected, Color.Black, 0.84f)
        }
    val body = applySectionIntensity(base, backgroundIntensity)
    val header = applySectionIntensity(base, headerIntensity)
    /*
     * El modo oscuro usa mezclas mucho más pequeñas.
     * 72% conserva la apariencia anterior y el resto escala
     * proporcionalmente hasta el valor elegido por el usuario.
     */
    val panelScale = (surfacePanelIntensity.coerceIn(0f, 100f) / 72f).coerceIn(0f, 1.3888889f)
    val rawAccent = resolveAccentColor(accentColor, palette)
    val accent = mixColor(rawAccent, Color.White, 0.14f)
    val panelTarget = if (automaticDarkTone) Color.Black else Color.White
    val surfaceVariantColor = mixColor(body, panelTarget, (0.08f * panelScale).coerceIn(0f, 1f))
    val surfaceContainerColor = mixColor(body, panelTarget, (0.08f * panelScale).coerceIn(0f, 1f))
    val bodyContent = resolvedPaletteTextColor(textColor = textColor, toneIndex = toneIndex, background = body)
    val surfaceContent = resolvedPaletteTextColor(textColor = textColor, toneIndex = toneIndex, background = header)
    val variantContent = resolvedPaletteSecondaryTextColor(textColor = textColor, toneIndex = toneIndex, background = surfaceVariantColor)
    return darkColorScheme(
        primary = accent,
        onPrimary = readableContentColor(accent),
        primaryContainer = mixColor(rawAccent, Color.Black, 0.50f),
        onPrimaryContainer = readableContentColor(mixColor(rawAccent, Color.Black, 0.50f)),
        secondary = accent,
        onSecondary = readableContentColor(accent),
        secondaryContainer = mixColor(rawAccent, Color.Black, 0.60f),
        onSecondaryContainer = readableContentColor(mixColor(rawAccent, Color.Black, 0.60f)),
        background = body,
        onBackground = bodyContent,
        surface = header,
        onSurface = surfaceContent,
        surfaceVariant = surfaceVariantColor,
        onSurfaceVariant = variantContent,
        surfaceContainerLow = mixColor(body, panelTarget, (0.05f * panelScale).coerceIn(0f, 1f)),
        surfaceContainer = surfaceContainerColor,
        surfaceContainerHigh = mixColor(body, panelTarget, (0.12f * panelScale).coerceIn(0f, 1f)),
        outline = ensureUiContrast(preferred = mixColor(accent, Color.White, 0.18f), background = body, minimumContrast = 3f))
}

@Composable
fun MyNotesTheme(darkTheme: Boolean = isSystemInDarkTheme(),
    backgroundColor: String = "neutral",
    backgroundToneIndex: Int = 0,
    backgroundIntensity: Float = 0f,
    surfacePanelIntensity: Float = 72f,
    headerIntensity: Float = 18f,
    textColor: String = "auto",
    textOutlineEnabled: Boolean = false,
    accentColor: String = "palette",
    fontFamily: FontFamily = FontFamily.Default,
    content:
        @Composable () -> Unit) {
    val palette = remember(backgroundColor) {
            PaletteCatalog.find(backgroundColor)
        }
    /*
     * Crear un ColorScheme completo implica varias mezclas de Color.
     * Lo memorizamos para que cambiar avatar, tamaño de tarjeta, iconos,
     * filtros, búsqueda, etc. no vuelva a calcular el tema.
     */
    val scheme = remember(darkTheme, palette.key, backgroundToneIndex, backgroundIntensity, surfacePanelIntensity, headerIntensity,
            textColor, textOutlineEnabled, accentColor) {
            if (darkTheme) {
                darkScheme(palette = palette,
                    toneIndex = backgroundToneIndex,
                    backgroundIntensity = backgroundIntensity,
                    surfacePanelIntensity = surfacePanelIntensity,
                    headerIntensity = headerIntensity,
                    textColor = textColor,
                    accentColor = accentColor)
            } else {
                lightScheme(palette = palette,
                    toneIndex = backgroundToneIndex,
                    backgroundIntensity = backgroundIntensity,
                    surfacePanelIntensity = surfacePanelIntensity,
                    headerIntensity = headerIntensity,
                    textColor = textColor,
                    accentColor = accentColor)
            }
        }
    val resolvedTypography = remember(textOutlineEnabled, fontFamily) {
            val fontAwareTypography = typographyWithFontFamily(base = Typography, fontFamily = fontFamily)
            typographyWithBlackOutline(base = fontAwareTypography,
                enabled = textOutlineEnabled)
        }
    MaterialTheme(colorScheme = scheme,
        typography = resolvedTypography) {
        /*
         * Los componentes Material toman el borde de Typography.
         * Los Text() simples que usan LocalTextStyle también reciben
         * el mismo halo sin tener que modificar cada pantalla.
         */
        val inheritedTextStyle = if (textOutlineEnabled) {
                LocalTextStyle.current.copy(fontFamily = fontFamily, shadow = BlackTextOutlineShadow)
            } else {
                LocalTextStyle.current.copy(fontFamily = fontFamily)
            }
        CompositionLocalProvider(LocalTextStyle provides
                inheritedTextStyle) {
            content()
        }
    }
}

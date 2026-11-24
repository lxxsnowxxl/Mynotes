package com.example.mynotes.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.LocalIndication
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.ExperimentalMaterial3Api
import com.example.mynotes.ui.components.NoShadeIndication
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Typography as MaterialTypography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import com.example.mynotes.settings.AppSettings
import com.example.mynotes.settings.SettingsValuePolicy
import com.example.mynotes.ui.components.AppBackgroundProvider
import com.example.mynotes.ui.components.LocalSliderStyle

private const val FIXED_HEADER_DARKEN_AMOUNT = 0.054f
private fun mixColor(first: Color, second: Color, amount: Float): Color {
    val value = amount.coerceIn(0f, 1f)
    return Color(red = first.red + (second.red - first.red) * value,
        green = first.green + (second.green - first.green) * value,
        blue = first.blue + (second.blue - first.blue) * value,
        alpha = 1f)
}

private fun readableContentColor(background: Color): Color = automaticUiTextColor(background)

/** El modo básico usa el tema claro; el modo avanzado aplica su ajuste manual. */
fun AppSettings.effectiveDarkTheme(): Boolean =
    configurationMode == "advanced" && darkMode

/**
 * En el modo automático se respeta la intención de cada tono si cumple contraste.
 * Negro y blanco manuales son preferencias, pero la INTERFAZ nunca dibuja un
 * texto ilegible: sólo el color mostrado puede cambiar, no el valor guardado.
 * El contenido elegido expresamente dentro de una nota sigue siendo independiente.
 */
private fun usesDarkTextForTone(palette: MyNotesPalette, toneIndex: Int): Boolean =
    palette.normalizeToneIndex(toneIndex) < palette.darkToneStartIndex

private fun resolvedPaletteTextColor(textColor: String, palette: MyNotesPalette, toneIndex: Int, background: Color): Color {
    val preferred = when (textColor) {
        "black" -> Color.Black
        "white" -> Color.White
        else -> if (usesDarkTextForTone(palette, toneIndex)) Color.Black else Color.White
    }
    return ensureUiContrast(preferred, background, minimumContrast = 4.5f)
}
private fun resolvedPaletteSecondaryTextColor(textColor: String, palette: MyNotesPalette, toneIndex: Int, background: Color): Color {
    val primary = resolvedPaletteTextColor(textColor, palette, toneIndex, background)
    return softenUiColorToContrast(
        foreground = primary, background = background,
        minimumContrast = 4.5f, maximumSoftening = 0.42f
    )
}

/** Ajusta un acento en su misma dirección cromática hasta que sea visible. */
private fun readablePaletteAccent(preferred: Color, background: Color): Color {
    if (uiContrastRatio(preferred, background) >= 3f) return preferred
    val target = automaticUiTextColor(background)
    var low = 0f
    var high = 1f
    repeat(18) {
        val middle = (low + high) / 2f
        if (uiContrastRatio(mixColor(preferred, target, middle), background) >= 3.05f) {
            high = middle
        } else {
            low = middle
        }
    }
    return ensureUiContrast(mixColor(preferred, target, high), background, 3f)
}

/** Papeles, tarjetas, ventanas y toolbars usan la misma tonalidad como origen. */
private fun schemeWithCompleteSurfaces(
    background: Color,
    content: Color,
    panelTarget: Color,
    lowAmount: Float,
    containerAmount: Float,
    highAmount: Float,
    variantAmount: Float,
    accent: Color
): PaletteSurfaces {
    // No usar colores de contenedor predeterminados de Material (grises fijos).
    val lowest = mixColor(background, panelTarget, lowAmount * 0.55f)
    val low = mixColor(background, panelTarget, lowAmount)
    val middle = mixColor(background, panelTarget, containerAmount)
    val high = mixColor(background, panelTarget, highAmount)
    val highest = mixColor(background, panelTarget, maxOf(highAmount, containerAmount) * 1.16f)
    val variant = mixColor(background, panelTarget, variantAmount)
    val inverted = mixColor(background, content, 0.78f)
    return PaletteSurfaces(
        lowest = lowest, low = low, middle = middle, high = high,
        highest = highest, variant = variant, inverted = inverted,
        inverseInk = automaticUiTextColor(inverted),
        inverseAccent = readablePaletteAccent(accent, inverted)
    )
}

private data class PaletteSurfaces(
    val lowest: Color, val low: Color, val middle: Color, val high: Color,
    val highest: Color, val variant: Color, val inverted: Color,
    val inverseInk: Color, val inverseAccent: Color
)

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

private val SafeFontPlatformStyle = PlatformTextStyle(includeFontPadding = true)

/**
 * Mantiene las métricas completas de la familia elegida.
 *
 * Google Sans Bold, serif y algunas fuentes OEM pueden tener ascendentes o
 * descendentes mayores que Roboto. Sin font padding, los controles compactos
 * pueden recortar píxeles del glifo aunque el Text todavía quepa en el layout.
 */
private fun TextStyle.withAppFontMetrics(fontFamily: FontFamily): TextStyle = copy(
    fontFamily = fontFamily,
    platformStyle = SafeFontPlatformStyle
)

internal fun typographyWithFontFamily(base: MaterialTypography, fontFamily: FontFamily): MaterialTypography {
    return base.copy(
        displayLarge = base.displayLarge.withAppFontMetrics(fontFamily),
        displayMedium = base.displayMedium.withAppFontMetrics(fontFamily),
        displaySmall = base.displaySmall.withAppFontMetrics(fontFamily),
        headlineLarge = base.headlineLarge.withAppFontMetrics(fontFamily),
        headlineMedium = base.headlineMedium.withAppFontMetrics(fontFamily),
        headlineSmall = base.headlineSmall.withAppFontMetrics(fontFamily),
        titleLarge = base.titleLarge.withAppFontMetrics(fontFamily),
        titleMedium = base.titleMedium.withAppFontMetrics(fontFamily),
        titleSmall = base.titleSmall.withAppFontMetrics(fontFamily),
        bodyLarge = base.bodyLarge.withAppFontMetrics(fontFamily),
        bodyMedium = base.bodyMedium.withAppFontMetrics(fontFamily),
        bodySmall = base.bodySmall.withAppFontMetrics(fontFamily),
        labelLarge = base.labelLarge.withAppFontMetrics(fontFamily),
        labelMedium = base.labelMedium.withAppFontMetrics(fontFamily),
        labelSmall = base.labelSmall.withAppFontMetrics(fontFamily)
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

internal fun lightScheme(palette: MyNotesPalette, toneIndex: Int, surfacePanelIntensity: Float,
    textColor: String, accentColor: String): ColorScheme {
    val body = palette.toneAt(toneIndex)
    val header = mixColor(body, Color.Black, FIXED_HEADER_DARKEN_AMOUNT)
    val bodyContent = resolvedPaletteTextColor(textColor, palette, toneIndex, body)
    val surfaceContent = resolvedPaletteTextColor(textColor, palette, toneIndex, header)
    val panelTarget = if (bodyContent == Color.Black) Color.White else Color.Black
    val panelMix = (surfacePanelIntensity.coerceIn(0f, 100f) / 100f)
    val accent = readablePaletteAccent(resolveAccentColor(accentColor, palette), body)
    val surfaces = schemeWithCompleteSurfaces(body, surfaceContent, panelTarget,
        lowAmount = panelMix, containerAmount = (panelMix * 0.8055556f),
        highAmount = (panelMix * 0.5833333f), variantAmount = (panelMix * 0.625f), accent = accent)
    val primaryContainer = mixColor(accent, panelTarget, 0.80f)
    val secondaryContainer = mixColor(accent, panelTarget, 0.88f)
    return lightColorScheme(
        primary = accent, onPrimary = readableContentColor(accent),
        primaryContainer = primaryContainer, onPrimaryContainer = readableContentColor(primaryContainer),
        secondary = accent, onSecondary = readableContentColor(accent),
        secondaryContainer = secondaryContainer, onSecondaryContainer = readableContentColor(secondaryContainer),
        tertiary = accent, onTertiary = readableContentColor(accent),
        tertiaryContainer = secondaryContainer, onTertiaryContainer = readableContentColor(secondaryContainer),
        background = body, onBackground = bodyContent,
        surface = header, onSurface = surfaceContent,
        surfaceVariant = surfaces.variant,
        onSurfaceVariant = resolvedPaletteSecondaryTextColor(textColor, palette, toneIndex, surfaces.variant),
        surfaceContainerLowest = surfaces.lowest, surfaceContainerLow = surfaces.low,
        surfaceContainer = surfaces.middle, surfaceContainerHigh = surfaces.high,
        surfaceContainerHighest = surfaces.highest,
        surfaceBright = surfaces.highest, surfaceDim = surfaces.lowest,
        inverseSurface = surfaces.inverted, inverseOnSurface = surfaces.inverseInk,
        inversePrimary = surfaces.inverseAccent,
        surfaceTint = accent,
        outline = paletteMatchedOutlineColor(body, minimumContrast = 3f),
        outlineVariant = paletteMatchedOutlineColor(surfaces.variant, minimumContrast = 3f)
    )
}

internal fun darkScheme(palette: MyNotesPalette, toneIndex: Int, surfacePanelIntensity: Float,
    textColor: String, accentColor: String): ColorScheme {
    // La elección manual del color de texto no puede volver negros los cinco tonos.
    // La tonalidad seleccionada es siempre el color real de la pantalla.
    val body = palette.toneAt(toneIndex)
    val header = mixColor(body, Color.Black, FIXED_HEADER_DARKEN_AMOUNT)
    val bodyContent = resolvedPaletteTextColor(textColor, palette, toneIndex, body)
    val surfaceContent = resolvedPaletteTextColor(textColor, palette, toneIndex, header)
    val panelTarget = if (bodyContent == Color.Black) Color.White else Color.Black
    val panelScale = (surfacePanelIntensity.coerceIn(0f, 100f) / 72f).coerceIn(0f, 1.3888889f)
    val rawAccent = resolveAccentColor(accentColor, palette)
    val accent = readablePaletteAccent(mixColor(rawAccent, Color.White, 0.14f), body)
    val surfaces = schemeWithCompleteSurfaces(body, surfaceContent, panelTarget,
        lowAmount = (0.05f * panelScale).coerceIn(0f, 1f),
        containerAmount = (0.08f * panelScale).coerceIn(0f, 1f),
        highAmount = (0.12f * panelScale).coerceIn(0f, 1f),
        variantAmount = (0.08f * panelScale).coerceIn(0f, 1f), accent = accent)
    val primaryContainer = mixColor(rawAccent, panelTarget, 0.50f)
    val secondaryContainer = mixColor(rawAccent, panelTarget, 0.60f)
    return darkColorScheme(
        primary = accent, onPrimary = readableContentColor(accent),
        primaryContainer = primaryContainer, onPrimaryContainer = readableContentColor(primaryContainer),
        secondary = accent, onSecondary = readableContentColor(accent),
        secondaryContainer = secondaryContainer, onSecondaryContainer = readableContentColor(secondaryContainer),
        tertiary = accent, onTertiary = readableContentColor(accent),
        tertiaryContainer = secondaryContainer, onTertiaryContainer = readableContentColor(secondaryContainer),
        background = body, onBackground = bodyContent,
        surface = header, onSurface = surfaceContent,
        surfaceVariant = surfaces.variant,
        onSurfaceVariant = resolvedPaletteSecondaryTextColor(textColor, palette, toneIndex, surfaces.variant),
        surfaceContainerLowest = surfaces.lowest, surfaceContainerLow = surfaces.low,
        surfaceContainer = surfaces.middle, surfaceContainerHigh = surfaces.high,
        surfaceContainerHighest = surfaces.highest,
        surfaceBright = surfaces.highest, surfaceDim = surfaces.lowest,
        inverseSurface = surfaces.inverted, inverseOnSurface = surfaces.inverseInk,
        inversePrimary = surfaces.inverseAccent,
        surfaceTint = accent,
        outline = paletteMatchedOutlineColor(body, minimumContrast = 3f),
        outlineVariant = paletteMatchedOutlineColor(surfaces.variant, minimumContrast = 3f)
    )
}

internal fun resolveAppColorScheme(
    darkTheme: Boolean,
    palette: MyNotesPalette,
    toneIndex: Int,
    surfacePanelIntensity: Float,
    textColor: String,
    accentColor: String
): ColorScheme = if (darkTheme) {
    darkScheme(palette, toneIndex, surfacePanelIntensity, textColor, accentColor)
} else {
    lightScheme(palette, toneIndex, surfacePanelIntensity, textColor, accentColor)
}

/** OLED is explicitly black. Foregrounds stay accessible even for manual black/white. */
internal fun oledColorScheme(base:ColorScheme, @Suppress("UNUSED_PARAMETER") textColor:String):ColorScheme {
    return base.copy(background = Color.Black, surface = Color.Black,
        surfaceContainerLowest = Color.Black, surfaceContainerLow = Color(0xFF101010),
        surfaceContainer = Color(0xFF171717), surfaceContainerHigh = Color(0xFF202020),
        surfaceContainerHighest = Color(0xFF292929), surfaceVariant = Color(0xFF202020),
        surfaceBright = Color(0xFF292929), surfaceDim = Color.Black,
        onBackground = Color.White, onSurface = Color.White,
        onSurfaceVariant = Color(0xFFD5D5D5),
        outline = ensureUiContrast(base.outline, Color.Black, 3f),
        outlineVariant = paletteMatchedOutlineColor(Color(0xFF202020), 3f))
}

@Composable
fun MyNotesTheme(settings: AppSettings, darkTheme: Boolean, fontFamily: FontFamily = FontFamily.Default,
    content: @Composable () -> Unit) = MyNotesTheme(darkTheme = darkTheme,
    backgroundColor = settings.backgroundColor, backgroundToneIndex = settings.backgroundToneIndex,
    surfacePanelIntensity = settings.surfacePanelIntensity, textColor = settings.textColor,
    textOutlineEnabled = settings.textOutlineEnabled, accentColor = settings.accentColor,
    fontFamily = fontFamily, oled = settings.oledMode,
    content = {
        CompositionLocalProvider(LocalSliderStyle provides SettingsValuePolicy.sliderStyle(settings.sliderStyle)) {
            AppBackgroundProvider(settings, content)
        }
    })

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun MyNotesTheme(darkTheme: Boolean = isSystemInDarkTheme(),
    backgroundColor: String = "neutral",
    backgroundToneIndex: Int = 0,
    surfacePanelIntensity: Float = 72f,
    textColor: String = "auto",
    textOutlineEnabled: Boolean = false,
    accentColor: String = "palette",
    fontFamily: FontFamily = FontFamily.Default,
    oled: Boolean = false,
    content:
        @Composable () -> Unit) {
    val palette = remember(backgroundColor) {
            PaletteCatalog.find(backgroundColor)
        }
    val scheme = remember(darkTheme, palette.key, backgroundToneIndex, surfacePanelIntensity,
            textColor, textOutlineEnabled, accentColor, oled) {
            val base = resolveAppColorScheme(darkTheme, palette, backgroundToneIndex, surfacePanelIntensity, textColor, accentColor)
            if (oled && darkTheme) oledColorScheme(base,textColor) else base
        }
    val resolvedTypography = remember(textOutlineEnabled, fontFamily) {
            val fontAwareTypography = typographyWithFontFamily(base = Typography, fontFamily = fontFamily)
            typographyWithBlackOutline(base = fontAwareTypography,
                enabled = textOutlineEnabled)
        }
    MaterialTheme(colorScheme = scheme,
        typography = resolvedTypography, shapes = Shapes(extraSmall = RoundedCornerShape(8.dp), small = RoundedCornerShape(12.dp), medium = RoundedCornerShape(16.dp), large = RoundedCornerShape(22.dp), extraLarge = RoundedCornerShape(28.dp))) {
        val inheritedTextStyle = LocalTextStyle.current.withAppFontMetrics(fontFamily).let { baseStyle ->
            if (textOutlineEnabled) baseStyle.copy(shadow = BlackTextOutlineShadow) else baseStyle
        }
        // Disable press overlays throughout the application, including Material3 buttons
        // and Foundation clickable surfaces. Keep disabled/selected colors unchanged.
        CompositionLocalProvider(
            LocalTextStyle provides inheritedTextStyle,
            LocalRippleConfiguration provides null,
            LocalIndication provides NoShadeIndication
        ) {
            com.example.mynotes.studio.PlusStyle(content)
        }
    }
}

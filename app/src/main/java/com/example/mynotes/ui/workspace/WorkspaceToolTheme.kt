package com.example.mynotes.ui.workspace

import androidx.compose.foundation.LocalIndication
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.LocalTonalElevationEnabled
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import com.example.mynotes.ui.theme.MyNotesPalette
import com.example.mynotes.ui.theme.adaptiveUiButtonContainer
import com.example.mynotes.ui.theme.compositeUiColor
import com.example.mynotes.ui.theme.ensureUiContrast
import com.example.mynotes.ui.theme.softenUiColorToContrast
import com.example.mynotes.ui.theme.uiContrastRatio
import com.example.mynotes.ui.components.LocalAppBackground

private const val ToolTextContrast = 4.5f

internal fun workspaceToolColorScheme(
    app: ColorScheme,
    palette: MyNotesPalette,
    panelIntensity: Float
): ColorScheme {
    val background = app.background.copy(alpha = 1f)
    val foreground = ensureUiContrast(app.onBackground, background, ToolTextContrast)
    val darkInk = foreground.luminance() < background.luminance()
    val depth = 0.70f + 0.40f * (panelIntensity.coerceIn(0f, 100f) / 100f)
    val reinforcingTone = if (darkInk) palette.tones.last() else palette.tones.first()
    val paletteShade = compositeUiColor(reinforcingTone.copy(alpha = 0.28f), palette.accent)

    // Move toward the other end of the palette when the selected tone is near
    // the contrast threshold. Reserve the neutral fallback for extreme tones.
    val alternateShade = if (darkInk) palette.tones.first() else palette.tones.last()
    val fallbackShade = if (darkInk) Color.White else Color.Black
    val shade = listOf(paletteShade, alternateShade, fallbackShade).first { tint ->
        val deepestSurface = compositeUiColor(tint.copy(alpha = 0.15f * depth), background)
        uiContrastRatio(foreground, deepestSurface) >= ToolTextContrast
    }

    fun surface(tint: Float): Color = compositeUiColor(shade.copy(alpha = tint * depth), background)

    val low = surface(0.04f)
    val middle = surface(0.075f)
    val high = surface(0.11f)
    val highest = surface(0.15f)
    val surfaces = listOf(background, low, middle, high, highest)
    val limitingSurface = surfaces.minBy { uiContrastRatio(foreground, it) }
    val secondaryText = softenUiColorToContrast(foreground, limitingSurface, ToolTextContrast, 0.52f)
    val outline = softenUiColorToContrast(foreground, limitingSurface, 3f, 0.74f)

    fun readableAccent(preferred: Color): Color {
        for (step in 0..12) {
            val tint = compositeUiColor(foreground.copy(alpha = step / 12f), preferred)
            if (surfaces.all { uiContrastRatio(tint, it) >= ToolTextContrast }) return tint
        }
        return foreground
    }

    val accent = readableAccent(app.primary)
    val accentText = ensureUiContrast(app.onPrimary, accent, ToolTextContrast)
    // Selection fills stay within the background palette even with a custom action accent.
    val selectionInk = readableAccent(paletteShade)
    var selected = selectionInk
    for (amount in listOf(0.24f, 0.33f, 0.43f, 0.55f)) {
        val tint = compositeUiColor(selectionInk.copy(alpha = amount), background)
        if (uiContrastRatio(tint, high) >= 1.2f) {
            selected = tint
            break
        }
    }
    val selectedText = ensureUiContrast(foreground, selected, ToolTextContrast)
    val error = readableAccent(app.error)

    return app.copy(
        background = background,
        surface = background,
        onSurface = foreground,
        onBackground = foreground,
        surfaceContainerLowest = background,
        surfaceContainerLow = low,
        surfaceContainer = middle,
        surfaceContainerHigh = high,
        surfaceContainerHighest = highest,
        surfaceVariant = middle,
        onSurfaceVariant = secondaryText,
        surfaceDim = low,
        surfaceBright = highest,
        primary = accent,
        onPrimary = accentText,
        primaryContainer = selected,
        onPrimaryContainer = selectedText,
        secondary = accent,
        onSecondary = accentText,
        secondaryContainer = selected,
        onSecondaryContainer = selectedText,
        tertiary = accent,
        onTertiary = accentText,
        tertiaryContainer = selected,
        onTertiaryContainer = selectedText,
        surfaceTint = accent,
        outline = outline,
        outlineVariant = outline,
        error = error,
        onError = ensureUiContrast(app.onError, error, ToolTextContrast)
    )
}

@Immutable
internal data class WorkspaceDrawerColors(
    val controls: ColorScheme,
    val section: Color,
    val sectionMutedContent: Color,
    val sectionErrorContent: Color,
    val disabledButtonContent: Color
)

internal val LocalWorkspaceDrawerColors = staticCompositionLocalOf<WorkspaceDrawerColors?> { null }

/** Selects a light palette tone that distinguishes the button group from its panel. */
internal fun workspaceDrawerColors(
    palette: MyNotesPalette,
    toneIndex: Int,
    panelIntensity: Float
): WorkspaceDrawerColors {
    val shade = compositeUiColor(palette.toneAt(toneIndex).copy(alpha = 0.16f), palette.accent)
    val panel = compositeUiColor(shade.copy(alpha = 0.09f), Color.White)
    val accent = (0..20).firstNotNullOf { step ->
        val tint = compositeUiColor(shade.copy(alpha = 1f - step / 20f), Color.Black)
        tint.takeIf { uiContrastRatio(Color.White, it) >= 5.2f }
    }

    val preferredSection = palette.tones.firstOrNull { tone ->
        uiContrastRatio(Color.Black, tone) >= 7f && uiContrastRatio(tone, panel) >= 1.25f
    } ?: palette.toneAt(1)
    val sectionBase = adaptiveUiButtonContainer(
        preferred = preferredSection,
        background = panel,
        contentColor = Color.Black,
        minimumContentContrast = 7f,
        minimumSurfaceContrast = 1.25f
    )
    val section = softenUiColorToContrast(sectionBase, panel, 1.25f, 0.90f)

    val depth = 0.85f + 0.30f * (panelIntensity.coerceIn(0f, 100f) / 100f)
    fun lightFill(amount: Float) = compositeUiColor(accent.copy(alpha = amount * depth), Color.White)

    val idle = lightFill(0.10f)
    val selected = lightFill(0.25f)
    val disabled = lightFill(0.04f)
    val outlineBackground = listOf(section, selected).minBy { uiContrastRatio(accent, it) }
    val border = softenUiColorToContrast(accent, outlineBackground, 3f, 0.70f)
    return WorkspaceDrawerColors(
        controls = lightColorScheme(
            primary = accent,
            onPrimary = Color.White,
            primaryContainer = selected,
            onPrimaryContainer = Color.Black,
            inversePrimary = selected,
            secondary = accent,
            onSecondary = Color.White,
            secondaryContainer = selected,
            onSecondaryContainer = Color.Black,
            tertiary = accent,
            onTertiary = Color.White,
            tertiaryContainer = selected,
            onTertiaryContainer = Color.Black,
            background = panel,
            onBackground = Color.Black,
            surface = panel,
            onSurface = Color.Black,
            onSurfaceVariant = Color(0xFF343434),
            surfaceVariant = lightFill(0.07f),
            surfaceContainerLowest = panel,
            surfaceContainerLow = disabled,
            surfaceContainer = lightFill(0.07f),
            surfaceContainerHigh = idle,
            surfaceContainerHighest = lightFill(0.15f),
            surfaceBright = panel,
            surfaceDim = idle,
            outline = accent,
            outlineVariant = border,
            surfaceTint = accent,
            onErrorContainer = Color.Black
        ),
        section = section,
        sectionMutedContent = softenUiColorToContrast(Color.Black, section, ToolTextContrast, 0.56f),
        sectionErrorContent = ensureUiContrast(Color(0xFFB3261E), section, ToolTextContrast),
        disabledButtonContent = softenUiColorToContrast(Color.Black, disabled, ToolTextContrast, 0.56f)
    )
}

@Composable
internal fun WorkspaceToolTheme(colors: ColorScheme, content: @Composable () -> Unit) {
    val indication = LocalIndication.current
    MaterialTheme(colorScheme = colors) {
        CompositionLocalProvider(
            LocalIndication provides indication,
            LocalTonalElevationEnabled provides false,
            content = content
        )
    }
}

/** Palette-derived opaque controls over the shared solid, gradient or blur background. */
@Composable
internal fun PaletteWorkspaceTheme(
    panelIntensity: Float,
    typography: Typography = MaterialTheme.typography,
    content: @Composable () -> Unit
) {
    val appColors = MaterialTheme.colorScheme
    val appearance = LocalAppBackground.current
    // MaterialTheme can update its ColorScheme in place. Observe the actual
    // color values so changing a tone or action accent refreshes existing panels.
    val colors = remember(
        appearance, panelIntensity,
        appColors.background, appColors.onBackground,
        appColors.primary, appColors.onPrimary,
        appColors.error, appColors.onError,
        appColors.errorContainer, appColors.onErrorContainer,
        appColors.inverseSurface, appColors.inverseOnSurface, appColors.inversePrimary,
        appColors.scrim
    ) {
        workspaceToolColorScheme(appColors, appearance.palette, panelIntensity)
    }
    val indication = LocalIndication.current
    MaterialTheme(colorScheme = colors, typography = typography) {
        CompositionLocalProvider(
            LocalIndication provides indication,
            LocalTonalElevationEnabled provides false,
            content = content
        )
    }
}

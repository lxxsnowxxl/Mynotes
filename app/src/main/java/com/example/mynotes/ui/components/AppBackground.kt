package com.example.mynotes.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.mynotes.settings.AppSettings
import com.example.mynotes.settings.SettingsValuePolicy
import com.example.mynotes.ui.theme.MyNotesPalette
import com.example.mynotes.ui.theme.PaletteCatalog

internal data class AppBackgroundAppearance(
    val style: String, val palette: MyNotesPalette, val toneIndex: Int
)

private val LocalAppBackground = compositionLocalOf {
    AppBackgroundAppearance("solid", PaletteCatalog.find("neutral"), 0)
}

internal fun appBackgroundAppearance(settings: AppSettings): AppBackgroundAppearance {
    val palette = PaletteCatalog.find(settings.backgroundColor)
    return AppBackgroundAppearance(
        SettingsValuePolicy.settingsBackgroundStyle(settings.settingsBackgroundStyle),
        palette, palette.normalizeToneIndex(settings.backgroundToneIndex)
    )
}

/** Cada actividad obtiene el mismo estilo guardado a través de MyNotesTheme. */
@Composable
internal fun AppBackgroundProvider(settings: AppSettings, content: @Composable () -> Unit) {
    val appearance = remember(settings.settingsBackgroundStyle, settings.backgroundColor, settings.backgroundToneIndex) {
        appBackgroundAppearance(settings)
    }
    CompositionLocalProvider(
        LocalAppBackground provides appearance,
        LocalContentColor provides MaterialTheme.colorScheme.onBackground
    ) {
        Box(Modifier.fillMaxSize().appScreenBackground()) { content() }
    }
}

/** Permite conservar el color propio de una nota y aplicar el efecto global. */
@Composable
internal fun Modifier.appScreenBackground(
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    foregroundColor: Color = MaterialTheme.colorScheme.onBackground
): Modifier {
    val appearance = LocalAppBackground.current
    val colors = remember(appearance, backgroundColor, foregroundColor) {
        settingsBackgroundColors(appearance.palette, appearance.toneIndex, backgroundColor, foregroundColor)
    }
    return settingsBackground(appearance.style, colors)
}

/** Las barras dejan ver el fondo que ya dibuja su pantalla. */
@Composable
internal fun appScreenContainerColor(backgroundColor: Color = MaterialTheme.colorScheme.background): Color =
    if (LocalAppBackground.current.style == "solid") backgroundColor else Color.Transparent

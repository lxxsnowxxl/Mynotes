package com.example.mynotes.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.State
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

internal val LocalAppBackground = compositionLocalOf {
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
    val clean = com.example.mynotes.studio.LocalCleanWorkspace.current
    val appearance = remember(settings.settingsBackgroundStyle, settings.backgroundColor, settings.backgroundToneIndex, clean) {
        appBackgroundAppearance(settings).let { if (clean) it.copy(style = "solid") else it }
    }
    BackgroundMotionProvider(appearance.style, settings.animationsEnabled, settings.performanceMode, settings.animationSpeed) {
        CompositionLocalProvider(
            LocalAppBackground provides appearance,
            LocalContentColor provides MaterialTheme.colorScheme.onBackground
        ) {
            // El respaldo queda estático; sólo las pantallas visibles consumen el reloj compartido.
            Box(Modifier.fillMaxSize().appScreenBackground(animate = false)) { content() }
        }
    }
}

/** Las notas con color fijo dibujan una capa opaca sin efectos de la paleta global. */
@Composable
internal fun Modifier.appScreenBackground(
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    foregroundColor: Color = MaterialTheme.colorScheme.onBackground,
    useGlobalStyle: Boolean = true,
    animate: Boolean = true,
    paused: State<Boolean>? = null
): Modifier {
    if (!useGlobalStyle) return background(backgroundColor.copy(alpha = 1f))
    val appearance = LocalAppBackground.current
    val colors = remember(appearance, backgroundColor, foregroundColor) {
        settingsBackgroundColors(appearance.palette, appearance.toneIndex, backgroundColor, foregroundColor)
    }
    val animateBackground = animate && appearance.style == "animated_blur"
    val phase = rememberBackgroundMotionPhase(animateBackground, paused = animateBackground && paused?.value == true)
    return settingsBackground(appearance.style, colors, phase)
}

/** Las barras son opacas en notas con color fijo y dejan ver los efectos globales en las demás. */
@Composable
internal fun appScreenContainerColor(
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    useGlobalStyle: Boolean = true
): Color = when {
    !useGlobalStyle -> backgroundColor.copy(alpha = 1f)
    LocalAppBackground.current.style == "solid" -> backgroundColor
    else -> Color.Transparent
}

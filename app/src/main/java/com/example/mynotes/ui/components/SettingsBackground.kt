package com.example.mynotes.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.FloatState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.first
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mynotes.R
import com.example.mynotes.ui.sound.UiActionSound
import com.example.mynotes.ui.sound.UiSoundPlayer
import com.example.mynotes.ui.theme.MyNotesPalette
import com.example.mynotes.ui.theme.SettingsSectionColors
import com.example.mynotes.ui.theme.uiContrastRatio
import com.example.mynotes.ui.theme.ensureUiContrast

internal data class SettingsBackgroundColors(val base: Color, val first: Color, val second: Color)

private fun mixSettingsBackground(first: Color, second: Color, amount: Float): Color = Color(
    red = first.red + (second.red - first.red) * amount,
    green = first.green + (second.green - first.green) * amount,
    blue = first.blue + (second.blue - first.blue) * amount,
    alpha = 1f
)

/** Conserva el contraste disponible y evita que ambos efectos queden planos. */
internal fun settingsBackgroundColors(
    palette: MyNotesPalette, toneIndex: Int, base: Color, foreground: Color
): SettingsBackgroundColors {
    // Los colores de texto manuales pueden partir de un contraste inferior a 4.5.
    // El pequeño margen protege la interpolación y su redondeo a canales de 8 bits.
    val minimumContrast = minOf(4.5f, uiContrastRatio(foreground, base) + 0.05f)
    fun tint(source: Color, amount: Float): Color {
        val candidate = mixSettingsBackground(base, source, amount)
        for (step in 0..10) {
            val color = mixSettingsBackground(candidate, base, step / 10f)
            if (uiContrastRatio(foreground, color) >= minimumContrast) return color
        }
        return base
    }
    val nextTone = palette.tones[(palette.normalizeToneIndex(toneIndex) + 2) % palette.tones.size]
    val first = tint(nextTone, 0.52f)
    val second = tint(palette.accent, 0.30f)
    if (first == base && second == base) {
        // Algunos tonos cercanos al límite de contraste rechazan ambos tintes.
        // Una variación de luminosidad permite conservar el degradado y el blur.
        val depth = if (uiContrastRatio(foreground, Color.Black) >= uiContrastRatio(foreground, Color.White)) {
            Color.Black
        } else Color.White
        return SettingsBackgroundColors(base, tint(depth, 0.18f), tint(depth, 0.08f))
    }
    return SettingsBackgroundColors(base, first, second)
}

internal data class SettingsBlurSpot(val x: Float, val y: Float, val radius: Float, val colorIndex: Int)

// Posiciones y caída de opacidad compartidas con los recursos XML de diseño.
internal val SettingsBlurSpots = listOf(
    SettingsBlurSpot(0.12f, 0.22f, 0.76f, 0),
    SettingsBlurSpot(0.94f, 0.59f, 0.86f, 1),
    SettingsBlurSpot(0.24f, 0.96f, 0.70f, 0)
)
// El modo animado necesita espacio para recorrer la pantalla sin quedar limitado a sus bordes.
internal val SettingsAnimatedBlurSpots = listOf(
    SettingsBlurSpot(0.32f, 0.26f, 0.76f, 0),
    SettingsBlurSpot(0.68f, 0.54f, 0.86f, 1),
    SettingsBlurSpot(0.42f, 0.74f, 0.70f, 0)
)
internal val SettingsBlurStops = listOf(0f to 1f, 0.25f to 0.82f, 0.50f to 0.42f, 0.75f to 0.10f, 1f to 0f)

/** Tonos difuminados compatibles con API 24; el contenido permanece nítido. */
internal fun Modifier.settingsBackground(
    style: String, colors: SettingsBackgroundColors, motionPhase: FloatState? = null
): Modifier {
    val animated = style == "animated_blur"
    if (style != "gradient" && style != "blur" && !animated) return background(colors.base)
    return drawWithCache {
        val referenceWidth = 411f
        val referenceHeight = 891f
        val referenceSize = Size(referenceWidth, referenceHeight)
        val scaleX = size.width / referenceWidth
        val scaleY = size.height / referenceHeight
        val gradient = if (style == "gradient") Brush.linearGradient(
            0f to colors.first, 0.5f to colors.base, 1f to colors.second,
            start = Offset.Zero, end = Offset(referenceWidth, referenceHeight)
        ) else null
        val spots = if (animated) SettingsAnimatedBlurSpots else SettingsBlurSpots
        val blurBrushes = if (style == "blur" || animated) spots.map { spot ->
            Brush.radialGradient(
                *SettingsBlurStops.map { (offset, opacity) ->
                    offset to (if (spot.colorIndex == 0) colors.first else colors.second).copy(alpha = opacity)
                }.toTypedArray(),
                center = Offset(spot.x * referenceWidth, spot.y * referenceHeight),
                radius = spot.radius * referenceWidth
            )
        } else emptyList()
        onDrawBehind {
            clipRect {
                withTransform({ scale(scaleX, scaleY, pivot = Offset.Zero) }) {
                    drawRect(colors.base, size = referenceSize)
                    if (gradient != null) drawRect(gradient, size = referenceSize)
                    else if (animated) {
                        // La fase se lee en dibujo; no recrea los pinceles ni recompone el contenido.
                        val phase = motionPhase?.floatValue ?: 0f
                        blurBrushes.forEachIndexed { index, brush ->
                            val spot = spots[index]
                            val drift = settingsBlurMotionOffset(index, phase)
                            withTransform({ translate(drift.x * referenceWidth, drift.y * referenceHeight) }) {
                                drawCircle(brush, radius = spot.radius * referenceWidth,
                                    center = Offset(spot.x * referenceWidth, spot.y * referenceHeight))
                            }
                        }
                    } else blurBrushes.forEach { drawRect(it, size = referenceSize) }
                }
            }
        }
    }
}

@Composable
internal fun SettingsBackgroundOptions(
    selectedStyle: String, colors: SettingsBackgroundColors, panel: SettingsSectionColors,
    fontFamily: FontFamily, onStyleSelected: (String) -> Unit, scrolling: State<Boolean>? = null
) {
    val fontScale = LocalConfiguration.current.fontScale
    val options = listOf("solid" to R.string.settings_background_solid,
        "gradient" to R.string.settings_background_gradient,
        "blur" to R.string.settings_background_blur,
        "animated_blur" to R.string.settings_background_animated_blur)
    var alignedStyle by rememberSaveable { mutableStateOf(selectedStyle) }
    val density = LocalDensity.current
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        // Conserva el ancho de las tres opciones visibles; las demás se alcanzan deslizando.
        val tileWidth = ((maxWidth - 16.dp) / 3f).coerceAtLeast(64.dp) * fontScale.coerceAtLeast(1f)
        val tileWidthPx = with(density) { tileWidth.roundToPx() }
        val gapPx = with(density) { 8.dp.roundToPx() }
        val viewportPx = with(density) { maxWidth.roundToPx() }
        val initialIndex = options.indexOfFirst { it.first == selectedStyle }.coerceAtLeast(0)
        val initialOffset = (initialIndex * (tileWidthPx + gapPx) + tileWidthPx - viewportPx).coerceAtLeast(0)
        val scrollState = rememberScrollState(initial = initialOffset)
        LaunchedEffect(selectedStyle, tileWidthPx, gapPx, viewportPx) {
            if (alignedStyle == selectedStyle) return@LaunchedEffect
            val maximum = snapshotFlow { scrollState.maxValue }.first { it != Int.MAX_VALUE }
            val index = options.indexOfFirst { it.first == selectedStyle }
            if (index < 0) return@LaunchedEffect
            val start = index * (tileWidthPx + gapPx)
            val end = start + tileWidthPx
            val target = when {
                start < scrollState.value -> start
                end > scrollState.value + viewportPx -> end - viewportPx
                else -> scrollState.value
            }
            scrollState.scrollTo(target.coerceIn(0, maximum))
            alignedStyle = selectedStyle
        }
        Row(Modifier.fillMaxWidth().horizontalScroll(scrollState), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            options.forEach { (style, labelRes) ->
                BackgroundStyleOption(style, labelRes, style == selectedStyle, colors, panel,
                    fontFamily, onStyleSelected, Modifier.width(tileWidth), paused = scrolling?.value == true)
            }
        }
    }
}

@Composable
private fun BackgroundStyleOption(
    style: String, labelRes: Int, isSelected: Boolean, colors: SettingsBackgroundColors,
    panel: SettingsSectionColors, fontFamily: FontFamily, onStyleSelected: (String) -> Unit,
    modifier: Modifier, paused: Boolean = false
) {
    val context = LocalContext.current
    val phase = rememberBackgroundMotionPhase(isSelected && (style == "animated_blur"), paused = paused)
    val previewGraphic = ensureUiContrast(panel.graphic, colors.base, 3f)
    val previewText = ensureUiContrast(panel.text, colors.base, 4.5f)
    Surface(
        onClick = UiSoundPlayer.actionHandler(context, UiActionSound.Color) { onStyleSelected(style) },
        modifier = modifier.semantics { selected = isSelected; role = Role.RadioButton },
        color = panel.background, shape = RoundedCornerShape(14.dp),
        border = BorderStroke(if (isSelected) 2.dp else 1.dp,
            panel.graphic),
        tonalElevation = 0.dp, shadowElevation = 0.dp
    ) {
        Column(Modifier.padding(7.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(Modifier.fillMaxWidth().height(64.dp).clip(RoundedCornerShape(9.dp)).settingsBackground(style, colors, phase)) {
                if (style == "animated_blur") Icon(Icons.Default.PlayArrow, contentDescription = null, tint = previewGraphic,
                    modifier = Modifier.align(Alignment.TopStart).padding(4.dp).size(16.dp))
                if (isSelected) Icon(Icons.Default.Check, contentDescription = null, tint = previewText,
                    modifier = Modifier.align(Alignment.TopEnd).padding(4.dp).size(18.dp))
            }
            Text(stringResource(labelRes), color = panel.text, fontFamily = fontFamily,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 12.sp, textAlign = TextAlign.Center, maxLines = 1, overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth().padding(top = 7.dp))
        }
    }
}

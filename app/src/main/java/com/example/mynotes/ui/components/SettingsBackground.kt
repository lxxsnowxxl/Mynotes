package com.example.mynotes.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mynotes.R
import com.example.mynotes.ui.sound.UiActionSound
import com.example.mynotes.ui.sound.UiSoundPlayer
import com.example.mynotes.ui.theme.MyNotesPalette
import com.example.mynotes.ui.theme.SettingsSectionColors
import com.example.mynotes.ui.theme.uiContrastRatio

internal data class SettingsBackgroundColors(val base: Color, val first: Color, val second: Color)

private fun mixSettingsBackground(first: Color, second: Color, amount: Float): Color = Color(
    red = first.red + (second.red - first.red) * amount,
    green = first.green + (second.green - first.green) * amount,
    blue = first.blue + (second.blue - first.blue) * amount,
    alpha = 1f
)

/** Conserva el contraste del texto al mezclar los tonos del fondo. */
internal fun settingsBackgroundColors(
    palette: MyNotesPalette, toneIndex: Int, base: Color, foreground: Color
): SettingsBackgroundColors {
    fun tint(source: Color, amount: Float): Color {
        val candidate = mixSettingsBackground(base, source, amount)
        for (step in 0..10) {
            val color = mixSettingsBackground(candidate, base, step / 10f)
            if (uiContrastRatio(foreground, color) >= 4.5f) return color
        }
        return base
    }
    val nextTone = palette.tones[(palette.normalizeToneIndex(toneIndex) + 2) % palette.tones.size]
    return SettingsBackgroundColors(base, tint(nextTone, 0.52f), tint(palette.accent, 0.30f))
}

internal data class SettingsBlurSpot(val x: Float, val y: Float, val radius: Float, val colorIndex: Int)

// Posiciones y caída de opacidad compartidas con los recursos XML de diseño.
internal val SettingsBlurSpots = listOf(
    SettingsBlurSpot(0.12f, 0.22f, 0.76f, 0),
    SettingsBlurSpot(0.94f, 0.59f, 0.86f, 1),
    SettingsBlurSpot(0.24f, 0.96f, 0.70f, 0)
)
internal val SettingsBlurStops = listOf(0f to 1f, 0.25f to 0.82f, 0.50f to 0.42f, 0.75f to 0.10f, 1f to 0f)

/** Tonos difuminados compatibles con API 24; el contenido permanece nítido. */
internal fun Modifier.settingsBackground(style: String, colors: SettingsBackgroundColors): Modifier {
    if (style != "gradient" && style != "blur") return background(colors.base)
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
        val blurBrushes = if (style == "blur") SettingsBlurSpots.map { spot ->
            Brush.radialGradient(
                *SettingsBlurStops.map { (offset, opacity) ->
                    offset to (if (spot.colorIndex == 0) colors.first else colors.second).copy(alpha = opacity)
                }.toTypedArray(),
                center = Offset(spot.x * referenceWidth, spot.y * referenceHeight),
                radius = spot.radius * referenceWidth
            )
        } else emptyList()
        onDrawBehind {
            withTransform({ scale(scaleX, scaleY, pivot = Offset.Zero) }) {
                drawRect(colors.base, size = referenceSize)
                if (gradient != null) drawRect(gradient, size = referenceSize)
                else blurBrushes.forEach { drawRect(it, size = referenceSize) }
            }
        }
    }
}

@Composable
internal fun SettingsBackgroundOptions(
    selectedStyle: String, colors: SettingsBackgroundColors, panel: SettingsSectionColors,
    fontFamily: FontFamily, onStyleSelected: (String) -> Unit
) {
    val context = LocalContext.current
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf("solid" to R.string.settings_background_solid,
            "gradient" to R.string.settings_background_gradient,
            "blur" to R.string.settings_background_blur).forEach { (style, labelRes) ->
            val isSelected = style == selectedStyle
            Surface(
                onClick = UiSoundPlayer.actionHandler(context, UiActionSound.Color) { onStyleSelected(style) },
                modifier = Modifier.weight(1f).semantics { selected = isSelected; role = Role.RadioButton },
                color = panel.background, shape = RoundedCornerShape(14.dp),
                border = BorderStroke(if (isSelected) 2.dp else 1.dp,
                    panel.graphic.copy(alpha = if (isSelected) 1f else 0.30f)),
                tonalElevation = 0.dp, shadowElevation = 0.dp
            ) {
                Column(Modifier.padding(7.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(Modifier.fillMaxWidth().height(64.dp).clip(RoundedCornerShape(9.dp)).settingsBackground(style, colors)) {
                        if (isSelected) Icon(Icons.Default.Check, contentDescription = null, tint = panel.text,
                            modifier = Modifier.align(Alignment.TopEnd).padding(4.dp).size(18.dp))
                    }
                    Text(stringResource(labelRes), color = panel.text, fontFamily = fontFamily,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 12.sp, textAlign = TextAlign.Center, maxLines = 2,
                        modifier = Modifier.fillMaxWidth().padding(top = 7.dp))
                }
            }
        }
    }
}

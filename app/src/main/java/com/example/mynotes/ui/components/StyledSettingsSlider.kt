package com.example.mynotes.ui.components

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.mynotes.ui.sound.UiSound
import com.example.mynotes.ui.sound.UiSoundPlayer
import com.example.mynotes.ui.theme.automaticUiTextColor

internal val LocalSliderStyle = compositionLocalOf { "capsule" }

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun StyledSettingsSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: (() -> Unit)? = null,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    activeColor: Color = MaterialTheme.colorScheme.primary,
    inactiveColor: Color = MaterialTheme.colorScheme.outlineVariant,
    valueLabel: String? = null,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    playTick: Boolean = false
) {
    val style = LocalSliderStyle.current
    val context = LocalContext.current
    val trackColor = if (enabled) activeColor else activeColor.copy(alpha = activeColor.alpha * 0.38f)
    val restColor = if (enabled) inactiveColor else inactiveColor.copy(alpha = inactiveColor.alpha * 0.38f)
    val ringColor = if (enabled) Color.White else Color.White.copy(alpha = 0.38f)
    val floatingTextPaint = if (style == "floating" && !valueLabel.isNullOrBlank()) {
        remember(trackColor) {
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = automaticUiTextColor(trackColor).toArgb()
                textAlign = Paint.Align.CENTER
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
        }
    } else null

    val sliderHeight = if (style == "floating") 58.dp else 44.dp
    val sliderModifier = modifier.fillMaxWidth()

    Slider(
        value = value,
        onValueChange = { newValue ->
            if (playTick) {
                UiSoundPlayer.playThrottled(context, UiSound.SliderTick, minimumIntervalMs = 48L)
            }
            onValueChange(newValue)
        },
        modifier = if (style == "capsule") sliderModifier.height(sliderHeight) else sliderModifier,
        enabled = enabled,
        onValueChangeFinished = onValueChangeFinished,
        steps = steps,
        thumb = {
            if (style == "capsule") {
                // El ancho reserva el margen de la pista y el indicador conserva su tamaño durante el gesto.
                Canvas(Modifier.size(width = 28.dp, height = 20.dp)) {
                    drawCircle(ringColor, 10.dp.toPx())
                    drawCircle(trackColor, 7.5.dp.toPx())
                }
            } else {
                Spacer(Modifier.width(if (style == "glow") 34.dp else 28.dp))
            }
        },
        track = { slider ->
            Canvas(Modifier.fillMaxWidth().height(sliderHeight)) {
                if (size.width <= 0f) return@Canvas
                val fraction = slider.coercedValueAsFraction
                val centerY = center.y + if (style == "floating") 6.dp.toPx() else 0f
                val thumbX = size.width * fraction
                val rtl = layoutDirection == LayoutDirection.Rtl
                scale(scaleX = if (rtl) -1f else 1f, scaleY = 1f) {
                    drawSliderStyle(style, centerY, thumbX, fraction, steps, trackColor, restColor)
                }
                if (floatingTextPaint != null && valueLabel != null) {
                    val labelX = if (rtl) size.width - thumbX else thumbX
                    drawFloatingValue(valueLabel, floatingTextPaint, labelX, trackColor)
                }
            }
        },
        valueRange = valueRange
    )
}

private fun DrawScope.drawSliderStyle(
    style: String, centerY: Float, thumbX: Float, fraction: Float, steps: Int,
    activeColor: Color, inactiveColor: Color
) {
    val thumb = Offset(thumbX, centerY)
    when (style) {
        "capsule" -> drawTrack(centerY, thumbX, 14.dp.toPx(), activeColor, inactiveColor)
        "glow" -> {
            drawTrack(centerY, thumbX, 5.dp.toPx(), activeColor, inactiveColor)
            drawCircle(activeColor.copy(alpha = activeColor.alpha * 0.12f), 17.dp.toPx(), thumb)
            drawCircle(activeColor.copy(alpha = activeColor.alpha * 0.24f), 12.dp.toPx(), thumb)
            drawCircle(activeColor, 6.5.dp.toPx(), thumb)
        }
        "segmented" -> {
            val count = if (steps > 0) steps + 2 else 8
            val gap = minOf(4.dp.toPx(), size.width / count / 2f)
            val width = (size.width - gap * (count - 1)) / count
            val height = 8.dp.toPx()
            repeat(count) { index ->
                val color = if (index.toFloat() / (count - 1) <= fraction + 0.0001f) {
                    activeColor
                } else {
                    inactiveColor
                }
                drawRoundRect(
                    color = color,
                    topLeft = Offset(index * (width + gap), centerY - height / 2f),
                    size = Size(width, height),
                    cornerRadius = CornerRadius(height)
                )
            }
            drawCircle(activeColor, 6.dp.toPx(), thumb)
        }
        "dots" -> {
            val count = if (steps > 0) steps + 2 else 9
            repeat(count) { index ->
                val point = index.toFloat() / (count - 1)
                val color = if (point <= fraction + 0.0001f) activeColor else inactiveColor
                val radius = if (kotlin.math.abs(point - fraction) < 0.08f) 6.dp else 3.2.dp
                drawCircle(color, radius.toPx(), Offset(size.width * point, centerY))
            }
        }
        "gradient" -> {
            val height = 7.dp.toPx()
            drawRoundRect(Brush.horizontalGradient(listOf(inactiveColor, activeColor), endX = size.width),
                Offset(0f, centerY - height / 2f), Size(size.width, height), CornerRadius(height))
            drawCircle(activeColor, 6.5.dp.toPx(), thumb)
        }
        "line_pill" -> {
            drawTrack(centerY, thumbX, 3.dp.toPx(), activeColor, inactiveColor)
            drawRoundRect(activeColor, Offset(thumbX - 4.dp.toPx(), centerY - 11.dp.toPx()),
                Size(8.dp.toPx(), 22.dp.toPx()), CornerRadius(8.dp.toPx()))
        }
        else -> {
            val height = when (style) {
                "glass" -> 15.dp
                "neumorphic" -> 12.dp
                else -> 4.dp
            }.toPx()
            if (style == "neumorphic") {
                drawRoundRect(Color.Black.copy(alpha = activeColor.alpha * 0.10f), Offset(1.dp.toPx(), centerY - height / 2f + 2.dp.toPx()),
                    Size(size.width, height), CornerRadius(height))
            }
            drawTrack(centerY, thumbX, height, activeColor, inactiveColor)
            drawCircle(activeColor, 6.5.dp.toPx(), thumb)
        }
    }
}

private fun DrawScope.drawFloatingValue(label: String, paint: Paint, thumbX: Float, color: Color) {
    val width = minOf(56.dp.toPx(), size.width)
    val height = 24.dp.toPx()
    val x = (thumbX - width / 2f).coerceIn(0f, size.width - width)
    val y = 2.dp.toPx()
    drawRoundRect(color, Offset(x, y), Size(width, height), CornerRadius(height / 2f))
    paint.textSize = 11.dp.toPx()
    drawContext.canvas.nativeCanvas.drawText(label, x + width / 2f, y + height * 0.69f, paint)
}

private fun DrawScope.drawTrack(centerY: Float, activeWidth: Float, height: Float, activeColor: Color, inactiveColor: Color) {
    val topLeft = Offset(0f, centerY - height / 2f)
    val radius = CornerRadius(height)
    drawRoundRect(inactiveColor, topLeft, Size(size.width, height), radius)
    if (activeWidth > 0f) {
        drawRoundRect(activeColor, topLeft, Size(activeWidth, height), radius)
    }
}

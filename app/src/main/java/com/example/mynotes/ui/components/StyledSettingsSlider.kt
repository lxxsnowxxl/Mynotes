package com.example.mynotes.ui.components

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.mynotes.ui.sound.UiSound
import com.example.mynotes.ui.sound.UiSoundPlayer

/*
 * Slider visual personalizado.
 *
 * Conserva los 10 diseños que ya habíamos agregado al proyecto,
 * pero la interacción real sigue siendo un Slider de Material
 * transparente. Así mantenemos gestos, accesibilidad y pasos.
 */
@Composable
fun StyledSettingsSlider(value: Float, onValueChange: (Float) -> Unit, onValueChangeFinished: (() -> Unit)? = null,
    valueRange: ClosedFloatingPointRange<Float>, steps: Int = 0, activeColor: Color, inactiveColor: Color, style: String,
    valueLabel: String? = null) {
    val context = LocalContext.current
    val min = valueRange.start
    val max = valueRange.endInclusive
    /*
     * Reutilizamos el Paint del estilo Floating. Antes se creaba un
     * android.graphics.Paint nuevo en cada frame mientras el usuario
     * arrastraba el slider.
     */
    val floatingTextPaint = remember {
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.White.toArgb()
                textAlign = Paint.Align.CENTER
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
        }
    val fraction = if (max >
            min) {
            ((value - min) / (max - min)).coerceIn(0f, 1f)
        } else {
            0f
        }
    Box(modifier = Modifier.fillMaxWidth().height(if (style == "floating") {
                        58.dp
                    } else {
                        44.dp
                    })) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val inset = 14.dp.toPx()
            val topExtra = if (style == "floating") {
                    12.dp.toPx()
                } else {
                    0f
                }
            val centerY = (size.height + topExtra) / 2f
            val trackWidth = (size.width - inset * 2f).coerceAtLeast(0f)
            val startX = inset
            val activeWidth = trackWidth * fraction
            val thumbX = startX + activeWidth
            when (style) {
                /*
                 * 1. Minimal.
                 */
                "minimal" -> {
                    drawTrack(startX = startX, centerY = centerY, trackWidth = trackWidth, activeWidth = activeWidth, height = 4.dp.toPx(),
                        activeColor = activeColor, inactiveColor = inactiveColor)
                    drawCircle(color = activeColor, radius = 6.5.dp.toPx(), center = Offset(thumbX, centerY))
                }
                /*
                 * 2. Capsule.
                 */
                "capsule" -> {
                    drawTrack(startX = startX, centerY = centerY, trackWidth = trackWidth, activeWidth = activeWidth, height = 14.dp.toPx(),
                        activeColor = activeColor, inactiveColor = inactiveColor)
                    drawCircle(color = Color.White, radius = 9.dp.toPx(), center = Offset(thumbX, centerY))
                    drawCircle(color = activeColor, radius = 6.dp.toPx(), center = Offset(thumbX, centerY))
                }
                /*
                 * 3. Glow.
                 */
                "glow" -> {
                    drawTrack(startX = startX, centerY = centerY, trackWidth = trackWidth, activeWidth = activeWidth, height = 5.dp.toPx(),
                        activeColor = activeColor, inactiveColor = inactiveColor)
                    drawCircle(color = activeColor.copy(alpha = 0.12f), radius = 17.dp.toPx(), center = Offset(thumbX, centerY))
                    drawCircle(color = activeColor.copy(alpha = 0.24f), radius = 12.dp.toPx(), center = Offset(thumbX, centerY))
                    drawCircle(color = activeColor, radius = 6.5.dp.toPx(), center = Offset(thumbX, centerY))
                }
                /*
                 * 4. Glass.
                 */
                "glass" -> {
                    val h = 15.dp.toPx()
                    drawRoundRect(color = inactiveColor.copy(alpha = 0.34f), topLeft = Offset(startX, centerY - h / 2f), size = Size(
                                trackWidth, h), cornerRadius = CornerRadius(h, h))
                    if (activeWidth >
                        0f) {
                        drawRoundRect(color = activeColor.copy(alpha = 0.72f), topLeft = Offset(startX, centerY - h / 2f), size = Size(
                                    activeWidth, h), cornerRadius = CornerRadius(h, h))
                    }
                    drawCircle(color = Color.White.copy(alpha = 0.88f), radius = 8.5.dp.toPx(), center = Offset(thumbX, centerY))
                    drawCircle(color = activeColor, radius = 5.dp.toPx(), center = Offset(thumbX, centerY))
                }
                /*
                 * 5. Segmented.
                 */
                "segmented" -> {
                    val count = if (steps >
                            0) {
                            steps + 2
                        } else {
                            8
                        }
                    val gap = 4.dp.toPx()
                    val segmentWidth = (trackWidth - gap * (count - 1)) / count
                    repeat(count) {
                            index ->
                        val segmentFraction = if (count <= 1) {
                                0f
                            } else {
                                index.toFloat() / (count - 1)
                            }
                        val selected = segmentFraction <= fraction + 0.0001f
                        drawRoundRect(color = if (selected) {
                                    activeColor
                                } else {
                                    inactiveColor.copy(alpha = 0.55f)
                                }, topLeft = Offset(startX + index * (segmentWidth + gap), centerY - 4.dp.toPx()), size = Size(
                                    segmentWidth, 8.dp.toPx()), cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx()))
                    }
                    drawCircle(color = activeColor, radius = 6.dp.toPx(), center = Offset(thumbX, centerY))
                }
                /*
                 * 6. Dots.
                 */
                "dots" -> {
                    val count = if (steps >
                            0) {
                            steps + 2
                        } else {
                            9
                        }
                    repeat(count) {
                            index ->
                        val dotFraction = if (count <= 1) {
                                0f
                            } else {
                                index.toFloat() / (count - 1)
                            }
                        val x = startX + trackWidth * dotFraction
                        drawCircle(color = if (dotFraction <= fraction + 0.0001f) {
                                    activeColor
                                } else {
                                    inactiveColor.copy(alpha = 0.60f)
                                }, radius = if (kotlin.math.abs(dotFraction - fraction) <
                                    0.08f) {
                                    6.dp.toPx()
                                } else {
                                    3.2.dp.toPx()
                                }, center = Offset(x, centerY))
                    }
                }
                /*
                 * 7. Gradient.
                 *
                 * El degradado utiliza colores del propio tema,
                 * no el color de fondo de la paleta. Esto evita
                 * que el slider de tono se camufle con la pantalla.
                 */
                "gradient" -> {
                    val h = 7.dp.toPx()
                    drawRoundRect(brush = Brush.horizontalGradient(colors = listOf(inactiveColor, activeColor), startX = startX, endX =
                                    startX + trackWidth), topLeft = Offset(startX, centerY - h / 2f), size = Size(trackWidth, h),
                        cornerRadius = CornerRadius(h, h))
                    drawCircle(color = Color.White, radius = 8.dp.toPx(), center = Offset(thumbX, centerY))
                    drawCircle(color = activeColor, radius = 5.5.dp.toPx(), center = Offset(thumbX, centerY))
                }
                /*
                 * 8. Neumorphic.
                 */
                "neumorphic" -> {
                    val h = 12.dp.toPx()
                    drawRoundRect(color = Color.Black.copy(alpha = 0.10f), topLeft = Offset(startX + 1.dp.toPx(), centerY - h / 2f +
                                    2.dp.toPx()), size = Size(trackWidth, h), cornerRadius = CornerRadius(h, h))
                    drawRoundRect(color = inactiveColor.copy(alpha = 0.55f), topLeft = Offset(startX, centerY - h / 2f), size = Size(
                                trackWidth, h), cornerRadius = CornerRadius(h, h))
                    if (activeWidth >
                        0f) {
                        drawRoundRect(color = activeColor, topLeft = Offset(startX, centerY - h / 2f), size = Size(activeWidth, h),
                            cornerRadius = CornerRadius(h, h))
                    }
                    drawCircle(color = Color.Black.copy(alpha = 0.12f), radius = 10.dp.toPx(), center = Offset(thumbX + 1.dp.toPx(),
                                centerY + 2.dp.toPx()))
                    drawCircle(color = Color.White, radius = 9.dp.toPx(), center = Offset(thumbX, centerY))
                }
                /*
                 * 9. Line + pill.
                 */
                "line_pill" -> {
                    drawTrack(startX = startX, centerY = centerY, trackWidth = trackWidth, activeWidth = activeWidth, height = 3.dp.toPx(),
                        activeColor = activeColor, inactiveColor = inactiveColor)
                    drawRoundRect(color = activeColor, topLeft = Offset(thumbX - 4.dp.toPx(), centerY - 11.dp.toPx()), size = Size(
                                8.dp.toPx(), 22.dp.toPx()), cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx()))
                }
                /*
                 * 10. Floating.
                 */
                "floating" -> {
                    drawTrack(startX = startX, centerY = centerY, trackWidth = trackWidth, activeWidth = activeWidth, height = 4.dp.toPx(),
                        activeColor = activeColor, inactiveColor = inactiveColor)
                    drawCircle(color = activeColor, radius = 6.5.dp.toPx(), center = Offset(thumbX, centerY))
                    if (!valueLabel.isNullOrBlank()) {
                        val bubbleWidth = 56.dp.toPx()
                        val bubbleHeight = 24.dp.toPx()
                        val bubbleX = (thumbX - bubbleWidth / 2f).coerceIn(0f, size.width - bubbleWidth)
                        val bubbleY = 2.dp.toPx()
                        drawRoundRect(color = activeColor, topLeft = Offset(bubbleX, bubbleY), size = Size(bubbleWidth, bubbleHeight),
                            cornerRadius = CornerRadius(12.dp.toPx(), 12.dp.toPx()))
                        drawContext.canvas.nativeCanvas.drawText(valueLabel, bubbleX + bubbleWidth / 2f, bubbleY + bubbleHeight * 0.69f,
                                floatingTextPaint.apply {
                                    textSize = 11.dp.toPx()
                                })
                    }
                }
                else -> {
                    drawTrack(startX = startX, centerY = centerY, trackWidth = trackWidth, activeWidth = activeWidth, height = 4.dp.toPx(),
                        activeColor = activeColor, inactiveColor = inactiveColor)
                    drawCircle(color = activeColor, radius = 6.5.dp.toPx(), center = Offset(thumbX, centerY))
                }
            }
        }
        /*
         * Slider funcional transparente.
         */
        Slider(value = value,
            onValueChange = {
                    newValue ->
                UiSoundPlayer.playThrottled(context = context, sound = UiSound.SliderTick, minimumIntervalMs = 48L)
                onValueChange(newValue)
            },
            onValueChangeFinished = onValueChangeFinished,
            valueRange = valueRange,
            steps = steps,
            modifier = Modifier.fillMaxSize(),
            colors = SliderDefaults.colors(thumbColor = Color.Transparent,
                    activeTrackColor = Color.Transparent,
                    inactiveTrackColor = Color.Transparent,
                    activeTickColor = Color.Transparent,
                    inactiveTickColor = Color.Transparent))
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawTrack(startX: Float, centerY: Float, trackWidth: Float, activeWidth: Float,
    height: Float, activeColor: Color, inactiveColor: Color) {
    drawRoundRect(color = inactiveColor.copy(alpha = 0.60f),
        topLeft = Offset(startX, centerY - height / 2f),
        size = Size(trackWidth, height),
        cornerRadius = CornerRadius(height, height))
    if (activeWidth >
        0f) {
        drawRoundRect(color = activeColor,
            topLeft = Offset(startX, centerY - height / 2f),
            size = Size(activeWidth, height),
            cornerRadius = CornerRadius(height, height))
    }
}

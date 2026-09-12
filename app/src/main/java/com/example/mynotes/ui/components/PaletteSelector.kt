package com.example.mynotes.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.mynotes.ui.theme.MyNotesPalette
import com.example.mynotes.ui.theme.resolveUiTextColor
import com.example.mynotes.ui.theme.resolveUiGraphicColor
import com.example.mynotes.ui.motion.AppMotion
import com.example.mynotes.ui.sound.UiActionSound
import com.example.mynotes.ui.sound.UiSoundPlayer

/*
 * El selector reproduce la idea del mockup:
 *
 * - dos tarjetas por fila;
 * - cada tarjeta contiene exactamente cuatro tonos;
 * - cada círculo se puede pulsar de manera individual;
 * - el tono activo muestra una marca de verificación.
 */
@Composable
fun PaletteSelector(palettes: List<MyNotesPalette>, selectedPaletteKey: String, selectedToneIndex: Int, onPaletteSelected: (String) -> Unit,
    onToneSelected: (paletteKey: String, toneIndex: Int) -> Unit, animationsEnabled: Boolean = true, animationSpeed: Float = 1f,
    textColorMode: String = "auto", fontFamily: FontFamily = FontFamily.Default) {
    val paletteRows = remember(palettes) {
            palettes.chunked(2)
        }
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        paletteRows.forEach {
                    rowPalettes ->
                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    rowPalettes.forEach {
                                palette ->
                            PaletteCard(modifier = Modifier.weight(1f),
                                palette = palette,
                                selected = palette.key == selectedPaletteKey,
                                selectedToneIndex = selectedToneIndex,
                                onPaletteSelected = onPaletteSelected,
                                onToneSelected = onToneSelected,
                                animationsEnabled = animationsEnabled,
                                animationSpeed = animationSpeed,
                                textColorMode = textColorMode,
                                fontFamily = fontFamily)
                        }
                    if (rowPalettes.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
    }
}

@Composable
private fun PaletteCard(modifier: Modifier, palette: MyNotesPalette, selected: Boolean, selectedToneIndex: Int,
    onPaletteSelected: (String) -> Unit, onToneSelected: (paletteKey: String, toneIndex: Int) -> Unit, animationsEnabled: Boolean,
    animationSpeed: Float, textColorMode: String, fontFamily: FontFamily) {
    val context = LocalContext.current
    val shape = RoundedCornerShape(16.dp)
    val cardBackground = MaterialTheme.colorScheme.surfaceContainerLow
    val cardTextColor = resolveUiTextColor(value = textColorMode, background = cardBackground)
    val cardGraphicColor = resolveUiGraphicColor(value = textColorMode, background = cardBackground)
    val motionDuration = AppMotion.duration(AppMotion.FAST, animationsEnabled, animationSpeed)
    val cardInteractionSource = remember {
            MutableInteractionSource()
        }
    val animatedBorderWidth by
        animateDpAsState(targetValue = if (selected) {
                    1.8.dp
                } else {
                    1.dp
                }, animationSpec = tween(durationMillis = motionDuration), label = "paletteBorder")
    Surface(modifier = modifier.clickable(interactionSource = cardInteractionSource, indication = null, onClick = {
                        UiSoundPlayer.playAction(context = context, action = UiActionSound.Theme)
                        onPaletteSelected(palette.key)
                    }),
        shape = shape,
        color = cardBackground,
        contentColor = cardTextColor,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        border = BorderStroke(width = animatedBorderWidth,
                color = cardGraphicColor)) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp,
                        vertical = 11.dp)) {
            Text(text = stringResource(palette.labelRes),
                style = MaterialTheme.typography.labelLarge,
                fontFamily = fontFamily,
                fontWeight = FontWeight.SemiBold,
                color = cardTextColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis)
            Row(modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically) {
                /*
                 * Cada tono recibe exactamente el mismo peso.
                 *
                 * Resultado:
                 * - los cuatro círculos siempre tienen el mismo diámetro;
                 * - aprovechan todo el ancho de la tarjeta;
                 * - no queda un hueco grande al final;
                 * - todas las paletas mantienen la misma proporción.
                 */
                palette.tones.forEachIndexed {
                            index, tone ->
                        Box(modifier = Modifier.weight(1f).aspectRatio(1f),
                            contentAlignment = Alignment.Center) {
                            PaletteToneCircle(modifier = Modifier.fillMaxWidth(),
                                color = tone,
                                selected = selected && selectedToneIndex == index,
                                onClick = {
                                    UiSoundPlayer.playAction(context = context, action = UiActionSound.Color)
                                    onToneSelected(palette.key, index)
                                },
                                animationsEnabled = animationsEnabled,
                                animationSpeed = animationSpeed)
                        }
                    }
            }
        }
    }
}

@Composable
private fun PaletteToneCircle(modifier: Modifier = Modifier, color: Color, selected: Boolean, onClick: () -> Unit,
    animationsEnabled: Boolean, animationSpeed: Float) {
    val checkColor = if (color.luminance() >
            0.48f) {
            Color.Black
        } else {
            Color.White
        }
    val toneInteractionSource = remember {
            MutableInteractionSource()
        }
    val motionDuration = AppMotion.duration(AppMotion.FAST, animationsEnabled, animationSpeed)
    Box(modifier = modifier.aspectRatio(1f).clip(CircleShape).background(color).then(if (selected) {
                        Modifier.border(width = 2.dp,
                                color = MaterialTheme.colorScheme.onSurface,
                                shape = CircleShape)
                    } else {
                        Modifier
                    }).clickable(interactionSource = toneInteractionSource, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center) {
        AnimatedVisibility(visible = selected, enter = fadeIn(animationSpec = tween(durationMillis = motionDuration)) + scaleIn(
                        animationSpec = tween(durationMillis = motionDuration), initialScale = 0.55f), exit = fadeOut(animationSpec = tween(
                            durationMillis = motionDuration)) + scaleOut(animationSpec = tween(durationMillis = motionDuration),
                        targetScale = 0.55f)) {
            Icon(imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = checkColor,
                modifier = Modifier.fillMaxWidth(0.52f).aspectRatio(1f))
        }
    }
}

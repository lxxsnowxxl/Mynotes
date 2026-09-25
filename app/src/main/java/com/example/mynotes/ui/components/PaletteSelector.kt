package com.example.mynotes.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.mynotes.ui.motion.AppMotion
import com.example.mynotes.ui.sound.UiActionSound
import com.example.mynotes.ui.sound.UiSoundPlayer
import com.example.mynotes.ui.theme.MyNotesPalette
import com.example.mynotes.ui.theme.resolveUiGraphicColor
import com.example.mynotes.ui.theme.resolveUiTextColor

/*
 * Selector de paletas.
 *
 * Settings usa PaletteSelectorRow directamente dentro de su LazyColumn para
 * que las 46 paletas no se compongan de golpe. PaletteSelector se conserva
 * como versión no-lazy reutilizable para cualquier pantalla que la necesite.
 */
@Composable
fun PaletteSelector(
    palettes: List<MyNotesPalette>,
    selectedPaletteKey: String,
    selectedToneIndex: Int,
    onPaletteSelected: (String) -> Unit,
    onToneSelected: (paletteKey: String, toneIndex: Int) -> Unit,
    animationsEnabled: Boolean = true,
    animationSpeed: Float = 1f,
    textColorMode: String = "auto",
    fontFamily: FontFamily = FontFamily.Default
) {
    val paletteRows = remember(palettes) { palettes.chunked(2) }
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        paletteRows.forEach { rowPalettes ->
            PaletteSelectorRow(
                rowPalettes = rowPalettes,
                selectedPaletteKey = selectedPaletteKey,
                selectedToneIndex = selectedToneIndex,
                onPaletteSelected = onPaletteSelected,
                onToneSelected = onToneSelected,
                animationsEnabled = animationsEnabled,
                animationSpeed = animationSpeed,
                textColorMode = textColorMode,
                fontFamily = fontFamily
            )
        }
    }
}

@Composable
internal fun PaletteSelectorRow(
    rowPalettes: List<MyNotesPalette>,
    selectedPaletteKey: String,
    selectedToneIndex: Int,
    onPaletteSelected: (String) -> Unit,
    onToneSelected: (paletteKey: String, toneIndex: Int) -> Unit,
    animationsEnabled: Boolean = true,
    animationSpeed: Float = 1f,
    textColorMode: String = "auto",
    fontFamily: FontFamily = FontFamily.Default
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        rowPalettes.forEach { palette ->
            PaletteCard(
                modifier = Modifier.weight(1f),
                palette = palette,
                selected = palette.key == selectedPaletteKey,
                selectedToneIndex = selectedToneIndex,
                onPaletteSelected = onPaletteSelected,
                onToneSelected = onToneSelected,
                animationsEnabled = animationsEnabled,
                animationSpeed = animationSpeed,
                textColorMode = textColorMode,
                fontFamily = fontFamily
            )
        }
        if (rowPalettes.size == 1) {
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun PaletteCard(
    modifier: Modifier,
    palette: MyNotesPalette,
    selected: Boolean,
    selectedToneIndex: Int,
    onPaletteSelected: (String) -> Unit,
    onToneSelected: (paletteKey: String, toneIndex: Int) -> Unit,
    animationsEnabled: Boolean,
    animationSpeed: Float,
    textColorMode: String,
    fontFamily: FontFamily
) {
    val context = LocalContext.current
    val shape = RoundedCornerShape(16.dp)
    val cardBackground = MaterialTheme.colorScheme.surfaceContainerLow
    val cardTextColor = remember(textColorMode, cardBackground) {
        resolveUiTextColor(value = textColorMode, background = cardBackground)
    }
    // La animación del borde no cambia el contraste de la tarjeta.
    val cardGraphicColor = remember(textColorMode, cardBackground) {
        resolveUiGraphicColor(value = textColorMode, background = cardBackground)
    }
    val motionDuration = AppMotion.duration(AppMotion.FAST, animationsEnabled, animationSpeed)
    val cardInteractionSource = remember { MutableInteractionSource() }
    val animatedBorderWidth by animateDpAsState(
        targetValue = if (selected) 1.8.dp else 1.dp,
        animationSpec = tween(durationMillis = motionDuration),
        label = "paletteBorder"
    )

    Surface(
        modifier = modifier.clickable(
            interactionSource = cardInteractionSource,
            indication = null,
            onClick = {
                UiSoundPlayer.playAction(context = context, action = UiActionSound.Theme)
                onPaletteSelected(palette.key)
            }
        ),
        shape = shape,
        color = cardBackground,
        contentColor = cardTextColor,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        border = BorderStroke(width = animatedBorderWidth, color = cardGraphicColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 11.dp)
        ) {
            Text(
                text = stringResource(palette.labelRes),
                style = MaterialTheme.typography.labelLarge,
                fontFamily = fontFamily,
                fontWeight = FontWeight.SemiBold,
                color = cardTextColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                palette.tones.forEachIndexed { index, tone ->
                    PaletteToneCircle(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f),
                        color = tone,
                        selected = selected && selectedToneIndex == index,
                        frameColor = cardBackground,
                        indicatorColor = cardGraphicColor,
                        onClick = {
                            UiSoundPlayer.playAction(context = context, action = UiActionSound.Color)
                            onToneSelected(palette.key, index)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun PaletteToneCircle(
    modifier: Modifier = Modifier,
    color: Color,
    selected: Boolean,
    frameColor: Color,
    indicatorColor: Color,
    onClick: () -> Unit
) {
    val checkColor = remember(color) {
        if (color.luminance() > 0.48f) Color.Black else Color.White
    }
    val toneInteractionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .clickable(
                interactionSource = toneInteractionSource,
                indication = null,
                onClick = onClick
            )
            /*
             * Antes cada tono utilizaba dos Box + dos clip(CircleShape) +
             * varios backgrounds. Con 184 tonos eso generaba muchas capas al
             * entrar a la sección. El dibujo directo conserva la geometría y
             * colores, pero evita esas capas intermedias.
             */
            .drawWithCache {
                val borderWidth = 2.dp.toPx()
                val selectedInset = 4.dp.toPx()
                val radius = size.minDimension / 2f
                val borderRadius = (radius - borderWidth / 2f).coerceAtLeast(0f)
                val toneRadius = (radius - selectedInset).coerceAtLeast(0f)
                val borderStroke = Stroke(width = borderWidth)
                onDrawBehind {
                    if (selected) {
                        drawCircle(color = frameColor, radius = radius)
                        drawCircle(
                            color = indicatorColor,
                            radius = borderRadius,
                            style = borderStroke
                        )
                        drawCircle(
                            color = color,
                            radius = toneRadius
                        )
                    } else {
                        drawCircle(color = color, radius = radius)
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        if (selected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = checkColor,
                modifier = Modifier
                    .fillMaxWidth(0.48f)
                    .aspectRatio(1f)
            )
        }
    }
}

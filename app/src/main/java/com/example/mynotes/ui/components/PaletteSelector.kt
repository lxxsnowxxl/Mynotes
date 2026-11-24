package com.example.mynotes.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.mynotes.ui.sound.UiActionSound
import com.example.mynotes.ui.sound.UiSoundPlayer
import com.example.mynotes.ui.theme.MyNotesPalette
import com.example.mynotes.ui.theme.PaletteCatalog
import com.example.mynotes.ui.theme.settingsSectionColors
import com.example.mynotes.ui.theme.ensureUiContrast
import com.example.mynotes.ui.theme.automaticUiTextColor

private val PaletteItemWidth = 132.dp
private val ToneDiameter = 48.dp
private val ToneClusterSize = 100.dp
private val PaletteSelectionCircleSize = 120.dp
private val TonePositions = listOf(
    26.dp to 0.dp,
    0.dp to 27.dp,
    52.dp to 27.dp,
    14.dp to 52.dp,
    40.dp to 52.dp
).also { positions ->
    check(positions.size == PaletteCatalog.SELECTABLE_TONE_COUNT) {
        "Tone selector geometry must match PaletteCatalog.SELECTABLE_TONE_COUNT"
    }
}

/**
 * Selector horizontal de color con cinco tonos en una composición tipo flor.
 * Todas las familias comparten la misma geometría: un tono arriba, dos al
 * centro y dos abajo, con solapamiento uniforme y fácil acceso táctil.
 *
 * Las tonalidades se muestran sin contornos ni elevación.
 * La palomita de alto contraste identifica el tono seleccionado.
 */
@Composable
internal fun PaletteSelectorRow(
    palettes: List<MyNotesPalette>,
    selectedPaletteKey: String,
    selectedToneIndex: Int,
    onToneSelected: (paletteKey: String, toneIndex: Int) -> Unit,
    animationsEnabled: Boolean = true,
    animationSpeed: Float = 1f,
    textColorMode: String = "auto",
    fontFamily: FontFamily = FontFamily.Default
) {
    val initialIndex = remember(palettes, selectedPaletteKey) {
        palettes.indexOfFirst { it.key == selectedPaletteKey }.coerceAtLeast(0)
    }
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)
    var alignedPalette by rememberSaveable { mutableStateOf(selectedPaletteKey) }

    LaunchedEffect(selectedPaletteKey, palettes) {
        // Re-entering the vertical viewport restores the user's horizontal browsing position.
        if (alignedPalette == selectedPaletteKey) return@LaunchedEffect
        val selectedIndex = palettes.indexOfFirst { it.key == selectedPaletteKey }
        if (selectedIndex >= 0 && listState.layoutInfo.visibleItemsInfo.none { it.index == selectedIndex }) {
            if (animationsEnabled) listState.animateScrollToItem(selectedIndex)
            else listState.scrollToItem(selectedIndex)
        }
        alignedPalette = selectedPaletteKey
    }

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        state = listState,
        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items(
            items = palettes,
            key = { it.key }
        ) { palette ->
            PaletteStackItem(
                palette = palette,
                selected = palette.key == selectedPaletteKey,
                selectedToneIndex = selectedToneIndex,
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
internal fun PaletteStackItem(
    palette: MyNotesPalette,
    selected: Boolean,
    selectedToneIndex: Int,
    onToneSelected: (paletteKey: String, toneIndex: Int) -> Unit,
    animationsEnabled: Boolean,
    animationSpeed: Float,
    textColorMode: String,
    fontFamily: FontFamily,
    toneDescriptions: List<String>? = null,
    playToneSound: Boolean = true,
    automaticToneCheckColor: Boolean = false
) {
    val cardBackground = MaterialTheme.colorScheme.surfaceContainerLow
    val labelInk = remember(textColorMode, cardBackground) {
        settingsSectionColors(cardBackground, textColorMode)
    }
    val cardTextColor = labelInk.text

    Column(
        modifier = Modifier
            .width(PaletteItemWidth)
            .padding(horizontal = 2.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier.size(PaletteSelectionCircleSize),
            shape = CircleShape,
            color = Color.Transparent,
            tonalElevation = 0.dp,
            shadowElevation = 0.dp
        ) {
            Box(
                modifier = Modifier.size(PaletteSelectionCircleSize),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier.size(ToneClusterSize),
                    contentAlignment = Alignment.TopStart
                ) {
                    palette.tones.forEachIndexed { toneIndex, toneColor ->
                        val (offsetX, offsetY) = TonePositions[toneIndex]
                        Box(
                            modifier = Modifier.offset(x = offsetX, y = offsetY)
                        ) {
                            PaletteTone(
                                color = toneColor,
                                selected = selected && selectedToneIndex == toneIndex,
                                description = toneDescriptions?.getOrNull(toneIndex),
                                playToneSound = playToneSound,
                                automaticCheckColor = automaticToneCheckColor,
                                onClick = { onToneSelected(palette.key, toneIndex) }
                            )
                        }
                    }
                }
            }
        }

        Text(
            text = stringResource(palette.labelRes),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, start = 2.dp, end = 2.dp),
            style = MaterialTheme.typography.labelMedium,
            fontFamily = fontFamily,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold,
            color = cardTextColor,
            textAlign = TextAlign.Center,
            maxLines = 2
        )
    }
}

@Composable
private fun PaletteTone(
    color: Color,
    selected: Boolean,
    description: String? = null,
    playToneSound: Boolean = true,
    automaticCheckColor: Boolean = false,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val interactionSource = remember { MutableInteractionSource() }
    // El contraste depende del tono real; respeta el estilo anterior cuando es legible.
    val checkColor = remember(color, automaticCheckColor) {
        val preferred = if (automaticCheckColor) automaticUiTextColor(color)
            else if (color.luminance() > 0.48f) Color.Black else Color.White
        ensureUiContrast(preferred, color, minimumContrast = 4.5f)
    }

    Surface(
        modifier = Modifier.size(ToneDiameter)
            .semantics {
                if (description != null) contentDescription = description
                this.selected = selected
                role = Role.RadioButton
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = if (playToneSound) UiSoundPlayer.actionHandler(context, UiActionSound.Color, onClick) else UiSoundPlayer.actionHandler(context, UiActionSound.Select, onClick)
            ),
        shape = CircleShape,
        color = color,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        if (selected) {
            Box(
                modifier = Modifier.size(ToneDiameter),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = checkColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

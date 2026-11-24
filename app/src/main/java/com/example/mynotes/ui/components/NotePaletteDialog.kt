package com.example.mynotes.ui.components

import com.example.mynotes.ui.sound.UiSoundPlayer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.mynotes.R
import com.example.mynotes.settings.MenuPreferencePolicy
import com.example.mynotes.ui.theme.NoteColorSelection
import com.example.mynotes.ui.theme.PaletteCatalog
import com.example.mynotes.ui.theme.noteBackgroundColor

/**
 * Selector común de fondos por nota: 46 familias de Configuración y cinco
 * tonos en cada una, con las mismas flores que Configuración en un carrusel horizontal.
 * Recibe únicamente la clave de color persistida, no modifica el tema global.
 */
@Composable
fun NotePaletteDialog(
    selectedColor: String,
    hiddenColorItems: Set<String> = emptySet(),
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    onDismiss: () -> Unit,
    onColorSelected: (String) -> Unit
) {
    // Keep the dialog mounted across color changes and show the tick immediately,
    // without waiting for the database/Flow to emit the updated note.
    var currentColor by remember { mutableStateOf(selectedColor) }
    LaunchedEffect(selectedColor) { currentColor = selectedColor }
    val selected = remember(currentColor) { NoteColorSelection.decode(currentColor) }
    val visiblePalettes = remember(hiddenColorItems) {
        PaletteCatalog.settingsPalettes.filterNot { "palette:${it.key}" in hiddenColorItems }
    }
    val showDefault = "default" !in hiddenColorItems
    val startIndex = remember(visiblePalettes, selected) {
        visiblePalettes.indexOfFirst { it.key == selected?.paletteKey }.coerceAtLeast(0)
    }
    val carouselState = rememberLazyListState(initialFirstVisibleItemIndex = startIndex)
    val windowWidth = (LocalConfiguration.current.screenWidthDp - 32).coerceIn(0, 320).dp
    // Altura estable al deslizar; reserva dos líneas y respeta el tamaño de fuente.
    val carouselHeight = 144.dp + with(LocalDensity.current) { 36.sp.toDp() }
    val paletteFont = MaterialTheme.typography.labelMedium.fontFamily ?: FontFamily.Default
    val paletteTextMode = when (contentColor) {
        Color.Black -> "black"
        Color.White -> "white"
        else -> "auto"
    }

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(
        usePlatformDefaultWidth = false, decorFitsSystemWindows = false
    )) {
        HideDialogNavigationBar(preventFocusFlash = true)
        Surface(
            modifier = Modifier.width(windowWidth),
            shape = RoundedCornerShape(22.dp),
            color = containerColor,
            contentColor = contentColor,
            tonalElevation = 0.dp,
            shadowElevation = 12.dp,
            border = BorderStroke(1.dp, contentColor.copy(alpha = 0.28f))
        ) {
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.Palette, contentDescription = null, modifier = Modifier.size(20.dp))
                    Text(
                        stringResource(R.string.mock_color_palette),
                        modifier = Modifier.weight(1f).padding(start = 9.dp),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    IconButton(onClick = { onDismiss(); UiSoundPlayer.playFallbackClick() }, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.Close, contentDescription = stringResource(R.string.note_palette_close), modifier = Modifier.size(20.dp))
                    }
                }
                if (visiblePalettes.isNotEmpty()) {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth().height(carouselHeight).testTag("note_palette_carousel"),
                        state = carouselState,
                        contentPadding = PaddingValues(horizontal = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        items(visiblePalettes, key = { it.key }) { palette ->
                            val label = stringResource(palette.labelRes)
                            val descriptions = palette.tones.indices.map { index ->
                                stringResource(R.string.note_palette_select_tone, index + 1, label)
                            }
                            PaletteStackItem(
                                palette = palette, selected = selected?.paletteKey == palette.key,
                                selectedToneIndex = selected?.toneIndex ?: 0,
                                onToneSelected = { key, index ->
                                    val color = NoteColorSelection.encode(key, index)
                                    if (currentColor != color) {
                                        currentColor = color
                                        onColorSelected(color)
                                    }
                                },
                                animationsEnabled = false, animationSpeed = 1f,
                                textColorMode = paletteTextMode, fontFamily = paletteFont,
                                toneDescriptions = descriptions, playToneSound = false, automaticToneCheckColor = true
                            )
                        }
                    }
                } else {
                    Text(stringResource(R.string.note_palette_empty), modifier = Modifier.padding(vertical = 16.dp),
                        color = contentColor, fontSize = 13.sp)
                }
                if (showDefault) {
                    Row(
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                            .clip(RoundedCornerShape(12.dp))
                            .background(contentColor.copy(alpha = if (currentColor == "default") 0.12f else 0.06f))
                            .semantics { this.selected = currentColor == "default" }
                            .clickable(role = Role.RadioButton) {
                                if (currentColor != "default") {
                                    currentColor = "default"
                                    onColorSelected("default")
                                }
                                UiSoundPlayer.playFallbackClick()
                            }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(20.dp).clip(CircleShape)
                                .background(MaterialTheme.colorScheme.background),
                            contentAlignment = Alignment.Center
                        ) {
                            if (currentColor == "default") {
                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp),
                                    tint = MaterialTheme.colorScheme.onBackground)
                            }
                        }
                        Text(stringResource(R.string.mock_color_default), modifier = Modifier.padding(start = 8.dp),
                            color = contentColor, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

/** Selector ligero del editor: reemplaza los 14 círculos fijos con una entrada compacta. */
@Composable
fun NotePalettePickerField(selectedColor: String, onClick: () -> Unit) {
    val selected = remember(selectedColor) { NoteColorSelection.decode(selectedColor) }
    val legacy = remember(selectedColor) { MenuPreferencePolicy.colorOptions.firstOrNull { it.key == selectedColor } }
    val label = when {
        selected != null -> {
            val palette = PaletteCatalog.find(selected.paletteKey)
            stringResource(palette.labelRes) + " · " + stringResource(R.string.note_palette_tone, selected.toneIndex + 1)
        }
        legacy != null -> stringResource(legacy.labelRes)
        else -> stringResource(R.string.mock_color_default)
    }
    Surface(
        onClick = { onClick(); UiSoundPlayer.playFallbackClick() },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(24.dp)
                    .background(noteBackgroundColor(selectedColor), CircleShape)
                    .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
            )
            Text(label, modifier = Modifier.weight(1f).padding(start = 12.dp),
                color = MaterialTheme.colorScheme.onSurface, fontSize = 14.sp)
            Icon(Icons.Default.Palette, contentDescription = stringResource(R.string.mock_color_palette),
                modifier = Modifier.size(19.dp), tint = MaterialTheme.colorScheme.onSurface)
        }
    }
}

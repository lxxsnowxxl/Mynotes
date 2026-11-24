package com.example.mynotes.ui.notes

import com.example.mynotes.ui.sound.UiSoundPlayer

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mynotes.R
import com.example.mynotes.data.Note
import com.example.mynotes.settings.AppSettings
import com.example.mynotes.settings.DesignPreferencePolicy
import com.example.mynotes.ui.theme.PaletteCatalog
import com.example.mynotes.ui.theme.harmonizedHomeSurfaces
import com.example.mynotes.data.CollectionPolicy
import com.example.mynotes.ui.components.AppAlertDialog
import com.example.mynotes.ui.components.ModernNoteCard
import com.example.mynotes.ui.components.StyledSettingsSlider
import com.example.mynotes.ui.components.toNoteCardStyle
import com.example.mynotes.ui.theme.MyNotesTheme
import com.example.mynotes.ui.theme.appFontFamily
import com.example.mynotes.ui.theme.effectiveDarkTheme
import kotlin.math.min
import kotlin.math.roundToInt

@Composable
internal fun AppearancePreviewDialog(settings: AppSettings, onApply: (AppSettings) -> Unit, onDismiss: () -> Unit) {
    var draft by remember { mutableStateOf(settings) }
    AppAlertDialog(onDismissRequest = onDismiss, title = { Text(stringResource(R.string.organize_live_preview)) },
        text = {
            Column(Modifier.fillMaxWidth().heightIn(max = 540.dp)) {
                Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(8.dp)){
                    listOf(stringResource(R.string.settings_tools_current) to settings, stringResource(R.string.settings_tools_proposed) to draft).forEach{(label,candidate)->Column(Modifier.weight(1f)){Text(label,style=MaterialTheme.typography.labelLarge)
                BoxWithConstraints(Modifier.fillMaxWidth().height(280.dp).clip(RoundedCornerShape(16.dp))) {
                    val scale = min(maxWidth.value / 360f, 250f / 640f)
                    Box(Modifier.align(Alignment.Center).layout { measurable, _ ->
                        val width = 360.dp.roundToPx(); val height = 640.dp.roundToPx()
                        val placeable = measurable.measure(Constraints.fixed(width, height))
                        layout((width * scale).roundToInt(), (height * scale).roundToInt()) {
                            placeable.placeWithLayer(0, 0) { scaleX = scale; scaleY = scale; transformOrigin = TransformOrigin(0f, 0f) }
                        }
                    }) {
                        MyNotesTheme(candidate, darkTheme = candidate.effectiveDarkTheme(), fontFamily = appFontFamily(candidate.font)) { PreviewHome(candidate) }
                    }
                }
                    }}
                }
                Column(Modifier.weight(1f, fill = false).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        DesignPreferencePolicy.presets.forEach { key ->
                            val label = when (key) { "minimal" -> R.string.design_preset_minimal; "notebook" -> R.string.design_preset_notebook; "default" -> R.string.design_preset_default; else -> R.string.design_preset_oled }
                            FilterChip(selected = draft.designPreset == key, onClick = { draft = DesignPreferencePolicy.applyPreset(draft, key) ; UiSoundPlayer.playFallbackClick()}, label = { Text(stringResource(label)) })
                        }
                    }
                    Text(stringResource(R.string.organize_preview_palette))
                    Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        (listOf("neutral") + CollectionPolicy.colors).forEach { key ->
                            FilterChip(selected = draft.backgroundColor == key && !draft.oledMode,
                                onClick = { draft = draft.copy(backgroundColor = key, backgroundToneIndex = 0, oledMode = false, designPreset = "custom") ; UiSoundPlayer.playFallbackClick()},
                                label = { Text(stringResource(PaletteCatalog.find(key).labelRes)) })
                        }
                    }
                    Text(stringResource(R.string.organize_preview_font_family))
                    Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("system_default" to R.string.organize_font_system, "serif" to R.string.organize_font_serif, "monospace" to R.string.organize_font_monospace).forEach { (key, label) ->
                            FilterChip(selected = draft.font == key, onClick = { draft = draft.copy(font = key, designPreset = "custom") ; UiSoundPlayer.playFallbackClick()}, label = { Text(stringResource(label)) })
                        }
                    }
                    Text(stringResource(R.string.organize_preview_font))
                    StyledSettingsSlider(
                        value = draft.fontSize,
                        onValueChange = { draft = draft.copy(fontSize = it, designPreset = "custom") },
                        valueRange = 12f..28f,
                        valueLabel = "${draft.fontSize.roundToInt()} sp"
                    )
                    Text(stringResource(R.string.organize_preview_corners))
                    StyledSettingsSlider(
                        value = draft.noteCardCornerRadius,
                        onValueChange = { draft = draft.copy(noteCardCornerRadius = it, designPreset = "custom") },
                        valueRange = 0f..36f,
                        valueLabel = "${draft.noteCardCornerRadius.roundToInt()} dp"
                    )
                    Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        DesignPreferencePolicy.densities.forEach { key ->
                            FilterChip(selected = draft.cardDensity == key, onClick = { draft = draft.copy(cardDensity = key, designPreset = "custom") ; UiSoundPlayer.playFallbackClick()},
                                label = { Text(stringResource(if (key == "compact") R.string.design_compact else R.string.design_comfortable)) })
                        }
                    }
                }
            }
        }, confirmButton = { TextButton(onClick = { onApply(draft) ; UiSoundPlayer.playFallbackClick(com.example.mynotes.ui.sound.UiActionSound.Save)}) { Text(stringResource(R.string.organize_apply)) } },
        dismissButton = { TextButton(onClick = { onDismiss(); UiSoundPlayer.playFallbackClick() }) { Text(stringResource(R.string.organize_cancel)) } })
}

/** Uses the real card renderer and theme. Sample notes and no-op actions cannot change user data. */
@Composable
private fun PreviewHome(settings: AppSettings) {
    val font = appFontFamily(settings.font)
    val style = settings.toNoteCardStyle()
    val previewBackground = MaterialTheme.colorScheme.background
    val previewHomeSurfaces = remember(settings.backgroundColor, previewBackground) {
        harmonizedHomeSurfaces(settings.backgroundColor, previewBackground)
    }
    val notes = listOf(
        Note(1, stringResource(R.string.organize_preview_note_title), stringResource(R.string.organize_preview_note_body)),
        Note(2, stringResource(R.string.organize_template_meeting), stringResource(R.string.organize_preview_note_body)),
        Note(3, stringResource(R.string.organize_template_shopping), stringResource(R.string.organize_preview_note_body)),
        Note(4, stringResource(R.string.organize_template_plan), stringResource(R.string.organize_preview_note_body))
    )
    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(stringResource(R.string.mock_my_notes), fontFamily = font, fontSize = 30.sp)
                Text(stringResource(R.string.mock_notes_subtitle), fontFamily = font, style = MaterialTheme.typography.bodySmall)
            }
            Icon(Icons.Default.Person, null, Modifier.padding(8.dp)); Icon(Icons.Default.Settings, null, Modifier.padding(8.dp))
        }
        Surface(shape = RoundedCornerShape(15.dp), color = MaterialTheme.colorScheme.surfaceContainerLow) {
            Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Search, null); Spacer(Modifier.width(12.dp)); Text(stringResource(R.string.mock_search_notes), fontFamily = font)
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = true, onClick = {}, label = { Text(stringResource(R.string.mock_filter_all), fontFamily = font) })
            FilterChip(selected = false, onClick = {}, label = { Text(stringResource(R.string.mock_favorites), fontFamily = font) })
        }
        Text(stringResource(R.string.design_section_today), fontFamily = font, style = MaterialTheme.typography.titleSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            repeat(2) { lane -> Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                listOf(notes[lane], notes[lane + 2]).forEach { note ->
                    ModernNoteCard(note, emptyList(), font, settings.fontSize, style, settings.textColor,
                        settings.optionMenuOrder, settings.optionMenuHiddenItems, settings.optionMenuShowIcons, settings.optionMenuTextColor,
                        settings.optionMenuOpacity, settings.priorityMenuHiddenItems, settings.colorMenuHiddenItems, settings.performanceMode,
                        onOpen = {}, onEdit = {}, onToggleFavorite = {}, onTogglePinned = {}, onPriorityChange = {}, onColorChange = {}, onCategoryChange = {}, onDelete = {},
                        globalPaletteCardColor = previewHomeSurfaces.noteCard,
                        globalPaletteCardOutlineColor = previewHomeSurfaces.noteOutline)
                }
            } }
        }
    }
}

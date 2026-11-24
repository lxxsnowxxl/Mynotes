package com.example.mynotes.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.mynotes.R
import com.example.mynotes.settings.AppSettings
import com.example.mynotes.settings.DesignPreferencePolicy
import com.example.mynotes.ui.sound.UiSoundPlayer
import com.example.mynotes.ui.state.NoteSectionKind
import com.example.mynotes.ui.state.SettingsActions
import com.example.mynotes.ui.theme.ensureUiContrast

internal fun designPresetTitle(key: String): Int = when (key) {
    "minimal" -> R.string.design_preset_minimal
    "notebook" -> R.string.design_preset_notebook
    "default" -> R.string.design_preset_default
    else -> R.string.design_preset_oled
}

internal fun designSortTitle(key: String): Int = when (key) {
    "oldest" -> R.string.design_sort_oldest
    "title" -> R.string.design_sort_title
    "priority" -> R.string.design_sort_priority
    else -> R.string.design_sort_newest
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun DesignSettingsPanel(
    settings: AppSettings, actions: SettingsActions, fontFamily: FontFamily,
    contentPadding: PaddingValues = PaddingValues(14.dp), onPreview: () -> Unit
) {
    SettingsSectionPanel(textColorMode = settings.textColor, contentPadding = contentPadding) { colors ->
        val palette = MaterialTheme.colorScheme
        val selectedBorder = ensureUiContrast(palette.primary, colors.background, 3f)
        val defaultBorder = ensureUiContrast(palette.outlineVariant, colors.background, 3f)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(stringResource(R.string.design_styles), modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium, color = colors.text, fontFamily = fontFamily)
                OutlinedButton(
                    onClick = {
                        onPreview()
                        UiSoundPlayer.playFallbackClick()
                    },
                    modifier = Modifier.heightIn(min = 48.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.secondaryText),
                    border = BorderStroke(1.dp, defaultBorder),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(stringResource(R.string.design_preview), fontFamily = fontFamily)
                }
            }
            Text(stringResource(R.string.design_styles_description), style = MaterialTheme.typography.bodySmall,
                color = colors.secondaryText, fontFamily = fontFamily)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(vertical = 8.dp)) {
                items(DesignPreferencePolicy.presets, key = { it }) { key ->
                    val selected = settings.designPreset == key
                    Surface(
                        onClick = {
                            actions.applyDesignPreset(key)
                            UiSoundPlayer.playFallbackClick()
                        },
                        modifier = Modifier.width(148.dp).semantics { this.selected = selected },
                        shape = RoundedCornerShape(16.dp),
                        color = colors.background,
                        contentColor = colors.text,
                        border = BorderStroke(if (selected) 2.dp else 1.dp,
                            if (selected) selectedBorder else defaultBorder)
                    ) {
                        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            DesignPresetThumbnail(key, Modifier.fillMaxWidth().height(80.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(stringResource(designPresetTitle(key)), modifier = Modifier.weight(1f),
                                    fontFamily = fontFamily, style = MaterialTheme.typography.labelLarge)
                                if (selected) Icon(Icons.Default.CheckCircle, null, Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
            Text(stringResource(R.string.design_density), style = MaterialTheme.typography.titleSmall,
                color = colors.text, fontFamily = fontFamily)
            val densityColors = FilterChipDefaults.filterChipColors(
                labelColor = colors.text,
                selectedLabelColor = ensureUiContrast(palette.onSecondaryContainer, palette.secondaryContainer, 4.5f)
            )
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                DesignPreferencePolicy.densities.forEach { key ->
                    FilterChip(
                        selected = settings.cardDensity == key,
                        onClick = {
                            actions.setCardDensity(key)
                            UiSoundPlayer.playFallbackClick()
                        },
                        label = { Text(stringResource(if (key == "compact") R.string.design_compact else R.string.design_comfortable),
                            fontFamily = fontFamily) },
                        modifier = Modifier.heightIn(min = 48.dp),
                        colors = densityColors
                    )
                }
            }
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f).padding(end = 12.dp)) {
                    Text(stringResource(R.string.design_focus), fontFamily = fontFamily,
                        style = MaterialTheme.typography.titleSmall, color = colors.text)
                    Text(stringResource(R.string.design_focus_description), fontFamily = fontFamily,
                        style = MaterialTheme.typography.bodySmall, color = colors.secondaryText)
                }
                Switch(checked = settings.editorFocusMode, onCheckedChange = { checked ->
                    actions.setEditorFocusMode(checked)
                    UiSoundPlayer.playFallbackClick()
                })
            }
        }
    }
}

/** Schematic preview of each complete style; these are not screenshots. */
@Composable
private fun DesignPresetThumbnail(key: String, modifier: Modifier) {
    val paper = when (key) { "oled" -> Color.Black; "notebook" -> Color(0xFFFFF7E5); "default" -> Color(0xFFE6E8F2); else -> Color(0xFFF3F2EF) }
    val ink = when (key) { "oled" -> Color(0xFFF4F0E8); "default" -> Color(0xFF414963); else -> Color(0xFF383B3A) }
    val card = when (key) { "oled" -> Color(0xFF202322); "notebook" -> Color(0xFFF0E3C6); "default" -> Color(0xFFFAFAFE); else -> Color.White }
    Canvas(modifier) {
        drawRoundRect(paper, cornerRadius = CornerRadius(8.dp.toPx()))
        drawRoundRect(ink, topLeft = Offset(size.width * .08f, size.height * .10f),
            size = Size(size.width * .42f, size.height * .06f), cornerRadius = CornerRadius(2.dp.toPx()))
        repeat(2) { column ->
            val left = size.width * (.08f + .46f * column)
            drawRoundRect(card, Offset(left, size.height * .28f), Size(size.width * .38f, size.height * .60f),
                CornerRadius(if (key == "notebook") 2.dp.toPx() else 6.dp.toPx()))
            repeat(3) { line -> drawLine(ink.copy(alpha = if (line == 0) .75f else .3f),
                Offset(left + size.width * .04f, size.height * (.39f + .14f * line)),
                Offset(left + size.width * .30f, size.height * (.39f + .14f * line)), 2.dp.toPx()) }
        }
    }
}

@Composable
internal fun HomeViewControls(settings: AppSettings, actions: SettingsActions?, count: Int, fontFamily: FontFamily) {
    var sortExpanded by remember { mutableStateOf(false) }
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(pluralStringResource(R.plurals.design_note_count, count, count), modifier = Modifier.weight(1f),
            fontFamily = fontFamily, style = MaterialTheme.typography.labelLarge)
        IconButton(onClick = { actions?.setGridColumns(if (settings.gridColumns == 1) 2 else 1) ; UiSoundPlayer.playFallbackClick()},
            modifier = Modifier.size(48.dp)) {
            Icon(if (settings.gridColumns == 1) Icons.Default.GridView else Icons.Default.ViewAgenda,
                stringResource(if (settings.gridColumns == 1) R.string.design_show_grid else R.string.design_show_list))
        }
        IconButton(onClick = { actions?.setCardDensity(if (settings.cardDensity == "compact") "comfortable" else "compact") ; UiSoundPlayer.playFallbackClick()},
            modifier = Modifier.size(48.dp)) {
            Icon(if (settings.cardDensity == "compact") Icons.Default.DensityMedium else Icons.Default.DensitySmall,
                stringResource(if (settings.cardDensity == "compact") R.string.design_use_comfortable else R.string.design_use_compact))
        }
        Box {
            IconButton(onClick = { sortExpanded = true ; UiSoundPlayer.playFallbackClick()}, modifier = Modifier.size(48.dp)) {
                Icon(Icons.Default.Sort, stringResource(R.string.design_sort_by))
            }
            AppDropdownMenu(expanded = sortExpanded, onDismissRequest = { sortExpanded = false }) {
                DesignPreferencePolicy.sortOrders.forEach { order ->
                    DropdownMenuItem(text = { Text(stringResource(designSortTitle(order)), fontFamily = fontFamily) },
                        onClick = { actions?.setSortOrder(order); sortExpanded = false ; UiSoundPlayer.playFallbackClick()},
                        trailingIcon = { if (settings.sortOrder == order) Icon(Icons.Default.Check, null) })
                }
            }
        }
    }
}

@Composable
internal fun NoteSectionHeading(kind: NoteSectionKind, count: Int, fontFamily: FontFamily) {
    val label = when (kind) {
        NoteSectionKind.PINNED -> R.string.design_section_pinned
        NoteSectionKind.TODAY -> R.string.design_section_today
        NoteSectionKind.THIS_WEEK -> R.string.design_section_week
        NoteSectionKind.EARLIER -> R.string.design_section_earlier
        NoteSectionKind.NOTES -> R.string.design_section_notes
    }
    Row(Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        if (kind == NoteSectionKind.PINNED) {
            Icon(Icons.Default.PushPin, null, Modifier.padding(end = 8.dp).size(16.dp))
        }
        Text(stringResource(label), style = MaterialTheme.typography.titleSmall, fontFamily = fontFamily,
            fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f), overflow = TextOverflow.Ellipsis)
        Text(count.toString(), style = MaterialTheme.typography.labelMedium, fontFamily = fontFamily,
            modifier = Modifier.padding(start = 8.dp))
    }
}

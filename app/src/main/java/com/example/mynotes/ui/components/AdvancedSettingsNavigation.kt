package com.example.mynotes.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mynotes.R
import com.example.mynotes.ui.sound.UiActionSound
import com.example.mynotes.ui.sound.UiSoundPlayer
import com.example.mynotes.ui.theme.settingsSectionColors
import com.example.mynotes.ui.theme.ensureUiContrast

/** Los índices se validan contra las claves reales de la lista de Ajustes. */
internal enum class AdvancedSettingsCategory(
    val anchorKey: String,
    @StringRes val labelRes: Int, @StringRes val titleRes: Int, @StringRes val descriptionRes: Int
) {
    APPEARANCE("section_appearance", R.string.advanced_nav_appearance, R.string.advanced_section_appearance, R.string.advanced_section_appearance_description),
    TYPOGRAPHY("section_typography", R.string.advanced_nav_typography, R.string.advanced_section_typography, R.string.advanced_section_typography_description),
    NOTES("section_notes", R.string.advanced_nav_notes, R.string.advanced_section_notes, R.string.advanced_section_notes_description),
    MENU("section_menu", R.string.advanced_nav_menu, R.string.advanced_section_menu, R.string.advanced_section_menu_description),
    FEEDBACK("section_feedback", R.string.advanced_nav_feedback, R.string.advanced_section_feedback, R.string.advanced_section_feedback_description),
    MOTION("section_motion", R.string.advanced_nav_motion, R.string.advanced_section_motion, R.string.advanced_section_motion_description),
    APPLICATION("section_application", R.string.advanced_nav_application, R.string.advanced_section_application, R.string.advanced_section_application_description),
    DATA("section_data", R.string.advanced_nav_data, R.string.advanced_section_data, R.string.advanced_section_data_description);

    val firstItemIndex: Int get() = advancedSettingsItemKeys.indexOf(anchorKey).also { check(it >= 0) }

    companion object {
        val ordered: List<AdvancedSettingsCategory> = values().toList()
        fun forItem(index: Int): AdvancedSettingsCategory =
            ordered.lastOrNull { it.firstItemIndex <= index } ?: APPEARANCE
    }
}

/** Orden único usado para verificar los anclajes de LazyColumn al reorganizar el menú. */
internal val advancedSettingsItemKeys: List<String> = listOf(
    "appearance_profile", "section_appearance", "palette_background", "design_styles", "extreme_accent",
    "appearance_panel_tone", "appearance_translucent_panels", "layout_dark_mode",
    "section_typography", "appearance_text_color", "appearance_text_outline", "font",
    "extreme_icons", "layout_slider_design", "section_notes", "extreme_note_cards",
    "layout_columns", "extreme_fab", "section_menu", "options_appearance",
    "section_feedback",
    "feedback_sound_effects", "feedback_alert_sounds", "feedback_vibration",
    "section_motion", "extreme_motion", "note_card_animation_styles", "extreme_performance",
    "section_application", "language", "section_data", "layout_backup_restore",
    "development", "updates"
)

/** Ítems que realmente se montan en modo básico, en el orden del LazyColumn. */
internal val basicSettingsItemKeys = listOf(
    "appearance_profile", "palette_background", "design_styles", "font",
    "language", "development", "updates"
)

/** Accesos siempre visibles; con texto grande se pueden desplazar horizontalmente. */
@Composable
internal fun AdvancedSettingsNavigation(
    activeCategory: AdvancedSettingsCategory, textColorMode: String, fontFamily: FontFamily,
    onCategorySelected: (AdvancedSettingsCategory) -> Unit, modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val background = MaterialTheme.colorScheme.surfaceContainerLow
    val colors = remember(background, textColorMode) { settingsSectionColors(background, textColorMode) }
    val fontScale = LocalDensity.current.fontScale
    val navState = rememberLazyListState()
    LaunchedEffect(activeCategory) {
        // Mantiene visible la pestaña activa incluso en teléfonos de pantalla estrecha.
        navState.animateScrollToItem(activeCategory.ordinal)
    }
    Surface(modifier = modifier, shape = RoundedCornerShape(16.dp), color = colors.background,
        tonalElevation = 0.dp, shadowElevation = 0.dp) {
        BoxWithConstraints(Modifier.fillMaxWidth().padding(4.dp)) {
            val cellWidth = maxOf(maxWidth / 6, if (fontScale > 1.1f) 92.dp else 70.dp)
            LazyRow(state = navState, modifier = Modifier.fillMaxWidth().selectableGroup()) {
                items(AdvancedSettingsCategory.ordered, key = { it.name }) { category ->
                    val isSelected = category == activeCategory
                    val selectedBackground = MaterialTheme.colorScheme.primaryContainer
                    val foreground = if (isSelected)
                        ensureUiContrast(MaterialTheme.colorScheme.onPrimaryContainer, selectedBackground, 4.5f)
                    else colors.text
                    Surface(
                        onClick = UiSoundPlayer.actionHandler(context, UiActionSound.Menu) { onCategorySelected(category) },
                        modifier = Modifier.width(cellWidth).heightIn(min = 48.dp)
                            .semantics { selected = isSelected; role = Role.Tab },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) selectedBackground else Color.Transparent,
                        contentColor = foreground,
                        border = if (isSelected) BorderStroke(1.dp, foreground.copy(alpha = 0.25f)) else null,
                        tonalElevation = 0.dp, shadowElevation = 0.dp
                    ) {
                        Box(Modifier.padding(horizontal = 4.dp, vertical = 10.dp), contentAlignment = Alignment.Center) {
                            Text(stringResource(category.labelRes), color = foreground, fontFamily = fontFamily,
                                fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                maxLines = 1, textAlign = TextAlign.Center)
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun AdvancedSettingsSectionHeading(
    category: AdvancedSettingsCategory, fontFamily: FontFamily,
    textColor: Color, secondaryTextColor: Color,
    @StringRes titleRes: Int = category.titleRes, @StringRes descriptionRes: Int = category.descriptionRes
) {
    val visibleAccent = ensureUiContrast(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.background, 3f)
    Row(Modifier.fillMaxWidth().padding(top = 18.dp, bottom = 10.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.width(3.dp).height(36.dp).clip(RoundedCornerShape(2.dp)).background(visibleAccent))
        Column(Modifier.weight(1f).padding(start = 12.dp)) {
            SettingsTitle(stringResource(titleRes), textColor, fontFamily, fontSize = 18.sp)
            SettingsSecondaryText(stringResource(descriptionRes), secondaryTextColor, fontFamily,
                modifier = Modifier.padding(top = 2.dp), lineHeight = 18.sp)
        }
    }
}

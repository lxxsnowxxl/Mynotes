package com.example.mynotes.settings

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.mynotes.ui.motion.AppMotion
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

private val Context.dataStore by
    preferencesDataStore(name = "app_settings")
class SettingsRepository(private val context: Context) {
    companion object {
        private val CONFIGURATION_MODE = stringPreferencesKey("configuration_mode")
        private val DARK_MODE = booleanPreferencesKey("dark_mode")
        private val BACKGROUND_COLOR = stringPreferencesKey("background_color")
        private val BACKGROUND_TONE_INDEX = intPreferencesKey("background_tone_index")
        private val BACKGROUND_INTENSITY = floatPreferencesKey("background_intensity")
        private val SETTINGS_PANEL_TONE = floatPreferencesKey("settings_panel_tone")
        private val SURFACE_PANEL_INTENSITY = floatPreferencesKey("surface_panel_intensity")
        private val HEADER_INTENSITY = floatPreferencesKey("header_intensity")
        private val TEXT_COLOR = stringPreferencesKey("text_color")
        private val TEXT_OUTLINE_ENABLED = booleanPreferencesKey("text_outline_enabled")
        private val SLIDER_STYLE = stringPreferencesKey("slider_style")
        private val FONT = stringPreferencesKey("font")
        private val FONT_SIZE = floatPreferencesKey("font_size")
        private val SOUND_EFFECTS_ENABLED = booleanPreferencesKey("sound_effects_enabled")
        private val SOUND_EFFECTS_VOLUME = floatPreferencesKey("sound_effects_volume")
        private val SOUND_EFFECTS_THEME = stringPreferencesKey("sound_effects_theme")
        private val REMINDER_SOUND_ENABLED = booleanPreferencesKey("reminder_sound_enabled")
        private val REMINDER_SOUND_VOLUME = floatPreferencesKey("reminder_sound_volume")
        private val REMINDER_RINGTONE = stringPreferencesKey("reminder_ringtone")
        private val HAPTIC_EFFECTS_ENABLED = booleanPreferencesKey("haptic_effects_enabled")
        private val HAPTIC_EFFECTS_INTENSITY = floatPreferencesKey("haptic_effects_intensity")
        private val HAPTIC_EFFECTS_STYLE = stringPreferencesKey("haptic_effects_style")
        private val LANGUAGE = stringPreferencesKey("language")
        private val GRID_COLUMNS = intPreferencesKey("grid_columns")
        private val SORT_ORDER = stringPreferencesKey("sort_order")
        private val PROFILE_IMAGE_URI = stringPreferencesKey("profile_image_uri")
        private val PROFILE_IMAGE_SIZE = floatPreferencesKey("profile_image_size")
        private val ICON_STYLE = stringPreferencesKey("icon_style")
        private val ICON_SIZE = floatPreferencesKey("icon_size")
        private val ACCENT_COLOR = stringPreferencesKey("accent_color")
        private val NOTE_CARD_CORNER_RADIUS = floatPreferencesKey("note_card_corner_radius")
        private val NOTE_CARD_ELEVATION = floatPreferencesKey("note_card_elevation")
        private val NOTE_CARD_PADDING = floatPreferencesKey("note_card_padding")
        private val NOTE_CARD_IMAGE_HEIGHT = floatPreferencesKey("note_card_image_height")
        private val NOTE_CARD_OUTLINE_ENABLED = booleanPreferencesKey("note_card_outline_enabled")
        private val NOTE_CARD_OUTLINE_WIDTH = floatPreferencesKey("note_card_outline_width")
        private val NOTE_TITLE_MAX_LINES = intPreferencesKey("note_title_max_lines")
        private val NOTE_CONTENT_MAX_LINES = intPreferencesKey("note_content_max_lines")
        private val NOTE_LINE_SPACING = floatPreferencesKey("note_line_spacing")
        private val SHOW_NOTE_DATE = booleanPreferencesKey("show_note_date")
        private val SHOW_CATEGORY_CHIP = booleanPreferencesKey("show_category_chip")
        private val SHOW_FAVORITE_ICON = booleanPreferencesKey("show_favorite_icon")
        private val FAB_SIZE = floatPreferencesKey("fab_size")
        private val OPTION_MENU_ORDER = stringPreferencesKey("option_menu_order")
        private val OPTION_MENU_HIDDEN_ITEMS = stringPreferencesKey("option_menu_hidden_items")
        private val OPTION_MENU_SHOW_ICONS = booleanPreferencesKey("option_menu_show_icons")
        private val OPTION_MENU_TEXT_COLOR = stringPreferencesKey("option_menu_text_color")
        private val OPTION_MENU_OPACITY = floatPreferencesKey("option_menu_opacity")
        private val PRIORITY_MENU_HIDDEN_ITEMS = stringPreferencesKey("priority_menu_hidden_items")
        private val COLOR_MENU_HIDDEN_ITEMS = stringPreferencesKey("color_menu_hidden_items")
        private val PERFORMANCE_MODE = stringPreferencesKey("performance_mode")
        private val ANIMATIONS_ENABLED = booleanPreferencesKey("animations_enabled")
        private val ANIMATION_SPEED = floatPreferencesKey("animation_speed")
        private val ANIMATION_STYLE = stringPreferencesKey("animation_style")
        private val ANIMATION_EASING = stringPreferencesKey("animation_easing")
        private val ANIMATION_INTENSITY = floatPreferencesKey("animation_intensity")
    }
    val settings:
        Flow<AppSettings> =
        context.dataStore.data.map {
                    preferences ->
                val rawPalette = preferences[BACKGROUND_COLOR]?: "neutral"
                val legacyPalette = SettingsValuePolicy.isLegacyPalette(rawPalette)
                val rawSoundEffectsVolume = preferences[SOUND_EFFECTS_VOLUME]
                val soundEffectsVolume = (rawSoundEffectsVolume ?: 65f).coerceIn(0f, 100f)
                val soundEffectsEnabled = preferences[SOUND_EFFECTS_ENABLED] ?: true
                val outlineEnabledPreference = preferences[NOTE_CARD_OUTLINE_ENABLED]
                val outlineWidthPreference = preferences[NOTE_CARD_OUTLINE_WIDTH] ?: 1f
                val rawTone = preferences[BACKGROUND_TONE_INDEX]?: if (legacyPalette) {
                            3
                        } else {
                            0
                        }
                val normalizedTone = if (legacyPalette) {
                        3 - rawTone.coerceIn(0, 3)
                    } else {
                        rawTone.coerceIn(0, 3)
                    }
                AppSettings(configurationMode = SettingsValuePolicy.configurationMode(preferences[CONFIGURATION_MODE] ?: "unset"),
                    darkMode = preferences[DARK_MODE]?: false,
                    backgroundColor = SettingsValuePolicy.paletteKey(rawPalette),
                    backgroundToneIndex = normalizedTone,
                    backgroundIntensity = (preferences[BACKGROUND_INTENSITY]?: 0f).coerceIn(0f, 100f),
                    settingsPanelTone = (preferences[SETTINGS_PANEL_TONE]?: 0f).coerceIn(0f, 100f),
                    surfacePanelIntensity = (preferences[SURFACE_PANEL_INTENSITY]?: 72f).coerceIn(0f, 100f),
                    headerIntensity = (preferences[HEADER_INTENSITY]?: 18f).coerceIn(0f, 100f),
                    textColor = SettingsValuePolicy.uiTextColor(preferences[TEXT_COLOR]?: "auto"),
                    textOutlineEnabled = preferences[TEXT_OUTLINE_ENABLED]?: false,
                    sliderStyle = SettingsValuePolicy.sliderStyle(preferences[SLIDER_STYLE]?: "capsule"),
                    font = FontPreferencePolicy.normalize(
                        preferences[FONT] ?: FontPreferencePolicy.SYSTEM_DEFAULT,
                        googleSansFlexUnlocked = DeveloperFeatures.isGoogleSansFlexUnlocked(context)
                    ),
                    fontSize = (preferences[FONT_SIZE]?: 16f).coerceIn(12f, 28f),
                    soundEffectsEnabled = soundEffectsEnabled,
                    soundEffectsVolume = soundEffectsVolume,
                    soundEffectsTheme = FeedbackPreferencePolicy.normalizeSoundTheme(preferences[SOUND_EFFECTS_THEME]?: "classic"),
                    reminderSoundEnabled = preferences[REMINDER_SOUND_ENABLED] ?: soundEffectsEnabled,
                    reminderSoundVolume = soundEffectsVolume,
                    reminderRingtone = FeedbackPreferencePolicy.normalizeReminderRingtone(preferences[REMINDER_RINGTONE]?: "classic"),
                    hapticEffectsEnabled = preferences[HAPTIC_EFFECTS_ENABLED]?: true,
                    hapticEffectsIntensity = (preferences[HAPTIC_EFFECTS_INTENSITY]?: 55f).coerceIn(0f, 100f),
                    hapticEffectsStyle = FeedbackPreferencePolicy.normalizeHapticStyle(preferences[HAPTIC_EFFECTS_STYLE]?: "soft"),
                    language = preferences[LANGUAGE]?: "system",
                    gridColumns = (preferences[GRID_COLUMNS]?: 2).coerceIn(1, 3),
                    sortOrder = preferences[SORT_ORDER]?: "newest",
                    profileImageUri = preferences[PROFILE_IMAGE_URI]?: "",
                    profileImageSize = (preferences[PROFILE_IMAGE_SIZE]?: 46f).coerceIn(36f, 84f),
                    iconStyle = SettingsValuePolicy.iconStyle(preferences[ICON_STYLE]?: "rounded"),
                    iconSize = (preferences[ICON_SIZE]?: 22f).coerceIn(16f, 36f),
                    accentColor = SettingsValuePolicy.accentColor(preferences[ACCENT_COLOR]?: "palette"),
                    noteCardCornerRadius = (preferences[NOTE_CARD_CORNER_RADIUS]?: 18f).coerceIn(0f, 36f),
                    noteCardElevation = (preferences[NOTE_CARD_ELEVATION]?: 1.5f).coerceIn(0f, 12f),
                    noteCardPadding = (preferences[NOTE_CARD_PADDING]?: 12f).coerceIn(6f, 24f),
                    noteCardImageHeight = (preferences[NOTE_CARD_IMAGE_HEIGHT]?: 112f).coerceIn(72f, 220f),
                    noteCardOutlineEnabled = (outlineEnabledPreference ?: false) && outlineWidthPreference > 0f,
                    noteCardOutlineWidth = if (outlineEnabledPreference == false) {
                            0f
                        } else if (outlineEnabledPreference == true) {
                            outlineWidthPreference.coerceIn(0f, 6f)
                        } else {
                            0f
                        },
                    noteTitleMaxLines = (preferences[NOTE_TITLE_MAX_LINES]?: 4).coerceIn(1, 8),
                    noteContentMaxLines = (preferences[NOTE_CONTENT_MAX_LINES]?: 6).coerceIn(2, 14),
                    noteLineSpacing = (preferences[NOTE_LINE_SPACING]?: 1.20f).coerceIn(1f, 1.8f),
                    showNoteDate = preferences[SHOW_NOTE_DATE]?: true,
                    showCategoryChip = preferences[SHOW_CATEGORY_CHIP]?: true,
                    showFavoriteIcon = preferences[SHOW_FAVORITE_ICON]?: true,
                    fabSize = (preferences[FAB_SIZE]?: 58f).coerceIn(48f, 82f),
                    optionMenuOrder = MenuPreferencePolicy.normalizeOrder(preferences[OPTION_MENU_ORDER]?: MenuPreferencePolicy.defaultOrder),
                    optionMenuHiddenItems = MenuPreferencePolicy.normalizeHiddenItems(raw = preferences[OPTION_MENU_HIDDEN_ITEMS]?: "", validKeys =
                                MenuPreferencePolicy.mainKeySet),
                    optionMenuShowIcons = preferences[OPTION_MENU_SHOW_ICONS]?: true,
                    optionMenuTextColor = SettingsValuePolicy.optionMenuTextColor(preferences[OPTION_MENU_TEXT_COLOR]?: "note"),
                    optionMenuOpacity = (preferences[OPTION_MENU_OPACITY]?: 100f).coerceIn(35f, 100f),
                    priorityMenuHiddenItems = MenuPreferencePolicy.normalizeHiddenItems(raw = preferences[PRIORITY_MENU_HIDDEN_ITEMS]?: "", validKeys =
                                MenuPreferencePolicy.priorityKeys),
                    colorMenuHiddenItems = MenuPreferencePolicy.normalizeHiddenItems(raw = preferences[COLOR_MENU_HIDDEN_ITEMS]?: "", validKeys =
                                MenuPreferencePolicy.colorKeys),
                    performanceMode = AppMotion.normalizePerformanceMode(preferences[PERFORMANCE_MODE]?: "balanced"),
                    animationsEnabled = preferences[ANIMATIONS_ENABLED]?: true,
                    animationStyle = AppMotion.normalizeStyle(preferences[ANIMATION_STYLE]?: "zoom"),
                    animationEasing = AppMotion.normalizeEasing(preferences[ANIMATION_EASING]?: "standard"),
                    animationSpeed = (preferences[ANIMATION_SPEED]?: 1f).coerceIn(0.5f, 2f),
                    animationIntensity = (preferences[ANIMATION_INTENSITY]?: 1f).coerceIn(0.5f, 1.5f))
            }.distinctUntilChanged()
    /** Escritura común para ajustes de una sola clave; conserva la transacción. */
    private suspend fun <T> writePreference(key: Preferences.Key<T>, value: T) {
        context.dataStore.edit { it[key] = value }
    }

    suspend fun setConfigurationMode(value: String) = writePreference(CONFIGURATION_MODE, SettingsValuePolicy.configurationMode(value))

    suspend fun setDarkMode(value: Boolean) = writePreference(DARK_MODE, value)

    suspend fun setBackgroundColor(value: String) = writePreference(BACKGROUND_COLOR, SettingsValuePolicy.paletteKey(value))

    suspend fun setBackgroundToneIndex(value: Int) {
        context.dataStore.edit {
                    preferences ->
                val rawPalette = preferences[BACKGROUND_COLOR]?: "neutral"
                preferences[BACKGROUND_COLOR] = SettingsValuePolicy.paletteKey(rawPalette)
                preferences[BACKGROUND_TONE_INDEX] = value.coerceIn(0, 3)
            }
    }
    suspend fun setBackgroundIntensity(value: Float) = writePreference(BACKGROUND_INTENSITY, value.coerceIn(0f, 100f))

    suspend fun setSettingsPanelTone(value: Float) = writePreference(SETTINGS_PANEL_TONE, value.coerceIn(0f, 100f))

    suspend fun setSurfacePanelIntensity(value: Float) = writePreference(SURFACE_PANEL_INTENSITY, value.coerceIn(0f, 100f))

    suspend fun setHeaderIntensity(value: Float) = writePreference(HEADER_INTENSITY, value.coerceIn(0f, 100f))

    suspend fun setTextColor(value: String) = writePreference(TEXT_COLOR, SettingsValuePolicy.uiTextColor(value))

    suspend fun setTextOutlineEnabled(value: Boolean) = writePreference(TEXT_OUTLINE_ENABLED, value)

    suspend fun setSliderStyle(value: String) = writePreference(SLIDER_STYLE, SettingsValuePolicy.sliderStyle(value))

    suspend fun setFont(value: String) {
        val normalized = FontPreferencePolicy.normalize(
            value,
            googleSansFlexUnlocked = DeveloperFeatures.isGoogleSansFlexUnlocked(context)
        )
        context.dataStore.edit {
            it[FONT] = normalized
        }
    }
    suspend fun setFontSize(value: Float) = writePreference(FONT_SIZE, value.coerceIn(12f, 28f))

    suspend fun setSoundEffectsEnabled(value: Boolean) = writePreference(SOUND_EFFECTS_ENABLED, value)

    suspend fun setSoundEffectsVolume(value: Float) {
        val normalized = value.coerceIn(0f, 100f)
        context.dataStore.edit { preferences ->
            preferences[SOUND_EFFECTS_VOLUME] = normalized
            // Alertas y clics de escritura comparten el mismo nivel maestro que los efectos.
            preferences[REMINDER_SOUND_VOLUME] = normalized
        }
    }

    suspend fun setSoundEffectsTheme(value: String) = writePreference(SOUND_EFFECTS_THEME, FeedbackPreferencePolicy.normalizeSoundTheme(value))

    suspend fun setReminderSoundEnabled(value: Boolean) = writePreference(REMINDER_SOUND_ENABLED, value)

    suspend fun setReminderSoundVolume(value: Float) = setSoundEffectsVolume(value)

    suspend fun setReminderRingtone(value: String) = writePreference(REMINDER_RINGTONE, FeedbackPreferencePolicy.normalizeReminderRingtone(value))

    suspend fun setHapticEffectsEnabled(value: Boolean) = writePreference(HAPTIC_EFFECTS_ENABLED, value)

    suspend fun setHapticEffectsIntensity(value: Float) = writePreference(HAPTIC_EFFECTS_INTENSITY, value.coerceIn(0f, 100f))

    suspend fun setHapticEffectsStyle(value: String) = writePreference(HAPTIC_EFFECTS_STYLE, FeedbackPreferencePolicy.normalizeHapticStyle(value))

    suspend fun setLanguage(value: String) = writePreference(LANGUAGE, value)

    suspend fun setGridColumns(value: Int) = writePreference(GRID_COLUMNS, value.coerceIn(1, 3))

    suspend fun setSortOrder(value: String) = writePreference(SORT_ORDER, value)

    suspend fun setProfileImageUri(value: String) = writePreference(PROFILE_IMAGE_URI, value)

    suspend fun setProfileImageSize(value: Float) = writePreference(PROFILE_IMAGE_SIZE, value.coerceIn(36f, 84f))

    suspend fun setIconStyle(value: String) = writePreference(ICON_STYLE, SettingsValuePolicy.iconStyle(value))

    suspend fun setIconSize(value: Float) = writePreference(ICON_SIZE, value.coerceIn(16f, 36f))

    suspend fun setAccentColor(value: String) = writePreference(ACCENT_COLOR, SettingsValuePolicy.accentColor(value))

    suspend fun setNoteCardCornerRadius(value: Float) = writePreference(NOTE_CARD_CORNER_RADIUS, value.coerceIn(0f, 36f))

    suspend fun setNoteCardElevation(value: Float) = writePreference(NOTE_CARD_ELEVATION, value.coerceIn(0f, 12f))

    suspend fun setNoteCardPadding(value: Float) = writePreference(NOTE_CARD_PADDING, value.coerceIn(6f, 24f))

    suspend fun setNoteCardImageHeight(value: Float) = writePreference(NOTE_CARD_IMAGE_HEIGHT, value.coerceIn(72f, 220f))

    suspend fun setNoteCardOutlineWidth(value: Float) {
        val normalized = value.coerceIn(0f, 6f)
        context.dataStore.edit {
            it[NOTE_CARD_OUTLINE_WIDTH] = normalized
            // v38 removes the visible switch. Zero thickness is now the
            // disabled state, while any positive width enables the outline.
            it[NOTE_CARD_OUTLINE_ENABLED] = normalized > 0.01f
        }
    }
    suspend fun setNoteTitleMaxLines(value: Int) = writePreference(NOTE_TITLE_MAX_LINES, value.coerceIn(1, 8))

    suspend fun setNoteContentMaxLines(value: Int) = writePreference(NOTE_CONTENT_MAX_LINES, value.coerceIn(2, 14))

    suspend fun setNoteLineSpacing(value: Float) = writePreference(NOTE_LINE_SPACING, value.coerceIn(1f, 1.8f))

    suspend fun setShowNoteDate(value: Boolean) = writePreference(SHOW_NOTE_DATE, value)

    suspend fun setShowCategoryChip(value: Boolean) = writePreference(SHOW_CATEGORY_CHIP, value)

    suspend fun setShowFavoriteIcon(value: Boolean) = writePreference(SHOW_FAVORITE_ICON, value)

    suspend fun setFabSize(value: Float) = writePreference(FAB_SIZE, value.coerceIn(48f, 82f))

    suspend fun setOptionMenuOrder(value: String) = writePreference(OPTION_MENU_ORDER, MenuPreferencePolicy.normalizeOrder(value))

    suspend fun setOptionMenuHiddenItems(value: String) = writePreference(OPTION_MENU_HIDDEN_ITEMS, MenuPreferencePolicy.normalizeHiddenItems(value, MenuPreferencePolicy.mainKeySet))

    suspend fun setOptionMenuShowIcons(value: Boolean) = writePreference(OPTION_MENU_SHOW_ICONS, value)

    suspend fun setOptionMenuTextColor(value: String) = writePreference(OPTION_MENU_TEXT_COLOR, SettingsValuePolicy.optionMenuTextColor(value))

    suspend fun setOptionMenuOpacity(value: Float) = writePreference(OPTION_MENU_OPACITY, value.coerceIn(35f, 100f))

    suspend fun setPriorityMenuHiddenItems(value: String) = writePreference(PRIORITY_MENU_HIDDEN_ITEMS, MenuPreferencePolicy.normalizeHiddenItems(value, MenuPreferencePolicy.priorityKeys))

    suspend fun setColorMenuHiddenItems(value: String) = writePreference(COLOR_MENU_HIDDEN_ITEMS, MenuPreferencePolicy.normalizeHiddenItems(value, MenuPreferencePolicy.colorKeys))

    suspend fun resetOptionMenuSettings() {
        context.dataStore.edit {
            it[OPTION_MENU_ORDER] = MenuPreferencePolicy.defaultOrder
            it[OPTION_MENU_HIDDEN_ITEMS] = ""
            it[OPTION_MENU_SHOW_ICONS] = true
            it[OPTION_MENU_TEXT_COLOR] = "note"
            it[OPTION_MENU_OPACITY] = 100f
            it[PRIORITY_MENU_HIDDEN_ITEMS] = ""
            it[COLOR_MENU_HIDDEN_ITEMS] = ""
        }
    }
    suspend fun setPerformanceMode(value: String) = writePreference(PERFORMANCE_MODE, AppMotion.normalizePerformanceMode(value))

    suspend fun setAnimationsEnabled(value: Boolean) = writePreference(ANIMATIONS_ENABLED, value)

    suspend fun setAnimationSpeed(value: Float) = writePreference(ANIMATION_SPEED, value.coerceIn(0.5f, 2f))

    suspend fun setAnimationStyle(value: String) = writePreference(ANIMATION_STYLE, AppMotion.normalizeStyle(value))

    suspend fun setAnimationEasing(value: String) = writePreference(ANIMATION_EASING, AppMotion.normalizeEasing(value))

    suspend fun setAnimationIntensity(value: Float) = writePreference(ANIMATION_INTENSITY, value.coerceIn(0.5f, 1.5f))

    /**
     * Restaura todas las preferencias de una copia de seguridad en una
     * sola transacción de DataStore. Los mismos límites y normalizadores
     * usados por los setters normales se aplican aquí.
     */
    suspend fun restoreFromBackup(value: AppSettings) {
        context.dataStore.edit { preferences -> preferences[CONFIGURATION_MODE] = SettingsValuePolicy.configurationMode(value.configurationMode)
            preferences[DARK_MODE] = value.darkMode
            preferences[BACKGROUND_COLOR] = SettingsValuePolicy.paletteKey(value.backgroundColor)
            preferences[BACKGROUND_TONE_INDEX] = value.backgroundToneIndex.coerceIn(0, 3)
            preferences[BACKGROUND_INTENSITY] = value.backgroundIntensity.coerceIn(0f, 100f)
            preferences[SETTINGS_PANEL_TONE] = value.settingsPanelTone.coerceIn(0f, 100f)
            preferences[SURFACE_PANEL_INTENSITY] = value.surfacePanelIntensity.coerceIn(0f, 100f)
            preferences[HEADER_INTENSITY] = value.headerIntensity.coerceIn(0f, 100f)
            preferences[TEXT_COLOR] = SettingsValuePolicy.uiTextColor(value.textColor)
            preferences[TEXT_OUTLINE_ENABLED] = value.textOutlineEnabled
            preferences[SLIDER_STYLE] = SettingsValuePolicy.sliderStyle(value.sliderStyle)
            preferences[FONT] = FontPreferencePolicy.normalize(
                value.font,
                googleSansFlexUnlocked = DeveloperFeatures.isGoogleSansFlexUnlocked(context)
            )
            preferences[FONT_SIZE] = value.fontSize.coerceIn(12f, 28f)
            preferences[SOUND_EFFECTS_ENABLED] = value.soundEffectsEnabled
            preferences[SOUND_EFFECTS_VOLUME] = value.soundEffectsVolume.coerceIn(0f, 100f)
            preferences[SOUND_EFFECTS_THEME] = FeedbackPreferencePolicy.normalizeSoundTheme(value.soundEffectsTheme)
            preferences[REMINDER_SOUND_ENABLED] = value.reminderSoundEnabled
            preferences[REMINDER_SOUND_VOLUME] = value.soundEffectsVolume.coerceIn(0f, 100f)
            preferences[REMINDER_RINGTONE] = FeedbackPreferencePolicy.normalizeReminderRingtone(value.reminderRingtone)
            preferences[HAPTIC_EFFECTS_ENABLED] = value.hapticEffectsEnabled
            preferences[HAPTIC_EFFECTS_INTENSITY] = value.hapticEffectsIntensity.coerceIn(0f, 100f)
            preferences[HAPTIC_EFFECTS_STYLE] = FeedbackPreferencePolicy.normalizeHapticStyle(value.hapticEffectsStyle)
            preferences[LANGUAGE] = value.language
            preferences[GRID_COLUMNS] = value.gridColumns.coerceIn(1, 3)
            preferences[SORT_ORDER] = value.sortOrder
            preferences[PROFILE_IMAGE_URI] = value.profileImageUri
            preferences[PROFILE_IMAGE_SIZE] = value.profileImageSize.coerceIn(36f, 84f)
            preferences[ICON_STYLE] = SettingsValuePolicy.iconStyle(value.iconStyle)
            preferences[ICON_SIZE] = value.iconSize.coerceIn(16f, 36f)
            preferences[ACCENT_COLOR] = SettingsValuePolicy.accentColor(value.accentColor)
            preferences[NOTE_CARD_CORNER_RADIUS] = value.noteCardCornerRadius.coerceIn(0f, 36f)
            preferences[NOTE_CARD_ELEVATION] = value.noteCardElevation.coerceIn(0f, 12f)
            preferences[NOTE_CARD_PADDING] = value.noteCardPadding.coerceIn(6f, 24f)
            preferences[NOTE_CARD_IMAGE_HEIGHT] = value.noteCardImageHeight.coerceIn(72f, 220f)
            val outlineWidth = value.noteCardOutlineWidth.coerceIn(0f, 6f)
            preferences[NOTE_CARD_OUTLINE_ENABLED] = outlineWidth > 0.01f
            preferences[NOTE_CARD_OUTLINE_WIDTH] = outlineWidth
            preferences[NOTE_TITLE_MAX_LINES] = value.noteTitleMaxLines.coerceIn(1, 8)
            preferences[NOTE_CONTENT_MAX_LINES] = value.noteContentMaxLines.coerceIn(2, 14)
            preferences[NOTE_LINE_SPACING] = value.noteLineSpacing.coerceIn(1f, 1.8f)
            preferences[SHOW_NOTE_DATE] = value.showNoteDate
            preferences[SHOW_CATEGORY_CHIP] = value.showCategoryChip
            preferences[SHOW_FAVORITE_ICON] = value.showFavoriteIcon
            preferences[FAB_SIZE] = value.fabSize.coerceIn(48f, 82f)
            preferences[OPTION_MENU_ORDER] = MenuPreferencePolicy.normalizeOrder(value.optionMenuOrder)
            preferences[OPTION_MENU_HIDDEN_ITEMS] = MenuPreferencePolicy.normalizeHiddenItems(value.optionMenuHiddenItems, MenuPreferencePolicy.mainKeySet)
            preferences[OPTION_MENU_SHOW_ICONS] = value.optionMenuShowIcons
            preferences[OPTION_MENU_TEXT_COLOR] = SettingsValuePolicy.optionMenuTextColor(value.optionMenuTextColor)
            preferences[OPTION_MENU_OPACITY] = value.optionMenuOpacity.coerceIn(35f, 100f)
            preferences[PRIORITY_MENU_HIDDEN_ITEMS] = MenuPreferencePolicy.normalizeHiddenItems(value.priorityMenuHiddenItems, MenuPreferencePolicy.priorityKeys)
            preferences[COLOR_MENU_HIDDEN_ITEMS] = MenuPreferencePolicy.normalizeHiddenItems(value.colorMenuHiddenItems, MenuPreferencePolicy.colorKeys)
            preferences[PERFORMANCE_MODE] = AppMotion.normalizePerformanceMode(value.performanceMode)
            preferences[ANIMATIONS_ENABLED] = value.animationsEnabled
            preferences[ANIMATION_SPEED] = value.animationSpeed.coerceIn(0.5f, 2f)
            preferences[ANIMATION_STYLE] = AppMotion.normalizeStyle(value.animationStyle)
            preferences[ANIMATION_EASING] = AppMotion.normalizeEasing(value.animationEasing)
            preferences[ANIMATION_INTENSITY] = value.animationIntensity.coerceIn(0.5f, 1.5f)
        }
    }
}

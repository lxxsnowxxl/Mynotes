package com.example.mynotes.settings

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
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
        /*
         * Claves actuales del catálogo de paletas.
         *
         * Mantener esta lista sincronizada con PaletteCatalog evita que
         * DataStore convierta una paleta válida a "neutral" al guardarla.
         */
        private val currentPaletteKeys = setOf("neutral", "warm", "sage", "ocean", "lavender", "rose", "sunset", "violet_dusk", "marple",
                "classic", "cream", "graphite", "soft_pink", "coral", "orange", "sun", "blue", "sky", "cyan", "green", "mint", "peach",
                "neon_pink", "electric_blue", "electric_cyan", "neon_lime", "neon_violet", "vivid_orange", "neon_yellow", "neon_green",
                "midnight", "navy", "emerald", "forest", "turquoise", "ice", "indigo", "plum", "wine", "cherry", "terracotta", "coffee", "sand",
                "olive", "mustard", "monochrome")
        /*
         * Solo estas claves pertenecen realmente al selector antiguo.
         * Una clave desconocida ya no se considera automáticamente legacy,
         * evitando invertir el tono de una paleta nueva por error.
         */
        private val legacyPaletteKeys = setOf("yellow", "purple", "pink", "default", "gray")
    }
    val settings:
        Flow<AppSettings> =
        context.dataStore.data.map {
                    preferences ->
                val rawPalette = preferences[BACKGROUND_COLOR]?: "neutral"
                /*
                 * Las paletas anteriores estaban ordenadas
                 * oscuro -> claro.
                 *
                 * El nuevo selector se muestra claro -> oscuro.
                 * Si encontramos una clave antigua invertimos
                 * una sola vez la interpretación del índice.
                 */
                val rawTone = preferences[BACKGROUND_TONE_INDEX]?: if (isLegacyPalette(rawPalette)) {
                            3
                        } else {
                            0
                        }
                val normalizedTone = if (isLegacyPalette(rawPalette)) {
                        3 - rawTone.coerceIn(0, 3)
                    } else {
                        rawTone.coerceIn(0, 3)
                    }
                AppSettings(configurationMode = normalizeConfigurationMode(preferences[CONFIGURATION_MODE] ?: "unset"),
                    darkMode = preferences[DARK_MODE]?: false,
                    backgroundColor = normalizePaletteKey(rawPalette),
                    backgroundToneIndex = normalizedTone,
                    backgroundIntensity = (preferences[BACKGROUND_INTENSITY]?: 0f).coerceIn(0f, 100f),
                    settingsPanelTone = (preferences[SETTINGS_PANEL_TONE]?: 0f).coerceIn(0f, 100f),
                    surfacePanelIntensity = (preferences[SURFACE_PANEL_INTENSITY]?: 72f).coerceIn(0f, 100f),
                    headerIntensity = (preferences[HEADER_INTENSITY]?: 18f).coerceIn(0f, 100f),
                    textColor = normalizeUiTextColor(preferences[TEXT_COLOR]?: "auto"),
                    textOutlineEnabled = preferences[TEXT_OUTLINE_ENABLED]?: false,
                    sliderStyle = normalizeSliderStyle(preferences[SLIDER_STYLE]?: "capsule"),
                    font = FontPreferencePolicy.normalize(
                        preferences[FONT] ?: FontPreferencePolicy.SYSTEM_DEFAULT,
                        googleSansFlexUnlocked = DeveloperFeatures.isGoogleSansFlexUnlocked(context)
                    ),
                    fontSize = (preferences[FONT_SIZE]?: 16f).coerceIn(12f, 28f),
                    soundEffectsEnabled = preferences[SOUND_EFFECTS_ENABLED]?: true,
                    soundEffectsVolume = (preferences[SOUND_EFFECTS_VOLUME]?: 65f).coerceIn(0f, 100f),
                    soundEffectsTheme = FeedbackPreferencePolicy.normalizeSoundTheme(preferences[SOUND_EFFECTS_THEME]?: "classic"),
                    reminderSoundEnabled = preferences[REMINDER_SOUND_ENABLED] ?: (preferences[SOUND_EFFECTS_ENABLED] ?: true),
                    reminderSoundVolume = (preferences[REMINDER_SOUND_VOLUME] ?: preferences[SOUND_EFFECTS_VOLUME] ?: 75f).coerceIn(0f, 100f),
                    reminderRingtone = FeedbackPreferencePolicy.normalizeReminderRingtone(preferences[REMINDER_RINGTONE]?: "classic"),
                    hapticEffectsEnabled = preferences[HAPTIC_EFFECTS_ENABLED]?: true,
                    hapticEffectsIntensity = (preferences[HAPTIC_EFFECTS_INTENSITY]?: 55f).coerceIn(0f, 100f),
                    hapticEffectsStyle = FeedbackPreferencePolicy.normalizeHapticStyle(preferences[HAPTIC_EFFECTS_STYLE]?: "soft"),
                    language = preferences[LANGUAGE]?: "system",
                    gridColumns = (preferences[GRID_COLUMNS]?: 2).coerceIn(1, 3),
                    sortOrder = preferences[SORT_ORDER]?: "newest",
                    profileImageUri = preferences[PROFILE_IMAGE_URI]?: "",
                    profileImageSize = (preferences[PROFILE_IMAGE_SIZE]?: 46f).coerceIn(36f, 84f),
                    iconStyle = normalizeIconStyle(preferences[ICON_STYLE]?: "rounded"),
                    iconSize = (preferences[ICON_SIZE]?: 22f).coerceIn(16f, 36f),
                    accentColor = normalizeAccentColor(preferences[ACCENT_COLOR]?: "palette"),
                    noteCardCornerRadius = (preferences[NOTE_CARD_CORNER_RADIUS]?: 18f).coerceIn(0f, 36f),
                    noteCardElevation = (preferences[NOTE_CARD_ELEVATION]?: 1.5f).coerceIn(0f, 12f),
                    noteCardPadding = (preferences[NOTE_CARD_PADDING]?: 12f).coerceIn(6f, 24f),
                    noteCardImageHeight = (preferences[NOTE_CARD_IMAGE_HEIGHT]?: 112f).coerceIn(72f, 220f),
                    noteCardOutlineEnabled = (preferences[NOTE_CARD_OUTLINE_ENABLED]?: false) &&
                        (preferences[NOTE_CARD_OUTLINE_WIDTH]?: 1f) > 0f,
                    noteCardOutlineWidth = if (preferences[NOTE_CARD_OUTLINE_ENABLED] == false) {
                            0f
                        } else if (preferences[NOTE_CARD_OUTLINE_ENABLED] == true) {
                            (preferences[NOTE_CARD_OUTLINE_WIDTH]?: 1f).coerceIn(0f, 6f)
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
                    optionMenuTextColor = normalizeOptionMenuTextColor(preferences[OPTION_MENU_TEXT_COLOR]?: "note"),
                    optionMenuOpacity = (preferences[OPTION_MENU_OPACITY]?: 100f).coerceIn(35f, 100f),
                    priorityMenuHiddenItems = MenuPreferencePolicy.normalizeHiddenItems(raw = preferences[PRIORITY_MENU_HIDDEN_ITEMS]?: "", validKeys =
                                MenuPreferencePolicy.priorityKeys),
                    colorMenuHiddenItems = MenuPreferencePolicy.normalizeHiddenItems(raw = preferences[COLOR_MENU_HIDDEN_ITEMS]?: "", validKeys =
                                MenuPreferencePolicy.colorKeys),
                    performanceMode = normalizePerformanceMode(preferences[PERFORMANCE_MODE]?: "balanced"),
                    animationsEnabled = preferences[ANIMATIONS_ENABLED]?: true,
                    animationStyle = normalizeAnimationStyle(preferences[ANIMATION_STYLE]?: "zoom"),
                    animationEasing = normalizeAnimationEasing(preferences[ANIMATION_EASING]?: "standard"),
                    animationSpeed = (preferences[ANIMATION_SPEED]?: 1f).coerceIn(0.5f, 2f),
                    animationIntensity = (preferences[ANIMATION_INTENSITY]?: 1f).coerceIn(0.5f, 1.5f))
            }.distinctUntilChanged()
    /** Escritura común para ajustes de una sola clave; conserva la transacción. */
    private suspend fun <T> writePreference(key: Preferences.Key<T>, value: T) {
        context.dataStore.edit { it[key] = value }
    }

    suspend fun setConfigurationMode(value: String) =
        writePreference(CONFIGURATION_MODE, normalizeConfigurationMode(value))

    suspend fun setDarkMode(value: Boolean) =
        writePreference(DARK_MODE, value)

    suspend fun setBackgroundColor(value: String) =
        writePreference(BACKGROUND_COLOR, normalizePaletteKey(value))

    suspend fun setBackgroundToneIndex(value: Int) {
        context.dataStore.edit {
                    preferences ->
                /*
                 * Si aún estaba guardada una paleta antigua,
                 * la convertimos cuando el usuario toque un tono.
                 */
                val rawPalette = preferences[BACKGROUND_COLOR]?: "neutral"
                preferences[BACKGROUND_COLOR] = normalizePaletteKey(rawPalette)
                preferences[BACKGROUND_TONE_INDEX] = value.coerceIn(0, 3)
            }
    }
    suspend fun setBackgroundIntensity(value: Float) =
        writePreference(BACKGROUND_INTENSITY, value.coerceIn(0f, 100f))

    suspend fun setSettingsPanelTone(value: Float) =
        writePreference(SETTINGS_PANEL_TONE, value.coerceIn(0f, 100f))

    suspend fun setSurfacePanelIntensity(value: Float) =
        writePreference(SURFACE_PANEL_INTENSITY, value.coerceIn(0f, 100f))

    suspend fun setHeaderIntensity(value: Float) =
        writePreference(HEADER_INTENSITY, value.coerceIn(0f, 100f))

    suspend fun setTextColor(value: String) =
        writePreference(TEXT_COLOR, normalizeUiTextColor(value))

    suspend fun setTextOutlineEnabled(value: Boolean) =
        writePreference(TEXT_OUTLINE_ENABLED, value)

    suspend fun setSliderStyle(value: String) =
        writePreference(SLIDER_STYLE, normalizeSliderStyle(value))

    suspend fun setFont(value: String) {
        val normalized = FontPreferencePolicy.normalize(
            value,
            googleSansFlexUnlocked = DeveloperFeatures.isGoogleSansFlexUnlocked(context)
        )
        context.dataStore.edit {
            it[FONT] = normalized
        }
    }
    suspend fun setFontSize(value: Float) =
        writePreference(FONT_SIZE, value.coerceIn(12f, 28f))

    suspend fun setSoundEffectsEnabled(value: Boolean) =
        writePreference(SOUND_EFFECTS_ENABLED, value)

    suspend fun setSoundEffectsVolume(value: Float) =
        writePreference(SOUND_EFFECTS_VOLUME, value.coerceIn(0f, 100f))

    suspend fun setSoundEffectsTheme(value: String) =
        writePreference(SOUND_EFFECTS_THEME, FeedbackPreferencePolicy.normalizeSoundTheme(value))

    suspend fun setReminderSoundEnabled(value: Boolean) =
        writePreference(REMINDER_SOUND_ENABLED, value)

    suspend fun setReminderSoundVolume(value: Float) =
        writePreference(REMINDER_SOUND_VOLUME, value.coerceIn(0f, 100f))

    suspend fun setReminderRingtone(value: String) =
        writePreference(REMINDER_RINGTONE, FeedbackPreferencePolicy.normalizeReminderRingtone(value))

    suspend fun setHapticEffectsEnabled(value: Boolean) =
        writePreference(HAPTIC_EFFECTS_ENABLED, value)

    suspend fun setHapticEffectsIntensity(value: Float) =
        writePreference(HAPTIC_EFFECTS_INTENSITY, value.coerceIn(0f, 100f))

    suspend fun setHapticEffectsStyle(value: String) =
        writePreference(HAPTIC_EFFECTS_STYLE, FeedbackPreferencePolicy.normalizeHapticStyle(value))

    suspend fun setLanguage(value: String) =
        writePreference(LANGUAGE, value)

    suspend fun setGridColumns(value: Int) =
        writePreference(GRID_COLUMNS, value.coerceIn(1, 3))

    suspend fun setSortOrder(value: String) =
        writePreference(SORT_ORDER, value)

    suspend fun setProfileImageUri(value: String) =
        writePreference(PROFILE_IMAGE_URI, value)

    suspend fun setProfileImageSize(value: Float) =
        writePreference(PROFILE_IMAGE_SIZE, value.coerceIn(36f, 84f))

    suspend fun setIconStyle(value: String) =
        writePreference(ICON_STYLE, normalizeIconStyle(value))

    suspend fun setIconSize(value: Float) =
        writePreference(ICON_SIZE, value.coerceIn(16f, 36f))

    suspend fun setAccentColor(value: String) =
        writePreference(ACCENT_COLOR, normalizeAccentColor(value))

    suspend fun setNoteCardCornerRadius(value: Float) =
        writePreference(NOTE_CARD_CORNER_RADIUS, value.coerceIn(0f, 36f))

    suspend fun setNoteCardElevation(value: Float) =
        writePreference(NOTE_CARD_ELEVATION, value.coerceIn(0f, 12f))

    suspend fun setNoteCardPadding(value: Float) =
        writePreference(NOTE_CARD_PADDING, value.coerceIn(6f, 24f))

    suspend fun setNoteCardImageHeight(value: Float) =
        writePreference(NOTE_CARD_IMAGE_HEIGHT, value.coerceIn(72f, 220f))

    suspend fun setNoteCardOutlineEnabled(value: Boolean) =
        writePreference(NOTE_CARD_OUTLINE_ENABLED, value)

    suspend fun setNoteCardOutlineWidth(value: Float) {
        val normalized = value.coerceIn(0f, 6f)
        context.dataStore.edit {
            it[NOTE_CARD_OUTLINE_WIDTH] = normalized
            // v38 removes the visible switch. Zero thickness is now the
            // disabled state, while any positive width enables the outline.
            it[NOTE_CARD_OUTLINE_ENABLED] = normalized > 0.01f
        }
    }
    suspend fun setNoteTitleMaxLines(value: Int) =
        writePreference(NOTE_TITLE_MAX_LINES, value.coerceIn(1, 8))

    suspend fun setNoteContentMaxLines(value: Int) =
        writePreference(NOTE_CONTENT_MAX_LINES, value.coerceIn(2, 14))

    suspend fun setNoteLineSpacing(value: Float) =
        writePreference(NOTE_LINE_SPACING, value.coerceIn(1f, 1.8f))

    suspend fun setShowNoteDate(value: Boolean) =
        writePreference(SHOW_NOTE_DATE, value)

    suspend fun setShowCategoryChip(value: Boolean) =
        writePreference(SHOW_CATEGORY_CHIP, value)

    suspend fun setShowFavoriteIcon(value: Boolean) =
        writePreference(SHOW_FAVORITE_ICON, value)

    suspend fun setFabSize(value: Float) =
        writePreference(FAB_SIZE, value.coerceIn(48f, 82f))

    suspend fun setOptionMenuOrder(value: String) =
        writePreference(OPTION_MENU_ORDER, MenuPreferencePolicy.normalizeOrder(value))

    suspend fun setOptionMenuHiddenItems(value: String) =
        writePreference(OPTION_MENU_HIDDEN_ITEMS, MenuPreferencePolicy.normalizeHiddenItems(value, MenuPreferencePolicy.mainKeySet))

    suspend fun setOptionMenuShowIcons(value: Boolean) =
        writePreference(OPTION_MENU_SHOW_ICONS, value)

    suspend fun setOptionMenuTextColor(value: String) =
        writePreference(OPTION_MENU_TEXT_COLOR, normalizeOptionMenuTextColor(value))

    suspend fun setOptionMenuOpacity(value: Float) =
        writePreference(OPTION_MENU_OPACITY, value.coerceIn(35f, 100f))

    suspend fun setPriorityMenuHiddenItems(value: String) =
        writePreference(PRIORITY_MENU_HIDDEN_ITEMS, MenuPreferencePolicy.normalizeHiddenItems(value, MenuPreferencePolicy.priorityKeys))

    suspend fun setColorMenuHiddenItems(value: String) =
        writePreference(COLOR_MENU_HIDDEN_ITEMS, MenuPreferencePolicy.normalizeHiddenItems(value, MenuPreferencePolicy.colorKeys))

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
    suspend fun setPerformanceMode(value: String) =
        writePreference(PERFORMANCE_MODE, normalizePerformanceMode(value))

    suspend fun setAnimationsEnabled(value: Boolean) =
        writePreference(ANIMATIONS_ENABLED, value)

    suspend fun setAnimationSpeed(value: Float) =
        writePreference(ANIMATION_SPEED, value.coerceIn(0.5f, 2f))

    suspend fun setAnimationStyle(value: String) =
        writePreference(ANIMATION_STYLE, normalizeAnimationStyle(value))

    suspend fun setAnimationEasing(value: String) =
        writePreference(ANIMATION_EASING, normalizeAnimationEasing(value))

    suspend fun setAnimationIntensity(value: Float) =
        writePreference(ANIMATION_INTENSITY, value.coerceIn(0.5f, 1.5f))

    /**
     * Restaura todas las preferencias de una copia de seguridad en una
     * sola transacción de DataStore. Los mismos límites y normalizadores
     * usados por los setters normales se aplican aquí.
     */
    suspend fun restoreFromBackup(value: AppSettings) {
        context.dataStore.edit { preferences -> preferences[CONFIGURATION_MODE] = normalizeConfigurationMode(value.configurationMode)
            preferences[DARK_MODE] = value.darkMode
            preferences[BACKGROUND_COLOR] = normalizePaletteKey(value.backgroundColor)
            preferences[BACKGROUND_TONE_INDEX] = value.backgroundToneIndex.coerceIn(0, 3)
            preferences[BACKGROUND_INTENSITY] = value.backgroundIntensity.coerceIn(0f, 100f)
            preferences[SETTINGS_PANEL_TONE] = value.settingsPanelTone.coerceIn(0f, 100f)
            preferences[SURFACE_PANEL_INTENSITY] = value.surfacePanelIntensity.coerceIn(0f, 100f)
            preferences[HEADER_INTENSITY] = value.headerIntensity.coerceIn(0f, 100f)
            preferences[TEXT_COLOR] = normalizeUiTextColor(value.textColor)
            preferences[TEXT_OUTLINE_ENABLED] = value.textOutlineEnabled
            preferences[SLIDER_STYLE] = normalizeSliderStyle(value.sliderStyle)
            preferences[FONT] = FontPreferencePolicy.normalize(
                value.font,
                googleSansFlexUnlocked = DeveloperFeatures.isGoogleSansFlexUnlocked(context)
            )
            preferences[FONT_SIZE] = value.fontSize.coerceIn(12f, 28f)
            preferences[SOUND_EFFECTS_ENABLED] = value.soundEffectsEnabled
            preferences[SOUND_EFFECTS_VOLUME] = value.soundEffectsVolume.coerceIn(0f, 100f)
            preferences[SOUND_EFFECTS_THEME] = FeedbackPreferencePolicy.normalizeSoundTheme(value.soundEffectsTheme)
            preferences[REMINDER_SOUND_ENABLED] = value.reminderSoundEnabled
            preferences[REMINDER_SOUND_VOLUME] = value.reminderSoundVolume.coerceIn(0f, 100f)
            preferences[REMINDER_RINGTONE] = FeedbackPreferencePolicy.normalizeReminderRingtone(value.reminderRingtone)
            preferences[HAPTIC_EFFECTS_ENABLED] = value.hapticEffectsEnabled
            preferences[HAPTIC_EFFECTS_INTENSITY] = value.hapticEffectsIntensity.coerceIn(0f, 100f)
            preferences[HAPTIC_EFFECTS_STYLE] = FeedbackPreferencePolicy.normalizeHapticStyle(value.hapticEffectsStyle)
            preferences[LANGUAGE] = value.language
            preferences[GRID_COLUMNS] = value.gridColumns.coerceIn(1, 3)
            preferences[SORT_ORDER] = value.sortOrder
            preferences[PROFILE_IMAGE_URI] = value.profileImageUri
            preferences[PROFILE_IMAGE_SIZE] = value.profileImageSize.coerceIn(36f, 84f)
            preferences[ICON_STYLE] = normalizeIconStyle(value.iconStyle)
            preferences[ICON_SIZE] = value.iconSize.coerceIn(16f, 36f)
            preferences[ACCENT_COLOR] = normalizeAccentColor(value.accentColor)
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
            preferences[OPTION_MENU_TEXT_COLOR] = normalizeOptionMenuTextColor(value.optionMenuTextColor)
            preferences[OPTION_MENU_OPACITY] = value.optionMenuOpacity.coerceIn(35f, 100f)
            preferences[PRIORITY_MENU_HIDDEN_ITEMS] = MenuPreferencePolicy.normalizeHiddenItems(value.priorityMenuHiddenItems, MenuPreferencePolicy.priorityKeys)
            preferences[COLOR_MENU_HIDDEN_ITEMS] = MenuPreferencePolicy.normalizeHiddenItems(value.colorMenuHiddenItems, MenuPreferencePolicy.colorKeys)
            preferences[PERFORMANCE_MODE] = normalizePerformanceMode(value.performanceMode)
            preferences[ANIMATIONS_ENABLED] = value.animationsEnabled
            preferences[ANIMATION_SPEED] = value.animationSpeed.coerceIn(0.5f, 2f)
            preferences[ANIMATION_STYLE] = normalizeAnimationStyle(value.animationStyle)
            preferences[ANIMATION_EASING] = normalizeAnimationEasing(value.animationEasing)
            preferences[ANIMATION_INTENSITY] = value.animationIntensity.coerceIn(0.5f, 1.5f)
        }
    }
    private fun normalizeOptionMenuTextColor(value: String): String = when (value) {
            "note", "black", "white" -> value
            else -> "note"
        }
    private fun normalizeConfigurationMode(value: String): String = when (value) {
            "basic", "advanced", "unset" -> value
            else -> "unset"
        }
    private fun normalizePerformanceMode(value: String): String = when (value) {
            "performance", "balanced", "quality" -> value
            else -> "balanced"
        }
    private fun normalizeAnimationStyle(value: String): String {
        return when (value) {
            "zoom", "zoom_fade", "fade", "slide_left", "slide_right", "slide_up", "slide_down", "slide_zoom_left", "slide_zoom_up",
            "axis_x", "axis_y", "axis_z", "expand", "expand_horizontal", "expand_vertical", "bounce", "elastic", "pop", "subtle",
            "expressive_spring", "container_transform", "soft_reveal", "elastic_slide", "predictive", "tonal_pop", "random" -> value
            else -> "zoom"
        }
    }
    private fun normalizeAnimationEasing(value: String): String {
        return when (value) {
            "standard", "linear", "accelerate", "decelerate", "emphasized", "expressive", "emphasized_accel", "emphasized_decel" -> value
            else -> "standard"
        }
    }
    /*
     * ---------------------------------------------------------
     * Compatibilidad de paletas anteriores
     * ---------------------------------------------------------
     */
    private fun normalizePaletteKey(value: String): String {
        if (value in currentPaletteKeys) {
            return value
        }
        return when (value) {
            "yellow" -> "sun"
            "purple" -> "lavender"
            "pink" -> "soft_pink"
            "default", "gray" -> "neutral"
            else -> "neutral"
        }
    }
    private fun isLegacyPalette(value: String): Boolean = value in legacyPaletteKeys
    private fun normalizeUiTextColor(value: String): String {
        return when (value) {
            "auto", "black", "white" -> value
            else -> "auto"
        }
    }
    private fun normalizeIconStyle(value: String): String {
        return when (value) {
            "material", "rounded", "outlined", "minimal" -> value
            else -> "rounded"
        }
    }
    private fun normalizeAccentColor(value: String): String {
        return when (value) {
            "palette", "red", "coral", "orange", "amber", "yellow", "lime", "green", "mint", "teal", "cyan", "sky", "blue", "indigo",
            "violet", "purple", "pink", "rose", "brown", "graphite" -> value
            else -> "palette"
        }
    }
    private fun normalizeSliderStyle(value: String): String {
        return when (value) {
            "minimal", "capsule", "glow", "glass", "segmented", "dots", "gradient", "neumorphic", "line_pill", "floating" -> value
            else -> "capsule"
        }
    }
}

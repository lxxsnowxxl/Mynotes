package com.example.mynotes.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mynotes.settings.AppSettings
import com.example.mynotes.settings.SettingsRepository
import com.example.mynotes.widget.MyNotesWidgetUpdater
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = SettingsRepository(application)
    val settings:
        StateFlow<AppSettings> =
        repository.settings.stateIn(scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                // "loading" evita que el diálogo de primer inicio parpadee
                // antes de que DataStore entregue el modo ya guardado.
                initialValue = AppSettings(configurationMode = "loading"))
    /**
     * Evita lanzar una coroutine y abrir una transacción de DataStore cuando
     * el valor solicitado ya está aplicado. Esto es especialmente importante
     * para sliders, switches y menús que pueden reenviar el mismo valor durante
     * recomposiciones o gestos rápidos.
     */
    private fun <T> launchIfChanged(
        current: T,
        requested: T,
        update: suspend (T) -> Unit,
        refreshWidgets: Boolean = false
    ) {
        if (current == requested) return
        viewModelScope.launch {
            update(requested)
            if (refreshWidgets) MyNotesWidgetUpdater.requestUpdate(getApplication<Application>())
        }
    }

    fun setConfigurationMode(value: String) =
        launchIfChanged(settings.value.configurationMode, value, repository::setConfigurationMode)

    fun setDarkMode(value: Boolean) =
        launchIfChanged(settings.value.darkMode, value, repository::setDarkMode, refreshWidgets = true)

    fun setBackgroundColor(value: String) =
        launchIfChanged(settings.value.backgroundColor, value, repository::setBackgroundColor, refreshWidgets = true)

    fun setBackgroundToneIndex(value: Int) =
        launchIfChanged(settings.value.backgroundToneIndex, value.coerceIn(0, 3), repository::setBackgroundToneIndex, refreshWidgets = true)

    fun setBackgroundIntensity(value: Float) =
        launchIfChanged(settings.value.backgroundIntensity, value.coerceIn(0f, 100f), repository::setBackgroundIntensity, refreshWidgets = true)

    fun setSettingsPanelTone(value: Float) =
        launchIfChanged(settings.value.settingsPanelTone, value.coerceIn(0f, 100f), repository::setSettingsPanelTone)

    fun setSurfacePanelIntensity(value: Float) =
        launchIfChanged(settings.value.surfacePanelIntensity, value.coerceIn(0f, 100f), repository::setSurfacePanelIntensity, refreshWidgets = true)

    fun setHeaderIntensity(value: Float) =
        launchIfChanged(settings.value.headerIntensity, value.coerceIn(0f, 100f), repository::setHeaderIntensity)

    fun setTextColor(value: String) =
        launchIfChanged(settings.value.textColor, value, repository::setTextColor)

    fun setTextOutlineEnabled(value: Boolean) =
        launchIfChanged(settings.value.textOutlineEnabled, value, repository::setTextOutlineEnabled)

    fun setSliderStyle(value: String) =
        launchIfChanged(settings.value.sliderStyle, value, repository::setSliderStyle)

    fun setFont(value: String) =
        launchIfChanged(settings.value.font, value, repository::setFont)

    fun setFontSize(value: Float) =
        launchIfChanged(settings.value.fontSize, value.coerceIn(12f, 28f), repository::setFontSize)

    fun setSoundEffectsEnabled(value: Boolean) =
        launchIfChanged(settings.value.soundEffectsEnabled, value, repository::setSoundEffectsEnabled)

    fun setSoundEffectsVolume(value: Float) =
        launchIfChanged(settings.value.soundEffectsVolume, value.coerceIn(0f, 100f), repository::setSoundEffectsVolume)

    fun setSoundEffectsTheme(value: String) =
        launchIfChanged(settings.value.soundEffectsTheme, value, repository::setSoundEffectsTheme)

    fun setReminderSoundEnabled(value: Boolean) =
        launchIfChanged(settings.value.reminderSoundEnabled, value, repository::setReminderSoundEnabled)

    fun setReminderSoundVolume(value: Float) =
        launchIfChanged(settings.value.reminderSoundVolume, value.coerceIn(0f, 100f), repository::setReminderSoundVolume)

    fun setReminderRingtone(value: String) =
        launchIfChanged(settings.value.reminderRingtone, value, repository::setReminderRingtone)

    fun setHapticEffectsEnabled(value: Boolean) =
        launchIfChanged(settings.value.hapticEffectsEnabled, value, repository::setHapticEffectsEnabled)

    fun setHapticEffectsIntensity(value: Float) =
        launchIfChanged(settings.value.hapticEffectsIntensity, value.coerceIn(0f, 100f), repository::setHapticEffectsIntensity)

    fun setHapticEffectsStyle(value: String) =
        launchIfChanged(settings.value.hapticEffectsStyle, value, repository::setHapticEffectsStyle)

    fun setLanguage(value: String) =
        launchIfChanged(settings.value.language, value, repository::setLanguage, refreshWidgets = true)

    fun setGridColumns(value: Int) =
        launchIfChanged(settings.value.gridColumns, value.coerceIn(1, 3), repository::setGridColumns)

    fun setSortOrder(value: String) =
        launchIfChanged(settings.value.sortOrder, value, repository::setSortOrder)

    fun setProfileImageUri(value: String) =
        launchIfChanged(settings.value.profileImageUri, value, repository::setProfileImageUri)

    fun setProfileImageSize(value: Float) =
        launchIfChanged(settings.value.profileImageSize, value.coerceIn(36f, 84f), repository::setProfileImageSize)

    fun setIconStyle(value: String) =
        launchIfChanged(settings.value.iconStyle, value, repository::setIconStyle)

    fun setIconSize(value: Float) =
        launchIfChanged(settings.value.iconSize, value.coerceIn(16f, 36f), repository::setIconSize)

    fun setAccentColor(value: String) =
        launchIfChanged(settings.value.accentColor, value, repository::setAccentColor)

    fun setNoteCardCornerRadius(value: Float) =
        launchIfChanged(settings.value.noteCardCornerRadius, value.coerceIn(0f, 36f), repository::setNoteCardCornerRadius)

    fun setNoteCardElevation(value: Float) =
        launchIfChanged(settings.value.noteCardElevation, value.coerceIn(0f, 12f), repository::setNoteCardElevation)

    fun setNoteCardPadding(value: Float) =
        launchIfChanged(settings.value.noteCardPadding, value.coerceIn(6f, 24f), repository::setNoteCardPadding)

    fun setNoteCardImageHeight(value: Float) =
        launchIfChanged(settings.value.noteCardImageHeight, value.coerceIn(72f, 220f), repository::setNoteCardImageHeight)

    fun setNoteCardOutlineWidth(value: Float) =
        launchIfChanged(settings.value.noteCardOutlineWidth, value.coerceIn(0f, 6f), repository::setNoteCardOutlineWidth)

    fun setNoteTitleMaxLines(value: Int) =
        launchIfChanged(settings.value.noteTitleMaxLines, value.coerceIn(1, 8), repository::setNoteTitleMaxLines)

    fun setNoteContentMaxLines(value: Int) =
        launchIfChanged(settings.value.noteContentMaxLines, value.coerceIn(2, 14), repository::setNoteContentMaxLines)

    fun setNoteLineSpacing(value: Float) =
        launchIfChanged(settings.value.noteLineSpacing, value.coerceIn(1f, 1.8f), repository::setNoteLineSpacing)

    fun setShowNoteDate(value: Boolean) =
        launchIfChanged(settings.value.showNoteDate, value, repository::setShowNoteDate)

    fun setShowCategoryChip(value: Boolean) =
        launchIfChanged(settings.value.showCategoryChip, value, repository::setShowCategoryChip)

    fun setShowFavoriteIcon(value: Boolean) =
        launchIfChanged(settings.value.showFavoriteIcon, value, repository::setShowFavoriteIcon)

    fun setFabSize(value: Float) =
        launchIfChanged(settings.value.fabSize, value.coerceIn(48f, 82f), repository::setFabSize)

    fun setOptionMenuOrder(value: String) =
        launchIfChanged(settings.value.optionMenuOrder, value, repository::setOptionMenuOrder)

    fun setOptionMenuHiddenItems(value: String) =
        launchIfChanged(settings.value.optionMenuHiddenItems, value, repository::setOptionMenuHiddenItems)

    fun setOptionMenuShowIcons(value: Boolean) =
        launchIfChanged(settings.value.optionMenuShowIcons, value, repository::setOptionMenuShowIcons)

    fun setOptionMenuTextColor(value: String) =
        launchIfChanged(settings.value.optionMenuTextColor, value, repository::setOptionMenuTextColor)

    fun setOptionMenuOpacity(value: Float) =
        launchIfChanged(settings.value.optionMenuOpacity, value.coerceIn(35f, 100f), repository::setOptionMenuOpacity)

    fun setPriorityMenuHiddenItems(value: String) =
        launchIfChanged(settings.value.priorityMenuHiddenItems, value, repository::setPriorityMenuHiddenItems)

    fun setColorMenuHiddenItems(value: String) =
        launchIfChanged(settings.value.colorMenuHiddenItems, value, repository::setColorMenuHiddenItems)

    fun resetOptionMenuSettings() {
        viewModelScope.launch {
            repository.resetOptionMenuSettings()
        }
    }
    fun setPerformanceMode(value: String) =
        launchIfChanged(settings.value.performanceMode, value, repository::setPerformanceMode)

    fun setAnimationsEnabled(value: Boolean) =
        launchIfChanged(settings.value.animationsEnabled, value, repository::setAnimationsEnabled)

    fun setAnimationSpeed(value: Float) =
        launchIfChanged(settings.value.animationSpeed, value.coerceIn(0.5f, 2f), repository::setAnimationSpeed)

    fun setAnimationStyle(value: String) =
        launchIfChanged(settings.value.animationStyle, value, repository::setAnimationStyle)

    fun setAnimationEasing(value: String) =
        launchIfChanged(settings.value.animationEasing, value, repository::setAnimationEasing)

    fun setAnimationIntensity(value: Float) =
        launchIfChanged(settings.value.animationIntensity, value.coerceIn(0.5f, 1.5f), repository::setAnimationIntensity)

}

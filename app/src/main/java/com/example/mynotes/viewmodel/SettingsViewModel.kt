package com.example.mynotes.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mynotes.ui.state.SettingsActions
import com.example.mynotes.settings.AppSettings
import com.example.mynotes.settings.SettingsRepository
import com.example.mynotes.widget.MyNotesWidgetUpdater
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application), SettingsActions {
    private val repository = SettingsRepository(application)
    val settings:
        StateFlow<AppSettings> =
        repository.settings.stateIn(scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                // "loading" evita que el diálogo de primer inicio parpadee
                // antes de que DataStore entregue el modo ya guardado.
                initialValue = AppSettings(configurationMode = "loading"))
    /** DataStore decide si cambió el valor; la UI puede seguir mostrando una emisión anterior. */
    private fun updateSetting(refreshWidgets: Boolean = false, update: suspend () -> Boolean) {
        viewModelScope.launch {
            val changed = update()
            if (changed && refreshWidgets) MyNotesWidgetUpdater.requestUpdate(getApplication<Application>())
        }
    }

    override fun setConfigurationMode(value: String) = updateSetting { repository.setConfigurationMode(value) }

    override fun setDarkMode(value: Boolean) = updateSetting(refreshWidgets = true) { repository.setDarkMode(value) }

    override fun setBackgroundSelection(paletteKey: String, toneIndex: Int) = updateSetting(refreshWidgets = true) { repository.setBackgroundSelection(paletteKey, toneIndex) }

    override fun setSettingsPanelTone(value: Float) = updateSetting { repository.setSettingsPanelTone(value) }

    override fun setSettingsBackgroundStyle(value: String) = updateSetting { repository.setSettingsBackgroundStyle(value) }

    override fun setSurfacePanelIntensity(value: Float) = updateSetting { repository.setSurfacePanelIntensity(value) }

    override fun setTextColor(value: String) = updateSetting { repository.setTextColor(value) }

    override fun setTextOutlineEnabled(value: Boolean) = updateSetting { repository.setTextOutlineEnabled(value) }

    override fun setSliderStyle(value: String) = updateSetting { repository.setSliderStyle(value) }

    override fun setFont(value: String) = updateSetting { repository.setFont(value) }

    override fun setFontSize(value: Float) = updateSetting { repository.setFontSize(value) }

    override fun setSoundEffectsEnabled(value: Boolean) = updateSetting { repository.setSoundEffectsEnabled(value) }

    override fun setSoundEffectsVolume(value: Float) = updateSetting { repository.setSoundEffectsVolume(value) }

    override fun setSoundEffectsTheme(value: String) = updateSetting { repository.setSoundEffectsTheme(value) }

    override fun setReminderSoundEnabled(value: Boolean) = updateSetting { repository.setReminderSoundEnabled(value) }

    override fun setReminderSoundVolume(value: Float) = updateSetting { repository.setReminderSoundVolume(value) }

    override fun setReminderRingtone(value: String) = updateSetting { repository.setReminderRingtone(value) }

    override fun setHapticEffectsEnabled(value: Boolean) = updateSetting { repository.setHapticEffectsEnabled(value) }

    override fun setHapticEffectsIntensity(value: Float) = updateSetting { repository.setHapticEffectsIntensity(value) }

    override fun setHapticEffectsStyle(value: String) = updateSetting { repository.setHapticEffectsStyle(value) }

    override fun setLanguage(value: String) = updateSetting(refreshWidgets = true) { repository.setLanguage(value) }

    override fun setGridColumns(value: Int) = updateSetting { repository.setGridColumns(value) }

    override fun setSortOrder(value: String) = updateSetting { repository.setSortOrder(value) }

    override fun setProfileImageUri(value: String) = updateSetting { repository.setProfileImageUri(value) }

    override fun setProfileImageSize(value: Float) = updateSetting { repository.setProfileImageSize(value) }

    override fun setIconStyle(value: String) = updateSetting { repository.setIconStyle(value) }

    override fun setIconSize(value: Float) = updateSetting { repository.setIconSize(value) }

    override fun setAccentColor(value: String) = updateSetting { repository.setAccentColor(value) }

    override fun setNoteCardCornerRadius(value: Float) = updateSetting { repository.setNoteCardCornerRadius(value) }

    override fun setNoteCardElevation(value: Float) = updateSetting { repository.setNoteCardElevation(value) }

    override fun setNoteCardPadding(value: Float) = updateSetting { repository.setNoteCardPadding(value) }

    override fun setNoteCardImageHeight(value: Float) = updateSetting { repository.setNoteCardImageHeight(value) }

    override fun setNoteCardOutlineWidth(value: Float) = updateSetting { repository.setNoteCardOutlineWidth(value) }

    override fun setNoteTitleMaxLines(value: Int) = updateSetting { repository.setNoteTitleMaxLines(value) }

    override fun setNoteContentMaxLines(value: Int) = updateSetting { repository.setNoteContentMaxLines(value) }

    override fun setNoteLineSpacing(value: Float) = updateSetting { repository.setNoteLineSpacing(value) }

    override fun setShowNoteDate(value: Boolean) = updateSetting { repository.setShowNoteDate(value) }

    override fun setShowCategoryChip(value: Boolean) = updateSetting { repository.setShowCategoryChip(value) }

    override fun setShowFavoriteIcon(value: Boolean) = updateSetting { repository.setShowFavoriteIcon(value) }

    override fun setFabSize(value: Float) = updateSetting { repository.setFabSize(value) }

    override fun setOptionMenuOrder(value: String) = updateSetting { repository.setOptionMenuOrder(value) }

    override fun setOptionMenuHiddenItems(value: String) = updateSetting { repository.setOptionMenuHiddenItems(value) }

    override fun setOptionMenuShowIcons(value: Boolean) = updateSetting { repository.setOptionMenuShowIcons(value) }

    override fun setOptionMenuTextColor(value: String) = updateSetting { repository.setOptionMenuTextColor(value) }

    override fun setOptionMenuOpacity(value: Float) = updateSetting { repository.setOptionMenuOpacity(value) }

    override fun setPriorityMenuHiddenItems(value: String) = updateSetting { repository.setPriorityMenuHiddenItems(value) }

    override fun setColorMenuHiddenItems(value: String) = updateSetting { repository.setColorMenuHiddenItems(value) }

    override fun setPerformanceMode(value: String) = updateSetting { repository.setPerformanceMode(value) }

    override fun setAnimationsEnabled(value: Boolean) = updateSetting { repository.setAnimationsEnabled(value) }

    override fun setAnimationSpeed(value: Float) = updateSetting { repository.setAnimationSpeed(value) }

    override fun setAnimationStyle(value: String) = updateSetting { repository.setAnimationStyle(value) }

    override fun setAnimationEasing(value: String) = updateSetting { repository.setAnimationEasing(value) }

    override fun setAnimationIntensity(value: Float) = updateSetting { repository.setAnimationIntensity(value) }
}

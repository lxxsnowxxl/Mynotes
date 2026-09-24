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
    fun setConfigurationMode(value: String) {
        viewModelScope.launch {
            repository.setConfigurationMode(value)
        }
    }
    fun setDarkMode(value: Boolean) {
        viewModelScope.launch {
            repository.setDarkMode(value)
            MyNotesWidgetUpdater.requestUpdate(getApplication<Application>())
        }
    }
    fun setBackgroundColor(value: String) {
        viewModelScope.launch {
            repository.setBackgroundColor(value)
            MyNotesWidgetUpdater.requestUpdate(getApplication<Application>())
        }
    }
    fun setBackgroundToneIndex(value: Int) {
        viewModelScope.launch {
            repository.setBackgroundToneIndex(value)
            MyNotesWidgetUpdater.requestUpdate(getApplication<Application>())
        }
    }
    fun setBackgroundIntensity(value: Float) {
        val normalized = value.coerceIn(0f, 100f)
        if (settings.value.backgroundIntensity == normalized) return
        viewModelScope.launch {
            repository.setBackgroundIntensity(normalized)
            MyNotesWidgetUpdater.requestUpdate(getApplication<Application>())
        }
    }
    fun setSettingsPanelTone(value: Float) {
        val normalized = value.coerceIn(0f, 100f)
        if (settings.value.settingsPanelTone == normalized) return
        viewModelScope.launch { repository.setSettingsPanelTone(normalized) }
    }
    fun setSurfacePanelIntensity(value: Float) {
        val normalized = value.coerceIn(0f, 100f)
        if (settings.value.surfacePanelIntensity == normalized) return
        viewModelScope.launch {
            repository.setSurfacePanelIntensity(normalized)
            MyNotesWidgetUpdater.requestUpdate(getApplication<Application>())
        }
    }
    fun setHeaderIntensity(value: Float) {
        val normalized = value.coerceIn(0f, 100f)
        if (settings.value.headerIntensity == normalized) return
        viewModelScope.launch { repository.setHeaderIntensity(normalized) }
    }
    fun setTextColor(value: String) {
        viewModelScope.launch {
            repository.setTextColor(value)
        }
    }
    fun setTextOutlineEnabled(value: Boolean) {
        viewModelScope.launch {
            repository.setTextOutlineEnabled(value)
        }
    }
    fun setSliderStyle(value: String) {
        viewModelScope.launch {
            repository.setSliderStyle(value)
        }
    }
    fun setFont(value: String) {
        viewModelScope.launch {
            repository.setFont(value)
        }
    }
    fun setFontSize(value: Float) {
        val normalized = value.coerceIn(12f, 28f)
        if (settings.value.fontSize == normalized) return
        viewModelScope.launch { repository.setFontSize(normalized) }
    }
    fun setSoundEffectsEnabled(value: Boolean) {
        viewModelScope.launch {
            repository.setSoundEffectsEnabled(value)
        }
    }
    fun setSoundEffectsVolume(value: Float) {
        val normalized = value.coerceIn(0f, 100f)
        if (settings.value.soundEffectsVolume == normalized) return
        viewModelScope.launch { repository.setSoundEffectsVolume(normalized) }
    }
    fun setSoundEffectsTheme(value: String) {
        viewModelScope.launch {
            repository.setSoundEffectsTheme(value)
        }
    }
    fun setReminderSoundEnabled(value: Boolean) {
        viewModelScope.launch {
            repository.setReminderSoundEnabled(value)
        }
    }
    fun setReminderSoundVolume(value: Float) {
        val normalized = value.coerceIn(0f, 100f)
        if (settings.value.reminderSoundVolume == normalized) return
        viewModelScope.launch { repository.setReminderSoundVolume(normalized) }
    }
    fun setReminderRingtone(value: String) {
        viewModelScope.launch {
            repository.setReminderRingtone(value)
        }
    }
    fun setHapticEffectsEnabled(value: Boolean) {
        viewModelScope.launch {
            repository.setHapticEffectsEnabled(value)
        }
    }
    fun setHapticEffectsIntensity(value: Float) {
        val normalized = value.coerceIn(0f, 100f)
        if (settings.value.hapticEffectsIntensity == normalized) return
        viewModelScope.launch { repository.setHapticEffectsIntensity(normalized) }
    }
    fun setHapticEffectsStyle(value: String) {
        viewModelScope.launch {
            repository.setHapticEffectsStyle(value)
        }
    }
    fun setLanguage(value: String) {
        viewModelScope.launch {
            repository.setLanguage(value)
            MyNotesWidgetUpdater.requestUpdate(getApplication<Application>())
        }
    }
    fun setGridColumns(value: Int) {
        viewModelScope.launch {
            repository.setGridColumns(value)
        }
    }
    fun setSortOrder(value: String) {
        viewModelScope.launch {
            repository.setSortOrder(value)
        }
    }
    fun setProfileImageUri(value: String) {
        viewModelScope.launch {
            repository.setProfileImageUri(value)
        }
    }
    fun setProfileImageSize(value: Float) {
        val normalized = value.coerceIn(36f, 84f)
        if (settings.value.profileImageSize == normalized) return
        viewModelScope.launch { repository.setProfileImageSize(normalized) }
    }
    fun setIconStyle(value: String) {
        viewModelScope.launch {
            repository.setIconStyle(value)
        }
    }
    fun setIconSize(value: Float) {
        val normalized = value.coerceIn(16f, 36f)
        if (settings.value.iconSize == normalized) return
        viewModelScope.launch { repository.setIconSize(normalized) }
    }
    fun setAccentColor(value: String) {
        viewModelScope.launch {
            repository.setAccentColor(value)
        }
    }
    fun setNoteCardCornerRadius(value: Float) {
        val normalized = value.coerceIn(0f, 36f)
        if (settings.value.noteCardCornerRadius == normalized) return
        viewModelScope.launch { repository.setNoteCardCornerRadius(normalized) }
    }
    fun setNoteCardElevation(value: Float) {
        val normalized = value.coerceIn(0f, 12f)
        if (settings.value.noteCardElevation == normalized) return
        viewModelScope.launch { repository.setNoteCardElevation(normalized) }
    }
    fun setNoteCardPadding(value: Float) {
        val normalized = value.coerceIn(6f, 24f)
        if (settings.value.noteCardPadding == normalized) return
        viewModelScope.launch { repository.setNoteCardPadding(normalized) }
    }
    fun setNoteCardImageHeight(value: Float) {
        val normalized = value.coerceIn(72f, 220f)
        if (settings.value.noteCardImageHeight == normalized) return
        viewModelScope.launch { repository.setNoteCardImageHeight(normalized) }
    }
    fun setNoteCardOutlineWidth(value: Float) {
        val normalized = value.coerceIn(0f, 6f)
        if (settings.value.noteCardOutlineWidth == normalized) return
        viewModelScope.launch { repository.setNoteCardOutlineWidth(normalized) }
    }
    fun setNoteTitleMaxLines(value: Int) {
        viewModelScope.launch {
            repository.setNoteTitleMaxLines(value)
        }
    }
    fun setNoteContentMaxLines(value: Int) {
        viewModelScope.launch {
            repository.setNoteContentMaxLines(value)
        }
    }
    fun setNoteLineSpacing(value: Float) {
        val normalized = value.coerceIn(1f, 1.8f)
        if (settings.value.noteLineSpacing == normalized) return
        viewModelScope.launch { repository.setNoteLineSpacing(normalized) }
    }
    fun setShowNoteDate(value: Boolean) {
        viewModelScope.launch {
            repository.setShowNoteDate(value)
        }
    }
    fun setShowCategoryChip(value: Boolean) {
        viewModelScope.launch {
            repository.setShowCategoryChip(value)
        }
    }
    fun setShowFavoriteIcon(value: Boolean) {
        viewModelScope.launch {
            repository.setShowFavoriteIcon(value)
        }
    }
    fun setFabSize(value: Float) {
        val normalized = value.coerceIn(48f, 82f)
        if (settings.value.fabSize == normalized) return
        viewModelScope.launch { repository.setFabSize(normalized) }
    }
    fun setOptionMenuOrder(value: String) {
        viewModelScope.launch {
            repository.setOptionMenuOrder(value)
        }
    }
    fun setOptionMenuHiddenItems(value: String) {
        viewModelScope.launch {
            repository.setOptionMenuHiddenItems(value)
        }
    }
    fun setOptionMenuShowIcons(value: Boolean) {
        viewModelScope.launch {
            repository.setOptionMenuShowIcons(value)
        }
    }
    fun setOptionMenuTextColor(value: String) {
        viewModelScope.launch {
            repository.setOptionMenuTextColor(value)
        }
    }
    fun setOptionMenuOpacity(value: Float) {
        viewModelScope.launch {
            repository.setOptionMenuOpacity(value)
        }
    }
    fun setPriorityMenuHiddenItems(value: String) {
        viewModelScope.launch {
            repository.setPriorityMenuHiddenItems(value)
        }
    }
    fun setColorMenuHiddenItems(value: String) {
        viewModelScope.launch {
            repository.setColorMenuHiddenItems(value)
        }
    }
    fun resetOptionMenuSettings() {
        viewModelScope.launch {
            repository.resetOptionMenuSettings()
        }
    }
    fun setPerformanceMode(value: String) {
        viewModelScope.launch {
            repository.setPerformanceMode(value)
        }
    }
    fun setAnimationsEnabled(value: Boolean) {
        viewModelScope.launch {
            repository.setAnimationsEnabled(value)
        }
    }
    fun setAnimationSpeed(value: Float) {
        val normalized = value.coerceIn(0.5f, 2f)
        if (settings.value.animationSpeed == normalized) return
        viewModelScope.launch { repository.setAnimationSpeed(normalized) }
    }
    fun setAnimationStyle(value: String) {
        viewModelScope.launch {
            repository.setAnimationStyle(value)
        }
    }
    fun setAnimationEasing(value: String) {
        viewModelScope.launch {
            repository.setAnimationEasing(value)
        }
    }
    fun setAnimationIntensity(value: Float) {
        val normalized = value.coerceIn(0.5f, 1.5f)
        if (settings.value.animationIntensity == normalized) return
        viewModelScope.launch { repository.setAnimationIntensity(normalized) }
    }
}

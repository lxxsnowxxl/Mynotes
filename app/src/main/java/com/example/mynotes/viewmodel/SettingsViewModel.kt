package com.example.mynotes.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mynotes.settings.AppSettings
import com.example.mynotes.settings.SettingsRepository
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
                initialValue = AppSettings())
    fun setDarkMode(value: Boolean) {
        viewModelScope.launch {
            repository.setDarkMode(value)
        }
    }
    fun setBackgroundColor(value: String) {
        viewModelScope.launch {
            repository.setBackgroundColor(value)
        }
    }
    fun setBackgroundToneIndex(value: Int) {
        viewModelScope.launch {
            repository.setBackgroundToneIndex(value)
        }
    }
    fun setBackgroundIntensity(value: Float) {
        viewModelScope.launch {
            repository.setBackgroundIntensity(value)
        }
    }
    fun setSettingsPanelTone(value: Float) {
        viewModelScope.launch {
            repository.setSettingsPanelTone(value)
        }
    }
    fun setSurfacePanelIntensity(value: Float) {
        viewModelScope.launch {
            repository.setSurfacePanelIntensity(value)
        }
    }
    fun setHeaderIntensity(value: Float) {
        viewModelScope.launch {
            repository.setHeaderIntensity(value)
        }
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
    fun setNoteUiTextColor(value: String) {
        viewModelScope.launch {
            repository.setNoteUiTextColor(value)
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
        viewModelScope.launch {
            repository.setFontSize(value)
        }
    }
    fun setSoundEffectsEnabled(value: Boolean) {
        viewModelScope.launch {
            repository.setSoundEffectsEnabled(value)
        }
    }
    fun setSoundEffectsVolume(value: Float) {
        viewModelScope.launch {
            repository.setSoundEffectsVolume(value)
        }
    }
    fun setSoundEffectsTheme(value: String) {
        viewModelScope.launch {
            repository.setSoundEffectsTheme(value)
        }
    }
    fun setHapticEffectsEnabled(value: Boolean) {
        viewModelScope.launch {
            repository.setHapticEffectsEnabled(value)
        }
    }
    fun setHapticEffectsIntensity(value: Float) {
        viewModelScope.launch {
            repository.setHapticEffectsIntensity(value)
        }
    }
    fun setHapticEffectsStyle(value: String) {
        viewModelScope.launch {
            repository.setHapticEffectsStyle(value)
        }
    }
    fun setLanguage(value: String) {
        viewModelScope.launch {
            repository.setLanguage(value)
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
        viewModelScope.launch {
            repository.setProfileImageSize(value)
        }
    }
    fun setIconStyle(value: String) {
        viewModelScope.launch {
            repository.setIconStyle(value)
        }
    }
    fun setIconSize(value: Float) {
        viewModelScope.launch {
            repository.setIconSize(value)
        }
    }
    fun setAccentColor(value: String) {
        viewModelScope.launch {
            repository.setAccentColor(value)
        }
    }
    fun setNoteCardCornerRadius(value: Float) {
        viewModelScope.launch {
            repository.setNoteCardCornerRadius(value)
        }
    }
    fun setNoteCardElevation(value: Float) {
        viewModelScope.launch {
            repository.setNoteCardElevation(value)
        }
    }
    fun setNoteCardPadding(value: Float) {
        viewModelScope.launch {
            repository.setNoteCardPadding(value)
        }
    }
    fun setNoteCardImageHeight(value: Float) {
        viewModelScope.launch {
            repository.setNoteCardImageHeight(value)
        }
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
        viewModelScope.launch {
            repository.setNoteLineSpacing(value)
        }
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
        viewModelScope.launch {
            repository.setFabSize(value)
        }
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
        viewModelScope.launch {
            repository.setAnimationSpeed(value)
        }
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
        viewModelScope.launch {
            repository.setAnimationIntensity(value)
        }
    }
}

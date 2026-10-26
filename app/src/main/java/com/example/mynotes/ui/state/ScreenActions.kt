package com.example.mynotes.ui.state

import com.example.mynotes.data.Attachment
import com.example.mynotes.data.Note
import com.example.mynotes.reminders.Reminder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/** Eventos de la pantalla. El ViewModel conserva la persistencia; el editor visual usa eventos vacíos. */
interface SettingsActions {
    fun setConfigurationMode(value: String)
    fun setDarkMode(value: Boolean)
    fun setBackgroundSelection(paletteKey: String, toneIndex: Int)
    fun setSettingsPanelTone(value: Float)
    fun setSurfacePanelIntensity(value: Float)
    fun setTextColor(value: String)
    fun setTextOutlineEnabled(value: Boolean)
    fun setSliderStyle(value: String)
    fun setFont(value: String)
    fun setFontSize(value: Float)
    fun setSoundEffectsEnabled(value: Boolean)
    fun setSoundEffectsVolume(value: Float)
    fun setSoundEffectsTheme(value: String)
    fun setReminderSoundEnabled(value: Boolean)
    fun setReminderSoundVolume(value: Float)
    fun setReminderRingtone(value: String)
    fun setHapticEffectsEnabled(value: Boolean)
    fun setHapticEffectsIntensity(value: Float)
    fun setHapticEffectsStyle(value: String)
    fun setLanguage(value: String)
    fun setGridColumns(value: Int)
    fun setSortOrder(value: String)
    fun setProfileImageUri(value: String)
    fun setProfileImageSize(value: Float)
    fun setIconStyle(value: String)
    fun setIconSize(value: Float)
    fun setAccentColor(value: String)
    fun setNoteCardCornerRadius(value: Float)
    fun setNoteCardElevation(value: Float)
    fun setNoteCardPadding(value: Float)
    fun setNoteCardImageHeight(value: Float)
    fun setNoteCardOutlineWidth(value: Float)
    fun setNoteTitleMaxLines(value: Int)
    fun setNoteContentMaxLines(value: Int)
    fun setNoteLineSpacing(value: Float)
    fun setShowNoteDate(value: Boolean)
    fun setShowCategoryChip(value: Boolean)
    fun setShowFavoriteIcon(value: Boolean)
    fun setFabSize(value: Float)
    fun setOptionMenuOrder(value: String)
    fun setOptionMenuHiddenItems(value: String)
    fun setOptionMenuShowIcons(value: Boolean)
    fun setOptionMenuTextColor(value: String)
    fun setOptionMenuOpacity(value: Float)
    fun setPriorityMenuHiddenItems(value: String)
    fun setColorMenuHiddenItems(value: String)
    fun setPerformanceMode(value: String)
    fun setAnimationsEnabled(value: Boolean)
    fun setAnimationSpeed(value: Float)
    fun setAnimationStyle(value: String)
    fun setAnimationEasing(value: String)
    fun setAnimationIntensity(value: Float)
}

interface NoteActions {
    val allAttachments: StateFlow<List<Attachment>>
    fun getAttachments(noteId: Int): Flow<List<Attachment>>
    fun toggleFavorite(note: Note)
    fun togglePinned(note: Note)
    fun changePriority(note: Note, priority: Int)
    fun changeNoteColor(note: Note, color: String)
    fun changeCategory(note: Note, category: String)
    fun deleteNote(note: Note)
}

interface ReminderActions {
    val reminders: StateFlow<List<Reminder>>
    fun upsert(reminder: Reminder)
    fun delete(id: Long)
    fun setEnabled(id: Long, enabled: Boolean)
}

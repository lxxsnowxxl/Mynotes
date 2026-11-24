package com.example.mynotes.settings

import com.example.mynotes.ui.components.toNoteCardStyle
import org.junit.Assert.*
import org.junit.Test

class DesignPreferencePolicyTest {
    @Test fun presetsPreserveFeedbackLanguageAndCollectionPreferences() {
        val original = AppSettings(configurationMode = "basic", language = "es", reminderSoundVolume = 27f,
            soundEffectsVolume = 83f, reminderRingtone = "classic", hapticEffectsEnabled = false,
            gridColumns = 3, sortOrder = "oldest", profileImageUri = "content://profile/avatar",
            editorFocusMode = false)
        DesignPreferencePolicy.presets.forEach { key ->
            val result = DesignPreferencePolicy.applyPreset(original, key)
            assertEquals(original.configurationMode, result.configurationMode)
            assertEquals(original.language, result.language)
            assertEquals(original.reminderSoundVolume, result.reminderSoundVolume)
            assertEquals(original.soundEffectsVolume, result.soundEffectsVolume)
            assertEquals(original.hapticEffectsEnabled, result.hapticEffectsEnabled)
            assertEquals(original.gridColumns, result.gridColumns)
            assertEquals(original.sortOrder, result.sortOrder)
            assertEquals(original.profileImageUri, result.profileImageUri)
            assertEquals(original.editorFocusMode, result.editorFocusMode)
        }
    }

    @Test fun defaultRestoresOriginalAppearanceWithoutResettingUserPreferences() {
        val baseline = AppSettings()
        val customized = AppSettings(
            designPreset = "notebook", oledMode = true, darkMode = true,
            backgroundColor = "cream", font = FontPreferencePolicy.SERIF,
            fontSize = 22f, noteCardCornerRadius = 32f,
            noteCardElevation = 0f, noteCardPadding = 20f,
            noteContentMaxLines = 3, cardDensity = "compact",
            language = "es", soundEffectsVolume = 21f, reminderSoundVolume = 82f,
            profileImageUri = "content://avatar", editorFocusMode = false,
            sortOrder = "oldest", gridColumns = 3
        )
        val result = DesignPreferencePolicy.applyPreset(customized, "default")
        assertEquals("default", result.designPreset)
        assertEquals(baseline.darkMode, result.darkMode)
        assertEquals(baseline.oledMode, result.oledMode)
        assertEquals(baseline.backgroundColor, result.backgroundColor)
        assertEquals(baseline.font, result.font)
        assertEquals(baseline.fontSize, result.fontSize)
        assertEquals(baseline.noteCardCornerRadius, result.noteCardCornerRadius)
        assertEquals(baseline.noteCardElevation, result.noteCardElevation)
        assertEquals(baseline.noteCardPadding, result.noteCardPadding)
        assertEquals(baseline.noteContentMaxLines, result.noteContentMaxLines)
        assertEquals(baseline.cardDensity, result.cardDensity)
        assertEquals(customized.language, result.language)
        assertEquals(customized.soundEffectsVolume, result.soundEffectsVolume)
        assertEquals(customized.reminderSoundVolume, result.reminderSoundVolume)
        assertEquals(customized.profileImageUri, result.profileImageUri)
        assertEquals(customized.editorFocusMode, result.editorFocusMode)
        assertEquals(customized.sortOrder, result.sortOrder)
        assertEquals(customized.gridColumns, result.gridColumns)
        assertEquals(4, DesignPreferencePolicy.presets.size)
    }

    @Test fun unknownPresetLeavesExistingSettingsIntact() {
        val settings = AppSettings(backgroundColor = "ocean", darkMode = true)
        assertEquals(settings, DesignPreferencePolicy.applyPreset(settings, "unknown"))
    }

    @Test fun oledEnablesDarkModeAndPureBlackSurfaces() {
        val oled = DesignPreferencePolicy.applyPreset(AppSettings(), "oled")
        assertTrue(oled.darkMode)
        assertTrue(oled.oledMode)
        assertEquals("solid", oled.settingsBackgroundStyle)
        assertTrue(oled.copy(cardDensity = "compact").oledMode)
    }

    @Test fun notebookAfterOledRestoresLightPaperAndSerif() {
        val settings = DesignPreferencePolicy.applyPreset(DesignPreferencePolicy.applyPreset(AppSettings(), "oled"), "notebook")
        assertFalse(settings.oledMode)
        assertFalse(settings.darkMode)
        assertEquals("cream", settings.backgroundColor)
        assertEquals(FontPreferencePolicy.SERIF, settings.font)
    }

    @Test fun compactCardsKeepCustomSettingsButReduceTheirDisplayDensity() {
        val settings = AppSettings(noteCardPadding = 20f, noteCardImageHeight = 160f, noteContentMaxLines = 9, noteTitleMaxLines = 5)
        val compact = settings.copy(cardDensity = "compact").toNoteCardStyle()
        assertTrue(compact.padding < settings.toNoteCardStyle().padding)
        assertTrue(compact.imageHeight < settings.toNoteCardStyle().imageHeight)
        assertEquals(3, compact.contentMaxLines)
        assertEquals(5, settings.toNoteCardStyle().titleMaxLines)
    }

    @Test fun importedUnknownValuesHaveSafeFallbacks() {
        assertEquals("comfortable", DesignPreferencePolicy.density("legacy"))
        assertEquals("custom", DesignPreferencePolicy.preset(""))
        assertEquals("newest", DesignPreferencePolicy.sortOrder("invalid"))
    }
}

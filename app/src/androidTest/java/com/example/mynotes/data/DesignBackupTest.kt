package com.example.mynotes.data

import com.example.mynotes.settings.AppSettings
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Test

/** Uses Android's real JSON implementation on device; also exercised by the isolated JVM check. */
class DesignBackupTest {
    @Test fun designPreferencesSurviveBackupRoundTrip() {
        val settings = AppSettings(configurationMode = "advanced", designPreset = "custom", oledMode = true,
            cardDensity = "compact", editorFocusMode = false, darkMode = true, language = "es",
            reminderSoundVolume = 24f, soundEffectsVolume = 82f)
        assertEquals(settings, jsonToSettings(settingsToJson(settings, null)))
    }

    @Test fun v310BackupUsesNewDefaultsAndPreservesOldPreferences() {
        val oldBackup = JSONObject().put("darkMode", true).put("language", "fr").put("reminderSoundVolume", 38)
        val settings = jsonToSettings(oldBackup)
        assertEquals("custom", settings.designPreset)
        assertEquals("comfortable", settings.cardDensity)
        assertTrue(settings.editorFocusMode)
        assertFalse(settings.oledMode)
        assertTrue(settings.darkMode)
        assertEquals("fr", settings.language)
        assertEquals(38f, settings.reminderSoundVolume)
    }

    @Test fun unknownDesignValuesAreNormalizedDuringImport() {
        val settings = jsonToSettings(JSONObject().put("designPreset", "unknown").put("cardDensity", "unknown"))
        assertEquals("custom", settings.designPreset)
        assertEquals("comfortable", settings.cardDensity)
    }
}

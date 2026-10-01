package com.example.mynotes.settings

import org.junit.Assert.assertEquals
import org.junit.Test

class FontPreferencePolicyTest {
    @Test
    fun systemDefaultRemainsCanonicalSystemFont() {
        assertEquals(
            FontPreferencePolicy.SYSTEM_DEFAULT,
            FontPreferencePolicy.normalize("system_default", googleSansFlexUnlocked = false)
        )
    }

    @Test
    fun developerFontCannotBypassUnlockThroughStoredOrImportedValue() {
        assertEquals(
            FontPreferencePolicy.SYSTEM_DEFAULT,
            FontPreferencePolicy.normalize(
                FontPreferencePolicy.DEVELOPER_GOOGLE_SANS_FLEX,
                googleSansFlexUnlocked = false
            )
        )
        assertEquals(
            FontPreferencePolicy.DEVELOPER_GOOGLE_SANS_FLEX,
            FontPreferencePolicy.normalize(
                FontPreferencePolicy.DEVELOPER_GOOGLE_SANS_FLEX,
                googleSansFlexUnlocked = true
            )
        )
    }

    @Test
    fun legacyGoogleSansKeysMigrateToAndroidSystemFont() {
        listOf(
            "google_sans",
            "google_sans_regular",
            "google_sans_medium",
            "google_sans_bold",
            "google_sans_italic",
            "google_sans_medium_italic",
            "google_sans_bold_italic",
            "google_sans_flex"
        ).forEach { legacyKey ->
            assertEquals(
                FontPreferencePolicy.SYSTEM_DEFAULT,
                FontPreferencePolicy.normalize(legacyKey, googleSansFlexUnlocked = true)
            )
        }
    }

    @Test
    fun unknownValuesFallBackSafelyToSystemFont() {
        assertEquals(
            FontPreferencePolicy.SYSTEM_DEFAULT,
            FontPreferencePolicy.normalize("unknown_font", googleSansFlexUnlocked = true)
        )
    }
}

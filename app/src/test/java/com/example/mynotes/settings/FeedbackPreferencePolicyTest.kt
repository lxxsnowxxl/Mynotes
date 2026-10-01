package com.example.mynotes.settings

import org.junit.Assert.assertEquals
import org.junit.Test

class FeedbackPreferencePolicyTest {
    @Test
    fun soundAndHapticCatalogsPreserveTheirExistingOrder() {
        assertEquals(
            listOf("classic", "soft", "digital", "glass", "retro", "pop", "mechanical", "bubble", "arcade", "wood", "synth",
                "minimal", "camera", "typewriter", "metal", "pixel", "space", "chime", "paper", "neon", "material",
                "expressive", "prism", "aurora", "fluid", "pulse"),
            FeedbackPreferencePolicy.soundThemes
        )
        assertEquals(
            listOf("soft", "crisp", "deep", "double", "pulse", "stepped", "mechanical", "minimal", "triple", "ripple",
                "heartbeat", "snap", "wave", "heavy", "spring", "echo"),
            FeedbackPreferencePolicy.hapticStyles
        )
    }

    @Test
    fun validKeysStillAcceptWhitespaceAndUppercase() {
        for (key in FeedbackPreferencePolicy.soundThemes) {
            assertEquals(key, FeedbackPreferencePolicy.normalizeSoundTheme(key))
            assertEquals(key, FeedbackPreferencePolicy.normalizeSoundTheme(" \t${key.uppercase()}\n"))
        }
        for (key in FeedbackPreferencePolicy.hapticStyles) {
            assertEquals(key, FeedbackPreferencePolicy.normalizeHapticStyle(key))
            assertEquals(key, FeedbackPreferencePolicy.normalizeHapticStyle(" \t${key.uppercase()}\n"))
        }
        val ringtones = listOf(
            "classic", "bell", "crystal", "pulse", "sunrise", "digital", "alert", "urgent", "beacon", "radar", "warning",
            "signal", "pager", "double_alarm", "serenity", "soft_bell", "breeze", "dew", "bamboo", "horizon", "calm",
            "moonlight", "orbit", "droplet", "glass_tap", "clockwork", "spark", "bubble_pop", "comet", "echo_ping",
            "woodblock", "starlight", "sentinel", "siren", "cascade", "escalation", "distress", "interlock", "scanner",
            "command", "rapid_triple", "priority_sequence", "double_sweep", "attention_burst"
        )
        for (key in ringtones) {
            assertEquals(key, FeedbackPreferencePolicy.normalizeReminderRingtone(key))
            assertEquals(key, FeedbackPreferencePolicy.normalizeReminderRingtone(" ${key.uppercase()} "))
        }
    }

    @Test
    fun unknownKeysKeepTheOriginalFallbacks() {
        for (value in listOf("", "  ", "unknown", "CLASSIC_extra", "SOFT_extra")) {
            assertEquals("classic", FeedbackPreferencePolicy.normalizeSoundTheme(value))
            assertEquals("soft", FeedbackPreferencePolicy.normalizeHapticStyle(value))
            assertEquals("classic", FeedbackPreferencePolicy.normalizeReminderRingtone(value))
        }
        assertEquals("classic", FeedbackPreferencePolicy.normalizeSoundTheme("bell"))
        assertEquals("soft", FeedbackPreferencePolicy.normalizeHapticStyle("classic"))
        assertEquals("classic", FeedbackPreferencePolicy.normalizeReminderRingtone("soft"))
    }
}

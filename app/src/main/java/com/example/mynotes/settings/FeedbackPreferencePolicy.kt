package com.example.mynotes.settings

/** Catálogo compartido por preferencias y reproductores; no inicializa audio. */
internal object FeedbackPreferencePolicy {
    const val DEFAULT_SOUND_THEME = "classic"
    const val DEFAULT_HAPTIC_STYLE = "soft"
    const val DEFAULT_REMINDER_RINGTONE = "classic"

    val soundThemes: List<String> = listOf(
        "classic", "soft", "digital", "glass", "retro", "pop", "mechanical", "bubble", "arcade",
        "wood", "synth", "minimal", "camera", "typewriter", "metal", "pixel", "space", "chime", "paper", "neon",
        "material", "expressive", "prism", "aurora", "fluid", "pulse"
    )
    val hapticStyles: List<String> = listOf(
        "soft", "crisp", "deep", "double", "pulse", "stepped", "mechanical", "minimal", "triple",
        "ripple", "heartbeat", "snap", "wave", "heavy", "spring", "echo"
    )
    private val reminderRingtones = setOf(
        "classic", "bell", "crystal", "pulse", "sunrise", "digital",
        "alert", "urgent", "beacon", "radar", "warning", "signal", "pager", "double_alarm",
        "serenity", "soft_bell", "breeze", "dew", "bamboo", "horizon", "calm", "moonlight",
        "orbit", "droplet", "glass_tap", "clockwork", "spark", "bubble_pop", "comet", "echo_ping", "woodblock", "starlight",
        "sentinel", "siren", "cascade", "escalation", "distress", "interlock", "scanner", "command",
        "rapid_triple", "priority_sequence", "double_sweep", "attention_burst"
    )
    private val soundThemeKeys = soundThemes.toHashSet()
    private val hapticStyleKeys = hapticStyles.toHashSet()

    fun normalizeSoundTheme(value: String): String = normalize(value, soundThemeKeys, DEFAULT_SOUND_THEME)

    fun normalizeHapticStyle(value: String): String = normalize(value, hapticStyleKeys, DEFAULT_HAPTIC_STYLE)

    fun normalizeReminderRingtone(value: String): String = normalize(value, reminderRingtones, DEFAULT_REMINDER_RINGTONE)

    private fun normalize(value: String, validKeys: Set<String>, fallback: String): String {
        // Las preferencias ya normalizadas son el caso habitual de cada toque.
        if (value in validKeys) return value
        val normalized = value.trim().lowercase()
        return if (normalized in validKeys) normalized else fallback
    }
}

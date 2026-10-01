package com.example.mynotes.settings

import com.example.mynotes.R

/** Catálogo compartido por preferencias y reproductores; no inicializa audio. */
internal object FeedbackPreferencePolicy {
    const val DEFAULT_SOUND_THEME = "classic"
    const val DEFAULT_HAPTIC_STYLE = "soft"
    const val DEFAULT_REMINDER_RINGTONE = "classic"

    data class SoundThemeResources(val key: String, val labelRes: Int, val edit: Int, val delete: Int, val priority: Int, val sliderTick: Int, val attachment: Int, val toggle: Int)

    val soundThemeResources = listOf(
        SoundThemeResources("classic", R.string.sound_theme_classic, R.raw.ui_edit, R.raw.ui_delete, R.raw.ui_priority, R.raw.ui_slider_tick, R.raw.ui_attachment, R.raw.ui_toggle),
        SoundThemeResources("soft", R.string.sound_theme_soft, R.raw.ui_soft_edit, R.raw.ui_soft_delete, R.raw.ui_soft_priority, R.raw.ui_soft_slider_tick, R.raw.ui_soft_attachment, R.raw.ui_soft_toggle),
        SoundThemeResources("digital", R.string.sound_theme_digital, R.raw.ui_digital_edit, R.raw.ui_digital_delete, R.raw.ui_digital_priority, R.raw.ui_digital_slider_tick, R.raw.ui_digital_attachment, R.raw.ui_digital_toggle),
        SoundThemeResources("glass", R.string.sound_theme_glass, R.raw.ui_glass_edit, R.raw.ui_glass_delete, R.raw.ui_glass_priority, R.raw.ui_glass_slider_tick, R.raw.ui_glass_attachment, R.raw.ui_glass_toggle),
        SoundThemeResources("retro", R.string.sound_theme_retro, R.raw.ui_retro_edit, R.raw.ui_retro_delete, R.raw.ui_retro_priority, R.raw.ui_retro_slider_tick, R.raw.ui_retro_attachment, R.raw.ui_retro_toggle),
        SoundThemeResources("pop", R.string.sound_theme_pop, R.raw.ui_pop_edit, R.raw.ui_pop_delete, R.raw.ui_pop_priority, R.raw.ui_pop_slider_tick, R.raw.ui_pop_attachment, R.raw.ui_pop_toggle),
        SoundThemeResources("mechanical", R.string.sound_theme_mechanical, R.raw.ui_mechanical_edit, R.raw.ui_mechanical_delete, R.raw.ui_mechanical_priority, R.raw.ui_mechanical_slider_tick, R.raw.ui_mechanical_attachment, R.raw.ui_mechanical_toggle),
        SoundThemeResources("bubble", R.string.sound_theme_bubble, R.raw.ui_bubble_edit, R.raw.ui_bubble_delete, R.raw.ui_bubble_priority, R.raw.ui_bubble_slider_tick, R.raw.ui_bubble_attachment, R.raw.ui_bubble_toggle),
        SoundThemeResources("arcade", R.string.sound_theme_arcade, R.raw.ui_arcade_edit, R.raw.ui_arcade_delete, R.raw.ui_arcade_priority, R.raw.ui_arcade_slider_tick, R.raw.ui_arcade_attachment, R.raw.ui_arcade_toggle),
        SoundThemeResources("wood", R.string.sound_theme_wood, R.raw.ui_wood_edit, R.raw.ui_wood_delete, R.raw.ui_wood_priority, R.raw.ui_wood_slider_tick, R.raw.ui_wood_attachment, R.raw.ui_wood_toggle),
        SoundThemeResources("synth", R.string.sound_theme_synth, R.raw.ui_synth_edit, R.raw.ui_synth_delete, R.raw.ui_synth_priority, R.raw.ui_synth_slider_tick, R.raw.ui_synth_attachment, R.raw.ui_synth_toggle),
        SoundThemeResources("minimal", R.string.sound_theme_minimal, R.raw.ui_minimal_edit, R.raw.ui_minimal_delete, R.raw.ui_minimal_priority, R.raw.ui_minimal_slider_tick, R.raw.ui_minimal_attachment, R.raw.ui_minimal_toggle),
        SoundThemeResources("camera", R.string.sound_theme_camera, R.raw.ui_camera_edit, R.raw.ui_camera_delete, R.raw.ui_camera_priority, R.raw.ui_camera_slider_tick, R.raw.ui_camera_attachment, R.raw.ui_camera_toggle),
        SoundThemeResources("typewriter", R.string.sound_theme_typewriter, R.raw.ui_typewriter_edit, R.raw.ui_typewriter_delete, R.raw.ui_typewriter_priority, R.raw.ui_typewriter_slider_tick, R.raw.ui_typewriter_attachment, R.raw.ui_typewriter_toggle),
        SoundThemeResources("metal", R.string.sound_theme_metal, R.raw.ui_metal_edit, R.raw.ui_metal_delete, R.raw.ui_metal_priority, R.raw.ui_metal_slider_tick, R.raw.ui_metal_attachment, R.raw.ui_metal_toggle),
        SoundThemeResources("pixel", R.string.sound_theme_pixel, R.raw.ui_pixel_edit, R.raw.ui_pixel_delete, R.raw.ui_pixel_priority, R.raw.ui_pixel_slider_tick, R.raw.ui_pixel_attachment, R.raw.ui_pixel_toggle),
        SoundThemeResources("space", R.string.sound_theme_space, R.raw.ui_space_edit, R.raw.ui_space_delete, R.raw.ui_space_priority, R.raw.ui_space_slider_tick, R.raw.ui_space_attachment, R.raw.ui_space_toggle),
        SoundThemeResources("chime", R.string.sound_theme_chime, R.raw.ui_chime_edit, R.raw.ui_chime_delete, R.raw.ui_chime_priority, R.raw.ui_chime_slider_tick, R.raw.ui_chime_attachment, R.raw.ui_chime_toggle),
        SoundThemeResources("paper", R.string.sound_theme_paper, R.raw.ui_paper_edit, R.raw.ui_paper_delete, R.raw.ui_paper_priority, R.raw.ui_paper_slider_tick, R.raw.ui_paper_attachment, R.raw.ui_paper_toggle),
        SoundThemeResources("neon", R.string.sound_theme_neon, R.raw.ui_neon_edit, R.raw.ui_neon_delete, R.raw.ui_neon_priority, R.raw.ui_neon_slider_tick, R.raw.ui_neon_attachment, R.raw.ui_neon_toggle),
        SoundThemeResources("material", R.string.sound_theme_material, R.raw.ui_material_edit, R.raw.ui_material_delete, R.raw.ui_material_priority, R.raw.ui_material_slider_tick, R.raw.ui_material_attachment, R.raw.ui_material_toggle),
        SoundThemeResources("expressive", R.string.sound_theme_expressive, R.raw.ui_expressive_edit, R.raw.ui_expressive_delete, R.raw.ui_expressive_priority, R.raw.ui_expressive_slider_tick, R.raw.ui_expressive_attachment, R.raw.ui_expressive_toggle),
        SoundThemeResources("prism", R.string.sound_theme_prism, R.raw.ui_prism_edit, R.raw.ui_prism_delete, R.raw.ui_prism_priority, R.raw.ui_prism_slider_tick, R.raw.ui_prism_attachment, R.raw.ui_prism_toggle),
        SoundThemeResources("aurora", R.string.sound_theme_aurora, R.raw.ui_aurora_edit, R.raw.ui_aurora_delete, R.raw.ui_aurora_priority, R.raw.ui_aurora_slider_tick, R.raw.ui_aurora_attachment, R.raw.ui_aurora_toggle),
        SoundThemeResources("fluid", R.string.sound_theme_fluid, R.raw.ui_fluid_edit, R.raw.ui_fluid_delete, R.raw.ui_fluid_priority, R.raw.ui_fluid_slider_tick, R.raw.ui_fluid_attachment, R.raw.ui_fluid_toggle),
        SoundThemeResources("pulse", R.string.sound_theme_pulse, R.raw.ui_pulse_edit, R.raw.ui_pulse_delete, R.raw.ui_pulse_priority, R.raw.ui_pulse_slider_tick, R.raw.ui_pulse_attachment, R.raw.ui_pulse_toggle)
    )
    data class HapticStyle(val key: String, val labelRes: Int)
    val hapticStyles = listOf(
        HapticStyle("soft", R.string.haptic_style_soft), HapticStyle("crisp", R.string.haptic_style_crisp), HapticStyle("deep", R.string.haptic_style_deep), HapticStyle("double", R.string.haptic_style_double),
        HapticStyle("pulse", R.string.haptic_style_pulse), HapticStyle("stepped", R.string.haptic_style_stepped), HapticStyle("mechanical", R.string.haptic_style_mechanical), HapticStyle("minimal", R.string.haptic_style_minimal),
        HapticStyle("triple", R.string.haptic_style_triple), HapticStyle("ripple", R.string.haptic_style_ripple), HapticStyle("heartbeat", R.string.haptic_style_heartbeat), HapticStyle("snap", R.string.haptic_style_snap),
        HapticStyle("wave", R.string.haptic_style_wave), HapticStyle("heavy", R.string.haptic_style_heavy), HapticStyle("spring", R.string.haptic_style_spring), HapticStyle("echo", R.string.haptic_style_echo)
    )
    private val hapticStyleKeys = hapticStyles.mapTo(hashSetOf(), HapticStyle::key)
    data class ReminderTone(val key: String, val labelRes: Int, val soundRes: Int)

    val reminderTones = listOf(
        ReminderTone("classic", R.string.reminder_tone_classic, R.raw.reminder_ringtone),
        ReminderTone("bell", R.string.reminder_tone_bell, R.raw.reminder_ringtone_bell),
        ReminderTone("crystal", R.string.reminder_tone_crystal, R.raw.reminder_ringtone_crystal),
        ReminderTone("pulse", R.string.reminder_tone_pulse, R.raw.reminder_ringtone_pulse),
        ReminderTone("sunrise", R.string.reminder_tone_sunrise, R.raw.reminder_ringtone_sunrise),
        ReminderTone("digital", R.string.reminder_tone_digital, R.raw.reminder_ringtone_digital),
        ReminderTone("alert", R.string.reminder_tone_alert, R.raw.reminder_ringtone_alert),
        ReminderTone("urgent", R.string.reminder_tone_urgent, R.raw.reminder_ringtone_urgent),
        ReminderTone("beacon", R.string.reminder_tone_beacon, R.raw.reminder_ringtone_beacon),
        ReminderTone("radar", R.string.reminder_tone_radar, R.raw.reminder_ringtone_radar),
        ReminderTone("warning", R.string.reminder_tone_warning, R.raw.reminder_ringtone_warning),
        ReminderTone("signal", R.string.reminder_tone_signal, R.raw.reminder_ringtone_signal),
        ReminderTone("pager", R.string.reminder_tone_pager, R.raw.reminder_ringtone_pager),
        ReminderTone("double_alarm", R.string.reminder_tone_double_alarm, R.raw.reminder_ringtone_double_alarm),
        ReminderTone("serenity", R.string.reminder_tone_serenity, R.raw.reminder_ringtone_serenity),
        ReminderTone("soft_bell", R.string.reminder_tone_soft_bell, R.raw.reminder_ringtone_soft_bell),
        ReminderTone("breeze", R.string.reminder_tone_breeze, R.raw.reminder_ringtone_breeze),
        ReminderTone("dew", R.string.reminder_tone_dew, R.raw.reminder_ringtone_dew),
        ReminderTone("bamboo", R.string.reminder_tone_bamboo, R.raw.reminder_ringtone_bamboo),
        ReminderTone("horizon", R.string.reminder_tone_horizon, R.raw.reminder_ringtone_horizon),
        ReminderTone("calm", R.string.reminder_tone_calm, R.raw.reminder_ringtone_calm),
        ReminderTone("moonlight", R.string.reminder_tone_moonlight, R.raw.reminder_ringtone_moonlight),
        ReminderTone("orbit", R.string.reminder_tone_orbit, R.raw.reminder_ringtone_orbit),
        ReminderTone("droplet", R.string.reminder_tone_droplet, R.raw.reminder_ringtone_droplet),
        ReminderTone("glass_tap", R.string.reminder_tone_glass_tap, R.raw.reminder_ringtone_glass_tap),
        ReminderTone("clockwork", R.string.reminder_tone_clockwork, R.raw.reminder_ringtone_clockwork),
        ReminderTone("spark", R.string.reminder_tone_spark, R.raw.reminder_ringtone_spark),
        ReminderTone("bubble_pop", R.string.reminder_tone_bubble_pop, R.raw.reminder_ringtone_bubble_pop),
        ReminderTone("comet", R.string.reminder_tone_comet, R.raw.reminder_ringtone_comet),
        ReminderTone("echo_ping", R.string.reminder_tone_echo_ping, R.raw.reminder_ringtone_echo_ping),
        ReminderTone("woodblock", R.string.reminder_tone_woodblock, R.raw.reminder_ringtone_woodblock),
        ReminderTone("starlight", R.string.reminder_tone_starlight, R.raw.reminder_ringtone_starlight),
        ReminderTone("sentinel", R.string.reminder_tone_sentinel, R.raw.reminder_ringtone_sentinel),
        ReminderTone("siren", R.string.reminder_tone_siren, R.raw.reminder_ringtone_siren),
        ReminderTone("cascade", R.string.reminder_tone_cascade, R.raw.reminder_ringtone_cascade),
        ReminderTone("escalation", R.string.reminder_tone_escalation, R.raw.reminder_ringtone_escalation),
        ReminderTone("distress", R.string.reminder_tone_distress, R.raw.reminder_ringtone_distress),
        ReminderTone("interlock", R.string.reminder_tone_interlock, R.raw.reminder_ringtone_interlock),
        ReminderTone("scanner", R.string.reminder_tone_scanner, R.raw.reminder_ringtone_scanner),
        ReminderTone("command", R.string.reminder_tone_command, R.raw.reminder_ringtone_command),
        ReminderTone("rapid_triple", R.string.reminder_tone_rapid_triple, R.raw.reminder_ringtone_rapid_triple),
        ReminderTone("priority_sequence", R.string.reminder_tone_priority_sequence, R.raw.reminder_ringtone_priority_sequence),
        ReminderTone("double_sweep", R.string.reminder_tone_double_sweep, R.raw.reminder_ringtone_double_sweep),
        ReminderTone("attention_burst", R.string.reminder_tone_attention_burst, R.raw.reminder_ringtone_attention_burst)
    )
    private val reminderToneByKey = reminderTones.associateBy(ReminderTone::key)
    private val reminderRingtones = reminderToneByKey.keys
    private val soundThemeKeys = soundThemeResources.mapTo(hashSetOf(), SoundThemeResources::key)

    fun normalizeSoundTheme(value: String): String = normalize(value, soundThemeKeys, DEFAULT_SOUND_THEME)

    fun normalizeHapticStyle(value: String): String = normalize(value, hapticStyleKeys, DEFAULT_HAPTIC_STYLE)

    fun normalizeReminderRingtone(value: String): String = normalize(value, reminderRingtones, DEFAULT_REMINDER_RINGTONE)

    fun reminderTone(value: String): ReminderTone = reminderToneByKey[normalizeReminderRingtone(value)] ?: reminderTones.first()

    private fun normalize(value: String, validKeys: Set<String>, fallback: String): String {
        // Las preferencias ya normalizadas son el caso habitual de cada toque.
        if (value in validKeys) return value
        val normalized = value.trim().lowercase()
        return if (normalized in validKeys) normalized else fallback
    }
}

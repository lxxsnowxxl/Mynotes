package com.example.mynotes.settings

/** Presets affect appearance only. Notes, reminders, feedback and language are preserved. */
object DesignPreferencePolicy {
    val presets = listOf("minimal", "notebook", "oled", "default")
    val densities = listOf("compact", "comfortable")
    val sortOrders = listOf("newest", "oldest", "title", "priority")

    fun preset(value: String) = value.takeIf { it in presets } ?: "custom"
    fun density(value: String) = value.takeIf { it in densities } ?: "comfortable"
    fun sortOrder(value: String) = value.takeIf { it in sortOrders } ?: "newest"

    fun applyPreset(current: AppSettings, key: String): AppSettings {
        if (key !in presets) return current
        // Restore only the visual fields written by SettingsRepository.writeAppearance.
        // Never reset notes, the chosen sound levels, profile, language or other preferences.
        if (key == "default") {
            val baseline = AppSettings()
            return current.copy(
                designPreset = "default",
                oledMode = baseline.oledMode,
                darkMode = baseline.darkMode,
                backgroundColor = baseline.backgroundColor,
                backgroundToneIndex = baseline.backgroundToneIndex,
                settingsBackgroundStyle = baseline.settingsBackgroundStyle,
                surfacePanelIntensity = baseline.surfacePanelIntensity,
                textColor = baseline.textColor,
                textOutlineEnabled = baseline.textOutlineEnabled,
                font = baseline.font,
                fontSize = baseline.fontSize,
                accentColor = baseline.accentColor,
                noteCardCornerRadius = baseline.noteCardCornerRadius,
                noteCardElevation = baseline.noteCardElevation,
                noteCardPadding = baseline.noteCardPadding,
                noteCardImageHeight = baseline.noteCardImageHeight,
                noteCardOutlineEnabled = baseline.noteCardOutlineEnabled,
                noteCardOutlineWidth = baseline.noteCardOutlineWidth,
                noteTitleMaxLines = baseline.noteTitleMaxLines,
                noteContentMaxLines = baseline.noteContentMaxLines,
                noteLineSpacing = baseline.noteLineSpacing,
                cardDensity = baseline.cardDensity
            )
        }
        val notebook = key == "notebook"
        val oled = key == "oled"
        return current.copy(
            designPreset = key,
            oledMode = oled,
            darkMode = oled,
            backgroundColor = if (notebook) "cream" else "neutral",
            backgroundToneIndex = 0,
            settingsBackgroundStyle = "solid",
            surfacePanelIntensity = 72f,
            textColor = "auto",
            textOutlineEnabled = false,
            font = if (notebook) FontPreferencePolicy.SERIF else FontPreferencePolicy.SYSTEM_DEFAULT,
            fontSize = 16f,
            accentColor = "palette",
            noteCardCornerRadius = if (notebook) 8f else 20f,
            noteCardElevation = 0f,
            noteCardPadding = 16f,
            noteCardImageHeight = 128f,
            noteCardOutlineEnabled = notebook || oled,
            noteCardOutlineWidth = if (notebook || oled) 1f else 0f,
            noteTitleMaxLines = 2,
            noteContentMaxLines = 5,
            noteLineSpacing = 1.5f,
            cardDensity = "comfortable"
        )
    }
}

/** Shared by DataStore and backup import, including non-finite imported values. */
object ReaderPreferencePolicy {
    fun width(value: Float) = if (value.isFinite()) value.coerceIn(320f, 900f) else 680f
    fun spacing(value: Float) = if (value.isFinite()) value.coerceIn(1.2f, 2.2f) else 1.6f
}

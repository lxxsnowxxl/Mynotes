package com.example.mynotes.settings

import com.example.mynotes.ui.theme.PaletteCatalog

internal object SettingsValuePolicy {
    private val configurationModes = setOf("basic", "advanced", "unset")
    private val uiTextColors = setOf("auto", "black", "white")
    private val iconStyles = setOf("material", "rounded", "outlined", "minimal")
    private val optionMenuTextColors = setOf("note", "black", "white")
    private val sliderStyles = setOf("minimal", "capsule", "glow", "glass", "segmented", "dots", "gradient", "neumorphic", "line_pill", "floating")
    private val accentColors = setOf(
        "palette", "red", "coral", "orange", "amber", "yellow", "lime", "green", "mint", "teal", "cyan", "sky",
        "blue", "indigo", "violet", "purple", "pink", "rose", "brown", "graphite"
    )
    private val currentPaletteKeys = PaletteCatalog.palettes.mapTo(HashSet(PaletteCatalog.palettes.size * 4 / 3 + 1)) { it.key }
    private val legacyPaletteKeys = setOf("yellow", "purple", "pink", "default", "gray")

    fun configurationMode(value: String) = value.validOr(configurationModes, "unset")
    fun uiTextColor(value: String) = value.validOr(uiTextColors, "auto")
    fun iconStyle(value: String) = value.validOr(iconStyles, "rounded")
    fun optionMenuTextColor(value: String) = value.validOr(optionMenuTextColors, "note")
    fun sliderStyle(value: String) = value.validOr(sliderStyles, "capsule")
    fun accentColor(value: String) = value.validOr(accentColors, "palette")
    fun isLegacyPalette(value: String) = value in legacyPaletteKeys

    fun paletteKey(value: String): String = when {
        value in currentPaletteKeys -> value
        value == "yellow" -> "sun"
        value == "purple" -> "lavender"
        value == "pink" -> "soft_pink"
        value == "default" || value == "gray" -> "neutral"
        else -> "neutral"
    }
}

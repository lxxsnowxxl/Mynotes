package com.example.mynotes.settings

/** Solo los ajustes que consumen los widgets; todos proceden de la misma transacción. */
internal data class WidgetSettings(
    val darkMode: Boolean,
    val backgroundColor: String,
    val backgroundToneIndex: Int,
    val language: String
)

package com.example.mynotes.settings

/** Panel plegable activo. Las claves obsoletas de otras secciones ya no se guardan. */
object SettingsSectionPreferencePolicy {
    const val NOTE_CARDS = "note_cards"
    val keys: Set<String> = setOf(NOTE_CARDS)

    fun normalize(value: String): String =
        if (value.split(',').any { it.trim() == NOTE_CARDS }) NOTE_CARDS else ""

    fun isExpanded(value: String, sectionKey: String): Boolean =
        sectionKey !in normalize(value).split(',')

    fun withExpanded(value: String, sectionKey: String, expanded: Boolean): String {
        if (sectionKey != NOTE_CARDS) return normalize(value)
        return if (expanded) "" else NOTE_CARDS
    }
}

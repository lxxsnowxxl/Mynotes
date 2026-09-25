package com.example.mynotes.settings

/**
 * Única fuente de verdad para las claves de tipografía persistidas por MyNotes.
 *
 * Mantener la normalización fuera de Compose evita que DataStore, Backup/Restore
 * y el selector de Configuración interpreten una misma clave de forma distinta.
 */
object FontPreferencePolicy {
    const val SYSTEM_DEFAULT = "system_default"
    const val SERIF = "serif"
    const val MONOSPACE = "monospace"
    const val DEVELOPER_GOOGLE_SANS_FLEX = "developer_google_sans_flex"

    private val legacyGoogleSansKeys = setOf(
        "google_sans",
        "google_sans_regular",
        "google_sans_medium",
        "google_sans_bold",
        "google_sans_italic",
        "google_sans_medium_italic",
        "google_sans_bold_italic",
        "google_sans_flex"
    )

    /**
     * Convierte cualquier valor persistido/importado a una clave válida.
     *
     * Las claves de tipografía que ya no existen terminan de forma segura en la
     * fuente del sistema Android. Las antiguas claves Google Sans también usan
     * ese mismo fallback.
     * La fuente descargable de desarrollador solo puede sobrevivir a la
     * normalización cuando el desbloqueo existe en este dispositivo.
     */
    fun normalize(value: String?, googleSansFlexUnlocked: Boolean): String {
        val key = value?.trim()?.lowercase().orEmpty()
        val canonical = when {
            key == SYSTEM_DEFAULT || key == "default" -> SYSTEM_DEFAULT
            key == SERIF -> SERIF
            key == MONOSPACE -> MONOSPACE
            key == DEVELOPER_GOOGLE_SANS_FLEX -> DEVELOPER_GOOGLE_SANS_FLEX
            key in legacyGoogleSansKeys -> SYSTEM_DEFAULT
            else -> SYSTEM_DEFAULT
        }

        return if (canonical == DEVELOPER_GOOGLE_SANS_FLEX && !googleSansFlexUnlocked) {
            SYSTEM_DEFAULT
        } else {
            canonical
        }
    }
}

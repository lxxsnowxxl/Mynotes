package com.example.mynotes.settings

import android.content.Context

/**
 * Preferencias locales para funciones deliberadamente ocultas de desarrollo.
 * No contienen datos de notas ni configuración crítica de la aplicación.
 */
object DeveloperFeatures {
    private const val PREFS_NAME = "mynotes_developer_features"
    private const val KEY_GOOGLE_SANS_FLEX_UNLOCKED = "google_sans_flex_unlocked"

    fun isGoogleSansFlexUnlocked(context: Context): Boolean =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_GOOGLE_SANS_FLEX_UNLOCKED, false)

    fun unlockGoogleSansFlex(context: Context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_GOOGLE_SANS_FLEX_UNLOCKED, true)
            .apply()
    }
}

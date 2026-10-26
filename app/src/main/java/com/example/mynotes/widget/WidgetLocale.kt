package com.example.mynotes.widget

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

/**
 * Contexto localizado específicamente para App Widgets.
 *
 * MyNotes permite elegir un idioma independiente al del sistema. Los widgets
 * viven fuera de la Activity y el launcher infla RemoteViews usando su propia
 * configuración. El idioma procede de la misma lectura que el tema; la copia
 * usada al iniciar la Activity queda como respaldo si no se pudo leer DataStore.
 */
object WidgetLocale {
    private const val LOCALE_PREFS = "locale_prefs"
    private const val LANGUAGE_KEY = "language"
    private const val SYSTEM_LANGUAGE = "system"

    private fun selectedLanguage(context: Context): String =
        context.getSharedPreferences(LOCALE_PREFS, Context.MODE_PRIVATE)
            .getString(LANGUAGE_KEY, SYSTEM_LANGUAGE)
            ?.takeIf { it.isNotBlank() }
            ?: SYSTEM_LANGUAGE

    fun localizedContext(context: Context, language: String?): Context {
        val selected = language ?: selectedLanguage(context)
        if (selected == SYSTEM_LANGUAGE || selected.isBlank()) return context

        val locale = Locale.forLanguageTag(selected)
        val configuration = Configuration(context.resources.configuration).apply {
            setLocale(locale)
            setLayoutDirection(locale)
        }
        return context.createConfigurationContext(configuration)
    }

}

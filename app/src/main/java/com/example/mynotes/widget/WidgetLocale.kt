package com.example.mynotes.widget

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

/**
 * Contexto localizado específicamente para App Widgets.
 *
 * MyNotes permite elegir un idioma independiente al del sistema. Los widgets
 * viven fuera de la Activity y el launcher infla RemoteViews usando su propia
 * configuración, por lo que los android:text del XML pueden quedarse en el
 * idioma del sistema. Este helper replica la preferencia persistida que usa
 * MainActivity y permite escribir explícitamente el texto ya traducido en cada
 * RemoteViews.
 */
object WidgetLocale {
    private const val LOCALE_PREFS = "locale_prefs"
    private const val LANGUAGE_KEY = "language"
    private const val SYSTEM_LANGUAGE = "system"

    fun selectedLanguage(context: Context): String =
        context.getSharedPreferences(LOCALE_PREFS, Context.MODE_PRIVATE)
            .getString(LANGUAGE_KEY, SYSTEM_LANGUAGE)
            ?.takeIf { it.isNotBlank() }
            ?: SYSTEM_LANGUAGE

    fun localizedContext(context: Context): Context {
        val language = selectedLanguage(context)
        if (language == SYSTEM_LANGUAGE) return context

        val locale = Locale.forLanguageTag(language)
        val configuration = Configuration(context.resources.configuration).apply {
            setLocale(locale)
            setLayoutDirection(locale)
        }
        return context.createConfigurationContext(configuration)
    }

    fun locale(context: Context): Locale {
        val localized = localizedContext(context)
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            localized.resources.configuration.locales[0]
        } else {
            @Suppress("DEPRECATION")
            localized.resources.configuration.locale
        }
    }
}

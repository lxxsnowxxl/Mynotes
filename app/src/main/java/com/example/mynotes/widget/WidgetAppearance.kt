package com.example.mynotes.widget

import android.content.Context
import android.util.Log
import com.example.mynotes.settings.SettingsRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first

internal data class WidgetAppearance(
    val textContext: Context,
    val theme: WidgetPresentation.WidgetThemeSpec
)

/** Una lectura de los cuatro ajustes necesarios, compartida por todas las variantes del lote. */
internal suspend fun loadWidgetAppearance(context: Context): WidgetAppearance {
    val settings = try {
        SettingsRepository(context.applicationContext).widgetSettings.first()
    } catch (cancelled: CancellationException) {
        throw cancelled
    } catch (error: Exception) {
        Log.w("MyNotesWidgets", "Widget settings could not be loaded", error)
        null
    }
    return WidgetAppearance(
        textContext = WidgetLocale.localizedContext(context, settings?.language),
        theme = WidgetPresentation.theme(context, settings)
    )
}

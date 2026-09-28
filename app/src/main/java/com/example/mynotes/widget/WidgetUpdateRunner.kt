package com.example.mynotes.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Ejecuta la actualización de cualquier widget con el mismo ciclo de vida
 * asíncrono. Evita repetir goAsync()/CoroutineScope/try-finally en cada provider.
 */
fun AppWidgetProvider.runWidgetUpdate(
    context: Context,
    manager: AppWidgetManager,
    widgetIds: IntArray,
    block: suspend (Context, AppWidgetManager, IntArray) -> Unit
) {
    val result = goAsync()
    CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
        try {
            block(context, manager, widgetIds)
        } finally {
            result.finish()
        }
    }
}

package com.example.mynotes.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.os.Bundle

/** Todos los widgets refrescan su contenido cuando el launcher cambia sus dimensiones. */
abstract class ResponsiveNoteWidgetProvider : AppWidgetProvider() {
    final override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        refreshWidgets(context, appWidgetManager, appWidgetIds)
    }

    final override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle
    ) {
        refreshWidgets(context, appWidgetManager, intArrayOf(appWidgetId))
    }

    protected abstract fun refreshWidgets(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    )
}

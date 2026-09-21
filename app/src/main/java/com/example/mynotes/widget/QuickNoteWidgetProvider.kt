package com.example.mynotes.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import com.example.mynotes.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class QuickNoteWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        val pendingResult = goAsync()
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            try {
                val theme = WidgetPresentation.theme(context)
                val textContext = WidgetLocale.localizedContext(context)
                appWidgetIds.forEach { appWidgetId ->
                    val views = RemoteViews(context.packageName, R.layout.widget_quick_note)
                    views.setInt(R.id.widget_quick_root, "setBackgroundResource", theme.rootBackgroundRes)
                    views.setInt(R.id.widget_quick_accent, "setBackgroundColor", theme.accent)
                    views.setTextViewText(R.id.widget_quick_title, textContext.getString(R.string.widget_quick_capture))
                    views.setTextViewText(R.id.widget_quick_subtitle, textContext.getString(R.string.widget_quick_capture_hint))
                    views.setTextColor(R.id.widget_quick_title, theme.textPrimary)
                    views.setTextColor(R.id.widget_quick_subtitle, theme.textSecondary)
                    views.setInt(R.id.widget_quick_icon, "setBackgroundResource", theme.actionBackgroundRes)
                    views.setInt(R.id.widget_quick_search, "setBackgroundResource", theme.actionBackgroundRes)
                    views.setInt(R.id.widget_quick_search, "setColorFilter", theme.onAction)
                    views.setInt(R.id.widget_quick_plus, "setBackgroundResource", theme.primaryActionBackgroundRes)
                    views.setInt(R.id.widget_quick_plus, "setColorFilter", theme.onPrimaryAction)
                    views.setCharSequence(R.id.widget_quick_search, "setContentDescription", textContext.getString(R.string.widget_search))
                    views.setCharSequence(R.id.widget_quick_plus, "setContentDescription", textContext.getString(R.string.widget_new_note))
                    views.setOnClickPendingIntent(R.id.widget_quick_root, WidgetIntents.openApp(context, appWidgetId))
                    views.setOnClickPendingIntent(R.id.widget_quick_search, WidgetIntents.search(context, appWidgetId))
                    views.setOnClickPendingIntent(R.id.widget_quick_plus, WidgetIntents.newNote(context, appWidgetId))
                    appWidgetManager.updateAppWidget(appWidgetId, views)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}

package com.example.mynotes.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import com.example.mynotes.R
import com.example.mynotes.data.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class StatsWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        val pendingResult = goAsync()
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            try {
                val dao = AppDatabase.getDatabase(context.applicationContext).noteDao()
                val stats = dao.getWidgetStats()
                val theme = WidgetPresentation.theme(context)
                val textContext = WidgetLocale.localizedContext(context)

                val cellIds = intArrayOf(
                    R.id.widget_stats_favorites,
                    R.id.widget_stats_pinned,
                    R.id.widget_stats_priority,
                    R.id.widget_stats_work,
                    R.id.widget_stats_personal,
                    R.id.widget_stats_all
                )
                val labelIds = intArrayOf(
                    R.id.widget_stats_favorites_label,
                    R.id.widget_stats_pinned_label,
                    R.id.widget_stats_priority_label,
                    R.id.widget_stats_work_label,
                    R.id.widget_stats_personal_label,
                    R.id.widget_stats_all_label
                )
                val valueIds = intArrayOf(
                    R.id.widget_stats_favorites_value,
                    R.id.widget_stats_pinned_value,
                    R.id.widget_stats_priority_value,
                    R.id.widget_stats_work_value,
                    R.id.widget_stats_personal_value,
                    R.id.widget_stats_all_value
                )

                appWidgetIds.forEach { appWidgetId ->
                    val views = RemoteViews(context.packageName, R.layout.widget_stats)
                    views.setInt(R.id.widget_stats_root, "setBackgroundResource", theme.rootBackgroundRes)
                    views.setTextViewText(R.id.widget_stats_title, textContext.getString(R.string.widget_stats))
                    views.setTextViewText(R.id.widget_stats_subtitle, textContext.getString(R.string.widget_stats_hint))
                    views.setTextViewText(R.id.widget_stats_favorites_label, textContext.getString(R.string.widget_favorites))
                    views.setTextViewText(R.id.widget_stats_pinned_label, textContext.getString(R.string.widget_pinned_collection))
                    views.setTextViewText(R.id.widget_stats_priority_label, textContext.getString(R.string.widget_high_priority))
                    views.setTextViewText(R.id.widget_stats_work_label, textContext.getString(R.string.widget_category_work))
                    views.setTextViewText(R.id.widget_stats_personal_label, textContext.getString(R.string.widget_category_personal))
                    views.setTextViewText(R.id.widget_stats_all_label, textContext.getString(R.string.widget_all_notes))
                    views.setTextColor(R.id.widget_stats_title, theme.textPrimary)
                    views.setTextColor(R.id.widget_stats_subtitle, theme.textSecondary)
                    views.setInt(R.id.widget_stats_total, "setBackgroundResource", theme.countBadgeBackgroundRes)
                    views.setTextColor(R.id.widget_stats_total, theme.countBadgeText)
                    cellIds.forEach {
                        views.setInt(it, "setBackgroundResource", theme.metricCardBackgroundRes)
                    }
                    labelIds.forEach { views.setTextColor(it, theme.textSecondary) }
                    valueIds.forEach { views.setTextColor(it, theme.textPrimary) }

                    views.setTextViewText(R.id.widget_stats_total, stats.totalCount.toString())
                    views.setTextViewText(R.id.widget_stats_favorites_value, stats.favoriteCount.toString())
                    views.setTextViewText(R.id.widget_stats_pinned_value, stats.pinnedCount.toString())
                    views.setTextViewText(R.id.widget_stats_priority_value, stats.highPriorityCount.toString())
                    views.setTextViewText(R.id.widget_stats_work_value, stats.workCount.toString())
                    views.setTextViewText(R.id.widget_stats_personal_value, stats.personalCount.toString())
                    views.setTextViewText(R.id.widget_stats_all_value, stats.totalCount.toString())

                    views.setOnClickPendingIntent(R.id.widget_stats_total, WidgetIntents.openCollection(context, WidgetActions.COLLECTION_ALL, appWidgetId + 701))
                    views.setOnClickPendingIntent(R.id.widget_stats_favorites, WidgetIntents.openCollection(context, WidgetActions.COLLECTION_FAVORITES, appWidgetId + 702))
                    views.setOnClickPendingIntent(R.id.widget_stats_pinned, WidgetIntents.openCollection(context, WidgetActions.COLLECTION_PINNED, appWidgetId + 703))
                    views.setOnClickPendingIntent(R.id.widget_stats_priority, WidgetIntents.openCollection(context, WidgetActions.COLLECTION_PRIORITY, appWidgetId + 704))
                    views.setOnClickPendingIntent(R.id.widget_stats_work, WidgetIntents.openCollection(context, WidgetActions.COLLECTION_WORK, appWidgetId + 705))
                    views.setOnClickPendingIntent(R.id.widget_stats_personal, WidgetIntents.openCollection(context, WidgetActions.COLLECTION_PERSONAL, appWidgetId + 706))
                    views.setOnClickPendingIntent(R.id.widget_stats_all, WidgetIntents.openCollection(context, WidgetActions.COLLECTION_ALL, appWidgetId + 707))
                    appWidgetManager.updateAppWidget(appWidgetId, views)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}

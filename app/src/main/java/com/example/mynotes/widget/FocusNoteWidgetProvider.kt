package com.example.mynotes.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.view.View
import android.widget.RemoteViews
import com.example.mynotes.R
import com.example.mynotes.data.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class FocusNoteWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        val pendingResult = goAsync()
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            try {
                val db = AppDatabase.getDatabase(context.applicationContext)
                val note = db.noteDao().getFocusNoteForWidget()
                val theme = WidgetPresentation.theme(context)
                val textContext = WidgetLocale.localizedContext(context)
                val visualMap = WidgetMediaPreview.firstVisualByNote(db.attachmentDao().getAllAttachmentsOnce())
                appWidgetIds.forEach { appWidgetId ->
                    val views = RemoteViews(context.packageName, R.layout.widget_focus_note)
                    views.setInt(R.id.widget_focus_root, "setBackgroundResource", theme.rootBackgroundRes)
                    views.setTextViewText(R.id.widget_focus_title_label, textContext.getString(R.string.widget_focus_note))
                    views.setTextViewText(R.id.widget_focus_subtitle, textContext.getString(R.string.widget_focus_note_hint))
                    views.setTextViewText(R.id.widget_focus_open, textContext.getString(R.string.widget_open_note))
                    views.setTextViewText(R.id.widget_focus_empty, textContext.getString(R.string.widget_empty_notes))
                    views.setTextColor(R.id.widget_focus_title_label, theme.textPrimary)
                    views.setTextColor(R.id.widget_focus_subtitle, theme.textSecondary)
                    views.setTextColor(R.id.widget_focus_empty, theme.textSecondary)
                    views.setInt(R.id.widget_focus_add, "setBackgroundResource", theme.primaryActionBackgroundRes)
                    views.setInt(R.id.widget_focus_add, "setColorFilter", theme.onPrimaryAction)
                    views.setCharSequence(R.id.widget_focus_add, "setContentDescription", textContext.getString(R.string.widget_new_note))
                    views.setCharSequence(R.id.widget_focus_favorite, "setContentDescription", textContext.getString(R.string.widget_toggle_favorite))
                    views.setCharSequence(R.id.widget_focus_pin, "setContentDescription", textContext.getString(R.string.widget_toggle_pin))
                    views.setOnClickPendingIntent(R.id.widget_focus_header, WidgetIntents.openApp(context, appWidgetId))
                    views.setOnClickPendingIntent(R.id.widget_focus_add, WidgetIntents.newNote(context, appWidgetId + 501))

                    if (note == null) {
                        views.setViewVisibility(R.id.widget_focus_card, View.GONE)
                        views.setViewVisibility(R.id.widget_focus_empty, View.VISIBLE)
                        views.setOnClickPendingIntent(R.id.widget_focus_empty, WidgetIntents.newNote(context, appWidgetId + 502))
                    } else {
                        views.setViewVisibility(R.id.widget_focus_card, View.VISIBLE)
                        views.setViewVisibility(R.id.widget_focus_empty, View.GONE)
                        views.setInt(R.id.widget_focus_card, "setBackgroundResource", theme.detailCardBackgroundRes)
                        views.setTextViewText(R.id.widget_focus_title, WidgetPresentation.title(textContext, note))
                        views.setTextViewText(R.id.widget_focus_preview, WidgetPresentation.contentPreview(note, 78).ifBlank { WidgetPresentation.preview(textContext, note) })
                        views.setTextViewText(R.id.widget_focus_meta, WidgetPresentation.metadata(textContext, note))
                        views.setTextViewText(R.id.widget_focus_badge, WidgetPresentation.badge(textContext, note))
                        views.setTextColor(R.id.widget_focus_title, theme.textPrimary)
                        views.setTextColor(R.id.widget_focus_preview, theme.textSecondary)
                        views.setTextColor(R.id.widget_focus_meta, theme.textMuted)
                        views.setInt(R.id.widget_focus_open, "setBackgroundResource", theme.chipBackgroundRes)
                        views.setTextColor(R.id.widget_focus_open, theme.textPrimary)
                        views.setInt(R.id.widget_focus_badge, "setBackgroundResource", theme.actionBackgroundRes)
                        views.setTextColor(R.id.widget_focus_badge, theme.textMuted)
                        views.setInt(R.id.widget_focus_favorite, "setBackgroundResource", theme.actionBackgroundRes)
                        views.setInt(R.id.widget_focus_pin, "setBackgroundResource", theme.actionBackgroundRes)

                        val bitmap = runCatching { WidgetMediaPreview.loadBestPreviewBitmap(context, note, visualMap[note.id], 260, 260) }.getOrNull()
                        views.setInt(R.id.widget_focus_thumb, "setBackgroundResource", theme.thumbnailBackgroundRes)
                        if (bitmap != null) {
                            views.setImageViewBitmap(R.id.widget_focus_thumb, bitmap)
                            views.setViewPadding(R.id.widget_focus_thumb, 0, 0, 0, 0)
                        } else {
                            views.setImageViewResource(R.id.widget_focus_thumb, R.drawable.widget_thumb_placeholder)
                            views.setViewPadding(R.id.widget_focus_thumb, 18, 18, 18, 18)
                            views.setInt(R.id.widget_focus_thumb, "setColorFilter", theme.textMuted)
                        }

                        val open = WidgetIntents.openNote(context, note.id, appWidgetId + 503)
                        views.setOnClickPendingIntent(R.id.widget_focus_card, open)
                        views.setOnClickPendingIntent(R.id.widget_focus_open, open)
                        views.setOnClickPendingIntent(R.id.widget_focus_favorite, WidgetIntents.toggleFavorite(context, note.id, appWidgetId + 504))
                        views.setOnClickPendingIntent(R.id.widget_focus_pin, WidgetIntents.togglePin(context, note.id, appWidgetId + 505))
                        views.setImageViewResource(R.id.widget_focus_favorite, if (note.isFavorite) R.drawable.widget_ic_star else R.drawable.widget_ic_star_outline)
                        views.setImageViewResource(R.id.widget_focus_pin, if (note.isPinned) R.drawable.widget_ic_pin else R.drawable.widget_ic_pin_outline)
                        views.setInt(R.id.widget_focus_favorite, "setColorFilter", if (note.isFavorite) 0xFFE0AA2D.toInt() else theme.onAction)
                        views.setInt(R.id.widget_focus_pin, "setColorFilter", theme.onAction)
                    }
                    appWidgetManager.updateAppWidget(appWidgetId, views)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}

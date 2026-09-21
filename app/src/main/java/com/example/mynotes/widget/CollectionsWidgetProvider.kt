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

class CollectionsWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        val pendingResult = goAsync()
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            try {
                val db = AppDatabase.getDatabase(context.applicationContext)
                val notes = db.noteDao().getRecentNotesForWidget(4)
                val visualMap = WidgetMediaPreview.firstVisualByNote(db.attachmentDao().getAllAttachmentsOnce())
                val theme = WidgetPresentation.theme(context)
                val textContext = WidgetLocale.localizedContext(context)

                appWidgetIds.forEach { appWidgetId ->
                    val views = RemoteViews(context.packageName, R.layout.widget_collections)
                    views.setInt(R.id.widget_collections_root, "setBackgroundResource", theme.rootBackgroundRes)
                    views.setTextViewText(R.id.widget_collections_title, textContext.getString(R.string.widget_note_collage))
                    views.setTextViewText(R.id.widget_collections_subtitle, textContext.getString(R.string.widget_note_collage_hint))
                    views.setTextViewText(R.id.widget_collage_empty, textContext.getString(R.string.widget_empty_notes))
                    views.setTextColor(R.id.widget_collections_title, theme.textPrimary)
                    views.setTextColor(R.id.widget_collections_subtitle, theme.textSecondary)
                    views.setTextColor(R.id.widget_collage_empty, theme.textSecondary)
                    views.setInt(R.id.widget_collections_add, "setBackgroundResource", theme.primaryActionBackgroundRes)
                    views.setInt(R.id.widget_collections_add, "setColorFilter", theme.onPrimaryAction)
                    views.setCharSequence(R.id.widget_collections_add, "setContentDescription", textContext.getString(R.string.widget_new_note))
                    views.setOnClickPendingIntent(R.id.widget_collections_header, WidgetIntents.openApp(context, appWidgetId + 600))
                    views.setOnClickPendingIntent(R.id.widget_collections_add, WidgetIntents.newNote(context, appWidgetId + 601))

                    val cardIds = intArrayOf(
                        R.id.widget_collage_note_1,
                        R.id.widget_collage_note_2,
                        R.id.widget_collage_note_3,
                        R.id.widget_collage_note_4
                    )
                    val thumbIds = intArrayOf(
                        R.id.widget_collage_thumb_1,
                        R.id.widget_collage_thumb_2,
                        R.id.widget_collage_thumb_3,
                        R.id.widget_collage_thumb_4
                    )
                    val titleIds = intArrayOf(
                        R.id.widget_collage_title_1,
                        R.id.widget_collage_title_2,
                        R.id.widget_collage_title_3,
                        R.id.widget_collage_title_4
                    )

                    views.setViewVisibility(R.id.widget_collage_empty, if (notes.isEmpty()) View.VISIBLE else View.GONE)
                    views.setViewVisibility(R.id.widget_collage_grid, if (notes.isEmpty()) View.GONE else View.VISIBLE)

                    cardIds.forEachIndexed { index, cardId ->
                        val note = notes.getOrNull(index)
                        views.setViewVisibility(cardId, if (note != null) View.VISIBLE else View.INVISIBLE)
                        if (note != null) {
                            views.setInt(cardId, "setBackgroundResource", theme.noteRowBackgroundRes)
                            views.setTextColor(titleIds[index], theme.textPrimary)
                            views.setTextViewText(titleIds[index], WidgetPresentation.title(textContext, note))
                            views.setOnClickPendingIntent(cardId, WidgetIntents.openNote(context, note.id, appWidgetId * 10 + index + 610))

                            val bitmap = runCatching {
                                WidgetMediaPreview.loadBestPreviewBitmap(context, note, visualMap[note.id], 180, 180)
                            }.getOrNull()
                            views.setInt(thumbIds[index], "setBackgroundResource", theme.thumbnailBackgroundRes)
                            if (bitmap != null) {
                                views.setImageViewBitmap(thumbIds[index], bitmap)
                                views.setViewPadding(thumbIds[index], 0, 0, 0, 0)
                            } else {
                                views.setImageViewResource(thumbIds[index], R.drawable.widget_thumb_placeholder)
                                views.setViewPadding(thumbIds[index], 12, 12, 12, 12)
                                views.setInt(thumbIds[index], "setColorFilter", theme.textMuted)
                            }
                        }
                    }
                    appWidgetManager.updateAppWidget(appWidgetId, views)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}

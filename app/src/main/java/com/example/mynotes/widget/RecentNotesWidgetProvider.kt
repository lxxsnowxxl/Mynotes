package com.example.mynotes.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.view.View
import android.widget.RemoteViews
import com.example.mynotes.R
import com.example.mynotes.data.AppDatabase
import com.example.mynotes.data.Attachment
import com.example.mynotes.data.Note
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class RecentNotesWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        val pendingResult = goAsync()
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            try {
                val db = AppDatabase.getDatabase(context.applicationContext)
                val notes = db.noteDao().getRecentNotesForWidget(2)
                val total = db.noteDao().getNoteCountForWidget()
                val theme = WidgetPresentation.theme(context)
                val textContext = WidgetLocale.localizedContext(context)
                val visualMap = WidgetMediaPreview.firstVisualByNote(db.attachmentDao().getAllAttachmentsOnce())
                appWidgetIds.forEach { appWidgetId ->
                    updateWidget(context, textContext, appWidgetManager, appWidgetId, notes, total, theme, visualMap)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }

    private suspend fun updateWidget(
        context: Context,
        textContext: Context,
        manager: AppWidgetManager,
        appWidgetId: Int,
        notes: List<Note>,
        total: Int,
        theme: WidgetPresentation.WidgetThemeSpec,
        visualMap: Map<Int, Attachment>
    ) {
        val views = RemoteViews(context.packageName, R.layout.widget_recent_notes)
        views.setInt(R.id.widget_recent_root, "setBackgroundResource", theme.rootBackgroundRes)
        views.setTextViewText(R.id.widget_recent_title, textContext.getString(R.string.widget_recent_notes))
        views.setTextViewText(R.id.widget_recent_empty, textContext.getString(R.string.widget_empty_notes))
        views.setTextColor(R.id.widget_recent_title, theme.textPrimary)
        views.setTextColor(R.id.widget_recent_count, theme.textSecondary)
        views.setTextColor(R.id.widget_recent_empty, theme.textSecondary)
        views.setInt(R.id.widget_recent_search, "setBackgroundResource", theme.actionBackgroundRes)
        views.setInt(R.id.widget_recent_search, "setColorFilter", theme.onAction)
        views.setInt(R.id.widget_recent_add, "setBackgroundResource", theme.primaryActionBackgroundRes)
        views.setInt(R.id.widget_recent_add, "setColorFilter", theme.onPrimaryAction)
        views.setCharSequence(R.id.widget_recent_search, "setContentDescription", textContext.getString(R.string.widget_search))
        views.setCharSequence(R.id.widget_recent_add, "setContentDescription", textContext.getString(R.string.widget_new_note))
        views.setTextViewText(R.id.widget_recent_count, textContext.resources.getQuantityString(R.plurals.widget_note_count, total, total))
        views.setOnClickPendingIntent(R.id.widget_recent_header, WidgetIntents.openCollection(context, WidgetActions.COLLECTION_ALL, appWidgetId))
        views.setOnClickPendingIntent(R.id.widget_recent_search, WidgetIntents.search(context, appWidgetId + 101))
        views.setOnClickPendingIntent(R.id.widget_recent_add, WidgetIntents.newNote(context, appWidgetId + 102))

        val rowIds = intArrayOf(R.id.widget_note_row_1, R.id.widget_note_row_2)
        val titleIds = intArrayOf(R.id.widget_note_title_1, R.id.widget_note_title_2)
        val previewIds = intArrayOf(R.id.widget_note_preview_1, R.id.widget_note_preview_2)
        val metaIds = intArrayOf(R.id.widget_note_meta_1, R.id.widget_note_meta_2)
        val badgeIds = intArrayOf(R.id.widget_note_badge_1, R.id.widget_note_badge_2)
        val accentIds = intArrayOf(R.id.widget_note_accent_1, R.id.widget_note_accent_2)
        val thumbIds = intArrayOf(R.id.widget_note_thumb_1, R.id.widget_note_thumb_2)

        views.setViewVisibility(R.id.widget_recent_empty, if (notes.isEmpty()) View.VISIBLE else View.GONE)
        views.setViewVisibility(R.id.widget_recent_cards, if (notes.isEmpty()) View.GONE else View.VISIBLE)

        rowIds.forEachIndexed { index, rowId ->
            val note = notes.getOrNull(index)
            views.setViewVisibility(rowId, if (note != null) View.VISIBLE else View.GONE)
            if (note != null) {
                views.setInt(rowId, "setBackgroundResource", theme.noteRowBackgroundRes)
                views.setInt(accentIds[index], "setBackgroundColor", WidgetPresentation.noteAccentColor(note, theme.accent))
                views.setTextViewText(titleIds[index], WidgetPresentation.title(textContext, note))
                views.setTextViewText(previewIds[index], WidgetPresentation.contentPreview(note, 32).ifBlank { WidgetPresentation.preview(textContext, note) })
                views.setTextViewText(metaIds[index], WidgetPresentation.metadata(textContext, note))
                views.setTextViewText(badgeIds[index], WidgetPresentation.badge(textContext, note))
                views.setInt(badgeIds[index], "setBackgroundResource", theme.actionBackgroundRes)
                views.setTextColor(titleIds[index], theme.textPrimary)
                views.setTextColor(previewIds[index], theme.textSecondary)
                views.setTextColor(metaIds[index], theme.textMuted)
                views.setTextColor(badgeIds[index], theme.textMuted)

                val bitmap = runCatching { WidgetMediaPreview.loadBestPreviewBitmap(context, note, visualMap[note.id], 220, 180) }.getOrNull()
                views.setInt(thumbIds[index], "setBackgroundResource", theme.thumbnailBackgroundRes)
                if (bitmap != null) {
                    views.setImageViewBitmap(thumbIds[index], bitmap)
                    views.setViewPadding(thumbIds[index], 0, 0, 0, 0)
                } else {
                    views.setImageViewResource(thumbIds[index], R.drawable.widget_thumb_placeholder)
                    views.setViewPadding(thumbIds[index], 16, 16, 16, 16)
                    views.setInt(thumbIds[index], "setColorFilter", theme.textMuted)
                }
                views.setOnClickPendingIntent(rowId, WidgetIntents.openNote(context, note.id, appWidgetId * 10 + index))
            }
        }
        manager.updateAppWidget(appWidgetId, views)
    }
}

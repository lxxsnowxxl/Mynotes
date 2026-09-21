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

class FavoritesWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        val pendingResult = goAsync()
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            try {
                val db = AppDatabase.getDatabase(context.applicationContext)
                val notes = db.noteDao().getFavoriteNotesForWidget(2)
                val favoriteCount = db.noteDao().getFavoriteCountForWidget()
                val theme = WidgetPresentation.theme(context)
                val textContext = WidgetLocale.localizedContext(context)
                val visualMap = WidgetMediaPreview.firstVisualByNote(db.attachmentDao().getAllAttachmentsOnce())
                appWidgetIds.forEach { appWidgetId -> updateWidget(context, textContext, appWidgetManager, appWidgetId, notes, favoriteCount, theme, visualMap) }
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
        val views = RemoteViews(context.packageName, R.layout.widget_favorites)
        views.setInt(R.id.widget_favorites_root, "setBackgroundResource", theme.rootBackgroundRes)
        views.setTextViewText(R.id.widget_favorites_title, textContext.getString(R.string.widget_favorites))
        views.setTextViewText(R.id.widget_favorites_empty, textContext.getString(R.string.widget_empty_favorites))
        views.setTextColor(R.id.widget_favorites_title, theme.textPrimary)
        views.setTextColor(R.id.widget_favorites_count, theme.textSecondary)
        views.setTextColor(R.id.widget_favorites_empty, theme.textSecondary)
        views.setInt(R.id.widget_favorites_add, "setBackgroundResource", theme.primaryActionBackgroundRes)
        views.setInt(R.id.widget_favorites_add, "setColorFilter", theme.onPrimaryAction)
        views.setCharSequence(R.id.widget_favorites_add, "setContentDescription", textContext.getString(R.string.widget_new_note))
        views.setTextViewText(R.id.widget_favorites_count, textContext.resources.getQuantityString(R.plurals.widget_favorite_count, total, total))
        views.setOnClickPendingIntent(R.id.widget_favorites_header, WidgetIntents.openCollection(context, WidgetActions.COLLECTION_FAVORITES, appWidgetId))
        views.setOnClickPendingIntent(R.id.widget_favorites_add, WidgetIntents.newNote(context, appWidgetId + 201))

        val rowIds = intArrayOf(R.id.widget_favorite_row_1, R.id.widget_favorite_row_2)
        val titleIds = intArrayOf(R.id.widget_favorite_title_1, R.id.widget_favorite_title_2)
        val previewIds = intArrayOf(R.id.widget_favorite_preview_1, R.id.widget_favorite_preview_2)
        val metaIds = intArrayOf(R.id.widget_favorite_meta_1, R.id.widget_favorite_meta_2)
        val accentIds = intArrayOf(R.id.widget_favorite_accent_1, R.id.widget_favorite_accent_2)
        val toggleIds = intArrayOf(R.id.widget_favorite_toggle_1, R.id.widget_favorite_toggle_2)
        val thumbIds = intArrayOf(R.id.widget_favorite_thumb_1, R.id.widget_favorite_thumb_2)

        views.setViewVisibility(R.id.widget_favorites_empty, if (notes.isEmpty()) View.VISIBLE else View.GONE)
        views.setViewVisibility(R.id.widget_favorites_cards, if (notes.isEmpty()) View.GONE else View.VISIBLE)

        rowIds.forEachIndexed { index, rowId ->
            val note = notes.getOrNull(index)
            views.setViewVisibility(rowId, if (note != null) View.VISIBLE else View.GONE)
            if (note != null) {
                views.setInt(rowId, "setBackgroundResource", theme.noteRowBackgroundRes)
                views.setInt(accentIds[index], "setBackgroundColor", WidgetPresentation.noteAccentColor(note, theme.accent))
                views.setTextViewText(titleIds[index], WidgetPresentation.title(textContext, note))
                views.setTextViewText(previewIds[index], WidgetPresentation.contentPreview(note, 34).ifBlank { WidgetPresentation.preview(textContext, note) })
                views.setTextViewText(metaIds[index], WidgetPresentation.metadata(textContext, note))
                views.setTextColor(titleIds[index], theme.textPrimary)
                views.setTextColor(previewIds[index], theme.textSecondary)
                views.setTextColor(metaIds[index], theme.textMuted)
                views.setInt(toggleIds[index], "setBackgroundResource", theme.actionBackgroundRes)
                views.setInt(toggleIds[index], "setColorFilter", 0xFFE0AA2D.toInt())
                views.setCharSequence(toggleIds[index], "setContentDescription", textContext.getString(R.string.widget_toggle_favorite))

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

                views.setOnClickPendingIntent(rowId, WidgetIntents.openNote(context, note.id, appWidgetId * 10 + index + 300))
                views.setOnClickPendingIntent(toggleIds[index], WidgetIntents.toggleFavorite(context, note.id, appWidgetId * 10 + index + 400))
            }
        }
        manager.updateAppWidget(appWidgetId, views)
    }
}

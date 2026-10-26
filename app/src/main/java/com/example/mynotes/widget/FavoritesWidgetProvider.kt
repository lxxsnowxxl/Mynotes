package com.example.mynotes.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.widget.RemoteViews
import com.example.mynotes.R
import com.example.mynotes.data.AppDatabase
import com.example.mynotes.data.Note

class FavoritesWidgetProvider : ResponsiveNoteWidgetProvider() {
    protected override fun refreshWidgets(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        launchWidgetUpdate(appWidgetIds) { pendingIds ->
            val db = AppDatabase.getDatabase(context.applicationContext)
            val notes = db.noteDao().getFavoriteNotesForWidget(2)
            val favoriteCount = db.noteDao().getFavoriteCountForWidget()
            val environment = loadNoteWidgetEnvironment(context, db, notes.map { it.id })
            pendingIds.forEach { appWidgetId ->
                updateWidget(
                    context,
                    appWidgetManager,
                    appWidgetId,
                    notes,
                    favoriteCount,
                    environment
                )
            }
        }
    }

    private fun updateWidget(
        context: Context,
        manager: AppWidgetManager,
        appWidgetId: Int,
        notes: List<Note>,
        total: Int,
        environment: NoteWidgetEnvironment
    ) = with(environment) {
        updateSizedWidget(context, manager, appWidgetId) { widgetSize ->
            val layoutRes = if (widgetSize.isTall) R.layout.widget_favorites_tall else R.layout.widget_favorites
            val views = RemoteViews(context.packageName, layoutRes)
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

            val hasNotes = notes.isNotEmpty()
            views.setVisible(R.id.widget_favorites_empty, !hasNotes)
            views.setVisible(R.id.widget_favorites_cards, hasNotes)

            val thumbnailSize = widgetSize.cardThumbnailSize()
            val (previewWidthPx, previewHeightPx) = previewPixels(context, thumbnailSize)
            val previewLength = widgetSize.cardPreviewLength()
            val previewLines = widgetSize.cardPreviewLines()

            rowIds.forEachIndexed { index, rowId ->
                val note = notes.getOrNull(index)
                views.setVisible(rowId, note != null)
                if (note == null) return@forEachIndexed

                WidgetPresentation.bindNoteRow(
                    context = context,
                    textContext = textContext,
                    views = views,
                    note = note,
                    visuals = visualMap[note.id],
                    theme = theme,
                    rowId = rowId,
                    titleId = titleIds[index],
                    previewId = previewIds[index],
                    metaId = metaIds[index],
                    accentId = accentIds[index],
                    thumbId = thumbIds[index],
                    previewLength = previewLength,
                    previewWidthPx = previewWidthPx,
                    previewHeightPx = previewHeightPx
                )
                views.setInt(previewIds[index], "setMaxLines", previewLines)
                views.setInt(toggleIds[index], "setBackgroundResource", theme.actionBackgroundRes)
                views.setInt(toggleIds[index], "setColorFilter", 0xFFE0AA2D.toInt())
                views.setCharSequence(toggleIds[index], "setContentDescription", textContext.getString(R.string.widget_toggle_favorite))
                views.setOnClickPendingIntent(rowId, WidgetIntents.openNote(context, note.id, appWidgetId * 10 + index + 300))
                views.setOnClickPendingIntent(toggleIds[index], WidgetIntents.toggleFavorite(context, note.id, appWidgetId * 10 + index + 400))
            }
            views
        }
    }
}

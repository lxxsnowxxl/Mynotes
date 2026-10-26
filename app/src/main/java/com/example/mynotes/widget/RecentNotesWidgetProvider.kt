package com.example.mynotes.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.widget.RemoteViews
import com.example.mynotes.R
import com.example.mynotes.data.AppDatabase
import com.example.mynotes.data.Note

class RecentNotesWidgetProvider : ResponsiveNoteWidgetProvider() {
    protected override fun refreshWidgets(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        launchWidgetUpdate(appWidgetIds) { pendingIds ->
            val db = AppDatabase.getDatabase(context.applicationContext)
            val notes = db.noteDao().getRecentNotesForWidget(2)
            val total = db.noteDao().getNoteCountForWidget()
            val environment = loadNoteWidgetEnvironment(context, db, notes.map { it.id })
            pendingIds.forEach { appWidgetId ->
                updateWidget(
                    context,
                    appWidgetManager,
                    appWidgetId,
                    notes,
                    total,
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
            val layoutRes = if (widgetSize.isTall) R.layout.widget_recent_notes_tall else R.layout.widget_recent_notes
            val views = RemoteViews(context.packageName, layoutRes)
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

            val hasNotes = notes.isNotEmpty()
            views.setVisible(R.id.widget_recent_empty, !hasNotes)
            views.setVisible(R.id.widget_recent_cards, hasNotes)

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
                views.setTextViewText(badgeIds[index], WidgetPresentation.badge(textContext, note))
                views.setInt(badgeIds[index], "setBackgroundResource", theme.actionBackgroundRes)
                views.setTextColor(badgeIds[index], theme.textMuted)
                views.setOnClickPendingIntent(rowId, WidgetIntents.openNote(context, note.id, appWidgetId * 10 + index))
            }
            views
        }
    }
}

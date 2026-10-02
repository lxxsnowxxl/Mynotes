package com.example.mynotes.widget

import android.content.Context
import android.view.View
import android.widget.RemoteViews
import com.example.mynotes.data.AppDatabase
import com.example.mynotes.data.Attachment

internal data class NoteWidgetEnvironment(
    val textContext: Context,
    val theme: WidgetPresentation.WidgetThemeSpec,
    val visualMap: Map<Int, Attachment>
)

internal suspend fun loadNoteWidgetEnvironment(
    context: Context,
    database: AppDatabase,
    noteIds: List<Int>
): NoteWidgetEnvironment =
    NoteWidgetEnvironment(
        textContext = WidgetLocale.localizedContext(context),
        theme = WidgetPresentation.theme(context),
        visualMap = if (noteIds.isEmpty()) emptyMap() else WidgetMediaPreview.firstVisualByNote(
            database.attachmentDao().getAttachmentsForNotesOnce(noteIds)
        )
    )

internal fun RemoteViews.setVisible(viewId: Int, visible: Boolean) {
    setViewVisibility(viewId, if (visible) View.VISIBLE else View.GONE)
}

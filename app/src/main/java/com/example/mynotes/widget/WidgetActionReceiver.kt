package com.example.mynotes.widget

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.mynotes.data.AppDatabase

class WidgetActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val noteId = intent.getIntExtra(WidgetActions.EXTRA_NOTE_ID, -1)
        if (noteId <= 0) return

        launchAsyncIo {
                val dao = AppDatabase.getDatabase(context.applicationContext).noteDao()
                val note = dao.getNoteByIdOnce(noteId) ?: return@launchAsyncIo

                when (intent.action) {
                    WidgetActions.ACTION_TOGGLE_FAVORITE -> {
                        dao.updateFavorite(noteId, !note.isFavorite)
                    }
                    WidgetActions.ACTION_TOGGLE_PIN -> {
                        dao.updatePinned(noteId, !note.isPinned)
                    }
                }
                MyNotesWidgetUpdater.requestUpdate(context.applicationContext)
        }
    }
}

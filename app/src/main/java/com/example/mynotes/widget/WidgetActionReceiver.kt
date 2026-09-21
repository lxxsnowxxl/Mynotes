package com.example.mynotes.widget

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.mynotes.data.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class WidgetActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val noteId = intent.getIntExtra(WidgetActions.EXTRA_NOTE_ID, -1)
        if (noteId <= 0) return

        val pendingResult = goAsync()
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            try {
                val dao = AppDatabase.getDatabase(context.applicationContext).noteDao()
                val note = dao.getNoteByIdOnce(noteId) ?: return@launch

                when (intent.action) {
                    WidgetActions.ACTION_TOGGLE_FAVORITE -> {
                        dao.updateFavorite(noteId, !note.isFavorite)
                    }
                    WidgetActions.ACTION_TOGGLE_PIN -> {
                        dao.updatePinned(noteId, !note.isPinned)
                    }
                }
                MyNotesWidgetUpdater.requestUpdate(context.applicationContext)
            } finally {
                pendingResult.finish()
            }
        }
    }
}

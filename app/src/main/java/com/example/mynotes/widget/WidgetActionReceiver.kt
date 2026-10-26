package com.example.mynotes.widget

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.mynotes.data.AppDatabase

class WidgetActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action != WidgetActions.ACTION_TOGGLE_FAVORITE && action != WidgetActions.ACTION_TOGGLE_PIN) {
            return
        }

        val noteId = intent.getIntExtra(WidgetActions.EXTRA_NOTE_ID, -1)
        if (noteId <= 0) return

        launchAsyncIo {
            val dao = AppDatabase.getDatabase(context.applicationContext).noteDao()
            val changedRows = if (action == WidgetActions.ACTION_TOGGLE_FAVORITE) {
                dao.toggleFavorite(noteId)
            } else {
                dao.togglePinned(noteId)
            }
            if (changedRows > 0) {
                MyNotesWidgetUpdater.requestUpdate(context.applicationContext)
            }
        }
    }
}

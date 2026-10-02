package com.example.mynotes

import android.content.Intent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.mynotes.reminders.ReminderReceiver
import com.example.mynotes.widget.WidgetActions

/** Centralizes transient external-navigation state consumed by MainActivity's Compose tree. */
internal class MainIntentState {
    var sharedText by mutableStateOf<String?>(null)
    var sharedTitle by mutableStateOf<String?>(null)
    var widgetNewNote by mutableStateOf(false)
    var widgetNoteId by mutableStateOf<Int?>(null)
    var widgetCollection by mutableStateOf<String?>(null)
    var widgetSearch by mutableStateOf(false)
    var widgetNavigationToken by mutableIntStateOf(0)
    var openReminders by mutableStateOf(false)

    fun handle(intent: Intent?) {
        handleShare(intent)
        handleWidget(intent)
        if (intent?.getBooleanExtra(ReminderReceiver.EXTRA_OPEN_REMINDERS, false) == true) openReminders = true
    }

    fun clearShare() {
        sharedText = null
        sharedTitle = null
    }

    private fun prepareWidget(newNote: Boolean = false, noteId: Int? = null, collection: String? = null, search: Boolean = false) {
        clearShare()
        widgetNewNote = newNote
        widgetNoteId = noteId
        widgetCollection = collection
        widgetSearch = search
        widgetNavigationToken++
    }

    private fun handleWidget(intent: Intent?) {
        when (intent?.action) {
            WidgetActions.ACTION_NEW_NOTE -> prepareWidget(newNote = true)
            WidgetActions.ACTION_OPEN_NOTE -> prepareWidget(
                noteId = intent.getIntExtra(WidgetActions.EXTRA_NOTE_ID, -1).takeIf { it > 0 }
            )
            WidgetActions.ACTION_OPEN_COLLECTION -> prepareWidget(
                collection = intent.getStringExtra(WidgetActions.EXTRA_COLLECTION) ?: WidgetActions.COLLECTION_ALL
            )
            WidgetActions.ACTION_SEARCH -> prepareWidget(collection = WidgetActions.COLLECTION_ALL, search = true)
        }
    }

    private fun handleShare(intent: Intent?) {
        if (intent?.action != Intent.ACTION_SEND) return
        val text = intent.getCharSequenceExtra(Intent.EXTRA_TEXT)?.toString()?.trim().orEmpty()
        if (text.isBlank()) return
        sharedText = text
        sharedTitle = intent.getCharSequenceExtra(Intent.EXTRA_SUBJECT)?.toString()?.trim()?.takeIf { it.isNotBlank() }
    }
}

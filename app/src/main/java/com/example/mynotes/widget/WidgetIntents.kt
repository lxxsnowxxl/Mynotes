package com.example.mynotes.widget

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.mynotes.MainActivity

object WidgetIntents {
    private const val ACTIVITY_FLAGS = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
    private const val PENDING_FLAGS = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE

    fun openApp(context: Context, uniqueId: Int) = activity(context, 10_000 + uniqueId, Intent.ACTION_MAIN,
        "mynotes://widget/app/$uniqueId") { addCategory(Intent.CATEGORY_LAUNCHER) }

    fun newNote(context: Context, uniqueId: Int) = activity(context, 20_000 + uniqueId, WidgetActions.ACTION_NEW_NOTE,
        "mynotes://widget/new/$uniqueId")

    fun openNote(context: Context, noteId: Int, uniqueId: Int) = activity(context, 30_000 + uniqueId,
        WidgetActions.ACTION_OPEN_NOTE, "mynotes://widget/note/$noteId/$uniqueId") {
        putExtra(WidgetActions.EXTRA_NOTE_ID, noteId)
    }

    fun openCollection(context: Context, collection: String, uniqueId: Int) = activity(context, 40_000 + uniqueId,
        WidgetActions.ACTION_OPEN_COLLECTION, "mynotes://widget/collection/$collection/$uniqueId") {
        putExtra(WidgetActions.EXTRA_COLLECTION, collection)
    }

    fun search(context: Context, uniqueId: Int) = activity(context, 50_000 + uniqueId, WidgetActions.ACTION_SEARCH,
        "mynotes://widget/search/$uniqueId")

    fun toggleFavorite(context: Context, noteId: Int, uniqueId: Int) = broadcast(context, 60_000 + uniqueId,
        WidgetActions.ACTION_TOGGLE_FAVORITE, "mynotes://widget/favorite/$noteId/$uniqueId", noteId)

    fun togglePin(context: Context, noteId: Int, uniqueId: Int) = broadcast(context, 70_000 + uniqueId,
        WidgetActions.ACTION_TOGGLE_PIN, "mynotes://widget/pin/$noteId/$uniqueId", noteId)

    private fun activity(
        context: Context,
        requestCode: Int,
        action: String,
        uri: String,
        configure: Intent.() -> Unit = {}
    ): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            this.action = action
            data = Uri.parse(uri)
            flags = ACTIVITY_FLAGS
            configure()
        }
        return PendingIntent.getActivity(context, requestCode, intent, PENDING_FLAGS)
    }

    private fun broadcast(context: Context, requestCode: Int, action: String, uri: String, noteId: Int): PendingIntent {
        val intent = Intent(context, WidgetActionReceiver::class.java).apply {
            this.action = action
            data = Uri.parse(uri)
            putExtra(WidgetActions.EXTRA_NOTE_ID, noteId)
        }
        return PendingIntent.getBroadcast(context, requestCode, intent, PENDING_FLAGS)
    }
}

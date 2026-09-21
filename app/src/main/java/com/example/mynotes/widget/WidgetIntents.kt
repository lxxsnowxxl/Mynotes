package com.example.mynotes.widget

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.mynotes.MainActivity

object WidgetIntents {
    private const val ACTIVITY_FLAGS = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
    private const val PENDING_FLAGS = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE

    fun openApp(context: Context, uniqueId: Int): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            action = Intent.ACTION_MAIN
            addCategory(Intent.CATEGORY_LAUNCHER)
            data = Uri.parse("mynotes://widget/app/$uniqueId")
            flags = ACTIVITY_FLAGS
        }
        return PendingIntent.getActivity(context, 10_000 + uniqueId, intent, PENDING_FLAGS)
    }

    fun newNote(context: Context, uniqueId: Int): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            action = WidgetActions.ACTION_NEW_NOTE
            data = Uri.parse("mynotes://widget/new/$uniqueId")
            flags = ACTIVITY_FLAGS
        }
        return PendingIntent.getActivity(context, 20_000 + uniqueId, intent, PENDING_FLAGS)
    }

    fun openNote(context: Context, noteId: Int, uniqueId: Int): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            action = WidgetActions.ACTION_OPEN_NOTE
            data = Uri.parse("mynotes://widget/note/$noteId/$uniqueId")
            putExtra(WidgetActions.EXTRA_NOTE_ID, noteId)
            flags = ACTIVITY_FLAGS
        }
        return PendingIntent.getActivity(context, 30_000 + uniqueId, intent, PENDING_FLAGS)
    }

    fun openCollection(context: Context, collection: String, uniqueId: Int): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            action = WidgetActions.ACTION_OPEN_COLLECTION
            data = Uri.parse("mynotes://widget/collection/$collection/$uniqueId")
            putExtra(WidgetActions.EXTRA_COLLECTION, collection)
            flags = ACTIVITY_FLAGS
        }
        return PendingIntent.getActivity(context, 40_000 + uniqueId, intent, PENDING_FLAGS)
    }

    fun search(context: Context, uniqueId: Int): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            action = WidgetActions.ACTION_SEARCH
            data = Uri.parse("mynotes://widget/search/$uniqueId")
            flags = ACTIVITY_FLAGS
        }
        return PendingIntent.getActivity(context, 50_000 + uniqueId, intent, PENDING_FLAGS)
    }

    fun toggleFavorite(context: Context, noteId: Int, uniqueId: Int): PendingIntent {
        val intent = Intent(context, WidgetActionReceiver::class.java).apply {
            action = WidgetActions.ACTION_TOGGLE_FAVORITE
            data = Uri.parse("mynotes://widget/favorite/$noteId/$uniqueId")
            putExtra(WidgetActions.EXTRA_NOTE_ID, noteId)
        }
        return PendingIntent.getBroadcast(context, 60_000 + uniqueId, intent, PENDING_FLAGS)
    }

    fun togglePin(context: Context, noteId: Int, uniqueId: Int): PendingIntent {
        val intent = Intent(context, WidgetActionReceiver::class.java).apply {
            action = WidgetActions.ACTION_TOGGLE_PIN
            data = Uri.parse("mynotes://widget/pin/$noteId/$uniqueId")
            putExtra(WidgetActions.EXTRA_NOTE_ID, noteId)
        }
        return PendingIntent.getBroadcast(context, 70_000 + uniqueId, intent, PENDING_FLAGS)
    }
}

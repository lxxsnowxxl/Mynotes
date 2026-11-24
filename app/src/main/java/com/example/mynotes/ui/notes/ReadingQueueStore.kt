package com.example.mynotes.ui.notes

import android.content.Context

/** Local, explicitly selected reading queue; does not change the notes database schema. */
internal object ReadingQueueStore {
    private const val FILE = "reading_queue_v319"
    private const val KEY = "note_ids"

    fun ids(context: Context): Set<Int> = context.applicationContext
        .getSharedPreferences(FILE, Context.MODE_PRIVATE)
        .getStringSet(KEY, emptySet()).orEmpty().mapNotNull { it.toIntOrNull()?.takeIf { id -> id > 0 } }.toSet()

    fun toggle(context: Context, noteId: Int): Set<Int> {
        if (noteId <= 0) return ids(context)
        val current = ids(context).toMutableSet()
        if (!current.add(noteId)) current.remove(noteId)
        context.applicationContext.getSharedPreferences(FILE, Context.MODE_PRIVATE)
            .edit().putStringSet(KEY, current.mapTo(mutableSetOf()) { it.toString() }).apply()
        return current
    }
}

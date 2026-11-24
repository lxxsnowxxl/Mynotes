package com.example.mynotes.ui.state

import com.example.mynotes.data.Note
import com.example.mynotes.settings.DesignPreferencePolicy
import java.util.Calendar
import java.util.TimeZone

enum class NoteSectionKind { PINNED, TODAY, THIS_WEEK, EARLIER, NOTES }

sealed class NoteFeedItem(val key: String) {
    class Header(val kind: NoteSectionKind, val count: Int) : NoteFeedItem("section_${kind.name}")
    class Entry(val note: Note) : NoteFeedItem("note_${note.id}")
}

/** Local calendar boundaries handle daylight-saving changes without 24-hour arithmetic. */
object NoteSections {
    fun nextMidnight(now: Long, timeZone: TimeZone = TimeZone.getDefault()): Long =
        midnight(now, timeZone).apply { add(Calendar.DAY_OF_MONTH, 1) }.timeInMillis

    private fun midnight(now: Long, timeZone: TimeZone) = Calendar.getInstance(timeZone).apply {
        timeInMillis = now
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    fun build(notes: List<Note>, sortOrder: String, now: Long,
        timeZone: TimeZone = TimeZone.getDefault()): List<NoteFeedItem> {
        val order = DesignPreferencePolicy.sortOrder(sortOrder)
        val comparator = when (order) {
            "oldest" -> compareBy<Note> { it.createdAt }.thenBy { it.id }
            "title" -> compareBy(String.CASE_INSENSITIVE_ORDER) { note: Note -> note.title }.thenBy { it.id }
            "priority" -> compareByDescending<Note> { it.priority }.thenByDescending { it.createdAt }.thenBy { it.id }
            else -> compareByDescending<Note> { it.createdAt }.thenByDescending { it.id }
        }
        val today = midnight(now, timeZone)
        val week = (today.clone() as Calendar).apply {
            val days = (get(Calendar.DAY_OF_WEEK) - firstDayOfWeek + 7) % 7
            add(Calendar.DAY_OF_MONTH, -days)
        }.timeInMillis
        val groups = notes.sortedWith(comparator).groupBy { note ->
            when {
                note.isPinned -> NoteSectionKind.PINNED
                order == "title" || order == "priority" -> NoteSectionKind.NOTES
                note.createdAt >= today.timeInMillis -> NoteSectionKind.TODAY
                note.createdAt >= week -> NoteSectionKind.THIS_WEEK
                else -> NoteSectionKind.EARLIER
            }
        }
        val dates = if (order == "oldest") listOf(NoteSectionKind.EARLIER, NoteSectionKind.THIS_WEEK, NoteSectionKind.TODAY)
            else listOf(NoteSectionKind.TODAY, NoteSectionKind.THIS_WEEK, NoteSectionKind.EARLIER)
        return buildList {
            (listOf(NoteSectionKind.PINNED) + dates + NoteSectionKind.NOTES).forEach { kind ->
                groups[kind]?.takeIf { it.isNotEmpty() }?.let { items ->
                    add(NoteFeedItem.Header(kind, items.size))
                    items.forEach { add(NoteFeedItem.Entry(it)) }
                }
            }
        }
    }
}

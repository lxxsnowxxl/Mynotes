package com.example.mynotes.ui.state

import com.example.mynotes.data.Note
import org.junit.Assert.*
import org.junit.Test
import java.util.Calendar
import java.util.TimeZone

class NoteSectionsTest {
    private val utc = TimeZone.getTimeZone("UTC")
    private fun date(day: Int, hour: Int = 12, month: Int = Calendar.NOVEMBER,
        zone: TimeZone = utc): Long = Calendar.getInstance(zone).apply {
        clear(); set(2026, month, day, hour, 0, 0)
    }.timeInMillis
    private fun entries(items: List<NoteFeedItem>) = items.filterIsInstance<NoteFeedItem.Entry>().map { it.note }
    private fun headers(items: List<NoteFeedItem>) = items.filterIsInstance<NoteFeedItem.Header>().map { it.kind }

    @Test fun pinnedNotesAppearOnceAndEmptySectionsAreOmitted() {
        val notes = listOf(Note(1, "Old pinned", "", date(1), isPinned = true),
            Note(2, "Today", "", date(6)), Note(3, "Thursday", "", date(5)), Note(4, "Old", "", date(20, month = Calendar.OCTOBER)))
        val result = NoteSections.build(notes, "newest", date(6), utc)
        assertEquals(listOf(NoteSectionKind.PINNED, NoteSectionKind.TODAY, NoteSectionKind.THIS_WEEK, NoteSectionKind.EARLIER), headers(result))
        assertEquals(listOf(1, 2, 3, 4), entries(result).map { it.id })
        assertEquals(4, result.filterIsInstance<NoteFeedItem.Header>().sumOf { it.count })
    }

    @Test fun todayStartsAtLocalMidnight() {
        val midnight = date(6, 0)
        val result = NoteSections.build(listOf(Note(1, "", "", midnight), Note(2, "", "", midnight - 1)), "newest", date(6), utc)
        assertEquals(listOf(NoteSectionKind.TODAY, NoteSectionKind.THIS_WEEK), headers(result))
    }

    @Test fun weekStartUsesDeviceCalendar() {
        val now = date(6)
        val start = Calendar.getInstance(utc).apply {
            timeInMillis = now
            set(Calendar.HOUR_OF_DAY, 0)
            add(Calendar.DAY_OF_MONTH, -(get(Calendar.DAY_OF_WEEK) - firstDayOfWeek + 7) % 7)
        }.timeInMillis
        val result = NoteSections.build(listOf(Note(1, "", "", start), Note(2, "", "", start - 1)), "newest", now, utc)
        assertEquals(listOf(NoteSectionKind.THIS_WEEK, NoteSectionKind.EARLIER), headers(result))
    }

    @Test fun oldestOrderRespectsChronologyAndKeepsPinnedFirst() {
        val notes = listOf(Note(1, "Pinned", "", date(6), isPinned = true), Note(2, "Today", "", date(6)),
            Note(3, "This week", "", date(5)), Note(4, "Old", "", date(20, month = Calendar.OCTOBER)))
        assertEquals(listOf(1, 4, 3, 2), entries(NoteSections.build(notes, "oldest", date(6), utc)).map { it.id })
    }

    @Test fun alphabeticalOrderIsGlobalWithinPinnedAndUnpinnedGroups() {
        val notes = listOf(Note(1, "zeta", "", date(6)), Note(2, "Alpha", "", date(1)),
            Note(3, "beta", "", date(4)), Note(4, "Pinned", "", date(1), isPinned = true))
        val result = NoteSections.build(notes, "title", date(6), utc)
        assertEquals(listOf(NoteSectionKind.PINNED, NoteSectionKind.NOTES), headers(result))
        assertEquals(listOf(4, 2, 3, 1), entries(result).map { it.id })
    }

    @Test fun priorityOrderBreaksTiesByDate() {
        val notes = listOf(Note(1, "", "", date(1), priority = 3), Note(2, "", "", date(6), priority = 1),
            Note(3, "", "", date(5), priority = 3))
        assertEquals(listOf(3, 1, 2), entries(NoteSections.build(notes, "priority", date(6), utc)).map { it.id })
    }

    @Test fun everyNoteHasOneStableKeyAcrossGroupingAndSortChanges() {
        val notes = (1..1000).map { id -> Note(id, "Note $id", "", date(6) - id * 3_600_000L, isPinned = id % 7 == 0) }
        for (order in listOf("newest", "oldest", "title", "priority")) {
            val result = NoteSections.build(notes, order, date(6), utc)
            assertEquals(notes.map { it.id }.toSet(), entries(result).map { it.id }.toSet())
            assertEquals(result.size, result.map { it.key }.toSet().size)
        }
    }

    @Test fun emptyCollectionHasNoHeaders() {
        assertTrue(NoteSections.build(emptyList(), "newest", date(6), utc).isEmpty())
    }

    @Test fun nextMidnightHandlesShortDayAtDaylightSavingTransition() {
        val zone = TimeZone.getTimeZone("America/New_York")
        val start = date(8, 0, Calendar.MARCH, zone)
        assertEquals(23 * 3_600_000L, NoteSections.nextMidnight(start, zone) - start)
    }

    @Test fun nextMidnightHandlesLongDayAtDaylightSavingTransition() {
        val zone = TimeZone.getTimeZone("America/New_York")
        val start = date(1, 0, Calendar.NOVEMBER, zone)
        assertEquals(25 * 3_600_000L, NoteSections.nextMidnight(start, zone) - start)
    }
}

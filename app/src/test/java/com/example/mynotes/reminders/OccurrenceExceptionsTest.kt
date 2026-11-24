package com.example.mynotes.reminders

import org.junit.Assert.*
import org.junit.Test
import java.util.Calendar
import java.util.TimeZone

class OccurrenceExceptionsTest {
    private val zone = TimeZone.getTimeZone("UTC")
    private fun at(day: Int) = Calendar.getInstance(zone).apply { clear(); set(2026, Calendar.NOVEMBER, day, 9, 0) }.timeInMillis
    @Test fun nextAndPreviewSkipExcludedDates() {
        val reminder = Reminder(id = 1, title = "Daily", triggerAtMillis = at(1), repeatMode = Reminder.REPEAT_DAILY, excludedOccurrences = setOf(at(2), at(4)))
        assertEquals(at(3), ReminderSchedule.next(reminder, at(1), zone))
        assertEquals(listOf(at(1), at(3), at(5), at(6)), ReminderSchedule.preview(reminder, at(1), 4, zone))
    }
    @Test fun exclusionStillCountsTowardASeriesOccurrenceLimit() {
        val reminder = Reminder(id = 1, title = "Daily", triggerAtMillis = at(1), repeatMode = Reminder.REPEAT_DAILY, maxOccurrences = 3, excludedOccurrences = setOf(at(2)))
        assertEquals(listOf(at(1), at(3)), ReminderSchedule.preview(reminder, at(1), 10, zone))
    }
    @Test fun endDateAndOneOffBoundThePreview() {
        val reminder = Reminder(id = 1, title = "Daily", triggerAtMillis = at(1), repeatMode = Reminder.REPEAT_DAILY, repeatEndAtMillis = at(3))
        assertEquals(listOf(at(1), at(2), at(3)), ReminderSchedule.preview(reminder, at(1), 20, zone))
        assertEquals(emptyList<Long>(), ReminderSchedule.preview(reminder.copy(repeatMode = Reminder.REPEAT_NONE), at(2), 20, zone))
    }
    @Test fun excludedCurrentOccurrenceIsNeverScheduled() {
        val reminder = Reminder(id = 1, title = "Daily", triggerAtMillis = at(2), repeatMode = Reminder.REPEAT_DAILY, excludedOccurrences = setOf(at(2)))
        assertNull(ReminderSchedule.alarmAt(reminder, at(1))); assertEquals(at(3), ReminderSchedule.enableAt(reminder, at(1), zone))
    }
    @Test fun exceptionsAndLibrarySurviveJsonRoundTrip() {
        val reminder = Reminder(id = 1, title = "Unicode 🌿", triggerAtMillis = at(1), repeatMode = Reminder.REPEAT_DAILY, excludedOccurrences = setOf(at(2)), libraryId = "work")
        assertEquals(reminder, remindersFromJson(listOf(reminder).toJsonString()).single())
    }
}

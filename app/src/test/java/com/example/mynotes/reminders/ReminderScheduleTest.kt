package com.example.mynotes.reminders

import org.junit.Assert.*
import org.junit.Test
import java.util.Calendar
import java.util.TimeZone

class ReminderScheduleTest {
    private val zone = TimeZone.getTimeZone("America/New_York")
    private fun date(year: Int, month: Int, day: Int, hour: Int = 9, minute: Int = 0): Long = Calendar.getInstance(zone).apply {
        clear(); set(year, month - 1, day, hour, minute)
    }.timeInMillis
    private fun reminder(time: Long, mode: String = Reminder.REPEAT_DAILY) = Reminder(id = 12, title = "Task", triggerAtMillis = time, repeatMode = mode, createdAt = 1)
    @Test fun dailyKeepsLocalClockAcrossSpringDst() {
        val initial = date(2026, 3, 7); val next = ReminderSchedule.next(reminder(initial), initial, zone)
        assertEquals(date(2026, 3, 8), next); assertEquals(23 * 3600000L, next!! - initial)
    }
    @Test fun dailyKeepsLocalClockAcrossAutumnDst() {
        val initial = date(2026, 10, 31); val next = ReminderSchedule.next(reminder(initial), initial, zone)
        assertEquals(date(2026, 11, 1), next); assertEquals(25 * 3600000L, next!! - initial)
    }
    @Test fun customDaysSkipWeekendAndMissedOccurrences() {
        val initial = date(2026, 11, 6)
        val task = reminder(initial, Reminder.REPEAT_CUSTOM).copy(weekDays = setOf(Calendar.MONDAY, Calendar.WEDNESDAY))
        assertEquals(date(2026, 11, 9), ReminderSchedule.next(task, initial, zone))
        assertEquals(date(2026, 11, 11), ReminderSchedule.next(task, date(2026, 11, 10, 20), zone))
    }
    @Test fun emptyCustomDaysCannotSchedule() { assertNull(ReminderSchedule.next(reminder(1, Reminder.REPEAT_CUSTOM), 2, zone)) }
    @Test fun weekdaysSkipSaturdayAndSunday() { val t = date(2026, 11, 6); assertEquals(date(2026, 11, 9), ReminderSchedule.next(reminder(t, Reminder.REPEAT_WEEKDAYS), t, zone)) }
    @Test fun repeatEndIsInclusiveAndNeverSchedulesAfterIt() {
        val t = date(2026, 11, 6); val task = reminder(t).copy(repeatEndAtMillis = date(2026, 11, 7))
        assertEquals(date(2026, 11, 7), ReminderSchedule.next(task, t, zone))
        assertNull(ReminderSchedule.next(task, date(2026, 11, 7), zone))
    }
    @Test fun monthlyClampsRatherThanSkippingFebruary() {
        val t = date(2026, 1, 31); assertEquals(date(2026, 2, 28), ReminderSchedule.next(reminder(t, Reminder.REPEAT_MONTHLY), t, zone))
    }
    @Test fun weeklyKeepsWeekdayAfterLongAbsence() {
        val t = date(2026, 10, 2); assertEquals(date(2026, 11, 13), ReminderSchedule.next(reminder(t, Reminder.REPEAT_WEEKLY), date(2026, 11, 8), zone))
    }
    @Test fun snoozeCanFireWhenOneOffAlreadyCompleted() {
        val task = reminder(100, Reminder.REPEAT_NONE).copy(enabled = false, snoozedUntil = 900)
        assertEquals(900L, ReminderSchedule.alarmAt(task, 200)); assertNull(ReminderSchedule.alarmAt(task, 901))
    }
    @Test fun regularAndSnoozeOccurrencesKeepTheirIndependentTimes() {
        val task = reminder(1000).copy(snoozedUntil = 700)
        assertEquals(700L, ReminderSchedule.alarmAt(task, 500))
        assertEquals(1000L, ReminderSchedule.alarmAt(task.copy(snoozedUntil = null), 500))
        assertEquals(1000L, task.triggerAtMillis)
    }
    @Test fun expiredSeriesCannotAppearEnabledWithoutAFutureAlarm() {
        val task = reminder(date(2026, 11, 1)).copy(enabled = false, repeatEndAtMillis = date(2026, 11, 2))
        assertNull(ReminderSchedule.enableAt(task, date(2026, 11, 6), zone))
    }
    @Test fun reenableRejectsSeriesWithNoOccurrenceBeforeItsEnd() {
        val task = reminder(date(2026, 11, 6)).copy(enabled = false, repeatEndAtMillis = date(2026, 11, 6, 23))
        assertNull(ReminderSchedule.enableAt(task, date(2026, 11, 6, 10), zone))
    }
    @Test fun completedOneOffCanBeReenabledInFiveMinutes() {
        val task = reminder(100L, Reminder.REPEAT_NONE).copy(enabled = false)
        assertEquals(301000L, ReminderSchedule.enableAt(task, 1000L, zone))
    }
    @Test fun legacyJsonDefaultsNewFields() {
        val task = remindersFromJson("""[{"id":4,"title":"Legacy","triggerAtMillis":123}]""").single()
        assertTrue(task.weekDays.isEmpty()); assertNull(task.repeatEndAtMillis); assertNull(task.linkedNoteId); assertNull(task.snoozedUntil)
        assertTrue(task.enabled)
    }
    @Test fun reminderJsonRoundTripKeepsAllWorkspaceFields() {
        val task = reminder(1000, Reminder.REPEAT_CUSTOM).copy(weekDays = setOf(2, 4, 6), repeatEndAtMillis = 9000, linkedNoteId = 42, snoozedUntil = 2000)
        assertEquals(task, remindersFromJson(listOf(task).toJsonString()).single())
    }
}

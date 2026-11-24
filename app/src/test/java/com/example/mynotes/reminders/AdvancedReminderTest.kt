package com.example.mynotes.reminders

import org.junit.Assert.*
import org.junit.Test
import java.util.Calendar
import java.util.TimeZone

class AdvancedReminderTest {
    private val utc=TimeZone.getTimeZone("UTC")
    private fun date(y:Int,m:Int,d:Int,h:Int=9,zone:TimeZone=utc)=Calendar.getInstance(zone).apply{clear();set(y,m-1,d,h,0,0)}.timeInMillis
    private fun monthly(start:Long)=Reminder(title="Monthly",triggerAtMillis=start,repeatMode=Reminder.REPEAT_MONTHLY,recurrenceAnchorMillis=start)
    @Test fun threeWeekIntervalKeepsOriginalAnchor() {
        val start=date(2026,1,5);val r=Reminder(title="Review",triggerAtMillis=start,repeatMode=Reminder.REPEAT_WEEKLY,repeatInterval=3,recurrenceAnchorMillis=start)
        assertEquals(date(2026,1,26),ReminderSchedule.next(r,date(2026,1,8),utc))
    }
    @Test fun twoMonthIntervalClampsDayAndThenReturnsToThirtyFirst() {
        val r=monthly(date(2026,1,31)).copy(repeatInterval=2)
        assertEquals(date(2026,3,31),ReminderSchedule.next(r,r.triggerAtMillis,utc))
        val feb=monthly(date(2026,1,31)).copy(triggerAtMillis=date(2026,2,28))
        assertEquals(date(2026,3,31),ReminderSchedule.next(feb,feb.triggerAtMillis,utc))
    }
    @Test fun fifthMondaySkipsMonthsWithoutThatDay() {
        val r=monthly(date(2026,3,30)).copy(monthOrdinal=5,monthWeekday=Calendar.MONDAY)
        assertEquals(date(2026,6,29),ReminderSchedule.next(r,r.triggerAtMillis,utc))
    }
    @Test fun lastFridayWorksAcrossDifferentMonthLengths() {
        val r=monthly(date(2026,1,30)).copy(monthOrdinal=-1,monthWeekday=Calendar.FRIDAY)
        assertEquals(date(2026,2,27),ReminderSchedule.next(r,r.triggerAtMillis,utc))
    }
    @Test fun intervalKeepsLocalClockAcrossDaylightSaving() {
        val zone=TimeZone.getTimeZone("America/New_York");val start=date(2026,3,7,9,zone)
        val r=Reminder(title="Local",triggerAtMillis=start,repeatMode=Reminder.REPEAT_DAILY,repeatInterval=2,recurrenceAnchorMillis=start)
        assertEquals(date(2026,3,9,9,zone),ReminderSchedule.next(r,start,zone))
    }
    @Test fun occurrenceLimitStopsAfterConfiguredCount() {
        val r=monthly(date(2026,1,1)).copy(maxOccurrences=3,occurrencesDone=2)
        assertNull(ReminderSchedule.next(r,r.triggerAtMillis,utc))
        assertNull(ReminderSchedule.enableAt(r.copy(occurrencesDone=3),0,utc))
    }
    @Test fun locationOnlyReminderDoesNotCreateAnOrdinaryDateAlarm() {
        val r=monthly(date(2026,1,1)).copy(locationOnly=true)
        assertNull(ReminderSchedule.next(r,0,utc));assertNull(ReminderSchedule.alarmAt(r,0))
        assertEquals(1234L,ReminderSchedule.alarmAt(r.copy(snoozedUntil=1234),0))
    }
    @Test fun recurrenceCannotExceedSeriesEndDate() {
        val r=monthly(date(2026,1,31)).copy(repeatEndAtMillis=date(2026,2,27))
        assertNull(ReminderSchedule.next(r,r.triggerAtMillis,utc))
    }
    @Test fun advancedFieldsRoundTripAndOldRemindersKeepDefaults() {
        val r=monthly(date(2026,1,31)).copy(repeatInterval=2,monthOrdinal=-1,monthWeekday=6,maxOccurrences=9,occurrencesDone=2,alertMinutes=listOf(5,60))
        assertEquals(r,remindersFromJson(listOf(r).toJsonString()).single())
        val old=remindersFromJson("[{\"id\":1,\"title\":\"Old\",\"triggerAtMillis\":100}]").single()
        assertEquals(1,old.repeatInterval);assertNull(old.maxOccurrences);assertTrue(old.alertMinutes.isEmpty())
    }
    @Test fun malformedAlertOffsetsAreFilteredAndBounded() {
        val r=remindersFromJson("[{\"title\":\"Bounds\",\"repeatInterval\":999,\"alertMinutes\":[-1,0,5,5,10081,60]}]").single()
        assertEquals(365,r.repeatInterval);assertEquals(listOf(5,60),r.alertMinutes)
    }
}

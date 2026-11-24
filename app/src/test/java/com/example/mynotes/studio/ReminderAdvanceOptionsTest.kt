package com.example.mynotes.studio

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ReminderAdvanceOptionsTest {
    @Test fun offsetsAreNormalizedAndLimited() {
        assertEquals(listOf(5, 30, 60), ReminderAdvanceOptions.normalize(listOf(60, -10, 5, 30, 5, 10081)))
        assertEquals(12, ReminderAdvanceOptions.normalize((1..40).toList()).size)
    }

    @Test fun unitsAreConvertedWithoutOverflow() {
        assertEquals(45, ReminderAdvanceOptions.toMinutes(45, ReminderAdvanceOptions.Unit.MINUTES))
        assertEquals(120, ReminderAdvanceOptions.toMinutes(2, ReminderAdvanceOptions.Unit.HOURS))
        assertEquals(2880, ReminderAdvanceOptions.toMinutes(2, ReminderAdvanceOptions.Unit.DAYS))
        assertNull(ReminderAdvanceOptions.toMinutes(8, ReminderAdvanceOptions.Unit.DAYS))
        assertNull(ReminderAdvanceOptions.toMinutes(0, ReminderAdvanceOptions.Unit.HOURS))
        assertNull(ReminderAdvanceOptions.toMinutes(Int.MAX_VALUE, ReminderAdvanceOptions.Unit.DAYS))
    }

    @Test fun pastAndFutureAreDistinguished() {
        val now = 1_800_000_000_000L
        val event = now + 45 * 60_000L
        assertTrue(ReminderAdvanceOptions.isUpcoming(event, 30, now))
        assertFalse(ReminderAdvanceOptions.isUpcoming(event, 60, now))
        assertFalse(ReminderAdvanceOptions.isUpcoming(event, 45, now))
        assertFalse(ReminderAdvanceOptions.isUpcoming(event, 0, now))
    }
    @Test fun oneMinuteAlertWorksForNearTermEvents() {
        val now = 1_800_000_000_000L
        val event = now + 5 * 60_000L
        assertTrue(ReminderAdvanceOptions.isUpcoming(event, 1, now))
        assertFalse(ReminderAdvanceOptions.isUpcoming(event, 5, now))
    }

}

package com.example.mynotes.ui.notes

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class IdleNoteMotionCoordinatorTest {
    @Test
    fun onlyOneCardCanAnimateAtOnce() {
        var now = 1_000L
        val coordinator = IdleNoteMotionCoordinator { now }
        assertTrue(coordinator.tryStart(1))
        assertFalse(coordinator.tryStart(2))
        assertFalse(coordinator.tryStart(1))
        coordinator.finish(1, 12_000L)
        assertFalse(coordinator.tryStart(2))
        now += 12_000L
        assertTrue(coordinator.tryStart(2))
    }

    @Test
    fun wrongCardCannotReleaseTheActiveCard() {
        var now = 1_000L
        val coordinator = IdleNoteMotionCoordinator { now }
        assertTrue(coordinator.tryStart(123))
        coordinator.finish(456, 0L)
        assertFalse(coordinator.tryStart(456))
        coordinator.finish(123, 0L)
        assertTrue(coordinator.tryStart(456))
    }

    @Test
    fun cancellingActiveCardStartsCooldownAndPreventsOverlap() {
        var now = 90_000L
        val coordinator = IdleNoteMotionCoordinator { now }
        assertTrue(coordinator.tryStart(11))
        // Same release path in the LaunchedEffect finally block on scroll/touch.
        coordinator.finish(11, 20_000L)
        now += 19_999L
        assertFalse(coordinator.tryStart(22))
        now += 1L
        assertTrue(coordinator.tryStart(22))
    }
}

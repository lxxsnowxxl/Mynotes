package com.example.mynotes.ui.notes

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class IdleNoteMotionStylesTest {
    @Test fun oldPreferencesDefaultToTheOriginalStyleKey() {
        assertEquals(IdleNoteMotionStyles.ANGULAR, IdleNoteMotionStyles.normalize(null))
        assertEquals(IdleNoteMotionStyles.ANGULAR, IdleNoteMotionStyles.normalize("unknown"))
        assertEquals(IdleNoteMotionStyles.ANGULAR, IdleMotionOptions().styleId)
    }

    @Test fun allPresetsAreUniqueAndRecognized() {
        assertEquals(6, IdleNoteMotionStyles.all.distinct().size)
        assertEquals(5, IdleNoteMotionStyles.concrete.size)
        IdleNoteMotionStyles.all.forEach { assertEquals(it, IdleNoteMotionStyles.normalize(it)) }
    }

    @Test fun sizeAndRotationStaySubtleAtAllLevels() {
        (1..5).forEach { level ->
            assertTrue(IdleNoteMotionStyles.maxScaleDelta(level) in 0f..0.024f)
            assertTrue(IdleNoteMotionStyles.swayAngle(level) in 0f..2f)
        }
        assertEquals(IdleNoteMotionStyles.maxScaleDelta(1), IdleNoteMotionStyles.maxScaleDelta(-500))
        assertEquals(IdleNoteMotionStyles.maxScaleDelta(5), IdleNoteMotionStyles.maxScaleDelta(500))
    }

    @Test fun everyConcreteStyleReturnsTheCardToRest() {
        for (style in IdleNoteMotionStyles.concrete) {
            val frames = IdleNoteMotionFrames.forStyle(style)
            assertEquals(0, frames.first().millis)
            assertTrue(frames.zipWithNext().all { (start, end) -> start.millis < end.millis })
            assertTrue(frames.any { it.slide != 0f || it.lift != 0f })
            for (rest in listOf(frames.first(), frames.last())) {
                assertEquals(0f, rest.slide, 0f)
                assertEquals(0f, rest.lift, 0f)
                assertEquals(0f, rest.turn, 0f)
                assertEquals(0f, rest.pitch, 0f)
                assertEquals(0f, rest.yaw, 0f)
                assertEquals(0f, rest.stretchX, 0f)
                assertEquals(0f, rest.stretchY, 0f)
            }
        }
    }
}

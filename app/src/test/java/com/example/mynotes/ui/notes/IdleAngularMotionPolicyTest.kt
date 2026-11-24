package com.example.mynotes.ui.notes

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class IdleAngularMotionPolicyTest {
    @Test fun intensityChangesAngleWithoutLargeRotations() {
        val angles = (1..5).map(IdleAngularMotionPolicy::maxAngleDegrees)
        assertTrue(angles.zipWithNext().all { (low, high) -> low < high })
        assertTrue(angles.first() > 0f)
        assertTrue(angles.last() <= 2.2f)
    }

    @Test fun intensityAndVariationAreClamped() {
        assertEquals(IdleAngularMotionPolicy.maxAngleDegrees(1),
            IdleAngularMotionPolicy.maxAngleDegrees(-20))
        assertEquals(IdleAngularMotionPolicy.maxAngleDegrees(5),
            IdleAngularMotionPolicy.maxAngleDegrees(42))
        val base = IdleAngularMotionPolicy.maxAngleDegrees(3)
        assertEquals(base * 0.85f, IdleAngularMotionPolicy.amplitudeDegrees(3, 0.0), 0.0001f)
        assertEquals(base * 1.12f, IdleAngularMotionPolicy.amplitudeDegrees(3, 9.0), 0.0001f)
    }
}

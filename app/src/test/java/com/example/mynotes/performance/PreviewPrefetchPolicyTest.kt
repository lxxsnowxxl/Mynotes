package com.example.mynotes.performance

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PreviewPrefetchPolicyTest {
    @Test
    fun visibleNotesArePrioritizedBeforeNearbyNotes() {
        val visible = listOf(10, 12, 11, 10)
        val selected = PreviewPrefetchPolicy.noteIndices(visible, 100)
        assertEquals(listOf(10, 12, 11), selected.take(3))
        assertEquals(selected.size, selected.distinct().size)
        assertTrue(selected.all { it in 6..16 })
        assertTrue(selected.size <= PreviewPrefetchPolicy.MAX_NOTES)
    }

    @Test
    fun emptyAndInvalidViewportNeverPreloadsTheWholeLibrary() {
        assertTrue(PreviewPrefetchPolicy.noteIndices(emptyList(), 1_000_000).isEmpty())
        assertTrue(PreviewPrefetchPolicy.noteIndices(listOf(0), 0).isEmpty())
        assertTrue(PreviewPrefetchPolicy.noteIndices(listOf(-1, 100), 10).isEmpty())
    }

    @Test
    fun largeLibrariesHaveTheSameBoundedPrefetchBudget() {
        val visible = (100_000..100_005).toList()
        val selected = PreviewPrefetchPolicy.noteIndices(visible, 1_000_000)
        assertEquals(visible, selected.take(visible.size))
        assertTrue(selected.size <= PreviewPrefetchPolicy.MAX_NOTES)
        assertTrue(selected.all { it in 99_996..100_009 })
    }

    @Test
    fun viewportAtEitherEdgeStaysInBoundsWithoutIntegerOverflow() {
        for (visible in listOf(listOf(0), listOf(Int.MAX_VALUE - 1))) {
            val selected = PreviewPrefetchPolicy.noteIndices(visible, Int.MAX_VALUE)
            assertEquals(visible.single(), selected.first())
            assertTrue(selected.all { it >= 0 && it < Int.MAX_VALUE })
            assertTrue(selected.size <= PreviewPrefetchPolicy.MAX_NOTES)
        }
        assertEquals(listOf(0), PreviewPrefetchPolicy.noteIndices(listOf(0), 1))
    }
}

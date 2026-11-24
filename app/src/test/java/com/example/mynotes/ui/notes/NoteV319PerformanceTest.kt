package com.example.mynotes.ui.notes

import com.example.mynotes.data.Note
import org.junit.Assert.assertEquals
import org.junit.Test

class NoteV319PerformanceTest {
    @Test fun taskScanIsCappedAtFortyThousandChars() {
        val input = "text".repeat(20_000) + "\n- [x] Should be ignored"
        assertEquals(NoteTaskProgress(0, 0), NoteTaskCounter.count(input))
    }

    @Test fun taskScanHasBoundedResultCount() {
        assertEquals(NoteTaskProgress(250, 250),
            NoteTaskCounter.count(("- [x] Done\n").repeat(300)))
    }

    @Test fun duplicateIndexOnlyMarksMatchingNonEmptyNonDeletedNotes() {
        val input = listOf(Note(id = 1, title = "Nombre", content = "repetido"),
            Note(id = 2, title = " nombre ", content = "repetido"),
            Note(id = 3, title = "", content = ""), Note(id = 4, title = "", content = ""),
            Note(id = 5, title = "nombre", content = "repetido", isDeleted = true))
        assertEquals(setOf(1, 2), NoteLibraryInsights.duplicateIds(input))
    }
}

package com.example.mynotes.ui.notes

import com.example.mynotes.data.Note
import org.junit.Assert.*
import org.junit.Test

class NoteV319FeaturesTest {
    @Test fun duplicateDetectionNormalizesCaseWhitespaceAndUnicode() {
        val notes = listOf(
            Note(id = 1, title = "  TÍTULO ", content = "Texto  igual"),
            Note(id = 2, title = "  título", content = "texto igual"),
            Note(id = 3, title = "Diferente", content = "Algo"),
            Note(id = 4, title = "", content = ""),
            Note(id = 5, title = "", content = "")
        )
        assertEquals(setOf(1, 2), NoteLibraryInsights.duplicateIds(notes))
    }

    @Test fun oldNotesRespectsCutoffAndExcludesDeleted() {
        val day = 86_400_000L
        val now = 55 * day
        val notes = listOf(
            Note(id = 1, title = "old", content = "", createdAt = now - 46 * day),
            Note(id = 2, title = "new", content = "", createdAt = now - 44 * day),
            Note(id = 3, title = "deleted", content = "", createdAt = now - 50 * day, isDeleted = true)
        )
        assertEquals(setOf(1), NoteLibraryInsights.oldNoteIds(notes, now))
    }

    @Test fun checkboxCounterHandlesCompletedAndIncompleteTasks() {
        val result = NoteTaskCounter.count("- [ ] One\n- [x] Two\n* [X] Three\nordinary")
        assertEquals(NoteTaskProgress(2, 3), result)
    }
}

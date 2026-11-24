package com.example.mynotes.performance

import com.example.mynotes.data.Note
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class NoteTextCacheTest {
    private fun note(id: Int, title: String = "Title $id", content: String = "body-$id") =
        Note(id = id, title = title, content = content, createdAt = id.toLong())

    @Test
    fun formattingModeInvalidatesSearchWithoutChangingStoredText() {
        val cache = NoteTextCache()
        val note = Note(1, "", "**Muy** importante")
        assertTrue(cache.update(listOf(note)).getValue(1).contains("**muy**"))
        assertEquals("\nmuy importante", cache.update(listOf(note.copy(formattingEnabled = true)))[1])
    }

    @Test
    fun metadataChangesReuseSearchText() {
        val cache = NoteTextCache()
        val note = note(1)
        val initial = cache.update(listOf(note))
        val updated = cache.update(listOf(note.copy(isFavorite = true, isPinned = true, priority = 3, color = "red")))
        assertEquals(initial, updated)
        assertSame(initial[1], updated[1])
    }

    @Test
    fun titleAndContentEditsInvalidateOnlyTheEditedNote() {
        val cache = NoteTextCache()
        val first = note(1)
        val second = note(2)
        val initial = cache.update(listOf(first, second))
        val titleEdit = cache.update(listOf(first.copy(title = "ÁRBOL İ Σ"), second))
        assertEquals("ÁRBOL İ Σ".lowercase() + "\n" + first.content, titleEdit[1])
        assertSame(initial[2], titleEdit[2])

        val bodyEdit = cache.update(listOf(first.copy(content = "New body"), second))
        assertEquals("title 1\nnew body", bodyEdit[1])
        assertSame(initial[2], bodyEdit[2])
    }

    @Test
    fun removedNotesAreEvictedAndPreviousSnapshotsStayUnchanged() {
        val cache = NoteTextCache()
        val notes = listOf(note(1), note(2))
        val initial = cache.update(notes)
        assertEquals(setOf(2), cache.update(notes.drop(1)).keys)
        assertEquals(setOf(1, 2), initial.keys)
        assertNotSame(initial[1], cache.update(notes)[1])
        assertTrue(cache.update(emptyList()).isEmpty())
    }
}

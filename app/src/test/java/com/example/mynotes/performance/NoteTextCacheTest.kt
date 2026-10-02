package com.example.mynotes.performance

import com.example.mynotes.data.Note
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class NoteTextCacheTest {
    private fun note(id: Int, title: String = "Title $id", content: String = "url-$id") =
        Note(id = id, title = title, content = content, createdAt = id.toLong())

    @Test
    fun metadataChangesReuseSearchTextAndParsedLinks() {
        var parses = 0
        val cache = NoteTextCache { parses++; listOf(it) }
        val note = note(1)
        val initial = cache.update(listOf(note), true)
        val updated = cache.update(listOf(note.copy(isFavorite = true, isPinned = true, priority = 3, color = "red")), true)
        assertEquals(initial, updated)
        assertSame(initial.searchableTextByNote[1], updated.searchableTextByNote[1])
        assertEquals(1, parses)
    }

    @Test
    fun titleChangesRefreshSearchWithoutParsingContentAgain() {
        var parses = 0
        val cache = NoteTextCache { parses++; listOf(it) }
        val note = note(1)
        cache.update(listOf(note), true)
        val updated = cache.update(listOf(note.copy(title = "ÁRBOL İ Σ")), true)
        assertEquals("ÁRBOL İ Σ".lowercase() + "\n" + note.content.lowercase(), updated.searchableTextByNote[1])
        assertEquals(listOf(note.content), updated.linkPreviewUrls)
        assertEquals(1, parses)
    }

    @Test
    fun contentChangesInvalidateOnlyTheEditedNote() {
        var parses = 0
        val cache = NoteTextCache { parses++; listOf(it) }
        val first = note(1)
        val second = note(2)
        val initial = cache.update(listOf(first, second), true)
        val updated = cache.update(listOf(first.copy(content = "NEW LINK"), second), true)
        assertEquals("title 1\nnew link", updated.searchableTextByNote[1])
        assertEquals(listOf("NEW LINK", second.content), updated.linkPreviewUrls)
        assertSame(initial.searchableTextByNote[2], updated.searchableTextByNote[2])
        assertEquals(3, parses)
    }

    @Test
    fun removedNotesAreEvictedAndPreviousSnapshotsStayUnchanged() {
        var parses = 0
        val cache = NoteTextCache { parses++; listOf(it) }
        val notes = listOf(note(1), note(2))
        val initial = cache.update(notes, true)
        val remaining = cache.update(notes.drop(1), true)
        assertEquals(setOf(2), remaining.searchableTextByNote.keys)
        assertEquals(setOf(1, 2), initial.searchableTextByNote.keys)
        cache.update(notes, true)
        assertEquals(3, parses)
    }

    @Test
    fun preloadPreservesOrderingDeduplicationAndBothLimits() {
        val extract: (String) -> List<String> = { it.split('|') }
        val cache = NoteTextCache(extract)
        val notes = (1..60).map { note(it, content = "shared|a-$it|b-$it|ignored-$it") }
        for (ordered in listOf(notes, notes.reversed(), notes.drop(12))) {
            val expected = ordered.asSequence().flatMap { extract(it.content).asSequence().take(3) }
                .distinct().take(80).toList()
            for (search in listOf(false, true)) {
                val result = cache.update(ordered, search)
                assertEquals(expected, result.linkPreviewUrls)
                assertEquals(if (search) ordered.size else 0, result.searchableTextByNote.size)
                assertTrue(result.linkPreviewUrls.none { it.startsWith("ignored-") })
            }
        }
    }

    @Test
    fun clearingSearchDropsNormalizedCopiesAndEmptyListDropsAllEntries() {
        var parses = 0
        val cache = NoteTextCache { parses++; listOf(it) }
        val notes = listOf(note(1))
        val initial = cache.update(notes, true)
        assertTrue(cache.update(notes, false).searchableTextByNote.isEmpty())
        val resumed = cache.update(notes, true)
        assertEquals(initial, resumed)
        assertNotSame(initial.searchableTextByNote[1], resumed.searchableTextByNote[1])
        assertEquals(1, parses)
        assertEquals(NoteTextIndex(emptyMap(), emptyList()), cache.update(emptyList(), false))
        cache.update(notes, false)
        assertEquals(2, parses)
    }
}

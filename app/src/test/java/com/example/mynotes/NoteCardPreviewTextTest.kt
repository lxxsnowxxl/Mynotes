package com.example.mynotes

import com.example.mynotes.ui.components.NoteCardPreviewText
import com.example.mynotes.data.Note
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class NoteCardPreviewTextTest {
    @Test fun shortNotePreserved() {
        val source = "Hola\nNota completa"
        assertSame(source, NoteCardPreviewText.prefix(source))
    }

    @Test fun longNoteBoundedToCardPreview() {
        val source = "x".repeat(10_000)
        assertEquals(NoteCardPreviewText.MAX_CHARS, NoteCardPreviewText.prefix(source).length)
    }

    @Test fun doesNotSplitSurrogatePair() {
        val source = "x".repeat(NoteCardPreviewText.MAX_CHARS - 1) + "😀" + "test"
        val result = NoteCardPreviewText.prefix(source)
        assertEquals(NoteCardPreviewText.MAX_CHARS - 1, result.length)
        assertTrue(result.last() == 'x')
    }

    @Test fun previewCacheReusesMetadataOnlyUpdatesAndRefreshesTextEdits() {
        val cache = NoteCardPreviewText.Cache()
        val first = Note(id = 1, title = "Note", content = "**Important**\n- [x] Done")
        val original = cache.previewFor(first)
        assertSame(original, cache.previewFor(first.copy(isFavorite = true, title = "New title")))
        val formatted = cache.previewFor(first.copy(formattingEnabled = true))
        assertNotSame(original, formatted)
        assertTrue(formatted.text.startsWith("Important"))
        assertEquals(1, formatted.tasks.complete)
        assertEquals("Edited", cache.previewFor(first.copy(content = "Edited")).text)
    }

    @Test fun keepsLinksAfterTheDisplayPrefixAndBoundsTheCache() {
        val cache = NoteCardPreviewText.Cache()
        val first = Note(id = 1, title = "Note", content = "x".repeat(3_300) + "\n[[mynotes-link:https://example.com]]")
        val preview = cache.previewFor(first)
        assertEquals("https://example.com", preview.link)
        assertEquals(NoteCardPreviewText.MAX_CHARS, preview.text.length)
        for (id in 2..100) cache.previewFor(first.copy(id = id, content = "Text $id"))
        assertNotSame(preview, cache.previewFor(first))
    }
}

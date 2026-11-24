package com.example.mynotes.settings

import com.example.mynotes.data.CollectionPolicy
import com.example.mynotes.data.Note
import org.junit.Assert.*
import org.junit.Test

class CollectionPolicyTest {
    @Test fun cropIsBoundedAndInvalidValuesHaveSafeDefaults() {
        assertEquals(0f, CollectionPolicy.crop(-1f))
        assertEquals(1f, CollectionPolicy.crop(2f))
        assertEquals(.5f, CollectionPolicy.crop(Float.NaN))
        assertEquals(.5f, CollectionPolicy.crop(Float.POSITIVE_INFINITY))
    }
    @Test fun namesAreTrimmedAndBoundedWithoutSplittingEmoji() {
        assertEquals("Ideas", CollectionPolicy.name("  Ideas  "))
        assertEquals("Mis notas ", CollectionPolicy.limitName("Mis notas "))
        val result = CollectionPolicy.name("x".repeat(59) + "💡")
        assertTrue(result.length <= 60)
        assertFalse(result.last().isHighSurrogate())
    }
    @Test fun migratedNotesDefaultToUnfiledActivePlainText() {
        val note = Note(title = "Anterior", content = "**literal**")
        assertNull(note.folderId)
        assertNull(note.coverUri)
        assertFalse(note.isArchived)
        assertFalse(note.isDeleted)
        assertFalse(note.formattingEnabled)
    }
    @Test fun collectionFiltersRespectArchiveFolderAndPendingDelete() {
        val note = Note(title = "", content = "", folderId = "f")
        assertTrue(CollectionPolicy.contains(note, null, false))
        assertTrue(CollectionPolicy.contains(note, "f", false))
        assertFalse(CollectionPolicy.contains(note, "g", false))
        assertFalse(CollectionPolicy.contains(note.copy(isArchived = true), null, false))
        assertTrue(CollectionPolicy.contains(note.copy(isArchived = true), null, true))
        assertFalse(CollectionPolicy.contains(note.copy(isDeleted = true), null, false))
    }
    @Test fun unsupportedIconsAndColorsDoNotBecomeInvalidResources() {
        assertNull(CollectionPolicy.icon("unknown"))
        assertEquals("sage", CollectionPolicy.color("unknown"))
    }
}

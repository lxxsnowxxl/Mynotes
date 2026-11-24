package com.example.mynotes.data

import com.example.mynotes.settings.AppSettings
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Test

class OrganizationBackupTest {
    @Test fun newNoteFieldsRoundTripWithoutExportingDeviceSpecificPaths() {
        val note = Note(9, "Plan", "**Importante**", folderId = "folder-1", isArchived = true,
            coverIcon = "book", coverX = .25f, coverY = .8f, formattingEnabled = true, isDeleted = true)
        val json = noteToJson(note)
        assertFalse(json.has("coverUri"))
        assertEquals(note.isDeleted,json.getBoolean("isDeleted"))
        assertEquals(note, jsonToNotes(JSONArray().put(json)).single())
    }
    @Test fun oldBackupsPreserveLiteralPlainText() {
        val old = JSONObject().put("id", 1).put("title", "Anterior").put("content", "**literal**")
        val note = jsonToNotes(JSONArray().put(old)).single()
        assertFalse(note.formattingEnabled)
        assertFalse(note.isArchived)
        assertFalse(note.isDeleted)
        assertNull(note.folderId)
        assertNull(note.coverUri)
    }
    @Test fun foldersRoundTripWithIconsAndColors() {
        val folder = NoteFolder("f-1", "Estudios 📚", "study", "ocean")
        assertEquals(listOf(folder), jsonToFolders(JSONArray().put(folderToJson(folder))))
    }
    @Test fun duplicateFolderIdentifiersAreRejected() {
        val array = JSONArray().put(folderToJson(NoteFolder("f", "Uno"))).put(folderToJson(NoteFolder("f", "Dos")))
        assertThrows(IllegalArgumentException::class.java) { jsonToFolders(array) }
    }
    @Test fun coverUriIsReboundToTheRestoredFile() {
        val note = Note(1, "Portada", "", folderId = "f", coverUri = "file:///old/cover.jpg", coverIcon = "idea", formattingEnabled = true)
        val restored = restoredNotePresentation(listOf(note), listOf(NoteFolder("f", "Ideas")),
            listOf(Attachment(1, 1, "cover", "file:///new/cover.jpg"))).single()
        assertEquals("file:///new/cover.jpg", restored.coverUri)
        assertNull(restored.coverIcon)
        assertEquals("f", restored.folderId)
        assertTrue(restored.formattingEnabled)
    }
    @Test fun unresolvedFoldersAndMissingCoverFilesLeaveUsableNotes() {
        val note = Note(1, "Sigue aquí", "Texto", folderId = "missing", coverUri = "file:///old/path", coverIcon = "book")
        val restored = restoredNotePresentation(listOf(note), emptyList(), emptyList()).single()
        assertNull(restored.folderId)
        assertNull(restored.coverUri)
        assertEquals("book", restored.coverIcon)
        assertEquals("Texto", restored.content)
    }
    @Test fun duplicateCoversAreRejected() {
        assertThrows(IllegalArgumentException::class.java) {
            restoredNotePresentation(listOf(Note(1, "", "")), emptyList(),
                listOf(Attachment(1, 1, "cover", "file:///a"), Attachment(2, 1, "cover", "file:///b")))
        }
    }
    @Test fun nonFiniteReaderAndCropValuesUseSafeDefaults() {
        val settings = jsonToSettings(JSONObject().put("readerWidth", "NaN").put("readerLineSpacing", "Infinity"))
        assertEquals(680f, settings.readerWidth)
        assertEquals(1.6f, settings.readerLineSpacing)
        val note = jsonToNotes(JSONArray().put(JSONObject().put("id", 1).put("coverX", "NaN").put("coverY", 8))).single()
        assertEquals(.5f, note.coverX)
        assertEquals(1f, note.coverY)
    }
    @Test fun readerPreferencesRoundTrip() {
        val settings = AppSettings(readerWidth = 520f, readerLineSpacing = 1.9f)
        assertEquals(settings, jsonToSettings(settingsToJson(settings, null)))
    }
}

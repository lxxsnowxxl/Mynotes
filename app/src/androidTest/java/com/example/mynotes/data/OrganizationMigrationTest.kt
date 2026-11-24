package com.example.mynotes.data

import android.database.sqlite.SQLiteDatabase
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID

/** Real Room validation against an isolated v5 database. Never opens notes_database. */
@RunWith(AndroidJUnit4::class)
class OrganizationMigrationTest {
    private fun migrated(test: suspend (AppDatabase) -> Unit) = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val name = "migration_v312_${UUID.randomUUID()}.db"
        val file = context.getDatabasePath(name)
        file.parentFile?.mkdirs()
        SQLiteDatabase.openOrCreateDatabase(file, null).use { db ->
            db.execSQL("CREATE TABLE notes (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, title TEXT NOT NULL, content TEXT NOT NULL, createdAt INTEGER NOT NULL, color TEXT NOT NULL, priority INTEGER NOT NULL, category TEXT NOT NULL DEFAULT 'personal', isFavorite INTEGER NOT NULL DEFAULT 0, isPinned INTEGER NOT NULL DEFAULT 0)")
            db.execSQL("CREATE TABLE attachments (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, noteId INTEGER NOT NULL, type TEXT NOT NULL, uri TEXT NOT NULL, name TEXT, createdAt INTEGER NOT NULL)")
            db.execSQL("CREATE INDEX index_notes_priority_createdAt ON notes(priority, createdAt)")
            db.execSQL("CREATE INDEX index_notes_isPinned_priority_createdAt ON notes(isPinned, priority, createdAt)")
            db.execSQL("CREATE INDEX index_notes_category ON notes(category)")
            db.execSQL("CREATE INDEX index_attachments_noteId_createdAt ON attachments(noteId, createdAt)")
            db.execSQL("INSERT INTO notes VALUES (1, 'Anterior', '**literal**', 1000, 'blue', 2, 'work', 1, 1)")
            db.execSQL("INSERT INTO attachments VALUES (1, 1, 'image', 'file:///test/keep.jpg', 'keep.jpg', 1000)")
            db.version = 5
        }
        val db = Room.databaseBuilder(context, AppDatabase::class.java, name).addMigrations(AppDatabase.MIGRATION_5_6).build()
        try { test(db) } finally { db.close(); context.deleteDatabase(name) }
    }
    @Test fun roomValidatesTheMigrationAndRetainsExistingData() = migrated { db ->
        val note = db.noteDao().getAllNotesOnce().single()
        assertEquals("**literal**", note.content)
        assertFalse(note.formattingEnabled)
        assertFalse(note.isArchived)
        assertFalse(note.isDeleted)
        assertNull(note.folderId)
        assertEquals(.5f, note.coverX)
        assertEquals("file:///test/keep.jpg", db.attachmentDao().getAttachmentsOnce(1).single().uri)
        db.folderDao().upsert(NoteFolder("f", "Estudios", "study", "ocean"))
        db.noteDao().move(listOf(1), "f")
        assertEquals("f", db.noteDao().getNoteByIdOnce(1)!!.folderId)
    }
    @Test fun stagedDeleteCanBeUndoneAndArchiveLeavesTheNoteInStorage() = migrated { db ->
        db.noteDao().setDeleted(listOf(1), true)
        assertTrue(db.noteDao().getAllNotesOnce().isEmpty())
        assertEquals(1, db.attachmentDao().getAttachmentsOnce(1).size)
        db.noteDao().setDeleted(listOf(1), false)
        assertEquals("Anterior", db.noteDao().getNoteByIdOnce(1)!!.title)
        db.noteDao().setArchived(listOf(1), true)
        assertTrue(db.noteDao().getRecentNotesForWidget(10).isEmpty())
        assertTrue(db.noteDao().getAllNotesOnce().single().isArchived)
    }
}

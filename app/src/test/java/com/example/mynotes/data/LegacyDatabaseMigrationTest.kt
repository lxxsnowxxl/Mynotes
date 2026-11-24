package com.example.mynotes.data

import android.database.sqlite.SQLiteDatabase
import androidx.room.Room
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

/** Opens legacy schemas through Room's real validation, including the v261 schema 5. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk=[28])
class LegacyDatabaseMigrationTest {
    private fun migrate(version:Int) {
        val context=RuntimeEnvironment.getApplication()
        val name="legacy_$version.db"
        val file=context.getDatabasePath(name).apply{parentFile!!.mkdirs()}
        SQLiteDatabase.openOrCreateDatabase(file,null).use{db->
            val extra=if(version>=5)",category TEXT NOT NULL DEFAULT 'personal',isFavorite INTEGER NOT NULL DEFAULT 0,isPinned INTEGER NOT NULL DEFAULT 0" else ""
            val presentation=if(version>=6)",folderId TEXT,isArchived INTEGER NOT NULL DEFAULT 0,isDeleted INTEGER NOT NULL DEFAULT 0,coverUri TEXT,coverIcon TEXT,coverX REAL NOT NULL DEFAULT 0.5,coverY REAL NOT NULL DEFAULT 0.5,formattingEnabled INTEGER NOT NULL DEFAULT 0" else ""
            db.execSQL("CREATE TABLE notes(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,title TEXT NOT NULL,content TEXT NOT NULL,createdAt INTEGER NOT NULL,color TEXT NOT NULL,priority INTEGER NOT NULL$extra$presentation)")
            db.execSQL("CREATE TABLE attachments(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,noteId INTEGER NOT NULL,type TEXT NOT NULL,uri TEXT NOT NULL,name TEXT,createdAt INTEGER NOT NULL)")
            if(version>=4){
                db.execSQL("CREATE INDEX index_notes_priority_createdAt ON notes(priority,createdAt)")
                db.execSQL("CREATE INDEX index_attachments_noteId_createdAt ON attachments(noteId,createdAt)")
            }
            if(version>=5){
                db.execSQL("CREATE INDEX index_notes_isPinned_priority_createdAt ON notes(isPinned,priority,createdAt)")
                db.execSQL("CREATE INDEX index_notes_category ON notes(category)")
            }
            if(version>=6){
                db.execSQL("CREATE TABLE note_folders(id TEXT NOT NULL,name TEXT NOT NULL,icon TEXT NOT NULL,color TEXT NOT NULL,PRIMARY KEY(id))")
                db.execSQL("CREATE INDEX index_notes_folderId ON notes(folderId)")
                db.execSQL("CREATE INDEX index_notes_isDeleted_isArchived ON notes(isDeleted,isArchived)")
                db.execSQL("INSERT INTO note_folders VALUES('work','Work','folder','ocean')")
            }
            db.execSQL("INSERT INTO notes(id,title,content,createdAt,color,priority) VALUES(7,'Original title','Original content',123456,'blue',2)")
            if(version>=5)db.execSQL("UPDATE notes SET category='work',isFavorite=1,isPinned=1 WHERE id=7")
            if(version>=6)db.execSQL("UPDATE notes SET folderId='work',coverUri='file:///cover.jpg',coverX=0.25,formattingEnabled=1 WHERE id=7")
            listOf("image","video","audio","voice","file").forEachIndexed{i,type->db.execSQL("INSERT INTO attachments(noteId,type,uri,name,createdAt) VALUES(?,?,?,?,?)",arrayOf(7,type,"content://original/$i","original_$i",123456L+i))}
            db.version=version
        }
        val db=Room.databaseBuilder(context,AppDatabase::class.java,name).addMigrations(
            AppDatabase.MIGRATION_3_4,AppDatabase.MIGRATION_4_5,AppDatabase.MIGRATION_5_6,AppDatabase.MIGRATION_6_7
        ).allowMainThreadQueries().build()
        try {
            db.openHelper.writableDatabase // Room validates every column and index before DAO access.
            runBlocking{
                val note=requireNotNull(db.noteDao().getNoteByIdOnce(7))
                assertEquals("Original title",note.title);assertEquals("Original content",note.content)
                assertEquals(123456L,note.createdAt);assertEquals("blue",note.color);assertEquals(2,note.priority)
                assertEquals(if(version>=5)"work" else "personal",note.category)
                assertEquals(version>=5,note.isFavorite);assertEquals(version>=5,note.isPinned)
                assertEquals("default",note.libraryId);assertFalse(note.isDeleted);assertFalse(note.isArchived)
                assertEquals(if(version>=6)"work" else null,note.folderId)
                assertEquals(version>=6,note.formattingEnabled)
                val attachments=db.attachmentDao().getAttachmentsOnce(7)
                assertEquals(listOf("image","video","audio","voice","file"),attachments.map{it.type})
                assertEquals((0..4).map{"content://original/$it"},attachments.map{it.uri})
            }
        } finally { db.close() }
    }
    @Test fun schema3ToCurrent()=migrate(3)
    @Test fun schema4ToCurrent()=migrate(4)
    @Test fun originalV261Schema5ToCurrent()=migrate(5)
    @Test fun schema6PresentationToCurrent()=migrate(6)
}

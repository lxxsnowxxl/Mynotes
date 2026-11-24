package com.example.mynotes.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Note::class, Attachment::class, NoteFolder::class], version = 7, exportSchema = false)
abstract class AppDatabase :
    RoomDatabase() {
    abstract fun noteDao():
        NoteDao
    abstract fun attachmentDao():
        AttachmentDao
    abstract fun folderDao(): FolderDao
    companion object {
        @Volatile
        private var INSTANCE:
                AppDatabase? = null
        internal val MIGRATION_3_4 = object :
                Migration(3, 4) {
                override fun migrate(database:
                    SupportSQLiteDatabase) {
                    database.execSQL(
                        """
                        CREATE INDEX IF NOT EXISTS
                        `index_notes_priority_createdAt`
                        ON `notes` (`priority`, `createdAt`)
                        """.trimIndent()
                    )
                    database.execSQL(
                        """
                        CREATE INDEX IF NOT EXISTS
                        `index_attachments_noteId_createdAt`
                        ON `attachments` (`noteId`, `createdAt`)
                        """.trimIndent()
                    )
                }
            }
        internal val MIGRATION_4_5 = object :
                Migration(4, 5) {
                override fun migrate(database:
                    SupportSQLiteDatabase) {
                    database.execSQL(
                        """
                        ALTER TABLE `notes`
                        ADD COLUMN `category`
                        TEXT NOT NULL
                        DEFAULT 'personal'
                        """.trimIndent()
                    )
                    database.execSQL(
                        """
                        ALTER TABLE `notes`
                        ADD COLUMN `isFavorite`
                        INTEGER NOT NULL
                        DEFAULT 0
                        """.trimIndent()
                    )
                    database.execSQL(
                        """
                        ALTER TABLE `notes`
                        ADD COLUMN `isPinned`
                        INTEGER NOT NULL
                        DEFAULT 0
                        """.trimIndent()
                    )
                    database.execSQL(
                        """
                        CREATE INDEX IF NOT EXISTS
                        `index_notes_isPinned_priority_createdAt`
                        ON `notes`
                        (`isPinned`, `priority`, `createdAt`)
                        """.trimIndent()
                    )
                    database.execSQL(
                        """
                        CREATE INDEX IF NOT EXISTS
                        `index_notes_category`
                        ON `notes` (`category`)
                        """.trimIndent()
                    )
                }
            }
        internal val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE notes ADD COLUMN folderId TEXT")
                database.execSQL("ALTER TABLE notes ADD COLUMN isArchived INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE notes ADD COLUMN isDeleted INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE notes ADD COLUMN coverUri TEXT")
                database.execSQL("ALTER TABLE notes ADD COLUMN coverIcon TEXT")
                database.execSQL("ALTER TABLE notes ADD COLUMN coverX REAL NOT NULL DEFAULT 0.5")
                database.execSQL("ALTER TABLE notes ADD COLUMN coverY REAL NOT NULL DEFAULT 0.5")
                database.execSQL("ALTER TABLE notes ADD COLUMN formattingEnabled INTEGER NOT NULL DEFAULT 0")
                database.execSQL("CREATE TABLE IF NOT EXISTS note_folders (id TEXT NOT NULL, name TEXT NOT NULL, icon TEXT NOT NULL, color TEXT NOT NULL, PRIMARY KEY(id))")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_notes_folderId ON notes(folderId)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_notes_isDeleted_isArchived ON notes(isDeleted, isArchived)")
            }
        }

        internal val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE notes ADD COLUMN libraryId TEXT NOT NULL DEFAULT 'default'")
                database.execSQL("ALTER TABLE note_folders ADD COLUMN libraryId TEXT NOT NULL DEFAULT 'default'")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_notes_libraryId ON notes(libraryId)")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            com.example.mynotes.studio.PlusProfiles.initialize(context)
            return INSTANCE?: synchronized(this) {
                    val instance = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "notes_database")
                            .addMigrations(MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7).build()
                    INSTANCE = instance
                    instance
                }
        }
    }
}

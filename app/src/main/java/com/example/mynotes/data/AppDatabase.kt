package com.example.mynotes.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Note::class, Attachment::class], version = 5, exportSchema = false)
abstract class AppDatabase :
    RoomDatabase() {
    abstract fun noteDao():
        NoteDao
    abstract fun attachmentDao():
        AttachmentDao
    companion object {
        @Volatile
        private var INSTANCE:
                AppDatabase? = null
        /*
         * -----------------------------------------------------
         * 3 -> 4
         * -----------------------------------------------------
         *
         * Esta es la migración de la fase de rendimiento anterior.
         * Se incluye también aquí para que una instalación que aún
         * esté en versión 3 pueda llegar hasta versión 5 sin borrar
         * ninguna nota.
         */
        private val MIGRATION_3_4 = object :
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
        /*
         * -----------------------------------------------------
         * 4 -> 5
         * -----------------------------------------------------
         *
         * Añade exclusivamente las propiedades necesarias para
         * el diseño nuevo:
         *
         * - category
         * - isFavorite
         * - isPinned
         *
         * No se elimina ni recrea ninguna tabla.
         */
        private val MIGRATION_4_5 = object :
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
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE?: synchronized(this) {
                    val instance = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "notes_database")
                            /*
                             * Importante:
                             * no usamos fallback destructivo.
                             */
                            .addMigrations(MIGRATION_3_4, MIGRATION_4_5).build()
                    INSTANCE = instance
                    instance
                }
        }
    }
}

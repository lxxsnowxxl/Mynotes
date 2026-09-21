package com.example.mynotes.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


data class WidgetNoteStats(
    val totalCount: Int,
    val favoriteCount: Int,
    val pinnedCount: Int,
    val highPriorityCount: Int,
    val workCount: Int,
    val personalCount: Int
)

@Dao
interface NoteDao {
    /*
     * Room devuelve la lista ya ordenada.
     *
     * 1. Fijadas.
     * 2. Prioridad alta -> baja.
     * 3. Más recientes.
     *
     * De esta forma Compose no ordena la colección en cada cambio.
     */
    @Query(
        """
        SELECT *
        FROM notes
        ORDER BY
            isPinned DESC,
            priority DESC,
            createdAt DESC
        """
    )
    fun getAllNotes():
        Flow<List<Note>>
    @Query(
        """
        SELECT *
        FROM notes
        ORDER BY id ASC
        """
    )
    suspend fun getAllNotesOnce():
        List<Note>

    @Query("SELECT * FROM notes WHERE id = :noteId LIMIT 1")
    suspend fun getNoteByIdOnce(noteId: Int): Note?

    @Query(
        """
        SELECT *
        FROM notes
        ORDER BY createdAt DESC
        LIMIT :limit
        """
    )
    suspend fun getRecentNotesForWidget(limit: Int): List<Note>

    @Query("SELECT COUNT(*) FROM notes")
    suspend fun getNoteCountForWidget(): Int

    @Query("SELECT COUNT(*) FROM notes WHERE isFavorite = 1")
    suspend fun getFavoriteCountForWidget(): Int

    @Query(
        """
        SELECT
            COUNT(*) AS totalCount,
            COALESCE(SUM(CASE WHEN isFavorite = 1 THEN 1 ELSE 0 END), 0) AS favoriteCount,
            COALESCE(SUM(CASE WHEN isPinned = 1 THEN 1 ELSE 0 END), 0) AS pinnedCount,
            COALESCE(SUM(CASE WHEN priority = 3 THEN 1 ELSE 0 END), 0) AS highPriorityCount,
            COALESCE(SUM(CASE WHEN category = 'work' THEN 1 ELSE 0 END), 0) AS workCount,
            COALESCE(SUM(CASE WHEN category != 'work' THEN 1 ELSE 0 END), 0) AS personalCount
        FROM notes
        """
    )
    suspend fun getWidgetStats(): WidgetNoteStats

    @Query(
        """
        SELECT *
        FROM notes
        WHERE isFavorite = 1
        ORDER BY isPinned DESC, priority DESC, createdAt DESC
        LIMIT :limit
        """
    )
    suspend fun getFavoriteNotesForWidget(limit: Int): List<Note>

    @Query(
        """
        SELECT *
        FROM notes
        WHERE isPinned = 1
        ORDER BY priority DESC, createdAt DESC
        LIMIT :limit
        """
    )
    suspend fun getPinnedNotesForWidget(limit: Int): List<Note>

    @Query(
        """
        SELECT *
        FROM notes
        ORDER BY isPinned DESC, priority DESC, isFavorite DESC, createdAt DESC
        LIMIT 1
        """
    )
    suspend fun getFocusNoteForWidget(): Note?
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotes(notes: List<Note>)
    @Query("DELETE FROM notes")
    suspend fun deleteAllNotes()
    @Insert
    suspend fun insertNote(note: Note): Long
    @Update
    suspend fun updateNote(note: Note)
    /*
     * Actualizaciones parciales: evitan escribir toda la fila cuando solo
     * cambia un atributo visual o de organización.
     */
    @Query("UPDATE notes SET color = :color WHERE id = :noteId")
    suspend fun updateColor(noteId: Int, color: String)
    @Query("UPDATE notes SET priority = :priority WHERE id = :noteId")
    suspend fun updatePriority(noteId: Int, priority: Int)
    @Query("UPDATE notes SET isFavorite = :isFavorite WHERE id = :noteId")
    suspend fun updateFavorite(noteId: Int, isFavorite: Boolean)
    @Query("UPDATE notes SET isPinned = :isPinned WHERE id = :noteId")
    suspend fun updatePinned(noteId: Int, isPinned: Boolean)
    @Query("UPDATE notes SET category = :category WHERE id = :noteId")
    suspend fun updateCategory(noteId: Int, category: String)
    @Delete
    suspend fun deleteNote(note: Note)
}

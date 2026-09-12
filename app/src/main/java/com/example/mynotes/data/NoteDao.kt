package com.example.mynotes.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

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

package com.example.mynotes.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
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
    @Query("""
        UPDATE notes SET title = :title, content = :content, color = :color
        WHERE id = :noteId AND (title != :title OR content != :content OR color != :color)
    """)
    suspend fun updateEditedFields(noteId: Int, title: String, content: String, color: String): Int
    @Query("UPDATE notes SET color = :color WHERE id = :noteId AND color != :color")
    suspend fun updateColor(noteId: Int, color: String): Int
    @Query("UPDATE notes SET priority = :priority WHERE id = :noteId AND priority != :priority")
    suspend fun updatePriority(noteId: Int, priority: Int): Int
    @Query("UPDATE notes SET isFavorite = :isFavorite WHERE id = :noteId AND isFavorite != :isFavorite")
    suspend fun updateFavorite(noteId: Int, isFavorite: Boolean): Int
    @Query("UPDATE notes SET isFavorite = CASE WHEN isFavorite = 1 THEN 0 ELSE 1 END WHERE id = :noteId")
    suspend fun toggleFavorite(noteId: Int): Int
    @Query("UPDATE notes SET isPinned = :isPinned WHERE id = :noteId AND isPinned != :isPinned")
    suspend fun updatePinned(noteId: Int, isPinned: Boolean): Int
    @Query("UPDATE notes SET isPinned = CASE WHEN isPinned = 1 THEN 0 ELSE 1 END WHERE id = :noteId")
    suspend fun togglePinned(noteId: Int): Int
    @Query("UPDATE notes SET category = :category WHERE id = :noteId AND category != :category")
    suspend fun updateCategory(noteId: Int, category: String): Int
    @Delete
    suspend fun deleteNote(note: Note)
}

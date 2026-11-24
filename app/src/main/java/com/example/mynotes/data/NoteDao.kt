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
        WHERE isDeleted = 0
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
        WHERE isDeleted = 0
        ORDER BY id ASC
        """
    )
    suspend fun getAllNotesOnce():
        List<Note>

    @Query("SELECT * FROM notes WHERE isDeleted = 1 ORDER BY createdAt DESC")
    fun observeTrash(): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE id = :noteId AND isDeleted = 0 LIMIT 1")
    suspend fun getNoteByIdOnce(noteId: Int): Note?

    @Query(
        """
        SELECT *
        FROM notes
        WHERE libraryId = :library AND isDeleted = 0 AND isArchived = 0
        ORDER BY createdAt DESC
        LIMIT :limit
        """
    )
    suspend fun getRecentNotesForWidget(limit: Int, library:String=com.example.mynotes.studio.PlusProfiles.currentId): List<Note>

    @Query("SELECT COUNT(*) FROM notes WHERE libraryId = :library AND isDeleted = 0 AND isArchived = 0")
    suspend fun getNoteCountForWidget(library:String=com.example.mynotes.studio.PlusProfiles.currentId): Int

    @Query("SELECT COUNT(*) FROM notes WHERE libraryId = :library AND isFavorite = 1 AND isDeleted = 0 AND isArchived = 0")
    suspend fun getFavoriteCountForWidget(library:String=com.example.mynotes.studio.PlusProfiles.currentId): Int

    @Query(
        """
        SELECT *
        FROM notes
        WHERE libraryId = :library AND isFavorite = 1 AND isDeleted = 0 AND isArchived = 0
        ORDER BY isPinned DESC, priority DESC, createdAt DESC
        LIMIT :limit
        """
    )
    suspend fun getFavoriteNotesForWidget(limit: Int, library:String=com.example.mynotes.studio.PlusProfiles.currentId): List<Note>

    @Query(
        """
        SELECT *
        FROM notes
        WHERE libraryId = :library AND isDeleted = 0 AND isArchived = 0
        ORDER BY isPinned DESC, priority DESC, isFavorite DESC, createdAt DESC
        LIMIT 1
        """
    )
    suspend fun getFocusNoteForWidget(library:String=com.example.mynotes.studio.PlusProfiles.currentId): Note?
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
    @Query("SELECT * FROM notes WHERE id IN (:ids) AND isDeleted = 0")
    suspend fun byIds(ids: List<Int>): List<Note>
    @Query("SELECT * FROM notes WHERE isDeleted = 1")
    suspend fun deletedNotes(): List<Note>
    @Query("UPDATE notes SET isDeleted = :deleted WHERE id IN (:ids)")
    suspend fun setDeleted(ids: List<Int>, deleted: Boolean): Int
    @Query("UPDATE notes SET isArchived = :archived WHERE id IN (:ids) AND isDeleted = 0")
    suspend fun setArchived(ids: List<Int>, archived: Boolean): Int
    @Query("UPDATE notes SET folderId = :folderId WHERE id IN (:ids) AND isDeleted = 0")
    suspend fun move(ids: List<Int>, folderId: String?): Int
    @Query("UPDATE notes SET folderId = NULL WHERE folderId = :folderId")
    suspend fun detachFolder(folderId: String)
    @Query("UPDATE notes SET content = :content WHERE id = :noteId AND isDeleted = 0 AND content != :content")
    suspend fun updateContent(noteId: Int, content: String): Int
    @Query("UPDATE notes SET folderId = :folderId, coverIcon = :coverIcon, coverX = :coverX, coverY = :coverY, formattingEnabled = :formatted WHERE id = :noteId AND isDeleted = 0 AND (folderId IS NOT :folderId OR coverIcon IS NOT :coverIcon OR coverX != :coverX OR coverY != :coverY OR formattingEnabled != :formatted)")
    suspend fun updatePresentation(noteId: Int, folderId: String?, coverIcon: String?, coverX: Float, coverY: Float, formatted: Boolean): Int
    @Query("UPDATE notes SET coverUri = :uri WHERE id = :noteId AND coverUri IS NOT :uri")
    suspend fun updateCover(noteId: Int, uri: String?): Int
    @Delete
    suspend fun deleteNote(note: Note)
}

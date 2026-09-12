package com.example.mynotes.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AttachmentDao {
    /*
     * NotesScreen observa todos los adjuntos una sola vez.
     * Después los agrupa por noteId en memoria.
     */
    @Query(
        """
        SELECT *
        FROM attachments
        ORDER BY createdAt DESC
        """
    )
    fun getAllAttachments():
        Flow<List<Attachment>>
    @Query(
        """
        SELECT *
        FROM attachments
        ORDER BY id ASC
        """
    )
    suspend fun getAllAttachmentsOnce():
        List<Attachment>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttachmentsForRestore(attachments: List<Attachment>)
    @Query("DELETE FROM attachments")
    suspend fun deleteAllAttachments()
    @Query(
        """
        SELECT *
        FROM attachments
        WHERE noteId = :noteId
        ORDER BY createdAt ASC
        """
    )
    fun getAttachments(noteId: Int): Flow<List<Attachment>>
    @Query(
        """
        SELECT *
        FROM attachments
        WHERE noteId = :noteId
        ORDER BY createdAt ASC
        """
    )
    suspend fun getAttachmentsOnce(noteId: Int): List<Attachment>
    @Insert
    suspend fun insertAttachment(attachment: Attachment)
    @Insert
    suspend fun insertAttachments(attachments: List<Attachment>)
    @Delete
    suspend fun deleteAttachment(attachment: Attachment)
    @Query(
        """
        DELETE FROM attachments
        WHERE noteId = :noteId
        """
    )
    suspend fun deleteAttachmentsForNote(noteId: Int)
}

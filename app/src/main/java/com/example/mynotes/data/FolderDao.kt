package com.example.mynotes.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FolderDao {
    @Query("SELECT * FROM note_folders ORDER BY name COLLATE NOCASE, id")
    fun folders(): Flow<List<NoteFolder>>
    @Query("SELECT * FROM note_folders ORDER BY name COLLATE NOCASE, id")
    suspend fun allOnce(): List<NoteFolder>
    @Query("SELECT * FROM note_folders WHERE id = :id LIMIT 1")
    suspend fun byId(id: String): NoteFolder?
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(folder: NoteFolder)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun restore(folders: List<NoteFolder>)
    @Query("DELETE FROM note_folders WHERE id = :id")
    suspend fun delete(id: String)
    @Query("DELETE FROM note_folders")
    suspend fun deleteAll()
}

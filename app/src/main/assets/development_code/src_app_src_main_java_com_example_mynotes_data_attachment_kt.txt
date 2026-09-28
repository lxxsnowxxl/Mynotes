package com.example.mynotes.data

import androidx.compose.runtime.Immutable
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Immutable
@Entity(tableName = "attachments", indices = [Index(value = ["noteId", "createdAt"])])
data class Attachment(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val noteId: Int,
    /*
     * image
     * video
     * audio
     * voice
     * file
     */
    val type: String,
    val uri: String,
    val name: String? = null,
    val createdAt: Long = System.currentTimeMillis())

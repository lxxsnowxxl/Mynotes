package com.example.mynotes.data

import androidx.compose.runtime.Immutable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Immutable
@Entity(tableName = "notes", indices = [
        Index(value = ["priority", "createdAt"]),
        Index(value = ["isPinned", "priority", "createdAt"]),
        Index(value = ["category"]),
        Index(value = ["folderId"]),
        Index(value = ["isDeleted", "isArchived"]),
        Index(value = ["libraryId"])])
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val content: String,
    val createdAt: Long = System.currentTimeMillis(),
    val color: String = "default",
    val priority: Int = 0,
    val category: String = "personal",
    val isFavorite: Boolean = false,
    val isPinned: Boolean = false,
    val folderId: String? = null,
    @ColumnInfo(defaultValue = "0") val isArchived: Boolean = false,
    @ColumnInfo(defaultValue = "0") val isDeleted: Boolean = false,
    val coverUri: String? = null,
    val coverIcon: String? = null,
    @ColumnInfo(defaultValue = "0.5") val coverX: Float = 0.5f,
    @ColumnInfo(defaultValue = "0.5") val coverY: Float = 0.5f,
    @ColumnInfo(defaultValue = "0") val formattingEnabled: Boolean = false,
    @ColumnInfo(defaultValue = "'default'") val libraryId: String = com.example.mynotes.studio.PlusProfiles.currentId)

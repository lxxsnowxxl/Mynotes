package com.example.mynotes.data

import androidx.compose.runtime.Immutable
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Immutable
@Entity(tableName = "notes", indices = [
        Index(value = ["priority", "createdAt"]),
        Index(value = ["isPinned", "priority", "createdAt"]),
        Index(value = ["category"])])
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
    val isPinned: Boolean = false)

package com.example.mynotes.data

import androidx.compose.runtime.Immutable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Immutable
@Entity(tableName = "note_folders")
data class NoteFolder(@PrimaryKey val id: String, val name: String, val icon: String = "folder", val color: String = "sage", @androidx.room.ColumnInfo(defaultValue = "'default'") val libraryId: String = com.example.mynotes.studio.PlusProfiles.currentId)

object CollectionPolicy {
    val icons = listOf("folder", "work", "idea", "study", "shopping", "book", "heart", "code")
    val colors = listOf("sage", "ocean", "lavender", "rose", "cream", "warm")
    fun contains(note: Note, folderId: String?, archived: Boolean): Boolean =
        !note.isDeleted && note.isArchived == archived && (folderId == null || note.folderId == folderId)
    fun icon(value: String?) = value?.takeIf { it in icons }
    fun color(value: String) = value.takeIf { it in colors } ?: "sage"
    fun limitName(value: String) = value.take(60).let { if (it.lastOrNull()?.isHighSurrogate() == true) it.dropLast(1) else it }
    fun name(value: String) = limitName(value.trim())
    fun crop(value: Float) = if (value.isFinite()) value.coerceIn(0f, 1f) else 0.5f
}

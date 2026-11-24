package com.example.mynotes.ui.state

import com.example.mynotes.data.Note
import com.example.mynotes.data.NoteFolder

enum class NoteOperation { DELETE, ARCHIVE, PIN, COLOR, MOVE, FOLDER_DELETE }
data class NoteUndo(val token: Long, val operation: NoteOperation, val before: List<Note>, val folder: NoteFolder? = null)

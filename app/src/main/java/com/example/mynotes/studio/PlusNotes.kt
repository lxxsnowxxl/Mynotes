package com.example.mynotes.studio

import android.content.Context
import android.net.Uri
import androidx.room.withTransaction
import com.example.mynotes.data.*
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.withLock
import java.io.File

internal object PlusNotes {
    suspend fun create(context: Context, title: String, content: String, files: List<Pair<File, String>> = emptyList()): Int = withContext(Dispatchers.IO) {
        StudioMutations.lock.withLock {
            val db = AppDatabase.getDatabase(context)
            var id = 0
            db.withTransaction {
                id = db.noteDao().insertNote(Note(title = title, content = content, formattingEnabled = true)).toInt()
                db.attachmentDao().insertAttachments(files.map { (file, kind) -> Attachment(noteId = id, type = kind, uri = Uri.fromFile(file).toString(), name = file.name) })
            }
            runCatching {
                StudioStore.get(context).changed(id, "created")
                com.example.mynotes.widget.MyNotesWidgetUpdater.requestUpdate(context)
            }.onFailure { StudioJobs.failure("Note saved; metadata update failed",it) }
            id
        }
    }
}

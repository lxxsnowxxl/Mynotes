package com.example.mynotes.ui.notes

import com.example.mynotes.data.Note
import java.text.Normalizer
import java.util.Locale

/** Pure calculations; never changes note contents or deletes suspected duplicates. */
internal object NoteLibraryInsights {
    private const val MINUTE = 60_000L
    private val whitespace = Regex("\\s+")
    const val STALE_AFTER_DAYS = 45L

    fun oldNoteIds(notes: List<Note>, now: Long): Set<Int> {
        val cutoff = now - STALE_AFTER_DAYS * 24L * 60L * MINUTE
        return notes.asSequence().filter { !it.isDeleted && it.createdAt < cutoff }.map { it.id }.toSet()
    }

    fun duplicateIds(notes: List<Note>): Set<Int> {
        // A single pass avoids the group sorting and intermediate lists needed by exactGroups.
        val firstByContent = HashMap<Pair<String, String>, Int>()
        val repeated = HashSet<Int>()
        notes.forEach { note ->
            if (note.isDeleted) return@forEach
            val title = normalized(note.title)
            val body = normalized(note.content)
            if (title.isBlank() && body.isBlank()) return@forEach
            val originalId = firstByContent.putIfAbsent(title to body, note.id)
            if (originalId != null) {
                repeated.add(originalId)
                repeated.add(note.id)
            }
        }
        return repeated
    }

    fun exactGroups(notes: List<Note>): List<List<Note>> = notes.asSequence()
        .filterNot { it.isDeleted }
        .groupBy { normalized(it.title) to normalized(it.content) }
        .filter { (key, items) -> items.size > 1 && (key.first.isNotBlank() || key.second.isNotBlank()) }
        .values.map { it.sortedBy { note -> note.createdAt } }
        .sortedByDescending { it.size }

    private fun normalized(text: String): String = Normalizer.normalize(text, Normalizer.Form.NFKC)
        .lowercase(Locale.ROOT).replace(whitespace, " ").trim()
}

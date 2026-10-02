package com.example.mynotes.performance

import com.example.mynotes.data.Note

internal data class NoteTextIndex(
    val searchableTextByNote: Map<Int, String>,
    val linkPreviewUrls: List<String>
)

/** Caché local al listado: conserva sólo las notas visitadas y nunca imágenes ni adjuntos. */
internal class NoteTextCache(private val extractUrls: (String) -> List<String>) {
    private class Entry(var title: String, var content: String) {
        var searchableText: String? = null
        var urls: List<String>? = null
    }

    private var entries = emptyMap<Int, Entry>()

    fun update(notes: List<Note>, includeSearchText: Boolean): NoteTextIndex {
        val retained = HashMap<Int, Entry>()
        val searchTexts = if (includeSearchText) LinkedHashMap<Int, String>(notes.size) else null
        val previewUrls = LinkedHashSet<String>()
        for (note in notes) {
            if (!includeSearchText && previewUrls.size == 80) break
            val entry = entries[note.id] ?: Entry(note.title, note.content)
            if (entry.content != note.content) {
                entry.urls = null
                entry.searchableText = null
            } else if (entry.title != note.title) {
                entry.searchableText = null
            }
            // Soltar las referencias antiguas aunque Room entregue cadenas nuevas con el mismo valor.
            entry.title = note.title
            entry.content = note.content
            if (searchTexts != null) {
                val normalized = entry.searchableText ?: buildString(note.title.length + note.content.length + 1) {
                    append(note.title.lowercase())
                    append('\n')
                    append(note.content.lowercase())
                }.also { entry.searchableText = it }
                searchTexts[note.id] = normalized
            } else {
                entry.searchableText = null
            }
            if (previewUrls.size < 80) {
                val urls = entry.urls ?: extractUrls(note.content).take(3).also { entry.urls = it }
                for (url in urls) {
                    previewUrls.add(url)
                    if (previewUrls.size == 80) break
                }
            }
            retained[note.id] = entry
        }
        entries = retained
        return NoteTextIndex(searchTexts ?: emptyMap(), previewUrls.toList())
    }
}

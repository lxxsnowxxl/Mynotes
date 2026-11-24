package com.example.mynotes.performance

import com.example.mynotes.data.Note
import com.example.mynotes.ui.notes.NoteMarkup

/** Local a la búsqueda. Los enlaces se precargan usando las notas cercanas al viewport. */
internal class NoteTextCache {
    private data class Entry(val title: String, val content: String, val formatted: Boolean, val text: String)

    private var entries = emptyMap<Int, Entry>()

    @Synchronized
    fun clear() {
        entries = emptyMap()
    }

    @Synchronized
    fun update(notes: List<Note>): Map<Int, String> {
        val retained = HashMap<Int, Entry>(notes.size)
        val searchTexts = LinkedHashMap<Int, String>(notes.size)
        for (note in notes) {
            val cached = entries[note.id]
            val text = if (cached != null && cached.title == note.title && cached.content == note.content &&
                cached.formatted == note.formattingEnabled) {
                cached.text
            } else {
                val content = if (note.formattingEnabled) NoteMarkup.render(note.content).text else note.content
                buildString(note.title.length + content.length + 1) {
                    append(note.title.lowercase())
                    append('\n')
                    append(content.lowercase())
                }
            }
            retained[note.id] = Entry(note.title, note.content, note.formattingEnabled, text)
            searchTexts[note.id] = text
        }
        entries = retained
        return searchTexts
    }
}

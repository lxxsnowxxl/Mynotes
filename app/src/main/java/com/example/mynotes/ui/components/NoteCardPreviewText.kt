package com.example.mynotes.ui.components

import androidx.compose.runtime.compositionLocalOf
import com.example.mynotes.data.Note
import com.example.mynotes.ui.notes.NoteMarkup
import com.example.mynotes.ui.notes.NoteTaskCounter
import com.example.mynotes.ui.notes.NoteTaskProgress
import java.text.DateFormat
import java.util.Date
import java.util.Locale

internal val LocalNoteCardPreviewCache = compositionLocalOf<NoteCardPreviewText.Cache?> { null }

/**
 * La tarjeta sólo muestra unas líneas: no se debe analizar ni medir como
 * Text el contenido completo de una nota extensa durante un fling.
 * El editor/exportación siguen usando el texto íntegro.
 */
internal object NoteCardPreviewText {
    const val MAX_CHARS = 3_200
    private const val MAX_CACHED_NOTES = 96

    data class Preview(val text: String, val link: String?, val tasks: NoteTaskProgress)

    /** Local a la pantalla. Reutiliza el texto al volver a una tarjeta que salió del viewport. */
    class Cache {
        private data class CachedPreview(val source: String, val formatted: Boolean, val preview: Preview)

        private val entries = object : LinkedHashMap<Int, CachedPreview>(16, 0.75f, true) {
            override fun removeEldestEntry(eldest: MutableMap.MutableEntry<Int, CachedPreview>): Boolean =
                size > MAX_CACHED_NOTES
        }

        fun previewFor(note: Note): Preview {
            val cached = entries[note.id]
            if (cached != null && cached.formatted == note.formattingEnabled && cached.source == note.content) {
                if (cached.source !== note.content) entries[note.id] = cached.copy(source = note.content)
                return cached.preview
            }
            return build(note.content, note.formattingEnabled).also { preview ->
                entries[note.id] = CachedPreview(note.content, note.formattingEnabled, preview)
            }
        }
    }

    fun firstLink(content: String): String? =
        noteLinkUrls(content).map(::trimLinkDecoration).firstOrNull { it.length > 8 }

    private fun trimLinkDecoration(url: String): String = url.trimEnd('*', '_', '=', '~')

    fun build(content: String, formatted: Boolean): Preview {
        // Buscar también en los metadatos de enlace al final de una nota larga.
        val links = extractLinkUrls(content).map(::trimLinkDecoration)
            .filter { it.length > 8 }.distinct()
        val visible = noteTextForDisplay(prefix(content), links)
        return Preview(
            text = if (formatted) NoteMarkup.render(visible).text else visible,
            link = links.firstOrNull(),
            tasks = NoteTaskCounter.count(content)
        )
    }

    fun prefix(content: String): String {
        if (content.length <= MAX_CHARS) return content
        val end = if (content[MAX_CHARS - 1].isHighSurrogate() &&
            content[MAX_CHARS].isLowSurrogate()) MAX_CHARS - 1 else MAX_CHARS
        return content.substring(0, end)
    }
}

internal val LocalNoteCardDateFormatter = compositionLocalOf<DateFormat?> { null }

internal fun formatNoteCardDate(timestamp: Long, formatter: DateFormat?, locale: Locale): String =
    (formatter ?: DateFormat.getDateInstance(DateFormat.MEDIUM, locale)).format(Date(timestamp))

package com.example.mynotes.ui.notes

internal data class NoteTaskProgress(val complete: Int, val total: Int)

/** Bounded, allocation-light Markdown checkbox counter for the visible note cards. */
internal object NoteTaskCounter {
    fun count(text: String): NoteTaskProgress {
        val limit = text.length.coerceAtMost(40_000)
        var position = 0
        var total = 0
        var complete = 0
        while (position < limit && total < 250) {
            var lineEnd = position
            while (lineEnd < limit && text[lineEnd] != '\n') lineEnd++
            var p = position
            while (p < lineEnd && (text[p] == ' ' || text[p] == '\t')) p++
            if (p < lineEnd && (text[p] == '-' || text[p] == '*')) {
                p++
                if (p < lineEnd && (text[p] == ' ' || text[p] == '\t')) {
                    while (p < lineEnd && (text[p] == ' ' || text[p] == '\t')) p++
                    if (p + 2 < lineEnd && text[p] == '[' && text[p + 2] == ']' &&
                        (text[p + 1] == ' ' || text[p + 1] == 'x' || text[p + 1] == 'X') &&
                        (p + 3 == lineEnd || text[p + 3] == ' ' || text[p + 3] == '\t')) {
                        total++
                        if (text[p + 1] != ' ') complete++
                    }
                }
            }
            position = lineEnd + 1
        }
        return NoteTaskProgress(complete, total)
    }
}

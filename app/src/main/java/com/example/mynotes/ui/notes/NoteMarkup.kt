package com.example.mynotes.ui.notes

/** Small, bounded markup grammar: existing plain-text notes remain plain text. */
object NoteMarkup {
    enum class Style { BOLD, HIGHLIGHT, HEADING }
    enum class Action { BOLD, HIGHLIGHT, HEADING, BULLET, TASK }
    data class Span(val start: Int, val end: Int, val style: Style)
    data class Rendered(val text: String, val spans: List<Span>, val originalToDisplay: IntArray, val displayToOriginal: IntArray)
    data class Edit(val text: String, val selectionStart: Int, val selectionEnd: Int)
    sealed class Block {
        data class Text(val source: String) : Block()
        data class Task(val line: Int, val checked: Boolean, val source: String) : Block()
    }
    private val task = Regex("^[-*] \\[([ xX])](?: (.*))?$")
    private val heading = Regex("^#{1,3} +")
    private val prefix = Regex("^(?:#{1,3} +|[-*] (?:\\[[ xX]](?: |$))?)")

    fun render(source: String): Rendered {
        val output = StringBuilder(source.length)
        val spans = mutableListOf<Span>()
        val forward = IntArray(source.length + 1)
        val reverse = mutableListOf(0)
        var position = 0
        fun hide(count: Int, opening: Boolean) {
            repeat(count) { position++; forward[position] = output.length }
            if (opening) reverse[reverse.lastIndex] = position
        }
        fun emit() {
            output.append(source[position++]); forward[position] = output.length; reverse.add(position)
        }
        fun replacement(count: Int, text: String) {
            val start = position
            repeat(count) { index -> forward[start + index] = output.length + index * text.length / count }
            text.forEachIndexed { index, char -> output.append(char); reverse.add(start + (index + 1) * count / text.length) }
            position += count; forward[position] = output.length
        }
        fun escaped(index: Int): Boolean {
            var slashes = 0; var i = index - 1
            while (i >= 0 && source[i] == '\\') { slashes++; i-- }
            return slashes % 2 == 1
        }
        fun closing(token: String, from: Int, end: Int): Int {
            var index = from
            while (index + token.length <= end) {
                if (source.startsWith(token, index) && !escaped(index)) return index
                index++
            }
            return -1
        }
        fun parseInline(end: Int) {
            while (position < end) {
                if (source[position] == '\\' && position + 1 < end && source[position + 1] in "*#=\\") {
                    hide(1, true); emit(); continue
                }
                val token = when {
                    source.startsWith("**", position) -> "**"
                    source.startsWith("==", position) -> "=="
                    else -> null
                }
                val close = token?.let { closing(it, position + 2, end) } ?: -1
                if (token != null && close >= 0) {
                    hide(2, true)
                    val start = output.length
                    parseInline(close)
                    if (output.length > start) spans.add(Span(start, output.length, if (token == "**") Style.BOLD else Style.HIGHLIGHT))
                    hide(2, false)
                } else emit()
            }
        }
        while (position < source.length) {
            val lineEnd = source.indexOf('\n', position).let { if (it < 0) source.length else it }
            val line = source.substring(position, lineEnd)
            val title = heading.find(line)?.value
            if (title != null) hide(title.length, true)
            else if (task.matches(line)) replacement(if (line.length > 5 && line[5] == ' ') 6 else 5,
                if (task.matchEntire(line)!!.groupValues[1].isBlank()) "☐ " else "☑ ")
            else if (line.startsWith("- ") || line.startsWith("* ")) replacement(2, "• ")
            val start = output.length
            parseInline(lineEnd)
            if (title != null && output.length > start) spans.add(Span(start, output.length, Style.HEADING))
            if (position < source.length) emit()
        }
        return Rendered(output.toString(), spans, forward, reverse.toIntArray())
    }

    /**
     * Toolbar taps normally collapse the text selection. For inline actions, format the
     * current word so Bold / Highlight have a visible result immediately. Keep the
     * existing insertion-marker behavior when the caret is on an empty line.
     * Line actions always work on the current line through [format].
     */
    fun formatForToolbar(source: String, selectionStart: Int, selectionEnd: Int, action: Action): Edit {
        if (selectionStart != selectionEnd || action !in setOf(Action.BOLD, Action.HIGHLIGHT)) {
            return format(source, selectionStart, selectionEnd, action)
        }
        val caret = selectionStart.coerceIn(0, source.length)
        fun wordAt(index: Int) = index in source.indices && (source[index].isLetterOrDigit() || source[index] == '_')
        val position = when {
            wordAt(caret) -> caret
            wordAt(caret - 1) -> caret - 1
            else -> return format(source, caret, caret, action)
        }
        var from = position
        var to = position + 1
        while (from > 0 && wordAt(from - 1)) from--
        while (to < source.length && wordAt(to)) to++
        return format(source, from, to, action)
    }

    fun format(source: String, selectionStart: Int, selectionEnd: Int, action: Action): Edit {
        val rawStart = minOf(selectionStart, selectionEnd).coerceIn(0, source.length)
        val rawEnd = maxOf(selectionStart, selectionEnd).coerceIn(rawStart, source.length)
        fun splitsPair(index: Int) = index > 0 && index < source.length && source[index - 1].isHighSurrogate() && source[index].isLowSurrogate()
        val start = if (splitsPair(rawStart)) rawStart - 1 else rawStart
        val end = if (splitsPair(rawEnd)) rawEnd + 1 else rawEnd
        if (action == Action.BOLD || action == Action.HIGHLIGHT) {
            val token = if (action == Action.BOLD) "**" else "=="
            if (start >= 2 && end + 2 <= source.length && source.substring(start - 2, start) == token && source.substring(end, end + 2) == token) {
                return Edit(source.removeRange(end, end + 2).removeRange(start - 2, start), start - 2, end - 2)
            }
            if (end - start >= 4 && source.substring(start, start + 2) == token && source.substring(end - 2, end) == token) {
                return Edit(source.removeRange(end - 2, end).removeRange(start, start + 2), start, end - 4)
            }
            return Edit(source.substring(0, start) + token + source.substring(start, end) + token + source.substring(end), start + 2, end + 2)
        }
        val first = source.lastIndexOf('\n', (start - 1).coerceAtLeast(-1)) + 1
        val anchor = if (end > start && source.getOrNull(end - 1) == '\n') end - 1 else end
        val last = source.indexOf('\n', anchor).let { if (it < 0) source.length else it }
        val lines = source.substring(first, last).split('\n')
        val marker = when (action) { Action.HEADING -> "## "; Action.TASK -> "- [ ] "; else -> "- " }
        fun enabled(line: String) = when (action) {
            Action.HEADING -> heading.containsMatchIn(line)
            Action.TASK -> task.matches(line) || line.startsWith("- [ ] ") || line.startsWith("- [x] ")
            else -> (line.startsWith("- ") || line.startsWith("* ")) && !task.matches(line)
        }
        val remove = lines.all(::enabled)
        val changed = lines.joinToString("\n") { line ->
            val bare = line.replaceFirst(prefix, "")
            if (remove) bare else marker + bare
        }
        val text = source.replaceRange(first, last, changed)
        return if (start == end && lines.size == 1) {
            val cursor = (start + changed.length - (last - first)).coerceIn(first, first + changed.length)
            Edit(text, cursor, cursor)
        } else Edit(text, first, first + changed.length)
    }

    private val linkMetadata = Regex("(?im)^[\\t ]*\\[\\[mynotes-link:(https?://[^\\]\\r\\n]+)]][\\t ]*$")
    /** Remove metadata text without collapsing preceding lines used by task callbacks. */
    fun visibleSource(source: String): String = source.replace(linkMetadata, "").trimEnd()
    fun shareText(source: String, formatted: Boolean): String {
        val visible = visibleSource(source)
        val body = if (formatted) render(visible).text else visible
        val links = linkMetadata.findAll(source).map { it.groupValues[1] }.distinct().filterNot { body.contains(it) }.toList()
        return (listOf(body).filter { it.isNotBlank() } + links).joinToString("\n\n")
    }
    fun editorSource(source: String): String = visibleSource(source).replace(
        Regex("(?m)^([-*] \\[[ xX]])$"), "$1 ")

    fun toggleTask(source: String, line: Int): String {
        val lines = source.split('\n').toMutableList()
        val original = lines.getOrNull(line) ?: return source
        val match = task.matchEntire(original) ?: return source
        lines[line] = original.replaceRange(3, 4, if (match.groupValues[1].isBlank()) "x" else " ")
        return lines.joinToString("\n")
    }
    fun blocks(source: String): List<Block> = buildList {
        val paragraph = mutableListOf<String>()
        fun flush() { if (paragraph.isNotEmpty()) { add(Block.Text(paragraph.joinToString("\n"))); paragraph.clear() } }
        source.split('\n').forEachIndexed { index, line ->
            val match = task.matchEntire(line)
            if (match == null) paragraph.add(line)
            else { flush(); add(Block.Task(index, match.groupValues[1].isNotBlank(), match.groupValues[2])) }
        }
        flush()
    }
}

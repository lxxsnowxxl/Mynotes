package com.example.mynotes.ui.notes

import org.junit.Assert.*
import org.junit.Test
import kotlin.random.Random

class NoteMarkupTest {
    @Test fun plainTextAndUnicodeRemainIntact() {
        val source = "Una idea 💡\nTexto con acentos: canción, árbol."
        val result = NoteMarkup.render(source)
        assertEquals(source, result.text)
        assertTrue(result.spans.isEmpty())
        assertEquals((0..source.length).toList(), result.originalToDisplay.toList())
    }
    @Test fun headingsBoldAndHighlightsRenderWithoutMarkers() {
        val result = NoteMarkup.render("## Ideas\n**Importante** y ==pendiente==")
        assertEquals("Ideas\nImportante y pendiente", result.text)
        assertEquals(setOf(NoteMarkup.Style.HEADING, NoteMarkup.Style.BOLD, NoteMarkup.Style.HIGHLIGHT), result.spans.map { it.style }.toSet())
    }
    @Test fun nestedStylesAreSupported() {
        val result = NoteMarkup.render("**Muy ==importante==**")
        assertEquals("Muy importante", result.text)
        assertEquals(2, result.spans.size)
    }
    @Test fun unmatchedAndEscapedMarkersRemainLiteral() {
        assertEquals("**incompleto", NoteMarkup.render("**incompleto").text)
        assertEquals("**literal**", NoteMarkup.render("\\*\\*literal\\*\\*").text)
    }
    @Test fun toggleBoldRoundTripPreservesSurroundingText() {
        val source = "Hoy escribo ideas"
        val edit = NoteMarkup.format(source, 4, 11, NoteMarkup.Action.BOLD)
        assertEquals("Hoy **escribo** ideas", edit.text)
        assertEquals(source, NoteMarkup.format(edit.text, edit.selectionStart, edit.selectionEnd, NoteMarkup.Action.BOLD).text)
    }
    @Test fun collapsedFormattingLeavesCursorInsideTheStyle() {
        val edit = NoteMarkup.format("", 0, 0, NoteMarkup.Action.BOLD)
        assertEquals("****", edit.text)
        assertEquals(2, edit.selectionStart)
        assertEquals(2, edit.selectionEnd)
        assertEquals(0, NoteMarkup.render(edit.text).originalToDisplay[2])
    }
    @Test fun boldToolbarFormatsCurrentWordWithoutSelection() {
        val source = "Hoy escribo ideas"
        val edit = NoteMarkup.formatForToolbar(source, 7, 7, NoteMarkup.Action.BOLD)
        assertEquals("Hoy **escribo** ideas", edit.text)
        assertEquals("Hoy escribo ideas", NoteMarkup.render(edit.text).text)
        assertTrue(NoteMarkup.render(edit.text).spans.any { it.style == NoteMarkup.Style.BOLD })
    }
    @Test fun highlightToolbarFormatsWordAtEndOfLine() {
        val edit = NoteMarkup.formatForToolbar("hola mundo", 10, 10, NoteMarkup.Action.HIGHLIGHT)
        assertEquals("hola ==mundo==", edit.text)
        assertTrue(NoteMarkup.render(edit.text).spans.any { it.style == NoteMarkup.Style.HIGHLIGHT })
    }
    @Test fun toolbarPreservesLineFormattingBehavior() {
        assertEquals("## Título", NoteMarkup.formatForToolbar("Título", 3, 3, NoteMarkup.Action.HEADING).text)
        assertEquals("- Pan", NoteMarkup.formatForToolbar("Pan", 1, 1, NoteMarkup.Action.BULLET).text)
        assertEquals("- [ ] Pan", NoteMarkup.formatForToolbar("Pan", 1, 1, NoteMarkup.Action.TASK).text)
    }
    @Test fun toolbarRetainsInsertionModeOnEmptyBody() {
        assertEquals("****", NoteMarkup.formatForToolbar("", 0, 0, NoteMarkup.Action.BOLD).text)
    }
    @Test fun formattingDoesNotSplitEmojiAtAnInvalidCursorBoundary() {
        assertEquals("A**💡**B", NoteMarkup.format("A💡B", 2, 2, NoteMarkup.Action.BOLD).text)
    }
    @Test fun reverseSelectionsAreNormalized() {
        assertEquals(NoteMarkup.format("texto", 0, 5, NoteMarkup.Action.HIGHLIGHT), NoteMarkup.format("texto", 5, 0, NoteMarkup.Action.HIGHLIGHT))
    }
    @Test fun multilineListsToggleAsOneOperation() {
        val source = "Leche\nPan\nFruta"
        val first = NoteMarkup.format(source, 0, source.length, NoteMarkup.Action.BULLET)
        assertEquals("- Leche\n- Pan\n- Fruta", first.text)
        assertEquals(source, NoteMarkup.format(first.text, first.selectionStart, first.selectionEnd, NoteMarkup.Action.BULLET).text)
    }
    @Test fun lineFormattingUsesOnlySelectedLines() {
        val edit = NoteMarkup.format("Primera\nSegunda\nTercera", 8, 15, NoteMarkup.Action.HEADING)
        assertEquals("Primera\n## Segunda\nTercera", edit.text)
    }
    @Test fun changingListTypeDoesNotStackPrefixes() {
        assertEquals("- [ ] tarea", NoteMarkup.format("- tarea", 0, 7, NoteMarkup.Action.TASK).text)
    }
    @Test fun togglingTaskChangesOnlyTheRequestedLine() {
        val source = "## Compras\n- [ ] Leche\n- [x] Pan\nhttps://example.org"
        val result = NoteMarkup.toggleTask(source, 1)
        assertEquals("## Compras\n- [x] Leche\n- [x] Pan\nhttps://example.org", result)
        assertEquals(source, NoteMarkup.toggleTask(result, 1))
        assertEquals(source, NoteMarkup.toggleTask(source, 0))
    }
    @Test fun emptySavedTasksStillRenderAndCanBeEdited() {
        assertEquals("☐ ", NoteMarkup.render("- [ ]").text)
        assertEquals("- [x]", NoteMarkup.toggleTask("- [ ]", 0))
        assertEquals("- [ ] ", NoteMarkup.editorSource("- [ ]"))
        assertEquals("", NoteMarkup.format("- [ ]", 0, 5, NoteMarkup.Action.TASK).text)
    }
    @Test fun embeddedLinksAndBlankLinesDoNotShiftTaskIndices() {
        val source = "Texto\n\n\n\n- [ ] Tarea\n\n[[mynotes-link:https://example.org]]"
        val visible = NoteMarkup.visibleSource(source)
        val task = NoteMarkup.blocks(visible).filterIsInstance<NoteMarkup.Block.Task>().single()
        assertEquals(4, task.line)
        assertTrue(NoteMarkup.toggleTask(source, task.line).contains("- [x] Tarea"))
        assertFalse(visible.contains("mynotes-link"))
    }
    @Test fun sharingKeepsProcessedLinksAndStripsOnlyEnabledFormatting() {
        val source = "**Ideas**\n\n[[mynotes-link:https://example.org]]"
        assertEquals("Ideas\n\nhttps://example.org", NoteMarkup.shareText(source, true))
        assertEquals("**Ideas**\n\nhttps://example.org", NoteMarkup.shareText(source, false))
        assertEquals("https://example.org", NoteMarkup.shareText("[[mynotes-link:https://example.org]]", false))
    }
    @Test fun blocksPreserveTaskLineNumbersAndPlainParagraphs() {
        val blocks = NoteMarkup.blocks("Texto\n\n- [ ] A\n- [x] B\nFinal")
        assertEquals(4, blocks.size)
        assertEquals(2, (blocks[1] as NoteMarkup.Block.Task).line)
        assertTrue((blocks[2] as NoteMarkup.Block.Task).checked)
        assertEquals("Texto\n", (blocks[0] as NoteMarkup.Block.Text).source)
    }
    @Test fun mappingsAreMonotonicAndWithinBothTextBounds() {
        val random = Random(312)
        val tokens = listOf("**", "==", "## ", "- [ ] ", "- [x] ", "- ", "idea", "á", "💡", "\n", "\\*", " ")
        repeat(1200) {
            val source = buildString { repeat(random.nextInt(1, 40)) { append(tokens.random(random)) } }
            val rendered = NoteMarkup.render(source)
            assertEquals(source.length + 1, rendered.originalToDisplay.size)
            assertEquals(rendered.text.length + 1, rendered.displayToOriginal.size)
            assertTrue(rendered.originalToDisplay.all { it in 0..rendered.text.length })
            assertTrue(rendered.displayToOriginal.all { it in 0..source.length })
            assertTrue(rendered.originalToDisplay.zipWithNextCompat().all { (a, b) -> a <= b })
            assertTrue(rendered.displayToOriginal.zipWithNextCompat().all { (a, b) -> a <= b })
        }
    }
    private fun IntArray.zipWithNextCompat() = (0 until lastIndex).map { this[it] to this[it + 1] }
}

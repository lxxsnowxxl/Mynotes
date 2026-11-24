package com.example.mynotes.studio

import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Test
import java.util.Calendar
import java.util.TimeZone

class PlusAlgorithmsTest {
    @Test fun replacementIsLiteralAndPreservesDollarSigns() {
        assertEquals(PlusAlgorithms.Replacement("$1 $1", 2), PlusAlgorithms.replace("a+b a+b", "a+b", "$1", false, true))
    }
    @Test fun replaceOneStartsAtTheCursorAndDoesNotTouchThePrefix() {
        assertEquals(PlusAlgorithms.Replacement("uno DOS uno", 1), PlusAlgorithms.replace("uno uno uno", "UNO", "DOS", true, false, 4))
    }
    @Test fun emptySearchNeverLoopsOrChangesTheText() { assertEquals(PlusAlgorithms.Replacement("abc", 0), PlusAlgorithms.replace("abc", "", "X", false, true)) }
    @Test fun tagsTrimDeduplicateAndBoundInput() { assertEquals(listOf("Ideas", "trabajo", "日本語"), PlusAlgorithms.tags(" #Ideas,ideas\ntrabajo, 日本語, ")) }
    @Test fun staleOrderRetainsAllCurrentItemsOnce() { assertEquals(listOf("b", "a", "c"), PlusAlgorithms.ordered(listOf("a", "b", "c"), listOf("gone", "b", "b"))) }
    @Test fun boundedReorderingKeepsTheSameElements() { assertEquals(listOf(3, 1, 2), PlusAlgorithms.move(listOf(1, 2, 3), 2, -20)); assertEquals(listOf(1), PlusAlgorithms.move(listOf(1), -1, 2)) }
    @Test fun templateValuesAreInsertedLiterallyAndUnknownFieldsSurvive() {
        assertEquals(listOf("name", "date"), PlusAlgorithms.templateFields("{{name}} / {{date}} / {{name}}"))
        assertEquals("$1 / {{date}}", PlusAlgorithms.fillTemplate("{{name}} / {{date}}", mapOf("name" to "$1")))
    }
    @Test fun completionRejectsAnIncompleteOpenerAndClosedLinks() {
        assertNull(PlusAlgorithms.completion("[[", 1)); assertNull(PlusAlgorithms.completion("[[done]]", 8)); assertNull(PlusAlgorithms.completion("[[one\ntwo", 9))
        assertEquals(PlusAlgorithms.LinkCompletion(5, 10, "pla"), PlusAlgorithms.completion("See: [[pla", 10))
    }
    @Test fun paragraphAnchorsDoNotSilentlyAttachToChangedText() {
        val source = "Primer párrafo\n\nSegundo 🌿"
        val parts = PlusAlgorithms.paragraphs(source)
        assertEquals(2, parts.size); assertTrue(PlusAlgorithms.anchorMatches(source, parts[1].start, parts[1].text))
        assertFalse(PlusAlgorithms.anchorMatches("new " + source, parts[1].start, parts[1].text))
    }
    private fun words() = JSONArray().apply { repeat(25) { put(JSONObject().put("start", it * .5).put("end", it * .5 + .4).put("word", "word$it")) } }
    @Test fun transcriptCorrectionPreservesCueTimesAndAllCorrectedWords() {
        val original = words(); val before = original.toString()
        val cues = PlusAlgorithms.cues(original, "Esta es una corrección completa con signos y acentos.")
        assertEquals(before, original.toString()); assertEquals(0.0, cues.first().start, .0001); assertEquals(12.4, cues.last().end, .0001)
        assertEquals("Esta es una corrección completa con signos y acentos.", cues.joinToString(" ") { it.text })
    }
    @Test fun subtitlesUseTheirOwnSeparatorAndEscapeCueArrows() {
        val cues = listOf(PlusAlgorithms.Cue(3661.125, 3662.5, "a --> b"))
        assertTrue(PlusAlgorithms.subtitles(cues, false).contains("01:01:01,125 --> 01:01:02,500"))
        assertTrue(PlusAlgorithms.subtitles(cues, true).startsWith("WEBVTT\n\n1\n01:01:01.125"))
        assertTrue(PlusAlgorithms.subtitles(cues, true).contains("a → b"))
    }
    @Test fun filterCombinesTypesAndBothDateBounds() {
        val filter = PlusSearchFilter(setOf("PDF", "note"), 100, 200)
        assertTrue(filter.accepts("PDF", 100)); assertFalse(filter.accepts("audio", 150)); assertFalse(filter.accepts("note", 201))
    }
    @Test fun scopedWorkspaceKeysKeepLegacyAndOtherLibrariesSeparate() {
        val state = StudioState(mapOf("tasks" to mapOf("a" to "{\"id\":\"a\",\"title\":\"Personal\"}"), "library:work:tasks" to mapOf("a" to "{\"id\":\"a\",\"title\":\"Work\"}"), "profiles" to mapOf("work" to "{\"id\":\"work\"}")), library = "work")
        assertEquals("Work", state.get("tasks", "a").getString("title")); assertEquals("Personal", state.copy(library = "default").get("tasks", "a").getString("title"))
        assertEquals(1, state.all("profiles").size); assertEquals(0, state.copy(library = "school").all("tasks").size)
    }
}

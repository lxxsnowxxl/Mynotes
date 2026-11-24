package com.example.mynotes.ui.pdf
import org.junit.Assert.*
import org.junit.Test
class PdfPageRangeTest {
    @Test fun emptyRangeMeansAllPagesInDocumentOrder() { assertEquals(listOf(0, 1, 2), PdfPageRange.parse("", 3)) }
    @Test fun combinesIntervalsWhitespaceAndSinglePages() { assertEquals(listOf(0, 1, 2, 4), PdfPageRange.parse(" 1 - 3, 5 ", 7)) }
    @Test fun removesDuplicatesAndPreservesDocumentOrder() { assertEquals(listOf(0, 1, 2), PdfPageRange.parse("3, 1-2, 2", 5)) }
    @Test fun rejectsIncompleteReversedAndOutOfBoundsRanges() { listOf("0", "6", "4-2", "1-", "1,,3", "1;3", "999999999999999", "1-6").forEach { assertNull(it, PdfPageRange.parse(it, 5)) } }
}

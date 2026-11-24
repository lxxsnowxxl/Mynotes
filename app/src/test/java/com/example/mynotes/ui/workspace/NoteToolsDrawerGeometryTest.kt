package com.example.mynotes.ui.workspace

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NoteToolsDrawerGeometryTest {
    @Test fun phoneShowsAlmostFullWidthAndLeavesTabWhenClosed() {
        val width = NoteToolsDrawerGeometry.drawerWidthDp(320f)
        assertEquals(312f, width, 0.001f)
        val closed = NoteToolsDrawerGeometry.closedOffsetDp(width)
        assertEquals(48f, width + closed, 0.001f)
        assertTrue(closed < 0f)
    }

    @Test fun tabDoesNotIntrudeIntoEditorTextColumn() {
        assertEquals(48f, NoteToolsDrawerGeometry.tabWidthDp, 0.001f)
        assertEquals(64f, NoteToolsDrawerGeometry.tabHeightDp, 0.001f)
        assertTrue(NoteToolsDrawerGeometry.contentStartInsetDp > NoteToolsDrawerGeometry.tabWidthDp)
    }

    @Test fun tabletDrawerHasSafeMaximumWidth() {
        val width = NoteToolsDrawerGeometry.drawerWidthDp(1024f)
        assertEquals(480f, width, 0.001f)
        assertEquals(-432f, NoteToolsDrawerGeometry.closedOffsetDp(width), 0.001f)
    }
}

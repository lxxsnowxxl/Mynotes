package com.example.mynotes.ui.notes

/**
 * One active idle NoteCard at a time, plus a quiet period between animations.
 * Owned by NotesScreen, so the preview in Settings remains independent.
 * Uses a monotonic clock to avoid issues when system time changes.
 */
internal class IdleNoteMotionCoordinator(
    private val uptimeMillis: () -> Long = { System.nanoTime() / 1_000_000L }
) {
    private var activeId: Int? = null
    private var nextAllowedMillis = 0L

    @Synchronized
    fun tryStart(noteId: Int): Boolean {
        if (activeId != null || uptimeMillis() < nextAllowedMillis) return false
        activeId = noteId
        return true
    }

    @Synchronized
    fun finish(noteId: Int, cooldownMillis: Long) {
        if (activeId != noteId) return
        activeId = null
        nextAllowedMillis = uptimeMillis() + cooldownMillis.coerceAtLeast(0L)
    }
}

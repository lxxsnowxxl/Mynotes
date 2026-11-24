package com.example.mynotes.ui.sound

/** Comparte una reproducción entre los callbacks síncronos de una pulsación. */
internal object UiClickSoundPolicy {
    const val COLLAPSE_WINDOW_MS = 70L

    private class Click(var played: Boolean = false)
    private val activeClick = ThreadLocal<Click>()

    fun runClick(action: () -> Unit, fallback: (() -> Unit)? = null) {
        if (activeClick.get() != null) {
            action()
            return
        }
        val click = Click()
        activeClick.set(click)
        try {
            action()
            if (!click.played) fallback?.invoke()
        } finally {
            activeClick.remove()
        }
    }

    fun recordPlayback() {
        activeClick.get()?.played = true
    }

    fun hasPlayedInClick(): Boolean = activeClick.get()?.played == true

    fun shouldPlay(nowMs: Long, lastPlayedAtMs: Long): Boolean =
        nowMs >= lastPlayedAtMs && nowMs - lastPlayedAtMs >= COLLAPSE_WINDOW_MS

    fun shouldPlayFallback(nowMs: Long, lastPlayedAtMs: Long): Boolean =
        activeClick.get()?.let { !it.played } ?: shouldPlay(nowMs, lastPlayedAtMs)

    fun shouldPlayTextInput(nowMs: Long, lastInputAtMs: Long, intervalMs: Long): Boolean =
        nowMs >= lastInputAtMs && nowMs - lastInputAtMs >= intervalMs

    fun isDuplicateSound(
        nowMs: Long, lastMs: Long, soundId: Int, lastId: Int,
        rate: Float, lastRate: Float, intervalMs: Long
    ): Boolean {
        activeClick.get()?.let { return it.played }
        return nowMs >= lastMs && nowMs - lastMs < intervalMs &&
            soundId == lastId && rate == lastRate
    }
}

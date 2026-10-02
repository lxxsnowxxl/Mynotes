package com.example.mynotes

import android.content.Context
import android.media.AudioManager

/** Keeps temporary keyboard-system-sound suppression outside MainActivity. */
internal class SystemKeyboardSoundController(context: Context) {
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private var enabled = false
    private var resumed = false
    private var mutedByApp = false
    private var wasMutedBeforeIme = false

    var imeVisible: Boolean = false
        private set

    fun setEnabled(value: Boolean) {
        enabled = value
        sync()
    }

    fun onImeVisibilityChanged(visible: Boolean): Boolean {
        val keyboardClosed = imeVisible && !visible
        imeVisible = visible
        sync()
        return keyboardClosed
    }

    fun onResume() {
        resumed = true
        sync()
    }

    fun onPause() {
        resumed = false
        restore()
    }

    fun restore() {
        if (!mutedByApp) return
        try {
            if (!wasMutedBeforeIme && !audioManager.isVolumeFixed) {
                audioManager.adjustStreamVolume(AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_UNMUTE, 0)
            }
        } catch (_: SecurityException) {
        } catch (_: RuntimeException) {
        } finally {
            mutedByApp = false
            wasMutedBeforeIme = false
        }
    }

    private fun sync() {
        val shouldMute = resumed && enabled && imeVisible
        if (!shouldMute) {
            restore()
            return
        }
        if (mutedByApp) return

        try {
            if (audioManager.isVolumeFixed) return
            wasMutedBeforeIme = audioManager.isStreamMute(AudioManager.STREAM_SYSTEM)
            if (!wasMutedBeforeIme) {
                audioManager.adjustStreamVolume(AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_MUTE, 0)
            }
            mutedByApp = true
        } catch (_: SecurityException) {
            mutedByApp = false
        } catch (_: RuntimeException) {
            mutedByApp = false
        }
    }
}

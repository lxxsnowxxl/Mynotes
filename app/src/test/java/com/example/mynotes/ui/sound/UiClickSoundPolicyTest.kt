package com.example.mynotes.ui.sound

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class UiClickSoundPolicyTest {
    @Test fun blocksDoubleSoundsFromSameTap() {
        assertFalse(UiClickSoundPolicy.shouldPlay(1_030, 1_000))
        assertFalse(UiClickSoundPolicy.shouldPlay(1_069, 1_000))
        assertTrue(UiClickSoundPolicy.shouldPlay(1_070, 1_000))
    }

    @Test fun throttlesRapidTypingAndDuplicateWavRequests() {
        assertFalse(UiClickSoundPolicy.shouldPlayTextInput(1_045, 1_000, 95))
        assertTrue(UiClickSoundPolicy.shouldPlayTextInput(1_095, 1_000, 95))
        assertTrue(UiClickSoundPolicy.isDuplicateSound(1_030, 1_000, 7, 7, 1.0f, 1.0f, 52))
        assertFalse(UiClickSoundPolicy.isDuplicateSound(1_030, 1_000, 7, 8, 1.0f, 1.0f, 52))
        assertFalse(UiClickSoundPolicy.isDuplicateSound(1_030, 1_000, 7, 7, 1.2f, 1.0f, 52))
    }

    @Test fun allowsDistinctTapsAndGuardsClockReset() {
        assertTrue(UiClickSoundPolicy.shouldPlay(10_000, 9_800))
        assertFalse(UiClickSoundPolicy.shouldPlay(10, 20))
    }

    @Test fun sharesPlaybackAcrossNestedCallbacksEvenWithDifferentSounds() {
        var callbacks = 0
        val fallback: () -> Unit = { error("The click already played a sound") }

        UiClickSoundPolicy.runClick(action = {
            UiClickSoundPolicy.recordPlayback()
            UiClickSoundPolicy.runClick(action = {
                callbacks++
                assertTrue(UiClickSoundPolicy.isDuplicateSound(1_500, 1_000, 8, 7, 1.2f, 1f, 52))
                assertFalse(UiClickSoundPolicy.shouldPlayFallback(1_500, 1_000))
            }, fallback = fallback)
        }, fallback = fallback)

        assertEquals(1, callbacks)
    }

    @Test fun eachClickCanPlayOnceEvenWhenTwoTapsUseTheSameSound() {
        var clicks = 0
        val fallback: () -> Unit = {
            assertTrue(UiClickSoundPolicy.shouldPlayFallback(1_020, 1_000))
            assertFalse(UiClickSoundPolicy.isDuplicateSound(1_020, 1_000, 7, 7, 1f, 1f, 52))
            UiClickSoundPolicy.recordPlayback()
            clicks++
        }

        repeat(2) {
            UiClickSoundPolicy.runClick(action = {
                UiClickSoundPolicy.runClick(action = {}, fallback = fallback)
            }, fallback = fallback)
        }

        assertEquals(2, clicks)
    }

    @Test fun clearsClickStateWhenAButtonCallbackFails() {
        assertThrows(IllegalStateException::class.java) {
            UiClickSoundPolicy.runClick(action = {
                UiClickSoundPolicy.recordPlayback()
                error("Callback failed")
            })
        }

        assertFalse(UiClickSoundPolicy.hasPlayedInClick())
        assertTrue(UiClickSoundPolicy.shouldPlayFallback(1_500, 1_000))
    }
}

package com.example.mynotes.ui.pdf

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.mynotes.settings.AppSettings
import java.util.concurrent.atomic.AtomicInteger
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PdfScreenTransitionTest {
    @get:Rule val compose = createComposeRule()

    @Test
    fun exitCallbackWaitsForTheRenderedAnimationAndRunsOnce() {
        val visibility = MutableTransitionState(true)
        val closing = mutableStateOf(false)
        val completed = AtomicInteger(0)
        compose.mainClock.autoAdvance = false
        compose.setContent {
            MaterialTheme {
                PdfScreenTransition(
                    visibility = visibility,
                    motionSettings = AppSettings(animationSpeed = 0.5f, performanceMode = "quality"),
                    closing = closing.value,
                    onExitFinished = { completed.incrementAndGet() }
                ) { Text("PDF content") }
            }
        }
        compose.mainClock.advanceTimeByFrame()
        compose.runOnIdle {
            closing.value = true
            visibility.targetState = false
        }
        compose.mainClock.advanceTimeByFrame()
        compose.mainClock.advanceTimeBy(80)
        assertEquals(0, completed.get())
        compose.mainClock.advanceTimeBy(800)
        compose.waitForIdle()
        assertEquals(1, completed.get())
        compose.mainClock.advanceTimeBy(800)
        compose.waitForIdle()
        assertEquals(1, completed.get())
    }
}

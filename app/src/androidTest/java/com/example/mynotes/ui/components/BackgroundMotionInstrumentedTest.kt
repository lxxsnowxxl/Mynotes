package com.example.mynotes.ui.components

import android.os.SystemClock
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.FloatState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import java.util.concurrent.atomic.AtomicReference
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** Comprueba avance y redibujado reales sin gestos: necesita un dispositivo/emulador. */
@RunWith(AndroidJUnit4::class)
class BackgroundMotionInstrumentedTest {
    @get:Rule val compose = createComposeRule()

    private class Owner : LifecycleOwner {
        override val lifecycle = LifecycleRegistry.createUnsafe(this).apply { currentState = Lifecycle.State.RESUMED }
    }

    @Test
    @SdkSuppress(minSdkVersion = 26)
    fun idleBackgroundAdvancesAndRepaintsWithoutTouchInput() {
        val phase = AtomicReference<FloatState?>()
        val owner = Owner()
        compose.setContent {
            CompositionLocalProvider(LocalLifecycleOwner provides owner) {
                BackgroundMotionProvider("animated_blur", true, "balanced", 1f) {
                    val value = rememberBackgroundMotionPhase(true)
                    SideEffect { phase.set(value) }
                    Box(Modifier.size(260.dp, 400.dp).testTag("moving_background")
                        .settingsBackground("animated_blur", SettingsBackgroundColors(
                            Color(0xFFEFEAFF), Color(0xFFBDADE9), Color(0xFFC5C0F2)), value))
                }
            }
        }
        compose.waitUntil(4_000) { (phase.get()?.floatValue ?: 0f) > 0f }
        val first = compose.onNodeWithTag("moving_background").captureToImage().toPixelMap()
        val start = requireNotNull(phase.get()).floatValue
        compose.waitUntil(5_000) { requireNotNull(phase.get()).floatValue >= start + 0.15f }
        val next = compose.onNodeWithTag("moving_background").captureToImage().toPixelMap()
        var changed = 0
        for (y in 0 until first.height step 8) for (x in 0 until first.width step 8) {
            if (first[x, y] != next[x, y]) changed++
        }
        assertTrue("El fondo debe repintarse sin deslizar ni tocar la pantalla", changed > 20)
    }

    @Test
    fun clockPausesAndResumesWithAnimationsLifecycleAndConsumers() {
        val phase = AtomicReference<FloatState?>()
        val enabled = mutableStateOf(true)
        val visible = mutableStateOf(true)
        val owner = Owner()
        compose.setContent {
            CompositionLocalProvider(LocalLifecycleOwner provides owner) {
                BackgroundMotionProvider("animated_blur", enabled.value, "balanced", 1f) {
                    if (visible.value) {
                        val value = rememberBackgroundMotionPhase(true)
                        SideEffect { phase.set(value) }
                        Box(Modifier.size(100.dp))
                    }
                }
            }
        }
        compose.waitUntil(4_000) { (phase.get()?.floatValue ?: 0f) > 0f }
        fun assertStopped() {
            compose.waitForIdle()
            val frozen = requireNotNull(phase.get()).floatValue
            SystemClock.sleep(250)
            compose.runOnIdle { assertEquals(frozen, requireNotNull(phase.get()).floatValue, 0f) }
        }
        fun assertResumed() {
            val frozen = requireNotNull(phase.get()).floatValue
            compose.waitUntil(4_000) { requireNotNull(phase.get()).floatValue != frozen }
        }
        compose.runOnIdle { enabled.value = false }
        assertStopped()
        compose.runOnIdle { enabled.value = true }
        assertResumed()
        compose.runOnIdle { owner.lifecycle.currentState = Lifecycle.State.STARTED }
        assertStopped()
        compose.runOnIdle { owner.lifecycle.currentState = Lifecycle.State.RESUMED }
        assertResumed()
        compose.runOnIdle { visible.value = false }
        assertStopped()
        compose.runOnIdle { visible.value = true }
        assertResumed()
    }
}

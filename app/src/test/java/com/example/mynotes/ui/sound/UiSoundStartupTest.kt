package com.example.mynotes.ui.sound

import android.content.Context
import android.media.SoundPool
import android.os.Looper
import com.example.mynotes.R
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements
import org.robolectric.annotation.LooperMode
import org.robolectric.annotation.RealObject
import org.robolectric.shadows.ShadowSoundPool

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28], shadows = [UiSoundStartupTest.CountingSoundPool::class])
@LooperMode(LooperMode.Mode.PAUSED)
class UiSoundStartupTest {
    @Implements(SoundPool::class)
    class CountingSoundPool : ShadowSoundPool() {
        @RealObject lateinit var realPool: SoundPool

        @Implementation
        public override fun load(context: Context, resId: Int, priority: Int): Int {
            val result = super.load(context, resId, priority)
            pool = realPool
            if (Looper.myLooper() == Looper.getMainLooper()) loadedOnMain.set(true)
            resources.add(resId)
            return result
        }

        companion object {
            val resources = CopyOnWriteArrayList<Int>()
            val loadedOnMain = AtomicBoolean(false)
            @Volatile var pool: SoundPool? = null
        }
    }

    private fun awaitCondition(check: () -> Boolean) = runBlocking {
        withTimeout(5_000L) { while (!check()) delay(10L) }
    }

    @Test fun startupLoadsOnlySelectedThemeAndColdPreviewPlaysOnce() {
        val context = RuntimeEnvironment.getApplication()
        UiSoundPlayer.configure(context, enabled = true, volumePercent = 65f, theme = "classic")
        awaitCondition { CountingSoundPool.resources.size >= 6 }
        assertEquals(setOf(R.raw.ui_edit, R.raw.ui_delete, R.raw.ui_priority, R.raw.ui_slider_tick,
            R.raw.ui_attachment, R.raw.ui_toggle), CountingSoundPool.resources.toSet())
        assertFalse(CountingSoundPool.loadedOnMain.get())

        // Reapplying settings should not load the same samples again.
        UiSoundPlayer.configure(context, enabled = true, volumePercent = 40f, theme = "classic")
        var fallbacks = 0
        UiClickSoundPolicy.runClick(
            action = { UiSoundPlayer.previewTheme(context, "soft") },
            fallback = { fallbacks++ }
        )
        assertEquals(0, fallbacks)
        awaitCondition { CountingSoundPool.resources.size >= 12 }
        assertEquals(12, CountingSoundPool.resources.size)
        val shadow = shadowOf(requireNotNull(CountingSoundPool.pool))
        assertFalse(shadow.wasResourcePlayed(R.raw.ui_soft_edit))
        shadow.notifyResourceLoaded(R.raw.ui_soft_edit, true)
        awaitCondition { shadow.wasResourcePlayed(R.raw.ui_soft_edit) }
        assertEquals(1, shadow.getResourcePlaybacks(R.raw.ui_soft_edit).size)

        assertEquals(12, CountingSoundPool.resources.size)
        assertFalse(CountingSoundPool.loadedOnMain.get())
    }
}

package com.example.mynotes

import android.app.Activity
import android.view.View
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
@Suppress("DEPRECATION")
class AndroidNavigationBarPolicyTest {
    @Test
    fun legacyNavigationIsVisibleOnlyWhileTheKeyboardIsOpen() {
        val activity = Robolectric.buildActivity(Activity::class.java).setup()
        try {
            val window = activity.get().window
            val decor = window.decorView
            val appearance = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

            decor.systemUiVisibility = appearance
            window.applyAndroidNavigationBarPolicy(imeVisible = false)
            assertTrue(decor.systemUiVisibility and View.SYSTEM_UI_FLAG_HIDE_NAVIGATION != 0)
            assertTrue(decor.systemUiVisibility and View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY != 0)

            window.applyAndroidNavigationBarPolicy(imeVisible = true)
            assertEquals(0, decor.systemUiVisibility and View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
            assertTrue(decor.systemUiVisibility and View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION != 0)

            window.applyAndroidNavigationBarPolicy(imeVisible = false)
            assertTrue(decor.systemUiVisibility and View.SYSTEM_UI_FLAG_HIDE_NAVIGATION != 0)
            assertEquals(appearance, decor.systemUiVisibility and appearance)
            assertEquals(0, decor.systemUiVisibility and View.SYSTEM_UI_FLAG_FULLSCREEN)
        } finally {
            activity.pause().stop().destroy()
        }
    }
}

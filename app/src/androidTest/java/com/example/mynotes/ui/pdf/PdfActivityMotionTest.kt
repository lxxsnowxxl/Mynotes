package com.example.mynotes.ui.pdf

import android.content.Intent
import android.os.Parcel
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.mynotes.settings.AppSettings
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PdfActivityMotionTest {
    @Test
    fun launchKeepsTheCompleteSelectedAppearanceForEveryToneAndBackgroundStyle() {
        for (style in listOf("solid", "gradient", "blur", "animated_blur")) {
            for (tone in 0..4) {
                val settings = AppSettings(
                    configurationMode = "advanced", darkMode = true,
                    backgroundColor = "indigo", backgroundToneIndex = tone,
                    settingsBackgroundStyle = style, accentColor = "palette",
                    textColor = "white", textOutlineEnabled = true,
                    soundEffectsEnabled = false, soundEffectsVolume = 23f,
                    reminderSoundVolume = 81f, optionMenuOpacity = 62f,
                    animationsEnabled = true, animationStyle = "depth_pull"
                )
                val launch = Intent().withPdfScreenMotion(settings)
                assertSame(settings, launch.pdfLaunchSettingsOrNull())
                assertEquals(style, launch.pdfLaunchSettingsOrNull()?.settingsBackgroundStyle)
                assertEquals(tone, launch.pdfLaunchSettingsOrNull()?.backgroundToneIndex)
            }
        }
    }

    @Test
    fun absentOrExpiredLaunchSettingsRemainNullSoTheActivityWaitsForPersistedSettings() {
        assertNull(Intent().pdfLaunchSettingsOrNull())
        val old = Intent().withPdfScreenMotion(AppSettings(backgroundColor = "ocean"))
        val latest = Intent().withPdfScreenMotion(AppSettings(backgroundColor = "plum"))
        assertNull(old.pdfLaunchSettingsOrNull())
        assertEquals("plum", latest.pdfLaunchSettingsOrNull()?.backgroundColor)
    }

    @Test
    fun activityIntentParcelRoundTripPreservesTheLaunchTokenAndMotionPreferences() {
        val settings = AppSettings(
            configurationMode = "advanced", backgroundColor = "plum", backgroundToneIndex = 4,
            settingsBackgroundStyle = "animated_blur", animationStyle = "corner_unfold",
            animationEasing = "expressive", animationSpeed = 1.6f,
            animationIntensity = 1.2f, performanceMode = "quality"
        )
        val launch = Intent().withPdfScreenMotion(settings)
        val parcel = Parcel.obtain()
        try {
            launch.writeToParcel(parcel, 0)
            parcel.setDataPosition(0)
            val restored = Intent.CREATOR.createFromParcel(parcel)
            assertSame(settings, restored.pdfLaunchSettingsOrNull())
            val motion = restored.pdfScreenMotionOr(AppSettings())
            assertEquals(settings.animationStyle, motion.animationStyle)
            assertEquals(settings.animationEasing, motion.animationEasing)
            assertEquals(settings.animationSpeed, motion.animationSpeed, 0f)
            assertEquals(settings.animationIntensity, motion.animationIntensity, 0f)
            assertEquals(settings.performanceMode, motion.performanceMode)
        } finally {
            parcel.recycle()
        }
    }
}

package com.example.mynotes.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import com.example.mynotes.settings.AppSettings
import com.example.mynotes.ui.preview.LayoutPreviewData
import com.example.mynotes.ui.preview.LocalLayoutPreview
import com.example.mynotes.ui.theme.MyNotesTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.math.abs

@RunWith(AndroidJUnit4::class)
@SdkSuppress(minSdkVersion = 26)
class StyledSettingsSliderTest {
    @get:Rule val compose = createComposeRule()

    @Test
    fun changingSavedStyleRedrawsTheSameSliderAndPreservesItsRangeAndCallbacks() {
        val settings = mutableStateOf(AppSettings(configurationMode = "advanced", sliderStyle = "glass"))
        val value = mutableFloatStateOf(0.5f)
        val enabled = mutableStateOf(true)
        val active = Color(0xFF4056A0)
        var commits = 0

        compose.setContent {
            CompositionLocalProvider(LocalLayoutPreview provides LayoutPreviewData()) {
                MyNotesTheme(settings.value, darkTheme = false) {
                    Box(Modifier.width(260.dp).background(Color(0xFFE9E9E9)).testTag("slider_background")) {
                        StyledSettingsSlider(
                            value = value.floatValue,
                            onValueChange = { value.floatValue = it },
                            onValueChangeFinished = { commits++ },
                            valueRange = 0f..1f,
                            steps = 3,
                            modifier = Modifier.testTag("slider"),
                            enabled = enabled.value,
                            activeColor = active,
                            inactiveColor = Color.DarkGray
                        )
                    }
                }
            }
        }

        val slider = compose.onNodeWithTag("slider")
        val background = compose.onNodeWithTag("slider_background")
        val glass = background.captureToImage()
        assertTrue(glass.boundsOf(Color.White) == null)
        compose.runOnIdle { settings.value = settings.value.copy(sliderStyle = "capsule") }
        val capsule = background.captureToImage()
        val thumb = requireNotNull(capsule.boundsOf(Color.White))
        val track = requireNotNull(capsule.boundsOf(Color.DarkGray))
        // Las medidas esperadas son 20 dp para el indicador y 14 dp para la pista.
        val density = capsule.width / 260f
        assertTrue(abs(thumb.width - 20 * density) <= 2f)
        assertTrue(abs(thumb.height - 20 * density) <= 2f)
        assertTrue(abs(track.height - 14 * density) <= 2f)
        assertTrue(abs(capsule.height - 44 * density) <= 1f)
        compose.runOnIdle {
            assertEquals(0.5f, value.floatValue, 0f)
            assertEquals(0, commits)
        }

        slider.performSemanticsAction(SemanticsActions.SetProgress) { setProgress ->
            assertTrue(setProgress(0.8f))
        }
        compose.runOnIdle {
            assertEquals(0.75f, value.floatValue, 0f)
            assertEquals(1, commits)
        }
        slider.performTouchInput {
            down(Offset(width * 0.72f, center.y))
            moveTo(Offset(width * 0.95f, center.y))
        }
        val draggedThumb = requireNotNull(background.captureToImage().boundsOf(Color.White))
        assertTrue(draggedThumb.left > thumb.left)
        assertTrue(abs(draggedThumb.width - thumb.width) <= 1)
        assertTrue(abs(draggedThumb.height - thumb.height) <= 1)
        slider.performTouchInput { up() }
        compose.runOnIdle {
            assertEquals(1f, value.floatValue, 0f)
            enabled.value = false
        }
        slider.assertIsNotEnabled()
    }

    private fun ImageBitmap.boundsOf(color: Color): IntRect? {
        val pixels = toPixelMap()
        var left = width
        var right = -1
        var top = height
        var bottom = -1
        for (y in 0 until height) {
            for (x in 0 until width) {
                if (pixels[x, y] == color) {
                    left = minOf(left, x)
                    right = maxOf(right, x)
                    top = minOf(top, y)
                    bottom = maxOf(bottom, y)
                }
            }
        }
        return if (right < 0) null else IntRect(left, top, right + 1, bottom + 1)
    }
}

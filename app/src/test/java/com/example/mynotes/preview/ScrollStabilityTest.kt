package com.example.mynotes.preview

import android.graphics.Bitmap
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.example.mynotes.ui.components.NoteListLinkPreview
import com.example.mynotes.ui.components.PaletteSelectorRow
import com.example.mynotes.ui.components.SettingsBackgroundColors
import com.example.mynotes.ui.components.SettingsBackgroundOptions
import com.example.mynotes.ui.preview.LayoutPreviewData
import com.example.mynotes.ui.preview.LocalLayoutPreview
import com.example.mynotes.ui.theme.PaletteCatalog
import com.example.mynotes.ui.theme.settingsSectionColors
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.robolectric.annotation.LooperMode

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], qualifiers = "w360dp-h800dp-mdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@LooperMode(LooperMode.Mode.PAUSED)
class ScrollStabilityTest {
    @get:Rule val compose = createComposeRule()

    @Test fun shortLinkMetadataDoesNotReserveUnusedTitleOrDescriptionLines() {
        val rich = mutableStateOf(false)
        val bitmap = Bitmap.createBitmap(8, 8, Bitmap.Config.ARGB_8888)
        compose.setContent {
            val data = if (rich.value) LayoutPreviewData(
                linkTitle = "A long title that wraps across several lines in a narrow note card",
                linkDescription = "A detailed description received after loading the link preview.", mediaBitmap = bitmap
            ) else LayoutPreviewData(linkTitle = "Blondie", mediaBitmap = bitmap)
            CompositionLocalProvider(LocalLayoutPreview provides data) {
                MaterialTheme {
                    val scrolling = remember { mutableStateOf(false) }
                    // Changing the URL simulates replacing initial metadata with the loaded result.
                    val url = if (rich.value) "https://example.com/loaded" else "https://example.com/initial"
                    androidx.compose.foundation.layout.Column {
                        Box(Modifier.width(150.dp)) {
                            NoteListLinkPreview(url, Modifier.testTag("column_link"), "auto", scrolling)
                        }
                        Box(Modifier.width(340.dp)) {
                            NoteListLinkPreview(url, Modifier.testTag("wide_link"), "auto", scrolling)
                        }
                    }
                }
            }
        }
        compose.waitForIdle()
        val columnHeight = compose.onNodeWithTag("column_link").fetchSemanticsNode().boundsInRoot.height
        val wideHeight = compose.onNodeWithTag("wide_link").fetchSemanticsNode().boundsInRoot.height
        compose.runOnIdle { rich.value = true }
        compose.waitForIdle()
        assertTrue(columnHeight < compose.onNodeWithTag("column_link").fetchSemanticsNode().boundsInRoot.height)
        assertTrue(wideHeight < compose.onNodeWithTag("wide_link").fetchSemanticsNode().boundsInRoot.height)
        compose.runOnIdle { rich.value = false }
        compose.waitForIdle()
        assertEquals(columnHeight, compose.onNodeWithTag("column_link").fetchSemanticsNode().boundsInRoot.height, 0.5f)
        assertEquals(wideHeight, compose.onNodeWithTag("wide_link").fetchSemanticsNode().boundsInRoot.height, 0.5f)
    }

    @Test fun linksWithoutAnImageDoNotReserveAnEmptyThumbnail() {
        val withImage = mutableStateOf(false)
        val bitmap = Bitmap.createBitmap(8, 8, Bitmap.Config.ARGB_8888)
        compose.setContent {
            val data = LayoutPreviewData(linkTitle = "Short title", mediaBitmap = if (withImage.value) bitmap else null)
            CompositionLocalProvider(LocalLayoutPreview provides data) {
                MaterialTheme {
                    val scrolling = remember { mutableStateOf(false) }
                    val url = if (withImage.value) "https://example.com/image" else "https://example.com/text"
                    Box(Modifier.width(150.dp)) {
                        NoteListLinkPreview(url, Modifier.testTag("link"), "auto", scrolling)
                    }
                }
            }
        }
        compose.waitForIdle()
        val textHeight = compose.onNodeWithTag("link").fetchSemanticsNode().boundsInRoot.height
        compose.runOnIdle { withImage.value = true }
        compose.waitForIdle()
        val imageHeight = compose.onNodeWithTag("link").fetchSemanticsNode().boundsInRoot.height
        val expectedMediaHeight = 170f * RuntimeEnvironment.getApplication().resources.displayMetrics.density
        assertEquals(expectedMediaHeight, imageHeight - textHeight, 0.5f)
    }

    @Test fun paletteBrowsingPositionSurvivesLeavingTheVerticalViewport() {
        val mounted = mutableStateOf(true)
        val palettes = PaletteCatalog.settingsPalettes
        val target = palettes[5]
        compose.setContent {
            MaterialTheme {
                val holder = rememberSaveableStateHolder()
                if (mounted.value) holder.SaveableStateProvider("palette") {
                    PaletteSelectorRow(palettes, palettes.first().key, 0, { _, _ -> }, animationsEnabled = false)
                }
            }
        }
        compose.onNode(SemanticsMatcher.keyIsDefined(SemanticsActions.ScrollToIndex)).performScrollToIndex(5)
        val label = RuntimeEnvironment.getApplication().getString(target.labelRes)
        compose.onNodeWithText(label).assertIsDisplayed()
        compose.runOnIdle { mounted.value = false }
        compose.waitForIdle()
        compose.runOnIdle { mounted.value = true }
        compose.waitForIdle()
        compose.onNodeWithText(label).assertIsDisplayed()
    }

    @Test fun backgroundStyleBrowsingPositionSurvivesLeavingTheVerticalViewport() {
        val mounted = mutableStateOf(true)
        compose.setContent {
            MaterialTheme {
                val holder = rememberSaveableStateHolder()
                if (mounted.value) holder.SaveableStateProvider("background") {
                    SettingsBackgroundOptions(
                        selectedStyle = "solid",
                        colors = SettingsBackgroundColors(Color.White, Color.LightGray, Color.Gray),
                        panel = settingsSectionColors(Color.White, "auto"),
                        fontFamily = FontFamily.Default,
                        onStyleSelected = {}
                    )
                }
            }
        }
        val row = compose.onNode(SemanticsMatcher.keyIsDefined(SemanticsProperties.HorizontalScrollAxisRange))
        row.performTouchInput { swipeLeft() }
        compose.waitForIdle()
        val position = row.fetchSemanticsNode().config[SemanticsProperties.HorizontalScrollAxisRange].value()
        assertTrue(position > 0f)
        compose.runOnIdle { mounted.value = false }
        compose.waitForIdle()
        compose.runOnIdle { mounted.value = true }
        compose.waitForIdle()
        val restored = row.fetchSemanticsNode().config[SemanticsProperties.HorizontalScrollAxisRange].value()
        assertEquals(position, restored, 0.5f)
    }
}

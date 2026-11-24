package com.example.mynotes.preview

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.example.mynotes.R
import com.example.mynotes.data.NoteDraft
import com.example.mynotes.settings.AppSettings
import com.example.mynotes.ui.NoteEditorScreen
import com.example.mynotes.ui.components.extractEmbeddedLinkUrls
import com.example.mynotes.ui.components.noteContentForStorage
import com.example.mynotes.ui.components.stripEmbeddedLinkMetadata
import com.example.mynotes.ui.preview.LayoutPreviewData
import com.example.mynotes.ui.preview.LocalLayoutPreview
import org.junit.Assert.*
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
class LinkOnlyNoteEditorTest {
    @get:Rule val compose = createComposeRule()

    private fun render(content: String = "", onSave: (NoteDraft) -> Unit = {}) {
        compose.setContent {
            // Inert previews prevent network requests and draft recovery during these editor tests.
            CompositionLocalProvider(LocalLayoutPreview provides LayoutPreviewData()) {
                MaterialTheme {
                    NoteEditorScreen(
                        settings = AppSettings(configurationMode = "basic", animationsEnabled = false),
                        initialContent = content, onSave = onSave, onCancel = {}
                    )
                }
            }
        }
    }

    private fun saveButton() = compose.onNodeWithText(RuntimeEnvironment.getApplication().getString(R.string.save))

    @Test fun previewOnlyUrlKeepsSaveEnabledAndIsIncludedInTheDraft() {
        val url = "https://open.spotify.com/track/offline-test?si=preserved"
        var saved: NoteDraft? = null
        // This is the editor state after the pasted URL has moved into its preview card.
        render(noteContentForStorage("", listOf(url))) { saved = it }
        saveButton().assertIsEnabled().performClick()
        compose.runOnIdle {
            val draft = requireNotNull(saved)
            assertEquals("", draft.title)
            assertTrue(stripEmbeddedLinkMetadata(draft.content).isBlank())
            assertEquals(listOf(url), extractEmbeddedLinkUrls(draft.content))
        }
    }

    @Test fun pastedUrlCanBeSavedBeforeAnyPreviewMetadataArrives() {
        val url = "https://example.com/page?item=1&source=paste"
        var saved: NoteDraft? = null
        render { saved = it }
        saveButton().assertIsNotEnabled()
        val fields = compose.onAllNodes(hasSetTextAction())
        fields.assertCountEquals(2)
        fields[1].performTextInput(url)
        saveButton().assertIsEnabled().performClick()
        compose.runOnIdle { assertTrue(requireNotNull(saved).content.contains(url)) }
    }

    @Test fun anActuallyEmptyNewNoteStillCannotBeSaved() {
        render()
        saveButton().assertIsNotEnabled()
    }
}

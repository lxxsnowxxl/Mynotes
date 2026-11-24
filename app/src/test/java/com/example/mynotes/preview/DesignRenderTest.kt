package com.example.mynotes.preview

import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.example.mynotes.R
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.graphics.Color
import com.example.mynotes.settings.AppSettings
import com.example.mynotes.studio.StudioStore
import com.example.mynotes.studio.StudioState
import com.example.mynotes.studio.LocalCleanWorkspace
import com.example.mynotes.ui.preview.LayoutPreviewData
import com.example.mynotes.ui.preview.LocalLayoutPreview
import com.example.mynotes.ui.components.appScreenContainerColor
import com.example.mynotes.ui.theme.MyNotesTheme
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.robolectric.RuntimeEnvironment
import java.io.File
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.robolectric.annotation.LooperMode

/** Renders the real Compose screens with inert preview data, without a device. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], qualifiers = "w360dp-h800dp-mdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@LooperMode(LooperMode.Mode.PAUSED)
class DesignRenderTest {
    @get:Rule val compose = createComposeRule()

    private fun render(scene: String, name: String, fontScale: Float = 1f) {
        compose.setContent {
            CompositionLocalProvider(LocalDensity provides Density(LocalDensity.current.density, fontScale)) {
                LayoutPreviewScreen(scene)
            }
        }
        compose.waitForIdle()
        val bitmap = compose.onRoot().captureToImage().asAndroidBitmap()
        check(bitmap.width >= 300 && bitmap.height >= 300)
        val folder = File(System.getProperty("mynotes.design.output", "build/reports/design317"))
        folder.mkdirs()
        File(folder, "$name.png").outputStream().use {
            check(bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, it))
        }
    }

    @Test fun noteEditorPhone() = render("preview_02_note_editor", "note_phone")
    @Test fun originalHomePhone() = render("preview_01_notes", "home_phone")
    @Test fun originalSettingsPhone() = render("preview_04_settings", "settings_phone")
    @Test fun originalNoteDetailPhone() {
        render("preview_03_note_detail", "detail_phone")
        val title=compose.onNodeWithText("Proyecto de diseño").fetchSemanticsNode().boundsInRoot
        val tools=compose.onNodeWithText("Paragraph bookmarks").fetchSemanticsNode().boundsInRoot
        assertTrue(title.bottom<=tools.top)
    }
    @Test fun reminderPhone() = render("preview_30_reminder_editor", "reminder_phone")
    @Test fun drawingPhone() = render("preview_28_drawing", "drawing_phone")
    @Test fun pdfPhone() = render("preview_33_pdf_editor_loaded", "pdf_phone")
    @Test @Config(qualifiers = "w320dp-h640dp-mdpi")
    fun noteEditorSmallLargeText() {
        render("preview_02_note_editor", "note_small_large_text",2f)
        val app=RuntimeEnvironment.getApplication()
        val save=compose.onNodeWithContentDescription(app.getString(R.string.save)).fetchSemanticsNode().boundsInRoot
        val microphone=compose.onNodeWithContentDescription(app.getString(R.string.record_voice_note)).fetchSemanticsNode().boundsInRoot
        assertTrue(save.left>=microphone.right)
        assertTrue(save.right<=320f);assertTrue(save.width>=48f);assertTrue(save.height>=48f)
    }
    @Test @Config(qualifiers = "w1200dp-h800dp-mdpi")
    fun drawingTablet() {
        render("preview_28_drawing", "drawing_tablet")
        assertTrue(compose.onNodeWithTag("tools_primary_panel").fetchSemanticsNode().boundsInRoot.width>=240f)
        assertTrue(compose.onNodeWithTag("tools_secondary_panel").fetchSemanticsNode().boundsInRoot.width>=240f)
    }
    @Test @Config(qualifiers = "w1200dp-h800dp-mdpi")
    fun reminderTablet() = render("preview_30_reminder_editor", "reminder_tablet")
    @Test @Config(qualifiers = "w1200dp-h800dp-mdpi")
    fun drawingTabletLargeText() {
        render("preview_28_drawing", "drawing_tablet_large_text",2f)
        assertTrue(compose.onNodeWithTag("tools_primary_panel").fetchSemanticsNode().boundsInRoot.width>=480f)
        assertTrue(compose.onNodeWithTag("tools_secondary_panel").fetchSemanticsNode().boundsInRoot.width>=480f)
    }

    @Test fun originalBackgroundSelectionRemainsVisibleByDefault() {
        val store=StudioStore.get(RuntimeEnvironment.getApplication())
        runBlocking{store.replace(StudioState())}
        var clean:Boolean?=null
        var bar=Color.Unspecified
        compose.setContent {
            CompositionLocalProvider(LocalLayoutPreview provides LayoutPreviewData()) {
                MyNotesTheme(AppSettings(configurationMode="advanced",settingsBackgroundStyle="gradient",animationsEnabled=false),darkTheme=false) {
                    clean=LocalCleanWorkspace.current
                    bar=appScreenContainerColor()
                }
            }
        }
        compose.waitForIdle()
        assertEquals(false,clean);assertEquals(Color.Transparent,bar)
    }

    @Test fun explicitCleanSurfacesAndFixedNoteColorsAreRespected() {
        val store=StudioStore.get(RuntimeEnvironment.getApplication())
        runBlocking{store.replace(StudioState());store.edit("preferences","polish"){it.put("clean",true)}}
        var clean:Boolean?=null
        var bar=Color.Unspecified
        compose.setContent {
            CompositionLocalProvider(LocalLayoutPreview provides LayoutPreviewData()) {
                MyNotesTheme(AppSettings(configurationMode="advanced",settingsBackgroundStyle="gradient",animationsEnabled=false),darkTheme=false) {
                    clean=LocalCleanWorkspace.current
                    bar=appScreenContainerColor(Color.Red,useGlobalStyle=false)
                }
            }
        }
        compose.waitForIdle()
        assertEquals(true,clean);assertEquals(Color.Red,bar)
    }
}

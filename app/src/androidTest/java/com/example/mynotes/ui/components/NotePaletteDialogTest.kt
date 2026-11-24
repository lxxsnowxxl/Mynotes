package com.example.mynotes.ui.components

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToKey
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.mynotes.R
import com.example.mynotes.settings.AppSettings
import com.example.mynotes.ui.theme.MyNotesTheme
import com.example.mynotes.ui.theme.PaletteCatalog
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.atomic.AtomicBoolean
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NotePaletteDialogTest {
    @get:Rule val compose = createComposeRule()

    @Test
    fun horizontalScrollAndToneSelectionReturnTheNoteTokenWithoutChangingTheTheme() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val selected = AtomicReference<String?>()
        val settings = AppSettings(backgroundColor = "ocean", backgroundToneIndex = 1)
        compose.setContent {
            MyNotesTheme(settings, darkTheme = false) {
                NotePaletteDialog(selectedColor = "default", onDismiss = {}, onColorSelected = { selected.set(it) })
            }
        }
        val label = context.getString(PaletteCatalog.find("lavender").labelRes)
        compose.onNodeWithTag("note_palette_carousel").performScrollToKey("lavender")
        val description = context.getString(R.string.note_palette_select_tone, 4, label)
        compose.onNodeWithContentDescription(description).performClick()
        assertEquals("palette:lavender:3", selected.get())
        assertEquals("ocean", settings.backgroundColor)
        assertEquals(1, settings.backgroundToneIndex)
    }

    @Test
    fun closingThePickerDoesNotApplyAColor() {
        val selected = AtomicReference<String?>()
        val dismissed = AtomicBoolean(false)
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        compose.setContent {
            MyNotesTheme(AppSettings(), darkTheme = false) {
                NotePaletteDialog(selectedColor = "red", onDismiss = { dismissed.set(true) }, onColorSelected = { selected.set(it) })
            }
        }
        compose.onNodeWithContentDescription(context.getString(R.string.note_palette_close)).performClick()
        assertNull(selected.get())
        assertTrue(dismissed.get())
    }
}

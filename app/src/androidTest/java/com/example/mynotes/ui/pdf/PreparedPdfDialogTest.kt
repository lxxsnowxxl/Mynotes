package com.example.mynotes.ui.pdf

import android.os.Build
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCompositionContext
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.unit.LayoutDirection
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PreparedPdfDialogTest {
    @get:Rule val compose = createComposeRule()

    @Suppress("DEPRECATION")
    @Test
    fun android9WindowAlreadyHidesNavigationWhenItAttachesAndThenAcceptsFocus() {
        assumeTrue(Build.VERSION.SDK_INT < Build.VERSION_CODES.R)
        val attached = AtomicBoolean(false)
        val blockedAtAttach = AtomicBoolean(false)
        val hiddenAtAttach = AtomicBoolean(false)
        val dialogReference = AtomicReference<PreparedPdfWindow>()
        compose.setContent {
            val host = LocalView.current
            val parent = rememberCompositionContext()
            DisposableEffect(host) {
                val dialog = PreparedPdfWindow(host, parent, View.generateViewId(), LayoutDirection.Ltr,
                    onDismissRequest = {}, content = { Text("Attachment probe") })
                dialogReference.set(dialog)
                val decor = requireNotNull(dialog.window).decorView
                val listener = object : View.OnAttachStateChangeListener {
                    override fun onViewAttachedToWindow(view: View) {
                        attached.set(true)
                        blockedAtAttach.set(requireNotNull(dialog.window).attributes.flags and
                            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE != 0)
                        hiddenAtAttach.set(view.systemUiVisibility and View.SYSTEM_UI_FLAG_HIDE_NAVIGATION != 0 &&
                            view.systemUiVisibility and View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY != 0)
                    }
                    override fun onViewDetachedFromWindow(view: View) = Unit
                }
                decor.addOnAttachStateChangeListener(listener)
                dialog.show()
                onDispose {
                    decor.removeOnAttachStateChangeListener(listener)
                    dialog.dismiss()
                    dialog.disposeContent()
                }
            }
            Box { }
        }
        compose.waitForIdle()
        assertTrue(attached.get())
        assertTrue(blockedAtAttach.get())
        assertTrue(hiddenAtAttach.get())
        compose.runOnIdle {
            val dialog = dialogReference.get()
            assertTrue(dialog.isShowing)
            assertEquals(0, requireNotNull(dialog.window).attributes.flags and WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
        }
    }

    @Test
    fun textCanBeEditedAndTheLatestValueIsConfirmedOnceAfterFocusIsRestored() {
        val result = AtomicReference<String?>()
        val confirmations = AtomicInteger(0)
        compose.setContent {
            MaterialTheme {
                var visible by remember { mutableStateOf(true) }
                var name by remember { mutableStateOf("Original") }
                if (visible) PreparedPdfAlertDialog(
                    onDismissRequest = { visible = false },
                    title = { Text("Rename test") },
                    text = { OutlinedTextField(name, { name = it }, Modifier.testTag("pdf_name")) },
                    confirmButton = {
                        TextButton(onClick = { result.set(name); confirmations.incrementAndGet(); visible = false }) {
                            Text("Confirm")
                        }
                    },
                    dismissButton = { TextButton(onClick = { visible = false }) { Text("Cancel") } }
                )
            }
        }
        compose.onNodeWithTag("pdf_name").performClick().assertIsFocused().performTextReplacement("Changed")
        compose.onNodeWithText("Confirm").performClick()
        compose.waitForIdle()
        assertEquals("Changed", result.get())
        assertEquals(1, confirmations.get())
        compose.onNodeWithText("Rename test").assertDoesNotExist()
    }

    @Test
    fun backDismissesTheDialogWithoutRunningTheConfirmAction() {
        val dismissed = AtomicBoolean(false)
        val confirmed = AtomicBoolean(false)
        compose.setContent {
            MaterialTheme {
                var visible by remember { mutableStateOf(true) }
                if (visible) PreparedPdfAlertDialog(
                    onDismissRequest = { dismissed.set(true); visible = false },
                    title = { Text("Back test") },
                    text = { Text("Body") },
                    confirmButton = { TextButton(onClick = { confirmed.set(true) }) { Text("Confirm") } },
                    dismissButton = { TextButton(onClick = { visible = false }) { Text("Cancel") } }
                )
            }
        }
        compose.waitForIdle()
        InstrumentationRegistry.getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_BACK)
        compose.waitForIdle()
        assertTrue(dismissed.get())
        assertFalse(confirmed.get())
        compose.onNodeWithText("Back test").assertDoesNotExist()
    }
}

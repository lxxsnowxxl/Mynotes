package com.example.mynotes.ui.components

import android.view.View
import android.view.ViewTreeObserver
import android.view.Window
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.DialogWindowProvider
import com.example.mynotes.applyAndroidNavigationBarPolicy
import com.example.mynotes.observeAndroidNavigationBarIme

/** Run inside the dialog content so the policy applies to the dialog's own window. */
@Composable
internal fun HideDialogNavigationBar(preventFocusFlash: Boolean = false) {
    val view = LocalView.current
    DisposableEffect(view, preventFocusFlash) {
        val window = generateSequence(view.parent) { it.parent }
            .filterIsInstance<DialogWindowProvider>().firstOrNull()?.window
        val stop = if (view.isInEditMode) null else window?.installDialogNavigationBarPolicy(preventFocusFlash)
        onDispose { stop?.invoke() }
    }
}

internal fun Window.installDialogNavigationBarPolicy(preventFocusFlash: Boolean): () -> Unit {
    val decor = decorView
    val focusFlag = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
    var ownsFocusGuard = preventFocusFlash && !decor.isAttachedToWindow && (attributes.flags and focusFlag == 0)
    if (ownsFocusGuard) addFlags(focusFlag)
    if (preventFocusFlash) setWindowAnimations(0)

    val applyNavigation = Runnable { applyAndroidNavigationBarPolicy() }
    val releaseFocusGuard = Runnable {
        if (ownsFocusGuard && decor.isAttachedToWindow) {
            applyNavigation.run()
            clearFlags(focusFlag)
            ownsFocusGuard = false
            applyNavigation.run()
        }
    }
    val attachListener = object : View.OnAttachStateChangeListener {
        override fun onViewAttachedToWindow(view: View) {
            applyNavigation.run()
            if (ownsFocusGuard) view.post(releaseFocusGuard)
        }

        override fun onViewDetachedFromWindow(view: View) {
            view.removeCallbacks(applyNavigation)
            view.removeCallbacks(releaseFocusGuard)
        }
    }
    val focusListener = ViewTreeObserver.OnWindowFocusChangeListener { hasFocus ->
        if (hasFocus) {
            applyNavigation.run()
            decor.removeCallbacks(applyNavigation)
            decor.post(applyNavigation)
        }
    }

    // Install before showing the dialog to avoid an intermediate frame with system buttons.
    decor.addOnAttachStateChangeListener(attachListener)
    val observer = decor.viewTreeObserver
    observer.addOnWindowFocusChangeListener(focusListener)
    val stopObservingIme = observeAndroidNavigationBarIme()
    applyNavigation.run()

    return {
        stopObservingIme()
        decor.removeCallbacks(applyNavigation)
        decor.removeCallbacks(releaseFocusGuard)
        decor.removeOnAttachStateChangeListener(attachListener)
        if (ownsFocusGuard) {
            clearFlags(focusFlag)
            ownsFocusGuard = false
        }
        val activeObserver = if (observer.isAlive) observer else decor.viewTreeObserver
        if (activeObserver.isAlive) activeObserver.removeOnWindowFocusChangeListener(focusListener)
    }
}

package com.example.mynotes.ui.pdf

import android.content.pm.ApplicationInfo
import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.activity.ComponentDialog
import androidx.activity.addCallback
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionContext
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCompositionContext
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.semantics.dialog
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindowProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.findViewTreeSavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.example.mynotes.R
import com.example.mynotes.applyAndroidNavigationBarPolicy
import com.example.mynotes.observeAndroidNavigationBarIme

/** La ventana se configura antes de show(), sin depender de efectos del contenido. */
@Composable
internal fun PreparedPdfAlertDialog(
    onDismissRequest: () -> Unit,
    title: @Composable () -> Unit,
    text: @Composable () -> Unit,
    confirmButton: @Composable () -> Unit,
    dismissButton: @Composable () -> Unit
) {
    val content: @Composable () -> Unit = {
        PreparedPdfDialogPanel(title, text, confirmButton, dismissButton)
    }
    if (LocalInspectionMode.current) {
        content()
        return
    }
    val host = LocalView.current
    val density = LocalDensity.current
    val direction = LocalLayoutDirection.current
    val parentComposition = rememberCompositionContext()
    val viewId = rememberSaveable { View.generateViewId() }
    val currentDismiss by rememberUpdatedState(onDismissRequest)
    val currentContent by rememberUpdatedState(content)
    val dialog = remember(host, density, direction) {
        PreparedPdfWindow(host, parentComposition, viewId, direction,
            onDismissRequest = { currentDismiss() }, content = { currentContent() })
    }
    DisposableEffect(dialog) {
        try {
            dialog.show()
        } catch (failure: Throwable) {
            dialog.dismiss()
            dialog.disposeContent()
            throw failure
        }
        onDispose {
            dialog.dismiss()
            dialog.disposeContent()
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PreparedPdfDialogPanel(
    title: @Composable () -> Unit,
    text: @Composable () -> Unit,
    confirmButton: @Composable () -> Unit,
    dismissButton: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier.widthIn(min = 280.dp, max = 560.dp).semantics { dialog() },
        shape = AlertDialogDefaults.shape,
        color = AlertDialogDefaults.containerColor,
        tonalElevation = AlertDialogDefaults.TonalElevation
    ) {
        Column(Modifier.padding(24.dp)) {
            CompositionLocalProvider(LocalContentColor provides AlertDialogDefaults.titleContentColor) {
                ProvideTextStyle(MaterialTheme.typography.headlineSmall) {
                    Box(Modifier.padding(bottom = 16.dp)) { title() }
                }
            }
            CompositionLocalProvider(LocalContentColor provides AlertDialogDefaults.textContentColor) {
                ProvideTextStyle(MaterialTheme.typography.bodyMedium) {
                    Box(Modifier.weight(1f, fill = false).padding(bottom = 24.dp)) { text() }
                }
            }
            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.primary) {
                ProvideTextStyle(MaterialTheme.typography.labelLarge) {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        dismissButton()
                        confirmButton()
                    }
                }
            }
        }
    }
}

internal class PreparedPdfWindow(
    host: View,
    parentComposition: CompositionContext,
    viewId: Int,
    direction: LayoutDirection,
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit
) : ComponentDialog(ContextThemeWrapper(host.context, R.style.PreparedPdfDialogOverlay)) {
    private val dialogWindow = requireNotNull(window)
    private val debug = (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
    private val stopObservingIme: () -> Unit
    private val composeView = ComposeView(context).apply {
        id = viewId
        setParentCompositionContext(parentComposition)
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent(content)
    }

    init {
        dialogWindow.requestFeature(Window.FEATURE_NO_TITLE)
        dialogWindow.setBackgroundDrawableResource(android.R.color.transparent)
        WindowCompat.setDecorFitsSystemWindows(dialogWindow, false)
        @Suppress("DEPRECATION")
        val hostBars = ViewCompat.getWindowInsetsController(host)
        hostBars?.let { appearance ->
            WindowCompat.getInsetsController(dialogWindow, dialogWindow.decorView).apply {
                isAppearanceLightStatusBars = appearance.isAppearanceLightStatusBars
                isAppearanceLightNavigationBars = appearance.isAppearanceLightNavigationBars
            }
        }
        dialogWindow.setWindowAnimations(0)
        dialogWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        val root = PreparedPdfRoot(context, dialogWindow).apply {
            clipChildren = false
            layoutDirection = if (direction == LayoutDirection.Rtl) View.LAYOUT_DIRECTION_RTL else View.LAYOUT_DIRECTION_LTR
            setViewTreeLifecycleOwner(host.findViewTreeLifecycleOwner())
            setViewTreeViewModelStoreOwner(host.findViewTreeViewModelStoreOwner())
            setViewTreeSavedStateRegistryOwner(host.findViewTreeSavedStateRegistryOwner())
            addView(composeView, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
            ))
        }
        setContentView(root)
        dialogWindow.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT)
        val secure = host.rootView.layoutParams as? WindowManager.LayoutParams
        if (secure != null && secure.flags and WindowManager.LayoutParams.FLAG_SECURE != 0) {
            dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
        onBackPressedDispatcher.addCallback(this) { onDismissRequest() }
        setCanceledOnTouchOutside(true)
        setOnCancelListener { onDismissRequest() }
        stopObservingIme = dialogWindow.observeAndroidNavigationBarIme()
        prepareNavigation()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prepareNavigation()
    }

    override fun show() {
        if (isShowing) return
        val focusFlag = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        val ownsFocusFlag = dialogWindow.attributes.flags and focusFlag == 0
        if (ownsFocusFlag) dialogWindow.addFlags(focusFlag)
        try {
            prepareNavigation()
            if (debug) Log.d("MyNotesDialogNav", "v307 beforeShow attached=${dialogWindow.decorView.isAttachedToWindow} focusBlocked=${dialogWindow.attributes.flags and focusFlag != 0}")
            super.show()
            prepareNavigation()
        } finally {
            // Recuperar entrada y teclado en la misma llamada, sin esperar a post().
            if (ownsFocusFlag) dialogWindow.clearFlags(focusFlag)
            prepareNavigation()
        }
        if (debug) Log.d("MyNotesDialogNav", "v307 focusReady")
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) prepareNavigation()
    }

    private fun prepareNavigation() = dialogWindow.applyAndroidNavigationBarPolicy()

    fun disposeContent() {
        stopObservingIme()
        composeView.disposeComposition()
    }
}

private class PreparedPdfRoot(context: android.content.Context, override val window: Window) :
    FrameLayout(context), DialogWindowProvider

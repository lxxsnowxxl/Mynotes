package com.example.mynotes.ui.pdf

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewTreeObserver
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.SystemBarStyle
import androidx.compose.runtime.Composable
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mynotes.R
import com.example.mynotes.SyncDisplayPerformance
import com.example.mynotes.SyncUiFeedback
import com.example.mynotes.applyAndroidNavigationBarPolicy
import com.example.mynotes.installKeyboardAwareNavigationBarPolicy
import com.example.mynotes.performance.DisplayPerformanceController
import com.example.mynotes.settings.AppSettings
import com.example.mynotes.settings.SettingsRepository
import com.example.mynotes.ui.theme.MyNotesTheme
import com.example.mynotes.ui.theme.effectiveDarkTheme
import com.example.mynotes.withSavedAppLocale

abstract class ImmersivePdfActivity : ComponentActivity() {
    private val screenVisibility = MutableTransitionState(false)
    private var launchSettings: AppSettings? = null
    private var activeMotionSettings = AppSettings()
    private var finishingWithMotion by mutableStateOf(false)
    private var firstFrameReady = false
    private var appliedWindowColor: Int? = null
    private var firstDrawListener: ViewTreeObserver.OnPreDrawListener? = null

    override fun attachBaseContext(newBase: Context) = super.attachBaseContext(newBase.withSavedAppLocale())

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_MyNotes_Pdf)
        super.onCreate(savedInstanceState)
        launchSettings = intent.pdfLaunchSettingsOrNull()
        screenVisibility.targetState = true
        suppressPendingActivityAnimation()
        enableEdgeToEdge(navigationBarStyle = SystemBarStyle.auto(
            android.graphics.Color.TRANSPARENT, android.graphics.Color.TRANSPARENT
        ))
        installKeyboardAwareNavigationBarPolicy()
        applyImmersiveUi()
        waitForPreparedFirstFrame()
    }

    protected fun setPdfContent(content: @Composable (AppSettings) -> Unit) {
        setContent {
            val repository = remember { SettingsRepository(applicationContext) }
            val settingsState by repository.settings.collectAsStateWithLifecycle<AppSettings?>(initialValue = launchSettings)
            val settings = settingsState ?: return@setContent
            val motionSettings = remember { intent.pdfScreenMotionOr(settings) }
            activeMotionSettings = motionSettings
            val effectiveDark = settings.effectiveDarkTheme()
            SyncDisplayPerformance(window = window, performanceMode = settings.performanceMode)
            SyncUiFeedback(context = this@ImmersivePdfActivity, settings = settings)
            MyNotesTheme(settings = settings, darkTheme = effectiveDark) {
                // AppBackgroundProvider ya pinta el fondo fuera de la transición.
                val windowColor = MaterialTheme.colorScheme.background.toArgb()
                SideEffect { prepareFirstPdfFrame(windowColor) }
                PdfScreenTransition(
                    visibility = screenVisibility,
                    motionSettings = motionSettings,
                    closing = finishingWithMotion,
                    onExitFinished = ::finishImmediately
                ) {
                    content(settings)
                }
            }
        }
    }

    protected fun finishWithPdfMotion() {
        if (finishingWithMotion || isFinishing) return
        if (!firstFrameReady || !activeMotionSettings.animationsEnabled) {
            finishImmediately()
            return
        }
        finishingWithMotion = true
        screenVisibility.targetState = false
    }

    private fun finishImmediately() {
        if (isFinishing) return
        super.finish()
        suppressPendingActivityAnimation()
    }

    private fun waitForPreparedFirstFrame() {
        val decor = window.decorView
        val listener = object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                if (!firstFrameReady) return false
                if (decor.viewTreeObserver.isAlive) decor.viewTreeObserver.removeOnPreDrawListener(this)
                firstDrawListener = null
                return true
            }
        }
        firstDrawListener = listener
        decor.viewTreeObserver.addOnPreDrawListener(listener)
    }

    private fun prepareFirstPdfFrame(color: Int) {
        if (appliedWindowColor != color) {
            window.setBackgroundDrawable(ColorDrawable(color))
            appliedWindowColor = color
        }
        if (!firstFrameReady) {
            firstFrameReady = true
            window.decorView.invalidate()
        }
    }

    override fun onResume() {
        super.onResume()
        DisplayPerformanceController.reapplyLastRequest(window)
        applyImmersiveUi()
    }
    private fun applyImmersiveUi() {
        window.applyAndroidNavigationBarPolicy()
        window.decorView.post { window.applyAndroidNavigationBarPolicy() }
    }

    override fun onMultiWindowModeChanged(isInMultiWindowMode: Boolean) {
        super.onMultiWindowModeChanged(isInMultiWindowMode)
        window.applyAndroidNavigationBarPolicy()
    }

    override fun onDestroy() {
        firstDrawListener?.let { listener ->
            val observer = window.decorView.viewTreeObserver
            if (observer.isAlive) observer.removeOnPreDrawListener(listener)
        }
        firstDrawListener = null
        DisplayPerformanceController.release(window)
        super.onDestroy()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) window.applyAndroidNavigationBarPolicy()
    }
}

package com.example.mynotes.ui.pdf

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mynotes.R
import com.example.mynotes.SyncDisplayPerformance
import com.example.mynotes.SyncUiFeedback
import com.example.mynotes.hideAndroidNavigationBar
import com.example.mynotes.performance.DisplayPerformanceController
import com.example.mynotes.settings.AppSettings
import com.example.mynotes.settings.SettingsRepository
import com.example.mynotes.ui.motion.AppMotion
import com.example.mynotes.ui.motion.ConfigurableAnimatedContent
import com.example.mynotes.ui.theme.MyNotesTheme
import com.example.mynotes.ui.theme.effectiveDarkTheme
import com.example.mynotes.withSavedAppLocale
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope

abstract class ImmersivePdfActivity : ComponentActivity() {
    private val screenVisible = mutableStateOf(false)
    private var latestSettings = AppSettings()
    private var activeMotionSettings = AppSettings()
    private var finishingWithMotion = false

    override fun attachBaseContext(newBase: Context) = super.attachBaseContext(newBase.withSavedAppLocale())

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_MyNotes)
        super.onCreate(savedInstanceState)
        suppressPendingActivityAnimation()
        enableEdgeToEdge()
        applyImmersiveUi()
    }

    protected fun setPdfContent(content: @Composable (AppSettings) -> Unit) {
        setContent {
            val repository = remember { SettingsRepository(applicationContext) }
            val settings by repository.settings.collectAsStateWithLifecycle(initialValue = AppSettings())
            latestSettings = settings
            val motionSettings = remember { intent.pdfScreenMotionOr(settings) }
            activeMotionSettings = motionSettings
            val effectiveDark = settings.effectiveDarkTheme(isSystemInDarkTheme())
            SyncDisplayPerformance(window = window, performanceMode = settings.performanceMode)
            SyncUiFeedback(context = this@ImmersivePdfActivity, settings = settings)
            MyNotesTheme(settings = settings, darkTheme = effectiveDark) {
                LaunchedEffect(Unit) { screenVisible.value = true }
                ConfigurableAnimatedContent(
                    targetState = screenVisible.value,
                    animationsEnabled = motionSettings.animationsEnabled,
                    animationSpeed = motionSettings.animationSpeed,
                    animationStyle = motionSettings.animationStyle,
                    animationEasing = motionSettings.animationEasing,
                    animationIntensity = motionSettings.animationIntensity,
                    performanceMode = motionSettings.performanceMode
                ) { visible ->
                    if (visible) content(settings)
                }
            }
        }
    }

    protected fun finishWithPdfMotion() {
        if (finishingWithMotion) return
        if (!screenVisible.value || !activeMotionSettings.animationsEnabled) {
            finishImmediately()
            return
        }
        finishingWithMotion = true
        screenVisible.value = false
        val delayMillis = AppMotion.screenExitDuration(
            animationsEnabled = activeMotionSettings.animationsEnabled,
            animationSpeed = activeMotionSettings.animationSpeed,
            performanceMode = activeMotionSettings.performanceMode
        )
        lifecycleScope.launch {
            delay(delayMillis.toLong() + 16L)
            finishImmediately()
        }
    }

    private fun finishImmediately() {
        super.finish()
        suppressPendingActivityAnimation()
    }

    override fun onResume() {
        super.onResume()
        DisplayPerformanceController.reapplyLastRequest(window)
        applyImmersiveUi()
    }
    private fun applyImmersiveUi() { hideAndroidNavigationBar(); window.decorView.post { hideAndroidNavigationBar() } }

    override fun onDestroy() { DisplayPerformanceController.release(window); super.onDestroy() }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideAndroidNavigationBar()
    }
}

package com.example.mynotes.ui.pdf

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mynotes.R
import com.example.mynotes.SyncDisplayPerformance
import com.example.mynotes.SyncUiFeedback
import com.example.mynotes.hideAndroidNavigationBar
import com.example.mynotes.performance.DisplayPerformanceController
import com.example.mynotes.settings.AppSettings
import com.example.mynotes.settings.SettingsRepository
import com.example.mynotes.ui.theme.MyNotesTheme
import com.example.mynotes.ui.theme.effectiveDarkTheme
import com.example.mynotes.withSavedAppLocale

abstract class ImmersivePdfActivity : ComponentActivity() {
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(newBase.withSavedAppLocale())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_MyNotes)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        hideAndroidNavigationBar()
        window.decorView.post { hideAndroidNavigationBar() }
    }

    protected fun setPdfContent(content: @Composable (AppSettings) -> Unit) {
        setContent {
            val repository = remember { SettingsRepository(applicationContext) }
            val settings by repository.settings.collectAsStateWithLifecycle(initialValue = AppSettings())
            val effectiveDark = settings.effectiveDarkTheme(isSystemInDarkTheme())
            SyncDisplayPerformance(window = window, performanceMode = settings.performanceMode)
            SyncUiFeedback(context = this@ImmersivePdfActivity, settings = settings)
            MyNotesTheme(settings = settings, darkTheme = effectiveDark) { content(settings) }
        }
    }

    override fun onResume() {
        super.onResume()
        DisplayPerformanceController.reapplyLastRequest(window)
        hideAndroidNavigationBar()
        window.decorView.post { hideAndroidNavigationBar() }
    }

    override fun onDestroy() {
        DisplayPerformanceController.release(window)
        super.onDestroy()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideAndroidNavigationBar()
    }
}

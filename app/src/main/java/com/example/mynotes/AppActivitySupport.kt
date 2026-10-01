package com.example.mynotes

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.mynotes.performance.DisplayPerformanceController
import com.example.mynotes.settings.AppSettings
import com.example.mynotes.ui.sound.UiSoundPlayer
import java.util.Locale

private const val LOCALE_PREFS = "locale_prefs"
private const val LANGUAGE_KEY = "language"
private const val DEFAULT_LANGUAGE = "system"

internal fun Context.withSavedAppLocale(): Context {
    val language = getSharedPreferences(LOCALE_PREFS, Context.MODE_PRIVATE)
        .getString(LANGUAGE_KEY, DEFAULT_LANGUAGE) ?: DEFAULT_LANGUAGE
    if (language == DEFAULT_LANGUAGE) return this
    val locale = Locale.forLanguageTag(language)
    Locale.setDefault(locale)
    return createConfigurationContext(Configuration(resources.configuration).apply { setLocale(locale) })
}

internal fun Context.saveAppLanguage(language: String) {
    getSharedPreferences(LOCALE_PREFS, Context.MODE_PRIVATE).edit().putString(LANGUAGE_KEY, language).commit()
}

@Composable
internal fun SyncDisplayPerformance(window: Window, performanceMode: String) {
    LaunchedEffect(performanceMode) {
        DisplayPerformanceController.requestForPerformanceMode(window = window, performanceMode = performanceMode)
    }
}

@Composable
internal fun SyncUiFeedback(context: Context, settings: AppSettings) {
    LaunchedEffect(settings.soundEffectsEnabled, settings.soundEffectsVolume, settings.soundEffectsTheme,
        settings.hapticEffectsEnabled, settings.hapticEffectsIntensity, settings.hapticEffectsStyle) {
        UiSoundPlayer.configure(context = context, enabled = settings.soundEffectsEnabled,
            volumePercent = settings.soundEffectsVolume, theme = settings.soundEffectsTheme,
            hapticEnabled = settings.hapticEffectsEnabled, hapticIntensityPercent = settings.hapticEffectsIntensity,
            hapticStyle = settings.hapticEffectsStyle)
    }
}

internal fun ComponentActivity.applyAndroidNavigationBarPolicy() {
    val controller = WindowCompat.getInsetsController(window, window.decorView)
    val isMultiWindow = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && isInMultiWindowMode
    if (isMultiWindow) controller.show(WindowInsetsCompat.Type.navigationBars()) else {
        controller.hide(WindowInsetsCompat.Type.navigationBars())
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
        @Suppress("DEPRECATION")
        window.navigationBarColor = Color.BLACK
    }
}

internal fun ComponentActivity.applySystemBarAppearance(darkMode: Boolean) {
    val controller = WindowCompat.getInsetsController(window, window.decorView)
    controller.isAppearanceLightStatusBars = !darkMode
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) controller.isAppearanceLightNavigationBars = !darkMode
}

internal fun ComponentActivity.hideAndroidNavigationBar() {
    val decorView = window.decorView
    WindowCompat.getInsetsController(window, decorView).run {
        systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        hide(WindowInsetsCompat.Type.navigationBars())
    }
    @Suppress("DEPRECATION")
    decorView.systemUiVisibility = decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
        @Suppress("DEPRECATION")
        window.navigationBarColor = Color.BLACK
    }
}

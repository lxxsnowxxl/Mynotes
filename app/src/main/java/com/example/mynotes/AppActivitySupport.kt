package com.example.mynotes

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.core.graphics.ColorUtils
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.example.mynotes.performance.DisplayPerformanceController
import com.example.mynotes.settings.AppSettings
import com.example.mynotes.ui.theme.PaletteCatalog
import com.example.mynotes.ui.theme.effectiveDarkTheme
import androidx.compose.ui.graphics.toArgb
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
    LaunchedEffect(settings.configurationMode, settings.soundEffectsEnabled, settings.soundEffectsVolume, settings.soundEffectsTheme,
        settings.hapticEffectsEnabled, settings.hapticEffectsIntensity, settings.hapticEffectsStyle) {
        // Wait for persisted preferences instead of preloading the temporary default theme too.
        if (settings.configurationMode == "loading") return@LaunchedEffect
        UiSoundPlayer.configure(context = context, enabled = settings.soundEffectsEnabled,
            volumePercent = settings.soundEffectsVolume, theme = settings.soundEffectsTheme,
            hapticEnabled = settings.hapticEffectsEnabled, hapticIntensityPercent = settings.hapticEffectsIntensity,
            hapticStyle = settings.hapticEffectsStyle)
    }
}

internal fun ComponentActivity.installKeyboardAwareNavigationBarPolicy(
    onImeVisibilityChanged: ((Boolean) -> Unit)? = null
) {
    val stopObserving = window.observeAndroidNavigationBarIme(onImeVisibilityChanged)
    lifecycle.addObserver(object : DefaultLifecycleObserver {
        override fun onDestroy(owner: LifecycleOwner) {
            stopObserving()
            owner.lifecycle.removeObserver(this)
        }
    })
}

/** Observe keyboard changes without consuming the insets used by the content. */
internal fun Window.observeAndroidNavigationBarIme(
    onImeVisibilityChanged: ((Boolean) -> Unit)? = null
): () -> Unit {
    val decor = decorView
    var keyboardVisible: Boolean? = null
    val applyVisibility = Runnable { applyAndroidNavigationBarPolicy(keyboardVisible) }
    ViewCompat.setOnApplyWindowInsetsListener(decor) { view, insets ->
        val visible = insets.isVisible(WindowInsetsCompat.Type.ime())
        if (visible != keyboardVisible) {
            keyboardVisible = visible
            onImeVisibilityChanged?.invoke(visible)
            // Applying visibility can dispatch new insets. Coalesce updates outside this dispatch.
            view.removeCallbacks(applyVisibility)
            view.post(applyVisibility)
        }
        insets
    }
    ViewCompat.requestApplyInsets(decor)
    return {
        decor.removeCallbacks(applyVisibility)
        ViewCompat.setOnApplyWindowInsetsListener(decor, null)
    }
}

internal fun ComponentActivity.applySystemBarAppearance(darkMode: Boolean) {
    val controller = WindowCompat.getInsetsController(window, window.decorView)
    controller.isAppearanceLightStatusBars = !darkMode
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) controller.isAppearanceLightNavigationBars = !darkMode
}

/**
 * Los iconos de las barras del sistema dependen del TONO REAL de la pantalla,
 * no sólo del interruptor modo oscuro. Por ejemplo, el quinto tono azul
 * necesita iconos claros aunque la configuración global sea "clara".
 */
internal fun ComponentActivity.applySystemBarAppearance(settings: AppSettings) {
    val selected = if (settings.oledMode && settings.effectiveDarkTheme()) {
        Color.BLACK
    } else {
        PaletteCatalog.find(settings.backgroundColor).toneAt(settings.backgroundToneIndex).toArgb()
    }
    val darkIcons = ColorUtils.calculateContrast(Color.BLACK, selected) >=
        ColorUtils.calculateContrast(Color.WHITE, selected)
    val controller = WindowCompat.getInsetsController(window, window.decorView)
    controller.isAppearanceLightStatusBars = darkIcons
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        controller.isAppearanceLightNavigationBars = darkIcons
    }
}

@Suppress("DEPRECATION")
internal fun Window.applyAndroidNavigationBarPolicy(imeVisible: Boolean? = null) {
    val decor = decorView
    val keyboardVisible = imeVisible ?: (ViewCompat.getRootWindowInsets(decor)
        ?.isVisible(WindowInsetsCompat.Type.ime()) == true)

    makeAndroidNavigationBarTransparent()
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
        // Keep edge-to-edge layout when show() clears the legacy hide-navigation flag.
        decor.systemUiVisibility = decor.systemUiVisibility or
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    }
    val controller = WindowCompat.getInsetsController(this, decor)
    controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    if (keyboardVisible) {
        controller.show(WindowInsetsCompat.Type.navigationBars())
    } else {
        controller.hide(WindowInsetsCompat.Type.navigationBars())
    }
}

/** Limpia el fondo de navegación tanto de actividades como de diálogos. */
@Suppress("DEPRECATION")
private fun Window.makeAndroidNavigationBarTransparent() {
    clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
    addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    navigationBarColor = Color.TRANSPARENT
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        navigationBarDividerColor = Color.TRANSPARENT
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        isNavigationBarContrastEnforced = false
    }
}

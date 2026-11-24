package com.example.mynotes.ui.pdf

import android.app.Activity
import android.content.Intent
import com.example.mynotes.settings.AppSettings
import java.util.UUID

private const val EXTRA_MOTION_ENABLED = "mynotes_pdf_motion_enabled"
private const val EXTRA_MOTION_STYLE = "mynotes_pdf_motion_style"
private const val EXTRA_MOTION_EASING = "mynotes_pdf_motion_easing"
private const val EXTRA_MOTION_SPEED = "mynotes_pdf_motion_speed"
private const val EXTRA_MOTION_INTENSITY = "mynotes_pdf_motion_intensity"
private const val EXTRA_MOTION_PERFORMANCE = "mynotes_pdf_motion_performance"
private const val EXTRA_LAUNCH_SETTINGS_ID = "mynotes_pdf_launch_settings_id"

private data class PdfLaunchSettings(val id: String, val settings: AppSettings)

// Una sola instantánea inmutable; no conserva Context, Activity ni imágenes.
@Volatile private var pendingLaunchSettings: PdfLaunchSettings? = null

/** Prepara el tema completo del primer fotograma y las opciones de movimiento. */
fun Intent.withPdfScreenMotion(settings: AppSettings): Intent = apply {
    val launch = PdfLaunchSettings(UUID.randomUUID().toString(), settings)
    pendingLaunchSettings = launch
    putExtra(EXTRA_LAUNCH_SETTINGS_ID, launch.id)
    putExtra(EXTRA_MOTION_ENABLED, settings.animationsEnabled)
    putExtra(EXTRA_MOTION_STYLE, settings.animationStyle)
    putExtra(EXTRA_MOTION_EASING, settings.animationEasing)
    putExtra(EXTRA_MOTION_SPEED, settings.animationSpeed)
    putExtra(EXTRA_MOTION_INTENSITY, settings.animationIntensity)
    putExtra(EXTRA_MOTION_PERFORMANCE, settings.performanceMode)
}

/** Si el proceso se recreó o la instantánea expiró, esperar a DataStore sin dibujar valores claros provisionales. */
internal fun Intent.pdfLaunchSettingsOrNull(): AppSettings? {
    val id = getStringExtra(EXTRA_LAUNCH_SETTINGS_ID) ?: return null
    return pendingLaunchSettings?.takeIf { it.id == id }?.settings
}

internal fun Intent.pdfScreenMotionOr(fallback: AppSettings): AppSettings {
    if (!hasExtra(EXTRA_MOTION_STYLE)) return fallback
    return fallback.copy(
        animationsEnabled = getBooleanExtra(EXTRA_MOTION_ENABLED, fallback.animationsEnabled),
        animationStyle = getStringExtra(EXTRA_MOTION_STYLE) ?: fallback.animationStyle,
        animationEasing = getStringExtra(EXTRA_MOTION_EASING) ?: fallback.animationEasing,
        animationSpeed = getFloatExtra(EXTRA_MOTION_SPEED, fallback.animationSpeed),
        animationIntensity = getFloatExtra(EXTRA_MOTION_INTENSITY, fallback.animationIntensity),
        performanceMode = getStringExtra(EXTRA_MOTION_PERFORMANCE) ?: fallback.performanceMode
    )
}

@Suppress("DEPRECATION")
fun Activity.suppressPendingActivityAnimation() {
    overridePendingTransition(0, 0)
}

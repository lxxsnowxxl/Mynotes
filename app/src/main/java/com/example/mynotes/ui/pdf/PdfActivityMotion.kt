package com.example.mynotes.ui.pdf

import android.app.Activity
import android.content.Intent
import com.example.mynotes.settings.AppSettings

private const val EXTRA_MOTION_ENABLED = "mynotes_pdf_motion_enabled"
private const val EXTRA_MOTION_STYLE = "mynotes_pdf_motion_style"
private const val EXTRA_MOTION_EASING = "mynotes_pdf_motion_easing"
private const val EXTRA_MOTION_SPEED = "mynotes_pdf_motion_speed"
private const val EXTRA_MOTION_INTENSITY = "mynotes_pdf_motion_intensity"
private const val EXTRA_MOTION_PERFORMANCE = "mynotes_pdf_motion_performance"

/** Carries the already-resolved screen motion settings across Activity boundaries. */
fun Intent.withPdfScreenMotion(settings: AppSettings): Intent = apply {
    putExtra(EXTRA_MOTION_ENABLED, settings.animationsEnabled)
    putExtra(EXTRA_MOTION_STYLE, settings.animationStyle)
    putExtra(EXTRA_MOTION_EASING, settings.animationEasing)
    putExtra(EXTRA_MOTION_SPEED, settings.animationSpeed)
    putExtra(EXTRA_MOTION_INTENSITY, settings.animationIntensity)
    putExtra(EXTRA_MOTION_PERFORMANCE, settings.performanceMode)
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

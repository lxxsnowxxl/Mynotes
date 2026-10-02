package com.example.mynotes.performance

import android.os.Build
import android.view.Display
import android.view.Window
import com.example.mynotes.ui.motion.AppMotion
import java.util.WeakHashMap
import kotlin.math.abs

/** Centralizes the preferred display refresh-rate policy for every Activity. */
object DisplayPerformanceController {
    private const val STANDARD_REFRESH_RATE = 60f
    private const val QUALITY_REFRESH_RATE = 120f
    private const val REFRESH_RATE_TOLERANCE = 0.5f
    private val lastRequestedMode = WeakHashMap<Window, String>()

    fun requestForPerformanceMode(window: Window, performanceMode: String) {
        val mode = normalizeMode(performanceMode)
        if (lastRequestedMode[window] == mode) return
        lastRequestedMode[window] = mode
        requestRefreshRate(window, refreshRateFor(mode))
    }

    fun reapplyLastRequest(window: Window) {
        val mode = lastRequestedMode[window] ?: return
        requestRefreshRate(window, refreshRateFor(mode))
    }

    fun release(window: Window) {
        lastRequestedMode.remove(window)
    }

    private fun normalizeMode(value: String): String = when (value) {
        "performance", "balanced", "quality" -> value
        else -> AppMotion.normalizePerformanceMode(value.trim().lowercase())
    }

    private fun refreshRateFor(mode: String): Float = AppMotion.performanceValue(
        mode, STANDARD_REFRESH_RATE, STANDARD_REFRESH_RATE, QUALITY_REFRESH_RATE, STANDARD_REFRESH_RATE
    )

    private fun requestRefreshRate(window: Window, targetRefreshRate: Float) {
        val attributes = window.attributes
        attributes.preferredRefreshRate = targetRefreshRate
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            attributes.preferredDisplayModeId = 0
            val display = window.decorView.display
            val currentMode = display?.mode
            val supportedModes = display?.supportedModes
            if (currentMode != null && !supportedModes.isNullOrEmpty()) {
                chooseClosestCompatibleMode(
                    supportedModes,
                    currentMode.physicalWidth,
                    currentMode.physicalHeight,
                    targetRefreshRate
                )?.let { bestMode ->
                    attributes.preferredDisplayModeId = bestMode.modeId
                    attributes.preferredRefreshRate = bestMode.refreshRate
                }
            }
        }
        window.attributes = attributes
    }

    private fun chooseClosestCompatibleMode(
        modes: Array<out Display.Mode>,
        physicalWidth: Int,
        physicalHeight: Int,
        targetRefreshRate: Float
    ): Display.Mode? {
        var best: Display.Mode? = null
        var bestDistance = Float.POSITIVE_INFINITY
        for (mode in modes) {
            if (mode.physicalWidth != physicalWidth || mode.physicalHeight != physicalHeight) continue
            if (mode.refreshRate > targetRefreshRate + REFRESH_RATE_TOLERANCE) continue
            val distance = abs(mode.refreshRate - targetRefreshRate)
            val currentBest = best
            if (currentBest == null || distance < bestDistance ||
                distance == bestDistance && mode.refreshRate > currentBest.refreshRate
            ) {
                best = mode
                bestDistance = distance
            }
        }
        return best
    }
}

package com.example.mynotes.performance

import android.os.Build
import android.view.Display
import android.view.Window
import java.util.WeakHashMap
import kotlin.math.abs

/**
 * Único punto de control para la frecuencia de refresco preferida de MyNotes.
 *
 * IMPORTANTE:
 * - Esto controla Hz / modo de pantalla, no un límite de FPS del renderizador.
 * - Jetpack Compose sincroniza el dibujo con VSYNC mediante Choreographer;
 *   no hace falta activar VSYNC manualmente desde MainActivity.
 *
 * Perfiles actuales:
 * - performance: 60 Hz
 * - balanced:    60 Hz
 * - quality:     hasta 120 Hz cuando el panel lo soporta
 *
 * Si la frecuencia exacta no está disponible, se elige el modo compatible
 * más cercano sin cambiar voluntariamente la resolución física actual.
 */
object DisplayPerformanceController {
    /*
     * Conservamos la última solicitud por Window para poder reaplicarla al
     * volver a primer plano sin duplicar reglas de Hz en las Activities.
     * WeakHashMap evita retener una Activity/Window destruida.
     */
    private val lastRequestedMode = WeakHashMap<Window, String>()
    fun requestForPerformanceMode(window: Window, performanceMode: String) {
        val normalizedMode = normalizePerformanceMode(performanceMode)
        /*
         * DataStore/Compose puede volver a entregar el mismo perfil durante
         * recreaciones internas. Si esta misma Window ya tiene exactamente la
         * misma solicitud no reescribimos LayoutParams ni recorremos de nuevo
         * todos los modos de pantalla soportados.
         *
         * reapplyLastRequest() sigue forzando la reaplicación al volver a
         * primer plano, por lo que no perdemos la recuperación frente a OEMs.
         */
        if (lastRequestedMode[window] == normalizedMode) return
        lastRequestedMode[window] = normalizedMode
        requestRefreshRate(window = window, targetRefreshRate = refreshRateFor(normalizedMode))
    }
    /**
     * Reaplica la última preferencia conocida para esta ventana.
     * Si todavía no se ha recibido AppSettings, no fuerza ningún Hz.
     */
    fun reapplyLastRequest(window: Window) {
        val mode = lastRequestedMode[window]?: return
        requestRefreshRate(window = window, targetRefreshRate = refreshRateFor(mode))
    }
    /**
     * Libera la referencia lógica cuando una Activity termina.
     * La clave es débil, pero retirarla explícitamente hace la intención clara.
     */
    fun release(window: Window) {
        lastRequestedMode.remove(window)
    }
    private fun refreshRateFor(performanceMode: String): Float = when (performanceMode) {
            MODE_QUALITY -> QUALITY_REFRESH_RATE
            MODE_PERFORMANCE, MODE_BALANCED -> STANDARD_REFRESH_RATE
            else -> STANDARD_REFRESH_RATE
        }
    private fun normalizePerformanceMode(value: String): String = when (value.trim().lowercase()) {
            MODE_PERFORMANCE -> MODE_PERFORMANCE
            MODE_QUALITY -> MODE_QUALITY
            else -> MODE_BALANCED
        }
    private fun requestRefreshRate(window: Window, targetRefreshRate: Float) {
        val attributes = window.attributes
        /*
         * preferredRefreshRate existe desde API 21. Android puede ignorar
         * la preferencia si el panel, compositor o estado actual no permiten
         * esa frecuencia.
         */
        attributes.preferredRefreshRate = targetRefreshRate
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val display = window.decorView.display
            val currentMode = display?.mode
            val supportedModes = display?.supportedModes?.toList().orEmpty()
            if (supportedModes.isNotEmpty()) {
                /*
                 * Primero mantenemos la resolución física actual. Solo si no
                 * existe ningún modo equivalente consideramos todos los modos.
                 */
                val sameResolutionModes = if (currentMode != null) {
                        supportedModes.filter { mode -> mode.physicalWidth == currentMode.physicalWidth &&
                                mode.physicalHeight == currentMode.physicalHeight
                        }
                    } else {
                        emptyList()
                    }
                val resolutionCandidates = sameResolutionModes.ifEmpty {
                        supportedModes
                    }
                /*
                 * En quality pedimos "hasta 120 Hz". Si el panel solo ofrece
                 * 144/165 Hz por encima del objetivo, preferimos el mejor modo
                 * disponible <= 120 Hz antes de forzar una tasa superior.
                 */
                val candidates = resolutionCandidates.filter { mode -> mode.refreshRate <= targetRefreshRate + REFRESH_RATE_TOLERANCE
                        }.ifEmpty {
                            resolutionCandidates
                        }
                val bestMode = chooseClosestMode(modes = candidates, targetRefreshRate = targetRefreshRate)
                if (bestMode != null) {
                    attributes.preferredDisplayModeId = bestMode.modeId
                    attributes.preferredRefreshRate = bestMode.refreshRate
                }
            }
        }
        window.attributes = attributes
    }
    private fun chooseClosestMode(modes: List<Display.Mode>, targetRefreshRate: Float): Display.Mode? = modes.minWithOrNull(
            compareBy<Display.Mode> {
                abs(it.refreshRate - targetRefreshRate)
            }.thenByDescending {
                it.refreshRate
            })
    private const val MODE_PERFORMANCE = "performance"
    private const val MODE_BALANCED = "balanced"
    private const val MODE_QUALITY = "quality"
    private const val STANDARD_REFRESH_RATE = 60f
    private const val QUALITY_REFRESH_RATE = 120f
    private const val REFRESH_RATE_TOLERANCE = 0.5f
}

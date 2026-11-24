package com.example.mynotes.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.FloatState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToLong
import kotlin.math.sin

internal data class BackgroundMotionProfile(val frameDelayMillis: Long, val cycleMillis: Long)

internal fun backgroundMotionProfile(performanceMode: String, animationSpeed: Float): BackgroundMotionProfile {
    val speed = if (animationSpeed.isFinite()) animationSpeed.coerceIn(0.5f, 2f) else 1f
    val (delay, cycle) = when (performanceMode) {
        "performance" -> 100L to 14_000L
        "quality" -> 34L to 10_000L
        else -> 50L to 12_000L
    }
    return BackgroundMotionProfile(delay, (cycle / speed).roundToLong())
}

internal fun backgroundMotionEnabled(style: String, animationsEnabled: Boolean, consumers: Int): Boolean =
    (style == "animated_blur") && animationsEnabled && consumers > 0

/** Trayectorias cerradas y suaves, expresadas como fracciones del tamaño de referencia. */
internal fun settingsBlurMotionOffset(index: Int, phase: Float): Offset {
    val safePhase = if (phase.isFinite()) phase else 0f
    val angle = safePhase * (2.0 * PI) + index * (2.0 * PI / 3.0)
    return Offset((sin(angle) * 0.25).toFloat(), (cos(angle) * 0.20).toFloat())
}

@Stable
private class BackgroundMotionClock {
    val phase = mutableFloatStateOf(0f)
    var consumers by mutableIntStateOf(0)
        private set

    fun attach() { consumers++ }
    fun detach() { consumers = (consumers - 1).coerceAtLeast(0) }
}

private val LocalBackgroundMotionClock = compositionLocalOf<BackgroundMotionClock?> { null }

/** Un único reloj por tema/actividad; se ejecuta sólo cuando hay un fondo que lo necesita. */
@Composable
internal fun BackgroundMotionProvider(
    style: String, animationsEnabled: Boolean, performanceMode: String, animationSpeed: Float,
    content: @Composable () -> Unit
) {
    val clock = remember { BackgroundMotionClock() }
    val inspectionMode = LocalInspectionMode.current
    val profile = remember(performanceMode, animationSpeed) { backgroundMotionProfile(performanceMode, animationSpeed) }
    val enabled = !inspectionMode && backgroundMotionEnabled(style, animationsEnabled, clock.consumers)
    val lifecycleOwner = if (enabled) LocalLifecycleOwner.current else null
    LaunchedEffect(lifecycleOwner, enabled, profile) {
        if (!enabled || lifecycleOwner == null) return@LaunchedEffect
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            var previousTick = System.nanoTime()
            while (isActive) {
                // El temporizador solicita el siguiente dibujo incluso con la pantalla en reposo.
                delay(profile.frameDelayMillis)
                val now = System.nanoTime()
                val elapsed = (now - previousTick).coerceIn(0L, 250_000_000L)
                val delta = elapsed.toDouble() / (profile.cycleMillis * 1_000_000.0)
                clock.phase.floatValue = ((clock.phase.floatValue + delta) % 1.0).toFloat()
                previousTick = now
            }
        }
    }
    CompositionLocalProvider(LocalBackgroundMotionClock provides clock, content = content)
}

/** Registrar consumidores no lee la fase: los frames invalidan sólo el dibujo del fondo. */
@Composable
internal fun rememberBackgroundMotionPhase(active: Boolean, paused: Boolean = false): FloatState? {
    val clock = LocalBackgroundMotionClock.current
    if (!active || clock == null) return null
    if (paused) return remember(clock) {
        // Mantener la posición actual sin observar cada fotograma de otros consumidores.
        mutableFloatStateOf(Snapshot.withoutReadObservation { clock.phase.floatValue })
    }
    DisposableEffect(clock) {
        clock.attach()
        onDispose { clock.detach() }
    }
    return clock.phase
}

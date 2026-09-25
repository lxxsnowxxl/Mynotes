package com.example.mynotes.ui.sound

import android.content.Context
import android.os.Build
import android.os.SystemClock
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.example.mynotes.settings.FeedbackPreferencePolicy
import java.util.EnumMap
import kotlin.math.roundToInt

/**
 * Motor centralizado de respuesta háptica para la interfaz.
 *
 * Los estilos se generan con patrones cortos de vibración. La intensidad se
 * controla desde Configuración y cada acción ajusta ligeramente la fuerza para
 * que un slider no se sienta igual que borrar, guardar o confirmar.
 */
enum class UiHaptic {
    Tap, Tick, ToggleOn, ToggleOff, Confirm, Warning, Attachment, Selection
}

object UiHapticPlayer {
    const val DEFAULT_STYLE = FeedbackPreferencePolicy.DEFAULT_HAPTIC_STYLE
    val availableStyles: List<String> = FeedbackPreferencePolicy.hapticStyles
    @Volatile
    private var enabled: Boolean = true
    @Volatile
    private var intensity: Float = 0.55f
    @Volatile
    private var style: String = DEFAULT_STYLE
    private val lastPlayAt = EnumMap<UiHaptic, Long>(UiHaptic::class.java)
    fun normalizeStyle(value: String): String = FeedbackPreferencePolicy.normalizeHapticStyle(value)
    fun configure(enabled: Boolean, intensityPercent: Float, style: String = DEFAULT_STYLE) {
        this.enabled = enabled
        this.intensity = (intensityPercent / 100f).coerceIn(0f, 1f)
        this.style = normalizeStyle(style)
    }
    fun play(context: Context, haptic: UiHaptic, force: Boolean = false) {
        if ((!enabled && !force) || intensity <= 0f) {
            return
        }
        vibrate(context = context, haptic = haptic, style = style, intensity = intensity)
    }
    fun playThrottled(context: Context, haptic: UiHaptic, minimumIntervalMs: Long = 45L, force: Boolean = false) {
        if ((!enabled && !force) || intensity <= 0f) {
            return
        }
        if (!acquireThrottleSlot(haptic = haptic, minimumIntervalMs = minimumIntervalMs)) {
            return
        }
        play(context = context, haptic = haptic, force = force)
    }
    fun playToggle(context: Context, checked: Boolean, force: Boolean = false) {
        play(context = context, haptic = if (checked) UiHaptic.ToggleOn else UiHaptic.ToggleOff, force = force)
    }
    fun previewStyle(context: Context, style: String, intensityPercent: Float = 55f, haptic: UiHaptic = UiHaptic.Confirm) {
        vibrate(context = context, haptic = haptic, style = normalizeStyle(style), intensity = (intensityPercent / 100f).coerceIn(0f, 1f))
    }
    fun playForSound(context: Context, sound: UiSound, throttled: Boolean = false, minimumIntervalMs: Long = 45L) {
        val haptic = when (sound) {
                UiSound.Edit -> UiHaptic.Tap
                UiSound.Delete -> UiHaptic.Warning
                UiSound.Priority -> UiHaptic.Selection
                UiSound.SliderTick -> UiHaptic.Tick
                UiSound.Attachment -> UiHaptic.Attachment
                UiSound.Toggle -> UiHaptic.Selection
            }
        if (throttled) {
            playThrottled(context = context, haptic = haptic, minimumIntervalMs = minimumIntervalMs)
        } else {
            play(context = context, haptic = haptic)
        }
    }
    fun playForAction(context: Context, action: UiActionSound, throttled: Boolean = false, minimumIntervalMs: Long = 45L) {
        // El mapeo acción -> háptico es constante y se crea una sola vez.
        val haptic = actionHaptics[action] ?: return
        if (throttled) {
            playThrottled(context = context, haptic = haptic, minimumIntervalMs = minimumIntervalMs)
        } else {
            play(context = context, haptic = haptic)
        }
    }

    private val actionHaptics = EnumMap<UiActionSound, UiHaptic>(UiActionSound::class.java).apply {
        put(UiActionSound.Open, UiHaptic.Tap)
        put(UiActionSound.Back, UiHaptic.Selection)
        put(UiActionSound.Save, UiHaptic.Confirm)
        put(UiActionSound.Search, UiHaptic.Tick)
        // TextInput se omite deliberadamente: escribir caracteres no vibra.
        put(UiActionSound.Menu, UiHaptic.Selection)
        put(UiActionSound.Select, UiHaptic.Selection)
        put(UiActionSound.Favorite, UiHaptic.Confirm)
        put(UiActionSound.Pin, UiHaptic.Selection)
        put(UiActionSound.Share, UiHaptic.Tap)
        put(UiActionSound.Move, UiHaptic.Selection)
        put(UiActionSound.Color, UiHaptic.Selection)
        put(UiActionSound.Category, UiHaptic.Selection)
        put(UiActionSound.Add, UiHaptic.Attachment)
        put(UiActionSound.Confirm, UiHaptic.Confirm)
        put(UiActionSound.Cancel, UiHaptic.Selection)
        put(UiActionSound.Navigation, UiHaptic.Selection)
        put(UiActionSound.Sort, UiHaptic.Tick)
        put(UiActionSound.Layout, UiHaptic.Selection)
        put(UiActionSound.Language, UiHaptic.Selection)
        put(UiActionSound.Theme, UiHaptic.Selection)
        put(UiActionSound.Link, UiHaptic.Tap)
        put(UiActionSound.PlayPause, UiHaptic.Selection)
        put(UiActionSound.Zoom, UiHaptic.Tick)
        put(UiActionSound.Backup, UiHaptic.Attachment)
        put(UiActionSound.Restore, UiHaptic.Confirm)
        put(UiActionSound.Settings, UiHaptic.Selection)
    }

    private fun acquireThrottleSlot(haptic: UiHaptic, minimumIntervalMs: Long): Boolean {
        val now = SystemClock.uptimeMillis()
        return synchronized(lastPlayAt) {
            val previous = lastPlayAt[haptic] ?: 0L
            if (now - previous < minimumIntervalMs) {
                false
            } else {
                lastPlayAt[haptic] = now
                true
            }
        }
    }

    private data class HapticPattern(val timings: LongArray, val amplitudes: IntArray)
    private fun vibrate(context: Context, haptic: UiHaptic, style: String, intensity: Float) {
        if (intensity <= 0f) {
            return
        }
        val vibrator = getVibrator(context) ?: return
        if (!vibrator.hasVibrator()) {
            return
        }
        val eventScale = when (haptic) {
                UiHaptic.Tick -> 0.46f
                UiHaptic.Selection -> 0.68f
                UiHaptic.Tap -> 0.76f
                UiHaptic.ToggleOn -> 0.82f
                UiHaptic.ToggleOff -> 0.64f
                UiHaptic.Attachment -> 0.88f
                UiHaptic.Confirm -> 0.96f
                UiHaptic.Warning -> 1.00f
            }
        // configure y previewStyle validan el estilo antes de entrar aquí.
        var pattern = basePattern(style)
        // Sliders y movimientos de cursor deben sentirse como ticks cortos,
        // incluso cuando el usuario eligió un estilo con varios pulsos.
        if (haptic == UiHaptic.Tick) {
            val firstAmp = pattern.amplitudes.firstOrNull { it > 0 }?: 120
            pattern = HapticPattern(timings = tickTimings, amplitudes = intArrayOf(0, firstAmp))
        }
        if (haptic == UiHaptic.ToggleOff) {
            val shortenedTimings = LongArray(pattern.timings.size) { index ->
                (pattern.timings[index] * 0.85f).roundToInt().coerceAtLeast(1).toLong()
            }
            pattern = HapticPattern(timings = shortenedTimings, amplitudes = pattern.amplitudes)
        }
        val scaledAmplitudes = IntArray(pattern.amplitudes.size) { index ->
            val amplitude = pattern.amplitudes[index]
            if (amplitude == 0) 0 else (amplitude * intensity * eventScale).roundToInt().coerceIn(1, 255)
        }
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val effect = if (pattern.timings.size <= 2) {
                        VibrationEffect.createOneShot(pattern.timings.lastOrNull()?.coerceAtLeast(1L) ?: 8L,
                            scaledAmplitudes.lastOrNull()?.coerceIn(1, 255)?: VibrationEffect.DEFAULT_AMPLITUDE)
                    } else {
                        VibrationEffect.createWaveform(pattern.timings, scaledAmplitudes, -1)
                    }
                vibrator.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(pattern.timings.sum().coerceIn(5L, 90L))
            }
        } catch (_: SecurityException) {
            // No se fuerza feedback si el fabricante bloquea la vibración.
        }
    }
    /*
     * Los patrones no cambian durante la vida de la app. Mantenerlos
     * preconstruidos evita crear LongArray/IntArray en cada pulsación.
     */
    private val basePatterns: Map<String, HapticPattern> = mapOf(
        "soft" to HapticPattern(longArrayOf(0L, 12L), intArrayOf(0, 115)),
        "crisp" to HapticPattern(longArrayOf(0L, 9L), intArrayOf(0, 220)),
        "deep" to HapticPattern(longArrayOf(0L, 24L), intArrayOf(0, 175)),
        "double" to HapticPattern(longArrayOf(0L, 10L, 34L, 12L), intArrayOf(0, 190, 0, 145)),
        "pulse" to HapticPattern(longArrayOf(0L, 15L, 22L, 22L), intArrayOf(0, 130, 0, 205)),
        "stepped" to HapticPattern(longArrayOf(0L, 6L, 10L, 8L, 10L, 11L), intArrayOf(0, 85, 0, 140, 0, 210)),
        "mechanical" to HapticPattern(longArrayOf(0L, 5L, 8L, 14L), intArrayOf(0, 225, 0, 150)),
        "minimal" to HapticPattern(longArrayOf(0L, 5L), intArrayOf(0, 125)),
        "triple" to HapticPattern(longArrayOf(0L, 6L, 10L, 7L, 10L, 9L), intArrayOf(0, 170, 0, 200, 0, 230)),
        "ripple" to HapticPattern(longArrayOf(0L, 5L, 8L, 7L, 10L, 10L), intArrayOf(0, 225, 0, 155, 0, 90)),
        "heartbeat" to HapticPattern(longArrayOf(0L, 11L, 38L, 20L), intArrayOf(0, 135, 0, 230)),
        "snap" to HapticPattern(longArrayOf(0L, 4L, 5L, 11L), intArrayOf(0, 255, 0, 120)),
        "wave" to HapticPattern(longArrayOf(0L, 10L, 8L, 14L, 8L, 8L), intArrayOf(0, 105, 0, 225, 0, 125)),
        "heavy" to HapticPattern(longArrayOf(0L, 32L), intArrayOf(0, 235)),
        "spring" to HapticPattern(longArrayOf(0L, 7L, 12L, 5L, 14L, 4L), intArrayOf(0, 235, 0, 150, 0, 85)),
        "echo" to HapticPattern(longArrayOf(0L, 10L, 24L, 7L, 22L, 5L), intArrayOf(0, 220, 0, 135, 0, 70))
    )
    private val tickTimings = longArrayOf(0L, 7L)

    private fun basePattern(style: String): HapticPattern = basePatterns[style] ?: basePatterns.getValue(DEFAULT_STYLE)

    private fun getVibrator(context: Context): Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.getSystemService(VibratorManager::class.java)?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
}

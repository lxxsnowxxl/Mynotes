package com.example.mynotes.reminders

import android.content.Context
import android.content.res.Configuration
import android.media.MediaPlayer
import androidx.compose.ui.graphics.toArgb
import com.example.mynotes.settings.AppSettings
import com.example.mynotes.ui.sound.UiHaptic
import com.example.mynotes.ui.sound.UiHapticPlayer
import com.example.mynotes.ui.sound.UiSoundPlayer
import com.example.mynotes.ui.theme.PaletteCatalog
import com.example.mynotes.ui.theme.darkScheme
import com.example.mynotes.ui.theme.lightScheme
import com.example.mynotes.ui.theme.resolveSecondaryUiTextColor
import com.example.mynotes.ui.theme.resolveUiTextColor

/**
 * Copia ligera y síncrona de las opciones que necesita ReminderReceiver.
 * DataStore es asíncrono y un BroadcastReceiver no debe quedarse esperando
 * a un Flow para poder construir la notificación cuando dispara una alarma.
 *
 * Desde v52 también conserva la apariencia actual de MyNotes para que la
 * notificación use la misma paleta, acento y contraste que la aplicación.
 */
object ReminderFeedbackPreferences {
    private const val PREFS = "reminder_feedback_prefs"
    private const val KEY_SOUND_ENABLED = "sound_enabled"
    private const val KEY_SOUND_VOLUME = "sound_volume"
    private const val KEY_SOUND_THEME = "sound_theme"
    private const val KEY_HAPTIC_ENABLED = "haptic_enabled"
    private const val KEY_HAPTIC_INTENSITY = "haptic_intensity"
    private const val KEY_HAPTIC_STYLE = "haptic_style"
    private const val KEY_NOTIFICATION_BACKGROUND = "notification_background"
    private const val KEY_NOTIFICATION_TEXT = "notification_text"
    private const val KEY_NOTIFICATION_SECONDARY_TEXT = "notification_secondary_text"
    private const val KEY_NOTIFICATION_ACCENT = "notification_accent"
    private const val KEY_NOTIFICATION_FONT_SIZE = "notification_font_size"

    data class Snapshot(
        val soundEnabled: Boolean,
        val soundVolume: Float,
        val soundTheme: String,
        val hapticEnabled: Boolean,
        val hapticIntensity: Float,
        val hapticStyle: String,
        val notificationBackground: Int,
        val notificationText: Int,
        val notificationSecondaryText: Int,
        val notificationAccent: Int,
        val notificationFontSize: Float
    )

    fun sync(context: Context, settings: AppSettings) {
        val systemDark = (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) ==
            Configuration.UI_MODE_NIGHT_YES
        val effectiveDark = if (settings.configurationMode == "advanced") settings.darkMode else systemDark
        val palette = PaletteCatalog.find(settings.backgroundColor)
        val scheme = if (effectiveDark) {
            darkScheme(
                palette = palette,
                toneIndex = settings.backgroundToneIndex,
                backgroundIntensity = settings.backgroundIntensity,
                surfacePanelIntensity = settings.surfacePanelIntensity,
                headerIntensity = settings.headerIntensity,
                textColor = settings.textColor,
                accentColor = settings.accentColor
            )
        } else {
            lightScheme(
                palette = palette,
                toneIndex = settings.backgroundToneIndex,
                backgroundIntensity = settings.backgroundIntensity,
                surfacePanelIntensity = settings.surfacePanelIntensity,
                headerIntensity = settings.headerIntensity,
                textColor = settings.textColor,
                accentColor = settings.accentColor
            )
        }
        val panel = scheme.surfaceContainer
        val primaryText = resolveUiTextColor(settings.textColor, panel)
        val secondaryText = resolveSecondaryUiTextColor(settings.textColor, panel)

        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putBoolean(KEY_SOUND_ENABLED, settings.soundEffectsEnabled)
            .putFloat(KEY_SOUND_VOLUME, settings.soundEffectsVolume.coerceIn(0f, 100f))
            .putString(KEY_SOUND_THEME, UiSoundPlayer.normalizeTheme(settings.soundEffectsTheme))
            .putBoolean(KEY_HAPTIC_ENABLED, settings.hapticEffectsEnabled)
            .putFloat(KEY_HAPTIC_INTENSITY, settings.hapticEffectsIntensity.coerceIn(0f, 100f))
            .putString(KEY_HAPTIC_STYLE, UiHapticPlayer.normalizeStyle(settings.hapticEffectsStyle))
            .putInt(KEY_NOTIFICATION_BACKGROUND, panel.toArgb())
            .putInt(KEY_NOTIFICATION_TEXT, primaryText.toArgb())
            .putInt(KEY_NOTIFICATION_SECONDARY_TEXT, secondaryText.toArgb())
            .putInt(KEY_NOTIFICATION_ACCENT, scheme.primary.toArgb())
            .putFloat(KEY_NOTIFICATION_FONT_SIZE, settings.fontSize.coerceIn(12f, 24f))
            .apply()
    }

    fun read(context: Context): Snapshot {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        return Snapshot(
            soundEnabled = prefs.getBoolean(KEY_SOUND_ENABLED, true),
            soundVolume = prefs.getFloat(KEY_SOUND_VOLUME, 65f).coerceIn(0f, 100f),
            soundTheme = UiSoundPlayer.normalizeTheme(prefs.getString(KEY_SOUND_THEME, UiSoundPlayer.DEFAULT_THEME).orEmpty()),
            hapticEnabled = prefs.getBoolean(KEY_HAPTIC_ENABLED, true),
            hapticIntensity = prefs.getFloat(KEY_HAPTIC_INTENSITY, 55f).coerceIn(0f, 100f),
            hapticStyle = UiHapticPlayer.normalizeStyle(prefs.getString(KEY_HAPTIC_STYLE, UiHapticPlayer.DEFAULT_STYLE).orEmpty()),
            notificationBackground = prefs.getInt(KEY_NOTIFICATION_BACKGROUND, 0xFFF3EFE9.toInt()),
            notificationText = prefs.getInt(KEY_NOTIFICATION_TEXT, 0xFF111111.toInt()),
            notificationSecondaryText = prefs.getInt(KEY_NOTIFICATION_SECONDARY_TEXT, 0xFF5F5B57.toInt()),
            notificationAccent = prefs.getInt(KEY_NOTIFICATION_ACCENT, 0xFF4B4743.toInt()),
            notificationFontSize = prefs.getFloat(KEY_NOTIFICATION_FONT_SIZE, 16f).coerceIn(12f, 24f)
        )
    }

    /**
     * Reproduce el feedback del recordatorio usando exactamente el paquete,
     * volumen y patrón háptico seleccionados en Configuración.
     */
    fun playReminderAlert(context: Context) {
        val config = read(context)

        UiHapticPlayer.configure(
            enabled = config.hapticEnabled,
            intensityPercent = config.hapticIntensity,
            style = config.hapticStyle
        )
        if (config.hapticEnabled && config.hapticIntensity > 0f) {
            UiHapticPlayer.play(context, UiHaptic.Confirm)
        }

        if (!config.soundEnabled || config.soundVolume <= 0f) return

        val rawName = if (config.soundTheme == UiSoundPlayer.DEFAULT_THEME) {
            "ui_priority"
        } else {
            "ui_${config.soundTheme}_priority"
        }
        var rawId = context.resources.getIdentifier(rawName, "raw", context.packageName)
        if (rawId == 0) {
            rawId = context.resources.getIdentifier("ui_priority", "raw", context.packageName)
        }
        if (rawId == 0) return

        synchronized(this) {
            activePlayer?.let { previous ->
                try { previous.stop() } catch (_: IllegalStateException) { }
                previous.release()
            }
            val volume = (config.soundVolume / 100f).coerceIn(0f, 1f)
            val player = MediaPlayer.create(context.applicationContext, rawId) ?: return
            activePlayer = player
            player.setVolume(volume, volume)
            player.setOnCompletionListener { finished ->
                finished.release()
                synchronized(this) {
                    if (activePlayer === finished) activePlayer = null
                }
            }
            player.setOnErrorListener { failed, _, _ ->
                failed.release()
                synchronized(this) {
                    if (activePlayer === failed) activePlayer = null
                }
                true
            }
            player.start()
        }
    }

    @Volatile
    private var activePlayer: MediaPlayer? = null
}

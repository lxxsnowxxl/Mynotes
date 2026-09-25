package com.example.mynotes.reminders

import android.content.Context
import android.content.res.Configuration
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.compose.ui.graphics.toArgb
import com.example.mynotes.R
import com.example.mynotes.settings.AppSettings
import com.example.mynotes.settings.FeedbackPreferencePolicy
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
    private const val KEY_REMINDER_SOUND_ENABLED = "reminder_sound_enabled"
    private const val KEY_REMINDER_SOUND_VOLUME = "reminder_sound_volume"
    private const val KEY_REMINDER_RINGTONE = "reminder_ringtone"
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
        val reminderSoundEnabled: Boolean,
        val reminderSoundVolume: Float,
        val reminderRingtone: String,
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

        val desired = Snapshot(
            soundEnabled = settings.soundEffectsEnabled,
            soundVolume = settings.soundEffectsVolume.coerceIn(0f, 100f),
            soundTheme = UiSoundPlayer.normalizeTheme(settings.soundEffectsTheme),
            reminderSoundEnabled = settings.reminderSoundEnabled,
            reminderSoundVolume = settings.reminderSoundVolume.coerceIn(0f, 100f),
            reminderRingtone = FeedbackPreferencePolicy.normalizeReminderRingtone(settings.reminderRingtone),
            hapticEnabled = settings.hapticEffectsEnabled,
            hapticIntensity = settings.hapticEffectsIntensity.coerceIn(0f, 100f),
            hapticStyle = UiHapticPlayer.normalizeStyle(settings.hapticEffectsStyle),
            notificationBackground = panel.toArgb(),
            notificationText = primaryText.toArgb(),
            notificationSecondaryText = secondaryText.toArgb(),
            notificationAccent = scheme.primary.toArgb(),
            notificationFontSize = settings.fontSize.coerceIn(12f, 24f)
        )

        /*
         * MainActivity puede recibir emisiones equivalentes durante
         * recreaciones y cambios de configuración. Si el snapshot final es
         * idéntico, no abrimos otra edición de SharedPreferences ni generamos
         * trabajo de persistencia innecesario.
         */
        if (read(context) == desired) return

        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putBoolean(KEY_SOUND_ENABLED, desired.soundEnabled)
            .putFloat(KEY_SOUND_VOLUME, desired.soundVolume)
            .putString(KEY_SOUND_THEME, desired.soundTheme)
            .putBoolean(KEY_REMINDER_SOUND_ENABLED, desired.reminderSoundEnabled)
            .putFloat(KEY_REMINDER_SOUND_VOLUME, desired.reminderSoundVolume)
            .putString(KEY_REMINDER_RINGTONE, desired.reminderRingtone)
            .putBoolean(KEY_HAPTIC_ENABLED, desired.hapticEnabled)
            .putFloat(KEY_HAPTIC_INTENSITY, desired.hapticIntensity)
            .putString(KEY_HAPTIC_STYLE, desired.hapticStyle)
            .putInt(KEY_NOTIFICATION_BACKGROUND, desired.notificationBackground)
            .putInt(KEY_NOTIFICATION_TEXT, desired.notificationText)
            .putInt(KEY_NOTIFICATION_SECONDARY_TEXT, desired.notificationSecondaryText)
            .putInt(KEY_NOTIFICATION_ACCENT, desired.notificationAccent)
            .putFloat(KEY_NOTIFICATION_FONT_SIZE, desired.notificationFontSize)
            .apply()
    }

    fun read(context: Context): Snapshot {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        return Snapshot(
            soundEnabled = prefs.getBoolean(KEY_SOUND_ENABLED, true),
            soundVolume = prefs.getFloat(KEY_SOUND_VOLUME, 65f).coerceIn(0f, 100f),
            soundTheme = UiSoundPlayer.normalizeTheme(prefs.getString(KEY_SOUND_THEME, UiSoundPlayer.DEFAULT_THEME).orEmpty()),
            reminderSoundEnabled = prefs.getBoolean(KEY_REMINDER_SOUND_ENABLED, true),
            reminderSoundVolume = prefs.getFloat(KEY_REMINDER_SOUND_VOLUME, 75f).coerceIn(0f, 100f),
            reminderRingtone = FeedbackPreferencePolicy.normalizeReminderRingtone(prefs.getString(KEY_REMINDER_RINGTONE, "classic").orEmpty()),
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
     * Reproduce el feedback del recordatorio con el tono dedicado elegido en
     * Configuración. El audio del recordatorio es independiente de los efectos
     * cortos de la interfaz, pero conserva el patrón háptico global.
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

        if (!config.reminderSoundEnabled || config.reminderSoundVolume <= 0f) return
        playRingtoneInternal(
            context = context,
            ringtone = config.reminderRingtone,
            volumePercent = config.reminderSoundVolume
        )
    }

    /**
     * Vista previa desde Configuración. Se permite escucharla aunque el switch
     * esté apagado para que el usuario pueda comparar tonos antes de activarlos.
     */
    fun previewRingtone(context: Context, ringtone: String, volumePercent: Float) {
        playRingtoneInternal(
            context = context,
            ringtone = ringtone,
            volumePercent = volumePercent
        )
    }

    private fun playRingtoneInternal(context: Context, ringtone: String, volumePercent: Float) {
        if (volumePercent <= 0f) return
        val ringtoneAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        synchronized(this) {
            activePlayer?.let { previous ->
                try { previous.stop() } catch (_: IllegalStateException) { }
                previous.release()
            }
            // Los recordatorios usan ahora sonidos de alerta dedicados, separados de los efectos
            // de interfaz. Son señales cortas y repetitivas pensadas para llamar la atención
            // de inmediato sin reutilizar los tonos anteriores.
            val volume = ((volumePercent / 100f).coerceIn(0f, 1f) * 0.84f)
            val player = MediaPlayer.create(
                context.applicationContext,
                ringtoneResource(FeedbackPreferencePolicy.normalizeReminderRingtone(ringtone)),
                ringtoneAttributes,
                0
            ) ?: return
            activePlayer = player
            player.isLooping = false
            player.setVolume(volume, volume)
            player.setOnCompletionListener { finished ->
                // Algunos dispositivos todavía tienen muestras pendientes en el mezclador de audio
                // cuando MediaPlayer notifica onCompletion. Liberarlo en ese mismo instante puede
                // hacer que la cola del tono se perciba cortada. Primero soltamos la referencia
                // activa y dejamos una pequeña ventana para que el hardware termine de vaciarla.
                synchronized(this) {
                    if (activePlayer === finished) activePlayer = null
                }
                completionReleaseHandler.postDelayed({
                    try {
                        finished.release()
                    } catch (_: Exception) {
                    }
                }, PLAYER_RELEASE_GRACE_MS)
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

    private fun ringtoneResource(value: String): Int = when (value) {
        "bell" -> R.raw.reminder_ringtone_bell
        "crystal" -> R.raw.reminder_ringtone_crystal
        "pulse" -> R.raw.reminder_ringtone_pulse
        "sunrise" -> R.raw.reminder_ringtone_sunrise
        "digital" -> R.raw.reminder_ringtone_digital
        "alert" -> R.raw.reminder_ringtone_alert
        "urgent" -> R.raw.reminder_ringtone_urgent
        "beacon" -> R.raw.reminder_ringtone_beacon
        "radar" -> R.raw.reminder_ringtone_radar
        "warning" -> R.raw.reminder_ringtone_warning
        "signal" -> R.raw.reminder_ringtone_signal
        "pager" -> R.raw.reminder_ringtone_pager
        "double_alarm" -> R.raw.reminder_ringtone_double_alarm
        "serenity" -> R.raw.reminder_ringtone_serenity
        "soft_bell" -> R.raw.reminder_ringtone_soft_bell
        "breeze" -> R.raw.reminder_ringtone_breeze
        "dew" -> R.raw.reminder_ringtone_dew
        "bamboo" -> R.raw.reminder_ringtone_bamboo
        "horizon" -> R.raw.reminder_ringtone_horizon
        "calm" -> R.raw.reminder_ringtone_calm
        "moonlight" -> R.raw.reminder_ringtone_moonlight
        "orbit" -> R.raw.reminder_ringtone_orbit
        "droplet" -> R.raw.reminder_ringtone_droplet
        "glass_tap" -> R.raw.reminder_ringtone_glass_tap
        "clockwork" -> R.raw.reminder_ringtone_clockwork
        "spark" -> R.raw.reminder_ringtone_spark
        "bubble_pop" -> R.raw.reminder_ringtone_bubble_pop
        "comet" -> R.raw.reminder_ringtone_comet
        "echo_ping" -> R.raw.reminder_ringtone_echo_ping
        "woodblock" -> R.raw.reminder_ringtone_woodblock
        "starlight" -> R.raw.reminder_ringtone_starlight
        "sentinel" -> R.raw.reminder_ringtone_sentinel
        "siren" -> R.raw.reminder_ringtone_siren
        "cascade" -> R.raw.reminder_ringtone_cascade
        "escalation" -> R.raw.reminder_ringtone_escalation
        "distress" -> R.raw.reminder_ringtone_distress
        "interlock" -> R.raw.reminder_ringtone_interlock
        "scanner" -> R.raw.reminder_ringtone_scanner
        "command" -> R.raw.reminder_ringtone_command
        "rapid_triple" -> R.raw.reminder_ringtone_rapid_triple
        "priority_sequence" -> R.raw.reminder_ringtone_priority_sequence
        "double_sweep" -> R.raw.reminder_ringtone_double_sweep
        "attention_burst" -> R.raw.reminder_ringtone_attention_burst
        else -> R.raw.reminder_ringtone
    }

    private const val PLAYER_RELEASE_GRACE_MS = 320L
    private val completionReleaseHandler = Handler(Looper.getMainLooper())
    private var activePlayer: MediaPlayer? = null
}

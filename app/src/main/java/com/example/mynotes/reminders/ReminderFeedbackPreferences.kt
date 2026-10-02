package com.example.mynotes.reminders

import android.content.Context
import android.content.res.Configuration
import android.media.AudioAttributes
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.audiofx.LoudnessEnhancer
import android.os.Build
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
import com.example.mynotes.ui.theme.resolveAppColorScheme
import com.example.mynotes.ui.theme.resolveSecondaryUiTextColor
import com.example.mynotes.ui.theme.resolveUiTextColor
import com.example.mynotes.ui.theme.effectiveDarkTheme

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
        val effectiveDark = settings.effectiveDarkTheme(systemDark)
        val palette = PaletteCatalog.find(settings.backgroundColor)
        val scheme = resolveAppColorScheme(effectiveDark, palette, settings.backgroundToneIndex, settings.backgroundIntensity,
            settings.surfacePanelIntensity, settings.headerIntensity, settings.textColor, settings.accentColor)
        val panel = scheme.surfaceContainer
        val primaryText = resolveUiTextColor(settings.textColor, panel)
        val secondaryText = resolveSecondaryUiTextColor(settings.textColor, panel)

        val desired = Snapshot(
            soundEnabled = settings.soundEffectsEnabled,
            soundVolume = settings.soundEffectsVolume.coerceIn(0f, 100f),
            soundTheme = UiSoundPlayer.normalizeTheme(settings.soundEffectsTheme),
            reminderSoundEnabled = settings.reminderSoundEnabled,
            reminderSoundVolume = settings.soundEffectsVolume.coerceIn(0f, 100f),
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
        val sharedSoundVolume = prefs.getFloat(KEY_SOUND_VOLUME, 65f).coerceIn(0f, 100f)
        return Snapshot(
            soundEnabled = prefs.getBoolean(KEY_SOUND_ENABLED, true),
            soundVolume = sharedSoundVolume,
            soundTheme = UiSoundPlayer.normalizeTheme(prefs.getString(KEY_SOUND_THEME, UiSoundPlayer.DEFAULT_THEME).orEmpty()),
            reminderSoundEnabled = prefs.getBoolean(KEY_REMINDER_SOUND_ENABLED, true),
            reminderSoundVolume = sharedSoundVolume,
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
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
        // El usuario pidió que MyNotes siga específicamente el control de volumen
        // de NOTIFICACIONES del teléfono, no Timbre ni Multimedia. La comprobación
        // explícita evita iniciar el reproductor cuando ese grupo está silenciado.
        if (audioManager != null && (
                audioManager.isStreamMute(AudioManager.STREAM_NOTIFICATION) ||
                audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION) == 0
            )) return

        // USAGE_NOTIFICATION hace que Android asocie estas alertas al mismo grupo
        // de volumen de notificaciones que controla el panel de sonido del teléfono.
        // El porcentaje interno de MyNotes sólo actúa como atenuación adicional.
        val ringtoneAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        synchronized(this) {
            activeEnhancer?.let { previousEnhancer ->
                try { previousEnhancer.release() } catch (_: Exception) { }
            }
            activeEnhancer = null
            activePlayer?.let { previous ->
                try { previous.stop() } catch (_: IllegalStateException) { }
                previous.release()
            }
            // Los recordatorios usan sonidos de alerta dedicados. Cuando hay una salida Bluetooth
            // conectada, la alerta parte del 100 % de ganancia interna y recibe un refuerzo adicional
            // de +6 dB. El control maestro sigue siendo STREAM_NOTIFICATION: si el usuario baja o
            // silencia Notificaciones en Android, MyNotes respeta ese nivel del sistema.
            val bluetoothBoost = hasBluetoothOutput(audioManager)
            val normalVolume = (volumePercent / 100f).coerceIn(0f, 1f)
            val playbackVolume = if (bluetoothBoost) 1f else normalVolume
            val player = MediaPlayer.create(
                context.applicationContext,
                FeedbackPreferencePolicy.reminderTone(ringtone).soundRes,
                ringtoneAttributes,
                0
            ) ?: return

            val enhancer = if (bluetoothBoost) {
                try {
                    LoudnessEnhancer(player.audioSessionId).also { effect ->
                        effect.setTargetGain(BLUETOOTH_ALERT_GAIN_MB)
                        effect.enabled = true
                    }
                } catch (_: Exception) {
                    // Algunos dispositivos/firmwares no exponen LoudnessEnhancer. En ese caso,
                    // mantenemos igualmente la alerta Bluetooth al 100 % de ganancia interna.
                    null
                }
            } else {
                null
            }

            activePlayer = player
            activeEnhancer = enhancer
            player.isLooping = false
            player.setVolume(playbackVolume, playbackVolume)
            player.setOnCompletionListener { finished ->
                // Algunos dispositivos todavía tienen muestras pendientes en el mezclador de audio
                // cuando MediaPlayer notifica onCompletion. Liberarlo en ese mismo instante puede
                // hacer que la cola del tono se perciba cortada. Primero soltamos las referencias
                // activas y dejamos una pequeña ventana para que el hardware termine de vaciarlas.
                synchronized(this) {
                    if (activePlayer === finished) activePlayer = null
                    if (activeEnhancer === enhancer) activeEnhancer = null
                }
                completionReleaseHandler.postDelayed({
                    try { enhancer?.release() } catch (_: Exception) { }
                    try {
                        finished.release()
                    } catch (_: Exception) {
                    }
                }, PLAYER_RELEASE_GRACE_MS)
            }
            player.setOnErrorListener { failed, _, _ ->
                try { enhancer?.release() } catch (_: Exception) { }
                failed.release()
                synchronized(this) {
                    if (activePlayer === failed) activePlayer = null
                    if (activeEnhancer === enhancer) activeEnhancer = null
                }
                true
            }
            player.start()
        }
    }

    private fun hasBluetoothOutput(audioManager: AudioManager?): Boolean {
        if (audioManager == null) return false
        return try {
            audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS).any { device ->
                when (device.type) {
                    AudioDeviceInfo.TYPE_BLUETOOTH_A2DP,
                    AudioDeviceInfo.TYPE_BLUETOOTH_SCO -> true
                    else -> Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && (
                        device.type == AudioDeviceInfo.TYPE_BLE_SPEAKER ||
                            device.type == AudioDeviceInfo.TYPE_BLE_HEADSET
                        )
                }
            }
        } catch (_: Exception) {
            false
        }
    }

    // +6 dB sobre la reproducción Bluetooth normal. Combinado con el paso de la
    // ganancia interna compartida (65 % por defecto) a 100 %, la alerta queda
    // aproximadamente 9,7 dB por encima del nivel interno predeterminado de MyNotes.
    private const val BLUETOOTH_ALERT_GAIN_MB = 600
    private const val PLAYER_RELEASE_GRACE_MS = 320L
    private val completionReleaseHandler = Handler(Looper.getMainLooper())
    private var activePlayer: MediaPlayer? = null
    private var activeEnhancer: LoudnessEnhancer? = null
}

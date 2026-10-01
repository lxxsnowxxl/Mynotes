package com.example.mynotes.ui.sound

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.SystemClock
import com.example.mynotes.settings.FeedbackPreferencePolicy
import java.util.EnumMap

/**
 * Reproductor ligero para efectos cortos de interfaz.
 *
 * Los eventos de la app disponen de varios paquetes de sonido. Todos los
 * WAV son muy pequeños y se cargan una sola vez con SoundPool para que cambiar
 * de paquete desde Configuración sea inmediato y no cree MediaPlayer nuevos.
 */
enum class UiSound {
    Edit, Delete, Priority, SliderTick, Attachment, Toggle
}

/**
 * Acciones semánticas de la interfaz. Se apoyan en los seis sonidos base de
 * cada paquete y cambian ligeramente velocidad/volumen para que cada acción
 * sea reconocible sin multiplicar innecesariamente los archivos de audio.
 */
enum class UiActionSound(val sound: UiSound, val rate: Float, val volumeScale: Float, val haptic: UiHaptic?) {
    Open(UiSound.Edit, 1.05f, 0.88f, UiHaptic.Tap),
    Back(UiSound.Toggle, 0.82f, 0.82f, UiHaptic.Selection),
    Save(UiSound.Edit, 1.18f, 1.00f, UiHaptic.Confirm),
    Search(UiSound.SliderTick, 1.25f, 0.55f, UiHaptic.Tick),
    TextInput(UiSound.SliderTick, 1.36f, 0.42f, null),
    Menu(UiSound.Toggle, 1.02f, 0.70f, UiHaptic.Selection),
    Select(UiSound.Toggle, 1.10f, 0.78f, UiHaptic.Selection),
    Favorite(UiSound.Priority, 1.28f, 0.92f, UiHaptic.Confirm),
    Pin(UiSound.Toggle, 0.94f, 0.92f, UiHaptic.Selection),
    Share(UiSound.Edit, 1.32f, 0.88f, UiHaptic.Tap),
    Move(UiSound.Toggle, 0.92f, 0.82f, UiHaptic.Selection),
    Color(UiSound.Priority, 1.10f, 0.82f, UiHaptic.Selection),
    Category(UiSound.Toggle, 1.16f, 0.82f, UiHaptic.Selection),
    Add(UiSound.Attachment, 1.16f, 0.92f, UiHaptic.Attachment),
    Confirm(UiSound.Edit, 1.22f, 0.95f, UiHaptic.Confirm),
    Cancel(UiSound.Toggle, 0.80f, 0.78f, UiHaptic.Selection),
    Navigation(UiSound.Toggle, 1.05f, 0.72f, UiHaptic.Selection),
    Sort(UiSound.SliderTick, 1.10f, 0.68f, UiHaptic.Tick),
    Layout(UiSound.SliderTick, 0.95f, 0.72f, UiHaptic.Selection),
    Language(UiSound.Edit, 0.94f, 0.78f, UiHaptic.Selection),
    Theme(UiSound.Priority, 0.98f, 0.84f, UiHaptic.Selection),
    Link(UiSound.Edit, 1.12f, 0.84f, UiHaptic.Tap),
    PlayPause(UiSound.Toggle, 1.20f, 0.78f, UiHaptic.Selection),
    Zoom(UiSound.SliderTick, 1.08f, 0.58f, UiHaptic.Tick),
    Backup(UiSound.Attachment, 0.90f, 0.88f, UiHaptic.Attachment),
    Restore(UiSound.Attachment, 1.05f, 0.88f, UiHaptic.Confirm),
    Settings(UiSound.Edit, 0.90f, 0.76f, UiHaptic.Selection)
}

object UiSoundPlayer {
    const val DEFAULT_THEME = FeedbackPreferencePolicy.DEFAULT_SOUND_THEME
    @Volatile
    private var pool: SoundPool? = null
    private val soundIds = mutableMapOf<String, EnumMap<UiSound, Int>>()
    private val loadedSoundIds = mutableSetOf<Int>()
    private data class PendingTextInput(val soundId: Int, val theme: String, val requestedAt: Long)
    private var pendingTextInput: PendingTextInput? = null
    private const val TEXT_INPUT_LOAD_GRACE_MS = 150L
    private val lastPlayAt = EnumMap<UiSound, Long>(UiSound::class.java)
    private val previewLock = Any()
    @Volatile
    private var previewPrimaryStreamId: Int = 0
    @Volatile
    private var enabled: Boolean = true
    @Volatile
    private var volume: Float = 0.65f
    @Volatile
    private var theme: String = DEFAULT_THEME
    fun normalizeTheme(value: String): String = FeedbackPreferencePolicy.normalizeSoundTheme(value)
    fun configure(context: Context, enabled: Boolean, volumePercent: Float, theme: String = DEFAULT_THEME, hapticEnabled: Boolean = true,
        hapticIntensityPercent: Float = 55f, hapticStyle: String = UiHapticPlayer.DEFAULT_STYLE) {
        synchronized(this) {
            this.enabled = enabled
            this.volume = (volumePercent / 100f).coerceIn(0f, 1f)
            this.theme = normalizeTheme(theme)
            pendingTextInput = null
        }
        UiHapticPlayer.configure(enabled = hapticEnabled, intensityPercent = hapticIntensityPercent, style = hapticStyle)
        if (enabled) {
            ensureInitialized(context)
        }
    }
    fun play(context: Context, sound: UiSound) {
        UiHapticPlayer.playForSound(context = context, sound = sound)
        if (!enabled || volume <= 0f) {
            return
        }
        playInternal(context = context, sound = sound, theme = theme, volume = volume)
    }
    /**
     * Reproduce un sonido por intención de UI. De esta forma casi todas las
     * acciones de la app pueden tener feedback auditivo usando el mismo paquete
     * elegido en Configuración.
     */
    fun playAction(context: Context, action: UiActionSound) {
        UiHapticPlayer.playForAction(context = context, action = action)
        playActionAudioOnly(context = context, action = action)
    }
    /** Ejecuta feedback -> acción y permite reutilizar el mismo orden desde cualquier callback de UI. */
    fun runAction(context: Context, action: UiActionSound, actionBlock: () -> Unit) { playAction(context, action); actionBlock() }
    fun actionHandler(context: Context, action: UiActionSound, actionBlock: () -> Unit): () -> Unit = {
        runAction(context, action, actionBlock)
    }
    fun <T> actionHandler(context: Context, action: UiActionSound, actionBlock: (T) -> Unit): (T) -> Unit = { value ->
        runAction(context, action) { actionBlock(value) }
    }
    /**
     * Variante exclusivamente auditiva. Se usa cuando la misma acción ya
     * reproduce una respuesta háptica específica por separado; así evitamos
     * duplicar o mezclar dos vibraciones al tocar un mismo control.
     */
    fun playActionAudioOnly(context: Context, action: UiActionSound) {
        if (!enabled || volume <= 0f) {
            return
        }
        playInternal(
            context = context,
            sound = action.sound,
            theme = theme,
            volume = (volume * action.volumeScale).coerceIn(0f, 1f),
            rate = action.rate
        )
    }
    /** Un clic por cambio de texto, sin consumir el límite compartido con sliders o selección. */
    fun playTextInput(context: Context, previousText: String, newText: String) {
        if (previousText == newText || !enabled || volume <= 0f) return
        ensureInitialized(context)
        synchronized(this) {
            val soundId = soundIds[theme]?.get(UiActionSound.TextInput.sound) ?: return
            if (soundId in loadedSoundIds) {
                pendingTextInput = null
                playActionAudioOnly(context, UiActionSound.TextInput)
            } else {
                // Al arrancar sólo retenemos el último clic reciente, nunca una ráfaga atrasada.
                pendingTextInput = PendingTextInput(soundId, theme, SystemClock.uptimeMillis())
            }
        }
    }
    fun playActionThrottled(context: Context, action: UiActionSound, minimumIntervalMs: Long = 45L) {
        UiHapticPlayer.playForAction(context = context, action = action, throttled = true, minimumIntervalMs = minimumIntervalMs)
        if (!enabled || volume <= 0f) {
            return
        }
        playThrottledInternal(context = context, sound = action.sound, minimumIntervalMs = minimumIntervalMs, rate = action.rate,
            volumeScale = action.volumeScale)
    }
    /**
     * Sonido específico para interruptores. El encendido se reproduce un poco
     * más agudo y el apagado un poco más grave para que el cambio se distinga
     * sin necesitar dos archivos por paquete.
     *
     * force=true se usa únicamente en el interruptor maestro de sonidos: así
     * también se oye al activar los efectos cuando todavía estaban deshabilitados.
     */
    fun playToggle(context: Context, checked: Boolean, force: Boolean = false) {
        UiHapticPlayer.playToggle(context = context, checked = checked)
        playToggleAudioOnly(context = context, checked = checked, force = force)
    }
    /** Devuelve un callback de switch que conserva el orden feedback -> cambio de estado. */
    fun toggleHandler(
        context: Context,
        force: Boolean = false,
        audioOnly: Boolean = false,
        actionBlock: (Boolean) -> Unit
    ): (Boolean) -> Unit = { checked ->
        if (audioOnly) playToggleAudioOnly(context, checked, force) else playToggle(context, checked, force)
        actionBlock(checked)
    }
    /** Reproduce únicamente el sonido del interruptor, sin disparar hápticos. */
    fun playToggleAudioOnly(context: Context, checked: Boolean, force: Boolean = false) {
        if ((!enabled && !force) || volume <= 0f) {
            return
        }
        playInternal(
            context = context,
            sound = UiSound.Toggle,
            theme = theme,
            volume = volume,
            rate = if (checked) 1.03f else 0.96f
        )
    }
    /**
     * Reproduce un ejemplo del paquete seleccionado sin esperar a que DataStore
     * termine de propagar el cambio. Se usa únicamente desde Configuración.
     */
    fun previewTheme(context: Context, theme: String, sound: UiSound = UiSound.Edit, volumePercent: Float = 65f) {
        val previewVolume = (volumePercent / 100f).coerceIn(0f, 1f)
        if (previewVolume <= 0f) {
            stopThemePreview()
            return
        }
        val soundPool = ensureInitialized(context)
        val normalizedTheme = normalizeTheme(theme)
        val soundId = soundIds[normalizedTheme]?.get(sound)
            ?: soundIds[DEFAULT_THEME]?.get(sound)
            ?: return

        synchronized(previewLock) {
            /*
             * SoundPool permite varias reproducciones simultáneas. Para un
             * selector de paquetes eso no es deseable: si el usuario cambia
             * rápidamente de Classic a Soft, etc., los previews se superponen
             * y parecen distorsionados. Detenemos exclusivamente el preview
             * anterior; los demás sonidos normales de la UI no se alteran.
             */
            if (previewPrimaryStreamId > 0) soundPool.stop(previewPrimaryStreamId)
            previewPrimaryStreamId = soundPool.play(soundId, previewVolume, previewVolume, 2, 0, 1f)
        }
    }

    private fun stopThemePreview() {
        val soundPool = pool ?: return
        synchronized(previewLock) {
            if (previewPrimaryStreamId > 0) soundPool.stop(previewPrimaryStreamId)
            previewPrimaryStreamId = 0
        }
    }
    fun playThrottled(context: Context, sound: UiSound, minimumIntervalMs: Long = 45L) {
        UiHapticPlayer.playForSound(context = context, sound = sound, throttled = true, minimumIntervalMs = minimumIntervalMs)
        if (!enabled || volume <= 0f) {
            return
        }
        if (!acquireThrottleSlot(sound = sound, minimumIntervalMs = minimumIntervalMs)) {
            return
        }
        playInternal(context = context, sound = sound, theme = theme, volume = volume)
    }
    private fun acquireThrottleSlot(sound: UiSound, minimumIntervalMs: Long): Boolean {
        val now = SystemClock.uptimeMillis()
        return synchronized(lastPlayAt) {
            val previous = lastPlayAt[sound] ?: 0L
            if (now - previous < minimumIntervalMs) {
                false
            } else {
                lastPlayAt[sound] = now
                true
            }
        }
    }
    private fun playThrottledInternal(context: Context, sound: UiSound, minimumIntervalMs: Long, rate: Float, volumeScale: Float) {
        if (!enabled || volume <= 0f) {
            return
        }
        if (!acquireThrottleSlot(sound = sound, minimumIntervalMs = minimumIntervalMs)) return
        playInternal(context = context, sound = sound, theme = theme, volume = (volume * volumeScale).coerceIn(0f, 1f), rate = rate)
    }
    private fun playInternal(context: Context, sound: UiSound, theme: String, volume: Float, rate: Float = 1f) {
        if (volume <= 0f) {
            return
        }
        val soundPool = ensureInitialized(context)
        // Los llamadores internos reciben el tema ya validado por configure.
        val soundId = soundIds[theme]?.get(sound)?: soundIds[DEFAULT_THEME]?.get(sound)?: return
        soundPool.play(soundId, volume, volume, 1, 0, rate.coerceIn(0.5f, 2f))
    }
    private fun ensureInitialized(context: Context): SoundPool {
        pool?.let {
            return it
        }
        synchronized(this) {
            pool?.let {
                return it
            }
            /*
             * Los efectos propios de MyNotes usan el canal multimedia en vez
             * del canal de sonidos de sistema. Así, cuando MainActivity mutea
             * temporalmente STREAM_SYSTEM para ocultar el clic del teclado,
             * los sonidos configurados dentro de la app continúan audibles.
             */
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
            val newPool = SoundPool.Builder().setMaxStreams(6).setAudioAttributes(audioAttributes).build()
            val appContext = context.applicationContext
            newPool.setOnLoadCompleteListener { readyPool, soundId, status ->
                synchronized(this) {
                    if (status == 0) loadedSoundIds.add(soundId)
                    val pending = pendingTextInput
                    if (pending != null && pending.soundId == soundId) {
                        pendingTextInput = null
                        if (status == 0 && readyPool === pool && pending.theme == theme &&
                            SystemClock.uptimeMillis() - pending.requestedAt <= TEXT_INPUT_LOAD_GRACE_MS
                        ) {
                            playActionAudioOnly(appContext, UiActionSound.TextInput)
                        }
                    }
                }
            }
            val selectedTheme = theme
            FeedbackPreferencePolicy.soundThemeResources.firstOrNull { it.key == selectedTheme }
                ?.let { loadTheme(newPool, appContext, it) }
            FeedbackPreferencePolicy.soundThemeResources.forEach {
                if (it.key != selectedTheme) loadTheme(newPool, appContext, it)
            }
            pool = newPool
            return newPool
        }
    }
    private fun loadTheme(pool: SoundPool, context: Context, theme: FeedbackPreferencePolicy.SoundThemeResources) {
        val ids = EnumMap<UiSound, Int>(UiSound::class.java)
        ids[UiSound.SliderTick] = pool.load(context, theme.sliderTick, 1)
        ids[UiSound.Edit] = pool.load(context, theme.edit, 1)
        ids[UiSound.Delete] = pool.load(context, theme.delete, 1)
        ids[UiSound.Priority] = pool.load(context, theme.priority, 1)
        ids[UiSound.Attachment] = pool.load(context, theme.attachment, 1)
        ids[UiSound.Toggle] = pool.load(context, theme.toggle, 1)
        soundIds[theme.key] = ids
    }
}

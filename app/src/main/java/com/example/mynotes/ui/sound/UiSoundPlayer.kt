package com.example.mynotes.ui.sound

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.SystemClock
import com.example.mynotes.settings.FeedbackPreferencePolicy
import java.util.EnumMap
import java.util.concurrent.ConcurrentHashMap
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

/**
 * Reproductor ligero para efectos cortos de interfaz.
 *
 * Los eventos de la app disponen de varios paquetes de sonido. Todos los
 * WAV se cargan por paquete con SoundPool, fuera del hilo de interfaz. Abrir
 * una pantalla no debe decodificar los 26 paquetes disponibles.
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
    TextInput(UiSound.SliderTick, 1.36f, 0.52f, null),
    TextErase(UiSound.SliderTick, 0.70f, 0.48f, null),
    Delete(UiSound.Delete, 1.00f, 0.95f, UiHaptic.Warning),
    Undo(UiSound.Toggle, 0.79f, 0.68f, UiHaptic.Selection),
    Redo(UiSound.Toggle, 1.22f, 0.72f, UiHaptic.Selection),
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
    private val soundIds = ConcurrentHashMap<String, EnumMap<UiSound, Int>>()
    private val loadedSoundIds = mutableSetOf<Int>()
    private val sampleLoads = mutableMapOf<Int, CompletableDeferred<Boolean>>()
    private val loadingScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var previewJob: Job? = null
    private var previewGeneration = 0L
    // Única voz para efectos UI: los nuevos clics no pueden quedar superpuestos.
    private val playbackLock = Any()
    private var activeUiStreamId = 0
    private var previewPrimaryStreamId = 0
    private var lastAudioSoundId = 0
    private var lastAudioRate = 0f
    private var lastAudioAtMs = 0L
    private var lastTextInputAtMs = 0L
    private const val INPUT_INTERVAL_MS = 95L
    private const val DUPLICATE_SOUND_INTERVAL_MS = 52L
    private val lastPlayAt = EnumMap<UiSound, Long>(UiSound::class.java)
    @Volatile private var configuredContext: Context? = null
    @Volatile private var lastAudibleFeedbackAtMs: Long = 0L
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
            configuredContext = context.applicationContext
            this.enabled = enabled
            this.volume = (volumePercent / 100f).coerceIn(0f, 1f)
            this.theme = normalizeTheme(theme)
        }
        UiHapticPlayer.configure(enabled = hapticEnabled, intensityPercent = hapticIntensityPercent, style = hapticStyle)
        if (enabled && volumePercent > 0f) queueTheme(context, this.theme) else stopThemePreview()
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
    fun runAction(context: Context, action: UiActionSound, actionBlock: () -> Unit) {
        UiClickSoundPolicy.runClick(action = {
            playAction(context, action)
            actionBlock()
        })
    }
    fun actionHandler(context: Context, action: UiActionSound, actionBlock: () -> Unit): () -> Unit = {
        runAction(context, action, actionBlock)
    }
    fun <T> actionHandler(context: Context, action: UiActionSound, actionBlock: (T) -> Unit): (T) -> Unit = { value ->
        runAction(context, action) { actionBlock(value) }
    }
    /** Usa el clic de respaldo si el callback termina sin haber reproducido su propio sonido. */
    fun runWithFallbackClick(action: UiActionSound = UiActionSound.Select, actionBlock: () -> Unit) {
        UiClickSoundPolicy.runClick(actionBlock) { playFallbackClick(action) }
    }

    /**
     * Clic de respaldo para controles sin una acción sonora semántica específica.
     * Los callbacks ejecutados con runWithFallbackClick comparten el mismo clic,
     * aunque el callback tarde o active otro control con sonido.
     */
    fun playFallbackClick(action: UiActionSound = UiActionSound.Select) {
        val context = configuredContext ?: return
        if (!enabled || volume <= 0f) return
        playInternal(context, action.sound, theme, volume * action.volumeScale, action.rate, fallback = true)
    }

    /** Reproduce el sonido de la acción sin disparar hápticos. */
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
    /**
     * Escribir y borrar caracteres son acciones distintas. Limitar la frecuencia
     * evita una cascada de clics al mantener Backspace o pegar varios caracteres.
     * Nunca se reproduce un sonido de eliminación destructiva al borrar letras.
     */
    fun playTextInput(context: Context, previousText: String, newText: String) {
        if (previousText == newText || !enabled || volume <= 0f) return
        val now = SystemClock.uptimeMillis()
        synchronized(playbackLock) {
            if (!UiClickSoundPolicy.shouldPlayTextInput(now, lastTextInputAtMs, INPUT_INTERVAL_MS)) return
            // Si el cambio de texto viene de un botón que acaba de sonar (limpiar,
            // restaurar, etc.), no añadir el efecto del teclado encima.
            if (!UiClickSoundPolicy.shouldPlay(now, lastAudibleFeedbackAtMs)) return
            lastTextInputAtMs = now
        }
        val intent = UiSemanticSoundPolicy.forTextChange(previousText, newText)
        playActionAudioOnly(context, intent)
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
        UiClickSoundPolicy.runClick(action = {
            if (audioOnly) playToggleAudioOnly(context, checked, force) else playToggle(context, checked, force)
            actionBlock(checked)
        })
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
        val normalizedTheme = normalizeTheme(theme)
        synchronized(playbackLock) {
            if (UiClickSoundPolicy.hasPlayedInClick()) return
            cancelPendingPreviewLocked()
            val generation = previewGeneration
            // Claim this click now so its fallback cannot add another sound while loading.
            UiClickSoundPolicy.recordPlayback()
            val appContext = context.applicationContext
            previewJob = loadingScope.launch {
                val soundPool = ensureInitialized(appContext, normalizedTheme)
                val soundId = soundIds[normalizedTheme]?.get(sound) ?: return@launch
                val ready = synchronized(sampleLoads) {
                    sampleLoads.getOrPut(soundId) { CompletableDeferred() }
                }
                if (withTimeoutOrNull(1_000L) { ready.await() } != true) return@launch
                synchronized(playbackLock) playback@{
                    if (generation != previewGeneration) return@playback
                    stopUiStreamLocked(soundPool)
                    previewPrimaryStreamId = soundPool.play(soundId, previewVolume, previewVolume, 2, 0, 1f)
                    activeUiStreamId = previewPrimaryStreamId
                    if (previewPrimaryStreamId > 0) lastAudibleFeedbackAtMs = SystemClock.uptimeMillis()
                }
            }
        }
    }

    private fun cancelPendingPreviewLocked() {
        previewGeneration++
        previewJob?.cancel()
        previewJob = null
    }

    private fun stopThemePreview() {
        synchronized(playbackLock) {
            cancelPendingPreviewLocked()
            if (previewPrimaryStreamId > 0) pool?.stop(previewPrimaryStreamId)
            if (activeUiStreamId == previewPrimaryStreamId) activeUiStreamId = 0
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
    private fun stopUiStreamLocked(soundPool: SoundPool) {
        if (activeUiStreamId > 0) soundPool.stop(activeUiStreamId)
        activeUiStreamId = 0
        previewPrimaryStreamId = 0
    }

    private fun playInternal(
        context: Context, sound: UiSound, theme: String, volume: Float, rate: Float = 1f,
        fallback: Boolean = false
    ) {
        if (volume <= 0f) return
        synchronized(playbackLock) {
            // Never open a resource or wait for initialization inside a button callback.
            val soundPool = pool
            val soundId = soundIds[theme]?.get(sound) ?: soundIds[DEFAULT_THEME]?.get(sound)
            if (soundPool == null || soundId == null) {
                if (!UiClickSoundPolicy.hasPlayedInClick()) cancelPendingPreviewLocked()
                queueTheme(context, theme)
                return
            }
            val now = SystemClock.uptimeMillis()
            if (fallback && !UiClickSoundPolicy.shouldPlayFallback(now, lastAudibleFeedbackAtMs)) return
            val normalizedRate = rate.coerceIn(0.5f, 2f)
            if (UiClickSoundPolicy.isDuplicateSound(
                    now, lastAudioAtMs, soundId, lastAudioSoundId,
                    normalizedRate, lastAudioRate, DUPLICATE_SOUND_INTERVAL_MS)) return
            // Cada pulsación usa únicamente los WAV que ya terminaron de cargar.
            if (synchronized(loadedSoundIds) { soundId !in loadedSoundIds }) return
            cancelPendingPreviewLocked()
            stopUiStreamLocked(soundPool)
            val streamId = soundPool.play(soundId, volume, volume, 1, 0, normalizedRate)
            if (streamId > 0) {
                UiClickSoundPolicy.recordPlayback()
                activeUiStreamId = streamId
                lastAudioSoundId = soundId
                lastAudioRate = normalizedRate
                lastAudioAtMs = now
                lastAudibleFeedbackAtMs = now
            }
        }
    }
    private fun queueTheme(context: Context, requestedTheme: String) {
        if (soundIds.containsKey(requestedTheme)) return
        val appContext = context.applicationContext
        loadingScope.launch { ensureInitialized(appContext, requestedTheme) }
    }

    private fun ensureInitialized(context: Context, requestedTheme: String): SoundPool {
        val normalizedTheme = normalizeTheme(requestedTheme)
        pool?.let { if (soundIds.containsKey(normalizedTheme)) return it }
        synchronized(this) {
            // Todos los sonidos de feedback de MyNotes pertenecen al grupo de
            // notificaciones del sistema. Así el volumen físico/rápido de Android
            // que controla "Notificaciones" gobierna teclado, efectos y vistas previas,
            // en lugar de depender del volumen multimedia.
            val appContext = context.applicationContext
            val soundPool = pool ?: run {
                val audioAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
                SoundPool.Builder().setMaxStreams(1).setAudioAttributes(audioAttributes).build().also { created ->
                    created.setOnLoadCompleteListener { _, soundId, status ->
                        if (status == 0) synchronized(loadedSoundIds) { loadedSoundIds.add(soundId) }
                        synchronized(sampleLoads) {
                            sampleLoads.getOrPut(soundId) { CompletableDeferred() }.complete(status == 0)
                        }
                    }
                    pool = created
                }
            }
            if (!soundIds.containsKey(normalizedTheme)) {
                loadTheme(soundPool, appContext,
                    FeedbackPreferencePolicy.soundThemeResources.first { it.key == normalizedTheme })
            }
            return soundPool
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

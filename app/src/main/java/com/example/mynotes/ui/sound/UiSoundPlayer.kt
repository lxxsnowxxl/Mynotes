package com.example.mynotes.ui.sound

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.SystemClock
import com.example.mynotes.R
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
enum class UiActionSound {
    Open, Back, Save, Search, TextInput, Menu, Select, Favorite, Pin, Share, Move, Color, Category, Add, Confirm, Cancel, Navigation, Sort,
    Layout, Language, Theme, Link, PlayPause, Zoom, Backup, Restore, Settings
}

object UiSoundPlayer {
    const val DEFAULT_THEME = FeedbackPreferencePolicy.DEFAULT_SOUND_THEME
    val availableThemes: List<String> = FeedbackPreferencePolicy.soundThemes
    private val layeredThemes: Set<String> = emptySet()
    @Volatile
    private var pool: SoundPool? = null
    private val soundIds = mutableMapOf<String, EnumMap<UiSound, Int>>()
    private val lastPlayAt = EnumMap<UiSound, Long>(UiSound::class.java)
    private val previewLock = Any()
    @Volatile
    private var previewPrimaryStreamId: Int = 0
    @Volatile
    private var previewAccentStreamId: Int = 0
    @Volatile
    private var enabled: Boolean = true
    @Volatile
    private var volume: Float = 0.65f
    @Volatile
    private var theme: String = DEFAULT_THEME
    fun normalizeTheme(value: String): String = FeedbackPreferencePolicy.normalizeSoundTheme(value)
    fun configure(context: Context, enabled: Boolean, volumePercent: Float, theme: String = DEFAULT_THEME, hapticEnabled: Boolean = true,
        hapticIntensityPercent: Float = 55f, hapticStyle: String = UiHapticPlayer.DEFAULT_STYLE) {
        this.enabled = enabled
        this.volume = (volumePercent / 100f).coerceIn(0f, 1f)
        this.theme = normalizeTheme(theme)
        UiHapticPlayer.configure(enabled = hapticEnabled, intensityPercent = hapticIntensityPercent, style = hapticStyle)
        if (enabled) {
            ensureInitialized(context)
        }
    }
    fun preload(context: Context) {
        ensureInitialized(context)
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
        if (!enabled || volume <= 0f) {
            return
        }
        val spec = actionSpec(action)
        playInternal(context = context, sound = spec.sound, theme = theme, volume = (volume * spec.volumeScale).coerceIn(0f, 1f),
            rate = spec.rate)
        playAccentLayer(context = context, action = action)
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
        val spec = actionSpec(action)
        playInternal(
            context = context,
            sound = spec.sound,
            theme = theme,
            volume = (volume * spec.volumeScale).coerceIn(0f, 1f),
            rate = spec.rate
        )
        playAccentLayer(context = context, action = action)
    }
    fun playActionThrottled(context: Context, action: UiActionSound, minimumIntervalMs: Long = 45L) {
        UiHapticPlayer.playForAction(context = context, action = action, throttled = true, minimumIntervalMs = minimumIntervalMs)
        if (!enabled || volume <= 0f) {
            return
        }
        val spec = actionSpec(action)
        playThrottledInternal(context = context, sound = spec.sound, minimumIntervalMs = minimumIntervalMs, rate = spec.rate,
            volumeScale = spec.volumeScale)
        playAccentLayer(context = context, action = action)
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
        if ((!enabled && !force) || volume <= 0f) {
            return
        }
        playInternal(context = context, sound = UiSound.Toggle, theme = theme, volume = volume, rate = if (checked) 1.03f else 0.96f)
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
            if (previewAccentStreamId > 0) soundPool.stop(previewAccentStreamId)
            previewPrimaryStreamId = soundPool.play(soundId, previewVolume, previewVolume, 2, 0, 1f)
            previewAccentStreamId = 0
            if (normalizedTheme in layeredThemes) {
                val accentId = soundIds[normalizedTheme]?.get(UiSound.Priority)
                if (accentId != null) {
                    val accentVolume = (previewVolume * 0.24f).coerceIn(0f, 0.35f)
                    previewAccentStreamId = soundPool.play(accentId, accentVolume, accentVolume, 1, 0, 1.32f)
                }
            }
        }
    }

    private fun stopThemePreview() {
        val soundPool = pool ?: return
        synchronized(previewLock) {
            if (previewPrimaryStreamId > 0) soundPool.stop(previewPrimaryStreamId)
            if (previewAccentStreamId > 0) soundPool.stop(previewAccentStreamId)
            previewPrimaryStreamId = 0
            previewAccentStreamId = 0
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
    private data class ActionSpec(val sound: UiSound, val rate: Float = 1f, val volumeScale: Float = 1f)
    private data class AccentSpec(val sound: UiSound, val rate: Float, val volumeScale: Float)

    private val accentSpecs = EnumMap<UiActionSound, AccentSpec>(UiActionSound::class.java).apply {
        put(UiActionSound.Save, AccentSpec(UiSound.Priority, 1.34f, 0.28f))
        put(UiActionSound.Confirm, AccentSpec(UiSound.Priority, 1.46f, 0.24f))
        put(UiActionSound.Favorite, AccentSpec(UiSound.SliderTick, 1.58f, 0.25f))
        put(UiActionSound.Add, AccentSpec(UiSound.Edit, 1.24f, 0.22f))
        put(UiActionSound.Open, AccentSpec(UiSound.SliderTick, 1.30f, 0.16f))
        put(UiActionSound.Navigation, AccentSpec(UiSound.SliderTick, 1.16f, 0.13f))
        put(UiActionSound.Theme, AccentSpec(UiSound.Priority, 1.16f, 0.20f))
    }

    /*
     * Las especificaciones de acciones son constantes. Antes se construía un
     * ActionSpec nuevo en cada toque; al mantenerlos en un EnumMap eliminamos
     * esas asignaciones del camino caliente de botones, búsqueda y navegación.
     */
    private val actionSpecs = EnumMap<UiActionSound, ActionSpec>(UiActionSound::class.java).apply {
        put(UiActionSound.Open, ActionSpec(UiSound.Edit, 1.05f, 0.88f))
        put(UiActionSound.Back, ActionSpec(UiSound.Toggle, 0.82f, 0.82f))
        put(UiActionSound.Save, ActionSpec(UiSound.Edit, 1.18f, 1.00f))
        put(UiActionSound.Search, ActionSpec(UiSound.SliderTick, 1.25f, 0.55f))
        put(UiActionSound.TextInput, ActionSpec(UiSound.SliderTick, 1.36f, 0.42f))
        put(UiActionSound.Menu, ActionSpec(UiSound.Toggle, 1.02f, 0.70f))
        put(UiActionSound.Select, ActionSpec(UiSound.Toggle, 1.10f, 0.78f))
        put(UiActionSound.Favorite, ActionSpec(UiSound.Priority, 1.28f, 0.92f))
        put(UiActionSound.Pin, ActionSpec(UiSound.Toggle, 0.94f, 0.92f))
        put(UiActionSound.Share, ActionSpec(UiSound.Edit, 1.32f, 0.88f))
        put(UiActionSound.Move, ActionSpec(UiSound.Toggle, 0.92f, 0.82f))
        put(UiActionSound.Color, ActionSpec(UiSound.Priority, 1.10f, 0.82f))
        put(UiActionSound.Category, ActionSpec(UiSound.Toggle, 1.16f, 0.82f))
        put(UiActionSound.Add, ActionSpec(UiSound.Attachment, 1.16f, 0.92f))
        put(UiActionSound.Confirm, ActionSpec(UiSound.Edit, 1.22f, 0.95f))
        put(UiActionSound.Cancel, ActionSpec(UiSound.Toggle, 0.80f, 0.78f))
        put(UiActionSound.Navigation, ActionSpec(UiSound.Toggle, 1.05f, 0.72f))
        put(UiActionSound.Sort, ActionSpec(UiSound.SliderTick, 1.10f, 0.68f))
        put(UiActionSound.Layout, ActionSpec(UiSound.SliderTick, 0.95f, 0.72f))
        put(UiActionSound.Language, ActionSpec(UiSound.Edit, 0.94f, 0.78f))
        put(UiActionSound.Theme, ActionSpec(UiSound.Priority, 0.98f, 0.84f))
        put(UiActionSound.Link, ActionSpec(UiSound.Edit, 1.12f, 0.84f))
        put(UiActionSound.PlayPause, ActionSpec(UiSound.Toggle, 1.20f, 0.78f))
        put(UiActionSound.Zoom, ActionSpec(UiSound.SliderTick, 1.08f, 0.58f))
        put(UiActionSound.Backup, ActionSpec(UiSound.Attachment, 0.90f, 0.88f))
        put(UiActionSound.Restore, ActionSpec(UiSound.Attachment, 1.05f, 0.88f))
        put(UiActionSound.Settings, ActionSpec(UiSound.Edit, 0.90f, 0.76f))
    }

    private fun actionSpec(action: UiActionSound): ActionSpec = actionSpecs[action] ?: actionSpecs.getValue(UiActionSound.Select)

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
    private fun playAccentLayer(context: Context, action: UiActionSound) {
        if (theme !in layeredThemes || !enabled || volume <= 0f) return
        val accent = accentSpecs[action] ?: return
        val soundPool = ensureInitialized(context)
        val soundId = soundIds[theme]?.get(accent.sound) ?: return
        val accentVolume = (volume * accent.volumeScale).coerceIn(0f, 0.42f)
        soundPool.play(soundId, accentVolume, accentVolume, 0, 0, accent.rate.coerceIn(0.5f, 2f))
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
            loadTheme(pool = newPool, context = appContext, theme = "classic", edit = R.raw.ui_edit, delete = R.raw.ui_delete,
                priority = R.raw.ui_priority, sliderTick = R.raw.ui_slider_tick, attachment = R.raw.ui_attachment, toggle = R.raw.ui_toggle
            )
            loadTheme(pool = newPool, context = appContext, theme = "soft", edit = R.raw.ui_soft_edit, delete = R.raw.ui_soft_delete,
                priority = R.raw.ui_soft_priority, sliderTick = R.raw.ui_soft_slider_tick, attachment = R.raw.ui_soft_attachment,
                toggle = R.raw.ui_soft_toggle)
            loadTheme(pool = newPool, context = appContext, theme = "digital", edit = R.raw.ui_digital_edit,
                delete = R.raw.ui_digital_delete, priority = R.raw.ui_digital_priority, sliderTick = R.raw.ui_digital_slider_tick,
                attachment = R.raw.ui_digital_attachment, toggle = R.raw.ui_digital_toggle)
            loadTheme(pool = newPool, context = appContext, theme = "glass", edit = R.raw.ui_glass_edit, delete = R.raw.ui_glass_delete,
                priority = R.raw.ui_glass_priority, sliderTick = R.raw.ui_glass_slider_tick, attachment = R.raw.ui_glass_attachment,
                toggle = R.raw.ui_glass_toggle)
            loadTheme(pool = newPool, context = appContext, theme = "retro", edit = R.raw.ui_retro_edit, delete = R.raw.ui_retro_delete,
                priority = R.raw.ui_retro_priority, sliderTick = R.raw.ui_retro_slider_tick, attachment = R.raw.ui_retro_attachment,
                toggle = R.raw.ui_retro_toggle)
            loadTheme(pool = newPool, context = appContext, theme = "pop", edit = R.raw.ui_pop_edit, delete = R.raw.ui_pop_delete,
                priority = R.raw.ui_pop_priority, sliderTick = R.raw.ui_pop_slider_tick, attachment = R.raw.ui_pop_attachment,
                toggle = R.raw.ui_pop_toggle)
            loadTheme(pool = newPool, context = appContext, theme = "mechanical", edit = R.raw.ui_mechanical_edit,
                delete = R.raw.ui_mechanical_delete, priority = R.raw.ui_mechanical_priority, sliderTick = R.raw.ui_mechanical_slider_tick,
                attachment = R.raw.ui_mechanical_attachment, toggle = R.raw.ui_mechanical_toggle)
            loadTheme(pool = newPool, context = appContext, theme = "bubble", edit = R.raw.ui_bubble_edit, delete = R.raw.ui_bubble_delete,
                priority = R.raw.ui_bubble_priority, sliderTick = R.raw.ui_bubble_slider_tick, attachment = R.raw.ui_bubble_attachment,
                toggle = R.raw.ui_bubble_toggle)
            loadTheme(pool = newPool, context = appContext, theme = "arcade", edit = R.raw.ui_arcade_edit, delete = R.raw.ui_arcade_delete,
                priority = R.raw.ui_arcade_priority, sliderTick = R.raw.ui_arcade_slider_tick, attachment = R.raw.ui_arcade_attachment,
                toggle = R.raw.ui_arcade_toggle)
            loadTheme(pool = newPool, context = appContext, theme = "wood", edit = R.raw.ui_wood_edit, delete = R.raw.ui_wood_delete,
                priority = R.raw.ui_wood_priority, sliderTick = R.raw.ui_wood_slider_tick, attachment = R.raw.ui_wood_attachment,
                toggle = R.raw.ui_wood_toggle)
            loadTheme(pool = newPool, context = appContext, theme = "synth", edit = R.raw.ui_synth_edit, delete = R.raw.ui_synth_delete,
                priority = R.raw.ui_synth_priority, sliderTick = R.raw.ui_synth_slider_tick, attachment = R.raw.ui_synth_attachment,
                toggle = R.raw.ui_synth_toggle)
            loadTheme(pool = newPool, context = appContext, theme = "minimal", edit = R.raw.ui_minimal_edit,
                delete = R.raw.ui_minimal_delete, priority = R.raw.ui_minimal_priority, sliderTick = R.raw.ui_minimal_slider_tick,
                attachment = R.raw.ui_minimal_attachment, toggle = R.raw.ui_minimal_toggle)
            loadTheme(pool = newPool, context = appContext, theme = "camera", edit = R.raw.ui_camera_edit, delete = R.raw.ui_camera_delete,
                priority = R.raw.ui_camera_priority, sliderTick = R.raw.ui_camera_slider_tick, attachment = R.raw.ui_camera_attachment,
                toggle = R.raw.ui_camera_toggle)
            loadTheme(pool = newPool, context = appContext, theme = "typewriter", edit = R.raw.ui_typewriter_edit,
                delete = R.raw.ui_typewriter_delete, priority = R.raw.ui_typewriter_priority, sliderTick = R.raw.ui_typewriter_slider_tick,
                attachment = R.raw.ui_typewriter_attachment, toggle = R.raw.ui_typewriter_toggle)
            loadTheme(pool = newPool, context = appContext, theme = "metal", edit = R.raw.ui_metal_edit, delete = R.raw.ui_metal_delete,
                priority = R.raw.ui_metal_priority, sliderTick = R.raw.ui_metal_slider_tick, attachment = R.raw.ui_metal_attachment,
                toggle = R.raw.ui_metal_toggle)
            loadTheme(pool = newPool, context = appContext, theme = "pixel", edit = R.raw.ui_pixel_edit, delete = R.raw.ui_pixel_delete,
                priority = R.raw.ui_pixel_priority, sliderTick = R.raw.ui_pixel_slider_tick, attachment = R.raw.ui_pixel_attachment,
                toggle = R.raw.ui_pixel_toggle)
            loadTheme(pool = newPool, context = appContext, theme = "space", edit = R.raw.ui_space_edit, delete = R.raw.ui_space_delete,
                priority = R.raw.ui_space_priority, sliderTick = R.raw.ui_space_slider_tick, attachment = R.raw.ui_space_attachment,
                toggle = R.raw.ui_space_toggle)
            loadTheme(pool = newPool, context = appContext, theme = "chime", edit = R.raw.ui_chime_edit, delete = R.raw.ui_chime_delete,
                priority = R.raw.ui_chime_priority, sliderTick = R.raw.ui_chime_slider_tick, attachment = R.raw.ui_chime_attachment,
                toggle = R.raw.ui_chime_toggle)
            loadTheme(pool = newPool, context = appContext, theme = "paper", edit = R.raw.ui_paper_edit, delete = R.raw.ui_paper_delete,
                priority = R.raw.ui_paper_priority, sliderTick = R.raw.ui_paper_slider_tick, attachment = R.raw.ui_paper_attachment,
                toggle = R.raw.ui_paper_toggle)
            loadTheme(pool = newPool, context = appContext, theme = "neon", edit = R.raw.ui_neon_edit, delete = R.raw.ui_neon_delete,
                priority = R.raw.ui_neon_priority, sliderTick = R.raw.ui_neon_slider_tick, attachment = R.raw.ui_neon_attachment,
                toggle = R.raw.ui_neon_toggle)
            loadTheme(pool = newPool, context = appContext, theme = "material", edit = R.raw.ui_material_edit, delete = R.raw.ui_material_delete,
                priority = R.raw.ui_material_priority, sliderTick = R.raw.ui_material_slider_tick, attachment = R.raw.ui_material_attachment,
                toggle = R.raw.ui_material_toggle)
            loadTheme(pool = newPool, context = appContext, theme = "expressive", edit = R.raw.ui_expressive_edit,
                delete = R.raw.ui_expressive_delete, priority = R.raw.ui_expressive_priority, sliderTick = R.raw.ui_expressive_slider_tick,
                attachment = R.raw.ui_expressive_attachment, toggle = R.raw.ui_expressive_toggle)
            loadTheme(pool = newPool, context = appContext, theme = "prism", edit = R.raw.ui_prism_edit, delete = R.raw.ui_prism_delete,
                priority = R.raw.ui_prism_priority, sliderTick = R.raw.ui_prism_slider_tick, attachment = R.raw.ui_prism_attachment,
                toggle = R.raw.ui_prism_toggle)
            loadTheme(pool = newPool, context = appContext, theme = "aurora", edit = R.raw.ui_aurora_edit, delete = R.raw.ui_aurora_delete,
                priority = R.raw.ui_aurora_priority, sliderTick = R.raw.ui_aurora_slider_tick, attachment = R.raw.ui_aurora_attachment,
                toggle = R.raw.ui_aurora_toggle)
            loadTheme(pool = newPool, context = appContext, theme = "fluid", edit = R.raw.ui_fluid_edit, delete = R.raw.ui_fluid_delete,
                priority = R.raw.ui_fluid_priority, sliderTick = R.raw.ui_fluid_slider_tick, attachment = R.raw.ui_fluid_attachment,
                toggle = R.raw.ui_fluid_toggle)
            loadTheme(pool = newPool, context = appContext, theme = "pulse", edit = R.raw.ui_pulse_edit, delete = R.raw.ui_pulse_delete,
                priority = R.raw.ui_pulse_priority, sliderTick = R.raw.ui_pulse_slider_tick, attachment = R.raw.ui_pulse_attachment,
                toggle = R.raw.ui_pulse_toggle)
            pool = newPool
            return newPool
        }
    }
    private fun loadTheme(pool: SoundPool, context: Context, theme: String, edit: Int, delete: Int, priority: Int, sliderTick: Int,
        attachment: Int, toggle: Int) {
        val ids = EnumMap<UiSound, Int>(UiSound::class.java)
        ids[UiSound.Edit] = pool.load(context, edit, 1)
        ids[UiSound.Delete] = pool.load(context, delete, 1)
        ids[UiSound.Priority] = pool.load(context, priority, 1)
        ids[UiSound.SliderTick] = pool.load(context, sliderTick, 1)
        ids[UiSound.Attachment] = pool.load(context, attachment, 1)
        ids[UiSound.Toggle] = pool.load(context, toggle, 1)
        soundIds[theme] = ids
    }
}

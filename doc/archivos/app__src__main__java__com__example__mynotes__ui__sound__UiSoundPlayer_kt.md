# UiSoundPlayer.kt — documentación exhaustiva actualizada

**Ruta de código real:** `app/src/main/java/com/example/mynotes/ui/sound/UiSoundPlayer.kt`  
**SHA-256 actual del archivo, sin modificar:** `08be2e8dd490cc055c8b648d42a3d6da03644e50c64ac254435a9d1643fd7c8f`  
**Líneas del código real:** 271  
**Estado respecto de la documentación anterior:** **archivo modificado desde la instantánea anterior**

> **Garantía:** este documento vive fuera de `app/`. No se insertó ni eliminó código en el fuente para crear esta explicación. Los fragmentos siguientes son copias de lectura.

## 1. Papel del archivo

Motor de efectos sonoros de la interfaz. Resuelve paquetes/sonidos configurados, volumen y reproducción de acciones de UI.

**Cambios recientes cubiertos por esta revisión.** Los cambios recientes separan el canal de audio propio de MyNotes del STREAM_SYSTEM usado habitualmente por clics del teclado para que el teclado pueda silenciarse sin apagar los sonidos de la app.

## 2. Package e imports

El package declarado es `com.example.mynotes.ui.sound`. El package fija el namespace de Kotlin y condiciona cómo se resuelven nombres, visibilidad, imports y referencias desde otros módulos.

El archivo contiene **6 imports**. Se agrupan por responsabilidad:

### Android / Jetpack / Compose

`android.content.Context`, `android.media.AudioAttributes`, `android.media.SoundPool`, `android.os.SystemClock`

### Proyecto MyNotes

`com.example.mynotes.R`

### Java / Kotlin estándar

`java.util.EnumMap`

## 3. Restricciones, límites e invariantes detectables

- **Llamada segura `?.`: 4 aparición/apariciones.** evita desreferenciar receptores nulos; si el receptor es `null`, la cadena se corta de forma segura.
- **Elvis `?:`: 5 aparición/apariciones.** define un fallback explícito cuando el operando izquierdo es nulo.
- **Acotaciones `coerceIn/AtLeast/AtMost`: 5 aparición/apariciones.** imponen límites numéricos para evitar valores fuera del rango aceptado.

Estas apariciones no implican por sí solas un error: son puntos donde el código expresa contratos que deben preservarse al modificarlo.

## 4. Declaraciones y funciones

### 4.1 `UiSound` — class, líneas 17–19

```kotlin
enum class UiSound {
    Edit, Delete, Priority, SliderTick, Attachment, Toggle
}
```

**Firma/entrada.** `enum class UiSound {`

**Funcionamiento observable.** encapsula una operación local cuyo contrato se deriva de sus parámetros, retorno y llamadas internas.

### 4.2 `UiActionSound` — class, líneas 26–29

```kotlin
enum class UiActionSound {
    Open, Back, Save, Search, TextInput, Menu, Select, Favorite, Pin, Share, Move, Color, Category, Add, Confirm, Cancel, Navigation, Sort,
    Layout, Language, Theme, Link, PlayPause, Zoom, Backup, Restore, Settings
}
```

**Firma/entrada.** `enum class UiActionSound {`

**Funcionamiento observable.** encapsula una operación local cuyo contrato se deriva de sus parámetros, retorno y llamadas internas.

### 4.3 `UiSoundPlayer` — object, líneas 31–271

```kotlin
object UiSoundPlayer {
    const val DEFAULT_THEME = "classic"
    val availableThemes: List<String> = listOf("classic", "soft", "digital", "glass", "retro", "pop", "mechanical", "bubble", "arcade",
            "wood", "synth", "minimal", "camera", "typewriter", "metal", "pixel", "space", "chime", "paper", "neon")
    @Volatile
    private var pool: SoundPool? = null
    private val soundIds = mutableMapOf<String, EnumMap<UiSound, Int>>()
    private val lastPlayAt = EnumMap<UiSound, Long>(UiSound::class.java)
    @Volatile
    private var enabled: Boolean = true
    @Volatile
    private var volume: Float = 0.65f
    @Volatile
    private var theme: String = DEFAULT_THEME
    fun normalizeTheme(value: String): String = value.trim().lowercase().takeIf { it in availableThemes }?: DEFAULT_THEME
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
    }
    fun playActionThrottled(context: Context, action: UiActionSound, minimumIntervalMs: Long = 45L) {
        UiHapticPlayer.playForAction(context = context, action = action, throttled = true, minimumIntervalMs = minimumIntervalMs)
        if (!enabled || volume <= 0f) {
            return
        }
        val spec = actionSpec(action)
        playThrottledInternal(context = context, sound = spec.sound, minimumIntervalMs = minimumIntervalMs, rate = spec.rate,
            volumeScale = spec.volumeScale)
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
        playInternal(context = context, sound = UiSound.Toggle, theme = theme, volume = volume, rate = if (checked) 1.08f else 0.88f)
    }
    /**
     * Reproduce un ejemplo del paquete seleccionado sin esperar a que DataStore
     * termine de propagar el cambio. Se usa únicamente desde Configuración.
     */
    fun previewTheme(context: Context, theme: String, sound: UiSound = UiSound.Edit, volumePercent: Float = 65f) {
        playInternal(context = context, sound = sound, theme = normalizeTheme(theme), volume = (volumePercent / 100f).coerceIn(0f, 1f))
    }
    fun playThrottled(context: Context, sound: UiSound, minimumIntervalMs: Long = 45L) {
        UiHapticPlayer.playForSound(context = context, sound = sound, throttled = true, minimumIntervalMs = minimumIntervalMs)
        if (!enabled || volume <= 0f) {
            return
        }
        val now = SystemClock.uptimeMillis()
        val previous = synchronized(lastPlayAt) {
                lastPlayAt[sound] ?: 0L
            }
        if (now - previous < minimumIntervalMs) {
            return
        }
        synchronized(lastPlayAt) {
            lastPlayAt[sound] = now
        }
        playInternal(context = context, sound = sound, theme = theme, volume = volume)
    }
    private data class ActionSpec(val sound: UiSound, val rate: Float = 1f, val volumeScale: Float = 1f)
    private fun actionSpec(action: UiActionSound): ActionSpec = when (action) {
            UiActionSound.Open -> ActionSpec(UiSound.Edit, 1.05f, 0.88f)
            UiActionSound.Back -> ActionSpec(UiSound.Toggle, 0.82f, 0.82f)
            UiActionSound.Save -> ActionSpec(UiSound.Edit, 1.18f, 1.00f)
            UiActionSound.Search -> ActionSpec(UiSound.SliderTick, 1.25f, 0.55f)
            UiActionSound.TextInput -> ActionSpec(UiSound.SliderTick, 1.36f, 0.42f)
            UiActionSound.Menu -> ActionSpec(UiSound.Toggle, 1.02f, 0.70f)
            UiActionSound.Select -> ActionSpec(UiSound.Toggle, 1.10f, 0.78f)
            UiActionSound.Favorite -> ActionSpec(UiSound.Priority, 1.28f, 0.92f)
            UiActionSound.Pin -> ActionSpec(UiSound.Toggle, 0.94f, 0.92f)
            UiActionSound.Share -> ActionSpec(UiSound.Edit, 1.32f, 0.88f)
            UiActionSound.Move -> ActionSpec(UiSound.Toggle, 0.92f, 0.82f)
            UiActionSound.Color -> ActionSpec(UiSound.Priority, 1.10f, 0.82f)
            UiActionSound.Category -> ActionSpec(UiSound.Toggle, 1.16f, 0.82f)
            UiActionSound.Add -> ActionSpec(UiSound.Attachment, 1.16f, 0.92f)
            UiActionSound.Confirm -> ActionSpec(UiSound.Edit, 1.22f, 0.95f)
            UiActionSound.Cancel -> ActionSpec(UiSound.Toggle, 0.80f, 0.78f)
            UiActionSound.Navigation -> ActionSpec(UiSound.Toggle, 1.05f, 0.72f)
            UiActionSound.Sort -> ActionSpec(UiSound.SliderTick, 1.10f, 0.68f)
            UiActionSound.Layout -> ActionSpec(UiSound.SliderTick, 0.95f, 0.72f)
            UiActionSound.Language -> ActionSpec(UiSound.Edit, 0.94f, 0.78f)
            UiActionSound.Theme -> ActionSpec(UiSound.Priority, 0.98f, 0.84f)
            UiActionSound.Link -> ActionSpec(UiSound.Edit, 1.12f, 0.84f)
            UiActionSound.PlayPause -> ActionSpec(UiSound.Toggle, 1.20f, 0.78f)
            UiActionSound.Zoom -> ActionSpec(UiSound.SliderTick, 1.08f, 0.58f)
            UiActionSound.Backup -> ActionSpec(UiSound.Attachment, 0.90f, 0.88f)
            UiActionSound.Restore -> ActionSpec(UiSound.Attachment, 1.05f, 0.88f)
            UiActionSound.Settings -> ActionSpec(UiSound.Edit, 0.90f, 0.76f)
        }
    private fun playThrottledInternal(context: Context, sound: UiSound, minimumIntervalMs: Long, rate: Float, volumeScale: Float) {
        if (!enabled || volume <= 0f) {
            return
        }
        val now = SystemClock.uptimeMillis()
        val previous = synchronized(lastPlayAt) { lastPlayAt[sound] ?: 0L }
        if (now - previous < minimumIntervalMs) return
        synchronized(lastPlayAt) { lastPlayAt[sound] = now }
        playInternal(context = context, sound = sound, theme = theme, volume = (volume * volumeScale).coerceIn(0f, 1f), rate = rate)
    }
    private fun playInternal(context: Context, sound: UiSound, theme: String, volume: Float, rate: Float = 1f) {
        if (volume <= 0f) {
            return
        }
        val soundPool = ensureInitialized(context)
        val soundId = soundIds[normalizeTheme(theme)]?.get(sound)?: soundIds[DEFAULT_THEME]?.get(sound)?: return
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
```

**Firma/entrada.** `object UiSoundPlayer {`

**Funcionamiento observable.** interactúa con el subsistema de audio de Android.

**Puntos que no conviene romper:** contiene retornos explícitos; algunos caminos pueden terminar antes de ejecutar el resto del bloque; acota valores antes de usarlos para proteger rangos de UI/rendimiento.

### 4.4 `normalizeTheme` — fun, líneas 45–45

```kotlin
    fun normalizeTheme(value: String): String = value.trim().lowercase().takeIf { it in availableThemes }?: DEFAULT_THEME
```

**Firma/entrada.** `fun normalizeTheme(value: String): String = value.trim().lowercase().takeIf { it in availableThemes }?: DEFAULT_THEME`

**Parámetros.**
- `value: String): String = value.trim().lowercase(` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente. Tiene valor por defecto y puede omitirse en la llamada.

**Funcionamiento observable.** encapsula una operación local cuyo contrato se deriva de sus parámetros, retorno y llamadas internas.

### 4.5 `configure` — fun, líneas 46–55

```kotlin
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
```

**Firma/entrada.** `fun configure(context: Context, enabled: Boolean, volumePercent: Float, theme: String = DEFAULT_THEME, hapticEnabled: Boolean = true, hapticIntensityPercent: Float = 55f, hapticStyle: String = UiHapticPlayer.DEFAULT_STYLE) {`

**Parámetros.**
- `context: Context` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `enabled: Boolean` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `volumePercent: Float` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `theme: String = DEFAULT_THEME` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente. Tiene valor por defecto y puede omitirse en la llamada.
- `hapticEnabled: Boolean = true` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente. Tiene valor por defecto y puede omitirse en la llamada.
- `hapticIntensityPercent: Float = 55f` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente. Tiene valor por defecto y puede omitirse en la llamada.
- `hapticStyle: String = UiHapticPlayer.DEFAULT_STYLE` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente. Tiene valor por defecto y puede omitirse en la llamada.

**Funcionamiento observable.** encapsula una operación local cuyo contrato se deriva de sus parámetros, retorno y llamadas internas.

**Puntos que no conviene romper:** acota valores antes de usarlos para proteger rangos de UI/rendimiento.

### 4.6 `preload` — fun, líneas 56–58

```kotlin
    fun preload(context: Context) {
        ensureInitialized(context)
    }
```

**Firma/entrada.** `fun preload(context: Context) {`

**Parámetros.**
- `context: Context` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.

**Funcionamiento observable.** encapsula una operación local cuyo contrato se deriva de sus parámetros, retorno y llamadas internas.

### 4.7 `play` — fun, líneas 59–65

```kotlin
    fun play(context: Context, sound: UiSound) {
        UiHapticPlayer.playForSound(context = context, sound = sound)
        if (!enabled || volume <= 0f) {
            return
        }
        playInternal(context = context, sound = sound, theme = theme, volume = volume)
    }
```

**Firma/entrada.** `fun play(context: Context, sound: UiSound) {`

**Parámetros.**
- `context: Context` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `sound: UiSound` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.

**Funcionamiento observable.** encapsula una operación local cuyo contrato se deriva de sus parámetros, retorno y llamadas internas.

**Puntos que no conviene romper:** contiene retornos explícitos; algunos caminos pueden terminar antes de ejecutar el resto del bloque.

### 4.8 `playAction` — fun, líneas 71–79

```kotlin
    fun playAction(context: Context, action: UiActionSound) {
        UiHapticPlayer.playForAction(context = context, action = action)
        if (!enabled || volume <= 0f) {
            return
        }
        val spec = actionSpec(action)
        playInternal(context = context, sound = spec.sound, theme = theme, volume = (volume * spec.volumeScale).coerceIn(0f, 1f),
            rate = spec.rate)
    }
```

**Firma/entrada.** `fun playAction(context: Context, action: UiActionSound) {`

**Parámetros.**
- `context: Context` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `action: UiActionSound` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.

**Funcionamiento observable.** encapsula una operación local cuyo contrato se deriva de sus parámetros, retorno y llamadas internas.

**Puntos que no conviene romper:** contiene retornos explícitos; algunos caminos pueden terminar antes de ejecutar el resto del bloque; acota valores antes de usarlos para proteger rangos de UI/rendimiento.

### 4.9 `playActionThrottled` — fun, líneas 80–88

```kotlin
    fun playActionThrottled(context: Context, action: UiActionSound, minimumIntervalMs: Long = 45L) {
        UiHapticPlayer.playForAction(context = context, action = action, throttled = true, minimumIntervalMs = minimumIntervalMs)
        if (!enabled || volume <= 0f) {
            return
        }
        val spec = actionSpec(action)
        playThrottledInternal(context = context, sound = spec.sound, minimumIntervalMs = minimumIntervalMs, rate = spec.rate,
            volumeScale = spec.volumeScale)
    }
```

**Firma/entrada.** `fun playActionThrottled(context: Context, action: UiActionSound, minimumIntervalMs: Long = 45L) {`

**Parámetros.**
- `context: Context` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `action: UiActionSound` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `minimumIntervalMs: Long = 45L` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente. Tiene valor por defecto y puede omitirse en la llamada.

**Funcionamiento observable.** encapsula una operación local cuyo contrato se deriva de sus parámetros, retorno y llamadas internas.

**Puntos que no conviene romper:** contiene retornos explícitos; algunos caminos pueden terminar antes de ejecutar el resto del bloque.

### 4.10 `playToggle` — fun, líneas 97–103

```kotlin
    fun playToggle(context: Context, checked: Boolean, force: Boolean = false) {
        UiHapticPlayer.playToggle(context = context, checked = checked)
        if ((!enabled && !force) || volume <= 0f) {
            return
        }
        playInternal(context = context, sound = UiSound.Toggle, theme = theme, volume = volume, rate = if (checked) 1.08f else 0.88f)
    }
```

**Firma/entrada.** `fun playToggle(context: Context, checked: Boolean, force: Boolean = false) {`

**Parámetros.**
- `context: Context` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `checked: Boolean` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `force: Boolean = false` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente. Tiene valor por defecto y puede omitirse en la llamada.

**Funcionamiento observable.** encapsula una operación local cuyo contrato se deriva de sus parámetros, retorno y llamadas internas.

**Puntos que no conviene romper:** contiene retornos explícitos; algunos caminos pueden terminar antes de ejecutar el resto del bloque.

### 4.11 `previewTheme` — fun, líneas 108–110

```kotlin
    fun previewTheme(context: Context, theme: String, sound: UiSound = UiSound.Edit, volumePercent: Float = 65f) {
        playInternal(context = context, sound = sound, theme = normalizeTheme(theme), volume = (volumePercent / 100f).coerceIn(0f, 1f))
    }
```

**Firma/entrada.** `fun previewTheme(context: Context, theme: String, sound: UiSound = UiSound.Edit, volumePercent: Float = 65f) {`

**Parámetros.**
- `context: Context` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `theme: String` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `sound: UiSound = UiSound.Edit` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente. Tiene valor por defecto y puede omitirse en la llamada.
- `volumePercent: Float = 65f` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente. Tiene valor por defecto y puede omitirse en la llamada.

**Funcionamiento observable.** encapsula una operación local cuyo contrato se deriva de sus parámetros, retorno y llamadas internas.

**Puntos que no conviene romper:** acota valores antes de usarlos para proteger rangos de UI/rendimiento.

### 4.12 `playThrottled` — fun, líneas 111–127

```kotlin
    fun playThrottled(context: Context, sound: UiSound, minimumIntervalMs: Long = 45L) {
        UiHapticPlayer.playForSound(context = context, sound = sound, throttled = true, minimumIntervalMs = minimumIntervalMs)
        if (!enabled || volume <= 0f) {
            return
        }
        val now = SystemClock.uptimeMillis()
        val previous = synchronized(lastPlayAt) {
                lastPlayAt[sound] ?: 0L
            }
        if (now - previous < minimumIntervalMs) {
            return
        }
        synchronized(lastPlayAt) {
            lastPlayAt[sound] = now
        }
        playInternal(context = context, sound = sound, theme = theme, volume = volume)
    }
```

**Firma/entrada.** `fun playThrottled(context: Context, sound: UiSound, minimumIntervalMs: Long = 45L) {`

**Parámetros.**
- `context: Context` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `sound: UiSound` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `minimumIntervalMs: Long = 45L` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente. Tiene valor por defecto y puede omitirse en la llamada.

**Funcionamiento observable.** encapsula una operación local cuyo contrato se deriva de sus parámetros, retorno y llamadas internas.

**Puntos que no conviene romper:** contiene retornos explícitos; algunos caminos pueden terminar antes de ejecutar el resto del bloque.

### 4.13 `ActionSpec` — class, líneas 128–157

```kotlin
    private data class ActionSpec(val sound: UiSound, val rate: Float = 1f, val volumeScale: Float = 1f)
    private fun actionSpec(action: UiActionSound): ActionSpec = when (action) {
            UiActionSound.Open -> ActionSpec(UiSound.Edit, 1.05f, 0.88f)
            UiActionSound.Back -> ActionSpec(UiSound.Toggle, 0.82f, 0.82f)
            UiActionSound.Save -> ActionSpec(UiSound.Edit, 1.18f, 1.00f)
            UiActionSound.Search -> ActionSpec(UiSound.SliderTick, 1.25f, 0.55f)
            UiActionSound.TextInput -> ActionSpec(UiSound.SliderTick, 1.36f, 0.42f)
            UiActionSound.Menu -> ActionSpec(UiSound.Toggle, 1.02f, 0.70f)
            UiActionSound.Select -> ActionSpec(UiSound.Toggle, 1.10f, 0.78f)
            UiActionSound.Favorite -> ActionSpec(UiSound.Priority, 1.28f, 0.92f)
            UiActionSound.Pin -> ActionSpec(UiSound.Toggle, 0.94f, 0.92f)
            UiActionSound.Share -> ActionSpec(UiSound.Edit, 1.32f, 0.88f)
            UiActionSound.Move -> ActionSpec(UiSound.Toggle, 0.92f, 0.82f)
            UiActionSound.Color -> ActionSpec(UiSound.Priority, 1.10f, 0.82f)
            UiActionSound.Category -> ActionSpec(UiSound.Toggle, 1.16f, 0.82f)
            UiActionSound.Add -> ActionSpec(UiSound.Attachment, 1.16f, 0.92f)
            UiActionSound.Confirm -> ActionSpec(UiSound.Edit, 1.22f, 0.95f)
            UiActionSound.Cancel -> ActionSpec(UiSound.Toggle, 0.80f, 0.78f)
            UiActionSound.Navigation -> ActionSpec(UiSound.Toggle, 1.05f, 0.72f)
            UiActionSound.Sort -> ActionSpec(UiSound.SliderTick, 1.10f, 0.68f)
            UiActionSound.Layout -> ActionSpec(UiSound.SliderTick, 0.95f, 0.72f)
            UiActionSound.Language -> ActionSpec(UiSound.Edit, 0.94f, 0.78f)
            UiActionSound.Theme -> ActionSpec(UiSound.Priority, 0.98f, 0.84f)
            UiActionSound.Link -> ActionSpec(UiSound.Edit, 1.12f, 0.84f)
            UiActionSound.PlayPause -> ActionSpec(UiSound.Toggle, 1.20f, 0.78f)
            UiActionSound.Zoom -> ActionSpec(UiSound.SliderTick, 1.08f, 0.58f)
            UiActionSound.Backup -> ActionSpec(UiSound.Attachment, 0.90f, 0.88f)
            UiActionSound.Restore -> ActionSpec(UiSound.Attachment, 1.05f, 0.88f)
            UiActionSound.Settings -> ActionSpec(UiSound.Edit, 0.90f, 0.76f)
        }
```

**Firma/entrada.** `private data class ActionSpec(val sound: UiSound, val rate: Float = 1f, val volumeScale: Float = 1f) private fun actionSpec(action: UiActionSound): ActionSpec = when (action) {`

**Parámetros.**
- `val sound: UiSound` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `val rate: Float = 1f` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente. Tiene valor por defecto y puede omitirse en la llamada.
- `val volumeScale: Float = 1f) private fun actionSpec(action: UiActionSound): ActionSpec = when (action` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente. Tiene valor por defecto y puede omitirse en la llamada.

**Funcionamiento observable.** encapsula una operación local cuyo contrato se deriva de sus parámetros, retorno y llamadas internas.

### 4.14 `actionSpec` — fun, líneas 129–157

```kotlin
    private fun actionSpec(action: UiActionSound): ActionSpec = when (action) {
            UiActionSound.Open -> ActionSpec(UiSound.Edit, 1.05f, 0.88f)
            UiActionSound.Back -> ActionSpec(UiSound.Toggle, 0.82f, 0.82f)
            UiActionSound.Save -> ActionSpec(UiSound.Edit, 1.18f, 1.00f)
            UiActionSound.Search -> ActionSpec(UiSound.SliderTick, 1.25f, 0.55f)
            UiActionSound.TextInput -> ActionSpec(UiSound.SliderTick, 1.36f, 0.42f)
            UiActionSound.Menu -> ActionSpec(UiSound.Toggle, 1.02f, 0.70f)
            UiActionSound.Select -> ActionSpec(UiSound.Toggle, 1.10f, 0.78f)
            UiActionSound.Favorite -> ActionSpec(UiSound.Priority, 1.28f, 0.92f)
            UiActionSound.Pin -> ActionSpec(UiSound.Toggle, 0.94f, 0.92f)
            UiActionSound.Share -> ActionSpec(UiSound.Edit, 1.32f, 0.88f)
            UiActionSound.Move -> ActionSpec(UiSound.Toggle, 0.92f, 0.82f)
            UiActionSound.Color -> ActionSpec(UiSound.Priority, 1.10f, 0.82f)
            UiActionSound.Category -> ActionSpec(UiSound.Toggle, 1.16f, 0.82f)
            UiActionSound.Add -> ActionSpec(UiSound.Attachment, 1.16f, 0.92f)
            UiActionSound.Confirm -> ActionSpec(UiSound.Edit, 1.22f, 0.95f)
            UiActionSound.Cancel -> ActionSpec(UiSound.Toggle, 0.80f, 0.78f)
            UiActionSound.Navigation -> ActionSpec(UiSound.Toggle, 1.05f, 0.72f)
            UiActionSound.Sort -> ActionSpec(UiSound.SliderTick, 1.10f, 0.68f)
            UiActionSound.Layout -> ActionSpec(UiSound.SliderTick, 0.95f, 0.72f)
            UiActionSound.Language -> ActionSpec(UiSound.Edit, 0.94f, 0.78f)
            UiActionSound.Theme -> ActionSpec(UiSound.Priority, 0.98f, 0.84f)
            UiActionSound.Link -> ActionSpec(UiSound.Edit, 1.12f, 0.84f)
            UiActionSound.PlayPause -> ActionSpec(UiSound.Toggle, 1.20f, 0.78f)
            UiActionSound.Zoom -> ActionSpec(UiSound.SliderTick, 1.08f, 0.58f)
            UiActionSound.Backup -> ActionSpec(UiSound.Attachment, 0.90f, 0.88f)
            UiActionSound.Restore -> ActionSpec(UiSound.Attachment, 1.05f, 0.88f)
            UiActionSound.Settings -> ActionSpec(UiSound.Edit, 0.90f, 0.76f)
        }
```

**Firma/entrada.** `private fun actionSpec(action: UiActionSound): ActionSpec = when (action) {`

**Parámetros.**
- `action: UiActionSound): ActionSpec = when (action` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente. Tiene valor por defecto y puede omitirse en la llamada.

**Funcionamiento observable.** encapsula una operación local cuyo contrato se deriva de sus parámetros, retorno y llamadas internas.

### 4.15 `playThrottledInternal` — fun, líneas 158–167

```kotlin
    private fun playThrottledInternal(context: Context, sound: UiSound, minimumIntervalMs: Long, rate: Float, volumeScale: Float) {
        if (!enabled || volume <= 0f) {
            return
        }
        val now = SystemClock.uptimeMillis()
        val previous = synchronized(lastPlayAt) { lastPlayAt[sound] ?: 0L }
        if (now - previous < minimumIntervalMs) return
        synchronized(lastPlayAt) { lastPlayAt[sound] = now }
        playInternal(context = context, sound = sound, theme = theme, volume = (volume * volumeScale).coerceIn(0f, 1f), rate = rate)
    }
```

**Firma/entrada.** `private fun playThrottledInternal(context: Context, sound: UiSound, minimumIntervalMs: Long, rate: Float, volumeScale: Float) {`

**Parámetros.**
- `context: Context` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `sound: UiSound` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `minimumIntervalMs: Long` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `rate: Float` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `volumeScale: Float` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.

**Funcionamiento observable.** encapsula una operación local cuyo contrato se deriva de sus parámetros, retorno y llamadas internas.

**Puntos que no conviene romper:** contiene retornos explícitos; algunos caminos pueden terminar antes de ejecutar el resto del bloque; acota valores antes de usarlos para proteger rangos de UI/rendimiento.

### 4.16 `playInternal` — fun, líneas 168–175

```kotlin
    private fun playInternal(context: Context, sound: UiSound, theme: String, volume: Float, rate: Float = 1f) {
        if (volume <= 0f) {
            return
        }
        val soundPool = ensureInitialized(context)
        val soundId = soundIds[normalizeTheme(theme)]?.get(sound)?: soundIds[DEFAULT_THEME]?.get(sound)?: return
        soundPool.play(soundId, volume, volume, 1, 0, rate.coerceIn(0.5f, 2f))
    }
```

**Firma/entrada.** `private fun playInternal(context: Context, sound: UiSound, theme: String, volume: Float, rate: Float = 1f) {`

**Parámetros.**
- `context: Context` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `sound: UiSound` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `theme: String` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `volume: Float` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `rate: Float = 1f` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente. Tiene valor por defecto y puede omitirse en la llamada.

**Funcionamiento observable.** encapsula una operación local cuyo contrato se deriva de sus parámetros, retorno y llamadas internas.

**Puntos que no conviene romper:** contiene retornos explícitos; algunos caminos pueden terminar antes de ejecutar el resto del bloque; acota valores antes de usarlos para proteger rangos de UI/rendimiento.

### 4.17 `ensureInitialized` — fun, líneas 176–259

```kotlin
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
            pool = newPool
            return newPool
        }
    }
```

**Firma/entrada.** `private fun ensureInitialized(context: Context): SoundPool {`

**Parámetros.**
- `context: Context` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.

**Funcionamiento observable.** interactúa con el subsistema de audio de Android.

**Puntos que no conviene romper:** contiene retornos explícitos; algunos caminos pueden terminar antes de ejecutar el resto del bloque.

### 4.18 `loadTheme` — fun, líneas 260–270

```kotlin
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
```

**Firma/entrada.** `private fun loadTheme(pool: SoundPool, context: Context, theme: String, edit: Int, delete: Int, priority: Int, sliderTick: Int, attachment: Int, toggle: Int) {`

**Parámetros.**
- `pool: SoundPool` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `context: Context` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `theme: String` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `edit: Int` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `delete: Int` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `priority: Int` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `sliderTick: Int` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `attachment: Int` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `toggle: Int` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.

**Funcionamiento observable.** interactúa con el subsistema de audio de Android.

## 5. Variables y propiedades, una por una

Se detectaron **20 declaraciones `val`/`var`** en la forma léxica principal. La tabla explica mutabilidad, tipo visible/inferido, inicialización y función práctica.

| Línea | Variable | Declaración | Explicación detallada |
|---:|---|---|---|
| 32 | `DEFAULT_THEME` | `local/pública por contexto const val DEFAULT_THEME: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `const val DEFAULT_THEME = "classic"` |
| 33 | `availableThemes` | `local/pública por contexto val availableThemes: L` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val availableThemes: List<String> = listOf("classic", "soft", "digital", "glass", "retro", "pop", "mechanical", "bubble", "arcade",` |
| 36 | `pool` | `private var pool: S` | `var` permite sustituir el valor durante la vida del ámbito; admite ausencia (`null`) y por tanto sus consumidores deben manejar la nulabilidad. **Inicialización visible:** `private var pool: SoundPool? = null` |
| 37 | `soundIds` | `private val soundIds: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `private val soundIds = mutableMapOf<String, EnumMap<UiSound, Int>>()` |
| 38 | `lastPlayAt` | `private val lastPlayAt: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `private val lastPlayAt = EnumMap<UiSound, Long>(UiSound::class.java)` |
| 40 | `enabled` | `private var enabled: B` | `var` permite sustituir el valor durante la vida del ámbito. **Inicialización visible:** `private var enabled: Boolean = true` |
| 42 | `volume` | `private var volume: F` | `var` permite sustituir el valor durante la vida del ámbito. **Inicialización visible:** `private var volume: Float = 0.65f` |
| 44 | `theme` | `private var theme: S` | `var` permite sustituir el valor durante la vida del ámbito. **Inicialización visible:** `private var theme: String = DEFAULT_THEME` |
| 76 | `spec` | `local/pública por contexto val spec: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val spec = actionSpec(action)` |
| 85 | `spec` | `local/pública por contexto val spec: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val spec = actionSpec(action)` |
| 116 | `now` | `local/pública por contexto val now: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val now = SystemClock.uptimeMillis()` |
| 117 | `previous` | `local/pública por contexto val previous: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val previous = synchronized(lastPlayAt) {` |
| 162 | `now` | `local/pública por contexto val now: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val now = SystemClock.uptimeMillis()` |
| 163 | `previous` | `local/pública por contexto val previous: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val previous = synchronized(lastPlayAt) { lastPlayAt[sound] ?: 0L }` |
| 172 | `soundPool` | `local/pública por contexto val soundPool: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val soundPool = ensureInitialized(context)` |
| 173 | `soundId` | `local/pública por contexto val soundId: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val soundId = soundIds[normalizeTheme(theme)]?.get(sound)?: soundIds[DEFAULT_THEME]?.get(sound)?: return` |
| 190 | `audioAttributes` | `local/pública por contexto val audioAttributes: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val audioAttributes = AudioAttributes.Builder()` |
| 194 | `newPool` | `local/pública por contexto val newPool: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val newPool = SoundPool.Builder().setMaxStreams(6).setAudioAttributes(audioAttributes).build()` |
| 195 | `appContext` | `local/pública por contexto val appContext: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val appContext = context.applicationContext` |
| 262 | `ids` | `local/pública por contexto val ids: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val ids = EnumMap<UiSound, Int>(UiSound::class.java)` |

## 6. Mapa de ámbitos y bloques `{ ... }`

Se documentan **33 bloques estructurales** relevantes. La profundidad indica cuántos ámbitos externos contienen al bloque.

| Inicio–fin | Prof. | Tipo de bloque | Cabecera | Qué implica |
|---|---:|---|---|---|
| 17–19 | 0 | ámbito/lambda anónima | `enum class UiSound {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 26–29 | 0 | ámbito/lambda anónima | `enum class UiActionSound {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 31–271 | 0 | ámbito/lambda anónima | `object UiSoundPlayer {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 45–45 | 1 | ámbito/lambda anónima | `fun normalizeTheme(value: String): String = value.trim().lowercase().takeIf { it in availableThemes }?: DEFAULT_THEME` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 47–55 | 1 | ámbito/lambda anónima | `hapticIntensityPercent: Float = 55f, hapticStyle: String = UiHapticPlayer.DEFAULT_STYLE) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 52–54 | 2 | condición `if` | `if (enabled) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 56–58 | 1 | ámbito/lambda anónima | `fun preload(context: Context) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 59–65 | 1 | ámbito/lambda anónima | `fun play(context: Context, sound: UiSound) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 61–63 | 2 | condición `if` | `if (!enabled \|\| volume <= 0f) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 71–79 | 1 | ámbito/lambda anónima | `fun playAction(context: Context, action: UiActionSound) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 73–75 | 2 | condición `if` | `if (!enabled \|\| volume <= 0f) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 80–88 | 1 | ámbito/lambda anónima | `fun playActionThrottled(context: Context, action: UiActionSound, minimumIntervalMs: Long = 45L) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 82–84 | 2 | condición `if` | `if (!enabled \|\| volume <= 0f) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 97–103 | 1 | ámbito/lambda anónima | `fun playToggle(context: Context, checked: Boolean, force: Boolean = false) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 99–101 | 2 | condición `if` | `if ((!enabled && !force) \|\| volume <= 0f) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 108–110 | 1 | ámbito/lambda anónima | `fun previewTheme(context: Context, theme: String, sound: UiSound = UiSound.Edit, volumePercent: Float = 65f) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 111–127 | 1 | ámbito/lambda anónima | `fun playThrottled(context: Context, sound: UiSound, minimumIntervalMs: Long = 45L) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 113–115 | 2 | condición `if` | `if (!enabled \|\| volume <= 0f) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 117–119 | 2 | ámbito/lambda anónima | `val previous = synchronized(lastPlayAt) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 120–122 | 2 | condición `if` | `if (now - previous < minimumIntervalMs) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 123–125 | 2 | ámbito/lambda anónima | `synchronized(lastPlayAt) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 129–157 | 1 | selección `when` | `private fun actionSpec(action: UiActionSound): ActionSpec = when (action) {` | Despacha comportamiento según un valor/condición. Las ramas representan contratos mutuamente seleccionados y el `else`, si existe, actúa como fallback. |
| 158–167 | 1 | ámbito/lambda anónima | `private fun playThrottledInternal(context: Context, sound: UiSound, minimumIntervalMs: Long, rate: Float, volumeScale: Float) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 159–161 | 2 | condición `if` | `if (!enabled \|\| volume <= 0f) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 163–163 | 2 | ámbito/lambda anónima | `val previous = synchronized(lastPlayAt) { lastPlayAt[sound] ?: 0L }` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 165–165 | 2 | ámbito/lambda anónima | `synchronized(lastPlayAt) { lastPlayAt[sound] = now }` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 168–175 | 1 | ámbito/lambda anónima | `private fun playInternal(context: Context, sound: UiSound, theme: String, volume: Float, rate: Float = 1f) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 169–171 | 2 | condición `if` | `if (volume <= 0f) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 176–259 | 1 | ámbito/lambda anónima | `private fun ensureInitialized(context: Context): SoundPool {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 177–179 | 2 | ámbito/lambda anónima | `pool?.let {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 180–258 | 2 | ámbito/lambda anónima | `synchronized(this) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 181–183 | 3 | ámbito/lambda anónima | `pool?.let {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 261–270 | 1 | ámbito/lambda anónima | `attachment: Int, toggle: Int) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |

## 7. Side effects, rendimiento y lifecycle

- **Persistencia / base de datos:** presente. Cualquier refactor futuro debe considerar el coste y el lifecycle del recurso, no solo el resultado visual.
- **Audio / vibración:** presente. Cualquier refactor futuro debe considerar el coste y el lifecycle del recurso, no solo el resultado visual.

## 8. Relación con los cambios recientes

Los efectos propios deben seguir una ruta de audio diferente del canal temporalmente silenciado para el teclado. La separación de streams es un requisito funcional: si ambos usaran `STREAM_SYSTEM`, silenciar los clics del IME también silenciaría la respuesta sonora de MyNotes.

## 9. Regla de mantenimiento

Cualquier modificación futura debería actualizar primero el archivo Kotlin real y después regenerar esta documentación. **No debe editarse el código para que coincida con el documento; el documento es el derivado y el código es la fuente de verdad.**

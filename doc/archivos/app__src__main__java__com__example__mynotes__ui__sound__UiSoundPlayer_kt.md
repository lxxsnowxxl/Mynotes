# UiSoundPlayer.kt — documentación del código actual
**Ruta real:** `app/src/main/java/com/example/mynotes/ui/sound/UiSoundPlayer.kt`  **SHA-256:** `d1d18c785aafae6474fb6a96e2e462fff3a0f3fca0fac5b4778f167e9d7d2b1b`  **Líneas:** 402 · **Bytes:** 24314 · **Imports:** 6 · **Declaraciones detectadas:** 24
> Documento generado fuera de `app/` a partir de lectura del código. El fuente es la única fuente de verdad; no se modificó para generar esta documentación.
## 1. Responsabilidad

Motor de efectos de sonido propios de MyNotes.
## 2. Package e imports

Package declarado: `com.example.mynotes.ui.sound`.

### Android / Jetpack / Compose

`android.content.Context`, `android.media.AudioAttributes`, `android.media.SoundPool`, `android.os.SystemClock`

### Proyecto MyNotes

`com.example.mynotes.R`

### Kotlin / Coroutines / Java

`java.util.EnumMap`

## 3. Declaraciones detectadas

| Línea | Tipo | Nombre | Firma/inicio |
|---:|---|---|---|
| 17 | `class` | `UiSound` | `enum class UiSound {` |
| 26 | `class` | `UiActionSound` | `enum class UiActionSound {` |
| 30 | `object` | `UiSoundPlayer` | `` |
| 53 | `fun` | `normalizeTheme` | `fun normalizeTheme(value: String): String = value.trim().lowercase().takeIf { it in availableThemeSet }?: DEFAULT_THEME` |
| 54 | `fun` | `configure` | `fun configure(context: Context, enabled: Boolean, volumePercent: Float, theme: String = DEFAULT_THEME, hapticEnabled: Boolean = true,` |
| 64 | `fun` | `preload` | `fun preload(context: Context) {` |
| 67 | `fun` | `play` | `fun play(context: Context, sound: UiSound) {` |
| 79 | `fun` | `playAction` | `fun playAction(context: Context, action: UiActionSound) {` |
| 94 | `fun` | `playActionAudioOnly` | `fun playActionAudioOnly(context: Context, action: UiActionSound) {` |
| 108 | `fun` | `playActionThrottled` | `fun playActionThrottled(context: Context, action: UiActionSound, minimumIntervalMs: Long = 45L) {` |
| 126 | `fun` | `playToggle` | `fun playToggle(context: Context, checked: Boolean, force: Boolean = false) {` |
| 134 | `fun` | `playToggleAudioOnly` | `fun playToggleAudioOnly(context: Context, checked: Boolean, force: Boolean = false) {` |
| 150 | `fun` | `previewTheme` | `fun previewTheme(context: Context, theme: String, sound: UiSound = UiSound.Edit, volumePercent: Float = 65f) {` |
| 183 | `fun` | `stopThemePreview` | `` |
| 193 | `fun` | `playThrottled` | `fun playThrottled(context: Context, sound: UiSound, minimumIntervalMs: Long = 45L) {` |
| 203 | `class` | `ActionSpec` | `private data class ActionSpec(val sound: UiSound, val rate: Float = 1f, val volumeScale: Float = 1f)` |
| 204 | `class` | `AccentSpec` | `private data class AccentSpec(val sound: UiSound, val rate: Float, val volumeScale: Float)` |
| 250 | `fun` | `actionSpec` | `` |
| 252 | `fun` | `acquireThrottleSlot` | `` |
| 265 | `fun` | `playThrottledInternal` | `private fun playThrottledInternal(context: Context, sound: UiSound, minimumIntervalMs: Long, rate: Float, volumeScale: Float) {` |
| 272 | `fun` | `playInternal` | `private fun playInternal(context: Context, sound: UiSound, theme: String, volume: Float, rate: Float = 1f) {` |
| 280 | `fun` | `playAccentLayer` | `private fun playAccentLayer(context: Context, action: UiActionSound) {` |
| 288 | `fun` | `ensureInitialized` | `` |
| 391 | `fun` | `loadTheme` | `private fun loadTheme(pool: SoundPool, context: Context, theme: String, edit: Int, delete: Int, priority: Int, sliderTick: Int,` |

## 4. Estado, efectos y límites observables

- **Room:** 2 aparición/apariciones.
- **coerce*:** 9 aparición/apariciones.
- **safe calls:** 8 aparición/apariciones.

Estas cifras son indicadores de superficie de cambio, no diagnósticos de error. Cualquier modificación debe preservar contratos de persistencia, lifecycle, límites numéricos y nulabilidad visibles en el fuente.

## 5. Dependencias internas directas

- `com.example.mynotes.R`

## 6. Recursos Android referenciados

- **R.raw:** `ui_arcade_attachment`, `ui_arcade_delete`, `ui_arcade_edit`, `ui_arcade_priority`, `ui_arcade_slider_tick`, `ui_arcade_toggle`, `ui_attachment`, `ui_aurora_attachment`, `ui_aurora_delete`, `ui_aurora_edit`, `ui_aurora_priority`, `ui_aurora_slider_tick`, `ui_aurora_toggle`, `ui_bubble_attachment`, `ui_bubble_delete`, `ui_bubble_edit`, `ui_bubble_priority`, `ui_bubble_slider_tick`, `ui_bubble_toggle`, `ui_camera_attachment`, `ui_camera_delete`, `ui_camera_edit`, `ui_camera_priority`, `ui_camera_slider_tick`, `ui_camera_toggle`, `ui_chime_attachment`, `ui_chime_delete`, `ui_chime_edit`, `ui_chime_priority`, `ui_chime_slider_tick`, `ui_chime_toggle`, `ui_delete`, `ui_digital_attachment`, `ui_digital_delete`, `ui_digital_edit`, `ui_digital_priority`, `ui_digital_slider_tick`, `ui_digital_toggle`, `ui_edit`, `ui_expressive_attachment`, `ui_expressive_delete`, `ui_expressive_edit`, `ui_expressive_priority`, `ui_expressive_slider_tick`, `ui_expressive_toggle`, `ui_fluid_attachment`, `ui_fluid_delete`, `ui_fluid_edit`, `ui_fluid_priority`, `ui_fluid_slider_tick`, `ui_fluid_toggle`, `ui_glass_attachment`, `ui_glass_delete`, `ui_glass_edit`, `ui_glass_priority`, `ui_glass_slider_tick`, `ui_glass_toggle`, `ui_material_attachment`, `ui_material_delete`, `ui_material_edit`, `ui_material_priority`, `ui_material_slider_tick`, `ui_material_toggle`, `ui_mechanical_attachment`, `ui_mechanical_delete`, `ui_mechanical_edit`, `ui_mechanical_priority`, `ui_mechanical_slider_tick`, `ui_mechanical_toggle`, `ui_metal_attachment`, `ui_metal_delete`, `ui_metal_edit`, `ui_metal_priority`, `ui_metal_slider_tick`, `ui_metal_toggle`, `ui_minimal_attachment`, `ui_minimal_delete`, `ui_minimal_edit`, `ui_minimal_priority`, `ui_minimal_slider_tick`, `ui_minimal_toggle`, `ui_neon_attachment`, `ui_neon_delete`, `ui_neon_edit`, `ui_neon_priority`, `ui_neon_slider_tick`, `ui_neon_toggle`, `ui_paper_attachment`, `ui_paper_delete`, `ui_paper_edit`, `ui_paper_priority`, `ui_paper_slider_tick`, `ui_paper_toggle`, `ui_pixel_attachment`, `ui_pixel_delete`, `ui_pixel_edit`, `ui_pixel_priority`, `ui_pixel_slider_tick`, `ui_pixel_toggle`, `ui_pop_attachment`, `ui_pop_delete`, `ui_pop_edit`, `ui_pop_priority`, `ui_pop_slider_tick`, `ui_pop_toggle`, `ui_priority`, `ui_prism_attachment`, `ui_prism_delete`, `ui_prism_edit`, `ui_prism_priority`, `ui_prism_slider_tick`, `ui_prism_toggle`, `ui_pulse_attachment`, `ui_pulse_delete`, `ui_pulse_edit`, `ui_pulse_priority`, `ui_pulse_slider_tick`, `ui_pulse_toggle`, `ui_retro_attachment`, `ui_retro_delete`, `ui_retro_edit`, `ui_retro_priority`, `ui_retro_slider_tick`, `ui_retro_toggle`, `ui_slider_tick`, `ui_soft_attachment`, `ui_soft_delete`, `ui_soft_edit`, `ui_soft_priority`, `ui_soft_slider_tick`, `ui_soft_toggle`, `ui_space_attachment`, `ui_space_delete`, `ui_space_edit`, `ui_space_priority`, `ui_space_slider_tick`, `ui_space_toggle`, `ui_synth_attachment`, `ui_synth_delete`, `ui_synth_edit`, `ui_synth_priority`, `ui_synth_slider_tick`, `ui_synth_toggle`, `ui_toggle`, `ui_typewriter_attachment`, `ui_typewriter_delete`, `ui_typewriter_edit`, `ui_typewriter_priority`, `ui_typewriter_slider_tick`, `ui_typewriter_toggle`, `ui_wood_attachment`, `ui_wood_delete`, `ui_wood_edit`, `ui_wood_priority`, `ui_wood_slider_tick`, `ui_wood_toggle`

## 7. Puntos de revisión al modificarlo

- Validar sus llamadores y el comportamiento visible asociado antes de alterar firmas o valores por defecto.

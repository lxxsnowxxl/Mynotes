# UiHapticPlayer.kt — documentación del código actual
**Ruta real:** `app/src/main/java/com/example/mynotes/ui/sound/UiHapticPlayer.kt`  **SHA-256:** `66e3744c2bf2cfa7731bd414b9c124994343be006c19ca439962d3d3065cf018`  **Líneas:** 215 · **Bytes:** 10702 · **Imports:** 8 · **Declaraciones detectadas:** 15
> Documento generado fuera de `app/` a partir de lectura del código. El fuente es la única fuente de verdad; no se modificó para generar esta documentación.
## 1. Responsabilidad

Motor de efectos hápticos y estilos de vibración.
## 2. Package e imports

Package declarado: `com.example.mynotes.ui.sound`.

### Android / Jetpack / Compose

`android.content.Context`, `android.os.Build`, `android.os.SystemClock`, `android.os.VibrationEffect`, `android.os.Vibrator`, `android.os.VibratorManager`

### Kotlin / Coroutines / Java

`java.util.EnumMap`, `kotlin.math.roundToInt`

## 3. Declaraciones detectadas

| Línea | Tipo | Nombre | Firma/inicio |
|---:|---|---|---|
| 19 | `class` | `UiHaptic` | `enum class UiHaptic {` |
| 22 | `object` | `UiHapticPlayer` | `` |
| 35 | `fun` | `normalizeStyle` | `fun normalizeStyle(value: String): String = value.trim().lowercase().takeIf { it in availableStyleSet }?: DEFAULT_STYLE` |
| 36 | `fun` | `configure` | `fun configure(enabled: Boolean, intensityPercent: Float, style: String = DEFAULT_STYLE) {` |
| 41 | `fun` | `play` | `fun play(context: Context, haptic: UiHaptic, force: Boolean = false) {` |
| 47 | `fun` | `playThrottled` | `fun playThrottled(context: Context, haptic: UiHaptic, minimumIntervalMs: Long = 45L, force: Boolean = false) {` |
| 56 | `fun` | `playToggle` | `fun playToggle(context: Context, checked: Boolean, force: Boolean = false) {` |
| 59 | `fun` | `previewStyle` | `fun previewStyle(context: Context, style: String, intensityPercent: Float = 55f, haptic: UiHaptic = UiHaptic.Confirm) {` |
| 62 | `fun` | `playForSound` | `fun playForSound(context: Context, sound: UiSound, throttled: Boolean = false, minimumIntervalMs: Long = 45L) {` |
| 77 | `fun` | `playForAction` | `fun playForAction(context: Context, action: UiActionSound, throttled: Boolean = false, minimumIntervalMs: Long = 45L) {` |
| 116 | `fun` | `acquireThrottleSlot` | `` |
| 129 | `class` | `HapticPattern` | `` |
| 131 | `fun` | `vibrate` | `private fun vibrate(context: Context, haptic: UiHaptic, style: String, intensity: Float) {` |
| 206 | `fun` | `basePattern` | `` |
| 208 | `fun` | `getVibrator` | `` |

## 4. Estado, efectos y límites observables

- **Room:** 1 aparición/apariciones.
- **try/catch:** 2 aparición/apariciones.
- **coerce*:** 7 aparición/apariciones.
- **safe calls:** 3 aparición/apariciones.

Estas cifras son indicadores de superficie de cambio, no diagnósticos de error. Cualquier modificación debe preservar contratos de persistencia, lifecycle, límites numéricos y nulabilidad visibles en el fuente.

## 5. Dependencias internas directas

No importa directamente otros símbolos `com.example.mynotes.*`.

## 6. Recursos Android referenciados

No se detectaron referencias `R.*` directas.

## 7. Puntos de revisión al modificarlo

- Validar sus llamadores y el comportamiento visible asociado antes de alterar firmas o valores por defecto.

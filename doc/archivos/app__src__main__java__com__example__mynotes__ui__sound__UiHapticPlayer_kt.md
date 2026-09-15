# UiHapticPlayer.kt

**Ruta:** `app/src/main/java/com/example/mynotes/ui/sound/UiHapticPlayer.kt`  
**Paquete:** `com.example.mynotes.ui.sound`  
**Líneas:** 365 → 197 (46.0% menos)

## Responsabilidad

Controlador de retroalimentación háptica. Traduce acciones de UI y ajustes de intensidad/estilo a patrones de vibración compatibles con la versión de Android.

## Papel dentro de la arquitectura

Permite que los composables soliciten una intención háptica sin conocer APIs de Vibrator ni diferencias entre versiones.

## Dependencias y relaciones

**Dependencias internas de MyNotes:** no importa directamente otras clases del paquete de la app.

**Android/Jetpack:** `android.content.Context`, `android.os.Build`, `android.os.SystemClock`, `android.os.VibrationEffect`, `android.os.Vibrator`, `android.os.VibratorManager`.

**Kotlin/Java/corrutinas:** `java.util.EnumMap`, `kotlin.math.roundToInt`.

## Declaraciones importantes detectadas

| Línea | Tipo | Nombre | Firma / declaración | Qué hace |
|---:|---|---|---|---|
| 19 | enum class | `UiHaptic` | `enum class UiHaptic {` | Conjunto cerrado de valores nominales usados por esta parte del sistema. |
| 23 | object | `UiHapticPlayer` | `object UiHapticPlayer {` | Singleton que centraliza funciones/estado compartido sin crear múltiples instancias. |
| 34 | fun | `normalizeStyle` | `fun normalizeStyle(value: String): String = value.trim().lowercase().takeIf { it in availableStyles }?: DEFAULT_STYLE` | Normaliza una entrada para limitarla a los valores/formato admitidos por la aplicación. |
| 35 | fun | `configure` | `fun configure(enabled: Boolean, intensityPercent: Float, style: String = DEFAULT_STYLE) {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 40 | fun | `play` | `fun play(context: Context, haptic: UiHaptic, force: Boolean = false) {` | Inicia o dispara reproducción/feedback asociado a la acción. |
| 46 | fun | `playThrottled` | `fun playThrottled(context: Context, haptic: UiHaptic, minimumIntervalMs: Long = 45L, force: Boolean = false) {` | Inicia o dispara reproducción/feedback asociado a la acción. |
| 60 | fun | `playToggle` | `fun playToggle(context: Context, checked: Boolean, force: Boolean = false) {` | Inicia o dispara reproducción/feedback asociado a la acción. |
| 63 | fun | `previewStyle` | `fun previewStyle(context: Context, style: String, intensityPercent: Float = 55f, haptic: UiHaptic = UiHaptic.Confirm) {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 66 | fun | `playForSound` | `fun playForSound(context: Context, sound: UiSound, throttled: Boolean = false, minimumIntervalMs: Long = 45L) {` | Inicia o dispara reproducción/feedback asociado a la acción. |
| 81 | fun | `playForAction` | `fun playForAction(context: Context, action: UiActionSound, throttled: Boolean = false, minimumIntervalMs: Long = 45L) {` | Inicia o dispara reproducción/feedback asociado a la acción. |
| 120 | data class | `HapticPattern` | `private data class HapticPattern(val timings: LongArray, val amplitudes: IntArray)` | Modelo de datos que agrupa valores relacionados con esta responsabilidad. |
| 121 | fun | `vibrate` | `private fun vibrate(context: Context, haptic: UiHaptic, style: String, intensity: Float) {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 173 | fun | `basePattern` | `private fun basePattern(style: String): HapticPattern = when (style) {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 191 | fun | `getVibrator` | `private fun getVibrator(context: Context): Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |

## Cómo leer este archivo

1. Empieza por las declaraciones públicas o composables: representan el contrato que usa el resto del proyecto. 2. Después revisa las funciones privadas: normalmente encapsulan normalización, cálculo, rendering o acceso a plataforma. 3. Los comentarios existentes se conservaron durante la compactación y explican decisiones específicas allí donde el código necesita contexto. 4. Consulta las dependencias internas anteriores para seguir el flujo hacia la capa que consume o persiste el resultado.

## Garantía de la compactación

En este archivo no se cambiaron identificadores, literales, operadores, comentarios, llamadas ni orden de tokens. La reducción proviene exclusivamente de compactar espacios/saltos de línea seguros. El proyecto fue comparado mediante el lexer de Kotlin y validado sintácticamente con PSI después del cambio.

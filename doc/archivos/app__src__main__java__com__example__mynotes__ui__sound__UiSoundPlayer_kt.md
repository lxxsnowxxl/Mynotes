# UiSoundPlayer.kt

**Ruta:** `app/src/main/java/com/example/mynotes/ui/sound/UiSoundPlayer.kt`  
**Paquete:** `com.example.mynotes.ui.sound`  
**Líneas:** 704 → 263 (62.6% menos)

## Responsabilidad

Controlador de efectos de sonido de la interfaz. Gestiona temas/paquetes, volumen, carga/reutilización y reproducción asociada a acciones.

## Papel dentro de la arquitectura

Evita crear reproductores desde cada composable y mantiene una política coherente para sonidos de toque, selección, menú y otras acciones.

## Dependencias y relaciones

**Dependencias internas de MyNotes:** `com.example.mynotes.R`.

**Android/Jetpack:** `android.content.Context`, `android.media.AudioAttributes`, `android.media.SoundPool`, `android.os.SystemClock`.

**Kotlin/Java/corrutinas:** `java.util.EnumMap`.

## Declaraciones importantes detectadas

| Línea | Tipo | Nombre | Firma / declaración | Qué hace |
|---:|---|---|---|---|
| 17 | enum class | `UiSound` | `enum class UiSound {` | Conjunto cerrado de valores nominales usados por esta parte del sistema. |
| 26 | enum class | `UiActionSound` | `enum class UiActionSound {` | Conjunto cerrado de valores nominales usados por esta parte del sistema. |
| 31 | object | `UiSoundPlayer` | `object UiSoundPlayer {` | Singleton que centraliza funciones/estado compartido sin crear múltiples instancias. |
| 45 | fun | `normalizeTheme` | `fun normalizeTheme(value: String): String = value.trim().lowercase().takeIf { it in availableThemes }?: DEFAULT_THEME` | Normaliza una entrada para limitarla a los valores/formato admitidos por la aplicación. |
| 46 | fun | `configure` | `fun configure(context: Context, enabled: Boolean, volumePercent: Float, theme: String = DEFAULT_THEME, hapticEnabled: Boolean = true,` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 56 | fun | `preload` | `fun preload(context: Context) {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 59 | fun | `play` | `fun play(context: Context, sound: UiSound) {` | Inicia o dispara reproducción/feedback asociado a la acción. |
| 71 | fun | `playAction` | `fun playAction(context: Context, action: UiActionSound) {` | Inicia o dispara reproducción/feedback asociado a la acción. |
| 80 | fun | `playActionThrottled` | `fun playActionThrottled(context: Context, action: UiActionSound, minimumIntervalMs: Long = 45L) {` | Inicia o dispara reproducción/feedback asociado a la acción. |
| 97 | fun | `playToggle` | `fun playToggle(context: Context, checked: Boolean, force: Boolean = false) {` | Inicia o dispara reproducción/feedback asociado a la acción. |
| 108 | fun | `previewTheme` | `fun previewTheme(context: Context, theme: String, sound: UiSound = UiSound.Edit, volumePercent: Float = 65f) {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 111 | fun | `playThrottled` | `fun playThrottled(context: Context, sound: UiSound, minimumIntervalMs: Long = 45L) {` | Inicia o dispara reproducción/feedback asociado a la acción. |
| 128 | data class | `ActionSpec` | `private data class ActionSpec(val sound: UiSound, val rate: Float = 1f, val volumeScale: Float = 1f)` | Modelo de datos que agrupa valores relacionados con esta responsabilidad. |
| 129 | fun | `actionSpec` | `private fun actionSpec(action: UiActionSound): ActionSpec = when (action) {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 158 | fun | `playThrottledInternal` | `private fun playThrottledInternal(context: Context, sound: UiSound, minimumIntervalMs: Long, rate: Float, volumeScale: Float) {` | Inicia o dispara reproducción/feedback asociado a la acción. |
| 168 | fun | `playInternal` | `private fun playInternal(context: Context, sound: UiSound, theme: String, volume: Float, rate: Float = 1f) {` | Inicia o dispara reproducción/feedback asociado a la acción. |
| 176 | fun | `ensureInitialized` | `private fun ensureInitialized(context: Context): SoundPool {` | Comprueba una condición y produce un resultado que cumple los requisitos esperados. |
| 252 | fun | `loadTheme` | `private fun loadTheme(pool: SoundPool, context: Context, theme: String, edit: Int, delete: Int, priority: Int, sliderTick: Int,` | Carga o prepara datos/recursos necesarios, normalmente aplicando caché o trabajo de I/O cuando corresponde. |

## Cómo leer este archivo

1. Empieza por las declaraciones públicas o composables: representan el contrato que usa el resto del proyecto. 2. Después revisa las funciones privadas: normalmente encapsulan normalización, cálculo, rendering o acceso a plataforma. 3. Los comentarios existentes se conservaron durante la compactación y explican decisiones específicas allí donde el código necesita contexto. 4. Consulta las dependencias internas anteriores para seguir el flujo hacia la capa que consume o persiste el resultado.

## Garantía de la compactación

En este archivo no se cambiaron identificadores, literales, operadores, comentarios, llamadas ni orden de tokens. La reducción proviene exclusivamente de compactar espacios/saltos de línea seguros. El proyecto fue comparado mediante el lexer de Kotlin y validado sintácticamente con PSI después del cambio.

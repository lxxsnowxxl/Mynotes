# UiHapticPlayer.kt — explicación completa del código

**Ruta:** `app/src/main/java/com/example/mynotes/ui/sound/UiHapticPlayer.kt`  
**SHA-256:** `66e3744c2bf2cfa7731bd414b9c124994343be006c19ca439962d3d3065cf018`  
**Líneas:** 216  
**Package:** `com.example.mynotes.ui.sound`

## 1. Para qué existe este archivo

Motor central de vibraciones de interfaz con estilos, intensidad, patrones y throttling.

## 2. Tipos/clases declarados

- Línea **19** — `enum  class UiHaptic`.
- Línea **23** — `object UiHapticPlayer`.
- Línea **130** — `private data  class HapticPattern`.

## 3. Estado, constantes y valores importantes

- **`DEFAULT_STYLE`** (línea 24) inicia con `"soft"`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`availableStyles`** (línea 25) inicia con `listOf("soft"`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`availableStyleSet`** (línea 27) inicia con `availableStyles.toHashSet(`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`enabled`** (línea 29) inicia con `true`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`intensity`** (línea 31) inicia con `0.55f`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`style`** (línea 33) inicia con `DEFAULT_STYLE`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`lastPlayAt`** (línea 34) inicia con `EnumMap<UiHaptic`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`haptic`** (línea 63) inicia con `when (sound`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`actionHaptics`** (línea 86) inicia con `EnumMap<UiActionSound`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`now`** (línea 118) inicia con `SystemClock.uptimeMillis(`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`previous`** (línea 120) inicia con `lastPlayAt[haptic] ?: 0L`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`vibrator`** (línea 135) inicia con `getVibrator(context`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`eventScale`** (línea 139) inicia con `when (haptic`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`pattern`** (línea 149) inicia con `basePattern(normalizeStyle(style`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`firstAmp`** (línea 153) inicia con `pattern.amplitudes.firstOrNull { it > 0 }?: 120`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`shortenedTimings`** (línea 157) inicia con `LongArray(pattern.timings.size`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`scaledAmplitudes`** (línea 162) inicia con `IntArray(pattern.amplitudes.size`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`amplitude`** (línea 163) inicia con `pattern.amplitudes[index]`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`effect`** (línea 168) inicia con `if (pattern.timings.size <= 2`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`basePatterns`** (línea 187) inicia con `mapOf(`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`tickTimings`** (línea 205) inicia con `longArrayOf(0L`. Su valor se usa dentro de la responsabilidad descrita para este archivo.

## 4. Funciones y flujo, una por una

### `normalizeStyle` — líneas 35–35

**Firma:** `fun normalizeStyle(value: String): String`

Normaliza una cadena/valor externo al conjunto de opciones admitidas por MyNotes y devuelve un fallback estable si el valor no es reconocido.

**Entradas:**
- `value: String`

**Salida:** String.

**Decisiones y protecciones visibles:**
- Usa operadores Elvis/fallback para datos nulos o ausentes.

**Operaciones/funciones que coordina:** `trim`, `lowercase`.

### `configure` — líneas 36–40

**Firma:** `fun configure(enabled: Boolean, intensityPercent: Float, style: String = DEFAULT_STYLE)`

Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.

**Entradas:**
- `enabled: Boolean`
- `intensityPercent: Float`
- `style: String = DEFAULT_STYLE`

**Salida:** Unit o inferido por Kotlin.

**Decisiones y protecciones visibles:**
- Limita valores con `coerce*` para evitar estados fuera de rango.

**Operaciones/funciones que coordina:** `coerceIn`, `normalizeStyle`.

### `play` — líneas 41–46

**Firma:** `fun play(context: Context, haptic: UiHaptic, force: Boolean = false)`

Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.

**Entradas:**
- `context: Context`
- `haptic: UiHaptic`
- `force: Boolean = false`

**Salida:** Unit o inferido por Kotlin.

**Decisiones y protecciones visibles:**
- Contiene decisiones condicionales (`if`) para seleccionar comportamiento.

**Operaciones/funciones que coordina:** `vibrate`.

### `playThrottled` — líneas 47–55

**Firma:** `fun playThrottled(context: Context, haptic: UiHaptic, minimumIntervalMs: Long = 45L, force: Boolean = false)`

Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.

**Entradas:**
- `context: Context`
- `haptic: UiHaptic`
- `minimumIntervalMs: Long = 45L`
- `force: Boolean = false`

**Salida:** Unit o inferido por Kotlin.

**Decisiones y protecciones visibles:**
- Contiene decisiones condicionales (`if`) para seleccionar comportamiento.

**Operaciones/funciones que coordina:** `acquireThrottleSlot`, `play`.

### `playToggle` — líneas 56–58

**Firma:** `fun playToggle(context: Context, checked: Boolean, force: Boolean = false)`

Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.

**Entradas:**
- `context: Context`
- `checked: Boolean`
- `force: Boolean = false`

**Salida:** Unit o inferido por Kotlin.

**Decisiones y protecciones visibles:**
- Contiene decisiones condicionales (`if`) para seleccionar comportamiento.

**Operaciones/funciones que coordina:** `play`.

### `previewStyle` — líneas 59–61

**Firma:** `fun previewStyle(context: Context, style: String, intensityPercent: Float = 55f, haptic: UiHaptic = UiHaptic.Confirm)`

Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.

**Entradas:**
- `context: Context`
- `style: String`
- `intensityPercent: Float = 55f`
- `haptic: UiHaptic = UiHaptic.Confirm`

**Salida:** Unit o inferido por Kotlin.

**Decisiones y protecciones visibles:**
- Limita valores con `coerce*` para evitar estados fuera de rango.

**Operaciones/funciones que coordina:** `vibrate`, `normalizeStyle`, `coerceIn`.

### `playForSound` — líneas 62–76

**Firma:** `fun playForSound(context: Context, sound: UiSound, throttled: Boolean = false, minimumIntervalMs: Long = 45L)`

Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.

**Entradas:**
- `context: Context`
- `sound: UiSound`
- `throttled: Boolean = false`
- `minimumIntervalMs: Long = 45L`

**Salida:** Unit o inferido por Kotlin.

**Decisiones y protecciones visibles:**
- Contiene decisiones condicionales (`if`) para seleccionar comportamiento.
- Usa `when` para mapear estados/tipos/opciones.

**Operaciones/funciones que coordina:** `playThrottled`, `play`.

### `playForAction` — líneas 77–85

**Firma:** `fun playForAction(context: Context, action: UiActionSound, throttled: Boolean = false, minimumIntervalMs: Long = 45L)`

Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.

**Entradas:**
- `context: Context`
- `action: UiActionSound`
- `throttled: Boolean = false`
- `minimumIntervalMs: Long = 45L`

**Salida:** Unit o inferido por Kotlin.

**Decisiones y protecciones visibles:**
- Usa operadores Elvis/fallback para datos nulos o ausentes.
- Contiene decisiones condicionales (`if`) para seleccionar comportamiento.

**Operaciones/funciones que coordina:** `playThrottled`, `play`.

### `acquireThrottleSlot` — líneas 117–128

**Firma:** `private fun acquireThrottleSlot(haptic: UiHaptic, minimumIntervalMs: Long): Boolean`

Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.

**Entradas:**
- `haptic: UiHaptic`
- `minimumIntervalMs: Long`

**Salida:** Boolean.

**Decisiones y protecciones visibles:**
- Usa operadores Elvis/fallback para datos nulos o ausentes.
- Contiene decisiones condicionales (`if`) para seleccionar comportamiento.

**Operaciones/funciones que coordina:** `uptimeMillis`, `synchronized`.

### `vibrate` — líneas 131–182

**Firma:** `private fun vibrate(context: Context, haptic: UiHaptic, style: String, intensity: Float)`

Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.

**Entradas:**
- `context: Context`
- `haptic: UiHaptic`
- `style: String`
- `intensity: Float`

**Salida:** Unit o inferido por Kotlin.

**Efectos/APIs observados en el cuerpo:**
- Produce feedback háptico.

**Decisiones y protecciones visibles:**
- Limita valores con `coerce*` para evitar estados fuera de rango.
- Contiene manejo de fallos/excepciones y una ruta de recuperación/fallback.
- Usa operadores Elvis/fallback para datos nulos o ausentes.
- Contiene decisiones condicionales (`if`) para seleccionar comportamiento.
- Usa `when` para mapear estados/tipos/opciones.

**Operaciones/funciones que coordina:** `getVibrator`, `hasVibrator`, `basePattern`, `normalizeStyle`, `HapticPattern`, `intArrayOf`, `LongArray`, `roundToInt`, `coerceAtLeast`, `toLong`, `IntArray`, `else`, `coerceIn`, `createOneShot`, `lastOrNull`, `createWaveform`, `vibrate`, `Suppress`.

### `basePattern` — líneas 207–207

**Firma:** `private fun basePattern(style: String): HapticPattern`

Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.

**Entradas:**
- `style: String`

**Salida:** HapticPattern.

**Decisiones y protecciones visibles:**
- Usa operadores Elvis/fallback para datos nulos o ausentes.

**Operaciones/funciones que coordina:** `getValue`.

### `getVibrator` — líneas 209–209

**Firma:** `private fun getVibrator(context: Context): Vibrator?`

Obtiene el dato solicitado desde la fuente o estructura que maneja este archivo, sin cambiar el contrato público del resto del módulo.

**Entradas:**
- `context: Context`

**Salida:** Vibrator?.

**Decisiones y protecciones visibles:**
- Contiene decisiones condicionales (`if`) para seleccionar comportamiento.

## 5. Cómo se conecta con el resto de MyNotes

- No importa directamente otro componente `com.example.mynotes`; funciona como modelo/utilidad base o mediante APIs Android/Jetpack.

## 6. Recursos Android que utiliza

- No se detectaron referencias directas `R.*` en este archivo.

## 7. Tecnologías y efectos relevantes

- Produce feedback háptico.

## 8. Lectura práctica del flujo

Una forma útil de seguir este archivo en el depurador es recorrer estas operaciones en este orden aproximado:
1. `normalizeStyle` — Normaliza una cadena/valor externo al conjunto de opciones admitidas por MyNotes y devuelve un fallback estable si el valor no es reconocido.
2. `configure` — Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.
3. `play` — Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.
4. `playThrottled` — Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.
5. `playToggle` — Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.
6. `previewStyle` — Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.
7. `playForSound` — Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.
8. `playForAction` — Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.

## 9. Qué no debe romperse al modificarlo

- Mantener las firmas públicas/callbacks que usan los archivos listados en la sección de integración.

## 10. Resumen en lenguaje sencillo

En términos simples: Motor central de vibraciones de interfaz con estilos, intensidad, patrones y throttling. La sección función por función anterior describe qué entra, qué devuelve y qué efectos produce cada operación detectada en el fuente actual.

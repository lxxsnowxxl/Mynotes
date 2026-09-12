# UiHapticPlayer.kt — documentación exhaustiva por bloques

**Ruta de código real:** `app/src/main/java/com/example/mynotes/ui/sound/UiHapticPlayer.kt`  
**SHA-256 del archivo ejecutable sin tocar:** `b3a702ce9a899a616fa7dbee4d17b07285dc91c2571c0ea20a8145a389506314`  
**Líneas del código real:** 197

> **Garantía:** esta documentación se genera en una carpeta paralela. El archivo Kotlin/Kotlin DSL anterior no se modifica. Los fragmentos mostrados abajo son copias de lectura; la aplicación compila usando únicamente el archivo original de `app/...`.

## 1. Papel del archivo

Controlador de retroalimentación háptica. Traduce acciones de UI y ajustes de intensidad/estilo a patrones de vibración compatibles con la versión de Android.

**Arquitectura.** Permite que los composables soliciten una intención háptica sin conocer APIs de Vibrator ni diferencias entre versiones.

**Flujo general.** El flujo se describe declaración por declaración en este documento.

## 2. Package e imports

El `package` es `com.example.mynotes.ui.sound`. Determina el namespace lógico del archivo, resolución de visibilidad `internal`/package-related tooling y la ruta conceptual desde la que otras clases lo importan.

El archivo declara **8 imports**. Cada import evita usar el nombre totalmente calificado en el cuerpo y, además, revela las dependencias técnicas del bloque:

**Android/plataforma:** `android.content.Context`, `android.os.Build`, `android.os.SystemClock`, `android.os.VibrationEffect`, `android.os.Vibrator`, `android.os.VibratorManager`.

**Kotlin/corrutinas/Java:** `java.util.EnumMap`, `kotlin.math.roundToInt`.

## 3. Restricciones e invariantes visibles en el archivo

- **Nulabilidad segura (3 aparición/apariciones):** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`.
- **Fallback nulo (7 aparición/apariciones):** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula.
- **Límite numérico (7 aparición/apariciones):** Se fuerza el valor a un intervalo o umbral para impedir que salgan valores fuera de los límites esperados.
- **Filtro condicional (1 aparición/apariciones):** `takeIf` conserva el valor únicamente cuando el predicado se cumple; en caso contrario lo convierte en `null`.
- **Guardia de API (2 aparición/apariciones):** Se condiciona una API según la versión de Android para no ejecutar llamadas incompatibles en dispositivos antiguos.

## 4. Bloques de código, uno por uno

### 4.1 `UiHaptic` — class, líneas 19–21

```kotlin
enum class UiHaptic {
    Tap, Tick, ToggleOn, ToggleOff, Confirm, Warning, Attachment, Selection
}
```

#### Qué hace y por qué existe

Controlador de retroalimentación háptica. Traduce acciones de UI y ajustes de intensidad/estilo a patrones de vibración compatibles con la versión de Android.

#### Contrato de la declaración


#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

No se detectaron llamadas de función relevantes fuera de la propia estructura de la declaración.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.2 `UiHapticPlayer` — object, líneas 23–197

```kotlin
object UiHapticPlayer {
    // … el cuerpo completo permanece en el archivo real; sus miembros se documentan individualmente abajo …
}
```

#### Qué hace y por qué existe

Controlador de retroalimentación háptica. Traduce acciones de UI y ajustes de intensidad/estilo a patrones de vibración compatibles con la versión de Android.

#### Contrato de la declaración


#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 24 | `val DEFAULT_STYLE` | `inferido` | `"soft"` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Está marcada `const`, por lo que es una constante de tiempo de compilación y solo puede usar un tipo/valor admitido por Kotlin para constantes. |
| 25 | `val availableStyles` | `List<String>` | `listOf("soft", "crisp", "deep", "double", "pulse", "stepped", "mechanical", "minimal", "triple",` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo declarado es `List<String>`. No declara nulabilidad explícita. |
| 28 | `var enabled` | `Boolean` | `true` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo declarado es `Boolean`. No declara nulabilidad explícita. El nombre indica un estado de decisión/visibilidad/selección usado para controlar una rama de comportamiento o presentación. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 30 | `var intensity` | `Float` | `0.55f` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo declarado es `Float`. No declara nulabilidad explícita. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 32 | `var style` | `String` | `DEFAULT_STYLE` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo declarado es `String`. No declara nulabilidad explícita. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 33 | `val lastPlayAt` | `inferido` | `EnumMap<UiHaptic, Long>(UiHaptic::class.java)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 50 | `val now` | `inferido` | `SystemClock.uptimeMillis()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 51 | `val previous` | `inferido` | `synchronized(lastPlayAt) { lastPlayAt[haptic] ?: 0L }` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 67 | `val haptic` | `inferido` | `when (sound) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 84 | `val haptic` | `UiHaptic?` | `when (action) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo declarado es `UiHaptic?`. Admite `null`, así que los consumidores deben contemplar ausencia de valor. |
| 125 | `val vibrator` | `inferido` | `getVibrator(context) ?: return` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 129 | `val eventScale` | `inferido` | `when (haptic) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 139 | `var pattern` | `inferido` | `basePattern(normalizeStyle(style))` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 143 | `val firstAmp` | `inferido` | `pattern.amplitudes.firstOrNull { it > 0 }?: 120` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 150 | `val scaledAmplitudes` | `inferido` | `pattern.amplitudes.map { amplitude -> if (amplitude == 0) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 158 | `val effect` | `inferido` | `if (pattern.timings.size <= 2) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 41 | `if ((!enabled && !force) \|\| intensity <= 0f) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 42 | `return` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 47 | `if ((!enabled && !force) \|\| intensity <= 0f) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 48 | `return` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 52 | `if (now - previous < minimumIntervalMs) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 53 | `return` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 68 | `UiSound.Edit -> UiHaptic.Tap` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 69 | `UiSound.Delete -> UiHaptic.Warning` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 70 | `UiSound.Priority -> UiHaptic.Selection` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 71 | `UiSound.SliderTick -> UiHaptic.Tick` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 72 | `UiSound.Attachment -> UiHaptic.Attachment` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 73 | `UiSound.Toggle -> UiHaptic.Selection` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 75 | `if (throttled) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 85 | `UiActionSound.TextInput -> null` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 86 | `UiActionSound.Open -> UiHaptic.Tap` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 87 | `UiActionSound.Back -> UiHaptic.Selection` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 88 | `UiActionSound.Save -> UiHaptic.Confirm` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 89 | `UiActionSound.Search -> UiHaptic.Tick` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 90 | `UiActionSound.Menu -> UiHaptic.Selection` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 91 | `UiActionSound.Select -> UiHaptic.Selection` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 92 | `UiActionSound.Favorite -> UiHaptic.Confirm` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 93 | `UiActionSound.Pin -> UiHaptic.Selection` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 94 | `UiActionSound.Share -> UiHaptic.Tap` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 95 | `UiActionSound.Move -> UiHaptic.Selection` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 96 | `UiActionSound.Color -> UiHaptic.Selection` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 97 | `UiActionSound.Category -> UiHaptic.Selection` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 98 | `UiActionSound.Add -> UiHaptic.Attachment` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 99 | `UiActionSound.Confirm -> UiHaptic.Confirm` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 100 | `UiActionSound.Cancel -> UiHaptic.Selection` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 101 | `UiActionSound.Navigation -> UiHaptic.Selection` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 102 | `UiActionSound.Sort -> UiHaptic.Tick` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 103 | `UiActionSound.Layout -> UiHaptic.Selection` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 104 | `UiActionSound.Language -> UiHaptic.Selection` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 105 | `UiActionSound.Theme -> UiHaptic.Selection` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 106 | `UiActionSound.Link -> UiHaptic.Tap` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 107 | `UiActionSound.PlayPause -> UiHaptic.Selection` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 108 | `UiActionSound.Zoom -> UiHaptic.Tick` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 109 | `UiActionSound.Backup -> UiHaptic.Attachment` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 110 | `UiActionSound.Restore -> UiHaptic.Confirm` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 111 | `UiActionSound.Settings -> UiHaptic.Selection` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 114 | `if (throttled) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 122 | `if (intensity <= 0f) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 123 | `return` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 126 | `if (!vibrator.hasVibrator()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 127 | `return` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 130 | `UiHaptic.Tick -> 0.46f` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 131 | `UiHaptic.Selection -> 0.68f` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 132 | `UiHaptic.Tap -> 0.76f` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 133 | `UiHaptic.ToggleOn -> 0.82f` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 134 | `UiHaptic.ToggleOff -> 0.64f` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 135 | `UiHaptic.Attachment -> 0.88f` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 136 | `UiHaptic.Confirm -> 0.96f` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 137 | `UiHaptic.Warning -> 1.00f` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 142 | `if (haptic == UiHaptic.Tick) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 146 | `if (haptic == UiHaptic.ToggleOff) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 156 | `try {` | Frontera de manejo de errores: agrupa operaciones que pueden lanzar excepciones para permitir recuperación o limpieza controlada. |
| 157 | `if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 174 | `"crisp" -> HapticPattern(longArrayOf(0L, 9L), intArrayOf(0, 220))` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 175 | `"deep" -> HapticPattern(longArrayOf(0L, 24L), intArrayOf(0, 175))` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 176 | `"double" -> HapticPattern(longArrayOf(0L, 10L, 34L, 12L), intArrayOf(0, 190, 0, 145))` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 177 | `"pulse" -> HapticPattern(longArrayOf(0L, 15L, 22L, 22L), intArrayOf(0, 130, 0, 205))` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 178 | `"stepped" -> HapticPattern(longArrayOf(0L, 6L, 10L, 8L, 10L, 11L), intArrayOf(0, 85, 0, 140, 0, 210))` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 179 | `"mechanical" -> HapticPattern(longArrayOf(0L, 5L, 8L, 14L), intArrayOf(0, 225, 0, 150))` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 180 | `"minimal" -> HapticPattern(longArrayOf(0L, 5L), intArrayOf(0, 125))` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 181 | `"triple" -> HapticPattern(longArrayOf(0L, 6L, 10L, 7L, 10L, 9L), intArrayOf(0, 170, 0, 200, 0, 230))` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 182 | `"ripple" -> HapticPattern(longArrayOf(0L, 5L, 8L, 7L, 10L, 10L), intArrayOf(0, 225, 0, 155, 0, 90))` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 183 | `"heartbeat" -> HapticPattern(longArrayOf(0L, 11L, 38L, 20L), intArrayOf(0, 135, 0, 230))` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 184 | `"snap" -> HapticPattern(longArrayOf(0L, 4L, 5L, 11L), intArrayOf(0, 255, 0, 120))` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 185 | `"wave" -> HapticPattern(longArrayOf(0L, 10L, 8L, 14L, 8L, 8L), intArrayOf(0, 105, 0, 225, 0, 125))` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 186 | `"heavy" -> HapticPattern(longArrayOf(0L, 32L), intArrayOf(0, 235))` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 187 | `"spring" -> HapticPattern(longArrayOf(0L, 7L, 12L, 5L, 14L, 4L), intArrayOf(0, 235, 0, 150, 0, 85))` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 188 | `"echo" -> HapticPattern(longArrayOf(0L, 10L, 24L, 7L, 22L, 5L), intArrayOf(0, 220, 0, 135, 0, 70))` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 189 | `else -> HapticPattern(longArrayOf(0L, 12L), intArrayOf(0, 115))` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 3.
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 7.
- **Límite numérico:** Se fuerza el valor a un intervalo o umbral para impedir que salgan valores fuera de los límites esperados. Apariciones en este bloque: 7.
- **Filtro condicional:** `takeIf` conserva el valor únicamente cuando el predicado se cumple; en caso contrario lo convierte en `null`. Apariciones en este bloque: 1.
- **Guardia de API:** Se condiciona una API según la versión de Android para no ejecutar llamadas incompatibles en dispositivos antiguos. Apariciones en este bloque: 2.

#### Efectos secundarios y recursos

- **Sonido/háptica:** Produce feedback perceptible para el usuario; debe ejecutarse en respuesta a la interacción/estado correspondiente.
- **Mutación de colección/estado compartido:** Modifica una colección/mapa/estructura existente; el cambio permanece visible para quienes compartan esa misma instancia.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `listOf`, `value.trim`, `lowercase`, `coerceIn`, `normalizeStyle`, `vibrate`, `SystemClock.uptimeMillis`, `synchronized`, `play`, `playThrottled`, `getVibrator`, `vibrator.hasVibrator`, `basePattern`, `HapticPattern`, `longArrayOf`, `intArrayOf`, `roundToInt`, `coerceAtLeast`, `toLong`, `toLongArray`, `toIntArray`, `VibrationEffect.createOneShot`, `pattern.timings.lastOrNull`, `scaledAmplitudes.lastOrNull`, `VibrationEffect.createWaveform`, `vibrator.vibrate`, `Suppress`, `pattern.timings.sum`, `context.getSystemService`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.
- Conservar las guardas de versión antes de APIs que no existen en Android antiguos.

### 4.3 `normalizeStyle` — fun, líneas 34–39

```kotlin
    fun normalizeStyle(value: String): String = value.trim().lowercase().takeIf { it in availableStyles }?: DEFAULT_STYLE
    fun configure(enabled: Boolean, intensityPercent: Float, style: String = DEFAULT_STYLE) {
        this.enabled = enabled
        this.intensity = (intensityPercent / 100f).coerceIn(0f, 1f)
        this.style = normalizeStyle(style)
    }
```

#### Qué hace y por qué existe

Normaliza una entrada a un conjunto de valores aceptados, proporcionando una salida estable aunque el dato original venga con variantes no canónicas.

#### Contrato de la declaración

**Parámetros:**

- `value: String` — `value` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `String`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.


#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

**Restricciones concretas que aparecen en este bloque:**
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 1.
- **Límite numérico:** Se fuerza el valor a un intervalo o umbral para impedir que salgan valores fuera de los límites esperados. Apariciones en este bloque: 1.
- **Filtro condicional:** `takeIf` conserva el valor únicamente cuando el predicado se cumple; en caso contrario lo convierte en `null`. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `value.trim`, `lowercase`, `coerceIn`, `normalizeStyle`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.4 `configure` — fun, líneas 35–39

```kotlin
    fun configure(enabled: Boolean, intensityPercent: Float, style: String = DEFAULT_STYLE) {
        this.enabled = enabled
        this.intensity = (intensityPercent / 100f).coerceIn(0f, 1f)
        this.style = normalizeStyle(style)
    }
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `enabled: Boolean` — `enabled` recibe un valor de tipo `Boolean`. El contrato no marca este parámetro como anulable.
- `intensityPercent: Float` — `intensityPercent` recibe un valor de tipo `Float`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.
- `style: String = DEFAULT_STYLE` — `style` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable. Tiene valor por defecto `DEFAULT_STYLE`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.


#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

**Restricciones concretas que aparecen en este bloque:**
- **Límite numérico:** Se fuerza el valor a un intervalo o umbral para impedir que salgan valores fuera de los límites esperados. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `coerceIn`, `normalizeStyle`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.5 `play` — fun, líneas 40–45

```kotlin
    fun play(context: Context, haptic: UiHaptic, force: Boolean = false) {
        if ((!enabled && !force) || intensity <= 0f) {
            return
        }
        vibrate(context = context, haptic = haptic, style = style, intensity = intensity)
    }
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `context: Context` — `context` recibe un valor de tipo `Context`. El contrato no marca este parámetro como anulable. Da acceso a recursos y APIs Android; su vida útil debe corresponder con el uso realizado.
- `haptic: UiHaptic` — `haptic` recibe un valor de tipo `UiHaptic`. El contrato no marca este parámetro como anulable.
- `force: Boolean = false` — `force` recibe un valor de tipo `Boolean`. El contrato no marca este parámetro como anulable. Tiene valor por defecto `false`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.


#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 41 | `if ((!enabled && !force) \|\| intensity <= 0f) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 42 | `return` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `vibrate`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.6 `playThrottled` — fun, líneas 46–59

```kotlin
    fun playThrottled(context: Context, haptic: UiHaptic, minimumIntervalMs: Long = 45L, force: Boolean = false) {
        if ((!enabled && !force) || intensity <= 0f) {
            return
        }
        val now = SystemClock.uptimeMillis()
        val previous = synchronized(lastPlayAt) { lastPlayAt[haptic] ?: 0L }
        if (now - previous < minimumIntervalMs) {
            return
        }
        synchronized(lastPlayAt) {
            lastPlayAt[haptic] = now
        }
        play(context = context, haptic = haptic, force = force)
    }
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `context: Context` — `context` recibe un valor de tipo `Context`. El contrato no marca este parámetro como anulable. Da acceso a recursos y APIs Android; su vida útil debe corresponder con el uso realizado.
- `haptic: UiHaptic` — `haptic` recibe un valor de tipo `UiHaptic`. El contrato no marca este parámetro como anulable.
- `minimumIntervalMs: Long = 45L` — `minimumIntervalMs` recibe un valor de tipo `Long`. El contrato no marca este parámetro como anulable. Tiene valor por defecto `45L`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.
- `force: Boolean = false` — `force` recibe un valor de tipo `Boolean`. El contrato no marca este parámetro como anulable. Tiene valor por defecto `false`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.


#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 50 | `val now` | `inferido` | `SystemClock.uptimeMillis()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 51 | `val previous` | `inferido` | `synchronized(lastPlayAt) { lastPlayAt[haptic] ?: 0L }` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 47 | `if ((!enabled && !force) \|\| intensity <= 0f) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 48 | `return` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 52 | `if (now - previous < minimumIntervalMs) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 53 | `return` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

**Restricciones concretas que aparecen en este bloque:**
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

- **Mutación de colección/estado compartido:** Modifica una colección/mapa/estructura existente; el cambio permanece visible para quienes compartan esa misma instancia.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `SystemClock.uptimeMillis`, `synchronized`, `play`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.7 `playToggle` — fun, líneas 60–62

```kotlin
    fun playToggle(context: Context, checked: Boolean, force: Boolean = false) {
        play(context = context, haptic = if (checked) UiHaptic.ToggleOn else UiHaptic.ToggleOff, force = force)
    }
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `context: Context` — `context` recibe un valor de tipo `Context`. El contrato no marca este parámetro como anulable. Da acceso a recursos y APIs Android; su vida útil debe corresponder con el uso realizado.
- `checked: Boolean` — `checked` recibe un valor de tipo `Boolean`. El contrato no marca este parámetro como anulable.
- `force: Boolean = false` — `force` recibe un valor de tipo `Boolean`. El contrato no marca este parámetro como anulable. Tiene valor por defecto `false`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.


#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `play`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.8 `previewStyle` — fun, líneas 63–65

```kotlin
    fun previewStyle(context: Context, style: String, intensityPercent: Float = 55f, haptic: UiHaptic = UiHaptic.Confirm) {
        vibrate(context = context, haptic = haptic, style = normalizeStyle(style), intensity = (intensityPercent / 100f).coerceIn(0f, 1f))
    }
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `context: Context` — `context` recibe un valor de tipo `Context`. El contrato no marca este parámetro como anulable. Da acceso a recursos y APIs Android; su vida útil debe corresponder con el uso realizado.
- `style: String` — `style` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.
- `intensityPercent: Float = 55f` — `intensityPercent` recibe un valor de tipo `Float`. El contrato no marca este parámetro como anulable. Tiene valor por defecto `55f`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.
- `haptic: UiHaptic = UiHaptic.Confirm` — `haptic` recibe un valor de tipo `UiHaptic`. El contrato no marca este parámetro como anulable. Tiene valor por defecto `UiHaptic.Confirm`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.


#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

**Restricciones concretas que aparecen en este bloque:**
- **Límite numérico:** Se fuerza el valor a un intervalo o umbral para impedir que salgan valores fuera de los límites esperados. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `vibrate`, `normalizeStyle`, `coerceIn`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.9 `playForSound` — fun, líneas 66–80

```kotlin
    fun playForSound(context: Context, sound: UiSound, throttled: Boolean = false, minimumIntervalMs: Long = 45L) {
        val haptic = when (sound) {
                UiSound.Edit -> UiHaptic.Tap
                UiSound.Delete -> UiHaptic.Warning
                UiSound.Priority -> UiHaptic.Selection
                UiSound.SliderTick -> UiHaptic.Tick
                UiSound.Attachment -> UiHaptic.Attachment
                UiSound.Toggle -> UiHaptic.Selection
            }
        if (throttled) {
            playThrottled(context = context, haptic = haptic, minimumIntervalMs = minimumIntervalMs)
        } else {
            play(context = context, haptic = haptic)
        }
    }
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `context: Context` — `context` recibe un valor de tipo `Context`. El contrato no marca este parámetro como anulable. Da acceso a recursos y APIs Android; su vida útil debe corresponder con el uso realizado.
- `sound: UiSound` — `sound` recibe un valor de tipo `UiSound`. El contrato no marca este parámetro como anulable.
- `throttled: Boolean = false` — `throttled` recibe un valor de tipo `Boolean`. El contrato no marca este parámetro como anulable. Tiene valor por defecto `false`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada.
- `minimumIntervalMs: Long = 45L` — `minimumIntervalMs` recibe un valor de tipo `Long`. El contrato no marca este parámetro como anulable. Tiene valor por defecto `45L`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.


#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 67 | `val haptic` | `inferido` | `when (sound) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 68 | `UiSound.Edit -> UiHaptic.Tap` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 69 | `UiSound.Delete -> UiHaptic.Warning` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 70 | `UiSound.Priority -> UiHaptic.Selection` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 71 | `UiSound.SliderTick -> UiHaptic.Tick` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 72 | `UiSound.Attachment -> UiHaptic.Attachment` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 73 | `UiSound.Toggle -> UiHaptic.Selection` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 75 | `if (throttled) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `playThrottled`, `play`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.

### 4.10 `playForAction` — fun, líneas 81–119

```kotlin
    fun playForAction(context: Context, action: UiActionSound, throttled: Boolean = false, minimumIntervalMs: Long = 45L) {
        // Escribir caracteres no vibra; mover selección/cursor sí entra por
        // SliderTick y conserva una respuesta háptica muy breve.
        val haptic: UiHaptic? = when (action) {
                UiActionSound.TextInput -> null
                UiActionSound.Open -> UiHaptic.Tap
                UiActionSound.Back -> UiHaptic.Selection
                UiActionSound.Save -> UiHaptic.Confirm
                UiActionSound.Search -> UiHaptic.Tick
                UiActionSound.Menu -> UiHaptic.Selection
                UiActionSound.Select -> UiHaptic.Selection
                UiActionSound.Favorite -> UiHaptic.Confirm
                UiActionSound.Pin -> UiHaptic.Selection
                UiActionSound.Share -> UiHaptic.Tap
                UiActionSound.Move -> UiHaptic.Selection
                UiActionSound.Color -> UiHaptic.Selection
                UiActionSound.Category -> UiHaptic.Selection
                UiActionSound.Add -> UiHaptic.Attachment
                UiActionSound.Confirm -> UiHaptic.Confirm
                UiActionSound.Cancel -> UiHaptic.Selection
                UiActionSound.Navigation -> UiHaptic.Selection
                UiActionSound.Sort -> UiHaptic.Tick
                UiActionSound.Layout -> UiHaptic.Selection
                UiActionSound.Language -> UiHaptic.Selection
                UiActionSound.Theme -> UiHaptic.Selection
                UiActionSound.Link -> UiHaptic.Tap
                UiActionSound.PlayPause -> UiHaptic.Selection
                UiActionSound.Zoom -> UiHaptic.Tick
                UiActionSound.Backup -> UiHaptic.Attachment
                UiActionSound.Restore -> UiHaptic.Confirm
                UiActionSound.Settings -> UiHaptic.Selection
            }
        haptic ?: return
        if (throttled) {
            playThrottled(context = context, haptic = haptic, minimumIntervalMs = minimumIntervalMs)
        } else {
            play(context = context, haptic = haptic)
        }
    }
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `context: Context` — `context` recibe un valor de tipo `Context`. El contrato no marca este parámetro como anulable. Da acceso a recursos y APIs Android; su vida útil debe corresponder con el uso realizado.
- `action: UiActionSound` — `action` recibe un valor de tipo `UiActionSound`. El contrato no marca este parámetro como anulable.
- `throttled: Boolean = false` — `throttled` recibe un valor de tipo `Boolean`. El contrato no marca este parámetro como anulable. Tiene valor por defecto `false`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada.
- `minimumIntervalMs: Long = 45L` — `minimumIntervalMs` recibe un valor de tipo `Long`. El contrato no marca este parámetro como anulable. Tiene valor por defecto `45L`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.


#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 84 | `val haptic` | `UiHaptic?` | `when (action) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo declarado es `UiHaptic?`. Admite `null`, así que los consumidores deben contemplar ausencia de valor. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 85 | `UiActionSound.TextInput -> null` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 86 | `UiActionSound.Open -> UiHaptic.Tap` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 87 | `UiActionSound.Back -> UiHaptic.Selection` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 88 | `UiActionSound.Save -> UiHaptic.Confirm` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 89 | `UiActionSound.Search -> UiHaptic.Tick` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 90 | `UiActionSound.Menu -> UiHaptic.Selection` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 91 | `UiActionSound.Select -> UiHaptic.Selection` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 92 | `UiActionSound.Favorite -> UiHaptic.Confirm` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 93 | `UiActionSound.Pin -> UiHaptic.Selection` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 94 | `UiActionSound.Share -> UiHaptic.Tap` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 95 | `UiActionSound.Move -> UiHaptic.Selection` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 96 | `UiActionSound.Color -> UiHaptic.Selection` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 97 | `UiActionSound.Category -> UiHaptic.Selection` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 98 | `UiActionSound.Add -> UiHaptic.Attachment` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 99 | `UiActionSound.Confirm -> UiHaptic.Confirm` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 100 | `UiActionSound.Cancel -> UiHaptic.Selection` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 101 | `UiActionSound.Navigation -> UiHaptic.Selection` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 102 | `UiActionSound.Sort -> UiHaptic.Tick` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 103 | `UiActionSound.Layout -> UiHaptic.Selection` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 104 | `UiActionSound.Language -> UiHaptic.Selection` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 105 | `UiActionSound.Theme -> UiHaptic.Selection` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 106 | `UiActionSound.Link -> UiHaptic.Tap` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 107 | `UiActionSound.PlayPause -> UiHaptic.Selection` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 108 | `UiActionSound.Zoom -> UiHaptic.Tick` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 109 | `UiActionSound.Backup -> UiHaptic.Attachment` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 110 | `UiActionSound.Restore -> UiHaptic.Confirm` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 111 | `UiActionSound.Settings -> UiHaptic.Selection` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 114 | `if (throttled) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |

**Restricciones concretas que aparecen en este bloque:**
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `playThrottled`, `play`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.

### 4.11 `vibrate` — fun, líneas 120–172

```kotlin
    private data class HapticPattern(val timings: LongArray, val amplitudes: IntArray)
    private fun vibrate(context: Context, haptic: UiHaptic, style: String, intensity: Float) {
        if (intensity <= 0f) {
            return
        }
        val vibrator = getVibrator(context) ?: return
        if (!vibrator.hasVibrator()) {
            return
        }
        val eventScale = when (haptic) {
                UiHaptic.Tick -> 0.46f
                UiHaptic.Selection -> 0.68f
                UiHaptic.Tap -> 0.76f
                UiHaptic.ToggleOn -> 0.82f
                UiHaptic.ToggleOff -> 0.64f
                UiHaptic.Attachment -> 0.88f
                UiHaptic.Confirm -> 0.96f
                UiHaptic.Warning -> 1.00f
            }
        var pattern = basePattern(normalizeStyle(style))
        // Sliders y movimientos de cursor deben sentirse como ticks cortos,
        // incluso cuando el usuario eligió un estilo con varios pulsos.
        if (haptic == UiHaptic.Tick) {
            val firstAmp = pattern.amplitudes.firstOrNull { it > 0 }?: 120
            pattern = HapticPattern(timings = longArrayOf(0L, 7L), amplitudes = intArrayOf(0, firstAmp))
        }
        if (haptic == UiHaptic.ToggleOff) {
            pattern = HapticPattern(timings = pattern.timings.map { (it * 0.85f).roundToInt().coerceAtLeast(1).toLong() }.toLongArray(),
                    amplitudes = pattern.amplitudes)
        }
        val scaledAmplitudes = pattern.amplitudes.map { amplitude -> if (amplitude == 0) {
                        0
                    } else {
                        (amplitude * intensity * eventScale).roundToInt().coerceIn(1, 255)
                    }
                }.toIntArray()
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val effect = if (pattern.timings.size <= 2) {
                        VibrationEffect.createOneShot(pattern.timings.lastOrNull()?.coerceAtLeast(1L) ?: 8L,
                            scaledAmplitudes.lastOrNull()?.coerceIn(1, 255)?: VibrationEffect.DEFAULT_AMPLITUDE)
                    } else {
                        VibrationEffect.createWaveform(pattern.timings, scaledAmplitudes, -1)
                    }
                vibrator.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(pattern.timings.sum().coerceIn(5L, 90L))
            }
        } catch (_: SecurityException) {
            // No se fuerza feedback si el fabricante bloquea la vibración.
        }
    }
```

#### Qué hace y por qué existe

Controlador de retroalimentación háptica. Traduce acciones de UI y ajustes de intensidad/estilo a patrones de vibración compatibles con la versión de Android.

#### Contrato de la declaración

**Parámetros:**

- `val timings: LongArray` — No se pudo separar automáticamente nombre/tipo; se conserva la firma exacta como contrato.
- `val amplitudes: IntArray` — No se pudo separar automáticamente nombre/tipo; se conserva la firma exacta como contrato.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 125 | `val vibrator` | `inferido` | `getVibrator(context) ?: return` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 129 | `val eventScale` | `inferido` | `when (haptic) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 139 | `var pattern` | `inferido` | `basePattern(normalizeStyle(style))` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 143 | `val firstAmp` | `inferido` | `pattern.amplitudes.firstOrNull { it > 0 }?: 120` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 150 | `val scaledAmplitudes` | `inferido` | `pattern.amplitudes.map { amplitude -> if (amplitude == 0) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 158 | `val effect` | `inferido` | `if (pattern.timings.size <= 2) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 122 | `if (intensity <= 0f) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 123 | `return` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 126 | `if (!vibrator.hasVibrator()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 127 | `return` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 130 | `UiHaptic.Tick -> 0.46f` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 131 | `UiHaptic.Selection -> 0.68f` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 132 | `UiHaptic.Tap -> 0.76f` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 133 | `UiHaptic.ToggleOn -> 0.82f` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 134 | `UiHaptic.ToggleOff -> 0.64f` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 135 | `UiHaptic.Attachment -> 0.88f` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 136 | `UiHaptic.Confirm -> 0.96f` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 137 | `UiHaptic.Warning -> 1.00f` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 142 | `if (haptic == UiHaptic.Tick) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 146 | `if (haptic == UiHaptic.ToggleOff) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 156 | `try {` | Frontera de manejo de errores: agrupa operaciones que pueden lanzar excepciones para permitir recuperación o limpieza controlada. |
| 157 | `if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 2.
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 4.
- **Límite numérico:** Se fuerza el valor a un intervalo o umbral para impedir que salgan valores fuera de los límites esperados. Apariciones en este bloque: 5.
- **Guardia de API:** Se condiciona una API según la versión de Android para no ejecutar llamadas incompatibles en dispositivos antiguos. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `getVibrator`, `vibrator.hasVibrator`, `basePattern`, `normalizeStyle`, `HapticPattern`, `longArrayOf`, `intArrayOf`, `roundToInt`, `coerceAtLeast`, `toLong`, `toLongArray`, `coerceIn`, `toIntArray`, `VibrationEffect.createOneShot`, `pattern.timings.lastOrNull`, `scaledAmplitudes.lastOrNull`, `VibrationEffect.createWaveform`, `vibrator.vibrate`, `Suppress`, `pattern.timings.sum`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.
- Conservar las guardas de versión antes de APIs que no existen en Android antiguos.

### 4.12 `vibrate` — fun, líneas 121–172

```kotlin
    private fun vibrate(context: Context, haptic: UiHaptic, style: String, intensity: Float) {
        if (intensity <= 0f) {
            return
        }
        val vibrator = getVibrator(context) ?: return
        if (!vibrator.hasVibrator()) {
            return
        }
        val eventScale = when (haptic) {
                UiHaptic.Tick -> 0.46f
                UiHaptic.Selection -> 0.68f
                UiHaptic.Tap -> 0.76f
                UiHaptic.ToggleOn -> 0.82f
                UiHaptic.ToggleOff -> 0.64f
                UiHaptic.Attachment -> 0.88f
                UiHaptic.Confirm -> 0.96f
                UiHaptic.Warning -> 1.00f
            }
        var pattern = basePattern(normalizeStyle(style))
        // Sliders y movimientos de cursor deben sentirse como ticks cortos,
        // incluso cuando el usuario eligió un estilo con varios pulsos.
        if (haptic == UiHaptic.Tick) {
            val firstAmp = pattern.amplitudes.firstOrNull { it > 0 }?: 120
            pattern = HapticPattern(timings = longArrayOf(0L, 7L), amplitudes = intArrayOf(0, firstAmp))
        }
        if (haptic == UiHaptic.ToggleOff) {
            pattern = HapticPattern(timings = pattern.timings.map { (it * 0.85f).roundToInt().coerceAtLeast(1).toLong() }.toLongArray(),
                    amplitudes = pattern.amplitudes)
        }
        val scaledAmplitudes = pattern.amplitudes.map { amplitude -> if (amplitude == 0) {
                        0
                    } else {
                        (amplitude * intensity * eventScale).roundToInt().coerceIn(1, 255)
                    }
                }.toIntArray()
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val effect = if (pattern.timings.size <= 2) {
                        VibrationEffect.createOneShot(pattern.timings.lastOrNull()?.coerceAtLeast(1L) ?: 8L,
                            scaledAmplitudes.lastOrNull()?.coerceIn(1, 255)?: VibrationEffect.DEFAULT_AMPLITUDE)
                    } else {
                        VibrationEffect.createWaveform(pattern.timings, scaledAmplitudes, -1)
                    }
                vibrator.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(pattern.timings.sum().coerceIn(5L, 90L))
            }
        } catch (_: SecurityException) {
            // No se fuerza feedback si el fabricante bloquea la vibración.
        }
    }
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `context: Context` — `context` recibe un valor de tipo `Context`. El contrato no marca este parámetro como anulable. Da acceso a recursos y APIs Android; su vida útil debe corresponder con el uso realizado.
- `haptic: UiHaptic` — `haptic` recibe un valor de tipo `UiHaptic`. El contrato no marca este parámetro como anulable.
- `style: String` — `style` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.
- `intensity: Float` — `intensity` recibe un valor de tipo `Float`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 125 | `val vibrator` | `inferido` | `getVibrator(context) ?: return` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 129 | `val eventScale` | `inferido` | `when (haptic) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 139 | `var pattern` | `inferido` | `basePattern(normalizeStyle(style))` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 143 | `val firstAmp` | `inferido` | `pattern.amplitudes.firstOrNull { it > 0 }?: 120` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 150 | `val scaledAmplitudes` | `inferido` | `pattern.amplitudes.map { amplitude -> if (amplitude == 0) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 158 | `val effect` | `inferido` | `if (pattern.timings.size <= 2) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 122 | `if (intensity <= 0f) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 123 | `return` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 126 | `if (!vibrator.hasVibrator()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 127 | `return` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 130 | `UiHaptic.Tick -> 0.46f` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 131 | `UiHaptic.Selection -> 0.68f` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 132 | `UiHaptic.Tap -> 0.76f` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 133 | `UiHaptic.ToggleOn -> 0.82f` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 134 | `UiHaptic.ToggleOff -> 0.64f` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 135 | `UiHaptic.Attachment -> 0.88f` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 136 | `UiHaptic.Confirm -> 0.96f` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 137 | `UiHaptic.Warning -> 1.00f` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 142 | `if (haptic == UiHaptic.Tick) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 146 | `if (haptic == UiHaptic.ToggleOff) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 156 | `try {` | Frontera de manejo de errores: agrupa operaciones que pueden lanzar excepciones para permitir recuperación o limpieza controlada. |
| 157 | `if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 2.
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 4.
- **Límite numérico:** Se fuerza el valor a un intervalo o umbral para impedir que salgan valores fuera de los límites esperados. Apariciones en este bloque: 5.
- **Guardia de API:** Se condiciona una API según la versión de Android para no ejecutar llamadas incompatibles en dispositivos antiguos. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `getVibrator`, `vibrator.hasVibrator`, `basePattern`, `normalizeStyle`, `HapticPattern`, `longArrayOf`, `intArrayOf`, `roundToInt`, `coerceAtLeast`, `toLong`, `toLongArray`, `coerceIn`, `toIntArray`, `VibrationEffect.createOneShot`, `pattern.timings.lastOrNull`, `scaledAmplitudes.lastOrNull`, `VibrationEffect.createWaveform`, `vibrator.vibrate`, `Suppress`, `pattern.timings.sum`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.
- Conservar las guardas de versión antes de APIs que no existen en Android antiguos.

### 4.13 `basePattern` — fun, líneas 173–190

```kotlin
    private fun basePattern(style: String): HapticPattern = when (style) {
            "crisp" -> HapticPattern(longArrayOf(0L, 9L), intArrayOf(0, 220))
            "deep" -> HapticPattern(longArrayOf(0L, 24L), intArrayOf(0, 175))
            "double" -> HapticPattern(longArrayOf(0L, 10L, 34L, 12L), intArrayOf(0, 190, 0, 145))
            "pulse" -> HapticPattern(longArrayOf(0L, 15L, 22L, 22L), intArrayOf(0, 130, 0, 205))
            "stepped" -> HapticPattern(longArrayOf(0L, 6L, 10L, 8L, 10L, 11L), intArrayOf(0, 85, 0, 140, 0, 210))
            "mechanical" -> HapticPattern(longArrayOf(0L, 5L, 8L, 14L), intArrayOf(0, 225, 0, 150))
            "minimal" -> HapticPattern(longArrayOf(0L, 5L), intArrayOf(0, 125))
            "triple" -> HapticPattern(longArrayOf(0L, 6L, 10L, 7L, 10L, 9L), intArrayOf(0, 170, 0, 200, 0, 230))
            "ripple" -> HapticPattern(longArrayOf(0L, 5L, 8L, 7L, 10L, 10L), intArrayOf(0, 225, 0, 155, 0, 90))
            "heartbeat" -> HapticPattern(longArrayOf(0L, 11L, 38L, 20L), intArrayOf(0, 135, 0, 230))
            "snap" -> HapticPattern(longArrayOf(0L, 4L, 5L, 11L), intArrayOf(0, 255, 0, 120))
            "wave" -> HapticPattern(longArrayOf(0L, 10L, 8L, 14L, 8L, 8L), intArrayOf(0, 105, 0, 225, 0, 125))
            "heavy" -> HapticPattern(longArrayOf(0L, 32L), intArrayOf(0, 235))
            "spring" -> HapticPattern(longArrayOf(0L, 7L, 12L, 5L, 14L, 4L), intArrayOf(0, 235, 0, 150, 0, 85))
            "echo" -> HapticPattern(longArrayOf(0L, 10L, 24L, 7L, 22L, 5L), intArrayOf(0, 220, 0, 135, 0, 70))
            else -> HapticPattern(longArrayOf(0L, 12L), intArrayOf(0, 115))
        }
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `style: String` — `style` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `HapticPattern`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 174 | `"crisp" -> HapticPattern(longArrayOf(0L, 9L), intArrayOf(0, 220))` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 175 | `"deep" -> HapticPattern(longArrayOf(0L, 24L), intArrayOf(0, 175))` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 176 | `"double" -> HapticPattern(longArrayOf(0L, 10L, 34L, 12L), intArrayOf(0, 190, 0, 145))` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 177 | `"pulse" -> HapticPattern(longArrayOf(0L, 15L, 22L, 22L), intArrayOf(0, 130, 0, 205))` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 178 | `"stepped" -> HapticPattern(longArrayOf(0L, 6L, 10L, 8L, 10L, 11L), intArrayOf(0, 85, 0, 140, 0, 210))` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 179 | `"mechanical" -> HapticPattern(longArrayOf(0L, 5L, 8L, 14L), intArrayOf(0, 225, 0, 150))` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 180 | `"minimal" -> HapticPattern(longArrayOf(0L, 5L), intArrayOf(0, 125))` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 181 | `"triple" -> HapticPattern(longArrayOf(0L, 6L, 10L, 7L, 10L, 9L), intArrayOf(0, 170, 0, 200, 0, 230))` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 182 | `"ripple" -> HapticPattern(longArrayOf(0L, 5L, 8L, 7L, 10L, 10L), intArrayOf(0, 225, 0, 155, 0, 90))` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 183 | `"heartbeat" -> HapticPattern(longArrayOf(0L, 11L, 38L, 20L), intArrayOf(0, 135, 0, 230))` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 184 | `"snap" -> HapticPattern(longArrayOf(0L, 4L, 5L, 11L), intArrayOf(0, 255, 0, 120))` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 185 | `"wave" -> HapticPattern(longArrayOf(0L, 10L, 8L, 14L, 8L, 8L), intArrayOf(0, 105, 0, 225, 0, 125))` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 186 | `"heavy" -> HapticPattern(longArrayOf(0L, 32L), intArrayOf(0, 235))` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 187 | `"spring" -> HapticPattern(longArrayOf(0L, 7L, 12L, 5L, 14L, 4L), intArrayOf(0, 235, 0, 150, 0, 85))` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 188 | `"echo" -> HapticPattern(longArrayOf(0L, 10L, 24L, 7L, 22L, 5L), intArrayOf(0, 220, 0, 135, 0, 70))` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 189 | `else -> HapticPattern(longArrayOf(0L, 12L), intArrayOf(0, 115))` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `HapticPattern`, `longArrayOf`, `intArrayOf`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.

### 4.14 `getVibrator` — fun, líneas 191–197

```kotlin
    private fun getVibrator(context: Context): Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.getSystemService(VibratorManager::class.java)?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `context: Context` — `context` recibe un valor de tipo `Context`. El contrato no marca este parámetro como anulable. Da acceso a recursos y APIs Android; su vida útil debe corresponder con el uso realizado.

**Retorno:** `Vibrator?`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 1.
- **Guardia de API:** Se condiciona una API según la versión de Android para no ejecutar llamadas incompatibles en dispositivos antiguos. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `context.getSystemService`, `Suppress`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar las guardas de versión antes de APIs que no existen en Android antiguos.

## 5. Inventario global de propiedades/variables detectadas

| Línea | Identificador | Mutabilidad | Tipo | Explicación |
|---:|---|---|---|---|
| 24 | `DEFAULT_STYLE` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Está marcada `const`, por lo que es una constante de tiempo de compilación y solo puede usar un tipo/valor admitido por Kotlin para constantes. |
| 25 | `availableStyles` | `val` | `List<String>` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo declarado es `List<String>`. No declara nulabilidad explícita. |
| 28 | `enabled` | `var` | `Boolean` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo declarado es `Boolean`. No declara nulabilidad explícita. El nombre indica un estado de decisión/visibilidad/selección usado para controlar una rama de comportamiento o presentación. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 30 | `intensity` | `var` | `Float` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo declarado es `Float`. No declara nulabilidad explícita. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 32 | `style` | `var` | `String` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo declarado es `String`. No declara nulabilidad explícita. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 33 | `lastPlayAt` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 50 | `now` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 51 | `previous` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 67 | `haptic` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 84 | `haptic` | `val` | `UiHaptic?` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo declarado es `UiHaptic?`. Admite `null`, así que los consumidores deben contemplar ausencia de valor. |
| 125 | `vibrator` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 129 | `eventScale` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 139 | `pattern` | `var` | `inferido` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 143 | `firstAmp` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 150 | `scaledAmplitudes` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 158 | `effect` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

## 6. Mapa exhaustivo de TODOS los bloques delimitados por `{}`

Esta tabla recorre todas las llaves estructurales detectadas fuera de strings/comentarios. Así se documentan también lambdas de Compose, callbacks, iteraciones y ámbitos locales que no tienen un nombre propio de función.

| Inicio–fin | Prof. | Cabecera/contexto | Qué significa ese bloque |
|---|---:|---|---|
| 19–21 | 0 | `enum class UiHaptic` | Cuerpo de tipo/contenedor: agrupa propiedades y miembros bajo la misma responsabilidad y controla su visibilidad/estado compartido. |
| 23–197 | 0 | `object UiHapticPlayer` | Cuerpo de tipo/contenedor: agrupa propiedades y miembros bajo la misma responsabilidad y controla su visibilidad/estado compartido. |
| 34–34 | 1 | `fun normalizeStyle(value: String): String = value.trim().lowercase().takeIf` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 35–39 | 1 | `fun configure(enabled: Boolean, intensityPercent: Float, style: String = DEFAULT_STYLE)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 40–45 | 1 | `fun play(context: Context, haptic: UiHaptic, force: Boolean = false)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 41–43 | 2 | `if ((!enabled && !force) \|\| intensity <= 0f)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 46–59 | 1 | `fun playThrottled(context: Context, haptic: UiHaptic, minimumIntervalMs: Long = 45L, force: Boolean = false)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 47–49 | 2 | `if ((!enabled && !force) \|\| intensity <= 0f)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 51–51 | 2 | `val previous = synchronized(lastPlayAt)` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 52–54 | 2 | `if (now - previous < minimumIntervalMs)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 55–57 | 2 | `synchronized(lastPlayAt)` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 60–62 | 1 | `fun playToggle(context: Context, checked: Boolean, force: Boolean = false)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 63–65 | 1 | `fun previewStyle(context: Context, style: String, intensityPercent: Float = 55f, haptic: UiHaptic = UiHaptic.Confirm)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 66–80 | 1 | `fun playForSound(context: Context, sound: UiSound, throttled: Boolean = false, minimumIntervalMs: Long = 45L)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 67–74 | 2 | `val haptic = when (sound)` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 75–77 | 2 | `if (throttled)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 77–79 | 2 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 81–119 | 1 | `fun playForAction(context: Context, action: UiActionSound, throttled: Boolean = false, minimumIntervalMs: Long = 45L)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 84–112 | 2 | `val haptic: UiHaptic? = when (action)` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 114–116 | 2 | `if (throttled)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 116–118 | 2 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 121–172 | 1 | `private fun vibrate(context: Context, haptic: UiHaptic, style: String, intensity: Float)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 122–124 | 2 | `if (intensity <= 0f)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 126–128 | 2 | `if (!vibrator.hasVibrator())` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 129–138 | 2 | `val eventScale = when (haptic)` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 142–145 | 2 | `if (haptic == UiHaptic.Tick)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 143–143 | 3 | `val firstAmp = pattern.amplitudes.firstOrNull` | Ámbito delimitado por llaves en profundidad 3. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 146–149 | 2 | `if (haptic == UiHaptic.ToggleOff)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 147–147 | 3 | `pattern = HapticPattern(timings = pattern.timings.map` | Transformación de colección: produce un nuevo elemento por cada entrada y normalmente genera una colección de salida. |
| 150–155 | 2 | `val scaledAmplitudes = pattern.amplitudes.map` | Transformación de colección: produce un nuevo elemento por cada entrada y normalmente genera una colección de salida. |
| 150–152 | 3 | `val scaledAmplitudes = pattern.amplitudes.map { amplitude -> if (amplitude == 0)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 152–154 | 3 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 156–169 | 2 | `try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 157–165 | 3 | `if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 158–161 | 4 | `val effect = if (pattern.timings.size <= 2)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 161–163 | 4 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 165–168 | 3 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 169–171 | 2 | `} catch (_: SecurityException)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 173–190 | 1 | `private fun basePattern(style: String): HapticPattern = when (style)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 191–193 | 1 | `private fun getVibrator(context: Context): Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 193–196 | 1 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |

## 7. Lectura de seguridad antes de tocar este archivo

La documentación describe **lo que el código actual ya hace**. No constituye una propuesta de refactorización. Si se quisiera cambiar algo en el futuro, primero habría que preservar contratos públicos, claves de configuración, restricciones de API Android, comportamiento de Compose, lifecycle, operaciones de I/O y compatibilidad con los datos ya persistidos. En esta entrega no se hizo ninguna de esas modificaciones.

# AppMotion.kt — documentación exhaustiva por bloques

**Ruta de código real:** `app/src/main/java/com/example/mynotes/ui/motion/AppMotion.kt`  
**SHA-256 del archivo ejecutable sin tocar:** `22837809d91b6e1e2958ff293b15165ae6c0be72b704c9ae50e1c167140a1964`  
**Líneas del código real:** 269

> **Garantía:** esta documentación se genera en una carpeta paralela. El archivo Kotlin/Kotlin DSL anterior no se modifica. Los fragmentos mostrados abajo son copias de lectura; la aplicación compila usando únicamente el archivo original de `app/...`.

## 1. Papel del archivo

Catálogo y lógica de animaciones/transiciones de la aplicación. Normaliza opciones y construye transformaciones según estilo, curva, velocidad, intensidad y perfil de rendimiento.

**Arquitectura.** Centraliza movimiento para que las pantallas no codifiquen animaciones incompatibles entre sí y para poder reducir trabajo cuando el usuario prioriza rendimiento.

**Flujo general.** El flujo se describe declaración por declaración en este documento.

## 2. Package e imports

El `package` es `com.example.mynotes.ui.motion`. Determina el namespace lógico del archivo, resolución de visibilidad `internal`/package-related tooling y la ruta conceptual desde la que otras clases lo importan.

El archivo declara **39 imports**. Cada import evita usar el nombre totalmente calificado en el cuerpo y, además, revela las dependencias técnicas del bloque:

**Jetpack/Compose:** `androidx.compose.animation.AnimatedContent`, `androidx.compose.animation.AnimatedContentTransitionScope`, `androidx.compose.animation.ContentTransform`, `androidx.compose.animation.EnterTransition`, `androidx.compose.animation.ExitTransition`, `androidx.compose.animation.expandHorizontally`, `androidx.compose.animation.expandIn`, `androidx.compose.animation.expandVertically`, `androidx.compose.animation.fadeIn`, `androidx.compose.animation.fadeOut`, `androidx.compose.animation.scaleIn`, `androidx.compose.animation.scaleOut`, `androidx.compose.animation.shrinkHorizontally`, `androidx.compose.animation.shrinkOut`, `androidx.compose.animation.shrinkVertically`, `androidx.compose.animation.slideInHorizontally`, `androidx.compose.animation.slideInVertically`, `androidx.compose.animation.slideOutHorizontally`, `androidx.compose.animation.slideOutVertically`, `androidx.compose.animation.togetherWith`, `androidx.compose.animation.core.CubicBezierEasing`, `androidx.compose.animation.core.Easing`, `androidx.compose.animation.core.FastOutLinearInEasing`, `androidx.compose.animation.core.FiniteAnimationSpec`, `androidx.compose.animation.core.FastOutSlowInEasing`, `androidx.compose.animation.core.LinearEasing`, `androidx.compose.animation.core.LinearOutSlowInEasing`, `androidx.compose.animation.core.Spring`, `androidx.compose.animation.core.spring`, `androidx.compose.animation.core.tween`, `androidx.compose.foundation.background`, `androidx.compose.foundation.layout.Box`, `androidx.compose.foundation.layout.fillMaxSize`, `androidx.compose.material3.MaterialTheme`, `androidx.compose.runtime.Composable`, `androidx.compose.runtime.remember`, `androidx.compose.ui.Alignment`, `androidx.compose.ui.Modifier`.

**Kotlin/corrutinas/Java:** `kotlin.math.roundToInt`.

## 3. Restricciones e invariantes visibles en el archivo

- **Límite numérico (13 aparición/apariciones):** Se fuerza el valor a un intervalo o umbral para impedir que salgan valores fuera de los límites esperados.

## 4. Bloques de código, uno por uno

### 4.1 `AppMotion` — object, líneas 49–75

```kotlin
object AppMotion {
    const val FAST = 140
    const val NORMAL = 220
    const val SLOW = 320
    val supportedStyles = setOf("zoom", "zoom_fade", "fade", "slide_left", "slide_right", "slide_up", "slide_down", "slide_zoom_left",
        "slide_zoom_up", "axis_x", "axis_y", "axis_z", "expand", "expand_horizontal", "expand_vertical", "bounce", "elastic", "pop",
        "subtle", "random")
    val supportedEasings = setOf("standard", "linear", "accelerate", "decelerate", "emphasized")
    fun normalizeStyle(value: String): String = if (value in supportedStyles) value else "zoom"
    fun normalizeEasing(value: String): String = if (value in supportedEasings) value else "standard"
    fun normalizePerformanceMode(value: String): String = when (value) {
            "performance", "balanced", "quality" -> value
            else -> "balanced"
        }
    fun duration(baseMilliseconds: Int, animationsEnabled: Boolean, animationSpeed: Float): Int {
        if (!animationsEnabled) return 0
        val speed = animationSpeed.coerceIn(0.5f, 2f)
        return (baseMilliseconds / speed).roundToInt().coerceAtLeast(1)
    }
    fun easing(key: String): Easing = when (normalizeEasing(key)) {
            "linear" -> LinearEasing
            "accelerate" -> FastOutLinearInEasing
            "decelerate" -> LinearOutSlowInEasing
            "emphasized" -> CubicBezierEasing(0.2f, 0f, 0f, 1f)
            else -> FastOutSlowInEasing
        }
}
```

#### Qué hace y por qué existe

Catálogo y lógica de animaciones/transiciones de la aplicación. Normaliza opciones y construye transformaciones según estilo, curva, velocidad, intensidad y perfil de rendimiento.

#### Contrato de la declaración


#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 50 | `val FAST` | `inferido` | `140` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Está marcada `const`, por lo que es una constante de tiempo de compilación y solo puede usar un tipo/valor admitido por Kotlin para constantes. |
| 51 | `val NORMAL` | `inferido` | `220` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Está marcada `const`, por lo que es una constante de tiempo de compilación y solo puede usar un tipo/valor admitido por Kotlin para constantes. |
| 52 | `val SLOW` | `inferido` | `320` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Está marcada `const`, por lo que es una constante de tiempo de compilación y solo puede usar un tipo/valor admitido por Kotlin para constantes. |
| 53 | `val supportedStyles` | `inferido` | `setOf("zoom", "zoom_fade", "fade", "slide_left", "slide_right", "slide_up", "slide_down", "slide_zoo…` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 56 | `val supportedEasings` | `inferido` | `setOf("standard", "linear", "accelerate", "decelerate", "emphasized")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 65 | `val speed` | `inferido` | `animationSpeed.coerceIn(0.5f, 2f)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El inicializador limita explícitamente el valor a un rango, evitando que dimensiones/intensidades/sizes inválidos se propaguen. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 60 | `"performance", "balanced", "quality" -> value` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 61 | `else -> "balanced"` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |
| 64 | `if (!animationsEnabled) return 0` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 66 | `return (baseMilliseconds / speed).roundToInt().coerceAtLeast(1)` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 69 | `"linear" -> LinearEasing` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 70 | `"accelerate" -> FastOutLinearInEasing` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 71 | `"decelerate" -> LinearOutSlowInEasing` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 72 | `"emphasized" -> CubicBezierEasing(0.2f, 0f, 0f, 1f)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 73 | `else -> FastOutSlowInEasing` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |

**Restricciones concretas que aparecen en este bloque:**
- **Límite numérico:** Se fuerza el valor a un intervalo o umbral para impedir que salgan valores fuera de los límites esperados. Apariciones en este bloque: 2.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `setOf`, `animationSpeed.coerceIn`, `roundToInt`, `coerceAtLeast`, `normalizeEasing`, `CubicBezierEasing`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.

### 4.2 `normalizeStyle` — fun, líneas 57–58

```kotlin
    fun normalizeStyle(value: String): String = if (value in supportedStyles) value else "zoom"
    fun normalizeEasing(value: String): String = if (value in supportedEasings) value else "standard"
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

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

No se detectaron llamadas de función relevantes fuera de la propia estructura de la declaración.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.3 `normalizeEasing` — fun, líneas 58–62

```kotlin
    fun normalizeEasing(value: String): String = if (value in supportedEasings) value else "standard"
    fun normalizePerformanceMode(value: String): String = when (value) {
            "performance", "balanced", "quality" -> value
            else -> "balanced"
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

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 60 | `"performance", "balanced", "quality" -> value` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 61 | `else -> "balanced"` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

No se detectaron llamadas de función relevantes fuera de la propia estructura de la declaración.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.

### 4.4 `normalizePerformanceMode` — fun, líneas 59–62

```kotlin
    fun normalizePerformanceMode(value: String): String = when (value) {
            "performance", "balanced", "quality" -> value
            else -> "balanced"
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

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 60 | `"performance", "balanced", "quality" -> value` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 61 | `else -> "balanced"` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

No se detectaron llamadas de función relevantes fuera de la propia estructura de la declaración.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.

### 4.5 `duration` — fun, líneas 63–67

```kotlin
    fun duration(baseMilliseconds: Int, animationsEnabled: Boolean, animationSpeed: Float): Int {
        if (!animationsEnabled) return 0
        val speed = animationSpeed.coerceIn(0.5f, 2f)
        return (baseMilliseconds / speed).roundToInt().coerceAtLeast(1)
    }
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `baseMilliseconds: Int` — `baseMilliseconds` recibe un valor de tipo `Int`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.
- `animationsEnabled: Boolean` — `animationsEnabled` recibe un valor de tipo `Boolean`. El contrato no marca este parámetro como anulable.
- `animationSpeed: Float` — `animationSpeed` recibe un valor de tipo `Float`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.

**Retorno:** `Int`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.


#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 65 | `val speed` | `inferido` | `animationSpeed.coerceIn(0.5f, 2f)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El inicializador limita explícitamente el valor a un rango, evitando que dimensiones/intensidades/sizes inválidos se propaguen. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 64 | `if (!animationsEnabled) return 0` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 66 | `return (baseMilliseconds / speed).roundToInt().coerceAtLeast(1)` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

**Restricciones concretas que aparecen en este bloque:**
- **Límite numérico:** Se fuerza el valor a un intervalo o umbral para impedir que salgan valores fuera de los límites esperados. Apariciones en este bloque: 2.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `animationSpeed.coerceIn`, `roundToInt`, `coerceAtLeast`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.6 `easing` — fun, líneas 68–75

```kotlin
    fun easing(key: String): Easing = when (normalizeEasing(key)) {
            "linear" -> LinearEasing
            "accelerate" -> FastOutLinearInEasing
            "decelerate" -> LinearOutSlowInEasing
            "emphasized" -> CubicBezierEasing(0.2f, 0f, 0f, 1f)
            else -> FastOutSlowInEasing
        }
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `key: String` — `key` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable. Aunque el tipo sea `String`, semánticamente suele funcionar como clave/enumeración textual; las ramas del código delimitan los valores reconocidos.

**Retorno:** `Easing`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.


#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 69 | `"linear" -> LinearEasing` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 70 | `"accelerate" -> FastOutLinearInEasing` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 71 | `"decelerate" -> LinearOutSlowInEasing` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 72 | `"emphasized" -> CubicBezierEasing(0.2f, 0f, 0f, 1f)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 73 | `else -> FastOutSlowInEasing` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `normalizeEasing`, `CubicBezierEasing`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.

### 4.7 `AnimatedContentTransitionScope` — fun, líneas 79–218

```kotlin
private fun <T> AnimatedContentTransitionScope<T>.motionTransform(animationsEnabled: Boolean, animationSpeed: Float, animationStyle: String,
    animationEasing: String, animationIntensity: Float, performanceMode: String): ContentTransform {
    if (!animationsEnabled) {
        return EnterTransition.None togetherWith ExitTransition.None
    }
    val mode = AppMotion.normalizePerformanceMode(performanceMode)
    val requestedStyle = AppMotion.normalizeStyle(animationStyle)
    /*
     * Expand/shrink modifica el layout completo en cada frame.
     * En los perfiles orientados a fluidez conservamos una apariencia
     * equivalente mediante transformaciones de capa (scale/slide/fade),
     * que son mucho más baratas en dispositivos antiguos.
     */
    val style = when (mode) {
            "performance" -> when (requestedStyle) {
                    "expand", "axis_z", "bounce", "elastic" -> "zoom_fade"
                    "expand_horizontal" -> "slide_zoom_left"
                    "expand_vertical" -> "slide_zoom_up"
                    else -> requestedStyle
                }
            "balanced" -> when (requestedStyle) {
                    "expand" -> "zoom_fade"
                    "expand_horizontal" -> "slide_zoom_left"
                    "expand_vertical" -> "slide_zoom_up"
                    else -> requestedStyle
                }
            else -> requestedStyle
        }
    val speedBoost = when (mode) {
            "performance" -> 1.28f
            "balanced" -> 1.10f
            else -> 1f
        }
    val speed = (animationSpeed * speedBoost).coerceIn(0.5f, 2.4f)
    val intensityFactor = if (mode == "performance") {
            0.82f
        } else {
            1f
        }
    val intensity = (animationIntensity * intensityFactor).coerceIn(0.45f, 1.5f)
    val easing = AppMotion.easing(animationEasing)
    val enterDuration = AppMotion.duration(AppMotion.NORMAL, true, speed)
    /*
     * Con salida casi instantánea el contenido anterior deja de dibujarse
     * muy pronto y no compite con la pantalla entrante por CPU/GPU.
     */
    val exitDuration = if (mode == "performance") {
            24
        } else {
            AppMotion.duration(AppMotion.FAST, true, speed)
        }
    val slowDuration = AppMotion.duration(AppMotion.SLOW, true, speed)
    fun <V> enterSpec(): FiniteAnimationSpec<V> = tween(durationMillis = enterDuration, easing = easing)
    fun <V> exitSpec(): FiniteAnimationSpec<V> = tween(durationMillis = exitDuration, easing = easing)
    fun <V> slowSpec(): FiniteAnimationSpec<V> = tween(durationMillis = slowDuration, easing = easing)
    val zoomScale = (1f - 0.08f * intensity).coerceIn(0.78f, 0.98f)
    val deepZoomScale = (1f - 0.16f * intensity).coerceIn(0.68f, 0.95f)
    val subtleScale = (1f - 0.025f * intensity).coerceIn(0.94f, 0.99f)
    val slideFactor = intensity.coerceIn(0.5f, 1.5f)
    fun horizontalOffset(fullWidth: Int): Int = (fullWidth * slideFactor).roundToInt()
    fun verticalOffset(fullHeight: Int): Int = (fullHeight * slideFactor).roundToInt()
    return when (style) {
        "fade" -> fadeIn(initialAlpha = (1f - 0.70f * intensity).coerceIn(0f, 0.65f), animationSpec = enterSpec()) togetherWith
                fadeOut(targetAlpha = (1f - 0.85f * intensity).coerceIn(0f, 0.55f), animationSpec = exitSpec())
        "slide_left" -> slideInHorizontally(initialOffsetX = { horizontalOffset(it) }, animationSpec = enterSpec()) togetherWith
                slideOutHorizontally(targetOffsetX = { -horizontalOffset(it) }, animationSpec = exitSpec())
        "slide_right" -> slideInHorizontally(initialOffsetX = { -horizontalOffset(it) }, animationSpec = enterSpec()) togetherWith
                slideOutHorizontally(targetOffsetX = { horizontalOffset(it) }, animationSpec = exitSpec())
        "slide_up" -> slideInVertically(initialOffsetY = { verticalOffset(it) }, animationSpec = enterSpec()) togetherWith
                slideOutVertically(targetOffsetY = { -verticalOffset(it) }, animationSpec = exitSpec())
        "slide_down" -> slideInVertically(initialOffsetY = { -verticalOffset(it) }, animationSpec = enterSpec()) togetherWith
                slideOutVertically(targetOffsetY = { verticalOffset(it) }, animationSpec = exitSpec())
        "slide_zoom_left" -> (slideInHorizontally(initialOffsetX = { (horizontalOffset(it) * 0.70f).roundToInt() },
                    animationSpec = enterSpec()) + scaleIn(initialScale = zoomScale, animationSpec = enterSpec()) + fadeIn(
                        initialAlpha = 0.55f, animationSpec = enterSpec())) togetherWith
                (slideOutHorizontally(targetOffsetX = { (-horizontalOffset(it) * 0.40f).roundToInt() }, animationSpec = exitSpec()) +
                        scaleOut(targetScale = zoomScale, animationSpec = exitSpec()) + fadeOut(targetAlpha = 0.25f,
                            animationSpec = exitSpec()))
        "slide_zoom_up" -> (slideInVertically(initialOffsetY = { (verticalOffset(it) * 0.65f).roundToInt() }, animationSpec = enterSpec()
                ) + scaleIn(initialScale = zoomScale, animationSpec = enterSpec()) + fadeIn(initialAlpha = 0.55f,
                        animationSpec = enterSpec())) togetherWith
                (slideOutVertically(targetOffsetY = { (-verticalOffset(it) * 0.35f).roundToInt() }, animationSpec = exitSpec()) + scaleOut(
                            targetScale = zoomScale, animationSpec = exitSpec()) + fadeOut(targetAlpha = 0.25f, animationSpec = exitSpec())
                    )
        "axis_x" -> (slideInHorizontally(initialOffsetX = { (horizontalOffset(it) * 0.28f).roundToInt() }, animationSpec = enterSpec()) +
                    fadeIn(initialAlpha = 0.25f, animationSpec = enterSpec()) +
                    scaleIn(initialScale = subtleScale, animationSpec = enterSpec())) togetherWith
                (slideOutHorizontally(targetOffsetX = { (-horizontalOffset(it) * 0.16f).roundToInt() }, animationSpec = exitSpec()) +
                        fadeOut(targetAlpha = 0.15f, animationSpec = exitSpec()) +
                        scaleOut(targetScale = subtleScale, animationSpec = exitSpec()))
        "axis_y" -> (slideInVertically(initialOffsetY = { (verticalOffset(it) * 0.24f).roundToInt() }, animationSpec = enterSpec()) +
                    fadeIn(initialAlpha = 0.25f, animationSpec = enterSpec()) +
                    scaleIn(initialScale = subtleScale, animationSpec = enterSpec())) togetherWith
                (slideOutVertically(targetOffsetY = { (-verticalOffset(it) * 0.14f).roundToInt() }, animationSpec = exitSpec()) +
                        fadeOut(targetAlpha = 0.15f, animationSpec = exitSpec()) +
                        scaleOut(targetScale = subtleScale, animationSpec = exitSpec()))
        "axis_z" -> (scaleIn(initialScale = deepZoomScale, animationSpec = slowSpec()) + fadeIn(initialAlpha = 0.15f,
                        animationSpec = slowSpec())) togetherWith
                (scaleOut(targetScale = (1f + 0.05f * intensity).coerceAtMost(1.12f), animationSpec = exitSpec()) + fadeOut(
                            targetAlpha = 0f, animationSpec = exitSpec()))
        "expand" -> (expandIn(expandFrom = Alignment.Center, animationSpec = enterSpec()) +
                    fadeIn(initialAlpha = 0.30f, animationSpec = enterSpec())) togetherWith
                (shrinkOut(shrinkTowards = Alignment.Center, animationSpec = exitSpec()) +
                        fadeOut(targetAlpha = 0.10f, animationSpec = exitSpec()))
        "expand_horizontal" -> (expandHorizontally(expandFrom = Alignment.CenterHorizontally, animationSpec = enterSpec()) +
                    fadeIn(initialAlpha = 0.35f, animationSpec = enterSpec())) togetherWith
                (shrinkHorizontally(shrinkTowards = Alignment.CenterHorizontally, animationSpec = exitSpec()) +
                        fadeOut(targetAlpha = 0.10f, animationSpec = exitSpec()))
        "expand_vertical" -> (expandVertically(expandFrom = Alignment.CenterVertically, animationSpec = enterSpec()) +
                    fadeIn(initialAlpha = 0.35f, animationSpec = enterSpec())) togetherWith
                (shrinkVertically(shrinkTowards = Alignment.CenterVertically, animationSpec = exitSpec()) +
                        fadeOut(targetAlpha = 0.10f, animationSpec = exitSpec()))
        "bounce" -> {
            val bounceSpec = spring<Float>(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium * speed)
            (scaleIn(initialScale = deepZoomScale, animationSpec = bounceSpec) + fadeIn(initialAlpha = 0.35f, animationSpec = enterSpec())
                ) togetherWith
                (scaleOut(targetScale = zoomScale, animationSpec = exitSpec()) + fadeOut(targetAlpha = 0.05f, animationSpec = exitSpec()))
        }
        "elastic" -> {
            val elasticSpec = spring<Float>(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow * speed)
            (scaleIn(initialScale = deepZoomScale, animationSpec = elasticSpec) + fadeIn(initialAlpha = 0.25f, animationSpec = enterSpec())
                ) togetherWith
                (scaleOut(targetScale = subtleScale, animationSpec = exitSpec()) + fadeOut(targetAlpha = 0.05f, animationSpec = exitSpec())
                    )
        }
        "pop" -> (scaleIn(initialScale = (1f - 0.25f * intensity).coerceIn(0.58f, 0.88f), animationSpec = enterSpec()) +
                    fadeIn(initialAlpha = 0.15f, animationSpec = enterSpec())) togetherWith
                (scaleOut(targetScale = (1f + 0.08f * intensity).coerceAtMost(1.15f), animationSpec = exitSpec()) +
                        fadeOut(targetAlpha = 0f, animationSpec = exitSpec()))
        "subtle" -> (scaleIn(initialScale = subtleScale, animationSpec = enterSpec()) +
                    fadeIn(initialAlpha = 0.72f, animationSpec = enterSpec())) togetherWith
                (scaleOut(targetScale = subtleScale, animationSpec = exitSpec()) + fadeOut(targetAlpha = 0.65f, animationSpec = exitSpec())
                    )
        "zoom_fade" -> (scaleIn(initialScale = zoomScale, animationSpec = enterSpec()) + fadeIn(initialAlpha = 0.35f,
                        animationSpec = enterSpec())) togetherWith
                (scaleOut(targetScale = zoomScale, animationSpec = exitSpec()) + fadeOut(targetAlpha = 0.10f, animationSpec = exitSpec()))
        else -> scaleIn(initialScale = zoomScale, animationSpec = enterSpec()) togetherWith
                scaleOut(targetScale = zoomScale, animationSpec = exitSpec())
    }
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `animationsEnabled: Boolean` — `animationsEnabled` recibe un valor de tipo `Boolean`. El contrato no marca este parámetro como anulable.
- `animationSpeed: Float` — `animationSpeed` recibe un valor de tipo `Float`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.
- `animationStyle: String` — `animationStyle` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.
- `animationEasing: String` — `animationEasing` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.
- `animationIntensity: Float` — `animationIntensity` recibe un valor de tipo `Float`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.
- `performanceMode: String` — `performanceMode` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable. Aunque el tipo sea `String`, semánticamente suele funcionar como clave/enumeración textual; las ramas del código delimitan los valores reconocidos.

**Retorno:** `ContentTransform`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 84 | `val mode` | `inferido` | `AppMotion.normalizePerformanceMode(performanceMode)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Actúa como selector de estrategia/perfil; su valor determina qué rama de configuración se aplica. |
| 85 | `val requestedStyle` | `inferido` | `AppMotion.normalizeStyle(animationStyle)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 92 | `val style` | `inferido` | `when (mode) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 107 | `val speedBoost` | `inferido` | `when (mode) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 112 | `val speed` | `inferido` | `(animationSpeed * speedBoost).coerceIn(0.5f, 2.4f)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El inicializador limita explícitamente el valor a un rango, evitando que dimensiones/intensidades/sizes inválidos se propaguen. |
| 113 | `val intensityFactor` | `inferido` | `if (mode == "performance") {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 118 | `val intensity` | `inferido` | `(animationIntensity * intensityFactor).coerceIn(0.45f, 1.5f)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El inicializador limita explícitamente el valor a un rango, evitando que dimensiones/intensidades/sizes inválidos se propaguen. |
| 119 | `val easing` | `inferido` | `AppMotion.easing(animationEasing)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 120 | `val enterDuration` | `inferido` | `AppMotion.duration(AppMotion.NORMAL, true, speed)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 125 | `val exitDuration` | `inferido` | `if (mode == "performance") {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 130 | `val slowDuration` | `inferido` | `AppMotion.duration(AppMotion.SLOW, true, speed)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 134 | `val zoomScale` | `inferido` | `(1f - 0.08f * intensity).coerceIn(0.78f, 0.98f)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El inicializador limita explícitamente el valor a un rango, evitando que dimensiones/intensidades/sizes inválidos se propaguen. |
| 135 | `val deepZoomScale` | `inferido` | `(1f - 0.16f * intensity).coerceIn(0.68f, 0.95f)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El inicializador limita explícitamente el valor a un rango, evitando que dimensiones/intensidades/sizes inválidos se propaguen. |
| 136 | `val subtleScale` | `inferido` | `(1f - 0.025f * intensity).coerceIn(0.94f, 0.99f)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El inicializador limita explícitamente el valor a un rango, evitando que dimensiones/intensidades/sizes inválidos se propaguen. |
| 137 | `val slideFactor` | `inferido` | `intensity.coerceIn(0.5f, 1.5f)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El inicializador limita explícitamente el valor a un rango, evitando que dimensiones/intensidades/sizes inválidos se propaguen. |
| 192 | `val bounceSpec` | `inferido` | `spring<Float>(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium * s…` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 198 | `val elasticSpec` | `inferido` | `spring<Float>(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow * speed)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 81 | `if (!animationsEnabled) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 82 | `return EnterTransition.None togetherWith ExitTransition.None` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 93 | `"performance" -> when (requestedStyle) {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 94 | `"expand", "axis_z", "bounce", "elastic" -> "zoom_fade"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 95 | `"expand_horizontal" -> "slide_zoom_left"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 96 | `"expand_vertical" -> "slide_zoom_up"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 97 | `else -> requestedStyle` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |
| 99 | `"balanced" -> when (requestedStyle) {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 100 | `"expand" -> "zoom_fade"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 101 | `"expand_horizontal" -> "slide_zoom_left"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 102 | `"expand_vertical" -> "slide_zoom_up"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 103 | `else -> requestedStyle` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |
| 105 | `else -> requestedStyle` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |
| 108 | `"performance" -> 1.28f` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 109 | `"balanced" -> 1.10f` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 110 | `else -> 1f` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |
| 140 | `return when (style) {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 141 | `"fade" -> fadeIn(initialAlpha = (1f - 0.70f * intensity).coerceIn(0f, 0.65f), animationSpec = enterSpec()) togetherWith` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 143 | `"slide_left" -> slideInHorizontally(initialOffsetX = { horizontalOffset(it) }, animationSpec = enterSpec()) togetherWith` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 145 | `"slide_right" -> slideInHorizontally(initialOffsetX = { -horizontalOffset(it) }, animationSpec = enterSpec()) togetherWith` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 147 | `"slide_up" -> slideInVertically(initialOffsetY = { verticalOffset(it) }, animationSpec = enterSpec()) togetherWith` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 149 | `"slide_down" -> slideInVertically(initialOffsetY = { -verticalOffset(it) }, animationSpec = enterSpec()) togetherWith` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 151 | `"slide_zoom_left" -> (slideInHorizontally(initialOffsetX = { (horizontalOffset(it) * 0.70f).roundToInt() },` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 157 | `"slide_zoom_up" -> (slideInVertically(initialOffsetY = { (verticalOffset(it) * 0.65f).roundToInt() }, animationSpec = enterSpec()` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 163 | `"axis_x" -> (slideInHorizontally(initialOffsetX = { (horizontalOffset(it) * 0.28f).roundToInt() }, animationSpec = enterSpec()) +` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 169 | `"axis_y" -> (slideInVertically(initialOffsetY = { (verticalOffset(it) * 0.24f).roundToInt() }, animationSpec = enterSpec()) +` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 175 | `"axis_z" -> (scaleIn(initialScale = deepZoomScale, animationSpec = slowSpec()) + fadeIn(initialAlpha = 0.15f,` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 179 | `"expand" -> (expandIn(expandFrom = Alignment.Center, animationSpec = enterSpec()) +` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 183 | `"expand_horizontal" -> (expandHorizontally(expandFrom = Alignment.CenterHorizontally, animationSpec = enterSpec()) +` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 187 | `"expand_vertical" -> (expandVertically(expandFrom = Alignment.CenterVertically, animationSpec = enterSpec()) +` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 191 | `"bounce" -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 197 | `"elastic" -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 204 | `"pop" -> (scaleIn(initialScale = (1f - 0.25f * intensity).coerceIn(0.58f, 0.88f), animationSpec = enterSpec()) +` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 208 | `"subtle" -> (scaleIn(initialScale = subtleScale, animationSpec = enterSpec()) +` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 212 | `"zoom_fade" -> (scaleIn(initialScale = zoomScale, animationSpec = enterSpec()) + fadeIn(initialAlpha = 0.35f,` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 215 | `else -> scaleIn(initialScale = zoomScale, animationSpec = enterSpec()) togetherWith` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |

**Restricciones concretas que aparecen en este bloque:**
- **Límite numérico:** Se fuerza el valor a un intervalo o umbral para impedir que salgan valores fuera de los límites esperados. Apariciones en este bloque: 11.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `motionTransform`, `AppMotion.normalizePerformanceMode`, `AppMotion.normalizeStyle`, `coerceIn`, `AppMotion.easing`, `AppMotion.duration`, `enterSpec`, `tween`, `exitSpec`, `slowSpec`, `intensity.coerceIn`, `roundToInt`, `fadeIn`, `fadeOut`, `slideInHorizontally`, `horizontalOffset`, `slideOutHorizontally`, `slideInVertically`, `verticalOffset`, `slideOutVertically`, `scaleIn`, `togetherWith`, `scaleOut`, `coerceAtMost`, `expandIn`, `shrinkOut`, `expandHorizontally`, `shrinkHorizontally`, `expandVertically`, `shrinkVertically`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.
- Preservar la normalización y correspondencia de `performanceMode`, porque otras capas usan las mismas claves para rendimiento/calidad.

### 4.8 `enterSpec` — fun, líneas 131–132

```kotlin
    fun <V> enterSpec(): FiniteAnimationSpec<V> = tween(durationMillis = enterDuration, easing = easing)
    fun <V> exitSpec(): FiniteAnimationSpec<V> = tween(durationMillis = exitDuration, easing = easing)
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:** ninguno explícito.

**Retorno:** `FiniteAnimationSpec<V>`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.


#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `enterSpec`, `tween`, `exitSpec`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.9 `exitSpec` — fun, líneas 132–137

```kotlin
    fun <V> exitSpec(): FiniteAnimationSpec<V> = tween(durationMillis = exitDuration, easing = easing)
    fun <V> slowSpec(): FiniteAnimationSpec<V> = tween(durationMillis = slowDuration, easing = easing)
    val zoomScale = (1f - 0.08f * intensity).coerceIn(0.78f, 0.98f)
    val deepZoomScale = (1f - 0.16f * intensity).coerceIn(0.68f, 0.95f)
    val subtleScale = (1f - 0.025f * intensity).coerceIn(0.94f, 0.99f)
    val slideFactor = intensity.coerceIn(0.5f, 1.5f)
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:** ninguno explícito.

**Retorno:** `FiniteAnimationSpec<V>`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.


#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 134 | `val zoomScale` | `inferido` | `(1f - 0.08f * intensity).coerceIn(0.78f, 0.98f)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El inicializador limita explícitamente el valor a un rango, evitando que dimensiones/intensidades/sizes inválidos se propaguen. |
| 135 | `val deepZoomScale` | `inferido` | `(1f - 0.16f * intensity).coerceIn(0.68f, 0.95f)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El inicializador limita explícitamente el valor a un rango, evitando que dimensiones/intensidades/sizes inválidos se propaguen. |
| 136 | `val subtleScale` | `inferido` | `(1f - 0.025f * intensity).coerceIn(0.94f, 0.99f)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El inicializador limita explícitamente el valor a un rango, evitando que dimensiones/intensidades/sizes inválidos se propaguen. |
| 137 | `val slideFactor` | `inferido` | `intensity.coerceIn(0.5f, 1.5f)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El inicializador limita explícitamente el valor a un rango, evitando que dimensiones/intensidades/sizes inválidos se propaguen. |

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

**Restricciones concretas que aparecen en este bloque:**
- **Límite numérico:** Se fuerza el valor a un intervalo o umbral para impedir que salgan valores fuera de los límites esperados. Apariciones en este bloque: 4.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `exitSpec`, `tween`, `slowSpec`, `coerceIn`, `intensity.coerceIn`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.10 `slowSpec` — fun, líneas 133–137

```kotlin
    fun <V> slowSpec(): FiniteAnimationSpec<V> = tween(durationMillis = slowDuration, easing = easing)
    val zoomScale = (1f - 0.08f * intensity).coerceIn(0.78f, 0.98f)
    val deepZoomScale = (1f - 0.16f * intensity).coerceIn(0.68f, 0.95f)
    val subtleScale = (1f - 0.025f * intensity).coerceIn(0.94f, 0.99f)
    val slideFactor = intensity.coerceIn(0.5f, 1.5f)
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:** ninguno explícito.

**Retorno:** `FiniteAnimationSpec<V>`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.


#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 134 | `val zoomScale` | `inferido` | `(1f - 0.08f * intensity).coerceIn(0.78f, 0.98f)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El inicializador limita explícitamente el valor a un rango, evitando que dimensiones/intensidades/sizes inválidos se propaguen. |
| 135 | `val deepZoomScale` | `inferido` | `(1f - 0.16f * intensity).coerceIn(0.68f, 0.95f)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El inicializador limita explícitamente el valor a un rango, evitando que dimensiones/intensidades/sizes inválidos se propaguen. |
| 136 | `val subtleScale` | `inferido` | `(1f - 0.025f * intensity).coerceIn(0.94f, 0.99f)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El inicializador limita explícitamente el valor a un rango, evitando que dimensiones/intensidades/sizes inválidos se propaguen. |
| 137 | `val slideFactor` | `inferido` | `intensity.coerceIn(0.5f, 1.5f)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El inicializador limita explícitamente el valor a un rango, evitando que dimensiones/intensidades/sizes inválidos se propaguen. |

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

**Restricciones concretas que aparecen en este bloque:**
- **Límite numérico:** Se fuerza el valor a un intervalo o umbral para impedir que salgan valores fuera de los límites esperados. Apariciones en este bloque: 4.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `slowSpec`, `tween`, `coerceIn`, `intensity.coerceIn`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.11 `horizontalOffset` — fun, líneas 138–218

```kotlin
    fun horizontalOffset(fullWidth: Int): Int = (fullWidth * slideFactor).roundToInt()
    fun verticalOffset(fullHeight: Int): Int = (fullHeight * slideFactor).roundToInt()
    return when (style) {
        "fade" -> fadeIn(initialAlpha = (1f - 0.70f * intensity).coerceIn(0f, 0.65f), animationSpec = enterSpec()) togetherWith
                fadeOut(targetAlpha = (1f - 0.85f * intensity).coerceIn(0f, 0.55f), animationSpec = exitSpec())
        "slide_left" -> slideInHorizontally(initialOffsetX = { horizontalOffset(it) }, animationSpec = enterSpec()) togetherWith
                slideOutHorizontally(targetOffsetX = { -horizontalOffset(it) }, animationSpec = exitSpec())
        "slide_right" -> slideInHorizontally(initialOffsetX = { -horizontalOffset(it) }, animationSpec = enterSpec()) togetherWith
                slideOutHorizontally(targetOffsetX = { horizontalOffset(it) }, animationSpec = exitSpec())
        "slide_up" -> slideInVertically(initialOffsetY = { verticalOffset(it) }, animationSpec = enterSpec()) togetherWith
                slideOutVertically(targetOffsetY = { -verticalOffset(it) }, animationSpec = exitSpec())
        "slide_down" -> slideInVertically(initialOffsetY = { -verticalOffset(it) }, animationSpec = enterSpec()) togetherWith
                slideOutVertically(targetOffsetY = { verticalOffset(it) }, animationSpec = exitSpec())
        "slide_zoom_left" -> (slideInHorizontally(initialOffsetX = { (horizontalOffset(it) * 0.70f).roundToInt() },
                    animationSpec = enterSpec()) + scaleIn(initialScale = zoomScale, animationSpec = enterSpec()) + fadeIn(
                        initialAlpha = 0.55f, animationSpec = enterSpec())) togetherWith
                (slideOutHorizontally(targetOffsetX = { (-horizontalOffset(it) * 0.40f).roundToInt() }, animationSpec = exitSpec()) +
                        scaleOut(targetScale = zoomScale, animationSpec = exitSpec()) + fadeOut(targetAlpha = 0.25f,
                            animationSpec = exitSpec()))
        "slide_zoom_up" -> (slideInVertically(initialOffsetY = { (verticalOffset(it) * 0.65f).roundToInt() }, animationSpec = enterSpec()
                ) + scaleIn(initialScale = zoomScale, animationSpec = enterSpec()) + fadeIn(initialAlpha = 0.55f,
                        animationSpec = enterSpec())) togetherWith
                (slideOutVertically(targetOffsetY = { (-verticalOffset(it) * 0.35f).roundToInt() }, animationSpec = exitSpec()) + scaleOut(
                            targetScale = zoomScale, animationSpec = exitSpec()) + fadeOut(targetAlpha = 0.25f, animationSpec = exitSpec())
                    )
        "axis_x" -> (slideInHorizontally(initialOffsetX = { (horizontalOffset(it) * 0.28f).roundToInt() }, animationSpec = enterSpec()) +
                    fadeIn(initialAlpha = 0.25f, animationSpec = enterSpec()) +
                    scaleIn(initialScale = subtleScale, animationSpec = enterSpec())) togetherWith
                (slideOutHorizontally(targetOffsetX = { (-horizontalOffset(it) * 0.16f).roundToInt() }, animationSpec = exitSpec()) +
                        fadeOut(targetAlpha = 0.15f, animationSpec = exitSpec()) +
                        scaleOut(targetScale = subtleScale, animationSpec = exitSpec()))
        "axis_y" -> (slideInVertically(initialOffsetY = { (verticalOffset(it) * 0.24f).roundToInt() }, animationSpec = enterSpec()) +
                    fadeIn(initialAlpha = 0.25f, animationSpec = enterSpec()) +
                    scaleIn(initialScale = subtleScale, animationSpec = enterSpec())) togetherWith
                (slideOutVertically(targetOffsetY = { (-verticalOffset(it) * 0.14f).roundToInt() }, animationSpec = exitSpec()) +
                        fadeOut(targetAlpha = 0.15f, animationSpec = exitSpec()) +
                        scaleOut(targetScale = subtleScale, animationSpec = exitSpec()))
        "axis_z" -> (scaleIn(initialScale = deepZoomScale, animationSpec = slowSpec()) + fadeIn(initialAlpha = 0.15f,
                        animationSpec = slowSpec())) togetherWith
                (scaleOut(targetScale = (1f + 0.05f * intensity).coerceAtMost(1.12f), animationSpec = exitSpec()) + fadeOut(
                            targetAlpha = 0f, animationSpec = exitSpec()))
        "expand" -> (expandIn(expandFrom = Alignment.Center, animationSpec = enterSpec()) +
                    fadeIn(initialAlpha = 0.30f, animationSpec = enterSpec())) togetherWith
                (shrinkOut(shrinkTowards = Alignment.Center, animationSpec = exitSpec()) +
                        fadeOut(targetAlpha = 0.10f, animationSpec = exitSpec()))
        "expand_horizontal" -> (expandHorizontally(expandFrom = Alignment.CenterHorizontally, animationSpec = enterSpec()) +
                    fadeIn(initialAlpha = 0.35f, animationSpec = enterSpec())) togetherWith
                (shrinkHorizontally(shrinkTowards = Alignment.CenterHorizontally, animationSpec = exitSpec()) +
                        fadeOut(targetAlpha = 0.10f, animationSpec = exitSpec()))
        "expand_vertical" -> (expandVertically(expandFrom = Alignment.CenterVertically, animationSpec = enterSpec()) +
                    fadeIn(initialAlpha = 0.35f, animationSpec = enterSpec())) togetherWith
                (shrinkVertically(shrinkTowards = Alignment.CenterVertically, animationSpec = exitSpec()) +
                        fadeOut(targetAlpha = 0.10f, animationSpec = exitSpec()))
        "bounce" -> {
            val bounceSpec = spring<Float>(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium * speed)
            (scaleIn(initialScale = deepZoomScale, animationSpec = bounceSpec) + fadeIn(initialAlpha = 0.35f, animationSpec = enterSpec())
                ) togetherWith
                (scaleOut(targetScale = zoomScale, animationSpec = exitSpec()) + fadeOut(targetAlpha = 0.05f, animationSpec = exitSpec()))
        }
        "elastic" -> {
            val elasticSpec = spring<Float>(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow * speed)
            (scaleIn(initialScale = deepZoomScale, animationSpec = elasticSpec) + fadeIn(initialAlpha = 0.25f, animationSpec = enterSpec())
                ) togetherWith
                (scaleOut(targetScale = subtleScale, animationSpec = exitSpec()) + fadeOut(targetAlpha = 0.05f, animationSpec = exitSpec())
                    )
        }
        "pop" -> (scaleIn(initialScale = (1f - 0.25f * intensity).coerceIn(0.58f, 0.88f), animationSpec = enterSpec()) +
                    fadeIn(initialAlpha = 0.15f, animationSpec = enterSpec())) togetherWith
                (scaleOut(targetScale = (1f + 0.08f * intensity).coerceAtMost(1.15f), animationSpec = exitSpec()) +
                        fadeOut(targetAlpha = 0f, animationSpec = exitSpec()))
        "subtle" -> (scaleIn(initialScale = subtleScale, animationSpec = enterSpec()) +
                    fadeIn(initialAlpha = 0.72f, animationSpec = enterSpec())) togetherWith
                (scaleOut(targetScale = subtleScale, animationSpec = exitSpec()) + fadeOut(targetAlpha = 0.65f, animationSpec = exitSpec())
                    )
        "zoom_fade" -> (scaleIn(initialScale = zoomScale, animationSpec = enterSpec()) + fadeIn(initialAlpha = 0.35f,
                        animationSpec = enterSpec())) togetherWith
                (scaleOut(targetScale = zoomScale, animationSpec = exitSpec()) + fadeOut(targetAlpha = 0.10f, animationSpec = exitSpec()))
        else -> scaleIn(initialScale = zoomScale, animationSpec = enterSpec()) togetherWith
                scaleOut(targetScale = zoomScale, animationSpec = exitSpec())
    }
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `fullWidth: Int` — `fullWidth` recibe un valor de tipo `Int`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.

**Retorno:** `Int`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.


#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 192 | `val bounceSpec` | `inferido` | `spring<Float>(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium * s…` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 198 | `val elasticSpec` | `inferido` | `spring<Float>(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow * speed)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 140 | `return when (style) {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 141 | `"fade" -> fadeIn(initialAlpha = (1f - 0.70f * intensity).coerceIn(0f, 0.65f), animationSpec = enterSpec()) togetherWith` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 143 | `"slide_left" -> slideInHorizontally(initialOffsetX = { horizontalOffset(it) }, animationSpec = enterSpec()) togetherWith` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 145 | `"slide_right" -> slideInHorizontally(initialOffsetX = { -horizontalOffset(it) }, animationSpec = enterSpec()) togetherWith` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 147 | `"slide_up" -> slideInVertically(initialOffsetY = { verticalOffset(it) }, animationSpec = enterSpec()) togetherWith` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 149 | `"slide_down" -> slideInVertically(initialOffsetY = { -verticalOffset(it) }, animationSpec = enterSpec()) togetherWith` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 151 | `"slide_zoom_left" -> (slideInHorizontally(initialOffsetX = { (horizontalOffset(it) * 0.70f).roundToInt() },` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 157 | `"slide_zoom_up" -> (slideInVertically(initialOffsetY = { (verticalOffset(it) * 0.65f).roundToInt() }, animationSpec = enterSpec()` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 163 | `"axis_x" -> (slideInHorizontally(initialOffsetX = { (horizontalOffset(it) * 0.28f).roundToInt() }, animationSpec = enterSpec()) +` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 169 | `"axis_y" -> (slideInVertically(initialOffsetY = { (verticalOffset(it) * 0.24f).roundToInt() }, animationSpec = enterSpec()) +` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 175 | `"axis_z" -> (scaleIn(initialScale = deepZoomScale, animationSpec = slowSpec()) + fadeIn(initialAlpha = 0.15f,` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 179 | `"expand" -> (expandIn(expandFrom = Alignment.Center, animationSpec = enterSpec()) +` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 183 | `"expand_horizontal" -> (expandHorizontally(expandFrom = Alignment.CenterHorizontally, animationSpec = enterSpec()) +` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 187 | `"expand_vertical" -> (expandVertically(expandFrom = Alignment.CenterVertically, animationSpec = enterSpec()) +` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 191 | `"bounce" -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 197 | `"elastic" -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 204 | `"pop" -> (scaleIn(initialScale = (1f - 0.25f * intensity).coerceIn(0.58f, 0.88f), animationSpec = enterSpec()) +` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 208 | `"subtle" -> (scaleIn(initialScale = subtleScale, animationSpec = enterSpec()) +` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 212 | `"zoom_fade" -> (scaleIn(initialScale = zoomScale, animationSpec = enterSpec()) + fadeIn(initialAlpha = 0.35f,` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 215 | `else -> scaleIn(initialScale = zoomScale, animationSpec = enterSpec()) togetherWith` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |

**Restricciones concretas que aparecen en este bloque:**
- **Límite numérico:** Se fuerza el valor a un intervalo o umbral para impedir que salgan valores fuera de los límites esperados. Apariciones en este bloque: 5.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `roundToInt`, `fadeIn`, `coerceIn`, `enterSpec`, `fadeOut`, `exitSpec`, `slideInHorizontally`, `horizontalOffset`, `slideOutHorizontally`, `slideInVertically`, `verticalOffset`, `slideOutVertically`, `scaleIn`, `togetherWith`, `scaleOut`, `slowSpec`, `coerceAtMost`, `expandIn`, `shrinkOut`, `expandHorizontally`, `shrinkHorizontally`, `expandVertically`, `shrinkVertically`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.

### 4.12 `verticalOffset` — fun, líneas 139–218

```kotlin
    fun verticalOffset(fullHeight: Int): Int = (fullHeight * slideFactor).roundToInt()
    return when (style) {
        "fade" -> fadeIn(initialAlpha = (1f - 0.70f * intensity).coerceIn(0f, 0.65f), animationSpec = enterSpec()) togetherWith
                fadeOut(targetAlpha = (1f - 0.85f * intensity).coerceIn(0f, 0.55f), animationSpec = exitSpec())
        "slide_left" -> slideInHorizontally(initialOffsetX = { horizontalOffset(it) }, animationSpec = enterSpec()) togetherWith
                slideOutHorizontally(targetOffsetX = { -horizontalOffset(it) }, animationSpec = exitSpec())
        "slide_right" -> slideInHorizontally(initialOffsetX = { -horizontalOffset(it) }, animationSpec = enterSpec()) togetherWith
                slideOutHorizontally(targetOffsetX = { horizontalOffset(it) }, animationSpec = exitSpec())
        "slide_up" -> slideInVertically(initialOffsetY = { verticalOffset(it) }, animationSpec = enterSpec()) togetherWith
                slideOutVertically(targetOffsetY = { -verticalOffset(it) }, animationSpec = exitSpec())
        "slide_down" -> slideInVertically(initialOffsetY = { -verticalOffset(it) }, animationSpec = enterSpec()) togetherWith
                slideOutVertically(targetOffsetY = { verticalOffset(it) }, animationSpec = exitSpec())
        "slide_zoom_left" -> (slideInHorizontally(initialOffsetX = { (horizontalOffset(it) * 0.70f).roundToInt() },
                    animationSpec = enterSpec()) + scaleIn(initialScale = zoomScale, animationSpec = enterSpec()) + fadeIn(
                        initialAlpha = 0.55f, animationSpec = enterSpec())) togetherWith
                (slideOutHorizontally(targetOffsetX = { (-horizontalOffset(it) * 0.40f).roundToInt() }, animationSpec = exitSpec()) +
                        scaleOut(targetScale = zoomScale, animationSpec = exitSpec()) + fadeOut(targetAlpha = 0.25f,
                            animationSpec = exitSpec()))
        "slide_zoom_up" -> (slideInVertically(initialOffsetY = { (verticalOffset(it) * 0.65f).roundToInt() }, animationSpec = enterSpec()
                ) + scaleIn(initialScale = zoomScale, animationSpec = enterSpec()) + fadeIn(initialAlpha = 0.55f,
                        animationSpec = enterSpec())) togetherWith
                (slideOutVertically(targetOffsetY = { (-verticalOffset(it) * 0.35f).roundToInt() }, animationSpec = exitSpec()) + scaleOut(
                            targetScale = zoomScale, animationSpec = exitSpec()) + fadeOut(targetAlpha = 0.25f, animationSpec = exitSpec())
                    )
        "axis_x" -> (slideInHorizontally(initialOffsetX = { (horizontalOffset(it) * 0.28f).roundToInt() }, animationSpec = enterSpec()) +
                    fadeIn(initialAlpha = 0.25f, animationSpec = enterSpec()) +
                    scaleIn(initialScale = subtleScale, animationSpec = enterSpec())) togetherWith
                (slideOutHorizontally(targetOffsetX = { (-horizontalOffset(it) * 0.16f).roundToInt() }, animationSpec = exitSpec()) +
                        fadeOut(targetAlpha = 0.15f, animationSpec = exitSpec()) +
                        scaleOut(targetScale = subtleScale, animationSpec = exitSpec()))
        "axis_y" -> (slideInVertically(initialOffsetY = { (verticalOffset(it) * 0.24f).roundToInt() }, animationSpec = enterSpec()) +
                    fadeIn(initialAlpha = 0.25f, animationSpec = enterSpec()) +
                    scaleIn(initialScale = subtleScale, animationSpec = enterSpec())) togetherWith
                (slideOutVertically(targetOffsetY = { (-verticalOffset(it) * 0.14f).roundToInt() }, animationSpec = exitSpec()) +
                        fadeOut(targetAlpha = 0.15f, animationSpec = exitSpec()) +
                        scaleOut(targetScale = subtleScale, animationSpec = exitSpec()))
        "axis_z" -> (scaleIn(initialScale = deepZoomScale, animationSpec = slowSpec()) + fadeIn(initialAlpha = 0.15f,
                        animationSpec = slowSpec())) togetherWith
                (scaleOut(targetScale = (1f + 0.05f * intensity).coerceAtMost(1.12f), animationSpec = exitSpec()) + fadeOut(
                            targetAlpha = 0f, animationSpec = exitSpec()))
        "expand" -> (expandIn(expandFrom = Alignment.Center, animationSpec = enterSpec()) +
                    fadeIn(initialAlpha = 0.30f, animationSpec = enterSpec())) togetherWith
                (shrinkOut(shrinkTowards = Alignment.Center, animationSpec = exitSpec()) +
                        fadeOut(targetAlpha = 0.10f, animationSpec = exitSpec()))
        "expand_horizontal" -> (expandHorizontally(expandFrom = Alignment.CenterHorizontally, animationSpec = enterSpec()) +
                    fadeIn(initialAlpha = 0.35f, animationSpec = enterSpec())) togetherWith
                (shrinkHorizontally(shrinkTowards = Alignment.CenterHorizontally, animationSpec = exitSpec()) +
                        fadeOut(targetAlpha = 0.10f, animationSpec = exitSpec()))
        "expand_vertical" -> (expandVertically(expandFrom = Alignment.CenterVertically, animationSpec = enterSpec()) +
                    fadeIn(initialAlpha = 0.35f, animationSpec = enterSpec())) togetherWith
                (shrinkVertically(shrinkTowards = Alignment.CenterVertically, animationSpec = exitSpec()) +
                        fadeOut(targetAlpha = 0.10f, animationSpec = exitSpec()))
        "bounce" -> {
            val bounceSpec = spring<Float>(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium * speed)
            (scaleIn(initialScale = deepZoomScale, animationSpec = bounceSpec) + fadeIn(initialAlpha = 0.35f, animationSpec = enterSpec())
                ) togetherWith
                (scaleOut(targetScale = zoomScale, animationSpec = exitSpec()) + fadeOut(targetAlpha = 0.05f, animationSpec = exitSpec()))
        }
        "elastic" -> {
            val elasticSpec = spring<Float>(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow * speed)
            (scaleIn(initialScale = deepZoomScale, animationSpec = elasticSpec) + fadeIn(initialAlpha = 0.25f, animationSpec = enterSpec())
                ) togetherWith
                (scaleOut(targetScale = subtleScale, animationSpec = exitSpec()) + fadeOut(targetAlpha = 0.05f, animationSpec = exitSpec())
                    )
        }
        "pop" -> (scaleIn(initialScale = (1f - 0.25f * intensity).coerceIn(0.58f, 0.88f), animationSpec = enterSpec()) +
                    fadeIn(initialAlpha = 0.15f, animationSpec = enterSpec())) togetherWith
                (scaleOut(targetScale = (1f + 0.08f * intensity).coerceAtMost(1.15f), animationSpec = exitSpec()) +
                        fadeOut(targetAlpha = 0f, animationSpec = exitSpec()))
        "subtle" -> (scaleIn(initialScale = subtleScale, animationSpec = enterSpec()) +
                    fadeIn(initialAlpha = 0.72f, animationSpec = enterSpec())) togetherWith
                (scaleOut(targetScale = subtleScale, animationSpec = exitSpec()) + fadeOut(targetAlpha = 0.65f, animationSpec = exitSpec())
                    )
        "zoom_fade" -> (scaleIn(initialScale = zoomScale, animationSpec = enterSpec()) + fadeIn(initialAlpha = 0.35f,
                        animationSpec = enterSpec())) togetherWith
                (scaleOut(targetScale = zoomScale, animationSpec = exitSpec()) + fadeOut(targetAlpha = 0.10f, animationSpec = exitSpec()))
        else -> scaleIn(initialScale = zoomScale, animationSpec = enterSpec()) togetherWith
                scaleOut(targetScale = zoomScale, animationSpec = exitSpec())
    }
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `fullHeight: Int` — `fullHeight` recibe un valor de tipo `Int`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.

**Retorno:** `Int`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.


#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 192 | `val bounceSpec` | `inferido` | `spring<Float>(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium * s…` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 198 | `val elasticSpec` | `inferido` | `spring<Float>(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow * speed)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 140 | `return when (style) {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 141 | `"fade" -> fadeIn(initialAlpha = (1f - 0.70f * intensity).coerceIn(0f, 0.65f), animationSpec = enterSpec()) togetherWith` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 143 | `"slide_left" -> slideInHorizontally(initialOffsetX = { horizontalOffset(it) }, animationSpec = enterSpec()) togetherWith` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 145 | `"slide_right" -> slideInHorizontally(initialOffsetX = { -horizontalOffset(it) }, animationSpec = enterSpec()) togetherWith` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 147 | `"slide_up" -> slideInVertically(initialOffsetY = { verticalOffset(it) }, animationSpec = enterSpec()) togetherWith` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 149 | `"slide_down" -> slideInVertically(initialOffsetY = { -verticalOffset(it) }, animationSpec = enterSpec()) togetherWith` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 151 | `"slide_zoom_left" -> (slideInHorizontally(initialOffsetX = { (horizontalOffset(it) * 0.70f).roundToInt() },` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 157 | `"slide_zoom_up" -> (slideInVertically(initialOffsetY = { (verticalOffset(it) * 0.65f).roundToInt() }, animationSpec = enterSpec()` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 163 | `"axis_x" -> (slideInHorizontally(initialOffsetX = { (horizontalOffset(it) * 0.28f).roundToInt() }, animationSpec = enterSpec()) +` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 169 | `"axis_y" -> (slideInVertically(initialOffsetY = { (verticalOffset(it) * 0.24f).roundToInt() }, animationSpec = enterSpec()) +` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 175 | `"axis_z" -> (scaleIn(initialScale = deepZoomScale, animationSpec = slowSpec()) + fadeIn(initialAlpha = 0.15f,` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 179 | `"expand" -> (expandIn(expandFrom = Alignment.Center, animationSpec = enterSpec()) +` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 183 | `"expand_horizontal" -> (expandHorizontally(expandFrom = Alignment.CenterHorizontally, animationSpec = enterSpec()) +` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 187 | `"expand_vertical" -> (expandVertically(expandFrom = Alignment.CenterVertically, animationSpec = enterSpec()) +` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 191 | `"bounce" -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 197 | `"elastic" -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 204 | `"pop" -> (scaleIn(initialScale = (1f - 0.25f * intensity).coerceIn(0.58f, 0.88f), animationSpec = enterSpec()) +` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 208 | `"subtle" -> (scaleIn(initialScale = subtleScale, animationSpec = enterSpec()) +` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 212 | `"zoom_fade" -> (scaleIn(initialScale = zoomScale, animationSpec = enterSpec()) + fadeIn(initialAlpha = 0.35f,` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 215 | `else -> scaleIn(initialScale = zoomScale, animationSpec = enterSpec()) togetherWith` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |

**Restricciones concretas que aparecen en este bloque:**
- **Límite numérico:** Se fuerza el valor a un intervalo o umbral para impedir que salgan valores fuera de los límites esperados. Apariciones en este bloque: 5.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `roundToInt`, `fadeIn`, `coerceIn`, `enterSpec`, `fadeOut`, `exitSpec`, `slideInHorizontally`, `horizontalOffset`, `slideOutHorizontally`, `slideInVertically`, `verticalOffset`, `slideOutVertically`, `scaleIn`, `togetherWith`, `scaleOut`, `slowSpec`, `coerceAtMost`, `expandIn`, `shrinkOut`, `expandHorizontally`, `shrinkHorizontally`, `expandVertically`, `shrinkVertically`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.

### 4.13 `ConfigurableAnimatedContent` — fun, líneas 226–248

```kotlin
fun <T> ConfigurableAnimatedContent(targetState: T, animationsEnabled: Boolean, animationSpeed: Float, animationStyle: String,
    animationEasing: String, animationIntensity: Float, performanceMode: String = "balanced", modifier: Modifier = Modifier,
    content: @Composable (T) -> Unit) {
    val resolvedStyle = remember(targetState, animationStyle) {
            val normalized = AppMotion.normalizeStyle(animationStyle)
            if (normalized == "random") {
                randomStylePool.random()
            } else {
                normalized
            }
        }
    Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background), contentAlignment = Alignment.Center) {
        AnimatedContent(targetState = targetState, modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center,
            transitionSpec = {
                motionTransform(animationsEnabled = animationsEnabled, animationSpeed = animationSpeed, animationStyle = resolvedStyle,
                    animationEasing = animationEasing, animationIntensity = animationIntensity, performanceMode = performanceMode)
            }, label = "screenMotionTransition") { state -> Box(modifier = Modifier.fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)) {
                content(state)
            }
        }
    }
}
```

#### Qué hace y por qué existe

Composable que declara una parte de la interfaz. Su cuerpo transforma estado y parámetros en UI y callbacks, y puede reevaluarse durante recomposiciones.

#### Contrato de la declaración

**Parámetros:**

- `targetState: T` — `targetState` recibe un valor de tipo `T`. El contrato no marca este parámetro como anulable.
- `animationsEnabled: Boolean` — `animationsEnabled` recibe un valor de tipo `Boolean`. El contrato no marca este parámetro como anulable.
- `animationSpeed: Float` — `animationSpeed` recibe un valor de tipo `Float`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.
- `animationStyle: String` — `animationStyle` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.
- `animationEasing: String` — `animationEasing` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.
- `animationIntensity: Float` — `animationIntensity` recibe un valor de tipo `Float`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.
- `performanceMode: String = "balanced"` — `performanceMode` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable. Tiene valor por defecto `"balanced"`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada. Aunque el tipo sea `String`, semánticamente suele funcionar como clave/enumeración textual; las ramas del código delimitan los valores reconocidos.
- `modifier: Modifier = Modifier` — `modifier` recibe un valor de tipo `Modifier`. El contrato no marca este parámetro como anulable. Tiene valor por defecto `Modifier`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada.
- `content: @Composable (T) -> Unit` — `content` recibe un valor de tipo `@Composable (T) -> Unit`. El contrato no marca este parámetro como anulable. Es un callback: el bloque no posee necesariamente la acción final, sino que notifica/delega al consumidor.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `@Composable` convierte la función en una unidad declarativa de Compose y la somete a reglas de recomposición.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 229 | `val resolvedStyle` | `inferido` | `remember(targetState, animationStyle) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 230 | `val normalized` | `inferido` | `AppMotion.normalizeStyle(animationStyle)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 228 | `content: @Composable (T) -> Unit) {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 231 | `if (normalized == "random") {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |

#### Semántica Compose/lifecycle

- **`@Composable`:** La función describe UI declarativa y puede ejecutarse nuevamente por recomposición; no debe interpretarse como una ejecución única imperativa.
- **`remember`:** Conserva valores entre recomposiciones bajo las mismas claves.
- **`Modifier`:** La cadena de modificadores define tamaño, interacción, dibujo, semántica o posicionamiento; el orden de los modificadores puede ser significativo.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `ConfigurableAnimatedContent`, `Composable`, `remember`, `AppMotion.normalizeStyle`, `randomStylePool.random`, `Box`, `modifier.fillMaxSize`, `background`, `AnimatedContent`, `Modifier.fillMaxSize`, `motionTransform`, `content`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- No convertir estado de Compose en variables ordinarias sin considerar recomposición y persistencia temporal.
- Preservar la normalización y correspondencia de `performanceMode`, porque otras capas usan las mismas claves para rendimiento/calidad.

### 4.14 `ZoomAnimatedContent` — fun, líneas 252–257

```kotlin
fun <T> ZoomAnimatedContent(targetState: T, animationsEnabled: Boolean, animationSpeed: Float, modifier: Modifier = Modifier,
    content: @Composable (T) -> Unit) {
    ConfigurableAnimatedContent(targetState = targetState, animationsEnabled = animationsEnabled, animationSpeed = animationSpeed,
        animationStyle = "zoom", animationEasing = "standard", animationIntensity = 1f, performanceMode = "balanced", modifier = modifier,
        content = content)
}
```

#### Qué hace y por qué existe

Composable que declara una parte de la interfaz. Su cuerpo transforma estado y parámetros en UI y callbacks, y puede reevaluarse durante recomposiciones.

#### Contrato de la declaración

**Parámetros:**

- `targetState: T` — `targetState` recibe un valor de tipo `T`. El contrato no marca este parámetro como anulable.
- `animationsEnabled: Boolean` — `animationsEnabled` recibe un valor de tipo `Boolean`. El contrato no marca este parámetro como anulable.
- `animationSpeed: Float` — `animationSpeed` recibe un valor de tipo `Float`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.
- `modifier: Modifier = Modifier` — `modifier` recibe un valor de tipo `Modifier`. El contrato no marca este parámetro como anulable. Tiene valor por defecto `Modifier`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada.
- `content: @Composable (T) -> Unit` — `content` recibe un valor de tipo `@Composable (T) -> Unit`. El contrato no marca este parámetro como anulable. Es un callback: el bloque no posee necesariamente la acción final, sino que notifica/delega al consumidor.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `@Composable` convierte la función en una unidad declarativa de Compose y la somete a reglas de recomposición.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 253 | `content: @Composable (T) -> Unit) {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |

#### Semántica Compose/lifecycle

- **`@Composable`:** La función describe UI declarativa y puede ejecutarse nuevamente por recomposición; no debe interpretarse como una ejecución única imperativa.
- **`Modifier`:** La cadena de modificadores define tamaño, interacción, dibujo, semántica o posicionamiento; el orden de los modificadores puede ser significativo.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `ZoomAnimatedContent`, `Composable`, `ConfigurableAnimatedContent`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Preservar la normalización y correspondencia de `performanceMode`, porque otras capas usan las mismas claves para rendimiento/calidad.

### 4.15 `AnimatedScreenEntry` — fun, líneas 264–269

```kotlin
fun AnimatedScreenEntry(animationsEnabled: Boolean, animationSpeed: Float, modifier: Modifier = Modifier, content: @Composable () -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        content()
    }
}
```

#### Qué hace y por qué existe

Composable que declara una parte de la interfaz. Su cuerpo transforma estado y parámetros en UI y callbacks, y puede reevaluarse durante recomposiciones.

#### Contrato de la declaración

**Parámetros:**

- `animationsEnabled: Boolean` — `animationsEnabled` recibe un valor de tipo `Boolean`. El contrato no marca este parámetro como anulable.
- `animationSpeed: Float` — `animationSpeed` recibe un valor de tipo `Float`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.
- `modifier: Modifier = Modifier` — `modifier` recibe un valor de tipo `Modifier`. El contrato no marca este parámetro como anulable. Tiene valor por defecto `Modifier`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada.
- `content: @Composable () -> Unit` — `content` recibe un valor de tipo `@Composable () -> Unit`. El contrato no marca este parámetro como anulable. Es un callback: el bloque no posee necesariamente la acción final, sino que notifica/delega al consumidor.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `@Composable` convierte la función en una unidad declarativa de Compose y la somete a reglas de recomposición.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 264 | `fun AnimatedScreenEntry(animationsEnabled: Boolean, animationSpeed: Float, modifier: Modifier = Modifier, content: @Composable () -> Unit` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |

#### Semántica Compose/lifecycle

- **`@Composable`:** La función describe UI declarativa y puede ejecutarse nuevamente por recomposición; no debe interpretarse como una ejecución única imperativa.
- **`Modifier`:** La cadena de modificadores define tamaño, interacción, dibujo, semántica o posicionamiento; el orden de los modificadores puede ser significativo.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `Composable`, `Box`, `modifier.fillMaxSize`, `content`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

## 5. Inventario global de propiedades/variables detectadas

| Línea | Identificador | Mutabilidad | Tipo | Explicación |
|---:|---|---|---|---|
| 50 | `FAST` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Está marcada `const`, por lo que es una constante de tiempo de compilación y solo puede usar un tipo/valor admitido por Kotlin para constantes. |
| 51 | `NORMAL` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Está marcada `const`, por lo que es una constante de tiempo de compilación y solo puede usar un tipo/valor admitido por Kotlin para constantes. |
| 52 | `SLOW` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Está marcada `const`, por lo que es una constante de tiempo de compilación y solo puede usar un tipo/valor admitido por Kotlin para constantes. |
| 53 | `supportedStyles` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 56 | `supportedEasings` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 65 | `speed` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El inicializador limita explícitamente el valor a un rango, evitando que dimensiones/intensidades/sizes inválidos se propaguen. |
| 77 | `randomStylePool` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 84 | `mode` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Actúa como selector de estrategia/perfil; su valor determina qué rama de configuración se aplica. |
| 85 | `requestedStyle` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 92 | `style` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 107 | `speedBoost` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 112 | `speed` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El inicializador limita explícitamente el valor a un rango, evitando que dimensiones/intensidades/sizes inválidos se propaguen. |
| 113 | `intensityFactor` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 118 | `intensity` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El inicializador limita explícitamente el valor a un rango, evitando que dimensiones/intensidades/sizes inválidos se propaguen. |
| 119 | `easing` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 120 | `enterDuration` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 125 | `exitDuration` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 130 | `slowDuration` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 134 | `zoomScale` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El inicializador limita explícitamente el valor a un rango, evitando que dimensiones/intensidades/sizes inválidos se propaguen. |
| 135 | `deepZoomScale` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El inicializador limita explícitamente el valor a un rango, evitando que dimensiones/intensidades/sizes inválidos se propaguen. |
| 136 | `subtleScale` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El inicializador limita explícitamente el valor a un rango, evitando que dimensiones/intensidades/sizes inválidos se propaguen. |
| 137 | `slideFactor` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El inicializador limita explícitamente el valor a un rango, evitando que dimensiones/intensidades/sizes inválidos se propaguen. |
| 192 | `bounceSpec` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 198 | `elasticSpec` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 229 | `resolvedStyle` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 230 | `normalized` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

## 6. Mapa exhaustivo de TODOS los bloques delimitados por `{}`

Esta tabla recorre todas las llaves estructurales detectadas fuera de strings/comentarios. Así se documentan también lambdas de Compose, callbacks, iteraciones y ámbitos locales que no tienen un nombre propio de función.

| Inicio–fin | Prof. | Cabecera/contexto | Qué significa ese bloque |
|---|---:|---|---|
| 49–75 | 0 | `object AppMotion` | Cuerpo de tipo/contenedor: agrupa propiedades y miembros bajo la misma responsabilidad y controla su visibilidad/estado compartido. |
| 59–62 | 1 | `fun normalizePerformanceMode(value: String): String = when (value)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 63–67 | 1 | `fun duration(baseMilliseconds: Int, animationsEnabled: Boolean, animationSpeed: Float): Int` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 68–74 | 1 | `fun easing(key: String): Easing = when (normalizeEasing(key))` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 80–218 | 0 | `animationEasing: String, animationIntensity: Float, performanceMode: String): ContentTransform` | Ámbito delimitado por llaves en profundidad 0. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 81–83 | 1 | `if (!animationsEnabled)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 92–106 | 1 | `val style = when (mode)` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 93–98 | 2 | `"performance" -> when (requestedStyle)` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 99–104 | 2 | `"balanced" -> when (requestedStyle)` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 107–111 | 1 | `val speedBoost = when (mode)` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 113–115 | 1 | `val intensityFactor = if (mode == "performance")` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 115–117 | 1 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 125–127 | 1 | `val exitDuration = if (mode == "performance")` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 127–129 | 1 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 140–217 | 1 | `return when (style)` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 143–143 | 2 | `"slide_left" -> slideInHorizontally(initialOffsetX =` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 144–144 | 2 | `slideOutHorizontally(targetOffsetX =` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 145–145 | 2 | `"slide_right" -> slideInHorizontally(initialOffsetX =` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 146–146 | 2 | `slideOutHorizontally(targetOffsetX =` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 147–147 | 2 | `"slide_up" -> slideInVertically(initialOffsetY =` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 148–148 | 2 | `slideOutVertically(targetOffsetY =` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 149–149 | 2 | `"slide_down" -> slideInVertically(initialOffsetY =` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 150–150 | 2 | `slideOutVertically(targetOffsetY =` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 151–151 | 2 | `"slide_zoom_left" -> (slideInHorizontally(initialOffsetX =` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 154–154 | 2 | `(slideOutHorizontally(targetOffsetX =` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 157–157 | 2 | `"slide_zoom_up" -> (slideInVertically(initialOffsetY =` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 160–160 | 2 | `(slideOutVertically(targetOffsetY =` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 163–163 | 2 | `"axis_x" -> (slideInHorizontally(initialOffsetX =` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 166–166 | 2 | `(slideOutHorizontally(targetOffsetX =` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 169–169 | 2 | `"axis_y" -> (slideInVertically(initialOffsetY =` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 172–172 | 2 | `(slideOutVertically(targetOffsetY =` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 191–196 | 2 | `"bounce" ->` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 197–203 | 2 | `"elastic" ->` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 228–248 | 0 | `content: @Composable (T) -> Unit)` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 229–236 | 1 | `val resolvedStyle = remember(targetState, animationStyle)` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 231–233 | 2 | `if (normalized == "random")` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 233–235 | 2 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 237–247 | 1 | `Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background), contentAlignment = Alignment.Center)` | Lambda de contenido de UI Compose: declara hijos y configuración visual dentro del contenedor. El orden y los `Modifier` influyen en medida, posición, dibujo e interacción. |
| 239–242 | 2 | `transitionSpec =` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 242–246 | 2 | `}, label = "screenMotionTransition")` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 243–245 | 3 | `.background(MaterialTheme.colorScheme.background))` | Ámbito delimitado por llaves en profundidad 3. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 253–257 | 0 | `content: @Composable (T) -> Unit)` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 265–269 | 0 | `)` | Ámbito delimitado por llaves en profundidad 0. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 266–268 | 1 | `Box(modifier = modifier.fillMaxSize())` | Lambda de contenido de UI Compose: declara hijos y configuración visual dentro del contenedor. El orden y los `Modifier` influyen en medida, posición, dibujo e interacción. |

## 7. Lectura de seguridad antes de tocar este archivo

La documentación describe **lo que el código actual ya hace**. No constituye una propuesta de refactorización. Si se quisiera cambiar algo en el futuro, primero habría que preservar contratos públicos, claves de configuración, restricciones de API Android, comportamiento de Compose, lifecycle, operaciones de I/O y compatibilidad con los datos ya persistidos. En esta entrega no se hizo ninguna de esas modificaciones.

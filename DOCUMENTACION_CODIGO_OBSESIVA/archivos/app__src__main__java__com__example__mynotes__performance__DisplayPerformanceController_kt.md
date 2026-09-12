# DisplayPerformanceController.kt — documentación exhaustiva por bloques

**Ruta de código real:** `app/src/main/java/com/example/mynotes/performance/DisplayPerformanceController.kt`  
**SHA-256 del archivo ejecutable sin tocar:** `18167bab1876feb4905daebe3eab8190e9164dcda090f933a807548759693b90`  
**Líneas del código real:** 119

> **Garantía:** esta documentación se genera en una carpeta paralela. El archivo Kotlin/Kotlin DSL anterior no se modifica. Los fragmentos mostrados abajo son copias de lectura; la aplicación compila usando únicamente el archivo original de `app/...`.

## 1. Papel del archivo

Centraliza la frecuencia de refresco preferida de las ventanas de MyNotes de acuerdo con el perfil de rendimiento.

**Arquitectura.** Evita duplicar reglas de 60/120 Hz en Activities. Selecciona un modo compatible del panel, recuerda la última petición por Window y permite reaplicarla al volver a primer plano.

**Flujo general.** Flujo típico: la Activity recibe/observa `performanceMode` -> llama `requestForPerformanceMode` -> se normaliza la clave -> se determina 60/120 Hz -> se buscan modos de pantalla compatibles preservando resolución -> se escribe la preferencia en `WindowManager.LayoutParams`.

## 2. Package e imports

El `package` es `com.example.mynotes.performance`. Determina el namespace lógico del archivo, resolución de visibilidad `internal`/package-related tooling y la ruta conceptual desde la que otras clases lo importan.

El archivo declara **5 imports**. Cada import evita usar el nombre totalmente calificado en el cuerpo y, además, revela las dependencias técnicas del bloque:

**Android/plataforma:** `android.os.Build`, `android.view.Display`, `android.view.Window`.

**Kotlin/corrutinas/Java:** `java.util.WeakHashMap`, `kotlin.math.abs`.

## 3. Restricciones e invariantes visibles en el archivo

- **Nulabilidad segura (3 aparición/apariciones):** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`.
- **Fallback nulo (1 aparición/apariciones):** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula.
- **Normalización vacía (1 aparición/apariciones):** `orEmpty` transforma una referencia nula en una colección/cadena vacía, simplificando el consumo posterior.
- **Guardia de API (1 aparición/apariciones):** Se condiciona una API según la versión de Android para no ejecutar llamadas incompatibles en dispositivos antiguos.

## 4. Bloques de código, uno por uno

### 4.1 `DisplayPerformanceController` — object, líneas 25–119

```kotlin
object DisplayPerformanceController {
    // … el cuerpo completo permanece en el archivo real; sus miembros se documentan individualmente abajo …
}
```

#### Qué hace y por qué existe

Centraliza la frecuencia de refresco preferida de las ventanas de MyNotes de acuerdo con el perfil de rendimiento.

#### Contrato de la declaración


#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 31 | `val lastRequestedMode` | `inferido` | `WeakHashMap<Window, String>()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa claves débiles para no prolongar artificialmente la vida del objeto clave y reducir riesgo de fugas de memoria. Actúa como selector de estrategia/perfil; su valor determina qué rama de configuración se aplica. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 33 | `val normalizedMode` | `inferido` | `normalizePerformanceMode(performanceMode)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Actúa como selector de estrategia/perfil; su valor determina qué rama de configuración se aplica. |
| 42 | `val mode` | `inferido` | `lastRequestedMode[window]?: return` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Actúa como selector de estrategia/perfil; su valor determina qué rama de configuración se aplica. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 63 | `val attributes` | `inferido` | `window.attributes` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 71 | `val display` | `inferido` | `window.decorView.display` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 72 | `val currentMode` | `inferido` | `display?.mode` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Actúa como selector de estrategia/perfil; su valor determina qué rama de configuración se aplica. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 73 | `val supportedModes` | `inferido` | `display?.supportedModes?.toList().orEmpty()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Actúa como selector de estrategia/perfil; su valor determina qué rama de configuración se aplica. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. Normaliza una ausencia hacia un valor vacío/alternativo para simplificar el código consumidor. |
| 79 | `val sameResolutionModes` | `inferido` | `if (currentMode != null) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Actúa como selector de estrategia/perfil; su valor determina qué rama de configuración se aplica. |
| 86 | `val resolutionCandidates` | `inferido` | `sameResolutionModes.ifEmpty {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 94 | `val candidates` | `inferido` | `resolutionCandidates.filter { mode -> mode.refreshRate <= targetRefreshRate + REFRESH_RATE_TOLERANCE` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 98 | `val bestMode` | `inferido` | `chooseClosestMode(modes = candidates, targetRefreshRate = targetRefreshRate)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Actúa como selector de estrategia/perfil; su valor determina qué rama de configuración se aplica. |
| 113 | `val MODE_PERFORMANCE` | `inferido` | `"performance"` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Actúa como selector de estrategia/perfil; su valor determina qué rama de configuración se aplica. Está marcada `const`, por lo que es una constante de tiempo de compilación y solo puede usar un tipo/valor admitido por Kotlin para constantes. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 114 | `val MODE_BALANCED` | `inferido` | `"balanced"` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Actúa como selector de estrategia/perfil; su valor determina qué rama de configuración se aplica. Está marcada `const`, por lo que es una constante de tiempo de compilación y solo puede usar un tipo/valor admitido por Kotlin para constantes. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 115 | `val MODE_QUALITY` | `inferido` | `"quality"` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Actúa como selector de estrategia/perfil; su valor determina qué rama de configuración se aplica. Está marcada `const`, por lo que es una constante de tiempo de compilación y solo puede usar un tipo/valor admitido por Kotlin para constantes. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 116 | `val STANDARD_REFRESH_RATE` | `inferido` | `60f` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Está marcada `const`, por lo que es una constante de tiempo de compilación y solo puede usar un tipo/valor admitido por Kotlin para constantes. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 117 | `val QUALITY_REFRESH_RATE` | `inferido` | `120f` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Está marcada `const`, por lo que es una constante de tiempo de compilación y solo puede usar un tipo/valor admitido por Kotlin para constantes. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 118 | `val REFRESH_RATE_TOLERANCE` | `inferido` | `0.5f` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Está marcada `const`, por lo que es una constante de tiempo de compilación y solo puede usar un tipo/valor admitido por Kotlin para constantes. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 53 | `MODE_QUALITY -> QUALITY_REFRESH_RATE` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 54 | `MODE_PERFORMANCE, MODE_BALANCED -> STANDARD_REFRESH_RATE` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 55 | `else -> STANDARD_REFRESH_RATE` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |
| 58 | `MODE_PERFORMANCE -> MODE_PERFORMANCE` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 59 | `MODE_QUALITY -> MODE_QUALITY` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 60 | `else -> MODE_BALANCED` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |
| 70 | `if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 74 | `if (supportedModes.isNotEmpty()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 99 | `if (bestMode != null) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 3.
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 1.
- **Normalización vacía:** `orEmpty` transforma una referencia nula en una colección/cadena vacía, simplificando el consumo posterior. Apariciones en este bloque: 1.
- **Guardia de API:** Se condiciona una API según la versión de Android para no ejecutar llamadas incompatibles en dispositivos antiguos. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

- **Ventana/sistema:** Interactúa con la ventana/sistema Android; puede cambiar barras, modo de pantalla o atributos de presentación.
- **Mutación de colección/estado compartido:** Modifica una colección/mapa/estructura existente; el cambio permanece visible para quienes compartan esa misma instancia.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `normalizePerformanceMode`, `requestRefreshRate`, `refreshRateFor`, `lastRequestedMode.remove`, `value.trim`, `lowercase`, `toList`, `orEmpty`, `supportedModes.isNotEmpty`, `emptyList`, `chooseClosestMode`, `modes.minWithOrNull`, `abs`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.
- Conservar las guardas de versión antes de APIs que no existen en Android antiguos.
- Preservar la normalización y correspondencia de `performanceMode`, porque otras capas usan las mismas claves para rendimiento/calidad.

### 4.2 `requestForPerformanceMode` — fun, líneas 32–36

```kotlin
    fun requestForPerformanceMode(window: Window, performanceMode: String) {
        val normalizedMode = normalizePerformanceMode(performanceMode)
        lastRequestedMode[window] = normalizedMode
        requestRefreshRate(window = window, targetRefreshRate = refreshRateFor(normalizedMode))
    }
```

#### Qué hace y por qué existe

Emite una solicitud a una capa de plataforma/controlador y adapta los parámetros a las restricciones disponibles.

#### Contrato de la declaración

**Parámetros:**

- `window: Window` — `window` recibe un valor de tipo `Window`. El contrato no marca este parámetro como anulable.
- `performanceMode: String` — `performanceMode` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable. Aunque el tipo sea `String`, semánticamente suele funcionar como clave/enumeración textual; las ramas del código delimitan los valores reconocidos.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.


#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 33 | `val normalizedMode` | `inferido` | `normalizePerformanceMode(performanceMode)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Actúa como selector de estrategia/perfil; su valor determina qué rama de configuración se aplica. |

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

- **Mutación de colección/estado compartido:** Modifica una colección/mapa/estructura existente; el cambio permanece visible para quienes compartan esa misma instancia.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `normalizePerformanceMode`, `requestRefreshRate`, `refreshRateFor`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Preservar la normalización y correspondencia de `performanceMode`, porque otras capas usan las mismas claves para rendimiento/calidad.

### 4.3 `reapplyLastRequest` — fun, líneas 41–44

```kotlin
    fun reapplyLastRequest(window: Window) {
        val mode = lastRequestedMode[window]?: return
        requestRefreshRate(window = window, targetRefreshRate = refreshRateFor(mode))
    }
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `window: Window` — `window` recibe un valor de tipo `Window`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.


#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 42 | `val mode` | `inferido` | `lastRequestedMode[window]?: return` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Actúa como selector de estrategia/perfil; su valor determina qué rama de configuración se aplica. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

**Restricciones concretas que aparecen en este bloque:**
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `requestRefreshRate`, `refreshRateFor`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.4 `release` — fun, líneas 49–51

```kotlin
    fun release(window: Window) {
        lastRequestedMode.remove(window)
    }
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `window: Window` — `window` recibe un valor de tipo `Window`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.


#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

- **Mutación de colección/estado compartido:** Modifica una colección/mapa/estructura existente; el cambio permanece visible para quienes compartan esa misma instancia.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `lastRequestedMode.remove`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.5 `refreshRateFor` — fun, líneas 52–56

```kotlin
    private fun refreshRateFor(performanceMode: String): Float = when (performanceMode) {
            MODE_QUALITY -> QUALITY_REFRESH_RATE
            MODE_PERFORMANCE, MODE_BALANCED -> STANDARD_REFRESH_RATE
            else -> STANDARD_REFRESH_RATE
        }
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `performanceMode: String` — `performanceMode` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable. Aunque el tipo sea `String`, semánticamente suele funcionar como clave/enumeración textual; las ramas del código delimitan los valores reconocidos.

**Retorno:** `Float`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 53 | `MODE_QUALITY -> QUALITY_REFRESH_RATE` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 54 | `MODE_PERFORMANCE, MODE_BALANCED -> STANDARD_REFRESH_RATE` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 55 | `else -> STANDARD_REFRESH_RATE` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

No se detectaron llamadas de función relevantes fuera de la propia estructura de la declaración.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.
- Preservar la normalización y correspondencia de `performanceMode`, porque otras capas usan las mismas claves para rendimiento/calidad.

### 4.6 `normalizePerformanceMode` — fun, líneas 57–61

```kotlin
    private fun normalizePerformanceMode(value: String): String = when (value.trim().lowercase()) {
            MODE_PERFORMANCE -> MODE_PERFORMANCE
            MODE_QUALITY -> MODE_QUALITY
            else -> MODE_BALANCED
        }
```

#### Qué hace y por qué existe

Normaliza una entrada a un conjunto de valores aceptados, proporcionando una salida estable aunque el dato original venga con variantes no canónicas.

#### Contrato de la declaración

**Parámetros:**

- `value: String` — `value` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `String`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 58 | `MODE_PERFORMANCE -> MODE_PERFORMANCE` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 59 | `MODE_QUALITY -> MODE_QUALITY` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 60 | `else -> MODE_BALANCED` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `value.trim`, `lowercase`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.

### 4.7 `requestRefreshRate` — fun, líneas 62–106

```kotlin
    private fun requestRefreshRate(window: Window, targetRefreshRate: Float) {
        val attributes = window.attributes
        /*
         * preferredRefreshRate existe desde API 21. Android puede ignorar
         * la preferencia si el panel, compositor o estado actual no permiten
         * esa frecuencia.
         */
        attributes.preferredRefreshRate = targetRefreshRate
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val display = window.decorView.display
            val currentMode = display?.mode
            val supportedModes = display?.supportedModes?.toList().orEmpty()
            if (supportedModes.isNotEmpty()) {
                /*
                 * Primero mantenemos la resolución física actual. Solo si no
                 * existe ningún modo equivalente consideramos todos los modos.
                 */
                val sameResolutionModes = if (currentMode != null) {
                        supportedModes.filter { mode -> mode.physicalWidth == currentMode.physicalWidth &&
                                mode.physicalHeight == currentMode.physicalHeight
                        }
                    } else {
                        emptyList()
                    }
                val resolutionCandidates = sameResolutionModes.ifEmpty {
                        supportedModes
                    }
                /*
                 * En quality pedimos "hasta 120 Hz". Si el panel solo ofrece
                 * 144/165 Hz por encima del objetivo, preferimos el mejor modo
                 * disponible <= 120 Hz antes de forzar una tasa superior.
                 */
                val candidates = resolutionCandidates.filter { mode -> mode.refreshRate <= targetRefreshRate + REFRESH_RATE_TOLERANCE
                        }.ifEmpty {
                            resolutionCandidates
                        }
                val bestMode = chooseClosestMode(modes = candidates, targetRefreshRate = targetRefreshRate)
                if (bestMode != null) {
                    attributes.preferredDisplayModeId = bestMode.modeId
                    attributes.preferredRefreshRate = bestMode.refreshRate
                }
            }
        }
        window.attributes = attributes
    }
```

#### Qué hace y por qué existe

Emite una solicitud a una capa de plataforma/controlador y adapta los parámetros a las restricciones disponibles.

#### Contrato de la declaración

**Parámetros:**

- `window: Window` — `window` recibe un valor de tipo `Window`. El contrato no marca este parámetro como anulable.
- `targetRefreshRate: Float` — `targetRefreshRate` recibe un valor de tipo `Float`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 63 | `val attributes` | `inferido` | `window.attributes` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 71 | `val display` | `inferido` | `window.decorView.display` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 72 | `val currentMode` | `inferido` | `display?.mode` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Actúa como selector de estrategia/perfil; su valor determina qué rama de configuración se aplica. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 73 | `val supportedModes` | `inferido` | `display?.supportedModes?.toList().orEmpty()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Actúa como selector de estrategia/perfil; su valor determina qué rama de configuración se aplica. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. Normaliza una ausencia hacia un valor vacío/alternativo para simplificar el código consumidor. |
| 79 | `val sameResolutionModes` | `inferido` | `if (currentMode != null) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Actúa como selector de estrategia/perfil; su valor determina qué rama de configuración se aplica. |
| 86 | `val resolutionCandidates` | `inferido` | `sameResolutionModes.ifEmpty {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 94 | `val candidates` | `inferido` | `resolutionCandidates.filter { mode -> mode.refreshRate <= targetRefreshRate + REFRESH_RATE_TOLERANCE` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 98 | `val bestMode` | `inferido` | `chooseClosestMode(modes = candidates, targetRefreshRate = targetRefreshRate)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Actúa como selector de estrategia/perfil; su valor determina qué rama de configuración se aplica. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 70 | `if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 74 | `if (supportedModes.isNotEmpty()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 99 | `if (bestMode != null) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 3.
- **Normalización vacía:** `orEmpty` transforma una referencia nula en una colección/cadena vacía, simplificando el consumo posterior. Apariciones en este bloque: 1.
- **Guardia de API:** Se condiciona una API según la versión de Android para no ejecutar llamadas incompatibles en dispositivos antiguos. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

- **Ventana/sistema:** Interactúa con la ventana/sistema Android; puede cambiar barras, modo de pantalla o atributos de presentación.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `toList`, `orEmpty`, `supportedModes.isNotEmpty`, `emptyList`, `chooseClosestMode`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar las guardas de versión antes de APIs que no existen en Android antiguos.

### 4.8 `chooseClosestMode` — fun, líneas 107–119

```kotlin
    private fun chooseClosestMode(modes: List<Display.Mode>, targetRefreshRate: Float): Display.Mode? = modes.minWithOrNull(
            compareBy<Display.Mode> {
                abs(it.refreshRate - targetRefreshRate)
            }.thenByDescending {
                it.refreshRate
            })
    private const val MODE_PERFORMANCE = "performance"
    private const val MODE_BALANCED = "balanced"
    private const val MODE_QUALITY = "quality"
    private const val STANDARD_REFRESH_RATE = 60f
    private const val QUALITY_REFRESH_RATE = 120f
    private const val REFRESH_RATE_TOLERANCE = 0.5f
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `modes: List<Display.Mode>` — `modes` recibe un valor de tipo `List<Display.Mode>`. El contrato no marca este parámetro como anulable.
- `targetRefreshRate: Float` — `targetRefreshRate` recibe un valor de tipo `Float`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.

**Retorno:** `Display.Mode?`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.
- `const val` es una constante de tiempo de compilación; su valor no cambia durante la ejecución.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 113 | `val MODE_PERFORMANCE` | `inferido` | `"performance"` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Actúa como selector de estrategia/perfil; su valor determina qué rama de configuración se aplica. Está marcada `const`, por lo que es una constante de tiempo de compilación y solo puede usar un tipo/valor admitido por Kotlin para constantes. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 114 | `val MODE_BALANCED` | `inferido` | `"balanced"` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Actúa como selector de estrategia/perfil; su valor determina qué rama de configuración se aplica. Está marcada `const`, por lo que es una constante de tiempo de compilación y solo puede usar un tipo/valor admitido por Kotlin para constantes. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 115 | `val MODE_QUALITY` | `inferido` | `"quality"` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Actúa como selector de estrategia/perfil; su valor determina qué rama de configuración se aplica. Está marcada `const`, por lo que es una constante de tiempo de compilación y solo puede usar un tipo/valor admitido por Kotlin para constantes. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 116 | `val STANDARD_REFRESH_RATE` | `inferido` | `60f` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Está marcada `const`, por lo que es una constante de tiempo de compilación y solo puede usar un tipo/valor admitido por Kotlin para constantes. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 117 | `val QUALITY_REFRESH_RATE` | `inferido` | `120f` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Está marcada `const`, por lo que es una constante de tiempo de compilación y solo puede usar un tipo/valor admitido por Kotlin para constantes. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 118 | `val REFRESH_RATE_TOLERANCE` | `inferido` | `0.5f` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Está marcada `const`, por lo que es una constante de tiempo de compilación y solo puede usar un tipo/valor admitido por Kotlin para constantes. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `modes.minWithOrNull`, `abs`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

## 5. Inventario global de propiedades/variables detectadas

| Línea | Identificador | Mutabilidad | Tipo | Explicación |
|---:|---|---|---|---|
| 31 | `lastRequestedMode` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa claves débiles para no prolongar artificialmente la vida del objeto clave y reducir riesgo de fugas de memoria. Actúa como selector de estrategia/perfil; su valor determina qué rama de configuración se aplica. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 33 | `normalizedMode` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Actúa como selector de estrategia/perfil; su valor determina qué rama de configuración se aplica. |
| 42 | `mode` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Actúa como selector de estrategia/perfil; su valor determina qué rama de configuración se aplica. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 63 | `attributes` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 71 | `display` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 72 | `currentMode` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Actúa como selector de estrategia/perfil; su valor determina qué rama de configuración se aplica. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 73 | `supportedModes` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Actúa como selector de estrategia/perfil; su valor determina qué rama de configuración se aplica. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. Normaliza una ausencia hacia un valor vacío/alternativo para simplificar el código consumidor. |
| 79 | `sameResolutionModes` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Actúa como selector de estrategia/perfil; su valor determina qué rama de configuración se aplica. |
| 86 | `resolutionCandidates` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 94 | `candidates` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 98 | `bestMode` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Actúa como selector de estrategia/perfil; su valor determina qué rama de configuración se aplica. |
| 113 | `MODE_PERFORMANCE` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Actúa como selector de estrategia/perfil; su valor determina qué rama de configuración se aplica. Está marcada `const`, por lo que es una constante de tiempo de compilación y solo puede usar un tipo/valor admitido por Kotlin para constantes. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 114 | `MODE_BALANCED` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Actúa como selector de estrategia/perfil; su valor determina qué rama de configuración se aplica. Está marcada `const`, por lo que es una constante de tiempo de compilación y solo puede usar un tipo/valor admitido por Kotlin para constantes. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 115 | `MODE_QUALITY` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Actúa como selector de estrategia/perfil; su valor determina qué rama de configuración se aplica. Está marcada `const`, por lo que es una constante de tiempo de compilación y solo puede usar un tipo/valor admitido por Kotlin para constantes. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 116 | `STANDARD_REFRESH_RATE` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Está marcada `const`, por lo que es una constante de tiempo de compilación y solo puede usar un tipo/valor admitido por Kotlin para constantes. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 117 | `QUALITY_REFRESH_RATE` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Está marcada `const`, por lo que es una constante de tiempo de compilación y solo puede usar un tipo/valor admitido por Kotlin para constantes. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 118 | `REFRESH_RATE_TOLERANCE` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Está marcada `const`, por lo que es una constante de tiempo de compilación y solo puede usar un tipo/valor admitido por Kotlin para constantes. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |

## 6. Mapa exhaustivo de TODOS los bloques delimitados por `{}`

Esta tabla recorre todas las llaves estructurales detectadas fuera de strings/comentarios. Así se documentan también lambdas de Compose, callbacks, iteraciones y ámbitos locales que no tienen un nombre propio de función.

| Inicio–fin | Prof. | Cabecera/contexto | Qué significa ese bloque |
|---|---:|---|---|
| 25–119 | 0 | `object DisplayPerformanceController` | Cuerpo de tipo/contenedor: agrupa propiedades y miembros bajo la misma responsabilidad y controla su visibilidad/estado compartido. |
| 32–36 | 1 | `fun requestForPerformanceMode(window: Window, performanceMode: String)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 41–44 | 1 | `fun reapplyLastRequest(window: Window)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 49–51 | 1 | `fun release(window: Window)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 52–56 | 1 | `private fun refreshRateFor(performanceMode: String): Float = when (performanceMode)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 57–61 | 1 | `private fun normalizePerformanceMode(value: String): String = when (value.trim().lowercase())` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 62–106 | 1 | `private fun requestRefreshRate(window: Window, targetRefreshRate: Float)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 70–104 | 2 | `if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 74–103 | 3 | `if (supportedModes.isNotEmpty())` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 79–83 | 4 | `val sameResolutionModes = if (currentMode != null)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 80–82 | 5 | `supportedModes.filter` | Filtro de colección: conserva solo los elementos cuyo predicado devuelve verdadero. |
| 83–85 | 4 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 86–88 | 4 | `val resolutionCandidates = sameResolutionModes.ifEmpty` | Ámbito delimitado por llaves en profundidad 4. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 94–95 | 4 | `val candidates = resolutionCandidates.filter` | Filtro de colección: conserva solo los elementos cuyo predicado devuelve verdadero. |
| 95–97 | 4 | `}.ifEmpty` | Ámbito delimitado por llaves en profundidad 4. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 99–102 | 4 | `if (bestMode != null)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 108–110 | 1 | `compareBy<Display.Mode>` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 110–112 | 1 | `}.thenByDescending` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |

## 7. Lectura de seguridad antes de tocar este archivo

La documentación describe **lo que el código actual ya hace**. No constituye una propuesta de refactorización. Si se quisiera cambiar algo en el futuro, primero habría que preservar contratos públicos, claves de configuración, restricciones de API Android, comportamiento de Compose, lifecycle, operaciones de I/O y compatibilidad con los datos ya persistidos. En esta entrega no se hizo ninguna de esas modificaciones.

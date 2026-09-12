# UiSoundPlayer.kt — documentación exhaustiva por bloques

**Ruta de código real:** `app/src/main/java/com/example/mynotes/ui/sound/UiSoundPlayer.kt`  
**SHA-256 del archivo ejecutable sin tocar:** `3fb898aa5d8388080c45d16698c0a6acb4ffdd685bb346080991fda3deb25e63`  
**Líneas del código real:** 263

> **Garantía:** esta documentación se genera en una carpeta paralela. El archivo Kotlin/Kotlin DSL anterior no se modifica. Los fragmentos mostrados abajo son copias de lectura; la aplicación compila usando únicamente el archivo original de `app/...`.

## 1. Papel del archivo

Controlador de efectos de sonido de la interfaz. Gestiona temas/paquetes, volumen, carga/reutilización y reproducción asociada a acciones.

**Arquitectura.** Evita crear reproductores desde cada composable y mantiene una política coherente para sonidos de toque, selección, menú y otras acciones.

**Flujo general.** El flujo se describe declaración por declaración en este documento.

## 2. Package e imports

El `package` es `com.example.mynotes.ui.sound`. Determina el namespace lógico del archivo, resolución de visibilidad `internal`/package-related tooling y la ruta conceptual desde la que otras clases lo importan.

El archivo declara **6 imports**. Cada import evita usar el nombre totalmente calificado en el cuerpo y, además, revela las dependencias técnicas del bloque:

**Android/plataforma:** `android.content.Context`, `android.media.AudioAttributes`, `android.media.SoundPool`, `android.os.SystemClock`.

**Proyecto MyNotes:** `com.example.mynotes.R`.

**Kotlin/corrutinas/Java:** `java.util.EnumMap`.

## 3. Restricciones e invariantes visibles en el archivo

- **Nulabilidad segura (4 aparición/apariciones):** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`.
- **Fallback nulo (5 aparición/apariciones):** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula.
- **Límite numérico (5 aparición/apariciones):** Se fuerza el valor a un intervalo o umbral para impedir que salgan valores fuera de los límites esperados.
- **Filtro condicional (1 aparición/apariciones):** `takeIf` conserva el valor únicamente cuando el predicado se cumple; en caso contrario lo convierte en `null`.

## 4. Bloques de código, uno por uno

### 4.1 `UiSound` — class, líneas 17–19

```kotlin
enum class UiSound {
    Edit, Delete, Priority, SliderTick, Attachment, Toggle
}
```

#### Qué hace y por qué existe

Controlador de efectos de sonido de la interfaz. Gestiona temas/paquetes, volumen, carga/reutilización y reproducción asociada a acciones.

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

### 4.2 `UiActionSound` — class, líneas 26–29

```kotlin
enum class UiActionSound {
    Open, Back, Save, Search, TextInput, Menu, Select, Favorite, Pin, Share, Move, Color, Category, Add, Confirm, Cancel, Navigation, Sort,
    Layout, Language, Theme, Link, PlayPause, Zoom, Backup, Restore, Settings
}
```

#### Qué hace y por qué existe

Controlador de efectos de sonido de la interfaz. Gestiona temas/paquetes, volumen, carga/reutilización y reproducción asociada a acciones.

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

### 4.3 `UiSoundPlayer` — object, líneas 31–263

```kotlin
object UiSoundPlayer {
    // … el cuerpo completo permanece en el archivo real; sus miembros se documentan individualmente abajo …
}
```

#### Qué hace y por qué existe

Controlador de efectos de sonido de la interfaz. Gestiona temas/paquetes, volumen, carga/reutilización y reproducción asociada a acciones.

#### Contrato de la declaración


#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 32 | `val DEFAULT_THEME` | `inferido` | `"classic"` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Está marcada `const`, por lo que es una constante de tiempo de compilación y solo puede usar un tipo/valor admitido por Kotlin para constantes. |
| 33 | `val availableThemes` | `List<String>` | `listOf("classic", "soft", "digital", "glass", "retro", "pop", "mechanical", "bubble", "arcade",` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo declarado es `List<String>`. No declara nulabilidad explícita. |
| 36 | `var pool` | `SoundPool?` | `null` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo declarado es `SoundPool?`. Admite `null`, así que los consumidores deben contemplar ausencia de valor. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 37 | `val soundIds` | `inferido` | `mutableMapOf<String, EnumMap<UiSound, Int>>()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 38 | `val lastPlayAt` | `inferido` | `EnumMap<UiSound, Long>(UiSound::class.java)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 40 | `var enabled` | `Boolean` | `true` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo declarado es `Boolean`. No declara nulabilidad explícita. El nombre indica un estado de decisión/visibilidad/selección usado para controlar una rama de comportamiento o presentación. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 42 | `var volume` | `Float` | `0.65f` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo declarado es `Float`. No declara nulabilidad explícita. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 44 | `var theme` | `String` | `DEFAULT_THEME` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo declarado es `String`. No declara nulabilidad explícita. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 76 | `val spec` | `inferido` | `actionSpec(action)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 85 | `val spec` | `inferido` | `actionSpec(action)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 116 | `val now` | `inferido` | `SystemClock.uptimeMillis()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 117 | `val previous` | `inferido` | `synchronized(lastPlayAt) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 162 | `val now` | `inferido` | `SystemClock.uptimeMillis()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 163 | `val previous` | `inferido` | `synchronized(lastPlayAt) { lastPlayAt[sound] ?: 0L }` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 172 | `val soundPool` | `inferido` | `ensureInitialized(context)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 173 | `val soundId` | `inferido` | `soundIds[normalizeTheme(theme)]?.get(sound)?: soundIds[DEFAULT_THEME]?.get(sound)?: return` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 184 | `val audioAttributes` | `inferido` | `AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION).setContentType(` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 186 | `val newPool` | `inferido` | `SoundPool.Builder().setMaxStreams(6).setAudioAttributes(audioAttributes).build()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 187 | `val appContext` | `inferido` | `context.applicationContext` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El `Context` da acceso a recursos/servicios/almacenamiento Android; debe respetarse el ciclo de vida del contexto recibido. |
| 254 | `val ids` | `inferido` | `EnumMap<UiSound, Int>(UiSound::class.java)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 52 | `if (enabled) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 61 | `if (!enabled \|\| volume <= 0f) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 62 | `return` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 73 | `if (!enabled \|\| volume <= 0f) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 74 | `return` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 82 | `if (!enabled \|\| volume <= 0f) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 83 | `return` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 99 | `if ((!enabled && !force) \|\| volume <= 0f) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 100 | `return` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 113 | `if (!enabled \|\| volume <= 0f) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 114 | `return` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 120 | `if (now - previous < minimumIntervalMs) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 121 | `return` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 130 | `UiActionSound.Open -> ActionSpec(UiSound.Edit, 1.05f, 0.88f)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 131 | `UiActionSound.Back -> ActionSpec(UiSound.Toggle, 0.82f, 0.82f)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 132 | `UiActionSound.Save -> ActionSpec(UiSound.Edit, 1.18f, 1.00f)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 133 | `UiActionSound.Search -> ActionSpec(UiSound.SliderTick, 1.25f, 0.55f)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 134 | `UiActionSound.TextInput -> ActionSpec(UiSound.SliderTick, 1.36f, 0.42f)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 135 | `UiActionSound.Menu -> ActionSpec(UiSound.Toggle, 1.02f, 0.70f)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 136 | `UiActionSound.Select -> ActionSpec(UiSound.Toggle, 1.10f, 0.78f)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 137 | `UiActionSound.Favorite -> ActionSpec(UiSound.Priority, 1.28f, 0.92f)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 138 | `UiActionSound.Pin -> ActionSpec(UiSound.Toggle, 0.94f, 0.92f)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 139 | `UiActionSound.Share -> ActionSpec(UiSound.Edit, 1.32f, 0.88f)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 140 | `UiActionSound.Move -> ActionSpec(UiSound.Toggle, 0.92f, 0.82f)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 141 | `UiActionSound.Color -> ActionSpec(UiSound.Priority, 1.10f, 0.82f)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 142 | `UiActionSound.Category -> ActionSpec(UiSound.Toggle, 1.16f, 0.82f)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 143 | `UiActionSound.Add -> ActionSpec(UiSound.Attachment, 1.16f, 0.92f)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 144 | `UiActionSound.Confirm -> ActionSpec(UiSound.Edit, 1.22f, 0.95f)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 145 | `UiActionSound.Cancel -> ActionSpec(UiSound.Toggle, 0.80f, 0.78f)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 146 | `UiActionSound.Navigation -> ActionSpec(UiSound.Toggle, 1.05f, 0.72f)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 147 | `UiActionSound.Sort -> ActionSpec(UiSound.SliderTick, 1.10f, 0.68f)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 148 | `UiActionSound.Layout -> ActionSpec(UiSound.SliderTick, 0.95f, 0.72f)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 149 | `UiActionSound.Language -> ActionSpec(UiSound.Edit, 0.94f, 0.78f)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 150 | `UiActionSound.Theme -> ActionSpec(UiSound.Priority, 0.98f, 0.84f)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 151 | `UiActionSound.Link -> ActionSpec(UiSound.Edit, 1.12f, 0.84f)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 152 | `UiActionSound.PlayPause -> ActionSpec(UiSound.Toggle, 1.20f, 0.78f)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 153 | `UiActionSound.Zoom -> ActionSpec(UiSound.SliderTick, 1.08f, 0.58f)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 154 | `UiActionSound.Backup -> ActionSpec(UiSound.Attachment, 0.90f, 0.88f)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 155 | `UiActionSound.Restore -> ActionSpec(UiSound.Attachment, 1.05f, 0.88f)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 156 | `UiActionSound.Settings -> ActionSpec(UiSound.Edit, 0.90f, 0.76f)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 159 | `if (!enabled \|\| volume <= 0f) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 160 | `return` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 164 | `if (now - previous < minimumIntervalMs) return` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 169 | `if (volume <= 0f) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 170 | `return` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 178 | `return it` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 182 | `return it` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 249 | `return newPool` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 4.
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 5.
- **Límite numérico:** Se fuerza el valor a un intervalo o umbral para impedir que salgan valores fuera de los límites esperados. Apariciones en este bloque: 5.
- **Filtro condicional:** `takeIf` conserva el valor únicamente cuando el predicado se cumple; en caso contrario lo convierte en `null`. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

- **Preferencias/DataStore:** Lee o escribe configuración persistente; el resultado sobrevive a recreaciones de la pantalla y normalmente al reinicio de la app.
- **Sonido/háptica:** Produce feedback perceptible para el usuario; debe ejecutarse en respuesta a la interacción/estado correspondiente.
- **Mutación de colección/estado compartido:** Modifica una colección/mapa/estructura existente; el cambio permanece visible para quienes compartan esa misma instancia.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `listOf`, `value.trim`, `lowercase`, `coerceIn`, `normalizeTheme`, `UiHapticPlayer.configure`, `ensureInitialized`, `UiHapticPlayer.playForSound`, `playInternal`, `UiHapticPlayer.playForAction`, `actionSpec`, `playThrottledInternal`, `UiHapticPlayer.playToggle`, `SystemClock.uptimeMillis`, `synchronized`, `ActionSpec`, `get`, `soundPool.play`, `rate.coerceIn`, `AudioAttributes.Builder`, `setUsage`, `setContentType`, `build`, `SoundPool.Builder`, `setMaxStreams`, `setAudioAttributes`, `loadTheme`, `pool.load`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.

### 4.4 `normalizeTheme` — fun, líneas 45–55

```kotlin
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
| 52 | `if (enabled) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |

**Restricciones concretas que aparecen en este bloque:**
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 1.
- **Límite numérico:** Se fuerza el valor a un intervalo o umbral para impedir que salgan valores fuera de los límites esperados. Apariciones en este bloque: 1.
- **Filtro condicional:** `takeIf` conserva el valor únicamente cuando el predicado se cumple; en caso contrario lo convierte en `null`. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

- **Sonido/háptica:** Produce feedback perceptible para el usuario; debe ejecutarse en respuesta a la interacción/estado correspondiente.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `value.trim`, `lowercase`, `coerceIn`, `normalizeTheme`, `UiHapticPlayer.configure`, `ensureInitialized`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

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

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `context: Context` — `context` recibe un valor de tipo `Context`. El contrato no marca este parámetro como anulable. Da acceso a recursos y APIs Android; su vida útil debe corresponder con el uso realizado.
- `enabled: Boolean` — `enabled` recibe un valor de tipo `Boolean`. El contrato no marca este parámetro como anulable.
- `volumePercent: Float` — `volumePercent` recibe un valor de tipo `Float`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.
- `theme: String = DEFAULT_THEME` — `theme` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable. Tiene valor por defecto `DEFAULT_THEME`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada.
- `hapticEnabled: Boolean = true` — `hapticEnabled` recibe un valor de tipo `Boolean`. El contrato no marca este parámetro como anulable. Tiene valor por defecto `true`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada.
- `hapticIntensityPercent: Float = 55f` — `hapticIntensityPercent` recibe un valor de tipo `Float`. El contrato no marca este parámetro como anulable. Tiene valor por defecto `55f`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.
- `hapticStyle: String = UiHapticPlayer.DEFAULT_STYLE` — `hapticStyle` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable. Tiene valor por defecto `UiHapticPlayer.DEFAULT_STYLE`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.


#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 52 | `if (enabled) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |

**Restricciones concretas que aparecen en este bloque:**
- **Límite numérico:** Se fuerza el valor a un intervalo o umbral para impedir que salgan valores fuera de los límites esperados. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

- **Sonido/háptica:** Produce feedback perceptible para el usuario; debe ejecutarse en respuesta a la interacción/estado correspondiente.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `coerceIn`, `normalizeTheme`, `UiHapticPlayer.configure`, `ensureInitialized`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.6 `preload` — fun, líneas 56–58

```kotlin
    fun preload(context: Context) {
        ensureInitialized(context)
    }
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `context: Context` — `context` recibe un valor de tipo `Context`. El contrato no marca este parámetro como anulable. Da acceso a recursos y APIs Android; su vida útil debe corresponder con el uso realizado.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.


#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `ensureInitialized`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

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

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `context: Context` — `context` recibe un valor de tipo `Context`. El contrato no marca este parámetro como anulable. Da acceso a recursos y APIs Android; su vida útil debe corresponder con el uso realizado.
- `sound: UiSound` — `sound` recibe un valor de tipo `UiSound`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.


#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 61 | `if (!enabled \|\| volume <= 0f) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 62 | `return` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

#### Efectos secundarios y recursos

- **Sonido/háptica:** Produce feedback perceptible para el usuario; debe ejecutarse en respuesta a la interacción/estado correspondiente.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `UiHapticPlayer.playForSound`, `playInternal`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

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

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `context: Context` — `context` recibe un valor de tipo `Context`. El contrato no marca este parámetro como anulable. Da acceso a recursos y APIs Android; su vida útil debe corresponder con el uso realizado.
- `action: UiActionSound` — `action` recibe un valor de tipo `UiActionSound`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.


#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 76 | `val spec` | `inferido` | `actionSpec(action)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 73 | `if (!enabled \|\| volume <= 0f) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 74 | `return` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

**Restricciones concretas que aparecen en este bloque:**
- **Límite numérico:** Se fuerza el valor a un intervalo o umbral para impedir que salgan valores fuera de los límites esperados. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

- **Sonido/háptica:** Produce feedback perceptible para el usuario; debe ejecutarse en respuesta a la interacción/estado correspondiente.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `UiHapticPlayer.playForAction`, `actionSpec`, `playInternal`, `coerceIn`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.9 `playActionThrottled` — fun, líneas 80–96

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
    /**
     * Sonido específico para interruptores. El encendido se reproduce un poco
     * más agudo y el apagado un poco más grave para que el cambio se distinga
     * sin necesitar dos archivos por paquete.
     *
     * force=true se usa únicamente en el interruptor maestro de sonidos: así
     * también se oye al activar los efectos cuando todavía estaban deshabilitados.
     */
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `context: Context` — `context` recibe un valor de tipo `Context`. El contrato no marca este parámetro como anulable. Da acceso a recursos y APIs Android; su vida útil debe corresponder con el uso realizado.
- `action: UiActionSound` — `action` recibe un valor de tipo `UiActionSound`. El contrato no marca este parámetro como anulable.
- `minimumIntervalMs: Long = 45L` — `minimumIntervalMs` recibe un valor de tipo `Long`. El contrato no marca este parámetro como anulable. Tiene valor por defecto `45L`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.


#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 85 | `val spec` | `inferido` | `actionSpec(action)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 82 | `if (!enabled \|\| volume <= 0f) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 83 | `return` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

#### Efectos secundarios y recursos

- **Sonido/háptica:** Produce feedback perceptible para el usuario; debe ejecutarse en respuesta a la interacción/estado correspondiente.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `UiHapticPlayer.playForAction`, `actionSpec`, `playThrottledInternal`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.10 `playToggle` — fun, líneas 97–107

```kotlin
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

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 99 | `if ((!enabled && !force) \|\| volume <= 0f) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 100 | `return` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

#### Efectos secundarios y recursos

- **Preferencias/DataStore:** Lee o escribe configuración persistente; el resultado sobrevive a recreaciones de la pantalla y normalmente al reinicio de la app.
- **Sonido/háptica:** Produce feedback perceptible para el usuario; debe ejecutarse en respuesta a la interacción/estado correspondiente.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `UiHapticPlayer.playToggle`, `playInternal`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.11 `previewTheme` — fun, líneas 108–110

```kotlin
    fun previewTheme(context: Context, theme: String, sound: UiSound = UiSound.Edit, volumePercent: Float = 65f) {
        playInternal(context = context, sound = sound, theme = normalizeTheme(theme), volume = (volumePercent / 100f).coerceIn(0f, 1f))
    }
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `context: Context` — `context` recibe un valor de tipo `Context`. El contrato no marca este parámetro como anulable. Da acceso a recursos y APIs Android; su vida útil debe corresponder con el uso realizado.
- `theme: String` — `theme` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.
- `sound: UiSound = UiSound.Edit` — `sound` recibe un valor de tipo `UiSound`. El contrato no marca este parámetro como anulable. Tiene valor por defecto `UiSound.Edit`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada.
- `volumePercent: Float = 65f` — `volumePercent` recibe un valor de tipo `Float`. El contrato no marca este parámetro como anulable. Tiene valor por defecto `65f`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.

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

Entre las llamadas presentes están: `playInternal`, `normalizeTheme`, `coerceIn`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

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

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `context: Context` — `context` recibe un valor de tipo `Context`. El contrato no marca este parámetro como anulable. Da acceso a recursos y APIs Android; su vida útil debe corresponder con el uso realizado.
- `sound: UiSound` — `sound` recibe un valor de tipo `UiSound`. El contrato no marca este parámetro como anulable.
- `minimumIntervalMs: Long = 45L` — `minimumIntervalMs` recibe un valor de tipo `Long`. El contrato no marca este parámetro como anulable. Tiene valor por defecto `45L`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.


#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 116 | `val now` | `inferido` | `SystemClock.uptimeMillis()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 117 | `val previous` | `inferido` | `synchronized(lastPlayAt) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 113 | `if (!enabled \|\| volume <= 0f) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 114 | `return` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 120 | `if (now - previous < minimumIntervalMs) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 121 | `return` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

**Restricciones concretas que aparecen en este bloque:**
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

- **Sonido/háptica:** Produce feedback perceptible para el usuario; debe ejecutarse en respuesta a la interacción/estado correspondiente.
- **Mutación de colección/estado compartido:** Modifica una colección/mapa/estructura existente; el cambio permanece visible para quienes compartan esa misma instancia.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `UiHapticPlayer.playForSound`, `SystemClock.uptimeMillis`, `synchronized`, `playInternal`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.13 `actionSpec` — fun, líneas 128–157

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

#### Qué hace y por qué existe

Controlador de efectos de sonido de la interfaz. Gestiona temas/paquetes, volumen, carga/reutilización y reproducción asociada a acciones.

#### Contrato de la declaración

**Parámetros:**

- `val sound: UiSound` — No se pudo separar automáticamente nombre/tipo; se conserva la firma exacta como contrato.
- `val rate: Float = 1f` — No se pudo separar automáticamente nombre/tipo; se conserva la firma exacta como contrato.
- `val volumeScale: Float = 1f` — No se pudo separar automáticamente nombre/tipo; se conserva la firma exacta como contrato.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 130 | `UiActionSound.Open -> ActionSpec(UiSound.Edit, 1.05f, 0.88f)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 131 | `UiActionSound.Back -> ActionSpec(UiSound.Toggle, 0.82f, 0.82f)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 132 | `UiActionSound.Save -> ActionSpec(UiSound.Edit, 1.18f, 1.00f)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 133 | `UiActionSound.Search -> ActionSpec(UiSound.SliderTick, 1.25f, 0.55f)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 134 | `UiActionSound.TextInput -> ActionSpec(UiSound.SliderTick, 1.36f, 0.42f)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 135 | `UiActionSound.Menu -> ActionSpec(UiSound.Toggle, 1.02f, 0.70f)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 136 | `UiActionSound.Select -> ActionSpec(UiSound.Toggle, 1.10f, 0.78f)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 137 | `UiActionSound.Favorite -> ActionSpec(UiSound.Priority, 1.28f, 0.92f)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 138 | `UiActionSound.Pin -> ActionSpec(UiSound.Toggle, 0.94f, 0.92f)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 139 | `UiActionSound.Share -> ActionSpec(UiSound.Edit, 1.32f, 0.88f)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 140 | `UiActionSound.Move -> ActionSpec(UiSound.Toggle, 0.92f, 0.82f)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 141 | `UiActionSound.Color -> ActionSpec(UiSound.Priority, 1.10f, 0.82f)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 142 | `UiActionSound.Category -> ActionSpec(UiSound.Toggle, 1.16f, 0.82f)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 143 | `UiActionSound.Add -> ActionSpec(UiSound.Attachment, 1.16f, 0.92f)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 144 | `UiActionSound.Confirm -> ActionSpec(UiSound.Edit, 1.22f, 0.95f)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 145 | `UiActionSound.Cancel -> ActionSpec(UiSound.Toggle, 0.80f, 0.78f)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 146 | `UiActionSound.Navigation -> ActionSpec(UiSound.Toggle, 1.05f, 0.72f)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 147 | `UiActionSound.Sort -> ActionSpec(UiSound.SliderTick, 1.10f, 0.68f)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 148 | `UiActionSound.Layout -> ActionSpec(UiSound.SliderTick, 0.95f, 0.72f)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 149 | `UiActionSound.Language -> ActionSpec(UiSound.Edit, 0.94f, 0.78f)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 150 | `UiActionSound.Theme -> ActionSpec(UiSound.Priority, 0.98f, 0.84f)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 151 | `UiActionSound.Link -> ActionSpec(UiSound.Edit, 1.12f, 0.84f)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 152 | `UiActionSound.PlayPause -> ActionSpec(UiSound.Toggle, 1.20f, 0.78f)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 153 | `UiActionSound.Zoom -> ActionSpec(UiSound.SliderTick, 1.08f, 0.58f)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 154 | `UiActionSound.Backup -> ActionSpec(UiSound.Attachment, 0.90f, 0.88f)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 155 | `UiActionSound.Restore -> ActionSpec(UiSound.Attachment, 1.05f, 0.88f)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 156 | `UiActionSound.Settings -> ActionSpec(UiSound.Edit, 0.90f, 0.76f)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `ActionSpec`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.

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

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `action: UiActionSound` — `action` recibe un valor de tipo `UiActionSound`. El contrato no marca este parámetro como anulable.

**Retorno:** `ActionSpec`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 130 | `UiActionSound.Open -> ActionSpec(UiSound.Edit, 1.05f, 0.88f)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 131 | `UiActionSound.Back -> ActionSpec(UiSound.Toggle, 0.82f, 0.82f)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 132 | `UiActionSound.Save -> ActionSpec(UiSound.Edit, 1.18f, 1.00f)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 133 | `UiActionSound.Search -> ActionSpec(UiSound.SliderTick, 1.25f, 0.55f)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 134 | `UiActionSound.TextInput -> ActionSpec(UiSound.SliderTick, 1.36f, 0.42f)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 135 | `UiActionSound.Menu -> ActionSpec(UiSound.Toggle, 1.02f, 0.70f)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 136 | `UiActionSound.Select -> ActionSpec(UiSound.Toggle, 1.10f, 0.78f)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 137 | `UiActionSound.Favorite -> ActionSpec(UiSound.Priority, 1.28f, 0.92f)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 138 | `UiActionSound.Pin -> ActionSpec(UiSound.Toggle, 0.94f, 0.92f)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 139 | `UiActionSound.Share -> ActionSpec(UiSound.Edit, 1.32f, 0.88f)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 140 | `UiActionSound.Move -> ActionSpec(UiSound.Toggle, 0.92f, 0.82f)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 141 | `UiActionSound.Color -> ActionSpec(UiSound.Priority, 1.10f, 0.82f)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 142 | `UiActionSound.Category -> ActionSpec(UiSound.Toggle, 1.16f, 0.82f)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 143 | `UiActionSound.Add -> ActionSpec(UiSound.Attachment, 1.16f, 0.92f)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 144 | `UiActionSound.Confirm -> ActionSpec(UiSound.Edit, 1.22f, 0.95f)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 145 | `UiActionSound.Cancel -> ActionSpec(UiSound.Toggle, 0.80f, 0.78f)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 146 | `UiActionSound.Navigation -> ActionSpec(UiSound.Toggle, 1.05f, 0.72f)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 147 | `UiActionSound.Sort -> ActionSpec(UiSound.SliderTick, 1.10f, 0.68f)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 148 | `UiActionSound.Layout -> ActionSpec(UiSound.SliderTick, 0.95f, 0.72f)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 149 | `UiActionSound.Language -> ActionSpec(UiSound.Edit, 0.94f, 0.78f)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 150 | `UiActionSound.Theme -> ActionSpec(UiSound.Priority, 0.98f, 0.84f)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 151 | `UiActionSound.Link -> ActionSpec(UiSound.Edit, 1.12f, 0.84f)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 152 | `UiActionSound.PlayPause -> ActionSpec(UiSound.Toggle, 1.20f, 0.78f)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 153 | `UiActionSound.Zoom -> ActionSpec(UiSound.SliderTick, 1.08f, 0.58f)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 154 | `UiActionSound.Backup -> ActionSpec(UiSound.Attachment, 0.90f, 0.88f)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 155 | `UiActionSound.Restore -> ActionSpec(UiSound.Attachment, 1.05f, 0.88f)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 156 | `UiActionSound.Settings -> ActionSpec(UiSound.Edit, 0.90f, 0.76f)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `ActionSpec`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.

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

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `context: Context` — `context` recibe un valor de tipo `Context`. El contrato no marca este parámetro como anulable. Da acceso a recursos y APIs Android; su vida útil debe corresponder con el uso realizado.
- `sound: UiSound` — `sound` recibe un valor de tipo `UiSound`. El contrato no marca este parámetro como anulable.
- `minimumIntervalMs: Long` — `minimumIntervalMs` recibe un valor de tipo `Long`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.
- `rate: Float` — `rate` recibe un valor de tipo `Float`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.
- `volumeScale: Float` — `volumeScale` recibe un valor de tipo `Float`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 162 | `val now` | `inferido` | `SystemClock.uptimeMillis()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 163 | `val previous` | `inferido` | `synchronized(lastPlayAt) { lastPlayAt[sound] ?: 0L }` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 159 | `if (!enabled \|\| volume <= 0f) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 160 | `return` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 164 | `if (now - previous < minimumIntervalMs) return` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |

**Restricciones concretas que aparecen en este bloque:**
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 1.
- **Límite numérico:** Se fuerza el valor a un intervalo o umbral para impedir que salgan valores fuera de los límites esperados. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

- **Mutación de colección/estado compartido:** Modifica una colección/mapa/estructura existente; el cambio permanece visible para quienes compartan esa misma instancia.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `SystemClock.uptimeMillis`, `synchronized`, `playInternal`, `coerceIn`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

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

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `context: Context` — `context` recibe un valor de tipo `Context`. El contrato no marca este parámetro como anulable. Da acceso a recursos y APIs Android; su vida útil debe corresponder con el uso realizado.
- `sound: UiSound` — `sound` recibe un valor de tipo `UiSound`. El contrato no marca este parámetro como anulable.
- `theme: String` — `theme` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.
- `volume: Float` — `volume` recibe un valor de tipo `Float`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.
- `rate: Float = 1f` — `rate` recibe un valor de tipo `Float`. El contrato no marca este parámetro como anulable. Tiene valor por defecto `1f`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 172 | `val soundPool` | `inferido` | `ensureInitialized(context)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 173 | `val soundId` | `inferido` | `soundIds[normalizeTheme(theme)]?.get(sound)?: soundIds[DEFAULT_THEME]?.get(sound)?: return` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 169 | `if (volume <= 0f) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 170 | `return` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 2.
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 2.
- **Límite numérico:** Se fuerza el valor a un intervalo o umbral para impedir que salgan valores fuera de los límites esperados. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `ensureInitialized`, `normalizeTheme`, `get`, `soundPool.play`, `rate.coerceIn`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.17 `ensureInitialized` — fun, líneas 176–251

```kotlin
    private fun ensureInitialized(context: Context): SoundPool {
        pool?.let {
            return it
        }
        synchronized(this) {
            pool?.let {
                return it
            }
            val audioAttributes = AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION).setContentType(
                        AudioAttributes.CONTENT_TYPE_SONIFICATION).build()
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

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `context: Context` — `context` recibe un valor de tipo `Context`. El contrato no marca este parámetro como anulable. Da acceso a recursos y APIs Android; su vida útil debe corresponder con el uso realizado.

**Retorno:** `SoundPool`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 184 | `val audioAttributes` | `inferido` | `AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION).setContentType(` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 186 | `val newPool` | `inferido` | `SoundPool.Builder().setMaxStreams(6).setAudioAttributes(audioAttributes).build()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 187 | `val appContext` | `inferido` | `context.applicationContext` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El `Context` da acceso a recursos/servicios/almacenamiento Android; debe respetarse el ciclo de vida del contexto recibido. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 178 | `return it` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 182 | `return it` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 249 | `return newPool` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 2.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `synchronized`, `AudioAttributes.Builder`, `setUsage`, `setContentType`, `build`, `SoundPool.Builder`, `setMaxStreams`, `setAudioAttributes`, `loadTheme`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.18 `loadTheme` — fun, líneas 252–262

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

#### Qué hace y por qué existe

Carga o reconstruye un recurso/dato a partir de sus entradas, aplicando las capas de caché/normalización definidas en el cuerpo.

#### Contrato de la declaración

**Parámetros:**

- `pool: SoundPool` — `pool` recibe un valor de tipo `SoundPool`. El contrato no marca este parámetro como anulable.
- `context: Context` — `context` recibe un valor de tipo `Context`. El contrato no marca este parámetro como anulable. Da acceso a recursos y APIs Android; su vida útil debe corresponder con el uso realizado.
- `theme: String` — `theme` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.
- `edit: Int` — `edit` recibe un valor de tipo `Int`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.
- `delete: Int` — `delete` recibe un valor de tipo `Int`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.
- `priority: Int` — `priority` recibe un valor de tipo `Int`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.
- `sliderTick: Int` — `sliderTick` recibe un valor de tipo `Int`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.
- `attachment: Int` — `attachment` recibe un valor de tipo `Int`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.
- `toggle: Int` — `toggle` recibe un valor de tipo `Int`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 254 | `val ids` | `inferido` | `EnumMap<UiSound, Int>(UiSound::class.java)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

- **Mutación de colección/estado compartido:** Modifica una colección/mapa/estructura existente; el cambio permanece visible para quienes compartan esa misma instancia.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `pool.load`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

## 5. Inventario global de propiedades/variables detectadas

| Línea | Identificador | Mutabilidad | Tipo | Explicación |
|---:|---|---|---|---|
| 32 | `DEFAULT_THEME` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Está marcada `const`, por lo que es una constante de tiempo de compilación y solo puede usar un tipo/valor admitido por Kotlin para constantes. |
| 33 | `availableThemes` | `val` | `List<String>` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo declarado es `List<String>`. No declara nulabilidad explícita. |
| 36 | `pool` | `var` | `SoundPool?` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo declarado es `SoundPool?`. Admite `null`, así que los consumidores deben contemplar ausencia de valor. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 37 | `soundIds` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 38 | `lastPlayAt` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 40 | `enabled` | `var` | `Boolean` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo declarado es `Boolean`. No declara nulabilidad explícita. El nombre indica un estado de decisión/visibilidad/selección usado para controlar una rama de comportamiento o presentación. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 42 | `volume` | `var` | `Float` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo declarado es `Float`. No declara nulabilidad explícita. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 44 | `theme` | `var` | `String` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo declarado es `String`. No declara nulabilidad explícita. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 76 | `spec` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 85 | `spec` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 116 | `now` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 117 | `previous` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 162 | `now` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 163 | `previous` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 172 | `soundPool` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 173 | `soundId` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 184 | `audioAttributes` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 186 | `newPool` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 187 | `appContext` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El `Context` da acceso a recursos/servicios/almacenamiento Android; debe respetarse el ciclo de vida del contexto recibido. |
| 254 | `ids` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

## 6. Mapa exhaustivo de TODOS los bloques delimitados por `{}`

Esta tabla recorre todas las llaves estructurales detectadas fuera de strings/comentarios. Así se documentan también lambdas de Compose, callbacks, iteraciones y ámbitos locales que no tienen un nombre propio de función.

| Inicio–fin | Prof. | Cabecera/contexto | Qué significa ese bloque |
|---|---:|---|---|
| 17–19 | 0 | `enum class UiSound` | Cuerpo de tipo/contenedor: agrupa propiedades y miembros bajo la misma responsabilidad y controla su visibilidad/estado compartido. |
| 26–29 | 0 | `enum class UiActionSound` | Cuerpo de tipo/contenedor: agrupa propiedades y miembros bajo la misma responsabilidad y controla su visibilidad/estado compartido. |
| 31–263 | 0 | `object UiSoundPlayer` | Cuerpo de tipo/contenedor: agrupa propiedades y miembros bajo la misma responsabilidad y controla su visibilidad/estado compartido. |
| 45–45 | 1 | `fun normalizeTheme(value: String): String = value.trim().lowercase().takeIf` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 47–55 | 1 | `hapticIntensityPercent: Float = 55f, hapticStyle: String = UiHapticPlayer.DEFAULT_STYLE)` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 52–54 | 2 | `if (enabled)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 56–58 | 1 | `fun preload(context: Context)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 59–65 | 1 | `fun play(context: Context, sound: UiSound)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 61–63 | 2 | `if (!enabled \|\| volume <= 0f)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 71–79 | 1 | `fun playAction(context: Context, action: UiActionSound)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 73–75 | 2 | `if (!enabled \|\| volume <= 0f)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 80–88 | 1 | `fun playActionThrottled(context: Context, action: UiActionSound, minimumIntervalMs: Long = 45L)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 82–84 | 2 | `if (!enabled \|\| volume <= 0f)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 97–103 | 1 | `fun playToggle(context: Context, checked: Boolean, force: Boolean = false)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 99–101 | 2 | `if ((!enabled && !force) \|\| volume <= 0f)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 108–110 | 1 | `fun previewTheme(context: Context, theme: String, sound: UiSound = UiSound.Edit, volumePercent: Float = 65f)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 111–127 | 1 | `fun playThrottled(context: Context, sound: UiSound, minimumIntervalMs: Long = 45L)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 113–115 | 2 | `if (!enabled \|\| volume <= 0f)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 117–119 | 2 | `val previous = synchronized(lastPlayAt)` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 120–122 | 2 | `if (now - previous < minimumIntervalMs)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 123–125 | 2 | `synchronized(lastPlayAt)` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 129–157 | 1 | `private fun actionSpec(action: UiActionSound): ActionSpec = when (action)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 158–167 | 1 | `private fun playThrottledInternal(context: Context, sound: UiSound, minimumIntervalMs: Long, rate: Float, volumeScale: Float)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 159–161 | 2 | `if (!enabled \|\| volume <= 0f)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 163–163 | 2 | `val previous = synchronized(lastPlayAt)` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 165–165 | 2 | `synchronized(lastPlayAt)` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 168–175 | 1 | `private fun playInternal(context: Context, sound: UiSound, theme: String, volume: Float, rate: Float = 1f)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 169–171 | 2 | `if (volume <= 0f)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 176–251 | 1 | `private fun ensureInitialized(context: Context): SoundPool` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 177–179 | 2 | `pool?.let` | Lambda `let`: transforma/usa el receptor dentro de un ámbito corto; con llamada segura suele ejecutarse solo cuando el receptor no es nulo. |
| 180–250 | 2 | `synchronized(this)` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 181–183 | 3 | `pool?.let` | Lambda `let`: transforma/usa el receptor dentro de un ámbito corto; con llamada segura suele ejecutarse solo cuando el receptor no es nulo. |
| 253–262 | 1 | `attachment: Int, toggle: Int)` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |

## 7. Lectura de seguridad antes de tocar este archivo

La documentación describe **lo que el código actual ya hace**. No constituye una propuesta de refactorización. Si se quisiera cambiar algo en el futuro, primero habría que preservar contratos públicos, claves de configuración, restricciones de API Android, comportamiento de Compose, lifecycle, operaciones de I/O y compatibilidad con los datos ya persistidos. En esta entrega no se hizo ninguna de esas modificaciones.

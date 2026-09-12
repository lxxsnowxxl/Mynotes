# Theme.kt — documentación exhaustiva por bloques

**Ruta de código real:** `app/src/main/java/com/example/mynotes/ui/theme/Theme.kt`  
**SHA-256 del archivo ejecutable sin tocar:** `0c133f3e450d754536248300863f7ac87e323257e287971f8322635cdcc5da0d`  
**Líneas del código real:** 267

> **Garantía:** esta documentación se genera en una carpeta paralela. El archivo Kotlin/Kotlin DSL anterior no se modifica. Los fragmentos mostrados abajo son copias de lectura; la aplicación compila usando únicamente el archivo original de `app/...`.

## 1. Papel del archivo

Construye el MaterialTheme global de MyNotes a partir de AppSettings: paleta, tono, modo claro/oscuro, contraste y tipografía.

**Arquitectura.** Es el punto donde las preferencias persistidas se convierten en ColorScheme/estilo efectivo que heredan las pantallas Compose.

**Flujo general.** El flujo se describe declaración por declaración en este documento.

## 2. Package e imports

El `package` es `com.example.mynotes.ui.theme`. Determina el namespace lógico del archivo, resolución de visibilidad `internal`/package-related tooling y la ruta conceptual desde la que otras clases lo importan.

El archivo declara **14 imports**. Cada import evita usar el nombre totalmente calificado en el cuerpo y, además, revela las dependencias técnicas del bloque:

**Jetpack/Compose:** `androidx.compose.foundation.isSystemInDarkTheme`, `androidx.compose.material3.ColorScheme`, `androidx.compose.material3.LocalTextStyle`, `androidx.compose.material3.MaterialTheme`, `androidx.compose.material3.Typography as MaterialTypography`, `androidx.compose.material3.darkColorScheme`, `androidx.compose.material3.lightColorScheme`, `androidx.compose.runtime.Composable`, `androidx.compose.runtime.CompositionLocalProvider`, `androidx.compose.runtime.remember`, `androidx.compose.ui.geometry.Offset`, `androidx.compose.ui.graphics.Color`, `androidx.compose.ui.graphics.Shadow`, `androidx.compose.ui.text.TextStyle`.

## 3. Restricciones e invariantes visibles en el archivo

- **Límite numérico (17 aparición/apariciones):** Se fuerza el valor a un intervalo o umbral para impedir que salgan valores fuera de los límites esperados.

## 4. Bloques de código, uno por uno

### 4.1 `mixColor` — fun, líneas 20–26

```kotlin
private fun mixColor(first: Color, second: Color, amount: Float): Color {
    val value = amount.coerceIn(0f, 1f)
    return Color(red = first.red + (second.red - first.red) * value,
        green = first.green + (second.green - first.green) * value,
        blue = first.blue + (second.blue - first.blue) * value,
        alpha = 1f)
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `first: Color` — `first` recibe un valor de tipo `Color`. El contrato no marca este parámetro como anulable.
- `second: Color` — `second` recibe un valor de tipo `Color`. El contrato no marca este parámetro como anulable.
- `amount: Float` — `amount` recibe un valor de tipo `Float`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.

**Retorno:** `Color`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 21 | `val value` | `inferido` | `amount.coerceIn(0f, 1f)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El inicializador limita explícitamente el valor a un rango, evitando que dimensiones/intensidades/sizes inválidos se propaguen. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 22 | `return Color(red = first.red + (second.red - first.red) * value,` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

**Restricciones concretas que aparecen en este bloque:**
- **Límite numérico:** Se fuerza el valor a un intervalo o umbral para impedir que salgan valores fuera de los límites esperados. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `amount.coerceIn`, `Color`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.2 `intensityAmount` — fun, líneas 28–30

```kotlin
private fun intensityAmount(intensity: Float): Float {
    return (intensity.coerceIn(0f, 100f) / 100f) * MAX_DARKEN_AMOUNT
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `intensity: Float` — `intensity` recibe un valor de tipo `Float`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.

**Retorno:** `Float`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 29 | `return (intensity.coerceIn(0f, 100f) / 100f) * MAX_DARKEN_AMOUNT` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

**Restricciones concretas que aparecen en este bloque:**
- **Límite numérico:** Se fuerza el valor a un intervalo o umbral para impedir que salgan valores fuera de los límites esperados. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `intensity.coerceIn`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.3 `applySectionIntensity` — fun, líneas 32–34

```kotlin
fun applySectionIntensity(color: Color, intensity: Float): Color {
    return mixColor(color, Color.Black, intensityAmount(intensity))
}
```

#### Qué hace y por qué existe

Aplica una política/configuración calculada sobre el objeto o sistema destino.

#### Contrato de la declaración

**Parámetros:**

- `color: Color` — `color` recibe un valor de tipo `Color`. El contrato no marca este parámetro como anulable.
- `intensity: Float` — `intensity` recibe un valor de tipo `Float`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.

**Retorno:** `Color`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.


#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 33 | `return mixColor(color, Color.Black, intensityAmount(intensity))` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `mixColor`, `intensityAmount`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.4 `readableContentColor` — fun, líneas 36–37

```kotlin
private fun readableContentColor(background: Color): Color = automaticUiTextColor(background)
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `background: Color` — `background` recibe un valor de tipo `Color`. El contrato no marca este parámetro como anulable.

**Retorno:** `Color`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `automaticUiTextColor`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.5 `resolvedTextColor` — fun, líneas 38–44

```kotlin
private fun resolvedTextColor(textColor: String, background: Color): Color = resolveUiTextColor(value = textColor, background = background)

/**
 * Regla de color solicitada para el modo Automático de la paleta global:
 * tonos 1-2 -> negro, tonos 3-4 -> blanco.
 * Negro/Blanco manuales siguen teniendo prioridad absoluta.
 */
```

#### Qué hace y por qué existe

Resuelve un valor final a partir del contexto y las reglas de prioridad/fallback definidas en el cuerpo.

#### Contrato de la declaración

**Parámetros:**

- `textColor: String` — `textColor` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.
- `background: Color` — `background` recibe un valor de tipo `Color`. El contrato no marca este parámetro como anulable.

**Retorno:** `Color`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `resolveUiTextColor`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.6 `resolvedPaletteTextColor` — fun, líneas 45–49

```kotlin
private fun resolvedPaletteTextColor(textColor: String, toneIndex: Int, background: Color): Color = when (textColor) {
        "black" -> Color.Black
        "white" -> Color.White
        else -> if (toneIndex.coerceIn(0, 3) <= 1) Color.Black else Color.White
    }
```

#### Qué hace y por qué existe

Resuelve un valor final a partir del contexto y las reglas de prioridad/fallback definidas en el cuerpo.

#### Contrato de la declaración

**Parámetros:**

- `textColor: String` — `textColor` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.
- `toneIndex: Int` — `toneIndex` recibe un valor de tipo `Int`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.
- `background: Color` — `background` recibe un valor de tipo `Color`. El contrato no marca este parámetro como anulable.

**Retorno:** `Color`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 46 | `"black" -> Color.Black` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 47 | `"white" -> Color.White` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 48 | `else -> if (toneIndex.coerceIn(0, 3) <= 1) Color.Black else Color.White` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |

**Restricciones concretas que aparecen en este bloque:**
- **Límite numérico:** Se fuerza el valor a un intervalo o umbral para impedir que salgan valores fuera de los límites esperados. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `toneIndex.coerceIn`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.

### 4.7 `resolvedPaletteSecondaryTextColor` — fun, líneas 50–57

```kotlin
private fun resolvedPaletteSecondaryTextColor(textColor: String, toneIndex: Int, background: Color): Color {
    val primary = resolvedPaletteTextColor(textColor, toneIndex, background)
    return if (textColor == "black" || textColor == "white") {
        primary
    } else {
        softenUiColorToContrast(foreground = primary, background = background, minimumContrast = 4.5f, maximumSoftening = 0.42f)
    }
}
```

#### Qué hace y por qué existe

Resuelve un valor final a partir del contexto y las reglas de prioridad/fallback definidas en el cuerpo.

#### Contrato de la declaración

**Parámetros:**

- `textColor: String` — `textColor` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.
- `toneIndex: Int` — `toneIndex` recibe un valor de tipo `Int`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.
- `background: Color` — `background` recibe un valor de tipo `Color`. El contrato no marca este parámetro como anulable.

**Retorno:** `Color`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 51 | `val primary` | `inferido` | `resolvedPaletteTextColor(textColor, toneIndex, background)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 52 | `return if (textColor == "black" \|\| textColor == "white") {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `resolvedPaletteTextColor`, `softenUiColorToContrast`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.8 `resolveAccentColor` — fun, líneas 59–82

```kotlin
private fun resolveAccentColor(value: String, palette: MyNotesPalette): Color {
    return when (value) {
        "red" -> Color(0xFFE55757)
        "coral" -> Color(0xFFF27663)
        "orange" -> Color(0xFFEF8A39)
        "amber" -> Color(0xFFE8B632)
        "yellow" -> Color(0xFFF2CF45)
        "lime" -> Color(0xFF9BCB4B)
        "green" -> Color(0xFF63A85C)
        "mint" -> Color(0xFF58B98E)
        "teal" -> Color(0xFF3FA4A0)
        "cyan" -> Color(0xFF45B9C8)
        "sky" -> Color(0xFF4FA9E2)
        "blue" -> Color(0xFF4B8EDB)
        "indigo" -> Color(0xFF6275CF)
        "violet" -> Color(0xFF8B6BC5)
        "purple" -> Color(0xFFA05BC1)
        "pink" -> Color(0xFFD96787)
        "rose" -> Color(0xFFE16F9A)
        "brown" -> Color(0xFF9A7157)
        "graphite" -> Color(0xFF59636A)
        else -> palette.accent
    }
}
```

#### Qué hace y por qué existe

Resuelve un valor final a partir del contexto y las reglas de prioridad/fallback definidas en el cuerpo.

#### Contrato de la declaración

**Parámetros:**

- `value: String` — `value` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.
- `palette: MyNotesPalette` — `palette` recibe un valor de tipo `MyNotesPalette`. El contrato no marca este parámetro como anulable.

**Retorno:** `Color`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 60 | `return when (value) {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 61 | `"red" -> Color(0xFFE55757)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 62 | `"coral" -> Color(0xFFF27663)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 63 | `"orange" -> Color(0xFFEF8A39)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 64 | `"amber" -> Color(0xFFE8B632)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 65 | `"yellow" -> Color(0xFFF2CF45)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 66 | `"lime" -> Color(0xFF9BCB4B)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 67 | `"green" -> Color(0xFF63A85C)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 68 | `"mint" -> Color(0xFF58B98E)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 69 | `"teal" -> Color(0xFF3FA4A0)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 70 | `"cyan" -> Color(0xFF45B9C8)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 71 | `"sky" -> Color(0xFF4FA9E2)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 72 | `"blue" -> Color(0xFF4B8EDB)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 73 | `"indigo" -> Color(0xFF6275CF)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 74 | `"violet" -> Color(0xFF8B6BC5)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 75 | `"purple" -> Color(0xFFA05BC1)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 76 | `"pink" -> Color(0xFFD96787)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 77 | `"rose" -> Color(0xFFE16F9A)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 78 | `"brown" -> Color(0xFF9A7157)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 79 | `"graphite" -> Color(0xFF59636A)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 80 | `else -> palette.accent` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `Color`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.

### 4.9 `TextStyle` — fun, líneas 86–92

```kotlin
private fun TextStyle.withBlackOutline(enabled: Boolean): TextStyle {
    return if (enabled) {
        copy(shadow = BlackTextOutlineShadow)
    } else {
        this
    }
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `enabled: Boolean` — `enabled` recibe un valor de tipo `Boolean`. El contrato no marca este parámetro como anulable.

**Retorno:** `TextStyle`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 87 | `return if (enabled) {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `withBlackOutline`, `copy`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.10 `typographyWithBlackOutline` — fun, líneas 94–106

```kotlin
private fun typographyWithBlackOutline(base: MaterialTypography, enabled: Boolean): MaterialTypography {
    if (!enabled) {
        return base
    }
    return base.copy(displayLarge = base.displayLarge.withBlackOutline(true), displayMedium = base.displayMedium.withBlackOutline(true),
        displaySmall = base.displaySmall.withBlackOutline(true), headlineLarge = base.headlineLarge.withBlackOutline(true), headlineMedium =
            base.headlineMedium.withBlackOutline(true), headlineSmall = base.headlineSmall.withBlackOutline(true), titleLarge =
            base.titleLarge.withBlackOutline(true), titleMedium = base.titleMedium.withBlackOutline(true), titleSmall =
            base.titleSmall.withBlackOutline(true), bodyLarge = base.bodyLarge.withBlackOutline(true), bodyMedium =
            base.bodyMedium.withBlackOutline(true), bodySmall = base.bodySmall.withBlackOutline(true), labelLarge =
            base.labelLarge.withBlackOutline(true), labelMedium = base.labelMedium.withBlackOutline(true), labelSmall =
            base.labelSmall.withBlackOutline(true))
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `base: MaterialTypography` — `base` recibe un valor de tipo `MaterialTypography`. El contrato no marca este parámetro como anulable.
- `enabled: Boolean` — `enabled` recibe un valor de tipo `Boolean`. El contrato no marca este parámetro como anulable.

**Retorno:** `MaterialTypography`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 95 | `if (!enabled) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 96 | `return base` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 98 | `return base.copy(displayLarge = base.displayLarge.withBlackOutline(true), displayMedium = base.displayMedium.withBlackOutline(true),` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `base.copy`, `base.displayLarge.withBlackOutline`, `base.displayMedium.withBlackOutline`, `base.displaySmall.withBlackOutline`, `base.headlineLarge.withBlackOutline`, `base.headlineMedium.withBlackOutline`, `base.headlineSmall.withBlackOutline`, `base.titleLarge.withBlackOutline`, `base.titleMedium.withBlackOutline`, `base.titleSmall.withBlackOutline`, `base.bodyLarge.withBlackOutline`, `base.bodyMedium.withBlackOutline`, `base.bodySmall.withBlackOutline`, `base.labelLarge.withBlackOutline`, `base.labelMedium.withBlackOutline`, `base.labelSmall.withBlackOutline`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.11 `lightScheme` — fun, líneas 108–151

```kotlin
private fun lightScheme(palette:
        MyNotesPalette, toneIndex: Int, backgroundIntensity: Float, surfacePanelIntensity: Float, headerIntensity: Float, textColor: String,
    accentColor: String): ColorScheme {
    val baseTone = palette.tones[toneIndex.coerceIn(0, 3)]
    val body = applySectionIntensity(baseTone, backgroundIntensity)
    val header = applySectionIntensity(baseTone, headerIntensity)
    /*
     * 72% reproduce exactamente las mezclas que tenía la app:
     * low 0.72, normal 0.58, high 0.42 y variant 0.45.
     */
    val panelMix = (surfacePanelIntensity.coerceIn(0f, 100f) / 100f)
    val accent = resolveAccentColor(accentColor, palette)
    val automaticDarkTone = textColor == "auto" && toneIndex.coerceIn(0, 3) >= 2
    /*
     * Los tonos 1-2 se mantienen en una familia clara para texto negro.
     * Los tonos 3-4 se mezclan hacia negro para que el texto blanco no
     * pierda contraste aunque el usuario aumente la intensidad del panel.
     */
    val panelTarget = if (automaticDarkTone) Color.Black else Color.White
    val surfaceVariantColor = mixColor(body, panelTarget, (panelMix * 0.625f).coerceIn(0f, 1f))
    val surfaceContainerColor = mixColor(body, panelTarget, (panelMix * 0.8055556f).coerceIn(0f, 1f))
    val bodyContent = resolvedPaletteTextColor(textColor = textColor, toneIndex = toneIndex, background = body)
    val surfaceContent = resolvedPaletteTextColor(textColor = textColor, toneIndex = toneIndex, background = header)
    val variantContent = resolvedPaletteSecondaryTextColor(textColor = textColor, toneIndex = toneIndex, background = surfaceVariantColor)
    return lightColorScheme(
        primary = accent,
        onPrimary = readableContentColor(accent),
        primaryContainer = mixColor(accent, Color.White, 0.80f),
        onPrimaryContainer = readableContentColor(mixColor(accent, Color.White, 0.80f)),
        secondary = accent,
        onSecondary = readableContentColor(accent),
        secondaryContainer = mixColor(accent, Color.White, 0.88f),
        onSecondaryContainer = readableContentColor(mixColor(accent, Color.White, 0.88f)),
        background = body,
        onBackground = bodyContent,
        surface = header,
        onSurface = surfaceContent,
        surfaceVariant = surfaceVariantColor,
        onSurfaceVariant = variantContent,
        surfaceContainerLow = mixColor(body, panelTarget, panelMix),
        surfaceContainer = surfaceContainerColor,
        surfaceContainerHigh = mixColor(body, panelTarget, (panelMix * 0.5833333f).coerceIn(0f, 1f)),
        outline = ensureUiContrast(preferred = mixColor(accent, Color.Black, 0.22f), background = body, minimumContrast = 3f))
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `palette: MyNotesPalette` — `palette` recibe un valor de tipo `MyNotesPalette`. El contrato no marca este parámetro como anulable.
- `toneIndex: Int` — `toneIndex` recibe un valor de tipo `Int`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.
- `backgroundIntensity: Float` — `backgroundIntensity` recibe un valor de tipo `Float`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.
- `surfacePanelIntensity: Float` — `surfacePanelIntensity` recibe un valor de tipo `Float`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.
- `headerIntensity: Float` — `headerIntensity` recibe un valor de tipo `Float`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.
- `textColor: String` — `textColor` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.
- `accentColor: String` — `accentColor` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `ColorScheme`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 111 | `val baseTone` | `inferido` | `palette.tones[toneIndex.coerceIn(0, 3)]` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El inicializador limita explícitamente el valor a un rango, evitando que dimensiones/intensidades/sizes inválidos se propaguen. |
| 112 | `val body` | `inferido` | `applySectionIntensity(baseTone, backgroundIntensity)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 113 | `val header` | `inferido` | `applySectionIntensity(baseTone, headerIntensity)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 118 | `val panelMix` | `inferido` | `(surfacePanelIntensity.coerceIn(0f, 100f) / 100f)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El inicializador limita explícitamente el valor a un rango, evitando que dimensiones/intensidades/sizes inválidos se propaguen. |
| 119 | `val accent` | `inferido` | `resolveAccentColor(accentColor, palette)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 120 | `val automaticDarkTone` | `inferido` | `textColor == "auto" && toneIndex.coerceIn(0, 3) >= 2` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El inicializador limita explícitamente el valor a un rango, evitando que dimensiones/intensidades/sizes inválidos se propaguen. |
| 126 | `val panelTarget` | `inferido` | `if (automaticDarkTone) Color.Black else Color.White` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 127 | `val surfaceVariantColor` | `inferido` | `mixColor(body, panelTarget, (panelMix * 0.625f).coerceIn(0f, 1f))` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El inicializador limita explícitamente el valor a un rango, evitando que dimensiones/intensidades/sizes inválidos se propaguen. |
| 128 | `val surfaceContainerColor` | `inferido` | `mixColor(body, panelTarget, (panelMix * 0.8055556f).coerceIn(0f, 1f))` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El inicializador limita explícitamente el valor a un rango, evitando que dimensiones/intensidades/sizes inválidos se propaguen. |
| 129 | `val bodyContent` | `inferido` | `resolvedPaletteTextColor(textColor = textColor, toneIndex = toneIndex, background = body)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 130 | `val surfaceContent` | `inferido` | `resolvedPaletteTextColor(textColor = textColor, toneIndex = toneIndex, background = header)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 131 | `val variantContent` | `inferido` | `resolvedPaletteSecondaryTextColor(textColor = textColor, toneIndex = toneIndex, background = surface…` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 132 | `return lightColorScheme(` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

**Restricciones concretas que aparecen en este bloque:**
- **Límite numérico:** Se fuerza el valor a un intervalo o umbral para impedir que salgan valores fuera de los límites esperados. Apariciones en este bloque: 6.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `toneIndex.coerceIn`, `applySectionIntensity`, `surfacePanelIntensity.coerceIn`, `resolveAccentColor`, `mixColor`, `coerceIn`, `resolvedPaletteTextColor`, `resolvedPaletteSecondaryTextColor`, `lightColorScheme`, `readableContentColor`, `ensureUiContrast`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.12 `darkScheme` — fun, líneas 153–204

```kotlin
private fun darkScheme(palette:
        MyNotesPalette, toneIndex: Int, backgroundIntensity: Float, surfacePanelIntensity: Float, headerIntensity: Float, textColor: String,
    accentColor: String): ColorScheme {
    val selected = palette.tones[toneIndex.coerceIn(0, 3)]
    val automaticPaletteMode = textColor != "black" && textColor != "white"
    val automaticDarkTone = automaticPaletteMode && toneIndex.coerceIn(0, 3) >= 2
    /*
     * En Automático la tonalidad elegida manda sobre el modo del sistema:
     * 1-2 permanecen claras (texto negro) y 3-4 oscuras (texto blanco).
     * En Negro/Blanco manual se conserva el comportamiento oscuro anterior.
     */
    val base = if (automaticPaletteMode) {
            selected
        } else {
            mixColor(selected, Color.Black, 0.84f)
        }
    val body = applySectionIntensity(base, backgroundIntensity)
    val header = applySectionIntensity(base, headerIntensity)
    /*
     * El modo oscuro usa mezclas mucho más pequeñas.
     * 72% conserva la apariencia anterior y el resto escala
     * proporcionalmente hasta el valor elegido por el usuario.
     */
    val panelScale = (surfacePanelIntensity.coerceIn(0f, 100f) / 72f).coerceIn(0f, 1.3888889f)
    val rawAccent = resolveAccentColor(accentColor, palette)
    val accent = mixColor(rawAccent, Color.White, 0.14f)
    val panelTarget = if (automaticDarkTone) Color.Black else Color.White
    val surfaceVariantColor = mixColor(body, panelTarget, (0.08f * panelScale).coerceIn(0f, 1f))
    val surfaceContainerColor = mixColor(body, panelTarget, (0.08f * panelScale).coerceIn(0f, 1f))
    val bodyContent = resolvedPaletteTextColor(textColor = textColor, toneIndex = toneIndex, background = body)
    val surfaceContent = resolvedPaletteTextColor(textColor = textColor, toneIndex = toneIndex, background = header)
    val variantContent = resolvedPaletteSecondaryTextColor(textColor = textColor, toneIndex = toneIndex, background = surfaceVariantColor)
    return darkColorScheme(
        primary = accent,
        onPrimary = readableContentColor(accent),
        primaryContainer = mixColor(rawAccent, Color.Black, 0.50f),
        onPrimaryContainer = readableContentColor(mixColor(rawAccent, Color.Black, 0.50f)),
        secondary = accent,
        onSecondary = readableContentColor(accent),
        secondaryContainer = mixColor(rawAccent, Color.Black, 0.60f),
        onSecondaryContainer = readableContentColor(mixColor(rawAccent, Color.Black, 0.60f)),
        background = body,
        onBackground = bodyContent,
        surface = header,
        onSurface = surfaceContent,
        surfaceVariant = surfaceVariantColor,
        onSurfaceVariant = variantContent,
        surfaceContainerLow = mixColor(body, panelTarget, (0.05f * panelScale).coerceIn(0f, 1f)),
        surfaceContainer = surfaceContainerColor,
        surfaceContainerHigh = mixColor(body, panelTarget, (0.12f * panelScale).coerceIn(0f, 1f)),
        outline = ensureUiContrast(preferred = mixColor(accent, Color.White, 0.18f), background = body, minimumContrast = 3f))
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `palette: MyNotesPalette` — `palette` recibe un valor de tipo `MyNotesPalette`. El contrato no marca este parámetro como anulable.
- `toneIndex: Int` — `toneIndex` recibe un valor de tipo `Int`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.
- `backgroundIntensity: Float` — `backgroundIntensity` recibe un valor de tipo `Float`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.
- `surfacePanelIntensity: Float` — `surfacePanelIntensity` recibe un valor de tipo `Float`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.
- `headerIntensity: Float` — `headerIntensity` recibe un valor de tipo `Float`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.
- `textColor: String` — `textColor` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.
- `accentColor: String` — `accentColor` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `ColorScheme`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 156 | `val selected` | `inferido` | `palette.tones[toneIndex.coerceIn(0, 3)]` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El nombre indica un estado de decisión/visibilidad/selección usado para controlar una rama de comportamiento o presentación. El inicializador limita explícitamente el valor a un rango, evitando que dimensiones/intensidades/sizes inválidos se propaguen. |
| 157 | `val automaticPaletteMode` | `inferido` | `textColor != "black" && textColor != "white"` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Actúa como selector de estrategia/perfil; su valor determina qué rama de configuración se aplica. |
| 158 | `val automaticDarkTone` | `inferido` | `automaticPaletteMode && toneIndex.coerceIn(0, 3) >= 2` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El inicializador limita explícitamente el valor a un rango, evitando que dimensiones/intensidades/sizes inválidos se propaguen. |
| 164 | `val base` | `inferido` | `if (automaticPaletteMode) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 169 | `val body` | `inferido` | `applySectionIntensity(base, backgroundIntensity)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 170 | `val header` | `inferido` | `applySectionIntensity(base, headerIntensity)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 176 | `val panelScale` | `inferido` | `(surfacePanelIntensity.coerceIn(0f, 100f) / 72f).coerceIn(0f, 1.3888889f)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El inicializador limita explícitamente el valor a un rango, evitando que dimensiones/intensidades/sizes inválidos se propaguen. |
| 177 | `val rawAccent` | `inferido` | `resolveAccentColor(accentColor, palette)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 178 | `val accent` | `inferido` | `mixColor(rawAccent, Color.White, 0.14f)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 179 | `val panelTarget` | `inferido` | `if (automaticDarkTone) Color.Black else Color.White` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 180 | `val surfaceVariantColor` | `inferido` | `mixColor(body, panelTarget, (0.08f * panelScale).coerceIn(0f, 1f))` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El inicializador limita explícitamente el valor a un rango, evitando que dimensiones/intensidades/sizes inválidos se propaguen. |
| 181 | `val surfaceContainerColor` | `inferido` | `mixColor(body, panelTarget, (0.08f * panelScale).coerceIn(0f, 1f))` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El inicializador limita explícitamente el valor a un rango, evitando que dimensiones/intensidades/sizes inválidos se propaguen. |
| 182 | `val bodyContent` | `inferido` | `resolvedPaletteTextColor(textColor = textColor, toneIndex = toneIndex, background = body)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 183 | `val surfaceContent` | `inferido` | `resolvedPaletteTextColor(textColor = textColor, toneIndex = toneIndex, background = header)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 184 | `val variantContent` | `inferido` | `resolvedPaletteSecondaryTextColor(textColor = textColor, toneIndex = toneIndex, background = surface…` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 185 | `return darkColorScheme(` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

**Restricciones concretas que aparecen en este bloque:**
- **Límite numérico:** Se fuerza el valor a un intervalo o umbral para impedir que salgan valores fuera de los límites esperados. Apariciones en este bloque: 8.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `toneIndex.coerceIn`, `mixColor`, `applySectionIntensity`, `surfacePanelIntensity.coerceIn`, `coerceIn`, `resolveAccentColor`, `resolvedPaletteTextColor`, `resolvedPaletteSecondaryTextColor`, `darkColorScheme`, `readableContentColor`, `ensureUiContrast`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.13 `MyNotesTheme` — fun, líneas 207–267

```kotlin
fun MyNotesTheme(darkTheme: Boolean = isSystemInDarkTheme(),
    backgroundColor: String = "neutral",
    backgroundToneIndex: Int = 0,
    backgroundIntensity: Float = 0f,
    surfacePanelIntensity: Float = 72f,
    headerIntensity: Float = 18f,
    textColor: String = "auto",
    textOutlineEnabled: Boolean = false,
    accentColor: String = "palette",
    content:
        @Composable () -> Unit) {
    val palette = remember(backgroundColor) {
            PaletteCatalog.find(backgroundColor)
        }
    /*
     * Crear un ColorScheme completo implica varias mezclas de Color.
     * Lo memorizamos para que cambiar avatar, tamaño de tarjeta, iconos,
     * filtros, búsqueda, etc. no vuelva a calcular el tema.
     */
    val scheme = remember(darkTheme, palette.key, backgroundToneIndex, backgroundIntensity, surfacePanelIntensity, headerIntensity,
            textColor, textOutlineEnabled, accentColor) {
            if (darkTheme) {
                darkScheme(palette = palette,
                    toneIndex = backgroundToneIndex,
                    backgroundIntensity = backgroundIntensity,
                    surfacePanelIntensity = surfacePanelIntensity,
                    headerIntensity = headerIntensity,
                    textColor = textColor,
                    accentColor = accentColor)
            } else {
                lightScheme(palette = palette,
                    toneIndex = backgroundToneIndex,
                    backgroundIntensity = backgroundIntensity,
                    surfacePanelIntensity = surfacePanelIntensity,
                    headerIntensity = headerIntensity,
                    textColor = textColor,
                    accentColor = accentColor)
            }
        }
    val resolvedTypography = remember(textOutlineEnabled) {
            typographyWithBlackOutline(base = Typography,
                enabled = textOutlineEnabled)
        }
    MaterialTheme(colorScheme = scheme,
        typography = resolvedTypography) {
        /*
         * Los componentes Material toman el borde de Typography.
         * Los Text() simples que usan LocalTextStyle también reciben
         * el mismo halo sin tener que modificar cada pantalla.
         */
        val inheritedTextStyle = if (textOutlineEnabled) {
                LocalTextStyle.current.copy(shadow = BlackTextOutlineShadow)
            } else {
                LocalTextStyle.current
            }
        CompositionLocalProvider(LocalTextStyle provides
                inheritedTextStyle) {
            content()
        }
    }
}
```

#### Qué hace y por qué existe

Composable que declara una parte de la interfaz. Su cuerpo transforma estado y parámetros en UI y callbacks, y puede reevaluarse durante recomposiciones.

#### Contrato de la declaración

**Parámetros:**

- `darkTheme: Boolean = isSystemInDarkTheme()` — `darkTheme` recibe un valor de tipo `Boolean`. El contrato no marca este parámetro como anulable. Tiene valor por defecto `isSystemInDarkTheme()`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada.
- `backgroundColor: String = "neutral"` — `backgroundColor` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable. Tiene valor por defecto `"neutral"`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada.
- `backgroundToneIndex: Int = 0` — `backgroundToneIndex` recibe un valor de tipo `Int`. El contrato no marca este parámetro como anulable. Tiene valor por defecto `0`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.
- `backgroundIntensity: Float = 0f` — `backgroundIntensity` recibe un valor de tipo `Float`. El contrato no marca este parámetro como anulable. Tiene valor por defecto `0f`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.
- `surfacePanelIntensity: Float = 72f` — `surfacePanelIntensity` recibe un valor de tipo `Float`. El contrato no marca este parámetro como anulable. Tiene valor por defecto `72f`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.
- `headerIntensity: Float = 18f` — `headerIntensity` recibe un valor de tipo `Float`. El contrato no marca este parámetro como anulable. Tiene valor por defecto `18f`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.
- `textColor: String = "auto"` — `textColor` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable. Tiene valor por defecto `"auto"`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada.
- `textOutlineEnabled: Boolean = false` — `textOutlineEnabled` recibe un valor de tipo `Boolean`. El contrato no marca este parámetro como anulable. Tiene valor por defecto `false`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada.
- `accentColor: String = "palette"` — `accentColor` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable. Tiene valor por defecto `"palette"`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada.
- `content: @Composable () -> Unit` — `content` recibe un valor de tipo `@Composable () -> Unit`. El contrato no marca este parámetro como anulable. Es un callback: el bloque no posee necesariamente la acción final, sino que notifica/delega al consumidor.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `@Composable` convierte la función en una unidad declarativa de Compose y la somete a reglas de recomposición.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 218 | `val palette` | `inferido` | `remember(backgroundColor) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 226 | `val scheme` | `inferido` | `remember(darkTheme, palette.key, backgroundToneIndex, backgroundIntensity, surfacePanelIntensity, he…` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 246 | `val resolvedTypography` | `inferido` | `remember(textOutlineEnabled) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 257 | `val inheritedTextStyle` | `inferido` | `if (textOutlineEnabled) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 217 | `@Composable () -> Unit) {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 228 | `if (darkTheme) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |

#### Semántica Compose/lifecycle

- **`@Composable`:** La función describe UI declarativa y puede ejecutarse nuevamente por recomposición; no debe interpretarse como una ejecución única imperativa.
- **`remember`:** Conserva valores entre recomposiciones bajo las mismas claves.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `isSystemInDarkTheme`, `Composable`, `remember`, `PaletteCatalog.find`, `darkScheme`, `lightScheme`, `typographyWithBlackOutline`, `MaterialTheme`, `LocalTextStyle.current.copy`, `CompositionLocalProvider`, `content`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- No convertir estado de Compose en variables ordinarias sin considerar recomposición y persistencia temporal.

## 5. Inventario global de propiedades/variables detectadas

| Línea | Identificador | Mutabilidad | Tipo | Explicación |
|---:|---|---|---|---|
| 21 | `value` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El inicializador limita explícitamente el valor a un rango, evitando que dimensiones/intensidades/sizes inválidos se propaguen. |
| 51 | `primary` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 84 | `BlackTextOutlineShadow` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 111 | `baseTone` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El inicializador limita explícitamente el valor a un rango, evitando que dimensiones/intensidades/sizes inválidos se propaguen. |
| 112 | `body` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 113 | `header` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 118 | `panelMix` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El inicializador limita explícitamente el valor a un rango, evitando que dimensiones/intensidades/sizes inválidos se propaguen. |
| 119 | `accent` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 120 | `automaticDarkTone` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El inicializador limita explícitamente el valor a un rango, evitando que dimensiones/intensidades/sizes inválidos se propaguen. |
| 126 | `panelTarget` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 127 | `surfaceVariantColor` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El inicializador limita explícitamente el valor a un rango, evitando que dimensiones/intensidades/sizes inválidos se propaguen. |
| 128 | `surfaceContainerColor` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El inicializador limita explícitamente el valor a un rango, evitando que dimensiones/intensidades/sizes inválidos se propaguen. |
| 129 | `bodyContent` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 130 | `surfaceContent` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 131 | `variantContent` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 156 | `selected` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El nombre indica un estado de decisión/visibilidad/selección usado para controlar una rama de comportamiento o presentación. El inicializador limita explícitamente el valor a un rango, evitando que dimensiones/intensidades/sizes inválidos se propaguen. |
| 157 | `automaticPaletteMode` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Actúa como selector de estrategia/perfil; su valor determina qué rama de configuración se aplica. |
| 158 | `automaticDarkTone` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El inicializador limita explícitamente el valor a un rango, evitando que dimensiones/intensidades/sizes inválidos se propaguen. |
| 164 | `base` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 169 | `body` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 170 | `header` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 176 | `panelScale` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El inicializador limita explícitamente el valor a un rango, evitando que dimensiones/intensidades/sizes inválidos se propaguen. |
| 177 | `rawAccent` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 178 | `accent` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 179 | `panelTarget` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 180 | `surfaceVariantColor` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El inicializador limita explícitamente el valor a un rango, evitando que dimensiones/intensidades/sizes inválidos se propaguen. |
| 181 | `surfaceContainerColor` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El inicializador limita explícitamente el valor a un rango, evitando que dimensiones/intensidades/sizes inválidos se propaguen. |
| 182 | `bodyContent` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 183 | `surfaceContent` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 184 | `variantContent` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 218 | `palette` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 226 | `scheme` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 246 | `resolvedTypography` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 257 | `inheritedTextStyle` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

## 6. Mapa exhaustivo de TODOS los bloques delimitados por `{}`

Esta tabla recorre todas las llaves estructurales detectadas fuera de strings/comentarios. Así se documentan también lambdas de Compose, callbacks, iteraciones y ámbitos locales que no tienen un nombre propio de función.

| Inicio–fin | Prof. | Cabecera/contexto | Qué significa ese bloque |
|---|---:|---|---|
| 20–26 | 0 | `private fun mixColor(first: Color, second: Color, amount: Float): Color` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 28–30 | 0 | `private fun intensityAmount(intensity: Float): Float` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 32–34 | 0 | `fun applySectionIntensity(color: Color, intensity: Float): Color` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 45–49 | 0 | `private fun resolvedPaletteTextColor(textColor: String, toneIndex: Int, background: Color): Color = when (textColor)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 50–57 | 0 | `private fun resolvedPaletteSecondaryTextColor(textColor: String, toneIndex: Int, background: Color): Color` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 52–54 | 1 | `return if (textColor == "black" \|\| textColor == "white")` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 54–56 | 1 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 59–82 | 0 | `private fun resolveAccentColor(value: String, palette: MyNotesPalette): Color` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 60–81 | 1 | `return when (value)` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 86–92 | 0 | `private fun TextStyle.withBlackOutline(enabled: Boolean): TextStyle` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 87–89 | 1 | `return if (enabled)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 89–91 | 1 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 94–106 | 0 | `private fun typographyWithBlackOutline(base: MaterialTypography, enabled: Boolean): MaterialTypography` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 95–97 | 1 | `if (!enabled)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 110–151 | 0 | `accentColor: String): ColorScheme` | Ámbito delimitado por llaves en profundidad 0. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 155–204 | 0 | `accentColor: String): ColorScheme` | Ámbito delimitado por llaves en profundidad 0. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 164–166 | 1 | `val base = if (automaticPaletteMode)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 166–168 | 1 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 217–267 | 0 | `@Composable () -> Unit)` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 218–220 | 1 | `val palette = remember(backgroundColor)` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 227–245 | 1 | `textColor, textOutlineEnabled, accentColor)` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 228–236 | 2 | `if (darkTheme)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 236–244 | 2 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 246–249 | 1 | `val resolvedTypography = remember(textOutlineEnabled)` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 251–266 | 1 | `typography = resolvedTypography)` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 257–259 | 2 | `val inheritedTextStyle = if (textOutlineEnabled)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 259–261 | 2 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 263–265 | 2 | `inheritedTextStyle)` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |

## 7. Lectura de seguridad antes de tocar este archivo

La documentación describe **lo que el código actual ya hace**. No constituye una propuesta de refactorización. Si se quisiera cambiar algo en el futuro, primero habría que preservar contratos públicos, claves de configuración, restricciones de API Android, comportamiento de Compose, lifecycle, operaciones de I/O y compatibilidad con los datos ya persistidos. En esta entrega no se hizo ninguna de esas modificaciones.

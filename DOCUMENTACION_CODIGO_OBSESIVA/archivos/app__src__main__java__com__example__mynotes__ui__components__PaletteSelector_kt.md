# PaletteSelector.kt — documentación exhaustiva por bloques

**Ruta de código real:** `app/src/main/java/com/example/mynotes/ui/components/PaletteSelector.kt`  
**SHA-256 del archivo ejecutable sin tocar:** `8bdcf40d3a8561a120df4c0254a220ca6d74836c6b7fd6272d7e04751014fcad`  
**Líneas del código real:** 195

> **Garantía:** esta documentación se genera en una carpeta paralela. El archivo Kotlin/Kotlin DSL anterior no se modifica. Los fragmentos mostrados abajo son copias de lectura; la aplicación compila usando únicamente el archivo original de `app/...`.

## 1. Papel del archivo

Componente Compose que presenta una paleta y permite seleccionar paleta/tono con indicación visual del color activo.

**Arquitectura.** Consume PaletteCatalog y callbacks de ajustes; mantiene separada la cuadrícula de colores de la pantalla grande de Configuración.

**Flujo general.** El flujo se describe declaración por declaración en este documento.

## 2. Package e imports

El `package` es `com.example.mynotes.ui.components`. Determina el namespace lógico del archivo, resolución de visibilidad `internal`/package-related tooling y la ruta conceptual desde la que otras clases lo importan.

El archivo declara **48 imports**. Cada import evita usar el nombre totalmente calificado en el cuerpo y, además, revela las dependencias técnicas del bloque:

**Jetpack/Compose:** `androidx.compose.animation.AnimatedVisibility`, `androidx.compose.animation.fadeIn`, `androidx.compose.animation.fadeOut`, `androidx.compose.animation.scaleIn`, `androidx.compose.animation.scaleOut`, `androidx.compose.animation.core.animateDpAsState`, `androidx.compose.animation.core.tween`, `androidx.compose.foundation.BorderStroke`, `androidx.compose.foundation.background`, `androidx.compose.foundation.border`, `androidx.compose.foundation.clickable`, `androidx.compose.foundation.interaction.MutableInteractionSource`, `androidx.compose.foundation.layout.Arrangement`, `androidx.compose.foundation.layout.aspectRatio`, `androidx.compose.foundation.layout.Box`, `androidx.compose.foundation.layout.Column`, `androidx.compose.foundation.layout.Row`, `androidx.compose.foundation.layout.Spacer`, `androidx.compose.foundation.layout.fillMaxWidth`, `androidx.compose.foundation.layout.padding`, `androidx.compose.foundation.shape.CircleShape`, `androidx.compose.foundation.shape.RoundedCornerShape`, `androidx.compose.material.icons.Icons`, `androidx.compose.material.icons.filled.Check`, `androidx.compose.material3.Icon`, `androidx.compose.material3.MaterialTheme`, `androidx.compose.material3.Surface`, `androidx.compose.material3.Text`, `androidx.compose.runtime.Composable`, `androidx.compose.runtime.getValue`, `androidx.compose.runtime.remember`, `androidx.compose.ui.Alignment`, `androidx.compose.ui.Modifier`, `androidx.compose.ui.draw.clip`, `androidx.compose.ui.graphics.Color`, `androidx.compose.ui.graphics.luminance`, `androidx.compose.ui.platform.LocalContext`, `androidx.compose.ui.res.stringResource`, `androidx.compose.ui.text.font.FontFamily`, `androidx.compose.ui.text.font.FontWeight`, `androidx.compose.ui.text.style.TextOverflow`, `androidx.compose.ui.unit.dp`.

**Proyecto MyNotes:** `com.example.mynotes.ui.theme.MyNotesPalette`, `com.example.mynotes.ui.theme.resolveUiTextColor`, `com.example.mynotes.ui.theme.resolveUiGraphicColor`, `com.example.mynotes.ui.motion.AppMotion`, `com.example.mynotes.ui.sound.UiActionSound`, `com.example.mynotes.ui.sound.UiSoundPlayer`.

## 3. Restricciones e invariantes visibles en el archivo

- **Límite visual (1 aparición/apariciones):** La UI impone un límite explícito de tamaño o cantidad de líneas para impedir crecimiento visual no controlado.

## 4. Bloques de código, uno por uno

### 4.1 `PaletteSelector` — fun, líneas 61–91

```kotlin
fun PaletteSelector(palettes: List<MyNotesPalette>, selectedPaletteKey: String, selectedToneIndex: Int, onPaletteSelected: (String) -> Unit,
    onToneSelected: (paletteKey: String, toneIndex: Int) -> Unit, animationsEnabled: Boolean = true, animationSpeed: Float = 1f,
    textColorMode: String = "auto", fontFamily: FontFamily = FontFamily.Default) {
    val paletteRows = remember(palettes) {
            palettes.chunked(2)
        }
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        paletteRows.forEach {
                    rowPalettes ->
                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    rowPalettes.forEach {
                                palette ->
                            PaletteCard(modifier = Modifier.weight(1f),
                                palette = palette,
                                selected = palette.key == selectedPaletteKey,
                                selectedToneIndex = selectedToneIndex,
                                onPaletteSelected = onPaletteSelected,
                                onToneSelected = onToneSelected,
                                animationsEnabled = animationsEnabled,
                                animationSpeed = animationSpeed,
                                textColorMode = textColorMode,
                                fontFamily = fontFamily)
                        }
                    if (rowPalettes.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
    }
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `palettes: List<MyNotesPalette>` — `palettes` recibe un valor de tipo `List<MyNotesPalette>`. El contrato no marca este parámetro como anulable.
- `selectedPaletteKey: String` — `selectedPaletteKey` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable. Aunque el tipo sea `String`, semánticamente suele funcionar como clave/enumeración textual; las ramas del código delimitan los valores reconocidos.
- `selectedToneIndex: Int` — `selectedToneIndex` recibe un valor de tipo `Int`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.
- `onPaletteSelected: (String) -> Unit` — `onPaletteSelected` recibe un valor de tipo `(String) -> Unit`. El contrato no marca este parámetro como anulable. Es un callback: el bloque no posee necesariamente la acción final, sino que notifica/delega al consumidor.
- `onToneSelected: (paletteKey: String, toneIndex: Int) -> Unit` — `onToneSelected` recibe un valor de tipo `(paletteKey: String, toneIndex: Int) -> Unit`. El contrato no marca este parámetro como anulable. Es un callback: el bloque no posee necesariamente la acción final, sino que notifica/delega al consumidor. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.
- `animationsEnabled: Boolean = true` — `animationsEnabled` recibe un valor de tipo `Boolean`. El contrato no marca este parámetro como anulable. Tiene valor por defecto `true`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada.
- `animationSpeed: Float = 1f` — `animationSpeed` recibe un valor de tipo `Float`. El contrato no marca este parámetro como anulable. Tiene valor por defecto `1f`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.
- `textColorMode: String = "auto"` — `textColorMode` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable. Tiene valor por defecto `"auto"`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada. Aunque el tipo sea `String`, semánticamente suele funcionar como clave/enumeración textual; las ramas del código delimitan los valores reconocidos.
- `fontFamily: FontFamily = FontFamily.Default` — `fontFamily` recibe un valor de tipo `FontFamily`. El contrato no marca este parámetro como anulable. Tiene valor por defecto `FontFamily.Default`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `@Composable` convierte la función en una unidad declarativa de Compose y la somete a reglas de recomposición.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 64 | `val paletteRows` | `inferido` | `remember(palettes) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 61 | `fun PaletteSelector(palettes: List<MyNotesPalette>, selectedPaletteKey: String, selectedToneIndex: Int, onPaletteSelected: (String) -> Unit,` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 62 | `onToneSelected: (paletteKey: String, toneIndex: Int) -> Unit, animationsEnabled: Boolean = true, animationSpeed: Float = 1f,` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 69 | `rowPalettes ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 73 | `palette ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 85 | `if (rowPalettes.size == 1) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |

#### Semántica Compose/lifecycle

- **`remember`:** Conserva valores entre recomposiciones bajo las mismas claves.
- **`Modifier`:** La cadena de modificadores define tamaño, interacción, dibujo, semántica o posicionamiento; el orden de los modificadores puede ser significativo.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `remember`, `palettes.chunked`, `Column`, `Arrangement.spacedBy`, `Row`, `Modifier.fillMaxWidth`, `PaletteCard`, `Modifier.weight`, `Spacer`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- No convertir estado de Compose en variables ordinarias sin considerar recomposición y persistencia temporal.

### 4.2 `PaletteCard` — fun, líneas 94–162

```kotlin
private fun PaletteCard(modifier: Modifier, palette: MyNotesPalette, selected: Boolean, selectedToneIndex: Int,
    onPaletteSelected: (String) -> Unit, onToneSelected: (paletteKey: String, toneIndex: Int) -> Unit, animationsEnabled: Boolean,
    animationSpeed: Float, textColorMode: String, fontFamily: FontFamily) {
    val context = LocalContext.current
    val shape = RoundedCornerShape(16.dp)
    val cardBackground = MaterialTheme.colorScheme.surfaceContainerLow
    val cardTextColor = resolveUiTextColor(value = textColorMode, background = cardBackground)
    val cardGraphicColor = resolveUiGraphicColor(value = textColorMode, background = cardBackground)
    val motionDuration = AppMotion.duration(AppMotion.FAST, animationsEnabled, animationSpeed)
    val cardInteractionSource = remember {
            MutableInteractionSource()
        }
    val animatedBorderWidth by
        animateDpAsState(targetValue = if (selected) {
                    1.8.dp
                } else {
                    1.dp
                }, animationSpec = tween(durationMillis = motionDuration), label = "paletteBorder")
    Surface(modifier = modifier.clickable(interactionSource = cardInteractionSource, indication = null, onClick = {
                        UiSoundPlayer.playAction(context = context, action = UiActionSound.Theme)
                        onPaletteSelected(palette.key)
                    }),
        shape = shape,
        color = cardBackground,
        contentColor = cardTextColor,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        border = BorderStroke(width = animatedBorderWidth,
                color = cardGraphicColor)) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp,
                        vertical = 11.dp)) {
            Text(text = stringResource(palette.labelRes),
                style = MaterialTheme.typography.labelLarge,
                fontFamily = fontFamily,
                fontWeight = FontWeight.SemiBold,
                color = cardTextColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis)
            Row(modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically) {
                /*
                 * Cada tono recibe exactamente el mismo peso.
                 *
                 * Resultado:
                 * - los cuatro círculos siempre tienen el mismo diámetro;
                 * - aprovechan todo el ancho de la tarjeta;
                 * - no queda un hueco grande al final;
                 * - todas las paletas mantienen la misma proporción.
                 */
                palette.tones.forEachIndexed {
                            index, tone ->
                        Box(modifier = Modifier.weight(1f).aspectRatio(1f),
                            contentAlignment = Alignment.Center) {
                            PaletteToneCircle(modifier = Modifier.fillMaxWidth(),
                                color = tone,
                                selected = selected && selectedToneIndex == index,
                                onClick = {
                                    UiSoundPlayer.playAction(context = context, action = UiActionSound.Color)
                                    onToneSelected(palette.key, index)
                                },
                                animationsEnabled = animationsEnabled,
                                animationSpeed = animationSpeed)
                        }
                    }
            }
        }
    }
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `modifier: Modifier` — `modifier` recibe un valor de tipo `Modifier`. El contrato no marca este parámetro como anulable.
- `palette: MyNotesPalette` — `palette` recibe un valor de tipo `MyNotesPalette`. El contrato no marca este parámetro como anulable.
- `selected: Boolean` — `selected` recibe un valor de tipo `Boolean`. El contrato no marca este parámetro como anulable.
- `selectedToneIndex: Int` — `selectedToneIndex` recibe un valor de tipo `Int`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.
- `onPaletteSelected: (String) -> Unit` — `onPaletteSelected` recibe un valor de tipo `(String) -> Unit`. El contrato no marca este parámetro como anulable. Es un callback: el bloque no posee necesariamente la acción final, sino que notifica/delega al consumidor.
- `onToneSelected: (paletteKey: String, toneIndex: Int) -> Unit` — `onToneSelected` recibe un valor de tipo `(paletteKey: String, toneIndex: Int) -> Unit`. El contrato no marca este parámetro como anulable. Es un callback: el bloque no posee necesariamente la acción final, sino que notifica/delega al consumidor. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.
- `animationsEnabled: Boolean` — `animationsEnabled` recibe un valor de tipo `Boolean`. El contrato no marca este parámetro como anulable.
- `animationSpeed: Float` — `animationSpeed` recibe un valor de tipo `Float`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.
- `textColorMode: String` — `textColorMode` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable. Aunque el tipo sea `String`, semánticamente suele funcionar como clave/enumeración textual; las ramas del código delimitan los valores reconocidos.
- `fontFamily: FontFamily` — `fontFamily` recibe un valor de tipo `FontFamily`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.
- `@Composable` convierte la función en una unidad declarativa de Compose y la somete a reglas de recomposición.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 97 | `val context` | `inferido` | `LocalContext.current` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El `Context` da acceso a recursos/servicios/almacenamiento Android; debe respetarse el ciclo de vida del contexto recibido. |
| 98 | `val shape` | `inferido` | `RoundedCornerShape(16.dp)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 99 | `val cardBackground` | `inferido` | `MaterialTheme.colorScheme.surfaceContainerLow` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 100 | `val cardTextColor` | `inferido` | `resolveUiTextColor(value = textColorMode, background = cardBackground)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 101 | `val cardGraphicColor` | `inferido` | `resolveUiGraphicColor(value = textColorMode, background = cardBackground)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 102 | `val motionDuration` | `inferido` | `AppMotion.duration(AppMotion.FAST, animationsEnabled, animationSpeed)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 103 | `val cardInteractionSource` | `inferido` | `remember {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 95 | `onPaletteSelected: (String) -> Unit, onToneSelected: (paletteKey: String, toneIndex: Int) -> Unit, animationsEnabled: Boolean,` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 145 | `index, tone ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |

**Restricciones concretas que aparecen en este bloque:**
- **Límite visual:** La UI impone un límite explícito de tamaño o cantidad de líneas para impedir crecimiento visual no controlado. Apariciones en este bloque: 1.

#### Semántica Compose/lifecycle

- **`remember`:** Conserva valores entre recomposiciones bajo las mismas claves.
- **Callbacks `onClick`:** La lógica asociada se ejecuta por evento de usuario, no durante la composición inicial.
- **`Modifier`:** La cadena de modificadores define tamaño, interacción, dibujo, semántica o posicionamiento; el orden de los modificadores puede ser significativo.

#### Efectos secundarios y recursos

- **Sonido/háptica:** Produce feedback perceptible para el usuario; debe ejecutarse en respuesta a la interacción/estado correspondiente.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `RoundedCornerShape`, `resolveUiTextColor`, `resolveUiGraphicColor`, `AppMotion.duration`, `MutableInteractionSource`, `animateDpAsState`, `tween`, `Surface`, `modifier.clickable`, `UiSoundPlayer.playAction`, `onPaletteSelected`, `BorderStroke`, `Column`, `Modifier.fillMaxWidth`, `padding`, `Text`, `stringResource`, `Row`, `Arrangement.spacedBy`, `Box`, `Modifier.weight`, `aspectRatio`, `PaletteToneCircle`, `onToneSelected`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- No convertir estado de Compose en variables ordinarias sin considerar recomposición y persistencia temporal.

### 4.3 `PaletteToneCircle` — fun, líneas 165–195

```kotlin
private fun PaletteToneCircle(modifier: Modifier = Modifier, color: Color, selected: Boolean, onClick: () -> Unit,
    animationsEnabled: Boolean, animationSpeed: Float) {
    val checkColor = if (color.luminance() >
            0.48f) {
            Color.Black
        } else {
            Color.White
        }
    val toneInteractionSource = remember {
            MutableInteractionSource()
        }
    val motionDuration = AppMotion.duration(AppMotion.FAST, animationsEnabled, animationSpeed)
    Box(modifier = modifier.aspectRatio(1f).clip(CircleShape).background(color).then(if (selected) {
                        Modifier.border(width = 2.dp,
                                color = MaterialTheme.colorScheme.onSurface,
                                shape = CircleShape)
                    } else {
                        Modifier
                    }).clickable(interactionSource = toneInteractionSource, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center) {
        AnimatedVisibility(visible = selected, enter = fadeIn(animationSpec = tween(durationMillis = motionDuration)) + scaleIn(
                        animationSpec = tween(durationMillis = motionDuration), initialScale = 0.55f), exit = fadeOut(animationSpec = tween(
                            durationMillis = motionDuration)) + scaleOut(animationSpec = tween(durationMillis = motionDuration),
                        targetScale = 0.55f)) {
            Icon(imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = checkColor,
                modifier = Modifier.fillMaxWidth(0.52f).aspectRatio(1f))
        }
    }
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `modifier: Modifier = Modifier` — `modifier` recibe un valor de tipo `Modifier`. El contrato no marca este parámetro como anulable. Tiene valor por defecto `Modifier`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada.
- `color: Color` — `color` recibe un valor de tipo `Color`. El contrato no marca este parámetro como anulable.
- `selected: Boolean` — `selected` recibe un valor de tipo `Boolean`. El contrato no marca este parámetro como anulable.
- `onClick: () -> Unit` — `onClick` recibe un valor de tipo `() -> Unit`. El contrato no marca este parámetro como anulable. Es un callback: el bloque no posee necesariamente la acción final, sino que notifica/delega al consumidor.
- `animationsEnabled: Boolean` — `animationsEnabled` recibe un valor de tipo `Boolean`. El contrato no marca este parámetro como anulable.
- `animationSpeed: Float` — `animationSpeed` recibe un valor de tipo `Float`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.
- `@Composable` convierte la función en una unidad declarativa de Compose y la somete a reglas de recomposición.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 167 | `val checkColor` | `inferido` | `if (color.luminance() >` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 173 | `val toneInteractionSource` | `inferido` | `remember {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 176 | `val motionDuration` | `inferido` | `AppMotion.duration(AppMotion.FAST, animationsEnabled, animationSpeed)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 165 | `private fun PaletteToneCircle(modifier: Modifier = Modifier, color: Color, selected: Boolean, onClick: () -> Unit,` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |

#### Semántica Compose/lifecycle

- **`remember`:** Conserva valores entre recomposiciones bajo las mismas claves.
- **Callbacks `onClick`:** La lógica asociada se ejecuta por evento de usuario, no durante la composición inicial.
- **`Modifier`:** La cadena de modificadores define tamaño, interacción, dibujo, semántica o posicionamiento; el orden de los modificadores puede ser significativo.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `color.luminance`, `MutableInteractionSource`, `AppMotion.duration`, `Box`, `modifier.aspectRatio`, `clip`, `background`, `then`, `Modifier.border`, `clickable`, `AnimatedVisibility`, `fadeIn`, `tween`, `scaleIn`, `fadeOut`, `scaleOut`, `Icon`, `Modifier.fillMaxWidth`, `aspectRatio`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- No convertir estado de Compose en variables ordinarias sin considerar recomposición y persistencia temporal.

## 5. Inventario global de propiedades/variables detectadas

| Línea | Identificador | Mutabilidad | Tipo | Explicación |
|---:|---|---|---|---|
| 64 | `paletteRows` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 97 | `context` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El `Context` da acceso a recursos/servicios/almacenamiento Android; debe respetarse el ciclo de vida del contexto recibido. |
| 98 | `shape` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 99 | `cardBackground` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 100 | `cardTextColor` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 101 | `cardGraphicColor` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 102 | `motionDuration` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 103 | `cardInteractionSource` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 167 | `checkColor` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 173 | `toneInteractionSource` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 176 | `motionDuration` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

## 6. Mapa exhaustivo de TODOS los bloques delimitados por `{}`

Esta tabla recorre todas las llaves estructurales detectadas fuera de strings/comentarios. Así se documentan también lambdas de Compose, callbacks, iteraciones y ámbitos locales que no tienen un nombre propio de función.

| Inicio–fin | Prof. | Cabecera/contexto | Qué significa ese bloque |
|---|---:|---|---|
| 63–91 | 0 | `textColorMode: String = "auto", fontFamily: FontFamily = FontFamily.Default)` | Ámbito delimitado por llaves en profundidad 0. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 64–66 | 1 | `val paletteRows = remember(palettes)` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 67–90 | 1 | `Column(verticalArrangement = Arrangement.spacedBy(10.dp))` | Lambda de contenido de UI Compose: declara hijos y configuración visual dentro del contenedor. El orden y los `Modifier` influyen en medida, posición, dibujo e interacción. |
| 68–89 | 2 | `paletteRows.forEach` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 71–88 | 3 | `horizontalArrangement = Arrangement.spacedBy(10.dp))` | Ámbito delimitado por llaves en profundidad 3. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 72–84 | 4 | `rowPalettes.forEach` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 85–87 | 4 | `if (rowPalettes.size == 1)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 96–162 | 0 | `animationSpeed: Float, textColorMode: String, fontFamily: FontFamily)` | Ámbito delimitado por llaves en profundidad 0. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 103–105 | 1 | `val cardInteractionSource = remember` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 107–109 | 1 | `animateDpAsState(targetValue = if (selected)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 109–111 | 1 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 112–115 | 1 | `Surface(modifier = modifier.clickable(interactionSource = cardInteractionSource, indication = null, onClick =` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 122–161 | 1 | `color = cardGraphicColor))` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 124–160 | 2 | `vertical = 11.dp))` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 134–159 | 3 | `verticalAlignment = Alignment.CenterVertically)` | Ámbito delimitado por llaves en profundidad 3. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 144–158 | 4 | `palette.tones.forEachIndexed` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 147–157 | 5 | `contentAlignment = Alignment.Center)` | Ámbito delimitado por llaves en profundidad 5. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 151–154 | 6 | `onClick =` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 166–195 | 0 | `animationsEnabled: Boolean, animationSpeed: Float)` | Ámbito delimitado por llaves en profundidad 0. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 168–170 | 1 | `0.48f)` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 170–172 | 1 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 173–175 | 1 | `val toneInteractionSource = remember` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 177–181 | 1 | `Box(modifier = modifier.aspectRatio(1f).clip(CircleShape).background(color).then(if (selected)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 181–183 | 1 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 184–194 | 1 | `contentAlignment = Alignment.Center)` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 188–193 | 2 | `targetScale = 0.55f))` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |

## 7. Lectura de seguridad antes de tocar este archivo

La documentación describe **lo que el código actual ya hace**. No constituye una propuesta de refactorización. Si se quisiera cambiar algo en el futuro, primero habría que preservar contratos públicos, claves de configuración, restricciones de API Android, comportamiento de Compose, lifecycle, operaciones de I/O y compatibilidad con los datos ya persistidos. En esta entrega no se hizo ninguna de esas modificaciones.

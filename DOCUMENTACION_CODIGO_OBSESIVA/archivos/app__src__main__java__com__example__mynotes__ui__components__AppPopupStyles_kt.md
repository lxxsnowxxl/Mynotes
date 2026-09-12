# AppPopupStyles.kt — documentación exhaustiva por bloques

**Ruta de código real:** `app/src/main/java/com/example/mynotes/ui/components/AppPopupStyles.kt`  
**SHA-256 del archivo ejecutable sin tocar:** `7f182fe08bbda18421cd59ce5666b6df12407d4a11c6f3ec2b4f35c99994d6c8`  
**Líneas del código real:** 38

> **Garantía:** esta documentación se genera en una carpeta paralela. El archivo Kotlin/Kotlin DSL anterior no se modifica. Los fragmentos mostrados abajo son copias de lectura; la aplicación compila usando únicamente el archivo original de `app/...`.

## 1. Papel del archivo

Define estilos y envoltorios comunes para menús desplegables y diálogos de la aplicación.

**Arquitectura.** Centraliza forma, elevación, borde, contraste y propiedades de popup; esto incluye el comportamiento no focusable usado para conservar el modo inmersivo en versiones antiguas de Android.

**Flujo general.** El flujo se describe declaración por declaración en este documento.

## 2. Package e imports

El `package` es `com.example.mynotes.ui.components`. Determina el namespace lógico del archivo, resolución de visibilidad `internal`/package-related tooling y la ruta conceptual desde la que otras clases lo importan.

El archivo declara **13 imports**. Cada import evita usar el nombre totalmente calificado en el cuerpo y, además, revela las dependencias técnicas del bloque:

**Jetpack/Compose:** `androidx.compose.foundation.BorderStroke`, `androidx.compose.foundation.layout.ColumnScope`, `androidx.compose.foundation.shape.RoundedCornerShape`, `androidx.compose.material3.AlertDialog`, `androidx.compose.material3.DropdownMenu`, `androidx.compose.material3.MaterialTheme`, `androidx.compose.runtime.Composable`, `androidx.compose.ui.Modifier`, `androidx.compose.ui.graphics.Color`, `androidx.compose.ui.unit.dp`, `androidx.compose.ui.window.DialogProperties`, `androidx.compose.ui.window.PopupProperties`.

**Proyecto MyNotes:** `com.example.mynotes.ui.theme.ensureUiContrast`.

## 3. Restricciones e invariantes visibles en el archivo

- **Sin foco de popup (1 aparición/apariciones):** El popup se configura para no tomar el foco de ventana; en este proyecto ayuda a preservar el modo inmersivo y evita reaparición indeseada de la navegación Android.

## 4. Bloques de código, uno por uno

### 4.1 `AppDropdownMenu` — fun, líneas 18–26

```kotlin
fun AppDropdownMenu(expanded: Boolean, onDismissRequest: () -> Unit, modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh, properties: PopupProperties = PopupProperties(
        focusable = false, dismissOnBackPress = true, dismissOnClickOutside = true), content: @Composable ColumnScope.() -> Unit) {
    val borderColor = ensureUiContrast(preferred = MaterialTheme.colorScheme.outline.copy(alpha = 0.70f), background = containerColor,
        minimumContrast = 2.6f)
    DropdownMenu(expanded = expanded, onDismissRequest = onDismissRequest, modifier = modifier, shape = RoundedCornerShape(18.dp),
        containerColor = containerColor, tonalElevation = 0.dp, shadowElevation = 12.dp, border = BorderStroke(1.dp, borderColor),
        properties = properties, content = content)
}
```

#### Qué hace y por qué existe

Composable que declara una parte de la interfaz. Su cuerpo transforma estado y parámetros en UI y callbacks, y puede reevaluarse durante recomposiciones.

#### Contrato de la declaración

**Parámetros:**

- `expanded: Boolean` — `expanded` recibe un valor de tipo `Boolean`. El contrato no marca este parámetro como anulable.
- `onDismissRequest: () -> Unit` — `onDismissRequest` recibe un valor de tipo `() -> Unit`. El contrato no marca este parámetro como anulable. Es un callback: el bloque no posee necesariamente la acción final, sino que notifica/delega al consumidor.
- `modifier: Modifier = Modifier` — `modifier` recibe un valor de tipo `Modifier`. El contrato no marca este parámetro como anulable. Tiene valor por defecto `Modifier`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada.
- `containerColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh` — `containerColor` recibe un valor de tipo `Color`. El contrato no marca este parámetro como anulable. Tiene valor por defecto `MaterialTheme.colorScheme.surfaceContainerHigh`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada.
- `properties: PopupProperties = PopupProperties( focusable = false, dismissOnBackPress = true, dismissOnClickOutside = true)` — `properties` recibe un valor de tipo `PopupProperties`. El contrato no marca este parámetro como anulable. Tiene valor por defecto `PopupProperties( focusable = false, dismissOnBackPress = true, dismissOnClickOutside = true)`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada.
- `content: @Composable ColumnScope.() -> Unit` — `content` recibe un valor de tipo `@Composable ColumnScope.() -> Unit`. El contrato no marca este parámetro como anulable. Es un callback: el bloque no posee necesariamente la acción final, sino que notifica/delega al consumidor.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `@Composable` convierte la función en una unidad declarativa de Compose y la somete a reglas de recomposición.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 21 | `val borderColor` | `inferido` | `ensureUiContrast(preferred = MaterialTheme.colorScheme.outline.copy(alpha = 0.70f), background = con…` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 18 | `fun AppDropdownMenu(expanded: Boolean, onDismissRequest: () -> Unit, modifier: Modifier = Modifier,` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 20 | `focusable = false, dismissOnBackPress = true, dismissOnClickOutside = true), content: @Composable ColumnScope.() -> Unit) {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |

**Restricciones concretas que aparecen en este bloque:**
- **Sin foco de popup:** El popup se configura para no tomar el foco de ventana; en este proyecto ayuda a preservar el modo inmersivo y evita reaparición indeseada de la navegación Android. Apariciones en este bloque: 1.

#### Semántica Compose/lifecycle

- **`@Composable`:** La función describe UI declarativa y puede ejecutarse nuevamente por recomposición; no debe interpretarse como una ejecución única imperativa.
- **`Modifier`:** La cadena de modificadores define tamaño, interacción, dibujo, semántica o posicionamiento; el orden de los modificadores puede ser significativo.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `PopupProperties`, `ensureUiContrast`, `MaterialTheme.colorScheme.outline.copy`, `DropdownMenu`, `RoundedCornerShape`, `BorderStroke`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar `focusable = false` en estos popups si se quiere mantener el comportamiento inmersivo que evita que reaparezcan los botones de navegación del sistema.

### 4.2 `AppAlertDialog` — fun, líneas 29–38

```kotlin
fun AppAlertDialog(onDismissRequest: () -> Unit, modifier: Modifier = Modifier, properties: DialogProperties = DialogProperties(
        dismissOnBackPress = true, dismissOnClickOutside = true), title: @Composable (() -> Unit)? = null,
    text: @Composable (() -> Unit)? = null, confirmButton: @Composable () -> Unit, dismissButton: @Composable (() -> Unit)? = null,
    icon: @Composable (() -> Unit)? = null) {
    val containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
    AlertDialog(onDismissRequest = onDismissRequest, modifier = modifier, properties = properties, shape = RoundedCornerShape(24.dp),
        containerColor = containerColor, tonalElevation = 0.dp, icon = icon, title = title, text = text, confirmButton = confirmButton,
        dismissButton = dismissButton, iconContentColor = MaterialTheme.colorScheme.onSurface,
        titleContentColor = MaterialTheme.colorScheme.onSurface, textContentColor = MaterialTheme.colorScheme.onSurfaceVariant)
}
```

#### Qué hace y por qué existe

Composable que declara una parte de la interfaz. Su cuerpo transforma estado y parámetros en UI y callbacks, y puede reevaluarse durante recomposiciones.

#### Contrato de la declaración

**Parámetros:**

- `onDismissRequest: () -> Unit` — `onDismissRequest` recibe un valor de tipo `() -> Unit`. El contrato no marca este parámetro como anulable. Es un callback: el bloque no posee necesariamente la acción final, sino que notifica/delega al consumidor.
- `modifier: Modifier = Modifier` — `modifier` recibe un valor de tipo `Modifier`. El contrato no marca este parámetro como anulable. Tiene valor por defecto `Modifier`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada.
- `properties: DialogProperties = DialogProperties( dismissOnBackPress = true, dismissOnClickOutside = true)` — `properties` recibe un valor de tipo `DialogProperties`. El contrato no marca este parámetro como anulable. Tiene valor por defecto `DialogProperties( dismissOnBackPress = true, dismissOnClickOutside = true)`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada.
- `title: @Composable (() -> Unit)? = null` — `title` recibe un valor de tipo `@Composable (() -> Unit)?`. El contrato permite `null`; el bloque debe decidir explícita o implícitamente qué significa la ausencia. Tiene valor por defecto `null`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada. Es un callback: el bloque no posee necesariamente la acción final, sino que notifica/delega al consumidor.
- `text: @Composable (() -> Unit)? = null` — `text` recibe un valor de tipo `@Composable (() -> Unit)?`. El contrato permite `null`; el bloque debe decidir explícita o implícitamente qué significa la ausencia. Tiene valor por defecto `null`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada. Es un callback: el bloque no posee necesariamente la acción final, sino que notifica/delega al consumidor.
- `confirmButton: @Composable () -> Unit` — `confirmButton` recibe un valor de tipo `@Composable () -> Unit`. El contrato no marca este parámetro como anulable. Es un callback: el bloque no posee necesariamente la acción final, sino que notifica/delega al consumidor.
- `dismissButton: @Composable (() -> Unit)? = null` — `dismissButton` recibe un valor de tipo `@Composable (() -> Unit)?`. El contrato permite `null`; el bloque debe decidir explícita o implícitamente qué significa la ausencia. Tiene valor por defecto `null`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada. Es un callback: el bloque no posee necesariamente la acción final, sino que notifica/delega al consumidor.
- `icon: @Composable (() -> Unit)? = null` — `icon` recibe un valor de tipo `@Composable (() -> Unit)?`. El contrato permite `null`; el bloque debe decidir explícita o implícitamente qué significa la ausencia. Tiene valor por defecto `null`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada. Es un callback: el bloque no posee necesariamente la acción final, sino que notifica/delega al consumidor.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `@Composable` convierte la función en una unidad declarativa de Compose y la somete a reglas de recomposición.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 33 | `val containerColor` | `inferido` | `MaterialTheme.colorScheme.surfaceContainerHigh` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 29 | `fun AppAlertDialog(onDismissRequest: () -> Unit, modifier: Modifier = Modifier, properties: DialogProperties = DialogProperties(` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 30 | `dismissOnBackPress = true, dismissOnClickOutside = true), title: @Composable (() -> Unit)? = null,` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 31 | `text: @Composable (() -> Unit)? = null, confirmButton: @Composable () -> Unit, dismissButton: @Composable (() -> Unit)? = null,` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 32 | `icon: @Composable (() -> Unit)? = null) {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |

#### Semántica Compose/lifecycle

- **`@Composable`:** La función describe UI declarativa y puede ejecutarse nuevamente por recomposición; no debe interpretarse como una ejecución única imperativa.
- **`Modifier`:** La cadena de modificadores define tamaño, interacción, dibujo, semántica o posicionamiento; el orden de los modificadores puede ser significativo.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `DialogProperties`, `Composable`, `AlertDialog`, `RoundedCornerShape`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

## 5. Inventario global de propiedades/variables detectadas

| Línea | Identificador | Mutabilidad | Tipo | Explicación |
|---:|---|---|---|---|
| 21 | `borderColor` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 33 | `containerColor` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

## 6. Mapa exhaustivo de TODOS los bloques delimitados por `{}`

Esta tabla recorre todas las llaves estructurales detectadas fuera de strings/comentarios. Así se documentan también lambdas de Compose, callbacks, iteraciones y ámbitos locales que no tienen un nombre propio de función.

| Inicio–fin | Prof. | Cabecera/contexto | Qué significa ese bloque |
|---|---:|---|---|
| 20–26 | 0 | `focusable = false, dismissOnBackPress = true, dismissOnClickOutside = true), content: @Composable ColumnScope.() -> Unit)` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 32–38 | 0 | `icon: @Composable (() -> Unit)? = null)` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |

## 7. Lectura de seguridad antes de tocar este archivo

La documentación describe **lo que el código actual ya hace**. No constituye una propuesta de refactorización. Si se quisiera cambiar algo en el futuro, primero habría que preservar contratos públicos, claves de configuración, restricciones de API Android, comportamiento de Compose, lifecycle, operaciones de I/O y compatibilidad con los datos ya persistidos. En esta entrega no se hizo ninguna de esas modificaciones.

# ExtremeCustomizationSection.kt — documentación exhaustiva por bloques

**Ruta de código real:** `app/src/main/java/com/example/mynotes/ui/components/ExtremeCustomizationSection.kt`  
**SHA-256 del archivo ejecutable sin tocar:** `39f943741a422733881e2400505b398bbb1365c061f58dd13432304ccb23300e`  
**Líneas del código real:** 487

> **Garantía:** esta documentación se genera en una carpeta paralela. El archivo Kotlin/Kotlin DSL anterior no se modifica. Los fragmentos mostrados abajo son copias de lectura; la aplicación compila usando únicamente el archivo original de `app/...`.

## 1. Papel del archivo

Sección avanzada de Configuración que agrupa controles de personalización y rendimiento con selectores, sliders y opciones de animación.

**Arquitectura.** Descompone SettingsScreen en un componente especializado y reutiliza los estilos de dropdown, sliders, sonido/háptica y opciones de movimiento.

**Flujo general.** El flujo se describe declaración por declaración en este documento.

## 2. Package e imports

El `package` es `com.example.mynotes.ui.components`. Determina el namespace lógico del archivo, resolución de visibilidad `internal`/package-related tooling y la ruta conceptual desde la que otras clases lo importan.

El archivo declara **63 imports**. Cada import evita usar el nombre totalmente calificado en el cuerpo y, además, revela las dependencias técnicas del bloque:

**Android/plataforma:** `android.content.Intent`, `android.net.Uri`.

**Jetpack/Compose:** `androidx.activity.compose.rememberLauncherForActivityResult`, `androidx.activity.result.contract.ActivityResultContracts`, `androidx.compose.foundation.BorderStroke`, `androidx.compose.foundation.clickable`, `androidx.compose.foundation.layout.Arrangement`, `androidx.compose.foundation.layout.Box`, `androidx.compose.foundation.layout.Column`, `androidx.compose.foundation.layout.PaddingValues`, `androidx.compose.foundation.layout.Row`, `androidx.compose.foundation.layout.Spacer`, `androidx.compose.foundation.layout.fillMaxSize`, `androidx.compose.foundation.layout.fillMaxWidth`, `androidx.compose.foundation.layout.defaultMinSize`, `androidx.compose.foundation.layout.heightIn`, `androidx.compose.foundation.layout.width`, `androidx.compose.foundation.layout.widthIn`, `androidx.compose.foundation.layout.height`, `androidx.compose.foundation.layout.padding`, `androidx.compose.foundation.layout.size`, `androidx.compose.foundation.shape.CircleShape`, `androidx.compose.foundation.shape.RoundedCornerShape`, `androidx.compose.material.icons.Icons`, `androidx.compose.material.icons.filled.Person`, `androidx.compose.material.icons.filled.ExpandMore`, `androidx.compose.material3.DropdownMenuItem`, `androidx.compose.material3.Icon`, `androidx.compose.material3.MaterialTheme`, `androidx.compose.material3.Surface`, `androidx.compose.material3.Switch`, `androidx.compose.material3.Text`, `androidx.compose.material3.TextButton`, `androidx.compose.runtime.Composable`, `androidx.compose.runtime.Immutable`, `androidx.compose.runtime.LaunchedEffect`, `androidx.compose.runtime.mutableFloatStateOf`, `androidx.compose.runtime.mutableStateOf`, `androidx.compose.runtime.getValue`, `androidx.compose.runtime.remember`, `androidx.compose.runtime.setValue`, `androidx.compose.ui.Alignment`, `androidx.compose.ui.Modifier`, `androidx.compose.ui.draw.clip`, `androidx.compose.ui.graphics.Color`, `androidx.compose.ui.layout.ContentScale`, `androidx.compose.ui.platform.LocalContext`, `androidx.compose.ui.res.stringResource`, `androidx.compose.ui.text.font.FontFamily`, `androidx.compose.ui.text.style.TextAlign`, `androidx.compose.ui.text.font.FontWeight`, `androidx.compose.ui.unit.dp`, `androidx.compose.ui.unit.sp`, `androidx.compose.ui.window.PopupProperties`.

**Proyecto MyNotes:** `com.example.mynotes.R`, `com.example.mynotes.ui.components.AppDropdownMenu`, `com.example.mynotes.settings.AppSettings`, `com.example.mynotes.ui.sound.UiActionSound`, `com.example.mynotes.ui.sound.UiSound`, `com.example.mynotes.ui.sound.UiSoundPlayer`, `com.example.mynotes.ui.motion.ConfigurableAnimatedContent`.

**Kotlin/corrutinas/Java:** `kotlin.math.roundToInt`.

**Otras librerías:** `coil3.compose.AsyncImage`.

## 3. Restricciones e invariantes visibles en el archivo

- **Nulabilidad segura (3 aparición/apariciones):** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`.
- **Fallback nulo (6 aparición/apariciones):** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula.
- **Límite visual (2 aparición/apariciones):** La UI impone un límite explícito de tamaño o cantidad de líneas para impedir crecimiento visual no controlado.
- **Sin foco de popup (2 aparición/apariciones):** El popup se configura para no tomar el foco de ventana; en este proyecto ayuda a preservar el modo inmersivo y evita reaparición indeseada de la navegación Android.

## 4. Bloques de código, uno por uno

### 4.1 `IconStyleOption` — class, líneas 68–409

```kotlin
private data class IconStyleOption(val key: String, val labelRes: Int)

private val iconStyles = listOf(IconStyleOption("material", R.string.extreme_icon_material),
        IconStyleOption("rounded", R.string.extreme_icon_rounded), IconStyleOption("outlined", R.string.extreme_icon_outlined),
        IconStyleOption("minimal", R.string.extreme_icon_minimal))
@Immutable
private data class AccentOption(val key: String, val color: Color)

@Immutable
private data class MotionOption(val key: String, val labelRes: Int)

    // … el cuerpo completo permanece en el archivo real; sus miembros se documentan individualmente abajo …
}
```

#### Qué hace y por qué existe

Sección avanzada de Configuración que agrupa controles de personalización y rendimiento con selectores, sliders y opciones de animación.

#### Contrato de la declaración

**Parámetros del constructor/encabezado:**
- `val key: String` — `key` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable. Aunque el tipo sea `String`, semánticamente suele funcionar como clave/enumeración textual; las ramas del código delimitan los valores reconocidos. Además, al declararse con `val/var` en el constructor se convierte en propiedad de la instancia.
- `val labelRes: Int` — `labelRes` recibe un valor de tipo `Int`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora. Además, al declararse con `val/var` en el constructor se convierte en propiedad de la instancia.
**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 70 | `val iconStyles` | `inferido` | `listOf(IconStyleOption("material", R.string.extreme_icon_material),` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 79 | `val motionStyles` | `inferido` | `listOf(MotionOption("zoom", R.string.motion_style_zoom),` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 90 | `val motionEasings` | `inferido` | `listOf(MotionOption("standard", R.string.motion_easing_standard),` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 93 | `val performanceModes` | `inferido` | `listOf(MotionOption("performance", R.string.performance_mode_performance),` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Actúa como selector de estrategia/perfil; su valor determina qué rama de configuración se aplica. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 95 | `val accents` | `inferido` | `listOf(AccentOption("red", Color(0xFFE55757)), AccentOption("coral", Color(0xFFF27663)),` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 112 | `val context` | `inferido` | `LocalContext.current` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El `Context` da acceso a recursos/servicios/almacenamiento Android; debe respetarse el ciclo de vida del contexto recibido. |
| 113 | `var iconMenu` | `inferido` | `by remember {` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 116 | `var profileSize` | `inferido` | `by remember {` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 119 | `var iconSize` | `inferido` | `by remember {` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 122 | `var radius` | `inferido` | `by remember {` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 125 | `var elevation` | `inferido` | `by remember {` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 128 | `var padding` | `inferido` | `by remember {` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 131 | `var imageHeight` | `inferido` | `by remember {` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 134 | `var titleLines` | `inferido` | `by remember {` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 137 | `var contentLines` | `inferido` | `by remember {` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 140 | `var lineSpacing` | `inferido` | `by remember {` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 143 | `var fabSize` | `inferido` | `by remember {` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 146 | `var animationSpeed` | `inferido` | `by remember {` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 149 | `var animationIntensity` | `inferido` | `by remember {` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 152 | `var previewState` | `inferido` | `by remember {` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 191 | `val picker` | `inferido` | `rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 256 | `val iconMenuLongestLabel` | `inferido` | `iconStyles.maxOfOrNull { context.getString(it.labelRes).length } ?: 0` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 257 | `val iconMenuWidth` | `inferido` | `when {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. |
| 293 | `val accentItems` | `inferido` | `remember {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 300 | `val color` | `inferido` | `option?.color?: MaterialTheme.colorScheme.primary` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 301 | `val selected` | `inferido` | `settings.accentColor == key` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El nombre indica un estado de decisión/visibilidad/selección usado para controlar una rama de comportamiento o presentación. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 104 | `graphicColor: Color, onProfileImageUriChange: (String) -> Unit, onProfileImageSizeChange: (Float) -> Unit,` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 105 | `onIconStyleChange: (String) -> Unit, onIconSizeChange: (Float) -> Unit, onAccentColorChange: (String) -> Unit,` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 106 | `onNoteCardCornerRadiusChange: (Float) -> Unit, onNoteCardElevationChange: (Float) -> Unit, onNoteCardPaddingChange: (Float) -> Unit,` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 107 | `onNoteCardImageHeightChange: (Float) -> Unit, onNoteTitleMaxLinesChange: (Int) -> Unit, onNoteContentMaxLinesChange: (Int) -> Unit,` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 108 | `onNoteLineSpacingChange: (Float) -> Unit, onShowNoteDateChange: (Boolean) -> Unit, onShowCategoryChipChange: (Boolean) -> Unit,` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 109 | `onShowFavoriteIconChange: (Boolean) -> Unit, onFabSizeChange: (Float) -> Unit, onPerformanceModeChange: (String) -> Unit,` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 110 | `onAnimationsEnabledChange: (Boolean) -> Unit, onAnimationStyleChange: (String) -> Unit, onAnimationEasingChange: (String) -> Unit,` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 111 | `onAnimationSpeedChange: (Float) -> Unit, onAnimationIntensityChange: (Float) -> Unit) {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 155 | `LaunchedEffect(settings.profileImageSize) {` | Efecto de Compose basado en corrutina: se inicia cuando entra en composición y se reinicia si cambian sus claves; se cancela al salir de composición. |
| 158 | `LaunchedEffect(settings.iconSize) {` | Efecto de Compose basado en corrutina: se inicia cuando entra en composición y se reinicia si cambian sus claves; se cancela al salir de composición. |
| 161 | `LaunchedEffect(settings.noteCardCornerRadius) {` | Efecto de Compose basado en corrutina: se inicia cuando entra en composición y se reinicia si cambian sus claves; se cancela al salir de composición. |
| 164 | `LaunchedEffect(settings.noteCardElevation) {` | Efecto de Compose basado en corrutina: se inicia cuando entra en composición y se reinicia si cambian sus claves; se cancela al salir de composición. |
| 167 | `LaunchedEffect(settings.noteCardPadding) {` | Efecto de Compose basado en corrutina: se inicia cuando entra en composición y se reinicia si cambian sus claves; se cancela al salir de composición. |
| 170 | `LaunchedEffect(settings.noteCardImageHeight) {` | Efecto de Compose basado en corrutina: se inicia cuando entra en composición y se reinicia si cambian sus claves; se cancela al salir de composición. |
| 173 | `LaunchedEffect(settings.noteTitleMaxLines) {` | Efecto de Compose basado en corrutina: se inicia cuando entra en composición y se reinicia si cambian sus claves; se cancela al salir de composición. |
| 176 | `LaunchedEffect(settings.noteContentMaxLines) {` | Efecto de Compose basado en corrutina: se inicia cuando entra en composición y se reinicia si cambian sus claves; se cancela al salir de composición. |
| 179 | `LaunchedEffect(settings.noteLineSpacing) {` | Efecto de Compose basado en corrutina: se inicia cuando entra en composición y se reinicia si cambian sus claves; se cancela al salir de composición. |
| 182 | `LaunchedEffect(settings.fabSize) {` | Efecto de Compose basado en corrutina: se inicia cuando entra en composición y se reinicia si cambian sus claves; se cancela al salir de composición. |
| 185 | `LaunchedEffect(settings.animationSpeed) {` | Efecto de Compose basado en corrutina: se inicia cuando entra en composición y se reinicia si cambian sus claves; se cancela al salir de composición. |
| 188 | `LaunchedEffect(settings.animationIntensity) {` | Efecto de Compose basado en corrutina: se inicia cuando entra en composición y se reinicia si cambian sus claves; se cancela al salir de composición. |
| 192 | `uri ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 193 | `if (uri != null) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 194 | `try {` | Frontera de manejo de errores: agrupa operaciones que pueden lanzar excepciones para permitir recuperación o limpieza controlada. |
| 211 | `if (settings.profileImageUri.isNotBlank()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 228 | `if (settings.profileImageUri.isNotBlank()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 258 | `iconMenuLongestLabel <= 8 -> 118.dp` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 259 | `iconMenuLongestLabel <= 12 -> 138.dp` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 260 | `else -> 158.dp` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |
| 266 | `option -> DropdownMenuItem(modifier = Modifier.height(32.dp),` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 367 | `if (settings.animationsEnabled) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 3.
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 4.
- **Límite visual:** La UI impone un límite explícito de tamaño o cantidad de líneas para impedir crecimiento visual no controlado. Apariciones en este bloque: 1.
- **Sin foco de popup:** El popup se configura para no tomar el foco de ventana; en este proyecto ayuda a preservar el modo inmersivo y evita reaparición indeseada de la navegación Android. Apariciones en este bloque: 1.

#### Semántica Compose/lifecycle

- **`@Composable`:** La función describe UI declarativa y puede ejecutarse nuevamente por recomposición; no debe interpretarse como una ejecución única imperativa.
- **`remember`:** Conserva valores entre recomposiciones bajo las mismas claves.
- **`mutableStateOf`:** Crea estado observable; las lecturas registradas por Compose quedan enlazadas a sus cambios.
- **`LaunchedEffect`:** Ejecuta una corrutina ligada al ciclo de vida de la composición y a sus claves.
- **Callbacks `onClick`:** La lógica asociada se ejecuta por evento de usuario, no durante la composición inicial.
- **`Modifier`:** La cadena de modificadores define tamaño, interacción, dibujo, semántica o posicionamiento; el orden de los modificadores puede ser significativo.

#### Efectos secundarios y recursos

- **Estado Compose:** Declara o utiliza estado observable de Compose; una escritura en ese estado puede desencadenar recomposición de los lectores.
- **Sonido/háptica:** Produce feedback perceptible para el usuario; debe ejecutarse en respuesta a la interacción/estado correspondiente.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `listOf`, `IconStyleOption`, `MotionOption`, `AccentOption`, `Color`, `mutableStateOf`, `mutableFloatStateOf`, `settings.noteTitleMaxLines.toFloat`, `settings.noteContentMaxLines.toFloat`, `LaunchedEffect`, `rememberLauncherForActivityResult`, `ActivityResultContracts.OpenDocument`, `context.contentResolver.takePersistableUriPermission`, `UiSoundPlayer.play`, `onProfileImageUriChange`, `uri.toString`, `Text`, `stringResource`, `Spacer`, `Modifier.height`, `SettingsSectionPanel`, `PaddingValues`, `Row`, `Surface`, `Modifier.size`, `settings.profileImageUri.isNotBlank`, `AsyncImage`, `Uri.parse`, `Modifier.fillMaxWidth`, `clip`, `Box`, `Icon`, `Column`, `Modifier.padding`, `TextButton`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.
- Conservar `focusable = false` en estos popups si se quiere mantener el comportamiento inmersivo que evita que reaparezcan los botones de navegación del sistema.
- No convertir estado de Compose en variables ordinarias sin considerar recomposición y persistencia temporal.
- Preservar la normalización y correspondencia de `performanceMode`, porque otras capas usan las mismas claves para rendimiento/calidad.

### 4.2 `AccentOption` — class, líneas 74–409

```kotlin
private data class AccentOption(val key: String, val color: Color)

@Immutable
private data class MotionOption(val key: String, val labelRes: Int)

private val motionStyles = listOf(MotionOption("zoom", R.string.motion_style_zoom),
        MotionOption("zoom_fade", R.string.motion_style_zoom_fade), MotionOption("fade", R.string.motion_style_fade),
        MotionOption("slide_left", R.string.motion_style_slide_left), MotionOption("slide_right", R.string.motion_style_slide_right),
        MotionOption("slide_up", R.string.motion_style_slide_up), MotionOption("slide_down", R.string.motion_style_slide_down),
        MotionOption("slide_zoom_left", R.string.motion_style_slide_zoom_left),
        MotionOption("slide_zoom_up", R.string.motion_style_slide_zoom_up), MotionOption("axis_x", R.string.motion_style_axis_x),
    // … el cuerpo completo permanece en el archivo real; sus miembros se documentan individualmente abajo …
}
```

#### Qué hace y por qué existe

Sección avanzada de Configuración que agrupa controles de personalización y rendimiento con selectores, sliders y opciones de animación.

#### Contrato de la declaración

**Parámetros del constructor/encabezado:**
- `val key: String` — `key` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable. Aunque el tipo sea `String`, semánticamente suele funcionar como clave/enumeración textual; las ramas del código delimitan los valores reconocidos. Además, al declararse con `val/var` en el constructor se convierte en propiedad de la instancia.
- `val color: Color` — `color` recibe un valor de tipo `Color`. El contrato no marca este parámetro como anulable. Además, al declararse con `val/var` en el constructor se convierte en propiedad de la instancia.
**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 79 | `val motionStyles` | `inferido` | `listOf(MotionOption("zoom", R.string.motion_style_zoom),` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 90 | `val motionEasings` | `inferido` | `listOf(MotionOption("standard", R.string.motion_easing_standard),` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 93 | `val performanceModes` | `inferido` | `listOf(MotionOption("performance", R.string.performance_mode_performance),` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Actúa como selector de estrategia/perfil; su valor determina qué rama de configuración se aplica. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 95 | `val accents` | `inferido` | `listOf(AccentOption("red", Color(0xFFE55757)), AccentOption("coral", Color(0xFFF27663)),` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 112 | `val context` | `inferido` | `LocalContext.current` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El `Context` da acceso a recursos/servicios/almacenamiento Android; debe respetarse el ciclo de vida del contexto recibido. |
| 113 | `var iconMenu` | `inferido` | `by remember {` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 116 | `var profileSize` | `inferido` | `by remember {` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 119 | `var iconSize` | `inferido` | `by remember {` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 122 | `var radius` | `inferido` | `by remember {` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 125 | `var elevation` | `inferido` | `by remember {` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 128 | `var padding` | `inferido` | `by remember {` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 131 | `var imageHeight` | `inferido` | `by remember {` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 134 | `var titleLines` | `inferido` | `by remember {` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 137 | `var contentLines` | `inferido` | `by remember {` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 140 | `var lineSpacing` | `inferido` | `by remember {` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 143 | `var fabSize` | `inferido` | `by remember {` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 146 | `var animationSpeed` | `inferido` | `by remember {` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 149 | `var animationIntensity` | `inferido` | `by remember {` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 152 | `var previewState` | `inferido` | `by remember {` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 191 | `val picker` | `inferido` | `rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 256 | `val iconMenuLongestLabel` | `inferido` | `iconStyles.maxOfOrNull { context.getString(it.labelRes).length } ?: 0` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 257 | `val iconMenuWidth` | `inferido` | `when {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. |
| 293 | `val accentItems` | `inferido` | `remember {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 300 | `val color` | `inferido` | `option?.color?: MaterialTheme.colorScheme.primary` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 301 | `val selected` | `inferido` | `settings.accentColor == key` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El nombre indica un estado de decisión/visibilidad/selección usado para controlar una rama de comportamiento o presentación. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 104 | `graphicColor: Color, onProfileImageUriChange: (String) -> Unit, onProfileImageSizeChange: (Float) -> Unit,` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 105 | `onIconStyleChange: (String) -> Unit, onIconSizeChange: (Float) -> Unit, onAccentColorChange: (String) -> Unit,` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 106 | `onNoteCardCornerRadiusChange: (Float) -> Unit, onNoteCardElevationChange: (Float) -> Unit, onNoteCardPaddingChange: (Float) -> Unit,` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 107 | `onNoteCardImageHeightChange: (Float) -> Unit, onNoteTitleMaxLinesChange: (Int) -> Unit, onNoteContentMaxLinesChange: (Int) -> Unit,` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 108 | `onNoteLineSpacingChange: (Float) -> Unit, onShowNoteDateChange: (Boolean) -> Unit, onShowCategoryChipChange: (Boolean) -> Unit,` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 109 | `onShowFavoriteIconChange: (Boolean) -> Unit, onFabSizeChange: (Float) -> Unit, onPerformanceModeChange: (String) -> Unit,` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 110 | `onAnimationsEnabledChange: (Boolean) -> Unit, onAnimationStyleChange: (String) -> Unit, onAnimationEasingChange: (String) -> Unit,` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 111 | `onAnimationSpeedChange: (Float) -> Unit, onAnimationIntensityChange: (Float) -> Unit) {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 155 | `LaunchedEffect(settings.profileImageSize) {` | Efecto de Compose basado en corrutina: se inicia cuando entra en composición y se reinicia si cambian sus claves; se cancela al salir de composición. |
| 158 | `LaunchedEffect(settings.iconSize) {` | Efecto de Compose basado en corrutina: se inicia cuando entra en composición y se reinicia si cambian sus claves; se cancela al salir de composición. |
| 161 | `LaunchedEffect(settings.noteCardCornerRadius) {` | Efecto de Compose basado en corrutina: se inicia cuando entra en composición y se reinicia si cambian sus claves; se cancela al salir de composición. |
| 164 | `LaunchedEffect(settings.noteCardElevation) {` | Efecto de Compose basado en corrutina: se inicia cuando entra en composición y se reinicia si cambian sus claves; se cancela al salir de composición. |
| 167 | `LaunchedEffect(settings.noteCardPadding) {` | Efecto de Compose basado en corrutina: se inicia cuando entra en composición y se reinicia si cambian sus claves; se cancela al salir de composición. |
| 170 | `LaunchedEffect(settings.noteCardImageHeight) {` | Efecto de Compose basado en corrutina: se inicia cuando entra en composición y se reinicia si cambian sus claves; se cancela al salir de composición. |
| 173 | `LaunchedEffect(settings.noteTitleMaxLines) {` | Efecto de Compose basado en corrutina: se inicia cuando entra en composición y se reinicia si cambian sus claves; se cancela al salir de composición. |
| 176 | `LaunchedEffect(settings.noteContentMaxLines) {` | Efecto de Compose basado en corrutina: se inicia cuando entra en composición y se reinicia si cambian sus claves; se cancela al salir de composición. |
| 179 | `LaunchedEffect(settings.noteLineSpacing) {` | Efecto de Compose basado en corrutina: se inicia cuando entra en composición y se reinicia si cambian sus claves; se cancela al salir de composición. |
| 182 | `LaunchedEffect(settings.fabSize) {` | Efecto de Compose basado en corrutina: se inicia cuando entra en composición y se reinicia si cambian sus claves; se cancela al salir de composición. |
| 185 | `LaunchedEffect(settings.animationSpeed) {` | Efecto de Compose basado en corrutina: se inicia cuando entra en composición y se reinicia si cambian sus claves; se cancela al salir de composición. |
| 188 | `LaunchedEffect(settings.animationIntensity) {` | Efecto de Compose basado en corrutina: se inicia cuando entra en composición y se reinicia si cambian sus claves; se cancela al salir de composición. |
| 192 | `uri ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 193 | `if (uri != null) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 194 | `try {` | Frontera de manejo de errores: agrupa operaciones que pueden lanzar excepciones para permitir recuperación o limpieza controlada. |
| 211 | `if (settings.profileImageUri.isNotBlank()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 228 | `if (settings.profileImageUri.isNotBlank()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 258 | `iconMenuLongestLabel <= 8 -> 118.dp` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 259 | `iconMenuLongestLabel <= 12 -> 138.dp` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 260 | `else -> 158.dp` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |
| 266 | `option -> DropdownMenuItem(modifier = Modifier.height(32.dp),` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 367 | `if (settings.animationsEnabled) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 3.
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 4.
- **Límite visual:** La UI impone un límite explícito de tamaño o cantidad de líneas para impedir crecimiento visual no controlado. Apariciones en este bloque: 1.
- **Sin foco de popup:** El popup se configura para no tomar el foco de ventana; en este proyecto ayuda a preservar el modo inmersivo y evita reaparición indeseada de la navegación Android. Apariciones en este bloque: 1.

#### Semántica Compose/lifecycle

- **`@Composable`:** La función describe UI declarativa y puede ejecutarse nuevamente por recomposición; no debe interpretarse como una ejecución única imperativa.
- **`remember`:** Conserva valores entre recomposiciones bajo las mismas claves.
- **`mutableStateOf`:** Crea estado observable; las lecturas registradas por Compose quedan enlazadas a sus cambios.
- **`LaunchedEffect`:** Ejecuta una corrutina ligada al ciclo de vida de la composición y a sus claves.
- **Callbacks `onClick`:** La lógica asociada se ejecuta por evento de usuario, no durante la composición inicial.
- **`Modifier`:** La cadena de modificadores define tamaño, interacción, dibujo, semántica o posicionamiento; el orden de los modificadores puede ser significativo.

#### Efectos secundarios y recursos

- **Estado Compose:** Declara o utiliza estado observable de Compose; una escritura en ese estado puede desencadenar recomposición de los lectores.
- **Sonido/háptica:** Produce feedback perceptible para el usuario; debe ejecutarse en respuesta a la interacción/estado correspondiente.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `listOf`, `MotionOption`, `AccentOption`, `Color`, `mutableStateOf`, `mutableFloatStateOf`, `settings.noteTitleMaxLines.toFloat`, `settings.noteContentMaxLines.toFloat`, `LaunchedEffect`, `rememberLauncherForActivityResult`, `ActivityResultContracts.OpenDocument`, `context.contentResolver.takePersistableUriPermission`, `UiSoundPlayer.play`, `onProfileImageUriChange`, `uri.toString`, `Text`, `stringResource`, `Spacer`, `Modifier.height`, `SettingsSectionPanel`, `PaddingValues`, `Row`, `Surface`, `Modifier.size`, `settings.profileImageUri.isNotBlank`, `AsyncImage`, `Uri.parse`, `Modifier.fillMaxWidth`, `clip`, `Box`, `Icon`, `Column`, `Modifier.padding`, `TextButton`, `UiSoundPlayer.playAction`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.
- Conservar `focusable = false` en estos popups si se quiere mantener el comportamiento inmersivo que evita que reaparezcan los botones de navegación del sistema.
- No convertir estado de Compose en variables ordinarias sin considerar recomposición y persistencia temporal.
- Preservar la normalización y correspondencia de `performanceMode`, porque otras capas usan las mismas claves para rendimiento/calidad.

### 4.3 `MotionOption` — class, líneas 77–409

```kotlin
private data class MotionOption(val key: String, val labelRes: Int)

private val motionStyles = listOf(MotionOption("zoom", R.string.motion_style_zoom),
        MotionOption("zoom_fade", R.string.motion_style_zoom_fade), MotionOption("fade", R.string.motion_style_fade),
        MotionOption("slide_left", R.string.motion_style_slide_left), MotionOption("slide_right", R.string.motion_style_slide_right),
        MotionOption("slide_up", R.string.motion_style_slide_up), MotionOption("slide_down", R.string.motion_style_slide_down),
        MotionOption("slide_zoom_left", R.string.motion_style_slide_zoom_left),
        MotionOption("slide_zoom_up", R.string.motion_style_slide_zoom_up), MotionOption("axis_x", R.string.motion_style_axis_x),
        MotionOption("axis_y", R.string.motion_style_axis_y), MotionOption("axis_z", R.string.motion_style_axis_z),
        MotionOption("expand", R.string.motion_style_expand), MotionOption("expand_horizontal", R.string.motion_style_expand_horizontal),
        MotionOption("expand_vertical", R.string.motion_style_expand_vertical), MotionOption("bounce", R.string.motion_style_bounce),
    // … el cuerpo completo permanece en el archivo real; sus miembros se documentan individualmente abajo …
}
```

#### Qué hace y por qué existe

Sección avanzada de Configuración que agrupa controles de personalización y rendimiento con selectores, sliders y opciones de animación.

#### Contrato de la declaración

**Parámetros del constructor/encabezado:**
- `val key: String` — `key` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable. Aunque el tipo sea `String`, semánticamente suele funcionar como clave/enumeración textual; las ramas del código delimitan los valores reconocidos. Además, al declararse con `val/var` en el constructor se convierte en propiedad de la instancia.
- `val labelRes: Int` — `labelRes` recibe un valor de tipo `Int`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora. Además, al declararse con `val/var` en el constructor se convierte en propiedad de la instancia.
**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 79 | `val motionStyles` | `inferido` | `listOf(MotionOption("zoom", R.string.motion_style_zoom),` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 90 | `val motionEasings` | `inferido` | `listOf(MotionOption("standard", R.string.motion_easing_standard),` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 93 | `val performanceModes` | `inferido` | `listOf(MotionOption("performance", R.string.performance_mode_performance),` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Actúa como selector de estrategia/perfil; su valor determina qué rama de configuración se aplica. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 95 | `val accents` | `inferido` | `listOf(AccentOption("red", Color(0xFFE55757)), AccentOption("coral", Color(0xFFF27663)),` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 112 | `val context` | `inferido` | `LocalContext.current` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El `Context` da acceso a recursos/servicios/almacenamiento Android; debe respetarse el ciclo de vida del contexto recibido. |
| 113 | `var iconMenu` | `inferido` | `by remember {` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 116 | `var profileSize` | `inferido` | `by remember {` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 119 | `var iconSize` | `inferido` | `by remember {` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 122 | `var radius` | `inferido` | `by remember {` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 125 | `var elevation` | `inferido` | `by remember {` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 128 | `var padding` | `inferido` | `by remember {` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 131 | `var imageHeight` | `inferido` | `by remember {` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 134 | `var titleLines` | `inferido` | `by remember {` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 137 | `var contentLines` | `inferido` | `by remember {` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 140 | `var lineSpacing` | `inferido` | `by remember {` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 143 | `var fabSize` | `inferido` | `by remember {` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 146 | `var animationSpeed` | `inferido` | `by remember {` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 149 | `var animationIntensity` | `inferido` | `by remember {` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 152 | `var previewState` | `inferido` | `by remember {` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 191 | `val picker` | `inferido` | `rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 256 | `val iconMenuLongestLabel` | `inferido` | `iconStyles.maxOfOrNull { context.getString(it.labelRes).length } ?: 0` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 257 | `val iconMenuWidth` | `inferido` | `when {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. |
| 293 | `val accentItems` | `inferido` | `remember {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 300 | `val color` | `inferido` | `option?.color?: MaterialTheme.colorScheme.primary` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 301 | `val selected` | `inferido` | `settings.accentColor == key` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El nombre indica un estado de decisión/visibilidad/selección usado para controlar una rama de comportamiento o presentación. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 104 | `graphicColor: Color, onProfileImageUriChange: (String) -> Unit, onProfileImageSizeChange: (Float) -> Unit,` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 105 | `onIconStyleChange: (String) -> Unit, onIconSizeChange: (Float) -> Unit, onAccentColorChange: (String) -> Unit,` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 106 | `onNoteCardCornerRadiusChange: (Float) -> Unit, onNoteCardElevationChange: (Float) -> Unit, onNoteCardPaddingChange: (Float) -> Unit,` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 107 | `onNoteCardImageHeightChange: (Float) -> Unit, onNoteTitleMaxLinesChange: (Int) -> Unit, onNoteContentMaxLinesChange: (Int) -> Unit,` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 108 | `onNoteLineSpacingChange: (Float) -> Unit, onShowNoteDateChange: (Boolean) -> Unit, onShowCategoryChipChange: (Boolean) -> Unit,` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 109 | `onShowFavoriteIconChange: (Boolean) -> Unit, onFabSizeChange: (Float) -> Unit, onPerformanceModeChange: (String) -> Unit,` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 110 | `onAnimationsEnabledChange: (Boolean) -> Unit, onAnimationStyleChange: (String) -> Unit, onAnimationEasingChange: (String) -> Unit,` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 111 | `onAnimationSpeedChange: (Float) -> Unit, onAnimationIntensityChange: (Float) -> Unit) {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 155 | `LaunchedEffect(settings.profileImageSize) {` | Efecto de Compose basado en corrutina: se inicia cuando entra en composición y se reinicia si cambian sus claves; se cancela al salir de composición. |
| 158 | `LaunchedEffect(settings.iconSize) {` | Efecto de Compose basado en corrutina: se inicia cuando entra en composición y se reinicia si cambian sus claves; se cancela al salir de composición. |
| 161 | `LaunchedEffect(settings.noteCardCornerRadius) {` | Efecto de Compose basado en corrutina: se inicia cuando entra en composición y se reinicia si cambian sus claves; se cancela al salir de composición. |
| 164 | `LaunchedEffect(settings.noteCardElevation) {` | Efecto de Compose basado en corrutina: se inicia cuando entra en composición y se reinicia si cambian sus claves; se cancela al salir de composición. |
| 167 | `LaunchedEffect(settings.noteCardPadding) {` | Efecto de Compose basado en corrutina: se inicia cuando entra en composición y se reinicia si cambian sus claves; se cancela al salir de composición. |
| 170 | `LaunchedEffect(settings.noteCardImageHeight) {` | Efecto de Compose basado en corrutina: se inicia cuando entra en composición y se reinicia si cambian sus claves; se cancela al salir de composición. |
| 173 | `LaunchedEffect(settings.noteTitleMaxLines) {` | Efecto de Compose basado en corrutina: se inicia cuando entra en composición y se reinicia si cambian sus claves; se cancela al salir de composición. |
| 176 | `LaunchedEffect(settings.noteContentMaxLines) {` | Efecto de Compose basado en corrutina: se inicia cuando entra en composición y se reinicia si cambian sus claves; se cancela al salir de composición. |
| 179 | `LaunchedEffect(settings.noteLineSpacing) {` | Efecto de Compose basado en corrutina: se inicia cuando entra en composición y se reinicia si cambian sus claves; se cancela al salir de composición. |
| 182 | `LaunchedEffect(settings.fabSize) {` | Efecto de Compose basado en corrutina: se inicia cuando entra en composición y se reinicia si cambian sus claves; se cancela al salir de composición. |
| 185 | `LaunchedEffect(settings.animationSpeed) {` | Efecto de Compose basado en corrutina: se inicia cuando entra en composición y se reinicia si cambian sus claves; se cancela al salir de composición. |
| 188 | `LaunchedEffect(settings.animationIntensity) {` | Efecto de Compose basado en corrutina: se inicia cuando entra en composición y se reinicia si cambian sus claves; se cancela al salir de composición. |
| 192 | `uri ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 193 | `if (uri != null) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 194 | `try {` | Frontera de manejo de errores: agrupa operaciones que pueden lanzar excepciones para permitir recuperación o limpieza controlada. |
| 211 | `if (settings.profileImageUri.isNotBlank()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 228 | `if (settings.profileImageUri.isNotBlank()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 258 | `iconMenuLongestLabel <= 8 -> 118.dp` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 259 | `iconMenuLongestLabel <= 12 -> 138.dp` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 260 | `else -> 158.dp` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |
| 266 | `option -> DropdownMenuItem(modifier = Modifier.height(32.dp),` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 367 | `if (settings.animationsEnabled) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 3.
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 4.
- **Límite visual:** La UI impone un límite explícito de tamaño o cantidad de líneas para impedir crecimiento visual no controlado. Apariciones en este bloque: 1.
- **Sin foco de popup:** El popup se configura para no tomar el foco de ventana; en este proyecto ayuda a preservar el modo inmersivo y evita reaparición indeseada de la navegación Android. Apariciones en este bloque: 1.

#### Semántica Compose/lifecycle

- **`@Composable`:** La función describe UI declarativa y puede ejecutarse nuevamente por recomposición; no debe interpretarse como una ejecución única imperativa.
- **`remember`:** Conserva valores entre recomposiciones bajo las mismas claves.
- **`mutableStateOf`:** Crea estado observable; las lecturas registradas por Compose quedan enlazadas a sus cambios.
- **`LaunchedEffect`:** Ejecuta una corrutina ligada al ciclo de vida de la composición y a sus claves.
- **Callbacks `onClick`:** La lógica asociada se ejecuta por evento de usuario, no durante la composición inicial.
- **`Modifier`:** La cadena de modificadores define tamaño, interacción, dibujo, semántica o posicionamiento; el orden de los modificadores puede ser significativo.

#### Efectos secundarios y recursos

- **Estado Compose:** Declara o utiliza estado observable de Compose; una escritura en ese estado puede desencadenar recomposición de los lectores.
- **Sonido/háptica:** Produce feedback perceptible para el usuario; debe ejecutarse en respuesta a la interacción/estado correspondiente.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `listOf`, `MotionOption`, `AccentOption`, `Color`, `mutableStateOf`, `mutableFloatStateOf`, `settings.noteTitleMaxLines.toFloat`, `settings.noteContentMaxLines.toFloat`, `LaunchedEffect`, `rememberLauncherForActivityResult`, `ActivityResultContracts.OpenDocument`, `context.contentResolver.takePersistableUriPermission`, `UiSoundPlayer.play`, `onProfileImageUriChange`, `uri.toString`, `Text`, `stringResource`, `Spacer`, `Modifier.height`, `SettingsSectionPanel`, `PaddingValues`, `Row`, `Surface`, `Modifier.size`, `settings.profileImageUri.isNotBlank`, `AsyncImage`, `Uri.parse`, `Modifier.fillMaxWidth`, `clip`, `Box`, `Icon`, `Column`, `Modifier.padding`, `TextButton`, `UiSoundPlayer.playAction`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.
- Conservar `focusable = false` en estos popups si se quiere mantener el comportamiento inmersivo que evita que reaparezcan los botones de navegación del sistema.
- No convertir estado de Compose en variables ordinarias sin considerar recomposición y persistencia temporal.
- Preservar la normalización y correspondencia de `performanceMode`, porque otras capas usan las mismas claves para rendimiento/calidad.

### 4.4 `ExtremeCustomizationSection` — fun, líneas 103–409

```kotlin
fun ExtremeCustomizationSection(settings: AppSettings, fontFamily: FontFamily, textColor: Color, secondaryTextColor: Color,
    graphicColor: Color, onProfileImageUriChange: (String) -> Unit, onProfileImageSizeChange: (Float) -> Unit,
    onIconStyleChange: (String) -> Unit, onIconSizeChange: (Float) -> Unit, onAccentColorChange: (String) -> Unit,
    onNoteCardCornerRadiusChange: (Float) -> Unit, onNoteCardElevationChange: (Float) -> Unit, onNoteCardPaddingChange: (Float) -> Unit,
    onNoteCardImageHeightChange: (Float) -> Unit, onNoteTitleMaxLinesChange: (Int) -> Unit, onNoteContentMaxLinesChange: (Int) -> Unit,
    onNoteLineSpacingChange: (Float) -> Unit, onShowNoteDateChange: (Boolean) -> Unit, onShowCategoryChipChange: (Boolean) -> Unit,
    onShowFavoriteIconChange: (Boolean) -> Unit, onFabSizeChange: (Float) -> Unit, onPerformanceModeChange: (String) -> Unit,
    onAnimationsEnabledChange: (Boolean) -> Unit, onAnimationStyleChange: (String) -> Unit, onAnimationEasingChange: (String) -> Unit,
    onAnimationSpeedChange: (Float) -> Unit, onAnimationIntensityChange: (Float) -> Unit) {
    val context = LocalContext.current
    var iconMenu by remember {
        mutableStateOf(false)
    }
    var profileSize by remember {
        mutableFloatStateOf(settings.profileImageSize)
    }
    var iconSize by remember {
        mutableFloatStateOf(settings.iconSize)
    }
    var radius by remember {
        mutableFloatStateOf(settings.noteCardCornerRadius)
    }
    var elevation by remember {
        mutableFloatStateOf(settings.noteCardElevation)
    }
    var padding by remember {
        mutableFloatStateOf(settings.noteCardPadding)
    }
    var imageHeight by remember {
        mutableFloatStateOf(settings.noteCardImageHeight)
    }
    var titleLines by remember {
        mutableFloatStateOf(settings.noteTitleMaxLines.toFloat())
    }
    var contentLines by remember {
        mutableFloatStateOf(settings.noteContentMaxLines.toFloat())
    }
    var lineSpacing by remember {
        mutableFloatStateOf(settings.noteLineSpacing)
    }
    var fabSize by remember {
        mutableFloatStateOf(settings.fabSize)
    }
    var animationSpeed by remember {
        mutableFloatStateOf(settings.animationSpeed)
    }
    var animationIntensity by remember {
        mutableFloatStateOf(settings.animationIntensity)
    }
    var previewState by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(settings.profileImageSize) {
        profileSize = settings.profileImageSize
    }
    LaunchedEffect(settings.iconSize) {
        iconSize = settings.iconSize
    }
    LaunchedEffect(settings.noteCardCornerRadius) {
        radius = settings.noteCardCornerRadius
    }
    LaunchedEffect(settings.noteCardElevation) {
        elevation = settings.noteCardElevation
    }
    LaunchedEffect(settings.noteCardPadding) {
        padding = settings.noteCardPadding
    }
    LaunchedEffect(settings.noteCardImageHeight) {
        imageHeight = settings.noteCardImageHeight
    }
    LaunchedEffect(settings.noteTitleMaxLines) {
        titleLines = settings.noteTitleMaxLines.toFloat()
    }
    LaunchedEffect(settings.noteContentMaxLines) {
        contentLines = settings.noteContentMaxLines.toFloat()
    }
    LaunchedEffect(settings.noteLineSpacing) {
        lineSpacing = settings.noteLineSpacing
    }
    LaunchedEffect(settings.fabSize) {
        fabSize = settings.fabSize
    }
    LaunchedEffect(settings.animationSpeed) {
        animationSpeed = settings.animationSpeed
    }
    LaunchedEffect(settings.animationIntensity) {
        animationIntensity = settings.animationIntensity
    }
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) {
                uri ->
            if (uri != null) {
                try {
                    context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                } catch (_: SecurityException) {
                }
                UiSoundPlayer.play(context = context, sound = UiSound.Attachment)
                onProfileImageUriChange(uri.toString())
            }
        }
    Text(text = stringResource(R.string.extreme_personalization), color = textColor, fontFamily = fontFamily, fontWeight = FontWeight.Bold,
        fontSize = 20.sp)
    Spacer(Modifier.height(10.dp))
    SettingsSectionPanel(textColorMode = settings.textColor, contentPadding = PaddingValues(14.dp)) { panelColors ->
        Text(text = stringResource(R.string.extreme_profile), color = panelColors.text, fontFamily = fontFamily,
            fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(modifier = Modifier.size(74.dp), shape = CircleShape, color = MaterialTheme.colorScheme.surfaceContainerHigh) {
                if (settings.profileImageUri.isNotBlank()) {
                    AsyncImage(model = Uri.parse(settings.profileImageUri), contentDescription = null,
                        modifier = Modifier.fillMaxWidth().clip(CircleShape), contentScale = ContentScale.Crop)
                } else {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(imageVector = Icons.Default.Person, contentDescription = null, modifier = Modifier.size(34.dp),
                            tint = panelColors.text)
                    }
                }
            }
            Column(modifier = Modifier.padding(start = 12.dp)) {
                TextButton(onClick = {
                        UiSoundPlayer.playAction(context = context, action = UiActionSound.Add)
                        picker.launch(arrayOf("image/*"))
                    }) {
                    Text(text = stringResource(R.string.extreme_change_photo), color = panelColors.text, fontFamily = fontFamily)
                }
                if (settings.profileImageUri.isNotBlank()) {
                    TextButton(onClick = {
                            UiSoundPlayer.play(context = context, sound = UiSound.Delete)
                            onProfileImageUriChange("")
                        }) {
                        Text(text = stringResource(R.string.extreme_remove_photo), color = panelColors.text, fontFamily = fontFamily)
                    }
                }
            }
        }
        CustomSlider(title = stringResource(R.string.extreme_profile_size), label = "${profileSize.roundToInt()} dp", value = profileSize,
            onValueChange = { profileSize = it }, onFinished = {
                onProfileImageSizeChange(profileSize)
            }, range = 36f..84f, steps = 23, settings = settings, fontFamily = fontFamily, textColor = panelColors.text)
    }
    Spacer(Modifier.height(12.dp))
    SettingsSectionPanel(textColorMode = settings.textColor, contentPadding = PaddingValues(14.dp)) { panelColors ->
        Text(text = stringResource(R.string.extreme_icons), color = panelColors.text, fontFamily = fontFamily, fontWeight = FontWeight.Bold
        )
        Box(modifier = Modifier.fillMaxWidth()) {
            TextButton(onClick = {
                    UiSoundPlayer.playAction(context = context, action = UiActionSound.Menu)
                    iconMenu = true
                }) {
                Text(text = stringResource(iconStyles.firstOrNull {
                            it.key == settings.iconStyle
                        }?.labelRes ?: R.string.extreme_icon_rounded), color = panelColors.text, fontFamily = fontFamily)
            }
            val iconMenuLongestLabel = iconStyles.maxOfOrNull { context.getString(it.labelRes).length } ?: 0
            val iconMenuWidth = when {
                    iconMenuLongestLabel <= 8 -> 118.dp
                    iconMenuLongestLabel <= 12 -> 138.dp
                    else -> 158.dp
                }
            AppDropdownMenu(modifier = Modifier.width(iconMenuWidth), expanded = iconMenu, onDismissRequest = {
                    iconMenu = false
                }, properties = PopupProperties(focusable = false, dismissOnBackPress = true, dismissOnClickOutside = true)) {
                iconStyles.forEach {
                        option -> DropdownMenuItem(modifier = Modifier.height(32.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp), text = {
                            Text(text = stringResource(option.labelRes), modifier = Modifier.fillMaxWidth(), color = panelColors.text,
                                fontFamily = fontFamily, fontSize = 12.sp, maxLines = 1, textAlign = TextAlign.Center)
                        }, onClick = {
                            UiSoundPlayer.playAction(context = context, action = UiActionSound.Select)
                            iconMenu = false
                            onIconStyleChange(option.key)
                        })
                }
            }
        }
        CustomSlider(title = stringResource(R.string.extreme_icon_size), label = "${iconSize.roundToInt()} dp", value = iconSize,
            onValueChange = { iconSize = it }, onFinished = {
                onIconSizeChange(iconSize)
            }, range = 16f..36f, steps = 19, settings = settings, fontFamily = fontFamily, textColor = panelColors.text)
    }
    Spacer(Modifier.height(12.dp))
    SettingsSectionPanel(textColorMode = settings.textColor, contentPadding = PaddingValues(14.dp)) { panelColors ->
        Text(text = stringResource(R.string.extreme_accent), color = panelColors.text, fontFamily = fontFamily, fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(10.dp))
        /*
         * La paleta se reparte en 4 filas de 5 círculos.
         * Así ningún color queda cortado en pantallas estrechas y todos
         * conservan exactamente el mismo tamaño y separación visual.
         */
        val accentItems = remember {
                listOf<AccentOption?>(null) + accents
            }
        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            accentItems.chunked(5).forEach { rowItems -> Row(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        rowItems.forEach { option -> val key = option?.key ?: "palette"
                            val color = option?.color?: MaterialTheme.colorScheme.primary
                            val selected = settings.accentColor == key
                            Surface(modifier = Modifier.size(48.dp).clickable {
                                        UiSoundPlayer.playAction(context = context, action = UiActionSound.Color)
                                        onAccentColorChange(key)
                                    }, shape = CircleShape, border = BorderStroke(width = if (selected) 3.dp else 1.dp,
                                    color = if (selected) {
                                        panelColors.text
                                    } else {
                                        panelColors.graphic
                                    }), color = color, shadowElevation = if (selected) 2.dp else 0.dp) {}
                        }
                        /*
                         * Solo se usa si en el futuro cambia el número de
                         * colores; mantiene la cuadrícula alineada.
                         */
                        repeat(5 - rowItems.size) {
                            Spacer(Modifier.size(48.dp))
                        }
                    }
                }
        }
    }
    Spacer(Modifier.height(12.dp))
    SettingsSectionPanel(textColorMode = settings.textColor, contentPadding = PaddingValues(14.dp)) { panelColors ->
        Text(text = stringResource(R.string.extreme_note_cards), color = panelColors.text, fontFamily = fontFamily,
            fontWeight = FontWeight.Bold)
        CustomSlider(stringResource(R.string.extreme_card_radius), "${radius.roundToInt()} dp", radius, { radius = it },
            { onNoteCardCornerRadiusChange(radius) }, 0f..36f, 35, settings, fontFamily, panelColors.text)
        CustomSlider(stringResource(R.string.extreme_card_elevation), "${String.format("%.1f", elevation)} dp", elevation,
            { elevation = it }, { onNoteCardElevationChange(elevation) }, 0f..12f, 23, settings, fontFamily, panelColors.text)
        CustomSlider(stringResource(R.string.extreme_card_padding), "${padding.roundToInt()} dp", padding, { padding = it },
            { onNoteCardPaddingChange(padding) }, 6f..24f, 17, settings, fontFamily, panelColors.text)
        CustomSlider(stringResource(R.string.extreme_image_height), "${imageHeight.roundToInt()} dp", imageHeight, { imageHeight = it },
            { onNoteCardImageHeightChange(imageHeight) }, 72f..220f, 36, settings, fontFamily, panelColors.text)
        CustomSlider(stringResource(R.string.extreme_title_lines), titleLines.roundToInt().toString(), titleLines, { titleLines = it }, {
                onNoteTitleMaxLinesChange(titleLines.roundToInt())
            }, 1f..8f, 6, settings, fontFamily, panelColors.text)
        CustomSlider(stringResource(R.string.extreme_content_lines), contentLines.roundToInt().toString(), contentLines,
            { contentLines = it }, {
                onNoteContentMaxLinesChange(contentLines.roundToInt())
            }, 2f..14f, 11, settings, fontFamily, panelColors.text)
        CustomSlider(stringResource(R.string.extreme_line_spacing), String.format("%.2fx", lineSpacing), lineSpacing, { lineSpacing = it },
            { onNoteLineSpacingChange(lineSpacing) }, 1f..1.8f, 15, settings, fontFamily, panelColors.text)
        ToggleRow(stringResource(R.string.extreme_show_date), settings.showNoteDate, onShowNoteDateChange, fontFamily, panelColors.text)
        ToggleRow(stringResource(R.string.extreme_show_category), settings.showCategoryChip, onShowCategoryChipChange, fontFamily,
            panelColors.text)
        ToggleRow(stringResource(R.string.extreme_show_favorite), settings.showFavoriteIcon, onShowFavoriteIconChange, fontFamily,
            panelColors.text)
    }
    Spacer(Modifier.height(12.dp))
    SettingsSectionPanel(textColorMode = settings.textColor, contentPadding = PaddingValues(14.dp)) { panelColors -> CustomSlider(
            stringResource(R.string.extreme_fab_size), "${fabSize.roundToInt()} dp", fabSize, { fabSize = it },
            { onFabSizeChange(fabSize) }, 48f..82f, 33, settings, fontFamily, panelColors.text)
    }
    Spacer(Modifier.height(12.dp))
    SettingsSectionPanel(textColorMode = settings.textColor, contentPadding = PaddingValues(14.dp)) { panelColors ->
        MotionOptionPicker(title = stringResource(R.string.performance_mode_title), selectedKey = settings.performanceMode,
            options = performanceModes, onSelected = onPerformanceModeChange, fontFamily = fontFamily, textColor = panelColors.text)
        Text(text = stringResource(R.string.performance_mode_description), modifier = Modifier.padding(top = 2.dp, bottom = 14.dp),
            color = panelColors.secondaryText, fontFamily = fontFamily, fontSize = 12.sp)
    }
    Spacer(Modifier.height(12.dp))
    SettingsSectionPanel(textColorMode = settings.textColor, contentPadding = PaddingValues(14.dp)) { panelColors -> Text(
            text = stringResource(R.string.motion_title), color = panelColors.text, fontFamily = fontFamily, fontWeight = FontWeight.Bold)
        ToggleRow(title = stringResource(R.string.motion_enabled), checked = settings.animationsEnabled,
            onCheckedChange = onAnimationsEnabledChange, fontFamily = fontFamily, textColor = panelColors.text)
        if (settings.animationsEnabled) {
            MotionOptionPicker(title = stringResource(R.string.motion_style), selectedKey = settings.animationStyle, options = motionStyles,
                onSelected = onAnimationStyleChange, fontFamily = fontFamily, textColor = panelColors.text)
            MotionOptionPicker(title = stringResource(R.string.motion_easing), selectedKey = settings.animationEasing,
                options = motionEasings, onSelected = onAnimationEasingChange, fontFamily = fontFamily, textColor = panelColors.text)
            CustomSlider(title = stringResource(R.string.motion_speed), label = String.format("%.1fx", animationSpeed),
                value = animationSpeed, onValueChange = {
                    animationSpeed = it
                }, onFinished = {
                    onAnimationSpeedChange(animationSpeed)
                }, range = 0.5f..2f, steps = 14, settings = settings, fontFamily = fontFamily, textColor = panelColors.text)
            CustomSlider(title = stringResource(R.string.motion_intensity), label = String.format("%.1fx", animationIntensity),
                value = animationIntensity, onValueChange = {
                    animationIntensity = it
                }, onFinished = {
                    onAnimationIntensityChange(animationIntensity)
                }, range = 0.5f..1.5f, steps = 9, settings = settings, fontFamily = fontFamily, textColor = panelColors.text)
            Spacer(Modifier.height(12.dp))
            Text(text = stringResource(R.string.motion_preview), color = panelColors.text, fontFamily = fontFamily, fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold)
            Surface(modifier = Modifier.fillMaxWidth().height(96.dp).padding(top = 8.dp), shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceContainerHigh) {
                ConfigurableAnimatedContent(targetState = previewState, animationsEnabled = true, animationSpeed = animationSpeed,
                    animationStyle = settings.animationStyle, animationEasing = settings.animationEasing,
                    animationIntensity = animationIntensity, performanceMode = settings.performanceMode) { state -> Box(
                        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = if (state) "B" else "A", color = panelColors.text, fontFamily = fontFamily, fontSize = 28.sp,
                            fontWeight = FontWeight.Bold)
                    }
                }
            }
            TextButton(onClick = {
                    UiSoundPlayer.playAction(context = context, action = UiActionSound.PlayPause)
                    previewState = !previewState
                }, modifier = Modifier.align(Alignment.End)) {
                Text(text = stringResource(R.string.motion_preview_button), color = panelColors.text, fontFamily = fontFamily,
                    fontWeight = FontWeight.SemiBold)
            }
        }
        Text(text = stringResource(R.string.motion_description), modifier = Modifier.padding(top = 6.dp), color = panelColors.secondaryText,
            fontFamily = fontFamily, fontSize = 12.sp)
    }
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `settings: AppSettings` — `settings` recibe un valor de tipo `AppSettings`. El contrato no marca este parámetro como anulable.
- `fontFamily: FontFamily` — `fontFamily` recibe un valor de tipo `FontFamily`. El contrato no marca este parámetro como anulable.
- `textColor: Color` — `textColor` recibe un valor de tipo `Color`. El contrato no marca este parámetro como anulable.
- `secondaryTextColor: Color` — `secondaryTextColor` recibe un valor de tipo `Color`. El contrato no marca este parámetro como anulable.
- `graphicColor: Color` — `graphicColor` recibe un valor de tipo `Color`. El contrato no marca este parámetro como anulable.
- `onProfileImageUriChange: (String) -> Unit` — `onProfileImageUriChange` recibe un valor de tipo `(String) -> Unit`. El contrato no marca este parámetro como anulable. Es un callback: el bloque no posee necesariamente la acción final, sino que notifica/delega al consumidor.
- `onProfileImageSizeChange: (Float) -> Unit` — `onProfileImageSizeChange` recibe un valor de tipo `(Float) -> Unit`. El contrato no marca este parámetro como anulable. Es un callback: el bloque no posee necesariamente la acción final, sino que notifica/delega al consumidor. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.
- `onIconStyleChange: (String) -> Unit` — `onIconStyleChange` recibe un valor de tipo `(String) -> Unit`. El contrato no marca este parámetro como anulable. Es un callback: el bloque no posee necesariamente la acción final, sino que notifica/delega al consumidor.
- `onIconSizeChange: (Float) -> Unit` — `onIconSizeChange` recibe un valor de tipo `(Float) -> Unit`. El contrato no marca este parámetro como anulable. Es un callback: el bloque no posee necesariamente la acción final, sino que notifica/delega al consumidor. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.
- `onAccentColorChange: (String) -> Unit` — `onAccentColorChange` recibe un valor de tipo `(String) -> Unit`. El contrato no marca este parámetro como anulable. Es un callback: el bloque no posee necesariamente la acción final, sino que notifica/delega al consumidor.
- `onNoteCardCornerRadiusChange: (Float) -> Unit` — `onNoteCardCornerRadiusChange` recibe un valor de tipo `(Float) -> Unit`. El contrato no marca este parámetro como anulable. Es un callback: el bloque no posee necesariamente la acción final, sino que notifica/delega al consumidor. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.
- `onNoteCardElevationChange: (Float) -> Unit` — `onNoteCardElevationChange` recibe un valor de tipo `(Float) -> Unit`. El contrato no marca este parámetro como anulable. Es un callback: el bloque no posee necesariamente la acción final, sino que notifica/delega al consumidor. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.
- `onNoteCardPaddingChange: (Float) -> Unit` — `onNoteCardPaddingChange` recibe un valor de tipo `(Float) -> Unit`. El contrato no marca este parámetro como anulable. Es un callback: el bloque no posee necesariamente la acción final, sino que notifica/delega al consumidor. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.
- `onNoteCardImageHeightChange: (Float) -> Unit` — `onNoteCardImageHeightChange` recibe un valor de tipo `(Float) -> Unit`. El contrato no marca este parámetro como anulable. Es un callback: el bloque no posee necesariamente la acción final, sino que notifica/delega al consumidor. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.
- `onNoteTitleMaxLinesChange: (Int) -> Unit` — `onNoteTitleMaxLinesChange` recibe un valor de tipo `(Int) -> Unit`. El contrato no marca este parámetro como anulable. Es un callback: el bloque no posee necesariamente la acción final, sino que notifica/delega al consumidor. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.
- `onNoteContentMaxLinesChange: (Int) -> Unit` — `onNoteContentMaxLinesChange` recibe un valor de tipo `(Int) -> Unit`. El contrato no marca este parámetro como anulable. Es un callback: el bloque no posee necesariamente la acción final, sino que notifica/delega al consumidor. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.
- `onNoteLineSpacingChange: (Float) -> Unit` — `onNoteLineSpacingChange` recibe un valor de tipo `(Float) -> Unit`. El contrato no marca este parámetro como anulable. Es un callback: el bloque no posee necesariamente la acción final, sino que notifica/delega al consumidor. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.
- `onShowNoteDateChange: (Boolean) -> Unit` — `onShowNoteDateChange` recibe un valor de tipo `(Boolean) -> Unit`. El contrato no marca este parámetro como anulable. Es un callback: el bloque no posee necesariamente la acción final, sino que notifica/delega al consumidor.
- `onShowCategoryChipChange: (Boolean) -> Unit` — `onShowCategoryChipChange` recibe un valor de tipo `(Boolean) -> Unit`. El contrato no marca este parámetro como anulable. Es un callback: el bloque no posee necesariamente la acción final, sino que notifica/delega al consumidor.
- `onShowFavoriteIconChange: (Boolean) -> Unit` — `onShowFavoriteIconChange` recibe un valor de tipo `(Boolean) -> Unit`. El contrato no marca este parámetro como anulable. Es un callback: el bloque no posee necesariamente la acción final, sino que notifica/delega al consumidor.
- `onFabSizeChange: (Float) -> Unit` — `onFabSizeChange` recibe un valor de tipo `(Float) -> Unit`. El contrato no marca este parámetro como anulable. Es un callback: el bloque no posee necesariamente la acción final, sino que notifica/delega al consumidor. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.
- `onPerformanceModeChange: (String) -> Unit` — `onPerformanceModeChange` recibe un valor de tipo `(String) -> Unit`. El contrato no marca este parámetro como anulable. Es un callback: el bloque no posee necesariamente la acción final, sino que notifica/delega al consumidor. Aunque el tipo sea `String`, semánticamente suele funcionar como clave/enumeración textual; las ramas del código delimitan los valores reconocidos.
- `onAnimationsEnabledChange: (Boolean) -> Unit` — `onAnimationsEnabledChange` recibe un valor de tipo `(Boolean) -> Unit`. El contrato no marca este parámetro como anulable. Es un callback: el bloque no posee necesariamente la acción final, sino que notifica/delega al consumidor.
- `onAnimationStyleChange: (String) -> Unit` — `onAnimationStyleChange` recibe un valor de tipo `(String) -> Unit`. El contrato no marca este parámetro como anulable. Es un callback: el bloque no posee necesariamente la acción final, sino que notifica/delega al consumidor.
- `onAnimationEasingChange: (String) -> Unit` — `onAnimationEasingChange` recibe un valor de tipo `(String) -> Unit`. El contrato no marca este parámetro como anulable. Es un callback: el bloque no posee necesariamente la acción final, sino que notifica/delega al consumidor.
- `onAnimationSpeedChange: (Float) -> Unit` — `onAnimationSpeedChange` recibe un valor de tipo `(Float) -> Unit`. El contrato no marca este parámetro como anulable. Es un callback: el bloque no posee necesariamente la acción final, sino que notifica/delega al consumidor. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.
- `onAnimationIntensityChange: (Float) -> Unit` — `onAnimationIntensityChange` recibe un valor de tipo `(Float) -> Unit`. El contrato no marca este parámetro como anulable. Es un callback: el bloque no posee necesariamente la acción final, sino que notifica/delega al consumidor. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `@Composable` convierte la función en una unidad declarativa de Compose y la somete a reglas de recomposición.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 112 | `val context` | `inferido` | `LocalContext.current` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El `Context` da acceso a recursos/servicios/almacenamiento Android; debe respetarse el ciclo de vida del contexto recibido. |
| 113 | `var iconMenu` | `inferido` | `by remember {` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 116 | `var profileSize` | `inferido` | `by remember {` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 119 | `var iconSize` | `inferido` | `by remember {` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 122 | `var radius` | `inferido` | `by remember {` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 125 | `var elevation` | `inferido` | `by remember {` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 128 | `var padding` | `inferido` | `by remember {` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 131 | `var imageHeight` | `inferido` | `by remember {` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 134 | `var titleLines` | `inferido` | `by remember {` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 137 | `var contentLines` | `inferido` | `by remember {` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 140 | `var lineSpacing` | `inferido` | `by remember {` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 143 | `var fabSize` | `inferido` | `by remember {` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 146 | `var animationSpeed` | `inferido` | `by remember {` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 149 | `var animationIntensity` | `inferido` | `by remember {` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 152 | `var previewState` | `inferido` | `by remember {` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 191 | `val picker` | `inferido` | `rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 256 | `val iconMenuLongestLabel` | `inferido` | `iconStyles.maxOfOrNull { context.getString(it.labelRes).length } ?: 0` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 257 | `val iconMenuWidth` | `inferido` | `when {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. |
| 293 | `val accentItems` | `inferido` | `remember {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 300 | `val color` | `inferido` | `option?.color?: MaterialTheme.colorScheme.primary` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 301 | `val selected` | `inferido` | `settings.accentColor == key` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El nombre indica un estado de decisión/visibilidad/selección usado para controlar una rama de comportamiento o presentación. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 104 | `graphicColor: Color, onProfileImageUriChange: (String) -> Unit, onProfileImageSizeChange: (Float) -> Unit,` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 105 | `onIconStyleChange: (String) -> Unit, onIconSizeChange: (Float) -> Unit, onAccentColorChange: (String) -> Unit,` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 106 | `onNoteCardCornerRadiusChange: (Float) -> Unit, onNoteCardElevationChange: (Float) -> Unit, onNoteCardPaddingChange: (Float) -> Unit,` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 107 | `onNoteCardImageHeightChange: (Float) -> Unit, onNoteTitleMaxLinesChange: (Int) -> Unit, onNoteContentMaxLinesChange: (Int) -> Unit,` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 108 | `onNoteLineSpacingChange: (Float) -> Unit, onShowNoteDateChange: (Boolean) -> Unit, onShowCategoryChipChange: (Boolean) -> Unit,` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 109 | `onShowFavoriteIconChange: (Boolean) -> Unit, onFabSizeChange: (Float) -> Unit, onPerformanceModeChange: (String) -> Unit,` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 110 | `onAnimationsEnabledChange: (Boolean) -> Unit, onAnimationStyleChange: (String) -> Unit, onAnimationEasingChange: (String) -> Unit,` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 111 | `onAnimationSpeedChange: (Float) -> Unit, onAnimationIntensityChange: (Float) -> Unit) {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 155 | `LaunchedEffect(settings.profileImageSize) {` | Efecto de Compose basado en corrutina: se inicia cuando entra en composición y se reinicia si cambian sus claves; se cancela al salir de composición. |
| 158 | `LaunchedEffect(settings.iconSize) {` | Efecto de Compose basado en corrutina: se inicia cuando entra en composición y se reinicia si cambian sus claves; se cancela al salir de composición. |
| 161 | `LaunchedEffect(settings.noteCardCornerRadius) {` | Efecto de Compose basado en corrutina: se inicia cuando entra en composición y se reinicia si cambian sus claves; se cancela al salir de composición. |
| 164 | `LaunchedEffect(settings.noteCardElevation) {` | Efecto de Compose basado en corrutina: se inicia cuando entra en composición y se reinicia si cambian sus claves; se cancela al salir de composición. |
| 167 | `LaunchedEffect(settings.noteCardPadding) {` | Efecto de Compose basado en corrutina: se inicia cuando entra en composición y se reinicia si cambian sus claves; se cancela al salir de composición. |
| 170 | `LaunchedEffect(settings.noteCardImageHeight) {` | Efecto de Compose basado en corrutina: se inicia cuando entra en composición y se reinicia si cambian sus claves; se cancela al salir de composición. |
| 173 | `LaunchedEffect(settings.noteTitleMaxLines) {` | Efecto de Compose basado en corrutina: se inicia cuando entra en composición y se reinicia si cambian sus claves; se cancela al salir de composición. |
| 176 | `LaunchedEffect(settings.noteContentMaxLines) {` | Efecto de Compose basado en corrutina: se inicia cuando entra en composición y se reinicia si cambian sus claves; se cancela al salir de composición. |
| 179 | `LaunchedEffect(settings.noteLineSpacing) {` | Efecto de Compose basado en corrutina: se inicia cuando entra en composición y se reinicia si cambian sus claves; se cancela al salir de composición. |
| 182 | `LaunchedEffect(settings.fabSize) {` | Efecto de Compose basado en corrutina: se inicia cuando entra en composición y se reinicia si cambian sus claves; se cancela al salir de composición. |
| 185 | `LaunchedEffect(settings.animationSpeed) {` | Efecto de Compose basado en corrutina: se inicia cuando entra en composición y se reinicia si cambian sus claves; se cancela al salir de composición. |
| 188 | `LaunchedEffect(settings.animationIntensity) {` | Efecto de Compose basado en corrutina: se inicia cuando entra en composición y se reinicia si cambian sus claves; se cancela al salir de composición. |
| 192 | `uri ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 193 | `if (uri != null) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 194 | `try {` | Frontera de manejo de errores: agrupa operaciones que pueden lanzar excepciones para permitir recuperación o limpieza controlada. |
| 211 | `if (settings.profileImageUri.isNotBlank()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 228 | `if (settings.profileImageUri.isNotBlank()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 258 | `iconMenuLongestLabel <= 8 -> 118.dp` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 259 | `iconMenuLongestLabel <= 12 -> 138.dp` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 260 | `else -> 158.dp` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |
| 266 | `option -> DropdownMenuItem(modifier = Modifier.height(32.dp),` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 367 | `if (settings.animationsEnabled) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 3.
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 4.
- **Límite visual:** La UI impone un límite explícito de tamaño o cantidad de líneas para impedir crecimiento visual no controlado. Apariciones en este bloque: 1.
- **Sin foco de popup:** El popup se configura para no tomar el foco de ventana; en este proyecto ayuda a preservar el modo inmersivo y evita reaparición indeseada de la navegación Android. Apariciones en este bloque: 1.

#### Semántica Compose/lifecycle

- **`remember`:** Conserva valores entre recomposiciones bajo las mismas claves.
- **`mutableStateOf`:** Crea estado observable; las lecturas registradas por Compose quedan enlazadas a sus cambios.
- **`LaunchedEffect`:** Ejecuta una corrutina ligada al ciclo de vida de la composición y a sus claves.
- **Callbacks `onClick`:** La lógica asociada se ejecuta por evento de usuario, no durante la composición inicial.
- **`Modifier`:** La cadena de modificadores define tamaño, interacción, dibujo, semántica o posicionamiento; el orden de los modificadores puede ser significativo.

#### Efectos secundarios y recursos

- **Estado Compose:** Declara o utiliza estado observable de Compose; una escritura en ese estado puede desencadenar recomposición de los lectores.
- **Sonido/háptica:** Produce feedback perceptible para el usuario; debe ejecutarse en respuesta a la interacción/estado correspondiente.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `mutableStateOf`, `mutableFloatStateOf`, `settings.noteTitleMaxLines.toFloat`, `settings.noteContentMaxLines.toFloat`, `LaunchedEffect`, `rememberLauncherForActivityResult`, `ActivityResultContracts.OpenDocument`, `context.contentResolver.takePersistableUriPermission`, `UiSoundPlayer.play`, `onProfileImageUriChange`, `uri.toString`, `Text`, `stringResource`, `Spacer`, `Modifier.height`, `SettingsSectionPanel`, `PaddingValues`, `Row`, `Surface`, `Modifier.size`, `settings.profileImageUri.isNotBlank`, `AsyncImage`, `Uri.parse`, `Modifier.fillMaxWidth`, `clip`, `Box`, `Icon`, `Column`, `Modifier.padding`, `TextButton`, `UiSoundPlayer.playAction`, `picker.launch`, `arrayOf`, `CustomSlider`, `onProfileImageSizeChange`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.
- Conservar `focusable = false` en estos popups si se quiere mantener el comportamiento inmersivo que evita que reaparezcan los botones de navegación del sistema.
- No convertir estado de Compose en variables ordinarias sin considerar recomposición y persistencia temporal.
- Preservar la normalización y correspondencia de `performanceMode`, porque otras capas usan las mismas claves para rendimiento/calidad.

### 4.5 `MotionOptionPicker` — fun, líneas 412–462

```kotlin
private fun MotionOptionPicker(title: String, selectedKey: String, options: List<MotionOption>, onSelected: (String) -> Unit,
    fontFamily: FontFamily, textColor: Color) {
    val context = LocalContext.current
    var expanded by remember {
        mutableStateOf(false)
    }
    val selected = options.firstOrNull { it.key == selectedKey }?: options.first()
    // El ancho del popup se adapta a la opción más larga para evitar
    // columnas anchas con espacio vacío a los lados.
    val longestOptionLength = options.maxOfOrNull { context.getString(it.labelRes).length } ?: 0
    val compactMenuWidth = when {
            longestOptionLength <= 8 -> 118.dp
            longestOptionLength <= 12 -> 138.dp
            longestOptionLength <= 16 -> 158.dp
            longestOptionLength <= 20 -> 178.dp
            else -> 196.dp
        }
    Spacer(Modifier.height(10.dp))
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(text = title, modifier = Modifier.weight(1f), color = textColor, fontFamily = fontFamily, fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold)
        Box {
            TextButton(onClick = {
                    UiSoundPlayer.playAction(context = context, action = UiActionSound.Menu)
                    expanded = true
                }) {
                Text(text = stringResource(selected.labelRes), color = textColor, fontFamily = fontFamily, fontSize = 12.sp)
                Icon(imageVector = Icons.Default.ExpandMore, contentDescription = null, tint = textColor, modifier = Modifier.size(18.dp))
            }
            AppDropdownMenu(modifier = Modifier.heightIn(max = 176.dp).width(compactMenuWidth), expanded = expanded, onDismissRequest = {
                    expanded = false
                }, properties = PopupProperties(focusable = false, dismissOnBackPress = true, dismissOnClickOutside = true)) {
                options.forEach { option -> DropdownMenuItem(modifier = Modifier.height(32.dp), contentPadding = PaddingValues(
                            horizontal = 4.dp, vertical = 0.dp), text = {
                            Text(text = stringResource(option.labelRes), modifier = Modifier.fillMaxWidth(), color = textColor,
                                fontFamily = fontFamily, fontSize = 12.sp, maxLines = 1, textAlign = TextAlign.Center, fontWeight =
                                    if (option.key == selectedKey) {
                                        FontWeight.Bold
                                    } else {
                                        FontWeight.Normal
                                    })
                        }, onClick = {
                            UiSoundPlayer.playAction(context = context, action = UiActionSound.Select)
                            expanded = false
                            onSelected(option.key)
                        })
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

- `title: String` — `title` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.
- `selectedKey: String` — `selectedKey` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable. Aunque el tipo sea `String`, semánticamente suele funcionar como clave/enumeración textual; las ramas del código delimitan los valores reconocidos.
- `options: List<MotionOption>` — `options` recibe un valor de tipo `List<MotionOption>`. El contrato no marca este parámetro como anulable.
- `onSelected: (String) -> Unit` — `onSelected` recibe un valor de tipo `(String) -> Unit`. El contrato no marca este parámetro como anulable. Es un callback: el bloque no posee necesariamente la acción final, sino que notifica/delega al consumidor.
- `fontFamily: FontFamily` — `fontFamily` recibe un valor de tipo `FontFamily`. El contrato no marca este parámetro como anulable.
- `textColor: Color` — `textColor` recibe un valor de tipo `Color`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.
- `@Composable` convierte la función en una unidad declarativa de Compose y la somete a reglas de recomposición.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 414 | `val context` | `inferido` | `LocalContext.current` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El `Context` da acceso a recursos/servicios/almacenamiento Android; debe respetarse el ciclo de vida del contexto recibido. |
| 415 | `var expanded` | `inferido` | `by remember {` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 418 | `val selected` | `inferido` | `options.firstOrNull { it.key == selectedKey }?: options.first()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El nombre indica un estado de decisión/visibilidad/selección usado para controlar una rama de comportamiento o presentación. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 421 | `val longestOptionLength` | `inferido` | `options.maxOfOrNull { context.getString(it.labelRes).length } ?: 0` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 422 | `val compactMenuWidth` | `inferido` | `when {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 412 | `private fun MotionOptionPicker(title: String, selectedKey: String, options: List<MotionOption>, onSelected: (String) -> Unit,` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 423 | `longestOptionLength <= 8 -> 118.dp` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 424 | `longestOptionLength <= 12 -> 138.dp` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 425 | `longestOptionLength <= 16 -> 158.dp` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 426 | `longestOptionLength <= 20 -> 178.dp` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 427 | `else -> 196.dp` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |
| 448 | `if (option.key == selectedKey) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |

**Restricciones concretas que aparecen en este bloque:**
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 2.
- **Límite visual:** La UI impone un límite explícito de tamaño o cantidad de líneas para impedir crecimiento visual no controlado. Apariciones en este bloque: 1.
- **Sin foco de popup:** El popup se configura para no tomar el foco de ventana; en este proyecto ayuda a preservar el modo inmersivo y evita reaparición indeseada de la navegación Android. Apariciones en este bloque: 1.

#### Semántica Compose/lifecycle

- **`remember`:** Conserva valores entre recomposiciones bajo las mismas claves.
- **`mutableStateOf`:** Crea estado observable; las lecturas registradas por Compose quedan enlazadas a sus cambios.
- **Callbacks `onClick`:** La lógica asociada se ejecuta por evento de usuario, no durante la composición inicial.
- **`Modifier`:** La cadena de modificadores define tamaño, interacción, dibujo, semántica o posicionamiento; el orden de los modificadores puede ser significativo.

#### Efectos secundarios y recursos

- **Estado Compose:** Declara o utiliza estado observable de Compose; una escritura en ese estado puede desencadenar recomposición de los lectores.
- **Sonido/háptica:** Produce feedback perceptible para el usuario; debe ejecutarse en respuesta a la interacción/estado correspondiente.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `mutableStateOf`, `options.first`, `context.getString`, `Spacer`, `Modifier.height`, `Row`, `Modifier.fillMaxWidth`, `Text`, `Modifier.weight`, `TextButton`, `UiSoundPlayer.playAction`, `stringResource`, `Icon`, `Modifier.size`, `AppDropdownMenu`, `Modifier.heightIn`, `width`, `PopupProperties`, `DropdownMenuItem`, `PaddingValues`, `onSelected`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.
- Conservar `focusable = false` en estos popups si se quiere mantener el comportamiento inmersivo que evita que reaparezcan los botones de navegación del sistema.
- No convertir estado de Compose en variables ordinarias sin considerar recomposición y persistencia temporal.

### 4.6 `CustomSlider` — fun, líneas 465–476

```kotlin
private fun CustomSlider(title: String, label: String, value: Float, onValueChange: (Float) -> Unit, onFinished: () -> Unit,
    range: ClosedFloatingPointRange<Float>, steps: Int, settings: AppSettings, fontFamily: FontFamily, textColor: Color) {
    Spacer(Modifier.height(12.dp))
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(text = title, modifier = Modifier.weight(1f), fontFamily = fontFamily, color = textColor, fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold)
        Text(text = label, fontFamily = fontFamily, color = textColor, fontSize = 12.sp)
    }
    StyledSettingsSlider(value = value, onValueChange = onValueChange, onValueChangeFinished = onFinished, valueRange = range,
        steps = steps, activeColor = MaterialTheme.colorScheme.primary,
        inactiveColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f), style = settings.sliderStyle, valueLabel = label)
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `title: String` — `title` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.
- `label: String` — `label` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.
- `value: Float` — `value` recibe un valor de tipo `Float`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.
- `onValueChange: (Float) -> Unit` — `onValueChange` recibe un valor de tipo `(Float) -> Unit`. El contrato no marca este parámetro como anulable. Es un callback: el bloque no posee necesariamente la acción final, sino que notifica/delega al consumidor. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.
- `onFinished: () -> Unit` — `onFinished` recibe un valor de tipo `() -> Unit`. El contrato no marca este parámetro como anulable. Es un callback: el bloque no posee necesariamente la acción final, sino que notifica/delega al consumidor.
- `range: ClosedFloatingPointRange<Float>` — `range` recibe un valor de tipo `ClosedFloatingPointRange<Float>`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.
- `steps: Int` — `steps` recibe un valor de tipo `Int`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.
- `settings: AppSettings` — `settings` recibe un valor de tipo `AppSettings`. El contrato no marca este parámetro como anulable.
- `fontFamily: FontFamily` — `fontFamily` recibe un valor de tipo `FontFamily`. El contrato no marca este parámetro como anulable.
- `textColor: Color` — `textColor` recibe un valor de tipo `Color`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.
- `@Composable` convierte la función en una unidad declarativa de Compose y la somete a reglas de recomposición.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 465 | `private fun CustomSlider(title: String, label: String, value: Float, onValueChange: (Float) -> Unit, onFinished: () -> Unit,` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |

#### Semántica Compose/lifecycle

- **`Modifier`:** La cadena de modificadores define tamaño, interacción, dibujo, semántica o posicionamiento; el orden de los modificadores puede ser significativo.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `Spacer`, `Modifier.height`, `Row`, `Modifier.fillMaxWidth`, `Text`, `Modifier.weight`, `StyledSettingsSlider`, `MaterialTheme.colorScheme.outline.copy`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.7 `ToggleRow` — fun, líneas 479–487

```kotlin
private fun ToggleRow(title: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit, fontFamily: FontFamily, textColor: Color) {
    val context = LocalContext.current
    Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(text = title, modifier = Modifier.weight(1f), fontFamily = fontFamily, color = textColor, fontSize = 13.sp)
        Switch(checked = checked, onCheckedChange = { newChecked -> UiSoundPlayer.playToggle(context = context, checked = newChecked)
                onCheckedChange(newChecked)
            })
    }
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `title: String` — `title` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.
- `checked: Boolean` — `checked` recibe un valor de tipo `Boolean`. El contrato no marca este parámetro como anulable.
- `onCheckedChange: (Boolean) -> Unit` — `onCheckedChange` recibe un valor de tipo `(Boolean) -> Unit`. El contrato no marca este parámetro como anulable. Es un callback: el bloque no posee necesariamente la acción final, sino que notifica/delega al consumidor.
- `fontFamily: FontFamily` — `fontFamily` recibe un valor de tipo `FontFamily`. El contrato no marca este parámetro como anulable.
- `textColor: Color` — `textColor` recibe un valor de tipo `Color`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.
- `@Composable` convierte la función en una unidad declarativa de Compose y la somete a reglas de recomposición.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 480 | `val context` | `inferido` | `LocalContext.current` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El `Context` da acceso a recursos/servicios/almacenamiento Android; debe respetarse el ciclo de vida del contexto recibido. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 479 | `private fun ToggleRow(title: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit, fontFamily: FontFamily, textColor: Color) {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |

#### Semántica Compose/lifecycle

- **`Modifier`:** La cadena de modificadores define tamaño, interacción, dibujo, semántica o posicionamiento; el orden de los modificadores puede ser significativo.

#### Efectos secundarios y recursos

- **Sonido/háptica:** Produce feedback perceptible para el usuario; debe ejecutarse en respuesta a la interacción/estado correspondiente.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `Row`, `Modifier.fillMaxWidth`, `padding`, `Text`, `Modifier.weight`, `Switch`, `UiSoundPlayer.playToggle`, `onCheckedChange`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

## 5. Inventario global de propiedades/variables detectadas

| Línea | Identificador | Mutabilidad | Tipo | Explicación |
|---:|---|---|---|---|
| 70 | `iconStyles` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 79 | `motionStyles` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 90 | `motionEasings` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 93 | `performanceModes` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Actúa como selector de estrategia/perfil; su valor determina qué rama de configuración se aplica. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 95 | `accents` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 112 | `context` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El `Context` da acceso a recursos/servicios/almacenamiento Android; debe respetarse el ciclo de vida del contexto recibido. |
| 113 | `iconMenu` | `var` | `inferido` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 116 | `profileSize` | `var` | `inferido` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 119 | `iconSize` | `var` | `inferido` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 122 | `radius` | `var` | `inferido` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 125 | `elevation` | `var` | `inferido` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 128 | `padding` | `var` | `inferido` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 131 | `imageHeight` | `var` | `inferido` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 134 | `titleLines` | `var` | `inferido` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 137 | `contentLines` | `var` | `inferido` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 140 | `lineSpacing` | `var` | `inferido` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 143 | `fabSize` | `var` | `inferido` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 146 | `animationSpeed` | `var` | `inferido` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 149 | `animationIntensity` | `var` | `inferido` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 152 | `previewState` | `var` | `inferido` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 191 | `picker` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 256 | `iconMenuLongestLabel` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 257 | `iconMenuWidth` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. |
| 293 | `accentItems` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 300 | `color` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 301 | `selected` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El nombre indica un estado de decisión/visibilidad/selección usado para controlar una rama de comportamiento o presentación. |
| 414 | `context` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El `Context` da acceso a recursos/servicios/almacenamiento Android; debe respetarse el ciclo de vida del contexto recibido. |
| 415 | `expanded` | `var` | `inferido` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 418 | `selected` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El nombre indica un estado de decisión/visibilidad/selección usado para controlar una rama de comportamiento o presentación. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 421 | `longestOptionLength` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 422 | `compactMenuWidth` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. |
| 480 | `context` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El `Context` da acceso a recursos/servicios/almacenamiento Android; debe respetarse el ciclo de vida del contexto recibido. |

## 6. Mapa exhaustivo de TODOS los bloques delimitados por `{}`

Esta tabla recorre todas las llaves estructurales detectadas fuera de strings/comentarios. Así se documentan también lambdas de Compose, callbacks, iteraciones y ámbitos locales que no tienen un nombre propio de función.

| Inicio–fin | Prof. | Cabecera/contexto | Qué significa ese bloque |
|---|---:|---|---|
| 111–409 | 0 | `onAnimationSpeedChange: (Float) -> Unit, onAnimationIntensityChange: (Float) -> Unit)` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 113–115 | 1 | `var iconMenu by remember` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 116–118 | 1 | `var profileSize by remember` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 119–121 | 1 | `var iconSize by remember` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 122–124 | 1 | `var radius by remember` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 125–127 | 1 | `var elevation by remember` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 128–130 | 1 | `var padding by remember` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 131–133 | 1 | `var imageHeight by remember` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 134–136 | 1 | `var titleLines by remember` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 137–139 | 1 | `var contentLines by remember` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 140–142 | 1 | `var lineSpacing by remember` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 143–145 | 1 | `var fabSize by remember` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 146–148 | 1 | `var animationSpeed by remember` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 149–151 | 1 | `var animationIntensity by remember` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 152–154 | 1 | `var previewState by remember` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 155–157 | 1 | `LaunchedEffect(settings.profileImageSize)` | Efecto de Compose con corrutina: su ciclo de vida depende de las claves; se cancela/reinicia al cambiar dichas claves o salir de composición. |
| 158–160 | 1 | `LaunchedEffect(settings.iconSize)` | Efecto de Compose con corrutina: su ciclo de vida depende de las claves; se cancela/reinicia al cambiar dichas claves o salir de composición. |
| 161–163 | 1 | `LaunchedEffect(settings.noteCardCornerRadius)` | Efecto de Compose con corrutina: su ciclo de vida depende de las claves; se cancela/reinicia al cambiar dichas claves o salir de composición. |
| 164–166 | 1 | `LaunchedEffect(settings.noteCardElevation)` | Efecto de Compose con corrutina: su ciclo de vida depende de las claves; se cancela/reinicia al cambiar dichas claves o salir de composición. |
| 167–169 | 1 | `LaunchedEffect(settings.noteCardPadding)` | Efecto de Compose con corrutina: su ciclo de vida depende de las claves; se cancela/reinicia al cambiar dichas claves o salir de composición. |
| 170–172 | 1 | `LaunchedEffect(settings.noteCardImageHeight)` | Efecto de Compose con corrutina: su ciclo de vida depende de las claves; se cancela/reinicia al cambiar dichas claves o salir de composición. |
| 173–175 | 1 | `LaunchedEffect(settings.noteTitleMaxLines)` | Efecto de Compose con corrutina: su ciclo de vida depende de las claves; se cancela/reinicia al cambiar dichas claves o salir de composición. |
| 176–178 | 1 | `LaunchedEffect(settings.noteContentMaxLines)` | Efecto de Compose con corrutina: su ciclo de vida depende de las claves; se cancela/reinicia al cambiar dichas claves o salir de composición. |
| 179–181 | 1 | `LaunchedEffect(settings.noteLineSpacing)` | Efecto de Compose con corrutina: su ciclo de vida depende de las claves; se cancela/reinicia al cambiar dichas claves o salir de composición. |
| 182–184 | 1 | `LaunchedEffect(settings.fabSize)` | Efecto de Compose con corrutina: su ciclo de vida depende de las claves; se cancela/reinicia al cambiar dichas claves o salir de composición. |
| 185–187 | 1 | `LaunchedEffect(settings.animationSpeed)` | Efecto de Compose con corrutina: su ciclo de vida depende de las claves; se cancela/reinicia al cambiar dichas claves o salir de composición. |
| 188–190 | 1 | `LaunchedEffect(settings.animationIntensity)` | Efecto de Compose con corrutina: su ciclo de vida depende de las claves; se cancela/reinicia al cambiar dichas claves o salir de composición. |
| 191–201 | 1 | `val picker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument())` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 193–200 | 2 | `if (uri != null)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 194–196 | 3 | `try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 196–197 | 3 | `} catch (_: SecurityException)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 205–242 | 1 | `SettingsSectionPanel(textColorMode = settings.textColor, contentPadding = PaddingValues(14.dp))` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 209–237 | 2 | `Row(verticalAlignment = Alignment.CenterVertically)` | Lambda de contenido de UI Compose: declara hijos y configuración visual dentro del contenedor. El orden y los `Modifier` influyen en medida, posición, dibujo e interacción. |
| 210–220 | 3 | `Surface(modifier = Modifier.size(74.dp), shape = CircleShape, color = MaterialTheme.colorScheme.surfaceContainerHigh)` | Lambda de contenido de UI Compose: declara hijos y configuración visual dentro del contenedor. El orden y los `Modifier` influyen en medida, posición, dibujo e interacción. |
| 211–214 | 4 | `if (settings.profileImageUri.isNotBlank())` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 214–219 | 4 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 215–218 | 5 | `Box(contentAlignment = Alignment.Center)` | Lambda de contenido de UI Compose: declara hijos y configuración visual dentro del contenedor. El orden y los `Modifier` influyen en medida, posición, dibujo e interacción. |
| 221–236 | 3 | `Column(modifier = Modifier.padding(start = 12.dp))` | Lambda de contenido de UI Compose: declara hijos y configuración visual dentro del contenedor. El orden y los `Modifier` influyen en medida, posición, dibujo e interacción. |
| 222–225 | 4 | `TextButton(onClick =` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 225–227 | 4 | `})` | Ámbito delimitado por llaves en profundidad 4. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 228–235 | 4 | `if (settings.profileImageUri.isNotBlank())` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 229–232 | 5 | `TextButton(onClick =` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 232–234 | 5 | `})` | Ámbito delimitado por llaves en profundidad 5. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 239–239 | 2 | `onValueChange =` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 239–241 | 2 | `onValueChange = { profileSize = it }, onFinished =` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 244–282 | 1 | `SettingsSectionPanel(textColorMode = settings.textColor, contentPadding = PaddingValues(14.dp))` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 247–277 | 2 | `Box(modifier = Modifier.fillMaxWidth())` | Lambda de contenido de UI Compose: declara hijos y configuración visual dentro del contenedor. El orden y los `Modifier` influyen en medida, posición, dibujo e interacción. |
| 248–251 | 3 | `TextButton(onClick =` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 251–255 | 3 | `})` | Ámbito delimitado por llaves en profundidad 3. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 252–254 | 4 | `Text(text = stringResource(iconStyles.firstOrNull` | Ámbito delimitado por llaves en profundidad 4. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 256–256 | 3 | `val iconMenuLongestLabel = iconStyles.maxOfOrNull` | Ámbito delimitado por llaves en profundidad 3. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 257–261 | 3 | `val iconMenuWidth = when` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 262–264 | 3 | `AppDropdownMenu(modifier = Modifier.width(iconMenuWidth), expanded = iconMenu, onDismissRequest =` | Callback de cierre: se ejecuta cuando la UI solicita descartar/cerrar el popup, diálogo o superficie asociada. |
| 264–276 | 3 | `}, properties = PopupProperties(focusable = false, dismissOnBackPress = true, dismissOnClickOutside = true))` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 265–275 | 4 | `iconStyles.forEach` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 267–270 | 5 | `contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp), text =` | Ámbito delimitado por llaves en profundidad 5. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 270–274 | 5 | `}, onClick =` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 279–279 | 2 | `onValueChange =` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 279–281 | 2 | `onValueChange = { iconSize = it }, onFinished =` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 284–322 | 1 | `SettingsSectionPanel(textColorMode = settings.textColor, contentPadding = PaddingValues(14.dp))` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 293–295 | 2 | `val accentItems = remember` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 296–321 | 2 | `Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp))` | Lambda de contenido de UI Compose: declara hijos y configuración visual dentro del contenedor. El orden y los `Modifier` influyen en medida, posición, dibujo e interacción. |
| 297–320 | 3 | `accentItems.chunked(5).forEach` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 298–319 | 4 | `horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically)` | Ámbito delimitado por llaves en profundidad 4. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 299–311 | 5 | `rowItems.forEach` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 302–305 | 6 | `Surface(modifier = Modifier.size(48.dp).clickable` | Lambda de contenido de UI Compose: declara hijos y configuración visual dentro del contenedor. El orden y los `Modifier` influyen en medida, posición, dibujo e interacción. |
| 306–308 | 6 | `color = if (selected)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 308–310 | 6 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 310–310 | 6 | `}), color = color, shadowElevation = if (selected) 2.dp else 0.dp)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 316–318 | 5 | `repeat(5 - rowItems.size)` | Ámbito delimitado por llaves en profundidad 5. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 324–349 | 1 | `SettingsSectionPanel(textColorMode = settings.textColor, contentPadding = PaddingValues(14.dp))` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 327–327 | 2 | `CustomSlider(stringResource(R.string.extreme_card_radius), "${radius.roundToInt()} dp", radius,` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 328–328 | 2 | `fontWeight = FontWeight.Bold) CustomSlider(stringResource(R.string.extreme_card_radius), "${radius.roundToInt()} dp", radius, { radius = it },` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 330–330 | 2 | `{ onNoteCardCornerRadiusChange(radius) }, 0f..36f, 35, settings, fontFamily, panelColors.text) CustomSlider(stringResource(R.string.extreme_card_elevation), "${String.format("%.1f"` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 330–330 | 2 | `{ elevation = it },` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 331–331 | 2 | `CustomSlider(stringResource(R.string.extreme_card_padding), "${padding.roundToInt()} dp", padding,` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 332–332 | 2 | `{ elevation = it }, { onNoteCardElevationChange(elevation) }, 0f..12f, 23, settings, fontFamily, panelColors.text) CustomSlider(stringResource(R.string.extreme_card_padding), "${pa` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 333–333 | 2 | `CustomSlider(stringResource(R.string.extreme_image_height), "${imageHeight.roundToInt()} dp", imageHeight,` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 334–334 | 2 | `{ onNoteCardPaddingChange(padding) }, 6f..24f, 17, settings, fontFamily, panelColors.text) CustomSlider(stringResource(R.string.extreme_image_height), "${imageHeight.roundToInt()} ` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 335–335 | 2 | `CustomSlider(stringResource(R.string.extreme_title_lines), titleLines.roundToInt().toString(), titleLines,` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 335–337 | 2 | `CustomSlider(stringResource(R.string.extreme_title_lines), titleLines.roundToInt().toString(), titleLines, { titleLines = it },` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 339–339 | 2 | `}, 1f..8f, 6, settings, fontFamily, panelColors.text) CustomSlider(stringResource(R.string.extreme_content_lines), contentLines.roundToInt().toString(), contentLines,` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 339–341 | 2 | `{ contentLines = it },` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 342–342 | 2 | `CustomSlider(stringResource(R.string.extreme_line_spacing), String.format("%.2fx", lineSpacing), lineSpacing,` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 343–343 | 2 | `}, 2f..14f, 11, settings, fontFamily, panelColors.text) CustomSlider(stringResource(R.string.extreme_line_spacing), String.format("%.2fx", lineSpacing), lineSpacing, { lineSpacing ` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 351–354 | 1 | `SettingsSectionPanel(textColorMode = settings.textColor, contentPadding = PaddingValues(14.dp))` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 352–352 | 2 | `stringResource(R.string.extreme_fab_size), "${fabSize.roundToInt()} dp", fabSize,` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 353–353 | 2 | `Spacer(Modifier.height(12.dp)) SettingsSectionPanel(textColorMode = settings.textColor, contentPadding = PaddingValues(14.dp)) { panelColors -> CustomSlider( stringResource(R.strin` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 356–361 | 1 | `SettingsSectionPanel(textColorMode = settings.textColor, contentPadding = PaddingValues(14.dp))` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 363–408 | 1 | `SettingsSectionPanel(textColorMode = settings.textColor, contentPadding = PaddingValues(14.dp))` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 367–405 | 2 | `if (settings.animationsEnabled)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 373–375 | 3 | `value = animationSpeed, onValueChange =` | Ámbito delimitado por llaves en profundidad 3. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 375–377 | 3 | `}, onFinished =` | Ámbito delimitado por llaves en profundidad 3. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 379–381 | 3 | `value = animationIntensity, onValueChange =` | Ámbito delimitado por llaves en profundidad 3. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 381–383 | 3 | `}, onFinished =` | Ámbito delimitado por llaves en profundidad 3. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 388–397 | 3 | `color = MaterialTheme.colorScheme.surfaceContainerHigh)` | Ámbito delimitado por llaves en profundidad 3. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 391–396 | 4 | `animationIntensity = animationIntensity, performanceMode = settings.performanceMode)` | Ámbito delimitado por llaves en profundidad 4. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 392–395 | 5 | `modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center)` | Ámbito delimitado por llaves en profundidad 5. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 398–401 | 3 | `TextButton(onClick =` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 401–404 | 3 | `}, modifier = Modifier.align(Alignment.End))` | Ámbito delimitado por llaves en profundidad 3. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 413–462 | 0 | `fontFamily: FontFamily, textColor: Color)` | Ámbito delimitado por llaves en profundidad 0. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 415–417 | 1 | `var expanded by remember` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 418–418 | 1 | `val selected = options.firstOrNull` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 421–421 | 1 | `val longestOptionLength = options.maxOfOrNull` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 422–428 | 1 | `val compactMenuWidth = when` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 430–461 | 1 | `Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically)` | Lambda de contenido de UI Compose: declara hijos y configuración visual dentro del contenedor. El orden y los `Modifier` influyen en medida, posición, dibujo e interacción. |
| 433–460 | 2 | `Box` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 434–437 | 3 | `TextButton(onClick =` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 437–440 | 3 | `})` | Ámbito delimitado por llaves en profundidad 3. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 441–443 | 3 | `AppDropdownMenu(modifier = Modifier.heightIn(max = 176.dp).width(compactMenuWidth), expanded = expanded, onDismissRequest =` | Callback de cierre: se ejecuta cuando la UI solicita descartar/cerrar el popup, diálogo o superficie asociada. |
| 443–459 | 3 | `}, properties = PopupProperties(focusable = false, dismissOnBackPress = true, dismissOnClickOutside = true))` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 444–458 | 4 | `options.forEach` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 445–453 | 5 | `horizontal = 4.dp, vertical = 0.dp), text =` | Ámbito delimitado por llaves en profundidad 5. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 448–450 | 6 | `if (option.key == selectedKey)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 450–452 | 6 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 453–457 | 5 | `}, onClick =` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 466–476 | 0 | `range: ClosedFloatingPointRange<Float>, steps: Int, settings: AppSettings, fontFamily: FontFamily, textColor: Color)` | Ámbito delimitado por llaves en profundidad 0. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 468–472 | 1 | `Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically)` | Lambda de contenido de UI Compose: declara hijos y configuración visual dentro del contenedor. El orden y los `Modifier` influyen en medida, posición, dibujo e interacción. |
| 479–487 | 0 | `private fun ToggleRow(title: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit, fontFamily: FontFamily, textColor: Color)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 481–486 | 1 | `Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically)` | Lambda de contenido de UI Compose: declara hijos y configuración visual dentro del contenedor. El orden y los `Modifier` influyen en medida, posición, dibujo e interacción. |
| 483–485 | 2 | `Switch(checked = checked, onCheckedChange =` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |

## 7. Lectura de seguridad antes de tocar este archivo

La documentación describe **lo que el código actual ya hace**. No constituye una propuesta de refactorización. Si se quisiera cambiar algo en el futuro, primero habría que preservar contratos públicos, claves de configuración, restricciones de API Android, comportamiento de Compose, lifecycle, operaciones de I/O y compatibilidad con los datos ya persistidos. En esta entrega no se hizo ninguna de esas modificaciones.

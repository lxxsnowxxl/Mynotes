# MainActivity.kt — documentación exhaustiva por bloques

**Ruta de código real:** `app/src/main/java/com/example/mynotes/MainActivity.kt`  
**SHA-256 del archivo ejecutable sin tocar:** `f2e25987e0259f32185847caa829e442b2f764bd91358b5de0fe0cc39b2a3de8`  
**Líneas del código real:** 670

> **Garantía:** esta documentación se genera en una carpeta paralela. El archivo Kotlin/Kotlin DSL anterior no se modifica. Los fragmentos mostrados abajo son copias de lectura; la aplicación compila usando únicamente el archivo original de `app/...`.

## 1. Papel del archivo

Punto de entrada principal de la aplicación. Inicializa la experiencia Compose, observa la configuración global, coordina las pantallas principales y conecta la UI con los ViewModel y con las políticas de ventana/rendimiento.

**Arquitectura.** Actúa como orquestador de alto nivel: no sustituye a los repositorios ni a los componentes visuales, sino que ensambla navegación, estado global, callbacks de edición y configuración, y comportamiento de la ventana.

**Flujo general.** Flujo típico: inicializa dependencias y Compose -> observa notas/ajustes -> decide qué pantalla mostrar -> enruta eventos a los ViewModel/Activities -> reaplica comportamiento de ventana/rendimiento en el ciclo de vida.

## 2. Package e imports

El `package` es `com.example.mynotes`. Determina el namespace lógico del archivo, resolución de visibilidad `internal`/package-related tooling y la ruta conceptual desde la que otras clases lo importan.

El archivo declara **34 imports**. Cada import evita usar el nombre totalmente calificado en el cuerpo y, además, revela las dependencias técnicas del bloque:

**Android/plataforma:** `android.content.Context`, `android.content.Intent`, `android.content.res.Configuration`, `android.os.Build`, `android.os.Bundle`.

**Jetpack/Compose:** `androidx.activity.ComponentActivity`, `androidx.activity.compose.BackHandler`, `androidx.activity.compose.setContent`, `androidx.activity.enableEdgeToEdge`, `androidx.compose.runtime.LaunchedEffect`, `androidx.compose.runtime.getValue`, `androidx.compose.runtime.mutableStateOf`, `androidx.compose.runtime.remember`, `androidx.compose.runtime.setValue`, `androidx.core.view.ViewCompat`, `androidx.core.view.WindowCompat`, `androidx.core.view.WindowInsetsCompat`, `androidx.core.view.WindowInsetsControllerCompat`, `androidx.lifecycle.compose.collectAsStateWithLifecycle`, `androidx.lifecycle.viewmodel.compose.viewModel`.

**Proyecto MyNotes:** `com.example.mynotes.data.Attachment`, `com.example.mynotes.data.Note`, `com.example.mynotes.performance.DisplayPerformanceController`, `com.example.mynotes.ui.NoteDetailScreen`, `com.example.mynotes.ui.NoteEditorScreen`, `com.example.mynotes.ui.NotesScreen`, `com.example.mynotes.ui.motion.AnimatedScreenEntry`, `com.example.mynotes.ui.motion.ConfigurableAnimatedContent`, `com.example.mynotes.ui.SettingsScreen`, `com.example.mynotes.ui.sound.UiSoundPlayer`, `com.example.mynotes.ui.theme.MyNotesTheme`, `com.example.mynotes.viewmodel.NoteViewModel`, `com.example.mynotes.viewmodel.SettingsViewModel`.

**Kotlin/corrutinas/Java:** `java.util.Locale`.

## 3. Restricciones e invariantes visibles en el archivo

- **Nulabilidad segura (9 aparición/apariciones):** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`.
- **Fallback nulo (5 aparición/apariciones):** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula.
- **Aserción no nula (4 aparición/apariciones):** La aserción `!!` exige un valor no nulo en tiempo de ejecución; si la suposición falla se produce una excepción.
- **Filtro condicional (1 aparición/apariciones):** `takeIf` conserva el valor únicamente cuando el predicado se cumple; en caso contrario lo convierte en `null`.
- **Normalización vacía (3 aparición/apariciones):** `orEmpty` transforma una referencia nula en una colección/cadena vacía, simplificando el consumo posterior.
- **Guardia de API (3 aparición/apariciones):** Se condiciona una API según la versión de Android para no ejecutar llamadas incompatibles en dispositivos antiguos.

## 4. Bloques de código, uno por uno

### 4.1 `AppDestination` — class, líneas 38–40

```kotlin
private enum class AppDestination {
    NOTES, SETTINGS, EDITOR, DETAIL
}
```

#### Qué hace y por qué existe

Punto de entrada principal de la aplicación. Inicializa la experiencia Compose, observa la configuración global, coordina las pantallas principales y conecta la UI con los ViewModel y con las políticas de ventana/rendimiento.

#### Contrato de la declaración

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

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

### 4.2 `NavigationSnapshot` — class, líneas 42–670

```kotlin
private data class NavigationSnapshot(val destination: AppDestination, val note: Note? = null)

class MainActivity : ComponentActivity() {
    // … el cuerpo completo permanece en el archivo real; sus miembros se documentan individualmente abajo …
}
```

#### Qué hace y por qué existe

Punto de entrada principal de la aplicación. Inicializa la experiencia Compose, observa la configuración global, coordina las pantallas principales y conecta la UI con los ViewModel y con las políticas de ventana/rendimiento.

#### Contrato de la declaración

**Parámetros del constructor/encabezado:**
- `val destination: AppDestination` — `destination` recibe un valor de tipo `AppDestination`. El contrato no marca este parámetro como anulable. Además, al declararse con `val/var` en el constructor se convierte en propiedad de la instancia.
- `val note: Note? = null` — `note` recibe un valor de tipo `Note?`. El contrato permite `null`; el bloque debe decidir explícita o implícitamente qué significa la ausencia. Tiene valor por defecto `null`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada. Además, al declararse con `val/var` en el constructor se convierte en propiedad de la instancia.
**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 50 | `var wasImeVisible` | `inferido` | `false` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 52 | `val LOCALE_PREFS` | `inferido` | `"locale_prefs"` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Está marcada `const`, por lo que es una constante de tiempo de compilación y solo puede usar un tipo/valor admitido por Kotlin para constantes. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 53 | `val LANGUAGE_KEY` | `inferido` | `"language"` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Está marcada `const`, por lo que es una constante de tiempo de compilación y solo puede usar un tipo/valor admitido por Kotlin para constantes. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 54 | `val DEFAULT_LANGUAGE` | `inferido` | `"es"` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Está marcada `const`, por lo que es una constante de tiempo de compilación y solo puede usar un tipo/valor admitido por Kotlin para constantes. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 68 | `val sharedText` | `inferido` | `incomingIntent.getCharSequenceExtra(Intent.EXTRA_TEXT)?.toString()?.trim().orEmpty()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. Normaliza una ausencia hacia un valor vacío/alternativo para simplificar el código consumidor. |
| 90 | `val preferences` | `inferido` | `newBase.getSharedPreferences(LOCALE_PREFS, Context.MODE_PRIVATE)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 91 | `val language` | `inferido` | `preferences.getString(LANGUAGE_KEY, DEFAULT_LANGUAGE) ?: DEFAULT_LANGUAGE` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 92 | `val locale` | `inferido` | `Locale.forLanguageTag(language)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 94 | `val configuration` | `inferido` | `Configuration(newBase.resources.configuration)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 96 | `val localizedContext` | `inferido` | `newBase.createConfigurationContext(configuration)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El `Context` da acceso a recursos/servicios/almacenamiento Android; debe respetarse el ciclo de vida del contexto recibido. |
| 122 | `val isImeVisible` | `inferido` | `insets.isVisible(WindowInsetsCompat.Type.ime())` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El nombre indica un estado de decisión/visibilidad/selección usado para controlar una rama de comportamiento o presentación. |
| 139 | `val controller` | `inferido` | `WindowCompat.getInsetsController(window, window.decorView)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 146 | `val isMultiWindow` | `inferido` | `Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && isInMultiWindowMode` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El nombre indica un estado de decisión/visibilidad/selección usado para controlar una rama de comportamiento o presentación. |
| 165 | `val controller` | `inferido` | `WindowCompat.getInsetsController(window, window.decorView)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 223 | `val notes` | `inferido` | `by noteViewModel.notes.collectAsStateWithLifecycle()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Convierte un `Flow/StateFlow` en estado de Compose respetando el ciclo de vida, evitando trabajo de colección innecesario cuando la UI no está activa. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 224 | `val settings` | `inferido` | `by settingsViewModel.settings.collectAsStateWithLifecycle()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Convierte un `Flow/StateFlow` en estado de Compose respetando el ciclo de vida, evitando trabajo de colección innecesario cuando la UI no está activa. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 248 | `var showEditor` | `inferido` | `by remember {` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. El nombre indica un estado de decisión/visibilidad/selección usado para controlar una rama de comportamiento o presentación. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 251 | `var showSettings` | `inferido` | `by remember {` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. El nombre indica un estado de decisión/visibilidad/selección usado para controlar una rama de comportamiento o presentación. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 254 | `var selectedNote` | `inferido` | `by remember {` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. El nombre indica un estado de decisión/visibilidad/selección usado para controlar una rama de comportamiento o presentación. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 264 | `var editingNote` | `inferido` | `by remember {` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 310 | `val currentScreen` | `inferido` | `when {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 516 | `val attachmentsFlow` | `inferido` | `remember(screen.note!!.id) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 551 | `val noteBeingEdited` | `inferido` | `screen.note` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 608 | `val currentSelectedNote` | `inferido` | `notes.firstOrNull {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El nombre indica un estado de decisión/visibilidad/selección usado para controlar una rama de comportamiento o presentación. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 65 | `if (incomingIntent?.action != Intent.ACTION_SEND) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 66 | `return` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 69 | `if (sharedText.isBlank()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 70 | `return` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 128 | `if (wasImeVisible && !isImeVisible) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 147 | `if (isMultiWindow) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 158 | `if (Build.VERSION.SDK_INT <` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 167 | `if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 173 | `if (hasFocus) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 225 | `LaunchedEffect(settings.soundEffectsEnabled, settings.soundEffectsVolume, settings.soundEffectsTheme,` | Efecto de Compose basado en corrutina: se inicia cuando entra en composición y se reinicia si cambian sus claves; se cancela al salir de composición. |
| 232 | `LaunchedEffect(settings.darkMode) {` | Efecto de Compose basado en corrutina: se inicia cuando entra en composición y se reinicia si cambian sus claves; se cancela al salir de composición. |
| 240 | `LaunchedEffect(settings.performanceMode) {` | Efecto de Compose basado en corrutina: se inicia cuando entra en composición y se reinicia si cambian sus claves; se cancela al salir de composición. |
| 272 | `LaunchedEffect(pendingSharedText, pendingSharedTitle) {` | Efecto de Compose basado en corrutina: se inicia cuando entra en composición y se reinicia si cambian sus claves; se cancela al salir de composición. |
| 273 | `if (!pendingSharedText.isNullOrBlank()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 292 | `when {` | Selección múltiple de Kotlin: concentra casos mutuamente evaluados y normalmente define una normalización o estrategia según el valor de entrada. |
| 293 | `showSettings -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 296 | `showEditor -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 301 | `selectedNote != null -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 311 | `showSettings -> NavigationSnapshot(AppDestination.SETTINGS)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 312 | `showEditor -> NavigationSnapshot(AppDestination.EDITOR, editingNote)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 313 | `selectedNote != null -> NavigationSnapshot(AppDestination.DETAIL, selectedNote)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 314 | `else -> NavigationSnapshot(AppDestination.NOTES)` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |
| 336 | `when (screen.destination) {` | Selección múltiple de Kotlin: concentra casos mutuamente evaluados y normalmente define una normalización o estrategia según el valor de entrada. |
| 342 | `AppDestination.SETTINGS -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 508 | `AppDestination.EDITOR -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 550 | `title, content, color, attachments, removedAttachments ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 552 | `if (noteBeingEdited == null) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 582 | `attachment ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 602 | `AppDestination.DETAIL -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 619 | `note ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 631 | `AppDestination.NOTES -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 9.
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 5.
- **Aserción no nula:** La aserción `!!` exige un valor no nulo en tiempo de ejecución; si la suposición falla se produce una excepción. Apariciones en este bloque: 4.
- **Filtro condicional:** `takeIf` conserva el valor únicamente cuando el predicado se cumple; en caso contrario lo convierte en `null`. Apariciones en este bloque: 1.
- **Normalización vacía:** `orEmpty` transforma una referencia nula en una colección/cadena vacía, simplificando el consumo posterior. Apariciones en este bloque: 3.
- **Guardia de API:** Se condiciona una API según la versión de Android para no ejecutar llamadas incompatibles en dispositivos antiguos. Apariciones en este bloque: 3.

#### Semántica Compose/lifecycle

- **`remember`:** Conserva valores entre recomposiciones bajo las mismas claves.
- **`mutableStateOf`:** Crea estado observable; las lecturas registradas por Compose quedan enlazadas a sus cambios.
- **`LaunchedEffect`:** Ejecuta una corrutina ligada al ciclo de vida de la composición y a sus claves.
- **`collectAsStateWithLifecycle`:** Observa un flujo de forma consciente del lifecycle y entrega el último valor como estado de Compose.

#### Efectos secundarios y recursos

- **Estado Compose:** Declara o utiliza estado observable de Compose; una escritura en ese estado puede desencadenar recomposición de los lectores.
- **Preferencias/DataStore:** Lee o escribe configuración persistente; el resultado sobrevive a recreaciones de la pantalla y normalmente al reinicio de la app.
- **Navegación/Activity:** Modifica navegación, ciclo de vida o contenido de una Activity.
- **Ventana/sistema:** Interactúa con la ventana/sistema Android; puede cambiar barras, modo de pantalla o atributos de presentación.
- **Sonido/háptica:** Produce feedback perceptible para el usuario; debe ejecutarse en respuesta a la interacción/estado correspondiente.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `ComponentActivity`, `incomingIntent.getCharSequenceExtra`, `toString`, `trim`, `orEmpty`, `sharedText.isBlank`, `it.isNotBlank`, `newBase.getSharedPreferences`, `preferences.getString`, `Locale.forLanguageTag`, `Locale.setDefault`, `Configuration`, `configuration.setLocale`, `newBase.createConfigurationContext`, `super.attachBaseContext`, `getSharedPreferences`, `edit`, `putString`, `commit`, `recreate`, `ViewCompat.setOnApplyWindowInsetsListener`, `insets.isVisible`, `WindowInsetsCompat.Type.ime`, `applyAndroidNavigationBarPolicy`, `ViewCompat.requestApplyInsets`, `WindowCompat.getInsetsController`, `controller.show`, `WindowInsetsCompat.Type.navigationBars`, `controller.hide`, `Suppress`, `super.onWindowFocusChanged`, `super.onResume`, `DisplayPerformanceController.reapplyLastRequest`, `super.onMultiWindowModeChanged`, `DisplayPerformanceController.release`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.
- Conservar las guardas de versión antes de APIs que no existen en Android antiguos.
- No convertir estado de Compose en variables ordinarias sin considerar recomposición y persistencia temporal.
- Preservar la normalización y correspondencia de `performanceMode`, porque otras capas usan las mismas claves para rendimiento/calidad.

### 4.3 `MainActivity` — class, líneas 44–670

```kotlin
class MainActivity : ComponentActivity() {
    // … el cuerpo completo permanece en el archivo real; sus miembros se documentan individualmente abajo …
}
```

#### Qué hace y por qué existe

Punto de entrada principal de la aplicación. Inicializa la experiencia Compose, observa la configuración global, coordina las pantallas principales y conecta la UI con los ViewModel y con las políticas de ventana/rendimiento.

#### Contrato de la declaración


#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 50 | `var wasImeVisible` | `inferido` | `false` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 52 | `val LOCALE_PREFS` | `inferido` | `"locale_prefs"` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Está marcada `const`, por lo que es una constante de tiempo de compilación y solo puede usar un tipo/valor admitido por Kotlin para constantes. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 53 | `val LANGUAGE_KEY` | `inferido` | `"language"` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Está marcada `const`, por lo que es una constante de tiempo de compilación y solo puede usar un tipo/valor admitido por Kotlin para constantes. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 54 | `val DEFAULT_LANGUAGE` | `inferido` | `"es"` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Está marcada `const`, por lo que es una constante de tiempo de compilación y solo puede usar un tipo/valor admitido por Kotlin para constantes. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 68 | `val sharedText` | `inferido` | `incomingIntent.getCharSequenceExtra(Intent.EXTRA_TEXT)?.toString()?.trim().orEmpty()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. Normaliza una ausencia hacia un valor vacío/alternativo para simplificar el código consumidor. |
| 90 | `val preferences` | `inferido` | `newBase.getSharedPreferences(LOCALE_PREFS, Context.MODE_PRIVATE)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 91 | `val language` | `inferido` | `preferences.getString(LANGUAGE_KEY, DEFAULT_LANGUAGE) ?: DEFAULT_LANGUAGE` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 92 | `val locale` | `inferido` | `Locale.forLanguageTag(language)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 94 | `val configuration` | `inferido` | `Configuration(newBase.resources.configuration)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 96 | `val localizedContext` | `inferido` | `newBase.createConfigurationContext(configuration)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El `Context` da acceso a recursos/servicios/almacenamiento Android; debe respetarse el ciclo de vida del contexto recibido. |
| 122 | `val isImeVisible` | `inferido` | `insets.isVisible(WindowInsetsCompat.Type.ime())` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El nombre indica un estado de decisión/visibilidad/selección usado para controlar una rama de comportamiento o presentación. |
| 139 | `val controller` | `inferido` | `WindowCompat.getInsetsController(window, window.decorView)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 146 | `val isMultiWindow` | `inferido` | `Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && isInMultiWindowMode` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El nombre indica un estado de decisión/visibilidad/selección usado para controlar una rama de comportamiento o presentación. |
| 165 | `val controller` | `inferido` | `WindowCompat.getInsetsController(window, window.decorView)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 223 | `val notes` | `inferido` | `by noteViewModel.notes.collectAsStateWithLifecycle()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Convierte un `Flow/StateFlow` en estado de Compose respetando el ciclo de vida, evitando trabajo de colección innecesario cuando la UI no está activa. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 224 | `val settings` | `inferido` | `by settingsViewModel.settings.collectAsStateWithLifecycle()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Convierte un `Flow/StateFlow` en estado de Compose respetando el ciclo de vida, evitando trabajo de colección innecesario cuando la UI no está activa. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 248 | `var showEditor` | `inferido` | `by remember {` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. El nombre indica un estado de decisión/visibilidad/selección usado para controlar una rama de comportamiento o presentación. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 251 | `var showSettings` | `inferido` | `by remember {` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. El nombre indica un estado de decisión/visibilidad/selección usado para controlar una rama de comportamiento o presentación. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 254 | `var selectedNote` | `inferido` | `by remember {` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. El nombre indica un estado de decisión/visibilidad/selección usado para controlar una rama de comportamiento o presentación. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 264 | `var editingNote` | `inferido` | `by remember {` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 310 | `val currentScreen` | `inferido` | `when {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 516 | `val attachmentsFlow` | `inferido` | `remember(screen.note!!.id) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 551 | `val noteBeingEdited` | `inferido` | `screen.note` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 608 | `val currentSelectedNote` | `inferido` | `notes.firstOrNull {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El nombre indica un estado de decisión/visibilidad/selección usado para controlar una rama de comportamiento o presentación. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 65 | `if (incomingIntent?.action != Intent.ACTION_SEND) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 66 | `return` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 69 | `if (sharedText.isBlank()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 70 | `return` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 128 | `if (wasImeVisible && !isImeVisible) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 147 | `if (isMultiWindow) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 158 | `if (Build.VERSION.SDK_INT <` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 167 | `if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 173 | `if (hasFocus) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 225 | `LaunchedEffect(settings.soundEffectsEnabled, settings.soundEffectsVolume, settings.soundEffectsTheme,` | Efecto de Compose basado en corrutina: se inicia cuando entra en composición y se reinicia si cambian sus claves; se cancela al salir de composición. |
| 232 | `LaunchedEffect(settings.darkMode) {` | Efecto de Compose basado en corrutina: se inicia cuando entra en composición y se reinicia si cambian sus claves; se cancela al salir de composición. |
| 240 | `LaunchedEffect(settings.performanceMode) {` | Efecto de Compose basado en corrutina: se inicia cuando entra en composición y se reinicia si cambian sus claves; se cancela al salir de composición. |
| 272 | `LaunchedEffect(pendingSharedText, pendingSharedTitle) {` | Efecto de Compose basado en corrutina: se inicia cuando entra en composición y se reinicia si cambian sus claves; se cancela al salir de composición. |
| 273 | `if (!pendingSharedText.isNullOrBlank()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 292 | `when {` | Selección múltiple de Kotlin: concentra casos mutuamente evaluados y normalmente define una normalización o estrategia según el valor de entrada. |
| 293 | `showSettings -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 296 | `showEditor -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 301 | `selectedNote != null -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 311 | `showSettings -> NavigationSnapshot(AppDestination.SETTINGS)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 312 | `showEditor -> NavigationSnapshot(AppDestination.EDITOR, editingNote)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 313 | `selectedNote != null -> NavigationSnapshot(AppDestination.DETAIL, selectedNote)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 314 | `else -> NavigationSnapshot(AppDestination.NOTES)` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |
| 336 | `when (screen.destination) {` | Selección múltiple de Kotlin: concentra casos mutuamente evaluados y normalmente define una normalización o estrategia según el valor de entrada. |
| 342 | `AppDestination.SETTINGS -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 508 | `AppDestination.EDITOR -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 550 | `title, content, color, attachments, removedAttachments ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 552 | `if (noteBeingEdited == null) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 582 | `attachment ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 602 | `AppDestination.DETAIL -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 619 | `note ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 631 | `AppDestination.NOTES -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 9.
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 5.
- **Aserción no nula:** La aserción `!!` exige un valor no nulo en tiempo de ejecución; si la suposición falla se produce una excepción. Apariciones en este bloque: 4.
- **Filtro condicional:** `takeIf` conserva el valor únicamente cuando el predicado se cumple; en caso contrario lo convierte en `null`. Apariciones en este bloque: 1.
- **Normalización vacía:** `orEmpty` transforma una referencia nula en una colección/cadena vacía, simplificando el consumo posterior. Apariciones en este bloque: 3.
- **Guardia de API:** Se condiciona una API según la versión de Android para no ejecutar llamadas incompatibles en dispositivos antiguos. Apariciones en este bloque: 3.

#### Semántica Compose/lifecycle

- **`remember`:** Conserva valores entre recomposiciones bajo las mismas claves.
- **`mutableStateOf`:** Crea estado observable; las lecturas registradas por Compose quedan enlazadas a sus cambios.
- **`LaunchedEffect`:** Ejecuta una corrutina ligada al ciclo de vida de la composición y a sus claves.
- **`collectAsStateWithLifecycle`:** Observa un flujo de forma consciente del lifecycle y entrega el último valor como estado de Compose.

#### Efectos secundarios y recursos

- **Estado Compose:** Declara o utiliza estado observable de Compose; una escritura en ese estado puede desencadenar recomposición de los lectores.
- **Preferencias/DataStore:** Lee o escribe configuración persistente; el resultado sobrevive a recreaciones de la pantalla y normalmente al reinicio de la app.
- **Navegación/Activity:** Modifica navegación, ciclo de vida o contenido de una Activity.
- **Ventana/sistema:** Interactúa con la ventana/sistema Android; puede cambiar barras, modo de pantalla o atributos de presentación.
- **Sonido/háptica:** Produce feedback perceptible para el usuario; debe ejecutarse en respuesta a la interacción/estado correspondiente.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `ComponentActivity`, `incomingIntent.getCharSequenceExtra`, `toString`, `trim`, `orEmpty`, `sharedText.isBlank`, `it.isNotBlank`, `newBase.getSharedPreferences`, `preferences.getString`, `Locale.forLanguageTag`, `Locale.setDefault`, `Configuration`, `configuration.setLocale`, `newBase.createConfigurationContext`, `super.attachBaseContext`, `getSharedPreferences`, `edit`, `putString`, `commit`, `recreate`, `ViewCompat.setOnApplyWindowInsetsListener`, `insets.isVisible`, `WindowInsetsCompat.Type.ime`, `applyAndroidNavigationBarPolicy`, `ViewCompat.requestApplyInsets`, `WindowCompat.getInsetsController`, `controller.show`, `WindowInsetsCompat.Type.navigationBars`, `controller.hide`, `Suppress`, `super.onWindowFocusChanged`, `super.onResume`, `DisplayPerformanceController.reapplyLastRequest`, `super.onMultiWindowModeChanged`, `DisplayPerformanceController.release`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.
- Conservar las guardas de versión antes de APIs que no existen en Android antiguos.
- No convertir estado de Compose en variables ordinarias sin considerar recomposición y persistencia temporal.
- Preservar la normalización y correspondencia de `performanceMode`, porque otras capas usan las mismas claves para rendimiento/calidad.

### 4.4 `handleIncomingShare` — fun, líneas 64–74

```kotlin
    private fun handleIncomingShare(incomingIntent: Intent?) {
        if (incomingIntent?.action != Intent.ACTION_SEND) {
            return
        }
        val sharedText = incomingIntent.getCharSequenceExtra(Intent.EXTRA_TEXT)?.toString()?.trim().orEmpty()
        if (sharedText.isBlank()) {
            return
        }
        pendingSharedText = sharedText
        pendingSharedTitle = incomingIntent.getCharSequenceExtra(Intent.EXTRA_SUBJECT)?.toString()?.trim()?.takeIf { it.isNotBlank() }
    }
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `incomingIntent: Intent?` — `incomingIntent` recibe un valor de tipo `Intent?`. El contrato permite `null`; el bloque debe decidir explícita o implícitamente qué significa la ausencia. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 68 | `val sharedText` | `inferido` | `incomingIntent.getCharSequenceExtra(Intent.EXTRA_TEXT)?.toString()?.trim().orEmpty()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. Normaliza una ausencia hacia un valor vacío/alternativo para simplificar el código consumidor. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 65 | `if (incomingIntent?.action != Intent.ACTION_SEND) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 66 | `return` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 69 | `if (sharedText.isBlank()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 70 | `return` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 6.
- **Filtro condicional:** `takeIf` conserva el valor únicamente cuando el predicado se cumple; en caso contrario lo convierte en `null`. Apariciones en este bloque: 1.
- **Normalización vacía:** `orEmpty` transforma una referencia nula en una colección/cadena vacía, simplificando el consumo posterior. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `incomingIntent.getCharSequenceExtra`, `toString`, `trim`, `orEmpty`, `sharedText.isBlank`, `it.isNotBlank`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.5 `clearPendingShare` — fun, líneas 75–78

```kotlin
    private fun clearPendingShare() {
        pendingSharedText = null
        pendingSharedTitle = null
    }
```

#### Qué hace y por qué existe

Elimina o invalida el estado/recurso indicado, incluyendo las limpiezas auxiliares previstas por el bloque.

#### Contrato de la declaración

**Parámetros:** ninguno explícito.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

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

### 4.6 `attachBaseContext` — fun, líneas 89–98

```kotlin
    override fun attachBaseContext(newBase: Context) {
        val preferences = newBase.getSharedPreferences(LOCALE_PREFS, Context.MODE_PRIVATE)
        val language = preferences.getString(LANGUAGE_KEY, DEFAULT_LANGUAGE) ?: DEFAULT_LANGUAGE
        val locale = Locale.forLanguageTag(language)
        Locale.setDefault(locale)
        val configuration = Configuration(newBase.resources.configuration)
        configuration.setLocale(locale)
        val localizedContext = newBase.createConfigurationContext(configuration)
        super.attachBaseContext(localizedContext)
    }
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `newBase: Context` — `newBase` recibe un valor de tipo `Context`. El contrato no marca este parámetro como anulable. Da acceso a recursos y APIs Android; su vida útil debe corresponder con el uso realizado.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `override` indica que el contrato viene de una superclase/interfaz; la firma debe respetar el método heredado.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 90 | `val preferences` | `inferido` | `newBase.getSharedPreferences(LOCALE_PREFS, Context.MODE_PRIVATE)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 91 | `val language` | `inferido` | `preferences.getString(LANGUAGE_KEY, DEFAULT_LANGUAGE) ?: DEFAULT_LANGUAGE` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 92 | `val locale` | `inferido` | `Locale.forLanguageTag(language)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 94 | `val configuration` | `inferido` | `Configuration(newBase.resources.configuration)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 96 | `val localizedContext` | `inferido` | `newBase.createConfigurationContext(configuration)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El `Context` da acceso a recursos/servicios/almacenamiento Android; debe respetarse el ciclo de vida del contexto recibido. |

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

**Restricciones concretas que aparecen en este bloque:**
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `newBase.getSharedPreferences`, `preferences.getString`, `Locale.forLanguageTag`, `Locale.setDefault`, `Configuration`, `configuration.setLocale`, `newBase.createConfigurationContext`, `super.attachBaseContext`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.7 `changeAppLanguage` — fun, líneas 99–110

```kotlin
    private fun changeAppLanguage(language: String) {
        /*
         * Guardamos también en SharedPreferences porque
         * attachBaseContext() ocurre antes de que DataStore
         * pueda entregar AppSettings.
         *
         * commit() es intencional: necesitamos que el idioma
         * ya esté guardado antes de recreate().
         */
        getSharedPreferences(LOCALE_PREFS, Context.MODE_PRIVATE).edit().putString(LANGUAGE_KEY, language).commit()
        recreate()
    }
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `language: String` — `language` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

- **Preferencias/DataStore:** Lee o escribe configuración persistente; el resultado sobrevive a recreaciones de la pantalla y normalmente al reinicio de la app.
- **Navegación/Activity:** Modifica navegación, ciclo de vida o contenido de una Activity.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `getSharedPreferences`, `edit`, `putString`, `commit`, `recreate`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.8 `installImeNavigationBarRecovery` — fun, líneas 120–137

```kotlin
    private fun installImeNavigationBarRecovery() {
        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { view, insets ->
            val isImeVisible = insets.isVisible(WindowInsetsCompat.Type.ime())
            /*
             * No ocultamos la navegación mientras el teclado está abierto.
             * Solo actuamos en la transición visible -> oculto, que es el
             * caso en el que Android/Samsung deja los tres botones en pantalla.
             */
            if (wasImeVisible && !isImeVisible) {
                view.post {
                    applyAndroidNavigationBarPolicy()
                }
            }
            wasImeVisible = isImeVisible
            insets
        }
        ViewCompat.requestApplyInsets(window.decorView)
    }
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:** ninguno explícito.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 122 | `val isImeVisible` | `inferido` | `insets.isVisible(WindowInsetsCompat.Type.ime())` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El nombre indica un estado de decisión/visibilidad/selección usado para controlar una rama de comportamiento o presentación. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 128 | `if (wasImeVisible && !isImeVisible) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |

#### Efectos secundarios y recursos

- **Ventana/sistema:** Interactúa con la ventana/sistema Android; puede cambiar barras, modo de pantalla o atributos de presentación.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `ViewCompat.setOnApplyWindowInsetsListener`, `insets.isVisible`, `WindowInsetsCompat.Type.ime`, `applyAndroidNavigationBarPolicy`, `ViewCompat.requestApplyInsets`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.9 `applyAndroidNavigationBarPolicy` — fun, líneas 138–163

```kotlin
    private fun applyAndroidNavigationBarPolicy() {
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        /*
         * En pantalla completa conservamos el modo inmersivo. En modo
         * multiventana (disponible desde Android 7) dejamos visible la barra
         * del sistema: ocultarla dentro de split-screen/desktop windowing
         * produce saltos de tamaño y controles inaccesibles en algunos OEM.
         */
        val isMultiWindow = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && isInMultiWindowMode
        if (isMultiWindow) {
            controller.show(WindowInsetsCompat.Type.navigationBars())
        } else {
            controller.hide(WindowInsetsCompat.Type.navigationBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        /*
         * En Android 7.0/7.1 no existe el modo de iconos oscuros para la
         * barra de navegación. Cuando el sistema la muestra usamos negro
         * para garantizar contraste con los botones blancos.
         */
        if (Build.VERSION.SDK_INT <
            Build.VERSION_CODES.O) {
            @Suppress("DEPRECATION")
            window.navigationBarColor = android.graphics.Color.BLACK
        }
    }
```

#### Qué hace y por qué existe

Aplica una política/configuración calculada sobre el objeto o sistema destino.

#### Contrato de la declaración

**Parámetros:** ninguno explícito.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 139 | `val controller` | `inferido` | `WindowCompat.getInsetsController(window, window.decorView)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 146 | `val isMultiWindow` | `inferido` | `Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && isInMultiWindowMode` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El nombre indica un estado de decisión/visibilidad/selección usado para controlar una rama de comportamiento o presentación. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 147 | `if (isMultiWindow) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 158 | `if (Build.VERSION.SDK_INT <` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |

**Restricciones concretas que aparecen en este bloque:**
- **Guardia de API:** Se condiciona una API según la versión de Android para no ejecutar llamadas incompatibles en dispositivos antiguos. Apariciones en este bloque: 2.

#### Efectos secundarios y recursos

- **Ventana/sistema:** Interactúa con la ventana/sistema Android; puede cambiar barras, modo de pantalla o atributos de presentación.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `WindowCompat.getInsetsController`, `controller.show`, `WindowInsetsCompat.Type.navigationBars`, `controller.hide`, `Suppress`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar las guardas de versión antes de APIs que no existen en Android antiguos.

### 4.10 `applySystemBarAppearance` — fun, líneas 164–170

```kotlin
    private fun applySystemBarAppearance(darkMode: Boolean) {
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        controller.isAppearanceLightStatusBars = !darkMode
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            controller.isAppearanceLightNavigationBars = !darkMode
        }
    }
```

#### Qué hace y por qué existe

Aplica una política/configuración calculada sobre el objeto o sistema destino.

#### Contrato de la declaración

**Parámetros:**

- `darkMode: Boolean` — `darkMode` recibe un valor de tipo `Boolean`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 165 | `val controller` | `inferido` | `WindowCompat.getInsetsController(window, window.decorView)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 167 | `if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |

**Restricciones concretas que aparecen en este bloque:**
- **Guardia de API:** Se condiciona una API según la versión de Android para no ejecutar llamadas incompatibles en dispositivos antiguos. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

- **Ventana/sistema:** Interactúa con la ventana/sistema Android; puede cambiar barras, modo de pantalla o atributos de presentación.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `WindowCompat.getInsetsController`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar las guardas de versión antes de APIs que no existen en Android antiguos.

### 4.11 `onWindowFocusChanged` — fun, líneas 171–176

```kotlin
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            applyAndroidNavigationBarPolicy()
        }
    }
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `hasFocus: Boolean` — `hasFocus` recibe un valor de tipo `Boolean`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `override` indica que el contrato viene de una superclase/interfaz; la firma debe respetar el método heredado.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 173 | `if (hasFocus) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `super.onWindowFocusChanged`, `applyAndroidNavigationBarPolicy`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.12 `onResume` — fun, líneas 177–186

```kotlin
    override fun onResume() {
        super.onResume()
        /*
         * DisplayPerformanceController conserva el último perfil aplicado.
         * Reaplicamos esa preferencia al volver a primer plano sin duplicar
         * aquí ninguna regla de frecuencia de refresco.
         */
        DisplayPerformanceController.reapplyLastRequest(window)
        applyAndroidNavigationBarPolicy()
    }
```

#### Qué hace y por qué existe

Callback de ciclo de vida ejecutado cuando la Activity vuelve al primer plano; reaplica políticas que el sistema puede haber alterado mientras estaba inactiva.

#### Contrato de la declaración

**Parámetros:** ninguno explícito.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `override` indica que el contrato viene de una superclase/interfaz; la firma debe respetar el método heredado.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `super.onResume`, `DisplayPerformanceController.reapplyLastRequest`, `applyAndroidNavigationBarPolicy`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.13 `onMultiWindowModeChanged` — fun, líneas 187–190

```kotlin
    override fun onMultiWindowModeChanged(isInMultiWindowMode: Boolean) {
        super.onMultiWindowModeChanged(isInMultiWindowMode)
        applyAndroidNavigationBarPolicy()
    }
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `isInMultiWindowMode: Boolean` — `isInMultiWindowMode` recibe un valor de tipo `Boolean`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `override` indica que el contrato viene de una superclase/interfaz; la firma debe respetar el método heredado.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `super.onMultiWindowModeChanged`, `applyAndroidNavigationBarPolicy`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.14 `onDestroy` — fun, líneas 191–194

```kotlin
    override fun onDestroy() {
        DisplayPerformanceController.release(window)
        super.onDestroy()
    }
```

#### Qué hace y por qué existe

Callback final de la Activity; libera o desvincula recursos asociados a esta instancia.

#### Contrato de la declaración

**Parámetros:** ninguno explícito.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `override` indica que el contrato viene de una superclase/interfaz; la firma debe respetar el método heredado.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `DisplayPerformanceController.release`, `super.onDestroy`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.15 `onNewIntent` — fun, líneas 195–199

```kotlin
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIncomingShare(intent)
    }
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `intent: Intent` — `intent` recibe un valor de tipo `Intent`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `override` indica que el contrato viene de una superclase/interfaz; la firma debe respetar el método heredado.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `super.onNewIntent`, `setIntent`, `handleIncomingShare`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.16 `onCreate` — fun, líneas 200–669

```kotlin
    override fun onCreate(savedInstanceState: Bundle?) {
        /*
         * El sistema ya mostró Theme.MyNotes.Starting mientras
         * el proceso arrancaba. Ahora cambiamos al tema normal
         * antes de crear la Activity.
         */
        setTheme(R.style.Theme_MyNotes)
        super.onCreate(savedInstanceState)
        handleIncomingShare(intent)
        /*
         * La frecuencia de refresco se aplica únicamente cuando AppSettings
         * entrega performanceMode. MainActivity no conoce valores concretos
         * de Hz; toda esa política vive en DisplayPerformanceController.
         * Compose ya sincroniza el renderizado con VSYNC.
         */
        enableEdgeToEdge()
        installImeNavigationBarRecovery()
        applyAndroidNavigationBarPolicy()
        setContent {
            val noteViewModel:
                    NoteViewModel = viewModel()
            val settingsViewModel:
                    SettingsViewModel = viewModel()
            val notes by noteViewModel.notes.collectAsStateWithLifecycle()
            val settings by settingsViewModel.settings.collectAsStateWithLifecycle()
            LaunchedEffect(settings.soundEffectsEnabled, settings.soundEffectsVolume, settings.soundEffectsTheme,
                settings.hapticEffectsEnabled, settings.hapticEffectsIntensity, settings.hapticEffectsStyle) {
                UiSoundPlayer.configure(context = this@MainActivity, enabled = settings.soundEffectsEnabled,
                    volumePercent = settings.soundEffectsVolume, theme = settings.soundEffectsTheme,
                    hapticEnabled = settings.hapticEffectsEnabled, hapticIntensityPercent = settings.hapticEffectsIntensity,
                    hapticStyle = settings.hapticEffectsStyle)
            }
            LaunchedEffect(settings.darkMode) {
                applySystemBarAppearance(settings.darkMode)
            }
            /*
             * El controlador traduce el perfil seleccionado a la frecuencia
             * adecuada y escoge el modo compatible sin cambiar
             * voluntariamente la resolución física.
             */
            LaunchedEffect(settings.performanceMode) {
                DisplayPerformanceController.requestForPerformanceMode(window = window, performanceMode = settings.performanceMode)
            }
            /*
             * ==========================================
             * NAVEGACIÓN
             * ==========================================
             */
            var showEditor by remember {
                mutableStateOf(false)
            }
            var showSettings by remember {
                mutableStateOf(false)
            }
            var selectedNote by remember {
                mutableStateOf<Note?>(null)
            }
            /*
             * null:
             * crear nota.
             *
             * Note:
             * editar nota existente.
             */
            var editingNote by remember {
                mutableStateOf<Note?>(null)
            }
            /*
             * Cuando llega un enlace mediante Compartir, abrimos una
             * nueva nota con el texto recibido. También funciona si la
             * Activity ya estaba abierta gracias a onNewIntent().
             */
            LaunchedEffect(pendingSharedText, pendingSharedTitle) {
                if (!pendingSharedText.isNullOrBlank()) {
                    showSettings = false
                    selectedNote = null
                    editingNote = null
                    showEditor = true
                }
            }
            /*
             * ==========================================
             * BOTÓN BACK DE ANDROID
             * ==========================================
             *
             * Mientras estemos en Ajustes, Editor o
             * Detalle, Back regresa a "Mis notas".
             *
             * Solo cuando ya estamos en "Mis notas",
             * Android puede cerrar la aplicación.
             */
            BackHandler(enabled = showSettings || showEditor || selectedNote != null) {
                when {
                    showSettings -> {
                        showSettings = false
                    }
                    showEditor -> {
                        editingNote = null
                        clearPendingShare()
                        showEditor = false
                    }
                    selectedNote != null -> {
                        selectedNote = null
                    }
                }
            }
            /*
             * Guardamos la nota en el estado de navegación para que la
             * pantalla saliente conserve sus datos durante el Zoom Out.
             */
            val currentScreen = when {
                    showSettings -> NavigationSnapshot(AppDestination.SETTINGS)
                    showEditor -> NavigationSnapshot(AppDestination.EDITOR, editingNote)
                    selectedNote != null -> NavigationSnapshot(AppDestination.DETAIL, selectedNote)
                    else -> NavigationSnapshot(AppDestination.NOTES)
                }
            MyNotesTheme(darkTheme = settings.darkMode,
                backgroundColor = settings.backgroundColor,
                backgroundToneIndex = settings.backgroundToneIndex,
                backgroundIntensity = settings.backgroundIntensity,
                surfacePanelIntensity = settings.surfacePanelIntensity,
                headerIntensity = settings.headerIntensity,
                /*
                 * auto adapta el texto al contraste del fondo; negro/blanco
                 * siguen siendo anulaciones manuales persistentes.
                 */
                textColor = settings.textColor,
                textOutlineEnabled = settings.textOutlineEnabled,
                accentColor = settings.accentColor) {
                ConfigurableAnimatedContent(targetState = currentScreen,
                    animationsEnabled = settings.animationsEnabled,
                    animationSpeed = settings.animationSpeed,
                    animationStyle = settings.animationStyle,
                    animationEasing = settings.animationEasing,
                    animationIntensity = settings.animationIntensity,
                    performanceMode = settings.performanceMode) { screen ->
                    when (screen.destination) {
                    /*
                     * ==========================================
                     * AJUSTES
                     * ==========================================
                     */
                    AppDestination.SETTINGS -> {
                        SettingsScreen(
                            settings = settings,
                            onDarkModeChange = {
                                settingsViewModel.setDarkMode(it)
                            },
                            onBackgroundColorChange = {
                                settingsViewModel.setBackgroundColor(it)
                            },
                            onBackgroundToneIndexChange = {
                                settingsViewModel.setBackgroundToneIndex(it)
                            },
                            onBackgroundIntensityChange = {
                                settingsViewModel.setBackgroundIntensity(it)
                            },
                            onSettingsPanelToneChange = {
                                settingsViewModel.setSettingsPanelTone(it)
                            },
                            onSurfacePanelIntensityChange = {
                                settingsViewModel.setSurfacePanelIntensity(it)
                            },
                            onHeaderIntensityChange = {
                                settingsViewModel.setHeaderIntensity(it)
                            },
                            onTextColorChange = {
                                settingsViewModel.setTextColor(it)
                            },
                            onTextOutlineEnabledChange = {
                                settingsViewModel.setTextOutlineEnabled(it)
                            },
                            onNoteUiTextColorChange = {
                                settingsViewModel.setNoteUiTextColor(it)
                            },
                            onSliderStyleChange = {
                                settingsViewModel.setSliderStyle(it)
                            },
                            onFontChange = {
                                settingsViewModel.setFont(it)
                            },
                            onFontSizeChange = {
                                settingsViewModel.setFontSize(it)
                            },
                            onSoundEffectsEnabledChange = {
                                settingsViewModel.setSoundEffectsEnabled(it)
                            },
                            onSoundEffectsVolumeChange = {
                                settingsViewModel.setSoundEffectsVolume(it)
                            },
                            onSoundEffectsThemeChange = {
                                settingsViewModel.setSoundEffectsTheme(it)
                            },
                            onHapticEffectsEnabledChange = {
                                settingsViewModel.setHapticEffectsEnabled(it)
                            },
                            onHapticEffectsIntensityChange = {
                                settingsViewModel.setHapticEffectsIntensity(it)
                            },
                            onHapticEffectsStyleChange = {
                                settingsViewModel.setHapticEffectsStyle(it)
                            },
                            onLanguageChange = { language ->
                                settingsViewModel.setLanguage(language)
                                changeAppLanguage(language)
                            },
                            onGridColumnsChange = {
                                settingsViewModel.setGridColumns(it)
                            },
                            onProfileImageUriChange = {
                                settingsViewModel.setProfileImageUri(it)
                            },
                            onProfileImageSizeChange = {
                                settingsViewModel.setProfileImageSize(it)
                            },
                            onIconStyleChange = {
                                settingsViewModel.setIconStyle(it)
                            },
                            onIconSizeChange = {
                                settingsViewModel.setIconSize(it)
                            },
                            onAccentColorChange = {
                                settingsViewModel.setAccentColor(it)
                            },
                            onNoteCardCornerRadiusChange = {
                                settingsViewModel.setNoteCardCornerRadius(it)
                            },
                            onNoteCardElevationChange = {
                                settingsViewModel.setNoteCardElevation(it)
                            },
                            onNoteCardPaddingChange = {
                                settingsViewModel.setNoteCardPadding(it)
                            },
                            onNoteCardImageHeightChange = {
                                settingsViewModel.setNoteCardImageHeight(it)
                            },
                            onNoteTitleMaxLinesChange = {
                                settingsViewModel.setNoteTitleMaxLines(it)
                            },
                            onNoteContentMaxLinesChange = {
                                settingsViewModel.setNoteContentMaxLines(it)
                            },
                            onNoteLineSpacingChange = {
                                settingsViewModel.setNoteLineSpacing(it)
                            },
                            onShowNoteDateChange = {
                                settingsViewModel.setShowNoteDate(it)
                            },
                            onShowCategoryChipChange = {
                                settingsViewModel.setShowCategoryChip(it)
                            },
                            onShowFavoriteIconChange = {
                                settingsViewModel.setShowFavoriteIcon(it)
                            },
                            onFabSizeChange = {
                                settingsViewModel.setFabSize(it)
                            },
                            onOptionMenuOrderChange = {
                                settingsViewModel.setOptionMenuOrder(it)
                            },
                            onOptionMenuHiddenItemsChange = {
                                settingsViewModel.setOptionMenuHiddenItems(it)
                            },
                            onOptionMenuShowIconsChange = {
                                settingsViewModel.setOptionMenuShowIcons(it)
                            },
                            onOptionMenuTextColorChange = {
                                settingsViewModel.setOptionMenuTextColor(it)
                            },
                            onOptionMenuOpacityChange = {
                                settingsViewModel.setOptionMenuOpacity(it)
                            },
                            onPriorityMenuHiddenItemsChange = {
                                settingsViewModel.setPriorityMenuHiddenItems(it)
                            },
                            onColorMenuHiddenItemsChange = {
                                settingsViewModel.setColorMenuHiddenItems(it)
                            },
                            onResetOptionMenu = {
                                settingsViewModel.resetOptionMenuSettings()
                            },
                            onPerformanceModeChange = {
                                settingsViewModel.setPerformanceMode(it)
                            },
                            onAnimationsEnabledChange = {
                                settingsViewModel.setAnimationsEnabled(it)
                            },
                            onAnimationStyleChange = {
                                settingsViewModel.setAnimationStyle(it)
                            },
                            onAnimationEasingChange = {
                                settingsViewModel.setAnimationEasing(it)
                            },
                            onAnimationSpeedChange = {
                                settingsViewModel.setAnimationSpeed(it)
                            },
                            onAnimationIntensityChange = {
                                settingsViewModel.setAnimationIntensity(it)
                            },
                            onBack = {
                                showSettings = false
                            })
                    }
                    /*
                     * ==========================================
                     * EDITOR
                     * ==========================================
                     */
                    AppDestination.EDITOR -> {
                        /*
                         * Si estamos editando, escuchamos los adjuntos
                         * que ya pertenecen a esa nota para mostrarlos
                         * dentro del editor.
                         */
                        val existingAttachments:
                                List<Attachment> = if (screen.note != null) {
                                val attachmentsFlow = remember(screen.note!!.id) {
                                        noteViewModel.getAttachments(screen.note!!.id)
                                    }
                                val currentAttachments by
                                    attachmentsFlow.collectAsStateWithLifecycle(initialValue = emptyList())
                                currentAttachments
                            } else {
                                emptyList()
                            }
                        AnimatedScreenEntry(animationsEnabled = settings.animationsEnabled,
                            animationSpeed = settings.animationSpeed) {
                            NoteEditorScreen(
                            settings = settings,
                            initialTitle = screen.note?.title?: pendingSharedTitle.orEmpty(),
                            initialContent = screen.note?.content?: pendingSharedText.orEmpty(),
                            initialColor = screen.note?.color?: "default",
                            isEditing = screen.note != null,
                            existingAttachments = existingAttachments,
                            /*
                             * attachments contiene únicamente
                             * adjuntos NUEVOS agregados durante
                             * esta edición.
                             *
                             * List<PendingAttachment>
                             *
                             * y puede contener:
                             *
                             * image
                             * video
                             * audio
                             * voice
                             * file
                             */
                            onSave = {
                                    title, content, color, attachments, removedAttachments ->
                                val noteBeingEdited = screen.note
                                if (noteBeingEdited == null) {
                                    /*
                                     * ==========================
                                     * NUEVA NOTA
                                     * ==========================
                                     */
                                    noteViewModel.addNote(title = title,
                                            content = content,
                                            color = color,
                                            attachments = attachments)
                                } else {
                                    /*
                                     * ==========================
                                     * EDITAR NOTA
                                     * ==========================
                                     *
                                     * Conserva los adjuntos
                                     * anteriores y agrega
                                     * los nuevos.
                                     */
                                    noteViewModel.updateNote(note = noteBeingEdited,
                                            title = title,
                                            content = content,
                                            color = color,
                                            newAttachments = attachments)
                                    /*
                                     * Eliminamos únicamente los adjuntos
                                     * existentes que el usuario marcó con X.
                                     */
                                    removedAttachments.forEach {
                                                attachment ->
                                            noteViewModel.deleteAttachment(attachment)
                                        }
                                }
                                editingNote = null
                                clearPendingShare()
                                showEditor = false
                            },
                            onCancel = {
                                editingNote = null
                                clearPendingShare()
                                showEditor = false
                            })
                        }
                    }
                    /*
                     * ==========================================
                     * DETALLE DE NOTA
                     * ==========================================
                     */
                    AppDestination.DETAIL -> {
                        /*
                         * Usamos la instancia más reciente de Room para
                         * reflejar Favorite / Pin / Category / Priority
                         * sin salir de la pantalla de detalle.
                         */
                        val currentSelectedNote = notes.firstOrNull {
                                    it.id == screen.note!!.id
                                }?: screen.note!!
                        NoteDetailScreen(
                            note = currentSelectedNote,
                            noteViewModel = noteViewModel,
                            settings = settings,
                            onBack = {
                                selectedNote = null
                            },
                            onEdit = {
                                    note ->
                                clearPendingShare()
                                editingNote = note
                                selectedNote = null
                                showEditor = true
                            })
                    }
                    /*
                     * ==========================================
                     * PANTALLA PRINCIPAL
                     * ==========================================
                     */
                    AppDestination.NOTES -> {
                        NotesScreen(
                            notes = notes,
                            noteViewModel = noteViewModel,
                            settings = settings,
                            /*
                             * Nueva nota.
                             */
                            onAddNote = {
                                clearPendingShare()
                                editingNote = null
                                showEditor = true
                            },
                            /*
                             * Ajustes.
                             */
                            onOpenSettings = {
                                showSettings = true
                            },
                            /*
                             * Abrir nota.
                             */
                            onOpenNote = { note ->
                                selectedNote = note
                            },
                            /*
                             * Editar desde ⋮.
                             */
                            onEditNote = { note ->
                                clearPendingShare()
                                editingNote = note
                                showEditor = true
                            })
                    }
                }
                }
            }
        }
    }
```

#### Qué hace y por qué existe

Punto de entrada de creación de la Activity: prepara estado, integra dependencias de UI y configura el contenido inicial según la lógica del proyecto.

#### Contrato de la declaración

**Parámetros:**

- `savedInstanceState: Bundle?` — `savedInstanceState` recibe un valor de tipo `Bundle?`. El contrato permite `null`; el bloque debe decidir explícita o implícitamente qué significa la ausencia.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `override` indica que el contrato viene de una superclase/interfaz; la firma debe respetar el método heredado.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 223 | `val notes` | `inferido` | `by noteViewModel.notes.collectAsStateWithLifecycle()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Convierte un `Flow/StateFlow` en estado de Compose respetando el ciclo de vida, evitando trabajo de colección innecesario cuando la UI no está activa. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 224 | `val settings` | `inferido` | `by settingsViewModel.settings.collectAsStateWithLifecycle()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Convierte un `Flow/StateFlow` en estado de Compose respetando el ciclo de vida, evitando trabajo de colección innecesario cuando la UI no está activa. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 248 | `var showEditor` | `inferido` | `by remember {` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. El nombre indica un estado de decisión/visibilidad/selección usado para controlar una rama de comportamiento o presentación. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 251 | `var showSettings` | `inferido` | `by remember {` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. El nombre indica un estado de decisión/visibilidad/selección usado para controlar una rama de comportamiento o presentación. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 254 | `var selectedNote` | `inferido` | `by remember {` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. El nombre indica un estado de decisión/visibilidad/selección usado para controlar una rama de comportamiento o presentación. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 264 | `var editingNote` | `inferido` | `by remember {` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 310 | `val currentScreen` | `inferido` | `when {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 516 | `val attachmentsFlow` | `inferido` | `remember(screen.note!!.id) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 551 | `val noteBeingEdited` | `inferido` | `screen.note` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 608 | `val currentSelectedNote` | `inferido` | `notes.firstOrNull {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El nombre indica un estado de decisión/visibilidad/selección usado para controlar una rama de comportamiento o presentación. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 225 | `LaunchedEffect(settings.soundEffectsEnabled, settings.soundEffectsVolume, settings.soundEffectsTheme,` | Efecto de Compose basado en corrutina: se inicia cuando entra en composición y se reinicia si cambian sus claves; se cancela al salir de composición. |
| 232 | `LaunchedEffect(settings.darkMode) {` | Efecto de Compose basado en corrutina: se inicia cuando entra en composición y se reinicia si cambian sus claves; se cancela al salir de composición. |
| 240 | `LaunchedEffect(settings.performanceMode) {` | Efecto de Compose basado en corrutina: se inicia cuando entra en composición y se reinicia si cambian sus claves; se cancela al salir de composición. |
| 272 | `LaunchedEffect(pendingSharedText, pendingSharedTitle) {` | Efecto de Compose basado en corrutina: se inicia cuando entra en composición y se reinicia si cambian sus claves; se cancela al salir de composición. |
| 273 | `if (!pendingSharedText.isNullOrBlank()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 292 | `when {` | Selección múltiple de Kotlin: concentra casos mutuamente evaluados y normalmente define una normalización o estrategia según el valor de entrada. |
| 293 | `showSettings -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 296 | `showEditor -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 301 | `selectedNote != null -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 311 | `showSettings -> NavigationSnapshot(AppDestination.SETTINGS)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 312 | `showEditor -> NavigationSnapshot(AppDestination.EDITOR, editingNote)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 313 | `selectedNote != null -> NavigationSnapshot(AppDestination.DETAIL, selectedNote)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 314 | `else -> NavigationSnapshot(AppDestination.NOTES)` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |
| 336 | `when (screen.destination) {` | Selección múltiple de Kotlin: concentra casos mutuamente evaluados y normalmente define una normalización o estrategia según el valor de entrada. |
| 342 | `AppDestination.SETTINGS -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 508 | `AppDestination.EDITOR -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 550 | `title, content, color, attachments, removedAttachments ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 552 | `if (noteBeingEdited == null) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 582 | `attachment ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 602 | `AppDestination.DETAIL -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 619 | `note ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 631 | `AppDestination.NOTES -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 3.
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 4.
- **Aserción no nula:** La aserción `!!` exige un valor no nulo en tiempo de ejecución; si la suposición falla se produce una excepción. Apariciones en este bloque: 4.
- **Normalización vacía:** `orEmpty` transforma una referencia nula en una colección/cadena vacía, simplificando el consumo posterior. Apariciones en este bloque: 2.

#### Semántica Compose/lifecycle

- **`remember`:** Conserva valores entre recomposiciones bajo las mismas claves.
- **`mutableStateOf`:** Crea estado observable; las lecturas registradas por Compose quedan enlazadas a sus cambios.
- **`LaunchedEffect`:** Ejecuta una corrutina ligada al ciclo de vida de la composición y a sus claves.
- **`collectAsStateWithLifecycle`:** Observa un flujo de forma consciente del lifecycle y entrega el último valor como estado de Compose.

#### Efectos secundarios y recursos

- **Estado Compose:** Declara o utiliza estado observable de Compose; una escritura en ese estado puede desencadenar recomposición de los lectores.
- **Sonido/háptica:** Produce feedback perceptible para el usuario; debe ejecutarse en respuesta a la interacción/estado correspondiente.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `setTheme`, `super.onCreate`, `handleIncomingShare`, `enableEdgeToEdge`, `installImeNavigationBarRecovery`, `applyAndroidNavigationBarPolicy`, `viewModel`, `noteViewModel.notes.collectAsStateWithLifecycle`, `settingsViewModel.settings.collectAsStateWithLifecycle`, `LaunchedEffect`, `UiSoundPlayer.configure`, `applySystemBarAppearance`, `DisplayPerformanceController.requestForPerformanceMode`, `mutableStateOf`, `pendingSharedText.isNullOrBlank`, `BackHandler`, `clearPendingShare`, `NavigationSnapshot`, `MyNotesTheme`, `ConfigurableAnimatedContent`, `SettingsScreen`, `settingsViewModel.setDarkMode`, `settingsViewModel.setBackgroundColor`, `settingsViewModel.setBackgroundToneIndex`, `settingsViewModel.setBackgroundIntensity`, `settingsViewModel.setSettingsPanelTone`, `settingsViewModel.setSurfacePanelIntensity`, `settingsViewModel.setHeaderIntensity`, `settingsViewModel.setTextColor`, `settingsViewModel.setTextOutlineEnabled`, `settingsViewModel.setNoteUiTextColor`, `settingsViewModel.setSliderStyle`, `settingsViewModel.setFont`, `settingsViewModel.setFontSize`, `settingsViewModel.setSoundEffectsEnabled`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.
- No convertir estado de Compose en variables ordinarias sin considerar recomposición y persistencia temporal.
- Preservar la normalización y correspondencia de `performanceMode`, porque otras capas usan las mismas claves para rendimiento/calidad.

## 5. Inventario global de propiedades/variables detectadas

| Línea | Identificador | Mutabilidad | Tipo | Explicación |
|---:|---|---|---|---|
| 50 | `wasImeVisible` | `var` | `inferido` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 52 | `LOCALE_PREFS` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Está marcada `const`, por lo que es una constante de tiempo de compilación y solo puede usar un tipo/valor admitido por Kotlin para constantes. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 53 | `LANGUAGE_KEY` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Está marcada `const`, por lo que es una constante de tiempo de compilación y solo puede usar un tipo/valor admitido por Kotlin para constantes. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 54 | `DEFAULT_LANGUAGE` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Está marcada `const`, por lo que es una constante de tiempo de compilación y solo puede usar un tipo/valor admitido por Kotlin para constantes. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 68 | `sharedText` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. Normaliza una ausencia hacia un valor vacío/alternativo para simplificar el código consumidor. |
| 90 | `preferences` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 91 | `language` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 92 | `locale` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 94 | `configuration` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 96 | `localizedContext` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El `Context` da acceso a recursos/servicios/almacenamiento Android; debe respetarse el ciclo de vida del contexto recibido. |
| 122 | `isImeVisible` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El nombre indica un estado de decisión/visibilidad/selección usado para controlar una rama de comportamiento o presentación. |
| 139 | `controller` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 146 | `isMultiWindow` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El nombre indica un estado de decisión/visibilidad/selección usado para controlar una rama de comportamiento o presentación. |
| 165 | `controller` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 223 | `notes` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Convierte un `Flow/StateFlow` en estado de Compose respetando el ciclo de vida, evitando trabajo de colección innecesario cuando la UI no está activa. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 224 | `settings` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Convierte un `Flow/StateFlow` en estado de Compose respetando el ciclo de vida, evitando trabajo de colección innecesario cuando la UI no está activa. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 248 | `showEditor` | `var` | `inferido` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. El nombre indica un estado de decisión/visibilidad/selección usado para controlar una rama de comportamiento o presentación. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 251 | `showSettings` | `var` | `inferido` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. El nombre indica un estado de decisión/visibilidad/selección usado para controlar una rama de comportamiento o presentación. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 254 | `selectedNote` | `var` | `inferido` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. El nombre indica un estado de decisión/visibilidad/selección usado para controlar una rama de comportamiento o presentación. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 264 | `editingNote` | `var` | `inferido` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 310 | `currentScreen` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 516 | `attachmentsFlow` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 551 | `noteBeingEdited` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 608 | `currentSelectedNote` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El nombre indica un estado de decisión/visibilidad/selección usado para controlar una rama de comportamiento o presentación. |

## 6. Mapa exhaustivo de TODOS los bloques delimitados por `{}`

Esta tabla recorre todas las llaves estructurales detectadas fuera de strings/comentarios. Así se documentan también lambdas de Compose, callbacks, iteraciones y ámbitos locales que no tienen un nombre propio de función.

| Inicio–fin | Prof. | Cabecera/contexto | Qué significa ese bloque |
|---|---:|---|---|
| 38–40 | 0 | `private enum class AppDestination` | Cuerpo de tipo/contenedor: agrupa propiedades y miembros bajo la misma responsabilidad y controla su visibilidad/estado compartido. |
| 44–670 | 0 | `class MainActivity : ComponentActivity()` | Cuerpo de tipo/contenedor: agrupa propiedades y miembros bajo la misma responsabilidad y controla su visibilidad/estado compartido. |
| 51–55 | 1 | `companion object` | Cuerpo de tipo/contenedor: agrupa propiedades y miembros bajo la misma responsabilidad y controla su visibilidad/estado compartido. |
| 64–74 | 1 | `private fun handleIncomingShare(incomingIntent: Intent?)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 65–67 | 2 | `if (incomingIntent?.action != Intent.ACTION_SEND)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 69–71 | 2 | `if (sharedText.isBlank())` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 73–73 | 2 | `pendingSharedTitle = incomingIntent.getCharSequenceExtra(Intent.EXTRA_SUBJECT)?.toString()?.trim()?.takeIf` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 75–78 | 1 | `private fun clearPendingShare()` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 89–98 | 1 | `override fun attachBaseContext(newBase: Context)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 99–110 | 1 | `private fun changeAppLanguage(language: String)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 120–137 | 1 | `private fun installImeNavigationBarRecovery()` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 121–135 | 2 | `ViewCompat.setOnApplyWindowInsetsListener(window.decorView)` | Listener de insets de ventana: Android invoca este bloque cuando cambian las áreas ocupadas por barras del sistema/IME. |
| 128–132 | 3 | `if (wasImeVisible && !isImeVisible)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 129–131 | 4 | `view.post` | Ámbito delimitado por llaves en profundidad 4. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 138–163 | 1 | `private fun applyAndroidNavigationBarPolicy()` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 147–149 | 2 | `if (isMultiWindow)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 149–152 | 2 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 159–162 | 2 | `Build.VERSION_CODES.O)` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 164–170 | 1 | `private fun applySystemBarAppearance(darkMode: Boolean)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 167–169 | 2 | `if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 171–176 | 1 | `override fun onWindowFocusChanged(hasFocus: Boolean)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 173–175 | 2 | `if (hasFocus)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 177–186 | 1 | `override fun onResume()` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 187–190 | 1 | `override fun onMultiWindowModeChanged(isInMultiWindowMode: Boolean)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 191–194 | 1 | `override fun onDestroy()` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 195–199 | 1 | `override fun onNewIntent(intent: Intent)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 200–669 | 1 | `override fun onCreate(savedInstanceState: Bundle?)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 218–668 | 2 | `setContent` | Raíz de composición de la Activity: establece el árbol Compose que se renderiza como contenido de la ventana. |
| 226–231 | 3 | `settings.hapticEffectsEnabled, settings.hapticEffectsIntensity, settings.hapticEffectsStyle)` | Ámbito delimitado por llaves en profundidad 3. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 232–234 | 3 | `LaunchedEffect(settings.darkMode)` | Efecto de Compose con corrutina: su ciclo de vida depende de las claves; se cancela/reinicia al cambiar dichas claves o salir de composición. |
| 240–242 | 3 | `LaunchedEffect(settings.performanceMode)` | Efecto de Compose con corrutina: su ciclo de vida depende de las claves; se cancela/reinicia al cambiar dichas claves o salir de composición. |
| 248–250 | 3 | `var showEditor by remember` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 251–253 | 3 | `var showSettings by remember` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 254–256 | 3 | `var selectedNote by remember` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 264–266 | 3 | `var editingNote by remember` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 272–279 | 3 | `LaunchedEffect(pendingSharedText, pendingSharedTitle)` | Efecto de Compose con corrutina: su ciclo de vida depende de las claves; se cancela/reinicia al cambiar dichas claves o salir de composición. |
| 273–278 | 4 | `if (!pendingSharedText.isNullOrBlank())` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 291–305 | 3 | `BackHandler(enabled = showSettings \|\| showEditor \|\| selectedNote != null)` | Ámbito delimitado por llaves en profundidad 3. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 292–304 | 4 | `when` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 293–295 | 5 | `showSettings ->` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 296–300 | 5 | `showEditor ->` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 301–303 | 5 | `selectedNote != null ->` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 310–315 | 3 | `val currentScreen = when` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 328–667 | 3 | `accentColor = settings.accentColor)` | Ámbito delimitado por llaves en profundidad 3. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 335–666 | 4 | `performanceMode = settings.performanceMode)` | Ámbito delimitado por llaves en profundidad 4. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 336–665 | 5 | `when (screen.destination)` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 342–502 | 6 | `AppDestination.SETTINGS ->` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 345–347 | 7 | `onDarkModeChange =` | Ámbito delimitado por llaves en profundidad 7. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 348–350 | 7 | `onBackgroundColorChange =` | Ámbito delimitado por llaves en profundidad 7. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 351–353 | 7 | `onBackgroundToneIndexChange =` | Ámbito delimitado por llaves en profundidad 7. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 354–356 | 7 | `onBackgroundIntensityChange =` | Ámbito delimitado por llaves en profundidad 7. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 357–359 | 7 | `onSettingsPanelToneChange =` | Ámbito delimitado por llaves en profundidad 7. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 360–362 | 7 | `onSurfacePanelIntensityChange =` | Ámbito delimitado por llaves en profundidad 7. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 363–365 | 7 | `onHeaderIntensityChange =` | Ámbito delimitado por llaves en profundidad 7. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 366–368 | 7 | `onTextColorChange =` | Ámbito delimitado por llaves en profundidad 7. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 369–371 | 7 | `onTextOutlineEnabledChange =` | Ámbito delimitado por llaves en profundidad 7. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 372–374 | 7 | `onNoteUiTextColorChange =` | Ámbito delimitado por llaves en profundidad 7. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 375–377 | 7 | `onSliderStyleChange =` | Ámbito delimitado por llaves en profundidad 7. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 378–380 | 7 | `onFontChange =` | Ámbito delimitado por llaves en profundidad 7. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 381–383 | 7 | `onFontSizeChange =` | Ámbito delimitado por llaves en profundidad 7. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 384–386 | 7 | `onSoundEffectsEnabledChange =` | Ámbito delimitado por llaves en profundidad 7. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 387–389 | 7 | `onSoundEffectsVolumeChange =` | Ámbito delimitado por llaves en profundidad 7. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 390–392 | 7 | `onSoundEffectsThemeChange =` | Ámbito delimitado por llaves en profundidad 7. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 393–395 | 7 | `onHapticEffectsEnabledChange =` | Ámbito delimitado por llaves en profundidad 7. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 396–398 | 7 | `onHapticEffectsIntensityChange =` | Ámbito delimitado por llaves en profundidad 7. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 399–401 | 7 | `onHapticEffectsStyleChange =` | Ámbito delimitado por llaves en profundidad 7. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 402–405 | 7 | `onLanguageChange =` | Ámbito delimitado por llaves en profundidad 7. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 406–408 | 7 | `onGridColumnsChange =` | Ámbito delimitado por llaves en profundidad 7. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 409–411 | 7 | `onProfileImageUriChange =` | Ámbito delimitado por llaves en profundidad 7. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 412–414 | 7 | `onProfileImageSizeChange =` | Ámbito delimitado por llaves en profundidad 7. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 415–417 | 7 | `onIconStyleChange =` | Ámbito delimitado por llaves en profundidad 7. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 418–420 | 7 | `onIconSizeChange =` | Ámbito delimitado por llaves en profundidad 7. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 421–423 | 7 | `onAccentColorChange =` | Ámbito delimitado por llaves en profundidad 7. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 424–426 | 7 | `onNoteCardCornerRadiusChange =` | Ámbito delimitado por llaves en profundidad 7. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 427–429 | 7 | `onNoteCardElevationChange =` | Ámbito delimitado por llaves en profundidad 7. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 430–432 | 7 | `onNoteCardPaddingChange =` | Ámbito delimitado por llaves en profundidad 7. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 433–435 | 7 | `onNoteCardImageHeightChange =` | Ámbito delimitado por llaves en profundidad 7. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 436–438 | 7 | `onNoteTitleMaxLinesChange =` | Ámbito delimitado por llaves en profundidad 7. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 439–441 | 7 | `onNoteContentMaxLinesChange =` | Ámbito delimitado por llaves en profundidad 7. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 442–444 | 7 | `onNoteLineSpacingChange =` | Ámbito delimitado por llaves en profundidad 7. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 445–447 | 7 | `onShowNoteDateChange =` | Ámbito delimitado por llaves en profundidad 7. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 448–450 | 7 | `onShowCategoryChipChange =` | Ámbito delimitado por llaves en profundidad 7. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 451–453 | 7 | `onShowFavoriteIconChange =` | Ámbito delimitado por llaves en profundidad 7. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 454–456 | 7 | `onFabSizeChange =` | Ámbito delimitado por llaves en profundidad 7. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 457–459 | 7 | `onOptionMenuOrderChange =` | Ámbito delimitado por llaves en profundidad 7. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 460–462 | 7 | `onOptionMenuHiddenItemsChange =` | Ámbito delimitado por llaves en profundidad 7. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 463–465 | 7 | `onOptionMenuShowIconsChange =` | Ámbito delimitado por llaves en profundidad 7. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 466–468 | 7 | `onOptionMenuTextColorChange =` | Ámbito delimitado por llaves en profundidad 7. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 469–471 | 7 | `onOptionMenuOpacityChange =` | Ámbito delimitado por llaves en profundidad 7. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 472–474 | 7 | `onPriorityMenuHiddenItemsChange =` | Ámbito delimitado por llaves en profundidad 7. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 475–477 | 7 | `onColorMenuHiddenItemsChange =` | Ámbito delimitado por llaves en profundidad 7. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 478–480 | 7 | `onResetOptionMenu =` | Ámbito delimitado por llaves en profundidad 7. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 481–483 | 7 | `onPerformanceModeChange =` | Ámbito delimitado por llaves en profundidad 7. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 484–486 | 7 | `onAnimationsEnabledChange =` | Ámbito delimitado por llaves en profundidad 7. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 487–489 | 7 | `onAnimationStyleChange =` | Ámbito delimitado por llaves en profundidad 7. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 490–492 | 7 | `onAnimationEasingChange =` | Ámbito delimitado por llaves en profundidad 7. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 493–495 | 7 | `onAnimationSpeedChange =` | Ámbito delimitado por llaves en profundidad 7. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 496–498 | 7 | `onAnimationIntensityChange =` | Ámbito delimitado por llaves en profundidad 7. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 499–501 | 7 | `onBack =` | Ámbito delimitado por llaves en profundidad 7. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 508–596 | 6 | `AppDestination.EDITOR ->` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 515–522 | 7 | `List<Attachment> = if (screen.note != null)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 516–518 | 8 | `val attachmentsFlow = remember(screen.note!!.id)` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 522–524 | 7 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 526–595 | 7 | `animationSpeed = settings.animationSpeed)` | Ámbito delimitado por llaves en profundidad 7. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 549–589 | 8 | `onSave =` | Ámbito delimitado por llaves en profundidad 8. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 552–562 | 9 | `if (noteBeingEdited == null)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 562–585 | 9 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 581–584 | 10 | `removedAttachments.forEach` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 590–594 | 8 | `onCancel =` | Ámbito delimitado por llaves en profundidad 8. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 602–625 | 6 | `AppDestination.DETAIL ->` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 608–610 | 7 | `val currentSelectedNote = notes.firstOrNull` | Ámbito delimitado por llaves en profundidad 7. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 615–617 | 7 | `onBack =` | Ámbito delimitado por llaves en profundidad 7. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 618–624 | 7 | `onEdit =` | Ámbito delimitado por llaves en profundidad 7. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 631–664 | 6 | `AppDestination.NOTES ->` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 639–643 | 7 | `onAddNote =` | Ámbito delimitado por llaves en profundidad 7. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 647–649 | 7 | `onOpenSettings =` | Ámbito delimitado por llaves en profundidad 7. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 653–655 | 7 | `onOpenNote =` | Ámbito delimitado por llaves en profundidad 7. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 659–663 | 7 | `onEditNote =` | Ámbito delimitado por llaves en profundidad 7. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |

## 7. Lectura de seguridad antes de tocar este archivo

La documentación describe **lo que el código actual ya hace**. No constituye una propuesta de refactorización. Si se quisiera cambiar algo en el futuro, primero habría que preservar contratos públicos, claves de configuración, restricciones de API Android, comportamiento de Compose, lifecycle, operaciones de I/O y compatibilidad con los datos ya persistidos. En esta entrega no se hizo ninguna de esas modificaciones.

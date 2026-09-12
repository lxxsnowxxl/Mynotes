# AttachmentViewerActivity.kt — documentación exhaustiva por bloques

**Ruta de código real:** `app/src/main/java/com/example/mynotes/ui/AttachmentViewerActivity.kt`  
**SHA-256 del archivo ejecutable sin tocar:** `7eaf143a7f4ee5fd842431af578db28a7e9a6618511c1a209fa0f09575bc156d`  
**Líneas del código real:** 1267

> **Garantía:** esta documentación se genera en una carpeta paralela. El archivo Kotlin/Kotlin DSL anterior no se modifica. Los fragmentos mostrados abajo son copias de lectura; la aplicación compila usando únicamente el archivo original de `app/...`.

## 1. Papel del archivo

Activity especializada en abrir y visualizar adjuntos. Decide el tratamiento según tipo/MIME y prepara la experiencia para imagen, video, audio, PDF, texto, Office u otros archivos soportados.

**Arquitectura.** Se separa de MainActivity porque el visor tiene requisitos propios de ventana, Media3, carga de documentos, controles y rendimiento. También reutiliza DisplayPerformanceController.

**Flujo general.** Flujo típico: recibe URI/ruta/MIME -> clasifica el archivo -> selecciona visor especializado -> configura controles y modo de pantalla -> libera recursos del reproductor/visor según el ciclo de vida.

## 2. Package e imports

El `package` es `com.example.mynotes.ui`. Determina el namespace lógico del archivo, resolución de visibilidad `internal`/package-related tooling y la ruta conceptual desde la que otras clases lo importan.

El archivo declara **115 imports**. Cada import evita usar el nombre totalmente calificado en el cuerpo y, además, revela las dependencias técnicas del bloque:

**Android/plataforma:** `android.app.Activity`, `android.app.ActivityManager`, `android.content.ActivityNotFoundException`, `android.content.Context`, `android.content.Intent`, `android.graphics.Bitmap`, `android.graphics.Canvas`, `android.graphics.Color as AndroidColor`, `android.graphics.pdf.PdfRenderer`, `android.net.Uri`, `android.os.Build`, `android.os.Bundle`, `android.os.ParcelFileDescriptor`, `android.text.Html`, `android.webkit.MimeTypeMap`, `android.widget.Toast`.

**Jetpack/Compose:** `androidx.activity.ComponentActivity`, `androidx.activity.compose.setContent`, `androidx.activity.enableEdgeToEdge`, `androidx.compose.foundation.Image`, `androidx.compose.foundation.background`, `androidx.compose.foundation.layout.Arrangement`, `androidx.compose.foundation.layout.Box`, `androidx.compose.foundation.layout.BoxWithConstraints`, `androidx.compose.foundation.layout.Column`, `androidx.compose.foundation.layout.Row`, `androidx.compose.foundation.layout.Spacer`, `androidx.compose.foundation.layout.fillMaxSize`, `androidx.compose.foundation.layout.fillMaxWidth`, `androidx.compose.foundation.layout.height`, `androidx.compose.foundation.layout.padding`, `androidx.compose.foundation.layout.size`, `androidx.compose.foundation.lazy.LazyColumn`, `androidx.compose.foundation.lazy.items`, `androidx.compose.foundation.rememberScrollState`, `androidx.compose.foundation.shape.RoundedCornerShape`, `androidx.compose.foundation.verticalScroll`, `androidx.compose.material.icons.Icons`, `androidx.compose.material.icons.automirrored.filled.ArrowBack`, `androidx.compose.material.icons.filled.Description`, `androidx.compose.material.icons.filled.OpenInNew`, `androidx.compose.material.icons.filled.Pause`, `androidx.compose.material.icons.filled.PictureAsPdf`, `androidx.compose.material.icons.filled.PlayArrow`, `androidx.compose.material3.Button`, `androidx.compose.material3.CircularProgressIndicator`, `androidx.compose.material3.ExperimentalMaterial3Api`, `androidx.compose.material3.Icon`, `androidx.compose.material3.IconButton`, `androidx.compose.material3.MaterialTheme`, `androidx.compose.material3.Scaffold`, `androidx.compose.material3.Slider`, `androidx.compose.material3.Surface`, `androidx.compose.material3.Text`, `androidx.compose.material3.TopAppBar`, `androidx.compose.material3.TopAppBarDefaults`, `androidx.compose.runtime.Composable`, `androidx.compose.runtime.DisposableEffect`, `androidx.compose.runtime.LaunchedEffect`, `androidx.compose.runtime.getValue`, `androidx.compose.runtime.mutableIntStateOf`, `androidx.compose.runtime.mutableStateOf`, `androidx.compose.runtime.produceState`, `androidx.compose.runtime.remember`, `androidx.compose.runtime.rememberCoroutineScope`, `androidx.compose.runtime.setValue`, `androidx.compose.ui.Alignment`, `androidx.compose.ui.Modifier`, `androidx.compose.ui.graphics.Color`, `androidx.compose.ui.graphics.asImageBitmap`, `androidx.compose.ui.layout.ContentScale`, `androidx.compose.ui.platform.LocalContext`, `androidx.compose.ui.platform.LocalDensity`, `androidx.compose.ui.text.font.FontFamily`, `androidx.compose.ui.text.font.FontWeight`, `androidx.compose.ui.text.style.TextOverflow`, `androidx.compose.ui.unit.dp`, `androidx.compose.ui.unit.sp`, `androidx.compose.ui.viewinterop.AndroidView`, `androidx.core.content.FileProvider`, `androidx.core.view.WindowCompat`, `androidx.core.view.WindowInsetsCompat`, `androidx.core.view.WindowInsetsControllerCompat`, `androidx.lifecycle.compose.collectAsStateWithLifecycle`, `androidx.lifecycle.viewmodel.compose.viewModel`, `androidx.media3.common.MediaItem`, `androidx.media3.common.PlaybackException`, `androidx.media3.common.Player`, `androidx.media3.exoplayer.ExoPlayer`, `androidx.media3.ui.AspectRatioFrameLayout`, `androidx.media3.ui.PlayerView`.

**Proyecto MyNotes:** `com.example.mynotes.R`, `com.example.mynotes.performance.DisplayPerformanceController`, `com.example.mynotes.ui.sound.UiActionSound`, `com.example.mynotes.ui.sound.UiSoundPlayer`, `com.example.mynotes.ui.theme.MyNotesTheme`, `com.example.mynotes.viewmodel.SettingsViewModel`.

**Kotlin/corrutinas/Java:** `kotlinx.coroutines.Dispatchers`, `kotlinx.coroutines.delay`, `kotlinx.coroutines.launch`, `kotlinx.coroutines.withContext`, `java.io.Closeable`, `java.io.File`, `java.io.InputStream`, `java.util.Locale`, `java.util.zip.ZipInputStream`, `kotlin.math.roundToInt`.

**Otras librerías:** `coil3.compose.AsyncImage`, `coil3.request.CachePolicy`, `coil3.request.ImageRequest`, `coil3.request.allowHardware`, `coil3.request.maxBitmapSize`, `coil3.size.Precision`, `coil3.size.Scale`, `coil3.size.Size`.

## 3. Restricciones e invariantes visibles en el archivo

- **Nulabilidad segura (21 aparición/apariciones):** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`.
- **Fallback nulo (17 aparición/apariciones):** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula.
- **Aserción no nula (3 aparición/apariciones):** La aserción `!!` exige un valor no nulo en tiempo de ejecución; si la suposición falla se produce una excepción.
- **Límite numérico (23 aparición/apariciones):** Se fuerza el valor a un intervalo o umbral para impedir que salgan valores fuera de los límites esperados.
- **Filtro condicional (10 aparición/apariciones):** `takeIf` conserva el valor únicamente cuando el predicado se cumple; en caso contrario lo convierte en `null`.
- **Normalización vacía (7 aparición/apariciones):** `orEmpty` transforma una referencia nula en una colección/cadena vacía, simplificando el consumo posterior.
- **Guardia de API (4 aparición/apariciones):** Se condiciona una API según la versión de Android para no ejecutar llamadas incompatibles en dispositivos antiguos.
- **Límite visual (3 aparición/apariciones):** La UI impone un límite explícito de tamaño o cantidad de líneas para impedir crecimiento visual no controlado.
- **Trabajo IO (6 aparición/apariciones):** El trabajo potencialmente bloqueante se desplaza al dispatcher de I/O en vez de ocupar el hilo principal.

## 4. Bloques de código, uno por uno

### 4.1 `openAttachmentViewer` — fun, líneas 129–136

```kotlin
fun openAttachmentViewer(context: Context, uri: String, type: String, name: String? = null, mimeType: String? = null) {
    val intent = Intent(context, AttachmentViewerActivity::class.java).putExtra(EXTRA_URI, uri).putExtra(EXTRA_TYPE, type)
            .putExtra(EXTRA_NAME, name).putExtra(EXTRA_MIME, mimeType).addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    if (context !is Activity) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(intent)
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `context: Context` — `context` recibe un valor de tipo `Context`. El contrato no marca este parámetro como anulable. Da acceso a recursos y APIs Android; su vida útil debe corresponder con el uso realizado.
- `uri: String` — `uri` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.
- `type: String` — `type` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable. Aunque el tipo sea `String`, semánticamente suele funcionar como clave/enumeración textual; las ramas del código delimitan los valores reconocidos.
- `name: String? = null` — `name` recibe un valor de tipo `String?`. El contrato permite `null`; el bloque debe decidir explícita o implícitamente qué significa la ausencia. Tiene valor por defecto `null`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada.
- `mimeType: String? = null` — `mimeType` recibe un valor de tipo `String?`. El contrato permite `null`; el bloque debe decidir explícita o implícitamente qué significa la ausencia. Tiene valor por defecto `null`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada. Aunque el tipo sea `String`, semánticamente suele funcionar como clave/enumeración textual; las ramas del código delimitan los valores reconocidos.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.


#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 130 | `val intent` | `inferido` | `Intent(context, AttachmentViewerActivity::class.java).putExtra(EXTRA_URI, uri).putExtra(EXTRA_TYPE, …` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 132 | `if (context !is Activity) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |

#### Efectos secundarios y recursos

- **Navegación/Activity:** Modifica navegación, ciclo de vida o contenido de una Activity.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `Intent`, `putExtra`, `addFlags`, `intent.addFlags`, `context.startActivity`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.2 `AttachmentViewerActivity` — class, líneas 138–219

```kotlin
class AttachmentViewerActivity : ComponentActivity() {
    // … el cuerpo completo permanece en el archivo real; sus miembros se documentan individualmente abajo …
}
```

#### Qué hace y por qué existe

Activity especializada en abrir y visualizar adjuntos. Decide el tratamiento según tipo/MIME y prepara la experiencia para imagen, video, audio, PDF, texto, Office u otros archivos soportados.

#### Contrato de la declaración


#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 140 | `val controller` | `inferido` | `WindowCompat.getInsetsController(window, window.decorView)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 141 | `val isMultiWindow` | `inferido` | `Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && isInMultiWindowMode` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El nombre indica un estado de decisión/visibilidad/selección usado para controlar una rama de comportamiento o presentación. |
| 159 | `val controller` | `inferido` | `WindowCompat.getInsetsController(window, window.decorView)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 194 | `val uriString` | `inferido` | `intent.getStringExtra(EXTRA_URI).orEmpty()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Representa una ubicación Android `Uri`; no debe asumirse que siempre corresponde a una ruta de archivo convencional. Normaliza una ausencia hacia un valor vacío/alternativo para simplificar el código consumidor. |
| 195 | `val type` | `inferido` | `intent.getStringExtra(EXTRA_TYPE).orEmpty()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Normaliza una ausencia hacia un valor vacío/alternativo para simplificar el código consumidor. |
| 196 | `val name` | `inferido` | `intent.getStringExtra(EXTRA_NAME)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 197 | `val mimeType` | `inferido` | `intent.getStringExtra(EXTRA_MIME)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 203 | `val settingsViewModel` | `SettingsViewModel` | `viewModel()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo declarado es `SettingsViewModel`. No declara nulabilidad explícita. Actúa como selector de estrategia/perfil; su valor determina qué rama de configuración se aplica. Referencia un ViewModel, cuyo objetivo es mantener estado/lógica de pantalla fuera de la instancia visual inmediata. |
| 204 | `val settings` | `inferido` | `by settingsViewModel.settings.collectAsStateWithLifecycle()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Convierte un `Flow/StateFlow` en estado de Compose respetando el ciclo de vida, evitando trabajo de colección innecesario cuando la UI no está activa. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 142 | `if (isMultiWindow) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 153 | `if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 161 | `if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 167 | `if (hasFocus) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 198 | `if (uriString.isBlank()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 200 | `return` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 205 | `LaunchedEffect(settings.performanceMode) {` | Efecto de Compose basado en corrutina: se inicia cuando entra en composición y se reinicia si cambian sus claves; se cancela al salir de composición. |
| 208 | `LaunchedEffect(settings.darkMode) {` | Efecto de Compose basado en corrutina: se inicia cuando entra en composición y se reinicia si cambian sus claves; se cancela al salir de composición. |

**Restricciones concretas que aparecen en este bloque:**
- **Normalización vacía:** `orEmpty` transforma una referencia nula en una colección/cadena vacía, simplificando el consumo posterior. Apariciones en este bloque: 2.
- **Guardia de API:** Se condiciona una API según la versión de Android para no ejecutar llamadas incompatibles en dispositivos antiguos. Apariciones en este bloque: 3.

#### Semántica Compose/lifecycle

- **`LaunchedEffect`:** Ejecuta una corrutina ligada al ciclo de vida de la composición y a sus claves.
- **`collectAsStateWithLifecycle`:** Observa un flujo de forma consciente del lifecycle y entrega el último valor como estado de Compose.

#### Efectos secundarios y recursos

- **Navegación/Activity:** Modifica navegación, ciclo de vida o contenido de una Activity.
- **Ventana/sistema:** Interactúa con la ventana/sistema Android; puede cambiar barras, modo de pantalla o atributos de presentación.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `ComponentActivity`, `WindowCompat.getInsetsController`, `controller.show`, `WindowInsetsCompat.Type.navigationBars`, `controller.hide`, `Suppress`, `super.onWindowFocusChanged`, `applyAndroidNavigationBarPolicy`, `super.onResume`, `DisplayPerformanceController.reapplyLastRequest`, `super.onMultiWindowModeChanged`, `DisplayPerformanceController.release`, `super.onDestroy`, `setTheme`, `super.onCreate`, `enableEdgeToEdge`, `intent.getStringExtra`, `orEmpty`, `uriString.isBlank`, `finish`, `viewModel`, `settingsViewModel.settings.collectAsStateWithLifecycle`, `LaunchedEffect`, `DisplayPerformanceController.requestForPerformanceMode`, `applySystemBarAppearance`, `MyNotesTheme`, `AttachmentViewerScreen`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar las guardas de versión antes de APIs que no existen en Android antiguos.
- Preservar la normalización y correspondencia de `performanceMode`, porque otras capas usan las mismas claves para rendimiento/calidad.

### 4.3 `applyAndroidNavigationBarPolicy` — fun, líneas 139–157

```kotlin
    private fun applyAndroidNavigationBarPolicy() {
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        val isMultiWindow = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && isInMultiWindowMode
        if (isMultiWindow) {
            controller.show(WindowInsetsCompat.Type.navigationBars())
        } else {
            controller.hide(WindowInsetsCompat.Type.navigationBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        /*
         * Android 7.x no dispone de iconos oscuros para la barra de
         * navegación. Si llega a mostrarse (multiventana o gesto), un fondo
         * negro garantiza contraste con los botones claros del sistema.
         */
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
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
| 140 | `val controller` | `inferido` | `WindowCompat.getInsetsController(window, window.decorView)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 141 | `val isMultiWindow` | `inferido` | `Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && isInMultiWindowMode` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El nombre indica un estado de decisión/visibilidad/selección usado para controlar una rama de comportamiento o presentación. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 142 | `if (isMultiWindow) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 153 | `if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |

**Restricciones concretas que aparecen en este bloque:**
- **Guardia de API:** Se condiciona una API según la versión de Android para no ejecutar llamadas incompatibles en dispositivos antiguos. Apariciones en este bloque: 2.

#### Efectos secundarios y recursos

- **Ventana/sistema:** Interactúa con la ventana/sistema Android; puede cambiar barras, modo de pantalla o atributos de presentación.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `WindowCompat.getInsetsController`, `controller.show`, `WindowInsetsCompat.Type.navigationBars`, `controller.hide`, `Suppress`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar las guardas de versión antes de APIs que no existen en Android antiguos.

### 4.4 `applySystemBarAppearance` — fun, líneas 158–164

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
| 159 | `val controller` | `inferido` | `WindowCompat.getInsetsController(window, window.decorView)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 161 | `if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |

**Restricciones concretas que aparecen en este bloque:**
- **Guardia de API:** Se condiciona una API según la versión de Android para no ejecutar llamadas incompatibles en dispositivos antiguos. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

- **Ventana/sistema:** Interactúa con la ventana/sistema Android; puede cambiar barras, modo de pantalla o atributos de presentación.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `WindowCompat.getInsetsController`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar las guardas de versión antes de APIs que no existen en Android antiguos.

### 4.5 `onWindowFocusChanged` — fun, líneas 165–170

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
| 167 | `if (hasFocus) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `super.onWindowFocusChanged`, `applyAndroidNavigationBarPolicy`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.6 `onResume` — fun, líneas 171–175

```kotlin
    override fun onResume() {
        super.onResume()
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

### 4.7 `onMultiWindowModeChanged` — fun, líneas 176–179

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

### 4.8 `onDestroy` — fun, líneas 180–183

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

### 4.9 `onCreate` — fun, líneas 184–218

```kotlin
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_MyNotes)
        super.onCreate(savedInstanceState)
        /*
         * No forzamos una frecuencia inicial aquí. El perfil guardado se
         * aplicará desde DisplayPerformanceController cuando SettingsViewModel
         * entregue performanceMode.
         */
        enableEdgeToEdge()
        applyAndroidNavigationBarPolicy()
        val uriString = intent.getStringExtra(EXTRA_URI).orEmpty()
        val type = intent.getStringExtra(EXTRA_TYPE).orEmpty()
        val name = intent.getStringExtra(EXTRA_NAME)
        val mimeType = intent.getStringExtra(EXTRA_MIME)
        if (uriString.isBlank()) {
            finish()
            return
        }
        setContent {
            val settingsViewModel: SettingsViewModel = viewModel()
            val settings by settingsViewModel.settings.collectAsStateWithLifecycle()
            LaunchedEffect(settings.performanceMode) {
                DisplayPerformanceController.requestForPerformanceMode(window = window, performanceMode = settings.performanceMode)
            }
            LaunchedEffect(settings.darkMode) {
                applySystemBarAppearance(settings.darkMode)
            }
            MyNotesTheme(darkTheme = settings.darkMode, backgroundColor = settings.backgroundColor,
                backgroundToneIndex = settings.backgroundToneIndex, backgroundIntensity = settings.backgroundIntensity,
                surfacePanelIntensity = settings.surfacePanelIntensity, headerIntensity = settings.headerIntensity,
                textColor = settings.textColor, textOutlineEnabled = settings.textOutlineEnabled, accentColor = settings.accentColor) {
                AttachmentViewerScreen(uriString = uriString, type = type, name = name, explicitMimeType = mimeType, onBack = { finish() })
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
| 194 | `val uriString` | `inferido` | `intent.getStringExtra(EXTRA_URI).orEmpty()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Representa una ubicación Android `Uri`; no debe asumirse que siempre corresponde a una ruta de archivo convencional. Normaliza una ausencia hacia un valor vacío/alternativo para simplificar el código consumidor. |
| 195 | `val type` | `inferido` | `intent.getStringExtra(EXTRA_TYPE).orEmpty()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Normaliza una ausencia hacia un valor vacío/alternativo para simplificar el código consumidor. |
| 196 | `val name` | `inferido` | `intent.getStringExtra(EXTRA_NAME)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 197 | `val mimeType` | `inferido` | `intent.getStringExtra(EXTRA_MIME)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 203 | `val settingsViewModel` | `SettingsViewModel` | `viewModel()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo declarado es `SettingsViewModel`. No declara nulabilidad explícita. Actúa como selector de estrategia/perfil; su valor determina qué rama de configuración se aplica. Referencia un ViewModel, cuyo objetivo es mantener estado/lógica de pantalla fuera de la instancia visual inmediata. |
| 204 | `val settings` | `inferido` | `by settingsViewModel.settings.collectAsStateWithLifecycle()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Convierte un `Flow/StateFlow` en estado de Compose respetando el ciclo de vida, evitando trabajo de colección innecesario cuando la UI no está activa. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 198 | `if (uriString.isBlank()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 200 | `return` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 205 | `LaunchedEffect(settings.performanceMode) {` | Efecto de Compose basado en corrutina: se inicia cuando entra en composición y se reinicia si cambian sus claves; se cancela al salir de composición. |
| 208 | `LaunchedEffect(settings.darkMode) {` | Efecto de Compose basado en corrutina: se inicia cuando entra en composición y se reinicia si cambian sus claves; se cancela al salir de composición. |

**Restricciones concretas que aparecen en este bloque:**
- **Normalización vacía:** `orEmpty` transforma una referencia nula en una colección/cadena vacía, simplificando el consumo posterior. Apariciones en este bloque: 2.

#### Semántica Compose/lifecycle

- **`LaunchedEffect`:** Ejecuta una corrutina ligada al ciclo de vida de la composición y a sus claves.
- **`collectAsStateWithLifecycle`:** Observa un flujo de forma consciente del lifecycle y entrega el último valor como estado de Compose.

#### Efectos secundarios y recursos

- **Navegación/Activity:** Modifica navegación, ciclo de vida o contenido de una Activity.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `setTheme`, `super.onCreate`, `enableEdgeToEdge`, `applyAndroidNavigationBarPolicy`, `intent.getStringExtra`, `orEmpty`, `uriString.isBlank`, `finish`, `viewModel`, `settingsViewModel.settings.collectAsStateWithLifecycle`, `LaunchedEffect`, `DisplayPerformanceController.requestForPerformanceMode`, `applySystemBarAppearance`, `MyNotesTheme`, `AttachmentViewerScreen`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Preservar la normalización y correspondencia de `performanceMode`, porque otras capas usan las mismas claves para rendimiento/calidad.

### 4.10 `AttachmentViewerScreen` — fun, líneas 223–288

```kotlin
private fun AttachmentViewerScreen(uriString: String, type: String, name: String?, explicitMimeType: String?, onBack: () -> Unit) {
    val context = LocalContext.current
    val uri = remember(uriString) { Uri.parse(uriString) }
    val mimeType = remember(uriString, explicitMimeType, name) {
        explicitMimeType?: resolveMimeType(context, uri, name)
    }
    val extension = remember(name, uriString) {
        fileExtension(name, uri)
    }
    val title = name?.takeIf { it.isNotBlank() }?: when {
            type == "video" || mimeType.startsWith("video/") -> androidx.compose.ui.res.stringResource(R.string.video)
            type == "image" || mimeType.startsWith("image/") -> androidx.compose.ui.res.stringResource(R.string.image)
            type == "audio" || type == "voice" || mimeType.startsWith("audio/") -> androidx.compose.ui.res.stringResource(R.string.audio)
            extension == "pdf" -> "PDF"
            else -> androidx.compose.ui.res.stringResource(R.string.file)
        }
    Scaffold(modifier = Modifier.fillMaxSize(), containerColor = MaterialTheme.colorScheme.background, topBar = {
            TopAppBar(title = {
                    Text(text = title, maxLines = 1, overflow = TextOverflow.Ellipsis, fontWeight = FontWeight.SemiBold)
                }, navigationIcon = {
                    IconButton(onClick = {
                            UiSoundPlayer.playAction(context = context, action = UiActionSound.Back)
                            onBack()
                        }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = androidx.compose.ui.res.stringResource(R.string.back))
                    }
                }, actions = {
                    IconButton(onClick = {
                            UiSoundPlayer.playAction(context = context, action = UiActionSound.Link)
                            openExternally(context = context, uri = uri, name = name, mimeType = mimeType)
                        }) {
                        Icon(imageVector = Icons.Default.OpenInNew,
                            contentDescription = androidx.compose.ui.res.stringResource(R.string.open_with_other_app))
                    }
                }, colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
                    actionIconContentColor = MaterialTheme.colorScheme.onBackground))
        }) { innerPadding -> Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            when {
                type == "image" || mimeType.startsWith("image/") -> {
                    ImageViewer(uri = uri, name = name)
                }
                type == "video" || mimeType.startsWith("video/") -> {
                    VideoViewer(uri = uri, name = title, mimeType = mimeType)
                }
                type == "audio" || type == "voice" || mimeType.startsWith("audio/") -> {
                    AudioFileViewer(uri = uri, name = title, mimeType = mimeType)
                }
                extension == "pdf" || mimeType == "application/pdf" -> {
                    PdfViewer(uri = uri)
                }
                isTextExtension(extension) || mimeType.startsWith("text/") -> {
                    TextFileViewer(uri = uri, extension = extension)
                }
                extension == "docx" || extension == "pptx" || extension == "xlsx" -> {
                    OfficeTextViewer(uri = uri, extension = extension, name = title, mimeType = mimeType)
                }
                else -> {
                    GenericFileViewer(uri = uri, name = title, extension = extension, mimeType = mimeType)
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

- `uriString: String` — `uriString` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.
- `type: String` — `type` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable. Aunque el tipo sea `String`, semánticamente suele funcionar como clave/enumeración textual; las ramas del código delimitan los valores reconocidos.
- `name: String?` — `name` recibe un valor de tipo `String?`. El contrato permite `null`; el bloque debe decidir explícita o implícitamente qué significa la ausencia.
- `explicitMimeType: String?` — `explicitMimeType` recibe un valor de tipo `String?`. El contrato permite `null`; el bloque debe decidir explícita o implícitamente qué significa la ausencia. Aunque el tipo sea `String`, semánticamente suele funcionar como clave/enumeración textual; las ramas del código delimitan los valores reconocidos.
- `onBack: () -> Unit` — `onBack` recibe un valor de tipo `() -> Unit`. El contrato no marca este parámetro como anulable. Es un callback: el bloque no posee necesariamente la acción final, sino que notifica/delega al consumidor.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.
- `@Composable` convierte la función en una unidad declarativa de Compose y la somete a reglas de recomposición.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 224 | `val context` | `inferido` | `LocalContext.current` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El `Context` da acceso a recursos/servicios/almacenamiento Android; debe respetarse el ciclo de vida del contexto recibido. |
| 225 | `val uri` | `inferido` | `remember(uriString) { Uri.parse(uriString) }` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Representa una ubicación Android `Uri`; no debe asumirse que siempre corresponde a una ruta de archivo convencional. |
| 226 | `val mimeType` | `inferido` | `remember(uriString, explicitMimeType, name) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 229 | `val extension` | `inferido` | `remember(name, uriString) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 232 | `val title` | `inferido` | `name?.takeIf { it.isNotBlank() }?: when {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 223 | `private fun AttachmentViewerScreen(uriString: String, type: String, name: String?, explicitMimeType: String?, onBack: () -> Unit) {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 233 | `type == "video" \|\| mimeType.startsWith("video/") -> androidx.compose.ui.res.stringResource(R.string.video)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 234 | `type == "image" \|\| mimeType.startsWith("image/") -> androidx.compose.ui.res.stringResource(R.string.image)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 235 | `type == "audio" \|\| type == "voice" \|\| mimeType.startsWith("audio/") -> androidx.compose.ui.res.stringResource(R.string.audio)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 236 | `extension == "pdf" -> "PDF"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 237 | `else -> androidx.compose.ui.res.stringResource(R.string.file)` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |
| 263 | `when {` | Selección múltiple de Kotlin: concentra casos mutuamente evaluados y normalmente define una normalización o estrategia según el valor de entrada. |
| 264 | `type == "image" \|\| mimeType.startsWith("image/") -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 267 | `type == "video" \|\| mimeType.startsWith("video/") -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 270 | `type == "audio" \|\| type == "voice" \|\| mimeType.startsWith("audio/") -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 273 | `extension == "pdf" \|\| mimeType == "application/pdf" -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 276 | `isTextExtension(extension) \|\| mimeType.startsWith("text/") -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 279 | `extension == "docx" \|\| extension == "pptx" \|\| extension == "xlsx" -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 282 | `else -> {` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 1.
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 2.
- **Filtro condicional:** `takeIf` conserva el valor únicamente cuando el predicado se cumple; en caso contrario lo convierte en `null`. Apariciones en este bloque: 1.
- **Límite visual:** La UI impone un límite explícito de tamaño o cantidad de líneas para impedir crecimiento visual no controlado. Apariciones en este bloque: 1.

#### Semántica Compose/lifecycle

- **`remember`:** Conserva valores entre recomposiciones bajo las mismas claves.
- **Callbacks `onClick`:** La lógica asociada se ejecuta por evento de usuario, no durante la composición inicial.
- **`Modifier`:** La cadena de modificadores define tamaño, interacción, dibujo, semántica o posicionamiento; el orden de los modificadores puede ser significativo.

#### Efectos secundarios y recursos

- **Sonido/háptica:** Produce feedback perceptible para el usuario; debe ejecutarse en respuesta a la interacción/estado correspondiente.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `remember`, `Uri.parse`, `resolveMimeType`, `fileExtension`, `it.isNotBlank`, `mimeType.startsWith`, `androidx.compose.ui.res.stringResource`, `Scaffold`, `Modifier.fillMaxSize`, `TopAppBar`, `Text`, `IconButton`, `UiSoundPlayer.playAction`, `onBack`, `Icon`, `openExternally`, `TopAppBarDefaults.topAppBarColors`, `Box`, `padding`, `ImageViewer`, `VideoViewer`, `AudioFileViewer`, `PdfViewer`, `isTextExtension`, `TextFileViewer`, `OfficeTextViewer`, `GenericFileViewer`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.
- No convertir estado de Compose en variables ordinarias sin considerar recomposición y persistencia temporal.

### 4.11 `ImageViewer` — fun, líneas 291–327

```kotlin
private fun ImageViewer(uri: Uri, name: String?) {
    val context = LocalContext.current
    val bitmapLimit = remember(context) {
            val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
            when {
                activityManager?.isLowRamDevice == true -> Size(3072, 3072)
                (activityManager?.memoryClass ?: 256) < 192 -> Size(4096, 4096)
                else -> Size.ORIGINAL
            }
        }
    val request = remember(context, uri, bitmapLimit) {
            ImageRequest.Builder(context).data(uri).size(Size.ORIGINAL).maxBitmapSize(bitmapLimit).precision(Precision.EXACT).scale(
                    Scale.FIT).allowHardware(true).memoryCachePolicy(CachePolicy.DISABLED).build()
        }
    var failed by
        remember(uri) {
            mutableStateOf(false)
        }
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (!failed) {
            AsyncImage(model = request, contentDescription = name, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Fit,
                onSuccess = {
                    failed = false
                }, onError = {
                    failed = true
                })
        } else {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement =
                    Arrangement.Center) {
                Icon(imageVector = Icons.Default.Description, contentDescription = null, modifier = Modifier.size(42.dp))
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = androidx.compose.ui.res.stringResource(R.string.image_format_not_supported), color = MaterialTheme.colorScheme
                            .onBackground)
            }
        }
    }
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `uri: Uri` — `uri` recibe un valor de tipo `Uri`. El contrato no marca este parámetro como anulable. Es una referencia de contenido Android; puede apuntar a content providers y no solo a archivos locales.
- `name: String?` — `name` recibe un valor de tipo `String?`. El contrato permite `null`; el bloque debe decidir explícita o implícitamente qué significa la ausencia.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.
- `@Composable` convierte la función en una unidad declarativa de Compose y la somete a reglas de recomposición.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 292 | `val context` | `inferido` | `LocalContext.current` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El `Context` da acceso a recursos/servicios/almacenamiento Android; debe respetarse el ciclo de vida del contexto recibido. |
| 293 | `val bitmapLimit` | `inferido` | `remember(context) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 294 | `val activityManager` | `inferido` | `context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 301 | `val request` | `inferido` | `remember(context, uri, bitmapLimit) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 295 | `when {` | Selección múltiple de Kotlin: concentra casos mutuamente evaluados y normalmente define una normalización o estrategia según el valor de entrada. |
| 296 | `activityManager?.isLowRamDevice == true -> Size(3072, 3072)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 297 | `(activityManager?.memoryClass ?: 256) < 192 -> Size(4096, 4096)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 298 | `else -> Size.ORIGINAL` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |
| 310 | `if (!failed) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 2.
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 1.

#### Semántica Compose/lifecycle

- **`remember`:** Conserva valores entre recomposiciones bajo las mismas claves.
- **`mutableStateOf`:** Crea estado observable; las lecturas registradas por Compose quedan enlazadas a sus cambios.
- **`Modifier`:** La cadena de modificadores define tamaño, interacción, dibujo, semántica o posicionamiento; el orden de los modificadores puede ser significativo.

#### Efectos secundarios y recursos

- **Estado Compose:** Declara o utiliza estado observable de Compose; una escritura en ese estado puede desencadenar recomposición de los lectores.
- **Caché:** Lee, llena, invalida o administra caché para evitar recomputación o decodificación repetida.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `remember`, `context.getSystemService`, `Size`, `ImageRequest.Builder`, `data`, `size`, `maxBitmapSize`, `precision`, `scale`, `allowHardware`, `memoryCachePolicy`, `build`, `mutableStateOf`, `Box`, `Modifier.fillMaxSize`, `AsyncImage`, `Column`, `Modifier.padding`, `Icon`, `Modifier.size`, `Spacer`, `Modifier.height`, `Text`, `androidx.compose.ui.res.stringResource`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.
- No convertir estado de Compose en variables ordinarias sin considerar recomposición y persistencia temporal.
- Mantener coherencia entre creación, clave, lectura e invalidación de caché; cambiar solo una de esas etapas puede dejar datos obsoletos.

### 4.12 `VideoViewer` — fun, líneas 330–542

```kotlin
private fun VideoViewer(uri: Uri, name: String, mimeType: String) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val player = remember(uri) {
            ExoPlayer.Builder(context).build()
        }
    var prepared by
        remember(uri) {
            mutableStateOf(false)
        }
    var buffering by
        remember(uri) {
            mutableStateOf(true)
        }
    var playing by
        remember(uri) {
            mutableStateOf(false)
        }
    var duration by
        remember(uri) {
            mutableIntStateOf(0)
        }
    var position by
        remember(uri) {
            mutableIntStateOf(0)
        }
    var error by
        remember(uri) {
            mutableStateOf(false)
        }
    var fallbackAttempted by
        remember(uri) {
            mutableStateOf(false)
        }
    DisposableEffect(player, uri) {
        val listener = object :
                Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    when (playbackState) {
                        Player.STATE_BUFFERING -> {
                            buffering = true
                        }
                        Player.STATE_READY -> {
                            buffering = false
                            prepared = true
                            error = false
                            duration = player.duration.coerceAtLeast(0L).coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
                        }
                        Player.STATE_ENDED -> {
                            buffering = false
                            prepared = true
                            playing = false
                            if (duration > 0) {
                                position = duration
                            }
                        }
                        Player.STATE_IDLE -> {
                            buffering = false
                        }
                    }
                }
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    playing = isPlaying
                }
                override fun onPlayerError(playbackException:
                    PlaybackException) {
                    prepared = false
                    playing = false
                    buffering = false
                    /*
                     * ExoPlayer maneja content:// de forma nativa. El fallback
                     * solo se usa con proveedores defectuosos de Android 7/8
                     * que no permiten reabrir correctamente el descriptor.
                     */
                    if (!fallbackAttempted && uri.scheme != "file") {
                        fallbackAttempted = true
                        scope.launch {
                            val fallbackFile = withContext(Dispatchers.IO) {
                                    copyVideoToPlaybackCache(context = context, uri = uri)
                                }
                            if (fallbackFile != null) {
                                try {
                                    error = false
                                    buffering = true
                                    player.stop()
                                    player.clearMediaItems()
                                    player.setMediaItem(MediaItem.fromUri(Uri.fromFile(fallbackFile)))
                                    player.prepare()
                                    player.play()
                                } catch (_: Exception) {
                                    error = true
                                    buffering = false
                                }
                            } else {
                                error = true
                            }
                        }
                    } else {
                        error = true
                    }
                }
            }
        player.addListener(listener)
        try {
            player.setMediaItem(MediaItem.fromUri(uri))
            player.playWhenReady = true
            player.prepare()
        } catch (_: Exception) {
            buffering = false
            error = true
        }
        onDispose {
            try {
                player.removeListener(listener)
            } catch (_: Exception) {
            }
            try {
                player.release()
            } catch (_: Exception) {
            }
        }
    }
    LaunchedEffect(player, prepared) {
        while (true) {
            if (prepared) {
                position = player.currentPosition.coerceAtLeast(0L).coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
                val currentDuration = player.duration.coerceAtLeast(0L).coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
                if (currentDuration > 0) {
                    duration = currentDuration
                }
            }
            delay(300)
        }
    }
    Column(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
            AndroidView(factory = {
                    PlayerView(context).apply {
                        useController = false
                        resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                        setShutterBackgroundColor(android.graphics.Color.BLACK)
                        this.player = player
                    }
                }, update = {
                    if (it.player !== player) {
                        it.player = player
                    }
                }, modifier = Modifier.fillMaxSize())
            if (buffering && !error) {
                CircularProgressIndicator(color = Color.White)
            }
            if (error) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement =
                        Arrangement.Center) {
                    Text(text = androidx.compose.ui.res.stringResource(R.string.video_playback_failed_inside), color = Color.White)
                    Spacer(modifier = Modifier.height(14.dp))
                    Button(onClick = {
                            UiSoundPlayer.playAction(context = context, action = UiActionSound.Link)
                            openExternally(context = context, uri = uri, name = name, mimeType = mimeType)
                        }) {
                        Icon(imageVector = Icons.Default.OpenInNew, contentDescription = null)
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(androidx.compose.ui.res.stringResource(R.string.open_with_other_app))
                    }
                }
            }
        }
        Surface(color = Color.Black, contentColor = Color.White) {
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp)) {
                Slider(value = position.coerceIn(0, duration.coerceAtLeast(1)).toFloat(), onValueChange = {
                            newPosition ->
                        UiSoundPlayer.playActionThrottled(context = context, action = UiActionSound.Navigation, minimumIntervalMs = 60L)
                        val target = newPosition.roundToInt()
                        position = target
                        if (prepared) {
                            try {
                                player.seekTo(target.toLong())
                            } catch (_: Exception) {
                            }
                        }
                    }, valueRange = 0f..duration.coerceAtLeast(1).toFloat(), enabled = prepared && !error)
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(enabled = prepared && !error, onClick = {
                            UiSoundPlayer.playAction(context = context, action = UiActionSound.PlayPause)
                            try {
                                if (player.isPlaying) {
                                    player.pause()
                                } else {
                                    if (duration > 0 && position >= duration - 250) {
                                        player.seekTo(0L)
                                        position = 0
                                    }
                                    player.play()
                                }
                            } catch (_: Exception) {
                            }
                        }) {
                        Icon(imageVector = if (playing) {
                                    Icons.Default.Pause
                                } else {
                                    Icons.Default.PlayArrow
                                }, contentDescription = if (playing) {
                                    androidx.compose.ui.res.stringResource(R.string.pause)
                                } else {
                                    androidx.compose.ui.res.stringResource(R.string.play)
                                }, tint = Color.White)
                    }
                    Text(text = "${formatTime(position)} / " + formatTime(duration), color = Color.White, fontSize = 13.sp)
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

- `uri: Uri` — `uri` recibe un valor de tipo `Uri`. El contrato no marca este parámetro como anulable. Es una referencia de contenido Android; puede apuntar a content providers y no solo a archivos locales.
- `name: String` — `name` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.
- `mimeType: String` — `mimeType` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable. Aunque el tipo sea `String`, semánticamente suele funcionar como clave/enumeración textual; las ramas del código delimitan los valores reconocidos.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.
- `@Composable` convierte la función en una unidad declarativa de Compose y la somete a reglas de recomposición.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 331 | `val context` | `inferido` | `LocalContext.current` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El `Context` da acceso a recursos/servicios/almacenamiento Android; debe respetarse el ciclo de vida del contexto recibido. |
| 332 | `val scope` | `inferido` | `rememberCoroutineScope()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 333 | `val player` | `inferido` | `remember(uri) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 365 | `val listener` | `inferido` | `object :` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 407 | `val fallbackFile` | `inferido` | `withContext(Dispatchers.IO) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 456 | `val currentDuration` | `inferido` | `player.duration.coerceAtLeast(0L).coerceAtMost(Int.MAX_VALUE.toLong()).toInt()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El inicializador limita explícitamente el valor a un rango, evitando que dimensiones/intensidades/sizes inválidos se propaguen. |
| 502 | `val target` | `inferido` | `newPosition.roundToInt()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 364 | `DisposableEffect(player, uri) {` | Efecto de Compose con limpieza explícita: registra trabajo/recurso y exige `onDispose` al abandonar o cambiar las claves. |
| 368 | `when (playbackState) {` | Selección múltiple de Kotlin: concentra casos mutuamente evaluados y normalmente define una normalización o estrategia según el valor de entrada. |
| 369 | `Player.STATE_BUFFERING -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 372 | `Player.STATE_READY -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 378 | `Player.STATE_ENDED -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 382 | `if (duration > 0) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 386 | `Player.STATE_IDLE -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 404 | `if (!fallbackAttempted && uri.scheme != "file") {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 410 | `if (fallbackFile != null) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 411 | `try {` | Frontera de manejo de errores: agrupa operaciones que pueden lanzar excepciones para permitir recuperación o limpieza controlada. |
| 433 | `try {` | Frontera de manejo de errores: agrupa operaciones que pueden lanzar excepciones para permitir recuperación o limpieza controlada. |
| 442 | `try {` | Frontera de manejo de errores: agrupa operaciones que pueden lanzar excepciones para permitir recuperación o limpieza controlada. |
| 446 | `try {` | Frontera de manejo de errores: agrupa operaciones que pueden lanzar excepciones para permitir recuperación o limpieza controlada. |
| 452 | `LaunchedEffect(player, prepared) {` | Efecto de Compose basado en corrutina: se inicia cuando entra en composición y se reinicia si cambian sus claves; se cancela al salir de composición. |
| 453 | `while (true) {` | Bucle condicionado: repite mientras la condición se mantenga verdadera; la lógica interna debe permitir progreso hacia la salida. |
| 454 | `if (prepared) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 457 | `if (currentDuration > 0) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 474 | `if (it.player !== player) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 478 | `if (buffering && !error) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 481 | `if (error) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 500 | `newPosition ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 504 | `if (prepared) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 505 | `try {` | Frontera de manejo de errores: agrupa operaciones que pueden lanzar excepciones para permitir recuperación o limpieza controlada. |
| 514 | `try {` | Frontera de manejo de errores: agrupa operaciones que pueden lanzar excepciones para permitir recuperación o limpieza controlada. |
| 515 | `if (player.isPlaying) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 518 | `if (duration > 0 && position >= duration - 250) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |

**Restricciones concretas que aparecen en este bloque:**
- **Límite numérico:** Se fuerza el valor a un intervalo o umbral para impedir que salgan valores fuera de los límites esperados. Apariciones en este bloque: 9.
- **Trabajo IO:** El trabajo potencialmente bloqueante se desplaza al dispatcher de I/O en vez de ocupar el hilo principal. Apariciones en este bloque: 1.

#### Semántica Compose/lifecycle

- **`remember`:** Conserva valores entre recomposiciones bajo las mismas claves.
- **`mutableStateOf`:** Crea estado observable; las lecturas registradas por Compose quedan enlazadas a sus cambios.
- **`LaunchedEffect`:** Ejecuta una corrutina ligada al ciclo de vida de la composición y a sus claves.
- **Callbacks `onClick`:** La lógica asociada se ejecuta por evento de usuario, no durante la composición inicial.
- **`Modifier`:** La cadena de modificadores define tamaño, interacción, dibujo, semántica o posicionamiento; el orden de los modificadores puede ser significativo.

#### Efectos secundarios y recursos

- **Estado Compose:** Declara o utiliza estado observable de Compose; una escritura en ese estado puede desencadenar recomposición de los lectores.
- **Sistema de archivos/caché:** Accede o modifica almacenamiento/caché; son efectos externos al estado puramente en memoria.
- **Sonido/háptica:** Produce feedback perceptible para el usuario; debe ejecutarse en respuesta a la interacción/estado correspondiente.
- **Caché:** Lee, llena, invalida o administra caché para evitar recomputación o decodificación repetida.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `rememberCoroutineScope`, `remember`, `ExoPlayer.Builder`, `build`, `mutableStateOf`, `mutableIntStateOf`, `DisposableEffect`, `player.duration.coerceAtLeast`, `coerceAtMost`, `Int.MAX_VALUE.toLong`, `toInt`, `withContext`, `copyVideoToPlaybackCache`, `player.stop`, `player.clearMediaItems`, `player.setMediaItem`, `MediaItem.fromUri`, `Uri.fromFile`, `player.prepare`, `player.play`, `player.addListener`, `player.removeListener`, `player.release`, `LaunchedEffect`, `player.currentPosition.coerceAtLeast`, `delay`, `Column`, `Modifier.fillMaxSize`, `background`, `Box`, `Modifier.fillMaxWidth`, `weight`, `AndroidView`, `PlayerView`, `setShutterBackgroundColor`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.
- No mover trabajo bloqueante al hilo principal; el uso de `Dispatchers.IO` forma parte de la protección de rendimiento.
- No convertir estado de Compose en variables ordinarias sin considerar recomposición y persistencia temporal.
- Mantener coherencia entre creación, clave, lectura e invalidación de caché; cambiar solo una de esas etapas puede dejar datos obsoletos.

### 4.13 `AudioFileViewer` — fun, líneas 545–756

```kotlin
private fun AudioFileViewer(uri: Uri, name: String, mimeType: String) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val player = remember(uri) {
            ExoPlayer.Builder(context).build()
        }
    var prepared by
        remember(uri) {
            mutableStateOf(false)
        }
    var buffering by
        remember(uri) {
            mutableStateOf(true)
        }
    var playing by
        remember(uri) {
            mutableStateOf(false)
        }
    var duration by
        remember(uri) {
            mutableIntStateOf(0)
        }
    var position by
        remember(uri) {
            mutableIntStateOf(0)
        }
    var error by
        remember(uri) {
            mutableStateOf(false)
        }
    var fallbackAttempted by
        remember(uri) {
            mutableStateOf(false)
        }
    DisposableEffect(player, uri) {
        val listener = object :
                Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    when (playbackState) {
                        Player.STATE_BUFFERING -> {
                            buffering = true
                        }
                        Player.STATE_READY -> {
                            buffering = false
                            prepared = true
                            error = false
                            duration = player.duration.coerceAtLeast(0L).coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
                        }
                        Player.STATE_ENDED -> {
                            buffering = false
                            prepared = true
                            playing = false
                            if (duration > 0) {
                                position = duration
                            }
                        }
                        Player.STATE_IDLE -> {
                            buffering = false
                        }
                    }
                }
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    playing = isPlaying
                }
                override fun onPlayerError(playbackException:
                    PlaybackException) {
                    prepared = false
                    playing = false
                    buffering = false
                    if (!fallbackAttempted && uri.scheme != "file") {
                        fallbackAttempted = true
                        scope.launch {
                            val fallbackFile = withContext(Dispatchers.IO) {
                                    copyAudioToPlaybackCache(context = context, uri = uri)
                                }
                            if (fallbackFile != null) {
                                try {
                                    error = false
                                    buffering = true
                                    player.stop()
                                    player.clearMediaItems()
                                    player.setMediaItem(MediaItem.fromUri(Uri.fromFile(fallbackFile)))
                                    player.prepare()
                                    player.play()
                                } catch (_: Exception) {
                                    error = true
                                    buffering = false
                                }
                            } else {
                                error = true
                            }
                        }
                    } else {
                        error = true
                    }
                }
            }
        player.addListener(listener)
        try {
            player.setMediaItem(MediaItem.fromUri(uri))
            player.prepare()
        } catch (_: Exception) {
            buffering = false
            error = true
        }
        onDispose {
            try {
                player.removeListener(listener)
            } catch (_: Exception) {
            }
            try {
                player.release()
            } catch (_: Exception) {
            }
        }
    }
    LaunchedEffect(player, prepared) {
        while (true) {
            if (prepared) {
                position = player.currentPosition.coerceAtLeast(0L).coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
                val currentDuration = player.duration.coerceAtLeast(0L).coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
                if (currentDuration > 0) {
                    duration = currentDuration
                }
            }
            delay(300)
        }
    }
    Column(modifier = Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement =
            Arrangement.Center) {
        Surface(modifier = Modifier.size(112.dp), shape = RoundedCornerShape(28.dp), color = MaterialTheme.colorScheme.primaryContainer) {
            Box(contentAlignment = Alignment.Center) {
                if (buffering && !error) {
                    CircularProgressIndicator(modifier = Modifier.size(36.dp))
                } else {
                    Icon(imageVector = if (playing) {
                                Icons.Default.Pause
                            } else {
                                Icons.Default.PlayArrow
                            }, contentDescription = if (playing) {
                                androidx.compose.ui.res.stringResource(R.string.pause)
                            } else {
                                androidx.compose.ui.res.stringResource(R.string.play)
                            }, modifier = Modifier.size(56.dp), tint = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(text = name, fontWeight = FontWeight.Bold, fontSize = 20.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = mimeType.ifBlank {
                        androidx.compose.ui.res.stringResource(R.string.audio)
                    }, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(18.dp))
        Slider(value = position.coerceIn(0, duration.coerceAtLeast(1)).toFloat(), onValueChange = {
                newPosition ->
                UiSoundPlayer.playActionThrottled(context = context, action = UiActionSound.Navigation, minimumIntervalMs = 60L)
                val target = newPosition.roundToInt()
                position = target
                if (prepared) {
                    try {
                        player.seekTo(target.toLong())
                    } catch (_: Exception) {
                    }
                }
            }, valueRange = 0f..duration.coerceAtLeast(1).toFloat(), enabled = prepared && !error, modifier = Modifier.fillMaxWidth())
        Text(text = "${formatTime(position)} / " + formatTime(duration), fontSize = 13.sp, color = MaterialTheme.colorScheme
                    .onSurfaceVariant)
        Spacer(modifier = Modifier.height(16.dp))
        Button(enabled = prepared && !error, onClick = {
                UiSoundPlayer.playAction(context = context, action = UiActionSound.PlayPause)
                try {
                    if (player.isPlaying) {
                        player.pause()
                    } else {
                        if (duration > 0 && position >= duration - 250) {
                            player.seekTo(0L)
                            position = 0
                        }
                        player.play()
                    }
                } catch (_: Exception) {
                }
            }) {
            Icon(imageVector = if (playing) {
                        Icons.Default.Pause
                    } else {
                        Icons.Default.PlayArrow
                    }, contentDescription = null)
            Spacer(modifier = Modifier.size(8.dp))
            Text(if (playing) {
                    androidx.compose.ui.res.stringResource(R.string.pause)
                } else {
                    androidx.compose.ui.res.stringResource(R.string.play)
                })
        }
        if (error) {
            Spacer(modifier = Modifier.height(14.dp))
            Text(text = androidx.compose.ui.res.stringResource(R.string.audio_playback_failed_inside), color = MaterialTheme.colorScheme
                        .error)
            Spacer(modifier = Modifier.height(10.dp))
            Button(onClick = {
                    UiSoundPlayer.playAction(context = context, action = UiActionSound.Link)
                    openExternally(context = context, uri = uri, name = name, mimeType = mimeType)
                }) {
                Icon(imageVector = Icons.Default.OpenInNew, contentDescription = null)
                Spacer(modifier = Modifier.size(8.dp))
                Text(androidx.compose.ui.res.stringResource(R.string.open_with_other_app))
            }
        }
    }
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `uri: Uri` — `uri` recibe un valor de tipo `Uri`. El contrato no marca este parámetro como anulable. Es una referencia de contenido Android; puede apuntar a content providers y no solo a archivos locales.
- `name: String` — `name` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.
- `mimeType: String` — `mimeType` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable. Aunque el tipo sea `String`, semánticamente suele funcionar como clave/enumeración textual; las ramas del código delimitan los valores reconocidos.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.
- `@Composable` convierte la función en una unidad declarativa de Compose y la somete a reglas de recomposición.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 546 | `val context` | `inferido` | `LocalContext.current` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El `Context` da acceso a recursos/servicios/almacenamiento Android; debe respetarse el ciclo de vida del contexto recibido. |
| 547 | `val scope` | `inferido` | `rememberCoroutineScope()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 548 | `val player` | `inferido` | `remember(uri) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 580 | `val listener` | `inferido` | `object :` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 617 | `val fallbackFile` | `inferido` | `withContext(Dispatchers.IO) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 665 | `val currentDuration` | `inferido` | `player.duration.coerceAtLeast(0L).coerceAtMost(Int.MAX_VALUE.toLong()).toInt()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El inicializador limita explícitamente el valor a un rango, evitando que dimensiones/intensidades/sizes inválidos se propaguen. |
| 702 | `val target` | `inferido` | `newPosition.roundToInt()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 579 | `DisposableEffect(player, uri) {` | Efecto de Compose con limpieza explícita: registra trabajo/recurso y exige `onDispose` al abandonar o cambiar las claves. |
| 583 | `when (playbackState) {` | Selección múltiple de Kotlin: concentra casos mutuamente evaluados y normalmente define una normalización o estrategia según el valor de entrada. |
| 584 | `Player.STATE_BUFFERING -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 587 | `Player.STATE_READY -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 593 | `Player.STATE_ENDED -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 597 | `if (duration > 0) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 601 | `Player.STATE_IDLE -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 614 | `if (!fallbackAttempted && uri.scheme != "file") {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 620 | `if (fallbackFile != null) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 621 | `try {` | Frontera de manejo de errores: agrupa operaciones que pueden lanzar excepciones para permitir recuperación o limpieza controlada. |
| 643 | `try {` | Frontera de manejo de errores: agrupa operaciones que pueden lanzar excepciones para permitir recuperación o limpieza controlada. |
| 651 | `try {` | Frontera de manejo de errores: agrupa operaciones que pueden lanzar excepciones para permitir recuperación o limpieza controlada. |
| 655 | `try {` | Frontera de manejo de errores: agrupa operaciones que pueden lanzar excepciones para permitir recuperación o limpieza controlada. |
| 661 | `LaunchedEffect(player, prepared) {` | Efecto de Compose basado en corrutina: se inicia cuando entra en composición y se reinicia si cambian sus claves; se cancela al salir de composición. |
| 662 | `while (true) {` | Bucle condicionado: repite mientras la condición se mantenga verdadera; la lógica interna debe permitir progreso hacia la salida. |
| 663 | `if (prepared) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 666 | `if (currentDuration > 0) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 677 | `if (buffering && !error) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 700 | `newPosition ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 704 | `if (prepared) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 705 | `try {` | Frontera de manejo de errores: agrupa operaciones que pueden lanzar excepciones para permitir recuperación o limpieza controlada. |
| 716 | `try {` | Frontera de manejo de errores: agrupa operaciones que pueden lanzar excepciones para permitir recuperación o limpieza controlada. |
| 717 | `if (player.isPlaying) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 720 | `if (duration > 0 && position >= duration - 250) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 741 | `if (error) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |

**Restricciones concretas que aparecen en este bloque:**
- **Límite numérico:** Se fuerza el valor a un intervalo o umbral para impedir que salgan valores fuera de los límites esperados. Apariciones en este bloque: 9.
- **Límite visual:** La UI impone un límite explícito de tamaño o cantidad de líneas para impedir crecimiento visual no controlado. Apariciones en este bloque: 1.
- **Trabajo IO:** El trabajo potencialmente bloqueante se desplaza al dispatcher de I/O en vez de ocupar el hilo principal. Apariciones en este bloque: 1.

#### Semántica Compose/lifecycle

- **`remember`:** Conserva valores entre recomposiciones bajo las mismas claves.
- **`mutableStateOf`:** Crea estado observable; las lecturas registradas por Compose quedan enlazadas a sus cambios.
- **`LaunchedEffect`:** Ejecuta una corrutina ligada al ciclo de vida de la composición y a sus claves.
- **Callbacks `onClick`:** La lógica asociada se ejecuta por evento de usuario, no durante la composición inicial.
- **`Modifier`:** La cadena de modificadores define tamaño, interacción, dibujo, semántica o posicionamiento; el orden de los modificadores puede ser significativo.

#### Efectos secundarios y recursos

- **Estado Compose:** Declara o utiliza estado observable de Compose; una escritura en ese estado puede desencadenar recomposición de los lectores.
- **Sistema de archivos/caché:** Accede o modifica almacenamiento/caché; son efectos externos al estado puramente en memoria.
- **Sonido/háptica:** Produce feedback perceptible para el usuario; debe ejecutarse en respuesta a la interacción/estado correspondiente.
- **Caché:** Lee, llena, invalida o administra caché para evitar recomputación o decodificación repetida.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `rememberCoroutineScope`, `remember`, `ExoPlayer.Builder`, `build`, `mutableStateOf`, `mutableIntStateOf`, `DisposableEffect`, `player.duration.coerceAtLeast`, `coerceAtMost`, `Int.MAX_VALUE.toLong`, `toInt`, `withContext`, `copyAudioToPlaybackCache`, `player.stop`, `player.clearMediaItems`, `player.setMediaItem`, `MediaItem.fromUri`, `Uri.fromFile`, `player.prepare`, `player.play`, `player.addListener`, `player.removeListener`, `player.release`, `LaunchedEffect`, `player.currentPosition.coerceAtLeast`, `delay`, `Column`, `Modifier.fillMaxSize`, `padding`, `Surface`, `Modifier.size`, `RoundedCornerShape`, `Box`, `CircularProgressIndicator`, `Icon`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.
- No mover trabajo bloqueante al hilo principal; el uso de `Dispatchers.IO` forma parte de la protección de rendimiento.
- No convertir estado de Compose en variables ordinarias sin considerar recomposición y persistencia temporal.
- Mantener coherencia entre creación, clave, lectura e invalidación de caché; cambiar solo una de esas etapas puede dejar datos obsoletos.

### 4.14 `PdfViewer` — fun, líneas 759–794

```kotlin
private fun PdfViewer(uri: Uri) {
    val context = LocalContext.current
    val density = LocalDensity.current
    val handle = remember(uri) {
        try {
            openPdfHandle(context, uri)
        } catch (_: Exception) {
            null
        }
    }
    DisposableEffect(handle) {
        onDispose {
            try {
                handle?.close()
            } catch (_: Exception) {
            }
        }
    }
    if (handle == null) {
        GenericFileViewer(uri = uri, name = androidx.compose.ui.res.stringResource(R.string.pdf_document), extension = "pdf",
            mimeType = "application/pdf")
        return
    }
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val widthPx = with(density) {
            maxWidth.roundToPx().coerceAtLeast(360).coerceAtMost(1080)
        }
        val pages = remember(handle.pageCount) {
            (0 until handle.pageCount).toList()
        }
        LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(items = pages, key = { it }) { pageIndex -> PdfPage(handle = handle, pageIndex = pageIndex, targetWidth = widthPx)
            }
        }
    }
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `uri: Uri` — `uri` recibe un valor de tipo `Uri`. El contrato no marca este parámetro como anulable. Es una referencia de contenido Android; puede apuntar a content providers y no solo a archivos locales.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.
- `@Composable` convierte la función en una unidad declarativa de Compose y la somete a reglas de recomposición.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 760 | `val context` | `inferido` | `LocalContext.current` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El `Context` da acceso a recursos/servicios/almacenamiento Android; debe respetarse el ciclo de vida del contexto recibido. |
| 761 | `val density` | `inferido` | `LocalDensity.current` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 762 | `val handle` | `inferido` | `remember(uri) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 783 | `val widthPx` | `inferido` | `with(density) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. |
| 786 | `val pages` | `inferido` | `remember(handle.pageCount) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 763 | `try {` | Frontera de manejo de errores: agrupa operaciones que pueden lanzar excepciones para permitir recuperación o limpieza controlada. |
| 769 | `DisposableEffect(handle) {` | Efecto de Compose con limpieza explícita: registra trabajo/recurso y exige `onDispose` al abandonar o cambiar las claves. |
| 771 | `try {` | Frontera de manejo de errores: agrupa operaciones que pueden lanzar excepciones para permitir recuperación o limpieza controlada. |
| 777 | `if (handle == null) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 780 | `return` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 1.
- **Límite numérico:** Se fuerza el valor a un intervalo o umbral para impedir que salgan valores fuera de los límites esperados. Apariciones en este bloque: 2.

#### Semántica Compose/lifecycle

- **`remember`:** Conserva valores entre recomposiciones bajo las mismas claves.
- **`Modifier`:** La cadena de modificadores define tamaño, interacción, dibujo, semántica o posicionamiento; el orden de los modificadores puede ser significativo.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `remember`, `openPdfHandle`, `DisposableEffect`, `close`, `GenericFileViewer`, `androidx.compose.ui.res.stringResource`, `BoxWithConstraints`, `Modifier.fillMaxSize`, `with`, `maxWidth.roundToPx`, `coerceAtLeast`, `coerceAtMost`, `toList`, `LazyColumn`, `Arrangement.spacedBy`, `items`, `PdfPage`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- No convertir estado de Compose en variables ordinarias sin considerar recomposición y persistencia temporal.

### 4.15 `PdfPage` — fun, líneas 797–831

```kotlin
private fun PdfPage(handle: PdfHandle, pageIndex: Int, targetWidth: Int) {
    val renderResult by produceState(initialValue = Pair(false, null as Bitmap?), key1 = handle, key2 = pageIndex, key3 = targetWidth) {
        val bitmap = withContext(Dispatchers.IO) {
            try {
                handle.renderPage(pageIndex, targetWidth)
            } catch (_: Exception) {
                null
            }
        }
        value = Pair(true, bitmap)
    }
    val completed = renderResult.first
    val bitmap = renderResult.second
    Surface(modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp), shape = RoundedCornerShape(10.dp), shadowElevation = 1.dp,
        color = Color.White) {
        when {
            !completed -> {
                Box(modifier = Modifier.fillMaxWidth().height(240.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            bitmap == null -> {
                Box(modifier = Modifier.fillMaxWidth().height(180.dp).padding(18.dp), contentAlignment = Alignment.Center) {
                    Text(text = androidx.compose.ui.res.stringResource(R.string.pdf_page_load_failed, pageIndex + 1),
                        color = Color.DarkGray, fontSize = 14.sp)
                }
            }
            else -> {
                Image(bitmap = bitmap.asImageBitmap(),
                    contentDescription = androidx.compose.ui.res.stringResource(R.string.pdf_page_description, pageIndex + 1),
                    modifier = Modifier.fillMaxWidth(), contentScale = ContentScale.FillWidth)
            }
        }
    }
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `handle: PdfHandle` — `handle` recibe un valor de tipo `PdfHandle`. El contrato no marca este parámetro como anulable.
- `pageIndex: Int` — `pageIndex` recibe un valor de tipo `Int`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.
- `targetWidth: Int` — `targetWidth` recibe un valor de tipo `Int`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.
- `@Composable` convierte la función en una unidad declarativa de Compose y la somete a reglas de recomposición.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 798 | `val renderResult` | `inferido` | `by produceState(initialValue = Pair(false, null as Bitmap?), key1 = handle, key2 = pageIndex, key3 =…` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 799 | `val bitmap` | `inferido` | `withContext(Dispatchers.IO) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 808 | `val completed` | `inferido` | `renderResult.first` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 809 | `val bitmap` | `inferido` | `renderResult.second` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 800 | `try {` | Frontera de manejo de errores: agrupa operaciones que pueden lanzar excepciones para permitir recuperación o limpieza controlada. |
| 812 | `when {` | Selección múltiple de Kotlin: concentra casos mutuamente evaluados y normalmente define una normalización o estrategia según el valor de entrada. |
| 813 | `!completed -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 818 | `bitmap == null -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 824 | `else -> {` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |

**Restricciones concretas que aparecen en este bloque:**
- **Trabajo IO:** El trabajo potencialmente bloqueante se desplaza al dispatcher de I/O en vez de ocupar el hilo principal. Apariciones en este bloque: 1.

#### Semántica Compose/lifecycle

- **`Modifier`:** La cadena de modificadores define tamaño, interacción, dibujo, semántica o posicionamiento; el orden de los modificadores puede ser significativo.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `produceState`, `Pair`, `withContext`, `handle.renderPage`, `Surface`, `Modifier.fillMaxWidth`, `padding`, `RoundedCornerShape`, `Box`, `height`, `CircularProgressIndicator`, `Text`, `androidx.compose.ui.res.stringResource`, `Image`, `bitmap.asImageBitmap`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.
- No mover trabajo bloqueante al hilo principal; el uso de `Dispatchers.IO` forma parte de la protección de rendimiento.

### 4.16 `PdfHandle` — class, líneas 833–851

```kotlin
private class PdfHandle(private val descriptor: ParcelFileDescriptor, private val renderer: PdfRenderer) : Closeable {
    val pageCount: Int
        get() = renderer.pageCount
    @Synchronized
    fun renderPage(pageIndex: Int, targetWidth: Int): Bitmap {
        renderer.openPage(pageIndex).use { page -> val safeWidth = targetWidth.coerceAtLeast(1)
            val scale = safeWidth.toFloat() / page.width.toFloat()
            val targetHeight = (page.height * scale).roundToInt().coerceAtLeast(1)
            val bitmap = Bitmap.createBitmap(safeWidth, targetHeight, Bitmap.Config.ARGB_8888)
            Canvas(bitmap).drawColor(AndroidColor.WHITE)
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            return bitmap
        }
    }
    override fun close() {
        renderer.close()
        descriptor.close()
    }
}
```

#### Qué hace y por qué existe

Activity especializada en abrir y visualizar adjuntos. Decide el tratamiento según tipo/MIME y prepara la experiencia para imagen, video, audio, PDF, texto, Office u otros archivos soportados.

#### Contrato de la declaración

**Parámetros del constructor/encabezado:**
- `private val descriptor: ParcelFileDescriptor` — No se pudo separar automáticamente nombre/tipo; se conserva la firma exacta como contrato.
- `private val renderer: PdfRenderer` — No se pudo separar automáticamente nombre/tipo; se conserva la firma exacta como contrato.
**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 834 | `val pageCount` | `Int` | `(sin inicializador en la declaración)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo declarado es `Int`. No declara nulabilidad explícita. |
| 839 | `val scale` | `inferido` | `safeWidth.toFloat() / page.width.toFloat()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 840 | `val targetHeight` | `inferido` | `(page.height * scale).roundToInt().coerceAtLeast(1)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. El inicializador limita explícitamente el valor a un rango, evitando que dimensiones/intensidades/sizes inválidos se propaguen. |
| 841 | `val bitmap` | `inferido` | `Bitmap.createBitmap(safeWidth, targetHeight, Bitmap.Config.ARGB_8888)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 844 | `return bitmap` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

**Restricciones concretas que aparecen en este bloque:**
- **Límite numérico:** Se fuerza el valor a un intervalo o umbral para impedir que salgan valores fuera de los límites esperados. Apariciones en este bloque: 2.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `get`, `renderer.openPage`, `targetWidth.coerceAtLeast`, `safeWidth.toFloat`, `page.width.toFloat`, `roundToInt`, `coerceAtLeast`, `Bitmap.createBitmap`, `Canvas`, `drawColor`, `page.render`, `renderer.close`, `descriptor.close`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.17 `renderPage` — fun, líneas 837–846

```kotlin
    fun renderPage(pageIndex: Int, targetWidth: Int): Bitmap {
        renderer.openPage(pageIndex).use { page -> val safeWidth = targetWidth.coerceAtLeast(1)
            val scale = safeWidth.toFloat() / page.width.toFloat()
            val targetHeight = (page.height * scale).roundToInt().coerceAtLeast(1)
            val bitmap = Bitmap.createBitmap(safeWidth, targetHeight, Bitmap.Config.ARGB_8888)
            Canvas(bitmap).drawColor(AndroidColor.WHITE)
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            return bitmap
        }
    }
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `pageIndex: Int` — `pageIndex` recibe un valor de tipo `Int`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.
- `targetWidth: Int` — `targetWidth` recibe un valor de tipo `Int`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.

**Retorno:** `Bitmap`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.


#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 839 | `val scale` | `inferido` | `safeWidth.toFloat() / page.width.toFloat()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 840 | `val targetHeight` | `inferido` | `(page.height * scale).roundToInt().coerceAtLeast(1)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. El inicializador limita explícitamente el valor a un rango, evitando que dimensiones/intensidades/sizes inválidos se propaguen. |
| 841 | `val bitmap` | `inferido` | `Bitmap.createBitmap(safeWidth, targetHeight, Bitmap.Config.ARGB_8888)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 844 | `return bitmap` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

**Restricciones concretas que aparecen en este bloque:**
- **Límite numérico:** Se fuerza el valor a un intervalo o umbral para impedir que salgan valores fuera de los límites esperados. Apariciones en este bloque: 2.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `renderer.openPage`, `targetWidth.coerceAtLeast`, `safeWidth.toFloat`, `page.width.toFloat`, `roundToInt`, `coerceAtLeast`, `Bitmap.createBitmap`, `Canvas`, `drawColor`, `page.render`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.18 `close` — fun, líneas 847–850

```kotlin
    override fun close() {
        renderer.close()
        descriptor.close()
    }
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

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

Entre las llamadas presentes están: `renderer.close`, `descriptor.close`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.19 `openPdfHandle` — fun, líneas 853–887

```kotlin
private fun openPdfHandle(context: Context, uri: Uri): PdfHandle {
    fun createHandle(descriptor: ParcelFileDescriptor): PdfHandle {
        return try {
            PdfHandle(descriptor = descriptor, renderer = PdfRenderer(descriptor))
        } catch (error: Exception) {
            try {
                descriptor.close()
            } catch (_: Exception) {
            }
            throw error
        }
    }
    if (uri.scheme == "file") {
        val path = uri.path?: throw IllegalArgumentException("Ruta PDF inválida")
        return createHandle(ParcelFileDescriptor.open(File(path), ParcelFileDescriptor.MODE_READ_ONLY))
    }
    /*
     * Muchos DocumentsProvider permiten abrir el PDF directamente. Algunos
     * proveedores de Android 8/9 entregan un descriptor que PdfRenderer no
     * puede buscar (seek). Primero intentamos el descriptor original y, si
     * falla, copiamos el documento al cache privado para obtener un archivo
     * local y completamente seekable.
     */
    try {
        val directDescriptor = context.contentResolver.openFileDescriptor(uri, "r")
        if (directDescriptor != null) {
            return createHandle(directDescriptor)
        }
    } catch (_: Exception) {
        // Continuamos con el fallback local.
    }
    val cachedPdf = copyDocumentToViewerCache(context = context, uri = uri, extension = "pdf", prefix = "pdf")
            ?: throw IllegalArgumentException("No se pudo abrir el PDF")
    return createHandle(ParcelFileDescriptor.open(cachedPdf, ParcelFileDescriptor.MODE_READ_ONLY))
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `context: Context` — `context` recibe un valor de tipo `Context`. El contrato no marca este parámetro como anulable. Da acceso a recursos y APIs Android; su vida útil debe corresponder con el uso realizado.
- `uri: Uri` — `uri` recibe un valor de tipo `Uri`. El contrato no marca este parámetro como anulable. Es una referencia de contenido Android; puede apuntar a content providers y no solo a archivos locales.

**Retorno:** `PdfHandle`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 866 | `val path` | `inferido` | `uri.path?: throw IllegalArgumentException("Ruta PDF inválida")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 877 | `val directDescriptor` | `inferido` | `context.contentResolver.openFileDescriptor(uri, "r")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 884 | `val cachedPdf` | `inferido` | `copyDocumentToViewerCache(context = context, uri = uri, extension = "pdf", prefix = "pdf")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Forma parte de la estrategia de reutilización de resultados para evitar repetir trabajo costoso. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 855 | `return try {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 858 | `try {` | Frontera de manejo de errores: agrupa operaciones que pueden lanzar excepciones para permitir recuperación o limpieza controlada. |
| 865 | `if (uri.scheme == "file") {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 867 | `return createHandle(ParcelFileDescriptor.open(File(path), ParcelFileDescriptor.MODE_READ_ONLY))` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 876 | `try {` | Frontera de manejo de errores: agrupa operaciones que pueden lanzar excepciones para permitir recuperación o limpieza controlada. |
| 878 | `if (directDescriptor != null) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 879 | `return createHandle(directDescriptor)` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 886 | `return createHandle(ParcelFileDescriptor.open(cachedPdf, ParcelFileDescriptor.MODE_READ_ONLY))` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

**Restricciones concretas que aparecen en este bloque:**
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 2.

#### Efectos secundarios y recursos

- **Sistema de archivos/caché:** Accede o modifica almacenamiento/caché; son efectos externos al estado puramente en memoria.
- **Caché:** Lee, llena, invalida o administra caché para evitar recomputación o decodificación repetida.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `PdfHandle`, `PdfRenderer`, `descriptor.close`, `IllegalArgumentException`, `createHandle`, `ParcelFileDescriptor.open`, `File`, `context.contentResolver.openFileDescriptor`, `copyDocumentToViewerCache`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener coherencia entre creación, clave, lectura e invalidación de caché; cambiar solo una de esas etapas puede dejar datos obsoletos.

### 4.20 `createHandle` — fun, líneas 854–864

```kotlin
    fun createHandle(descriptor: ParcelFileDescriptor): PdfHandle {
        return try {
            PdfHandle(descriptor = descriptor, renderer = PdfRenderer(descriptor))
        } catch (error: Exception) {
            try {
                descriptor.close()
            } catch (_: Exception) {
            }
            throw error
        }
    }
```

#### Qué hace y por qué existe

Construye un valor/recurso derivado a partir de los parámetros y reglas internas del bloque.

#### Contrato de la declaración

**Parámetros:**

- `descriptor: ParcelFileDescriptor` — `descriptor` recibe un valor de tipo `ParcelFileDescriptor`. El contrato no marca este parámetro como anulable.

**Retorno:** `PdfHandle`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.


#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 855 | `return try {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 858 | `try {` | Frontera de manejo de errores: agrupa operaciones que pueden lanzar excepciones para permitir recuperación o limpieza controlada. |

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `PdfHandle`, `PdfRenderer`, `descriptor.close`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.21 `TextFileViewer` — fun, líneas 890–912

```kotlin
private fun TextFileViewer(uri: Uri, extension: String) {
    val context = LocalContext.current
    val text by produceState<String?>(initialValue = null, key1 = uri) {
        value = withContext(Dispatchers.IO) {
            readTextPreview(context, uri)
        }
    }
    if (text == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
            Text(text = text!!, fontFamily = if (extension in setOf("txt", "log", "json", "xml", "csv", "md",
                        "kt", "java", "gradle", "kts", "py", "js", "ts", "css", "html", "htm", "sh", "c", "cpp",
                        "h", "hpp", "ini", "cfg", "yaml", "yml")) {
                    FontFamily.Monospace
                } else {
                    FontFamily.Default
                }, fontSize = 14.sp, lineHeight = 20.sp, color = MaterialTheme.colorScheme.onBackground)
        }
    }
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `uri: Uri` — `uri` recibe un valor de tipo `Uri`. El contrato no marca este parámetro como anulable. Es una referencia de contenido Android; puede apuntar a content providers y no solo a archivos locales.
- `extension: String` — `extension` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.
- `@Composable` convierte la función en una unidad declarativa de Compose y la somete a reglas de recomposición.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 891 | `val context` | `inferido` | `LocalContext.current` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El `Context` da acceso a recursos/servicios/almacenamiento Android; debe respetarse el ciclo de vida del contexto recibido. |
| 892 | `val text` | `inferido` | `by produceState<String?>(initialValue = null, key1 = uri) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 897 | `if (text == null) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |

**Restricciones concretas que aparecen en este bloque:**
- **Aserción no nula:** La aserción `!!` exige un valor no nulo en tiempo de ejecución; si la suposición falla se produce una excepción. Apariciones en este bloque: 1.
- **Trabajo IO:** El trabajo potencialmente bloqueante se desplaza al dispatcher de I/O en vez de ocupar el hilo principal. Apariciones en este bloque: 1.

#### Semántica Compose/lifecycle

- **`remember`:** Conserva valores entre recomposiciones bajo las mismas claves.
- **`Modifier`:** La cadena de modificadores define tamaño, interacción, dibujo, semántica o posicionamiento; el orden de los modificadores puede ser significativo.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `withContext`, `readTextPreview`, `Box`, `Modifier.fillMaxSize`, `CircularProgressIndicator`, `Column`, `verticalScroll`, `rememberScrollState`, `padding`, `Text`, `setOf`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- No mover trabajo bloqueante al hilo principal; el uso de `Dispatchers.IO` forma parte de la protección de rendimiento.
- No convertir estado de Compose en variables ordinarias sin considerar recomposición y persistencia temporal.

### 4.22 `OfficeTextViewer` — fun, líneas 915–938

```kotlin
private fun OfficeTextViewer(uri: Uri, extension: String, name: String, mimeType: String) {
    val context = LocalContext.current
    val extracted by produceState<String?>(initialValue = null, key1 = uri, key2 = extension) {
        value = withContext(Dispatchers.IO) {
            extractOfficeText(context = context, uri = uri, extension = extension)
        }
    }
    if (extracted == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }
    if (extracted!!.isBlank()) {
        GenericFileViewer(uri = uri, name = name, extension = extension, mimeType = mimeType)
        return
    }
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp)) {
        Text(text = "Vista de texto extraída del documento", fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary,
            fontSize = 13.sp)
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = extracted!!, color = MaterialTheme.colorScheme.onBackground, fontSize = 15.sp, lineHeight = 22.sp)
    }
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `uri: Uri` — `uri` recibe un valor de tipo `Uri`. El contrato no marca este parámetro como anulable. Es una referencia de contenido Android; puede apuntar a content providers y no solo a archivos locales.
- `extension: String` — `extension` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.
- `name: String` — `name` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.
- `mimeType: String` — `mimeType` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable. Aunque el tipo sea `String`, semánticamente suele funcionar como clave/enumeración textual; las ramas del código delimitan los valores reconocidos.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.
- `@Composable` convierte la función en una unidad declarativa de Compose y la somete a reglas de recomposición.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 916 | `val context` | `inferido` | `LocalContext.current` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El `Context` da acceso a recursos/servicios/almacenamiento Android; debe respetarse el ciclo de vida del contexto recibido. |
| 917 | `val extracted` | `inferido` | `by produceState<String?>(initialValue = null, key1 = uri, key2 = extension) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 922 | `if (extracted == null) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 926 | `return` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 928 | `if (extracted!!.isBlank()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 930 | `return` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

**Restricciones concretas que aparecen en este bloque:**
- **Aserción no nula:** La aserción `!!` exige un valor no nulo en tiempo de ejecución; si la suposición falla se produce una excepción. Apariciones en este bloque: 2.
- **Trabajo IO:** El trabajo potencialmente bloqueante se desplaza al dispatcher de I/O en vez de ocupar el hilo principal. Apariciones en este bloque: 1.

#### Semántica Compose/lifecycle

- **`remember`:** Conserva valores entre recomposiciones bajo las mismas claves.
- **`Modifier`:** La cadena de modificadores define tamaño, interacción, dibujo, semántica o posicionamiento; el orden de los modificadores puede ser significativo.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `withContext`, `extractOfficeText`, `Box`, `Modifier.fillMaxSize`, `CircularProgressIndicator`, `isBlank`, `GenericFileViewer`, `Column`, `verticalScroll`, `rememberScrollState`, `padding`, `Text`, `Spacer`, `Modifier.height`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- No mover trabajo bloqueante al hilo principal; el uso de `Dispatchers.IO` forma parte de la protección de rendimiento.
- No convertir estado de Compose en variables ordinarias sin considerar recomposición y persistencia temporal.

### 4.23 `GenericFileViewer` — fun, líneas 941–990

```kotlin
private fun GenericFileViewer(uri: Uri, name: String, extension: String, mimeType: String) {
    val context = LocalContext.current
    val fileSize by produceState<Long?>(initialValue = null, key1 = uri) {
        value = withContext(Dispatchers.IO) {
            resolveFileSize(context, uri)
        }
    }
    Column(modifier = Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {
        Surface(modifier = Modifier.size(120.dp), shape = RoundedCornerShape(28.dp), color = MaterialTheme.colorScheme.primaryContainer) {
            Box(contentAlignment = Alignment.Center) {
                Icon(imageVector = if (extension == "pdf") {
                        Icons.Default.PictureAsPdf
                    } else {
                        Icons.Default.Description
                    }, contentDescription = null, modifier = Modifier.size(58.dp), tint = MaterialTheme.colorScheme.onPrimaryContainer)
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(text = name, fontWeight = FontWeight.Bold, fontSize = 20.sp, maxLines = 3, overflow = TextOverflow.Ellipsis)
        Spacer(modifier = Modifier.height(8.dp))
        val metadata = buildString {
            if (extension.isNotBlank()) {
                append(extension.uppercase(Locale.ROOT))
            }
            if (mimeType.isNotBlank()) {
                if (isNotEmpty()) append(" · ")
                append(mimeType)
            }
            fileSize?.let { size -> if (isNotEmpty()) append(" · ")
                append(formatFileSize(size))
            }
        }
        if (metadata.isNotBlank()) {
            Text(text = metadata, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
        }
        Spacer(modifier = Modifier.height(18.dp))
        Text(text = "Este formato no tiene una vista previa completa integrada. Puedes abrirlo con una aplicación compatible.",
            color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(22.dp))
        Button(onClick = {
                UiSoundPlayer.playAction(context = context, action = UiActionSound.Link)
                openExternally(context = context, uri = uri, name = name, mimeType = mimeType)
            }) {
            Icon(imageVector = Icons.Default.OpenInNew, contentDescription = null)
            Spacer(modifier = Modifier.size(8.dp))
            Text(androidx.compose.ui.res.stringResource(R.string.open_with_other_app))
        }
    }
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `uri: Uri` — `uri` recibe un valor de tipo `Uri`. El contrato no marca este parámetro como anulable. Es una referencia de contenido Android; puede apuntar a content providers y no solo a archivos locales.
- `name: String` — `name` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.
- `extension: String` — `extension` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.
- `mimeType: String` — `mimeType` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable. Aunque el tipo sea `String`, semánticamente suele funcionar como clave/enumeración textual; las ramas del código delimitan los valores reconocidos.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.
- `@Composable` convierte la función en una unidad declarativa de Compose y la somete a reglas de recomposición.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 942 | `val context` | `inferido` | `LocalContext.current` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El `Context` da acceso a recursos/servicios/almacenamiento Android; debe respetarse el ciclo de vida del contexto recibido. |
| 943 | `val fileSize` | `inferido` | `by produceState<Long?>(initialValue = null, key1 = uri) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 962 | `val metadata` | `inferido` | `buildString {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 963 | `if (extension.isNotBlank()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 966 | `if (mimeType.isNotBlank()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 967 | `if (isNotEmpty()) append(" · ")` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 974 | `if (metadata.isNotBlank()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 1.
- **Límite visual:** La UI impone un límite explícito de tamaño o cantidad de líneas para impedir crecimiento visual no controlado. Apariciones en este bloque: 1.
- **Trabajo IO:** El trabajo potencialmente bloqueante se desplaza al dispatcher de I/O en vez de ocupar el hilo principal. Apariciones en este bloque: 1.

#### Semántica Compose/lifecycle

- **Callbacks `onClick`:** La lógica asociada se ejecuta por evento de usuario, no durante la composición inicial.
- **`Modifier`:** La cadena de modificadores define tamaño, interacción, dibujo, semántica o posicionamiento; el orden de los modificadores puede ser significativo.

#### Efectos secundarios y recursos

- **Sonido/háptica:** Produce feedback perceptible para el usuario; debe ejecutarse en respuesta a la interacción/estado correspondiente.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `withContext`, `resolveFileSize`, `Column`, `Modifier.fillMaxSize`, `padding`, `Surface`, `Modifier.size`, `RoundedCornerShape`, `Box`, `Icon`, `Spacer`, `Modifier.height`, `Text`, `extension.isNotBlank`, `append`, `extension.uppercase`, `mimeType.isNotBlank`, `isNotEmpty`, `formatFileSize`, `metadata.isNotBlank`, `Button`, `UiSoundPlayer.playAction`, `openExternally`, `androidx.compose.ui.res.stringResource`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- No mover trabajo bloqueante al hilo principal; el uso de `Dispatchers.IO` forma parte de la protección de rendimiento.

### 4.24 `copyDocumentToViewerCache` — fun, líneas 992–1012

```kotlin
private fun copyDocumentToViewerCache(context: Context, uri: Uri, extension: String, prefix: String): File? {
    return try {
        val directory = File(context.cacheDir, "viewer_documents")
        if (!directory.exists() && !directory.mkdirs()) {
            return null
        }
        val safeExtension = extension.trim().trimStart('.').ifBlank { "bin" }
        val key = uri.toString().hashCode().toUInt().toString(16)
        val file = File(directory, "${prefix}_${key}.${safeExtension}")
        if (file.exists() && file.length() > 0L) {
            return file
        }
        openInputStream(context, uri).use { input -> file.outputStream().buffered(64 * 1024).use { output -> input.copyTo(out = output,
                    bufferSize = 64 * 1024)
            }
        }
        file.takeIf { it.exists() && it.length() > 0L }
    } catch (_: Exception) {
        null
    }
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `context: Context` — `context` recibe un valor de tipo `Context`. El contrato no marca este parámetro como anulable. Da acceso a recursos y APIs Android; su vida útil debe corresponder con el uso realizado.
- `uri: Uri` — `uri` recibe un valor de tipo `Uri`. El contrato no marca este parámetro como anulable. Es una referencia de contenido Android; puede apuntar a content providers y no solo a archivos locales.
- `extension: String` — `extension` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.
- `prefix: String` — `prefix` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `File?`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 994 | `val directory` | `inferido` | `File(context.cacheDir, "viewer_documents")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 998 | `val safeExtension` | `inferido` | `extension.trim().trimStart('.').ifBlank { "bin" }` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 999 | `val key` | `inferido` | `uri.toString().hashCode().toUInt().toString(16)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1000 | `val file` | `inferido` | `File(directory, "${prefix}_${key}.${safeExtension}")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 993 | `return try {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 995 | `if (!directory.exists() && !directory.mkdirs()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 996 | `return null` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 1001 | `if (file.exists() && file.length() > 0L) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1002 | `return file` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

**Restricciones concretas que aparecen en este bloque:**
- **Filtro condicional:** `takeIf` conserva el valor únicamente cuando el predicado se cumple; en caso contrario lo convierte en `null`. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

- **Sistema de archivos/caché:** Accede o modifica almacenamiento/caché; son efectos externos al estado puramente en memoria.
- **Caché:** Lee, llena, invalida o administra caché para evitar recomputación o decodificación repetida.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `File`, `directory.exists`, `directory.mkdirs`, `extension.trim`, `trimStart`, `uri.toString`, `hashCode`, `toUInt`, `toString`, `file.exists`, `file.length`, `openInputStream`, `file.outputStream`, `buffered`, `input.copyTo`, `it.exists`, `it.length`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener coherencia entre creación, clave, lectura e invalidación de caché; cambiar solo una de esas etapas puede dejar datos obsoletos.

### 4.25 `copyVideoToPlaybackCache` — fun, líneas 1014–1036

```kotlin
private fun copyVideoToPlaybackCache(context: Context, uri: Uri): File? {
    return try {
        val directory = File(context.cacheDir, "viewer_video")
        if (!directory.exists() && !directory.mkdirs()) {
            return null
        }
        val mime = resolveMimeType(context = context, uri = uri, name = null)
        val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mime)?.takeIf { it.isNotBlank() }?: fileExtension(null, uri)
                .takeIf { it.isNotBlank() }?: "mp4"
        val key = uri.toString().hashCode().toUInt().toString(16)
        val file = File(directory, "video_$key.$extension")
        if (file.exists() && file.length() > 0L) {
            return file
        }
        openInputStream(context, uri).use { input -> file.outputStream().buffered(64 * 1024).use { output -> input.copyTo(out = output,
                    bufferSize = 64 * 1024)
            }
        }
        file.takeIf { it.exists() && it.length() > 0L }
    } catch (_: Exception) {
        null
    }
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `context: Context` — `context` recibe un valor de tipo `Context`. El contrato no marca este parámetro como anulable. Da acceso a recursos y APIs Android; su vida útil debe corresponder con el uso realizado.
- `uri: Uri` — `uri` recibe un valor de tipo `Uri`. El contrato no marca este parámetro como anulable. Es una referencia de contenido Android; puede apuntar a content providers y no solo a archivos locales.

**Retorno:** `File?`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 1016 | `val directory` | `inferido` | `File(context.cacheDir, "viewer_video")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1020 | `val mime` | `inferido` | `resolveMimeType(context = context, uri = uri, name = null)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1021 | `val extension` | `inferido` | `MimeTypeMap.getSingleton().getExtensionFromMimeType(mime)?.takeIf { it.isNotBlank() }?: fileExtensio…` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 1023 | `val key` | `inferido` | `uri.toString().hashCode().toUInt().toString(16)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1024 | `val file` | `inferido` | `File(directory, "video_$key.$extension")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 1015 | `return try {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 1017 | `if (!directory.exists() && !directory.mkdirs()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1018 | `return null` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 1025 | `if (file.exists() && file.length() > 0L) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1026 | `return file` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 1.
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 2.
- **Filtro condicional:** `takeIf` conserva el valor únicamente cuando el predicado se cumple; en caso contrario lo convierte en `null`. Apariciones en este bloque: 3.

#### Efectos secundarios y recursos

- **Sistema de archivos/caché:** Accede o modifica almacenamiento/caché; son efectos externos al estado puramente en memoria.
- **Caché:** Lee, llena, invalida o administra caché para evitar recomputación o decodificación repetida.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `File`, `directory.exists`, `directory.mkdirs`, `resolveMimeType`, `MimeTypeMap.getSingleton`, `getExtensionFromMimeType`, `it.isNotBlank`, `fileExtension`, `uri.toString`, `hashCode`, `toUInt`, `toString`, `file.exists`, `file.length`, `openInputStream`, `file.outputStream`, `buffered`, `input.copyTo`, `it.exists`, `it.length`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener coherencia entre creación, clave, lectura e invalidación de caché; cambiar solo una de esas etapas puede dejar datos obsoletos.

### 4.26 `copyAudioToPlaybackCache` — fun, líneas 1038–1068

```kotlin
private fun copyAudioToPlaybackCache(context: Context, uri: Uri): File? {
    return try {
        val directory = File(context.cacheDir, "viewer_audio")
        if (!directory.exists() && !directory.mkdirs()) {
            return null
        }
        val mime = resolveMimeType(context = context, uri = uri, name = null)
        val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mime)?.takeIf {
                    it.isNotBlank()
                }?: fileExtension(null, uri).takeIf {
                        it.isNotBlank()
                    }?: "m4a"
        val key = uri.toString().hashCode().toUInt().toString(16)
        val file = File(directory, "audio_$key.$extension")
        if (file.exists() && file.length() > 0L) {
            return file
        }
        openInputStream(context, uri).use {
            input ->
            file.outputStream().buffered(64 * 1024).use {
                    output ->
                    input.copyTo(out = output, bufferSize = 64 * 1024)
                }
        }
        file.takeIf {
            it.exists() && it.length() > 0L
        }
    } catch (_: Exception) {
        null
    }
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `context: Context` — `context` recibe un valor de tipo `Context`. El contrato no marca este parámetro como anulable. Da acceso a recursos y APIs Android; su vida útil debe corresponder con el uso realizado.
- `uri: Uri` — `uri` recibe un valor de tipo `Uri`. El contrato no marca este parámetro como anulable. Es una referencia de contenido Android; puede apuntar a content providers y no solo a archivos locales.

**Retorno:** `File?`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 1040 | `val directory` | `inferido` | `File(context.cacheDir, "viewer_audio")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1044 | `val mime` | `inferido` | `resolveMimeType(context = context, uri = uri, name = null)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1045 | `val extension` | `inferido` | `MimeTypeMap.getSingleton().getExtensionFromMimeType(mime)?.takeIf {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 1050 | `val key` | `inferido` | `uri.toString().hashCode().toUInt().toString(16)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1051 | `val file` | `inferido` | `File(directory, "audio_$key.$extension")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 1039 | `return try {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 1041 | `if (!directory.exists() && !directory.mkdirs()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1042 | `return null` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 1052 | `if (file.exists() && file.length() > 0L) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1053 | `return file` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 1056 | `input ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 1058 | `output ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 1.
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 2.
- **Filtro condicional:** `takeIf` conserva el valor únicamente cuando el predicado se cumple; en caso contrario lo convierte en `null`. Apariciones en este bloque: 3.

#### Efectos secundarios y recursos

- **Sistema de archivos/caché:** Accede o modifica almacenamiento/caché; son efectos externos al estado puramente en memoria.
- **Caché:** Lee, llena, invalida o administra caché para evitar recomputación o decodificación repetida.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `File`, `directory.exists`, `directory.mkdirs`, `resolveMimeType`, `MimeTypeMap.getSingleton`, `getExtensionFromMimeType`, `it.isNotBlank`, `fileExtension`, `uri.toString`, `hashCode`, `toUInt`, `toString`, `file.exists`, `file.length`, `openInputStream`, `file.outputStream`, `buffered`, `input.copyTo`, `it.exists`, `it.length`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener coherencia entre creación, clave, lectura e invalidación de caché; cambiar solo una de esas etapas puede dejar datos obsoletos.

### 4.27 `readTextPreview` — fun, líneas 1070–1088

```kotlin
private fun readTextPreview(context: Context, uri: Uri): String {
    return try {
        openInputStream(context, uri).use { input -> val bytes = input.readUpTo(MAX_TEXT_BYTES)
            val text = bytes.toString(Charsets.UTF_8)
            if (text.indexOf('\u0000') >= 0) {
                "El archivo parece contener datos binarios y no puede mostrarse como texto."
            } else {
                buildString {
                    append(text)
                    if (bytes.size >= MAX_TEXT_BYTES) {
                        append("\n\n… Vista limitada a los primeros 2 MB del archivo.")
                    }
                }
            }
        }
    } catch (e: Exception) {
        "No se pudo leer el archivo.\n\n${e.message.orEmpty()}"
    }
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `context: Context` — `context` recibe un valor de tipo `Context`. El contrato no marca este parámetro como anulable. Da acceso a recursos y APIs Android; su vida útil debe corresponder con el uso realizado.
- `uri: Uri` — `uri` recibe un valor de tipo `Uri`. El contrato no marca este parámetro como anulable. Es una referencia de contenido Android; puede apuntar a content providers y no solo a archivos locales.

**Retorno:** `String`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 1073 | `val text` | `inferido` | `bytes.toString(Charsets.UTF_8)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 1071 | `return try {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 1074 | `if (text.indexOf('\u0000') >= 0) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1079 | `if (bytes.size >= MAX_TEXT_BYTES) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |

**Restricciones concretas que aparecen en este bloque:**
- **Normalización vacía:** `orEmpty` transforma una referencia nula en una colección/cadena vacía, simplificando el consumo posterior. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `openInputStream`, `input.readUpTo`, `bytes.toString`, `text.indexOf`, `append`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.28 `extractOfficeText` — fun, líneas 1090–1157

```kotlin
private fun extractOfficeText(context: Context, uri: Uri, extension: String): String {
    return try {
        val fragments = mutableListOf<String>()
        val sharedStrings = mutableListOf<String>()
        val worksheetXml = mutableListOf<String>()
        ZipInputStream(openInputStream(context, uri).buffered()).use { zip -> var entry = zip.nextEntry
            while (entry != null) {
                val entryName = entry.name
                val wanted = when (extension) {
                    "docx" -> entryName == "word/document.xml"
                    "pptx" -> entryName.startsWith("ppt/slides/slide") && entryName.endsWith(".xml")
                    "xlsx" -> entryName == "xl/sharedStrings.xml" ||
                        (entryName.startsWith("xl/worksheets/sheet") && entryName.endsWith(".xml"))
                    else -> false
                }
                if (wanted) {
                    val xml = zip.readBytes().toString(Charsets.UTF_8)
                    when {
                        extension == "xlsx" && entryName == "xl/sharedStrings.xml" -> {
                            Regex("<t(?:\\s[^>]*)?>(.*?)</t>", RegexOption.DOT_MATCHES_ALL).findAll(xml).forEach { match ->
                                    sharedStrings += decodeXml(match.groupValues[1])
                                }
                        }
                        extension == "xlsx" -> {
                            worksheetXml += xml
                        }
                        extension == "docx" -> {
                            Regex("<w:t(?:\\s[^>]*)?>(.*?)</w:t>", RegexOption.DOT_MATCHES_ALL).findAll(xml).forEach { match ->
                                    fragments += decodeXml(match.groupValues[1])
                                }
                        }
                        extension == "pptx" -> {
                            Regex("<a:t(?:\\s[^>]*)?>(.*?)</a:t>", RegexOption.DOT_MATCHES_ALL).findAll(xml).forEach { match ->
                                    fragments += decodeXml(match.groupValues[1])
                                }
                            fragments += "\n"
                        }
                    }
                }
                zip.closeEntry()
                entry = zip.nextEntry
            }
        }
        if (extension == "xlsx") {
            worksheetXml.forEachIndexed { sheetIndex, xml -> fragments += "Hoja ${sheetIndex + 1}"
                val cellRegex = Regex("<c([^>]*)>(.*?)</c>", setOf(RegexOption.DOT_MATCHES_ALL))
                cellRegex.findAll(xml).forEach { cellMatch -> val attrs = cellMatch.groupValues[1]
                    val body = cellMatch.groupValues[2]
                    val reference = Regex("r=\"([^\"]+)\"").find(attrs)?.groupValues?.getOrNull(1).orEmpty()
                    val rawValue = Regex("<v>(.*?)</v>", RegexOption.DOT_MATCHES_ALL).find(body)?.groupValues?.getOrNull(1).orEmpty()
                    val isShared = attrs.contains("t=\"s\"")
                    val value = if (isShared) {
                        rawValue.toIntOrNull()?.let { sharedStrings.getOrNull(it) }?: rawValue
                    } else {
                        decodeXml(rawValue)
                    }
                    if (value.isNotBlank()) {
                        fragments += if (reference.isBlank()) value else "$reference: $value"
                    }
                }
                fragments += "\n"
            }
        }
        fragments.joinToString("\n").replace(Regex("\n{3,}"), "\n\n").trim()
    } catch (_: Exception) {
        ""
    }
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `context: Context` — `context` recibe un valor de tipo `Context`. El contrato no marca este parámetro como anulable. Da acceso a recursos y APIs Android; su vida útil debe corresponder con el uso realizado.
- `uri: Uri` — `uri` recibe un valor de tipo `Uri`. El contrato no marca este parámetro como anulable. Es una referencia de contenido Android; puede apuntar a content providers y no solo a archivos locales.
- `extension: String` — `extension` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `String`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 1092 | `val fragments` | `inferido` | `mutableListOf<String>()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1093 | `val sharedStrings` | `inferido` | `mutableListOf<String>()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1094 | `val worksheetXml` | `inferido` | `mutableListOf<String>()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1097 | `val entryName` | `inferido` | `entry.name` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1098 | `val wanted` | `inferido` | `when (extension) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1106 | `val xml` | `inferido` | `zip.readBytes().toString(Charsets.UTF_8)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1135 | `val cellRegex` | `inferido` | `Regex("<c([^>]*)>(.*?)</c>", setOf(RegexOption.DOT_MATCHES_ALL))` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1137 | `val body` | `inferido` | `cellMatch.groupValues[2]` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1138 | `val reference` | `inferido` | `Regex("r=\"([^\"]+)\"").find(attrs)?.groupValues?.getOrNull(1).orEmpty()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. Normaliza una ausencia hacia un valor vacío/alternativo para simplificar el código consumidor. |
| 1139 | `val rawValue` | `inferido` | `Regex("<v>(.*?)</v>", RegexOption.DOT_MATCHES_ALL).find(body)?.groupValues?.getOrNull(1).orEmpty()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. Normaliza una ausencia hacia un valor vacío/alternativo para simplificar el código consumidor. |
| 1140 | `val isShared` | `inferido` | `attrs.contains("t=\"s\"")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El nombre indica un estado de decisión/visibilidad/selección usado para controlar una rama de comportamiento o presentación. |
| 1141 | `val value` | `inferido` | `if (isShared) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 1091 | `return try {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 1096 | `while (entry != null) {` | Bucle condicionado: repite mientras la condición se mantenga verdadera; la lógica interna debe permitir progreso hacia la salida. |
| 1099 | `"docx" -> entryName == "word/document.xml"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 1100 | `"pptx" -> entryName.startsWith("ppt/slides/slide") && entryName.endsWith(".xml")` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 1101 | `"xlsx" -> entryName == "xl/sharedStrings.xml" \|\|` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 1103 | `else -> false` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |
| 1105 | `if (wanted) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1107 | `when {` | Selección múltiple de Kotlin: concentra casos mutuamente evaluados y normalmente define una normalización o estrategia según el valor de entrada. |
| 1108 | `extension == "xlsx" && entryName == "xl/sharedStrings.xml" -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 1113 | `extension == "xlsx" -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 1116 | `extension == "docx" -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 1121 | `extension == "pptx" -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 1133 | `if (extension == "xlsx") {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1146 | `if (value.isNotBlank()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 5.
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 4.
- **Normalización vacía:** `orEmpty` transforma una referencia nula en una colección/cadena vacía, simplificando el consumo posterior. Apariciones en este bloque: 2.

#### Efectos secundarios y recursos

- **Persistencia Room/DAO:** Realiza una operación de persistencia o modificación a través de una capa de datos/DAO.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `ZipInputStream`, `openInputStream`, `buffered`, `entryName.startsWith`, `entryName.endsWith`, `zip.readBytes`, `toString`, `Regex`, `findAll`, `decodeXml`, `zip.closeEntry`, `setOf`, `cellRegex.findAll`, `find`, `getOrNull`, `orEmpty`, `attrs.contains`, `rawValue.toIntOrNull`, `sharedStrings.getOrNull`, `value.isNotBlank`, `reference.isBlank`, `fragments.joinToString`, `replace`, `trim`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.

### 4.29 `decodeXml` — fun, líneas 1159–1166

```kotlin
private fun decodeXml(value: String): String {
    return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
        Html.fromHtml(value, Html.FROM_HTML_MODE_LEGACY).toString()
    } else {
        @Suppress("DEPRECATION")
        Html.fromHtml(value).toString()
    }
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

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
| 1160 | `return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

**Restricciones concretas que aparecen en este bloque:**
- **Guardia de API:** Se condiciona una API según la versión de Android para no ejecutar llamadas incompatibles en dispositivos antiguos. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `Html.fromHtml`, `toString`, `Suppress`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar las guardas de versión antes de APIs que no existen en Android antiguos.

### 4.30 `InputStream` — fun, líneas 1168–1179

```kotlin
private fun InputStream.readUpTo(maxBytes: Int): ByteArray {
    val output = java.io.ByteArrayOutputStream()
    val buffer = ByteArray(16 * 1024)
    var total = 0
    while (total < maxBytes) {
        val count = read(buffer, 0, minOf(buffer.size, maxBytes - total))
        if (count <= 0) break
        output.write(buffer, 0, count)
        total += count
    }
    return output.toByteArray()
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `maxBytes: Int` — `maxBytes` recibe un valor de tipo `Int`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.

**Retorno:** `ByteArray`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 1169 | `val output` | `inferido` | `java.io.ByteArrayOutputStream()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1170 | `val buffer` | `inferido` | `ByteArray(16 * 1024)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1171 | `var total` | `inferido` | `0` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1173 | `val count` | `inferido` | `read(buffer, 0, minOf(buffer.size, maxBytes - total))` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 1172 | `while (total < maxBytes) {` | Bucle condicionado: repite mientras la condición se mantenga verdadera; la lógica interna debe permitir progreso hacia la salida. |
| 1174 | `if (count <= 0) break` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1178 | `return output.toByteArray()` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `readUpTo`, `java.io.ByteArrayOutputStream`, `ByteArray`, `read`, `minOf`, `output.write`, `output.toByteArray`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.31 `openInputStream` — fun, líneas 1181–1191

```kotlin
private fun openInputStream(context: Context, uri: Uri): InputStream {
    return when (uri.scheme) {
        "file" -> {
            val path = uri.path?: throw IllegalArgumentException("Ruta inválida")
            File(path).inputStream()
        }
        else -> {
            context.contentResolver.openInputStream(uri)?: throw IllegalArgumentException("No se pudo abrir el archivo")
        }
    }
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `context: Context` — `context` recibe un valor de tipo `Context`. El contrato no marca este parámetro como anulable. Da acceso a recursos y APIs Android; su vida útil debe corresponder con el uso realizado.
- `uri: Uri` — `uri` recibe un valor de tipo `Uri`. El contrato no marca este parámetro como anulable. Es una referencia de contenido Android; puede apuntar a content providers y no solo a archivos locales.

**Retorno:** `InputStream`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 1184 | `val path` | `inferido` | `uri.path?: throw IllegalArgumentException("Ruta inválida")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 1182 | `return when (uri.scheme) {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 1183 | `"file" -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 1187 | `else -> {` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |

**Restricciones concretas que aparecen en este bloque:**
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 2.

#### Efectos secundarios y recursos

- **Sistema de archivos/caché:** Accede o modifica almacenamiento/caché; son efectos externos al estado puramente en memoria.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `IllegalArgumentException`, `File`, `inputStream`, `context.contentResolver.openInputStream`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.

### 4.32 `openExternally` — fun, líneas 1193–1204

```kotlin
private fun openExternally(context: Context, uri: Uri, name: String?, mimeType: String) {
    try {
        val shareableUri = toShareableUri(context, uri)
        val intent = Intent(Intent.ACTION_VIEW).setDataAndType(shareableUri,
                mimeType.ifBlank { resolveMimeType(context, shareableUri, name) }).addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.open_with)))
    } catch (_: ActivityNotFoundException) {
        Toast.makeText(context, context.getString(R.string.no_compatible_app), Toast.LENGTH_SHORT).show()
    } catch (_: Exception) {
        Toast.makeText(context, context.getString(R.string.open_file_failed), Toast.LENGTH_SHORT).show()
    }
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `context: Context` — `context` recibe un valor de tipo `Context`. El contrato no marca este parámetro como anulable. Da acceso a recursos y APIs Android; su vida útil debe corresponder con el uso realizado.
- `uri: Uri` — `uri` recibe un valor de tipo `Uri`. El contrato no marca este parámetro como anulable. Es una referencia de contenido Android; puede apuntar a content providers y no solo a archivos locales.
- `name: String?` — `name` recibe un valor de tipo `String?`. El contrato permite `null`; el bloque debe decidir explícita o implícitamente qué significa la ausencia.
- `mimeType: String` — `mimeType` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable. Aunque el tipo sea `String`, semánticamente suele funcionar como clave/enumeración textual; las ramas del código delimitan los valores reconocidos.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 1195 | `val shareableUri` | `inferido` | `toShareableUri(context, uri)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Representa una ubicación Android `Uri`; no debe asumirse que siempre corresponde a una ruta de archivo convencional. |
| 1196 | `val intent` | `inferido` | `Intent(Intent.ACTION_VIEW).setDataAndType(shareableUri,` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 1194 | `try {` | Frontera de manejo de errores: agrupa operaciones que pueden lanzar excepciones para permitir recuperación o limpieza controlada. |

#### Efectos secundarios y recursos

- **Navegación/Activity:** Modifica navegación, ciclo de vida o contenido de una Activity.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `toShareableUri`, `Intent`, `setDataAndType`, `resolveMimeType`, `addFlags`, `context.startActivity`, `Intent.createChooser`, `context.getString`, `Toast.makeText`, `show`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.33 `toShareableUri` — fun, líneas 1206–1212

```kotlin
private fun toShareableUri(context: Context, uri: Uri): Uri {
    if (uri.scheme != "file") {
        return uri
    }
    val path = uri.path ?: return uri
    return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", File(path))
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `context: Context` — `context` recibe un valor de tipo `Context`. El contrato no marca este parámetro como anulable. Da acceso a recursos y APIs Android; su vida útil debe corresponder con el uso realizado.
- `uri: Uri` — `uri` recibe un valor de tipo `Uri`. El contrato no marca este parámetro como anulable. Es una referencia de contenido Android; puede apuntar a content providers y no solo a archivos locales.

**Retorno:** `Uri`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 1210 | `val path` | `inferido` | `uri.path ?: return uri` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 1207 | `if (uri.scheme != "file") {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1208 | `return uri` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 1211 | `return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", File(path))` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

**Restricciones concretas que aparecen en este bloque:**
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

- **Sistema de archivos/caché:** Accede o modifica almacenamiento/caché; son efectos externos al estado puramente en memoria.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `FileProvider.getUriForFile`, `File`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.34 `resolveMimeType` — fun, líneas 1214–1218

```kotlin
private fun resolveMimeType(context: Context, uri: Uri, name: String?): String {
    context.contentResolver.getType(uri)?.let { return it }
    val extension = fileExtension(name, uri)
    return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)?: "*/*"
}
```

#### Qué hace y por qué existe

Resuelve un valor final a partir del contexto y las reglas de prioridad/fallback definidas en el cuerpo.

#### Contrato de la declaración

**Parámetros:**

- `context: Context` — `context` recibe un valor de tipo `Context`. El contrato no marca este parámetro como anulable. Da acceso a recursos y APIs Android; su vida útil debe corresponder con el uso realizado.
- `uri: Uri` — `uri` recibe un valor de tipo `Uri`. El contrato no marca este parámetro como anulable. Es una referencia de contenido Android; puede apuntar a content providers y no solo a archivos locales.
- `name: String?` — `name` recibe un valor de tipo `String?`. El contrato permite `null`; el bloque debe decidir explícita o implícitamente qué significa la ausencia.

**Retorno:** `String`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 1216 | `val extension` | `inferido` | `fileExtension(name, uri)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 1217 | `return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)?: "*/*"` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 1.
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `context.contentResolver.getType`, `fileExtension`, `MimeTypeMap.getSingleton`, `getMimeTypeFromExtension`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.35 `fileExtension` — fun, líneas 1220–1226

```kotlin
private fun fileExtension(name: String?, uri: Uri): String {
    val byName = name?.substringAfterLast('.', "")?.lowercase(Locale.ROOT).orEmpty()
    if (byName.isNotBlank()) {
        return byName
    }
    return uri.path?.substringAfterLast('.', "")?.lowercase(Locale.ROOT).orEmpty()
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `name: String?` — `name` recibe un valor de tipo `String?`. El contrato permite `null`; el bloque debe decidir explícita o implícitamente qué significa la ausencia.
- `uri: Uri` — `uri` recibe un valor de tipo `Uri`. El contrato no marca este parámetro como anulable. Es una referencia de contenido Android; puede apuntar a content providers y no solo a archivos locales.

**Retorno:** `String`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 1221 | `val byName` | `inferido` | `name?.substringAfterLast('.', "")?.lowercase(Locale.ROOT).orEmpty()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. Normaliza una ausencia hacia un valor vacío/alternativo para simplificar el código consumidor. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 1222 | `if (byName.isNotBlank()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1223 | `return byName` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 1225 | `return uri.path?.substringAfterLast('.', "")?.lowercase(Locale.ROOT).orEmpty()` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 4.
- **Normalización vacía:** `orEmpty` transforma una referencia nula en una colección/cadena vacía, simplificando el consumo posterior. Apariciones en este bloque: 2.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `substringAfterLast`, `lowercase`, `orEmpty`, `byName.isNotBlank`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.36 `isTextExtension` — fun, líneas 1228–1231

```kotlin
private fun isTextExtension(extension: String): Boolean {
    return extension in setOf("txt", "log", "json", "xml", "csv", "md", "kt", "java", "gradle", "kts", "py", "js",
        "ts", "css", "html", "htm", "sh", "c", "cpp", "h", "hpp", "ini", "cfg", "yaml", "yml", "sql", "properties", "conf")
}
```

#### Qué hace y por qué existe

Evalúa una condición y devuelve/representa una decisión booleana utilizada por otras ramas del flujo.

#### Contrato de la declaración

**Parámetros:**

- `extension: String` — `extension` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `Boolean`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 1229 | `return extension in setOf("txt", "log", "json", "xml", "csv", "md", "kt", "java", "gradle", "kts", "py", "js",` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `setOf`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.37 `resolveFileSize` — fun, líneas 1233–1245

```kotlin
private fun resolveFileSize(context: Context, uri: Uri): Long? {
    return try {
        when (uri.scheme) {
            "file" -> uri.path?.let { File(it) }?.takeIf { it.exists() }?.length()
            else -> {
                context.contentResolver.openAssetFileDescriptor(uri, "r")?.use { descriptor -> descriptor.length.takeIf { it >= 0L }
                    }
            }
        }
    } catch (_: Exception) {
        null
    }
}
```

#### Qué hace y por qué existe

Resuelve un valor final a partir del contexto y las reglas de prioridad/fallback definidas en el cuerpo.

#### Contrato de la declaración

**Parámetros:**

- `context: Context` — `context` recibe un valor de tipo `Context`. El contrato no marca este parámetro como anulable. Da acceso a recursos y APIs Android; su vida útil debe corresponder con el uso realizado.
- `uri: Uri` — `uri` recibe un valor de tipo `Uri`. El contrato no marca este parámetro como anulable. Es una referencia de contenido Android; puede apuntar a content providers y no solo a archivos locales.

**Retorno:** `Long?`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 1234 | `return try {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 1235 | `when (uri.scheme) {` | Selección múltiple de Kotlin: concentra casos mutuamente evaluados y normalmente define una normalización o estrategia según el valor de entrada. |
| 1236 | `"file" -> uri.path?.let { File(it) }?.takeIf { it.exists() }?.length()` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 1237 | `else -> {` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 4.
- **Filtro condicional:** `takeIf` conserva el valor únicamente cuando el predicado se cumple; en caso contrario lo convierte en `null`. Apariciones en este bloque: 2.

#### Efectos secundarios y recursos

- **Sistema de archivos/caché:** Accede o modifica almacenamiento/caché; son efectos externos al estado puramente en memoria.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `File`, `it.exists`, `length`, `context.contentResolver.openAssetFileDescriptor`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.

### 4.38 `formatFileSize` — fun, líneas 1247–1255

```kotlin
private fun formatFileSize(bytes: Long): String {
    if (bytes < 1024L) return "$bytes B"
    val kb = bytes / 1024.0
    if (kb < 1024.0) return String.format(Locale.ROOT, "%.1f KB", kb)
    val mb = kb / 1024.0
    if (mb < 1024.0) return String.format(Locale.ROOT, "%.1f MB", mb)
    val gb = mb / 1024.0
    return String.format(Locale.ROOT, "%.2f GB", gb)
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `bytes: Long` — `bytes` recibe un valor de tipo `Long`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.

**Retorno:** `String`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 1249 | `val kb` | `inferido` | `bytes / 1024.0` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1251 | `val mb` | `inferido` | `kb / 1024.0` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1253 | `val gb` | `inferido` | `mb / 1024.0` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 1248 | `if (bytes < 1024L) return "$bytes B"` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1250 | `if (kb < 1024.0) return String.format(Locale.ROOT, "%.1f KB", kb)` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1252 | `if (mb < 1024.0) return String.format(Locale.ROOT, "%.1f MB", mb)` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1254 | `return String.format(Locale.ROOT, "%.2f GB", gb)` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `String.format`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.39 `formatTime` — fun, líneas 1257–1267

```kotlin
private fun formatTime(milliseconds: Int): String {
    val totalSeconds = (milliseconds.coerceAtLeast(0) / 1000)
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return if (hours > 0) {
        String.format(Locale.ROOT, "%d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format(Locale.ROOT, "%d:%02d", minutes, seconds)
    }
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `milliseconds: Int` — `milliseconds` recibe un valor de tipo `Int`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.

**Retorno:** `String`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 1258 | `val totalSeconds` | `inferido` | `(milliseconds.coerceAtLeast(0) / 1000)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El inicializador limita explícitamente el valor a un rango, evitando que dimensiones/intensidades/sizes inválidos se propaguen. |
| 1259 | `val hours` | `inferido` | `totalSeconds / 3600` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1260 | `val minutes` | `inferido` | `(totalSeconds % 3600) / 60` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1261 | `val seconds` | `inferido` | `totalSeconds % 60` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 1262 | `return if (hours > 0) {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

**Restricciones concretas que aparecen en este bloque:**
- **Límite numérico:** Se fuerza el valor a un intervalo o umbral para impedir que salgan valores fuera de los límites esperados. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `milliseconds.coerceAtLeast`, `String.format`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

## 5. Inventario global de propiedades/variables detectadas

| Línea | Identificador | Mutabilidad | Tipo | Explicación |
|---:|---|---|---|---|
| 119 | `EXTRA_URI` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Representa una ubicación Android `Uri`; no debe asumirse que siempre corresponde a una ruta de archivo convencional. Está marcada `const`, por lo que es una constante de tiempo de compilación y solo puede usar un tipo/valor admitido por Kotlin para constantes. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 120 | `EXTRA_TYPE` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Está marcada `const`, por lo que es una constante de tiempo de compilación y solo puede usar un tipo/valor admitido por Kotlin para constantes. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 121 | `EXTRA_NAME` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Está marcada `const`, por lo que es una constante de tiempo de compilación y solo puede usar un tipo/valor admitido por Kotlin para constantes. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 122 | `EXTRA_MIME` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Está marcada `const`, por lo que es una constante de tiempo de compilación y solo puede usar un tipo/valor admitido por Kotlin para constantes. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 123 | `MAX_TEXT_BYTES` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Está marcada `const`, por lo que es una constante de tiempo de compilación y solo puede usar un tipo/valor admitido por Kotlin para constantes. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 130 | `intent` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 140 | `controller` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 141 | `isMultiWindow` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El nombre indica un estado de decisión/visibilidad/selección usado para controlar una rama de comportamiento o presentación. |
| 159 | `controller` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 194 | `uriString` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Representa una ubicación Android `Uri`; no debe asumirse que siempre corresponde a una ruta de archivo convencional. Normaliza una ausencia hacia un valor vacío/alternativo para simplificar el código consumidor. |
| 195 | `type` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Normaliza una ausencia hacia un valor vacío/alternativo para simplificar el código consumidor. |
| 196 | `name` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 197 | `mimeType` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 203 | `settingsViewModel` | `val` | `SettingsViewModel` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo declarado es `SettingsViewModel`. No declara nulabilidad explícita. Actúa como selector de estrategia/perfil; su valor determina qué rama de configuración se aplica. Referencia un ViewModel, cuyo objetivo es mantener estado/lógica de pantalla fuera de la instancia visual inmediata. |
| 204 | `settings` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Convierte un `Flow/StateFlow` en estado de Compose respetando el ciclo de vida, evitando trabajo de colección innecesario cuando la UI no está activa. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 224 | `context` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El `Context` da acceso a recursos/servicios/almacenamiento Android; debe respetarse el ciclo de vida del contexto recibido. |
| 225 | `uri` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Representa una ubicación Android `Uri`; no debe asumirse que siempre corresponde a una ruta de archivo convencional. |
| 226 | `mimeType` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 229 | `extension` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 232 | `title` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 292 | `context` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El `Context` da acceso a recursos/servicios/almacenamiento Android; debe respetarse el ciclo de vida del contexto recibido. |
| 293 | `bitmapLimit` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 294 | `activityManager` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 301 | `request` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 331 | `context` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El `Context` da acceso a recursos/servicios/almacenamiento Android; debe respetarse el ciclo de vida del contexto recibido. |
| 332 | `scope` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 333 | `player` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 365 | `listener` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 407 | `fallbackFile` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 456 | `currentDuration` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El inicializador limita explícitamente el valor a un rango, evitando que dimensiones/intensidades/sizes inválidos se propaguen. |
| 502 | `target` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 546 | `context` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El `Context` da acceso a recursos/servicios/almacenamiento Android; debe respetarse el ciclo de vida del contexto recibido. |
| 547 | `scope` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 548 | `player` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 580 | `listener` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 617 | `fallbackFile` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 665 | `currentDuration` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El inicializador limita explícitamente el valor a un rango, evitando que dimensiones/intensidades/sizes inválidos se propaguen. |
| 702 | `target` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 760 | `context` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El `Context` da acceso a recursos/servicios/almacenamiento Android; debe respetarse el ciclo de vida del contexto recibido. |
| 761 | `density` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 762 | `handle` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 783 | `widthPx` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. |
| 786 | `pages` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 798 | `renderResult` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 799 | `bitmap` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 808 | `completed` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 809 | `bitmap` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 834 | `pageCount` | `val` | `Int` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo declarado es `Int`. No declara nulabilidad explícita. |
| 839 | `scale` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 840 | `targetHeight` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. El inicializador limita explícitamente el valor a un rango, evitando que dimensiones/intensidades/sizes inválidos se propaguen. |
| 841 | `bitmap` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 866 | `path` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 877 | `directDescriptor` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 884 | `cachedPdf` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Forma parte de la estrategia de reutilización de resultados para evitar repetir trabajo costoso. |
| 891 | `context` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El `Context` da acceso a recursos/servicios/almacenamiento Android; debe respetarse el ciclo de vida del contexto recibido. |
| 892 | `text` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 916 | `context` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El `Context` da acceso a recursos/servicios/almacenamiento Android; debe respetarse el ciclo de vida del contexto recibido. |
| 917 | `extracted` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 942 | `context` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El `Context` da acceso a recursos/servicios/almacenamiento Android; debe respetarse el ciclo de vida del contexto recibido. |
| 943 | `fileSize` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 962 | `metadata` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 994 | `directory` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 998 | `safeExtension` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 999 | `key` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1000 | `file` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1016 | `directory` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1020 | `mime` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1021 | `extension` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 1023 | `key` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1024 | `file` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1040 | `directory` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1044 | `mime` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1045 | `extension` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 1050 | `key` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1051 | `file` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1073 | `text` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1092 | `fragments` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1093 | `sharedStrings` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1094 | `worksheetXml` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1097 | `entryName` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1098 | `wanted` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1106 | `xml` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1135 | `cellRegex` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1137 | `body` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1138 | `reference` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. Normaliza una ausencia hacia un valor vacío/alternativo para simplificar el código consumidor. |
| 1139 | `rawValue` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. Normaliza una ausencia hacia un valor vacío/alternativo para simplificar el código consumidor. |
| 1140 | `isShared` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El nombre indica un estado de decisión/visibilidad/selección usado para controlar una rama de comportamiento o presentación. |
| 1141 | `value` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1169 | `output` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1170 | `buffer` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1171 | `total` | `var` | `inferido` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1173 | `count` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1184 | `path` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 1195 | `shareableUri` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Representa una ubicación Android `Uri`; no debe asumirse que siempre corresponde a una ruta de archivo convencional. |
| 1196 | `intent` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1210 | `path` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 1216 | `extension` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1221 | `byName` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. Normaliza una ausencia hacia un valor vacío/alternativo para simplificar el código consumidor. |
| 1249 | `kb` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1251 | `mb` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1253 | `gb` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1258 | `totalSeconds` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El inicializador limita explícitamente el valor a un rango, evitando que dimensiones/intensidades/sizes inválidos se propaguen. |
| 1259 | `hours` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1260 | `minutes` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1261 | `seconds` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

## 6. Mapa exhaustivo de TODOS los bloques delimitados por `{}`

Esta tabla recorre todas las llaves estructurales detectadas fuera de strings/comentarios. Así se documentan también lambdas de Compose, callbacks, iteraciones y ámbitos locales que no tienen un nombre propio de función.

| Inicio–fin | Prof. | Cabecera/contexto | Qué significa ese bloque |
|---|---:|---|---|
| 129–136 | 0 | `fun openAttachmentViewer(context: Context, uri: String, type: String, name: String? = null, mimeType: String? = null)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 132–134 | 1 | `if (context !is Activity)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 138–219 | 0 | `class AttachmentViewerActivity : ComponentActivity()` | Cuerpo de tipo/contenedor: agrupa propiedades y miembros bajo la misma responsabilidad y controla su visibilidad/estado compartido. |
| 139–157 | 1 | `private fun applyAndroidNavigationBarPolicy()` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 142–144 | 2 | `if (isMultiWindow)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 144–147 | 2 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 153–156 | 2 | `if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 158–164 | 1 | `private fun applySystemBarAppearance(darkMode: Boolean)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 161–163 | 2 | `if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 165–170 | 1 | `override fun onWindowFocusChanged(hasFocus: Boolean)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 167–169 | 2 | `if (hasFocus)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 171–175 | 1 | `override fun onResume()` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 176–179 | 1 | `override fun onMultiWindowModeChanged(isInMultiWindowMode: Boolean)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 180–183 | 1 | `override fun onDestroy()` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 184–218 | 1 | `override fun onCreate(savedInstanceState: Bundle?)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 198–201 | 2 | `if (uriString.isBlank())` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 202–217 | 2 | `setContent` | Raíz de composición de la Activity: establece el árbol Compose que se renderiza como contenido de la ventana. |
| 205–207 | 3 | `LaunchedEffect(settings.performanceMode)` | Efecto de Compose con corrutina: su ciclo de vida depende de las claves; se cancela/reinicia al cambiar dichas claves o salir de composición. |
| 208–210 | 3 | `LaunchedEffect(settings.darkMode)` | Efecto de Compose con corrutina: su ciclo de vida depende de las claves; se cancela/reinicia al cambiar dichas claves o salir de composición. |
| 214–216 | 3 | `textColor = settings.textColor, textOutlineEnabled = settings.textOutlineEnabled, accentColor = settings.accentColor)` | Ámbito delimitado por llaves en profundidad 3. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 215–215 | 4 | `AttachmentViewerScreen(uriString = uriString, type = type, name = name, explicitMimeType = mimeType, onBack =` | Ámbito delimitado por llaves en profundidad 4. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 223–288 | 0 | `private fun AttachmentViewerScreen(uriString: String, type: String, name: String?, explicitMimeType: String?, onBack: () -> Unit)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 225–225 | 1 | `val uri = remember(uriString)` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 226–228 | 1 | `val mimeType = remember(uriString, explicitMimeType, name)` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 229–231 | 1 | `val extension = remember(name, uriString)` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 232–232 | 1 | `val title = name?.takeIf` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 232–238 | 1 | `val title = name?.takeIf { it.isNotBlank() }?: when` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 239–262 | 1 | `Scaffold(modifier = Modifier.fillMaxSize(), containerColor = MaterialTheme.colorScheme.background, topBar =` | Lambda de contenido de UI Compose: declara hijos y configuración visual dentro del contenedor. El orden y los `Modifier` influyen en medida, posición, dibujo e interacción. |
| 240–242 | 2 | `TopAppBar(title =` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 242–250 | 2 | `}, navigationIcon =` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 243–246 | 3 | `IconButton(onClick =` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 246–249 | 3 | `})` | Ámbito delimitado por llaves en profundidad 3. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 250–258 | 2 | `}, actions =` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 251–254 | 3 | `IconButton(onClick =` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 254–257 | 3 | `})` | Ámbito delimitado por llaves en profundidad 3. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 262–287 | 1 | `})` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 262–286 | 2 | `}) { innerPadding -> Box(modifier = Modifier.fillMaxSize().padding(innerPadding))` | Lambda de contenido de UI Compose: declara hijos y configuración visual dentro del contenedor. El orden y los `Modifier` influyen en medida, posición, dibujo e interacción. |
| 263–285 | 3 | `when` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 264–266 | 4 | `type == "image" \|\| mimeType.startsWith("image/") ->` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 267–269 | 4 | `type == "video" \|\| mimeType.startsWith("video/") ->` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 270–272 | 4 | `type == "audio" \|\| type == "voice" \|\| mimeType.startsWith("audio/") ->` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 273–275 | 4 | `extension == "pdf" \|\| mimeType == "application/pdf" ->` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 276–278 | 4 | `isTextExtension(extension) \|\| mimeType.startsWith("text/") ->` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 279–281 | 4 | `extension == "docx" \|\| extension == "pptx" \|\| extension == "xlsx" ->` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 282–284 | 4 | `else ->` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 291–327 | 0 | `private fun ImageViewer(uri: Uri, name: String?)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 293–300 | 1 | `val bitmapLimit = remember(context)` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 295–299 | 2 | `when` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 301–304 | 1 | `val request = remember(context, uri, bitmapLimit)` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 306–308 | 1 | `remember(uri)` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 309–326 | 1 | `Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center)` | Lambda de contenido de UI Compose: declara hijos y configuración visual dentro del contenedor. El orden y los `Modifier` influyen en medida, posición, dibujo e interacción. |
| 310–317 | 2 | `if (!failed)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 312–314 | 3 | `onSuccess =` | Ámbito delimitado por llaves en profundidad 3. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 314–316 | 3 | `}, onError =` | Ámbito delimitado por llaves en profundidad 3. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 317–325 | 2 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 319–324 | 3 | `Arrangement.Center)` | Ámbito delimitado por llaves en profundidad 3. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 330–542 | 0 | `private fun VideoViewer(uri: Uri, name: String, mimeType: String)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 333–335 | 1 | `val player = remember(uri)` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 337–339 | 1 | `remember(uri)` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 341–343 | 1 | `remember(uri)` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 345–347 | 1 | `remember(uri)` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 349–351 | 1 | `remember(uri)` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 353–355 | 1 | `remember(uri)` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 357–359 | 1 | `remember(uri)` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 361–363 | 1 | `remember(uri)` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 364–451 | 1 | `DisposableEffect(player, uri)` | Efecto de Compose que exige liberar recursos mediante `onDispose` cuando cambia la clave o el composable abandona la composición. |
| 366–431 | 2 | `Player.Listener` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 367–390 | 3 | `override fun onPlaybackStateChanged(playbackState: Int)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 368–389 | 4 | `when (playbackState)` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 369–371 | 5 | `Player.STATE_BUFFERING ->` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 372–377 | 5 | `Player.STATE_READY ->` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 378–385 | 5 | `Player.STATE_ENDED ->` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 382–384 | 6 | `if (duration > 0)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 386–388 | 5 | `Player.STATE_IDLE ->` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 391–393 | 3 | `override fun onIsPlayingChanged(isPlaying: Boolean)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 395–430 | 3 | `PlaybackException)` | Ámbito delimitado por llaves en profundidad 3. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 404–427 | 4 | `if (!fallbackAttempted && uri.scheme != "file")` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 406–426 | 5 | `scope.launch` | Nueva corrutina: el bloque se ejecuta asíncronamente dentro del `CoroutineScope` receptor y respeta su cancelación. |
| 407–409 | 6 | `val fallbackFile = withContext(Dispatchers.IO)` | Cambio de contexto de corrutina: el cuerpo se ejecuta bajo el dispatcher/contexto indicado y devuelve el resultado al contexto llamador. |
| 410–423 | 6 | `if (fallbackFile != null)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 411–419 | 7 | `try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 419–422 | 7 | `} catch (_: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 423–425 | 6 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 427–429 | 4 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 433–437 | 2 | `try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 437–440 | 2 | `} catch (_: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 441–450 | 2 | `onDispose` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 442–444 | 3 | `try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 444–445 | 3 | `} catch (_: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 446–448 | 3 | `try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 448–449 | 3 | `} catch (_: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 452–463 | 1 | `LaunchedEffect(player, prepared)` | Efecto de Compose con corrutina: su ciclo de vida depende de las claves; se cancela/reinicia al cambiar dichas claves o salir de composición. |
| 453–462 | 2 | `while (true)` | Bucle `while`: se repite mientras la condición siga siendo verdadera; el cuerpo debe producir progreso para evitar un ciclo infinito. |
| 454–460 | 3 | `if (prepared)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 457–459 | 4 | `if (currentDuration > 0)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 464–541 | 1 | `Column(modifier = Modifier.fillMaxSize().background(Color.Black))` | Lambda de contenido de UI Compose: declara hijos y configuración visual dentro del contenedor. El orden y los `Modifier` influyen en medida, posición, dibujo e interacción. |
| 465–496 | 2 | `Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center)` | Lambda de contenido de UI Compose: declara hijos y configuración visual dentro del contenedor. El orden y los `Modifier` influyen en medida, posición, dibujo e interacción. |
| 466–473 | 3 | `AndroidView(factory =` | Ámbito delimitado por llaves en profundidad 3. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 467–472 | 4 | `PlayerView(context).apply` | Lambda `apply`: configura el receptor y devuelve ese mismo receptor; útil para inicialización encadenada sin perder la instancia. |
| 473–477 | 3 | `}, update =` | Ámbito delimitado por llaves en profundidad 3. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 474–476 | 4 | `if (it.player !== player)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 478–480 | 3 | `if (buffering && !error)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 481–495 | 3 | `if (error)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 483–494 | 4 | `Arrangement.Center)` | Ámbito delimitado por llaves en profundidad 4. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 486–489 | 5 | `Button(onClick =` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 489–493 | 5 | `})` | Ámbito delimitado por llaves en profundidad 5. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 497–540 | 2 | `Surface(color = Color.Black, contentColor = Color.White)` | Lambda de contenido de UI Compose: declara hijos y configuración visual dentro del contenedor. El orden y los `Modifier` influyen en medida, posición, dibujo e interacción. |
| 498–539 | 3 | `Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp))` | Lambda de contenido de UI Compose: declara hijos y configuración visual dentro del contenedor. El orden y los `Modifier` influyen en medida, posición, dibujo e interacción. |
| 499–510 | 4 | `Slider(value = position.coerceIn(0, duration.coerceAtLeast(1)).toFloat(), onValueChange =` | Ámbito delimitado por llaves en profundidad 4. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 504–509 | 5 | `if (prepared)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 505–507 | 6 | `try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 507–508 | 6 | `} catch (_: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 511–538 | 4 | `Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically)` | Lambda de contenido de UI Compose: declara hijos y configuración visual dentro del contenedor. El orden y los `Modifier` influyen en medida, posición, dibujo e interacción. |
| 512–526 | 5 | `IconButton(enabled = prepared && !error, onClick =` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 514–524 | 6 | `try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 515–517 | 7 | `if (player.isPlaying)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 517–523 | 7 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 518–521 | 8 | `if (duration > 0 && position >= duration - 250)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 524–525 | 6 | `} catch (_: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 526–536 | 5 | `})` | Ámbito delimitado por llaves en profundidad 5. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 527–529 | 6 | `Icon(imageVector = if (playing)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 529–531 | 6 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 531–533 | 6 | `}, contentDescription = if (playing)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 533–535 | 6 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 545–756 | 0 | `private fun AudioFileViewer(uri: Uri, name: String, mimeType: String)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 548–550 | 1 | `val player = remember(uri)` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 552–554 | 1 | `remember(uri)` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 556–558 | 1 | `remember(uri)` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 560–562 | 1 | `remember(uri)` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 564–566 | 1 | `remember(uri)` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 568–570 | 1 | `remember(uri)` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 572–574 | 1 | `remember(uri)` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 576–578 | 1 | `remember(uri)` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 579–660 | 1 | `DisposableEffect(player, uri)` | Efecto de Compose que exige liberar recursos mediante `onDispose` cuando cambia la clave o el composable abandona la composición. |
| 581–641 | 2 | `Player.Listener` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 582–605 | 3 | `override fun onPlaybackStateChanged(playbackState: Int)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 583–604 | 4 | `when (playbackState)` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 584–586 | 5 | `Player.STATE_BUFFERING ->` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 587–592 | 5 | `Player.STATE_READY ->` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 593–600 | 5 | `Player.STATE_ENDED ->` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 597–599 | 6 | `if (duration > 0)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 601–603 | 5 | `Player.STATE_IDLE ->` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 606–608 | 3 | `override fun onIsPlayingChanged(isPlaying: Boolean)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 610–640 | 3 | `PlaybackException)` | Ámbito delimitado por llaves en profundidad 3. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 614–637 | 4 | `if (!fallbackAttempted && uri.scheme != "file")` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 616–636 | 5 | `scope.launch` | Nueva corrutina: el bloque se ejecuta asíncronamente dentro del `CoroutineScope` receptor y respeta su cancelación. |
| 617–619 | 6 | `val fallbackFile = withContext(Dispatchers.IO)` | Cambio de contexto de corrutina: el cuerpo se ejecuta bajo el dispatcher/contexto indicado y devuelve el resultado al contexto llamador. |
| 620–633 | 6 | `if (fallbackFile != null)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 621–629 | 7 | `try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 629–632 | 7 | `} catch (_: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 633–635 | 6 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 637–639 | 4 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 643–646 | 2 | `try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 646–649 | 2 | `} catch (_: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 650–659 | 2 | `onDispose` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 651–653 | 3 | `try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 653–654 | 3 | `} catch (_: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 655–657 | 3 | `try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 657–658 | 3 | `} catch (_: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 661–672 | 1 | `LaunchedEffect(player, prepared)` | Efecto de Compose con corrutina: su ciclo de vida depende de las claves; se cancela/reinicia al cambiar dichas claves o salir de composición. |
| 662–671 | 2 | `while (true)` | Bucle `while`: se repite mientras la condición siga siendo verdadera; el cuerpo debe producir progreso para evitar un ciclo infinito. |
| 663–669 | 3 | `if (prepared)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 666–668 | 4 | `if (currentDuration > 0)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 674–755 | 1 | `Arrangement.Center)` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 675–691 | 2 | `Surface(modifier = Modifier.size(112.dp), shape = RoundedCornerShape(28.dp), color = MaterialTheme.colorScheme.primaryContainer)` | Lambda de contenido de UI Compose: declara hijos y configuración visual dentro del contenedor. El orden y los `Modifier` influyen en medida, posición, dibujo e interacción. |
| 676–690 | 3 | `Box(contentAlignment = Alignment.Center)` | Lambda de contenido de UI Compose: declara hijos y configuración visual dentro del contenedor. El orden y los `Modifier` influyen en medida, posición, dibujo e interacción. |
| 677–679 | 4 | `if (buffering && !error)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 679–689 | 4 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 680–682 | 5 | `Icon(imageVector = if (playing)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 682–684 | 5 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 684–686 | 5 | `}, contentDescription = if (playing)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 686–688 | 5 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 695–697 | 2 | `Text(text = mimeType.ifBlank` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 699–710 | 2 | `Slider(value = position.coerceIn(0, duration.coerceAtLeast(1)).toFloat(), onValueChange =` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 704–709 | 3 | `if (prepared)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 705–707 | 4 | `try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 707–708 | 4 | `} catch (_: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 714–728 | 2 | `Button(enabled = prepared && !error, onClick =` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 716–726 | 3 | `try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 717–719 | 4 | `if (player.isPlaying)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 719–725 | 4 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 720–723 | 5 | `if (duration > 0 && position >= duration - 250)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 726–727 | 3 | `} catch (_: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 728–740 | 2 | `})` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 729–731 | 3 | `Icon(imageVector = if (playing)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 731–733 | 3 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 735–737 | 3 | `Text(if (playing)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 737–739 | 3 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 741–754 | 2 | `if (error)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 746–749 | 3 | `Button(onClick =` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 749–753 | 3 | `})` | Ámbito delimitado por llaves en profundidad 3. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 759–794 | 0 | `private fun PdfViewer(uri: Uri)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 762–768 | 1 | `val handle = remember(uri)` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 763–765 | 2 | `try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 765–767 | 2 | `} catch (_: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 769–776 | 1 | `DisposableEffect(handle)` | Efecto de Compose que exige liberar recursos mediante `onDispose` cuando cambia la clave o el composable abandona la composición. |
| 770–775 | 2 | `onDispose` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 771–773 | 3 | `try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 773–774 | 3 | `} catch (_: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 777–781 | 1 | `if (handle == null)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 782–793 | 1 | `BoxWithConstraints(modifier = Modifier.fillMaxSize())` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 783–785 | 2 | `val widthPx = with(density)` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 786–788 | 2 | `val pages = remember(handle.pageCount)` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 789–792 | 2 | `LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp))` | Lambda de contenido de UI Compose: declara hijos y configuración visual dentro del contenedor. El orden y los `Modifier` influyen en medida, posición, dibujo e interacción. |
| 790–790 | 3 | `items(items = pages, key =` | Ámbito delimitado por llaves en profundidad 3. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 790–791 | 3 | `items(items = pages, key = { it })` | Ámbito delimitado por llaves en profundidad 3. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 797–831 | 0 | `private fun PdfPage(handle: PdfHandle, pageIndex: Int, targetWidth: Int)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 798–807 | 1 | `val renderResult by produceState(initialValue = Pair(false, null as Bitmap?), key1 = handle, key2 = pageIndex, key3 = targetWidth)` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 799–805 | 2 | `val bitmap = withContext(Dispatchers.IO)` | Cambio de contexto de corrutina: el cuerpo se ejecuta bajo el dispatcher/contexto indicado y devuelve el resultado al contexto llamador. |
| 800–802 | 3 | `try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 802–804 | 3 | `} catch (_: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 811–830 | 1 | `color = Color.White)` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 812–829 | 2 | `when` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 813–817 | 3 | `!completed ->` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 814–816 | 4 | `Box(modifier = Modifier.fillMaxWidth().height(240.dp), contentAlignment = Alignment.Center)` | Lambda de contenido de UI Compose: declara hijos y configuración visual dentro del contenedor. El orden y los `Modifier` influyen en medida, posición, dibujo e interacción. |
| 818–823 | 3 | `bitmap == null ->` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 819–822 | 4 | `Box(modifier = Modifier.fillMaxWidth().height(180.dp).padding(18.dp), contentAlignment = Alignment.Center)` | Lambda de contenido de UI Compose: declara hijos y configuración visual dentro del contenedor. El orden y los `Modifier` influyen en medida, posición, dibujo e interacción. |
| 824–828 | 3 | `else ->` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 833–851 | 0 | `private class PdfHandle(private val descriptor: ParcelFileDescriptor, private val renderer: PdfRenderer) : Closeable` | Cuerpo de tipo/contenedor: agrupa propiedades y miembros bajo la misma responsabilidad y controla su visibilidad/estado compartido. |
| 837–846 | 1 | `fun renderPage(pageIndex: Int, targetWidth: Int): Bitmap` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 838–845 | 2 | `renderer.openPage(pageIndex).use` | Bloque `use`: el recurso cerrable se libera automáticamente al terminar el cuerpo, incluso si ocurre una excepción. |
| 847–850 | 1 | `override fun close()` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 853–887 | 0 | `private fun openPdfHandle(context: Context, uri: Uri): PdfHandle` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 854–864 | 1 | `fun createHandle(descriptor: ParcelFileDescriptor): PdfHandle` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 855–857 | 2 | `return try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 857–863 | 2 | `} catch (error: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 858–860 | 3 | `try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 860–861 | 3 | `} catch (_: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 865–868 | 1 | `if (uri.scheme == "file")` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 876–881 | 1 | `try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 878–880 | 2 | `if (directDescriptor != null)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 881–883 | 1 | `} catch (_: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 890–912 | 0 | `private fun TextFileViewer(uri: Uri, extension: String)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 892–896 | 1 | `val text by produceState<String?>(initialValue = null, key1 = uri)` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 893–895 | 2 | `value = withContext(Dispatchers.IO)` | Cambio de contexto de corrutina: el cuerpo se ejecuta bajo el dispatcher/contexto indicado y devuelve el resultado al contexto llamador. |
| 897–901 | 1 | `if (text == null)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 898–900 | 2 | `Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center)` | Lambda de contenido de UI Compose: declara hijos y configuración visual dentro del contenedor. El orden y los `Modifier` influyen en medida, posición, dibujo e interacción. |
| 901–911 | 1 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 902–910 | 2 | `Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp))` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 905–907 | 3 | `"h", "hpp", "ini", "cfg", "yaml", "yml"))` | Ámbito delimitado por llaves en profundidad 3. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 907–909 | 3 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 915–938 | 0 | `private fun OfficeTextViewer(uri: Uri, extension: String, name: String, mimeType: String)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 917–921 | 1 | `val extracted by produceState<String?>(initialValue = null, key1 = uri, key2 = extension)` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 918–920 | 2 | `value = withContext(Dispatchers.IO)` | Cambio de contexto de corrutina: el cuerpo se ejecuta bajo el dispatcher/contexto indicado y devuelve el resultado al contexto llamador. |
| 922–927 | 1 | `if (extracted == null)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 923–925 | 2 | `Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center)` | Lambda de contenido de UI Compose: declara hijos y configuración visual dentro del contenedor. El orden y los `Modifier` influyen en medida, posición, dibujo e interacción. |
| 928–931 | 1 | `if (extracted!!.isBlank())` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 932–937 | 1 | `Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp))` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 941–990 | 0 | `private fun GenericFileViewer(uri: Uri, name: String, extension: String, mimeType: String)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 943–947 | 1 | `val fileSize by produceState<Long?>(initialValue = null, key1 = uri)` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 944–946 | 2 | `value = withContext(Dispatchers.IO)` | Cambio de contexto de corrutina: el cuerpo se ejecuta bajo el dispatcher/contexto indicado y devuelve el resultado al contexto llamador. |
| 949–989 | 1 | `verticalArrangement = Arrangement.Center)` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 950–958 | 2 | `Surface(modifier = Modifier.size(120.dp), shape = RoundedCornerShape(28.dp), color = MaterialTheme.colorScheme.primaryContainer)` | Lambda de contenido de UI Compose: declara hijos y configuración visual dentro del contenedor. El orden y los `Modifier` influyen en medida, posición, dibujo e interacción. |
| 951–957 | 3 | `Box(contentAlignment = Alignment.Center)` | Lambda de contenido de UI Compose: declara hijos y configuración visual dentro del contenedor. El orden y los `Modifier` influyen en medida, posición, dibujo e interacción. |
| 952–954 | 4 | `Icon(imageVector = if (extension == "pdf")` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 954–956 | 4 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 962–973 | 2 | `val metadata = buildString` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 963–965 | 3 | `if (extension.isNotBlank())` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 966–969 | 3 | `if (mimeType.isNotBlank())` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 970–972 | 3 | `fileSize?.let` | Lambda `let`: transforma/usa el receptor dentro de un ámbito corto; con llamada segura suele ejecutarse solo cuando el receptor no es nulo. |
| 974–976 | 2 | `if (metadata.isNotBlank())` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 981–984 | 2 | `Button(onClick =` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 984–988 | 2 | `})` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 992–1012 | 0 | `private fun copyDocumentToViewerCache(context: Context, uri: Uri, extension: String, prefix: String): File?` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 993–1009 | 1 | `return try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 995–997 | 2 | `if (!directory.exists() && !directory.mkdirs())` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 998–998 | 2 | `val safeExtension = extension.trim().trimStart('.').ifBlank` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 1001–1003 | 2 | `if (file.exists() && file.length() > 0L)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 1004–1007 | 2 | `openInputStream(context, uri).use` | Bloque `use`: el recurso cerrable se libera automáticamente al terminar el cuerpo, incluso si ocurre una excepción. |
| 1004–1006 | 3 | `openInputStream(context, uri).use { input -> file.outputStream().buffered(64 * 1024).use` | Bloque `use`: el recurso cerrable se libera automáticamente al terminar el cuerpo, incluso si ocurre una excepción. |
| 1008–1008 | 2 | `file.takeIf` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 1009–1011 | 1 | `} catch (_: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 1014–1036 | 0 | `private fun copyVideoToPlaybackCache(context: Context, uri: Uri): File?` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 1015–1033 | 1 | `return try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 1017–1019 | 2 | `if (!directory.exists() && !directory.mkdirs())` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 1021–1021 | 2 | `val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mime)?.takeIf` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 1022–1022 | 2 | `.takeIf` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 1025–1027 | 2 | `if (file.exists() && file.length() > 0L)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 1028–1031 | 2 | `openInputStream(context, uri).use` | Bloque `use`: el recurso cerrable se libera automáticamente al terminar el cuerpo, incluso si ocurre una excepción. |
| 1028–1030 | 3 | `openInputStream(context, uri).use { input -> file.outputStream().buffered(64 * 1024).use` | Bloque `use`: el recurso cerrable se libera automáticamente al terminar el cuerpo, incluso si ocurre una excepción. |
| 1032–1032 | 2 | `file.takeIf` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 1033–1035 | 1 | `} catch (_: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 1038–1068 | 0 | `private fun copyAudioToPlaybackCache(context: Context, uri: Uri): File?` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 1039–1065 | 1 | `return try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 1041–1043 | 2 | `if (!directory.exists() && !directory.mkdirs())` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 1045–1047 | 2 | `val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mime)?.takeIf` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 1047–1049 | 2 | `}?: fileExtension(null, uri).takeIf` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 1052–1054 | 2 | `if (file.exists() && file.length() > 0L)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 1055–1061 | 2 | `openInputStream(context, uri).use` | Bloque `use`: el recurso cerrable se libera automáticamente al terminar el cuerpo, incluso si ocurre una excepción. |
| 1057–1060 | 3 | `file.outputStream().buffered(64 * 1024).use` | Bloque `use`: el recurso cerrable se libera automáticamente al terminar el cuerpo, incluso si ocurre una excepción. |
| 1062–1064 | 2 | `file.takeIf` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 1065–1067 | 1 | `} catch (_: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 1070–1088 | 0 | `private fun readTextPreview(context: Context, uri: Uri): String` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 1071–1085 | 1 | `return try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 1072–1084 | 2 | `openInputStream(context, uri).use` | Bloque `use`: el recurso cerrable se libera automáticamente al terminar el cuerpo, incluso si ocurre una excepción. |
| 1074–1076 | 3 | `if (text.indexOf('\u0000') >= 0)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 1076–1083 | 3 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 1077–1082 | 4 | `buildString` | Ámbito delimitado por llaves en profundidad 4. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 1079–1081 | 5 | `if (bytes.size >= MAX_TEXT_BYTES)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 1085–1087 | 1 | `} catch (e: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 1090–1157 | 0 | `private fun extractOfficeText(context: Context, uri: Uri, extension: String): String` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 1091–1154 | 1 | `return try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 1095–1132 | 2 | `ZipInputStream(openInputStream(context, uri).buffered()).use` | Bloque `use`: el recurso cerrable se libera automáticamente al terminar el cuerpo, incluso si ocurre una excepción. |
| 1096–1131 | 3 | `while (entry != null)` | Bucle `while`: se repite mientras la condición siga siendo verdadera; el cuerpo debe producir progreso para evitar un ciclo infinito. |
| 1098–1104 | 4 | `val wanted = when (extension)` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 1105–1128 | 4 | `if (wanted)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 1107–1127 | 5 | `when` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 1108–1112 | 6 | `extension == "xlsx" && entryName == "xl/sharedStrings.xml" ->` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 1109–1111 | 7 | `Regex("<t(?:\\s[^>]*)?>(.*?)</t>", RegexOption.DOT_MATCHES_ALL).findAll(xml).forEach` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 1113–1115 | 6 | `extension == "xlsx" ->` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 1116–1120 | 6 | `extension == "docx" ->` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 1117–1119 | 7 | `Regex("<w:t(?:\\s[^>]*)?>(.*?)</w:t>", RegexOption.DOT_MATCHES_ALL).findAll(xml).forEach` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 1121–1126 | 6 | `extension == "pptx" ->` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 1122–1124 | 7 | `Regex("<a:t(?:\\s[^>]*)?>(.*?)</a:t>", RegexOption.DOT_MATCHES_ALL).findAll(xml).forEach` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 1133–1152 | 2 | `if (extension == "xlsx")` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 1134–1151 | 3 | `worksheetXml.forEachIndexed` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 1136–1149 | 4 | `cellRegex.findAll(xml).forEach` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 1141–1143 | 5 | `val value = if (isShared)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 1142–1142 | 6 | `rawValue.toIntOrNull()?.let` | Lambda `let`: transforma/usa el receptor dentro de un ámbito corto; con llamada segura suele ejecutarse solo cuando el receptor no es nulo. |
| 1143–1145 | 5 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 1146–1148 | 5 | `if (value.isNotBlank())` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 1154–1156 | 1 | `} catch (_: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 1159–1166 | 0 | `private fun decodeXml(value: String): String` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 1160–1162 | 1 | `return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 1162–1165 | 1 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 1168–1179 | 0 | `private fun InputStream.readUpTo(maxBytes: Int): ByteArray` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 1172–1177 | 1 | `while (total < maxBytes)` | Bucle `while`: se repite mientras la condición siga siendo verdadera; el cuerpo debe producir progreso para evitar un ciclo infinito. |
| 1181–1191 | 0 | `private fun openInputStream(context: Context, uri: Uri): InputStream` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 1182–1190 | 1 | `return when (uri.scheme)` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 1183–1186 | 2 | `"file" ->` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 1187–1189 | 2 | `else ->` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 1193–1204 | 0 | `private fun openExternally(context: Context, uri: Uri, name: String?, mimeType: String)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 1194–1199 | 1 | `try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 1197–1197 | 2 | `mimeType.ifBlank` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 1199–1201 | 1 | `} catch (_: ActivityNotFoundException)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 1201–1203 | 1 | `} catch (_: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 1206–1212 | 0 | `private fun toShareableUri(context: Context, uri: Uri): Uri` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 1207–1209 | 1 | `if (uri.scheme != "file")` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 1214–1218 | 0 | `private fun resolveMimeType(context: Context, uri: Uri, name: String?): String` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 1215–1215 | 1 | `context.contentResolver.getType(uri)?.let` | Lambda `let`: transforma/usa el receptor dentro de un ámbito corto; con llamada segura suele ejecutarse solo cuando el receptor no es nulo. |
| 1220–1226 | 0 | `private fun fileExtension(name: String?, uri: Uri): String` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 1222–1224 | 1 | `if (byName.isNotBlank())` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 1228–1231 | 0 | `private fun isTextExtension(extension: String): Boolean` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 1233–1245 | 0 | `private fun resolveFileSize(context: Context, uri: Uri): Long?` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 1234–1242 | 1 | `return try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 1235–1241 | 2 | `when (uri.scheme)` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 1236–1236 | 3 | `"file" -> uri.path?.let` | Lambda `let`: transforma/usa el receptor dentro de un ámbito corto; con llamada segura suele ejecutarse solo cuando el receptor no es nulo. |
| 1236–1236 | 3 | `"file" -> uri.path?.let { File(it) }?.takeIf` | Lambda `let`: transforma/usa el receptor dentro de un ámbito corto; con llamada segura suele ejecutarse solo cuando el receptor no es nulo. |
| 1237–1240 | 3 | `else ->` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 1238–1239 | 4 | `context.contentResolver.openAssetFileDescriptor(uri, "r")?.use` | Bloque `use`: el recurso cerrable se libera automáticamente al terminar el cuerpo, incluso si ocurre una excepción. |
| 1238–1238 | 5 | `context.contentResolver.openAssetFileDescriptor(uri, "r")?.use { descriptor -> descriptor.length.takeIf` | Bloque `use`: el recurso cerrable se libera automáticamente al terminar el cuerpo, incluso si ocurre una excepción. |
| 1242–1244 | 1 | `} catch (_: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 1247–1255 | 0 | `private fun formatFileSize(bytes: Long): String` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 1257–1267 | 0 | `private fun formatTime(milliseconds: Int): String` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 1262–1264 | 1 | `return if (hours > 0)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 1264–1266 | 1 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |

## 7. Lectura de seguridad antes de tocar este archivo

La documentación describe **lo que el código actual ya hace**. No constituye una propuesta de refactorización. Si se quisiera cambiar algo en el futuro, primero habría que preservar contratos públicos, claves de configuración, restricciones de API Android, comportamiento de Compose, lifecycle, operaciones de I/O y compatibilidad con los datos ya persistidos. En esta entrega no se hizo ninguna de esas modificaciones.

# LinkPreviewCard.kt — documentación exhaustiva por bloques

**Ruta de código real:** `app/src/main/java/com/example/mynotes/ui/components/LinkPreviewCard.kt`  
**SHA-256 del archivo ejecutable sin tocar:** `dd4ae0d535dfe48cef3dff2ceb2b8b4a74ca1b4b4cfe75d96c6b60a7c560b3a5`  
**Líneas del código real:** 1739

> **Garantía:** esta documentación se genera en una carpeta paralela. El archivo Kotlin/Kotlin DSL anterior no se modifica. Los fragmentos mostrados abajo son copias de lectura; la aplicación compila usando únicamente el archivo original de `app/...`.

## 1. Papel del archivo

Implementa las tarjetas de vista previa para enlaces y la obtención/normalización de metadatos de múltiples sitios. Contiene lógica de red, parsing HTML/JSON, detección específica de proveedores y fallbacks.

**Arquitectura.** Es uno de los archivos más complejos: separa la URL visible del trabajo de resolver título, imagen, sitio y contenido enriquecido, y ofrece un componente Compose para presentarlo.

**Flujo general.** Flujo típico: se detecta una URL -> se normaliza/procesa según proveedor -> se intenta obtener metadata específica o genérica -> se aplican fallbacks -> el resultado se representa en una tarjeta. El archivo contiene rutas especiales para servicios con HTML/JSON no uniforme.

## 2. Package e imports

El `package` es `com.example.mynotes.ui.components`. Determina el namespace lógico del archivo, resolución de visibilidad `internal`/package-related tooling y la ruta conceptual desde la que otras clases lo importan.

El archivo declara **68 imports**. Cada import evita usar el nombre totalmente calificado en el cuerpo y, además, revela las dependencias técnicas del bloque:

**Android/plataforma:** `android.content.Context`, `android.content.Intent`, `android.net.Uri`, `android.os.Build`, `android.text.Html`, `android.util.LruCache`.

**Jetpack/Compose:** `androidx.compose.foundation.layout.Arrangement`, `androidx.compose.foundation.layout.BoxWithConstraints`, `androidx.compose.foundation.layout.Box`, `androidx.compose.foundation.layout.Column`, `androidx.compose.foundation.layout.Row`, `androidx.compose.foundation.layout.Spacer`, `androidx.compose.foundation.layout.fillMaxSize`, `androidx.compose.foundation.layout.fillMaxWidth`, `androidx.compose.foundation.layout.aspectRatio`, `androidx.compose.foundation.layout.height`, `androidx.compose.foundation.layout.padding`, `androidx.compose.foundation.layout.size`, `androidx.compose.foundation.layout.width`, `androidx.compose.material.icons.Icons`, `androidx.compose.material.icons.filled.OpenInNew`, `androidx.compose.material3.Icon`, `androidx.compose.material3.MaterialTheme`, `androidx.compose.material3.Surface`, `androidx.compose.material3.Text`, `androidx.compose.runtime.Composable`, `androidx.compose.runtime.LaunchedEffect`, `androidx.compose.runtime.getValue`, `androidx.compose.runtime.mutableStateOf`, `androidx.compose.runtime.remember`, `androidx.compose.runtime.setValue`, `androidx.compose.ui.Alignment`, `androidx.compose.ui.graphics.Color`, `androidx.compose.ui.Modifier`, `androidx.compose.ui.layout.ContentScale`, `androidx.compose.ui.platform.LocalContext`, `androidx.compose.ui.text.font.FontWeight`, `androidx.compose.ui.text.style.TextOverflow`, `androidx.compose.ui.unit.dp`.

**Proyecto MyNotes:** `com.example.mynotes.ui.theme.resolveUiTextColor`, `com.example.mynotes.ui.theme.resolveSecondaryUiTextColor`, `com.example.mynotes.ui.theme.resolveUiGraphicColor`, `com.example.mynotes.ui.sound.UiActionSound`, `com.example.mynotes.ui.sound.UiSoundPlayer`.

**Kotlin/corrutinas/Java:** `kotlinx.coroutines.Dispatchers`, `kotlinx.coroutines.async`, `kotlinx.coroutines.awaitAll`, `kotlinx.coroutines.coroutineScope`, `kotlinx.coroutines.delay`, `kotlinx.coroutines.withContext`, `kotlinx.coroutines.sync.Mutex`, `kotlinx.coroutines.sync.Semaphore`, `kotlinx.coroutines.sync.withLock`, `kotlinx.coroutines.sync.withPermit`, `java.io.File`, `java.io.FileOutputStream`, `java.io.InputStreamReader`, `java.net.HttpURLConnection`, `java.net.URI`, `java.net.URL`, `java.net.URLDecoder`, `java.net.URLEncoder`, `java.nio.charset.Charset`, `java.security.MessageDigest`, `java.util.Locale`.

**Otras librerías:** `coil3.compose.AsyncImage`, `org.json.JSONArray`, `org.json.JSONObject`.

## 3. Restricciones e invariantes visibles en el archivo

- **Nulabilidad segura (173 aparición/apariciones):** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`.
- **Fallback nulo (80 aparición/apariciones):** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula.
- **Límite numérico (2 aparición/apariciones):** Se fuerza el valor a un intervalo o umbral para impedir que salgan valores fuera de los límites esperados.
- **Filtro condicional (61 aparición/apariciones):** `takeIf` conserva el valor únicamente cuando el predicado se cumple; en caso contrario lo convierte en `null`.
- **Normalización vacía (13 aparición/apariciones):** `orEmpty` transforma una referencia nula en una colección/cadena vacía, simplificando el consumo posterior.
- **Guardia de API (2 aparición/apariciones):** Se condiciona una API según la versión de Android para no ejecutar llamadas incompatibles en dispositivos antiguos.
- **Límite visual (3 aparición/apariciones):** La UI impone un límite explícito de tamaño o cantidad de líneas para impedir crecimiento visual no controlado.
- **Trabajo IO (2 aparición/apariciones):** El trabajo potencialmente bloqueante se desplaza al dispatcher de I/O en vez de ocupar el hilo principal.

## 4. Bloques de código, uno por uno

### 4.1 `extractLinkUrls` — fun, líneas 85–91

```kotlin
fun extractLinkUrls(text: String): List<String> = UrlRegex.findAll(text).map { match -> match.value.trimEnd('.', ',', ';', '!', ')', ']',
                '}')
        }.filter { it.length > 8 }.distinct().toList()
/**
 * Si el contenido de la nota es únicamente una URL, evita repetir el enlace
 * como texto cuando ya se va a mostrar su tarjeta enriquecida.
 */
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `text: String` — `text` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `List<String>`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.


#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `UrlRegex.findAll`, `match.value.trimEnd`, `distinct`, `toList`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.2 `noteTextForDisplay` — fun, líneas 92–99

```kotlin
fun noteTextForDisplay(content: String, links: List<String>): String {
    val trimmed = content.trim()
    return if (links.size == 1 && trimmed == links.first()) {
        ""
    } else {
        content
    }
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `content: String` — `content` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.
- `links: List<String>` — `links` recibe un valor de tipo `List<String>`. El contrato no marca este parámetro como anulable.

**Retorno:** `String`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.


#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 93 | `val trimmed` | `inferido` | `content.trim()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 94 | `return if (links.size == 1 && trimmed == links.first()) {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `content.trim`, `links.first`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.3 `LinkPreviewData` — class, líneas 101–110

```kotlin
private data class LinkPreviewData(val url: String, val title: String?, val description: String?, val imageUrl: String?,
    val siteName: String?, val host: String, val imageCandidates: List<String> = emptyList(), val cachedImagePath: String? = null) {
    companion object {
        fun basic(url: String): LinkPreviewData {
            val host = safeHost(url)
            return LinkPreviewData(url = url, title = null, description = null, imageUrl = directImageUrl(url), siteName = host, host = host
            )
        }
    }
}
```

#### Qué hace y por qué existe

Implementa las tarjetas de vista previa para enlaces y la obtención/normalización de metadatos de múltiples sitios. Contiene lógica de red, parsing HTML/JSON, detección específica de proveedores y fallbacks.

#### Contrato de la declaración

**Parámetros del constructor/encabezado:**
- `val url: String` — `url` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable. Además, al declararse con `val/var` en el constructor se convierte en propiedad de la instancia.
- `val title: String?` — `title` recibe un valor de tipo `String?`. El contrato permite `null`; el bloque debe decidir explícita o implícitamente qué significa la ausencia. Además, al declararse con `val/var` en el constructor se convierte en propiedad de la instancia.
- `val description: String?` — `description` recibe un valor de tipo `String?`. El contrato permite `null`; el bloque debe decidir explícita o implícitamente qué significa la ausencia. Además, al declararse con `val/var` en el constructor se convierte en propiedad de la instancia.
- `val imageUrl: String?` — `imageUrl` recibe un valor de tipo `String?`. El contrato permite `null`; el bloque debe decidir explícita o implícitamente qué significa la ausencia. Además, al declararse con `val/var` en el constructor se convierte en propiedad de la instancia.
- `val siteName: String?` — `siteName` recibe un valor de tipo `String?`. El contrato permite `null`; el bloque debe decidir explícita o implícitamente qué significa la ausencia. Además, al declararse con `val/var` en el constructor se convierte en propiedad de la instancia.
- `val host: String` — `host` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable. Además, al declararse con `val/var` en el constructor se convierte en propiedad de la instancia.
- `val imageCandidates: List<String> = emptyList()` — `imageCandidates` recibe un valor de tipo `List<String>`. El contrato no marca este parámetro como anulable. Tiene valor por defecto `emptyList()`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada. Además, al declararse con `val/var` en el constructor se convierte en propiedad de la instancia.
- `val cachedImagePath: String? = null` — `cachedImagePath` recibe un valor de tipo `String?`. El contrato permite `null`; el bloque debe decidir explícita o implícitamente qué significa la ausencia. Tiene valor por defecto `null`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada. Además, al declararse con `val/var` en el constructor se convierte en propiedad de la instancia.
**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 102 | `val siteName` | `String?, val host: String, val imageCandidates: List<String>` | `emptyList(), val cachedImagePath: String? = null) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo declarado es `String?, val host: String, val imageCandidates: List<String>`. Admite `null`, así que los consumidores deben contemplar ausencia de valor. |
| 105 | `val host` | `inferido` | `safeHost(url)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 106 | `return LinkPreviewData(url = url, title = null, description = null, imageUrl = directImageUrl(url), siteName = host, host = host` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

#### Efectos secundarios y recursos

- **Caché:** Lee, llena, invalida o administra caché para evitar recomputación o decodificación repetida.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `emptyList`, `safeHost`, `LinkPreviewData`, `directImageUrl`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener coherencia entre creación, clave, lectura e invalidación de caché; cambiar solo una de esas etapas puede dejar datos obsoletos.

### 4.4 `basic` — fun, líneas 104–108

```kotlin
        fun basic(url: String): LinkPreviewData {
            val host = safeHost(url)
            return LinkPreviewData(url = url, title = null, description = null, imageUrl = directImageUrl(url), siteName = host, host = host
            )
        }
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `url: String` — `url` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `LinkPreviewData`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.


#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 105 | `val host` | `inferido` | `safeHost(url)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 106 | `return LinkPreviewData(url = url, title = null, description = null, imageUrl = directImageUrl(url), siteName = host, host = host` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `safeHost`, `LinkPreviewData`, `directImageUrl`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.5 `LinkPreviewRepository` — object, líneas 112–273

```kotlin
private object LinkPreviewRepository {
    // … el cuerpo completo permanece en el archivo real; sus miembros se documentan individualmente abajo …
}
```

#### Qué hace y por qué existe

Implementa las tarjetas de vista previa para enlaces y la obtención/normalización de metadatos de múltiples sitios. Contiene lógica de red, parsing HTML/JSON, detección específica de proveedores y fallbacks.

#### Contrato de la declaración

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 113 | `val cache` | `inferido` | `LruCache<String, LinkPreviewData>(120)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Es una caché acotada por política LRU: privilegia entradas recientes y permite expulsión automática cuando se supera el límite. Forma parte de la estrategia de reutilización de resultados para evitar repetir trabajo costoso. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 114 | `val locks` | `inferido` | `mutableMapOf<String, Mutex>()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 115 | `val legacyLoadGate` | `inferido` | `Semaphore(permits = 1)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 116 | `val loadGate` | `inferido` | `Semaphore(permits = 2)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 139 | `val gate` | `inferido` | `if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) legacyLoadGate else loadGate` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 149 | `val persisted` | `inferido` | `readPersistedPreview(context = context, url = url)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 151 | `val ready` | `inferido` | `ensureThumbnailCached(context = context, preview = persisted)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 156 | `val fetched` | `inferido` | `fetch(url)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 157 | `val ready` | `inferido` | `ensureThumbnailCached(context = context, preview = fetched)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 165 | `val basic` | `inferido` | `LinkPreviewData.basic(url)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 172 | `val socialProviderPreview` | `inferido` | `when {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 198 | `var connection` | `HttpURLConnection?` | `null` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo declarado es `HttpURLConnection?`. Admite `null`, así que los consumidores deben contemplar ausencia de valor. |
| 214 | `val responseCode` | `inferido` | `connection.responseCode` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 218 | `val finalUrl` | `inferido` | `connection.url?.toString() ?: url` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 236 | `val contentType` | `inferido` | `connection.contentType?.lowercase(Locale.ROOT).orEmpty()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. Normaliza una ausencia hacia un valor vacío/alternativo para simplificar el código consumidor. |
| 244 | `val charset` | `inferido` | `charsetFromContentType(connection.contentType)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 245 | `val html` | `inferido` | `connection.inputStream.use { input -> readLimitedHtml(reader = InputStreamReader(input, charset))` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 247 | `val parsed` | `inferido` | `parseHtml(pageUrl = finalUrl, html = html)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 249 | `val enriched` | `inferido` | `enrichSocialHtmlPreview(originalUrl = url, pageUrl = finalUrl, html = html, base = parsed,` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 256 | `val spotifyEntityUrl` | `inferido` | `findSpotifyEntityUrl(url)?: findSpotifyEntityUrl(finalUrl)?: findSpotifyEntityUrlInText(html)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 129 | `return readPersistedPreview(context = context, url = url)?.also { cached -> cache.put(url, cached)` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 150 | `if (persisted != null) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 154 | `return@withLock ready` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 173 | `isMetaSocialUrl(url) -> fetchMetaOEmbedPreview(url)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 174 | `isRedditRelatedUrl(url) -> fetchRedditJsonPreview(entityUrl = url, clickUrl = url)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 175 | `else -> null` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |
| 177 | `if (socialProviderPreview?.imageUrl != null \|\| socialProviderPreview?.imageCandidates?.isNotEmpty() == true) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 178 | `return socialProviderPreview` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 180 | `if (isSpotifyRelatedUrl(url)) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 185 | `if (isDouyinRelatedUrl(url)) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 187 | `return preview` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 191 | `if (isTikTokRelatedUrl(url)) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 195 | `if (directImageUrl(url) != null) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 196 | `return basic.copy(title = fileNameFromUrl(url).takeIf { it.isNotBlank() })` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 199 | `return try {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 215 | `if (responseCode !in 200..399) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 216 | `return basic.withKnownProviderFallback()` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 219 | `if (isRedditRelatedUrl(url) \|\| isRedditRelatedUrl(finalUrl)) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 222 | `return preview` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 226 | `if (isMetaSocialUrl(url) \|\| isMetaSocialUrl(finalUrl)) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 228 | `return preview.copy(url = url)` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 232 | `if (isTikTokRelatedUrl(url) \|\| isTikTokRelatedUrl(finalUrl)) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 237 | `if (contentType.startsWith("image/")) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 238 | `return LinkPreviewData.basic(finalUrl).copy(url = url, title = fileNameFromUrl(finalUrl).takeIf { it.isNotBlank() },` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 241 | `if (contentType.isNotBlank() && !contentType.contains("html") && !contentType.contains("xhtml")) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 242 | `return LinkPreviewData.basic(finalUrl).copy(url = url).withKnownProviderFallback()` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 248 | `if (isMetaSocialUrl(url) \|\| isMetaSocialUrl(finalUrl) \|\| isRedditRelatedUrl(url) \|\| isRedditRelatedUrl(finalUrl)) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 251 | `if (enriched.imageUrl != null \|\| enriched.imageCandidates.isNotEmpty()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 252 | `return enriched` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 255 | `if (isSpotifyRelatedUrl(url) \|\| isSpotifyRelatedUrl(finalUrl)) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 257 | `if (spotifyEntityUrl != null) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 261 | `return parsed.copy(url = url, siteName = "Spotify").withKnownProviderFallback()` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 263 | `if (isDouyinRelatedUrl(url) \|\| isDouyinRelatedUrl(finalUrl)) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 264 | `return enrichDouyinPreview(originalUrl = url, pageUrl = finalUrl, html = html, base = parsed)` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 18.
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 3.
- **Filtro condicional:** `takeIf` conserva el valor únicamente cuando el predicado se cumple; en caso contrario lo convierte en `null`. Apariciones en este bloque: 2.
- **Normalización vacía:** `orEmpty` transforma una referencia nula en una colección/cadena vacía, simplificando el consumo posterior. Apariciones en este bloque: 1.
- **Guardia de API:** Se condiciona una API según la versión de Android para no ejecutar llamadas incompatibles en dispositivos antiguos. Apariciones en este bloque: 1.
- **Trabajo IO:** El trabajo potencialmente bloqueante se desplaza al dispatcher de I/O en vez de ocupar el hilo principal. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

- **Caché:** Lee, llena, invalida o administra caché para evitar recomputación o decodificación repetida.
- **Mutación de colección/estado compartido:** Modifica una colección/mapa/estructura existente; el cambio permanece visible para quienes compartan esa misma instancia.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `Semaphore`, `cache.get`, `synchronized`, `locks.getOrPut`, `Mutex`, `readPersistedPreview`, `cache.put`, `withContext`, `ensureThumbnailCached`, `persistPreview`, `lockFor`, `fetch`, `LinkPreviewData.basic`, `isMetaSocialUrl`, `fetchMetaOEmbedPreview`, `isRedditRelatedUrl`, `fetchRedditJsonPreview`, `isNotEmpty`, `isSpotifyRelatedUrl`, `findSpotifyEntityUrl`, `fetchSpotifyOEmbed`, `isDouyinRelatedUrl`, `fetchDouyinDirectPreview`, `preview.imageCandidates.isNotEmpty`, `isTikTokRelatedUrl`, `fetchTikTokOEmbed`, `directImageUrl`, `basic.copy`, `fileNameFromUrl`, `it.isNotBlank`, `URL`, `openConnection`, `setRequestProperty`, `douyinUserAgent`, `previewUserAgent`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.
- Conservar las guardas de versión antes de APIs que no existen en Android antiguos.
- No mover trabajo bloqueante al hilo principal; el uso de `Dispatchers.IO` forma parte de la protección de rendimiento.
- Mantener coherencia entre creación, clave, lectura e invalidación de caché; cambiar solo una de esas etapas puede dejar datos obsoletos.

### 4.6 `peekMemory` — fun, líneas 123–126

```kotlin
    fun peekMemory(url: String): LinkPreviewData? = cache.get(url)
    private fun lockFor(url: String): Mutex = synchronized(locks) {
            locks.getOrPut(url) { Mutex() }
        }
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `url: String` — `url` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `LinkPreviewData?`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.


#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

- **Caché:** Lee, llena, invalida o administra caché para evitar recomputación o decodificación repetida.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `cache.get`, `synchronized`, `locks.getOrPut`, `Mutex`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener coherencia entre creación, clave, lectura e invalidación de caché; cambiar solo una de esas etapas puede dejar datos obsoletos.

### 4.7 `lockFor` — fun, líneas 124–126

```kotlin
    private fun lockFor(url: String): Mutex = synchronized(locks) {
            locks.getOrPut(url) { Mutex() }
        }
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `url: String` — `url` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `Mutex`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `synchronized`, `locks.getOrPut`, `Mutex`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.8 `peek` — fun, líneas 127–131

```kotlin
    fun peek(context: Context, url: String): LinkPreviewData? {
        cache.get(url)?.let { return it }
        return readPersistedPreview(context = context, url = url)?.also { cached -> cache.put(url, cached)
        }
    }
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `context: Context` — `context` recibe un valor de tipo `Context`. El contrato no marca este parámetro como anulable. Da acceso a recursos y APIs Android; su vida útil debe corresponder con el uso realizado.
- `url: String` — `url` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `LinkPreviewData?`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.


#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 129 | `return readPersistedPreview(context = context, url = url)?.also { cached -> cache.put(url, cached)` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 2.

#### Efectos secundarios y recursos

- **Caché:** Lee, llena, invalida o administra caché para evitar recomputación o decodificación repetida.
- **Mutación de colección/estado compartido:** Modifica una colección/mapa/estructura existente; el cambio permanece visible para quienes compartan esa misma instancia.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `cache.get`, `readPersistedPreview`, `cache.put`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener coherencia entre creación, clave, lectura e invalidación de caché; cambiar solo una de esas etapas puede dejar datos obsoletos.

### 4.9 `load` — fun, líneas 132–163

```kotlin
    suspend fun load(context: Context, url: String): LinkPreviewData = withContext(Dispatchers.IO) {
            /*
             * Toda la ruta persistencia/red/miniatura queda limitada. En API
             * 28 o anterior permitimos un único trabajo de link preview a la
             * vez; en Android moderno, dos. Esto incluye tanto precarga como
             * tarjetas visibles y evita ráfagas de descargas/decodificación.
             */
            val gate = if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) legacyLoadGate else loadGate
            gate.withPermit {
                cache.get(url)?.let { cached -> return@withPermit ensureThumbnailCached(context = context, preview = cached).also { ready ->
                        cache.put(url, ready)
                        persistPreview(context, url, ready)
                    }
                }
                lockFor(url).withLock {
                    cache.get(url)?.let { cached -> return@withLock cached
                    }
                    val persisted = readPersistedPreview(context = context, url = url)
                    if (persisted != null) {
                        val ready = ensureThumbnailCached(context = context, preview = persisted)
                        cache.put(url, ready)
                        persistPreview(context, url, ready)
                        return@withLock ready
                    }
                    val fetched = fetch(url)
                    val ready = ensureThumbnailCached(context = context, preview = fetched)
                    cache.put(url, ready)
                    persistPreview(context, url, ready)
                    ready
                }
            }
        }
```

#### Qué hace y por qué existe

Carga o reconstruye un recurso/dato a partir de sus entradas, aplicando las capas de caché/normalización definidas en el cuerpo.

#### Contrato de la declaración

**Parámetros:**

- `context: Context` — `context` recibe un valor de tipo `Context`. El contrato no marca este parámetro como anulable. Da acceso a recursos y APIs Android; su vida útil debe corresponder con el uso realizado.
- `url: String` — `url` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `LinkPreviewData`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `suspend` permite suspender sin bloquear el hilo; el llamador debe estar dentro de una corrutina u otra función suspendible.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 139 | `val gate` | `inferido` | `if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) legacyLoadGate else loadGate` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 149 | `val persisted` | `inferido` | `readPersistedPreview(context = context, url = url)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 151 | `val ready` | `inferido` | `ensureThumbnailCached(context = context, preview = persisted)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 156 | `val fetched` | `inferido` | `fetch(url)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 157 | `val ready` | `inferido` | `ensureThumbnailCached(context = context, preview = fetched)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 150 | `if (persisted != null) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 154 | `return@withLock ready` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 2.
- **Guardia de API:** Se condiciona una API según la versión de Android para no ejecutar llamadas incompatibles en dispositivos antiguos. Apariciones en este bloque: 1.
- **Trabajo IO:** El trabajo potencialmente bloqueante se desplaza al dispatcher de I/O en vez de ocupar el hilo principal. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

- **Caché:** Lee, llena, invalida o administra caché para evitar recomputación o decodificación repetida.
- **Mutación de colección/estado compartido:** Modifica una colección/mapa/estructura existente; el cambio permanece visible para quienes compartan esa misma instancia.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `withContext`, `cache.get`, `ensureThumbnailCached`, `cache.put`, `persistPreview`, `lockFor`, `readPersistedPreview`, `fetch`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar las guardas de versión antes de APIs que no existen en Android antiguos.
- No mover trabajo bloqueante al hilo principal; el uso de `Dispatchers.IO` forma parte de la protección de rendimiento.
- Mantener coherencia entre creación, clave, lectura e invalidación de caché; cambiar solo una de esas etapas puede dejar datos obsoletos.

### 4.10 `fetch` — fun, líneas 164–272

```kotlin
    private fun fetch(url: String): LinkPreviewData {
        val basic = LinkPreviewData.basic(url)
        /*
         * Varias redes sociales ya no exponen una página HTML sencilla a
         * clientes móviles. Antes de intentar raspar la página usamos sus
         * endpoints públicos de vista previa cuando existen. Si el endpoint
         * no trae imagen seguimos con el HTML normal como segundo intento.
         */
        val socialProviderPreview = when {
                isMetaSocialUrl(url) -> fetchMetaOEmbedPreview(url)
                isRedditRelatedUrl(url) -> fetchRedditJsonPreview(entityUrl = url, clickUrl = url)
                else -> null
            }
        if (socialProviderPreview?.imageUrl != null || socialProviderPreview?.imageCandidates?.isNotEmpty() == true) {
            return socialProviderPreview
        }
        if (isSpotifyRelatedUrl(url)) {
            findSpotifyEntityUrl(url)?.let { entityUrl -> fetchSpotifyOEmbed(entityUrl = entityUrl, clickUrl = url)
                }?.let { spotifyPreview -> return spotifyPreview
                }
        }
        if (isDouyinRelatedUrl(url)) {
            fetchDouyinDirectPreview(url)?.let { preview -> if (preview.imageUrl != null || preview.imageCandidates.isNotEmpty()) {
                    return preview
                }
            }
        }
        if (isTikTokRelatedUrl(url)) {
            fetchTikTokOEmbed(entityUrl = url, clickUrl = url)?.let { preview -> return preview
            }
        }
        if (directImageUrl(url) != null) {
            return basic.copy(title = fileNameFromUrl(url).takeIf { it.isNotBlank() })
        }
        var connection: HttpURLConnection? = null
        return try {
            connection = (URL(url).openConnection() as HttpURLConnection).apply {
                        instanceFollowRedirects = true
                        connectTimeout = 6_000
                        readTimeout = 7_000
                        requestMethod = "GET"
                        setRequestProperty("User-Agent", if (isDouyinRelatedUrl(url)) {
                                douyinUserAgent()
                            } else {
                                previewUserAgent()
                            })
                        setRequestProperty("Accept", "text/html,application/xhtml+xml,image/webp,image/jpeg,image/png,image/*,*/*;q=0.8")
                        setRequestProperty("Accept-Language", "es-MX,es;q=0.9,en;q=0.7")
                    }
            connection.connect()
            val responseCode = connection.responseCode
            if (responseCode !in 200..399) {
                return basic.withKnownProviderFallback()
            }
            val finalUrl = connection.url?.toString() ?: url
            if (isRedditRelatedUrl(url) || isRedditRelatedUrl(finalUrl)) {
                fetchRedditJsonPreview(entityUrl = finalUrl, clickUrl = url)?.let { preview -> if (preview.imageUrl != null ||
                        preview.imageCandidates.isNotEmpty()) {
                        return preview
                    }
                }
            }
            if (isMetaSocialUrl(url) || isMetaSocialUrl(finalUrl)) {
                fetchMetaOEmbedPreview(finalUrl)?.let { preview -> if (preview.imageUrl != null || preview.imageCandidates.isNotEmpty()) {
                        return preview.copy(url = url)
                    }
                }
            }
            if (isTikTokRelatedUrl(url) || isTikTokRelatedUrl(finalUrl)) {
                fetchTikTokOEmbed(entityUrl = finalUrl, clickUrl = url)?.let { preview -> return preview
                }
            }
            val contentType = connection.contentType?.lowercase(Locale.ROOT).orEmpty()
            if (contentType.startsWith("image/")) {
                return LinkPreviewData.basic(finalUrl).copy(url = url, title = fileNameFromUrl(finalUrl).takeIf { it.isNotBlank() },
                    imageUrl = finalUrl)
            }
            if (contentType.isNotBlank() && !contentType.contains("html") && !contentType.contains("xhtml")) {
                return LinkPreviewData.basic(finalUrl).copy(url = url).withKnownProviderFallback()
            }
            val charset = charsetFromContentType(connection.contentType)
            val html = connection.inputStream.use { input -> readLimitedHtml(reader = InputStreamReader(input, charset))
                }
            val parsed = parseHtml(pageUrl = finalUrl, html = html)
            if (isMetaSocialUrl(url) || isMetaSocialUrl(finalUrl) || isRedditRelatedUrl(url) || isRedditRelatedUrl(finalUrl)) {
                val enriched = enrichSocialHtmlPreview(originalUrl = url, pageUrl = finalUrl, html = html, base = parsed,
                        providerPreview = socialProviderPreview)
                if (enriched.imageUrl != null || enriched.imageCandidates.isNotEmpty()) {
                    return enriched
                }
            }
            if (isSpotifyRelatedUrl(url) || isSpotifyRelatedUrl(finalUrl)) {
                val spotifyEntityUrl = findSpotifyEntityUrl(url)?: findSpotifyEntityUrl(finalUrl)?: findSpotifyEntityUrlInText(html)
                if (spotifyEntityUrl != null) {
                    fetchSpotifyOEmbed(entityUrl = spotifyEntityUrl, clickUrl = url)?.let { spotifyPreview -> return spotifyPreview
                    }
                }
                return parsed.copy(url = url, siteName = "Spotify").withKnownProviderFallback()
            }
            if (isDouyinRelatedUrl(url) || isDouyinRelatedUrl(finalUrl)) {
                return enrichDouyinPreview(originalUrl = url, pageUrl = finalUrl, html = html, base = parsed)
            }
            parsed.copy(url = url).withKnownProviderFallback()
        } catch (_: Exception) {
            basic.withKnownProviderFallback()
        } finally {
            connection?.disconnect()
        }
    }
```

#### Qué hace y por qué existe

Modelo persistente de Room: define los campos que se almacenan y las restricciones de la entidad.

#### Contrato de la declaración

**Parámetros:**

- `url: String` — `url` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `LinkPreviewData`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 165 | `val basic` | `inferido` | `LinkPreviewData.basic(url)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 172 | `val socialProviderPreview` | `inferido` | `when {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 198 | `var connection` | `HttpURLConnection?` | `null` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo declarado es `HttpURLConnection?`. Admite `null`, así que los consumidores deben contemplar ausencia de valor. |
| 214 | `val responseCode` | `inferido` | `connection.responseCode` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 218 | `val finalUrl` | `inferido` | `connection.url?.toString() ?: url` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 236 | `val contentType` | `inferido` | `connection.contentType?.lowercase(Locale.ROOT).orEmpty()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. Normaliza una ausencia hacia un valor vacío/alternativo para simplificar el código consumidor. |
| 244 | `val charset` | `inferido` | `charsetFromContentType(connection.contentType)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 245 | `val html` | `inferido` | `connection.inputStream.use { input -> readLimitedHtml(reader = InputStreamReader(input, charset))` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 247 | `val parsed` | `inferido` | `parseHtml(pageUrl = finalUrl, html = html)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 249 | `val enriched` | `inferido` | `enrichSocialHtmlPreview(originalUrl = url, pageUrl = finalUrl, html = html, base = parsed,` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 256 | `val spotifyEntityUrl` | `inferido` | `findSpotifyEntityUrl(url)?: findSpotifyEntityUrl(finalUrl)?: findSpotifyEntityUrlInText(html)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 173 | `isMetaSocialUrl(url) -> fetchMetaOEmbedPreview(url)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 174 | `isRedditRelatedUrl(url) -> fetchRedditJsonPreview(entityUrl = url, clickUrl = url)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 175 | `else -> null` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |
| 177 | `if (socialProviderPreview?.imageUrl != null \|\| socialProviderPreview?.imageCandidates?.isNotEmpty() == true) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 178 | `return socialProviderPreview` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 180 | `if (isSpotifyRelatedUrl(url)) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 185 | `if (isDouyinRelatedUrl(url)) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 187 | `return preview` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 191 | `if (isTikTokRelatedUrl(url)) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 195 | `if (directImageUrl(url) != null) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 196 | `return basic.copy(title = fileNameFromUrl(url).takeIf { it.isNotBlank() })` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 199 | `return try {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 215 | `if (responseCode !in 200..399) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 216 | `return basic.withKnownProviderFallback()` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 219 | `if (isRedditRelatedUrl(url) \|\| isRedditRelatedUrl(finalUrl)) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 222 | `return preview` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 226 | `if (isMetaSocialUrl(url) \|\| isMetaSocialUrl(finalUrl)) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 228 | `return preview.copy(url = url)` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 232 | `if (isTikTokRelatedUrl(url) \|\| isTikTokRelatedUrl(finalUrl)) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 237 | `if (contentType.startsWith("image/")) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 238 | `return LinkPreviewData.basic(finalUrl).copy(url = url, title = fileNameFromUrl(finalUrl).takeIf { it.isNotBlank() },` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 241 | `if (contentType.isNotBlank() && !contentType.contains("html") && !contentType.contains("xhtml")) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 242 | `return LinkPreviewData.basic(finalUrl).copy(url = url).withKnownProviderFallback()` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 248 | `if (isMetaSocialUrl(url) \|\| isMetaSocialUrl(finalUrl) \|\| isRedditRelatedUrl(url) \|\| isRedditRelatedUrl(finalUrl)) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 251 | `if (enriched.imageUrl != null \|\| enriched.imageCandidates.isNotEmpty()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 252 | `return enriched` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 255 | `if (isSpotifyRelatedUrl(url) \|\| isSpotifyRelatedUrl(finalUrl)) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 257 | `if (spotifyEntityUrl != null) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 261 | `return parsed.copy(url = url, siteName = "Spotify").withKnownProviderFallback()` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 263 | `if (isDouyinRelatedUrl(url) \|\| isDouyinRelatedUrl(finalUrl)) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 264 | `return enrichDouyinPreview(originalUrl = url, pageUrl = finalUrl, html = html, base = parsed)` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 14.
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 3.
- **Filtro condicional:** `takeIf` conserva el valor únicamente cuando el predicado se cumple; en caso contrario lo convierte en `null`. Apariciones en este bloque: 2.
- **Normalización vacía:** `orEmpty` transforma una referencia nula en una colección/cadena vacía, simplificando el consumo posterior. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `LinkPreviewData.basic`, `isMetaSocialUrl`, `fetchMetaOEmbedPreview`, `isRedditRelatedUrl`, `fetchRedditJsonPreview`, `isNotEmpty`, `isSpotifyRelatedUrl`, `findSpotifyEntityUrl`, `fetchSpotifyOEmbed`, `isDouyinRelatedUrl`, `fetchDouyinDirectPreview`, `preview.imageCandidates.isNotEmpty`, `isTikTokRelatedUrl`, `fetchTikTokOEmbed`, `directImageUrl`, `basic.copy`, `fileNameFromUrl`, `it.isNotBlank`, `URL`, `openConnection`, `setRequestProperty`, `douyinUserAgent`, `previewUserAgent`, `connection.connect`, `basic.withKnownProviderFallback`, `toString`, `preview.copy`, `lowercase`, `orEmpty`, `contentType.startsWith`, `copy`, `contentType.isNotBlank`, `contentType.contains`, `withKnownProviderFallback`, `charsetFromContentType`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.

### 4.11 `preloadLinkPreviews` — fun, líneas 279–301

```kotlin
suspend fun preloadLinkPreviews(context: Context, urls: List<String>, performanceMode: String = "balanced") {
    val appContext = context.applicationContext
    val maxUrls = when (performanceMode) {
        "performance" -> 12
        "quality" -> 40
        else -> 24
    }
    val parallelism = if (performanceMode == "quality") 2 else 1
    val pauseBetweenBatchesMs = when (performanceMode) {
        "performance" -> 120L
        "quality" -> 40L
        else -> 75L
    }
    urls.asSequence().filter { it.startsWith("http", ignoreCase = true) }.distinct().take(maxUrls).chunked(parallelism).forEach { batch ->
        coroutineScope {
            batch.map { url -> async(Dispatchers.IO) {
                    LinkPreviewRepository.load(context = appContext, url = url)
                }
            }.awaitAll()
        }
        if (pauseBetweenBatchesMs > 0L) delay(pauseBetweenBatchesMs)
    }
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `context: Context` — `context` recibe un valor de tipo `Context`. El contrato no marca este parámetro como anulable. Da acceso a recursos y APIs Android; su vida útil debe corresponder con el uso realizado.
- `urls: List<String>` — `urls` recibe un valor de tipo `List<String>`. El contrato no marca este parámetro como anulable.
- `performanceMode: String = "balanced"` — `performanceMode` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable. Tiene valor por defecto `"balanced"`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada. Aunque el tipo sea `String`, semánticamente suele funcionar como clave/enumeración textual; las ramas del código delimitan los valores reconocidos.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `suspend` permite suspender sin bloquear el hilo; el llamador debe estar dentro de una corrutina u otra función suspendible.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 280 | `val appContext` | `inferido` | `context.applicationContext` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El `Context` da acceso a recursos/servicios/almacenamiento Android; debe respetarse el ciclo de vida del contexto recibido. |
| 281 | `val maxUrls` | `inferido` | `when (performanceMode) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 286 | `val parallelism` | `inferido` | `if (performanceMode == "quality") 2 else 1` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 287 | `val pauseBetweenBatchesMs` | `inferido` | `when (performanceMode) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 282 | `"performance" -> 12` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 283 | `"quality" -> 40` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 284 | `else -> 24` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |
| 288 | `"performance" -> 120L` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 289 | `"quality" -> 40L` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 290 | `else -> 75L` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |
| 299 | `if (pauseBetweenBatchesMs > 0L) delay(pauseBetweenBatchesMs)` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |

**Restricciones concretas que aparecen en este bloque:**
- **Trabajo IO:** El trabajo potencialmente bloqueante se desplaza al dispatcher de I/O en vez de ocupar el hilo principal. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `urls.asSequence`, `it.startsWith`, `distinct`, `take`, `chunked`, `async`, `LinkPreviewRepository.load`, `awaitAll`, `delay`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.
- No mover trabajo bloqueante al hilo principal; el uso de `Dispatchers.IO` forma parte de la protección de rendimiento.
- Preservar la normalización y correspondencia de `performanceMode`, porque otras capas usan las mismas claves para rendimiento/calidad.

### 4.12 `previewUserAgent` — fun, líneas 303–308

```kotlin
private fun previewUserAgent(): String = "Mozilla/5.0 (Linux; Android 10) AppleWebKit/537.36 " +
        "(KHTML, like Gecko) Chrome/124.0 Mobile Safari/537.36"
/**
 * Las páginas share de Douyin actualmente entregan _ROUTER_DATA de forma
 * mucho más consistente con un User-Agent de Safari/iPhone.
 */
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:** ninguno explícito.

**Retorno:** `String`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

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

### 4.13 `douyinUserAgent` — fun, líneas 309–310

```kotlin
private fun douyinUserAgent(): String = "Mozilla/5.0 (iPhone; CPU iPhone OS 17_5 like Mac OS X) " +
        "AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.5 " + "Mobile/15E148 Safari/604.1"
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:** ninguno explícito.

**Retorno:** `String`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

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

### 4.14 `previewCachePreferences` — fun, líneas 311–312

```kotlin
private fun previewCachePreferences(context: Context) = context.getSharedPreferences("link_preview_cache_v$PREVIEW_CACHE_VERSION",
        Context.MODE_PRIVATE)
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `context: Context` — `context` recibe un valor de tipo `Context`. El contrato no marca este parámetro como anulable. Da acceso a recursos y APIs Android; su vida útil debe corresponder con el uso realizado.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

- **Caché:** Lee, llena, invalida o administra caché para evitar recomputación o decodificación repetida.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `context.getSharedPreferences`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener coherencia entre creación, clave, lectura e invalidación de caché; cambiar solo una de esas etapas puede dejar datos obsoletos.

### 4.15 `previewCacheKey` — fun, líneas 313–314

```kotlin
private fun previewCacheKey(url: String): String = "preview_" + sha256(url)
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `url: String` — `url` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `String`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

- **Caché:** Lee, llena, invalida o administra caché para evitar recomputación o decodificación repetida.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `sha256`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener coherencia entre creación, clave, lectura e invalidación de caché; cambiar solo una de esas etapas puede dejar datos obsoletos.

### 4.16 `persistPreview` — fun, líneas 315–322

```kotlin
private fun persistPreview(context: Context, originalUrl: String, preview: LinkPreviewData) {
    val json = JSONObject().put("savedAt", System.currentTimeMillis()).put("url", preview.url)
            .put("title", preview.title ?: JSONObject.NULL).put("description", preview.description ?: JSONObject.NULL)
            .put("imageUrl", preview.imageUrl ?: JSONObject.NULL).put("siteName", preview.siteName ?: JSONObject.NULL)
            .put("host", preview.host).put("imageCandidates", JSONArray(preview.imageCandidates))
            .put("cachedImagePath", preview.cachedImagePath ?: JSONObject.NULL)
    previewCachePreferences(context).edit().putString(previewCacheKey(originalUrl), json.toString()).apply()
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `context: Context` — `context` recibe un valor de tipo `Context`. El contrato no marca este parámetro como anulable. Da acceso a recursos y APIs Android; su vida útil debe corresponder con el uso realizado.
- `originalUrl: String` — `originalUrl` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.
- `preview: LinkPreviewData` — `preview` recibe un valor de tipo `LinkPreviewData`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 316 | `val json` | `inferido` | `JSONObject().put("savedAt", System.currentTimeMillis()).put("url", preview.url)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

**Restricciones concretas que aparecen en este bloque:**
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 5.

#### Efectos secundarios y recursos

- **Caché:** Lee, llena, invalida o administra caché para evitar recomputación o decodificación repetida.
- **Mutación de colección/estado compartido:** Modifica una colección/mapa/estructura existente; el cambio permanece visible para quienes compartan esa misma instancia.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `JSONObject`, `put`, `System.currentTimeMillis`, `JSONArray`, `previewCachePreferences`, `edit`, `putString`, `previewCacheKey`, `json.toString`, `apply`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener coherencia entre creación, clave, lectura e invalidación de caché; cambiar solo una de esas etapas puede dejar datos obsoletos.

### 4.17 `readPersistedPreview` — fun, líneas 324–348

```kotlin
private fun readPersistedPreview(context: Context, url: String): LinkPreviewData? {
    val key = previewCacheKey(url)
    val raw = previewCachePreferences(context).getString(key, null)?: return null
    return try {
        val json = JSONObject(raw)
        val savedAt = json.optLong("savedAt", 0L)
        if (savedAt <= 0L || System.currentTimeMillis() - savedAt > PREVIEW_CACHE_MAX_AGE_MS) {
            previewCachePreferences(context).edit().remove(key).apply()
            return null
        }
        val cachedPath = json.optString("cachedImagePath").takeIf { it.isNotBlank() && it != "null" }?.takeIf { File(it).isFile }
        val imageCandidates = json.optJSONArray("imageCandidates")?.let { array -> buildList {
                        for (index in 0 until array.length()) {
                            array.optString(index).takeIf { it.startsWith("http", ignoreCase = true) }?.let(::add)
                        }
                    }
                }.orEmpty()
        LinkPreviewData(url = json.optString("url", url), title = json.optNullableString("title"),
            description = json.optNullableString("description"), imageUrl = json.optNullableString("imageUrl"),
            siteName = json.optNullableString("siteName"), host = json.optString("host", safeHost(url)), imageCandidates = imageCandidates,
            cachedImagePath = cachedPath)
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
- `url: String` — `url` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `LinkPreviewData?`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 325 | `val key` | `inferido` | `previewCacheKey(url)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 326 | `val raw` | `inferido` | `previewCachePreferences(context).getString(key, null)?: return null` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 328 | `val json` | `inferido` | `JSONObject(raw)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 329 | `val savedAt` | `inferido` | `json.optLong("savedAt", 0L)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 334 | `val cachedPath` | `inferido` | `json.optString("cachedImagePath").takeIf { it.isNotBlank() && it != "null" }?.takeIf { File(it).isFi…` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Forma parte de la estrategia de reutilización de resultados para evitar repetir trabajo costoso. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 335 | `val imageCandidates` | `inferido` | `json.optJSONArray("imageCandidates")?.let { array -> buildList {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 327 | `return try {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 330 | `if (savedAt <= 0L \|\| System.currentTimeMillis() - savedAt > PREVIEW_CACHE_MAX_AGE_MS) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 332 | `return null` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 336 | `for (index in 0 until array.length()) {` | Iteración: repite el cuerpo para cada elemento de la colección/rango; el coste del bloque crece con el número de elementos recorridos. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 3.
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 1.
- **Filtro condicional:** `takeIf` conserva el valor únicamente cuando el predicado se cumple; en caso contrario lo convierte en `null`. Apariciones en este bloque: 3.
- **Normalización vacía:** `orEmpty` transforma una referencia nula en una colección/cadena vacía, simplificando el consumo posterior. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

- **Sistema de archivos/caché:** Accede o modifica almacenamiento/caché; son efectos externos al estado puramente en memoria.
- **Caché:** Lee, llena, invalida o administra caché para evitar recomputación o decodificación repetida.
- **Mutación de colección/estado compartido:** Modifica una colección/mapa/estructura existente; el cambio permanece visible para quienes compartan esa misma instancia.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `previewCacheKey`, `previewCachePreferences`, `getString`, `JSONObject`, `json.optLong`, `System.currentTimeMillis`, `edit`, `remove`, `apply`, `json.optString`, `it.isNotBlank`, `File`, `json.optJSONArray`, `array.length`, `array.optString`, `it.startsWith`, `let`, `orEmpty`, `LinkPreviewData`, `json.optNullableString`, `safeHost`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener coherencia entre creación, clave, lectura e invalidación de caché; cambiar solo una de esas etapas puede dejar datos obsoletos.

### 4.18 `JSONObject` — fun, líneas 350–353

```kotlin
private fun JSONObject.optNullableString(key: String): String? {
    if (isNull(key)) return null
    return optString(key).takeIf { it.isNotBlank() && it != "null" }
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `key: String` — `key` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable. Aunque el tipo sea `String`, semánticamente suele funcionar como clave/enumeración textual; las ramas del código delimitan los valores reconocidos.

**Retorno:** `String?`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 351 | `if (isNull(key)) return null` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 352 | `return optString(key).takeIf { it.isNotBlank() && it != "null" }` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

**Restricciones concretas que aparecen en este bloque:**
- **Filtro condicional:** `takeIf` conserva el valor únicamente cuando el predicado se cumple; en caso contrario lo convierte en `null`. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `optNullableString`, `isNull`, `optString`, `it.isNotBlank`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.19 `ensureThumbnailCached` — fun, líneas 355–383

```kotlin
private fun ensureThumbnailCached(context: Context, preview: LinkPreviewData): LinkPreviewData {
    preview.cachedImagePath?.let(::File)?.takeIf { it.isFile && it.length() > 0L && looksLikeImageFile(it) }?.let { file ->
            file.setLastModified(System.currentTimeMillis())
            return preview
        }
    val remoteCandidates = buildList {
            preview.imageUrl?.let(::add)
            addAll(preview.imageCandidates)
        }.asSequence().map { it.trim() }.filter {
                it.startsWith("http://", ignoreCase = true) || it.startsWith("https://", ignoreCase = true)
            }.distinct().take(12).toList()
    if (remoteCandidates.isEmpty()) {
        return preview.copy(cachedImagePath = null)
    }
    val cacheDir = File(context.filesDir, "link_preview_thumbnails_v6").apply { mkdirs() }
    remoteCandidates.forEach { remoteUrl -> val destination = File(cacheDir, sha256(remoteUrl) + ".img")
        if (destination.isFile && destination.length() > 0L && looksLikeImageFile(destination)) {
            destination.setLastModified(System.currentTimeMillis())
            return preview.copy(imageUrl = remoteUrl, cachedImagePath = destination.absolutePath)
        }
        destination.takeIf { it.exists() }?.delete()
        val downloaded = downloadThumbnail(imageUrl = remoteUrl, refererUrl = preview.url, destination = destination)
        if (downloaded) {
            pruneThumbnailCache(cacheDir)
            return preview.copy(imageUrl = remoteUrl, cachedImagePath = destination.absolutePath)
        }
    }
    return preview.copy(cachedImagePath = null)
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `context: Context` — `context` recibe un valor de tipo `Context`. El contrato no marca este parámetro como anulable. Da acceso a recursos y APIs Android; su vida útil debe corresponder con el uso realizado.
- `preview: LinkPreviewData` — `preview` recibe un valor de tipo `LinkPreviewData`. El contrato no marca este parámetro como anulable.

**Retorno:** `LinkPreviewData`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 360 | `val remoteCandidates` | `inferido` | `buildList {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 369 | `val cacheDir` | `inferido` | `File(context.filesDir, "link_preview_thumbnails_v6").apply { mkdirs() }` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Forma parte de la estrategia de reutilización de resultados para evitar repetir trabajo costoso. |
| 376 | `val downloaded` | `inferido` | `downloadThumbnail(imageUrl = remoteUrl, refererUrl = preview.url, destination = destination)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 358 | `return preview` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 366 | `if (remoteCandidates.isEmpty()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 367 | `return preview.copy(cachedImagePath = null)` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 371 | `if (destination.isFile && destination.length() > 0L && looksLikeImageFile(destination)) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 373 | `return preview.copy(imageUrl = remoteUrl, cachedImagePath = destination.absolutePath)` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 377 | `if (downloaded) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 379 | `return preview.copy(imageUrl = remoteUrl, cachedImagePath = destination.absolutePath)` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 382 | `return preview.copy(cachedImagePath = null)` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 5.
- **Filtro condicional:** `takeIf` conserva el valor únicamente cuando el predicado se cumple; en caso contrario lo convierte en `null`. Apariciones en este bloque: 2.

#### Efectos secundarios y recursos

- **Persistencia Room/DAO:** Realiza una operación de persistencia o modificación a través de una capa de datos/DAO.
- **Sistema de archivos/caché:** Accede o modifica almacenamiento/caché; son efectos externos al estado puramente en memoria.
- **Caché:** Lee, llena, invalida o administra caché para evitar recomputación o decodificación repetida.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `let`, `it.length`, `looksLikeImageFile`, `file.setLastModified`, `System.currentTimeMillis`, `addAll`, `asSequence`, `it.trim`, `it.startsWith`, `distinct`, `take`, `toList`, `remoteCandidates.isEmpty`, `preview.copy`, `File`, `mkdirs`, `sha256`, `destination.length`, `destination.setLastModified`, `it.exists`, `delete`, `downloadThumbnail`, `pruneThumbnailCache`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener coherencia entre creación, clave, lectura e invalidación de caché; cambiar solo una de esas etapas puede dejar datos obsoletos.

### 4.20 `downloadThumbnail` — fun, líneas 385–444

```kotlin
private fun downloadThumbnail(imageUrl: String, refererUrl: String, destination: File): Boolean {
    var connection: HttpURLConnection? = null
    val temp = File(destination.absolutePath + ".tmp")
    return try {
        connection = (URL(imageUrl).openConnection() as HttpURLConnection).apply {
                    instanceFollowRedirects = true
                    connectTimeout = 6_000
                    readTimeout = 8_000
                    requestMethod = "GET"
                    setRequestProperty("User-Agent", if (isDouyinRelatedUrl(refererUrl)) {
                            douyinUserAgent()
                        } else {
                            previewUserAgent()
                        })
                    // Android 9 / API 28 no decodifica AVIF de forma fiable.
                    // Pedimos formatos que Coil puede mostrar en ese dispositivo.
                    setRequestProperty("Accept", "image/webp,image/jpeg,image/png,image/*;q=0.9,*/*;q=0.5")
                    setRequestProperty("Referer", providerReferer(refererUrl))
                    setRequestProperty("Accept-Language", "es-MX,es;q=0.9,en;q=0.7")
                }
        connection.connect()
        if (connection.responseCode !in 200..299) {
            return false
        }
        val declaredLength = connection.contentLengthLong
        if (declaredLength > THUMBNAIL_MAX_BYTES) {
            return false
        }
        var total = 0L
        connection.inputStream.use { input -> FileOutputStream(temp).use { output -> val buffer = ByteArray(16_384)
                while (true) {
                    val count = input.read(buffer)
                    if (count <= 0) break
                    total += count
                    if (total > THUMBNAIL_MAX_BYTES) {
                        return false
                    }
                    output.write(buffer, 0, count)
                }
            }
        }
        if (total <= 0L || !looksLikeImageFile(temp)) {
            return false
        }
        if (destination.exists()) {
            destination.delete()
        }
        if (!temp.renameTo(destination)) {
            temp.copyTo(destination, overwrite = true)
            temp.delete()
        }
        destination.setLastModified(System.currentTimeMillis())
        true
    } catch (_: Exception) {
        false
    } finally {
        temp.takeIf { it.exists() }?.delete()
        connection?.disconnect()
    }
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `imageUrl: String` — `imageUrl` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.
- `refererUrl: String` — `refererUrl` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.
- `destination: File` — `destination` recibe un valor de tipo `File`. El contrato no marca este parámetro como anulable.

**Retorno:** `Boolean`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 386 | `var connection` | `HttpURLConnection?` | `null` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo declarado es `HttpURLConnection?`. Admite `null`, así que los consumidores deben contemplar ausencia de valor. |
| 387 | `val temp` | `inferido` | `File(destination.absolutePath + ".tmp")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 409 | `val declaredLength` | `inferido` | `connection.contentLengthLong` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 413 | `var total` | `inferido` | `0L` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 416 | `val count` | `inferido` | `input.read(buffer)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 388 | `return try {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 406 | `if (connection.responseCode !in 200..299) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 407 | `return false` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 410 | `if (declaredLength > THUMBNAIL_MAX_BYTES) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 411 | `return false` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 415 | `while (true) {` | Bucle condicionado: repite mientras la condición se mantenga verdadera; la lógica interna debe permitir progreso hacia la salida. |
| 417 | `if (count <= 0) break` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 419 | `if (total > THUMBNAIL_MAX_BYTES) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 420 | `return false` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 426 | `if (total <= 0L \|\| !looksLikeImageFile(temp)) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 427 | `return false` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 429 | `if (destination.exists()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 432 | `if (!temp.renameTo(destination)) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 2.
- **Filtro condicional:** `takeIf` conserva el valor únicamente cuando el predicado se cumple; en caso contrario lo convierte en `null`. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

- **Persistencia Room/DAO:** Realiza una operación de persistencia o modificación a través de una capa de datos/DAO.
- **Sistema de archivos/caché:** Accede o modifica almacenamiento/caché; son efectos externos al estado puramente en memoria.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `File`, `URL`, `openConnection`, `setRequestProperty`, `isDouyinRelatedUrl`, `douyinUserAgent`, `previewUserAgent`, `providerReferer`, `connection.connect`, `FileOutputStream`, `ByteArray`, `input.read`, `output.write`, `looksLikeImageFile`, `destination.exists`, `destination.delete`, `temp.renameTo`, `temp.copyTo`, `temp.delete`, `destination.setLastModified`, `System.currentTimeMillis`, `it.exists`, `delete`, `disconnect`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.21 `looksLikeImageFile` — fun, líneas 446–462

```kotlin
private fun looksLikeImageFile(file: File): Boolean {
    if (!file.isFile || file.length() < 12L) return false
    return try {
        val header = ByteArray(32)
        val count = file.inputStream().use { it.read(header) }
        if (count < 12) return false
        val jpeg = header[0].toInt() and 0xFF == 0xFF && header[1].toInt() and 0xFF == 0xD8 && header[2].toInt() and 0xFF == 0xFF
        val png = header.copyOfRange(0, 8).contentEquals(byteArrayOf(0x89.toByte(), 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A))
        val gif = String(header, 0, 6, Charsets.US_ASCII) == "GIF87a" || String(header, 0, 6, Charsets.US_ASCII) == "GIF89a"
        val webp = String(header, 0, 4, Charsets.US_ASCII) == "RIFF" && String(header, 8, 4, Charsets.US_ASCII) == "WEBP"
        val avif = count >= 12 && String(header, 4, 4, Charsets.US_ASCII) == "ftyp" && listOf("avif", "avis", "mif1", "heic", "heix")
                    .contains(String(header, 8, 4, Charsets.US_ASCII))
        jpeg || png || gif || webp || (avif && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
    } catch (_: Exception) {
        false
    }
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `file: File` — `file` recibe un valor de tipo `File`. El contrato no marca este parámetro como anulable.

**Retorno:** `Boolean`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 449 | `val header` | `inferido` | `ByteArray(32)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 450 | `val count` | `inferido` | `file.inputStream().use { it.read(header) }` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 452 | `val jpeg` | `inferido` | `header[0].toInt() and 0xFF == 0xFF && header[1].toInt() and 0xFF == 0xD8 && header[2].toInt() and 0x…` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 453 | `val png` | `inferido` | `header.copyOfRange(0, 8).contentEquals(byteArrayOf(0x89.toByte(), 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A…` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 454 | `val gif` | `inferido` | `String(header, 0, 6, Charsets.US_ASCII) == "GIF87a" \|\| String(header, 0, 6, Charsets.US_ASCII) == "G…` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 455 | `val webp` | `inferido` | `String(header, 0, 4, Charsets.US_ASCII) == "RIFF" && String(header, 8, 4, Charsets.US_ASCII) == "WEB…` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 456 | `val avif` | `inferido` | `count >= 12 && String(header, 4, 4, Charsets.US_ASCII) == "ftyp" && listOf("avif", "avis", "mif1", "…` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 447 | `if (!file.isFile \|\| file.length() < 12L) return false` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 448 | `return try {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 451 | `if (count < 12) return false` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |

**Restricciones concretas que aparecen en este bloque:**
- **Guardia de API:** Se condiciona una API según la versión de Android para no ejecutar llamadas incompatibles en dispositivos antiguos. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

- **Sistema de archivos/caché:** Accede o modifica almacenamiento/caché; son efectos externos al estado puramente en memoria.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `file.length`, `ByteArray`, `file.inputStream`, `it.read`, `toInt`, `header.copyOfRange`, `contentEquals`, `byteArrayOf`, `toByte`, `String`, `listOf`, `contains`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar las guardas de versión antes de APIs que no existen en Android antiguos.

### 4.22 `providerReferer` — fun, líneas 464–475

```kotlin
private fun providerReferer(url: String): String {
    val host = safeHost(url).lowercase(Locale.ROOT)
    return when {
        host.contains("douyin") || host.contains("iesdouyin") -> "https://www.douyin.com/"
        host.contains("tiktok") -> "https://www.tiktok.com/"
        host.contains("instagram") -> "https://www.instagram.com/"
        host.contains("threads") -> "https://www.threads.com/"
        host.contains("facebook") || host.contains("fb.com") -> "https://www.facebook.com/"
        host.contains("reddit") || host == "redd.it" -> "https://www.reddit.com/"
        else -> url
    }
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `url: String` — `url` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `String`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 465 | `val host` | `inferido` | `safeHost(url).lowercase(Locale.ROOT)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 466 | `return when {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 467 | `host.contains("douyin") \|\| host.contains("iesdouyin") -> "https://www.douyin.com/"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 468 | `host.contains("tiktok") -> "https://www.tiktok.com/"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 469 | `host.contains("instagram") -> "https://www.instagram.com/"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 470 | `host.contains("threads") -> "https://www.threads.com/"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 471 | `host.contains("facebook") \|\| host.contains("fb.com") -> "https://www.facebook.com/"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 472 | `host.contains("reddit") \|\| host == "redd.it" -> "https://www.reddit.com/"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 473 | `else -> url` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `safeHost`, `lowercase`, `host.contains`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.

### 4.23 `pruneThumbnailCache` — fun, líneas 477–486

```kotlin
private fun pruneThumbnailCache(directory: File) {
    val files = directory.listFiles()?.filter { it.isFile && !it.name.endsWith(".tmp") }?.sortedByDescending { it.lastModified() }
            .orEmpty()
    var totalBytes = files.sumOf { it.length() }
    files.forEachIndexed { index, file -> if (index >= THUMBNAIL_CACHE_MAX_FILES || totalBytes > THUMBNAIL_CACHE_MAX_BYTES) {
            totalBytes -= file.length()
            file.delete()
        }
    }
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `directory: File` — `directory` recibe un valor de tipo `File`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 478 | `val files` | `inferido` | `directory.listFiles()?.filter { it.isFile && !it.name.endsWith(".tmp") }?.sortedByDescending { it.la…` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 480 | `var totalBytes` | `inferido` | `files.sumOf { it.length() }` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 2.
- **Normalización vacía:** `orEmpty` transforma una referencia nula en una colección/cadena vacía, simplificando el consumo posterior. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

- **Persistencia Room/DAO:** Realiza una operación de persistencia o modificación a través de una capa de datos/DAO.
- **Sistema de archivos/caché:** Accede o modifica almacenamiento/caché; son efectos externos al estado puramente en memoria.
- **Caché:** Lee, llena, invalida o administra caché para evitar recomputación o decodificación repetida.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `directory.listFiles`, `it.name.endsWith`, `it.lastModified`, `orEmpty`, `it.length`, `file.length`, `file.delete`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener coherencia entre creación, clave, lectura e invalidación de caché; cambiar solo una de esas etapas puede dejar datos obsoletos.

### 4.24 `sha256` — fun, líneas 488–492

```kotlin
private fun sha256(value: String): String {
    val digest = MessageDigest.getInstance("SHA-256").digest(value.toByteArray(Charsets.UTF_8))
    return digest.joinToString("") { byte -> "%02x".format(byte)
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

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 489 | `val digest` | `inferido` | `MessageDigest.getInstance("SHA-256").digest(value.toByteArray(Charsets.UTF_8))` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 490 | `return digest.joinToString("") { byte -> "%02x".format(byte)` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `MessageDigest.getInstance`, `digest`, `value.toByteArray`, `digest.joinToString`, `format`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.25 `parseHtml` — fun, líneas 494–526

```kotlin
private fun parseHtml(pageUrl: String, html: String): LinkPreviewData {
    val metadata = linkedMapOf<String, String>()
    val metaTagRegex = Regex(
            pattern = """<meta\b[^>]*>""",
            option = RegexOption.IGNORE_CASE)
    val attributeRegex = Regex(
            pattern = """([A-Za-z_:][A-Za-z0-9_:\-.]*)\s*=\s*(?:\"([^\"]*)\"|'([^']*)'|([^\s>]+))"""
        )
    metaTagRegex.findAll(html).forEach { tagMatch -> val attributes = attributeRegex.findAll(tagMatch.value).associate { attr -> val key =
                            attr.groupValues[1].lowercase(Locale.ROOT)
                        val value = attr.groupValues.drop(2).firstOrNull { it.isNotEmpty() }.orEmpty()
                        key to value
                    }
            val key = attributes["property"]?: attributes["name"]
            val content = attributes["content"]
            if (!key.isNullOrBlank() && !content.isNullOrBlank()) {
                metadata.putIfAbsent(key.lowercase(Locale.ROOT), decodeHtml(content))
            }
        }
    val titleFromTag = Regex(
            pattern = """<title\b[^>]*>(.*?)</title>""",
            options = setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL)).find(html)?.groupValues?.getOrNull(1)?.let(::decodeHtml)
            ?.takeIf { it.isNotBlank() }
    val host = safeHost(pageUrl)
    val rawImage = firstNonBlank(metadata["og:image"], metadata["og:image:secure_url"], metadata["twitter:image"],
            metadata["twitter:image:src"])
    val resolvedImage = rawImage?.let { resolveUrl(pageUrl, it) }
    val title = firstNonBlank(metadata["og:title"], metadata["twitter:title"], titleFromTag)?.cleanText()
    val description = firstNonBlank(metadata["og:description"], metadata["twitter:description"], metadata["description"])?.cleanText()
    val siteName = firstNonBlank(metadata["og:site_name"], metadata["application-name"], host)?.cleanText()
    return LinkPreviewData(url = pageUrl, title = title, description = description, imageUrl = resolvedImage, siteName = siteName,
        host = host)
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `pageUrl: String` — `pageUrl` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.
- `html: String` — `html` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `LinkPreviewData`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 495 | `val metadata` | `inferido` | `linkedMapOf<String, String>()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 496 | `val metaTagRegex` | `inferido` | `Regex(` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 499 | `val attributeRegex` | `inferido` | `Regex(` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 504 | `val value` | `inferido` | `attr.groupValues.drop(2).firstOrNull { it.isNotEmpty() }.orEmpty()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Normaliza una ausencia hacia un valor vacío/alternativo para simplificar el código consumidor. |
| 507 | `val key` | `inferido` | `attributes["property"]?: attributes["name"]` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 508 | `val content` | `inferido` | `attributes["content"]` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 513 | `val titleFromTag` | `inferido` | `Regex(` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 517 | `val host` | `inferido` | `safeHost(pageUrl)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 518 | `val rawImage` | `inferido` | `firstNonBlank(metadata["og:image"], metadata["og:image:secure_url"], metadata["twitter:image"],` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 520 | `val resolvedImage` | `inferido` | `rawImage?.let { resolveUrl(pageUrl, it) }` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 521 | `val title` | `inferido` | `firstNonBlank(metadata["og:title"], metadata["twitter:title"], titleFromTag)?.cleanText()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 522 | `val description` | `inferido` | `firstNonBlank(metadata["og:description"], metadata["twitter:description"], metadata["description"])?…` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 523 | `val siteName` | `inferido` | `firstNonBlank(metadata["og:site_name"], metadata["application-name"], host)?.cleanText()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 509 | `if (!key.isNullOrBlank() && !content.isNullOrBlank()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 524 | `return LinkPreviewData(url = pageUrl, title = title, description = description, imageUrl = resolvedImage, siteName = siteName,` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 8.
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 2.
- **Filtro condicional:** `takeIf` conserva el valor únicamente cuando el predicado se cumple; en caso contrario lo convierte en `null`. Apariciones en este bloque: 1.
- **Normalización vacía:** `orEmpty` transforma una referencia nula en una colección/cadena vacía, simplificando el consumo posterior. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `Regex`, `metaTagRegex.findAll`, `attributeRegex.findAll`, `lowercase`, `attr.groupValues.drop`, `it.isNotEmpty`, `orEmpty`, `key.isNullOrBlank`, `content.isNullOrBlank`, `metadata.putIfAbsent`, `key.lowercase`, `decodeHtml`, `setOf`, `find`, `getOrNull`, `let`, `it.isNotBlank`, `safeHost`, `firstNonBlank`, `resolveUrl`, `cleanText`, `LinkPreviewData`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.26 `isDouyinRelatedUrl` — fun, líneas 535–542

```kotlin
private fun isDouyinRelatedUrl(url: String): Boolean {
    val host = try {
            Uri.parse(url).host?.lowercase(Locale.ROOT).orEmpty()
        } catch (_: Exception) {
            ""
        }
    return host == "douyin.com" || host.endsWith(".douyin.com") || host == "iesdouyin.com" || host.endsWith(".iesdouyin.com")
}
```

#### Qué hace y por qué existe

Evalúa una condición y devuelve/representa una decisión booleana utilizada por otras ramas del flujo.

#### Contrato de la declaración

**Parámetros:**

- `url: String` — `url` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `Boolean`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 536 | `val host` | `inferido` | `try {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 541 | `return host == "douyin.com" \|\| host.endsWith(".douyin.com") \|\| host == "iesdouyin.com" \|\| host.endsWith(".iesdouyin.com")` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 1.
- **Normalización vacía:** `orEmpty` transforma una referencia nula en una colección/cadena vacía, simplificando el consumo posterior. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `Uri.parse`, `lowercase`, `orEmpty`, `host.endsWith`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.27 `resolveDouyinLink` — fun, líneas 544–611

```kotlin
private data class ResolvedDouyinLink(val finalUrl: String, val awemeId: String?, val cookieHeader: String?)

/**
 * Resuelve v.douyin.com manualmente para conservar el UA móvil y las cookies
 * de la cadena de redirecciones. Esto evita depender de la página genérica
 * que Douyin entrega a algunos clientes Android.
 */
private fun resolveDouyinLink(url: String): ResolvedDouyinLink? {
    var current = url
    val cookies = linkedMapOf<String, String>()
    repeat(8) {
        var connection: HttpURLConnection? = null
        try {
            connection = (URL(current).openConnection() as HttpURLConnection).apply {
                        instanceFollowRedirects = false
                        connectTimeout = 5_000
                        readTimeout = 6_000
                        requestMethod = "GET"
                        setRequestProperty("User-Agent", douyinUserAgent())
                        setRequestProperty("Accept", "text/html,application/xhtml+xml,*/*;q=0.8")
                        setRequestProperty("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.7")
                        if (cookies.isNotEmpty()) {
                            setRequestProperty("Cookie", cookies.entries.joinToString("; ") {
                                    "${it.key}=${it.value}"
                                })
                        }
                    }
            connection.connect()
            connection.headerFields.filterKeys { key -> key?.equals("Set-Cookie", ignoreCase = true) == true
                }.values.flatten().forEach { rawCookie -> val first = rawCookie.substringBefore(';').trim()
                    val name = first.substringBefore('=', "").trim()
                    val value = first.substringAfter('=', "").trim()
                    if (name.isNotBlank()) {
                        cookies[name] = value
                    }
                }
            val code = connection.responseCode
            if (code in 300..399) {
                val location = connection.getHeaderField("Location")?.takeIf { it.isNotBlank() }?: return null
                current = URL(URL(current), location).toString()
                extractDouyinAwemeId(pageUrl = current, html = "")?.let { id -> return ResolvedDouyinLink(finalUrl = current, awemeId = id,
                        cookieHeader = cookies.entries.joinToString("; ") {
                            "${it.key}=${it.value}"
                        }.takeIf { it.isNotBlank() })
                }
                return@repeat
            }
            if (code !in 200..299) {
                return null
            }
            val html = connection.inputStream.use { input -> readLimitedHtml(InputStreamReader(input,
                            charsetFromContentType(connection.contentType)))
                }
            return ResolvedDouyinLink(finalUrl = current, awemeId = extractDouyinAwemeId(current, html),
                cookieHeader = cookies.entries.joinToString("; ") {
                    "${it.key}=${it.value}"
                }.takeIf { it.isNotBlank() })
        } catch (_: Exception) {
            return null
        } finally {
            connection?.disconnect()
        }
    }
    return ResolvedDouyinLink(finalUrl = current, awemeId = extractDouyinAwemeId(current, ""),
        cookieHeader = cookies.entries.joinToString("; ") {
            "${it.key}=${it.value}"
        }.takeIf { it.isNotBlank() })
}
```

#### Qué hace y por qué existe

Resuelve un valor final a partir del contexto y las reglas de prioridad/fallback definidas en el cuerpo.

#### Contrato de la declaración

**Parámetros:**

- `val finalUrl: String` — No se pudo separar automáticamente nombre/tipo; se conserva la firma exacta como contrato.
- `val awemeId: String?` — No se pudo separar automáticamente nombre/tipo; se conserva la firma exacta como contrato.
- `val cookieHeader: String?` — No se pudo separar automáticamente nombre/tipo; se conserva la firma exacta como contrato.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 552 | `var current` | `inferido` | `url` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 553 | `val cookies` | `inferido` | `linkedMapOf<String, String>()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 555 | `var connection` | `HttpURLConnection?` | `null` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo declarado es `HttpURLConnection?`. Admite `null`, así que los consumidores deben contemplar ausencia de valor. |
| 574 | `val name` | `inferido` | `first.substringBefore('=', "").trim()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 575 | `val value` | `inferido` | `first.substringAfter('=', "").trim()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 580 | `val code` | `inferido` | `connection.responseCode` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 582 | `val location` | `inferido` | `connection.getHeaderField("Location")?.takeIf { it.isNotBlank() }?: return null` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 594 | `val html` | `inferido` | `connection.inputStream.use { input -> readLimitedHtml(InputStreamReader(input,` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 556 | `try {` | Frontera de manejo de errores: agrupa operaciones que pueden lanzar excepciones para permitir recuperación o limpieza controlada. |
| 565 | `if (cookies.isNotEmpty()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 576 | `if (name.isNotBlank()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 581 | `if (code in 300..399) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 589 | `return@repeat` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 591 | `if (code !in 200..299) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 592 | `return null` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 597 | `return ResolvedDouyinLink(finalUrl = current, awemeId = extractDouyinAwemeId(current, html),` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 602 | `return null` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 607 | `return ResolvedDouyinLink(finalUrl = current, awemeId = extractDouyinAwemeId(current, ""),` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 4.
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 1.
- **Filtro condicional:** `takeIf` conserva el valor únicamente cuando el predicado se cumple; en caso contrario lo convierte en `null`. Apariciones en este bloque: 4.

#### Efectos secundarios y recursos

- **Mutación de colección/estado compartido:** Modifica una colección/mapa/estructura existente; el cambio permanece visible para quienes compartan esa misma instancia.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `repeat`, `URL`, `openConnection`, `setRequestProperty`, `douyinUserAgent`, `cookies.isNotEmpty`, `cookies.entries.joinToString`, `connection.connect`, `equals`, `values.flatten`, `rawCookie.substringBefore`, `trim`, `first.substringBefore`, `first.substringAfter`, `name.isNotBlank`, `connection.getHeaderField`, `it.isNotBlank`, `toString`, `extractDouyinAwemeId`, `ResolvedDouyinLink`, `readLimitedHtml`, `InputStreamReader`, `charsetFromContentType`, `disconnect`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.28 `resolveDouyinLink` — fun, líneas 551–611

```kotlin
private fun resolveDouyinLink(url: String): ResolvedDouyinLink? {
    var current = url
    val cookies = linkedMapOf<String, String>()
    repeat(8) {
        var connection: HttpURLConnection? = null
        try {
            connection = (URL(current).openConnection() as HttpURLConnection).apply {
                        instanceFollowRedirects = false
                        connectTimeout = 5_000
                        readTimeout = 6_000
                        requestMethod = "GET"
                        setRequestProperty("User-Agent", douyinUserAgent())
                        setRequestProperty("Accept", "text/html,application/xhtml+xml,*/*;q=0.8")
                        setRequestProperty("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.7")
                        if (cookies.isNotEmpty()) {
                            setRequestProperty("Cookie", cookies.entries.joinToString("; ") {
                                    "${it.key}=${it.value}"
                                })
                        }
                    }
            connection.connect()
            connection.headerFields.filterKeys { key -> key?.equals("Set-Cookie", ignoreCase = true) == true
                }.values.flatten().forEach { rawCookie -> val first = rawCookie.substringBefore(';').trim()
                    val name = first.substringBefore('=', "").trim()
                    val value = first.substringAfter('=', "").trim()
                    if (name.isNotBlank()) {
                        cookies[name] = value
                    }
                }
            val code = connection.responseCode
            if (code in 300..399) {
                val location = connection.getHeaderField("Location")?.takeIf { it.isNotBlank() }?: return null
                current = URL(URL(current), location).toString()
                extractDouyinAwemeId(pageUrl = current, html = "")?.let { id -> return ResolvedDouyinLink(finalUrl = current, awemeId = id,
                        cookieHeader = cookies.entries.joinToString("; ") {
                            "${it.key}=${it.value}"
                        }.takeIf { it.isNotBlank() })
                }
                return@repeat
            }
            if (code !in 200..299) {
                return null
            }
            val html = connection.inputStream.use { input -> readLimitedHtml(InputStreamReader(input,
                            charsetFromContentType(connection.contentType)))
                }
            return ResolvedDouyinLink(finalUrl = current, awemeId = extractDouyinAwemeId(current, html),
                cookieHeader = cookies.entries.joinToString("; ") {
                    "${it.key}=${it.value}"
                }.takeIf { it.isNotBlank() })
        } catch (_: Exception) {
            return null
        } finally {
            connection?.disconnect()
        }
    }
    return ResolvedDouyinLink(finalUrl = current, awemeId = extractDouyinAwemeId(current, ""),
        cookieHeader = cookies.entries.joinToString("; ") {
            "${it.key}=${it.value}"
        }.takeIf { it.isNotBlank() })
}
```

#### Qué hace y por qué existe

Resuelve un valor final a partir del contexto y las reglas de prioridad/fallback definidas en el cuerpo.

#### Contrato de la declaración

**Parámetros:**

- `url: String` — `url` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `ResolvedDouyinLink?`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 552 | `var current` | `inferido` | `url` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 553 | `val cookies` | `inferido` | `linkedMapOf<String, String>()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 555 | `var connection` | `HttpURLConnection?` | `null` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo declarado es `HttpURLConnection?`. Admite `null`, así que los consumidores deben contemplar ausencia de valor. |
| 574 | `val name` | `inferido` | `first.substringBefore('=', "").trim()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 575 | `val value` | `inferido` | `first.substringAfter('=', "").trim()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 580 | `val code` | `inferido` | `connection.responseCode` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 582 | `val location` | `inferido` | `connection.getHeaderField("Location")?.takeIf { it.isNotBlank() }?: return null` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 594 | `val html` | `inferido` | `connection.inputStream.use { input -> readLimitedHtml(InputStreamReader(input,` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 556 | `try {` | Frontera de manejo de errores: agrupa operaciones que pueden lanzar excepciones para permitir recuperación o limpieza controlada. |
| 565 | `if (cookies.isNotEmpty()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 576 | `if (name.isNotBlank()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 581 | `if (code in 300..399) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 589 | `return@repeat` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 591 | `if (code !in 200..299) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 592 | `return null` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 597 | `return ResolvedDouyinLink(finalUrl = current, awemeId = extractDouyinAwemeId(current, html),` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 602 | `return null` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 607 | `return ResolvedDouyinLink(finalUrl = current, awemeId = extractDouyinAwemeId(current, ""),` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 4.
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 1.
- **Filtro condicional:** `takeIf` conserva el valor únicamente cuando el predicado se cumple; en caso contrario lo convierte en `null`. Apariciones en este bloque: 4.

#### Efectos secundarios y recursos

- **Mutación de colección/estado compartido:** Modifica una colección/mapa/estructura existente; el cambio permanece visible para quienes compartan esa misma instancia.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `repeat`, `URL`, `openConnection`, `setRequestProperty`, `douyinUserAgent`, `cookies.isNotEmpty`, `cookies.entries.joinToString`, `connection.connect`, `equals`, `values.flatten`, `rawCookie.substringBefore`, `trim`, `first.substringBefore`, `first.substringAfter`, `name.isNotBlank`, `connection.getHeaderField`, `it.isNotBlank`, `toString`, `extractDouyinAwemeId`, `ResolvedDouyinLink`, `readLimitedHtml`, `InputStreamReader`, `charsetFromContentType`, `disconnect`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.29 `fetchDouyinDirectPreview` — fun, líneas 613–622

```kotlin
private fun fetchDouyinDirectPreview(url: String): LinkPreviewData? {
    val resolved = resolveDouyinLink(url) ?: return null
    val awemeId = resolved.awemeId ?: return null
    val share = fetchDouyinSharePagePreview(awemeId = awemeId, clickUrl = url, cookieHeader = resolved.cookieHeader)
    if (share != null && share.imageCandidates.isNotEmpty()) {
        return share
    }
    val api = fetchDouyinItemInfo(awemeId = awemeId, clickUrl = url, cookieHeader = resolved.cookieHeader)
    return api ?: share
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `url: String` — `url` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `LinkPreviewData?`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 614 | `val resolved` | `inferido` | `resolveDouyinLink(url) ?: return null` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 615 | `val awemeId` | `inferido` | `resolved.awemeId ?: return null` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 616 | `val share` | `inferido` | `fetchDouyinSharePagePreview(awemeId = awemeId, clickUrl = url, cookieHeader = resolved.cookieHeader)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 620 | `val api` | `inferido` | `fetchDouyinItemInfo(awemeId = awemeId, clickUrl = url, cookieHeader = resolved.cookieHeader)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 617 | `if (share != null && share.imageCandidates.isNotEmpty()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 618 | `return share` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 621 | `return api ?: share` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

**Restricciones concretas que aparecen en este bloque:**
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 3.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `resolveDouyinLink`, `fetchDouyinSharePagePreview`, `share.imageCandidates.isNotEmpty`, `fetchDouyinItemInfo`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.30 `enrichDouyinPreview` — fun, líneas 624–644

```kotlin
private fun enrichDouyinPreview(originalUrl: String, pageUrl: String, html: String, base: LinkPreviewData): LinkPreviewData {
    val awemeId = extractDouyinAwemeId(pageUrl = pageUrl, html = html)
    val sharePreview = awemeId?.let { id -> fetchDouyinSharePagePreview(awemeId = id, clickUrl = originalUrl)
        }
    val apiPreview = if (awemeId != null && (sharePreview == null || sharePreview.imageCandidates.isEmpty())) {
            fetchDouyinItemInfo(awemeId = awemeId, clickUrl = originalUrl)
        } else {
            null
        }
    val embeddedCover = base.imageUrl?.takeIf { it.isNotBlank() }?: extractDouyinCoverUrl(pageUrl = pageUrl, html = html)
    val candidates = buildList {
            sharePreview?.imageUrl?.let(::add)
            addAll(sharePreview?.imageCandidates.orEmpty())
            apiPreview?.imageUrl?.let(::add)
            addAll(apiPreview?.imageCandidates.orEmpty())
            embeddedCover?.let(::add)
        }.filter { it.startsWith("http", ignoreCase = true) }.distinct()
    return base.copy(url = originalUrl, title = base.title?.takeIf { it.isNotBlank() }?: sharePreview?.title?: apiPreview?.title,
            description = base.description?.takeIf { it.isNotBlank() }?: sharePreview?.description?: apiPreview?.description,
            imageUrl = candidates.firstOrNull(), imageCandidates = candidates, siteName = "Douyin").withKnownProviderFallback()
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `originalUrl: String` — `originalUrl` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.
- `pageUrl: String` — `pageUrl` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.
- `html: String` — `html` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.
- `base: LinkPreviewData` — `base` recibe un valor de tipo `LinkPreviewData`. El contrato no marca este parámetro como anulable.

**Retorno:** `LinkPreviewData`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 625 | `val awemeId` | `inferido` | `extractDouyinAwemeId(pageUrl = pageUrl, html = html)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 626 | `val sharePreview` | `inferido` | `awemeId?.let { id -> fetchDouyinSharePagePreview(awemeId = id, clickUrl = originalUrl)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 628 | `val apiPreview` | `inferido` | `if (awemeId != null && (sharePreview == null \|\| sharePreview.imageCandidates.isEmpty())) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 633 | `val embeddedCover` | `inferido` | `base.imageUrl?.takeIf { it.isNotBlank() }?: extractDouyinCoverUrl(pageUrl = pageUrl, html = html)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 634 | `val candidates` | `inferido` | `buildList {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 641 | `return base.copy(url = originalUrl, title = base.title?.takeIf { it.isNotBlank() }?: sharePreview?.title?: apiPreview?.title,` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 15.
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 5.
- **Filtro condicional:** `takeIf` conserva el valor únicamente cuando el predicado se cumple; en caso contrario lo convierte en `null`. Apariciones en este bloque: 3.
- **Normalización vacía:** `orEmpty` transforma una referencia nula en una colección/cadena vacía, simplificando el consumo posterior. Apariciones en este bloque: 2.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `extractDouyinAwemeId`, `fetchDouyinSharePagePreview`, `sharePreview.imageCandidates.isEmpty`, `fetchDouyinItemInfo`, `it.isNotBlank`, `extractDouyinCoverUrl`, `let`, `addAll`, `imageCandidates.orEmpty`, `it.startsWith`, `distinct`, `base.copy`, `candidates.firstOrNull`, `withKnownProviderFallback`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.31 `extractDouyinAwemeId` — fun, líneas 646–660

```kotlin
private fun extractDouyinAwemeId(pageUrl: String, html: String): String? {
    val sources = listOf(pageUrl, decodeDouyinEmbeddedJson(html))
    val patterns = listOf(Regex(
                pattern = """/(?:share/)?(?:video|note|slides)/(\d{10,})""",
                option = RegexOption.IGNORE_CASE), Regex(
                pattern = """(?:modal_id|aweme_id|awemeId|itemId)[=:%\"']+(\d{10,})""",
                option = RegexOption.IGNORE_CASE), Regex(
                pattern = """[\"'](?:aweme_id|awemeId|itemId)[\"']\s*[:=]\s*[\"']?(\d{10,})""",
                option = RegexOption.IGNORE_CASE))
    sources.forEach { source -> patterns.forEach { regex -> regex.find(source)?.groupValues?.getOrNull(1)?.takeIf { it.isNotBlank() }
                ?.let { return it }
        }
    }
    return null
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `pageUrl: String` — `pageUrl` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.
- `html: String` — `html` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `String?`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 647 | `val sources` | `inferido` | `listOf(pageUrl, decodeDouyinEmbeddedJson(html))` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 648 | `val patterns` | `inferido` | `listOf(Regex(` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 659 | `return null` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 4.
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 4.
- **Filtro condicional:** `takeIf` conserva el valor únicamente cuando el predicado se cumple; en caso contrario lo convierte en `null`. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `listOf`, `decodeDouyinEmbeddedJson`, `Regex`, `regex.find`, `getOrNull`, `it.isNotBlank`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.32 `fetchDouyinSharePagePreview` — fun, líneas 662–696

```kotlin
private fun fetchDouyinSharePagePreview(awemeId: String, clickUrl: String, cookieHeader: String? = null): LinkPreviewData? {
    val paths = listOf("share/video", "share/slides", "share/note")
    paths.forEach { path -> val shareUrl = "https://www.iesdouyin.com/$path/$awemeId/"
        var connection: HttpURLConnection? = null
        try {
            connection = (URL(shareUrl).openConnection() as HttpURLConnection).apply {
                        instanceFollowRedirects = true
                        connectTimeout = 6_000
                        readTimeout = 8_000
                        requestMethod = "GET"
                        setRequestProperty("User-Agent", douyinUserAgent())
                        setRequestProperty("Accept", "text/html,application/xhtml+xml,*/*;q=0.8")
                        setRequestProperty("Referer", "https://www.douyin.com/")
                        setRequestProperty("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.7")
                        cookieHeader?.takeIf { it.isNotBlank() }?.let { setRequestProperty("Cookie", it) }
                    }
            connection.connect()
            if (connection.responseCode !in 200..399) return@forEach
            val charset = charsetFromContentType(connection.contentType)
            val html = connection.inputStream.use { input -> readLimitedHtml(InputStreamReader(input, charset))
                }
            val router = extractDouyinRouterData(html) ?: return@forEach
            val item = findDouyinItem(router) ?: return@forEach
            val preview = douyinPreviewFromItem(item, clickUrl)
            if (preview.imageCandidates.isNotEmpty() || !preview.title.isNullOrBlank()) {
                return preview
            }
        } catch (_: Exception) {
            // Probar la siguiente variante (video/slides/note).
        } finally {
            connection?.disconnect()
        }
    }
    return null
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `awemeId: String` — `awemeId` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.
- `clickUrl: String` — `clickUrl` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.
- `cookieHeader: String? = null` — `cookieHeader` recibe un valor de tipo `String?`. El contrato permite `null`; el bloque debe decidir explícita o implícitamente qué significa la ausencia. Tiene valor por defecto `null`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada.

**Retorno:** `LinkPreviewData?`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 663 | `val paths` | `inferido` | `listOf("share/video", "share/slides", "share/note")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 665 | `var connection` | `HttpURLConnection?` | `null` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo declarado es `HttpURLConnection?`. Admite `null`, así que los consumidores deben contemplar ausencia de valor. |
| 680 | `val charset` | `inferido` | `charsetFromContentType(connection.contentType)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 681 | `val html` | `inferido` | `connection.inputStream.use { input -> readLimitedHtml(InputStreamReader(input, charset))` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 683 | `val router` | `inferido` | `extractDouyinRouterData(html) ?: return@forEach` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 684 | `val item` | `inferido` | `findDouyinItem(router) ?: return@forEach` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 685 | `val preview` | `inferido` | `douyinPreviewFromItem(item, clickUrl)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 666 | `try {` | Frontera de manejo de errores: agrupa operaciones que pueden lanzar excepciones para permitir recuperación o limpieza controlada. |
| 679 | `if (connection.responseCode !in 200..399) return@forEach` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 686 | `if (preview.imageCandidates.isNotEmpty() \|\| !preview.title.isNullOrBlank()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 687 | `return preview` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 695 | `return null` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 3.
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 2.
- **Filtro condicional:** `takeIf` conserva el valor únicamente cuando el predicado se cumple; en caso contrario lo convierte en `null`. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `listOf`, `URL`, `openConnection`, `setRequestProperty`, `douyinUserAgent`, `it.isNotBlank`, `connection.connect`, `charsetFromContentType`, `readLimitedHtml`, `InputStreamReader`, `extractDouyinRouterData`, `findDouyinItem`, `douyinPreviewFromItem`, `preview.imageCandidates.isNotEmpty`, `preview.title.isNullOrBlank`, `disconnect`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.33 `extractDouyinRouterData` — fun, líneas 698–723

```kotlin
private fun extractDouyinRouterData(html: String): JSONObject? {
    val markerIndex = html.indexOf("_ROUTER_DATA")
    if (markerIndex < 0) return null
    val equalsIndex = html.indexOf('=', markerIndex)
    if (equalsIndex < 0) return null
    var index = equalsIndex + 1
    while (index < html.length && html[index].isWhitespace()) index++
    if (index >= html.length) return null
    return try {
        when (html[index]) {
            '{' -> {
                val raw = scanBalancedJsonObject(html, index) ?: return null
                JSONObject(raw)
            }
            '"' -> {
                val literal = scanJsonStringLiteral(html, index) ?: return null
                val wrapped = JSONObject("{\"value\":$literal}")
                val inner = wrapped.getString("value")
                JSONObject(decodeDouyinEmbeddedJson(inner))
            }
            else -> null
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

- `html: String` — `html` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `JSONObject?`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 699 | `val markerIndex` | `inferido` | `html.indexOf("_ROUTER_DATA")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 701 | `val equalsIndex` | `inferido` | `html.indexOf('=', markerIndex)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 703 | `var index` | `inferido` | `equalsIndex + 1` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 709 | `val raw` | `inferido` | `scanBalancedJsonObject(html, index) ?: return null` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 713 | `val literal` | `inferido` | `scanJsonStringLiteral(html, index) ?: return null` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 714 | `val wrapped` | `inferido` | `JSONObject("{\"value\":$literal}")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 715 | `val inner` | `inferido` | `wrapped.getString("value")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 700 | `if (markerIndex < 0) return null` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 702 | `if (equalsIndex < 0) return null` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 704 | `while (index < html.length && html[index].isWhitespace()) index++` | Bucle condicionado: repite mientras la condición se mantenga verdadera; la lógica interna debe permitir progreso hacia la salida. |
| 705 | `if (index >= html.length) return null` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 706 | `return try {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 707 | `when (html[index]) {` | Selección múltiple de Kotlin: concentra casos mutuamente evaluados y normalmente define una normalización o estrategia según el valor de entrada. |
| 712 | `'"' -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 718 | `else -> null` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |

**Restricciones concretas que aparecen en este bloque:**
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 2.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `html.indexOf`, `isWhitespace`, `scanBalancedJsonObject`, `JSONObject`, `scanJsonStringLiteral`, `wrapped.getString`, `decodeDouyinEmbeddedJson`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.

### 4.34 `scanBalancedJsonObject` — fun, líneas 725–752

```kotlin
private fun scanBalancedJsonObject(text: String, start: Int): String? {
    if (start !in text.indices || text[start] != '{') return null
    var depth = 0
    var inString = false
    var escaped = false
    for (index in start until text.length) {
        val char = text[index]
        if (inString) {
            when {
                escaped -> escaped = false
                char == '\\' -> escaped = true
                char == '"' -> inString = false
            }
        } else {
            when (char) {
                '"' -> inString = true
                '{' -> depth++
                '}' -> {
                    depth--
                    if (depth == 0) {
                        return text.substring(start, index + 1)
                    }
                }
            }
        }
    }
    return null
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `text: String` — `text` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.
- `start: Int` — `start` recibe un valor de tipo `Int`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.

**Retorno:** `String?`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 727 | `var depth` | `inferido` | `0` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 728 | `var inString` | `inferido` | `false` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 729 | `var escaped` | `inferido` | `false` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 731 | `val char` | `inferido` | `text[index]` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 726 | `if (start !in text.indices \|\| text[start] != '{') return null` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 730 | `for (index in start until text.length) {` | Iteración: repite el cuerpo para cada elemento de la colección/rango; el coste del bloque crece con el número de elementos recorridos. |
| 732 | `if (inString) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 733 | `when {` | Selección múltiple de Kotlin: concentra casos mutuamente evaluados y normalmente define una normalización o estrategia según el valor de entrada. |
| 734 | `escaped -> escaped = false` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 735 | `char == '\\' -> escaped = true` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 736 | `char == '"' -> inString = false` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 739 | `when (char) {` | Selección múltiple de Kotlin: concentra casos mutuamente evaluados y normalmente define una normalización o estrategia según el valor de entrada. |
| 740 | `'"' -> inString = true` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 742 | `'}' -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 744 | `if (depth == 0) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 745 | `return text.substring(start, index + 1)` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 751 | `return null` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `text.substring`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.

### 4.35 `scanJsonStringLiteral` — fun, líneas 754–766

```kotlin
private fun scanJsonStringLiteral(text: String, start: Int): String? {
    if (start !in text.indices || text[start] != '"') return null
    var escaped = false
    for (index in start + 1 until text.length) {
        val char = text[index]
        when {
            escaped -> escaped = false
            char == '\\' -> escaped = true
            char == '"' -> return text.substring(start, index + 1)
        }
    }
    return null
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `text: String` — `text` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.
- `start: Int` — `start` recibe un valor de tipo `Int`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.

**Retorno:** `String?`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 756 | `var escaped` | `inferido` | `false` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 758 | `val char` | `inferido` | `text[index]` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 755 | `if (start !in text.indices \|\| text[start] != '"') return null` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 757 | `for (index in start + 1 until text.length) {` | Iteración: repite el cuerpo para cada elemento de la colección/rango; el coste del bloque crece con el número de elementos recorridos. |
| 759 | `when {` | Selección múltiple de Kotlin: concentra casos mutuamente evaluados y normalmente define una normalización o estrategia según el valor de entrada. |
| 760 | `escaped -> escaped = false` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 761 | `char == '\\' -> escaped = true` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 762 | `char == '"' -> return text.substring(start, index + 1)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 765 | `return null` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `text.substring`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.

### 4.36 `findDouyinItem` — fun, líneas 768–795

```kotlin
private fun findDouyinItem(node: Any?, depth: Int = 0): JSONObject? {
    if (node == null || node == JSONObject.NULL || depth > 28) return null
    when (node) {
        is JSONObject -> {
            for (key in listOf("item_list", "aweme_list", "aweme_detail", "aweme")) {
                if (node.has(key)) {
                    val found = findDouyinItem(node.opt(key), depth + 1)
                    if (found != null) return found
                }
            }
            if (node.has("aweme_id") && (node.has("video") || node.has("images") || node.has("desc"))) {
                return node
            }
            val keys = node.keys()
            while (keys.hasNext()) {
                val found = findDouyinItem(node.opt(keys.next()), depth + 1)
                if (found != null) return found
            }
        }
        is JSONArray -> {
            for (index in 0 until node.length()) {
                val found = findDouyinItem(node.opt(index), depth + 1)
                if (found != null) return found
            }
        }
    }
    return null
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `node: Any?` — `node` recibe un valor de tipo `Any?`. El contrato permite `null`; el bloque debe decidir explícita o implícitamente qué significa la ausencia.
- `depth: Int = 0` — `depth` recibe un valor de tipo `Int`. El contrato no marca este parámetro como anulable. Tiene valor por defecto `0`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.

**Retorno:** `JSONObject?`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 774 | `val found` | `inferido` | `findDouyinItem(node.opt(key), depth + 1)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 781 | `val keys` | `inferido` | `node.keys()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 783 | `val found` | `inferido` | `findDouyinItem(node.opt(keys.next()), depth + 1)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 789 | `val found` | `inferido` | `findDouyinItem(node.opt(index), depth + 1)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 769 | `if (node == null \|\| node == JSONObject.NULL \|\| depth > 28) return null` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 770 | `when (node) {` | Selección múltiple de Kotlin: concentra casos mutuamente evaluados y normalmente define una normalización o estrategia según el valor de entrada. |
| 771 | `is JSONObject -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 772 | `for (key in listOf("item_list", "aweme_list", "aweme_detail", "aweme")) {` | Iteración: repite el cuerpo para cada elemento de la colección/rango; el coste del bloque crece con el número de elementos recorridos. |
| 773 | `if (node.has(key)) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 775 | `if (found != null) return found` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 778 | `if (node.has("aweme_id") && (node.has("video") \|\| node.has("images") \|\| node.has("desc"))) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 779 | `return node` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 782 | `while (keys.hasNext()) {` | Bucle condicionado: repite mientras la condición se mantenga verdadera; la lógica interna debe permitir progreso hacia la salida. |
| 784 | `if (found != null) return found` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 787 | `is JSONArray -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 788 | `for (index in 0 until node.length()) {` | Iteración: repite el cuerpo para cada elemento de la colección/rango; el coste del bloque crece con el número de elementos recorridos. |
| 790 | `if (found != null) return found` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 794 | `return null` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `listOf`, `node.has`, `findDouyinItem`, `node.opt`, `node.keys`, `keys.hasNext`, `keys.next`, `node.length`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.

### 4.37 `allJsonUrls` — fun, líneas 797–809

```kotlin
private fun allJsonUrls(node: JSONObject?): List<String> {
    if (node == null) return emptyList()
    val urls = linkedSetOf<String>()
    listOf("url_list", "urlList").forEach { key -> val list = node.optJSONArray(key) ?: return@forEach
            for (index in 0 until list.length()) {
                decodeHtml(list.optString(index)).replace("\\/", "/").takeIf { it.startsWith("http", ignoreCase = true) }?.let(urls::add)
            }
        }
    listOf("uri", "url").forEach { key -> decodeHtml(node.optString(key)).replace("\\/", "/")
                .takeIf { it.startsWith("http", ignoreCase = true) }?.let(urls::add)
        }
    return urls.toList()
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `node: JSONObject?` — `node` recibe un valor de tipo `JSONObject?`. El contrato permite `null`; el bloque debe decidir explícita o implícitamente qué significa la ausencia.

**Retorno:** `List<String>`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 799 | `val urls` | `inferido` | `linkedSetOf<String>()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 798 | `if (node == null) return emptyList()` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 801 | `for (index in 0 until list.length()) {` | Iteración: repite el cuerpo para cada elemento de la colección/rango; el coste del bloque crece con el número de elementos recorridos. |
| 808 | `return urls.toList()` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 2.
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 1.
- **Filtro condicional:** `takeIf` conserva el valor únicamente cuando el predicado se cumple; en caso contrario lo convierte en `null`. Apariciones en este bloque: 2.

#### Efectos secundarios y recursos

- **Persistencia Room/DAO:** Realiza una operación de persistencia o modificación a través de una capa de datos/DAO.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `emptyList`, `listOf`, `node.optJSONArray`, `list.length`, `decodeHtml`, `list.optString`, `replace`, `it.startsWith`, `let`, `node.optString`, `urls.toList`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.38 `douyinPreviewFromItem` — fun, líneas 811–836

```kotlin
private fun douyinPreviewFromItem(item: JSONObject, clickUrl: String): LinkPreviewData {
    val video = item.optJSONObject("video")
    val coverCandidates = buildList {
            addAll(allJsonUrls(video?.optJSONObject("origin_cover")))
            addAll(allJsonUrls(video?.optJSONObject("originCover")))
            addAll(allJsonUrls(video?.optJSONObject("cover")))
            addAll(allJsonUrls(video?.optJSONObject("dynamic_cover")))
            addAll(allJsonUrls(video?.optJSONObject("dynamicCover")))
            addAll(allJsonUrls(video?.optJSONObject("cover_medium")))
            addAll(allJsonUrls(video?.optJSONObject("coverMedium")))
            addAll(allJsonUrls(video?.optJSONObject("cover_thumb")))
            addAll(allJsonUrls(video?.optJSONObject("coverThumb")))
            val images = item.optJSONArray("images")
            if (images != null) {
                for (index in 0 until images.length()) {
                    val image = images.optJSONObject(index)
                    addAll(allJsonUrls(image))
                    addAll(allJsonUrls(image?.optJSONObject("display_image")))
                }
            }
        }.filter { it.startsWith("http", ignoreCase = true) }.distinct()
    val title = item.optString("desc").cleanText().takeIf { it.isNotBlank() }
    val author = item.optJSONObject("author")?.optString("nickname")?.cleanText()?.takeIf { it.isNotBlank() }
    return LinkPreviewData(url = clickUrl, title = title ?: author ?: "Douyin", description = author,
        imageUrl = coverCandidates.firstOrNull(), siteName = "Douyin", host = safeHost(clickUrl), imageCandidates = coverCandidates)
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `item: JSONObject` — `item` recibe un valor de tipo `JSONObject`. El contrato no marca este parámetro como anulable.
- `clickUrl: String` — `clickUrl` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `LinkPreviewData`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 812 | `val video` | `inferido` | `item.optJSONObject("video")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 813 | `val coverCandidates` | `inferido` | `buildList {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 823 | `val images` | `inferido` | `item.optJSONArray("images")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 826 | `val image` | `inferido` | `images.optJSONObject(index)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 832 | `val title` | `inferido` | `item.optString("desc").cleanText().takeIf { it.isNotBlank() }` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 833 | `val author` | `inferido` | `item.optJSONObject("author")?.optString("nickname")?.cleanText()?.takeIf { it.isNotBlank() }` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 824 | `if (images != null) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 825 | `for (index in 0 until images.length()) {` | Iteración: repite el cuerpo para cada elemento de la colección/rango; el coste del bloque crece con el número de elementos recorridos. |
| 834 | `return LinkPreviewData(url = clickUrl, title = title ?: author ?: "Douyin", description = author,` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 13.
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 2.
- **Filtro condicional:** `takeIf` conserva el valor únicamente cuando el predicado se cumple; en caso contrario lo convierte en `null`. Apariciones en este bloque: 2.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `item.optJSONObject`, `addAll`, `allJsonUrls`, `optJSONObject`, `item.optJSONArray`, `images.length`, `images.optJSONObject`, `it.startsWith`, `distinct`, `item.optString`, `cleanText`, `it.isNotBlank`, `optString`, `LinkPreviewData`, `coverCandidates.firstOrNull`, `safeHost`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.39 `fetchDouyinItemInfo` — fun, líneas 838–887

```kotlin
private fun fetchDouyinItemInfo(awemeId: String, clickUrl: String, cookieHeader: String? = null): LinkPreviewData? {
    val endpoints = listOf("https://www.iesdouyin.com/web/api/v2/aweme/iteminfo/?item_ids=$awemeId",
            "https://www.douyin.com/aweme/v1/web/aweme/detail/" + "?device_platform=webapp&aid=6383&channel=channel_pc_web" +
                "&aweme_id=$awemeId&request_source=600&origin_type=quick_player")
    endpoints.forEach { endpoint -> var connection: HttpURLConnection? = null
        try {
            connection = (URL(endpoint).openConnection() as HttpURLConnection).apply {
                        instanceFollowRedirects = true
                        connectTimeout = 5_000
                        readTimeout = 6_000
                        requestMethod = "GET"
                        setRequestProperty("User-Agent", douyinUserAgent())
                        setRequestProperty("Accept", "application/json,text/plain,*/*")
                        setRequestProperty("Referer", "https://www.douyin.com/")
                        cookieHeader?.takeIf { it.isNotBlank() }?.let { setRequestProperty("Cookie", it) }
                    }
            connection.connect()
            if (connection.responseCode !in 200..299) {
                return@forEach
            }
            val body = connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
            val root = JSONObject(body)
            val item = root.optJSONArray("item_list")?.optJSONObject(0)?: root.optJSONObject("aweme_detail")?: return@forEach
            val video = item.optJSONObject("video")
            val coverCandidates = buildList {
                    addAll(allJsonUrls(video?.optJSONObject("origin_cover")))
                    addAll(allJsonUrls(video?.optJSONObject("originCover")))
                    addAll(allJsonUrls(video?.optJSONObject("cover")))
                    addAll(allJsonUrls(video?.optJSONObject("dynamic_cover")))
                    addAll(allJsonUrls(video?.optJSONObject("dynamicCover")))
                    addAll(allJsonUrls(video?.optJSONObject("cover_medium")))
                    addAll(allJsonUrls(video?.optJSONObject("coverMedium")))
                    addAll(allJsonUrls(video?.optJSONObject("cover_thumb")))
                    addAll(allJsonUrls(video?.optJSONObject("coverThumb")))
                }.distinct()
            val cover = coverCandidates.firstOrNull()
            val title = item.optString("desc").cleanText().takeIf { it.isNotBlank() }
            val author = item.optJSONObject("author")?.optString("nickname")?.cleanText()?.takeIf { it.isNotBlank() }
            if (cover != null || title != null) {
                return LinkPreviewData(url = clickUrl, title = title ?: author ?: "Douyin", description = author, imageUrl = cover,
                    siteName = "Douyin", host = safeHost(clickUrl), imageCandidates = coverCandidates)
            }
        } catch (_: Exception) {
            // Probamos el siguiente endpoint.
        } finally {
            connection?.disconnect()
        }
    }
    return null
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `awemeId: String` — `awemeId` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.
- `clickUrl: String` — `clickUrl` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.
- `cookieHeader: String? = null` — `cookieHeader` recibe un valor de tipo `String?`. El contrato permite `null`; el bloque debe decidir explícita o implícitamente qué significa la ausencia. Tiene valor por defecto `null`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada.

**Retorno:** `LinkPreviewData?`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 839 | `val endpoints` | `inferido` | `listOf("https://www.iesdouyin.com/web/api/v2/aweme/iteminfo/?item_ids=$awemeId",` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 858 | `val body` | `inferido` | `connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 859 | `val root` | `inferido` | `JSONObject(body)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 860 | `val item` | `inferido` | `root.optJSONArray("item_list")?.optJSONObject(0)?: root.optJSONObject("aweme_detail")?: return@forEa…` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 861 | `val video` | `inferido` | `item.optJSONObject("video")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 862 | `val coverCandidates` | `inferido` | `buildList {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 873 | `val cover` | `inferido` | `coverCandidates.firstOrNull()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 874 | `val title` | `inferido` | `item.optString("desc").cleanText().takeIf { it.isNotBlank() }` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 875 | `val author` | `inferido` | `item.optJSONObject("author")?.optString("nickname")?.cleanText()?.takeIf { it.isNotBlank() }` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 843 | `try {` | Frontera de manejo de errores: agrupa operaciones que pueden lanzar excepciones para permitir recuperación o limpieza controlada. |
| 855 | `if (connection.responseCode !in 200..299) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 856 | `return@forEach` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 876 | `if (cover != null \|\| title != null) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 877 | `return LinkPreviewData(url = clickUrl, title = title ?: author ?: "Douyin", description = author, imageUrl = cover,` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 886 | `return null` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 16.
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 4.
- **Filtro condicional:** `takeIf` conserva el valor únicamente cuando el predicado se cumple; en caso contrario lo convierte en `null`. Apariciones en este bloque: 3.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `listOf`, `URL`, `openConnection`, `setRequestProperty`, `douyinUserAgent`, `it.isNotBlank`, `connection.connect`, `connection.inputStream.bufferedReader`, `it.readText`, `JSONObject`, `root.optJSONArray`, `optJSONObject`, `root.optJSONObject`, `item.optJSONObject`, `addAll`, `allJsonUrls`, `distinct`, `coverCandidates.firstOrNull`, `item.optString`, `cleanText`, `optString`, `LinkPreviewData`, `safeHost`, `disconnect`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.40 `firstJsonUrl` — fun, líneas 889–899

```kotlin
private fun firstJsonUrl(node: JSONObject?): String? {
    if (node == null) return null
    val list = node.optJSONArray("url_list")?: return null
    for (index in 0 until list.length()) {
        val value = list.optString(index)
        if (value.startsWith("http://", ignoreCase = true) || value.startsWith("https://", ignoreCase = true)) {
            return value
        }
    }
    return null
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `node: JSONObject?` — `node` recibe un valor de tipo `JSONObject?`. El contrato permite `null`; el bloque debe decidir explícita o implícitamente qué significa la ausencia.

**Retorno:** `String?`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 891 | `val list` | `inferido` | `node.optJSONArray("url_list")?: return null` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 893 | `val value` | `inferido` | `list.optString(index)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 890 | `if (node == null) return null` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 892 | `for (index in 0 until list.length()) {` | Iteración: repite el cuerpo para cada elemento de la colección/rango; el coste del bloque crece con el número de elementos recorridos. |
| 894 | `if (value.startsWith("http://", ignoreCase = true) \|\| value.startsWith("https://", ignoreCase = true)) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 895 | `return value` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 898 | `return null` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

**Restricciones concretas que aparecen en este bloque:**
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `node.optJSONArray`, `list.length`, `list.optString`, `value.startsWith`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.41 `extractDouyinCoverUrl` — fun, líneas 901–981

```kotlin
private fun extractDouyinCoverUrl(pageUrl: String, html: String): String? {
    val candidates = mutableListOf<Pair<Int, String>>()
    fun addCandidate(value: String?, path: String, bonus: Int = 0) {
        if (value.isNullOrBlank()) {
            return
        }
        extractUrlsFromDouyinValue(value).forEach { rawUrl -> val resolved = resolveUrl(pageUrl, rawUrl)?: rawUrl
                val score = douyinCoverScore(path = path, url = resolved) + bonus
                if (score > 0) {
                    candidates += score to resolved
                }
            }
    }
    fun walkJson(node: Any?, path: String = "root", depth: Int = 0) {
        if (node == null || depth > 24) {
            return
        }
        when (node) {
            is JSONObject -> {
                val keys = node.keys()
                while (keys.hasNext()) {
                    val key = keys.next()
                    val value = node.opt(key)
                    val childPath = "$path.$key"
                    when (value) {
                        is String -> addCandidate(value = value, path = childPath)
                        JSONObject.NULL -> Unit
                        else -> walkJson(node = value, path = childPath, depth = depth + 1)
                    }
                }
            }
            is JSONArray -> {
                for (index in 0 until node.length()) {
                    walkJson(node = node.opt(index), path = "$path[$index]", depth = depth + 1)
                }
            }
            is String -> addCandidate(value = node, path = path)
        }
    }
    val renderData = Regex(pattern =
                """<script\b[^>]*\bid=["']RENDER_DATA["'][^>]*>(.*?)</script>""",
            options = setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL)).find(html)?.groupValues?.getOrNull(1)
    if (!renderData.isNullOrBlank()) {
        val decoded = decodeDouyinEmbeddedJson(renderData)
        try {
            walkJson(JSONObject(decoded), path = "RENDER_DATA")
        } catch (_: Exception) {
            // La estructura de Douyin cambia con frecuencia. El fallback
            // textual de abajo todavía puede encontrar la portada.
        }
    }
    val routerData = Regex(pattern =
                """window\._ROUTER_DATA\s*=\s*(\{.*?\})\s*;?\s*</script>""",
            options = setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL)).find(html)?.groupValues?.getOrNull(1)
    if (!routerData.isNullOrBlank()) {
        val decoded = decodeDouyinEmbeddedJson(routerData)
        try {
            walkJson(JSONObject(decoded), path = "_ROUTER_DATA")
        } catch (_: Exception) {
            // Se continúa con el fallback textual.
        }
    }
    // Fallback para variantes nuevas de la página: buscamos URLs de los CDN
    // de ByteDance y puntuamos mejor las que aparecen junto a "cover".
    val normalizedHtml = decodeDouyinEmbeddedJson(html).replace("\\/", "/").replace("\\u002F", "/", ignoreCase = true)
            .replace("\\u003A", ":", ignoreCase = true).replace("\\u0026", "&", ignoreCase = true)
            .replace("\\u003D", "=", ignoreCase = true)
    val cdnUrlRegex = Regex(pattern =
                """https?://[^\s"'<>]+(?:douyinpic\.com|byteimg\.com|douyincdn\.com)[^\s"'<>]*""",
            option = RegexOption.IGNORE_CASE)
    cdnUrlRegex.findAll(normalizedHtml).forEach { match -> val start = (match.range.first - 160).coerceAtLeast(0)
            val end = (match.range.last + 160).coerceAtMost(normalizedHtml.lastIndex)
            val context = if (end >= start) {
                    normalizedHtml.substring(start, end + 1)
                } else {
                    ""
                }
            addCandidate(value = match.value, path = "html.$context", bonus = 5)
        }
    return candidates.asSequence().distinctBy { it.second }.maxByOrNull { it.first }?.second
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `pageUrl: String` — `pageUrl` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.
- `html: String` — `html` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `String?`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 902 | `val candidates` | `inferido` | `mutableListOf<Pair<Int, String>>()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 908 | `val score` | `inferido` | `douyinCoverScore(path = path, url = resolved) + bonus` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 920 | `val keys` | `inferido` | `node.keys()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 922 | `val key` | `inferido` | `keys.next()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 923 | `val value` | `inferido` | `node.opt(key)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 924 | `val childPath` | `inferido` | `"$path.$key"` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 940 | `val renderData` | `inferido` | `Regex(pattern =` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 944 | `val decoded` | `inferido` | `decodeDouyinEmbeddedJson(renderData)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 952 | `val routerData` | `inferido` | `Regex(pattern =` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 956 | `val decoded` | `inferido` | `decodeDouyinEmbeddedJson(routerData)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 965 | `val normalizedHtml` | `inferido` | `decodeDouyinEmbeddedJson(html).replace("\\/", "/").replace("\\u002F", "/", ignoreCase = true)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 968 | `val cdnUrlRegex` | `inferido` | `Regex(pattern =` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 972 | `val end` | `inferido` | `(match.range.last + 160).coerceAtMost(normalizedHtml.lastIndex)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El inicializador limita explícitamente el valor a un rango, evitando que dimensiones/intensidades/sizes inválidos se propaguen. |
| 973 | `val context` | `inferido` | `if (end >= start) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El `Context` da acceso a recursos/servicios/almacenamiento Android; debe respetarse el ciclo de vida del contexto recibido. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 904 | `if (value.isNullOrBlank()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 905 | `return` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 909 | `if (score > 0) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 915 | `if (node == null \|\| depth > 24) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 916 | `return` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 918 | `when (node) {` | Selección múltiple de Kotlin: concentra casos mutuamente evaluados y normalmente define una normalización o estrategia según el valor de entrada. |
| 919 | `is JSONObject -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 921 | `while (keys.hasNext()) {` | Bucle condicionado: repite mientras la condición se mantenga verdadera; la lógica interna debe permitir progreso hacia la salida. |
| 925 | `when (value) {` | Selección múltiple de Kotlin: concentra casos mutuamente evaluados y normalmente define una normalización o estrategia según el valor de entrada. |
| 926 | `is String -> addCandidate(value = value, path = childPath)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 927 | `JSONObject.NULL -> Unit` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 928 | `else -> walkJson(node = value, path = childPath, depth = depth + 1)` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |
| 932 | `is JSONArray -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 933 | `for (index in 0 until node.length()) {` | Iteración: repite el cuerpo para cada elemento de la colección/rango; el coste del bloque crece con el número de elementos recorridos. |
| 937 | `is String -> addCandidate(value = node, path = path)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 943 | `if (!renderData.isNullOrBlank()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 945 | `try {` | Frontera de manejo de errores: agrupa operaciones que pueden lanzar excepciones para permitir recuperación o limpieza controlada. |
| 955 | `if (!routerData.isNullOrBlank()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 957 | `try {` | Frontera de manejo de errores: agrupa operaciones que pueden lanzar excepciones para permitir recuperación o limpieza controlada. |
| 980 | `return candidates.asSequence().distinctBy { it.second }.maxByOrNull { it.first }?.second` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 5.
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 3.
- **Límite numérico:** Se fuerza el valor a un intervalo o umbral para impedir que salgan valores fuera de los límites esperados. Apariciones en este bloque: 2.

#### Efectos secundarios y recursos

- **Persistencia Room/DAO:** Realiza una operación de persistencia o modificación a través de una capa de datos/DAO.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `value.isNullOrBlank`, `extractUrlsFromDouyinValue`, `resolveUrl`, `douyinCoverScore`, `node.keys`, `keys.hasNext`, `keys.next`, `node.opt`, `addCandidate`, `walkJson`, `node.length`, `Regex`, `setOf`, `find`, `getOrNull`, `renderData.isNullOrBlank`, `decodeDouyinEmbeddedJson`, `JSONObject`, `routerData.isNullOrBlank`, `replace`, `cdnUrlRegex.findAll`, `coerceAtLeast`, `coerceAtMost`, `normalizedHtml.substring`, `candidates.asSequence`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.

### 4.42 `addCandidate` — fun, líneas 903–913

```kotlin
    fun addCandidate(value: String?, path: String, bonus: Int = 0) {
        if (value.isNullOrBlank()) {
            return
        }
        extractUrlsFromDouyinValue(value).forEach { rawUrl -> val resolved = resolveUrl(pageUrl, rawUrl)?: rawUrl
                val score = douyinCoverScore(path = path, url = resolved) + bonus
                if (score > 0) {
                    candidates += score to resolved
                }
            }
    }
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `value: String?` — `value` recibe un valor de tipo `String?`. El contrato permite `null`; el bloque debe decidir explícita o implícitamente qué significa la ausencia.
- `path: String` — `path` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.
- `bonus: Int = 0` — `bonus` recibe un valor de tipo `Int`. El contrato no marca este parámetro como anulable. Tiene valor por defecto `0`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.


#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 908 | `val score` | `inferido` | `douyinCoverScore(path = path, url = resolved) + bonus` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 904 | `if (value.isNullOrBlank()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 905 | `return` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 909 | `if (score > 0) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |

**Restricciones concretas que aparecen en este bloque:**
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `value.isNullOrBlank`, `extractUrlsFromDouyinValue`, `resolveUrl`, `douyinCoverScore`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.43 `walkJson` — fun, líneas 914–981

```kotlin
    fun walkJson(node: Any?, path: String = "root", depth: Int = 0) {
        if (node == null || depth > 24) {
            return
        }
        when (node) {
            is JSONObject -> {
                val keys = node.keys()
                while (keys.hasNext()) {
                    val key = keys.next()
                    val value = node.opt(key)
                    val childPath = "$path.$key"
                    when (value) {
                        is String -> addCandidate(value = value, path = childPath)
                        JSONObject.NULL -> Unit
                        else -> walkJson(node = value, path = childPath, depth = depth + 1)
                    }
                }
            }
            is JSONArray -> {
                for (index in 0 until node.length()) {
                    walkJson(node = node.opt(index), path = "$path[$index]", depth = depth + 1)
                }
            }
            is String -> addCandidate(value = node, path = path)
        }
    }
    val renderData = Regex(pattern =
                """<script\b[^>]*\bid=["']RENDER_DATA["'][^>]*>(.*?)</script>""",
            options = setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL)).find(html)?.groupValues?.getOrNull(1)
    if (!renderData.isNullOrBlank()) {
        val decoded = decodeDouyinEmbeddedJson(renderData)
        try {
            walkJson(JSONObject(decoded), path = "RENDER_DATA")
        } catch (_: Exception) {
            // La estructura de Douyin cambia con frecuencia. El fallback
            // textual de abajo todavía puede encontrar la portada.
        }
    }
    val routerData = Regex(pattern =
                """window\._ROUTER_DATA\s*=\s*(\{.*?\})\s*;?\s*</script>""",
            options = setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL)).find(html)?.groupValues?.getOrNull(1)
    if (!routerData.isNullOrBlank()) {
        val decoded = decodeDouyinEmbeddedJson(routerData)
        try {
            walkJson(JSONObject(decoded), path = "_ROUTER_DATA")
        } catch (_: Exception) {
            // Se continúa con el fallback textual.
        }
    }
    // Fallback para variantes nuevas de la página: buscamos URLs de los CDN
    // de ByteDance y puntuamos mejor las que aparecen junto a "cover".
    val normalizedHtml = decodeDouyinEmbeddedJson(html).replace("\\/", "/").replace("\\u002F", "/", ignoreCase = true)
            .replace("\\u003A", ":", ignoreCase = true).replace("\\u0026", "&", ignoreCase = true)
            .replace("\\u003D", "=", ignoreCase = true)
    val cdnUrlRegex = Regex(pattern =
                """https?://[^\s"'<>]+(?:douyinpic\.com|byteimg\.com|douyincdn\.com)[^\s"'<>]*""",
            option = RegexOption.IGNORE_CASE)
    cdnUrlRegex.findAll(normalizedHtml).forEach { match -> val start = (match.range.first - 160).coerceAtLeast(0)
            val end = (match.range.last + 160).coerceAtMost(normalizedHtml.lastIndex)
            val context = if (end >= start) {
                    normalizedHtml.substring(start, end + 1)
                } else {
                    ""
                }
            addCandidate(value = match.value, path = "html.$context", bonus = 5)
        }
    return candidates.asSequence().distinctBy { it.second }.maxByOrNull { it.first }?.second
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `node: Any?` — `node` recibe un valor de tipo `Any?`. El contrato permite `null`; el bloque debe decidir explícita o implícitamente qué significa la ausencia.
- `path: String = "root"` — `path` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable. Tiene valor por defecto `"root"`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada.
- `depth: Int = 0` — `depth` recibe un valor de tipo `Int`. El contrato no marca este parámetro como anulable. Tiene valor por defecto `0`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.


#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 920 | `val keys` | `inferido` | `node.keys()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 922 | `val key` | `inferido` | `keys.next()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 923 | `val value` | `inferido` | `node.opt(key)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 924 | `val childPath` | `inferido` | `"$path.$key"` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 940 | `val renderData` | `inferido` | `Regex(pattern =` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 944 | `val decoded` | `inferido` | `decodeDouyinEmbeddedJson(renderData)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 952 | `val routerData` | `inferido` | `Regex(pattern =` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 956 | `val decoded` | `inferido` | `decodeDouyinEmbeddedJson(routerData)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 965 | `val normalizedHtml` | `inferido` | `decodeDouyinEmbeddedJson(html).replace("\\/", "/").replace("\\u002F", "/", ignoreCase = true)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 968 | `val cdnUrlRegex` | `inferido` | `Regex(pattern =` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 972 | `val end` | `inferido` | `(match.range.last + 160).coerceAtMost(normalizedHtml.lastIndex)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El inicializador limita explícitamente el valor a un rango, evitando que dimensiones/intensidades/sizes inválidos se propaguen. |
| 973 | `val context` | `inferido` | `if (end >= start) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El `Context` da acceso a recursos/servicios/almacenamiento Android; debe respetarse el ciclo de vida del contexto recibido. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 915 | `if (node == null \|\| depth > 24) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 916 | `return` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 918 | `when (node) {` | Selección múltiple de Kotlin: concentra casos mutuamente evaluados y normalmente define una normalización o estrategia según el valor de entrada. |
| 919 | `is JSONObject -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 921 | `while (keys.hasNext()) {` | Bucle condicionado: repite mientras la condición se mantenga verdadera; la lógica interna debe permitir progreso hacia la salida. |
| 925 | `when (value) {` | Selección múltiple de Kotlin: concentra casos mutuamente evaluados y normalmente define una normalización o estrategia según el valor de entrada. |
| 926 | `is String -> addCandidate(value = value, path = childPath)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 927 | `JSONObject.NULL -> Unit` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 928 | `else -> walkJson(node = value, path = childPath, depth = depth + 1)` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |
| 932 | `is JSONArray -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 933 | `for (index in 0 until node.length()) {` | Iteración: repite el cuerpo para cada elemento de la colección/rango; el coste del bloque crece con el número de elementos recorridos. |
| 937 | `is String -> addCandidate(value = node, path = path)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 943 | `if (!renderData.isNullOrBlank()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 945 | `try {` | Frontera de manejo de errores: agrupa operaciones que pueden lanzar excepciones para permitir recuperación o limpieza controlada. |
| 955 | `if (!routerData.isNullOrBlank()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 957 | `try {` | Frontera de manejo de errores: agrupa operaciones que pueden lanzar excepciones para permitir recuperación o limpieza controlada. |
| 980 | `return candidates.asSequence().distinctBy { it.second }.maxByOrNull { it.first }?.second` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 5.
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 2.
- **Límite numérico:** Se fuerza el valor a un intervalo o umbral para impedir que salgan valores fuera de los límites esperados. Apariciones en este bloque: 2.

#### Efectos secundarios y recursos

- **Persistencia Room/DAO:** Realiza una operación de persistencia o modificación a través de una capa de datos/DAO.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `node.keys`, `keys.hasNext`, `keys.next`, `node.opt`, `addCandidate`, `walkJson`, `node.length`, `Regex`, `setOf`, `find`, `getOrNull`, `renderData.isNullOrBlank`, `decodeDouyinEmbeddedJson`, `JSONObject`, `routerData.isNullOrBlank`, `replace`, `cdnUrlRegex.findAll`, `coerceAtLeast`, `coerceAtMost`, `normalizedHtml.substring`, `candidates.asSequence`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.

### 4.44 `decodeDouyinEmbeddedJson` — fun, líneas 983–997

```kotlin
private fun decodeDouyinEmbeddedJson(value: String): String {
    var current = decodeHtml(value).trim()
    repeat(3) {
        val next = try {
                URLDecoder.decode(current.replace("+", "%2B"), Charsets.UTF_8.name())
            } catch (_: Exception) {
                current
            }
        if (next == current) {
            return current
        }
        current = next
    }
    return current
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

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 984 | `var current` | `inferido` | `decodeHtml(value).trim()` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 986 | `val next` | `inferido` | `try {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 991 | `if (next == current) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 992 | `return current` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 996 | `return current` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

#### Efectos secundarios y recursos

- **Persistencia Room/DAO:** Realiza una operación de persistencia o modificación a través de una capa de datos/DAO.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `decodeHtml`, `trim`, `repeat`, `URLDecoder.decode`, `current.replace`, `Charsets.UTF_8.name`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.45 `extractUrlsFromDouyinValue` — fun, líneas 999–1012

```kotlin
private fun extractUrlsFromDouyinValue(value: String): List<String> {
    val normalized = decodeHtml(value).replace("\\/", "/").replace("\\u002F", "/", ignoreCase = true)
            .replace("\\u003A", ":", ignoreCase = true).replace("\\u0026", "&", ignoreCase = true)
            .replace("\\u003D", "=", ignoreCase = true).trim()
    val results = linkedSetOf<String>()
    if (normalized.startsWith("http://", ignoreCase = true) || normalized.startsWith("https://", ignoreCase = true) ||
        normalized.startsWith("//")) {
        results += normalized.trim('"', '\'', '`')
    }
    Regex(
        pattern = """(?:https?:)?//[^\s"'<>]+""",
        option = RegexOption.IGNORE_CASE).findAll(normalized).map { it.value }.forEach(results::add)
    return results.toList()
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `value: String` — `value` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `List<String>`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 1000 | `val normalized` | `inferido` | `decodeHtml(value).replace("\\/", "/").replace("\\u002F", "/", ignoreCase = true)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1003 | `val results` | `inferido` | `linkedSetOf<String>()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 1004 | `if (normalized.startsWith("http://", ignoreCase = true) \|\| normalized.startsWith("https://", ignoreCase = true) \|\|` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1011 | `return results.toList()` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

**Restricciones concretas que aparecen en este bloque:**
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 2.

#### Efectos secundarios y recursos

- **Persistencia Room/DAO:** Realiza una operación de persistencia o modificación a través de una capa de datos/DAO.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `decodeHtml`, `replace`, `trim`, `normalized.startsWith`, `normalized.trim`, `Regex`, `findAll`, `forEach`, `results.toList`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.46 `douyinCoverScore` — fun, líneas 1014–1046

```kotlin
private fun douyinCoverScore(path: String, url: String): Int {
    val normalizedPath = path.lowercase(Locale.ROOT).replace("_", "").replace("-", "")
    val normalizedUrl = url.lowercase(Locale.ROOT)
    var score = 0
    when {
        normalizedPath.contains("origincover") -> score += 220
        normalizedPath.contains("itemcover") -> score += 200
        normalizedPath.contains("videocover") -> score += 190
        normalizedPath.contains("dynamiccover") -> score += 175
        normalizedPath.contains("cover") -> score += 150
        normalizedPath.contains("poster") -> score += 120
    }
    if (normalizedPath.contains("avatar") || normalizedPath.contains("author") && !normalizedPath.contains("cover")) {
        score -= 240
    }
    if (normalizedPath.contains("music") || normalizedPath.contains("emoji") || normalizedPath.contains("icon")) {
        score -= 140
    }
    if (normalizedUrl.contains("douyinpic.com")) {
        score += 45
    }
    if (normalizedUrl.contains("byteimg.com")) {
        score += 35
    }
    if (normalizedUrl.contains("douyincdn.com")) {
        score += 30
    }
    if (normalizedUrl.contains(".jpeg") || normalizedUrl.contains(".jpg") || normalizedUrl.contains(".webp") ||
        normalizedUrl.contains(".png")) {
        score += 15
    }
    return score
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `path: String` — `path` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.
- `url: String` — `url` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `Int`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 1015 | `val normalizedPath` | `inferido` | `path.lowercase(Locale.ROOT).replace("_", "").replace("-", "")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1016 | `val normalizedUrl` | `inferido` | `url.lowercase(Locale.ROOT)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1017 | `var score` | `inferido` | `0` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 1018 | `when {` | Selección múltiple de Kotlin: concentra casos mutuamente evaluados y normalmente define una normalización o estrategia según el valor de entrada. |
| 1019 | `normalizedPath.contains("origincover") -> score += 220` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 1020 | `normalizedPath.contains("itemcover") -> score += 200` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 1021 | `normalizedPath.contains("videocover") -> score += 190` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 1022 | `normalizedPath.contains("dynamiccover") -> score += 175` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 1023 | `normalizedPath.contains("cover") -> score += 150` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 1024 | `normalizedPath.contains("poster") -> score += 120` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 1026 | `if (normalizedPath.contains("avatar") \|\| normalizedPath.contains("author") && !normalizedPath.contains("cover")) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1029 | `if (normalizedPath.contains("music") \|\| normalizedPath.contains("emoji") \|\| normalizedPath.contains("icon")) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1032 | `if (normalizedUrl.contains("douyinpic.com")) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1035 | `if (normalizedUrl.contains("byteimg.com")) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1038 | `if (normalizedUrl.contains("douyincdn.com")) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1041 | `if (normalizedUrl.contains(".jpeg") \|\| normalizedUrl.contains(".jpg") \|\| normalizedUrl.contains(".webp") \|\|` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1045 | `return score` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

#### Efectos secundarios y recursos

- **Persistencia Room/DAO:** Realiza una operación de persistencia o modificación a través de una capa de datos/DAO.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `path.lowercase`, `replace`, `url.lowercase`, `normalizedPath.contains`, `normalizedUrl.contains`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.

### 4.47 `isInstagramRelatedUrl` — fun, líneas 1048–1051

```kotlin
private fun isInstagramRelatedUrl(url: String): Boolean {
    val host = safeHost(url).lowercase(Locale.ROOT)
    return host == "instagram.com" || host.endsWith(".instagram.com")
}
```

#### Qué hace y por qué existe

Evalúa una condición y devuelve/representa una decisión booleana utilizada por otras ramas del flujo.

#### Contrato de la declaración

**Parámetros:**

- `url: String` — `url` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `Boolean`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 1049 | `val host` | `inferido` | `safeHost(url).lowercase(Locale.ROOT)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 1050 | `return host == "instagram.com" \|\| host.endsWith(".instagram.com")` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `safeHost`, `lowercase`, `host.endsWith`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.48 `isThreadsRelatedUrl` — fun, líneas 1053–1056

```kotlin
private fun isThreadsRelatedUrl(url: String): Boolean {
    val host = safeHost(url).lowercase(Locale.ROOT)
    return host == "threads.net" || host.endsWith(".threads.net") || host == "threads.com" || host.endsWith(".threads.com")
}
```

#### Qué hace y por qué existe

Evalúa una condición y devuelve/representa una decisión booleana utilizada por otras ramas del flujo.

#### Contrato de la declaración

**Parámetros:**

- `url: String` — `url` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `Boolean`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 1054 | `val host` | `inferido` | `safeHost(url).lowercase(Locale.ROOT)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 1055 | `return host == "threads.net" \|\| host.endsWith(".threads.net") \|\| host == "threads.com" \|\| host.endsWith(".threads.com")` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `safeHost`, `lowercase`, `host.endsWith`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.49 `isFacebookRelatedUrl` — fun, líneas 1058–1062

```kotlin
private fun isFacebookRelatedUrl(url: String): Boolean {
    val host = safeHost(url).lowercase(Locale.ROOT)
    return host == "facebook.com" || host.endsWith(".facebook.com") || host == "fb.com" || host.endsWith(".fb.com") || host == "fb.watch" ||
        host.endsWith(".fb.watch")
}
```

#### Qué hace y por qué existe

Evalúa una condición y devuelve/representa una decisión booleana utilizada por otras ramas del flujo.

#### Contrato de la declaración

**Parámetros:**

- `url: String` — `url` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `Boolean`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 1059 | `val host` | `inferido` | `safeHost(url).lowercase(Locale.ROOT)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 1060 | `return host == "facebook.com" \|\| host.endsWith(".facebook.com") \|\| host == "fb.com" \|\| host.endsWith(".fb.com") \|\| host == "fb.watch" \|\|` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `safeHost`, `lowercase`, `host.endsWith`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.50 `isMetaSocialUrl` — fun, líneas 1064–1065

```kotlin
private fun isMetaSocialUrl(url: String): Boolean = isInstagramRelatedUrl(url) || isThreadsRelatedUrl(url) || isFacebookRelatedUrl(url)
```

#### Qué hace y por qué existe

Evalúa una condición y devuelve/representa una decisión booleana utilizada por otras ramas del flujo.

#### Contrato de la declaración

**Parámetros:**

- `url: String` — `url` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `Boolean`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `isInstagramRelatedUrl`, `isThreadsRelatedUrl`, `isFacebookRelatedUrl`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.51 `isRedditRelatedUrl` — fun, líneas 1066–1069

```kotlin
private fun isRedditRelatedUrl(url: String): Boolean {
    val host = safeHost(url).lowercase(Locale.ROOT)
    return host == "reddit.com" || host.endsWith(".reddit.com") || host == "redd.it" || host.endsWith(".redd.it")
}
```

#### Qué hace y por qué existe

Evalúa una condición y devuelve/representa una decisión booleana utilizada por otras ramas del flujo.

#### Contrato de la declaración

**Parámetros:**

- `url: String` — `url` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `Boolean`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 1067 | `val host` | `inferido` | `safeHost(url).lowercase(Locale.ROOT)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 1068 | `return host == "reddit.com" \|\| host.endsWith(".reddit.com") \|\| host == "redd.it" \|\| host.endsWith(".redd.it")` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `safeHost`, `lowercase`, `host.endsWith`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.52 `fetchMetaOEmbedPreview` — fun, líneas 1076–1132

```kotlin
private fun fetchMetaOEmbedPreview(url: String): LinkPreviewData? {
    val encoded = URLEncoder.encode(url, Charsets.UTF_8.name())
    val provider: String
    val endpoint: String
    when {
        isInstagramRelatedUrl(url) -> {
            provider = "Instagram"
            endpoint = "https://graph.facebook.com/v25.0/instagram_oembed" + "?omitscript=true&maxwidth=720&url=$encoded"
        }
        isThreadsRelatedUrl(url) -> {
            provider = "Threads"
            endpoint = "https://graph.threads.com/oembed" + "?omitscript=true&maxwidth=720&url=$encoded"
        }
        isFacebookRelatedUrl(url) -> {
            provider = "Facebook"
            val isVideo = url.contains("/reel/", ignoreCase = true) || url.contains("/videos/", ignoreCase = true) ||
                    safeHost(url).equals("fb.watch", ignoreCase = true)
            val type = if (isVideo) "oembed_video" else "oembed_post"
            endpoint = "https://graph.facebook.com/v25.0/$type" + "?omitscript=true&maxwidth=720&url=$encoded"
        }
        else -> return null
    }
    var connection: HttpURLConnection? = null
    return try {
        connection = (URL(endpoint).openConnection() as HttpURLConnection).apply {
                    instanceFollowRedirects = true
                    connectTimeout = 6_000
                    readTimeout = 7_000
                    requestMethod = "GET"
                    setRequestProperty("User-Agent", previewUserAgent())
                    setRequestProperty("Accept", "application/json,text/plain,*/*")
                    setRequestProperty("Accept-Language", "es-MX,es;q=0.9,en;q=0.7")
                }
        connection.connect()
        if (connection.responseCode !in 200..299) {
            return null
        }
        val body = connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
        val json = JSONObject(body)
        val html = json.optString("html")
        val thumbnail = firstNonBlank(json.optString("thumbnail_url"), json.optString("thumbnailUrl"))
                ?.takeIf { it.startsWith("http", ignoreCase = true) }
        val htmlCandidates = extractSocialImageCandidates(pageUrl = url, html = html)
        val candidates = buildList {
                thumbnail?.let(::add)
                addAll(htmlCandidates)
            }.distinct()
        val title = firstNonBlank(json.optString("title"), json.optString("author_name"))?.cleanText()
        val author = json.optString("author_name").cleanText().takeIf { it.isNotBlank() }
        LinkPreviewData(url = url, title = title ?: provider, description = author, imageUrl = candidates.firstOrNull(), siteName =
                json.optString("provider_name").takeIf { it.isNotBlank() }?: provider, host = safeHost(url), imageCandidates = candidates)
    } catch (_: Exception) {
        null
    } finally {
        connection?.disconnect()
    }
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `url: String` — `url` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `LinkPreviewData?`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 1077 | `val encoded` | `inferido` | `URLEncoder.encode(url, Charsets.UTF_8.name())` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1078 | `val provider` | `String` | `(sin inicializador en la declaración)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo declarado es `String`. No declara nulabilidad explícita. |
| 1079 | `val endpoint` | `String` | `(sin inicializador en la declaración)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo declarado es `String`. No declara nulabilidad explícita. |
| 1091 | `val isVideo` | `inferido` | `url.contains("/reel/", ignoreCase = true) \|\| url.contains("/videos/", ignoreCase = true) \|\|` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El nombre indica un estado de decisión/visibilidad/selección usado para controlar una rama de comportamiento o presentación. |
| 1093 | `val type` | `inferido` | `if (isVideo) "oembed_video" else "oembed_post"` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1098 | `var connection` | `HttpURLConnection?` | `null` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo declarado es `HttpURLConnection?`. Admite `null`, así que los consumidores deben contemplar ausencia de valor. |
| 1113 | `val body` | `inferido` | `connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1114 | `val json` | `inferido` | `JSONObject(body)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1115 | `val html` | `inferido` | `json.optString("html")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1116 | `val thumbnail` | `inferido` | `firstNonBlank(json.optString("thumbnail_url"), json.optString("thumbnailUrl"))` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1118 | `val htmlCandidates` | `inferido` | `extractSocialImageCandidates(pageUrl = url, html = html)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1119 | `val candidates` | `inferido` | `buildList {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1123 | `val title` | `inferido` | `firstNonBlank(json.optString("title"), json.optString("author_name"))?.cleanText()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 1124 | `val author` | `inferido` | `json.optString("author_name").cleanText().takeIf { it.isNotBlank() }` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 1080 | `when {` | Selección múltiple de Kotlin: concentra casos mutuamente evaluados y normalmente define una normalización o estrategia según el valor de entrada. |
| 1081 | `isInstagramRelatedUrl(url) -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 1085 | `isThreadsRelatedUrl(url) -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 1089 | `isFacebookRelatedUrl(url) -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 1096 | `else -> return null` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |
| 1099 | `return try {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 1110 | `if (connection.responseCode !in 200..299) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1111 | `return null` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 4.
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 2.
- **Filtro condicional:** `takeIf` conserva el valor únicamente cuando el predicado se cumple; en caso contrario lo convierte en `null`. Apariciones en este bloque: 3.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `URLEncoder.encode`, `Charsets.UTF_8.name`, `isInstagramRelatedUrl`, `isThreadsRelatedUrl`, `isFacebookRelatedUrl`, `url.contains`, `safeHost`, `equals`, `URL`, `openConnection`, `setRequestProperty`, `previewUserAgent`, `connection.connect`, `connection.inputStream.bufferedReader`, `it.readText`, `JSONObject`, `json.optString`, `firstNonBlank`, `it.startsWith`, `extractSocialImageCandidates`, `let`, `addAll`, `distinct`, `cleanText`, `it.isNotBlank`, `LinkPreviewData`, `candidates.firstOrNull`, `disconnect`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.

### 4.53 `fetchRedditJsonPreview` — fun, líneas 1138–1209

```kotlin
private fun fetchRedditJsonPreview(entityUrl: String, clickUrl: String): LinkPreviewData? {
    val canonical = resolveSimpleRedirect(entityUrl) ?: entityUrl
    val clean = canonical.substringBefore('#').substringBefore('?').trimEnd('/')
    if (!isRedditRelatedUrl(clean)) return null
    val jsonUrl = "$clean.json?raw_json=1"
    var connection: HttpURLConnection? = null
    return try {
        connection = (URL(jsonUrl).openConnection() as HttpURLConnection).apply {
                    instanceFollowRedirects = true
                    connectTimeout = 6_000
                    readTimeout = 7_000
                    requestMethod = "GET"
                    setRequestProperty("User-Agent", "MyNotes/1.0 Android link-preview")
                    setRequestProperty("Accept", "application/json,text/plain,*/*")
                    setRequestProperty("Accept-Language", "en-US,en;q=0.9")
                }
        connection.connect()
        if (connection.responseCode !in 200..299) {
            return null
        }
        val body = connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
        val root = JSONArray(body)
        val post = root.optJSONObject(0)?.optJSONObject("data")?.optJSONArray("children")?.optJSONObject(0)?.optJSONObject("data")
                ?: return null
        val candidates = linkedSetOf<String>()
        fun addCandidate(value: String?) {
            value?.let(::decodeHtml)?.takeIf { it.startsWith("http", ignoreCase = true) }?.let(candidates::add)
        }
        val previewImages = post.optJSONObject("preview")?.optJSONArray("images")
        if (previewImages != null) {
            for (index in 0 until previewImages.length()) {
                val image = previewImages.optJSONObject(index) ?: continue
                addCandidate(image.optJSONObject("source")?.optString("url"))
                val resolutions = image.optJSONArray("resolutions")
                if (resolutions != null) {
                    for (r in resolutions.length() - 1 downTo 0) {
                        addCandidate(resolutions.optJSONObject(r)?.optString("url"))
                    }
                }
            }
        }
        addCandidate(post.optJSONObject("secure_media")?.optJSONObject("oembed")?.optString("thumbnail_url"))
        addCandidate(post.optJSONObject("media")?.optJSONObject("oembed")?.optString("thumbnail_url"))
        addCandidate(post.optString("thumbnail"))
        addCandidate(directImageUrl(post.optString("url_overridden_by_dest")))
        addCandidate(directImageUrl(post.optString("url")))
        val mediaMetadata = post.optJSONObject("media_metadata")
        if (mediaMetadata != null) {
            val keys = mediaMetadata.keys()
            while (keys.hasNext()) {
                val media = mediaMetadata.optJSONObject(keys.next()) ?: continue
                addCandidate(media.optJSONObject("s")?.optString("u"))
                val previews = media.optJSONArray("p")
                if (previews != null) {
                    for (index in previews.length() - 1 downTo 0) {
                        addCandidate(previews.optJSONObject(index)?.optString("u"))
                    }
                }
            }
        }
        val title = post.optString("title").cleanText().takeIf { it.isNotBlank() }
        val subreddit = post.optString("subreddit_name_prefixed").takeIf { it.isNotBlank() }
        val author = post.optString("author").takeIf { it.isNotBlank() }
        LinkPreviewData(url = clickUrl, title = title ?: "Reddit", description = listOfNotNull(subreddit, author?.let { "u/$it" })
                    .joinToString(" · ").takeIf { it.isNotBlank() }, imageUrl = candidates.firstOrNull(), siteName = "Reddit",
            host = safeHost(clickUrl), imageCandidates = candidates.toList())
    } catch (_: Exception) {
        null
    } finally {
        connection?.disconnect()
    }
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `entityUrl: String` — `entityUrl` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.
- `clickUrl: String` — `clickUrl` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `LinkPreviewData?`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 1139 | `val canonical` | `inferido` | `resolveSimpleRedirect(entityUrl) ?: entityUrl` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 1140 | `val clean` | `inferido` | `canonical.substringBefore('#').substringBefore('?').trimEnd('/')` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1142 | `val jsonUrl` | `inferido` | `"$clean.json?raw_json=1"` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1143 | `var connection` | `HttpURLConnection?` | `null` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo declarado es `HttpURLConnection?`. Admite `null`, así que los consumidores deben contemplar ausencia de valor. |
| 1158 | `val body` | `inferido` | `connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1159 | `val root` | `inferido` | `JSONArray(body)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1160 | `val post` | `inferido` | `root.optJSONObject(0)?.optJSONObject("data")?.optJSONArray("children")?.optJSONObject(0)?.optJSONObj…` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 1162 | `val candidates` | `inferido` | `linkedSetOf<String>()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1166 | `val previewImages` | `inferido` | `post.optJSONObject("preview")?.optJSONArray("images")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 1169 | `val image` | `inferido` | `previewImages.optJSONObject(index) ?: continue` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 1171 | `val resolutions` | `inferido` | `image.optJSONArray("resolutions")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1184 | `val mediaMetadata` | `inferido` | `post.optJSONObject("media_metadata")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1186 | `val keys` | `inferido` | `mediaMetadata.keys()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1188 | `val media` | `inferido` | `mediaMetadata.optJSONObject(keys.next()) ?: continue` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 1190 | `val previews` | `inferido` | `media.optJSONArray("p")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1198 | `val title` | `inferido` | `post.optString("title").cleanText().takeIf { it.isNotBlank() }` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1199 | `val subreddit` | `inferido` | `post.optString("subreddit_name_prefixed").takeIf { it.isNotBlank() }` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1200 | `val author` | `inferido` | `post.optString("author").takeIf { it.isNotBlank() }` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 1141 | `if (!isRedditRelatedUrl(clean)) return null` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1144 | `return try {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 1155 | `if (connection.responseCode !in 200..299) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1156 | `return null` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 1167 | `if (previewImages != null) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1168 | `for (index in 0 until previewImages.length()) {` | Iteración: repite el cuerpo para cada elemento de la colección/rango; el coste del bloque crece con el número de elementos recorridos. |
| 1172 | `if (resolutions != null) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1173 | `for (r in resolutions.length() - 1 downTo 0) {` | Iteración: repite el cuerpo para cada elemento de la colección/rango; el coste del bloque crece con el número de elementos recorridos. |
| 1185 | `if (mediaMetadata != null) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1187 | `while (keys.hasNext()) {` | Bucle condicionado: repite mientras la condición se mantenga verdadera; la lógica interna debe permitir progreso hacia la salida. |
| 1191 | `if (previews != null) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1192 | `for (index in previews.length() - 1 downTo 0) {` | Iteración: repite el cuerpo para cada elemento de la colección/rango; el coste del bloque crece con el número de elementos recorridos. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 18.
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 5.
- **Filtro condicional:** `takeIf` conserva el valor únicamente cuando el predicado se cumple; en caso contrario lo convierte en `null`. Apariciones en este bloque: 5.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `resolveSimpleRedirect`, `canonical.substringBefore`, `substringBefore`, `trimEnd`, `isRedditRelatedUrl`, `URL`, `openConnection`, `setRequestProperty`, `connection.connect`, `connection.inputStream.bufferedReader`, `it.readText`, `JSONArray`, `root.optJSONObject`, `optJSONObject`, `optJSONArray`, `let`, `it.startsWith`, `post.optJSONObject`, `previewImages.length`, `previewImages.optJSONObject`, `addCandidate`, `image.optJSONObject`, `optString`, `image.optJSONArray`, `resolutions.length`, `resolutions.optJSONObject`, `post.optString`, `directImageUrl`, `mediaMetadata.keys`, `keys.hasNext`, `mediaMetadata.optJSONObject`, `keys.next`, `media.optJSONObject`, `media.optJSONArray`, `previews.length`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.54 `addCandidate` — fun, líneas 1163–1165

```kotlin
        fun addCandidate(value: String?) {
            value?.let(::decodeHtml)?.takeIf { it.startsWith("http", ignoreCase = true) }?.let(candidates::add)
        }
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `value: String?` — `value` recibe un valor de tipo `String?`. El contrato permite `null`; el bloque debe decidir explícita o implícitamente qué significa la ausencia.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.


#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 3.
- **Filtro condicional:** `takeIf` conserva el valor únicamente cuando el predicado se cumple; en caso contrario lo convierte en `null`. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `let`, `it.startsWith`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.55 `resolveSimpleRedirect` — fun, líneas 1211–1231

```kotlin
private fun resolveSimpleRedirect(url: String): String? {
    if (!safeHost(url).equals("redd.it", ignoreCase = true) && !safeHost(url).endsWith(".redd.it", ignoreCase = true)) {
        return url
    }
    var connection: HttpURLConnection? = null
    return try {
        connection = (URL(url).openConnection() as HttpURLConnection).apply {
                    instanceFollowRedirects = true
                    connectTimeout = 5_000
                    readTimeout = 5_000
                    requestMethod = "GET"
                    setRequestProperty("User-Agent", previewUserAgent())
                }
        connection.connect()
        connection.url?.toString()
    } catch (_: Exception) {
        null
    } finally {
        connection?.disconnect()
    }
}
```

#### Qué hace y por qué existe

Resuelve un valor final a partir del contexto y las reglas de prioridad/fallback definidas en el cuerpo.

#### Contrato de la declaración

**Parámetros:**

- `url: String` — `url` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `String?`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 1215 | `var connection` | `HttpURLConnection?` | `null` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo declarado es `HttpURLConnection?`. Admite `null`, así que los consumidores deben contemplar ausencia de valor. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 1212 | `if (!safeHost(url).equals("redd.it", ignoreCase = true) && !safeHost(url).endsWith(".redd.it", ignoreCase = true)) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1213 | `return url` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 1216 | `return try {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 2.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `safeHost`, `equals`, `endsWith`, `URL`, `openConnection`, `setRequestProperty`, `previewUserAgent`, `connection.connect`, `toString`, `disconnect`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.56 `enrichSocialHtmlPreview` — fun, líneas 1233–1252

```kotlin
private fun enrichSocialHtmlPreview(originalUrl: String, pageUrl: String, html: String, base: LinkPreviewData,
    providerPreview: LinkPreviewData?): LinkPreviewData {
    val candidates = buildList {
            base.imageUrl?.let(::add)
            providerPreview?.imageUrl?.let(::add)
            addAll(base.imageCandidates)
            addAll(providerPreview?.imageCandidates.orEmpty())
            addAll(extractSocialImageCandidates(pageUrl = pageUrl, html = html))
        }.filter { it.startsWith("http", ignoreCase = true) }.distinct()
    val provider = when {
            isInstagramRelatedUrl(originalUrl) || isInstagramRelatedUrl(pageUrl) -> "Instagram"
            isThreadsRelatedUrl(originalUrl) || isThreadsRelatedUrl(pageUrl) -> "Threads"
            isFacebookRelatedUrl(originalUrl) || isFacebookRelatedUrl(pageUrl) -> "Facebook"
            isRedditRelatedUrl(originalUrl) || isRedditRelatedUrl(pageUrl) -> "Reddit"
            else -> null
        }
    return base.copy(url = originalUrl, title = base.title?.takeIf { it.isNotBlank() }?: providerPreview?.title, description =
            base.description?.takeIf { it.isNotBlank() }?: providerPreview?.description, imageUrl = candidates.firstOrNull(), siteName =
            providerPreview?.siteName?.takeIf { it.isNotBlank() }?: provider?: base.siteName, imageCandidates = candidates)
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `originalUrl: String` — `originalUrl` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.
- `pageUrl: String` — `pageUrl` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.
- `html: String` — `html` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.
- `base: LinkPreviewData` — `base` recibe un valor de tipo `LinkPreviewData`. El contrato no marca este parámetro como anulable.
- `providerPreview: LinkPreviewData?` — `providerPreview` recibe un valor de tipo `LinkPreviewData?`. El contrato permite `null`; el bloque debe decidir explícita o implícitamente qué significa la ausencia.

**Retorno:** `LinkPreviewData`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 1235 | `val candidates` | `inferido` | `buildList {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1242 | `val provider` | `inferido` | `when {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 1243 | `isInstagramRelatedUrl(originalUrl) \|\| isInstagramRelatedUrl(pageUrl) -> "Instagram"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 1244 | `isThreadsRelatedUrl(originalUrl) \|\| isThreadsRelatedUrl(pageUrl) -> "Threads"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 1245 | `isFacebookRelatedUrl(originalUrl) \|\| isFacebookRelatedUrl(pageUrl) -> "Facebook"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 1246 | `isRedditRelatedUrl(originalUrl) \|\| isRedditRelatedUrl(pageUrl) -> "Reddit"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 1247 | `else -> null` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |
| 1249 | `return base.copy(url = originalUrl, title = base.title?.takeIf { it.isNotBlank() }?: providerPreview?.title, description =` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 10.
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 4.
- **Filtro condicional:** `takeIf` conserva el valor únicamente cuando el predicado se cumple; en caso contrario lo convierte en `null`. Apariciones en este bloque: 3.
- **Normalización vacía:** `orEmpty` transforma una referencia nula en una colección/cadena vacía, simplificando el consumo posterior. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `let`, `addAll`, `imageCandidates.orEmpty`, `extractSocialImageCandidates`, `it.startsWith`, `distinct`, `isInstagramRelatedUrl`, `isThreadsRelatedUrl`, `isFacebookRelatedUrl`, `isRedditRelatedUrl`, `base.copy`, `it.isNotBlank`, `candidates.firstOrNull`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.

### 4.57 `extractSocialImageCandidates` — fun, líneas 1255–1277

```kotlin
private fun extractSocialImageCandidates(pageUrl: String, html: String): List<String> {
    if (html.isBlank()) return emptyList()
    val normalized = html.replace("\\/", "/").replace("\\u002F", "/", ignoreCase = true).replace("\\u003A", ":", ignoreCase = true)
            .replace("\\u0026", "&", ignoreCase = true).replace("\\u003D", "=", ignoreCase = true)
            .replace("\\u0025", "%", ignoreCase = true).replace("&amp;", "&", ignoreCase = true).replace("&quot;", "\"", ignoreCase = true)
            .replace("&#39;", "'", ignoreCase = true)
    val explicit = mutableListOf<String>()
    val keyRegex = Regex(pattern =
                """(?i)[\"'](?:thumbnail_url|thumbnailUrl|display_url|displayUrl|image_url|imageUrl|contentUrl|content_url|poster_url|posterUrl|cover_url|coverUrl)[\"']\s*:\s*[\"']([^\"']+)[\"']"""
        )
    keyRegex.findAll(normalized).forEach { match -> match.groupValues.getOrNull(1)?.let { resolveUrl(pageUrl, it) }?.let(explicit::add)
    }
    val discovered = UrlRegex.findAll(normalized).map { it.value.trimEnd('.', ',', ';', ')', ']', '}') }
            .mapNotNull { resolveUrl(pageUrl, it) }.filter { candidate -> val lower = candidate.lowercase(Locale.ROOT)
                lower.contains("fbcdn.net") || lower.contains("cdninstagram.com") ||
                    lower.contains("instagram.com") && looksLikeRemoteImageUrl(lower) || lower.contains("threadscdn") ||
                    lower.contains("redd.it") || lower.contains("redditmedia.com") || lower.contains("redditstatic.com") ||
                    lower.contains("douyinpic.com") || lower.contains("byteimg.com") || lower.contains("douyincdn.com") ||
                    lower.contains("pstatp.com") || looksLikeRemoteImageUrl(lower)
            }.toList()
    return (explicit + discovered).asSequence().filter { it.startsWith("http", ignoreCase = true) }.distinct()
        .sortedByDescending(::scoreSocialImageCandidate).take(16).toList()
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `pageUrl: String` — `pageUrl` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.
- `html: String` — `html` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `List<String>`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 1257 | `val normalized` | `inferido` | `html.replace("\\/", "/").replace("\\u002F", "/", ignoreCase = true).replace("\\u003A", ":", ignoreCa…` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1261 | `val explicit` | `inferido` | `mutableListOf<String>()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1262 | `val keyRegex` | `inferido` | `Regex(pattern =` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1267 | `val discovered` | `inferido` | `UrlRegex.findAll(normalized).map { it.value.trimEnd('.', ',', ';', ')', ']', '}') }` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 1256 | `if (html.isBlank()) return emptyList()` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1275 | `return (explicit + discovered).asSequence().filter { it.startsWith("http", ignoreCase = true) }.distinct()` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 2.
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

- **Persistencia Room/DAO:** Realiza una operación de persistencia o modificación a través de una capa de datos/DAO.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `html.isBlank`, `emptyList`, `html.replace`, `replace`, `Regex`, `keyRegex.findAll`, `match.groupValues.getOrNull`, `resolveUrl`, `let`, `UrlRegex.findAll`, `it.value.trimEnd`, `candidate.lowercase`, `lower.contains`, `looksLikeRemoteImageUrl`, `toList`, `asSequence`, `it.startsWith`, `distinct`, `sortedByDescending`, `take`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.58 `looksLikeRemoteImageUrl` — fun, líneas 1279–1281

```kotlin
private fun looksLikeRemoteImageUrl(url: String): Boolean = url.contains(".jpg") || url.contains(".jpeg") || url.contains(".png") ||
        url.contains(".webp") || url.contains(".gif") || url.contains("/image") || url.contains("thumbnail") || url.contains("preview") ||
        url.contains("cover")
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `url: String` — `url` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `Boolean`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `url.contains`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.59 `scoreSocialImageCandidate` — fun, líneas 1282–1304

```kotlin
private fun scoreSocialImageCandidate(url: String): Int {
    val lower = url.lowercase(Locale.ROOT)
    var score = 0
    if (lower.contains("thumbnail")) score += 80
    if (lower.contains("display")) score += 70
    if (lower.contains("preview")) score += 65
    if (lower.contains("cover")) score += 60
    if (lower.contains("media")) score += 30
    if (lower.contains("fbcdn.net")) score += 35
    if (lower.contains("cdninstagram.com")) score += 35
    if (lower.contains("preview.redd.it")) score += 40
    if (lower.contains("i.redd.it")) score += 35
    if (lower.contains("douyinpic.com")) score += 35
    if (lower.contains("byteimg.com")) score += 30
    if (lower.contains("avatar")) score -= 120
    if (lower.contains("profile_pic")) score -= 120
    if (lower.contains("profilepic")) score -= 120
    if (lower.contains("favicon")) score -= 150
    if (lower.contains("emoji")) score -= 140
    if (lower.contains("icon")) score -= 80
    if (lower.contains("logo")) score -= 90
    return score
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `url: String` — `url` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `Int`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 1283 | `val lower` | `inferido` | `url.lowercase(Locale.ROOT)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1284 | `var score` | `inferido` | `0` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 1285 | `if (lower.contains("thumbnail")) score += 80` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1286 | `if (lower.contains("display")) score += 70` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1287 | `if (lower.contains("preview")) score += 65` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1288 | `if (lower.contains("cover")) score += 60` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1289 | `if (lower.contains("media")) score += 30` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1290 | `if (lower.contains("fbcdn.net")) score += 35` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1291 | `if (lower.contains("cdninstagram.com")) score += 35` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1292 | `if (lower.contains("preview.redd.it")) score += 40` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1293 | `if (lower.contains("i.redd.it")) score += 35` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1294 | `if (lower.contains("douyinpic.com")) score += 35` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1295 | `if (lower.contains("byteimg.com")) score += 30` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1296 | `if (lower.contains("avatar")) score -= 120` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1297 | `if (lower.contains("profile_pic")) score -= 120` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1298 | `if (lower.contains("profilepic")) score -= 120` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1299 | `if (lower.contains("favicon")) score -= 150` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1300 | `if (lower.contains("emoji")) score -= 140` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1301 | `if (lower.contains("icon")) score -= 80` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1302 | `if (lower.contains("logo")) score -= 90` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1303 | `return score` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `url.lowercase`, `lower.contains`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.60 `isTikTokRelatedUrl` — fun, líneas 1306–1313

```kotlin
private fun isTikTokRelatedUrl(url: String): Boolean {
    val host = try {
            Uri.parse(url).host?.lowercase(Locale.ROOT).orEmpty()
        } catch (_: Exception) {
            ""
        }
    return host == "tiktok.com" || host.endsWith(".tiktok.com")
}
```

#### Qué hace y por qué existe

Evalúa una condición y devuelve/representa una decisión booleana utilizada por otras ramas del flujo.

#### Contrato de la declaración

**Parámetros:**

- `url: String` — `url` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `Boolean`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 1307 | `val host` | `inferido` | `try {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 1312 | `return host == "tiktok.com" \|\| host.endsWith(".tiktok.com")` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 1.
- **Normalización vacía:** `orEmpty` transforma una referencia nula en una colección/cadena vacía, simplificando el consumo posterior. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `Uri.parse`, `lowercase`, `orEmpty`, `host.endsWith`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.61 `fetchTikTokOEmbed` — fun, líneas 1321–1353

```kotlin
private fun fetchTikTokOEmbed(entityUrl: String, clickUrl: String): LinkPreviewData? {
    var connection: HttpURLConnection? = null
    return try {
        val endpoint = "https://www.tiktok.com/oembed?url=" + URLEncoder.encode(entityUrl, Charsets.UTF_8.name())
        connection = (URL(endpoint).openConnection() as HttpURLConnection).apply {
                    instanceFollowRedirects = true
                    connectTimeout = 5_000
                    readTimeout = 6_000
                    requestMethod = "GET"
                    setRequestProperty("User-Agent", previewUserAgent())
                    setRequestProperty("Accept", "application/json,text/plain,*/*")
                    setRequestProperty("Referer", "https://www.tiktok.com/")
                }
        connection.connect()
        if (connection.responseCode !in 200..299) {
            return null
        }
        val body = connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
        val json = JSONObject(body)
        val thumbnail = json.optString("thumbnail_url").takeIf { it.isNotBlank() }
        val title = json.optString("title").cleanText().takeIf { it.isNotBlank() }
        val author = json.optString("author_name").cleanText().takeIf { it.isNotBlank() }
        if (thumbnail == null && title == null) {
            return null
        }
        LinkPreviewData(url = clickUrl, title = title ?: author ?: "TikTok", description = author, imageUrl = thumbnail,
            siteName = "TikTok", host = safeHost(clickUrl))
    } catch (_: Exception) {
        null
    } finally {
        connection?.disconnect()
    }
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `entityUrl: String` — `entityUrl` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.
- `clickUrl: String` — `clickUrl` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `LinkPreviewData?`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 1322 | `var connection` | `HttpURLConnection?` | `null` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo declarado es `HttpURLConnection?`. Admite `null`, así que los consumidores deben contemplar ausencia de valor. |
| 1324 | `val endpoint` | `inferido` | `"https://www.tiktok.com/oembed?url=" + URLEncoder.encode(entityUrl, Charsets.UTF_8.name())` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1338 | `val body` | `inferido` | `connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1339 | `val json` | `inferido` | `JSONObject(body)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1340 | `val thumbnail` | `inferido` | `json.optString("thumbnail_url").takeIf { it.isNotBlank() }` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1341 | `val title` | `inferido` | `json.optString("title").cleanText().takeIf { it.isNotBlank() }` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1342 | `val author` | `inferido` | `json.optString("author_name").cleanText().takeIf { it.isNotBlank() }` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 1323 | `return try {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 1335 | `if (connection.responseCode !in 200..299) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1336 | `return null` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 1343 | `if (thumbnail == null && title == null) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1344 | `return null` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 1.
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 2.
- **Filtro condicional:** `takeIf` conserva el valor únicamente cuando el predicado se cumple; en caso contrario lo convierte en `null`. Apariciones en este bloque: 3.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `URLEncoder.encode`, `Charsets.UTF_8.name`, `URL`, `openConnection`, `setRequestProperty`, `previewUserAgent`, `connection.connect`, `connection.inputStream.bufferedReader`, `it.readText`, `JSONObject`, `json.optString`, `it.isNotBlank`, `cleanText`, `LinkPreviewData`, `safeHost`, `disconnect`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.62 `isSpotifyRelatedUrl` — fun, líneas 1366–1373

```kotlin
private fun isSpotifyRelatedUrl(url: String): Boolean {
    val host = try {
            Uri.parse(url).host?.lowercase(Locale.ROOT).orEmpty()
        } catch (_: Exception) {
            ""
        }
    return host == "spotify.link" || host == "spoti.fi" || host == "spotify.com" || host.endsWith(".spotify.com")
}
```

#### Qué hace y por qué existe

Evalúa una condición y devuelve/representa una decisión booleana utilizada por otras ramas del flujo.

#### Contrato de la declaración

**Parámetros:**

- `url: String` — `url` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `Boolean`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 1367 | `val host` | `inferido` | `try {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 1372 | `return host == "spotify.link" \|\| host == "spoti.fi" \|\| host == "spotify.com" \|\| host.endsWith(".spotify.com")` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 1.
- **Normalización vacía:** `orEmpty` transforma una referencia nula en una colección/cadena vacía, simplificando el consumo posterior. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `Uri.parse`, `lowercase`, `orEmpty`, `host.endsWith`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.63 `findSpotifyEntityUrl` — fun, líneas 1375–1391

```kotlin
private fun findSpotifyEntityUrl(url: String): String? {
    normalizeSpotifyEntityUrl(url)?.let {
        return it
    }
    val decoded = decodeUrlRepeatedly(url)
    normalizeSpotifyEntityUrl(decoded)?.let {
        return it
    }
    return try {
        val uri = Uri.parse(url)
        listOf("\$full_url", "\$fallback_url", "full_url", "fallback_url", "url", "deep_link_value", "canonical_url"
        ).firstNotNullOfOrNull { key -> uri.getQueryParameter(key)?.let(::decodeUrlRepeatedly)?.let(::normalizeSpotifyEntityUrl)
        }
    } catch (_: Exception) {
        null
    }
}
```

#### Qué hace y por qué existe

Modelo persistente de Room: define los campos que se almacenan y las restricciones de la entidad.

#### Contrato de la declaración

**Parámetros:**

- `url: String` — `url` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `String?`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 1379 | `val decoded` | `inferido` | `decodeUrlRepeatedly(url)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1384 | `val uri` | `inferido` | `Uri.parse(url)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Representa una ubicación Android `Uri`; no debe asumirse que siempre corresponde a una ruta de archivo convencional. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 1377 | `return it` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 1381 | `return it` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 1383 | `return try {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 4.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `normalizeSpotifyEntityUrl`, `decodeUrlRepeatedly`, `Uri.parse`, `listOf`, `uri.getQueryParameter`, `let`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.64 `findSpotifyEntityUrlInText` — fun, líneas 1393–1415

```kotlin
private fun findSpotifyEntityUrlInText(text: String): String? {
    val variants = linkedSetOf(text, decodeHtml(text), text.replace("\\/", "/").replace("\\u002F", "/", ignoreCase = true)
                .replace("\\u003A", ":", ignoreCase = true), decodeUrlRepeatedly(text))
    variants.forEach { candidateText -> val normalizedText = candidateText.replace("&amp;", "&", ignoreCase = true).replace("\\/", "/")
        val openUrl = Regex(pattern =
                    """https?://open\.spotify\.com/[^\s\"'<>\\]+""",
                option = RegexOption.IGNORE_CASE).find(normalizedText)?.value?.trimEnd('.', ',', ';', ')', ']', '}')
        if (openUrl != null) {
            normalizeSpotifyEntityUrl(openUrl)?.let {
                return it
            }
        }
        val spotifyUri = Regex(pattern =
                    """spotify:(track|album|artist|playlist|show|episode|audiobook):([A-Za-z0-9]+)""",
                option = RegexOption.IGNORE_CASE).find(normalizedText)
        if (spotifyUri != null) {
            val type = spotifyUri.groupValues[1].lowercase(Locale.ROOT)
            val id = spotifyUri.groupValues[2]
            return "https://open.spotify.com/$type/$id"
        }
    }
    return null
}
```

#### Qué hace y por qué existe

Modelo persistente de Room: define los campos que se almacenan y las restricciones de la entidad.

#### Contrato de la declaración

**Parámetros:**

- `text: String` — `text` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `String?`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 1394 | `val variants` | `inferido` | `linkedSetOf(text, decodeHtml(text), text.replace("\\/", "/").replace("\\u002F", "/", ignoreCase = tr…` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1397 | `val openUrl` | `inferido` | `Regex(pattern =` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1405 | `val spotifyUri` | `inferido` | `Regex(pattern =` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Representa una ubicación Android `Uri`; no debe asumirse que siempre corresponde a una ruta de archivo convencional. |
| 1409 | `val type` | `inferido` | `spotifyUri.groupValues[1].lowercase(Locale.ROOT)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1410 | `val id` | `inferido` | `spotifyUri.groupValues[2]` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 1400 | `if (openUrl != null) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1402 | `return it` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 1408 | `if (spotifyUri != null) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1411 | `return "https://open.spotify.com/$type/$id"` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 1414 | `return null` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 3.
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

- **Persistencia Room/DAO:** Realiza una operación de persistencia o modificación a través de una capa de datos/DAO.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `linkedSetOf`, `decodeHtml`, `text.replace`, `replace`, `decodeUrlRepeatedly`, `candidateText.replace`, `Regex`, `find`, `trimEnd`, `normalizeSpotifyEntityUrl`, `lowercase`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.65 `normalizeSpotifyEntityUrl` — fun, líneas 1417–1446

```kotlin
private fun normalizeSpotifyEntityUrl(candidate: String): String? {
    val cleaned = candidate.trim().trim('"', '\'', '`').replace("\\/", "/")
    if (cleaned.startsWith("spotify:", ignoreCase = true)) {
        val match = Regex(pattern =
                    """spotify:(track|album|artist|playlist|show|episode|audiobook):([A-Za-z0-9]+)""",
                option = RegexOption.IGNORE_CASE).find(cleaned)?: return null
        return "https://open.spotify.com/" + match.groupValues[1].lowercase(Locale.ROOT) + "/" + match.groupValues[2]
    }
    return try {
        val uri = Uri.parse(cleaned)
        val host = uri.host?.lowercase(Locale.ROOT).orEmpty()
        if (host != "open.spotify.com") {
            return null
        }
        val segments = uri.pathSegments
        val typeIndex = segments.indexOfFirst {
                it.lowercase(Locale.ROOT) in SpotifyEntityTypes
            }
        if (typeIndex < 0 || typeIndex + 1 >= segments.size) {
            return null
        }
        val type = segments[typeIndex].lowercase(Locale.ROOT)
        val id = segments[typeIndex + 1].substringBefore('?').substringBefore('#').takeIf { value -> value.isNotBlank() &&
                        value.all { it.isLetterOrDigit() }
                }?: return null
        "https://open.spotify.com/$type/$id"
    } catch (_: Exception) {
        null
    }
}
```

#### Qué hace y por qué existe

Normaliza una entrada a un conjunto de valores aceptados, proporcionando una salida estable aunque el dato original venga con variantes no canónicas.

#### Contrato de la declaración

**Parámetros:**

- `candidate: String` — `candidate` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `String?`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 1418 | `val cleaned` | `inferido` | `candidate.trim().trim('"', '\'', '`').replace("\\/", "/")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1420 | `val match` | `inferido` | `Regex(pattern =` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1426 | `val uri` | `inferido` | `Uri.parse(cleaned)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Representa una ubicación Android `Uri`; no debe asumirse que siempre corresponde a una ruta de archivo convencional. |
| 1427 | `val host` | `inferido` | `uri.host?.lowercase(Locale.ROOT).orEmpty()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. Normaliza una ausencia hacia un valor vacío/alternativo para simplificar el código consumidor. |
| 1431 | `val segments` | `inferido` | `uri.pathSegments` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1432 | `val typeIndex` | `inferido` | `segments.indexOfFirst {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1438 | `val type` | `inferido` | `segments[typeIndex].lowercase(Locale.ROOT)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1439 | `val id` | `inferido` | `segments[typeIndex + 1].substringBefore('?').substringBefore('#').takeIf { value -> value.isNotBlank…` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 1419 | `if (cleaned.startsWith("spotify:", ignoreCase = true)) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1423 | `return "https://open.spotify.com/" + match.groupValues[1].lowercase(Locale.ROOT) + "/" + match.groupValues[2]` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 1425 | `return try {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 1428 | `if (host != "open.spotify.com") {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1429 | `return null` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 1435 | `if (typeIndex < 0 \|\| typeIndex + 1 >= segments.size) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1436 | `return null` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 1.
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 2.
- **Filtro condicional:** `takeIf` conserva el valor únicamente cuando el predicado se cumple; en caso contrario lo convierte en `null`. Apariciones en este bloque: 1.
- **Normalización vacía:** `orEmpty` transforma una referencia nula en una colección/cadena vacía, simplificando el consumo posterior. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

- **Persistencia Room/DAO:** Realiza una operación de persistencia o modificación a través de una capa de datos/DAO.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `candidate.trim`, `trim`, `replace`, `cleaned.startsWith`, `Regex`, `find`, `lowercase`, `Uri.parse`, `orEmpty`, `it.lowercase`, `substringBefore`, `value.isNotBlank`, `it.isLetterOrDigit`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.66 `decodeUrlRepeatedly` — fun, líneas 1448–1462

```kotlin
private fun decodeUrlRepeatedly(value: String): String {
    var current = value
    repeat(3) {
        val next = try {
                URLDecoder.decode(current, Charsets.UTF_8.name())
            } catch (_: Exception) {
                current
            }
        if (next == current) {
            return current
        }
        current = next
    }
    return current
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

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 1449 | `var current` | `inferido` | `value` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1451 | `val next` | `inferido` | `try {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 1456 | `if (next == current) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1457 | `return current` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 1461 | `return current` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `repeat`, `URLDecoder.decode`, `Charsets.UTF_8.name`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.67 `fetchSpotifyOEmbed` — fun, líneas 1464–1498

```kotlin
private fun fetchSpotifyOEmbed(entityUrl: String, clickUrl: String): LinkPreviewData? {
    var connection: HttpURLConnection? = null
    return try {
        val endpoint = "https://open.spotify.com/oembed?url=" + URLEncoder.encode(entityUrl, Charsets.UTF_8.name())
        connection = (URL(endpoint).openConnection() as HttpURLConnection).apply {
                    instanceFollowRedirects = true
                    connectTimeout = 6_000
                    readTimeout = 7_000
                    requestMethod = "GET"
                    setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 10) AppleWebKit/537.36 " +
                            "(KHTML, like Gecko) Chrome/124.0 Mobile Safari/537.36")
                    setRequestProperty("Accept", "application/json,text/plain,*/*")
                }
        connection.connect()
        if (connection.responseCode !in 200..299) {
            return null
        }
        val body = connection.inputStream.use { input -> readLimitedHtml(InputStreamReader(input, Charsets.UTF_8))
            }
        val json = JSONObject(body)
        val title = json.optString("title").takeIf { it.isNotBlank() }
        val author = json.optString("author_name").takeIf { it.isNotBlank() }
        val thumbnail = json.optString("thumbnail_url").takeIf { it.isNotBlank() }
        val provider = json.optString("provider_name").takeIf { it.isNotBlank() }?: "Spotify"
        if (title == null && thumbnail == null) {
            return null
        }
        LinkPreviewData(url = clickUrl, title = title, description = author, imageUrl = thumbnail, siteName = provider,
            host = "open.spotify.com")
    } catch (_: Exception) {
        null
    } finally {
        connection?.disconnect()
    }
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `entityUrl: String` — `entityUrl` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.
- `clickUrl: String` — `clickUrl` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `LinkPreviewData?`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 1465 | `var connection` | `HttpURLConnection?` | `null` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo declarado es `HttpURLConnection?`. Admite `null`, así que los consumidores deben contemplar ausencia de valor. |
| 1467 | `val endpoint` | `inferido` | `"https://open.spotify.com/oembed?url=" + URLEncoder.encode(entityUrl, Charsets.UTF_8.name())` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1481 | `val body` | `inferido` | `connection.inputStream.use { input -> readLimitedHtml(InputStreamReader(input, Charsets.UTF_8))` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1483 | `val json` | `inferido` | `JSONObject(body)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1484 | `val title` | `inferido` | `json.optString("title").takeIf { it.isNotBlank() }` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1485 | `val author` | `inferido` | `json.optString("author_name").takeIf { it.isNotBlank() }` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1486 | `val thumbnail` | `inferido` | `json.optString("thumbnail_url").takeIf { it.isNotBlank() }` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1487 | `val provider` | `inferido` | `json.optString("provider_name").takeIf { it.isNotBlank() }?: "Spotify"` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 1466 | `return try {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 1478 | `if (connection.responseCode !in 200..299) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1479 | `return null` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 1488 | `if (title == null && thumbnail == null) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1489 | `return null` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 1.
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 1.
- **Filtro condicional:** `takeIf` conserva el valor únicamente cuando el predicado se cumple; en caso contrario lo convierte en `null`. Apariciones en este bloque: 4.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `URLEncoder.encode`, `Charsets.UTF_8.name`, `URL`, `openConnection`, `setRequestProperty`, `connection.connect`, `readLimitedHtml`, `InputStreamReader`, `JSONObject`, `json.optString`, `it.isNotBlank`, `LinkPreviewData`, `disconnect`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.68 `LinkPreviewData` — fun, líneas 1500–1517

```kotlin
private fun LinkPreviewData.withKnownProviderFallback(): LinkPreviewData {
    if (!imageUrl.isNullOrBlank()) {
        return this
    }
    val youtubeId = youtubeVideoId(url)
    return when {
        youtubeId != null -> copy(imageUrl = "https://i.ytimg.com/vi/$youtubeId/hqdefault.jpg", siteName = siteName
                        ?.takeIf { it.isNotBlank() }?: "YouTube")
        isSpotifyRelatedUrl(url) -> copy(siteName = "Spotify", title = title?.takeIf { it.isNotBlank() }?: "Spotify")
        isTikTokRelatedUrl(url) -> copy(siteName = "TikTok", title = title?.takeIf { it.isNotBlank() }?: "TikTok")
        isDouyinRelatedUrl(url) -> copy(siteName = "Douyin", title = title?.takeIf { it.isNotBlank() }?: "Douyin")
        isInstagramRelatedUrl(url) -> copy(siteName = "Instagram", title = title?.takeIf { it.isNotBlank() } ?: "Instagram")
        isThreadsRelatedUrl(url) -> copy(siteName = "Threads", title = title?.takeIf { it.isNotBlank() } ?: "Threads")
        isFacebookRelatedUrl(url) -> copy(siteName = "Facebook", title = title?.takeIf { it.isNotBlank() } ?: "Facebook")
        isRedditRelatedUrl(url) -> copy(siteName = "Reddit", title = title?.takeIf { it.isNotBlank() } ?: "Reddit")
        else -> this
    }
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:** ninguno explícito.

**Retorno:** `LinkPreviewData`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 1504 | `val youtubeId` | `inferido` | `youtubeVideoId(url)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 1501 | `if (!imageUrl.isNullOrBlank()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1502 | `return this` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 1505 | `return when {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 1506 | `youtubeId != null -> copy(imageUrl = "https://i.ytimg.com/vi/$youtubeId/hqdefault.jpg", siteName = siteName` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 1508 | `isSpotifyRelatedUrl(url) -> copy(siteName = "Spotify", title = title?.takeIf { it.isNotBlank() }?: "Spotify")` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 1509 | `isTikTokRelatedUrl(url) -> copy(siteName = "TikTok", title = title?.takeIf { it.isNotBlank() }?: "TikTok")` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 1510 | `isDouyinRelatedUrl(url) -> copy(siteName = "Douyin", title = title?.takeIf { it.isNotBlank() }?: "Douyin")` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 1511 | `isInstagramRelatedUrl(url) -> copy(siteName = "Instagram", title = title?.takeIf { it.isNotBlank() } ?: "Instagram")` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 1512 | `isThreadsRelatedUrl(url) -> copy(siteName = "Threads", title = title?.takeIf { it.isNotBlank() } ?: "Threads")` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 1513 | `isFacebookRelatedUrl(url) -> copy(siteName = "Facebook", title = title?.takeIf { it.isNotBlank() } ?: "Facebook")` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 1514 | `isRedditRelatedUrl(url) -> copy(siteName = "Reddit", title = title?.takeIf { it.isNotBlank() } ?: "Reddit")` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 1515 | `else -> this` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 8.
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 8.
- **Filtro condicional:** `takeIf` conserva el valor únicamente cuando el predicado se cumple; en caso contrario lo convierte en `null`. Apariciones en este bloque: 8.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `withKnownProviderFallback`, `imageUrl.isNullOrBlank`, `youtubeVideoId`, `copy`, `it.isNotBlank`, `isSpotifyRelatedUrl`, `isTikTokRelatedUrl`, `isDouyinRelatedUrl`, `isInstagramRelatedUrl`, `isThreadsRelatedUrl`, `isFacebookRelatedUrl`, `isRedditRelatedUrl`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.

### 4.69 `LinkPreviewCard` — fun, líneas 1520–1612

```kotlin
fun LinkPreviewCard(url: String, modifier: Modifier = Modifier, compact: Boolean = false, textColorMode: String = "auto",
    deferLoad: Boolean = false) {
    val context = LocalContext.current
    /*
     * La composición solo consulta RAM. La lectura persistida, el JSON, la
     * red y la caché de miniaturas se ejecutan desde la corrutina del efecto.
     * Si la tarjeta aparece durante un scroll rápido, deferLoad evita iniciar
     * trabajo nuevo hasta que el grid vuelva a estar en reposo. Un preview ya
     * cargado permanece visible porque el estado recordado no se reinicia.
     */
    var preview by remember(url) {
        mutableStateOf(LinkPreviewRepository.peekMemory(url) ?: LinkPreviewData.basic(url))
    }
    LaunchedEffect(url, deferLoad) {
        if (!deferLoad) {
            preview = LinkPreviewRepository.load(context = context.applicationContext, url = url)
        }
    }
    val title = preview.title?.takeIf { it.isNotBlank() }?: preview.siteName?.takeIf { it.isNotBlank() }?: preview.host
    val subtitle = preview.description?.takeIf { it.isNotBlank() }
    val imageModel: Any? = preview.cachedImagePath?.let(::File)?.takeIf { it.isFile && it.length() > 0L }
            // Si el guardado local falla (CDN con hot-link protection), Coil
            // todavía puede intentar cargar directamente la URL remota.
            ?: preview.imageUrl
    val previewBackground = MaterialTheme.colorScheme.surfaceContainer
    val placeholderBackground = MaterialTheme.colorScheme.surfaceContainerHigh
    val previewPrimaryText = resolveUiTextColor(value = textColorMode, background = previewBackground)
    val previewSecondaryText = resolveSecondaryUiTextColor(value = textColorMode, background = previewBackground)
    val previewGraphicColor = resolveUiGraphicColor(value = textColorMode, background = previewBackground)
    val placeholderTextColor = resolveUiTextColor(value = textColorMode, background = placeholderBackground)
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val veryCompact = compact && maxWidth < 170.dp
        val narrowCompact = compact && maxWidth < 220.dp
        Surface(onClick = {
                UiSoundPlayer.playAction(context = context, action = UiActionSound.Link)
                openExternalLink(context = context, url = preview.url)
            }, modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium, color = previewBackground, tonalElevation = 1.dp) {
            if (compact) {
                when {
                    veryCompact -> {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Surface(modifier = Modifier.fillMaxWidth().aspectRatio(16f / 9f), color = placeholderBackground) {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    if (imageModel != null) {
                                        AsyncImage(model = imageModel, contentDescription = null, modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop)
                                    } else {
                                        Text(text = (preview.siteName ?: preview.host).take(1).uppercase(), color = placeholderTextColor,
                                            style = MaterialTheme.typography.titleMedium)
                                    }
                                }
                            }
                            LinkPreviewText(title = title, subtitle = subtitle, siteName = preview.siteName ?: preview.host, compact = true,
                                veryCompact = true, showSubtitle = false, primaryTextColor = previewPrimaryText,
                                secondaryTextColor = previewSecondaryText, graphicColor = previewGraphicColor, modifier =
                                    Modifier.padding(10.dp))
                        }
                    }
                    else -> {
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Surface(modifier = Modifier.width(if (narrowCompact) 84.dp else 112.dp).height(
                                            if (narrowCompact) 68.dp else 86.dp), color = placeholderBackground) {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    if (imageModel != null) {
                                        AsyncImage(model = imageModel, contentDescription = null, modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop)
                                    } else {
                                        Text(text = (preview.siteName ?: preview.host).take(1).uppercase(), color = placeholderTextColor,
                                            style = MaterialTheme.typography.titleMedium)
                                    }
                                }
                            }
                            LinkPreviewText(title = title, subtitle = subtitle, siteName = preview.siteName ?: preview.host, compact = true,
                                veryCompact = narrowCompact, showSubtitle = !narrowCompact, primaryTextColor = previewPrimaryText,
                                secondaryTextColor = previewSecondaryText, graphicColor = previewGraphicColor, modifier = Modifier
                                        .weight(1f).padding(horizontal = 10.dp, vertical = 9.dp))
                        }
                    }
                }
            } else {
                Column(modifier = Modifier.fillMaxWidth()) {
                    if (imageModel != null) {
                        AsyncImage(model = imageModel, contentDescription = null, modifier = Modifier.fillMaxWidth().height(170.dp),
                            contentScale = ContentScale.Crop)
                    }
                    LinkPreviewText(title = title, subtitle = subtitle, siteName = preview.siteName ?: preview.host, compact = false,
                        primaryTextColor = previewPrimaryText, secondaryTextColor = previewSecondaryText,
                        graphicColor = previewGraphicColor, modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp))
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

- `url: String` — `url` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.
- `modifier: Modifier = Modifier` — `modifier` recibe un valor de tipo `Modifier`. El contrato no marca este parámetro como anulable. Tiene valor por defecto `Modifier`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada.
- `compact: Boolean = false` — `compact` recibe un valor de tipo `Boolean`. El contrato no marca este parámetro como anulable. Tiene valor por defecto `false`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada.
- `textColorMode: String = "auto"` — `textColorMode` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable. Tiene valor por defecto `"auto"`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada. Aunque el tipo sea `String`, semánticamente suele funcionar como clave/enumeración textual; las ramas del código delimitan los valores reconocidos.
- `deferLoad: Boolean = false` — `deferLoad` recibe un valor de tipo `Boolean`. El contrato no marca este parámetro como anulable. Tiene valor por defecto `false`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `@Composable` convierte la función en una unidad declarativa de Compose y la somete a reglas de recomposición.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 1522 | `val context` | `inferido` | `LocalContext.current` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El `Context` da acceso a recursos/servicios/almacenamiento Android; debe respetarse el ciclo de vida del contexto recibido. |
| 1530 | `var preview` | `inferido` | `by remember(url) {` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 1538 | `val title` | `inferido` | `preview.title?.takeIf { it.isNotBlank() }?: preview.siteName?.takeIf { it.isNotBlank() }?: preview.h…` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 1539 | `val subtitle` | `inferido` | `preview.description?.takeIf { it.isNotBlank() }` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 1540 | `val imageModel` | `Any?` | `preview.cachedImagePath?.let(::File)?.takeIf { it.isFile && it.length() > 0L }` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo declarado es `Any?`. Admite `null`, así que los consumidores deben contemplar ausencia de valor. Actúa como selector de estrategia/perfil; su valor determina qué rama de configuración se aplica. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 1544 | `val previewBackground` | `inferido` | `MaterialTheme.colorScheme.surfaceContainer` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1545 | `val placeholderBackground` | `inferido` | `MaterialTheme.colorScheme.surfaceContainerHigh` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1546 | `val previewPrimaryText` | `inferido` | `resolveUiTextColor(value = textColorMode, background = previewBackground)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1547 | `val previewSecondaryText` | `inferido` | `resolveSecondaryUiTextColor(value = textColorMode, background = previewBackground)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1548 | `val previewGraphicColor` | `inferido` | `resolveUiGraphicColor(value = textColorMode, background = previewBackground)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1549 | `val placeholderTextColor` | `inferido` | `resolveUiTextColor(value = textColorMode, background = placeholderBackground)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1551 | `val veryCompact` | `inferido` | `compact && maxWidth < 170.dp` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1552 | `val narrowCompact` | `inferido` | `compact && maxWidth < 220.dp` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 1533 | `LaunchedEffect(url, deferLoad) {` | Efecto de Compose basado en corrutina: se inicia cuando entra en composición y se reinicia si cambian sus claves; se cancela al salir de composición. |
| 1534 | `if (!deferLoad) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1557 | `if (compact) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1558 | `when {` | Selección múltiple de Kotlin: concentra casos mutuamente evaluados y normalmente define una normalización o estrategia según el valor de entrada. |
| 1559 | `veryCompact -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 1563 | `if (imageModel != null) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1578 | `else -> {` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |
| 1581 | `if (narrowCompact) 68.dp else 86.dp), color = placeholderBackground) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1583 | `if (imageModel != null) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1601 | `if (imageModel != null) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 5.
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 9.
- **Filtro condicional:** `takeIf` conserva el valor únicamente cuando el predicado se cumple; en caso contrario lo convierte en `null`. Apariciones en este bloque: 4.

#### Semántica Compose/lifecycle

- **`remember`:** Conserva valores entre recomposiciones bajo las mismas claves.
- **`mutableStateOf`:** Crea estado observable; las lecturas registradas por Compose quedan enlazadas a sus cambios.
- **`LaunchedEffect`:** Ejecuta una corrutina ligada al ciclo de vida de la composición y a sus claves.
- **Callbacks `onClick`:** La lógica asociada se ejecuta por evento de usuario, no durante la composición inicial.
- **`Modifier`:** La cadena de modificadores define tamaño, interacción, dibujo, semántica o posicionamiento; el orden de los modificadores puede ser significativo.

#### Efectos secundarios y recursos

- **Estado Compose:** Declara o utiliza estado observable de Compose; una escritura en ese estado puede desencadenar recomposición de los lectores.
- **Sonido/háptica:** Produce feedback perceptible para el usuario; debe ejecutarse en respuesta a la interacción/estado correspondiente.
- **Caché:** Lee, llena, invalida o administra caché para evitar recomputación o decodificación repetida.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `remember`, `mutableStateOf`, `LinkPreviewRepository.peekMemory`, `LinkPreviewData.basic`, `LaunchedEffect`, `LinkPreviewRepository.load`, `it.isNotBlank`, `let`, `it.length`, `resolveUiTextColor`, `resolveSecondaryUiTextColor`, `resolveUiGraphicColor`, `BoxWithConstraints`, `modifier.fillMaxWidth`, `Surface`, `UiSoundPlayer.playAction`, `openExternalLink`, `Modifier.fillMaxWidth`, `Column`, `aspectRatio`, `Box`, `Modifier.fillMaxSize`, `AsyncImage`, `Text`, `take`, `uppercase`, `LinkPreviewText`, `Modifier.padding`, `Row`, `Modifier.width`, `height`, `weight`, `padding`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.
- No convertir estado de Compose en variables ordinarias sin considerar recomposición y persistencia temporal.
- Mantener coherencia entre creación, clave, lectura e invalidación de caché; cambiar solo una de esas etapas puede dejar datos obsoletos.

### 4.70 `LinkPreviewText` — fun, líneas 1615–1644

```kotlin
private fun LinkPreviewText(title: String, subtitle: String?, siteName: String, compact: Boolean, veryCompact: Boolean = false,
    showSubtitle: Boolean = true, primaryTextColor: Color, secondaryTextColor: Color, graphicColor: Color, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.Center) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = siteName, modifier = Modifier.weight(1f), color = secondaryTextColor, style = MaterialTheme.typography.labelMedium,
                maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(modifier = Modifier.width(6.dp))
            Icon(imageVector = Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(
                        if (veryCompact) 13.dp else 15.dp), tint = graphicColor)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = title, color = primaryTextColor, style = when {
                    veryCompact -> MaterialTheme.typography.bodyLarge
                    compact -> MaterialTheme.typography.titleSmall
                    else -> MaterialTheme.typography.titleMedium
                }, fontWeight = FontWeight.SemiBold, maxLines = if (compact) 2 else 3, overflow = TextOverflow.Ellipsis)
        if (showSubtitle && !subtitle.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = subtitle, color = secondaryTextColor, style = if (veryCompact) {
                        MaterialTheme.typography.labelMedium
                    } else {
                        MaterialTheme.typography.bodySmall
                    }, maxLines = when {
                        veryCompact -> 1
                        compact -> 2
                        else -> 3
                    }, overflow = TextOverflow.Ellipsis)
        }
    }
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `title: String` — `title` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.
- `subtitle: String?` — `subtitle` recibe un valor de tipo `String?`. El contrato permite `null`; el bloque debe decidir explícita o implícitamente qué significa la ausencia.
- `siteName: String` — `siteName` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.
- `compact: Boolean` — `compact` recibe un valor de tipo `Boolean`. El contrato no marca este parámetro como anulable.
- `veryCompact: Boolean = false` — `veryCompact` recibe un valor de tipo `Boolean`. El contrato no marca este parámetro como anulable. Tiene valor por defecto `false`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada.
- `showSubtitle: Boolean = true` — `showSubtitle` recibe un valor de tipo `Boolean`. El contrato no marca este parámetro como anulable. Tiene valor por defecto `true`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada.
- `primaryTextColor: Color` — `primaryTextColor` recibe un valor de tipo `Color`. El contrato no marca este parámetro como anulable.
- `secondaryTextColor: Color` — `secondaryTextColor` recibe un valor de tipo `Color`. El contrato no marca este parámetro como anulable.
- `graphicColor: Color` — `graphicColor` recibe un valor de tipo `Color`. El contrato no marca este parámetro como anulable.
- `modifier: Modifier = Modifier` — `modifier` recibe un valor de tipo `Modifier`. El contrato no marca este parámetro como anulable. Tiene valor por defecto `Modifier`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.
- `@Composable` convierte la función en una unidad declarativa de Compose y la somete a reglas de recomposición.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 1623 | `if (veryCompact) 13.dp else 15.dp), tint = graphicColor)` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1627 | `veryCompact -> MaterialTheme.typography.bodyLarge` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 1628 | `compact -> MaterialTheme.typography.titleSmall` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 1629 | `else -> MaterialTheme.typography.titleMedium` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |
| 1631 | `if (showSubtitle && !subtitle.isNullOrBlank()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1638 | `veryCompact -> 1` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 1639 | `compact -> 2` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 1640 | `else -> 3` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |

**Restricciones concretas que aparecen en este bloque:**
- **Límite visual:** La UI impone un límite explícito de tamaño o cantidad de líneas para impedir crecimiento visual no controlado. Apariciones en este bloque: 3.

#### Semántica Compose/lifecycle

- **`Modifier`:** La cadena de modificadores define tamaño, interacción, dibujo, semántica o posicionamiento; el orden de los modificadores puede ser significativo.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `Column`, `Row`, `Text`, `Modifier.weight`, `Spacer`, `Modifier.width`, `Icon`, `Modifier.size`, `Modifier.height`, `subtitle.isNullOrBlank`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.

### 4.71 `openExternalLink` — fun, líneas 1646–1655

```kotlin
private fun openExternalLink(context: Context, url: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        context.startActivity(intent)
    } catch (_: Exception) {
        // Si no existe una app capaz de abrirlo, la nota sigue siendo usable.
    }
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `context: Context` — `context` recibe un valor de tipo `Context`. El contrato no marca este parámetro como anulable. Da acceso a recursos y APIs Android; su vida útil debe corresponder con el uso realizado.
- `url: String` — `url` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 1648 | `val intent` | `inferido` | `Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 1647 | `try {` | Frontera de manejo de errores: agrupa operaciones que pueden lanzar excepciones para permitir recuperación o limpieza controlada. |

#### Efectos secundarios y recursos

- **Navegación/Activity:** Modifica navegación, ciclo de vida o contenido de una Activity.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `Intent`, `Uri.parse`, `addFlags`, `context.startActivity`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.72 `readLimitedHtml` — fun, líneas 1657–1671

```kotlin
private fun readLimitedHtml(reader: InputStreamReader): String {
    reader.use {
        val buffer = CharArray(8_192)
        val builder = StringBuilder()
        while (builder.length < MAX_HTML_CHARS) {
            val remaining = MAX_HTML_CHARS - builder.length
            val count = it.read(buffer, 0, minOf(buffer.size, remaining))
            if (count <= 0) {
                break
            }
            builder.append(buffer, 0, count)
        }
        return builder.toString()
    }
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `reader: InputStreamReader` — `reader` recibe un valor de tipo `InputStreamReader`. El contrato no marca este parámetro como anulable.

**Retorno:** `String`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 1659 | `val buffer` | `inferido` | `CharArray(8_192)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1660 | `val builder` | `inferido` | `StringBuilder()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1662 | `val remaining` | `inferido` | `MAX_HTML_CHARS - builder.length` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1663 | `val count` | `inferido` | `it.read(buffer, 0, minOf(buffer.size, remaining))` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 1661 | `while (builder.length < MAX_HTML_CHARS) {` | Bucle condicionado: repite mientras la condición se mantenga verdadera; la lógica interna debe permitir progreso hacia la salida. |
| 1664 | `if (count <= 0) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1665 | `break` | Interrumpe el bucle más cercano; ninguna iteración posterior de ese bucle se ejecuta. |
| 1669 | `return builder.toString()` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `CharArray`, `StringBuilder`, `it.read`, `minOf`, `builder.append`, `builder.toString`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.73 `charsetFromContentType` — fun, líneas 1673–1685

```kotlin
private fun charsetFromContentType(contentType: String?): Charset {
    val charsetName = contentType?.substringAfter("charset=", missingDelimiterValue = "")?.substringBefore(';')?.trim()?.trim('"', '\'')
            .orEmpty()
    return try {
        if (charsetName.isNotBlank()) {
            Charset.forName(charsetName)
        } else {
            Charsets.UTF_8
        }
    } catch (_: Exception) {
        Charsets.UTF_8
    }
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `contentType: String?` — `contentType` recibe un valor de tipo `String?`. El contrato permite `null`; el bloque debe decidir explícita o implícitamente qué significa la ausencia. Aunque el tipo sea `String`, semánticamente suele funcionar como clave/enumeración textual; las ramas del código delimitan los valores reconocidos.

**Retorno:** `Charset`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 1674 | `val charsetName` | `inferido` | `contentType?.substringAfter("charset=", missingDelimiterValue = "")?.substringBefore(';')?.trim()?.t…` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 1676 | `return try {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 1677 | `if (charsetName.isNotBlank()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 4.
- **Normalización vacía:** `orEmpty` transforma una referencia nula en una colección/cadena vacía, simplificando el consumo posterior. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `substringAfter`, `substringBefore`, `trim`, `orEmpty`, `charsetName.isNotBlank`, `Charset.forName`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.74 `decodeHtml` — fun, líneas 1687–1688

```kotlin
private fun decodeHtml(value: String): String = Html.fromHtml(value, Html.FROM_HTML_MODE_LEGACY).toString().replace(Regex("\\s+"), " ")
        .trim()
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

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

- **Persistencia Room/DAO:** Realiza una operación de persistencia o modificación a través de una capa de datos/DAO.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `Html.fromHtml`, `toString`, `replace`, `Regex`, `trim`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.75 `String` — fun, líneas 1689–1690

```kotlin
private fun String.cleanText(): String = decodeHtml(this).take(500)
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:** ninguno explícito.

**Retorno:** `String`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `cleanText`, `decodeHtml`, `take`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.76 `firstNonBlank` — fun, líneas 1691–1693

```kotlin
private fun firstNonBlank(vararg values: String?): String? = values.firstOrNull {
        !it.isNullOrBlank()
    }
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `vararg values: String?` — `values` recibe un valor de tipo `String?`. El contrato permite `null`; el bloque debe decidir explícita o implícitamente qué significa la ausencia.

**Retorno:** `String?`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `it.isNullOrBlank`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.77 `resolveUrl` — fun, líneas 1694–1698

```kotlin
private fun resolveUrl(baseUrl: String, candidate: String): String? = try {
        URL(URL(baseUrl), candidate).toString()
    } catch (_: Exception) {
        null
    }
```

#### Qué hace y por qué existe

Resuelve un valor final a partir del contexto y las reglas de prioridad/fallback definidas en el cuerpo.

#### Contrato de la declaración

**Parámetros:**

- `baseUrl: String` — `baseUrl` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.
- `candidate: String` — `candidate` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `String?`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `URL`, `toString`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.78 `safeHost` — fun, líneas 1699–1703

```kotlin
private fun safeHost(url: String): String = try {
        URI(url).host?.removePrefix("www.")?.takeIf { it.isNotBlank() }?: url
    } catch (_: Exception) {
        url
    }
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `url: String` — `url` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `String`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 2.
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 1.
- **Filtro condicional:** `takeIf` conserva el valor únicamente cuando el predicado se cumple; en caso contrario lo convierte en `null`. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `URI`, `removePrefix`, `it.isNotBlank`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.79 `directImageUrl` — fun, líneas 1704–1711

```kotlin
private fun directImageUrl(url: String): String? {
    val cleanPath = url.substringBefore('#').substringBefore('?').lowercase(Locale.ROOT)
    return if (listOf(".jpg", ".jpeg", ".png", ".webp", ".gif", ".avif").any(cleanPath::endsWith)) {
        url
    } else {
        null
    }
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `url: String` — `url` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `String?`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 1705 | `val cleanPath` | `inferido` | `url.substringBefore('#').substringBefore('?').lowercase(Locale.ROOT)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 1706 | `return if (listOf(".jpg", ".jpeg", ".png", ".webp", ".gif", ".avif").any(cleanPath::endsWith)) {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `url.substringBefore`, `substringBefore`, `lowercase`, `listOf`, `any`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.80 `fileNameFromUrl` — fun, líneas 1713–1717

```kotlin
private fun fileNameFromUrl(url: String): String = try {
        URI(url).path?.substringAfterLast('/')?.takeIf { it.isNotBlank() }?: safeHost(url)
    } catch (_: Exception) {
        safeHost(url)
    }
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `url: String` — `url` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `String`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 2.
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 1.
- **Filtro condicional:** `takeIf` conserva el valor únicamente cuando el predicado se cumple; en caso contrario lo convierte en `null`. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `URI`, `substringAfterLast`, `it.isNotBlank`, `safeHost`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.81 `youtubeVideoId` — fun, líneas 1718–1739

```kotlin
private fun youtubeVideoId(url: String): String? = try {
        val uri = Uri.parse(url)
        val host = uri.host?.lowercase(Locale.ROOT).orEmpty()
        when {
            host == "youtu.be" -> uri.pathSegments.firstOrNull()?.takeIf { it.isNotBlank() }
            host.endsWith("youtube.com") -> {
                uri.getQueryParameter("v")?.takeIf { it.isNotBlank() }?: uri.pathSegments.let { segments -> val markerIndex =
                                segments.indexOfFirst {
                                    it == "shorts" || it == "embed"
                                }
                            if (markerIndex >= 0 && markerIndex + 1 < segments.size) {
                                segments[markerIndex + 1]
                            } else {
                                null
                            }
                        }
            }
            else -> null
        }
    } catch (_: Exception) {
        null
    }
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `url: String` — `url` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `String?`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 1719 | `val uri` | `inferido` | `Uri.parse(url)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Representa una ubicación Android `Uri`; no debe asumirse que siempre corresponde a una ruta de archivo convencional. |
| 1720 | `val host` | `inferido` | `uri.host?.lowercase(Locale.ROOT).orEmpty()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. Normaliza una ausencia hacia un valor vacío/alternativo para simplificar el código consumidor. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 1721 | `when {` | Selección múltiple de Kotlin: concentra casos mutuamente evaluados y normalmente define una normalización o estrategia según el valor de entrada. |
| 1722 | `host == "youtu.be" -> uri.pathSegments.firstOrNull()?.takeIf { it.isNotBlank() }` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 1723 | `host.endsWith("youtube.com") -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 1728 | `if (markerIndex >= 0 && markerIndex + 1 < segments.size) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1735 | `else -> null` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 3.
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 1.
- **Filtro condicional:** `takeIf` conserva el valor únicamente cuando el predicado se cumple; en caso contrario lo convierte en `null`. Apariciones en este bloque: 2.
- **Normalización vacía:** `orEmpty` transforma una referencia nula en una colección/cadena vacía, simplificando el consumo posterior. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `Uri.parse`, `lowercase`, `orEmpty`, `uri.pathSegments.firstOrNull`, `it.isNotBlank`, `host.endsWith`, `uri.getQueryParameter`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.

## 5. Inventario global de propiedades/variables detectadas

| Línea | Identificador | Mutabilidad | Tipo | Explicación |
|---:|---|---|---|---|
| 72 | `MAX_HTML_CHARS` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Está marcada `const`, por lo que es una constante de tiempo de compilación y solo puede usar un tipo/valor admitido por Kotlin para constantes. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 73 | `PREVIEW_CACHE_VERSION` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Forma parte de la estrategia de reutilización de resultados para evitar repetir trabajo costoso. Está marcada `const`, por lo que es una constante de tiempo de compilación y solo puede usar un tipo/valor admitido por Kotlin para constantes. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 74 | `PREVIEW_CACHE_MAX_AGE_MS` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Forma parte de la estrategia de reutilización de resultados para evitar repetir trabajo costoso. Está marcada `const`, por lo que es una constante de tiempo de compilación y solo puede usar un tipo/valor admitido por Kotlin para constantes. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 75 | `THUMBNAIL_MAX_BYTES` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Está marcada `const`, por lo que es una constante de tiempo de compilación y solo puede usar un tipo/valor admitido por Kotlin para constantes. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 76 | `THUMBNAIL_CACHE_MAX_BYTES` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Forma parte de la estrategia de reutilización de resultados para evitar repetir trabajo costoso. Está marcada `const`, por lo que es una constante de tiempo de compilación y solo puede usar un tipo/valor admitido por Kotlin para constantes. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 77 | `THUMBNAIL_CACHE_MAX_FILES` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Forma parte de la estrategia de reutilización de resultados para evitar repetir trabajo costoso. Está marcada `const`, por lo que es una constante de tiempo de compilación y solo puede usar un tipo/valor admitido por Kotlin para constantes. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 79 | `UrlRegex` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 93 | `trimmed` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 102 | `siteName` | `val` | `String?, val host: String, val imageCandidates: List<String>` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo declarado es `String?, val host: String, val imageCandidates: List<String>`. Admite `null`, así que los consumidores deben contemplar ausencia de valor. |
| 105 | `host` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 113 | `cache` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Es una caché acotada por política LRU: privilegia entradas recientes y permite expulsión automática cuando se supera el límite. Forma parte de la estrategia de reutilización de resultados para evitar repetir trabajo costoso. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 114 | `locks` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 115 | `legacyLoadGate` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 116 | `loadGate` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 139 | `gate` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 149 | `persisted` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 151 | `ready` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 156 | `fetched` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 157 | `ready` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 165 | `basic` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 172 | `socialProviderPreview` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 198 | `connection` | `var` | `HttpURLConnection?` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo declarado es `HttpURLConnection?`. Admite `null`, así que los consumidores deben contemplar ausencia de valor. |
| 214 | `responseCode` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 218 | `finalUrl` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 236 | `contentType` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. Normaliza una ausencia hacia un valor vacío/alternativo para simplificar el código consumidor. |
| 244 | `charset` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 245 | `html` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 247 | `parsed` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 249 | `enriched` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 256 | `spotifyEntityUrl` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 280 | `appContext` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El `Context` da acceso a recursos/servicios/almacenamiento Android; debe respetarse el ciclo de vida del contexto recibido. |
| 281 | `maxUrls` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 286 | `parallelism` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 287 | `pauseBetweenBatchesMs` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 316 | `json` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 325 | `key` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 326 | `raw` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 328 | `json` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 329 | `savedAt` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 334 | `cachedPath` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Forma parte de la estrategia de reutilización de resultados para evitar repetir trabajo costoso. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 335 | `imageCandidates` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 360 | `remoteCandidates` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 369 | `cacheDir` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Forma parte de la estrategia de reutilización de resultados para evitar repetir trabajo costoso. |
| 376 | `downloaded` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 386 | `connection` | `var` | `HttpURLConnection?` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo declarado es `HttpURLConnection?`. Admite `null`, así que los consumidores deben contemplar ausencia de valor. |
| 387 | `temp` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 409 | `declaredLength` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 413 | `total` | `var` | `inferido` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 416 | `count` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 449 | `header` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 450 | `count` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 452 | `jpeg` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 453 | `png` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 454 | `gif` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 455 | `webp` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 456 | `avif` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 465 | `host` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 478 | `files` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 480 | `totalBytes` | `var` | `inferido` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 489 | `digest` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 495 | `metadata` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 496 | `metaTagRegex` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 499 | `attributeRegex` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 504 | `value` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Normaliza una ausencia hacia un valor vacío/alternativo para simplificar el código consumidor. |
| 507 | `key` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 508 | `content` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 513 | `titleFromTag` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 517 | `host` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 518 | `rawImage` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 520 | `resolvedImage` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 521 | `title` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 522 | `description` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 523 | `siteName` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 536 | `host` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 552 | `current` | `var` | `inferido` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 553 | `cookies` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 555 | `connection` | `var` | `HttpURLConnection?` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo declarado es `HttpURLConnection?`. Admite `null`, así que los consumidores deben contemplar ausencia de valor. |
| 574 | `name` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 575 | `value` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 580 | `code` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 582 | `location` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 594 | `html` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 614 | `resolved` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 615 | `awemeId` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 616 | `share` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 620 | `api` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 625 | `awemeId` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 626 | `sharePreview` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 628 | `apiPreview` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 633 | `embeddedCover` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 634 | `candidates` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 647 | `sources` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 648 | `patterns` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 663 | `paths` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 665 | `connection` | `var` | `HttpURLConnection?` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo declarado es `HttpURLConnection?`. Admite `null`, así que los consumidores deben contemplar ausencia de valor. |
| 680 | `charset` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 681 | `html` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 683 | `router` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 684 | `item` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 685 | `preview` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 699 | `markerIndex` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 701 | `equalsIndex` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 703 | `index` | `var` | `inferido` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 709 | `raw` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 713 | `literal` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 714 | `wrapped` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 715 | `inner` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 727 | `depth` | `var` | `inferido` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 728 | `inString` | `var` | `inferido` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 729 | `escaped` | `var` | `inferido` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 731 | `char` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 756 | `escaped` | `var` | `inferido` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 758 | `char` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 774 | `found` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 781 | `keys` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 783 | `found` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 789 | `found` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 799 | `urls` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 812 | `video` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 813 | `coverCandidates` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 823 | `images` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 826 | `image` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 832 | `title` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 833 | `author` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 839 | `endpoints` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 858 | `body` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 859 | `root` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 860 | `item` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 861 | `video` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 862 | `coverCandidates` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 873 | `cover` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 874 | `title` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 875 | `author` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 891 | `list` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 893 | `value` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 902 | `candidates` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 908 | `score` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 920 | `keys` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 922 | `key` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 923 | `value` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 924 | `childPath` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 940 | `renderData` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 944 | `decoded` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 952 | `routerData` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 956 | `decoded` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 965 | `normalizedHtml` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 968 | `cdnUrlRegex` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 972 | `end` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El inicializador limita explícitamente el valor a un rango, evitando que dimensiones/intensidades/sizes inválidos se propaguen. |
| 973 | `context` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El `Context` da acceso a recursos/servicios/almacenamiento Android; debe respetarse el ciclo de vida del contexto recibido. |
| 984 | `current` | `var` | `inferido` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 986 | `next` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1000 | `normalized` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1003 | `results` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1015 | `normalizedPath` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1016 | `normalizedUrl` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1017 | `score` | `var` | `inferido` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1049 | `host` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1054 | `host` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1059 | `host` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1067 | `host` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1077 | `encoded` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1078 | `provider` | `val` | `String` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo declarado es `String`. No declara nulabilidad explícita. |
| 1079 | `endpoint` | `val` | `String` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo declarado es `String`. No declara nulabilidad explícita. |
| 1091 | `isVideo` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El nombre indica un estado de decisión/visibilidad/selección usado para controlar una rama de comportamiento o presentación. |
| 1093 | `type` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1098 | `connection` | `var` | `HttpURLConnection?` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo declarado es `HttpURLConnection?`. Admite `null`, así que los consumidores deben contemplar ausencia de valor. |
| 1113 | `body` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1114 | `json` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1115 | `html` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1116 | `thumbnail` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1118 | `htmlCandidates` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1119 | `candidates` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1123 | `title` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 1124 | `author` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1139 | `canonical` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 1140 | `clean` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1142 | `jsonUrl` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1143 | `connection` | `var` | `HttpURLConnection?` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo declarado es `HttpURLConnection?`. Admite `null`, así que los consumidores deben contemplar ausencia de valor. |
| 1158 | `body` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1159 | `root` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1160 | `post` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 1162 | `candidates` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1166 | `previewImages` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 1169 | `image` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 1171 | `resolutions` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1184 | `mediaMetadata` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1186 | `keys` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1188 | `media` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 1190 | `previews` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1198 | `title` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1199 | `subreddit` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1200 | `author` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1215 | `connection` | `var` | `HttpURLConnection?` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo declarado es `HttpURLConnection?`. Admite `null`, así que los consumidores deben contemplar ausencia de valor. |
| 1235 | `candidates` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1242 | `provider` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1257 | `normalized` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1261 | `explicit` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1262 | `keyRegex` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1267 | `discovered` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1283 | `lower` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1284 | `score` | `var` | `inferido` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1307 | `host` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1322 | `connection` | `var` | `HttpURLConnection?` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo declarado es `HttpURLConnection?`. Admite `null`, así que los consumidores deben contemplar ausencia de valor. |
| 1324 | `endpoint` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1338 | `body` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1339 | `json` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1340 | `thumbnail` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1341 | `title` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1342 | `author` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1355 | `SpotifyEntityTypes` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 1367 | `host` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1379 | `decoded` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1384 | `uri` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Representa una ubicación Android `Uri`; no debe asumirse que siempre corresponde a una ruta de archivo convencional. |
| 1394 | `variants` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1397 | `openUrl` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1405 | `spotifyUri` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Representa una ubicación Android `Uri`; no debe asumirse que siempre corresponde a una ruta de archivo convencional. |
| 1409 | `type` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1410 | `id` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1418 | `cleaned` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1420 | `match` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1426 | `uri` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Representa una ubicación Android `Uri`; no debe asumirse que siempre corresponde a una ruta de archivo convencional. |
| 1427 | `host` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. Normaliza una ausencia hacia un valor vacío/alternativo para simplificar el código consumidor. |
| 1431 | `segments` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1432 | `typeIndex` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1438 | `type` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1439 | `id` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1449 | `current` | `var` | `inferido` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1451 | `next` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1465 | `connection` | `var` | `HttpURLConnection?` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo declarado es `HttpURLConnection?`. Admite `null`, así que los consumidores deben contemplar ausencia de valor. |
| 1467 | `endpoint` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1481 | `body` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1483 | `json` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1484 | `title` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1485 | `author` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1486 | `thumbnail` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1487 | `provider` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 1504 | `youtubeId` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1522 | `context` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El `Context` da acceso a recursos/servicios/almacenamiento Android; debe respetarse el ciclo de vida del contexto recibido. |
| 1530 | `preview` | `var` | `inferido` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 1538 | `title` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 1539 | `subtitle` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 1540 | `imageModel` | `val` | `Any?` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo declarado es `Any?`. Admite `null`, así que los consumidores deben contemplar ausencia de valor. Actúa como selector de estrategia/perfil; su valor determina qué rama de configuración se aplica. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 1544 | `previewBackground` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1545 | `placeholderBackground` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1546 | `previewPrimaryText` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1547 | `previewSecondaryText` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1548 | `previewGraphicColor` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1549 | `placeholderTextColor` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1551 | `veryCompact` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1552 | `narrowCompact` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1648 | `intent` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1659 | `buffer` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1660 | `builder` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1662 | `remaining` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1663 | `count` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1674 | `charsetName` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 1705 | `cleanPath` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1719 | `uri` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Representa una ubicación Android `Uri`; no debe asumirse que siempre corresponde a una ruta de archivo convencional. |
| 1720 | `host` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. Normaliza una ausencia hacia un valor vacío/alternativo para simplificar el código consumidor. |

## 6. Mapa exhaustivo de TODOS los bloques delimitados por `{}`

Esta tabla recorre todas las llaves estructurales detectadas fuera de strings/comentarios. Así se documentan también lambdas de Compose, callbacks, iteraciones y ámbitos locales que no tienen un nombre propio de función.

| Inicio–fin | Prof. | Cabecera/contexto | Qué significa ese bloque |
|---|---:|---|---|
| 85–87 | 0 | `fun extractLinkUrls(text: String): List<String> = UrlRegex.findAll(text).map` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 87–87 | 0 | `}.filter` | Filtro de colección: conserva solo los elementos cuyo predicado devuelve verdadero. |
| 92–99 | 0 | `fun noteTextForDisplay(content: String, links: List<String>): String` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 94–96 | 1 | `return if (links.size == 1 && trimmed == links.first())` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 96–98 | 1 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 102–110 | 0 | `val siteName: String?, val host: String, val imageCandidates: List<String> = emptyList(), val cachedImagePath: String? = null)` | Ámbito delimitado por llaves en profundidad 0. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 103–109 | 1 | `companion object` | Cuerpo de tipo/contenedor: agrupa propiedades y miembros bajo la misma responsabilidad y controla su visibilidad/estado compartido. |
| 104–108 | 2 | `fun basic(url: String): LinkPreviewData` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 112–273 | 0 | `private object LinkPreviewRepository` | Cuerpo de tipo/contenedor: agrupa propiedades y miembros bajo la misma responsabilidad y controla su visibilidad/estado compartido. |
| 124–126 | 1 | `private fun lockFor(url: String): Mutex = synchronized(locks)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 125–125 | 2 | `locks.getOrPut(url)` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 127–131 | 1 | `fun peek(context: Context, url: String): LinkPreviewData?` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 128–128 | 2 | `cache.get(url)?.let` | Lambda `let`: transforma/usa el receptor dentro de un ámbito corto; con llamada segura suele ejecutarse solo cuando el receptor no es nulo. |
| 129–130 | 2 | `return readPersistedPreview(context = context, url = url)?.also` | Lambda `also`: ejecuta una acción auxiliar sobre el valor y conserva el valor original como resultado de la expresión. |
| 132–163 | 1 | `suspend fun load(context: Context, url: String): LinkPreviewData = withContext(Dispatchers.IO)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 140–162 | 2 | `gate.withPermit` | Sección protegida por semáforo: limita cuántos bloques equivalentes pueden ejecutarse simultáneamente. |
| 141–145 | 3 | `cache.get(url)?.let` | Lambda `let`: transforma/usa el receptor dentro de un ámbito corto; con llamada segura suele ejecutarse solo cuando el receptor no es nulo. |
| 141–144 | 4 | `cache.get(url)?.let { cached -> return@withPermit ensureThumbnailCached(context = context, preview = cached).also` | Lambda `let`: transforma/usa el receptor dentro de un ámbito corto; con llamada segura suele ejecutarse solo cuando el receptor no es nulo. |
| 146–161 | 3 | `lockFor(url).withLock` | Ámbito delimitado por llaves en profundidad 3. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 147–148 | 4 | `cache.get(url)?.let` | Lambda `let`: transforma/usa el receptor dentro de un ámbito corto; con llamada segura suele ejecutarse solo cuando el receptor no es nulo. |
| 150–155 | 4 | `if (persisted != null)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 164–272 | 1 | `private fun fetch(url: String): LinkPreviewData` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 172–176 | 2 | `val socialProviderPreview = when` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 177–179 | 2 | `if (socialProviderPreview?.imageUrl != null \|\| socialProviderPreview?.imageCandidates?.isNotEmpty() == true)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 180–184 | 2 | `if (isSpotifyRelatedUrl(url))` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 181–182 | 3 | `findSpotifyEntityUrl(url)?.let` | Lambda `let`: transforma/usa el receptor dentro de un ámbito corto; con llamada segura suele ejecutarse solo cuando el receptor no es nulo. |
| 182–183 | 3 | `}?.let` | Lambda `let`: transforma/usa el receptor dentro de un ámbito corto; con llamada segura suele ejecutarse solo cuando el receptor no es nulo. |
| 185–190 | 2 | `if (isDouyinRelatedUrl(url))` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 186–189 | 3 | `fetchDouyinDirectPreview(url)?.let` | Lambda `let`: transforma/usa el receptor dentro de un ámbito corto; con llamada segura suele ejecutarse solo cuando el receptor no es nulo. |
| 186–188 | 4 | `fetchDouyinDirectPreview(url)?.let { preview -> if (preview.imageUrl != null \|\| preview.imageCandidates.isNotEmpty())` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 191–194 | 2 | `if (isTikTokRelatedUrl(url))` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 192–193 | 3 | `fetchTikTokOEmbed(entityUrl = url, clickUrl = url)?.let` | Lambda `let`: transforma/usa el receptor dentro de un ámbito corto; con llamada segura suele ejecutarse solo cuando el receptor no es nulo. |
| 195–197 | 2 | `if (directImageUrl(url) != null)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 196–196 | 3 | `return basic.copy(title = fileNameFromUrl(url).takeIf` | Ámbito delimitado por llaves en profundidad 3. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 199–267 | 2 | `return try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 200–212 | 3 | `connection = (URL(url).openConnection() as HttpURLConnection).apply` | Lambda `apply`: configura el receptor y devuelve ese mismo receptor; útil para inicialización encadenada sin perder la instancia. |
| 205–207 | 4 | `setRequestProperty("User-Agent", if (isDouyinRelatedUrl(url))` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 207–209 | 4 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 215–217 | 3 | `if (responseCode !in 200..399)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 219–225 | 3 | `if (isRedditRelatedUrl(url) \|\| isRedditRelatedUrl(finalUrl))` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 220–224 | 4 | `fetchRedditJsonPreview(entityUrl = finalUrl, clickUrl = url)?.let` | Lambda `let`: transforma/usa el receptor dentro de un ámbito corto; con llamada segura suele ejecutarse solo cuando el receptor no es nulo. |
| 221–223 | 5 | `preview.imageCandidates.isNotEmpty())` | Ámbito delimitado por llaves en profundidad 5. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 226–231 | 3 | `if (isMetaSocialUrl(url) \|\| isMetaSocialUrl(finalUrl))` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 227–230 | 4 | `fetchMetaOEmbedPreview(finalUrl)?.let` | Lambda `let`: transforma/usa el receptor dentro de un ámbito corto; con llamada segura suele ejecutarse solo cuando el receptor no es nulo. |
| 227–229 | 5 | `fetchMetaOEmbedPreview(finalUrl)?.let { preview -> if (preview.imageUrl != null \|\| preview.imageCandidates.isNotEmpty())` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 232–235 | 3 | `if (isTikTokRelatedUrl(url) \|\| isTikTokRelatedUrl(finalUrl))` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 233–234 | 4 | `fetchTikTokOEmbed(entityUrl = finalUrl, clickUrl = url)?.let` | Lambda `let`: transforma/usa el receptor dentro de un ámbito corto; con llamada segura suele ejecutarse solo cuando el receptor no es nulo. |
| 237–240 | 3 | `if (contentType.startsWith("image/"))` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 238–238 | 4 | `return LinkPreviewData.basic(finalUrl).copy(url = url, title = fileNameFromUrl(finalUrl).takeIf` | Ámbito delimitado por llaves en profundidad 4. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 241–243 | 3 | `if (contentType.isNotBlank() && !contentType.contains("html") && !contentType.contains("xhtml"))` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 245–246 | 3 | `val html = connection.inputStream.use` | Bloque `use`: el recurso cerrable se libera automáticamente al terminar el cuerpo, incluso si ocurre una excepción. |
| 248–254 | 3 | `if (isMetaSocialUrl(url) \|\| isMetaSocialUrl(finalUrl) \|\| isRedditRelatedUrl(url) \|\| isRedditRelatedUrl(finalUrl))` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 251–253 | 4 | `if (enriched.imageUrl != null \|\| enriched.imageCandidates.isNotEmpty())` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 255–262 | 3 | `if (isSpotifyRelatedUrl(url) \|\| isSpotifyRelatedUrl(finalUrl))` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 257–260 | 4 | `if (spotifyEntityUrl != null)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 258–259 | 5 | `fetchSpotifyOEmbed(entityUrl = spotifyEntityUrl, clickUrl = url)?.let` | Lambda `let`: transforma/usa el receptor dentro de un ámbito corto; con llamada segura suele ejecutarse solo cuando el receptor no es nulo. |
| 263–265 | 3 | `if (isDouyinRelatedUrl(url) \|\| isDouyinRelatedUrl(finalUrl))` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 267–269 | 2 | `} catch (_: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 269–271 | 2 | `} finally` | Bloque de limpieza garantizada: se ejecuta después del intento tanto si hubo éxito como si hubo excepción. |
| 279–301 | 0 | `suspend fun preloadLinkPreviews(context: Context, urls: List<String>, performanceMode: String = "balanced")` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 281–285 | 1 | `val maxUrls = when (performanceMode)` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 287–291 | 1 | `val pauseBetweenBatchesMs = when (performanceMode)` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 292–292 | 1 | `urls.asSequence().filter` | Filtro de colección: conserva solo los elementos cuyo predicado devuelve verdadero. |
| 292–300 | 1 | `urls.asSequence().filter { it.startsWith("http", ignoreCase = true) }.distinct().take(maxUrls).chunked(parallelism).forEach` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 293–298 | 2 | `coroutineScope` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 294–297 | 3 | `batch.map` | Transformación de colección: produce un nuevo elemento por cada entrada y normalmente genera una colección de salida. |
| 294–296 | 4 | `batch.map { url -> async(Dispatchers.IO)` | Transformación de colección: produce un nuevo elemento por cada entrada y normalmente genera una colección de salida. |
| 315–322 | 0 | `private fun persistPreview(context: Context, originalUrl: String, preview: LinkPreviewData)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 324–348 | 0 | `private fun readPersistedPreview(context: Context, url: String): LinkPreviewData?` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 327–345 | 1 | `return try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 330–333 | 2 | `if (savedAt <= 0L \|\| System.currentTimeMillis() - savedAt > PREVIEW_CACHE_MAX_AGE_MS)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 334–334 | 2 | `val cachedPath = json.optString("cachedImagePath").takeIf` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 334–334 | 2 | `val cachedPath = json.optString("cachedImagePath").takeIf { it.isNotBlank() && it != "null" }?.takeIf` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 335–340 | 2 | `val imageCandidates = json.optJSONArray("imageCandidates")?.let` | Lambda `let`: transforma/usa el receptor dentro de un ámbito corto; con llamada segura suele ejecutarse solo cuando el receptor no es nulo. |
| 335–339 | 3 | `val imageCandidates = json.optJSONArray("imageCandidates")?.let { array -> buildList` | Lambda `let`: transforma/usa el receptor dentro de un ámbito corto; con llamada segura suele ejecutarse solo cuando el receptor no es nulo. |
| 336–338 | 4 | `for (index in 0 until array.length())` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 337–337 | 5 | `array.optString(index).takeIf` | Ámbito delimitado por llaves en profundidad 5. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 345–347 | 1 | `} catch (_: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 350–353 | 0 | `private fun JSONObject.optNullableString(key: String): String?` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 352–352 | 1 | `return optString(key).takeIf` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 355–383 | 0 | `private fun ensureThumbnailCached(context: Context, preview: LinkPreviewData): LinkPreviewData` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 356–356 | 1 | `preview.cachedImagePath?.let(::File)?.takeIf` | Lambda `let`: transforma/usa el receptor dentro de un ámbito corto; con llamada segura suele ejecutarse solo cuando el receptor no es nulo. |
| 356–359 | 1 | `preview.cachedImagePath?.let(::File)?.takeIf { it.isFile && it.length() > 0L && looksLikeImageFile(it) }?.let` | Lambda `let`: transforma/usa el receptor dentro de un ámbito corto; con llamada segura suele ejecutarse solo cuando el receptor no es nulo. |
| 360–363 | 1 | `val remoteCandidates = buildList` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 363–363 | 1 | `}.asSequence().map` | Transformación de colección: produce un nuevo elemento por cada entrada y normalmente genera una colección de salida. |
| 363–365 | 1 | `}.asSequence().map { it.trim() }.filter` | Transformación de colección: produce un nuevo elemento por cada entrada y normalmente genera una colección de salida. |
| 366–368 | 1 | `if (remoteCandidates.isEmpty())` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 369–369 | 1 | `val cacheDir = File(context.filesDir, "link_preview_thumbnails_v6").apply` | Lambda `apply`: configura el receptor y devuelve ese mismo receptor; útil para inicialización encadenada sin perder la instancia. |
| 370–381 | 1 | `remoteCandidates.forEach` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 371–374 | 2 | `if (destination.isFile && destination.length() > 0L && looksLikeImageFile(destination))` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 375–375 | 2 | `destination.takeIf` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 377–380 | 2 | `if (downloaded)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 385–444 | 0 | `private fun downloadThumbnail(imageUrl: String, refererUrl: String, destination: File): Boolean` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 388–438 | 1 | `return try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 389–404 | 2 | `connection = (URL(imageUrl).openConnection() as HttpURLConnection).apply` | Lambda `apply`: configura el receptor y devuelve ese mismo receptor; útil para inicialización encadenada sin perder la instancia. |
| 394–396 | 3 | `setRequestProperty("User-Agent", if (isDouyinRelatedUrl(refererUrl))` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 396–398 | 3 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 406–408 | 2 | `if (connection.responseCode !in 200..299)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 410–412 | 2 | `if (declaredLength > THUMBNAIL_MAX_BYTES)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 414–425 | 2 | `connection.inputStream.use` | Bloque `use`: el recurso cerrable se libera automáticamente al terminar el cuerpo, incluso si ocurre una excepción. |
| 414–424 | 3 | `connection.inputStream.use { input -> FileOutputStream(temp).use` | Bloque `use`: el recurso cerrable se libera automáticamente al terminar el cuerpo, incluso si ocurre una excepción. |
| 415–423 | 4 | `while (true)` | Bucle `while`: se repite mientras la condición siga siendo verdadera; el cuerpo debe producir progreso para evitar un ciclo infinito. |
| 419–421 | 5 | `if (total > THUMBNAIL_MAX_BYTES)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 426–428 | 2 | `if (total <= 0L \|\| !looksLikeImageFile(temp))` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 429–431 | 2 | `if (destination.exists())` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 432–435 | 2 | `if (!temp.renameTo(destination))` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 438–440 | 1 | `} catch (_: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 440–443 | 1 | `} finally` | Bloque de limpieza garantizada: se ejecuta después del intento tanto si hubo éxito como si hubo excepción. |
| 441–441 | 2 | `temp.takeIf` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 446–462 | 0 | `private fun looksLikeImageFile(file: File): Boolean` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 448–459 | 1 | `return try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 450–450 | 2 | `val count = file.inputStream().use` | Bloque `use`: el recurso cerrable se libera automáticamente al terminar el cuerpo, incluso si ocurre una excepción. |
| 459–461 | 1 | `} catch (_: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 464–475 | 0 | `private fun providerReferer(url: String): String` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 466–474 | 1 | `return when` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 477–486 | 0 | `private fun pruneThumbnailCache(directory: File)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 478–478 | 1 | `val files = directory.listFiles()?.filter` | Filtro de colección: conserva solo los elementos cuyo predicado devuelve verdadero. |
| 478–478 | 1 | `val files = directory.listFiles()?.filter { it.isFile && !it.name.endsWith(".tmp") }?.sortedByDescending` | Filtro de colección: conserva solo los elementos cuyo predicado devuelve verdadero. |
| 480–480 | 1 | `var totalBytes = files.sumOf` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 481–485 | 1 | `files.forEachIndexed` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 481–484 | 2 | `files.forEachIndexed { index, file -> if (index >= THUMBNAIL_CACHE_MAX_FILES \|\| totalBytes > THUMBNAIL_CACHE_MAX_BYTES)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 488–492 | 0 | `private fun sha256(value: String): String` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 490–491 | 1 | `return digest.joinToString("")` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 494–526 | 0 | `private fun parseHtml(pageUrl: String, html: String): LinkPreviewData` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 502–512 | 1 | `metaTagRegex.findAll(html).forEach` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 502–506 | 2 | `metaTagRegex.findAll(html).forEach { tagMatch -> val attributes = attributeRegex.findAll(tagMatch.value).associate` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 504–504 | 3 | `val value = attr.groupValues.drop(2).firstOrNull` | Ámbito delimitado por llaves en profundidad 3. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 509–511 | 2 | `if (!key.isNullOrBlank() && !content.isNullOrBlank())` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 516–516 | 1 | `?.takeIf` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 520–520 | 1 | `val resolvedImage = rawImage?.let` | Lambda `let`: transforma/usa el receptor dentro de un ámbito corto; con llamada segura suele ejecutarse solo cuando el receptor no es nulo. |
| 535–542 | 0 | `private fun isDouyinRelatedUrl(url: String): Boolean` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 536–538 | 1 | `val host = try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 538–540 | 1 | `} catch (_: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 551–611 | 0 | `private fun resolveDouyinLink(url: String): ResolvedDouyinLink?` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 554–606 | 1 | `repeat(8)` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 556–601 | 2 | `try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 557–570 | 3 | `connection = (URL(current).openConnection() as HttpURLConnection).apply` | Lambda `apply`: configura el receptor y devuelve ese mismo receptor; útil para inicialización encadenada sin perder la instancia. |
| 565–569 | 4 | `if (cookies.isNotEmpty())` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 566–568 | 5 | `setRequestProperty("Cookie", cookies.entries.joinToString("; ")` | Ámbito delimitado por llaves en profundidad 5. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 572–573 | 3 | `connection.headerFields.filterKeys` | Filtro de colección: conserva solo los elementos cuyo predicado devuelve verdadero. |
| 573–579 | 3 | `}.values.flatten().forEach` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 576–578 | 4 | `if (name.isNotBlank())` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 581–590 | 3 | `if (code in 300..399)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 582–582 | 4 | `val location = connection.getHeaderField("Location")?.takeIf` | Ámbito delimitado por llaves en profundidad 4. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 584–588 | 4 | `extractDouyinAwemeId(pageUrl = current, html = "")?.let` | Lambda `let`: transforma/usa el receptor dentro de un ámbito corto; con llamada segura suele ejecutarse solo cuando el receptor no es nulo. |
| 585–587 | 5 | `cookieHeader = cookies.entries.joinToString("; ")` | Ámbito delimitado por llaves en profundidad 5. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 587–587 | 5 | `}.takeIf` | Ámbito delimitado por llaves en profundidad 5. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 591–593 | 3 | `if (code !in 200..299)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 594–596 | 3 | `val html = connection.inputStream.use` | Bloque `use`: el recurso cerrable se libera automáticamente al terminar el cuerpo, incluso si ocurre una excepción. |
| 598–600 | 3 | `cookieHeader = cookies.entries.joinToString("; ")` | Ámbito delimitado por llaves en profundidad 3. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 600–600 | 3 | `}.takeIf` | Ámbito delimitado por llaves en profundidad 3. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 601–603 | 2 | `} catch (_: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 603–605 | 2 | `} finally` | Bloque de limpieza garantizada: se ejecuta después del intento tanto si hubo éxito como si hubo excepción. |
| 608–610 | 1 | `cookieHeader = cookies.entries.joinToString("; ")` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 610–610 | 1 | `}.takeIf` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 613–622 | 0 | `private fun fetchDouyinDirectPreview(url: String): LinkPreviewData?` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 617–619 | 1 | `if (share != null && share.imageCandidates.isNotEmpty())` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 624–644 | 0 | `private fun enrichDouyinPreview(originalUrl: String, pageUrl: String, html: String, base: LinkPreviewData): LinkPreviewData` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 626–627 | 1 | `val sharePreview = awemeId?.let` | Lambda `let`: transforma/usa el receptor dentro de un ámbito corto; con llamada segura suele ejecutarse solo cuando el receptor no es nulo. |
| 628–630 | 1 | `val apiPreview = if (awemeId != null && (sharePreview == null \|\| sharePreview.imageCandidates.isEmpty()))` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 630–632 | 1 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 633–633 | 1 | `val embeddedCover = base.imageUrl?.takeIf` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 634–640 | 1 | `val candidates = buildList` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 640–640 | 1 | `}.filter` | Filtro de colección: conserva solo los elementos cuyo predicado devuelve verdadero. |
| 641–641 | 1 | `return base.copy(url = originalUrl, title = base.title?.takeIf` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 642–642 | 1 | `description = base.description?.takeIf` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 646–660 | 0 | `private fun extractDouyinAwemeId(pageUrl: String, html: String): String?` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 655–658 | 1 | `sources.forEach` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 655–657 | 2 | `sources.forEach { source -> patterns.forEach` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 655–655 | 3 | `sources.forEach { source -> patterns.forEach { regex -> regex.find(source)?.groupValues?.getOrNull(1)?.takeIf` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 656–656 | 3 | `?.let` | Lambda `let`: transforma/usa el receptor dentro de un ámbito corto; con llamada segura suele ejecutarse solo cuando el receptor no es nulo. |
| 662–696 | 0 | `private fun fetchDouyinSharePagePreview(awemeId: String, clickUrl: String, cookieHeader: String? = null): LinkPreviewData?` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 664–694 | 1 | `paths.forEach` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 666–689 | 2 | `try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 667–677 | 3 | `connection = (URL(shareUrl).openConnection() as HttpURLConnection).apply` | Lambda `apply`: configura el receptor y devuelve ese mismo receptor; útil para inicialización encadenada sin perder la instancia. |
| 676–676 | 4 | `cookieHeader?.takeIf` | Ámbito delimitado por llaves en profundidad 4. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 676–676 | 4 | `cookieHeader?.takeIf { it.isNotBlank() }?.let` | Lambda `let`: transforma/usa el receptor dentro de un ámbito corto; con llamada segura suele ejecutarse solo cuando el receptor no es nulo. |
| 681–682 | 3 | `val html = connection.inputStream.use` | Bloque `use`: el recurso cerrable se libera automáticamente al terminar el cuerpo, incluso si ocurre una excepción. |
| 686–688 | 3 | `if (preview.imageCandidates.isNotEmpty() \|\| !preview.title.isNullOrBlank())` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 689–691 | 2 | `} catch (_: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 691–693 | 2 | `} finally` | Bloque de limpieza garantizada: se ejecuta después del intento tanto si hubo éxito como si hubo excepción. |
| 698–723 | 0 | `private fun extractDouyinRouterData(html: String): JSONObject?` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 706–720 | 1 | `return try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 707–719 | 2 | `when (html[index])` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 708–711 | 3 | `'{' ->` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 712–717 | 3 | `'"' ->` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 720–722 | 1 | `} catch (_: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 725–752 | 0 | `private fun scanBalancedJsonObject(text: String, start: Int): String?` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 730–750 | 1 | `for (index in start until text.length)` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 732–738 | 2 | `if (inString)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 733–737 | 3 | `when` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 738–749 | 2 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 739–748 | 3 | `when (char)` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 742–747 | 4 | `'}' ->` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 744–746 | 5 | `if (depth == 0)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 754–766 | 0 | `private fun scanJsonStringLiteral(text: String, start: Int): String?` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 757–764 | 1 | `for (index in start + 1 until text.length)` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 759–763 | 2 | `when` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 768–795 | 0 | `private fun findDouyinItem(node: Any?, depth: Int = 0): JSONObject?` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 770–793 | 1 | `when (node)` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 771–786 | 2 | `is JSONObject ->` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 772–777 | 3 | `for (key in listOf("item_list", "aweme_list", "aweme_detail", "aweme"))` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 773–776 | 4 | `if (node.has(key))` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 778–780 | 3 | `if (node.has("aweme_id") && (node.has("video") \|\| node.has("images") \|\| node.has("desc")))` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 782–785 | 3 | `while (keys.hasNext())` | Bucle `while`: se repite mientras la condición siga siendo verdadera; el cuerpo debe producir progreso para evitar un ciclo infinito. |
| 787–792 | 2 | `is JSONArray ->` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 788–791 | 3 | `for (index in 0 until node.length())` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 797–809 | 0 | `private fun allJsonUrls(node: JSONObject?): List<String>` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 800–804 | 1 | `listOf("url_list", "urlList").forEach` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 801–803 | 2 | `for (index in 0 until list.length())` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 802–802 | 3 | `decodeHtml(list.optString(index)).replace("\\/", "/").takeIf` | Ámbito delimitado por llaves en profundidad 3. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 805–807 | 1 | `listOf("uri", "url").forEach` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 806–806 | 2 | `.takeIf` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 811–836 | 0 | `private fun douyinPreviewFromItem(item: JSONObject, clickUrl: String): LinkPreviewData` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 813–831 | 1 | `val coverCandidates = buildList` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 824–830 | 2 | `if (images != null)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 825–829 | 3 | `for (index in 0 until images.length())` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 831–831 | 1 | `}.filter` | Filtro de colección: conserva solo los elementos cuyo predicado devuelve verdadero. |
| 832–832 | 1 | `val title = item.optString("desc").cleanText().takeIf` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 833–833 | 1 | `val author = item.optJSONObject("author")?.optString("nickname")?.cleanText()?.takeIf` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 838–887 | 0 | `private fun fetchDouyinItemInfo(awemeId: String, clickUrl: String, cookieHeader: String? = null): LinkPreviewData?` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 842–885 | 1 | `endpoints.forEach` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 843–880 | 2 | `try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 844–853 | 3 | `connection = (URL(endpoint).openConnection() as HttpURLConnection).apply` | Lambda `apply`: configura el receptor y devuelve ese mismo receptor; útil para inicialización encadenada sin perder la instancia. |
| 852–852 | 4 | `cookieHeader?.takeIf` | Ámbito delimitado por llaves en profundidad 4. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 852–852 | 4 | `cookieHeader?.takeIf { it.isNotBlank() }?.let` | Lambda `let`: transforma/usa el receptor dentro de un ámbito corto; con llamada segura suele ejecutarse solo cuando el receptor no es nulo. |
| 855–857 | 3 | `if (connection.responseCode !in 200..299)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 858–858 | 3 | `val body = connection.inputStream.bufferedReader(Charsets.UTF_8).use` | Bloque `use`: el recurso cerrable se libera automáticamente al terminar el cuerpo, incluso si ocurre una excepción. |
| 862–872 | 3 | `val coverCandidates = buildList` | Ámbito delimitado por llaves en profundidad 3. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 874–874 | 3 | `val title = item.optString("desc").cleanText().takeIf` | Ámbito delimitado por llaves en profundidad 3. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 875–875 | 3 | `val author = item.optJSONObject("author")?.optString("nickname")?.cleanText()?.takeIf` | Ámbito delimitado por llaves en profundidad 3. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 876–879 | 3 | `if (cover != null \|\| title != null)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 880–882 | 2 | `} catch (_: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 882–884 | 2 | `} finally` | Bloque de limpieza garantizada: se ejecuta después del intento tanto si hubo éxito como si hubo excepción. |
| 889–899 | 0 | `private fun firstJsonUrl(node: JSONObject?): String?` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 892–897 | 1 | `for (index in 0 until list.length())` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 894–896 | 2 | `if (value.startsWith("http://", ignoreCase = true) \|\| value.startsWith("https://", ignoreCase = true))` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 901–981 | 0 | `private fun extractDouyinCoverUrl(pageUrl: String, html: String): String?` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 903–913 | 1 | `fun addCandidate(value: String?, path: String, bonus: Int = 0)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 904–906 | 2 | `if (value.isNullOrBlank())` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 907–912 | 2 | `extractUrlsFromDouyinValue(value).forEach` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 909–911 | 3 | `if (score > 0)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 914–939 | 1 | `fun walkJson(node: Any?, path: String = "root", depth: Int = 0)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 915–917 | 2 | `if (node == null \|\| depth > 24)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 918–938 | 2 | `when (node)` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 919–931 | 3 | `is JSONObject ->` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 921–930 | 4 | `while (keys.hasNext())` | Bucle `while`: se repite mientras la condición siga siendo verdadera; el cuerpo debe producir progreso para evitar un ciclo infinito. |
| 925–929 | 5 | `when (value)` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 932–936 | 3 | `is JSONArray ->` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 933–935 | 4 | `for (index in 0 until node.length())` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 943–951 | 1 | `if (!renderData.isNullOrBlank())` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 945–947 | 2 | `try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 947–950 | 2 | `} catch (_: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 955–962 | 1 | `if (!routerData.isNullOrBlank())` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 957–959 | 2 | `try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 959–961 | 2 | `} catch (_: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 971–979 | 1 | `cdnUrlRegex.findAll(normalizedHtml).forEach` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 973–975 | 2 | `val context = if (end >= start)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 975–977 | 2 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 980–980 | 1 | `return candidates.asSequence().distinctBy` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 980–980 | 1 | `return candidates.asSequence().distinctBy { it.second }.maxByOrNull` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 983–997 | 0 | `private fun decodeDouyinEmbeddedJson(value: String): String` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 985–995 | 1 | `repeat(3)` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 986–988 | 2 | `val next = try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 988–990 | 2 | `} catch (_: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 991–993 | 2 | `if (next == current)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 999–1012 | 0 | `private fun extractUrlsFromDouyinValue(value: String): List<String>` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 1005–1007 | 1 | `normalized.startsWith("//"))` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 1010–1010 | 1 | `option = RegexOption.IGNORE_CASE).findAll(normalized).map` | Transformación de colección: produce un nuevo elemento por cada entrada y normalmente genera una colección de salida. |
| 1014–1046 | 0 | `private fun douyinCoverScore(path: String, url: String): Int` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 1018–1025 | 1 | `when` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 1026–1028 | 1 | `if (normalizedPath.contains("avatar") \|\| normalizedPath.contains("author") && !normalizedPath.contains("cover"))` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 1029–1031 | 1 | `if (normalizedPath.contains("music") \|\| normalizedPath.contains("emoji") \|\| normalizedPath.contains("icon"))` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 1032–1034 | 1 | `if (normalizedUrl.contains("douyinpic.com"))` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 1035–1037 | 1 | `if (normalizedUrl.contains("byteimg.com"))` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 1038–1040 | 1 | `if (normalizedUrl.contains("douyincdn.com"))` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 1042–1044 | 1 | `normalizedUrl.contains(".png"))` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 1048–1051 | 0 | `private fun isInstagramRelatedUrl(url: String): Boolean` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 1053–1056 | 0 | `private fun isThreadsRelatedUrl(url: String): Boolean` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 1058–1062 | 0 | `private fun isFacebookRelatedUrl(url: String): Boolean` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 1066–1069 | 0 | `private fun isRedditRelatedUrl(url: String): Boolean` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 1076–1132 | 0 | `private fun fetchMetaOEmbedPreview(url: String): LinkPreviewData?` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 1080–1097 | 1 | `when` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 1081–1084 | 2 | `isInstagramRelatedUrl(url) ->` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 1085–1088 | 2 | `isThreadsRelatedUrl(url) ->` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 1089–1095 | 2 | `isFacebookRelatedUrl(url) ->` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 1099–1127 | 1 | `return try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 1100–1108 | 2 | `connection = (URL(endpoint).openConnection() as HttpURLConnection).apply` | Lambda `apply`: configura el receptor y devuelve ese mismo receptor; útil para inicialización encadenada sin perder la instancia. |
| 1110–1112 | 2 | `if (connection.responseCode !in 200..299)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 1113–1113 | 2 | `val body = connection.inputStream.bufferedReader(Charsets.UTF_8).use` | Bloque `use`: el recurso cerrable se libera automáticamente al terminar el cuerpo, incluso si ocurre una excepción. |
| 1117–1117 | 2 | `?.takeIf` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 1119–1122 | 2 | `val candidates = buildList` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 1124–1124 | 2 | `val author = json.optString("author_name").cleanText().takeIf` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 1126–1126 | 2 | `json.optString("provider_name").takeIf` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 1127–1129 | 1 | `} catch (_: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 1129–1131 | 1 | `} finally` | Bloque de limpieza garantizada: se ejecuta después del intento tanto si hubo éxito como si hubo excepción. |
| 1138–1209 | 0 | `private fun fetchRedditJsonPreview(entityUrl: String, clickUrl: String): LinkPreviewData?` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 1144–1204 | 1 | `return try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 1145–1153 | 2 | `connection = (URL(jsonUrl).openConnection() as HttpURLConnection).apply` | Lambda `apply`: configura el receptor y devuelve ese mismo receptor; útil para inicialización encadenada sin perder la instancia. |
| 1155–1157 | 2 | `if (connection.responseCode !in 200..299)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 1158–1158 | 2 | `val body = connection.inputStream.bufferedReader(Charsets.UTF_8).use` | Bloque `use`: el recurso cerrable se libera automáticamente al terminar el cuerpo, incluso si ocurre una excepción. |
| 1163–1165 | 2 | `fun addCandidate(value: String?)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 1164–1164 | 3 | `value?.let(::decodeHtml)?.takeIf` | Lambda `let`: transforma/usa el receptor dentro de un ámbito corto; con llamada segura suele ejecutarse solo cuando el receptor no es nulo. |
| 1167–1178 | 2 | `if (previewImages != null)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 1168–1177 | 3 | `for (index in 0 until previewImages.length())` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 1172–1176 | 4 | `if (resolutions != null)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 1173–1175 | 5 | `for (r in resolutions.length() - 1 downTo 0)` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 1185–1197 | 2 | `if (mediaMetadata != null)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 1187–1196 | 3 | `while (keys.hasNext())` | Bucle `while`: se repite mientras la condición siga siendo verdadera; el cuerpo debe producir progreso para evitar un ciclo infinito. |
| 1191–1195 | 4 | `if (previews != null)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 1192–1194 | 5 | `for (index in previews.length() - 1 downTo 0)` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 1198–1198 | 2 | `val title = post.optString("title").cleanText().takeIf` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 1199–1199 | 2 | `val subreddit = post.optString("subreddit_name_prefixed").takeIf` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 1200–1200 | 2 | `val author = post.optString("author").takeIf` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 1201–1201 | 2 | `LinkPreviewData(url = clickUrl, title = title ?: "Reddit", description = listOfNotNull(subreddit, author?.let` | Lambda `let`: transforma/usa el receptor dentro de un ámbito corto; con llamada segura suele ejecutarse solo cuando el receptor no es nulo. |
| 1202–1202 | 2 | `.joinToString(" · ").takeIf` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 1204–1206 | 1 | `} catch (_: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 1206–1208 | 1 | `} finally` | Bloque de limpieza garantizada: se ejecuta después del intento tanto si hubo éxito como si hubo excepción. |
| 1211–1231 | 0 | `private fun resolveSimpleRedirect(url: String): String?` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 1212–1214 | 1 | `if (!safeHost(url).equals("redd.it", ignoreCase = true) && !safeHost(url).endsWith(".redd.it", ignoreCase = true))` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 1216–1226 | 1 | `return try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 1217–1223 | 2 | `connection = (URL(url).openConnection() as HttpURLConnection).apply` | Lambda `apply`: configura el receptor y devuelve ese mismo receptor; útil para inicialización encadenada sin perder la instancia. |
| 1226–1228 | 1 | `} catch (_: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 1228–1230 | 1 | `} finally` | Bloque de limpieza garantizada: se ejecuta después del intento tanto si hubo éxito como si hubo excepción. |
| 1234–1252 | 0 | `providerPreview: LinkPreviewData?): LinkPreviewData` | Ámbito delimitado por llaves en profundidad 0. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 1235–1241 | 1 | `val candidates = buildList` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 1241–1241 | 1 | `}.filter` | Filtro de colección: conserva solo los elementos cuyo predicado devuelve verdadero. |
| 1242–1248 | 1 | `val provider = when` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 1249–1249 | 1 | `return base.copy(url = originalUrl, title = base.title?.takeIf` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 1250–1250 | 1 | `base.description?.takeIf` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 1251–1251 | 1 | `providerPreview?.siteName?.takeIf` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 1255–1277 | 0 | `private fun extractSocialImageCandidates(pageUrl: String, html: String): List<String>` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 1265–1266 | 1 | `keyRegex.findAll(normalized).forEach` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 1265–1265 | 2 | `keyRegex.findAll(normalized).forEach { match -> match.groupValues.getOrNull(1)?.let` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 1267–1267 | 1 | `val discovered = UrlRegex.findAll(normalized).map` | Transformación de colección: produce un nuevo elemento por cada entrada y normalmente genera una colección de salida. |
| 1268–1268 | 1 | `.mapNotNull` | Transformación de colección: produce un nuevo elemento por cada entrada y normalmente genera una colección de salida. |
| 1268–1274 | 1 | `.mapNotNull { resolveUrl(pageUrl, it) }.filter` | Transformación de colección: produce un nuevo elemento por cada entrada y normalmente genera una colección de salida. |
| 1275–1275 | 1 | `return (explicit + discovered).asSequence().filter` | Filtro de colección: conserva solo los elementos cuyo predicado devuelve verdadero. |
| 1282–1304 | 0 | `private fun scoreSocialImageCandidate(url: String): Int` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 1306–1313 | 0 | `private fun isTikTokRelatedUrl(url: String): Boolean` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 1307–1309 | 1 | `val host = try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 1309–1311 | 1 | `} catch (_: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 1321–1353 | 0 | `private fun fetchTikTokOEmbed(entityUrl: String, clickUrl: String): LinkPreviewData?` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 1323–1348 | 1 | `return try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 1325–1333 | 2 | `connection = (URL(endpoint).openConnection() as HttpURLConnection).apply` | Lambda `apply`: configura el receptor y devuelve ese mismo receptor; útil para inicialización encadenada sin perder la instancia. |
| 1335–1337 | 2 | `if (connection.responseCode !in 200..299)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 1338–1338 | 2 | `val body = connection.inputStream.bufferedReader(Charsets.UTF_8).use` | Bloque `use`: el recurso cerrable se libera automáticamente al terminar el cuerpo, incluso si ocurre una excepción. |
| 1340–1340 | 2 | `val thumbnail = json.optString("thumbnail_url").takeIf` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 1341–1341 | 2 | `val title = json.optString("title").cleanText().takeIf` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 1342–1342 | 2 | `val author = json.optString("author_name").cleanText().takeIf` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 1343–1345 | 2 | `if (thumbnail == null && title == null)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 1348–1350 | 1 | `} catch (_: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 1350–1352 | 1 | `} finally` | Bloque de limpieza garantizada: se ejecuta después del intento tanto si hubo éxito como si hubo excepción. |
| 1366–1373 | 0 | `private fun isSpotifyRelatedUrl(url: String): Boolean` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 1367–1369 | 1 | `val host = try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 1369–1371 | 1 | `} catch (_: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 1375–1391 | 0 | `private fun findSpotifyEntityUrl(url: String): String?` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 1376–1378 | 1 | `normalizeSpotifyEntityUrl(url)?.let` | Lambda `let`: transforma/usa el receptor dentro de un ámbito corto; con llamada segura suele ejecutarse solo cuando el receptor no es nulo. |
| 1380–1382 | 1 | `normalizeSpotifyEntityUrl(decoded)?.let` | Lambda `let`: transforma/usa el receptor dentro de un ámbito corto; con llamada segura suele ejecutarse solo cuando el receptor no es nulo. |
| 1383–1388 | 1 | `return try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 1386–1387 | 2 | `).firstNotNullOfOrNull` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 1388–1390 | 1 | `} catch (_: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 1393–1415 | 0 | `private fun findSpotifyEntityUrlInText(text: String): String?` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 1396–1413 | 1 | `variants.forEach` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 1400–1404 | 2 | `if (openUrl != null)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 1401–1403 | 3 | `normalizeSpotifyEntityUrl(openUrl)?.let` | Lambda `let`: transforma/usa el receptor dentro de un ámbito corto; con llamada segura suele ejecutarse solo cuando el receptor no es nulo. |
| 1408–1412 | 2 | `if (spotifyUri != null)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 1417–1446 | 0 | `private fun normalizeSpotifyEntityUrl(candidate: String): String?` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 1419–1424 | 1 | `if (cleaned.startsWith("spotify:", ignoreCase = true))` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 1425–1443 | 1 | `return try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 1428–1430 | 2 | `if (host != "open.spotify.com")` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 1432–1434 | 2 | `val typeIndex = segments.indexOfFirst` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 1435–1437 | 2 | `if (typeIndex < 0 \|\| typeIndex + 1 >= segments.size)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 1439–1441 | 2 | `val id = segments[typeIndex + 1].substringBefore('?').substringBefore('#').takeIf` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 1440–1440 | 3 | `value.all` | Ámbito delimitado por llaves en profundidad 3. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 1443–1445 | 1 | `} catch (_: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 1448–1462 | 0 | `private fun decodeUrlRepeatedly(value: String): String` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 1450–1460 | 1 | `repeat(3)` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 1451–1453 | 2 | `val next = try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 1453–1455 | 2 | `} catch (_: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 1456–1458 | 2 | `if (next == current)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 1464–1498 | 0 | `private fun fetchSpotifyOEmbed(entityUrl: String, clickUrl: String): LinkPreviewData?` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 1466–1493 | 1 | `return try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 1468–1476 | 2 | `connection = (URL(endpoint).openConnection() as HttpURLConnection).apply` | Lambda `apply`: configura el receptor y devuelve ese mismo receptor; útil para inicialización encadenada sin perder la instancia. |
| 1478–1480 | 2 | `if (connection.responseCode !in 200..299)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 1481–1482 | 2 | `val body = connection.inputStream.use` | Bloque `use`: el recurso cerrable se libera automáticamente al terminar el cuerpo, incluso si ocurre una excepción. |
| 1484–1484 | 2 | `val title = json.optString("title").takeIf` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 1485–1485 | 2 | `val author = json.optString("author_name").takeIf` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 1486–1486 | 2 | `val thumbnail = json.optString("thumbnail_url").takeIf` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 1487–1487 | 2 | `val provider = json.optString("provider_name").takeIf` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 1488–1490 | 2 | `if (title == null && thumbnail == null)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 1493–1495 | 1 | `} catch (_: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 1495–1497 | 1 | `} finally` | Bloque de limpieza garantizada: se ejecuta después del intento tanto si hubo éxito como si hubo excepción. |
| 1500–1517 | 0 | `private fun LinkPreviewData.withKnownProviderFallback(): LinkPreviewData` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 1501–1503 | 1 | `if (!imageUrl.isNullOrBlank())` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 1505–1516 | 1 | `return when` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 1507–1507 | 2 | `?.takeIf` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 1508–1508 | 2 | `isSpotifyRelatedUrl(url) -> copy(siteName = "Spotify", title = title?.takeIf` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 1509–1509 | 2 | `isTikTokRelatedUrl(url) -> copy(siteName = "TikTok", title = title?.takeIf` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 1510–1510 | 2 | `isDouyinRelatedUrl(url) -> copy(siteName = "Douyin", title = title?.takeIf` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 1511–1511 | 2 | `isInstagramRelatedUrl(url) -> copy(siteName = "Instagram", title = title?.takeIf` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 1512–1512 | 2 | `isThreadsRelatedUrl(url) -> copy(siteName = "Threads", title = title?.takeIf` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 1513–1513 | 2 | `isFacebookRelatedUrl(url) -> copy(siteName = "Facebook", title = title?.takeIf` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 1514–1514 | 2 | `isRedditRelatedUrl(url) -> copy(siteName = "Reddit", title = title?.takeIf` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 1521–1612 | 0 | `deferLoad: Boolean = false)` | Ámbito delimitado por llaves en profundidad 0. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 1530–1532 | 1 | `var preview by remember(url)` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 1533–1537 | 1 | `LaunchedEffect(url, deferLoad)` | Efecto de Compose con corrutina: su ciclo de vida depende de las claves; se cancela/reinicia al cambiar dichas claves o salir de composición. |
| 1534–1536 | 2 | `if (!deferLoad)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 1538–1538 | 1 | `val title = preview.title?.takeIf` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 1538–1538 | 1 | `val title = preview.title?.takeIf { it.isNotBlank() }?: preview.siteName?.takeIf` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 1539–1539 | 1 | `val subtitle = preview.description?.takeIf` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 1540–1540 | 1 | `val imageModel: Any? = preview.cachedImagePath?.let(::File)?.takeIf` | Lambda `let`: transforma/usa el receptor dentro de un ámbito corto; con llamada segura suele ejecutarse solo cuando el receptor no es nulo. |
| 1550–1611 | 1 | `BoxWithConstraints(modifier = modifier.fillMaxWidth())` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 1553–1556 | 2 | `Surface(onClick =` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 1556–1610 | 2 | `}, modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium, color = previewBackground, tonalElevation = 1.dp)` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 1557–1599 | 3 | `if (compact)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 1558–1598 | 4 | `when` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 1559–1577 | 5 | `veryCompact ->` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 1560–1576 | 6 | `Column(modifier = Modifier.fillMaxWidth())` | Lambda de contenido de UI Compose: declara hijos y configuración visual dentro del contenedor. El orden y los `Modifier` influyen en medida, posición, dibujo e interacción. |
| 1561–1571 | 7 | `Surface(modifier = Modifier.fillMaxWidth().aspectRatio(16f / 9f), color = placeholderBackground)` | Lambda de contenido de UI Compose: declara hijos y configuración visual dentro del contenedor. El orden y los `Modifier` influyen en medida, posición, dibujo e interacción. |
| 1562–1570 | 8 | `Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center)` | Lambda de contenido de UI Compose: declara hijos y configuración visual dentro del contenedor. El orden y los `Modifier` influyen en medida, posición, dibujo e interacción. |
| 1563–1566 | 9 | `if (imageModel != null)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 1566–1569 | 9 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 1578–1597 | 5 | `else ->` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 1579–1596 | 6 | `Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically)` | Lambda de contenido de UI Compose: declara hijos y configuración visual dentro del contenedor. El orden y los `Modifier` influyen en medida, posición, dibujo e interacción. |
| 1581–1591 | 7 | `if (narrowCompact) 68.dp else 86.dp), color = placeholderBackground)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 1582–1590 | 8 | `Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center)` | Lambda de contenido de UI Compose: declara hijos y configuración visual dentro del contenedor. El orden y los `Modifier` influyen en medida, posición, dibujo e interacción. |
| 1583–1586 | 9 | `if (imageModel != null)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 1586–1589 | 9 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 1599–1609 | 3 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 1600–1608 | 4 | `Column(modifier = Modifier.fillMaxWidth())` | Lambda de contenido de UI Compose: declara hijos y configuración visual dentro del contenedor. El orden y los `Modifier` influyen en medida, posición, dibujo e interacción. |
| 1601–1604 | 5 | `if (imageModel != null)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 1616–1644 | 0 | `showSubtitle: Boolean = true, primaryTextColor: Color, secondaryTextColor: Color, graphicColor: Color, modifier: Modifier = Modifier)` | Ámbito delimitado por llaves en profundidad 0. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 1617–1643 | 1 | `Column(modifier = modifier, verticalArrangement = Arrangement.Center)` | Lambda de contenido de UI Compose: declara hijos y configuración visual dentro del contenedor. El orden y los `Modifier` influyen en medida, posición, dibujo e interacción. |
| 1618–1624 | 2 | `Row(verticalAlignment = Alignment.CenterVertically)` | Lambda de contenido de UI Compose: declara hijos y configuración visual dentro del contenedor. El orden y los `Modifier` influyen en medida, posición, dibujo e interacción. |
| 1626–1630 | 2 | `Text(text = title, color = primaryTextColor, style = when` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 1631–1642 | 2 | `if (showSubtitle && !subtitle.isNullOrBlank())` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 1633–1635 | 3 | `Text(text = subtitle, color = secondaryTextColor, style = if (veryCompact)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 1635–1637 | 3 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 1637–1641 | 3 | `}, maxLines = when` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 1646–1655 | 0 | `private fun openExternalLink(context: Context, url: String)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 1647–1652 | 1 | `try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 1648–1650 | 2 | `val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply` | Lambda `apply`: configura el receptor y devuelve ese mismo receptor; útil para inicialización encadenada sin perder la instancia. |
| 1652–1654 | 1 | `} catch (_: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 1657–1671 | 0 | `private fun readLimitedHtml(reader: InputStreamReader): String` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 1658–1670 | 1 | `reader.use` | Bloque `use`: el recurso cerrable se libera automáticamente al terminar el cuerpo, incluso si ocurre una excepción. |
| 1661–1668 | 2 | `while (builder.length < MAX_HTML_CHARS)` | Bucle `while`: se repite mientras la condición siga siendo verdadera; el cuerpo debe producir progreso para evitar un ciclo infinito. |
| 1664–1666 | 3 | `if (count <= 0)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 1673–1685 | 0 | `private fun charsetFromContentType(contentType: String?): Charset` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 1676–1682 | 1 | `return try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 1677–1679 | 2 | `if (charsetName.isNotBlank())` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 1679–1681 | 2 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 1682–1684 | 1 | `} catch (_: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 1691–1693 | 0 | `private fun firstNonBlank(vararg values: String?): String? = values.firstOrNull` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 1694–1696 | 0 | `private fun resolveUrl(baseUrl: String, candidate: String): String? = try` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 1696–1698 | 0 | `} catch (_: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 1699–1701 | 0 | `private fun safeHost(url: String): String = try` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 1700–1700 | 1 | `URI(url).host?.removePrefix("www.")?.takeIf` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 1701–1703 | 0 | `} catch (_: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 1704–1711 | 0 | `private fun directImageUrl(url: String): String?` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 1706–1708 | 1 | `return if (listOf(".jpg", ".jpeg", ".png", ".webp", ".gif", ".avif").any(cleanPath::endsWith))` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 1708–1710 | 1 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 1713–1715 | 0 | `private fun fileNameFromUrl(url: String): String = try` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 1714–1714 | 1 | `URI(url).path?.substringAfterLast('/')?.takeIf` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 1715–1717 | 0 | `} catch (_: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 1718–1737 | 0 | `private fun youtubeVideoId(url: String): String? = try` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 1721–1736 | 1 | `when` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 1722–1722 | 2 | `host == "youtu.be" -> uri.pathSegments.firstOrNull()?.takeIf` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 1723–1734 | 2 | `host.endsWith("youtube.com") ->` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 1724–1724 | 3 | `uri.getQueryParameter("v")?.takeIf` | Ámbito delimitado por llaves en profundidad 3. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 1724–1733 | 3 | `uri.getQueryParameter("v")?.takeIf { it.isNotBlank() }?: uri.pathSegments.let` | Lambda `let`: transforma/usa el receptor dentro de un ámbito corto; con llamada segura suele ejecutarse solo cuando el receptor no es nulo. |
| 1725–1727 | 4 | `segments.indexOfFirst` | Ámbito delimitado por llaves en profundidad 4. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 1728–1730 | 4 | `if (markerIndex >= 0 && markerIndex + 1 < segments.size)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 1730–1732 | 4 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 1737–1739 | 0 | `} catch (_: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |

## 7. Lectura de seguridad antes de tocar este archivo

La documentación describe **lo que el código actual ya hace**. No constituye una propuesta de refactorización. Si se quisiera cambiar algo en el futuro, primero habría que preservar contratos públicos, claves de configuración, restricciones de API Android, comportamiento de Compose, lifecycle, operaciones de I/O y compatibilidad con los datos ya persistidos. En esta entrega no se hizo ninguna de esas modificaciones.

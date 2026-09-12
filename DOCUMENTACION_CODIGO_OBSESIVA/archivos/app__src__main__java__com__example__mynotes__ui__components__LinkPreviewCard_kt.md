# LinkPreviewCard.kt — documentación exhaustiva por bloques

**Ruta de código real:** `app/src/main/java/com/example/mynotes/ui/components/LinkPreviewCard.kt`  
**SHA-256 del archivo ejecutable sin tocar:** `7381b843ffff585c506470eb52066dff2b580d04df9b73c87d244b767af2e80c`  
**Líneas del código real:** 1691

> **Garantía:** esta documentación se genera en una carpeta paralela. El archivo Kotlin/Kotlin DSL anterior no se modifica. Los fragmentos mostrados abajo son copias de lectura; la aplicación compila usando únicamente el archivo original de `app/...`.

## 1. Papel del archivo

Implementa las tarjetas de vista previa para enlaces y la obtención/normalización de metadatos de múltiples sitios. Contiene lógica de red, parsing HTML/JSON, detección específica de proveedores y fallbacks.

**Arquitectura.** Es uno de los archivos más complejos: separa la URL visible del trabajo de resolver título, imagen, sitio y contenido enriquecido, y ofrece un componente Compose para presentarlo.

**Flujo general.** Flujo típico: se detecta una URL -> se normaliza/procesa según proveedor -> se intenta obtener metadata específica o genérica -> se aplican fallbacks -> el resultado se representa en una tarjeta. El archivo contiene rutas especiales para servicios con HTML/JSON no uniforme.

## 2. Package e imports

El `package` es `com.example.mynotes.ui.components`. Determina el namespace lógico del archivo, resolución de visibilidad `internal`/package-related tooling y la ruta conceptual desde la que otras clases lo importan.

El archivo declara **62 imports**. Cada import evita usar el nombre totalmente calificado en el cuerpo y, además, revela las dependencias técnicas del bloque:

**Android/plataforma:** `android.content.Context`, `android.content.Intent`, `android.net.Uri`, `android.os.Build`, `android.text.Html`, `android.util.LruCache`.

**Jetpack/Compose:** `androidx.compose.foundation.layout.Arrangement`, `androidx.compose.foundation.layout.BoxWithConstraints`, `androidx.compose.foundation.layout.Box`, `androidx.compose.foundation.layout.Column`, `androidx.compose.foundation.layout.Row`, `androidx.compose.foundation.layout.Spacer`, `androidx.compose.foundation.layout.fillMaxSize`, `androidx.compose.foundation.layout.fillMaxWidth`, `androidx.compose.foundation.layout.aspectRatio`, `androidx.compose.foundation.layout.height`, `androidx.compose.foundation.layout.padding`, `androidx.compose.foundation.layout.size`, `androidx.compose.foundation.layout.width`, `androidx.compose.material.icons.Icons`, `androidx.compose.material.icons.filled.OpenInNew`, `androidx.compose.material3.Icon`, `androidx.compose.material3.MaterialTheme`, `androidx.compose.material3.Surface`, `androidx.compose.material3.Text`, `androidx.compose.runtime.Composable`, `androidx.compose.runtime.getValue`, `androidx.compose.runtime.produceState`, `androidx.compose.ui.Alignment`, `androidx.compose.ui.graphics.Color`, `androidx.compose.ui.Modifier`, `androidx.compose.ui.layout.ContentScale`, `androidx.compose.ui.platform.LocalContext`, `androidx.compose.ui.text.font.FontWeight`, `androidx.compose.ui.text.style.TextOverflow`, `androidx.compose.ui.unit.dp`.

**Proyecto MyNotes:** `com.example.mynotes.ui.theme.resolveUiTextColor`, `com.example.mynotes.ui.theme.resolveSecondaryUiTextColor`, `com.example.mynotes.ui.theme.resolveUiGraphicColor`, `com.example.mynotes.ui.sound.UiActionSound`, `com.example.mynotes.ui.sound.UiSoundPlayer`.

**Kotlin/corrutinas/Java:** `kotlinx.coroutines.Dispatchers`, `kotlinx.coroutines.async`, `kotlinx.coroutines.awaitAll`, `kotlinx.coroutines.coroutineScope`, `kotlinx.coroutines.withContext`, `kotlinx.coroutines.sync.Mutex`, `kotlinx.coroutines.sync.withLock`, `java.io.File`, `java.io.FileOutputStream`, `java.io.InputStreamReader`, `java.net.HttpURLConnection`, `java.net.URI`, `java.net.URL`, `java.net.URLDecoder`, `java.net.URLEncoder`, `java.nio.charset.Charset`, `java.security.MessageDigest`, `java.util.Locale`.

**Otras librerías:** `coil3.compose.AsyncImage`, `org.json.JSONArray`, `org.json.JSONObject`.

## 3. Restricciones e invariantes visibles en el archivo

- **Nulabilidad segura (173 aparición/apariciones):** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`.
- **Fallback nulo (80 aparición/apariciones):** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula.
- **Límite numérico (2 aparición/apariciones):** Se fuerza el valor a un intervalo o umbral para impedir que salgan valores fuera de los límites esperados.
- **Filtro condicional (61 aparición/apariciones):** `takeIf` conserva el valor únicamente cuando el predicado se cumple; en caso contrario lo convierte en `null`.
- **Normalización vacía (13 aparición/apariciones):** `orEmpty` transforma una referencia nula en una colección/cadena vacía, simplificando el consumo posterior.
- **Guardia de API (1 aparición/apariciones):** Se condiciona una API según la versión de Android para no ejecutar llamadas incompatibles en dispositivos antiguos.
- **Límite visual (3 aparición/apariciones):** La UI impone un límite explícito de tamaño o cantidad de líneas para impedir crecimiento visual no controlado.
- **Trabajo IO (2 aparición/apariciones):** El trabajo potencialmente bloqueante se desplaza al dispatcher de I/O en vez de ocupar el hilo principal.

## 4. Bloques de código, uno por uno

### 4.1 `extractLinkUrls` — fun, líneas 79–85

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

### 4.2 `noteTextForDisplay` — fun, líneas 86–93

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
| 87 | `val trimmed` | `inferido` | `content.trim()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 88 | `return if (links.size == 1 && trimmed == links.first()) {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `content.trim`, `links.first`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.3 `LinkPreviewData` — class, líneas 95–104

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
| 96 | `val siteName` | `String?, val host: String, val imageCandidates: List<String>` | `emptyList(), val cachedImagePath: String? = null) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo declarado es `String?, val host: String, val imageCandidates: List<String>`. Admite `null`, así que los consumidores deben contemplar ausencia de valor. |
| 99 | `val host` | `inferido` | `safeHost(url)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 100 | `return LinkPreviewData(url = url, title = null, description = null, imageUrl = directImageUrl(url), siteName = host, host = host` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

#### Efectos secundarios y recursos

- **Caché:** Lee, llena, invalida o administra caché para evitar recomputación o decodificación repetida.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `emptyList`, `safeHost`, `LinkPreviewData`, `directImageUrl`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener coherencia entre creación, clave, lectura e invalidación de caché; cambiar solo una de esas etapas puede dejar datos obsoletos.

### 4.4 `basic` — fun, líneas 98–102

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
| 99 | `val host` | `inferido` | `safeHost(url)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 100 | `return LinkPreviewData(url = url, title = null, description = null, imageUrl = directImageUrl(url), siteName = host, host = host` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `safeHost`, `LinkPreviewData`, `directImageUrl`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.5 `LinkPreviewRepository` — object, líneas 106–249

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
| 107 | `val cache` | `inferido` | `LruCache<String, LinkPreviewData>(120)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Es una caché acotada por política LRU: privilegia entradas recientes y permite expulsión automática cuando se supera el límite. Forma parte de la estrategia de reutilización de resultados para evitar repetir trabajo costoso. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 108 | `val locks` | `inferido` | `mutableMapOf<String, Mutex>()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 126 | `val persisted` | `inferido` | `readPersistedPreview(context = context, url = url)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 128 | `val ready` | `inferido` | `ensureThumbnailCached(context = context, preview = persisted)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 133 | `val fetched` | `inferido` | `fetch(url)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 134 | `val ready` | `inferido` | `ensureThumbnailCached(context = context, preview = fetched)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 141 | `val basic` | `inferido` | `LinkPreviewData.basic(url)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 148 | `val socialProviderPreview` | `inferido` | `when {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 174 | `var connection` | `HttpURLConnection?` | `null` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo declarado es `HttpURLConnection?`. Admite `null`, así que los consumidores deben contemplar ausencia de valor. |
| 190 | `val responseCode` | `inferido` | `connection.responseCode` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 194 | `val finalUrl` | `inferido` | `connection.url?.toString() ?: url` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 212 | `val contentType` | `inferido` | `connection.contentType?.lowercase(Locale.ROOT).orEmpty()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. Normaliza una ausencia hacia un valor vacío/alternativo para simplificar el código consumidor. |
| 220 | `val charset` | `inferido` | `charsetFromContentType(connection.contentType)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 221 | `val html` | `inferido` | `connection.inputStream.use { input -> readLimitedHtml(reader = InputStreamReader(input, charset))` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 223 | `val parsed` | `inferido` | `parseHtml(pageUrl = finalUrl, html = html)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 225 | `val enriched` | `inferido` | `enrichSocialHtmlPreview(originalUrl = url, pageUrl = finalUrl, html = html, base = parsed,` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 232 | `val spotifyEntityUrl` | `inferido` | `findSpotifyEntityUrl(url)?: findSpotifyEntityUrl(finalUrl)?: findSpotifyEntityUrlInText(html)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 114 | `return readPersistedPreview(context = context, url = url)?.also { cached -> cache.put(url, cached)` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 127 | `if (persisted != null) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 131 | `return@withLock ready` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 149 | `isMetaSocialUrl(url) -> fetchMetaOEmbedPreview(url)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 150 | `isRedditRelatedUrl(url) -> fetchRedditJsonPreview(entityUrl = url, clickUrl = url)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 151 | `else -> null` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |
| 153 | `if (socialProviderPreview?.imageUrl != null \|\| socialProviderPreview?.imageCandidates?.isNotEmpty() == true) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 154 | `return socialProviderPreview` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 156 | `if (isSpotifyRelatedUrl(url)) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 161 | `if (isDouyinRelatedUrl(url)) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 163 | `return preview` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 167 | `if (isTikTokRelatedUrl(url)) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 171 | `if (directImageUrl(url) != null) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 172 | `return basic.copy(title = fileNameFromUrl(url).takeIf { it.isNotBlank() })` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 175 | `return try {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 191 | `if (responseCode !in 200..399) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 192 | `return basic.withKnownProviderFallback()` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 195 | `if (isRedditRelatedUrl(url) \|\| isRedditRelatedUrl(finalUrl)) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 198 | `return preview` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 202 | `if (isMetaSocialUrl(url) \|\| isMetaSocialUrl(finalUrl)) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 204 | `return preview.copy(url = url)` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 208 | `if (isTikTokRelatedUrl(url) \|\| isTikTokRelatedUrl(finalUrl)) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 213 | `if (contentType.startsWith("image/")) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 214 | `return LinkPreviewData.basic(finalUrl).copy(url = url, title = fileNameFromUrl(finalUrl).takeIf { it.isNotBlank() },` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 217 | `if (contentType.isNotBlank() && !contentType.contains("html") && !contentType.contains("xhtml")) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 218 | `return LinkPreviewData.basic(finalUrl).copy(url = url).withKnownProviderFallback()` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 224 | `if (isMetaSocialUrl(url) \|\| isMetaSocialUrl(finalUrl) \|\| isRedditRelatedUrl(url) \|\| isRedditRelatedUrl(finalUrl)) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 227 | `if (enriched.imageUrl != null \|\| enriched.imageCandidates.isNotEmpty()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 228 | `return enriched` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 231 | `if (isSpotifyRelatedUrl(url) \|\| isSpotifyRelatedUrl(finalUrl)) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 233 | `if (spotifyEntityUrl != null) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 237 | `return parsed.copy(url = url, siteName = "Spotify").withKnownProviderFallback()` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 239 | `if (isDouyinRelatedUrl(url) \|\| isDouyinRelatedUrl(finalUrl)) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 240 | `return enrichDouyinPreview(originalUrl = url, pageUrl = finalUrl, html = html, base = parsed)` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 18.
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 3.
- **Filtro condicional:** `takeIf` conserva el valor únicamente cuando el predicado se cumple; en caso contrario lo convierte en `null`. Apariciones en este bloque: 2.
- **Normalización vacía:** `orEmpty` transforma una referencia nula en una colección/cadena vacía, simplificando el consumo posterior. Apariciones en este bloque: 1.
- **Trabajo IO:** El trabajo potencialmente bloqueante se desplaza al dispatcher de I/O en vez de ocupar el hilo principal. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

- **Caché:** Lee, llena, invalida o administra caché para evitar recomputación o decodificación repetida.
- **Mutación de colección/estado compartido:** Modifica una colección/mapa/estructura existente; el cambio permanece visible para quienes compartan esa misma instancia.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `synchronized`, `locks.getOrPut`, `Mutex`, `cache.get`, `readPersistedPreview`, `cache.put`, `withContext`, `ensureThumbnailCached`, `persistPreview`, `lockFor`, `fetch`, `LinkPreviewData.basic`, `isMetaSocialUrl`, `fetchMetaOEmbedPreview`, `isRedditRelatedUrl`, `fetchRedditJsonPreview`, `isNotEmpty`, `isSpotifyRelatedUrl`, `findSpotifyEntityUrl`, `fetchSpotifyOEmbed`, `isDouyinRelatedUrl`, `fetchDouyinDirectPreview`, `preview.imageCandidates.isNotEmpty`, `isTikTokRelatedUrl`, `fetchTikTokOEmbed`, `directImageUrl`, `basic.copy`, `fileNameFromUrl`, `it.isNotBlank`, `URL`, `openConnection`, `setRequestProperty`, `douyinUserAgent`, `previewUserAgent`, `connection.connect`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.
- No mover trabajo bloqueante al hilo principal; el uso de `Dispatchers.IO` forma parte de la protección de rendimiento.
- Mantener coherencia entre creación, clave, lectura e invalidación de caché; cambiar solo una de esas etapas puede dejar datos obsoletos.

### 4.6 `lockFor` — fun, líneas 109–111

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

### 4.7 `peek` — fun, líneas 112–116

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
| 114 | `return readPersistedPreview(context = context, url = url)?.also { cached -> cache.put(url, cached)` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 2.

#### Efectos secundarios y recursos

- **Caché:** Lee, llena, invalida o administra caché para evitar recomputación o decodificación repetida.
- **Mutación de colección/estado compartido:** Modifica una colección/mapa/estructura existente; el cambio permanece visible para quienes compartan esa misma instancia.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `cache.get`, `readPersistedPreview`, `cache.put`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener coherencia entre creación, clave, lectura e invalidación de caché; cambiar solo una de esas etapas puede dejar datos obsoletos.

### 4.8 `load` — fun, líneas 117–139

```kotlin
    suspend fun load(context: Context, url: String): LinkPreviewData = withContext(Dispatchers.IO) {
            cache.get(url)?.let { cached -> return@withContext ensureThumbnailCached(context = context, preview = cached).also { ready ->
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
| 126 | `val persisted` | `inferido` | `readPersistedPreview(context = context, url = url)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 128 | `val ready` | `inferido` | `ensureThumbnailCached(context = context, preview = persisted)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 133 | `val fetched` | `inferido` | `fetch(url)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 134 | `val ready` | `inferido` | `ensureThumbnailCached(context = context, preview = fetched)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 127 | `if (persisted != null) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 131 | `return@withLock ready` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 2.
- **Trabajo IO:** El trabajo potencialmente bloqueante se desplaza al dispatcher de I/O en vez de ocupar el hilo principal. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

- **Caché:** Lee, llena, invalida o administra caché para evitar recomputación o decodificación repetida.
- **Mutación de colección/estado compartido:** Modifica una colección/mapa/estructura existente; el cambio permanece visible para quienes compartan esa misma instancia.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `withContext`, `cache.get`, `ensureThumbnailCached`, `cache.put`, `persistPreview`, `lockFor`, `readPersistedPreview`, `fetch`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- No mover trabajo bloqueante al hilo principal; el uso de `Dispatchers.IO` forma parte de la protección de rendimiento.
- Mantener coherencia entre creación, clave, lectura e invalidación de caché; cambiar solo una de esas etapas puede dejar datos obsoletos.

### 4.9 `fetch` — fun, líneas 140–248

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
| 141 | `val basic` | `inferido` | `LinkPreviewData.basic(url)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 148 | `val socialProviderPreview` | `inferido` | `when {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 174 | `var connection` | `HttpURLConnection?` | `null` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo declarado es `HttpURLConnection?`. Admite `null`, así que los consumidores deben contemplar ausencia de valor. |
| 190 | `val responseCode` | `inferido` | `connection.responseCode` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 194 | `val finalUrl` | `inferido` | `connection.url?.toString() ?: url` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 212 | `val contentType` | `inferido` | `connection.contentType?.lowercase(Locale.ROOT).orEmpty()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. Normaliza una ausencia hacia un valor vacío/alternativo para simplificar el código consumidor. |
| 220 | `val charset` | `inferido` | `charsetFromContentType(connection.contentType)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 221 | `val html` | `inferido` | `connection.inputStream.use { input -> readLimitedHtml(reader = InputStreamReader(input, charset))` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 223 | `val parsed` | `inferido` | `parseHtml(pageUrl = finalUrl, html = html)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 225 | `val enriched` | `inferido` | `enrichSocialHtmlPreview(originalUrl = url, pageUrl = finalUrl, html = html, base = parsed,` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 232 | `val spotifyEntityUrl` | `inferido` | `findSpotifyEntityUrl(url)?: findSpotifyEntityUrl(finalUrl)?: findSpotifyEntityUrlInText(html)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 149 | `isMetaSocialUrl(url) -> fetchMetaOEmbedPreview(url)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 150 | `isRedditRelatedUrl(url) -> fetchRedditJsonPreview(entityUrl = url, clickUrl = url)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 151 | `else -> null` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |
| 153 | `if (socialProviderPreview?.imageUrl != null \|\| socialProviderPreview?.imageCandidates?.isNotEmpty() == true) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 154 | `return socialProviderPreview` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 156 | `if (isSpotifyRelatedUrl(url)) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 161 | `if (isDouyinRelatedUrl(url)) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 163 | `return preview` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 167 | `if (isTikTokRelatedUrl(url)) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 171 | `if (directImageUrl(url) != null) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 172 | `return basic.copy(title = fileNameFromUrl(url).takeIf { it.isNotBlank() })` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 175 | `return try {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 191 | `if (responseCode !in 200..399) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 192 | `return basic.withKnownProviderFallback()` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 195 | `if (isRedditRelatedUrl(url) \|\| isRedditRelatedUrl(finalUrl)) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 198 | `return preview` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 202 | `if (isMetaSocialUrl(url) \|\| isMetaSocialUrl(finalUrl)) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 204 | `return preview.copy(url = url)` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 208 | `if (isTikTokRelatedUrl(url) \|\| isTikTokRelatedUrl(finalUrl)) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 213 | `if (contentType.startsWith("image/")) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 214 | `return LinkPreviewData.basic(finalUrl).copy(url = url, title = fileNameFromUrl(finalUrl).takeIf { it.isNotBlank() },` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 217 | `if (contentType.isNotBlank() && !contentType.contains("html") && !contentType.contains("xhtml")) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 218 | `return LinkPreviewData.basic(finalUrl).copy(url = url).withKnownProviderFallback()` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 224 | `if (isMetaSocialUrl(url) \|\| isMetaSocialUrl(finalUrl) \|\| isRedditRelatedUrl(url) \|\| isRedditRelatedUrl(finalUrl)) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 227 | `if (enriched.imageUrl != null \|\| enriched.imageCandidates.isNotEmpty()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 228 | `return enriched` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 231 | `if (isSpotifyRelatedUrl(url) \|\| isSpotifyRelatedUrl(finalUrl)) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 233 | `if (spotifyEntityUrl != null) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 237 | `return parsed.copy(url = url, siteName = "Spotify").withKnownProviderFallback()` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 239 | `if (isDouyinRelatedUrl(url) \|\| isDouyinRelatedUrl(finalUrl)) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 240 | `return enrichDouyinPreview(originalUrl = url, pageUrl = finalUrl, html = html, base = parsed)` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

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

### 4.10 `preloadLinkPreviews` — fun, líneas 255–264

```kotlin
suspend fun preloadLinkPreviews(context: Context, urls: List<String>) {
    val appContext = context.applicationContext
    urls.asSequence().filter { it.startsWith("http", ignoreCase = true) }.distinct().take(80).chunked(3).forEach { batch -> coroutineScope {
                batch.map { url -> async(Dispatchers.IO) {
                        LinkPreviewRepository.load(context = appContext, url = url)
                    }
                }.awaitAll()
            }
        }
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `context: Context` — `context` recibe un valor de tipo `Context`. El contrato no marca este parámetro como anulable. Da acceso a recursos y APIs Android; su vida útil debe corresponder con el uso realizado.
- `urls: List<String>` — `urls` recibe un valor de tipo `List<String>`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `suspend` permite suspender sin bloquear el hilo; el llamador debe estar dentro de una corrutina u otra función suspendible.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 256 | `val appContext` | `inferido` | `context.applicationContext` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El `Context` da acceso a recursos/servicios/almacenamiento Android; debe respetarse el ciclo de vida del contexto recibido. |

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

**Restricciones concretas que aparecen en este bloque:**
- **Trabajo IO:** El trabajo potencialmente bloqueante se desplaza al dispatcher de I/O en vez de ocupar el hilo principal. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `urls.asSequence`, `it.startsWith`, `distinct`, `take`, `chunked`, `async`, `LinkPreviewRepository.load`, `awaitAll`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- No mover trabajo bloqueante al hilo principal; el uso de `Dispatchers.IO` forma parte de la protección de rendimiento.

### 4.11 `previewUserAgent` — fun, líneas 266–271

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

### 4.12 `douyinUserAgent` — fun, líneas 272–273

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

### 4.13 `previewCachePreferences` — fun, líneas 274–275

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

### 4.14 `previewCacheKey` — fun, líneas 276–277

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

### 4.15 `persistPreview` — fun, líneas 278–285

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
| 279 | `val json` | `inferido` | `JSONObject().put("savedAt", System.currentTimeMillis()).put("url", preview.url)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

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

### 4.16 `readPersistedPreview` — fun, líneas 287–311

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
| 288 | `val key` | `inferido` | `previewCacheKey(url)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 289 | `val raw` | `inferido` | `previewCachePreferences(context).getString(key, null)?: return null` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 291 | `val json` | `inferido` | `JSONObject(raw)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 292 | `val savedAt` | `inferido` | `json.optLong("savedAt", 0L)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 297 | `val cachedPath` | `inferido` | `json.optString("cachedImagePath").takeIf { it.isNotBlank() && it != "null" }?.takeIf { File(it).isFi…` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Forma parte de la estrategia de reutilización de resultados para evitar repetir trabajo costoso. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 298 | `val imageCandidates` | `inferido` | `json.optJSONArray("imageCandidates")?.let { array -> buildList {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 290 | `return try {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 293 | `if (savedAt <= 0L \|\| System.currentTimeMillis() - savedAt > PREVIEW_CACHE_MAX_AGE_MS) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 295 | `return null` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 299 | `for (index in 0 until array.length()) {` | Iteración: repite el cuerpo para cada elemento de la colección/rango; el coste del bloque crece con el número de elementos recorridos. |

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

### 4.17 `JSONObject` — fun, líneas 313–316

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
| 314 | `if (isNull(key)) return null` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 315 | `return optString(key).takeIf { it.isNotBlank() && it != "null" }` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

**Restricciones concretas que aparecen en este bloque:**
- **Filtro condicional:** `takeIf` conserva el valor únicamente cuando el predicado se cumple; en caso contrario lo convierte en `null`. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `optNullableString`, `isNull`, `optString`, `it.isNotBlank`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.18 `ensureThumbnailCached` — fun, líneas 318–346

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
| 323 | `val remoteCandidates` | `inferido` | `buildList {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 332 | `val cacheDir` | `inferido` | `File(context.filesDir, "link_preview_thumbnails_v6").apply { mkdirs() }` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Forma parte de la estrategia de reutilización de resultados para evitar repetir trabajo costoso. |
| 339 | `val downloaded` | `inferido` | `downloadThumbnail(imageUrl = remoteUrl, refererUrl = preview.url, destination = destination)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 321 | `return preview` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 329 | `if (remoteCandidates.isEmpty()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 330 | `return preview.copy(cachedImagePath = null)` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 334 | `if (destination.isFile && destination.length() > 0L && looksLikeImageFile(destination)) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 336 | `return preview.copy(imageUrl = remoteUrl, cachedImagePath = destination.absolutePath)` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 340 | `if (downloaded) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 342 | `return preview.copy(imageUrl = remoteUrl, cachedImagePath = destination.absolutePath)` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 345 | `return preview.copy(cachedImagePath = null)` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

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

### 4.19 `downloadThumbnail` — fun, líneas 348–407

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
| 349 | `var connection` | `HttpURLConnection?` | `null` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo declarado es `HttpURLConnection?`. Admite `null`, así que los consumidores deben contemplar ausencia de valor. |
| 350 | `val temp` | `inferido` | `File(destination.absolutePath + ".tmp")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 372 | `val declaredLength` | `inferido` | `connection.contentLengthLong` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 376 | `var total` | `inferido` | `0L` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 379 | `val count` | `inferido` | `input.read(buffer)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 351 | `return try {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 369 | `if (connection.responseCode !in 200..299) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 370 | `return false` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 373 | `if (declaredLength > THUMBNAIL_MAX_BYTES) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 374 | `return false` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 378 | `while (true) {` | Bucle condicionado: repite mientras la condición se mantenga verdadera; la lógica interna debe permitir progreso hacia la salida. |
| 380 | `if (count <= 0) break` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 382 | `if (total > THUMBNAIL_MAX_BYTES) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 383 | `return false` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 389 | `if (total <= 0L \|\| !looksLikeImageFile(temp)) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 390 | `return false` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 392 | `if (destination.exists()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 395 | `if (!temp.renameTo(destination)) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |

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

### 4.20 `looksLikeImageFile` — fun, líneas 409–425

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
| 412 | `val header` | `inferido` | `ByteArray(32)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 413 | `val count` | `inferido` | `file.inputStream().use { it.read(header) }` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 415 | `val jpeg` | `inferido` | `header[0].toInt() and 0xFF == 0xFF && header[1].toInt() and 0xFF == 0xD8 && header[2].toInt() and 0x…` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 416 | `val png` | `inferido` | `header.copyOfRange(0, 8).contentEquals(byteArrayOf(0x89.toByte(), 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A…` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 417 | `val gif` | `inferido` | `String(header, 0, 6, Charsets.US_ASCII) == "GIF87a" \|\| String(header, 0, 6, Charsets.US_ASCII) == "G…` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 418 | `val webp` | `inferido` | `String(header, 0, 4, Charsets.US_ASCII) == "RIFF" && String(header, 8, 4, Charsets.US_ASCII) == "WEB…` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 419 | `val avif` | `inferido` | `count >= 12 && String(header, 4, 4, Charsets.US_ASCII) == "ftyp" && listOf("avif", "avis", "mif1", "…` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 410 | `if (!file.isFile \|\| file.length() < 12L) return false` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 411 | `return try {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 414 | `if (count < 12) return false` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |

**Restricciones concretas que aparecen en este bloque:**
- **Guardia de API:** Se condiciona una API según la versión de Android para no ejecutar llamadas incompatibles en dispositivos antiguos. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

- **Sistema de archivos/caché:** Accede o modifica almacenamiento/caché; son efectos externos al estado puramente en memoria.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `file.length`, `ByteArray`, `file.inputStream`, `it.read`, `toInt`, `header.copyOfRange`, `contentEquals`, `byteArrayOf`, `toByte`, `String`, `listOf`, `contains`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar las guardas de versión antes de APIs que no existen en Android antiguos.

### 4.21 `providerReferer` — fun, líneas 427–438

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
| 428 | `val host` | `inferido` | `safeHost(url).lowercase(Locale.ROOT)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 429 | `return when {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 430 | `host.contains("douyin") \|\| host.contains("iesdouyin") -> "https://www.douyin.com/"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 431 | `host.contains("tiktok") -> "https://www.tiktok.com/"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 432 | `host.contains("instagram") -> "https://www.instagram.com/"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 433 | `host.contains("threads") -> "https://www.threads.com/"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 434 | `host.contains("facebook") \|\| host.contains("fb.com") -> "https://www.facebook.com/"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 435 | `host.contains("reddit") \|\| host == "redd.it" -> "https://www.reddit.com/"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 436 | `else -> url` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `safeHost`, `lowercase`, `host.contains`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.

### 4.22 `pruneThumbnailCache` — fun, líneas 440–449

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
| 441 | `val files` | `inferido` | `directory.listFiles()?.filter { it.isFile && !it.name.endsWith(".tmp") }?.sortedByDescending { it.la…` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 443 | `var totalBytes` | `inferido` | `files.sumOf { it.length() }` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

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

### 4.23 `sha256` — fun, líneas 451–455

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
| 452 | `val digest` | `inferido` | `MessageDigest.getInstance("SHA-256").digest(value.toByteArray(Charsets.UTF_8))` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 453 | `return digest.joinToString("") { byte -> "%02x".format(byte)` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `MessageDigest.getInstance`, `digest`, `value.toByteArray`, `digest.joinToString`, `format`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.24 `parseHtml` — fun, líneas 457–489

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
| 458 | `val metadata` | `inferido` | `linkedMapOf<String, String>()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 459 | `val metaTagRegex` | `inferido` | `Regex(` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 462 | `val attributeRegex` | `inferido` | `Regex(` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 467 | `val value` | `inferido` | `attr.groupValues.drop(2).firstOrNull { it.isNotEmpty() }.orEmpty()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Normaliza una ausencia hacia un valor vacío/alternativo para simplificar el código consumidor. |
| 470 | `val key` | `inferido` | `attributes["property"]?: attributes["name"]` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 471 | `val content` | `inferido` | `attributes["content"]` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 476 | `val titleFromTag` | `inferido` | `Regex(` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 480 | `val host` | `inferido` | `safeHost(pageUrl)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 481 | `val rawImage` | `inferido` | `firstNonBlank(metadata["og:image"], metadata["og:image:secure_url"], metadata["twitter:image"],` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 483 | `val resolvedImage` | `inferido` | `rawImage?.let { resolveUrl(pageUrl, it) }` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 484 | `val title` | `inferido` | `firstNonBlank(metadata["og:title"], metadata["twitter:title"], titleFromTag)?.cleanText()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 485 | `val description` | `inferido` | `firstNonBlank(metadata["og:description"], metadata["twitter:description"], metadata["description"])?…` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 486 | `val siteName` | `inferido` | `firstNonBlank(metadata["og:site_name"], metadata["application-name"], host)?.cleanText()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 472 | `if (!key.isNullOrBlank() && !content.isNullOrBlank()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 487 | `return LinkPreviewData(url = pageUrl, title = title, description = description, imageUrl = resolvedImage, siteName = siteName,` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

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

### 4.25 `isDouyinRelatedUrl` — fun, líneas 498–505

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
| 499 | `val host` | `inferido` | `try {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 504 | `return host == "douyin.com" \|\| host.endsWith(".douyin.com") \|\| host == "iesdouyin.com" \|\| host.endsWith(".iesdouyin.com")` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 1.
- **Normalización vacía:** `orEmpty` transforma una referencia nula en una colección/cadena vacía, simplificando el consumo posterior. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `Uri.parse`, `lowercase`, `orEmpty`, `host.endsWith`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.26 `resolveDouyinLink` — fun, líneas 507–574

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
| 515 | `var current` | `inferido` | `url` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 516 | `val cookies` | `inferido` | `linkedMapOf<String, String>()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 518 | `var connection` | `HttpURLConnection?` | `null` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo declarado es `HttpURLConnection?`. Admite `null`, así que los consumidores deben contemplar ausencia de valor. |
| 537 | `val name` | `inferido` | `first.substringBefore('=', "").trim()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 538 | `val value` | `inferido` | `first.substringAfter('=', "").trim()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 543 | `val code` | `inferido` | `connection.responseCode` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 545 | `val location` | `inferido` | `connection.getHeaderField("Location")?.takeIf { it.isNotBlank() }?: return null` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 557 | `val html` | `inferido` | `connection.inputStream.use { input -> readLimitedHtml(InputStreamReader(input,` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 519 | `try {` | Frontera de manejo de errores: agrupa operaciones que pueden lanzar excepciones para permitir recuperación o limpieza controlada. |
| 528 | `if (cookies.isNotEmpty()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 539 | `if (name.isNotBlank()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 544 | `if (code in 300..399) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 552 | `return@repeat` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 554 | `if (code !in 200..299) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 555 | `return null` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 560 | `return ResolvedDouyinLink(finalUrl = current, awemeId = extractDouyinAwemeId(current, html),` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 565 | `return null` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 570 | `return ResolvedDouyinLink(finalUrl = current, awemeId = extractDouyinAwemeId(current, ""),` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

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

### 4.27 `resolveDouyinLink` — fun, líneas 514–574

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
| 515 | `var current` | `inferido` | `url` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 516 | `val cookies` | `inferido` | `linkedMapOf<String, String>()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 518 | `var connection` | `HttpURLConnection?` | `null` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo declarado es `HttpURLConnection?`. Admite `null`, así que los consumidores deben contemplar ausencia de valor. |
| 537 | `val name` | `inferido` | `first.substringBefore('=', "").trim()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 538 | `val value` | `inferido` | `first.substringAfter('=', "").trim()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 543 | `val code` | `inferido` | `connection.responseCode` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 545 | `val location` | `inferido` | `connection.getHeaderField("Location")?.takeIf { it.isNotBlank() }?: return null` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 557 | `val html` | `inferido` | `connection.inputStream.use { input -> readLimitedHtml(InputStreamReader(input,` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 519 | `try {` | Frontera de manejo de errores: agrupa operaciones que pueden lanzar excepciones para permitir recuperación o limpieza controlada. |
| 528 | `if (cookies.isNotEmpty()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 539 | `if (name.isNotBlank()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 544 | `if (code in 300..399) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 552 | `return@repeat` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 554 | `if (code !in 200..299) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 555 | `return null` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 560 | `return ResolvedDouyinLink(finalUrl = current, awemeId = extractDouyinAwemeId(current, html),` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 565 | `return null` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 570 | `return ResolvedDouyinLink(finalUrl = current, awemeId = extractDouyinAwemeId(current, ""),` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

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

### 4.28 `fetchDouyinDirectPreview` — fun, líneas 576–585

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
| 577 | `val resolved` | `inferido` | `resolveDouyinLink(url) ?: return null` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 578 | `val awemeId` | `inferido` | `resolved.awemeId ?: return null` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 579 | `val share` | `inferido` | `fetchDouyinSharePagePreview(awemeId = awemeId, clickUrl = url, cookieHeader = resolved.cookieHeader)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 583 | `val api` | `inferido` | `fetchDouyinItemInfo(awemeId = awemeId, clickUrl = url, cookieHeader = resolved.cookieHeader)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 580 | `if (share != null && share.imageCandidates.isNotEmpty()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 581 | `return share` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 584 | `return api ?: share` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

**Restricciones concretas que aparecen en este bloque:**
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 3.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `resolveDouyinLink`, `fetchDouyinSharePagePreview`, `share.imageCandidates.isNotEmpty`, `fetchDouyinItemInfo`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.29 `enrichDouyinPreview` — fun, líneas 587–607

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
| 588 | `val awemeId` | `inferido` | `extractDouyinAwemeId(pageUrl = pageUrl, html = html)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 589 | `val sharePreview` | `inferido` | `awemeId?.let { id -> fetchDouyinSharePagePreview(awemeId = id, clickUrl = originalUrl)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 591 | `val apiPreview` | `inferido` | `if (awemeId != null && (sharePreview == null \|\| sharePreview.imageCandidates.isEmpty())) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 596 | `val embeddedCover` | `inferido` | `base.imageUrl?.takeIf { it.isNotBlank() }?: extractDouyinCoverUrl(pageUrl = pageUrl, html = html)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 597 | `val candidates` | `inferido` | `buildList {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 604 | `return base.copy(url = originalUrl, title = base.title?.takeIf { it.isNotBlank() }?: sharePreview?.title?: apiPreview?.title,` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

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

### 4.30 `extractDouyinAwemeId` — fun, líneas 609–623

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
| 610 | `val sources` | `inferido` | `listOf(pageUrl, decodeDouyinEmbeddedJson(html))` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 611 | `val patterns` | `inferido` | `listOf(Regex(` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 622 | `return null` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

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

### 4.31 `fetchDouyinSharePagePreview` — fun, líneas 625–659

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
| 626 | `val paths` | `inferido` | `listOf("share/video", "share/slides", "share/note")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 628 | `var connection` | `HttpURLConnection?` | `null` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo declarado es `HttpURLConnection?`. Admite `null`, así que los consumidores deben contemplar ausencia de valor. |
| 643 | `val charset` | `inferido` | `charsetFromContentType(connection.contentType)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 644 | `val html` | `inferido` | `connection.inputStream.use { input -> readLimitedHtml(InputStreamReader(input, charset))` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 646 | `val router` | `inferido` | `extractDouyinRouterData(html) ?: return@forEach` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 647 | `val item` | `inferido` | `findDouyinItem(router) ?: return@forEach` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 648 | `val preview` | `inferido` | `douyinPreviewFromItem(item, clickUrl)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 629 | `try {` | Frontera de manejo de errores: agrupa operaciones que pueden lanzar excepciones para permitir recuperación o limpieza controlada. |
| 642 | `if (connection.responseCode !in 200..399) return@forEach` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 649 | `if (preview.imageCandidates.isNotEmpty() \|\| !preview.title.isNullOrBlank()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 650 | `return preview` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 658 | `return null` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

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

### 4.32 `extractDouyinRouterData` — fun, líneas 661–686

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
| 662 | `val markerIndex` | `inferido` | `html.indexOf("_ROUTER_DATA")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 664 | `val equalsIndex` | `inferido` | `html.indexOf('=', markerIndex)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 666 | `var index` | `inferido` | `equalsIndex + 1` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 672 | `val raw` | `inferido` | `scanBalancedJsonObject(html, index) ?: return null` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 676 | `val literal` | `inferido` | `scanJsonStringLiteral(html, index) ?: return null` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 677 | `val wrapped` | `inferido` | `JSONObject("{\"value\":$literal}")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 678 | `val inner` | `inferido` | `wrapped.getString("value")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 663 | `if (markerIndex < 0) return null` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 665 | `if (equalsIndex < 0) return null` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 667 | `while (index < html.length && html[index].isWhitespace()) index++` | Bucle condicionado: repite mientras la condición se mantenga verdadera; la lógica interna debe permitir progreso hacia la salida. |
| 668 | `if (index >= html.length) return null` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 669 | `return try {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 670 | `when (html[index]) {` | Selección múltiple de Kotlin: concentra casos mutuamente evaluados y normalmente define una normalización o estrategia según el valor de entrada. |
| 675 | `'"' -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 681 | `else -> null` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |

**Restricciones concretas que aparecen en este bloque:**
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 2.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `html.indexOf`, `isWhitespace`, `scanBalancedJsonObject`, `JSONObject`, `scanJsonStringLiteral`, `wrapped.getString`, `decodeDouyinEmbeddedJson`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.

### 4.33 `scanBalancedJsonObject` — fun, líneas 688–715

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
| 690 | `var depth` | `inferido` | `0` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 691 | `var inString` | `inferido` | `false` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 692 | `var escaped` | `inferido` | `false` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 694 | `val char` | `inferido` | `text[index]` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 689 | `if (start !in text.indices \|\| text[start] != '{') return null` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 693 | `for (index in start until text.length) {` | Iteración: repite el cuerpo para cada elemento de la colección/rango; el coste del bloque crece con el número de elementos recorridos. |
| 695 | `if (inString) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 696 | `when {` | Selección múltiple de Kotlin: concentra casos mutuamente evaluados y normalmente define una normalización o estrategia según el valor de entrada. |
| 697 | `escaped -> escaped = false` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 698 | `char == '\\' -> escaped = true` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 699 | `char == '"' -> inString = false` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 702 | `when (char) {` | Selección múltiple de Kotlin: concentra casos mutuamente evaluados y normalmente define una normalización o estrategia según el valor de entrada. |
| 703 | `'"' -> inString = true` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 705 | `'}' -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 707 | `if (depth == 0) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 708 | `return text.substring(start, index + 1)` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 714 | `return null` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `text.substring`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.

### 4.34 `scanJsonStringLiteral` — fun, líneas 717–729

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
| 719 | `var escaped` | `inferido` | `false` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 721 | `val char` | `inferido` | `text[index]` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 718 | `if (start !in text.indices \|\| text[start] != '"') return null` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 720 | `for (index in start + 1 until text.length) {` | Iteración: repite el cuerpo para cada elemento de la colección/rango; el coste del bloque crece con el número de elementos recorridos. |
| 722 | `when {` | Selección múltiple de Kotlin: concentra casos mutuamente evaluados y normalmente define una normalización o estrategia según el valor de entrada. |
| 723 | `escaped -> escaped = false` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 724 | `char == '\\' -> escaped = true` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 725 | `char == '"' -> return text.substring(start, index + 1)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 728 | `return null` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `text.substring`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.

### 4.35 `findDouyinItem` — fun, líneas 731–758

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
| 737 | `val found` | `inferido` | `findDouyinItem(node.opt(key), depth + 1)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 744 | `val keys` | `inferido` | `node.keys()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 746 | `val found` | `inferido` | `findDouyinItem(node.opt(keys.next()), depth + 1)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 752 | `val found` | `inferido` | `findDouyinItem(node.opt(index), depth + 1)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 732 | `if (node == null \|\| node == JSONObject.NULL \|\| depth > 28) return null` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 733 | `when (node) {` | Selección múltiple de Kotlin: concentra casos mutuamente evaluados y normalmente define una normalización o estrategia según el valor de entrada. |
| 734 | `is JSONObject -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 735 | `for (key in listOf("item_list", "aweme_list", "aweme_detail", "aweme")) {` | Iteración: repite el cuerpo para cada elemento de la colección/rango; el coste del bloque crece con el número de elementos recorridos. |
| 736 | `if (node.has(key)) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 738 | `if (found != null) return found` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 741 | `if (node.has("aweme_id") && (node.has("video") \|\| node.has("images") \|\| node.has("desc"))) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 742 | `return node` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 745 | `while (keys.hasNext()) {` | Bucle condicionado: repite mientras la condición se mantenga verdadera; la lógica interna debe permitir progreso hacia la salida. |
| 747 | `if (found != null) return found` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 750 | `is JSONArray -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 751 | `for (index in 0 until node.length()) {` | Iteración: repite el cuerpo para cada elemento de la colección/rango; el coste del bloque crece con el número de elementos recorridos. |
| 753 | `if (found != null) return found` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 757 | `return null` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `listOf`, `node.has`, `findDouyinItem`, `node.opt`, `node.keys`, `keys.hasNext`, `keys.next`, `node.length`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.

### 4.36 `allJsonUrls` — fun, líneas 760–772

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
| 762 | `val urls` | `inferido` | `linkedSetOf<String>()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 761 | `if (node == null) return emptyList()` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 764 | `for (index in 0 until list.length()) {` | Iteración: repite el cuerpo para cada elemento de la colección/rango; el coste del bloque crece con el número de elementos recorridos. |
| 771 | `return urls.toList()` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

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

### 4.37 `douyinPreviewFromItem` — fun, líneas 774–799

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
| 775 | `val video` | `inferido` | `item.optJSONObject("video")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 776 | `val coverCandidates` | `inferido` | `buildList {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 786 | `val images` | `inferido` | `item.optJSONArray("images")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 789 | `val image` | `inferido` | `images.optJSONObject(index)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 795 | `val title` | `inferido` | `item.optString("desc").cleanText().takeIf { it.isNotBlank() }` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 796 | `val author` | `inferido` | `item.optJSONObject("author")?.optString("nickname")?.cleanText()?.takeIf { it.isNotBlank() }` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 787 | `if (images != null) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 788 | `for (index in 0 until images.length()) {` | Iteración: repite el cuerpo para cada elemento de la colección/rango; el coste del bloque crece con el número de elementos recorridos. |
| 797 | `return LinkPreviewData(url = clickUrl, title = title ?: author ?: "Douyin", description = author,` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

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

### 4.38 `fetchDouyinItemInfo` — fun, líneas 801–850

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
| 802 | `val endpoints` | `inferido` | `listOf("https://www.iesdouyin.com/web/api/v2/aweme/iteminfo/?item_ids=$awemeId",` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 821 | `val body` | `inferido` | `connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 822 | `val root` | `inferido` | `JSONObject(body)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 823 | `val item` | `inferido` | `root.optJSONArray("item_list")?.optJSONObject(0)?: root.optJSONObject("aweme_detail")?: return@forEa…` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 824 | `val video` | `inferido` | `item.optJSONObject("video")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 825 | `val coverCandidates` | `inferido` | `buildList {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 836 | `val cover` | `inferido` | `coverCandidates.firstOrNull()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 837 | `val title` | `inferido` | `item.optString("desc").cleanText().takeIf { it.isNotBlank() }` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 838 | `val author` | `inferido` | `item.optJSONObject("author")?.optString("nickname")?.cleanText()?.takeIf { it.isNotBlank() }` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 806 | `try {` | Frontera de manejo de errores: agrupa operaciones que pueden lanzar excepciones para permitir recuperación o limpieza controlada. |
| 818 | `if (connection.responseCode !in 200..299) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 819 | `return@forEach` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 839 | `if (cover != null \|\| title != null) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 840 | `return LinkPreviewData(url = clickUrl, title = title ?: author ?: "Douyin", description = author, imageUrl = cover,` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 849 | `return null` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

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

### 4.39 `firstJsonUrl` — fun, líneas 852–862

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
| 854 | `val list` | `inferido` | `node.optJSONArray("url_list")?: return null` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 856 | `val value` | `inferido` | `list.optString(index)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 853 | `if (node == null) return null` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 855 | `for (index in 0 until list.length()) {` | Iteración: repite el cuerpo para cada elemento de la colección/rango; el coste del bloque crece con el número de elementos recorridos. |
| 857 | `if (value.startsWith("http://", ignoreCase = true) \|\| value.startsWith("https://", ignoreCase = true)) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 858 | `return value` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 861 | `return null` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

**Restricciones concretas que aparecen en este bloque:**
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `node.optJSONArray`, `list.length`, `list.optString`, `value.startsWith`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.40 `extractDouyinCoverUrl` — fun, líneas 864–944

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
| 865 | `val candidates` | `inferido` | `mutableListOf<Pair<Int, String>>()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 871 | `val score` | `inferido` | `douyinCoverScore(path = path, url = resolved) + bonus` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 883 | `val keys` | `inferido` | `node.keys()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 885 | `val key` | `inferido` | `keys.next()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 886 | `val value` | `inferido` | `node.opt(key)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 887 | `val childPath` | `inferido` | `"$path.$key"` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 903 | `val renderData` | `inferido` | `Regex(pattern =` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 907 | `val decoded` | `inferido` | `decodeDouyinEmbeddedJson(renderData)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 915 | `val routerData` | `inferido` | `Regex(pattern =` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 919 | `val decoded` | `inferido` | `decodeDouyinEmbeddedJson(routerData)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 928 | `val normalizedHtml` | `inferido` | `decodeDouyinEmbeddedJson(html).replace("\\/", "/").replace("\\u002F", "/", ignoreCase = true)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 931 | `val cdnUrlRegex` | `inferido` | `Regex(pattern =` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 935 | `val end` | `inferido` | `(match.range.last + 160).coerceAtMost(normalizedHtml.lastIndex)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El inicializador limita explícitamente el valor a un rango, evitando que dimensiones/intensidades/sizes inválidos se propaguen. |
| 936 | `val context` | `inferido` | `if (end >= start) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El `Context` da acceso a recursos/servicios/almacenamiento Android; debe respetarse el ciclo de vida del contexto recibido. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 867 | `if (value.isNullOrBlank()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 868 | `return` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 872 | `if (score > 0) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 878 | `if (node == null \|\| depth > 24) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 879 | `return` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 881 | `when (node) {` | Selección múltiple de Kotlin: concentra casos mutuamente evaluados y normalmente define una normalización o estrategia según el valor de entrada. |
| 882 | `is JSONObject -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 884 | `while (keys.hasNext()) {` | Bucle condicionado: repite mientras la condición se mantenga verdadera; la lógica interna debe permitir progreso hacia la salida. |
| 888 | `when (value) {` | Selección múltiple de Kotlin: concentra casos mutuamente evaluados y normalmente define una normalización o estrategia según el valor de entrada. |
| 889 | `is String -> addCandidate(value = value, path = childPath)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 890 | `JSONObject.NULL -> Unit` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 891 | `else -> walkJson(node = value, path = childPath, depth = depth + 1)` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |
| 895 | `is JSONArray -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 896 | `for (index in 0 until node.length()) {` | Iteración: repite el cuerpo para cada elemento de la colección/rango; el coste del bloque crece con el número de elementos recorridos. |
| 900 | `is String -> addCandidate(value = node, path = path)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 906 | `if (!renderData.isNullOrBlank()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 908 | `try {` | Frontera de manejo de errores: agrupa operaciones que pueden lanzar excepciones para permitir recuperación o limpieza controlada. |
| 918 | `if (!routerData.isNullOrBlank()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 920 | `try {` | Frontera de manejo de errores: agrupa operaciones que pueden lanzar excepciones para permitir recuperación o limpieza controlada. |
| 943 | `return candidates.asSequence().distinctBy { it.second }.maxByOrNull { it.first }?.second` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

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

### 4.41 `addCandidate` — fun, líneas 866–876

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
| 871 | `val score` | `inferido` | `douyinCoverScore(path = path, url = resolved) + bonus` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 867 | `if (value.isNullOrBlank()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 868 | `return` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 872 | `if (score > 0) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |

**Restricciones concretas que aparecen en este bloque:**
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `value.isNullOrBlank`, `extractUrlsFromDouyinValue`, `resolveUrl`, `douyinCoverScore`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.42 `walkJson` — fun, líneas 877–944

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
| 883 | `val keys` | `inferido` | `node.keys()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 885 | `val key` | `inferido` | `keys.next()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 886 | `val value` | `inferido` | `node.opt(key)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 887 | `val childPath` | `inferido` | `"$path.$key"` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 903 | `val renderData` | `inferido` | `Regex(pattern =` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 907 | `val decoded` | `inferido` | `decodeDouyinEmbeddedJson(renderData)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 915 | `val routerData` | `inferido` | `Regex(pattern =` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 919 | `val decoded` | `inferido` | `decodeDouyinEmbeddedJson(routerData)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 928 | `val normalizedHtml` | `inferido` | `decodeDouyinEmbeddedJson(html).replace("\\/", "/").replace("\\u002F", "/", ignoreCase = true)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 931 | `val cdnUrlRegex` | `inferido` | `Regex(pattern =` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 935 | `val end` | `inferido` | `(match.range.last + 160).coerceAtMost(normalizedHtml.lastIndex)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El inicializador limita explícitamente el valor a un rango, evitando que dimensiones/intensidades/sizes inválidos se propaguen. |
| 936 | `val context` | `inferido` | `if (end >= start) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El `Context` da acceso a recursos/servicios/almacenamiento Android; debe respetarse el ciclo de vida del contexto recibido. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 878 | `if (node == null \|\| depth > 24) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 879 | `return` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 881 | `when (node) {` | Selección múltiple de Kotlin: concentra casos mutuamente evaluados y normalmente define una normalización o estrategia según el valor de entrada. |
| 882 | `is JSONObject -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 884 | `while (keys.hasNext()) {` | Bucle condicionado: repite mientras la condición se mantenga verdadera; la lógica interna debe permitir progreso hacia la salida. |
| 888 | `when (value) {` | Selección múltiple de Kotlin: concentra casos mutuamente evaluados y normalmente define una normalización o estrategia según el valor de entrada. |
| 889 | `is String -> addCandidate(value = value, path = childPath)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 890 | `JSONObject.NULL -> Unit` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 891 | `else -> walkJson(node = value, path = childPath, depth = depth + 1)` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |
| 895 | `is JSONArray -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 896 | `for (index in 0 until node.length()) {` | Iteración: repite el cuerpo para cada elemento de la colección/rango; el coste del bloque crece con el número de elementos recorridos. |
| 900 | `is String -> addCandidate(value = node, path = path)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 906 | `if (!renderData.isNullOrBlank()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 908 | `try {` | Frontera de manejo de errores: agrupa operaciones que pueden lanzar excepciones para permitir recuperación o limpieza controlada. |
| 918 | `if (!routerData.isNullOrBlank()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 920 | `try {` | Frontera de manejo de errores: agrupa operaciones que pueden lanzar excepciones para permitir recuperación o limpieza controlada. |
| 943 | `return candidates.asSequence().distinctBy { it.second }.maxByOrNull { it.first }?.second` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

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

### 4.43 `decodeDouyinEmbeddedJson` — fun, líneas 946–960

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
| 947 | `var current` | `inferido` | `decodeHtml(value).trim()` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 949 | `val next` | `inferido` | `try {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 954 | `if (next == current) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 955 | `return current` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 959 | `return current` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

#### Efectos secundarios y recursos

- **Persistencia Room/DAO:** Realiza una operación de persistencia o modificación a través de una capa de datos/DAO.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `decodeHtml`, `trim`, `repeat`, `URLDecoder.decode`, `current.replace`, `Charsets.UTF_8.name`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.44 `extractUrlsFromDouyinValue` — fun, líneas 962–975

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
| 963 | `val normalized` | `inferido` | `decodeHtml(value).replace("\\/", "/").replace("\\u002F", "/", ignoreCase = true)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 966 | `val results` | `inferido` | `linkedSetOf<String>()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 967 | `if (normalized.startsWith("http://", ignoreCase = true) \|\| normalized.startsWith("https://", ignoreCase = true) \|\|` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 974 | `return results.toList()` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

**Restricciones concretas que aparecen en este bloque:**
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 2.

#### Efectos secundarios y recursos

- **Persistencia Room/DAO:** Realiza una operación de persistencia o modificación a través de una capa de datos/DAO.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `decodeHtml`, `replace`, `trim`, `normalized.startsWith`, `normalized.trim`, `Regex`, `findAll`, `forEach`, `results.toList`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.45 `douyinCoverScore` — fun, líneas 977–1009

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
| 978 | `val normalizedPath` | `inferido` | `path.lowercase(Locale.ROOT).replace("_", "").replace("-", "")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 979 | `val normalizedUrl` | `inferido` | `url.lowercase(Locale.ROOT)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 980 | `var score` | `inferido` | `0` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 981 | `when {` | Selección múltiple de Kotlin: concentra casos mutuamente evaluados y normalmente define una normalización o estrategia según el valor de entrada. |
| 982 | `normalizedPath.contains("origincover") -> score += 220` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 983 | `normalizedPath.contains("itemcover") -> score += 200` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 984 | `normalizedPath.contains("videocover") -> score += 190` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 985 | `normalizedPath.contains("dynamiccover") -> score += 175` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 986 | `normalizedPath.contains("cover") -> score += 150` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 987 | `normalizedPath.contains("poster") -> score += 120` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 989 | `if (normalizedPath.contains("avatar") \|\| normalizedPath.contains("author") && !normalizedPath.contains("cover")) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 992 | `if (normalizedPath.contains("music") \|\| normalizedPath.contains("emoji") \|\| normalizedPath.contains("icon")) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 995 | `if (normalizedUrl.contains("douyinpic.com")) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 998 | `if (normalizedUrl.contains("byteimg.com")) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1001 | `if (normalizedUrl.contains("douyincdn.com")) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1004 | `if (normalizedUrl.contains(".jpeg") \|\| normalizedUrl.contains(".jpg") \|\| normalizedUrl.contains(".webp") \|\|` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1008 | `return score` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

#### Efectos secundarios y recursos

- **Persistencia Room/DAO:** Realiza una operación de persistencia o modificación a través de una capa de datos/DAO.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `path.lowercase`, `replace`, `url.lowercase`, `normalizedPath.contains`, `normalizedUrl.contains`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.

### 4.46 `isInstagramRelatedUrl` — fun, líneas 1011–1014

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
| 1012 | `val host` | `inferido` | `safeHost(url).lowercase(Locale.ROOT)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 1013 | `return host == "instagram.com" \|\| host.endsWith(".instagram.com")` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `safeHost`, `lowercase`, `host.endsWith`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.47 `isThreadsRelatedUrl` — fun, líneas 1016–1019

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
| 1017 | `val host` | `inferido` | `safeHost(url).lowercase(Locale.ROOT)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 1018 | `return host == "threads.net" \|\| host.endsWith(".threads.net") \|\| host == "threads.com" \|\| host.endsWith(".threads.com")` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `safeHost`, `lowercase`, `host.endsWith`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.48 `isFacebookRelatedUrl` — fun, líneas 1021–1025

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
| 1022 | `val host` | `inferido` | `safeHost(url).lowercase(Locale.ROOT)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 1023 | `return host == "facebook.com" \|\| host.endsWith(".facebook.com") \|\| host == "fb.com" \|\| host.endsWith(".fb.com") \|\| host == "fb.watch" \|\|` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `safeHost`, `lowercase`, `host.endsWith`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.49 `isMetaSocialUrl` — fun, líneas 1027–1028

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

### 4.50 `isRedditRelatedUrl` — fun, líneas 1029–1032

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
| 1030 | `val host` | `inferido` | `safeHost(url).lowercase(Locale.ROOT)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 1031 | `return host == "reddit.com" \|\| host.endsWith(".reddit.com") \|\| host == "redd.it" \|\| host.endsWith(".redd.it")` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `safeHost`, `lowercase`, `host.endsWith`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.51 `fetchMetaOEmbedPreview` — fun, líneas 1039–1095

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
| 1040 | `val encoded` | `inferido` | `URLEncoder.encode(url, Charsets.UTF_8.name())` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1041 | `val provider` | `String` | `(sin inicializador en la declaración)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo declarado es `String`. No declara nulabilidad explícita. |
| 1042 | `val endpoint` | `String` | `(sin inicializador en la declaración)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo declarado es `String`. No declara nulabilidad explícita. |
| 1054 | `val isVideo` | `inferido` | `url.contains("/reel/", ignoreCase = true) \|\| url.contains("/videos/", ignoreCase = true) \|\|` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El nombre indica un estado de decisión/visibilidad/selección usado para controlar una rama de comportamiento o presentación. |
| 1056 | `val type` | `inferido` | `if (isVideo) "oembed_video" else "oembed_post"` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1061 | `var connection` | `HttpURLConnection?` | `null` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo declarado es `HttpURLConnection?`. Admite `null`, así que los consumidores deben contemplar ausencia de valor. |
| 1076 | `val body` | `inferido` | `connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1077 | `val json` | `inferido` | `JSONObject(body)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1078 | `val html` | `inferido` | `json.optString("html")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1079 | `val thumbnail` | `inferido` | `firstNonBlank(json.optString("thumbnail_url"), json.optString("thumbnailUrl"))` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1081 | `val htmlCandidates` | `inferido` | `extractSocialImageCandidates(pageUrl = url, html = html)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1082 | `val candidates` | `inferido` | `buildList {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1086 | `val title` | `inferido` | `firstNonBlank(json.optString("title"), json.optString("author_name"))?.cleanText()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 1087 | `val author` | `inferido` | `json.optString("author_name").cleanText().takeIf { it.isNotBlank() }` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 1043 | `when {` | Selección múltiple de Kotlin: concentra casos mutuamente evaluados y normalmente define una normalización o estrategia según el valor de entrada. |
| 1044 | `isInstagramRelatedUrl(url) -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 1048 | `isThreadsRelatedUrl(url) -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 1052 | `isFacebookRelatedUrl(url) -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 1059 | `else -> return null` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |
| 1062 | `return try {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 1073 | `if (connection.responseCode !in 200..299) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1074 | `return null` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

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

### 4.52 `fetchRedditJsonPreview` — fun, líneas 1101–1172

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
| 1102 | `val canonical` | `inferido` | `resolveSimpleRedirect(entityUrl) ?: entityUrl` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 1103 | `val clean` | `inferido` | `canonical.substringBefore('#').substringBefore('?').trimEnd('/')` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1105 | `val jsonUrl` | `inferido` | `"$clean.json?raw_json=1"` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1106 | `var connection` | `HttpURLConnection?` | `null` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo declarado es `HttpURLConnection?`. Admite `null`, así que los consumidores deben contemplar ausencia de valor. |
| 1121 | `val body` | `inferido` | `connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1122 | `val root` | `inferido` | `JSONArray(body)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1123 | `val post` | `inferido` | `root.optJSONObject(0)?.optJSONObject("data")?.optJSONArray("children")?.optJSONObject(0)?.optJSONObj…` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 1125 | `val candidates` | `inferido` | `linkedSetOf<String>()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1129 | `val previewImages` | `inferido` | `post.optJSONObject("preview")?.optJSONArray("images")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 1132 | `val image` | `inferido` | `previewImages.optJSONObject(index) ?: continue` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 1134 | `val resolutions` | `inferido` | `image.optJSONArray("resolutions")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1147 | `val mediaMetadata` | `inferido` | `post.optJSONObject("media_metadata")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1149 | `val keys` | `inferido` | `mediaMetadata.keys()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1151 | `val media` | `inferido` | `mediaMetadata.optJSONObject(keys.next()) ?: continue` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 1153 | `val previews` | `inferido` | `media.optJSONArray("p")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1161 | `val title` | `inferido` | `post.optString("title").cleanText().takeIf { it.isNotBlank() }` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1162 | `val subreddit` | `inferido` | `post.optString("subreddit_name_prefixed").takeIf { it.isNotBlank() }` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1163 | `val author` | `inferido` | `post.optString("author").takeIf { it.isNotBlank() }` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 1104 | `if (!isRedditRelatedUrl(clean)) return null` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1107 | `return try {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 1118 | `if (connection.responseCode !in 200..299) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1119 | `return null` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 1130 | `if (previewImages != null) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1131 | `for (index in 0 until previewImages.length()) {` | Iteración: repite el cuerpo para cada elemento de la colección/rango; el coste del bloque crece con el número de elementos recorridos. |
| 1135 | `if (resolutions != null) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1136 | `for (r in resolutions.length() - 1 downTo 0) {` | Iteración: repite el cuerpo para cada elemento de la colección/rango; el coste del bloque crece con el número de elementos recorridos. |
| 1148 | `if (mediaMetadata != null) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1150 | `while (keys.hasNext()) {` | Bucle condicionado: repite mientras la condición se mantenga verdadera; la lógica interna debe permitir progreso hacia la salida. |
| 1154 | `if (previews != null) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1155 | `for (index in previews.length() - 1 downTo 0) {` | Iteración: repite el cuerpo para cada elemento de la colección/rango; el coste del bloque crece con el número de elementos recorridos. |

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

### 4.53 `addCandidate` — fun, líneas 1126–1128

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

### 4.54 `resolveSimpleRedirect` — fun, líneas 1174–1194

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
| 1178 | `var connection` | `HttpURLConnection?` | `null` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo declarado es `HttpURLConnection?`. Admite `null`, así que los consumidores deben contemplar ausencia de valor. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 1175 | `if (!safeHost(url).equals("redd.it", ignoreCase = true) && !safeHost(url).endsWith(".redd.it", ignoreCase = true)) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1176 | `return url` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 1179 | `return try {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 2.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `safeHost`, `equals`, `endsWith`, `URL`, `openConnection`, `setRequestProperty`, `previewUserAgent`, `connection.connect`, `toString`, `disconnect`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.55 `enrichSocialHtmlPreview` — fun, líneas 1196–1215

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
| 1198 | `val candidates` | `inferido` | `buildList {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1205 | `val provider` | `inferido` | `when {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 1206 | `isInstagramRelatedUrl(originalUrl) \|\| isInstagramRelatedUrl(pageUrl) -> "Instagram"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 1207 | `isThreadsRelatedUrl(originalUrl) \|\| isThreadsRelatedUrl(pageUrl) -> "Threads"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 1208 | `isFacebookRelatedUrl(originalUrl) \|\| isFacebookRelatedUrl(pageUrl) -> "Facebook"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 1209 | `isRedditRelatedUrl(originalUrl) \|\| isRedditRelatedUrl(pageUrl) -> "Reddit"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 1210 | `else -> null` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |
| 1212 | `return base.copy(url = originalUrl, title = base.title?.takeIf { it.isNotBlank() }?: providerPreview?.title, description =` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

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

### 4.56 `extractSocialImageCandidates` — fun, líneas 1218–1240

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
| 1220 | `val normalized` | `inferido` | `html.replace("\\/", "/").replace("\\u002F", "/", ignoreCase = true).replace("\\u003A", ":", ignoreCa…` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1224 | `val explicit` | `inferido` | `mutableListOf<String>()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1225 | `val keyRegex` | `inferido` | `Regex(pattern =` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1230 | `val discovered` | `inferido` | `UrlRegex.findAll(normalized).map { it.value.trimEnd('.', ',', ';', ')', ']', '}') }` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 1219 | `if (html.isBlank()) return emptyList()` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1238 | `return (explicit + discovered).asSequence().filter { it.startsWith("http", ignoreCase = true) }.distinct()` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 2.
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

- **Persistencia Room/DAO:** Realiza una operación de persistencia o modificación a través de una capa de datos/DAO.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `html.isBlank`, `emptyList`, `html.replace`, `replace`, `Regex`, `keyRegex.findAll`, `match.groupValues.getOrNull`, `resolveUrl`, `let`, `UrlRegex.findAll`, `it.value.trimEnd`, `candidate.lowercase`, `lower.contains`, `looksLikeRemoteImageUrl`, `toList`, `asSequence`, `it.startsWith`, `distinct`, `sortedByDescending`, `take`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.57 `looksLikeRemoteImageUrl` — fun, líneas 1242–1244

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

### 4.58 `scoreSocialImageCandidate` — fun, líneas 1245–1267

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
| 1246 | `val lower` | `inferido` | `url.lowercase(Locale.ROOT)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1247 | `var score` | `inferido` | `0` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 1248 | `if (lower.contains("thumbnail")) score += 80` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1249 | `if (lower.contains("display")) score += 70` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1250 | `if (lower.contains("preview")) score += 65` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1251 | `if (lower.contains("cover")) score += 60` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1252 | `if (lower.contains("media")) score += 30` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1253 | `if (lower.contains("fbcdn.net")) score += 35` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1254 | `if (lower.contains("cdninstagram.com")) score += 35` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1255 | `if (lower.contains("preview.redd.it")) score += 40` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1256 | `if (lower.contains("i.redd.it")) score += 35` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1257 | `if (lower.contains("douyinpic.com")) score += 35` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1258 | `if (lower.contains("byteimg.com")) score += 30` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1259 | `if (lower.contains("avatar")) score -= 120` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1260 | `if (lower.contains("profile_pic")) score -= 120` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1261 | `if (lower.contains("profilepic")) score -= 120` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1262 | `if (lower.contains("favicon")) score -= 150` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1263 | `if (lower.contains("emoji")) score -= 140` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1264 | `if (lower.contains("icon")) score -= 80` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1265 | `if (lower.contains("logo")) score -= 90` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1266 | `return score` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `url.lowercase`, `lower.contains`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.59 `isTikTokRelatedUrl` — fun, líneas 1269–1276

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
| 1270 | `val host` | `inferido` | `try {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 1275 | `return host == "tiktok.com" \|\| host.endsWith(".tiktok.com")` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 1.
- **Normalización vacía:** `orEmpty` transforma una referencia nula en una colección/cadena vacía, simplificando el consumo posterior. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `Uri.parse`, `lowercase`, `orEmpty`, `host.endsWith`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.60 `fetchTikTokOEmbed` — fun, líneas 1284–1316

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
| 1285 | `var connection` | `HttpURLConnection?` | `null` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo declarado es `HttpURLConnection?`. Admite `null`, así que los consumidores deben contemplar ausencia de valor. |
| 1287 | `val endpoint` | `inferido` | `"https://www.tiktok.com/oembed?url=" + URLEncoder.encode(entityUrl, Charsets.UTF_8.name())` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1301 | `val body` | `inferido` | `connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1302 | `val json` | `inferido` | `JSONObject(body)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1303 | `val thumbnail` | `inferido` | `json.optString("thumbnail_url").takeIf { it.isNotBlank() }` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1304 | `val title` | `inferido` | `json.optString("title").cleanText().takeIf { it.isNotBlank() }` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1305 | `val author` | `inferido` | `json.optString("author_name").cleanText().takeIf { it.isNotBlank() }` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 1286 | `return try {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 1298 | `if (connection.responseCode !in 200..299) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1299 | `return null` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 1306 | `if (thumbnail == null && title == null) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1307 | `return null` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

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

### 4.61 `isSpotifyRelatedUrl` — fun, líneas 1329–1336

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
| 1330 | `val host` | `inferido` | `try {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 1335 | `return host == "spotify.link" \|\| host == "spoti.fi" \|\| host == "spotify.com" \|\| host.endsWith(".spotify.com")` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 1.
- **Normalización vacía:** `orEmpty` transforma una referencia nula en una colección/cadena vacía, simplificando el consumo posterior. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `Uri.parse`, `lowercase`, `orEmpty`, `host.endsWith`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.62 `findSpotifyEntityUrl` — fun, líneas 1338–1354

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
| 1342 | `val decoded` | `inferido` | `decodeUrlRepeatedly(url)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1347 | `val uri` | `inferido` | `Uri.parse(url)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Representa una ubicación Android `Uri`; no debe asumirse que siempre corresponde a una ruta de archivo convencional. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 1340 | `return it` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 1344 | `return it` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 1346 | `return try {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 4.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `normalizeSpotifyEntityUrl`, `decodeUrlRepeatedly`, `Uri.parse`, `listOf`, `uri.getQueryParameter`, `let`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.63 `findSpotifyEntityUrlInText` — fun, líneas 1356–1378

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
| 1357 | `val variants` | `inferido` | `linkedSetOf(text, decodeHtml(text), text.replace("\\/", "/").replace("\\u002F", "/", ignoreCase = tr…` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1360 | `val openUrl` | `inferido` | `Regex(pattern =` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1368 | `val spotifyUri` | `inferido` | `Regex(pattern =` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Representa una ubicación Android `Uri`; no debe asumirse que siempre corresponde a una ruta de archivo convencional. |
| 1372 | `val type` | `inferido` | `spotifyUri.groupValues[1].lowercase(Locale.ROOT)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1373 | `val id` | `inferido` | `spotifyUri.groupValues[2]` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 1363 | `if (openUrl != null) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1365 | `return it` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 1371 | `if (spotifyUri != null) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1374 | `return "https://open.spotify.com/$type/$id"` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 1377 | `return null` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 3.
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

- **Persistencia Room/DAO:** Realiza una operación de persistencia o modificación a través de una capa de datos/DAO.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `linkedSetOf`, `decodeHtml`, `text.replace`, `replace`, `decodeUrlRepeatedly`, `candidateText.replace`, `Regex`, `find`, `trimEnd`, `normalizeSpotifyEntityUrl`, `lowercase`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.64 `normalizeSpotifyEntityUrl` — fun, líneas 1380–1409

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
| 1381 | `val cleaned` | `inferido` | `candidate.trim().trim('"', '\'', '`').replace("\\/", "/")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1383 | `val match` | `inferido` | `Regex(pattern =` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1389 | `val uri` | `inferido` | `Uri.parse(cleaned)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Representa una ubicación Android `Uri`; no debe asumirse que siempre corresponde a una ruta de archivo convencional. |
| 1390 | `val host` | `inferido` | `uri.host?.lowercase(Locale.ROOT).orEmpty()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. Normaliza una ausencia hacia un valor vacío/alternativo para simplificar el código consumidor. |
| 1394 | `val segments` | `inferido` | `uri.pathSegments` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1395 | `val typeIndex` | `inferido` | `segments.indexOfFirst {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1401 | `val type` | `inferido` | `segments[typeIndex].lowercase(Locale.ROOT)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1402 | `val id` | `inferido` | `segments[typeIndex + 1].substringBefore('?').substringBefore('#').takeIf { value -> value.isNotBlank…` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 1382 | `if (cleaned.startsWith("spotify:", ignoreCase = true)) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1386 | `return "https://open.spotify.com/" + match.groupValues[1].lowercase(Locale.ROOT) + "/" + match.groupValues[2]` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 1388 | `return try {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 1391 | `if (host != "open.spotify.com") {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1392 | `return null` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 1398 | `if (typeIndex < 0 \|\| typeIndex + 1 >= segments.size) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1399 | `return null` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

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

### 4.65 `decodeUrlRepeatedly` — fun, líneas 1411–1425

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
| 1412 | `var current` | `inferido` | `value` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1414 | `val next` | `inferido` | `try {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 1419 | `if (next == current) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1420 | `return current` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 1424 | `return current` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `repeat`, `URLDecoder.decode`, `Charsets.UTF_8.name`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.66 `fetchSpotifyOEmbed` — fun, líneas 1427–1461

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
| 1428 | `var connection` | `HttpURLConnection?` | `null` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo declarado es `HttpURLConnection?`. Admite `null`, así que los consumidores deben contemplar ausencia de valor. |
| 1430 | `val endpoint` | `inferido` | `"https://open.spotify.com/oembed?url=" + URLEncoder.encode(entityUrl, Charsets.UTF_8.name())` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1444 | `val body` | `inferido` | `connection.inputStream.use { input -> readLimitedHtml(InputStreamReader(input, Charsets.UTF_8))` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1446 | `val json` | `inferido` | `JSONObject(body)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1447 | `val title` | `inferido` | `json.optString("title").takeIf { it.isNotBlank() }` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1448 | `val author` | `inferido` | `json.optString("author_name").takeIf { it.isNotBlank() }` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1449 | `val thumbnail` | `inferido` | `json.optString("thumbnail_url").takeIf { it.isNotBlank() }` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1450 | `val provider` | `inferido` | `json.optString("provider_name").takeIf { it.isNotBlank() }?: "Spotify"` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 1429 | `return try {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 1441 | `if (connection.responseCode !in 200..299) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1442 | `return null` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 1451 | `if (title == null && thumbnail == null) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1452 | `return null` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

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

### 4.67 `LinkPreviewData` — fun, líneas 1463–1480

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
| 1467 | `val youtubeId` | `inferido` | `youtubeVideoId(url)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 1464 | `if (!imageUrl.isNullOrBlank()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1465 | `return this` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 1468 | `return when {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 1469 | `youtubeId != null -> copy(imageUrl = "https://i.ytimg.com/vi/$youtubeId/hqdefault.jpg", siteName = siteName` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 1471 | `isSpotifyRelatedUrl(url) -> copy(siteName = "Spotify", title = title?.takeIf { it.isNotBlank() }?: "Spotify")` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 1472 | `isTikTokRelatedUrl(url) -> copy(siteName = "TikTok", title = title?.takeIf { it.isNotBlank() }?: "TikTok")` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 1473 | `isDouyinRelatedUrl(url) -> copy(siteName = "Douyin", title = title?.takeIf { it.isNotBlank() }?: "Douyin")` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 1474 | `isInstagramRelatedUrl(url) -> copy(siteName = "Instagram", title = title?.takeIf { it.isNotBlank() } ?: "Instagram")` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 1475 | `isThreadsRelatedUrl(url) -> copy(siteName = "Threads", title = title?.takeIf { it.isNotBlank() } ?: "Threads")` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 1476 | `isFacebookRelatedUrl(url) -> copy(siteName = "Facebook", title = title?.takeIf { it.isNotBlank() } ?: "Facebook")` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 1477 | `isRedditRelatedUrl(url) -> copy(siteName = "Reddit", title = title?.takeIf { it.isNotBlank() } ?: "Reddit")` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 1478 | `else -> this` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |

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

### 4.68 `LinkPreviewCard` — fun, líneas 1483–1564

```kotlin
fun LinkPreviewCard(url: String, modifier: Modifier = Modifier, compact: Boolean = false, textColorMode: String = "auto") {
    val context = LocalContext.current
    val preview by
        produceState(initialValue = LinkPreviewRepository.peek(context = context.applicationContext, url = url
                ) ?: LinkPreviewData.basic(url), key1 = url) {
            value = LinkPreviewRepository.load(context = context.applicationContext, url = url)
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

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `@Composable` convierte la función en una unidad declarativa de Compose y la somete a reglas de recomposición.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 1484 | `val context` | `inferido` | `LocalContext.current` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El `Context` da acceso a recursos/servicios/almacenamiento Android; debe respetarse el ciclo de vida del contexto recibido. |
| 1490 | `val title` | `inferido` | `preview.title?.takeIf { it.isNotBlank() }?: preview.siteName?.takeIf { it.isNotBlank() }?: preview.h…` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 1491 | `val subtitle` | `inferido` | `preview.description?.takeIf { it.isNotBlank() }` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 1492 | `val imageModel` | `Any?` | `preview.cachedImagePath?.let(::File)?.takeIf { it.isFile && it.length() > 0L }` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo declarado es `Any?`. Admite `null`, así que los consumidores deben contemplar ausencia de valor. Actúa como selector de estrategia/perfil; su valor determina qué rama de configuración se aplica. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 1496 | `val previewBackground` | `inferido` | `MaterialTheme.colorScheme.surfaceContainer` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1497 | `val placeholderBackground` | `inferido` | `MaterialTheme.colorScheme.surfaceContainerHigh` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1498 | `val previewPrimaryText` | `inferido` | `resolveUiTextColor(value = textColorMode, background = previewBackground)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1499 | `val previewSecondaryText` | `inferido` | `resolveSecondaryUiTextColor(value = textColorMode, background = previewBackground)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1500 | `val previewGraphicColor` | `inferido` | `resolveUiGraphicColor(value = textColorMode, background = previewBackground)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1501 | `val placeholderTextColor` | `inferido` | `resolveUiTextColor(value = textColorMode, background = placeholderBackground)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1503 | `val veryCompact` | `inferido` | `compact && maxWidth < 170.dp` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1504 | `val narrowCompact` | `inferido` | `compact && maxWidth < 220.dp` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 1509 | `if (compact) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1510 | `when {` | Selección múltiple de Kotlin: concentra casos mutuamente evaluados y normalmente define una normalización o estrategia según el valor de entrada. |
| 1511 | `veryCompact -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 1515 | `if (imageModel != null) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1530 | `else -> {` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |
| 1533 | `if (narrowCompact) 68.dp else 86.dp), color = placeholderBackground) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1535 | `if (imageModel != null) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1553 | `if (imageModel != null) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 5.
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 9.
- **Filtro condicional:** `takeIf` conserva el valor únicamente cuando el predicado se cumple; en caso contrario lo convierte en `null`. Apariciones en este bloque: 4.

#### Semántica Compose/lifecycle

- **Callbacks `onClick`:** La lógica asociada se ejecuta por evento de usuario, no durante la composición inicial.
- **`Modifier`:** La cadena de modificadores define tamaño, interacción, dibujo, semántica o posicionamiento; el orden de los modificadores puede ser significativo.

#### Efectos secundarios y recursos

- **Sonido/háptica:** Produce feedback perceptible para el usuario; debe ejecutarse en respuesta a la interacción/estado correspondiente.
- **Caché:** Lee, llena, invalida o administra caché para evitar recomputación o decodificación repetida.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `produceState`, `LinkPreviewRepository.peek`, `LinkPreviewData.basic`, `LinkPreviewRepository.load`, `it.isNotBlank`, `let`, `it.length`, `resolveUiTextColor`, `resolveSecondaryUiTextColor`, `resolveUiGraphicColor`, `BoxWithConstraints`, `modifier.fillMaxWidth`, `Surface`, `UiSoundPlayer.playAction`, `openExternalLink`, `Modifier.fillMaxWidth`, `Column`, `aspectRatio`, `Box`, `Modifier.fillMaxSize`, `AsyncImage`, `Text`, `take`, `uppercase`, `LinkPreviewText`, `Modifier.padding`, `Row`, `Modifier.width`, `height`, `weight`, `padding`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.
- Mantener coherencia entre creación, clave, lectura e invalidación de caché; cambiar solo una de esas etapas puede dejar datos obsoletos.

### 4.69 `LinkPreviewText` — fun, líneas 1567–1596

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
| 1575 | `if (veryCompact) 13.dp else 15.dp), tint = graphicColor)` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1579 | `veryCompact -> MaterialTheme.typography.bodyLarge` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 1580 | `compact -> MaterialTheme.typography.titleSmall` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 1581 | `else -> MaterialTheme.typography.titleMedium` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |
| 1583 | `if (showSubtitle && !subtitle.isNullOrBlank()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1590 | `veryCompact -> 1` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 1591 | `compact -> 2` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 1592 | `else -> 3` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |

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

### 4.70 `openExternalLink` — fun, líneas 1598–1607

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
| 1600 | `val intent` | `inferido` | `Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 1599 | `try {` | Frontera de manejo de errores: agrupa operaciones que pueden lanzar excepciones para permitir recuperación o limpieza controlada. |

#### Efectos secundarios y recursos

- **Navegación/Activity:** Modifica navegación, ciclo de vida o contenido de una Activity.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `Intent`, `Uri.parse`, `addFlags`, `context.startActivity`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.71 `readLimitedHtml` — fun, líneas 1609–1623

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
| 1611 | `val buffer` | `inferido` | `CharArray(8_192)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1612 | `val builder` | `inferido` | `StringBuilder()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1614 | `val remaining` | `inferido` | `MAX_HTML_CHARS - builder.length` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1615 | `val count` | `inferido` | `it.read(buffer, 0, minOf(buffer.size, remaining))` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 1613 | `while (builder.length < MAX_HTML_CHARS) {` | Bucle condicionado: repite mientras la condición se mantenga verdadera; la lógica interna debe permitir progreso hacia la salida. |
| 1616 | `if (count <= 0) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1617 | `break` | Interrumpe el bucle más cercano; ninguna iteración posterior de ese bucle se ejecuta. |
| 1621 | `return builder.toString()` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `CharArray`, `StringBuilder`, `it.read`, `minOf`, `builder.append`, `builder.toString`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.72 `charsetFromContentType` — fun, líneas 1625–1637

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
| 1626 | `val charsetName` | `inferido` | `contentType?.substringAfter("charset=", missingDelimiterValue = "")?.substringBefore(';')?.trim()?.t…` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 1628 | `return try {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 1629 | `if (charsetName.isNotBlank()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 4.
- **Normalización vacía:** `orEmpty` transforma una referencia nula en una colección/cadena vacía, simplificando el consumo posterior. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `substringAfter`, `substringBefore`, `trim`, `orEmpty`, `charsetName.isNotBlank`, `Charset.forName`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.73 `decodeHtml` — fun, líneas 1639–1640

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

### 4.74 `String` — fun, líneas 1641–1642

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

### 4.75 `firstNonBlank` — fun, líneas 1643–1645

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

### 4.76 `resolveUrl` — fun, líneas 1646–1650

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

### 4.77 `safeHost` — fun, líneas 1651–1655

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

### 4.78 `directImageUrl` — fun, líneas 1656–1663

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
| 1657 | `val cleanPath` | `inferido` | `url.substringBefore('#').substringBefore('?').lowercase(Locale.ROOT)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 1658 | `return if (listOf(".jpg", ".jpeg", ".png", ".webp", ".gif", ".avif").any(cleanPath::endsWith)) {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `url.substringBefore`, `substringBefore`, `lowercase`, `listOf`, `any`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.79 `fileNameFromUrl` — fun, líneas 1665–1669

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

### 4.80 `youtubeVideoId` — fun, líneas 1670–1691

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
| 1671 | `val uri` | `inferido` | `Uri.parse(url)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Representa una ubicación Android `Uri`; no debe asumirse que siempre corresponde a una ruta de archivo convencional. |
| 1672 | `val host` | `inferido` | `uri.host?.lowercase(Locale.ROOT).orEmpty()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. Normaliza una ausencia hacia un valor vacío/alternativo para simplificar el código consumidor. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 1673 | `when {` | Selección múltiple de Kotlin: concentra casos mutuamente evaluados y normalmente define una normalización o estrategia según el valor de entrada. |
| 1674 | `host == "youtu.be" -> uri.pathSegments.firstOrNull()?.takeIf { it.isNotBlank() }` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 1675 | `host.endsWith("youtube.com") -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 1680 | `if (markerIndex >= 0 && markerIndex + 1 < segments.size) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 1687 | `else -> null` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |

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
| 66 | `MAX_HTML_CHARS` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Está marcada `const`, por lo que es una constante de tiempo de compilación y solo puede usar un tipo/valor admitido por Kotlin para constantes. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 67 | `PREVIEW_CACHE_VERSION` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Forma parte de la estrategia de reutilización de resultados para evitar repetir trabajo costoso. Está marcada `const`, por lo que es una constante de tiempo de compilación y solo puede usar un tipo/valor admitido por Kotlin para constantes. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 68 | `PREVIEW_CACHE_MAX_AGE_MS` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Forma parte de la estrategia de reutilización de resultados para evitar repetir trabajo costoso. Está marcada `const`, por lo que es una constante de tiempo de compilación y solo puede usar un tipo/valor admitido por Kotlin para constantes. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 69 | `THUMBNAIL_MAX_BYTES` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Está marcada `const`, por lo que es una constante de tiempo de compilación y solo puede usar un tipo/valor admitido por Kotlin para constantes. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 70 | `THUMBNAIL_CACHE_MAX_BYTES` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Forma parte de la estrategia de reutilización de resultados para evitar repetir trabajo costoso. Está marcada `const`, por lo que es una constante de tiempo de compilación y solo puede usar un tipo/valor admitido por Kotlin para constantes. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 71 | `THUMBNAIL_CACHE_MAX_FILES` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Forma parte de la estrategia de reutilización de resultados para evitar repetir trabajo costoso. Está marcada `const`, por lo que es una constante de tiempo de compilación y solo puede usar un tipo/valor admitido por Kotlin para constantes. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 73 | `UrlRegex` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 87 | `trimmed` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 96 | `siteName` | `val` | `String?, val host: String, val imageCandidates: List<String>` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo declarado es `String?, val host: String, val imageCandidates: List<String>`. Admite `null`, así que los consumidores deben contemplar ausencia de valor. |
| 99 | `host` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 107 | `cache` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Es una caché acotada por política LRU: privilegia entradas recientes y permite expulsión automática cuando se supera el límite. Forma parte de la estrategia de reutilización de resultados para evitar repetir trabajo costoso. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 108 | `locks` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 126 | `persisted` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 128 | `ready` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 133 | `fetched` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 134 | `ready` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 141 | `basic` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 148 | `socialProviderPreview` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 174 | `connection` | `var` | `HttpURLConnection?` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo declarado es `HttpURLConnection?`. Admite `null`, así que los consumidores deben contemplar ausencia de valor. |
| 190 | `responseCode` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 194 | `finalUrl` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 212 | `contentType` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. Normaliza una ausencia hacia un valor vacío/alternativo para simplificar el código consumidor. |
| 220 | `charset` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 221 | `html` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 223 | `parsed` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 225 | `enriched` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 232 | `spotifyEntityUrl` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 256 | `appContext` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El `Context` da acceso a recursos/servicios/almacenamiento Android; debe respetarse el ciclo de vida del contexto recibido. |
| 279 | `json` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 288 | `key` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 289 | `raw` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 291 | `json` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 292 | `savedAt` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 297 | `cachedPath` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Forma parte de la estrategia de reutilización de resultados para evitar repetir trabajo costoso. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 298 | `imageCandidates` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 323 | `remoteCandidates` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 332 | `cacheDir` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Forma parte de la estrategia de reutilización de resultados para evitar repetir trabajo costoso. |
| 339 | `downloaded` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 349 | `connection` | `var` | `HttpURLConnection?` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo declarado es `HttpURLConnection?`. Admite `null`, así que los consumidores deben contemplar ausencia de valor. |
| 350 | `temp` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 372 | `declaredLength` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 376 | `total` | `var` | `inferido` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 379 | `count` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 412 | `header` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 413 | `count` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 415 | `jpeg` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 416 | `png` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 417 | `gif` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 418 | `webp` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 419 | `avif` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 428 | `host` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 441 | `files` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 443 | `totalBytes` | `var` | `inferido` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 452 | `digest` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 458 | `metadata` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 459 | `metaTagRegex` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 462 | `attributeRegex` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 467 | `value` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Normaliza una ausencia hacia un valor vacío/alternativo para simplificar el código consumidor. |
| 470 | `key` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 471 | `content` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 476 | `titleFromTag` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 480 | `host` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 481 | `rawImage` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 483 | `resolvedImage` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 484 | `title` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 485 | `description` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 486 | `siteName` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 499 | `host` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 515 | `current` | `var` | `inferido` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 516 | `cookies` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 518 | `connection` | `var` | `HttpURLConnection?` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo declarado es `HttpURLConnection?`. Admite `null`, así que los consumidores deben contemplar ausencia de valor. |
| 537 | `name` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 538 | `value` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 543 | `code` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 545 | `location` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 557 | `html` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 577 | `resolved` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 578 | `awemeId` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 579 | `share` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 583 | `api` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 588 | `awemeId` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 589 | `sharePreview` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 591 | `apiPreview` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 596 | `embeddedCover` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 597 | `candidates` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 610 | `sources` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 611 | `patterns` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 626 | `paths` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 628 | `connection` | `var` | `HttpURLConnection?` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo declarado es `HttpURLConnection?`. Admite `null`, así que los consumidores deben contemplar ausencia de valor. |
| 643 | `charset` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 644 | `html` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 646 | `router` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 647 | `item` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 648 | `preview` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 662 | `markerIndex` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 664 | `equalsIndex` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 666 | `index` | `var` | `inferido` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 672 | `raw` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 676 | `literal` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 677 | `wrapped` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 678 | `inner` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 690 | `depth` | `var` | `inferido` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 691 | `inString` | `var` | `inferido` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 692 | `escaped` | `var` | `inferido` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 694 | `char` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 719 | `escaped` | `var` | `inferido` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 721 | `char` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 737 | `found` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 744 | `keys` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 746 | `found` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 752 | `found` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 762 | `urls` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 775 | `video` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 776 | `coverCandidates` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 786 | `images` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 789 | `image` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 795 | `title` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 796 | `author` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 802 | `endpoints` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 821 | `body` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 822 | `root` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 823 | `item` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 824 | `video` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 825 | `coverCandidates` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 836 | `cover` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 837 | `title` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 838 | `author` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 854 | `list` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 856 | `value` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 865 | `candidates` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 871 | `score` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 883 | `keys` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 885 | `key` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 886 | `value` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 887 | `childPath` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 903 | `renderData` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 907 | `decoded` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 915 | `routerData` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 919 | `decoded` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 928 | `normalizedHtml` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 931 | `cdnUrlRegex` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 935 | `end` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El inicializador limita explícitamente el valor a un rango, evitando que dimensiones/intensidades/sizes inválidos se propaguen. |
| 936 | `context` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El `Context` da acceso a recursos/servicios/almacenamiento Android; debe respetarse el ciclo de vida del contexto recibido. |
| 947 | `current` | `var` | `inferido` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 949 | `next` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 963 | `normalized` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 966 | `results` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 978 | `normalizedPath` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 979 | `normalizedUrl` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 980 | `score` | `var` | `inferido` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1012 | `host` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1017 | `host` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1022 | `host` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1030 | `host` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1040 | `encoded` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1041 | `provider` | `val` | `String` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo declarado es `String`. No declara nulabilidad explícita. |
| 1042 | `endpoint` | `val` | `String` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo declarado es `String`. No declara nulabilidad explícita. |
| 1054 | `isVideo` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El nombre indica un estado de decisión/visibilidad/selección usado para controlar una rama de comportamiento o presentación. |
| 1056 | `type` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1061 | `connection` | `var` | `HttpURLConnection?` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo declarado es `HttpURLConnection?`. Admite `null`, así que los consumidores deben contemplar ausencia de valor. |
| 1076 | `body` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1077 | `json` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1078 | `html` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1079 | `thumbnail` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1081 | `htmlCandidates` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1082 | `candidates` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1086 | `title` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 1087 | `author` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1102 | `canonical` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 1103 | `clean` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1105 | `jsonUrl` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1106 | `connection` | `var` | `HttpURLConnection?` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo declarado es `HttpURLConnection?`. Admite `null`, así que los consumidores deben contemplar ausencia de valor. |
| 1121 | `body` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1122 | `root` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1123 | `post` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 1125 | `candidates` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1129 | `previewImages` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 1132 | `image` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 1134 | `resolutions` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1147 | `mediaMetadata` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1149 | `keys` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1151 | `media` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 1153 | `previews` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1161 | `title` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1162 | `subreddit` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1163 | `author` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1178 | `connection` | `var` | `HttpURLConnection?` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo declarado es `HttpURLConnection?`. Admite `null`, así que los consumidores deben contemplar ausencia de valor. |
| 1198 | `candidates` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1205 | `provider` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1220 | `normalized` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1224 | `explicit` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1225 | `keyRegex` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1230 | `discovered` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1246 | `lower` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1247 | `score` | `var` | `inferido` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1270 | `host` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1285 | `connection` | `var` | `HttpURLConnection?` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo declarado es `HttpURLConnection?`. Admite `null`, así que los consumidores deben contemplar ausencia de valor. |
| 1287 | `endpoint` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1301 | `body` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1302 | `json` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1303 | `thumbnail` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1304 | `title` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1305 | `author` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1318 | `SpotifyEntityTypes` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 1330 | `host` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1342 | `decoded` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1347 | `uri` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Representa una ubicación Android `Uri`; no debe asumirse que siempre corresponde a una ruta de archivo convencional. |
| 1357 | `variants` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1360 | `openUrl` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1368 | `spotifyUri` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Representa una ubicación Android `Uri`; no debe asumirse que siempre corresponde a una ruta de archivo convencional. |
| 1372 | `type` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1373 | `id` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1381 | `cleaned` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1383 | `match` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1389 | `uri` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Representa una ubicación Android `Uri`; no debe asumirse que siempre corresponde a una ruta de archivo convencional. |
| 1390 | `host` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. Normaliza una ausencia hacia un valor vacío/alternativo para simplificar el código consumidor. |
| 1394 | `segments` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1395 | `typeIndex` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1401 | `type` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1402 | `id` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1412 | `current` | `var` | `inferido` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1414 | `next` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1428 | `connection` | `var` | `HttpURLConnection?` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo declarado es `HttpURLConnection?`. Admite `null`, así que los consumidores deben contemplar ausencia de valor. |
| 1430 | `endpoint` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1444 | `body` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1446 | `json` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1447 | `title` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1448 | `author` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1449 | `thumbnail` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1450 | `provider` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 1467 | `youtubeId` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1484 | `context` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El `Context` da acceso a recursos/servicios/almacenamiento Android; debe respetarse el ciclo de vida del contexto recibido. |
| 1490 | `title` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 1491 | `subtitle` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 1492 | `imageModel` | `val` | `Any?` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo declarado es `Any?`. Admite `null`, así que los consumidores deben contemplar ausencia de valor. Actúa como selector de estrategia/perfil; su valor determina qué rama de configuración se aplica. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 1496 | `previewBackground` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1497 | `placeholderBackground` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1498 | `previewPrimaryText` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1499 | `previewSecondaryText` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1500 | `previewGraphicColor` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1501 | `placeholderTextColor` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1503 | `veryCompact` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1504 | `narrowCompact` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1600 | `intent` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1611 | `buffer` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1612 | `builder` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1614 | `remaining` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1615 | `count` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1626 | `charsetName` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 1657 | `cleanPath` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 1671 | `uri` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Representa una ubicación Android `Uri`; no debe asumirse que siempre corresponde a una ruta de archivo convencional. |
| 1672 | `host` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. Normaliza una ausencia hacia un valor vacío/alternativo para simplificar el código consumidor. |

## 6. Mapa exhaustivo de TODOS los bloques delimitados por `{}`

Esta tabla recorre todas las llaves estructurales detectadas fuera de strings/comentarios. Así se documentan también lambdas de Compose, callbacks, iteraciones y ámbitos locales que no tienen un nombre propio de función.

| Inicio–fin | Prof. | Cabecera/contexto | Qué significa ese bloque |
|---|---:|---|---|
| 79–81 | 0 | `fun extractLinkUrls(text: String): List<String> = UrlRegex.findAll(text).map` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 81–81 | 0 | `}.filter` | Filtro de colección: conserva solo los elementos cuyo predicado devuelve verdadero. |
| 86–93 | 0 | `fun noteTextForDisplay(content: String, links: List<String>): String` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 88–90 | 1 | `return if (links.size == 1 && trimmed == links.first())` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 90–92 | 1 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 96–104 | 0 | `val siteName: String?, val host: String, val imageCandidates: List<String> = emptyList(), val cachedImagePath: String? = null)` | Ámbito delimitado por llaves en profundidad 0. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 97–103 | 1 | `companion object` | Cuerpo de tipo/contenedor: agrupa propiedades y miembros bajo la misma responsabilidad y controla su visibilidad/estado compartido. |
| 98–102 | 2 | `fun basic(url: String): LinkPreviewData` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 106–249 | 0 | `private object LinkPreviewRepository` | Cuerpo de tipo/contenedor: agrupa propiedades y miembros bajo la misma responsabilidad y controla su visibilidad/estado compartido. |
| 109–111 | 1 | `private fun lockFor(url: String): Mutex = synchronized(locks)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 110–110 | 2 | `locks.getOrPut(url)` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 112–116 | 1 | `fun peek(context: Context, url: String): LinkPreviewData?` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 113–113 | 2 | `cache.get(url)?.let` | Lambda `let`: transforma/usa el receptor dentro de un ámbito corto; con llamada segura suele ejecutarse solo cuando el receptor no es nulo. |
| 114–115 | 2 | `return readPersistedPreview(context = context, url = url)?.also` | Lambda `also`: ejecuta una acción auxiliar sobre el valor y conserva el valor original como resultado de la expresión. |
| 117–139 | 1 | `suspend fun load(context: Context, url: String): LinkPreviewData = withContext(Dispatchers.IO)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 118–122 | 2 | `cache.get(url)?.let` | Lambda `let`: transforma/usa el receptor dentro de un ámbito corto; con llamada segura suele ejecutarse solo cuando el receptor no es nulo. |
| 118–121 | 3 | `cache.get(url)?.let { cached -> return@withContext ensureThumbnailCached(context = context, preview = cached).also` | Cambio de contexto de corrutina: el cuerpo se ejecuta bajo el dispatcher/contexto indicado y devuelve el resultado al contexto llamador. |
| 123–138 | 2 | `lockFor(url).withLock` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 124–125 | 3 | `cache.get(url)?.let` | Lambda `let`: transforma/usa el receptor dentro de un ámbito corto; con llamada segura suele ejecutarse solo cuando el receptor no es nulo. |
| 127–132 | 3 | `if (persisted != null)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 140–248 | 1 | `private fun fetch(url: String): LinkPreviewData` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 148–152 | 2 | `val socialProviderPreview = when` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 153–155 | 2 | `if (socialProviderPreview?.imageUrl != null \|\| socialProviderPreview?.imageCandidates?.isNotEmpty() == true)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 156–160 | 2 | `if (isSpotifyRelatedUrl(url))` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 157–158 | 3 | `findSpotifyEntityUrl(url)?.let` | Lambda `let`: transforma/usa el receptor dentro de un ámbito corto; con llamada segura suele ejecutarse solo cuando el receptor no es nulo. |
| 158–159 | 3 | `}?.let` | Lambda `let`: transforma/usa el receptor dentro de un ámbito corto; con llamada segura suele ejecutarse solo cuando el receptor no es nulo. |
| 161–166 | 2 | `if (isDouyinRelatedUrl(url))` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 162–165 | 3 | `fetchDouyinDirectPreview(url)?.let` | Lambda `let`: transforma/usa el receptor dentro de un ámbito corto; con llamada segura suele ejecutarse solo cuando el receptor no es nulo. |
| 162–164 | 4 | `fetchDouyinDirectPreview(url)?.let { preview -> if (preview.imageUrl != null \|\| preview.imageCandidates.isNotEmpty())` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 167–170 | 2 | `if (isTikTokRelatedUrl(url))` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 168–169 | 3 | `fetchTikTokOEmbed(entityUrl = url, clickUrl = url)?.let` | Lambda `let`: transforma/usa el receptor dentro de un ámbito corto; con llamada segura suele ejecutarse solo cuando el receptor no es nulo. |
| 171–173 | 2 | `if (directImageUrl(url) != null)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 172–172 | 3 | `return basic.copy(title = fileNameFromUrl(url).takeIf` | Ámbito delimitado por llaves en profundidad 3. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 175–243 | 2 | `return try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 176–188 | 3 | `connection = (URL(url).openConnection() as HttpURLConnection).apply` | Lambda `apply`: configura el receptor y devuelve ese mismo receptor; útil para inicialización encadenada sin perder la instancia. |
| 181–183 | 4 | `setRequestProperty("User-Agent", if (isDouyinRelatedUrl(url))` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 183–185 | 4 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 191–193 | 3 | `if (responseCode !in 200..399)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 195–201 | 3 | `if (isRedditRelatedUrl(url) \|\| isRedditRelatedUrl(finalUrl))` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 196–200 | 4 | `fetchRedditJsonPreview(entityUrl = finalUrl, clickUrl = url)?.let` | Lambda `let`: transforma/usa el receptor dentro de un ámbito corto; con llamada segura suele ejecutarse solo cuando el receptor no es nulo. |
| 197–199 | 5 | `preview.imageCandidates.isNotEmpty())` | Ámbito delimitado por llaves en profundidad 5. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 202–207 | 3 | `if (isMetaSocialUrl(url) \|\| isMetaSocialUrl(finalUrl))` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 203–206 | 4 | `fetchMetaOEmbedPreview(finalUrl)?.let` | Lambda `let`: transforma/usa el receptor dentro de un ámbito corto; con llamada segura suele ejecutarse solo cuando el receptor no es nulo. |
| 203–205 | 5 | `fetchMetaOEmbedPreview(finalUrl)?.let { preview -> if (preview.imageUrl != null \|\| preview.imageCandidates.isNotEmpty())` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 208–211 | 3 | `if (isTikTokRelatedUrl(url) \|\| isTikTokRelatedUrl(finalUrl))` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 209–210 | 4 | `fetchTikTokOEmbed(entityUrl = finalUrl, clickUrl = url)?.let` | Lambda `let`: transforma/usa el receptor dentro de un ámbito corto; con llamada segura suele ejecutarse solo cuando el receptor no es nulo. |
| 213–216 | 3 | `if (contentType.startsWith("image/"))` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 214–214 | 4 | `return LinkPreviewData.basic(finalUrl).copy(url = url, title = fileNameFromUrl(finalUrl).takeIf` | Ámbito delimitado por llaves en profundidad 4. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 217–219 | 3 | `if (contentType.isNotBlank() && !contentType.contains("html") && !contentType.contains("xhtml"))` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 221–222 | 3 | `val html = connection.inputStream.use` | Bloque `use`: el recurso cerrable se libera automáticamente al terminar el cuerpo, incluso si ocurre una excepción. |
| 224–230 | 3 | `if (isMetaSocialUrl(url) \|\| isMetaSocialUrl(finalUrl) \|\| isRedditRelatedUrl(url) \|\| isRedditRelatedUrl(finalUrl))` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 227–229 | 4 | `if (enriched.imageUrl != null \|\| enriched.imageCandidates.isNotEmpty())` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 231–238 | 3 | `if (isSpotifyRelatedUrl(url) \|\| isSpotifyRelatedUrl(finalUrl))` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 233–236 | 4 | `if (spotifyEntityUrl != null)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 234–235 | 5 | `fetchSpotifyOEmbed(entityUrl = spotifyEntityUrl, clickUrl = url)?.let` | Lambda `let`: transforma/usa el receptor dentro de un ámbito corto; con llamada segura suele ejecutarse solo cuando el receptor no es nulo. |
| 239–241 | 3 | `if (isDouyinRelatedUrl(url) \|\| isDouyinRelatedUrl(finalUrl))` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 243–245 | 2 | `} catch (_: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 245–247 | 2 | `} finally` | Bloque de limpieza garantizada: se ejecuta después del intento tanto si hubo éxito como si hubo excepción. |
| 255–264 | 0 | `suspend fun preloadLinkPreviews(context: Context, urls: List<String>)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 257–257 | 1 | `urls.asSequence().filter` | Filtro de colección: conserva solo los elementos cuyo predicado devuelve verdadero. |
| 257–263 | 1 | `urls.asSequence().filter { it.startsWith("http", ignoreCase = true) }.distinct().take(80).chunked(3).forEach` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 257–262 | 2 | `urls.asSequence().filter { it.startsWith("http", ignoreCase = true) }.distinct().take(80).chunked(3).forEach { batch -> coroutineScope` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 258–261 | 3 | `batch.map` | Transformación de colección: produce un nuevo elemento por cada entrada y normalmente genera una colección de salida. |
| 258–260 | 4 | `batch.map { url -> async(Dispatchers.IO)` | Transformación de colección: produce un nuevo elemento por cada entrada y normalmente genera una colección de salida. |
| 278–285 | 0 | `private fun persistPreview(context: Context, originalUrl: String, preview: LinkPreviewData)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 287–311 | 0 | `private fun readPersistedPreview(context: Context, url: String): LinkPreviewData?` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 290–308 | 1 | `return try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 293–296 | 2 | `if (savedAt <= 0L \|\| System.currentTimeMillis() - savedAt > PREVIEW_CACHE_MAX_AGE_MS)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 297–297 | 2 | `val cachedPath = json.optString("cachedImagePath").takeIf` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 297–297 | 2 | `val cachedPath = json.optString("cachedImagePath").takeIf { it.isNotBlank() && it != "null" }?.takeIf` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 298–303 | 2 | `val imageCandidates = json.optJSONArray("imageCandidates")?.let` | Lambda `let`: transforma/usa el receptor dentro de un ámbito corto; con llamada segura suele ejecutarse solo cuando el receptor no es nulo. |
| 298–302 | 3 | `val imageCandidates = json.optJSONArray("imageCandidates")?.let { array -> buildList` | Lambda `let`: transforma/usa el receptor dentro de un ámbito corto; con llamada segura suele ejecutarse solo cuando el receptor no es nulo. |
| 299–301 | 4 | `for (index in 0 until array.length())` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 300–300 | 5 | `array.optString(index).takeIf` | Ámbito delimitado por llaves en profundidad 5. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 308–310 | 1 | `} catch (_: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 313–316 | 0 | `private fun JSONObject.optNullableString(key: String): String?` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 315–315 | 1 | `return optString(key).takeIf` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 318–346 | 0 | `private fun ensureThumbnailCached(context: Context, preview: LinkPreviewData): LinkPreviewData` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 319–319 | 1 | `preview.cachedImagePath?.let(::File)?.takeIf` | Lambda `let`: transforma/usa el receptor dentro de un ámbito corto; con llamada segura suele ejecutarse solo cuando el receptor no es nulo. |
| 319–322 | 1 | `preview.cachedImagePath?.let(::File)?.takeIf { it.isFile && it.length() > 0L && looksLikeImageFile(it) }?.let` | Lambda `let`: transforma/usa el receptor dentro de un ámbito corto; con llamada segura suele ejecutarse solo cuando el receptor no es nulo. |
| 323–326 | 1 | `val remoteCandidates = buildList` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 326–326 | 1 | `}.asSequence().map` | Transformación de colección: produce un nuevo elemento por cada entrada y normalmente genera una colección de salida. |
| 326–328 | 1 | `}.asSequence().map { it.trim() }.filter` | Transformación de colección: produce un nuevo elemento por cada entrada y normalmente genera una colección de salida. |
| 329–331 | 1 | `if (remoteCandidates.isEmpty())` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 332–332 | 1 | `val cacheDir = File(context.filesDir, "link_preview_thumbnails_v6").apply` | Lambda `apply`: configura el receptor y devuelve ese mismo receptor; útil para inicialización encadenada sin perder la instancia. |
| 333–344 | 1 | `remoteCandidates.forEach` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 334–337 | 2 | `if (destination.isFile && destination.length() > 0L && looksLikeImageFile(destination))` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 338–338 | 2 | `destination.takeIf` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 340–343 | 2 | `if (downloaded)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 348–407 | 0 | `private fun downloadThumbnail(imageUrl: String, refererUrl: String, destination: File): Boolean` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 351–401 | 1 | `return try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 352–367 | 2 | `connection = (URL(imageUrl).openConnection() as HttpURLConnection).apply` | Lambda `apply`: configura el receptor y devuelve ese mismo receptor; útil para inicialización encadenada sin perder la instancia. |
| 357–359 | 3 | `setRequestProperty("User-Agent", if (isDouyinRelatedUrl(refererUrl))` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 359–361 | 3 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 369–371 | 2 | `if (connection.responseCode !in 200..299)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 373–375 | 2 | `if (declaredLength > THUMBNAIL_MAX_BYTES)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 377–388 | 2 | `connection.inputStream.use` | Bloque `use`: el recurso cerrable se libera automáticamente al terminar el cuerpo, incluso si ocurre una excepción. |
| 377–387 | 3 | `connection.inputStream.use { input -> FileOutputStream(temp).use` | Bloque `use`: el recurso cerrable se libera automáticamente al terminar el cuerpo, incluso si ocurre una excepción. |
| 378–386 | 4 | `while (true)` | Bucle `while`: se repite mientras la condición siga siendo verdadera; el cuerpo debe producir progreso para evitar un ciclo infinito. |
| 382–384 | 5 | `if (total > THUMBNAIL_MAX_BYTES)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 389–391 | 2 | `if (total <= 0L \|\| !looksLikeImageFile(temp))` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 392–394 | 2 | `if (destination.exists())` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 395–398 | 2 | `if (!temp.renameTo(destination))` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 401–403 | 1 | `} catch (_: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 403–406 | 1 | `} finally` | Bloque de limpieza garantizada: se ejecuta después del intento tanto si hubo éxito como si hubo excepción. |
| 404–404 | 2 | `temp.takeIf` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 409–425 | 0 | `private fun looksLikeImageFile(file: File): Boolean` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 411–422 | 1 | `return try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 413–413 | 2 | `val count = file.inputStream().use` | Bloque `use`: el recurso cerrable se libera automáticamente al terminar el cuerpo, incluso si ocurre una excepción. |
| 422–424 | 1 | `} catch (_: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 427–438 | 0 | `private fun providerReferer(url: String): String` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 429–437 | 1 | `return when` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 440–449 | 0 | `private fun pruneThumbnailCache(directory: File)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 441–441 | 1 | `val files = directory.listFiles()?.filter` | Filtro de colección: conserva solo los elementos cuyo predicado devuelve verdadero. |
| 441–441 | 1 | `val files = directory.listFiles()?.filter { it.isFile && !it.name.endsWith(".tmp") }?.sortedByDescending` | Filtro de colección: conserva solo los elementos cuyo predicado devuelve verdadero. |
| 443–443 | 1 | `var totalBytes = files.sumOf` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 444–448 | 1 | `files.forEachIndexed` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 444–447 | 2 | `files.forEachIndexed { index, file -> if (index >= THUMBNAIL_CACHE_MAX_FILES \|\| totalBytes > THUMBNAIL_CACHE_MAX_BYTES)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 451–455 | 0 | `private fun sha256(value: String): String` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 453–454 | 1 | `return digest.joinToString("")` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 457–489 | 0 | `private fun parseHtml(pageUrl: String, html: String): LinkPreviewData` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 465–475 | 1 | `metaTagRegex.findAll(html).forEach` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 465–469 | 2 | `metaTagRegex.findAll(html).forEach { tagMatch -> val attributes = attributeRegex.findAll(tagMatch.value).associate` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 467–467 | 3 | `val value = attr.groupValues.drop(2).firstOrNull` | Ámbito delimitado por llaves en profundidad 3. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 472–474 | 2 | `if (!key.isNullOrBlank() && !content.isNullOrBlank())` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 479–479 | 1 | `?.takeIf` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 483–483 | 1 | `val resolvedImage = rawImage?.let` | Lambda `let`: transforma/usa el receptor dentro de un ámbito corto; con llamada segura suele ejecutarse solo cuando el receptor no es nulo. |
| 498–505 | 0 | `private fun isDouyinRelatedUrl(url: String): Boolean` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 499–501 | 1 | `val host = try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 501–503 | 1 | `} catch (_: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 514–574 | 0 | `private fun resolveDouyinLink(url: String): ResolvedDouyinLink?` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 517–569 | 1 | `repeat(8)` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 519–564 | 2 | `try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 520–533 | 3 | `connection = (URL(current).openConnection() as HttpURLConnection).apply` | Lambda `apply`: configura el receptor y devuelve ese mismo receptor; útil para inicialización encadenada sin perder la instancia. |
| 528–532 | 4 | `if (cookies.isNotEmpty())` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 529–531 | 5 | `setRequestProperty("Cookie", cookies.entries.joinToString("; ")` | Ámbito delimitado por llaves en profundidad 5. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 535–536 | 3 | `connection.headerFields.filterKeys` | Filtro de colección: conserva solo los elementos cuyo predicado devuelve verdadero. |
| 536–542 | 3 | `}.values.flatten().forEach` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 539–541 | 4 | `if (name.isNotBlank())` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 544–553 | 3 | `if (code in 300..399)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 545–545 | 4 | `val location = connection.getHeaderField("Location")?.takeIf` | Ámbito delimitado por llaves en profundidad 4. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 547–551 | 4 | `extractDouyinAwemeId(pageUrl = current, html = "")?.let` | Lambda `let`: transforma/usa el receptor dentro de un ámbito corto; con llamada segura suele ejecutarse solo cuando el receptor no es nulo. |
| 548–550 | 5 | `cookieHeader = cookies.entries.joinToString("; ")` | Ámbito delimitado por llaves en profundidad 5. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 550–550 | 5 | `}.takeIf` | Ámbito delimitado por llaves en profundidad 5. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 554–556 | 3 | `if (code !in 200..299)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 557–559 | 3 | `val html = connection.inputStream.use` | Bloque `use`: el recurso cerrable se libera automáticamente al terminar el cuerpo, incluso si ocurre una excepción. |
| 561–563 | 3 | `cookieHeader = cookies.entries.joinToString("; ")` | Ámbito delimitado por llaves en profundidad 3. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 563–563 | 3 | `}.takeIf` | Ámbito delimitado por llaves en profundidad 3. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 564–566 | 2 | `} catch (_: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 566–568 | 2 | `} finally` | Bloque de limpieza garantizada: se ejecuta después del intento tanto si hubo éxito como si hubo excepción. |
| 571–573 | 1 | `cookieHeader = cookies.entries.joinToString("; ")` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 573–573 | 1 | `}.takeIf` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 576–585 | 0 | `private fun fetchDouyinDirectPreview(url: String): LinkPreviewData?` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 580–582 | 1 | `if (share != null && share.imageCandidates.isNotEmpty())` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 587–607 | 0 | `private fun enrichDouyinPreview(originalUrl: String, pageUrl: String, html: String, base: LinkPreviewData): LinkPreviewData` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 589–590 | 1 | `val sharePreview = awemeId?.let` | Lambda `let`: transforma/usa el receptor dentro de un ámbito corto; con llamada segura suele ejecutarse solo cuando el receptor no es nulo. |
| 591–593 | 1 | `val apiPreview = if (awemeId != null && (sharePreview == null \|\| sharePreview.imageCandidates.isEmpty()))` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 593–595 | 1 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 596–596 | 1 | `val embeddedCover = base.imageUrl?.takeIf` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 597–603 | 1 | `val candidates = buildList` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 603–603 | 1 | `}.filter` | Filtro de colección: conserva solo los elementos cuyo predicado devuelve verdadero. |
| 604–604 | 1 | `return base.copy(url = originalUrl, title = base.title?.takeIf` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 605–605 | 1 | `description = base.description?.takeIf` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 609–623 | 0 | `private fun extractDouyinAwemeId(pageUrl: String, html: String): String?` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 618–621 | 1 | `sources.forEach` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 618–620 | 2 | `sources.forEach { source -> patterns.forEach` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 618–618 | 3 | `sources.forEach { source -> patterns.forEach { regex -> regex.find(source)?.groupValues?.getOrNull(1)?.takeIf` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 619–619 | 3 | `?.let` | Lambda `let`: transforma/usa el receptor dentro de un ámbito corto; con llamada segura suele ejecutarse solo cuando el receptor no es nulo. |
| 625–659 | 0 | `private fun fetchDouyinSharePagePreview(awemeId: String, clickUrl: String, cookieHeader: String? = null): LinkPreviewData?` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 627–657 | 1 | `paths.forEach` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 629–652 | 2 | `try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 630–640 | 3 | `connection = (URL(shareUrl).openConnection() as HttpURLConnection).apply` | Lambda `apply`: configura el receptor y devuelve ese mismo receptor; útil para inicialización encadenada sin perder la instancia. |
| 639–639 | 4 | `cookieHeader?.takeIf` | Ámbito delimitado por llaves en profundidad 4. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 639–639 | 4 | `cookieHeader?.takeIf { it.isNotBlank() }?.let` | Lambda `let`: transforma/usa el receptor dentro de un ámbito corto; con llamada segura suele ejecutarse solo cuando el receptor no es nulo. |
| 644–645 | 3 | `val html = connection.inputStream.use` | Bloque `use`: el recurso cerrable se libera automáticamente al terminar el cuerpo, incluso si ocurre una excepción. |
| 649–651 | 3 | `if (preview.imageCandidates.isNotEmpty() \|\| !preview.title.isNullOrBlank())` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 652–654 | 2 | `} catch (_: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 654–656 | 2 | `} finally` | Bloque de limpieza garantizada: se ejecuta después del intento tanto si hubo éxito como si hubo excepción. |
| 661–686 | 0 | `private fun extractDouyinRouterData(html: String): JSONObject?` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 669–683 | 1 | `return try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 670–682 | 2 | `when (html[index])` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 671–674 | 3 | `'{' ->` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 675–680 | 3 | `'"' ->` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 683–685 | 1 | `} catch (_: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 688–715 | 0 | `private fun scanBalancedJsonObject(text: String, start: Int): String?` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 693–713 | 1 | `for (index in start until text.length)` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 695–701 | 2 | `if (inString)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 696–700 | 3 | `when` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 701–712 | 2 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 702–711 | 3 | `when (char)` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 705–710 | 4 | `'}' ->` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 707–709 | 5 | `if (depth == 0)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 717–729 | 0 | `private fun scanJsonStringLiteral(text: String, start: Int): String?` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 720–727 | 1 | `for (index in start + 1 until text.length)` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 722–726 | 2 | `when` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 731–758 | 0 | `private fun findDouyinItem(node: Any?, depth: Int = 0): JSONObject?` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 733–756 | 1 | `when (node)` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 734–749 | 2 | `is JSONObject ->` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 735–740 | 3 | `for (key in listOf("item_list", "aweme_list", "aweme_detail", "aweme"))` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 736–739 | 4 | `if (node.has(key))` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 741–743 | 3 | `if (node.has("aweme_id") && (node.has("video") \|\| node.has("images") \|\| node.has("desc")))` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 745–748 | 3 | `while (keys.hasNext())` | Bucle `while`: se repite mientras la condición siga siendo verdadera; el cuerpo debe producir progreso para evitar un ciclo infinito. |
| 750–755 | 2 | `is JSONArray ->` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 751–754 | 3 | `for (index in 0 until node.length())` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 760–772 | 0 | `private fun allJsonUrls(node: JSONObject?): List<String>` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 763–767 | 1 | `listOf("url_list", "urlList").forEach` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 764–766 | 2 | `for (index in 0 until list.length())` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 765–765 | 3 | `decodeHtml(list.optString(index)).replace("\\/", "/").takeIf` | Ámbito delimitado por llaves en profundidad 3. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 768–770 | 1 | `listOf("uri", "url").forEach` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 769–769 | 2 | `.takeIf` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 774–799 | 0 | `private fun douyinPreviewFromItem(item: JSONObject, clickUrl: String): LinkPreviewData` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 776–794 | 1 | `val coverCandidates = buildList` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 787–793 | 2 | `if (images != null)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 788–792 | 3 | `for (index in 0 until images.length())` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 794–794 | 1 | `}.filter` | Filtro de colección: conserva solo los elementos cuyo predicado devuelve verdadero. |
| 795–795 | 1 | `val title = item.optString("desc").cleanText().takeIf` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 796–796 | 1 | `val author = item.optJSONObject("author")?.optString("nickname")?.cleanText()?.takeIf` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 801–850 | 0 | `private fun fetchDouyinItemInfo(awemeId: String, clickUrl: String, cookieHeader: String? = null): LinkPreviewData?` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 805–848 | 1 | `endpoints.forEach` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 806–843 | 2 | `try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 807–816 | 3 | `connection = (URL(endpoint).openConnection() as HttpURLConnection).apply` | Lambda `apply`: configura el receptor y devuelve ese mismo receptor; útil para inicialización encadenada sin perder la instancia. |
| 815–815 | 4 | `cookieHeader?.takeIf` | Ámbito delimitado por llaves en profundidad 4. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 815–815 | 4 | `cookieHeader?.takeIf { it.isNotBlank() }?.let` | Lambda `let`: transforma/usa el receptor dentro de un ámbito corto; con llamada segura suele ejecutarse solo cuando el receptor no es nulo. |
| 818–820 | 3 | `if (connection.responseCode !in 200..299)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 821–821 | 3 | `val body = connection.inputStream.bufferedReader(Charsets.UTF_8).use` | Bloque `use`: el recurso cerrable se libera automáticamente al terminar el cuerpo, incluso si ocurre una excepción. |
| 825–835 | 3 | `val coverCandidates = buildList` | Ámbito delimitado por llaves en profundidad 3. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 837–837 | 3 | `val title = item.optString("desc").cleanText().takeIf` | Ámbito delimitado por llaves en profundidad 3. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 838–838 | 3 | `val author = item.optJSONObject("author")?.optString("nickname")?.cleanText()?.takeIf` | Ámbito delimitado por llaves en profundidad 3. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 839–842 | 3 | `if (cover != null \|\| title != null)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 843–845 | 2 | `} catch (_: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 845–847 | 2 | `} finally` | Bloque de limpieza garantizada: se ejecuta después del intento tanto si hubo éxito como si hubo excepción. |
| 852–862 | 0 | `private fun firstJsonUrl(node: JSONObject?): String?` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 855–860 | 1 | `for (index in 0 until list.length())` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 857–859 | 2 | `if (value.startsWith("http://", ignoreCase = true) \|\| value.startsWith("https://", ignoreCase = true))` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 864–944 | 0 | `private fun extractDouyinCoverUrl(pageUrl: String, html: String): String?` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 866–876 | 1 | `fun addCandidate(value: String?, path: String, bonus: Int = 0)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 867–869 | 2 | `if (value.isNullOrBlank())` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 870–875 | 2 | `extractUrlsFromDouyinValue(value).forEach` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 872–874 | 3 | `if (score > 0)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 877–902 | 1 | `fun walkJson(node: Any?, path: String = "root", depth: Int = 0)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 878–880 | 2 | `if (node == null \|\| depth > 24)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 881–901 | 2 | `when (node)` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 882–894 | 3 | `is JSONObject ->` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 884–893 | 4 | `while (keys.hasNext())` | Bucle `while`: se repite mientras la condición siga siendo verdadera; el cuerpo debe producir progreso para evitar un ciclo infinito. |
| 888–892 | 5 | `when (value)` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 895–899 | 3 | `is JSONArray ->` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 896–898 | 4 | `for (index in 0 until node.length())` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 906–914 | 1 | `if (!renderData.isNullOrBlank())` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 908–910 | 2 | `try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 910–913 | 2 | `} catch (_: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 918–925 | 1 | `if (!routerData.isNullOrBlank())` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 920–922 | 2 | `try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 922–924 | 2 | `} catch (_: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 934–942 | 1 | `cdnUrlRegex.findAll(normalizedHtml).forEach` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 936–938 | 2 | `val context = if (end >= start)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 938–940 | 2 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 943–943 | 1 | `return candidates.asSequence().distinctBy` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 943–943 | 1 | `return candidates.asSequence().distinctBy { it.second }.maxByOrNull` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 946–960 | 0 | `private fun decodeDouyinEmbeddedJson(value: String): String` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 948–958 | 1 | `repeat(3)` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 949–951 | 2 | `val next = try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 951–953 | 2 | `} catch (_: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 954–956 | 2 | `if (next == current)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 962–975 | 0 | `private fun extractUrlsFromDouyinValue(value: String): List<String>` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 968–970 | 1 | `normalized.startsWith("//"))` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 973–973 | 1 | `option = RegexOption.IGNORE_CASE).findAll(normalized).map` | Transformación de colección: produce un nuevo elemento por cada entrada y normalmente genera una colección de salida. |
| 977–1009 | 0 | `private fun douyinCoverScore(path: String, url: String): Int` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 981–988 | 1 | `when` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 989–991 | 1 | `if (normalizedPath.contains("avatar") \|\| normalizedPath.contains("author") && !normalizedPath.contains("cover"))` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 992–994 | 1 | `if (normalizedPath.contains("music") \|\| normalizedPath.contains("emoji") \|\| normalizedPath.contains("icon"))` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 995–997 | 1 | `if (normalizedUrl.contains("douyinpic.com"))` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 998–1000 | 1 | `if (normalizedUrl.contains("byteimg.com"))` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 1001–1003 | 1 | `if (normalizedUrl.contains("douyincdn.com"))` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 1005–1007 | 1 | `normalizedUrl.contains(".png"))` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 1011–1014 | 0 | `private fun isInstagramRelatedUrl(url: String): Boolean` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 1016–1019 | 0 | `private fun isThreadsRelatedUrl(url: String): Boolean` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 1021–1025 | 0 | `private fun isFacebookRelatedUrl(url: String): Boolean` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 1029–1032 | 0 | `private fun isRedditRelatedUrl(url: String): Boolean` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 1039–1095 | 0 | `private fun fetchMetaOEmbedPreview(url: String): LinkPreviewData?` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 1043–1060 | 1 | `when` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 1044–1047 | 2 | `isInstagramRelatedUrl(url) ->` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 1048–1051 | 2 | `isThreadsRelatedUrl(url) ->` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 1052–1058 | 2 | `isFacebookRelatedUrl(url) ->` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 1062–1090 | 1 | `return try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 1063–1071 | 2 | `connection = (URL(endpoint).openConnection() as HttpURLConnection).apply` | Lambda `apply`: configura el receptor y devuelve ese mismo receptor; útil para inicialización encadenada sin perder la instancia. |
| 1073–1075 | 2 | `if (connection.responseCode !in 200..299)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 1076–1076 | 2 | `val body = connection.inputStream.bufferedReader(Charsets.UTF_8).use` | Bloque `use`: el recurso cerrable se libera automáticamente al terminar el cuerpo, incluso si ocurre una excepción. |
| 1080–1080 | 2 | `?.takeIf` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 1082–1085 | 2 | `val candidates = buildList` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 1087–1087 | 2 | `val author = json.optString("author_name").cleanText().takeIf` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 1089–1089 | 2 | `json.optString("provider_name").takeIf` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 1090–1092 | 1 | `} catch (_: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 1092–1094 | 1 | `} finally` | Bloque de limpieza garantizada: se ejecuta después del intento tanto si hubo éxito como si hubo excepción. |
| 1101–1172 | 0 | `private fun fetchRedditJsonPreview(entityUrl: String, clickUrl: String): LinkPreviewData?` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 1107–1167 | 1 | `return try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 1108–1116 | 2 | `connection = (URL(jsonUrl).openConnection() as HttpURLConnection).apply` | Lambda `apply`: configura el receptor y devuelve ese mismo receptor; útil para inicialización encadenada sin perder la instancia. |
| 1118–1120 | 2 | `if (connection.responseCode !in 200..299)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 1121–1121 | 2 | `val body = connection.inputStream.bufferedReader(Charsets.UTF_8).use` | Bloque `use`: el recurso cerrable se libera automáticamente al terminar el cuerpo, incluso si ocurre una excepción. |
| 1126–1128 | 2 | `fun addCandidate(value: String?)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 1127–1127 | 3 | `value?.let(::decodeHtml)?.takeIf` | Lambda `let`: transforma/usa el receptor dentro de un ámbito corto; con llamada segura suele ejecutarse solo cuando el receptor no es nulo. |
| 1130–1141 | 2 | `if (previewImages != null)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 1131–1140 | 3 | `for (index in 0 until previewImages.length())` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 1135–1139 | 4 | `if (resolutions != null)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 1136–1138 | 5 | `for (r in resolutions.length() - 1 downTo 0)` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 1148–1160 | 2 | `if (mediaMetadata != null)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 1150–1159 | 3 | `while (keys.hasNext())` | Bucle `while`: se repite mientras la condición siga siendo verdadera; el cuerpo debe producir progreso para evitar un ciclo infinito. |
| 1154–1158 | 4 | `if (previews != null)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 1155–1157 | 5 | `for (index in previews.length() - 1 downTo 0)` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 1161–1161 | 2 | `val title = post.optString("title").cleanText().takeIf` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 1162–1162 | 2 | `val subreddit = post.optString("subreddit_name_prefixed").takeIf` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 1163–1163 | 2 | `val author = post.optString("author").takeIf` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 1164–1164 | 2 | `LinkPreviewData(url = clickUrl, title = title ?: "Reddit", description = listOfNotNull(subreddit, author?.let` | Lambda `let`: transforma/usa el receptor dentro de un ámbito corto; con llamada segura suele ejecutarse solo cuando el receptor no es nulo. |
| 1165–1165 | 2 | `.joinToString(" · ").takeIf` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 1167–1169 | 1 | `} catch (_: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 1169–1171 | 1 | `} finally` | Bloque de limpieza garantizada: se ejecuta después del intento tanto si hubo éxito como si hubo excepción. |
| 1174–1194 | 0 | `private fun resolveSimpleRedirect(url: String): String?` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 1175–1177 | 1 | `if (!safeHost(url).equals("redd.it", ignoreCase = true) && !safeHost(url).endsWith(".redd.it", ignoreCase = true))` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 1179–1189 | 1 | `return try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 1180–1186 | 2 | `connection = (URL(url).openConnection() as HttpURLConnection).apply` | Lambda `apply`: configura el receptor y devuelve ese mismo receptor; útil para inicialización encadenada sin perder la instancia. |
| 1189–1191 | 1 | `} catch (_: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 1191–1193 | 1 | `} finally` | Bloque de limpieza garantizada: se ejecuta después del intento tanto si hubo éxito como si hubo excepción. |
| 1197–1215 | 0 | `providerPreview: LinkPreviewData?): LinkPreviewData` | Ámbito delimitado por llaves en profundidad 0. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 1198–1204 | 1 | `val candidates = buildList` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 1204–1204 | 1 | `}.filter` | Filtro de colección: conserva solo los elementos cuyo predicado devuelve verdadero. |
| 1205–1211 | 1 | `val provider = when` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 1212–1212 | 1 | `return base.copy(url = originalUrl, title = base.title?.takeIf` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 1213–1213 | 1 | `base.description?.takeIf` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 1214–1214 | 1 | `providerPreview?.siteName?.takeIf` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 1218–1240 | 0 | `private fun extractSocialImageCandidates(pageUrl: String, html: String): List<String>` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 1228–1229 | 1 | `keyRegex.findAll(normalized).forEach` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 1228–1228 | 2 | `keyRegex.findAll(normalized).forEach { match -> match.groupValues.getOrNull(1)?.let` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 1230–1230 | 1 | `val discovered = UrlRegex.findAll(normalized).map` | Transformación de colección: produce un nuevo elemento por cada entrada y normalmente genera una colección de salida. |
| 1231–1231 | 1 | `.mapNotNull` | Transformación de colección: produce un nuevo elemento por cada entrada y normalmente genera una colección de salida. |
| 1231–1237 | 1 | `.mapNotNull { resolveUrl(pageUrl, it) }.filter` | Transformación de colección: produce un nuevo elemento por cada entrada y normalmente genera una colección de salida. |
| 1238–1238 | 1 | `return (explicit + discovered).asSequence().filter` | Filtro de colección: conserva solo los elementos cuyo predicado devuelve verdadero. |
| 1245–1267 | 0 | `private fun scoreSocialImageCandidate(url: String): Int` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 1269–1276 | 0 | `private fun isTikTokRelatedUrl(url: String): Boolean` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 1270–1272 | 1 | `val host = try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 1272–1274 | 1 | `} catch (_: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 1284–1316 | 0 | `private fun fetchTikTokOEmbed(entityUrl: String, clickUrl: String): LinkPreviewData?` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 1286–1311 | 1 | `return try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 1288–1296 | 2 | `connection = (URL(endpoint).openConnection() as HttpURLConnection).apply` | Lambda `apply`: configura el receptor y devuelve ese mismo receptor; útil para inicialización encadenada sin perder la instancia. |
| 1298–1300 | 2 | `if (connection.responseCode !in 200..299)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 1301–1301 | 2 | `val body = connection.inputStream.bufferedReader(Charsets.UTF_8).use` | Bloque `use`: el recurso cerrable se libera automáticamente al terminar el cuerpo, incluso si ocurre una excepción. |
| 1303–1303 | 2 | `val thumbnail = json.optString("thumbnail_url").takeIf` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 1304–1304 | 2 | `val title = json.optString("title").cleanText().takeIf` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 1305–1305 | 2 | `val author = json.optString("author_name").cleanText().takeIf` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 1306–1308 | 2 | `if (thumbnail == null && title == null)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 1311–1313 | 1 | `} catch (_: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 1313–1315 | 1 | `} finally` | Bloque de limpieza garantizada: se ejecuta después del intento tanto si hubo éxito como si hubo excepción. |
| 1329–1336 | 0 | `private fun isSpotifyRelatedUrl(url: String): Boolean` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 1330–1332 | 1 | `val host = try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 1332–1334 | 1 | `} catch (_: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 1338–1354 | 0 | `private fun findSpotifyEntityUrl(url: String): String?` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 1339–1341 | 1 | `normalizeSpotifyEntityUrl(url)?.let` | Lambda `let`: transforma/usa el receptor dentro de un ámbito corto; con llamada segura suele ejecutarse solo cuando el receptor no es nulo. |
| 1343–1345 | 1 | `normalizeSpotifyEntityUrl(decoded)?.let` | Lambda `let`: transforma/usa el receptor dentro de un ámbito corto; con llamada segura suele ejecutarse solo cuando el receptor no es nulo. |
| 1346–1351 | 1 | `return try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 1349–1350 | 2 | `).firstNotNullOfOrNull` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 1351–1353 | 1 | `} catch (_: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 1356–1378 | 0 | `private fun findSpotifyEntityUrlInText(text: String): String?` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 1359–1376 | 1 | `variants.forEach` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 1363–1367 | 2 | `if (openUrl != null)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 1364–1366 | 3 | `normalizeSpotifyEntityUrl(openUrl)?.let` | Lambda `let`: transforma/usa el receptor dentro de un ámbito corto; con llamada segura suele ejecutarse solo cuando el receptor no es nulo. |
| 1371–1375 | 2 | `if (spotifyUri != null)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 1380–1409 | 0 | `private fun normalizeSpotifyEntityUrl(candidate: String): String?` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 1382–1387 | 1 | `if (cleaned.startsWith("spotify:", ignoreCase = true))` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 1388–1406 | 1 | `return try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 1391–1393 | 2 | `if (host != "open.spotify.com")` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 1395–1397 | 2 | `val typeIndex = segments.indexOfFirst` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 1398–1400 | 2 | `if (typeIndex < 0 \|\| typeIndex + 1 >= segments.size)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 1402–1404 | 2 | `val id = segments[typeIndex + 1].substringBefore('?').substringBefore('#').takeIf` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 1403–1403 | 3 | `value.all` | Ámbito delimitado por llaves en profundidad 3. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 1406–1408 | 1 | `} catch (_: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 1411–1425 | 0 | `private fun decodeUrlRepeatedly(value: String): String` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 1413–1423 | 1 | `repeat(3)` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 1414–1416 | 2 | `val next = try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 1416–1418 | 2 | `} catch (_: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 1419–1421 | 2 | `if (next == current)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 1427–1461 | 0 | `private fun fetchSpotifyOEmbed(entityUrl: String, clickUrl: String): LinkPreviewData?` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 1429–1456 | 1 | `return try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 1431–1439 | 2 | `connection = (URL(endpoint).openConnection() as HttpURLConnection).apply` | Lambda `apply`: configura el receptor y devuelve ese mismo receptor; útil para inicialización encadenada sin perder la instancia. |
| 1441–1443 | 2 | `if (connection.responseCode !in 200..299)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 1444–1445 | 2 | `val body = connection.inputStream.use` | Bloque `use`: el recurso cerrable se libera automáticamente al terminar el cuerpo, incluso si ocurre una excepción. |
| 1447–1447 | 2 | `val title = json.optString("title").takeIf` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 1448–1448 | 2 | `val author = json.optString("author_name").takeIf` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 1449–1449 | 2 | `val thumbnail = json.optString("thumbnail_url").takeIf` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 1450–1450 | 2 | `val provider = json.optString("provider_name").takeIf` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 1451–1453 | 2 | `if (title == null && thumbnail == null)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 1456–1458 | 1 | `} catch (_: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 1458–1460 | 1 | `} finally` | Bloque de limpieza garantizada: se ejecuta después del intento tanto si hubo éxito como si hubo excepción. |
| 1463–1480 | 0 | `private fun LinkPreviewData.withKnownProviderFallback(): LinkPreviewData` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 1464–1466 | 1 | `if (!imageUrl.isNullOrBlank())` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 1468–1479 | 1 | `return when` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 1470–1470 | 2 | `?.takeIf` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 1471–1471 | 2 | `isSpotifyRelatedUrl(url) -> copy(siteName = "Spotify", title = title?.takeIf` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 1472–1472 | 2 | `isTikTokRelatedUrl(url) -> copy(siteName = "TikTok", title = title?.takeIf` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 1473–1473 | 2 | `isDouyinRelatedUrl(url) -> copy(siteName = "Douyin", title = title?.takeIf` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 1474–1474 | 2 | `isInstagramRelatedUrl(url) -> copy(siteName = "Instagram", title = title?.takeIf` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 1475–1475 | 2 | `isThreadsRelatedUrl(url) -> copy(siteName = "Threads", title = title?.takeIf` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 1476–1476 | 2 | `isFacebookRelatedUrl(url) -> copy(siteName = "Facebook", title = title?.takeIf` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 1477–1477 | 2 | `isRedditRelatedUrl(url) -> copy(siteName = "Reddit", title = title?.takeIf` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 1483–1564 | 0 | `fun LinkPreviewCard(url: String, modifier: Modifier = Modifier, compact: Boolean = false, textColorMode: String = "auto")` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 1487–1489 | 1 | `) ?: LinkPreviewData.basic(url), key1 = url)` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 1490–1490 | 1 | `val title = preview.title?.takeIf` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 1490–1490 | 1 | `val title = preview.title?.takeIf { it.isNotBlank() }?: preview.siteName?.takeIf` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 1491–1491 | 1 | `val subtitle = preview.description?.takeIf` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 1492–1492 | 1 | `val imageModel: Any? = preview.cachedImagePath?.let(::File)?.takeIf` | Lambda `let`: transforma/usa el receptor dentro de un ámbito corto; con llamada segura suele ejecutarse solo cuando el receptor no es nulo. |
| 1502–1563 | 1 | `BoxWithConstraints(modifier = modifier.fillMaxWidth())` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 1505–1508 | 2 | `Surface(onClick =` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 1508–1562 | 2 | `}, modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium, color = previewBackground, tonalElevation = 1.dp)` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 1509–1551 | 3 | `if (compact)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 1510–1550 | 4 | `when` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 1511–1529 | 5 | `veryCompact ->` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 1512–1528 | 6 | `Column(modifier = Modifier.fillMaxWidth())` | Lambda de contenido de UI Compose: declara hijos y configuración visual dentro del contenedor. El orden y los `Modifier` influyen en medida, posición, dibujo e interacción. |
| 1513–1523 | 7 | `Surface(modifier = Modifier.fillMaxWidth().aspectRatio(16f / 9f), color = placeholderBackground)` | Lambda de contenido de UI Compose: declara hijos y configuración visual dentro del contenedor. El orden y los `Modifier` influyen en medida, posición, dibujo e interacción. |
| 1514–1522 | 8 | `Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center)` | Lambda de contenido de UI Compose: declara hijos y configuración visual dentro del contenedor. El orden y los `Modifier` influyen en medida, posición, dibujo e interacción. |
| 1515–1518 | 9 | `if (imageModel != null)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 1518–1521 | 9 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 1530–1549 | 5 | `else ->` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 1531–1548 | 6 | `Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically)` | Lambda de contenido de UI Compose: declara hijos y configuración visual dentro del contenedor. El orden y los `Modifier` influyen en medida, posición, dibujo e interacción. |
| 1533–1543 | 7 | `if (narrowCompact) 68.dp else 86.dp), color = placeholderBackground)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 1534–1542 | 8 | `Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center)` | Lambda de contenido de UI Compose: declara hijos y configuración visual dentro del contenedor. El orden y los `Modifier` influyen en medida, posición, dibujo e interacción. |
| 1535–1538 | 9 | `if (imageModel != null)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 1538–1541 | 9 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 1551–1561 | 3 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 1552–1560 | 4 | `Column(modifier = Modifier.fillMaxWidth())` | Lambda de contenido de UI Compose: declara hijos y configuración visual dentro del contenedor. El orden y los `Modifier` influyen en medida, posición, dibujo e interacción. |
| 1553–1556 | 5 | `if (imageModel != null)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 1568–1596 | 0 | `showSubtitle: Boolean = true, primaryTextColor: Color, secondaryTextColor: Color, graphicColor: Color, modifier: Modifier = Modifier)` | Ámbito delimitado por llaves en profundidad 0. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 1569–1595 | 1 | `Column(modifier = modifier, verticalArrangement = Arrangement.Center)` | Lambda de contenido de UI Compose: declara hijos y configuración visual dentro del contenedor. El orden y los `Modifier` influyen en medida, posición, dibujo e interacción. |
| 1570–1576 | 2 | `Row(verticalAlignment = Alignment.CenterVertically)` | Lambda de contenido de UI Compose: declara hijos y configuración visual dentro del contenedor. El orden y los `Modifier` influyen en medida, posición, dibujo e interacción. |
| 1578–1582 | 2 | `Text(text = title, color = primaryTextColor, style = when` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 1583–1594 | 2 | `if (showSubtitle && !subtitle.isNullOrBlank())` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 1585–1587 | 3 | `Text(text = subtitle, color = secondaryTextColor, style = if (veryCompact)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 1587–1589 | 3 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 1589–1593 | 3 | `}, maxLines = when` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 1598–1607 | 0 | `private fun openExternalLink(context: Context, url: String)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 1599–1604 | 1 | `try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 1600–1602 | 2 | `val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply` | Lambda `apply`: configura el receptor y devuelve ese mismo receptor; útil para inicialización encadenada sin perder la instancia. |
| 1604–1606 | 1 | `} catch (_: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 1609–1623 | 0 | `private fun readLimitedHtml(reader: InputStreamReader): String` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 1610–1622 | 1 | `reader.use` | Bloque `use`: el recurso cerrable se libera automáticamente al terminar el cuerpo, incluso si ocurre una excepción. |
| 1613–1620 | 2 | `while (builder.length < MAX_HTML_CHARS)` | Bucle `while`: se repite mientras la condición siga siendo verdadera; el cuerpo debe producir progreso para evitar un ciclo infinito. |
| 1616–1618 | 3 | `if (count <= 0)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 1625–1637 | 0 | `private fun charsetFromContentType(contentType: String?): Charset` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 1628–1634 | 1 | `return try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 1629–1631 | 2 | `if (charsetName.isNotBlank())` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 1631–1633 | 2 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 1634–1636 | 1 | `} catch (_: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 1643–1645 | 0 | `private fun firstNonBlank(vararg values: String?): String? = values.firstOrNull` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 1646–1648 | 0 | `private fun resolveUrl(baseUrl: String, candidate: String): String? = try` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 1648–1650 | 0 | `} catch (_: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 1651–1653 | 0 | `private fun safeHost(url: String): String = try` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 1652–1652 | 1 | `URI(url).host?.removePrefix("www.")?.takeIf` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 1653–1655 | 0 | `} catch (_: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 1656–1663 | 0 | `private fun directImageUrl(url: String): String?` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 1658–1660 | 1 | `return if (listOf(".jpg", ".jpeg", ".png", ".webp", ".gif", ".avif").any(cleanPath::endsWith))` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 1660–1662 | 1 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 1665–1667 | 0 | `private fun fileNameFromUrl(url: String): String = try` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 1666–1666 | 1 | `URI(url).path?.substringAfterLast('/')?.takeIf` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 1667–1669 | 0 | `} catch (_: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 1670–1689 | 0 | `private fun youtubeVideoId(url: String): String? = try` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 1673–1688 | 1 | `when` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 1674–1674 | 2 | `host == "youtu.be" -> uri.pathSegments.firstOrNull()?.takeIf` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 1675–1686 | 2 | `host.endsWith("youtube.com") ->` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 1676–1676 | 3 | `uri.getQueryParameter("v")?.takeIf` | Ámbito delimitado por llaves en profundidad 3. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 1676–1685 | 3 | `uri.getQueryParameter("v")?.takeIf { it.isNotBlank() }?: uri.pathSegments.let` | Lambda `let`: transforma/usa el receptor dentro de un ámbito corto; con llamada segura suele ejecutarse solo cuando el receptor no es nulo. |
| 1677–1679 | 4 | `segments.indexOfFirst` | Ámbito delimitado por llaves en profundidad 4. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 1680–1682 | 4 | `if (markerIndex >= 0 && markerIndex + 1 < segments.size)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 1682–1684 | 4 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 1689–1691 | 0 | `} catch (_: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |

## 7. Lectura de seguridad antes de tocar este archivo

La documentación describe **lo que el código actual ya hace**. No constituye una propuesta de refactorización. Si se quisiera cambiar algo en el futuro, primero habría que preservar contratos públicos, claves de configuración, restricciones de API Android, comportamiento de Compose, lifecycle, operaciones de I/O y compatibilidad con los datos ya persistidos. En esta entrega no se hizo ninguna de esas modificaciones.

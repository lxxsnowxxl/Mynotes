# LinkPreviewRepository.kt — documentación del código actual
**Ruta real:** `app/src/main/java/com/example/mynotes/links/LinkPreviewRepository.kt`  **SHA-256:** `d4b2960786dec39421214e2588c2684994e39b4fce73d06dd941902fa9ee0cae`  **Líneas:** 606 · **Bytes:** 19825 · **Imports:** 19 · **Declaraciones detectadas:** 23
> Documento generado fuera de `app/` a partir de lectura del código. El fuente es la única fuente de verdad; no se modificó para generar esta documentación.
## 1. Responsabilidad

Obtención, normalización y caché de metadatos para vistas previas de enlaces.
## 2. Package e imports

Package declarado: `com.example.mynotes.links`.

### Android / Jetpack / Compose

`android.content.Context`, `android.os.Build`, `android.text.Html`, `android.net.Uri`

### Kotlin / Coroutines / Java

`kotlinx.coroutines.Dispatchers`, `kotlinx.coroutines.withContext`, `kotlinx.coroutines.sync.Semaphore`, `kotlinx.coroutines.sync.withPermit`, `java.io.BufferedInputStream`, `java.io.BufferedReader`, `java.io.File`, `java.io.InputStreamReader`, `java.net.HttpURLConnection`, `java.net.URL`, `java.nio.charset.Charset`, `java.security.MessageDigest`, `java.util.Locale`, `kotlin.math.min`

### Terceros / otros

`org.json.JSONObject`

## 3. Declaraciones detectadas

| Línea | Tipo | Nombre | Firma/inicio |
|---:|---|---|---|
| 30 | `class` | `LinkPreviewData` | `data class LinkPreviewData(` |
| 51 | `object` | `LinkPreviewRepository` | `` |
| 68 | `fun` | `normalizeUrl` | `` |
| 103 | `fun` | `invalidate` | `` |
| 174 | `fun` | `fetchPreview` | `` |
| 238 | `class` | `ParsedMetadata` | `` |
| 246 | `fun` | `parseHtml` | `` |
| 334 | `fun` | `parseAttributes` | `` |
| 354 | `fun` | `findPageIcon` | `` |
| 373 | `fun` | `resolveUrl` | `` |
| 380 | `fun` | `youtubeThumbnail` | `` |
| 401 | `fun` | `openConnection` | `` |
| 416 | `fun` | `readHtml` | `` |
| 447 | `fun` | `charsetFromContentType` | `` |
| 462 | `fun` | `downloadPreviewImage` | `` |
| 519 | `fun` | `cacheDirectory` | `` |
| 524 | `fun` | `readCache` | `` |
| 551 | `fun` | `writeCache` | `` |
| 569 | `fun` | `fallback` | `` |
| 582 | `fun` | `decodeHtml` | `` |
| 592 | `fun` | `stripTags` | `` |
| 597 | `fun` | `firstNotBlank` | `` |
| 600 | `fun` | `sha256` | `` |

## 4. Estado, efectos y límites observables

- **Coroutines:** 7 aparición/apariciones.
- **I/O/red:** 15 aparición/apariciones.
- **try/catch:** 3 aparición/apariciones.
- **safe calls:** 27 aparición/apariciones.

Estas cifras son indicadores de superficie de cambio, no diagnósticos de error. Cualquier modificación debe preservar contratos de persistencia, lifecycle, límites numéricos y nulabilidad visibles en el fuente.

## 5. Dependencias internas directas

No importa directamente otros símbolos `com.example.mynotes.*`.

## 6. Recursos Android referenciados

No se detectaron referencias `R.*` directas.

## 7. Puntos de revisión al modificarlo

- No degradar calidad, rutas persistentes ni cachés de adjuntos/miniaturas sin una prueba explícita.
- Evitar trabajo de red/decodificación en el frame de scroll y conservar caché/placeholder.

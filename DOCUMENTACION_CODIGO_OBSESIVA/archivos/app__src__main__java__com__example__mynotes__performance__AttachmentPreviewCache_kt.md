# AttachmentPreviewCache.kt — documentación exhaustiva por bloques

**Ruta de código real:** `app/src/main/java/com/example/mynotes/performance/AttachmentPreviewCache.kt`  
**SHA-256 del archivo ejecutable sin tocar:** `0748d29b164821c2583f9c70b3da5ff6fb50ea85c901f6815038b9647b299b38`  
**Líneas del código real:** 744

> **Garantía:** esta documentación se genera en una carpeta paralela. El archivo Kotlin/Kotlin DSL anterior no se modifica. Los fragmentos mostrados abajo son copias de lectura; la aplicación compila usando únicamente el archivo original de `app/...`.

## 1. Papel del archivo

Sistema central de miniaturas y caché de adjuntos. Genera representaciones ligeras de imágenes, video, audio, PDF y documentos, conserva variantes en memoria/disco y adapta la resolución al perfil de rendimiento seleccionado.

**Arquitectura.** Es una pieza de rendimiento transversal: las tarjetas, el editor y otras vistas solicitan previews aquí en lugar de decodificar repetidamente los archivos originales. También implementa precalentamiento e invalidación.

**Flujo general.** Flujo típico: un composable solicita un preview -> se calcula la variante según el perfil de rendimiento -> se consulta memoria -> se consulta disco -> si falta, se genera desde el archivo fuente -> se almacena para próximas lecturas. `prewarm` adelanta ese trabajo y `invalidate` evita miniaturas obsoletas.

## 2. Package e imports

El `package` es `com.example.mynotes.performance`. Determina el namespace lógico del archivo, resolución de visibilidad `internal`/package-related tooling y la ruta conceptual desde la que otras clases lo importan.

El archivo declara **26 imports**. Cada import evita usar el nombre totalmente calificado en el cuerpo y, además, revela las dependencias técnicas del bloque:

**Android/plataforma:** `android.content.Context`, `android.graphics.Bitmap`, `android.graphics.BitmapFactory`, `android.graphics.Matrix`, `android.graphics.pdf.PdfRenderer`, `android.media.ExifInterface`, `android.media.MediaMetadataRetriever`, `android.media.ThumbnailUtils`, `android.net.Uri`, `android.os.Build`, `android.os.ParcelFileDescriptor`, `android.provider.MediaStore`, `android.util.LruCache`, `android.util.Xml`.

**Kotlin/corrutinas/Java:** `kotlinx.coroutines.Dispatchers`, `kotlinx.coroutines.withContext`, `kotlinx.coroutines.sync.Semaphore`, `kotlinx.coroutines.sync.withPermit`, `java.io.BufferedInputStream`, `java.io.File`, `java.io.FileInputStream`, `java.io.InputStream`, `java.security.MessageDigest`, `java.util.zip.ZipInputStream`, `kotlin.math.max`.

**Otras librerías:** `org.xmlpull.v1.XmlPullParser`.

## 3. Restricciones e invariantes visibles en el archivo

- **Nulabilidad segura (23 aparición/apariciones):** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`.
- **Fallback nulo (21 aparición/apariciones):** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula.
- **Límite numérico (4 aparición/apariciones):** Se fuerza el valor a un intervalo o umbral para impedir que salgan valores fuera de los límites esperados.
- **Normalización vacía (1 aparición/apariciones):** `orEmpty` transforma una referencia nula en una colección/cadena vacía, simplificando el consumo posterior.
- **Guardia de API (2 aparición/apariciones):** Se condiciona una API según la versión de Android para no ejecutar llamadas incompatibles en dispositivos antiguos.
- **Trabajo IO (6 aparición/apariciones):** El trabajo potencialmente bloqueante se desplaza al dispatcher de I/O en vez de ocupar el hilo principal.

## 4. Bloques de código, uno por uno

### 4.1 `AttachmentPreviewCache` — object, líneas 46–744

```kotlin
object AttachmentPreviewCache {
    // … el cuerpo completo permanece en el archivo real; sus miembros se documentan individualmente abajo …
}
```

#### Qué hace y por qué existe

Sistema central de miniaturas y caché de adjuntos. Genera representaciones ligeras de imágenes, video, audio, PDF y documentos, conserva variantes en memoria/disco y adapta la resolución al perfil de rendimiento seleccionado.

#### Contrato de la declaración


#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 52 | `val previewLoadGate` | `inferido` | `Semaphore(permits = 2)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 57 | `val aspectRatio` | `Float` | `(sin inicializador en la declaración)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo declarado es `Float`. No declara nulabilidad explícita. |
| 64 | `val DOCX_CACHE_CHARACTERS` | `inferido` | `4_000` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Forma parte de la estrategia de reutilización de resultados para evitar repetir trabajo costoso. Está marcada `const`, por lo que es una constante de tiempo de compilación y solo puede usar un tipo/valor admitido por Kotlin para constantes. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 76 | `val videoHeight` | `Int, val audioArtSize: Int, val pdfWidth: Int, val imageJpegQuality: Int, val videoJpegQuality: Int,` | `(sin inicializador en la declaración)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo declarado es `Int, val audioArtSize: Int, val pdfWidth: Int, val imageJpegQuality: Int, val videoJpegQuality: Int,`. No declara nulabilidad explícita. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. |
| 77 | `val audioJpegQuality` | `Int, val pdfJpegQuality: Int)` | `(sin inicializador en la declaración)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo declarado es `Int, val pdfJpegQuality: Int)`. No declara nulabilidad explícita. |
| 99 | `val memoryCacheKb` | `inferido` | `(Runtime.getRuntime().maxMemory() / 1024L / 16L).coerceIn(8L * 1024L, 24L * 1024L).toInt()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Forma parte de la estrategia de reutilización de resultados para evitar repetir trabajo costoso. El inicializador limita explícitamente el valor a un rango, evitando que dimensiones/intensidades/sizes inválidos se propaguen. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 100 | `val bitmapMemoryCache` | `inferido` | `object : LruCache<String, Bitmap>(memoryCacheKb) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Es una caché acotada por política LRU: privilegia entradas recientes y permite expulsión automática cuando se supera el límite. Forma parte de la estrategia de reutilización de resultados para evitar repetir trabajo costoso. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 118 | `val directory` | `inferido` | `cacheDirectory(context, "image_previews")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 119 | `val key` | `inferido` | `cacheKey(uri)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 120 | `val profile` | `inferido` | `previewProfile(performanceMode)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 121 | `val profileKey` | `inferido` | `"$key-${profile.cacheTag}"` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 122 | `val memoryKey` | `inferido` | `"image:$profileKey"` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 123 | `val cacheFile` | `inferido` | `File(directory, "$profileKey.jpg")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Forma parte de la estrategia de reutilización de resultados para evitar repetir trabajo costoso. |
| 133 | `val bitmap` | `inferido` | `decodeImageThumbnail(context = context, uri = uri, maxWidth = profile.imageWidth, maxHeight = profil…` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 146 | `val directory` | `inferido` | `cacheDirectory(context, "video_previews")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 147 | `val key` | `inferido` | `cacheKey(uri)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 148 | `val profile` | `inferido` | `previewProfile(performanceMode)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 149 | `val profileKey` | `inferido` | `"$key-${profile.cacheTag}"` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 150 | `val imageFile` | `inferido` | `File(directory, "$profileKey.jpg")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 151 | `val metadataFile` | `inferido` | `File(directory, "$profileKey.meta")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 152 | `val memoryKey` | `inferido` | `"video:$profileKey"` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 153 | `val cachedMetadata` | `inferido` | `readMediaMetadata(metadataFile)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Forma parte de la estrategia de reutilización de resultados para evitar repetir trabajo costoso. |
| 154 | `val memoryBitmap` | `inferido` | `bitmapMemoryCache.get(memoryKey)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 158 | `val cachedBitmap` | `inferido` | `decodeCachedBitmap(imageFile)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Forma parte de la estrategia de reutilización de resultados para evitar repetir trabajo costoso. |
| 163 | `val retriever` | `inferido` | `MediaMetadataRetriever()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 166 | `val duration` | `inferido` | `retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull()?: 0L` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 167 | `val rawWidth` | `inferido` | `retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)?.toIntOrNull()?: 0` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 168 | `val rawHeight` | `inferido` | `retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)?.toIntOrNull()?: 0` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 169 | `val rotation` | `inferido` | `retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)?.toIntOrNull()?: 0` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 170 | `val width` | `inferido` | `if (rotation == 90 \|\| rotation == 270) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. |
| 175 | `val height` | `inferido` | `if (rotation == 90 \|\| rotation == 270) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. |
| 180 | `val bitmap` | `inferido` | `createVideoFrame(context = context, uri = uri, retriever = retriever, sourceWidth = width, sourceHei…` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 188 | `val result` | `inferido` | `MediaPreview(bitmap = bitmap,` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 211 | `val directory` | `inferido` | `cacheDirectory(context, "audio_previews")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 212 | `val key` | `inferido` | `cacheKey(uri)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 213 | `val profile` | `inferido` | `previewProfile(performanceMode)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 214 | `val profileKey` | `inferido` | `"$key-${profile.cacheTag}"` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 215 | `val imageFile` | `inferido` | `File(directory, "$profileKey.jpg")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 216 | `val metadataFile` | `inferido` | `File(directory, "$profileKey.meta")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 217 | `val memoryKey` | `inferido` | `"audio:$profileKey"` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 218 | `val cachedMetadata` | `inferido` | `readMediaMetadata(metadataFile)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Forma parte de la estrategia de reutilización de resultados para evitar repetir trabajo costoso. |
| 219 | `val cachedBitmap` | `inferido` | `if (loadAlbumArt) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Forma parte de la estrategia de reutilización de resultados para evitar repetir trabajo costoso. |
| 230 | `val retriever` | `inferido` | `MediaMetadataRetriever()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 233 | `val duration` | `inferido` | `retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull()?: 0L` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 237 | `val embeddedPicture` | `inferido` | `retriever.embeddedPicture` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 257 | `val result` | `inferido` | `MediaPreview(bitmap = bitmap,` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 277 | `val directory` | `inferido` | `cacheDirectory(context, "docx_previews")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 278 | `val cacheFile` | `inferido` | `File(directory, "${cacheKey(uri)}.txt")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Forma parte de la estrategia de reutilización de resultados para evitar repetir trabajo costoso. |
| 280 | `val cached` | `inferido` | `try {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Forma parte de la estrategia de reutilización de resultados para evitar repetir trabajo costoso. |
| 289 | `val preview` | `inferido` | `readDocxPreviewStreaming(context = context,` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 306 | `val directory` | `inferido` | `cacheDirectory(context, "pdf_previews")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 307 | `val key` | `inferido` | `cacheKey(uri)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 308 | `val profile` | `inferido` | `previewProfile(performanceMode)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 309 | `val profileKey` | `inferido` | `"$key-${profile.cacheTag}"` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 310 | `val memoryKey` | `inferido` | `"pdf:$profileKey"` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 311 | `val cacheFile` | `inferido` | `File(directory, "$profileKey.jpg")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Forma parte de la estrategia de reutilización de resultados para evitar repetir trabajo costoso. |
| 312 | `val cached` | `inferido` | `bitmapMemoryCache.get(memoryKey)?: decodeCachedBitmap(cacheFile)?.also {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Forma parte de la estrategia de reutilización de resultados para evitar repetir trabajo costoso. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 327 | `val path` | `inferido` | `uri.path?: return@withContext null` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 332 | `val safeDescriptor` | `inferido` | `descriptor?: return@withContext null` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 338 | `val sourceWidth` | `inferido` | `max(page.width, 1)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. |
| 339 | `val sourceHeight` | `inferido` | `max(page.height, 1)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. |
| 340 | `val scale` | `inferido` | `profile.pdfWidth.toFloat() / sourceWidth.toFloat()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 341 | `val outputHeight` | `inferido` | `max(1, (sourceHeight * scale).toInt())` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. |
| 342 | `val bitmap` | `inferido` | `Bitmap.createBitmap(profile.pdfWidth, outputHeight, Bitmap.Config.ARGB_8888)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 384 | `val extension` | `inferido` | `name?.substringAfterLast(".", "")?.lowercase().orEmpty()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. Normaliza una ausencia hacia un valor vacío/alternativo para simplificar el código consumidor. |
| 403 | `val key` | `inferido` | `cacheKey(uri)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 425 | `val directory` | `inferido` | `File(context.cacheDir, "$rootFolder/$folder")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 445 | `val directory` | `inferido` | `File(context.cacheDir, "mynotes_perf_v2/$name")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 452 | `val digest` | `inferido` | `MessageDigest.getInstance("SHA-256").digest(uri.toString().toByteArray(Charsets.UTF_8))` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 459 | `val path` | `inferido` | `uri.path?: throw IllegalArgumentException("URI de archivo inválida")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 467 | `val safeWidth` | `inferido` | `max(sourceWidth, 1)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. |
| 468 | `val safeHeight` | `inferido` | `max(sourceHeight, 1)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. |
| 469 | `val scale` | `inferido` | `minOf(maxWidth.toFloat() / safeWidth.toFloat(),` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 472 | `val targetWidth` | `inferido` | `max(1, (safeWidth * scale).toInt())` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. |
| 473 | `val targetHeight` | `inferido` | `max(1, (safeHeight * scale).toInt())` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. |
| 483 | `val path` | `inferido` | `uri.path` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 487 | `val legacyThumbnail` | `inferido` | `ThumbnailUtils.createVideoThumbnail(path, MediaStore.Video.Thumbnails.MINI_KIND)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 505 | `val original` | `inferido` | `retriever.getFrameAtTime(1_000_000L, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)?: return null` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 516 | `val bounds` | `inferido` | `BitmapFactory.Options().apply {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 526 | `var sample` | `inferido` | `1` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 530 | `val options` | `inferido` | `BitmapFactory.Options().apply {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 534 | `val decoded` | `inferido` | `openUriInputStream(context = context, uri = uri)?.use {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 538 | `val oriented` | `inferido` | `applyExifOrientation(context = context, uri = uri, bitmap = decoded)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 548 | `val path` | `inferido` | `uri.path?: return null` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 555 | `val orientation` | `inferido` | `try {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 557 | `val path` | `inferido` | `uri.path?: return bitmap` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 571 | `val matrix` | `inferido` | `Matrix()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 589 | `val transformed` | `inferido` | `Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 604 | `val scale` | `inferido` | `minOf(maxWidth.toFloat() / bitmap.width.coerceAtLeast(1),` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El inicializador limita explícitamente el valor a un rango, evitando que dimensiones/intensidades/sizes inválidos se propaguen. |
| 606 | `val width` | `inferido` | `max(1, (bitmap.width * scale).toInt())` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. |
| 607 | `val height` | `inferido` | `max(1, (bitmap.height * scale).toInt())` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. |
| 608 | `val scaled` | `inferido` | `Bitmap.createScaledBitmap(bitmap, width, height, true)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 632 | `val options` | `inferido` | `BitmapFactory.Options().apply {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 641 | `val bounds` | `inferido` | `BitmapFactory.Options().apply {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 645 | `var sample` | `inferido` | `1` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 651 | `val options` | `inferido` | `BitmapFactory.Options().apply {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 662 | `val values` | `inferido` | `file.readText().split(",")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 678 | `val source` | `inferido` | `if (uri.scheme == "file") {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 679 | `val path` | `inferido` | `uri.path?: return null` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 688 | `var entry` | `inferido` | `zip.nextEntry` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 691 | `val parser` | `inferido` | `Xml.newPullParser()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 693 | `val result` | `inferido` | `StringBuilder()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 694 | `var event` | `inferido` | `parser.eventType` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 700 | `val text` | `inferido` | `parser.nextText()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 717 | `val clean` | `inferido` | `result.toString().replace(Regex("[\\t ]+"), " ").replace(Regex("\\n{3,}"), "\n\n").trim()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 53 | `suspend fun <T> withPreviewPermit(block: suspend () -> T): T = previewLoadGate.withPermit {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 79 | `"performance" -> PreviewProfile(cacheTag = "performance", imageWidth = 360, imageHeight = 360, videoWidth = 360,` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 82 | `"quality" -> PreviewProfile(cacheTag = "quality", imageWidth = 1280, imageHeight = 1280, videoWidth = 1080, videoHeight = 1080,` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 85 | `else -> PreviewProfile(cacheTag = "balanced", imageWidth = 720, imageHeight = 720, videoWidth = 720, videoHeight = 720,` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |
| 102 | `return (value.byteCount / 1024).coerceAtLeast(1)` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 125 | `return@withContext it` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 128 | `bitmap ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 131 | `return@withContext it` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 155 | `if (memoryBitmap != null && cachedMetadata != null) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 156 | `return@withContext cachedMetadata.copy(bitmap = memoryBitmap)` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 159 | `if (cachedBitmap != null && cachedMetadata != null) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 161 | `return@withContext cachedMetadata.copy(bitmap = cachedBitmap)` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 164 | `try {` | Frontera de manejo de errores: agrupa operaciones que pueden lanzar excepciones para permitir recuperación o limpieza controlada. |
| 182 | `if (bitmap != null) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 198 | `try {` | Frontera de manejo de errores: agrupa operaciones que pueden lanzar excepciones para permitir recuperación o limpieza controlada. |
| 221 | `bitmap ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 227 | `if (cachedMetadata != null && (!loadAlbumArt \|\| cachedBitmap != null \|\| imageFile.exists())) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 228 | `return@withContext cachedMetadata.copy(bitmap = cachedBitmap)` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 231 | `try {` | Frontera de manejo de errores: agrupa operaciones que pueden lanzar excepciones para permitir recuperación o limpieza controlada. |
| 236 | `if (loadAlbumArt) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 238 | `if (embeddedPicture != null) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 241 | `if (bitmap != null) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 265 | `try {` | Frontera de manejo de errores: agrupa operaciones que pueden lanzar excepciones para permitir recuperación o limpieza controlada. |
| 279 | `if (cacheFile.exists() && cacheFile.length() > 0L) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 285 | `if (!cached.isNullOrBlank()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 286 | `return@withContext trimPreview(cached, maxCharacters)` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 292 | `if (!preview.isNullOrBlank()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 293 | `try {` | Frontera de manejo de errores: agrupa operaciones que pueden lanzar excepciones para permitir recuperación o limpieza controlada. |
| 313 | `bitmap ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 316 | `if (cached != null) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 317 | `return@withContext cached` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 325 | `try {` | Frontera de manejo de errores: agrupa operaciones que pueden lanzar excepciones para permitir recuperación o limpieza controlada. |
| 334 | `if (renderer.pageCount <= 0) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 335 | `return@withContext null` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 353 | `try {` | Frontera de manejo de errores: agrupa operaciones que pueden lanzar excepciones para permitir recuperación o limpieza controlada. |
| 357 | `try {` | Frontera de manejo de errores: agrupa operaciones que pueden lanzar excepciones para permitir recuperación o limpieza controlada. |
| 361 | `try {` | Frontera de manejo de errores: agrupa operaciones que pueden lanzar excepciones para permitir recuperación o limpieza controlada. |
| 374 | `when (type) {` | Selección múltiple de Kotlin: concentra casos mutuamente evaluados y normalmente define una normalización o estrategia según el valor de entrada. |
| 375 | `"image" -> loadImagePreview(context = context, uri = uri, performanceMode = performanceMode)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 376 | `"video" -> loadVideoPreview(context = context, uri = uri, performanceMode = performanceMode)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 377 | `"audio" -> loadAudioPreview(context = context,` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 380 | `"voice" -> loadAudioPreview(context = context,` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 383 | `"file" -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 385 | `when (extension) {` | Selección múltiple de Kotlin: concentra casos mutuamente evaluados y normalmente define una normalización o estrategia según el valor de entrada. |
| 386 | `"docx" -> loadDocxPreview(context = context,` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 389 | `"pdf" -> loadPdfFirstPage(context = context,` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 422 | `rootFolder ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 424 | `folder ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 427 | `file ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 430 | `file ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 431 | `try {` | Frontera de manejo de errores: agrupa operaciones que pueden lanzar excepciones para permitir recuperación o limpieza controlada. |
| 446 | `if (!directory.exists()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 449 | `return directory` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 453 | `return digest.joinToString(separator = "") {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 454 | `byte -> "%02x".format(byte)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 458 | `if (uri.scheme == "file") {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 481 | `if (Build.VERSION.SDK_INT <` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 484 | `if (!path.isNullOrBlank()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 485 | `try {` | Frontera de manejo de errores: agrupa operaciones que pueden lanzar excepciones para permitir recuperación o limpieza controlada. |
| 488 | `if (legacyThumbnail != null) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 489 | `return scalePreviewBitmap(bitmap = legacyThumbnail, maxWidth = maxWidth, maxHeight = maxHeight)` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 501 | `return try {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 502 | `if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 515 | `return try {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 520 | `input ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 523 | `if (bounds.outWidth <= 0 \|\| bounds.outHeight <= 0) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 524 | `return null` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 527 | `while (bounds.outWidth / sample > maxWidth * 2 \|\| bounds.outHeight / sample > maxHeight * 2) {` | Bucle condicionado: repite mientras la condición se mantenga verdadera; la lógica interna debe permitir progreso hacia la salida. |
| 535 | `input ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 547 | `return if (uri.scheme == "file") {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 556 | `if (uri.scheme == "file") {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 561 | `input ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 568 | `if (orientation == ExifInterface.ORIENTATION_NORMAL \|\| orientation == ExifInterface.ORIENTATION_UNDEFINED) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 569 | `return bitmap` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 572 | `when (orientation) {` | Selección múltiple de Kotlin: concentra casos mutuamente evaluados y normalmente define una normalización o estrategia según el valor de entrada. |
| 573 | `ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.setScale(-1f, 1f)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 574 | `ExifInterface.ORIENTATION_ROTATE_180 -> matrix.setRotate(180f)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 575 | `ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.setScale(1f, -1f)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 576 | `ExifInterface.ORIENTATION_TRANSPOSE -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 580 | `ExifInterface.ORIENTATION_ROTATE_90 -> matrix.setRotate(90f)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |

> Se detectaron 120 estructuras; la tabla limita la vista a las primeras 80 para no duplicar de forma inútil el código completo. El fragmento de código anterior conserva todas.

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 23.
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 21.
- **Límite numérico:** Se fuerza el valor a un intervalo o umbral para impedir que salgan valores fuera de los límites esperados. Apariciones en este bloque: 4.
- **Normalización vacía:** `orEmpty` transforma una referencia nula en una colección/cadena vacía, simplificando el consumo posterior. Apariciones en este bloque: 1.
- **Guardia de API:** Se condiciona una API según la versión de Android para no ejecutar llamadas incompatibles en dispositivos antiguos. Apariciones en este bloque: 2.
- **Trabajo IO:** El trabajo potencialmente bloqueante se desplaza al dispatcher de I/O en vez de ocupar el hilo principal. Apariciones en este bloque: 6.

#### Efectos secundarios y recursos

- **Persistencia Room/DAO:** Realiza una operación de persistencia o modificación a través de una capa de datos/DAO.
- **Sistema de archivos/caché:** Accede o modifica almacenamiento/caché; son efectos externos al estado puramente en memoria.
- **Caché:** Lee, llena, invalida o administra caché para evitar recomputación o decodificación repetida.
- **Mutación de colección/estado compartido:** Modifica una colección/mapa/estructura existente; el cambio permanece visible para quienes compartan esa misma instancia.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `Semaphore`, `withPreviewPermit`, `suspend`, `block`, `get`, `width.toFloat`, `height.toFloat`, `PreviewProfile`, `Runtime.getRuntime`, `maxMemory`, `coerceIn`, `toInt`, `coerceAtLeast`, `withContext`, `cacheDirectory`, `cacheKey`, `previewProfile`, `File`, `bitmapMemoryCache.get`, `decodeCachedBitmap`, `bitmapMemoryCache.put`, `decodeImageThumbnail`, `saveJpeg`, `readMediaMetadata`, `cachedMetadata.copy`, `MediaMetadataRetriever`, `setRetrieverDataSource`, `retriever.extractMetadata`, `toLongOrNull`, `toIntOrNull`, `createVideoFrame`, `MediaPreview`, `writeMediaMetadata`, `e.printStackTrace`, `retriever.release`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.
- Conservar las guardas de versión antes de APIs que no existen en Android antiguos.
- No mover trabajo bloqueante al hilo principal; el uso de `Dispatchers.IO` forma parte de la protección de rendimiento.
- Mantener coherencia entre creación, clave, lectura e invalidación de caché; cambiar solo una de esas etapas puede dejar datos obsoletos.
- Preservar la normalización y correspondencia de `performanceMode`, porque otras capas usan las mismas claves para rendimiento/calidad.

### 4.2 `withPreviewPermit` — fun, líneas 53–55

```kotlin
    suspend fun <T> withPreviewPermit(block: suspend () -> T): T = previewLoadGate.withPermit {
                block()
            }
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `block: suspend () -> T` — `block` recibe un valor de tipo `suspend () -> T`. El contrato no marca este parámetro como anulable.

**Retorno:** `T`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `suspend` permite suspender sin bloquear el hilo; el llamador debe estar dentro de una corrutina u otra función suspendible.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 53 | `suspend fun <T> withPreviewPermit(block: suspend () -> T): T = previewLoadGate.withPermit {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `withPreviewPermit`, `suspend`, `block`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.3 `MediaPreview` — class, líneas 56–63

```kotlin
    data class MediaPreview(val bitmap: Bitmap? = null, val durationMillis: Long = 0L, val width: Int = 0, val height: Int = 0) {
        val aspectRatio: Float
            get() = if (width > 0 && height > 0) {
                    width.toFloat() / height.toFloat()
                } else {
                    16f / 9f
                }
    }
```

#### Qué hace y por qué existe

Sistema central de miniaturas y caché de adjuntos. Genera representaciones ligeras de imágenes, video, audio, PDF y documentos, conserva variantes en memoria/disco y adapta la resolución al perfil de rendimiento seleccionado.

#### Contrato de la declaración

**Parámetros del constructor/encabezado:**
- `val bitmap: Bitmap? = null` — `bitmap` recibe un valor de tipo `Bitmap?`. El contrato permite `null`; el bloque debe decidir explícita o implícitamente qué significa la ausencia. Tiene valor por defecto `null`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada. Además, al declararse con `val/var` en el constructor se convierte en propiedad de la instancia.
- `val durationMillis: Long = 0L` — `durationMillis` recibe un valor de tipo `Long`. El contrato no marca este parámetro como anulable. Tiene valor por defecto `0L`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora. Además, al declararse con `val/var` en el constructor se convierte en propiedad de la instancia.
- `val width: Int = 0` — `width` recibe un valor de tipo `Int`. El contrato no marca este parámetro como anulable. Tiene valor por defecto `0`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora. Además, al declararse con `val/var` en el constructor se convierte en propiedad de la instancia.
- `val height: Int = 0` — `height` recibe un valor de tipo `Int`. El contrato no marca este parámetro como anulable. Tiene valor por defecto `0`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora. Además, al declararse con `val/var` en el constructor se convierte en propiedad de la instancia.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 57 | `val aspectRatio` | `Float` | `(sin inicializador en la declaración)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo declarado es `Float`. No declara nulabilidad explícita. |

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `get`, `width.toFloat`, `height.toFloat`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.4 `previewProfile` — fun, líneas 75–77

```kotlin
    private data class PreviewProfile(val cacheTag: String, val imageWidth: Int, val imageHeight: Int, val videoWidth: Int,
        val videoHeight: Int, val audioArtSize: Int, val pdfWidth: Int, val imageJpegQuality: Int, val videoJpegQuality: Int,
        val audioJpegQuality: Int, val pdfJpegQuality: Int)
```

#### Qué hace y por qué existe

Sistema central de miniaturas y caché de adjuntos. Genera representaciones ligeras de imágenes, video, audio, PDF y documentos, conserva variantes en memoria/disco y adapta la resolución al perfil de rendimiento seleccionado.

#### Contrato de la declaración

**Parámetros:**

- `val cacheTag: String` — No se pudo separar automáticamente nombre/tipo; se conserva la firma exacta como contrato.
- `val imageWidth: Int` — No se pudo separar automáticamente nombre/tipo; se conserva la firma exacta como contrato.
- `val imageHeight: Int` — No se pudo separar automáticamente nombre/tipo; se conserva la firma exacta como contrato.
- `val videoWidth: Int` — No se pudo separar automáticamente nombre/tipo; se conserva la firma exacta como contrato.
- `val videoHeight: Int` — No se pudo separar automáticamente nombre/tipo; se conserva la firma exacta como contrato.
- `val audioArtSize: Int` — No se pudo separar automáticamente nombre/tipo; se conserva la firma exacta como contrato.
- `val pdfWidth: Int` — No se pudo separar automáticamente nombre/tipo; se conserva la firma exacta como contrato.
- `val imageJpegQuality: Int` — No se pudo separar automáticamente nombre/tipo; se conserva la firma exacta como contrato.
- `val videoJpegQuality: Int` — No se pudo separar automáticamente nombre/tipo; se conserva la firma exacta como contrato.
- `val audioJpegQuality: Int` — No se pudo separar automáticamente nombre/tipo; se conserva la firma exacta como contrato.
- `val pdfJpegQuality: Int` — No se pudo separar automáticamente nombre/tipo; se conserva la firma exacta como contrato.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 76 | `val videoHeight` | `Int, val audioArtSize: Int, val pdfWidth: Int, val imageJpegQuality: Int, val videoJpegQuality: Int,` | `(sin inicializador en la declaración)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo declarado es `Int, val audioArtSize: Int, val pdfWidth: Int, val imageJpegQuality: Int, val videoJpegQuality: Int,`. No declara nulabilidad explícita. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. |
| 77 | `val audioJpegQuality` | `Int, val pdfJpegQuality: Int)` | `(sin inicializador en la declaración)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo declarado es `Int, val pdfJpegQuality: Int)`. No declara nulabilidad explícita. |

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

- **Caché:** Lee, llena, invalida o administra caché para evitar recomputación o decodificación repetida.

#### Dependencias de ejecución / llamadas relevantes

No se detectaron llamadas de función relevantes fuera de la propia estructura de la declaración.

#### Qué no debe romperse al modificar este bloque

- Mantener coherencia entre creación, clave, lectura e invalidación de caché; cambiar solo una de esas etapas puede dejar datos obsoletos.

### 4.5 `previewProfile` — fun, líneas 78–116

```kotlin
    private fun previewProfile(performanceMode: String): PreviewProfile = when (performanceMode) {
            "performance" -> PreviewProfile(cacheTag = "performance", imageWidth = 360, imageHeight = 360, videoWidth = 360,
                    videoHeight = 360, audioArtSize = 256, pdfWidth = 480, imageJpegQuality = 78, videoJpegQuality = 80,
                    audioJpegQuality = 74, pdfJpegQuality = 84)
            "quality" -> PreviewProfile(cacheTag = "quality", imageWidth = 1280, imageHeight = 1280, videoWidth = 1080, videoHeight = 1080,
                    audioArtSize = 768, pdfWidth = 1440, imageJpegQuality = 94, videoJpegQuality = 94, audioJpegQuality = 90,
                    pdfJpegQuality = 95)
            else -> PreviewProfile(cacheTag = "balanced", imageWidth = 720, imageHeight = 720, videoWidth = 720, videoHeight = 720,
                    audioArtSize = 512, pdfWidth = 960, imageJpegQuality = 88, videoJpegQuality = 90, audioJpegQuality = 82,
                    pdfJpegQuality = 92)
        }
    /*
     * ========================================================
     * CACHÉ DE BITMAPS EN RAM
     * ========================================================
     */
    /*
     * Caché adaptativa: reserva aproximadamente 1/16 del heap para previews,
     * con límites seguros para equipos antiguos y actuales. Evita fijar 20 MB
     * incluso en dispositivos con poca RAM.
     */
    private val memoryCacheKb = (Runtime.getRuntime().maxMemory() / 1024L / 16L).coerceIn(8L * 1024L, 24L * 1024L).toInt()
    private val bitmapMemoryCache = object : LruCache<String, Bitmap>(memoryCacheKb) {
            override fun sizeOf(key: String, value: Bitmap): Int {
                return (value.byteCount / 1024).coerceAtLeast(1)
            }
        }
    /*
     * ========================================================
     * IMAGEN
     * ========================================================
     *
     * Las miniaturas de imagen se guardan explícitamente en disco dentro
     * de cacheDir, en lugar de depender únicamente de la caché implícita
     * del cargador de imágenes. Así sobreviven a recomposiciones, cambios
     * de pantalla y reinicios normales de la aplicación. Android todavía
     * puede borrar cacheDir cuando necesita espacio; en ese caso la
     * miniatura se vuelve a crear automáticamente desde el adjunto privado.
     */
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `performanceMode: String` — `performanceMode` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable. Aunque el tipo sea `String`, semánticamente suele funcionar como clave/enumeración textual; las ramas del código delimitan los valores reconocidos.

**Retorno:** `PreviewProfile`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 99 | `val memoryCacheKb` | `inferido` | `(Runtime.getRuntime().maxMemory() / 1024L / 16L).coerceIn(8L * 1024L, 24L * 1024L).toInt()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Forma parte de la estrategia de reutilización de resultados para evitar repetir trabajo costoso. El inicializador limita explícitamente el valor a un rango, evitando que dimensiones/intensidades/sizes inválidos se propaguen. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 100 | `val bitmapMemoryCache` | `inferido` | `object : LruCache<String, Bitmap>(memoryCacheKb) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Es una caché acotada por política LRU: privilegia entradas recientes y permite expulsión automática cuando se supera el límite. Forma parte de la estrategia de reutilización de resultados para evitar repetir trabajo costoso. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 79 | `"performance" -> PreviewProfile(cacheTag = "performance", imageWidth = 360, imageHeight = 360, videoWidth = 360,` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 82 | `"quality" -> PreviewProfile(cacheTag = "quality", imageWidth = 1280, imageHeight = 1280, videoWidth = 1080, videoHeight = 1080,` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 85 | `else -> PreviewProfile(cacheTag = "balanced", imageWidth = 720, imageHeight = 720, videoWidth = 720, videoHeight = 720,` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |
| 102 | `return (value.byteCount / 1024).coerceAtLeast(1)` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

**Restricciones concretas que aparecen en este bloque:**
- **Límite numérico:** Se fuerza el valor a un intervalo o umbral para impedir que salgan valores fuera de los límites esperados. Apariciones en este bloque: 2.

#### Efectos secundarios y recursos

- **Caché:** Lee, llena, invalida o administra caché para evitar recomputación o decodificación repetida.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `PreviewProfile`, `Runtime.getRuntime`, `maxMemory`, `coerceIn`, `toInt`, `coerceAtLeast`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.
- Mantener coherencia entre creación, clave, lectura e invalidación de caché; cambiar solo una de esas etapas puede dejar datos obsoletos.
- Preservar la normalización y correspondencia de `performanceMode`, porque otras capas usan las mismas claves para rendimiento/calidad.

### 4.6 `sizeOf` — fun, líneas 101–103

```kotlin
            override fun sizeOf(key: String, value: Bitmap): Int {
                return (value.byteCount / 1024).coerceAtLeast(1)
            }
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `key: String` — `key` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable. Aunque el tipo sea `String`, semánticamente suele funcionar como clave/enumeración textual; las ramas del código delimitan los valores reconocidos.
- `value: Bitmap` — `value` recibe un valor de tipo `Bitmap`. El contrato no marca este parámetro como anulable.

**Retorno:** `Int`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `override` indica que el contrato viene de una superclase/interfaz; la firma debe respetar el método heredado.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 102 | `return (value.byteCount / 1024).coerceAtLeast(1)` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

**Restricciones concretas que aparecen en este bloque:**
- **Límite numérico:** Se fuerza el valor a un intervalo o umbral para impedir que salgan valores fuera de los límites esperados. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `coerceAtLeast`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.7 `loadImagePreview` — fun, líneas 117–143

```kotlin
    suspend fun loadImagePreview(context: Context, uri: Uri, performanceMode: String = "balanced"): Bitmap? = withContext(Dispatchers.IO) {
            val directory = cacheDirectory(context, "image_previews")
            val key = cacheKey(uri)
            val profile = previewProfile(performanceMode)
            val profileKey = "$key-${profile.cacheTag}"
            val memoryKey = "image:$profileKey"
            val cacheFile = File(directory, "$profileKey.jpg")
            bitmapMemoryCache.get(memoryKey)?.let {
                    return@withContext it
                }
            decodeCachedBitmap(cacheFile)?.also {
                        bitmap ->
                    bitmapMemoryCache.put(memoryKey, bitmap)
                }?.let {
                    return@withContext it
                }
            val bitmap = decodeImageThumbnail(context = context, uri = uri, maxWidth = profile.imageWidth, maxHeight = profile.imageHeight)
                    ?: return@withContext null
            saveJpeg(bitmap = bitmap, destination = cacheFile, quality = profile.imageJpegQuality)
            bitmapMemoryCache.put(memoryKey, bitmap)
            bitmap
        }
    /*
     * ========================================================
     * VIDEO
     * ========================================================
     */
```

#### Qué hace y por qué existe

Carga o reconstruye un recurso/dato a partir de sus entradas, aplicando las capas de caché/normalización definidas en el cuerpo.

#### Contrato de la declaración

**Parámetros:**

- `context: Context` — `context` recibe un valor de tipo `Context`. El contrato no marca este parámetro como anulable. Da acceso a recursos y APIs Android; su vida útil debe corresponder con el uso realizado.
- `uri: Uri` — `uri` recibe un valor de tipo `Uri`. El contrato no marca este parámetro como anulable. Es una referencia de contenido Android; puede apuntar a content providers y no solo a archivos locales.
- `performanceMode: String = "balanced"` — `performanceMode` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable. Tiene valor por defecto `"balanced"`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada. Aunque el tipo sea `String`, semánticamente suele funcionar como clave/enumeración textual; las ramas del código delimitan los valores reconocidos.

**Retorno:** `Bitmap?`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `suspend` permite suspender sin bloquear el hilo; el llamador debe estar dentro de una corrutina u otra función suspendible.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 118 | `val directory` | `inferido` | `cacheDirectory(context, "image_previews")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 119 | `val key` | `inferido` | `cacheKey(uri)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 120 | `val profile` | `inferido` | `previewProfile(performanceMode)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 121 | `val profileKey` | `inferido` | `"$key-${profile.cacheTag}"` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 122 | `val memoryKey` | `inferido` | `"image:$profileKey"` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 123 | `val cacheFile` | `inferido` | `File(directory, "$profileKey.jpg")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Forma parte de la estrategia de reutilización de resultados para evitar repetir trabajo costoso. |
| 133 | `val bitmap` | `inferido` | `decodeImageThumbnail(context = context, uri = uri, maxWidth = profile.imageWidth, maxHeight = profil…` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 125 | `return@withContext it` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 128 | `bitmap ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 131 | `return@withContext it` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 3.
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 1.
- **Trabajo IO:** El trabajo potencialmente bloqueante se desplaza al dispatcher de I/O en vez de ocupar el hilo principal. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

- **Sistema de archivos/caché:** Accede o modifica almacenamiento/caché; son efectos externos al estado puramente en memoria.
- **Caché:** Lee, llena, invalida o administra caché para evitar recomputación o decodificación repetida.
- **Mutación de colección/estado compartido:** Modifica una colección/mapa/estructura existente; el cambio permanece visible para quienes compartan esa misma instancia.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `withContext`, `cacheDirectory`, `cacheKey`, `previewProfile`, `File`, `bitmapMemoryCache.get`, `decodeCachedBitmap`, `bitmapMemoryCache.put`, `decodeImageThumbnail`, `saveJpeg`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- No mover trabajo bloqueante al hilo principal; el uso de `Dispatchers.IO` forma parte de la protección de rendimiento.
- Mantener coherencia entre creación, clave, lectura e invalidación de caché; cambiar solo una de esas etapas puede dejar datos obsoletos.
- Preservar la normalización y correspondencia de `performanceMode`, porque otras capas usan las mismas claves para rendimiento/calidad.

### 4.8 `loadVideoPreview` — fun, líneas 144–208

```kotlin
    suspend fun loadVideoPreview(context: Context, uri: Uri, performanceMode: String = "balanced"): MediaPreview = withContext(
            Dispatchers.IO) {
            val directory = cacheDirectory(context, "video_previews")
            val key = cacheKey(uri)
            val profile = previewProfile(performanceMode)
            val profileKey = "$key-${profile.cacheTag}"
            val imageFile = File(directory, "$profileKey.jpg")
            val metadataFile = File(directory, "$profileKey.meta")
            val memoryKey = "video:$profileKey"
            val cachedMetadata = readMediaMetadata(metadataFile)
            val memoryBitmap = bitmapMemoryCache.get(memoryKey)
            if (memoryBitmap != null && cachedMetadata != null) {
                return@withContext cachedMetadata.copy(bitmap = memoryBitmap)
            }
            val cachedBitmap = decodeCachedBitmap(imageFile)
            if (cachedBitmap != null && cachedMetadata != null) {
                bitmapMemoryCache.put(memoryKey, cachedBitmap)
                return@withContext cachedMetadata.copy(bitmap = cachedBitmap)
            }
            val retriever = MediaMetadataRetriever()
            try {
                setRetrieverDataSource(context = context, retriever = retriever, uri = uri)
                val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull()?: 0L
                val rawWidth = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)?.toIntOrNull()?: 0
                val rawHeight = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)?.toIntOrNull()?: 0
                val rotation = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)?.toIntOrNull()?: 0
                val width = if (rotation == 90 || rotation == 270) {
                        rawHeight
                    } else {
                        rawWidth
                    }
                val height = if (rotation == 90 || rotation == 270) {
                        rawWidth
                    } else {
                        rawHeight
                    }
                val bitmap = createVideoFrame(context = context, uri = uri, retriever = retriever, sourceWidth = width, sourceHeight =
                            height, maxWidth = profile.videoWidth, maxHeight = profile.videoHeight)
                if (bitmap != null) {
                    saveJpeg(bitmap = bitmap,
                        destination = imageFile,
                        quality = profile.videoJpegQuality)
                    bitmapMemoryCache.put(memoryKey, bitmap)
                }
                val result = MediaPreview(bitmap = bitmap,
                        durationMillis = duration,
                        width = width,
                        height = height)
                writeMediaMetadata(metadataFile, result)
                result
            } catch (e: Exception) {
                e.printStackTrace()
                MediaPreview()
            } finally {
                try {
                    retriever.release()
                } catch (ignored: Exception) {
                }
            }
        }
    /*
     * ========================================================
     * AUDIO
     * ========================================================
     */
```

#### Qué hace y por qué existe

Carga o reconstruye un recurso/dato a partir de sus entradas, aplicando las capas de caché/normalización definidas en el cuerpo.

#### Contrato de la declaración

**Parámetros:**

- `context: Context` — `context` recibe un valor de tipo `Context`. El contrato no marca este parámetro como anulable. Da acceso a recursos y APIs Android; su vida útil debe corresponder con el uso realizado.
- `uri: Uri` — `uri` recibe un valor de tipo `Uri`. El contrato no marca este parámetro como anulable. Es una referencia de contenido Android; puede apuntar a content providers y no solo a archivos locales.
- `performanceMode: String = "balanced"` — `performanceMode` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable. Tiene valor por defecto `"balanced"`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada. Aunque el tipo sea `String`, semánticamente suele funcionar como clave/enumeración textual; las ramas del código delimitan los valores reconocidos.

**Retorno:** `MediaPreview`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `suspend` permite suspender sin bloquear el hilo; el llamador debe estar dentro de una corrutina u otra función suspendible.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 146 | `val directory` | `inferido` | `cacheDirectory(context, "video_previews")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 147 | `val key` | `inferido` | `cacheKey(uri)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 148 | `val profile` | `inferido` | `previewProfile(performanceMode)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 149 | `val profileKey` | `inferido` | `"$key-${profile.cacheTag}"` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 150 | `val imageFile` | `inferido` | `File(directory, "$profileKey.jpg")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 151 | `val metadataFile` | `inferido` | `File(directory, "$profileKey.meta")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 152 | `val memoryKey` | `inferido` | `"video:$profileKey"` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 153 | `val cachedMetadata` | `inferido` | `readMediaMetadata(metadataFile)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Forma parte de la estrategia de reutilización de resultados para evitar repetir trabajo costoso. |
| 154 | `val memoryBitmap` | `inferido` | `bitmapMemoryCache.get(memoryKey)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 158 | `val cachedBitmap` | `inferido` | `decodeCachedBitmap(imageFile)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Forma parte de la estrategia de reutilización de resultados para evitar repetir trabajo costoso. |
| 163 | `val retriever` | `inferido` | `MediaMetadataRetriever()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 166 | `val duration` | `inferido` | `retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull()?: 0L` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 167 | `val rawWidth` | `inferido` | `retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)?.toIntOrNull()?: 0` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 168 | `val rawHeight` | `inferido` | `retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)?.toIntOrNull()?: 0` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 169 | `val rotation` | `inferido` | `retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)?.toIntOrNull()?: 0` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 170 | `val width` | `inferido` | `if (rotation == 90 \|\| rotation == 270) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. |
| 175 | `val height` | `inferido` | `if (rotation == 90 \|\| rotation == 270) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. |
| 180 | `val bitmap` | `inferido` | `createVideoFrame(context = context, uri = uri, retriever = retriever, sourceWidth = width, sourceHei…` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 188 | `val result` | `inferido` | `MediaPreview(bitmap = bitmap,` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 155 | `if (memoryBitmap != null && cachedMetadata != null) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 156 | `return@withContext cachedMetadata.copy(bitmap = memoryBitmap)` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 159 | `if (cachedBitmap != null && cachedMetadata != null) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 161 | `return@withContext cachedMetadata.copy(bitmap = cachedBitmap)` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 164 | `try {` | Frontera de manejo de errores: agrupa operaciones que pueden lanzar excepciones para permitir recuperación o limpieza controlada. |
| 182 | `if (bitmap != null) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 198 | `try {` | Frontera de manejo de errores: agrupa operaciones que pueden lanzar excepciones para permitir recuperación o limpieza controlada. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 4.
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 4.
- **Trabajo IO:** El trabajo potencialmente bloqueante se desplaza al dispatcher de I/O en vez de ocupar el hilo principal. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

- **Sistema de archivos/caché:** Accede o modifica almacenamiento/caché; son efectos externos al estado puramente en memoria.
- **Caché:** Lee, llena, invalida o administra caché para evitar recomputación o decodificación repetida.
- **Mutación de colección/estado compartido:** Modifica una colección/mapa/estructura existente; el cambio permanece visible para quienes compartan esa misma instancia.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `withContext`, `cacheDirectory`, `cacheKey`, `previewProfile`, `File`, `readMediaMetadata`, `bitmapMemoryCache.get`, `cachedMetadata.copy`, `decodeCachedBitmap`, `bitmapMemoryCache.put`, `MediaMetadataRetriever`, `setRetrieverDataSource`, `retriever.extractMetadata`, `toLongOrNull`, `toIntOrNull`, `createVideoFrame`, `saveJpeg`, `MediaPreview`, `writeMediaMetadata`, `e.printStackTrace`, `retriever.release`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- No mover trabajo bloqueante al hilo principal; el uso de `Dispatchers.IO` forma parte de la protección de rendimiento.
- Mantener coherencia entre creación, clave, lectura e invalidación de caché; cambiar solo una de esas etapas puede dejar datos obsoletos.
- Preservar la normalización y correspondencia de `performanceMode`, porque otras capas usan las mismas claves para rendimiento/calidad.

### 4.9 `loadAudioPreview` — fun, líneas 209–275

```kotlin
    suspend fun loadAudioPreview(context: Context, uri: Uri, loadAlbumArt: Boolean, performanceMode: String = "balanced"): MediaPreview =
        withContext(Dispatchers.IO) {
            val directory = cacheDirectory(context, "audio_previews")
            val key = cacheKey(uri)
            val profile = previewProfile(performanceMode)
            val profileKey = "$key-${profile.cacheTag}"
            val imageFile = File(directory, "$profileKey.jpg")
            val metadataFile = File(directory, "$profileKey.meta")
            val memoryKey = "audio:$profileKey"
            val cachedMetadata = readMediaMetadata(metadataFile)
            val cachedBitmap = if (loadAlbumArt) {
                    bitmapMemoryCache.get(memoryKey)?: decodeCachedBitmap(imageFile)?.also {
                                    bitmap ->
                                bitmapMemoryCache.put(memoryKey, bitmap)
                            }
                } else {
                    null
                }
            if (cachedMetadata != null && (!loadAlbumArt || cachedBitmap != null || imageFile.exists())) {
                return@withContext cachedMetadata.copy(bitmap = cachedBitmap)
            }
            val retriever = MediaMetadataRetriever()
            try {
                setRetrieverDataSource(context = context, retriever = retriever, uri = uri)
                val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull()?: 0L
                var bitmap:
                    Bitmap? = null
                if (loadAlbumArt) {
                    val embeddedPicture = retriever.embeddedPicture
                    if (embeddedPicture != null) {
                        bitmap = decodeSampledBitmap(bytes = embeddedPicture,
                                requestedSize = profile.audioArtSize)
                        if (bitmap != null) {
                            saveJpeg(bitmap = bitmap,
                                destination = imageFile,
                                quality = profile.audioJpegQuality)
                            bitmapMemoryCache.put(memoryKey, bitmap)
                        } else {
                            /*
                             * Archivo vacío = ya intentamos buscar
                             * carátula y no había una utilizable.
                             */
                            imageFile.createNewFile()
                        }
                    } else {
                        imageFile.createNewFile()
                    }
                }
                val result = MediaPreview(bitmap = bitmap,
                        durationMillis = duration)
                writeMediaMetadata(metadataFile, result)
                result
            } catch (e: Exception) {
                e.printStackTrace()
                MediaPreview()
            } finally {
                try {
                    retriever.release()
                } catch (ignored: Exception) {
                }
            }
        }
    /*
     * ========================================================
     * DOCX
     * ========================================================
     */
```

#### Qué hace y por qué existe

Carga o reconstruye un recurso/dato a partir de sus entradas, aplicando las capas de caché/normalización definidas en el cuerpo.

#### Contrato de la declaración

**Parámetros:**

- `context: Context` — `context` recibe un valor de tipo `Context`. El contrato no marca este parámetro como anulable. Da acceso a recursos y APIs Android; su vida útil debe corresponder con el uso realizado.
- `uri: Uri` — `uri` recibe un valor de tipo `Uri`. El contrato no marca este parámetro como anulable. Es una referencia de contenido Android; puede apuntar a content providers y no solo a archivos locales.
- `loadAlbumArt: Boolean` — `loadAlbumArt` recibe un valor de tipo `Boolean`. El contrato no marca este parámetro como anulable.
- `performanceMode: String = "balanced"` — `performanceMode` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable. Tiene valor por defecto `"balanced"`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada. Aunque el tipo sea `String`, semánticamente suele funcionar como clave/enumeración textual; las ramas del código delimitan los valores reconocidos.

**Retorno:** `MediaPreview`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `suspend` permite suspender sin bloquear el hilo; el llamador debe estar dentro de una corrutina u otra función suspendible.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 211 | `val directory` | `inferido` | `cacheDirectory(context, "audio_previews")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 212 | `val key` | `inferido` | `cacheKey(uri)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 213 | `val profile` | `inferido` | `previewProfile(performanceMode)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 214 | `val profileKey` | `inferido` | `"$key-${profile.cacheTag}"` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 215 | `val imageFile` | `inferido` | `File(directory, "$profileKey.jpg")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 216 | `val metadataFile` | `inferido` | `File(directory, "$profileKey.meta")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 217 | `val memoryKey` | `inferido` | `"audio:$profileKey"` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 218 | `val cachedMetadata` | `inferido` | `readMediaMetadata(metadataFile)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Forma parte de la estrategia de reutilización de resultados para evitar repetir trabajo costoso. |
| 219 | `val cachedBitmap` | `inferido` | `if (loadAlbumArt) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Forma parte de la estrategia de reutilización de resultados para evitar repetir trabajo costoso. |
| 230 | `val retriever` | `inferido` | `MediaMetadataRetriever()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 233 | `val duration` | `inferido` | `retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull()?: 0L` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 237 | `val embeddedPicture` | `inferido` | `retriever.embeddedPicture` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 257 | `val result` | `inferido` | `MediaPreview(bitmap = bitmap,` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 221 | `bitmap ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 227 | `if (cachedMetadata != null && (!loadAlbumArt \|\| cachedBitmap != null \|\| imageFile.exists())) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 228 | `return@withContext cachedMetadata.copy(bitmap = cachedBitmap)` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 231 | `try {` | Frontera de manejo de errores: agrupa operaciones que pueden lanzar excepciones para permitir recuperación o limpieza controlada. |
| 236 | `if (loadAlbumArt) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 238 | `if (embeddedPicture != null) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 241 | `if (bitmap != null) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 265 | `try {` | Frontera de manejo de errores: agrupa operaciones que pueden lanzar excepciones para permitir recuperación o limpieza controlada. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 2.
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 2.
- **Trabajo IO:** El trabajo potencialmente bloqueante se desplaza al dispatcher de I/O en vez de ocupar el hilo principal. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

- **Sistema de archivos/caché:** Accede o modifica almacenamiento/caché; son efectos externos al estado puramente en memoria.
- **Caché:** Lee, llena, invalida o administra caché para evitar recomputación o decodificación repetida.
- **Mutación de colección/estado compartido:** Modifica una colección/mapa/estructura existente; el cambio permanece visible para quienes compartan esa misma instancia.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `withContext`, `cacheDirectory`, `cacheKey`, `previewProfile`, `File`, `readMediaMetadata`, `bitmapMemoryCache.get`, `decodeCachedBitmap`, `bitmapMemoryCache.put`, `imageFile.exists`, `cachedMetadata.copy`, `MediaMetadataRetriever`, `setRetrieverDataSource`, `retriever.extractMetadata`, `toLongOrNull`, `decodeSampledBitmap`, `saveJpeg`, `imageFile.createNewFile`, `MediaPreview`, `writeMediaMetadata`, `e.printStackTrace`, `retriever.release`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- No mover trabajo bloqueante al hilo principal; el uso de `Dispatchers.IO` forma parte de la protección de rendimiento.
- Mantener coherencia entre creación, clave, lectura e invalidación de caché; cambiar solo una de esas etapas puede dejar datos obsoletos.
- Preservar la normalización y correspondencia de `performanceMode`, porque otras capas usan las mismas claves para rendimiento/calidad.

### 4.10 `loadDocxPreview` — fun, líneas 276–304

```kotlin
    suspend fun loadDocxPreview(context: Context, uri: Uri, maxCharacters: Int): String? = withContext(Dispatchers.IO) {
            val directory = cacheDirectory(context, "docx_previews")
            val cacheFile = File(directory, "${cacheKey(uri)}.txt")
            if (cacheFile.exists() && cacheFile.length() > 0L) {
                val cached = try {
                        cacheFile.readText(Charsets.UTF_8)
                    } catch (e: Exception) {
                        null
                    }
                if (!cached.isNullOrBlank()) {
                    return@withContext trimPreview(cached, maxCharacters)
                }
            }
            val preview = readDocxPreviewStreaming(context = context,
                    uri = uri,
                    maxCharacters = DOCX_CACHE_CHARACTERS)
            if (!preview.isNullOrBlank()) {
                try {
                    cacheFile.writeText(preview, Charsets.UTF_8)
                } catch (ignored: Exception) {
                }
            }
            trimPreview(preview, maxCharacters)
        }
    /*
     * ========================================================
     * PDF
     * ========================================================
     */
```

#### Qué hace y por qué existe

Carga o reconstruye un recurso/dato a partir de sus entradas, aplicando las capas de caché/normalización definidas en el cuerpo.

#### Contrato de la declaración

**Parámetros:**

- `context: Context` — `context` recibe un valor de tipo `Context`. El contrato no marca este parámetro como anulable. Da acceso a recursos y APIs Android; su vida útil debe corresponder con el uso realizado.
- `uri: Uri` — `uri` recibe un valor de tipo `Uri`. El contrato no marca este parámetro como anulable. Es una referencia de contenido Android; puede apuntar a content providers y no solo a archivos locales.
- `maxCharacters: Int` — `maxCharacters` recibe un valor de tipo `Int`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.

**Retorno:** `String?`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `suspend` permite suspender sin bloquear el hilo; el llamador debe estar dentro de una corrutina u otra función suspendible.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 277 | `val directory` | `inferido` | `cacheDirectory(context, "docx_previews")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 278 | `val cacheFile` | `inferido` | `File(directory, "${cacheKey(uri)}.txt")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Forma parte de la estrategia de reutilización de resultados para evitar repetir trabajo costoso. |
| 280 | `val cached` | `inferido` | `try {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Forma parte de la estrategia de reutilización de resultados para evitar repetir trabajo costoso. |
| 289 | `val preview` | `inferido` | `readDocxPreviewStreaming(context = context,` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 279 | `if (cacheFile.exists() && cacheFile.length() > 0L) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 285 | `if (!cached.isNullOrBlank()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 286 | `return@withContext trimPreview(cached, maxCharacters)` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 292 | `if (!preview.isNullOrBlank()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 293 | `try {` | Frontera de manejo de errores: agrupa operaciones que pueden lanzar excepciones para permitir recuperación o limpieza controlada. |

**Restricciones concretas que aparecen en este bloque:**
- **Trabajo IO:** El trabajo potencialmente bloqueante se desplaza al dispatcher de I/O en vez de ocupar el hilo principal. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

- **Sistema de archivos/caché:** Accede o modifica almacenamiento/caché; son efectos externos al estado puramente en memoria.
- **Caché:** Lee, llena, invalida o administra caché para evitar recomputación o decodificación repetida.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `withContext`, `cacheDirectory`, `File`, `cacheFile.exists`, `cacheFile.length`, `cacheFile.readText`, `cached.isNullOrBlank`, `trimPreview`, `readDocxPreviewStreaming`, `preview.isNullOrBlank`, `cacheFile.writeText`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- No mover trabajo bloqueante al hilo principal; el uso de `Dispatchers.IO` forma parte de la protección de rendimiento.
- Mantener coherencia entre creación, clave, lectura e invalidación de caché; cambiar solo una de esas etapas puede dejar datos obsoletos.

### 4.11 `loadPdfFirstPage` — fun, líneas 305–371

```kotlin
    suspend fun loadPdfFirstPage(context: Context, uri: Uri, performanceMode: String = "balanced"): Bitmap? = withContext(Dispatchers.IO) {
            val directory = cacheDirectory(context, "pdf_previews")
            val key = cacheKey(uri)
            val profile = previewProfile(performanceMode)
            val profileKey = "$key-${profile.cacheTag}"
            val memoryKey = "pdf:$profileKey"
            val cacheFile = File(directory, "$profileKey.jpg")
            val cached = bitmapMemoryCache.get(memoryKey)?: decodeCachedBitmap(cacheFile)?.also {
                                bitmap ->
                            bitmapMemoryCache.put(memoryKey, bitmap)
                        }
            if (cached != null) {
                return@withContext cached
            }
            var descriptor:
                ParcelFileDescriptor? = null
            var renderer:
                PdfRenderer? = null
            var page:
                PdfRenderer.Page? = null
            try {
                descriptor = if (uri.scheme == "file") {
                        val path = uri.path?: return@withContext null
                        ParcelFileDescriptor.open(File(path), ParcelFileDescriptor.MODE_READ_ONLY)
                    } else {
                        context.contentResolver.openFileDescriptor(uri, "r")
                    }
                val safeDescriptor = descriptor?: return@withContext null
                renderer = PdfRenderer(safeDescriptor)
                if (renderer.pageCount <= 0) {
                    return@withContext null
                }
                page = renderer.openPage(0)
                val sourceWidth = max(page.width, 1)
                val sourceHeight = max(page.height, 1)
                val scale = profile.pdfWidth.toFloat() / sourceWidth.toFloat()
                val outputHeight = max(1, (sourceHeight * scale).toInt())
                val bitmap = Bitmap.createBitmap(profile.pdfWidth, outputHeight, Bitmap.Config.ARGB_8888)
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                saveJpeg(bitmap = bitmap,
                    destination = cacheFile,
                    quality = 92)
                bitmapMemoryCache.put(memoryKey, bitmap)
                bitmap
            } catch (e: Exception) {
                e.printStackTrace()
                null
            } finally {
                try {
                    page?.close()
                } catch (ignored: Exception) {
                }
                try {
                    renderer?.close()
                } catch (ignored: Exception) {
                }
                try {
                    descriptor?.close()
                } catch (ignored: Exception) {
                }
            }
        }
    /*
     * ========================================================
     * PRECALENTAR DESPUÉS DE GUARDAR
     * ========================================================
     */
```

#### Qué hace y por qué existe

Carga o reconstruye un recurso/dato a partir de sus entradas, aplicando las capas de caché/normalización definidas en el cuerpo.

#### Contrato de la declaración

**Parámetros:**

- `context: Context` — `context` recibe un valor de tipo `Context`. El contrato no marca este parámetro como anulable. Da acceso a recursos y APIs Android; su vida útil debe corresponder con el uso realizado.
- `uri: Uri` — `uri` recibe un valor de tipo `Uri`. El contrato no marca este parámetro como anulable. Es una referencia de contenido Android; puede apuntar a content providers y no solo a archivos locales.
- `performanceMode: String = "balanced"` — `performanceMode` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable. Tiene valor por defecto `"balanced"`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada. Aunque el tipo sea `String`, semánticamente suele funcionar como clave/enumeración textual; las ramas del código delimitan los valores reconocidos.

**Retorno:** `Bitmap?`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `suspend` permite suspender sin bloquear el hilo; el llamador debe estar dentro de una corrutina u otra función suspendible.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 306 | `val directory` | `inferido` | `cacheDirectory(context, "pdf_previews")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 307 | `val key` | `inferido` | `cacheKey(uri)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 308 | `val profile` | `inferido` | `previewProfile(performanceMode)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 309 | `val profileKey` | `inferido` | `"$key-${profile.cacheTag}"` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 310 | `val memoryKey` | `inferido` | `"pdf:$profileKey"` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 311 | `val cacheFile` | `inferido` | `File(directory, "$profileKey.jpg")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Forma parte de la estrategia de reutilización de resultados para evitar repetir trabajo costoso. |
| 312 | `val cached` | `inferido` | `bitmapMemoryCache.get(memoryKey)?: decodeCachedBitmap(cacheFile)?.also {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Forma parte de la estrategia de reutilización de resultados para evitar repetir trabajo costoso. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 327 | `val path` | `inferido` | `uri.path?: return@withContext null` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 332 | `val safeDescriptor` | `inferido` | `descriptor?: return@withContext null` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 338 | `val sourceWidth` | `inferido` | `max(page.width, 1)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. |
| 339 | `val sourceHeight` | `inferido` | `max(page.height, 1)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. |
| 340 | `val scale` | `inferido` | `profile.pdfWidth.toFloat() / sourceWidth.toFloat()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 341 | `val outputHeight` | `inferido` | `max(1, (sourceHeight * scale).toInt())` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. |
| 342 | `val bitmap` | `inferido` | `Bitmap.createBitmap(profile.pdfWidth, outputHeight, Bitmap.Config.ARGB_8888)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 313 | `bitmap ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 316 | `if (cached != null) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 317 | `return@withContext cached` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 325 | `try {` | Frontera de manejo de errores: agrupa operaciones que pueden lanzar excepciones para permitir recuperación o limpieza controlada. |
| 334 | `if (renderer.pageCount <= 0) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 335 | `return@withContext null` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 353 | `try {` | Frontera de manejo de errores: agrupa operaciones que pueden lanzar excepciones para permitir recuperación o limpieza controlada. |
| 357 | `try {` | Frontera de manejo de errores: agrupa operaciones que pueden lanzar excepciones para permitir recuperación o limpieza controlada. |
| 361 | `try {` | Frontera de manejo de errores: agrupa operaciones que pueden lanzar excepciones para permitir recuperación o limpieza controlada. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 4.
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 3.
- **Trabajo IO:** El trabajo potencialmente bloqueante se desplaza al dispatcher de I/O en vez de ocupar el hilo principal. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

- **Sistema de archivos/caché:** Accede o modifica almacenamiento/caché; son efectos externos al estado puramente en memoria.
- **Caché:** Lee, llena, invalida o administra caché para evitar recomputación o decodificación repetida.
- **Mutación de colección/estado compartido:** Modifica una colección/mapa/estructura existente; el cambio permanece visible para quienes compartan esa misma instancia.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `withContext`, `cacheDirectory`, `cacheKey`, `previewProfile`, `File`, `bitmapMemoryCache.get`, `decodeCachedBitmap`, `bitmapMemoryCache.put`, `ParcelFileDescriptor.open`, `context.contentResolver.openFileDescriptor`, `PdfRenderer`, `renderer.openPage`, `max`, `profile.pdfWidth.toFloat`, `sourceWidth.toFloat`, `toInt`, `Bitmap.createBitmap`, `page.render`, `saveJpeg`, `e.printStackTrace`, `close`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- No mover trabajo bloqueante al hilo principal; el uso de `Dispatchers.IO` forma parte de la protección de rendimiento.
- Mantener coherencia entre creación, clave, lectura e invalidación de caché; cambiar solo una de esas etapas puede dejar datos obsoletos.
- Preservar la normalización y correspondencia de `performanceMode`, porque otras capas usan las mismas claves para rendimiento/calidad.

### 4.12 `prewarm` — fun, líneas 372–401

```kotlin
    suspend fun prewarm(context: Context, uri: Uri, type: String, name: String?, performanceMode: String = "balanced") {
        withPreviewPermit {
            when (type) {
            "image" -> loadImagePreview(context = context, uri = uri, performanceMode = performanceMode)
            "video" -> loadVideoPreview(context = context, uri = uri, performanceMode = performanceMode)
            "audio" -> loadAudioPreview(context = context,
                    uri = uri,
                    loadAlbumArt = true, performanceMode = performanceMode)
            "voice" -> loadAudioPreview(context = context,
                    uri = uri,
                    loadAlbumArt = false, performanceMode = performanceMode)
            "file" -> {
                val extension = name?.substringAfterLast(".", "")?.lowercase().orEmpty()
                when (extension) {
                    "docx" -> loadDocxPreview(context = context,
                            uri = uri,
                            maxCharacters = 650)
                    "pdf" -> loadPdfFirstPage(context = context,
                            uri = uri,
                            performanceMode = performanceMode)
                }
            }
        }
        }
    }
    /*
     * ========================================================
     * INVALIDAR AL BORRAR
     * ========================================================
     */
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `context: Context` — `context` recibe un valor de tipo `Context`. El contrato no marca este parámetro como anulable. Da acceso a recursos y APIs Android; su vida útil debe corresponder con el uso realizado.
- `uri: Uri` — `uri` recibe un valor de tipo `Uri`. El contrato no marca este parámetro como anulable. Es una referencia de contenido Android; puede apuntar a content providers y no solo a archivos locales.
- `type: String` — `type` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable. Aunque el tipo sea `String`, semánticamente suele funcionar como clave/enumeración textual; las ramas del código delimitan los valores reconocidos.
- `name: String?` — `name` recibe un valor de tipo `String?`. El contrato permite `null`; el bloque debe decidir explícita o implícitamente qué significa la ausencia.
- `performanceMode: String = "balanced"` — `performanceMode` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable. Tiene valor por defecto `"balanced"`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada. Aunque el tipo sea `String`, semánticamente suele funcionar como clave/enumeración textual; las ramas del código delimitan los valores reconocidos.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `suspend` permite suspender sin bloquear el hilo; el llamador debe estar dentro de una corrutina u otra función suspendible.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 384 | `val extension` | `inferido` | `name?.substringAfterLast(".", "")?.lowercase().orEmpty()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. Normaliza una ausencia hacia un valor vacío/alternativo para simplificar el código consumidor. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 374 | `when (type) {` | Selección múltiple de Kotlin: concentra casos mutuamente evaluados y normalmente define una normalización o estrategia según el valor de entrada. |
| 375 | `"image" -> loadImagePreview(context = context, uri = uri, performanceMode = performanceMode)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 376 | `"video" -> loadVideoPreview(context = context, uri = uri, performanceMode = performanceMode)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 377 | `"audio" -> loadAudioPreview(context = context,` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 380 | `"voice" -> loadAudioPreview(context = context,` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 383 | `"file" -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 385 | `when (extension) {` | Selección múltiple de Kotlin: concentra casos mutuamente evaluados y normalmente define una normalización o estrategia según el valor de entrada. |
| 386 | `"docx" -> loadDocxPreview(context = context,` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 389 | `"pdf" -> loadPdfFirstPage(context = context,` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 2.
- **Normalización vacía:** `orEmpty` transforma una referencia nula en una colección/cadena vacía, simplificando el consumo posterior. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `loadImagePreview`, `loadVideoPreview`, `loadAudioPreview`, `substringAfterLast`, `lowercase`, `orEmpty`, `loadDocxPreview`, `loadPdfFirstPage`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.
- Preservar la normalización y correspondencia de `performanceMode`, porque otras capas usan las mismas claves para rendimiento/calidad.

### 4.13 `invalidate` — fun, líneas 402–443

```kotlin
    suspend fun invalidate(context: Context, uri: Uri) = withContext(Dispatchers.IO) {
            val key = cacheKey(uri)
            /*
             * El mismo adjunto puede tener hasta tres miniaturas distintas
             * (performance / balanced / quality). Eliminamos todas las
             * variantes de RAM para que un adjunto borrado no deje bitmaps
             * retenidos hasta que el LruCache los expulse por presión de
             * memoria.
             */
            listOf("performance", "balanced", "quality").forEach { profile -> bitmapMemoryCache.remove("image:$key-$profile")
                bitmapMemoryCache.remove("video:$key-$profile")
                bitmapMemoryCache.remove("audio:$key-$profile")
                bitmapMemoryCache.remove("pdf:$key-$profile")
            }
            // Compatibilidad con cachés antiguas sin sufijo de perfil.
            bitmapMemoryCache.remove("image:$key")
            bitmapMemoryCache.remove("video:$key")
            bitmapMemoryCache.remove("audio:$key")
            bitmapMemoryCache.remove("pdf:$key")
            listOf("mynotes_perf_v2", "mynotes").forEach {
                        rootFolder ->
                    listOf("image_previews", "video_previews", "audio_previews", "docx_previews", "pdf_previews").forEach {
                                folder ->
                            val directory = File(context.cacheDir, "$rootFolder/$folder")
                            directory.listFiles()?.filter {
                                        file ->
                                    file.name.startsWith(key)
                                }?.forEach {
                                        file ->
                                    try {
                                        file.delete()
                                    } catch (ignored: Exception) {
                                    }
                                }
                        }
                }
        }
    /*
     * ========================================================
     * HELPERS
     * ========================================================
     */
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `context: Context` — `context` recibe un valor de tipo `Context`. El contrato no marca este parámetro como anulable. Da acceso a recursos y APIs Android; su vida útil debe corresponder con el uso realizado.
- `uri: Uri` — `uri` recibe un valor de tipo `Uri`. El contrato no marca este parámetro como anulable. Es una referencia de contenido Android; puede apuntar a content providers y no solo a archivos locales.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `suspend` permite suspender sin bloquear el hilo; el llamador debe estar dentro de una corrutina u otra función suspendible.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 403 | `val key` | `inferido` | `cacheKey(uri)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 425 | `val directory` | `inferido` | `File(context.cacheDir, "$rootFolder/$folder")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 422 | `rootFolder ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 424 | `folder ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 427 | `file ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 430 | `file ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 431 | `try {` | Frontera de manejo de errores: agrupa operaciones que pueden lanzar excepciones para permitir recuperación o limpieza controlada. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 2.
- **Trabajo IO:** El trabajo potencialmente bloqueante se desplaza al dispatcher de I/O en vez de ocupar el hilo principal. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

- **Persistencia Room/DAO:** Realiza una operación de persistencia o modificación a través de una capa de datos/DAO.
- **Sistema de archivos/caché:** Accede o modifica almacenamiento/caché; son efectos externos al estado puramente en memoria.
- **Caché:** Lee, llena, invalida o administra caché para evitar recomputación o decodificación repetida.
- **Mutación de colección/estado compartido:** Modifica una colección/mapa/estructura existente; el cambio permanece visible para quienes compartan esa misma instancia.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `withContext`, `cacheKey`, `listOf`, `bitmapMemoryCache.remove`, `File`, `directory.listFiles`, `file.name.startsWith`, `file.delete`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- No mover trabajo bloqueante al hilo principal; el uso de `Dispatchers.IO` forma parte de la protección de rendimiento.
- Mantener coherencia entre creación, clave, lectura e invalidación de caché; cambiar solo una de esas etapas puede dejar datos obsoletos.

### 4.14 `cacheDirectory` — fun, líneas 444–450

```kotlin
    private fun cacheDirectory(context: Context, name: String): File {
        val directory = File(context.cacheDir, "mynotes_perf_v2/$name")
        if (!directory.exists()) {
            directory.mkdirs()
        }
        return directory
    }
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `context: Context` — `context` recibe un valor de tipo `Context`. El contrato no marca este parámetro como anulable. Da acceso a recursos y APIs Android; su vida útil debe corresponder con el uso realizado.
- `name: String` — `name` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `File`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 445 | `val directory` | `inferido` | `File(context.cacheDir, "mynotes_perf_v2/$name")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 446 | `if (!directory.exists()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 449 | `return directory` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

#### Efectos secundarios y recursos

- **Sistema de archivos/caché:** Accede o modifica almacenamiento/caché; son efectos externos al estado puramente en memoria.
- **Caché:** Lee, llena, invalida o administra caché para evitar recomputación o decodificación repetida.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `File`, `directory.exists`, `directory.mkdirs`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener coherencia entre creación, clave, lectura e invalidación de caché; cambiar solo una de esas etapas puede dejar datos obsoletos.

### 4.15 `cacheKey` — fun, líneas 451–456

```kotlin
    private fun cacheKey(uri: Uri): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(uri.toString().toByteArray(Charsets.UTF_8))
        return digest.joinToString(separator = "") {
                byte -> "%02x".format(byte)
            }
    }
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `uri: Uri` — `uri` recibe un valor de tipo `Uri`. El contrato no marca este parámetro como anulable. Es una referencia de contenido Android; puede apuntar a content providers y no solo a archivos locales.

**Retorno:** `String`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 452 | `val digest` | `inferido` | `MessageDigest.getInstance("SHA-256").digest(uri.toString().toByteArray(Charsets.UTF_8))` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 453 | `return digest.joinToString(separator = "") {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 454 | `byte -> "%02x".format(byte)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |

#### Efectos secundarios y recursos

- **Caché:** Lee, llena, invalida o administra caché para evitar recomputación o decodificación repetida.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `MessageDigest.getInstance`, `digest`, `uri.toString`, `toByteArray`, `digest.joinToString`, `format`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener coherencia entre creación, clave, lectura e invalidación de caché; cambiar solo una de esas etapas puede dejar datos obsoletos.

### 4.16 `setRetrieverDataSource` — fun, líneas 457–464

```kotlin
    private fun setRetrieverDataSource(context: Context, retriever: MediaMetadataRetriever, uri: Uri) {
        if (uri.scheme == "file") {
            val path = uri.path?: throw IllegalArgumentException("URI de archivo inválida")
            retriever.setDataSource(path)
        } else {
            retriever.setDataSource(context, uri)
        }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `context: Context` — `context` recibe un valor de tipo `Context`. El contrato no marca este parámetro como anulable. Da acceso a recursos y APIs Android; su vida útil debe corresponder con el uso realizado.
- `retriever: MediaMetadataRetriever` — `retriever` recibe un valor de tipo `MediaMetadataRetriever`. El contrato no marca este parámetro como anulable.
- `uri: Uri` — `uri` recibe un valor de tipo `Uri`. El contrato no marca este parámetro como anulable. Es una referencia de contenido Android; puede apuntar a content providers y no solo a archivos locales.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 459 | `val path` | `inferido` | `uri.path?: throw IllegalArgumentException("URI de archivo inválida")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 458 | `if (uri.scheme == "file") {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |

**Restricciones concretas que aparecen en este bloque:**
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `IllegalArgumentException`, `retriever.setDataSource`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.17 `createVideoFrame` — fun, líneas 465–513

```kotlin
    private fun createVideoFrame(context: Context, uri: Uri, retriever: MediaMetadataRetriever, sourceWidth: Int, sourceHeight: Int,
        maxWidth: Int, maxHeight: Int): Bitmap? {
        val safeWidth = max(sourceWidth, 1)
        val safeHeight = max(sourceHeight, 1)
        val scale = minOf(maxWidth.toFloat() / safeWidth.toFloat(),
                maxHeight.toFloat() / safeHeight.toFloat(),
                1f)
        val targetWidth = max(1, (safeWidth * scale).toInt())
        val targetHeight = max(1, (safeHeight * scale).toInt())
        /*
         * Android 8.1+ puede pedir directamente un frame reducido al
         * MediaMetadataRetriever. En Android 7/7.1 y 8.0 esa API no existe.
         * Cuando el adjunto ya está en almacenamiento privado usamos
         * ThumbnailUtils para evitar decodificar temporalmente un frame 4K
         * completo, algo que podía provocar picos fuertes de RAM.
         */
        if (Build.VERSION.SDK_INT <
            Build.VERSION_CODES.O_MR1 && uri.scheme == "file") {
            val path = uri.path
            if (!path.isNullOrBlank()) {
                try {
                    @Suppress("DEPRECATION")
                    val legacyThumbnail = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Video.Thumbnails.MINI_KIND)
                    if (legacyThumbnail != null) {
                        return scalePreviewBitmap(bitmap = legacyThumbnail, maxWidth = maxWidth, maxHeight = maxHeight)
                    }
                } catch (_: OutOfMemoryError) {
                    /*
                     * Un vídeo enorme no debe cerrar la aplicación por una
                     * miniatura. Continuamos con el fallback protegido.
                     */
                } catch (_: Exception) {
                    // Continuamos con MediaMetadataRetriever.
                }
            }
        }
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                retriever.getScaledFrameAtTime(1_000_000L, MediaMetadataRetriever.OPTION_CLOSEST_SYNC, targetWidth, targetHeight)
            } else {
                val original = retriever.getFrameAtTime(1_000_000L, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)?: return null
                scalePreviewBitmap(bitmap = original, maxWidth = maxWidth, maxHeight = maxHeight)
            }
        } catch (_: OutOfMemoryError) {
            null
        } catch (_: Exception) {
            null
        }
    }
```

#### Qué hace y por qué existe

Construye un valor/recurso derivado a partir de los parámetros y reglas internas del bloque.

#### Contrato de la declaración

**Parámetros:**

- `context: Context` — `context` recibe un valor de tipo `Context`. El contrato no marca este parámetro como anulable. Da acceso a recursos y APIs Android; su vida útil debe corresponder con el uso realizado.
- `uri: Uri` — `uri` recibe un valor de tipo `Uri`. El contrato no marca este parámetro como anulable. Es una referencia de contenido Android; puede apuntar a content providers y no solo a archivos locales.
- `retriever: MediaMetadataRetriever` — `retriever` recibe un valor de tipo `MediaMetadataRetriever`. El contrato no marca este parámetro como anulable.
- `sourceWidth: Int` — `sourceWidth` recibe un valor de tipo `Int`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.
- `sourceHeight: Int` — `sourceHeight` recibe un valor de tipo `Int`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.
- `maxWidth: Int` — `maxWidth` recibe un valor de tipo `Int`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.
- `maxHeight: Int` — `maxHeight` recibe un valor de tipo `Int`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.

**Retorno:** `Bitmap?`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 467 | `val safeWidth` | `inferido` | `max(sourceWidth, 1)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. |
| 468 | `val safeHeight` | `inferido` | `max(sourceHeight, 1)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. |
| 469 | `val scale` | `inferido` | `minOf(maxWidth.toFloat() / safeWidth.toFloat(),` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 472 | `val targetWidth` | `inferido` | `max(1, (safeWidth * scale).toInt())` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. |
| 473 | `val targetHeight` | `inferido` | `max(1, (safeHeight * scale).toInt())` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. |
| 483 | `val path` | `inferido` | `uri.path` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 487 | `val legacyThumbnail` | `inferido` | `ThumbnailUtils.createVideoThumbnail(path, MediaStore.Video.Thumbnails.MINI_KIND)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 505 | `val original` | `inferido` | `retriever.getFrameAtTime(1_000_000L, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)?: return null` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 481 | `if (Build.VERSION.SDK_INT <` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 484 | `if (!path.isNullOrBlank()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 485 | `try {` | Frontera de manejo de errores: agrupa operaciones que pueden lanzar excepciones para permitir recuperación o limpieza controlada. |
| 488 | `if (legacyThumbnail != null) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 489 | `return scalePreviewBitmap(bitmap = legacyThumbnail, maxWidth = maxWidth, maxHeight = maxHeight)` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 501 | `return try {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 502 | `if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |

**Restricciones concretas que aparecen en este bloque:**
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 1.
- **Guardia de API:** Se condiciona una API según la versión de Android para no ejecutar llamadas incompatibles en dispositivos antiguos. Apariciones en este bloque: 2.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `max`, `minOf`, `maxWidth.toFloat`, `safeWidth.toFloat`, `maxHeight.toFloat`, `safeHeight.toFloat`, `toInt`, `path.isNullOrBlank`, `Suppress`, `ThumbnailUtils.createVideoThumbnail`, `scalePreviewBitmap`, `retriever.getScaledFrameAtTime`, `retriever.getFrameAtTime`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar las guardas de versión antes de APIs que no existen en Android antiguos.

### 4.18 `decodeImageThumbnail` — fun, líneas 514–545

```kotlin
    private fun decodeImageThumbnail(context: Context, uri: Uri, maxWidth: Int, maxHeight: Int): Bitmap? {
        return try {
            val bounds = BitmapFactory.Options().apply {
                        inJustDecodeBounds = true
                    }
            openUriInputStream(context = context, uri = uri)?.use {
                        input ->
                    BitmapFactory.decodeStream(input, null, bounds)
                }
            if (bounds.outWidth <= 0 || bounds.outHeight <= 0) {
                return null
            }
            var sample = 1
            while (bounds.outWidth / sample > maxWidth * 2 || bounds.outHeight / sample > maxHeight * 2) {
                sample *= 2
            }
            val options = BitmapFactory.Options().apply {
                        inSampleSize = sample
                        inPreferredConfig = Bitmap.Config.RGB_565
                    }
            val decoded = openUriInputStream(context = context, uri = uri)?.use {
                            input ->
                        BitmapFactory.decodeStream(input, null, options)
                    }?: return null
            val oriented = applyExifOrientation(context = context, uri = uri, bitmap = decoded)
            scalePreviewBitmap(bitmap = oriented, maxWidth = maxWidth, maxHeight = maxHeight)
        } catch (_: OutOfMemoryError) {
            null
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
- `maxWidth: Int` — `maxWidth` recibe un valor de tipo `Int`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.
- `maxHeight: Int` — `maxHeight` recibe un valor de tipo `Int`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.

**Retorno:** `Bitmap?`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 516 | `val bounds` | `inferido` | `BitmapFactory.Options().apply {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 526 | `var sample` | `inferido` | `1` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 530 | `val options` | `inferido` | `BitmapFactory.Options().apply {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 534 | `val decoded` | `inferido` | `openUriInputStream(context = context, uri = uri)?.use {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 538 | `val oriented` | `inferido` | `applyExifOrientation(context = context, uri = uri, bitmap = decoded)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 515 | `return try {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 520 | `input ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 523 | `if (bounds.outWidth <= 0 \|\| bounds.outHeight <= 0) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 524 | `return null` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 527 | `while (bounds.outWidth / sample > maxWidth * 2 \|\| bounds.outHeight / sample > maxHeight * 2) {` | Bucle condicionado: repite mientras la condición se mantenga verdadera; la lógica interna debe permitir progreso hacia la salida. |
| 535 | `input ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 2.
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `BitmapFactory.Options`, `openUriInputStream`, `BitmapFactory.decodeStream`, `applyExifOrientation`, `scalePreviewBitmap`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.19 `openUriInputStream` — fun, líneas 546–553

```kotlin
    private fun openUriInputStream(context: Context, uri: Uri): InputStream? {
        return if (uri.scheme == "file") {
            val path = uri.path?: return null
            FileInputStream(File(path))
        } else {
            context.contentResolver.openInputStream(uri)
        }
    }
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `context: Context` — `context` recibe un valor de tipo `Context`. El contrato no marca este parámetro como anulable. Da acceso a recursos y APIs Android; su vida útil debe corresponder con el uso realizado.
- `uri: Uri` — `uri` recibe un valor de tipo `Uri`. El contrato no marca este parámetro como anulable. Es una referencia de contenido Android; puede apuntar a content providers y no solo a archivos locales.

**Retorno:** `InputStream?`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 548 | `val path` | `inferido` | `uri.path?: return null` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 547 | `return if (uri.scheme == "file") {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

**Restricciones concretas que aparecen en este bloque:**
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

- **Sistema de archivos/caché:** Accede o modifica almacenamiento/caché; son efectos externos al estado puramente en memoria.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `FileInputStream`, `File`, `context.contentResolver.openInputStream`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.20 `applyExifOrientation` — fun, líneas 554–599

```kotlin
    private fun applyExifOrientation(context: Context, uri: Uri, bitmap: Bitmap): Bitmap {
        val orientation = try {
                if (uri.scheme == "file") {
                    val path = uri.path?: return bitmap
                    ExifInterface(path).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
                } else {
                    openUriInputStream(context = context, uri = uri)?.use {
                                input ->
                            ExifInterface(input).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
                        }?: ExifInterface.ORIENTATION_NORMAL
                }
            } catch (_: Exception) {
                ExifInterface.ORIENTATION_NORMAL
            }
        if (orientation == ExifInterface.ORIENTATION_NORMAL || orientation == ExifInterface.ORIENTATION_UNDEFINED) {
            return bitmap
        }
        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.setScale(-1f, 1f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.setRotate(180f)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.setScale(1f, -1f)
            ExifInterface.ORIENTATION_TRANSPOSE -> {
                matrix.setRotate(90f)
                matrix.postScale(-1f, 1f)
            }
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.setRotate(90f)
            ExifInterface.ORIENTATION_TRANSVERSE -> {
                matrix.setRotate(-90f)
                matrix.postScale(-1f, 1f)
            }
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.setRotate(-90f)
            else -> return bitmap
        }
        return try {
            val transformed = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            if (transformed !== bitmap) {
                bitmap.recycle()
            }
            transformed
        } catch (_: OutOfMemoryError) {
            bitmap
        } catch (_: Exception) {
            bitmap
        }
    }
```

#### Qué hace y por qué existe

Aplica una política/configuración calculada sobre el objeto o sistema destino.

#### Contrato de la declaración

**Parámetros:**

- `context: Context` — `context` recibe un valor de tipo `Context`. El contrato no marca este parámetro como anulable. Da acceso a recursos y APIs Android; su vida útil debe corresponder con el uso realizado.
- `uri: Uri` — `uri` recibe un valor de tipo `Uri`. El contrato no marca este parámetro como anulable. Es una referencia de contenido Android; puede apuntar a content providers y no solo a archivos locales.
- `bitmap: Bitmap` — `bitmap` recibe un valor de tipo `Bitmap`. El contrato no marca este parámetro como anulable.

**Retorno:** `Bitmap`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 555 | `val orientation` | `inferido` | `try {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 557 | `val path` | `inferido` | `uri.path?: return bitmap` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 571 | `val matrix` | `inferido` | `Matrix()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 589 | `val transformed` | `inferido` | `Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 556 | `if (uri.scheme == "file") {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 561 | `input ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 568 | `if (orientation == ExifInterface.ORIENTATION_NORMAL \|\| orientation == ExifInterface.ORIENTATION_UNDEFINED) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 569 | `return bitmap` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 572 | `when (orientation) {` | Selección múltiple de Kotlin: concentra casos mutuamente evaluados y normalmente define una normalización o estrategia según el valor de entrada. |
| 573 | `ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.setScale(-1f, 1f)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 574 | `ExifInterface.ORIENTATION_ROTATE_180 -> matrix.setRotate(180f)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 575 | `ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.setScale(1f, -1f)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 576 | `ExifInterface.ORIENTATION_TRANSPOSE -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 580 | `ExifInterface.ORIENTATION_ROTATE_90 -> matrix.setRotate(90f)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 581 | `ExifInterface.ORIENTATION_TRANSVERSE -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 585 | `ExifInterface.ORIENTATION_ROTATE_270 -> matrix.setRotate(-90f)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 586 | `else -> return bitmap` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |
| 588 | `return try {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 590 | `if (transformed !== bitmap) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 1.
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 2.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `ExifInterface`, `getAttributeInt`, `openUriInputStream`, `Matrix`, `matrix.setScale`, `matrix.setRotate`, `matrix.postScale`, `Bitmap.createBitmap`, `bitmap.recycle`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.

### 4.21 `scalePreviewBitmap` — fun, líneas 600–613

```kotlin
    private fun scalePreviewBitmap(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
        if (bitmap.width <= maxWidth && bitmap.height <= maxHeight) {
            return bitmap
        }
        val scale = minOf(maxWidth.toFloat() / bitmap.width.coerceAtLeast(1),
                maxHeight.toFloat() / bitmap.height.coerceAtLeast(1))
        val width = max(1, (bitmap.width * scale).toInt())
        val height = max(1, (bitmap.height * scale).toInt())
        val scaled = Bitmap.createScaledBitmap(bitmap, width, height, true)
        if (scaled !== bitmap) {
            bitmap.recycle()
        }
        return scaled
    }
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `bitmap: Bitmap` — `bitmap` recibe un valor de tipo `Bitmap`. El contrato no marca este parámetro como anulable.
- `maxWidth: Int` — `maxWidth` recibe un valor de tipo `Int`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.
- `maxHeight: Int` — `maxHeight` recibe un valor de tipo `Int`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.

**Retorno:** `Bitmap`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 604 | `val scale` | `inferido` | `minOf(maxWidth.toFloat() / bitmap.width.coerceAtLeast(1),` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El inicializador limita explícitamente el valor a un rango, evitando que dimensiones/intensidades/sizes inválidos se propaguen. |
| 606 | `val width` | `inferido` | `max(1, (bitmap.width * scale).toInt())` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. |
| 607 | `val height` | `inferido` | `max(1, (bitmap.height * scale).toInt())` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. |
| 608 | `val scaled` | `inferido` | `Bitmap.createScaledBitmap(bitmap, width, height, true)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 601 | `if (bitmap.width <= maxWidth && bitmap.height <= maxHeight) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 602 | `return bitmap` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 609 | `if (scaled !== bitmap) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 612 | `return scaled` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

**Restricciones concretas que aparecen en este bloque:**
- **Límite numérico:** Se fuerza el valor a un intervalo o umbral para impedir que salgan valores fuera de los límites esperados. Apariciones en este bloque: 2.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `minOf`, `maxWidth.toFloat`, `bitmap.width.coerceAtLeast`, `maxHeight.toFloat`, `bitmap.height.coerceAtLeast`, `max`, `toInt`, `Bitmap.createScaledBitmap`, `bitmap.recycle`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.22 `saveJpeg` — fun, líneas 614–626

```kotlin
    private fun saveJpeg(bitmap: Bitmap, destination: File, quality: Int) {
        try {
            destination.outputStream().buffered().use {
                        output ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, output)
                }
        } catch (e: Exception) {
            try {
                destination.delete()
            } catch (ignored: Exception) {
            }
        }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `bitmap: Bitmap` — `bitmap` recibe un valor de tipo `Bitmap`. El contrato no marca este parámetro como anulable.
- `destination: File` — `destination` recibe un valor de tipo `File`. El contrato no marca este parámetro como anulable.
- `quality: Int` — `quality` recibe un valor de tipo `Int`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 615 | `try {` | Frontera de manejo de errores: agrupa operaciones que pueden lanzar excepciones para permitir recuperación o limpieza controlada. |
| 617 | `output ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 621 | `try {` | Frontera de manejo de errores: agrupa operaciones que pueden lanzar excepciones para permitir recuperación o limpieza controlada. |

#### Efectos secundarios y recursos

- **Persistencia Room/DAO:** Realiza una operación de persistencia o modificación a través de una capa de datos/DAO.
- **Sistema de archivos/caché:** Accede o modifica almacenamiento/caché; son efectos externos al estado puramente en memoria.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `destination.outputStream`, `buffered`, `bitmap.compress`, `destination.delete`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.23 `decodeCachedBitmap` — fun, líneas 627–639

```kotlin
    private fun decodeCachedBitmap(file: File): Bitmap? {
        if (!file.exists() || file.length() <= 0L) {
            return null
        }
        return try {
            val options = BitmapFactory.Options().apply {
                        inPreferredConfig = Bitmap.Config.ARGB_8888
                    }
            BitmapFactory.decodeFile(file.absolutePath, options)
        } catch (e: Exception) {
            null
        }
    }
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `file: File` — `file` recibe un valor de tipo `File`. El contrato no marca este parámetro como anulable.

**Retorno:** `Bitmap?`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 632 | `val options` | `inferido` | `BitmapFactory.Options().apply {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 628 | `if (!file.exists() \|\| file.length() <= 0L) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 629 | `return null` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 631 | `return try {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

#### Efectos secundarios y recursos

- **Sistema de archivos/caché:** Accede o modifica almacenamiento/caché; son efectos externos al estado puramente en memoria.
- **Caché:** Lee, llena, invalida o administra caché para evitar recomputación o decodificación repetida.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `file.exists`, `file.length`, `BitmapFactory.Options`, `BitmapFactory.decodeFile`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener coherencia entre creación, clave, lectura e invalidación de caché; cambiar solo una de esas etapas puede dejar datos obsoletos.

### 4.24 `decodeSampledBitmap` — fun, líneas 640–656

```kotlin
    private fun decodeSampledBitmap(bytes: ByteArray, requestedSize: Int): Bitmap? {
        val bounds = BitmapFactory.Options().apply {
                    inJustDecodeBounds = true
                }
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size, bounds)
        var sample = 1
        while (bounds.outWidth / sample >
            requestedSize * 2 || bounds.outHeight / sample >
            requestedSize * 2) {
            sample *= 2
        }
        val options = BitmapFactory.Options().apply {
                    inSampleSize = sample
                    inPreferredConfig = Bitmap.Config.RGB_565
                }
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
    }
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `bytes: ByteArray` — `bytes` recibe un valor de tipo `ByteArray`. El contrato no marca este parámetro como anulable.
- `requestedSize: Int` — `requestedSize` recibe un valor de tipo `Int`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.

**Retorno:** `Bitmap?`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 641 | `val bounds` | `inferido` | `BitmapFactory.Options().apply {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 645 | `var sample` | `inferido` | `1` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 651 | `val options` | `inferido` | `BitmapFactory.Options().apply {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 646 | `while (bounds.outWidth / sample >` | Bucle condicionado: repite mientras la condición se mantenga verdadera; la lógica interna debe permitir progreso hacia la salida. |
| 655 | `return BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `BitmapFactory.Options`, `BitmapFactory.decodeByteArray`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.25 `readMediaMetadata` — fun, líneas 657–669

```kotlin
    private fun readMediaMetadata(file: File): MediaPreview? {
        if (!file.exists() || file.length() <= 0L) {
            return null
        }
        return try {
            val values = file.readText().split(",")
            MediaPreview(durationMillis = values.getOrNull(0)?.toLongOrNull()?: 0L,
                width = values.getOrNull(1)?.toIntOrNull()?: 0,
                height = values.getOrNull(2)?.toIntOrNull()?: 0)
        } catch (e: Exception) {
            null
        }
    }
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `file: File` — `file` recibe un valor de tipo `File`. El contrato no marca este parámetro como anulable.

**Retorno:** `MediaPreview?`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 662 | `val values` | `inferido` | `file.readText().split(",")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 658 | `if (!file.exists() \|\| file.length() <= 0L) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 659 | `return null` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 661 | `return try {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 3.
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 3.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `file.exists`, `file.length`, `file.readText`, `split`, `MediaPreview`, `values.getOrNull`, `toLongOrNull`, `toIntOrNull`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.26 `writeMediaMetadata` — fun, líneas 670–675

```kotlin
    private fun writeMediaMetadata(file: File, preview: MediaPreview) {
        try {
            file.writeText("${preview.durationMillis}," + "${preview.width}," + "${preview.height}")
        } catch (ignored: Exception) {
        }
    }
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `file: File` — `file` recibe un valor de tipo `File`. El contrato no marca este parámetro como anulable.
- `preview: MediaPreview` — `preview` recibe un valor de tipo `MediaPreview`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 671 | `try {` | Frontera de manejo de errores: agrupa operaciones que pueden lanzar excepciones para permitir recuperación o limpieza controlada. |

#### Efectos secundarios y recursos

- **Sistema de archivos/caché:** Accede o modifica almacenamiento/caché; son efectos externos al estado puramente en memoria.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `file.writeText`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.27 `readDocxPreviewStreaming` — fun, líneas 676–734

```kotlin
    private fun readDocxPreviewStreaming(context: Context, uri: Uri, maxCharacters: Int): String? {
        return try {
            val source = if (uri.scheme == "file") {
                    val path = uri.path?: return null
                    FileInputStream(path)
                } else {
                    context.contentResolver.openInputStream(uri)?: return null
                }
            source.use {
                        input ->
                    ZipInputStream(BufferedInputStream(input)).use {
                                zip ->
                            var entry = zip.nextEntry
                            while (entry != null) {
                                if (entry.name == "word/document.xml") {
                                    val parser = Xml.newPullParser()
                                    parser.setInput(zip, "UTF-8")
                                    val result = StringBuilder()
                                    var event = parser.eventType
                                    while (event != XmlPullParser.END_DOCUMENT && result.length <
                                        maxCharacters) {
                                        when (event) {
                                            XmlPullParser.START_TAG -> {
                                                if (parser.name == "t") {
                                                    val text = parser.nextText()
                                                    if (text.isNotBlank()) {
                                                        if (result.isNotEmpty() && !result.endsWith(" ") && !result.endsWith("\n")) {
                                                            result.append(' ')
                                                        }
                                                        result.append(text)
                                                    }
                                                }
                                            }
                                            XmlPullParser.END_TAG -> {
                                                if (parser.name == "p" && result.isNotEmpty() && !result.endsWith("\n")) {
                                                    result.append('\n')
                                                }
                                            }
                                        }
                                        event = parser.next()
                                    }
                                    val clean = result.toString().replace(Regex("[\\t ]+"), " ").replace(Regex("\\n{3,}"), "\n\n").trim()
                                    return if (clean.isBlank()) {
                                        null
                                    } else {
                                        clean
                                    }
                                }
                                zip.closeEntry()
                                entry = zip.nextEntry
                            }
                        }
                }
            null
        } catch (e: Exception) {
            e.printStackTrace()
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
- `maxCharacters: Int` — `maxCharacters` recibe un valor de tipo `Int`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.

**Retorno:** `String?`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 678 | `val source` | `inferido` | `if (uri.scheme == "file") {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 679 | `val path` | `inferido` | `uri.path?: return null` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 688 | `var entry` | `inferido` | `zip.nextEntry` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 691 | `val parser` | `inferido` | `Xml.newPullParser()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 693 | `val result` | `inferido` | `StringBuilder()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 694 | `var event` | `inferido` | `parser.eventType` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 700 | `val text` | `inferido` | `parser.nextText()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 717 | `val clean` | `inferido` | `result.toString().replace(Regex("[\\t ]+"), " ").replace(Regex("\\n{3,}"), "\n\n").trim()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 677 | `return try {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 685 | `input ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 687 | `zip ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 689 | `while (entry != null) {` | Bucle condicionado: repite mientras la condición se mantenga verdadera; la lógica interna debe permitir progreso hacia la salida. |
| 690 | `if (entry.name == "word/document.xml") {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 695 | `while (event != XmlPullParser.END_DOCUMENT && result.length <` | Bucle condicionado: repite mientras la condición se mantenga verdadera; la lógica interna debe permitir progreso hacia la salida. |
| 697 | `when (event) {` | Selección múltiple de Kotlin: concentra casos mutuamente evaluados y normalmente define una normalización o estrategia según el valor de entrada. |
| 698 | `XmlPullParser.START_TAG -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 699 | `if (parser.name == "t") {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 701 | `if (text.isNotBlank()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 702 | `if (result.isNotEmpty() && !result.endsWith(" ") && !result.endsWith("\n")) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 709 | `XmlPullParser.END_TAG -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 710 | `if (parser.name == "p" && result.isNotEmpty() && !result.endsWith("\n")) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 718 | `return if (clean.isBlank()) {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

**Restricciones concretas que aparecen en este bloque:**
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 2.

#### Efectos secundarios y recursos

- **Persistencia Room/DAO:** Realiza una operación de persistencia o modificación a través de una capa de datos/DAO.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `FileInputStream`, `context.contentResolver.openInputStream`, `ZipInputStream`, `BufferedInputStream`, `Xml.newPullParser`, `parser.setInput`, `StringBuilder`, `parser.nextText`, `text.isNotBlank`, `result.isNotEmpty`, `result.endsWith`, `result.append`, `parser.next`, `result.toString`, `replace`, `Regex`, `trim`, `clean.isBlank`, `zip.closeEntry`, `e.printStackTrace`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.

### 4.28 `trimPreview` — fun, líneas 735–743

```kotlin
    private fun trimPreview(text: String?, maxCharacters: Int): String? {
        if (text.isNullOrBlank()) {
            return null
        }
        if (text.length <= maxCharacters) {
            return text
        }
        return text.take(maxCharacters) + "…"
    }
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `text: String?` — `text` recibe un valor de tipo `String?`. El contrato permite `null`; el bloque debe decidir explícita o implícitamente qué significa la ausencia.
- `maxCharacters: Int` — `maxCharacters` recibe un valor de tipo `Int`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.

**Retorno:** `String?`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 736 | `if (text.isNullOrBlank()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 737 | `return null` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 739 | `if (text.length <= maxCharacters) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 740 | `return text` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 742 | `return text.take(maxCharacters) + "…"` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `text.isNullOrBlank`, `text.take`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

## 5. Inventario global de propiedades/variables detectadas

| Línea | Identificador | Mutabilidad | Tipo | Explicación |
|---:|---|---|---|---|
| 52 | `previewLoadGate` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 57 | `aspectRatio` | `val` | `Float` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo declarado es `Float`. No declara nulabilidad explícita. |
| 64 | `DOCX_CACHE_CHARACTERS` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Forma parte de la estrategia de reutilización de resultados para evitar repetir trabajo costoso. Está marcada `const`, por lo que es una constante de tiempo de compilación y solo puede usar un tipo/valor admitido por Kotlin para constantes. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 76 | `videoHeight` | `val` | `Int, val audioArtSize: Int, val pdfWidth: Int, val imageJpegQuality: Int, val videoJpegQuality: Int,` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo declarado es `Int, val audioArtSize: Int, val pdfWidth: Int, val imageJpegQuality: Int, val videoJpegQuality: Int,`. No declara nulabilidad explícita. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. |
| 77 | `audioJpegQuality` | `val` | `Int, val pdfJpegQuality: Int)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo declarado es `Int, val pdfJpegQuality: Int)`. No declara nulabilidad explícita. |
| 99 | `memoryCacheKb` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Forma parte de la estrategia de reutilización de resultados para evitar repetir trabajo costoso. El inicializador limita explícitamente el valor a un rango, evitando que dimensiones/intensidades/sizes inválidos se propaguen. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 100 | `bitmapMemoryCache` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Es una caché acotada por política LRU: privilegia entradas recientes y permite expulsión automática cuando se supera el límite. Forma parte de la estrategia de reutilización de resultados para evitar repetir trabajo costoso. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 118 | `directory` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 119 | `key` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 120 | `profile` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 121 | `profileKey` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 122 | `memoryKey` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 123 | `cacheFile` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Forma parte de la estrategia de reutilización de resultados para evitar repetir trabajo costoso. |
| 133 | `bitmap` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 146 | `directory` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 147 | `key` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 148 | `profile` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 149 | `profileKey` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 150 | `imageFile` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 151 | `metadataFile` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 152 | `memoryKey` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 153 | `cachedMetadata` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Forma parte de la estrategia de reutilización de resultados para evitar repetir trabajo costoso. |
| 154 | `memoryBitmap` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 158 | `cachedBitmap` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Forma parte de la estrategia de reutilización de resultados para evitar repetir trabajo costoso. |
| 163 | `retriever` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 166 | `duration` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 167 | `rawWidth` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 168 | `rawHeight` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 169 | `rotation` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 170 | `width` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. |
| 175 | `height` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. |
| 180 | `bitmap` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 188 | `result` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 211 | `directory` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 212 | `key` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 213 | `profile` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 214 | `profileKey` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 215 | `imageFile` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 216 | `metadataFile` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 217 | `memoryKey` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 218 | `cachedMetadata` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Forma parte de la estrategia de reutilización de resultados para evitar repetir trabajo costoso. |
| 219 | `cachedBitmap` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Forma parte de la estrategia de reutilización de resultados para evitar repetir trabajo costoso. |
| 230 | `retriever` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 233 | `duration` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 237 | `embeddedPicture` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 257 | `result` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 277 | `directory` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 278 | `cacheFile` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Forma parte de la estrategia de reutilización de resultados para evitar repetir trabajo costoso. |
| 280 | `cached` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Forma parte de la estrategia de reutilización de resultados para evitar repetir trabajo costoso. |
| 289 | `preview` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 306 | `directory` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 307 | `key` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 308 | `profile` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 309 | `profileKey` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 310 | `memoryKey` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 311 | `cacheFile` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Forma parte de la estrategia de reutilización de resultados para evitar repetir trabajo costoso. |
| 312 | `cached` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Forma parte de la estrategia de reutilización de resultados para evitar repetir trabajo costoso. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 327 | `path` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 332 | `safeDescriptor` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 338 | `sourceWidth` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. |
| 339 | `sourceHeight` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. |
| 340 | `scale` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 341 | `outputHeight` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. |
| 342 | `bitmap` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 384 | `extension` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. Normaliza una ausencia hacia un valor vacío/alternativo para simplificar el código consumidor. |
| 403 | `key` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 425 | `directory` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 445 | `directory` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 452 | `digest` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 459 | `path` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 467 | `safeWidth` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. |
| 468 | `safeHeight` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. |
| 469 | `scale` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 472 | `targetWidth` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. |
| 473 | `targetHeight` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. |
| 483 | `path` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 487 | `legacyThumbnail` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 505 | `original` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 516 | `bounds` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 526 | `sample` | `var` | `inferido` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 530 | `options` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 534 | `decoded` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 538 | `oriented` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 548 | `path` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 555 | `orientation` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 557 | `path` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 571 | `matrix` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 589 | `transformed` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 604 | `scale` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El inicializador limita explícitamente el valor a un rango, evitando que dimensiones/intensidades/sizes inválidos se propaguen. |
| 606 | `width` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. |
| 607 | `height` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. |
| 608 | `scaled` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 632 | `options` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 641 | `bounds` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 645 | `sample` | `var` | `inferido` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 651 | `options` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 662 | `values` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 678 | `source` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 679 | `path` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 688 | `entry` | `var` | `inferido` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 691 | `parser` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 693 | `result` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 694 | `event` | `var` | `inferido` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 700 | `text` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 717 | `clean` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

## 6. Mapa exhaustivo de TODOS los bloques delimitados por `{}`

Esta tabla recorre todas las llaves estructurales detectadas fuera de strings/comentarios. Así se documentan también lambdas de Compose, callbacks, iteraciones y ámbitos locales que no tienen un nombre propio de función.

| Inicio–fin | Prof. | Cabecera/contexto | Qué significa ese bloque |
|---|---:|---|---|
| 46–744 | 0 | `object AttachmentPreviewCache` | Cuerpo de tipo/contenedor: agrupa propiedades y miembros bajo la misma responsabilidad y controla su visibilidad/estado compartido. |
| 53–55 | 1 | `suspend fun <T> withPreviewPermit(block: suspend () -> T): T = previewLoadGate.withPermit` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 56–63 | 1 | `data class MediaPreview(val bitmap: Bitmap? = null, val durationMillis: Long = 0L, val width: Int = 0, val height: Int = 0)` | Cuerpo de tipo/contenedor: agrupa propiedades y miembros bajo la misma responsabilidad y controla su visibilidad/estado compartido. |
| 58–60 | 2 | `get() = if (width > 0 && height > 0)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 60–62 | 2 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 78–88 | 1 | `private fun previewProfile(performanceMode: String): PreviewProfile = when (performanceMode)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 100–104 | 1 | `private val bitmapMemoryCache = object : LruCache<String, Bitmap>(memoryCacheKb)` | Cuerpo de tipo/contenedor: agrupa propiedades y miembros bajo la misma responsabilidad y controla su visibilidad/estado compartido. |
| 101–103 | 2 | `override fun sizeOf(key: String, value: Bitmap): Int` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 117–138 | 1 | `suspend fun loadImagePreview(context: Context, uri: Uri, performanceMode: String = "balanced"): Bitmap? = withContext(Dispatchers.IO)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 124–126 | 2 | `bitmapMemoryCache.get(memoryKey)?.let` | Lambda `let`: transforma/usa el receptor dentro de un ámbito corto; con llamada segura suele ejecutarse solo cuando el receptor no es nulo. |
| 127–130 | 2 | `decodeCachedBitmap(cacheFile)?.also` | Lambda `also`: ejecuta una acción auxiliar sobre el valor y conserva el valor original como resultado de la expresión. |
| 130–132 | 2 | `}?.let` | Lambda `let`: transforma/usa el receptor dentro de un ámbito corto; con llamada segura suele ejecutarse solo cuando el receptor no es nulo. |
| 145–203 | 1 | `Dispatchers.IO)` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 155–157 | 2 | `if (memoryBitmap != null && cachedMetadata != null)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 159–162 | 2 | `if (cachedBitmap != null && cachedMetadata != null)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 164–194 | 2 | `try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 170–172 | 3 | `val width = if (rotation == 90 \|\| rotation == 270)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 172–174 | 3 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 175–177 | 3 | `val height = if (rotation == 90 \|\| rotation == 270)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 177–179 | 3 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 182–187 | 3 | `if (bitmap != null)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 194–197 | 2 | `} catch (e: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 197–202 | 2 | `} finally` | Bloque de limpieza garantizada: se ejecuta después del intento tanto si hubo éxito como si hubo excepción. |
| 198–200 | 3 | `try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 200–201 | 3 | `} catch (ignored: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 210–270 | 1 | `withContext(Dispatchers.IO)` | Cambio de contexto de corrutina: el cuerpo se ejecuta bajo el dispatcher/contexto indicado y devuelve el resultado al contexto llamador. |
| 219–224 | 2 | `val cachedBitmap = if (loadAlbumArt)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 220–223 | 3 | `bitmapMemoryCache.get(memoryKey)?: decodeCachedBitmap(imageFile)?.also` | Lambda `also`: ejecuta una acción auxiliar sobre el valor y conserva el valor original como resultado de la expresión. |
| 224–226 | 2 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 227–229 | 2 | `if (cachedMetadata != null && (!loadAlbumArt \|\| cachedBitmap != null \|\| imageFile.exists()))` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 231–261 | 2 | `try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 236–256 | 3 | `if (loadAlbumArt)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 238–253 | 4 | `if (embeddedPicture != null)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 241–246 | 5 | `if (bitmap != null)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 246–252 | 5 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 253–255 | 4 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 261–264 | 2 | `} catch (e: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 264–269 | 2 | `} finally` | Bloque de limpieza garantizada: se ejecuta después del intento tanto si hubo éxito como si hubo excepción. |
| 265–267 | 3 | `try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 267–268 | 3 | `} catch (ignored: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 276–299 | 1 | `suspend fun loadDocxPreview(context: Context, uri: Uri, maxCharacters: Int): String? = withContext(Dispatchers.IO)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 279–288 | 2 | `if (cacheFile.exists() && cacheFile.length() > 0L)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 280–282 | 3 | `val cached = try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 282–284 | 3 | `} catch (e: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 285–287 | 3 | `if (!cached.isNullOrBlank())` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 292–297 | 2 | `if (!preview.isNullOrBlank())` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 293–295 | 3 | `try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 295–296 | 3 | `} catch (ignored: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 305–366 | 1 | `suspend fun loadPdfFirstPage(context: Context, uri: Uri, performanceMode: String = "balanced"): Bitmap? = withContext(Dispatchers.IO)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 312–315 | 2 | `val cached = bitmapMemoryCache.get(memoryKey)?: decodeCachedBitmap(cacheFile)?.also` | Lambda `also`: ejecuta una acción auxiliar sobre el valor y conserva el valor original como resultado de la expresión. |
| 316–318 | 2 | `if (cached != null)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 325–349 | 2 | `try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 326–329 | 3 | `descriptor = if (uri.scheme == "file")` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 329–331 | 3 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 334–336 | 3 | `if (renderer.pageCount <= 0)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 349–352 | 2 | `} catch (e: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 352–365 | 2 | `} finally` | Bloque de limpieza garantizada: se ejecuta después del intento tanto si hubo éxito como si hubo excepción. |
| 353–355 | 3 | `try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 355–356 | 3 | `} catch (ignored: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 357–359 | 3 | `try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 359–360 | 3 | `} catch (ignored: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 361–363 | 3 | `try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 363–364 | 3 | `} catch (ignored: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 372–396 | 1 | `suspend fun prewarm(context: Context, uri: Uri, type: String, name: String?, performanceMode: String = "balanced")` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 373–395 | 2 | `withPreviewPermit` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 374–394 | 3 | `when (type)` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 383–393 | 4 | `"file" ->` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 385–392 | 5 | `when (extension)` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 402–438 | 1 | `suspend fun invalidate(context: Context, uri: Uri) = withContext(Dispatchers.IO)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 411–415 | 2 | `listOf("performance", "balanced", "quality").forEach` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 421–437 | 2 | `listOf("mynotes_perf_v2", "mynotes").forEach` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 423–436 | 3 | `listOf("image_previews", "video_previews", "audio_previews", "docx_previews", "pdf_previews").forEach` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 426–429 | 4 | `directory.listFiles()?.filter` | Filtro de colección: conserva solo los elementos cuyo predicado devuelve verdadero. |
| 429–435 | 4 | `}?.forEach` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 431–433 | 5 | `try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 433–434 | 5 | `} catch (ignored: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 444–450 | 1 | `private fun cacheDirectory(context: Context, name: String): File` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 446–448 | 2 | `if (!directory.exists())` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 451–456 | 1 | `private fun cacheKey(uri: Uri): String` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 453–455 | 2 | `return digest.joinToString(separator = "")` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 457–464 | 1 | `private fun setRetrieverDataSource(context: Context, retriever: MediaMetadataRetriever, uri: Uri)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 458–461 | 2 | `if (uri.scheme == "file")` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 461–463 | 2 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 466–513 | 1 | `maxWidth: Int, maxHeight: Int): Bitmap?` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 482–500 | 2 | `Build.VERSION_CODES.O_MR1 && uri.scheme == "file")` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 484–499 | 3 | `if (!path.isNullOrBlank())` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 485–491 | 4 | `try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 488–490 | 5 | `if (legacyThumbnail != null)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 491–496 | 4 | `} catch (_: OutOfMemoryError)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 496–498 | 4 | `} catch (_: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 501–508 | 2 | `return try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 502–504 | 3 | `if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 504–507 | 3 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 508–510 | 2 | `} catch (_: OutOfMemoryError)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 510–512 | 2 | `} catch (_: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 514–545 | 1 | `private fun decodeImageThumbnail(context: Context, uri: Uri, maxWidth: Int, maxHeight: Int): Bitmap?` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 515–540 | 2 | `return try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 516–518 | 3 | `val bounds = BitmapFactory.Options().apply` | Lambda `apply`: configura el receptor y devuelve ese mismo receptor; útil para inicialización encadenada sin perder la instancia. |
| 519–522 | 3 | `openUriInputStream(context = context, uri = uri)?.use` | Bloque `use`: el recurso cerrable se libera automáticamente al terminar el cuerpo, incluso si ocurre una excepción. |
| 523–525 | 3 | `if (bounds.outWidth <= 0 \|\| bounds.outHeight <= 0)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 527–529 | 3 | `while (bounds.outWidth / sample > maxWidth * 2 \|\| bounds.outHeight / sample > maxHeight * 2)` | Bucle `while`: se repite mientras la condición siga siendo verdadera; el cuerpo debe producir progreso para evitar un ciclo infinito. |
| 530–533 | 3 | `val options = BitmapFactory.Options().apply` | Lambda `apply`: configura el receptor y devuelve ese mismo receptor; útil para inicialización encadenada sin perder la instancia. |
| 534–537 | 3 | `val decoded = openUriInputStream(context = context, uri = uri)?.use` | Bloque `use`: el recurso cerrable se libera automáticamente al terminar el cuerpo, incluso si ocurre una excepción. |
| 540–542 | 2 | `} catch (_: OutOfMemoryError)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 542–544 | 2 | `} catch (_: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 546–553 | 1 | `private fun openUriInputStream(context: Context, uri: Uri): InputStream?` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 547–550 | 2 | `return if (uri.scheme == "file")` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 550–552 | 2 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 554–599 | 1 | `private fun applyExifOrientation(context: Context, uri: Uri, bitmap: Bitmap): Bitmap` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 555–565 | 2 | `val orientation = try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 556–559 | 3 | `if (uri.scheme == "file")` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 559–564 | 3 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 560–563 | 4 | `openUriInputStream(context = context, uri = uri)?.use` | Bloque `use`: el recurso cerrable se libera automáticamente al terminar el cuerpo, incluso si ocurre una excepción. |
| 565–567 | 2 | `} catch (_: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 568–570 | 2 | `if (orientation == ExifInterface.ORIENTATION_NORMAL \|\| orientation == ExifInterface.ORIENTATION_UNDEFINED)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 572–587 | 2 | `when (orientation)` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 576–579 | 3 | `ExifInterface.ORIENTATION_TRANSPOSE ->` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 581–584 | 3 | `ExifInterface.ORIENTATION_TRANSVERSE ->` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 588–594 | 2 | `return try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 590–592 | 3 | `if (transformed !== bitmap)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 594–596 | 2 | `} catch (_: OutOfMemoryError)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 596–598 | 2 | `} catch (_: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 600–613 | 1 | `private fun scalePreviewBitmap(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 601–603 | 2 | `if (bitmap.width <= maxWidth && bitmap.height <= maxHeight)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 609–611 | 2 | `if (scaled !== bitmap)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 614–626 | 1 | `private fun saveJpeg(bitmap: Bitmap, destination: File, quality: Int)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 615–620 | 2 | `try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 616–619 | 3 | `destination.outputStream().buffered().use` | Bloque `use`: el recurso cerrable se libera automáticamente al terminar el cuerpo, incluso si ocurre una excepción. |
| 620–625 | 2 | `} catch (e: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 621–623 | 3 | `try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 623–624 | 3 | `} catch (ignored: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 627–639 | 1 | `private fun decodeCachedBitmap(file: File): Bitmap?` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 628–630 | 2 | `if (!file.exists() \|\| file.length() <= 0L)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 631–636 | 2 | `return try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 632–634 | 3 | `val options = BitmapFactory.Options().apply` | Lambda `apply`: configura el receptor y devuelve ese mismo receptor; útil para inicialización encadenada sin perder la instancia. |
| 636–638 | 2 | `} catch (e: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 640–656 | 1 | `private fun decodeSampledBitmap(bytes: ByteArray, requestedSize: Int): Bitmap?` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 641–643 | 2 | `val bounds = BitmapFactory.Options().apply` | Lambda `apply`: configura el receptor y devuelve ese mismo receptor; útil para inicialización encadenada sin perder la instancia. |
| 648–650 | 2 | `requestedSize * 2)` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 651–654 | 2 | `val options = BitmapFactory.Options().apply` | Lambda `apply`: configura el receptor y devuelve ese mismo receptor; útil para inicialización encadenada sin perder la instancia. |
| 657–669 | 1 | `private fun readMediaMetadata(file: File): MediaPreview?` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 658–660 | 2 | `if (!file.exists() \|\| file.length() <= 0L)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 661–666 | 2 | `return try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 666–668 | 2 | `} catch (e: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 670–675 | 1 | `private fun writeMediaMetadata(file: File, preview: MediaPreview)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 671–673 | 2 | `try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 673–674 | 2 | `} catch (ignored: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 676–734 | 1 | `private fun readDocxPreviewStreaming(context: Context, uri: Uri, maxCharacters: Int): String?` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 677–730 | 2 | `return try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 678–681 | 3 | `val source = if (uri.scheme == "file")` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 681–683 | 3 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 684–728 | 3 | `source.use` | Bloque `use`: el recurso cerrable se libera automáticamente al terminar el cuerpo, incluso si ocurre una excepción. |
| 686–727 | 4 | `ZipInputStream(BufferedInputStream(input)).use` | Bloque `use`: el recurso cerrable se libera automáticamente al terminar el cuerpo, incluso si ocurre una excepción. |
| 689–726 | 5 | `while (entry != null)` | Bucle `while`: se repite mientras la condición siga siendo verdadera; el cuerpo debe producir progreso para evitar un ciclo infinito. |
| 690–723 | 6 | `if (entry.name == "word/document.xml")` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 696–716 | 7 | `maxCharacters)` | Ámbito delimitado por llaves en profundidad 7. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 697–714 | 8 | `when (event)` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 698–708 | 9 | `XmlPullParser.START_TAG ->` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 699–707 | 10 | `if (parser.name == "t")` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 701–706 | 11 | `if (text.isNotBlank())` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 702–704 | 12 | `if (result.isNotEmpty() && !result.endsWith(" ") && !result.endsWith("\n"))` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 709–713 | 9 | `XmlPullParser.END_TAG ->` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 710–712 | 10 | `if (parser.name == "p" && result.isNotEmpty() && !result.endsWith("\n"))` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 718–720 | 7 | `return if (clean.isBlank())` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 720–722 | 7 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 730–733 | 2 | `} catch (e: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 735–743 | 1 | `private fun trimPreview(text: String?, maxCharacters: Int): String?` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 736–738 | 2 | `if (text.isNullOrBlank())` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 739–741 | 2 | `if (text.length <= maxCharacters)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |

## 7. Lectura de seguridad antes de tocar este archivo

La documentación describe **lo que el código actual ya hace**. No constituye una propuesta de refactorización. Si se quisiera cambiar algo en el futuro, primero habría que preservar contratos públicos, claves de configuración, restricciones de API Android, comportamiento de Compose, lifecycle, operaciones de I/O y compatibilidad con los datos ya persistidos. En esta entrega no se hizo ninguna de esas modificaciones.

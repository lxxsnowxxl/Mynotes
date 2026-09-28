# WidgetMediaPreview.kt — explicación completa del código

**Ruta:** `app/src/main/java/com/example/mynotes/widget/WidgetMediaPreview.kt`  
**SHA-256:** `ce79e30686a95596cddb2bdc10100bba9e11a44cee318ba5349ca99a35cde675`  
**Líneas:** 143  
**Package:** `com.example.mynotes.widget`

## 1. Para qué existe este archivo

Decodificación ligera de miniaturas para widgets sin alterar el pipeline principal de adjuntos.

## 2. Tipos/clases declarados

- Línea **17** — `object WidgetMediaPreview`.

## 3. Estado, constantes y valores importantes

- **`normalizedUrl`** (línea 35) inicia con `LinkPreviewRepository.normalizeUrl(note.content`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`cachedFile`** (línea 39) inicia con `cachedLinkPreviewFile(context.applicationContext`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`key`** (línea 47) inicia con `sha256(normalizedUrl`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`imageFile`** (línea 48) inicia con `File(File(context.filesDir`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`digest`** (línea 53) inicia con `MessageDigest.getInstance("SHA-256"`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`retriever`** (línea 70) inicia con `MediaMetadataRetriever(`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`raw`** (línea 76) inicia con `retriever.getFrameAtTime(0`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`path`** (línea 90) inicia con `uri.path ?: return null`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`file`** (línea 91) inicia con `File(path`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`bounds`** (línea 93) inicia con `BitmapFactory.Options(`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`options`** (línea 95) inicia con `BitmapFactory.Options(`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`scale`** (línea 116) inicia con `minOf(reqWidth.toFloat(`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`scaledWidth`** (línea 117) inicia con `(source.width * scale`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`scaledHeight`** (línea 118) inicia con `(source.height * scale`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`scaled`** (línea 119) inicia con `Bitmap.createScaledBitmap(source`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`output`** (línea 120) inicia con `Bitmap.createBitmap(reqWidth`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`canvas`** (línea 121) inicia con `Canvas(output`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`left`** (línea 122) inicia con `((reqWidth - scaledWidth`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`top`** (línea 123) inicia con `((reqHeight - scaledHeight`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`height`** (línea 130) inicia con `options.outHeight`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`width`** (línea 131) inicia con `options.outWidth`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`inSampleSize`** (línea 132) inicia con `1`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`halfHeight`** (línea 134) inicia con `height / 2`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`halfWidth`** (línea 135) inicia con `width / 2`. Su valor se usa dentro de la responsabilidad descrita para este archivo.

## 4. Funciones y flujo, una por una

### `firstVisualByNote` — líneas 19–25

**Firma:** `fun firstVisualByNote(attachments: List<Attachment>): Map<Int, Attachment>`

Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.

**Entradas:**
- `attachments: List<Attachment>`

**Salida:** Map<Int, Attachment>.

**Operaciones/funciones que coordina:** `asSequence`, `isNotBlank`, `first`.

### `loadBestPreviewBitmap` — líneas 27–44

**Firma:** `fun loadBestPreviewBitmap( context: Context, note: Note, attachment: Attachment?, reqWidth: Int = 160, reqHeight: Int = 120 ): Bitmap?`

Carga la información solicitada. El cuerpo intenta reutilizar datos disponibles y realiza I/O/decodificación sólo cuando es necesario.

**Entradas:**
- `context: Context`
- `note: Note`
- `attachment: Attachment?`
- `reqWidth: Int = 160`
- `reqHeight: Int = 120`

**Salida:** Bitmap?.

**Efectos/APIs observados en el cuerpo:**
- Procesa imágenes/bitmaps.
- Accede al sistema de archivos interno/cache.
- Lee/escribe Uris mediante ContentResolver.

**Decisiones y protecciones visibles:**
- Contiene manejo de fallos/excepciones y una ruta de recuperación/fallback.
- Usa operadores Elvis/fallback para datos nulos o ausentes.

**Operaciones/funciones que coordina:** `loadPreviewBitmap`, `normalizeUrl`, `cachedLinkPreviewFile`, `decodeSampledBitmap`, `fromFile`, `getOrNull`.

### `cachedLinkPreviewFile` — líneas 46–50

**Firma:** `private fun cachedLinkPreviewFile(context: Context, normalizedUrl: String): File?`

Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.

**Entradas:**
- `context: Context`
- `normalizedUrl: String`

**Salida:** File?.

**Efectos/APIs observados en el cuerpo:**
- Accede al sistema de archivos interno/cache.

**Operaciones/funciones que coordina:** `sha256`, `File`, `length`.

### `sha256` — líneas 52–57

**Firma:** `private fun sha256(text: String): String`

Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.

**Entradas:**
- `text: String`

**Salida:** String.

**Operaciones/funciones que coordina:** `getInstance`, `digest`, `toByteArray`, `buildString`, `append`, `format`.

### `loadPreviewBitmap` — líneas 59–67

**Firma:** `fun loadPreviewBitmap(context: Context, attachment: Attachment?, reqWidth: Int = 160, reqHeight: Int = 120): Bitmap?`

Carga una miniatura apta para RemoteViews desde imagen/video o caché local de link preview y la escala sin tocar los archivos originales.

**Entradas:**
- `context: Context`
- `attachment: Attachment?`
- `reqWidth: Int = 160`
- `reqHeight: Int = 120`

**Salida:** Bitmap?.

**Efectos/APIs observados en el cuerpo:**
- Procesa imágenes/bitmaps.
- Lee/escribe Uris mediante ContentResolver.

**Decisiones y protecciones visibles:**
- Contiene manejo de fallos/excepciones y una ruta de recuperación/fallback.
- Usa operadores Elvis/fallback para datos nulos o ausentes.
- Usa `when` para mapear estados/tipos/opciones.

**Operaciones/funciones que coordina:** `loadVideoFrame`, `parse`, `decodeSampledBitmap`, `getOrNull`.

### `loadVideoFrame` — líneas 69–81

**Firma:** `private fun loadVideoFrame(context: Context, uri: Uri, reqWidth: Int, reqHeight: Int): Bitmap?`

Carga la información solicitada. El cuerpo intenta reutilizar datos disponibles y realiza I/O/decodificación sólo cuando es necesario.

**Entradas:**
- `context: Context`
- `uri: Uri`
- `reqWidth: Int`
- `reqHeight: Int`

**Salida:** Bitmap?.

**Efectos/APIs observados en el cuerpo:**
- Extrae metadata o frames multimedia.

**Decisiones y protecciones visibles:**
- Contiene manejo de fallos/excepciones y una ruta de recuperación/fallback.
- Usa operadores Elvis/fallback para datos nulos o ausentes.
- Usa `when` para mapear estados/tipos/opciones.

**Operaciones/funciones que coordina:** `MediaMetadataRetriever`, `setDataSource`, `getFrameAtTime`, `scaleFitInside`, `release`.

### `decodeSampledBitmap` — líneas 83–112

**Firma:** `private fun decodeSampledBitmap( resolver: ContentResolver, uri: Uri, reqWidth: Int, reqHeight: Int ): Bitmap?`

Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.

**Entradas:**
- `resolver: ContentResolver`
- `uri: Uri`
- `reqWidth: Int`
- `reqHeight: Int`

**Salida:** Bitmap?.

**Efectos/APIs observados en el cuerpo:**
- Procesa imágenes/bitmaps.
- Accede al sistema de archivos interno/cache.
- Lee/escribe Uris mediante ContentResolver.

**Decisiones y protecciones visibles:**
- Usa operadores Elvis/fallback para datos nulos o ausentes.
- Contiene decisiones condicionales (`if`) para seleccionar comportamiento.

**Operaciones/funciones que coordina:** `File`, `exists`, `Options`, `decodeFile`, `calculateInSampleSize`, `scaleFitInside`, `openInputStream`, `decodeStream`.

### `scaleFitInside` — líneas 114–127

**Firma:** `private fun scaleFitInside(source: Bitmap, reqWidth: Int, reqHeight: Int): Bitmap`

Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.

**Entradas:**
- `source: Bitmap`
- `reqWidth: Int`
- `reqHeight: Int`

**Salida:** Bitmap.

**Efectos/APIs observados en el cuerpo:**
- Procesa imágenes/bitmaps.

**Decisiones y protecciones visibles:**
- Limita valores con `coerce*` para evitar estados fuera de rango.
- Contiene manejo de fallos/excepciones y una ruta de recuperación/fallback.
- Contiene decisiones condicionales (`if`) para seleccionar comportamiento.

**Operaciones/funciones que coordina:** `minOf`, `toFloat`, `toInt`, `coerceAtLeast`, `createScaledBitmap`, `createBitmap`, `Canvas`, `drawBitmap`, `recycle`.

### `calculateInSampleSize` — líneas 129–141

**Firma:** `private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int`

Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.

**Entradas:**
- `options: BitmapFactory.Options`
- `reqWidth: Int`
- `reqHeight: Int`

**Salida:** Int.

**Decisiones y protecciones visibles:**
- Limita valores con `coerce*` para evitar estados fuera de rango.
- Contiene decisiones condicionales (`if`) para seleccionar comportamiento.

**Operaciones/funciones que coordina:** `coerceAtLeast`.

## 5. Cómo se conecta con el resto de MyNotes

- Usa `com.example.mynotes.data.Attachment`.
- Usa `com.example.mynotes.data.Note`.
- Usa `com.example.mynotes.links.LinkPreviewRepository`.

## 6. Recursos Android que utiliza

- No se detectaron referencias directas `R.*` en este archivo.

## 7. Tecnologías y efectos relevantes

- Procesa imágenes/bitmaps.
- Extrae metadata o frames multimedia.
- Accede al sistema de archivos interno/cache.
- Lee/escribe Uris mediante ContentResolver.

## 8. Lectura práctica del flujo

Una forma útil de seguir este archivo en el depurador es recorrer estas operaciones en este orden aproximado:
1. `firstVisualByNote` — Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.
2. `loadBestPreviewBitmap` — Carga la información solicitada. El cuerpo intenta reutilizar datos disponibles y realiza I/O/decodificación sólo cuando es necesario.
3. `loadPreviewBitmap` — Carga una miniatura apta para RemoteViews desde imagen/video o caché local de link preview y la escala sin tocar los archivos originales.

## 9. Qué no debe romperse al modificarlo

- Evitar aumentar resoluciones/cargas sin considerar memoria y scroll; preservar caché y liberación de recursos.
- Conservar validaciones de Uri/ruta y no confiar en nombres externos sin sanitizar.
- Este archivo toca previews/adjuntos; cualquier cambio debe probar visualización y miniaturas en los perfiles de rendimiento.

## 10. Resumen en lenguaje sencillo

En términos simples: Decodificación ligera de miniaturas para widgets sin alterar el pipeline principal de adjuntos. La sección función por función anterior describe qué entra, qué devuelve y qué efectos produce cada operación detectada en el fuente actual.

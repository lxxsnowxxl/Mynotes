# AttachmentPreviewCache.kt — documentación del código actual
**Ruta real:** `app/src/main/java/com/example/mynotes/performance/AttachmentPreviewCache.kt`  **SHA-256:** `b5e0d23958e75f3e88b21ab5ff8a6b70f66f3b4c7e470a5a1d98d4801a10b65d`  **Líneas:** 819 · **Bytes:** 39530 · **Imports:** 26 · **Declaraciones detectadas:** 25
> Documento generado fuera de `app/` a partir de lectura del código. El fuente es la única fuente de verdad; no se modificó para generar esta documentación.
## 1. Responsabilidad

Caché RAM/disco y generación de previews/miniaturas de adjuntos.
## 2. Package e imports

Package declarado: `com.example.mynotes.performance`.

### Android / Jetpack / Compose

`android.content.Context`, `android.graphics.Bitmap`, `android.graphics.BitmapFactory`, `android.graphics.Matrix`, `android.graphics.pdf.PdfRenderer`, `android.media.ExifInterface`, `android.media.MediaMetadataRetriever`, `android.media.ThumbnailUtils`, `android.net.Uri`, `android.os.Build`, `android.os.ParcelFileDescriptor`, `android.provider.MediaStore`, `android.util.LruCache`, `android.util.Xml`

### Kotlin / Coroutines / Java

`kotlinx.coroutines.Dispatchers`, `kotlinx.coroutines.withContext`, `kotlinx.coroutines.sync.Semaphore`, `kotlinx.coroutines.sync.withPermit`, `java.io.BufferedInputStream`, `java.io.File`, `java.io.FileInputStream`, `java.io.InputStream`, `java.security.MessageDigest`, `java.util.zip.ZipInputStream`, `kotlin.math.max`

### Terceros / otros

`org.xmlpull.v1.XmlPullParser`

## 3. Declaraciones detectadas

| Línea | Tipo | Nombre | Firma/inicio |
|---:|---|---|---|
| 46 | `object` | `AttachmentPreviewCache` | `object AttachmentPreviewCache {` |
| 65 | `class` | `MediaPreview` | `data class MediaPreview(val bitmap: Bitmap? = null, val durationMillis: Long = 0L, val width: Int = 0, val height: Int = 0) {` |
| 84 | `class` | `PreviewProfile` | `private data class PreviewProfile(val cacheTag: String, val imageWidth: Int, val imageHeight: Int, val videoWidth: Int,` |
| 87 | `fun` | `previewProfile` | `private fun previewProfile(performanceMode: String): PreviewProfile = when (performanceMode) {` |
| 135 | `fun` | `memoryBitmap` | `private fun memoryBitmap(memoryKey: String, cacheTag: String): Bitmap? =` |
| 138 | `fun` | `rememberBitmap` | `private fun rememberBitmap(memoryKey: String, cacheTag: String, bitmap: Bitmap) {` |
| 141 | `fun` | `preferredTags` | `private fun preferredTags(performanceMode: String): List<String> = when (performanceMode) {` |
| 151 | `fun` | `peekImagePreview` | `fun peekImagePreview(uri: Uri, performanceMode: String): Bitmap? {` |
| 159 | `fun` | `peekVideoPreview` | `fun peekVideoPreview(uri: Uri, performanceMode: String): MediaPreview? {` |
| 168 | `fun` | `peekPdfPreview` | `fun peekPdfPreview(uri: Uri, performanceMode: String): Bitmap? {` |
| 519 | `fun` | `cacheDirectory` | `private fun cacheDirectory(context: Context, name: String): File {` |
| 526 | `fun` | `cacheKey` | `private fun cacheKey(uri: Uri): String {` |
| 532 | `fun` | `setRetrieverDataSource` | `private fun setRetrieverDataSource(context: Context, retriever: MediaMetadataRetriever, uri: Uri) {` |
| 540 | `fun` | `createVideoFrame` | `private fun createVideoFrame(context: Context, uri: Uri, retriever: MediaMetadataRetriever, sourceWidth: Int, sourceHeight: Int,` |
| 589 | `fun` | `decodeImageThumbnail` | `private fun decodeImageThumbnail(context: Context, uri: Uri, maxWidth: Int, maxHeight: Int): Bitmap? {` |
| 621 | `fun` | `openUriInputStream` | `private fun openUriInputStream(context: Context, uri: Uri): InputStream? {` |
| 629 | `fun` | `applyExifOrientation` | `private fun applyExifOrientation(context: Context, uri: Uri, bitmap: Bitmap): Bitmap {` |
| 675 | `fun` | `scalePreviewBitmap` | `private fun scalePreviewBitmap(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {` |
| 689 | `fun` | `saveJpeg` | `private fun saveJpeg(bitmap: Bitmap, destination: File, quality: Int) {` |
| 702 | `fun` | `decodeCachedBitmap` | `private fun decodeCachedBitmap(file: File): Bitmap? {` |
| 715 | `fun` | `decodeSampledBitmap` | `private fun decodeSampledBitmap(bytes: ByteArray, requestedSize: Int): Bitmap? {` |
| 732 | `fun` | `readMediaMetadata` | `private fun readMediaMetadata(file: File): MediaPreview? {` |
| 745 | `fun` | `writeMediaMetadata` | `private fun writeMediaMetadata(file: File, preview: MediaPreview) {` |
| 751 | `fun` | `readDocxPreviewStreaming` | `private fun readDocxPreviewStreaming(context: Context, uri: Uri, maxCharacters: Int): String? {` |
| 810 | `fun` | `trimPreview` | `private fun trimPreview(text: String?, maxCharacters: Int): String? {` |

## 4. Estado, efectos y límites observables

- **Coroutines:** 18 aparición/apariciones.
- **I/O/red:** 18 aparición/apariciones.
- **try/catch:** 48 aparición/apariciones.
- **coerce*:** 5 aparición/apariciones.
- **safe calls:** 24 aparición/apariciones.

Estas cifras son indicadores de superficie de cambio, no diagnósticos de error. Cualquier modificación debe preservar contratos de persistencia, lifecycle, límites numéricos y nulabilidad visibles en el fuente.

## 5. Dependencias internas directas

No importa directamente otros símbolos `com.example.mynotes.*`.

## 6. Recursos Android referenciados

No se detectaron referencias `R.*` directas.

## 7. Puntos de revisión al modificarlo

- No degradar calidad, rutas persistentes ni cachés de adjuntos/miniaturas sin una prueba explícita.

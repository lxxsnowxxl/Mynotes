# AttachmentPreviewCache.kt

**Ruta:** `app/src/main/java/com/example/mynotes/performance/AttachmentPreviewCache.kt`  
**Paquete:** `com.example.mynotes.performance`  
**Líneas:** 2236 → 744 (66.7% menos)

## Responsabilidad

Sistema central de miniaturas y caché de adjuntos. Genera representaciones ligeras de imágenes, video, audio, PDF y documentos, conserva variantes en memoria/disco y adapta la resolución al perfil de rendimiento seleccionado.

## Papel dentro de la arquitectura

Es una pieza de rendimiento transversal: las tarjetas, el editor y otras vistas solicitan previews aquí en lugar de decodificar repetidamente los archivos originales. También implementa precalentamiento e invalidación.

## Flujo funcional principal

Flujo típico: un composable solicita un preview -> se calcula la variante según el perfil de rendimiento -> se consulta memoria -> se consulta disco -> si falta, se genera desde el archivo fuente -> se almacena para próximas lecturas. `prewarm` adelanta ese trabajo y `invalidate` evita miniaturas obsoletas.

## Dependencias y relaciones

**Dependencias internas de MyNotes:** no importa directamente otras clases del paquete de la app.

**Android/Jetpack:** `android.content.Context`, `android.graphics.Bitmap`, `android.graphics.BitmapFactory`, `android.graphics.Matrix`, `android.graphics.pdf.PdfRenderer`, `android.media.ExifInterface`, `android.media.MediaMetadataRetriever`, `android.media.ThumbnailUtils`, `android.net.Uri`, `android.os.Build`, `android.os.ParcelFileDescriptor`, `android.provider.MediaStore`, `android.util.LruCache`, `android.util.Xml`.

**Bibliotecas externas:** `org.xmlpull.v1.XmlPullParser`.

**Kotlin/Java/corrutinas:** `kotlinx.coroutines.Dispatchers`, `kotlinx.coroutines.withContext`, `kotlinx.coroutines.sync.Semaphore`, `kotlinx.coroutines.sync.withPermit`, `java.io.BufferedInputStream`, `java.io.File`, `java.io.FileInputStream`, `java.io.InputStream`, `java.security.MessageDigest`, `java.util.zip.ZipInputStream`, `kotlin.math.max`.

## Declaraciones importantes detectadas

| Línea | Tipo | Nombre | Firma / declaración | Qué hace |
|---:|---|---|---|---|
| 46 | object | `AttachmentPreviewCache` | `object AttachmentPreviewCache {` | Singleton que centraliza funciones/estado compartido sin crear múltiples instancias. |
| 56 | data class | `MediaPreview` | `data class MediaPreview(val bitmap: Bitmap? = null, val durationMillis: Long = 0L, val width: Int = 0, val height: Int = 0) {` | Modelo de datos que agrupa valores relacionados con esta responsabilidad. |
| 75 | data class | `PreviewProfile` | `private data class PreviewProfile(val cacheTag: String, val imageWidth: Int, val imageHeight: Int, val videoWidth: Int,` | Modelo de datos que agrupa valores relacionados con esta responsabilidad. |
| 78 | fun | `previewProfile` | `private fun previewProfile(performanceMode: String): PreviewProfile = when (performanceMode) {` | Convierte la clave de rendimiento en límites de resolución y una etiqueta de caché independiente para esa calidad. |
| 101 | fun | `sizeOf` | `override fun sizeOf(key: String, value: Bitmap): Int {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 117 | fun | `loadImagePreview` | `suspend fun loadImagePreview(context: Context, uri: Uri, performanceMode: String = "balanced"): Bitmap? = withContext(Dispatchers.IO) {` | Carga una miniatura de imagen usando primero RAM/disco; si no existe, decodifica con muestreo, corrige EXIF, escala y persiste el resultado. |
| 144 | fun | `loadVideoPreview` | `suspend fun loadVideoPreview(context: Context, uri: Uri, performanceMode: String = "balanced"): MediaPreview = withContext(` | Obtiene metadata y un fotograma representativo de video, reutilizando las variantes cacheadas según rendimiento. |
| 209 | fun | `loadAudioPreview` | `suspend fun loadAudioPreview(context: Context, uri: Uri, loadAlbumArt: Boolean, performanceMode: String = "balanced"): MediaPreview =` | Lee duración y, cuando corresponde, carátula embebida del audio con tamaño adaptado al perfil de rendimiento. |
| 276 | fun | `loadDocxPreview` | `suspend fun loadDocxPreview(context: Context, uri: Uri, maxCharacters: Int): String? = withContext(Dispatchers.IO) {` | Extrae una previsualización textual limitada de un DOCX sin convertir el documento completo. |
| 305 | fun | `loadPdfFirstPage` | `suspend fun loadPdfFirstPage(context: Context, uri: Uri, performanceMode: String = "balanced"): Bitmap? = withContext(Dispatchers.IO) {` | Renderiza y cachea la primera página de un PDF a una anchura dependiente del perfil de rendimiento. |
| 372 | fun | `prewarm` | `suspend fun prewarm(context: Context, uri: Uri, type: String, name: String?, performanceMode: String = "balanced") {` | Genera anticipadamente el preview apropiado para reducir trabajo visible cuando la UI necesite mostrarlo. |
| 402 | fun | `invalidate` | `suspend fun invalidate(context: Context, uri: Uri) = withContext(Dispatchers.IO) {` | Borra de memoria y disco las variantes derivadas de un URI para impedir que quede una miniatura obsoleta. |
| 444 | fun | `cacheDirectory` | `private fun cacheDirectory(context: Context, name: String): File {` | Administra una parte del almacenamiento temporal utilizado para acelerar lecturas posteriores. |
| 451 | fun | `cacheKey` | `private fun cacheKey(uri: Uri): String {` | Genera una clave estable para asociar el URI de origen con archivos/entradas de caché. |
| 457 | fun | `setRetrieverDataSource` | `private fun setRetrieverDataSource(context: Context, retriever: MediaMetadataRetriever, uri: Uri) {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 465 | fun | `createVideoFrame` | `private fun createVideoFrame(context: Context, uri: Uri, retriever: MediaMetadataRetriever, sourceWidth: Int, sourceHeight: Int,` | Construye una nueva instancia/recurso a partir de los parámetros recibidos. |
| 514 | fun | `decodeImageThumbnail` | `private fun decodeImageThumbnail(context: Context, uri: Uri, maxWidth: Int, maxHeight: Int): Bitmap? {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 546 | fun | `openUriInputStream` | `private fun openUriInputStream(context: Context, uri: Uri): InputStream? {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 554 | fun | `applyExifOrientation` | `private fun applyExifOrientation(context: Context, uri: Uri, bitmap: Bitmap): Bitmap {` | Lee la orientación EXIF y rota/refleja el bitmap para que la miniatura coincida con la fotografía real. |
| 600 | fun | `scalePreviewBitmap` | `private fun scalePreviewBitmap(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 614 | fun | `saveJpeg` | `private fun saveJpeg(bitmap: Bitmap, destination: File, quality: Int) {` | Guarda el estado o recurso indicado. |
| 627 | fun | `decodeCachedBitmap` | `private fun decodeCachedBitmap(file: File): Bitmap? {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 640 | fun | `decodeSampledBitmap` | `private fun decodeSampledBitmap(bytes: ByteArray, requestedSize: Int): Bitmap? {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 657 | fun | `readMediaMetadata` | `private fun readMediaMetadata(file: File): MediaPreview? {` | Lee datos desde la fuente indicada y los transforma al formato usado internamente. |
| 670 | fun | `writeMediaMetadata` | `private fun writeMediaMetadata(file: File, preview: MediaPreview) {` | Escribe/persiste datos ya preparados en su destino correspondiente. |
| 676 | fun | `readDocxPreviewStreaming` | `private fun readDocxPreviewStreaming(context: Context, uri: Uri, maxCharacters: Int): String? {` | Lee datos desde la fuente indicada y los transforma al formato usado internamente. |
| 735 | fun | `trimPreview` | `private fun trimPreview(text: String?, maxCharacters: Int): String? {` | Limita/recorta el valor al tamaño o contenido permitido. |

## Cómo leer este archivo

1. Empieza por las declaraciones públicas o composables: representan el contrato que usa el resto del proyecto. 2. Después revisa las funciones privadas: normalmente encapsulan normalización, cálculo, rendering o acceso a plataforma. 3. Los comentarios existentes se conservaron durante la compactación y explican decisiones específicas allí donde el código necesita contexto. 4. Consulta las dependencias internas anteriores para seguir el flujo hacia la capa que consume o persiste el resultado.

## Garantía de la compactación

En este archivo no se cambiaron identificadores, literales, operadores, comentarios, llamadas ni orden de tokens. La reducción proviene exclusivamente de compactar espacios/saltos de línea seguros. El proyecto fue comparado mediante el lexer de Kotlin y validado sintácticamente con PSI después del cambio.

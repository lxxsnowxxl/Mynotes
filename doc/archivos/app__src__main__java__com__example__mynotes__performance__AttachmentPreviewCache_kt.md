# AttachmentPreviewCache.kt — documentación exhaustiva actualizada

**Ruta de código real:** `app/src/main/java/com/example/mynotes/performance/AttachmentPreviewCache.kt`  
**SHA-256 actual del archivo, sin modificar:** `b5e0d23958e75f3e88b21ab5ff8a6b70f66f3b4c7e470a5a1d98d4801a10b65d`  
**Líneas del código real:** 819  
**Estado respecto de la documentación anterior:** **archivo modificado desde la instantánea anterior**

> **Garantía:** este documento vive fuera de `app/`. No se insertó ni eliminó código en el fuente para crear esta explicación. Los fragmentos siguientes son copias de lectura.

## 1. Papel del archivo

Subsistema central de caché y generación de miniaturas para imágenes, video, audio, PDF y documentos. Separa trabajo pesado de la UI, usa caché RAM/disco y adapta resolución/concurrencia al perfil de rendimiento.

**Cambios recientes cubiertos por esta revisión.** Los cambios recientes añaden perfiles por rendimiento, variante instantánea para evitar pop-in durante scroll rápido, peeks de memoria, precalentamiento y límites de concurrencia más conservadores en Android 9/API 28.

## 2. Package e imports

El package declarado es `com.example.mynotes.performance`. El package fija el namespace de Kotlin y condiciona cómo se resuelven nombres, visibilidad, imports y referencias desde otros módulos.

El archivo contiene **26 imports**. Se agrupan por responsabilidad:

### Android / Jetpack / Compose

`android.content.Context`, `android.graphics.Bitmap`, `android.graphics.BitmapFactory`, `android.graphics.Matrix`, `android.graphics.pdf.PdfRenderer`, `android.media.ExifInterface`, `android.media.MediaMetadataRetriever`, `android.media.ThumbnailUtils`, `android.net.Uri`, `android.os.Build`, `android.os.ParcelFileDescriptor`, `android.provider.MediaStore`, `android.util.LruCache`, `android.util.Xml`

### Kotlin Coroutines / extensiones

`kotlinx.coroutines.Dispatchers`, `kotlinx.coroutines.withContext`, `kotlinx.coroutines.sync.Semaphore`, `kotlinx.coroutines.sync.withPermit`

### Terceros / otros

`org.xmlpull.v1.XmlPullParser`

### Java / Kotlin estándar

`java.io.BufferedInputStream`, `java.io.File`, `java.io.FileInputStream`, `java.io.InputStream`, `java.security.MessageDigest`, `java.util.zip.ZipInputStream`, `kotlin.math.max`

## 3. Restricciones, límites e invariantes detectables

- **Llamada segura `?.`: 24 aparición/apariciones.** evita desreferenciar receptores nulos; si el receptor es `null`, la cadena se corta de forma segura.
- **Elvis `?:`: 25 aparición/apariciones.** define un fallback explícito cuando el operando izquierdo es nulo.
- **Guardias de API Android: 3 aparición/apariciones.** protegen llamadas cuya disponibilidad cambia según la versión de Android.
- **Acotaciones `coerceIn/AtLeast/AtMost`: 5 aparición/apariciones.** imponen límites numéricos para evitar valores fuera del rango aceptado.
- **Límites visuales: 30 aparición/apariciones.** evitan crecimiento o reducción de UI fuera de los límites previstos.
- **Corrutinas / dispatcher: 24 aparición/apariciones.** separan trabajo concurrente o pesado del hilo que compone/renderiza la interfaz.
- **Estado Compose: 7 aparición/apariciones.** introduce estado observado por Compose y, por tanto, puntos potenciales de recomposición.

Estas apariciones no implican por sí solas un error: son puntos donde el código expresa contratos que deben preservarse al modificarlo.

## 4. Declaraciones y funciones

### 4.1 `AttachmentPreviewCache` — object, líneas 46–819

```kotlin
object AttachmentPreviewCache {
    /*
     * Límite compartido para previews pesados solicitados por la interfaz.
     * Dos trabajos simultáneos mantienen ocupados los núcleos sin provocar
     * una ráfaga de 6-10 decodificaciones al abrir una nota.
     */
    private val legacyPreviewLoadGate = Semaphore(permits = 1)
    private val previewLoadGate = Semaphore(permits = 2)
    suspend fun <T> withPreviewPermit(block: suspend () -> T): T {
        /*
         * En Android 9/API 28 y anteriores, dos decodificaciones pesadas a la
         * vez (BitmapFactory/MediaMetadataRetriever/PdfRenderer) pueden
         * competir de forma visible con el hilo de render y provocar GC más
         * frecuente. Serializamos esos trabajos en equipos legacy; Android
         * moderno conserva dos permisos para mantener buena velocidad.
         */
        val gate = if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) legacyPreviewLoadGate else previewLoadGate
        return gate.withPermit { block() }
    }
    data class MediaPreview(val bitmap: Bitmap? = null, val durationMillis: Long = 0L, val width: Int = 0, val height: Int = 0) {
        val aspectRatio: Float
            get() = if (width > 0 && height > 0) {
                    width.toFloat() / height.toFloat()
                } else {
                    16f / 9f
                }
    }
    private const val DOCX_CACHE_CHARACTERS = 4_000
    /*
     * La resolución de las miniaturas sigue el perfil seleccionado en
     * Configuración > Rendimiento. Cada perfil usa su propia variante de
     * caché, por lo que al cambiar de modo no se reutiliza por accidente una
     * miniatura generada con otra resolución.
     *
     * performance = menos RAM/CPU y archivos de caché más pequeños
     * balanced    = calidad intermedia (equivalente al comportamiento previo)
     * quality     = miniaturas de mayor fidelidad
     */
    private data class PreviewProfile(val cacheTag: String, val imageWidth: Int, val imageHeight: Int, val videoWidth: Int,
        val videoHeight: Int, val audioArtSize: Int, val pdfWidth: Int, val imageJpegQuality: Int, val videoJpegQuality: Int,
        val audioJpegQuality: Int, val pdfJpegQuality: Int)
    private fun previewProfile(performanceMode: String): PreviewProfile = when (performanceMode) {
            /*
             * "instant" no es una cuarta opción visible de Rendimiento. Es una
             * miniatura interna muy pequeña que Maximum quality usa como
             * representación inmediata mientras la variante 1280/1080/1440 se
             * prepara fuera del frame crítico del scroll. Al ser persistente,
             * una nota ya visitada puede volver a mostrar imagen desde el primer
             * frame sin esperar a decodificar el adjunto original.
             */
            "instant" -> PreviewProfile(cacheTag = "instant", imageWidth = 192, imageHeight = 192, videoWidth = 192,
                    videoHeight = 192, audioArtSize = 160, pdfWidth = 256, imageJpegQuality = 72, videoJpegQuality = 74,
                    audioJpegQuality = 70, pdfJpegQuality = 76)
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
     * Las miniaturas instantáneas tienen una caché RAM separada. Si compartieran
     * la LruCache principal, unos pocos bitmaps quality grandes podrían expulsar
     * precisamente las miniaturas pequeñas que necesitamos para que un fling no
     * muestre placeholders. 6 MiB permiten mantener decenas de previews 192 px
     * sin retener las imágenes quality de forma artificial.
     */
    private val instantMemoryCache = object : LruCache<String, Bitmap>(6 * 1024) {
            override fun sizeOf(key: String, value: Bitmap): Int = (value.byteCount / 1024).coerceAtLeast(1)
        }
    private fun memoryBitmap(memoryKey: String, cacheTag: String): Bitmap? =
        if (cacheTag == "instant") instantMemoryCache.get(memoryKey) ?: bitmapMemoryCache.get(memoryKey)
        else bitmapMemoryCache.get(memoryKey)
    private fun rememberBitmap(memoryKey: String, cacheTag: String, bitmap: Bitmap) {
        if (cacheTag == "instant") instantMemoryCache.put(memoryKey, bitmap) else bitmapMemoryCache.put(memoryKey, bitmap)
    }
    private fun preferredTags(performanceMode: String): List<String> = when (performanceMode) {
            "quality" -> listOf("quality", "instant", "balanced", "performance")
            "balanced" -> listOf("balanced", "performance")
            else -> listOf("performance")
        }
    /*
     * Consultas sin I/O. Se usan al componer una tarjeta: si el precalentamiento
     * ya dejó un bitmap en RAM, Compose puede dibujarlo en el primer frame y no
     * existe una transición visual de "archivo -> imagen" durante el scroll.
     */
    fun peekImagePreview(uri: Uri, performanceMode: String): Bitmap? {
        val key = cacheKey(uri)
        return preferredTags(performanceMode).firstNotNullOfOrNull { tag ->
            val memoryKey = "image:$key-$tag"
            if (tag == "instant") instantMemoryCache.get(memoryKey) ?: bitmapMemoryCache.get(memoryKey)
            else bitmapMemoryCache.get(memoryKey)
        }
    }
    fun peekVideoPreview(uri: Uri, performanceMode: String): MediaPreview? {
        val key = cacheKey(uri)
        val bitmap = preferredTags(performanceMode).firstNotNullOfOrNull { tag ->
            val memoryKey = "video:$key-$tag"
            if (tag == "instant") instantMemoryCache.get(memoryKey) ?: bitmapMemoryCache.get(memoryKey)
            else bitmapMemoryCache.get(memoryKey)
        }
        return bitmap?.let { MediaPreview(bitmap = it) }
    }
    fun peekPdfPreview(uri: Uri, performanceMode: String): Bitmap? {
        val key = cacheKey(uri)
        return preferredTags(performanceMode).firstNotNullOfOrNull { tag ->
            val memoryKey = "pdf:$key-$tag"
            if (tag == "instant") instantMemoryCache.get(memoryKey) ?: bitmapMemoryCache.get(memoryKey)
            else bitmapMemoryCache.get(memoryKey)
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
    suspend fun loadImagePreview(context: Context, uri: Uri, performanceMode: String = "balanced"): Bitmap? = withContext(Dispatchers.IO) {
            val directory = cacheDirectory(context, "image_previews")
            val key = cacheKey(uri)
            val profile = previewProfile(performanceMode)
            val profileKey = "$key-${profile.cacheTag}"
            val memoryKey = "image:$profileKey"
            val cacheFile = File(directory, "$profileKey.jpg")
            memoryBitmap(memoryKey, profile.cacheTag)?.let {
                    return@withContext it
                }
            decodeCachedBitmap(cacheFile)?.also {
                        bitmap ->
                    rememberBitmap(memoryKey, profile.cacheTag, bitmap)
                }?.let {
                    return@withContext it
                }
            val bitmap = decodeImageThumbnail(context = context, uri = uri, maxWidth = profile.imageWidth, maxHeight = profile.imageHeight)
                    ?: return@withContext null
            saveJpeg(bitmap = bitmap, destination = cacheFile, quality = profile.imageJpegQuality)
            rememberBitmap(memoryKey, profile.cacheTag, bitmap)
            bitmap
        }
    /*
     * ========================================================
     * VIDEO
     * ========================================================
     */
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
            val memoryBitmap = memoryBitmap(memoryKey, profile.cacheTag)
            if (memoryBitmap != null && cachedMetadata != null) {
                return@withContext cachedMetadata.copy(bitmap = memoryBitmap)
            }
            val cachedBitmap = decodeCachedBitmap(imageFile)
            if (cachedBitmap != null && cachedMetadata != null) {
                rememberBitmap(memoryKey, profile.cacheTag, cachedBitmap)
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
                    rememberBitmap(memoryKey, profile.cacheTag, bitmap)
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
    suspend fun loadPdfFirstPage(context: Context, uri: Uri, performanceMode: String = "balanced"): Bitmap? = withContext(Dispatchers.IO) {
            val directory = cacheDirectory(context, "pdf_previews")
            val key = cacheKey(uri)
            val profile = previewProfile(performanceMode)
            val profileKey = "$key-${profile.cacheTag}"
            val memoryKey = "pdf:$profileKey"
            val cacheFile = File(directory, "$profileKey.jpg")
            val cached = memoryBitmap(memoryKey, profile.cacheTag)?: decodeCachedBitmap(cacheFile)?.also {
                                bitmap ->
                            rememberBitmap(memoryKey, profile.cacheTag, bitmap)
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
                rememberBitmap(memoryKey, profile.cacheTag, bitmap)
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
    suspend fun invalidate(context: Context, uri: Uri) = withContext(Dispatchers.IO) {
            val key = cacheKey(uri)
            /*
             * El mismo adjunto puede tener hasta tres miniaturas distintas
             * (performance / balanced / quality). Eliminamos todas las
             * variantes de RAM para que un adjunto borrado no deje bitmaps
             * retenidos hasta que el LruCache los expulse por presión de
             * memoria.
             */
            listOf("instant", "performance", "balanced", "quality").forEach { profile ->
                bitmapMemoryCache.remove("image:$key-$profile")
                bitmapMemoryCache.remove("video:$key-$profile")
                bitmapMemoryCache.remove("audio:$key-$profile")
                bitmapMemoryCache.remove("pdf:$key-$profile")
                instantMemoryCache.remove("image:$key-$profile")
                instantMemoryCache.remove("video:$key-$profile")
                instantMemoryCache.remove("pdf:$key-$profile")
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
    private fun cacheDirectory(context: Context, name: String): File {
        val directory = File(context.cacheDir, "mynotes_perf_v2/$name")
        if (!directory.exists()) {
            directory.mkdirs()
        }
        return directory
    }
    private fun cacheKey(uri: Uri): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(uri.toString().toByteArray(Charsets.UTF_8))
        return digest.joinToString(separator = "") {
                byte -> "%02x".format(byte)
            }
    }
    private fun setRetrieverDataSource(context: Context, retriever: MediaMetadataRetriever, uri: Uri) {
        if (uri.scheme == "file") {
            val path = uri.path?: throw IllegalArgumentException("URI de archivo inválida")
            retriever.setDataSource(path)
        } else {
            retriever.setDataSource(context, uri)
        }
    }
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
    private fun openUriInputStream(context: Context, uri: Uri): InputStream? {
        return if (uri.scheme == "file") {
            val path = uri.path?: return null
            FileInputStream(File(path))
        } else {
            context.contentResolver.openInputStream(uri)
        }
    }
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
    private fun writeMediaMetadata(file: File, preview: MediaPreview) {
        try {
            file.writeText("${preview.durationMillis}," + "${preview.width}," + "${preview.height}")
        } catch (ignored: Exception) {
        }
    }
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
    private fun trimPreview(text: String?, maxCharacters: Int): String? {
        if (text.isNullOrBlank()) {
            return null
        }
        if (text.length <= maxCharacters) {
            return text
        }
        return text.take(maxCharacters) + "…"
    }
}
```

**Firma/entrada.** `object AttachmentPreviewCache {`

**Funcionamiento observable.** participa en la UI declarativa Compose y puede recomponerse al cambiar el estado que lee; desplaza I/O/decodificación fuera del hilo principal; accede o prepara almacenamiento local/caché; observa insets/IME y por ello depende del estado de la ventana; integra el pipeline de miniaturas y caché de adjuntos; ramifica o parametriza comportamiento según el perfil de rendimiento.

**Puntos que no conviene romper:** contiene retornos explícitos; algunos caminos pueden terminar antes de ejecutar el resto del bloque; incluye manejo de excepciones; la ruta de error forma parte del contrato, no es un detalle decorativo; captura estado/objetos de composición; sus claves determinan cuándo se reutilizan o reconstruyen; acota valores antes de usarlos para proteger rangos de UI/rendimiento; la concurrencia está deliberadamente limitada para evitar saturación.

### 4.2 `<anónimo>` — fun, líneas 54–64

```kotlin
    suspend fun <T> withPreviewPermit(block: suspend () -> T): T {
        /*
         * En Android 9/API 28 y anteriores, dos decodificaciones pesadas a la
         * vez (BitmapFactory/MediaMetadataRetriever/PdfRenderer) pueden
         * competir de forma visible con el hilo de render y provocar GC más
         * frecuente. Serializamos esos trabajos en equipos legacy; Android
         * moderno conserva dos permisos para mantener buena velocidad.
         */
        val gate = if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) legacyPreviewLoadGate else previewLoadGate
        return gate.withPermit { block() }
    }
```

**Firma/entrada.** `suspend fun <T> withPreviewPermit(block: suspend () -> T): T {`

**Parámetros.**
- `block: suspend () -> T` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.

**Funcionamiento observable.** encapsula una operación local cuyo contrato se deriva de sus parámetros, retorno y llamadas internas.

**Puntos que no conviene romper:** contiene retornos explícitos; algunos caminos pueden terminar antes de ejecutar el resto del bloque; la concurrencia está deliberadamente limitada para evitar saturación.

### 4.3 `MediaPreview` — class, líneas 65–72

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

**Firma/entrada.** `data class MediaPreview(val bitmap: Bitmap? = null, val durationMillis: Long = 0L, val width: Int = 0, val height: Int = 0) {`

**Parámetros.**
- `val bitmap: Bitmap? = null` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente. Su tipo admite `null`, por lo que el cuerpo debe contemplar ausencia. Tiene valor por defecto y puede omitirse en la llamada.
- `val durationMillis: Long = 0L` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente. Tiene valor por defecto y puede omitirse en la llamada.
- `val width: Int = 0` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente. Tiene valor por defecto y puede omitirse en la llamada.
- `val height: Int = 0` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente. Tiene valor por defecto y puede omitirse en la llamada.

**Funcionamiento observable.** encapsula una operación local cuyo contrato se deriva de sus parámetros, retorno y llamadas internas.

### 4.4 `PreviewProfile` — class, líneas 84–108

```kotlin
    private data class PreviewProfile(val cacheTag: String, val imageWidth: Int, val imageHeight: Int, val videoWidth: Int,
        val videoHeight: Int, val audioArtSize: Int, val pdfWidth: Int, val imageJpegQuality: Int, val videoJpegQuality: Int,
        val audioJpegQuality: Int, val pdfJpegQuality: Int)
    private fun previewProfile(performanceMode: String): PreviewProfile = when (performanceMode) {
            /*
             * "instant" no es una cuarta opción visible de Rendimiento. Es una
             * miniatura interna muy pequeña que Maximum quality usa como
             * representación inmediata mientras la variante 1280/1080/1440 se
             * prepara fuera del frame crítico del scroll. Al ser persistente,
             * una nota ya visitada puede volver a mostrar imagen desde el primer
             * frame sin esperar a decodificar el adjunto original.
             */
            "instant" -> PreviewProfile(cacheTag = "instant", imageWidth = 192, imageHeight = 192, videoWidth = 192,
                    videoHeight = 192, audioArtSize = 160, pdfWidth = 256, imageJpegQuality = 72, videoJpegQuality = 74,
                    audioJpegQuality = 70, pdfJpegQuality = 76)
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
```

**Firma/entrada.** `private data class PreviewProfile(val cacheTag: String, val imageWidth: Int, val imageHeight: Int, val videoWidth: Int, val videoHeight: Int, val audioArtSize: Int, val pdfWidth: Int, val imageJpegQuality: Int, val videoJpegQuality: Int, val audioJpegQuality: Int, val pdfJpegQuality: Int) private fun previewProfile(performanceMode: String): PreviewProfile = when (performanceMode) {`

**Parámetros.**
- `val cacheTag: String` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `val imageWidth: Int` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `val imageHeight: Int` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `val videoWidth: Int` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `val videoHeight: Int` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `val audioArtSize: Int` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `val pdfWidth: Int` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `val imageJpegQuality: Int` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `val videoJpegQuality: Int` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `val audioJpegQuality: Int` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `val pdfJpegQuality: Int) private fun previewProfile(performanceMode: String): PreviewProfile = when (performanceMode` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente. Tiene valor por defecto y puede omitirse en la llamada.

**Funcionamiento observable.** ramifica o parametriza comportamiento según el perfil de rendimiento.

### 4.5 `previewProfile` — fun, líneas 87–108

```kotlin
    private fun previewProfile(performanceMode: String): PreviewProfile = when (performanceMode) {
            /*
             * "instant" no es una cuarta opción visible de Rendimiento. Es una
             * miniatura interna muy pequeña que Maximum quality usa como
             * representación inmediata mientras la variante 1280/1080/1440 se
             * prepara fuera del frame crítico del scroll. Al ser persistente,
             * una nota ya visitada puede volver a mostrar imagen desde el primer
             * frame sin esperar a decodificar el adjunto original.
             */
            "instant" -> PreviewProfile(cacheTag = "instant", imageWidth = 192, imageHeight = 192, videoWidth = 192,
                    videoHeight = 192, audioArtSize = 160, pdfWidth = 256, imageJpegQuality = 72, videoJpegQuality = 74,
                    audioJpegQuality = 70, pdfJpegQuality = 76)
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
```

**Firma/entrada.** `private fun previewProfile(performanceMode: String): PreviewProfile = when (performanceMode) {`

**Parámetros.**
- `performanceMode: String): PreviewProfile = when (performanceMode` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente. Tiene valor por defecto y puede omitirse en la llamada.

**Funcionamiento observable.** ramifica o parametriza comportamiento según el perfil de rendimiento.

### 4.6 `memoryBitmap` — fun, líneas 135–140

```kotlin
    private fun memoryBitmap(memoryKey: String, cacheTag: String): Bitmap? =
        if (cacheTag == "instant") instantMemoryCache.get(memoryKey) ?: bitmapMemoryCache.get(memoryKey)
        else bitmapMemoryCache.get(memoryKey)
    private fun rememberBitmap(memoryKey: String, cacheTag: String, bitmap: Bitmap) {
        if (cacheTag == "instant") instantMemoryCache.put(memoryKey, bitmap) else bitmapMemoryCache.put(memoryKey, bitmap)
    }
```

**Firma/entrada.** `private fun memoryBitmap(memoryKey: String, cacheTag: String): Bitmap? = if (cacheTag == "instant") instantMemoryCache.get(memoryKey) ?: bitmapMemoryCache.get(memoryKey) else bitmapMemoryCache.get(memoryKey) private fun rememberBitmap(memoryKey: String, cacheTag: String, bitmap: Bitmap) {`

**Parámetros.**
- `memoryKey: String` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `cacheTag: String): Bitmap? = if (cacheTag == "instant") instantMemoryCache.get(memoryKey) ?: bitmapMemoryCache.get(memoryKey) else bitmapMemoryCache.get(memoryKey) private fun rememberBitmap(memoryKey: String, cacheTag: String, bitmap: Bitmap` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente. Su tipo admite `null`, por lo que el cuerpo debe contemplar ausencia. Tiene valor por defecto y puede omitirse en la llamada.

**Funcionamiento observable.** participa en la UI declarativa Compose y puede recomponerse al cambiar el estado que lee.

**Puntos que no conviene romper:** captura estado/objetos de composición; sus claves determinan cuándo se reutilizan o reconstruyen.

### 4.7 `rememberBitmap` — fun, líneas 138–140

```kotlin
    private fun rememberBitmap(memoryKey: String, cacheTag: String, bitmap: Bitmap) {
        if (cacheTag == "instant") instantMemoryCache.put(memoryKey, bitmap) else bitmapMemoryCache.put(memoryKey, bitmap)
    }
```

**Firma/entrada.** `private fun rememberBitmap(memoryKey: String, cacheTag: String, bitmap: Bitmap) {`

**Parámetros.**
- `memoryKey: String` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `cacheTag: String` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `bitmap: Bitmap` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.

**Funcionamiento observable.** participa en la UI declarativa Compose y puede recomponerse al cambiar el estado que lee.

**Puntos que no conviene romper:** captura estado/objetos de composición; sus claves determinan cuándo se reutilizan o reconstruyen.

### 4.8 `preferredTags` — fun, líneas 141–145

```kotlin
    private fun preferredTags(performanceMode: String): List<String> = when (performanceMode) {
            "quality" -> listOf("quality", "instant", "balanced", "performance")
            "balanced" -> listOf("balanced", "performance")
            else -> listOf("performance")
        }
```

**Firma/entrada.** `private fun preferredTags(performanceMode: String): List<String> = when (performanceMode) {`

**Parámetros.**
- `performanceMode: String): List<String> = when (performanceMode` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente. Tiene valor por defecto y puede omitirse en la llamada.

**Funcionamiento observable.** ramifica o parametriza comportamiento según el perfil de rendimiento.

### 4.9 `peekImagePreview` — fun, líneas 151–158

```kotlin
    fun peekImagePreview(uri: Uri, performanceMode: String): Bitmap? {
        val key = cacheKey(uri)
        return preferredTags(performanceMode).firstNotNullOfOrNull { tag ->
            val memoryKey = "image:$key-$tag"
            if (tag == "instant") instantMemoryCache.get(memoryKey) ?: bitmapMemoryCache.get(memoryKey)
            else bitmapMemoryCache.get(memoryKey)
        }
    }
```

**Firma/entrada.** `fun peekImagePreview(uri: Uri, performanceMode: String): Bitmap? {`

**Parámetros.**
- `uri: Uri` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `performanceMode: String` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.

**Funcionamiento observable.** ramifica o parametriza comportamiento según el perfil de rendimiento.

**Puntos que no conviene romper:** contiene retornos explícitos; algunos caminos pueden terminar antes de ejecutar el resto del bloque.

### 4.10 `peekVideoPreview` — fun, líneas 159–167

```kotlin
    fun peekVideoPreview(uri: Uri, performanceMode: String): MediaPreview? {
        val key = cacheKey(uri)
        val bitmap = preferredTags(performanceMode).firstNotNullOfOrNull { tag ->
            val memoryKey = "video:$key-$tag"
            if (tag == "instant") instantMemoryCache.get(memoryKey) ?: bitmapMemoryCache.get(memoryKey)
            else bitmapMemoryCache.get(memoryKey)
        }
        return bitmap?.let { MediaPreview(bitmap = it) }
    }
```

**Firma/entrada.** `fun peekVideoPreview(uri: Uri, performanceMode: String): MediaPreview? {`

**Parámetros.**
- `uri: Uri` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `performanceMode: String` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.

**Funcionamiento observable.** ramifica o parametriza comportamiento según el perfil de rendimiento.

**Puntos que no conviene romper:** contiene retornos explícitos; algunos caminos pueden terminar antes de ejecutar el resto del bloque.

### 4.11 `peekPdfPreview` — fun, líneas 168–175

```kotlin
    fun peekPdfPreview(uri: Uri, performanceMode: String): Bitmap? {
        val key = cacheKey(uri)
        return preferredTags(performanceMode).firstNotNullOfOrNull { tag ->
            val memoryKey = "pdf:$key-$tag"
            if (tag == "instant") instantMemoryCache.get(memoryKey) ?: bitmapMemoryCache.get(memoryKey)
            else bitmapMemoryCache.get(memoryKey)
        }
    }
```

**Firma/entrada.** `fun peekPdfPreview(uri: Uri, performanceMode: String): Bitmap? {`

**Parámetros.**
- `uri: Uri` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `performanceMode: String` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.

**Funcionamiento observable.** ramifica o parametriza comportamiento según el perfil de rendimiento.

**Puntos que no conviene romper:** contiene retornos explícitos; algunos caminos pueden terminar antes de ejecutar el resto del bloque.

### 4.12 `loadImagePreview` — fun, líneas 188–209

```kotlin
    suspend fun loadImagePreview(context: Context, uri: Uri, performanceMode: String = "balanced"): Bitmap? = withContext(Dispatchers.IO) {
            val directory = cacheDirectory(context, "image_previews")
            val key = cacheKey(uri)
            val profile = previewProfile(performanceMode)
            val profileKey = "$key-${profile.cacheTag}"
            val memoryKey = "image:$profileKey"
            val cacheFile = File(directory, "$profileKey.jpg")
            memoryBitmap(memoryKey, profile.cacheTag)?.let {
                    return@withContext it
                }
            decodeCachedBitmap(cacheFile)?.also {
                        bitmap ->
                    rememberBitmap(memoryKey, profile.cacheTag, bitmap)
                }?.let {
                    return@withContext it
                }
            val bitmap = decodeImageThumbnail(context = context, uri = uri, maxWidth = profile.imageWidth, maxHeight = profile.imageHeight)
                    ?: return@withContext null
            saveJpeg(bitmap = bitmap, destination = cacheFile, quality = profile.imageJpegQuality)
            rememberBitmap(memoryKey, profile.cacheTag, bitmap)
            bitmap
        }
```

**Firma/entrada.** `suspend fun loadImagePreview(context: Context, uri: Uri, performanceMode: String = "balanced"): Bitmap? = withContext(Dispatchers.IO) {`

**Parámetros.**
- `context: Context` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `uri: Uri` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `performanceMode: String = "balanced"): Bitmap? = withContext(Dispatchers.IO` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente. Su tipo admite `null`, por lo que el cuerpo debe contemplar ausencia. Tiene valor por defecto y puede omitirse en la llamada.

**Funcionamiento observable.** participa en la UI declarativa Compose y puede recomponerse al cambiar el estado que lee; desplaza I/O/decodificación fuera del hilo principal; accede o prepara almacenamiento local/caché; ramifica o parametriza comportamiento según el perfil de rendimiento.

**Puntos que no conviene romper:** contiene retornos explícitos; algunos caminos pueden terminar antes de ejecutar el resto del bloque; captura estado/objetos de composición; sus claves determinan cuándo se reutilizan o reconstruyen.

### 4.13 `loadVideoPreview` — fun, líneas 215–274

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
            val memoryBitmap = memoryBitmap(memoryKey, profile.cacheTag)
            if (memoryBitmap != null && cachedMetadata != null) {
                return@withContext cachedMetadata.copy(bitmap = memoryBitmap)
            }
            val cachedBitmap = decodeCachedBitmap(imageFile)
            if (cachedBitmap != null && cachedMetadata != null) {
                rememberBitmap(memoryKey, profile.cacheTag, cachedBitmap)
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
                    rememberBitmap(memoryKey, profile.cacheTag, bitmap)
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
```

**Firma/entrada.** `suspend fun loadVideoPreview(context: Context, uri: Uri, performanceMode: String = "balanced"): MediaPreview = withContext( Dispatchers.IO) {`

**Parámetros.**
- `context: Context` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `uri: Uri` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `performanceMode: String = "balanced"): MediaPreview = withContext( Dispatchers.IO` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente. Tiene valor por defecto y puede omitirse en la llamada.

**Funcionamiento observable.** participa en la UI declarativa Compose y puede recomponerse al cambiar el estado que lee; desplaza I/O/decodificación fuera del hilo principal; accede o prepara almacenamiento local/caché; ramifica o parametriza comportamiento según el perfil de rendimiento.

**Puntos que no conviene romper:** contiene retornos explícitos; algunos caminos pueden terminar antes de ejecutar el resto del bloque; incluye manejo de excepciones; la ruta de error forma parte del contrato, no es un detalle decorativo; captura estado/objetos de composición; sus claves determinan cuándo se reutilizan o reconstruyen.

### 4.14 `loadAudioPreview` — fun, líneas 280–341

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
```

**Firma/entrada.** `suspend fun loadAudioPreview(context: Context, uri: Uri, loadAlbumArt: Boolean, performanceMode: String = "balanced"): MediaPreview = withContext(Dispatchers.IO) {`

**Parámetros.**
- `context: Context` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `uri: Uri` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `loadAlbumArt: Boolean` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `performanceMode: String = "balanced"): MediaPreview = withContext(Dispatchers.IO` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente. Tiene valor por defecto y puede omitirse en la llamada.

**Funcionamiento observable.** desplaza I/O/decodificación fuera del hilo principal; accede o prepara almacenamiento local/caché; ramifica o parametriza comportamiento según el perfil de rendimiento.

**Puntos que no conviene romper:** contiene retornos explícitos; algunos caminos pueden terminar antes de ejecutar el resto del bloque; incluye manejo de excepciones; la ruta de error forma parte del contrato, no es un detalle decorativo.

### 4.15 `loadDocxPreview` — fun, líneas 347–370

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
```

**Firma/entrada.** `suspend fun loadDocxPreview(context: Context, uri: Uri, maxCharacters: Int): String? = withContext(Dispatchers.IO) {`

**Parámetros.**
- `context: Context` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `uri: Uri` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `maxCharacters: Int): String? = withContext(Dispatchers.IO` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente. Su tipo admite `null`, por lo que el cuerpo debe contemplar ausencia. Tiene valor por defecto y puede omitirse en la llamada.

**Funcionamiento observable.** desplaza I/O/decodificación fuera del hilo principal; accede o prepara almacenamiento local/caché.

**Puntos que no conviene romper:** contiene retornos explícitos; algunos caminos pueden terminar antes de ejecutar el resto del bloque; incluye manejo de excepciones; la ruta de error forma parte del contrato, no es un detalle decorativo.

### 4.16 `loadPdfFirstPage` — fun, líneas 376–437

```kotlin
    suspend fun loadPdfFirstPage(context: Context, uri: Uri, performanceMode: String = "balanced"): Bitmap? = withContext(Dispatchers.IO) {
            val directory = cacheDirectory(context, "pdf_previews")
            val key = cacheKey(uri)
            val profile = previewProfile(performanceMode)
            val profileKey = "$key-${profile.cacheTag}"
            val memoryKey = "pdf:$profileKey"
            val cacheFile = File(directory, "$profileKey.jpg")
            val cached = memoryBitmap(memoryKey, profile.cacheTag)?: decodeCachedBitmap(cacheFile)?.also {
                                bitmap ->
                            rememberBitmap(memoryKey, profile.cacheTag, bitmap)
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
                rememberBitmap(memoryKey, profile.cacheTag, bitmap)
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
```

**Firma/entrada.** `suspend fun loadPdfFirstPage(context: Context, uri: Uri, performanceMode: String = "balanced"): Bitmap? = withContext(Dispatchers.IO) {`

**Parámetros.**
- `context: Context` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `uri: Uri` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `performanceMode: String = "balanced"): Bitmap? = withContext(Dispatchers.IO` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente. Su tipo admite `null`, por lo que el cuerpo debe contemplar ausencia. Tiene valor por defecto y puede omitirse en la llamada.

**Funcionamiento observable.** participa en la UI declarativa Compose y puede recomponerse al cambiar el estado que lee; desplaza I/O/decodificación fuera del hilo principal; accede o prepara almacenamiento local/caché; ramifica o parametriza comportamiento según el perfil de rendimiento.

**Puntos que no conviene romper:** contiene retornos explícitos; algunos caminos pueden terminar antes de ejecutar el resto del bloque; incluye manejo de excepciones; la ruta de error forma parte del contrato, no es un detalle decorativo; captura estado/objetos de composición; sus claves determinan cuándo se reutilizan o reconstruyen.

### 4.17 `prewarm` — fun, líneas 443–467

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
```

**Firma/entrada.** `suspend fun prewarm(context: Context, uri: Uri, type: String, name: String?, performanceMode: String = "balanced") {`

**Parámetros.**
- `context: Context` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `uri: Uri` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `type: String` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `name: String?` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente. Su tipo admite `null`, por lo que el cuerpo debe contemplar ausencia.
- `performanceMode: String = "balanced"` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente. Tiene valor por defecto y puede omitirse en la llamada.

**Funcionamiento observable.** ramifica o parametriza comportamiento según el perfil de rendimiento.

### 4.18 `invalidate` — fun, líneas 473–513

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
            listOf("instant", "performance", "balanced", "quality").forEach { profile ->
                bitmapMemoryCache.remove("image:$key-$profile")
                bitmapMemoryCache.remove("video:$key-$profile")
                bitmapMemoryCache.remove("audio:$key-$profile")
                bitmapMemoryCache.remove("pdf:$key-$profile")
                instantMemoryCache.remove("image:$key-$profile")
                instantMemoryCache.remove("video:$key-$profile")
                instantMemoryCache.remove("pdf:$key-$profile")
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
```

**Firma/entrada.** `suspend fun invalidate(context: Context, uri: Uri) = withContext(Dispatchers.IO) {`

**Parámetros.**
- `context: Context` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `uri: Uri) = withContext(Dispatchers.IO` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente. Tiene valor por defecto y puede omitirse en la llamada.

**Funcionamiento observable.** desplaza I/O/decodificación fuera del hilo principal; accede o prepara almacenamiento local/caché.

**Puntos que no conviene romper:** incluye manejo de excepciones; la ruta de error forma parte del contrato, no es un detalle decorativo.

### 4.19 `cacheDirectory` — fun, líneas 519–525

```kotlin
    private fun cacheDirectory(context: Context, name: String): File {
        val directory = File(context.cacheDir, "mynotes_perf_v2/$name")
        if (!directory.exists()) {
            directory.mkdirs()
        }
        return directory
    }
```

**Firma/entrada.** `private fun cacheDirectory(context: Context, name: String): File {`

**Parámetros.**
- `context: Context` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `name: String` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.

**Funcionamiento observable.** accede o prepara almacenamiento local/caché.

**Puntos que no conviene romper:** contiene retornos explícitos; algunos caminos pueden terminar antes de ejecutar el resto del bloque.

### 4.20 `cacheKey` — fun, líneas 526–531

```kotlin
    private fun cacheKey(uri: Uri): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(uri.toString().toByteArray(Charsets.UTF_8))
        return digest.joinToString(separator = "") {
                byte -> "%02x".format(byte)
            }
    }
```

**Firma/entrada.** `private fun cacheKey(uri: Uri): String {`

**Parámetros.**
- `uri: Uri` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.

**Funcionamiento observable.** encapsula una operación local cuyo contrato se deriva de sus parámetros, retorno y llamadas internas.

**Puntos que no conviene romper:** contiene retornos explícitos; algunos caminos pueden terminar antes de ejecutar el resto del bloque.

### 4.21 `setRetrieverDataSource` — fun, líneas 532–539

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

**Firma/entrada.** `private fun setRetrieverDataSource(context: Context, retriever: MediaMetadataRetriever, uri: Uri) {`

**Parámetros.**
- `context: Context` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `retriever: MediaMetadataRetriever` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `uri: Uri` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.

**Funcionamiento observable.** encapsula una operación local cuyo contrato se deriva de sus parámetros, retorno y llamadas internas.

### 4.22 `createVideoFrame` — fun, líneas 540–588

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

**Firma/entrada.** `private fun createVideoFrame(context: Context, uri: Uri, retriever: MediaMetadataRetriever, sourceWidth: Int, sourceHeight: Int, maxWidth: Int, maxHeight: Int): Bitmap? {`

**Parámetros.**
- `context: Context` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `uri: Uri` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `retriever: MediaMetadataRetriever` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `sourceWidth: Int` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `sourceHeight: Int` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `maxWidth: Int` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `maxHeight: Int` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.

**Funcionamiento observable.** encapsula una operación local cuyo contrato se deriva de sus parámetros, retorno y llamadas internas.

**Puntos que no conviene romper:** contiene retornos explícitos; algunos caminos pueden terminar antes de ejecutar el resto del bloque; incluye manejo de excepciones; la ruta de error forma parte del contrato, no es un detalle decorativo.

### 4.23 `decodeImageThumbnail` — fun, líneas 589–620

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

**Firma/entrada.** `private fun decodeImageThumbnail(context: Context, uri: Uri, maxWidth: Int, maxHeight: Int): Bitmap? {`

**Parámetros.**
- `context: Context` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `uri: Uri` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `maxWidth: Int` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `maxHeight: Int` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.

**Funcionamiento observable.** encapsula una operación local cuyo contrato se deriva de sus parámetros, retorno y llamadas internas.

**Puntos que no conviene romper:** contiene retornos explícitos; algunos caminos pueden terminar antes de ejecutar el resto del bloque; incluye manejo de excepciones; la ruta de error forma parte del contrato, no es un detalle decorativo.

### 4.24 `openUriInputStream` — fun, líneas 621–628

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

**Firma/entrada.** `private fun openUriInputStream(context: Context, uri: Uri): InputStream? {`

**Parámetros.**
- `context: Context` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `uri: Uri` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.

**Funcionamiento observable.** accede o prepara almacenamiento local/caché.

**Puntos que no conviene romper:** contiene retornos explícitos; algunos caminos pueden terminar antes de ejecutar el resto del bloque.

### 4.25 `applyExifOrientation` — fun, líneas 629–674

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

**Firma/entrada.** `private fun applyExifOrientation(context: Context, uri: Uri, bitmap: Bitmap): Bitmap {`

**Parámetros.**
- `context: Context` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `uri: Uri` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `bitmap: Bitmap` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.

**Funcionamiento observable.** encapsula una operación local cuyo contrato se deriva de sus parámetros, retorno y llamadas internas.

**Puntos que no conviene romper:** contiene retornos explícitos; algunos caminos pueden terminar antes de ejecutar el resto del bloque; incluye manejo de excepciones; la ruta de error forma parte del contrato, no es un detalle decorativo.

### 4.26 `scalePreviewBitmap` — fun, líneas 675–688

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

**Firma/entrada.** `private fun scalePreviewBitmap(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {`

**Parámetros.**
- `bitmap: Bitmap` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `maxWidth: Int` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `maxHeight: Int` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.

**Funcionamiento observable.** encapsula una operación local cuyo contrato se deriva de sus parámetros, retorno y llamadas internas.

**Puntos que no conviene romper:** contiene retornos explícitos; algunos caminos pueden terminar antes de ejecutar el resto del bloque; acota valores antes de usarlos para proteger rangos de UI/rendimiento.

### 4.27 `saveJpeg` — fun, líneas 689–701

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

**Firma/entrada.** `private fun saveJpeg(bitmap: Bitmap, destination: File, quality: Int) {`

**Parámetros.**
- `bitmap: Bitmap` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `destination: File` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `quality: Int` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.

**Funcionamiento observable.** encapsula una operación local cuyo contrato se deriva de sus parámetros, retorno y llamadas internas.

**Puntos que no conviene romper:** incluye manejo de excepciones; la ruta de error forma parte del contrato, no es un detalle decorativo.

### 4.28 `decodeCachedBitmap` — fun, líneas 702–714

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

**Firma/entrada.** `private fun decodeCachedBitmap(file: File): Bitmap? {`

**Parámetros.**
- `file: File` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.

**Funcionamiento observable.** accede o prepara almacenamiento local/caché.

**Puntos que no conviene romper:** contiene retornos explícitos; algunos caminos pueden terminar antes de ejecutar el resto del bloque; incluye manejo de excepciones; la ruta de error forma parte del contrato, no es un detalle decorativo.

### 4.29 `decodeSampledBitmap` — fun, líneas 715–731

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

**Firma/entrada.** `private fun decodeSampledBitmap(bytes: ByteArray, requestedSize: Int): Bitmap? {`

**Parámetros.**
- `bytes: ByteArray` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `requestedSize: Int` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.

**Funcionamiento observable.** encapsula una operación local cuyo contrato se deriva de sus parámetros, retorno y llamadas internas.

**Puntos que no conviene romper:** contiene retornos explícitos; algunos caminos pueden terminar antes de ejecutar el resto del bloque.

### 4.30 `readMediaMetadata` — fun, líneas 732–744

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

**Firma/entrada.** `private fun readMediaMetadata(file: File): MediaPreview? {`

**Parámetros.**
- `file: File` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.

**Funcionamiento observable.** encapsula una operación local cuyo contrato se deriva de sus parámetros, retorno y llamadas internas.

**Puntos que no conviene romper:** contiene retornos explícitos; algunos caminos pueden terminar antes de ejecutar el resto del bloque; incluye manejo de excepciones; la ruta de error forma parte del contrato, no es un detalle decorativo.

### 4.31 `writeMediaMetadata` — fun, líneas 745–750

```kotlin
    private fun writeMediaMetadata(file: File, preview: MediaPreview) {
        try {
            file.writeText("${preview.durationMillis}," + "${preview.width}," + "${preview.height}")
        } catch (ignored: Exception) {
        }
    }
```

**Firma/entrada.** `private fun writeMediaMetadata(file: File, preview: MediaPreview) {`

**Parámetros.**
- `file: File` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `preview: MediaPreview` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.

**Funcionamiento observable.** encapsula una operación local cuyo contrato se deriva de sus parámetros, retorno y llamadas internas.

**Puntos que no conviene romper:** incluye manejo de excepciones; la ruta de error forma parte del contrato, no es un detalle decorativo.

### 4.32 `readDocxPreviewStreaming` — fun, líneas 751–809

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

**Firma/entrada.** `private fun readDocxPreviewStreaming(context: Context, uri: Uri, maxCharacters: Int): String? {`

**Parámetros.**
- `context: Context` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `uri: Uri` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `maxCharacters: Int` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.

**Funcionamiento observable.** encapsula una operación local cuyo contrato se deriva de sus parámetros, retorno y llamadas internas.

**Puntos que no conviene romper:** contiene retornos explícitos; algunos caminos pueden terminar antes de ejecutar el resto del bloque; incluye manejo de excepciones; la ruta de error forma parte del contrato, no es un detalle decorativo.

### 4.33 `trimPreview` — fun, líneas 810–818

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

**Firma/entrada.** `private fun trimPreview(text: String?, maxCharacters: Int): String? {`

**Parámetros.**
- `text: String?` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente. Su tipo admite `null`, por lo que el cuerpo debe contemplar ausencia.
- `maxCharacters: Int` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.

**Funcionamiento observable.** encapsula una operación local cuyo contrato se deriva de sus parámetros, retorno y llamadas internas.

**Puntos que no conviene romper:** contiene retornos explícitos; algunos caminos pueden terminar antes de ejecutar el resto del bloque.

## 5. Variables y propiedades, una por una

Se detectaron **119 declaraciones `val`/`var`** en la forma léxica principal. La tabla explica mutabilidad, tipo visible/inferido, inicialización y función práctica.

| Línea | Variable | Declaración | Explicación detallada |
|---:|---|---|---|
| 52 | `legacyPreviewLoadGate` | `private val legacyPreviewLoadGate: inferido` | `val` fija la referencia después de inicializarla; impone un máximo de trabajos concurrentes para proteger CPU/memoria/I/O; forma parte del camino de previsualización/caché; cambios aquí pueden afectar latencia, memoria y pop-in. **Inicialización visible:** `private val legacyPreviewLoadGate = Semaphore(permits = 1)` |
| 53 | `previewLoadGate` | `private val previewLoadGate: inferido` | `val` fija la referencia después de inicializarla; impone un máximo de trabajos concurrentes para proteger CPU/memoria/I/O; forma parte del camino de previsualización/caché; cambios aquí pueden afectar latencia, memoria y pop-in. **Inicialización visible:** `private val previewLoadGate = Semaphore(permits = 2)` |
| 62 | `gate` | `local/pública por contexto val gate: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val gate = if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) legacyPreviewLoadGate else previewLoadGate` |
| 66 | `aspectRatio` | `local/pública por contexto val aspectRatio: F` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val aspectRatio: Float` |
| 73 | `DOCX_CACHE_CHARACTERS` | `private const val DOCX_CACHE_CHARACTERS: inferido` | `val` fija la referencia después de inicializarla; forma parte del camino de previsualización/caché; cambios aquí pueden afectar latencia, memoria y pop-in. **Inicialización visible:** `private const val DOCX_CACHE_CHARACTERS = 4_000` |
| 85 | `videoHeight` | `local/pública por contexto val videoHeight: I` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val videoHeight: Int, val audioArtSize: Int, val pdfWidth: Int, val imageJpegQuality: Int, val videoJpegQuality: Int,` |
| 86 | `audioJpegQuality` | `local/pública por contexto val audioJpegQuality: I` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val audioJpegQuality: Int, val pdfJpegQuality: Int)` |
| 119 | `memoryCacheKb` | `private val memoryCacheKb: inferido` | `val` fija la referencia después de inicializarla; su inicialización está acotada a un intervalo explícito con `coerceIn`; forma parte del camino de previsualización/caché; cambios aquí pueden afectar latencia, memoria y pop-in. **Inicialización visible:** `private val memoryCacheKb = (Runtime.getRuntime().maxMemory() / 1024L / 16L).coerceIn(8L * 1024L, 24L * 1024L).toInt()` |
| 120 | `bitmapMemoryCache` | `private val bitmapMemoryCache: inferido` | `val` fija la referencia después de inicializarla; limita memoria retenida mediante expulsión LRU en lugar de crecimiento ilimitado; forma parte del camino de previsualización/caché; cambios aquí pueden afectar latencia, memoria y pop-in. **Inicialización visible:** `private val bitmapMemoryCache = object : LruCache<String, Bitmap>(memoryCacheKb) {` |
| 132 | `instantMemoryCache` | `private val instantMemoryCache: inferido` | `val` fija la referencia después de inicializarla; limita memoria retenida mediante expulsión LRU en lugar de crecimiento ilimitado; forma parte del camino de previsualización/caché; cambios aquí pueden afectar latencia, memoria y pop-in. **Inicialización visible:** `private val instantMemoryCache = object : LruCache<String, Bitmap>(6 * 1024) {` |
| 152 | `key` | `local/pública por contexto val key: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val key = cacheKey(uri)` |
| 154 | `memoryKey` | `local/pública por contexto val memoryKey: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val memoryKey = "image:$key-$tag"` |
| 160 | `key` | `local/pública por contexto val key: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val key = cacheKey(uri)` |
| 161 | `bitmap` | `local/pública por contexto val bitmap: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val bitmap = preferredTags(performanceMode).firstNotNullOfOrNull { tag ->` |
| 162 | `memoryKey` | `local/pública por contexto val memoryKey: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val memoryKey = "video:$key-$tag"` |
| 169 | `key` | `local/pública por contexto val key: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val key = cacheKey(uri)` |
| 171 | `memoryKey` | `local/pública por contexto val memoryKey: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val memoryKey = "pdf:$key-$tag"` |
| 189 | `directory` | `local/pública por contexto val directory: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val directory = cacheDirectory(context, "image_previews")` |
| 190 | `key` | `local/pública por contexto val key: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val key = cacheKey(uri)` |
| 191 | `profile` | `local/pública por contexto val profile: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val profile = previewProfile(performanceMode)` |
| 192 | `profileKey` | `local/pública por contexto val profileKey: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val profileKey = "$key-${profile.cacheTag}"` |
| 193 | `memoryKey` | `local/pública por contexto val memoryKey: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val memoryKey = "image:$profileKey"` |
| 194 | `cacheFile` | `local/pública por contexto val cacheFile: inferido` | `val` fija la referencia después de inicializarla; representa una ruta/archivo y puede implicar I/O en usos posteriores; forma parte del camino de previsualización/caché; cambios aquí pueden afectar latencia, memoria y pop-in. **Inicialización visible:** `val cacheFile = File(directory, "$profileKey.jpg")` |
| 204 | `bitmap` | `local/pública por contexto val bitmap: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val bitmap = decodeImageThumbnail(context = context, uri = uri, maxWidth = profile.imageWidth, maxHeight = profile.imageHeight)` |
| 217 | `directory` | `local/pública por contexto val directory: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val directory = cacheDirectory(context, "video_previews")` |
| 218 | `key` | `local/pública por contexto val key: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val key = cacheKey(uri)` |
| 219 | `profile` | `local/pública por contexto val profile: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val profile = previewProfile(performanceMode)` |
| 220 | `profileKey` | `local/pública por contexto val profileKey: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val profileKey = "$key-${profile.cacheTag}"` |
| 221 | `imageFile` | `local/pública por contexto val imageFile: inferido` | `val` fija la referencia después de inicializarla; representa una ruta/archivo y puede implicar I/O en usos posteriores. **Inicialización visible:** `val imageFile = File(directory, "$profileKey.jpg")` |
| 222 | `metadataFile` | `local/pública por contexto val metadataFile: inferido` | `val` fija la referencia después de inicializarla; representa una ruta/archivo y puede implicar I/O en usos posteriores. **Inicialización visible:** `val metadataFile = File(directory, "$profileKey.meta")` |
| 223 | `memoryKey` | `local/pública por contexto val memoryKey: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val memoryKey = "video:$profileKey"` |
| 224 | `cachedMetadata` | `local/pública por contexto val cachedMetadata: inferido` | `val` fija la referencia después de inicializarla; forma parte del camino de previsualización/caché; cambios aquí pueden afectar latencia, memoria y pop-in. **Inicialización visible:** `val cachedMetadata = readMediaMetadata(metadataFile)` |
| 225 | `memoryBitmap` | `local/pública por contexto val memoryBitmap: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val memoryBitmap = memoryBitmap(memoryKey, profile.cacheTag)` |
| 229 | `cachedBitmap` | `local/pública por contexto val cachedBitmap: inferido` | `val` fija la referencia después de inicializarla; forma parte del camino de previsualización/caché; cambios aquí pueden afectar latencia, memoria y pop-in. **Inicialización visible:** `val cachedBitmap = decodeCachedBitmap(imageFile)` |
| 234 | `retriever` | `local/pública por contexto val retriever: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val retriever = MediaMetadataRetriever()` |
| 237 | `duration` | `local/pública por contexto val duration: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull()?: 0L` |
| 238 | `rawWidth` | `local/pública por contexto val rawWidth: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val rawWidth = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)?.toIntOrNull()?: 0` |
| 239 | `rawHeight` | `local/pública por contexto val rawHeight: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val rawHeight = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)?.toIntOrNull()?: 0` |
| 240 | `rotation` | `local/pública por contexto val rotation: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val rotation = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)?.toIntOrNull()?: 0` |
| 241 | `width` | `local/pública por contexto val width: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val width = if (rotation == 90 \|\| rotation == 270) {` |
| 246 | `height` | `local/pública por contexto val height: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val height = if (rotation == 90 \|\| rotation == 270) {` |
| 251 | `bitmap` | `local/pública por contexto val bitmap: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val bitmap = createVideoFrame(context = context, uri = uri, retriever = retriever, sourceWidth = width, sourceHeight =` |
| 259 | `result` | `local/pública por contexto val result: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val result = MediaPreview(bitmap = bitmap,` |
| 282 | `directory` | `local/pública por contexto val directory: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val directory = cacheDirectory(context, "audio_previews")` |
| 283 | `key` | `local/pública por contexto val key: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val key = cacheKey(uri)` |
| 284 | `profile` | `local/pública por contexto val profile: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val profile = previewProfile(performanceMode)` |
| 285 | `profileKey` | `local/pública por contexto val profileKey: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val profileKey = "$key-${profile.cacheTag}"` |
| 286 | `imageFile` | `local/pública por contexto val imageFile: inferido` | `val` fija la referencia después de inicializarla; representa una ruta/archivo y puede implicar I/O en usos posteriores. **Inicialización visible:** `val imageFile = File(directory, "$profileKey.jpg")` |
| 287 | `metadataFile` | `local/pública por contexto val metadataFile: inferido` | `val` fija la referencia después de inicializarla; representa una ruta/archivo y puede implicar I/O en usos posteriores. **Inicialización visible:** `val metadataFile = File(directory, "$profileKey.meta")` |
| 288 | `memoryKey` | `local/pública por contexto val memoryKey: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val memoryKey = "audio:$profileKey"` |
| 289 | `cachedMetadata` | `local/pública por contexto val cachedMetadata: inferido` | `val` fija la referencia después de inicializarla; forma parte del camino de previsualización/caché; cambios aquí pueden afectar latencia, memoria y pop-in. **Inicialización visible:** `val cachedMetadata = readMediaMetadata(metadataFile)` |
| 290 | `cachedBitmap` | `local/pública por contexto val cachedBitmap: inferido` | `val` fija la referencia después de inicializarla; forma parte del camino de previsualización/caché; cambios aquí pueden afectar latencia, memoria y pop-in. **Inicialización visible:** `val cachedBitmap = if (loadAlbumArt) {` |
| 301 | `retriever` | `local/pública por contexto val retriever: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val retriever = MediaMetadataRetriever()` |
| 304 | `duration` | `local/pública por contexto val duration: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull()?: 0L` |
| 305 | `bitmap` | `local/pública por contexto var bitmap: inferido` | `var` permite sustituir el valor durante la vida del ámbito. **Inicialización visible:** `var bitmap:` |
| 308 | `embeddedPicture` | `local/pública por contexto val embeddedPicture: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val embeddedPicture = retriever.embeddedPicture` |
| 328 | `result` | `local/pública por contexto val result: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val result = MediaPreview(bitmap = bitmap,` |
| 348 | `directory` | `local/pública por contexto val directory: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val directory = cacheDirectory(context, "docx_previews")` |
| 349 | `cacheFile` | `local/pública por contexto val cacheFile: inferido` | `val` fija la referencia después de inicializarla; representa una ruta/archivo y puede implicar I/O en usos posteriores; forma parte del camino de previsualización/caché; cambios aquí pueden afectar latencia, memoria y pop-in. **Inicialización visible:** `val cacheFile = File(directory, "${cacheKey(uri)}.txt")` |
| 351 | `cached` | `local/pública por contexto val cached: inferido` | `val` fija la referencia después de inicializarla; forma parte del camino de previsualización/caché; cambios aquí pueden afectar latencia, memoria y pop-in. **Inicialización visible:** `val cached = try {` |
| 360 | `preview` | `local/pública por contexto val preview: inferido` | `val` fija la referencia después de inicializarla; forma parte del camino de previsualización/caché; cambios aquí pueden afectar latencia, memoria y pop-in. **Inicialización visible:** `val preview = readDocxPreviewStreaming(context = context,` |
| 377 | `directory` | `local/pública por contexto val directory: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val directory = cacheDirectory(context, "pdf_previews")` |
| 378 | `key` | `local/pública por contexto val key: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val key = cacheKey(uri)` |
| 379 | `profile` | `local/pública por contexto val profile: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val profile = previewProfile(performanceMode)` |
| 380 | `profileKey` | `local/pública por contexto val profileKey: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val profileKey = "$key-${profile.cacheTag}"` |
| 381 | `memoryKey` | `local/pública por contexto val memoryKey: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val memoryKey = "pdf:$profileKey"` |
| 382 | `cacheFile` | `local/pública por contexto val cacheFile: inferido` | `val` fija la referencia después de inicializarla; representa una ruta/archivo y puede implicar I/O en usos posteriores; forma parte del camino de previsualización/caché; cambios aquí pueden afectar latencia, memoria y pop-in. **Inicialización visible:** `val cacheFile = File(directory, "$profileKey.jpg")` |
| 383 | `cached` | `local/pública por contexto val cached: inferido` | `val` fija la referencia después de inicializarla; forma parte del camino de previsualización/caché; cambios aquí pueden afectar latencia, memoria y pop-in. **Inicialización visible:** `val cached = memoryBitmap(memoryKey, profile.cacheTag)?: decodeCachedBitmap(cacheFile)?.also {` |
| 390 | `descriptor` | `local/pública por contexto var descriptor: inferido` | `var` permite sustituir el valor durante la vida del ámbito. **Inicialización visible:** `var descriptor:` |
| 392 | `renderer` | `local/pública por contexto var renderer: inferido` | `var` permite sustituir el valor durante la vida del ámbito. **Inicialización visible:** `var renderer:` |
| 394 | `page` | `local/pública por contexto var page: inferido` | `var` permite sustituir el valor durante la vida del ámbito. **Inicialización visible:** `var page:` |
| 398 | `path` | `local/pública por contexto val path: inferido` | `val` fija la referencia después de inicializarla; admite ausencia (`null`) y por tanto sus consumidores deben manejar la nulabilidad. **Inicialización visible:** `val path = uri.path?: return@withContext null` |
| 403 | `safeDescriptor` | `local/pública por contexto val safeDescriptor: inferido` | `val` fija la referencia después de inicializarla; admite ausencia (`null`) y por tanto sus consumidores deben manejar la nulabilidad. **Inicialización visible:** `val safeDescriptor = descriptor?: return@withContext null` |
| 409 | `sourceWidth` | `local/pública por contexto val sourceWidth: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val sourceWidth = max(page.width, 1)` |
| 410 | `sourceHeight` | `local/pública por contexto val sourceHeight: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val sourceHeight = max(page.height, 1)` |
| 411 | `scale` | `local/pública por contexto val scale: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val scale = profile.pdfWidth.toFloat() / sourceWidth.toFloat()` |
| 412 | `outputHeight` | `local/pública por contexto val outputHeight: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val outputHeight = max(1, (sourceHeight * scale).toInt())` |
| 413 | `bitmap` | `local/pública por contexto val bitmap: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val bitmap = Bitmap.createBitmap(profile.pdfWidth, outputHeight, Bitmap.Config.ARGB_8888)` |
| 455 | `extension` | `local/pública por contexto val extension: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val extension = name?.substringAfterLast(".", "")?.lowercase().orEmpty()` |
| 474 | `key` | `local/pública por contexto val key: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val key = cacheKey(uri)` |
| 500 | `directory` | `local/pública por contexto val directory: inferido` | `val` fija la referencia después de inicializarla; representa una ruta/archivo y puede implicar I/O en usos posteriores. **Inicialización visible:** `val directory = File(context.cacheDir, "$rootFolder/$folder")` |
| 520 | `directory` | `local/pública por contexto val directory: inferido` | `val` fija la referencia después de inicializarla; representa una ruta/archivo y puede implicar I/O en usos posteriores. **Inicialización visible:** `val directory = File(context.cacheDir, "mynotes_perf_v2/$name")` |
| 527 | `digest` | `local/pública por contexto val digest: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val digest = MessageDigest.getInstance("SHA-256").digest(uri.toString().toByteArray(Charsets.UTF_8))` |
| 534 | `path` | `local/pública por contexto val path: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val path = uri.path?: throw IllegalArgumentException("URI de archivo inválida")` |
| 542 | `safeWidth` | `local/pública por contexto val safeWidth: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val safeWidth = max(sourceWidth, 1)` |
| 543 | `safeHeight` | `local/pública por contexto val safeHeight: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val safeHeight = max(sourceHeight, 1)` |
| 544 | `scale` | `local/pública por contexto val scale: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val scale = minOf(maxWidth.toFloat() / safeWidth.toFloat(),` |
| 547 | `targetWidth` | `local/pública por contexto val targetWidth: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val targetWidth = max(1, (safeWidth * scale).toInt())` |
| 548 | `targetHeight` | `local/pública por contexto val targetHeight: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val targetHeight = max(1, (safeHeight * scale).toInt())` |
| 558 | `path` | `local/pública por contexto val path: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val path = uri.path` |
| 562 | `legacyThumbnail` | `local/pública por contexto val legacyThumbnail: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val legacyThumbnail = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Video.Thumbnails.MINI_KIND)` |
| 580 | `original` | `local/pública por contexto val original: inferido` | `val` fija la referencia después de inicializarla; admite ausencia (`null`) y por tanto sus consumidores deben manejar la nulabilidad. **Inicialización visible:** `val original = retriever.getFrameAtTime(1_000_000L, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)?: return null` |
| 591 | `bounds` | `local/pública por contexto val bounds: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val bounds = BitmapFactory.Options().apply {` |
| 601 | `sample` | `local/pública por contexto var sample: inferido` | `var` permite sustituir el valor durante la vida del ámbito. **Inicialización visible:** `var sample = 1` |
| 605 | `options` | `local/pública por contexto val options: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val options = BitmapFactory.Options().apply {` |
| 609 | `decoded` | `local/pública por contexto val decoded: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val decoded = openUriInputStream(context = context, uri = uri)?.use {` |
| 613 | `oriented` | `local/pública por contexto val oriented: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val oriented = applyExifOrientation(context = context, uri = uri, bitmap = decoded)` |
| 623 | `path` | `local/pública por contexto val path: inferido` | `val` fija la referencia después de inicializarla; admite ausencia (`null`) y por tanto sus consumidores deben manejar la nulabilidad. **Inicialización visible:** `val path = uri.path?: return null` |
| 630 | `orientation` | `local/pública por contexto val orientation: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val orientation = try {` |
| 632 | `path` | `local/pública por contexto val path: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val path = uri.path?: return bitmap` |
| 646 | `matrix` | `local/pública por contexto val matrix: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val matrix = Matrix()` |
| 664 | `transformed` | `local/pública por contexto val transformed: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val transformed = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)` |
| 679 | `scale` | `local/pública por contexto val scale: inferido` | `val` fija la referencia después de inicializarla; impone un límite inferior explícito. **Inicialización visible:** `val scale = minOf(maxWidth.toFloat() / bitmap.width.coerceAtLeast(1),` |
| 681 | `width` | `local/pública por contexto val width: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val width = max(1, (bitmap.width * scale).toInt())` |
| 682 | `height` | `local/pública por contexto val height: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val height = max(1, (bitmap.height * scale).toInt())` |
| 683 | `scaled` | `local/pública por contexto val scaled: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val scaled = Bitmap.createScaledBitmap(bitmap, width, height, true)` |
| 707 | `options` | `local/pública por contexto val options: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val options = BitmapFactory.Options().apply {` |
| 716 | `bounds` | `local/pública por contexto val bounds: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val bounds = BitmapFactory.Options().apply {` |
| 720 | `sample` | `local/pública por contexto var sample: inferido` | `var` permite sustituir el valor durante la vida del ámbito. **Inicialización visible:** `var sample = 1` |
| 726 | `options` | `local/pública por contexto val options: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val options = BitmapFactory.Options().apply {` |
| 737 | `values` | `local/pública por contexto val values: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val values = file.readText().split(",")` |
| 753 | `source` | `local/pública por contexto val source: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val source = if (uri.scheme == "file") {` |
| 754 | `path` | `local/pública por contexto val path: inferido` | `val` fija la referencia después de inicializarla; admite ausencia (`null`) y por tanto sus consumidores deben manejar la nulabilidad. **Inicialización visible:** `val path = uri.path?: return null` |
| 763 | `entry` | `local/pública por contexto var entry: inferido` | `var` permite sustituir el valor durante la vida del ámbito. **Inicialización visible:** `var entry = zip.nextEntry` |
| 766 | `parser` | `local/pública por contexto val parser: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val parser = Xml.newPullParser()` |
| 768 | `result` | `local/pública por contexto val result: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val result = StringBuilder()` |
| 769 | `event` | `local/pública por contexto var event: inferido` | `var` permite sustituir el valor durante la vida del ámbito. **Inicialización visible:** `var event = parser.eventType` |
| 775 | `text` | `local/pública por contexto val text: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val text = parser.nextText()` |
| 792 | `clean` | `local/pública por contexto val clean: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val clean = result.toString().replace(Regex("[\\t ]+"), " ").replace(Regex("\\n{3,}"), "\n\n").trim()` |

## 6. Mapa de ámbitos y bloques `{ ... }`

Se documentan **170 bloques estructurales** relevantes. La profundidad indica cuántos ámbitos externos contienen al bloque.

| Inicio–fin | Prof. | Tipo de bloque | Cabecera | Qué implica |
|---|---:|---|---|---|
| 46–819 | 0 | ámbito/lambda anónima | `object AttachmentPreviewCache {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 54–64 | 1 | ámbito/lambda anónima | `suspend fun <T> withPreviewPermit(block: suspend () -> T): T {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 63–63 | 2 | ámbito/lambda anónima | `return gate.withPermit { block() }` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 65–72 | 1 | ámbito/lambda anónima | `data class MediaPreview(val bitmap: Bitmap? = null, val durationMillis: Long = 0L, val width: Int = 0, val height: Int = 0) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 67–69 | 2 | condición `if` | `get() = if (width > 0 && height > 0) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 69–71 | 2 | ámbito/lambda anónima | `} else {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 87–108 | 1 | selección `when` | `private fun previewProfile(performanceMode: String): PreviewProfile = when (performanceMode) {` | Despacha comportamiento según un valor/condición. Las ramas representan contratos mutuamente seleccionados y el `else`, si existe, actúa como fallback. |
| 120–124 | 1 | ámbito/lambda anónima | `private val bitmapMemoryCache = object : LruCache<String, Bitmap>(memoryCacheKb) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 121–123 | 2 | ámbito/lambda anónima | `override fun sizeOf(key: String, value: Bitmap): Int {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 132–134 | 1 | ámbito/lambda anónima | `private val instantMemoryCache = object : LruCache<String, Bitmap>(6 * 1024) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 138–140 | 1 | ámbito/lambda anónima | `private fun rememberBitmap(memoryKey: String, cacheTag: String, bitmap: Bitmap) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 141–145 | 1 | selección `when` | `private fun preferredTags(performanceMode: String): List<String> = when (performanceMode) {` | Despacha comportamiento según un valor/condición. Las ramas representan contratos mutuamente seleccionados y el `else`, si existe, actúa como fallback. |
| 151–158 | 1 | ámbito/lambda anónima | `fun peekImagePreview(uri: Uri, performanceMode: String): Bitmap? {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 153–157 | 2 | ámbito/lambda anónima | `return preferredTags(performanceMode).firstNotNullOfOrNull { tag ->` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 159–167 | 1 | ámbito/lambda anónima | `fun peekVideoPreview(uri: Uri, performanceMode: String): MediaPreview? {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 161–165 | 2 | ámbito/lambda anónima | `val bitmap = preferredTags(performanceMode).firstNotNullOfOrNull { tag ->` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 166–166 | 2 | ámbito/lambda anónima | `return bitmap?.let { MediaPreview(bitmap = it) }` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 168–175 | 1 | ámbito/lambda anónima | `fun peekPdfPreview(uri: Uri, performanceMode: String): Bitmap? {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 170–174 | 2 | ámbito/lambda anónima | `return preferredTags(performanceMode).firstNotNullOfOrNull { tag ->` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 188–209 | 1 | `withContext` / cambio de dispatcher | `suspend fun loadImagePreview(context: Context, uri: Uri, performanceMode: String = "balanced"): Bitmap? = withContext(Dispatchers.IO) {` | Mueve temporalmente la ejecución a otro dispatcher; normalmente evita hacer I/O/decodificación en el hilo principal. |
| 195–197 | 2 | ámbito/lambda anónima | `memoryBitmap(memoryKey, profile.cacheTag)?.let {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 198–201 | 2 | ámbito/lambda anónima | `decodeCachedBitmap(cacheFile)?.also {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 201–203 | 2 | ámbito/lambda anónima | `}?.let {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 216–274 | 1 | ámbito/lambda anónima | `Dispatchers.IO) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 226–228 | 2 | condición `if` | `if (memoryBitmap != null && cachedMetadata != null) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 230–233 | 2 | condición `if` | `if (cachedBitmap != null && cachedMetadata != null) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 235–265 | 2 | `try` / manejo de error | `try {` | Protege una operación susceptible de excepción. El camino de éxito y el de fallo deben dejar recursos/estado en condiciones coherentes. |
| 241–243 | 3 | condición `if` | `val width = if (rotation == 90 \|\| rotation == 270) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 243–245 | 3 | ámbito/lambda anónima | `} else {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 246–248 | 3 | condición `if` | `val height = if (rotation == 90 \|\| rotation == 270) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 248–250 | 3 | ámbito/lambda anónima | `} else {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 253–258 | 3 | condición `if` | `if (bitmap != null) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 265–268 | 2 | `catch` / recuperación de error | `} catch (e: Exception) {` | Captura una excepción del bloque protegido y define recuperación/limpieza en vez de propagarla sin control. |
| 268–273 | 2 | `finally` / limpieza garantizada | `} finally {` | Se ejecuta tanto en éxito como en error y suele usarse para liberar recursos que no deben quedar abiertos. |
| 269–271 | 3 | `try` / manejo de error | `try {` | Protege una operación susceptible de excepción. El camino de éxito y el de fallo deben dejar recursos/estado en condiciones coherentes. |
| 271–272 | 3 | `catch` / recuperación de error | `} catch (ignored: Exception) {` | Captura una excepción del bloque protegido y define recuperación/limpieza en vez de propagarla sin control. |
| 281–341 | 1 | `withContext` / cambio de dispatcher | `withContext(Dispatchers.IO) {` | Mueve temporalmente la ejecución a otro dispatcher; normalmente evita hacer I/O/decodificación en el hilo principal. |
| 290–295 | 2 | condición `if` | `val cachedBitmap = if (loadAlbumArt) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 291–294 | 3 | ámbito/lambda anónima | `bitmapMemoryCache.get(memoryKey)?: decodeCachedBitmap(imageFile)?.also {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 295–297 | 2 | ámbito/lambda anónima | `} else {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 298–300 | 2 | condición `if` | `if (cachedMetadata != null && (!loadAlbumArt \|\| cachedBitmap != null \|\| imageFile.exists())) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 302–332 | 2 | `try` / manejo de error | `try {` | Protege una operación susceptible de excepción. El camino de éxito y el de fallo deben dejar recursos/estado en condiciones coherentes. |
| 307–327 | 3 | condición `if` | `if (loadAlbumArt) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 309–324 | 4 | condición `if` | `if (embeddedPicture != null) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 312–317 | 5 | condición `if` | `if (bitmap != null) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 332–335 | 2 | `catch` / recuperación de error | `} catch (e: Exception) {` | Captura una excepción del bloque protegido y define recuperación/limpieza en vez de propagarla sin control. |
| 335–340 | 2 | `finally` / limpieza garantizada | `} finally {` | Se ejecuta tanto en éxito como en error y suele usarse para liberar recursos que no deben quedar abiertos. |
| 336–338 | 3 | `try` / manejo de error | `try {` | Protege una operación susceptible de excepción. El camino de éxito y el de fallo deben dejar recursos/estado en condiciones coherentes. |
| 338–339 | 3 | `catch` / recuperación de error | `} catch (ignored: Exception) {` | Captura una excepción del bloque protegido y define recuperación/limpieza en vez de propagarla sin control. |
| 347–370 | 1 | `withContext` / cambio de dispatcher | `suspend fun loadDocxPreview(context: Context, uri: Uri, maxCharacters: Int): String? = withContext(Dispatchers.IO) {` | Mueve temporalmente la ejecución a otro dispatcher; normalmente evita hacer I/O/decodificación en el hilo principal. |
| 350–359 | 2 | condición `if` | `if (cacheFile.exists() && cacheFile.length() > 0L) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 351–353 | 3 | `try` / manejo de error | `val cached = try {` | Protege una operación susceptible de excepción. El camino de éxito y el de fallo deben dejar recursos/estado en condiciones coherentes. |
| 353–355 | 3 | `catch` / recuperación de error | `} catch (e: Exception) {` | Captura una excepción del bloque protegido y define recuperación/limpieza en vez de propagarla sin control. |
| 356–358 | 3 | condición `if` | `if (!cached.isNullOrBlank()) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 363–368 | 2 | condición `if` | `if (!preview.isNullOrBlank()) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 364–366 | 3 | `try` / manejo de error | `try {` | Protege una operación susceptible de excepción. El camino de éxito y el de fallo deben dejar recursos/estado en condiciones coherentes. |
| 366–367 | 3 | `catch` / recuperación de error | `} catch (ignored: Exception) {` | Captura una excepción del bloque protegido y define recuperación/limpieza en vez de propagarla sin control. |
| 376–437 | 1 | `withContext` / cambio de dispatcher | `suspend fun loadPdfFirstPage(context: Context, uri: Uri, performanceMode: String = "balanced"): Bitmap? = withContext(Dispatchers.IO) {` | Mueve temporalmente la ejecución a otro dispatcher; normalmente evita hacer I/O/decodificación en el hilo principal. |
| 383–386 | 2 | ámbito/lambda anónima | `val cached = memoryBitmap(memoryKey, profile.cacheTag)?: decodeCachedBitmap(cacheFile)?.also {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 387–389 | 2 | condición `if` | `if (cached != null) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 396–420 | 2 | `try` / manejo de error | `try {` | Protege una operación susceptible de excepción. El camino de éxito y el de fallo deben dejar recursos/estado en condiciones coherentes. |
| 397–400 | 3 | condición `if` | `descriptor = if (uri.scheme == "file") {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 400–402 | 3 | ámbito/lambda anónima | `} else {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 405–407 | 3 | condición `if` | `if (renderer.pageCount <= 0) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 420–423 | 2 | `catch` / recuperación de error | `} catch (e: Exception) {` | Captura una excepción del bloque protegido y define recuperación/limpieza en vez de propagarla sin control. |
| 423–436 | 2 | `finally` / limpieza garantizada | `} finally {` | Se ejecuta tanto en éxito como en error y suele usarse para liberar recursos que no deben quedar abiertos. |
| 424–426 | 3 | `try` / manejo de error | `try {` | Protege una operación susceptible de excepción. El camino de éxito y el de fallo deben dejar recursos/estado en condiciones coherentes. |
| 426–427 | 3 | `catch` / recuperación de error | `} catch (ignored: Exception) {` | Captura una excepción del bloque protegido y define recuperación/limpieza en vez de propagarla sin control. |
| 428–430 | 3 | `try` / manejo de error | `try {` | Protege una operación susceptible de excepción. El camino de éxito y el de fallo deben dejar recursos/estado en condiciones coherentes. |
| 430–431 | 3 | `catch` / recuperación de error | `} catch (ignored: Exception) {` | Captura una excepción del bloque protegido y define recuperación/limpieza en vez de propagarla sin control. |
| 432–434 | 3 | `try` / manejo de error | `try {` | Protege una operación susceptible de excepción. El camino de éxito y el de fallo deben dejar recursos/estado en condiciones coherentes. |
| 434–435 | 3 | `catch` / recuperación de error | `} catch (ignored: Exception) {` | Captura una excepción del bloque protegido y define recuperación/limpieza en vez de propagarla sin control. |
| 443–467 | 1 | ámbito/lambda anónima | `suspend fun prewarm(context: Context, uri: Uri, type: String, name: String?, performanceMode: String = "balanced") {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 444–466 | 2 | ámbito/lambda anónima | `withPreviewPermit {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 445–465 | 3 | selección `when` | `when (type) {` | Despacha comportamiento según un valor/condición. Las ramas representan contratos mutuamente seleccionados y el `else`, si existe, actúa como fallback. |
| 456–463 | 5 | selección `when` | `when (extension) {` | Despacha comportamiento según un valor/condición. Las ramas representan contratos mutuamente seleccionados y el `else`, si existe, actúa como fallback. |
| 473–513 | 1 | `withContext` / cambio de dispatcher | `suspend fun invalidate(context: Context, uri: Uri) = withContext(Dispatchers.IO) {` | Mueve temporalmente la ejecución a otro dispatcher; normalmente evita hacer I/O/decodificación en el hilo principal. |
| 482–490 | 2 | iteración funcional | `listOf("instant", "performance", "balanced", "quality").forEach { profile ->` | Repite el cuerpo para elementos/condiciones; el coste crece con la cantidad de entradas y las operaciones internas deben evitar trabajo pesado innecesario en UI. |
| 496–512 | 2 | iteración funcional | `listOf("mynotes_perf_v2", "mynotes").forEach {` | Repite el cuerpo para elementos/condiciones; el coste crece con la cantidad de entradas y las operaciones internas deben evitar trabajo pesado innecesario en UI. |
| 498–511 | 3 | iteración funcional | `listOf("image_previews", "video_previews", "audio_previews", "docx_previews", "pdf_previews").forEach {` | Repite el cuerpo para elementos/condiciones; el coste crece con la cantidad de entradas y las operaciones internas deben evitar trabajo pesado innecesario en UI. |
| 504–510 | 4 | iteración funcional | `}?.forEach {` | Repite el cuerpo para elementos/condiciones; el coste crece con la cantidad de entradas y las operaciones internas deben evitar trabajo pesado innecesario en UI. |
| 506–508 | 5 | `try` / manejo de error | `try {` | Protege una operación susceptible de excepción. El camino de éxito y el de fallo deben dejar recursos/estado en condiciones coherentes. |
| 508–509 | 5 | `catch` / recuperación de error | `} catch (ignored: Exception) {` | Captura una excepción del bloque protegido y define recuperación/limpieza en vez de propagarla sin control. |
| 519–525 | 1 | ámbito/lambda anónima | `private fun cacheDirectory(context: Context, name: String): File {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 521–523 | 2 | condición `if` | `if (!directory.exists()) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 526–531 | 1 | ámbito/lambda anónima | `private fun cacheKey(uri: Uri): String {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 528–530 | 2 | ámbito/lambda anónima | `return digest.joinToString(separator = "") {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 532–539 | 1 | ámbito/lambda anónima | `private fun setRetrieverDataSource(context: Context, retriever: MediaMetadataRetriever, uri: Uri) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 533–536 | 2 | condición `if` | `if (uri.scheme == "file") {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 536–538 | 2 | ámbito/lambda anónima | `} else {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 541–588 | 1 | ámbito/lambda anónima | `maxWidth: Int, maxHeight: Int): Bitmap? {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 557–575 | 2 | ámbito/lambda anónima | `Build.VERSION_CODES.O_MR1 && uri.scheme == "file") {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 559–574 | 3 | condición `if` | `if (!path.isNullOrBlank()) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 560–566 | 4 | `try` / manejo de error | `try {` | Protege una operación susceptible de excepción. El camino de éxito y el de fallo deben dejar recursos/estado en condiciones coherentes. |
| 563–565 | 5 | condición `if` | `if (legacyThumbnail != null) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 566–571 | 4 | `catch` / recuperación de error | `} catch (_: OutOfMemoryError) {` | Captura una excepción del bloque protegido y define recuperación/limpieza en vez de propagarla sin control. |
| 571–573 | 4 | `catch` / recuperación de error | `} catch (_: Exception) {` | Captura una excepción del bloque protegido y define recuperación/limpieza en vez de propagarla sin control. |
| 576–583 | 2 | `try` / manejo de error | `return try {` | Protege una operación susceptible de excepción. El camino de éxito y el de fallo deben dejar recursos/estado en condiciones coherentes. |
| 577–579 | 3 | condición `if` | `if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 579–582 | 3 | ámbito/lambda anónima | `} else {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 583–585 | 2 | `catch` / recuperación de error | `} catch (_: OutOfMemoryError) {` | Captura una excepción del bloque protegido y define recuperación/limpieza en vez de propagarla sin control. |
| 585–587 | 2 | `catch` / recuperación de error | `} catch (_: Exception) {` | Captura una excepción del bloque protegido y define recuperación/limpieza en vez de propagarla sin control. |
| 589–620 | 1 | ámbito/lambda anónima | `private fun decodeImageThumbnail(context: Context, uri: Uri, maxWidth: Int, maxHeight: Int): Bitmap? {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 590–615 | 2 | `try` / manejo de error | `return try {` | Protege una operación susceptible de excepción. El camino de éxito y el de fallo deben dejar recursos/estado en condiciones coherentes. |
| 591–593 | 3 | ámbito/lambda anónima | `val bounds = BitmapFactory.Options().apply {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 594–597 | 3 | ámbito/lambda anónima | `openUriInputStream(context = context, uri = uri)?.use {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 598–600 | 3 | condición `if` | `if (bounds.outWidth <= 0 \|\| bounds.outHeight <= 0) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 602–604 | 3 | bucle `while` | `while (bounds.outWidth / sample > maxWidth * 2 \|\| bounds.outHeight / sample > maxHeight * 2) {` | Repite el cuerpo para elementos/condiciones; el coste crece con la cantidad de entradas y las operaciones internas deben evitar trabajo pesado innecesario en UI. |
| 605–608 | 3 | ámbito/lambda anónima | `val options = BitmapFactory.Options().apply {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 609–612 | 3 | ámbito/lambda anónima | `val decoded = openUriInputStream(context = context, uri = uri)?.use {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 615–617 | 2 | `catch` / recuperación de error | `} catch (_: OutOfMemoryError) {` | Captura una excepción del bloque protegido y define recuperación/limpieza en vez de propagarla sin control. |
| 617–619 | 2 | `catch` / recuperación de error | `} catch (_: Exception) {` | Captura una excepción del bloque protegido y define recuperación/limpieza en vez de propagarla sin control. |
| 621–628 | 1 | ámbito/lambda anónima | `private fun openUriInputStream(context: Context, uri: Uri): InputStream? {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 622–625 | 2 | condición `if` | `return if (uri.scheme == "file") {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 625–627 | 2 | ámbito/lambda anónima | `} else {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 629–674 | 1 | ámbito/lambda anónima | `private fun applyExifOrientation(context: Context, uri: Uri, bitmap: Bitmap): Bitmap {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 630–640 | 2 | `try` / manejo de error | `val orientation = try {` | Protege una operación susceptible de excepción. El camino de éxito y el de fallo deben dejar recursos/estado en condiciones coherentes. |
| 631–634 | 3 | condición `if` | `if (uri.scheme == "file") {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 634–639 | 3 | ámbito/lambda anónima | `} else {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 640–642 | 2 | `catch` / recuperación de error | `} catch (_: Exception) {` | Captura una excepción del bloque protegido y define recuperación/limpieza en vez de propagarla sin control. |
| 643–645 | 2 | condición `if` | `if (orientation == ExifInterface.ORIENTATION_NORMAL \|\| orientation == ExifInterface.ORIENTATION_UNDEFINED) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 647–662 | 2 | selección `when` | `when (orientation) {` | Despacha comportamiento según un valor/condición. Las ramas representan contratos mutuamente seleccionados y el `else`, si existe, actúa como fallback. |
| 651–654 | 3 | ámbito/lambda anónima | `ExifInterface.ORIENTATION_TRANSPOSE -> {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 656–659 | 3 | ámbito/lambda anónima | `ExifInterface.ORIENTATION_TRANSVERSE -> {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 663–669 | 2 | `try` / manejo de error | `return try {` | Protege una operación susceptible de excepción. El camino de éxito y el de fallo deben dejar recursos/estado en condiciones coherentes. |
| 665–667 | 3 | condición `if` | `if (transformed !== bitmap) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 669–671 | 2 | `catch` / recuperación de error | `} catch (_: OutOfMemoryError) {` | Captura una excepción del bloque protegido y define recuperación/limpieza en vez de propagarla sin control. |
| 671–673 | 2 | `catch` / recuperación de error | `} catch (_: Exception) {` | Captura una excepción del bloque protegido y define recuperación/limpieza en vez de propagarla sin control. |
| 675–688 | 1 | ámbito/lambda anónima | `private fun scalePreviewBitmap(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 676–678 | 2 | condición `if` | `if (bitmap.width <= maxWidth && bitmap.height <= maxHeight) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 684–686 | 2 | condición `if` | `if (scaled !== bitmap) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 689–701 | 1 | ámbito/lambda anónima | `private fun saveJpeg(bitmap: Bitmap, destination: File, quality: Int) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 690–695 | 2 | `try` / manejo de error | `try {` | Protege una operación susceptible de excepción. El camino de éxito y el de fallo deben dejar recursos/estado en condiciones coherentes. |
| 691–694 | 3 | ámbito/lambda anónima | `destination.outputStream().buffered().use {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 695–700 | 2 | `catch` / recuperación de error | `} catch (e: Exception) {` | Captura una excepción del bloque protegido y define recuperación/limpieza en vez de propagarla sin control. |
| 696–698 | 3 | `try` / manejo de error | `try {` | Protege una operación susceptible de excepción. El camino de éxito y el de fallo deben dejar recursos/estado en condiciones coherentes. |
| 698–699 | 3 | `catch` / recuperación de error | `} catch (ignored: Exception) {` | Captura una excepción del bloque protegido y define recuperación/limpieza en vez de propagarla sin control. |
| 702–714 | 1 | ámbito/lambda anónima | `private fun decodeCachedBitmap(file: File): Bitmap? {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 703–705 | 2 | condición `if` | `if (!file.exists() \|\| file.length() <= 0L) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 706–711 | 2 | `try` / manejo de error | `return try {` | Protege una operación susceptible de excepción. El camino de éxito y el de fallo deben dejar recursos/estado en condiciones coherentes. |
| 707–709 | 3 | ámbito/lambda anónima | `val options = BitmapFactory.Options().apply {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 711–713 | 2 | `catch` / recuperación de error | `} catch (e: Exception) {` | Captura una excepción del bloque protegido y define recuperación/limpieza en vez de propagarla sin control. |
| 715–731 | 1 | ámbito/lambda anónima | `private fun decodeSampledBitmap(bytes: ByteArray, requestedSize: Int): Bitmap? {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 716–718 | 2 | ámbito/lambda anónima | `val bounds = BitmapFactory.Options().apply {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 723–725 | 2 | ámbito/lambda anónima | `requestedSize * 2) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 726–729 | 2 | ámbito/lambda anónima | `val options = BitmapFactory.Options().apply {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 732–744 | 1 | ámbito/lambda anónima | `private fun readMediaMetadata(file: File): MediaPreview? {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 733–735 | 2 | condición `if` | `if (!file.exists() \|\| file.length() <= 0L) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 736–741 | 2 | `try` / manejo de error | `return try {` | Protege una operación susceptible de excepción. El camino de éxito y el de fallo deben dejar recursos/estado en condiciones coherentes. |
| 741–743 | 2 | `catch` / recuperación de error | `} catch (e: Exception) {` | Captura una excepción del bloque protegido y define recuperación/limpieza en vez de propagarla sin control. |
| 745–750 | 1 | ámbito/lambda anónima | `private fun writeMediaMetadata(file: File, preview: MediaPreview) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 746–748 | 2 | `try` / manejo de error | `try {` | Protege una operación susceptible de excepción. El camino de éxito y el de fallo deben dejar recursos/estado en condiciones coherentes. |
| 748–749 | 2 | `catch` / recuperación de error | `} catch (ignored: Exception) {` | Captura una excepción del bloque protegido y define recuperación/limpieza en vez de propagarla sin control. |
| 751–809 | 1 | ámbito/lambda anónima | `private fun readDocxPreviewStreaming(context: Context, uri: Uri, maxCharacters: Int): String? {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 752–805 | 2 | `try` / manejo de error | `return try {` | Protege una operación susceptible de excepción. El camino de éxito y el de fallo deben dejar recursos/estado en condiciones coherentes. |
| 753–756 | 3 | condición `if` | `val source = if (uri.scheme == "file") {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 756–758 | 3 | ámbito/lambda anónima | `} else {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 759–803 | 3 | ámbito/lambda anónima | `source.use {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 764–801 | 5 | bucle `while` | `while (entry != null) {` | Repite el cuerpo para elementos/condiciones; el coste crece con la cantidad de entradas y las operaciones internas deben evitar trabajo pesado innecesario en UI. |
| 765–798 | 6 | condición `if` | `if (entry.name == "word/document.xml") {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 772–789 | 8 | selección `when` | `when (event) {` | Despacha comportamiento según un valor/condición. Las ramas representan contratos mutuamente seleccionados y el `else`, si existe, actúa como fallback. |
| 774–782 | 10 | condición `if` | `if (parser.name == "t") {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 776–781 | 11 | condición `if` | `if (text.isNotBlank()) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 777–779 | 12 | condición `if` | `if (result.isNotEmpty() && !result.endsWith(" ") && !result.endsWith("\n")) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 785–787 | 10 | condición `if` | `if (parser.name == "p" && result.isNotEmpty() && !result.endsWith("\n")) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 793–795 | 7 | condición `if` | `return if (clean.isBlank()) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 805–808 | 2 | `catch` / recuperación de error | `} catch (e: Exception) {` | Captura una excepción del bloque protegido y define recuperación/limpieza en vez de propagarla sin control. |
| 810–818 | 1 | ámbito/lambda anónima | `private fun trimPreview(text: String?, maxCharacters: Int): String? {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 811–813 | 2 | condición `if` | `if (text.isNullOrBlank()) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 814–816 | 2 | condición `if` | `if (text.length <= maxCharacters) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |

## 7. Side effects, rendimiento y lifecycle

- **Sistema de archivos / caché:** presente. Cualquier refactor futuro debe considerar el coste y el lifecycle del recurso, no solo el resultado visual.
- **Corrutinas:** presente. Cualquier refactor futuro debe considerar el coste y el lifecycle del recurso, no solo el resultado visual.
- **Recomposición Compose:** presente. Cualquier refactor futuro debe considerar el coste y el lifecycle del recurso, no solo el resultado visual.
- **Decodificación multimedia:** presente. Cualquier refactor futuro debe considerar el coste y el lifecycle del recurso, no solo el resultado visual.

## 8. Relación con los cambios recientes

El cache usa dos niveles de representación en máxima calidad: una variante pequeña **instant** para disponer de pixels rápidamente durante fling y otra variante de calidad final. `peek*Preview` consulta RAM sin iniciar I/O pesado, de modo que una tarjeta que entra al viewport puede dibujar algo ya preparado sin esperar decodificación.

Los `Semaphore` limitan la cantidad de decodificaciones simultáneas. En API 28 se usa un permiso para reducir presión de CPU/RAM/GC; en sistemas más recientes se permiten dos. Aumentar estos límites puede empeorar el scroll aunque reduzca el tiempo total de precarga.

## 9. Regla de mantenimiento

Cualquier modificación futura debería actualizar primero el archivo Kotlin real y después regenerar esta documentación. **No debe editarse el código para que coincida con el documento; el documento es el derivado y el código es la fuente de verdad.**

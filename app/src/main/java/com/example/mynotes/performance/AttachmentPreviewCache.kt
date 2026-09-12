package com.example.mynotes.performance

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.pdf.PdfRenderer
import android.media.ExifInterface
import android.media.MediaMetadataRetriever
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Build
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.util.LruCache
import android.util.Xml
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import org.xmlpull.v1.XmlPullParser
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.security.MessageDigest
import java.util.zip.ZipInputStream
import kotlin.math.max

/*
 * ============================================================
 * CACHÉ DE PREVISUALIZACIONES
 * ============================================================
 *
 * Objetivos:
 *
 * - Evitar decodificar la imagen original en cada miniatura.
 * - Evitar abrir el mismo video en cada recomposición.
 * - Evitar extraer repetidamente carátulas de audio.
 * - Evitar descomprimir el mismo DOCX cada vez.
 * - Evitar renderizar la primera página de un PDF repetidamente.
 *
 * Todos los archivos generados viven en cacheDir. Android puede
 * eliminarlos cuando necesita espacio y la app los recreará.
 */
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

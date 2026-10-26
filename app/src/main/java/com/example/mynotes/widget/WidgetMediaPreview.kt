package com.example.mynotes.widget

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Shader
import android.media.MediaMetadataRetriever
import android.net.Uri
import com.example.mynotes.data.Attachment
import com.example.mynotes.data.Note
import com.example.mynotes.links.LinkPreviewRepository
import java.io.File
import java.io.InputStream
import java.security.MessageDigest
import org.json.JSONObject
import kotlin.math.min

object WidgetMediaPreview {
    private const val MAX_WIDGET_VISUALS = 6
    private const val LINK_PREVIEW_CACHE_VERSION = 6
    private const val LINK_PREVIEW_CACHE_MAX_AGE_MS = 14L * 24L * 60L * 60L * 1000L
    private const val THUMBNAIL_CORNER_RADIUS_DP = 10f

    fun visualsByNote(attachments: List<Attachment>): Map<Int, List<Attachment>> {
        return attachments
            .asSequence()
            .filter { (it.type == "image" || it.type == "video") && it.uri.isNotBlank() }
            .groupBy { it.noteId }
    }

    fun loadBestPreviewBitmap(
        context: Context,
        note: Note,
        attachments: List<Attachment>?,
        reqWidth: Int = 160,
        reqHeight: Int = 120
    ): Bitmap? {
        val visuals = attachments.orEmpty()
        val rawBitmap = if (visuals.isNotEmpty()) {
            loadVisualCollageBitmap(context, visuals, reqWidth, reqHeight)
        } else {
            val normalizedUrl = LinkPreviewRepository.normalizeUrl(note.content)
                ?: LinkPreviewRepository.normalizeUrl(note.title)
                ?: return null
            val cachedFile = cachedLinkPreviewFile(context.applicationContext, normalizedUrl) ?: return null
            runCatching {
                decodeSampledBitmap(context.contentResolver, Uri.fromFile(cachedFile), reqWidth, reqHeight)
            }.getOrNull()
        } ?: return null

        return roundOuterCorners(context, rawBitmap)
    }

    private fun loadVisualCollageBitmap(
        context: Context,
        attachments: List<Attachment>,
        reqWidth: Int,
        reqHeight: Int
    ): Bitmap? {
        if (reqWidth <= 0 || reqHeight <= 0) return null

        val visible = attachments.take(MAX_WIDGET_VISUALS)
        if (visible.isEmpty()) return null
        if (visible.size == 1) {
            return loadPreviewBitmap(context, visible.first(), reqWidth, reqHeight)
        }

        val cells = collageCells(visible.size, reqWidth, reqHeight)
        val output = Bitmap.createBitmap(reqWidth, reqHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        var loadedCount = 0

        visible.forEachIndexed { index, attachment ->
            val cell = cells[index]
            val bitmap = loadPreviewBitmap(
                context = context,
                attachment = attachment,
                reqWidth = cell.width().coerceAtLeast(1),
                reqHeight = cell.height().coerceAtLeast(1)
            ) ?: return@forEachIndexed

            canvas.drawBitmap(bitmap, null, cell, paint)
            loadedCount += 1
            runCatching { bitmap.recycle() }
        }

        if (loadedCount == 0) {
            runCatching { output.recycle() }
            return null
        }

        val hiddenCount = attachments.size - visible.size
        if (hiddenCount > 0 && cells.isNotEmpty()) {
            drawMoreCount(canvas, cells.last(), hiddenCount)
        }
        return output
    }

    private fun collageCells(count: Int, width: Int, height: Int): List<Rect> {
        if (count <= 1) return listOf(Rect(0, 0, width, height))
        if (count == 2) {
            val split = width / 2
            return listOf(
                Rect(0, 0, split, height),
                Rect(split, 0, width, height)
            )
        }
        if (count == 3) {
            val split = width / 2
            val halfHeight = height / 2
            return listOf(
                Rect(0, 0, split, height),
                Rect(split, 0, width, halfHeight),
                Rect(split, halfHeight, width, height)
            )
        }
        if (count == 4) {
            val halfWidth = width / 2
            val halfHeight = height / 2
            return listOf(
                Rect(0, 0, halfWidth, halfHeight),
                Rect(halfWidth, 0, width, halfHeight),
                Rect(0, halfHeight, halfWidth, height),
                Rect(halfWidth, halfHeight, width, height)
            )
        }
        if (count == 5) {
            val halfWidth = width / 2
            val halfHeight = height / 2
            val firstThird = width / 3
            val secondThird = (width * 2) / 3
            return listOf(
                Rect(0, 0, halfWidth, halfHeight),
                Rect(halfWidth, 0, width, halfHeight),
                Rect(0, halfHeight, firstThird, height),
                Rect(firstThird, halfHeight, secondThird, height),
                Rect(secondThird, halfHeight, width, height)
            )
        }

        val columns = 3
        val rows = 2
        val cellWidth = width.toFloat() / columns
        val cellHeight = height.toFloat() / rows
        val result = ArrayList<Rect>(count)

        repeat(minOf(count, 3)) { column ->
            val left = (column * cellWidth).toInt()
            val right = if (column == columns - 1) width else ((column + 1) * cellWidth).toInt()
            result += Rect(left, 0, right, cellHeight.toInt())
        }

        val secondRowCount = (count - 3).coerceIn(0, 3)
        val startX = if (secondRowCount == 2) cellWidth / 2f else 0f
        repeat(secondRowCount) { column ->
            val left = (startX + column * cellWidth).toInt()
            val right = (startX + (column + 1) * cellWidth).toInt().coerceAtMost(width)
            result += Rect(left, cellHeight.toInt(), right, height)
        }
        return result
    }

    private fun drawMoreCount(canvas: Canvas, cell: Rect, hiddenCount: Int) {
        val overlayPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(165, 0, 0, 0)
            style = Paint.Style.FILL
        }
        canvas.drawRect(cell.left.toFloat(), cell.top.toFloat(), cell.right.toFloat(), cell.bottom.toFloat(), overlayPaint)

        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textAlign = Paint.Align.CENTER
            textSize = min(cell.width(), cell.height()) * 0.34f
            isFakeBoldText = true
        }
        val label = "+$hiddenCount"
        val metrics = textPaint.fontMetrics
        val baseline = cell.centerY() - (metrics.ascent + metrics.descent) / 2f
        canvas.drawText(label, cell.exactCenterX(), baseline, textPaint)
    }

    /**
     * Reutiliza exactamente la caché persistida por LinkPreviewCard.
     * La app guarda el metadata en SharedPreferences y la miniatura descargada
     * en link_preview_thumbnails_v6; el widget no vuelve a descargar la imagen.
     */
    private fun cachedLinkPreviewFile(context: Context, normalizedUrl: String): File? {
        val prefs = context.getSharedPreferences(
            "link_preview_cache_v$LINK_PREVIEW_CACHE_VERSION",
            Context.MODE_PRIVATE
        )
        val cacheKey = "preview_${sha256(normalizedUrl)}"
        val raw = prefs.getString(cacheKey, null)

        if (!raw.isNullOrBlank()) {
            runCatching { JSONObject(raw) }.getOrNull()?.let { json ->
                val savedAt = json.optLong("savedAt", 0L)
                val cacheIsFresh = savedAt > 0L &&
                    System.currentTimeMillis() - savedAt <= LINK_PREVIEW_CACHE_MAX_AGE_MS
                if (cacheIsFresh) {
                    json.optString("cachedImagePath")
                        .takeIf { it.isNotBlank() && it != "null" }
                        ?.let(::File)
                        ?.takeIf { it.isFile && it.length() > 0L }
                        ?.let { return it }

                    val candidateUrls = buildList {
                        json.optString("imageUrl")
                            .takeIf { it.startsWith("http://", true) || it.startsWith("https://", true) }
                            ?.let(::add)
                        json.optJSONArray("imageCandidates")?.let { array ->
                            for (index in 0 until array.length()) {
                                array.optString(index)
                                    .takeIf { it.startsWith("http://", true) || it.startsWith("https://", true) }
                                    ?.let(::add)
                            }
                        }
                    }
                    val thumbnailDir = File(context.filesDir, "link_preview_thumbnails_v$LINK_PREVIEW_CACHE_VERSION")
                    candidateUrls.asSequence()
                        .distinct()
                        .map { remoteUrl -> File(thumbnailDir, "${sha256(remoteUrl)}.img") }
                        .firstOrNull { it.isFile && it.length() > 0L }
                        ?.let { return it }
                }
            }
        }

        // Compatibilidad con previews muy antiguos, si todavía existen.
        val legacy = File(File(context.filesDir, "link_previews"), "${sha256(normalizedUrl)}.img")
        return legacy.takeIf { it.isFile && it.length() > 0L }
    }

    /**
     * Redondea el bitmap final (imagen, collage o preview de enlace) para que
     * los launchers que no recortan el ImageView por su background no muestren
     * esquinas rectas/puntiagudas.
     */
    private fun roundOuterCorners(context: Context, source: Bitmap): Bitmap {
        if (source.width <= 0 || source.height <= 0) return source
        val requestedRadiusPx = THUMBNAIL_CORNER_RADIUS_DP * context.resources.displayMetrics.density
        val radiusPx = requestedRadiusPx.coerceAtMost(min(source.width, source.height) / 2f)
        if (radiusPx <= 0f) return source

        val output = Bitmap.createBitmap(source.width, source.height, Bitmap.Config.ARGB_8888)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG).apply {
            shader = BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        }
        Canvas(output).drawRoundRect(
            0f,
            0f,
            source.width.toFloat(),
            source.height.toFloat(),
            radiusPx,
            radiusPx,
            paint
        )
        if (!source.isRecycled) {
            runCatching { source.recycle() }
        }
        return output
    }

    private fun sha256(text: String): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(text.toByteArray(Charsets.UTF_8))
        return buildString(digest.size * 2) {
            digest.forEach { append("%02x".format(it)) }
        }
    }

    fun loadPreviewBitmap(context: Context, attachment: Attachment?, reqWidth: Int = 160, reqHeight: Int = 120): Bitmap? {
        attachment ?: return null
        return runCatching {
            when (attachment.type) {
                "video" -> loadVideoFrame(context, Uri.parse(attachment.uri), reqWidth, reqHeight)
                else -> decodeSampledBitmap(context.contentResolver, Uri.parse(attachment.uri), reqWidth, reqHeight)
            }
        }.getOrNull()
    }

    private fun loadVideoFrame(context: Context, uri: Uri, reqWidth: Int, reqHeight: Int): Bitmap? {
        val retriever = MediaMetadataRetriever()
        return try {
            when (uri.scheme) {
                "file" -> retriever.setDataSource(uri.path)
                else -> retriever.setDataSource(context, uri)
            }
            val raw = retriever.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST_SYNC) ?: return null
            scaleCenterCrop(raw, reqWidth, reqHeight)
        } finally {
            runCatching { retriever.release() }
        }
    }

    private fun decodeSampledBitmap(
        resolver: ContentResolver,
        uri: Uri,
        reqWidth: Int,
        reqHeight: Int
    ): Bitmap? {
        if (uri.scheme == "file") {
            val path = uri.path ?: return null
            val file = File(path)
            if (!file.exists()) return null
            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeFile(path, bounds)
            val options = BitmapFactory.Options().apply {
                inSampleSize = calculateInSampleSize(bounds, reqWidth, reqHeight)
                inPreferredConfig = Bitmap.Config.RGB_565
            }
            return BitmapFactory.decodeFile(path, options)?.let { scaleCenterCrop(it, reqWidth, reqHeight) }
        }

        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
        val options = BitmapFactory.Options().apply {
            inSampleSize = calculateInSampleSize(bounds, reqWidth, reqHeight)
            inPreferredConfig = Bitmap.Config.RGB_565
        }
        resolver.openInputStream(uri)?.use { stream: InputStream ->
            return BitmapFactory.decodeStream(stream, null, options)?.let { scaleCenterCrop(it, reqWidth, reqHeight) }
        }
        return null
    }

    /**
     * Amplía únicamente la imagen adjunta hasta cubrir por completo el área de
     * miniatura. Conserva su relación de aspecto y recorta de forma centrada
     * solo el excedente necesario; no crea una segunda copia como fondo.
     */
    private fun scaleCenterCrop(source: Bitmap, reqWidth: Int, reqHeight: Int): Bitmap {
        if (source.width <= 0 || source.height <= 0 || reqWidth <= 0 || reqHeight <= 0) return source

        val sourceAspect = source.width.toFloat() / source.height.toFloat()
        val targetAspect = reqWidth.toFloat() / reqHeight.toFloat()

        val sourceRect = if (sourceAspect > targetAspect) {
            val cropWidth = (source.height * targetAspect).toInt().coerceIn(1, source.width)
            val left = ((source.width - cropWidth) / 2).coerceAtLeast(0)
            Rect(left, 0, left + cropWidth, source.height)
        } else {
            val cropHeight = (source.width / targetAspect).toInt().coerceIn(1, source.height)
            val top = ((source.height - cropHeight) / 2).coerceAtLeast(0)
            Rect(0, top, source.width, top + cropHeight)
        }

        val output = Bitmap.createBitmap(reqWidth, reqHeight, Bitmap.Config.ARGB_8888)
        Canvas(output).drawBitmap(
            source,
            sourceRect,
            Rect(0, 0, reqWidth, reqHeight),
            Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        )
        if (!source.isRecycled) {
            runCatching { source.recycle() }
        }
        return output
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize.coerceAtLeast(1)
    }
}

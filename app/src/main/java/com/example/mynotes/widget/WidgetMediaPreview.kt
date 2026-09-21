package com.example.mynotes.widget

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.media.MediaMetadataRetriever
import android.net.Uri
import com.example.mynotes.data.Attachment
import com.example.mynotes.data.Note
import com.example.mynotes.links.LinkPreviewRepository
import java.io.File
import java.io.InputStream
import java.security.MessageDigest

object WidgetMediaPreview {

    fun firstVisualByNote(attachments: List<Attachment>): Map<Int, Attachment> {
        return attachments
            .asSequence()
            .filter { (it.type == "image" || it.type == "video") && it.uri.isNotBlank() }
            .groupBy { it.noteId }
            .mapValues { (_, list) -> list.first() }
    }

    fun loadBestPreviewBitmap(
        context: Context,
        note: Note,
        attachment: Attachment?,
        reqWidth: Int = 160,
        reqHeight: Int = 120
    ): Bitmap? {
        loadPreviewBitmap(context, attachment, reqWidth, reqHeight)?.let { return it }

        val normalizedUrl = LinkPreviewRepository.normalizeUrl(note.content)
            ?: LinkPreviewRepository.normalizeUrl(note.title)
            ?: return null

        val cachedFile = cachedLinkPreviewFile(context.applicationContext, normalizedUrl) ?: return null
        return runCatching {
            decodeSampledBitmap(context.contentResolver, Uri.fromFile(cachedFile), reqWidth, reqHeight)
        }.getOrNull()
    }

    private fun cachedLinkPreviewFile(context: Context, normalizedUrl: String): File? {
        val key = sha256(normalizedUrl)
        val imageFile = File(File(context.filesDir, "link_previews"), "$key.img")
        return imageFile.takeIf { it.isFile && it.length() > 0L }
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
            scaleFitInside(raw, reqWidth, reqHeight)
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
            return BitmapFactory.decodeFile(path, options)?.let { scaleFitInside(it, reqWidth, reqHeight) }
        }

        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
        val options = BitmapFactory.Options().apply {
            inSampleSize = calculateInSampleSize(bounds, reqWidth, reqHeight)
            inPreferredConfig = Bitmap.Config.RGB_565
        }
        resolver.openInputStream(uri)?.use { stream: InputStream ->
            return BitmapFactory.decodeStream(stream, null, options)?.let { scaleFitInside(it, reqWidth, reqHeight) }
        }
        return null
    }

    private fun scaleFitInside(source: Bitmap, reqWidth: Int, reqHeight: Int): Bitmap {
        if (source.width <= 0 || source.height <= 0 || reqWidth <= 0 || reqHeight <= 0) return source
        val scale = minOf(reqWidth.toFloat() / source.width, reqHeight.toFloat() / source.height)
        val scaledWidth = (source.width * scale).toInt().coerceAtLeast(1)
        val scaledHeight = (source.height * scale).toInt().coerceAtLeast(1)
        val scaled = Bitmap.createScaledBitmap(source, scaledWidth, scaledHeight, true)
        val output = Bitmap.createBitmap(reqWidth, reqHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val left = ((reqWidth - scaledWidth) / 2f)
        val top = ((reqHeight - scaledHeight) / 2f)
        canvas.drawBitmap(scaled, left, top, null)
        if (scaled !== source) runCatching { scaled.recycle() }
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

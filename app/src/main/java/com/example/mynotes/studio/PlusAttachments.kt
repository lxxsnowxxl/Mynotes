package com.example.mynotes.studio

import android.content.Context
import android.graphics.Bitmap
import android.media.*
import android.net.Uri
import android.os.Build
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.example.mynotes.data.*
import kotlinx.coroutines.*
import java.io.File
import java.nio.ByteBuffer
import java.security.MessageDigest
import java.util.UUID

internal object PlusAttachmentEngine {
    fun key(uri: String) = MessageDigest.getInstance("SHA-256").digest(uri.toByteArray()).joinToString("") { "%02x".format(it) }
    data class Clip(val file: File, val startUs: Long, val endUs: Long)
    suspend fun trim(context: Context, uri: Uri, start: Long, end: Long): Clip = withContext(Dispatchers.IO) {
        require(start >= 0 && end > start && end - start <= 7_200_000_000L) { "Choose a clip up to two hours" }
        val extractor = MediaExtractor()
        val file = File(context.cacheDir, "media_export/${UUID.randomUUID()}.mp4").apply { parentFile!!.mkdirs() }
        var muxer: MediaMuxer? = null
        try {
            extractor.setDataSource(context, uri, null)
            val tracks = (0 until extractor.trackCount).filter { val mime = extractor.getTrackFormat(it).getString(MediaFormat.KEY_MIME).orEmpty(); mime.startsWith("audio/") || mime.startsWith("video/") }
            require(tracks.isNotEmpty() && tracks.size <= 16) { "No supported audio or video tracks" }
            val output = MediaMuxer(file.path, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4); muxer = output
            val map = tracks.associateWith { track -> extractor.selectTrack(track); output.addTrack(extractor.getTrackFormat(track)) }
            output.start()
            extractor.seekTo(start, MediaExtractor.SEEK_TO_PREVIOUS_SYNC)
            val actualStart = extractor.sampleTime.coerceAtLeast(0)
            require(actualStart < end) { "No samples in the selected range" }
            val maximum = tracks.maxOf { val f = extractor.getTrackFormat(it); if (f.containsKey(MediaFormat.KEY_MAX_INPUT_SIZE)) f.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE) else 4 * 1024 * 1024 }.coerceIn(1024 * 1024, 32 * 1024 * 1024)
            val buffer = ByteBuffer.allocate(maximum); val info = MediaCodec.BufferInfo()
            var last = actualStart; var count = 0
            while (extractor.sampleTime >= 0 && extractor.sampleTime < end) {
                currentCoroutineContext().ensureActive()
                require(extractor.sampleFlags and MediaExtractor.SAMPLE_FLAG_ENCRYPTED == 0) { "Encrypted media cannot be trimmed" }
                if (Build.VERSION.SDK_INT >= 28) require(extractor.sampleSize <= buffer.capacity()) { "Media sample is too large" }
                buffer.clear(); val size = extractor.readSampleData(buffer, 0); if (size < 0) break
                val time = extractor.sampleTime
                if (time >= actualStart) {
                    info.set(0, size, time - actualStart, if (extractor.sampleFlags and MediaExtractor.SAMPLE_FLAG_SYNC != 0) MediaCodec.BUFFER_FLAG_KEY_FRAME else 0)
                    output.writeSampleData(map.getValue(extractor.sampleTrackIndex), buffer, info)
                    last = maxOf(last, time); count++
                }
                require(file.length() <= 512L * 1024 * 1024) { "Export exceeds 512 MB" }
                extractor.advance()
            }
            require(count > 0) { "No samples in the selected range" }
            output.stop(); output.release(); muxer = null
            Clip(file, actualStart, last)
        } catch (e: Throwable) { file.delete(); throw e } finally { runCatching { muxer?.stop() }; muxer?.release(); extractor.release() }
    }
    @Suppress("DEPRECATION") suspend fun webp(context: Context, uri: Uri, quality: Int): File = withContext(Dispatchers.IO) {
        val bitmap = com.example.mynotes.ui.pdf.PdfDocumentEngine.loadImage(context, uri)
        val file = File(context.cacheDir, "media_export/${UUID.randomUUID()}.webp").apply { parentFile!!.mkdirs() }
        try {
            file.outputStream().use { check(bitmap.compress(if (Build.VERSION.SDK_INT >= 30) Bitmap.CompressFormat.WEBP_LOSSY else Bitmap.CompressFormat.WEBP, quality.coerceIn(0, 100), it)) }
            file
        } catch (e: Throwable) { file.delete(); throw e } finally { bitmap.recycle() }
    }
}

@Composable internal fun plusImageDescription(uri: String, fallback: String?): String {
    val context = LocalContext.current
    val store = remember { StudioStore.get(context) }
    val key = remember(uri) { PlusAttachmentEngine.key(uri) }
    val description = rememberStudioRow(store, "attachment_alts", key)
    val preview = com.example.mynotes.ui.preview.LocalLayoutPreview.current
    LaunchedEffect(Unit) { if (preview == null) store.load() }
    return description.optString("text").ifBlank { fallback ?: "Note image" }
}

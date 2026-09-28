package com.example.mynotes.ui.pdf

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import com.google.mlkit.common.MlKitException
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.segmentation.subject.SubjectSegmentation
import com.google.mlkit.vision.segmentation.subject.SubjectSegmentationResult
import com.google.mlkit.vision.segmentation.subject.SubjectSegmenterOptions
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import java.nio.FloatBuffer
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/** Recorte local mediante ML Kit Subject Segmentation. */
object PdfSubjectCropper {
    private const val MIN_INFERENCE_SIDE = 512
    private const val MAX_INFERENCE_SIDE = 1800
    private const val RETRY_CANVAS_SIDE = 1024
    private const val FINAL_RETRY_CANVAS_SIDE = 768
    private const val MODEL_READY_RETRIES = 5
    private const val MASK_EDGE_LOW = 0.20f
    private const val MASK_EDGE_HIGH = 0.72f

    suspend fun isolateAndCrop(
        context: Context,
        bitmap: Bitmap,
        onModelDownload: suspend () -> Unit = {}
    ): Bitmap {
        val source = bitmap.asSoftwareArgb8888()
        var lastError: Throwable? = null
        try {
            // Los reintentos se crean de forma perezosa. V200/V202 construía tres bitmaps
            // grandes antes de la primera inferencia, elevando memoria/GC innecesariamente.
            val frames = listOf<() -> InferenceFrame>(
                { InferenceFrame(prepareForInference(source)) },
                { prepareSquareRetry(source, RETRY_CANVAS_SIDE) },
                { prepareSquareRetry(source, FINAL_RETRY_CANVAS_SIDE) }
            )

            frames.forEachIndexed { index, createFrame ->
                val frame = createFrame()
                try {
                    return segmentPrecise(context, frame, source, onModelDownload)
                } catch (error: CancellationException) {
                    throw error
                } catch (error: Throwable) {
                    lastError = error
                    if (!error.canRetrySegmentation()) throw error
                    if (index < frames.lastIndex) delay(300L * (index + 1))
                } finally {
                    frame.recycleInputUnless(source)
                }
            }

            // Última ruta: bitmaps individuales por sujeto y recomposición. Se crea sólo
            // si todos los caminos anteriores fallaron, evitando mantener copias de 1024².
            val fallbackFrame = prepareSquareRetry(source, RETRY_CANVAS_SIDE)
            try {
                val foreground = segmentBySubjects(context, fallbackFrame.bitmap, onModelDownload)
                return fallbackFrame.restore(foreground, source.width, source.height)
            } catch (error: CancellationException) {
                throw error
            } catch (error: Throwable) {
                lastError?.let(error::addSuppressed)
                throw error
            } finally {
                fallbackFrame.recycleInputUnless(source)
            }
        } finally {
            if (source !== bitmap && !source.isRecycled) source.recycle()
        }
    }

    /**
     * La ruta principal usa únicamente la confidence mask y conserva los RGB originales.
     * Esto evita volver a escalar la foto recortada y permite un borde subpíxel más limpio.
     * Si el grafo de máscara falla en una versión concreta de Play services, se conserva la
     * ruta foregroundBitmap como respaldo automático.
     */
    private suspend fun segmentPrecise(
        context: Context,
        frame: InferenceFrame,
        source: Bitmap,
        onModelDownload: suspend () -> Unit
    ): Bitmap = try {
        val options = SubjectSegmenterOptions.Builder()
            .enableForegroundConfidenceMask()
            .build()
        val mask = processWithModelRetries(context, options, frame.bitmap, onModelDownload)
            .foregroundConfidenceMask ?: error("ML Kit did not return a foreground confidence mask")
        frame.applyConfidenceMask(mask, source)
    } catch (error: CancellationException) {
        throw error
    } catch (error: Throwable) {
        if (!error.canRetrySegmentation()) throw error
        val options = SubjectSegmenterOptions.Builder()
            .enableForegroundBitmap()
            .build()
        val foreground = processWithModelRetries(context, options, frame.bitmap, onModelDownload)
            .foregroundBitmap ?: error("ML Kit did not return a foreground bitmap")
        frame.restore(foreground, source.width, source.height)
    }

    private suspend fun segmentBySubjects(
        context: Context,
        bitmap: Bitmap,
        onModelDownload: suspend () -> Unit
    ): Bitmap {
        val subjectOptions = SubjectSegmenterOptions.SubjectResultOptions.Builder()
            .enableSubjectBitmap()
            .build()
        val options = SubjectSegmenterOptions.Builder()
            .enableMultipleSubjects(subjectOptions)
            .build()
        val subjects = processWithModelRetries(context, options, bitmap, onModelDownload).subjects
        if (subjects.isEmpty()) error("ML Kit did not return segmented subjects")

        val output = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        var drawn = false
        Canvas(output).apply {
            drawColor(Color.TRANSPARENT)
            subjects.forEach { subject ->
                val subjectBitmap = subject.bitmap ?: return@forEach
                val left = subject.startX.coerceIn(0, bitmap.width)
                val top = subject.startY.coerceIn(0, bitmap.height)
                val right = (left + subject.width).coerceIn(left, bitmap.width)
                val bottom = (top + subject.height).coerceIn(top, bitmap.height)
                if (right > left && bottom > top) {
                    drawBitmap(subjectBitmap, null, Rect(left, top, right, bottom), paint)
                    drawn = true
                }
            }
        }
        if (!drawn) error("ML Kit returned subjects without bitmaps")
        return output
    }

    private suspend fun processWithModelRetries(
        context: Context,
        options: SubjectSegmenterOptions,
        bitmap: Bitmap,
        onModelDownload: suspend () -> Unit
    ): SubjectSegmentationResult {
        var prepared = false
        var lastError: Throwable? = null

        repeat(MODEL_READY_RETRIES) { attempt ->
            val segmenter = SubjectSegmentation.getClient(options)
            try {
                if (!prepared) {
                    PdfSubjectModel.prepare(context, segmenter, onModelDownload)
                    prepared = true
                }
                return process(segmenter, bitmap)
            } catch (error: CancellationException) {
                throw error
            } catch (error: Throwable) {
                lastError = error
                val waiting = PdfSubjectModel.isTemporarilyUnavailable(error)
                if (!waiting || attempt == MODEL_READY_RETRIES - 1) {
                    throw if (waiting) PdfSubjectModel.ModelUnavailableException(error) else error
                }
                delay(500L * (1 shl attempt.coerceAtMost(3)))
            } finally {
                segmenter.close()
            }
        }
        throw PdfSubjectModel.ModelUnavailableException(lastError)
    }

    private suspend fun process(
        segmenter: com.google.mlkit.vision.segmentation.subject.SubjectSegmenter,
        bitmap: Bitmap
    ): SubjectSegmentationResult = suspendCancellableCoroutine { continuation ->
        segmenter.process(InputImage.fromBitmap(bitmap, 0))
            .addOnSuccessListener { result ->
                if (continuation.isActive) continuation.resume(result)
            }
            .addOnFailureListener { error ->
                if (continuation.isActive) continuation.resumeWithException(error)
            }
            .addOnCanceledListener {
                if (continuation.isActive) continuation.cancel()
            }
    }

    private fun prepareForInference(source: Bitmap): Bitmap {
        val width = source.width.coerceAtLeast(1)
        val height = source.height.coerceAtLeast(1)
        val minSide = min(width, height)
        val maxSide = max(width, height)
        val scaleUp = MIN_INFERENCE_SIDE.toFloat() / minSide
        val scaleDown = MAX_INFERENCE_SIDE.toFloat() / maxSide
        val scale = when {
            maxSide > MAX_INFERENCE_SIDE -> scaleDown
            minSide < MIN_INFERENCE_SIDE && maxSide * scaleUp <= MAX_INFERENCE_SIDE -> scaleUp
            else -> 1f
        }
        val targetWidth = (width * scale).roundToInt().coerceAtLeast(1)
        val targetHeight = (height * scale).roundToInt().coerceAtLeast(1)
        return if (targetWidth == width && targetHeight == height) source
        else Bitmap.createScaledBitmap(source, targetWidth, targetHeight, true)
    }

    private fun prepareSquareRetry(source: Bitmap, side: Int): InferenceFrame {
        val scale = min(side.toFloat() / source.width, side.toFloat() / source.height)
        val width = (source.width * scale).roundToInt().coerceIn(1, side)
        val height = (source.height * scale).roundToInt().coerceIn(1, side)
        val left = (side - width) / 2
        val top = (side - height) / 2
        val canvasBitmap = Bitmap.createBitmap(side, side, Bitmap.Config.ARGB_8888)
        Canvas(canvasBitmap).apply {
            drawColor(Color.BLACK)
            drawBitmap(
                source,
                null,
                Rect(left, top, left + width, top + height),
                Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
            )
        }
        return InferenceFrame(canvasBitmap, Rect(left, top, left + width, top + height))
    }

    private fun Bitmap.asSoftwareArgb8888(): Bitmap {
        check(!isRecycled) { "Could not prepare recycled bitmap for subject segmentation" }
        return Bitmap.createBitmap(width.coerceAtLeast(1), height.coerceAtLeast(1), Bitmap.Config.ARGB_8888).also { normalized ->
            Canvas(normalized).drawBitmap(this, 0f, 0f, Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG))
            normalized.setPremultiplied(true)
        }
    }

    private fun Throwable.canRetrySegmentation(): Boolean = when (this) {
        is Error,
        is PdfSubjectModel.PlayServicesUnavailableException,
        is PdfSubjectModel.ModelUnavailableException -> false
        is MlKitException -> errorCode != MlKitException.UNAVAILABLE
        else -> true
    }

    private data class InferenceFrame(
        val bitmap: Bitmap,
        val content: Rect = Rect(0, 0, bitmap.width, bitmap.height)
    ) {
        /**
         * Reproyecta la máscara a la resolución original y sólo sustituye alpha. Los RGB
         * proceden del bitmap fuente, así que no se pierde nitidez por downscale/upscale.
         */
        fun applyConfidenceMask(mask: FloatBuffer, source: Bitmap): Bitmap {
            val expected = bitmap.width * bitmap.height
            require(mask.capacity() >= expected) {
                "Invalid ML Kit confidence mask: ${mask.capacity()} < $expected"
            }
            val output = Bitmap.createBitmap(source.width, source.height, Bitmap.Config.ARGB_8888)
            val row = IntArray(source.width)
            val frameWidth = bitmap.width
            val frameHeight = bitmap.height
            val contentWidth = content.width().coerceAtLeast(1)
            val contentHeight = content.height().coerceAtLeast(1)

            for (y in 0 until source.height) {
                source.getPixels(row, 0, source.width, 0, y, source.width, 1)
                val fy = (content.top + (y + 0.5f) * contentHeight / source.height - 0.5f)
                    .coerceIn(0f, frameHeight - 1f)
                val y0 = fy.toInt()
                val y1 = (y0 + 1).coerceAtMost(frameHeight - 1)
                val ty = fy - y0
                for (x in row.indices) {
                    val fx = (content.left + (x + 0.5f) * contentWidth / source.width - 0.5f)
                        .coerceIn(0f, frameWidth - 1f)
                    val x0 = fx.toInt()
                    val x1 = (x0 + 1).coerceAtMost(frameWidth - 1)
                    val tx = fx - x0
                    val top = lerp(mask.get(y0 * frameWidth + x0), mask.get(y0 * frameWidth + x1), tx)
                    val bottom = lerp(mask.get(y1 * frameWidth + x0), mask.get(y1 * frameWidth + x1), tx)
                    val confidence = lerp(top, bottom, ty).coerceIn(0f, 1f)
                    val alpha = (Color.alpha(row[x]) * smoothMaskAlpha(confidence)).roundToInt().coerceIn(0, 255)
                    row[x] = (row[x] and 0x00FFFFFF) or (alpha shl 24)
                }
                output.setPixels(row, 0, source.width, 0, y, source.width, 1)
            }
            return output
        }

        fun restore(foreground: Bitmap, sourceWidth: Int, sourceHeight: Int): Bitmap {
            val normalized = if (foreground.width == bitmap.width && foreground.height == bitmap.height) {
                foreground
            } else {
                Bitmap.createScaledBitmap(foreground, bitmap.width, bitmap.height, true)
            }
            val contentBitmap = if (
                content.left == 0 && content.top == 0 &&
                content.right == bitmap.width && content.bottom == bitmap.height
            ) normalized else Bitmap.createBitmap(
                normalized,
                content.left,
                content.top,
                content.width(),
                content.height()
            )
            return if (contentBitmap.width == sourceWidth && contentBitmap.height == sourceHeight) {
                contentBitmap
            } else {
                Bitmap.createScaledBitmap(contentBitmap, sourceWidth, sourceHeight, true)
            }
        }

        fun recycleInputUnless(source: Bitmap) {
            if (bitmap !== source && !bitmap.isRecycled) bitmap.recycle()
        }
    }

    private fun smoothMaskAlpha(confidence: Float): Float {
        val t = ((confidence - MASK_EDGE_LOW) / (MASK_EDGE_HIGH - MASK_EDGE_LOW)).coerceIn(0f, 1f)
        return t * t * (3f - 2f * t)
    }

    private fun lerp(start: Float, end: Float, fraction: Float): Float =
        start + (end - start) * fraction
}

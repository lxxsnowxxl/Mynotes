package com.example.mynotes.ui.pdf

import android.graphics.Bitmap
import android.graphics.Color
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.segmentation.subject.SubjectSegmentation
import com.google.mlkit.vision.segmentation.subject.SubjectSegmenterOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import java.nio.FloatBuffer
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Recorte inteligente local mediante ML Kit Subject Segmentation.
 *
 * La inferencia usa una copia dimensionada para el modelo, pero el resultado
 * final se reconstruye sobre los píxeles de la imagen original. De ese modo
 * se obtiene una máscara suave sin sacrificar resolución al guardar el recorte.
 */
object PdfSubjectCropper {
    private const val MIN_INFERENCE_SIDE = 512
    private const val MAX_INFERENCE_SIDE = 2048
    private const val HARD_BACKGROUND = 0.16f
    private const val HARD_FOREGROUND = 0.84f

    suspend fun isolateAndCrop(bitmap: Bitmap): Bitmap {
        val source = if (bitmap.config == Bitmap.Config.ARGB_8888) {
            bitmap
        } else {
            bitmap.copy(Bitmap.Config.ARGB_8888, false)
        }
        val inferenceBitmap = prepareForInference(source)

        val options = SubjectSegmenterOptions.Builder()
            .enableForegroundConfidenceMask()
            .enableForegroundBitmap()
            .build()
        val segmenter = SubjectSegmentation.getClient(options)

        return try {
            val result = suspendCancellableCoroutine<CropSegmentationResult> { continuation ->
                segmenter.process(InputImage.fromBitmap(inferenceBitmap, 0))
                    .addOnSuccessListener { segmentation ->
                        val confidence = segmentation.foregroundConfidenceMask
                        val fallback = segmentation.foregroundBitmap
                        when {
                            confidence != null && continuation.isActive -> continuation.resume(
                                CropSegmentationResult(
                                    confidence = confidence.toFloatArrayCopy(),
                                    fallbackForeground = fallback
                                )
                            )
                            fallback != null && continuation.isActive -> continuation.resume(
                                CropSegmentationResult(null, fallback)
                            )
                            continuation.isActive -> continuation.resumeWithException(
                                IllegalStateException("ML Kit did not return a foreground result")
                            )
                        }
                    }
                    .addOnFailureListener { error ->
                        if (continuation.isActive) continuation.resumeWithException(error)
                    }
            }

            val refined = result.confidence?.let { confidence ->
                runCatching {
                    if (confidence.size == inferenceBitmap.width * inferenceBitmap.height) {
                        buildRefinedForeground(
                            source = source,
                            inferenceWidth = inferenceBitmap.width,
                            inferenceHeight = inferenceBitmap.height,
                            confidence = confidence
                        )
                    } else null
                }.getOrNull()
            }

            val fallback = result.fallbackForeground?.let { foreground ->
                // foregroundBitmap usa la resolución de inferencia. Si ésta difiere de
                // la original, se escala con filtrado antes del recorte final.
                if (foreground.width == source.width && foreground.height == source.height) {
                    foreground
                } else {
                    Bitmap.createScaledBitmap(foreground, source.width, source.height, true)
                }
            }

            trimTransparentEdges(refined ?: fallback ?: error("No foreground bitmap available"))
        } finally {
            segmenter.close()
        }
    }

    /**
     * ML Kit funciona mejor con entradas suficientemente grandes. Escalamos
     * sólo la copia usada para inferencia; la salida conserva la resolución
     * original mediante el remapeo posterior de la máscara.
     */
    private fun prepareForInference(source: Bitmap): Bitmap {
        val width = source.width.coerceAtLeast(1)
        val height = source.height.coerceAtLeast(1)
        val minSide = min(width, height)
        val maxSide = max(width, height)
        val scale = when {
            minSide < MIN_INFERENCE_SIDE -> MIN_INFERENCE_SIDE.toFloat() / minSide
            maxSide > MAX_INFERENCE_SIDE -> MAX_INFERENCE_SIDE.toFloat() / maxSide
            else -> 1f
        }
        if (scale == 1f) return source
        return Bitmap.createScaledBitmap(
            source,
            (width * scale).roundToInt().coerceAtLeast(1),
            (height * scale).roundToInt().coerceAtLeast(1),
            true
        )
    }

    /**
     * Construye una máscara alfa de alta calidad y la reaplica a la imagen
     * original. La máscara se suaviza primero en el espacio de inferencia y
     * después se reescala con interpolación bilineal, lo que evita bordes
     * dentados y conserva la resolución del archivo insertado.
     */
    private fun buildRefinedForeground(
        source: Bitmap,
        inferenceWidth: Int,
        inferenceHeight: Int,
        confidence: FloatArray
    ): Bitmap {
        val smoothed = smoothConfidence(confidence, inferenceWidth, inferenceHeight)
        val maskPixels = IntArray(smoothed.size)
        var meaningful = 0

        for (i in smoothed.indices) {
            val probability = smoothed[i].coerceIn(0f, 1f)
            val alpha = confidenceToAlpha(probability)
            if (alpha > 20) meaningful++
            maskPixels[i] = Color.argb(alpha, 255, 255, 255)
        }
        if (meaningful < maskPixels.size / 500) {
            error("Foreground mask is effectively empty")
        }

        val inferenceMask = Bitmap.createBitmap(
            maskPixels,
            inferenceWidth,
            inferenceHeight,
            Bitmap.Config.ARGB_8888
        )
        val fullMask = if (inferenceWidth == source.width && inferenceHeight == source.height) {
            inferenceMask
        } else {
            Bitmap.createScaledBitmap(inferenceMask, source.width, source.height, true)
        }

        val sourcePixels = IntArray(source.width * source.height)
        val alphaPixels = IntArray(sourcePixels.size)
        source.getPixels(sourcePixels, 0, source.width, 0, 0, source.width, source.height)
        fullMask.getPixels(alphaPixels, 0, source.width, 0, 0, source.width, source.height)

        for (i in sourcePixels.indices) {
            val maskAlpha = Color.alpha(alphaPixels[i])
            val originalAlpha = Color.alpha(sourcePixels[i])
            val finalAlpha = ((originalAlpha * maskAlpha) / 255f).roundToInt().coerceIn(0, 255)
            sourcePixels[i] = (sourcePixels[i] and 0x00FFFFFF) or (finalAlpha shl 24)
        }

        return Bitmap.createBitmap(
            sourcePixels,
            source.width,
            source.height,
            Bitmap.Config.ARGB_8888
        )
    }

    /** Curva suave que deja fondo claro fuera y preserva detalle en el borde. */
    private fun confidenceToAlpha(probability: Float): Int {
        val normalized = when {
            probability <= HARD_BACKGROUND -> 0f
            probability >= HARD_FOREGROUND -> 1f
            else -> {
                val t = ((probability - HARD_BACKGROUND) /
                    (HARD_FOREGROUND - HARD_BACKGROUND)).coerceIn(0f, 1f)
                t * t * (3f - 2f * t) // smoothstep
            }
        }
        return (normalized * 255f).roundToInt().coerceIn(0, 255)
    }

    /**
     * Kernel gaussiano aproximado 3x3 (1-2-1 / 2-4-2 / 1-2-1). El suavizado
     * se limita a un píxel para no comerse detalles finos como pelo o plumas.
     */
    private fun smoothConfidence(input: FloatArray, width: Int, height: Int): FloatArray {
        if (width < 3 || height < 3) return input.copyOf()
        val output = FloatArray(input.size)
        val kernel = intArrayOf(1, 2, 1, 2, 4, 2, 1, 2, 1)

        for (y in 0 until height) {
            for (x in 0 until width) {
                var sum = 0f
                var totalWeight = 0
                var k = 0
                for (dy in -1..1) {
                    val sy = (y + dy).coerceIn(0, height - 1)
                    for (dx in -1..1) {
                        val sx = (x + dx).coerceIn(0, width - 1)
                        val weight = kernel[k++]
                        sum += input[sy * width + sx] * weight
                        totalWeight += weight
                    }
                }
                output[y * width + x] = sum / totalWeight.toFloat()
            }
        }
        return output
    }

    private fun FloatBuffer.toFloatArrayCopy(): FloatArray {
        val copy = duplicate()
        copy.rewind()
        return FloatArray(copy.remaining()).also { copy.get(it) }
    }

    /**
     * El recorte final usa un umbral muy bajo y un margen del 2.5 % para
     * conservar sombras suaves, transparencias y elementos finos del borde.
     */
    private fun trimTransparentEdges(bitmap: Bitmap): Bitmap {
        if (!bitmap.hasAlpha()) return bitmap
        val width = bitmap.width
        val height = bitmap.height
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

        var minX = width
        var minY = height
        var maxX = -1
        var maxY = -1
        for (y in 0 until height) {
            val row = y * width
            for (x in 0 until width) {
                if (Color.alpha(pixels[row + x]) > 6) {
                    if (x < minX) minX = x
                    if (x > maxX) maxX = x
                    if (y < minY) minY = y
                    if (y > maxY) maxY = y
                }
            }
        }
        if (maxX < minX || maxY < minY) return bitmap

        val cropWidth = maxX - minX + 1
        val cropHeight = maxY - minY + 1
        val padX = (cropWidth * 0.025f).roundToInt().coerceAtLeast(3)
        val padY = (cropHeight * 0.025f).roundToInt().coerceAtLeast(3)
        val left = (minX - padX).coerceAtLeast(0)
        val top = (minY - padY).coerceAtLeast(0)
        val right = (maxX + padX).coerceAtMost(width - 1)
        val bottom = (maxY + padY).coerceAtMost(height - 1)
        return Bitmap.createBitmap(bitmap, left, top, right - left + 1, bottom - top + 1)
    }

    private data class CropSegmentationResult(
        val confidence: FloatArray?,
        val fallbackForeground: Bitmap?
    )
}

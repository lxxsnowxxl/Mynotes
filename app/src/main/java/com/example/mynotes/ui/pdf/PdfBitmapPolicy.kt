package com.example.mynotes.ui.pdf

import kotlin.math.roundToInt

/** Límites de rasterización; las dimensiones lógicas del documento no cambian. */
internal object PdfBitmapPolicy {
    const val RENDER_WIDTH = 1200
    const val MAX_BITMAP_SIDE = 2600

    data class RasterSize(val width: Int, val height: Int)

    fun rasterSize(sourceWidth: Int, sourceHeight: Int, requestedWidth: Int = RENDER_WIDTH): RasterSize {
        require(sourceWidth > 0 && sourceHeight > 0)
        val widthLimit = requestedWidth.coerceIn(1, RENDER_WIDTH)
        val scale = minOf(widthLimit.toDouble() / sourceWidth, MAX_BITMAP_SIDE.toDouble() / sourceHeight)
        return RasterSize(
            (sourceWidth * scale).roundToInt().coerceIn(1, widthLimit),
            (sourceHeight * scale).roundToInt().coerceIn(1, MAX_BITMAP_SIDE)
        )
    }

    fun imageSampleSize(width: Int, height: Int): Int {
        require(width > 0 && height > 0)
        val largest = maxOf(width, height).toLong()
        var sample = 1
        while ((largest + sample - 1L) / sample > MAX_BITMAP_SIDE) sample *= 2
        return sample
    }
}

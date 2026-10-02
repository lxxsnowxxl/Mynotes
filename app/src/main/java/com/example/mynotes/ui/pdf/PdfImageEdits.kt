package com.example.mynotes.ui.pdf

import android.graphics.Bitmap
import android.graphics.Matrix
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

internal enum class PdfCropHandle(val horizontal: Int, val vertical: Int) {
    TOP_LEFT(-1, -1), TOP(0, -1), TOP_RIGHT(1, -1), RIGHT(1, 0),
    BOTTOM_RIGHT(1, 1), BOTTOM(0, 1), BOTTOM_LEFT(-1, 1), LEFT(-1, 0), MOVE(0, 0)
}

/** Normalized coordinates; a crop always retains at least one source pixel. */
internal data class PdfImageCrop(
    val left: Float = 0f, val top: Float = 0f,
    val right: Float = 1f, val bottom: Float = 1f
) {
    init {
        require(left >= 0f && top >= 0f && right <= 1f && bottom <= 1f)
        require(left < right && top < bottom)
    }

    val width: Float get() = right - left
    val height: Float get() = bottom - top

    fun drag(handle: PdfCropHandle, dx: Float, dy: Float, minWidth: Float, minHeight: Float): PdfImageCrop {
        if (handle == PdfCropHandle.MOVE) {
            val x = (left + dx).coerceIn(0f, 1f - width)
            val y = (top + dy).coerceIn(0f, 1f - height)
            return copy(left = x, top = y, right = (x + width).coerceAtMost(1f), bottom = (y + height).coerceAtMost(1f))
        }
        val minW = minOf(minWidth, width)
        val minH = minOf(minHeight, height)
        return copy(
            left = if (handle.horizontal < 0) (left + dx).coerceIn(0f, right - minW) else left,
            top = if (handle.vertical < 0) (top + dy).coerceIn(0f, bottom - minH) else top,
            right = if (handle.horizontal > 0) (right + dx).coerceIn(left + minW, 1f) else right,
            bottom = if (handle.vertical > 0) (bottom + dy).coerceIn(top + minH, 1f) else bottom
        )
    }

    /** The preview includes the quarter turns applied with the existing Rotate tool. */
    fun inSourceCoordinates(turns: Int): PdfImageCrop = when (turns) {
        1 -> PdfImageCrop(top, 1f - right, bottom, 1f - left)
        2 -> PdfImageCrop(1f - right, 1f - bottom, 1f - left, 1f - top)
        3 -> PdfImageCrop(1f - bottom, left, 1f - top, right)
        else -> this
    }
}

internal object PdfImageEdits {
    fun quarterTurns(image: PdfImageElement): Int = ((image.rotationDegrees / 90f).roundToInt() % 4 + 4) % 4

    fun flip(image: PdfImageElement, horizontal: Boolean): PdfImageElement {
        // Directions refer to the page, including images already rotated by 90/270 degrees.
        val sourceHorizontal = horizontal != (quarterTurns(image) % 2 == 1)
        val matrix = Matrix().apply {
            setScale(if (sourceHorizontal) -1f else 1f, if (sourceHorizontal) 1f else -1f)
        }
        val source = image.bitmap
        return image.copy(bitmap = Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, false))
    }

    fun crop(image: PdfImageElement, page: PdfPageModel, area: PdfImageCrop): PdfImageElement {
        val source = image.bitmap
        val left = (area.left * source.width).roundToInt().coerceIn(0, source.width - 1)
        val top = (area.top * source.height).roundToInt().coerceIn(0, source.height - 1)
        val right = (area.right * source.width).roundToInt().coerceIn(left + 1, source.width)
        val bottom = (area.bottom * source.height).roundToInt().coerceIn(top + 1, source.height)
        if (left == 0 && top == 0 && right == source.width && bottom == source.height) return image

        val newWidth = image.width * (right - left) / source.width
        val newHeight = image.height * (bottom - top) / source.height
        // Keep the retained pixels at their original position and scale on the page.
        // Rotate offsets in page points, since normalized X and Y have different scales.
        val dx = ((left + right) / (2f * source.width) - 0.5f) * image.width * page.widthPt
        val dy = ((top + bottom) / (2f * source.height) - 0.5f) * image.height * page.heightPt
        val angle = image.rotationDegrees * PI / 180.0
        val centerX = image.x + image.width / 2f + (dx * cos(angle) - dy * sin(angle)).toFloat() / page.widthPt
        val centerY = image.y + image.height / 2f + (dx * sin(angle) + dy * cos(angle)).toFloat() / page.heightPt
        return image.copy(
            bitmap = Bitmap.createBitmap(source, left, top, right - left, bottom - top),
            x = centerX - newWidth / 2f, y = centerY - newHeight / 2f,
            width = newWidth, height = newHeight
        )
    }
}

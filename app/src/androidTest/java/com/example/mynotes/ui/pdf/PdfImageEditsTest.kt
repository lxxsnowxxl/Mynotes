package com.example.mynotes.ui.pdf

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.RectF
import androidx.test.ext.junit.runners.AndroidJUnit4
import java.io.ByteArrayOutputStream
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PdfImageEditsTest {
    private val page = PdfPageModel(widthPt = 1000, heightPt = 500)

    private fun image(rotation: Float = 0f): PdfImageElement {
        val bitmap = Bitmap.createBitmap(8, 6, Bitmap.Config.ARGB_8888)
        for (y in 0 until 6) for (x in 0 until 8) {
            bitmap.setPixel(x, y, if (x == 0 && y == 0) Color.TRANSPARENT else Color.rgb(x * 30, y * 40, 90))
        }
        return PdfImageElement(7L, bitmap, 0.1f, 0.2f, 0.4f, 0.4f, rotation)
    }

    private fun pixels(bitmap: Bitmap) = IntArray(bitmap.width * bitmap.height).also {
        bitmap.getPixels(it, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
    }

    private fun render(image: PdfImageElement): Bitmap {
        val source = image.bitmap
        val swapped = image.rotationDegrees.toInt() % 180 != 0
        val result = Bitmap.createBitmap(if (swapped) source.height else source.width, if (swapped) source.width else source.height, Bitmap.Config.ARGB_8888)
        Canvas(result).apply {
            translate(result.width / 2f, result.height / 2f)
            rotate(image.rotationDegrees)
            drawBitmap(source, null, RectF(-source.width / 2f, -source.height / 2f, source.width / 2f, source.height / 2f), null)
        }
        return result
    }

    @Test fun flipDirectionsFollowTheVisibleImageAtEveryRotation() {
        for (rotation in listOf(0f, 90f, 180f, 270f)) for (horizontal in listOf(true, false)) {
            val source = image(rotation)
            val originalPixels = pixels(source.bitmap)
            val before = render(source)
            val edited = PdfImageEdits.flip(source, horizontal)
            val after = render(edited)
            for (y in 0 until after.height) for (x in 0 until after.width) {
                val expected = before.getPixel(if (horizontal) before.width - 1 - x else x, if (horizontal) y else before.height - 1 - y)
                assertEquals(expected, after.getPixel(x, y))
            }
            assertArrayEquals(originalPixels, pixels(source.bitmap))
            assertEquals(source, edited.copy(bitmap = source.bitmap))
            val restored = PdfImageEdits.flip(edited, horizontal)
            assertArrayEquals(originalPixels, pixels(restored.bitmap))
        }
    }

    @Test fun cropKeepsTheSelectedPixelsAtTheirOriginalScale() {
        val source = image()
        val before = pixels(source.bitmap)
        val edited = PdfImageEdits.crop(source, page, PdfImageCrop(0.25f, 1f / 6f, 0.75f, 5f / 6f))
        assertEquals(4, edited.bitmap.width)
        assertEquals(4, edited.bitmap.height)
        for (y in 0 until 4) for (x in 0 until 4) assertEquals(source.bitmap.getPixel(x + 2, y + 1), edited.bitmap.getPixel(x, y))
        assertEquals(0.2f, edited.x, 0.00001f)
        assertEquals(0.2f + 0.4f / 6f, edited.y, 0.00001f)
        assertEquals(0.2f, edited.width, 0.00001f)
        assertEquals(0.4f * 4f / 6f, edited.height, 0.00001f)
        assertEquals(source.id, edited.id)
        assertArrayEquals(before, pixels(source.bitmap))
    }

    @Test fun cropPreservesPlacementOnANonSquarePageAfterRotation() {
        val expected = listOf(0.1f to 0.2f, 0.2f to 0f, 0.3f to 0.2f, 0.2f to 0.4f)
        expected.forEachIndexed { turn, position ->
            val edited = PdfImageEdits.crop(image(turn * 90f), page, PdfImageCrop(right = 0.5f))
            assertEquals(position.first, edited.x, 0.00001f)
            assertEquals(position.second, edited.y, 0.00001f)
            assertEquals(0.2f, edited.width, 0.00001f)
            assertEquals(0.4f, edited.height, 0.00001f)
            assertEquals(turn * 90f, edited.rotationDegrees, 0f)
        }
    }

    @Test fun previewCropMapsBackToTheCorrectSourceArea() {
        val displayLeftHalf = PdfImageCrop(right = 0.5f)
        val expected = listOf(
            PdfImageCrop(right = 0.5f), PdfImageCrop(top = 0.5f),
            PdfImageCrop(left = 0.5f), PdfImageCrop(bottom = 0.5f)
        )
        expected.forEachIndexed { turns, area -> assertEquals(area, displayLeftHalf.inSourceCoordinates(turns)) }
    }

    @Test fun fullCropDoesNotAllocateOrCreateAHistoryChange() {
        val source = image(90f)
        assertSame(source, PdfImageEdits.crop(source, page, PdfImageCrop()))
    }

    @Test fun aTinyCropAtTheLastPixelRemainsValid() {
        val source = image()
        val edited = PdfImageEdits.crop(source, page, PdfImageCrop(0.9999f, 0.9999f, 1f, 1f))
        assertEquals(1, edited.bitmap.width)
        assertEquals(1, edited.bitmap.height)
        assertEquals(source.bitmap.getPixel(7, 5), edited.bitmap.getPixel(0, 0))
    }

    @Test fun editedPixelsAndTransparencySurvivePngPersistence() {
        val source = image()
        val cropped = PdfImageEdits.crop(source, page, PdfImageCrop(right = 0.5f, bottom = 0.5f))
        val flipped = PdfImageEdits.flip(cropped, horizontal = true)
        val output = ByteArrayOutputStream()
        assertTrue(flipped.bitmap.compress(Bitmap.CompressFormat.PNG, 100, output))
        val encoded = output.toByteArray()
        val reloaded = requireNotNull(BitmapFactory.decodeByteArray(encoded, 0, encoded.size))
        assertArrayEquals(pixels(flipped.bitmap), pixels(reloaded))
        assertEquals(Color.TRANSPARENT, reloaded.getPixel(reloaded.width - 1, 0))
    }

    @Test fun draggingCannotInvertTheFrameOrMoveItOutsideTheImage() {
        val original = PdfImageCrop(0.2f, 0.2f, 0.8f, 0.8f)
        val resized = original.drag(PdfCropHandle.TOP_LEFT, 100f, 100f, 0.1f, 0.1f)
        assertEquals(0.1f, resized.width, 0.00001f)
        assertEquals(0.1f, resized.height, 0.00001f)
        val moved = original.drag(PdfCropHandle.MOVE, -100f, 100f, 0.1f, 0.1f)
        assertEquals(0f, moved.left, 0f)
        assertEquals(1f, moved.bottom, 0f)
        assertEquals(original.width, moved.width, 0.00001f)
        assertEquals(original.height, moved.height, 0.00001f)
    }
}

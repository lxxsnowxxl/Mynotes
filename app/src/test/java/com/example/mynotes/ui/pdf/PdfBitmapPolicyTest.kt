package com.example.mynotes.ui.pdf

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PdfBitmapPolicyTest {
    @Test
    fun ordinaryPortraitAndLandscapePagesKeepTheirRenderingSize() {
        assertEquals(PdfBitmapPolicy.RasterSize(1200, 1698), PdfBitmapPolicy.rasterSize(595, 842))
        assertEquals(PdfBitmapPolicy.RasterSize(1200, 600), PdfBitmapPolicy.rasterSize(1200, 600))
        assertEquals(PdfBitmapPolicy.RasterSize(260, 368), PdfBitmapPolicy.rasterSize(595, 842, 260))
    }

    @Test
    fun tallPagesStayWithinThePixelBudgetAndKeepTheAspectRatio() {
        val size = PdfBitmapPolicy.rasterSize(100, 10000)
        assertEquals(PdfBitmapPolicy.RasterSize(26, 2600), size)
        assertEquals(100.0, size.height.toDouble() / size.width, 0.01)
        assertTrue(size.width.toLong() * size.height <= 1200L * 2600)
    }

    @Test
    fun extremeDimensionsDoNotOverflowOrProduceZeroSizedBitmaps() {
        for ((width, height) in listOf(1 to Int.MAX_VALUE, Int.MAX_VALUE to 1, Int.MAX_VALUE to Int.MAX_VALUE)) {
            val size = PdfBitmapPolicy.rasterSize(width, height)
            assertTrue(size.width in 1..1200 && size.height in 1..2600)
            val sample = PdfBitmapPolicy.imageSampleSize(width, height)
            assertTrue(sample > 0 && (sample and (sample - 1)) == 0)
            assertTrue((maxOf(width, height).toLong() + sample - 1) / sample <= 2600)
        }
    }

    @Test
    fun imageSamplingHandlesOddDimensionsAtTheLimitWithoutAllocatingTheOriginal() {
        assertEquals(1, PdfBitmapPolicy.imageSampleSize(2600, 1000))
        assertEquals(2, PdfBitmapPolicy.imageSampleSize(2601, 1000))
        assertEquals(4, PdfBitmapPolicy.imageSampleSize(5201, 2600))
        assertEquals(4, PdfBitmapPolicy.imageSampleSize(10000, 5000))
    }
}

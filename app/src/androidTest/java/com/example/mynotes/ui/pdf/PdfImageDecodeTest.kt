package com.example.mynotes.ui.pdf

import android.graphics.Bitmap
import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import java.io.File
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PdfImageDecodeTest {
    @Test
    fun boundsOnlyDecodeCanReturnNullWhileValidImagesStillLoadAndAreSampled() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        for ((width, height) in listOf(64 to 32, 6000 to 4)) {
            val file = File.createTempFile("pdf_decode_test", ".png", context.cacheDir)
            val source = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            var decoded: Bitmap? = null
            try {
                file.outputStream().use { assertTrue(source.compress(Bitmap.CompressFormat.PNG, 100, it)) }
                val result = PdfDocumentEngine.loadImage(context, Uri.fromFile(file))
                decoded = result
                assertTrue(result.width in 1..2600 && result.height in 1..2600)
                if (width == 64) {
                    assertEquals(width, result.width)
                    assertEquals(height, result.height)
                    assertEquals(0, result.getPixel(0, 0))
                }
            } finally {
                decoded?.recycle()
                source.recycle()
                file.delete()
            }
        }
    }

    @Test
    fun invalidImageMetadataFailsBeforeAttemptingAnUnboundedDecode() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val file = File.createTempFile("pdf_decode_invalid", ".bin", context.cacheDir)
        try {
            file.writeBytes(byteArrayOf(1, 2, 3, 4))
            val failure = runCatching { PdfDocumentEngine.loadImage(context, Uri.fromFile(file)) }.exceptionOrNull()
            assertTrue(failure is IllegalStateException)
        } finally {
            file.delete()
        }
    }
}

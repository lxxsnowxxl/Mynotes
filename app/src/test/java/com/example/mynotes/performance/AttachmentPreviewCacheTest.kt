package com.example.mynotes.performance

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.net.Uri
import java.io.File
import java.security.MessageDigest
import java.util.UUID
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class AttachmentPreviewCacheTest {
    @get:Rule val files = TemporaryFolder()
    private val uris = mutableListOf<Uri>()
    private val context: Context by lazy {
        object : ContextWrapper(RuntimeEnvironment.getApplication()) {
            override fun getCacheDir(): File = files.root
        }
    }

    private fun missingOriginal(): Uri = Uri.parse("file:///missing/${UUID.randomUUID()}").also(uris::add)

    private fun seed(uri: Uri, kind: String, tag: String, width: Int = 768, height: Int = 384): File {
        val hash = MessageDigest.getInstance("SHA-256").digest(uri.toString().toByteArray(Charsets.UTF_8))
            .joinToString("") { "%02x".format(it) }
        val folder = File(context.cacheDir, "mynotes_perf_v2/${kind}_previews").apply { mkdirs() }
        val file = File(folder, "$hash-$tag.jpg")
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        try {
            file.outputStream().use { assertTrue(bitmap.compress(Bitmap.CompressFormat.JPEG, 85, it)) }
        } finally {
            bitmap.recycle()
        }
        return file
    }

    @After fun clearCache() = runBlocking {
        uris.forEach { AttachmentPreviewCache.invalidate(context, it) }
    }

    @Test fun cachedImageAndPdfAppearWithoutOpeningMissingOriginals() = runBlocking {
        for (kind in listOf("image", "pdf")) {
            for (mode in listOf("quality", "balanced", "performance")) {
                val uri = missingOriginal()
                seed(uri, kind, "quality")
                val result = AttachmentPreviewCache.loadCachedPreview(context, uri, kind, mode)
                val bitmap = requireNotNull(result?.bitmap)
                assertTrue(bitmap.width <= 192 && bitmap.height <= 192)
                assertFalse(AttachmentPreviewCache.isExactBitmapPreview(kind, uri, mode, bitmap))
                val warm = if (kind == "image") AttachmentPreviewCache.peekImagePreview(uri, mode)
                    else AttachmentPreviewCache.peekPdfPreview(uri, mode)
                assertSame(bitmap, warm)
            }
        }
    }

    @Test fun absentCacheLeavesOriginalMediaDeferred() = runBlocking {
        val uri = missingOriginal()
        for (kind in listOf("image", "video", "pdf")) {
            assertNull(AttachmentPreviewCache.loadCachedPreview(context, uri, kind, "balanced"))
        }
        assertTrue(context.cacheDir.listFiles().orEmpty().isEmpty())
    }

    @Test fun cachedVideoRetainsMetadataOnReentryAndClearsOnInvalidation() = runBlocking {
        val uri = missingOriginal()
        val image = seed(uri, "video", "balanced")
        File(image.parentFile, "${image.nameWithoutExtension}.meta").writeText("12345,1920,1080")
        val loaded = requireNotNull(AttachmentPreviewCache.loadCachedPreview(context, uri, "video", "balanced"))
        val warm = requireNotNull(AttachmentPreviewCache.peekVideoPreview(uri, "balanced"))
        assertSame(loaded.bitmap, warm.bitmap)
        assertEquals(12_345L, warm.durationMillis)
        assertEquals(1920, warm.width)
        assertEquals(1080, warm.height)
        AttachmentPreviewCache.invalidate(context, uri)
        assertNull(AttachmentPreviewCache.peekVideoPreview(uri, "balanced"))
        assertNull(AttachmentPreviewCache.loadCachedPreview(context, uri, "video", "balanced"))
    }

    @Test fun concurrentRequestsShareTheDecodedTargetBitmap() = runBlocking {
        val uri = missingOriginal()
        seed(uri, "image", "quality")
        val start = CompletableDeferred<Unit>()
        val requests = List(8) {
            async(Dispatchers.Default) {
                start.await()
                AttachmentPreviewCache.loadImagePreview(context, uri, "quality")
            }
        }
        start.complete(Unit)
        val results = requests.awaitAll()
        val target = requireNotNull(results.first())
        results.forEach { assertSame(target, it) }
        assertTrue(AttachmentPreviewCache.isExactBitmapPreview("image", uri, "quality", target))
    }
}

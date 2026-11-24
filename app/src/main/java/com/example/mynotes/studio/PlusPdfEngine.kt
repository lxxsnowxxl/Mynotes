package com.example.mynotes.studio

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.example.mynotes.ui.pdf.*
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.*
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle
import com.tom_roush.pdfbox.pdmodel.graphics.image.JPEGFactory
import kotlinx.coroutines.*
import org.json.*
import java.io.*
import java.security.MessageDigest
import java.util.UUID
import kotlin.math.*

internal object PlusPdfEngine {
    private fun directory(context: Context, id: String): File {
        require(id.matches(Regex("[a-zA-Z0-9-]{1,80}")))
        return File(context.filesDir, "pdf_projects/$id")
    }
    private fun digest(input: InputStream, hash: MessageDigest) {
        input.use { source -> val bytes = ByteArray(65536); while (true) { val n = source.read(bytes); if (n < 0) break; hash.update(bytes, 0, n) } }
    }
    fun revision(context: Context, id: String): String {
        val dir = directory(context, id)
        val model = JSONObject(File(dir, "project.json").readText()).getJSONArray("pages").toString()
        val source = File(dir, "source.pdf")
        val assets=listOf("images","backgrounds").flatMap{folder->File(dir,folder).walkTopDown().filter{it.isFile}.toList()}.sortedBy{it.relativeTo(dir).path}.joinToString("|"){"${it.relativeTo(dir).path}:${it.length()}:${it.lastModified()}"}
        val data = "$model:${source.length()}:${source.lastModified()}:$assets"
        return MessageDigest.getInstance("SHA-256").digest(data.toByteArray()).joinToString("") { "%02x".format(it) }
    }
    suspend fun duplicates(context: Context, projects: List<PdfProjectRepository.Summary>, job: String): List<List<PdfProjectRepository.Summary>> = withContext(Dispatchers.IO) {
        val results = linkedMapOf<String, MutableList<PdfProjectRepository.Summary>>()
        projects.forEachIndexed { index, project ->
            ensureActive()
            val dir = directory(context, project.id)
            val json = JSONObject(File(dir, "project.json").readText()).apply { remove("id"); remove("name"); remove("modifiedAt") }
            val hash = MessageDigest.getInstance("SHA-256")
            hash.update(json.toString().toByteArray())
            val files = listOf(File(dir, "source.pdf")) + listOf("images", "backgrounds").flatMap { folder -> File(dir, folder).walkTopDown().filter { it.isFile }.toList() }
            files.filter(File::isFile).sortedBy { it.relativeTo(dir).path }.forEach { file -> ensureActive(); hash.update(file.relativeTo(dir).path.toByteArray()); digest(file.inputStream(), hash) }
            val key = hash.digest().joinToString("") { "%02x".format(it) }
            results.getOrPut(key) { mutableListOf() } += project
            StudioJobs.progress(job, (index + 1f) / projects.size.coerceAtLeast(1))
        }
        results.values.filter { it.size > 1 }
    }

    /** OCR results are only usable while they match the saved PDF project revision. */
    suspend fun indexedText(context: Context, projectId: String, pageIndices: Iterable<Int>): Map<Int, String> = withContext(Dispatchers.IO) {
        try {
            val expected = revision(context, projectId)
            val store = StudioStore.get(context)
            store.load()
            val state = store.state.value
            buildMap<Int, String> {
                pageIndices.forEach { index ->
                    val row = state.get("ocr", "${projectId}_$index")
                    if (row.optString("revision") == expected) {
                        val value = row.optString("text")
                        if (value.isNotBlank()) put(index, value)
                    }
                }
            }
        } catch (cancelled: CancellationException) { throw cancelled }
          catch (_: Exception) { emptyMap() }
    }

    suspend fun recognize(context: Context, id: String, name: String, pages: List<PdfPageModel>, source: Uri?, selected: List<Int>, job: String) = withContext(Dispatchers.IO) {
        PdfProjectRepository.saveProject(context, id, name, pages, source)
        val version = revision(context, id)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        val store = StudioStore.get(context)
        var characters = 0
        try {
            selected.distinct().forEachIndexed { index, pageIndex ->
                ensureActive()
                require(pageIndex in pages.indices)
                val bitmap = PdfDocumentEngine.renderFlatPage(context, pages[pageIndex], source, 2400)
                val data = try {
                    // Wait for ownership of the bitmap to return before recycling on cancellation.
                    withContext(NonCancellable) { recognizer.process(InputImage.fromBitmap(bitmap, 0)).studioAwait() }
                } finally { bitmap.recycle() }
                ensureActive()
                check(revision(context, id) == version) { "The project changed during recognition. Run recognition again." }
                characters += data.text.length
                require(characters <= 3_000_000) { "Recognize a smaller page range; the text index is too large." }
                val text = data.text.take(200000)
                store.edit("ocr", "${id}_$pageIndex") { it.put("project", id).put("page", pageIndex).put("text", text).put("revision", version).put("at", System.currentTimeMillis()) }
                StudioJobs.progress(job, (index + 1f) / selected.size.coerceAtLeast(1), "Page ${pageIndex + 1}")
            }
        } finally { recognizer.close() }
    }

    suspend fun compressed(context: Context, pages: List<PdfPageModel>, source: Uri?, quality: Int, maxSide: Int, job: String): File = withContext(Dispatchers.IO) {
        PDFBoxResourceLoader.init(context)
        require(pages.isNotEmpty())
        val target = File(context.cacheDir, "pdf_compression/${UUID.randomUUID()}.pdf").apply { parentFile!!.mkdirs() }
        try {
            PDDocument().use { document ->
                pages.forEachIndexed { index, page ->
                    ensureActive()
                    val bitmap = PdfDocumentEngine.renderFlatPage(context, page, source, maxSide.coerceIn(400, 2400))
                    try {
                        val data = ByteArrayOutputStream()
                        check(bitmap.compress(Bitmap.CompressFormat.JPEG, quality.coerceIn(30, 95), data))
                        val pdfPage = PDPage(PDRectangle(page.widthPt.toFloat(), page.heightPt.toFloat()))
                        document.addPage(pdfPage)
                        val image = JPEGFactory.createFromStream(document, ByteArrayInputStream(data.toByteArray()))
                        PDPageContentStream(document, pdfPage).use { stream -> stream.drawImage(image, 0f, 0f, page.widthPt.toFloat(), page.heightPt.toFloat()) }
                    } finally { bitmap.recycle() }
                    StudioJobs.progress(job, (index + 1f) / pages.size)
                }
                document.save(target)
            }
            target
        } catch (error: Throwable) { target.delete(); throw error }
    }

}

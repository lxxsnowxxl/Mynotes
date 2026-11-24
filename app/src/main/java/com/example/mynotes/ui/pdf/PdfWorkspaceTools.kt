package com.example.mynotes.ui.pdf

import com.example.mynotes.ui.sound.UiSoundPlayer

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.net.Uri
import android.util.AtomicFile
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.mynotes.R
import com.example.mynotes.ui.components.AppAlertDialog
import com.example.mynotes.ui.preview.LocalLayoutPreview
import com.example.mynotes.util.openUriStream
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.io.MemoryUsageSetting
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.json.JSONObject
import java.io.File
import java.util.UUID

internal data class PdfSearchHit(val page: Int, val snippet: String)
internal data class PdfSearchResults(val hits: List<PdfSearchHit>, val hasText: Boolean)
internal object PdfTextSearch {
    private val mutex = Mutex()
    suspend fun find(context: Context, projectId: String, source: Uri?, pages: List<PdfPageModel>, query: String): PdfSearchResults = withContext(Dispatchers.IO) { mutex.withLock {
        val recognized = com.example.mynotes.studio.PlusPdfEngine.indexedText(context, projectId, pages.indices.toList())
        val native = mutableMapOf<Int, String>()
        if (source != null) {
            PDFBoxResourceLoader.init(context.applicationContext)
            val memory = MemoryUsageSetting.setupTempFileOnly().setTempDir(context.cacheDir)
            context.openUriStream(source)?.use { input -> PDDocument.load(input, memory).use { document ->
                val requested = pages.mapNotNull { it.sourcePdfPageIndex }.distinct()
                val stripper = PDFTextStripper()
                for (index in requested) {
                    currentCoroutineContext().ensureActive()
                    if (index !in 0 until document.numberOfPages) continue
                    stripper.startPage = index + 1; stripper.endPage = index + 1
                    val text = stripper.getText(document)
                    // Cache only a match excerpt; a large document never accumulates all extracted text.
                    val found = text.indexOf(query, ignoreCase = true)
                    native[index] = if (found >= 0) "\u0001" + text.substring((found - 45).coerceAtLeast(0), (found + query.length + 90).coerceAtMost(text.length)) else if (text.isNotBlank()) "\u0002" else ""
                }
            } }
        }
        var hasText = native.values.any { it.isNotEmpty() }
        val hits = buildList {
            pages.forEachIndexed { index, page ->
                val added = page.texts.joinToString("\n") { it.text }
                if (added.isNotBlank()) hasText = true
                val offset = added.indexOf(query, ignoreCase = true)
                val original = page.sourcePdfPageIndex?.let { native[it] }.orEmpty()
                val ocrText = recognized[index].orEmpty()
                if (ocrText.isNotBlank()) hasText = true
                val ocrOffset = ocrText.indexOf(query, ignoreCase = true)
                if (offset >= 0) add(PdfSearchHit(index, added.substring((offset - 45).coerceAtLeast(0), (offset + query.length + 90).coerceAtMost(added.length))))
                else if (original.startsWith("\u0001")) add(PdfSearchHit(index, original.drop(1)))
                else if (ocrOffset >= 0) add(PdfSearchHit(index, ocrText.substring((ocrOffset - 45).coerceAtLeast(0), (ocrOffset + query.length + 90).coerceAtMost(ocrText.length))))
            }
        }
        PdfSearchResults(hits, hasText)
    } }
}

@Composable
internal fun PdfSearchDialog(projectId: String, source: Uri?, pages: List<PdfPageModel>, onPage: (Int) -> Unit, onDismiss: () -> Unit) {
    val context = LocalContext.current
    var query by remember { mutableStateOf("") }
    var results by remember { mutableStateOf<PdfSearchResults?>(null) }
    var busy by remember { mutableStateOf(false) }
    var failed by remember { mutableStateOf(false) }
    LaunchedEffect(query, projectId, source, pages) {
        results = null; failed = false
        if (query.isBlank()) { busy = false; return@LaunchedEffect }
        busy = true; delay(350)
        try { results = PdfTextSearch.find(context, projectId, source, pages, query.trim()) }
        catch (cancelled: CancellationException) { throw cancelled }
        catch (_: Exception) { failed = true }
        finally { busy = false }
    }
    AppAlertDialog(onDismissRequest = onDismiss, title = { Text(stringResource(R.string.workspace_pdf_search)) }, text = {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(query, { query = it }, modifier = Modifier.fillMaxWidth(), singleLine = true, label = { Text(stringResource(R.string.workspace_find)) })
            if (busy) LinearProgressIndicator(Modifier.fillMaxWidth())
            if (failed) Text(stringResource(R.string.pdf_error_open))
            if (results?.hits?.isEmpty() == true) Text(stringResource(if (results!!.hasText) R.string.workspace_no_results else R.string.workspace_pdf_no_text))
            LazyColumn(Modifier.heightIn(max = 320.dp)) { items(results?.hits.orEmpty(), key = { it.page }) { hit ->
                TextButton(onClick = { onPage(hit.page); onDismiss() ; UiSoundPlayer.playFallbackClick()}) { Column(Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.workspace_page_label, hit.page + 1), style = MaterialTheme.typography.labelLarge)
                    Text(hit.snippet.replace('\n', ' '), maxLines = 3, style = MaterialTheme.typography.bodySmall)
                } }
            } }
        }
    }, confirmButton = { TextButton(onClick = { onDismiss(); UiSoundPlayer.playFallbackClick() }) { Text(stringResource(R.string.workspace_done)) } })
}

@Composable
internal fun PdfRangeDialog(count: Int, onExport: (List<Int>) -> Unit, onDismiss: () -> Unit) {
    var range by remember { mutableStateOf("") }; var error by remember { mutableStateOf(false) }
    AppAlertDialog(onDismissRequest = onDismiss, title = { Text(stringResource(R.string.workspace_export)) }, text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(stringResource(R.string.workspace_page_range_hint))
            OutlinedTextField(range, { range = it; error = false }, label = { Text(stringResource(R.string.workspace_page_range)) }, singleLine = true, isError = error,
                supportingText = { if (error) Text(stringResource(R.string.workspace_invalid_range)) })
        }
    }, confirmButton = { TextButton(onClick = { val parsed = PdfPageRange.parse(range, count); if (parsed == null) error = true else onExport(parsed) ; UiSoundPlayer.playFallbackClick()}) { Text(stringResource(R.string.workspace_export)) } },
        dismissButton = { TextButton(onClick = { onExport((0 until count).toList()) ; UiSoundPlayer.playFallbackClick()}) { Text(stringResource(R.string.workspace_export_all)) } })
}

private val thumbnailMutex = Mutex()
@Composable
internal fun PdfPageBrowser(pages: List<PdfPageModel>, source: Uri?, selected: Int, onPage: (Int) -> Unit) {
    var bookmarks by remember { mutableStateOf(false) }
    Row { FilterChip(!bookmarks, { bookmarks = false }, label = { Text(stringResource(R.string.workspace_pages)) }); Spacer(Modifier.width(8.dp))
        FilterChip(bookmarks, { bookmarks = true }, label = { Text(stringResource(R.string.workspace_bookmarks)) }) }
    val visible = pages.indices.filter { !bookmarks || pages[it].bookmarked }
    if (visible.isEmpty()) Text(stringResource(R.string.workspace_no_results), style = MaterialTheme.typography.bodySmall)
    LazyColumn(Modifier.fillMaxWidth().height(260.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(visible, key = { it }) { index ->
            val page = pages[index]; val context = LocalContext.current; val preview = LocalLayoutPreview.current != null
            var thumbnail by remember(page, source) { mutableStateOf<Bitmap?>(null) }
            LaunchedEffect(page, source) {
                if (!preview) thumbnail = runCatching { thumbnailMutex.withLock { PdfDocumentEngine.renderProjectThumbnail(context, page, source, 128) } }.getOrNull()
            }
            // Lazy composition retains only visible thumbnails; the bitmap is collected after the item leaves.
            Surface(Modifier.fillMaxWidth().clickable { onPage(index) ; UiSoundPlayer.playFallbackClick()}, shape = MaterialTheme.shapes.medium,
                color = if (selected == index) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface) {
                Row(Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    thumbnail?.takeUnless { it.isRecycled }?.let { Image(it.asImageBitmap(), null, Modifier.size(52.dp, 72.dp)) }
                    Text(stringResource(R.string.workspace_page_label, index + 1), Modifier.weight(1f))
                    if (page.bookmarked) Icon(Icons.Default.Bookmark, stringResource(R.string.workspace_bookmark))
                }
            }
        }
    }
}

private data class SavedSignature(val id: String, val name: String, val file: File)
private object Signatures {
    private fun dir(context: Context) = File(context.filesDir, "workspaces/signatures").apply { mkdirs() }
    suspend fun list(context: Context) = withContext(Dispatchers.IO) { dir(context).listFiles().orEmpty().filter { it.extension == "json" }.mapNotNull { file -> runCatching {
        val json = JSONObject(file.readText()); val id = file.nameWithoutExtension
        SavedSignature(id, json.optString("name"), File(dir(context), "$id.png")).takeIf { it.file.isFile }
    }.getOrNull() } }
    suspend fun save(context: Context, name: String, strokes: List<List<Offset>>, width: Float, height: Float) = withContext(Dispatchers.IO) {
        val id = UUID.randomUUID().toString(); val file = File(dir(context), "$id.png")
        val bitmap = Bitmap.createBitmap(1000, 360, Bitmap.Config.ARGB_8888)
        try {
            val canvas = android.graphics.Canvas(bitmap); canvas.scale(1000 / width.coerceAtLeast(1f), 360 / height.coerceAtLeast(1f))
            val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = android.graphics.Color.BLACK; strokeWidth = 3f; strokeCap = Paint.Cap.ROUND }
            strokes.forEach { points -> points.zipWithNext().forEach { (a, b) -> canvas.drawLine(a.x, a.y, b.x, b.y, paint) } }
            file.outputStream().use { check(bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)) }
            val atomic = AtomicFile(File(dir(context), "$id.json")); val output = atomic.startWrite()
            try { output.write(JSONObject().put("name", name).toString().toByteArray()); atomic.finishWrite(output) }
            catch (error: Exception) { atomic.failWrite(output); file.delete(); throw error }
        } finally { bitmap.recycle() }
    }
    suspend fun delete(context: Context, signature: SavedSignature) = withContext(Dispatchers.IO) { signature.file.delete(); File(dir(context), "${signature.id}.json").delete() }
}

@Composable
internal fun PdfSignatureDialog(onBitmap: (Bitmap) -> Unit, onDismiss: () -> Unit) {
    val composeResources = androidx.compose.ui.platform.LocalResources.current

    val context = LocalContext.current; val scope = rememberCoroutineScope()
    var saved by remember { mutableStateOf<List<SavedSignature>>(emptyList()) }; var creating by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }; var strokes by remember { mutableStateOf<List<List<Offset>>>(emptyList()) }; var current by remember { mutableStateOf<List<Offset>>(emptyList()) }
    var padWidth by remember { mutableFloatStateOf(1f) }; var padHeight by remember { mutableFloatStateOf(1f) }
    var busy by remember { mutableStateOf(false) }; var failed by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { saved = Signatures.list(context) }
    AppAlertDialog(onDismissRequest = onDismiss, title = { Text(stringResource(R.string.workspace_signature)) }, text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            if (creating) {
                OutlinedTextField(name, { name = it.take(80) }, singleLine = true, label = { Text(stringResource(R.string.workspace_signature_name)) })
                Canvas(Modifier.fillMaxWidth().height(180.dp).background(Color.White).pointerInput(Unit) {
                    detectDragGestures(onDragStart = { current = listOf(it) }, onDrag = { change, _ -> change.consume(); current = current + change.position },
                        onDragEnd = { if (current.size > 1) strokes = strokes + listOf(current); current = emptyList() }, onDragCancel = { current = emptyList() })
                }) {
                    padWidth = size.width; padHeight = size.height
                    (strokes + listOf(current)).forEach { list -> list.zipWithNext().forEach { (a, b) -> drawLine(Color.Black, a, b, 3f, StrokeCap.Round) } }
                }
                TextButton(onClick = { strokes = emptyList(); current = emptyList() ; UiSoundPlayer.playFallbackClick(com.example.mynotes.ui.sound.UiActionSound.Delete)}) { Text(stringResource(R.string.drawing_clear)) }
            } else {
                if (saved.isEmpty()) Text(stringResource(R.string.workspace_no_signatures))
                LazyColumn(Modifier.heightIn(max = 300.dp)) { items(saved, key = { it.id }) { signature -> Row(verticalAlignment = Alignment.CenterVertically) {
                    TextButton(modifier = Modifier.weight(1f), onClick = {
                        scope.launch {
                            val bitmap = withContext(Dispatchers.IO) { BitmapFactory.decodeFile(signature.file.path) }
                            if (bitmap != null) {
                                onBitmap(bitmap)
                                onDismiss()
                            } else {
                                failed = true
                            }
                        }
                    }) { Text(signature.name.ifBlank { composeResources.getString(R.string.workspace_signature) }) }
                    IconButton(onClick = { scope.launch { Signatures.delete(context, signature); saved = Signatures.list(context) } ; UiSoundPlayer.playFallbackClick(com.example.mynotes.ui.sound.UiActionSound.Delete)}) { Icon(Icons.Default.Delete, stringResource(R.string.pdf_delete)) }
                } } }
            }
            if (busy) LinearProgressIndicator(Modifier.fillMaxWidth())
            if (failed) Text(stringResource(R.string.workspace_save_error))
        }
    }, confirmButton = { TextButton(enabled = !busy && (!creating || strokes.isNotEmpty()), onClick = {
        if (!creating) creating = true else scope.launch {
            busy = true; failed = false
            try { Signatures.save(context, name, strokes, padWidth, padHeight); saved = Signatures.list(context); creating = false; strokes = emptyList(); name = "" }
            catch (_: Exception) { failed = true }
            finally { busy = false }
        }
    ; UiSoundPlayer.playFallbackClick()}) { Text(stringResource(if (creating) R.string.workspace_save_signature else R.string.workspace_new_signature)) } },
        dismissButton = { TextButton(onClick = { onDismiss(); UiSoundPlayer.playFallbackClick() }) { Text(stringResource(R.string.workspace_done)) } })
}

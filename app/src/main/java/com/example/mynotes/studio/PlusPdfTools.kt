package com.example.mynotes.studio

import com.example.mynotes.ui.sound.UiSoundPlayer

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.mynotes.ui.components.AppAlertDialog
import com.example.mynotes.ui.components.StyledSettingsSlider
import com.example.mynotes.ui.workspace.workspaceToolActionButtonColors
import com.example.mynotes.ui.pdf.*
import kotlinx.coroutines.*
import java.io.File
import java.util.UUID

private data class PlusAnnotation(val key: String, val page: Int, val kind: String, val id: Long, val label: String)

@Composable internal fun PlusPdfTools(projectId: String, name: String, pages: List<PdfPageModel>, source: Uri?, pageIndex: Int, request: Int, onConsumed: () -> Unit) {
    val context = LocalContext.current
    var dialog by remember { mutableIntStateOf(0) }
    var allPages by remember { mutableStateOf(false) }
    var quality by remember { mutableFloatStateOf(75f) }
    var resolution by remember { mutableFloatStateOf(1600f) }
    var prepared by rememberSaveable { mutableStateOf<String?>(null) }
    var pending by rememberSaveable { mutableStateOf<String?>(null) }
    var status by remember { mutableStateOf("") }
    var busy by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val annotations = remember(pages) { pages.flatMapIndexed { index, page ->
        page.texts.map { PlusAnnotation("$index:text:${it.id}", index, "text", it.id, it.text) } +
            page.strokes.map { PlusAnnotation("$index:stroke:${it.id}", index, "stroke", it.id, "Freehand annotation") } +
            page.images.map { PlusAnnotation("$index:image:${it.id}", index, "image", it.id, "Image annotation") }
    } }
    var selected by remember(pages) { mutableStateOf(annotations.filter { it.page == pageIndex }.map { it.key }.toSet()) }
    LaunchedEffect(request) { if (request in 1034..1036) { dialog = request - 1000; onConsumed() } }
    val export = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/pdf")) { uri ->
        val file = pending?.let(::File); pending = null
        if (uri != null && file != null) StudioJobs.start("Export compressed PDF") {
            context.contentResolver.openOutputStream(uri, "wt")?.use { out -> file.inputStream().use { it.copyTo(out) } } ?: error("Cannot write PDF")
        }
    }
    val hasOverlayContent = pages.any { it.strokes.isNotEmpty() || it.texts.isNotEmpty() || it.images.isNotEmpty() }
    if (source != null || hasOverlayContent) {
        val buttonColors = workspaceToolActionButtonColors() ?: ButtonDefaults.outlinedButtonColors()
        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            OutlinedButton(onClick = { dialog = 34 ; UiSoundPlayer.playFallbackClick()}, colors = buttonColors) { Text("Recognize text") }
            OutlinedButton(onClick = { dialog = 35 ; UiSoundPlayer.playFallbackClick()}, colors = buttonColors) { Text("Compress PDF") }
            if (annotations.isNotEmpty()) OutlinedButton(onClick = { dialog = 36 ; UiSoundPlayer.playFallbackClick()}, colors = buttonColors) { Text("Annotations to note") }
        }
    }
    if (dialog > 0) AppAlertDialog(onDismissRequest = { if (!busy) dialog = 0 }, title = { Text(when (dialog) { 34 -> "Recognize text"; 35 -> "Compress PDF"; 36 -> "Annotations to note"; else -> "PDF tools" }) }, text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            if (busy) LinearProgressIndicator(Modifier.fillMaxWidth())
            if (status.isNotBlank()) Text(status)
            when (dialog) {
                34 -> {
                    Text("Recognize Latin-script text, including Spanish and English, on this device. The original PDF is preserved; recognized text becomes searchable.")
                    Row { Text("All pages", Modifier.weight(1f)); Switch(allPages, { allPages = it }, enabled = !busy) }
                    Text(if (allPages) "${pages.size} pages" else "Current page · ${pageIndex + 1}")
                }
                35 -> {
                    Text("Creates a new image-based PDF. Searchable text and editable form fields are flattened. Keep the original for editing.")
                    Text("JPEG quality · ${quality.toInt()}%")
                    StyledSettingsSlider(
                        value = quality,
                        onValueChange = {
                            quality = it
                            prepared = null
                        },
                        valueRange = 30f..95f,
                        valueLabel = "${quality.toInt()}%",
                        enabled = !busy
                    )
                    Text("Maximum image side · ${resolution.toInt()} px")
                    StyledSettingsSlider(
                        value = resolution,
                        onValueChange = {
                            resolution = it
                            prepared = null
                        },
                        valueRange = 400f..2400f,
                        valueLabel = "${resolution.toInt()} px",
                        enabled = !busy
                    )
                    prepared?.let(::File)?.takeIf(File::isFile)?.let { Text("Prepared size · " + android.text.format.Formatter.formatFileSize(context, it.length())) }
                    TextButton(enabled = !busy, onClick = {
                        val captured = pages; val original = source; val q = quality.toInt(); val side = resolution.toInt()
                        busy = true
                        StudioJobs.start("Prepare compressed PDF") { job ->
                            try {
                                val file = PlusPdfEngine.compressed(context, captured, original, q, side, job)
                                withContext(Dispatchers.Main) { prepared = file.path; status = "Preview prepared. Its exact file size is shown below." }
                            } finally { withContext(NonCancellable + Dispatchers.Main) { busy = false } }
                        }
                    ; UiSoundPlayer.playFallbackClick()}) { Text("Prepare size preview") }
                }
                36 -> {
                    Text("Select added annotations. Visual annotations are preserved in an attached PDF containing the selected pages and annotations.")
                    LazyColumn(Modifier.heightIn(max = 320.dp)) {
                        if (annotations.isEmpty()) item { Text("No added annotations in this project.") }
                        items(annotations, key = { it.key }) { item -> Row {
                            Checkbox(item.key in selected, { checked -> selected = if (checked) selected + item.key else selected - item.key }, enabled = !busy)
                            Text("Page ${item.page + 1} · ${item.label.take(120)}", Modifier.padding(top = 12.dp))
                        } }
                    }
                }
            }
        }
    }, confirmButton = { TextButton(enabled = !busy && (dialog != 35 || prepared?.let { File(it).isFile } == true) && (dialog != 36 || selected.isNotEmpty()), onClick = {
        when (dialog) {
            34 -> {
                val captured = pages; val original = source; val chosen = if (allPages) pages.indices.toList() else listOf(pageIndex)
                busy = true
                StudioJobs.start("Recognize PDF text") { job ->
                    try { PlusPdfEngine.recognize(context, projectId, name, captured, original, chosen, job); withContext(Dispatchers.Main) { status = "Text recognized and indexed."; dialog = 0 } }
                    finally { withContext(NonCancellable + Dispatchers.Main) { busy = false } }
                }
            }
            35 -> { pending = prepared; export.launch(name.ifBlank { "Document" } + "_compressed.pdf") }
            36 -> {
                val chosen = annotations.filter { it.key in selected }
                val captured = pages; val original = source
                busy = true
                StudioJobs.start("Annotations to note") {
                    val file = File(context.filesDir, "attachments/annotations_${UUID.randomUUID()}.pdf").apply { parentFile!!.mkdirs() }
                    try {
                        val models = chosen.map { it.page }.distinct().sorted().map { page ->
                            val ids = chosen.filter { it.page == page }
                            captured[page].copy(texts = captured[page].texts.filter { text -> ids.any { it.kind == "text" && it.id == text.id } }, strokes = captured[page].strokes.filter { stroke -> ids.any { it.kind == "stroke" && it.id == stroke.id } }, images = captured[page].images.filter { image -> ids.any { it.kind == "image" && it.id == image.id } })
                        }
                        PdfDocumentEngine.exportPdf(context, Uri.fromFile(file), models, original)
                        require(file.length() <= 128L * 1024 * 1024) { "The annotation attachment exceeds 128 MB. Select fewer pages." }
                        val body = chosen.groupBy { it.page }.entries.sortedBy { it.key }.joinToString("\n\n") { (page, rows) -> "## Page ${page + 1}\n" + rows.joinToString("\n") { it.label } }
                        PlusNotes.create(context, name + " · Annotations", body, listOf(file to "file"))
                        withContext(Dispatchers.Main) { status = "Annotation note created."; dialog = 0 }
                    } catch (failure: Throwable) { file.delete(); throw failure }
                    finally { withContext(NonCancellable + Dispatchers.Main) { busy = false } }
                }
            }
        }
    ; UiSoundPlayer.playFallbackClick()}) { Text(when (dialog) { 34 -> "Recognize"; 35 -> "Export prepared copy"; else -> "Create note" }) } }, dismissButton = { TextButton(onClick = { dialog = 0 ; UiSoundPlayer.playFallbackClick()}, enabled = !busy) { Text("Close") } })
}

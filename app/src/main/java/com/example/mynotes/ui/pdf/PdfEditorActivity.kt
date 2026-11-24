package com.example.mynotes.ui.pdf

import com.example.mynotes.ui.components.appScreenBackground
import com.example.mynotes.ui.components.AppCircularIconButton
import com.example.mynotes.ui.workspace.PaletteWorkspaceTheme
import com.example.mynotes.ui.preview.LocalLayoutPreview
import com.example.mynotes.ui.theme.rememberUiTextColors
import com.example.mynotes.ui.components.AppIconButton
import com.example.mynotes.ui.components.AppIconLabel
import com.example.mynotes.ui.components.AppTextButton
import com.example.mynotes.ui.components.HideDialogNavigationBar
import androidx.compose.ui.window.DialogProperties

import android.graphics.Color as AndroidColor
import android.graphics.Paint
import android.graphics.RectF
import android.os.Bundle
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.layout.*
import androidx.compose.material3.TextButton
import androidx.compose.material3.Switch
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import com.example.mynotes.ui.workspace.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.HorizontalRule
import androidx.compose.material.icons.filled.Highlight
import androidx.compose.material.icons.filled.CropSquare
import androidx.compose.material.icons.filled.Crop
import androidx.compose.material.icons.filled.Flip
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mynotes.R
import com.example.mynotes.settings.AppSettings
import com.example.mynotes.ui.motion.appMotionTransform
import com.example.mynotes.ui.motion.rememberMotionStyle
import com.example.mynotes.ui.sound.UiActionSound
import com.example.mynotes.ui.sound.UiHaptic
import com.example.mynotes.ui.sound.UiHapticPlayer
import com.example.mynotes.ui.sound.UiSound
import com.example.mynotes.ui.sound.UiSoundPlayer
import com.example.mynotes.ui.theme.rememberAppFontFamily
import com.example.mynotes.ui.theme.rememberAdaptiveUiButtonColors
import com.example.mynotes.ui.theme.compositeUiColor
import com.example.mynotes.util.uriDisplayName
import com.google.mlkit.common.MlKitException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.roundToInt

private val PdfEditorPalette = intArrayOf(
    AndroidColor.BLACK, AndroidColor.WHITE, 0xFFE33D3D.toInt(), 0xFFFF7A2D.toInt(), 0xFFF4C438.toInt(),
    0xFF47B071.toInt(), 0xFF35A8B5.toInt(), 0xFF3978D4.toInt(), 0xFF7651CA.toInt(), 0xFFD44B88.toInt()
)

private fun nextPdfElementId(pages: List<PdfPageModel>): Long {
    var maximum = 0L
    pages.forEach { page ->
        page.images.forEach { maximum = maxOf(maximum, it.id) }
        page.texts.forEach { maximum = maxOf(maximum, it.id) }
        page.strokes.forEach { maximum = maxOf(maximum, it.id) }
    }
    return maximum + 1L
}

class PdfEditorActivity : ImmersivePdfActivity() {
    companion object {
        const val EXTRA_PROJECT_ID = "pdf_project_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val initialProjectId = intent.getStringExtra(EXTRA_PROJECT_ID)
        setPdfContent { settings ->
            PdfEditorScreen(
                settings = settings,
                initialProjectId = initialProjectId,
                onClose = { finishWithPdfMotion() }
            )
        }
    }

}

@Composable
internal fun PdfEditorScreen(
    settings: AppSettings,
    initialProjectId: String?,
    onClose: () -> Unit, suggestedProjectId:String?=null,studioTool:Int=0,initialPageIndex:Int=0,onStudioConsumed:()->Unit={}
) {
    val composeResources = androidx.compose.ui.platform.LocalResources.current

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val fontFamily = rememberAppFontFamily(settings.font)
    val (primaryText, secondaryText) = rememberUiTextColors(settings.textColor, MaterialTheme.colorScheme.background)

    val layoutPreview = LocalLayoutPreview.current
    var pages by remember { mutableStateOf(layoutPreview?.pdfPages?.takeIf { it.isNotEmpty() } ?: listOf(PdfPageModel.blank())) }
    var currentPageIndex by remember { mutableIntStateOf(0) }
    var tool by remember { mutableStateOf(PdfEditorTool.SELECT) }
    var penColor by remember { mutableIntStateOf(AndroidColor.BLACK) }
    var penWidth by remember { mutableFloatStateOf(3f) }
    var selectedImageId by remember { mutableStateOf<Long?>(null) }
    var selectedTextId by remember { mutableStateOf<Long?>(null) }
    var selectedStrokeId by remember { mutableStateOf<Long?>(null) }
    var canvasTransforming by remember { mutableStateOf(false) }
    var nextElementId by remember { mutableLongStateOf(nextPdfElementId(pages)) }
    var aiBusy by remember { mutableStateOf(false) }
    var imageEditBusy by remember { mutableStateOf(false) }
    var showImageCropDialog by remember { mutableStateOf(false) }
    var pendingTextPosition by remember { mutableStateOf<PdfPoint?>(null) }
    var pendingText by remember { mutableStateOf("") }
    var showTextDialog by remember { mutableStateOf(false) }
    var textSize by remember { mutableFloatStateOf(18f) }
    var history by remember { mutableStateOf(listOf<List<PdfPageModel>>()) }
    var redoHistory by remember { mutableStateOf(listOf<List<PdfPageModel>>()) }
    var gesturePages by remember { mutableStateOf<List<PdfPageModel>?>(null) }
    var showTestModeNotice by rememberSaveable { mutableStateOf(true) }
    var showSearch by remember { mutableStateOf(false) }
    var showSignatures by remember { mutableStateOf(false) }
    var showExportRange by remember { mutableStateOf(false) }
    var showPageBrowser by rememberSaveable { mutableStateOf(false) }
    // PDF Studio reutiliza la pestaña lateral de Nueva nota, pero mantiene
    // su propio conjunto de herramientas y su estado abierto/cerrado.
    var pdfToolsExpanded by rememberSaveable { mutableStateOf(false) }
    BackHandler(enabled = pdfToolsExpanded) { pdfToolsExpanded = false }
    var stylusOnly by rememberSaveable { mutableStateOf(false) }
    var pendingExport by remember { mutableStateOf<List<PdfPageModel>>(emptyList()) }
    var pendingExportSource by remember { mutableStateOf<Uri?>(null) }
    var pendingExportFull by remember { mutableStateOf(false) }
    var sourcePdfUri by remember { mutableStateOf<Uri?>(null) }
    var pageLoading by remember { mutableStateOf(false) }
    var showGoToPageDialog by remember { mutableStateOf(false) }
    var goToPageInput by remember { mutableStateOf("") }
    var goToPageError by remember { mutableStateOf(false) }

    fun setSelection(imageId: Long? = null, textId: Long? = null, strokeId: Long? = null) {
        selectedImageId = imageId
        selectedTextId = textId
        selectedStrokeId = strokeId
    }

    fun clearSelection() = setSelection()

    fun navigatePage(delta: Int) {
        val target = currentPageIndex + delta
        if (target !in pages.indices) return
        currentPageIndex = target
        clearSelection()
        UiSoundPlayer.playAction(context, UiActionSound.Navigation)
    }

    val projectId = remember(initialProjectId) { initialProjectId ?: suggestedProjectId ?: PdfProjectRepository.newProjectId() }
    var projectName by remember(initialProjectId) { mutableStateOf(composeResources.getString(R.string.pdf_library_untitled)) }
    var projectReady by remember(initialProjectId) { mutableStateOf(initialProjectId == null) }
    var dirtyVersion by remember { mutableIntStateOf(0) }
    val projectEpoch=remember{com.example.mynotes.studio.StudioFiles.epoch.value}
    val saveCoordinator = remember(projectId) { PdfProjectSaveCoordinator() }

    LaunchedEffect(initialProjectId) {
        if (layoutPreview != null) return@LaunchedEffect
        if (initialProjectId == null) return@LaunchedEffect
        runCatching { PdfProjectRepository.loadProject(context, initialProjectId) }
            .onSuccess { project ->
                pages = project.pages
                projectName = project.name
                sourcePdfUri = project.sourcePdfUri
                currentPageIndex = initialPageIndex.coerceIn(0,project.pages.lastIndex)
                history = emptyList()
                redoHistory = emptyList()
                clearSelection()
                nextElementId = nextPdfElementId(project.pages)
                projectReady = true
            }
            .onFailure {
                projectReady = true
                Toast.makeText(context, composeResources.getString(R.string.pdf_library_error_open_project), Toast.LENGTH_LONG).show()
            }
    }

    fun markDirty() {
        dirtyVersion++
    }

    fun commit(newPages: List<PdfPageModel>) {
        if (newPages == pages) return
        history = (history + listOf(pages)).takeLast(30)
        redoHistory = emptyList()
        pages = newPages
        currentPageIndex = currentPageIndex.coerceIn(0, pages.lastIndex)
        markDirty()
    }

    fun editGesture(phase: Int) {
        if (phase == 0) { gesturePages = pages; return }
        val before = gesturePages ?: return
        gesturePages = null
        if (before != pages) {
            if (phase == 2) { pages = before; markDirty() }
            else { history = (history + listOf(before)).takeLast(30); redoHistory = emptyList() }
        }
    }

    fun replaceCurrent(page: PdfPageModel, recordHistory: Boolean = true) {
        val updated = pages.toMutableList().also { it[currentPageIndex] = page }
        if (recordHistory) {
            commit(updated)
        } else {
            pages = updated
            markDirty()
        }
    }

    fun editSelectedImage(edit: (PdfImageElement, PdfPageModel) -> PdfImageElement) {
        if (imageEditBusy || aiBusy) return
        val pageIndex = currentPageIndex
        val page = pages[pageIndex]
        val source = page.images.firstOrNull { it.id == selectedImageId } ?: return
        val editVersion = dirtyVersion
        imageEditBusy = true
        scope.launch {
            var result: PdfImageElement? = null
            var committed = false
            try {
                val updated = withContext(Dispatchers.Default) { edit(source, page).also { result = it } }
                if (currentPageIndex != pageIndex || dirtyVersion != editVersion || selectedImageId != source.id ||
                    pages.getOrNull(pageIndex)?.images?.firstOrNull { it.id == source.id } !== source) {
                    Toast.makeText(context, composeResources.getString(R.string.pdf_image_edit_changed), Toast.LENGTH_LONG).show()
                    return@launch
                }
                if (updated == source) return@launch
                replaceCurrent(pages[pageIndex].copy(images = pages[pageIndex].images.map { if (it.id == source.id) updated else it }))
                committed = true
                UiHapticPlayer.play(context, UiHaptic.Confirm)
            } catch (error: CancellationException) {
                throw error
            } catch (error: Throwable) {
                Log.w("PdfImageEdits", "Image edit failed", error)
                Toast.makeText(context, composeResources.getString(R.string.pdf_image_edit_error), Toast.LENGTH_LONG).show()
            } finally {
                if (!committed) result?.bitmap?.takeIf { it !== source.bitmap }?.recycle()
                imageEditBusy = false
            }
        }
    }

    fun undo() {
        val previous = history.lastOrNull() ?: return
        redoHistory = (redoHistory + listOf(pages)).takeLast(30)
        history = history.dropLast(1)
        pages = previous
        currentPageIndex = currentPageIndex.coerceIn(0, pages.lastIndex)
        clearSelection()
        markDirty()
    }

    fun redo() {
        val next = redoHistory.lastOrNull() ?: return
        history = (history + listOf(pages)).takeLast(30)
        redoHistory = redoHistory.dropLast(1)
        pages = next
        currentPageIndex = currentPageIndex.coerceIn(0, pages.lastIndex)
        clearSelection()
        markDirty()
    }

    fun displayNameForUri(uri: Uri): String? = context.uriDisplayName(uri)

    fun hasMeaningfulProject(): Boolean {
        return sourcePdfUri != null ||
            pages.size > 1 ||
            pages.any { page ->
                page.strokes.isNotEmpty() || page.texts.isNotEmpty() || page.images.isNotEmpty() || page.bookmarked
            }
    }

    suspend fun persistProject() {
        if(projectEpoch!=com.example.mynotes.studio.StudioFiles.epoch.value)return
        if (layoutPreview != null || !projectReady || !hasMeaningfulProject()) return
        try {
        saveCoordinator.saveIfChanged(currentVersion = {
            if (projectReady && hasMeaningfulProject()) dirtyVersion else null
        }) {
            val savedSource = sourcePdfUri
            val saved = PdfProjectRepository.saveProject(
                context = context,
                id = projectId,
                name = projectName,
                pages = pages,
                sourcePdfUri = savedSource
            )
            // Una importación más reciente debe conservar su propio PDF base.
            if (saved.sourcePdfUri != null && sourcePdfUri == savedSource) {
                sourcePdfUri = saved.sourcePdfUri
                if (savedSource != null && savedSource != saved.sourcePdfUri) com.example.mynotes.data.workspace.WorkspaceAssets.release(context, listOf(savedSource))
            }
        }
        } catch (cancelled: CancellationException) { throw cancelled }
        catch (error: Exception) { throw error }
    }

    fun importPdf(uri: Uri, displayName: String? = null) {
        scope.launch {
            runCatching {
                val editableExport = PdfProjectRepository.loadProjectForExportedPdf(context, uri)
                if (editableExport != null) {
                    Triple(editableExport.pages, editableExport.sourcePdfUri, true)
                } else {
                    Triple(PdfDocumentEngine.loadPdfLazy(context, uri), uri, false)
                }
            }.onSuccess { (loadedPages, loadedSourceUri, restoredEditableExport) ->
                val newPages = if (restoredEditableExport) {
                    loadedPages
                } else {
                    val previousPages = pages
                    loadedPages.mapIndexed { index, pdfPage ->
                        val previous = previousPages.getOrNull(index)
                        if (previous == null) {
                            pdfPage
                        } else {
                            pdfPage.copy(
                                strokes = previous.strokes,
                                texts = previous.texts,
                                images = previous.images,
                                bookmarked = previous.bookmarked
                            )
                        }
                    }.toMutableList().also { mergedPages ->
                        if (previousPages.size > loadedPages.size) {
                            previousPages.drop(loadedPages.size)
                                .filter { page ->
                                    page.strokes.isNotEmpty() || page.texts.isNotEmpty() || page.images.isNotEmpty() || page.bookmarked
                                }
                                .forEach { oldPage ->
                                    mergedPages += oldPage.copy(sourcePdfPageIndex = null)
                                }
                        }
                    }
                }

                pages = newPages.ifEmpty { listOf(PdfPageModel.blank()) }
                history = emptyList()
                redoHistory = emptyList()
                sourcePdfUri = loadedSourceUri
                (displayName ?: displayNameForUri(uri))?.let { displayName ->
                    projectName = displayName.substringBeforeLast('.', displayName)
                }
                currentPageIndex = 0
                clearSelection()
                nextElementId = nextPdfElementId(pages)

                runCatching {
                    context.contentResolver.takePersistableUriPermission(
                        uri,
                        android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                }

                markDirty()
                UiSoundPlayer.playAction(context, UiActionSound.Open)
            }.onFailure {
                Toast.makeText(context, composeResources.getString(R.string.pdf_error_open), Toast.LENGTH_LONG).show()
            }
        }
        }
    val openPdfLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri -> if (uri != null) importPdf(uri) }

    fun insertBitmap(bitmap: android.graphics.Bitmap) {

                    val ratio = bitmap.height.toFloat() / bitmap.width.toFloat().coerceAtLeast(1f)
                    val width = 0.46f
                    val height = (width * ratio * pages[currentPageIndex].widthPt / pages[currentPageIndex].heightPt)
                        .coerceIn(0.10f, 0.65f)
                    val element = PdfImageElement(
                        id = nextElementId++,
                        bitmap = bitmap,
                        x = 0.5f - width / 2f,
                        y = 0.5f - height / 2f,
                        width = width,
                        height = height
                    )
                    replaceCurrent(pages[currentPageIndex].copy(images = pages[currentPageIndex].images + element))
                    setSelection(imageId = element.id)
                    tool = PdfEditorTool.SELECT
                    UiSoundPlayer.playAction(context, UiActionSound.Add)

    }
    fun importImage(uri: Uri) {
        scope.launch { runCatching { PdfDocumentEngine.loadImage(context, uri) }.onSuccess { bitmap -> insertBitmap(bitmap); com.example.mynotes.data.workspace.WorkspaceAssets.release(context, listOf(uri)) }
            .onFailure { Toast.makeText(context, R.string.pdf_error_image, Toast.LENGTH_LONG).show() } }
    }
    val imageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri -> uri?.let(::importImage) }

    val exportLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/pdf")) { uri ->
        uri ?: return@rememberLauncherForActivityResult
        scope.launch {
            runCatching {
                val exported = pendingExport
                require(exported.isNotEmpty())
                PdfDocumentEngine.exportPdf(context, uri, exported, pendingExportSource)
                if (pendingExportFull) PdfProjectRepository.registerExportedPdf(context, uri, projectId)
                else PdfProjectRepository.registerFlatExport(context, uri)
            }.onSuccess {
                    UiSoundPlayer.playAction(context, UiActionSound.Save)
                    Toast.makeText(context, composeResources.getString(R.string.pdf_saved), Toast.LENGTH_SHORT).show()
                }
                .onFailure { Toast.makeText(context, composeResources.getString(R.string.pdf_error_save), Toast.LENGTH_LONG).show() }
        }
    }

    LaunchedEffect(dirtyVersion, projectReady) {
        if (!projectReady || dirtyVersion <= 0 || !hasMeaningfulProject()) return@LaunchedEffect
        delay(900)
        runCatching { persistProject() }
    }

    WorkspaceOnStop { scope.launch { runCatching { persistProject() } } }
    val exitSaver by rememberUpdatedState<suspend ()->Unit>({persistProject()})
    DisposableEffect(projectId){onDispose{if(layoutPreview==null)com.example.mynotes.studio.StudioJobs.start("Save PDF on exit"){exitSaver()}}}

    BackHandler {
        scope.launch {
            runCatching { persistProject() }.onSuccess { onClose() }.onFailure { Toast.makeText(context, R.string.workspace_save_error, Toast.LENGTH_LONG).show() }
        }
    }

    fun runAiCrop() {
        val id = selectedImageId ?: return
        val pageIndex = currentPageIndex
        val image = pages[pageIndex].images.firstOrNull { it.id == id } ?: return
        if (aiBusy || imageEditBusy) return
        val editVersion = dirtyVersion
        aiBusy = true
        scope.launch {
            try {
                val cropped = withContext(Dispatchers.Default) {
                    PdfSubjectCropper.isolateAndCrop(context.applicationContext, image.bitmap) {
                        withContext(Dispatchers.Main.immediate) {
                            Toast.makeText(context, composeResources.getString(R.string.pdf_ai_crop_downloading), Toast.LENGTH_LONG).show()
                        }
                    }
                }
                if (currentPageIndex != pageIndex || dirtyVersion != editVersion || selectedImageId != id) {
                    Toast.makeText(context, composeResources.getString(R.string.pdf_ai_crop_changed), Toast.LENGTH_LONG).show()
                    return@launch
                }
                val updatedImage = image.copy(bitmap = cropped)
                replaceCurrent(
                    pages[currentPageIndex].copy(
                        images = pages[currentPageIndex].images.map { if (it.id == id) updatedImage else it }
                    )
                )
                UiHapticPlayer.play(context, UiHaptic.Confirm)
                Toast.makeText(context, composeResources.getString(R.string.pdf_ai_crop_done), Toast.LENGTH_SHORT).show()
            } catch (error: CancellationException) {
                throw error
            } catch (error: Throwable) {
                Log.w(
                    "PdfSubjectCropper",
                    "AI cutout failed: ${error::class.java.name}: ${error.message}; " +
                        "cause=${error.cause?.javaClass?.name}: ${error.cause?.message}",
                    error
                )
                val message = when {
                    error is PdfSubjectModel.PlayServicesUnavailableException -> R.string.pdf_ai_crop_play_services
                    error is PdfSubjectModel.NoInternetConnectionException -> R.string.pdf_ai_crop_not_ready
                    error is PdfSubjectModel.ModelUnavailableException ||
                        (error is MlKitException && error.errorCode == MlKitException.UNAVAILABLE) -> R.string.pdf_ai_crop_not_ready
                    else -> R.string.pdf_ai_crop_error
                }
                Toast.makeText(context, composeResources.getString(message), Toast.LENGTH_LONG).show()
            } finally {
                aiBusy = false
            }
        }
    }

    if (showImageCropDialog) {
        val image = pages.getOrNull(currentPageIndex)?.images?.firstOrNull { it.id == selectedImageId }
        if (image != null) {
            PdfImageCropDialog(image, fontFamily, { showImageCropDialog = false }) { area ->
                showImageCropDialog = false
                editSelectedImage { selected, page -> PdfImageEdits.crop(selected, page, area) }
            }
        }
    }

    LaunchedEffect(currentPageIndex, sourcePdfUri, pages.size) {
        val sourceUri = sourcePdfUri
        val targetIndex = currentPageIndex
        val current = pages.getOrNull(targetIndex)
        if (sourceUri != null && current?.sourcePdfPageIndex != null && current.background == null) {
            pageLoading = true
            try {
                val rendered = PdfDocumentEngine.renderPdfPage(context, sourceUri, current.sourcePdfPageIndex)
                if (currentPageIndex == targetIndex && sourcePdfUri == sourceUri) {
                    pages = pages.mapIndexed { index, page ->
                        when {
                            index == targetIndex -> page.copy(
                                widthPt = rendered.widthPt,
                                heightPt = rendered.heightPt,
                                background = rendered.bitmap
                            )
                            page.sourcePdfPageIndex != null &&
                                page.background != null &&
                                abs(index - targetIndex) > 1 -> page.copy(background = null)
                            else -> page
                        }
                    }
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Throwable) {
                Toast.makeText(context, composeResources.getString(R.string.pdf_error_page), Toast.LENGTH_LONG).show()
            } finally {
                pageLoading = false
            }
        } else if (sourceUri != null) {
            val trimmed = pages.mapIndexed { index, page ->
                if (
                    page.sourcePdfPageIndex != null &&
                    page.background != null &&
                    abs(index - currentPageIndex) > 1
                ) page.copy(background = null) else page
            }
            if (trimmed != pages) pages = trimmed
        }
    }

    if (showGoToPageDialog) {
        AlertDialog(
            onDismissRequest = {
                showGoToPageDialog = false
                goToPageError = false
            },
            properties = DialogProperties(decorFitsSystemWindows = false),
            title = {
                HideDialogNavigationBar(preventFocusFlash = true)
                Text(stringResource(R.string.pdf_go_to_page), fontFamily = fontFamily)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        stringResource(R.string.pdf_go_to_page_range, pages.size),
                        fontFamily = fontFamily
                    )
                    OutlinedTextField(
                        value = goToPageInput,
                        onValueChange = { value ->
                            val nextInput = value.filter { it.isDigit() }.take(6)
                            UiSoundPlayer.playTextInput(context, goToPageInput, nextInput)
                            goToPageInput = nextInput
                            goToPageError = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(stringResource(R.string.pdf_page_number), fontFamily = fontFamily) },
                        singleLine = true,
                        isError = goToPageError,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        supportingText = if (goToPageError) {
                            { Text(stringResource(R.string.pdf_invalid_page), fontFamily = fontFamily) }
                        } else null
                    )
                }
            },
            confirmButton = {
                AppTextButton(stringResource(R.string.pdf_go),
                    onClick = {
                        val requested = goToPageInput.toIntOrNull()
                        if (requested != null && requested in 1..pages.size) {
                            currentPageIndex = requested - 1
                            clearSelection()
                            showGoToPageDialog = false
                            goToPageError = false
                            UiSoundPlayer.playAction(context, UiActionSound.Navigation)
                        } else {
                            goToPageError = true
                        }
                    }, fontFamily = fontFamily)
            },
            dismissButton = {
                AppTextButton(stringResource(android.R.string.cancel),
                    onClick = {
                        showGoToPageDialog = false
                        goToPageError = false
                    }, fontFamily = fontFamily)
            }
        )
    }

    if (showTextDialog) {
        PreparedPdfAlertDialog(
            onDismissRequest = { showTextDialog = false },
            title = {
                Text(stringResource(R.string.pdf_add_text), fontFamily = fontFamily)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = pendingText,
                        onValueChange = {
                            UiSoundPlayer.playTextInput(context, pendingText, it)
                            pendingText = it
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(stringResource(R.string.pdf_text), fontFamily = fontFamily) },
                        minLines = 3
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(stringResource(R.string.pdf_text_size), fontFamily = fontFamily)
                        ToolChoiceButton("−", false, fontFamily, textColorMode = settings.textColor) { textSize = (textSize - 2f).coerceAtLeast(10f) }
                        Text("${textSize.roundToInt()} pt", fontFamily = fontFamily)
                        ToolChoiceButton("+", false, fontFamily, textColorMode = settings.textColor) { textSize = (textSize + 2f).coerceAtMost(48f) }
                    }
                }
            },
            confirmButton = {
                AppTextButton(stringResource(R.string.pdf_insert),
                    onClick = {
                        val point = pendingTextPosition ?: PdfPoint(0.12f, 0.15f)
                        if (pendingText.isNotBlank()) {
                            val element = PdfTextElement(
                                id = nextElementId++,
                                text = pendingText.trim(),
                                x = point.x.coerceIn(0.02f, 0.95f),
                                y = point.y.coerceIn(0.04f, 0.95f),
                                sizePt = textSize,
                                colorArgb = penColor
                            )
                            replaceCurrent(pages[currentPageIndex].copy(texts = pages[currentPageIndex].texts + element))
                            setSelection(textId = element.id)
                            tool = PdfEditorTool.SELECT
                            UiSoundPlayer.playAction(context, UiActionSound.Add)
                        }
                        pendingText = ""
                        showTextDialog = false
                    }, fontFamily = fontFamily)
            },
            dismissButton = {
                AppTextButton(stringResource(android.R.string.cancel), onClick = { showTextDialog = false }, fontFamily = fontFamily)
            }
        )
    }

    if (showSearch) PdfSearchDialog(projectId, sourcePdfUri, pages, onPage = { currentPageIndex = it; clearSelection() }, onDismiss = { showSearch = false })
    if (showSignatures) PdfSignatureDialog(onBitmap = ::insertBitmap, onDismiss = { showSignatures = false })
    if (showExportRange) PdfRangeDialog(pages.size, onExport = { indices ->
        pendingExport = indices.map { pages[it] }; pendingExportSource = sourcePdfUri
        pendingExportFull = indices.size == pages.size
        showExportRange = false
        scope.launch {
            runCatching { persistProject() }
            exportLauncher.launch("MyNotes_${SimpleDateFormat("yyyyMMdd_HHmm", Locale.US).format(Date())}.pdf")
        }
    }, onDismiss = { showExportRange = false })
    WorkspaceDropReceiver(onAssets = { assets -> assets.firstOrNull { it.mimeType == "application/pdf" }?.let { importPdf(it.uri, it.name) }
        ?: assets.filter { it.mimeType.startsWith("image/") }.forEach { importImage(it.uri) }
    }, onError = { Toast.makeText(context, R.string.workspace_drop_error, Toast.LENGTH_SHORT).show() })

    PaletteWorkspaceTheme(settings.surfacePanelIntensity) {
        Column(modifier = Modifier.fillMaxSize().appScreenBackground().statusBarsPadding().navigationBarsPadding()
            .then(workspaceShortcuts({ scope.launch { runCatching { persistProject() } } }, ::undo, ::redo, { showSearch = true }))) {
            PdfTopBar(
                fontFamily = fontFamily,
                textColorMode = settings.textColor,
                primaryText = primaryText,
                secondaryText = secondaryText,
                onBack = {
                    UiSoundPlayer.playAction(context, UiActionSound.Back)
                    scope.launch {
                        runCatching { persistProject() }.onSuccess { onClose() }.onFailure { Toast.makeText(context, R.string.workspace_save_error, Toast.LENGTH_LONG).show() }
                    }
                },
                onSave = { showExportRange = true }

            )

            val page = pages[currentPageIndex]
            if (showTestModeNotice) PdfTestModeNotice(settings, fontFamily, { showTestModeNotice = false }, Modifier.padding(horizontal = 12.dp))
            com.example.mynotes.studio.PlusStatusPill(showSaved = false)
            AdaptiveWorkspace(
                modifier = Modifier.weight(1f).padding(horizontal = 10.dp, vertical = 6.dp),
                expandedTools = pdfToolsExpanded,
                toolPanelIntensity = settings.surfacePanelIntensity,
                toolsDrawer = true,
                drawerTitleRes = R.string.pdf_tools_panel_title,
                // La lista de acciones PDF es extensa; mostrarla con scroll dentro de
                // un panel de altura limitada, evitando que cubra todo el documento.
                drawerMaxHeightFraction = 0.58f,
                compactTools = true,
                onDrawerOpenChange = { pdfToolsExpanded = it },
                content = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 4.dp)
                        .clipToBounds(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    PdfPageCanvas(
                        page = page,
                        backgroundColor = if (projectReady && !hasMeaningfulProject())
                            MaterialTheme.colorScheme.surfaceContainerLow else Color.White,
                        pageKey = currentPageIndex,
                        stylusOnly = stylusOnly,
                        tool = tool,
                        penColor = penColor,
                        penWidth = penWidth,
                        selectedImageId = selectedImageId,
                        selectedTextId = selectedTextId,
                        selectedStrokeId = selectedStrokeId,
                        onEditGesture = ::editGesture,
                        onStrokeFinished = { stroke ->
                            val editableStroke = if (stroke.id > 0L) stroke else stroke.copy(id = nextElementId++)
                            replaceCurrent(pages[currentPageIndex].copy(strokes = pages[currentPageIndex].strokes + editableStroke))
                        },
                        onEraseAt = { point ->
                            val livePage = pages[currentPageIndex]
                            val threshold = 0.035f
                            val filtered = livePage.strokes.filterNot { stroke ->
                                stroke.points.any { p -> abs(p.x - point.x) < threshold && abs(p.y - point.y) < threshold }
                            }
                            if (filtered.size != livePage.strokes.size) {
                                replaceCurrent(livePage.copy(strokes = filtered), recordHistory = false)
                            }
                        },
                        onTextTap = { point ->
                            pendingTextPosition = point
                            pendingText = ""
                            showTextDialog = true
                        },
                        onSelect = { imageId, textId, strokeId ->
                            selectedImageId = imageId
                            selectedTextId = textId
                            selectedStrokeId = strokeId
                        },
                        onMoveImage = { id, dx, dy ->
                            replaceCurrent(
                                pages[currentPageIndex].copy(
                                    images = pages[currentPageIndex].images.map { image ->
                                        if (image.id != id) image else image.copy(
                                            x = (image.x + dx).coerceIn(0f, 1f - image.width),
                                            y = (image.y + dy).coerceIn(0f, 1f - image.height)
                                        )
                                    }
                                ),
                                recordHistory = false
                            )
                        },
                        onResizeImage = { id, dx, dy ->
                            val livePage = pages[currentPageIndex]
                            val currentImage = livePage.images.firstOrNull { it.id == id }
                            if (currentImage != null) {
                                val normalizedDx = dx / currentImage.width.coerceAtLeast(0.001f)
                                val normalizedDy = dy / currentImage.height.coerceAtLeast(0.001f)
                                val factor = (1f + if (abs(normalizedDx) >= abs(normalizedDy)) normalizedDx else normalizedDy)
                                    .coerceIn(0.20f, 5f)

                                val aspect = (currentImage.height / currentImage.width.coerceAtLeast(0.001f))
                                    .coerceAtLeast(0.05f)
                                val maxWidth = (1f - currentImage.x).coerceAtLeast(0.08f)
                                val maxHeight = (1f - currentImage.y).coerceAtLeast(0.05f)

                                var newWidth = (currentImage.width * factor).coerceIn(0.08f, maxWidth)
                                var newHeight = newWidth * aspect
                                if (newHeight > maxHeight) {
                                    newHeight = maxHeight
                                    newWidth = (newHeight / aspect).coerceIn(0.08f, maxWidth)
                                }

                                replaceCurrent(
                                    livePage.copy(
                                        images = livePage.images.map { image ->
                                            if (image.id == id) image.copy(width = newWidth, height = newHeight) else image
                                        }
                                    ),
                                    recordHistory = false
                                )
                            }
                        },
                        onMoveText = { id, dx, dy ->
                            replaceCurrent(
                                pages[currentPageIndex].copy(
                                    texts = pages[currentPageIndex].texts.map { text ->
                                        if (text.id != id) text else text.copy(
                                            x = (text.x + dx).coerceIn(0.01f, 0.97f),
                                            y = (text.y + dy).coerceIn(0.03f, 0.97f)
                                        )
                                    }
                                ),
                                recordHistory = false
                            )
                        },
                        onMoveStroke = { id, dx, dy ->
                            val livePage = pages[currentPageIndex]
                            val stroke = livePage.strokes.firstOrNull { it.id == id }
                            if (stroke != null) {
                                val bounds = strokeBounds(stroke)
                                if (bounds != null) {
                                    val safeDx = dx.coerceIn(-bounds.left, 1f - bounds.right)
                                    val safeDy = dy.coerceIn(-bounds.top, 1f - bounds.bottom)
                                    replaceCurrent(
                                        livePage.copy(
                                            strokes = livePage.strokes.map { item ->
                                                if (item.id == id) item.copy(
                                                    points = item.points.map { point ->
                                                        point.copy(x = point.x + safeDx, y = point.y + safeDy)
                                                    }
                                                ) else item
                                            }
                                        ),
                                        recordHistory = false
                                    )
                                }
                            }
                        },
                        onResizeStroke = { id, dx, dy ->
                            val livePage = pages[currentPageIndex]
                            val stroke = livePage.strokes.firstOrNull { it.id == id }
                            val bounds = stroke?.let(::strokeBounds)
                            if (stroke != null && bounds != null) {
                                val width = (bounds.right - bounds.left).coerceAtLeast(0.002f)
                                val height = (bounds.bottom - bounds.top).coerceAtLeast(0.002f)
                                val relativeDx = dx / width
                                val relativeDy = dy / height
                                val factor = (1f + if (abs(relativeDx) >= abs(relativeDy)) relativeDx else relativeDy)
                                    .coerceIn(0.20f, 5f)
                                val maxFactorX = ((1f - bounds.left) / width).coerceAtLeast(0.20f)
                                val maxFactorY = ((1f - bounds.top) / height).coerceAtLeast(0.20f)
                                val safeFactor = factor.coerceAtMost(minOf(maxFactorX, maxFactorY))
                                replaceCurrent(
                                    livePage.copy(
                                        strokes = livePage.strokes.map { item ->
                                            if (item.id == id) item.copy(
                                                points = item.points.map { point ->
                                                    point.copy(
                                                        x = (bounds.left + (point.x - bounds.left) * safeFactor).coerceIn(0f, 1f),
                                                        y = (bounds.top + (point.y - bounds.top) * safeFactor).coerceIn(0f, 1f)
                                                    )
                                                }
                                            ) else item
                                        }
                                    ),
                                    recordHistory = false
                                )
                            }
                        },
                        onTransformingChanged = { transforming ->
                            canvasTransforming = transforming
                        }
                    )
                    if (projectReady && !hasMeaningfulProject()) {
                        PdfCanvasOpenButton(
                            settings = settings,
                            fontFamily = fontFamily,
                            onClick = { openPdfLauncher.launch(arrayOf("application/pdf")) },
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    if (aiBusy || pageLoading || imageEditBusy) {
                        Surface(shape = CircleShape, color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f)) {
                            CircularProgressIndicator(modifier = Modifier.padding(18.dp).size(34.dp))
                        }
                    }
                }
            }, supporting = {
                // Las herramientas más usadas aparecen primero; las utilidades PDF avanzadas
                // quedan en filas horizontales desplazables al final del mismo panel.
                PdfFloatingToolbars(
                    settings = settings,
                    fontFamily = fontFamily,
                    tool = tool,
                    penColor = penColor,
                    penWidth = penWidth,
                    pageIndex = currentPageIndex,
                    pageCount = pages.size,
                    pageHasEdits = pages[currentPageIndex].let { it.strokes.isNotEmpty() || it.texts.isNotEmpty() || it.images.isNotEmpty() },
                    selectedImage = pages[currentPageIndex].images.firstOrNull { it.id == selectedImageId },
                    selectedText = pages[currentPageIndex].texts.firstOrNull { it.id == selectedTextId },
                    selectedStroke = pages[currentPageIndex].strokes.firstOrNull { it.id == selectedStrokeId },
                    selectionTemporarilyDisabled = canvasTransforming,
                    canUndo = history.isNotEmpty(),
                    canRedo = redoHistory.isNotEmpty(),
                    onTool = {
                        tool = it
                        UiSoundPlayer.playAction(context, UiActionSound.Select)
                    },
                    onColor = {
                        penColor = it
                        UiSoundPlayer.playAction(context, UiActionSound.Color)
                    },
                    onPenWidth = {
                        penWidth = it
                        UiSoundPlayer.play(context, UiSound.SliderTick)
                    },
                    onAddImage = { imageLauncher.launch(arrayOf("image/png", "image/jpeg", "image/webp")) },
                    onAddPage = {
                        UiSoundPlayer.playAction(context, UiActionSound.Add)
                        commit(pages + PdfPageModel.blank())
                        currentPageIndex = pages.lastIndex
                        clearSelection()
                    },
                    onDuplicatePage = {
                        val copy = pages[currentPageIndex].copy()
                        val updated = pages.toMutableList().apply { add(currentPageIndex + 1, copy) }
                        commit(updated)
                        currentPageIndex = (currentPageIndex + 1).coerceAtMost(updated.lastIndex)
                        clearSelection()
                        UiSoundPlayer.playAction(context, UiActionSound.Add)
                    },
                    onDeletePage = {
                        if (pages.size > 1) {
                            val updated = pages.toMutableList().apply { removeAt(currentPageIndex) }
                            commit(updated)
                            currentPageIndex = currentPageIndex.coerceAtMost(updated.lastIndex)
                        } else {
                            replaceCurrent(PdfPageModel.blank())
                        }
                        clearSelection()
                        UiSoundPlayer.play(context, UiSound.Delete)
                    },
                    onClearPageEdits = {
                        val current = pages[currentPageIndex]
                        replaceCurrent(current.copy(strokes = emptyList(), texts = emptyList(), images = emptyList()))
                        clearSelection()
                        UiSoundPlayer.play(context, UiSound.Delete)
                    },
                    onPreviousPage = { navigatePage(-1) },
                    onNextPage = { navigatePage(1) },
                    onGoToPage = {
                        goToPageInput = (currentPageIndex + 1).toString()
                        goToPageError = false
                        showGoToPageDialog = true
                        UiSoundPlayer.playAction(context, UiActionSound.Select)
                    },
                    onUndo = {
                        undo()
                        UiSoundPlayer.playAction(context, UiActionSound.Back)
                    },
                    onRedo = {
                        redo()
                        UiSoundPlayer.playAction(context, UiActionSound.Select)
                    },
                    onAiCrop = { runAiCrop() },
                    onFlipImage = {
                        editSelectedImage { selected, _ -> PdfImageEdits.flip(selected, horizontal = true) }
                    },
                    onCropImage = {
                        if (!imageEditBusy && !aiBusy && selectedImageId != null) {
                            showImageCropDialog = true
                            UiSoundPlayer.playAction(context, UiActionSound.Select)
                        }
                    },
                    onScaleImage = { factor ->
                        selectedImageId?.let { id ->
                            pages[currentPageIndex].images.firstOrNull { it.id == id }?.let { image ->
                                val centerX = image.x + image.width / 2f
                                val centerY = image.y + image.height / 2f
                                val newWidth = (image.width * factor).coerceIn(0.08f, 0.92f)
                                val newHeight = (image.height * factor).coerceIn(0.05f, 0.92f)
                                replaceCurrent(
                                    pages[currentPageIndex].copy(images = pages[currentPageIndex].images.map {
                                        if (it.id == id) it.copy(
                                            width = newWidth,
                                            height = newHeight,
                                            x = (centerX - newWidth / 2f).coerceIn(0f, 1f - newWidth),
                                            y = (centerY - newHeight / 2f).coerceIn(0f, 1f - newHeight)
                                        ) else it
                                    })
                                )
                                UiSoundPlayer.play(context, UiSound.SliderTick)
                            }
                        }
                    },
                    onRotateImage = {
                        selectedImageId?.let { id ->
                            replaceCurrent(
                                pages[currentPageIndex].copy(images = pages[currentPageIndex].images.map {
                                    if (it.id == id) it.copy(rotationDegrees = (it.rotationDegrees + 90f) % 360f) else it
                                })
                            )
                            UiSoundPlayer.playAction(context, UiActionSound.Move)
                        }
                    },
                    onDuplicateSelected = {
                        when {
                            selectedImageId != null -> {
                                val source = pages[currentPageIndex].images.firstOrNull { it.id == selectedImageId }
                                source?.let {
                                    val duplicate = it.copy(
                                        id = nextElementId++,
                                        x = (it.x + 0.035f).coerceIn(0f, 1f - it.width),
                                        y = (it.y + 0.035f).coerceIn(0f, 1f - it.height)
                                    )
                                    replaceCurrent(pages[currentPageIndex].copy(images = pages[currentPageIndex].images + duplicate))
                                    setSelection(imageId = duplicate.id)
                                }
                            }
                            selectedStrokeId != null -> {
                                val source = pages[currentPageIndex].strokes.firstOrNull { it.id == selectedStrokeId }
                                source?.let { stroke ->
                                    val bounds = strokeBounds(stroke)
                                    val offsetX = bounds?.let { minOf(0.03f, 1f - it.right) } ?: 0.03f
                                    val offsetY = bounds?.let { minOf(0.03f, 1f - it.bottom) } ?: 0.03f
                                    val duplicate = stroke.copy(
                                        id = nextElementId++,
                                        points = stroke.points.map { point ->
                                            point.copy(
                                                x = (point.x + offsetX).coerceIn(0f, 1f),
                                                y = (point.y + offsetY).coerceIn(0f, 1f)
                                            )
                                        }
                                    )
                                    replaceCurrent(pages[currentPageIndex].copy(strokes = pages[currentPageIndex].strokes + duplicate))
                                    setSelection(strokeId = duplicate.id)
                                }
                            }
                            selectedTextId != null -> {
                                val source = pages[currentPageIndex].texts.firstOrNull { it.id == selectedTextId }
                                source?.let {
                                    val duplicate = it.copy(
                                        id = nextElementId++,
                                        x = (it.x + 0.03f).coerceIn(0.01f, 0.97f),
                                        y = (it.y + 0.03f).coerceIn(0.03f, 0.97f)
                                    )
                                    replaceCurrent(pages[currentPageIndex].copy(texts = pages[currentPageIndex].texts + duplicate))
                                    setSelection(textId = duplicate.id)
                                }
                            }
                        }
                        UiSoundPlayer.playAction(context, UiActionSound.Add)
                    },
                    onResizeSelectedText = { factor ->
                        selectedTextId?.let { id ->
                            replaceCurrent(
                                pages[currentPageIndex].copy(texts = pages[currentPageIndex].texts.map {
                                    if (it.id == id) it.copy(sizePt = (it.sizePt * factor).coerceIn(8f, 72f)) else it
                                })
                            )
                            UiSoundPlayer.play(context, UiSound.SliderTick)
                        }
                    },
                    onApplyColorToSelectedText = {
                        selectedTextId?.let { id ->
                            replaceCurrent(
                                pages[currentPageIndex].copy(texts = pages[currentPageIndex].texts.map {
                                    if (it.id == id) it.copy(colorArgb = penColor) else it
                                })
                            )
                            UiSoundPlayer.playAction(context, UiActionSound.Color)
                        }
                    },
                    onApplyColorToSelectedStroke = { color ->
                        selectedStrokeId?.let { id ->
                            replaceCurrent(
                                pages[currentPageIndex].copy(strokes = pages[currentPageIndex].strokes.map { stroke ->
                                    if (stroke.id == id) stroke.copy(colorArgb = color) else stroke
                                })
                            )
                            penColor = color
                            UiSoundPlayer.playAction(context, UiActionSound.Color)
                        }
                    },
                    onSetSelectedStrokeWidth = { width ->
                        selectedStrokeId?.let { id ->
                            replaceCurrent(
                                pages[currentPageIndex].copy(strokes = pages[currentPageIndex].strokes.map { stroke ->
                                    if (stroke.id == id) stroke.copy(widthPt = width) else stroke
                                })
                            )
                            penWidth = width
                            UiSoundPlayer.play(context, UiSound.SliderTick)
                        }
                    },
                    onDeleteSelected = {
                        when {
                            selectedImageId != null -> {
                                val id = selectedImageId
                                replaceCurrent(pages[currentPageIndex].copy(images = pages[currentPageIndex].images.filterNot { it.id == id }))
                                selectedImageId = null
                            }
                            selectedStrokeId != null -> {
                                val id = selectedStrokeId
                                replaceCurrent(pages[currentPageIndex].copy(strokes = pages[currentPageIndex].strokes.filterNot { it.id == id }))
                                selectedStrokeId = null
                            }
                            selectedTextId != null -> {
                                val id = selectedTextId
                                replaceCurrent(pages[currentPageIndex].copy(texts = pages[currentPageIndex].texts.filterNot { it.id == id }))
                                selectedTextId = null
                            }
                        }
                        UiSoundPlayer.play(context, UiSound.Delete)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                // Acciones rápidas: búsqueda, firma, marcador y explorador de páginas.
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    WorkspaceToolIconButton(Icons.Default.Search, stringResource(R.string.workspace_find), onClick = { showSearch = true })
                    WorkspaceToolIconButton(Icons.Default.Draw, stringResource(R.string.workspace_signature), onClick = { showSignatures = true })
                    WorkspaceToolIconButton(if (page.bookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        stringResource(if (page.bookmarked) R.string.workspace_unbookmark else R.string.workspace_bookmark),
                        onClick = { replaceCurrent(page.copy(bookmarked = !page.bookmarked)) })
                    WorkspaceToolIconButton(Icons.Default.PictureAsPdf, stringResource(R.string.workspace_pages), onClick = { showPageBrowser = !showPageBrowser })
                }
                if (showPageBrowser) PdfPageBrowser(pages, sourcePdfUri, currentPageIndex) { currentPageIndex = it; clearSelection() }
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(stringResource(R.string.workspace_stylus_only), Modifier.weight(1f)); Switch(stylusOnly, { stylusOnly = it }, colors = workspaceToolSwitchColors())
                }
                // Utilidades de PDF: siguen disponibles sin ocupar varias filas del panel.
                com.example.mynotes.studio.StudioPdfTools(projectId,pages,sourcePdfUri,currentPageIndex,studioTool,onStudioConsumed,onPage={currentPageIndex=it;clearSelection()},onReplace=::commit,onSource={uri,models->sourcePdfUri=uri;commit(models);history=emptyList();redoHistory=emptyList();markDirty()})
                com.example.mynotes.studio.PlusPdfTools(projectId,projectName,pages,sourcePdfUri,currentPageIndex,studioTool,onStudioConsumed)
            })
        }
    }
}

@Composable
private fun PdfCanvasOpenButton(
    settings: AppSettings,
    fontFamily: FontFamily,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val canvasBackground = MaterialTheme.colorScheme.surfaceContainerLow
    val colors = rememberAdaptiveUiButtonColors(
        MaterialTheme.colorScheme.surfaceContainerHigh, canvasBackground, settings.textColor, 7.0f, 1.35f
    )
    val fontScale = (settings.fontSize / 16f).coerceIn(0.82f, 1.20f)

    Surface(
        modifier = modifier.clickable(onClick = { UiSoundPlayer.runWithFallbackClick(UiActionSound.Open, onClick) }),
        color = colors.container,
        contentColor = colors.content,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 22.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(9.dp)
        ) {
            AppIconLabel(Icons.Default.FolderOpen, stringResource(R.string.pdf_open_pdf),
                iconModifier = Modifier.size(settings.iconSize.coerceIn(18f, 24f).dp), fontFamily = fontFamily,
                fontWeight = FontWeight.SemiBold, fontSize = (14f * fontScale).coerceIn(12.5f, 17f).sp)
        }
    }
}

@Composable
private fun PdfTestModeNotice(
    settings: AppSettings,
    fontFamily: FontFamily,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var info by remember { mutableStateOf(false) }
    if (info) AlertDialog(
        onDismissRequest = { info = false },
        properties = DialogProperties(decorFitsSystemWindows = false),
        title = {
            HideDialogNavigationBar(preventFocusFlash = true)
            Text(stringResource(R.string.pdf_test_mode_title))
        },
        text = { Text(stringResource(R.string.pdf_test_mode_description)) },
        confirmButton = {
            TextButton(onClick = {
                info = false
                UiSoundPlayer.playFallbackClick()
            }) { Text(stringResource(R.string.workspace_done)) }
        }
    )
    Surface(modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium, color = MaterialTheme.colorScheme.surfaceContainerHigh) {
        Row(Modifier.padding(start = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(R.string.workspace_beta), Modifier.weight(1f), style = MaterialTheme.typography.labelMedium, maxLines = 2)
            AppIconButton(Icons.Default.Info, stringResource(R.string.workspace_info), onClick = { info = true })
            AppIconButton(Icons.Default.Close, stringResource(R.string.pdf_test_mode_close), onClick = onDismiss)
        }
    }
}

@Composable
private fun PdfTopBar(
    fontFamily: FontFamily,
    textColorMode: String,
    primaryText: Color,
    secondaryText: Color,
    onBack: () -> Unit,
    onSave: () -> Unit
) {
    val topBarBackground = MaterialTheme.colorScheme.background
    val exportColors = rememberAdaptiveUiButtonColors(MaterialTheme.colorScheme.primary, topBarBackground, textColorMode)
    val exportContainer = exportColors.container
    val exportContent = exportColors.content
    BoxWithConstraints(Modifier.fillMaxWidth()) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        AppCircularIconButton(Icons.Default.ArrowBack, stringResource(R.string.pdf_back), onClick = onBack,
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh, tint = primaryText)
        Column(modifier = Modifier.weight(1f)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(R.string.pdf_studio),
                    modifier = Modifier.weight(1f).padding(end = 8.dp),
                    color = primaryText, fontFamily = fontFamily,
                    fontWeight = FontWeight.Bold, fontSize = 25.sp,maxLines=2,overflow=androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
                Button(
                    onClick = { UiSoundPlayer.runWithFallbackClick(UiActionSound.Save, onSave) },
                    modifier = Modifier.heightIn(min = 48.dp).widthIn(min = 96.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = exportContainer,
                        contentColor = exportContent,
                        disabledContainerColor = compositeUiColor(
                            primaryText.copy(alpha = 0.12f), topBarBackground
                        ),
                        disabledContentColor = primaryText.copy(alpha = 0.50f)
                    ),
                    contentPadding = PaddingValues(horizontal = 18.dp, vertical = 0.dp)
                ) {
                    Text(
                        text = stringResource(R.string.pdf_export),
                        fontFamily = fontFamily,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }

            }
            Text(
                text = stringResource(R.string.pdf_studio_subtitle),
                modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                color = secondaryText, fontFamily = fontFamily, fontSize = 13.sp,
                softWrap = true
            )
        }
    }
    }
}

private fun strokeBounds(stroke: PdfStroke): RectF? {
    if (stroke.points.isEmpty()) return null
    var left = stroke.points.first().x
    var right = left
    var top = stroke.points.first().y
    var bottom = top
    stroke.points.drop(1).forEach { point ->
        left = minOf(left, point.x)
        right = maxOf(right, point.x)
        top = minOf(top, point.y)
        bottom = maxOf(bottom, point.y)
    }
    return RectF(left, top, right, bottom)
}

private fun squaredDistanceToSegment(point: PdfPoint, a: PdfPoint, b: PdfPoint): Float {
    val abX = b.x - a.x
    val abY = b.y - a.y
    val lengthSquared = abX * abX + abY * abY
    if (lengthSquared <= 0.0000001f) {
        val dx = point.x - a.x
        val dy = point.y - a.y
        return dx * dx + dy * dy
    }
    val t = (((point.x - a.x) * abX + (point.y - a.y) * abY) / lengthSquared).coerceIn(0f, 1f)
    val projectionX = a.x + t * abX
    val projectionY = a.y + t * abY
    val dx = point.x - projectionX
    val dy = point.y - projectionY
    return dx * dx + dy * dy
}

private fun findStrokeAt(page: PdfPageModel, point: PdfPoint): PdfStroke? {
    val threshold = 0.026f
    val thresholdSquared = threshold * threshold
    return page.strokes.asReversed().firstOrNull { stroke ->
        val closedShapeHit = if (stroke.points.size >= 4) {
            val first = stroke.points.first()
            val last = stroke.points.last()
            val closesOnItself = abs(first.x - last.x) < 0.01f && abs(first.y - last.y) < 0.01f
            val bounds = if (closesOnItself) strokeBounds(stroke) else null
            bounds != null &&
                point.x >= bounds.left - threshold && point.x <= bounds.right + threshold &&
                point.y >= bounds.top - threshold && point.y <= bounds.bottom + threshold
        } else false

        closedShapeHit || if (stroke.points.size == 1) {
            squaredDistanceToSegment(point, stroke.points.first(), stroke.points.first()) <= thresholdSquared
        } else {
            stroke.points.zipWithNext().any { (a, b) ->
                squaredDistanceToSegment(point, a, b) <= thresholdSquared
            }
        }
    }
}

private fun selectionIdsAt(page: PdfPageModel, offset: Offset, size: IntSize): Triple<Long?, Long?, Long?> {
    val nx = offset.x / size.width
    val ny = offset.y / size.height
    val image = page.images.asReversed().firstOrNull {
        nx >= it.x && nx <= it.x + it.width && ny >= it.y && ny <= it.y + it.height
    }
    val text = if (image == null) page.texts.asReversed().firstOrNull {
        nx >= it.x && nx <= it.x + 0.45f && ny >= it.y - 0.06f && ny <= it.y + 0.10f
    } else null
    val stroke = if (image == null && text == null) findStrokeAt(page, PdfPoint(nx, ny)) else null
    return Triple(image?.id, text?.id, stroke?.id)
}

private suspend fun androidx.compose.ui.input.pointer.AwaitPointerEventScope.awaitPdfDrawingPointer(stylusOnly: Boolean): androidx.compose.ui.input.pointer.PointerInputChange? {
    val first = awaitFirstDown(requireUnconsumed = false)
    if (!stylusOnly || first.type == androidx.compose.ui.input.pointer.PointerType.Stylus || first.type == androidx.compose.ui.input.pointer.PointerType.Eraser) return first
    while (true) {
        val event = awaitPointerEvent()
        event.changes.firstOrNull { it.pressed && (it.type == androidx.compose.ui.input.pointer.PointerType.Stylus || it.type == androidx.compose.ui.input.pointer.PointerType.Eraser) }?.let { return it }
        if (event.changes.none { it.pressed }) return null
    }
}

@Composable
private fun PdfPageCanvas(
    page: PdfPageModel,
    pageKey: Int,
    stylusOnly: Boolean,
    tool: PdfEditorTool,
    penColor: Int,
    penWidth: Float,
    selectedImageId: Long?,
    selectedTextId: Long?,
    selectedStrokeId: Long?,
    onEditGesture: (Int) -> Unit,
    onStrokeFinished: (PdfStroke) -> Unit,
    onEraseAt: (PdfPoint) -> Unit,
    onTextTap: (PdfPoint) -> Unit,
    onSelect: (Long?, Long?, Long?) -> Unit,
    onMoveImage: (Long, Float, Float) -> Unit,
    onResizeImage: (Long, Float, Float) -> Unit,
    onMoveText: (Long, Float, Float) -> Unit,
    onMoveStroke: (Long, Float, Float) -> Unit,
    onResizeStroke: (Long, Float, Float) -> Unit,
    onTransformingChanged: (Boolean) -> Unit,
    backgroundColor: Color = Color.White
) {
    var transientPoints by remember(tool, pageKey) { mutableStateOf<List<Offset>>(emptyList()) }
    var transientShapeStart by remember(tool, pageKey) { mutableStateOf<Offset?>(null) }
    var transientShapeEnd by remember(tool, pageKey) { mutableStateOf<Offset?>(null) }
    var zoomScale by remember(pageKey) { mutableFloatStateOf(1f) }
    var panOffset by remember(pageKey) { mutableStateOf(Offset.Zero) }
    var transformingCanvas by remember(pageKey) { mutableStateOf(false) }
    val ratio = page.widthPt.toFloat() / page.heightPt.toFloat()
    val backgroundImage = remember(page.background) { page.background?.asImageBitmap() }
    val selectionColor = MaterialTheme.colorScheme.primary
    val highlighterColor = (penColor and 0x00FFFFFF) or (0x66 shl 24)
    val highlighterWidth = (penWidth * 3.2f).coerceIn(8f, 28f)
    val latestPage by rememberUpdatedState(page)
    val latestSelectedImageId by rememberUpdatedState(selectedImageId)
    val latestSelectedStrokeId by rememberUpdatedState(selectedStrokeId)

    LaunchedEffect(pageKey) {
        onTransformingChanged(false)
    }

    Canvas(
        modifier = Modifier
            .fillMaxHeight()
            .aspectRatio(ratio)
            .pointerInput(pageKey) {
                awaitEachGesture {
                    var hadMultiTouch = false
                    try {
                        var event = awaitPointerEvent(PointerEventPass.Initial)
                        while (event.changes.any { it.pressed }) {
                            val pressed = event.changes.filter { it.pressed }
                            if (pressed.size >= 2 && pressed.all { it.type == androidx.compose.ui.input.pointer.PointerType.Touch }) {
                                if (!hadMultiTouch) {
                                    hadMultiTouch = true
                                    onEditGesture(2)
                                    transformingCanvas = true
                                    onTransformingChanged(true)
                                    onSelect(null, null, null)
                                }

                                val first = pressed[0]
                                val second = pressed[1]
                                val previousDistance = (first.previousPosition - second.previousPosition).getDistance()
                                val currentDistance = (first.position - second.position).getDistance()
                                val previousCentroid = Offset(
                                    (first.previousPosition.x + second.previousPosition.x) / 2f,
                                    (first.previousPosition.y + second.previousPosition.y) / 2f
                                )
                                val currentCentroid = Offset(
                                    (first.position.x + second.position.x) / 2f,
                                    (first.position.y + second.position.y) / 2f
                                )

                                if (previousDistance > 0.5f && currentDistance > 0.5f) {
                                    val oldScale = zoomScale
                                    val newScale = (oldScale * (currentDistance / previousDistance)).coerceIn(1f, 4.5f)
                                    val factor = if (oldScale > 0f) newScale / oldScale else 1f
                                    val center = Offset(size.width / 2f, size.height / 2f)
                                    val panDelta = currentCentroid - previousCentroid
                                    var nextPan = panOffset + panDelta -
                                        (previousCentroid - center - panOffset) * (factor - 1f)

                                    if (newScale <= 1.001f) {
                                        nextPan = Offset.Zero
                                    } else {
                                        val maxX = size.width * (newScale - 1f) / 2f
                                        val maxY = size.height * (newScale - 1f) / 2f
                                        nextPan = Offset(
                                            nextPan.x.coerceIn(-maxX, maxX),
                                            nextPan.y.coerceIn(-maxY, maxY)
                                        )
                                    }
                                    zoomScale = newScale
                                    panOffset = nextPan
                                }
                                event.changes.forEach { it.consume() }
                            }
                            event = awaitPointerEvent(PointerEventPass.Initial)
                        }
                    } finally {
                        if (hadMultiTouch) {
                            transformingCanvas = false
                            onTransformingChanged(false)
                        }
                    }
                }
            }
            .graphicsLayer {
                scaleX = zoomScale
                scaleY = zoomScale
                translationX = panOffset.x
                translationY = panOffset.y
            }
            .background(backgroundColor, RoundedCornerShape(22.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.7f), RoundedCornerShape(22.dp))
            .pointerInput(tool, pageKey, transformingCanvas) {
                if (tool == PdfEditorTool.SELECT && !transformingCanvas) {
                    detectTapGestures { offset ->
                        val (imageId, textId, strokeId) = selectionIdsAt(latestPage, offset, size)
                        onSelect(imageId, textId, strokeId)
                    }
                }
            }
            .pointerInput(tool, pageKey, penColor, penWidth, transformingCanvas, stylusOnly) {
                if (transformingCanvas) return@pointerInput
                when (tool) {
                    PdfEditorTool.TEXT -> awaitEachGesture {
                        val down = awaitPdfDrawingPointer(stylusOnly) ?: return@awaitEachGesture
                        val up = waitForUpOrCancellation()
                        if (up != null) { up.consume(); onTextTap(PdfPoint(down.position.x / size.width, down.position.y / size.height)) }
                    }
                    PdfEditorTool.SELECT -> {
                        var activeImageId: Long? = null
                        var activeTextId: Long? = null
                        var activeStrokeId: Long? = null
                        var resizingImageId: Long? = null
                        var resizingStrokeId: Long? = null
                        detectDragGestures(
                            onDragStart = { offset ->
                                onEditGesture(0)
                                val livePage = latestPage
                                val selectedImage = livePage.images.firstOrNull { it.id == latestSelectedImageId }
                                val selectedStroke = livePage.strokes.firstOrNull { it.id == latestSelectedStrokeId }
                                val resizeHandleRadiusPx = 30.dp.toPx()
                                val handleInsetPx = 10.dp.toPx()
                                val touchingImageResizeHandle = selectedImage?.let { image ->
                                    val handle = Offset(
                                        (image.x + image.width) * size.width - handleInsetPx,
                                        (image.y + image.height) * size.height - handleInsetPx
                                    )
                                    (offset - handle).getDistance() <= resizeHandleRadiusPx
                                } == true
                                val touchingStrokeResizeHandle = selectedStroke?.let { stroke ->
                                    strokeBounds(stroke)?.let { bounds ->
                                        val leftPx = bounds.left * size.width
                                        val topPx = bounds.top * size.height
                                        val rightPx = maxOf(bounds.right * size.width, leftPx + 8.dp.toPx())
                                        val bottomPx = maxOf(bounds.bottom * size.height, topPx + 8.dp.toPx())
                                        val handle = Offset(
                                            rightPx - handleInsetPx,
                                            bottomPx - handleInsetPx
                                        )
                                        (offset - handle).getDistance() <= resizeHandleRadiusPx
                                    }
                                } == true

                                when {
                                    touchingImageResizeHandle && selectedImage != null -> {
                                        resizingImageId = selectedImage.id
                                        resizingStrokeId = null
                                        activeImageId = null
                                        activeTextId = null
                                        activeStrokeId = null
                                        onSelect(selectedImage.id, null, null)
                                    }
                                    touchingStrokeResizeHandle && selectedStroke != null -> {
                                        resizingStrokeId = selectedStroke.id
                                        resizingImageId = null
                                        activeImageId = null
                                        activeTextId = null
                                        activeStrokeId = null
                                        onSelect(null, null, selectedStroke.id)
                                    }
                                    else -> {
                                        val (imageId, textId, strokeId) = selectionIdsAt(livePage, offset, size)
                                        activeImageId = imageId
                                        activeTextId = textId
                                        activeStrokeId = strokeId
                                        resizingImageId = null
                                        resizingStrokeId = null
                                        onSelect(imageId, textId, strokeId)
                                    }
                                }
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                resizingImageId?.let { id ->
                                    onResizeImage(id, dragAmount.x / size.width, dragAmount.y / size.height)
                                } ?: resizingStrokeId?.let { id ->
                                    onResizeStroke(id, dragAmount.x / size.width, dragAmount.y / size.height)
                                } ?: activeImageId?.let { id ->
                                    onMoveImage(id, dragAmount.x / size.width, dragAmount.y / size.height)
                                } ?: activeTextId?.let { id ->
                                    onMoveText(id, dragAmount.x / size.width, dragAmount.y / size.height)
                                } ?: activeStrokeId?.let { id ->
                                    onMoveStroke(id, dragAmount.x / size.width, dragAmount.y / size.height)
                                }
                            },
                            onDragEnd = { onEditGesture(1) },
                            onDragCancel = { onEditGesture(2) }
                        )
                    }
                    PdfEditorTool.PEN, PdfEditorTool.HIGHLIGHTER -> awaitEachGesture {
                        val down = awaitPdfDrawingPointer(stylusOnly) ?: return@awaitEachGesture
                        var cancelled = false
                        val samples = mutableListOf(PdfPoint(down.position.x / size.width, down.position.y / size.height,
                            if (down.type == androidx.compose.ui.input.pointer.PointerType.Stylus && tool == PdfEditorTool.PEN) down.pressure.coerceIn(.15f, 2f) else 1f))
                        transientPoints = listOf(down.position)
                        try {
                            down.consume()
                            while (true) {
                                val event = awaitPointerEvent()
                                val change = event.changes.firstOrNull { it.id == down.id } ?: break
                                if ((down.type != androidx.compose.ui.input.pointer.PointerType.Stylus && down.type != androidx.compose.ui.input.pointer.PointerType.Eraser && event.changes.count { it.pressed } > 1) || change.isConsumed) { cancelled = true; break }
                                if (!change.pressed) break
                                samples += PdfPoint((change.position.x / size.width).coerceIn(0f, 1f), (change.position.y / size.height).coerceIn(0f, 1f),
                                    if (change.type == androidx.compose.ui.input.pointer.PointerType.Stylus && tool == PdfEditorTool.PEN) change.pressure.coerceIn(.15f, 2f) else 1f)
                                transientPoints = transientPoints + change.position
                                change.consume()
                            }
                            if (!cancelled) onStrokeFinished(PdfStroke(points = samples, colorArgb = if (tool == PdfEditorTool.HIGHLIGHTER) highlighterColor else penColor,
                                widthPt = if (tool == PdfEditorTool.HIGHLIGHTER) highlighterWidth else penWidth))
                        } finally { transientPoints = emptyList() }
                    }
                    PdfEditorTool.LINE, PdfEditorTool.RECTANGLE, PdfEditorTool.ELLIPSE -> awaitEachGesture {
                        val down = awaitPdfDrawingPointer(stylusOnly) ?: return@awaitEachGesture
                        val start = down.position
                        var end = start
                        var completed = false
                        transientShapeStart = start; transientShapeEnd = start
                        try {
                            down.consume()
                            while (true) {
                                val event = awaitPointerEvent()
                                val change = event.changes.firstOrNull { it.id == down.id } ?: break
                                if (change.isConsumed || (down.type == androidx.compose.ui.input.pointer.PointerType.Touch && event.changes.count { it.pressed } > 1)) break
                                if (!change.pressed) { completed = true; break }
                                end = Offset(change.position.x.coerceIn(0f, size.width.toFloat()), change.position.y.coerceIn(0f, size.height.toFloat()))
                                transientShapeEnd = end; change.consume()
                            }
                            if (completed) buildShapeStroke(tool, PdfPoint(start.x / size.width, start.y / size.height),
                                PdfPoint(end.x / size.width, end.y / size.height), penColor, penWidth)?.let(onStrokeFinished)
                        } finally { transientShapeStart = null; transientShapeEnd = null }
                    }
                    PdfEditorTool.ERASER -> awaitEachGesture {
                        val down = awaitPdfDrawingPointer(stylusOnly) ?: return@awaitEachGesture
                        var completed = false
                        onEditGesture(0)
                        try {
                            onEraseAt(PdfPoint(down.position.x / size.width, down.position.y / size.height)); down.consume()
                            while (true) {
                                val event = awaitPointerEvent()
                                val change = event.changes.firstOrNull { it.id == down.id } ?: break
                                if (change.isConsumed || (down.type == androidx.compose.ui.input.pointer.PointerType.Touch && event.changes.count { it.pressed } > 1)) break
                                if (!change.pressed) { completed = true; break }
                                onEraseAt(PdfPoint(change.position.x / size.width, change.position.y / size.height)); change.consume()
                            }
                        } finally { onEditGesture(if (completed) 1 else 2) }
                    }
                    PdfEditorTool.PAGE -> Unit
                }
            }
    ) {
        backgroundImage?.let { image ->
            drawImage(image = image, dstSize = IntSize(size.width.roundToInt(), size.height.roundToInt()))
        }

        page.strokes.forEach { stroke ->
            val widthPx = stroke.widthPt * size.width / page.widthPt
            if (stroke.points.size == 1) stroke.points.first().let { drawCircle(Color(stroke.colorArgb), widthPx * it.pressure / 2, Offset(it.x * size.width, it.y * size.height)) }
            else if (stroke.points.all { it.pressure == 1f }) {
                val path = Path().apply {
                    stroke.points.firstOrNull()?.let { moveTo(it.x * size.width, it.y * size.height) }
                    stroke.points.drop(1).forEach { lineTo(it.x * size.width, it.y * size.height) }
                }
                drawPath(path, Color(stroke.colorArgb), style = Stroke(width = widthPx, cap = androidx.compose.ui.graphics.StrokeCap.Round))
            } else stroke.points.zipWithNext().forEach { (a, b) ->
                drawLine(Color(stroke.colorArgb), Offset(a.x * size.width, a.y * size.height), Offset(b.x * size.width, b.y * size.height), widthPx * (a.pressure + b.pressure) / 2, androidx.compose.ui.graphics.StrokeCap.Round)
            }

            if (stroke.id == selectedStrokeId) {
                strokeBounds(stroke)?.let { bounds ->
                    val left = bounds.left * size.width
                    val top = bounds.top * size.height
                    val right = bounds.right * size.width
                    val bottom = bounds.bottom * size.height
                    val minSize = 8.dp.toPx()
                    val visualRight = maxOf(right, left + minSize)
                    val visualBottom = maxOf(bottom, top + minSize)
                    drawRect(
                        color = selectionColor,
                        topLeft = Offset(left, top),
                        size = androidx.compose.ui.geometry.Size(visualRight - left, visualBottom - top),
                        style = Stroke(width = 2.5.dp.toPx())
                    )
                    val handleInset = 10.dp.toPx()
                    val handleCenter = Offset(visualRight - handleInset, visualBottom - handleInset)
                    drawCircle(Color.White, radius = 9.dp.toPx(), center = handleCenter)
                    drawCircle(selectionColor, radius = 9.dp.toPx(), center = handleCenter, style = Stroke(width = 2.5.dp.toPx()))
                    drawCircle(selectionColor, radius = 2.8.dp.toPx(), center = handleCenter)
                }
            }
        }

        if (transientPoints.size >= 2) {
            val path = Path().apply {
                moveTo(transientPoints.first().x, transientPoints.first().y)
                transientPoints.drop(1).forEach { lineTo(it.x, it.y) }
            }
            drawPath(
                path = path,
                color = Color(if (tool == PdfEditorTool.HIGHLIGHTER) highlighterColor else penColor),
                style = Stroke(
                    width = (if (tool == PdfEditorTool.HIGHLIGHTER) highlighterWidth else penWidth) * size.width / page.widthPt,
                    cap = androidx.compose.ui.graphics.StrokeCap.Round
                )
            )
        }

        val shapeStart = transientShapeStart
        val shapeEnd = transientShapeEnd
        if (shapeStart != null && shapeEnd != null) {
            buildShapeStroke(
                tool = tool,
                start = PdfPoint(shapeStart.x / size.width, shapeStart.y / size.height),
                end = PdfPoint(shapeEnd.x / size.width, shapeEnd.y / size.height),
                colorArgb = penColor,
                widthPt = penWidth
            )?.let { preview ->
                val path = Path().apply {
                    val first = preview.points.first()
                    moveTo(first.x * size.width, first.y * size.height)
                    preview.points.drop(1).forEach { lineTo(it.x * size.width, it.y * size.height) }
                }
                drawPath(
                    path = path,
                    color = Color(preview.colorArgb),
                    style = Stroke(width = preview.widthPt * size.width / page.widthPt, cap = androidx.compose.ui.graphics.StrokeCap.Round)
                )
            }
        }

        page.images.forEach { image ->
            val bitmap = image.bitmap
            val nativePaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
            val left = image.x * size.width
            val top = image.y * size.height
            val right = (image.x + image.width) * size.width
            val bottom = (image.y + image.height) * size.height
            val rect = RectF(left, top, right, bottom)
            drawIntoCanvas { composeCanvas ->
                val native = composeCanvas.nativeCanvas
                native.save()
                native.rotate(image.rotationDegrees, rect.centerX(), rect.centerY())
                native.drawBitmap(bitmap, null, rect, nativePaint)
                native.restore()
            }
            if (image.id == selectedImageId) {
                drawRect(
                    color = selectionColor,
                    topLeft = Offset(left, top),
                    size = androidx.compose.ui.geometry.Size(right - left, bottom - top),
                    style = Stroke(width = 3.dp.toPx())
                )
                val handleInset = 10.dp.toPx()
                val handleCenter = Offset(right - handleInset, bottom - handleInset)
                drawCircle(color = Color.White, radius = 10.dp.toPx(), center = handleCenter)
                drawCircle(color = selectionColor, radius = 10.dp.toPx(), center = handleCenter, style = Stroke(width = 3.dp.toPx()))
                drawCircle(color = selectionColor, radius = 3.dp.toPx(), center = handleCenter)
            }
        }

        page.texts.forEach { text ->
            val nativePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = text.colorArgb
                textSize = text.sizePt * size.width / page.widthPt
                typeface = android.graphics.Typeface.DEFAULT_BOLD
            }
            val x = text.x * size.width
            var y = text.y * size.height
            val lineHeight = nativePaint.textSize * 1.2f
            drawIntoCanvas { composeCanvas ->
                text.text.lines().forEach { line ->
                    composeCanvas.nativeCanvas.drawText(line, x, y, nativePaint)
                    y += lineHeight
                }
            }
            if (text.id == selectedTextId) {
                drawCircle(selectionColor, radius = 5.dp.toPx(), center = Offset(x, text.y * size.height))
            }
        }
    }
}

private fun buildShapeStroke(
    tool: PdfEditorTool,
    start: PdfPoint,
    end: PdfPoint,
    colorArgb: Int,
    widthPt: Float
): PdfStroke? {
    val left = minOf(start.x, end.x)
    val right = maxOf(start.x, end.x)
    val top = minOf(start.y, end.y)
    val bottom = maxOf(start.y, end.y)
    val points = when (tool) {
        PdfEditorTool.LINE -> listOf(start, end)
        PdfEditorTool.RECTANGLE -> listOf(
            PdfPoint(left, top),
            PdfPoint(right, top),
            PdfPoint(right, bottom),
            PdfPoint(left, bottom),
            PdfPoint(left, top)
        )
        PdfEditorTool.ELLIPSE -> {
            val centerX = (left + right) / 2f
            val centerY = (top + bottom) / 2f
            val radiusX = (right - left) / 2f
            val radiusY = (bottom - top) / 2f
            if (radiusX < 0.002f || radiusY < 0.002f) return null
            (0..48).map { index ->
                val angle = 2.0 * PI * index / 48.0
                PdfPoint(
                    x = (centerX + radiusX * cos(angle).toFloat()).coerceIn(0f, 1f),
                    y = (centerY + radiusY * sin(angle).toFloat()).coerceIn(0f, 1f)
                )
            }
        }
        else -> return null
    }
    if (points.size < 2) return null
    return PdfStroke(points = points, colorArgb = colorArgb, widthPt = widthPt)
}

private data class PdfToolStyle(
    val fontFamily: FontFamily,
    val idleContainer: Color,
    val idleContent: Color,
    val selectedContainer: Color,
    val selectedContent: Color,
    val iconSize: Dp,
    val labelSize: TextUnit,
    val radius: Dp,
    val selectedOutline: Color? = null
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PdfFloatingToolbars(
    settings: AppSettings,
    fontFamily: FontFamily,
    tool: PdfEditorTool,
    penColor: Int,
    penWidth: Float,
    pageIndex: Int,
    pageCount: Int,
    pageHasEdits: Boolean,
    selectedImage: PdfImageElement?,
    selectedText: PdfTextElement?,
    selectedStroke: PdfStroke?,
    selectionTemporarilyDisabled: Boolean,
    canUndo: Boolean,
    canRedo: Boolean,
    onTool: (PdfEditorTool) -> Unit,
    onColor: (Int) -> Unit,
    onPenWidth: (Float) -> Unit,
    onAddImage: () -> Unit,
    onAddPage: () -> Unit,
    onDuplicatePage: () -> Unit,
    onDeletePage: () -> Unit,
    onClearPageEdits: () -> Unit,
    onPreviousPage: () -> Unit,
    onNextPage: () -> Unit,
    onGoToPage: () -> Unit,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onAiCrop: () -> Unit,
    onScaleImage: (Float) -> Unit,
    onRotateImage: () -> Unit,
    onFlipImage: () -> Unit,
    onCropImage: () -> Unit,
    onDuplicateSelected: () -> Unit,
    onResizeSelectedText: (Float) -> Unit,
    onApplyColorToSelectedText: () -> Unit,
    onApplyColorToSelectedStroke: (Int) -> Unit,
    onSetSelectedStrokeWidth: (Float) -> Unit,
    onDeleteSelected: () -> Unit,
    modifier: Modifier = Modifier
) {
    val toolColors = MaterialTheme.colorScheme
    val fontScale = (settings.fontSize / 16f).coerceIn(0.82f, 1.24f)
    val labelSize = (10.5f * fontScale).coerceIn(9f, 13f).sp
    val pageSize = (12f * fontScale).coerceIn(10.5f, 15f).sp
    val iconSize = settings.iconSize.coerceIn(16f, 24f).dp
    val chipRadius = when (settings.iconStyle) {
        "minimal" -> 10.dp
        "material" -> 13.dp
        "outlined" -> 15.dp
        else -> 18.dp
    }
    val toolStyle = PdfToolStyle(
        fontFamily, toolColors.surfaceContainerHigh, toolColors.onSurface,
        toolColors.primaryContainer, toolColors.onPrimaryContainer,
        iconSize, labelSize, chipRadius, selectedOutline = toolColors.outline
    )
    val drawingTool = tool in setOf(
        PdfEditorTool.PEN,
        PdfEditorTool.HIGHLIGHTER,
        PdfEditorTool.LINE,
        PdfEditorTool.RECTANGLE,
        PdfEditorTool.ELLIPSE
    )
    val contextMode = when {
        tool == PdfEditorTool.PAGE -> "page"
        selectionTemporarilyDisabled -> "none"
        tool == PdfEditorTool.SELECT && selectedImage != null -> "image"
        tool == PdfEditorTool.SELECT && selectedText != null -> "selected_text"
        tool == PdfEditorTool.SELECT && selectedStroke != null -> "selected_stroke"
        drawingTool -> "stroke"
        tool == PdfEditorTool.TEXT -> "text"
        else -> "none"
    }
    val toolMotionStyle = rememberMotionStyle(contextMode, settings.animationStyle)

    var group by remember { mutableIntStateOf(0) }
    WorkspaceToolSection(modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                listOf(R.string.workspace_annotate, R.string.workspace_insert, R.string.workspace_pages).forEachIndexed { index, label ->
                    WorkspaceToolFilterChip(group == index, onClick = {
                        UiSoundPlayer.runWithFallbackClick {
                            group = index
                            if (index == 2) onTool(PdfEditorTool.PAGE)
                            else if (tool == PdfEditorTool.PAGE) onTool(PdfEditorTool.SELECT)
                        }
                    }, label = { Text(stringResource(label)) })
                }
            }
            FlowRow(horizontalArrangement = Arrangement.spacedBy(5.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                if (group == 0) {
                    PdfToolChip(stringResource(R.string.pdf_select), Icons.Default.SelectAll, tool == PdfEditorTool.SELECT && !selectionTemporarilyDisabled, toolStyle) { if (!selectionTemporarilyDisabled) onTool(PdfEditorTool.SELECT) }
                    PdfToolChip(stringResource(R.string.pdf_draw), Icons.Default.Brush, tool == PdfEditorTool.PEN, toolStyle) { onTool(PdfEditorTool.PEN) }
                    PdfToolChip(stringResource(R.string.pdf_highlighter), Icons.Default.Highlight, tool == PdfEditorTool.HIGHLIGHTER, toolStyle) { onTool(PdfEditorTool.HIGHLIGHTER) }
                    PdfToolChip(stringResource(R.string.pdf_line), Icons.Default.HorizontalRule, tool == PdfEditorTool.LINE, toolStyle) { onTool(PdfEditorTool.LINE) }
                    PdfToolChip(stringResource(R.string.pdf_rectangle), Icons.Default.CropSquare, tool == PdfEditorTool.RECTANGLE, toolStyle) { onTool(PdfEditorTool.RECTANGLE) }
                    PdfToolChip(stringResource(R.string.pdf_ellipse), Icons.Default.RadioButtonUnchecked, tool == PdfEditorTool.ELLIPSE, toolStyle) { onTool(PdfEditorTool.ELLIPSE) }
                    PdfToolChip(stringResource(R.string.pdf_eraser), Icons.Default.Delete, tool == PdfEditorTool.ERASER, toolStyle) { onTool(PdfEditorTool.ERASER) }
                } else if (group == 1) {
                    PdfToolChip(stringResource(R.string.pdf_text), Icons.Default.TextFields, tool == PdfEditorTool.TEXT, toolStyle) { onTool(PdfEditorTool.TEXT) }
                    PdfToolChip(stringResource(R.string.pdf_add_image), Icons.Default.Image, false, toolStyle, onAddImage)
                }
            }
            AnimatedContent(
                targetState = contextMode,
                transitionSpec = {
                    appMotionTransform(
                        animationsEnabled = settings.animationsEnabled,
                        animationSpeed = settings.animationSpeed,
                        animationStyle = toolMotionStyle,
                        animationEasing = settings.animationEasing,
                        animationIntensity = settings.animationIntensity,
                        performanceMode = settings.performanceMode
                    )
                },
                label = "pdfToolContext"
            ) { mode ->
                when (mode) {
                    "stroke", "text" -> {
                        PdfToolRow(7.dp) {
                            PdfPaletteChoices(penColor, onColor)
                            if (mode == "stroke") {
                                ToolChoiceButton(stringResource(R.string.pdf_thin), penWidth == 2f, toolStyle) { onPenWidth(2f) }
                                ToolChoiceButton(stringResource(R.string.pdf_medium), penWidth == 5f, toolStyle) { onPenWidth(5f) }
                                ToolChoiceButton(stringResource(R.string.pdf_thick), penWidth == 10f, toolStyle) { onPenWidth(10f) }
                            }
                        }
                    }
                    "selected_stroke" -> {
                        PdfToolRow(7.dp) {
                            PdfPaletteChoices(selectedStroke?.colorArgb, onApplyColorToSelectedStroke)
                            ToolChoiceButton(stringResource(R.string.pdf_thin), (selectedStroke?.widthPt ?: 0f) <= 2.5f, toolStyle) { onSetSelectedStrokeWidth(2f) }
                            ToolChoiceButton(stringResource(R.string.pdf_medium), (selectedStroke?.widthPt ?: 0f) in 2.5f..7f, toolStyle) { onSetSelectedStrokeWidth(5f) }
                            ToolChoiceButton(stringResource(R.string.pdf_thick), (selectedStroke?.widthPt ?: 0f) > 7f, toolStyle) { onSetSelectedStrokeWidth(10f) }
                            PdfToolChip(stringResource(R.string.pdf_duplicate), Icons.Default.ContentCopy, false, toolStyle, onDuplicateSelected)
                            PdfToolChip(stringResource(R.string.pdf_delete), Icons.Default.Delete, false, toolStyle, onDeleteSelected)
                        }
                    }
                    "image" -> {
                        PdfToolRow(6.dp) {
                            ToolChoiceButton("−", false, toolStyle) { onScaleImage(0.88f) }
                            ToolChoiceButton("+", false, toolStyle) { onScaleImage(1.14f) }
                            PdfToolChip(stringResource(R.string.pdf_rotate), Icons.Default.RotateRight, false, toolStyle, onRotateImage)
                            PdfToolChip(stringResource(R.string.pdf_flip), Icons.Default.Flip, false, toolStyle, onFlipImage)
                            PdfToolChip(stringResource(R.string.pdf_crop), Icons.Default.Crop, false, toolStyle, onCropImage)
                            PdfToolChip(stringResource(R.string.pdf_ai_crop), Icons.Default.AutoFixHigh, false, toolStyle, onAiCrop)
                            PdfToolChip(stringResource(R.string.pdf_duplicate), Icons.Default.ContentCopy, false, toolStyle, onDuplicateSelected)
                            PdfToolChip(stringResource(R.string.pdf_delete), Icons.Default.Delete, false, toolStyle, onDeleteSelected)
                        }
                    }
                    "selected_text" -> {
                        PdfToolRow(6.dp) {
                            ToolChoiceButton("A−", false, toolStyle) { onResizeSelectedText(0.90f) }
                            ToolChoiceButton("A+", false, toolStyle) { onResizeSelectedText(1.10f) }
                            PdfToolChip(stringResource(R.string.pdf_apply_color), Icons.Default.Highlight, false, toolStyle, onApplyColorToSelectedText)
                            PdfToolChip(stringResource(R.string.pdf_duplicate), Icons.Default.ContentCopy, false, toolStyle, onDuplicateSelected)
                            PdfToolChip(stringResource(R.string.pdf_delete_text), Icons.Default.Delete, false, toolStyle, onDeleteSelected)
                        }
                    }
                    "page" -> {
                        PdfToolRow(6.dp) {
                            PdfToolChip(stringResource(R.string.pdf_add_page), Icons.Default.Add, false, toolStyle, onAddPage)
                            PdfToolChip(stringResource(R.string.pdf_duplicate_page), Icons.Default.ContentCopy, false, toolStyle, onDuplicatePage)
                            if (pageHasEdits) PdfToolChip(stringResource(R.string.pdf_clear_edits), Icons.Default.DeleteSweep, false, toolStyle, onClearPageEdits)
                            if (pageCount > 1 || pageHasEdits) PdfToolChip(stringResource(R.string.pdf_delete_page), Icons.Default.Delete, false, toolStyle, onDeletePage)
                        }
                    }
                    else -> Spacer(Modifier.height(0.dp))
                }
            }
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    WorkspaceToolIconButton(Icons.Default.Undo, stringResource(R.string.pdf_undo), enabled = canUndo, onClick = onUndo)
                    WorkspaceToolIconButton(Icons.Default.Redo, stringResource(R.string.pdf_redo), enabled = canRedo, onClick = onRedo)
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    PageArrowButton("‹", pageIndex > 0, onPreviousPage)
                    WorkspaceToolActionButton(onClick = { UiSoundPlayer.runWithFallbackClick(actionBlock = onGoToPage) }) {
                        Text(stringResource(R.string.pdf_page_counter, pageIndex + 1, pageCount), fontSize = pageSize)
                    }
                    PageArrowButton("›", pageIndex < pageCount - 1, onNextPage)
                }
            }
        }
    }
}

@Composable
private fun PdfToolRow(spacing: Dp, content: @Composable RowScope.() -> Unit) {
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(spacing),
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )
}

@Composable
private fun PdfPaletteChoices(selectedColor: Int?, onColor: (Int) -> Unit) {
    val ring = LocalWorkspaceDrawerColors.current?.sectionMutedContent ?: MaterialTheme.colorScheme.outline
    PdfEditorPalette.forEach { color ->
        val selected = selectedColor == color
        Box(Modifier.size(24.dp).background(Color(color), CircleShape)
            .border(if (selected) 3.dp else 1.dp, if (selected) MaterialTheme.colorScheme.primary else ring, CircleShape)
            .clickable { UiSoundPlayer.runWithFallbackClick(UiActionSound.Color) { onColor(color) } })
    }
}

@Composable
private fun PageArrowButton(
    label: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val disabledContent = LocalWorkspaceDrawerColors.current?.disabledButtonContent ?: colors.onSurfaceVariant
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(if (enabled) colors.surfaceContainerHigh else colors.surfaceContainerLow)
            .clickable(enabled = enabled, onClick = { UiSoundPlayer.runWithFallbackClick(actionBlock = onClick) }),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (enabled) colors.onSurface else disabledContent,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun PdfToolChip(label: String, icon: ImageVector, selected: Boolean, style: PdfToolStyle, onClick: () -> Unit) {
    val container = if (selected) style.selectedContainer else style.idleContainer
    val content = if (selected) style.selectedContent else style.idleContent
    Surface(
        modifier = Modifier.clickable(onClick = { UiSoundPlayer.runWithFallbackClick(actionBlock = onClick) }),
        color = container,
        contentColor = content,
        shape = RoundedCornerShape(style.radius),
        border = style.selectedOutline?.takeIf { selected }?.let { BorderStroke(2.dp, it) }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppIconLabel(icon, label, iconModifier = Modifier.size(style.iconSize.coerceIn(15.dp, 20.dp)), gap = 5.dp,
                fontFamily = style.fontFamily, fontSize = style.labelSize, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun ToolChoiceButton(
    label: String,
    selected: Boolean,
    fontFamily: FontFamily,
    textColorMode: String = "auto",
    onClick: () -> Unit
) {
    val background = MaterialTheme.colorScheme.surface
    val normal = rememberAdaptiveUiButtonColors(
        MaterialTheme.colorScheme.surfaceContainerHigh, background, textColorMode, 7.0f, 1.30f
    )
    val active = rememberAdaptiveUiButtonColors(
        MaterialTheme.colorScheme.primary, background, textColorMode, 7.0f, 1.30f
    )
    ToolChoiceButton(
        label, selected,
        PdfToolStyle(fontFamily, normal.container, normal.content, active.container, active.content, 18.dp, 11.sp, 12.dp),
        onClick
    )
}

@Composable
private fun ToolChoiceButton(label: String, selected: Boolean, style: PdfToolStyle, onClick: () -> Unit) {
    Button(
        onClick = { UiSoundPlayer.runWithFallbackClick(actionBlock = onClick) },
        shape = RoundedCornerShape(style.radius),
        border = style.selectedOutline?.takeIf { selected }?.let { BorderStroke(2.dp, it) },
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) style.selectedContainer else style.idleContainer,
            contentColor = if (selected) style.selectedContent else style.idleContent
        ),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 9.dp, vertical = 5.dp)
    ) {
        Text(label, fontFamily = style.fontFamily, fontSize = style.labelSize, fontWeight = FontWeight.SemiBold)
    }
}

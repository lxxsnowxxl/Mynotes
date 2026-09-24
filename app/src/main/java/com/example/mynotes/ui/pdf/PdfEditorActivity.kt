package com.example.mynotes.ui.pdf

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color as AndroidColor
import android.graphics.Paint
import android.graphics.RectF
import android.os.Build
import android.os.Bundle
import android.provider.OpenableColumns
import android.net.Uri
import android.view.View
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.HorizontalRule
import androidx.compose.material.icons.filled.Highlight
import androidx.compose.material.icons.filled.CropSquare
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.mynotes.R
import com.example.mynotes.settings.AppSettings
import com.example.mynotes.settings.SettingsRepository
import com.example.mynotes.ui.motion.AppMotion
import com.example.mynotes.ui.sound.UiActionSound
import com.example.mynotes.ui.sound.UiSound
import com.example.mynotes.ui.sound.UiSoundPlayer
import com.example.mynotes.ui.theme.MyNotesTheme
import com.example.mynotes.ui.theme.appFontFamily
import com.example.mynotes.ui.theme.resolveSecondaryUiTextColor
import com.example.mynotes.ui.theme.resolveAdaptiveUiButtonColors
import com.example.mynotes.ui.theme.resolveUiTextColor
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.roundToInt

class PdfEditorActivity : ComponentActivity() {
    /**
     * Mantiene PDF Studio en modo inmersivo respecto a la barra de navegación.
     * Se ocultan únicamente los botones inferiores de Android; la barra de
     * estado (hora, batería y notificaciones) permanece visible.
     *
     * Se combina WindowInsetsControllerCompat con las flags legacy porque
     * PDF Studio sigue soportando Android 7+ y, en dispositivos Samsung con
     * navegación de tres botones (como API 28), el enfoque de la ventana o el
     * regreso desde un picker puede hacer reaparecer temporalmente la barra.
     */
    private fun hideAndroidNavigationBar() {
        val decorView = window.decorView
        val controller = WindowCompat.getInsetsController(window, decorView)
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        controller.hide(WindowInsetsCompat.Type.navigationBars())

        @Suppress("DEPRECATION")
        decorView.systemUiVisibility =
            decorView.systemUiVisibility or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE

        // En Android 7.x, si el sistema muestra transitoriamente la barra,
        // usar negro evita un destello claro antes de volver a ocultarla.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            @Suppress("DEPRECATION")
            window.navigationBarColor = AndroidColor.BLACK
        }
    }

    companion object {
        const val EXTRA_PROJECT_ID = "pdf_project_id"
        private const val LOCALE_PREFS = "locale_prefs"
        private const val LANGUAGE_KEY = "language"
        private const val DEFAULT_LANGUAGE = "system"
    }

    override fun attachBaseContext(newBase: Context) {
        val preferences = newBase.getSharedPreferences(LOCALE_PREFS, Context.MODE_PRIVATE)
        val language = preferences.getString(LANGUAGE_KEY, DEFAULT_LANGUAGE) ?: DEFAULT_LANGUAGE
        if (language == "system") {
            super.attachBaseContext(newBase)
            return
        }
        val locale = Locale.forLanguageTag(language)
        Locale.setDefault(locale)
        val configuration = Configuration(newBase.resources.configuration)
        configuration.setLocale(locale)
        super.attachBaseContext(newBase.createConfigurationContext(configuration))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_MyNotes)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        hideAndroidNavigationBar()
        // Ejecutarlo otra vez cuando el decor ya está adjunto evita el breve
        // parpadeo de los tres botones al entrar en la Activity.
        window.decorView.post { hideAndroidNavigationBar() }
        val initialProjectId = intent.getStringExtra(EXTRA_PROJECT_ID)
        setContent {
            val repository = remember { SettingsRepository(applicationContext) }
            val settings by repository.settings.collectAsStateWithLifecycle(initialValue = AppSettings())
            val systemDark = androidx.compose.foundation.isSystemInDarkTheme()
            val effectiveDark = if (settings.configurationMode == "advanced") settings.darkMode else systemDark

            LaunchedEffect(
                settings.soundEffectsEnabled,
                settings.soundEffectsVolume,
                settings.soundEffectsTheme,
                settings.hapticEffectsEnabled,
                settings.hapticEffectsIntensity,
                settings.hapticEffectsStyle
            ) {
                UiSoundPlayer.configure(
                    context = this@PdfEditorActivity,
                    enabled = settings.soundEffectsEnabled,
                    volumePercent = settings.soundEffectsVolume,
                    theme = settings.soundEffectsTheme,
                    hapticEnabled = settings.hapticEffectsEnabled,
                    hapticIntensityPercent = settings.hapticEffectsIntensity,
                    hapticStyle = settings.hapticEffectsStyle
                )
            }

            MyNotesTheme(
                darkTheme = effectiveDark,
                backgroundColor = settings.backgroundColor,
                backgroundToneIndex = settings.backgroundToneIndex,
                backgroundIntensity = settings.backgroundIntensity,
                surfacePanelIntensity = settings.surfacePanelIntensity,
                headerIntensity = settings.headerIntensity,
                textColor = settings.textColor,
                textOutlineEnabled = settings.textOutlineEnabled,
                accentColor = settings.accentColor
            ) {
                PdfEditorScreen(
                    settings = settings,
                    initialProjectId = initialProjectId,
                    onClose = { finish() }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Pickers de imágenes/PDF y otros componentes del sistema pueden
        // restaurar la navegación. Al regresar, la ocultamos inmediatamente.
        hideAndroidNavigationBar()
        window.decorView.post { hideAndroidNavigationBar() }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            hideAndroidNavigationBar()
        }
    }
}

@Composable
private fun PdfEditorScreen(
    settings: AppSettings,
    initialProjectId: String?,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val fontFamily = remember(settings.font) { appFontFamily(settings.font) }
    val primaryText = resolveUiTextColor(settings.textColor, MaterialTheme.colorScheme.background)
    val secondaryText = resolveSecondaryUiTextColor(settings.textColor, MaterialTheme.colorScheme.background)

    var pages by remember { mutableStateOf(listOf(PdfPageModel.blank())) }
    var currentPageIndex by remember { mutableIntStateOf(0) }
    var tool by remember { mutableStateOf(PdfEditorTool.SELECT) }
    var penColor by remember { mutableIntStateOf(AndroidColor.BLACK) }
    var penWidth by remember { mutableFloatStateOf(3f) }
    var selectedImageId by remember { mutableStateOf<Long?>(null) }
    var selectedTextId by remember { mutableStateOf<Long?>(null) }
    var selectedStrokeId by remember { mutableStateOf<Long?>(null) }
    var canvasTransforming by remember { mutableStateOf(false) }
    var nextElementId by remember { mutableLongStateOf(1L) }
    var aiBusy by remember { mutableStateOf(false) }
    var pendingTextPosition by remember { mutableStateOf<PdfPoint?>(null) }
    var pendingText by remember { mutableStateOf("") }
    var showTextDialog by remember { mutableStateOf(false) }
    var textSize by remember { mutableFloatStateOf(18f) }
    var history by remember { mutableStateOf(listOf<List<PdfPageModel>>()) }
    var redoHistory by remember { mutableStateOf(listOf<List<PdfPageModel>>()) }
    var showTestModeNotice by remember { mutableStateOf(true) }
    var sourcePdfUri by remember { mutableStateOf<Uri?>(null) }
    var pageLoading by remember { mutableStateOf(false) }
    var showGoToPageDialog by remember { mutableStateOf(false) }
    var goToPageInput by remember { mutableStateOf("") }
    var goToPageError by remember { mutableStateOf(false) }

    val projectId = remember(initialProjectId) { initialProjectId ?: PdfProjectRepository.newProjectId() }
    var projectName by remember(initialProjectId) { mutableStateOf(context.getString(R.string.pdf_library_untitled)) }
    var projectReady by remember(initialProjectId) { mutableStateOf(initialProjectId == null) }
    var dirtyVersion by remember { mutableIntStateOf(0) }
    var autosaveBusy by remember { mutableStateOf(false) }

    LaunchedEffect(initialProjectId) {
        if (initialProjectId == null) return@LaunchedEffect
        runCatching { PdfProjectRepository.loadProject(context, initialProjectId) }
            .onSuccess { project ->
                pages = project.pages
                projectName = project.name
                sourcePdfUri = project.sourcePdfUri
                currentPageIndex = 0
                history = emptyList()
                redoHistory = emptyList()
                selectedImageId = null
                selectedTextId = null
                selectedStrokeId = null
                nextElementId = (
                    project.pages.flatMap { page ->
                        page.images.map { it.id } + page.texts.map { it.id } + page.strokes.map { it.id }
                    }.filter { it > 0L }.maxOrNull() ?: 0L
                ) + 1L
                projectReady = true
            }
            .onFailure {
                projectReady = true
                Toast.makeText(context, context.getString(R.string.pdf_library_error_open_project), Toast.LENGTH_LONG).show()
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

    fun replaceCurrent(page: PdfPageModel, recordHistory: Boolean = true) {
        val updated = pages.toMutableList().also { it[currentPageIndex] = page }
        if (recordHistory) {
            commit(updated)
        } else {
            pages = updated
            markDirty()
        }
    }

    fun undo() {
        val previous = history.lastOrNull() ?: return
        redoHistory = (redoHistory + listOf(pages)).takeLast(30)
        history = history.dropLast(1)
        pages = previous
        currentPageIndex = currentPageIndex.coerceIn(0, pages.lastIndex)
        selectedImageId = null
        selectedTextId = null
        selectedStrokeId = null
        markDirty()
    }

    fun redo() {
        val next = redoHistory.lastOrNull() ?: return
        history = (history + listOf(pages)).takeLast(30)
        redoHistory = redoHistory.dropLast(1)
        pages = next
        currentPageIndex = currentPageIndex.coerceIn(0, pages.lastIndex)
        selectedImageId = null
        selectedTextId = null
        selectedStrokeId = null
        markDirty()
    }


    fun displayNameForUri(uri: Uri): String? {
        return runCatching {
            context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
                ?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
                    } else null
                }
        }.getOrNull()
    }

    fun hasMeaningfulProject(): Boolean {
        return sourcePdfUri != null ||
            pages.size > 1 ||
            pages.any { page ->
                page.strokes.isNotEmpty() || page.texts.isNotEmpty() || page.images.isNotEmpty()
            }
    }

    suspend fun persistProject() {
        if (!projectReady || !hasMeaningfulProject()) return
        autosaveBusy = true
        try {
            val saved = PdfProjectRepository.saveProject(
                context = context,
                id = projectId,
                name = projectName,
                pages = pages,
                sourcePdfUri = sourcePdfUri
            )
            // Tras el primer autoguardado de un PDF externo se usa la copia
            // privada de MyNotes. Así el proyecto puede abrirse aunque el
            // proveedor externo deje de estar disponible.
            if (saved.sourcePdfUri != null && sourcePdfUri != saved.sourcePdfUri) {
                sourcePdfUri = saved.sourcePdfUri
            }
        } finally {
            autosaveBusy = false
        }
    }

    val openPdfLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri ?: return@rememberLauncherForActivityResult
        scope.launch {
            runCatching {
                // Un PDF exportado por MyNotes mantiene un vínculo con su proyecto editable.
                // Si existe, recuperamos las capas originales en lugar de cargar el PDF plano.
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
                    /*
                     * El PDF es la capa base; las ediciones creadas en PDF Studio son
                     * overlays normalizados. Al abrir/cambiar un PDF externo conservamos
                     * los overlays de la sesión por número de página.
                     */
                    val previousPages = pages
                    loadedPages.mapIndexed { index, pdfPage ->
                        val previous = previousPages.getOrNull(index)
                        if (previous == null) {
                            pdfPage
                        } else {
                            pdfPage.copy(
                                strokes = previous.strokes,
                                texts = previous.texts,
                                images = previous.images
                            )
                        }
                    }.toMutableList().also { mergedPages ->
                        if (previousPages.size > loadedPages.size) {
                            previousPages.drop(loadedPages.size)
                                .filter { page ->
                                    page.strokes.isNotEmpty() || page.texts.isNotEmpty() || page.images.isNotEmpty()
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
                displayNameForUri(uri)?.let { displayName ->
                    projectName = displayName.substringBeforeLast('.', displayName)
                }
                currentPageIndex = 0
                selectedImageId = null
                selectedTextId = null
                selectedStrokeId = null
                nextElementId = (
                    pages.flatMap { page ->
                        page.images.map { it.id } + page.texts.map { it.id } + page.strokes.map { it.id }
                    }.filter { it > 0L }.maxOrNull() ?: 0L
                ) + 1L

                runCatching {
                    context.contentResolver.takePersistableUriPermission(
                        uri,
                        android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                }

                markDirty()
                UiSoundPlayer.playAction(context, UiActionSound.Open)
            }.onFailure {
                Toast.makeText(context, context.getString(R.string.pdf_error_open), Toast.LENGTH_LONG).show()
            }
        }
    }

    val imageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri ?: return@rememberLauncherForActivityResult
        scope.launch {
            runCatching { PdfDocumentEngine.loadImage(context, uri) }
                .onSuccess { bitmap ->
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
                    selectedImageId = element.id
                    selectedTextId = null
                    selectedStrokeId = null
                    tool = PdfEditorTool.SELECT
                    UiSoundPlayer.playAction(context, UiActionSound.Add)
                }
                .onFailure { Toast.makeText(context, context.getString(R.string.pdf_error_image), Toast.LENGTH_LONG).show() }
        }
    }

    val exportLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/pdf")) { uri ->
        uri ?: return@rememberLauncherForActivityResult
        scope.launch {
            runCatching {
                PdfDocumentEngine.exportPdf(context, uri, pages, sourcePdfUri)
                PdfProjectRepository.registerExportedPdf(context, uri, projectId)
            }.onSuccess {
                    UiSoundPlayer.playAction(context, UiActionSound.Save)
                    Toast.makeText(context, context.getString(R.string.pdf_saved), Toast.LENGTH_SHORT).show()
                }
                .onFailure { Toast.makeText(context, context.getString(R.string.pdf_error_save), Toast.LENGTH_LONG).show() }
        }
    }

    LaunchedEffect(dirtyVersion, projectReady) {
        if (!projectReady || dirtyVersion <= 0 || !hasMeaningfulProject()) return@LaunchedEffect
        delay(900)
        runCatching { persistProject() }
    }

    BackHandler {
        scope.launch {
            runCatching { persistProject() }
            onClose()
        }
    }

    fun runAiCrop() {
        val id = selectedImageId ?: return
        val image = pages[currentPageIndex].images.firstOrNull { it.id == id } ?: return
        if (aiBusy) return
        aiBusy = true
        scope.launch {
            runCatching { PdfSubjectCropper.isolateAndCrop(image.bitmap) }
                .onSuccess { cropped ->
                    val oldCenterX = image.x + image.width / 2f
                    val oldCenterY = image.y + image.height / 2f
                    val imageRatio = cropped.height.toFloat() / cropped.width.toFloat().coerceAtLeast(1f)
                    val newHeight = (image.width * imageRatio * pages[currentPageIndex].widthPt / pages[currentPageIndex].heightPt)
                        .coerceIn(0.05f, 0.85f)
                    val updatedImage = image.copy(
                        bitmap = cropped,
                        height = newHeight,
                        x = (oldCenterX - image.width / 2f).coerceIn(0f, 1f - image.width),
                        y = (oldCenterY - newHeight / 2f).coerceIn(0f, 1f - newHeight)
                    )
                    replaceCurrent(
                        pages[currentPageIndex].copy(
                            images = pages[currentPageIndex].images.map { if (it.id == id) updatedImage else it }
                        )
                    )
                    UiSoundPlayer.playAction(context, UiActionSound.Confirm)
                    Toast.makeText(context, context.getString(R.string.pdf_ai_crop_done), Toast.LENGTH_SHORT).show()
                }
                .onFailure {
                    Toast.makeText(context, context.getString(R.string.pdf_ai_crop_not_ready), Toast.LENGTH_LONG).show()
                }
            aiBusy = false
        }
    }

    // Renderiza únicamente la página que el usuario está viendo. Para documentos
    // largos, las páginas alejadas se liberan de memoria y se vuelven a crear al volver.
    LaunchedEffect(currentPageIndex, sourcePdfUri, pages.size) {
        val sourceUri = sourcePdfUri
        val targetIndex = currentPageIndex
        val current = pages.getOrNull(targetIndex)
        if (sourceUri != null && current?.sourcePdfPageIndex != null && current.background == null) {
            pageLoading = true
            try {
                val rendered = PdfDocumentEngine.renderPdfPage(context, sourceUri, current.sourcePdfPageIndex)
                // Si el usuario cambió de página mientras se rasterizaba ésta, el
                // LaunchedEffect anterior se cancela y no debe sobrescribir la nueva vista.
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
                Toast.makeText(context, context.getString(R.string.pdf_error_page), Toast.LENGTH_LONG).show()
            } finally {
                pageLoading = false
            }
        } else if (sourceUri != null) {
            // Incluso si la página actual ya está cargada, conservar sólo un pequeño
            // vecindario de bitmaps evita que recorrer un PDF grande llene la memoria.
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
            title = { Text(stringResource(R.string.pdf_go_to_page), fontFamily = fontFamily) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        stringResource(R.string.pdf_go_to_page_range, pages.size),
                        fontFamily = fontFamily
                    )
                    OutlinedTextField(
                        value = goToPageInput,
                        onValueChange = { value ->
                            goToPageInput = value.filter { it.isDigit() }.take(6)
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
                TextButton(onClick = {
                    val requested = goToPageInput.toIntOrNull()
                    if (requested != null && requested in 1..pages.size) {
                        currentPageIndex = requested - 1
                        selectedImageId = null
                        selectedTextId = null
                        selectedStrokeId = null
                        showGoToPageDialog = false
                        goToPageError = false
                        UiSoundPlayer.playAction(context, UiActionSound.Navigation)
                    } else {
                        goToPageError = true
                    }
                }) {
                    Text(stringResource(R.string.pdf_go), fontFamily = fontFamily)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showGoToPageDialog = false
                    goToPageError = false
                }) {
                    Text(stringResource(android.R.string.cancel), fontFamily = fontFamily)
                }
            }
        )
    }

    if (showTextDialog) {
        AlertDialog(
            onDismissRequest = { showTextDialog = false },
            title = { Text(stringResource(R.string.pdf_add_text), fontFamily = fontFamily) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = pendingText,
                        onValueChange = { pendingText = it },
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
                TextButton(onClick = {
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
                        selectedTextId = element.id
                        selectedImageId = null
                        selectedStrokeId = null
                        tool = PdfEditorTool.SELECT
                        UiSoundPlayer.playAction(context, UiActionSound.Add)
                    }
                    pendingText = ""
                    showTextDialog = false
                }) { Text(stringResource(R.string.pdf_insert), fontFamily = fontFamily) }
            },
            dismissButton = {
                TextButton(onClick = { showTextDialog = false }) {
                    Text(stringResource(android.R.string.cancel), fontFamily = fontFamily)
                }
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(top = 26.dp)) {
        PdfTopBar(
            settings = settings,
            fontFamily = fontFamily,
            primaryText = primaryText,
            secondaryText = secondaryText,
            onBack = {
                UiSoundPlayer.playAction(context, UiActionSound.Back)
                scope.launch {
                    runCatching { persistProject() }
                    onClose()
                }
            },
            onSave = {
                UiSoundPlayer.playAction(context, UiActionSound.Save)
                val date = SimpleDateFormat("yyyyMMdd_HHmm", Locale.US).format(Date())
                scope.launch {
                    runCatching { persistProject() }
                    exportLauncher.launch("MyNotes_$date.pdf")
                }
            }
        )

        val page = pages[currentPageIndex]
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 4.dp)
                    .clipToBounds(),
                contentAlignment = Alignment.TopCenter
            ) {
                PdfPageCanvas(
                    page = page,
                    pageKey = currentPageIndex,
                    tool = tool,
                    penColor = penColor,
                    penWidth = penWidth,
                    selectedImageId = selectedImageId,
                    selectedTextId = selectedTextId,
                    selectedStrokeId = selectedStrokeId,
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
                                                    PdfPoint(point.x + safeDx, point.y + safeDy)
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
                                                PdfPoint(
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
                // El acceso para abrir un PDF vive dentro del canvas, no en la barra inferior.
                // Se muestra únicamente mientras todavía no existe un documento PDF cargado.
                // Al cargarlo correctamente, sourcePdfUri deja de ser null y el botón desaparece.
                if (sourcePdfUri == null) {
                    PdfCanvasOpenButton(
                        settings = settings,
                        fontFamily = fontFamily,
                        onClick = { openPdfLauncher.launch(arrayOf("application/pdf")) },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                if (aiBusy || pageLoading) {
                    Surface(shape = CircleShape, color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f)) {
                        CircularProgressIndicator(modifier = Modifier.padding(18.dp).size(34.dp))
                    }
                }
            }

            PdfFloatingToolbars(
                settings = settings,
                fontFamily = fontFamily,
                tool = tool,
                penColor = penColor,
                penWidth = penWidth,
                pageIndex = currentPageIndex,
                pageCount = pages.size,
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
                    selectedImageId = null
                    selectedTextId = null
                    selectedStrokeId = null
                },
                onDuplicatePage = {
                    val copy = pages[currentPageIndex].copy()
                    val updated = pages.toMutableList().apply { add(currentPageIndex + 1, copy) }
                    commit(updated)
                    currentPageIndex = (currentPageIndex + 1).coerceAtMost(updated.lastIndex)
                    selectedImageId = null
                    selectedTextId = null
                    selectedStrokeId = null
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
                    selectedImageId = null
                    selectedTextId = null
                    selectedStrokeId = null
                    UiSoundPlayer.play(context, UiSound.Delete)
                },
                onClearPageEdits = {
                    val current = pages[currentPageIndex]
                    replaceCurrent(current.copy(strokes = emptyList(), texts = emptyList(), images = emptyList()))
                    selectedImageId = null
                    selectedTextId = null
                    selectedStrokeId = null
                    UiSoundPlayer.play(context, UiSound.Delete)
                },
                onPreviousPage = {
                    if (currentPageIndex > 0) {
                        currentPageIndex--
                        selectedImageId = null
                        selectedTextId = null
                        selectedStrokeId = null
                        UiSoundPlayer.playAction(context, UiActionSound.Navigation)
                    }
                },
                onNextPage = {
                    if (currentPageIndex < pages.lastIndex) {
                        currentPageIndex++
                        selectedImageId = null
                        selectedTextId = null
                        selectedStrokeId = null
                        UiSoundPlayer.playAction(context, UiActionSound.Navigation)
                    }
                },
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
                                selectedImageId = duplicate.id
                                selectedTextId = null
                                selectedStrokeId = null
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
                                        PdfPoint(
                                            (point.x + offsetX).coerceIn(0f, 1f),
                                            (point.y + offsetY).coerceIn(0f, 1f)
                                        )
                                    }
                                )
                                replaceCurrent(pages[currentPageIndex].copy(strokes = pages[currentPageIndex].strokes + duplicate))
                                selectedStrokeId = duplicate.id
                                selectedImageId = null
                                selectedTextId = null
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
                                selectedTextId = duplicate.id
                                selectedImageId = null
                                selectedStrokeId = null
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
                modifier = Modifier.fillMaxSize()
            )

            if (showTestModeNotice) {
                PdfTestModeNotice(
                    settings = settings,
                    fontFamily = fontFamily,
                    onDismiss = {
                        showTestModeNotice = false
                        UiSoundPlayer.playAction(context, UiActionSound.Back)
                    },
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 10.dp)
                )
            }
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
    val canvasBackground = MaterialTheme.colorScheme.surface
    val colors = resolveAdaptiveUiButtonColors(
        preferred = MaterialTheme.colorScheme.surfaceContainerHigh,
        background = canvasBackground,
        textColorMode = settings.textColor,
        minimumContentContrast = 7.0f,
        minimumSurfaceContrast = 1.35f
    )
    val fontScale = (settings.fontSize / 16f).coerceIn(0.82f, 1.20f)
    val radius = settings.noteCardCornerRadius.coerceIn(18f, 30f).dp

    Surface(
        modifier = modifier.clickable(onClick = onClick),
        color = colors.container,
        contentColor = colors.content,
        shape = RoundedCornerShape(radius),
        tonalElevation = 5.dp,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 22.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(9.dp)
        ) {
            Icon(
                imageVector = Icons.Default.FolderOpen,
                contentDescription = null,
                modifier = Modifier.size(settings.iconSize.coerceIn(18f, 24f).dp)
            )
            Text(
                text = stringResource(R.string.pdf_open_pdf),
                fontFamily = fontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = (14f * fontScale).coerceIn(12.5f, 17f).sp
            )
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
    val base = MaterialTheme.colorScheme.surfaceContainerHigh
    val alpha = (0.90f + settings.surfacePanelIntensity.coerceIn(0f, 100f) / 100f * 0.08f)
        .coerceIn(0.90f, 0.98f)
    val container = base.copy(alpha = alpha)
    val titleColor = resolveUiTextColor(settings.textColor, base)
    val bodyColor = resolveSecondaryUiTextColor(settings.textColor, base)
    val closeColors = resolveAdaptiveUiButtonColors(
        preferred = MaterialTheme.colorScheme.primary,
        background = base,
        textColorMode = settings.textColor,
        minimumContentContrast = 7.0f,
        minimumSurfaceContrast = 1.35f
    )
    val fontScale = (settings.fontSize / 16f).coerceIn(0.82f, 1.20f)
    val radius = settings.noteCardCornerRadius.coerceIn(18f, 30f).dp

    Surface(
        modifier = modifier
            .fillMaxWidth(0.92f)
            .widthIn(max = 410.dp),
        color = container,
        shape = RoundedCornerShape(radius),
        tonalElevation = 8.dp,
        shadowElevation = 12.dp
    ) {
        Row(
            modifier = Modifier.padding(start = 16.dp, top = 13.dp, bottom = 13.dp, end = 9.dp),
            verticalAlignment = Alignment.Top
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                Text(
                    text = stringResource(R.string.pdf_test_mode_title),
                    color = titleColor,
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = (17f * fontScale).coerceIn(15f, 21f).sp,
                    lineHeight = (21f * fontScale).coerceIn(19f, 25f).sp
                )
                Text(
                    text = stringResource(R.string.pdf_test_mode_description),
                    color = bodyColor,
                    fontFamily = fontFamily,
                    fontSize = (14f * fontScale).coerceIn(12.5f, 17f).sp,
                    lineHeight = (20f * fontScale).coerceIn(18f, 24f).sp
                )
            }
            Surface(
                modifier = Modifier
                    .size(34.dp)
                    .clickable(onClick = onDismiss),
                color = closeColors.container,
                contentColor = closeColors.content,
                shape = CircleShape
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.pdf_test_mode_close),
                        modifier = Modifier.size(settings.iconSize.coerceIn(16f, 21f).dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun PdfTopBar(
    settings: AppSettings,
    fontFamily: FontFamily,
    primaryText: Color,
    secondaryText: Color,
    onBack: () -> Unit,
    onSave: () -> Unit
) {
    val exportColors = resolveAdaptiveUiButtonColors(
        preferred = MaterialTheme.colorScheme.primary,
        background = MaterialTheme.colorScheme.background,
        textColorMode = settings.textColor,
        minimumContentContrast = 7.0f,
        minimumSurfaceContrast = 1.35f
    )
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.pdf_back), tint = primaryText)
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                stringResource(R.string.pdf_studio),
                fontFamily = fontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 25.sp,
                color = primaryText
            )
            Text(
                stringResource(R.string.pdf_studio_subtitle),
                fontFamily = fontFamily,
                fontSize = 13.sp,
                color = secondaryText
            )
        }
        Button(
            onClick = onSave,
            shape = RoundedCornerShape(settings.noteCardCornerRadius.coerceIn(16f, 24f).dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = exportColors.container,
                contentColor = exportColors.content
            )
        ) {
            Icon(
                Icons.Default.Save,
                contentDescription = null,
                modifier = Modifier.size(settings.iconSize.coerceIn(16f, 21f).dp),
                tint = exportColors.content
            )
            Spacer(Modifier.width(6.dp))
            Text(
                stringResource(R.string.pdf_export),
                fontFamily = fontFamily,
                color = exportColors.content
            )
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

@Composable
private fun PdfPageCanvas(
    page: PdfPageModel,
    pageKey: Int,
    tool: PdfEditorTool,
    penColor: Int,
    penWidth: Float,
    selectedImageId: Long?,
    selectedTextId: Long?,
    selectedStrokeId: Long?,
    onStrokeFinished: (PdfStroke) -> Unit,
    onEraseAt: (PdfPoint) -> Unit,
    onTextTap: (PdfPoint) -> Unit,
    onSelect: (Long?, Long?, Long?) -> Unit,
    onMoveImage: (Long, Float, Float) -> Unit,
    onResizeImage: (Long, Float, Float) -> Unit,
    onMoveText: (Long, Float, Float) -> Unit,
    onMoveStroke: (Long, Float, Float) -> Unit,
    onResizeStroke: (Long, Float, Float) -> Unit,
    onTransformingChanged: (Boolean) -> Unit
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
            // V95: zoom de dos dedos. Mientras hay dos punteros, se consume el gesto
            // antes de las herramientas de edición para que Seleccionar no mueva objetos.
            .pointerInput(pageKey) {
                awaitEachGesture {
                    var hadMultiTouch = false
                    try {
                        var event = awaitPointerEvent(PointerEventPass.Initial)
                        while (event.changes.any { it.pressed }) {
                            val pressed = event.changes.filter { it.pressed }
                            if (pressed.size >= 2) {
                                if (!hadMultiTouch) {
                                    hadMultiTouch = true
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
            .background(Color.White, RoundedCornerShape(22.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.7f), RoundedCornerShape(22.dp))
            .pointerInput(tool, pageKey, transformingCanvas) {
                if (tool == PdfEditorTool.SELECT && !transformingCanvas) {
                    detectTapGestures { offset ->
                        val livePage = latestPage
                        val nx = offset.x / size.width
                        val ny = offset.y / size.height
                        val point = PdfPoint(nx, ny)
                        val image = livePage.images.asReversed().firstOrNull {
                            nx >= it.x && nx <= it.x + it.width && ny >= it.y && ny <= it.y + it.height
                        }
                        val text = if (image == null) livePage.texts.asReversed().firstOrNull {
                            nx >= it.x && nx <= it.x + 0.45f && ny >= it.y - 0.06f && ny <= it.y + 0.10f
                        } else null
                        val stroke = if (image == null && text == null) findStrokeAt(livePage, point) else null
                        onSelect(image?.id, text?.id, stroke?.id)
                    }
                }
            }
            .pointerInput(tool, pageKey, penColor, penWidth, transformingCanvas) {
                if (transformingCanvas) return@pointerInput
                when (tool) {
                    PdfEditorTool.TEXT -> detectTapGestures { offset ->
                        onTextTap(PdfPoint(offset.x / size.width, offset.y / size.height))
                    }
                    PdfEditorTool.SELECT -> {
                        var activeImageId: Long? = null
                        var activeTextId: Long? = null
                        var activeStrokeId: Long? = null
                        var resizingImageId: Long? = null
                        var resizingStrokeId: Long? = null
                        detectDragGestures(
                            onDragStart = { offset ->
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
                                        val nx = offset.x / size.width
                                        val ny = offset.y / size.height
                                        val point = PdfPoint(nx, ny)
                                        val image = livePage.images.asReversed().firstOrNull {
                                            nx >= it.x && nx <= it.x + it.width && ny >= it.y && ny <= it.y + it.height
                                        }
                                        val text = if (image == null) livePage.texts.asReversed().firstOrNull {
                                            nx >= it.x && nx <= it.x + 0.45f && ny >= it.y - 0.06f && ny <= it.y + 0.10f
                                        } else null
                                        val stroke = if (image == null && text == null) findStrokeAt(livePage, point) else null
                                        activeImageId = image?.id
                                        activeTextId = text?.id
                                        activeStrokeId = stroke?.id
                                        resizingImageId = null
                                        resizingStrokeId = null
                                        onSelect(image?.id, text?.id, stroke?.id)
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
                            }
                        )
                    }
                    PdfEditorTool.PEN, PdfEditorTool.HIGHLIGHTER -> detectDragGestures(
                        onDragStart = { transientPoints = listOf(it) },
                        onDrag = { change, _ ->
                            change.consume()
                            transientPoints = transientPoints + change.position
                        },
                        onDragEnd = {
                            if (transientPoints.size >= 2) {
                                onStrokeFinished(
                                    PdfStroke(
                                        points = transientPoints.map { PdfPoint(it.x / size.width, it.y / size.height) },
                                        colorArgb = if (tool == PdfEditorTool.HIGHLIGHTER) highlighterColor else penColor,
                                        widthPt = if (tool == PdfEditorTool.HIGHLIGHTER) highlighterWidth else penWidth
                                    )
                                )
                            }
                            transientPoints = emptyList()
                        },
                        onDragCancel = { transientPoints = emptyList() }
                    )
                    PdfEditorTool.LINE, PdfEditorTool.RECTANGLE, PdfEditorTool.ELLIPSE -> {
                        var dragStart: Offset? = null
                        var dragEnd: Offset? = null
                        detectDragGestures(
                            onDragStart = {
                                dragStart = it
                                dragEnd = it
                                transientShapeStart = it
                                transientShapeEnd = it
                            },
                            onDrag = { change, _ ->
                                change.consume()
                                dragEnd = change.position
                                transientShapeEnd = change.position
                            },
                            onDragEnd = {
                                val startPoint = dragStart
                                val endPoint = dragEnd
                                if (startPoint != null && endPoint != null) {
                                    buildShapeStroke(
                                        tool = tool,
                                        start = PdfPoint(startPoint.x / size.width, startPoint.y / size.height),
                                        end = PdfPoint(endPoint.x / size.width, endPoint.y / size.height),
                                        colorArgb = penColor,
                                        widthPt = penWidth
                                    )?.let(onStrokeFinished)
                                }
                                transientShapeStart = null
                                transientShapeEnd = null
                            },
                            onDragCancel = {
                                transientShapeStart = null
                                transientShapeEnd = null
                            }
                        )
                    }
                    PdfEditorTool.ERASER -> detectDragGestures(
                        onDragStart = { onEraseAt(PdfPoint(it.x / size.width, it.y / size.height)) },
                        onDrag = { change, _ ->
                            change.consume()
                            onEraseAt(PdfPoint(change.position.x / size.width, change.position.y / size.height))
                        }
                    )
                    PdfEditorTool.PAGE -> Unit
                }
            }
    ) {
        backgroundImage?.let { image ->
            drawImage(image = image, dstSize = IntSize(size.width.roundToInt(), size.height.roundToInt()))
        }

        page.strokes.forEach { stroke ->
            if (stroke.points.size < 2) return@forEach
            val path = Path().apply {
                val first = stroke.points.first()
                moveTo(first.x * size.width, first.y * size.height)
                stroke.points.drop(1).forEach { lineTo(it.x * size.width, it.y * size.height) }
            }
            drawPath(
                path = path,
                color = Color(stroke.colorArgb),
                style = Stroke(width = stroke.widthPt * size.width / page.widthPt, cap = androidx.compose.ui.graphics.StrokeCap.Round)
            )

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

@Composable
private fun PdfFloatingToolbars(
    settings: AppSettings,
    fontFamily: FontFamily,
    tool: PdfEditorTool,
    penColor: Int,
    penWidth: Float,
    pageIndex: Int,
    pageCount: Int,
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
    onDuplicateSelected: () -> Unit,
    onResizeSelectedText: (Float) -> Unit,
    onApplyColorToSelectedText: () -> Unit,
    onApplyColorToSelectedStroke: (Int) -> Unit,
    onSetSelectedStrokeWidth: (Float) -> Unit,
    onDeleteSelected: () -> Unit,
    modifier: Modifier = Modifier
) {
    val panelBase = MaterialTheme.colorScheme.surfaceContainer
    val panelAlpha = (0.84f + settings.surfacePanelIntensity.coerceIn(0f, 100f) / 100f * 0.14f)
        .coerceIn(0.84f, 0.98f)
    val panelColor = panelBase.copy(alpha = panelAlpha)
    val primaryText = resolveUiTextColor(settings.textColor, panelBase)
    val secondaryText = resolveSecondaryUiTextColor(settings.textColor, panelBase)
    val idleColors = resolveAdaptiveUiButtonColors(
        preferred = MaterialTheme.colorScheme.surfaceContainerHigh,
        background = panelBase,
        textColorMode = settings.textColor,
        minimumContentContrast = 7.0f,
        minimumSurfaceContrast = 1.30f
    )
    val selectedColors = resolveAdaptiveUiButtonColors(
        preferred = MaterialTheme.colorScheme.primary,
        background = panelBase,
        textColorMode = settings.textColor,
        minimumContentContrast = 7.0f,
        minimumSurfaceContrast = 1.40f
    )
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
    val barRadius = settings.noteCardCornerRadius.coerceIn(18f, 30f).dp
    val palette = listOf(
        AndroidColor.BLACK,
        AndroidColor.WHITE,
        0xFFE33D3D.toInt(),
        0xFFFF7A2D.toInt(),
        0xFFF4C438.toInt(),
        0xFF47B071.toInt(),
        0xFF35A8B5.toInt(),
        0xFF3978D4.toInt(),
        0xFF7651CA.toInt(),
        0xFFD44B88.toInt()
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
    val motionDuration = AppMotion.duration(
        AppMotion.NORMAL,
        settings.animationsEnabled,
        settings.animationSpeed * if (settings.performanceMode == "performance") 1.2f else 1f
    )
    val motionEasing = AppMotion.easing(settings.animationEasing)
    val motionIntensity = settings.animationIntensity.coerceIn(0.45f, 1.5f)
    val motionStyle = AppMotion.normalizeStyle(settings.animationStyle)

    Box(modifier = modifier.fillMaxSize()) {
        // Barra superior: historial + navegación.
        FloatingBar(
            widthFraction = 0.72f,
            verticalPadding = 5.dp,
            panelColor = panelColor,
            radius = barRadius,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    SmallIconCircle(
                        icon = Icons.Default.Undo,
                        description = stringResource(R.string.pdf_undo),
                        enabled = canUndo,
                        contentColor = idleColors.content,
                        disabledColor = idleColors.content.copy(alpha = 0.38f),
                        containerColor = idleColors.container,
                        iconSize = iconSize,
                        onClick = onUndo
                    )
                    SmallIconCircle(
                        icon = Icons.Default.Redo,
                        description = stringResource(R.string.pdf_redo),
                        enabled = canRedo,
                        contentColor = idleColors.content,
                        disabledColor = idleColors.content.copy(alpha = 0.38f),
                        containerColor = idleColors.container,
                        iconSize = iconSize,
                        onClick = onRedo
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(1.dp)) {
                    PageArrowButton("‹", pageIndex > 0, primaryText, secondaryText, onPreviousPage)
                    Surface(
                        modifier = Modifier.clickable(onClick = onGoToPage),
                        color = idleColors.container,
                        contentColor = idleColors.content,
                        shape = RoundedCornerShape(chipRadius)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text("#", fontFamily = fontFamily, fontWeight = FontWeight.Bold, fontSize = pageSize)
                            Text(
                                stringResource(R.string.pdf_page_counter, pageIndex + 1, pageCount),
                                fontFamily = fontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = pageSize
                            )
                        }
                    }
                    PageArrowButton("›", pageIndex < pageCount - 1, primaryText, secondaryText, onNextPage)
                }
            }
        }

        // Barra inferior: herramientas principales + contexto avanzado animado.
        FloatingBar(
            widthFraction = 0.96f,
            verticalPadding = 7.dp,
            panelColor = panelColor,
            radius = barRadius,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 4.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PdfToolChip(stringResource(R.string.pdf_select), Icons.Default.SelectAll,
                        tool == PdfEditorTool.SELECT && !selectionTemporarilyDisabled, fontFamily,
                        idleColors.container, idleColors.content, selectedColors.container, selectedColors.content, iconSize, labelSize,
                        chipRadius) { if (!selectionTemporarilyDisabled) onTool(PdfEditorTool.SELECT) }
                    PdfToolChip(stringResource(R.string.pdf_draw), Icons.Default.Brush, tool == PdfEditorTool.PEN, fontFamily,
                        idleColors.container, idleColors.content, selectedColors.container, selectedColors.content, iconSize, labelSize,
                        chipRadius) { onTool(PdfEditorTool.PEN) }
                    PdfToolChip(stringResource(R.string.pdf_highlighter), Icons.Default.Highlight, tool == PdfEditorTool.HIGHLIGHTER, fontFamily,
                        idleColors.container, idleColors.content, selectedColors.container, selectedColors.content, iconSize, labelSize,
                        chipRadius) { onTool(PdfEditorTool.HIGHLIGHTER) }
                    PdfToolChip(stringResource(R.string.pdf_line), Icons.Default.HorizontalRule, tool == PdfEditorTool.LINE, fontFamily,
                        idleColors.container, idleColors.content, selectedColors.container, selectedColors.content, iconSize, labelSize,
                        chipRadius) { onTool(PdfEditorTool.LINE) }
                    PdfToolChip(stringResource(R.string.pdf_rectangle), Icons.Default.CropSquare, tool == PdfEditorTool.RECTANGLE, fontFamily,
                        idleColors.container, idleColors.content, selectedColors.container, selectedColors.content, iconSize, labelSize,
                        chipRadius) { onTool(PdfEditorTool.RECTANGLE) }
                    PdfToolChip(stringResource(R.string.pdf_ellipse), Icons.Default.RadioButtonUnchecked, tool == PdfEditorTool.ELLIPSE, fontFamily,
                        idleColors.container, idleColors.content, selectedColors.container, selectedColors.content, iconSize, labelSize,
                        chipRadius) { onTool(PdfEditorTool.ELLIPSE) }
                    PdfToolChip(stringResource(R.string.pdf_text), Icons.Default.TextFields, tool == PdfEditorTool.TEXT, fontFamily,
                        idleColors.container, idleColors.content, selectedColors.container, selectedColors.content, iconSize, labelSize,
                        chipRadius) { onTool(PdfEditorTool.TEXT) }
                    PdfToolChip(stringResource(R.string.pdf_eraser), Icons.Default.Delete, tool == PdfEditorTool.ERASER, fontFamily,
                        idleColors.container, idleColors.content, selectedColors.container, selectedColors.content, iconSize, labelSize,
                        chipRadius) { onTool(PdfEditorTool.ERASER) }
                    PdfToolChip(stringResource(R.string.pdf_add_image), Icons.Default.Image, false, fontFamily,
                        idleColors.container, idleColors.content, selectedColors.container, selectedColors.content, iconSize, labelSize,
                        chipRadius, onAddImage)
                    PdfToolChip(stringResource(R.string.pdf_pages), Icons.Default.PictureAsPdf, tool == PdfEditorTool.PAGE, fontFamily,
                        idleColors.container, idleColors.content, selectedColors.container, selectedColors.content, iconSize, labelSize,
                        chipRadius) { onTool(PdfEditorTool.PAGE) }
                }

                AnimatedContent(
                    targetState = contextMode,
                    transitionSpec = {
                        if (!settings.animationsEnabled || motionDuration <= 0) {
                            EnterTransition.None togetherWith ExitTransition.None
                        } else if (motionStyle.startsWith("slide") || motionStyle == "axis_y" || motionStyle == "elastic_slide") {
                            (slideInVertically(
                                initialOffsetY = { (it * 0.20f * motionIntensity).roundToInt() },
                                animationSpec = tween(motionDuration, easing = motionEasing)
                            ) + fadeIn(animationSpec = tween(motionDuration, easing = motionEasing))) togetherWith
                                (slideOutVertically(
                                    targetOffsetY = { (it * 0.12f * motionIntensity).roundToInt() },
                                    animationSpec = tween((motionDuration * 0.75f).roundToInt().coerceAtLeast(1), easing = motionEasing)
                                ) + fadeOut(animationSpec = tween((motionDuration * 0.75f).roundToInt().coerceAtLeast(1), easing = motionEasing)))
                        } else if (motionStyle == "fade" || motionStyle == "soft_reveal" || motionStyle == "subtle") {
                            fadeIn(animationSpec = tween(motionDuration, easing = motionEasing)) togetherWith
                                fadeOut(animationSpec = tween((motionDuration * 0.75f).roundToInt().coerceAtLeast(1), easing = motionEasing))
                        } else {
                            (fadeIn(animationSpec = tween(motionDuration, easing = motionEasing)) +
                                scaleIn(
                                    initialScale = (1f - 0.045f * motionIntensity).coerceIn(0.90f, 0.98f),
                                    animationSpec = tween(motionDuration, easing = motionEasing)
                                )) togetherWith
                                (fadeOut(animationSpec = tween((motionDuration * 0.75f).roundToInt().coerceAtLeast(1), easing = motionEasing)) +
                                    scaleOut(
                                        targetScale = 0.98f,
                                        animationSpec = tween((motionDuration * 0.75f).roundToInt().coerceAtLeast(1), easing = motionEasing)
                                    ))
                        }
                    },
                    label = "pdfToolContext"
                ) { mode ->
                    when (mode) {
                        "stroke", "text" -> {
                            Row(
                                modifier = Modifier.horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(7.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                palette.forEach { color ->
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .background(Color(color), CircleShape)
                                            .border(
                                                if (penColor == color) 3.dp else 1.dp,
                                                if (penColor == color) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                                CircleShape
                                            )
                                            .clickable { onColor(color) }
                                    )
                                }
                                if (mode == "stroke") {
                                    ToolChoiceButton(stringResource(R.string.pdf_thin), penWidth == 2f, fontFamily,
                                        idleColors.container, idleColors.content, selectedColors.container, selectedColors.content, labelSize,
                                        chipRadius) { onPenWidth(2f) }
                                    ToolChoiceButton(stringResource(R.string.pdf_medium), penWidth == 5f, fontFamily,
                                        idleColors.container, idleColors.content, selectedColors.container, selectedColors.content, labelSize,
                                        chipRadius) { onPenWidth(5f) }
                                    ToolChoiceButton(stringResource(R.string.pdf_thick), penWidth == 10f, fontFamily,
                                        idleColors.container, idleColors.content, selectedColors.container, selectedColors.content, labelSize,
                                        chipRadius) { onPenWidth(10f) }
                                }
                            }
                        }
                        "selected_stroke" -> {
                            Row(
                                modifier = Modifier.horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(7.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                palette.forEach { color ->
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .background(Color(color), CircleShape)
                                            .border(
                                                if (selectedStroke?.colorArgb == color) 3.dp else 1.dp,
                                                if (selectedStroke?.colorArgb == color) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                                CircleShape
                                            )
                                            .clickable { onApplyColorToSelectedStroke(color) }
                                    )
                                }
                                ToolChoiceButton(stringResource(R.string.pdf_thin), (selectedStroke?.widthPt ?: 0f) <= 2.5f, fontFamily,
                                    idleColors.container, idleColors.content, selectedColors.container, selectedColors.content, labelSize,
                                    chipRadius) { onSetSelectedStrokeWidth(2f) }
                                ToolChoiceButton(stringResource(R.string.pdf_medium), (selectedStroke?.widthPt ?: 0f) in 2.5f..7f, fontFamily,
                                    idleColors.container, idleColors.content, selectedColors.container, selectedColors.content, labelSize,
                                    chipRadius) { onSetSelectedStrokeWidth(5f) }
                                ToolChoiceButton(stringResource(R.string.pdf_thick), (selectedStroke?.widthPt ?: 0f) > 7f, fontFamily,
                                    idleColors.container, idleColors.content, selectedColors.container, selectedColors.content, labelSize,
                                    chipRadius) { onSetSelectedStrokeWidth(10f) }
                                PdfToolChip(stringResource(R.string.pdf_duplicate), Icons.Default.ContentCopy, false, fontFamily,
                                    idleColors.container, idleColors.content, selectedColors.container, selectedColors.content, iconSize, labelSize,
                                    chipRadius, onDuplicateSelected)
                                PdfToolChip(stringResource(R.string.pdf_delete), Icons.Default.Delete, false, fontFamily,
                                    idleColors.container, idleColors.content, selectedColors.container, selectedColors.content, iconSize, labelSize,
                                    chipRadius, onDeleteSelected)
                            }
                        }
                        "image" -> {
                            Row(
                                modifier = Modifier.horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                ToolChoiceButton("−", false, fontFamily, idleColors.container, idleColors.content,
                                    selectedColors.container, selectedColors.content, labelSize, chipRadius) { onScaleImage(0.88f) }
                                ToolChoiceButton("+", false, fontFamily, idleColors.container, idleColors.content,
                                    selectedColors.container, selectedColors.content, labelSize, chipRadius) { onScaleImage(1.14f) }
                                PdfToolChip(stringResource(R.string.pdf_rotate), Icons.Default.RotateRight, false, fontFamily,
                                    idleColors.container, idleColors.content, selectedColors.container, selectedColors.content, iconSize, labelSize,
                                    chipRadius, onRotateImage)
                                PdfToolChip(stringResource(R.string.pdf_ai_crop), Icons.Default.AutoFixHigh, false, fontFamily,
                                    idleColors.container, idleColors.content, selectedColors.container, selectedColors.content, iconSize, labelSize,
                                    chipRadius, onAiCrop)
                                PdfToolChip(stringResource(R.string.pdf_duplicate), Icons.Default.ContentCopy, false, fontFamily,
                                    idleColors.container, idleColors.content, selectedColors.container, selectedColors.content, iconSize, labelSize,
                                    chipRadius, onDuplicateSelected)
                                PdfToolChip(stringResource(R.string.pdf_delete), Icons.Default.Delete, false, fontFamily,
                                    idleColors.container, idleColors.content, selectedColors.container, selectedColors.content, iconSize, labelSize,
                                    chipRadius, onDeleteSelected)
                            }
                        }
                        "selected_text" -> {
                            Row(
                                modifier = Modifier.horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                ToolChoiceButton("A−", false, fontFamily, idleColors.container, idleColors.content,
                                    selectedColors.container, selectedColors.content, labelSize, chipRadius) { onResizeSelectedText(0.90f) }
                                ToolChoiceButton("A+", false, fontFamily, idleColors.container, idleColors.content,
                                    selectedColors.container, selectedColors.content, labelSize, chipRadius) { onResizeSelectedText(1.10f) }
                                PdfToolChip(stringResource(R.string.pdf_apply_color), Icons.Default.Highlight, false, fontFamily,
                                    idleColors.container, idleColors.content, selectedColors.container, selectedColors.content, iconSize, labelSize,
                                    chipRadius, onApplyColorToSelectedText)
                                PdfToolChip(stringResource(R.string.pdf_duplicate), Icons.Default.ContentCopy, false, fontFamily,
                                    idleColors.container, idleColors.content, selectedColors.container, selectedColors.content, iconSize, labelSize,
                                    chipRadius, onDuplicateSelected)
                                PdfToolChip(stringResource(R.string.pdf_delete_text), Icons.Default.Delete, false, fontFamily,
                                    idleColors.container, idleColors.content, selectedColors.container, selectedColors.content, iconSize, labelSize,
                                    chipRadius, onDeleteSelected)
                            }
                        }
                        "page" -> {
                            Row(
                                modifier = Modifier.horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                PdfToolChip(stringResource(R.string.pdf_add_page), Icons.Default.Add, false, fontFamily,
                                    idleColors.container, idleColors.content, selectedColors.container, selectedColors.content, iconSize, labelSize,
                                    chipRadius, onAddPage)
                                PdfToolChip(stringResource(R.string.pdf_duplicate_page), Icons.Default.ContentCopy, false, fontFamily,
                                    idleColors.container, idleColors.content, selectedColors.container, selectedColors.content, iconSize, labelSize,
                                    chipRadius, onDuplicatePage)
                                PdfToolChip(stringResource(R.string.pdf_clear_edits), Icons.Default.DeleteSweep, false, fontFamily,
                                    idleColors.container, idleColors.content, selectedColors.container, selectedColors.content, iconSize, labelSize,
                                    chipRadius, onClearPageEdits)
                                PdfToolChip(stringResource(R.string.pdf_delete_page), Icons.Default.Delete, false, fontFamily,
                                    idleColors.container, idleColors.content, selectedColors.container, selectedColors.content, iconSize, labelSize,
                                    chipRadius, onDeletePage)
                            }
                        }
                        else -> Spacer(Modifier.height(0.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun FloatingBar(
    widthFraction: Float,
    verticalPadding: androidx.compose.ui.unit.Dp,
    panelColor: Color,
    radius: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxWidth(widthFraction.coerceIn(0.55f, 0.98f))
            .widthIn(max = 410.dp),
        color = panelColor,
        shape = RoundedCornerShape(radius),
        tonalElevation = 6.dp,
        shadowElevation = 9.dp
    ) {
        Box(modifier = Modifier.padding(horizontal = 9.dp, vertical = verticalPadding)) {
            content()
        }
    }
}

@Composable
private fun PageArrowButton(
    label: String,
    enabled: Boolean,
    contentColor: Color,
    disabledColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (enabled) contentColor else disabledColor.copy(alpha = 0.38f),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun SmallIconCircle(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    description: String,
    enabled: Boolean,
    contentColor: Color,
    disabledColor: Color,
    containerColor: Color,
    iconSize: androidx.compose.ui.unit.Dp,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .size(32.dp)
            .clickable(enabled = enabled, onClick = onClick),
        color = if (enabled) containerColor else containerColor.copy(alpha = 0.45f),
        shape = CircleShape
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                icon,
                contentDescription = description,
                tint = if (enabled) contentColor else disabledColor,
                modifier = Modifier.size(iconSize.coerceIn(15.dp, 20.dp))
            )
        }
    }
}

@Composable
private fun PdfToolChip(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    fontFamily: FontFamily,
    idleContainer: Color,
    idleContent: Color,
    selectedContainer: Color,
    selectedContent: Color,
    iconSize: androidx.compose.ui.unit.Dp,
    labelSize: androidx.compose.ui.unit.TextUnit,
    radius: androidx.compose.ui.unit.Dp,
    onClick: () -> Unit
) {
    val container = if (selected) selectedContainer else idleContainer
    val content = if (selected) selectedContent else idleContent
    Surface(
        modifier = Modifier.clickable(onClick = onClick),
        color = container,
        contentColor = content,
        shape = RoundedCornerShape(radius)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(iconSize.coerceIn(15.dp, 20.dp)))
            Spacer(Modifier.width(5.dp))
            Text(label, fontFamily = fontFamily, fontSize = labelSize, fontWeight = FontWeight.SemiBold)
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
    val normal = resolveAdaptiveUiButtonColors(
        preferred = MaterialTheme.colorScheme.surfaceContainerHigh,
        background = background,
        textColorMode = textColorMode,
        minimumContentContrast = 7.0f,
        minimumSurfaceContrast = 1.30f
    )
    val active = resolveAdaptiveUiButtonColors(
        preferred = MaterialTheme.colorScheme.primary,
        background = background,
        textColorMode = textColorMode,
        minimumContentContrast = 7.0f,
        minimumSurfaceContrast = 1.30f
    )
    ToolChoiceButton(
        label = label,
        selected = selected,
        fontFamily = fontFamily,
        idleContainer = normal.container,
        idleContent = normal.content,
        selectedContainer = active.container,
        selectedContent = active.content,
        labelSize = 11.sp,
        radius = 12.dp,
        onClick = onClick
    )
}

@Composable
private fun ToolChoiceButton(
    label: String,
    selected: Boolean,
    fontFamily: FontFamily,
    idleContainer: Color,
    idleContent: Color,
    selectedContainer: Color,
    selectedContent: Color,
    labelSize: androidx.compose.ui.unit.TextUnit,
    radius: androidx.compose.ui.unit.Dp,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(radius),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) selectedContainer else idleContainer,
            contentColor = if (selected) selectedContent else idleContent
        ),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 9.dp, vertical = 5.dp)
    ) {
        Text(label, fontFamily = fontFamily, fontSize = labelSize, fontWeight = FontWeight.SemiBold)
    }
}

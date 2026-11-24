package com.example.mynotes.ui

import com.example.mynotes.ui.sound.UiActionSound
import com.example.mynotes.ui.sound.UiSoundPlayer

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Paint
import android.net.Uri
import android.view.MotionEvent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.example.mynotes.R
import com.example.mynotes.data.workspace.*
import com.example.mynotes.settings.AppSettings
import com.example.mynotes.ui.workspace.PaletteWorkspaceTheme
import com.example.mynotes.ui.components.AppAlertDialog
import com.example.mynotes.ui.components.StyledSettingsSlider
import com.example.mynotes.ui.components.appScreenBackground
import com.example.mynotes.ui.theme.compositeUiColor
import com.example.mynotes.ui.theme.automaticUiTextColor
import com.example.mynotes.ui.theme.ensureUiContrast
import com.example.mynotes.ui.theme.uiContrastRatio
import com.example.mynotes.ui.preview.LocalLayoutPreview
import com.example.mynotes.ui.workspace.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.math.*

@OptIn(ExperimentalComposeUiApi::class, ExperimentalLayoutApi::class)
@Composable
fun DrawingScreen(settings: AppSettings, onCancel: () -> Unit, onSave: (Uri) -> Unit,studioTool:Int=0) {
    val composeResources = androidx.compose.ui.platform.LocalResources.current

    val context = LocalContext.current
    val preview = LocalLayoutPreview.current != null
    val scope = rememberCoroutineScope()
    var projectId by rememberSaveable { mutableStateOf(java.util.UUID.randomUUID().toString()) }
    var name by rememberSaveable { mutableStateOf("") }
    var logicalSize by remember { mutableStateOf(IntSize.Zero) }
    var viewport by remember { mutableStateOf(IntSize.Zero) }
    // A subdued tint derived from the active theme makes the starting canvas feel
    // part of the chosen palette instead of looking like a disconnected dark box.
    // Once a user chooses a paper color (or loads a project), it is never overridden.
    val defaultPaper = if (MaterialTheme.colorScheme.background.luminance() < 0.45f) {
        // A real page should remain legible even when the surrounding palette is dark.
        androidx.compose.ui.graphics.lerp(MaterialTheme.colorScheme.surfaceContainerHigh, Color.White, 0.75f)
    } else {
        androidx.compose.ui.graphics.lerp(MaterialTheme.colorScheme.surfaceContainerLow, MaterialTheme.colorScheme.outlineVariant, 0.14f)
    }
    var paper by remember { mutableIntStateOf(defaultPaper.toArgb()) }
    var pattern by rememberSaveable { mutableStateOf("plain") }
    var layers by remember { mutableStateOf(listOf(DrawingLayer())) }
    var activeLayer by remember { mutableStateOf("base") }
    var rulers by rememberSaveable { mutableStateOf(false) }
    var lasso by remember { mutableStateOf<List<DrawingPoint>>(emptyList()) }
    var measures by remember { mutableStateOf<List<DrawingPoint>>(emptyList()) }
    var strokes by remember { mutableStateOf<List<DrawingStroke>>(emptyList()) }
    var undo by remember { mutableStateOf<List<com.example.mynotes.studio.DrawingSnapshot>>(emptyList()) }
    var redo by remember { mutableStateOf<List<com.example.mynotes.studio.DrawingSnapshot>>(emptyList()) }
    var mode by rememberSaveable { mutableStateOf("draw") }
    var ink by rememberSaveable { mutableIntStateOf(if (defaultPaper.luminance() < .45f) Color.White.toArgb() else Color(0xFF242424).toArgb()) }
    var width by rememberSaveable { mutableFloatStateOf(4.2f) }
    var alpha by rememberSaveable { mutableFloatStateOf(1f) }
    var stylusOnly by rememberSaveable { mutableStateOf(false) }
    var pressureEnabled by rememberSaveable { mutableStateOf(true) }
    var stamp by remember{mutableStateOf("star")};var saveStamp by remember{mutableStateOf(false)}
    val studioStore = remember { com.example.mynotes.studio.StudioStore.get(context) }
    val studioStamps = com.example.mynotes.studio.rememberStudioTable(studioStore, "stamps")
    val stampEntries = remember(studioStamps) {
        studioStamps.mapNotNull { (id, encoded) ->
            runCatching {
                val entry = org.json.JSONObject(encoded)
                val drawing = entry.optJSONObject("document") ?: return@runCatching null
                val stroke = DrawingProjects.decode(drawing).strokes.firstOrNull { it.points.isNotEmpty() }
                    ?: return@runCatching null
                id to (entry.optString("name").ifBlank { "Stamp" } to stroke)
            }.getOrNull()
        }.toMap()
    }
    val stampOptions = remember(stampEntries) { stampEntries.map { (id, value) -> id to value.first } }
    var pendingSvg by remember{mutableStateOf<DrawingDocument?>(null)};var tapFingers by remember{mutableIntStateOf(0)};var tapAt by remember{mutableLongStateOf(0)};var tapCenter by remember{mutableStateOf(Offset.Zero)};var tapDistance by remember{mutableFloatStateOf(1f)};var tapMoved by remember{mutableStateOf(false)}
    LaunchedEffect(studioTool){ if(!preview) studioStore.load() }
    val svgLauncher=androidx.activity.compose.rememberLauncherForActivityResult(androidx.activity.result.contract.ActivityResultContracts.CreateDocument("image/svg+xml")){uri->val captured=pendingSvg;pendingSvg=null;if(uri!=null&&captured!=null)com.example.mynotes.studio.StudioJobs.start("Export SVG"){context.contentResolver.openOutputStream(uri,"wt")?.bufferedWriter()?.use{it.write(com.example.mynotes.studio.StudioDrawingGeometry.svg(captured))}?:error("Cannot write SVG")}}
    var zoom by remember { mutableFloatStateOf(1f) }
    var pan by remember { mutableStateOf(Offset.Zero) }
    var tools by rememberSaveable { mutableStateOf(false) }
    // "floating" preserves the original left drawer; "split" docks tools below
    // the same drawing surface, without creating a second drawing or project.
    var workspaceMode by rememberSaveable { mutableStateOf("split") }
    var showMoreDrawingTools by rememberSaveable { mutableStateOf(false) }
    var canvasExpanded by rememberSaveable { mutableStateOf(false) }
    var saving by remember { mutableStateOf(false) }
    var ready by remember { mutableStateOf(preview) }
    var previouslySaved by remember { mutableStateOf(false) }
    var recovery by remember { mutableStateOf<DrawingDocument?>(null) }
    var showProjects by remember { mutableStateOf(false) }
    var deletingProject by remember { mutableStateOf<DrawingProjectSummary?>(null) }
    var projects by remember { mutableStateOf<List<DrawingProjectSummary>>(emptyList()) }
    var selected by remember { mutableStateOf<String?>(null) }
    var selectedIds by remember { mutableStateOf<Set<String>>(emptySet()) }
    var points by remember { mutableStateOf<List<DrawingPoint>>(emptyList()) }
    var gestureBefore by remember { mutableStateOf<List<DrawingStroke>>(emptyList()) }
    var lastPoint by remember { mutableStateOf(Offset.Zero) }
    var lastScreen by remember { mutableStateOf(Offset.Zero) }
    var multiTouch by remember { mutableStateOf(false) }
    var drawing by remember { mutableStateOf(false) }
    var pinchDistance by remember { mutableFloatStateOf(0f) }
    var pinchCenter by remember { mutableStateOf(Offset.Zero) }
    var erasing by remember { mutableStateOf(false) }
    val fit = if (logicalSize.width > 0 && viewport.width > 0) min(viewport.width.toFloat() / logicalSize.width, viewport.height.toFloat() / logicalSize.height) else 1f
    val scale = fit * zoom
    val origin = Offset((viewport.width - logicalSize.width * scale) / 2f, (viewport.height - logicalSize.height * scale) / 2f) + pan
    fun toPaper(x:Float,y:Float):Offset = Offset((x-origin.x)/scale, (y-origin.y)/scale)
    fun bounded(point:Offset)=Offset(point.x.coerceIn(0f,logicalSize.width.toFloat()),point.y.coerceIn(0f,logicalSize.height.toFloat()))
    fun document() = DrawingDocument(projectId, name, logicalSize.width.coerceAtLeast(1), logicalSize.height.coerceAtLeast(1), paper, pattern, strokes,infinite=false,rotation=0f,layers=layers,activeLayer=activeLayer)
    fun open(document: DrawingDocument) {
        projectId = document.id; name = document.name; logicalSize = IntSize(document.width, document.height)
        paper = document.paperArgb; pattern = document.pattern; strokes = document.strokes;layers=document.layers;activeLayer=document.activeLayer;selectedIds=emptySet()
        undo = emptyList(); redo = emptyList(); selected = null; points = emptyList(); zoom = 1f; pan = Offset.Zero
        if (!preview) DrawingProjects.activate(context, projectId)
        ready = true; previouslySaved = true; recovery = null; showProjects = false
    }
    fun change(new: List<DrawingStroke>) {
        if (new != strokes) { undo = (undo + com.example.mynotes.studio.DrawingSnapshot(strokes,layers,activeLayer)).takeLast(30); redo = emptyList(); strokes = new }
    }
    fun undoDrawing() { undo.lastOrNull()?.let { redo = (redo + com.example.mynotes.studio.DrawingSnapshot(strokes,layers,activeLayer)).takeLast(30); strokes = it.strokes; layers=it.layers;activeLayer=it.active;undo = undo.dropLast(1); selected = null;selectedIds=emptySet() } }
    fun redoDrawing() { redo.lastOrNull()?.let { undo = (undo + com.example.mynotes.studio.DrawingSnapshot(strokes,layers,activeLayer)).takeLast(30); strokes = it.strokes;layers=it.layers;activeLayer=it.active;redo = redo.dropLast(1); selected = null;selectedIds=emptySet() } }
    fun reset() {
        projectId = java.util.UUID.randomUUID().toString(); name = ""; strokes = emptyList(); layers=listOf(DrawingLayer());activeLayer="base";selectedIds=emptySet();undo = emptyList(); redo = emptyList()
        logicalSize = viewport; selected = null; points = emptyList(); zoom = 1f; pan = Offset.Zero; ready = true; previouslySaved = false; recovery = null
        if (!preview) DrawingProjects.activate(context, projectId)
    }
    LaunchedEffect(Unit) {
        if (preview) return@LaunchedEffect
        val retained = DrawingProjects.load(context, projectId)
        val active = retained ?: DrawingProjects.active(context)?.let { DrawingProjects.load(context, it) }
        if (retained != null) open(retained)
        else if (active != null && active.strokes.isNotEmpty()) recovery = active
        else { ready = true; DrawingProjects.activate(context, projectId) }
    }
    LaunchedEffect(strokes.map{it.id},layers){selectedIds=selectedIds.filter{id->strokes.any{it.id==id&&com.example.mynotes.studio.PlusDrawingGeometry.editable(it,layers)}}.toSet()}
    val snapshot = document()
    val visibleStrokes=remember(strokes,layers){com.example.mynotes.studio.PlusDrawingGeometry.visible(document()).strokes}
    LaunchedEffect(snapshot.copy(updatedAt = 0), ready, recovery) {
        if (preview || !ready || recovery != null || logicalSize.width <= 0 || (strokes.isEmpty() && name.isBlank() && !previouslySaved)) return@LaunchedEffect
        delay(650)
        try {
            DrawingProjects.save(context, snapshot)
            previouslySaved = true
        } catch (cancelled: kotlinx.coroutines.CancellationException) {
            throw cancelled
        } catch (_: Exception) {
            // Autosave remains silent; the editor surface stays visually clean.
        }
    }
    val latest by rememberUpdatedState(snapshot)
    val canSaveExit by rememberUpdatedState(ready && recovery == null && !preview && logicalSize.width > 0 && (strokes.isNotEmpty() || name.isNotBlank() || previouslySaved))
    WorkspaceOnStop { if (canSaveExit) DrawingProjects.saveOnExit(context.applicationContext, latest) }
    DisposableEffect(Unit) { onDispose { if (canSaveExit) DrawingProjects.saveOnExit(context.applicationContext, latest) } }
    fun export() {
        if (saving || strokes.isEmpty() || !ready || recovery != null) return
        val captured = document(); saving = true
        scope.launch {
            try {
                DrawingProjects.save(context, captured)
                val uri = renderDrawing(context,com.example.mynotes.studio.StudioDrawingGeometry.export(captured))
                DrawingProjects.finish(context, captured.id)
                onSave(uri)
            } catch (_: Exception) {
                // Keep export failure behavior silent as before, without a canvas status label.
            }
            finally { saving = false }
        }
    }
    if(saveStamp)com.example.mynotes.studio.StudioForm("Stamp name",onClose={saveStamp=false}){label,_->strokes.firstOrNull{it.id==selected}?.let{stroke->studioStore.put("stamps",java.util.UUID.randomUUID().toString()){it.put("name",label).put("document",DrawingProjects.encode(document().copy(strokes=listOf(stroke))))}};saveStamp=false}
    if (recovery != null) {
        val recovered = recovery!!
        val recoveryShape = RoundedCornerShape(28.dp)
        val screenBackground = MaterialTheme.colorScheme.background
        val recoveryContainer = compositeUiColor(
            automaticUiTextColor(screenBackground).copy(alpha = 0.20f),
            screenBackground
        )
        val recoveryContent = automaticUiTextColor(recoveryContainer)
        val recoverySecondary = recoveryContent.copy(alpha = 0.74f)
        val recoveryAccent = ensureUiContrast(MaterialTheme.colorScheme.primary, recoveryContainer, 3f)
        val recoveryAccentContent = automaticUiTextColor(recoveryAccent)
        val recoveryPreviewSurface = compositeUiColor(recoveryContent.copy(alpha = 0.075f), recoveryContainer)
        val recoveryBorder = recoveryContent.copy(alpha = 0.24f)
        val savedLabel = remember(recovered.updatedAt) {
            java.text.DateFormat.getDateTimeInstance(
                java.text.DateFormat.MEDIUM,
                java.text.DateFormat.SHORT
            ).format(java.util.Date(recovered.updatedAt))
        }

        AppAlertDialog(
            onDismissRequest = onCancel,
            modifier = Modifier
                .shadow(24.dp, recoveryShape)
                .border(1.dp, recoveryBorder, recoveryShape),
            containerColor = recoveryContainer,
            icon = {
                Surface(
                    shape = CircleShape,
                    color = recoveryAccent,
                    contentColor = recoveryAccentContent,
                    tonalElevation = 0.dp
                ) {
                    Icon(
                        imageVector = Icons.Default.Brush,
                        contentDescription = null,
                        modifier = Modifier.padding(12.dp).size(24.dp)
                    )
                }
            },
            title = {
                Text(
                    text = stringResource(R.string.workspace_draft_found),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                    color = recoveryContent
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text(
                        text = stringResource(R.string.workspace_drawing_recovery_message),
                        style = MaterialTheme.typography.bodyMedium,
                        color = recoverySecondary
                    )
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        color = recoveryPreviewSurface,
                        contentColor = recoveryContent,
                        border = BorderStroke(1.dp, recoveryBorder),
                        tonalElevation = 0.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(Modifier.weight(1f)) {
                                    Text(
                                        text = stringResource(R.string.workspace_drawing_preview),
                                        style = MaterialTheme.typography.labelLarge,
                                        color = recoveryAccent
                                    )
                                    Text(
                                        text = recovered.name.ifBlank { stringResource(R.string.drawing_title) },
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                                        color = recoveryContent,
                                        maxLines = 1,
                                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                    )
                                }
                            }
                            DrawingRecoveryPreview(
                                document = recovered,
                                modifier = Modifier.fillMaxWidth().height(128.dp),
                                outlineColor = recoveryBorder
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                RecoveryInfoChip(
                                    text = stringResource(R.string.workspace_drawing_strokes, recovered.strokes.size),
                                    container = recoveryContainer,
                                    content = recoverySecondary,
                                    border = recoveryBorder
                                )
                                RecoveryInfoChip(
                                    text = stringResource(R.string.workspace_drawing_layers, recovered.layers.size),
                                    container = recoveryContainer,
                                    content = recoverySecondary,
                                    border = recoveryBorder
                                )
                            }
                            Text(
                                text = stringResource(R.string.workspace_drawing_updated, savedLabel),
                                style = MaterialTheme.typography.labelMedium,
                                color = recoverySecondary
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { open(recovered); UiSoundPlayer.playFallbackClick() },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = recoveryAccent,
                        contentColor = recoveryAccentContent
                    ),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Text(
                        stringResource(R.string.workspace_resume),
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { reset(); UiSoundPlayer.playFallbackClick() },
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, recoveryBorder),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = recoveryContent),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Text(
                        stringResource(R.string.workspace_new_project),
                        fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                    )
                }
            }
        )
    }
    LaunchedEffect(showProjects) { if (showProjects && !preview) projects = DrawingProjects.list(context) }
    if (showProjects) AppAlertDialog(onDismissRequest = { showProjects = false }, title = { Text(stringResource(R.string.workspace_projects)) },
        text = { LazyColumn(Modifier.heightIn(max = 440.dp)) {
            if (projects.isEmpty()) item { Text(stringResource(R.string.workspace_no_projects)) }
            items(projects, key = { it.id }) { project -> Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(modifier = Modifier.weight(1f), onClick = {
                    val current = document()
                    scope.launch { if (ready && (current.strokes.isNotEmpty() || previouslySaved)) DrawingProjects.save(context, current); DrawingProjects.load(context, project.id)?.let(::open) }
                ; UiSoundPlayer.playFallbackClick()}) { Column(Modifier.fillMaxWidth()) {
                    Text(project.name.ifBlank { composeResources.getString(R.string.drawing_title) })
                    Text(java.text.DateFormat.getDateTimeInstance(java.text.DateFormat.SHORT, java.text.DateFormat.SHORT).format(java.util.Date(project.updatedAt)), style = MaterialTheme.typography.labelSmall)
                } }
                IconButton(onClick = { deletingProject = project ; UiSoundPlayer.playFallbackClick()}) {
                    Icon(Icons.Default.Delete, stringResource(R.string.pdf_delete))
                }
            } }
        } }, confirmButton = { TextButton(onClick = { showProjects = false ; UiSoundPlayer.playFallbackClick()}) { Text(stringResource(R.string.workspace_done)) } },
        dismissButton = { TextButton(onClick = { val current = document(); scope.launch { if (ready && (current.strokes.isNotEmpty() || previouslySaved)) DrawingProjects.save(context, current); reset(); showProjects = false } ; UiSoundPlayer.playFallbackClick()}) { Text(stringResource(R.string.workspace_new_project)) } })

    if (deletingProject != null) AppAlertDialog(onDismissRequest = { deletingProject = null },
        title = { Text(stringResource(R.string.pdf_delete)) },
        text = { Text(deletingProject!!.name.ifBlank { composeResources.getString(R.string.drawing_title) }) },
        confirmButton = { TextButton(onClick = {
            val candidate = deletingProject ?: return@TextButton
            deletingProject = null
            scope.launch { DrawingProjects.delete(context, candidate.id); if (candidate.id == projectId) reset(); projects = DrawingProjects.list(context) }
        ; UiSoundPlayer.playFallbackClick()}) { Text(stringResource(R.string.pdf_delete)) } },
        dismissButton = { TextButton(onClick = { deletingProject = null ; UiSoundPlayer.playFallbackClick()}) { Text(stringResource(R.string.cancel)) } })

    // Share a single drawing canvas and the existing complete set of advanced
    // tools between the docked reference layout and the legacy floating layout.
    val drawingCanvas: @Composable BoxScope.() -> Unit = {
            // Both layouts display this exact same canvas, with the same strokes.
            // Split mode uses only top rounded corners so the tools panel connects
            // directly below the canvas with a shared boundary.
            val canvasShape = RoundedCornerShape(22.dp)
            Box(Modifier.fillMaxSize()
                .clip(canvasShape)
                .background(Color(paper))
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.65f), canvasShape)) {
                Canvas(Modifier.fillMaxSize().onSizeChanged { viewport = it; if (logicalSize.width <= 0) logicalSize = it }
                    .pointerInteropFilter { event ->
                        if (!ready || recovery != null) return@pointerInteropFilter false
                        if (event.actionMasked == MotionEvent.ACTION_CANCEL || (android.os.Build.VERSION.SDK_INT >= 33 && event.flags and 0x20 != 0 && ((0 until event.pointerCount).none { event.getToolType(it) == MotionEvent.TOOL_TYPE_STYLUS || event.getToolType(it) == MotionEvent.TOOL_TYPE_ERASER } || event.getToolType(event.actionIndex) == MotionEvent.TOOL_TYPE_STYLUS || event.getToolType(event.actionIndex) == MotionEvent.TOOL_TYPE_ERASER))) {
                            if (drawing) strokes = gestureBefore
                            points = emptyList(); lasso=emptyList();drawing = false; multiTouch = false; return@pointerInteropFilter true
                        }
                        val stylusIndex = (0 until event.pointerCount).firstOrNull { event.getToolType(it) == MotionEvent.TOOL_TYPE_STYLUS || event.getToolType(it) == MotionEvent.TOOL_TYPE_ERASER }
                        if (event.pointerCount >= 2 && stylusIndex == null) {
                            if (drawing) strokes = gestureBefore
                            points = emptyList(); drawing = false
                            val center = Offset((event.getX(0) + event.getX(1)) / 2, (event.getY(0) + event.getY(1)) / 2)
                            val distance = hypot(event.getX(0) - event.getX(1), event.getY(0) - event.getY(1)).coerceAtLeast(1f)
                            if(!multiTouch){tapAt=event.eventTime;tapCenter=center;tapDistance=distance;tapFingers=event.pointerCount;tapMoved=false};tapFingers=max(tapFingers,event.pointerCount)
                            if (multiTouch && event.actionMasked == MotionEvent.ACTION_MOVE) {
                                if((center-tapCenter).getDistance()>14||abs(distance-tapDistance)>14)tapMoved=true
                                val newZoom = (zoom * distance / pinchDistance.coerceAtLeast(1f)).coerceIn(.5f, 6f)
                                val ratio = newZoom / zoom
                                val mid = Offset(viewport.width / 2f, viewport.height / 2f)
                                pan = (center - mid) - (pinchCenter - mid - pan) * ratio
                                pan = Offset(pan.x.coerceIn(-viewport.width * 4f, viewport.width * 4f), pan.y.coerceIn(-viewport.height * 4f, viewport.height * 4f))
                                zoom = newZoom
                            }
                            multiTouch = true; pinchCenter = center; pinchDistance = distance
                            return@pointerInteropFilter true
                        }
                        if (multiTouch && stylusIndex == null) { if(event.actionMasked==MotionEvent.ACTION_UP){if(!tapMoved&&event.eventTime-tapAt<280){if(tapFingers==2)undoDrawing()else if(tapFingers==3)redoDrawing()};multiTouch=false}; return@pointerInteropFilter true }
                        val pointerIndex = stylusIndex ?: 0
                        val x = event.getX(pointerIndex); val y = event.getY(pointerIndex)
                        val point = toPaper(x, y)
                        val stylus = stylusIndex != null
                        val pressure = if (stylus && pressureEnabled) event.getPressure(pointerIndex).coerceIn(.15f, 2f) else 1f
                        val action = when (event.actionMasked) {
                            MotionEvent.ACTION_POINTER_DOWN -> if (event.actionIndex == pointerIndex) MotionEvent.ACTION_DOWN else MotionEvent.ACTION_MOVE
                            MotionEvent.ACTION_POINTER_UP -> if (event.actionIndex == pointerIndex) MotionEvent.ACTION_UP else MotionEvent.ACTION_MOVE
                            else -> event.actionMasked
                        }
                        if(mode !in listOf("pan","select","lasso","measure") && layers.firstOrNull{it.id==activeLayer}?.let{it.locked||!it.visible}==true)return@pointerInteropFilter true
                        when (action) {
                            MotionEvent.ACTION_DOWN -> {
                                if (drawing && stylus) strokes = gestureBefore
                                points = emptyList(); multiTouch = false
                                gestureBefore = strokes; lastPoint = point; lastScreen = Offset(x, y)
                                if(mode=="measure"){measures=if(measures.size>=3)listOf(DrawingPoint(point.x,point.y))else measures+DrawingPoint(point.x,point.y);drawing=false}
                                else if (mode == "pan") drawing = true
                                else if ((!stylusOnly || stylus) && (point.x in 0f..logicalSize.width.toFloat()&&point.y in 0f..logicalSize.height.toFloat())) {
                                    drawing = true; erasing = mode == "eraser" || event.getToolType(pointerIndex) == MotionEvent.TOOL_TYPE_ERASER || event.buttonState and MotionEvent.BUTTON_STYLUS_PRIMARY != 0
                                    if(mode=="lasso"){lasso=listOf(DrawingPoint(point.x,point.y));erasing=false}
                                    else if(mode=="fill"){val hit=strokes.lastOrNull{it.layer==activeLayer&&com.example.mynotes.studio.PlusDrawingGeometry.editable(it,layers)&&com.example.mynotes.studio.StudioDrawingGeometry.closed(it.points)&&com.example.mynotes.studio.StudioAlgorithms.inside(com.example.mynotes.studio.StudioPoint(point.x,point.y),it.points.map{p->com.example.mynotes.studio.StudioPoint(p.x,p.y)})};if(hit!=null)strokes=strokes.map{if(it.id==hit.id)it.copy(fillArgb=ink)else it}}
                                    else if(mode=="stamp"){val custom=stampEntries[stamp]?.second;val placed=if(custom==null)com.example.mynotes.studio.StudioDrawingGeometry.stamp(stamp,point.x,point.y,ink,width)else{val minX=custom.points.minOf{it.x};val minY=custom.points.minOf{it.y};val w=custom.points.maxOf{it.x}-minX;val h=custom.points.maxOf{it.y}-minY;val factor=90f/max(w,h).coerceAtLeast(1f);custom.copy(id=java.util.UUID.randomUUID().toString(),points=custom.points.map{it.copy(x=point.x+(it.x-minX-w/2)*factor,y=point.y+(it.y-minY-h/2)*factor)},colorArgb=ink,width=width)};strokes=strokes+placed.copy(layer=activeLayer)}
                                    else if (erasing) strokes = strokes.filterNot { it.layer==activeLayer&&com.example.mynotes.studio.PlusDrawingGeometry.editable(it,layers)&&DrawingGeometry.hit(it, point.x, point.y, width / scale + 6f) }
                                    else if (mode == "select") { val hit=strokes.lastOrNull { com.example.mynotes.studio.PlusDrawingGeometry.editable(it,layers)&&DrawingGeometry.hit(it, point.x, point.y, 16f / scale) }?.id; selected=hit; if(hit !in selectedIds)selectedIds=hit?.let{setOf(it)}?:emptySet() }
                                    else points = listOf(DrawingPoint(point.x, point.y, pressure))
                                }
                            }
                            MotionEvent.ACTION_MOVE -> if (drawing) {
                                when {
                                    mode == "lasso" -> {if(lasso.lastOrNull()?.let{hypot(it.x-point.x,it.y-point.y)>2f/scale}!=false)lasso=lasso+DrawingPoint(point.x,point.y)}
                                    mode == "pan" -> { val screen = Offset(x, y); pan += screen - lastScreen; lastScreen = screen }
                                    erasing -> strokes = strokes.filterNot { it.layer==activeLayer&&com.example.mynotes.studio.PlusDrawingGeometry.editable(it,layers)&&DrawingGeometry.hit(it, point.x, point.y, width / scale + 6f) }
                                    mode == "select" -> { strokes=com.example.mynotes.studio.PlusDrawingGeometry.moved(strokes,selectedIds,point.x-lastPoint.x,point.y-lastPoint.y,logicalSize.width.toFloat(),logicalSize.height.toFloat(),false);lastPoint=point }

                                    mode in listOf("line", "rectangle", "ellipse") -> points = DrawingGeometry.shape(mode, points.first(), DrawingPoint(bounded(point).x,bounded(point).y))
                                    mode=="draw" -> {val next=DrawingPoint(bounded(point).x,bounded(point).y,pressure);points=points+next}
                                }
                            }
                            MotionEvent.ACTION_UP -> {
                                if(drawing&&mode=="lasso"){selectedIds=strokes.filter{com.example.mynotes.studio.PlusDrawingGeometry.editable(it,layers)&&com.example.mynotes.studio.PlusDrawingGeometry.inLasso(it,lasso)}.map{it.id}.toSet();selected=selectedIds.firstOrNull();lasso=emptyList()}
                                if(drawing&&points.isNotEmpty()){strokes=strokes+DrawingStroke(points=points,colorArgb=ink,width=width,layer=activeLayer)}
                                if (drawing && strokes != gestureBefore) { undo = (undo + com.example.mynotes.studio.DrawingSnapshot(gestureBefore,layers,activeLayer)).takeLast(30); redo = emptyList() }
                                points = emptyList(); drawing = false
                            }
                        }
                        true
                    }) {
                    // El color/patrón se pinta en coordenadas de pantalla. Solo los
                    // trazos usan zoom, paneo y rotación; el fondo nunca forma un rombo.
                    drawDrawingPattern(pattern, size.width, size.height, Color(paper))
                    withTransform({translate(origin.x,origin.y);scale(scale,scale,Offset.Zero)}){
                        clipRect(0f,0f,logicalSize.width.toFloat(),logicalSize.height.toFloat()){
                            visibleStrokes.forEach { drawDrawingStroke(it) }
                            if(lasso.isNotEmpty())drawDrawingStroke(DrawingStroke(points=lasso,colorArgb=0xff3366cc.toInt(),width=1.5f/scale))
                            if(measures.isNotEmpty()){measures.forEach{drawCircle(Color(0xff3366cc),4f/scale,Offset(it.x,it.y))};measures.zipWithNext().forEach{(a,b)->drawLine(Color(0xff3366cc),Offset(a.x,a.y),Offset(b.x,b.y),1.5f/scale)}}
                            if (points.isNotEmpty()) drawDrawingStroke(DrawingStroke(points = points, colorArgb = ink, width = width))
                        }
                        strokes.filter { it.id in selectedIds }.forEach { selectedStroke -> selectedStroke.points.takeIf { it.isNotEmpty() }?.let { list ->
                            val x = list.minOf { it.x }; val y = list.minOf { it.y }
                            drawRect(Color(0xFF5373BD), Offset(x - 6f, y - 6f), Size(list.maxOf { it.x } - x + 12f, list.maxOf { it.y } - y + 12f), style = Stroke(1.5f / scale))
                        }}
                    }
                }
                if(rulers)com.example.mynotes.studio.PlusRulers{x,y->val p=toPaper(x,y);com.example.mynotes.studio.StudioPoint(p.x,p.y)}
                // Expand the exact same canvas instead of starting a second drawing.
                Surface(
                    modifier = Modifier.align(Alignment.TopEnd).padding(10.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surface.copy(alpha = .96f),
                    tonalElevation = 1.dp
                ) {
                    IconButton(onClick = {
                        canvasExpanded = !canvasExpanded
                        zoom = 1f
                        pan = Offset.Zero
                        UiSoundPlayer.playFallbackClick(UiActionSound.Layout)
                    }, modifier = Modifier.size(42.dp)) {
                        Icon(
                            if (canvasExpanded) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                            stringResource(if (canvasExpanded) R.string.drawing_restore_canvas else R.string.drawing_expand_canvas),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
    }
    val expandedDrawingTools: @Composable ColumnScope.() -> Unit = {
        val drawer = LocalWorkspaceDrawerColors.current
        // The expanded panel is intentionally task-oriented instead of mirroring every
        // low-level drawing option in one continuous list. Project naming was removed:
        // drawings are identified by their content and saved automatically.
        WorkspaceToolSection(
            modifier = Modifier.fillMaxWidth(),
            fallbackColor = MaterialTheme.colorScheme.surfaceContainer,
            fallbackTonalElevation = 1.dp
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        stringResource(R.string.drawing_tools),
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.weight(1f)
                    )
                    WorkspaceToolIconButton(
                        Icons.Default.Undo,
                        stringResource(R.string.drawing_undo),
                        enabled = undo.isNotEmpty(),
                        onClick = { undoDrawing(); UiSoundPlayer.playFallbackClick(UiActionSound.Undo) }
                    )
                    WorkspaceToolIconButton(
                        Icons.Default.Redo,
                        stringResource(R.string.drawing_redo),
                        enabled = redo.isNotEmpty(),
                        onClick = { redoDrawing(); UiSoundPlayer.playFallbackClick(UiActionSound.Redo) }
                    )
                    if (selected != null) {
                        WorkspaceToolIconButton(
                            Icons.Default.Delete,
                            stringResource(R.string.workspace_delete_selected),
                            onClick = {
                                change(strokes.filterNot { it.id == selected })
                                selected = null
                                selectedIds = emptySet()
                                UiSoundPlayer.playFallbackClick(UiActionSound.Delete)
                            }
                        )
                    } else {
                        WorkspaceToolIconButton(
                            Icons.Default.DeleteSweep,
                            stringResource(R.string.drawing_clear),
                            enabled = strokes.isNotEmpty(),
                            onClick = {
                                change(emptyList())
                                selected = null
                                selectedIds = emptySet()
                                UiSoundPlayer.playFallbackClick(UiActionSound.Delete)
                            }
                        )
                    }
                }

                // Core actions first. These are the tools used most often while drawing.
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        "draw" to R.string.pdf_draw,
                        "select" to R.string.workspace_select,
                        "pan" to R.string.workspace_pan,
                        "eraser" to R.string.drawing_tool_eraser
                    ).forEach { (value, label) ->
                        WorkspaceToolFilterChip(
                            selected = mode == value,
                            onClick = {
                                mode = value
                                selected = null
                                if (value != "select") selectedIds = emptySet()
                                UiSoundPlayer.playFallbackClick(UiActionSound.Select)
                            },
                            label = { Text(stringResource(label), maxLines = 1) }
                        )
                    }
                }

                // Geometry is separated from navigation/selection so the two roles are
                // visually distinct and easier to scan on a phone.
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        "line" to R.string.workspace_line,
                        "rectangle" to R.string.workspace_rectangle,
                        "ellipse" to R.string.workspace_ellipse
                    ).forEach { (value, label) ->
                        WorkspaceToolFilterChip(
                            selected = mode == value,
                            onClick = {
                                mode = value
                                selected = null
                                selectedIds = emptySet()
                                UiSoundPlayer.playFallbackClick(UiActionSound.Select)
                            },
                            label = { Text(stringResource(label), maxLines = 1) }
                        )
                    }
                }
            }
        }

        WorkspaceToolSection(Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                // Brush presets have a real stroke preview instead of another row of text-only chips.
                Row(
                    Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    listOf(
                        Triple(R.string.drawing_tool_fine, 1.5f, 1f),
                        Triple(R.string.drawing_tool_pencil, 2.8f, .7f),
                        Triple(R.string.drawing_tool_pen, 4.2f, 1f),
                        Triple(R.string.drawing_tool_marker, 8.5f, 1f),
                        Triple(R.string.drawing_tool_highlighter, 16f, .32f)
                    ).forEach { (label, toolWidth, toolAlpha) ->
                        val active = mode == "draw" && abs(width - toolWidth) < .15f && abs(alpha - toolAlpha) < .05f
                        Surface(
                            onClick = {
                                width = toolWidth
                                alpha = toolAlpha
                                mode = "draw"
                                ink = (ink and 0x00FFFFFF) or ((toolAlpha * 255).roundToInt() shl 24)
                                UiSoundPlayer.playFallbackClick(UiActionSound.Select)
                            },
                            modifier = Modifier.width(92.dp).height(48.dp),
                            shape = RoundedCornerShape(14.dp),
                            color = if (active) MaterialTheme.colorScheme.primaryContainer
                                else MaterialTheme.colorScheme.surfaceContainer,
                            border = BorderStroke(
                                if (active && drawer != null) 2.dp else 1.dp,
                                if (active) drawer?.controls?.outline ?: MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.outlineVariant.copy(alpha = if (drawer == null) 0.55f else 1f)
                            )
                        ) {
                            Column(
                                Modifier.fillMaxSize().padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Text(stringResource(label), style = MaterialTheme.typography.labelSmall, maxLines = 1)
                                val lineColor = LocalContentColor.current
                                Canvas(Modifier.fillMaxWidth().height(10.dp)) {
                                    drawLine(
                                        lineColor,
                                        Offset(0f, size.height / 2f),
                                        Offset(size.width, size.height / 2f),
                                        toolWidth.dp.toPx().coerceAtMost(size.height),
                                        StrokeCap.Round
                                    )
                                }
                            }
                        }
                    }
                }

                Text(stringResource(R.string.drawing_colors), style = MaterialTheme.typography.labelLarge)
                DrawingPalette(
                    listOf(0xFF242424, 0xFFFFFFFF, 0xFFD83A3A, 0xFFF57C32, 0xFFF3C63D,
                        0xFF49A765, 0xFF24A9A8, 0xFF3977D4, 0xFF7754C7, 0xFFD54E91).map { it.toInt() },
                    ink
                ) { ink = (it and 0x00FFFFFF) or ((alpha * 255).toInt() shl 24) }

                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        stringResource(R.string.drawing_thickness) + " · ${width.roundToInt()}",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.weight(1f)
                    )
                    val sampleInk = Color(ink)
                    val samplePaper = drawer?.section ?: MaterialTheme.colorScheme.surfaceContainerLow
                    val sampleBackground = if (uiContrastRatio(sampleInk.copy(alpha = 1f), samplePaper) < 3f) {
                        automaticUiTextColor(sampleInk.copy(alpha = 1f))
                    } else Color.Transparent
                    Canvas(Modifier.width(92.dp).height(24.dp).background(sampleBackground, RoundedCornerShape(8.dp))) {
                        drawLine(
                            sampleInk,
                            Offset(8f, size.height / 2f),
                            Offset(size.width - 8f, size.height / 2f),
                            width.coerceAtMost(size.height),
                            StrokeCap.Round
                        )
                    }
                }
                val colors = MaterialTheme.colorScheme
                StyledSettingsSlider(
                    value = width,
                    onValueChange = { width = it },
                    valueRange = 1f..32f,
                    activeColor = drawer?.controls?.primary ?: colors.primary,
                    inactiveColor = drawer?.sectionMutedContent ?: colors.outlineVariant,
                    valueLabel = width.roundToInt().toString()
                )
            }
        }

        // Selection, layers, lasso and measurement remain available exactly as before,
        // but are visually separated from brush configuration.
        WorkspaceToolSection(Modifier.fillMaxWidth()) {
            Column(Modifier.fillMaxWidth().padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                com.example.mynotes.studio.PlusDrawingTools(
                    strokes, layers, activeLayer, selectedIds, mode, rulers,
                    onMode = { mode = it; tools = true },
                    onRulers = { rulers = it },
                    onLayers = { nextLayers, nextActive, nextStrokes ->
                        if (nextLayers != layers || nextStrokes != strokes) {
                            undo = (undo + com.example.mynotes.studio.DrawingSnapshot(strokes, layers, activeLayer)).takeLast(30)
                            redo = emptyList()
                        }
                        layers = nextLayers
                        activeLayer = nextActive
                        strokes = nextStrokes
                        selectedIds = selectedIds.filter { id ->
                            strokes.any { it.id == id && com.example.mynotes.studio.PlusDrawingGeometry.editable(it, layers) }
                        }.toSet()
                        selected = selectedIds.firstOrNull()
                    },
                    onSelection = { selectedIds = it; selected = it.firstOrNull() }
                )
                if (measures.size >= 2) {
                    val a = measures[0]
                    val b = measures[1]
                    Text("Length · ${"%.1f".format(hypot(a.x - b.x, a.y - b.y))} canvas px")
                    if (measures.size == 3) {
                        val c = measures[2]
                        Text("Angle · ${"%.1f".format(com.example.mynotes.studio.PlusAlgorithms.angle(
                            com.example.mynotes.studio.StudioPoint(a.x, a.y),
                            com.example.mynotes.studio.StudioPoint(b.x, b.y),
                            com.example.mynotes.studio.StudioPoint(c.x, c.y)))}°")
                    }
                    WorkspaceToolActionButton(onClick = { measures = emptyList(); UiSoundPlayer.playFallbackClick() }) {
                        Text("Clear measurement")
                    }
                }
            }
        }

        val hasFillTarget = strokes.any { stroke ->
            stroke.layer == activeLayer &&
                com.example.mynotes.studio.PlusDrawingGeometry.editable(stroke, layers) &&
                com.example.mynotes.studio.StudioDrawingGeometry.closed(stroke.points)
        }
        val canSaveStamp = strokes.any { stroke ->
            stroke.id == selected && stroke.points.isNotEmpty() &&
                com.example.mynotes.studio.PlusDrawingGeometry.editable(stroke, layers)
        }

        // Canvas behaviour and advanced operations live in their own card. Split mode is
        // deliberately not touched here: this panel is shared by floating and split layouts.
        WorkspaceToolSection(Modifier.fillMaxWidth()) {
            Column(Modifier.fillMaxWidth().padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                com.example.mynotes.studio.StudioDrawingPanel(
                    canFill = hasFillTarget,
                    canSaveStamp = canSaveStamp,
                    canExportSvg = strokes.isNotEmpty(),
                    onFill = { mode = "fill" },
                    onStamp = { stamp = it; mode = "stamp" },
                    onSaveStamp = { if (canSaveStamp) saveStamp = true },
                    stamps = stampOptions,
                    onSvg = { pendingSvg = document(); svgLauncher.launch("Drawing.svg") },
                    selectedStamp = stamp.takeIf { mode == "stamp" }
                )
            }
        }

        WorkspaceToolSection(Modifier.fillMaxWidth()) {
            Column(Modifier.fillMaxWidth().padding(10.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                Text(stringResource(R.string.drawing_canvas_color), style = MaterialTheme.typography.labelLarge)
                DrawingPalette(
                    listOf(0xFFFFFEF9, 0xFFFFFFFF, 0xFFFFF4C7, 0xFFE5F1FB,
                        0xFFDFF7EA, 0xFFF0E7FA, 0xFF242735).map { it.toInt() },
                    paper
                ) { paper = it }
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(
                        "plain" to R.string.workspace_plain,
                        "grid" to R.string.workspace_grid,
                        "ruled" to R.string.workspace_ruled
                    ).forEach { (value, label) ->
                        WorkspaceToolFilterChip(
                            selected = pattern == value,
                            onClick = { pattern = value; UiSoundPlayer.playFallbackClick() },
                            label = { Text(stringResource(label)) }
                        )
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(stringResource(R.string.workspace_stylus_only), Modifier.weight(1f))
                    Switch(stylusOnly, { stylusOnly = it }, colors = workspaceToolSwitchColors())
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(stringResource(R.string.workspace_pressure), Modifier.weight(1f))
                    Switch(pressureEnabled, { pressureEnabled = it }, colors = workspaceToolSwitchColors())
                }
            }
        }
    }
    val toolPresets = listOf(
        Triple(R.string.drawing_tool_fine, 1.5f, 1f),
        Triple(R.string.drawing_tool_pencil, 2.8f, 0.7f),
        Triple(R.string.drawing_tool_pen, 4.2f, 1f),
        Triple(R.string.drawing_tool_marker, 8.5f, 1f),
        Triple(R.string.drawing_tool_highlighter, 16f, .32f)
    )
    val drawingInks = remember {
        listOf(0xFF242424, 0xFFFFFFFF, 0xFFD83A3A, 0xFFF57C32,
            0xFFF3C63D, 0xFF49A765, 0xFF24A9A8, 0xFF3977D4,
            0xFF7754C7, 0xFFD54E91, 0xFF93664B).map { it.toInt() }
    }
    val paperColors = remember(defaultPaper) {
        listOf(defaultPaper.toArgb(), 0xFFFFFFFF, 0xFFFFF4C7, 0xFFFFE3D2,
            0xFFFFE2EB, 0xFFF0E7FA, 0xFFE5F1FB, 0xFFDFF7EA,
            0xFFEAF4E8, 0xFFECEEF0, 0xFF242735).map { it.toInt() }
    }

    PaletteWorkspaceTheme(settings.surfacePanelIntensity) {
        Column(
            Modifier.fillMaxSize().appScreenBackground().statusBarsPadding()
                .navigationBarsPadding().padding(horizontal = 12.dp, vertical = 8.dp)
                .then(workspaceShortcuts(::export, ::undoDrawing, ::redoDrawing)),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(Modifier.fillMaxWidth().heightIn(min = 60.dp), verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = CircleShape, color = MaterialTheme.colorScheme.surfaceContainerHigh) {
                    IconButton(onClick = {
                        onCancel()
                        UiSoundPlayer.playFallbackClick(UiActionSound.Back)
                    }, modifier = Modifier.size(48.dp)) {
                        Icon(Icons.Default.ArrowBack, stringResource(R.string.drawing_back))
                    }
                }
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text(name.ifBlank { stringResource(R.string.drawing_title) },
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                    Text(stringResource(R.string.drawing_subtitle),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 2, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                }
                Surface(shape = CircleShape, color = if (strokes.isNotEmpty() && !saving && ready && recovery == null)
                        MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainerHigh,
                    contentColor = if (strokes.isNotEmpty() && !saving && ready && recovery == null)
                        MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant) {
                    IconButton(onClick = {
                        export()
                        UiSoundPlayer.playFallbackClick(UiActionSound.Save)
                    }, enabled = strokes.isNotEmpty() && !saving && ready && recovery == null,
                        modifier = Modifier.size(48.dp)) {
                        Icon(Icons.Default.Check, stringResource(R.string.drawing_save))
                    }
                }
            }
            BoxWithConstraints(Modifier.fillMaxWidth().weight(1f)) {
                if (workspaceMode == "floating") {
                    AdaptiveWorkspace(
                        modifier = Modifier.fillMaxSize(),
                        expandedTools = tools,
                        toolPanelIntensity = settings.surfacePanelIntensity,
                        compactTools = true,
                        toolsDrawer = true,
                        linkedSplit = false,
                        drawerTitleRes = R.string.drawing_tools_panel_title,
                        drawerMaxHeightFraction = 0.58f,
                        onDrawerOpenChange = { tools = it },
                        content = drawingCanvas,
                        supporting = expandedDrawingTools
                    )
                    // The mode switch is reachable even while the floating drawer is closed.
                    Surface(
                        modifier = Modifier.align(Alignment.BottomEnd).padding(12.dp),
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                        tonalElevation = 2.dp
                    ) {
                        TextButton(onClick = {
                            workspaceMode = "split"
                            tools = false
                            canvasExpanded = false
                            showMoreDrawingTools = false
                            zoom = 1f
                            pan = Offset.Zero
                            UiSoundPlayer.playFallbackClick(UiActionSound.Layout)
                        }) {
                            Icon(Icons.Default.ViewAgenda, null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(5.dp))
                            Text(stringResource(R.string.drawing_layout_split))
                        }
                    }
                } else {
                    // Keep the main actions visible without giving the panel half the screen.
                    val toolPanelHeight = (maxHeight * 0.43f).coerceAtMost(350.dp)
                    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(Modifier.fillMaxWidth().weight(1f), content = drawingCanvas)
                        if (!canvasExpanded) {
                            Surface(
                                modifier = Modifier.fillMaxWidth().height(toolPanelHeight),
                                shape = RoundedCornerShape(22.dp),
                                color = MaterialTheme.colorScheme.surfaceContainerLow,
                                tonalElevation = 2.dp,
                                shadowElevation = 0.dp,
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                            ) {
                                Column(
                                    Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                                        .padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                        Text(stringResource(R.string.drawing_tools),
                                            style = MaterialTheme.typography.titleMedium,
                                            modifier = Modifier.weight(1f))
                                        IconButton(onClick = {
                                            showProjects = true
                                            UiSoundPlayer.playFallbackClick(UiActionSound.Open)
                                        }, modifier = Modifier.size(34.dp)) {
                                            Icon(Icons.Default.FolderOpen, stringResource(R.string.workspace_projects),
                                                modifier = Modifier.size(20.dp))
                                        }
                                        IconButton(onClick = {
                                            workspaceMode = "floating"
                                            tools = true
                                            showMoreDrawingTools = false
                                            zoom = 1f
                                            pan = Offset.Zero
                                            UiSoundPlayer.playFallbackClick(UiActionSound.Layout)
                                        }, modifier = Modifier.size(34.dp)) {
                                            Icon(Icons.Default.Layers, stringResource(R.string.drawing_layout_floating),
                                                modifier = Modifier.size(20.dp))
                                        }
                                        TextButton(onClick = {
                                            showMoreDrawingTools = !showMoreDrawingTools
                                            UiSoundPlayer.playFallbackClick(UiActionSound.Menu)
                                        }, contentPadding = PaddingValues(horizontal = 7.dp)) {
                                            Text(stringResource(if (showMoreDrawingTools)
                                                R.string.drawing_compact_back else R.string.drawing_compact_more))
                                        }
                                    }
                                    if (showMoreDrawingTools) {
                                        expandedDrawingTools()
                                    } else {
                                        // Split mode keeps its own compact tool strip instead of forcing
                                        // users into the expanded panel for basic selection and shapes.
                                        Row(
                                            Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            listOf(
                                                "draw" to R.string.pdf_draw,
                                                "select" to R.string.workspace_select,
                                                "pan" to R.string.workspace_pan,
                                                "eraser" to R.string.drawing_tool_eraser,
                                                "line" to R.string.workspace_line,
                                                "rectangle" to R.string.workspace_rectangle,
                                                "ellipse" to R.string.workspace_ellipse
                                            ).forEach { (value, label) ->
                                                FilterChip(
                                                    selected = mode == value,
                                                    onClick = {
                                                        mode = value
                                                        selected = null
                                                        if (value != "select") selectedIds = emptySet()
                                                        UiSoundPlayer.playFallbackClick(UiActionSound.Select)
                                                    },
                                                    label = { Text(stringResource(label), maxLines = 1) }
                                                )
                                            }
                                        }
                                        // Short brush strip with a real stroke-width preview.
                                        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            toolPresets.forEach { (label, toolWidth, toolAlpha) ->
                                                val active = mode == "draw" && abs(width - toolWidth) < .15f && abs(alpha - toolAlpha) < .05f
                                                Surface(
                                                    onClick = {
                                                        width = toolWidth
                                                        alpha = toolAlpha
                                                        mode = "draw"
                                                        ink = (ink and 0x00FFFFFF) or ((toolAlpha * 255).roundToInt() shl 24)
                                                        UiSoundPlayer.playFallbackClick(UiActionSound.Select)
                                                    },
                                                    modifier = Modifier.width(94.dp).height(44.dp),
                                                    shape = RoundedCornerShape(14.dp),
                                                    color = if (active) MaterialTheme.colorScheme.surfaceContainerHigh
                                                        else MaterialTheme.colorScheme.surfaceContainer,
                                                ) {
                                                    Column(Modifier.fillMaxSize().padding(7.dp),
                                                        verticalArrangement = Arrangement.SpaceEvenly) {
                                                        Text(stringResource(label), style = MaterialTheme.typography.labelSmall,
                                                            maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                                                        val lineColor = MaterialTheme.colorScheme.onSurface
                                                        Canvas(Modifier.fillMaxWidth().height(12.dp)) {
                                                            drawLine(lineColor,
                                                                start = Offset(0f, size.height / 2f),
                                                                end = Offset(size.width, size.height / 2f),
                                                                strokeWidth = toolWidth.dp.toPx().coerceAtMost(size.height),
                                                                cap = StrokeCap.Round)
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        Text(stringResource(R.string.drawing_colors),
                                            style = MaterialTheme.typography.labelMedium)
                                        DrawingPalette(drawingInks, ink, dense = true) {
                                            ink = (it and 0x00FFFFFF) or ((alpha * 255).roundToInt() shl 24)
                                        }
                                        Text(stringResource(R.string.drawing_canvas_color),
                                            style = MaterialTheme.typography.labelMedium)
                                        DrawingPalette(paperColors, paper, dense = true) { paper = it }
                                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                            Text(stringResource(R.string.drawing_thickness),
                                                style = MaterialTheme.typography.labelMedium,
                                                modifier = Modifier.weight(1f))
                                            listOf(Triple(R.string.drawing_thin, 2f, 0),
                                                Triple(R.string.drawing_medium, 5f, 1),
                                                Triple(R.string.drawing_thick, 11f, 2)).forEach { (label, preset, index) ->
                                                val active = when (index) {
                                                    0 -> width < 3.5f
                                                    1 -> width in 3.5f..8f
                                                    else -> width > 8f
                                                }
                                                Surface(
                                                    onClick = {
                                                        width = preset
                                                        UiSoundPlayer.playFallbackClick(UiActionSound.Select)
                                                    },
                                                    modifier = Modifier.weight(1f).padding(start = 4.dp),
                                                    shape = RoundedCornerShape(16.dp),
                                                    color = if (active) MaterialTheme.colorScheme.surfaceContainerHigh
                                                        else MaterialTheme.colorScheme.surfaceContainer
                                                ) {
                                                    Box(Modifier.height(34.dp), contentAlignment = Alignment.Center) {
                                                        Text(stringResource(label), style = MaterialTheme.typography.labelSmall)
                                                    }
                                                }
                                            }
                                        }
                                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            IconButton(
                                                modifier = Modifier.size(42.dp)
                                                    .background(MaterialTheme.colorScheme.surfaceContainer, CircleShape),
                                                enabled = undo.isNotEmpty(),
                                                onClick = { undoDrawing(); UiSoundPlayer.playFallbackClick(UiActionSound.Undo) }
                                            ) { Icon(Icons.Default.Undo, stringResource(R.string.drawing_undo)) }
                                            IconButton(
                                                modifier = Modifier.size(42.dp)
                                                    .background(MaterialTheme.colorScheme.surfaceContainer, CircleShape),
                                                enabled = redo.isNotEmpty(),
                                                onClick = { redoDrawing(); UiSoundPlayer.playFallbackClick(UiActionSound.Redo) }
                                            ) { Icon(Icons.Default.Redo, stringResource(R.string.drawing_redo)) }
                                            Spacer(Modifier.weight(1f))
                                            if (selected != null) {
                                                IconButton(
                                                    modifier = Modifier.size(42.dp)
                                                        .background(MaterialTheme.colorScheme.surfaceContainer, CircleShape),
                                                    onClick = {
                                                        change(strokes.filterNot { it.id == selected })
                                                        selected = null
                                                        selectedIds = emptySet()
                                                        UiSoundPlayer.playFallbackClick(UiActionSound.Delete)
                                                    }
                                                ) { Icon(Icons.Default.Delete, stringResource(R.string.workspace_delete_selected)) }
                                            }
                                            IconButton(
                                                modifier = Modifier.size(42.dp)
                                                    .background(MaterialTheme.colorScheme.surfaceContainer, CircleShape),
                                                enabled = strokes.isNotEmpty(),
                                                onClick = {
                                                    change(emptyList())
                                                    selected = null
                                                    UiSoundPlayer.playFallbackClick(UiActionSound.Delete)
                                                }
                                            ) { Icon(Icons.Default.DeleteSweep, stringResource(R.string.drawing_clear)) }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun DrawingPalette(colors: List<Int>, selected: Int, dense: Boolean = false, onSelect: (Int) -> Unit) {
    val drawer = LocalWorkspaceDrawerColors.current
    val uiColors = MaterialTheme.colorScheme
    Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        colors.forEach { color -> Box(Modifier.size(if (dense) 32.dp else 44.dp)
            .clickable { onSelect(color); UiSoundPlayer.playFallbackClick(UiActionSound.Color) },
            contentAlignment = Alignment.Center) {
            val active = (selected and 0xFFFFFF) == (color and 0xFFFFFF)
            val ring = if (active) {
                uiColors.primary
            } else drawer?.sectionMutedContent ?: uiColors.outline
            Box(Modifier.size(if (dense) 27.dp else 30.dp)
                .background(Color(color), CircleShape)
                .border(if (active) 2.5.dp else 1.dp, ring, CircleShape))
        } }
    }
}
private fun DrawScope.drawDrawingStroke(stroke: DrawingStroke) {
    val color=Color(stroke.colorArgb)
    stroke.fillArgb?.let{fill->val path=Path();stroke.points.firstOrNull()?.let{path.moveTo(it.x,it.y)};stroke.points.drop(1).forEach{path.lineTo(it.x,it.y)};path.close();drawPath(path,Color(fill))}
    if (stroke.points.size == 1) stroke.points.first().let { drawCircle(color, stroke.width * it.pressure / 2, Offset(it.x, it.y)) }
    else if (stroke.points.all { it.pressure == 1f }) {
        val path = Path()
        stroke.points.firstOrNull()?.let { path.moveTo(it.x, it.y) }
        for (index in 1 until stroke.points.size) path.lineTo(stroke.points[index].x, stroke.points[index].y)
        drawPath(path, color, style = Stroke(stroke.width, cap = StrokeCap.Round, join = androidx.compose.ui.graphics.StrokeJoin.Round))
    } else for (index in 1 until stroke.points.size) {
        val a = stroke.points[index - 1]; val b = stroke.points[index]
        drawLine(color, Offset(a.x, a.y), Offset(b.x, b.y), stroke.width * (a.pressure + b.pressure) / 2, StrokeCap.Round)
    }
}
private fun DrawScope.drawDrawingPattern(pattern: String, width: Float, height: Float, paper: Color) {
    if (pattern == "plain") return
    val color = if ((paper.red + paper.green + paper.blue) > 1.5f) Color.Black.copy(alpha = .10f) else Color.White.copy(alpha = .16f)
    var y = 28f; while (y < height) { drawLine(color, Offset(0f, y), Offset(width, y), 1f); y += 28f }
    if (pattern == "grid") { var x = 28f; while (x < width) { drawLine(color, Offset(x, 0f), Offset(x, height), 1f); x += 28f } }
}
internal suspend fun renderDrawing(context: Context, document: DrawingDocument): Uri = withContext(Dispatchers.IO) {com.example.mynotes.studio.StudioFiles.write {
    val rendered=com.example.mynotes.studio.PlusDrawingGeometry.visible(document)
    val ratio = min(1f, 2048f / max(rendered.width, rendered.height).coerceAtLeast(1))
    val bitmap = Bitmap.createBitmap((rendered.width * ratio).roundToInt().coerceAtLeast(1), (rendered.height * ratio).roundToInt().coerceAtLeast(1), Bitmap.Config.ARGB_8888)
    try {
        val canvas = android.graphics.Canvas(bitmap); canvas.scale(ratio, ratio); canvas.drawColor(rendered.paperArgb)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { strokeCap = Paint.Cap.ROUND; style = Paint.Style.STROKE }
        if (rendered.pattern != "plain") {
            paint.color = if (android.graphics.Color.luminance(rendered.paperArgb) > .45f) 0x1A000000 else 0x29FFFFFF; paint.strokeWidth = 1f
            var y = 28f; while (y < rendered.height) { canvas.drawLine(0f, y, rendered.width.toFloat(), y, paint); y += 28f }
            if (rendered.pattern == "grid") { var x = 28f; while (x < rendered.width) { canvas.drawLine(x, 0f, x, rendered.height.toFloat(), paint); x += 28f } }
        }
        rendered.strokes.forEach { stroke ->
            stroke.fillArgb?.let{fill->val path=android.graphics.Path();stroke.points.firstOrNull()?.let{path.moveTo(it.x,it.y)};stroke.points.drop(1).forEach{path.lineTo(it.x,it.y)};path.close();paint.color=fill;paint.style=Paint.Style.FILL;canvas.drawPath(path,paint);paint.style=Paint.Style.STROKE}
            paint.color = stroke.colorArgb
            if (stroke.points.size == 1) stroke.points.first().let { paint.style = Paint.Style.FILL; canvas.drawCircle(it.x, it.y, stroke.width * it.pressure / 2f, paint); paint.style = Paint.Style.STROKE }
            if (stroke.points.size > 1 && stroke.points.all { it.pressure == 1f }) {
                val path = android.graphics.Path(); val first = stroke.points.first(); path.moveTo(first.x, first.y)
                for (index in 1 until stroke.points.size) path.lineTo(stroke.points[index].x, stroke.points[index].y)
                paint.strokeWidth = stroke.width; paint.strokeJoin = Paint.Join.ROUND; canvas.drawPath(path, paint)
            } else for (index in 1 until stroke.points.size) {
                val a = stroke.points[index - 1]; val b = stroke.points[index]
                paint.strokeWidth = stroke.width * (a.pressure + b.pressure) / 2; canvas.drawLine(a.x, a.y, b.x, b.y, paint)
            }
        }
        val dir = File(context.filesDir, "workspaces/drawing_exports").apply { mkdirs() }
        val output = File(dir, "${rendered.id}_${System.currentTimeMillis()}.png")
        output.outputStream().use { check(bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)) }
        Uri.fromFile(output)
    } finally { bitmap.recycle() }
}}

@Composable
private fun DrawingRecoveryPreview(
    document: DrawingDocument,
    modifier: Modifier = Modifier,
    outlineColor: Color
) {
    val shape = RoundedCornerShape(16.dp)
    Canvas(
        modifier = modifier
            .clip(shape)
            .background(Color(document.paperArgb))
            .border(1.dp, outlineColor, shape)
    ) {
        val documentWidth = document.width.coerceAtLeast(1).toFloat()
        val documentHeight = document.height.coerceAtLeast(1).toFloat()
        val scale = minOf(size.width / documentWidth, size.height / documentHeight)
        val offsetX = (size.width - documentWidth * scale) / 2f
        val offsetY = (size.height - documentHeight * scale) / 2f
        document.strokes
            .filter { stroke -> document.layers.firstOrNull { it.id == stroke.layer }?.visible != false }
            .takeLast(1000)
            .forEach { stroke ->
                val points = stroke.points
                if (points.size == 1) {
                    val p = points.first()
                    drawCircle(
                        color = Color(stroke.colorArgb),
                        radius = (stroke.width * scale / 2f).coerceAtLeast(1f),
                        center = Offset(offsetX + p.x * scale, offsetY + p.y * scale)
                    )
                } else {
                    for (index in 1 until points.size) {
                        val start = points[index - 1]
                        val end = points[index]
                        drawLine(
                            color = Color(stroke.colorArgb),
                            start = Offset(offsetX + start.x * scale, offsetY + start.y * scale),
                            end = Offset(offsetX + end.x * scale, offsetY + end.y * scale),
                            strokeWidth = (stroke.width * scale).coerceAtLeast(1f),
                            cap = StrokeCap.Round
                        )
                    }
                }
            }
    }
}

@Composable
private fun RecoveryInfoChip(
    text: String,
    container: Color,
    content: Color,
    border: Color
) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = container,
        contentColor = content,
        border = BorderStroke(1.dp, border),
        tonalElevation = 0.dp
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            style = MaterialTheme.typography.labelMedium,
            maxLines = 1
        )
    }
}


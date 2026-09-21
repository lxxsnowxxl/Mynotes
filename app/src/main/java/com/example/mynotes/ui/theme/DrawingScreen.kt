package com.example.mynotes.ui

import android.content.Context
import android.app.Activity
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.Paint
import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.mynotes.R
import com.example.mynotes.settings.AppSettings
import com.example.mynotes.ui.sound.UiActionSound
import com.example.mynotes.ui.sound.UiSoundPlayer
import com.example.mynotes.ui.theme.appFontFamily
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

private data class DrawingStrokeData(
    val points: List<Offset>,
    val color: Color,
    val widthPx: Float,
    val isEraser: Boolean = false
)

private enum class DrawingTool(
    val labelRes: Int,
    val baseWidthDp: Float,
    val alpha: Float = 1f
) {
    FINE_POINT(R.string.drawing_tool_fine, 1.6f),
    PENCIL(R.string.drawing_tool_pencil, 2.8f, 0.78f),
    PEN(R.string.drawing_tool_pen, 4.2f),
    MARKER(R.string.drawing_tool_marker, 8.5f),
    HIGHLIGHTER(R.string.drawing_tool_highlighter, 16f, 0.32f),
    ERASER(R.string.drawing_tool_eraser, 22f)
}

@Composable
fun DrawingScreen(
    settings: AppSettings,
    onCancel: () -> Unit,
    onSave: (Uri) -> Unit
) {
    val context = LocalContext.current
    val view = LocalView.current
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()

    // Drawing mode keeps the canvas distraction-free by hiding only the
    // status bar (time, battery and notification icons). Restore the exact
    // previous visibility when leaving this screen so the rest of MyNotes
    // keeps its normal system-bar behavior.
    DisposableEffect(view) {
        val activity = context.findActivity()
        val window = activity?.window
        if (window == null) {
            onDispose { }
        } else {
            val controller = WindowCompat.getInsetsController(window, view)
            val statusBars = WindowInsetsCompat.Type.statusBars()
            val wasStatusBarVisible =
                ViewCompat.getRootWindowInsets(view)?.isVisible(statusBars) ?: true
            val previousBehavior = controller.systemBarsBehavior

            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            controller.hide(statusBars)

            onDispose {
                controller.systemBarsBehavior = previousBehavior
                if (wasStatusBarVisible) {
                    controller.show(statusBars)
                } else {
                    controller.hide(statusBars)
                }
            }
        }
    }
    val fontFamily = remember(settings.font) { appFontFamily(settings.font) }

    val defaultPaperColor = MaterialTheme.colorScheme.surface
    val canvasColors = remember(defaultPaperColor) {
        listOf(
            defaultPaperColor,
            Color(0xFFFFFFFF),
            Color(0xFFFFF4C7),
            Color(0xFFFFE7D1),
            Color(0xFFFFE5EC),
            Color(0xFFF0E7FA),
            Color(0xFFE5F1FB),
            Color(0xFFDFF7EA),
            Color(0xFFE4F2E8),
            Color(0xFFE9ECEF),
            Color(0xFF34384A),
            Color(0xFF111111)
        )
    }
    var selectedCanvasIndex by remember { mutableIntStateOf(0) }
    var paperColor by remember { mutableStateOf(defaultPaperColor) }
    val outlineColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.55f)
    val strokes = remember { mutableStateListOf<DrawingStrokeData>() }
    val redoStrokes = remember { mutableStateListOf<DrawingStrokeData>() }
    val currentPoints = remember { mutableStateListOf<Offset>() }
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }
    var isSaving by remember { mutableStateOf(false) }
    var isCanvasExpanded by remember { mutableStateOf(false) }

    val inkColors = remember {
        listOf(
            Color(0xFF111111),
            Color(0xFFFFFFFF),
            Color(0xFFD83A3A),
            Color(0xFFF57C32),
            Color(0xFFF3C63D),
            Color(0xFF49A765),
            Color(0xFF24A9A8),
            Color(0xFF3977D4),
            Color(0xFF7754C7),
            Color(0xFFD54E91),
            Color(0xFF8A5A3B),
            Color(0xFF727272)
        )
    }

    var selectedColorIndex by remember {
        mutableIntStateOf(if (paperColor.luminance() < 0.45f) 1 else 0)
    }
    var selectedTool by remember { mutableStateOf(DrawingTool.PEN) }
    var selectedThicknessIndex by remember { mutableIntStateOf(1) }
    val thicknessMultipliers = remember { listOf(0.72f, 1f, 1.38f) }

    val selectedInk = inkColors[selectedColorIndex]
    val activeColor = when (selectedTool) {
        DrawingTool.ERASER -> paperColor
        else -> selectedInk.copy(alpha = selectedTool.alpha)
    }
    val activeWidthPx = with(density) {
        (selectedTool.baseWidthDp * thicknessMultipliers[selectedThicknessIndex]).dp.toPx()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilledTonalIconButton(
                onClick = {
                    UiSoundPlayer.playAction(context, UiActionSound.Back)
                    onCancel()
                }
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.drawing_back))
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            ) {
                Text(
                    text = stringResource(R.string.drawing_title),
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = stringResource(R.string.drawing_subtitle),
                    fontFamily = fontFamily,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.68f)
                )
            }

            FilledTonalIconButton(
                enabled = strokes.isNotEmpty() && !isSaving,
                onClick = {
                    if (canvasSize.width <= 0 || canvasSize.height <= 0 || strokes.isEmpty()) return@FilledTonalIconButton
                    UiSoundPlayer.playAction(context, UiActionSound.Save)
                    isSaving = true
                    scope.launch {
                        val uri = saveDrawingToCache(
                            context = context.applicationContext,
                            size = canvasSize,
                            strokes = strokes.toList(),
                            backgroundColor = paperColor
                        )
                        isSaving = false
                        if (uri != null) onSave(uri)
                    }
                }
            ) {
                Icon(Icons.Default.Check, contentDescription = stringResource(R.string.drawing_save))
            }
        }

        Spacer(Modifier.height(8.dp))

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            shape = RoundedCornerShape(22.dp),
            color = paperColor,
            tonalElevation = 1.dp
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .border(1.dp, outlineColor, RoundedCornerShape(22.dp))
                        .onSizeChanged { newSize ->
                            if (
                                canvasSize.width > 0 &&
                                canvasSize.height > 0 &&
                                newSize.width > 0 &&
                                newSize.height > 0 &&
                                newSize != canvasSize
                            ) {
                                val scaleX = newSize.width.toFloat() / canvasSize.width.toFloat()
                                val scaleY = newSize.height.toFloat() / canvasSize.height.toFloat()
                                strokes.indices.forEach { index ->
                                    strokes[index] = strokes[index].scaled(scaleX, scaleY)
                                }
                                redoStrokes.indices.forEach { index ->
                                    redoStrokes[index] = redoStrokes[index].scaled(scaleX, scaleY)
                                }
                                if (currentPoints.isNotEmpty()) {
                                    val resizedPoints = currentPoints.map { point ->
                                        Offset(point.x * scaleX, point.y * scaleY)
                                    }
                                    currentPoints.clear()
                                    currentPoints.addAll(resizedPoints)
                                }
                            }
                            canvasSize = newSize
                        }
                        .pointerInput(activeColor, activeWidthPx, selectedTool) {
                            detectDragGestures(
                                onDragStart = { offset ->
                                    currentPoints.clear()
                                    currentPoints.add(offset)
                                    redoStrokes.clear()
                                },
                                onDrag = { change, _ ->
                                    change.consume()
                                    currentPoints.add(change.position)
                                },
                                onDragEnd = {
                                    if (currentPoints.isNotEmpty()) {
                                        strokes.add(
                                            DrawingStrokeData(
                                                points = currentPoints.toList(),
                                                color = activeColor,
                                                widthPx = activeWidthPx,
                                                isEraser = selectedTool == DrawingTool.ERASER
                                            )
                                        )
                                    }
                                    currentPoints.clear()
                                },
                                onDragCancel = { currentPoints.clear() }
                            )
                        }
                ) {
                    fun drawStrokeData(strokeData: DrawingStrokeData) {
                        val renderedColor = if (strokeData.isEraser) paperColor else strokeData.color
                        if (strokeData.points.size == 1) {
                            drawCircle(
                                color = renderedColor,
                                radius = strokeData.widthPx / 2f,
                                center = strokeData.points.first()
                            )
                        } else if (strokeData.points.size > 1) {
                            val path = smoothComposePath(strokeData.points)
                            drawPath(
                                path = path,
                                color = renderedColor,
                                style = Stroke(
                                    width = strokeData.widthPx,
                                    cap = StrokeCap.Round,
                                    join = StrokeJoin.Round
                                )
                            )
                        }
                    }

                    strokes.forEach(::drawStrokeData)
                    if (currentPoints.isNotEmpty()) {
                        drawStrokeData(
                            DrawingStrokeData(
                                points = currentPoints.toList(),
                                color = activeColor,
                                widthPx = activeWidthPx,
                                isEraser = selectedTool == DrawingTool.ERASER
                            )
                        )
                    }
                }

                FilledTonalIconButton(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(42.dp),
                    onClick = {
                        isCanvasExpanded = !isCanvasExpanded
                        UiSoundPlayer.playAction(context, UiActionSound.Open)
                    }
                ) {
                    Icon(
                        imageVector = if (isCanvasExpanded) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                        contentDescription = stringResource(
                            if (isCanvasExpanded) R.string.drawing_restore_canvas
                            else R.string.drawing_expand_canvas
                        )
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surfaceContainerLow
        ) {
            if (isCanvasExpanded) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = stringResource(R.string.drawing_tools),
                        modifier = Modifier.weight(1f),
                        fontFamily = fontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(selectedInk, CircleShape)
                            .border(1.dp, outlineColor, CircleShape)
                    )
                    FilledTonalIconButton(
                        modifier = Modifier.size(38.dp),
                        enabled = strokes.isNotEmpty(),
                        onClick = {
                            if (strokes.isNotEmpty()) {
                                redoStrokes.add(strokes.removeAt(strokes.lastIndex))
                                UiSoundPlayer.playAction(context, UiActionSound.Back)
                            }
                        }
                    ) {
                        Icon(Icons.Default.Undo, contentDescription = stringResource(R.string.drawing_undo))
                    }
                    FilledTonalIconButton(
                        modifier = Modifier.size(38.dp),
                        enabled = redoStrokes.isNotEmpty(),
                        onClick = {
                            if (redoStrokes.isNotEmpty()) {
                                strokes.add(redoStrokes.removeAt(redoStrokes.lastIndex))
                                UiSoundPlayer.playAction(context, UiActionSound.Open)
                            }
                        }
                    ) {
                        Icon(Icons.Default.Redo, contentDescription = stringResource(R.string.drawing_redo))
                    }
                    FilledTonalIconButton(
                        modifier = Modifier.size(38.dp),
                        enabled = strokes.isNotEmpty(),
                        onClick = {
                            if (strokes.isNotEmpty()) {
                                strokes.clear()
                                redoStrokes.clear()
                                currentPoints.clear()
                                UiSoundPlayer.playAction(context, UiActionSound.Cancel)
                            }
                        }
                    ) {
                        Icon(Icons.Default.DeleteSweep, contentDescription = stringResource(R.string.drawing_clear))
                    }
                }
            } else {
                Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 9.dp)) {
                    Text(
                        text = stringResource(R.string.drawing_tools),
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(Modifier.height(6.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    DrawingTool.entries.forEach { tool ->
                        DrawingToolChip(
                            selected = selectedTool == tool,
                            text = stringResource(tool.labelRes),
                            previewWidthDp = tool.baseWidthDp.coerceIn(1.5f, 10f),
                            onClick = {
                                selectedTool = tool
                                if (tool == DrawingTool.HIGHLIGHTER && selectedColorIndex in 0..1) {
                                    selectedColorIndex = 4
                                }
                                UiSoundPlayer.playAction(context, UiActionSound.Select)
                            }
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.drawing_colors),
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(Modifier.height(6.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    inkColors.forEachIndexed { index, color ->
                        val selected = selectedTool != DrawingTool.ERASER && selectedColorIndex == index
                        Box(
                            modifier = Modifier
                                .size(if (selected) 32.dp else 28.dp)
                                .background(color, CircleShape)
                                .border(
                                    width = if (selected) 3.dp else 1.dp,
                                    color = if (selected) MaterialTheme.colorScheme.primary else outlineColor,
                                    shape = CircleShape
                                )
                                .clickable {
                                    selectedColorIndex = index
                                    if (selectedTool == DrawingTool.ERASER) selectedTool = DrawingTool.PEN
                                    UiSoundPlayer.playAction(context, UiActionSound.Color)
                                }
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.drawing_canvas_color),
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(Modifier.height(6.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    canvasColors.forEachIndexed { index, color ->
                        val selected = selectedCanvasIndex == index
                        Box(
                            modifier = Modifier
                                .size(if (selected) 32.dp else 28.dp)
                                .background(color, CircleShape)
                                .border(
                                    width = if (selected) 3.dp else 1.dp,
                                    color = if (selected) MaterialTheme.colorScheme.primary else outlineColor,
                                    shape = CircleShape
                                )
                                .clickable {
                                    selectedCanvasIndex = index
                                    paperColor = color
                                    UiSoundPlayer.playAction(context, UiActionSound.Color)
                                }
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.drawing_thickness),
                        modifier = Modifier.width(68.dp),
                        fontFamily = fontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    listOf(
                        R.string.drawing_thin,
                        R.string.drawing_medium,
                        R.string.drawing_thick
                    ).forEachIndexed { index, labelRes ->
                        DrawingThicknessButton(
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = if (index == 0) 0.dp else 6.dp),
                            selected = selectedThicknessIndex == index,
                            text = stringResource(labelRes),
                            onClick = {
                                selectedThicknessIndex = index
                                UiSoundPlayer.playAction(context, UiActionSound.Select)
                            }
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilledTonalIconButton(
                        enabled = strokes.isNotEmpty(),
                        onClick = {
                            if (strokes.isNotEmpty()) {
                                redoStrokes.add(strokes.removeAt(strokes.lastIndex))
                                UiSoundPlayer.playAction(context, UiActionSound.Back)
                            }
                        }
                    ) {
                        Icon(Icons.Default.Undo, contentDescription = stringResource(R.string.drawing_undo))
                    }
                    FilledTonalIconButton(
                        enabled = redoStrokes.isNotEmpty(),
                        onClick = {
                            if (redoStrokes.isNotEmpty()) {
                                strokes.add(redoStrokes.removeAt(redoStrokes.lastIndex))
                                UiSoundPlayer.playAction(context, UiActionSound.Open)
                            }
                        }
                    ) {
                        Icon(Icons.Default.Redo, contentDescription = stringResource(R.string.drawing_redo))
                    }
                    DrawingToolButton(
                        modifier = Modifier.weight(1f),
                        selected = selectedTool == DrawingTool.ERASER,
                        text = stringResource(R.string.drawing_tool_eraser),
                        onClick = {
                            selectedTool = DrawingTool.ERASER
                            UiSoundPlayer.playAction(context, UiActionSound.Select)
                        }
                    )
                    FilledTonalIconButton(
                        enabled = strokes.isNotEmpty(),
                        onClick = {
                            if (strokes.isNotEmpty()) {
                                strokes.clear()
                                redoStrokes.clear()
                                currentPoints.clear()
                                UiSoundPlayer.playAction(context, UiActionSound.Cancel)
                            }
                        }
                    ) {
                        Icon(Icons.Default.DeleteSweep, contentDescription = stringResource(R.string.drawing_clear))
                    }
                }
            }
            }
        }
    }
}

@Composable
private fun DrawingToolChip(
    selected: Boolean,
    text: String,
    previewWidthDp: Float,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .width(106.dp)
            .height(48.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = text,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                maxLines = 1,
                color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(5.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(previewWidthDp.dp.coerceAtMost(8.dp))
                    .background(
                        if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                        RoundedCornerShape(99.dp)
                    )
            )
        }
    }
}

@Composable
private fun DrawingThicknessButton(
    modifier: Modifier = Modifier,
    selected: Boolean,
    text: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(34.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(13.dp),
        color = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun DrawingToolButton(
    modifier: Modifier = Modifier,
    selected: Boolean,
    text: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(40.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(15.dp),
        color = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

private fun DrawingStrokeData.scaled(scaleX: Float, scaleY: Float): DrawingStrokeData {
    if (scaleX == 1f && scaleY == 1f) return this
    return copy(
        points = points.map { point -> Offset(point.x * scaleX, point.y * scaleY) }
    )
}

private fun smoothComposePath(points: List<Offset>): Path {
    return Path().apply {
        if (points.isEmpty()) return@apply
        moveTo(points.first().x, points.first().y)
        if (points.size == 2) {
            lineTo(points[1].x, points[1].y)
        } else {
            for (index in 1 until points.lastIndex) {
                val current = points[index]
                val next = points[index + 1]
                val mid = Offset((current.x + next.x) / 2f, (current.y + next.y) / 2f)
                quadraticBezierTo(current.x, current.y, mid.x, mid.y)
            }
            lineTo(points.last().x, points.last().y)
        }
    }
}

private suspend fun saveDrawingToCache(
    context: Context,
    size: IntSize,
    strokes: List<DrawingStrokeData>,
    backgroundColor: Color
): Uri? = withContext(Dispatchers.IO) {
    if (size.width <= 0 || size.height <= 0 || strokes.isEmpty()) return@withContext null

    runCatching {
        val bitmap = Bitmap.createBitmap(size.width, size.height, Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(bitmap)
        canvas.drawColor(backgroundColor.toArgb())

        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
        }

        strokes.forEach { stroke ->
            paint.color = (if (stroke.isEraser) backgroundColor else stroke.color).toArgb()
            paint.strokeWidth = stroke.widthPx
            if (stroke.points.size == 1) {
                paint.style = Paint.Style.FILL
                val point = stroke.points.first()
                canvas.drawCircle(point.x, point.y, stroke.widthPx / 2f, paint)
                paint.style = Paint.Style.STROKE
            } else if (stroke.points.size > 1) {
                val path = android.graphics.Path().apply {
                    moveTo(stroke.points.first().x, stroke.points.first().y)
                    if (stroke.points.size == 2) {
                        lineTo(stroke.points[1].x, stroke.points[1].y)
                    } else {
                        for (index in 1 until stroke.points.lastIndex) {
                            val current = stroke.points[index]
                            val next = stroke.points[index + 1]
                            val midX = (current.x + next.x) / 2f
                            val midY = (current.y + next.y) / 2f
                            quadTo(current.x, current.y, midX, midY)
                        }
                        lineTo(stroke.points.last().x, stroke.points.last().y)
                    }
                }
                canvas.drawPath(path, paint)
            }
        }

        val directory = File(context.cacheDir, "drawings").apply { mkdirs() }
        val file = File(directory, "drawing_${System.currentTimeMillis()}.png")
        FileOutputStream(file).use { output ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, output)
        }
        bitmap.recycle()
        Uri.fromFile(file)
    }.getOrNull()
}


private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

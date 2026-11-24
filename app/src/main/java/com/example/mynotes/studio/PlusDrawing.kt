package com.example.mynotes.studio

import com.example.mynotes.ui.sound.UiSoundPlayer

import com.example.mynotes.ui.workspace.WorkspaceToolActionButton
import com.example.mynotes.ui.workspace.WorkspaceToolFilterChip

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import com.example.mynotes.data.workspace.*
import com.example.mynotes.ui.components.AppAlertDialog
import com.example.mynotes.ui.components.StyledSettingsSlider
import java.util.UUID
import kotlin.math.*

internal data class DrawingSnapshot(val strokes: List<DrawingStroke>, val layers: List<DrawingLayer>, val active: String)

internal object PlusDrawingGeometry {
    fun visible(document: DrawingDocument): DrawingDocument {
        val layers = document.layers.ifEmpty { listOf(DrawingLayer()) }
        fun alpha(color: Int, opacity: Float) = (color and 0xffffff) or ((((color ushr 24) * opacity).roundToInt().coerceIn(0, 255)) shl 24)
        val result = layers.flatMap { layer ->
            if (!layer.visible) emptyList() else document.strokes.filter { it.layer == layer.id || layer.id == layers.first().id && layers.none { l -> l.id == it.layer } }.map { stroke ->
                stroke.copy(layer = "base", colorArgb = alpha(stroke.colorArgb, layer.opacity), fillArgb = stroke.fillArgb?.let { alpha(it, layer.opacity) })
            }
        }
        return document.copy(strokes = result, layers = listOf(DrawingLayer()), activeLayer = "base")
    }
    fun editable(stroke: DrawingStroke, layers: List<DrawingLayer>): Boolean {
        val layer = layers.firstOrNull { it.id == stroke.layer } ?: layers.firstOrNull()
        return layer?.let { it.visible && !it.locked } ?: true
    }
    fun inLasso(stroke: DrawingStroke, polygon: List<DrawingPoint>): Boolean {
        if (polygon.size < 3 || stroke.points.isEmpty()) return false
        val shape = polygon.map { StudioPoint(it.x, it.y) }
        if (stroke.points.any { StudioAlgorithms.inside(StudioPoint(it.x, it.y), shape) }) return true
        fun cross(a: DrawingPoint, b: DrawingPoint, c: DrawingPoint) = (b.x - a.x) * (c.y - a.y) - (b.y - a.y) * (c.x - a.x)
        fun intersects(a: DrawingPoint, b: DrawingPoint, c: DrawingPoint, d: DrawingPoint): Boolean {
            if (max(a.x, b.x) < min(c.x, d.x) || max(c.x, d.x) < min(a.x, b.x) || max(a.y, b.y) < min(c.y, d.y) || max(c.y, d.y) < min(a.y, b.y)) return false
            return cross(a, b, c) * cross(a, b, d) <= 0 && cross(c, d, a) * cross(c, d, b) <= 0
        }
        return stroke.points.zipWithNext().any { (a, b) -> polygon.indices.any { i -> intersects(a, b, polygon[i], polygon[(i + 1) % polygon.size]) } }
    }
    fun moved(strokes: List<DrawingStroke>, selected: Set<String>, dx: Float, dy: Float, width: Float, height: Float, infinite: Boolean): List<DrawingStroke> {
        val points = strokes.filter { it.id in selected }.flatMap { it.points }
        if (points.isEmpty()) return strokes
        val safeX = if (infinite) dx else dx.coerceIn(-points.minOf { it.x }, (width - points.maxOf { it.x }).coerceAtLeast(-points.minOf { it.x }))
        val safeY = if (infinite) dy else dy.coerceIn(-points.minOf { it.y }, (height - points.maxOf { it.y }).coerceAtLeast(-points.minOf { it.y }))
        return strokes.map { if (it.id in selected) DrawingGeometry.moved(it, safeX, safeY) else it }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable internal fun PlusDrawingTools(strokes: List<DrawingStroke>, layers: List<DrawingLayer>, active: String, selected: Set<String>,
    mode: String, rulers: Boolean, onMode: (String) -> Unit, onRulers: (Boolean) -> Unit,
    onLayers: (List<DrawingLayer>, String, List<DrawingStroke>) -> Unit, onSelection: (Set<String>) -> Unit) {
    var dialog by remember { mutableStateOf(false) }
    var rename by remember { mutableStateOf<DrawingLayer?>(null) }
    Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        WorkspaceToolActionButton(outlined = true, onClick = { dialog = true ; UiSoundPlayer.playFallbackClick()}) { Text("Layers · ${layers.size}") }
        WorkspaceToolFilterChip(mode == "lasso", { onMode("lasso") }, label = { Text("Lasso") })
        WorkspaceToolFilterChip(mode == "measure", { onMode("measure"); onRulers(true) }, label = { Text("Measure") })
        WorkspaceToolFilterChip(rulers, { onRulers(!rulers) }, label = { Text("Rulers") })
    }
    if (selected.isNotEmpty()) FlowRow {
        Text("${selected.size} selected", Modifier.padding(12.dp))
        WorkspaceToolActionButton(onClick = { onMode("select") ; UiSoundPlayer.playFallbackClick()}) { Text("Move selection") }
        WorkspaceToolActionButton(onClick = { onSelection(emptySet()) ; UiSoundPlayer.playFallbackClick(com.example.mynotes.ui.sound.UiActionSound.Cancel)}) { Text("Deselect") }
        WorkspaceToolActionButton(onClick = { onLayers(layers, active, strokes.filterNot { it.id in selected }); onSelection(emptySet()) ; UiSoundPlayer.playFallbackClick(com.example.mynotes.ui.sound.UiActionSound.Delete)}) { Text("Delete selection") }
    }
    rename?.let { layer -> StudioForm("Layer name", layer.name, onClose = { rename = null }) { name, _ -> onLayers(layers.map { if (it.id == layer.id) it.copy(name = name) else it }, active, strokes); rename = null } }
    if (dialog) AppAlertDialog(onDismissRequest = { dialog = false }, title = { Text("Drawing layers · bottom to top") }, text = {
        Column(Modifier.heightIn(max = 440.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            layers.forEachIndexed { index, layer ->
                var opacity by remember(layer.opacity) { mutableFloatStateOf(layer.opacity) }
                OutlinedCard { Column(Modifier.padding(12.dp)) {
                    TextButton(onClick = { onLayers(layers, layer.id, strokes) ; UiSoundPlayer.playFallbackClick()}) { Text((if (layer.id == active) "✓ " else "") + layer.name) }
                    Text("${strokes.count { it.layer == layer.id }} strokes")
                    Row { Text("Visible", Modifier.weight(1f)); Switch(layer.visible, { value -> onLayers(layers.map { if (it.id == layer.id) it.copy(visible = value) else it }, active, strokes) }) }
                    Row { Text("Locked", Modifier.weight(1f)); Switch(layer.locked, { value -> onLayers(layers.map { if (it.id == layer.id) it.copy(locked = value) else it }, active, strokes) }) }
                    Text("Opacity · ${(opacity * 100).roundToInt()}%")
                    StyledSettingsSlider(
                        value = opacity,
                        onValueChange = { opacity = it },
                        onValueChangeFinished = {
                            val updated = layers.map { if (it.id == layer.id) it.copy(opacity = opacity) else it }
                            onLayers(updated, active, strokes)
                        },
                        valueLabel = "${(opacity * 100).roundToInt()}%"
                    )
                    Row {
                        TextButton(onClick = { rename = layer; UiSoundPlayer.playFallbackClick() }) { Text("Rename") }
                        // At the ends of the stack these actions were no-ops;
                        // hide them instead of showing inert arrow buttons.
                        if (index > 0) TextButton(onClick = {
                            onLayers(PlusAlgorithms.move(layers, index, -1), active, strokes)
                            UiSoundPlayer.playFallbackClick()
                        }) { Text("↓") }
                        if (index < layers.lastIndex) TextButton(onClick = {
                            onLayers(PlusAlgorithms.move(layers, index, 1), active, strokes)
                            UiSoundPlayer.playFallbackClick()
                        }) { Text("↑") }
                    }
                    TextButton(enabled = layers.size > 1, onClick = {
                        val remaining = layers.filterNot { it.id == layer.id }
                        onLayers(remaining, if (active == layer.id) remaining.first().id else active, strokes.filterNot { it.layer == layer.id })
                    ; UiSoundPlayer.playFallbackClick(com.example.mynotes.ui.sound.UiActionSound.Delete)}) { Text("Delete layer and its strokes") }
                } }
            }
            TextButton(enabled = layers.size < 30, onClick = { val layer = DrawingLayer(UUID.randomUUID().toString(), "Layer ${layers.size + 1}"); onLayers(layers + layer, layer.id, strokes) ; UiSoundPlayer.playFallbackClick(com.example.mynotes.ui.sound.UiActionSound.Add)}) { Text("Add layer") }
            Text("Stroke list · accessible selection", style = MaterialTheme.typography.titleMedium)
            strokes.take(500).forEachIndexed { index, stroke ->
                Row { Checkbox(stroke.id in selected, { value -> onSelection(if (value) selected + stroke.id else selected - stroke.id) }, enabled = PlusDrawingGeometry.editable(stroke, layers)); Text("Stroke ${index + 1} · ${layers.firstOrNull { it.id == stroke.layer }?.name ?: "Base"}", Modifier.padding(top = 12.dp)) }
            }
        }
    }, confirmButton = { TextButton(onClick = { dialog = false ; UiSoundPlayer.playFallbackClick(com.example.mynotes.ui.sound.UiActionSound.Confirm)}) { Text("Done") } })
}

@Composable internal fun PlusRulers(coordinate: (Float, Float) -> StudioPoint) {
    val colors = MaterialTheme.colorScheme
    val paint = remember { android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG) }
    Canvas(Modifier.fillMaxSize()) {
        val thickness = 22.dp.toPx()
        val step = 64.dp.toPx()
        drawRect(colors.surfaceContainerHigh.copy(alpha = .92f), size = Size(size.width, thickness))
        drawRect(colors.surfaceContainerHigh.copy(alpha = .92f), size = Size(thickness, size.height))
        paint.color = colors.onSurface.toArgb()
        paint.textSize = 9.dp.toPx()
        repeat((size.width / step).toInt() + 1) { index ->
            val x = index * step
            drawLine(colors.outline, Offset(x, thickness - 5.dp.toPx()), Offset(x, thickness))
            drawIntoCanvas { it.nativeCanvas.drawText(coordinate(x, 0f).x.roundToInt().toString(), x + 3.dp.toPx(), 11.dp.toPx(), paint) }
        }
        repeat((size.height / step).toInt() + 1) { index ->
            val y = index * step
            drawLine(colors.outline, Offset(thickness - 5.dp.toPx(), y), Offset(thickness, y))
            drawIntoCanvas { it.nativeCanvas.save(); it.nativeCanvas.rotate(-90f, 11.dp.toPx(), y); it.nativeCanvas.drawText(coordinate(0f, y).y.roundToInt().toString(), 11.dp.toPx(), y, paint); it.nativeCanvas.restore() }
        }
    }
}

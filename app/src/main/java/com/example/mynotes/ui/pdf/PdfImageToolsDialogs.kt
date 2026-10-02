package com.example.mynotes.ui.pdf

import android.graphics.Paint
import android.graphics.RectF
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.example.mynotes.R
import com.example.mynotes.ui.components.AppTextButton

@Composable
internal fun PdfImageCropDialog(
    image: PdfImageElement, fontFamily: FontFamily, onDismiss: () -> Unit, onCrop: (PdfImageCrop) -> Unit
) {
    var crop by remember(image) { mutableStateOf(PdfImageCrop()) }
    val turns = PdfImageEdits.quarterTurns(image)
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.pdf_crop), fontFamily = fontFamily) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(stringResource(R.string.pdf_crop_hint), fontFamily = fontFamily)
                PdfCropPreview(image, crop) { crop = it }
                AppTextButton(stringResource(R.string.pdf_crop_reset), { crop = PdfImageCrop() }, fontFamily = fontFamily)
            }
        },
        confirmButton = {
            AppTextButton(stringResource(R.string.pdf_image_apply), { onCrop(crop.inSourceCoordinates(turns)) }, fontFamily = fontFamily)
        },
        dismissButton = { AppTextButton(stringResource(android.R.string.cancel), onDismiss, fontFamily = fontFamily) }
    )
}

@Composable
private fun PdfCropPreview(image: PdfImageElement, crop: PdfImageCrop, onCropChange: (PdfImageCrop) -> Unit) {
    val turns = PdfImageEdits.quarterTurns(image)
    val swapped = turns % 2 == 1
    val ratio = if (swapped) image.bitmap.height.toFloat() / image.bitmap.width else image.bitmap.width.toFloat() / image.bitmap.height
    val paint = remember { Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG) }
    val handles = remember { PdfCropHandle.entries.filter { it != PdfCropHandle.MOVE } }
    val accent = MaterialTheme.colorScheme.primary
    // Keep the pointer gesture alive while its latest selection/callback changes.
    val latestCrop by rememberUpdatedState(crop)
    val latestOnCropChange by rememberUpdatedState(onCropChange)
    BoxWithConstraints(Modifier.fillMaxWidth().heightIn(max = 320.dp), contentAlignment = Alignment.Center) {
        val previewHeight = minOf(maxWidth / ratio, maxHeight)
        Canvas(
            Modifier.size(previewHeight * ratio, previewHeight)
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                .clipToBounds()
                .pointerInput(image) {
                    var activeHandle: PdfCropHandle? = null
                    var gestureCrop = latestCrop
                    detectDragGestures(
                        onDragStart = { point ->
                            gestureCrop = latestCrop
                            val bounds = gestureCrop.rect(size.width.toFloat(), size.height.toFloat())
                            activeHandle = handles.minByOrNull { (it.position(bounds) - point).getDistanceSquared() }
                                ?.takeIf { (it.position(bounds) - point).getDistance() <= 24.dp.toPx() }
                                ?: PdfCropHandle.MOVE.takeIf { bounds.contains(point) }
                        },
                        onDragEnd = { activeHandle = null },
                        onDragCancel = { activeHandle = null },
                        onDrag = { change, amount ->
                            activeHandle?.let { handle ->
                                change.consume()
                                gestureCrop = gestureCrop.drag(
                                    handle, amount.x / size.width, amount.y / size.height,
                                    (24.dp.toPx() / size.width).coerceAtMost(1f),
                                    (24.dp.toPx() / size.height).coerceAtMost(1f)
                                )
                                latestOnCropChange(gestureCrop)
                            }
                        }
                    )
                }
        ) {
            val drawWidth = if (swapped) size.height else size.width
            val drawHeight = if (swapped) size.width else size.height
            drawIntoCanvas { canvas ->
                val native = canvas.nativeCanvas
                native.save()
                native.translate(size.width / 2f, size.height / 2f)
                native.rotate(turns * 90f)
                native.drawBitmap(image.bitmap, null, RectF(-drawWidth / 2f, -drawHeight / 2f, drawWidth / 2f, drawHeight / 2f), paint)
                native.restore()
            }
            val bounds = crop.rect(size.width, size.height)
            val shade = Color.Black.copy(alpha = 0.55f)
            drawRect(shade, size = Size(size.width, bounds.top))
            drawRect(shade, Offset(0f, bounds.bottom), Size(size.width, size.height - bounds.bottom))
            drawRect(shade, Offset(0f, bounds.top), Size(bounds.left, bounds.height))
            drawRect(shade, Offset(bounds.right, bounds.top), Size(size.width - bounds.right, bounds.height))
            drawRect(Color.White, bounds.topLeft, bounds.size, style = Stroke(3.dp.toPx()))
            drawRect(accent, bounds.topLeft, bounds.size, style = Stroke(1.dp.toPx()))
            handles.forEach { handle ->
                drawCircle(Color.White, 5.dp.toPx(), handle.position(bounds))
                drawCircle(accent, 5.dp.toPx(), handle.position(bounds), style = Stroke(1.dp.toPx()))
            }
        }
    }
}

private fun PdfImageCrop.rect(width: Float, height: Float) = Rect(left * width, top * height, right * width, bottom * height)

private fun PdfCropHandle.position(bounds: Rect) = Offset(
    when (horizontal) { -1 -> bounds.left; 1 -> bounds.right; else -> bounds.center.x },
    when (vertical) { -1 -> bounds.top; 1 -> bounds.bottom; else -> bounds.center.y }
)

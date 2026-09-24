package com.example.mynotes.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas as AndroidCanvas
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.media.ExifInterface
import android.graphics.Paint
import android.graphics.RectF
import android.net.Uri
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.example.mynotes.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import kotlin.math.max
import kotlin.math.roundToInt

private const val PROFILE_EDITOR_MAX_SOURCE_SIDE = 2048
private const val PROFILE_OUTPUT_SIZE = 1024
private const val PROFILE_MIN_ZOOM = 1f
private const val PROFILE_MAX_ZOOM = 5f
private const val PROFILE_FOLDER = "profile"
private const val PROFILE_SOURCE_FILE = "profile_source.bin"
private const val PROFILE_SOURCE_OWNER_FILE = "profile_source_owner.txt"

/**
 * Editor autocontenido de la imagen de perfil.
 *
 * La imagen se dibuja manualmente dentro de un viewport circular para que el
 * usuario vea exactamente la porción que terminará utilizándose como avatar.
 * El gesto de arrastre modifica [offset] y el gesto de pinza modifica [zoom].
 * El recorte final se guarda como un JPEG cuadrado; las pantallas de MyNotes
 * lo muestran con CircleShape, por lo que no se pierden píxeles de las esquinas
 * y puede volver a editarse sin introducir un borde circular permanente.
 */
@Composable
fun ProfileImageEditorDialog(
    sourceUri: Uri,
    fontFamily: FontFamily,
    onDismissRequest: () -> Unit,
    onImageSaved: (Uri) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var sourceBitmap by remember(sourceUri) { mutableStateOf<Bitmap?>(null) }
    var loadFailed by remember(sourceUri) { mutableStateOf(false) }
    var saving by remember(sourceUri) { mutableStateOf(false) }
    var zoom by remember(sourceUri) { mutableFloatStateOf(PROFILE_MIN_ZOOM) }
    var offset by remember(sourceUri) { mutableStateOf(Offset.Zero) }
    var viewportSize by remember(sourceUri) { mutableStateOf(IntSize.Zero) }

    LaunchedEffect(sourceUri) {
        sourceBitmap = withContext(Dispatchers.IO) { decodeProfileBitmap(context, sourceUri) }
        loadFailed = sourceBitmap == null
        zoom = PROFILE_MIN_ZOOM
        offset = Offset.Zero
    }
    DisposableEffect(sourceBitmap) {
        val ownedBitmap = sourceBitmap
        onDispose {
            if (ownedBitmap != null && !ownedBitmap.isRecycled) ownedBitmap.recycle()
        }
    }

    /*
     * No usamos androidx.compose.ui.window.Dialog aquí. En Android 8/9,
     * especialmente en Samsung, un Dialog crea una Window enfocada y el sistema
     * puede mostrar la barra de navegación durante uno o dos frames antes de que
     * podamos ocultarla. Ese era el destello de los tres botones al abrir el editor.
     *
     * Un Popup NO enfocable mantiene el foco en la Activity inmersiva que ya está
     * ocultando la navegación, por lo que los botones de Android nunca reciben una
     * ventana para reaparecer. Los gestos táctiles del editor siguen funcionando.
     */
    BackHandler(enabled = !saving) { onDismissRequest() }

    Popup(
        alignment = Alignment.Center,
        properties = PopupProperties(
            focusable = false,
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        val scrimInteraction = remember { MutableInteractionSource() }
        val surfaceInteraction = remember { MutableInteractionSource() }
        val editorPanelColor = MaterialTheme.colorScheme.surfaceContainerHigh
        val darkNeutralButtons = remember(editorPanelColor) { editorPanelColor.luminance() < 0.52f }
        val editorButtonContainer = remember(editorPanelColor, darkNeutralButtons) {
            if (darkNeutralButtons) {
                mixProfileEditorColor(Color.White, editorPanelColor, 0.18f)
            } else {
                mixProfileEditorColor(Color.Black, editorPanelColor, 0.14f)
            }
        }
        val editorButtonContent = remember(editorButtonContainer) {
            if (editorButtonContainer.luminance() >= 0.56f) Color.Black else Color.White
        }
        val editorButtonBorder = remember(editorButtonContainer, editorButtonContent) {
            if (editorButtonContainer.luminance() >= 0.56f) {
                Color.Black.copy(alpha = 0.14f)
            } else {
                Color.White.copy(alpha = 0.20f)
            }
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.55f))
                .clickable(
                    enabled = !saving,
                    interactionSource = scrimInteraction,
                    indication = null,
                    onClick = onDismissRequest
                ),
            contentAlignment = Alignment.Center
        ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .widthIn(max = 430.dp)
                .clickable(
                    interactionSource = surfaceInteraction,
                    indication = null,
                    onClick = { }
                ),
            shape = RoundedCornerShape(28.dp),
            color = editorPanelColor,
            tonalElevation = 0.dp,
            shadowElevation = 0.dp
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.extreme_profile_editor_title),
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = stringResource(R.string.extreme_profile_editor_hint),
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontFamily = fontFamily,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(16.dp))

                when {
                    sourceBitmap != null -> {
                        val bitmap = sourceBitmap!!
                        val imageBitmap = remember(bitmap) { bitmap.asImageBitmap() }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .widthIn(max = 310.dp)
                                .aspectRatio(1f)
                                .clip(CircleShape)
                                .background(Color.Black)
                                .border(3.dp, MaterialTheme.colorScheme.primary, CircleShape)
                                .onSizeChanged {
                                    viewportSize = it
                                    offset = clampProfileOffset(bitmap, it, zoom, offset)
                                }
                                .pointerInput(bitmap, viewportSize) {
                                    detectTransformGestures { _, pan, gestureZoom, _ ->
                                        val nextZoom = (zoom * gestureZoom).coerceIn(PROFILE_MIN_ZOOM, PROFILE_MAX_ZOOM)
                                        zoom = nextZoom
                                        offset = clampProfileOffset(bitmap, viewportSize, nextZoom, offset + pan)
                                    }
                                }
                        ) {
                            Canvas(Modifier.fillMaxSize()) {
                                val baseScale = max(size.width / bitmap.width.toFloat(), size.height / bitmap.height.toFloat())
                                val width = bitmap.width * baseScale * zoom
                                val height = bitmap.height * baseScale * zoom
                                val left = (size.width - width) / 2f + offset.x
                                val top = (size.height - height) / 2f + offset.y
                                drawImage(
                                    image = imageBitmap,
                                    srcOffset = IntOffset.Zero,
                                    srcSize = IntSize(bitmap.width, bitmap.height),
                                    dstOffset = IntOffset(left.roundToInt(), top.roundToInt()),
                                    dstSize = IntSize(width.roundToInt().coerceAtLeast(1), height.roundToInt().coerceAtLeast(1))
                                )
                            }
                        }
                        Spacer(Modifier.height(14.dp))
                        Text(
                            text = stringResource(R.string.extreme_profile_zoom, (zoom * 100f).roundToInt()),
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.onSurface,
                            fontFamily = fontFamily,
                            textAlign = TextAlign.Center
                        )
                        StyledSettingsSlider(
                            value = zoom,
                            onValueChange = { requested ->
                                zoom = requested.coerceIn(PROFILE_MIN_ZOOM, PROFILE_MAX_ZOOM)
                                offset = clampProfileOffset(bitmap, viewportSize, zoom, offset)
                            },
                            valueRange = PROFILE_MIN_ZOOM..PROFILE_MAX_ZOOM,
                            activeColor = MaterialTheme.colorScheme.primary,
                            inactiveColor = MaterialTheme.colorScheme.outlineVariant,
                            style = "capsule",
                            valueLabel = stringResource(
                                R.string.extreme_profile_zoom,
                                (zoom * 100f).roundToInt()
                            )
                        )
                        Button(
                            onClick = {
                                zoom = PROFILE_MIN_ZOOM
                                offset = Offset.Zero
                            },
                            enabled = !saving,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = editorButtonContainer,
                                contentColor = editorButtonContent,
                                disabledContainerColor = editorButtonContainer.copy(alpha = 0.55f),
                                disabledContentColor = editorButtonContent.copy(alpha = 0.55f)
                            ),
                            border = BorderStroke(1.dp, editorButtonBorder)
                        ) {
                            Text(
                                text = stringResource(R.string.extreme_profile_reset),
                                fontFamily = fontFamily,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    loadFailed -> {
                        Text(
                            text = stringResource(R.string.extreme_profile_error),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp),
                            color = MaterialTheme.colorScheme.error,
                            fontFamily = fontFamily,
                            textAlign = TextAlign.Center
                        )
                    }
                    else -> {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 52.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator()
                            Spacer(Modifier.height(12.dp))
                            Text(
                                text = stringResource(R.string.extreme_profile_loading),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontFamily = fontFamily
                            )
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.End),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onDismissRequest,
                        enabled = !saving,
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = editorButtonContainer,
                            contentColor = editorButtonContent,
                            disabledContainerColor = editorButtonContainer.copy(alpha = 0.55f),
                            disabledContentColor = editorButtonContent.copy(alpha = 0.55f)
                        ),
                        border = BorderStroke(1.dp, editorButtonBorder)
                    ) {
                        Text(
                            text = stringResource(R.string.cancel),
                            fontFamily = fontFamily,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Button(
                        enabled = sourceBitmap != null && viewportSize.width > 0 && viewportSize.height > 0 && !saving,
                        onClick = {
                            val bitmap = sourceBitmap ?: return@Button
                            saving = true
                            scope.launch {
                                val savedUri = withContext(Dispatchers.IO) {
                                    saveProfileCrop(
                                        context = context,
                                        sourceUri = sourceUri,
                                        bitmap = bitmap,
                                        viewportSize = viewportSize,
                                        zoom = zoom,
                                        offset = offset
                                    )
                                }
                                saving = false
                                if (savedUri != null) onImageSaved(savedUri) else loadFailed = true
                            }
                        },
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = editorButtonContainer,
                            contentColor = editorButtonContent,
                            disabledContainerColor = editorButtonContainer.copy(alpha = 0.55f),
                            disabledContentColor = editorButtonContent.copy(alpha = 0.55f)
                        ),
                        border = BorderStroke(1.dp, editorButtonBorder)
                    ) {
                        if (saving) {
                            CircularProgressIndicator(modifier = Modifier.height(18.dp), strokeWidth = 2.dp, color = editorButtonContent)
                        } else {
                            Text(
                                text = stringResource(R.string.extreme_profile_apply),
                                fontFamily = fontFamily,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
        }
    }
}

/** Devuelve la imagen fuente persistida para volver a ajustar un avatar ya guardado. */
fun managedProfileSourceUri(context: Context, currentProfileImageUri: String): Uri? {
    val directory = File(context.filesDir, PROFILE_FOLDER)
    val source = File(directory, PROFILE_SOURCE_FILE)
    val owner = File(directory, PROFILE_SOURCE_OWNER_FILE).takeIf { it.isFile }?.readText()?.trim().orEmpty()
    return source.takeIf { owner == currentProfileImageUri && it.isFile && it.length() > 0L }?.let(Uri::fromFile)
}

/** Elimina únicamente los archivos internos gestionados por el editor de perfil. */
fun clearManagedProfileImages(context: Context) {
    File(context.filesDir, PROFILE_FOLDER).deleteRecursively()
}

private fun mixProfileEditorColor(base: Color, target: Color, amount: Float): Color {
    val value = amount.coerceIn(0f, 1f)
    return Color(
        red = base.red + (target.red - base.red) * value,
        green = base.green + (target.green - base.green) * value,
        blue = base.blue + (target.blue - base.blue) * value,
        alpha = 1f
    )
}

private fun clampProfileOffset(bitmap: Bitmap, viewport: IntSize, zoom: Float, requested: Offset): Offset {
    if (viewport.width <= 0 || viewport.height <= 0 || bitmap.width <= 0 || bitmap.height <= 0) return Offset.Zero
    val baseScale = max(viewport.width / bitmap.width.toFloat(), viewport.height / bitmap.height.toFloat())
    val displayedWidth = bitmap.width * baseScale * zoom
    val displayedHeight = bitmap.height * baseScale * zoom
    val maxX = ((displayedWidth - viewport.width) / 2f).coerceAtLeast(0f)
    val maxY = ((displayedHeight - viewport.height) / 2f).coerceAtLeast(0f)
    return Offset(requested.x.coerceIn(-maxX, maxX), requested.y.coerceIn(-maxY, maxY))
}

private fun decodeProfileBitmap(context: Context, uri: Uri): Bitmap? {
    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = if (uri.scheme == "file") {
                val path = uri.path ?: return null
                ImageDecoder.createSource(File(path))
            } else {
                ImageDecoder.createSource(context.contentResolver, uri)
            }
            ImageDecoder.decodeBitmap(source) { decoder, info, _ ->
                val width = info.size.width.coerceAtLeast(1)
                val height = info.size.height.coerceAtLeast(1)
                val longest = max(width, height)
                if (longest > PROFILE_EDITOR_MAX_SOURCE_SIDE) {
                    val ratio = PROFILE_EDITOR_MAX_SOURCE_SIDE.toFloat() / longest.toFloat()
                    decoder.setTargetSize((width * ratio).roundToInt().coerceAtLeast(1), (height * ratio).roundToInt().coerceAtLeast(1))
                }
                decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
            }
        } else {
            decodeProfileBitmapLegacy(context, uri)
        }
    } catch (_: Exception) {
        null
    }
}

private fun decodeProfileBitmapLegacy(context: Context, uri: Uri): Bitmap? {
    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    openProfileInputStream(context, uri)?.use { BitmapFactory.decodeStream(it, null, bounds) } ?: return null
    var sample = 1
    while (max(bounds.outWidth / sample, bounds.outHeight / sample) > PROFILE_EDITOR_MAX_SOURCE_SIDE) sample *= 2
    val options = BitmapFactory.Options().apply {
        inSampleSize = sample
        inPreferredConfig = Bitmap.Config.ARGB_8888
    }
    val decoded = openProfileInputStream(context, uri)?.use { BitmapFactory.decodeStream(it, null, options) } ?: return null
    return applyLegacyExifOrientation(context, uri, decoded)
}

private fun applyLegacyExifOrientation(context: Context, uri: Uri, bitmap: Bitmap): Bitmap {
    val orientation = try {
        openProfileInputStream(context, uri)?.use { stream ->
            ExifInterface(stream).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        } ?: ExifInterface.ORIENTATION_NORMAL
    } catch (_: Exception) {
        ExifInterface.ORIENTATION_NORMAL
    }
    val matrix = Matrix()
    when (orientation) {
        ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.setScale(-1f, 1f)
        ExifInterface.ORIENTATION_ROTATE_180 -> matrix.setRotate(180f)
        ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.setScale(1f, -1f)
        ExifInterface.ORIENTATION_TRANSPOSE -> {
            matrix.setRotate(90f)
            matrix.postScale(-1f, 1f)
        }
        ExifInterface.ORIENTATION_ROTATE_90 -> matrix.setRotate(90f)
        ExifInterface.ORIENTATION_TRANSVERSE -> {
            matrix.setRotate(-90f)
            matrix.postScale(-1f, 1f)
        }
        ExifInterface.ORIENTATION_ROTATE_270 -> matrix.setRotate(-90f)
        else -> return bitmap
    }
    return try {
        Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true).also { rotated ->
            if (rotated !== bitmap) bitmap.recycle()
        }
    } catch (_: Exception) {
        bitmap
    }
}

private fun saveProfileCrop(
    context: Context,
    sourceUri: Uri,
    bitmap: Bitmap,
    viewportSize: IntSize,
    zoom: Float,
    offset: Offset
): Uri? {
    if (viewportSize.width <= 0 || viewportSize.height <= 0) return null
    return try {
        val profileDir = File(context.filesDir, PROFILE_FOLDER).apply { mkdirs() }
        persistProfileSource(context, sourceUri, File(profileDir, PROFILE_SOURCE_FILE))

        val output = Bitmap.createBitmap(PROFILE_OUTPUT_SIZE, PROFILE_OUTPUT_SIZE, Bitmap.Config.ARGB_8888)
        val canvas = AndroidCanvas(output)
        canvas.drawColor(android.graphics.Color.BLACK)
        val baseScale = max(viewportSize.width / bitmap.width.toFloat(), viewportSize.height / bitmap.height.toFloat())
        val displayedWidth = bitmap.width * baseScale * zoom
        val displayedHeight = bitmap.height * baseScale * zoom
        val left = (viewportSize.width - displayedWidth) / 2f + offset.x
        val top = (viewportSize.height - displayedHeight) / 2f + offset.y
        val scaleX = PROFILE_OUTPUT_SIZE.toFloat() / viewportSize.width.toFloat()
        val scaleY = PROFILE_OUTPUT_SIZE.toFloat() / viewportSize.height.toFloat()
        val destination = RectF(left * scaleX, top * scaleY, (left + displayedWidth) * scaleX, (top + displayedHeight) * scaleY)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG or Paint.DITHER_FLAG)
        canvas.drawBitmap(bitmap, null, destination, paint)

        val target = File(profileDir, "avatar_${System.currentTimeMillis()}.jpg")
        FileOutputStream(target).use { stream ->
            if (!output.compress(Bitmap.CompressFormat.JPEG, 95, stream)) error("Could not encode profile image")
        }
        output.recycle()
        val targetUri = Uri.fromFile(target)
        File(profileDir, PROFILE_SOURCE_OWNER_FILE).writeText(targetUri.toString())
        targetUri
    } catch (_: Exception) {
        null
    }
}

private fun persistProfileSource(context: Context, sourceUri: Uri, destination: File) {
    val currentSource = sourceUri.takeIf { it.scheme == "file" }?.path?.let(::File)
    if (currentSource != null && runCatching { currentSource.canonicalFile == destination.canonicalFile }.getOrDefault(false)) return
    val temp = File(destination.parentFile, "${destination.name}.tmp")
    openProfileInputStream(context, sourceUri)?.use { input -> FileOutputStream(temp).use { output -> input.copyTo(output) } } ?: return
    if (destination.exists()) destination.delete()
    if (!temp.renameTo(destination)) {
        temp.copyTo(destination, overwrite = true)
        temp.delete()
    }
}

private fun openProfileInputStream(context: Context, uri: Uri): InputStream? {
    return try {
        if (uri.scheme == "file") {
            val path = uri.path ?: return null
            FileInputStream(File(path))
        } else {
            context.contentResolver.openInputStream(uri)
        }
    } catch (_: Exception) {
        null
    }
}

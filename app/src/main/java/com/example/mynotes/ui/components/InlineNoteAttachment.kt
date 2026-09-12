package com.example.mynotes.ui.components

import android.app.ActivityManager
import android.content.Context
import android.net.Uri
import android.view.TextureView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import coil3.compose.AsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.request.maxBitmapSize
import coil3.size.Precision
import coil3.size.Scale
import coil3.size.Size
import com.example.mynotes.R
import com.example.mynotes.data.Attachment
import com.example.mynotes.performance.AttachmentPreviewCache
import com.example.mynotes.ui.sound.UiActionSound
import com.example.mynotes.ui.sound.UiSoundPlayer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

private val InlineAttachmentShape = RoundedCornerShape(28.dp)

private object InlinePlaybackCoordinator {
    private var activePlayer: ExoPlayer? = null
    fun activate(player: ExoPlayer) {
        if (activePlayer !== player) {
            try {
                activePlayer?.pause()
            } catch (_: Exception) {
            }
        }
        activePlayer = player
    }
    fun clear(player: ExoPlayer) {
        if (activePlayer === player) {
            activePlayer = null
        }
    }
}

/**
 * Adjuntos grandes para la pantalla de detalle de una nota.
 *
 * - Las imágenes respetan su proporción original y ocupan todo el ancho.
 * - Los videos se reproducen dentro de la nota usando Media3 ExoPlayer + TextureView.
 * - Los audios se reproducen dentro de la nota con controles propios.
 * - PDF/DOCX/XLSX/PPTX y otros archivos se abren en el visor de pantalla completa.
 */
@Composable
fun InlineNoteAttachment(attachment: Attachment, fontFamily: FontFamily = FontFamily.Default, previewDelayMillis: Long = 0L,
    performanceMode: String = "balanced") {
    val extension = attachment.name?.substringAfterLast(".", "")?.lowercase().orEmpty()
    when {
        attachment.type == "image" -> InlineImageAttachment(attachment = attachment)
        attachment.type == "video" -> InlineVideoAttachment(attachment = attachment, previewDelayMillis = previewDelayMillis,
                performanceMode = performanceMode)
        attachment.type == "audio" || attachment.type == "voice" -> InlineAudioAttachment(attachment = attachment, fontFamily = fontFamily)
        extension == "pdf" -> InlinePdfAttachment(attachment = attachment, previewDelayMillis = previewDelayMillis,
                performanceMode = performanceMode)
        else -> InlineFileAttachment(attachment = attachment, extension = extension, fontFamily = fontFamily)
    }
}

@Composable
private fun InlineImageAttachment(attachment: Attachment) {
    val context = LocalContext.current
    val uri = remember(attachment.uri) { Uri.parse(attachment.uri) }
    /*
     * DETALLE DE LA NOTA = ARCHIVO ORIGINAL.
     *
     * No se usa una miniatura ni una relación 4:3 provisional. AsyncImage
     * comienza a cargar directamente el archivo original y su tamaño de
     * composición se obtiene de la propia imagen cuando Coil termina de
     * decodificarla. Por eso no existe una primera fase visible con franjas
     * laterales: la primera imagen que se dibuja ya tiene su proporción real.
     *
     * No se aplica retraso de preview a las imágenes de detalle.
     */
    val detailBitmapLimit = remember(context) {
            val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE)
                    as? ActivityManager
            when {
                activityManager?.isLowRamDevice == true -> Size(3072, 3072)
                (activityManager?.memoryClass ?: 256) < 192 -> Size(4096, 4096)
                else -> Size.ORIGINAL
            }
        }
    val fullResolutionRequest = remember(context, uri, detailBitmapLimit) {
            ImageRequest.Builder(context).data(uri).size(Size.ORIGINAL)
                /*
                 * Equipos normales conservan la resolución original. Solo
                 * limitamos el bitmap en dispositivos oficialmente Low-RAM o
                 * con heaps muy pequeños, donde una foto de 48/108 MP puede
                 * cerrar la aplicación por falta de memoria.
                 */
                .maxBitmapSize(detailBitmapLimit).precision(Precision.EXACT).scale(Scale.FIT).allowHardware(true)
                /*
                 * La imagen ya permanece en composición mientras la nota está
                 * abierta. Evitamos retener otra copia gigante en la caché RAM
                 * de Coil al salir de la pantalla.
                 */
                .memoryCachePolicy(CachePolicy.DISABLED).build()
        }
    var imageFailed by remember(uri) {
        mutableStateOf(false)
    }
    var zoomEnabled by remember(uri) {
        mutableStateOf(false)
    }
    var zoom by remember(uri) {
        mutableStateOf(1f)
    }
    var offsetX by remember(uri) {
        mutableStateOf(0f)
    }
    var offsetY by remember(uri) {
        mutableStateOf(0f)
    }
    LaunchedEffect(zoomEnabled) {
        if (!zoomEnabled) {
            zoom = 1f
            offsetX = 0f
            offsetY = 0f
        }
    }
    Box(modifier = Modifier.fillMaxWidth().clip(InlineAttachmentShape), contentAlignment = Alignment.Center) {
        val gestureModifier = if (zoomEnabled) {
                Modifier.pointerInput(uri, zoomEnabled) {
                    detectTransformGestures { _, pan, gestureZoom, _ -> val newZoom = (zoom * gestureZoom).coerceIn(1f, 5f)
                        if (newZoom <= 1.001f) {
                            zoom = 1f
                            offsetX = 0f
                            offsetY = 0f
                        } else {
                            val zoomChange = newZoom / zoom
                            zoom = newZoom
                            offsetX = (offsetX * zoomChange) + pan.x
                            offsetY = (offsetY * zoomChange) + pan.y
                        }
                    }
                }
            } else {
                Modifier
            }
        if (!imageFailed) {
            AsyncImage(model = fullResolutionRequest, contentDescription = attachment.name, modifier = Modifier.fillMaxWidth()
                    .then(gestureModifier).graphicsLayer {
                        scaleX = zoom
                        scaleY = zoom
                        translationX = offsetX
                        translationY = offsetY
                        transformOrigin = TransformOrigin.Center
                        clip = false
                    },
                /*
                 * FillWidth usa todo el ancho disponible y, al no imponer una
                 * altura ni un aspectRatio artificial, conserva la relación real
                 * del bitmap.
                 */
                contentScale = ContentScale.FillWidth, filterQuality = FilterQuality.Medium, onSuccess = {
                    imageFailed = false
                }, onError = {
                    imageFailed = true
                    zoomEnabled = false
                })
        } else {
            Surface(modifier = Modifier.fillMaxWidth().height(180.dp), shape = InlineAttachmentShape,
                color = MaterialTheme.colorScheme.surfaceContainerLow) {
                Column(modifier = Modifier.padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center) {
                    Icon(imageVector = Icons.Default.Description, contentDescription = null, modifier = Modifier.size(34.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = stringResource(R.string.image_format_not_supported), color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 12.sp)
                }
            }
        }
        /*
         * El zoom está DESACTIVADO por defecto. Solo se habilita cuando se
         * toca este botón blanco. El botón no se transforma con la foto y
         * permanece fijo en la esquina inferior derecha.
         */
        if (!imageFailed) {
        Surface(modifier = Modifier.align(Alignment.BottomEnd).padding(12.dp).size(48.dp), shape = CircleShape, color = Color.White,
            shadowElevation = 4.dp, onClick = {
                UiSoundPlayer.playAction(context = context, action = UiActionSound.Zoom)
                zoomEnabled = !zoomEnabled
            }) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Icon(imageVector = if (zoomEnabled) {
                            Icons.Default.Close
                        } else {
                            Icons.Default.ZoomIn
                        }, contentDescription = if (zoomEnabled) {
                            "Desactivar zoom"
                        } else {
                            "Activar zoom"
                        }, tint = Color.Black, modifier = Modifier.size(24.dp))
            }
        }
        }
    }
}

@Composable
private fun InlineVideoAttachment(attachment: Attachment, previewDelayMillis: Long, performanceMode: String) {
    val context = LocalContext.current
    val uri = remember(attachment.uri) { Uri.parse(attachment.uri) }
    val preview by produceState<AttachmentPreviewCache.MediaPreview?>(initialValue = null, key1 = attachment.uri, key2 = performanceMode) {
        if (previewDelayMillis > 0L) {
            delay(previewDelayMillis)
        }
        value = AttachmentPreviewCache.withPreviewPermit {
            AttachmentPreviewCache.loadVideoPreview(context = context, uri = uri, performanceMode = performanceMode)
        }
    }
    val ratio = preview?.aspectRatio?.coerceIn(0.45f, 2.4f)?: (16f / 9f)
    val textureView = remember(uri) {
        TextureView(context).apply {
            isOpaque = false
        }
    }
    var player by remember(uri) {
        mutableStateOf<ExoPlayer?>(null)
    }
    var prepared by remember(uri) {
        mutableStateOf(false)
    }
    var buffering by remember(uri) {
        mutableStateOf(false)
    }
    var playing by remember(uri) {
        mutableStateOf(false)
    }
    var playbackError by remember(uri) {
        mutableStateOf(false)
    }
    var duration by remember(uri) {
        mutableIntStateOf(0)
    }
    var position by remember(uri) {
        mutableIntStateOf(0)
    }
    DisposableEffect(player, textureView, uri) {
        val currentPlayer = player
        if (currentPlayer == null) {
            onDispose {
            }
        } else {
            val listener = object : Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        when (playbackState) {
                            Player.STATE_BUFFERING -> {
                                buffering = true
                            }
                            Player.STATE_READY -> {
                                buffering = false
                                prepared = true
                                duration = currentPlayer.duration.coerceAtLeast(0L).coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
                            }
                            Player.STATE_ENDED -> {
                                buffering = false
                                prepared = true
                                playing = false
                                if (duration > 0) {
                                    position = duration
                                }
                            }
                            Player.STATE_IDLE -> {
                                buffering = false
                            }
                        }
                    }
                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        playing = isPlaying
                    }
                    override fun onPlayerError(error: PlaybackException) {
                        playbackError = true
                        buffering = false
                        prepared = false
                        playing = false
                    }
                }
            currentPlayer.addListener(listener)
            currentPlayer.setVideoTextureView(textureView)
            try {
                currentPlayer.setMediaItem(MediaItem.fromUri(uri))
                currentPlayer.playWhenReady = true
                currentPlayer.prepare()
                InlinePlaybackCoordinator.activate(currentPlayer)
            } catch (_: Exception) {
                playbackError = true
            }
            onDispose {
                try {
                    currentPlayer.removeListener(listener)
                } catch (_: Exception) {
                }
                try {
                    currentPlayer.clearVideoTextureView(textureView)
                } catch (_: Exception) {
                }
                InlinePlaybackCoordinator.clear(currentPlayer)
                try {
                    currentPlayer.release()
                } catch (_: Exception) {
                }
            }
        }
    }
    LaunchedEffect(player, prepared) {
        while (player != null) {
            val currentPlayer = player
            if (currentPlayer != null && prepared) {
                position = currentPlayer.currentPosition.coerceAtLeast(0L).coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
                val currentDuration = currentPlayer.duration.coerceAtLeast(0L).coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
                if (currentDuration > 0) {
                    duration = currentDuration
                }
            }
            delay(300)
        }
    }
    Column(modifier = Modifier.fillMaxWidth().clip(InlineAttachmentShape).background(Color.Black)) {
        Box(modifier = Modifier.fillMaxWidth().aspectRatio(ratio).clip(InlineAttachmentShape).background(Color.Black),
            contentAlignment = Alignment.Center) {
            val previewBitmap = preview?.bitmap
            if (player == null && previewBitmap != null) {
                Image(bitmap = previewBitmap.asImageBitmap(), contentDescription = attachment.name, modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit)
            }
            if (player != null) {
                AndroidView(factory = {
                        textureView
                    }, modifier = Modifier.fillMaxSize())
            }
            if (player != null && buffering && !playbackError) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(34.dp))
            }
            if (playbackError) {
                Surface(color = Color.Black.copy(alpha = 0.72f), shape = RoundedCornerShape(14.dp)) {
                    Text(text = stringResource(R.string.video_playback_failed), modifier = Modifier.padding(horizontal = 14.dp,
                            vertical = 9.dp), color = Color.White, fontSize = 12.sp)
                }
            } else if (player == null) {
                Surface(modifier = Modifier.align(Alignment.Center).clickable {
                            UiSoundPlayer.playAction(context = context, action = UiActionSound.PlayPause)
                            playbackError = false
                            prepared = false
                            buffering = true
                            playing = false
                            duration = 0
                            position = 0
                            player = ExoPlayer.Builder(context).build()
                        }, shape = CircleShape, color = Color.Black.copy(alpha = 0.62f)) {
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = stringResource(R.string.play), tint = Color.White,
                        modifier = Modifier.padding(10.dp).size(26.dp))
                }
            }
            val previewDuration = preview?.durationMillis ?: 0L
            val shownDuration = if (duration > 0) duration.toLong() else previewDuration
            if (shownDuration > 0L) {
                Surface(modifier = Modifier.align(Alignment.BottomEnd).padding(10.dp), shape = RoundedCornerShape(8.dp),
                    color = Color.Black.copy(alpha = 0.68f)) {
                    Text(text = formatInlineDuration(shownDuration), modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp),
                        color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
        if (player != null && !playbackError) {
            Surface(color = Color.Black, contentColor = Color.White) {
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp)) {
                    Slider(value = position.coerceIn(0, duration.coerceAtLeast(1)).toFloat(), onValueChange = { newPosition ->
                            UiSoundPlayer.playActionThrottled(context = context, action = UiActionSound.Navigation, minimumIntervalMs = 60L
                            )
                            val currentPlayer = player ?: return@Slider
                            val target = newPosition.toLong()
                            position = target.toInt()
                            if (prepared) {
                                try {
                                    currentPlayer.seekTo(target)
                                } catch (_: Exception) {
                                }
                            }
                        }, valueRange = 0f..duration.coerceAtLeast(1).toFloat(), enabled = prepared)
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Surface(modifier = Modifier.clickable(enabled = prepared) {
                                    UiSoundPlayer.playAction(context = context, action = UiActionSound.PlayPause)
                                    val currentPlayer = player?: return@clickable
                                    try {
                                        if (currentPlayer.isPlaying) {
                                            currentPlayer.pause()
                                        } else {
                                            if (duration > 0 && position >= duration - 250) {
                                                currentPlayer.seekTo(0L)
                                                position = 0
                                            }
                                            InlinePlaybackCoordinator.activate(currentPlayer)
                                            currentPlayer.play()
                                        }
                                    } catch (_: Exception) {
                                    }
                                }, color = Color.Transparent) {
                            Icon(imageVector = if (playing) {
                                        Icons.Default.Pause
                                    } else {
                                        Icons.Default.PlayArrow
                                    }, contentDescription = if (playing) {
                                        stringResource(R.string.pause)
                                    } else {
                                        stringResource(R.string.play)
                                    }, tint = Color.White, modifier = Modifier.padding(6.dp).size(28.dp))
                        }
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(text = "${formatInlineDuration(position.toLong())} / " + formatInlineDuration(duration.toLong()),
                            color = Color.White, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun InlineAudioAttachment(attachment: Attachment, fontFamily: FontFamily) {
    val context = LocalContext.current
    val uri = remember(attachment.uri) {
        Uri.parse(attachment.uri)
    }
    var player by remember(uri) {
        mutableStateOf<ExoPlayer?>(null)
    }
    var prepared by remember(uri) {
        mutableStateOf(false)
    }
    var buffering by remember(uri) {
        mutableStateOf(false)
    }
    var playing by remember(uri) {
        mutableStateOf(false)
    }
    var playbackError by remember(uri) {
        mutableStateOf(false)
    }
    var duration by remember(uri) {
        mutableIntStateOf(0)
    }
    var position by remember(uri) {
        mutableIntStateOf(0)
    }
    DisposableEffect(player, uri) {
        val currentPlayer = player
        if (currentPlayer == null) {
            onDispose {
            }
        } else {
            val listener = object : Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        when (playbackState) {
                            Player.STATE_BUFFERING -> {
                                buffering = true
                            }
                            Player.STATE_READY -> {
                                buffering = false
                                prepared = true
                                duration = currentPlayer.duration.coerceAtLeast(0L).coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
                            }
                            Player.STATE_ENDED -> {
                                buffering = false
                                prepared = true
                                playing = false
                                if (duration > 0) {
                                    position = duration
                                }
                            }
                            Player.STATE_IDLE -> {
                                buffering = false
                            }
                        }
                    }
                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        playing = isPlaying
                    }
                    override fun onPlayerError(error: PlaybackException) {
                        playbackError = true
                        buffering = false
                        prepared = false
                        playing = false
                    }
                }
            currentPlayer.addListener(listener)
            try {
                currentPlayer.setMediaItem(MediaItem.fromUri(uri))
                currentPlayer.playWhenReady = true
                currentPlayer.prepare()
                InlinePlaybackCoordinator.activate(currentPlayer)
            } catch (_: Exception) {
                playbackError = true
            }
            onDispose {
                try {
                    currentPlayer.removeListener(listener)
                } catch (_: Exception) {
                }
                InlinePlaybackCoordinator.clear(currentPlayer)
                try {
                    currentPlayer.release()
                } catch (_: Exception) {
                }
            }
        }
    }
    LaunchedEffect(player, prepared) {
        while (player != null) {
            val currentPlayer = player
            if (currentPlayer != null && prepared) {
                position = currentPlayer.currentPosition.coerceAtLeast(0L).coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
                val currentDuration = currentPlayer.duration.coerceAtLeast(0L).coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
                if (currentDuration > 0) {
                    duration = currentDuration
                }
            }
            delay(300)
        }
    }
    Surface(modifier = Modifier.fillMaxWidth().clip(InlineAttachmentShape), shape = InlineAttachmentShape,
        color = MaterialTheme.colorScheme.surfaceContainerLow, tonalElevation = 0.dp, shadowElevation = 0.dp) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(modifier = Modifier.size(54.dp).clickable(enabled = !playbackError) {
                            UiSoundPlayer.playAction(context = context, action = UiActionSound.PlayPause)
                            val currentPlayer = player
                            if (currentPlayer == null) {
                                playbackError = false
                                prepared = false
                                buffering = true
                                playing = false
                                duration = 0
                                position = 0
                                player = ExoPlayer.Builder(context).build()
                            } else if (prepared) {
                                try {
                                    if (currentPlayer.isPlaying) {
                                        currentPlayer.pause()
                                    } else {
                                        if (duration > 0 && position >= duration - 250) {
                                            currentPlayer.seekTo(0L)
                                            position = 0
                                        }
                                        InlinePlaybackCoordinator.activate(currentPlayer)
                                        currentPlayer.play()
                                    }
                                } catch (_: Exception) {
                                }
                            }
                        }, shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer) {
                    Box(contentAlignment = Alignment.Center) {
                        if (player != null && buffering && !playbackError) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                        } else {
                            Icon(imageVector = if (playing) {
                                        Icons.Default.Pause
                                    } else {
                                        Icons.Default.PlayArrow
                                    }, contentDescription = if (playing) {
                                        "Pausar audio"
                                    } else {
                                        "Reproducir audio"
                                    }, modifier = Modifier.size(30.dp), tint = MaterialTheme.colorScheme.onPrimaryContainer)
                        }
                    }
                }
                Spacer(modifier = Modifier.size(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = if (attachment.type == "voice") {
                                    Icons.Default.Mic
                                } else {
                                    Icons.Default.MusicNote
                                }, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.size(7.dp))
                        Text(text = attachment.name?.takeIf { it.isNotBlank() }?: if (attachment.type == "voice") {
                                        stringResource(R.string.voice_note)
                                    } else {
                                        stringResource(R.string.audio)
                                    }, fontFamily = fontFamily, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, maxLines = 1,
                            overflow = TextOverflow.Ellipsis)
                    }
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(text = if (playbackError) {
                                stringResource(R.string.audio_playback_failed)
                            } else {
                                "${formatInlineDuration(position.toLong())} / " + formatInlineDuration(duration.toLong())
                            }, fontFamily = fontFamily, fontSize = 11.sp, color = if (playbackError) {
                                MaterialTheme.colorScheme.error
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            })
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Slider(value = position.coerceIn(0, duration.coerceAtLeast(1)).toFloat(), onValueChange = { newPosition ->
                    UiSoundPlayer.playActionThrottled(context = context, action = UiActionSound.Navigation, minimumIntervalMs = 60L)
                    val currentPlayer = player ?: return@Slider
                    val target = newPosition.toLong()
                    position = target.toInt()
                    if (prepared) {
                        try {
                            currentPlayer.seekTo(target)
                        } catch (_: Exception) {
                        }
                    }
                }, valueRange = 0f..duration.coerceAtLeast(1).toFloat(), enabled = prepared && !playbackError)
        }
    }
}

@Composable
private fun InlinePdfAttachment(attachment: Attachment, previewDelayMillis: Long, performanceMode: String) {
    val context = LocalContext.current
    val uri = remember(attachment.uri) { Uri.parse(attachment.uri) }
    val preview by produceState<android.graphics.Bitmap?>(initialValue = null, key1 = attachment.uri, key2 = performanceMode) {
        if (previewDelayMillis > 0L) {
            delay(previewDelayMillis)
        }
        value = AttachmentPreviewCache.withPreviewPermit {
            AttachmentPreviewCache.loadPdfFirstPage(context = context, uri = uri, performanceMode = performanceMode)
        }
    }
    val bitmap = preview
    if (bitmap != null) {
        val ratio = if (bitmap.height > 0) {
                bitmap.width.toFloat() / bitmap.height.toFloat()
            } else {
                0.72f
            }
        Image(bitmap = bitmap.asImageBitmap(), contentDescription = attachment.name, modifier = Modifier.fillMaxWidth().aspectRatio(
                    ratio.coerceIn(0.5f, 1.5f)).clip(InlineAttachmentShape).clickable {
                    UiSoundPlayer.playAction(context = context, action = UiActionSound.Open)
                    openAttachment(context = context, attachment = attachment)
                }, contentScale = ContentScale.Fit)
    } else {
        InlineFileAttachment(attachment = attachment, extension = "pdf", fontFamily = FontFamily.Default)
    }
}

@Composable
private fun InlineFileAttachment(attachment: Attachment, extension: String, fontFamily: FontFamily) {
    val context = LocalContext.current
    val typeLabel = extension.takeIf { it.isNotBlank() }?.uppercase()?: stringResource(R.string.file)
    Surface(modifier = Modifier.fillMaxWidth().clip(InlineAttachmentShape).clickable {
                UiSoundPlayer.playAction(context = context, action = UiActionSound.Open)
                openAttachment(context = context, attachment = attachment)
            }, shape = InlineAttachmentShape, color = MaterialTheme.colorScheme.surfaceContainerLow, tonalElevation = 0.dp,
        shadowElevation = 0.dp) {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 18.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(shape = RoundedCornerShape(18.dp), color = MaterialTheme.colorScheme.primaryContainer) {
                Box(modifier = Modifier.size(58.dp), contentAlignment = Alignment.Center) {
                    Icon(imageVector = Icons.Default.Description, contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(30.dp))
                }
            }
            Spacer(modifier = Modifier.size(14.dp))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = attachment.name ?: stringResource(R.string.file), fontFamily = fontFamily, fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text(text = "$typeLabel · ${stringResource(R.string.tap_to_open)}", fontFamily = fontFamily, fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

private fun formatInlineDuration(durationMillis: Long): String {
    val totalSeconds = durationMillis / 1000L
    val minutes = totalSeconds / 60L
    val seconds = totalSeconds % 60L
    return "%d:%02d".format(minutes, seconds)
}

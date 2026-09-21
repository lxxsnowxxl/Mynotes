package com.example.mynotes.ui

import android.app.Activity
import android.app.ActivityManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color as AndroidColor
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.text.Html
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.FileProvider
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import coil3.compose.AsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.request.maxBitmapSize
import coil3.size.Precision
import coil3.size.Scale
import coil3.size.Size
import com.example.mynotes.R
import com.example.mynotes.ui.components.ScrollPositionCapsule
import com.example.mynotes.performance.DisplayPerformanceController
import com.example.mynotes.ui.sound.UiActionSound
import com.example.mynotes.ui.sound.UiSoundPlayer
import com.example.mynotes.ui.theme.MyNotesTheme
import com.example.mynotes.viewmodel.SettingsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.Closeable
import java.io.File
import java.io.InputStream
import java.util.Locale
import java.util.zip.ZipInputStream
import kotlin.math.roundToInt

private const val EXTRA_URI = "attachment_uri"
private const val EXTRA_TYPE = "attachment_type"
private const val EXTRA_NAME = "attachment_name"
private const val EXTRA_MIME = "attachment_mime"
private const val MAX_TEXT_BYTES = 2 * 1024 * 1024

/**
 * Abre un adjunto dentro de MyNotes. Se usa tanto desde la pantalla de
 * detalle como desde el editor, incluso antes de guardar una nota.
 */
fun openAttachmentViewer(context: Context, uri: String, type: String, name: String? = null, mimeType: String? = null) {
    val intent = Intent(context, AttachmentViewerActivity::class.java).putExtra(EXTRA_URI, uri).putExtra(EXTRA_TYPE, type)
            .putExtra(EXTRA_NAME, name).putExtra(EXTRA_MIME, mimeType).addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    if (context !is Activity) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(intent)
}

class AttachmentViewerActivity : ComponentActivity() {
    private fun applyAndroidNavigationBarPolicy() {
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        val isMultiWindow = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && isInMultiWindowMode
        if (isMultiWindow) {
            controller.show(WindowInsetsCompat.Type.navigationBars())
        } else {
            controller.hide(WindowInsetsCompat.Type.navigationBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        /*
         * Android 7.x no dispone de iconos oscuros para la barra de
         * navegación. Si llega a mostrarse (multiventana o gesto), un fondo
         * negro garantiza contraste con los botones claros del sistema.
         */
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            @Suppress("DEPRECATION")
            window.navigationBarColor = android.graphics.Color.BLACK
        }
    }
    private fun applySystemBarAppearance(darkMode: Boolean) {
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        controller.isAppearanceLightStatusBars = !darkMode
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            controller.isAppearanceLightNavigationBars = !darkMode
        }
    }
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            applyAndroidNavigationBarPolicy()
        }
    }
    override fun onResume() {
        super.onResume()
        DisplayPerformanceController.reapplyLastRequest(window)
        applyAndroidNavigationBarPolicy()
    }
    override fun onMultiWindowModeChanged(isInMultiWindowMode: Boolean) {
        super.onMultiWindowModeChanged(isInMultiWindowMode)
        applyAndroidNavigationBarPolicy()
    }
    override fun onDestroy() {
        DisplayPerformanceController.release(window)
        super.onDestroy()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_MyNotes)
        super.onCreate(savedInstanceState)
        /*
         * No forzamos una frecuencia inicial aquí. El perfil guardado se
         * aplicará desde DisplayPerformanceController cuando SettingsViewModel
         * entregue performanceMode.
         */
        enableEdgeToEdge()
        applyAndroidNavigationBarPolicy()
        val uriString = intent.getStringExtra(EXTRA_URI).orEmpty()
        val type = intent.getStringExtra(EXTRA_TYPE).orEmpty()
        val name = intent.getStringExtra(EXTRA_NAME)
        val mimeType = intent.getStringExtra(EXTRA_MIME)
        if (uriString.isBlank()) {
            finish()
            return
        }
        setContent {
            val settingsViewModel: SettingsViewModel = viewModel()
            val settings by settingsViewModel.settings.collectAsStateWithLifecycle()
            LaunchedEffect(settings.performanceMode) {
                DisplayPerformanceController.requestForPerformanceMode(window = window, performanceMode = settings.performanceMode)
            }
            /* Igual que la Activity principal: Básico = tema del teléfono;
             * Avanzado = preferencia manual guardada. */
            val systemDarkTheme = isSystemInDarkTheme()
            val effectiveDarkTheme = if (settings.configurationMode == "advanced") {
                settings.darkMode
            } else {
                systemDarkTheme
            }
            LaunchedEffect(effectiveDarkTheme) {
                applySystemBarAppearance(effectiveDarkTheme)
            }
            MyNotesTheme(darkTheme = effectiveDarkTheme, backgroundColor = settings.backgroundColor,
                backgroundToneIndex = settings.backgroundToneIndex, backgroundIntensity = settings.backgroundIntensity,
                surfacePanelIntensity = settings.surfacePanelIntensity, headerIntensity = settings.headerIntensity,
                textColor = settings.textColor, textOutlineEnabled = settings.textOutlineEnabled, accentColor = settings.accentColor) {
                AttachmentViewerScreen(uriString = uriString, type = type, name = name, explicitMimeType = mimeType, onBack = { finish() })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AttachmentViewerScreen(uriString: String, type: String, name: String?, explicitMimeType: String?, onBack: () -> Unit) {
    val context = LocalContext.current
    val uri = remember(uriString) { Uri.parse(uriString) }
    val mimeType = remember(uriString, explicitMimeType, name) {
        explicitMimeType?: resolveMimeType(context, uri, name)
    }
    val extension = remember(name, uriString) {
        fileExtension(name, uri)
    }
    val title = name?.takeIf { it.isNotBlank() }?: when {
            type == "video" || mimeType.startsWith("video/") -> androidx.compose.ui.res.stringResource(R.string.video)
            type == "image" || mimeType.startsWith("image/") -> androidx.compose.ui.res.stringResource(R.string.image)
            type == "audio" || type == "voice" || mimeType.startsWith("audio/") -> androidx.compose.ui.res.stringResource(R.string.audio)
            extension == "pdf" -> "PDF"
            else -> androidx.compose.ui.res.stringResource(R.string.file)
        }
    Scaffold(modifier = Modifier.fillMaxSize(), containerColor = MaterialTheme.colorScheme.background, topBar = {
            TopAppBar(title = {
                    Text(text = title, maxLines = 1, overflow = TextOverflow.Ellipsis, fontWeight = FontWeight.SemiBold)
                }, navigationIcon = {
                    IconButton(onClick = {
                            UiSoundPlayer.playAction(context = context, action = UiActionSound.Back)
                            onBack()
                        }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = androidx.compose.ui.res.stringResource(R.string.back))
                    }
                }, actions = {
                    IconButton(onClick = {
                            UiSoundPlayer.playAction(context = context, action = UiActionSound.Link)
                            openExternally(context = context, uri = uri, name = name, mimeType = mimeType)
                        }) {
                        Icon(imageVector = Icons.Default.OpenInNew,
                            contentDescription = androidx.compose.ui.res.stringResource(R.string.open_with_other_app))
                    }
                }, colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
                    actionIconContentColor = MaterialTheme.colorScheme.onBackground))
        }) { innerPadding -> Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            when {
                type == "image" || mimeType.startsWith("image/") -> {
                    ImageViewer(uri = uri, name = name)
                }
                type == "video" || mimeType.startsWith("video/") -> {
                    VideoViewer(uri = uri, name = title, mimeType = mimeType)
                }
                type == "audio" || type == "voice" || mimeType.startsWith("audio/") -> {
                    AudioFileViewer(uri = uri, name = title, mimeType = mimeType)
                }
                extension == "pdf" || mimeType == "application/pdf" -> {
                    PdfViewer(uri = uri)
                }
                isTextExtension(extension) || mimeType.startsWith("text/") -> {
                    TextFileViewer(uri = uri, extension = extension)
                }
                extension == "docx" || extension == "pptx" || extension == "xlsx" -> {
                    OfficeTextViewer(uri = uri, extension = extension, name = title, mimeType = mimeType)
                }
                else -> {
                    GenericFileViewer(uri = uri, name = title, extension = extension, mimeType = mimeType)
                }
            }
        }
    }
}

@Composable
private fun ImageViewer(uri: Uri, name: String?) {
    val context = LocalContext.current
    val bitmapLimit = remember(context) {
            val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
            when {
                activityManager?.isLowRamDevice == true -> Size(3072, 3072)
                (activityManager?.memoryClass ?: 256) < 192 -> Size(4096, 4096)
                else -> Size.ORIGINAL
            }
        }
    val request = remember(context, uri, bitmapLimit) {
            ImageRequest.Builder(context).data(uri).size(Size.ORIGINAL).maxBitmapSize(bitmapLimit).precision(Precision.EXACT).scale(
                    Scale.FIT).allowHardware(true).memoryCachePolicy(CachePolicy.DISABLED).build()
        }
    var failed by
        remember(uri) {
            mutableStateOf(false)
        }
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (!failed) {
            AsyncImage(model = request, contentDescription = name, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Fit,
                onSuccess = {
                    failed = false
                }, onError = {
                    failed = true
                })
        } else {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement =
                    Arrangement.Center) {
                Icon(imageVector = Icons.Default.Description, contentDescription = null, modifier = Modifier.size(42.dp))
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = androidx.compose.ui.res.stringResource(R.string.image_format_not_supported), color = MaterialTheme.colorScheme
                            .onBackground)
            }
        }
    }
}

@Composable
private fun VideoViewer(uri: Uri, name: String, mimeType: String) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val player = remember(uri) {
            ExoPlayer.Builder(context).build()
        }
    var prepared by
        remember(uri) {
            mutableStateOf(false)
        }
    var buffering by
        remember(uri) {
            mutableStateOf(true)
        }
    var playing by
        remember(uri) {
            mutableStateOf(false)
        }
    var duration by
        remember(uri) {
            mutableIntStateOf(0)
        }
    var position by
        remember(uri) {
            mutableIntStateOf(0)
        }
    var error by
        remember(uri) {
            mutableStateOf(false)
        }
    var fallbackAttempted by
        remember(uri) {
            mutableStateOf(false)
        }
    DisposableEffect(player, uri) {
        val listener = object :
                Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    when (playbackState) {
                        Player.STATE_BUFFERING -> {
                            buffering = true
                        }
                        Player.STATE_READY -> {
                            buffering = false
                            prepared = true
                            error = false
                            duration = player.duration.coerceAtLeast(0L).coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
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
                override fun onPlayerError(playbackException:
                    PlaybackException) {
                    prepared = false
                    playing = false
                    buffering = false
                    /*
                     * ExoPlayer maneja content:// de forma nativa. El fallback
                     * solo se usa con proveedores defectuosos de Android 7/8
                     * que no permiten reabrir correctamente el descriptor.
                     */
                    if (!fallbackAttempted && uri.scheme != "file") {
                        fallbackAttempted = true
                        scope.launch {
                            val fallbackFile = withContext(Dispatchers.IO) {
                                    copyVideoToPlaybackCache(context = context, uri = uri)
                                }
                            if (fallbackFile != null) {
                                try {
                                    error = false
                                    buffering = true
                                    player.stop()
                                    player.clearMediaItems()
                                    player.setMediaItem(MediaItem.fromUri(Uri.fromFile(fallbackFile)))
                                    player.prepare()
                                    player.play()
                                } catch (_: Exception) {
                                    error = true
                                    buffering = false
                                }
                            } else {
                                error = true
                            }
                        }
                    } else {
                        error = true
                    }
                }
            }
        player.addListener(listener)
        try {
            player.setMediaItem(MediaItem.fromUri(uri))
            player.playWhenReady = true
            player.prepare()
        } catch (_: Exception) {
            buffering = false
            error = true
        }
        onDispose {
            try {
                player.removeListener(listener)
            } catch (_: Exception) {
            }
            try {
                player.release()
            } catch (_: Exception) {
            }
        }
    }
    LaunchedEffect(player, prepared) {
        while (true) {
            if (prepared) {
                position = player.currentPosition.coerceAtLeast(0L).coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
                val currentDuration = player.duration.coerceAtLeast(0L).coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
                if (currentDuration > 0) {
                    duration = currentDuration
                }
            }
            delay(300)
        }
    }
    Column(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
            AndroidView(factory = {
                    PlayerView(context).apply {
                        useController = false
                        resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                        setShutterBackgroundColor(android.graphics.Color.BLACK)
                        this.player = player
                    }
                }, update = {
                    if (it.player !== player) {
                        it.player = player
                    }
                }, modifier = Modifier.fillMaxSize())
            if (buffering && !error) {
                CircularProgressIndicator(color = Color.White)
            }
            if (error) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement =
                        Arrangement.Center) {
                    Text(text = androidx.compose.ui.res.stringResource(R.string.video_playback_failed_inside), color = Color.White)
                    Spacer(modifier = Modifier.height(14.dp))
                    Button(onClick = {
                            UiSoundPlayer.playAction(context = context, action = UiActionSound.Link)
                            openExternally(context = context, uri = uri, name = name, mimeType = mimeType)
                        }) {
                        Icon(imageVector = Icons.Default.OpenInNew, contentDescription = null)
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(androidx.compose.ui.res.stringResource(R.string.open_with_other_app))
                    }
                }
            }
        }
        Surface(color = Color.Black, contentColor = Color.White) {
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp)) {
                Slider(value = position.coerceIn(0, duration.coerceAtLeast(1)).toFloat(), onValueChange = {
                            newPosition ->
                        UiSoundPlayer.playActionThrottled(context = context, action = UiActionSound.Navigation, minimumIntervalMs = 60L)
                        val target = newPosition.roundToInt()
                        position = target
                        if (prepared) {
                            try {
                                player.seekTo(target.toLong())
                            } catch (_: Exception) {
                            }
                        }
                    }, valueRange = 0f..duration.coerceAtLeast(1).toFloat(), enabled = prepared && !error)
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(enabled = prepared && !error, onClick = {
                            UiSoundPlayer.playAction(context = context, action = UiActionSound.PlayPause)
                            try {
                                if (player.isPlaying) {
                                    player.pause()
                                } else {
                                    if (duration > 0 && position >= duration - 250) {
                                        player.seekTo(0L)
                                        position = 0
                                    }
                                    player.play()
                                }
                            } catch (_: Exception) {
                            }
                        }) {
                        Icon(imageVector = if (playing) {
                                    Icons.Default.Pause
                                } else {
                                    Icons.Default.PlayArrow
                                }, contentDescription = if (playing) {
                                    androidx.compose.ui.res.stringResource(R.string.pause)
                                } else {
                                    androidx.compose.ui.res.stringResource(R.string.play)
                                }, tint = Color.White)
                    }
                    Text(text = "${formatTime(position)} / " + formatTime(duration), color = Color.White, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
private fun AudioFileViewer(uri: Uri, name: String, mimeType: String) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val player = remember(uri) {
            ExoPlayer.Builder(context).build()
        }
    var prepared by
        remember(uri) {
            mutableStateOf(false)
        }
    var buffering by
        remember(uri) {
            mutableStateOf(true)
        }
    var playing by
        remember(uri) {
            mutableStateOf(false)
        }
    var duration by
        remember(uri) {
            mutableIntStateOf(0)
        }
    var position by
        remember(uri) {
            mutableIntStateOf(0)
        }
    var error by
        remember(uri) {
            mutableStateOf(false)
        }
    var fallbackAttempted by
        remember(uri) {
            mutableStateOf(false)
        }
    DisposableEffect(player, uri) {
        val listener = object :
                Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    when (playbackState) {
                        Player.STATE_BUFFERING -> {
                            buffering = true
                        }
                        Player.STATE_READY -> {
                            buffering = false
                            prepared = true
                            error = false
                            duration = player.duration.coerceAtLeast(0L).coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
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
                override fun onPlayerError(playbackException:
                    PlaybackException) {
                    prepared = false
                    playing = false
                    buffering = false
                    if (!fallbackAttempted && uri.scheme != "file") {
                        fallbackAttempted = true
                        scope.launch {
                            val fallbackFile = withContext(Dispatchers.IO) {
                                    copyAudioToPlaybackCache(context = context, uri = uri)
                                }
                            if (fallbackFile != null) {
                                try {
                                    error = false
                                    buffering = true
                                    player.stop()
                                    player.clearMediaItems()
                                    player.setMediaItem(MediaItem.fromUri(Uri.fromFile(fallbackFile)))
                                    player.prepare()
                                    player.play()
                                } catch (_: Exception) {
                                    error = true
                                    buffering = false
                                }
                            } else {
                                error = true
                            }
                        }
                    } else {
                        error = true
                    }
                }
            }
        player.addListener(listener)
        try {
            player.setMediaItem(MediaItem.fromUri(uri))
            player.prepare()
        } catch (_: Exception) {
            buffering = false
            error = true
        }
        onDispose {
            try {
                player.removeListener(listener)
            } catch (_: Exception) {
            }
            try {
                player.release()
            } catch (_: Exception) {
            }
        }
    }
    LaunchedEffect(player, prepared) {
        while (true) {
            if (prepared) {
                position = player.currentPosition.coerceAtLeast(0L).coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
                val currentDuration = player.duration.coerceAtLeast(0L).coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
                if (currentDuration > 0) {
                    duration = currentDuration
                }
            }
            delay(300)
        }
    }
    Column(modifier = Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement =
            Arrangement.Center) {
        Surface(modifier = Modifier.size(112.dp), shape = RoundedCornerShape(28.dp), color = MaterialTheme.colorScheme.primaryContainer) {
            Box(contentAlignment = Alignment.Center) {
                if (buffering && !error) {
                    CircularProgressIndicator(modifier = Modifier.size(36.dp))
                } else {
                    Icon(imageVector = if (playing) {
                                Icons.Default.Pause
                            } else {
                                Icons.Default.PlayArrow
                            }, contentDescription = if (playing) {
                                androidx.compose.ui.res.stringResource(R.string.pause)
                            } else {
                                androidx.compose.ui.res.stringResource(R.string.play)
                            }, modifier = Modifier.size(56.dp), tint = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(text = name, fontWeight = FontWeight.Bold, fontSize = 20.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = mimeType.ifBlank {
                        androidx.compose.ui.res.stringResource(R.string.audio)
                    }, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(18.dp))
        Slider(value = position.coerceIn(0, duration.coerceAtLeast(1)).toFloat(), onValueChange = {
                newPosition ->
                UiSoundPlayer.playActionThrottled(context = context, action = UiActionSound.Navigation, minimumIntervalMs = 60L)
                val target = newPosition.roundToInt()
                position = target
                if (prepared) {
                    try {
                        player.seekTo(target.toLong())
                    } catch (_: Exception) {
                    }
                }
            }, valueRange = 0f..duration.coerceAtLeast(1).toFloat(), enabled = prepared && !error, modifier = Modifier.fillMaxWidth())
        Text(text = "${formatTime(position)} / " + formatTime(duration), fontSize = 13.sp, color = MaterialTheme.colorScheme
                    .onSurfaceVariant)
        Spacer(modifier = Modifier.height(16.dp))
        Button(enabled = prepared && !error, onClick = {
                UiSoundPlayer.playAction(context = context, action = UiActionSound.PlayPause)
                try {
                    if (player.isPlaying) {
                        player.pause()
                    } else {
                        if (duration > 0 && position >= duration - 250) {
                            player.seekTo(0L)
                            position = 0
                        }
                        player.play()
                    }
                } catch (_: Exception) {
                }
            }) {
            Icon(imageVector = if (playing) {
                        Icons.Default.Pause
                    } else {
                        Icons.Default.PlayArrow
                    }, contentDescription = null)
            Spacer(modifier = Modifier.size(8.dp))
            Text(if (playing) {
                    androidx.compose.ui.res.stringResource(R.string.pause)
                } else {
                    androidx.compose.ui.res.stringResource(R.string.play)
                })
        }
        if (error) {
            Spacer(modifier = Modifier.height(14.dp))
            Text(text = androidx.compose.ui.res.stringResource(R.string.audio_playback_failed_inside), color = MaterialTheme.colorScheme
                        .error)
            Spacer(modifier = Modifier.height(10.dp))
            Button(onClick = {
                    UiSoundPlayer.playAction(context = context, action = UiActionSound.Link)
                    openExternally(context = context, uri = uri, name = name, mimeType = mimeType)
                }) {
                Icon(imageVector = Icons.Default.OpenInNew, contentDescription = null)
                Spacer(modifier = Modifier.size(8.dp))
                Text(androidx.compose.ui.res.stringResource(R.string.open_with_other_app))
            }
        }
    }
}

@Composable
private fun PdfViewer(uri: Uri) {
    val context = LocalContext.current
    val density = LocalDensity.current
    val pdfScrollState = rememberLazyListState()
    val handle = remember(uri) {
        try {
            openPdfHandle(context, uri)
        } catch (_: Exception) {
            null
        }
    }
    DisposableEffect(handle) {
        onDispose {
            try {
                handle?.close()
            } catch (_: Exception) {
            }
        }
    }
    if (handle == null) {
        GenericFileViewer(uri = uri, name = androidx.compose.ui.res.stringResource(R.string.pdf_document), extension = "pdf",
            mimeType = "application/pdf")
        return
    }
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val widthPx = with(density) {
            maxWidth.roundToPx().coerceAtLeast(360).coerceAtMost(1080)
        }
        val pages = remember(handle.pageCount) {
            (0 until handle.pageCount).toList()
        }
        LazyColumn(state = pdfScrollState, modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(items = pages, key = { it }) { pageIndex -> PdfPage(handle = handle, pageIndex = pageIndex, targetWidth = widthPx)
            }
        }
        ScrollPositionCapsule(state = pdfScrollState, modifier = Modifier.align(Alignment.CenterEnd))
    }
}

@Composable
private fun PdfPage(handle: PdfHandle, pageIndex: Int, targetWidth: Int) {
    val renderResult by produceState(initialValue = Pair(false, null as Bitmap?), key1 = handle, key2 = pageIndex, key3 = targetWidth) {
        val bitmap = withContext(Dispatchers.IO) {
            try {
                handle.renderPage(pageIndex, targetWidth)
            } catch (_: Exception) {
                null
            }
        }
        value = Pair(true, bitmap)
    }
    val completed = renderResult.first
    val bitmap = renderResult.second
    Surface(modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp), shape = RoundedCornerShape(10.dp), shadowElevation = 1.dp,
        color = Color.White) {
        when {
            !completed -> {
                Box(modifier = Modifier.fillMaxWidth().height(240.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            bitmap == null -> {
                Box(modifier = Modifier.fillMaxWidth().height(180.dp).padding(18.dp), contentAlignment = Alignment.Center) {
                    Text(text = androidx.compose.ui.res.stringResource(R.string.pdf_page_load_failed, pageIndex + 1),
                        color = Color.DarkGray, fontSize = 14.sp)
                }
            }
            else -> {
                Image(bitmap = bitmap.asImageBitmap(),
                    contentDescription = androidx.compose.ui.res.stringResource(R.string.pdf_page_description, pageIndex + 1),
                    modifier = Modifier.fillMaxWidth(), contentScale = ContentScale.FillWidth)
            }
        }
    }
}

private class PdfHandle(private val descriptor: ParcelFileDescriptor, private val renderer: PdfRenderer) : Closeable {
    val pageCount: Int
        get() = renderer.pageCount
    @Synchronized
    fun renderPage(pageIndex: Int, targetWidth: Int): Bitmap {
        renderer.openPage(pageIndex).use { page -> val safeWidth = targetWidth.coerceAtLeast(1)
            val scale = safeWidth.toFloat() / page.width.toFloat()
            val targetHeight = (page.height * scale).roundToInt().coerceAtLeast(1)
            val bitmap = Bitmap.createBitmap(safeWidth, targetHeight, Bitmap.Config.ARGB_8888)
            Canvas(bitmap).drawColor(AndroidColor.WHITE)
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            return bitmap
        }
    }
    override fun close() {
        renderer.close()
        descriptor.close()
    }
}

private fun openPdfHandle(context: Context, uri: Uri): PdfHandle {
    fun createHandle(descriptor: ParcelFileDescriptor): PdfHandle {
        return try {
            PdfHandle(descriptor = descriptor, renderer = PdfRenderer(descriptor))
        } catch (error: Exception) {
            try {
                descriptor.close()
            } catch (_: Exception) {
            }
            throw error
        }
    }
    if (uri.scheme == "file") {
        val path = uri.path?: throw IllegalArgumentException("Ruta PDF inválida")
        return createHandle(ParcelFileDescriptor.open(File(path), ParcelFileDescriptor.MODE_READ_ONLY))
    }
    /*
     * Muchos DocumentsProvider permiten abrir el PDF directamente. Algunos
     * proveedores de Android 8/9 entregan un descriptor que PdfRenderer no
     * puede buscar (seek). Primero intentamos el descriptor original y, si
     * falla, copiamos el documento al cache privado para obtener un archivo
     * local y completamente seekable.
     */
    try {
        val directDescriptor = context.contentResolver.openFileDescriptor(uri, "r")
        if (directDescriptor != null) {
            return createHandle(directDescriptor)
        }
    } catch (_: Exception) {
        // Continuamos con el fallback local.
    }
    val cachedPdf = copyDocumentToViewerCache(context = context, uri = uri, extension = "pdf", prefix = "pdf")
            ?: throw IllegalArgumentException("No se pudo abrir el PDF")
    return createHandle(ParcelFileDescriptor.open(cachedPdf, ParcelFileDescriptor.MODE_READ_ONLY))
}

@Composable
private fun TextFileViewer(uri: Uri, extension: String) {
    val context = LocalContext.current
    val text by produceState<String?>(initialValue = null, key1 = uri) {
        value = withContext(Dispatchers.IO) {
            readTextPreview(context, uri)
        }
    }
    if (text == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        val textScrollState = rememberScrollState()
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize().verticalScroll(textScrollState).padding(16.dp)) {
                Text(text = text!!, fontFamily = if (extension in setOf("txt", "log", "json", "xml", "csv", "md",
                            "kt", "java", "gradle", "kts", "py", "js", "ts", "css", "html", "htm", "sh", "c", "cpp",
                            "h", "hpp", "ini", "cfg", "yaml", "yml")) {
                        FontFamily.Monospace
                    } else {
                        FontFamily.Default
                    }, fontSize = 14.sp, lineHeight = 20.sp, color = MaterialTheme.colorScheme.onBackground)
            }
            ScrollPositionCapsule(state = textScrollState, modifier = Modifier.align(Alignment.CenterEnd))
        }
    }
}

@Composable
private fun OfficeTextViewer(uri: Uri, extension: String, name: String, mimeType: String) {
    val context = LocalContext.current
    val extracted by produceState<String?>(initialValue = null, key1 = uri, key2 = extension) {
        value = withContext(Dispatchers.IO) {
            extractOfficeText(context = context, uri = uri, extension = extension)
        }
    }
    if (extracted == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }
    if (extracted!!.isBlank()) {
        GenericFileViewer(uri = uri, name = name, extension = extension, mimeType = mimeType)
        return
    }
    val officeScrollState = rememberScrollState()
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().verticalScroll(officeScrollState).padding(18.dp)) {
            Text(text = "Vista de texto extraída del documento", fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary,
                fontSize = 13.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = extracted!!, color = MaterialTheme.colorScheme.onBackground, fontSize = 15.sp, lineHeight = 22.sp)
        }
        ScrollPositionCapsule(state = officeScrollState, modifier = Modifier.align(Alignment.CenterEnd))
    }
}

@Composable
private fun GenericFileViewer(uri: Uri, name: String, extension: String, mimeType: String) {
    val context = LocalContext.current
    val fileSize by produceState<Long?>(initialValue = null, key1 = uri) {
        value = withContext(Dispatchers.IO) {
            resolveFileSize(context, uri)
        }
    }
    Column(modifier = Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {
        Surface(modifier = Modifier.size(120.dp), shape = RoundedCornerShape(28.dp), color = MaterialTheme.colorScheme.primaryContainer) {
            Box(contentAlignment = Alignment.Center) {
                Icon(imageVector = if (extension == "pdf") {
                        Icons.Default.PictureAsPdf
                    } else {
                        Icons.Default.Description
                    }, contentDescription = null, modifier = Modifier.size(58.dp), tint = MaterialTheme.colorScheme.onPrimaryContainer)
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(text = name, fontWeight = FontWeight.Bold, fontSize = 20.sp, maxLines = 3, overflow = TextOverflow.Ellipsis)
        Spacer(modifier = Modifier.height(8.dp))
        val metadata = buildString {
            if (extension.isNotBlank()) {
                append(extension.uppercase(Locale.ROOT))
            }
            if (mimeType.isNotBlank()) {
                if (isNotEmpty()) append(" · ")
                append(mimeType)
            }
            fileSize?.let { size -> if (isNotEmpty()) append(" · ")
                append(formatFileSize(size))
            }
        }
        if (metadata.isNotBlank()) {
            Text(text = metadata, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
        }
        Spacer(modifier = Modifier.height(18.dp))
        Text(text = "Este formato no tiene una vista previa completa integrada. Puedes abrirlo con una aplicación compatible.",
            color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(22.dp))
        Button(onClick = {
                UiSoundPlayer.playAction(context = context, action = UiActionSound.Link)
                openExternally(context = context, uri = uri, name = name, mimeType = mimeType)
            }) {
            Icon(imageVector = Icons.Default.OpenInNew, contentDescription = null)
            Spacer(modifier = Modifier.size(8.dp))
            Text(androidx.compose.ui.res.stringResource(R.string.open_with_other_app))
        }
    }
}

private fun copyDocumentToViewerCache(context: Context, uri: Uri, extension: String, prefix: String): File? {
    return try {
        val directory = File(context.cacheDir, "viewer_documents")
        if (!directory.exists() && !directory.mkdirs()) {
            return null
        }
        val safeExtension = extension.trim().trimStart('.').ifBlank { "bin" }
        val key = uri.toString().hashCode().toUInt().toString(16)
        val file = File(directory, "${prefix}_${key}.${safeExtension}")
        if (file.exists() && file.length() > 0L) {
            return file
        }
        openInputStream(context, uri).use { input -> file.outputStream().buffered(64 * 1024).use { output -> input.copyTo(out = output,
                    bufferSize = 64 * 1024)
            }
        }
        file.takeIf { it.exists() && it.length() > 0L }
    } catch (_: Exception) {
        null
    }
}

private fun copyVideoToPlaybackCache(context: Context, uri: Uri): File? {
    return try {
        val directory = File(context.cacheDir, "viewer_video")
        if (!directory.exists() && !directory.mkdirs()) {
            return null
        }
        val mime = resolveMimeType(context = context, uri = uri, name = null)
        val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mime)?.takeIf { it.isNotBlank() }?: fileExtension(null, uri)
                .takeIf { it.isNotBlank() }?: "mp4"
        val key = uri.toString().hashCode().toUInt().toString(16)
        val file = File(directory, "video_$key.$extension")
        if (file.exists() && file.length() > 0L) {
            return file
        }
        openInputStream(context, uri).use { input -> file.outputStream().buffered(64 * 1024).use { output -> input.copyTo(out = output,
                    bufferSize = 64 * 1024)
            }
        }
        file.takeIf { it.exists() && it.length() > 0L }
    } catch (_: Exception) {
        null
    }
}

private fun copyAudioToPlaybackCache(context: Context, uri: Uri): File? {
    return try {
        val directory = File(context.cacheDir, "viewer_audio")
        if (!directory.exists() && !directory.mkdirs()) {
            return null
        }
        val mime = resolveMimeType(context = context, uri = uri, name = null)
        val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mime)?.takeIf {
                    it.isNotBlank()
                }?: fileExtension(null, uri).takeIf {
                        it.isNotBlank()
                    }?: "m4a"
        val key = uri.toString().hashCode().toUInt().toString(16)
        val file = File(directory, "audio_$key.$extension")
        if (file.exists() && file.length() > 0L) {
            return file
        }
        openInputStream(context, uri).use {
            input ->
            file.outputStream().buffered(64 * 1024).use {
                    output ->
                    input.copyTo(out = output, bufferSize = 64 * 1024)
                }
        }
        file.takeIf {
            it.exists() && it.length() > 0L
        }
    } catch (_: Exception) {
        null
    }
}

private fun readTextPreview(context: Context, uri: Uri): String {
    return try {
        openInputStream(context, uri).use { input -> val bytes = input.readUpTo(MAX_TEXT_BYTES)
            val text = bytes.toString(Charsets.UTF_8)
            if (text.indexOf('\u0000') >= 0) {
                "El archivo parece contener datos binarios y no puede mostrarse como texto."
            } else {
                buildString {
                    append(text)
                    if (bytes.size >= MAX_TEXT_BYTES) {
                        append("\n\n… Vista limitada a los primeros 2 MB del archivo.")
                    }
                }
            }
        }
    } catch (e: Exception) {
        "No se pudo leer el archivo.\n\n${e.message.orEmpty()}"
    }
}

private fun extractOfficeText(context: Context, uri: Uri, extension: String): String {
    return try {
        val fragments = mutableListOf<String>()
        val sharedStrings = mutableListOf<String>()
        val worksheetXml = mutableListOf<String>()
        ZipInputStream(openInputStream(context, uri).buffered()).use { zip -> var entry = zip.nextEntry
            while (entry != null) {
                val entryName = entry.name
                val wanted = when (extension) {
                    "docx" -> entryName == "word/document.xml"
                    "pptx" -> entryName.startsWith("ppt/slides/slide") && entryName.endsWith(".xml")
                    "xlsx" -> entryName == "xl/sharedStrings.xml" ||
                        (entryName.startsWith("xl/worksheets/sheet") && entryName.endsWith(".xml"))
                    else -> false
                }
                if (wanted) {
                    val xml = zip.readBytes().toString(Charsets.UTF_8)
                    when {
                        extension == "xlsx" && entryName == "xl/sharedStrings.xml" -> {
                            Regex("<t(?:\\s[^>]*)?>(.*?)</t>", RegexOption.DOT_MATCHES_ALL).findAll(xml).forEach { match ->
                                    sharedStrings += decodeXml(match.groupValues[1])
                                }
                        }
                        extension == "xlsx" -> {
                            worksheetXml += xml
                        }
                        extension == "docx" -> {
                            Regex("<w:t(?:\\s[^>]*)?>(.*?)</w:t>", RegexOption.DOT_MATCHES_ALL).findAll(xml).forEach { match ->
                                    fragments += decodeXml(match.groupValues[1])
                                }
                        }
                        extension == "pptx" -> {
                            Regex("<a:t(?:\\s[^>]*)?>(.*?)</a:t>", RegexOption.DOT_MATCHES_ALL).findAll(xml).forEach { match ->
                                    fragments += decodeXml(match.groupValues[1])
                                }
                            fragments += "\n"
                        }
                    }
                }
                zip.closeEntry()
                entry = zip.nextEntry
            }
        }
        if (extension == "xlsx") {
            worksheetXml.forEachIndexed { sheetIndex, xml -> fragments += "Hoja ${sheetIndex + 1}"
                val cellRegex = Regex("<c([^>]*)>(.*?)</c>", setOf(RegexOption.DOT_MATCHES_ALL))
                cellRegex.findAll(xml).forEach { cellMatch -> val attrs = cellMatch.groupValues[1]
                    val body = cellMatch.groupValues[2]
                    val reference = Regex("r=\"([^\"]+)\"").find(attrs)?.groupValues?.getOrNull(1).orEmpty()
                    val rawValue = Regex("<v>(.*?)</v>", RegexOption.DOT_MATCHES_ALL).find(body)?.groupValues?.getOrNull(1).orEmpty()
                    val isShared = attrs.contains("t=\"s\"")
                    val value = if (isShared) {
                        rawValue.toIntOrNull()?.let { sharedStrings.getOrNull(it) }?: rawValue
                    } else {
                        decodeXml(rawValue)
                    }
                    if (value.isNotBlank()) {
                        fragments += if (reference.isBlank()) value else "$reference: $value"
                    }
                }
                fragments += "\n"
            }
        }
        fragments.joinToString("\n").replace(Regex("\n{3,}"), "\n\n").trim()
    } catch (_: Exception) {
        ""
    }
}

private fun decodeXml(value: String): String {
    return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
        Html.fromHtml(value, Html.FROM_HTML_MODE_LEGACY).toString()
    } else {
        @Suppress("DEPRECATION")
        Html.fromHtml(value).toString()
    }
}

private fun InputStream.readUpTo(maxBytes: Int): ByteArray {
    val output = java.io.ByteArrayOutputStream()
    val buffer = ByteArray(16 * 1024)
    var total = 0
    while (total < maxBytes) {
        val count = read(buffer, 0, minOf(buffer.size, maxBytes - total))
        if (count <= 0) break
        output.write(buffer, 0, count)
        total += count
    }
    return output.toByteArray()
}

private fun openInputStream(context: Context, uri: Uri): InputStream {
    return when (uri.scheme) {
        "file" -> {
            val path = uri.path?: throw IllegalArgumentException("Ruta inválida")
            File(path).inputStream()
        }
        else -> {
            context.contentResolver.openInputStream(uri)?: throw IllegalArgumentException("No se pudo abrir el archivo")
        }
    }
}

private fun openExternally(context: Context, uri: Uri, name: String?, mimeType: String) {
    try {
        val shareableUri = toShareableUri(context, uri)
        val intent = Intent(Intent.ACTION_VIEW).setDataAndType(shareableUri,
                mimeType.ifBlank { resolveMimeType(context, shareableUri, name) }).addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.open_with)))
    } catch (_: ActivityNotFoundException) {
        Toast.makeText(context, context.getString(R.string.no_compatible_app), Toast.LENGTH_SHORT).show()
    } catch (_: Exception) {
        Toast.makeText(context, context.getString(R.string.open_file_failed), Toast.LENGTH_SHORT).show()
    }
}

private fun toShareableUri(context: Context, uri: Uri): Uri {
    if (uri.scheme != "file") {
        return uri
    }
    val path = uri.path ?: return uri
    return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", File(path))
}

private fun resolveMimeType(context: Context, uri: Uri, name: String?): String {
    context.contentResolver.getType(uri)?.let { return it }
    val extension = fileExtension(name, uri)
    return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)?: "*/*"
}

private fun fileExtension(name: String?, uri: Uri): String {
    val byName = name?.substringAfterLast('.', "")?.lowercase(Locale.ROOT).orEmpty()
    if (byName.isNotBlank()) {
        return byName
    }
    return uri.path?.substringAfterLast('.', "")?.lowercase(Locale.ROOT).orEmpty()
}

private fun isTextExtension(extension: String): Boolean {
    return extension in setOf("txt", "log", "json", "xml", "csv", "md", "kt", "java", "gradle", "kts", "py", "js",
        "ts", "css", "html", "htm", "sh", "c", "cpp", "h", "hpp", "ini", "cfg", "yaml", "yml", "sql", "properties", "conf")
}

private fun resolveFileSize(context: Context, uri: Uri): Long? {
    return try {
        when (uri.scheme) {
            "file" -> uri.path?.let { File(it) }?.takeIf { it.exists() }?.length()
            else -> {
                context.contentResolver.openAssetFileDescriptor(uri, "r")?.use { descriptor -> descriptor.length.takeIf { it >= 0L }
                    }
            }
        }
    } catch (_: Exception) {
        null
    }
}

private fun formatFileSize(bytes: Long): String {
    if (bytes < 1024L) return "$bytes B"
    val kb = bytes / 1024.0
    if (kb < 1024.0) return String.format(Locale.ROOT, "%.1f KB", kb)
    val mb = kb / 1024.0
    if (mb < 1024.0) return String.format(Locale.ROOT, "%.1f MB", mb)
    val gb = mb / 1024.0
    return String.format(Locale.ROOT, "%.2f GB", gb)
}

private fun formatTime(milliseconds: Int): String {
    val totalSeconds = (milliseconds.coerceAtLeast(0) / 1000)
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return if (hours > 0) {
        String.format(Locale.ROOT, "%d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format(Locale.ROOT, "%d:%02d", minutes, seconds)
    }
}

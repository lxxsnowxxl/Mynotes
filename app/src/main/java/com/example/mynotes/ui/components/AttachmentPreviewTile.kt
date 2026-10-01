package com.example.mynotes.ui.components

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mynotes.R
import com.example.mynotes.data.Attachment
import com.example.mynotes.performance.AttachmentPreviewCache
import com.example.mynotes.ui.media.formatMinuteSecondDuration
import com.example.mynotes.ui.sound.UiActionSound
import com.example.mynotes.ui.sound.UiSoundPlayer
import com.example.mynotes.ui.openAttachmentViewer
import kotlinx.coroutines.delay

/*
 * Vista reutilizable de adjuntos para NoteDetailScreen.
 *
 * - image: miniatura cacheada según el perfil de rendimiento;
 * - video: thumbnail cacheado + botón play;
 * - audio/voice: carátula cacheada cuando existe;
 * - PDF: primera página cacheada;
 * - otros archivos: tarjeta con icono.
 */
@Composable
fun AttachmentPreviewTile(attachment: Attachment, modifier: Modifier = Modifier, fontFamily: FontFamily = FontFamily.Default,
    showName: Boolean = true, previewDelayMillis: Long = 0L, performanceMode: String = "balanced", tileHeight: Dp = 142.dp,
    openOnTap: Boolean = true) {
    val context = LocalContext.current
    val uri = Uri.parse(attachment.uri)
    val isBorderlessPreview = attachment.type == "image" || attachment.type == "video" || attachment.name?.substringAfterLast(".", "")
                ?.equals("pdf", ignoreCase = true) == true
    Surface(modifier = modifier.height(tileHeight).clip(RoundedCornerShape(24.dp)).let { baseModifier -> if (openOnTap) {
                        baseModifier.clickable(onClick = UiSoundPlayer.actionHandler(context, UiActionSound.Open) {
                            openAttachment(context = context, attachment = attachment)
                        })
                    } else {
                        baseModifier
                    }
                },
        shape = RoundedCornerShape(24.dp),
        color = if (isBorderlessPreview) {
                Color.Transparent
            } else {
                MaterialTheme.colorScheme.surfaceContainerLow
            },
        tonalElevation = 0.dp, shadowElevation = 0.dp) {
        when (attachment.type) {
            "image" -> {
                ImageAttachment(attachment, previewDelayMillis, performanceMode)
            }
            "video" -> {
                VideoAttachment(attachment, uri, previewDelayMillis, performanceMode)
            }
            "audio", "voice" -> {
                AudioAttachment(attachment = attachment, uri = uri, showName = showName, fontFamily = fontFamily, previewDelayMillis =
                        previewDelayMillis, performanceMode = performanceMode)
            }
            else -> {
                FileAttachment(attachment = attachment, uri = uri, showName = showName, fontFamily = fontFamily, previewDelayMillis =
                        previewDelayMillis, performanceMode = performanceMode)
            }
        }
    }
}

@Composable
internal fun rememberPreviewReady(key: Any?, delayMillis: Long): Boolean =
    produceState(initialValue = delayMillis <= 0L, key1 = key, key2 = delayMillis) {
        if (delayMillis > 0L) delay(delayMillis)
        value = true
    }.value

@Composable
private fun ImageAttachment(attachment: Attachment, previewDelayMillis: Long, performanceMode: String) {
    val previewReady = rememberPreviewReady(attachment.uri, previewDelayMillis)
    Box(modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center) {
        if (previewReady) {
            val context = LocalContext.current
            val preview by
                produceState<android.graphics.Bitmap?>(initialValue = null, key1 = attachment.uri, key2 = performanceMode) {
                    value = AttachmentPreviewCache.withPreviewPermit {
                                AttachmentPreviewCache.loadImagePreview(context = context, uri = Uri.parse(attachment.uri),
                                        performanceMode = performanceMode)
                            }
                }
            if (preview != null) {
                Image(bitmap = preview!!.asImageBitmap(), contentDescription = attachment.name, modifier = Modifier.fillMaxSize().clip(
                                RoundedCornerShape(16.dp)), contentScale = ContentScale.Fit)
            } else {
                Icon(imageVector = Icons.Default.Description, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant
                            .copy(alpha = 0.42f), modifier = Modifier.size(30.dp))
            }
        } else {
            Icon(imageVector = Icons.Default.Description,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.42f),
                modifier = Modifier.size(30.dp))
        }
    }
}

@Composable
private fun VideoAttachment(attachment: Attachment, uri: Uri, previewDelayMillis: Long, performanceMode: String) {
    val context = LocalContext.current
    val preview = rememberDelayedPreview(attachment.uri, performanceMode, previewDelayMillis) {
        AttachmentPreviewCache.loadVideoPreview(context = context, uri = uri, performanceMode = performanceMode)
    }
    Box(modifier = Modifier.fillMaxSize()) {
        val bitmap = preview?.bitmap
        if (bitmap != null) {
            Image(bitmap = bitmap.asImageBitmap(),
                contentDescription = attachment.name,
                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Fit)
        } else {
            AttachmentFallback(Icons.Default.VideoFile, iconSize = 36.dp, padding = 0.dp, tint = LocalContentColor.current)
        }
        Surface(modifier = Modifier.align(Alignment.Center),
            shape = CircleShape,
            color = Color.Black.copy(alpha = 0.62f)) {
            Icon(imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.padding(7.dp).size(22.dp))
        }
        val duration = preview?.durationMillis?: 0L
        if (duration >
            0L) {
            DurationBadge(duration = duration,
                modifier = Modifier.align(Alignment.BottomEnd))
        }
    }
}

@Composable
private fun AudioAttachment(attachment: Attachment, uri: Uri, showName: Boolean, fontFamily: FontFamily, previewDelayMillis: Long,
    performanceMode: String) {
    val context = LocalContext.current
    val preview = rememberDelayedPreview(attachment.uri, performanceMode, previewDelayMillis) {
        AttachmentPreviewCache.loadAudioPreview(context = context, uri = uri, loadAlbumArt = true, performanceMode = performanceMode)
    }
    val bitmap = preview?.bitmap
    if (bitmap != null) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(bitmap = bitmap.asImageBitmap(),
                contentDescription = attachment.name,
                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Fit)
        }
    } else {
        val voice = attachment.type == "voice"
        AttachmentFallback(
            if (voice) Icons.Default.Mic else Icons.Default.MusicNote,
            if (showName) if (voice) stringResource(R.string.mock_voice_note) else attachment.name ?: stringResource(R.string.mock_audio) else null,
            fontFamily, 34.dp
        )
    }
}

@Composable
private fun FileAttachment(attachment: Attachment, uri: Uri, showName: Boolean, fontFamily: FontFamily, previewDelayMillis: Long,
    performanceMode: String) {
    val context = LocalContext.current
    val extension = attachment.name?.substringAfterLast(".", "")?.lowercase()?: uri.path?.substringAfterLast(".", "")?.lowercase()
                .orEmpty()
    if (extension == "pdf") {
        val preview = rememberDelayedPreview(attachment.uri, performanceMode, previewDelayMillis) {
            AttachmentPreviewCache.loadPdfFirstPage(context = context, uri = uri, performanceMode = performanceMode)
        }
        if (preview != null) {
            Box(modifier = Modifier.fillMaxSize()) {
                Image(bitmap = preview!!.asImageBitmap(),
                    contentDescription = attachment.name,
                    modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Fit)
            }
            return
        }
    }
    AttachmentFallback(Icons.Default.Description, if (showName) attachment.name ?: stringResource(R.string.mock_file) else null,
        fontFamily, 35.dp)
}

@Composable
private fun AttachmentFallback(
    icon: ImageVector, label: String? = null, fontFamily: FontFamily = FontFamily.Default, iconSize: Dp, padding: Dp = 12.dp,
    tint: Color = MaterialTheme.colorScheme.primary
) {
    Column(Modifier.fillMaxSize().padding(padding), horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {
        Icon(icon, null, tint = tint, modifier = Modifier.size(iconSize))
        label?.let {
            Spacer(Modifier.height(7.dp))
            Text(it, fontFamily = fontFamily, fontWeight = FontWeight.SemiBold, fontSize = 11.sp, maxLines = 2,
                overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun <T> rememberDelayedPreview(
    uriKey: String,
    performanceMode: String,
    previewDelayMillis: Long,
    loader: suspend () -> T?
): T? {
    val preview by produceState<T?>(initialValue = null, key1 = uriKey, key2 = performanceMode) {
        if (previewDelayMillis > 0L) delay(previewDelayMillis)
        value = AttachmentPreviewCache.withPreviewPermit { loader() }
    }
    return preview
}

@Composable
private fun DurationBadge(duration: Long, modifier: Modifier = Modifier) {
    Surface(modifier = modifier.padding(7.dp),
        color = Color.Black.copy(alpha = 0.68f),
        shape = RoundedCornerShape(7.dp)) {
        Text(text = formatMinuteSecondDuration(duration),
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
            color = Color.White,
            fontSize = 9.sp,
            fontWeight = FontWeight.Medium)
    }
}

/*
 * Abre file:// internos mediante FileProvider y conserva content://.
 */
fun openAttachment(context: Context, attachment: Attachment) {
    openAttachmentViewer(context = context, uri = attachment.uri, type = attachment.type, name = attachment.name,
        mimeType = resolveMimeType(context = context, uri = Uri.parse(attachment.uri), name = attachment.name))
}

private fun resolveMimeType(context: Context, uri: Uri, name: String?): String {
    context.contentResolver.getType(uri)?.let {
            return it
        }
    val extension = name?.substringAfterLast(".", "")?.lowercase().orEmpty()
    return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)?: "*/*"
}

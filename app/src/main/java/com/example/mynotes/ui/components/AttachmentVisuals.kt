package com.example.mynotes.ui.components

import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.util.LruCache
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.mynotes.data.Attachment
import com.example.mynotes.performance.AttachmentPreviewCache
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.sin

/** Decorative audio glyph. Bar heights do not represent sampled audio. Seeking uses the real player slider. */
@Composable
internal fun AudioWaveformGlyph(modifier: Modifier = Modifier, progress: Float = 0f,
    color: Color = MaterialTheme.colorScheme.primary) {
    Canvas(modifier) {
        val count = (size.width / 6.dp.toPx()).toInt().coerceIn(12, 64)
        val step = size.width / count
        repeat(count) { index ->
            val height = size.height * (.22f + .70f * kotlin.math.abs(sin(index * .70f)))
            val x = step * (index + .5f)
            drawLine(if (index.toFloat() / count < progress.coerceIn(0f, 1f)) color else color.copy(alpha = .35f),
                Offset(x, (size.height - height) / 2), Offset(x, (size.height + height) / 2),
                strokeWidth = 3.dp.toPx(), cap = StrokeCap.Round)
        }
    }
}

@Composable
internal fun AttachmentMetaBadge(text: String, modifier: Modifier = Modifier) {
    Surface(modifier.padding(8.dp), color = Color.Black.copy(alpha = .82f),
        contentColor = Color.White, shape = RoundedCornerShape(8.dp)) {
        Text(text, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelMedium)
    }
}

private val DocumentPageCache = LruCache<String, Int>(64)

/** Metadata only: this does not allocate another PDF bitmap. Descriptors close on every path. */
@Composable
internal fun rememberDocumentPageCount(
    attachment: Attachment, enabled: Boolean = true, scrolling: androidx.compose.runtime.State<Boolean>? = null
): Int? {
    val context = LocalContext.current.applicationContext
    var pages by remember(attachment.uri) { mutableStateOf(DocumentPageCache.get(attachment.uri)) }
    val canLoad = enabled && pages == null && !attachment.uri.startsWith("preview:") && scrolling?.value != true
    LaunchedEffect(attachment.uri, canLoad) {
        if (!canLoad) return@LaunchedEffect
        if (scrolling != null) kotlinx.coroutines.delay(160L)
        pages = AttachmentPreviewCache.withPreviewPermit {
            withContext(Dispatchers.IO) {
                try {
                    context.contentResolver.openFileDescriptor(Uri.parse(attachment.uri), "r")?.use { descriptor ->
                        PdfRenderer(descriptor).use { renderer -> renderer.pageCount.takeIf { it > 0 } }
                    }?.also { DocumentPageCache.put(attachment.uri, it) }
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (_: Exception) { null }
            }
        }
    }
    return pages
}

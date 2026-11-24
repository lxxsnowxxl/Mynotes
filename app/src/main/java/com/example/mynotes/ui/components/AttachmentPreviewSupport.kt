package com.example.mynotes.ui.components

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import com.example.mynotes.data.Attachment
import com.example.mynotes.ui.openAttachmentViewer
import kotlinx.coroutines.delay

/** Soporte compartido por los renderizadores de adjuntos que utiliza la aplicación. */
@Composable
internal fun rememberPreviewReady(key: Any?, delayMillis: Long): Boolean =
    produceState(initialValue = delayMillis <= 0L, key1 = key, key2 = delayMillis) {
        if (delayMillis > 0L) delay(delayMillis)
        value = true
    }.value

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

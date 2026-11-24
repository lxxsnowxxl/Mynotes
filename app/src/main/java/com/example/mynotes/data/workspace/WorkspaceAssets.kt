package com.example.mynotes.data.workspace

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import com.example.mynotes.util.openUriStream
import com.example.mynotes.util.uriDisplayName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

data class DroppedAsset(val uri: Uri, val mimeType: String, val name: String) {
    val attachmentType: String get() = when {
        mimeType.startsWith("image/") -> "image"
        mimeType.startsWith("video/") -> "video"
        mimeType.startsWith("audio/") -> "audio"
        else -> "file"
    }
}
object WorkspaceAssets {
    /** Only staging files owned by this workflow may be removed after a successful private copy. */
    suspend fun release(context: Context, uris: List<Uri>) = withContext(Dispatchers.IO) {
        val roots = listOf(File(context.filesDir, "workspace_assets"), File(context.filesDir, "workspaces/drawing_exports"))
            .map { it.canonicalPath + File.separator }
        uris.filter { it.scheme == "file" }.forEach { uri -> runCatching {
            val file = File(uri.path ?: return@runCatching)
            if (roots.any { file.canonicalPath.startsWith(it) }) file.delete()
        } }
    }
    suspend fun copy(context: Context, uris: List<Uri>): List<DroppedAsset> = withContext(Dispatchers.IO) {
        val directory = File(context.filesDir, "workspace_assets").apply { check(exists() || mkdirs()) }
        val written = mutableListOf<File>()
        try {
            uris.take(20).map { uri ->
                val name = context.uriDisplayName(uri) ?: "attachment"
                val mime = context.contentResolver.getType(uri)
                    ?: MimeTypeMap.getSingleton().getMimeTypeFromExtension(name.substringAfterLast('.', "").lowercase())
                    ?: "application/octet-stream"
                val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mime)?.filter { it.isLetterOrDigit() }?.take(12).orEmpty()
                val file = File(directory, UUID.randomUUID().toString() + if (extension.isEmpty()) "" else ".$extension")
                written += file
                context.openUriStream(uri)?.use { input ->
                    file.outputStream().use { output ->
                        val buffer = ByteArray(64 * 1024)
                        var total = 0L
                        while (true) {
                            val read = input.read(buffer)
                            if (read < 0) break
                            total += read
                            check(total <= 128L * 1024L * 1024L) { "Attachment exceeds the import limit" }
                            output.write(buffer, 0, read)
                        }
                    }
                } ?: error("Attachment cannot be opened")
                DroppedAsset(Uri.fromFile(file), mime, name)
            }
        } catch (error: Exception) {
            written.forEach { it.delete() }
            throw error
        }
    }
}


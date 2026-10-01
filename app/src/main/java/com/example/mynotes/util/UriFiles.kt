package com.example.mynotes.util

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import java.io.File
import java.io.InputStream

fun Context.openUriStream(uri: Uri): InputStream? = runCatching {
    if (uri.scheme == "file") uri.path?.let(::File)?.inputStream() else contentResolver.openInputStream(uri)
}.getOrNull()

fun Context.uriDisplayName(uri: Uri): String? {
    if (uri.scheme == "file") return uri.path?.let(::File)?.name
    return runCatching {
        contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
            val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (index >= 0 && cursor.moveToFirst()) cursor.getString(index) else null
        }
    }.getOrNull()
}

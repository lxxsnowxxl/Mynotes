package com.example.mynotes.util

import java.io.File
import java.io.IOException

/** Atomically renames when possible and falls back to copy+delete on filesystems that cannot rename. */
internal fun File.moveReplacing(destination: File) {
    if (!renameTo(destination)) {
        copyTo(destination, overwrite = true)
        delete()
    }
}

internal fun File.requireDirectory(errorMessage: String): File {
    if (!exists() && !mkdirs()) throw IOException(errorMessage)
    return this
}

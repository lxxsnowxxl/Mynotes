package com.example.mynotes.ui.pdf

import android.util.JsonReader
import android.util.JsonToken
import java.io.File
import org.json.JSONObject

/** Lee el resumen sin construir los objetos de trazos, textos e imágenes de cada página. */
internal fun readPdfProjectSummary(file: File): PdfProjectRepository.Summary {
    val dir = requireNotNull(file.parentFile)
    return runCatching {
        JsonReader(file.bufferedReader()).use { reader ->
            var id = dir.name
            var name = "PDF"
            var modifiedAt: Long? = null
            var pageCount = 1
            reader.beginObject()
            while (reader.hasNext()) {
                when (reader.nextName()) {
                    "id" -> id = reader.nextSummaryString()
                    "name" -> name = reader.nextSummaryString()
                    "modifiedAt" -> {
                        check(reader.peek() == JsonToken.NUMBER)
                        modifiedAt = reader.nextString().toLong()
                    }
                    "pages" -> {
                        reader.beginArray()
                        pageCount = 0
                        while (reader.hasNext()) {
                            reader.skipValue()
                            pageCount++
                        }
                        reader.endArray()
                    }
                    else -> reader.skipValue()
                }
            }
            reader.endObject()
            PdfProjectRepository.Summary(id, name, modifiedAt ?: dir.lastModified(), pageCount)
        }
    }.getOrElse {
        // Conserva las conversiones y la sintaxis admitidas por el lector anterior.
        val json = JSONObject(file.readText())
        PdfProjectRepository.Summary(
            id = json.optString("id", dir.name),
            name = json.optString("name", "PDF"),
            modifiedAt = json.optLong("modifiedAt", dir.lastModified()),
            pageCount = json.optJSONArray("pages")?.length() ?: 1
        )
    }
}

private fun JsonReader.nextSummaryString(): String {
    check(peek() == JsonToken.STRING)
    return nextString()
}

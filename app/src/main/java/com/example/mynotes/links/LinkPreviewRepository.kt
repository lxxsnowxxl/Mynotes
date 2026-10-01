package com.example.mynotes.links

import android.net.Uri
import java.util.Locale

/**
 * Utilidades de enlaces compartidas fuera del renderer de tarjetas.
 *
 * La carga, caché y enriquecimiento de previews activos viven en
 * `ui/components/LinkPreviewCard.kt`. Este objeto conserva únicamente la API
 * realmente consumida por widgets: normalizar una URL escrita o pegada.
 */
object LinkPreviewRepository {
    fun normalizeUrl(raw: String): String? {
        var value = raw.trim()
        if (value.isBlank()) return null

        Regex("https?://[^\\s]+", RegexOption.IGNORE_CASE).find(value)?.value?.let { value = it }
        value = value.trimEnd('.', ',', ';', ':', '!', '?', ')', ']', '}')

        if (!value.startsWith("http://", true) && !value.startsWith("https://", true)) {
            if (value.startsWith("www.", true) || Regex("^[A-Za-z0-9.-]+\\.[A-Za-z]{2,}(/.*)?$").matches(value)) {
                value = "https://$value"
            } else return null
        }

        val uri = runCatching { Uri.parse(value) }.getOrNull() ?: return null
        val scheme = uri.scheme?.lowercase(Locale.ROOT)
        return value.takeIf { (scheme == "http" || scheme == "https") && !uri.host.isNullOrBlank() }
    }
}

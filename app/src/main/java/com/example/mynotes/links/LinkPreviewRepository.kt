package com.example.mynotes.links

import android.content.Context
import android.os.Build
import android.text.Html
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset
import java.security.MessageDigest
import java.util.Locale
import kotlin.math.min

/**
 * Metadatos persistentes para vistas previas de enlaces.
 *
 * La app intenta leer Open Graph / Twitter Cards de la página compartida.
 * La miniatura se descarga una sola vez al almacenamiento privado para que
 * la portada siga disponible aunque la app quede sin conexión.
 */
data class LinkPreviewData(
    val url: String,
    val title: String,
    val description: String = "",
    val siteName: String = "",
    val imageUrl: String = "",
    val localImageUri: String = "",
    val mediaType: String = "",
    val fetchedAt: Long = 0L
) {
    val domain: String
        get() =
            runCatching {
                Uri.parse(url).host
                    ?.removePrefix("www.")
                    .orEmpty()
            }.getOrDefault("")

    val displaySite: String
        get() = siteName.ifBlank { domain }
}

object LinkPreviewRepository {

    private const val CACHE_DIR = "link_previews"
    private const val META_SUFFIX = ".json"
    private const val IMAGE_SUFFIX = ".img"
    private const val CACHE_MAX_AGE_MS = 7L * 24L * 60L * 60L * 1000L
    private const val CONNECT_TIMEOUT_MS = 6_000
    private const val READ_TIMEOUT_MS = 8_000
    private const val MAX_HTML_BYTES = 900 * 1024
    private const val MAX_IMAGE_BYTES = 4 * 1024 * 1024

    private const val USER_AGENT =
        "Mozilla/5.0 (Linux; Android 10) AppleWebKit/537.36 " +
            "(KHTML, like Gecko) Chrome/120.0 Mobile Safari/537.36"

    private val networkGate = Semaphore(permits = 3)

    fun normalizeUrl(raw: String): String? {
        var value = raw.trim()
        if (value.isBlank()) return null

        // Cuando se pega texto completo (por ejemplo desde Compartir),
        // tomamos la primera URL HTTP/HTTPS que encontremos.
        val embedded = Regex("https?://[^\\s]+", RegexOption.IGNORE_CASE)
            .find(value)
            ?.value
        if (embedded != null) {
            value = embedded
        }

        value = value.trimEnd('.', ',', ';', ':', '!', '?', ')', ']', '}')

        if (!value.startsWith("http://", true) &&
            !value.startsWith("https://", true)
        ) {
            if (value.startsWith("www.", true) ||
                Regex("^[A-Za-z0-9.-]+\\.[A-Za-z]{2,}(/.*)?$").matches(value)
            ) {
                value = "https://$value"
            } else {
                return null
            }
        }

        val uri = runCatching { Uri.parse(value) }.getOrNull() ?: return null
        val scheme = uri.scheme?.lowercase(Locale.ROOT)
        if (scheme != "http" && scheme != "https") return null
        if (uri.host.isNullOrBlank()) return null

        return value
    }

    fun invalidate(
        context: Context,
        rawUrl: String
    ) {
        val normalized = normalizeUrl(rawUrl) ?: return
        val directory = cacheDirectory(context.applicationContext)
        val key = sha256(normalized)
        runCatching { File(directory, "$key$META_SUFFIX").delete() }
        runCatching { File(directory, "$key$IMAGE_SUFFIX").delete() }
        runCatching { File(directory, "$key$IMAGE_SUFFIX.tmp").delete() }
    }

    suspend fun load(
        context: Context,
        rawUrl: String,
        forceRefresh: Boolean = false
    ): LinkPreviewData = withContext(Dispatchers.IO) {
        val normalized = normalizeUrl(rawUrl)
            ?: return@withContext fallback(rawUrl)

        val directory = cacheDirectory(context)
        val key = sha256(normalized)
        val metaFile = File(directory, "$key$META_SUFFIX")
        val imageFile = File(directory, "$key$IMAGE_SUFFIX")

        val cached = readCache(metaFile, imageFile)
        if (!forceRefresh && cached != null &&
            System.currentTimeMillis() - cached.fetchedAt <= CACHE_MAX_AGE_MS
        ) {
            return@withContext cached
        }

        val fetched = networkGate.withPermit {
            runCatching {
                fetchPreview(
                    normalized = normalized,
                    imageFile = imageFile
                )
            }.getOrNull()
        }

        if (fetched != null) {
            writeCache(metaFile, fetched)
            return@withContext fetched
        }

        if (cached != null) {
            return@withContext cached
        }

        /* YouTube permite derivar una miniatura incluso cuando la página
         * bloquea la lectura automática de metadatos. */
        val youtubeImage = youtubeThumbnail(normalized)
        if (!youtubeImage.isNullOrBlank()) {
            val localImage = networkGate.withPermit {
                downloadPreviewImage(
                    imageUrl = youtubeImage,
                    destination = imageFile
                )
            }
            val fallback = fallback(normalized).copy(
                imageUrl = youtubeImage,
                localImageUri = localImage
            )
            writeCache(metaFile, fallback)
            return@withContext fallback
        }

        fallback(normalized)
    }

    private fun fetchPreview(
        normalized: String,
        imageFile: File
    ): LinkPreviewData {
        val connection = openConnection(normalized)

        try {
            val finalUrl = connection.url.toString()
            val contentType = connection.contentType.orEmpty()
            val html = if (
                contentType.contains("text/html", ignoreCase = true) ||
                contentType.isBlank()
            ) {
                readHtml(connection)
            } else {
                ""
            }

            val metadata = parseHtml(html, finalUrl)

            val specialImage =
                if (contentType.startsWith("image/", ignoreCase = true)) {
                    finalUrl
                } else {
                    metadata.imageUrl.ifBlank {
                        youtubeThumbnail(finalUrl).orEmpty()
                    }
                }

            val localImage = if (specialImage.isNotBlank()) {
                downloadPreviewImage(
                    imageUrl = specialImage,
                    destination = imageFile
                )
            } else {
                imageFile.delete()
                ""
            }

            val host = Uri.parse(finalUrl).host
                ?.removePrefix("www.")
                .orEmpty()

            val fallbackTitle =
                metadata.title.ifBlank {
                    host.ifBlank { finalUrl }
                }

            return LinkPreviewData(
                url = finalUrl,
                title = fallbackTitle,
                description = metadata.description,
                siteName = metadata.siteName,
                imageUrl = specialImage,
                localImageUri = localImage,
                mediaType = metadata.mediaType,
                fetchedAt = System.currentTimeMillis()
            )

        } finally {
            connection.disconnect()
        }
    }

    private data class ParsedMetadata(
        val title: String,
        val description: String,
        val siteName: String,
        val imageUrl: String,
        val mediaType: String
    )

    private fun parseHtml(
        html: String,
        baseUrl: String
    ): ParsedMetadata {
        if (html.isBlank()) {
            return ParsedMetadata("", "", "", "", "")
        }

        val metaValues = LinkedHashMap<String, String>()

        Regex("<meta\\b[^>]*>", setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))
            .findAll(html)
            .forEach { match ->
                val attributes = parseAttributes(match.value)
                val key = (
                    attributes["property"]
                        ?: attributes["name"]
                        ?: attributes["itemprop"]
                    )
                    ?.trim()
                    ?.lowercase(Locale.ROOT)
                    .orEmpty()
                val content = attributes["content"]
                    ?.let(::decodeHtml)
                    ?.trim()
                    .orEmpty()

                if (key.isNotBlank() && content.isNotBlank() &&
                    key !in metaValues
                ) {
                    metaValues[key] = content
                }
            }

        val titleTag = Regex(
            "<title[^>]*>(.*?)</title>",
            setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL)
        )
            .find(html)
            ?.groupValues
            ?.getOrNull(1)
            ?.let(::stripTags)
            ?.let(::decodeHtml)
            ?.trim()
            .orEmpty()

        val title = firstNotBlank(
            metaValues["og:title"],
            metaValues["twitter:title"],
            metaValues["title"],
            titleTag
        )

        val description = firstNotBlank(
            metaValues["og:description"],
            metaValues["twitter:description"],
            metaValues["description"]
        )

        val siteName = firstNotBlank(
            metaValues["og:site_name"],
            metaValues["application-name"]
        )

        val rawImage = firstNotBlank(
            metaValues["og:image:secure_url"],
            metaValues["og:image:url"],
            metaValues["og:image"],
            metaValues["twitter:image"],
            metaValues["twitter:image:src"],
            metaValues["image"]
        )

        val imageUrl = resolveUrl(baseUrl, rawImage)
            ?: ""

        return ParsedMetadata(
            title = title,
            description = description,
            siteName = siteName,
            imageUrl = imageUrl,
            mediaType = firstNotBlank(
                metaValues["og:type"],
                metaValues["twitter:card"]
            )
        )
    }

    private fun parseAttributes(tag: String): Map<String, String> {
        val output = LinkedHashMap<String, String>()
        val regex = Regex(
            """([A-Za-z_:][-A-Za-z0-9_:.]*)\s*=\s*(?:"([^"]*)"|'([^']*)'|([^\s"'=<>`]+))"""
        )

        regex.findAll(tag).forEach { match ->
            val name = match.groupValues[1].lowercase(Locale.ROOT)
            val value = firstNotBlank(
                match.groupValues.getOrNull(2),
                match.groupValues.getOrNull(3),
                match.groupValues.getOrNull(4)
            )
            if (name.isNotBlank() && value.isNotBlank()) {
                output[name] = value
            }
        }
        return output
    }

    private fun findPageIcon(
        html: String,
        baseUrl: String
    ): String? {
        Regex("<link\\b[^>]*>", setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))
            .findAll(html)
            .forEach { match ->
                val attributes = parseAttributes(match.value)
                val rel = attributes["rel"]?.lowercase(Locale.ROOT).orEmpty()
                val href = attributes["href"].orEmpty()
                if (href.isNotBlank() &&
                    ("icon" in rel || "apple-touch-icon" in rel)
                ) {
                    resolveUrl(baseUrl, decodeHtml(href))?.let { return it }
                }
            }
        return null
    }

    private fun resolveUrl(base: String, value: String): String? {
        if (value.isBlank()) return null
        return runCatching {
            URL(URL(base), value).toString()
        }.getOrNull()
    }

    private fun youtubeThumbnail(url: String): String? {
        val uri = runCatching { Uri.parse(url) }.getOrNull() ?: return null
        val host = uri.host?.lowercase(Locale.ROOT).orEmpty()
        val id = when {
            host == "youtu.be" -> uri.pathSegments.firstOrNull()
            host.endsWith("youtube.com") -> {
                when {
                    uri.path?.startsWith("/shorts/") == true ->
                        uri.pathSegments.getOrNull(1)
                    uri.path?.startsWith("/embed/") == true ->
                        uri.pathSegments.getOrNull(1)
                    else -> uri.getQueryParameter("v")
                }
            }
            else -> null
        }
        return id
            ?.takeIf { it.matches(Regex("[A-Za-z0-9_-]{6,}")) }
            ?.let { "https://i.ytimg.com/vi/$it/hqdefault.jpg" }
    }

    private fun openConnection(url: String): HttpURLConnection {
        val connection = URL(url).openConnection() as HttpURLConnection
        connection.instanceFollowRedirects = true
        connection.connectTimeout = CONNECT_TIMEOUT_MS
        connection.readTimeout = READ_TIMEOUT_MS
        connection.setRequestProperty("User-Agent", USER_AGENT)
        connection.setRequestProperty(
            "Accept",
            "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"
        )
        connection.setRequestProperty("Accept-Language", "es,en;q=0.8")
        connection.connect()
        return connection
    }

    private fun readHtml(connection: HttpURLConnection): String {
        val length = connection.contentLengthLong
        if (length > MAX_HTML_BYTES) {
            // Seguimos leyendo, pero con un límite estricto.
        }

        val charset = charsetFromContentType(connection.contentType)
        val reader = BufferedReader(
            InputStreamReader(
                BufferedInputStream(connection.inputStream),
                charset
            )
        )

        reader.use {
            val output = StringBuilder()
            val buffer = CharArray(8 * 1024)
            var total = 0
            while (true) {
                val read = it.read(buffer)
                if (read <= 0) break
                val allowed = min(read, MAX_HTML_BYTES - total)
                if (allowed <= 0) break
                output.append(buffer, 0, allowed)
                total += allowed
                if (total >= MAX_HTML_BYTES) break
            }
            return output.toString()
        }
    }

    private fun charsetFromContentType(contentType: String?): Charset {
        val charsetName = contentType
            ?.split(';')
            ?.map { it.trim() }
            ?.firstOrNull { it.startsWith("charset=", true) }
            ?.substringAfter('=')
            ?.trim()
            ?.trim('"', '\'')

        return runCatching {
            if (charsetName.isNullOrBlank()) Charsets.UTF_8
            else Charset.forName(charsetName)
        }.getOrDefault(Charsets.UTF_8)
    }

    private fun downloadPreviewImage(
        imageUrl: String,
        destination: File
    ): String {
        val connection = runCatching {
            openConnection(imageUrl)
        }.getOrNull() ?: return ""

        try {
            val contentType = connection.contentType.orEmpty()
            if (!contentType.startsWith("image/", ignoreCase = true)) {
                return ""
            }

            val declared = connection.contentLengthLong
            if (declared > MAX_IMAGE_BYTES) {
                return ""
            }

            val temp = File(destination.parentFile, "${destination.name}.tmp")
            var total = 0

            BufferedInputStream(connection.inputStream).use { input ->
                temp.outputStream().buffered().use { output ->
                    val buffer = ByteArray(16 * 1024)
                    while (true) {
                        val read = input.read(buffer)
                        if (read <= 0) break
                        total += read
                        if (total > MAX_IMAGE_BYTES) {
                            temp.delete()
                            return ""
                        }
                        output.write(buffer, 0, read)
                    }
                }
            }

            if (total <= 0) {
                temp.delete()
                return ""
            }

            if (destination.exists()) destination.delete()
            if (!temp.renameTo(destination)) {
                temp.copyTo(destination, overwrite = true)
                temp.delete()
            }

            return Uri.fromFile(destination).toString()
        } catch (_: Exception) {
            return ""
        } finally {
            connection.disconnect()
        }
    }

    private fun cacheDirectory(context: Context): File =
        File(context.filesDir, CACHE_DIR).apply {
            if (!exists()) mkdirs()
        }

    private fun readCache(
        metaFile: File,
        imageFile: File
    ): LinkPreviewData? {
        if (!metaFile.isFile) return null
        return runCatching {
            val json = JSONObject(metaFile.readText(Charsets.UTF_8))
            val localImage =
                if (imageFile.isFile && imageFile.length() > 0L) {
                    Uri.fromFile(imageFile).toString()
                } else {
                    ""
                }

            LinkPreviewData(
                url = json.getString("url"),
                title = json.optString("title"),
                description = json.optString("description"),
                siteName = json.optString("siteName"),
                imageUrl = json.optString("imageUrl"),
                localImageUri = localImage,
                mediaType = json.optString("mediaType"),
                fetchedAt = json.optLong("fetchedAt", metaFile.lastModified())
            )
        }.getOrNull()
    }

    private fun writeCache(
        metaFile: File,
        preview: LinkPreviewData
    ) {
        runCatching {
            val json = JSONObject().apply {
                put("url", preview.url)
                put("title", preview.title)
                put("description", preview.description)
                put("siteName", preview.siteName)
                put("imageUrl", preview.imageUrl)
                put("mediaType", preview.mediaType)
                put("fetchedAt", preview.fetchedAt)
            }
            metaFile.writeText(json.toString(), Charsets.UTF_8)
        }
    }

    private fun fallback(url: String): LinkPreviewData {
        val normalized = normalizeUrl(url) ?: url.trim()
        val host = runCatching {
            Uri.parse(normalized).host?.removePrefix("www.").orEmpty()
        }.getOrDefault("")
        return LinkPreviewData(
            url = normalized,
            title = host.ifBlank { normalized },
            siteName = host,
            fetchedAt = System.currentTimeMillis()
        )
    }

    private fun decodeHtml(value: String): String {
        if (value.isBlank()) return ""
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(value, Html.FROM_HTML_MODE_LEGACY).toString()
        } else {
            @Suppress("DEPRECATION")
            Html.fromHtml(value).toString()
        }
    }

    private fun stripTags(value: String): String =
        value.replace(Regex("<[^>]+>"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()

    private fun firstNotBlank(vararg values: String?): String =
        values.firstOrNull { !it.isNullOrBlank() }.orEmpty()

    private fun sha256(value: String): String {
        val bytes = MessageDigest.getInstance("SHA-256")
            .digest(value.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }
}

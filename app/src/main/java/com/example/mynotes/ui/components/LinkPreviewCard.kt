package com.example.mynotes.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.text.Html
import android.util.LruCache
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.mynotes.ui.theme.resolveUiTextColor
import com.example.mynotes.ui.theme.resolveSecondaryUiTextColor
import com.example.mynotes.ui.theme.resolveUiGraphicColor
import com.example.mynotes.ui.sound.UiActionSound
import com.example.mynotes.ui.sound.UiSoundPlayer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.sync.withPermit
import java.io.File
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.Charset
import java.security.MessageDigest
import java.util.Locale
import org.json.JSONArray
import org.json.JSONObject

private const val MAX_HTML_CHARS = 1_800_000
private const val PREVIEW_CACHE_VERSION = 6
private const val PREVIEW_CACHE_MAX_AGE_MS = 14L * 24L * 60L * 60L * 1000L
private const val THUMBNAIL_MAX_BYTES = 6L * 1024L * 1024L
private const val THUMBNAIL_CACHE_MAX_BYTES = 80L * 1024L * 1024L
private const val THUMBNAIL_CACHE_MAX_FILES = 120

private val UrlRegex = Regex(
        pattern = """https?://[^\s<>"']+""",
        option = RegexOption.IGNORE_CASE)
/**
 * Extrae URLs HTTP/HTTPS del texto de la nota conservando el orden.
 */
fun extractLinkUrls(text: String): List<String> = UrlRegex.findAll(text).map { match -> match.value.trimEnd('.', ',', ';', '!', ')', ']',
                '}')
        }.filter { it.length > 8 }.distinct().toList()
/**
 * Si el contenido de la nota es únicamente una URL, evita repetir el enlace
 * como texto cuando ya se va a mostrar su tarjeta enriquecida.
 */
fun noteTextForDisplay(content: String, links: List<String>): String {
    val trimmed = content.trim()
    return if (links.size == 1 && trimmed == links.first()) {
        ""
    } else {
        content
    }
}

private data class LinkPreviewData(val url: String, val title: String?, val description: String?, val imageUrl: String?,
    val siteName: String?, val host: String, val imageCandidates: List<String> = emptyList(), val cachedImagePath: String? = null) {
    companion object {
        fun basic(url: String): LinkPreviewData {
            val host = safeHost(url)
            return LinkPreviewData(url = url, title = null, description = null, imageUrl = directImageUrl(url), siteName = host, host = host
            )
        }
    }
}

private object LinkPreviewRepository {
    private val cache = LruCache<String, LinkPreviewData>(120)
    private val locks = mutableMapOf<String, Mutex>()
    private val legacyLoadGate = Semaphore(permits = 1)
    private val loadGate = Semaphore(permits = 2)

    /*
     * Lectura exclusivamente de RAM para composición. No consulta
     * SharedPreferences ni el sistema de archivos, por lo que puede usarse
     * al construir una tarjeta sin introducir I/O síncrono en el hilo UI.
     */
    fun peekMemory(url: String): LinkPreviewData? = cache.get(url)
    private fun lockFor(url: String): Mutex = synchronized(locks) {
            locks.getOrPut(url) { Mutex() }
        }
    fun peek(context: Context, url: String): LinkPreviewData? {
        cache.get(url)?.let { return it }
        return readPersistedPreview(context = context, url = url)?.also { cached -> cache.put(url, cached)
        }
    }
    suspend fun load(context: Context, url: String): LinkPreviewData = withContext(Dispatchers.IO) {
            /*
             * Toda la ruta persistencia/red/miniatura queda limitada. En API
             * 28 o anterior permitimos un único trabajo de link preview a la
             * vez; en Android moderno, dos. Esto incluye tanto precarga como
             * tarjetas visibles y evita ráfagas de descargas/decodificación.
             */
            val gate = if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) legacyLoadGate else loadGate
            gate.withPermit {
                cache.get(url)?.let { cached -> return@withPermit ensureThumbnailCached(context = context, preview = cached).also { ready ->
                        cache.put(url, ready)
                        persistPreview(context, url, ready)
                    }
                }
                lockFor(url).withLock {
                    cache.get(url)?.let { cached -> return@withLock cached
                    }
                    val persisted = readPersistedPreview(context = context, url = url)
                    if (persisted != null) {
                        val ready = ensureThumbnailCached(context = context, preview = persisted)
                        cache.put(url, ready)
                        persistPreview(context, url, ready)
                        return@withLock ready
                    }
                    val fetched = fetch(url)
                    val ready = ensureThumbnailCached(context = context, preview = fetched)
                    cache.put(url, ready)
                    persistPreview(context, url, ready)
                    ready
                }
            }
        }
    private fun fetch(url: String): LinkPreviewData {
        val basic = LinkPreviewData.basic(url)
        /*
         * Varias redes sociales ya no exponen una página HTML sencilla a
         * clientes móviles. Antes de intentar raspar la página usamos sus
         * endpoints públicos de vista previa cuando existen. Si el endpoint
         * no trae imagen seguimos con el HTML normal como segundo intento.
         */
        val socialProviderPreview = when {
                isMetaSocialUrl(url) -> fetchMetaOEmbedPreview(url)
                isRedditRelatedUrl(url) -> fetchRedditJsonPreview(entityUrl = url, clickUrl = url)
                else -> null
            }
        if (socialProviderPreview?.imageUrl != null || socialProviderPreview?.imageCandidates?.isNotEmpty() == true) {
            return socialProviderPreview
        }
        if (isSpotifyRelatedUrl(url)) {
            findSpotifyEntityUrl(url)?.let { entityUrl -> fetchSpotifyOEmbed(entityUrl = entityUrl, clickUrl = url)
                }?.let { spotifyPreview -> return spotifyPreview
                }
        }
        if (isDouyinRelatedUrl(url)) {
            fetchDouyinDirectPreview(url)?.let { preview -> if (preview.imageUrl != null || preview.imageCandidates.isNotEmpty()) {
                    return preview
                }
            }
        }
        if (isTikTokRelatedUrl(url)) {
            fetchTikTokOEmbed(entityUrl = url, clickUrl = url)?.let { preview -> return preview
            }
        }
        if (directImageUrl(url) != null) {
            return basic.copy(title = fileNameFromUrl(url).takeIf { it.isNotBlank() })
        }
        var connection: HttpURLConnection? = null
        return try {
            connection = (URL(url).openConnection() as HttpURLConnection).apply {
                        instanceFollowRedirects = true
                        connectTimeout = 6_000
                        readTimeout = 7_000
                        requestMethod = "GET"
                        setRequestProperty("User-Agent", if (isDouyinRelatedUrl(url)) {
                                douyinUserAgent()
                            } else {
                                previewUserAgent()
                            })
                        setRequestProperty("Accept", "text/html,application/xhtml+xml,image/webp,image/jpeg,image/png,image/*,*/*;q=0.8")
                        setRequestProperty("Accept-Language", "es-MX,es;q=0.9,en;q=0.7")
                    }
            connection.connect()
            val responseCode = connection.responseCode
            if (responseCode !in 200..399) {
                return basic.withKnownProviderFallback()
            }
            val finalUrl = connection.url?.toString() ?: url
            if (isRedditRelatedUrl(url) || isRedditRelatedUrl(finalUrl)) {
                fetchRedditJsonPreview(entityUrl = finalUrl, clickUrl = url)?.let { preview -> if (preview.imageUrl != null ||
                        preview.imageCandidates.isNotEmpty()) {
                        return preview
                    }
                }
            }
            if (isMetaSocialUrl(url) || isMetaSocialUrl(finalUrl)) {
                fetchMetaOEmbedPreview(finalUrl)?.let { preview -> if (preview.imageUrl != null || preview.imageCandidates.isNotEmpty()) {
                        return preview.copy(url = url)
                    }
                }
            }
            if (isTikTokRelatedUrl(url) || isTikTokRelatedUrl(finalUrl)) {
                fetchTikTokOEmbed(entityUrl = finalUrl, clickUrl = url)?.let { preview -> return preview
                }
            }
            val contentType = connection.contentType?.lowercase(Locale.ROOT).orEmpty()
            if (contentType.startsWith("image/")) {
                return LinkPreviewData.basic(finalUrl).copy(url = url, title = fileNameFromUrl(finalUrl).takeIf { it.isNotBlank() },
                    imageUrl = finalUrl)
            }
            if (contentType.isNotBlank() && !contentType.contains("html") && !contentType.contains("xhtml")) {
                return LinkPreviewData.basic(finalUrl).copy(url = url).withKnownProviderFallback()
            }
            val charset = charsetFromContentType(connection.contentType)
            val html = connection.inputStream.use { input -> readLimitedHtml(reader = InputStreamReader(input, charset))
                }
            val parsed = parseHtml(pageUrl = finalUrl, html = html)
            if (isMetaSocialUrl(url) || isMetaSocialUrl(finalUrl) || isRedditRelatedUrl(url) || isRedditRelatedUrl(finalUrl)) {
                val enriched = enrichSocialHtmlPreview(originalUrl = url, pageUrl = finalUrl, html = html, base = parsed,
                        providerPreview = socialProviderPreview)
                if (enriched.imageUrl != null || enriched.imageCandidates.isNotEmpty()) {
                    return enriched
                }
            }
            if (isSpotifyRelatedUrl(url) || isSpotifyRelatedUrl(finalUrl)) {
                val spotifyEntityUrl = findSpotifyEntityUrl(url)?: findSpotifyEntityUrl(finalUrl)?: findSpotifyEntityUrlInText(html)
                if (spotifyEntityUrl != null) {
                    fetchSpotifyOEmbed(entityUrl = spotifyEntityUrl, clickUrl = url)?.let { spotifyPreview -> return spotifyPreview
                    }
                }
                return parsed.copy(url = url, siteName = "Spotify").withKnownProviderFallback()
            }
            if (isDouyinRelatedUrl(url) || isDouyinRelatedUrl(finalUrl)) {
                return enrichDouyinPreview(originalUrl = url, pageUrl = finalUrl, html = html, base = parsed)
            }
            parsed.copy(url = url).withKnownProviderFallback()
        } catch (_: Exception) {
            basic.withKnownProviderFallback()
        } finally {
            connection?.disconnect()
        }
    }
}

/**
 * Precarga metadatos y miniaturas fuera del viewport del grid.
 * Se procesan pocos enlaces a la vez para no competir con el scroll.
 */
suspend fun preloadLinkPreviews(context: Context, urls: List<String>, performanceMode: String = "balanced") {
    val appContext = context.applicationContext
    val maxUrls = when (performanceMode) {
        "performance" -> 12
        "quality" -> 40
        else -> 24
    }
    val parallelism = if (performanceMode == "quality") 2 else 1
    val pauseBetweenBatchesMs = when (performanceMode) {
        "performance" -> 120L
        "quality" -> 40L
        else -> 75L
    }
    urls.asSequence().filter { it.startsWith("http", ignoreCase = true) }.distinct().take(maxUrls).chunked(parallelism).forEach { batch ->
        coroutineScope {
            batch.map { url -> async(Dispatchers.IO) {
                    LinkPreviewRepository.load(context = appContext, url = url)
                }
            }.awaitAll()
        }
        if (pauseBetweenBatchesMs > 0L) delay(pauseBetweenBatchesMs)
    }
}

private fun previewUserAgent(): String = "Mozilla/5.0 (Linux; Android 10) AppleWebKit/537.36 " +
        "(KHTML, like Gecko) Chrome/124.0 Mobile Safari/537.36"
/**
 * Las páginas share de Douyin actualmente entregan _ROUTER_DATA de forma
 * mucho más consistente con un User-Agent de Safari/iPhone.
 */
private fun douyinUserAgent(): String = "Mozilla/5.0 (iPhone; CPU iPhone OS 17_5 like Mac OS X) " +
        "AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.5 " + "Mobile/15E148 Safari/604.1"
private fun previewCachePreferences(context: Context) = context.getSharedPreferences("link_preview_cache_v$PREVIEW_CACHE_VERSION",
        Context.MODE_PRIVATE)
private fun previewCacheKey(url: String): String = "preview_" + sha256(url)

private fun persistPreview(context: Context, originalUrl: String, preview: LinkPreviewData) {
    val json = JSONObject().put("savedAt", System.currentTimeMillis()).put("url", preview.url)
            .put("title", preview.title ?: JSONObject.NULL).put("description", preview.description ?: JSONObject.NULL)
            .put("imageUrl", preview.imageUrl ?: JSONObject.NULL).put("siteName", preview.siteName ?: JSONObject.NULL)
            .put("host", preview.host).put("imageCandidates", JSONArray(preview.imageCandidates))
            .put("cachedImagePath", preview.cachedImagePath ?: JSONObject.NULL)
    previewCachePreferences(context).edit().putString(previewCacheKey(originalUrl), json.toString()).apply()
}

private fun readPersistedPreview(context: Context, url: String): LinkPreviewData? {
    val key = previewCacheKey(url)
    val raw = previewCachePreferences(context).getString(key, null)?: return null
    return try {
        val json = JSONObject(raw)
        val savedAt = json.optLong("savedAt", 0L)
        if (savedAt <= 0L || System.currentTimeMillis() - savedAt > PREVIEW_CACHE_MAX_AGE_MS) {
            previewCachePreferences(context).edit().remove(key).apply()
            return null
        }
        val cachedPath = json.optString("cachedImagePath").takeIf { it.isNotBlank() && it != "null" }?.takeIf { File(it).isFile }
        val imageCandidates = json.optJSONArray("imageCandidates")?.let { array -> buildList {
                        for (index in 0 until array.length()) {
                            array.optString(index).takeIf { it.startsWith("http", ignoreCase = true) }?.let(::add)
                        }
                    }
                }.orEmpty()
        LinkPreviewData(url = json.optString("url", url), title = json.optNullableString("title"),
            description = json.optNullableString("description"), imageUrl = json.optNullableString("imageUrl"),
            siteName = json.optNullableString("siteName"), host = json.optString("host", safeHost(url)), imageCandidates = imageCandidates,
            cachedImagePath = cachedPath)
    } catch (_: Exception) {
        null
    }
}

private fun JSONObject.optNullableString(key: String): String? {
    if (isNull(key)) return null
    return optString(key).takeIf { it.isNotBlank() && it != "null" }
}

private fun ensureThumbnailCached(context: Context, preview: LinkPreviewData): LinkPreviewData {
    preview.cachedImagePath?.let(::File)?.takeIf { it.isFile && it.length() > 0L && looksLikeImageFile(it) }?.let { file ->
            file.setLastModified(System.currentTimeMillis())
            return preview
        }
    val remoteCandidates = buildList {
            preview.imageUrl?.let(::add)
            addAll(preview.imageCandidates)
        }.asSequence().map { it.trim() }.filter {
                it.startsWith("http://", ignoreCase = true) || it.startsWith("https://", ignoreCase = true)
            }.distinct().take(12).toList()
    if (remoteCandidates.isEmpty()) {
        return preview.copy(cachedImagePath = null)
    }
    val cacheDir = File(context.filesDir, "link_preview_thumbnails_v6").apply { mkdirs() }
    remoteCandidates.forEach { remoteUrl -> val destination = File(cacheDir, sha256(remoteUrl) + ".img")
        if (destination.isFile && destination.length() > 0L && looksLikeImageFile(destination)) {
            destination.setLastModified(System.currentTimeMillis())
            return preview.copy(imageUrl = remoteUrl, cachedImagePath = destination.absolutePath)
        }
        destination.takeIf { it.exists() }?.delete()
        val downloaded = downloadThumbnail(imageUrl = remoteUrl, refererUrl = preview.url, destination = destination)
        if (downloaded) {
            pruneThumbnailCache(cacheDir)
            return preview.copy(imageUrl = remoteUrl, cachedImagePath = destination.absolutePath)
        }
    }
    return preview.copy(cachedImagePath = null)
}

private fun downloadThumbnail(imageUrl: String, refererUrl: String, destination: File): Boolean {
    var connection: HttpURLConnection? = null
    val temp = File(destination.absolutePath + ".tmp")
    return try {
        connection = (URL(imageUrl).openConnection() as HttpURLConnection).apply {
                    instanceFollowRedirects = true
                    connectTimeout = 6_000
                    readTimeout = 8_000
                    requestMethod = "GET"
                    setRequestProperty("User-Agent", if (isDouyinRelatedUrl(refererUrl)) {
                            douyinUserAgent()
                        } else {
                            previewUserAgent()
                        })
                    // Android 9 / API 28 no decodifica AVIF de forma fiable.
                    // Pedimos formatos que Coil puede mostrar en ese dispositivo.
                    setRequestProperty("Accept", "image/webp,image/jpeg,image/png,image/*;q=0.9,*/*;q=0.5")
                    setRequestProperty("Referer", providerReferer(refererUrl))
                    setRequestProperty("Accept-Language", "es-MX,es;q=0.9,en;q=0.7")
                }
        connection.connect()
        if (connection.responseCode !in 200..299) {
            return false
        }
        val declaredLength = connection.contentLengthLong
        if (declaredLength > THUMBNAIL_MAX_BYTES) {
            return false
        }
        var total = 0L
        connection.inputStream.use { input -> FileOutputStream(temp).use { output -> val buffer = ByteArray(16_384)
                while (true) {
                    val count = input.read(buffer)
                    if (count <= 0) break
                    total += count
                    if (total > THUMBNAIL_MAX_BYTES) {
                        return false
                    }
                    output.write(buffer, 0, count)
                }
            }
        }
        if (total <= 0L || !looksLikeImageFile(temp)) {
            return false
        }
        if (destination.exists()) {
            destination.delete()
        }
        if (!temp.renameTo(destination)) {
            temp.copyTo(destination, overwrite = true)
            temp.delete()
        }
        destination.setLastModified(System.currentTimeMillis())
        true
    } catch (_: Exception) {
        false
    } finally {
        temp.takeIf { it.exists() }?.delete()
        connection?.disconnect()
    }
}

private fun looksLikeImageFile(file: File): Boolean {
    if (!file.isFile || file.length() < 12L) return false
    return try {
        val header = ByteArray(32)
        val count = file.inputStream().use { it.read(header) }
        if (count < 12) return false
        val jpeg = header[0].toInt() and 0xFF == 0xFF && header[1].toInt() and 0xFF == 0xD8 && header[2].toInt() and 0xFF == 0xFF
        val png = header.copyOfRange(0, 8).contentEquals(byteArrayOf(0x89.toByte(), 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A))
        val gif = String(header, 0, 6, Charsets.US_ASCII) == "GIF87a" || String(header, 0, 6, Charsets.US_ASCII) == "GIF89a"
        val webp = String(header, 0, 4, Charsets.US_ASCII) == "RIFF" && String(header, 8, 4, Charsets.US_ASCII) == "WEBP"
        val avif = count >= 12 && String(header, 4, 4, Charsets.US_ASCII) == "ftyp" && listOf("avif", "avis", "mif1", "heic", "heix")
                    .contains(String(header, 8, 4, Charsets.US_ASCII))
        jpeg || png || gif || webp || (avif && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
    } catch (_: Exception) {
        false
    }
}

private fun providerReferer(url: String): String {
    val host = safeHost(url).lowercase(Locale.ROOT)
    return when {
        host.contains("douyin") || host.contains("iesdouyin") -> "https://www.douyin.com/"
        host.contains("tiktok") -> "https://www.tiktok.com/"
        host.contains("instagram") -> "https://www.instagram.com/"
        host.contains("threads") -> "https://www.threads.com/"
        host.contains("facebook") || host.contains("fb.com") -> "https://www.facebook.com/"
        host.contains("reddit") || host == "redd.it" -> "https://www.reddit.com/"
        else -> url
    }
}

private fun pruneThumbnailCache(directory: File) {
    val files = directory.listFiles()?.filter { it.isFile && !it.name.endsWith(".tmp") }?.sortedByDescending { it.lastModified() }
            .orEmpty()
    var totalBytes = files.sumOf { it.length() }
    files.forEachIndexed { index, file -> if (index >= THUMBNAIL_CACHE_MAX_FILES || totalBytes > THUMBNAIL_CACHE_MAX_BYTES) {
            totalBytes -= file.length()
            file.delete()
        }
    }
}

private fun sha256(value: String): String {
    val digest = MessageDigest.getInstance("SHA-256").digest(value.toByteArray(Charsets.UTF_8))
    return digest.joinToString("") { byte -> "%02x".format(byte)
    }
}

private fun parseHtml(pageUrl: String, html: String): LinkPreviewData {
    val metadata = linkedMapOf<String, String>()
    val metaTagRegex = Regex(
            pattern = """<meta\b[^>]*>""",
            option = RegexOption.IGNORE_CASE)
    val attributeRegex = Regex(
            pattern = """([A-Za-z_:][A-Za-z0-9_:\-.]*)\s*=\s*(?:\"([^\"]*)\"|'([^']*)'|([^\s>]+))"""
        )
    metaTagRegex.findAll(html).forEach { tagMatch -> val attributes = attributeRegex.findAll(tagMatch.value).associate { attr -> val key =
                            attr.groupValues[1].lowercase(Locale.ROOT)
                        val value = attr.groupValues.drop(2).firstOrNull { it.isNotEmpty() }.orEmpty()
                        key to value
                    }
            val key = attributes["property"]?: attributes["name"]
            val content = attributes["content"]
            if (!key.isNullOrBlank() && !content.isNullOrBlank()) {
                metadata.putIfAbsent(key.lowercase(Locale.ROOT), decodeHtml(content))
            }
        }
    val titleFromTag = Regex(
            pattern = """<title\b[^>]*>(.*?)</title>""",
            options = setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL)).find(html)?.groupValues?.getOrNull(1)?.let(::decodeHtml)
            ?.takeIf { it.isNotBlank() }
    val host = safeHost(pageUrl)
    val rawImage = firstNonBlank(metadata["og:image"], metadata["og:image:secure_url"], metadata["twitter:image"],
            metadata["twitter:image:src"])
    val resolvedImage = rawImage?.let { resolveUrl(pageUrl, it) }
    val title = firstNonBlank(metadata["og:title"], metadata["twitter:title"], titleFromTag)?.cleanText()
    val description = firstNonBlank(metadata["og:description"], metadata["twitter:description"], metadata["description"])?.cleanText()
    val siteName = firstNonBlank(metadata["og:site_name"], metadata["application-name"], host)?.cleanText()
    return LinkPreviewData(url = pageUrl, title = title, description = description, imageUrl = resolvedImage, siteName = siteName,
        host = host)
}

/**
 * Douyin suele renderizar la portada dentro de JSON embebido en la página
 * (RENDER_DATA / _ROUTER_DATA) en lugar de publicarla como og:image.
 * Los enlaces v.douyin.com terminan normalmente en una página de
 * iesdouyin.com, por eso primero seguimos la redirección y después buscamos
 * la portada real dentro de esos datos.
 */
private fun isDouyinRelatedUrl(url: String): Boolean {
    val host = try {
            Uri.parse(url).host?.lowercase(Locale.ROOT).orEmpty()
        } catch (_: Exception) {
            ""
        }
    return host == "douyin.com" || host.endsWith(".douyin.com") || host == "iesdouyin.com" || host.endsWith(".iesdouyin.com")
}

private data class ResolvedDouyinLink(val finalUrl: String, val awemeId: String?, val cookieHeader: String?)

/**
 * Resuelve v.douyin.com manualmente para conservar el UA móvil y las cookies
 * de la cadena de redirecciones. Esto evita depender de la página genérica
 * que Douyin entrega a algunos clientes Android.
 */
private fun resolveDouyinLink(url: String): ResolvedDouyinLink? {
    var current = url
    val cookies = linkedMapOf<String, String>()
    repeat(8) {
        var connection: HttpURLConnection? = null
        try {
            connection = (URL(current).openConnection() as HttpURLConnection).apply {
                        instanceFollowRedirects = false
                        connectTimeout = 5_000
                        readTimeout = 6_000
                        requestMethod = "GET"
                        setRequestProperty("User-Agent", douyinUserAgent())
                        setRequestProperty("Accept", "text/html,application/xhtml+xml,*/*;q=0.8")
                        setRequestProperty("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.7")
                        if (cookies.isNotEmpty()) {
                            setRequestProperty("Cookie", cookies.entries.joinToString("; ") {
                                    "${it.key}=${it.value}"
                                })
                        }
                    }
            connection.connect()
            connection.headerFields.filterKeys { key -> key?.equals("Set-Cookie", ignoreCase = true) == true
                }.values.flatten().forEach { rawCookie -> val first = rawCookie.substringBefore(';').trim()
                    val name = first.substringBefore('=', "").trim()
                    val value = first.substringAfter('=', "").trim()
                    if (name.isNotBlank()) {
                        cookies[name] = value
                    }
                }
            val code = connection.responseCode
            if (code in 300..399) {
                val location = connection.getHeaderField("Location")?.takeIf { it.isNotBlank() }?: return null
                current = URL(URL(current), location).toString()
                extractDouyinAwemeId(pageUrl = current, html = "")?.let { id -> return ResolvedDouyinLink(finalUrl = current, awemeId = id,
                        cookieHeader = cookies.entries.joinToString("; ") {
                            "${it.key}=${it.value}"
                        }.takeIf { it.isNotBlank() })
                }
                return@repeat
            }
            if (code !in 200..299) {
                return null
            }
            val html = connection.inputStream.use { input -> readLimitedHtml(InputStreamReader(input,
                            charsetFromContentType(connection.contentType)))
                }
            return ResolvedDouyinLink(finalUrl = current, awemeId = extractDouyinAwemeId(current, html),
                cookieHeader = cookies.entries.joinToString("; ") {
                    "${it.key}=${it.value}"
                }.takeIf { it.isNotBlank() })
        } catch (_: Exception) {
            return null
        } finally {
            connection?.disconnect()
        }
    }
    return ResolvedDouyinLink(finalUrl = current, awemeId = extractDouyinAwemeId(current, ""),
        cookieHeader = cookies.entries.joinToString("; ") {
            "${it.key}=${it.value}"
        }.takeIf { it.isNotBlank() })
}

private fun fetchDouyinDirectPreview(url: String): LinkPreviewData? {
    val resolved = resolveDouyinLink(url) ?: return null
    val awemeId = resolved.awemeId ?: return null
    val share = fetchDouyinSharePagePreview(awemeId = awemeId, clickUrl = url, cookieHeader = resolved.cookieHeader)
    if (share != null && share.imageCandidates.isNotEmpty()) {
        return share
    }
    val api = fetchDouyinItemInfo(awemeId = awemeId, clickUrl = url, cookieHeader = resolved.cookieHeader)
    return api ?: share
}

private fun enrichDouyinPreview(originalUrl: String, pageUrl: String, html: String, base: LinkPreviewData): LinkPreviewData {
    val awemeId = extractDouyinAwemeId(pageUrl = pageUrl, html = html)
    val sharePreview = awemeId?.let { id -> fetchDouyinSharePagePreview(awemeId = id, clickUrl = originalUrl)
        }
    val apiPreview = if (awemeId != null && (sharePreview == null || sharePreview.imageCandidates.isEmpty())) {
            fetchDouyinItemInfo(awemeId = awemeId, clickUrl = originalUrl)
        } else {
            null
        }
    val embeddedCover = base.imageUrl?.takeIf { it.isNotBlank() }?: extractDouyinCoverUrl(pageUrl = pageUrl, html = html)
    val candidates = buildList {
            sharePreview?.imageUrl?.let(::add)
            addAll(sharePreview?.imageCandidates.orEmpty())
            apiPreview?.imageUrl?.let(::add)
            addAll(apiPreview?.imageCandidates.orEmpty())
            embeddedCover?.let(::add)
        }.filter { it.startsWith("http", ignoreCase = true) }.distinct()
    return base.copy(url = originalUrl, title = base.title?.takeIf { it.isNotBlank() }?: sharePreview?.title?: apiPreview?.title,
            description = base.description?.takeIf { it.isNotBlank() }?: sharePreview?.description?: apiPreview?.description,
            imageUrl = candidates.firstOrNull(), imageCandidates = candidates, siteName = "Douyin").withKnownProviderFallback()
}

private fun extractDouyinAwemeId(pageUrl: String, html: String): String? {
    val sources = listOf(pageUrl, decodeDouyinEmbeddedJson(html))
    val patterns = listOf(Regex(
                pattern = """/(?:share/)?(?:video|note|slides)/(\d{10,})""",
                option = RegexOption.IGNORE_CASE), Regex(
                pattern = """(?:modal_id|aweme_id|awemeId|itemId)[=:%\"']+(\d{10,})""",
                option = RegexOption.IGNORE_CASE), Regex(
                pattern = """[\"'](?:aweme_id|awemeId|itemId)[\"']\s*[:=]\s*[\"']?(\d{10,})""",
                option = RegexOption.IGNORE_CASE))
    sources.forEach { source -> patterns.forEach { regex -> regex.find(source)?.groupValues?.getOrNull(1)?.takeIf { it.isNotBlank() }
                ?.let { return it }
        }
    }
    return null
}

private fun fetchDouyinSharePagePreview(awemeId: String, clickUrl: String, cookieHeader: String? = null): LinkPreviewData? {
    val paths = listOf("share/video", "share/slides", "share/note")
    paths.forEach { path -> val shareUrl = "https://www.iesdouyin.com/$path/$awemeId/"
        var connection: HttpURLConnection? = null
        try {
            connection = (URL(shareUrl).openConnection() as HttpURLConnection).apply {
                        instanceFollowRedirects = true
                        connectTimeout = 6_000
                        readTimeout = 8_000
                        requestMethod = "GET"
                        setRequestProperty("User-Agent", douyinUserAgent())
                        setRequestProperty("Accept", "text/html,application/xhtml+xml,*/*;q=0.8")
                        setRequestProperty("Referer", "https://www.douyin.com/")
                        setRequestProperty("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.7")
                        cookieHeader?.takeIf { it.isNotBlank() }?.let { setRequestProperty("Cookie", it) }
                    }
            connection.connect()
            if (connection.responseCode !in 200..399) return@forEach
            val charset = charsetFromContentType(connection.contentType)
            val html = connection.inputStream.use { input -> readLimitedHtml(InputStreamReader(input, charset))
                }
            val router = extractDouyinRouterData(html) ?: return@forEach
            val item = findDouyinItem(router) ?: return@forEach
            val preview = douyinPreviewFromItem(item, clickUrl)
            if (preview.imageCandidates.isNotEmpty() || !preview.title.isNullOrBlank()) {
                return preview
            }
        } catch (_: Exception) {
            // Probar la siguiente variante (video/slides/note).
        } finally {
            connection?.disconnect()
        }
    }
    return null
}

private fun extractDouyinRouterData(html: String): JSONObject? {
    val markerIndex = html.indexOf("_ROUTER_DATA")
    if (markerIndex < 0) return null
    val equalsIndex = html.indexOf('=', markerIndex)
    if (equalsIndex < 0) return null
    var index = equalsIndex + 1
    while (index < html.length && html[index].isWhitespace()) index++
    if (index >= html.length) return null
    return try {
        when (html[index]) {
            '{' -> {
                val raw = scanBalancedJsonObject(html, index) ?: return null
                JSONObject(raw)
            }
            '"' -> {
                val literal = scanJsonStringLiteral(html, index) ?: return null
                val wrapped = JSONObject("{\"value\":$literal}")
                val inner = wrapped.getString("value")
                JSONObject(decodeDouyinEmbeddedJson(inner))
            }
            else -> null
        }
    } catch (_: Exception) {
        null
    }
}

private fun scanBalancedJsonObject(text: String, start: Int): String? {
    if (start !in text.indices || text[start] != '{') return null
    var depth = 0
    var inString = false
    var escaped = false
    for (index in start until text.length) {
        val char = text[index]
        if (inString) {
            when {
                escaped -> escaped = false
                char == '\\' -> escaped = true
                char == '"' -> inString = false
            }
        } else {
            when (char) {
                '"' -> inString = true
                '{' -> depth++
                '}' -> {
                    depth--
                    if (depth == 0) {
                        return text.substring(start, index + 1)
                    }
                }
            }
        }
    }
    return null
}

private fun scanJsonStringLiteral(text: String, start: Int): String? {
    if (start !in text.indices || text[start] != '"') return null
    var escaped = false
    for (index in start + 1 until text.length) {
        val char = text[index]
        when {
            escaped -> escaped = false
            char == '\\' -> escaped = true
            char == '"' -> return text.substring(start, index + 1)
        }
    }
    return null
}

private fun findDouyinItem(node: Any?, depth: Int = 0): JSONObject? {
    if (node == null || node == JSONObject.NULL || depth > 28) return null
    when (node) {
        is JSONObject -> {
            for (key in listOf("item_list", "aweme_list", "aweme_detail", "aweme")) {
                if (node.has(key)) {
                    val found = findDouyinItem(node.opt(key), depth + 1)
                    if (found != null) return found
                }
            }
            if (node.has("aweme_id") && (node.has("video") || node.has("images") || node.has("desc"))) {
                return node
            }
            val keys = node.keys()
            while (keys.hasNext()) {
                val found = findDouyinItem(node.opt(keys.next()), depth + 1)
                if (found != null) return found
            }
        }
        is JSONArray -> {
            for (index in 0 until node.length()) {
                val found = findDouyinItem(node.opt(index), depth + 1)
                if (found != null) return found
            }
        }
    }
    return null
}

private fun allJsonUrls(node: JSONObject?): List<String> {
    if (node == null) return emptyList()
    val urls = linkedSetOf<String>()
    listOf("url_list", "urlList").forEach { key -> val list = node.optJSONArray(key) ?: return@forEach
            for (index in 0 until list.length()) {
                decodeHtml(list.optString(index)).replace("\\/", "/").takeIf { it.startsWith("http", ignoreCase = true) }?.let(urls::add)
            }
        }
    listOf("uri", "url").forEach { key -> decodeHtml(node.optString(key)).replace("\\/", "/")
                .takeIf { it.startsWith("http", ignoreCase = true) }?.let(urls::add)
        }
    return urls.toList()
}

private fun douyinPreviewFromItem(item: JSONObject, clickUrl: String): LinkPreviewData {
    val video = item.optJSONObject("video")
    val coverCandidates = buildList {
            addAll(allJsonUrls(video?.optJSONObject("origin_cover")))
            addAll(allJsonUrls(video?.optJSONObject("originCover")))
            addAll(allJsonUrls(video?.optJSONObject("cover")))
            addAll(allJsonUrls(video?.optJSONObject("dynamic_cover")))
            addAll(allJsonUrls(video?.optJSONObject("dynamicCover")))
            addAll(allJsonUrls(video?.optJSONObject("cover_medium")))
            addAll(allJsonUrls(video?.optJSONObject("coverMedium")))
            addAll(allJsonUrls(video?.optJSONObject("cover_thumb")))
            addAll(allJsonUrls(video?.optJSONObject("coverThumb")))
            val images = item.optJSONArray("images")
            if (images != null) {
                for (index in 0 until images.length()) {
                    val image = images.optJSONObject(index)
                    addAll(allJsonUrls(image))
                    addAll(allJsonUrls(image?.optJSONObject("display_image")))
                }
            }
        }.filter { it.startsWith("http", ignoreCase = true) }.distinct()
    val title = item.optString("desc").cleanText().takeIf { it.isNotBlank() }
    val author = item.optJSONObject("author")?.optString("nickname")?.cleanText()?.takeIf { it.isNotBlank() }
    return LinkPreviewData(url = clickUrl, title = title ?: author ?: "Douyin", description = author,
        imageUrl = coverCandidates.firstOrNull(), siteName = "Douyin", host = safeHost(clickUrl), imageCandidates = coverCandidates)
}

private fun fetchDouyinItemInfo(awemeId: String, clickUrl: String, cookieHeader: String? = null): LinkPreviewData? {
    val endpoints = listOf("https://www.iesdouyin.com/web/api/v2/aweme/iteminfo/?item_ids=$awemeId",
            "https://www.douyin.com/aweme/v1/web/aweme/detail/" + "?device_platform=webapp&aid=6383&channel=channel_pc_web" +
                "&aweme_id=$awemeId&request_source=600&origin_type=quick_player")
    endpoints.forEach { endpoint -> var connection: HttpURLConnection? = null
        try {
            connection = (URL(endpoint).openConnection() as HttpURLConnection).apply {
                        instanceFollowRedirects = true
                        connectTimeout = 5_000
                        readTimeout = 6_000
                        requestMethod = "GET"
                        setRequestProperty("User-Agent", douyinUserAgent())
                        setRequestProperty("Accept", "application/json,text/plain,*/*")
                        setRequestProperty("Referer", "https://www.douyin.com/")
                        cookieHeader?.takeIf { it.isNotBlank() }?.let { setRequestProperty("Cookie", it) }
                    }
            connection.connect()
            if (connection.responseCode !in 200..299) {
                return@forEach
            }
            val body = connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
            val root = JSONObject(body)
            val item = root.optJSONArray("item_list")?.optJSONObject(0)?: root.optJSONObject("aweme_detail")?: return@forEach
            val video = item.optJSONObject("video")
            val coverCandidates = buildList {
                    addAll(allJsonUrls(video?.optJSONObject("origin_cover")))
                    addAll(allJsonUrls(video?.optJSONObject("originCover")))
                    addAll(allJsonUrls(video?.optJSONObject("cover")))
                    addAll(allJsonUrls(video?.optJSONObject("dynamic_cover")))
                    addAll(allJsonUrls(video?.optJSONObject("dynamicCover")))
                    addAll(allJsonUrls(video?.optJSONObject("cover_medium")))
                    addAll(allJsonUrls(video?.optJSONObject("coverMedium")))
                    addAll(allJsonUrls(video?.optJSONObject("cover_thumb")))
                    addAll(allJsonUrls(video?.optJSONObject("coverThumb")))
                }.distinct()
            val cover = coverCandidates.firstOrNull()
            val title = item.optString("desc").cleanText().takeIf { it.isNotBlank() }
            val author = item.optJSONObject("author")?.optString("nickname")?.cleanText()?.takeIf { it.isNotBlank() }
            if (cover != null || title != null) {
                return LinkPreviewData(url = clickUrl, title = title ?: author ?: "Douyin", description = author, imageUrl = cover,
                    siteName = "Douyin", host = safeHost(clickUrl), imageCandidates = coverCandidates)
            }
        } catch (_: Exception) {
            // Probamos el siguiente endpoint.
        } finally {
            connection?.disconnect()
        }
    }
    return null
}

private fun firstJsonUrl(node: JSONObject?): String? {
    if (node == null) return null
    val list = node.optJSONArray("url_list")?: return null
    for (index in 0 until list.length()) {
        val value = list.optString(index)
        if (value.startsWith("http://", ignoreCase = true) || value.startsWith("https://", ignoreCase = true)) {
            return value
        }
    }
    return null
}

private fun extractDouyinCoverUrl(pageUrl: String, html: String): String? {
    val candidates = mutableListOf<Pair<Int, String>>()
    fun addCandidate(value: String?, path: String, bonus: Int = 0) {
        if (value.isNullOrBlank()) {
            return
        }
        extractUrlsFromDouyinValue(value).forEach { rawUrl -> val resolved = resolveUrl(pageUrl, rawUrl)?: rawUrl
                val score = douyinCoverScore(path = path, url = resolved) + bonus
                if (score > 0) {
                    candidates += score to resolved
                }
            }
    }
    fun walkJson(node: Any?, path: String = "root", depth: Int = 0) {
        if (node == null || depth > 24) {
            return
        }
        when (node) {
            is JSONObject -> {
                val keys = node.keys()
                while (keys.hasNext()) {
                    val key = keys.next()
                    val value = node.opt(key)
                    val childPath = "$path.$key"
                    when (value) {
                        is String -> addCandidate(value = value, path = childPath)
                        JSONObject.NULL -> Unit
                        else -> walkJson(node = value, path = childPath, depth = depth + 1)
                    }
                }
            }
            is JSONArray -> {
                for (index in 0 until node.length()) {
                    walkJson(node = node.opt(index), path = "$path[$index]", depth = depth + 1)
                }
            }
            is String -> addCandidate(value = node, path = path)
        }
    }
    val renderData = Regex(pattern =
                """<script\b[^>]*\bid=["']RENDER_DATA["'][^>]*>(.*?)</script>""",
            options = setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL)).find(html)?.groupValues?.getOrNull(1)
    if (!renderData.isNullOrBlank()) {
        val decoded = decodeDouyinEmbeddedJson(renderData)
        try {
            walkJson(JSONObject(decoded), path = "RENDER_DATA")
        } catch (_: Exception) {
            // La estructura de Douyin cambia con frecuencia. El fallback
            // textual de abajo todavía puede encontrar la portada.
        }
    }
    val routerData = Regex(pattern =
                """window\._ROUTER_DATA\s*=\s*(\{.*?\})\s*;?\s*</script>""",
            options = setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL)).find(html)?.groupValues?.getOrNull(1)
    if (!routerData.isNullOrBlank()) {
        val decoded = decodeDouyinEmbeddedJson(routerData)
        try {
            walkJson(JSONObject(decoded), path = "_ROUTER_DATA")
        } catch (_: Exception) {
            // Se continúa con el fallback textual.
        }
    }
    // Fallback para variantes nuevas de la página: buscamos URLs de los CDN
    // de ByteDance y puntuamos mejor las que aparecen junto a "cover".
    val normalizedHtml = decodeDouyinEmbeddedJson(html).replace("\\/", "/").replace("\\u002F", "/", ignoreCase = true)
            .replace("\\u003A", ":", ignoreCase = true).replace("\\u0026", "&", ignoreCase = true)
            .replace("\\u003D", "=", ignoreCase = true)
    val cdnUrlRegex = Regex(pattern =
                """https?://[^\s"'<>]+(?:douyinpic\.com|byteimg\.com|douyincdn\.com)[^\s"'<>]*""",
            option = RegexOption.IGNORE_CASE)
    cdnUrlRegex.findAll(normalizedHtml).forEach { match -> val start = (match.range.first - 160).coerceAtLeast(0)
            val end = (match.range.last + 160).coerceAtMost(normalizedHtml.lastIndex)
            val context = if (end >= start) {
                    normalizedHtml.substring(start, end + 1)
                } else {
                    ""
                }
            addCandidate(value = match.value, path = "html.$context", bonus = 5)
        }
    return candidates.asSequence().distinctBy { it.second }.maxByOrNull { it.first }?.second
}

private fun decodeDouyinEmbeddedJson(value: String): String {
    var current = decodeHtml(value).trim()
    repeat(3) {
        val next = try {
                URLDecoder.decode(current.replace("+", "%2B"), Charsets.UTF_8.name())
            } catch (_: Exception) {
                current
            }
        if (next == current) {
            return current
        }
        current = next
    }
    return current
}

private fun extractUrlsFromDouyinValue(value: String): List<String> {
    val normalized = decodeHtml(value).replace("\\/", "/").replace("\\u002F", "/", ignoreCase = true)
            .replace("\\u003A", ":", ignoreCase = true).replace("\\u0026", "&", ignoreCase = true)
            .replace("\\u003D", "=", ignoreCase = true).trim()
    val results = linkedSetOf<String>()
    if (normalized.startsWith("http://", ignoreCase = true) || normalized.startsWith("https://", ignoreCase = true) ||
        normalized.startsWith("//")) {
        results += normalized.trim('"', '\'', '`')
    }
    Regex(
        pattern = """(?:https?:)?//[^\s"'<>]+""",
        option = RegexOption.IGNORE_CASE).findAll(normalized).map { it.value }.forEach(results::add)
    return results.toList()
}

private fun douyinCoverScore(path: String, url: String): Int {
    val normalizedPath = path.lowercase(Locale.ROOT).replace("_", "").replace("-", "")
    val normalizedUrl = url.lowercase(Locale.ROOT)
    var score = 0
    when {
        normalizedPath.contains("origincover") -> score += 220
        normalizedPath.contains("itemcover") -> score += 200
        normalizedPath.contains("videocover") -> score += 190
        normalizedPath.contains("dynamiccover") -> score += 175
        normalizedPath.contains("cover") -> score += 150
        normalizedPath.contains("poster") -> score += 120
    }
    if (normalizedPath.contains("avatar") || normalizedPath.contains("author") && !normalizedPath.contains("cover")) {
        score -= 240
    }
    if (normalizedPath.contains("music") || normalizedPath.contains("emoji") || normalizedPath.contains("icon")) {
        score -= 140
    }
    if (normalizedUrl.contains("douyinpic.com")) {
        score += 45
    }
    if (normalizedUrl.contains("byteimg.com")) {
        score += 35
    }
    if (normalizedUrl.contains("douyincdn.com")) {
        score += 30
    }
    if (normalizedUrl.contains(".jpeg") || normalizedUrl.contains(".jpg") || normalizedUrl.contains(".webp") ||
        normalizedUrl.contains(".png")) {
        score += 15
    }
    return score
}

private fun isInstagramRelatedUrl(url: String): Boolean {
    val host = safeHost(url).lowercase(Locale.ROOT)
    return host == "instagram.com" || host.endsWith(".instagram.com")
}

private fun isThreadsRelatedUrl(url: String): Boolean {
    val host = safeHost(url).lowercase(Locale.ROOT)
    return host == "threads.net" || host.endsWith(".threads.net") || host == "threads.com" || host.endsWith(".threads.com")
}

private fun isFacebookRelatedUrl(url: String): Boolean {
    val host = safeHost(url).lowercase(Locale.ROOT)
    return host == "facebook.com" || host.endsWith(".facebook.com") || host == "fb.com" || host.endsWith(".fb.com") || host == "fb.watch" ||
        host.endsWith(".fb.watch")
}

private fun isMetaSocialUrl(url: String): Boolean = isInstagramRelatedUrl(url) || isThreadsRelatedUrl(url) || isFacebookRelatedUrl(url)

private fun isRedditRelatedUrl(url: String): Boolean {
    val host = safeHost(url).lowercase(Locale.ROOT)
    return host == "reddit.com" || host.endsWith(".reddit.com") || host == "redd.it" || host.endsWith(".redd.it")
}

/**
 * Meta volvió a publicar endpoints oEmbed sin token para contenido público.
 * Se usan como primera fuente para Instagram/Facebook/Threads porque sus
 * páginas normales pueden devolver un login/intersticial a HttpURLConnection.
 */
private fun fetchMetaOEmbedPreview(url: String): LinkPreviewData? {
    val encoded = URLEncoder.encode(url, Charsets.UTF_8.name())
    val provider: String
    val endpoint: String
    when {
        isInstagramRelatedUrl(url) -> {
            provider = "Instagram"
            endpoint = "https://graph.facebook.com/v25.0/instagram_oembed" + "?omitscript=true&maxwidth=720&url=$encoded"
        }
        isThreadsRelatedUrl(url) -> {
            provider = "Threads"
            endpoint = "https://graph.threads.com/oembed" + "?omitscript=true&maxwidth=720&url=$encoded"
        }
        isFacebookRelatedUrl(url) -> {
            provider = "Facebook"
            val isVideo = url.contains("/reel/", ignoreCase = true) || url.contains("/videos/", ignoreCase = true) ||
                    safeHost(url).equals("fb.watch", ignoreCase = true)
            val type = if (isVideo) "oembed_video" else "oembed_post"
            endpoint = "https://graph.facebook.com/v25.0/$type" + "?omitscript=true&maxwidth=720&url=$encoded"
        }
        else -> return null
    }
    var connection: HttpURLConnection? = null
    return try {
        connection = (URL(endpoint).openConnection() as HttpURLConnection).apply {
                    instanceFollowRedirects = true
                    connectTimeout = 6_000
                    readTimeout = 7_000
                    requestMethod = "GET"
                    setRequestProperty("User-Agent", previewUserAgent())
                    setRequestProperty("Accept", "application/json,text/plain,*/*")
                    setRequestProperty("Accept-Language", "es-MX,es;q=0.9,en;q=0.7")
                }
        connection.connect()
        if (connection.responseCode !in 200..299) {
            return null
        }
        val body = connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
        val json = JSONObject(body)
        val html = json.optString("html")
        val thumbnail = firstNonBlank(json.optString("thumbnail_url"), json.optString("thumbnailUrl"))
                ?.takeIf { it.startsWith("http", ignoreCase = true) }
        val htmlCandidates = extractSocialImageCandidates(pageUrl = url, html = html)
        val candidates = buildList {
                thumbnail?.let(::add)
                addAll(htmlCandidates)
            }.distinct()
        val title = firstNonBlank(json.optString("title"), json.optString("author_name"))?.cleanText()
        val author = json.optString("author_name").cleanText().takeIf { it.isNotBlank() }
        LinkPreviewData(url = url, title = title ?: provider, description = author, imageUrl = candidates.firstOrNull(), siteName =
                json.optString("provider_name").takeIf { it.isNotBlank() }?: provider, host = safeHost(url), imageCandidates = candidates)
    } catch (_: Exception) {
        null
    } finally {
        connection?.disconnect()
    }
}

/**
 * Reddit expone los datos del post en JSON. De ahí se puede obtener la imagen
 * de preview incluso cuando reddit.com devuelve 403 al intentar raspar HTML.
 */
private fun fetchRedditJsonPreview(entityUrl: String, clickUrl: String): LinkPreviewData? {
    val canonical = resolveSimpleRedirect(entityUrl) ?: entityUrl
    val clean = canonical.substringBefore('#').substringBefore('?').trimEnd('/')
    if (!isRedditRelatedUrl(clean)) return null
    val jsonUrl = "$clean.json?raw_json=1"
    var connection: HttpURLConnection? = null
    return try {
        connection = (URL(jsonUrl).openConnection() as HttpURLConnection).apply {
                    instanceFollowRedirects = true
                    connectTimeout = 6_000
                    readTimeout = 7_000
                    requestMethod = "GET"
                    setRequestProperty("User-Agent", "MyNotes/1.0 Android link-preview")
                    setRequestProperty("Accept", "application/json,text/plain,*/*")
                    setRequestProperty("Accept-Language", "en-US,en;q=0.9")
                }
        connection.connect()
        if (connection.responseCode !in 200..299) {
            return null
        }
        val body = connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
        val root = JSONArray(body)
        val post = root.optJSONObject(0)?.optJSONObject("data")?.optJSONArray("children")?.optJSONObject(0)?.optJSONObject("data")
                ?: return null
        val candidates = linkedSetOf<String>()
        fun addCandidate(value: String?) {
            value?.let(::decodeHtml)?.takeIf { it.startsWith("http", ignoreCase = true) }?.let(candidates::add)
        }
        val previewImages = post.optJSONObject("preview")?.optJSONArray("images")
        if (previewImages != null) {
            for (index in 0 until previewImages.length()) {
                val image = previewImages.optJSONObject(index) ?: continue
                addCandidate(image.optJSONObject("source")?.optString("url"))
                val resolutions = image.optJSONArray("resolutions")
                if (resolutions != null) {
                    for (r in resolutions.length() - 1 downTo 0) {
                        addCandidate(resolutions.optJSONObject(r)?.optString("url"))
                    }
                }
            }
        }
        addCandidate(post.optJSONObject("secure_media")?.optJSONObject("oembed")?.optString("thumbnail_url"))
        addCandidate(post.optJSONObject("media")?.optJSONObject("oembed")?.optString("thumbnail_url"))
        addCandidate(post.optString("thumbnail"))
        addCandidate(directImageUrl(post.optString("url_overridden_by_dest")))
        addCandidate(directImageUrl(post.optString("url")))
        val mediaMetadata = post.optJSONObject("media_metadata")
        if (mediaMetadata != null) {
            val keys = mediaMetadata.keys()
            while (keys.hasNext()) {
                val media = mediaMetadata.optJSONObject(keys.next()) ?: continue
                addCandidate(media.optJSONObject("s")?.optString("u"))
                val previews = media.optJSONArray("p")
                if (previews != null) {
                    for (index in previews.length() - 1 downTo 0) {
                        addCandidate(previews.optJSONObject(index)?.optString("u"))
                    }
                }
            }
        }
        val title = post.optString("title").cleanText().takeIf { it.isNotBlank() }
        val subreddit = post.optString("subreddit_name_prefixed").takeIf { it.isNotBlank() }
        val author = post.optString("author").takeIf { it.isNotBlank() }
        LinkPreviewData(url = clickUrl, title = title ?: "Reddit", description = listOfNotNull(subreddit, author?.let { "u/$it" })
                    .joinToString(" · ").takeIf { it.isNotBlank() }, imageUrl = candidates.firstOrNull(), siteName = "Reddit",
            host = safeHost(clickUrl), imageCandidates = candidates.toList())
    } catch (_: Exception) {
        null
    } finally {
        connection?.disconnect()
    }
}

private fun resolveSimpleRedirect(url: String): String? {
    if (!safeHost(url).equals("redd.it", ignoreCase = true) && !safeHost(url).endsWith(".redd.it", ignoreCase = true)) {
        return url
    }
    var connection: HttpURLConnection? = null
    return try {
        connection = (URL(url).openConnection() as HttpURLConnection).apply {
                    instanceFollowRedirects = true
                    connectTimeout = 5_000
                    readTimeout = 5_000
                    requestMethod = "GET"
                    setRequestProperty("User-Agent", previewUserAgent())
                }
        connection.connect()
        connection.url?.toString()
    } catch (_: Exception) {
        null
    } finally {
        connection?.disconnect()
    }
}

private fun enrichSocialHtmlPreview(originalUrl: String, pageUrl: String, html: String, base: LinkPreviewData,
    providerPreview: LinkPreviewData?): LinkPreviewData {
    val candidates = buildList {
            base.imageUrl?.let(::add)
            providerPreview?.imageUrl?.let(::add)
            addAll(base.imageCandidates)
            addAll(providerPreview?.imageCandidates.orEmpty())
            addAll(extractSocialImageCandidates(pageUrl = pageUrl, html = html))
        }.filter { it.startsWith("http", ignoreCase = true) }.distinct()
    val provider = when {
            isInstagramRelatedUrl(originalUrl) || isInstagramRelatedUrl(pageUrl) -> "Instagram"
            isThreadsRelatedUrl(originalUrl) || isThreadsRelatedUrl(pageUrl) -> "Threads"
            isFacebookRelatedUrl(originalUrl) || isFacebookRelatedUrl(pageUrl) -> "Facebook"
            isRedditRelatedUrl(originalUrl) || isRedditRelatedUrl(pageUrl) -> "Reddit"
            else -> null
        }
    return base.copy(url = originalUrl, title = base.title?.takeIf { it.isNotBlank() }?: providerPreview?.title, description =
            base.description?.takeIf { it.isNotBlank() }?: providerPreview?.description, imageUrl = candidates.firstOrNull(), siteName =
            providerPreview?.siteName?.takeIf { it.isNotBlank() }?: provider?: base.siteName, imageCandidates = candidates)
}

/** Busca URLs de imagen escondidas dentro de JSON/HTML hidratado. */
private fun extractSocialImageCandidates(pageUrl: String, html: String): List<String> {
    if (html.isBlank()) return emptyList()
    val normalized = html.replace("\\/", "/").replace("\\u002F", "/", ignoreCase = true).replace("\\u003A", ":", ignoreCase = true)
            .replace("\\u0026", "&", ignoreCase = true).replace("\\u003D", "=", ignoreCase = true)
            .replace("\\u0025", "%", ignoreCase = true).replace("&amp;", "&", ignoreCase = true).replace("&quot;", "\"", ignoreCase = true)
            .replace("&#39;", "'", ignoreCase = true)
    val explicit = mutableListOf<String>()
    val keyRegex = Regex(pattern =
                """(?i)[\"'](?:thumbnail_url|thumbnailUrl|display_url|displayUrl|image_url|imageUrl|contentUrl|content_url|poster_url|posterUrl|cover_url|coverUrl)[\"']\s*:\s*[\"']([^\"']+)[\"']"""
        )
    keyRegex.findAll(normalized).forEach { match -> match.groupValues.getOrNull(1)?.let { resolveUrl(pageUrl, it) }?.let(explicit::add)
    }
    val discovered = UrlRegex.findAll(normalized).map { it.value.trimEnd('.', ',', ';', ')', ']', '}') }
            .mapNotNull { resolveUrl(pageUrl, it) }.filter { candidate -> val lower = candidate.lowercase(Locale.ROOT)
                lower.contains("fbcdn.net") || lower.contains("cdninstagram.com") ||
                    lower.contains("instagram.com") && looksLikeRemoteImageUrl(lower) || lower.contains("threadscdn") ||
                    lower.contains("redd.it") || lower.contains("redditmedia.com") || lower.contains("redditstatic.com") ||
                    lower.contains("douyinpic.com") || lower.contains("byteimg.com") || lower.contains("douyincdn.com") ||
                    lower.contains("pstatp.com") || looksLikeRemoteImageUrl(lower)
            }.toList()
    return (explicit + discovered).asSequence().filter { it.startsWith("http", ignoreCase = true) }.distinct()
        .sortedByDescending(::scoreSocialImageCandidate).take(16).toList()
}

private fun looksLikeRemoteImageUrl(url: String): Boolean = url.contains(".jpg") || url.contains(".jpeg") || url.contains(".png") ||
        url.contains(".webp") || url.contains(".gif") || url.contains("/image") || url.contains("thumbnail") || url.contains("preview") ||
        url.contains("cover")
private fun scoreSocialImageCandidate(url: String): Int {
    val lower = url.lowercase(Locale.ROOT)
    var score = 0
    if (lower.contains("thumbnail")) score += 80
    if (lower.contains("display")) score += 70
    if (lower.contains("preview")) score += 65
    if (lower.contains("cover")) score += 60
    if (lower.contains("media")) score += 30
    if (lower.contains("fbcdn.net")) score += 35
    if (lower.contains("cdninstagram.com")) score += 35
    if (lower.contains("preview.redd.it")) score += 40
    if (lower.contains("i.redd.it")) score += 35
    if (lower.contains("douyinpic.com")) score += 35
    if (lower.contains("byteimg.com")) score += 30
    if (lower.contains("avatar")) score -= 120
    if (lower.contains("profile_pic")) score -= 120
    if (lower.contains("profilepic")) score -= 120
    if (lower.contains("favicon")) score -= 150
    if (lower.contains("emoji")) score -= 140
    if (lower.contains("icon")) score -= 80
    if (lower.contains("logo")) score -= 90
    return score
}

private fun isTikTokRelatedUrl(url: String): Boolean {
    val host = try {
            Uri.parse(url).host?.lowercase(Locale.ROOT).orEmpty()
        } catch (_: Exception) {
            ""
        }
    return host == "tiktok.com" || host.endsWith(".tiktok.com")
}

/**
 * TikTok expone oEmbed para publicaciones públicas. Además de dar un título
 * mucho más útil que el HTML genérico, devuelve thumbnail_url. La imagen se
 * descarga después al caché local para que el grid no dependa del CDN al
 * hacer scroll.
 */
private fun fetchTikTokOEmbed(entityUrl: String, clickUrl: String): LinkPreviewData? {
    var connection: HttpURLConnection? = null
    return try {
        val endpoint = "https://www.tiktok.com/oembed?url=" + URLEncoder.encode(entityUrl, Charsets.UTF_8.name())
        connection = (URL(endpoint).openConnection() as HttpURLConnection).apply {
                    instanceFollowRedirects = true
                    connectTimeout = 5_000
                    readTimeout = 6_000
                    requestMethod = "GET"
                    setRequestProperty("User-Agent", previewUserAgent())
                    setRequestProperty("Accept", "application/json,text/plain,*/*")
                    setRequestProperty("Referer", "https://www.tiktok.com/")
                }
        connection.connect()
        if (connection.responseCode !in 200..299) {
            return null
        }
        val body = connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
        val json = JSONObject(body)
        val thumbnail = json.optString("thumbnail_url").takeIf { it.isNotBlank() }
        val title = json.optString("title").cleanText().takeIf { it.isNotBlank() }
        val author = json.optString("author_name").cleanText().takeIf { it.isNotBlank() }
        if (thumbnail == null && title == null) {
            return null
        }
        LinkPreviewData(url = clickUrl, title = title ?: author ?: "TikTok", description = author, imageUrl = thumbnail,
            siteName = "TikTok", host = safeHost(clickUrl))
    } catch (_: Exception) {
        null
    } finally {
        connection?.disconnect()
    }
}

private val SpotifyEntityTypes = setOf("track", "album", "artist", "playlist", "show", "episode", "audiobook")

/**
 * Spotify usa enlaces cortos (spotify.link / spotify.app.link) que a veces
 * hacen el último salto mediante JavaScript en vez de un 301/302. Por eso
 * HttpURLConnection puede quedarse en spotify.app.link aunque el navegador
 * termine abriendo open.spotify.com.
 *
 * Estas funciones buscan el destino real tanto en los parámetros de Branch
 * como dentro del HTML/JavaScript de la página intermedia.
 */
private fun isSpotifyRelatedUrl(url: String): Boolean {
    val host = try {
            Uri.parse(url).host?.lowercase(Locale.ROOT).orEmpty()
        } catch (_: Exception) {
            ""
        }
    return host == "spotify.link" || host == "spoti.fi" || host == "spotify.com" || host.endsWith(".spotify.com")
}

private fun findSpotifyEntityUrl(url: String): String? {
    normalizeSpotifyEntityUrl(url)?.let {
        return it
    }
    val decoded = decodeUrlRepeatedly(url)
    normalizeSpotifyEntityUrl(decoded)?.let {
        return it
    }
    return try {
        val uri = Uri.parse(url)
        listOf("\$full_url", "\$fallback_url", "full_url", "fallback_url", "url", "deep_link_value", "canonical_url"
        ).firstNotNullOfOrNull { key -> uri.getQueryParameter(key)?.let(::decodeUrlRepeatedly)?.let(::normalizeSpotifyEntityUrl)
        }
    } catch (_: Exception) {
        null
    }
}

private fun findSpotifyEntityUrlInText(text: String): String? {
    val variants = linkedSetOf(text, decodeHtml(text), text.replace("\\/", "/").replace("\\u002F", "/", ignoreCase = true)
                .replace("\\u003A", ":", ignoreCase = true), decodeUrlRepeatedly(text))
    variants.forEach { candidateText -> val normalizedText = candidateText.replace("&amp;", "&", ignoreCase = true).replace("\\/", "/")
        val openUrl = Regex(pattern =
                    """https?://open\.spotify\.com/[^\s\"'<>\\]+""",
                option = RegexOption.IGNORE_CASE).find(normalizedText)?.value?.trimEnd('.', ',', ';', ')', ']', '}')
        if (openUrl != null) {
            normalizeSpotifyEntityUrl(openUrl)?.let {
                return it
            }
        }
        val spotifyUri = Regex(pattern =
                    """spotify:(track|album|artist|playlist|show|episode|audiobook):([A-Za-z0-9]+)""",
                option = RegexOption.IGNORE_CASE).find(normalizedText)
        if (spotifyUri != null) {
            val type = spotifyUri.groupValues[1].lowercase(Locale.ROOT)
            val id = spotifyUri.groupValues[2]
            return "https://open.spotify.com/$type/$id"
        }
    }
    return null
}

private fun normalizeSpotifyEntityUrl(candidate: String): String? {
    val cleaned = candidate.trim().trim('"', '\'', '`').replace("\\/", "/")
    if (cleaned.startsWith("spotify:", ignoreCase = true)) {
        val match = Regex(pattern =
                    """spotify:(track|album|artist|playlist|show|episode|audiobook):([A-Za-z0-9]+)""",
                option = RegexOption.IGNORE_CASE).find(cleaned)?: return null
        return "https://open.spotify.com/" + match.groupValues[1].lowercase(Locale.ROOT) + "/" + match.groupValues[2]
    }
    return try {
        val uri = Uri.parse(cleaned)
        val host = uri.host?.lowercase(Locale.ROOT).orEmpty()
        if (host != "open.spotify.com") {
            return null
        }
        val segments = uri.pathSegments
        val typeIndex = segments.indexOfFirst {
                it.lowercase(Locale.ROOT) in SpotifyEntityTypes
            }
        if (typeIndex < 0 || typeIndex + 1 >= segments.size) {
            return null
        }
        val type = segments[typeIndex].lowercase(Locale.ROOT)
        val id = segments[typeIndex + 1].substringBefore('?').substringBefore('#').takeIf { value -> value.isNotBlank() &&
                        value.all { it.isLetterOrDigit() }
                }?: return null
        "https://open.spotify.com/$type/$id"
    } catch (_: Exception) {
        null
    }
}

private fun decodeUrlRepeatedly(value: String): String {
    var current = value
    repeat(3) {
        val next = try {
                URLDecoder.decode(current, Charsets.UTF_8.name())
            } catch (_: Exception) {
                current
            }
        if (next == current) {
            return current
        }
        current = next
    }
    return current
}

private fun fetchSpotifyOEmbed(entityUrl: String, clickUrl: String): LinkPreviewData? {
    var connection: HttpURLConnection? = null
    return try {
        val endpoint = "https://open.spotify.com/oembed?url=" + URLEncoder.encode(entityUrl, Charsets.UTF_8.name())
        connection = (URL(endpoint).openConnection() as HttpURLConnection).apply {
                    instanceFollowRedirects = true
                    connectTimeout = 6_000
                    readTimeout = 7_000
                    requestMethod = "GET"
                    setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 10) AppleWebKit/537.36 " +
                            "(KHTML, like Gecko) Chrome/124.0 Mobile Safari/537.36")
                    setRequestProperty("Accept", "application/json,text/plain,*/*")
                }
        connection.connect()
        if (connection.responseCode !in 200..299) {
            return null
        }
        val body = connection.inputStream.use { input -> readLimitedHtml(InputStreamReader(input, Charsets.UTF_8))
            }
        val json = JSONObject(body)
        val title = json.optString("title").takeIf { it.isNotBlank() }
        val author = json.optString("author_name").takeIf { it.isNotBlank() }
        val thumbnail = json.optString("thumbnail_url").takeIf { it.isNotBlank() }
        val provider = json.optString("provider_name").takeIf { it.isNotBlank() }?: "Spotify"
        if (title == null && thumbnail == null) {
            return null
        }
        LinkPreviewData(url = clickUrl, title = title, description = author, imageUrl = thumbnail, siteName = provider,
            host = "open.spotify.com")
    } catch (_: Exception) {
        null
    } finally {
        connection?.disconnect()
    }
}

private fun LinkPreviewData.withKnownProviderFallback(): LinkPreviewData {
    if (!imageUrl.isNullOrBlank()) {
        return this
    }
    val youtubeId = youtubeVideoId(url)
    return when {
        youtubeId != null -> copy(imageUrl = "https://i.ytimg.com/vi/$youtubeId/hqdefault.jpg", siteName = siteName
                        ?.takeIf { it.isNotBlank() }?: "YouTube")
        isSpotifyRelatedUrl(url) -> copy(siteName = "Spotify", title = title?.takeIf { it.isNotBlank() }?: "Spotify")
        isTikTokRelatedUrl(url) -> copy(siteName = "TikTok", title = title?.takeIf { it.isNotBlank() }?: "TikTok")
        isDouyinRelatedUrl(url) -> copy(siteName = "Douyin", title = title?.takeIf { it.isNotBlank() }?: "Douyin")
        isInstagramRelatedUrl(url) -> copy(siteName = "Instagram", title = title?.takeIf { it.isNotBlank() } ?: "Instagram")
        isThreadsRelatedUrl(url) -> copy(siteName = "Threads", title = title?.takeIf { it.isNotBlank() } ?: "Threads")
        isFacebookRelatedUrl(url) -> copy(siteName = "Facebook", title = title?.takeIf { it.isNotBlank() } ?: "Facebook")
        isRedditRelatedUrl(url) -> copy(siteName = "Reddit", title = title?.takeIf { it.isNotBlank() } ?: "Reddit")
        else -> this
    }
}

@Composable
fun LinkPreviewCard(url: String, modifier: Modifier = Modifier, compact: Boolean = false, textColorMode: String = "auto",
    deferLoad: Boolean = false) {
    val context = LocalContext.current
    /*
     * La composición solo consulta RAM. La lectura persistida, el JSON, la
     * red y la caché de miniaturas se ejecutan desde la corrutina del efecto.
     * Si la tarjeta aparece durante un scroll rápido, deferLoad evita iniciar
     * trabajo nuevo hasta que el grid vuelva a estar en reposo. Un preview ya
     * cargado permanece visible porque el estado recordado no se reinicia.
     */
    var preview by remember(url) {
        mutableStateOf(LinkPreviewRepository.peekMemory(url) ?: LinkPreviewData.basic(url))
    }
    LaunchedEffect(url, deferLoad) {
        if (!deferLoad) {
            preview = LinkPreviewRepository.load(context = context.applicationContext, url = url)
        }
    }
    val title = preview.title?.takeIf { it.isNotBlank() }?: preview.siteName?.takeIf { it.isNotBlank() }?: preview.host
    val subtitle = preview.description?.takeIf { it.isNotBlank() }
    val imageModel: Any? = preview.cachedImagePath?.let(::File)?.takeIf { it.isFile && it.length() > 0L }
            // Si el guardado local falla (CDN con hot-link protection), Coil
            // todavía puede intentar cargar directamente la URL remota.
            ?: preview.imageUrl
    val previewBackground = MaterialTheme.colorScheme.surfaceContainer
    val placeholderBackground = MaterialTheme.colorScheme.surfaceContainerHigh
    val previewPrimaryText = resolveUiTextColor(value = textColorMode, background = previewBackground)
    val previewSecondaryText = resolveSecondaryUiTextColor(value = textColorMode, background = previewBackground)
    val previewGraphicColor = resolveUiGraphicColor(value = textColorMode, background = previewBackground)
    val placeholderTextColor = resolveUiTextColor(value = textColorMode, background = placeholderBackground)
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val veryCompact = compact && maxWidth < 170.dp
        val narrowCompact = compact && maxWidth < 220.dp
        Surface(onClick = {
                UiSoundPlayer.playAction(context = context, action = UiActionSound.Link)
                openExternalLink(context = context, url = preview.url)
            }, modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium, color = previewBackground, tonalElevation = 1.dp) {
            if (compact) {
                when {
                    veryCompact -> {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Surface(modifier = Modifier.fillMaxWidth().aspectRatio(16f / 9f), color = placeholderBackground) {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    if (imageModel != null) {
                                        AsyncImage(model = imageModel, contentDescription = null, modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop)
                                    } else {
                                        Text(text = (preview.siteName ?: preview.host).take(1).uppercase(), color = placeholderTextColor,
                                            style = MaterialTheme.typography.titleMedium)
                                    }
                                }
                            }
                            LinkPreviewText(title = title, subtitle = subtitle, siteName = preview.siteName ?: preview.host, compact = true,
                                veryCompact = true, showSubtitle = false, primaryTextColor = previewPrimaryText,
                                secondaryTextColor = previewSecondaryText, graphicColor = previewGraphicColor, modifier =
                                    Modifier.padding(10.dp))
                        }
                    }
                    else -> {
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Surface(modifier = Modifier.width(if (narrowCompact) 84.dp else 112.dp).height(
                                            if (narrowCompact) 68.dp else 86.dp), color = placeholderBackground) {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    if (imageModel != null) {
                                        AsyncImage(model = imageModel, contentDescription = null, modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop)
                                    } else {
                                        Text(text = (preview.siteName ?: preview.host).take(1).uppercase(), color = placeholderTextColor,
                                            style = MaterialTheme.typography.titleMedium)
                                    }
                                }
                            }
                            LinkPreviewText(title = title, subtitle = subtitle, siteName = preview.siteName ?: preview.host, compact = true,
                                veryCompact = narrowCompact, showSubtitle = !narrowCompact, primaryTextColor = previewPrimaryText,
                                secondaryTextColor = previewSecondaryText, graphicColor = previewGraphicColor, modifier = Modifier
                                        .weight(1f).padding(horizontal = 10.dp, vertical = 9.dp))
                        }
                    }
                }
            } else {
                Column(modifier = Modifier.fillMaxWidth()) {
                    if (imageModel != null) {
                        AsyncImage(model = imageModel, contentDescription = null, modifier = Modifier.fillMaxWidth().height(170.dp),
                            contentScale = ContentScale.Crop)
                    }
                    LinkPreviewText(title = title, subtitle = subtitle, siteName = preview.siteName ?: preview.host, compact = false,
                        primaryTextColor = previewPrimaryText, secondaryTextColor = previewSecondaryText,
                        graphicColor = previewGraphicColor, modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp))
                }
            }
        }
    }
}

@Composable
private fun LinkPreviewText(title: String, subtitle: String?, siteName: String, compact: Boolean, veryCompact: Boolean = false,
    showSubtitle: Boolean = true, primaryTextColor: Color, secondaryTextColor: Color, graphicColor: Color, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.Center) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = siteName, modifier = Modifier.weight(1f), color = secondaryTextColor, style = MaterialTheme.typography.labelMedium,
                maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(modifier = Modifier.width(6.dp))
            Icon(imageVector = Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(
                        if (veryCompact) 13.dp else 15.dp), tint = graphicColor)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = title, color = primaryTextColor, style = when {
                    veryCompact -> MaterialTheme.typography.bodyLarge
                    compact -> MaterialTheme.typography.titleSmall
                    else -> MaterialTheme.typography.titleMedium
                }, fontWeight = FontWeight.SemiBold, maxLines = if (compact) 2 else 3, overflow = TextOverflow.Ellipsis)
        if (showSubtitle && !subtitle.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = subtitle, color = secondaryTextColor, style = if (veryCompact) {
                        MaterialTheme.typography.labelMedium
                    } else {
                        MaterialTheme.typography.bodySmall
                    }, maxLines = when {
                        veryCompact -> 1
                        compact -> 2
                        else -> 3
                    }, overflow = TextOverflow.Ellipsis)
        }
    }
}

private fun openExternalLink(context: Context, url: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        context.startActivity(intent)
    } catch (_: Exception) {
        // Si no existe una app capaz de abrirlo, la nota sigue siendo usable.
    }
}

private fun readLimitedHtml(reader: InputStreamReader): String {
    reader.use {
        val buffer = CharArray(8_192)
        val builder = StringBuilder()
        while (builder.length < MAX_HTML_CHARS) {
            val remaining = MAX_HTML_CHARS - builder.length
            val count = it.read(buffer, 0, minOf(buffer.size, remaining))
            if (count <= 0) {
                break
            }
            builder.append(buffer, 0, count)
        }
        return builder.toString()
    }
}

private fun charsetFromContentType(contentType: String?): Charset {
    val charsetName = contentType?.substringAfter("charset=", missingDelimiterValue = "")?.substringBefore(';')?.trim()?.trim('"', '\'')
            .orEmpty()
    return try {
        if (charsetName.isNotBlank()) {
            Charset.forName(charsetName)
        } else {
            Charsets.UTF_8
        }
    } catch (_: Exception) {
        Charsets.UTF_8
    }
}

private fun decodeHtml(value: String): String = Html.fromHtml(value, Html.FROM_HTML_MODE_LEGACY).toString().replace(Regex("\\s+"), " ")
        .trim()
private fun String.cleanText(): String = decodeHtml(this).take(500)

private fun firstNonBlank(vararg values: String?): String? = values.firstOrNull {
        !it.isNullOrBlank()
    }
private fun resolveUrl(baseUrl: String, candidate: String): String? = try {
        URL(URL(baseUrl), candidate).toString()
    } catch (_: Exception) {
        null
    }
private fun safeHost(url: String): String = try {
        URI(url).host?.removePrefix("www.")?.takeIf { it.isNotBlank() }?: url
    } catch (_: Exception) {
        url
    }
private fun directImageUrl(url: String): String? {
    val cleanPath = url.substringBefore('#').substringBefore('?').lowercase(Locale.ROOT)
    return if (listOf(".jpg", ".jpeg", ".png", ".webp", ".gif", ".avif").any(cleanPath::endsWith)) {
        url
    } else {
        null
    }
}

private fun fileNameFromUrl(url: String): String = try {
        URI(url).path?.substringAfterLast('/')?.takeIf { it.isNotBlank() }?: safeHost(url)
    } catch (_: Exception) {
        safeHost(url)
    }
private fun youtubeVideoId(url: String): String? = try {
        val uri = Uri.parse(url)
        val host = uri.host?.lowercase(Locale.ROOT).orEmpty()
        when {
            host == "youtu.be" -> uri.pathSegments.firstOrNull()?.takeIf { it.isNotBlank() }
            host.endsWith("youtube.com") -> {
                uri.getQueryParameter("v")?.takeIf { it.isNotBlank() }?: uri.pathSegments.let { segments -> val markerIndex =
                                segments.indexOfFirst {
                                    it == "shorts" || it == "embed"
                                }
                            if (markerIndex >= 0 && markerIndex + 1 < segments.size) {
                                segments[markerIndex + 1]
                            } else {
                                null
                            }
                        }
            }
            else -> null
        }
    } catch (_: Exception) {
        null
    }

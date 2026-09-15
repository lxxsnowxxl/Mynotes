# LinkPreviewCard.kt

**Ruta:** `app/src/main/java/com/example/mynotes/ui/components/LinkPreviewCard.kt`  
**Paquete:** `com.example.mynotes.ui.components`  
**Líneas:** 3571 → 1691 (52.6% menos)

## Responsabilidad

Implementa las tarjetas de vista previa para enlaces y la obtención/normalización de metadatos de múltiples sitios. Contiene lógica de red, parsing HTML/JSON, detección específica de proveedores y fallbacks.

## Papel dentro de la arquitectura

Es uno de los archivos más complejos: separa la URL visible del trabajo de resolver título, imagen, sitio y contenido enriquecido, y ofrece un componente Compose para presentarlo.

## Flujo funcional principal

Flujo típico: se detecta una URL -> se normaliza/procesa según proveedor -> se intenta obtener metadata específica o genérica -> se aplican fallbacks -> el resultado se representa en una tarjeta. El archivo contiene rutas especiales para servicios con HTML/JSON no uniforme.

## Dependencias y relaciones

**Dependencias internas de MyNotes:** `com.example.mynotes.ui.theme.resolveUiTextColor`, `com.example.mynotes.ui.theme.resolveSecondaryUiTextColor`, `com.example.mynotes.ui.theme.resolveUiGraphicColor`, `com.example.mynotes.ui.sound.UiActionSound`, `com.example.mynotes.ui.sound.UiSoundPlayer`.

**Compose:** `androidx.compose.foundation.layout.Arrangement`, `androidx.compose.foundation.layout.BoxWithConstraints`, `androidx.compose.foundation.layout.Box`, `androidx.compose.foundation.layout.Column`, `androidx.compose.foundation.layout.Row`, `androidx.compose.foundation.layout.Spacer`, `androidx.compose.foundation.layout.fillMaxSize`, `androidx.compose.foundation.layout.fillMaxWidth`, `androidx.compose.foundation.layout.aspectRatio`, `androidx.compose.foundation.layout.height`, `androidx.compose.foundation.layout.padding`, `androidx.compose.foundation.layout.size`, `androidx.compose.foundation.layout.width`, `androidx.compose.material.icons.Icons`, `androidx.compose.material.icons.filled.OpenInNew`, `androidx.compose.material3.Icon`, `androidx.compose.material3.MaterialTheme`, `androidx.compose.material3.Surface`….

**Android/Jetpack:** `android.content.Context`, `android.content.Intent`, `android.net.Uri`, `android.os.Build`, `android.text.Html`, `android.util.LruCache`.

**Bibliotecas externas:** `coil3.compose.AsyncImage`, `org.json.JSONArray`, `org.json.JSONObject`.

**Kotlin/Java/corrutinas:** `kotlinx.coroutines.Dispatchers`, `kotlinx.coroutines.async`, `kotlinx.coroutines.awaitAll`, `kotlinx.coroutines.coroutineScope`, `kotlinx.coroutines.withContext`, `kotlinx.coroutines.sync.Mutex`, `kotlinx.coroutines.sync.withLock`, `java.io.File`, `java.io.FileOutputStream`, `java.io.InputStreamReader`, `java.net.HttpURLConnection`, `java.net.URI`, `java.net.URL`, `java.net.URLDecoder`, `java.net.URLEncoder`, `java.nio.charset.Charset`, `java.security.MessageDigest`, `java.util.Locale`.

## Declaraciones importantes detectadas

| Línea | Tipo | Nombre | Firma / declaración | Qué hace |
|---:|---|---|---|---|
| 79 | fun | `extractLinkUrls` | `fun extractLinkUrls(text: String): List<String> = UrlRegex.findAll(text).map { match -> match.value.trimEnd('.', ',', ';', '!', ')', ']',` | Extrae una porción de información desde contenido más amplio, como HTML, JSON, URI o metadata. |
| 86 | fun | `noteTextForDisplay` | `fun noteTextForDisplay(content: String, links: List<String>): String {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 95 | data class | `LinkPreviewData` | `private data class LinkPreviewData(val url: String, val title: String?, val description: String?, val imageUrl: String?,` | Modelo de datos que agrupa valores relacionados con esta responsabilidad. |
| 98 | fun | `basic` | `fun basic(url: String): LinkPreviewData {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 106 | object | `LinkPreviewRepository` | `private object LinkPreviewRepository {` | Singleton que centraliza funciones/estado compartido sin crear múltiples instancias. |
| 109 | fun | `lockFor` | `private fun lockFor(url: String): Mutex = synchronized(locks) {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 112 | fun | `peek` | `fun peek(context: Context, url: String): LinkPreviewData? {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 117 | fun | `load` | `suspend fun load(context: Context, url: String): LinkPreviewData = withContext(Dispatchers.IO) {` | Carga o prepara datos/recursos necesarios, normalmente aplicando caché o trabajo de I/O cuando corresponde. |
| 140 | fun | `fetch` | `private fun fetch(url: String): LinkPreviewData {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 255 | fun | `preloadLinkPreviews` | `suspend fun preloadLinkPreviews(context: Context, urls: List<String>) {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 266 | fun | `previewUserAgent` | `private fun previewUserAgent(): String = "Mozilla/5.0 (Linux; Android 10) AppleWebKit/537.36 " +` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 272 | fun | `douyinUserAgent` | `private fun douyinUserAgent(): String = "Mozilla/5.0 (iPhone; CPU iPhone OS 17_5 like Mac OS X) " +` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 274 | fun | `previewCachePreferences` | `private fun previewCachePreferences(context: Context) = context.getSharedPreferences("link_preview_cache_v$PREVIEW_CACHE_VERSION",` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 276 | fun | `previewCacheKey` | `private fun previewCacheKey(url: String): String = "preview_" + sha256(url)` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 278 | fun | `persistPreview` | `private fun persistPreview(context: Context, originalUrl: String, preview: LinkPreviewData) {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 287 | fun | `readPersistedPreview` | `private fun readPersistedPreview(context: Context, url: String): LinkPreviewData? {` | Lee datos desde la fuente indicada y los transforma al formato usado internamente. |
| 318 | fun | `ensureThumbnailCached` | `private fun ensureThumbnailCached(context: Context, preview: LinkPreviewData): LinkPreviewData {` | Comprueba una condición y produce un resultado que cumple los requisitos esperados. |
| 348 | fun | `downloadThumbnail` | `private fun downloadThumbnail(imageUrl: String, refererUrl: String, destination: File): Boolean {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 409 | fun | `looksLikeImageFile` | `private fun looksLikeImageFile(file: File): Boolean {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 427 | fun | `providerReferer` | `private fun providerReferer(url: String): String {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 440 | fun | `pruneThumbnailCache` | `private fun pruneThumbnailCache(directory: File) {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 451 | fun | `sha256` | `private fun sha256(value: String): String {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 457 | fun | `parseHtml` | `private fun parseHtml(pageUrl: String, html: String): LinkPreviewData {` | Interpreta texto/datos externos y los convierte a una estructura utilizable. |
| 498 | fun | `isDouyinRelatedUrl` | `private fun isDouyinRelatedUrl(url: String): Boolean {` | Evalúa una condición y devuelve un resultado booleano. |
| 507 | data class | `ResolvedDouyinLink` | `private data class ResolvedDouyinLink(val finalUrl: String, val awemeId: String?, val cookieHeader: String?)` | Modelo de datos que agrupa valores relacionados con esta responsabilidad. |
| 514 | fun | `resolveDouyinLink` | `private fun resolveDouyinLink(url: String): ResolvedDouyinLink? {` | Resuelve una clave/estado a su representación efectiva aplicando reglas y fallbacks. |
| 576 | fun | `fetchDouyinDirectPreview` | `private fun fetchDouyinDirectPreview(url: String): LinkPreviewData? {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 587 | fun | `enrichDouyinPreview` | `private fun enrichDouyinPreview(originalUrl: String, pageUrl: String, html: String, base: LinkPreviewData): LinkPreviewData {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 609 | fun | `extractDouyinAwemeId` | `private fun extractDouyinAwemeId(pageUrl: String, html: String): String? {` | Extrae una porción de información desde contenido más amplio, como HTML, JSON, URI o metadata. |
| 625 | fun | `fetchDouyinSharePagePreview` | `private fun fetchDouyinSharePagePreview(awemeId: String, clickUrl: String, cookieHeader: String? = null): LinkPreviewData? {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 661 | fun | `extractDouyinRouterData` | `private fun extractDouyinRouterData(html: String): JSONObject? {` | Extrae una porción de información desde contenido más amplio, como HTML, JSON, URI o metadata. |
| 688 | fun | `scanBalancedJsonObject` | `private fun scanBalancedJsonObject(text: String, start: Int): String? {` | Recorre una secuencia para localizar o delimitar el dato buscado. |
| 717 | fun | `scanJsonStringLiteral` | `private fun scanJsonStringLiteral(text: String, start: Int): String? {` | Recorre una secuencia para localizar o delimitar el dato buscado. |
| 731 | fun | `findDouyinItem` | `private fun findDouyinItem(node: Any?, depth: Int = 0): JSONObject? {` | Busca y devuelve el elemento que satisface el criterio implementado. |
| 760 | fun | `allJsonUrls` | `private fun allJsonUrls(node: JSONObject?): List<String> {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 774 | fun | `douyinPreviewFromItem` | `private fun douyinPreviewFromItem(item: JSONObject, clickUrl: String): LinkPreviewData {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 801 | fun | `fetchDouyinItemInfo` | `private fun fetchDouyinItemInfo(awemeId: String, clickUrl: String, cookieHeader: String? = null): LinkPreviewData? {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 852 | fun | `firstJsonUrl` | `private fun firstJsonUrl(node: JSONObject?): String? {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 864 | fun | `extractDouyinCoverUrl` | `private fun extractDouyinCoverUrl(pageUrl: String, html: String): String? {` | Extrae una porción de información desde contenido más amplio, como HTML, JSON, URI o metadata. |
| 866 | fun | `addCandidate` | `fun addCandidate(value: String?, path: String, bonus: Int = 0) {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 877 | fun | `walkJson` | `fun walkJson(node: Any?, path: String = "root", depth: Int = 0) {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 946 | fun | `decodeDouyinEmbeddedJson` | `private fun decodeDouyinEmbeddedJson(value: String): String {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 962 | fun | `extractUrlsFromDouyinValue` | `private fun extractUrlsFromDouyinValue(value: String): List<String> {` | Extrae una porción de información desde contenido más amplio, como HTML, JSON, URI o metadata. |
| 977 | fun | `douyinCoverScore` | `private fun douyinCoverScore(path: String, url: String): Int {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 1011 | fun | `isInstagramRelatedUrl` | `private fun isInstagramRelatedUrl(url: String): Boolean {` | Evalúa una condición y devuelve un resultado booleano. |
| 1016 | fun | `isThreadsRelatedUrl` | `private fun isThreadsRelatedUrl(url: String): Boolean {` | Evalúa una condición y devuelve un resultado booleano. |
| 1021 | fun | `isFacebookRelatedUrl` | `private fun isFacebookRelatedUrl(url: String): Boolean {` | Evalúa una condición y devuelve un resultado booleano. |
| 1027 | fun | `isMetaSocialUrl` | `private fun isMetaSocialUrl(url: String): Boolean = isInstagramRelatedUrl(url) \|\| isThreadsRelatedUrl(url) \|\| isFacebookRelatedUrl(url)` | Evalúa una condición y devuelve un resultado booleano. |
| 1029 | fun | `isRedditRelatedUrl` | `private fun isRedditRelatedUrl(url: String): Boolean {` | Evalúa una condición y devuelve un resultado booleano. |
| 1039 | fun | `fetchMetaOEmbedPreview` | `private fun fetchMetaOEmbedPreview(url: String): LinkPreviewData? {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 1101 | fun | `fetchRedditJsonPreview` | `private fun fetchRedditJsonPreview(entityUrl: String, clickUrl: String): LinkPreviewData? {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 1126 | fun | `addCandidate` | `fun addCandidate(value: String?) {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 1174 | fun | `resolveSimpleRedirect` | `private fun resolveSimpleRedirect(url: String): String? {` | Resuelve una clave/estado a su representación efectiva aplicando reglas y fallbacks. |
| 1196 | fun | `enrichSocialHtmlPreview` | `private fun enrichSocialHtmlPreview(originalUrl: String, pageUrl: String, html: String, base: LinkPreviewData,` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 1218 | fun | `extractSocialImageCandidates` | `private fun extractSocialImageCandidates(pageUrl: String, html: String): List<String> {` | Extrae una porción de información desde contenido más amplio, como HTML, JSON, URI o metadata. |
| 1242 | fun | `looksLikeRemoteImageUrl` | `private fun looksLikeRemoteImageUrl(url: String): Boolean = url.contains(".jpg") \|\| url.contains(".jpeg") \|\| url.contains(".png") \|\|` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 1245 | fun | `scoreSocialImageCandidate` | `private fun scoreSocialImageCandidate(url: String): Int {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 1269 | fun | `isTikTokRelatedUrl` | `private fun isTikTokRelatedUrl(url: String): Boolean {` | Evalúa una condición y devuelve un resultado booleano. |
| 1284 | fun | `fetchTikTokOEmbed` | `private fun fetchTikTokOEmbed(entityUrl: String, clickUrl: String): LinkPreviewData? {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 1329 | fun | `isSpotifyRelatedUrl` | `private fun isSpotifyRelatedUrl(url: String): Boolean {` | Evalúa una condición y devuelve un resultado booleano. |
| 1338 | fun | `findSpotifyEntityUrl` | `private fun findSpotifyEntityUrl(url: String): String? {` | Busca y devuelve el elemento que satisface el criterio implementado. |
| 1356 | fun | `findSpotifyEntityUrlInText` | `private fun findSpotifyEntityUrlInText(text: String): String? {` | Busca y devuelve el elemento que satisface el criterio implementado. |
| 1380 | fun | `normalizeSpotifyEntityUrl` | `private fun normalizeSpotifyEntityUrl(candidate: String): String? {` | Normaliza una entrada para limitarla a los valores/formato admitidos por la aplicación. |
| 1411 | fun | `decodeUrlRepeatedly` | `private fun decodeUrlRepeatedly(value: String): String {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 1427 | fun | `fetchSpotifyOEmbed` | `private fun fetchSpotifyOEmbed(entityUrl: String, clickUrl: String): LinkPreviewData? {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 1483 | composable | `LinkPreviewCard` | `fun LinkPreviewCard(url: String, modifier: Modifier = Modifier, compact: Boolean = false, textColorMode: String = "auto") {` | Composable que construye una parte de la interfaz y reacciona al estado/callbacks recibidos. |
| 1567 | composable | `LinkPreviewText` | `private fun LinkPreviewText(title: String, subtitle: String?, siteName: String, compact: Boolean, veryCompact: Boolean = false,` | Composable que construye una parte de la interfaz y reacciona al estado/callbacks recibidos. |
| 1598 | fun | `openExternalLink` | `private fun openExternalLink(context: Context, url: String) {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 1609 | fun | `readLimitedHtml` | `private fun readLimitedHtml(reader: InputStreamReader): String {` | Lee datos desde la fuente indicada y los transforma al formato usado internamente. |
| 1625 | fun | `charsetFromContentType` | `private fun charsetFromContentType(contentType: String?): Charset {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 1639 | fun | `decodeHtml` | `private fun decodeHtml(value: String): String = Html.fromHtml(value, Html.FROM_HTML_MODE_LEGACY).toString().replace(Regex("\\s+"), " ")` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 1643 | fun | `firstNonBlank` | `private fun firstNonBlank(vararg values: String?): String? = values.firstOrNull {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 1646 | fun | `resolveUrl` | `private fun resolveUrl(baseUrl: String, candidate: String): String? = try {` | Resuelve una clave/estado a su representación efectiva aplicando reglas y fallbacks. |
| 1651 | fun | `safeHost` | `private fun safeHost(url: String): String = try {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 1656 | fun | `directImageUrl` | `private fun directImageUrl(url: String): String? {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 1665 | fun | `fileNameFromUrl` | `private fun fileNameFromUrl(url: String): String = try {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 1670 | fun | `youtubeVideoId` | `private fun youtubeVideoId(url: String): String? = try {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |

## Cómo leer este archivo

1. Empieza por las declaraciones públicas o composables: representan el contrato que usa el resto del proyecto. 2. Después revisa las funciones privadas: normalmente encapsulan normalización, cálculo, rendering o acceso a plataforma. 3. Los comentarios existentes se conservaron durante la compactación y explican decisiones específicas allí donde el código necesita contexto. 4. Consulta las dependencias internas anteriores para seguir el flujo hacia la capa que consume o persiste el resultado.

## Garantía de la compactación

En este archivo no se cambiaron identificadores, literales, operadores, comentarios, llamadas ni orden de tokens. La reducción proviene exclusivamente de compactar espacios/saltos de línea seguros. El proyecto fue comparado mediante el lexer de Kotlin y validado sintácticamente con PSI después del cambio.

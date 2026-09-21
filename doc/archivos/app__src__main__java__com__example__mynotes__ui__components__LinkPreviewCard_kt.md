# LinkPreviewCard.kt — documentación del código actual
**Ruta real:** `app/src/main/java/com/example/mynotes/ui/components/LinkPreviewCard.kt`  **SHA-256:** `64f3f5dee465d1145cb6bbfb068fcea49b55e020235c7962813e0a0d25caa0c3`  **Líneas:** 1781 · **Bytes:** 87209 · **Imports:** 68 · **Declaraciones detectadas:** 82
> Documento generado fuera de `app/` a partir de lectura del código. El fuente es la única fuente de verdad; no se modificó para generar esta documentación.
## 1. Responsabilidad

Tarjeta de vista previa de URLs con metadatos y miniaturas.
## 2. Package e imports

Package declarado: `com.example.mynotes.ui.components`.

### Android / Jetpack / Compose

`android.content.Context`, `android.content.Intent`, `android.net.Uri`, `android.os.Build`, `android.text.Html`, `android.util.LruCache`, `androidx.compose.foundation.layout.Arrangement`, `androidx.compose.foundation.layout.BoxWithConstraints`, `androidx.compose.foundation.layout.Box`, `androidx.compose.foundation.layout.Column`, `androidx.compose.foundation.layout.Row`, `androidx.compose.foundation.layout.Spacer`, `androidx.compose.foundation.layout.fillMaxSize`, `androidx.compose.foundation.layout.fillMaxWidth`, `androidx.compose.foundation.layout.aspectRatio`, `androidx.compose.foundation.layout.height`, `androidx.compose.foundation.layout.padding`, `androidx.compose.foundation.layout.size`, `androidx.compose.foundation.layout.width`, `androidx.compose.material.icons.Icons`, `androidx.compose.material.icons.filled.OpenInNew`, `androidx.compose.material3.Icon`, `androidx.compose.material3.MaterialTheme`, `androidx.compose.material3.Surface`, `androidx.compose.material3.Text`, `androidx.compose.runtime.Composable`, `androidx.compose.runtime.LaunchedEffect`, `androidx.compose.runtime.getValue`, `androidx.compose.runtime.mutableStateOf`, `androidx.compose.runtime.remember`, `androidx.compose.runtime.setValue`, `androidx.compose.ui.Alignment`, `androidx.compose.ui.graphics.Color`, `androidx.compose.ui.Modifier`, `androidx.compose.ui.layout.ContentScale`, `androidx.compose.ui.platform.LocalContext`, `androidx.compose.ui.text.font.FontWeight`, `androidx.compose.ui.text.style.TextOverflow`, `androidx.compose.ui.unit.dp`

### Proyecto MyNotes

`com.example.mynotes.ui.theme.resolveUiTextColor`, `com.example.mynotes.ui.theme.resolveSecondaryUiTextColor`, `com.example.mynotes.ui.theme.resolveUiGraphicColor`, `com.example.mynotes.ui.sound.UiActionSound`, `com.example.mynotes.ui.sound.UiSoundPlayer`

### Kotlin / Coroutines / Java

`kotlinx.coroutines.Dispatchers`, `kotlinx.coroutines.async`, `kotlinx.coroutines.awaitAll`, `kotlinx.coroutines.coroutineScope`, `kotlinx.coroutines.delay`, `kotlinx.coroutines.withContext`, `kotlinx.coroutines.sync.Mutex`, `kotlinx.coroutines.sync.Semaphore`, `kotlinx.coroutines.sync.withLock`, `kotlinx.coroutines.sync.withPermit`, `java.io.File`, `java.io.FileOutputStream`, `java.io.InputStreamReader`, `java.net.HttpURLConnection`, `java.net.URI`, `java.net.URL`, `java.net.URLDecoder`, `java.net.URLEncoder`, `java.nio.charset.Charset`, `java.security.MessageDigest`, `java.util.Locale`

### Terceros / otros

`coil3.compose.AsyncImage`, `org.json.JSONArray`, `org.json.JSONObject`

## 3. Declaraciones detectadas

| Línea | Tipo | Nombre | Firma/inicio |
|---:|---|---|---|
| 85 | `fun` | `extractLinkUrls` | `fun extractLinkUrls(text: String): List<String> = UrlRegex.findAll(text).map { match -> match.value.trimEnd('.', ',', ';', '!', ')', ']',` |
| 99 | `fun` | `extractEmbeddedLinkUrls` | `` |
| 104 | `fun` | `stripEmbeddedLinkMetadata` | `` |
| 109 | `fun` | `noteContentForStorage` | `` |
| 126 | `fun` | `noteTextForDisplay` | `fun noteTextForDisplay(content: String, links: List<String>): String {` |
| 134 | `class` | `LinkPreviewData` | `` |
| 138 | `fun` | `basic` | `fun basic(url: String): LinkPreviewData {` |
| 145 | `object` | `LinkPreviewRepository` | `` |
| 157 | `fun` | `peekMemory` | `fun peekMemory(url: String): LinkPreviewData? = cache.get(url)` |
| 158 | `fun` | `lockFor` | `private fun lockFor(url: String): Mutex = synchronized(locks) {` |
| 161 | `fun` | `peek` | `fun peek(context: Context, url: String): LinkPreviewData? {` |
| 198 | `fun` | `fetch` | `private fun fetch(url: String): LinkPreviewData {` |
| 336 | `fun` | `previewUserAgent` | `` |
| 343 | `fun` | `douyinUserAgent` | `private fun douyinUserAgent(): String = "Mozilla/5.0 (iPhone; CPU iPhone OS 17_5 like Mac OS X) " +` |
| 345 | `fun` | `previewCachePreferences` | `private fun previewCachePreferences(context: Context) = context.getSharedPreferences("link_preview_cache_v$PREVIEW_CACHE_VERSION",` |
| 347 | `fun` | `previewCacheKey` | `private fun previewCacheKey(url: String): String = "preview_" + sha256(url)` |
| 348 | `fun` | `persistPreview` | `` |
| 357 | `fun` | `readPersistedPreview` | `` |
| 383 | `fun` | `JSONObject` | `` |
| 388 | `fun` | `ensureThumbnailCached` | `` |
| 418 | `fun` | `downloadThumbnail` | `` |
| 479 | `fun` | `looksLikeImageFile` | `` |
| 497 | `fun` | `providerReferer` | `` |
| 510 | `fun` | `pruneThumbnailCache` | `` |
| 521 | `fun` | `sha256` | `` |
| 527 | `fun` | `parseHtml` | `` |
| 569 | `fun` | `isDouyinRelatedUrl` | `private fun isDouyinRelatedUrl(url: String): Boolean {` |
| 577 | `class` | `ResolvedDouyinLink` | `` |
| 585 | `fun` | `resolveDouyinLink` | `private fun resolveDouyinLink(url: String): ResolvedDouyinLink? {` |
| 646 | `fun` | `fetchDouyinDirectPreview` | `` |
| 657 | `fun` | `enrichDouyinPreview` | `` |
| 679 | `fun` | `extractDouyinAwemeId` | `` |
| 695 | `fun` | `fetchDouyinSharePagePreview` | `` |
| 731 | `fun` | `extractDouyinRouterData` | `` |
| 758 | `fun` | `scanBalancedJsonObject` | `` |
| 787 | `fun` | `scanJsonStringLiteral` | `` |
| 801 | `fun` | `findDouyinItem` | `` |
| 830 | `fun` | `allJsonUrls` | `` |
| 844 | `fun` | `douyinPreviewFromItem` | `` |
| 871 | `fun` | `fetchDouyinItemInfo` | `` |
| 922 | `fun` | `firstJsonUrl` | `` |
| 934 | `fun` | `extractDouyinCoverUrl` | `` |
| 937 | `fun` | `addCandidate` | `fun addCandidate(value: String?, path: String, bonus: Int = 0) {` |
| 948 | `fun` | `walkJson` | `fun walkJson(node: Any?, path: String = "root", depth: Int = 0) {` |
| 1016 | `fun` | `decodeDouyinEmbeddedJson` | `` |
| 1032 | `fun` | `extractUrlsFromDouyinValue` | `` |
| 1047 | `fun` | `douyinCoverScore` | `` |
| 1081 | `fun` | `isInstagramRelatedUrl` | `` |
| 1086 | `fun` | `isThreadsRelatedUrl` | `` |
| 1091 | `fun` | `isFacebookRelatedUrl` | `` |
| 1097 | `fun` | `isMetaSocialUrl` | `` |
| 1099 | `fun` | `isRedditRelatedUrl` | `` |
| 1110 | `fun` | `fetchMetaOEmbedPreview` | `private fun fetchMetaOEmbedPreview(url: String): LinkPreviewData? {` |
| 1172 | `fun` | `fetchRedditJsonPreview` | `private fun fetchRedditJsonPreview(entityUrl: String, clickUrl: String): LinkPreviewData? {` |
| 1197 | `fun` | `addCandidate` | `fun addCandidate(value: String?) {` |
| 1244 | `fun` | `resolveSimpleRedirect` | `` |
| 1266 | `fun` | `enrichSocialHtmlPreview` | `` |
| 1289 | `fun` | `extractSocialImageCandidates` | `private fun extractSocialImageCandidates(pageUrl: String, html: String): List<String> {` |
| 1312 | `fun` | `looksLikeRemoteImageUrl` | `` |
| 1316 | `fun` | `scoreSocialImageCandidate` | `private fun scoreSocialImageCandidate(url: String): Int {` |
| 1339 | `fun` | `isTikTokRelatedUrl` | `` |
| 1355 | `fun` | `fetchTikTokOEmbed` | `private fun fetchTikTokOEmbed(entityUrl: String, clickUrl: String): LinkPreviewData? {` |
| 1400 | `fun` | `isSpotifyRelatedUrl` | `private fun isSpotifyRelatedUrl(url: String): Boolean {` |
| 1408 | `fun` | `findSpotifyEntityUrl` | `` |
| 1426 | `fun` | `findSpotifyEntityUrlInText` | `` |
| 1450 | `fun` | `normalizeSpotifyEntityUrl` | `` |
| 1481 | `fun` | `decodeUrlRepeatedly` | `` |
| 1497 | `fun` | `fetchSpotifyOEmbed` | `` |
| 1533 | `fun` | `LinkPreviewData` | `` |
| 1552 | `fun` | `LinkPreviewCard` | `` |
| 1655 | `fun` | `LinkPreviewText` | `` |
| 1687 | `fun` | `openExternalLink` | `` |
| 1698 | `fun` | `readLimitedHtml` | `` |
| 1714 | `fun` | `charsetFromContentType` | `` |
| 1728 | `fun` | `decodeHtml` | `` |
| 1731 | `fun` | `String` | `private fun String.cleanText(): String = decodeHtml(this).take(500)` |
| 1732 | `fun` | `firstNonBlank` | `` |
| 1736 | `fun` | `resolveUrl` | `private fun resolveUrl(baseUrl: String, candidate: String): String? = try {` |
| 1741 | `fun` | `safeHost` | `private fun safeHost(url: String): String = try {` |
| 1746 | `fun` | `directImageUrl` | `private fun directImageUrl(url: String): String? {` |
| 1754 | `fun` | `fileNameFromUrl` | `` |
| 1760 | `fun` | `youtubeVideoId` | `private fun youtubeVideoId(url: String): String? = try {` |

## 4. Estado, efectos y límites observables

- **Compose state:** 4 aparición/apariciones.
- **LaunchedEffect/DisposableEffect:** 2 aparición/apariciones.
- **Coroutines:** 6 aparición/apariciones.
- **Room:** 1 aparición/apariciones.
- **I/O/red:** 45 aparición/apariciones.
- **try/catch:** 56 aparición/apariciones.
- **coerce*:** 2 aparición/apariciones.
- **safe calls:** 176 aparición/apariciones.

Estas cifras son indicadores de superficie de cambio, no diagnósticos de error. Cualquier modificación debe preservar contratos de persistencia, lifecycle, límites numéricos y nulabilidad visibles en el fuente.

## 5. Dependencias internas directas

- `com.example.mynotes.ui.sound.UiActionSound`
- `com.example.mynotes.ui.sound.UiSoundPlayer`
- `com.example.mynotes.ui.theme.resolveSecondaryUiTextColor`
- `com.example.mynotes.ui.theme.resolveUiGraphicColor`
- `com.example.mynotes.ui.theme.resolveUiTextColor`

## 6. Recursos Android referenciados

No se detectaron referencias `R.*` directas.

## 7. Puntos de revisión al modificarlo

- No degradar calidad, rutas persistentes ni cachés de adjuntos/miniaturas sin una prueba explícita.
- Evitar trabajo de red/decodificación en el frame de scroll y conservar caché/placeholder.

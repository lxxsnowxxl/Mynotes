# AttachmentViewerActivity.kt

**Ruta:** `app/src/main/java/com/example/mynotes/ui/AttachmentViewerActivity.kt`  
**Paquete:** `com.example.mynotes.ui`  
**Líneas:** 2825 → 1267 (55.2% menos)

## Responsabilidad

Activity especializada en abrir y visualizar adjuntos. Decide el tratamiento según tipo/MIME y prepara la experiencia para imagen, video, audio, PDF, texto, Office u otros archivos soportados.

## Papel dentro de la arquitectura

Se separa de MainActivity porque el visor tiene requisitos propios de ventana, Media3, carga de documentos, controles y rendimiento. También reutiliza DisplayPerformanceController.

## Flujo funcional principal

Flujo típico: recibe URI/ruta/MIME -> clasifica el archivo -> selecciona visor especializado -> configura controles y modo de pantalla -> libera recursos del reproductor/visor según el ciclo de vida.

## Dependencias y relaciones

**Dependencias internas de MyNotes:** `com.example.mynotes.R`, `com.example.mynotes.performance.DisplayPerformanceController`, `com.example.mynotes.ui.sound.UiActionSound`, `com.example.mynotes.ui.sound.UiSoundPlayer`, `com.example.mynotes.ui.theme.MyNotesTheme`, `com.example.mynotes.viewmodel.SettingsViewModel`.

**Compose:** `androidx.compose.foundation.Image`, `androidx.compose.foundation.background`, `androidx.compose.foundation.layout.Arrangement`, `androidx.compose.foundation.layout.Box`, `androidx.compose.foundation.layout.BoxWithConstraints`, `androidx.compose.foundation.layout.Column`, `androidx.compose.foundation.layout.Row`, `androidx.compose.foundation.layout.Spacer`, `androidx.compose.foundation.layout.fillMaxSize`, `androidx.compose.foundation.layout.fillMaxWidth`, `androidx.compose.foundation.layout.height`, `androidx.compose.foundation.layout.padding`, `androidx.compose.foundation.layout.size`, `androidx.compose.foundation.lazy.LazyColumn`, `androidx.compose.foundation.lazy.items`, `androidx.compose.foundation.rememberScrollState`, `androidx.compose.foundation.shape.RoundedCornerShape`, `androidx.compose.foundation.verticalScroll`….

**Android/Jetpack:** `android.app.Activity`, `android.app.ActivityManager`, `android.content.ActivityNotFoundException`, `android.content.Context`, `android.content.Intent`, `android.graphics.Bitmap`, `android.graphics.Canvas`, `android.graphics.Color as AndroidColor`, `android.graphics.pdf.PdfRenderer`, `android.net.Uri`, `android.os.Build`, `android.os.Bundle`, `android.os.ParcelFileDescriptor`, `android.text.Html`, `android.webkit.MimeTypeMap`, `android.widget.Toast`, `androidx.activity.ComponentActivity`, `androidx.activity.compose.setContent`….

**Bibliotecas externas:** `coil3.compose.AsyncImage`, `coil3.request.CachePolicy`, `coil3.request.ImageRequest`, `coil3.request.allowHardware`, `coil3.request.maxBitmapSize`, `coil3.size.Precision`, `coil3.size.Scale`, `coil3.size.Size`.

**Kotlin/Java/corrutinas:** `kotlinx.coroutines.Dispatchers`, `kotlinx.coroutines.delay`, `kotlinx.coroutines.launch`, `kotlinx.coroutines.withContext`, `java.io.Closeable`, `java.io.File`, `java.io.InputStream`, `java.util.Locale`, `java.util.zip.ZipInputStream`, `kotlin.math.roundToInt`.

## Declaraciones importantes detectadas

| Línea | Tipo | Nombre | Firma / declaración | Qué hace |
|---:|---|---|---|---|
| 129 | fun | `openAttachmentViewer` | `fun openAttachmentViewer(context: Context, uri: String, type: String, name: String? = null, mimeType: String? = null) {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 138 | class | `AttachmentViewerActivity` | `class AttachmentViewerActivity : ComponentActivity() {` | Clase que encapsula estado y comportamiento de esta parte del sistema. |
| 139 | fun | `applyAndroidNavigationBarPolicy` | `private fun applyAndroidNavigationBarPolicy() {` | Aplica una transformación o configuración sobre el objeto/estado recibido. |
| 158 | fun | `applySystemBarAppearance` | `private fun applySystemBarAppearance(darkMode: Boolean) {` | Aplica una transformación o configuración sobre el objeto/estado recibido. |
| 165 | fun | `onWindowFocusChanged` | `override fun onWindowFocusChanged(hasFocus: Boolean) {` | Manejador de un evento/ciclo de vida que coordina la respuesta correspondiente sin ser la fuente de datos. |
| 171 | fun | `onResume` | `override fun onResume() {` | Manejador de un evento/ciclo de vida que coordina la respuesta correspondiente sin ser la fuente de datos. |
| 176 | fun | `onMultiWindowModeChanged` | `override fun onMultiWindowModeChanged(isInMultiWindowMode: Boolean) {` | Manejador de un evento/ciclo de vida que coordina la respuesta correspondiente sin ser la fuente de datos. |
| 180 | fun | `onDestroy` | `override fun onDestroy() {` | Manejador de un evento/ciclo de vida que coordina la respuesta correspondiente sin ser la fuente de datos. |
| 184 | fun | `onCreate` | `override fun onCreate(savedInstanceState: Bundle?) {` | Inicializa el visor con los datos recibidos, el modo de ventana y la estrategia apropiada para el adjunto. |
| 223 | composable | `AttachmentViewerScreen` | `private fun AttachmentViewerScreen(uriString: String, type: String, name: String?, explicitMimeType: String?, onBack: () -> Unit) {` | Composable que construye una parte de la interfaz y reacciona al estado/callbacks recibidos. |
| 291 | composable | `ImageViewer` | `private fun ImageViewer(uri: Uri, name: String?) {` | Composable que construye una parte de la interfaz y reacciona al estado/callbacks recibidos. |
| 330 | composable | `VideoViewer` | `private fun VideoViewer(uri: Uri, name: String, mimeType: String) {` | Composable que construye una parte de la interfaz y reacciona al estado/callbacks recibidos. |
| 367 | fun | `onPlaybackStateChanged` | `override fun onPlaybackStateChanged(playbackState: Int) {` | Manejador de un evento/ciclo de vida que coordina la respuesta correspondiente sin ser la fuente de datos. |
| 391 | fun | `onIsPlayingChanged` | `override fun onIsPlayingChanged(isPlaying: Boolean) {` | Manejador de un evento/ciclo de vida que coordina la respuesta correspondiente sin ser la fuente de datos. |
| 394 | fun | `onPlayerError` | `override fun onPlayerError(playbackException:` | Manejador de un evento/ciclo de vida que coordina la respuesta correspondiente sin ser la fuente de datos. |
| 545 | composable | `AudioFileViewer` | `private fun AudioFileViewer(uri: Uri, name: String, mimeType: String) {` | Composable que construye una parte de la interfaz y reacciona al estado/callbacks recibidos. |
| 582 | fun | `onPlaybackStateChanged` | `override fun onPlaybackStateChanged(playbackState: Int) {` | Manejador de un evento/ciclo de vida que coordina la respuesta correspondiente sin ser la fuente de datos. |
| 606 | fun | `onIsPlayingChanged` | `override fun onIsPlayingChanged(isPlaying: Boolean) {` | Manejador de un evento/ciclo de vida que coordina la respuesta correspondiente sin ser la fuente de datos. |
| 609 | fun | `onPlayerError` | `override fun onPlayerError(playbackException:` | Manejador de un evento/ciclo de vida que coordina la respuesta correspondiente sin ser la fuente de datos. |
| 759 | composable | `PdfViewer` | `private fun PdfViewer(uri: Uri) {` | Composable que construye una parte de la interfaz y reacciona al estado/callbacks recibidos. |
| 797 | composable | `PdfPage` | `private fun PdfPage(handle: PdfHandle, pageIndex: Int, targetWidth: Int) {` | Composable que construye una parte de la interfaz y reacciona al estado/callbacks recibidos. |
| 833 | class | `PdfHandle` | `private class PdfHandle(private val descriptor: ParcelFileDescriptor, private val renderer: PdfRenderer) : Closeable {` | Clase que encapsula estado y comportamiento de esta parte del sistema. |
| 837 | fun | `renderPage` | `fun renderPage(pageIndex: Int, targetWidth: Int): Bitmap {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 847 | fun | `close` | `override fun close() {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 853 | fun | `openPdfHandle` | `private fun openPdfHandle(context: Context, uri: Uri): PdfHandle {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 854 | fun | `createHandle` | `fun createHandle(descriptor: ParcelFileDescriptor): PdfHandle {` | Construye una nueva instancia/recurso a partir de los parámetros recibidos. |
| 890 | composable | `TextFileViewer` | `private fun TextFileViewer(uri: Uri, extension: String) {` | Composable que construye una parte de la interfaz y reacciona al estado/callbacks recibidos. |
| 915 | composable | `OfficeTextViewer` | `private fun OfficeTextViewer(uri: Uri, extension: String, name: String, mimeType: String) {` | Composable que construye una parte de la interfaz y reacciona al estado/callbacks recibidos. |
| 941 | composable | `GenericFileViewer` | `private fun GenericFileViewer(uri: Uri, name: String, extension: String, mimeType: String) {` | Composable que construye una parte de la interfaz y reacciona al estado/callbacks recibidos. |
| 992 | fun | `copyDocumentToViewerCache` | `private fun copyDocumentToViewerCache(context: Context, uri: Uri, extension: String, prefix: String): File? {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 1014 | fun | `copyVideoToPlaybackCache` | `private fun copyVideoToPlaybackCache(context: Context, uri: Uri): File? {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 1038 | fun | `copyAudioToPlaybackCache` | `private fun copyAudioToPlaybackCache(context: Context, uri: Uri): File? {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 1070 | fun | `readTextPreview` | `private fun readTextPreview(context: Context, uri: Uri): String {` | Lee datos desde la fuente indicada y los transforma al formato usado internamente. |
| 1090 | fun | `extractOfficeText` | `private fun extractOfficeText(context: Context, uri: Uri, extension: String): String {` | Extrae una porción de información desde contenido más amplio, como HTML, JSON, URI o metadata. |
| 1159 | fun | `decodeXml` | `private fun decodeXml(value: String): String {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 1181 | fun | `openInputStream` | `private fun openInputStream(context: Context, uri: Uri): InputStream {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 1193 | fun | `openExternally` | `private fun openExternally(context: Context, uri: Uri, name: String?, mimeType: String) {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 1206 | fun | `toShareableUri` | `private fun toShareableUri(context: Context, uri: Uri): Uri {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 1214 | fun | `resolveMimeType` | `private fun resolveMimeType(context: Context, uri: Uri, name: String?): String {` | Resuelve una clave/estado a su representación efectiva aplicando reglas y fallbacks. |
| 1220 | fun | `fileExtension` | `private fun fileExtension(name: String?, uri: Uri): String {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 1228 | fun | `isTextExtension` | `private fun isTextExtension(extension: String): Boolean {` | Evalúa una condición y devuelve un resultado booleano. |
| 1233 | fun | `resolveFileSize` | `private fun resolveFileSize(context: Context, uri: Uri): Long? {` | Resuelve una clave/estado a su representación efectiva aplicando reglas y fallbacks. |
| 1247 | fun | `formatFileSize` | `private fun formatFileSize(bytes: Long): String {` | Convierte un valor a una representación textual o visual apropiada. |
| 1257 | fun | `formatTime` | `private fun formatTime(milliseconds: Int): String {` | Convierte un valor a una representación textual o visual apropiada. |

## Cómo leer este archivo

1. Empieza por las declaraciones públicas o composables: representan el contrato que usa el resto del proyecto. 2. Después revisa las funciones privadas: normalmente encapsulan normalización, cálculo, rendering o acceso a plataforma. 3. Los comentarios existentes se conservaron durante la compactación y explican decisiones específicas allí donde el código necesita contexto. 4. Consulta las dependencias internas anteriores para seguir el flujo hacia la capa que consume o persiste el resultado.

## Garantía de la compactación

En este archivo no se cambiaron identificadores, literales, operadores, comentarios, llamadas ni orden de tokens. La reducción proviene exclusivamente de compactar espacios/saltos de línea seguros. El proyecto fue comparado mediante el lexer de Kotlin y validado sintácticamente con PSI después del cambio.

# MainActivity.kt

**Ruta:** `app/src/main/java/com/example/mynotes/MainActivity.kt`  
**Paquete:** `com.example.mynotes`  
**Líneas:** 1216 → 670 (44.9% menos)

## Responsabilidad

Punto de entrada principal de la aplicación. Inicializa la experiencia Compose, observa la configuración global, coordina las pantallas principales y conecta la UI con los ViewModel y con las políticas de ventana/rendimiento.

## Papel dentro de la arquitectura

Actúa como orquestador de alto nivel: no sustituye a los repositorios ni a los componentes visuales, sino que ensambla navegación, estado global, callbacks de edición y configuración, y comportamiento de la ventana.

## Flujo funcional principal

Flujo típico: inicializa dependencias y Compose -> observa notas/ajustes -> decide qué pantalla mostrar -> enruta eventos a los ViewModel/Activities -> reaplica comportamiento de ventana/rendimiento en el ciclo de vida.

## Dependencias y relaciones

**Dependencias internas de MyNotes:** `com.example.mynotes.data.Attachment`, `com.example.mynotes.data.Note`, `com.example.mynotes.performance.DisplayPerformanceController`, `com.example.mynotes.ui.NoteDetailScreen`, `com.example.mynotes.ui.NoteEditorScreen`, `com.example.mynotes.ui.NotesScreen`, `com.example.mynotes.ui.motion.AnimatedScreenEntry`, `com.example.mynotes.ui.motion.ConfigurableAnimatedContent`, `com.example.mynotes.ui.SettingsScreen`, `com.example.mynotes.ui.sound.UiSoundPlayer`, `com.example.mynotes.ui.theme.MyNotesTheme`, `com.example.mynotes.viewmodel.NoteViewModel`, `com.example.mynotes.viewmodel.SettingsViewModel`.

**Compose:** `androidx.compose.runtime.LaunchedEffect`, `androidx.compose.runtime.getValue`, `androidx.compose.runtime.mutableStateOf`, `androidx.compose.runtime.remember`, `androidx.compose.runtime.setValue`.

**Android/Jetpack:** `android.content.Context`, `android.content.Intent`, `android.content.res.Configuration`, `android.os.Build`, `android.os.Bundle`, `androidx.activity.ComponentActivity`, `androidx.activity.compose.BackHandler`, `androidx.activity.compose.setContent`, `androidx.activity.enableEdgeToEdge`, `androidx.core.view.ViewCompat`, `androidx.core.view.WindowCompat`, `androidx.core.view.WindowInsetsCompat`, `androidx.core.view.WindowInsetsControllerCompat`, `androidx.lifecycle.compose.collectAsStateWithLifecycle`, `androidx.lifecycle.viewmodel.compose.viewModel`.

**Kotlin/Java/corrutinas:** `java.util.Locale`.

## Declaraciones importantes detectadas

| Línea | Tipo | Nombre | Firma / declaración | Qué hace |
|---:|---|---|---|---|
| 38 | enum class | `AppDestination` | `private enum class AppDestination {` | Conjunto cerrado de valores nominales usados por esta parte del sistema. |
| 42 | data class | `NavigationSnapshot` | `private data class NavigationSnapshot(val destination: AppDestination, val note: Note? = null)` | Modelo de datos que agrupa valores relacionados con esta responsabilidad. |
| 44 | class | `MainActivity` | `class MainActivity : ComponentActivity() {` | Clase que encapsula estado y comportamiento de esta parte del sistema. |
| 64 | fun | `handleIncomingShare` | `private fun handleIncomingShare(incomingIntent: Intent?) {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 75 | fun | `clearPendingShare` | `private fun clearPendingShare() {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 89 | fun | `attachBaseContext` | `override fun attachBaseContext(newBase: Context) {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 99 | fun | `changeAppLanguage` | `private fun changeAppLanguage(language: String) {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 120 | fun | `installImeNavigationBarRecovery` | `private fun installImeNavigationBarRecovery() {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 138 | fun | `applyAndroidNavigationBarPolicy` | `private fun applyAndroidNavigationBarPolicy() {` | Aplica una transformación o configuración sobre el objeto/estado recibido. |
| 164 | fun | `applySystemBarAppearance` | `private fun applySystemBarAppearance(darkMode: Boolean) {` | Aplica una transformación o configuración sobre el objeto/estado recibido. |
| 171 | fun | `onWindowFocusChanged` | `override fun onWindowFocusChanged(hasFocus: Boolean) {` | Manejador de un evento/ciclo de vida que coordina la respuesta correspondiente sin ser la fuente de datos. |
| 177 | fun | `onResume` | `override fun onResume() {` | Manejador de un evento/ciclo de vida que coordina la respuesta correspondiente sin ser la fuente de datos. |
| 187 | fun | `onMultiWindowModeChanged` | `override fun onMultiWindowModeChanged(isInMultiWindowMode: Boolean) {` | Manejador de un evento/ciclo de vida que coordina la respuesta correspondiente sin ser la fuente de datos. |
| 191 | fun | `onDestroy` | `override fun onDestroy() {` | Manejador de un evento/ciclo de vida que coordina la respuesta correspondiente sin ser la fuente de datos. |
| 195 | fun | `onNewIntent` | `override fun onNewIntent(intent: Intent) {` | Manejador de un evento/ciclo de vida que coordina la respuesta correspondiente sin ser la fuente de datos. |
| 200 | fun | `onCreate` | `override fun onCreate(savedInstanceState: Bundle?) {` | Inicializa la Activity, configura la UI Compose y conecta estado/callbacks principales de la aplicación. |

## Cómo leer este archivo

1. Empieza por las declaraciones públicas o composables: representan el contrato que usa el resto del proyecto. 2. Después revisa las funciones privadas: normalmente encapsulan normalización, cálculo, rendering o acceso a plataforma. 3. Los comentarios existentes se conservaron durante la compactación y explican decisiones específicas allí donde el código necesita contexto. 4. Consulta las dependencias internas anteriores para seguir el flujo hacia la capa que consume o persiste el resultado.

## Garantía de la compactación

En este archivo no se cambiaron identificadores, literales, operadores, comentarios, llamadas ni orden de tokens. La reducción proviene exclusivamente de compactar espacios/saltos de línea seguros. El proyecto fue comparado mediante el lexer de Kotlin y validado sintácticamente con PSI después del cambio.

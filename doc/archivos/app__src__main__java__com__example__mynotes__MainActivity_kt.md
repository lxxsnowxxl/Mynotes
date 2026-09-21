# MainActivity.kt — documentación del código actual
**Ruta real:** `app/src/main/java/com/example/mynotes/MainActivity.kt`  **SHA-256:** `521e82c74bbb6f3f0492fdea4a35c1b591afab5cbe43a7bd01bd700eea38690f`  **Líneas:** 1169 · **Bytes:** 53520 · **Imports:** 56 · **Declaraciones detectadas:** 14
> Documento generado fuera de `app/` a partir de lectura del código. El fuente es la única fuente de verdad; no se modificó para generar esta documentación.
## 1. Responsabilidad

Actividad principal, navegación Compose, recepción de intents compartidos y coordinación de pantallas/estado global.
## 2. Package e imports

Package declarado: `com.example.mynotes`.

### Android / Jetpack / Compose

`android.content.Context`, `android.content.Intent`, `android.content.res.Configuration`, `android.media.AudioManager`, `android.os.Build`, `android.os.Bundle`, `android.net.Uri`, `androidx.activity.ComponentActivity`, `androidx.activity.compose.BackHandler`, `androidx.activity.compose.setContent`, `androidx.activity.enableEdgeToEdge`, `androidx.compose.foundation.isSystemInDarkTheme`, `androidx.compose.foundation.layout.Box`, `androidx.compose.foundation.layout.fillMaxSize`, `androidx.compose.ui.Modifier`, `androidx.compose.runtime.LaunchedEffect`, `androidx.compose.runtime.getValue`, `androidx.compose.runtime.mutableIntStateOf`, `androidx.compose.runtime.mutableStateOf`, `androidx.compose.runtime.remember`, `androidx.compose.runtime.setValue`, `androidx.core.view.ViewCompat`, `androidx.core.view.WindowCompat`, `androidx.core.view.WindowInsetsCompat`, `androidx.core.view.WindowInsetsControllerCompat`, `androidx.lifecycle.compose.collectAsStateWithLifecycle`, `androidx.lifecycle.viewmodel.compose.viewModel`

### Proyecto MyNotes

`com.example.mynotes.data.AppDatabase`, `com.example.mynotes.data.Attachment`, `com.example.mynotes.data.Note`, `com.example.mynotes.data.PendingAttachment`, `com.example.mynotes.performance.DisplayPerformanceController`, `com.example.mynotes.reminders.ReminderRepository`, `com.example.mynotes.reminders.ReminderFeedbackPreferences`, `com.example.mynotes.reminders.ReminderReceiver`, `com.example.mynotes.ui.DrawingScreen`, `com.example.mynotes.ui.NoteDetailScreen`, `com.example.mynotes.ui.NoteEditorScreen`, `com.example.mynotes.ui.ReminderScreen`, `com.example.mynotes.ui.NotesScreen`, `com.example.mynotes.ui.DevelopmentInfoScreen`, `com.example.mynotes.ui.SourceCodeInfoScreen`, `com.example.mynotes.ui.components.ConfigurationModeDialog`, `com.example.mynotes.ui.motion.AnimatedScreenEntry`, `com.example.mynotes.ui.motion.ConfigurableAnimatedContent`, `com.example.mynotes.ui.SettingsScreen`, `com.example.mynotes.ui.sound.UiSoundPlayer`, `com.example.mynotes.ui.theme.MyNotesTheme`, `com.example.mynotes.ui.theme.appFontFamily`, `com.example.mynotes.viewmodel.NoteViewModel`, `com.example.mynotes.viewmodel.SettingsViewModel`, `com.example.mynotes.widget.WidgetActions`, `com.example.mynotes.widget.MyNotesWidgetUpdater`

### Kotlin / Coroutines / Java

`kotlinx.coroutines.Dispatchers`, `kotlinx.coroutines.withContext`, `java.util.Locale`

## 3. Declaraciones detectadas

| Línea | Tipo | Nombre | Firma/inicio |
|---:|---|---|---|
| 59 | `class` | `AppDestination` | `` |
| 63 | `class` | `NavigationSnapshot` | `` |
| 65 | `class` | `MainActivity` | `` |
| 116 | `fun` | `handleReminderIntent` | `` |
| 122 | `fun` | `handleWidgetIntent` | `` |
| 161 | `fun` | `handleIncomingShare` | `private fun handleIncomingShare(incomingIntent: Intent?) {` |
| 172 | `fun` | `clearPendingShare` | `private fun clearPendingShare() {` |
| 203 | `fun` | `changeAppLanguage` | `private fun changeAppLanguage(language: String) {` |
| 233 | `fun` | `setSystemKeyboardSoundSuppressionEnabled` | `private fun setSystemKeyboardSoundSuppressionEnabled(enabled: Boolean) {` |
| 247 | `fun` | `updateSystemKeyboardSoundSuppression` | `private fun updateSystemKeyboardSoundSuppression(isImeVisible: Boolean) {` |
| 282 | `fun` | `restoreSystemSoundStreamIfNeeded` | `private fun restoreSystemSoundStreamIfNeeded() {` |
| 302 | `fun` | `installImeNavigationBarRecovery` | `` |
| 322 | `fun` | `applyAndroidNavigationBarPolicy` | `private fun applyAndroidNavigationBarPolicy() {` |
| 348 | `fun` | `applySystemBarAppearance` | `private fun applySystemBarAppearance(darkMode: Boolean) {` |

## 4. Estado, efectos y límites observables

- **Compose state:** 32 aparición/apariciones.
- **LaunchedEffect/DisposableEffect:** 10 aparición/apariciones.
- **Coroutines:** 2 aparición/apariciones.
- **Room:** 3 aparición/apariciones.
- **try/catch:** 6 aparición/apariciones.
- **safe calls:** 11 aparición/apariciones.

Estas cifras son indicadores de superficie de cambio, no diagnósticos de error. Cualquier modificación debe preservar contratos de persistencia, lifecycle, límites numéricos y nulabilidad visibles en el fuente.

## 5. Dependencias internas directas

- `com.example.mynotes.data.AppDatabase`
- `com.example.mynotes.data.Attachment`
- `com.example.mynotes.data.Note`
- `com.example.mynotes.data.PendingAttachment`
- `com.example.mynotes.performance.DisplayPerformanceController`
- `com.example.mynotes.reminders.ReminderFeedbackPreferences`
- `com.example.mynotes.reminders.ReminderReceiver`
- `com.example.mynotes.reminders.ReminderRepository`
- `com.example.mynotes.ui.DevelopmentInfoScreen`
- `com.example.mynotes.ui.DrawingScreen`
- `com.example.mynotes.ui.NoteDetailScreen`
- `com.example.mynotes.ui.NoteEditorScreen`
- `com.example.mynotes.ui.NotesScreen`
- `com.example.mynotes.ui.ReminderScreen`
- `com.example.mynotes.ui.SettingsScreen`
- `com.example.mynotes.ui.SourceCodeInfoScreen`
- `com.example.mynotes.ui.components.ConfigurationModeDialog`
- `com.example.mynotes.ui.motion.AnimatedScreenEntry`
- `com.example.mynotes.ui.motion.ConfigurableAnimatedContent`
- `com.example.mynotes.ui.sound.UiSoundPlayer`
- `com.example.mynotes.ui.theme.MyNotesTheme`
- `com.example.mynotes.ui.theme.appFontFamily`
- `com.example.mynotes.viewmodel.NoteViewModel`
- `com.example.mynotes.viewmodel.SettingsViewModel`
- `com.example.mynotes.widget.MyNotesWidgetUpdater`
- `com.example.mynotes.widget.WidgetActions`

## 6. Recursos Android referenciados

- **R.string:** `drawing_default_note_title`

## 7. Puntos de revisión al modificarlo

- Conservar rangos `coerce*`, claves DataStore y compatibilidad con backups existentes.
- Revisar navegación, intents externos, modo inmersivo, IME y restauración de barras del sistema.

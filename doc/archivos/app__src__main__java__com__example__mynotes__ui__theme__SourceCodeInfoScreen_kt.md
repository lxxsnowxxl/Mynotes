# SourceCodeInfoScreen.kt — documentación exhaustiva actualizada

**Ruta de código real:** `app/src/main/java/com/example/mynotes/ui/theme/SourceCodeInfoScreen.kt`  
**SHA-256 actual del archivo, sin modificar:** `e6efafe29f85a786f1466ff28afebb392b13a95061b01941183436d65b24c259`  
**Líneas del código real:** 210  
**Estado respecto de la documentación anterior:** **archivo nuevo**

> **Garantía:** este documento vive fuera de `app/`. No se insertó ni eliminó código en el fuente para crear esta explicación. Los fragmentos siguientes son copias de lectura.

## 1. Papel del archivo

Pantalla secundaria que documenta desde la propia app la organización del código, rutas relevantes y repositorio GitHub.

**Cambios recientes cubiertos por esta revisión.** Es un archivo nuevo respecto de la primera documentación obsesiva; sirve como mapa legible del proyecto sin intentar mostrar los fuentes completos dentro del APK.

## 2. Package e imports

El package declarado es `com.example.mynotes.ui`. El package fija el namespace de Kotlin y condiciona cómo se resuelven nombres, visibilidad, imports y referencias desde otros módulos.

El archivo contiene **46 imports**. Se agrupan por responsabilidad:

### Android / Jetpack / Compose

`android.content.Intent`, `android.net.Uri`, `androidx.compose.foundation.BorderStroke`, `androidx.compose.foundation.layout.Column`, `androidx.compose.foundation.layout.Row`, `androidx.compose.foundation.layout.Spacer`, `androidx.compose.foundation.layout.fillMaxSize`, `androidx.compose.foundation.layout.fillMaxWidth`, `androidx.compose.foundation.layout.height`, `androidx.compose.foundation.layout.padding`, `androidx.compose.foundation.layout.size`, `androidx.compose.foundation.layout.widthIn`, `androidx.compose.foundation.rememberScrollState`, `androidx.compose.foundation.shape.RoundedCornerShape`, `androidx.compose.foundation.verticalScroll`, `androidx.compose.material.icons.Icons`, `androidx.compose.material.icons.automirrored.filled.ArrowBack`, `androidx.compose.material.icons.filled.Code`, `androidx.compose.material.icons.filled.OpenInNew`, `androidx.compose.material3.ButtonDefaults`, `androidx.compose.material3.ExperimentalMaterial3Api`, `androidx.compose.material3.Icon`, `androidx.compose.material3.MaterialTheme`, `androidx.compose.material3.Scaffold`, `androidx.compose.material3.Surface`, `androidx.compose.material3.Text`, `androidx.compose.material3.TextButton`, `androidx.compose.material3.TopAppBar`, `androidx.compose.runtime.Composable`, `androidx.compose.runtime.remember`, `androidx.compose.ui.Alignment`, `androidx.compose.ui.Modifier`, `androidx.compose.ui.platform.LocalContext`, `androidx.compose.ui.res.stringResource`, `androidx.compose.ui.text.font.FontWeight`, `androidx.compose.ui.unit.dp`, `androidx.compose.ui.unit.sp`

### Proyecto MyNotes

`com.example.mynotes.R`, `com.example.mynotes.settings.AppSettings`, `com.example.mynotes.ui.motion.AnimatedScreenEntry`, `com.example.mynotes.ui.sound.UiActionSound`, `com.example.mynotes.ui.sound.UiSoundPlayer`, `com.example.mynotes.ui.theme.appFontFamily`, `com.example.mynotes.ui.theme.ensureUiContrast`, `com.example.mynotes.ui.theme.resolveSecondaryUiTextColor`, `com.example.mynotes.ui.theme.resolveUiTextColor`

## 3. Restricciones, límites e invariantes detectables

- **Límites visuales: 2 aparición/apariciones.** evitan crecimiento o reducción de UI fuera de los límites previstos.
- **Estado Compose: 4 aparición/apariciones.** introduce estado observado por Compose y, por tanto, puntos potenciales de recomposición.

Estas apariciones no implican por sí solas un error: son puntos donde el código expresa contratos que deben preservarse al modificarlo.

## 4. Declaraciones y funciones

### 4.1 `SourceCodeInfoScreen` — fun, líneas 61–174

```kotlin
fun SourceCodeInfoScreen(settings: AppSettings, onBack: () -> Unit) {
    val context = LocalContext.current
    val fontFamily = remember(settings.font) { appFontFamily(settings.font) }
    val screenBackground = MaterialTheme.colorScheme.background
    val primaryText = resolveUiTextColor(settings.textColor, screenBackground)
    val secondaryText = resolveSecondaryUiTextColor(settings.textColor, screenBackground)

    AnimatedScreenEntry(animationsEnabled = settings.animationsEnabled, animationSpeed = settings.animationSpeed) {
        Scaffold(containerColor = screenBackground,
            topBar = {
                TopAppBar(title = {
                    Text(text = stringResource(R.string.development_source_title), fontFamily = fontFamily,
                        fontWeight = FontWeight.Bold, fontSize = 24.sp)
                }, navigationIcon = {
                    TextButton(onClick = {
                        UiSoundPlayer.playAction(context = context, action = UiActionSound.Back)
                        onBack()
                    }, colors = ButtonDefaults.textButtonColors(contentColor = primaryText)) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.mock_back), modifier = Modifier.size(25.dp))
                    }
                })
            }) { paddingValues ->
            Column(modifier = Modifier.fillMaxSize().padding(paddingValues).verticalScroll(rememberScrollState())
                .widthIn(max = 840.dp).padding(horizontal = 14.dp, vertical = 16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Code, contentDescription = null, tint = primaryText,
                        modifier = Modifier.size(28.dp))
                    Column(modifier = Modifier.padding(start = 12.dp)) {
                        Text(text = stringResource(R.string.development_source_heading), color = primaryText,
                            fontFamily = fontFamily, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Text(text = stringResource(R.string.development_source_subtitle), color = secondaryText,
                            fontFamily = fontFamily, fontSize = 13.sp)
                    }
                }
                Spacer(Modifier.height(18.dp))

                SourceSection(title = stringResource(R.string.development_repository_section), settings = settings) {
                    SourceCodeRow(path = "GitHub · lxxsnowxxl/Mynotes",
                        description = MYNOTES_SOURCE_REPOSITORY_URL, settings = settings)
                    TextButton(onClick = {
                        UiSoundPlayer.playAction(context = context, action = UiActionSound.Menu)
                        runCatching {
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(MYNOTES_SOURCE_REPOSITORY_URL)))
                        }
                    }, modifier = Modifier.fillMaxWidth()) {
                        Icon(imageVector = Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(18.dp))
                        Text(text = stringResource(R.string.development_repository_open),
                            modifier = Modifier.padding(start = 8.dp), fontFamily = fontFamily, fontWeight = FontWeight.SemiBold)
                    }
                }

                SourceSection(title = stringResource(R.string.development_source_location_section), settings = settings) {
                    SourceCodeRow(path = "app/src/main/java/com/example/mynotes/",
                        description = stringResource(R.string.development_source_location_body), settings = settings)
                }
                SourceSection(title = stringResource(R.string.development_source_core_section), settings = settings) {
                    SourceCodeRow("MainActivity.kt", stringResource(R.string.development_source_main_activity), settings)
                    SourceCodeRow("viewmodel/NoteViewModel.kt", stringResource(R.string.development_source_note_vm), settings)
                    SourceCodeRow("viewmodel/SettingsViewModel.kt", stringResource(R.string.development_source_settings_vm), settings)
                }
                SourceSection(title = stringResource(R.string.development_source_ui_section), settings = settings) {
                    SourceCodeRow("ui/theme/NotesScreen.kt", stringResource(R.string.development_source_notes_screen), settings)
                    SourceCodeRow("ui/theme/NoteEditorScreen.kt", stringResource(R.string.development_source_editor_screen), settings)
                    SourceCodeRow("ui/theme/NoteDetailScreen.kt", stringResource(R.string.development_source_detail_screen), settings)
                    SourceCodeRow("ui/theme/SettingsScreen.kt", stringResource(R.string.development_source_settings_screen), settings)
                    SourceCodeRow("ui/theme/DevelopmentInfoScreen.kt",
                        stringResource(R.string.development_source_development_screen), settings)
                    SourceCodeRow("ui/theme/SourceCodeInfoScreen.kt",
                        stringResource(R.string.development_source_source_screen), settings)
                    SourceCodeRow("ui/components/NoteCard.kt", stringResource(R.string.development_source_note_card), settings)
                    SourceCodeRow("ui/components/LinkPreviewCard.kt",
                        stringResource(R.string.development_source_link_card), settings)
                }
                SourceSection(title = stringResource(R.string.development_source_data_section), settings = settings) {
                    SourceCodeRow("data/AppDatabase.kt", stringResource(R.string.development_source_database), settings)
                    SourceCodeRow("data/NoteDao.kt", stringResource(R.string.development_source_note_dao), settings)
                    SourceCodeRow("data/AttachmentDao.kt", stringResource(R.string.development_source_attachment_dao), settings)
                    SourceCodeRow("data/AppDataBackupManager.kt",
                        stringResource(R.string.development_source_backup_manager), settings)
                    SourceCodeRow("settings/SettingsRepository.kt", stringResource(R.string.development_source_settings_repo), settings)
                    SourceCodeRow("links/LinkPreviewRepository.kt", stringResource(R.string.development_source_link_repo), settings)
                }
                SourceSection(title = stringResource(R.string.development_source_performance_section), settings = settings) {
                    SourceCodeRow("performance/AttachmentPreviewCache.kt",
                        stringResource(R.string.development_source_attachment_cache), settings)
                    SourceCodeRow("performance/DisplayPerformanceController.kt",
                        stringResource(R.string.development_source_display_controller), settings)
                    SourceCodeRow("ui/motion/AppMotion.kt", stringResource(R.string.development_source_motion), settings)
                }
                SourceSection(title = stringResource(R.string.development_source_support_section), settings = settings) {
                    SourceCodeRow("ui/AttachmentViewerActivity.kt",
                        stringResource(R.string.development_source_attachment_viewer), settings)
                    SourceCodeRow("ui/sound/UiSoundPlayer.kt + UiHapticPlayer.kt",
                        stringResource(R.string.development_source_audio_haptics), settings)
                    SourceCodeRow("ui/components/BackupRestoreSection.kt",
                        stringResource(R.string.development_source_backup_restore), settings)
                    SourceCodeRow("ui/theme/PaletteCatalog.kt",
                        stringResource(R.string.development_source_palette_catalog), settings)
                }
                SourceSection(title = stringResource(R.string.development_source_resources_section), settings = settings) {
                    SourceCodeRow("res/layout/", stringResource(R.string.development_source_layouts), settings)
                    SourceCodeRow("res/values*/", stringResource(R.string.development_source_values), settings)
                    SourceCodeRow("AndroidManifest.xml", stringResource(R.string.development_source_manifest), settings)
                    SourceCodeRow("build.gradle.kts", stringResource(R.string.development_source_gradle), settings)
                }
                SourceSection(title = stringResource(R.string.development_source_note_section), settings = settings) {
                    SourceParagraph(text = stringResource(R.string.development_source_note_body), settings = settings)
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}
```

**Firma/entrada.** `fun SourceCodeInfoScreen(settings: AppSettings, onBack: () -> Unit) {`

**Parámetros.**
- `settings: AppSettings` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `onBack: () -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.

**Funcionamiento observable.** participa en la UI declarativa Compose y puede recomponerse al cambiar el estado que lee; integra el pipeline de miniaturas y caché de adjuntos; produce navegación/interacción externa mediante Intent; expone o consume callbacks de interacción.

**Puntos que no conviene romper:** incluye manejo de excepciones; la ruta de error forma parte del contrato, no es un detalle decorativo; captura estado/objetos de composición; sus claves determinan cuándo se reutilizan o reconstruyen.

### 4.2 `SourceSection` — fun, líneas 177–190

```kotlin
private fun SourceSection(title: String, settings: AppSettings, content: @Composable () -> Unit) {
    val background = MaterialTheme.colorScheme.surfaceContainerLow
    val text = resolveUiTextColor(settings.textColor, background)
    val border = ensureUiContrast(MaterialTheme.colorScheme.outline.copy(alpha = 0.55f), background, 2.2f)
    Surface(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp), shape = RoundedCornerShape(18.dp),
        color = background, border = BorderStroke(1.dp, border), tonalElevation = 0.dp) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, color = text, fontFamily = appFontFamily(settings.font),
                fontWeight = FontWeight.Bold, fontSize = 17.sp)
            Spacer(Modifier.height(10.dp))
            content()
        }
    }
}
```

**Firma/entrada.** `private fun SourceSection(title: String, settings: AppSettings, content: @Composable () -> Unit) {`

**Parámetros.**
- `title: String` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `settings: AppSettings` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `content: @Composable () -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.

**Funcionamiento observable.** participa en la UI declarativa Compose y puede recomponerse al cambiar el estado que lee.

### 4.3 `SourceCodeRow` — fun, líneas 193–203

```kotlin
private fun SourceCodeRow(path: String, description: String, settings: AppSettings) {
    val background = MaterialTheme.colorScheme.surfaceContainerLow
    val text = resolveUiTextColor(settings.textColor, background)
    val secondary = resolveSecondaryUiTextColor(settings.textColor, background)
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Text(text = path, color = text, fontFamily = appFontFamily(settings.font),
            fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
        Text(text = description, modifier = Modifier.padding(top = 2.dp), color = secondary,
            fontFamily = appFontFamily(settings.font), fontSize = 12.sp, lineHeight = 17.sp)
    }
}
```

**Firma/entrada.** `private fun SourceCodeRow(path: String, description: String, settings: AppSettings) {`

**Parámetros.**
- `path: String` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `description: String` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `settings: AppSettings` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.

**Funcionamiento observable.** participa en la UI declarativa Compose y puede recomponerse al cambiar el estado que lee.

### 4.4 `SourceParagraph` — fun, líneas 206–210

```kotlin
private fun SourceParagraph(text: String, settings: AppSettings) {
    val background = MaterialTheme.colorScheme.surfaceContainerLow
    val secondary = resolveSecondaryUiTextColor(settings.textColor, background)
    Text(text = text, color = secondary, fontFamily = appFontFamily(settings.font), fontSize = 13.sp, lineHeight = 19.sp)
}
```

**Firma/entrada.** `private fun SourceParagraph(text: String, settings: AppSettings) {`

**Parámetros.**
- `text: String` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `settings: AppSettings` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.

**Funcionamiento observable.** participa en la UI declarativa Compose y puede recomponerse al cambiar el estado que lee.

## 5. Variables y propiedades, una por una

Se detectaron **14 declaraciones `val`/`var`** en la forma léxica principal. La tabla explica mutabilidad, tipo visible/inferido, inicialización y función práctica.

| Línea | Variable | Declaración | Explicación detallada |
|---:|---|---|---|
| 50 | `MYNOTES_SOURCE_REPOSITORY_URL` | `private const val MYNOTES_SOURCE_REPOSITORY_URL: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `private const val MYNOTES_SOURCE_REPOSITORY_URL = "https://github.com/lxxsnowxxl/Mynotes"` |
| 62 | `context` | `local/pública por contexto val context: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val context = LocalContext.current` |
| 63 | `fontFamily` | `local/pública por contexto val fontFamily: inferido` | `val` fija la referencia después de inicializarla; participa en estado Compose; los cambios pueden provocar recomposición de los lectores. **Inicialización visible:** `val fontFamily = remember(settings.font) { appFontFamily(settings.font) }` |
| 64 | `screenBackground` | `local/pública por contexto val screenBackground: inferido` | `val` fija la referencia después de inicializarla; deriva su valor del tema activo, por lo que cambia con paleta/modo. **Inicialización visible:** `val screenBackground = MaterialTheme.colorScheme.background` |
| 65 | `primaryText` | `local/pública por contexto val primaryText: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val primaryText = resolveUiTextColor(settings.textColor, screenBackground)` |
| 66 | `secondaryText` | `local/pública por contexto val secondaryText: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val secondaryText = resolveSecondaryUiTextColor(settings.textColor, screenBackground)` |
| 178 | `background` | `local/pública por contexto val background: inferido` | `val` fija la referencia después de inicializarla; deriva su valor del tema activo, por lo que cambia con paleta/modo. **Inicialización visible:** `val background = MaterialTheme.colorScheme.surfaceContainerLow` |
| 179 | `text` | `local/pública por contexto val text: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val text = resolveUiTextColor(settings.textColor, background)` |
| 180 | `border` | `local/pública por contexto val border: inferido` | `val` fija la referencia después de inicializarla; deriva su valor del tema activo, por lo que cambia con paleta/modo. **Inicialización visible:** `val border = ensureUiContrast(MaterialTheme.colorScheme.outline.copy(alpha = 0.55f), background, 2.2f)` |
| 194 | `background` | `local/pública por contexto val background: inferido` | `val` fija la referencia después de inicializarla; deriva su valor del tema activo, por lo que cambia con paleta/modo. **Inicialización visible:** `val background = MaterialTheme.colorScheme.surfaceContainerLow` |
| 195 | `text` | `local/pública por contexto val text: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val text = resolveUiTextColor(settings.textColor, background)` |
| 196 | `secondary` | `local/pública por contexto val secondary: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val secondary = resolveSecondaryUiTextColor(settings.textColor, background)` |
| 207 | `background` | `local/pública por contexto val background: inferido` | `val` fija la referencia después de inicializarla; deriva su valor del tema activo, por lo que cambia con paleta/modo. **Inicialización visible:** `val background = MaterialTheme.colorScheme.surfaceContainerLow` |
| 208 | `secondary` | `local/pública por contexto val secondary: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val secondary = resolveSecondaryUiTextColor(settings.textColor, background)` |

## 6. Mapa de ámbitos y bloques `{ ... }`

Se documentan **16 bloques estructurales** relevantes. La profundidad indica cuántos ámbitos externos contienen al bloque.

| Inicio–fin | Prof. | Tipo de bloque | Cabecera | Qué implica |
|---|---:|---|---|---|
| 61–174 | 0 | ámbito/lambda anónima | `fun SourceCodeInfoScreen(settings: AppSettings, onBack: () -> Unit) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 63–63 | 1 | `remember` / memoria de composición | `val fontFamily = remember(settings.font) { appFontFamily(settings.font) }` | Conserva el resultado entre recomposiciones mientras las claves no cambien. No es persistencia permanente; su alcance es la composición. |
| 68–173 | 1 | ámbito/lambda anónima | `AnimatedScreenEntry(animationsEnabled = settings.animationsEnabled, animationSpeed = settings.animationSpeed) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 70–83 | 2 | ámbito/lambda anónima | `topBar = {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 71–74 | 3 | ámbito/lambda anónima | `TopAppBar(title = {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 74–82 | 3 | ámbito/lambda anónima | `}, navigationIcon = {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 83–172 | 2 | ámbito/lambda anónima | `}) { paddingValues ->` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 85–171 | 3 | ámbito/lambda anónima | `.widthIn(max = 840.dp).padding(horizontal = 14.dp, vertical = 16.dp)) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 86–95 | 4 | bloque UI Compose | `Row(verticalAlignment = Alignment.CenterVertically) {` | Define una parte del árbol declarativo. Al cambiar estado leído por este ámbito, Compose puede recomponerlo; el bloque debe ser relativamente barato e idempotente. |
| 89–94 | 5 | bloque UI Compose | `Column(modifier = Modifier.padding(start = 12.dp)) {` | Define una parte del árbol declarativo. Al cambiar estado leído por este ámbito, Compose puede recomponerlo; el bloque debe ser relativamente barato e idempotente. |
| 177–190 | 0 | ámbito/lambda anónima | `private fun SourceSection(title: String, settings: AppSettings, content: @Composable () -> Unit) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 182–189 | 1 | ámbito/lambda anónima | `color = background, border = BorderStroke(1.dp, border), tonalElevation = 0.dp) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 183–188 | 2 | bloque UI Compose | `Column(modifier = Modifier.padding(16.dp)) {` | Define una parte del árbol declarativo. Al cambiar estado leído por este ámbito, Compose puede recomponerlo; el bloque debe ser relativamente barato e idempotente. |
| 193–203 | 0 | ámbito/lambda anónima | `private fun SourceCodeRow(path: String, description: String, settings: AppSettings) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 197–202 | 1 | bloque UI Compose | `Column(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {` | Define una parte del árbol declarativo. Al cambiar estado leído por este ámbito, Compose puede recomponerlo; el bloque debe ser relativamente barato e idempotente. |
| 206–210 | 0 | ámbito/lambda anónima | `private fun SourceParagraph(text: String, settings: AppSettings) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |

## 7. Side effects, rendimiento y lifecycle

- **Persistencia / base de datos:** presente. Cualquier refactor futuro debe considerar el coste y el lifecycle del recurso, no solo el resultado visual.
- **Audio / vibración:** presente. Cualquier refactor futuro debe considerar el coste y el lifecycle del recurso, no solo el resultado visual.
- **Recomposición Compose:** presente. Cualquier refactor futuro debe considerar el coste y el lifecycle del recurso, no solo el resultado visual.
- **Navegación / Intents:** presente. Cualquier refactor futuro debe considerar el coste y el lifecycle del recurso, no solo el resultado visual.
- **Decodificación multimedia:** presente. Cualquier refactor futuro debe considerar el coste y el lifecycle del recurso, no solo el resultado visual.

## 8. Relación con los cambios recientes

Esta pantalla no pretende leer los `.kt` desde el APK. Enumera rutas y responsabilidades para orientar al usuario/desarrollador, y enlaza al repositorio donde sí están los fuentes. Esa separación evita asumir que el código Kotlin original permanece empaquetado como texto tras la compilación.

## 9. Regla de mantenimiento

Cualquier modificación futura debería actualizar primero el archivo Kotlin real y después regenerar esta documentación. **No debe editarse el código para que coincida con el documento; el documento es el derivado y el código es la fuente de verdad.**

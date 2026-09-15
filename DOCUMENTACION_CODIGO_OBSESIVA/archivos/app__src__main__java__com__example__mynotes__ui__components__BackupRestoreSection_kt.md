# BackupRestoreSection.kt — documentación exhaustiva por bloques

**Ruta de código real:** `app/src/main/java/com/example/mynotes/ui/components/BackupRestoreSection.kt`  
**SHA-256 del archivo ejecutable sin tocar:** `89e8386c8cf33add82aa9ab4b96f66612c65b0dfbbf62788718af85d5a34f20b`  
**Líneas del código real:** 163

> **Garantía:** esta documentación se genera en una carpeta paralela. El archivo Kotlin/Kotlin DSL anterior no se modifica. Los fragmentos mostrados abajo son copias de lectura; la aplicación compila usando únicamente el archivo original de `app/...`.

## 1. Papel del archivo

Sección de Configuración dedicada a exportar/importar respaldos y a presentar los controles asociados.

**Arquitectura.** Es UI: recibe callbacks/estado y evita colocar toda la interfaz de respaldo directamente dentro de SettingsScreen.

**Flujo general.** El flujo se describe declaración por declaración en este documento.

## 2. Package e imports

El `package` es `com.example.mynotes.ui.components`. Determina el namespace lógico del archivo, resolución de visibilidad `internal`/package-related tooling y la ruta conceptual desde la que otras clases lo importan.

El archivo declara **45 imports**. Cada import evita usar el nombre totalmente calificado en el cuerpo y, además, revela las dependencias técnicas del bloque:

**Android/plataforma:** `android.app.Activity`, `android.net.Uri`, `android.widget.Toast`.

**Jetpack/Compose:** `androidx.activity.compose.rememberLauncherForActivityResult`, `androidx.activity.result.contract.ActivityResultContracts`, `androidx.compose.foundation.layout.Arrangement`, `androidx.compose.foundation.layout.PaddingValues`, `androidx.compose.foundation.layout.Row`, `androidx.compose.foundation.layout.Spacer`, `androidx.compose.foundation.layout.fillMaxWidth`, `androidx.compose.foundation.layout.height`, `androidx.compose.foundation.layout.size`, `androidx.compose.material.icons.Icons`, `androidx.compose.material.icons.filled.Download`, `androidx.compose.material.icons.filled.Upload`, `androidx.compose.material3.CircularProgressIndicator`, `androidx.compose.material3.Icon`, `androidx.compose.material3.OutlinedButton`, `androidx.compose.material3.Text`, `androidx.compose.material3.TextButton`, `androidx.compose.runtime.Composable`, `androidx.compose.runtime.getValue`, `androidx.compose.runtime.mutableStateOf`, `androidx.compose.runtime.remember`, `androidx.compose.runtime.rememberCoroutineScope`, `androidx.compose.runtime.setValue`, `androidx.compose.ui.Alignment`, `androidx.compose.ui.Modifier`, `androidx.compose.ui.graphics.Color`, `androidx.compose.ui.platform.LocalContext`, `androidx.compose.ui.res.stringResource`, `androidx.compose.ui.text.font.FontFamily`, `androidx.compose.ui.text.font.FontWeight`, `androidx.compose.ui.unit.dp`, `androidx.compose.ui.unit.sp`.

**Proyecto MyNotes:** `com.example.mynotes.R`, `com.example.mynotes.ui.components.AppAlertDialog`, `com.example.mynotes.data.AppDataBackupManager`, `com.example.mynotes.settings.AppSettings`, `com.example.mynotes.ui.sound.UiActionSound`, `com.example.mynotes.ui.sound.UiSoundPlayer`.

**Kotlin/corrutinas/Java:** `kotlinx.coroutines.launch`, `java.text.SimpleDateFormat`, `java.util.Date`, `java.util.Locale`.

## 3. Restricciones e invariantes visibles en el archivo

- **Nulabilidad segura (1 aparición/apariciones):** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`.
- **Fallback nulo (2 aparición/apariciones):** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula.
- **Normalización vacía (1 aparición/apariciones):** `orEmpty` transforma una referencia nula en una colección/cadena vacía, simplificando el consumo posterior.

## 4. Bloques de código, uno por uno

### 4.1 `BackupRestoreSection` — fun, líneas 50–163

```kotlin
fun BackupRestoreSection(settings: AppSettings, fontFamily: FontFamily, textColor: Color, secondaryTextColor: Color, graphicColor: Color) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var busy by remember { mutableStateOf(false) }
    var pendingImportUri by remember { mutableStateOf<Uri?>(null) }
    var statusMessage by remember { mutableStateOf<String?>(null) }
    val exportLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.CreateDocument("application/zip")) { uri ->
            if (uri != null && !busy) {
                scope.launch {
                    busy = true
                    statusMessage = null
                    val result = AppDataBackupManager.exportBackup(context = context, destination = uri, settings = settings)
                    result.onSuccess { summary -> statusMessage = context.getString(R.string.backup_export_success, summary.noteCount,
                                    summary.attachmentCount)
                            if (summary.skippedAttachmentCount > 0) {
                                statusMessage += " " + context.getString(R.string.backup_export_skipped, summary.skippedAttachmentCount)
                            }
                        }.onFailure { error -> statusMessage = context.getString(R.string.backup_operation_error,
                                    error.message ?: context.getString(R.string.backup_unknown_error))
                        }
                    busy = false
                }
            }
        }
    val importLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenDocument()) { uri ->
            if (uri != null && !busy) {
                pendingImportUri = uri
            }
        }
    SettingsSectionPanel(textColorMode = settings.textColor, contentPadding = PaddingValues(16.dp)) { panelColors -> Text(
            text = stringResource(R.string.backup_restore_title), color = panelColors.text, fontFamily = fontFamily,
            fontWeight = FontWeight.Bold, fontSize = 17.sp)
        Spacer(Modifier.height(4.dp))
        Text(text = stringResource(R.string.backup_restore_description), color = panelColors.secondaryText, fontFamily = fontFamily,
            fontSize = 12.sp, lineHeight = 17.sp)
        Spacer(Modifier.height(14.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedButton(onClick = {
                    UiSoundPlayer.playAction(context = context, action = UiActionSound.Backup)
                    val stamp = SimpleDateFormat("yyyy-MM-dd_HH-mm", Locale.US).format(Date())
                    exportLauncher.launch("MyNotes_backup_$stamp.zip")
                }, enabled = !busy, modifier = Modifier.weight(1f)) {
                Icon(imageVector = Icons.Default.Upload, contentDescription = null, modifier = Modifier.size(18.dp), tint = panelColors.text
                )
                Spacer(Modifier.size(7.dp))
                Text(text = stringResource(R.string.backup_export_button), color = panelColors.text, fontFamily = fontFamily)
            }
            OutlinedButton(onClick = {
                    UiSoundPlayer.playAction(context = context, action = UiActionSound.Restore)
                    importLauncher.launch(arrayOf("application/zip", "application/octet-stream", "application/x-zip-compressed"))
                }, enabled = !busy, modifier = Modifier.weight(1f)) {
                Icon(imageVector = Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp),
                    tint = panelColors.text)
                Spacer(Modifier.size(7.dp))
                Text(text = stringResource(R.string.backup_import_button), color = panelColors.text, fontFamily = fontFamily)
            }
        }
        if (busy) {
            Spacer(Modifier.height(14.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = panelColors.text)
                Spacer(Modifier.size(9.dp))
                Text(text = stringResource(R.string.backup_processing), color = panelColors.secondaryText, fontFamily = fontFamily,
                    fontSize = 12.sp)
            }
        } else if (!statusMessage.isNullOrBlank()) {
            Spacer(Modifier.height(12.dp))
            Text(text = statusMessage.orEmpty(), color = panelColors.secondaryText, fontFamily = fontFamily, fontSize = 12.sp,
                lineHeight = 16.sp)
        }
        }
    val importUri = pendingImportUri
    if (importUri != null) {
        AppAlertDialog(onDismissRequest = {
                if (!busy) pendingImportUri = null
            }, title = {
                Text(text = stringResource(R.string.backup_import_confirm_title), fontFamily = fontFamily)
            }, text = {
                Text(text = stringResource(R.string.backup_import_confirm_message), fontFamily = fontFamily)
            }, confirmButton = {
                TextButton(onClick = {
                        UiSoundPlayer.playAction(context = context, action = UiActionSound.Confirm)
                        pendingImportUri = null
                        scope.launch {
                            busy = true
                            statusMessage = null
                            val result = AppDataBackupManager.importBackup(context = context, source = importUri)
                            result.onSuccess { summary -> val message = context.getString(R.string.backup_import_success, summary.noteCount,
                                            summary.attachmentCount)
                                    statusMessage = message
                                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                                    /*
                                     * También vuelve a leer el idioma guardado
                                     * por MainActivity.attachBaseContext().
                                     */
                                    (context as? Activity)?.recreate()
                                }.onFailure { error -> statusMessage = context.getString(R.string.backup_operation_error,
                                            error.message ?: context.getString(R.string.backup_unknown_error))
                                }
                            busy = false
                        }
                    }) {
                    Text(stringResource(R.string.backup_import_confirm_action))
                }
            }, dismissButton = {
                TextButton(onClick = {
                        UiSoundPlayer.playAction(context = context, action = UiActionSound.Cancel)
                        pendingImportUri = null
                    }) {
                    Text(stringResource(R.string.backup_cancel))
                }
            })
    }
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `settings: AppSettings` — `settings` recibe un valor de tipo `AppSettings`. El contrato no marca este parámetro como anulable.
- `fontFamily: FontFamily` — `fontFamily` recibe un valor de tipo `FontFamily`. El contrato no marca este parámetro como anulable.
- `textColor: Color` — `textColor` recibe un valor de tipo `Color`. El contrato no marca este parámetro como anulable.
- `secondaryTextColor: Color` — `secondaryTextColor` recibe un valor de tipo `Color`. El contrato no marca este parámetro como anulable.
- `graphicColor: Color` — `graphicColor` recibe un valor de tipo `Color`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `@Composable` convierte la función en una unidad declarativa de Compose y la somete a reglas de recomposición.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 51 | `val context` | `inferido` | `LocalContext.current` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El `Context` da acceso a recursos/servicios/almacenamiento Android; debe respetarse el ciclo de vida del contexto recibido. |
| 52 | `val scope` | `inferido` | `rememberCoroutineScope()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 53 | `var busy` | `inferido` | `by remember { mutableStateOf(false) }` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Es estado observable de Compose: una escritura puede invalidar la composición y provocar que la UI dependiente se recomponga. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 54 | `var pendingImportUri` | `inferido` | `by remember { mutableStateOf<Uri?>(null) }` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Es estado observable de Compose: una escritura puede invalidar la composición y provocar que la UI dependiente se recomponga. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Representa una ubicación Android `Uri`; no debe asumirse que siempre corresponde a una ruta de archivo convencional. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 55 | `var statusMessage` | `inferido` | `by remember { mutableStateOf<String?>(null) }` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Es estado observable de Compose: una escritura puede invalidar la composición y provocar que la UI dependiente se recomponga. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 56 | `val exportLauncher` | `inferido` | `rememberLauncherForActivityResult(contract = ActivityResultContracts.CreateDocument("application/zip…` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 61 | `val result` | `inferido` | `AppDataBackupManager.exportBackup(context = context, destination = uri, settings = settings)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 74 | `val importLauncher` | `inferido` | `rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenDocument()) { uri ->` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 89 | `val stamp` | `inferido` | `SimpleDateFormat("yyyy-MM-dd_HH-mm", Locale.US).format(Date())` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 121 | `val importUri` | `inferido` | `pendingImportUri` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Representa una ubicación Android `Uri`; no debe asumirse que siempre corresponde a una ruta de archivo convencional. |
| 136 | `val result` | `inferido` | `AppDataBackupManager.importBackup(context = context, source = importUri)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 57 | `if (uri != null && !busy) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 64 | `if (summary.skippedAttachmentCount > 0) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 75 | `if (uri != null && !busy) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 107 | `if (busy) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 122 | `if (importUri != null) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 124 | `if (!busy) pendingImportUri = null` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 1.
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 2.
- **Normalización vacía:** `orEmpty` transforma una referencia nula en una colección/cadena vacía, simplificando el consumo posterior. Apariciones en este bloque: 1.

#### Semántica Compose/lifecycle

- **`remember`:** Conserva valores entre recomposiciones bajo las mismas claves.
- **`mutableStateOf`:** Crea estado observable; las lecturas registradas por Compose quedan enlazadas a sus cambios.
- **Callbacks `onClick`:** La lógica asociada se ejecuta por evento de usuario, no durante la composición inicial.
- **`Modifier`:** La cadena de modificadores define tamaño, interacción, dibujo, semántica o posicionamiento; el orden de los modificadores puede ser significativo.

#### Efectos secundarios y recursos

- **Estado Compose:** Declara o utiliza estado observable de Compose; una escritura en ese estado puede desencadenar recomposición de los lectores.
- **Navegación/Activity:** Modifica navegación, ciclo de vida o contenido de una Activity.
- **Sonido/háptica:** Produce feedback perceptible para el usuario; debe ejecutarse en respuesta a la interacción/estado correspondiente.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `rememberCoroutineScope`, `mutableStateOf`, `rememberLauncherForActivityResult`, `ActivityResultContracts.CreateDocument`, `AppDataBackupManager.exportBackup`, `context.getString`, `ActivityResultContracts.OpenDocument`, `SettingsSectionPanel`, `PaddingValues`, `Text`, `stringResource`, `Spacer`, `Modifier.height`, `Row`, `Modifier.fillMaxWidth`, `Arrangement.spacedBy`, `OutlinedButton`, `UiSoundPlayer.playAction`, `SimpleDateFormat`, `format`, `Date`, `exportLauncher.launch`, `Modifier.weight`, `Icon`, `Modifier.size`, `importLauncher.launch`, `arrayOf`, `CircularProgressIndicator`, `statusMessage.isNullOrBlank`, `statusMessage.orEmpty`, `AppAlertDialog`, `TextButton`, `AppDataBackupManager.importBackup`, `Toast.makeText`, `show`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- No convertir estado de Compose en variables ordinarias sin considerar recomposición y persistencia temporal.

## 5. Inventario global de propiedades/variables detectadas

| Línea | Identificador | Mutabilidad | Tipo | Explicación |
|---:|---|---|---|---|
| 51 | `context` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El `Context` da acceso a recursos/servicios/almacenamiento Android; debe respetarse el ciclo de vida del contexto recibido. |
| 52 | `scope` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 53 | `busy` | `var` | `inferido` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Es estado observable de Compose: una escritura puede invalidar la composición y provocar que la UI dependiente se recomponga. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 54 | `pendingImportUri` | `var` | `inferido` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Es estado observable de Compose: una escritura puede invalidar la composición y provocar que la UI dependiente se recomponga. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Representa una ubicación Android `Uri`; no debe asumirse que siempre corresponde a una ruta de archivo convencional. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 55 | `statusMessage` | `var` | `inferido` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Es estado observable de Compose: una escritura puede invalidar la composición y provocar que la UI dependiente se recomponga. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 56 | `exportLauncher` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 61 | `result` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 74 | `importLauncher` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 89 | `stamp` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 121 | `importUri` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Representa una ubicación Android `Uri`; no debe asumirse que siempre corresponde a una ruta de archivo convencional. |
| 136 | `result` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

## 6. Mapa exhaustivo de TODOS los bloques delimitados por `{}`

Esta tabla recorre todas las llaves estructurales detectadas fuera de strings/comentarios. Así se documentan también lambdas de Compose, callbacks, iteraciones y ámbitos locales que no tienen un nombre propio de función.

| Inicio–fin | Prof. | Cabecera/contexto | Qué significa ese bloque |
|---|---:|---|---|
| 50–163 | 0 | `fun BackupRestoreSection(settings: AppSettings, fontFamily: FontFamily, textColor: Color, secondaryTextColor: Color, graphicColor: Color)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 53–53 | 1 | `var busy by remember` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 54–54 | 1 | `var pendingImportUri by remember` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 55–55 | 1 | `var statusMessage by remember` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 56–73 | 1 | `val exportLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.CreateDocument("application/zip"))` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 57–72 | 2 | `if (uri != null && !busy)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 58–71 | 3 | `scope.launch` | Nueva corrutina: el bloque se ejecuta asíncronamente dentro del `CoroutineScope` receptor y respeta su cancelación. |
| 62–67 | 4 | `result.onSuccess` | Ámbito delimitado por llaves en profundidad 4. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 64–66 | 5 | `if (summary.skippedAttachmentCount > 0)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 67–69 | 4 | `}.onFailure` | Ámbito delimitado por llaves en profundidad 4. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 74–78 | 1 | `val importLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenDocument())` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 75–77 | 2 | `if (uri != null && !busy)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 79–120 | 1 | `SettingsSectionPanel(textColorMode = settings.textColor, contentPadding = PaddingValues(16.dp))` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 86–106 | 2 | `Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp))` | Lambda de contenido de UI Compose: declara hijos y configuración visual dentro del contenedor. El orden y los `Modifier` influyen en medida, posición, dibujo e interacción. |
| 87–91 | 3 | `OutlinedButton(onClick =` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 91–96 | 3 | `}, enabled = !busy, modifier = Modifier.weight(1f))` | Ámbito delimitado por llaves en profundidad 3. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 97–100 | 3 | `OutlinedButton(onClick =` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 100–105 | 3 | `}, enabled = !busy, modifier = Modifier.weight(1f))` | Ámbito delimitado por llaves en profundidad 3. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 107–115 | 2 | `if (busy)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 109–114 | 3 | `Row(verticalAlignment = Alignment.CenterVertically)` | Lambda de contenido de UI Compose: declara hijos y configuración visual dentro del contenedor. El orden y los `Modifier` influyen en medida, posición, dibujo e interacción. |
| 115–119 | 2 | `} else if (!statusMessage.isNullOrBlank())` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 122–162 | 1 | `if (importUri != null)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 123–125 | 2 | `AppAlertDialog(onDismissRequest =` | Callback de cierre: se ejecuta cuando la UI solicita descartar/cerrar el popup, diálogo o superficie asociada. |
| 125–127 | 2 | `}, title =` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 127–129 | 2 | `}, text =` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 129–154 | 2 | `}, confirmButton =` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 130–151 | 3 | `TextButton(onClick =` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 133–150 | 4 | `scope.launch` | Nueva corrutina: el bloque se ejecuta asíncronamente dentro del `CoroutineScope` receptor y respeta su cancelación. |
| 137–146 | 5 | `result.onSuccess` | Ámbito delimitado por llaves en profundidad 5. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 146–148 | 5 | `}.onFailure` | Ámbito delimitado por llaves en profundidad 5. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 151–153 | 3 | `})` | Ámbito delimitado por llaves en profundidad 3. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 154–161 | 2 | `}, dismissButton =` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 155–158 | 3 | `TextButton(onClick =` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 158–160 | 3 | `})` | Ámbito delimitado por llaves en profundidad 3. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |

## 7. Lectura de seguridad antes de tocar este archivo

La documentación describe **lo que el código actual ya hace**. No constituye una propuesta de refactorización. Si se quisiera cambiar algo en el futuro, primero habría que preservar contratos públicos, claves de configuración, restricciones de API Android, comportamiento de Compose, lifecycle, operaciones de I/O y compatibilidad con los datos ya persistidos. En esta entrega no se hizo ninguna de esas modificaciones.

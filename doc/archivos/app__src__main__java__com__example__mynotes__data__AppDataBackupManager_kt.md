# AppDataBackupManager.kt — documentación exhaustiva por bloques

**Ruta de código real:** `app/src/main/java/com/example/mynotes/data/AppDataBackupManager.kt`  
**SHA-256 del archivo ejecutable sin tocar:** `4d00600b45e0626e9b71dedaa85474ceceb562ab57fb13c10281fed13b10838a`  
**Líneas del código real:** 487

> **Garantía:** esta documentación se genera en una carpeta paralela. El archivo Kotlin/Kotlin DSL anterior no se modifica. Los fragmentos mostrados abajo son copias de lectura; la aplicación compila usando únicamente el archivo original de `app/...`.

## 1. Papel del archivo

Gestiona la exportación e importación de los datos de MyNotes, incluyendo la información persistida de notas, adjuntos y ajustes que forman parte de una copia de seguridad.

**Arquitectura.** Se ubica en la capa de datos porque conoce cómo serializar/restaurar el estado persistente y cómo coordinar archivos asociados sin convertir esa lógica en responsabilidad de la UI.

**Flujo general.** El flujo se describe declaración por declaración en este documento.

## 2. Package e imports

El `package` es `com.example.mynotes.data`. Determina el namespace lógico del archivo, resolución de visibilidad `internal`/package-related tooling y la ruta conceptual desde la que otras clases lo importan.

El archivo declara **16 imports**. Cada import evita usar el nombre totalmente calificado en el cuerpo y, además, revela las dependencias técnicas del bloque:

**Android/plataforma:** `android.content.Context`, `android.net.Uri`.

**Jetpack/Compose:** `androidx.room.withTransaction`.

**Proyecto MyNotes:** `com.example.mynotes.settings.AppSettings`, `com.example.mynotes.settings.SettingsRepository`.

**Kotlin/corrutinas/Java:** `kotlinx.coroutines.Dispatchers`, `kotlinx.coroutines.withContext`, `java.io.File`, `java.io.FileInputStream`, `java.io.InputStream`, `java.util.UUID`, `java.util.zip.ZipEntry`, `java.util.zip.ZipInputStream`, `java.util.zip.ZipOutputStream`.

**Otras librerías:** `org.json.JSONArray`, `org.json.JSONObject`.

## 3. Restricciones e invariantes visibles en el archivo

- **Nulabilidad segura (7 aparición/apariciones):** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`.
- **Fallback nulo (11 aparición/apariciones):** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula.
- **Límite numérico (2 aparición/apariciones):** Se fuerza el valor a un intervalo o umbral para impedir que salgan valores fuera de los límites esperados.
- **Filtro condicional (3 aparición/apariciones):** `takeIf` conserva el valor únicamente cuando el predicado se cumple; en caso contrario lo convierte en `null`.
- **Trabajo IO (2 aparición/apariciones):** El trabajo potencialmente bloqueante se desplaza al dispatcher de I/O en vez de ocupar el hilo principal.

## 4. Bloques de código, uno por uno

### 4.1 `AppDataBackupManager` — object, líneas 33–487

```kotlin
object AppDataBackupManager {
    // … el cuerpo completo permanece en el archivo real; sus miembros se documentan individualmente abajo …
}
```

#### Qué hace y por qué existe

Gestiona la exportación e importación de los datos de MyNotes, incluyendo la información persistida de notas, adjuntos y ajustes que forman parte de una copia de seguridad.

#### Contrato de la declaración


#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 34 | `val FORMAT_NAME` | `inferido` | `"mynotes-backup"` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Está marcada `const`, por lo que es una constante de tiempo de compilación y solo puede usar un tipo/valor admitido por Kotlin para constantes. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 35 | `val FORMAT_VERSION` | `inferido` | `1` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Está marcada `const`, por lo que es una constante de tiempo de compilación y solo puede usar un tipo/valor admitido por Kotlin para constantes. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 36 | `val DATA_ENTRY` | `inferido` | `"data.json"` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Está marcada `const`, por lo que es una constante de tiempo de compilación y solo puede usar un tipo/valor admitido por Kotlin para constantes. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 37 | `val ATTACHMENTS_PREFIX` | `inferido` | `"attachments/"` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Está marcada `const`, por lo que es una constante de tiempo de compilación y solo puede usar un tipo/valor admitido por Kotlin para constantes. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 38 | `val PROFILE_PREFIX` | `inferido` | `"profile/"` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Está marcada `const`, por lo que es una constante de tiempo de compilación y solo puede usar un tipo/valor admitido por Kotlin para constantes. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 45 | `val MAX_BACKUP_ENTRIES` | `inferido` | `20_000` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Está marcada `const`, por lo que es una constante de tiempo de compilación y solo puede usar un tipo/valor admitido por Kotlin para constantes. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 46 | `val MAX_METADATA_BYTES` | `inferido` | `64L * 1024L * 1024L` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Está marcada `const`, por lo que es una constante de tiempo de compilación y solo puede usar un tipo/valor admitido por Kotlin para constantes. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 51 | `val appContext` | `inferido` | `context.applicationContext` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El `Context` da acceso a recursos/servicios/almacenamiento Android; debe respetarse el ciclo de vida del contexto recibido. |
| 52 | `val database` | `inferido` | `AppDatabase.getDatabase(appContext)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 53 | `val notes` | `inferido` | `database.noteDao().getAllNotesOnce()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 54 | `val attachments` | `inferido` | `database.attachmentDao().getAllAttachmentsOnce()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 55 | `val output` | `inferido` | `appContext.contentResolver.openOutputStream(destination, "w"` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 58 | `val attachmentJson` | `inferido` | `JSONArray()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 59 | `var exportedAttachments` | `inferido` | `0` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 60 | `var skippedAttachments` | `inferido` | `0` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 62 | `val entryName` | `inferido` | `"$ATTACHMENTS_PREFIX${attachment.id}_$safeName"` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 63 | `val copied` | `inferido` | `writeUriEntry(context = appContext, zip = zip, source = Uri.parse(attachment.uri),` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 72 | `var profileEntry` | `String?` | `null` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo declarado es `String?`. Admite `null`, así que los consumidores deben contemplar ausencia de valor. |
| 74 | `val profileUri` | `inferido` | `Uri.parse(settings.profileImageUri)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Representa una ubicación Android `Uri`; no debe asumirse que siempre corresponde a una ruta de archivo convencional. |
| 75 | `val profileName` | `inferido` | `safeFileName(profileUri.lastPathSegment?.substringAfterLast('/')?.takeIf { it.isNotBlank() }` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 77 | `val candidate` | `inferido` | `"$PROFILE_PREFIX$profileName"` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 82 | `val data` | `inferido` | `JSONObject().apply {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 103 | `val appContext` | `inferido` | `context.applicationContext` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El `Context` da acceso a recursos/servicios/almacenamiento Android; debe respetarse el ciclo de vida del contexto recibido. |
| 104 | `val stageDir` | `inferido` | `File(appContext.cacheDir, "backup_restore_${UUID.randomUUID()}")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 108 | `val importedFiles` | `inferido` | `mutableListOf<File>()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 111 | `val dataFile` | `inferido` | `File(stageDir, DATA_ENTRY)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 119 | `val data` | `inferido` | `JSONObject(dataFile.readText(Charsets.UTF_8))` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 126 | `val notes` | `inferido` | `jsonToNotes(data.getJSONArray("notes"))` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 127 | `val attachmentRecords` | `inferido` | `jsonToAttachmentRecords(data.getJSONArray("attachments"))` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 128 | `val settingsJson` | `inferido` | `data.optJSONObject("settings") ?: JSONObject()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 129 | `var restoredSettings` | `inferido` | `jsonToSettings(settingsJson)` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 130 | `val noteIds` | `inferido` | `notes.map {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 147 | `val attachmentsDirectory` | `inferido` | `File(appContext.filesDir, "attachments")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 151 | `val restoreToken` | `inferido` | `System.currentTimeMillis()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 152 | `val restoredAttachments` | `inferido` | `attachmentRecords.map { record -> val sourceFile = safeStageFile(stageDir = stageDir,` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 157 | `val targetName` | `inferido` | `"restore_${restoreToken}_${record.id}_${safeFileName(record.name ?: sourceFile.name)}"` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 158 | `val target` | `inferido` | `File(attachmentsDirectory, targetName)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 164 | `val profileEntry` | `inferido` | `settingsJson.optString("profileEntry", "")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 166 | `val profileSource` | `inferido` | `safeStageFile(stageDir, profileEntry)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 168 | `val profileDir` | `inferido` | `File(appContext.filesDir, "profile_backup")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 172 | `val profileTarget` | `inferido` | `File(profileDir, "restored_${restoreToken}_${safeFileName(profileSource.name)}")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 182 | `val database` | `inferido` | `AppDatabase.getDatabase(appContext)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 200 | `val keepPaths` | `inferido` | `importedFiles.filter {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 215 | `val activeProfilePath` | `inferido` | `Uri.parse(restoredSettings.profileImageUri).takeIf {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 240 | `val input` | `inferido` | `openInputStream(context, source) ?: return false` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 257 | `val path` | `inferido` | `uri.path ?: return null` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 266 | `val input` | `inferido` | `context.contentResolver.openInputStream(source)?: error("No se pudo abrir la copia")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 271 | `val initialFreeSpace` | `inferido` | `stageDir.usableSpace.coerceAtLeast(0L)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El inicializador limita explícitamente el valor a un rango, evitando que dimensiones/intensidades/sizes inválidos se propaguen. |
| 272 | `val extractionLimit` | `inferido` | `if (initialFreeSpace > 0L) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 278 | `val reserve` | `inferido` | `maxOf(32L * 1024L * 1024L, initialFreeSpace * 15L / 100L)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 291 | `var extractedBytes` | `inferido` | `0L` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 292 | `var entryCount` | `inferido` | `0` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 293 | `val buffer` | `inferido` | `ByteArray(64 * 1024)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 299 | `val entry` | `inferido` | `zip.nextEntry?: break` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 306 | `val target` | `inferido` | `safeStageFile(stageDir, entry.name)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 313 | `val count` | `inferido` | `zip.read(buffer)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 335 | `val target` | `inferido` | `File(stageDir, entryName)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 336 | `val rootPath` | `inferido` | `stageDir.canonicalPath + File.separator` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 337 | `val targetPath` | `inferido` | `target.canonicalPath` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 365 | `val item` | `inferido` | `array.getJSONObject(index)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 373 | `val fileEntry` | `String)` | `(sin inicializador en la declaración)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo declarado es `String)`. No declara nulabilidad explícita. |
| 376 | `val item` | `inferido` | `array.getJSONObject(index)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 437 | `val defaults` | `inferido` | `AppSettings()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 57 | `return@runCatching output.use { rawOutput -> ZipOutputStream(rawOutput.buffered()).use { zip ->` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 65 | `if (copied) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 73 | `if (settings.profileImageUri.isNotBlank()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 78 | `if (writeUriEntry(context = appContext, zip = zip, source = profileUri, entryName = candidate)) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 95 | `return@use BackupSummary(noteCount = notes.size, attachmentCount = exportedAttachments,` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 105 | `if (!stageDir.mkdirs() && !stageDir.isDirectory) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 109 | `try {` | Frontera de manejo de errores: agrupa operaciones que pueden lanzar excepciones para permitir recuperación o limpieza controlada. |
| 112 | `if (!dataFile.isFile) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 115 | `if (dataFile.length() >` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 120 | `if (data.optString("format") != FORMAT_NAME) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 123 | `if (data.optInt("version", -1) != FORMAT_VERSION) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 133 | `if (noteIds.size != notes.size) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 136 | `if (attachmentRecords.map {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 141 | `if (attachmentRecords.any {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 148 | `if (!attachmentsDirectory.exists() && !attachmentsDirectory.mkdirs()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 154 | `if (!sourceFile.isFile) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 165 | `if (profileEntry.isNotBlank()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 167 | `if (profileSource.isFile) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 169 | `if (!profileDir.exists() && !profileDir.mkdirs()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 183 | `try {` | Frontera de manejo de errores: agrupa operaciones que pueden lanzar excepciones para permitir recuperación o limpieza controlada. |
| 187 | `if (notes.isNotEmpty()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 190 | `if (restoredAttachments.isNotEmpty()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 219 | `file ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 220 | `if (file.absolutePath != activeProfilePath) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 241 | `return try {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 248 | `try {` | Frontera de manejo de errores: agrupa operaciones que pueden lanzar excepciones para permitir recuperación o limpieza controlada. |
| 256 | `if (uri.scheme == "file") {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 288 | `if (initialFreeSpace > 0L && extractionLimit < 8L * 1024L * 1024L) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 295 | `rawInput ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 297 | `zip ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 298 | `while (true) {` | Bucle condicionado: repite mientras la condición se mantenga verdadera; la lógica interna debe permitir progreso hacia la salida. |
| 301 | `if (entryCount >` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 305 | `if (!entry.isDirectory) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 307 | `if (target.parentFile?.exists() == false) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 311 | `output ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 312 | `while (true) {` | Bucle condicionado: repite mientras la condición se mantenga verdadera; la lógica interna debe permitir progreso hacia la salida. |
| 314 | `if (count <= 0) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 315 | `break` | Interrumpe el bucle más cercano; ninguna iteración posterior de ese bucle se ejecuta. |
| 318 | `if (extractedBytes >` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 332 | `if (entryName.isBlank() \|\| entryName.startsWith('/') \|\| entryName.startsWith('\\') \|\| entryName.contains("..")) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 338 | `if (!targetPath.startsWith(rootPath)) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 341 | `return target` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 364 | `for (index in 0 until array.length()) {` | Iteración: repite el cuerpo para cada elemento de la colección/rango; el coste del bloque crece con el número de elementos recorridos. |
| 375 | `for (index in 0 until array.length()) {` | Iteración: repite el cuerpo para cada elemento de la colección/rango; el coste del bloque crece con el número de elementos recorridos. |
| 378 | `if (item.isNull("name")) null` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 379 | `else item.optString("name").takeIf { it.isNotBlank() },` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |
| 438 | `return AppSettings(darkMode = json.optBoolean("darkMode", defaults.darkMode),` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 7.
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 11.
- **Límite numérico:** Se fuerza el valor a un intervalo o umbral para impedir que salgan valores fuera de los límites esperados. Apariciones en este bloque: 2.
- **Filtro condicional:** `takeIf` conserva el valor únicamente cuando el predicado se cumple; en caso contrario lo convierte en `null`. Apariciones en este bloque: 3.
- **Trabajo IO:** El trabajo potencialmente bloqueante se desplaza al dispatcher de I/O en vez de ocupar el hilo principal. Apariciones en este bloque: 2.

#### Efectos secundarios y recursos

- **Persistencia Room/DAO:** Realiza una operación de persistencia o modificación a través de una capa de datos/DAO.
- **Preferencias/DataStore:** Lee o escribe configuración persistente; el resultado sobrevive a recreaciones de la pantalla y normalmente al reinicio de la app.
- **Sistema de archivos/caché:** Accede o modifica almacenamiento/caché; son efectos externos al estado puramente en memoria.
- **Caché:** Lee, llena, invalida o administra caché para evitar recomputación o decodificación repetida.
- **Mutación de colección/estado compartido:** Modifica una colección/mapa/estructura existente; el cambio permanece visible para quienes compartan esa misma instancia.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `withContext`, `AppDatabase.getDatabase`, `database.noteDao`, `getAllNotesOnce`, `database.attachmentDao`, `getAllAttachmentsOnce`, `appContext.contentResolver.openOutputStream`, `error`, `ZipOutputStream`, `rawOutput.buffered`, `JSONArray`, `safeFileName`, `writeUriEntry`, `Uri.parse`, `attachmentJson.put`, `attachmentToJson`, `settings.profileImageUri.isNotBlank`, `substringAfterLast`, `it.isNotBlank`, `JSONObject`, `put`, `System.currentTimeMillis`, `noteToJson`, `settingsToJson`, `zip.putNextEntry`, `ZipEntry`, `zip.write`, `data.toString`, `toByteArray`, `zip.closeEntry`, `BackupSummary`, `File`, `stageDir.mkdirs`, `extractBackup`, `dataFile.length`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- No mover trabajo bloqueante al hilo principal; el uso de `Dispatchers.IO` forma parte de la protección de rendimiento.
- Mantener coherencia entre creación, clave, lectura e invalidación de caché; cambiar solo una de esas etapas puede dejar datos obsoletos.
- Preservar la normalización y correspondencia de `performanceMode`, porque otras capas usan las mismas claves para rendimiento/calidad.

### 4.2 `exportBackup` — fun, líneas 47–100

```kotlin
    data class BackupSummary(val noteCount: Int, val attachmentCount: Int, val skippedAttachmentCount: Int = 0)
    suspend fun exportBackup(context: Context, destination: Uri, settings: AppSettings): Result<BackupSummary> =
        withContext(Dispatchers.IO) {
            runCatching {
                val appContext = context.applicationContext
                val database = AppDatabase.getDatabase(appContext)
                val notes = database.noteDao().getAllNotesOnce()
                val attachments = database.attachmentDao().getAllAttachmentsOnce()
                val output = appContext.contentResolver.openOutputStream(destination, "w"
                ) ?: error("No se pudo abrir el archivo de destino")
                return@runCatching output.use { rawOutput -> ZipOutputStream(rawOutput.buffered()).use { zip ->
                        val attachmentJson = JSONArray()
                        var exportedAttachments = 0
                        var skippedAttachments = 0
                        attachments.forEach { attachment -> val safeName = safeFileName(attachment.name ?: "attachment_${attachment.id}")
                            val entryName = "$ATTACHMENTS_PREFIX${attachment.id}_$safeName"
                            val copied = writeUriEntry(context = appContext, zip = zip, source = Uri.parse(attachment.uri),
                                entryName = entryName)
                            if (copied) {
                                attachmentJson.put(attachmentToJson(attachment = attachment, fileEntry = entryName))
                                exportedAttachments++
                            } else {
                                skippedAttachments++
                            }
                        }
                        var profileEntry: String? = null
                        if (settings.profileImageUri.isNotBlank()) {
                            val profileUri = Uri.parse(settings.profileImageUri)
                            val profileName = safeFileName(profileUri.lastPathSegment?.substringAfterLast('/')?.takeIf { it.isNotBlank() }
                                        ?: "profile_image")
                            val candidate = "$PROFILE_PREFIX$profileName"
                            if (writeUriEntry(context = appContext, zip = zip, source = profileUri, entryName = candidate)) {
                                profileEntry = candidate
                            }
                        }
                        val data = JSONObject().apply {
                            put("format", FORMAT_NAME)
                            put("version", FORMAT_VERSION)
                            put("createdAt", System.currentTimeMillis())
                            put("notes", JSONArray().apply {
                                    notes.forEach { put(noteToJson(it)) }
                                })
                            put("attachments", attachmentJson)
                            put("settings", settingsToJson(settings = settings, profileEntry = profileEntry))
                        }
                        zip.putNextEntry(ZipEntry(DATA_ENTRY))
                        zip.write(data.toString().toByteArray(Charsets.UTF_8))
                        zip.closeEntry()
                        return@use BackupSummary(noteCount = notes.size, attachmentCount = exportedAttachments,
                            skippedAttachmentCount = skippedAttachments)
                    }
                }
            }
        }
```

#### Qué hace y por qué existe

Gestiona la exportación e importación de los datos de MyNotes, incluyendo la información persistida de notas, adjuntos y ajustes que forman parte de una copia de seguridad.

#### Contrato de la declaración

**Parámetros:**

- `val noteCount: Int` — No se pudo separar automáticamente nombre/tipo; se conserva la firma exacta como contrato.
- `val attachmentCount: Int` — No se pudo separar automáticamente nombre/tipo; se conserva la firma exacta como contrato.
- `val skippedAttachmentCount: Int = 0` — No se pudo separar automáticamente nombre/tipo; se conserva la firma exacta como contrato.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `suspend` permite suspender sin bloquear el hilo; el llamador debe estar dentro de una corrutina u otra función suspendible.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 51 | `val appContext` | `inferido` | `context.applicationContext` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El `Context` da acceso a recursos/servicios/almacenamiento Android; debe respetarse el ciclo de vida del contexto recibido. |
| 52 | `val database` | `inferido` | `AppDatabase.getDatabase(appContext)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 53 | `val notes` | `inferido` | `database.noteDao().getAllNotesOnce()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 54 | `val attachments` | `inferido` | `database.attachmentDao().getAllAttachmentsOnce()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 55 | `val output` | `inferido` | `appContext.contentResolver.openOutputStream(destination, "w"` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 58 | `val attachmentJson` | `inferido` | `JSONArray()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 59 | `var exportedAttachments` | `inferido` | `0` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 60 | `var skippedAttachments` | `inferido` | `0` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 62 | `val entryName` | `inferido` | `"$ATTACHMENTS_PREFIX${attachment.id}_$safeName"` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 63 | `val copied` | `inferido` | `writeUriEntry(context = appContext, zip = zip, source = Uri.parse(attachment.uri),` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 72 | `var profileEntry` | `String?` | `null` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo declarado es `String?`. Admite `null`, así que los consumidores deben contemplar ausencia de valor. |
| 74 | `val profileUri` | `inferido` | `Uri.parse(settings.profileImageUri)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Representa una ubicación Android `Uri`; no debe asumirse que siempre corresponde a una ruta de archivo convencional. |
| 75 | `val profileName` | `inferido` | `safeFileName(profileUri.lastPathSegment?.substringAfterLast('/')?.takeIf { it.isNotBlank() }` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 77 | `val candidate` | `inferido` | `"$PROFILE_PREFIX$profileName"` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 82 | `val data` | `inferido` | `JSONObject().apply {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 57 | `return@runCatching output.use { rawOutput -> ZipOutputStream(rawOutput.buffered()).use { zip ->` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 65 | `if (copied) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 73 | `if (settings.profileImageUri.isNotBlank()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 78 | `if (writeUriEntry(context = appContext, zip = zip, source = profileUri, entryName = candidate)) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 95 | `return@use BackupSummary(noteCount = notes.size, attachmentCount = exportedAttachments,` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 2.
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 3.
- **Filtro condicional:** `takeIf` conserva el valor únicamente cuando el predicado se cumple; en caso contrario lo convierte en `null`. Apariciones en este bloque: 1.
- **Trabajo IO:** El trabajo potencialmente bloqueante se desplaza al dispatcher de I/O en vez de ocupar el hilo principal. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

- **Mutación de colección/estado compartido:** Modifica una colección/mapa/estructura existente; el cambio permanece visible para quienes compartan esa misma instancia.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `withContext`, `AppDatabase.getDatabase`, `database.noteDao`, `getAllNotesOnce`, `database.attachmentDao`, `getAllAttachmentsOnce`, `appContext.contentResolver.openOutputStream`, `error`, `ZipOutputStream`, `rawOutput.buffered`, `JSONArray`, `safeFileName`, `writeUriEntry`, `Uri.parse`, `attachmentJson.put`, `attachmentToJson`, `settings.profileImageUri.isNotBlank`, `substringAfterLast`, `it.isNotBlank`, `JSONObject`, `put`, `System.currentTimeMillis`, `noteToJson`, `settingsToJson`, `zip.putNextEntry`, `ZipEntry`, `zip.write`, `data.toString`, `toByteArray`, `zip.closeEntry`, `BackupSummary`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- No mover trabajo bloqueante al hilo principal; el uso de `Dispatchers.IO` forma parte de la protección de rendimiento.

### 4.3 `exportBackup` — fun, líneas 48–100

```kotlin
    suspend fun exportBackup(context: Context, destination: Uri, settings: AppSettings): Result<BackupSummary> =
        withContext(Dispatchers.IO) {
            runCatching {
                val appContext = context.applicationContext
                val database = AppDatabase.getDatabase(appContext)
                val notes = database.noteDao().getAllNotesOnce()
                val attachments = database.attachmentDao().getAllAttachmentsOnce()
                val output = appContext.contentResolver.openOutputStream(destination, "w"
                ) ?: error("No se pudo abrir el archivo de destino")
                return@runCatching output.use { rawOutput -> ZipOutputStream(rawOutput.buffered()).use { zip ->
                        val attachmentJson = JSONArray()
                        var exportedAttachments = 0
                        var skippedAttachments = 0
                        attachments.forEach { attachment -> val safeName = safeFileName(attachment.name ?: "attachment_${attachment.id}")
                            val entryName = "$ATTACHMENTS_PREFIX${attachment.id}_$safeName"
                            val copied = writeUriEntry(context = appContext, zip = zip, source = Uri.parse(attachment.uri),
                                entryName = entryName)
                            if (copied) {
                                attachmentJson.put(attachmentToJson(attachment = attachment, fileEntry = entryName))
                                exportedAttachments++
                            } else {
                                skippedAttachments++
                            }
                        }
                        var profileEntry: String? = null
                        if (settings.profileImageUri.isNotBlank()) {
                            val profileUri = Uri.parse(settings.profileImageUri)
                            val profileName = safeFileName(profileUri.lastPathSegment?.substringAfterLast('/')?.takeIf { it.isNotBlank() }
                                        ?: "profile_image")
                            val candidate = "$PROFILE_PREFIX$profileName"
                            if (writeUriEntry(context = appContext, zip = zip, source = profileUri, entryName = candidate)) {
                                profileEntry = candidate
                            }
                        }
                        val data = JSONObject().apply {
                            put("format", FORMAT_NAME)
                            put("version", FORMAT_VERSION)
                            put("createdAt", System.currentTimeMillis())
                            put("notes", JSONArray().apply {
                                    notes.forEach { put(noteToJson(it)) }
                                })
                            put("attachments", attachmentJson)
                            put("settings", settingsToJson(settings = settings, profileEntry = profileEntry))
                        }
                        zip.putNextEntry(ZipEntry(DATA_ENTRY))
                        zip.write(data.toString().toByteArray(Charsets.UTF_8))
                        zip.closeEntry()
                        return@use BackupSummary(noteCount = notes.size, attachmentCount = exportedAttachments,
                            skippedAttachmentCount = skippedAttachments)
                    }
                }
            }
        }
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `context: Context` — `context` recibe un valor de tipo `Context`. El contrato no marca este parámetro como anulable. Da acceso a recursos y APIs Android; su vida útil debe corresponder con el uso realizado.
- `destination: Uri` — `destination` recibe un valor de tipo `Uri`. El contrato no marca este parámetro como anulable. Es una referencia de contenido Android; puede apuntar a content providers y no solo a archivos locales.
- `settings: AppSettings` — `settings` recibe un valor de tipo `AppSettings`. El contrato no marca este parámetro como anulable.

**Retorno:** `Result<BackupSummary>`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `suspend` permite suspender sin bloquear el hilo; el llamador debe estar dentro de una corrutina u otra función suspendible.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 51 | `val appContext` | `inferido` | `context.applicationContext` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El `Context` da acceso a recursos/servicios/almacenamiento Android; debe respetarse el ciclo de vida del contexto recibido. |
| 52 | `val database` | `inferido` | `AppDatabase.getDatabase(appContext)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 53 | `val notes` | `inferido` | `database.noteDao().getAllNotesOnce()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 54 | `val attachments` | `inferido` | `database.attachmentDao().getAllAttachmentsOnce()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 55 | `val output` | `inferido` | `appContext.contentResolver.openOutputStream(destination, "w"` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 58 | `val attachmentJson` | `inferido` | `JSONArray()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 59 | `var exportedAttachments` | `inferido` | `0` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 60 | `var skippedAttachments` | `inferido` | `0` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 62 | `val entryName` | `inferido` | `"$ATTACHMENTS_PREFIX${attachment.id}_$safeName"` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 63 | `val copied` | `inferido` | `writeUriEntry(context = appContext, zip = zip, source = Uri.parse(attachment.uri),` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 72 | `var profileEntry` | `String?` | `null` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo declarado es `String?`. Admite `null`, así que los consumidores deben contemplar ausencia de valor. |
| 74 | `val profileUri` | `inferido` | `Uri.parse(settings.profileImageUri)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Representa una ubicación Android `Uri`; no debe asumirse que siempre corresponde a una ruta de archivo convencional. |
| 75 | `val profileName` | `inferido` | `safeFileName(profileUri.lastPathSegment?.substringAfterLast('/')?.takeIf { it.isNotBlank() }` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 77 | `val candidate` | `inferido` | `"$PROFILE_PREFIX$profileName"` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 82 | `val data` | `inferido` | `JSONObject().apply {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 57 | `return@runCatching output.use { rawOutput -> ZipOutputStream(rawOutput.buffered()).use { zip ->` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 65 | `if (copied) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 73 | `if (settings.profileImageUri.isNotBlank()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 78 | `if (writeUriEntry(context = appContext, zip = zip, source = profileUri, entryName = candidate)) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 95 | `return@use BackupSummary(noteCount = notes.size, attachmentCount = exportedAttachments,` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 2.
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 3.
- **Filtro condicional:** `takeIf` conserva el valor únicamente cuando el predicado se cumple; en caso contrario lo convierte en `null`. Apariciones en este bloque: 1.
- **Trabajo IO:** El trabajo potencialmente bloqueante se desplaza al dispatcher de I/O en vez de ocupar el hilo principal. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

- **Mutación de colección/estado compartido:** Modifica una colección/mapa/estructura existente; el cambio permanece visible para quienes compartan esa misma instancia.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `withContext`, `AppDatabase.getDatabase`, `database.noteDao`, `getAllNotesOnce`, `database.attachmentDao`, `getAllAttachmentsOnce`, `appContext.contentResolver.openOutputStream`, `error`, `ZipOutputStream`, `rawOutput.buffered`, `JSONArray`, `safeFileName`, `writeUriEntry`, `Uri.parse`, `attachmentJson.put`, `attachmentToJson`, `settings.profileImageUri.isNotBlank`, `substringAfterLast`, `it.isNotBlank`, `JSONObject`, `put`, `System.currentTimeMillis`, `noteToJson`, `settingsToJson`, `zip.putNextEntry`, `ZipEntry`, `zip.write`, `data.toString`, `toByteArray`, `zip.closeEntry`, `BackupSummary`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- No mover trabajo bloqueante al hilo principal; el uso de `Dispatchers.IO` forma parte de la protección de rendimiento.

### 4.4 `importBackup` — fun, líneas 101–238

```kotlin
    suspend fun importBackup(context: Context, source: Uri): Result<BackupSummary> = withContext(Dispatchers.IO) {
            runCatching {
                val appContext = context.applicationContext
                val stageDir = File(appContext.cacheDir, "backup_restore_${UUID.randomUUID()}")
                if (!stageDir.mkdirs() && !stageDir.isDirectory) {
                    error("No se pudo preparar la restauración")
                }
                val importedFiles = mutableListOf<File>()
                try {
                    extractBackup(context = appContext, source = source, stageDir = stageDir)
                    val dataFile = File(stageDir, DATA_ENTRY)
                    if (!dataFile.isFile) {
                        error("La copia no contiene data.json")
                    }
                    if (dataFile.length() >
                        MAX_METADATA_BYTES) {
                        error("Los metadatos de la copia son demasiado grandes")
                    }
                    val data = JSONObject(dataFile.readText(Charsets.UTF_8))
                    if (data.optString("format") != FORMAT_NAME) {
                        error("El archivo no es una copia de MyNotes")
                    }
                    if (data.optInt("version", -1) != FORMAT_VERSION) {
                        error("Versión de copia no compatible")
                    }
                    val notes = jsonToNotes(data.getJSONArray("notes"))
                    val attachmentRecords = jsonToAttachmentRecords(data.getJSONArray("attachments"))
                    val settingsJson = data.optJSONObject("settings") ?: JSONObject()
                    var restoredSettings = jsonToSettings(settingsJson)
                    val noteIds = notes.map {
                                it.id
                            }.toSet()
                    if (noteIds.size != notes.size) {
                        error("La copia contiene identificadores de nota duplicados")
                    }
                    if (attachmentRecords.map {
                                it.id
                            }.toSet().size != attachmentRecords.size) {
                        error("La copia contiene identificadores de adjunto duplicados")
                    }
                    if (attachmentRecords.any {
                            it.noteId !in
                                noteIds
                        }) {
                        error("La copia contiene adjuntos sin una nota válida")
                    }
                    val attachmentsDirectory = File(appContext.filesDir, "attachments")
                    if (!attachmentsDirectory.exists() && !attachmentsDirectory.mkdirs()) {
                        error("No se pudo preparar el almacenamiento de adjuntos")
                    }
                    val restoreToken = System.currentTimeMillis()
                    val restoredAttachments = attachmentRecords.map { record -> val sourceFile = safeStageFile(stageDir = stageDir,
                                entryName = record.fileEntry)
                            if (!sourceFile.isFile) {
                                error("Falta un adjunto en la copia: ${record.fileEntry}")
                            }
                            val targetName = "restore_${restoreToken}_${record.id}_${safeFileName(record.name ?: sourceFile.name)}"
                            val target = File(attachmentsDirectory, targetName)
                            sourceFile.copyTo(target, overwrite = true)
                            importedFiles += target
                            Attachment(id = record.id, noteId = record.noteId, type = record.type, uri = Uri.fromFile(target).toString(),
                                name = record.name, createdAt = record.createdAt)
                        }
                    val profileEntry = settingsJson.optString("profileEntry", "")
                    if (profileEntry.isNotBlank()) {
                        val profileSource = safeStageFile(stageDir, profileEntry)
                        if (profileSource.isFile) {
                            val profileDir = File(appContext.filesDir, "profile_backup")
                            if (!profileDir.exists() && !profileDir.mkdirs()) {
                                error("No se pudo preparar la imagen de perfil")
                            }
                            val profileTarget = File(profileDir, "restored_${restoreToken}_${safeFileName(profileSource.name)}")
                            profileSource.copyTo(profileTarget, overwrite = true)
                            importedFiles += profileTarget
                            restoredSettings = restoredSettings.copy(profileImageUri = Uri.fromFile(profileTarget).toString())
                        } else {
                            restoredSettings = restoredSettings.copy(profileImageUri = "")
                        }
                    } else {
                        restoredSettings = restoredSettings.copy(profileImageUri = "")
                    }
                    val database = AppDatabase.getDatabase(appContext)
                    try {
                        database.withTransaction {
                            database.attachmentDao().deleteAllAttachments()
                            database.noteDao().deleteAllNotes()
                            if (notes.isNotEmpty()) {
                                database.noteDao().insertNotes(notes)
                            }
                            if (restoredAttachments.isNotEmpty()) {
                                database.attachmentDao().insertAttachmentsForRestore(restoredAttachments)
                            }
                        }
                    } catch (e: Exception) {
                        importedFiles.forEach { it.delete() }
                        throw e
                    }
                    // La base ya apunta únicamente a los archivos recién restaurados.
                    // Eliminamos adjuntos antiguos que hayan quedado en almacenamiento.
                    val keepPaths = importedFiles.filter {
                                it.parentFile == attachmentsDirectory
                            }.map {
                                it.absolutePath
                            }.toSet()
                    attachmentsDirectory.listFiles()?.forEach { file -> if (file.absolutePath !in keepPaths) {
                            file.deleteRecursively()
                        }
                    }
                    SettingsRepository(appContext).restoreFromBackup(restoredSettings)
                    /*
                     * Conservamos únicamente el perfil activo creado por una
                     * restauración. Así las importaciones repetidas no dejan
                     * imágenes huérfanas ocupando almacenamiento.
                     */
                    val activeProfilePath = Uri.parse(restoredSettings.profileImageUri).takeIf {
                                it.scheme == "file"
                            }?.path
                    File(appContext.filesDir, "profile_backup").listFiles()?.forEach {
                            file ->
                            if (file.absolutePath != activeProfilePath) {
                                file.delete()
                            }
                        }
                    // MainActivity usa esta preferencia antes de que DataStore esté listo.
                    appContext.getSharedPreferences("locale_prefs", Context.MODE_PRIVATE).edit()
                        .putString("language", restoredSettings.language).commit()
                    // Los previews se vuelven a generar para las nuevas rutas.
                    listOf("video_previews", "audio_previews", "pdf_previews", "docx_previews").forEach { name -> File(appContext.cacheDir,
                            "mynotes_perf_v2/$name").deleteRecursively()
                    }
                    File(appContext.cacheDir, "viewer_video").deleteRecursively()
                    File(appContext.cacheDir, "viewer_audio").deleteRecursively()
                    BackupSummary(noteCount = notes.size, attachmentCount = restoredAttachments.size)
                } finally {
                    stageDir.deleteRecursively()
                }
            }
        }
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `context: Context` — `context` recibe un valor de tipo `Context`. El contrato no marca este parámetro como anulable. Da acceso a recursos y APIs Android; su vida útil debe corresponder con el uso realizado.
- `source: Uri` — `source` recibe un valor de tipo `Uri`. El contrato no marca este parámetro como anulable. Es una referencia de contenido Android; puede apuntar a content providers y no solo a archivos locales.

**Retorno:** `Result<BackupSummary>`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `suspend` permite suspender sin bloquear el hilo; el llamador debe estar dentro de una corrutina u otra función suspendible.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 103 | `val appContext` | `inferido` | `context.applicationContext` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El `Context` da acceso a recursos/servicios/almacenamiento Android; debe respetarse el ciclo de vida del contexto recibido. |
| 104 | `val stageDir` | `inferido` | `File(appContext.cacheDir, "backup_restore_${UUID.randomUUID()}")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 108 | `val importedFiles` | `inferido` | `mutableListOf<File>()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 111 | `val dataFile` | `inferido` | `File(stageDir, DATA_ENTRY)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 119 | `val data` | `inferido` | `JSONObject(dataFile.readText(Charsets.UTF_8))` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 126 | `val notes` | `inferido` | `jsonToNotes(data.getJSONArray("notes"))` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 127 | `val attachmentRecords` | `inferido` | `jsonToAttachmentRecords(data.getJSONArray("attachments"))` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 128 | `val settingsJson` | `inferido` | `data.optJSONObject("settings") ?: JSONObject()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 129 | `var restoredSettings` | `inferido` | `jsonToSettings(settingsJson)` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 130 | `val noteIds` | `inferido` | `notes.map {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 147 | `val attachmentsDirectory` | `inferido` | `File(appContext.filesDir, "attachments")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 151 | `val restoreToken` | `inferido` | `System.currentTimeMillis()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 152 | `val restoredAttachments` | `inferido` | `attachmentRecords.map { record -> val sourceFile = safeStageFile(stageDir = stageDir,` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 157 | `val targetName` | `inferido` | `"restore_${restoreToken}_${record.id}_${safeFileName(record.name ?: sourceFile.name)}"` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 158 | `val target` | `inferido` | `File(attachmentsDirectory, targetName)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 164 | `val profileEntry` | `inferido` | `settingsJson.optString("profileEntry", "")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 166 | `val profileSource` | `inferido` | `safeStageFile(stageDir, profileEntry)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 168 | `val profileDir` | `inferido` | `File(appContext.filesDir, "profile_backup")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 172 | `val profileTarget` | `inferido` | `File(profileDir, "restored_${restoreToken}_${safeFileName(profileSource.name)}")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 182 | `val database` | `inferido` | `AppDatabase.getDatabase(appContext)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 200 | `val keepPaths` | `inferido` | `importedFiles.filter {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 215 | `val activeProfilePath` | `inferido` | `Uri.parse(restoredSettings.profileImageUri).takeIf {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 105 | `if (!stageDir.mkdirs() && !stageDir.isDirectory) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 109 | `try {` | Frontera de manejo de errores: agrupa operaciones que pueden lanzar excepciones para permitir recuperación o limpieza controlada. |
| 112 | `if (!dataFile.isFile) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 115 | `if (dataFile.length() >` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 120 | `if (data.optString("format") != FORMAT_NAME) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 123 | `if (data.optInt("version", -1) != FORMAT_VERSION) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 133 | `if (noteIds.size != notes.size) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 136 | `if (attachmentRecords.map {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 141 | `if (attachmentRecords.any {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 148 | `if (!attachmentsDirectory.exists() && !attachmentsDirectory.mkdirs()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 154 | `if (!sourceFile.isFile) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 165 | `if (profileEntry.isNotBlank()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 167 | `if (profileSource.isFile) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 169 | `if (!profileDir.exists() && !profileDir.mkdirs()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 183 | `try {` | Frontera de manejo de errores: agrupa operaciones que pueden lanzar excepciones para permitir recuperación o limpieza controlada. |
| 187 | `if (notes.isNotEmpty()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 190 | `if (restoredAttachments.isNotEmpty()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 219 | `file ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 220 | `if (file.absolutePath != activeProfilePath) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 3.
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 2.
- **Filtro condicional:** `takeIf` conserva el valor únicamente cuando el predicado se cumple; en caso contrario lo convierte en `null`. Apariciones en este bloque: 1.
- **Trabajo IO:** El trabajo potencialmente bloqueante se desplaza al dispatcher de I/O en vez de ocupar el hilo principal. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

- **Persistencia Room/DAO:** Realiza una operación de persistencia o modificación a través de una capa de datos/DAO.
- **Preferencias/DataStore:** Lee o escribe configuración persistente; el resultado sobrevive a recreaciones de la pantalla y normalmente al reinicio de la app.
- **Sistema de archivos/caché:** Accede o modifica almacenamiento/caché; son efectos externos al estado puramente en memoria.
- **Caché:** Lee, llena, invalida o administra caché para evitar recomputación o decodificación repetida.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `withContext`, `File`, `stageDir.mkdirs`, `error`, `extractBackup`, `dataFile.length`, `JSONObject`, `dataFile.readText`, `data.optString`, `data.optInt`, `jsonToNotes`, `data.getJSONArray`, `jsonToAttachmentRecords`, `data.optJSONObject`, `jsonToSettings`, `toSet`, `attachmentsDirectory.exists`, `attachmentsDirectory.mkdirs`, `System.currentTimeMillis`, `safeStageFile`, `sourceFile.copyTo`, `Attachment`, `Uri.fromFile`, `toString`, `settingsJson.optString`, `profileEntry.isNotBlank`, `profileDir.exists`, `profileDir.mkdirs`, `profileSource.copyTo`, `restoredSettings.copy`, `AppDatabase.getDatabase`, `database.attachmentDao`, `deleteAllAttachments`, `database.noteDao`, `deleteAllNotes`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- No mover trabajo bloqueante al hilo principal; el uso de `Dispatchers.IO` forma parte de la protección de rendimiento.
- Mantener coherencia entre creación, clave, lectura e invalidación de caché; cambiar solo una de esas etapas puede dejar datos obsoletos.

### 4.5 `writeUriEntry` — fun, líneas 239–254

```kotlin
    private fun writeUriEntry(context: Context, zip: ZipOutputStream, source: Uri, entryName: String): Boolean {
        val input = openInputStream(context, source) ?: return false
        return try {
            input.use { stream -> zip.putNextEntry(ZipEntry(entryName))
                stream.copyTo(zip, DEFAULT_BUFFER_SIZE)
                zip.closeEntry()
            }
            true
        } catch (_: Exception) {
            try {
                zip.closeEntry()
            } catch (_: Exception) {
            }
            false
        }
    }
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `context: Context` — `context` recibe un valor de tipo `Context`. El contrato no marca este parámetro como anulable. Da acceso a recursos y APIs Android; su vida útil debe corresponder con el uso realizado.
- `zip: ZipOutputStream` — `zip` recibe un valor de tipo `ZipOutputStream`. El contrato no marca este parámetro como anulable.
- `source: Uri` — `source` recibe un valor de tipo `Uri`. El contrato no marca este parámetro como anulable. Es una referencia de contenido Android; puede apuntar a content providers y no solo a archivos locales.
- `entryName: String` — `entryName` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `Boolean`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 240 | `val input` | `inferido` | `openInputStream(context, source) ?: return false` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 241 | `return try {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 248 | `try {` | Frontera de manejo de errores: agrupa operaciones que pueden lanzar excepciones para permitir recuperación o limpieza controlada. |

**Restricciones concretas que aparecen en este bloque:**
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `openInputStream`, `zip.putNextEntry`, `ZipEntry`, `stream.copyTo`, `zip.closeEntry`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.6 `openInputStream` — fun, líneas 255–264

```kotlin
    private fun openInputStream(context: Context, uri: Uri): InputStream? = try {
            if (uri.scheme == "file") {
                val path = uri.path ?: return null
                FileInputStream(File(path))
            } else {
                context.contentResolver.openInputStream(uri)
            }
        } catch (_: Exception) {
            null
        }
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `context: Context` — `context` recibe un valor de tipo `Context`. El contrato no marca este parámetro como anulable. Da acceso a recursos y APIs Android; su vida útil debe corresponder con el uso realizado.
- `uri: Uri` — `uri` recibe un valor de tipo `Uri`. El contrato no marca este parámetro como anulable. Es una referencia de contenido Android; puede apuntar a content providers y no solo a archivos locales.

**Retorno:** `InputStream?`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 257 | `val path` | `inferido` | `uri.path ?: return null` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 256 | `if (uri.scheme == "file") {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |

**Restricciones concretas que aparecen en este bloque:**
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

- **Sistema de archivos/caché:** Accede o modifica almacenamiento/caché; son efectos externos al estado puramente en memoria.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `FileInputStream`, `File`, `context.contentResolver.openInputStream`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.7 `extractBackup` — fun, líneas 265–330

```kotlin
    private fun extractBackup(context: Context, source: Uri, stageDir: File) {
        val input = context.contentResolver.openInputStream(source)?: error("No se pudo abrir la copia")
        /*
         * Dejamos un margen de almacenamiento libre para que Android y la
         * propia aplicación sigan pudiendo escribir archivos temporales.
         */
        val initialFreeSpace = stageDir.usableSpace.coerceAtLeast(0L)
        val extractionLimit = if (initialFreeSpace > 0L) {
                /*
                 * Nunca permitimos extraer más de lo que realmente cabe.
                 * Reservamos al menos 32 MiB (o 15 % del espacio disponible)
                 * para que Android pueda seguir creando archivos temporales.
                 */
                val reserve = maxOf(32L * 1024L * 1024L, initialFreeSpace * 15L / 100L)
                (initialFreeSpace - reserve).coerceAtLeast(0L)
            } else {
                /*
                 * usableSpace puede devolver 0 en proveedores peculiares.
                 * En ese caso usamos un límite defensivo; las escrituras
                 * siguen fallando limpiamente si el sistema se queda sin espacio.
                 */
                2L * 1024L * 1024L * 1024L
            }
        if (initialFreeSpace > 0L && extractionLimit < 8L * 1024L * 1024L) {
            error("No hay espacio suficiente para restaurar la copia")
        }
        var extractedBytes = 0L
        var entryCount = 0
        val buffer = ByteArray(64 * 1024)
        input.use {
            rawInput ->
            ZipInputStream(rawInput.buffered()).use {
                    zip ->
                    while (true) {
                        val entry = zip.nextEntry?: break
                        entryCount++
                        if (entryCount >
                            MAX_BACKUP_ENTRIES) {
                            error("La copia contiene demasiados archivos")
                        }
                        if (!entry.isDirectory) {
                            val target = safeStageFile(stageDir, entry.name)
                            if (target.parentFile?.exists() == false) {
                                target.parentFile?.mkdirs()
                            }
                            target.outputStream().buffered(64 * 1024).use {
                                    output ->
                                    while (true) {
                                        val count = zip.read(buffer)
                                        if (count <= 0) {
                                            break
                                        }
                                        extractedBytes += count.toLong()
                                        if (extractedBytes >
                                            extractionLimit) {
                                            error("La copia requiere demasiado espacio de almacenamiento")
                                        }
                                        output.write(buffer, 0, count)
                                    }
                                }
                        }
                        zip.closeEntry()
                    }
                }
        }
    }
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `context: Context` — `context` recibe un valor de tipo `Context`. El contrato no marca este parámetro como anulable. Da acceso a recursos y APIs Android; su vida útil debe corresponder con el uso realizado.
- `source: Uri` — `source` recibe un valor de tipo `Uri`. El contrato no marca este parámetro como anulable. Es una referencia de contenido Android; puede apuntar a content providers y no solo a archivos locales.
- `stageDir: File` — `stageDir` recibe un valor de tipo `File`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 266 | `val input` | `inferido` | `context.contentResolver.openInputStream(source)?: error("No se pudo abrir la copia")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 271 | `val initialFreeSpace` | `inferido` | `stageDir.usableSpace.coerceAtLeast(0L)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El inicializador limita explícitamente el valor a un rango, evitando que dimensiones/intensidades/sizes inválidos se propaguen. |
| 272 | `val extractionLimit` | `inferido` | `if (initialFreeSpace > 0L) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 278 | `val reserve` | `inferido` | `maxOf(32L * 1024L * 1024L, initialFreeSpace * 15L / 100L)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 291 | `var extractedBytes` | `inferido` | `0L` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 292 | `var entryCount` | `inferido` | `0` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 293 | `val buffer` | `inferido` | `ByteArray(64 * 1024)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 299 | `val entry` | `inferido` | `zip.nextEntry?: break` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 306 | `val target` | `inferido` | `safeStageFile(stageDir, entry.name)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 313 | `val count` | `inferido` | `zip.read(buffer)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 288 | `if (initialFreeSpace > 0L && extractionLimit < 8L * 1024L * 1024L) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 295 | `rawInput ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 297 | `zip ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 298 | `while (true) {` | Bucle condicionado: repite mientras la condición se mantenga verdadera; la lógica interna debe permitir progreso hacia la salida. |
| 301 | `if (entryCount >` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 305 | `if (!entry.isDirectory) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 307 | `if (target.parentFile?.exists() == false) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 311 | `output ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 312 | `while (true) {` | Bucle condicionado: repite mientras la condición se mantenga verdadera; la lógica interna debe permitir progreso hacia la salida. |
| 314 | `if (count <= 0) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 315 | `break` | Interrumpe el bucle más cercano; ninguna iteración posterior de ese bucle se ejecuta. |
| 318 | `if (extractedBytes >` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 2.
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 2.
- **Límite numérico:** Se fuerza el valor a un intervalo o umbral para impedir que salgan valores fuera de los límites esperados. Apariciones en este bloque: 2.

#### Efectos secundarios y recursos

- **Sistema de archivos/caché:** Accede o modifica almacenamiento/caché; son efectos externos al estado puramente en memoria.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `context.contentResolver.openInputStream`, `error`, `stageDir.usableSpace.coerceAtLeast`, `maxOf`, `coerceAtLeast`, `ByteArray`, `ZipInputStream`, `rawInput.buffered`, `safeStageFile`, `exists`, `mkdirs`, `target.outputStream`, `buffered`, `zip.read`, `count.toLong`, `output.write`, `zip.closeEntry`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.8 `safeStageFile` — fun, líneas 331–342

```kotlin
    private fun safeStageFile(stageDir: File, entryName: String): File {
        if (entryName.isBlank() || entryName.startsWith('/') || entryName.startsWith('\\') || entryName.contains("..")) {
            error("Ruta inválida dentro de la copia")
        }
        val target = File(stageDir, entryName)
        val rootPath = stageDir.canonicalPath + File.separator
        val targetPath = target.canonicalPath
        if (!targetPath.startsWith(rootPath)) {
            error("Ruta inválida dentro de la copia")
        }
        return target
    }
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `stageDir: File` — `stageDir` recibe un valor de tipo `File`. El contrato no marca este parámetro como anulable.
- `entryName: String` — `entryName` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `File`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 335 | `val target` | `inferido` | `File(stageDir, entryName)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 336 | `val rootPath` | `inferido` | `stageDir.canonicalPath + File.separator` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 337 | `val targetPath` | `inferido` | `target.canonicalPath` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 332 | `if (entryName.isBlank() \|\| entryName.startsWith('/') \|\| entryName.startsWith('\\') \|\| entryName.contains("..")) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 338 | `if (!targetPath.startsWith(rootPath)) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 341 | `return target` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

#### Efectos secundarios y recursos

- **Sistema de archivos/caché:** Accede o modifica almacenamiento/caché; son efectos externos al estado puramente en memoria.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `entryName.isBlank`, `entryName.startsWith`, `entryName.contains`, `error`, `File`, `targetPath.startsWith`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.9 `safeFileName` — fun, líneas 343–354

```kotlin
    private fun safeFileName(value: String): String = value.replace(Regex("[^A-Za-z0-9._-]"), "_").trim('_').take(100).ifBlank { "file" }
    private fun noteToJson(note: Note) = JSONObject().apply {
            put("id", note.id)
            put("title", note.title)
            put("content", note.content)
            put("createdAt", note.createdAt)
            put("color", note.color)
            put("priority", note.priority)
            put("category", note.category)
            put("isFavorite", note.isFavorite)
            put("isPinned", note.isPinned)
        }
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `value: String` — `value` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `String`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

- **Persistencia Room/DAO:** Realiza una operación de persistencia o modificación a través de una capa de datos/DAO.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `value.replace`, `Regex`, `trim`, `take`, `JSONObject`, `put`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.10 `noteToJson` — fun, líneas 344–354

```kotlin
    private fun noteToJson(note: Note) = JSONObject().apply {
            put("id", note.id)
            put("title", note.title)
            put("content", note.content)
            put("createdAt", note.createdAt)
            put("color", note.color)
            put("priority", note.priority)
            put("category", note.category)
            put("isFavorite", note.isFavorite)
            put("isPinned", note.isPinned)
        }
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `note: Note` — `note` recibe un valor de tipo `Note`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `JSONObject`, `put`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.11 `attachmentToJson` — fun, líneas 355–362

```kotlin
    private fun attachmentToJson(attachment: Attachment, fileEntry: String) = JSONObject().apply {
            put("id", attachment.id)
            put("noteId", attachment.noteId)
            put("type", attachment.type)
            put("name", attachment.name ?: JSONObject.NULL)
            put("createdAt", attachment.createdAt)
            put("fileEntry", fileEntry)
        }
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `attachment: Attachment` — `attachment` recibe un valor de tipo `Attachment`. El contrato no marca este parámetro como anulable.
- `fileEntry: String` — `fileEntry` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

**Restricciones concretas que aparecen en este bloque:**
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `JSONObject`, `put`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.12 `jsonToNotes` — fun, líneas 363–371

```kotlin
    private fun jsonToNotes(array: JSONArray): List<Note> = buildList {
            for (index in 0 until array.length()) {
                val item = array.getJSONObject(index)
                add(Note(id = item.getInt("id"), title = item.optString("title", ""), content = item.optString("content", ""),
                        createdAt = item.optLong("createdAt", System.currentTimeMillis()), color = item.optString("color", "default"),
                        priority = item.optInt("priority", 0), category = item.optString("category", "personal"),
                        isFavorite = item.optBoolean("isFavorite", false), isPinned = item.optBoolean("isPinned", false)))
            }
        }
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `array: JSONArray` — `array` recibe un valor de tipo `JSONArray`. El contrato no marca este parámetro como anulable.

**Retorno:** `List<Note>`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 365 | `val item` | `inferido` | `array.getJSONObject(index)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 364 | `for (index in 0 until array.length()) {` | Iteración: repite el cuerpo para cada elemento de la colección/rango; el coste del bloque crece con el número de elementos recorridos. |

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `array.length`, `array.getJSONObject`, `add`, `Note`, `item.getInt`, `item.optString`, `item.optLong`, `System.currentTimeMillis`, `item.optInt`, `item.optBoolean`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.13 `jsonToAttachmentRecords` — fun, líneas 372–373

```kotlin
    private data class AttachmentRecord(val id: Int, val noteId: Int, val type: String, val name: String?, val createdAt: Long,
        val fileEntry: String)
```

#### Qué hace y por qué existe

Gestiona la exportación e importación de los datos de MyNotes, incluyendo la información persistida de notas, adjuntos y ajustes que forman parte de una copia de seguridad.

#### Contrato de la declaración

**Parámetros:**

- `val id: Int` — No se pudo separar automáticamente nombre/tipo; se conserva la firma exacta como contrato.
- `val noteId: Int` — No se pudo separar automáticamente nombre/tipo; se conserva la firma exacta como contrato.
- `val type: String` — No se pudo separar automáticamente nombre/tipo; se conserva la firma exacta como contrato.
- `val name: String?` — No se pudo separar automáticamente nombre/tipo; se conserva la firma exacta como contrato.
- `val createdAt: Long` — No se pudo separar automáticamente nombre/tipo; se conserva la firma exacta como contrato.
- `val fileEntry: String` — No se pudo separar automáticamente nombre/tipo; se conserva la firma exacta como contrato.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 373 | `val fileEntry` | `String)` | `(sin inicializador en la declaración)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo declarado es `String)`. No declara nulabilidad explícita. |

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

No se detectaron llamadas de función relevantes fuera de la propia estructura de la declaración.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.14 `jsonToAttachmentRecords` — fun, líneas 374–382

```kotlin
    private fun jsonToAttachmentRecords(array: JSONArray): List<AttachmentRecord> = buildList {
            for (index in 0 until array.length()) {
                val item = array.getJSONObject(index)
                add(AttachmentRecord(id = item.getInt("id"), noteId = item.getInt("noteId"), type = item.optString("type", "file"), name =
                            if (item.isNull("name")) null
                            else item.optString("name").takeIf { it.isNotBlank() },
                        createdAt = item.optLong("createdAt", System.currentTimeMillis()), fileEntry = item.getString("fileEntry")))
            }
        }
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `array: JSONArray` — `array` recibe un valor de tipo `JSONArray`. El contrato no marca este parámetro como anulable.

**Retorno:** `List<AttachmentRecord>`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 376 | `val item` | `inferido` | `array.getJSONObject(index)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 375 | `for (index in 0 until array.length()) {` | Iteración: repite el cuerpo para cada elemento de la colección/rango; el coste del bloque crece con el número de elementos recorridos. |
| 378 | `if (item.isNull("name")) null` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 379 | `else item.optString("name").takeIf { it.isNotBlank() },` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |

**Restricciones concretas que aparecen en este bloque:**
- **Filtro condicional:** `takeIf` conserva el valor únicamente cuando el predicado se cumple; en caso contrario lo convierte en `null`. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `array.length`, `array.getJSONObject`, `add`, `AttachmentRecord`, `item.getInt`, `item.optString`, `item.isNull`, `it.isNotBlank`, `item.optLong`, `System.currentTimeMillis`, `item.getString`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.15 `settingsToJson` — fun, líneas 383–435

```kotlin
    private fun settingsToJson(settings: AppSettings, profileEntry: String?) = JSONObject().apply {
        put("darkMode", settings.darkMode)
        put("backgroundColor", settings.backgroundColor)
        put("backgroundToneIndex", settings.backgroundToneIndex)
        put("backgroundIntensity", settings.backgroundIntensity.toDouble())
        put("settingsPanelTone", settings.settingsPanelTone.toDouble())
        put("surfacePanelIntensity", settings.surfacePanelIntensity.toDouble())
        put("headerIntensity", settings.headerIntensity.toDouble())
        put("textColor", settings.textColor)
        put("textOutlineEnabled", settings.textOutlineEnabled)
        put("noteUiTextColor", settings.noteUiTextColor)
        put("sliderStyle", settings.sliderStyle)
        put("font", settings.font)
        put("fontSize", settings.fontSize.toDouble())
        put("soundEffectsEnabled", settings.soundEffectsEnabled)
        put("soundEffectsVolume", settings.soundEffectsVolume.toDouble())
        put("soundEffectsTheme", settings.soundEffectsTheme)
        put("hapticEffectsEnabled", settings.hapticEffectsEnabled)
        put("hapticEffectsIntensity", settings.hapticEffectsIntensity.toDouble())
        put("hapticEffectsStyle", settings.hapticEffectsStyle)
        put("language", settings.language)
        put("gridColumns", settings.gridColumns)
        put("sortOrder", settings.sortOrder)
        put("profileImageSize", settings.profileImageSize.toDouble())
        put("profileEntry", profileEntry ?: "")
        put("iconStyle", settings.iconStyle)
        put("iconSize", settings.iconSize.toDouble())
        put("accentColor", settings.accentColor)
        put("noteCardCornerRadius", settings.noteCardCornerRadius.toDouble())
        put("noteCardElevation", settings.noteCardElevation.toDouble())
        put("noteCardPadding", settings.noteCardPadding.toDouble())
        put("noteCardImageHeight", settings.noteCardImageHeight.toDouble())
        put("noteTitleMaxLines", settings.noteTitleMaxLines)
        put("noteContentMaxLines", settings.noteContentMaxLines)
        put("noteLineSpacing", settings.noteLineSpacing.toDouble())
        put("showNoteDate", settings.showNoteDate)
        put("showCategoryChip", settings.showCategoryChip)
        put("showFavoriteIcon", settings.showFavoriteIcon)
        put("fabSize", settings.fabSize.toDouble())
        put("optionMenuOrder", settings.optionMenuOrder)
        put("optionMenuHiddenItems", settings.optionMenuHiddenItems)
        put("optionMenuShowIcons", settings.optionMenuShowIcons)
        put("optionMenuTextColor", settings.optionMenuTextColor)
        put("optionMenuOpacity", settings.optionMenuOpacity.toDouble())
        put("priorityMenuHiddenItems", settings.priorityMenuHiddenItems)
        put("colorMenuHiddenItems", settings.colorMenuHiddenItems)
        put("performanceMode", settings.performanceMode)
        put("animationsEnabled", settings.animationsEnabled)
        put("animationStyle", settings.animationStyle)
        put("animationEasing", settings.animationEasing)
        put("animationSpeed", settings.animationSpeed.toDouble())
        put("animationIntensity", settings.animationIntensity.toDouble())
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `settings: AppSettings` — `settings` recibe un valor de tipo `AppSettings`. El contrato no marca este parámetro como anulable.
- `profileEntry: String?` — `profileEntry` recibe un valor de tipo `String?`. El contrato permite `null`; el bloque debe decidir explícita o implícitamente qué significa la ausencia.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

**Restricciones concretas que aparecen en este bloque:**
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `JSONObject`, `put`, `settings.backgroundIntensity.toDouble`, `settings.settingsPanelTone.toDouble`, `settings.surfacePanelIntensity.toDouble`, `settings.headerIntensity.toDouble`, `settings.fontSize.toDouble`, `settings.soundEffectsVolume.toDouble`, `settings.hapticEffectsIntensity.toDouble`, `settings.profileImageSize.toDouble`, `settings.iconSize.toDouble`, `settings.noteCardCornerRadius.toDouble`, `settings.noteCardElevation.toDouble`, `settings.noteCardPadding.toDouble`, `settings.noteCardImageHeight.toDouble`, `settings.noteLineSpacing.toDouble`, `settings.fabSize.toDouble`, `settings.optionMenuOpacity.toDouble`, `settings.animationSpeed.toDouble`, `settings.animationIntensity.toDouble`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Preservar la normalización y correspondencia de `performanceMode`, porque otras capas usan las mismas claves para rendimiento/calidad.

### 4.16 `jsonToSettings` — fun, líneas 436–486

```kotlin
    private fun jsonToSettings(json: JSONObject): AppSettings {
        val defaults = AppSettings()
        return AppSettings(darkMode = json.optBoolean("darkMode", defaults.darkMode),
            backgroundColor = json.optString("backgroundColor", defaults.backgroundColor),
            backgroundToneIndex = json.optInt("backgroundToneIndex", defaults.backgroundToneIndex),
            backgroundIntensity = json.optDouble("backgroundIntensity", defaults.backgroundIntensity.toDouble()).toFloat(),
            settingsPanelTone = json.optDouble("settingsPanelTone", defaults.settingsPanelTone.toDouble()).toFloat(),
            surfacePanelIntensity = json.optDouble("surfacePanelIntensity", defaults.surfacePanelIntensity.toDouble()).toFloat(),
            headerIntensity = json.optDouble("headerIntensity", defaults.headerIntensity.toDouble()).toFloat(),
            textColor = json.optString("textColor", defaults.textColor),
            textOutlineEnabled = json.optBoolean("textOutlineEnabled", defaults.textOutlineEnabled),
            noteUiTextColor = json.optString("noteUiTextColor", defaults.noteUiTextColor),
            sliderStyle = json.optString("sliderStyle", defaults.sliderStyle), font = json.optString("font", defaults.font),
            fontSize = json.optDouble("fontSize", defaults.fontSize.toDouble()).toFloat(),
            soundEffectsEnabled = json.optBoolean("soundEffectsEnabled", defaults.soundEffectsEnabled),
            soundEffectsVolume = json.optDouble("soundEffectsVolume", defaults.soundEffectsVolume.toDouble()).toFloat(),
            soundEffectsTheme = json.optString("soundEffectsTheme", defaults.soundEffectsTheme),
            hapticEffectsEnabled = json.optBoolean("hapticEffectsEnabled", defaults.hapticEffectsEnabled),
            hapticEffectsIntensity = json.optDouble("hapticEffectsIntensity", defaults.hapticEffectsIntensity.toDouble()).toFloat(),
            hapticEffectsStyle = json.optString("hapticEffectsStyle", defaults.hapticEffectsStyle),
            language = json.optString("language", defaults.language), gridColumns = json.optInt("gridColumns", defaults.gridColumns),
            sortOrder = json.optString("sortOrder", defaults.sortOrder), profileImageUri = "",
            profileImageSize = json.optDouble("profileImageSize", defaults.profileImageSize.toDouble()).toFloat(),
            iconStyle = json.optString("iconStyle", defaults.iconStyle),
            iconSize = json.optDouble("iconSize", defaults.iconSize.toDouble()).toFloat(),
            accentColor = json.optString("accentColor", defaults.accentColor),
            noteCardCornerRadius = json.optDouble("noteCardCornerRadius", defaults.noteCardCornerRadius.toDouble()).toFloat(),
            noteCardElevation = json.optDouble("noteCardElevation", defaults.noteCardElevation.toDouble()).toFloat(),
            noteCardPadding = json.optDouble("noteCardPadding", defaults.noteCardPadding.toDouble()).toFloat(),
            noteCardImageHeight = json.optDouble("noteCardImageHeight", defaults.noteCardImageHeight.toDouble()).toFloat(),
            noteTitleMaxLines = json.optInt("noteTitleMaxLines", defaults.noteTitleMaxLines),
            noteContentMaxLines = json.optInt("noteContentMaxLines", defaults.noteContentMaxLines),
            noteLineSpacing = json.optDouble("noteLineSpacing", defaults.noteLineSpacing.toDouble()).toFloat(),
            showNoteDate = json.optBoolean("showNoteDate", defaults.showNoteDate),
            showCategoryChip = json.optBoolean("showCategoryChip", defaults.showCategoryChip),
            showFavoriteIcon = json.optBoolean("showFavoriteIcon", defaults.showFavoriteIcon),
            fabSize = json.optDouble("fabSize", defaults.fabSize.toDouble()).toFloat(),
            optionMenuOrder = json.optString("optionMenuOrder", defaults.optionMenuOrder),
            optionMenuHiddenItems = json.optString("optionMenuHiddenItems", defaults.optionMenuHiddenItems),
            optionMenuShowIcons = json.optBoolean("optionMenuShowIcons", defaults.optionMenuShowIcons),
            optionMenuTextColor = json.optString("optionMenuTextColor", defaults.optionMenuTextColor),
            optionMenuOpacity = json.optDouble("optionMenuOpacity", defaults.optionMenuOpacity.toDouble()).toFloat(),
            priorityMenuHiddenItems = json.optString("priorityMenuHiddenItems", defaults.priorityMenuHiddenItems),
            colorMenuHiddenItems = json.optString("colorMenuHiddenItems", defaults.colorMenuHiddenItems),
            performanceMode = json.optString("performanceMode", defaults.performanceMode),
            animationsEnabled = json.optBoolean("animationsEnabled", defaults.animationsEnabled),
            animationStyle = json.optString("animationStyle", defaults.animationStyle),
            animationEasing = json.optString("animationEasing", defaults.animationEasing),
            animationSpeed = json.optDouble("animationSpeed", defaults.animationSpeed.toDouble()).toFloat(),
            animationIntensity = json.optDouble("animationIntensity", defaults.animationIntensity.toDouble()).toFloat())
    }
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `json: JSONObject` — `json` recibe un valor de tipo `JSONObject`. El contrato no marca este parámetro como anulable.

**Retorno:** `AppSettings`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 437 | `val defaults` | `inferido` | `AppSettings()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 438 | `return AppSettings(darkMode = json.optBoolean("darkMode", defaults.darkMode),` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `AppSettings`, `json.optBoolean`, `json.optString`, `json.optInt`, `json.optDouble`, `defaults.backgroundIntensity.toDouble`, `toFloat`, `defaults.settingsPanelTone.toDouble`, `defaults.surfacePanelIntensity.toDouble`, `defaults.headerIntensity.toDouble`, `defaults.fontSize.toDouble`, `defaults.soundEffectsVolume.toDouble`, `defaults.hapticEffectsIntensity.toDouble`, `defaults.profileImageSize.toDouble`, `defaults.iconSize.toDouble`, `defaults.noteCardCornerRadius.toDouble`, `defaults.noteCardElevation.toDouble`, `defaults.noteCardPadding.toDouble`, `defaults.noteCardImageHeight.toDouble`, `defaults.noteLineSpacing.toDouble`, `defaults.fabSize.toDouble`, `defaults.optionMenuOpacity.toDouble`, `defaults.animationSpeed.toDouble`, `defaults.animationIntensity.toDouble`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Preservar la normalización y correspondencia de `performanceMode`, porque otras capas usan las mismas claves para rendimiento/calidad.

## 5. Inventario global de propiedades/variables detectadas

| Línea | Identificador | Mutabilidad | Tipo | Explicación |
|---:|---|---|---|---|
| 34 | `FORMAT_NAME` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Está marcada `const`, por lo que es una constante de tiempo de compilación y solo puede usar un tipo/valor admitido por Kotlin para constantes. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 35 | `FORMAT_VERSION` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Está marcada `const`, por lo que es una constante de tiempo de compilación y solo puede usar un tipo/valor admitido por Kotlin para constantes. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 36 | `DATA_ENTRY` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Está marcada `const`, por lo que es una constante de tiempo de compilación y solo puede usar un tipo/valor admitido por Kotlin para constantes. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 37 | `ATTACHMENTS_PREFIX` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Está marcada `const`, por lo que es una constante de tiempo de compilación y solo puede usar un tipo/valor admitido por Kotlin para constantes. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 38 | `PROFILE_PREFIX` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Está marcada `const`, por lo que es una constante de tiempo de compilación y solo puede usar un tipo/valor admitido por Kotlin para constantes. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 45 | `MAX_BACKUP_ENTRIES` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Está marcada `const`, por lo que es una constante de tiempo de compilación y solo puede usar un tipo/valor admitido por Kotlin para constantes. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 46 | `MAX_METADATA_BYTES` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Está marcada `const`, por lo que es una constante de tiempo de compilación y solo puede usar un tipo/valor admitido por Kotlin para constantes. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 51 | `appContext` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El `Context` da acceso a recursos/servicios/almacenamiento Android; debe respetarse el ciclo de vida del contexto recibido. |
| 52 | `database` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 53 | `notes` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 54 | `attachments` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 55 | `output` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 58 | `attachmentJson` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 59 | `exportedAttachments` | `var` | `inferido` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 60 | `skippedAttachments` | `var` | `inferido` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 62 | `entryName` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 63 | `copied` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 72 | `profileEntry` | `var` | `String?` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo declarado es `String?`. Admite `null`, así que los consumidores deben contemplar ausencia de valor. |
| 74 | `profileUri` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Representa una ubicación Android `Uri`; no debe asumirse que siempre corresponde a una ruta de archivo convencional. |
| 75 | `profileName` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 77 | `candidate` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 82 | `data` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 103 | `appContext` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El `Context` da acceso a recursos/servicios/almacenamiento Android; debe respetarse el ciclo de vida del contexto recibido. |
| 104 | `stageDir` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 108 | `importedFiles` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 111 | `dataFile` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 119 | `data` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 126 | `notes` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 127 | `attachmentRecords` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 128 | `settingsJson` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 129 | `restoredSettings` | `var` | `inferido` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 130 | `noteIds` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 147 | `attachmentsDirectory` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 151 | `restoreToken` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 152 | `restoredAttachments` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 157 | `targetName` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 158 | `target` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 164 | `profileEntry` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 166 | `profileSource` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 168 | `profileDir` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 172 | `profileTarget` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 182 | `database` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 200 | `keepPaths` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 215 | `activeProfilePath` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 240 | `input` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 257 | `path` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 266 | `input` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 271 | `initialFreeSpace` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El inicializador limita explícitamente el valor a un rango, evitando que dimensiones/intensidades/sizes inválidos se propaguen. |
| 272 | `extractionLimit` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 278 | `reserve` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 291 | `extractedBytes` | `var` | `inferido` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 292 | `entryCount` | `var` | `inferido` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 293 | `buffer` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 299 | `entry` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 306 | `target` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 313 | `count` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 335 | `target` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 336 | `rootPath` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 337 | `targetPath` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 365 | `item` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 373 | `fileEntry` | `val` | `String)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo declarado es `String)`. No declara nulabilidad explícita. |
| 376 | `item` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 437 | `defaults` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

## 6. Mapa exhaustivo de TODOS los bloques delimitados por `{}`

Esta tabla recorre todas las llaves estructurales detectadas fuera de strings/comentarios. Así se documentan también lambdas de Compose, callbacks, iteraciones y ámbitos locales que no tienen un nombre propio de función.

| Inicio–fin | Prof. | Cabecera/contexto | Qué significa ese bloque |
|---|---:|---|---|
| 33–487 | 0 | `object AppDataBackupManager` | Cuerpo de tipo/contenedor: agrupa propiedades y miembros bajo la misma responsabilidad y controla su visibilidad/estado compartido. |
| 49–100 | 1 | `withContext(Dispatchers.IO)` | Cambio de contexto de corrutina: el cuerpo se ejecuta bajo el dispatcher/contexto indicado y devuelve el resultado al contexto llamador. |
| 50–99 | 2 | `runCatching` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 57–98 | 3 | `return@runCatching output.use` | Bloque `use`: el recurso cerrable se libera automáticamente al terminar el cuerpo, incluso si ocurre una excepción. |
| 57–97 | 4 | `return@runCatching output.use { rawOutput -> ZipOutputStream(rawOutput.buffered()).use` | Bloque `use`: el recurso cerrable se libera automáticamente al terminar el cuerpo, incluso si ocurre una excepción. |
| 61–71 | 5 | `attachments.forEach` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 65–68 | 6 | `if (copied)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 68–70 | 6 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 73–81 | 5 | `if (settings.profileImageUri.isNotBlank())` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 75–75 | 6 | `val profileName = safeFileName(profileUri.lastPathSegment?.substringAfterLast('/')?.takeIf` | Ámbito delimitado por llaves en profundidad 6. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 78–80 | 6 | `if (writeUriEntry(context = appContext, zip = zip, source = profileUri, entryName = candidate))` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 82–91 | 5 | `val data = JSONObject().apply` | Lambda `apply`: configura el receptor y devuelve ese mismo receptor; útil para inicialización encadenada sin perder la instancia. |
| 86–88 | 6 | `put("notes", JSONArray().apply` | Lambda `apply`: configura el receptor y devuelve ese mismo receptor; útil para inicialización encadenada sin perder la instancia. |
| 87–87 | 7 | `notes.forEach` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 101–238 | 1 | `suspend fun importBackup(context: Context, source: Uri): Result<BackupSummary> = withContext(Dispatchers.IO)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 102–237 | 2 | `runCatching` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 105–107 | 3 | `if (!stageDir.mkdirs() && !stageDir.isDirectory)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 109–234 | 3 | `try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 112–114 | 4 | `if (!dataFile.isFile)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 116–118 | 4 | `MAX_METADATA_BYTES)` | Ámbito delimitado por llaves en profundidad 4. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 120–122 | 4 | `if (data.optString("format") != FORMAT_NAME)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 123–125 | 4 | `if (data.optInt("version", -1) != FORMAT_VERSION)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 130–132 | 4 | `val noteIds = notes.map` | Transformación de colección: produce un nuevo elemento por cada entrada y normalmente genera una colección de salida. |
| 133–135 | 4 | `if (noteIds.size != notes.size)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 136–138 | 4 | `if (attachmentRecords.map` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 138–140 | 4 | `}.toSet().size != attachmentRecords.size)` | Ámbito delimitado por llaves en profundidad 4. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 141–144 | 4 | `if (attachmentRecords.any` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 144–146 | 4 | `})` | Ámbito delimitado por llaves en profundidad 4. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 148–150 | 4 | `if (!attachmentsDirectory.exists() && !attachmentsDirectory.mkdirs())` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 152–163 | 4 | `val restoredAttachments = attachmentRecords.map` | Transformación de colección: produce un nuevo elemento por cada entrada y normalmente genera una colección de salida. |
| 154–156 | 5 | `if (!sourceFile.isFile)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 165–179 | 4 | `if (profileEntry.isNotBlank())` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 167–176 | 5 | `if (profileSource.isFile)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 169–171 | 6 | `if (!profileDir.exists() && !profileDir.mkdirs())` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 176–178 | 5 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 179–181 | 4 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 183–194 | 4 | `try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 184–193 | 5 | `database.withTransaction` | Ámbito delimitado por llaves en profundidad 5. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 187–189 | 6 | `if (notes.isNotEmpty())` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 190–192 | 6 | `if (restoredAttachments.isNotEmpty())` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 194–197 | 4 | `} catch (e: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 195–195 | 5 | `importedFiles.forEach` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 200–202 | 4 | `val keepPaths = importedFiles.filter` | Filtro de colección: conserva solo los elementos cuyo predicado devuelve verdadero. |
| 202–204 | 4 | `}.map` | Transformación de colección: produce un nuevo elemento por cada entrada y normalmente genera una colección de salida. |
| 205–208 | 4 | `attachmentsDirectory.listFiles()?.forEach` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 205–207 | 5 | `attachmentsDirectory.listFiles()?.forEach { file -> if (file.absolutePath !in keepPaths)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 215–217 | 4 | `val activeProfilePath = Uri.parse(restoredSettings.profileImageUri).takeIf` | Ámbito delimitado por llaves en profundidad 4. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 218–223 | 4 | `File(appContext.filesDir, "profile_backup").listFiles()?.forEach` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 220–222 | 5 | `if (file.absolutePath != activeProfilePath)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 228–230 | 4 | `listOf("video_previews", "audio_previews", "pdf_previews", "docx_previews").forEach` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 234–236 | 3 | `} finally` | Bloque de limpieza garantizada: se ejecuta después del intento tanto si hubo éxito como si hubo excepción. |
| 239–254 | 1 | `private fun writeUriEntry(context: Context, zip: ZipOutputStream, source: Uri, entryName: String): Boolean` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 241–247 | 2 | `return try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 242–245 | 3 | `input.use` | Bloque `use`: el recurso cerrable se libera automáticamente al terminar el cuerpo, incluso si ocurre una excepción. |
| 247–253 | 2 | `} catch (_: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 248–250 | 3 | `try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 250–251 | 3 | `} catch (_: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 255–262 | 1 | `private fun openInputStream(context: Context, uri: Uri): InputStream? = try` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 256–259 | 2 | `if (uri.scheme == "file")` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 259–261 | 2 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 262–264 | 1 | `} catch (_: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 265–330 | 1 | `private fun extractBackup(context: Context, source: Uri, stageDir: File)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 272–280 | 2 | `val extractionLimit = if (initialFreeSpace > 0L)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 280–287 | 2 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 288–290 | 2 | `if (initialFreeSpace > 0L && extractionLimit < 8L * 1024L * 1024L)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 294–329 | 2 | `input.use` | Bloque `use`: el recurso cerrable se libera automáticamente al terminar el cuerpo, incluso si ocurre una excepción. |
| 296–328 | 3 | `ZipInputStream(rawInput.buffered()).use` | Bloque `use`: el recurso cerrable se libera automáticamente al terminar el cuerpo, incluso si ocurre una excepción. |
| 298–327 | 4 | `while (true)` | Bucle `while`: se repite mientras la condición siga siendo verdadera; el cuerpo debe producir progreso para evitar un ciclo infinito. |
| 302–304 | 5 | `MAX_BACKUP_ENTRIES)` | Ámbito delimitado por llaves en profundidad 5. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 305–325 | 5 | `if (!entry.isDirectory)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 307–309 | 6 | `if (target.parentFile?.exists() == false)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 310–324 | 6 | `target.outputStream().buffered(64 * 1024).use` | Bloque `use`: el recurso cerrable se libera automáticamente al terminar el cuerpo, incluso si ocurre una excepción. |
| 312–323 | 7 | `while (true)` | Bucle `while`: se repite mientras la condición siga siendo verdadera; el cuerpo debe producir progreso para evitar un ciclo infinito. |
| 314–316 | 8 | `if (count <= 0)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 319–321 | 8 | `extractionLimit)` | Ámbito delimitado por llaves en profundidad 8. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 331–342 | 1 | `private fun safeStageFile(stageDir: File, entryName: String): File` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 332–334 | 2 | `if (entryName.isBlank() \|\| entryName.startsWith('/') \|\| entryName.startsWith('\\') \|\| entryName.contains(".."))` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 338–340 | 2 | `if (!targetPath.startsWith(rootPath))` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 343–343 | 1 | `private fun safeFileName(value: String): String = value.replace(Regex("[^A-Za-z0-9._-]"), "_").trim('_').take(100).ifBlank` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 344–354 | 1 | `private fun noteToJson(note: Note) = JSONObject().apply` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 355–362 | 1 | `private fun attachmentToJson(attachment: Attachment, fileEntry: String) = JSONObject().apply` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 363–371 | 1 | `private fun jsonToNotes(array: JSONArray): List<Note> = buildList` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 364–370 | 2 | `for (index in 0 until array.length())` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 374–382 | 1 | `private fun jsonToAttachmentRecords(array: JSONArray): List<AttachmentRecord> = buildList` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 375–381 | 2 | `for (index in 0 until array.length())` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 379–379 | 3 | `else item.optString("name").takeIf` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 383–435 | 1 | `private fun settingsToJson(settings: AppSettings, profileEntry: String?) = JSONObject().apply` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 436–486 | 1 | `private fun jsonToSettings(json: JSONObject): AppSettings` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |

## 7. Lectura de seguridad antes de tocar este archivo

La documentación describe **lo que el código actual ya hace**. No constituye una propuesta de refactorización. Si se quisiera cambiar algo en el futuro, primero habría que preservar contratos públicos, claves de configuración, restricciones de API Android, comportamiento de Compose, lifecycle, operaciones de I/O y compatibilidad con los datos ya persistidos. En esta entrega no se hizo ninguna de esas modificaciones.

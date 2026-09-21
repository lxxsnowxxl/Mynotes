# AppDataBackupManager.kt — documentación del código actual
**Ruta real:** `app/src/main/java/com/example/mynotes/data/AppDataBackupManager.kt`  **SHA-256:** `5d9cc192f4cf9abe63b9fb530651c18a6de7c1917c803cdbfb9a6cf5307b2daa`  **Líneas:** 499 · **Bytes:** 30017 · **Imports:** 17 · **Declaraciones detectadas:** 14
> Documento generado fuera de `app/` a partir de lectura del código. El fuente es la única fuente de verdad; no se modificó para generar esta documentación.
## 1. Responsabilidad

Exportación e importación de la configuración de MyNotes y datos de respaldo.
## 2. Package e imports

Package declarado: `com.example.mynotes.data`.

### Android / Jetpack / Compose

`android.content.Context`, `android.net.Uri`, `androidx.room.withTransaction`

### Proyecto MyNotes

`com.example.mynotes.settings.AppSettings`, `com.example.mynotes.settings.SettingsRepository`, `com.example.mynotes.widget.MyNotesWidgetUpdater`

### Kotlin / Coroutines / Java

`kotlinx.coroutines.Dispatchers`, `kotlinx.coroutines.withContext`, `java.io.File`, `java.io.FileInputStream`, `java.io.InputStream`, `java.util.UUID`, `java.util.zip.ZipEntry`, `java.util.zip.ZipInputStream`, `java.util.zip.ZipOutputStream`

### Terceros / otros

`org.json.JSONArray`, `org.json.JSONObject`

## 3. Declaraciones detectadas

| Línea | Tipo | Nombre | Firma/inicio |
|---:|---|---|---|
| 34 | `object` | `AppDataBackupManager` | `object AppDataBackupManager {` |
| 48 | `class` | `BackupSummary` | `data class BackupSummary(val noteCount: Int, val attachmentCount: Int, val skippedAttachmentCount: Int = 0)` |
| 241 | `fun` | `writeUriEntry` | `private fun writeUriEntry(context: Context, zip: ZipOutputStream, source: Uri, entryName: String): Boolean {` |
| 257 | `fun` | `openInputStream` | `private fun openInputStream(context: Context, uri: Uri): InputStream? = try {` |
| 267 | `fun` | `extractBackup` | `private fun extractBackup(context: Context, source: Uri, stageDir: File) {` |
| 333 | `fun` | `safeStageFile` | `private fun safeStageFile(stageDir: File, entryName: String): File {` |
| 345 | `fun` | `safeFileName` | `private fun safeFileName(value: String): String = value.replace(Regex("[^A-Za-z0-9._-]"), "_").trim('_').take(100).ifBlank { "file" }` |
| 346 | `fun` | `noteToJson` | `private fun noteToJson(note: Note) = JSONObject().apply {` |
| 357 | `fun` | `attachmentToJson` | `private fun attachmentToJson(attachment: Attachment, fileEntry: String) = JSONObject().apply {` |
| 365 | `fun` | `jsonToNotes` | `private fun jsonToNotes(array: JSONArray): List<Note> = buildList {` |
| 374 | `class` | `AttachmentRecord` | `private data class AttachmentRecord(val id: Int, val noteId: Int, val type: String, val name: String?, val createdAt: Long,` |
| 376 | `fun` | `jsonToAttachmentRecords` | `private fun jsonToAttachmentRecords(array: JSONArray): List<AttachmentRecord> = buildList {` |
| 385 | `fun` | `settingsToJson` | `private fun settingsToJson(settings: AppSettings, profileEntry: String?) = JSONObject().apply {` |
| 441 | `fun` | `jsonToSettings` | `private fun jsonToSettings(json: JSONObject): AppSettings {` |

## 4. Estado, efectos y límites observables

- **Coroutines:** 3 aparición/apariciones.
- **Room:** 1 aparición/apariciones.
- **I/O/red:** 21 aparición/apariciones.
- **try/catch:** 9 aparición/apariciones.
- **coerce*:** 4 aparición/apariciones.
- **safe calls:** 7 aparición/apariciones.

Estas cifras son indicadores de superficie de cambio, no diagnósticos de error. Cualquier modificación debe preservar contratos de persistencia, lifecycle, límites numéricos y nulabilidad visibles en el fuente.

## 5. Dependencias internas directas

- `com.example.mynotes.settings.AppSettings`
- `com.example.mynotes.settings.SettingsRepository`
- `com.example.mynotes.widget.MyNotesWidgetUpdater`

## 6. Recursos Android referenciados

No se detectaron referencias `R.*` directas.

## 7. Puntos de revisión al modificarlo

- Conservar rangos `coerce*`, claves DataStore y compatibilidad con backups existentes.

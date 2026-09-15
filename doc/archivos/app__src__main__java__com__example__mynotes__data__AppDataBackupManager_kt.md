# AppDataBackupManager.kt

**Ruta:** `app/src/main/java/com/example/mynotes/data/AppDataBackupManager.kt`  
**Paquete:** `com.example.mynotes.data`  
**Líneas:** 867 → 487 (43.8% menos)

## Responsabilidad

Gestiona la exportación e importación de los datos de MyNotes, incluyendo la información persistida de notas, adjuntos y ajustes que forman parte de una copia de seguridad.

## Papel dentro de la arquitectura

Se ubica en la capa de datos porque conoce cómo serializar/restaurar el estado persistente y cómo coordinar archivos asociados sin convertir esa lógica en responsabilidad de la UI.

## Dependencias y relaciones

**Dependencias internas de MyNotes:** `com.example.mynotes.settings.AppSettings`, `com.example.mynotes.settings.SettingsRepository`.

**Android/Jetpack:** `android.content.Context`, `android.net.Uri`, `androidx.room.withTransaction`.

**Bibliotecas externas:** `org.json.JSONArray`, `org.json.JSONObject`.

**Kotlin/Java/corrutinas:** `kotlinx.coroutines.Dispatchers`, `kotlinx.coroutines.withContext`, `java.io.File`, `java.io.FileInputStream`, `java.io.InputStream`, `java.util.UUID`, `java.util.zip.ZipEntry`, `java.util.zip.ZipInputStream`, `java.util.zip.ZipOutputStream`.

## Declaraciones importantes detectadas

| Línea | Tipo | Nombre | Firma / declaración | Qué hace |
|---:|---|---|---|---|
| 33 | object | `AppDataBackupManager` | `object AppDataBackupManager {` | Singleton que centraliza funciones/estado compartido sin crear múltiples instancias. |
| 47 | data class | `BackupSummary` | `data class BackupSummary(val noteCount: Int, val attachmentCount: Int, val skippedAttachmentCount: Int = 0)` | Modelo de datos que agrupa valores relacionados con esta responsabilidad. |
| 48 | fun | `exportBackup` | `suspend fun exportBackup(context: Context, destination: Uri, settings: AppSettings): Result<BackupSummary> =` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 101 | fun | `importBackup` | `suspend fun importBackup(context: Context, source: Uri): Result<BackupSummary> = withContext(Dispatchers.IO) {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 239 | fun | `writeUriEntry` | `private fun writeUriEntry(context: Context, zip: ZipOutputStream, source: Uri, entryName: String): Boolean {` | Escribe/persiste datos ya preparados en su destino correspondiente. |
| 255 | fun | `openInputStream` | `private fun openInputStream(context: Context, uri: Uri): InputStream? = try {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 265 | fun | `extractBackup` | `private fun extractBackup(context: Context, source: Uri, stageDir: File) {` | Extrae una porción de información desde contenido más amplio, como HTML, JSON, URI o metadata. |
| 331 | fun | `safeStageFile` | `private fun safeStageFile(stageDir: File, entryName: String): File {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 343 | fun | `safeFileName` | `private fun safeFileName(value: String): String = value.replace(Regex("[^A-Za-z0-9._-]"), "_").trim('_').take(100).ifBlank { "file" }` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 344 | fun | `noteToJson` | `private fun noteToJson(note: Note) = JSONObject().apply {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 355 | fun | `attachmentToJson` | `private fun attachmentToJson(attachment: Attachment, fileEntry: String) = JSONObject().apply {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 363 | fun | `jsonToNotes` | `private fun jsonToNotes(array: JSONArray): List<Note> = buildList {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 372 | data class | `AttachmentRecord` | `private data class AttachmentRecord(val id: Int, val noteId: Int, val type: String, val name: String?, val createdAt: Long,` | Modelo de datos que agrupa valores relacionados con esta responsabilidad. |
| 374 | fun | `jsonToAttachmentRecords` | `private fun jsonToAttachmentRecords(array: JSONArray): List<AttachmentRecord> = buildList {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 383 | fun | `settingsToJson` | `private fun settingsToJson(settings: AppSettings, profileEntry: String?) = JSONObject().apply {` | Actualiza o persiste el valor indicado y propaga el cambio a la capa responsable. |
| 436 | fun | `jsonToSettings` | `private fun jsonToSettings(json: JSONObject): AppSettings {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |

## Cómo leer este archivo

1. Empieza por las declaraciones públicas o composables: representan el contrato que usa el resto del proyecto. 2. Después revisa las funciones privadas: normalmente encapsulan normalización, cálculo, rendering o acceso a plataforma. 3. Los comentarios existentes se conservaron durante la compactación y explican decisiones específicas allí donde el código necesita contexto. 4. Consulta las dependencias internas anteriores para seguir el flujo hacia la capa que consume o persiste el resultado.

## Garantía de la compactación

En este archivo no se cambiaron identificadores, literales, operadores, comentarios, llamadas ni orden de tokens. La reducción proviene exclusivamente de compactar espacios/saltos de línea seguros. El proyecto fue comparado mediante el lexer de Kotlin y validado sintácticamente con PSI después del cambio.

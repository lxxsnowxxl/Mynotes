# AttachmentDao.kt

**Ruta:** `app/src/main/java/com/example/mynotes/data/AttachmentDao.kt`  
**Paquete:** `com.example.mynotes.data`  
**Líneas:** 98 → 69 (29.6% menos)

## Responsabilidad

Interfaz DAO de Room para consultar, insertar y eliminar adjuntos y para relacionarlos con sus notas.

## Papel dentro de la arquitectura

Concentra SQL y operaciones de persistencia de adjuntos para que el resto del proyecto no tenga que construir consultas manualmente.

## Dependencias y relaciones

**Dependencias internas de MyNotes:** no importa directamente otras clases del paquete de la app.

**Android/Jetpack:** `androidx.room.Dao`, `androidx.room.Delete`, `androidx.room.Insert`, `androidx.room.OnConflictStrategy`, `androidx.room.Query`.

**Kotlin/Java/corrutinas:** `kotlinx.coroutines.flow.Flow`.

## Declaraciones importantes detectadas

| Línea | Tipo | Nombre | Firma / declaración | Qué hace |
|---:|---|---|---|---|
| 11 | interface | `AttachmentDao` | `interface AttachmentDao {` | Contrato que define operaciones implementadas/procesadas por otra capa o framework. |
| 23 | fun | `getAllAttachments` | `fun getAllAttachments():` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 32 | fun | `getAllAttachmentsOnce` | `suspend fun getAllAttachmentsOnce():` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 35 | fun | `insertAttachmentsForRestore` | `suspend fun insertAttachmentsForRestore(attachments: List<Attachment>)` | Inserta una entidad o valor en su almacenamiento/colección. |
| 37 | fun | `deleteAllAttachments` | `suspend fun deleteAllAttachments()` | Elimina el dato/recurso indicado y ejecuta la limpieza asociada. |
| 46 | fun | `getAttachments` | `fun getAttachments(noteId: Int): Flow<List<Attachment>>` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 55 | fun | `getAttachmentsOnce` | `suspend fun getAttachmentsOnce(noteId: Int): List<Attachment>` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 57 | fun | `insertAttachment` | `suspend fun insertAttachment(attachment: Attachment)` | Inserta una entidad o valor en su almacenamiento/colección. |
| 59 | fun | `insertAttachments` | `suspend fun insertAttachments(attachments: List<Attachment>)` | Inserta una entidad o valor en su almacenamiento/colección. |
| 61 | fun | `deleteAttachment` | `suspend fun deleteAttachment(attachment: Attachment)` | Elimina el dato/recurso indicado y ejecuta la limpieza asociada. |
| 68 | fun | `deleteAttachmentsForNote` | `suspend fun deleteAttachmentsForNote(noteId: Int)` | Elimina el dato/recurso indicado y ejecuta la limpieza asociada. |

## Cómo leer este archivo

1. Empieza por las declaraciones públicas o composables: representan el contrato que usa el resto del proyecto. 2. Después revisa las funciones privadas: normalmente encapsulan normalización, cálculo, rendering o acceso a plataforma. 3. Los comentarios existentes se conservaron durante la compactación y explican decisiones específicas allí donde el código necesita contexto. 4. Consulta las dependencias internas anteriores para seguir el flujo hacia la capa que consume o persiste el resultado.

## Garantía de la compactación

En este archivo no se cambiaron identificadores, literales, operadores, comentarios, llamadas ni orden de tokens. La reducción proviene exclusivamente de compactar espacios/saltos de línea seguros. El proyecto fue comparado mediante el lexer de Kotlin y validado sintácticamente con PSI después del cambio.

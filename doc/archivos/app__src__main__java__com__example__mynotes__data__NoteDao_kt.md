# NoteDao.kt

**Ruta:** `app/src/main/java/com/example/mynotes/data/NoteDao.kt`  
**Paquete:** `com.example.mynotes.data`  
**Líneas:** 92 → 67 (27.2% menos)

## Responsabilidad

Interfaz DAO de Room para las operaciones de notas y sus consultas observables.

## Papel dentro de la arquitectura

Es la frontera entre la lógica de aplicación y SQLite: centraliza inserciones, actualizaciones, borrados y consultas que alimentan los flujos de la pantalla principal.

## Dependencias y relaciones

**Dependencias internas de MyNotes:** no importa directamente otras clases del paquete de la app.

**Android/Jetpack:** `androidx.room.Dao`, `androidx.room.Delete`, `androidx.room.Insert`, `androidx.room.OnConflictStrategy`, `androidx.room.Query`, `androidx.room.Update`.

**Kotlin/Java/corrutinas:** `kotlinx.coroutines.flow.Flow`.

## Declaraciones importantes detectadas

| Línea | Tipo | Nombre | Firma / declaración | Qué hace |
|---:|---|---|---|---|
| 12 | interface | `NoteDao` | `interface NoteDao {` | Contrato que define operaciones implementadas/procesadas por otra capa o framework. |
| 32 | fun | `getAllNotes` | `fun getAllNotes():` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 41 | fun | `getAllNotesOnce` | `suspend fun getAllNotesOnce():` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 44 | fun | `insertNotes` | `suspend fun insertNotes(notes: List<Note>)` | Inserta una entidad o valor en su almacenamiento/colección. |
| 46 | fun | `deleteAllNotes` | `suspend fun deleteAllNotes()` | Elimina el dato/recurso indicado y ejecuta la limpieza asociada. |
| 48 | fun | `insertNote` | `suspend fun insertNote(note: Note): Long` | Inserta una entidad o valor en su almacenamiento/colección. |
| 50 | fun | `updateNote` | `suspend fun updateNote(note: Note)` | Modifica un elemento existente conservando su identidad lógica. |
| 56 | fun | `updateColor` | `suspend fun updateColor(noteId: Int, color: String)` | Modifica un elemento existente conservando su identidad lógica. |
| 58 | fun | `updatePriority` | `suspend fun updatePriority(noteId: Int, priority: Int)` | Modifica un elemento existente conservando su identidad lógica. |
| 60 | fun | `updateFavorite` | `suspend fun updateFavorite(noteId: Int, isFavorite: Boolean)` | Modifica un elemento existente conservando su identidad lógica. |
| 62 | fun | `updatePinned` | `suspend fun updatePinned(noteId: Int, isPinned: Boolean)` | Modifica un elemento existente conservando su identidad lógica. |
| 64 | fun | `updateCategory` | `suspend fun updateCategory(noteId: Int, category: String)` | Modifica un elemento existente conservando su identidad lógica. |
| 66 | fun | `deleteNote` | `suspend fun deleteNote(note: Note)` | Elimina el dato/recurso indicado y ejecuta la limpieza asociada. |

## Cómo leer este archivo

1. Empieza por las declaraciones públicas o composables: representan el contrato que usa el resto del proyecto. 2. Después revisa las funciones privadas: normalmente encapsulan normalización, cálculo, rendering o acceso a plataforma. 3. Los comentarios existentes se conservaron durante la compactación y explican decisiones específicas allí donde el código necesita contexto. 4. Consulta las dependencias internas anteriores para seguir el flujo hacia la capa que consume o persiste el resultado.

## Garantía de la compactación

En este archivo no se cambiaron identificadores, literales, operadores, comentarios, llamadas ni orden de tokens. La reducción proviene exclusivamente de compactar espacios/saltos de línea seguros. El proyecto fue comparado mediante el lexer de Kotlin y validado sintácticamente con PSI después del cambio.

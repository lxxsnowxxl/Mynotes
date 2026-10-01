# MyNotes – Performance changes v20

Esta revisión está limitada a rendimiento general. No modifica el código de visualización de adjuntos ni el sistema de miniaturas/previews.

## Cambios

- El índice de búsqueda de NotesScreen ya no se construye cuando el buscador está vacío. Se crea al iniciar una búsqueda y se reutiliza mientras se escribe.
- Se eliminó una lectura duplicada de LocalConfiguration en NotesScreen.
- PaletteCatalog ahora usa un índice por clave para resolver paletas en O(1), evitando recorridos repetidos de la lista completa.
- MyNotesWidgetUpdater agrupa solicitudes de actualización cercanas (120 ms) para evitar ráfagas de broadcasts, consultas Room y refrescos redundantes del launcher.
- Los widgets Recent notes y Favorites obtienen sus contadores mediante COUNT(*) en SQLite, sin cargar todas las notas en memoria.
- Overview obtiene todas sus estadísticas con una única consulta agregada de SQLite.
- DisplayPerformanceController evita volver a recorrer modos de pantalla y reescribir LayoutParams cuando la misma Window ya tiene el mismo perfil solicitado. reapplyLastRequest() conserva la reaplicación al volver a primer plano.

## Rutas preservadas sin cambios

Se verificaron por SHA-256 antes y después de esta revisión:

- performance/AttachmentPreviewCache.kt
- ui/components/AttachmentPreviewTile.kt
- ui/components/InlineNoteAttachment.kt
- ui/components/LinkPreviewCard.kt
- ui/components/NoteCard.kt
- links/LinkPreviewRepository.kt
- ui/AttachmentViewerActivity.kt
- data/Attachment.kt
- data/AttachmentDao.kt
- data/PendingAttachment.kt
- widget/WidgetMediaPreview.kt

La compilación Gradle no se pudo ejecutar en este entorno porque el wrapper solicita Gradle 9.3.0 desde services.gradle.org y aquí no hay acceso de red.
